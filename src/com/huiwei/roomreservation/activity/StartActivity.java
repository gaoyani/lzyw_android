package com.huiwei.roomreservation.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.huiwei.commonlib.NetCheck;
import com.huiwei.roomreservation.R;

public class StartActivity extends Activity {
 	private CountDownTimer countDownTimer;
    private boolean hasViewMeasured = false;
	private ViewPager mViewPager;
	private ArrayList<View> mViews;
	private PopupWindow mPopupWindow;
	private View mPopupView;
	private MyHandler mMyHandler;
	// @luxspace
	private class MyHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			switch (msg.what) {
			case 9:
				mPopupWindow.setAnimationStyle(R.style.anim);
				mPopupWindow.showAtLocation(findViewById(R.id.iv_app_name),
						Gravity.CENTER, 0, 0);
				break;
			default:
				break;
			}
		}
	}
		private void iniView() {
		mPopupView = getLayoutInflater().inflate(R.layout.popupwindow, null);
		mViewPager = (ViewPager) mPopupView.findViewById(R.id.pager);


		mPopupWindow = new PopupWindow(mPopupView, LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT, true);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);
		//countDown();
		// checkNet();
		// @luxspace
        SharedPreferences preferences=this.getSharedPreferences("app_info", MODE_PRIVATE);
        //boolean isFirstBoot=preferences.getBoolean("first_boot", true);
        boolean isFirstBoot = false;
        Editor editor=preferences.edit();
        
		if(isFirstBoot){
		mMyHandler = new MyHandler();
		editor.putBoolean("first_boot", false);
		resizeAppName();
		iniView();
		iniViewPager(); // 初始化ViewPager
		new Thread(new Runnable() {
			@Override
			public void run() {
				mMyHandler.sendEmptyMessageDelayed(9, 2000);
			}
		}).start();
		editor.commit();
		}else {
			countDown();
		}
	}
	private void resizeAppName() {
		final ImageView appName = (ImageView) findViewById(R.id.iv_app_name);
		ViewTreeObserver vto = appName.getViewTreeObserver();
		vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
			public boolean onPreDraw() {
				if (hasViewMeasured == false) {
					Drawable drawable = getResources().getDrawable(R.drawable.launcher_text);
					int screenWidth = getResources().getDisplayMetrics().widthPixels;
					
					int width = screenWidth / 4 * 3;
					int height = width * drawable.getIntrinsicHeight() / drawable.getIntrinsicWidth();
					RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
							width, height);
					lp.addRule(RelativeLayout.CENTER_IN_PARENT);
					appName.setLayoutParams(lp);

					hasViewMeasured = true;
				}
				return true;
			}
		});
	}



	private void iniViewPager() {
		View v1 = getLayoutInflater().inflate(R.layout.layout_1, null);
		View v2 = getLayoutInflater().inflate(R.layout.layout_2, null);
		View v3 = getLayoutInflater().inflate(R.layout.layout_3, null);

//		ImageView img1 = (ImageView) v1.findViewById(R.id.img);
//		ImageView img2 = (ImageView) v2.findViewById(R.id.img2);
//		ImageView img3 = (ImageView) v3.findViewById(R.id.img3);

		// 设置图片透明度
//		img1.setAlpha(180);
//		img2.setAlpha(180);
//		img3.setAlpha(180);

		// 点击最后一张图片的立即体验后退出
		ImageView start=(ImageView) v3.findViewById(R.id.img3);
		start.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {   
				// TODO Auto-generated method stub
				//Toast.makeText(StartActivity.this, "开始体验", 1).show();
				
				goMain();
				//mPopupWindow.dismiss();
				
			}
		});
		mViews = new ArrayList<View>();
		mViews.add(v1);
		mViews.add(v2);
		mViews.add(v3);

		// 设置适配器
		mViewPager.setAdapter(new MyPagerAdapter());
		// 设置监听事件
		mViewPager.setOnPageChangeListener(new MyPagerChangeListener());
	}

	// @zhangtw
	private class MyPagerAdapter extends PagerAdapter {

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			((ViewPager) arg0).removeView(mViews.get(arg1));
		}

		@Override
		public void finishUpdate(View arg0) {

		}

		@Override
		public int getCount() {
			return mViews.size();
		}

		@Override
		public Object instantiateItem(View arg0, int arg1) {
			((ViewPager) arg0).addView(mViews.get(arg1));
			return mViews.get(arg1);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {
			for (int i = 0; i < mViews.size(); i++) {

			}
		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {

		}
	}

	// @zhangtw
	private class MyPagerChangeListener implements OnPageChangeListener {

		@Override
		public void onPageScrollStateChanged(int arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onPageSelected(int position) {
			Log.i("zj", "onPagerChange position=" + position);
		}

	}

	private void checkNet() {
		if (NetCheck.checkNet(this)) {
		//	countDown();
		} else {
			NetCheck.settingWifiNet(this, netCheckHandler);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == NetCheck.SETTING) {
		//	countDown();
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	private void countDown() {
		countDownTimer = new CountDownTimer(500, 500) {

			@Override
			public void onTick(long millisUntilFinished) {  
			}

			@Override
			public void onFinish() {
				goMain();
			}
		};
		countDownTimer.start();
	}

	private Handler netCheckHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			if (msg.what == NetCheck.CANCEL) {
				finish();
			}
		};
	};

	private void goMain() {
		Intent intent = new Intent(StartActivity.this, MainActivity.class);
		StartActivity.this.startActivity(intent);
		StartActivity.this.finish();
	}
}
