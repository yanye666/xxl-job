package com.xxl.job.admin.core.trigger;

/**
 * @author 空青
 * @date 2022-01-16
 */
public class JobChainParam {

    private Integer jobId;

    private String executorParam;

    private Integer sort;

    public JobChainParam() {
    }

    public JobChainParam(Integer jobId, String executeParam) {
        this.jobId = jobId;
        this.executorParam = executeParam;
    }

    public Integer getJobId() {
        return jobId;
    }

    public void setJobId(Integer jobId) {
        this.jobId = jobId;
    }

    public String getExecutorParam() {
        return executorParam;
    }

    public void setExecutorParam(String executorParam) {
        this.executorParam = executorParam;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

}
