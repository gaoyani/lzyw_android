package com.huiwei.roomreservation.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.huiwei.roomreservation.R;
import com.huiwei.roomreservationlib.info.TimeSlotInfo;

public class PictureIndicatorView extends RelativeLayout {
	
	private Context context;
	private LinearLayout indicators;
	private List<ImageView> indicatorList = new ArrayList<ImageView>();
	
	public PictureIndicatorView(Context context) {
		super(context);
		this.context = context;
	}
	
	public PictureIndicatorView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
	}
	
	@Override
	protected void onFinishInflate() {
		// TODO Auto-generated method stub
		super.onFinishInflate();
		
		indicators = (LinearLayout)findViewById(R.id.layout_indicator);
	}
	
	public void setIndicatorNum(int num) {
		indicators.removeAllViews();
		for (int i=0; i<num; i++) {
			ImageView iv = new ImageView(context);
			iv.setBackgroundResource(R.drawable.indicator_normal);
			indicators.addView(iv);
			indicatorList.add(iv);
		}
		
		if (num != 0) {
			indicatorList.get(0).setBackgroundResource(R.drawable.indicator_selected);
		}
	}
	
	public void setCurPicIndex(int index) {
		for (int i=0; i<indicatorList.size(); i++) {
			ImageView iv = indicatorList.get(i);
			if (index == i) {
				iv.setBackgroundResource(R.drawable.indicator_selected);
			} else {
				iv.setBackgroundResource(R.drawable.indicator_normal);
			}
		}
	}
}
	
