package com.huiwei.roomreservation.view;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.edmodo.rangebar.RangeBar;
import com.edmodo.rangebar.RangeBar.OnRangeBarChangeListener;
import com.huiwei.commonlib.Preferences;
import com.huiwei.roomreservation.R;
import com.huiwei.roomreservation.adapter.MenuItemAdapter;
import com.huiwei.roomreservation.common.CommonConstant;
import com.huiwei.roomreservationlib.info.CityInfo;
import com.huiwei.roomreservationlib.info.CityInfo.AreaInfo;
import com.huiwei.roomreservationlib.info.CityInfo.RegionInfo;
import com.huiwei.roomreservationlib.info.RoomSizeInfo;
import com.huiwei.roomreservationlib.info.StoreSceneInfo;
import com.huiwei.roomreservationlib.data.Data;
import com.huiwei.roomreservationlib.info.TimeSlotInfo;

public class SearchView extends RelativeLayout implements OnClickListener, OnRangeBarChangeListener {
	
	private Context mContext;
	private RelativeLayout layout;
	private LinearLayout roomSizeLayout;
	private RangeBar rangeBar;
	private RatingBar ratingBar;
	private TextView city, area, region, rangeMin, rangeMax;
	private boolean hasViewMeasured;
	private int viewWidth;
	private Handler handler;
	private CityInfo cityInfo = null;
	private AreaInfo areaInfo = null;
	private RegionInfo regionInfo = null;
	
	public SearchView(Context context) {
		super(context);
		mContext = context;
	}
	
