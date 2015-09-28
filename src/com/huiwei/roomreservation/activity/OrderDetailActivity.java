package com.huiwei.roomreservation.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.huiwei.roomreservation.R;
import com.huiwei.roomreservation.adapter.SubOrderItemAdapter;
import com.huiwei.roomreservation.baseview.XListView;
import com.huiwei.roomreservation.view.LoadingView;
import com.huiwei.roomreservationlib.data.Constant;
import com.huiwei.roomreservationlib.data.Data;
import com.huiwei.roomreservationlib.info.CommentInfo;
import com.huiwei.roomreservationlib.task.order.OrdeDetailTask;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class OrderDetailActivity extends Activity implements OnClickListener {
	
	private XListView subOrderListView;
	private SubOrderItemAdapter adapter;
	private LoadingView loadingView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_order_detail);
		
		loadingView = (LoadingView)findViewById(R.id.loading);
		
		((ImageView)findViewById(R.id.iv_return)).setOnClickListener(this);
		((ImageView)findViewById(R.id.rb_comment)).setOnClickListener(this);
		((ImageView)findViewById(R.id.rb_navigation)).setOnClickListener(this);
		((ImageView)findViewById(R.id.rb_share)).setOnClickListener(this);
		((ImageView)findViewById(R.id.rb_complaint)).setOnClickListener(this);
		
		initSubOrderList();
	}
	
	private void initSubOrderList() {
		subOrderListView = (XListView)findViewById(R.id.lv_sub_orders);
		subOrderListView.setForScrollView(false);
		subOrderListView.setPullLoadEnable(false);
		adapter = new SubOrderItemAdapter(this);
		subOrderListView.setAdapter(adapter);
	}
	
	@Override
	public void onResume() {
		getDataTask();
		
		super.onResume();
	}
	
	private void getDataTask() {
		loadingView.setVisibility(View.VISIBLE);
		OrdeDetailTask task = new OrdeDetailTask(this, taskHandler);
		task.execute();
	}
	
	private Handler taskHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == Constant.SUCCESS) {		
				setInfo();
				adapter.notifyDataSetChanged();
			} else {
				Toast.makeText(OrderDetailActivity.this, getResources().getString(
						R.string.load_data_fail), Toast.LENGTH_SHORT).show();
			}
			
			loadingView.setVisibility(View.GONE);
		};
	};
	
	private void setInfo() {
		((TextView)findViewById(R.id.tv_order_id)).setText(Data.orderDetialInfo.orderID);
		((TextView)findViewById(R.id.tv_store_name)).setText(Data.orderDetialInfo.storeName);
		((TextView)findViewById(R.id.tv_order_time)).setText(Data.orderDetialInfo.time);
		((TextView)findViewById(R.id.tv_phone_number)).setText(Data.orderDetialInfo.phoneNumber);
		((TextView)findViewById(R.id.tv_address)).setText(Data.orderDetialInfo.address);
		((TextView)findViewById(R.id.tv_status)).setText(Data.orderDetialInfo.state);
		((TextView)findViewById(R.id.tv_price)).setText(Data.orderDetialInfo.price);
		
		if (Data.orderDetialInfo.isNoPayment()) {
			((Button)findViewById(R.id.btn_pay)).setVisibility(View.VISIBLE);
			((Button)findViewById(R.id.btn_pay)).setOnClickListener(this);
		}
		
		if (!Data.orderDetialInfo.state.equals("已完成")) {
			((LinearLayout)findViewById(R.id.rg_tab)).setVisibility(View.GONE);
		}
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
			
		case R.id.btn_pay: {
			Intent intent = new Intent();
			intent.putExtra("order_id", Data.orderDetialInfo.id);
			intent.putExtra("order_sn", Data.orderDetialInfo.orderID);
			intent.putExtra("store_name", Data.orderDetialInfo.storeName);
			intent.putExtra("price_type", Data.orderDetialInfo.info);
			intent.putExtra("price", Data.orderDetialInfo.price);
			intent.putExtra("is_from_order", true);
			intent.setClass(OrderDetailActivity.this, PaymentChoseActivity.class);
			startActivity(intent);
		}
			break;
			
		case R.id.rb_comment: {
			Intent intent = new Intent();
			intent.putExtra("comment_type", CommentInfo.COMMENT_ORDER);
			intent.setClass(OrderDetailActivity.this, CommentActivity.class);
			startActivity(intent);
		}
			break;
			
		case R.id.rb_navigation: {
			Data.storeDetailInfo.name = Data.orderDetialInfo.storeName;
			Data.storeDetailInfo.address = Data.orderDetialInfo.address;
			Intent intent = new Intent();
			intent.setClass(OrderDetailActivity.this, NavigationActivity.class);
			startActivity(intent);
		}
			break;
			
		case R.id.rb_complaint: 
			toComplaint();
			break;
			
		case R.id.rb_share:
			break;

		default:
			break;
		}
	}
	
	private void toComplaint() {
		if (!Data.memberInfo.isLogin) {
			Toast.makeText(OrderDetailActivity.this, getResources().getString(
					R.string.unlogin), Toast.LENGTH_SHORT).show();
		} else {
			Data.storeDetailInfo.name = Data.orderDetialInfo.storeName;
			Intent intent = new Intent();
			intent.setClass(OrderDetailActivity.this, ComplaintActivity.class);
			startActivity(intent);
		}
	}
}
