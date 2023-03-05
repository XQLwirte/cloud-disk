package com.zcx.cloud.file.service.impl;

import com.zcx.cloud.common.constant.Constant;
import com.zcx.cloud.common.helper.DictHelper;
import com.zcx.cloud.common.vo.TimeData;
import com.zcx.cloud.dict.entity.DictKey;
import com.zcx.cloud.dict.entity.DictValue;
import com.zcx.cloud.file.entity.File;
import com.zcx.cloud.file.mapper.FileMapper;
import com.zcx.cloud.file.service.IFileService;
import com.zcx.cloud.folder.entity.Folder;
import com.zcx.cloud.folder.entity.FolderUser;
import com.zcx.cloud.folder.mapper.FolderUserMapper;
import com.zcx.cloud.folder.service.IFolderService;
import com.zcx.cloud.garbage.entity.Garbage;
import com.zcx.cloud.garbage.entity.GarbageUser;
import com.zcx.cloud.garbage.mapper.GarbageMapper;
import com.zcx.cloud.garbage.mapper.GarbageUserMapper;
import com.zcx.cloud.storage.entity.Storage;
import com.zcx.cloud.storage.service.IStorageService;
import com.zcx.cloud.system.exception.OutOfStorageException;
import com.zcx.cloud.system.fastdfs.helper.FDFSHelper;
import com.zcx.cloud.util.AppUtil;
import com.zcx.cloud.util.CollectionUtil;
import com.zcx.cloud.util.DateUtil;
import com.zcx.cloud.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 * 文件表 服务实现类
 * </p>
 *

 */
@Service
public class FileServiceImpl extends ServiceImpl<FileMapper, File> implements IFileService {
	@Autowired
	private FolderUserMapper folderUserMapper;
	@Autowired
	private FileMapper fileMapper;
	@Autowired
	private IStorageService storageService;
	@Autowired
	private FDFSHelper fdfsHelper;
	@Autowired
	private DictHelper dictHelper;
	@Autowired
	private GarbageMapper garbageMapper;
	@Autowired
	private GarbageUserMapper garbageUserMapper;
	@Autowired
	private IFolderService folderService;
	
	
	@Override
	public List<File> getChildFiles(Long currentId) {
		//当前文件夹ID为负数，则查询当前用户根目录
		if(Objects.isNull(currentId)||currentId<=0) {
			LambdaQueryWrapper<FolderUser> queryWrapper = new LambdaQueryWrapper<FolderUser>();
			queryWrapper.eq(FolderUser::getUserId, AppUtil.getCurrentUser().getUserId());
			FolderUser folderUser = folderUserMapper.selectOne(queryWrapper);
			currentId = folderUser.getFolderId();
		}
		//获取到挂载的文件
		LambdaQueryWrapper<File> queryWrapper = new LambdaQueryWrapper<File>();
		queryWrapper.eq(File::getFolderId, currentId);
		queryWrapper.eq(File::getValid, Constant.VALID_NORMAL);
		queryWrapper.orderByAsc(File::getFileName);
		List<File> files = fileMapper.selectList(queryWrapper);
		
		return files;
	}

