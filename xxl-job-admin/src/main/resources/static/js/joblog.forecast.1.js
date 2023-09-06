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
	freshChartDate(rangesConf[I18n.daterangepicker_ranges_today][0].format('YYYY-MM-DD HH:mm:ss'), rangesConf[I18n.daterangepicker_ranges_today][1].format('YYYY-MM-DD HH:mm:ss'));

	// init date tables
	var logTable = $("#job_forecast_list").dataTable({
		"deferRender": true,
		"processing": true,
		"serverSide": true,
		"pageLength": 100,  // 设置默认分页大小为10
		"ajax": {
			url: base_url + "/jobForecast/pageList",
			type: "post",
			data: function (d) {
				var obj = {};
				obj.jobGroup = $('#jobGroup').val();
				obj.jobId = $('#jobId').val();
				obj.logId = $('#logId').val();
				obj.excludeGtCount = $('#excludeGtCount').val();
				obj.logStatus = $('#logStatus').val();
				obj.filterTime = $('#filterTime').val();
				obj.start = d.start;
				obj.length = d.length;
				return obj;
			}
		},
		"searching": false,
		"ordering": false,
		//"scrollX": false,
		"columns": [
			{"data": 'id', "width": '5%', "visible": true},
			{
				"data": 'scheduleType',
				"visible" : true,
				"width":'13%',
				"render": function ( data, type, row ) {
					if (row.scheduleConf) {
						return row.scheduleType + '：'+ row.scheduleConf;
					} else {
						return row.scheduleType;
					}
				}
			},
			{"data": 'executorHandler', "width": '10%', "visible": true},
			{"data": 'jobDesc', "width": '10%', "visible": true},
			{"data": 'totalCount', "width": '7%', "visible": true},
			{
				"data": 'firstTime',
				"width":'10%',
				"render": function ( data, type, row ) {
					return data?moment(data).format("YYYY-MM-DD HH:mm:ss"):"";
				}
			},
			{
				"data": 'lastTime',
				"width":'10%',
				"render": function ( data, type, row ) {
					return data?moment(data).format("YYYY-MM-DD HH:mm:ss"):"";
				}
			},
			{
				"data": 'jobStatistic.avgHandleSecond',
				"width":'10%',
				"render": function (data, type, row) {
					if (row.jobStatistic && row.jobStatistic.avgHandleSecond !== null) {
						return formatTime(data);
					} else {
						return "";
					}
				}
			},

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
		let [value1, value2] = $('#filterTime').val().split(" - ");
		freshChartDate(value1, value2);
	});

	$(document).keyup(function(e){
		var key = e.which;
		if(key==13){
			logTable.fnDraw();
		}
	});

	$('#jobGroup').on('change', function(){
		logTable.fnDraw();
		let [value1, value2] = $('#filterTime').val().split(" - ");
		freshChartDate(value1, value2);
	});

	function freshChartDate(startDate, endDate) {
		$.ajax({
			type: 'POST',
			url: base_url + '/jobForecast/chartInfo',
			data: {
				'startDate': startDate,
				'endDate': endDate,
				'jobGroup': $('#jobGroup').val(),
				'excludeGtCount': $('#excludeGtCount').val()
			},
			dataType: "json",
			success: function (data) {
				if (data.code == 200) {
					lineChartInit(data)
				} else {
					layer.open({
						title: I18n.system_tips,
						btn: [I18n.system_ok],
						content: (data.msg || I18n.job_dashboard_report_loaddata_fail),
						icon: '2'
					});
				}
			}
		});
	}

	/**
	 * line Chart Init
	 */
	function lineChartInit(data) {
		var option = {
			title: {
				text: I18n.job_dashboard_date_report
			},
			tooltip: {
				trigger: 'axis',
				axisPointer: {
					type: 'cross',
					label: {
						backgroundColor: '#6a7985'
					}
				}
			},
			legend: {
				data: ["预测任务调度次数", "近三日累计平均耗时(s)"]
			},
			toolbox: {
				feature: {
					/*saveAsImage: {}*/
				}
			},
			grid: {
				left: '3%',
				right: '4%',
				bottom: '3%',
				containLabel: true
			},
			xAxis: [
				{
					type: 'category',
					boundaryGap: false,
					data: data.content.triggerHourList
				}
			],
			yAxis: [
				{
					type: 'value'
				}
			],
			series: [
				{
					name: "预测任务调度次数",
					type: 'line',
					stack: 'Total',
					areaStyle: {normal: {}},
					data: data.content.triggerCountList
				},
				{
					name: "近三日累计平均耗时(s)",
					type: 'line',
					stack: 'Total',
					label: {
						normal: {
							show: true,
							position: 'top'
						}
					},
					areaStyle: {normal: {}},
					data: data.content.handleSecondList
				}
			],
			color: ['#00A65A', '#c23632', '#F39C12']
		};

		var lineChart = echarts.init(document.getElementById('lineChart'));
		lineChart.setOption(option);
	}
});