	public SearchView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
	}
	
	@Override
	protected void onFinishInflate() {
		// TODO Auto-generated method stub
		super.onFinishInflate();
		
		int screenWidth = getResources().getDisplayMetrics().widthPixels;
		viewWidth = screenWidth/6*5;
		
		rangeMin =(TextView)findViewById(R.id.tv_min);
		rangeMax =(TextView)findViewById(R.id.tv_max);
		layout =(RelativeLayout)findViewById(R.id.layout_search);
		Button search =(Button)findViewById(R.id.btn_search);
		roomSizeLayout = (LinearLayout)findViewById(R.id.layout_room_size);
		ratingBar = (RatingBar)findViewById(R.id.ratingBar);
		
		if (layout != null) {
			search.setOnClickListener(this);
			resizeSearchView();
			initRoomSizeView();
		}
		
		rangeBar = (RangeBar)findViewById(R.id.rangebar1);
		if (rangeBar != null) {
			rangeBar.setThumbImageNormal(R.drawable.slider_block);
			rangeBar.setThumbImagePressed(R.drawable.slider_block_down);
			rangeBar.setOnRangeBarChangeListener(this);
		}
		
		city = (TextView)findViewById(R.id.tv_city);
		area = (TextView)findViewById(R.id.tv_area);
		region = (TextView)findViewById(R.id.tv_region);
		if (city != null) {
			city.setOnClickListener(this);
			area.setOnClickListener(this);
			region.setOnClickListener(this);
			
			cityInfo = Data.findCityInfo(Preferences.GetString(
					mContext, CommonConstant.KEY_CITY_ID));
			if (cityInfo != null) {
				if (cityInfo.areaList.size() > 0) {
					areaInfo = cityInfo.areaList.get(0);
					if (areaInfo.regionList.size() > 0)
						regionInfo = areaInfo.regionList.get(0);
				}
				
				city.setText(cityInfo.name);
				if (areaInfo != null){
					area.setText(areaInfo.name);
					if (regionInfo != null) {
						region.setText(regionInfo.name);
					}
				}
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			
		case R.id.btn_search: {
			Data.searchInfo.fromSearch = true;
			Data.searchInfo.cppMin = getRangValue(rangeBar.getLeftIndex());
			Data.searchInfo.cppMax = getRangValue(rangeBar.getRightIndex());
			Data.searchInfo.recommend = ratingBar.getRating();
			Data.searchInfo.cityID = cityInfo.id;
			Data.searchInfo.areaID = areaInfo == null ? "" : areaInfo.id;
			Data.searchInfo.regionID = regionInfo == null ? "" : regionInfo.id;
			Data.searchInfo.roomSizeList.clear();
			for (int i=0; i<Data.roomSizeList.size(); i++) {
				RoomSizeInfo info = Data.roomSizeList.get(i);
				if (info.isSelected) {
					Data.searchInfo.roomSizeList.add(info.id);
				}
				
			}
			
			handler.sendEmptyMessage(0);
		}
			break;
			
		case R.id.tv_city:
			cityChoseDialog();
			break;
			
		case R.id.tv_area: {
			if (areaInfo != null)
				areaChoseDialog();
		}
			break;
			
		case R.id.tv_region: {
			if (regionInfo != null) 
				regionChoseDialog();
		}
			break;

		default:
			break;
		}
	}
	
	private int getRangValue(int progress) {
		if (progress == 99)
			return 0;
		
		return (Data.cppMax-0)*progress / 100;
	}
	
	private void cityChoseDialog() {
		List<String> citys = new ArrayList<String>();
		for (int i=0; i<Data.cityList.size(); i++) {
			citys.add(Data.cityList.get(i).name);
		}
		new AlertDialog.Builder(mContext)             
		.setSingleChoiceItems((String[]) citys.toArray(
				new String[citys.size()]), 0, 
		  new DialogInterface.OnClickListener() {
		                            
		     public void onClick(DialogInterface dialog, int which) {
		        dialog.dismiss();
		        cityInfo = Data.cityList.get(which);
		        city.setText(cityInfo.name);
		        if (cityInfo.areaList.size() > 0) {
		        	areaInfo = cityInfo.areaList.get(0);
		        	area.setText(areaInfo.name);
		        	if (areaInfo.regionList.size() > 0) {
		        		regionInfo = areaInfo.regionList.get(0);
						region.setText(regionInfo.name);
					} else {
						regionInfo = null;
						region.setText("");
					}
		        } else {
		        	areaInfo = null;
		        	area.setText("");
		        }
		     }
		  }
		)
		.show();
	}
	
	private void areaChoseDialog() {
		List<String> areas = new ArrayList<String>();
		for (int i=0; i<cityInfo.areaList.size(); i++) {
			areas.add(cityInfo.areaList.get(i).name);
		}
		new AlertDialog.Builder(mContext)             
		.setSingleChoiceItems((String[]) areas.toArray(
				new String[areas.size()]), 0, 
		  new DialogInterface.OnClickListener() {
		                            
		     public void onClick(DialogInterface dialog, int which) {
		        dialog.dismiss();
		        areaInfo = cityInfo.areaList.get(which);
		        area.setText(areaInfo.name);
				if (areaInfo.regionList.size() > 0) {
					regionInfo = areaInfo.regionList.get(0);
					region.setText(regionInfo.name);
				} else {
					regionInfo = null;
					region.setText("");
				}
		     }
		  }
		)
		.show();
	}
	
	private void regionChoseDialog() {
		List<String> regions = new ArrayList<String>();
		for (int i=0; i<areaInfo.regionList.size(); i++) {
			regions.add(areaInfo.regionList.get(i).name);
		}
		new AlertDialog.Builder(mContext)             
		.setSingleChoiceItems((String[]) regions.toArray(
				new String[regions.size()]), 0, 
		  new DialogInterface.OnClickListener() {
		                            
		     public void onClick(DialogInterface dialog, int which) {
		        dialog.dismiss();
		        regionInfo = areaInfo.regionList.get(which);
				region.setText(regionInfo.name);
		     }
		  }
		)
		.show();
	}
	
	
	private void resizeSearchView() {
		ViewTreeObserver vto = layout.getViewTreeObserver();
		vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
			public boolean onPreDraw() {
				if (hasViewMeasured == false) {
					RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
							viewWidth, layout.getHeight());
					lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
					layout.setLayoutParams(lp);
					setVisibility(View.GONE);
					hasViewMeasured = true;
				}
				return true;
			}
		});
	}
	
	private void initRoomSizeView() {
		roomSizeLayout.removeAllViews();
		
		LayoutInflater inflater = LayoutInflater.from(mContext);
		for (int i = 0; i<Data.roomSizeList.size(); i+=3) {
			RoomSizeView view = (RoomSizeView) inflater.inflate(R.layout.room_size_view, null);
			if (i+1 == Data.roomSizeList.size()) {
				view.setRoomSizeInfo(Data.roomSizeList.get(i), null, null);
			} else if (i+2 == Data.roomSizeList.size()) {
				view.setRoomSizeInfo(Data.roomSizeList.get(i),
						Data.roomSizeList.get(i+1), null);
			} else {
				view.setRoomSizeInfo(Data.roomSizeList.get(i), 
						Data.roomSizeList.get(i+1), 
						Data.roomSizeList.get(i+2));
			}
			
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			lp.weight = 1;
			lp.setMargins(0, 0, 0, 10);
			
			roomSizeLayout.addView(view, lp);
		}
	}
	
	public void setHandler(Handler handler) {
		this.handler = handler;
	}

	@Override
	public void onIndexChangeListener(RangeBar rangeBar, int leftThumbIndex,
			int rightThumbIndex) {
		int leftX = leftThumbIndex*rangeBar.getWidth()/100;
		int rightX = rightThumbIndex*rangeBar.getWidth()/100;
		int cppMin = getRangValue(leftThumbIndex);
		int cppMax = getRangValue(rightThumbIndex);
		
		rangeMin.setText(getResources().getString(R.string.yuan)+cppMin);
		rangeMax.setText(cppMax == 0 ? getResources().getString(R.string.limitless) :
			(""+cppMax+getResources().getString(R.string.yuan)));
		
		RelativeLayout.LayoutParams leftLP = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		leftLP.addRule(RelativeLayout.ALIGN_LEFT, R.id.rangebar1);
		leftLP.setMargins(leftX, 0, 0, 0);
		rangeMin.setLayoutParams(leftLP);
		
		RelativeLayout.LayoutParams rightLP = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		rightLP.addRule(RelativeLayout.ALIGN_LEFT, R.id.rangebar1);
		rightLP.setMargins(rightX-rangeMax.getWidth(), 0, 0, 0);
		
		rangeMax.setLayoutParams(rightLP);
	}
}
	
