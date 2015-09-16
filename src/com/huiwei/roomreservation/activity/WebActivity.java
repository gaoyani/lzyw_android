package com.huiwei.roomreservation.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.huiwei.roomreservation.R;
import com.huiwei.roomreservation.view.LoadingView;

public class WebActivity extends Activity {

	private WebView webView;
	private LoadingView loadingView;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_web);
		
		loadingView = (LoadingView)findViewById(R.id.loading);
		String url = getIntent().getStringExtra("url");
		webView = (WebView) findViewById(R.id.webView);
		WebSettings webSettings = webView.getSettings(); 
		webSettings.setJavaScriptEnabled(true);
		webView.loadUrl(url);
		webView.setWebViewClient(new WebViewClient() {
			@Override
            public void onPageFinished(WebView view, String url) 
            {
				loadingView.setVisibility(View.GONE);
                super.onPageFinished(view, url);
            }
		});
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


