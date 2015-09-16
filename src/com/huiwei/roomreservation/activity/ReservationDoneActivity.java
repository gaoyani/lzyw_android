package com.huiwei.roomreservation.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.huiwei.roomreservation.R;
import com.huiwei.roomreservation.common.RequestShareTextTask;
import com.huiwei.roomreservation.view.RoomInfoView;
import com.huiwei.roomreservationlib.data.Constant;
import com.huiwei.roomreservationlib.data.Data;
import com.huiwei.roomreservationlib.data.UrlConstant;
import com.huiwei.roomreservationlib.info.StoreDetailInfo;

public class ReservationDoneActivity extends Activity implements OnClickListener {
	
	private EditText shareEdit;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reservation_done);
		
		Data.orderDetialInfo.id = getIntent().getStringExtra("order_id");
				
		((ImageView)findViewById(R.id.iv_return)).setOnClickListener(this);
		((Button)findViewById(R.id.btn_share)).setOnClickListener(this);
		((Button)findViewById(R.id.btn_check_order)).setOnClickListener(this);
		
		String roomInfo = Data.roomInfo.otherInfo+
				Data.storeDetailInfo.service;
		int orderType = getIntent().getIntExtra("order_type", StoreDetailInfo.TYPE_ROOM);
		if (orderType == StoreDetailInfo.TYPE_ROOM) {
			((RoomInfoView)findViewById(R.id.room_info_view)).setInfo(Data.storeDetailInfo.name,
					Data.storeDetailInfo.phoneNumber, Data.roomInfo.name, roomInfo, 
					Data.roomInfo.price, Data.roomInfo.priceType,
					Data.storeDetailInfo.iconUrl);
		} else if (orderType == StoreDetailInfo.TYPE_SERVICE) {
			((RoomInfoView) findViewById(R.id.room_info_view)).setInfo(
					Data.storeDetailInfo.name, "", "服务项目",
					Data.serviceInfo.name, Data.serviceInfo.price, Data.serviceInfo.priceType,
					Data.storeDetailInfo.iconUrl);
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
		case R.id.iv_return:
			finish();
			break;
			
		case R.id.btn_share:
			RequestShareTextTask shareTextTask = new RequestShareTextTask(this, handlerShare, false);
			shareTextTask.execute(UrlConstant.SHARE_ORDER+Data.orderDetialInfo.id);
			break;
			
		case R.id.btn_check_order:
			Intent intent = new Intent();
			intent.setClass(ReservationDoneActivity.this, OrderDetailActivity.class);
			startActivity(intent);
			break;

		default:
			break;
		}
		
	}
	
	private Handler handlerShare = new Handler() {
		public void dispatchMessage(android.os.Message msg) {
			if (msg.what == Constant.SUCCESS) {
				final EditText shareEdit = new EditText(ReservationDoneActivity.this);
				shareEdit.setText((String)msg.obj);
				
				new AlertDialog.Builder(ReservationDoneActivity.this).
				setCancelable(false).
				setView(shareEdit).
				setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent shareIntent = new Intent();
						shareIntent.setAction(Intent.ACTION_SEND);
						shareIntent.putExtra(Intent.EXTRA_TEXT, shareEdit.getText().toString());
						shareIntent.setType("text/plain");
						
						startActivity(Intent.createChooser(shareIntent, 
								getResources().getString(R.string.leshare)));
					}
				}).
				setNegativeButton(R.string.cancel, null).create().show();
			} else {
				Toast.makeText(ReservationDoneActivity.this, 
						R.string.share_error, Toast.LENGTH_LONG).show();
			}
		};
	};

}
