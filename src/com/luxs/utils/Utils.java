package com.luxs.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.luxs.config.G;

public class Utils {
	private final static String TAG = "luxsrc";

	/**
	 * 打印LOG
	 * 
	 * @param String
	 *            LOG消息
	 */
	public static void log(String msg) {
		Log.d(TAG, msg);
	}

	/**
	 * 弹出提示信息
	 * 
	 * @param Activity
	 *            当前活动
	 * @param String
	 *            提示信息内容
	 */
	public static void toast(Activity activity, String msg) {
		Toast.makeText(activity, msg, Toast.LENGTH_LONG).show();
	}

	/**
	 * 跳转到
	 * 
	 * @param Activity
	 *            当前活动
	 * @param String
	 *            跳转到的活动
	 */
	public static void toActivity(Activity activity, Class<?> cls) {
		Intent intent = new Intent();
		intent.setClass(activity, cls);
		activity.startActivity(intent);
	}

	/**
	 * 输入法显示或隐藏
	 * 
	 * @param context
	 */
	public static void showHideInputMethod(Context context) {
		// 隐藏输入法
		InputMethodManager imm = (InputMethodManager) context
				.getApplicationContext().getSystemService(
						Context.INPUT_METHOD_SERVICE);
		// 显示或者隐藏输入法
		imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
	}

	/**
	 * 存储键值对:整型
	 */
	public static void setKeyValueInt(Activity activity, String name,
			String key, int value) {
		SharedPreferences sharedPref = activity.getSharedPreferences(name,
				Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putInt(key, value);
		editor.commit();
	}

	/**
	 * 获取键值对：整型
	 */
	public static int getKeyValueInt(Activity activity, String name,
			String key, int value) {
		SharedPreferences sharedPref = activity.getSharedPreferences(name,
				Activity.MODE_PRIVATE);
		return sharedPref.getInt(key, value);
	}

	/**
	 * 存储键值对:字符串型
	 */
	public static void setKeyValueString(Activity activity, String name,
			String key, String value) {
		SharedPreferences sharedPref = activity.getSharedPreferences(name,
				Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putString(key, value);
		editor.commit();
	}

	/**
	 * 获取键值对：字符串型
	 */
	public static String getKeyValueString(Activity activity, String name,
			String key, String value) {
		SharedPreferences sharedPref = activity.getSharedPreferences(name,
				Activity.MODE_PRIVATE);
		return sharedPref.getString(key, value);
	}

	/**
	 * 存储键值对:布尔型
	 */
	public static void setKeyValueBoolean(Activity activity, String name,
			String key, boolean value) {
		SharedPreferences sharedPref = activity.getSharedPreferences(name,
				Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}

	/**
	 * 获取键值对：布尔型
	 */
	public static boolean getKeyValueBoolean(Activity activity, String name,
			String key, boolean value) {
		SharedPreferences sharedPref = activity.getSharedPreferences(name,
				Activity.MODE_PRIVATE);
		return sharedPref.getBoolean(key, value);
	}

	/**
	 * * @param time format the time.
	 * 
	 * @return
	 */
	public static String convertToTime(int time) {
		int h = time / 3600;
		int m = time % 3600 / 60;
		int s = time % 60;
		String result = String.format("%02d:%02d:%02d", h, m, s);
		return result;
	}

	/**
	 * 返回主页
	 * 
	 * @param context
	 *            上下文
	 */
	public static void send_back(Context context) {
		Intent intent = new Intent();
		intent.setAction(G.SEND_BACK_HOME);
		context.sendBroadcast(intent);
	}

	/**
	 * 保存是否弹出打开GPS对话框
	 * 
	 * @param context
	 *            上下文
	 * @param isopen
	 *            开启标志
	 */
	public static void saveGpsPreferences(Context context, boolean isopen) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				"gps", Context.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();
		editor.putBoolean("isopen", isopen);
		editor.commit();
	}

	/**
	 * 是否打开GPS对话框
	 * 
	 * @param context
	 *            上下文
	 * @return 开启标志
	 */
	public static boolean getGpsPreferences(Context context) {
		return context.getSharedPreferences("gps", Context.MODE_PRIVATE)
				.getBoolean("isopen", false);
	}

	/**
	 * 保存是否弹出打开Ip对话框
	 * 
	 * @param context
	 *            上下文
	 * @param isopen
	 *            开启标志
	 */
	public static void saveIpconPreferences(Context context, boolean iscon) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				"ip", Context.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();
		editor.putBoolean("iscon", iscon);
		editor.commit();
	}

	/**
	 * 是否打开ip对话框
	 * 
	 * @param context
	 *            上下文
	 * @return 开启标志
	 */
	public static boolean getIpconPreferences(Context context) {
		return context.getSharedPreferences("ip", Context.MODE_PRIVATE)
				.getBoolean("iscon", false);
	}

	/**
	 * 保存是否弹出打开分享对话框
	 * 
	 * @param context
	 *            上下文
	 * @param isopen
	 *            开启标志
	 */
	public static void saveSharePreferences(Context context, boolean isshare) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				"share", Context.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();
		editor.putBoolean("isshare", isshare);
		editor.commit();
	}

	/**
	 * 是否打开分享对话框
	 * 
	 * @param context
	 *            上下文
	 * @return 开启标志
	 */
	public static boolean getSharePreferences(Context context) {
		return context.getSharedPreferences("share", Context.MODE_PRIVATE)
				.getBoolean("isshare", false);
	}

}
