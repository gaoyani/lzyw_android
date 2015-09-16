package com.huiwei.roomreservation.activity;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.DhcpInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.format.Formatter;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.platform.comapi.map.v;
import com.huiwei.commonlib.AppVersionUpdate;
import com.huiwei.commonlib.CommonFunction;
import com.huiwei.commonlib.NetCheck;
import com.huiwei.commonlib.Preferences;
import com.huiwei.roomreservation.R;
import com.huiwei.roomreservation.activity.setting.SettingActivity;
import com.huiwei.roomreservation.common.CommonAnimation;
import com.huiwei.roomreservation.common.CommonConstant;
import com.huiwei.roomreservation.common.GestureListener;
import com.huiwei.roomreservation.common.SysApplication;
import com.huiwei.roomreservation.common.VersionUpdate;
import com.huiwei.roomreservation.fragment.MainFragment;
import com.huiwei.roomreservation.fragment.MapFragment;
import com.huiwei.roomreservation.fragment.MemberFragment;
import com.huiwei.roomreservation.fragment.ShareFragment;
import com.huiwei.roomreservation.view.CityChoseView;
import com.huiwei.roomreservation.view.MainMenuView;
import com.huiwei.roomreservationlib.data.Constant;
import com.huiwei.roomreservationlib.data.Data;
import com.huiwei.roomreservationlib.data.UrlConstant;
import com.huiwei.roomreservationlib.task.main.CheckSSIDTask;
import com.huiwei.roomreservationlib.task.main.MainPublicTask;
import com.huiwei.roomreservationlib.task.member.LoginTask;
import com.huiwei.roomreservationlib.task.member.LogoutTask;

public class MainActivity extends FragmentActivity implements OnClickListener {

    public final static int MAIN_PAGE = 0;
    public final static int MEMBER_PAGE = 1;
    public final static int SHARE_PAGE = 2;
    public final static int MAP_PAGE = 3;
    
    public final static int TO_LOGIN = 2;
    public final static int TO_STORE_SCENE = 3;
    public final static int TO_CONTRATS = 4;
    public final static int TO_MEMBER = 10;
    public final static int TO_SHARE = 11;
    public final static int TO_SEARCH = 12;
    public final static int TO_MAIN = 13;
    
    public final static int CHOSE_CITY = 14;
    
    //app verstion update
    private VersionUpdate versionUpdate;
    private boolean isCheckVersion = false;

    private int curPage = MAIN_PAGE;
    private boolean hasTabMeasured = false;
    private boolean hasMenuViewMeasured = false;
	private boolean slided = false;
	private boolean isFirstLocation = true;
	private int screenWidth;
	private int viewWidth;
	
	private MainFragment mainFragment;
	private ShareFragment shareFragment;
	private MapFragment mapFragment;
	private MemberFragment memberFragment;
	private FragmentManager fragmentManager;
	private FragmentTransaction fragmentTransaction;
	private Map<Integer, Fragment> fragmentMap = new HashMap<Integer, Fragment>();

	private ImageView cover;
	private MainMenuView mainMenuView;
	private RadioButton tabMain, tabMember, tabSearch, tabShare;
	private CityChoseView cityChoseView;
	
	private long mExitTime;
	private static final int INTERVAL = 2000;
	private MyGestureListener gestureListener = null;
	
	private CountDownTimer countDownTimer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		initTabButton();
		initFragment();
//		location();
		
		screenWidth = getResources().getDisplayMetrics().widthPixels;
		viewWidth = screenWidth/5*3;
		resizeTabLayout();
		resizeMenuView();
		cityChoseView = (CityChoseView)findViewById(R.id.view_chose_city);
		cityChoseView.setHandler(cityChoseHandler);
		
		thread.start();  //connect Network thread

		gestureListener = new MyGestureListener(this);
		cover = (ImageView)findViewById(R.id.iv_cover);
		cover.setOnTouchListener(gestureListener); 
		
