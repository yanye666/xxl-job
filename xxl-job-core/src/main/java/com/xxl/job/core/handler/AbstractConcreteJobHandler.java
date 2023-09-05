package com.xxl.job.core.handler;

import com.xxl.job.core.biz.model.ReturnT;

import java.util.List;

/**
 * @author 空青
 * @date 2022-01-21
 */
public class AbstractConcreteJobHandler<T> extends IConcreteJobHandler<T> {


    protected IConcreteJobHandler concreteJobHandler;

    public AbstractConcreteJobHandler(IConcreteJobHandler iConcreteJobHandler) {
        this.concreteJobHandler = iConcreteJobHandler;
    }

    @Override
    public ReturnT doExecute(T t) {
        return concreteJobHandler.doExecute(t);
    }

    @Override
    public List concreteJob(String param) {
        List jobs = concreteJobHandler.concreteJob(param);
        return jobs;
    }

    @Override
    public ReturnT<String> execute(String param) throws Exception {
        reset();
        List<T> jobs = concreteJob(param);
        setJobCount(jobs.size());
        doExecute(jobs);
        return ReturnT.SUCCESS;
    }

    public void reset() {
        concreteJobHandler.reset();
    }

    public void setJobCount(Integer jobCount) {
        concreteJobHandler.setJobCount(jobCount);
    }

    public Integer getJobCount() {
        return concreteJobHandler.getJobCount();
    }

    public Integer getExecuteCount() {
        return concreteJobHandler.getExecuteCount();
    }

    public Integer getSuccessCount() {
        return concreteJobHandler.getSuccessCount();
    }

    public List<ReturnT> getExecuteResult() {
        return concreteJobHandler.getExecuteResult();
    }
}
