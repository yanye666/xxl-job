package com.xxl.job.admin.core.trigger;

import com.xxl.job.admin.core.complete.XxlJobCompleter;
import com.xxl.job.admin.core.conf.XxlJobAdminConfig;
import com.xxl.job.admin.core.model.XxlJobChain;
import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.core.model.XxlJobLog;
import com.xxl.job.admin.core.util.JacksonUtil;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.glue.GlueTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * xxl-job trigger
 * Created by xuxueli on 17/7/13.
 */
public class XkXxlJobTrigger extends XxlJobTrigger {

    String a = "{\"error_no\":\"0\",\"error_info\":\"success!\",\"data\":\"{\\\"id\\\":1,\\\"name\\\":\\\"www\\\"}\"}\n";

    public static void main(String[] args) {
        String a = "[{\"jobId\":210,\"executorParam\":\"\",\"sort\":0},{\"jobId\":222,\"executorParam\":\"\",\"sort\":1},{\"jobId\":183,\"executorParam\":\"\",\"sort\":2},{\"jobId\":221,\"executorParam\":\"\",\"sort\":3},{\"jobId\":209,\"executorParam\":\"\",\"sort\":4},{\"jobId\":271,\"executorParam\":\"\",\"sort\":5},{\"jobId\":287,\"executorParam\":\"\",\"sort\":6},{\"jobId\":171,\"executorParam\":\"\",\"sort\":7},{\"jobId\":219,\"executorParam\":\"{\\\"noInitMonths\\\":[202212,202301]}\",\"sort\":8},{\"jobId\":446,\"executorParam\":\"\",\"sort\":9},{\"jobId\":479,\"executorParam\":\"\",\"sort\":10},{\"jobId\":478,\"executorParam\":\"\",\"sort\":11},{\"jobId\":77,\"executorParam\":\"\",\"sort\":12}]";
        List<JobChainParam> jobChainParams = JacksonUtil.readList(a, JobChainParam.class);
        System.out.println(jobChainParams);
    }

    private static Logger logger = LoggerFactory.getLogger(XkXxlJobTrigger.class);

    public static void trigger(int jobId, TriggerTypeEnum triggerType, int failRetryCount, String executorShardingParam, String executorParam, String addressList) {
        XxlJobInfo jobInfo = XxlJobAdminConfig.getAdminConfig().getXxlJobInfoDao().loadById(jobId);
        if (jobInfo == null) {
            logger.warn(">>>>>>>>>>>> trigger fail, jobId invalid，jobId={}", jobId);
            return;
        }
        if (GlueTypeEnum.JOB_CHAIN != GlueTypeEnum.match(jobInfo.getGlueType())) {
            XxlJobTrigger.trigger(jobId, triggerType, failRetryCount, executorShardingParam, executorParam, addressList);
            return;
        }
        List<JobChainParam> jobChainParams = JacksonUtil.readList(executorParam, JobChainParam.class);
        if (CollectionUtils.isEmpty(jobChainParams)) {
            logger.warn(">>>>>>>>>>>> trigger fail, chainParams is null，jobId={}", jobId);
            return;
        }

        //插入链路job日志
        XxlJobLog jobLog = new XxlJobLog();
        jobLog.setJobGroup(jobInfo.getJobGroup());
        jobLog.setJobId(jobInfo.getId());
        jobLog.setTriggerTime(new Date());
        jobLog.setExecutorParam(executorParam);
        jobLog.setTriggerCode(ReturnT.SUCCESS_CODE);
        jobLog.setAlarmStatus(-1);
        XxlJobAdminConfig.getAdminConfig().getXxlJobLogDao().save(jobLog);
        jobLog.setExecutorAddress(addressList);
        jobLog.setExecutorHandler(jobInfo.getExecutorHandler());
        jobLog.setExecutorParam(jobInfo.getExecutorParam());
        jobLog.setTriggerCode(ReturnT.SUCCESS_CODE);
        XxlJobAdminConfig.getAdminConfig().getXxlJobLogDao().updateTriggerInfo(jobLog);
        jobChainParams.sort(Comparator.comparingInt(JobChainParam::getSort));
        List<XxlJobChain> xxlJobChains = jobChainParams.stream().map(jobChainParam -> {
            XxlJobChain xxlJobChain = new XxlJobChain();
            xxlJobChain.setJobId(jobLog.getJobId());
            xxlJobChain.setLogId(jobLog.getId());
            xxlJobChain.setExecutorJobId(jobChainParam.getJobId());
            xxlJobChain.setSort(jobChainParam.getSort());
            xxlJobChain.setHandleCode(ReturnT.WAIT_CODE);
            xxlJobChain.setExecutorParam(jobChainParam.getExecutorParam());
            return xxlJobChain;
        }).collect(Collectors.toList());
        XxlJobAdminConfig.getAdminConfig().getXxlJobChainDao().saveList(xxlJobChains);
        trigger(jobLog);
    }

    public static void trigger(XxlJobLog xxlJobLog) {
        XxlJobInfo jobInfo = XxlJobAdminConfig.getAdminConfig().getXxlJobInfoDao().loadById(xxlJobLog.getJobId());

        List<XxlJobChain> xxlJobChains = XxlJobAdminConfig.getAdminConfig().getXxlJobChainDao().selectByLogId(xxlJobLog.getId());
        long count = xxlJobChains.stream()
                .filter(xxlJobChain -> ReturnT.RUNNING_CODE == xxlJobChain.getHandleCode())
                .count();
        if (count > 0) {
            logger.info("[JOb-CHAIN-TRIGGER]->还有任务在执行->[masterJobId:{},masterLogId:{}]", xxlJobLog.getJobId(), xxlJobLog.getId());
            return;
        }
        Optional<XxlJobChain> optionalXxlJobChain = xxlJobChains.stream()
                .filter(xxlJobChain -> ReturnT.WAIT_CODE == xxlJobChain.getHandleCode()).findFirst();
        if (!optionalXxlJobChain.isPresent()) {
            xxlJobLog.setHandleCode(ReturnT.SUCCESS_CODE);
            xxlJobLog.setHandleTime(new Date());
            XxlJobCompleter.updateHandleInfoAndFinish(xxlJobLog);
            logger.info("[JOb-CHAIN-TRIGGER]->链路任务执行完成->[masterJobId:{},masterLogId:{}]", xxlJobLog.getJobId(), xxlJobLog.getId());
        } else {
            optionalXxlJobChain.ifPresent(xxlJobChain -> {
                JobChainHelper.set(xxlJobChain);
                xxlJobChain.setHandleCode(ReturnT.RUNNING_CODE);
                XxlJobAdminConfig.getAdminConfig().getXxlJobChainDao().update(xxlJobChain);
                XxlJobTrigger.trigger(xxlJobChain.getExecutorJobId(), TriggerTypeEnum.PARENT, -1, null, xxlJobChain.getExecutorParam(), null);
                JobChainHelper.remove();
            });
        }

    }


}
