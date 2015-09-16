package com.huiwei.roomreservation.view;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.huiwei.roomreservation.R;
import com.huiwei.roomreservation.activity.OrderDetailActivity;
import com.huiwei.roomreservation.activity.StoreDetailActivity;
import com.huiwei.roomreservation.adapter.MemberCommentItemAdapter;
import com.huiwei.roomreservation.adapter.OrderItemAdapter;
import com.huiwei.roomreservation.adapter.ScoreItemAdapter;
import com.huiwei.roomreservation.adapter.StoreItemAdapter;
import com.huiwei.roomreservation.baseview.XListView;
import com.huiwei.roomreservation.baseview.XListView.IXListViewListener;
import com.huiwei.roomreservationlib.data.Constant;
import com.huiwei.roomreservationlib.data.Data;
import com.huiwei.roomreservationlib.data.OrderManager;
import com.huiwei.roomreservationlib.info.OrderDetialInfo;
import com.huiwei.roomreservationlib.info.StoreDetailInfo;
import com.huiwei.roomreservationlib.task.AroundStoreListTask;
import com.huiwei.roomreservationlib.task.FavoriteStoreListTask;
import com.huiwei.roomreservationlib.task.StoreListTask;
import com.huiwei.roomreservationlib.task.member.MemberCommentListTask;
import com.huiwei.roomreservationlib.task.member.ScoreListTask;
import com.huiwei.roomreservationlib.task.order.OrderListTask;

public class ViewPagerListView extends RelativeLayout implements IXListViewListener {

	public static final int TYPE_ORDER = 0;
	public static final int TYPE_COMMENT = 1;
	public static final int TYPE_SCORE = 2;
	public static final int TYPE_RECOMMENT = 3;
	public static final int TYPE_AROUND = 4;
	public static final int TYPE_FAVORITE = 5;

	private Context mContext;
	private XListView listView;
	private ProgressBar pBar;
	private BaseAdapter adapter;
	private int curType;
	
	public ViewPagerListView(Context context) {
		super(context);
		mContext = context;
	}
	
	public ViewPagerListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
	}
	
	@Override
	protected void onFinishInflate() {
		// TODO Auto-generated method stub
		super.onFinishInflate();
		
		pBar = (ProgressBar)findViewById(R.id.pb);
		listView = (XListView)findViewById(R.id.lv_member_order);
	}
	
	public void setListType(int type, Handler handler) {
		curType = type;
		switch (type) {
		case TYPE_ORDER: {
			adapter = new OrderItemAdapter(mContext);
			listView.setOnItemClickListener(orderClickListener);
			OrderManager.clearOrderList();
			pBar.setVisibility(View.VISIBLE);
			startOrderListTask();
		}
			break;
			
		case TYPE_COMMENT:
			adapter = new MemberCommentItemAdapter(mContext);
			listView.setSelector(getResources().getDrawable(R.color.transprant));
			Data.clearCommentList();
			pBar.setVisibility(View.VISIBLE);
			startCommentListTask();
			break;
			
		case TYPE_SCORE:
			adapter = new ScoreItemAdapter(mContext);
			listView.setSelector(getResources().getDrawable(R.color.transprant));
			Data.clearScoreList();
			pBar.setVisibility(View.VISIBLE);
			startScoreListTask();
			break;
			
		case TYPE_RECOMMENT:
		case TYPE_AROUND:
		case TYPE_FAVORITE: {
			if (type == TYPE_RECOMMENT) {
				adapter = new StoreItemAdapter(mContext, Data.storeList, handler);
			} else if (type == TYPE_AROUND) {
				adapter = new StoreItemAdapter(mContext, Data.aroundStoreList, handler);
			} else if (type == TYPE_FAVORITE) {
				adapter = new StoreItemAdapter(mContext, Data.favoriteStoreList, handler);
			}
			
			listView.setOnItemClickListener(itemClickListener);
			listView.setDivider(getResources().getDrawable(R.color.store_item_split));
			listView.setDividerHeight(1);
			pBar.setVisibility(View.VISIBLE);
			startStoreListTask(true);
		}
			break;
		}
		
		listView.setPullLoadEnable(true);
		listView.setXListViewListener(this);
		listView.setAdapter(adapter);
		
	}
	
	public void refreshListView() {
		adapter.notifyDataSetChanged();
	}
	
	private OnItemClickListener itemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
