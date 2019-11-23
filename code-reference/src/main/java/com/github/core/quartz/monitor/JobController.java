package com.github.core.quartz.monitor;

import com.github.core.authorization.Permission;
import com.github.core.quartz.monitor.entity.LiteJob;
import com.github.core.quartz.monitor.service.QuartzMonitor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.github.core.modules.exception.BusinessException;
import org.github.core.modules.web.BaseControl;
import org.github.core.modules.web.ajax.ResultJson;

import java.util.List;
import java.util.Map;

@Controller
@Permission
public class JobController extends BaseControl{
	@Autowired(required = false)
	private QuartzMonitor quartzMonitor;
	@RequestMapping(value="list")
	public String list(Model model) throws BusinessException{
		List<LiteJob> list = quartzMonitor.queryAll();
		model.addAttribute("list", list);
		return viewpath("list");
	}
	@RequestMapping(value="pausejob",method = RequestMethod.POST)
	public @ResponseBody ResultJson pauseJob(@RequestParam(value="id") String id) throws BusinessException{
		quartzMonitor.pauseJob(id);
		return new ResultJson();
	}
	@RequestMapping(value="resumejob",method = RequestMethod.POST)
	public @ResponseBody ResultJson resumeJob(@RequestParam(value="id") String id) throws BusinessException{
		quartzMonitor.resumeJob(id);
		return new ResultJson();
	}
	@RequestMapping(value="updateJobCron")
	public @ResponseBody ResultJson updateJobCron(@RequestParam(value="id") String id,@RequestParam(value="cronexpression") String cronexpression) throws BusinessException{
		quartzMonitor.updateJobCron(id,cronexpression);
		return new ResultJson();
	}

	@RequestMapping(value="addJob")
	public @ResponseBody ResultJson addJob(@RequestParam(value="id") String id) throws BusinessException{
		quartzMonitor.addJob(id);
		return new ResultJson();
	}
	@RequestMapping(value="changeTime")
	public @ResponseBody ResultJson changeTime(@RequestParam(value="id") String id,@RequestParam(value="time") String time) throws BusinessException{
		quartzMonitor.changeTime(id,time);
		return new ResultJson();
	}
	@RequestMapping(value="changeTimeShow")
	public String changeTimeShow(Model model, @RequestParam Map map) throws BusinessException{
		model.addAttribute("map",map);
		return viewpath("changeTime");
	}
}
