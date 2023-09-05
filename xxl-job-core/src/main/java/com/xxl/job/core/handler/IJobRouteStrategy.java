package com.xxl.job.core.handler;

import java.util.List;

/**
 * @author 空青
 * @date 2022-01-20
 */
public interface IJobRouteStrategy<T> {

    List<T> getJobs(List<T> jobs);
}
