package com.huiwei.roomreservation.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.Scroller;
import android.widget.TextView;
import android.widget.Toast;

import com.huiwei.roomreservation.R;
import com.huiwei.roomreservation.adapter.RoomItemAdapter;
import com.huiwei.roomreservation.baseview.XListView;
import com.huiwei.roomreservation.baseview.XListView.IXListViewListener;
import com.huiwei.roomreservation.common.CommonConstant;
import com.huiwei.roomreservation.view.BottomTabView;
import com.huiwei.roomreservation.view.LoadingView;
import com.huiwei.roomreservation.view.PictureView;
import com.huiwei.roomreservationlib.data.Constant;
import com.huiwei.roomreservationlib.data.Data;
import com.huiwei.roomreservationlib.info.CommentInfo;
import com.huiwei.roomreservationlib.info.NewsInfo;
import com.huiwei.roomreservationlib.info.StoreDetailInfo;
import com.huiwei.roomreservationlib.task.CollectStoreTask;
import com.huiwei.roomreservationlib.task.StoreDetailTask;

public class StoreDetailActivity extends Activity implements OnClickListener,
		IXListViewListener {

	private XListView roomListView;
	private RoomItemAdapter adapter;
	private PictureView pictureView;
	private BottomTabView bottomTabView;
	private Button favoriteTip, tabRoom, tabService, tabArtificer;
	private LoadingView loadingView;
	private TextView collect, privilege;

	private String storeID;
	private Timer timer;
	private String privilegeMsg = "";
	private int listType = StoreDetailInfo.TYPE_ROOM;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_store_detail);

		favoriteTip = (Button) findViewById(R.id.iv_favorite_icn);
		storeID = getIntent().getStringExtra("store_id");
		loadingView = (LoadingView) findViewById(R.id.loading);
		pictureView = (PictureView) findViewById(R.id.view_picture);
		((ImageView) findViewById(R.id.iv_return)).setOnClickListener(this);
		((Button) findViewById(R.id.btn_phone_call)).setOnClickListener(this);
		
		privilege = (TextView) findViewById(R.id.tv_privilege);
		privilege.setOnClickListener(this);

		bottomTabView = (BottomTabView)findViewById(R.id.view_tab);
		bottomTabView.setCommentType(CommentInfo.COMMENT_STORE);
		
		((LinearLayout) findViewById(R.id.layout_info)).setOnClickListener(this);
		
		initTypeTab();

		collect = (TextView) findViewById(R.id.tv_favorite);
		collect.setOnClickListener(this);

		getDataTask();
	}
	
	private void initTypeTab() {
		tabRoom = (Button)findViewById(R.id.btn_room);
		tabService = (Button)findViewById(R.id.btn_service);
		tabArtificer = (Button)findViewById(R.id.btn_artificer);
		tabRoom.setOnClickListener(this);
		tabService.setOnClickListener(this);
		tabArtificer.setOnClickListener(this);
	}

	private void getDataTask() {
		loadingView.setVisibility(View.VISIBLE);
		StoreDetailTask task = new StoreDetailTask(this, storeID, 0,
				taskHandler);
		task.execute();
	}

	private Handler taskHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == Constant.SUCCESS) {
				pictureView.setPicture(Data.storeDetailInfo.picture360Url, 
						Data.storeDetailInfo.picUrlList);
				setInfo();
				if (Data.storeDetailInfo.roomList.size() == 0) {
					roomListView.setNoDataHint(getResources().getString(
							R.string.xlistview_footer_no_rooms));
				}
			} else if (msg.what == Constant.DATA_ERROR) {
				Toast.makeText(StoreDetailActivity.this, (String)msg.obj,
						Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(StoreDetailActivity.this,
						getResources().getString(R.string.load_data_fail),
						Toast.LENGTH_SHORT).show();
			}

			loadingView.setVisibility(View.GONE);
		};
	};
	
	private void resetTabButton(int type) {
		listType = type;
		tabRoom.setBackgroundResource(R.drawable.top_tab_up);
		tabService.setBackgroundResource(R.drawable.top_tab_up);
		tabArtificer.setBackgroundResource(R.drawable.top_tab_up);
		if (type == StoreDetailInfo.TYPE_ROOM) {
			tabRoom.setBackgroundResource(R.drawable.top_tab_down);
		} else if(type == StoreDetailInfo.TYPE_SERVICE) {
			tabService.setBackgroundResource(R.drawable.top_tab_down);
		} else {
			tabArtificer.setBackgroundResource(R.drawable.top_tab_down);
		}
		
		adapter.setListType(type);
		adapter.notifyDataSetChanged();
	}

	private void setInfo() {
		((TextView) findViewById(R.id.tv_title))
				.setText(Data.storeDetailInfo.name);
		((TextView) findViewById(R.id.tv_service))
				.setText(Data.storeDetailInfo.service);
		((TextView) findViewById(R.id.tv_recommend))
			.setText(Data.storeDetailInfo.recommend);
		((TextView) findViewById(R.id.tv_address))
				.setText(Data.storeDetailInfo.address);
		((RatingBar) findViewById(R.id.ratingBar))
				.setRating(Data.storeDetailInfo.stars);
		updateCollect();
		
		setPrivilege();

		roomListView = (XListView) findViewById(R.id.lv_rooms);
		roomListView.setForScrollView(true);
		roomListView.setPullLoadEnable(true);
		roomListView.setXListViewListener(this);
		adapter = new RoomItemAdapter(this);
		roomListView.setAdapter(adapter);
		roomListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int index,
					long arg3) {
				Intent intent = new Intent();
				intent.putExtra("room_index", index);
				if (listType == StoreDetailInfo.TYPE_ROOM) {
					intent.setClass(StoreDetailActivity.this,
							RoomDetailActivity.class);
				} else if (listType == StoreDetailInfo.TYPE_SERVICE) {
					intent.setClass(StoreDetailActivity.this,
							ServiceDetailActivity.class);
				}
				startActivity(intent);
			}
		});

		((ScrollView) findViewById(R.id.scrollview)).smoothScrollTo(0, 0);
	}
	
	private void setPrivilege() {
		for (int i=0; i<Data.storeDetailInfo.newsList.size(); i++) {
			NewsInfo info = Data.storeDetailInfo.newsList.get(i);
			privilegeMsg += ""+(i+1)+"."+info.name+"    ";
		}
		privilege.setText(privilegeMsg);
		privilege.setFocusable(true);
		privilege.setFocusableInTouchMode(true);
		privilege.requestFocus();
	}

	private void updateCollect() {
		collect.setBackgroundResource(Data.storeDetailInfo.favorite ? R.drawable.heart_act
				: R.drawable.heart);
		((TextView) findViewById(R.id.tv_favorite_num)).setText(String
				.valueOf(Data.storeDetailInfo.favoriteNum));
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onDestroy() {
		if (Data.storeDetailInfo != null) {
			Data.storeDetailInfo.clearData();
		}

		if (Data.roomInfo != null) {
			Data.roomInfo.clearData();
		}

		if (timer != null) {
			timer.cancel();
		}

		super.onDestroy();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == MainActivity.TO_LOGIN) {
			if (resultCode != LoginActivity.RETURN) {
				Intent intent = new Intent();
				intent.setClass(StoreDetailActivity.this,
						RoomReservationActivity.class);
				startActivity(intent);
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_return:
			finish();
			break;

		case R.id.btn_phone_call: {
			Intent intent = new Intent();
			intent.setAction("android.intent.action.DIAL");
			intent.setData(Uri.parse("tel:"
					+ ((TextView) findViewById(R.id.tv_phone_number)).getText()));
			// startActivity(intent);
			startActivityForResult(intent, RESULT_OK);
		}
			break;
			
		case R.id.tv_privilege: {
			List<String> msgList = new ArrayList<String>();
			for (NewsInfo info : Data.storeDetailInfo.newsList) {
				msgList.add(info.name);
			}
			new AlertDialog.Builder(StoreDetailActivity.this)
			.setTitle(R.string.choose_news)
			.setIcon(android.R.drawable.ic_dialog_info)                
			.setSingleChoiceItems((String[]) msgList.toArray(
					new String[msgList.size()]), 0, 
			  new DialogInterface.OnClickListener() {
			                            
			     public void onClick(DialogInterface dialog, int which) {
			        dialog.dismiss();
			        Intent intent = new Intent();
					intent.setClass(StoreDetailActivity.this, NewsDetailActivity.class);
					intent.putExtra("news_id", Data.storeDetailInfo.newsList.get(which).id);
					startActivity(intent);
			     }
			  }
			)
			.show();
		}
			break;

		case R.id.tv_favorite: {
			if (Data.memberInfo.isLogin) {
				handler.sendEmptyMessage(CommonConstant.START_COLLECT);
				CollectStoreTask task = new CollectStoreTask(
						StoreDetailActivity.this, handler,
						Data.storeDetailInfo.id, !Data.storeDetailInfo.favorite);
				task.execute();
			} else {
				Intent intent = new Intent();
				intent.setClass(StoreDetailActivity.this, LoginActivity.class);
				startActivity(intent);
				Toast.makeText(
						StoreDetailActivity.this,
						getResources().getString(R.string.please_login_collect),
						Toast.LENGTH_SHORT).show();
			}
		}
			break;

		case R.id.layout_info:
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setInverseBackgroundForced(true);
			builder.setTitle("详情介绍");
			builder.setMessage(
					"商家名称："+Data.storeDetailInfo.name + "\n"
					+"商家地址："+ Data.storeDetailInfo.address + "\n"
					//+ Data.storeDetailInfo.classify + "\n"
					//+ Data.storeDetailInfo.cpp + "\n"
					+"距离："+ Data.storeDetailInfo.distance + "\n"
					//+ Data.storeDetailInfo.favoriteNum + "\n"
					+"特色服务："+ Data.storeDetailInfo.service + "\n"
					+"推荐项目："+ Data.storeDetailInfo.recommend + "\n"
					+"优惠信息："+ privilegeMsg + "\n"
					+Data.storeDetailInfo.consumeTitle+"："+ Data.storeDetailInfo.consumeValue + "\n"
					//+ Data.storeDetailInfo.iconUrl + "\n" 
					//+ Data.storeDetailInfo.id+ "\n" 
					+"纬度："+ Data.storeDetailInfo.latitude + "\n"
					//+ Data.storeDetailInfo.localPicPath + "\n"
					+"经度："+ Data.storeDetailInfo.longitude + "\n"
					+"电话："+ Data.storeDetailInfo.phoneNumber + "\n"
					//+ Data.storeDetailInfo.picture360Url + "\n"
					+"推荐度："+ Data.storeDetailInfo.recommend + "\n"
					//+ Data.storeDetailInfo.roomNum + "\n"
					//+ Data.storeDetailInfo.service + "\n"
					//+ Data.storeDetailInfo.storeTips + "\n"
					+"营业时间："+ Data.storeDetailInfo.time);
			builder.setNeutralButton("关闭详情页",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
						}
					});
			builder.show();
			break;
			
		case R.id.btn_room:
			resetTabButton(StoreDetailInfo.TYPE_ROOM);
			break;
			
		case R.id.btn_service:
			resetTabButton(StoreDetailInfo.TYPE_SERVICE);
			break;
			
		case R.id.btn_artificer:
			resetTabButton(StoreDetailInfo.TYPE_ARTIFICER);
			break;
			
		default:
			break;
		}

	}

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if (msg.what == Constant.SUCCESS) {
				Data.storeDetailInfo.favorite = (msg.arg1 == 1);
				favoriteTip.setVisibility(View.VISIBLE);
				favoriteTip
						.setText(getResources()
								.getString(
										Data.storeDetailInfo.favorite ? R.string.collect_success
												: R.string.collect_cancel));
				setTimer();
				loadingView.setVisibility(View.GONE);

				Data.collectStore((String) msg.obj,
						Data.storeDetailInfo.favorite);
				if (Data.storeDetailInfo.favorite) {
					Data.storeDetailInfo.favoriteNum += 1;
				} else {
					Data.storeDetailInfo.favoriteNum -= 1;
				}
				updateCollect();
			} else if (msg.what == Constant.DATA_ERROR) {
				Toast.makeText(StoreDetailActivity.this, (String) msg.obj,
						Toast.LENGTH_SHORT).show();
				loadingView.setVisibility(View.GONE);
			} else if (msg.what == CommonConstant.CANCEL_TIMER) {
				favoriteTip.setVisibility(View.GONE);
				timer.cancel();
			} else if (msg.what == CommonConstant.START_COLLECT) {
				loadingView.setLoadingText(getResources().getString(
						R.string.submiting));
				loadingView.setVisibility(View.VISIBLE);
			} else {
				loadingView.setVisibility(View.GONE);
			}
		};
	};

	private void setTimer() {
		timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				handler.sendEmptyMessage(CommonConstant.CANCEL_TIMER);

			}
		}, 1 * 1000, 1 * 1000);
	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onLoadMore() {
		StoreDetailTask task = new StoreDetailTask(this,
				Data.storeDetailInfo.id, Data.storeDetailInfo.roomList.size(),
				roomListHandler);
		task.execute();
	}

	private Handler roomListHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == Constant.SUCCESS) {
				adapter.notifyDataSetChanged();
				roomListView.stopLoadMore();
			} else if (msg.what == Constant.DATA_LOAD_COMPLETE) {
				roomListView.loadComplete();
			}
		};
	};
}