//			if (curType == TYPE_RECOMMENT || curType == TYPE_AROUND || curType == TYPE_FAVORITE)
//				Data.clearStoreList();
			String storeID = "";
			switch (curType) {
			case TYPE_RECOMMENT:
				storeID = Data.storeList.get(position).id;
				break;
				
			case TYPE_AROUND:
				storeID = Data.aroundStoreList.get(position).id;
				break;
				
			case TYPE_FAVORITE:
				storeID = Data.favoriteStoreList.get(position).id;
				break;
			}
				
			Intent intent = new Intent();
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.putExtra("store_id", storeID);
			intent.setClass(mContext, StoreDetailActivity.class);
			mContext.startActivity(intent);
		}
	};
	
	private OnItemClickListener orderClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
//			if (Data.orderList.get(position).orderType == StoreDetailInfo.TYPE_ROOM) {
				Intent intent = new Intent();
				Data.orderDetialInfo.copy(Data.orderList.get(position));
				intent.setClass(mContext, OrderDetailActivity.class);
				mContext.startActivity(intent);
//			}
		}
	};

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLoadMore() {
		switch (curType) {
		case TYPE_ORDER: 
			startOrderListTask();
			break;
			
		case TYPE_RECOMMENT:
		case TYPE_AROUND:
		case TYPE_FAVORITE:
			startStoreListTask(false);
			break;
			
		case TYPE_SCORE:
			startScoreListTask();
			break;
			
		case TYPE_COMMENT:
			startCommentListTask();
			break;
		}
		
	}

	private void startOrderListTask() {
		OrderListTask task = new OrderListTask(mContext, taskHandler);
		task.execute();
	}
	
	private void startCommentListTask() {
		MemberCommentListTask task = new MemberCommentListTask(mContext, taskHandler);
		task.execute();
	}
	
	private void startScoreListTask() {
		ScoreListTask task = new ScoreListTask(mContext, taskHandler);
		task.execute();
	}
	
	public void reloadStoreList() {
		pBar.setVisibility(View.VISIBLE);
		startStoreListTask(true);
	}
	
	public void reloadOrderList() {
		pBar.setVisibility(View.VISIBLE);
		OrderManager.clearOrderList();
		startOrderListTask();
	}
	
	public void reloadScoreList() {
		pBar.setVisibility(View.VISIBLE);
		Data.clearScoreList();
		startScoreListTask();
	}
	
	public void reloadCommentList() {
		pBar.setVisibility(View.VISIBLE);
		Data.clearCommentList();
		startCommentListTask();
	}
	
	private void startStoreListTask(boolean reload) {
		switch (curType) {
		case TYPE_RECOMMENT: {
			StoreListTask task = new StoreListTask(mContext, taskHandler,
					reload ? 0 : Data.storeList.size());
			task.execute();
		}
			break;
			
		case TYPE_AROUND: {
			AroundStoreListTask task = new AroundStoreListTask(mContext, taskHandler,
					reload ? 0 : Data.aroundStoreList.size());
			task.execute();
		}
			break;
			
		case TYPE_FAVORITE: {
			FavoriteStoreListTask task = new FavoriteStoreListTask(mContext, taskHandler,
					reload ? 0 : Data.favoriteStoreList.size());
			task.execute();
		}
			break;

		default:
			break;
		}
	}
	
	private Handler taskHandler = new Handler() {
		public void dispatchMessage(android.os.Message msg) {
			if (adapter == null)
				return;
			
			if (msg.what == Constant.SUCCESS) {
				adapter.notifyDataSetChanged();
				listView.stopLoadMore();
			} else if (msg.what == Constant.DATA_LOAD_COMPLETE) {
				listView.loadComplete();
			}
			
			pBar.setVisibility(View.GONE);
		};
	};
}
	
