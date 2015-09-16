package com.huiwei.roomreservation.common;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.widget.Toast;

import com.huiwei.roomreservation.R;
import com.huiwei.roomreservation.activity.MainActivity;
import com.huiwei.roomreservationlib.data.Data;
import com.huiwei.roomreservationlib.info.MemberInfo;

public class CommonFunction {
	
	public static void autoLogout(final Context context) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(R.string.auto_logout);
		builder.setPositiveButton(R.string.ok, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Data.memberInfo = null;
				Data.memberInfo = new MemberInfo();
				Data.memberInfo.isLogin = false;
				
				Intent intent = new Intent();
				intent.setClass(context, MainActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.putExtra("page_flag", MainActivity.MEMBER_PAGE);
				context.startActivity(intent);
			}
		});
		builder.create().show();
	}
}
