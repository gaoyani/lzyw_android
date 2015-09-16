package com.luxs.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import com.luxs.config.G;

/**
 * 随机码工具
 * 
 * @author lyl
 * 
 */
public class CodeUtils {
	/**
	 * 保存随机码
	 * 
	 * @param context
	 *            上下文
	 * @param key
	 *            随机码
	 */
	public static void saveSuijiCode(Context context, String key) {
		if (context != null) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				"code", Context.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();
		editor.putString("mycode", key);
		Log.i("保存", "====数据保存=="+key);
		editor.commit();
		}
	}

	/**
	 * 获取随机码
	 * 
	 * @param context
	 *            上下文
	 */
	public static void getSuijiCode(Context context) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				"code", Context.MODE_PRIVATE);
		G.CURRENT_VALUE = sharedPreferences.getString("mycode", "");
	}

}
