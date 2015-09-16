package com.huiwei.roomreservation.view;

import java.util.ArrayList;
import java.util.List;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;
import com.huiwei.commonlib.SyncImageLoader;
import com.huiwei.roomreservation.R;
import com.huiwei.roomreservation.adapter.PicturePageAdapter;
import com.huiwei.roomreservationlib.info.RoomInfo;
import com.huiwei.roomreservationlib.info.StoreInfo;
import com.huiwei.roomreservationlib.data.Data;

public class PictureView extends RelativeLayout {
	
	private Context context;
	private PictureIndicatorView indicatorView;
	private ProgressBar pb;
	private ImageView picture360;
	private ViewPager viewPager;
	
	private String picture360Url;
	private List<String> picUrlList;
	
	private int screenWidth;
	private boolean hasMeasured = false;
	
	public PictureView(Context context) {
		super(context);
		this.context = context;
	}
	
	public PictureView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
	}
	
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		
		screenWidth = getResources().getDisplayMetrics().widthPixels;
		
		indicatorView = (PictureIndicatorView) findViewById(R.id.view_indicator);
		pb = (ProgressBar) findViewById(R.id.pb_picture);
		
		if (indicatorView != null) {
			resetPicLayout();
		}
	}
	
	private void resetPicLayout() {
		final RelativeLayout layoutPic = (RelativeLayout) findViewById(R.id.layout_picture);
		ViewTreeObserver vto = layoutPic.getViewTreeObserver();
		vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
			public boolean onPreDraw() {
				if (hasMeasured == false) {
					int height = layoutPic.getWidth() * 2 / 3;
					RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
							LayoutParams.MATCH_PARENT, height);
					layoutPic.setLayoutParams(lp);
					hasMeasured = true;
				}
				return true;
			}
		});
	}

	public void setPicture(String pic360, List<String> picList) {
		picture360 = (ImageView) findViewById(R.id.iv_picture);
		viewPager = (ViewPager) findViewById(R.id.viewpager);

		picture360Url = pic360;
		picUrlList = picList;
		
		if (picture360Url == null
				|| picture360Url.length() == 0) {
			picture360.setVisibility(View.GONE);
			indicatorView.setVisibility(View.VISIBLE);
			indicatorView.setIndicatorNum(picUrlList.size());
			viewPager.setVisibility(View.VISIBLE);
			viewPager.setOffscreenPageLimit(3);
			viewPager.setPageMargin(10);
			List<View> views = new ArrayList<View>();
			for (int i = 1; i <= picUrlList.size(); i++) {
				View view = LayoutInflater.from(context)
						.inflate(R.layout.picture_item, null);
				views.add(view);
			}
			viewPager.setAdapter(new PicturePageAdapter(
					context, views, picUrlList));
			viewPager.setOnPageChangeListener(pageChangeListener);
		} else {
			indicatorView.setVisibility(View.GONE);
			viewPager.setVisibility(View.GONE);
			picture360.setVisibility(View.VISIBLE);
			load360Pic();
		}
	}
	
	private OnPageChangeListener pageChangeListener = new OnPageChangeListener() {

		@Override
		public void onPageScrollStateChanged(int index) {
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onPageSelected(int index) {
			indicatorView.setCurPicIndex(index);
		}

	};
	
	private void load360Pic() {
		pb.setVisibility(View.VISIBLE);
		SyncImageLoader imageLoader = new SyncImageLoader(context);
		imageLoader.loadImage(picture360Url,
				new SyncImageLoader.OnImageLoadListener() {

					@Override
					public void onImageLoad(Bitmap bitmap) {
						if (bitmap == null) {
							picture360.setImageBitmap(null);
						} else {
							int ivWidth = bitmap.getWidth()
									* picture360.getHeight()
									/ bitmap.getHeight();

							RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
									ivWidth, picture360.getHeight());
							int left = -(ivWidth - picture360.getWidth()) / 2;
							lp.setMargins(left, 0, left, 0);
							picture360.setLayoutParams(lp);

							picture360.setImageBitmap(bitmap);
							picture360.setOnTouchListener(onTouchListener);
							pb.setVisibility(View.GONE);
						}

					}

					@Override
					public void onError() {
						// TODO Auto-generated method stub

					}
				});
	}
	
	private float lastX = 0;
	private OnTouchListener onTouchListener = new OnTouchListener() {

		@SuppressLint("ClickableViewAccessibility")
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				lastX = event.getRawX();
				break;

			case MotionEvent.ACTION_MOVE: {
				float dx = event.getRawX() - lastX;
				int left = (int) (v.getLeft() + dx);
//				int right = (int) (v.getRight() + dx);
				int right = left + v.getWidth();

				if (left > 0) {
					left = 0;
					right = left + v.getWidth();
				} 

				if (right < screenWidth) {
					right = screenWidth;
					left = right - v.getWidth();
				}

				v.layout(left, v.getTop(), right, v.getBottom());
				
				lastX = event.getRawX();
			}
				break;
				
			case MotionEvent.ACTION_UP:
				break;

			default:
				break;
			}

			return true;
		}
	};
}
	
