package com.huiwei.roomreservation.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.huiwei.commonlib.CommonFunction;
import com.huiwei.commonlib.Preferences;
import com.huiwei.roomreservation.R;
import com.huiwei.roomreservation.common.CommonConstant;
import com.huiwei.roomreservation.common.SysApplication;
import com.huiwei.roomreservationlib.data.Data;
import com.huiwei.roomreservationlib.info.CityInfo;

public class CityChoseView extends RelativeLayout implements OnClickListener {
	
	public static final int CLOSE = 1;
	
	private Context context;
	private List<TextView> cities = new ArrayList<TextView>();
	private Handler handler;
	private TextView curCity;
	private LinearLayout layoutCities;
	private LoadingView loadingView;
	private boolean isStartLocation = true;
	
	public CityChoseView(Context context) {
		super(context);
		this.context = context;
	}
	
	public CityChoseView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
	}
	
	@Override
	protected void onFinishInflate() {
		// TODO Auto-generated method stub
		super.onFinishInflate();
		
		loadingView = (LoadingView)findViewById(R.id.loading);
		curCity = (TextView)findViewById(R.id.tv_city);
		layoutCities = (LinearLayout)findViewById(R.id.layout_cities);
		TextView cityTitle = (TextView)findViewById(R.id.tv_city_title);
		ImageView back = (ImageView)findViewById(R.id.iv_back);
		ImageView close = (ImageView)findViewById(R.id.iv_close);
		Button location = (Button)findViewById(R.id.btn_location);
		
		if (location != null) {
			location.setOnClickListener(this);
			cityTitle.setOnClickListener(this);
			back.setOnClickListener(this);
			close.setOnClickListener(this);
			initView();
		}
	}

	public void initView() {
		if (layoutCities == null || Data.cityList.size() == 0)
			return;
		
		curCity.setText(Data.findCityInfo(Preferences.GetString(
				context, CommonConstant.KEY_CITY_ID)).name);
		layoutCities.removeAllViews();
		cities.clear();
		int col = 3;
		int row = Data.cityList.size()%col == 0 ? Data.cityList.size()/col : 
			Data.cityList.size()/col+1;
		
		int number = 0;
		for (int i=0; i<row; i++) {
			LinearLayout layout = new LinearLayout(context);
			layout.setOrientation(LinearLayout.HORIZONTAL);
			
			for (int j=0; j<col; j++) {
				TextView tv = new TextView(context);
				tv.setTag(number);
				tv.setGravity(Gravity.CENTER);
				tv.setTextSize(TypedValue.COMPLEX_UNIT_PX,
		                getResources().getDimensionPixelSize(R.dimen.size_16sp));
				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
						LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
				lp.weight = 1;
				
				if (Data.cityList.size() > number) {
					tv.setText(Data.cityList.get(number).name);
					tv.setOnClickListener(this);
					cities.add(tv);
				} else {
					tv.setText("        ");
					tv.setVisibility(View.INVISIBLE);
				}
				
				layout.addView(tv, lp);
				number++;
			}
			LinearLayout.LayoutParams layoutLP = new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			layoutLP.topMargin = CommonFunction.dip2px(context, 10);
			layoutCities.addView(layout, layoutLP);
		}
	}

	
	public void setHandler(Handler handler) {
		this.handler = handler;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_location: {
			loadingView.setVisibility(View.VISIBLE);
			loadingView.setLoadingText(getResources().getString(R.string.locating));
			isStartLocation = true;
			location();
		}
			break;
			
		case R.id.tv_city_title:
		case R.id.iv_back:
		case R.id.iv_close: {
			handler.sendEmptyMessage(CLOSE);
		}
			break;

		default: {
			int index = (Integer)v.getTag();
			CityInfo cityInfo = Data.cityList.get(index);
			curCity.setText(cityInfo.name);
			Preferences.SetString(context, CommonConstant.KEY_CITY_ID, cityInfo.id);
			handler.sendEmptyMessage(CLOSE);
			}
			break;
		}
	}
	
	private boolean findCity(String cityName) {
		if (cityName.equals(""))
			return false;
		
		for (CityInfo cityInfo : Data.cityList) {
			if (cityName.contains(cityInfo.name)) {
				Preferences.SetString(context, CommonConstant.KEY_CITY_ID, cityInfo.id);
				curCity.setText(cityInfo.name);
				return true;
			}
		}
		
		Toast.makeText(context, cityName+":"+getResources().
				getString(R.string.no_such_city), Toast.LENGTH_SHORT).show();
		
		return false;
	}

	private void location() {
		final LocationClient locationClient = new LocationClient(context);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);
		option.setCoorType("bd09ll");
		option.setIsNeedAddress(true);
		locationClient.setLocOption(option);
		locationClient.start();
		locationClient.requestLocation();

		locationClient.registerLocationListener(new BDLocationListener() {
			@Override
			public void onReceiveLocation(BDLocation location) {
				if (location == null || !isStartLocation) {
					return;
				}

				if (!findCity(location.getCity())) {
					curCity.setText(Data.findCityInfo(Preferences.GetString(
							context, CommonConstant.KEY_CITY_ID)).name);
				}
				isStartLocation = false;
				loadingView.setVisibility(View.GONE);
				locationClient.stop();
				handler.sendEmptyMessage(CLOSE);
			}
		});
	}
}
	
