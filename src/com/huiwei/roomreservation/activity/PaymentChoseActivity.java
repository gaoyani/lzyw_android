package com.huiwei.roomreservation.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.AndroidCharacter;
import android.text.style.BulletSpan;
import android.view.LayoutInflater;
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
import com.huiwei.roomreservation.activity.setting.SetPayPwdActivity;
import com.huiwei.roomreservation.alipay.AliPayManager;
import com.huiwei.roomreservation.common.CommonConstant;
import com.huiwei.roomreservation.view.LoadingView;
import com.huiwei.roomreservationlib.data.Constant;
import com.huiwei.roomreservationlib.data.Data;
import com.huiwei.roomreservationlib.info.StoreDetailInfo;
import com.huiwei.roomreservationlib.task.pay.CreateRechargeOrderTask;
import com.huiwei.roomreservationlib.task.pay.PaymentTask;

public class PaymentChoseActivity extends Activity implements OnClickListener, OnCheckedChangeListener {
	
	private AlertDialog passworDialog, loadingDialog;
	private EditText passwordInput;
	private LoadingView loadingView;
	private String orderID, orderSN, storeName, priceType;
	private float payMoney;
	private float price;
	private int payType = CommonConstant.PAYMENT_LEZI;
	private boolean isFromOrder = false;
	private int inputPayPwdNum = 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_payment_chose);
		
		((RadioGroup)findViewById(R.id.rg_payment)).setOnCheckedChangeListener(this);
		((ImageView)findViewById(R.id.iv_return)).setOnClickListener(this);
		((Button)findViewById(R.id.btn_pay)).setOnClickListener(this);
		
		loadingView = (LoadingView)findViewById(R.id.loading);
		loadingView.setLoadingText(getResources().getString(R.string.submiting));
		
		orderID = getIntent().getStringExtra("order_id");
		orderSN = getIntent().getStringExtra("order_sn");
		storeName = getIntent().getStringExtra("store_name");
		priceType = getIntent().getStringExtra("price_type");
		price = Float.valueOf(getIntent().getStringExtra("price"));
		isFromOrder = getIntent().getBooleanExtra("is_from_order", false);

		setInfo();
	}
	
	private void setInfo() {
		float accout = Float.valueOf(Data.memberInfo.account);
		if (accout >= price) {
			payMoney = 0;
		} else {
			payMoney = Math.round(price - accout);
		}

		((TextView) findViewById(R.id.tv_order_id)).setText(orderSN);
		((TextView) findViewById(R.id.tv_store_name)).setText(storeName);
		((TextView) findViewById(R.id.tv_pay_name)).setText(priceType);
		
		((TextView) findViewById(R.id.tv_balance))
				.setText(Data.memberInfo.account
						+ getResources().getString(R.string.yuan));
		((TextView) findViewById(R.id.tv_price)).setText(""+price
				+ getResources().getString(R.string.yuan));
		((TextView) findViewById(R.id.tv_payment)).setText(String.format("%.2f", payMoney)
				+ getResources().getString(R.string.yuan));
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
			
		case R.id.btn_pay: 
			startPay();
			break;
			
		case R.id.btn_ok: 
			payTask();
			break;
			
		case R.id.btn_cancel:
			passworDialog.dismiss();
			break;
			
		case R.id.tv_forget_password: {
			Intent intent = new Intent();
			intent.putExtra("is_set", false);
			intent.setClass(PaymentChoseActivity.this, SetPayPwdActivity.class);
			startActivity(intent);
		}
			break;

		default:
			break;
		}
	}
	
	private Handler createOrderHandler = new Handler() {
		public void dispatchMessage(Message msg) {
			if (msg.what == Constant.SUCCESS) {
				String rechargeOrderID = (String) msg.obj;
				Intent intent = new Intent();
				intent.putExtra("order_id", rechargeOrderID);
				intent.setClass(PaymentChoseActivity.this, RechargeChoseActivity.class);
				startActivityForResult(intent, 1);
			} else if (msg.what == Constant.DATA_ERROR){
				Toast.makeText(PaymentChoseActivity.this, (String) msg.obj, Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(PaymentChoseActivity.this, getResources().getString(
						R.string.recharge_order_create_fail), Toast.LENGTH_SHORT).show();
			}
			
			loadingView.setVisibility(View.GONE);
		};
	};
	
	private void startPay() {
		if (payType == CommonConstant.PAYMENT_LEZI) {
			inputPayPwdNum = 1;
			if (payMoney > 0) {
				Toast.makeText(PaymentChoseActivity.this, 
						getResources().getString(R.string.not_sufficient_funds), 
						Toast.LENGTH_SHORT).show();
				CreateRechargeOrderTask task = new CreateRechargeOrderTask(
						PaymentChoseActivity.this, createOrderHandler);
				task.execute();
				loadingView.setLoadingText(getResources().getString(R.string.loading));
				loadingView.setVisibility(View.VISIBLE);
			} else {
				if (Data.memberInfo.isSetPayPassword) {
					getPassword();
				} else {
					Intent intent = new Intent();
					intent.putExtra("is_set", true);
					intent.setClass(PaymentChoseActivity.this, SetPayPwdActivity.class);
					startActivity(intent);
				}
			}
		} else {
			payTask();
		}
	}
	
	private void getPassword() {
		if (passworDialog == null) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setIcon(android.R.drawable.ic_dialog_info);
			View view = LayoutInflater.from(this).inflate(R.layout.payment_password_view, null);
			((Button)view.findViewById(R.id.btn_ok)).setOnClickListener(this);
			((Button)view.findViewById(R.id.btn_cancel)).setOnClickListener(this);
			((TextView)view.findViewById(R.id.tv_forget_password)).setOnClickListener(this);
			passwordInput = (EditText)view.findViewById(R.id.et_pay_password);
			builder.setView(view);
			passworDialog = builder.create();
		} 
		
		passworDialog.show();
	}
	
	private void popLoadingDialog() {
		if (loadingDialog == null) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			LoadingView view = (LoadingView) LayoutInflater.from(this).
					inflate(R.layout.loading_view, null);
			view.setLoadingText(getResources().getString(R.string.paying));
			builder.setView(view);
			loadingDialog = builder.create();
		} 
		
		loadingDialog.show();
	}
	
	private void payTask() {
		if (payType == CommonConstant.PAYMENT_LEZI) {
			popLoadingDialog();
		} else {
			loadingView.setVisibility(View.VISIBLE);
			loadingView.setLoadingText(getResources().getString(R.string.submiting));
		}
		
		PaymentTask task = new PaymentTask(this, handler, orderID, 
				payType, payType == CommonConstant.PAYMENT_LEZI ? 
				passwordInput.getText().toString() : "", inputPayPwdNum);
		task.execute();
	}
	
	private Handler handler = new Handler() {
		public void dispatchMessage(android.os.Message msg) {
			if (msg.what == Constant.SUCCESS) {
				startPay((String)msg.obj);
			} else if (msg.what == Constant.DATA_ERROR){
				if (payType == CommonConstant.PAYMENT_LEZI) {
					inputPayPwdNum++;
				} 
				Toast.makeText(PaymentChoseActivity.this, 
					(String)msg.obj, Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(PaymentChoseActivity.this, 
						getResources().getString(R.string.payment_fail),
						Toast.LENGTH_SHORT).show();
			}
			
			if (loadingDialog != null) {
				loadingDialog.dismiss();
			}
			
			loadingView.setVisibility(View.GONE);
		};
	};
	
	private void startPay(String info) {
		switch (payType) {
		case CommonConstant.PAYMENT_LEZI:
			passworDialog.dismiss();
			float accout = Float.valueOf(Data.memberInfo.account);
			Data.memberInfo.account = String.format("%.2f", accout-price);
			toReservationDonePage();
			break;
			
		case CommonConstant.PAYMENT_ALI:
			AliPayManager alipayManager = new AliPayManager();
			alipayManager.pay(PaymentChoseActivity.this, info,
					orderSN, false, aliPayHandler);
			break;
			
		case CommonConstant.PAYMENT_YL:
			
			break;

		default:
			break;
		}
	}
	
	private Handler aliPayHandler = new Handler() {
		public void dispatchMessage(android.os.Message msg) {
			switch (msg.what) {
			case AliPayManager.PAY_CHECK:
				loadingView.setVisibility(View.VISIBLE);
				loadingView.setLoadingText(getResources().getString(R.string.pay_checking));
				break;
				
			case AliPayManager.PAY_SUCCESS:
				passworDialog.dismiss();
				loadingView.setVisibility(View.GONE);
				Toast.makeText(PaymentChoseActivity.this, getResources().
						getString(R.string.payment_success), Toast.LENGTH_SHORT).show();
				toReservationDonePage();
				break;
				
			case AliPayManager.PAY_FAIL:
				loadingView.setVisibility(View.GONE);
				Toast.makeText(PaymentChoseActivity.this, getResources().
						getString(R.string.payment_fail), Toast.LENGTH_SHORT).show();
				break;

			default:
				break;
			}
		};
	};
	
	private void toReservationDonePage() {
		if (!isFromOrder) {
			Intent intent = new Intent();
			intent.putExtra("order_id", orderID);
			intent.putExtra("order_type", getIntent().getIntExtra(
					"order_type", StoreDetailInfo.TYPE_ROOM));
			intent.setClass(PaymentChoseActivity.this, ReservationDoneActivity.class);
			startActivity(intent);
		}
		
		finish();
	}

	@Override
	public void onCheckedChanged(RadioGroup arg0, int arg1) {
		int radioButtonId = arg0.getCheckedRadioButtonId();
		switch (radioButtonId) {
		case R.id.rb_lezi: 
			payType = CommonConstant.PAYMENT_LEZI;
			break;

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
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		
		if (resultCode == RechargeChoseActivity.SUCCESS) {
			setInfo();
		}
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

}
