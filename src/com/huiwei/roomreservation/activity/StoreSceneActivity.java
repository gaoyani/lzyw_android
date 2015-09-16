package com.huiwei.roomreservation.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
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
import cat.projects.mediaplayer_ui.PushFragment;
import cat.projects.store.ShakeFragment;

import com.huiwei.roomreservation.R;
import com.huiwei.roomreservation.common.CommonAnimation;
import com.huiwei.roomreservation.common.GestureListener;
import com.huiwei.roomreservation.fragment.WebFragment;
import com.huiwei.roomreservation.view.MainMenuView;
import com.huiwei.roomreservationlib.data.Data;
import com.huiwei.roomreservationlib.info.StoreSceneInfo;
import com.luxs.fragmentutil.FragmentListener;
import com.luxs.fragmentutil.FragmentTouchListener;
import com.luxs.remotecontrol_ui.RcDirectionKeyFragment;
import com.luxs.remotecontrol_ui.RcNumberKeyFragment;
//import com.google.gson.JsonObject;

public class StoreSceneActivity extends FragmentActivity implements
		OnClickListener, FragmentListener {

	private boolean hasTabMeasured = false;
	private boolean hasMenuViewMeasured = false;
	private boolean slided = false;
	private int screenWidth;
	private int viewWidth;

	private TextView title;
	private ImageView cover;
	private MainMenuView mainMenuView;
	private RadioButton tabMain, tabShake, tabpush, tabCtrl;

	private MyGestureListener gestureListener = null;
	private FragmentTouchListener fragementTouchListener = null;
	private ImageView leftButton;

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		if (fragementTouchListener != null)
			fragementTouchListener.onFragmentTouchEvent(event);
		return super.onTouchEvent(event);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub 
		if (fragementTouchListener != null)
			fragementTouchListener.onFragmentTouchEvent(ev);
		return super.dispatchTouchEvent(ev);
	}

	private int curPage = RC_WEBIVIEW_PAGE;
	private final static int RC_WEBIVIEW_PAGE = 1;
	private final static int RC_SHAKEVIEW_PAGE = 2;
	private final static int RC_DIRECTIRONKEY_PAGE = 3;
	private final static int RC_NUMBERKEY_PAGE = 4;
	private final static int RC_PUSH_PAGE = 5;
	private Map<Integer, Fragment> fragmentMap = new HashMap<Integer, Fragment>();
	private RcDirectionKeyFragment rcDirectionFragment;
	private RcNumberKeyFragment rcNumberFragment;
	private WebFragment webFragment;
	private PushFragment pushFragment;
	private ShakeFragment shakeFragment;
	private FragmentManager fragmentManager;
	private FragmentTransaction fragmentTransaction;
	private String storeName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_store_scene);

		((ImageButton) findViewById(R.id.btn_menu)).setOnClickListener(this);
		leftButton = (ImageButton) findViewById(R.id.btn_return);
		leftButton.setOnClickListener(this);
		title = (TextView) findViewById(R.id.tv_title);
		initTabButton();
		initFragment();

		screenWidth = getResources().getDisplayMetrics().widthPixels;
		viewWidth = screenWidth / 5 * 3;
		resizeTabLayout();
		resizeMenuView();

		gestureListener = new MyGestureListener(this);
		cover = (ImageView) findViewById(R.id.iv_cover);
		// cover.setOnTouchListener(gestureListener);
		initData();
	}

	public void setFragmentTouchListener(FragmentTouchListener listener) {
		// TODO Auto-generated method stub
		fragementTouchListener = listener;

	}

	@Override
	public void onResume() {
		super.onResume();
//		 String url="http://182.92.158.59/api.php/app/remote_config";
//		 RequestCodeTask requestCodeTask=new RequestCodeTask(this);
//		 requestCodeTask.execute(url);
	}

	private void initData() {
		if (Data.storeSceneList.size() == 0) {
			storeName = getResources().getString(R.string.store_scene);
		} else if (Data.storeSceneList.size() == 1) {
			storeName = Data.storeSceneList.get(0).storeName;
			Log.i("url", Data.storeSceneList.get(0).storeUrl);
			// @luxspace
//			if (CommonFunction.getSSID(getApplication()).equals("luxstv")) {
//				webFragment.setWebUrl("http://10.10.6.1:8080/");
//			} else {
			webFragment.setStoreSceneInfo(Data.storeSceneList.get(0));
//			}
			title.setText(storeName);
		} else {
			chooseStoreScene();
		}
	}
	
	private void chooseStoreScene() {
		List<String> storeScenes = new ArrayList<String>();
		for (int i=0; i<Data.storeSceneList.size(); i++) {
			storeScenes.add(Data.storeSceneList.get(i).storeName);
		}
		new AlertDialog.Builder(this)
		.setTitle(R.string.choose_store_scene)
		.setIcon(android.R.drawable.ic_dialog_info) 
		.setCancelable(false)
		.setSingleChoiceItems((String[]) storeScenes.toArray(
				new String[storeScenes.size()]), 0, 
		  new DialogInterface.OnClickListener() {
		                            
		     public void onClick(DialogInterface dialog, int which) {
		        dialog.dismiss();
		        StoreSceneInfo info = Data.storeSceneList.get(which);
		        storeName = info.storeName; 
		        webFragment.setStoreSceneInfo(info);
		        title.setText(storeName);
		     }
		  }
		) 
		.show();
	}     

	private void resizeTabLayout() {
		final RadioGroup rg = (RadioGroup) findViewById(R.id.rg_tab);
		ViewTreeObserver vto = rg.getViewTreeObserver();
		vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
			public boolean onPreDraw() {
				if (hasTabMeasured == false) {
					int width = getResources().getDrawable(R.drawable.tab_main)
							.getIntrinsicWidth();
					int height = getResources()
							.getDrawable(R.drawable.tab_main)
							.getIntrinsicHeight();

					int marginTop = ((LinearLayout.LayoutParams) tabMain
							.getLayoutParams()).topMargin;
					int resultWidth = screenWidth / 4;
					int resultHeight = (resultWidth * height) / width
							+ marginTop;
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
		mainMenuView = (MainMenuView) findViewById(R.id.view_main_menu);
		mainMenuView.setParentPage(MainMenuView.FROM_STORE_SCENE);
		mainMenuView.setMenuName(0, R.string.app_name);
		ViewTreeObserver vto = mainMenuView.getViewTreeObserver();
		vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
			public boolean onPreDraw() {
				if (hasMenuViewMeasured == false) {
					int height = mainMenuView.getLayoutParams().height;
					RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
							viewWidth, height);
					lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
					lp.addRule(RelativeLayout.BELOW, R.id.layout_title);
					mainMenuView.setLayoutParams(lp);

					hasMenuViewMeasured = true;
				}
				return true;
			}
		});
	}

	private void initTabButton() {
		// tabMain, tabShake, tabpush, tabCtrl;
		tabMain = (RadioButton) findViewById(R.id.rb_mainStore);
		tabCtrl = (RadioButton) findViewById(R.id.rb_control);
		tabpush = (RadioButton) findViewById(R.id.rb_push);
		tabShake = (RadioButton) findViewById(R.id.rb_store_shake);
		tabMain.setOnClickListener(this);
		tabCtrl.setOnClickListener(this);
		tabpush.setOnClickListener(this);
		tabShake.setOnClickListener(this);
	}

	private void resetTabButton() {
		tabMain.setBackgroundResource(R.drawable.tab_main);
		tabCtrl.setBackgroundResource(R.drawable.tab_remoto);
		tabpush.setBackgroundResource(R.drawable.tab_push);
		tabShake.setBackgroundResource(R.drawable.tab_shake);
	}

	private void initFragment() {
		int pageType = 0;
		webFragment = new WebFragment();
		fragmentMap.put(RC_WEBIVIEW_PAGE, webFragment);
		// webViewFragment.setFragmentListener(this);

		rcDirectionFragment = new RcDirectionKeyFragment();
		fragmentMap.put(RC_DIRECTIRONKEY_PAGE, rcDirectionFragment);
		rcDirectionFragment.setFragmentListener(this);

		rcNumberFragment = new RcNumberKeyFragment();
		fragmentMap.put(RC_NUMBERKEY_PAGE, rcNumberFragment);
		rcNumberFragment.setFragmentListener(this);
		shakeFragment = new ShakeFragment();
		fragmentMap.put(RC_SHAKEVIEW_PAGE, shakeFragment);

		pushFragment = new PushFragment();
		fragmentMap.put(RC_PUSH_PAGE, pushFragment);
		// rcFileListFragment.setFragmentListener(this);

		fragmentManager = getSupportFragmentManager();
		fragmentTransaction = fragmentManager.beginTransaction();
		pageType = getIntent().getIntExtra("push", RC_WEBIVIEW_PAGE);
		if (pageType == RC_WEBIVIEW_PAGE) {
			fragmentTransaction.add(R.id.layout_content, webFragment);
		} else if (pageType == RC_PUSH_PAGE) {
			resetTabButton();
			curPage = RC_PUSH_PAGE;
			fragmentTransaction.add(R.id.layout_content, pushFragment);
			tabpush.setBackgroundResource(R.drawable.tab_push_focus);
			title.setText(getResources().getString(R.string.menu_push));
			leftButton.setBackgroundResource(R.drawable.button_back_selector);
		} else if (pageType == RC_SHAKEVIEW_PAGE) {
			resetTabButton();
			curPage = RC_SHAKEVIEW_PAGE;
			fragmentTransaction.add(R.id.layout_content, shakeFragment);
			tabShake.setBackgroundResource(R.drawable.tab_shake_down);
			title.setText(getResources().getString(R.string.menu_shake));
			leftButton.setBackgroundResource(R.drawable.button_back_selector);
		} else if (pageType == RC_DIRECTIRONKEY_PAGE) {
			resetTabButton();
			curPage = RC_DIRECTIRONKEY_PAGE;
			fragmentTransaction.add(R.id.layout_content, rcDirectionFragment);
			tabCtrl.setBackgroundResource(R.drawable.tab_remoto_focus);
			title.setText(getResources().getString(R.string.menu_control));
			leftButton.setBackgroundResource(R.drawable.button_back_selector);
		}
		fragmentTransaction.commit();
	}

	private void switchFragment(int pageKey) {
		fragmentTransaction = fragmentManager.beginTransaction();

		for (Map.Entry entry : fragmentMap.entrySet()) {
			int key = (Integer) entry.getKey();
			if (key == pageKey) {
				fragmentTransaction.replace(R.id.layout_content,
						fragmentMap.get(key));
			}
		}

		if (pageKey != curPage) {
			fragmentTransaction.commit();
			curPage = pageKey;
		}
	}

	private Handler animationHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == CommonAnimation.SLIDE_OUT) {
				slided = true;
			} else if (msg.what == CommonAnimation.SLIDE_IN) {
				slided = false;
				mainMenuView.setVisibility(View.GONE);
			}
		};
	};

	@Override
	public void onDestroy() {

		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_menu:
			slideMenu(slided ? false : true);
			break;
		case R.id.btn_return:
			finish();
			break;
		case R.id.rb_control:
			toRcDirectionPage();
			break;
		case R.id.rb_push:
			toRcPushPage();
			break;
		case R.id.rb_store_shake:
			toShakePage();
			break;
		case R.id.rb_mainStore:
			toMainPage();
			break;
		default:
			break;
		}
	}

	public void slideMenu(boolean isOut) {
		if (slided && !isOut) {
			CommonAnimation.slideIn(mainMenuView, viewWidth, animationHandler);
			cover.setLongClickable(false);
			cover.setOnTouchListener(null);
			cover.setVisibility(View.GONE);
		} else if (!slided && isOut) {
			mainMenuView.setVisibility(View.VISIBLE);
			CommonAnimation.slideOut(mainMenuView, viewWidth, animationHandler);
			cover.setVisibility(View.VISIBLE);
			cover.setLongClickable(true);
			cover.setOnTouchListener(gestureListener);

		}
	}

	public void returnToMain() {
		finish();
	}

	public void toMainPage() {
		resetTabButton();
		switchFragment(RC_WEBIVIEW_PAGE);
		tabMain.setBackgroundResource(R.drawable.tab_main_down);
		title.setText(storeName);
		leftButton.setBackgroundResource(R.drawable.button_back_selector);
	}

	private void toShakePage() {
		resetTabButton();
		tabShake.setBackgroundResource(R.drawable.tab_shake_down);
		switchFragment(RC_SHAKEVIEW_PAGE);
		title.setText(getResources().getString(R.string.menu_shake));
		leftButton.setBackgroundResource(R.drawable.button_back_selector);
	}

	public void toRcDirectionPage() {
		resetTabButton();
		// 设置点击图标效果
		tabCtrl.setBackgroundResource(R.drawable.tab_remoto_focus);
		switchFragment(RC_DIRECTIRONKEY_PAGE);
		title.setText(getResources().getString(R.string.menu_control));
		leftButton.setBackgroundResource(R.drawable.button_back_selector);
	}

	public void toRcPushPage() {
		resetTabButton();
		// 设置点击图标效果
		tabpush.setBackgroundResource(R.drawable.tab_push_focus);
		switchFragment(RC_PUSH_PAGE);
		title.setText(getResources().getString(R.string.menu_push));
		leftButton.setBackgroundResource(R.drawable.button_back_selector);
	}

	public void toRcNumberPage() {
		switchFragment(RC_NUMBERKEY_PAGE);
	}

	@Override
	public void switchFragmentWithIn(int fragmentNumber) {
		// TODO Auto-generated method stub
		switch (fragmentNumber) {

		case RC_DIRECTIRONKEY_PAGE: {
			toRcDirectionPage();
		}
			break;
		case RC_NUMBERKEY_PAGE: {
			toRcNumberPage();
		}
			break;
		default:
			break;

		}
	}

	class MyGestureListener extends GestureListener {
		public MyGestureListener(Context context) {
			super(context);
		}

		@Override
		public void right() {
			slideMenu(false);
		}

		@Override
		public void left() {
			slideMenu(true);
		}
	}

}
