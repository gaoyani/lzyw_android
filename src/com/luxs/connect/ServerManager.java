package com.luxs.connect;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import cat.projects.dmc_service.DMCMediaService;

import com.luxs.common_ui.SettingConnectActivity;
import com.luxs.config.G;
import com.luxs.fileoperategroup.FileBean;
import com.luxs.fileoperategroup.FileManager;
import com.luxs.provider.IPsProviderMateData.TableMateData;
import com.luxs.setting.SettingManager;
import com.luxs.utils.Utils;

/**
 * 机顶盒通讯管理理器
 */
public class ServerManager {
	Socket socket = null;
	DataOutputStream dos = null;
	DataInputStream dis = null;
	boolean isConected = false;
	Activity mActivity;
	SettingManager settingManager = new SettingManager();

	public void setActivity(Activity activity) {
		mActivity = activity;
	}

	/**
	 * 搜索局域网机顶盒
	 * 
	 * @return
	 */
	public List<?> scan(Context context, Handler handler) {

		List<ServerBean> serverBeans = new ArrayList<ServerBean>();

		// clean the old data

		ServerBean tempBean = new ServerBean();
		tempBean.setContext(context);
		deleteAll(tempBean);
		tempBean = null;

		Log.d("scan", "scan the device="
				+ DMCMediaService.getUpnpController().GetMR().size());
		DMCMediaService.getUpnpController().GetCurMR();
		for (int i = 0; i < DMCMediaService.getUpnpController().GetMR().size(); i++) {
			Log.i("搜索", "数量:"
					+ DMCMediaService.getUpnpController().GetMR().size());

			ServerBean mBean = new ServerBean();
			mBean.setIp(DMCMediaService.getUpnpController().GetMR().get(i).localip);
			mBean.setName(DMCMediaService.getUpnpController().GetMR().get(i).devicename);
			mBean.setContext(context);
			mBean.setFileStatus(0);
			if (!SettingConnectActivity.sameHost(DMCMediaService
					.getUpnpController().GetMR().get(i).localip)) {
				updateOrInsertData(mBean);
				Log.i("搜索",
						"添加:"
								+ DMCMediaService.getUpnpController().GetMR()
										.get(i).localip);
			}

			serverBeans.add(mBean);
			Message msg = handler.obtainMessage();
			msg.what = G.LOAD_ING;
			float percent = ((float) i / DMCMediaService.getUpnpController()
					.GetMR().size());
			msg.arg1 = (int) percent;
			Utils.log(msg.arg1 + " percent:" + percent);
			msg.obj = DMCMediaService.getUpnpController().GetMR().get(i).localip;
			msg.sendToTarget();
		}

		return serverBeans;

	}

