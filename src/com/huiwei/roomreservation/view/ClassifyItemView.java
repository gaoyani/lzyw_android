package com.huiwei.roomreservation.view;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huiwei.roomreservation.R;
import com.huiwei.roomreservation.activity.StoreListActivity;

public class ClassifyItemView extends RelativeLayout {
	
	public final static int CLASSIFY_XY = 1;
	public final static int CLASSIFY_ZL = 2;
	public final static int CLASSIFY_KTV = 3;
	public final static int CLASSIFY_HS = 4;
	public final static int CLASSIFY_CYBF = 5;
	public final static int CLASSIFY_CSKF = 6;
	public final static int CLASSIFY_MRMT = 7;
	public final static int CLASSIFY_GD = 8;
	
	private Context context;
	private TextView classifyName;
	private ImageView classifyIcon;
	private int id;
	private String name;
	
	public ClassifyItemView(Context context) {
		super(context);
		this.context = context;
	}
	
	public ClassifyItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
	}
	
	@Override
	protected void onFinishInflate() {
		// TODO Auto-generated method stub
		super.onFinishInflate();
		
		classifyName = (TextView)findViewById(R.id.tv_classify_name);
		classifyIcon = (ImageView)findViewById(R.id.iv_classify_icon);
		
		if (classifyIcon != null) {
			classifyIcon.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					Intent intent = new Intent();
					intent.putExtra("classify_id", id);
					intent.putExtra("classify_name", name);
					intent.setClass(context, StoreListActivity.class);
					context.startActivity(intent);
				}
			});
		}
	}
	
	public void setClassify(int id, String name) {
		this.id = id;
		this.name = name;
		
		classifyName.setText(name);
		switch (id) {
		case CLASSIFY_XY:
			classifyIcon.setBackgroundResource(R.drawable.classify_xy_selector);
			break;

		case CLASSIFY_ZL:
			classifyIcon.setBackgroundResource(R.drawable.classify_zl_selector);
			break;

		case CLASSIFY_KTV:
			classifyIcon.setBackgroundResource(R.drawable.classify_ktv_selector);
			break;

		case CLASSIFY_HS:
			classifyIcon.setBackgroundResource(R.drawable.classify_hs_selector);
			break;

		case CLASSIFY_CYBF:
			classifyIcon.setBackgroundResource(R.drawable.classify_cybf_selector);
			break;

		case CLASSIFY_CSKF:
			classifyIcon.setBackgroundResource(R.drawable.classify_cskf_selector);
			break;

		case CLASSIFY_MRMT:
			classifyIcon.setBackgroundResource(R.drawable.classify_mrmt_selector);
			break;

		case CLASSIFY_GD:
			classifyIcon.setBackgroundResource(R.drawable.classify_gd_selector);
			break;

		default:
			break;
		}
	}
}
	
