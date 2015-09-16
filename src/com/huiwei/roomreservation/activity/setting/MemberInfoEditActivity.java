package com.huiwei.roomreservation.activity.setting;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.huiwei.roomreservation.R;
import com.huiwei.roomreservation.view.EditInputView;
import com.huiwei.roomreservation.view.LoadingView;
import com.huiwei.roomreservation.view.MemberImgView;
import com.huiwei.roomreservationlib.data.Constant;
import com.huiwei.roomreservationlib.data.Data;
import com.huiwei.roomreservationlib.info.MemberInfo;
import com.huiwei.roomreservationlib.task.member.ModifyUserInfoTask;

public class MemberInfoEditActivity extends Activity implements OnClickListener {
	
	private MemberImgView memberImgView;
	private TextView phoneNum;
	private EditInputView nickname, userName, realName;
	private Button btnFemale, btnMale;
	private int sex;
	private LoadingView loadingView;
	private AlertDialog confirmDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_member_edit);
		
		//hide soft keyboard
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		
		loadingView = (LoadingView)findViewById(R.id.loading);
		loadingView.setLoadingText(getResources().getString(R.string.submiting));
		
		memberImgView = (MemberImgView)findViewById(R.id.view_member_img);
		nickname = (EditInputView)findViewById(R.id.view_nickname);
		userName = (EditInputView)findViewById(R.id.view_user_name);
		realName = (EditInputView)findViewById(R.id.view_real_name);
		phoneNum = (TextView)findViewById(R.id.tv_phone_number);
		
		((ImageView)findViewById(R.id.iv_return)).setOnClickListener(this);
		((Button)findViewById(R.id.btn_save)).setOnClickListener(this);
		((Button)findViewById(R.id.btn_set_icon)).setOnClickListener(this);
		((TextView)findViewById(R.id.tv_login_password)).setOnClickListener(this);
		((TextView)findViewById(R.id.tv_pay_password)).setOnClickListener(this);
		((TextView)findViewById(R.id.tv_other_info)).setOnClickListener(this);
		((TextView)findViewById(R.id.tv_phone_number)).setOnClickListener(this);
		((Button)findViewById(R.id.btn_login_password)).setOnClickListener(this);
		((Button)findViewById(R.id.btn_pay_password)).setOnClickListener(this);
		((Button)findViewById(R.id.btn_other_info)).setOnClickListener(this);
		((Button)findViewById(R.id.btn_phone_edit)).setOnClickListener(this);
		btnFemale = (Button)findViewById(R.id.btn_female);
		btnFemale.setOnClickListener(this);
		btnMale = (Button)findViewById(R.id.btn_male);
		btnMale.setOnClickListener(this);
		sex = Data.memberInfo.sex;
		if (sex == MemberInfo.FAMALE) {
			btnMale.setBackgroundResource(R.drawable.type_unselected);
			btnFemale.setBackgroundResource(R.drawable.type_single_selected);
		} else if (sex == MemberInfo.MALE){
			btnMale.setBackgroundResource(R.drawable.type_single_selected);
			btnFemale.setBackgroundResource(R.drawable.type_unselected);
		} 
		
		initMemberInfo();
	}
	
	private void initMemberInfo() {
		memberImgView.setImgClickable(true);
		memberImgView.loadImage(Data.memberInfo.imgUrl);
		nickname.setInfo(getResources().getString(R.string.hint_nickname),
				Data.memberInfo.nickName, InputType.TYPE_CLASS_TEXT, R.drawable.member_nickname_icon);
		userName.setInfo(getResources().getString(R.string.hint_user_name),
				Data.memberInfo.userName, InputType.TYPE_CLASS_TEXT, R.drawable.login_user_name_icon);
		realName.setInfo(getResources().getString(R.string.hint_real_name),
				Data.memberInfo.realName, InputType.TYPE_CLASS_TEXT, R.drawable.share_name_icon);
		setPhoneNumber();
	}
		
	@Override
	public void onResume() {
		setPhoneNumber();
		
		super.onResume();
	}
	
	private void setPhoneNumber() {
		phoneNum.setText(Data.memberInfo.phoneNum.replace(
				Data.memberInfo.phoneNum.substring(3, 7), "****"));
	}
	
	@Override
	public void onDestroy() {

		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_return: 
			finish();
			break;
			
		case R.id.btn_save: 
			popConfirmDialog();
			break;
			
		case R.id.btn_set_icon: 
			memberImgView.getPhoto();
			break;
			
		case R.id.btn_phone_edit: 
		case R.id.tv_phone_number: {
			Intent intent = new Intent();
			intent.setClass(MemberInfoEditActivity.this, ResetPhoneNumberActivity.class);
			startActivity(intent); 
		}
			break;
			
		case R.id.btn_other_info: 
		case R.id.tv_other_info: {
			Intent intent = new Intent();
			intent.setClass(MemberInfoEditActivity.this, SetNormalInfoActivity.class);
			startActivity(intent); 
		}
			break;
			
		case R.id.btn_login_password: 
		case R.id.tv_login_password: {
			Intent intent = new Intent();
			intent.putExtra("password_type", MemberInfo.PASSWORD_LOGIN);
			intent.setClass(MemberInfoEditActivity.this, ModifyPwdActivity.class);
			startActivity(intent); 
		}
			break;
			
		case R.id.btn_pay_password: 
		case R.id.tv_pay_password: {
			if (Data.memberInfo.isSetPayPassword) {
				Intent intent = new Intent();
				intent.putExtra("password_type", MemberInfo.PASSWORD_PAY);
				intent.setClass(MemberInfoEditActivity.this, ModifyPwdActivity.class);
				startActivity(intent); 
			} else {
				Intent intent = new Intent();
				intent.putExtra("is_set", true);
				intent.setClass(MemberInfoEditActivity.this, SetPayPwdActivity.class);
				startActivity(intent);
			}
		}
			break;
			
		case R.id.btn_female: {
			btnMale.setBackgroundResource(R.drawable.type_unselected);
			btnFemale.setBackgroundResource(R.drawable.type_single_selected);
			sex = MemberInfo.FAMALE;
		}
			break;
			
		case R.id.btn_male: {
			btnMale.setBackgroundResource(R.drawable.type_single_selected);
			btnFemale.setBackgroundResource(R.drawable.type_unselected);
			sex = MemberInfo.MALE;
		}
			break;
			
		case R.id.btn_ok:{
			loadingView.setVisibility(View.VISIBLE);
			ModifyUserInfoTask task = new ModifyUserInfoTask(
					MemberInfoEditActivity.this, handler, userName.getText(), 
					nickname.getText(), realName.getText(), memberImgView.getImgFilePath(), sex);
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
	
	private Handler handler = new Handler() {
		public void dispatchMessage(android.os.Message msg) {
			if (msg.what == Constant.SUCCESS) {
				Toast.makeText(MemberInfoEditActivity.this, 
						getResources().getString(R.string.modify_info_success), 
						Toast.LENGTH_SHORT).show();
				Data.memberInfo.userName = userName.getText();
				Data.memberInfo.nickName = nickname.getText();
				Data.memberInfo.realName = realName.getText();
				Data.memberInfo.imgUrl = memberImgView.getImgFilePath();
				Data.memberInfo.sex = sex;
			} else if (msg.what == Constant.DATA_ERROR) {
				Toast.makeText(MemberInfoEditActivity.this, 
						(String)(msg.obj), Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(MemberInfoEditActivity.this, 
						getResources().getString(R.string.modify_info_fail), 
						Toast.LENGTH_SHORT).show();
			}
			loadingView.setVisibility(View.GONE);
		};
	};
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == -1)
			return;
		
		memberImgView.getPhotoResult(requestCode, data);
	}
}
