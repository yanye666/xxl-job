package com.xxl.job.admin.core.dto;

import com.xxl.job.admin.core.model.XxlJobInfo;

import java.util.Date;

/**
 * @author 空青
 * @since 2023/6/27
 **/

public class XxlJobForecastDTO extends XxlJobInfo {

    private Integer totalCount;

    private Date lastTime;

    private Date firstTime;

    private XxlJobStatisticDTO jobStatistic;

    public XxlJobStatisticDTO getJobStatistic() {
        return jobStatistic;
    }

    public void setJobStatistic(XxlJobStatisticDTO jobStatistic) {
        this.jobStatistic = jobStatistic;
    }

    public Date getFirstTime() {
        return firstTime;
    }

    public void setFirstTime(Date firstTime) {
        this.firstTime = firstTime;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public Date getLastTime() {
        return lastTime;
    }

    public void setLastTime(Date lastTime) {
        this.lastTime = lastTime;
    }
}
