package com.huiwei.roomreservation.alipay;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.alipay.android.app.sdk.AliPay;
import com.huiwei.roomreservation.R;
import com.huiwei.roomreservationlib.data.Constant;
import com.huiwei.roomreservationlib.task.pay.GetPayResultTimerTask;

public class AliPayManager  {
	
	public static final int PAY_SUCCESS = 0;
	public static final int PAY_FAIL = 1;
	public static final int PAY_CHECK = 2;

//	public static String TAG = "AliPayManager";
	private static final int RQF_PAY = 1;
	private static final int RQF_LOGIN = 2;
	private Activity context;
	private GetPayResultTimerTask task = null;
	private String orderID;
	private boolean isRecharge;
	private Handler parentHandler;

	public void pay(Activity activity, final String info, 
			String orderID, boolean isRecharge, Handler parentHandler) {
		this.context = activity;
		this.orderID = orderID;
		this.isRecharge = isRecharge;
		this.parentHandler = parentHandler;
		
		try {
			new Thread() {
				public void run() {
					AliPay alipay = new AliPay(context, mHandler);
					//alipay.setSandBox(true);

					String result = alipay.pay(info);

					Log.i("AliPayManager", "result = " + result);
					Message msg = new Message();
					msg.what = RQF_PAY;
					msg.obj = result;
					mHandler.sendMessage(msg);
				}
			}.start();

		} catch (Exception ex) {
			ex.printStackTrace();
			Toast.makeText(context, R.string.remote_call_failed,
					Toast.LENGTH_SHORT).show();
		}
	}
	
	Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			Result result = new Result((String) msg.obj);

			switch (msg.what) {
			case RQF_PAY:
			case RQF_LOGIN: {
				result.parseResult();
				if (result.resultKey.equals("9000")) {
					startSyncResultTimer();
					parentHandler.sendEmptyMessage(PAY_CHECK);
				} else {
					Toast.makeText(context, result.getResult(),
							Toast.LENGTH_SHORT).show();
				}		
			}
				break;
			default:
				break;
			}
		};
	};
	
	private void startSyncResultTimer() {
		task = new GetPayResultTimerTask(context, handlerTimer, orderID, isRecharge);
		task.execute();
	}
	
	private void stopSyncResultTimer() {
		if (!task.isCancelled()) {
			task.stopTimer();
			task.cancel(true);
			task = null;
		}
	}

	Handler handlerTimer = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == Constant.SUCCESS) {
				parentHandler.sendEmptyMessage(PAY_SUCCESS);
			} else {
				parentHandler.sendEmptyMessage(PAY_FAIL);
			}
			stopSyncResultTimer();
		}
	};

//	private String getSignType() {
//		return "sign_type=\"RSA\"";
//	}

//	private void doLogin() {
//		final String orderInfo = getUserInfo();
//		new Thread() {
//			public void run() {
//				String result = new AliPay(AliPayManager.this, mHandler)
//						.pay(orderInfo);
//
//				Log.i(TAG, "result = " + result);
//				Message msg = new Message();
//				msg.what = RQF_LOGIN;
//				msg.obj = result;
//				mHandler.sendMessage(msg);
//			}
//		}.start();
//	}

//	private String getUserInfo() {
//		String userId = mUserId.getText().toString();
//		return trustLogin(Keys.DEFAULT_PARTNER, userId);
//
//	}
//
//	private String trustLogin(String partnerId, String appUserId) {
//		StringBuilder sb = new StringBuilder();
//		sb.append("app_name=\"mc\"&biz_type=\"trust_login\"&partner=\"");
//		sb.append(partnerId);
//		Log.d("TAG", "UserID = " + appUserId);
//		if (!TextUtils.isEmpty(appUserId)) {
//			appUserId = appUserId.replace("\"", "");
//			sb.append("\"&app_id=\"");
//			sb.append(appUserId);
//		}
//		sb.append("\"");
//
//		String info = sb.toString();
//
//		// 请求信息签名
//		String sign = Rsa.sign(info, Keys.PRIVATE);
//		try {
//			sign = URLEncoder.encode(sign, "UTF-8");
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		}
//		info += "&sign=\"" + sign + "\"&" + getSignType();
//
//		return info;
//	}


	

}