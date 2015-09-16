package com.huiwei.roomreservation.fragment;

import java.util.Timer;
import java.util.TimerTask;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.huiwei.commonlib.CommonFunction;
import com.huiwei.roomreservation.R;
import com.huiwei.roomreservationlib.data.Data;
import com.huiwei.roomreservationlib.info.StoreSceneInfo;

public class WebFragment extends Fragment {

	private final int TIME_OUT = 5000;
	private WebView webView;
	private TextView tips;
	private ProgressBar pb;
//	private String webUrl = null;
	private StoreSceneInfo storeSceneInfo;
	private Timer timer;
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_web,
				container, false);
		
		pb = (ProgressBar)view.findViewById(R.id.progressBar);
		webView = (WebView) view.findViewById(R.id.webView);
		tips = (TextView) view.findViewById(R.id.tv_content);
		WebSettings webSettings = webView.getSettings(); 
		webSettings.setJavaScriptEnabled(true);
		String ua = webView.getSettings().getUserAgentString();
		webSettings.setUserAgentString(ua+"; Luxshare"+"; Mac="+
				CommonFunction.getLocalMacAddress(getActivity()));
		
		openStoreScene();
		
		return view;
	}
	
	public void openStoreScene() {
		if (Data.storeSceneList.size() == 0) {
			tips.setText(
					getResources().getString(R.string.no_store_scene));
			webView.setVisibility(View.GONE);
			return;
		}
		
		if (storeSceneInfo == null)
			return;
		
		if (storeSceneInfo.storeUrl == null || 
				storeSceneInfo.storeUrl.length() == 0) {
			webView.setVisibility(View.GONE);
			tips.setText(
					getResources().getString(R.string.no_store_web));
			return;
		}
		
		pb.setVisibility(View.VISIBLE);
		webView.setVisibility(View.VISIBLE);
		webView.loadUrl(storeSceneInfo.storeUrl);
		webView.setWebViewClient(new WebViewClient() {
			
			@Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                if (storeSceneInfo.storeUrl2 == null || storeSceneInfo.storeUrl2.length() == 0
                		|| storeSceneInfo.storeUrl2.equals(url))
                	return;
                
                timer = new Timer();
                TimerTask tt = new TimerTask() {
                    @Override
                    public void run() {
                        if (webView.getProgress() < 100) {
                        	webView.stopLoading();
                        	webView.loadUrl(storeSceneInfo.storeUrl2);

                        	stopTimer();
                        }
                    }
                };
                timer.schedule(tt, TIME_OUT, 1);
            }
			
			@Override
            public void onPageFinished(WebView view, String url) 
            {
				stopTimer();
				pb.setVisibility(View.GONE);
                super.onPageFinished(view, url);
            }
		});
	}
	
	public void setStoreSceneInfo(StoreSceneInfo info) {
		this.storeSceneInfo = info;
		if (webView != null) {
			openStoreScene();
		}
	}
	
	private void stopTimer() {
		if (timer != null) {
			timer.cancel();
            timer.purge();
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onDestroyView() {
		stopTimer();
		super.onDestroy();
	}
	
	@Override
	public void onDetach() {
		
		super.onDetach();
	} 
}


