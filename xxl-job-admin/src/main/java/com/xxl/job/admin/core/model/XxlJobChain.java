package com.xxl.job.admin.core.model;

public class XxlJobChain {

    private Integer id;

    /**
     * 任务，主键id
     */
    private Integer jobId;

    /**
     * 任务日志，主键id
     */
    private Long logId;

    /**
     * 执行任务id
     */
    private Integer executorJobId;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 执行-状态
     */
    private Integer handleCode;

    private String executorParam;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getJobId() {
        return jobId;
    }

    public void setJobId(Integer jobId) {
        this.jobId = jobId;
    }

    public Long getLogId() {
        return logId;
    }

    public void setLogId(Long logId) {
        this.logId = logId;
    }

    public Integer getExecutorJobId() {
        return executorJobId;
    }

    public void setExecutorJobId(Integer executorJobId) {
        this.executorJobId = executorJobId;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public Integer getHandleCode() {
        return handleCode;
    }

    public void setHandleCode(Integer handleCode) {
        this.handleCode = handleCode;
    }

    public String getExecutorParam() {
        return executorParam;
    }

    public void setExecutorParam(String executorParam) {
        this.executorParam = executorParam;
    }
}