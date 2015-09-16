package com.luxs.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import cat.projects.store.G;

import com.huiwei.roomreservation.R;

/**
 * 分享工具类
 * 
 * @author lyl
 * 
 * 
 */
public class ShareUtlis {
	
	private static boolean isshare=false;
	
	/**
	 * 调用系统分享
	 * 
	 * @param context
	 *            上下文
	 */
	public static void share2Fre(Context context,String shareText) {
		Intent shareIntent = new Intent();
		shareIntent.setAction(Intent.ACTION_SEND);
		shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
		shareIntent.setType("text/plain");
		context.startActivity(Intent.createChooser(shareIntent, context
				.getResources().getString(R.string.leshare)));
//		shareIntent.putExtra(Intent.EXTRA_TEXT, "乐商务的这个软件不错哦，可以控制电视，现在连接"+"\n"
//				+ "乐享验证码:"
//				+ G.CURRENT_VALUE +"\n"+ "WIFI:" + G.WIFI_SSID +"\n"+ "下载地址:"
//				+ G.DOWNLOAD_URL);
	}

	/**
	 * 分享对话框
	 * 
	 * @param context
	 */
//	public static void shareDailog(final Activity context) {
//		
//		View checkView=context.getLayoutInflater().inflate(R.layout.gps_sec, null);
//		CheckBox checkBox=(CheckBox)checkView.findViewById(R.id.gpscheck);
//		
//		new AlertDialog.Builder(context)
//				.setTitle(R.string.leshare)
//				.setMessage(R.string.isshare)
//				.setView(checkView)
//				.setPositiveButton(R.string.ok,
//						new DialogInterface.OnClickListener() {
//
//							@Override
//							public void onClick(DialogInterface dialog,
//									int which) {
//								// TODO Auto-generated method stub
//								Utils.saveSharePreferences(context, isshare);
//								share2Fre(context);
//							}
//						})
//				.setNegativeButton(R.string.cancel,
//						new DialogInterface.OnClickListener() {
//
//							@Override
//							public void onClick(DialogInterface dialog,
//									int which) {
//								// TODO Auto-generated method stub
//								Utils.saveSharePreferences(context, isshare);
//							}
//						}).show();
//		
//		checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
//			
//			@Override
//			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//				// TODO Auto-generated method stub
//				isshare=isChecked;
//			}
//		});
//		
//	}

}
