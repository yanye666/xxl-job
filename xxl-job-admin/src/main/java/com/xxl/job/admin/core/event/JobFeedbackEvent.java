package com.xxl.job.admin.core.event;

import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.core.model.XxlJobLog;
import com.xxl.job.core.biz.model.HandleCallbackParam;
import org.springframework.context.ApplicationEvent;

/**
 * @author 空青
 * @date 2022-01-16
 */
public class JobFeedbackEvent extends ApplicationEvent {

    private final XxlJobLog xxlJobLog;
    private final XxlJobInfo xxlJobInfo;
    private final HandleCallbackParam handleCallbackParam;

    public JobFeedbackEvent(Object source, XxlJobInfo xxlJobInfo, XxlJobLog xxlJobLog, HandleCallbackParam handleCallbackParam) {
        super(source);
        this.xxlJobInfo = xxlJobInfo;
        this.xxlJobLog = xxlJobLog;
        this.handleCallbackParam = handleCallbackParam;
    }

    public XxlJobLog getXxlJobLog() {
        return xxlJobLog;
    }

    public XxlJobInfo getXxlJobInfo() {
        return xxlJobInfo;
    }

    public HandleCallbackParam getHandleCallbackParam() {
        return handleCallbackParam;
    }
}
