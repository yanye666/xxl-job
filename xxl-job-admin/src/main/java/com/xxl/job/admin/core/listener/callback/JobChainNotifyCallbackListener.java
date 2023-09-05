
package com.xxl.job.admin.core.listener.callback;

import com.xxl.job.admin.core.conf.XxlJobAdminConfig;
import com.xxl.job.admin.core.event.JobCallbackEvent;
import com.xxl.job.admin.core.model.*;
import com.xxl.job.admin.core.util.JacksonUtil;
import com.xxl.job.admin.dao.*;
import com.xxl.job.core.enums.ReturnCodeEnums;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 空青
 * @date 2022-01-17
 */
@Order(999)
@Component
public class JobChainNotifyCallbackListener implements ApplicationListener<JobCallbackEvent> {

	private static Logger logger = LoggerFactory.getLogger(JobChainNotifyCallbackListener.class);

	@Resource
	XxlJobInfoDao xxlJobInfoDao;
	@Resource
	XxlJobGroupDao xxlJobGroupDao;
	@Resource
	XxlJobLogDao xxlJobLogDao;
	@Resource
	XxlJobChainDao xxlJobChainDao;
	@Resource
	XxlJobChainLogDao xxlJobChainLogDao;

	@Override
	public void onApplicationEvent(JobCallbackEvent jobCallbackEvent) {
		try {
			//当前任务
			XxlJobLog xxlJobLog = jobCallbackEvent.getXxlJobLog();
			//链路记录与日志的映射
			XxlJobChainLog xxlJobChainLog = xxlJobChainLogDao.selectByExecutorLogId(xxlJobLog.getId());
			if (null == xxlJobChainLog) {
				return;
			}
			XxlJobInfo chainInfo = xxlJobInfoDao.loadById(xxlJobChainLog.getJobId());
			XxlJobLog chainLog = xxlJobLogDao.load(xxlJobChainLog.getLogId());
			List<XxlJobAlarm> jobAlarmList = XxlJobAdminConfig.getAdminConfig().getXxlJobAlarmDao().findByJobId(chainInfo.getId());
			if (CollectionUtils.isEmpty(jobAlarmList)) {
				return;
			}
			String template = "{\n" +
					"    \"msgtype\": \"markdown\",\n" +
					"    \"markdown\": {\n" +
					"        \"content\": \"%s\"\n" +
					"    }\n" +
					"}";
			String content = String.format(template, getContent(chainInfo, chainLog));
			if (StringUtils.isBlank(content)) {
				return;
			}
			boolean alarmResult = XxlJobAdminConfig.getAdminConfig().getJobAlarmer().alarm(chainInfo, jobAlarmList, xxlJobLog, content);
		} catch (Exception e) {
			logger.error("通知发生异常->[{}]", JacksonUtil.writeValueAsString(jobCallbackEvent), e);
		}

	}

	public String getContent(XxlJobInfo jobInfo, XxlJobLog jobLog) {
		List<XxlJobChain> xxlJobChains = xxlJobChainDao.selectByLogId(jobLog.getId());
		if (CollectionUtils.isEmpty(xxlJobChains)) {
			return null;
		}
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("编排任务:%s\n" +
						"执行结果:<font color=\\\"warning\\\">%s</font>，请<font color=\\\"warning\\\">%s</font>注意。\\n\n",
				jobInfo.getJobDesc(), ReturnCodeEnums.ofText(jobLog.getHandleCode()), jobInfo.getAuthor()));
		String template = ">链路ID:<font color=\\\"comment\\\">%s</font>" +
				"   执行器:<font color=\\\"comment\\\">%s</font>" +
				"   JOB:<font color=\\\"comment\\\">%s</font>\\n" +
				"描述:<font color=\\\"comment\\\">%s</font>" +
				"   分片数:<font color=\\\"comment\\\">%s</font>" +
				"   状态:<font color=\\\"warning\\\">%s</font>\\n ---\n";
		if (xxlJobChains.size() > 7) {
			xxlJobChains = xxlJobChains.stream().skip(xxlJobChains.size() - 7).collect(Collectors.toList());
		}
		for (XxlJobChain xxlJobChain : xxlJobChains) {
			XxlJobInfo jobInfo1 = xxlJobInfoDao.loadById(xxlJobChain.getExecutorJobId());
			XxlJobGroup xxlJobGroup = xxlJobGroupDao.load(jobInfo1.getJobGroup());
			List<XxlJobChainLog> xxlJobChainLogs = xxlJobChainLogDao.selectByExecutorChainId(xxlJobChain.getLogId(), xxlJobChain.getId());
			sb.append(String.format(template,
					xxlJobChain.getId(),
					xxlJobGroup.getAppname(),
					jobInfo1.getExecutorHandler(),
					jobInfo1.getJobDesc(),
					xxlJobChainLogs.size(),
					ReturnCodeEnums.ofText(xxlJobChain.getHandleCode())));
		}
		return sb.toString();
	}
}
