package com.xxl.job.core.handler.impl;

import com.xxl.job.core.biz.model.HandleCallbackParam;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.biz.model.TriggerParam;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.AbstractConcreteJobHandler;
import com.xxl.job.core.handler.IConcreteJobHandler;
import com.xxl.job.core.thread.TriggerCallbackThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * 可拆分的任务
 *
 * @author 空青
 * @date 2022-01-19
 */
public class FeedbackJobHandler<T> extends AbstractConcreteJobHandler<T> {

    Logger log = LoggerFactory.getLogger(FeedbackJobHandler.class);

    private ArrayList<ReturnT> feedResult = new ArrayList<>();

    public FeedbackJobHandler(IConcreteJobHandler<T> concreteJobHandler) {
        super(concreteJobHandler);
    }

    @Override
    public void doExecute(List<T> jobs) {
        concreteJobHandler.doExecute(jobs);
        log.debug("xxl-job-反馈进度" + getExecuteCount() + "/" + getJobCount());
        List<ReturnT> executeResult = concreteJobHandler.getExecuteResult();
        String result = String.format("进度：总任务数量:%d,执行数量:%d,成功数量:%d", getJobCount(), getExecuteCount(), getSuccessCount());
        StringBuilder sb = new StringBuilder();
        sb.append(result)
                .append('\n');
        for (ReturnT returnT : executeResult) {
            if (feedResult.contains(returnT)) {
                continue;
            }
            sb.append(returnT.getMsg())
                    .append('\n');
            feedResult.add(returnT);
        }
        TriggerParam triggerParam = XxlJobHelper.getTriggerParam();
        TriggerCallbackThread.pushCallBack(new HandleCallbackParam(triggerParam.getLogId(), triggerParam.getLogDateTime(), ReturnT.RUNNING_CODE, sb.toString()));
    }

}
