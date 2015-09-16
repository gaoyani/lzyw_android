package com.huiwei.roomreservation.common;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;

import com.huiwei.commonlib.AppVersionUpdate;
import com.huiwei.commonlib.CommonFunction;
import com.huiwei.roomreservationlib.data.Constant;
import com.huiwei.roomreservationlib.data.Data;
import com.huiwei.roomreservationlib.data.UrlConstant;
import com.huiwei.roomreservationlib.task.main.ServerVersionTask;

public class VersionUpdate {
	
	private Context mContext;
	private Handler appUpdateHandler;
	private String localVersion;
	private String serverUrl;
	private String serverVersion;
	private String serverDes;
	private AppVersionUpdate versionUpdate;
	
	public VersionUpdate(Context context, Handler handler) {
		mContext = context;
		appUpdateHandler = handler;
		localVersion = CommonFunction.getVersionName(context);
		versionUpdate = new AppVersionUpdate();
	}
	
	public void startUpdate() {
		getServerVersionTask();
//		getAPVersionTask();
	}
	
	private void getServerVersionTask() {
		ServerVersionTask task = new ServerVersionTask(mContext, serverVersionHandler);
		task.execute(UrlConstant.SERVER_VERSION_URL);
	}
	
	private void getAPVersionTask() {
		ServerVersionTask task = new ServerVersionTask(mContext, apVersionHandler);
		task.execute(UrlConstant.getAPVersionUrl(CommonFunction.getGetway(mContext)));
	}
	
	private Handler serverVersionHandler = new Handler() {
		public void dispatchMessage(android.os.Message msg) {
			if (msg.what == Constant.SUCCESS) {
				String responseJson = (String) msg.obj;
				try {
					JSONObject jsonObject = new JSONObject(responseJson);
					serverVersion = jsonObject.getString("version");
					serverUrl = jsonObject.getString("path");
					serverDes = jsonObject.getString("describe");
					
					boolean update = CommonFunction.checkAppVersion(localVersion, serverVersion);
					SysApplication.isLatestVersion = !update;
					if (update) {
						if (Data.storeSceneList.size() == 0) {
							versionUpdate.doNewVersionUpdate(serverVersion, serverUrl, serverDes,
									mContext, appUpdateHandler);
						} else {
							getAPVersionTask();
						}
						
					} else {
						appUpdateHandler.sendEmptyMessage(AppVersionUpdate.NOT_UPDATE);
					}
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				appUpdateHandler.sendEmptyMessage(AppVersionUpdate.NOT_UPDATE);
			}
		};
	};
	
	public Handler apVersionHandler = new Handler() {
		public void dispatchMessage(android.os.Message msg) {
			if (msg.what == Constant.SUCCESS) {
				String responseJson = (String) msg.obj;
				try {
					JSONObject jsonObject = new JSONObject(responseJson);
					String urlVersion = jsonObject.getString("version");
					String url = jsonObject.getString("path");
					String desc = jsonObject.getString("describe");

					boolean update = CommonFunction.checkAppVersion(urlVersion, serverVersion);
					if (update) {
						versionUpdate.doNewVersionUpdate(serverVersion, serverUrl, serverDes,
								mContext, appUpdateHandler);
					} else {
						versionUpdate.doNewVersionUpdate(urlVersion, url, desc,
								mContext, appUpdateHandler);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				versionUpdate.doNewVersionUpdate(serverVersion, serverUrl, serverDes,
						mContext, appUpdateHandler);
			}
		};
	};
	
	
}
