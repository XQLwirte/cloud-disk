package com.zcx.cloud.folder.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.zcx.cloud.folder.entity.Folder;
import com.zcx.cloud.folder.service.IFolderService;
import com.zcx.cloud.util.ViewUtil;

@Controller("folderViewController")
@RequestMapping("/folder")
public class ViewController {
	@Autowired
	private IFolderService folderService;
	
	@GetMapping("folder")
	public String folder() {
		return ViewUtil.view("folder/folder");
	}
	
	@GetMapping("folderEdit/{folderId}")
	public String folderEdit(@PathVariable("folderId")Long folderId, Model model) {
		Folder folder = folderService.getById(folderId);
		model.addAttribute(folder);
		return ViewUtil.view("folder/folderEdit");
	}
	
}
