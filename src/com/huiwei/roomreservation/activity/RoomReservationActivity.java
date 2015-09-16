package com.huiwei.roomreservation.activity;

import java.io.Console;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.provider.ContactsContract;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TimePicker.OnTimeChangedListener;
import android.widget.TextView;

import com.baidu.mapapi.search.poi.h;
import com.huiwei.roomreservation.R;
import com.huiwei.roomreservation.view.LoadingView;
import com.huiwei.roomreservation.view.RoomInfoView;
import com.huiwei.roomreservation.view.TimeSlotView;
import com.huiwei.roomreservationlib.data.Constant;
import com.huiwei.roomreservationlib.data.Data;
import com.huiwei.roomreservationlib.info.MemberInfo;
import com.huiwei.roomreservationlib.info.ReservationInfo;
import com.huiwei.roomreservationlib.info.StoreDetailInfo;
import com.huiwei.roomreservationlib.info.TimeSlotInfo;
import com.huiwei.roomreservationlib.task.GetTimeSlotTask;
import com.huiwei.roomreservationlib.task.order.OrderModifyTask;
import com.huiwei.roomreservationlib.task.order.ReservationDetailTask;
import com.huiwei.roomreservationlib.task.pay.RoomReservationTask;

public class RoomReservationActivity extends Activity implements OnClickListener {
	public static final int RESERVATION_ROOM = 0;
	public static final int RESERVATION_ORDER = 1;
	public static final int RESERVATION_SUB_ORDER = 2;
	
