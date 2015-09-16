package com.lux.broadlinksdkcall;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;
import android.util.Log;

public class NetworkUtil {
	private WifiManager mWifiManager;
	private WifiInfo mWifiInfo;
	private String mWifiIp;
	
	public String getWifiIp() {
		return mWifiIp;
	}

	public NetworkUtil(Context context) {
		// Get the instance of the WifiManager
		mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		mWifiInfo = mWifiManager.getConnectionInfo();
		mWifiIp =  Formatter.formatIpAddress(mWifiInfo.getIpAddress());
		Log.d("NetWorkUtil", mWifiIp);
	}

	public void startScan() {
		mWifiManager.startScan();
		// Get the WifiInfo
		mWifiInfo = mWifiManager.getConnectionInfo();

	}

	public String getWiFiSSID() {

		String CurInfoStr = mWifiInfo.toString() + "";
		String CurSsidStr = mWifiInfo.getSSID().toString() + "";
		if (CurInfoStr.contains(CurSsidStr)) {
			return CurSsidStr;
		} else {
			return CurSsidStr.replaceAll("\"", "") + "";
		}

	}
}
