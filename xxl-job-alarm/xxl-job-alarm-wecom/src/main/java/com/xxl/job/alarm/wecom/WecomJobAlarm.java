package com.xxl.job.alarm.wecom;

import com.xxl.job.alarm.JobAlarm;
import com.xxl.job.alarm.SPI;

import java.util.Properties;

@SPI("wecom")
public class WecomJobAlarm implements JobAlarm {
    @Override
    public boolean doAlarm(Properties config, String message) {
        return new WecomSender(config).sendMsg(message);
    }
}