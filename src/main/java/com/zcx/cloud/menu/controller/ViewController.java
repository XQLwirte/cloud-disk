package com.zcx.cloud.menu.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.zcx.cloud.menu.entity.Menu;
import com.zcx.cloud.menu.service.IMenuService;
import com.zcx.cloud.util.ViewUtil;

@Controller("menuViewController")
@RequestMapping("/base/menu")
public class ViewController {
	@Autowired
	private IMenuService menuService;
	
	/**
	 * 菜单页面
	 */
	@GetMapping("menu")
	public String menu() {
		return ViewUtil.view("base/menu/menu");
	}
	
}
