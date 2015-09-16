package com.huiwei.roomreservation.activity.setting;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.huiwei.roomreservation.R;
import com.huiwei.roomreservation.view.EditInputView;
import com.huiwei.roomreservation.view.LoadingView;
import com.huiwei.roomreservationlib.data.Constant;
import com.huiwei.roomreservationlib.data.Data;
import com.huiwei.roomreservationlib.data.UrlConstant;
import com.huiwei.roomreservationlib.task.GetAuthCodeTask;
import com.huiwei.roomreservationlib.task.member.ResetPasswordTask;

public class ResetPwdActivity extends Activity implements OnClickListener {
	
	private EditInputView phoneNum, password, confirmPwd, authCode;
	private Button getAuthCode;
	private String authCoceSave;
	private boolean isAuthCodeInvalid = false;
	private boolean isCountDown = false;
	private LoadingView loadingView;
	private CountDownTimer countDownTimer;
	private Timer timer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reset_password);
		
		loadingView = (LoadingView)findViewById(R.id.loading);
		loadingView.setLoadingText(getResources().getString(R.string.submiting));
		phoneNum = (EditInputView)findViewById(R.id.view_phone_number);
		password = (EditInputView)findViewById(R.id.view_new_password);
		confirmPwd = (EditInputView)findViewById(R.id.view_confirm_password);
		authCode = (EditInputView)findViewById(R.id.view_auth_code);
		initInputView();
		
		Button register = (Button)findViewById(R.id.btn_register);
		register.setOnClickListener(this);
		getAuthCode = (Button)findViewById(R.id.btn_get_auth_code);
		getAuthCode.setOnClickListener(this);
		ImageButton back = (ImageButton)findViewById(R.id.btn_return);
		back.setOnClickListener(this);
	}
	
	private void initInputView() {
		phoneNum.setInfo(getResources().getString(R.string.hint_phone_number),
				Data.memberInfo.phoneNum, InputType.TYPE_CLASS_NUMBER, R.drawable.login_user_name_icon);
		password.setInfo(getResources().getString(R.string.hint_new_pwd),
				"", InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD, 
				R.drawable.login_password_icon);
		confirmPwd.setInfo(getResources().getString(R.string.hint_confirm_pwd),
				"", InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD,
				R.drawable.register_confpwd);
		authCode.setInfo(getResources().getString(R.string.hint_auth_code),
				"", InputType.TYPE_CLASS_NUMBER, R.drawable.register_captcha_icon);
	}
	
	@Override
	public void onResume() {
		super.onResume();
	}
	
	@Override
	public void onDestroy() {
		if (timer != null)
			timer.cancel();
		
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_register: {
			if (checkInput()) {
				resetTask();
			}
		}
			break;
			
		case R.id.btn_return:
			finish();
			break;
			
		case R.id.btn_get_auth_code: {
			if (isCountDown)
				return;
			
			String phoneNumber = phoneNum.getText().toString();
			if (phoneNumber.equals("")) {
				Toast.makeText(ResetPwdActivity.this, getResources().getString(
						R.string.please_input_phone_num), Toast.LENGTH_SHORT).show();
			} else {
				getAuthCode.setBackgroundResource(R.drawable.reservation_invalid);
				isCountDown = true;
				GetAuthCodeTask task = new GetAuthCodeTask(
						ResetPwdActivity.this, authCodeHandler, phoneNumber);
				task.execute(UrlConstant.RESET_PASSWORD_AUTH_CODE_URL);
			}
		}
			break;
		
		default:
			break;
		}
	}
	
	private boolean checkInput() {
		if (password.getText().toString().length() < 6) {
			Toast.makeText(ResetPwdActivity.this, getResources().getString(
					R.string.password_number_error), Toast.LENGTH_SHORT).show();
			return false;
		}
		
		if (!password.getText().toString().equals(confirmPwd.getText().toString())) {
			Toast.makeText(ResetPwdActivity.this, getResources().getString(
					R.string.password_confirm_error), Toast.LENGTH_SHORT).show();
			return false;
		}
		
		if (authCode.getText().toString().length() == 0) {
			Toast.makeText(ResetPwdActivity.this, getResources().getString(
					R.string.please_input_auth_code), Toast.LENGTH_SHORT).show();
			return false;
		}
		
		if (isAuthCodeInvalid) {
			Toast.makeText(ResetPwdActivity.this, getResources().getString(
					R.string.auth_code_invalid), Toast.LENGTH_SHORT).show();
			return false;
		}
		
		if (!authCode.getText().toString().equals(authCoceSave)) {
			Toast.makeText(ResetPwdActivity.this, getResources().getString(
					R.string.auth_code_error), Toast.LENGTH_SHORT).show();
			return false;
		}
		
		return true;
	}
	
	private Handler authCodeHandler = new Handler() {
		public void dispatchMessage(android.os.Message msg) {
			if (msg.what == Constant.SUCCESS) {
				authCoceSave = (String) msg.obj;
				isAuthCodeInvalid = false;
				countDownTimer = new CountDownTimer(60000, 1000) {
					
					@Override
					public void onTick(long millisUntilFinished) {
						getAuthCode.setText(""+millisUntilFinished/1000+getResources().
								getString(R.string.second_reget));
					}
					
					@Override
					public void onFinish() {
						getAuthCode.setBackgroundResource(R.drawable.button_blue_selector);
						getAuthCode.setText(getResources().
								getString(R.string.get_auth_code));
						timer = new Timer(true);
						timer.schedule(task, 30000, 1000);
					}
				};
				countDownTimer.start();
			} else if (msg.what == Constant.DATA_ERROR) {
				Toast.makeText(ResetPwdActivity.this, (String)(msg.obj), Toast.LENGTH_SHORT).show();
				isCountDown = false;
				getAuthCode.setBackgroundResource(R.drawable.button_blue_selector);
			} else {
				Toast.makeText(ResetPwdActivity.this, getResources().
						getString(R.string.get_auth_code_fail), Toast.LENGTH_SHORT).show();
				isCountDown = false;
				getAuthCode.setBackgroundResource(R.drawable.button_blue_selector);
			}
		};
	};
	
	TimerTask task = new TimerTask() {
		public void run() {
			isAuthCodeInvalid = true;
			isCountDown = false;
		}
	};
	
	private void resetTask() {
		loadingView.setVisibility(View.VISIBLE);
		ResetPasswordTask task = new ResetPasswordTask(ResetPwdActivity.this, resetHandler, 
				phoneNum.getText().toString(), 
				password.getText().toString(), 
				authCode.getText().toString());
		task.execute();
	}
	
	private Handler resetHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			if (msg.what == Constant.SUCCESS) {
				Toast.makeText(ResetPwdActivity.this, getResources().
						getString(R.string.reset_password_success), Toast.LENGTH_SHORT).show();
				ResetPwdActivity.this.finish();
			} else if (msg.what == Constant.DATA_ERROR) {
				Toast.makeText(ResetPwdActivity.this, (String)(msg.obj), Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(ResetPwdActivity.this, getResources().
						getString(R.string.reset_password_fail), Toast.LENGTH_SHORT).show();
			}
			loadingView.setVisibility(View.GONE);
		};
	};


}
