package com.huiwei.roomreservation.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.huiwei.roomreservation.R;
import com.huiwei.roomreservation.adapter.CommentItemAdapter;
import com.huiwei.roomreservation.baseview.XListView;
import com.huiwei.roomreservation.baseview.XListView.IXListViewListener;
import com.huiwei.roomreservation.view.LoadingView;
import com.huiwei.roomreservationlib.data.Constant;
import com.huiwei.roomreservationlib.data.Data;
import com.huiwei.roomreservationlib.info.CommentInfo;
import com.huiwei.roomreservationlib.task.CommentListTask;
import com.huiwei.roomreservationlib.task.SubmitCommentTask;
import com.huiwei.roomreservationlib.task.main.NewsDetailTask;

public class NewsDetailActivity extends Activity implements OnClickListener, IXListViewListener {
	
	private XListView commentListView;
	private CommentItemAdapter adapter;
	private LoadingView loadingView;
	private Button startComment, submitComment, share;
	private RelativeLayout layoutComment;
	private EditText commentInput;
	private WebView webView;
	private String newsID;
	
	private List<CommentInfo> commentList = new ArrayList<CommentInfo>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_news_detail);
		
		newsID = getIntent().getStringExtra("news_id");
		loadingView = (LoadingView)findViewById(R.id.loading);
		webView = (WebView) findViewById(R.id.webView);
		((ImageView)findViewById(R.id.iv_return)).setOnClickListener(this);
		
		initCommentEdit();
		initCommentList();
		
		loadingView.setVisibility(View.VISIBLE);
		getDataTask();
	}
	
	
	
	private void initCommentEdit() {
		commentInput = (EditText)findViewById(R.id.et_comment);
		startComment = (Button)findViewById(R.id.btn_comment);
		startComment.setOnClickListener(this);
		share = (Button)findViewById(R.id.btn_share);
		share.setOnClickListener(this);
		submitComment = (Button)findViewById(R.id.btn_submit_comment);
		submitComment.setOnClickListener(this);
		layoutComment = (RelativeLayout)findViewById(R.id.layout_comment);
		
		if (Data.memberInfo.isLogin) {
			((TextView)findViewById(R.id.tv_nickname)).setText(Data.memberInfo.getName());
		} else {
			startComment.setVisibility(View.GONE);
			layoutComment.setVisibility(View.GONE);
			share.setVisibility(View.GONE);
		}
	}
	
	private void getDataTask() {
		NewsDetailTask task = new NewsDetailTask(this, infoHandler, newsID);
		task.execute();
	}
	
	private Handler infoHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == Constant.SUCCESS) {		
				initInfo();
				getCommentTask();
			} else {
				Toast.makeText(NewsDetailActivity.this, getResources().getString(
						R.string.load_data_fail), Toast.LENGTH_SHORT).show();
				loadingView.setVisibility(View.GONE);
			}
			
			((LinearLayout)findViewById(R.id.layout_info)).setVisibility(View.VISIBLE);
			
		};
	};
	
	
	private void getCommentTask() {
		CommentListTask task = new CommentListTask(this, commentHandler, 
				CommentInfo.COMMENT_NEWS, commentList);
		task.execute();
	}
	
	private Handler commentHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == Constant.SUCCESS || 
					msg.what == Constant.DATA_LOAD_COMPLETE) {		
				adapter.notifyDataSetChanged();
			} else {
				Toast.makeText(NewsDetailActivity.this, getResources().getString(
						R.string.load_data_fail), Toast.LENGTH_SHORT).show();
				loadingView.setVisibility(View.GONE);
			}
		};
	};
	
	private void initInfo() {
		((TextView)findViewById(R.id.tv_title)).setText(Data.newsInfo.title);
		((TextView)findViewById(R.id.tv_author)).setText(Data.newsInfo.author);
		((TextView)findViewById(R.id.tv_scan_number)).setText(Data.newsInfo.scanTimes);
		((TextView)findViewById(R.id.tv_time)).setText(Data.newsInfo.time);
		
		WebSettings webSettings = webView.getSettings(); 
		webSettings.setJavaScriptEnabled(true);
		webView.loadUrl(Data.newsInfo.content);
		webView.setWebViewClient(new WebViewClient() {
			@Override
            public void onPageFinished(WebView view, String url) 
            {
				loadingView.setVisibility(View.GONE);
                super.onPageFinished(view, url);
            }
		});
//		((TextView)findViewById(R.id.tv_content)).setText(Html.fromHtml(Data.newsInfo.content));
		
		if (Data.newsInfo.isCommented) {
			startComment.setBackgroundResource(R.drawable.reservation_invalid);
			startComment.setText(getResources().getString(R.string.comment_already));
			startComment.setOnClickListener(null);
		}
	}
	
	private void submitTask() {
		loadingView.setVisibility(View.VISIBLE);
		loadingView.setLoadingText(getResources().getString(R.string.submiting));
		CommentInfo info = new CommentInfo();
		info.newsID = newsID;
//		info.stars = (float)((RatingBar)findViewById(R.id.ratingBar)).getRating();
		info.comment = commentInput.getText().toString();
		SubmitCommentTask task = new SubmitCommentTask(this, submitHandler,
				info, CommentInfo.COMMENT_NEWS);
		task.execute();
	}
	
	private Handler submitHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == Constant.SUCCESS) {		
				Toast.makeText(NewsDetailActivity.this, getResources().getString(
						R.string.comment_success), Toast.LENGTH_SHORT).show();
				layoutComment.setVisibility(View.GONE);
				Data.storeDetailInfo.isCommented = true;
				Data.roomInfo.isCommented = true;
				startComment.setBackgroundResource(R.drawable.reservation_invalid);
				startComment.setText(getResources().getString(R.string.comment_already));
				startComment.setOnClickListener(null);
				commentList.clear();
				getCommentTask();
			} else {
				Toast.makeText(NewsDetailActivity.this, getResources().getString(
						R.string.comment_fail), Toast.LENGTH_SHORT).show();
			}
			
			loadingView.setVisibility(View.GONE);
		};
	};
	
	private void initCommentList() {
		commentListView = (XListView)findViewById(R.id.lv_comments);
		commentListView.setForScrollView(true);
		commentListView.setPullLoadEnable(true);
		commentListView.setXListViewListener(this);
		adapter = new CommentItemAdapter(this, commentList, CommentInfo.COMMENT_NEWS);
		commentListView.setAdapter(adapter);
		
		((ScrollView)findViewById(R.id.scrollview)).smoothScrollTo(0, 0);
	}
	
	@Override
	public void onResume() {
		
		super.onResume();
	}
	
	@Override
	public void onDestroy() {
		
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_return:
			finish();
			break;
			
		case R.id.btn_comment: 
			layoutComment.setVisibility(View.VISIBLE);
			break;
			
		case R.id.btn_submit_comment:
			submitTask();
			break;
		case R.id.btn_share:
            //  Toast.makeText(this, "点击了分享按钮", Toast.LENGTH_LONG).show();
            Intent shareIntent = new Intent();
     		shareIntent.setAction(Intent.ACTION_SEND);
     		shareIntent.putExtra(Intent.EXTRA_TEXT, "【汇为乐汇】  " +Data.newsInfo.title+"\n详情请见 :"+Data.newsInfo.content);
     		shareIntent.setType("text/plain");
     		this.startActivity(Intent.createChooser(shareIntent, this
     				.getResources().getString(R.string.leshare)));
			break;
		default:
			break;
		}
		
	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLoadMore() {
		CommentListTask task = new CommentListTask(this, commentListHandler, 
				CommentInfo.COMMENT_NEWS, commentList);
		task.execute();
	}

	private Handler commentListHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == Constant.SUCCESS) {
				adapter.notifyDataSetChanged();
				commentListView.stopLoadMore();
			} else if (msg.what == Constant.DATA_LOAD_COMPLETE) {
				commentListView.loadComplete();
			}
		};
	};
}
