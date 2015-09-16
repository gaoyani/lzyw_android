package com.luxs.common_ui;

import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import cat.projects.constant.DMCConstants;
import cat.projects.dmc_service.DMCMediaService;

import com.bing.debug.AppLog;
import com.huiwei.roomreservation.R;
import com.huiwei.roomreservation.activity.MipcaCaptureActivity;
import com.luxs.config.G;
import com.luxs.connect.ConnectManager;
import com.luxs.connect.ServerBean;
import com.luxs.connect.ServerManager;
import com.luxs.provider.IPsProviderMateData.TableMateData;
import com.luxs.setting.SettingManager;
import com.luxs.utils.CodeUtils;
import com.luxs.utils.Utils;

public class SettingConnectActivity extends Activity {

	private static final String TAG = "SettingConnectActivity";
	private Context mContext;
	private Activity mActivity;

	FrameLayout mMainFrameLayout;
	View connectView;

	TextView mNoConnectInfoText;
	ListView mServerIpListView;
	private static List<ServerBean> dataList = new ArrayList<ServerBean>();
	final int CONNECT_ON = 1;
	final int CONNECT_OFF = 0;

	ServerAdapter serverAdapter =null;
	ServerBean serverBean = new ServerBean();

	SettingManager settingManager = new SettingManager();
	ServerManager serverManager = new ServerManager();

	private static String mServerIP;
	boolean isConnecting = false;
	boolean isScanning = false;
    boolean isConnect=false;
	private int mInitUIType = 0; // if 1 means start capture UI.

