package com.huiwei.roomreservation.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;

import com.huiwei.roomreservation.R;

public class ControlFragment extends Fragment implements OnTouchListener, OnGestureListener {
    
	private static final int FLING_MIN_DISTANCE = 120;
    private static final int FLING_MIN_VELOCITY = 200;
	private GestureDetector gestureDetector = new GestureDetector(this);  
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_control,
				container, false);
		
		view.setOnTouchListener(this);
		
		return view;
	}
	
	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onDestroyView() {
		super.onDestroy();
	}
	
	@Override
	public void onDetach() {
		
		super.onDetach();
	}

	
    @Override  
    public boolean onTouch(View v, MotionEvent event) {  
        return gestureDetector.onTouchEvent(event);  
    }

	@Override
	public boolean onDown(MotionEvent arg0) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		//left
		 if (e1.getX() - e2.getX() > FLING_MIN_DISTANCE      
                 && Math.abs(velocityX) > FLING_MIN_VELOCITY) {      
			 int i = 0;  
         }   
    
		//right
		 if (e2.getX() - e1.getX() > FLING_MIN_DISTANCE      
                 && Math.abs(velocityX) > FLING_MIN_VELOCITY) {      
			 int i = 0;  
		 } 
		 
		//up
		 if (e1.getY() - e2.getY() > FLING_MIN_DISTANCE      
                 && Math.abs(velocityY) > FLING_MIN_VELOCITY) {      
			 int i = 0;  
		 }   
		 
		//down
		 if (e2.getY() - e1.getY() > FLING_MIN_DISTANCE      
                 && Math.abs(velocityY) > FLING_MIN_VELOCITY) {      
			 int i = 0;  
		 }   
		
		return false;
	}

	@Override
	public void onLongPress(MotionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2,
			float arg3) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShowPress(MotionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onSingleTapUp(MotionEvent arg0) {
		// TODO Auto-generated method stub
		return false;
	} 
}