	@Override
	public void upload(MultipartFile file, Long folderId) {
		//1.查询空间是否充足
		Storage currentStorage = storageService.getCurrentStorage();
		Long fileSize = file.getSize();
		Long totalSize = currentStorage.getCapacityTotal();
		Long useSize = currentStorage.getCapacityUse();
		if((useSize+fileSize)>totalSize)
			throw new OutOfStorageException("存储空间不足");
		
		//2.文件上传FastDFS服务器
		String fileUrl = fdfsHelper.uploadFile(file);
		if(Objects.isNull(fileUrl))
			throw new RuntimeException("文件上传失败");
		
		//3.图标处理，文件类型处理
		String fileIcon = null;
		String fileType = null;
		//查询所有文件类型数据字典
		List<DictKey> keys = dictHelper.keys(Constant.FILE_TYPE_PREFIX);
		if(CollectionUtil.noNullAndSizeGtZero(keys)) {
			Iterator<DictKey> keyIterator = keys.iterator();
			while(fileIcon==null&&keyIterator.hasNext()) {
				//查询文件类型下具体文件后缀
				DictKey key = keyIterator.next();
				List<DictValue> values = dictHelper.getValues(key.getKeyCode());
				if(CollectionUtil.noNullAndSizeGtZero(values)) {
					Iterator<DictValue> valueIterator = values.iterator();
					while(fileIcon==null&&valueIterator.hasNext()) {
						DictValue value = valueIterator.next();
						//获取文件后缀
						String extension = StringUtil.getExtension(file.getOriginalFilename());
						//比对
						if(extension.equalsIgnoreCase(value.getValueCode())) {
							fileIcon = Constant.FILE_ICON_PREFIX + value.getValueCode() + Constant.FILE_ICON_SUFFIX;
							fileType = key.getKeyCode().substring(Constant.FILE_TYPE_PREFIX.length());
						}
					}
				}
			}
		}
		//如果没有找到匹配的类型，则直接其他类型
		if(Objects.isNull(fileIcon)&&Objects.isNull(fileType)) {
			fileIcon = Constant.FILE_ICON_OTHER;
			fileType = Constant.FILE_TYPE_OTHER;
		}
		//如果是图片类型，则直接使用图片作为图标
		if(fileType.equals(Constant.FILE_TYPE_IMG)) {
			fileIcon = fileUrl;
		}
		
		//4.插入文件记录
		File fileDB = new File();
		fileDB.setFileName(StringUtil.excludeExtension(file.getOriginalFilename()));
		fileDB.setFileType(fileType);
		fileDB.setFileIcon(fileIcon);
		fileDB.setFileUrl(fileUrl);
		fileDB.setFileSize(file.getSize());
		fileDB.setFolderId(folderId);
		fileDB.setCreateBy(AppUtil.getCurrentUser().getUsername());
		fileDB.setCreateTime(DateUtil.now());
		fileMapper.insert(fileDB);
		
		//5.更新存储容量信息
		currentStorage.setCapacityUse(currentStorage.getCapacityUse()+fileSize);
		currentStorage.setUpdateBy(AppUtil.getCurrentUser().getUsername());
		currentStorage.setUpdateTime(DateUtil.now());
		storageService.updateById(currentStorage);
	}

	@Override
	public void rename(Long fileId, String newName) {
		File file = new File();
		file.setFileId(fileId);
		file.setFileName(newName);
		file.setUpdateBy(AppUtil.getCurrentUser().getUsername());
		file.setUpdateTime(DateUtil.now());
		
		fileMapper.updateById(file);
	}

	@Override
	public void moveGarbage(Long fileId) {
		//1.修改文件可用标志
		File file = new File();
		file.setFileId(fileId);
		file.setUpdateBy(AppUtil.getCurrentUser().getUsername());
		file.setUpdateTime(DateUtil.now());
		file.setValid(Constant.VALID_DELETE);
		fileMapper.updateById(file);
		
		//2.添加文件对应垃圾信息
		Garbage garbage = new Garbage();
		garbage.setResourceId(fileId);
		garbage.setResourceType(Constant.GARBAGE_FILE);
		garbage.setActive(Constant.GARBAGE_DEFAULT_ACTIVE);
		garbage.setCreateBy(AppUtil.getCurrentUser().getUsername());
		garbage.setCreateTime(DateUtil.now());
		garbageMapper.insert(garbage);
		
		//3.添加垃圾与用户的关联信息
		GarbageUser garbageUser = new GarbageUser();
		garbageUser.setUserId(AppUtil.getCurrentUser().getUserId());
		garbageUser.setGarbageId(garbage.getGarbageId());
		garbageUserMapper.insert(garbageUser);
	}

