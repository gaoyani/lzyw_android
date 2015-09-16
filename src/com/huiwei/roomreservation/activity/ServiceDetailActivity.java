package com.huiwei.roomreservation.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.huiwei.roomreservation.R;
import com.huiwei.roomreservation.view.BottomTabView;
import com.huiwei.roomreservation.view.LoadingView;
import com.huiwei.roomreservation.view.PictureView;
import com.huiwei.roomreservationlib.data.Constant;
import com.huiwei.roomreservationlib.data.Data;
import com.huiwei.roomreservationlib.info.CommentInfo;
import com.huiwei.roomreservationlib.task.RoomDetailTask;
import com.huiwei.roomreservationlib.task.ServiceDetailTask;

public class ServiceDetailActivity extends Activity implements OnClickListener {

	private PictureView pictureView;
	private BottomTabView bottomTabView;
	private LoadingView loadingView;
	private Button reservation;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_service_detail);
		
		int roomIndex = getIntent().getIntExtra("room_index", 0);
		Data.serviceInfo = Data.storeDetailInfo.serviceList.get(roomIndex);

		loadingView = (LoadingView) findViewById(R.id.loading);
		pictureView = (PictureView) findViewById(R.id.view_picture);
		((ImageView) findViewById(R.id.iv_return)).setOnClickListener(this);
		((Button) findViewById(R.id.btn_phone_call)).setOnClickListener(this);
		reservation = (Button) findViewById(R.id.btn_reservation);
		reservation.setOnClickListener(this);

		((LinearLayout) findViewById(R.id.layout_info)).setOnClickListener(this);
		bottomTabView = (BottomTabView)findViewById(R.id.view_tab);
		bottomTabView.setCommentType(CommentInfo.COMMENT_SERVICE);
		
		getDataTask();
	}

	private void getDataTask() {
		loadingView.setVisibility(View.VISIBLE);
		ServiceDetailTask task = new ServiceDetailTask(this, taskHandler,
				Data.serviceInfo.id);
		task.execute();
	}

	private Handler taskHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == Constant.SUCCESS) {
				pictureView.setPicture(Data.serviceInfo.picture360Url, Data.serviceInfo.picUrlList);
				setInfo();
			} else {
				Toast.makeText(ServiceDetailActivity.this,
						getResources().getString(R.string.load_data_fail),
						Toast.LENGTH_SHORT).show();
			}

			loadingView.setVisibility(View.GONE);
		};
	};

	private void setInfo() {
		((TextView) findViewById(R.id.tv_name_title)).setText(Data.serviceInfo.nameTitle+"：");
		((TextView) findViewById(R.id.tv_name)).setText(Data.serviceInfo.name);
		((TextView) findViewById(R.id.tv_feature_title))
			.setText(Data.serviceInfo.otherTitle+"：");
		((TextView) findViewById(R.id.tv_feature))
				.setText(Data.serviceInfo.otherInfo);
		((TextView) findViewById(R.id.tv_consume_title))
			.setText(Data.serviceInfo.consumeTitle+"：");
		((TextView) findViewById(R.id.tv_consume))
			.setText(Data.serviceInfo.consumeValue);
		((TextView) findViewById(R.id.tv_consume_original))
			.setText(Data.serviceInfo.consumeOriginal);
		((TextView) findViewById(R.id.tv_consume_original)).getPaint()
			.setFlags(Paint. STRIKE_THRU_TEXT_FLAG); 
		
		((TextView) findViewById(R.id.tv_duration))
			.setText(Data.serviceInfo.serviceDuration);
		((TextView) findViewById(R.id.tv_time))
			.setText(Data.serviceInfo.serviceTime);
		ProgressBar pbHot = (ProgressBar) findViewById(R.id.pb_hot);
		Drawable daHot = getResources().getDrawable(R.drawable.room_hot_bg);
		int width = daHot.getIntrinsicWidth() * pbHot.getHeight()
				/ daHot.getIntrinsicHeight();
		// pbHot.setMinimumWidth(width);
		// pbHot.setMinimumHeight(minHeight);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				daHot.getIntrinsicWidth(), pbHot.getHeight());
		pbHot.setLayoutParams(lp);
		pbHot.setProgress(Data.serviceInfo.hot);

		if (!Data.serviceInfo.bookable) {
			reservation.setText(getResources().getString(
					R.string.room_booked_full_button));
			reservation.setBackgroundResource(R.drawable.reservation_invalid);
		}

		((ScrollView) findViewById(R.id.scrollview)).smoothScrollTo(0, 0);
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

		case R.id.btn_phone_call: {
			Intent intent = new Intent();
			intent.setAction("android.intent.action.DIAL");
			intent.setData(Uri.parse("tel:"
					+ Data.storeDetailInfo.phoneNumber));
			// startActivity(intent);
			startActivityForResult(intent, RESULT_OK);
		}
			break;

		case R.id.btn_reservation: {
			if (Data.memberInfo.isLogin) {
				if (Data.serviceInfo.bookable) {
					Intent intent = new Intent();
					intent.setClass(ServiceDetailActivity.this,
							ServiceReservationActivity.class);
					startActivity(intent);
				}
			} else {
				Intent intent = new Intent();
				intent.setClass(ServiceDetailActivity.this, LoginActivity.class);
				startActivityForResult(intent, MainActivity.TO_LOGIN);
				Toast.makeText(ServiceDetailActivity.this,
						getResources().getString(R.string.unlogin),
						Toast.LENGTH_SHORT).show();
			}
		}
			break;
			
		case R.id.layout_info:
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setInverseBackgroundForced(true);
			builder.setTitle("详情介绍");
			builder.setMessage(
					"商家名称："+Data.storeDetailInfo.name + "\n"
					+Data.serviceInfo.nameTitle+"："+ Data.serviceInfo.name + "\n"
					+Data.serviceInfo.consumeTitle+"："+ Data.serviceInfo.consumeValue + "\n"
					+Data.serviceInfo.otherTitle+"："+ Data.serviceInfo.otherInfo + "\n"
					+"项目时长："+ Data.serviceInfo.serviceDuration + "\n"
					+"服务时间："+ Data.serviceInfo.serviceTime + "\n"
					+"服务热度："+ Data.serviceInfo.hot);
			builder.setNeutralButton("关闭详情页",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
						}
					});
			builder.show();
			break;

		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == MainActivity.TO_LOGIN) {
			if (resultCode != LoginActivity.RETURN) {
				Intent intent = new Intent();
				intent.setClass(ServiceDetailActivity.this,
						ServiceReservationActivity.class);
				startActivity(intent);
			}
		}
	}
}
