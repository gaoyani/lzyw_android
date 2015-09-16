package com.huiwei.roomreservation.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;

import com.huiwei.roomreservation.R;
import com.huiwei.roomreservation.fragment.LoginFragment;
import com.huiwei.roomreservation.fragment.RegisterFragment;

public class LoginActivity extends FragmentActivity {

    public final static Integer LOGIN_PAGE = 0;
    public final static Integer REGISTER_PAGE = 1;
    public final static Integer RETURN = 2;
	
	private LoginFragment loginFragment;
	private RegisterFragment registerFragment;
	private FragmentManager fragmentManager;
	private FragmentTransaction fragmentTransaction;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		initFragment();
	}
	
	@Override
	public void onResume() {
		super.onResume();
	}

	
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			fragmentTransaction = fragmentManager.beginTransaction();
			
			if (msg.what == LOGIN_PAGE) {
				fragmentTransaction.show(loginFragment); 
		    	fragmentTransaction.hide(registerFragment);
			} else if (msg.what == REGISTER_PAGE){
				fragmentTransaction.show(registerFragment); 
		    	fragmentTransaction.hide(loginFragment);
			} else {
				setResult(RETURN);
				finish();
			}
			
			fragmentTransaction.commit();
		};
	};

	private void initFragment() {
		loginFragment = new LoginFragment();
		loginFragment.setHandler(handler);
		registerFragment = new RegisterFragment();
		registerFragment.setHandler(handler);

		fragmentManager = getSupportFragmentManager();
		fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.add(R.id.layout_content, loginFragment);
		fragmentTransaction.add(R.id.layout_content, registerFragment);
		fragmentTransaction.show(loginFragment);  
		fragmentTransaction.commit();
	}

	@Override
	public void onDestroy() {

		super.onDestroy();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			setResult(RETURN);
		}
		
		return super.onKeyDown(keyCode, event);
	}
}


