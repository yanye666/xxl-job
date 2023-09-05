package com.xxl.job.core.handler;

import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.context.XxlJobHelper;

/**
 * job handler
 *
 * @author xuxueli 2015-12-19 19:06:38
 */
public abstract class BaseJobHandler extends IJobHandler {

	/**
	 * success
	 */
	public static final ReturnT<String> SUCCESS = new ReturnT<String>(200, null);
	/**
	 * fail
	 */
	public static final ReturnT<String> FAIL = new ReturnT<String>(500, null);
	/**
	 * fail timeout
	 */
	public static final ReturnT<String> FAIL_TIMEOUT = new ReturnT<String>(502, null);

	@Override
	public void execute() throws Exception {
		ReturnT execute = execute(XxlJobHelper.getJobParam());
		if (ReturnT.SUCCESS_CODE == execute.getCode()) {
			XxlJobHelper.handleSuccess();
		} else {
			XxlJobHelper.handleFail();
		}
	}

	/**
	 * execute handler, invoked when executor receives a scheduling request
	 *
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public abstract ReturnT<String> execute(String param) throws Exception;

}
