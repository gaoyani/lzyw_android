package com.huiwei.roomreservation.common;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.View.OnTouchListener;

public class GestureListener extends SimpleOnGestureListener implements OnTouchListener {  

    private int distance = 50;  
    private int velocity = 100;  
      
    private GestureDetector gestureDetector; 
    private MotionEvent mLastOnDownEvent;
      
    public GestureListener(Context context) {  
        super();  
        gestureDetector = new GestureDetector(context, this);  
    }  
    
    public void left() {  
    }  
      
    public void right() {   
    }  
      
    @Override
    public boolean onSingleTapUp(MotionEvent e) {
    	right();
        return true;
    }
    
    @Override
    public boolean onDown(MotionEvent e) {
    	mLastOnDownEvent = e;
    	return true;
   }
    
    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
    	return true;
   }
    
    @Override  
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,  
            float velocityY) { 
    	
    	 if (null == e1)
             e1 = mLastOnDownEvent;
    	
    	if (e1 == null || e2 == null)
    		return false;
    	
        // left
        if (e1.getX() - e2.getX() > distance && Math.abs(velocityX) > velocity && 
        		 Math.abs((e1.getX() - e2.getX())/(e1.getY() - e2.getY())) > 3) {  
            left();  
            return true; 
        }  
        // right
        if (e2.getX() - e1.getX() > distance && Math.abs(velocityX) > velocity) {  
            right();  
            return true; 
        }  
        
        return false;  
    }  
  
	@Override
	public boolean onTouch(View arg0, MotionEvent event) {
		 gestureDetector.onTouchEvent(event);
		return false;
	}
}