	/**
	 * 自动链接
	 * 
	 * @return
	 */
	public String connect() {

		try {
			InetAddress inetAddress = getLocalIpAddress();
			String ip = inetAddress.getHostAddress();
			String ipStr = ip.substring(0, ip.lastIndexOf("."));
			for (int i = 1; i <= 255; i++) {
				String tempIp = ipStr + "." + i;
				if (tempIp.equals(ip))
					continue;

				String has = runPingIPprocess(tempIp);
				if (has != null) {
					Utils.log(tempIp + " ON");
					isConected = connectServer(tempIp);
					if (isConected) {
						return tempIp;
					}
				} else {
					Utils.log(tempIp + " OFF");
				}
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获取本地IP
	 * 
	 * @return
	 * @throws UnknownHostException
	 */
	private InetAddress getLocalIpAddress() throws UnknownHostException {
		WifiManager wifiManager = (WifiManager) mActivity
				.getSystemService(android.content.Context.WIFI_SERVICE);
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		int ipAddress = wifiInfo.getIpAddress();
		return InetAddress.getByName(String.format("%d.%d.%d.%d",
				(ipAddress & 0xff), (ipAddress >> 8 & 0xff),
				(ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff)));
	}

	/**
	 * 
	 * @param ipString
	 */
	public String runPingIPprocess(final String ipString) {
		Process p;
		try {
			p = Runtime.getRuntime().exec("ping -c 1 -w 5 " + ipString);
			int status = p.waitFor();
			Utils.log(" status:" + status);
			if (status == 0)
				return ipString;
		} catch (IOException e) {
			Utils.log(" IOException");
			e.printStackTrace();
		} catch (InterruptedException e) {
			Utils.log(" InterruptedException");
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 执行链接
	 * 
	 * @param host
	 * @return
	 */
	public boolean connectServer(String host) {
		try {
			Utils.log("connecting" + host);
			socket = new Socket(host, G.PORT);
			Utils.log("socket true");
			dos = new DataOutputStream(socket.getOutputStream());
			dis = new DataInputStream(socket.getInputStream());
			Utils.log("dos true");
			settingManager.setHostStatus(mActivity, true);
			isConected = true;
			new RecvThread().start();
			return true;
		} catch (UnknownHostException e) {
			Utils.log("UnknownHostException");
			e.printStackTrace();
		} catch (IOException e) {
			Utils.log("IOException");
			e.printStackTrace();
		}
		settingManager.setHostStatus(mActivity, false);
		return false;
	}

	/**
	 * 是否已连接
	 */
	public boolean isConnected() {
		try {
			if (socket != null) {
				socket.sendUrgentData(0xFF);
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

	/**
	 * 发送指令
	 * 
	 * @param info
	 */
	public boolean send(String info) {
		try {
			if (socket != null && dos != null) {
				if (!"".equals(G.CURRENT_VALUE)) {
					dos.writeUTF((G.CURRENT_VALUE + "#" + info));
					Utils.log(G.CURRENT_VALUE + "#" + info);
				} else
					dos.writeInt(Integer.parseInt(info));
				dos.flush();
				return true;
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		settingManager.setHostStatus(mActivity, false);
		return false;
	}

	/**
	 * 启动更新随机码线程
	 */
	public class RecvThread extends Thread {

		public void run() {
			try {
				Utils.log("启动更新随机码线程");
				while (isConected) {
					String str = dis.readUTF();
					Utils.log(str);
					if (!"".equals(str) && str.length() > 8
							&& str.indexOf("#") != -1) {
						G.CURRENT_VALUE = str.substring(str.indexOf("#") + 1);
						Utils.log(G.CURRENT_VALUE);
					}
				}
			} catch (SocketException e) {
				Utils.log("SocketException 机顶盒端已退出！");
			} catch (EOFException e) {
				Utils.log("EOFException 机顶盒端已退出！");
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

	/**
	 * 断开链接
	 */
	public void close() {
		try {
			if (socket != null)
				socket.close();
			if (dos != null)
				dos.close();
			if (dis != null)
				dis.close();
			isConected = false;
			settingManager.setHostStatus(mActivity, false);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取数据库数据
	 */
	public List<?> queryData(FileBean fileBean) {

		FileManager fileManager = new FileManager();
		fileManager.setSqliter(new IPsSqliter());
		return fileManager.query(fileBean);
	}

	/**
	 * 更新数据库数据
	 */
	public void updateOrInsertData(FileBean fileBean) {
		FileManager fileManager = new FileManager();
		fileManager.setSqliter(new IPsSqliter());
		int count = fileManager.getLocalCount(fileBean);
		if (count == 0) {
			Utils.log("insert:" + fileManager.insert(fileBean));
		} else {
			Utils.log("update:" + fileManager.update(fileBean));
			fileManager.insert(fileBean);
		}

	}

	/**
	 * 更新数据库数据
	 */
	public int updateData(FileBean fileBean) {
		FileManager fileManager = new FileManager();
		fileManager.setSqliter(new IPsSqliter());
		return fileManager.update(fileBean);
	}

	/**
	 * 删除数据库数据
	 */
	public boolean deleteData(FileBean fileBean) {
		FileManager fileManager = new FileManager();
		fileManager.setSqliter(new IPsSqliter());
		int result = fileManager.delete(fileBean);
		if (result != 0)
			return true;
		else
			return false;
	}

	public int deleteAll(FileBean fileBean) {
		if (fileBean != null) {
			// String selection = fileBean.getSelection();
			// String[] selectionArgs = fileBean.getSelectionArgs();
			Uri uri = TableMateData.CONTENT_URI;
			ContentResolver cr = fileBean.getContext().getContentResolver();
			Cursor c = cr.query(uri, null, null, null, null);

			while (c.moveToNext()) {

				String pid = c.getString(1); // Get thread id;
				String uri_id = uri + "/" +pid;
				cr.delete(Uri.parse(uri_id), null, null);
			}
			c.close();
			return 0;
		} else {
			return G.SQL_ERROR;
		}

	}

	/**
	 * 获取默认机顶盒IP
	 */
	public String getHostIP(Context context) {
		FileBean fileBean = new FileBean();
		fileBean.setContext(context);
		fileBean.setCurrentPage(1);
		fileBean.setPageSize(1);
		fileBean.setOrderBy(TableMateData.STATUS + " DESC , "
				+ TableMateData._ID + " DESC");
		List<ServerBean> severList = (List<ServerBean>) this
				.queryData(fileBean);
		if (severList != null && severList.size() > 0) {
			String host = severList.get(0).getIp();
			return host;
		} else {
			return null;
		}
	}

	/**
	 * 设置所有机顶盒连接状态
	 */
	public int setAllHostStatus(Context context, int status) {
		ServerBean tempBean = new ServerBean();
		tempBean.setContext(context);
		tempBean.setFileStatus(status);
		return this.updateData(tempBean);
	}

	/**
	 * 设置机顶盒连接状态
	 */
	public int setHostStatus(Context context, String host, int status) {
		ServerBean tempBean = new ServerBean();
		tempBean.setContext(context);
		tempBean.setFileStatus(status);
		tempBean.setSelection(TableMateData.IP + "=?");
		tempBean.setSelectionArgs(new String[] { host });
		return this.updateData(tempBean);
	}

}
