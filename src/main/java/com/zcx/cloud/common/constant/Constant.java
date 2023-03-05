package com.zcx.cloud.common.constant;

/**
 * 静态常量
 * @author Administrator
 *
 */
public interface Constant {
	
	/**
	 * 逗号 ，
	 */
	public static final String COMMA = ",";
	/**
	 * 点号 .
	 */
	public static final String DOT = ".";
	
	/**
	 * 视图前缀
	 */
	public static final String VIEW_PREFIX = "cloud/views/";
	
	/**
	 * 视图后缀
	 */
	public static final String VIEW_SUFFIX = ".html";
	
	/**
	 * 删除标志，正常
	 */
	public static final String VALID_NORMAL = "1";
	
	/**
	 * 删除标志，删除
	 */
	public static final String VALID_DELETE = "0";
	
	
	/**
	 * 顶级菜单父级ID
	 */
	public static final Long TOP_MENU_PARENT_ID = 0L;
	
	
	/**
	 * 排序
	 */
	public static final String SORT_ASC = "asc";//升序
	public static final String SORT_DESC = "desc";//降序
	
	/**
	 * http与https协议前缀
	 */
	public static final String HTTP_PREFIX = "http://";
	public static final String HTTPS_PREFIX = "https://";
	
	/**
	 * 用户默认头像
	 */
	public static final String USER_DEFAULT_PHOTO = "/img/user_default.png";
	
	/**
	 * 东八区时间，默认@JsonFormat为格林威治时间，比中国时间早8小时
	 */
	public static final String GMT_8 = "GMT+8";
	
	
    /**
     * 作为在JobDetail中的Key
     */
	public static final String JOB_ENTITY_KEY = "job_entity";
	
	
	/**
	 * Job状态
	 * 停止
	 */
	public static final String JOB_STOP = "00";
	/**
	 * Job状态
	 * 开始
	 */
	public static final String JOB_START = "01";
	/**
	 * Job状态
	 * 暂停
	 */
	public static final String JOB_PAUSE = "02";
	
	
	/**
	 * 顶级目录名称
	 */
	public static final String ROOT_FOLDER_NAME = "/";
	/**
	 * 顶级目录父级ID
	 */
	public static final Long ROOT_FOLDER_PARENT_ID = 0l;
	
	/**
	 * 初始存储初始大小 500M
	 */
	public static final Long INIT_TOTAL_SIZE = 1024*1024*500l;
	/**
	 * 初始存储使用大小
	 */
	public static final Long INIT_USE_SIZE = 0l;
	
	/**
	 * 垃圾类型
	 */
	public static final String GARBAGE_FILE = "01";
	public static final String GARBAGE_FOLDER = "00";
	/**
	 * 垃圾有效期,7天
	 */
	public static final Integer GARBAGE_DEFAULT_ACTIVE = 7;
	
	
	/**
	 * 前端token key
	 */
	public static final String AUTHENTICATION_KEY = "Authorization";
	
	/**
	 * 客户端、服务器标识
	 */
	public static final String FLAG_CLIENT = "client";
	public static final String FLAG_SERVER = "server";
	
	
	/**
	 * 文件类型key字典前缀
	 */
	public static final String FILE_TYPE_PREFIX = "file_type_";

	/**
	 * 不识别的其他类型
	 */
	public static final String FILE_TYPE_OTHER = "other";

	/**
	 * 图片类型
	 */
	public static final String FILE_TYPE_IMG = "img";
	/**
	 * 文件图标存储前缀
	 */
	public static final String FILE_ICON_PREFIX = "/img/file_icon/";
	/**
	 * 文件图标后缀
	 */
	public static final String FILE_ICON_SUFFIX = ".png";
	/**
	 * 无法识别的文件图标
	 */
	public static final String FILE_ICON_OTHER = FILE_ICON_PREFIX + FILE_TYPE_OTHER + FILE_ICON_SUFFIX;
}
