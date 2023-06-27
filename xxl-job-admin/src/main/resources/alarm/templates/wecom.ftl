{
"msgtype": "markdown",
"markdown": {
"content":"
状态:<font color=\"warning\"><#if jobLog.triggerCode != 200 && jobLog.triggerCode != 0 >调度失败<#else>执行失败</#if></font>，请<font color=\"warning\">${jobInfo.author}</font>注意。\n
> 执行器:<font color=\"comment\">${jobGroup.appname}</font>
> 任务:<font color=\"comment\">${jobInfo.executorHandler}</font>
> 任务描述:<font color=\"comment\">${jobInfo.jobDesc}</font>
> <font color=\"comment\">[点击查看](${domain}/joblog?id=${jobInfo.id})</font>
"
}

