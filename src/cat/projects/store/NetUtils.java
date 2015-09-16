package cat.projects.store;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

public class NetUtils {

	/**
	 * 判断网络状�?
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isConnect(Context context) {
		boolean netState = false;
		ConnectivityManager cManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		boolean wifi = cManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
				.isConnected();
		boolean edge = cManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
				.isConnected();

		if (wifi || edge) {
			netState = true;
		}
		return netState;
	}

	/**
	 * 获取WIFI名称
	 * 
	 * @param context
	 * @return
	 */
	public static String getWifiName(Context context) {
		String wifiname = "";
		WifiManager manager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfo = manager.getConnectionInfo();
		wifiname = wifiInfo.getSSID();
		G.WIFI_SSID=wifiname;
//		Log.i("WIFI", "WIFI名称:" + wifiname);

		return wifiname;
	}

	
	
	/**
	 * WIFI mac地址
	 * 
	 * @param context
	 * @return
	 */
	public static String GetMACAddress(Context context) {
		String MACAddress = "";
		WifiManager manager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfo = manager.getConnectionInfo();
		MACAddress = wifiInfo.getMacAddress();
		return MACAddress;
	}

	/**
	 * WIFI mac地址
	 * 
	 * @param context
	 * @return
	 */
	public static int GetIpAddress(Context context) {
		int IpAddress = 0;
		WifiManager manager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfo = manager.getConnectionInfo();
		IpAddress = wifiInfo.getIpAddress();
		return IpAddress;
	}

	public static String getLocalIpAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf
						.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (SocketException ex) {
			Log.e("错误", ex.toString());
		}
		return null;
	}
}
