package com.luxs.setting;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;

import com.luxs.utils.Utils;

public class SettingManager {
	final String KEY_NAME = "assistant";
	final String VALUE_HOST = "host";
	final String VALUE_HOST_STATUS = "hoststatus";
	final String VALUE_SC = "sc";
	final String VALUE_VIB = "vib";
	final String VALUE_SEN = "sen";
	final String VALUE_PUSH_COMMON = "pushcommon";
	final int VALUE_SC_DEFAULT = 1;
	final int VALUE_VIB_DEFAULT = 2;
	final int VALUE_SEN_DEFAULT = 10;
	final int VALUE_PUSH_COMMON_DEFAULT = 5;
	final boolean VALUE_HOST_STATUS_DEFAULT = false;
	
	/**
	 * 执行按键振动
	 * @param activity
	 * @return
	 */
	public void vibrate(Activity activity){
		Vibrator v = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE); 
		long milliseconds = 100 * getVibration(activity); 
		if(milliseconds != 0) v.vibrate(milliseconds); 
	}
	
	/**
	 * 点击
	 * @param activity
	 * @return
	 */
	public void click(Activity activity,Handler handler){
		new VibrateThread(activity,handler).start();
	}
	
	/**
	 * 振动线程
	 */
	class VibrateThread extends Thread{
		Activity activity;
		Handler handler;
		
		public VibrateThread(Activity activity,Handler handler) {
			this.activity = activity;
			this.handler = handler;
		}
		
		public void run() {
			try {
				int sens = getSensitivity(activity);
				Utils.log("sens：" + sens);
				long time = 100 * (10 - sens);	
				Utils.log("time：" + time);
				if(time != 0) this.sleep(time);
				Message msg = handler.obtainMessage();
				msg.sendToTarget();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		};
	}
	
	/**
	 * 获取默认机顶盒IP
	 * @param activity
	 * @return
	 */
	public String getHost(Activity activity){
		return Utils.getKeyValueString(activity,KEY_NAME , VALUE_HOST, null);
	}
	
	/**
	 * 设置默认机顶盒IP
	 * @param activity
	 * @return
	 */
	public void setHost(Activity activity,String value){
		Utils.setKeyValueString(activity,KEY_NAME , VALUE_HOST, value);
	}
	
	/**
	 * 获取默认机顶盒连接状态
	 * @param activity
	 * @return
	 */
	public boolean getHostStatus(Activity activity){
		return Utils.getKeyValueBoolean(activity,KEY_NAME , VALUE_HOST_STATUS, VALUE_HOST_STATUS_DEFAULT);
	}
	
	/**
	 * 设置默认机顶盒连接状态
	 * @param activity
	 * @return
	 */
	public void setHostStatus(Activity activity,boolean value){
		Utils.setKeyValueBoolean(activity,KEY_NAME , VALUE_HOST_STATUS, value);
	}
	
	/**
	 * 获取机顶盒显示数量
	 * @param activity
	 * @return
	 */
	public int getServerCount(Activity activity){
		return Utils.getKeyValueInt(activity,KEY_NAME , VALUE_SC, VALUE_SC_DEFAULT);
	}

	/**
	 * 设置机顶盒显示数量
	 * @param activity
	 * @return
	 */
	public void setServerCount(Activity activity,int value){
		Utils.setKeyValueInt(activity,KEY_NAME , VALUE_SC, value);
	}
	
	/**
	 * 获取按钮振动强度值
	 * @param activity
	 * @return
	 */
	public int getVibration(Activity activity){
		return Utils.getKeyValueInt(activity,KEY_NAME , VALUE_VIB, VALUE_VIB_DEFAULT);
	}
	
	/**
	 * 设置按钮振动强度值
	 * @param activity
	 * @return
	 */
	public void setVibration(Activity activity,int value){
		Utils.setKeyValueInt(activity,KEY_NAME , VALUE_VIB, value);
	}
	
	/**
	 * 获取按钮灵敏度值
	 * @param activity
	 * @return
	 */
	public int getSensitivity(Activity activity){
		return Utils.getKeyValueInt(activity,KEY_NAME , VALUE_SEN, VALUE_SEN_DEFAULT);
	}

	/**
	 * 设置按钮灵敏度值
	 * @param activity
	 * @return
	 */
	public void setSensitivity(Activity activity,int value){
		Utils.setKeyValueInt(activity,KEY_NAME , VALUE_SEN, value);
	}
	
	/**
	 * 获取推送常用显示数量
	 * @param activity
	 * @return
	 */
	public int getPushCommonCount(Activity activity){
		return Utils.getKeyValueInt(activity,KEY_NAME , VALUE_PUSH_COMMON, VALUE_PUSH_COMMON_DEFAULT);
	}

	/**
	 * 设置推送常用显示数量
	 * @param activity
	 * @return
	 */
	public void setPushCommonCount(Activity activity,int value){
		Utils.setKeyValueInt(activity,KEY_NAME , VALUE_PUSH_COMMON, value);
	}
	
}
