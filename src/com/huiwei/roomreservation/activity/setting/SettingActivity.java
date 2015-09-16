package com.huiwei.roomreservation.activity.setting;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.edmodo.rangebar.RangeBar;
import com.edmodo.rangebar.RangeBar.OnRangeBarChangeListener;
import com.huiwei.commonlib.Preferences;
import com.huiwei.roomreservation.R;
import com.huiwei.roomreservation.activity.LoginActivity;
import com.huiwei.roomreservation.activity.MainActivity;
import com.huiwei.roomreservation.common.CommonConstant;
import com.huiwei.roomreservation.common.WiperSwitch;
import com.huiwei.roomreservation.common.WiperSwitch.OnChangedListener;
import com.huiwei.roomreservationlib.data.Constant;
import com.huiwei.roomreservationlib.data.Data;
import com.huiwei.roomreservationlib.info.CityInfo;
import com.huiwei.roomreservationlib.task.member.LogoutTask;

public class SettingActivity extends Activity implements OnClickListener, OnRangeBarChangeListener {
	
	private WiperSwitch storeSceneSwitch, autoLoginSwitch;
	private RangeBar sensitivityBar, intensityBar;
	private TextView city;
	private CityInfo cityInfo;
	private boolean hasStoreSceneMeasured = false;
	private boolean hasAutoLoginMeasured = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		
        sensitivityBar = (RangeBar)findViewById(R.id.sensitivity_bar);
        intensityBar = (RangeBar)findViewById(R.id.intensity_bar);
        sensitivityBar.setSingleThumb();
        sensitivityBar.setFontSize(getResources().getDimension(R.dimen.size_12sp));
        sensitivityBar.setOnRangeBarChangeListener(this);
        intensityBar.setSingleThumb();
        intensityBar.setFontSize(getResources().getDimension(R.dimen.size_12sp));

		ImageButton back = (ImageButton)findViewById(R.id.btn_return);
		back.setOnClickListener(this);
		
		((TextView)findViewById(R.id.tv_normal_info)).setOnClickListener(this);
		((ImageView)findViewById(R.id.iv_enter)).setOnClickListener(this);
		((Button)findViewById(R.id.btn_logout)).setOnClickListener(this);
		initSwitch();
		
		city = (TextView)findViewById(R.id.tv_city);
		cityInfo = Data.findCityInfo(Preferences.GetString(this, CommonConstant.KEY_CITY_ID));
		city.setOnClickListener(this);
		city.setText(cityInfo.name);
	}
	
	private void initSwitch() {
		storeSceneSwitch = (WiperSwitch)findViewById(R.id.switch_store_scene);
		ViewTreeObserver vto = storeSceneSwitch.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
            	if (hasStoreSceneMeasured == false) {
            		storeSceneSwitch.init(R.drawable.switch_off, R.drawable.switch_on,
            				R.drawable.switch_slipper, !Preferences.GetBoolean(
                        	SettingActivity.this, CommonConstant.KEY_STORE_SCENE_FLOAT, true));
            		hasStoreSceneMeasured = true;
            	} 
            	
                return true;
            }
        });
        storeSceneSwitch.setOnChangedListener(new OnChangedListener() {
			
			@Override
			public void OnChanged(WiperSwitch wiperSwitch, boolean checkState) {
				Preferences.SetBoolean(SettingActivity.this, 
						CommonConstant.KEY_STORE_SCENE_FLOAT, !checkState);
			}
		});
        
        autoLoginSwitch = (WiperSwitch)findViewById(R.id.switch_auto_login);
		ViewTreeObserver vtoAutoLogin = autoLoginSwitch.getViewTreeObserver();
		vtoAutoLogin.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
            	if (hasAutoLoginMeasured == false) {
            		autoLoginSwitch.init(R.drawable.switch_off, R.drawable.switch_on,
        				R.drawable.switch_slipper, !Preferences.GetBoolean(
                    	SettingActivity.this, CommonConstant.KEY_AUTO_LOGIN, true));
            		hasAutoLoginMeasured = true;
            	}
            	
                return true;
            }
        });
		autoLoginSwitch.setOnChangedListener(new OnChangedListener() {
			
			@Override
			public void OnChanged(WiperSwitch wiperSwitch, boolean checkState) {
				Preferences.SetBoolean(SettingActivity.this, CommonConstant.KEY_AUTO_LOGIN, !checkState);
			}
		});
	}
	
	@Override
	public void onResume() {
		super.onResume();
	}
	
	@Override
	public void onDestroy() {
		
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			
		case R.id.btn_return:
			finish();
			break;
			
		case R.id.tv_normal_info:
		case R.id.iv_enter: {
			if (Data.memberInfo.isLogin) {
				Intent intent = new Intent();
				intent.setClass(SettingActivity.this, MemberInfoEditActivity.class);
				startActivity(intent); 
			} else {
				Toast.makeText(SettingActivity.this, getResources().getString(R.string.unlogin), 
						Toast.LENGTH_SHORT).show();
				Intent intent = new Intent();
				intent.setClass(SettingActivity.this, LoginActivity.class);
				startActivityForResult(intent, MainActivity.TO_LOGIN);
			}
		}
			break;
			
		case R.id.tv_city:
			cityChoseDialog();
			break;
			
		case R.id.btn_logout: {
			LogoutTask logoutTask = new LogoutTask(SettingActivity.this, logoutHandler);
			logoutTask.execute();
		}
			break;
		
		default:
			break;
		}
	}
	
	private void cityChoseDialog() {
		List<String> citys = new ArrayList<String>();
		for (int i=0; i<Data.cityList.size(); i++) {
			citys.add(Data.cityList.get(i).name);
		}
		new AlertDialog.Builder(this)             
		.setSingleChoiceItems((String[]) citys.toArray(
				new String[citys.size()]), 0, 
		  new DialogInterface.OnClickListener() {
		                            
		     public void onClick(DialogInterface dialog, int which) {
		        dialog.dismiss();
		        cityInfo = Data.cityList.get(which);
		        city.setText(cityInfo.name);
		        Preferences.SetString(SettingActivity.this, CommonConstant.KEY_CITY_ID, cityInfo.id);
		     }
		  }
		)
		.show();
	}
	
	private Handler logoutHandler = new Handler() {
		public void dispatchMessage(android.os.Message msg) {
			if (msg.what == Constant.SUCCESS) {
				Preferences.SetBoolean(SettingActivity.this, CommonConstant.KEY_AUTO_LOGIN, false);
				Toast.makeText(SettingActivity.this, getResources().
						getString(R.string.logout_success), Toast.LENGTH_SHORT).show();
			} else if (msg.what == Constant.DATA_ERROR) {
				Toast.makeText(SettingActivity.this, (String)msg.obj, Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(SettingActivity.this, getResources().
						getString(R.string.logout_fail), Toast.LENGTH_SHORT).show();
			}
		};
	};
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (requestCode == MainActivity.TO_LOGIN) {
			if (resultCode != LoginActivity.RETURN) {
				Intent intent = new Intent();
				intent.setClass(SettingActivity.this, MemberInfoEditActivity.class);
				startActivity(intent); 
			} 
		}
	}

	@Override
	public void onIndexChangeListener(RangeBar rangeBar, int leftThumbIndex,
			int rightThumbIndex) {
		switch (rangeBar.getId()) {
		case R.id.sensitivity_bar:
			
			break;
			
		case R.id.intensity_bar:
			
			break;

		default:
			break;
		}
		
	}

}
