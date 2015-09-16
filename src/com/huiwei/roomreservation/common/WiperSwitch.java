package com.huiwei.roomreservation.common;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

/**
 * 
 * @author xiaanming
 *
 */
public class WiperSwitch extends View implements OnTouchListener{
	private Bitmap bg_on, bg_off, slipper_btn;

	private float downX, nowX;
	private boolean onSlip = false;

	private boolean nowStatus = false;
	private OnChangedListener listener;
	
	
	public WiperSwitch(Context context) {
		super(context);
	}

	public WiperSwitch(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public void init(int onID, int offID, int slipperID, boolean isOff){
		bg_on = resize(BitmapFactory.decodeResource(getResources(), onID));
		bg_off = resize(BitmapFactory.decodeResource(getResources(), offID));
		slipper_btn = resize(BitmapFactory.decodeResource(getResources(), slipperID));
		setChecked(isOff);
		
		setOnTouchListener(this);
	}
	
	 private Bitmap resize(Bitmap bitmap) {
		  Matrix matrix = new Matrix(); 
		  float scale = (float)getHeight()/bitmap.getHeight();
		  matrix.postScale(scale,scale); 
		  Bitmap resizeBmp = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),
				  bitmap.getHeight(),matrix,true);
		  return resizeBmp;
	}
	
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (bg_on == null)
			return; 
		
		Log.d("WiperSwitch", "onDraw  nowX = "+nowX);
		Matrix matrix = new Matrix();
		Paint paint = new Paint();
		float x = 0;
		
		if (nowX < (bg_on.getWidth()/2)){
			canvas.drawBitmap(bg_off, matrix, paint);
		}else{
			canvas.drawBitmap(bg_on, matrix, paint);
		}
		
		if (onSlip) {
			if(nowX >= bg_on.getWidth())
				x = bg_on.getWidth() - slipper_btn.getWidth()/2;
			else
				x = nowX - slipper_btn.getWidth()/2;
		}else {
			if(nowStatus){
				x = bg_on.getWidth() - slipper_btn.getWidth();
			}else{
				x = 0;
			}
		}
		
		if (x < 0 ){
			x = 0;
		}
		else if(x > bg_on.getWidth() - slipper_btn.getWidth()){
			x = bg_on.getWidth() - slipper_btn.getWidth();
		}
		
		canvas.drawBitmap(slipper_btn, x , 0, paint); 
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch(event.getAction()){
		case MotionEvent.ACTION_DOWN:{
			if (event.getX() > bg_off.getWidth() || event.getY() > bg_off.getHeight()){
				return false;
			}else{
				onSlip = true;
				downX = event.getX();
				nowX = downX;
			}
			Log.d("WiperSwitch", "ACTION_DOWN  nowX = "+nowX);
			break;
		}
		case MotionEvent.ACTION_MOVE:{
			nowX = event.getX();
			Log.d("WiperSwitch", "ACTION_MOVE  nowX = "+nowX);
			break;
		}
		case MotionEvent.ACTION_UP:{
			onSlip = false;
			if(event.getX() >= (bg_on.getWidth()/2)){
				nowStatus = true;
				nowX = bg_on.getWidth() - slipper_btn.getWidth();
			}else{
				nowStatus = false;
				nowX = 0;
			}
			
			if(listener != null){
				listener.OnChanged(WiperSwitch.this, nowStatus);
			}
			break;
		}
		}
	
		invalidate();
		return true;
	}
	
	public void setOnChangedListener(OnChangedListener listener){
		this.listener = listener;
	}

	public void setChecked(boolean checked){
		if(checked){
			nowX = bg_on.getWidth();
		}else{
			nowX = 0;
		}
		nowStatus = checked;
		invalidate();
	}

	public interface OnChangedListener {
		public void OnChanged(WiperSwitch wiperSwitch, boolean checkState);
	}

}
