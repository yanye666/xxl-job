package com.xxl.job.core.handler;

import com.xxl.job.core.biz.model.ReturnT;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author 空青
 * @date 2022/1/20
 */
public abstract class IConcreteJobHandler<T> extends BaseJobHandler {


    private Integer jobCount = 0;

    private AtomicInteger executeCount = new AtomicInteger(0);

    private List<ReturnT> executeResult = new ArrayList<>();

    private AtomicInteger successCount = new AtomicInteger(0);

    public void reset() {
        jobCount = 0;
        executeCount = new AtomicInteger(0);
        successCount = new AtomicInteger(0);
        executeResult.clear();
    }

    public abstract ReturnT doExecute(T t);

    public void doExecute(List<T> jobs) {
        for (T job : jobs) {
            ReturnT result = doExecute(job);
            increment(result);
        }
    }

    public void increment(ReturnT result) {
        if (result.getCode() == 200) {
            successCount.incrementAndGet();
        }
        executeCount.incrementAndGet();
        executeResult.add(result);
    }

    public abstract List<T> concreteJob(String param);

    @Override
    public ReturnT<String> execute(String param) throws Exception {
        reset();
        List<T> jobs = concreteJob(param);
        jobCount = jobs.size();
        doExecute(jobs);
        return ReturnT.SUCCESS;
    }

    public Integer getJobCount() {
        return jobCount;
    }

    public void setJobCount(Integer jobCount) {
        this.jobCount = jobCount;
    }

    public Integer getExecuteCount() {
        return executeCount.get();
    }

    public Integer getSuccessCount() {
        return successCount.get();
    }

    public List<ReturnT> getExecuteResult() {
        return executeResult;
    }
}
