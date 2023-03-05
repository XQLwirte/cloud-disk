package com.zcx.cloud.menu.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zcx.cloud.common.vo.PageInfo;
import com.zcx.cloud.common.vo.Result;
import com.zcx.cloud.menu.entity.Menu;

/**
 * <p>
 * 菜单表 服务类
 * </p>
 *
 
 */
public interface IMenuService extends IService<Menu> {


	/**
	 * 分页查询菜单信息
	 * @return
	 */
	public Result getMenusPage(Menu menu,PageInfo pageInfo);
	
	/**
	 * 获取数据库所有的菜单信息组成树
	 * @return
	 */
	public List<Menu> getMenuTree();
	
	/**
	 * 获取所有的菜单
	 */
	public List<Menu> getAllMenu();
	
	/**
	 * 添加菜单
	 */
	public void addMenu(Menu menu);
	
	/**
	 * 更新菜单
	 */
	public void updateMenu(Menu menu);
	
	/**
	 * 删除菜单
	 */
	public void logicDeleteMenu(Long menuId);
}
