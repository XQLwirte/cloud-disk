package com.zcx.cloud.garbage.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcx.cloud.common.constant.Constant;
import com.zcx.cloud.common.vo.PageInfo;
import com.zcx.cloud.common.vo.Result;
import com.zcx.cloud.common.vo.SortInfo;
import com.zcx.cloud.common.vo.TimeData;
import com.zcx.cloud.file.entity.File;
import com.zcx.cloud.file.mapper.FileMapper;
import com.zcx.cloud.folder.entity.Folder;
import com.zcx.cloud.folder.mapper.FolderMapper;
import com.zcx.cloud.garbage.entity.Garbage;
import com.zcx.cloud.garbage.entity.GarbageUser;
import com.zcx.cloud.garbage.mapper.GarbageMapper;
import com.zcx.cloud.garbage.mapper.GarbageUserMapper;
import com.zcx.cloud.garbage.service.IGarbageService;
import com.zcx.cloud.user.entity.User;
import com.zcx.cloud.user.mapper.UserMapper;
import com.zcx.cloud.util.AppUtil;
import com.zcx.cloud.util.CollectionUtil;
import com.zcx.cloud.util.DateUtil;

/**
 * <p>
 * 垃圾箱 服务实现类
 * </p>
 *
 
 */
@Service
public class GarbageServiceImpl extends ServiceImpl<GarbageMapper, Garbage> implements IGarbageService {
	@Autowired
	private GarbageMapper garbageMapper;
	@Autowired
	private FileMapper fileMapper;
	@Autowired
	private FolderMapper folderMapper;
	@Autowired
	private GarbageUserMapper garbageUserMapper;
	@Autowired
	private UserMapper userMapper;
	
	@Override
	public Result getGarbagesPage(Garbage garbage, PageInfo<Garbage> pageInfo, SortInfo sortInfo) {
		//排序信息
		Page<Garbage> page = pageInfo.getPage();
		sortInfo.toPageSort(page);
		
		//查询数据
		Page<Garbage> selectPage = garbageMapper.selectGarbagePage(page, garbage);
		long count = selectPage.getTotal();
		List<Garbage> data = selectPage.getRecords();
		//按不同类型查询文件名称
		data.forEach(g -> {
			if(Constant.GARBAGE_FILE.equals(g.getResourceType())) {//文件
				File file = fileMapper.selectById(g.getResourceId());
				g.setResourceName(Objects.nonNull(file)?file.getFileName():null);
				g.setResourceUrl(Objects.nonNull(file)?file.getFileUrl():null);
			} else if(Constant.GARBAGE_FOLDER.equals(g.getResourceType())) {
				Folder folder = folderMapper.selectById(g.getResourceId());
				g.setResourceName(Objects.nonNull(folder)?folder.getFolderName():null);
			}
		});
		return new Result().success().count(count).data(data);
	}

	@Override
	public Garbage getGarbageById(Long garbageId) {
		//1.查询垃圾信息
		Garbage garbage = garbageMapper.selectById(garbageId);

		//2.查询资源信息
		if(Constant.GARBAGE_FILE.equals(garbage.getResourceType())) {//文件
			LambdaQueryWrapper<File> qwFile = new LambdaQueryWrapper<File>();
			qwFile.eq(File::getFileId, garbage.getResourceId());
			File file = fileMapper.selectOne(qwFile);
			garbage.setResourceName(Objects.nonNull(file)?file.getFileName():null);
			garbage.setResourceUrl(Objects.nonNull(file)?file.getFileUrl():null);
		} else if(Constant.GARBAGE_FOLDER.equals(garbage.getResourceType())) {//文件夹
			LambdaQueryWrapper<Folder> qwFolder = new LambdaQueryWrapper<Folder>();
			qwFolder.eq(Folder::getFolderId, garbage.getResourceId());
			Folder folder = folderMapper.selectOne(qwFolder);
			garbage.setResourceName(Objects.nonNull(folder)?folder.getFolderName():null);
		}
		
		//3.查询用户信息
		LambdaQueryWrapper<GarbageUser> queryWrapper = new LambdaQueryWrapper<GarbageUser>();
		queryWrapper.eq(GarbageUser::getGarbageId, garbageId);
		GarbageUser garbageUser = garbageUserMapper.selectOne(queryWrapper);
		if(Objects.nonNull(garbageUser)) {
			LambdaQueryWrapper<User> qwU = new LambdaQueryWrapper<User>();
			qwU.eq(User::getUserId, garbageUser.getUserId());
			qwU.eq(User::getValid, Constant.VALID_NORMAL);
			User user = userMapper.selectOne(qwU);
			garbage.setUsername(Objects.nonNull(user)?user.getUsername():null);
		}
		
		return garbage;
	}

