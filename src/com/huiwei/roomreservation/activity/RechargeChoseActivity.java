package com.huiwei.roomreservation.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.huiwei.roomreservation.R;
import com.huiwei.roomreservation.alipay.AliPayManager;
import com.huiwei.roomreservation.common.CommonConstant;
import com.huiwei.roomreservation.view.LoadingView;
import com.huiwei.roomreservationlib.data.Constant;
import com.huiwei.roomreservationlib.data.Data;
import com.huiwei.roomreservationlib.task.pay.ConfirmRechargeOrderTask;
import com.huiwei.roomreservationlib.task.pay.PaymentTask;

public class RechargeChoseActivity extends Activity implements OnClickListener, OnCheckedChangeListener {
	
	public static final int SUCCESS = 1;
	public static final int RETURN = 2;
	
	private EditText moneyInput;
	private String orderID;
	private int payType = CommonConstant.PAYMENT_ALI;
	private LoadingView loadingView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recharge_chose);
		
		loadingView = (LoadingView)findViewById(R.id.loading);
		((RadioGroup)findViewById(R.id.rg_payment)).setOnCheckedChangeListener(this);
		((ImageView)findViewById(R.id.iv_return)).setOnClickListener(this);
		((Button)findViewById(R.id.btn_50)).setOnClickListener(this);
		((Button)findViewById(R.id.btn_100)).setOnClickListener(this);
		((Button)findViewById(R.id.btn_300)).setOnClickListener(this);
		((Button)findViewById(R.id.btn_500)).setOnClickListener(this);
		((Button)findViewById(R.id.btn_pay)).setOnClickListener(this);
		moneyInput = (EditText)findViewById(R.id.et_recharge_money);
		
		orderID = getIntent().getStringExtra("order_id");

		initInfo();
	}
	
	private void initInfo() {
		((TextView) findViewById(R.id.tv_order_id)).setText(orderID);
		((TextView) findViewById(R.id.tv_balance))
				.setText(Data.memberInfo.account
						+ getResources().getString(R.string.yuan));
	}
	
//	private void recharge() {
//		if (payType == YL) {
//			payPD = ProgressDialog.show(mContext, "", "");
//			String money = "" + Double.valueOf(inputMoney.getText().toString())*100;
//			YLCreateOrderTask task = new YLCreateOrderTask(mContext,
//					YLPayHandler, money, orderID);
//			task.execute();
//		} else {
//			
//		}
//	}
//	
//	private Handler YLPayHandler = new Handler() {
//		public void dispatchMessage(Message msg) {
//			if (msg.what == Constant.SUCCESS) {
//				
//			} else {
//				if (payPD != null)
//					payPD.dismiss();
//			}
//		};
//	};
//	
//	private Handler YLConfirmPayHandler = new Handler() {
//		public void dispatchMessage(Message msg) {
//			if (msg.what == Constant.SUCCESS) {
//				
//			}
//			
//			if (payPD != null)
//				payPD.dismiss();
//		};
//	};
		
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
			setResult(RETURN);
			finish();
			break;
			
		case R.id.btn_pay: {
			if (moneyInput.getText().toString().length() != 0) {
				payTask();
			} else {
				Toast.makeText(RechargeChoseActivity.this, 
						getResources().getString(R.string.input_recharge_money),
						Toast.LENGTH_SHORT).show();
			}
		}
			break;
			
		case R.id.btn_50:
			moneyInput.setText("50");
			break;
			
		case R.id.btn_100:
			moneyInput.setText("100");
			break;
			
		case R.id.btn_300:
			moneyInput.setText("300");
			break;
			
		case R.id.btn_500:
			moneyInput.setText("500");
			break;

		default:
			break;
		}
		
	}
	
	private void payTask() {
		ConfirmRechargeOrderTask task = new ConfirmRechargeOrderTask(this, 
				handler, orderID, payType, moneyInput.getText().toString());
		task.execute();
	}
	
	private Handler handler = new Handler() {
		public void dispatchMessage(android.os.Message msg) {
			if (msg.what == Constant.SUCCESS) {
				AliPayManager alipayManager = new AliPayManager();
				alipayManager.pay(RechargeChoseActivity.this, (String)msg.obj,
						orderID, true, aliPayHandler);
			} else if (msg.what == Constant.DATA_ERROR){
				Toast.makeText(RechargeChoseActivity.this, 
						(String)msg.obj, Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(RechargeChoseActivity.this, 
						getResources().getString(R.string.recharge_order_confirm_fail),
						Toast.LENGTH_SHORT).show();
			}
		};
	};
	
	private Handler aliPayHandler = new Handler() {
		public void dispatchMessage(android.os.Message msg) {
			switch (msg.what) {
			case AliPayManager.PAY_CHECK:
				loadingView.setVisibility(View.VISIBLE);
				loadingView.setLoadingText(getResources().getString(R.string.pay_checking));
				break;
				
			case AliPayManager.PAY_SUCCESS:
				loadingView.setVisibility(View.GONE);
				Toast.makeText(RechargeChoseActivity.this, getResources().
						getString(R.string.recharge_success), Toast.LENGTH_SHORT).show();
				float account = Float.valueOf(Data.memberInfo.account)+
						Float.valueOf(moneyInput.getText().toString());
				Data.memberInfo.account = String.format("%.2f", account);
				setResult(SUCCESS);
				finish();
				break;
				
			case AliPayManager.PAY_FAIL:
				loadingView.setVisibility(View.GONE);
				Toast.makeText(RechargeChoseActivity.this, getResources().
						getString(R.string.recharge_fail), Toast.LENGTH_SHORT).show();
				break;

			default:
				break;
			}
		};
	};
	
	@Override
	public void onCheckedChanged(RadioGroup arg0, int arg1) {
		int radioButtonId = arg0.getCheckedRadioButtonId();
		switch (radioButtonId) {
		case R.id.rb_ali:
			payType = CommonConstant.PAYMENT_ALI;
			break;

		case R.id.rb_yl:
			payType = CommonConstant.PAYMENT_YL;
			break;

		default:
			break;
		}
	}

}
