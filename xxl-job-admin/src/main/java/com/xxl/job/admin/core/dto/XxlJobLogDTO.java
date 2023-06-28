package com.xxl.job.admin.core.dto;

import com.xxl.job.admin.core.model.XxlJobLog;

/**
 * @author 空青
 * @since 2023/6/27
 **/
public class XxlJobLogDTO extends XxlJobLog {

    private String jobDesc;

    private Long handleSecond;

    public Long getHandleSecond() {
        return handleSecond;
    }

    public void setHandleSecond(Long handleSecond) {
        this.handleSecond = handleSecond;
    }

    public String getJobDesc() {
        return jobDesc;
    }

    public void setJobDesc(String jobDesc) {
        this.jobDesc = jobDesc;
    }
}
