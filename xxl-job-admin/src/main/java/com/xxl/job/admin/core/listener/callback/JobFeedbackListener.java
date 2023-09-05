package com.xxl.job.admin.core.listener.callback;

import com.xxl.job.admin.core.conf.XxlJobAdminConfig;
import com.xxl.job.admin.core.event.JobFeedbackEvent;
import com.xxl.job.admin.core.listener.trigger.JobChainTriggerListener;
import com.xxl.job.admin.core.model.XxlJobAlarm;
import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.core.model.XxlJobLog;
import com.xxl.job.admin.core.util.JacksonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;

/**
 * @author 空青
 * @date 2022-01-22
 */
@Component
public class JobFeedbackListener implements ApplicationListener<JobFeedbackEvent> {
	private static Logger logger = LoggerFactory.getLogger(JobChainTriggerListener.class);

	@Override
	public void onApplicationEvent(JobFeedbackEvent jobFeedbackEvent) {
		try {
			XxlJobInfo xxlJobInfo = jobFeedbackEvent.getXxlJobInfo();
			XxlJobLog xxlJobLog = jobFeedbackEvent.getXxlJobLog();
			List<XxlJobAlarm> jobAlarmList = XxlJobAdminConfig.getAdminConfig().getXxlJobAlarmDao().findByJobId(xxlJobInfo.getId());
			if (CollectionUtils.isEmpty(jobAlarmList)) {
				return;
			}
			xxlJobLog.setHandleMsg(jobFeedbackEvent.getHandleCallbackParam().getHandleMsg());
			xxlJobLog.setHandleCode(jobFeedbackEvent.getHandleCallbackParam().getHandleCode());
			xxlJobLog.setHandleTime(new Date(jobFeedbackEvent.getHandleCallbackParam().getLogDateTim()));
			boolean alarmResult = XxlJobAdminConfig.getAdminConfig().getJobAlarmer().alarm(xxlJobInfo, jobAlarmList, xxlJobLog);
		} catch (Exception e) {
			logger.error("反馈通知发生异常->[{}]", JacksonUtil.writeValueAsString(jobFeedbackEvent), e);
		}

	}
}
