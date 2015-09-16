package com.huiwei.roomreservation.activity;

import android.app.Activity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapLoadedCallback;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.overlayutil.DrivingRouteOvelray;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.huiwei.roomreservation.R;
import com.huiwei.roomreservation.view.EditInputView;
import com.huiwei.roomreservation.view.LoadingView;
import com.huiwei.roomreservationlib.data.Data;

public class NavigationActivity extends Activity implements OnClickListener,
	OnGetRoutePlanResultListener, OnGetGeoCoderResultListener {

	private MapView mMapView;
	private BaiduMap mBaiduMap;
	private LocationClient mLocClient;
	private RoutePlanSearch routePlanSearch = null;
	private GeoCoder geoCoder = null;
	private boolean isFirstLoc = true;
	private LatLng myLocation = null;
	private LatLng destination = null;

	private LoadingView loadingView;
	private LinearLayout layoutNavigation;
	private EditInputView startingLoc, destinationLoc;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_navigation);

		layoutNavigation = (LinearLayout)findViewById(R.id.layout_navigation);
		startingLoc = (EditInputView)findViewById(R.id.view_starting);
		destinationLoc = (EditInputView)findViewById(R.id.view_destination);
		initInputView();
		loadingView = (LoadingView)findViewById(R.id.loading);
		mMapView = (MapView) findViewById(R.id.bmapView);
		mBaiduMap = mMapView.getMap();
		mBaiduMap.setMyLocationEnabled(true);

		// initialize location
		mLocClient = new LocationClient(this);
		mLocClient.registerLocationListener(locationListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);
		option.setCoorType("bd09ll"); // set the type of coordinate
		option.setScanSpan(1000);
		option.setIsNeedAddress(true);
		mLocClient.setLocOption(option);
		mLocClient.start();
		
		routePlanSearch = RoutePlanSearch.newInstance();
		routePlanSearch.setOnGetRoutePlanResultListener(this);
        
        geoCoder = GeoCoder.newInstance();
        geoCoder.setOnGetGeoCodeResultListener(this);
		
		mBaiduMap.setOnMapLoadedCallback(new OnMapLoadedCallback() {
			
			@Override
			public void onMapLoaded() {
			}
		});

		((ImageButton)findViewById(R.id.btn_navigation)).setOnClickListener(this);
		((ImageButton)findViewById(R.id.btn_return)).setOnClickListener(this);
		((Button)findViewById(R.id.btn_start_navigation)).setOnClickListener(this);
	}
	
	private void initInputView() {
		startingLoc.setInfo(getResources().getString(R.string.my_location),
				getResources().getString(R.string.my_location),
				InputType.TYPE_CLASS_TEXT, R.drawable.navigation_starting);
		destinationLoc.setInfo(Data.storeDetailInfo.name, "", 
				InputType.TYPE_CLASS_TEXT, R.drawable.navigation_destination);
		destinationLoc.disableInput();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		routePlanSearch.destroy();
		geoCoder.destroy();
		
		mLocClient.stop();
		mBaiduMap.setMyLocationEnabled(false);
		mMapView.onDestroy();
		mMapView = null;
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
			
			myLocation = null;
			myLocation = new LatLng(location.getLatitude(), location.getLongitude());

			mBaiduMap.setMyLocationData(locData);

			LatLng ll = new LatLng(location.getLatitude(),
					location.getLongitude());
			MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
			mBaiduMap.animateMapStatus(u);
		}
	};

	   

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_navigation: {
			if (layoutNavigation.getVisibility() == View.GONE) {
				layoutNavigation.setVisibility(View.VISIBLE);
			} else {
				layoutNavigation.setVisibility(View.GONE);
			}
		}
			break;
			
		case R.id.btn_return:
			finish();
			break;
			
		case R.id.btn_start_navigation: {
			loadingView.setVisibility(View.VISIBLE);
			loadingView.setLoadingText(getResources().getString(R.string.navigating));
			String starting = startingLoc.getText();
	        if (!starting.equals("") && !starting.equals(
	        		getResources().getString(R.string.my_location))) {
	        	geoCoder.geocode(new GeoCodeOption().city("").address(starting));
	        	myLocation = null;
	        } else {
	        	geoCoder.geocode(new GeoCodeOption().city("").address(
						Data.storeDetailInfo.address));
				destination = null;
	        }
		}
			break;

		default:
			break;
		}
		
	}
	
	public void startNavigation() {
//        route = null;
//        mBtnPre.setVisibility(View.INVISIBLE);
//        mBtnNext.setVisibility(View.INVISIBLE);
        mBaiduMap.clear();
        
        if (myLocation == null) {
        	return;
        }
        
        PlanNode stNode = PlanNode.withLocation(myLocation);
        PlanNode enNode = PlanNode.withLocation(destination);
       
//        if (v.getId() == R.id.drive) {
            routePlanSearch.drivingSearch((new DrivingRoutePlanOption())
                    .from(stNode)
                    .to(enNode));
//        } else if (v.getId() == R.id.transit) {
//            mSearch.transitSearch((new TransitRoutePlanOption())
//                    .from(stNode)
//                    .city("北京")
//                    .to(enNode));
//        } else if (v.getId() == R.id.walk) {
//            mSearch.walkingSearch((new WalkingRoutePlanOption())
//                    .from(stNode)
//                    .to(enNode));
//        }
    }
	
	@Override
    public void onGetDrivingRouteResult(DrivingRouteResult result) {
		loadingView.setVisibility(View.GONE);
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(NavigationActivity.this, getResources().getString(
            		R.string.not_route_line), Toast.LENGTH_SHORT).show();
        }
        
        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            //result.getSuggestAddrInfo()
            return;
        }
        
        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
