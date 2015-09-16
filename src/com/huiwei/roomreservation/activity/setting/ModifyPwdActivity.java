package com.huiwei.roomreservation.activity.setting;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
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
import com.huiwei.roomreservationlib.info.MemberInfo;
import com.huiwei.roomreservationlib.task.member.ModifyPasswordTask;

public class ModifyPwdActivity extends Activity implements OnClickListener {
	
	private EditInputView oldPassword, newPassword, confirmPwd;
	private int pwdType = MemberInfo.PASSWORD_LOGIN;
	private LoadingView loadingView;
	private AlertDialog confirmDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_modify_password);
		
		pwdType = getIntent().getIntExtra("password_type", MemberInfo.PASSWORD_LOGIN);
		
		((TextView)findViewById(R.id.tv_title)).setText(getResources().getString(
						pwdType == MemberInfo.PASSWORD_LOGIN ? 
						R.string.modify_login_password: 
						R.string.modify_pay_password));
		loadingView = (LoadingView)findViewById(R.id.loading);
		loadingView.setLoadingText(getResources().getString(R.string.submiting));
		oldPassword = (EditInputView)findViewById(R.id.view_old_password);
		newPassword = (EditInputView)findViewById(R.id.view_new_password);
		confirmPwd = (EditInputView)findViewById(R.id.view_confirm_password);
		initInputView();
		
		Button save = (Button)findViewById(R.id.btn_save);
		save.setOnClickListener(this);
		ImageButton back = (ImageButton)findViewById(R.id.btn_return);
		back.setOnClickListener(this);
	}
	
	private void initInputView() {
		oldPassword.setInfo(getResources().getString(R.string.hint_old_pwd),
				"", InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD, 
				R.drawable.login_password_icon);
		newPassword.setInfo(getResources().getString(R.string.hint_new_pwd),
				"", InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD, 
				R.drawable.member_pdws_icon);
		confirmPwd.setInfo(getResources().getString(R.string.hint_confirm_pwd),
				"", InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD,
				R.drawable.register_confpwd);
	}
	
	@Override
	public void onResume() {
		super.onResume();
	}
	
	@Override
	public void onDestroy() {
		
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_save: {
			if (checkInput()) {
				popConfirmDialog();
			}
		}
			break;
			
		case R.id.btn_return:
			finish();
			break;
			
		case R.id.btn_ok:{
			loadingView.setVisibility(View.VISIBLE);
			ModifyPasswordTask task = new ModifyPasswordTask(ModifyPwdActivity.this,
					resetHandler, oldPassword.getText().toString(), 
					newPassword.getText().toString(), pwdType);
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
	
	private Handler resetHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			if (msg.what == Constant.SUCCESS) {
				Toast.makeText(ModifyPwdActivity.this, getResources().getString(
						pwdType == MemberInfo.PASSWORD_LOGIN ? 
						R.string.modify_login_password_success : 
						R.string.modify_pay_password_success), Toast.LENGTH_SHORT).show();
				ModifyPwdActivity.this.finish();
			} else if (msg.what == Constant.DATA_ERROR) {
				Toast.makeText(ModifyPwdActivity.this, 
						(String)(msg.obj), Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(ModifyPwdActivity.this, 
						getResources().getString(pwdType == MemberInfo.PASSWORD_LOGIN ? 
								R.string.modify_login_password_fail : 
								R.string.modify_pay_password_fail), Toast.LENGTH_SHORT).show();
			}
			loadingView.setVisibility(View.GONE);
		};
	};
	
	private boolean checkInput() {
		if (oldPassword.getText().toString().length() < 6) {
			Toast.makeText(ModifyPwdActivity.this, getResources().getString(
					R.string.password_number_error), Toast.LENGTH_SHORT).show();
			return false;
		}
		
		if (newPassword.getText().toString().length() < 6) {
			Toast.makeText(ModifyPwdActivity.this, getResources().getString(
					R.string.password_number_error), Toast.LENGTH_SHORT).show();
			return false;
		}
		
		if (!newPassword.getText().toString().equals(confirmPwd.getText().toString())) {
			Toast.makeText(ModifyPwdActivity.this, getResources().getString(
					R.string.password_confirm_error), Toast.LENGTH_SHORT).show();
			return false;
		}
		
		return true;
	}

}
