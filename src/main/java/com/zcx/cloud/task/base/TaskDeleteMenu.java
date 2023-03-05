package com.zcx.cloud.task.base;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zcx.cloud.common.constant.Constant;
import com.zcx.cloud.menu.entity.Menu;
import com.zcx.cloud.menu.mapper.MenuMapper;
import com.zcx.cloud.util.CollectionUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * 删除菜单定时任务
 * 
 * 1.删除菜单基本信息
 * 涉及表：t_menu
 * @author ZCX
 *
 */
@Slf4j
@Component("taskDeleteMenu")
public class TaskDeleteMenu {
	@Autowired
	private MenuMapper menuMapper;
	
	public void deleteMenu() {
		//1.删除所有无效菜单信息
		LambdaQueryWrapper<Menu> qwM = new LambdaQueryWrapper<Menu>();
		qwM.eq(Menu::getValid, Constant.VALID_DELETE);
		menuMapper.delete(qwM);
	}
	
}