//            nodeIndex = -1;
//            mBtnPre.setVisibility(View.VISIBLE);
//            mBtnNext.setVisibility(View.VISIBLE);
//            route = result.getRouteLines().get(0);
            DrivingRouteOvelray overlay = new MyDrivingRouteOverlay(mBaiduMap);
//            routeOverlay = overlay;
            mBaiduMap.setOnMarkerClickListener(overlay);
            overlay.setData(result.getRouteLines().get(0));
            overlay.addToMap();
            overlay.zoomToSpan();
        }
    }
	
	private class MyDrivingRouteOverlay extends DrivingRouteOvelray {

        public MyDrivingRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public BitmapDescriptor getStartMarker() {
//            if (useDefaultIcon) {
//                return BitmapDescriptorFactory.fromResource(R.drawable.icon_st);
//            }
            return null;
        }

        @Override
        public BitmapDescriptor getTerminalMarker() {
//            if (useDefaultIcon) {
//                return BitmapDescriptorFactory.fromResource(R.drawable.icon_en);
//            }
            return null;
        }
    }

	@Override
	public void onGetTransitRouteResult(TransitRouteResult arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onGetWalkingRouteResult(WalkingRouteResult arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onGetGeoCodeResult(GeoCodeResult result) {
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			Toast.makeText(NavigationActivity.this, getResources().getString(
            		R.string.not_route_line), Toast.LENGTH_SHORT).show();
			return;
		}
		
		if (result.getAddress().equals(startingLoc.getText())) {
			myLocation = new LatLng(result.getLocation().latitude, 
					result.getLocation().longitude);
			geoCoder.geocode(new GeoCodeOption().city("").address(
					Data.storeDetailInfo.address));
			destination = null;
		} else if (result.getAddress().equals(Data.storeDetailInfo.address.toLowerCase())) {
			destination = new LatLng(result.getLocation().latitude, 
					result.getLocation().longitude);
		}
		
		if (myLocation != null || destination != null) {
			startNavigation();
		}
	}

	@Override
	public void onGetReverseGeoCodeResult(ReverseGeoCodeResult arg0) {
		// TODO Auto-generated method stub
		
	}
	
	
}
