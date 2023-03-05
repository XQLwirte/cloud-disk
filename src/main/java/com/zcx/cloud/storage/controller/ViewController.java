package com.zcx.cloud.storage.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.zcx.cloud.storage.entity.Storage;
import com.zcx.cloud.storage.service.IStorageService;
import com.zcx.cloud.util.ViewUtil;

@Controller("storageViewController")
@RequestMapping("/storage")
public class ViewController {
	@Autowired
	private IStorageService storageService;
	
	@GetMapping("storage")
	public String storage() {
		return ViewUtil.view("storage/storage");
	}
	
	@GetMapping("storageDetail/{storageId}")
	public String storageDetail(@PathVariable("storageId")Long storageId, Model model) {
		Storage storage = storageService.getStorageWithUsername(storageId);
		model.addAttribute(storage);
		return ViewUtil.view("storage/storageDetail");
	}
	
	@GetMapping("storageEdit/{storageId}")
	public String storageEdit(@PathVariable("storageId")Long storageId, Model model) {
		Storage storage = storageService.getStorageWithUsername(storageId);
		model.addAttribute(storage);
		return ViewUtil.view("storage/storageEdit");
	}
	
}
