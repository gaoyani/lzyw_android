package com.huiwei.roomreservation.activity.setting;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.huiwei.roomreservation.R;
import com.huiwei.roomreservation.view.EditInputView;
import com.huiwei.roomreservation.view.LoadingView;
import com.huiwei.roomreservationlib.data.Constant;
import com.huiwei.roomreservationlib.data.Data;
import com.huiwei.roomreservationlib.data.UrlConstant;
import com.huiwei.roomreservationlib.task.GetAuthCodeTask;
import com.huiwei.roomreservationlib.task.member.ModifyPhoneNumberTask;

public class ResetPhoneNumberActivity extends Activity implements OnClickListener {
	
	private EditInputView oldPhoneNum, newPhoneNum, safeAnswer, authCode;
	private TextView safeQuestion;
	private Button getAuthCode;
	private String authCoceSave;
	private boolean isAuthCodeInvalid = false;
	private boolean isCountDown = false;
	private LoadingView loadingView;
	private AlertDialog confirmDialog;

	private CountDownTimer countDownTimer;
	private Timer timer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reset_phone_number);
		
		loadingView = (LoadingView)findViewById(R.id.loading);
		loadingView.setLoadingText(getResources().getString(R.string.submiting));
		oldPhoneNum = (EditInputView)findViewById(R.id.view_old_phone_number);
		newPhoneNum = (EditInputView)findViewById(R.id.view_new_phone_number);
		safeAnswer = (EditInputView)findViewById(R.id.view_save_answer);
		authCode = (EditInputView)findViewById(R.id.view_auth_code);
		safeQuestion = (TextView)findViewById(R.id.tv_safe_question);
		initInputView();
		
		getAuthCode = (Button)findViewById(R.id.btn_get_auth_code);
		getAuthCode.setOnClickListener(this);
		((ImageButton)findViewById(R.id.btn_return)).setOnClickListener(this);
		((Button)findViewById(R.id.btn_save)).setOnClickListener(this);
	}
	
	private void initInputView() {
		TextView safeQuestion = (TextView)findViewById(R.id.tv_safe_question);
		safeQuestion.setText(safeQuestion.getText().toString()+Data.memberInfo.safeQuestion);
		
		oldPhoneNum.setInfo(getResources().getString(R.string.hint_old_phone_number),
				Data.memberInfo.phoneNum, InputType.TYPE_CLASS_NUMBER, R.drawable.member_phone_icon);
		newPhoneNum.setInfo(getResources().getString(R.string.hint_new_phone_number),
				"", InputType.TYPE_CLASS_NUMBER, R.drawable.member_phone_icon);
		safeAnswer.setInfo(getResources().getString(R.string.hint_safe_answer),
				"", InputType.TYPE_CLASS_TEXT, R.drawable.register_confpwd);
		authCode.setInfo(getResources().getString(R.string.hint_auth_code),
				"", InputType.TYPE_CLASS_NUMBER, R.drawable.register_captcha_icon);

		safeQuestion.setText(safeQuestion.getText()+Data.memberInfo.safeQuestion);
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
		case R.id.btn_return:
			finish();
			break;
			
		case R.id.btn_save: {
			if (checkInput()) {
				popConfirmDialog();
			}
		}
			break;
			
		case R.id.btn_get_auth_code: {
			if (isCountDown)
				return;
			
			String phoneNumber = newPhoneNum.getText().toString();
			if (phoneNumber.equals("")) {
				Toast.makeText(ResetPhoneNumberActivity.this, getResources().getString(
						R.string.please_input_phone_num), Toast.LENGTH_SHORT).show();
			} else {
				getAuthCode.setBackgroundResource(R.drawable.reservation_invalid);
				isCountDown = true;
				GetAuthCodeTask task = new GetAuthCodeTask(
						ResetPhoneNumberActivity.this, authCodeHandler, phoneNumber);
				task.execute(UrlConstant.EDIT_AUTH_CODE_URL);
			}
		}
			break;
			
		case R.id.btn_ok:{
			loadingView.setVisibility(View.VISIBLE);
			ModifyPhoneNumberTask task = new ModifyPhoneNumberTask(
					ResetPhoneNumberActivity.this, resetHandler, oldPhoneNum.getText(),
					newPhoneNum.getText(), safeAnswer.getText(), authCode.getText());
			task.execute();
			confirmDialog.dismiss();
		}
			break;
			
		case R.id.btn_cancel:
			confirmDialog.dismiss();
			break;
		
		default:
			break;
		}
	}
	
	private void popConfirmDialog() {
		if (confirmDialog == null) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setIcon(android.R.drawable.ic_dialog_info);
			View view = LayoutInflater.from(this).inflate(R.layout.confirm_info_view, null);
			((Button)view.findViewById(R.id.btn_ok)).setOnClickListener(this);
			((Button)view.findViewById(R.id.btn_cancel)).setOnClickListener(this);
			builder.setView(view);
			confirmDialog = builder.create();
		}
		
		confirmDialog.show();
	}
	
	private boolean checkInput() {
		if (oldPhoneNum.getText().toString().length() < 11) {
			Toast.makeText(ResetPhoneNumberActivity.this, getResources().getString(
					R.string.phone_number_format_error), Toast.LENGTH_SHORT).show();
			return false;
		}
		
		if (newPhoneNum.getText().toString().length() < 11) {
			Toast.makeText(ResetPhoneNumberActivity.this, getResources().getString(
					R.string.phone_number_format_error), Toast.LENGTH_SHORT).show();
			return false;
		}
		
		if (Data.memberInfo.safeQuestion.length() == 0) {
			Toast.makeText(ResetPhoneNumberActivity.this, getResources().getString(
					R.string.no_safe_question), Toast.LENGTH_SHORT).show();
			return false;
		}
		
		if (!Data.memberInfo.safeAnswer.equals(safeAnswer.getText())) {
			Toast.makeText(ResetPhoneNumberActivity.this, getResources().getString(
					R.string.safe_answer_error), Toast.LENGTH_SHORT).show();
			return false;
		}
		
		if (authCode.getText().toString().length() == 0) {
			Toast.makeText(ResetPhoneNumberActivity.this, getResources().getString(
					R.string.please_input_auth_code), Toast.LENGTH_SHORT).show();
			return false;
		}
		
		if (isAuthCodeInvalid) {
			Toast.makeText(ResetPhoneNumberActivity.this, getResources().getString(
					R.string.auth_code_invalid), Toast.LENGTH_SHORT).show();
			return false;
		}
		
		if (!authCode.getText().toString().equals(authCoceSave)) {
			Toast.makeText(ResetPhoneNumberActivity.this, getResources().getString(
					R.string.auth_code_error), Toast.LENGTH_SHORT).show();
			return false;
		}
		
		if (Data.memberInfo.safeQuestion == null || Data.memberInfo.safeQuestion.length() == 0) {
			Toast.makeText(ResetPhoneNumberActivity.this, getResources().getString(
					R.string.not_set_safe_question), Toast.LENGTH_SHORT).show();
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
				Toast.makeText(ResetPhoneNumberActivity.this, (String)(msg.obj), Toast.LENGTH_SHORT).show();
				isCountDown = false;
				getAuthCode.setBackgroundResource(R.drawable.button_blue_selector);
			} else {
				Toast.makeText(ResetPhoneNumberActivity.this, getResources().
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
	
	private Handler resetHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			if (msg.what == Constant.SUCCESS) {
				Toast.makeText(ResetPhoneNumberActivity.this, getResources().
						getString(R.string.modify_phone_number_success), Toast.LENGTH_SHORT).show();
				Data.memberInfo.phoneNum = newPhoneNum.getText();
				ResetPhoneNumberActivity.this.finish();
			} else if (msg.what == Constant.DATA_ERROR) {
				Toast.makeText(ResetPhoneNumberActivity.this, (String)(msg.obj), Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(ResetPhoneNumberActivity.this, getResources().
						getString(R.string.modify_phone_number_fail), Toast.LENGTH_SHORT).show();
			}
			loadingView.setVisibility(View.GONE);
		};
	};


}
