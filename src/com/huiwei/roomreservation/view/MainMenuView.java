package com.huiwei.roomreservation.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.text.style.BulletSpan;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.huiwei.commonlib.AppVersionUpdate;
import com.huiwei.commonlib.CommonFunction;
import com.huiwei.roomreservation.R;
import com.huiwei.roomreservation.activity.AboutActivity;
import com.huiwei.roomreservation.activity.MainActivity;
import com.huiwei.roomreservation.activity.MipcaCaptureActivity;
import com.huiwei.roomreservation.activity.StoreSceneActivity;
import com.huiwei.roomreservation.activity.setting.SettingActivity;
import com.huiwei.roomreservation.adapter.MenuItemAdapter;
import com.huiwei.roomreservation.common.CommonConstant;
import com.huiwei.roomreservation.common.SysApplication;
import com.huiwei.roomreservation.common.VersionUpdate;
import com.huiwei.roomreservationlib.data.Data;

public class MainMenuView extends RelativeLayout {

	public static final int MENU_STORE = 0;
	public static final int MENU_MEMBER = 1;
	public static final int MENU_CONTROL = 2;
	public static final int MENU_PUSH = 3;
	public static final int MENU_SEARCH = 4;
	public static final int MENU_SHARE = 5;
	public static final int MENU_SCAN = 6;
	public static final int MENU_UPDATE = 7;
	public static final int MENU_SETTING = 8;
	public static final int MENU_ABOUT = 9;

	public static final int FROM_MAIN = 10;
	public static final int FROM_STORE_SCENE = 11;
	public static final int SLIDE_IN = 15;
	public static final int SLIDE_OUT = 16;
	public static final int SLIDE = 17;

	private Context mContext;

	private RelativeLayout quit;
	private ListView menus;
	private MenuItemAdapter adapter;
	private int fromPage = FROM_MAIN;
	private Handler handler;

	public MainMenuView(Context context) {
		super(context);
		mContext = context;
	}

	public MainMenuView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
	}

	@Override
	protected void onFinishInflate() {
		// TODO Auto-generated method stub
		super.onFinishInflate();

		menus = (ListView) findViewById(R.id.lv_menu);
		if (menus != null) {
			initMenus();
		}
	}

	public void setParentPage(int page) {
		fromPage = page;
	}

	public void setParentHandler(Handler handler) {
		this.handler = handler;
	}
	
	public void setMenuName(int index, int strID) {
		adapter.setMenuName(index, strID);
	}

	private void initMenus() {
		adapter = new MenuItemAdapter(mContext);
		menus.setAdapter(adapter);
		menus.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int id,
					long arg3) {
//				adapter.setSelectItemID(id);
//				adapter.notifyDataSetChanged();
				switch (id) {
				case MENU_STORE:
					menuStoreScene();
					break;

				case MENU_MEMBER: {
					if (fromPage == FROM_STORE_SCENE) {
						((Activity) mContext).setResult(MainActivity.TO_MEMBER);
						((Activity) mContext).finish();
					} else {
						handler.sendEmptyMessage(MainActivity.TO_MEMBER);
					}
				}
					break;
				case MENU_CONTROL: {
					Intent intent = new Intent();
					intent.putExtra("push", 3);
					intent.setClass(mContext, StoreSceneActivity.class);
					((FragmentActivity) mContext).startActivityForResult(
							intent, MainActivity.TO_STORE_SCENE);
				}
					break;

				case MENU_PUSH: {
					Intent intent = new Intent();
					intent.putExtra("push", 5);
					intent.setClass(mContext, StoreSceneActivity.class);
					((FragmentActivity) mContext).startActivityForResult(
							intent, MainActivity.TO_STORE_SCENE);
				}
					break;


				case MENU_SEARCH:
					if (fromPage == FROM_STORE_SCENE) {
						((Activity) mContext).setResult(MainActivity.TO_SEARCH);
						((Activity) mContext).finish();
					} else {
						handler.sendEmptyMessage(MainActivity.TO_SEARCH);
					}
					break;

				case MENU_SHARE: {
					if (fromPage == FROM_STORE_SCENE) {
						((Activity) mContext).setResult(MainActivity.TO_SHARE);
						((Activity) mContext).finish();
					} else {
						handler.sendEmptyMessage(MainActivity.TO_SHARE);
					}
				}
					break;

				case MENU_SCAN: {
					Intent intent = new Intent();
					intent.setClass(mContext, MipcaCaptureActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					mContext.startActivity(intent);
				}
					break;

				case MENU_UPDATE:
					updateVersion();
					break;

				case MENU_SETTING: {
					Intent intent = new Intent();
					intent.setClass(mContext, SettingActivity.class);
					mContext.startActivity(intent);
				}
					break;

				case MENU_ABOUT: {
					Intent intent = new Intent();
					intent.setClass(mContext, AboutActivity.class);
					mContext.startActivity(intent);
				}
					break;

				default:
					break;
				}
				
				if (handler != null) {
					handler.sendEmptyMessage(SLIDE_IN);
				}
			}
		});
	}

	private void menuStoreScene() {
		if (fromPage == FROM_MAIN) {
			Intent intent = new Intent();
			intent.putExtra("push", 1);
			intent.setClass(mContext, StoreSceneActivity.class);
			((FragmentActivity) mContext).startActivityForResult(intent,
					MainActivity.TO_STORE_SCENE);
		} else {
			((StoreSceneActivity) mContext).returnToMain();
		}
	}

	private void updateVersion() {
		VersionUpdate versionUpdate = new VersionUpdate(mContext,
				appUpdateHandler);
		if (SysApplication.isLatestVersion) {
			AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
			builder.setTitle(R.string.version_update);
			builder.setMessage(getResources()
					.getString(R.string.latest_version)
					+ CommonFunction.getVersionName(mContext));
			builder.setPositiveButton(R.string.ok, null);
			builder.create().show();
		} else {
			versionUpdate.startUpdate();
		}
	}

	private Handler appUpdateHandler = new Handler() {
		public void dispatchMessage(android.os.Message msg) {
			if (msg.what == AppVersionUpdate.UPDATE_COMPLETE) {
				SysApplication.isLatestVersion = true;
			}
		};
	};
}
