package com.huiwei.roomreservation.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ZoomControls;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapLoadedCallback;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.model.LatLngBounds.Builder;
import com.huiwei.commonlib.CommonFunction;
import com.huiwei.roomreservation.R;
import com.huiwei.roomreservation.activity.MainActivity;
import com.huiwei.roomreservation.activity.StoreDetailActivity;
import com.huiwei.roomreservation.common.CommonConstant;
import com.huiwei.roomreservation.view.LoadingView;
import com.huiwei.roomreservationlib.data.Constant;
import com.huiwei.roomreservationlib.info.StoreInfo;
import com.huiwei.roomreservationlib.task.MapStoreListTask;

public class MapFragment extends Fragment implements OnClickListener {

	private MapView mMapView;
	private BaiduMap mBaiduMap;
	private LocationClient mLocClient;
	private boolean isFirstLoc = true;
	private Handler parentHandler;

	private EditText searchKey;
	private float zoom;
	private List<StoreInfo> storeList = new ArrayList<StoreInfo>();
	private List<BitmapDescriptor> bdList = new ArrayList<BitmapDescriptor>();
	private LoadingView loadingView;
	private RelativeLayout locLayout;
	private TextView locationLL, address;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_map,
				container, false);

		address = (TextView)view.findViewById(R.id.tv_address);
		locationLL = (TextView)view.findViewById(R.id.tv_location);
		locLayout = (RelativeLayout)view.findViewById(R.id.layout_location);
		loadingView = (LoadingView)view.findViewById(R.id.loading);
		mMapView = (MapView) view.findViewById(R.id.bmapView);
		mBaiduMap = mMapView.getMap();
		mBaiduMap.setMyLocationEnabled(true);

		// initialize location
		if (mLocClient == null) {
			mLocClient = new LocationClient(getActivity());
			mLocClient.registerLocationListener(locationListener);
			LocationClientOption option = new LocationClientOption();
			option.setOpenGps(true);
			option.setCoorType("bd09ll"); // set the type of coordinate
			option.setScanSpan(1000);
			option.setIsNeedAddress(true);
			mLocClient.setLocOption(option);
		}
		
		if (!mLocClient.isStarted()) {
			isFirstLoc = true;
			mLocClient.start();
		}

		mBaiduMap.setOnMarkerClickListener(new OnMarkerClickListener() {
			public boolean onMarkerClick(final Marker marker) {
				Intent intent = new Intent();
				intent.putExtra("store_id",
						marker.getExtraInfo().getString("store_id"));
				intent.setClass(getActivity(), StoreDetailActivity.class);
				startActivity(intent);
				return true;
			}
		});
		
		mBaiduMap.setOnMapLoadedCallback(new OnMapLoadedCallback() {
			
			@Override
			public void onMapLoaded() {
				startMapStoreListTask();
				
				RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
						LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
				lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
				lp.setMargins(CommonFunction.dip2px(getActivity(), 10), 
						0, CommonFunction.dip2px(getActivity(), 10),
						CommonFunction.dip2px(getActivity(), 60));
				locLayout.setLayoutParams(lp);
			}
		});

		searchKey = (EditText) view.findViewById(R.id.et_search);
		((ImageButton) view.findViewById(R.id.btn_search)).setOnClickListener(this);
		((ImageButton) view.findViewById(R.id.btn_return)).setOnClickListener(this);
		
		return view;
	}
	
	public void setParentHandler(Handler handler) {
		parentHandler = handler;
	}
	
	private void startMapStoreListTask() {
		MapStoreListTask task = new MapStoreListTask(getActivity(), 
				handler, searchKey.getText().toString());
		task.execute();
	}

	private Handler handler = new Handler() {
		@SuppressWarnings("unchecked")
		public void dispatchMessage(android.os.Message msg) {
			if (msg.what == Constant.SUCCESS) {
				storeList = (List<StoreInfo>) msg.obj;
				resetOverlay();
			} else if (msg.what == Constant.DATA_ERROR) {
				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				builder.setMessage(R.string.no_related_store);
				builder.setPositiveButton(R.string.ok, null);
				builder.create().show();
			} else {
				Toast.makeText(getActivity(), getResources().getString(
						R.string.load_data_fail), Toast.LENGTH_SHORT).show();
			}
			
			loadingView.setVisibility(View.GONE);
		};
	};

	@Override
	public void onDestroy() {
		super.onDestroy();

		clearOverlay();
		mLocClient.stop();
		mBaiduMap.setMyLocationEnabled(false);
		mMapView.onDestroy();
		mMapView = null;

		for (StoreInfo storeInfo : storeList) {
			storeInfo = null;
		}
		storeList.clear();
	}

	@Override
	public void onResume() {
		super.onResume();
		mMapView.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
		mMapView.onPause();
	}

	private BDLocationListener locationListener = new BDLocationListener() {

		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null || mMapView == null || !isFirstLoc) {
				return;
			}

			isFirstLoc = false;
			MyLocationData locData = new MyLocationData.Builder()
					.accuracy(location.getRadius()).direction(100)
					.latitude(location.getLatitude())
					.longitude(location.getLongitude()).build();
			
			address.setText(location.getAddrStr());
			String locString = ""+location.getLongitude()+","+location.getLatitude();
			locationLL.setText(locString);

			mBaiduMap.setMyLocationData(locData);

			LatLng ll = new LatLng(location.getLatitude(),
					location.getLongitude());
			MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
			mBaiduMap.animateMapStatus(u);
		}
	};

	public void initOverlay() {
		LatLngBounds.Builder builder = new Builder();
		for (StoreInfo storeInfo : storeList) {
			addOverlay(storeInfo, builder);
		}

		LatLngBounds bounds = builder.build();
		MapStatusUpdate u = MapStatusUpdateFactory.newLatLngBounds(bounds);
		mBaiduMap.animateMapStatus(u);
	}

	private void addOverlay(StoreInfo storeInfo, Builder builder) {
		LatLng latLng = new LatLng(storeInfo.latitude, storeInfo.longitude);
		OverlayOptions oo = new MarkerOptions().position(latLng).icon(
				getIcon(storeInfo.name));
		Marker marker = (Marker) (mBaiduMap.addOverlay(oo));
		Bundle bundle = new Bundle();
		bundle.putString("store_id", storeInfo.id);
		marker.setExtraInfo(bundle);
		builder.include(latLng);
	}

	private BitmapDescriptor getIcon(String text) {
		TextView tv = new TextView(getActivity());
		tv.setText(text);
		tv.setTextColor(Color.BLACK);
		tv.setBackgroundResource(R.drawable.popup);

		BitmapDescriptor bd = BitmapDescriptorFactory.fromBitmap(CommonFunction
				.getBitmapFromView(tv));

		bdList.add(bd);

		return bd;
	}

	// clear Overlay
	public void clearOverlay() {
		mBaiduMap.clear();

		for (BitmapDescriptor bd : bdList) {
			bd.recycle();
		}
		bdList.clear();
	}

	public void resetOverlay() {
		clearOverlay();
		initOverlay();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_search: {
			if (searchKey.getText().toString().equals("")) {
				Toast.makeText(getActivity(), getResources().getString(R.string.please_input_search_key), 
						Toast.LENGTH_SHORT).show();;
			} else {
				loadingView.setVisibility(View.VISIBLE);
				startMapStoreListTask();
			}
		}
			break;
			
		case R.id.btn_return:
			parentHandler.sendEmptyMessage(MainActivity.TO_MAIN);
			break;

		default:
			break;
		}
		
	}
}
