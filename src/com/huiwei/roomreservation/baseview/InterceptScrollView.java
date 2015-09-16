package com.huiwei.roomreservation.baseview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

import com.huiwei.roomreservation.activity.MainActivity;
import com.huiwei.roomreservation.common.GestureListener;

public class InterceptScrollView extends ScrollView {
	
	private MainActivity activity;

	public InterceptScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		activity = (MainActivity) context;

		this.setLongClickable(true);  
		this.setOnTouchListener(new MyGestureListener(context)); 
	}

	public InterceptScrollView(Context context) {
		super(context);
		
		activity = (MainActivity) context;
		
		this.setLongClickable(true);  
		this.setOnTouchListener(new MyGestureListener(context)); 
	}
	
//	private float xDistance, yDistance, xLast, yLast;
//    @Override
//    public boolean onInterceptTouchEvent(MotionEvent ev) {
//        // TODO Auto-generated method stub
//         switch (ev.getAction()) {  
//         case MotionEvent.ACTION_DOWN:  
//             xDistance = yDistance = 0f;  
//             xLast = ev.getX();  
//             yLast = ev.getY();  
//             break;  
//         case MotionEvent.ACTION_MOVE:  
//             final float curX = ev.getX();  
//             final float curY = ev.getY();  
//               
//             xDistance += Math.abs(curX - xLast);  
//             yDistance += Math.abs(curY - yLast);  
//             xLast = curX;  
//             yLast = curY;  
//               
//             if(xDistance > yDistance){  
//            	 activity.slideMenu(true);
//                 return true;  
//             }    
//     }  
//
//        return super.onInterceptTouchEvent(ev);
//    }
	
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
