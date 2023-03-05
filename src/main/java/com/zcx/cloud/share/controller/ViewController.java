package com.zcx.cloud.share.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.zcx.cloud.share.entity.Share;
import com.zcx.cloud.share.service.IShareService;
import com.zcx.cloud.util.ViewUtil;

@Controller("shareViewController")
@RequestMapping("/share")
public class ViewController {
	@Autowired
	private IShareService shareService;

	@GetMapping("share")
	public String share() {
		return ViewUtil.view("share/share");
	}
	
	

	@GetMapping("shareDetail/{shareId}")
	public String shareDetail(@PathVariable("shareId")Long shareId, Model model) {
		Share share = shareService.getShareById(shareId);
		model.addAttribute(share);
		return ViewUtil.view("share/shareDetail");
	}
	
}
