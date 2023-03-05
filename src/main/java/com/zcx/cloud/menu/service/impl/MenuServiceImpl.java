package com.zcx.cloud.menu.service.impl;

import com.zcx.cloud.common.constant.Constant;
import com.zcx.cloud.common.vo.PageInfo;
import com.zcx.cloud.common.vo.Result;
import com.zcx.cloud.menu.entity.Menu;
import com.zcx.cloud.menu.mapper.MenuMapper;
import com.zcx.cloud.menu.service.IMenuService;
import com.zcx.cloud.role.entity.Role;
import com.zcx.cloud.role.entity.RoleMenu;
import com.zcx.cloud.role.mapper.RoleMapper;
import com.zcx.cloud.role.mapper.RoleMenuMapper;
import com.zcx.cloud.util.AppUtil;
import com.zcx.cloud.util.CollectionUtil;
import com.zcx.cloud.util.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.assertj.core.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 菜单表 服务实现类
 * </p>
 *
 
 */
@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements IMenuService {
	@Autowired
	private MenuMapper menuMapper;
	@Autowired
	private RoleMenuMapper roleMenuMapper;
	@Autowired
	private RoleMapper roleMapper;
	
	@Override
	public List<Menu> getMenuTree() {
		//1.获取所有顶级菜单
		LambdaQueryWrapper<Menu> queryWrapper = new LambdaQueryWrapper<Menu>();
		queryWrapper.eq(Menu::getParentId, Constant.TOP_MENU_PARENT_ID);
		queryWrapper.eq(Menu::getValid, Constant.VALID_NORMAL);
		queryWrapper.orderByAsc(Menu::getMenuSort);
		List<Menu> menuTree = menuMapper.selectList(queryWrapper);
		
		//2.递归获取菜单树
		if(Objects.nonNull(menuTree)&&menuTree.size()>0) {
			menuTree.stream().forEach(menu -> {
				//查询菜单所需角色代码
				menu.setRoleCodes(getRoleCodesWidthComma(menu));
				//挂载子菜单
				mountChildMenus(menu);
			});
		}
		
		//3.返回结果
		return menuTree;
	}
	/**
	 * 递归挂载所有字节点
	 * @param menus
	 */
	private void mountChildMenus(Menu menu) {
		//1.查询子节点信息
		Long menuId = menu.getMenuId();
		LambdaQueryWrapper<Menu> queryWrapper = new LambdaQueryWrapper<Menu>();
		queryWrapper.eq(Menu::getParentId, menuId);
		queryWrapper.eq(Menu::getValid, Constant.VALID_NORMAL);
		queryWrapper.orderByAsc(Menu::getMenuSort);
		List<Menu> childMenus = menuMapper.selectList(queryWrapper);
		
		//2.存在子节点则继续挂载，没有则直接返回
		if(CollectionUtil.noNullAndSizeGtZero(childMenus)) {
			//挂载
			menu.setChildren(childMenus);
			//查询子节点
			childMenus.forEach(childMenu -> {
				//查询菜单所需角色代码
				childMenu.setRoleCodes(getRoleCodesWidthComma(childMenu));
				//挂载子菜单
				mountChildMenus(childMenu);
			});
		}
	}

	/**
	 * 根据菜单信息获取到菜单所需的角色码，用逗号分割
	 * @param menu
	 * @return
	 */
	private String getRoleCodesWidthComma(Menu menu) {
		String roleCodes = "";
		//1.查询菜单角色关联信息
		LambdaQueryWrapper<RoleMenu> queryWrapperRM = new LambdaQueryWrapper<RoleMenu>();
		queryWrapperRM.eq(RoleMenu::getMenuId, menu.getMenuId());
		List<RoleMenu> roleMenus = roleMenuMapper.selectList(queryWrapperRM);
		if(CollectionUtil.isNullOrSizeLeZero(roleMenus))
			return roleCodes;
		
		//2.查询角色信息
		List<Long> roleIds = roleMenus.stream().map(roleMenu -> roleMenu.getRoleId()).collect(Collectors.toList());
		LambdaQueryWrapper<Role> queryWrapperR = new LambdaQueryWrapper<Role>();
		queryWrapperR.in(Role::getRoleId, roleIds);
		queryWrapperR.eq(Role::getValid, Constant.VALID_NORMAL);
		List<Role> roles = roleMapper.selectList(queryWrapperR);
		if(CollectionUtil.isNullOrSizeLeZero(roles))
			return roleCodes;
		
		//3.返回所需代码串
		for(Role role:roles) {
			roleCodes += role.getRoleCode() + Constant.COMMA;
		}
		roleCodes = roleCodes.substring(0, roleCodes.length()-1);
		return roleCodes;
	}

	@Override
	public Result getMenusPage(Menu menu,PageInfo pageInfo) {
		LambdaQueryWrapper<Menu> queryWrapper = new LambdaQueryWrapper<Menu>();
		//设置查询条件
		if(Objects.nonNull(menu)) {
			if(!Strings.isNullOrEmpty(menu.getMenuName()))
				queryWrapper.like(Menu::getMenuName, menu.getMenuName());
			if(!Strings.isNullOrEmpty(menu.getMenuUrl()))
				queryWrapper.like(Menu::getMenuUrl, menu.getMenuUrl());
			if(Objects.nonNull(menu.getCreateTime()))
				queryWrapper.gt(Menu::getCreateTime, menu.getCreateTime());
		}
		queryWrapper.eq(Menu::getValid, Constant.VALID_NORMAL);
		
		Page selectPage = menuMapper.selectPage(pageInfo.getPage(), queryWrapper);
		long count = selectPage.getTotal();
		List<Menu> data = selectPage.getRecords();
		return new Result().success().count(count).data(data);
	}
	
	@Override
	public List<Menu> getAllMenu() {
		LambdaQueryWrapper<Menu> queryWrapper = new LambdaQueryWrapper<Menu>();
		queryWrapper.eq(Menu::getValid, Constant.VALID_NORMAL);
		
		return menuMapper.selectList(queryWrapper);
	}

	@Override
	public void addMenu(Menu menu) {
		menu.setCreateBy(AppUtil.getCurrentUser().getUsername());
		menu.setCreateTime(DateUtil.now());
		
		menuMapper.insert(menu);
	}

	@Override
	public void updateMenu(Menu menu) {
		menu.setUpdateBy(AppUtil.getCurrentUser().getUsername());
		menu.setUpdateTime(DateUtil.now());
		
		menuMapper.updateById(menu);
	}

	@Override
	public void logicDeleteMenu(Long menuId) {
		Menu menu = new Menu();
		menu.setUpdateBy(AppUtil.getCurrentUser().getUsername());
		menu.setUpdateTime(DateUtil.now());
		menu.setValid(Constant.VALID_DELETE);
		List<Long> menuIds = new ArrayList<Long>();
		getAllChildMenuIds(menuIds,menuId);
		
		LambdaUpdateWrapper<Menu> updateWrapper = new LambdaUpdateWrapper<Menu>();
		updateWrapper.in(Menu::getMenuId, menuIds);
		menuMapper.update(menu, updateWrapper);
	}
	
	/**
	 * 获取到所有的子菜单ID（包括本身）
	 * @param menuIds
	 */
	private void getAllChildMenuIds(List<Long> menuIds,Long menuId) {
		//1.添加到集合中
		menuIds.add(menuId);
	
		//2.查询下级子节点
		LambdaQueryWrapper<Menu> queryWrapper = new LambdaQueryWrapper<Menu>();
		queryWrapper.eq(Menu::getParentId, menuId);
		queryWrapper.eq(Menu::getValid, Constant.VALID_NORMAL);
		List<Menu> childs = menuMapper.selectList(queryWrapper);
		if(CollectionUtil.noNullAndSizeGtZero(childs)) {
			childs.forEach(child -> {
				getAllChildMenuIds(menuIds,child.getMenuId());
			});
		}
	}
}
