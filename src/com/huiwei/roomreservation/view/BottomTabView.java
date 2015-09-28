package com.huiwei.roomreservation.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.huiwei.roomreservation.R;
import com.huiwei.roomreservation.activity.CommentActivity;
import com.huiwei.roomreservation.activity.ComplaintActivity;
import com.huiwei.roomreservation.activity.LoginActivity;
import com.huiwei.roomreservation.activity.MainActivity;
import com.huiwei.roomreservation.activity.NavigationActivity;
import com.huiwei.roomreservation.common.RequestShareTextTask;
import com.huiwei.roomreservationlib.data.Constant;
import com.huiwei.roomreservationlib.data.Data;
import com.huiwei.roomreservationlib.data.UrlConstant;
import com.huiwei.roomreservationlib.info.CommentInfo;

public class BottomTabView extends LinearLayout implements OnClickListener {
	
	private Context context;
	private int commentType;
	public BottomTabView(Context context) {
		super(context);
		this.context = context;
	}
	
	public BottomTabView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
	}
	
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		
		if (((ImageView) findViewById(R.id.rb_comment)) != null) {
			((ImageView) findViewById(R.id.rb_comment)).setOnClickListener(this);
			((ImageView) findViewById(R.id.rb_navigation)).setOnClickListener(this);
			((ImageView) findViewById(R.id.rb_share)).setOnClickListener(this);
			((ImageView) findViewById(R.id.rb_complaint)).setOnClickListener(this);
		}
	}
	
	public void setCommentType(int type) {
		commentType = type;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.rb_comment: {
			Intent intent = new Intent();
			intent.putExtra("comment_type", commentType);
			intent.setClass(context, CommentActivity.class);
			context.startActivity(intent);
		}
			break;

		case R.id.rb_complaint:
			toComplaint();
			break;

		case R.id.rb_navigation: {
			Intent intent = new Intent();
			intent.setClass(context, NavigationActivity.class);
			context.startActivity(intent);
		}
			break;
		case R.id.rb_share:
			toShare();
			break;
		default:
			break;
		}
	}
	
	private void toShare() {
		RequestShareTextTask shareTextTask=new RequestShareTextTask(context, handlerShare, true);
		if (commentType == CommentInfo.COMMENT_ROOM) {
			shareTextTask.execute(UrlConstant.SHARE_ROOM+Data.roomInfo.id);
		} else if (commentType == CommentInfo.COMMENT_STORE) {
			shareTextTask.execute(UrlConstant.SHARE_STORE+Data.storeDetailInfo.id);
		}
	}
	
	private Handler handlerShare = new Handler() {
		public void dispatchMessage(android.os.Message msg) {
			if (msg.what == Constant.SUCCESS) {
				
			} else {
				Toast.makeText(context, 
						R.string.share_error, Toast.LENGTH_LONG).show();
			}
		};
	};
	
	
	private void toComplaint() {
		if (!Data.memberInfo.isLogin) {
			Toast.makeText(context,
					getResources().getString(R.string.unlogin),
					Toast.LENGTH_SHORT).show();
			Intent intent = new Intent();
			intent.setClass(context, LoginActivity.class);
			((Activity)context).startActivityForResult(intent, MainActivity.TO_LOGIN);
		} else {
			if (!Data.storeDetailInfo.isOrdered) {
				Toast.makeText(context,
						getResources().getString(R.string.can_not_comment),
						Toast.LENGTH_SHORT).show();
			} else {
				Intent intent = new Intent();
				intent.setClass(context,
						ComplaintActivity.class);
				context.startActivity(intent);
			}
		}
	}
}
	
