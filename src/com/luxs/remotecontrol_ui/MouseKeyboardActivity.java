package com.luxs.remotecontrol_ui;

import java.net.SocketException;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.huiwei.roomreservation.R;
import com.luxs.common_ui.SettingConnectActivity;
import com.luxs.config.G;
import com.luxs.connect.ConnectManager;
import com.luxs.connect.ServerBean;
import com.luxs.connect.ServerManager;
import com.luxs.setting.SettingManager;
import com.luxs.utils.Utils;

public class MouseKeyboardActivity extends Activity {
	Context context;
	Activity activity;
	
	FrameLayout wrapper;
	LinearLayout mousePanel;
	EditText editText;
	Button leftButton,rightButton;
	SettingManager settingManager = new SettingManager();
	ServerBean serverBean = new ServerBean();
	
	float startX = 0;
	float startY = 0;
	float endX = 0;
	float endY = 0;
	final float DEFAULT_XY = -1f;
	float preMoveX = DEFAULT_XY;
	float preMoveY = DEFAULT_XY;
	int SENS_WEIDTH = 50;
	int SENS_HEIGHT = 300;
	boolean isClick = true;
	
	String host;
	boolean isConnecting = false;
	ServerManager serverManager = new ServerManager();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mouse_keyboard);
		initUI();
		//自动链接
		connect();
		serverManager.setActivity(this);
	}
	
	//int heightDifference = 0;
	
	/**
	 * 初始化UI
	 */
	public void initUI(){
		context = this;
		activity = this;
		mousePanel = (LinearLayout)findViewById(R.id.mousePanel);
		wrapper = (FrameLayout)findViewById(R.id.wrapper);
		editText = (EditText)findViewById(R.id.editText);
		
		leftButton = (Button)findViewById(R.id.leftButton);
		rightButton = (Button)findViewById(R.id.rightButton);
		
		Utils.showHideInputMethod(context);
		editText.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence words, int arg1, int arg2, int arg3) {
				Utils.log("onTextChanged:" + " arg0:" + words + " arg1:" + arg1 + " arg2:" + arg2  + " arg3:" + arg3);
				if(!"".equals(words + "")) {
					excute(words + "");
					Utils.log("excute:" + words);
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				Utils.log("beforeTextChanged" + " arg0:" + arg0 + " arg1:" + arg1 + " arg2:" + arg2);
			}
			
			@Override
			public void afterTextChanged(Editable arg0) {
				Utils.log("afterTextChanged:" + arg0);
				if(!"".equals(arg0 + "")) editText.setText("");
			}
		});
		
		
		editText.setVisibility(View.GONE);
		
		leftButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				excute(G.VALUE_MOUSE_CLICK + "");
			}
		});
		
		rightButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				excute(G.VALUE_BACK + "");
			}
		});
	
	}
	
	
	
	/**
	 * 连接
	 */
	public void connect(){
		Utils.log("isConnecting" + isConnecting);
		if(isConnecting == true) {
			return;
		}
		//获取默认机顶盒IP
		host = serverManager.getHostIP(context);
		if(host == null) {
			Utils.toActivity(activity, SettingConnectActivity.class);
			return;
		}
		//是否已连接？
		boolean isConnected = false;
		try {
			isConnected = ConnectManager.isConnected();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(isConnected == true) {
			return;
		}else{
			serverManager.setHostStatus(context, host, 0);
		} 
		
		new Thread(){
			public void run() {
				isConnecting = true;
				boolean result = ConnectManager.connectServer(host, false, false);
				if(result){
					Message msg = connectHandler.obtainMessage();
					msg.what = G.LOAD_SUCCESS;
					msg.sendToTarget();
				}else{
					Message msg = connectHandler.obtainMessage();
					msg.what = G.LOAD_FAILED;
					msg.sendToTarget();
				}
			};
		}.start(); 
	}
	
	/**
	 * 连接后更新UI
	 */
	Handler connectHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case G.LOAD_SUCCESS:
				serverManager.setHostStatus(context, host, 1);
				//Utils.toast(activity,"连接 " + host+ " 成功！");
				break;
			case G.LOAD_FAILED:
				Utils.toActivity(activity, SettingConnectActivity.class);
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
	public void excute(String data){
		if(isConnecting == true) {
			Utils.toast(activity, "机顶盒连接不上！\n1.请检查机顶盒是否开启?\n2.手机WIFI是否与机顶盒在同一网段?\n或者点击乐更多 > 乐遥控设置 > 连接机顶盒中重新扫描连接！");
			return;
		}
		new SendThread(data + "").start();
	}
	
	/**
	 * 发送消息线程
	 */
	class SendThread extends Thread{
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
	Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case G.SEND:
				boolean isSended = (Boolean) msg.obj;
				if(!isSended) {
					connect();
				}
				break;
			default:
				break;
			}
		};
	};
	
	/**
	 * 移动鼠标指针
	 * @param x
	 * @param y
	 */
	private void mouseMove(float x,float y){
		//鼠标指针左侧坐标
		int left = (int) x;
		//鼠标指针顶部坐标,需减去标题栏高度
		int top = (int) (y - 96f);
		//设置鼠标指针新坐标
		mousePanel.setPadding(left, top, 0, 0);
		
		//X轴相对移动距离
		float distanceX = 0f;
		if(preMoveX != DEFAULT_XY) distanceX = x - preMoveX;
		//Y轴相对移动距离
		float distanceY = 0f;
		if(preMoveY != DEFAULT_XY) distanceY = y - preMoveY;
		
		if(distanceX != 0f || distanceY != 0f) {
			excute(distanceX + "|" + distanceY);
		}
		
		preMoveX = x;
		preMoveY = y;
	}
	
	/**
	 * 触摸
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			startX = event.getX();
			startY = event.getY();
			mouseMove((int) event.getX(),(int) event.getY());
Utils.log("按下: x=" + event.getX() + "y=" + event.getY());
			break;
		case MotionEvent.ACTION_MOVE:
			isClick = false;
			mouseMove((int) event.getX(),(int) event.getY());
Utils.log("移动: x=" + event.getX() + "y=" + event.getY());
			break;
		case MotionEvent.ACTION_UP:
			endX = event.getX();
			endY = event.getY();
			
			float x = 0;
			if(startX >= endX) x = startX - endX;
			else x = endX - startX;
			
			float y = 0;
			if(startY >= endY) y = startY - endY;
			else y = endY - startY;
Utils.log("x:" + x + " y:" + y);
			
			//上/下/左/右键映射
			if(x > y && y <= SENS_WEIDTH){
				if(startX > endX + SENS_HEIGHT){
					excute(G.VALUE_LEFT + "");
					Utils.toast(activity, "keyCode:" + G.VALUE_LEFT);
				}else if(startX < (endX - SENS_HEIGHT)){
					excute(G.VALUE_RIGHT + "");
					Utils.toast(activity, "keyCode:" + G.VALUE_RIGHT);
				} 
			}else if(x < y && x <= SENS_WEIDTH){
				if(startY > endY + SENS_HEIGHT){
					excute(G.VALUE_UP + "");
					Utils.toast(activity, "keyCode:" + G.VALUE_UP);
				}else if(startY < (endY - SENS_HEIGHT)){
					excute(G.VALUE_BOTTOM + "");
					Utils.toast(activity, "keyCode:" + G.VALUE_BOTTOM);
				} 
			}

			//点击
			int sX = (int) startX;
			int eX = (int) endX;
			int sY = (int) startY;
			int eY = (int) endY;
			Utils.log("sX " + sX);
			Utils.log("eX " + eX);
			Utils.log("sY " + sY);
			Utils.log("eY " + eY);
			int xOffset =  sX > eX ? sX - eX : eX - sX;
			int yOffset =  sY > eY ? sY - eY : eY - sY;
			if(xOffset < 2 && yOffset < 2) { 
				Utils.log("点击！");
				excute(G.VALUE_MOUSE_CLICK + "");
			}
			/*if(isClick) { 
				excute(G.VALUE_MOUSE_CLICK + "");
			}else{
				isClick = true;
			}*/
			
			preMoveX = DEFAULT_XY;
			preMoveY = DEFAULT_XY;
Utils.log("抬起: x=" + event.getX() + "y=" + event.getY());
			break;
		}
		return true;
	}
	
	/**
	 * 点击键盘
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode != KeyEvent.KEYCODE_BACK) {
			excute(keyCode + "");
			return true;
		}else{
			return super.onKeyDown(keyCode, event);
		}
		
	}
	
	/**
	 * 点击事件
	 */
	public void doClick(View view){
		switch (view.getId()) {
		case R.id.btnBack:
			finish();
			break;
		case R.id.btnKeyboard:
			Utils.showHideInputMethod(context);
			break;
		}
	}
}