		countDown();
	}
	
	private void countDown() {
		countDownTimer = new CountDownTimer(2000, 1000) {
			
			@Override
			public void onTick(long millisUntilFinished) {
			}
			
			@Override
			public void onFinish() {
				checkNet();
			}
		};
		countDownTimer.start();
	}

	private void checkNet() {
		if (NetCheck.checkNet(this)) {
			MainPublicTask task = new MainPublicTask(this, publicInfoHandler);
			task.execute();
			autoLogin();
		} else {
			Toast.makeText(this, R.string.no_network, Toast.LENGTH_LONG).show();
		}
	}
	
	private Handler publicInfoHandler  = new Handler() {
		public void dispatchMessage(Message msg) {
			if (msg.what == Constant.SUCCESS) {
				mainFragment.setDefaultCity();
				cityChoseView.initView();
//				location();
			}
		};
	};

	@Override
	public void onResume() {
		mainMenuView.setMenuName(0, R.string.menu_store);
//		checkSSID();
		isFirstLocation = true;
		location();
		
		super.onResume();
	}
	
	@Override  
	protected void onNewIntent(Intent intent) {        
	    super.onNewIntent(intent);  
	    setIntent(intent); 
//	    int page = getIntent().getIntExtra("page_flag", MAIN_PAGE);
//		switch (page) {
//		case MEMBER_PAGE:
//			toMemberPage();
//			break;
//
//		default:
//			break;
//		}
	}
	
	private void checkSSID() {
		CheckSSIDTask task = new CheckSSIDTask(this, checkSSIDHandler);
		task.execute();
	}
	
	private Handler checkSSIDHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			if (msg.what == Constant.SUCCESS) {
				if (!isCheckVersion) {
					if (versionUpdate == null) {
						versionUpdate = new VersionUpdate(MainActivity.this, appUpdateHandler);
					}
					versionUpdate.startUpdate();
				}
				isCheckVersion = true;
				mainFragment.enableStoreScene();
			} 
		};
	};
	
	private Handler appUpdateHandler = new Handler() {
		public void dispatchMessage(android.os.Message msg) {
			if (msg.what == AppVersionUpdate.NOT_UPDATE) {
				
			} else if (msg.what == AppVersionUpdate.UPDATE_COMPLETE) {
				SysApplication.isLatestVersion = true;
			}
		};
	};
	
	private void autoLogin() {
		boolean isAutoLogin = Preferences.GetBoolean(this, 
				CommonConstant.KEY_AUTO_LOGIN, false);
		if (isAutoLogin && !Data.memberInfo.isLogin) {
			LoginTask task = new LoginTask(this, loginHandler, 
					Preferences.GetString(this, CommonConstant.KEY_USER_NAME), 
					Preferences.GetString(this, CommonConstant.KEY_PASSWORD), false);
			task.execute();
		} else {
			mainFragment.startGetDataTask(false);
		}
	}
	
	private Handler loginHandler = new Handler() {
		public void dispatchMessage(Message msg) {
			if (msg.what == Constant.SUCCESS) {
				mainFragment.startGetDataTask(false);
			}
		};
	};

	private void resizeTabLayout() {
		final RadioGroup rg = (RadioGroup)findViewById(R.id.rg_tab);
		ViewTreeObserver vto = rg.getViewTreeObserver();
		vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
			public boolean onPreDraw() {
				if (hasTabMeasured == false) {
					int width = getResources().getDrawable(R.drawable.tab_main).getIntrinsicWidth();
					int height = getResources().getDrawable(R.drawable.tab_main).getIntrinsicHeight();
					
					int marginTop = ((LinearLayout.LayoutParams)tabMain.getLayoutParams()).topMargin;
					int resultWidth = screenWidth/4;
					int resultHeight = (resultWidth*height)/width+marginTop;
					RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
							screenWidth, resultHeight);
					lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
					rg.setLayoutParams(lp);

					hasTabMeasured = true;
				}
				return true;
			}
		});
	}
	
	private void resizeMenuView() {
		mainMenuView = (MainMenuView)findViewById(R.id.view_main_menu);
		mainMenuView.setParentHandler(mainMenuHandler);
		ViewTreeObserver vto = mainMenuView.getViewTreeObserver();
		vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
			public boolean onPreDraw() {
				if (hasMenuViewMeasured == false) {
					int height = mainMenuView.getLayoutParams().height;
					RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
							viewWidth, height);
					lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
					lp.setMargins(0, CommonFunction.dip2px(MainActivity.this, 50), 0, 0);
					mainMenuView.setLayoutParams(lp);

					hasMenuViewMeasured = true;
				}
				return true;
			}
		});
	}
	
	private void initTabButton() {
		tabMain = (RadioButton)findViewById(R.id.rb_main);
		tabMember = (RadioButton)findViewById(R.id.rb_member);
		tabShare = (RadioButton)findViewById(R.id.rb_share);
		tabSearch = (RadioButton)findViewById(R.id.rb_search);
		tabMain.setOnClickListener(this);
		tabMember.setOnClickListener(this);
		tabShare.setOnClickListener(this);
		tabSearch.setOnClickListener(this);
	}
	
	private void resetTabButton() {
		tabMain.setBackgroundResource(R.drawable.tab_main);
		tabMember.setBackgroundResource(R.drawable.tab_member);
		tabShare.setBackgroundResource(R.drawable.tab_share);
		tabSearch.setBackgroundResource(R.drawable.tab_search);
	}
	
	private void initFragment() {
		mainFragment = new MainFragment();
		mainFragment.setParentHandler(mainMenuHandler);
		fragmentMap.put(MAIN_PAGE, mainFragment);
		
		memberFragment = new MemberFragment();
		memberFragment.setParentHandler(mainMenuHandler);
		fragmentMap.put(MEMBER_PAGE, memberFragment);
		
		shareFragment = new ShareFragment();
		shareFragment.setParentHandler(mainMenuHandler);
		fragmentMap.put(SHARE_PAGE, shareFragment);
		
		mapFragment = new MapFragment();
		mapFragment.setParentHandler(mainMenuHandler);
		fragmentMap.put(MAP_PAGE, mapFragment);

		fragmentManager = getSupportFragmentManager();
		fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.add(R.id.layout_content, mainFragment);
		fragmentTransaction.commit();
	}
	
	@Override
	public void onBackPressed() {
		if (System.currentTimeMillis() - mExitTime > INTERVAL) {
			if (cityChoseView.getVisibility() == View.VISIBLE) {
				cityChoseView.setVisibility(View.GONE);
			} else {
				Toast.makeText(this, getResources().getString(R.string.exit_app), 
						Toast.LENGTH_SHORT).show();
				mExitTime = System.currentTimeMillis();
			}
		} else {
			LogoutTask logoutTask = new LogoutTask(MainActivity.this, null);
			logoutTask.execute();
			finish();
			System.exit(0);
		}
	}
	
	@Override
	public void onDestroy() {
		thread.stop();
		
		if (countDownTimer != null)
			countDownTimer.cancel();
		
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		
		switch (v.getId()) {
		case R.id.btn_search: {
			if (curPage == SHARE_PAGE || curPage == MEMBER_PAGE) {
				toMainPage();
			} else {
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, MapFragment.class);
				startActivity(intent);
			}
		}
			break;
			
		case R.id.rb_main: 
			toMainPage();
			break;
			
		case R.id.rb_member: 
			toMemberPage();
			break;
			
		case R.id.rb_share: 
			toSharePage();
			break;
			
		case R.id.rb_search: 
			toSearchPage();
			break;

		default:
			break;
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		
		if (requestCode == TO_LOGIN) {
			if (resultCode != LoginActivity.RETURN) {
				toMemberPage();
				mainFragment.startGetDataTask(false);
			} else {
				toMainPage();
			}
		} else if (requestCode == TO_STORE_SCENE) {
			if(resultCode == MainActivity.TO_MEMBER) {
				toMemberPage();
			} else if (resultCode == MainActivity.TO_SHARE) {
				toSharePage();
			}
		} else if (requestCode == TO_CONTRATS) {
			if (curPage == SHARE_PAGE && resultCode == Activity.RESULT_OK) {
				shareFragment.setContract(intent);		
			}
		} else {
			if (curPage == MEMBER_PAGE) {
				memberFragment.getPhotoResult(requestCode, intent);
			}
		}
	}
	
	private Handler mainMenuHandler = new Handler() {
		public void dispatchMessage(Message msg) {
			if (msg.what == TO_MEMBER) {
				toMemberPage();
			} else if (msg.what == TO_SHARE) {
				toSharePage();
			} else if (msg.what == TO_SEARCH) {
				toSearchPage();
			} else if (msg.what == TO_MAIN) {
				toMainPage();
			} else if (msg.what == MainMenuView.SLIDE_IN){
				slideMenu(false);
			} else if (msg.what == MainMenuView.SLIDE_OUT) {
				slideMenu(true);
			} else if (msg.what == MainMenuView.SLIDE) {
				slideMenu(slided ? false : true);
			} else if (msg.what == CHOSE_CITY) {
				cityChoseView.setVisibility(View.VISIBLE);
				cityChoseView.setLongClickable(true);
			}
		};
	};
	
	private void toMainPage() {
		resetTabButton();
		tabMain.setBackgroundResource(R.drawable.tab_main_down);
		switchFragment(MAIN_PAGE);
		mainFragment.startMarquee();
	}
	
	private void toMemberPage() {
		if (Data.memberInfo.isLogin) {
			resetTabButton();
			tabMember.setBackgroundResource(R.drawable.tab_member_down);
			switchFragment(MEMBER_PAGE);
		} else {
			Intent intent = new Intent();
			intent.setClass(MainActivity.this, LoginActivity.class);
			startActivityForResult(intent, TO_LOGIN);
		}
	}
	
	private void toSharePage() {
		resetTabButton();
		tabShare.setBackgroundResource(R.drawable.tab_share_down);
		switchFragment(SHARE_PAGE);
	}
	
	private void toSearchPage() {
		resetTabButton();
		tabSearch.setBackgroundResource(R.drawable.tab_search_down);
		switchFragment(MAP_PAGE);
	}
	
	public void slideMenu(boolean isOut) {
		if (slided && !isOut) {
			CommonAnimation.slideIn(mainMenuView, 
					viewWidth, animationHandler);
			cover.setLongClickable(false);
			cover.setOnTouchListener(null);
			cover.setVisibility(View.GONE);
		} else if (!slided && isOut) {
			mainMenuView.setVisibility(View.VISIBLE);
			CommonAnimation.slideOut(mainMenuView, 
					viewWidth, animationHandler);
			cover.setVisibility(View.VISIBLE);
			cover.setLongClickable(true);
			cover.setOnTouchListener(gestureListener);
		}
	}
	
	private Handler animationHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == CommonAnimation.SLIDE_OUT) {
				slided = true;
			} else if (msg.what == CommonAnimation.SLIDE_IN){
				slided = false;
				mainMenuView.setVisibility(View.GONE);
				mainFragment.startMarquee();
			}
		};
	};
	
	private void switchFragment(int pageKey) {
		((LinearLayout)findViewById(R.id.layout_content)).setVisibility(View.GONE);
		fragmentTransaction = fragmentManager.beginTransaction();
		
		for (Map.Entry entry : fragmentMap.entrySet()) {       
		    int key = (Integer) entry.getKey( );    
		    if (key == pageKey) {
		    	fragmentTransaction.replace(R.id.layout_content, fragmentMap.get(key)); 
		    } 
		}    
		
		if (pageKey != curPage) {
			fragmentTransaction.commit();
			curPage = pageKey;
		}
		((LinearLayout)findViewById(R.id.layout_content)).setVisibility(View.VISIBLE);
	}
	
	private Thread thread = new Thread(new Runnable() {
		
		@Override
		public void run() {
			connectNet();
		}
	});
	
	private void connectNet() {
		String gateway = CommonFunction.getGetway(getApplicationContext());
		String url = UrlConstant.getNetworkUrl(gateway,
				CommonFunction.getLocalMacAddress(this));
		HttpGet request = new HttpGet(url);
		HttpResponse httpResponse;
		try {
			httpResponse = new DefaultHttpClient()
					.execute(request);
			String retSrc = EntityUtils.toString(httpResponse.getEntity());
			if (retSrc.equals("sucsess")) {
				reconnectWifi();
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
//			Looper.prepare();    
//			Toast.makeText(MainActivity.this, getResources().
//					getString(R.string.network_exception), Toast.LENGTH_LONG).show();
//			Looper.loop();  
		} catch (IOException e) {
			e.printStackTrace();
//			Looper.prepare();    
//			Toast.makeText(MainActivity.this, getResources().
//					getString(R.string.network_exception), Toast.LENGTH_LONG).show();
//			Looper.loop();  
		} 
	}
	
	void reconnectWifi() {
		WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		List<WifiConfiguration> networks = wifi.getConfiguredNetworks();

		for (WifiConfiguration wifiConfiguration : networks) {
			if (wifiConfiguration.equals(CommonFunction.getSSID(this))) {
				wifi.removeNetwork(wifiConfiguration.networkId);
				wifi.addNetwork(wifiConfiguration);
			}
		}
	}
	
	private void location() {
		final LocationClient locationClient = new LocationClient(getApplicationContext());
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);
		option.setCoorType("bd09ll");
		locationClient.setLocOption(option);
		locationClient.start();
		locationClient.requestLocation();

		locationClient.registerLocationListener(new BDLocationListener() {
			@Override
			public void onReceiveLocation(BDLocation location) {
				if (location == null && !isFirstLocation) {
					return;
				}

				Data.memberInfo.latitude = location.getLatitude();
				Data.memberInfo.longitude = location.getLongitude();
				locationClient.stop();
				checkSSID();
				mainFragment.startGetDataTask(false);
				isFirstLocation = false;
			}
		});
	}
	
	private Handler cityChoseHandler = new Handler() {
		public void dispatchMessage(Message msg) {
			if (msg.what == CityChoseView.CLOSE) {
				cityChoseView.setVisibility(View.GONE);
				cityChoseView.setLongClickable(false);
				mainFragment.setDefaultCity();
			}
		};
	};
	
	class MyGestureListener extends GestureListener {  
        public MyGestureListener(Context context) {  
            super(context);  
        }  
  
        @Override  
        public void right() {  
        	slideMenu(false);
        }  
    } 
}
