package com.luxs.config;

import java.io.File;

import android.os.Environment;

public class G {
	/**
	 * 遥控接收器标识
	 */
	public final static String ACTION = "tv.luxs.action";

	/**
	 * 端口号
	 */
	public final static int PORT = 8888;

	/**
	 * 随机码
	 */
	public static String CURRENT_VALUE = "";

	/**
	 * SD
	 */
	public final static String SD_ROOT_PATH = Environment
			.getExternalStorageDirectory().getAbsolutePath();

	/**
	 * 应用文件夹
	 */
	public static final String SD_PATH = "luxs" + File.separator;

	/**
	 * 图片文件夹
	 */
	public static final String SD_IMG_PATH = SD_ROOT_PATH + SD_PATH + "img"
			+ File.separator;

	/**
	 * 图片网址
	 */
	public static final String URL_IMG = "http://115.28.10.189/Public/Img/";

	/**
	 * 单页数量
	 */
	public final static int PAGE_SIZE = 10;

	/**
	 * 乐推送：常用：每种文件显示数量
	 */
	public final static int COMMON_FILE_SIZE = 5;

	/**
	 * 键值：首页键
	 */
	public final static int VALUE_HOME = 3;

	/**
	 * 键值：数字0键
	 */
	public final static int VALUE_0 = 7;
	/**
	 * 键值：数字1键
	 */
	public final static int VALUE_1 = 8;
	/**
	 * 键值：数字2键
	 */
	public final static int VALUE_2 = 9;
	/**
	 * 键值：数字3键
	 */
	public final static int VALUE_3 = 10;
	/**
	 * 键值：数字4键
	 */
	public final static int VALUE_4 = 11;
	/**
	 * 键值：数字5键
	 */
	public final static int VALUE_5 = 12;
	/**
	 * 键值：数字6键
	 */
	public final static int VALUE_6 = 13;
	/**
	 * 键值：数字7键
	 */
	public final static int VALUE_7 = 14;
	/**
	 * 键值：数字8键
	 */
	public final static int VALUE_8 = 15;
	/**
	 * 键值：数字9键
	 */
	public final static int VALUE_9 = 16;

	/**
	 * 键值：向上键
	 */
	public final static int VALUE_UP = 19;

	/**
	 * 键值：向下键
	 */
	public final static int VALUE_BOTTOM = 20;

	/**
	 * 键值：向左键
	 */
	public final static int VALUE_LEFT = 21;

	/**
	 * 键值：向右键
	 */
	public final static int VALUE_RIGHT = 22;

	/**
	 * 键值：音量加键
	 */
	public final static int VALUE_VOLUME_UP = 24;

	/**
	 * 键值：竟是减键
	 */
	public final static int VALUE_VOLUME_DOWN = 25;

	/**
	 * 键值：确定键
	 */
	public final static int VALUE_OK = 66;

	/**
	 * 键值：返回键
	 */
	public final static int VALUE_BACK = 67;

	/**
	 * 键值：减-键
	 */
	public final static int VALUE_MINUS = 69;

	/**
	 * 键值：加+键
	 */
	public final static int VALUE_PLUS = 81;

	/**
	 * 键值：菜单键
	 */
	public final static int VALUE_MENU = 82;

	/**
	 * 键值：静音键
	 */
	public final static int VALUE_MUTE = 91;

	/**
	 * 键值：音乐键
	 */
	public final static int VALUE_MUSIC = 92;

	/**
	 * 键值：红色键
	 */
	public final static int VALUE_RED = 84;

	/**
	 * 键值：黄色键
	 */
	public final static int VALUE_YELLOW = 136;

	/**
	 * 键值：蓝色键
	 */
	public final static int VALUE_BLUE = 137;

	/**
	 * 键值：绿色键
	 */
	public final static int VALUE_GREEN = 138;

	/**
	 * 键值：鼠标点击
	 */
	public final static int VALUE_MOUSE_CLICK = 300;

