package com.xxl.job.admin.core.model;

public class XxlJobChainLog {

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
     * 链路id
     */
    private Integer chainId;

    /**
     * 执行任务id
     */
    private Integer executorJobId;

    /**
     * 执行任务的日志id
     */
    private Long executorLogId;

    /**
     * 执行-状态
     */
    private Integer handleCode;


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

    public Integer getHandleCode() {
        return handleCode;
    }

    public void setHandleCode(Integer handleCode) {
        this.handleCode = handleCode;
    }

    public Long getExecutorLogId() {
        return executorLogId;
    }

    public void setExecutorLogId(Long executorLogId) {
        this.executorLogId = executorLogId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getChainId() {
        return chainId;
    }

    public void setChainId(Integer chainId) {
        this.chainId = chainId;
    }
}