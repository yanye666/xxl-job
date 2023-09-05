package com.xxl.job.admin.core.trigger;

import com.xxl.job.admin.core.model.XxlJobChain;

/**
 * @author 空青
 * @date 2022-01-16
 */
public class JobChainHelper {

    protected static ThreadLocal<XxlJobChain> chainThreadLocal = new ThreadLocal<XxlJobChain>();

    public static void remove() {
        chainThreadLocal.remove();
    }

    public static void set(XxlJobChain xxlJobChain) {
        chainThreadLocal.set(xxlJobChain);
    }

    public static XxlJobChain get() {
        return chainThreadLocal.get();
    }

}
