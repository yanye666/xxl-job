package com.xxl.job.admin.core.dto;

/**
 * @author 空青
 * @since 2023/6/28
 **/
public class XxlJobStatisticDTO {

    private Integer jobId;
    private String executorHandler;
    private String jobDesc;
    private Integer totalCount;
    private Integer successCount;
    private Integer errorCount;
    private Integer triggerSuccessCount;
    private Integer triggerErrorCount;
    private Integer handleSuccessCount;
    private Integer handleErrorCount;
    private Integer maxHandleSecond;
    private Integer minHandleSecond;
    private Integer avgHandleSecond;

    public String getExecutorHandler() {
        return executorHandler;
    }

    public void setExecutorHandler(String executorHandler) {
        this.executorHandler = executorHandler;
    }

    public Integer getJobId() {
        return jobId;
    }

    public void setJobId(Integer jobId) {
        this.jobId = jobId;
    }

    public String getJobDesc() {
        return jobDesc;
    }

    public void setJobDesc(String jobDesc) {
        this.jobDesc = jobDesc;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public Integer getSuccessCount() {
        return successCount;
    }

    public void setSuccessCount(Integer successCount) {
        this.successCount = successCount;
    }

    public Integer getErrorCount() {
        return errorCount;
    }

    public void setErrorCount(Integer errorCount) {
        this.errorCount = errorCount;
    }

    public Integer getTriggerSuccessCount() {
        return triggerSuccessCount;
    }

    public void setTriggerSuccessCount(Integer triggerSuccessCount) {
        this.triggerSuccessCount = triggerSuccessCount;
    }

    public Integer getTriggerErrorCount() {
        return triggerErrorCount;
    }

    public void setTriggerErrorCount(Integer triggerErrorCount) {
        this.triggerErrorCount = triggerErrorCount;
    }

    public Integer getHandleSuccessCount() {
        return handleSuccessCount;
    }

    public void setHandleSuccessCount(Integer handleSuccessCount) {
        this.handleSuccessCount = handleSuccessCount;
    }

    public Integer getHandleErrorCount() {
        return handleErrorCount;
    }

    public void setHandleErrorCount(Integer handleErrorCount) {
        this.handleErrorCount = handleErrorCount;
    }

    public Integer getMaxHandleSecond() {
        return maxHandleSecond;
    }

    public void setMaxHandleSecond(Integer maxHandleSecond) {
        this.maxHandleSecond = maxHandleSecond;
    }

    public Integer getMinHandleSecond() {
        return minHandleSecond;
    }

    public void setMinHandleSecond(Integer minHandleSecond) {
        this.minHandleSecond = minHandleSecond;
    }

    public Integer getAvgHandleSecond() {
        return avgHandleSecond;
    }

    public void setAvgHandleSecond(Integer avgHandleSecond) {
        this.avgHandleSecond = avgHandleSecond;
    }
}
