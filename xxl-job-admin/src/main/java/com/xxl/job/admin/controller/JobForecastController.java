package com.xxl.job.admin.controller;

import com.xxl.job.admin.core.cron.CronExpression;
import com.xxl.job.admin.core.dto.XxlJobForecastDTO;
import com.xxl.job.admin.core.dto.XxlJobStatisticDTO;
import com.xxl.job.admin.core.exception.XxlJobException;
import com.xxl.job.admin.core.model.XxlJobGroup;
import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.core.util.I18nUtil;
import com.xxl.job.admin.dao.XxlJobGroupDao;
import com.xxl.job.admin.dao.XxlJobInfoDao;
import com.xxl.job.admin.dao.XxlJobLogDao;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * index controller
 *
 * @author xuxueli 2015-12-19 16:13:16
 */
@Controller
@RequestMapping("/jobForecast")
public class JobForecastController {
	private static Logger logger = LoggerFactory.getLogger(JobForecastController.class);

	@Resource
	private XxlJobGroupDao xxlJobGroupDao;
	@Resource
	public XxlJobInfoDao xxlJobInfoDao;
	@Resource
	public XxlJobLogDao xxlJobLogDao;

	@RequestMapping
	public String index(HttpServletRequest request, Model model, @RequestParam(required = false, defaultValue = "0") Integer jobId) {
		// 执行器列表
		List<XxlJobGroup> jobGroupList_all = xxlJobGroupDao.findAll();

		// filter group
		List<XxlJobGroup> jobGroupList = JobInfoController.filterJobGroupByRole(request, jobGroupList_all);
		if (jobGroupList == null || jobGroupList.size() == 0) {
			throw new XxlJobException(I18nUtil.getString("jobgroup_empty"));
		}
		model.addAttribute("JobGroupList", jobGroupList);
		return "jobinfo/jobinfo.forecast";
	}

	@RequestMapping("/pageList")
	@ResponseBody
	public Map<String, Object> pageList(HttpServletRequest request,
										@RequestParam(required = false, defaultValue = "0") int start,
										@RequestParam(required = false, defaultValue = "10") int length,
										Integer excludeGtCount,
										int jobGroup, @RequestParam(required = false) Integer logId, String filterTime) {
		// valid permission
		JobInfoController.validPermission(request, jobGroup);    // 仅管理员支持查询全部；普通用户仅支持查询有权限的 jobGroup

		// parse param
		Date triggerTimeStart;
		Date triggerTimeEnd;
		if (filterTime != null && filterTime.trim().length() > 0) {
			String[] temp = filterTime.split(" - ");
			if (temp.length == 2) {
				triggerTimeStart = DateUtil.parseDateTime(temp[0]);
				triggerTimeEnd = DateUtil.parseDateTime(temp[1]);
			} else {
				triggerTimeEnd = null;
				triggerTimeStart = null;
			}
		} else {
			triggerTimeEnd = null;
			triggerTimeStart = null;
		}
		int list_count = xxlJobInfoDao.pageListCount(start, length, jobGroup, 1, null, null, null);
		List<XxlJobInfo> list = xxlJobInfoDao.pageList(start, length, jobGroup, 1, null, null, null);

		// package result
		Map<String, Object> maps = new HashMap<String, Object>();
		maps.put("recordsTotal", list_count);        // 总记录数
		maps.put("recordsFiltered", list_count);    // 过滤后的总记录数
		maps.put("data", list);                    // 分页列表
		if (!list.isEmpty()) {
			List<XxlJobForecastDTO> collect = new ArrayList<>();
			List<Integer> jobs = list.stream().map(XxlJobInfo::getId).collect(Collectors.toList());
			Map<Integer, XxlJobStatisticDTO> statisticMap = xxlJobLogDao.pageLogStatistic(0, Integer.MAX_VALUE, jobGroup, jobs, DateUtil.addDays(triggerTimeEnd, -3), triggerTimeEnd, null, null)
					.stream().collect(Collectors.toMap(XxlJobStatisticDTO::getJobId, Function.identity()));
			for (XxlJobInfo xxlJobInfo : list) {
				XxlJobForecastDTO xxlJobForecastDTO = new XxlJobForecastDTO();
				BeanUtils.copyProperties(xxlJobInfo, xxlJobForecastDTO);
				List<Date> nextValidTimeAfter = getNextValidTimeAfter(xxlJobInfo.getScheduleConf(), triggerTimeStart, triggerTimeEnd);
				if (excludeGtCount != null && nextValidTimeAfter.size() > excludeGtCount) {
					continue;
				}
				xxlJobForecastDTO.setTotalCount(nextValidTimeAfter.size());
				xxlJobForecastDTO.setFirstTime(CollectionUtils.firstElement(nextValidTimeAfter));
				xxlJobForecastDTO.setLastTime(CollectionUtils.lastElement(nextValidTimeAfter));
				Optional.ofNullable(statisticMap.get(xxlJobInfo.getId())).ifPresent(xxlJobForecastDTO::setJobStatistic);
				collect.add(xxlJobForecastDTO);
			}
			collect.sort(Comparator.comparing(XxlJobForecastDTO::getTotalCount).reversed());
			maps.put("data", collect);
		}
		return maps;
	}

