package com.huiwei.roomreservation.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.huiwei.commonlib.SyncImageLoader;
import com.huiwei.roomreservation.R;
import com.huiwei.roomreservation.adapter.CommentItemAdapter;
import com.huiwei.roomreservation.baseview.XListView;
import com.huiwei.roomreservation.baseview.XListView.IXListViewListener;
import com.huiwei.roomreservation.view.LoadingView;
import com.huiwei.roomreservationlib.data.Constant;
import com.huiwei.roomreservationlib.data.Data;
import com.huiwei.roomreservationlib.data.OrderManager;
import com.huiwei.roomreservationlib.info.CommentInfo;
import com.huiwei.roomreservationlib.task.CommentListTask;
import com.huiwei.roomreservationlib.task.ServiceDetailTask;
import com.huiwei.roomreservationlib.task.StoreDetailTask;
import com.huiwei.roomreservationlib.task.SubmitCommentTask;

public class CommentActivity extends Activity implements OnClickListener, IXListViewListener {
	
	private XListView commentListView;
	private CommentItemAdapter adapter;
	private LoadingView loadingView;
	private Button startComment, submitComment;
	private RelativeLayout layoutComment;
	private EditText commentInput;
	private int commentType;
	
	private List<CommentInfo> commentList = new ArrayList<CommentInfo>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_comment);
		
		commentType = getIntent().getIntExtra("comment_type", CommentInfo.COMMENT_STORE);
		loadingView = (LoadingView)findViewById(R.id.loading);
		
		((ImageView)findViewById(R.id.iv_return)).setOnClickListener(this);
		initCommentList();
		
		loadingView.setVisibility(View.VISIBLE);
		if (commentType == CommentInfo.COMMENT_ORDER) {
			getStroeInfoTask();
			Data.storeDetailInfo.id = Data.orderDetialInfo.storeID;
		} else {
			initCommentEdit();
			initNormalInfo();
			getDataTask();
		}
	}
	
	private void getStroeInfoTask() {
		StoreDetailTask task = new StoreDetailTask(this, 
				Data.orderDetialInfo.storeID, 0, storeInfoHandler);
		task.execute();
	}
	
	private Handler storeInfoHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == Constant.SUCCESS) {
				initCommentEdit();
				initNormalInfo();
				getDataTask();
			} else {
				Toast.makeText(CommentActivity.this, getResources().getString(
						R.string.load_data_fail), Toast.LENGTH_SHORT).show();
			}
		};
	};
	
	private void initCommentEdit() {
		commentInput = (EditText)findViewById(R.id.et_comment);
		startComment = (Button)findViewById(R.id.btn_comment);
		startComment.setOnClickListener(this);
		submitComment = (Button)findViewById(R.id.btn_submit_comment);
		submitComment.setOnClickListener(this);
		layoutComment = (RelativeLayout)findViewById(R.id.layout_comment);
		
		if (Data.memberInfo.isLogin) {
			((TextView)findViewById(R.id.tv_nickname)).setText(Data.memberInfo.getName());
			if (!isCommentable()) {
				resetCommentBtn(R.string.can_not_comment);
			} else {
				if (isCommented()) {
					resetCommentBtn(R.string.comment_already);
				} else {
					if (commentType == CommentInfo.COMMENT_ORDER) {
						layoutComment.setVisibility(View.VISIBLE);
					}
				}
			}
		} else {
			startComment.setVisibility(View.GONE);
			layoutComment.setVisibility(View.GONE);
		}
	}
	
	private boolean isCommentable() {
		if (commentType == CommentInfo.COMMENT_ROOM) {
			return Data.roomInfo.isOrdered;
		} else if (commentType == CommentInfo.COMMENT_SERVICE) {
			return Data.serviceInfo.isOrdered;
		} else {
			return Data.storeDetailInfo.isOrdered;
		}
	}
	
	private boolean isCommented() {
		if (commentType == CommentInfo.COMMENT_ROOM) {
			return Data.roomInfo.isCommented;
		} else if (commentType == CommentInfo.COMMENT_SERVICE) {
			return Data.serviceInfo.isCommented;
		} else if (commentType == CommentInfo.COMMENT_ORDER) {
			return Data.orderDetialInfo.isCommented;
		} else {
			return Data.storeDetailInfo.isCommented;
		}
	}
	
	private void resetCommentBtn(int textResID) {
		int left = startComment.getPaddingLeft();
		int right = startComment.getPaddingRight();
		startComment.setBackgroundResource(R.drawable.reservation_invalid);
		startComment.setText(getResources().getString(textResID));
		startComment.setOnClickListener(null);
		startComment.setPadding(left, 0, right, 0);
	}
	
	private void setInfo(String iconUrl, String name, String title1, String info2, 
			String info3, String price, String priceType) {
		loadImage(iconUrl);
		((TextView)findViewById(R.id.tv_name)).setText(name);
		((TextView)findViewById(R.id.tv_title_1)).setText(title1);
		((TextView)findViewById(R.id.tv_info_2)).setText(info2);
		((TextView)findViewById(R.id.tv_info_3)).setText(info3);
		
		((TextView)findViewById(R.id.tv_room_price)).setText(price);
		((TextView)findViewById(R.id.tv_price_type)).setText(priceType);
	}
	
	private void initNormalInfo() {
		switch (commentType) {
		case CommentInfo.COMMENT_ROOM: {
			setInfo(Data.roomInfo.iconUrl, Data.roomInfo.name, getResources().getString(
					R.string.comment_hot_level), Data.roomInfo.otherTitle+"："+Data.roomInfo.otherInfo,
					"规格："+Data.roomInfo.roomClassify, getResources().getString(R.string.yuan)+Data.roomInfo.price,
					Data.roomInfo.priceType);
		}
			break;
			
		case CommentInfo.COMMENT_SERVICE: {
			setInfo(Data.serviceInfo.iconUrl, Data.serviceInfo.name, getResources().getString(
					R.string.comment_hot_level), Data.serviceInfo.otherTitle+"："+Data.serviceInfo.otherInfo, 
					"项目时长："+Data.serviceInfo.serviceDuration, 
					getResources().getString(R.string.yuan)+Data.serviceInfo.price, Data.serviceInfo.priceType);
		}
			break;
			
		case CommentInfo.COMMENT_ORDER:
		case CommentInfo.COMMENT_STORE: {
			setInfo(Data.storeDetailInfo.iconUrl, Data.storeDetailInfo.name, getResources().getString(
					R.string.comment_recommend_level), "包间个数："+Data.storeDetailInfo.roomNum+"\n服务项目："+
					Data.storeDetailInfo.serviceNum, "地址："+Data.storeDetailInfo.address, "", "");
		}
			break;
		default:
			break;
		}
		
		if (commentType == CommentInfo.COMMENT_ROOM || commentType == CommentInfo.COMMENT_SERVICE) {
			((RatingBar)findViewById(R.id.ratingBar)).setVisibility(View.GONE);
			ProgressBar pbHot = (ProgressBar)findViewById(R.id.pb_hot);
			pbHot.setVisibility(View.VISIBLE);
			Drawable daHot = getResources().getDrawable(R.drawable.room_hot_bg);
			int width = daHot.getIntrinsicWidth()*pbHot.getHeight()/daHot.getIntrinsicHeight();
			pbHot.setMinimumWidth(width);
			RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
					daHot.getIntrinsicWidth(), RelativeLayout.LayoutParams.WRAP_CONTENT);
			lp.addRule(RelativeLayout.RIGHT_OF, R.id.tv_title_1);
			pbHot.setLayoutParams(lp);
			pbHot.setProgress(commentType == CommentInfo.COMMENT_ROOM ? Data.roomInfo.hot : Data.serviceInfo.hot);
		} else {
			((RatingBar)findViewById(R.id.ratingBar)).setRating(Data.storeDetailInfo.stars);
		}
	}
	
	private void getDataTask() {
		CommentListTask task = new CommentListTask(this, taskHandler, 
				commentType, commentList);
		task.execute();
	}
	
	private Handler taskHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == Constant.SUCCESS || msg.what == Constant.DATA_LOAD_COMPLETE) {		
				adapter.notifyDataSetChanged();
				if (commentList.size() == 0) {
					commentListView.setNoDataHint(getResources().getString(
							R.string.xlistview_footer_no_comment));
				}
			} else {
				Toast.makeText(CommentActivity.this, getResources().getString(
						R.string.load_data_fail), Toast.LENGTH_SHORT).show();
			}
			
			loadingView.setVisibility(View.GONE);
		};
	};
	
	private void submitTask() {
		loadingView.setVisibility(View.VISIBLE);
		loadingView.setLoadingText(getResources().getString(R.string.submiting));
		CommentInfo info = new CommentInfo();
		info.stars = (float)((RatingBar)findViewById(R.id.rating_comment)).getRating();
		info.comment = commentInput.getText().toString();
		if (commentType == CommentInfo.COMMENT_ORDER) {
			info.orderID = Data.orderDetialInfo.id;
			info.storeID = Data.orderDetialInfo.storeID;
		}
		SubmitCommentTask task = new SubmitCommentTask(this, submitHandler,
				info, commentType);
		task.execute();
	}
	
	private Handler submitHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == Constant.SUCCESS) {		
				Toast.makeText(CommentActivity.this, getResources().getString(
						R.string.comment_success), Toast.LENGTH_SHORT).show();
				layoutComment.setVisibility(View.GONE);
				if (commentType == CommentInfo.COMMENT_ORDER) {
					Data.orderDetialInfo.isCommented = true;
					OrderManager.commentOrder(Data.orderDetialInfo.id);
				} else {
					Data.storeDetailInfo.isCommented = true;
					Data.roomInfo.isCommented = true;
				}
				
				resetCommentBtn(R.string.comment_already);
				getDataTask();
			} else if (msg.what == Constant.DATA_ERROR) {
				Toast.makeText(CommentActivity.this, (String)msg.obj, Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(CommentActivity.this, getResources().getString(
						R.string.comment_fail), Toast.LENGTH_SHORT).show();
			}
			
			loadingView.setVisibility(View.GONE);
		};
	};
	
	private void loadImage(String iconUrl) {
		final ImageView img = (ImageView)findViewById(R.id.iv_icon);
		if (iconUrl == null || iconUrl.length() == 0) {
			return;
		}
		
		SyncImageLoader imageLoader = new SyncImageLoader(this);
		if (iconUrl != null && !iconUrl.equals("")) {
			imageLoader.loadImage(iconUrl,
					new SyncImageLoader.OnImageLoadListener() {
						
						@Override
						public void onImageLoad(Bitmap bitmap) {
							if (bitmap == null) {
								img.setImageBitmap(null);
								img.setBackgroundResource(R.drawable.default_icon);
							} else {
								img.setImageBitmap(bitmap);
								img.setBackgroundDrawable(null);
							}
						}
						
						@Override
						public void onError() {
							img.setImageBitmap(null);
							img.setBackgroundResource(R.drawable.default_icon);
						}
					}); 
			
		} else {
			img.setImageBitmap(null);
			img.setBackgroundResource(R.drawable.default_icon);
		}
	}
	
	private void initCommentList() {
		commentListView = (XListView)findViewById(R.id.lv_comments);
		commentListView.setForScrollView(true);
		commentListView.setPullLoadEnable(true);
		commentListView.setXListViewListener(this);
		adapter = new CommentItemAdapter(this, commentList, commentType);
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
				commentType, commentList);
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
