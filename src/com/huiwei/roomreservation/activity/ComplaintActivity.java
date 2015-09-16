package com.huiwei.roomreservation.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.huiwei.roomreservation.R;
import com.huiwei.roomreservation.view.LoadingView;
import com.huiwei.roomreservation.view.TimeSlotView;
import com.huiwei.roomreservationlib.data.Constant;
import com.huiwei.roomreservationlib.data.Data;
import com.huiwei.roomreservationlib.info.ComplaintInfo;
import com.huiwei.roomreservationlib.info.TimeSlotInfo;
import com.huiwei.roomreservationlib.task.SubmitComplaintTask;

public class ComplaintActivity extends Activity implements OnClickListener {
	
	private LoadingView loadingView;
	private Button submitComplaint;
	private RelativeLayout layoutRespose;
	private EditText complaintInput;
	private ComplaintInfo complaintInfo;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_complaint);
		
		complaintInfo = new ComplaintInfo();
		loadingView = (LoadingView)findViewById(R.id.loading);
		((ImageView)findViewById(R.id.iv_return)).setOnClickListener(this);
		((TextView)findViewById(R.id.tv_store_name)).setText(
				getIntent().getStringExtra("store_name"));
		
		initNormalInfo();
	}
	
	private void initNormalInfo() {
		complaintInput = (EditText)findViewById(R.id.et_complaint);
		submitComplaint = (Button)findViewById(R.id.btn_submit_complaint);
		submitComplaint.setOnClickListener(this);
		layoutRespose = (RelativeLayout)findViewById(R.id.layout_respose);
		
		((TextView)findViewById(R.id.tv_store_name)).setText(
				Data.storeDetailInfo.name);
		((TextView)findViewById(R.id.tv_nickname)).setText(Data.memberInfo.getName());
		initReasonsView();
	}
	
	private void initReasonsView() {
		TimeSlotView view = (TimeSlotView) findViewById(R.id.view_reason);
		view.setSingleSel(true);
		view.initView(3, Data.complaintReasonList);
		view.setClickable(true);
	}
	
	private String getReason() {
		String reason = "";
		for (TimeSlotInfo info : Data.complaintReasonList) {
			if (info.isReserved) {
				reason = info.time;
				break;
			}
		}
		
		return reason;
	}
	
	private void submitTask() {
		loadingView.setVisibility(View.VISIBLE);
		loadingView.setLoadingText(getResources().getString(R.string.submiting));
		complaintInfo.reason = getReason();
		complaintInfo.content = complaintInput.getText().toString();
		SubmitComplaintTask task = new SubmitComplaintTask(this, submitHandler,
				complaintInfo);
		task.execute();
	}
	
	private Handler submitHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == Constant.SUCCESS) {		
				Toast.makeText(ComplaintActivity.this, getResources().getString(
						R.string.complaint_success), Toast.LENGTH_SHORT).show();
				layoutRespose.setVisibility(View.VISIBLE);
				complaintInfo = (ComplaintInfo)(msg.obj);
				((TextView)findViewById(R.id.tv_time)).setText(complaintInfo.time);
				((TextView)findViewById(R.id.tv_name)).setText(complaintInfo.userName);
				((TextView)findViewById(R.id.tv_respose_info)).setText(complaintInfo.note);
				submitComplaint.setBackgroundResource(R.drawable.reservation_invalid);
				submitComplaint.setText(getResources().getString(R.string.complaint_already));
				submitComplaint.setOnClickListener(null);
				
			} else if (msg.what == Constant.DATA_ERROR ){
				Toast.makeText(ComplaintActivity.this, (String)msg.obj, Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(ComplaintActivity.this, getResources().getString(
						R.string.complaint_fail), Toast.LENGTH_SHORT).show();
			}
			
			loadingView.setVisibility(View.GONE);
		};
	};
	
	
	
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

		case R.id.btn_submit_complaint:
			submitTask();
			break;

		default:
			break;
		}
		
	}
}