	public List<Date> getNextValidTimeAfter(String cronExpression, Date startDate, Date endDate) {
		CronExpression cron;
		try {
			cron = new CronExpression(cronExpression);
		} catch (ParseException e) {
			logger.error(e.getMessage(), e);
			return Collections.emptyList();
		}
		Date d1;
		List<Date> list = new ArrayList<>();
		while (true) {
			d1 = cron.getNextValidTimeAfter(startDate);
			if (d1 != null && d1.compareTo(endDate) <= 0) {
				list.add(d1);
				startDate = d1;
			} else {
				break;
			}
		}
		return list;
	}

	@RequestMapping("/chartInfo")
	@ResponseBody
	public ReturnT<Map<String, Object>> chartInfo(Integer jobGroup, Integer excludeGtCount, Date startDate, Date endDate) {
		// process
		List<String> triggerHourList = new ArrayList<String>();
		List<Integer> triggerCountList = new ArrayList<Integer>();
		List<Integer> handleSecondList = new ArrayList<Integer>();
		Date time = startDate;
		while (time.compareTo(endDate) <= 0) {
			triggerHourList.add(DateUtil.formatDateHourTime(time));
			time = DateUtil.addHours(time, 1);
		}
		HashMap<String, Integer> map = new HashMap<>();
		HashMap<String, Integer> mapSecond = new HashMap<>();
		triggerHourList = triggerHourList.stream().distinct().collect(Collectors.toList());
		List<XxlJobInfo> list = xxlJobInfoDao.pageList(0, Integer.MAX_VALUE, jobGroup, 1, null, null, null);
		if (!list.isEmpty()) {
			List<Integer> jobs = list.stream().map(XxlJobInfo::getId).collect(Collectors.toList());
			Map<Integer, XxlJobStatisticDTO> statisticMap = xxlJobLogDao.pageLogStatistic(0, Integer.MAX_VALUE, jobGroup, jobs, DateUtil.addDays(endDate, -3), endDate, null, null)
					.stream().collect(Collectors.toMap(XxlJobStatisticDTO::getJobId, Function.identity()));

			for (XxlJobInfo xxlJobInfo : list) {
				List<Date> nextValidTimeAfter = getNextValidTimeAfter(xxlJobInfo.getScheduleConf(), startDate, endDate);
				if (excludeGtCount != null && nextValidTimeAfter.size() > excludeGtCount) {
					continue;
				}
				for (Date date : nextValidTimeAfter) {
					String key = DateUtil.formatDateHourTime(date);
					Integer avgHandleSecond = Optional.ofNullable(statisticMap.get(xxlJobInfo.getId())).map(XxlJobStatisticDTO::getAvgHandleSecond).orElse(0);
					mapSecond.put(key, mapSecond.getOrDefault(key, 0) + avgHandleSecond);
					map.put(key, map.getOrDefault(key, 0) + 1);
				}
			}
		}
		for (String s : triggerHourList) {
			triggerCountList.add(map.getOrDefault(s, 0));
			handleSecondList.add(mapSecond.getOrDefault(s, 0));
		}
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("triggerHourList", triggerHourList);
		result.put("triggerCountList", triggerCountList);
		result.put("handleSecondList", handleSecondList);
		return new ReturnT<>(result);
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		dateFormat.setLenient(false);
		binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
	}

}
