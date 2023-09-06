$(function () {
	// jobGroup change, job list init and select
	$("#jobGroup").on("change", function () {
		var jobGroup = $(this).children('option:selected').val();
		$.ajax({
			type: 'POST',
			async: false,   // async, avoid js invoke pagelist before jobId data init
			url: base_url + '/joblog/getJobsByGroup',
			data: {"jobGroup": jobGroup},
			dataType: "json",
			success: function (data) {
				if (data.code == 200) {
					$("#jobId").html('<option value="0" >' + I18n.system_all + '</option>');
					$.each(data.content, function (n, value) {
						$("#jobId").append('<option value="' + value.id + '" >' + value.jobDesc + '</option>');
					});
					if ($("#jobId").attr("paramVal")) {
						$("#jobId").find("option[value='" + $("#jobId").attr("paramVal") + "']").attr("selected", true);
					}
				} else {
					layer.open({
						title: I18n.system_tips,
						btn: [I18n.system_ok],
						content: (data.msg || I18n.system_api_error),
						icon: '2'
					});
				}
			},
		});
	});
	if ($("#jobGroup").attr("paramVal")) {
		$("#jobGroup").find("option[value='" + $("#jobGroup").attr("paramVal") + "']").attr("selected", true);
		$("#jobGroup").change();
	}

	// filter Time
	var rangesConf = {};
	rangesConf[I18n.daterangepicker_ranges_recent_hour] = [moment().subtract(1, 'hours'), moment()];
	rangesConf[I18n.daterangepicker_ranges_today] = [moment().startOf('day'), moment().endOf('day')];
	rangesConf[I18n.daterangepicker_ranges_yesterday] = [moment().subtract(1, 'days').startOf('day'), moment().subtract(1, 'days').endOf('day')];
	rangesConf[I18n.daterangepicker_ranges_this_month] = [moment().startOf('month'), moment().endOf('month')];
	rangesConf[I18n.daterangepicker_ranges_last_month] = [moment().subtract(1, 'months').startOf('month'), moment().subtract(1, 'months').endOf('month')];
	rangesConf[I18n.daterangepicker_ranges_recent_week] = [moment().subtract(1, 'weeks').startOf('day'), moment().endOf('day')];
	rangesConf[I18n.daterangepicker_ranges_recent_month] = [moment().subtract(1, 'months').startOf('day'), moment().endOf('day')];

	$('#filterTime').daterangepicker({
		autoApply: false,
		singleDatePicker: false,
		showDropdowns: false,        // 是否显示年月选择条件
		timePicker: true, 			// 是否显示小时和分钟选择条件
		timePickerIncrement: 10, 	// 时间的增量，单位为分钟
		timePicker24Hour: true,
		opens: 'left', //日期选择框的弹出位置
		ranges: rangesConf,
		locale: {
			format: 'YYYY-MM-DD HH:mm:ss',
			separator: ' - ',
			customRangeLabel: I18n.daterangepicker_custom_name,
			applyLabel: I18n.system_ok,
			cancelLabel: I18n.system_cancel,
			fromLabel: I18n.daterangepicker_custom_starttime,
			toLabel: I18n.daterangepicker_custom_endtime,
			daysOfWeek: I18n.daterangepicker_custom_daysofweek.split(','),        // '日', '一', '二', '三', '四', '五', '六'
			monthNames: I18n.daterangepicker_custom_monthnames.split(','),        // '一月', '二月', '三月', '四月', '五月', '六月', '七月', '八月', '九月', '十月', '十一月', '十二月'
			firstDay: 1
		},
		startDate: rangesConf[I18n.daterangepicker_ranges_today][0],
		endDate: rangesConf[I18n.daterangepicker_ranges_today][1]
	});

	// init date tables
	var logTable = $("#job_statistic_list").dataTable({
		"deferRender": true,
		"processing": true,
		"serverSide": true,
		"ajax": {
			url: base_url + "/jobStatistic/pageList",
			type: "post",
			data: function (d) {
				var obj = {};
				obj.jobGroup = $('#jobGroup').val();
				obj.jobId = $('#jobId').val();
				obj.logId = $('#logId').val();
				obj.logStatus = $('#logStatus').val();
				obj.filterTime = $('#filterTime').val();
				obj.start = d.start;
				obj.length = d.length;
				obj.orderColumn = d.columns[d.order[0].column].data; // 获取排序列的字段名
				obj.orderAsc = d.order[0].dir; // 获取排序方向
				return obj;
			}
		},
		"searching": false,
		"ordering": true,
		//"scrollX": false,
		"order": [
			[4, 'desc']
		],
		"columns": [
			{"data": 'jobId', "width": '5%', "visible": true},
			{
				"data": 'scheduleType',
				"visible": true,
				"width": '10%',
				"render": function (data, type, row) {
					if (row.scheduleConf) {
						return row.scheduleType + '：' + row.scheduleConf;
					} else {
						return row.scheduleType;
					}
				}
			},
			{"data": 'executorHandler', "width": '10%', "visible": true},
			{"data": 'jobDesc', "width": '10%', "visible": true},
			{"data": 'totalCount', "width": '5%', "visible": true},
			{"data": 'successCount', "width": '5%', "visible": true},
			{"data": 'runningCount', "width": '5%', "visible": true},
			{"data": 'errorCount', "width": '5%', "visible": true},
			{"data": 'triggerSuccessCount', "width": '5%', "visible": true},
			{"data": 'triggerErrorCount', "width": '5%', "visible": true},
			{"data": 'handleSuccessCount', "width": '5%', "visible": true},
			{"data": 'handleErrorCount', "width": '5%', "visible": true},
			{
				"data": 'maxHandleSecond', "width": '5%', "visible": true,
				"render": function (data, type, row) {
					return formatTime(data);
				}
			},
			{
				"data": 'minHandleSecond', "width": '5%', "visible": true,
				"render": function (data, type, row) {
					return formatTime(data);
				}
			},
			{
				"data": 'avgHandleSecond', "width": '5%', "visible": true,
				"render": function (data, type, row) {
					return formatTime(data);
				}
			}

		],
		"language": {
			"sProcessing": I18n.dataTable_sProcessing,
			"sLengthMenu": I18n.dataTable_sLengthMenu,
			"sZeroRecords": I18n.dataTable_sZeroRecords,
			"sInfo": I18n.dataTable_sInfo,
			"sInfoEmpty": I18n.dataTable_sInfoEmpty,
			"sInfoFiltered": I18n.dataTable_sInfoFiltered,
			"sInfoPostFix": "",
			"sSearch": I18n.dataTable_sSearch,
			"sUrl": "",
			"sEmptyTable": I18n.dataTable_sEmptyTable,
			"sLoadingRecords": I18n.dataTable_sLoadingRecords,
			"sInfoThousands": ",",
			"oPaginate": {
				"sFirst": I18n.dataTable_sFirst,
				"sPrevious": I18n.dataTable_sPrevious,
				"sNext": I18n.dataTable_sNext,
				"sLast": I18n.dataTable_sLast
			},
			"oAria": {
				"sSortAscending": I18n.dataTable_sSortAscending,
				"sSortDescending": I18n.dataTable_sSortDescending
			}
		}
	});
	logTable.on('xhr.dt', function (e, settings, json, xhr) {
		if (json.code && json.code != 200) {
			layer.msg(json.msg || I18n.system_api_error);
		}
	});

	// search Btn
	$('#searchBtn').on('click', function () {
		logTable.fnDraw();
	});

	$(document).keyup(function(e){
		var key = e.which;
		if(key==13){
			logTable.fnDraw();
		}
	});
});