	private Button today, tomorrow, afterTomorrow;
	private EditText dateTime, otherInfo, contact, phoneNum;
	private Calendar selCalendar;
	private List<TimeSlotInfo> curTimeSlotInfo = null;
	private LoadingView loadingView;
	private ProgressBar pbBar;
	private TimeSlotView resTypeView;
	private int reserationVia = RESERVATION_ROOM;
	private String subOrderId = "";
	private ReservationInfo reservationinfo = new ReservationInfo();
	private Button btnFemale, btnMale;
	private int sex;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_room_reservation);
		
		selCalendar = Calendar.getInstance();
		reserationVia = getIntent().getIntExtra("reservation_via", RESERVATION_ROOM);
		if (reserationVia == RESERVATION_SUB_ORDER)
			subOrderId = getIntent().getStringExtra("sub_order_id");
		
		pbBar = (ProgressBar)findViewById(R.id.pb_time_slot);
		loadingView = (LoadingView)findViewById(R.id.loading);
		dateTime = (EditText)findViewById(R.id.et_date);
		otherInfo = (EditText)findViewById(R.id.et_other);
		contact = (EditText)findViewById(R.id.et_name);
		phoneNum = (EditText)findViewById(R.id.et_phone);
		((ImageView)findViewById(R.id.iv_return)).setOnClickListener(this);
		((ImageView)findViewById(R.id.iv_contacts)).setOnClickListener(this);
		((ImageView)findViewById(R.id.iv_cal)).setOnClickListener(this);
		((Button)findViewById(R.id.btn_reservation)).setOnClickListener(this);
		
		today = (Button)findViewById(R.id.btn_today);
		tomorrow = (Button)findViewById(R.id.btn_tomorrow);
		afterTomorrow = (Button)findViewById(R.id.btn_after_tomorrow);
		today.setOnClickListener(this);
		tomorrow.setOnClickListener(this);
		afterTomorrow.setOnClickListener(this);
		
		setDefaultInfo();
		initTypeView();
		if (reserationVia == RESERVATION_ROOM) {
			((TextView)findViewById(R.id.tv_tips)).setText(
					Html.fromHtml(Data.storeDetailInfo.storeTips));
			String roomInfo = Data.roomInfo.otherInfo+
					" "+Data.storeDetailInfo.service;
			((RoomInfoView)findViewById(R.id.room_info_view)).setInfo(Data.storeDetailInfo.name,
					Data.storeDetailInfo.phoneNumber, Data.roomInfo.name, roomInfo, 
					Data.roomInfo.price, Data.roomInfo.priceType,
					Data.storeDetailInfo.iconUrl);
			
			selToday();
		} else {
			if (reserationVia == RESERVATION_SUB_ORDER) {
				reservationinfo.subOrderID = subOrderId;
			}
			
			getReservationInfo();
		}
	}
	
	private void setDefaultInfo() {
		contact.setText(Data.memberInfo.realName == null ? "" : Data.memberInfo.realName);
		phoneNum.setText(Data.memberInfo.phoneNum);
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
	}
	
	private void getReservationInfo() {
		loadingView.setVisibility(View.VISIBLE);
		ReservationDetailTask task = new ReservationDetailTask(this, resInfoHandler,
				subOrderId, reservationinfo);
		task.execute();
	}
	
	private Handler resInfoHandler = new Handler() {
		public void dispatchMessage(Message msg) {
			if (msg.what == Constant.SUCCESS) {
				reservationinfo = (ReservationInfo)msg.obj;
				((TextView)findViewById(R.id.tv_tips)).setText(
						Html.fromHtml(reservationinfo.storeTips));
				((RoomInfoView)findViewById(R.id.room_info_view)).setInfo(reservationinfo.storeName,
						reservationinfo.storePhone, reservationinfo.roomName, 
						reservationinfo.roomInfo, reservationinfo.price, reservationinfo.priceType,
						reservationinfo.storeIconUrl);
				setOrderInfo();
			} else if (msg.what == Constant.DATA_ERROR){
				Toast.makeText(RoomReservationActivity.this,
						(String)msg.obj, Toast.LENGTH_SHORT).show();
				finish();
			} else {
				Toast.makeText(RoomReservationActivity.this, getResources().getString(
						R.string.load_data_fail), Toast.LENGTH_SHORT).show();
			}
			
			loadingView.setVisibility(View.GONE);
		};
	};
	
	private void setOrderInfo() {
		contact.setText(reservationinfo.contacts);
		phoneNum.setText(reservationinfo.phoneNum);
		otherInfo.setText(reservationinfo.otherInfo);
		if (reserationVia == RESERVATION_SUB_ORDER) {
			contact.setKeyListener(null);
			phoneNum.setKeyListener(null);
			otherInfo.setKeyListener(null);
			resTypeView.setClickable(false);
			btnMale.setOnClickListener(null);
			btnFemale.setOnClickListener(null);
		}
		resTypeView.setSelection(reservationinfo.type);
		dateTime.setText(reservationinfo.time);
		String date[] = reservationinfo.time.split("-");
		selCalendar.set(Integer.valueOf(date[0]), Integer.valueOf(date[1])-1, 
				Integer.valueOf(date[2]), 0, 0, 0);
		setDateButton();
		getTimeSlotTask();
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
		case R.id.iv_return:
			finish();
			break;
			
		case R.id.et_date:
		case R.id.iv_cal:
			showDialog(0);
			break;
			
		case R.id.btn_reservation: {
			if (checkInput()) {
				reservation();
			}
		}
			break;
			
		case R.id.iv_contacts:
			startActivityForResult(new Intent(
		             Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI), 0);
			break;
			
		case R.id.btn_today: 
			selToday();
			break;
			
		case R.id.btn_tomorrow:
			selTomorrow();
			break;
			
		case R.id.btn_after_tomorrow: 
			selAfterTomorrow();
			break;
			
		case R.id.btn_female: {
			if (sex == MemberInfo.FAMALE) {
				btnFemale.setBackgroundResource(R.drawable.type_unselected);
				sex = 0;
			} else {
				btnMale.setBackgroundResource(R.drawable.type_unselected);
				btnFemale.setBackgroundResource(R.drawable.type_single_selected);
				sex = MemberInfo.FAMALE;
			}
		}
			break;
			
		case R.id.btn_male: {
			if (sex == MemberInfo.MALE) {
				btnMale.setBackgroundResource(R.drawable.type_unselected);
				sex = 0;
			} else {
				btnMale.setBackgroundResource(R.drawable.type_single_selected);
				btnFemale.setBackgroundResource(R.drawable.type_unselected);
				sex = MemberInfo.MALE;
			}
		}
			break;

		default:
			break;
		}
	}
	
	private void selToday() {
		today.setBackgroundResource(R.drawable.type_single_selected);
		tomorrow.setBackgroundResource(R.drawable.type_unselected);
		afterTomorrow.setBackgroundResource(R.drawable.type_unselected);
		Calendar todayCal = Calendar.getInstance();
		selCalendar.set(todayCal.get(Calendar.YEAR), todayCal.get(Calendar.MONTH), 
				todayCal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
		setCalendar();
		getTimeSlotTask();
	}
	
	private void selTomorrow() {
		tomorrow.setBackgroundResource(R.drawable.type_single_selected);
		afterTomorrow.setBackgroundResource(R.drawable.type_unselected);
		today.setBackgroundResource(R.drawable.type_unselected);
		Calendar todayCal = Calendar.getInstance();
		todayCal.set(todayCal.get(Calendar.YEAR), todayCal.get(Calendar.MONTH), 
				todayCal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
		selCalendar.setTimeInMillis(todayCal.getTimeInMillis()+ 24 * 60 * 60 * 1000 * 1);
		setCalendar();
		getTimeSlotTask();
	}
	
	private void selAfterTomorrow() {
		afterTomorrow.setBackgroundResource(R.drawable.type_single_selected);
		today.setBackgroundResource(R.drawable.type_unselected);
		tomorrow.setBackgroundResource(R.drawable.type_unselected);
		Calendar todayCal = Calendar.getInstance();
		todayCal.set(todayCal.get(Calendar.YEAR), todayCal.get(Calendar.MONTH), 
				todayCal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
		selCalendar.setTimeInMillis(todayCal.getTimeInMillis()+ 24 * 60 * 60 * 1000 * 2);
		setCalendar();
		getTimeSlotTask();
	}
	
	private boolean checkInput() {
		if (contact.getText().toString().length() == 0) {
			Toast.makeText(this, getResources().getString(
					R.string.please_input_contact_name), Toast.LENGTH_SHORT).show();
			return false;
		}
		
		if (phoneNum.getText().toString().length() == 0) {
			Toast.makeText(this, getResources().getString(
					R.string.please_input_contact_number), Toast.LENGTH_SHORT).show();
			return false;
		}
		
		return true;
	}
	
	private void reservation() {
		loadingView.setLoadingText(getResources().getString(R.string.submiting));
		loadingView.setVisibility(View.VISIBLE);
		
		reservationinfo.time = String.format("%d-%02d-%02d", selCalendar.get(Calendar.YEAR),
				(selCalendar.get(selCalendar.MONTH)+1), selCalendar.get(selCalendar.DAY_OF_MONTH));
		reservationinfo.otherInfo = otherInfo.getText().toString();
		reservationinfo.contacts = contact.getText().toString();
		reservationinfo.phoneNum = phoneNum.getText().toString();
		reservationinfo.sex = sex;
		reservationinfo.type = getTypeID();
		reservationinfo.timeList.clear();
		for (TimeSlotInfo timeSlotInfo : curTimeSlotInfo) {
			if (timeSlotInfo.isReserved) {
				reservationinfo.timeList.add(timeSlotInfo.id);
			}
		}
		
		if (reserationVia == RESERVATION_SUB_ORDER) {
			OrderModifyTask task = new OrderModifyTask(this, orderModifyHandler, reservationinfo);
			task.execute();
		} else {
			RoomReservationTask task = new RoomReservationTask(this, reservationHandler, reservationinfo);
			task.execute();
		}
	}
	
	private String getTypeID() {
		String id = "";
		for (TimeSlotInfo info : Data.resTypeList) {
			if (info.isReserved) {
				id = info.id;
				break;
			}
		}
		
		return id;
	}
	
	private Handler reservationHandler = new Handler() {
		public void dispatchMessage(Message msg) {
			if (msg.what == Constant.SUCCESS) {
				List<String> infoList = (List<String>)msg.obj;
				Intent intent = new Intent();
				intent.putExtra("order_id", infoList.get(0));
				intent.putExtra("order_sn", infoList.get(1));
				intent.putExtra("store_name", Data.storeDetailInfo.name);
				intent.putExtra("price_type", Data.roomInfo.priceType);
				intent.putExtra("price", infoList.get(2));
				intent.putExtra("is_from_order", false);
				intent.putExtra("order_type", StoreDetailInfo.TYPE_ROOM);
				intent.setClass(RoomReservationActivity.this, PaymentChoseActivity.class);
				startActivity(intent);
			} else if (msg.what == Constant.DATA_ERROR) {
				Toast.makeText(RoomReservationActivity.this,
						(String)msg.obj, Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(RoomReservationActivity.this, getResources().getString(
						R.string.upload_info_fail), Toast.LENGTH_SHORT).show();
			}
			
			loadingView.setVisibility(View.GONE);
		};
	};
	
	private Handler orderModifyHandler = new Handler() {
		public void dispatchMessage(Message msg) {
			if (msg.what == Constant.SUCCESS) {
				Toast.makeText(RoomReservationActivity.this, getResources().getString(
						R.string.sub_order_reorder_success), Toast.LENGTH_SHORT).show();
				finish();
			} else if (msg.what == Constant.DATA_ERROR) {
				Toast.makeText(RoomReservationActivity.this,
						(String)msg.obj, Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(RoomReservationActivity.this, getResources().getString(
						R.string.sub_order_reorder_fail), Toast.LENGTH_SHORT).show();
			}
			
			loadingView.setVisibility(View.GONE);
		};
	};
	
	private void initTimeSlotView() {
		TimeSlotView view = (TimeSlotView) findViewById(R.id.view_time_slot);
		if (reserationVia == RESERVATION_SUB_ORDER) {
			view.setSingleSel(true);
		}
		view.initView(3, curTimeSlotInfo);
		view.setClickable(true);
	}
	
	private void initTypeView() {
		resTypeView = (TimeSlotView) findViewById(R.id.view_type);
		resTypeView.setSingleSel(true);
		resTypeView.initView(3, Data.resTypeList);
		resTypeView.setClickable(true);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {

			Uri contactData = data.getData();

			Cursor cursor = managedQuery(contactData, null, null, null, null);
			cursor.moveToFirst();
			String username = cursor.getString(cursor
					.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
			String contactId = cursor.getString(cursor
					.getColumnIndex(ContactsContract.Contacts._ID));
			int iPhoneCnt = cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
			
			Cursor phone = getContentResolver().query(
					ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
					ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = "
							+ contactId, null, null);
			phone.moveToFirst();
			String usernumber = phone
					.getString(phone
							.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
			contact.setText(username);
			phoneNum.setText(usernumber.replace("-", ""));
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog = new DatePickerDialog(this,
				new DatePickerDialog.OnDateSetListener() {
					public void onDateSet(DatePicker dp, int year, int month,
							int dayOfMonth) {
						String text = String.format("%d-%02d-%02d", year, (month+1), dayOfMonth);
						dateTime.setText(text);
						selCalendar.set(year, month, dayOfMonth, 0, 0, 0);
						setDateButton();
						getTimeSlotTask();
					}
				}, selCalendar.get(Calendar.YEAR), 
				selCalendar.get(Calendar.MONTH), 
				selCalendar.get(Calendar.DAY_OF_MONTH) 
		);

		return dialog;
	}
	
	private void setDateButton() {
		Calendar todayCal = Calendar.getInstance();
		todayCal.set(todayCal.get(Calendar.YEAR), todayCal.get(Calendar.MONTH), 
				todayCal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);

		long intervalMilli = selCalendar.getTimeInMillis()
				- todayCal.getTimeInMillis();
		int xcts = (int) Math.round(intervalMilli / (24 * 60 * 60 * 1000));
		if (xcts == 0) {
			selToday();
		} else if (xcts == 1) {
			selTomorrow();
		} else if (xcts == 2) {
			selAfterTomorrow();
		} else {
			afterTomorrow.setBackgroundResource(R.drawable.type_unselected);
			today.setBackgroundResource(R.drawable.type_unselected);
			tomorrow.setBackgroundResource(R.drawable.type_unselected);
		}
	}
	
	private void setCalendar() {
		String text = String.format("%d-%02d-%02d", selCalendar.get(Calendar.YEAR),
				(selCalendar.get(Calendar.MARCH)+1), selCalendar.get(Calendar.DAY_OF_MONTH));
		dateTime.setText(text);
	}
	
	private void getTimeSlotTask() {
		pbBar.setVisibility(View.VISIBLE);
		String time = String.format("%d-%02d-%02d",
				selCalendar.get(Calendar.YEAR),
				(selCalendar.get(Calendar.MARCH) + 1),
				selCalendar.get(Calendar.DAY_OF_MONTH));
		if (reserationVia != RESERVATION_ROOM) {
			Data.roomInfo.id = Data.orderDetialInfo.roomID;
		}
		
		GetTimeSlotTask task = new GetTimeSlotTask(this, timeSlotHandler, time, curTimeSlotInfo);
		task.execute();
	}
	
	private Handler timeSlotHandler = new Handler() {
		@SuppressWarnings("unchecked")
		public void dispatchMessage(Message msg) {
			if (msg.what == Constant.SUCCESS) {
				curTimeSlotInfo = (List<TimeSlotInfo>) msg.obj;
				initTimeSlotView();
			} else if (msg.what == Constant.DATA_ERROR) {
				Toast.makeText(RoomReservationActivity.this,
						(String)msg.obj, Toast.LENGTH_SHORT).show();
			}
			
			pbBar.setVisibility(View.GONE);
		};
	};
}
