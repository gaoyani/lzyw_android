package com.huiwei.roomreservation.view;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huiwei.roomreservation.R;

public class PhoneOrderConfirmView extends RelativeLayout implements OnClickListener {
	
	private Context context;
	private Handler handler;
	
	private TextView info;
	
	public PhoneOrderConfirmView(Context context) {
		super(context);
		this.context = context;
	}
	
	public PhoneOrderConfirmView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
	}
	
	@Override
	protected void onFinishInflate() {
		// TODO Auto-generated method stub
		super.onFinishInflate();
		
		Button ok = (Button)findViewById(R.id.btn_ok);
		if (ok != null) {
			ok.setOnClickListener(this);
		}
		
		Button cancel = (Button)findViewById(R.id.btn_cancel);
		if (cancel != null) {
			cancel.setOnClickListener(this);
		}
		
		info = (TextView)findViewById(R.id.tv_info);
	}
	
	public void setHandler(Handler handler) {
		this.handler = handler;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_ok:
			
			break;
			
		case R.id.btn_cancel:
			
			break;

		default:
			break;
		}
		
		handler.sendEmptyMessage(0);
	}
}
	
