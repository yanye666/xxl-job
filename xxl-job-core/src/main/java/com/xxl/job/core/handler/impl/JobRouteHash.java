package com.xxl.job.core.handler.impl;

import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.IJobRouteStrategy;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 空青
 * @date 2022-01-22
 */
public class JobRouteHash<T> implements IJobRouteStrategy<T> {

    @Override
    public List<T> getJobs(List<T> jobs) {
        if (CollectionUtils.isEmpty(jobs)) {
            return jobs;
        }
        int shardTotal = XxlJobHelper.getShardTotal();
        int shardIndex = XxlJobHelper.getShardIndex();
        jobs = jobs.stream()
                .filter(job -> job.hashCode() % shardTotal == shardIndex)
                .collect(Collectors.toList());
        return jobs;
    }
}
