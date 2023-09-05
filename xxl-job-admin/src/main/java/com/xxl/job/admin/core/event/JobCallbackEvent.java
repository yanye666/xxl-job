package com.xxl.job.admin.core.event;

import com.xxl.job.admin.core.model.XxlJobLog;
import org.springframework.context.ApplicationEvent;

/**
 * @author 空青
 * @date 2022-01-16
 */
public class JobCallbackEvent extends ApplicationEvent {

    private final XxlJobLog xxlJobLog;

    public JobCallbackEvent(Object source, XxlJobLog xxlJobLog) {
        super(source);
        this.xxlJobLog = xxlJobLog;
    }

    public XxlJobLog getXxlJobLog() {
        return xxlJobLog;
    }

}
