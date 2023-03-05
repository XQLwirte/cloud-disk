package com.zcx.cloud.garbage.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.zcx.cloud.garbage.entity.Garbage;
import com.zcx.cloud.garbage.service.IGarbageService;
import com.zcx.cloud.util.ViewUtil;

@Controller("garbageViewController")
@RequestMapping("/garbage")
public class ViewController {
	@Autowired
	private IGarbageService garbageService;
	
	@GetMapping("garbage")
	public String garbage() {
		return ViewUtil.view("garbage/garbage");
	}
	
	
	@GetMapping("garbageDetail/{garbageId}")
	public String garbageDetail(@PathVariable("garbageId")Long garbageId,Model model) {
		Garbage garbage = garbageService.getGarbageById(garbageId);
		model.addAttribute(garbage);
		return ViewUtil.view("garbage/garbageDetail");
	}
	
}
