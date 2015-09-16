package com.huiwei.roomreservation.activity.setting;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
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
import com.huiwei.roomreservationlib.task.member.ModifyNormalInfoTask;

public class SetNormalInfoActivity extends Activity implements OnClickListener {
	
	private EditInputView safeQuestion, safeAnswer, email, billTitle, address, idNumber;
	private TextView birthday;
	private ImageView idCover;
	private MemberImgView idImgView;
	private List<Button> idButtons = new ArrayList<Button>();
	private int curIDTag = MemberInfo.IDENT_ID_CARD;
	private Calendar calendar;
	private LoadingView loadingView;
	private MemberInfo tempMemberInfo;
	private AlertDialog confirmDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_set_normal_info);
		
		//hide soft keyboard
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		
		calendar = Calendar.getInstance();
		tempMemberInfo = new MemberInfo();
		
		loadingView = (LoadingView)findViewById(R.id.loading);
		loadingView.setLoadingText(getResources().getString(R.string.submiting));
		
		safeQuestion = (EditInputView)findViewById(R.id.view_safe_question);
		safeAnswer = (EditInputView)findViewById(R.id.view_safe_answer);
		email = (EditInputView)findViewById(R.id.view_email);
		billTitle = (EditInputView)findViewById(R.id.view_bill_title);
		address = (EditInputView)findViewById(R.id.view_address);
		idNumber = (EditInputView)findViewById(R.id.view_id_number);
		
		birthday = (TextView)findViewById(R.id.tv_birthday);
		idImgView = (MemberImgView)findViewById(R.id.view_member_img);
		idCover = (ImageView)findViewById(R.id.iv_cover);
		
		initIDButton();
		initInputView();
		setBirthday();
		birthday.setOnClickListener(this);
		((Button)findViewById(R.id.btn_save)).setOnClickListener(this);
		((ImageButton)findViewById(R.id.btn_return)).setOnClickListener(this);
		((Button)findViewById(R.id.btn_set_icon)).setOnClickListener(this);
	}
	
	private void initIDButton() {
		Button idCard = (Button)findViewById(R.id.btn_id_card);
		idCard.setOnClickListener(this);
		idCard.setTag(MemberInfo.IDENT_ID_CARD);
		idButtons.add(idCard);
		
		Button passport = (Button)findViewById(R.id.btn_passport);
		passport.setOnClickListener(this);
		passport.setTag(MemberInfo.IDENT_PASSPORT);
		idButtons.add(passport);
		
		Button officerCard = (Button)findViewById(R.id.btn_officer_card);
		officerCard.setOnClickListener(this);
		officerCard.setTag(MemberInfo.IDENT_OFFICER_CARD);
		idButtons.add(officerCard);
		
		Button other = (Button)findViewById(R.id.btn_other);
		other.setOnClickListener(this);
		other.setTag(MemberInfo.IDENT_OTHER);
		idButtons.add(other);
	}
	
	private void initInputView() {
		idCover.setVisibility(Data.memberInfo.identifyImg.length() != 0 ? View.VISIBLE : View.GONE);
		idImgView.setImgClickable(true);
		idImgView.loadImage(Data.memberInfo.identifyImg);
		
		safeQuestion.setInfo(getResources().getString(R.string.hint_safe_question),
				Data.memberInfo.safeQuestion, InputType.TYPE_CLASS_TEXT, R.drawable.member_pdws_icon);
		safeAnswer.setInfo(getResources().getString(R.string.hint_safe_answer),
				Data.memberInfo.safeAnswer, InputType.TYPE_CLASS_TEXT, 
				R.drawable.register_confpwd);
		email.setInfo(getResources().getString(R.string.hint_email),
				Data.memberInfo.email, InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS,
				R.drawable.member_email_icon);
		billTitle.setInfo(getResources().getString(R.string.hint_bill_title),
				Data.memberInfo.billTitile, InputType.TYPE_CLASS_TEXT, R.drawable.member_other_info_icon);
		address.setInfo(getResources().getString(R.string.hint_address),
				Data.memberInfo.address, InputType.TYPE_CLASS_TEXT, R.drawable.member_other_info_icon);
		
		String id = "";
		if (Data.memberInfo.identifyNum != null && Data.memberInfo.identifyNum.length() != 0) {
			id = Data.memberInfo.identifyNum.substring(0, 2);
			for (int i = 0; i < Data.memberInfo.identifyNum.length()-4; i++) {
				id += "*";
			}
			id += Data.memberInfo.identifyNum.substring(Data.memberInfo.identifyNum.length()-2);
		}
		
		idNumber.setInfo(getResources().getString(R.string.hint_id_number),
				id, InputType.TYPE_CLASS_TEXT, R.drawable.member_idnum_icon);
	}
	
	private void setBirthday() {
		if (Data.memberInfo.birthday.length() != 0) {
			String str[] = Data.memberInfo.birthday.split("-");
			calendar.set(Integer.valueOf(str[0]), Integer.valueOf(str[1])-1, Integer.valueOf(str[2]));
			birthday.setText(Data.memberInfo.birthday);
		}
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
		case R.id.btn_save: 
			popConfirmDialog();
			break;
			
		case R.id.btn_return:
			finish();
			break;
			
		case R.id.btn_set_icon: 
			idImgView.getPhoto();
			break;
			
		case R.id.btn_id_card:
		case R.id.btn_passport:
		case R.id.btn_officer_card:
		case R.id.btn_other: {
			for (Button btn : idButtons) {
				btn.setBackgroundResource(R.drawable.type_unselected);
			}
			v.setBackgroundResource(R.drawable.type_single_selected);
			curIDTag = (Integer) v.getTag();
		}
			break;
			
		case R.id.tv_birthday:
			showDialog(0);
			break;
			
		case R.id.btn_ok:{
			tempMemberInfo.safeQuestion = safeQuestion.getText();
			tempMemberInfo.safeAnswer = safeAnswer.getText();
			tempMemberInfo.email = email.getText();
			tempMemberInfo.birthday = String.format("%d-%02d-%02d",
					calendar.get(Calendar.YEAR), (calendar.get(Calendar.MONTH)+1), 
					calendar.get(Calendar.DAY_OF_MONTH));
			tempMemberInfo.billTitile = billTitle.getText();
			tempMemberInfo.address = address.getText();
			tempMemberInfo.idType = curIDTag;
			tempMemberInfo.identifyNum = idNumber.getText();
			tempMemberInfo.identifyImg = idImgView.getImgFilePath();
			ModifyNormalInfoTask task = new ModifyNormalInfoTask(
					SetNormalInfoActivity.this, handler, tempMemberInfo);
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
	
	private Handler handler = new Handler() {
		public void dispatchMessage(android.os.Message msg) {
			if (msg.what == Constant.SUCCESS) {
				Toast.makeText(SetNormalInfoActivity.this, 
						getResources().getString(R.string.modify_info_success), 
						Toast.LENGTH_SHORT).show();
				Data.memberInfo.safeQuestion = tempMemberInfo.safeQuestion;
				Data.memberInfo.safeAnswer = tempMemberInfo.safeAnswer;
				Data.memberInfo.email = tempMemberInfo.email;
				Data.memberInfo.birthday = tempMemberInfo.birthday;
				Data.memberInfo.billTitile = tempMemberInfo.billTitile;
				Data.memberInfo.address = tempMemberInfo.address;
				Data.memberInfo.idType = tempMemberInfo.idType;
				Data.memberInfo.identifyNum = tempMemberInfo.identifyNum;
				Data.memberInfo.identifyImg = tempMemberInfo.identifyImg;
			} else if (msg.what == Constant.DATA_ERROR) {
				Toast.makeText(SetNormalInfoActivity.this, 
						(String)(msg.obj), Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(SetNormalInfoActivity.this, 
						getResources().getString(R.string.modify_info_fail), 
						Toast.LENGTH_SHORT).show();
			}
			loadingView.setVisibility(View.GONE);
		};
	};
	
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
	
	 @Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog = new DatePickerDialog(this,
				new DatePickerDialog.OnDateSetListener() {
					public void onDateSet(DatePicker dp, int year, int month,
							int dayOfMonth) {
						calendar.set(year, month, dayOfMonth);
						String text = String.format("%d-%02d-%02d", year, (month+1), dayOfMonth);
						birthday.setText(text);
					}
				}, calendar.get(Calendar.YEAR), 
				calendar.get(Calendar.MONTH), 
				calendar.get(Calendar.DAY_OF_MONTH) 
		);

		return dialog;
	}
	 
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		if (requestCode == -1)
			return;

		idImgView.getPhotoResult(requestCode, intent);

	}
}
