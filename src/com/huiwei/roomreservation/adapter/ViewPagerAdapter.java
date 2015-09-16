package com.huiwei.roomreservation.adapter;

import java.util.List;

import com.huiwei.roomreservation.view.ViewPagerListView;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

public class ViewPagerAdapter extends PagerAdapter {
	public List<View> mListViews;

	public ViewPagerAdapter(List<View> mListViews) {
		this.mListViews = mListViews;
	}

	@Override
	public void destroyItem(View v, int index, Object arg2) {
		((ViewPager) v).removeView(mListViews.get(index));
	}

	@Override
	public void finishUpdate(View arg0) {
	}

	@Override
	public int getCount() {
		return mListViews.size();
	}

	@Override
	public Object instantiateItem(View v, int index) {
		((ViewPager) v).addView(mListViews.get(index), 0);
		return mListViews.get(index);
	}

	@Override
	public void startUpdate(View arg0) {
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		// TODO Auto-generated method stub
		return arg0 == arg1;
	}
}
