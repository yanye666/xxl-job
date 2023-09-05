package com.xxl.job.core.handler.impl;

import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.AbstractConcreteJobHandler;
import com.xxl.job.core.handler.IConcreteJobHandler;

import java.util.List;

/**
 * 并行执行
 *
 * @author 空青
 * @date 2022-01-19
 */
public class ParallelJobHandler<T> extends AbstractConcreteJobHandler<T> {

    public ParallelJobHandler(IConcreteJobHandler<T> concreteJobHandler) {
        super(concreteJobHandler);
    }

    @Override
    public void doExecute(List<T> jobs) {
        jobs.parallelStream().forEach(job -> {
            ReturnT result = concreteJobHandler.doExecute(job);
            concreteJobHandler.increment(result);
        });
    }
}