	/**
	 * 遥控器当前页面（遥控器功能键页面 和 数字键页面）
	 */
	public static int ACTIVITY_CURRENT_RC = 1;

	/**
	 * 常用
	 */
	public final static int NOT_COMMON = -1;

	/**
	 * 常用
	 */
	public final static int COMMON = 0;

	/**
	 * 图片
	 */
	public final static int IMAGE = 1;

	/**
	 * 音乐
	 */
	public final static int MUSIC = 2;

	/**
	 * 视频
	 */
	public final static int VIDEO = 3;

	/**
	 * 云资源
	 */
	public final static int CLOUD = 4;

	/**
	 * 文件夹
	 */
	public final static int FOLDER = 5;

	/**
	 * 设置
	 */
	public final static int SETTING = 0;

	/**
	 * 乐遥控设置
	 */
	public final static int SETTING_RC = 1;

	/**
	 * 乐推送设置
	 */
	public final static int SETTING_PUSH = 2;

	/**
	 * 乐摇摇设置
	 */
	public final static int SETTING_SHAKE = 3;

	/**
	 * 乐摇摇退出
	 */
	public final static int SETTING_LOGOUT = 4;
	/**
	 * 关于
	 */
	public final static int SETTING_ABOUT = 10;

	/**
	 * 乐扫扫
	 */
	public final static int SCAN = 1;

	/**
	 * 乐摇摇
	 */
	public final static int SHAKE = 2;

	/**
	 * 乐游戏
	 */
	public final static int GAME = 3;

	/**
	 * 乐理财
	 */
	public final static int FINANCE = 4;

	/**
	 * 加载成功
	 */
	public final static int LOAD_SUCCESS = 1;

	/**
	 * 加载失败
	 */
	public final static int LOAD_FAILED = 2;

	/**
	 * 加载中
	 */
	public final static int LOAD_ING = 3;

	/**
	 * 链接
	 */
	public final static int CONNECT = 1;

	/**
	 * 链接
	 */
	public static boolean CONNECT_STATUS = false;

	/**
	 * 发送指令
	 */
	public final static int SEND = 2;

	/**
	 * 发送指令成功
	 */
	public final static int SEND_SUCCESS = 1;

	/**
	 * 发送指令失败
	 */
	public final static int SEND_FAILED = 0;

	public static final long SQL_INSERT_SUCCESS = 1L;
	public static final int SQL_UPDATE_SUCCESS = 1;
	public static final int SQL_ERROR = 0;

	/**************** 乐会员 ****************/
	/**
	 * 优惠ID
	 */
	public static final int YOUHUI_TAG = 0;
	/**
	 * 会员中心ID
	 */
	public static final int VIP_CENTER_TAG = 1;
	/**
	 * 乐支付ID
	 */
	public static final int LE_PAY_TAG = 2;
	/**
	 * 返回主页动作
	 */
	public static final String SEND_BACK_HOME = "ACTION.BING.LUSXSHARE";
	/**
	 * 摇一摇
	 */
	public static final String ACTION_YAOYIYAO = "ACTION.BING.YAO.PUSH";
	/**
	 * 分享
	 */
	public static final String ACTION_SHARE_ = "ACIONT_BING_SHARE";
	/**
	 * WIFI 名称
	 */
	public static String WIFI_SSID = "";
	/**
	 * WIFI密码
	 */
	public static String WIFI_PASSWORD = "";
	/**
	 * 下载地址
	 */
	public static final String DOWNLOAD_URL = "http://s.luxs.tv:8070";
	/**
	 * 打开GPS
	 */
	public static final String ACTION_OPEN_GPS="ACTION.LUXSHARE.OPENGPS";
	/**
	 * 连接提示
	 */
	public static final String ACTION_CONNECT="com.luxshare.connect";
	/**
	 * 密码错误动作
	 */
	public static final String ACTION_ERRO_PASSWORD="action.luxshare.erro.password";
	
}
