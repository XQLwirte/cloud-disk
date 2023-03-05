package com.zcx.cloud.job.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.zcx.cloud.job.entity.Job;
import com.zcx.cloud.job.service.IJobService;
import com.zcx.cloud.util.ViewUtil;

@Controller("jobViewController")
@RequestMapping("/base/job")
public class ViewController {
	@Autowired
	private IJobService jobService;
	
	@GetMapping("job")
	public String job() {
		return ViewUtil.view("base/job/job");
	}
	
	@GetMapping("jobAdd")
	public String jobAdd() {
		return ViewUtil.view("base/job/jobAdd");
	}
	
	@GetMapping("jobDetail/{jobId}")
	public String jobDetail(@PathVariable("jobId")Long jobId, Model model) {
		Job job = jobService.getById(jobId);
		model.addAttribute(job);
		return ViewUtil.view("base/job/jobDetail");
	}
	
	@GetMapping("jobEdit/{jobId}")
	public String jobEdit(@PathVariable("jobId")Long jobId, Model model) {
		Job job = jobService.getById(jobId);
		model.addAttribute(job);
		return ViewUtil.view("base/job/jobEdit");
	}
}
