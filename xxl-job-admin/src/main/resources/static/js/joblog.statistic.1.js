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
	var logTable = $("#joblog_list").dataTable({
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
				return obj;
			}
		},
		"searching": false,
		"ordering": false,
		//"scrollX": false,
		"columns": [
			{"data": 'jobId', "width": '5%', "visible": true},
			{"data": 'executorHandler', "width": '10%', "visible": true},
			{"data": 'jobDesc', "width": '10%', "visible": true},
			{"data": 'totalCount', "width": '7%', "visible": true},
			{"data": 'successCount', "width": '7%', "visible": true},
			{"data": 'errorCount', "width": '7%', "visible": true},
			{"data": 'triggerSuccessCount', "width": '7%', "visible": true},
			{"data": 'triggerErrorCount', "width": '7%', "visible": true},
			{"data": 'handleSuccessCount', "width": '7%', "visible": true},
			{"data": 'handleErrorCount', "width": '7%', "visible": true},
			{"data": 'maxHandleSecond', "width": '5%', "visible": true},
			{"data": 'minHandleSecond', "width": '5%', "visible": true},
			{"data": 'avgHandleSecond', "width": '5%', "visible": true}

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

	// logTips alert
	$('#joblog_list').on('click', '.logTips', function () {
		var msg = $(this).find('span').html();
		ComAlertTec.show(msg);
	});

	// search Btn
	$('#searchBtn').on('click', function () {
		logTable.fnDraw();
	});

	// logDetail look
	$('#joblog_list').on('click', '.logDetail', function () {
		var _id = $(this).attr('_id');

		window.open(base_url + '/joblog/logDetailPage?id=' + _id);
		return;
	});

	/**
	 * log Kill
	 */
	$('#joblog_list').on('click', '.logKill', function () {
		var _id = $(this).attr('_id');

		layer.confirm((I18n.system_ok + I18n.joblog_kill_log + '?'), {
			icon: 3,
			title: I18n.system_tips,
			btn: [I18n.system_ok, I18n.system_cancel]
		}, function (index) {
			layer.close(index);

			$.ajax({
				type: 'POST',
				url: base_url + '/joblog/logKill',
				data: {"id": _id},
				dataType: "json",
				success: function (data) {
					if (data.code == 200) {
						layer.open({
							title: I18n.system_tips,
							btn: [I18n.system_ok],
							content: I18n.system_opt_suc,
							icon: '1',
							end: function (layero, index) {
								logTable.fnDraw();
							}
						});
					} else {
						layer.open({
							title: I18n.system_tips,
							btn: [I18n.system_ok],
							content: (data.msg || I18n.system_opt_fail),
							icon: '2'
						});
					}
				},
			});
		});

	});

	/**
	 * clear Log
	 */
	$('#clearLog').on('click', function () {

		var jobGroup = $('#jobGroup').val();
		var jobId = $('#jobId').val();

		var jobGroupText = $("#jobGroup").find("option:selected").text();
		var jobIdText = $("#jobId").find("option:selected").text();

		$('#clearLogModal input[name=jobGroup]').val(jobGroup);
		$('#clearLogModal input[name=jobId]').val(jobId);

		$('#clearLogModal .jobGroupText').val(jobGroupText);
		$('#clearLogModal .jobIdText').val(jobIdText);

		$('#clearLogModal').modal('show');

	});
	$("#clearLogModal .ok").on('click', function () {
		$.post(base_url + "/joblog/clearLog", $("#clearLogModal .form").serialize(), function (data, status) {
			if (data.code == "200") {
				$('#clearLogModal').modal('hide');
				layer.open({
					title: I18n.system_tips,
					btn: [I18n.system_ok],
					content: (I18n.joblog_clean_log + I18n.system_success),
					icon: '1',
					end: function (layero, index) {
						logTable.fnDraw();
					}
				});
			} else {
				layer.open({
					title: I18n.system_tips,
					btn: [I18n.system_ok],
					content: (data.msg || (I18n.joblog_clean_log + I18n.system_fail)),
					icon: '2'
				});
			}
		});
	});
	$("#clearLogModal").on('hide.bs.modal', function () {
		$("#clearLogModal .form")[0].reset();
	});

});


// Com Alert by Tec theme
var ComAlertTec = {
	html: function () {
		var html =
			'<div class="modal fade" id="ComAlertTec" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">' +
			'	<div class="modal-dialog modal-lg-">' +
			'		<div class="modal-content-tec">' +
			'			<div class="modal-body">' +
			'				<div class="alert" style="color:#fff;word-wrap: break-word;">' +
			'				</div>' +
			'			</div>' +
			'				<div class="modal-footer">' +
			'				<div class="text-center" >' +
			'					<button type="button" class="btn btn-info ok" data-dismiss="modal" >' + I18n.system_ok + '</button>' +
			'				</div>' +
			'			</div>' +
			'		</div>' +
			'	</div>' +
			'</div>';
		return html;
	},
	show: function (msg, callback) {
		// dom init
		if ($('#ComAlertTec').length == 0) {
			$('body').append(ComAlertTec.html());
		}

		// init com alert
		$('#ComAlertTec .alert').html(msg);
		$('#ComAlertTec').modal('show');

		$('#ComAlertTec .ok').click(function () {
			$('#ComAlertTec').modal('hide');
			if (typeof callback == 'function') {
				callback();
			}
		});
	}
};
