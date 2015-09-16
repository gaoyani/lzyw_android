package com.huiwei.roomreservation.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.huiwei.roomreservation.R;
import com.huiwei.roomreservation.common.CommonConstant;
import com.huiwei.roomreservation.view.LoadingView;
import com.huiwei.roomreservationlib.data.Constant;
import com.huiwei.roomreservationlib.task.GetAgreementTask;

public class AgreementActivity extends Activity {

	private TextView agreement;
	private LoadingView loadingView;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_agreement);
		
		loadingView = (LoadingView)findViewById(R.id.loading);
		agreement = (TextView) findViewById(R.id.tv_agreement);
		getAgreement();
	}
	
	private void getAgreement() {
		GetAgreementTask task = new GetAgreementTask(this, handler, 
				getIntent().getIntExtra("agreement_type", CommonConstant.AGREEMENT_SOFTWARE));
		task.execute();
	}
	
	private Handler handler = new Handler() {
		public void dispatchMessage(android.os.Message msg) {
			if (msg.what == Constant.SUCCESS) {
				agreement.setText(Html.fromHtml((String)msg.obj));
			} else {
				Toast.makeText(AgreementActivity.this, getResources().
						getString(R.string.load_data_fail), Toast.LENGTH_SHORT).show();
			}
			
			loadingView.setVisibility(View.GONE);
		};
	};
	
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


