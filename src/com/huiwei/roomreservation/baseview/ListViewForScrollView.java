package com.huiwei.roomreservation.baseview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

import com.huiwei.roomreservation.activity.MainActivity;
import com.huiwei.roomreservation.common.GestureListener;

public class ListViewForScrollView extends ListView {
	
	private MainActivity activity;
	private boolean isForScrollView = false;
	
	public ListViewForScrollView(Context context) {
		super(context);
	}

	public ListViewForScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ListViewForScrollView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		if (isForScrollView) {
			int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
					MeasureSpec.AT_MOST);
			super.onMeasure(widthMeasureSpec, expandSpec);
		} else {
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		}
	}
	
	public void setActivity(MainActivity activity) {
		this.activity = activity;
		this.setLongClickable(true);  
		this.setOnTouchListener(new MyGestureListener(activity.getApplicationContext())); 
	}
	
	public void setForScrollView(boolean isForScrollView) {
		this.isForScrollView = isForScrollView;
	}

	class MyGestureListener extends GestureListener {  
        public MyGestureListener(Context context) {  
            super(context);  
        }  
  
        @Override  
        public void left() {  
        	activity.slideMenu(true);
        }    
    } 
	
}