	public static boolean isTryConnecting = false;
	public static boolean isTryConnectingForRandom = false;
	private int mPagersize = 0;
	private int dataListPosition = 0;
	private static SettingConnectActivity settingConnectActivity = null;
	private static List<Map<String, Object>> connectList = new ArrayList<Map<String, Object>>();
	private static final int SHOW_RANDOM_DIALOG = 3;
	private static final int RANDOM_DIALOG_RESULT = 0x01;
    private static final int SCAN_RESULT=4;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting_connect);
		if (settingConnectActivity == null) {
			settingConnectActivity = new SettingConnectActivity();
		}
		initUI();
		initData();
		serverManager.setActivity(this);
		ConnectManager.setConnectMangerContext(getApplicationContext());

		settingConnectStartService();

		Log.i(TAG, "=====onCreate==随机码===" + G.CURRENT_VALUE);
	}

	/**
	 * 
	 */
	private void settingConnectStartService() {
		Intent intent = new Intent();
		intent.setClass(getApplicationContext(), DMCMediaService.class);
		startService(intent);
	}

	public static SettingConnectActivity getSettingConnectActivity() {
		return settingConnectActivity;
	}

	/**
	 * 初始化UI
	 */
	public void initUI() {
		mContext = this;
		mActivity = this;
        serverAdapter = new ServerAdapter();

		mInitUIType = getIntent().getIntExtra("type", 0);

		mNoConnectInfoText = (TextView) findViewById(R.id.info);
		mServerIpListView = (ListView) findViewById(R.id.listView);
		mServerIpListView.setAdapter(serverAdapter);

		mMainFrameLayout = (FrameLayout) findViewById(R.id.wrapper);
		LayoutInflater inflater = getLayoutInflater();
		connectView = inflater.inflate(R.layout.connect, null);
	}

	/**
	 * 初始化数据
	 */
	public void initData() {
		serverBean.setContext(mContext);
		serverBean.setCurrentPage(1);
		// serverBean.setPageSize(10);
		serverBean.setPageSize(settingManager.getServerCount(mActivity));
		serverBean.setOrderBy(TableMateData.STATUS + " DESC , "
				+ TableMateData._ID + " DESC");
	}

	@Override
	protected void onStart() {
		if (mInitUIType == 1) {
			// 启动扫描二维码界面
			// Intent openCameraIntent = new Intent(mContext,
			// CaptureActivity.class);
			// startActivityForResult(openCameraIntent, 0);
			mInitUIType = 0;
		}
		super.onStart();
	}

	@Override
	protected void onResume() {
		// 获取默认机顶盒IP
		String host = serverManager.getHostIP(mContext);
		if (host != null) {
			// 是否已连接？
			boolean isConnected = false;
			try {
				isConnected = ConnectManager.isConnected();
			} catch (SocketException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (isConnected == false || TextUtils.isEmpty(G.CURRENT_VALUE)) {
				serverManager.setHostStatus(mContext, host, 0);
			} else {
				serverManager.setHostStatus(mContext, host, 1);
			}
			updateOnResume();
		}
		super.onResume();
	}

	/**
	 * 更新数据
	 */
	public void updateOnResume() {
		try {
			if (mPagersize != 0) {
				serverBean.setPageSize(10);
			}
			try {
				dataList = (List<ServerBean>) serverManager
						.queryData(serverBean);

			} catch (Exception e) {
				// TODO: handle exception
			}

			if (dataList != null) {
				Log.i("连接", "列表:" + dataList.size());
				mNoConnectInfoText.setVisibility(View.GONE);
				serverAdapter.notifyDataSetChanged();
			} else {
				mNoConnectInfoText.setVisibility(View.VISIBLE);
				serverAdapter.notifyDataSetChanged();
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}

	/**
	 * 更新数据
	 */
	public void updateServerAdapter() {
		try {
			if (mPagersize != 0) {
				serverBean.setPageSize(10);
			}
			if (dataList != null) {
				dataList.clear();
			}

			try {
				dataList = (List<ServerBean>) serverManager
						.queryData(serverBean);
			} catch (Exception e) {
				// TODO: handle exception
			}

			if (null != dataList) {

				if (mPagersize < dataList.size() && mPagersize != 0) {
					for (int j = 0; j < mPagersize; j++) {
						for (int i = mPagersize; i < dataList.size(); i++) {
							Log.i(TAG, "IP地址:" + "[" + j + "]"
									+ dataList.get(j).getIp());
							Log.i(TAG, "IP地址:" + "[" + i + "]"
									+ dataList.get(i).getIp());
							if (dataList.get(j).getIp()
									.equals(dataList.get(i).getIp())) {
								deletem(dataList.get(i).getId());
								dataList.remove(i);
							}
						}
					}

				}
			}

			if (isConnecting && !TextUtils.isEmpty(G.CURRENT_VALUE)) {
				dataList.get(dataListPosition).setFileStatus(1);
			}

			if (dataList != null) {
				mNoConnectInfoText.setVisibility(View.GONE);
				serverAdapter.notifyDataSetChanged();
				Log.i("连接", "列表:" + dataList.size());
			} else {
				mNoConnectInfoText.setVisibility(View.VISIBLE);
				serverAdapter.notifyDataSetChanged();
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}

	public static void connectToServerForRandom(final boolean isRandom) {
		ConnectManager.connectServer(mServerIP, isRandom, false);
	}

	/**
	 * 连接 sendRandom is true, means it's back from random dialog.
	 */
	public void connectToServer(final boolean sendRandom,
			final boolean cancelRandom,final String ipname) {
		if (isConnecting == true)
			return;
		setAlert(getString(R.string.connecting));
		mMainFrameLayout.addView(connectView);
		new Thread() {
			public void run() {
				isConnecting = true;
				boolean result = ConnectManager.connectServer(mServerIP,
						sendRandom, cancelRandom);
                    isConnect=result;
				if (result && !sendRandom && cancelRandom) {
					serverManager.setHostStatus(mContext, mServerIP, 0);
					// serverManager.setHostStatus(context, host, 1);
					Message msg = handler.obtainMessage();
					msg.what = G.LOAD_SUCCESS;
					msg.sendToTarget();
				} else if (result && (sendRandom)) {
					Message msg = handler.obtainMessage();
					msg.what = SHOW_RANDOM_DIALOG;
					msg.sendToTarget();
				} else if(ipname.indexOf("lux")==-1){//@zhangtw  如果搜索到的ipName中不包含lux則按照第三方連接方式連接
					qrconnect();
				}else {
					Message msg = handler.obtainMessage();
					msg.what = G.LOAD_FAILED;
					msg.sendToTarget();
				}
			};
		}.start();
	}

	/**
	 * 二维码连接
	 */
	public void qrconnect() {
		if (isConnecting == true)
			return;
		setAlert(getString(R.string.connecting));
		mMainFrameLayout.addView(connectView);
		new Thread() {
			public void run() {
				isConnecting = true;
				boolean result = ConnectManager.connectServer(mServerIP, false,
						false);
				Log.i(TAG, "连接:" + result);
				if (result) {
					serverManager.setHostStatus(mContext, mServerIP, 0);
					// serverManager.setHostStatus(context, host, 1);
                    Log.i(TAG, "连接成功:" + result);
					SettingConnectActivity.this.finish();
					Message msg = handler.obtainMessage();
					msg.what = G.LOAD_SUCCESS;
					msg.sendToTarget();
				} else {
					Message msg = handler.obtainMessage();
					msg.what = G.LOAD_FAILED;
					msg.sendToTarget();
				}
			};
		}.start();
	}

	/**
	 * 连接后更新UI
	 */
	Handler handler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case G.LOAD_SUCCESS:
				Utils.toast(mActivity, "连接 " + mServerIP + " 成功！");
				mNoConnectInfoText.setVisibility(View.GONE);
				saveConnectState();
				updateServerAdapter();
				send2rcShare();
				finish();
				break;
			case G.LOAD_FAILED:
				Utils.toast(mActivity, "连接 " + mServerIP + " 失败！");
				break;

			case SHOW_RANDOM_DIALOG:
				mNoConnectInfoText.setVisibility(View.GONE);
				startRandomDialog();
				break;

			default:
				break;
			}
			mMainFrameLayout.removeView(connectView);
			isConnecting = false;
		};
	};

	/**
	 * 搜索局域网机顶盒
	 */
	public synchronized void scan() {
		if (isScanning == true)
			return;
		setAlert("搜索中...");
		mMainFrameLayout.addView(connectView);
		new Thread() {

			public void run() {
				Log.i(TAG, "run is runing");
				isScanning = true;
				serverManager.setActivity(mActivity);

				@SuppressWarnings("unchecked")
				List<ServerBean> result = (List<ServerBean>) serverManager
						.scan(mContext, scanHandler);
				Log.i(TAG, "设备数量:" + result.size());
				mPagersize = result.size();
				if (result != null && result.size() > 0) {
					Message msg = scanHandler.obtainMessage();
					msg.what = G.LOAD_SUCCESS;
					msg.sendToTarget();
				} else {
					Message msg = scanHandler.obtainMessage();
					msg.what = G.LOAD_FAILED;
					msg.sendToTarget();
				}
			};
		}.start();
	}

	/**
	 * 搜索局域网机顶盒更新UI
	 */
	Handler scanHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case G.LOAD_SUCCESS:
				updateServerAdapter();
				mMainFrameLayout.removeView(connectView);
				isScanning = false;

				break;
			case G.LOAD_FAILED:
				Utils.log("没有找到机顶盒！");
				Utils.toast(mActivity, "没有找到机顶盒！");
				mMainFrameLayout.removeView(connectView);
				isScanning = false;
				break;
			case G.LOAD_ING:
				String info = "搜索中...\nIP:" + msg.obj + "\n" + msg.arg1 + "%";
				setAlert(info);
				break;
			default:
				break;
			}

		};
	};

	/**
	 * 设置提示信息
	 * 
	 * @param info
	 */
	private void setAlert(String info) {
		TextView title = (TextView) connectView.findViewById(R.id.title);
		title.setText(info);
	}

	/**
	 * 机顶盒适配器
	 */
	public class ServerAdapter extends BaseAdapter {

		LayoutInflater layoutInflater = null;

		@Override
		public int getCount() {
			if (dataList == null){
                return 0;
            }
			return dataList.size();
		}

		@Override
		public Object getItem(int position) {
			if (dataList == null){
                return null;
            }
			return dataList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder;
			final int myPostion = position;
			if (convertView == null) {
				if (layoutInflater == null) {
					layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
				}
				convertView = layoutInflater.inflate(R.layout.server_list_item,parent,
						false);
				viewHolder = new ViewHolder();
				viewHolder.ip = (TextView) convertView.findViewById(R.id.ip);
				viewHolder.status = (TextView) convertView
						.findViewById(R.id.status);
				viewHolder.delete = (Button) convertView
						.findViewById(R.id.delete);
				viewHolder.connect = (Button) convertView
						.findViewById(R.id.connect);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			if (dataList != null && position < dataList.size()
					/*&& (dataList.get(position).getName() != null)*/) {
				viewHolder.ip.setText(dataList.get(position).getName() + ":"
						+ dataList.get(position).getIp());
				if (dataList.get(position).getFileStatus() == CONNECT_ON) {
					viewHolder.status
							.setText(getString(R.string.status_connected));
					viewHolder.connect.setVisibility(View.GONE);
					viewHolder.delete.setVisibility(View.GONE);
                    SettingConnectActivity.this.finish();
				} else {
					viewHolder.status
							.setText(getString(R.string.status_not_connect));
					viewHolder.connect.setVisibility(View.VISIBLE);
					viewHolder.connect
							.setOnClickListener(new ConnectOnClickListener(
									position));
					viewHolder.delete.setVisibility(View.VISIBLE);
					viewHolder.delete
							.setOnClickListener(new DeleteOnClickListener(
									myPostion));
				}

			}
			return convertView;
		}

	}

	/**
	 * 幻灯列表控件
	 */
	class ViewHolder {
		TextView ip;
		TextView status;
		Button delete;
		Button connect;
	}

	/**
	 * 点击连接按钮
	 */
	class ConnectOnClickListener implements View.OnClickListener {
		int position;

		public ConnectOnClickListener(int position) {
			this.position = position;
		}
    
		@Override
		public void onClick(View v) {
			if (dataList != null && dataList.get(position) != null) {
                String name=dataList.get(position).getName();

				if (TextUtils.isEmpty(name)||!name.startsWith("lux")) {
					Log.d(TAG, "other dlna service");
					DMCMediaService.mIsThirdPartyDlna = 1;
					SettingConnectActivity.this.finish();
//					return;
				}
				DMCMediaService.mIsThirdPartyDlna = 0;
				mServerIP = dataList.get(position).getIp();
				String ipName = dataList.get(position).getName();//@zhangtw
				dataListPosition = position;
				connectToServer(true, false,ipName);     
			}
		}
	}

	/**
	 * 点击删除按钮
	 */
	class DeleteOnClickListener implements View.OnClickListener {
		int position;

		public DeleteOnClickListener(int position) {
			this.position = position;
		}

		@Override
		public void onClick(View v) {
			// Log.i(TAG, "数据:"+dataList.size());
			if (dataList != null && dataList.get(position) != null) {
				deleteAlert(dataList.get(position).getId());
			}
		}
	}

	private void deleteAlert(int id) {
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setTitle(R.string.confirm_delete);
		DeleteListener pl = new DeleteListener(id);
		builder.setPositiveButton(getString(R.string.ok), pl);
		builder.setNegativeButton(getString(R.string.cancel), pl);
		AlertDialog alertDialog = builder.create();
		alertDialog.show();
	}

	public class DeleteListener implements DialogInterface.OnClickListener {

		int id;

		public DeleteListener(int id) {
			this.id = id;
		}

		@Override
		public void onClick(DialogInterface v, int btnId) {
			if (btnId == DialogInterface.BUTTON_POSITIVE) {
				delete(id);
			}
		}

	}

	private void delete(int id) {
		ServerBean tempBean = new ServerBean();
		tempBean.setContext(mContext);
		tempBean.setSelection(TableMateData._ID + "=?");
		tempBean.setSelectionArgs(new String[] { "" + id });
		serverManager.deleteData(tempBean);
		// pagersize--;
		// if (pagersize==0) {
		//
		// }
		updateServerAdapter();
	}

	private void deletem(int id) {
		ServerBean tempBean = new ServerBean();
		tempBean.setContext(mContext);
		tempBean.setSelection(TableMateData._ID + "=?");
		tempBean.setSelectionArgs(new String[] { "" + id });
		serverManager.deleteData(tempBean);
	}

	/**
	 * 返回本页监听
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// get RandomDialog result
        if (resultCode!=RESULT_OK){
            return;
        }
        Log.i(TAG ,"返回:"+requestCode);
        Log.i(TAG ,"返回:"+(requestCode==RANDOM_DIALOG_RESULT));
		if (requestCode == RANDOM_DIALOG_RESULT) {

			Log.i("返回",
					"=====onActivityResult=====:"
							+ RandomCodeActivity.isConected());
			Log.i("返回", "KEY:" + RandomCodeActivity.getKey()
					+ ":::CURRENT_VALUE:" + G.CURRENT_VALUE);
			int flag = data.getIntExtra("CONNECT_FLAG", 0);
            Log.i(TAG ,"返回flag:"+flag);
			if (flag == 0) {
				connectToServer(false, true,"");
			} else {
				new Thread() {
					public void run() {
						Message msg = handler.obtainMessage();
						msg.what = G.LOAD_FAILED;
						msg.sendToTarget();

					};
				}.start();
				Utils.toast(mActivity, "连接失败");
				ConnectManager.close();
				updateServerAdapter();
			}
			return;
		}
		// 处理扫描结果（在界面上显示）
        AppLog.i(TAG,"扫描:"+(requestCode == SCAN_RESULT));
		if (requestCode == SCAN_RESULT) {
            AppLog.i(TAG,"扫描:");
			Bundle bundle = data.getExtras();
            AppLog.i(TAG,"得到数据:"+bundle);
			mServerIP = bundle.getString("result");
			Log.i("设置", "扫描:" + mServerIP);
			if (mServerIP.indexOf("ip=") != -1) {
				mServerIP = mServerIP.split("ip=")[1];
			}
			Log.i("设置", "扫描:" + mServerIP);

			if (mServerIP != null && mServerIP.length() >= 7 /*
															 * &&
															 * host.indexOf("192."
															 * ) != -1
															 */) {
				try {
					if (mServerIP.indexOf("#") != -1
							&& mServerIP.lastIndexOf("#") != -1) {

						String key = mServerIP.split("#")[1]/*
															 * host.substring(host
															 * . indexOf("#") +
															 * 1)
															 */;
                        Log.i(TAG,"秘钥:"+key);
//						if (key == null || (key != null && key.length() < 6))
//							return;
						G.CURRENT_VALUE = key;
						Log.i("连接", "秘钥" + key);
						mServerIP = mServerIP.substring(
								mServerIP.indexOf("=") + 1,
								mServerIP.indexOf("#"));
						Utils.toast(mActivity, "key:" + key + "\nhost:"
								+ mServerIP);
						// if (null!=dataList) {
						// for (int i = 0; i < dataList.size(); i++) {
						// if (host.equals(dataList.get(i).getIp())) {
						// delete(dataList.get(i).getId());
						// }
						// }
						// }
						ServerBean tempBean = new ServerBean();
						tempBean.setContext(mContext);
						tempBean.setIp(mServerIP);
						tempBean.setFileStatus(0);
						tempBean.setSelection(TableMateData.IP + "=?");
						tempBean.setSelectionArgs(new String[] { mServerIP });

						serverManager.updateOrInsertData(tempBean);
						CodeUtils.saveSuijiCode(SettingConnectActivity.this,
								key);
						updateServerAdapter();
						qrconnect();
					} else {
						// G.CURRENT_VALUE = "";
						ServerBean tempBean = new ServerBean();
						tempBean.setContext(mContext);
						tempBean.setIp(mServerIP);
						tempBean.setFileStatus(0);
						tempBean.setSelection(TableMateData.IP + "=?");
						tempBean.setSelectionArgs(new String[] { mServerIP });
						serverManager.updateOrInsertData(tempBean);
						updateServerAdapter();
						qrconnect();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				Utils.toast(mActivity, "无效的机顶盒IP地址：" + mServerIP);
			}
		}
	}

	/**
	 * 点击事件
	 */
	public void doClick(View view) {
		switch (view.getId()) {
		case R.id.btnBack:
			finish();
			break;
		case R.id.btnScan:
			// 二维码扫描
			Intent openCameraIntent = new Intent(mContext,
					MipcaCaptureActivity.class);
			startActivityForResult(openCameraIntent, SCAN_RESULT);
			break;
		case R.id.btnAutoScan:
			scan();
			break;
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		isTryConnecting = false;
		isTryConnectingForRandom = false;
        sendPushFile();
		// serverManager.deleteAll(serverBean);
	}

	private void startRandomDialog() {
		// if (!TextUtils.isEmpty(G.CURRENT_VALUE)) {
		Intent intent = new Intent(mContext, RandomCodeActivity.class);
		startActivityForResult(intent, RANDOM_DIALOG_RESULT);
		// } else {
		// ConnectManager.close();
		// Toast.makeText(mContext, "连接失败，请再次连接", Toast.LENGTH_SHORT).show();
		// }

	}

	private void send2rcShare() {
		Intent intent = new Intent();
		intent.setAction(G.ACTION_SHARE_);
		sendBroadcast(intent);
	}

	public void deleteHost(String host) {
		for (int i = 0; i < dataList.size(); i++) {
			if (host.equals(dataList.get(i).getIp())) {
				delete(dataList.get(i).getId());
				Log.i(TAG, "删除:" + host);
			}
		}
	}

	public static boolean sameHost(String host) {
		if (null == dataList) {
			return false;
		}
		for (int i = 0; i < dataList.size(); i++) {

			Log.i(TAG, "IP:" + dataList.get(i).getIp() + "host:" + host);

			if (host.equals(dataList.get(i).getIp())) {
				return true;
			}
		}

		return false;

	}

	private void saveConnectState() {
		connectList.clear();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ip", mServerIP);
		map.put("code", G.CURRENT_VALUE);
		connectList.add(map);
	}

    private void sendPushFile(){
        if (isConnect){
            sendBroadcast(new Intent(DMCConstants.Action.ACTION_PUSH_FILE_TV));
        }

    }

}
