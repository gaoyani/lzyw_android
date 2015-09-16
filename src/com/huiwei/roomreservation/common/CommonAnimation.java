/*
 * ��¼app��Ϣ��ģ��
 */

package com.huiwei.roomreservation.common;

import android.R.bool;
import android.R.integer;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;

public class CommonAnimation {
	private final static int ANIMATION_DURATION_FAST = 250;
    
    public final static int SLIDE_OUT = 1;
    public final static int SLIDE_IN = 2;
	
	public static void slideOut(View sideView, int viewWidth, final Handler handler) {
		TranslateAnimation translate = new TranslateAnimation(viewWidth,
				0, 0, 0);
		translate.setDuration(ANIMATION_DURATION_FAST);
		translate.setFillAfter(true);
		sideView.startAnimation(translate);
		sideView.getAnimation().setAnimationListener(
				new Animation.AnimationListener() {

					@Override
					public void onAnimationStart(Animation anim) {
						
					}

					@Override
					public void onAnimationRepeat(Animation animation) {

					}

					@Override
					public void onAnimationEnd(Animation anima) {
						handler.sendEmptyMessage(SLIDE_OUT);
					}
				});
	}

	public static void slideIn(View sideView, int viewWidth, final Handler handler) {
		
		TranslateAnimation translate = new TranslateAnimation(
				0, viewWidth, 0, 0);
		translate.setDuration(ANIMATION_DURATION_FAST);
//		translate.setFillAfter(true);
		sideView.clearAnimation();
		sideView.startAnimation(translate);
		sideView.getAnimation().setAnimationListener(
				new Animation.AnimationListener() {

					@Override
					public void onAnimationStart(Animation anim) {
						
					}

					@Override
					public void onAnimationRepeat(Animation animation) {

					}

					@Override
					public void onAnimationEnd(Animation anima) {
						handler.sendEmptyMessage(SLIDE_IN);
					}
				});
	}

}
