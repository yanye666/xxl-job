{
"msgtype": "markdown",
"markdown": {
"content":"
任务执行失败
> 失败原因: <font color=\"warning\"><#if jobLog.triggerCode != 200 && jobLog.triggerCode != 0 >调度失败<#else>执行失败</#if></font> 请<font color=\"warning\">${jobInfo.author}</font>注意
> 执行器: <font color=\"comment\">${jobGroup.appname}</font>
> 任务: <font color=\"comment\">${jobInfo.executorHandler}</font>
> 任务描述: <font color=\"comment\">${jobInfo.jobDesc}</font>
> 执行时间: <font color=\"comment\">${(jobLog.triggerTime?string("yyyy-MM-dd HH:mm:ss"))}  ${(jobLog.handleTime?string("yyyy-MM-dd HH:mm:ss"))!}</font>
> <font color=\"comment\">[点击查看](${domain}/joblog?logId=${jobLog.id})</font>
"
}

