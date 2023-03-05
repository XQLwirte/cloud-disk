package com.zcx.cloud.log.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.zcx.cloud.log.entity.Log;
import com.zcx.cloud.log.service.ILogService;
import com.zcx.cloud.util.ViewUtil;

@Controller("logViewController")
@RequestMapping("/base/log")
public class ViewController {
	@Autowired
	private ILogService logService;
	
	/**
	 * 日志首页
	 * @return
	 */
	@GetMapping("log")
	public String log() {
		return ViewUtil.view("base/log/log");
	}
	
	/**
	 * 日志详情
	 */
	@GetMapping("logDetail/{logId}")
	public String logDetail(Model model,@PathVariable("logId")Long logId) {
		Log log = logService.getById(logId);
		model.addAttribute("log", log);
		return ViewUtil.view("base/log/logDetail");
	}
	
}
