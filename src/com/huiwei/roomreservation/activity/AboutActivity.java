package com.huiwei.roomreservation.activity;

import android.R.integer;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.huiwei.commonlib.CommonFunction;
import com.huiwei.roomreservation.R;
import com.huiwei.roomreservation.common.CommonConstant;

public class AboutActivity extends Activity {

	private TextView info1, info2, version;
	private String versionType = "beta";
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		
		version = (TextView)findViewById(R.id.tv_app_version);
		version.setText("v"+CommonFunction.getVersionName(getApplicationContext())+
				" " + versionType);
		
		info1 = (TextView)findViewById(R.id.tv_info_1);
		info1.setText(getInfo1ClickableSpan());
		info2 = (TextView)findViewById(R.id.tv_info_2);
//		info2.setText(getInfo2ClickableSpan());
		info2.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		info2.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent();
				intent.putExtra("agreement_type", CommonConstant.AGREEMENT_SOFTWARE);
				intent.setClass(AboutActivity.this, AgreementActivity.class);
				startActivity(intent);
			}
		});
		
		((ImageView)findViewById(R.id.iv_return)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
	}
	
	private SpannableString getInfo1ClickableSpan() {
		SpannableString spanableInfo = new SpannableString(info1.getText());
		String appName = getResources().getString(R.string.about_app_name);
		int start = info1.getText().toString().indexOf(appName);
		int end = appName.length()+start;
		spanableInfo.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.highlight_text)),
				start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		return spanableInfo;
	}
	
	private SpannableString getInfo2ClickableSpan() {
		View.OnClickListener l = new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.putExtra("agreement_type", CommonConstant.AGREEMENT_SOFTWARE);
				intent.setClass(AboutActivity.this, AgreementActivity.class);
				startActivity(intent);
			}
		};

		SpannableString spanableInfo = new SpannableString(info2.getText());
		String agreement = getResources().getString(R.string.about_agreement_name);
		int start = info2.getText().toString().indexOf(agreement);
		int end = agreement.length()+start;
		spanableInfo.setSpan(new Clickable(l), start, end,
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);   
		return spanableInfo;
	}

	class Clickable extends ClickableSpan implements OnClickListener {
		private final View.OnClickListener mListener;

		public Clickable(View.OnClickListener l) {
			mListener = l;
		}

		@Override
		public void onClick(View v) {
			mListener.onClick(v);
		}
	}
	
	@Override  
    protected void onDestroy() {  
        super.onDestroy();  
    }  
	
    @Override  
    protected void onResume() {  
        super.onResume();  
   }  
    
    @Override  
    protected void onPause() {  
        super.onPause();  
   }  
}


