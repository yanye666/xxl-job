package com.xxl.job.admin.core.listener.trigger;

import com.xxl.job.admin.core.complete.XxlJobCompleter;
import com.xxl.job.admin.core.conf.XxlJobAdminConfig;
import com.xxl.job.admin.core.event.JobTriggerEvent;
import com.xxl.job.admin.core.model.XxlJobChain;
import com.xxl.job.admin.core.model.XxlJobChainLog;
import com.xxl.job.admin.core.model.XxlJobLog;
import com.xxl.job.admin.core.trigger.JobChainHelper;
import com.xxl.job.core.biz.model.ReturnT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 链路调度成功入库
 *
 * @author 空青
 * @date 2022-01-16
 */
@Order(0)
@Component
public class JobChainTriggerListener implements ApplicationListener<JobTriggerEvent> {

    private static Logger logger = LoggerFactory.getLogger(JobChainTriggerListener.class);


    @Override
    public void onApplicationEvent(JobTriggerEvent jobTriggerEvent) {
        XxlJobLog xxlJobLog = jobTriggerEvent.getXxlJobLog();
        XxlJobChain xxlJobChain = JobChainHelper.get();
        if (null == xxlJobChain) {
            return;
        }
        XxlJobChainLog xxlJobChainLog = new XxlJobChainLog();
        xxlJobChainLog.setJobId(xxlJobChain.getJobId());
        xxlJobChainLog.setLogId(xxlJobChain.getLogId());
        xxlJobChainLog.setChainId(xxlJobChain.getId());
        xxlJobChainLog.setExecutorJobId(xxlJobLog.getJobId());
        if (xxlJobLog.getTriggerCode() == ReturnT.SUCCESS_CODE) {
            xxlJobChainLog.setHandleCode(ReturnT.RUNNING_CODE);
        } else {
            xxlJobChainLog.setHandleCode(ReturnT.FAIL_CODE);
        }
        xxlJobChainLog.setExecutorLogId(xxlJobLog.getId());
        XxlJobAdminConfig.getAdminConfig().getXxlJobChainLogDao().save(xxlJobChainLog);
        logger.info("[JOB-CHAIN-TRIGGER]->[masterJobId:{},masterLogId:{},chainId:{},jobId:{},logId:{}]",
                xxlJobChain.getJobId(), xxlJobChain.getLogId(), xxlJobChain.getId(), xxlJobLog.getJobId(), xxlJobLog.getId());

        if (xxlJobLog.getTriggerCode() == ReturnT.FAIL_CODE) {
            //链路状态+链路任务日志 状态修改为失败
            int i = XxlJobAdminConfig.getAdminConfig().getXxlJobChainDao().updateStatus(xxlJobChain.getId(), ReturnT.RUNNING_CODE, ReturnT.FAIL_CODE);
            XxlJobLog masterLog = XxlJobAdminConfig.getAdminConfig().getXxlJobLogDao().load(xxlJobChain.getLogId());
            masterLog.setHandleCode(xxlJobChain.getHandleCode());
            masterLog.setAlarmStatus(0);
            XxlJobCompleter.updateHandleInfoAndFinish(masterLog);
            logger.info("[JOB-CHAIN-CALLBACK]->当前任务出现异常->[masterJobId:{},masterLogId:{},chainId:{},jobId:{},logId:{}]",
                    xxlJobChain.getJobId(), xxlJobChain.getLogId(), xxlJobChain.getId(), xxlJobLog.getJobId(), xxlJobLog.getId());
        }
    }
}
