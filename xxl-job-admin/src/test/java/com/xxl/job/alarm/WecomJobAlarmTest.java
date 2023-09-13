package com.xxl.job.alarm;


import com.xxl.job.admin.XxlJobAdminApplication;
import com.xxl.job.admin.core.alarm.JobAlarmMessageConverter;
import com.xxl.job.admin.core.model.XxlJobGroup;
import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.core.model.XxlJobLog;
import com.xxl.job.alarm.wecom.WecomJobAlarm;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.annotation.Resource;
import java.util.Properties;

@SpringBootTest(classes = XxlJobAdminApplication.class)
@WebAppConfiguration
public class WecomJobAlarmTest {


	@Resource
	private JobAlarmMessageConverter jobAlarmMessageConverter;

	@Test
	public void test() throws Exception {
		Properties properties = new Properties();
		properties.setProperty(AlarmConstants.ALARM_TARGET, "ccb122ee-e023-41f8-a8a8-217eb562320f");
		String template = "{\n" +
				"    \"msgtype\": \"markdown\",\n" +
				"    \"markdown\": {\n" +
				"        \"content\": \"%s\"\n" +
				"    }\n" +
				"}";
		XxlJobGroup xxlJobGroup = new XxlJobGroup();
		XxlJobInfo xxlJobInfo = new XxlJobInfo();
		XxlJobLog xxlJobLog = new XxlJobLog();
		xxlJobLog.setHandleMsg("1211");
		String wecom = jobAlarmMessageConverter.convert("wecom", properties, xxlJobGroup, xxlJobInfo, xxlJobLog);
		boolean alarm = new WecomJobAlarm().doAlarm(properties, wecom);
	}
}