	@Override
	public void garbageResume(Long garbageId) {
		//1.恢复文件可用性
		Garbage garbage = garbageMapper.selectById(garbageId);
		if(Constant.GARBAGE_FILE.equals(garbage.getResourceType())) {//文件
			File file = new File();
			file.setFileId(garbage.getResourceId());
			file.setUpdateBy(AppUtil.getCurrentUser().getUsername());
			file.setUpdateTime(DateUtil.now());
			file.setValid(Constant.VALID_NORMAL);
			fileMapper.updateById(file);
		} else if(Constant.GARBAGE_FOLDER.equals(garbage.getResourceType())) {//目录
			Folder folder = new Folder();
			folder.setFolderId(garbage.getResourceId());
			folder.setUpdateBy(AppUtil.getCurrentUser().getUsername());
			folder.setUpdateTime(DateUtil.now());
			folder.setValid(Constant.VALID_NORMAL);
			folderMapper.updateById(folder);
		}
		
		//2.删除垃圾信息
		garbageMapper.deleteById(garbageId);
		
		//3.删除垃圾用户关联信息
		LambdaUpdateWrapper<GarbageUser> updateWrapper = new LambdaUpdateWrapper<GarbageUser>();
		updateWrapper.eq(GarbageUser::getGarbageId, garbageId);
		garbageUserMapper.delete(updateWrapper);
	}

	@Override
	public void logicDelete(Long garbageId) {
		Garbage garbage = new Garbage();
		garbage.setGarbageId(garbageId);
		garbage.setUpdateBy(AppUtil.getCurrentUser().getUsername());
		garbage.setUpdateTime(DateUtil.now());
		garbage.setValid(Constant.VALID_DELETE);
		
		garbageMapper.updateById(garbage);
	}

	@Override
	public void logicDeleteBatch(List<Long> garbageIds) {
		Garbage garbage = new Garbage();
		garbage.setUpdateBy(AppUtil.getCurrentUser().getUsername());
		garbage.setUpdateTime(DateUtil.now());
		garbage.setValid(Constant.VALID_DELETE);
		
		LambdaUpdateWrapper<Garbage> updateWrapper = new LambdaUpdateWrapper<Garbage>();
		updateWrapper.in(Garbage::getGarbageId, garbageIds);
		
		garbageMapper.update(garbage, updateWrapper);
	}

	@Override
	public List<TimeData<Garbage>> getCurrentGarbagesTimeData() {
		List<TimeData<Garbage>> timeDatas = new ArrayList<TimeData<Garbage>>();
		//1.获取当前用户关联垃圾ID集合
		User currentUser = AppUtil.getCurrentUser();
		LambdaQueryWrapper<GarbageUser> qwGU = new LambdaQueryWrapper<GarbageUser>();
		qwGU.eq(GarbageUser::getUserId, currentUser.getUserId());
		List<GarbageUser> garbageUsers = garbageUserMapper.selectList(qwGU);
		List<Long> garbageIds = new ArrayList<Long>();
		if(CollectionUtil.isNullOrSizeLeZero(garbageUsers))
			return timeDatas;
		garbageUsers.forEach(garbageUser -> {
			garbageIds.add(garbageUser.getGarbageId());
		});
		
		//2.按时间降序收集ID集合的垃圾信息
		LambdaQueryWrapper<Garbage> qwG = new LambdaQueryWrapper<Garbage>();
		qwG.in(Garbage::getGarbageId, garbageIds);
		qwG.eq(Garbage::getValid, Constant.VALID_NORMAL);
		qwG.orderByDesc(Garbage::getCreateTime);
		List<Garbage> garbages = garbageMapper.selectList(qwG);
		if(CollectionUtil.isNullOrSizeLeZero(garbages))
			return timeDatas;
		
		//3.挂载垃圾对应的资源信息
		garbages.forEach(garbage -> {
			if(Constant.GARBAGE_FILE.equals(garbage.getResourceType())) {//文件
				LambdaQueryWrapper<File> qwFile = new LambdaQueryWrapper<File>();
				qwFile.eq(File::getFileId, garbage.getResourceId());
				File file = fileMapper.selectOne(qwFile);
				garbage.setFile(file);
			} else if(Constant.GARBAGE_FOLDER.equals(garbage.getResourceType())) {//文件夹
				LambdaQueryWrapper<Folder> qwFolder = new LambdaQueryWrapper<Folder>();
				qwFolder.eq(Folder::getFolderId, garbage.getResourceId());
				Folder folder = folderMapper.selectOne(qwFolder);
				garbage.setFolder(folder);
			}
		});
		
		//4.将垃圾信息格式化到时间集合对象中
		Map<String, List<Garbage>> collect = garbages.stream()
				.collect(Collectors.groupingBy(garbage -> DateUtil.toString(garbage.getCreateTime(), DateUtil.YYYY_MM_DD)));
		collect.forEach((key, val) -> {
			TimeData<Garbage> timeData = new TimeData<Garbage>();
			timeData.setTime(DateUtil.toDate(key, DateUtil.YYYY_MM_DD));
			timeData.setData(val);
			timeDatas.add(timeData);
		});
		timeDatas.sort((r1,r2) -> {
			return r2.getTime().compareTo(r1.getTime());
		});
		
		return timeDatas;
	}

}
