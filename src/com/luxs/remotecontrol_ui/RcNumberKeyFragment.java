package com.luxs.remotecontrol_ui;

import java.net.SocketException;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.View.OnClickListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cat.projects.mediaplayer_ui.DMCPlayerActivity;

import com.huiwei.roomreservation.R;
import com.huiwei.roomreservation.baseview.XListView;
import com.huiwei.roomreservation.common.Remote;
import com.huiwei.roomreservation.common.RequestCodeTask;
import com.huiwei.roomreservation.view.RecommendPictrueView;
import com.luxs.common_ui.SettingConnectActivity;
import com.luxs.config.G;
import com.luxs.connect.ConnectManager;
import com.luxs.connect.ServerManager;
import com.luxs.fragmentutil.FragmentListener;
import com.luxs.setting.SettingManager;
import com.luxs.utils.CodeUtils;
import com.luxs.utils.Utils;

public class RcNumberKeyFragment extends Fragment implements OnClickListener,
		OnTouchListener, OnGestureListener {
	Context context;
	public Activity activity = getActivity();

	FrameLayout wrapper;

	final static int RC = 1;
	final static int DP = 2;
	float startX = 0;
	float startY = 0;
	float endX = 0;
	float endY = 0;
	int SENS_WEIDTH = 50;
	int SENS_HEIGHT = 300;
	boolean isClick = true;
	SettingManager settingManager = new SettingManager();

	String host;
	boolean isConnecting = false;
	ServerManager serverManager = new ServerManager();
	private FragmentListener mRcFragmentLister;
	private final static int RC_DIRECTIRONKEY_PAGE = 3;
	private final static int RC_NUMBERKEY_PAGE = 4;

	// init UI
	private TextView tvRed, tvYellow, tvBlue, tvGreen;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = null;
		view = inflater.inflate(R.layout.activity_dp, container, false);
		view.setOnTouchListener(this);
		initContext();
		serverManager.setActivity(getActivity());
		setUpView(view);
		addAction();
		connectFirst();
		return view;
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		String url = "http://182.92.158.59/api.php/app/remote_config";
		RequestCodeTask requestCodeTask = new RequestCodeTask(getActivity(),
				handler);
		requestCodeTask.execute(url);
	}

	private void setUpView(View view) {
		// TODO Auto-generated method stub
		((ImageButton) view.findViewById(R.id.btnSwitch))
				.setOnClickListener(this);

		((ImageButton) view.findViewById(R.id.btnMouse))
				.setOnClickListener(this);
		((Button) view.findViewById(R.id.btn1)).setOnClickListener(this);
		((Button) view.findViewById(R.id.btn0)).setOnClickListener(this);
		((Button) view.findViewById(R.id.btn2)).setOnClickListener(this);
		((Button) view.findViewById(R.id.btn3)).setOnClickListener(this);
		((Button) view.findViewById(R.id.btn4)).setOnClickListener(this);
		((Button) view.findViewById(R.id.btn5)).setOnClickListener(this);
		((Button) view.findViewById(R.id.btn6)).setOnClickListener(this);
		((Button) view.findViewById(R.id.btn7)).setOnClickListener(this);
		((Button) view.findViewById(R.id.btn8)).setOnClickListener(this);
		((Button) view.findViewById(R.id.btn9)).setOnClickListener(this);
		((Button) view.findViewById(R.id.btnVoiceUp)).setOnClickListener(this);
		((Button) view.findViewById(R.id.btnVoiceDown))
				.setOnClickListener(this);
		((ImageButton) view.findViewById(R.id.btnMute))
				.setOnClickListener(this);
		((Button) view.findViewById(R.id.btnCannelUp)).setOnClickListener(this);
		((Button) view.findViewById(R.id.btnCannelDown))
				.setOnClickListener(this);
		((ImageButton) view.findViewById(R.id.btnRed)).setOnClickListener(this);
		((ImageButton) view.findViewById(R.id.btnYellow))
				.setOnClickListener(this);
		((ImageButton) view.findViewById(R.id.btnBlue))
				.setOnClickListener(this);
		((ImageButton) view.findViewById(R.id.btnGreen))
				.setOnClickListener(this);
		tvBlue = (TextView) view.findViewById(R.id.textViewBlue);
		tvGreen = (TextView) view.findViewById(R.id.textViewGreen);
		tvRed = (TextView) view.findViewById(R.id.textViewRed);
		tvYellow = (TextView) view.findViewById(R.id.textViewYellow);
	}

	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		ConnectManager.close();
		getActivity().unregisterReceiver(homeReceiver);
		super.onDestroyView();
	}

	/**
	 * 初始化UI
	 */
	public void initContext() {
		context = getActivity().getBaseContext();
		activity = getActivity();
	}

	public void setFragmentListener(FragmentListener listener) {
		mRcFragmentLister = listener;
	}

	/**
	 * 连接
	 */
	public void connect() {
		Utils.log("isConnecting" + isConnecting);
		if (isConnecting == true)
			return;

		// 获取默认机顶盒IP
		host = serverManager.getHostIP(context);
		if (TextUtils.isEmpty(G.CURRENT_VALUE)) {
			CodeUtils.getSuijiCode(getActivity());
		}
		if (host == null) {
			Utils.toActivity(activity, SettingConnectActivity.class);
			return;
		}

		if (TextUtils.isEmpty(G.CURRENT_VALUE)) {
			Utils.toActivity(activity, SettingConnectActivity.class);
		}

		Log.i("控制", "随机码:" + G.CURRENT_VALUE);

		// 是否已连接？
		boolean isConnected = false;
		try {
			isConnected = ConnectManager.isConnected();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Utils.log("isConnected" + isConnected);
		if (isConnected == true) {
			return;
		} else {
			serverManager.setHostStatus(context, host, 0);
			Utils.toActivity(activity, SettingConnectActivity.class);

		}

		new Thread() {
			public void run() {
				isConnecting = true;
				boolean result = ConnectManager.connectServer(host, false,
						false);
				if (result) {
					Message msg = connectHandler.obtainMessage();
					msg.what = G.LOAD_SUCCESS;
					msg.sendToTarget();
				} else {
					Message msg = connectHandler.obtainMessage();
					msg.what = G.LOAD_FAILED;
					msg.sendToTarget();
				}
			};
		}.start();
	}

	/**
	 * 连接
	 */
	public void connectFirst() {
		Utils.log("isConnecting" + isConnecting);
		if (isConnecting == true)
			return;

		// 获取默认机顶盒IP
		host = serverManager.getHostIP(context);
		if (TextUtils.isEmpty(G.CURRENT_VALUE)) {
			CodeUtils.getSuijiCode(getActivity());
		}
		if (host == null) {

			return;
		}

		if (TextUtils.isEmpty(G.CURRENT_VALUE)) {
			return;
		}

		// 是否已连接？
		boolean isConnected = false;
		try {
			isConnected = ConnectManager.isConnected();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Utils.log("isConnected" + isConnected);
		if (isConnected == true) {
			return;
		} else {
			// Utils.toActivity(activity, SettingConnectActivity.class);
			serverManager.setHostStatus(context, host, 0);
		}

		new Thread() {
			public void run() {
				isConnecting = true;
				boolean result = ConnectManager.connectServer(host, false,
						false);
				if (result) {
					Message msg = connectHandler.obtainMessage();
					msg.what = G.LOAD_SUCCESS;
					msg.sendToTarget();
				} else {
					Message msg = connectHandler.obtainMessage();
					msg.what = 100;
					msg.sendToTarget();
				}
			};
		}.start();
	}

	/**
	 * 连接后更新UI
	 */
	Handler connectHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case G.LOAD_SUCCESS:
				serverManager.setHostStatus(context, host, 1);
				// Utils.toast(activity,"连接 " + host+ " 成功！");
				break;
			case G.LOAD_FAILED:
				serverManager.setHostStatus(context, host, 0);
				Utils.toActivity(activity, SettingConnectActivity.class);
				break;

			case 100:
				serverManager.setHostStatus(context, host, 0);
				// Utils.toActivity(activity, SettingConnectActivity.class);
				break;

			default:
				break;
			}
			isConnecting = false;
		};
	};

	/**
	 * 执行点击
	 */
	public void excute(int data) {
		if (TextUtils.isEmpty(G.CURRENT_VALUE)) {
			CodeUtils.getSuijiCode(getActivity());
		}
		settingManager.click(activity, handler);
		if (isConnecting == true) {

			Utils.toActivity(activity, SettingConnectActivity.class);

			Utils.toast(
					activity,
					"机顶盒连接不上！\n1.请检查机顶盒是否开启?\n2.手机WIFI是否与机顶盒在同一网段?\n或者点击乐更多 > 乐遥控设置 > 连接机顶盒中重新扫描连接！");
			return;
		}
		new SendThread(data + "").start();
	}

	/**
	 * 发送消息线程
	 */
	class SendThread extends Thread {
		String data;

		public SendThread(String data) {
			this.data = data;
		}

		@Override
		public void run() {
			boolean isSended = false;
			try {
				isSended = ConnectManager.send(data);
			} catch (SocketException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Utils.log("isSended:" + isSended);
			Message msg = handler.obtainMessage();
			msg.what = G.SEND;
			msg.obj = isSended;
			msg.sendToTarget();
		};
	}

	/**
	 * 发送返回
	 */
	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			settingManager.vibrate(activity);
			switch (msg.what) {
			case G.SEND:
				boolean isSended = (Boolean) msg.obj;
				if (!isSended) {
					connect();
					// Utils.toActivity(activity, SettingConnectActivity.class);
				}
				break;
			case 0x101:
				Remote remote = (Remote) msg.obj;
				tvBlue.setText(remote.getBlueName());
				tvGreen.setText(remote.getGreenName());
				tvRed.setText(remote.getRedName());
				tvYellow.setText(remote.getYellowName());
				break;
			default:
				break;
			}
		};
	};

	/**
	 * 切换功能和数字面板
	 */
	public void switchPanel() {
		mRcFragmentLister.switchFragmentWithIn(RC_DIRECTIRONKEY_PAGE);
	}

	/**
	 * 发送关闭电源指令
	 * 
	 */
	private void sendPower() {

	}

	/**
	 * 返回主页接收
	 */
	private BroadcastReceiver homeReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			if (action.equals(G.SEND_BACK_HOME)) {
				excute(G.VALUE_BACK);
				excute(G.VALUE_HOME);
			} else if (action.equals(G.ACTION_SHARE_)
					&& !Utils.getSharePreferences(activity)) {
				// 暂时注释掉
				// ShareUtlis.shareDailog(activity);
			}

		}
	};

	/**
	 * 注册广播
	 */
	private void addAction() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(G.SEND_BACK_HOME);
		filter.addAction(G.ACTION_SHARE_);
		getActivity().registerReceiver(homeReceiver, filter);
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {

		case R.id.btnSwitch:
			switchPanel();
			break;
		case R.id.btnPower:
			sendPower();
			break;

		case R.id.btnMusic:
			excute(G.VALUE_MUSIC);
			break;
		case R.id.btnMouse:
			Utils.toActivity(activity, MouseKeyboardActivity.class);
			break;
		case R.id.btnRed:
			excute(G.VALUE_RED);
			break;
		case R.id.btnYellow:
			excute(G.VALUE_YELLOW);
			break;
		case R.id.btnBlue:
			excute(G.VALUE_BLUE);
			break;
		case R.id.btnGreen:
			excute(G.VALUE_GREEN);
			break;

		case R.id.btn1:
			excute(G.VALUE_1);
			break;
		case R.id.btn2:
			excute(G.VALUE_2);
			break;
		case R.id.btn3:
			excute(G.VALUE_3);
			break;
		case R.id.btn4:
			excute(G.VALUE_4);
			break;
		case R.id.btn5:
			excute(G.VALUE_5);
			break;
		case R.id.btn6:
			excute(G.VALUE_6);
			break;
		case R.id.btn7:
			excute(G.VALUE_7);
			break;
		case R.id.btn8:
			excute(G.VALUE_8);
			break;
		case R.id.btn9:
			excute(G.VALUE_9);
			break;
		case R.id.btn0:
			excute(G.VALUE_0);
			break;
		case R.id.btnVoiceUp:
			excute(G.VALUE_VOLUME_UP);
			break;
		case R.id.btnVoiceDown:
			excute(G.VALUE_VOLUME_DOWN);
			break;
		case R.id.btnMute:
			excute(G.VALUE_MUTE);
			break;
		case R.id.btnCannelUp:
			excute(G.VALUE_PLUS);
			break;
		case R.id.btnCannelDown:
			excute(G.VALUE_MINUS);
			break;

		}
	}

	@Override
	public boolean onDown(MotionEvent arg0) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean onFling(MotionEvent arg0, MotionEvent arg1, float arg2,
			float arg3) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onLongPress(MotionEvent arg0) {
		// TODO Auto-generated method stub
		// start connect UI.
		Log.i("tags", arg0.toString());
		Intent intent = new Intent();
		intent.putExtra("type", 0);
		intent.setClass(getActivity(), SettingConnectActivity.class);
		startActivity(intent);
	}

	@Override
	public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2,
			float arg3) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShowPress(MotionEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onSingleTapUp(MotionEvent arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	private GestureDetector gestureDetector = new GestureDetector(this);

	@Override
	public boolean onTouch(View arg0, MotionEvent event) {
		// TODO Auto-generated method stub
		return gestureDetector.onTouchEvent(event);
	}

}
