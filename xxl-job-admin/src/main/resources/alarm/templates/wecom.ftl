{
"msgtype": "markdown",
"markdown": {
"content":"
任务执行通知
> 通知类型: <font color=\"warning\"><#if jobLog.handleCode == 100 >执行中<#elseif jobLog.triggerCode != 200 && jobLog.triggerCode != 0>调度失败<#elseif jobLog.handleCode != 200>执行失败</#if></font> 请<font color=\"warning\">${jobInfo.author}</font>注意
> 执行器: <font color=\"comment\">${jobGroup.appname}</font>
> 任务: <font color=\"comment\">${jobInfo.executorHandler}</font>
> 任务描述: <font color=\"comment\">${jobInfo.jobDesc}</font>
> 执行时间: <font color=\"comment\">${(jobLog.triggerTime?string("yyyy-MM-dd HH:mm:ss"))}  ${(jobLog.handleTime?string("yyyy-MM-dd HH:mm:ss"))!}</font>
<#if jobLog.handleMsg ??>> 执行结果: <font color=\"comment\">${jobLog.handleMsg[0..1000]}</font></#if>
> <font color=\"comment\">[点击查看](${domain}/joblog?logId=${jobLog.id})</font>
"
}

