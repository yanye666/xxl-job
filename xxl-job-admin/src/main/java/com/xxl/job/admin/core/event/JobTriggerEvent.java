package com.xxl.job.admin.core.event;

import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.core.model.XxlJobLog;
import org.springframework.context.ApplicationEvent;

/**
 * @author 空青
 * @date 2022-01-16
 */
public class JobTriggerEvent extends ApplicationEvent {

    private final XxlJobLog xxlJobLog;
    private final XxlJobInfo xxlJobInfo;

    public JobTriggerEvent(Object source, XxlJobInfo xxlJobInfo, XxlJobLog xxlJobLog) {
        super(source);
        this.xxlJobInfo = xxlJobInfo;
        this.xxlJobLog = xxlJobLog;
    }

    public XxlJobLog getXxlJobLog() {
        return xxlJobLog;
    }

    public XxlJobInfo getXxlJobInfo() {
        return xxlJobInfo;
    }
}
