package com.huiwei.roomreservation.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.huiwei.roomreservation.R;
import com.huiwei.roomreservation.adapter.ViewPagerAdapter;
import com.huiwei.roomreservation.common.CommonAnimation;
import com.huiwei.roomreservation.common.CommonConstant;
import com.huiwei.roomreservation.view.LoadingView;
import com.huiwei.roomreservation.view.SearchView;
import com.huiwei.roomreservation.view.ViewPagerListView;
import com.huiwei.roomreservationlib.data.Constant;
import com.huiwei.roomreservationlib.data.Data;
import com.huiwei.roomreservationlib.task.StoreListTask;

public class StoreListActivity extends Activity implements OnClickListener {
	
	private Button recommend, around, favorite;
	private Button favoriteTip;
	private Timer timer;
	private SearchView searchView;
	private int classifyID;
	private boolean slided = false;
	private int screenWidth;
	private ViewPager viewPager;
	private List<View> viewList = new ArrayList<View>();
	private LoadingView loadingView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_store_list);
		
		screenWidth = getResources().getDisplayMetrics().widthPixels;
		loadingView = (LoadingView)findViewById(R.id.loading);
		searchView = (SearchView)findViewById(R.id.view_search);
		favoriteTip = (Button)findViewById(R.id.iv_favorite_icn);
		viewPager = (ViewPager)findViewById(R.id.viewPager);
		
		classifyID = getIntent().getIntExtra("classify_id", 0);
		String classifyName = getIntent().getStringExtra("classify_name");
		((TextView)findViewById(R.id.tv_title)).setText(classifyName);
		
		((ImageView)findViewById(R.id.iv_return)).setOnClickListener(this);
		((ImageButton)findViewById(R.id.btn_search)).setOnClickListener(this);
		
		Data.searchInfo.classifyID = classifyID;
		Data.searchInfo.fromSearch = false;
		
		initButtons();
		initViews();
		
		searchView.setHandler(searchHandler);
		searchView.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				// TODO Auto-generated method stub
				return searchView.getVisibility() == View.VISIBLE;
			}
		});
	}
	
	private void initButtons() {
		recommend = (Button) findViewById(R.id.btn_recommend);
		recommend.setBackgroundResource(R.drawable.top_tab_down);
		recommend.setOnClickListener(this);

		around = (Button) findViewById(R.id.btn_around);
		around.setOnClickListener(this);
		
		favorite = (Button) findViewById(R.id.btn_favorite);
		favorite.setOnClickListener(this);
	}
	
	private void initViews() {
		addView(ViewPagerListView.TYPE_RECOMMENT);
		addView(ViewPagerListView.TYPE_AROUND);
		addView(ViewPagerListView.TYPE_FAVORITE);
		viewPager.setAdapter(new ViewPagerAdapter(viewList));
		viewPager.setCurrentItem(0);
		viewPager.setOnPageChangeListener(pageChangeListener);
	}
	
	private void addView(int type) {
		ViewPagerListView listView = (ViewPagerListView) LayoutInflater.from(
				this).inflate(R.layout.view_pager_list_view, null);
		listView.setListType(type, handler);
		viewList.add(listView);
	}
	
	private OnPageChangeListener pageChangeListener = new OnPageChangeListener() {

		@Override
		public void onPageSelected(int index) {
			resetTabButton(index);
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
			// TODO Auto-generated method stub

		}
	};
	
	private void resetTabButton(int index) {
		recommend.setBackgroundResource(R.drawable.top_tab_up);
		around.setBackgroundResource(R.drawable.top_tab_up);
		favorite.setBackgroundResource(R.drawable.top_tab_up);
		viewPager.setCurrentItem(index);
		if (index == 0) {
			recommend.setBackgroundResource(R.drawable.top_tab_down);
		} else if(index == 1) {
			around.setBackgroundResource(R.drawable.top_tab_down);
		} else {
			favorite.setBackgroundResource(R.drawable.top_tab_down);
		}
	}
	
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if (msg.what == Constant.SUCCESS) {
				boolean isCollect = msg.arg1 == 1;
				Data.collectStore((String)msg.obj, isCollect);
				
				favoriteTip.setVisibility(View.VISIBLE);
				favoriteTip.setText(getResources().getString(isCollect ? 
						R.string.collect_success : R.string.collect_cancel));
				setTimer();
				((ViewPagerListView)viewList.get(0)).refreshListView();
				((ViewPagerListView)viewList.get(1)).refreshListView();
				((ViewPagerListView)viewList.get(2)).reloadStoreList();
				loadingView.setVisibility(View.GONE);
			} else if (msg.what == Constant.DATA_ERROR) {
				Toast.makeText(StoreListActivity.this, (String)msg.obj, Toast.LENGTH_SHORT).show();
				loadingView.setVisibility(View.GONE);
			} else  if (msg.what == CommonConstant.CANCEL_TIMER) {
				favoriteTip.setVisibility(View.GONE);         
            	timer.cancel();
			} else if (msg.what == CommonConstant.START_COLLECT) {
				loadingView.setLoadingText(getResources().getString(R.string.submiting));
				loadingView.setVisibility(View.VISIBLE);
			} else {
				loadingView.setVisibility(View.GONE);
			}
		};
	};
	
	private void setTimer() { 
		timer = new Timer();
		timer.schedule(new TimerTask() {              
		            @Override  
		            public void run() {   
		            	handler.sendEmptyMessage(CommonConstant.CANCEL_TIMER);
		            	
		            }   
		        }, 1*1000, 1*1000);   
	}
	
	@Override
	public void onResume() {
		super.onResume();
	}
	
	@Override
	public void onDestroy() {
		if(timer != null){
			timer.cancel();
		}
		
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_recommend: 
			resetTabButton(0);
			break;
		case R.id.btn_around: 
			resetTabButton(1);
			break;
			
		case R.id.btn_favorite:
			resetTabButton(2);
			break;
			
		case R.id.iv_return:
			finish();
			break;
			
		case R.id.btn_search: {
			if (slided) {
				CommonAnimation.slideIn(searchView, 
						screenWidth, animationHandler);
			} else {
				searchView.setVisibility(View.VISIBLE);
				searchView.setLongClickable(true);
				CommonAnimation.slideOut(searchView, 
						screenWidth, animationHandler);
			}
		}
			break;
			
		default:
			break;
		}
	}
	
	private Handler searchHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			CommonAnimation.slideIn(searchView, 
					screenWidth, animationHandler);
			for (View view : viewList) {
				((ViewPagerListView)view).reloadStoreList();
			}
		};
	};
	
	private Handler animationHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == CommonAnimation.SLIDE_OUT) {
				slided = true;
			} else if (msg.what == CommonAnimation.SLIDE_IN){
				slided = false;
				searchView.setVisibility(View.GONE);
			}
		};
	};
}
