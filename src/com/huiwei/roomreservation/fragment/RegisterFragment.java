package com.huiwei.roomreservation.fragment;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.huiwei.roomreservation.R;
import com.huiwei.roomreservation.activity.AboutActivity;
import com.huiwei.roomreservation.activity.AgreementActivity;
import com.huiwei.roomreservation.activity.LoginActivity;
import com.huiwei.roomreservation.common.CommonConstant;
import com.huiwei.roomreservation.view.EditInputView;
import com.huiwei.roomreservation.view.LoadingView;
import com.huiwei.roomreservationlib.data.Constant;
import com.huiwei.roomreservationlib.data.Data;
import com.huiwei.roomreservationlib.data.UrlConstant;
import com.huiwei.roomreservationlib.task.GetAuthCodeTask;
import com.huiwei.roomreservationlib.task.member.RegisterTask;

public class RegisterFragment extends Fragment implements OnClickListener {
	
	private EditInputView phoneNum, password, confirmPwd, authCode;
	private CheckBox agree;
	private TextView agreement;
	private Button getAuthCode;
	private Handler parentHandler;
	private String authCoceSave;
	private boolean isAuthCodeInvalid = false;
	private boolean isCountDown = false;
	private CountDownTimer countDownTimer;
	private Timer timer;
	private LoadingView loadingView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_register,
				container, false);
		
		loadingView = (LoadingView)view.findViewById(R.id.loading);
		loadingView.setLoadingText(getResources().getString(R.string.registering));
		phoneNum = (EditInputView)view.findViewById(R.id.view_phone_number);
		password = (EditInputView)view.findViewById(R.id.view_password);
		confirmPwd = (EditInputView)view.findViewById(R.id.view_confirm_password);
		authCode = (EditInputView)view.findViewById(R.id.view_auth_code);
		initEditInput();
		agree = (CheckBox)view.findViewById(R.id.cb_agree);
		agree.setText(getClickableSpan());
		agree.setMovementMethod(LinkMovementMethod.getInstance());
		Button register = (Button)view.findViewById(R.id.btn_register);
		register.setOnClickListener(this);
		getAuthCode = (Button)view.findViewById(R.id.btn_get_auth_code);
		getAuthCode.setOnClickListener(this);
		Button toLogin = (Button)view.findViewById(R.id.btn_login);
		toLogin.setOnClickListener(this);
		ImageButton back = (ImageButton)view.findViewById(R.id.btn_return);
		back.setOnClickListener(this);
		
		return view;
	}
	
	private void initEditInput() {
		phoneNum.setInfo(getResources().getString(R.string.hint_phone_number),
				"", InputType.TYPE_CLASS_PHONE, R.drawable.login_user_name_icon);
		password.setInfo(getResources().getString(R.string.hint_register_password),
				"", InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD, 
				R.drawable.login_password_icon);
		confirmPwd.setInfo(getResources().getString(R.string.hint_confirm_pwd),
				"", InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD, 
				R.drawable.register_confpwd);
		authCode.setInfo(getResources().getString(R.string.hint_auth_code),
				"", InputType.TYPE_CLASS_NUMBER, R.drawable.register_captcha_icon);
	}

	private SpannableString getClickableSpan() {
		View.OnClickListener l = new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.putExtra("agreement_type", CommonConstant.AGREEMENT_LOGIN);
				intent.setClass(getActivity(), AgreementActivity.class);
				getActivity().startActivity(intent);
			}
		};

		SpannableString spanableInfo = new SpannableString(agree.getText());
		String agreement = getResources().getString(R.string.agreement);
		int start = agree.getText().toString().indexOf(agreement);
		int end = agreement.length()+start;
		spanableInfo.setSpan(new Clickable(l), start, end,
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); 
		return spanableInfo;
	}

	public class Clickable extends ClickableSpan implements OnClickListener {
		private final View.OnClickListener mListener;

		public Clickable(View.OnClickListener l) {
			mListener = l;
		}

		@Override
		public void onClick(View v) {
			mListener.onClick(v);
		}
	}
	
	public void setHandler(Handler handler) {
		parentHandler = handler;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}
	
	@Override
	public void onResume() {
		super.onResume();
	}
	
	@Override
	public void onHiddenChanged(boolean hidden) {
		Log.d("MainFragment", "onHiddenChanged");
		Log.d("hidden", hidden ? "true" : "false");
		super.onHiddenChanged(hidden);
	}

	@Override
	public void onDestroyView() {
		if (timer != null)
			timer.cancel();
		
		if (countDownTimer != null)
			countDownTimer.cancel();
		
		super.onDestroy();
	}
	
	@Override
	public void onDetach() {
		
		super.onDetach();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_register: {
			if (checkInput()) {
				registerTask();
			}
		}
			break;
			
		case R.id.btn_return:
			parentHandler.sendEmptyMessage(LoginActivity.RETURN);
			break;
			
		case R.id.btn_login:
			parentHandler.sendEmptyMessage(LoginActivity.LOGIN_PAGE);
			break;
			
		case R.id.btn_get_auth_code: {
			if (isCountDown)
				return;
						
			String phoneNumber = phoneNum.getText().toString();
			if (phoneNumber.equals("")) {
				Toast.makeText(getActivity(), getResources().getString(
						R.string.please_input_phone_num), Toast.LENGTH_SHORT).show();
			} else {
				loadingView.setVisibility(View.VISIBLE);
				getAuthCode.setBackgroundResource(R.drawable.reservation_invalid);
				isCountDown = true;
				GetAuthCodeTask task = new GetAuthCodeTask(
						getActivity(), authCodeHandler, phoneNumber);
				task.execute(UrlConstant.AUTH_CODE_URL);
			}
		}
			break;
		
		default:
			break;
		}
	}
	
	private boolean checkInput() {
		if (password.getText().toString().length() < 6) {
			Toast.makeText(getActivity(), getResources().getString(
					R.string.password_number_error), Toast.LENGTH_SHORT).show();
			return false;
		}
		
		if (!password.getText().toString().equals(confirmPwd.getText().toString())) {
			Toast.makeText(getActivity(), getResources().getString(
					R.string.password_confirm_error), Toast.LENGTH_SHORT).show();
			return false;
		}
		
		if (authCode.getText().toString().length() == 0) {
			Toast.makeText(getActivity(), getResources().getString(
					R.string.please_input_auth_code), Toast.LENGTH_SHORT).show();
			return false;
		}
		
		if (isAuthCodeInvalid) {
			Toast.makeText(getActivity(), getResources().getString(
					R.string.auth_code_invalid), Toast.LENGTH_SHORT).show();
			return false;
		}
		
		if (!authCode.getText().toString().equals(authCoceSave)) {
			Toast.makeText(getActivity(), getResources().getString(
					R.string.auth_code_error), Toast.LENGTH_SHORT).show();
			return false;
		}
		
		if (!agree.isChecked()) {
			Toast.makeText(getActivity(), getResources().getString(
					R.string.please_read_agreement), Toast.LENGTH_SHORT).show();
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
						getAuthCode.setText(getResources().
								getString(R.string.get_auth_code));
						getAuthCode.setBackgroundResource(R.drawable.button_blue_selector);
						timer = new Timer(true);
						timer.schedule(task, 30000, 1000);
					}
				};
				countDownTimer.start();
			} else if (msg.what == Constant.DATA_ERROR) {
				Toast.makeText(getActivity(), (String)(msg.obj), Toast.LENGTH_SHORT).show();
				isCountDown = false;
				getAuthCode.setBackgroundResource(R.drawable.button_blue_selector);
			} else {
				Toast.makeText(getActivity(), getResources().
						getString(R.string.get_auth_code_fail), Toast.LENGTH_SHORT).show();
				isCountDown = false;
				getAuthCode.setBackgroundResource(R.drawable.button_blue_selector);
			}
			
			loadingView.setVisibility(View.GONE);
		};
	};
	
	TimerTask task = new TimerTask() {
		public void run() {
			isAuthCodeInvalid = true;
			isCountDown = false;
		}
	};
	
	private void registerTask() {
		RegisterTask task = new RegisterTask(getActivity(), registerHandler, 
				phoneNum.getText().toString(), 
				password.getText().toString(), 
				authCode.getText().toString());
		task.execute();
	}
	
	private Handler registerHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			if (msg.what == Constant.SUCCESS) {
				Toast.makeText(getActivity(), getResources().
						getString(R.string.register_success), Toast.LENGTH_SHORT).show();
				getActivity().finish();
			} else if (msg.what == Constant.DATA_ERROR) {
				Toast.makeText(getActivity(), (String)(msg.obj), Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(getActivity(), getResources().
						getString(R.string.register_fail), Toast.LENGTH_SHORT).show();
			}
			
		};
	};


}
