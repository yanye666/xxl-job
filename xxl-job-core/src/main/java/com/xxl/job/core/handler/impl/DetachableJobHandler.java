package com.xxl.job.core.handler.impl;

import com.xxl.job.core.handler.AbstractConcreteJobHandler;
import com.xxl.job.core.handler.IConcreteJobHandler;
import com.xxl.job.core.util.CollUtil;

import java.util.List;

/**
 * 可切分的任务
 *
 * @author 空青
 * @date 2022-01-19
 */
public class DetachableJobHandler<T> extends AbstractConcreteJobHandler<T> {

    protected IConcreteJobHandler<T> concreteJobHandler;
    protected Integer splitSize = 100;

    public DetachableJobHandler(IConcreteJobHandler<T> concreteJobHandler) {
        super(concreteJobHandler);
        this.concreteJobHandler = concreteJobHandler;
    }

    public DetachableJobHandler(IConcreteJobHandler<T> concreteJobHandler, Integer splitSize) {
        super(concreteJobHandler);
        this.concreteJobHandler = concreteJobHandler;
        this.splitSize = splitSize;
    }

    @Override
    public void doExecute(List<T> jobs) {
        List<List<T>> split = CollUtil.split(jobs, splitSize);
        for (List<T> ts : split) {
            concreteJobHandler.doExecute(ts);
        }
    }
}
