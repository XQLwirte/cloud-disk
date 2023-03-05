package com.zcx.cloud.share.controller;


import java.util.List;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zcx.cloud.common.controller.BaseController;
import com.zcx.cloud.common.vo.PageInfo;
import com.zcx.cloud.common.vo.Result;
import com.zcx.cloud.common.vo.SortInfo;
import com.zcx.cloud.file.entity.File;
import com.zcx.cloud.share.entity.Share;
import com.zcx.cloud.share.service.IShareService;
import com.zcx.cloud.system.annotaion.SysLog;
import com.zcx.cloud.system.exception.BusException;

/**
 * <p>
 * 共享信息表 前端控制器
 * </p>
 *

 */
@RestController
@RequestMapping("/share")
public class ShareController extends BaseController {
	@Autowired
	private IShareService shareService;
	
	@RequiresPermissions("share:view")
	@GetMapping("getSharesPage")
	public Result getSharesPage(Share share,PageInfo<Share> pageInfo,SortInfo sortInfo) {
		try {
			Result result = shareService.getSharesPage(share, pageInfo, sortInfo);
			return result;
		}catch (Exception e) {
			e.printStackTrace();
			throw new BusException("获取共享分页信息失败");
		}
	}
	
	@RequiresPermissions("share:view")
	@PostMapping("getCurrentShares")
	public Result getCurrentShares(PageInfo<Share> pageInfo) {
		try {
			List<Share> shares = shareService.getCurrentShares(pageInfo);
			return new Result().success().data(shares).put("pageInfo", pageInfo);
		}catch (Exception e) {
			e.printStackTrace();
			throw new BusException("获取当前用户共享信息失败");
		}
	}
	
	@SysLog("添加共享信息")
	@RequiresPermissions("share:add")
	@PostMapping("add")
	public Result add(Share share) {
		try {
			Share data = shareService.saveShare(share);
			return new Result().success().data(data);
		}catch (Exception e) {
			e.printStackTrace();
			throw new BusException("添加共享信息失败");
		}
	}
	
	@SysLog("取消共享信息")
	@RequiresPermissions("share:delete")
	@GetMapping("cancelShare/{shareId}")
	public Result cancelShare(@PathVariable("shareId")Long shareId) {
		try {
			shareService.cancelShare(shareId);
			return new Result().success();
		}catch (Exception e) {
			e.printStackTrace();
			throw new BusException("取消共享信息失败");
		}
	}
	
	
	/**
	 * 共享文件接口，无需登录，无需权限控制
	 * @param share
	 * @return
	 */
	//不需要权限，否则游客无法访问
//	@RequiresPermissions("share:view")
	@PostMapping("checkShareCode")
	public Result checkShareCode(Share share) {
		try {
			boolean status = shareService.checkShareCode(share);
			if(status) {
				return new Result().success();
			}else {
				return new Result().error();
			}
		}catch (Exception e) {
			e.printStackTrace();
			throw new BusException("验证共享码失败");
		}
	}
	@GetMapping("getShareFile/{shareId}")
	public Result getShareFile(@PathVariable("shareId")Long shareId) {
		try {
			File file = shareService.getShareFile(shareId);
			return new Result().success().data(file);
		}catch (Exception e) {
			e.printStackTrace();
			throw new BusException("获取共享文件失败");
		}
	}
	@GetMapping("download/{shareId}")
	public Result download(@PathVariable("shareId")Long shareId) {
		try {
			shareService.download(shareId);
			return new Result().success();
		}catch (Exception e) {
			e.printStackTrace();
			throw new BusException("下载共享文件失败");
		}
	}
}
