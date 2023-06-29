<!DOCTYPE html>
<html>
<head>
    <#import "../common/common.macro.ftl" as netCommon>
    <@netCommon.commonStyle />
    <!-- DataTables -->
    <link rel="stylesheet"
          href="${request.contextPath}/static/adminlte/bower_components/datatables.net-bs/css/dataTables.bootstrap.min.css">
    <!-- daterangepicker -->
    <link rel="stylesheet"
          href="${request.contextPath}/static/adminlte/bower_components/bootstrap-daterangepicker/daterangepicker.css">
    <title>${I18n.admin_name}</title>
</head>
<body class="hold-transition skin-blue sidebar-mini <#if cookieMap?exists && cookieMap["xxljob_adminlte_settings"]?exists && "off" == cookieMap["xxljob_adminlte_settings"].value >sidebar-collapse</#if> ">
<div class="wrapper">
    <!-- header -->
    <@netCommon.commonHeader />
    <!-- left -->
    <@netCommon.commonLeft "jobStatistic" />

    <!-- Content Wrapper. Contains page content -->
    <div class="content-wrapper">
        <!-- Content Header (Page header) -->
        <section class="content-header">
            <h1>${I18n.joblog_name}</h1>
        </section>

        <!-- Main content -->
        <section class="content">
            <div class="row">
                <div class="col-xs-2">
                    <div class="input-group">
                        <span class="input-group-addon">${I18n.jobinfo_field_jobgroup}</span>
                        <select class="form-control" id="jobGroup"
                                paramVal="<#if jobInfo?exists>${jobInfo.jobGroup}</#if>">
                            <#if Request["XXL_JOB_LOGIN_IDENTITY"].role == 1>
                                <option value="0">${I18n.system_all}</option>  <#-- 仅管理员支持查询全部；普通用户仅支持查询有权限的 jobGroup -->
                            </#if>
                            <#list JobGroupList as group>
                                <option value="${group.id}">${group.title}</option>
                            </#list>
                        </select>
                    </div>
                </div>
                <div class="col-xs-2">
                    <div class="input-group">
                        <span class="input-group-addon">${I18n.jobinfo_job}</span>
                        <select class="form-control" id="jobId" paramVal="<#if jobInfo?exists>${jobInfo.id}</#if>">
                            <option value="0">${I18n.system_all}</option>
                        </select>
                    </div>
                </div>

                <div class="col-xs-4">
                    <div class="input-group">
                		<span class="input-group-addon">
	                  		${I18n.joblog_field_triggerTime}
	                	</span>
                        <input type="text" class="form-control" id="filterTime" readonly>
                    </div>
                </div>

                <div class="col-xs-1">
                    <button class="btn btn-block btn-info" id="searchBtn">${I18n.system_search}</button>
                </div>
            </div>

            <div class="row">
                <div class="col-xs-12">
                    <div class="box">
                        <#--<div class="box-header hide"><h3 class="box-title">调度日志</h3></div>-->
                        <div class="box-body">
                            <table id="job_statistic_list" class="table table-bordered table-striped display" width="100%">
                                <thead>
                                <tr>
                                    <th name="jobId">任务ID</th>
                                    <th name="scheduleType">调度类型</th>
                                    <th name="executorHandler">执行器</th>
                                    <th name="jobDesc">任务描述</th>
                                    <th name="totalCount">总数量</th>
                                    <th name="successCount">成功数</th>
                                    <th name="runningCount">进行中</th>
                                    <th name="errorCount">失败数</th>
                                    <th name="triggerSuccessCount">调度成功数</th>
                                    <th name="triggerErrorCount">调度失败数</th>
                                    <th name="handleSuccessCount">执行成功数</th>
                                    <th name="handleErrorCount">执行失败数</th>
                                    <th name="maxHandleSecond">最大耗时</th>
                                    <th name="minHandleSecond">最小耗时</th>
                                    <th name="avgHandleSecond">平均耗时</th>
                                </tr>
                                </thead>
                                <tbody></tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        </section>
    </div>

    <!-- footer -->
    <@netCommon.commonFooter />
</div>
<@netCommon.commonScript />
<!-- DataTables -->
<script src="${request.contextPath}/static/adminlte/bower_components/datatables.net/js/jquery.dataTables.min.js"></script>
<script src="${request.contextPath}/static/adminlte/bower_components/datatables.net-bs/js/dataTables.bootstrap.min.js"></script>
<!-- daterangepicker -->
<script src="${request.contextPath}/static/adminlte/bower_components/moment/moment.min.js"></script>
<script src="${request.contextPath}/static/adminlte/bower_components/bootstrap-daterangepicker/daterangepicker.js"></script>
<script src="${request.contextPath}/static/js/joblog.statistic.1.js"></script>
</body>
</html>
