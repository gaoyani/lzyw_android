package com.huiwei.roomreservation.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.InputType;
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

import com.huiwei.commonlib.Preferences;
import com.huiwei.roomreservation.R;
import com.huiwei.roomreservation.activity.LoginActivity;
import com.huiwei.roomreservation.activity.setting.ResetPwdActivity;
import com.huiwei.roomreservation.common.CommonConstant;
import com.huiwei.roomreservation.view.EditInputView;
import com.huiwei.roomreservation.view.LoadingView;
import com.huiwei.roomreservationlib.data.Constant;
import com.huiwei.roomreservationlib.data.Data;
import com.huiwei.roomreservationlib.task.member.LoginTask;

public class LoginFragment extends Fragment implements OnClickListener {
	
	private EditInputView userName, password;
	private CheckBox rememberMe, autoLogin;
	private Handler parentHandler;
	private LoadingView loadingView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_login,
				container, false);
		
		loadingView = (LoadingView)view.findViewById(R.id.loading);
		rememberMe = (CheckBox)view.findViewById(R.id.cb_remember_me);
		autoLogin = (CheckBox)view.findViewById(R.id.cb_auto_login);
		Button login = (Button)view.findViewById(R.id.btn_login);
		login.setOnClickListener(this);
		
		Button register = (Button)view.findViewById(R.id.btn_register);
		register.setOnClickListener(this);
		ImageButton back = (ImageButton)view.findViewById(R.id.btn_return);
		back.setOnClickListener(this);
		TextView forgetPassword = (TextView)view.findViewById(R.id.tv_forget_password);
		forgetPassword.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		forgetPassword.setOnClickListener(this);
		
		initView(view);
		return view;
	}
	
	private void initView(View view) {
		userName = (EditInputView)view.findViewById(R.id.view_user_name);
		password = (EditInputView)view.findViewById(R.id.view_password);
		
		password.setInfo(getResources().getString(R.string.hint_password),
				"", InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD, 
				R.drawable.login_password_icon);
		
		String saveName = "";
		if (Preferences.GetBoolean(getActivity(), CommonConstant.KEY_REMEMBER_ME, false)) {
			saveName = Preferences.GetString(getActivity(), CommonConstant.KEY_USER_NAME);
		}
		userName.setInfo(getResources().getString(R.string.hint_login_name),
				saveName, InputType.TYPE_CLASS_TEXT, R.drawable.login_user_name_icon);
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
		super.onDestroy();
	}
	
	@Override
	public void onDetach() {
		
		super.onDetach();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_login: {
			if (checkInput()) {
				loadingView.setVisibility(View.VISIBLE);
				loadingView.setLoadingText(getResources().getString(R.string.logging));
				LoginTask task = new LoginTask(getActivity(), taskHandler, 
						userName.getText().toString(), password.getText().toString(), false);
				task.execute();
			}
		}
			break;
		
		case R.id.btn_return:
			parentHandler.sendEmptyMessage(LoginActivity.RETURN);
			break;
			
		case R.id.btn_register:
			parentHandler.sendEmptyMessage(LoginActivity.REGISTER_PAGE);
			break;
			
		case R.id.tv_forget_password: {
			Intent intent = new Intent();
			intent.setClass(getActivity(), ResetPwdActivity.class);
			startActivity(intent);
		}
			break;
		
		default:
			break;
		}
	}
	
	private boolean checkInput() {
		if (userName.getText().toString().length() == 0) {
			Toast.makeText(getActivity(), getResources().
					getString(R.string.please_input_login_user_name), Toast.LENGTH_SHORT).show();
			return false;
		}
		
		if (password.getText().toString().length() == 0) {
			Toast.makeText(getActivity(), getResources().
					getString(R.string.please_input_login_password), Toast.LENGTH_SHORT).show();
			return false;
		}
		
		return true;
	}
	
	private Handler taskHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			if (msg.what == Constant.SUCCESS) {
				Toast.makeText(getActivity(), getResources().
						getString(R.string.login_success), Toast.LENGTH_SHORT).show();
				Preferences.SetBoolean(getActivity(), CommonConstant.KEY_REMEMBER_ME, rememberMe.isChecked());
				Preferences.SetBoolean(getActivity(), CommonConstant.KEY_AUTO_LOGIN, autoLogin.isChecked());
				Preferences.SetString(getActivity(), CommonConstant.KEY_USER_NAME, userName.getText().toString());
				Preferences.SetString(getActivity(), CommonConstant.KEY_PASSWORD, password.getText().toString());
				getActivity().finish();
			} else if (msg.what == Constant.ALREADY_LOGIN) {
				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				builder.setMessage(R.string.already_login);
				builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						LoginTask task = new LoginTask(getActivity(), taskHandler, 
								userName.getText().toString(), password.getText().toString(), true);
						task.execute();
					}
				});
				builder.setNegativeButton(R.string.cancel, null);
				builder.create().show();
			} else if (msg.what == Constant.DATA_ERROR) {
				Toast.makeText(getActivity(), (String)msg.obj, Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(getActivity(), getResources().
						getString(R.string.login_fail), Toast.LENGTH_SHORT).show();
			}
			
			loadingView.setVisibility(View.GONE);
		};
	};


}