	@Override
	public void moveGarbageBat(List<Long> fileIds) {
		//1.修改文件可用标志
		File file = new File();
		file.setUpdateBy(AppUtil.getCurrentUser().getUsername());
		file.setUpdateTime(DateUtil.now());
		file.setValid(Constant.VALID_DELETE);
		
		LambdaUpdateWrapper<File> uwFile = new LambdaUpdateWrapper<File>();
		uwFile.in(File::getFileId, fileIds);
		
		fileMapper.update(file, uwFile);
		
		//2.添加文件夹对应垃圾信息
		List<Garbage> garbages = new ArrayList<Garbage>();
		fileIds.forEach(fileId -> {
			Garbage garbage = new Garbage();
			garbage.setResourceId(fileId);
			garbage.setResourceType(Constant.GARBAGE_FILE);
			garbage.setActive(Constant.GARBAGE_DEFAULT_ACTIVE);
			garbage.setCreateBy(AppUtil.getCurrentUser().getUsername());
			garbage.setCreateTime(DateUtil.now());
			garbageMapper.insert(garbage);
			
			garbages.add(garbage);
		});
		
		
		//3.添加垃圾与用户的关联信息
		garbages.forEach(garbage -> {
			GarbageUser garbageUser = new GarbageUser();
			garbageUser.setUserId(AppUtil.getCurrentUser().getUserId());
			garbageUser.setGarbageId(garbage.getGarbageId());
			garbageUserMapper.insert(garbageUser);
		});
	}

	@Override
	public void move(Long fileId, Long targetId) {
		File file = new File();
		file.setFileId(fileId);
		file.setFolderId(targetId);
		file.setUpdateBy(AppUtil.getCurrentUser().getUsername());
		file.setUpdateTime(DateUtil.now());
		
		fileMapper.updateById(file);
	}

	@Override
	public List<TimeData<File>> getTimeDataByType(String fileType) {
		List<TimeData<File>> result = new ArrayList<TimeData<File>>();
		//1.查询当前用户的所有文件夹树
		List<Folder> folderTree = folderService.getCurrentFolderTree();
		
		//2.将文件夹树的ID收集到集合中
		List<Long> folderIds = parseTreeToIds(folderTree);
		
		//3.从数据库中排序查询挂载在文件夹下的所有同类型的文件信息
		LambdaQueryWrapper<File> queryWrapper = new LambdaQueryWrapper<File>();
		queryWrapper.in(File::getFolderId, folderIds);
		queryWrapper.eq(File::getFileType, fileType);
		queryWrapper.eq(File::getValid, Constant.VALID_NORMAL);
		queryWrapper.orderByDesc(File::getCreateBy);
		List<File> files = fileMapper.selectList(queryWrapper);
		
		//4.将文件集合解析到时间数据集合中
		Map<String, List<File>> collect = files.stream()
				.collect(Collectors.groupingBy(file -> DateUtil.toString(file.getCreateTime(), DateUtil.YYYY_MM_DD)));
		collect.forEach((key,fs) -> {
			TimeData<File> td = new TimeData<File>();
			td.setTime(DateUtil.toDate(key, DateUtil.YYYY_MM_DD));
			td.setData(fs);
			result.add(td);
		});
		result.sort((r1,r2) -> {
			return r2.getTime().compareTo(r1.getTime());
		});
		
		return result;
	}
	
	/**
	 * 将目录树中的
	 * @param tree
	 * @return
	 */
	private List<Long> parseTreeToIds(List<Folder> folderTree){
		List<Long> result = new ArrayList<Long>();
		if(CollectionUtil.noNullAndSizeGtZero(folderTree)) {
			folderTree.forEach(folder -> {
				result.add(folder.getFolderId());
				if(CollectionUtil.noNullAndSizeGtZero(folder.getFolders())) {
					result.addAll(this.parseTreeToIds(folder.getFolders()));
				}
			});
		}
		return result;
	}

	@Override
	public List<File> searchByName(String fileName) {
		//1.查询当前用户的所有文件夹树
		List<Folder> folderTree = folderService.getCurrentFolderTree();
		
		//2.将文件夹树的ID收集到集合中
		List<Long> folderIds = parseTreeToIds(folderTree);
		
		//3.查询文件夹下名称匹配的所有文件信息
		LambdaQueryWrapper<File> queryWrapper = new LambdaQueryWrapper<File>();
		queryWrapper.in(File::getFolderId, folderIds);
		queryWrapper.like(File::getFileName, fileName);
		queryWrapper.orderByDesc(File::getCreateTime);
		queryWrapper.eq(File::getValid, Constant.VALID_NORMAL);
		List<File> files = fileMapper.selectList(queryWrapper);
		
		return files;
	}

}
