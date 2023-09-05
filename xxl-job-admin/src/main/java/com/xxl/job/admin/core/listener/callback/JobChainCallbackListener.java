package com.xxl.job.admin.core.listener.callback;

import com.xxl.job.admin.core.complete.XxlJobCompleter;
import com.xxl.job.admin.core.event.JobCallbackEvent;
import com.xxl.job.admin.core.listener.trigger.JobChainTriggerListener;
import com.xxl.job.admin.core.model.XxlJobChain;
import com.xxl.job.admin.core.model.XxlJobChainLog;
import com.xxl.job.admin.core.model.XxlJobLog;
import com.xxl.job.admin.core.trigger.XkXxlJobTrigger;
import com.xxl.job.admin.core.util.JacksonUtil;
import com.xxl.job.admin.dao.XxlJobChainDao;
import com.xxl.job.admin.dao.XxlJobChainLogDao;
import com.xxl.job.admin.dao.XxlJobLogDao;
import com.xxl.job.core.biz.model.ReturnT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author 空青
 * @date 2022-01-16
 */
@Component
@Order(2)
public class JobChainCallbackListener implements ApplicationListener<JobCallbackEvent> {

    private static Logger logger = LoggerFactory.getLogger(JobChainTriggerListener.class);

    @Resource
    XxlJobChainLogDao xxlJobChainLogDao;

    @Resource
    XxlJobChainDao xxlJobChainDao;

    @Resource
    XxlJobLogDao xxlJobLogDao;


    @Override
    public void onApplicationEvent(JobCallbackEvent jobCallbackEvent) {
        try {
            trigger(jobCallbackEvent);
        } catch (Exception e) {
            logger.error("链路调用发生异常->[{}]", JacksonUtil.writeValueAsString(jobCallbackEvent), e);
        }
    }

    public void trigger(JobCallbackEvent jobCallbackEvent) {
        //当前任务
        XxlJobLog xxlJobLog = jobCallbackEvent.getXxlJobLog();
        //链路记录与日志的映射
        XxlJobChainLog xxlJobChainLog = xxlJobChainLogDao.selectByExecutorLogId(xxlJobLog.getId());
        if (null == xxlJobChainLog) {
            return;
        }
        //链路记录
        //修改链路日志状态
        XxlJobChain xxlJobChain = xxlJobChainDao.selectById(xxlJobChainLog.getChainId());
        xxlJobChainLog.setHandleCode(xxlJobLog.getHandleCode());
        xxlJobChainLogDao.update(xxlJobChainLog);

        logger.info("[JOB-CHAIN-CALLBACK]->[masterJobId:{},masterLogId:{},chainId:{},jobId:{},logId:{}]",
                xxlJobChain.getJobId(), xxlJobChain.getLogId(), xxlJobChain.getId(), xxlJobLog.getJobId(), xxlJobLog.getId());

        int count = xxlJobChainLogDao.countByLogId(xxlJobChainLog.getLogId(), ReturnT.RUNNING_CODE);
        if (count > 0) {
            logger.info("[JOB-CHAIN-CALLBACK]->还有任务在执行->[masterJobId:{},masterLogId:{},chainId:{},jobId:{},logId:{}]",
                    xxlJobChain.getJobId(), xxlJobChain.getLogId(), xxlJobChain.getId(), xxlJobLog.getJobId(), xxlJobLog.getId());
            return;
        }

        List<XxlJobChainLog> xxlJobChainLogs = xxlJobChainLogDao.selectByExecutorChainId(xxlJobChain.getLogId(), xxlJobChain.getId());
        long errorCount = xxlJobChainLogs.stream().filter(xxlJobChainLog1 -> xxlJobChainLog1.getHandleCode() == ReturnT.FAIL_CODE).count();
        //修改链路状态
        xxlJobChain.setHandleCode(errorCount > 0 ? ReturnT.FAIL_CODE : ReturnT.SUCCESS_CODE);
        int lockRet = xxlJobChainDao.updateStatus(xxlJobChain.getId(), ReturnT.RUNNING_CODE, xxlJobChain.getHandleCode());
        if (lockRet < 1) {
            return;
        }

        if (xxlJobChain.getHandleCode() == ReturnT.FAIL_CODE) {
            XxlJobLog masterLog = xxlJobLogDao.load(xxlJobChain.getLogId());
            masterLog.setHandleCode(xxlJobChain.getHandleCode());
            masterLog.setAlarmStatus(0);
            XxlJobCompleter.updateHandleInfoAndFinish(masterLog);
            logger.info("[JOB-CHAIN-CALLBACK]->当前任务出现异常->[masterJobId:{},masterLogId:{},chainId:{},jobId:{},logId:{}]",
                    xxlJobChain.getJobId(), xxlJobChain.getLogId(), xxlJobChain.getId(), xxlJobLog.getJobId(), xxlJobLog.getId());
            return;
        }

        logger.info("[JOB-CHAIN-CALLBACK]->当前任务执行完毕,触发下一个->[masterJobId:{},masterLogId:{},chainId:{},jobId:{},logId:{}]",
                xxlJobChain.getJobId(), xxlJobChain.getLogId(), xxlJobChain.getId(), xxlJobLog.getJobId(), xxlJobLog.getId());

        //链路任务log
        XxlJobLog masterLog = xxlJobLogDao.load(xxlJobChainLog.getLogId());
        XkXxlJobTrigger.trigger(masterLog);
    }
}
