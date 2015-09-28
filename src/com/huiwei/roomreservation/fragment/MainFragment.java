package com.huiwei.roomreservation.fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.android.bbalbs.common.a.b;
import com.baidu.mapapi.map.Text;
import com.huiwei.commonlib.CommonFunction;
import com.huiwei.commonlib.Preferences;
import com.huiwei.roomreservation.R;
import com.huiwei.roomreservation.activity.MainActivity;
import com.huiwei.roomreservation.activity.NewsDetailActivity;
import com.huiwei.roomreservation.activity.StoreDetailActivity;
import com.huiwei.roomreservation.activity.StoreListActivity;
import com.huiwei.roomreservation.activity.StoreSceneActivity;
import com.huiwei.roomreservation.activity.WebActivity;
import com.huiwei.roomreservation.activity.setting.SettingActivity;
import com.huiwei.roomreservation.adapter.StoreItemAdapter;
import com.huiwei.roomreservation.baseview.InterceptScrollView;
import com.huiwei.roomreservation.baseview.XListView;
import com.huiwei.roomreservation.baseview.XListView.IXListViewListener;
import com.huiwei.roomreservation.common.CommonConstant;
import com.huiwei.roomreservation.common.SysApplication;
import com.huiwei.roomreservation.view.ClassifyItemView;
import com.huiwei.roomreservation.view.LoadingView;
import com.huiwei.roomreservation.view.MainMenuView;
import com.huiwei.roomreservation.view.RecommendPictrueView;
import com.huiwei.roomreservation.view.ViewPagerListView;
import com.huiwei.roomreservationlib.data.Constant;
import com.huiwei.roomreservationlib.data.Data;
import com.huiwei.roomreservationlib.info.NewsInfo;
import com.huiwei.roomreservationlib.info.RecommendInfo.RecommendItemInfo;
import com.huiwei.roomreservationlib.task.main.MainPageTask;

public class MainFragment extends Fragment implements OnClickListener, IXListViewListener {
	
	private XListView listViewContent;
	private StoreItemAdapter adapter;
	private Button favoriteTip;
	private TextView recommendMsg;
	private TextView cityName;
	private RecommendPictrueView recommendLeftView, recommendRightView1, recommendRightView2;
	private Timer timer;
	private LinearLayout layoutRecommend;
	private RelativeLayout layoutFloat;
	private boolean hasViewMeasured = false;
	private boolean hasTabMeasured = false;
	private boolean isStoreSceneFloat = true;
	private LoadingView loadingView;
	private Handler parentHandler;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_main,
				container, false);
	
		hasViewMeasured = false;
		hasTabMeasured = false;
		loadingView = (LoadingView)view.findViewById(R.id.loading);
		favoriteTip = (Button)view.findViewById(R.id.iv_favorite_icn);
		listViewContent = (XListView)view.findViewById(R.id.lv_content);
		recommendMsg = (TextView)view.findViewById(R.id.tv_recommend_msg);
		recommendMsg.setOnClickListener(this);
		recommendLeftView = (RecommendPictrueView)view.findViewById(R.id.view_recommend_left);
		recommendRightView1 = (RecommendPictrueView)view.findViewById(R.id.view_recommend_right_1);
		recommendRightView2 = (RecommendPictrueView)view.findViewById(R.id.view_recommend_right_2);
		recommendLeftView.setOnClickListener(this);
		recommendRightView1.setOnClickListener(this);
		recommendRightView2.setOnClickListener(this);
		
		((ImageButton)view.findViewById(R.id.btn_menu)).setOnClickListener(this);
		cityName = (TextView)view.findViewById(R.id.tv_city);
		cityName.setOnClickListener(this);
		
		layoutRecommend = (LinearLayout)view.findViewById(R.id.layout_recommend);
		layoutFloat = (RelativeLayout)view.findViewById(R.id.layout_store_float);
		resizeFloatLayout();
		
		((ImageButton)view.findViewById(R.id.ib_store_float_cancel)).setOnClickListener(this);
		
		initButtons(view);
		initRecommendView();
		initListView();
		
		isStoreSceneFloat = Preferences.GetBoolean(getActivity(), 
				CommonConstant.KEY_STORE_SCENE_FLOAT, true);
		if (Data.recommendInfo.recommendStoreList.size() == 0) {
			loadingView.setVisibility(View.VISIBLE);
			startGetDataTask(true);
		} else {
			updatePageData();
		}
		
		return view;
	}
	
	public void setParentHandler(Handler handler) {
		parentHandler = handler;
	}
	
	public void setDefaultCity() {
		cityName.setText(Data.findCityInfo(Preferences.GetString(
				getActivity(), CommonConstant.KEY_CITY_ID)).name);
	}
	
	private void resizeFloatLayout() {
		layoutFloat.setOnClickListener(this);
		ViewTreeObserver vto = layoutFloat.getViewTreeObserver();
		vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
			public boolean onPreDraw() {
				if (hasTabMeasured == false) {
					int screenWidth = getResources().getDisplayMetrics().widthPixels;
					int resultWidth = screenWidth/3;
					Drawable drawable = getResources().getDrawable(R.drawable.store_button_bg);
					int resultHeight = resultWidth*drawable.getIntrinsicHeight()/drawable.getIntrinsicWidth();
					RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
							resultWidth, resultHeight);
					lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
					lp.setMargins(0, CommonFunction.dip2px(getActivity(), 150), 0, 0);
					layoutFloat.setLayoutParams(lp);

					hasTabMeasured = true;
				}
				return true;
			}
		});
	}
	
	public void startGetDataTask(boolean isLocalData) {
		MainPageTask task = new MainPageTask(getActivity(), taskHandler, isLocalData);
		task.execute("");
	}
	
	private Handler taskHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == Constant.SUCCESS) {
//				initListView();
				updatePageData();
			}
			
			loadingView.setVisibility(View.GONE);
		};
	};
	
	private void initButtons(View view) {
		((ClassifyItemView)view.findViewById(R.id.classify_view_1)).setClassify(
				ClassifyItemView.CLASSIFY_XY, getResources().getString(R.string.classify_xy));
		((ClassifyItemView)view.findViewById(R.id.classify_view_2)).setClassify(
				ClassifyItemView.CLASSIFY_ZL, getResources().getString(R.string.classify_zl));
		((ClassifyItemView)view.findViewById(R.id.classify_view_3)).setClassify(
				ClassifyItemView.CLASSIFY_KTV, getResources().getString(R.string.classify_ktv));
		((ClassifyItemView)view.findViewById(R.id.classify_view_4)).setClassify(
				ClassifyItemView.CLASSIFY_HS, getResources().getString(R.string.classify_hs));
		((ClassifyItemView)view.findViewById(R.id.classify_view_5)).setClassify(
				ClassifyItemView.CLASSIFY_CYBF, getResources().getString(R.string.classify_cybf));
		((ClassifyItemView)view.findViewById(R.id.classify_view_6)).setClassify(
				ClassifyItemView.CLASSIFY_CSKF, getResources().getString(R.string.classify_cskf));
		((ClassifyItemView)view.findViewById(R.id.classify_view_7)).setClassify(
				ClassifyItemView.CLASSIFY_MRMT, getResources().getString(R.string.classify_mrmt));
		((ClassifyItemView)view.findViewById(R.id.classify_view_8)).setClassify(
				ClassifyItemView.CLASSIFY_GD, getResources().getString(R.string.classify_gd));
	}
	
	public void updatePageData() {
		String message = "";
		for (int i=0; i<Data.recommendInfo.newsList.size(); i++) {
			NewsInfo info = Data.recommendInfo.newsList.get(i);
			message += ""+(i+1)+"."+info.name+"    ";
		}
		recommendMsg.setText(message);
		recommendLeftView.setInfo(Data.recommendInfo.leftItemInfo);
		recommendRightView1.setInfo(Data.recommendInfo.rightTopItemInfo);
		recommendRightView2.setInfo(Data.recommendInfo.rightBottomItemInfo);
		enableStoreScene();
		adapter.setData(Data.recommendInfo.recommendStoreList);
	}
	
	private void initRecommendView() {
		ViewTreeObserver vto = layoutRecommend.getViewTreeObserver();
		vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
			public boolean onPreDraw() {
				if (hasViewMeasured == false) {
					int height = layoutRecommend.getWidth() * 65 / (52 * 2);
					int margin = ((LinearLayout.LayoutParams) layoutRecommend
							.getLayoutParams()).leftMargin;
					LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
							layoutRecommend.getWidth(), height);
					lp.setMargins(margin, margin, margin, margin);
					layoutRecommend.setLayoutParams(lp);

					hasViewMeasured = true;
				}
				return true;
			}
		});
	}

	private void initListView() {
		listViewContent.setForScrollView(true);
		listViewContent.setPullLoadEnable(true);
		listViewContent.setXListViewListener(this);
		adapter = new StoreItemAdapter(getActivity(),  handler);
		listViewContent.setAdapter(adapter);
		listViewContent.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent();
				intent.putExtra("store_id", Data.recommendInfo.
						recommendStoreList.get(position).id);
				intent.setClass(getActivity().getApplicationContext(),
						StoreDetailActivity.class);
				startActivity(intent);
			}
		});
	}
	
	public void startMarquee() {
		recommendMsg.setFocusable(true);
		recommendMsg.setFocusableInTouchMode(true);
		recommendMsg.requestFocus();
	}
	
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if (msg.what == Constant.SUCCESS) {
				boolean isCollect = msg.arg1 == 1;
				favoriteTip.setVisibility(View.VISIBLE);
				favoriteTip.setText(getResources().getString(isCollect ? 
						R.string.collect_success : R.string.collect_cancel));
				Data.recommendInfo.collectRecommendStore((String)msg.obj, isCollect);
				setTimer();
				adapter.notifyDataSetChanged();
				loadingView.setVisibility(View.GONE);
			} else if (msg.what == Constant.DATA_ERROR) {
				Toast.makeText(getActivity(), (String)msg.obj, Toast.LENGTH_SHORT).show();
				loadingView.setVisibility(View.GONE);
			} else  if (msg.what == CommonConstant.CANCEL_TIMER) {
				favoriteTip.setVisibility(View.GONE);         
            	timer.cancel();
			} else if (msg.what == CommonConstant.START_COLLECT) {
				loadingView.setLoadingText(getResources().getString(R.string.submiting));
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
		        }, 1*1000, 1*1000);   
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}
	
	@Override
	public void onResume() {
		
		super.onResume();
	}
	
	public void enableStoreScene() {
		if (Data.storeSceneList.size() != 0 && isStoreSceneFloat) {
			layoutFloat.setVisibility(View.VISIBLE);
		} else {
			layoutFloat.setVisibility(View.GONE);
		}
	}
	
	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
	}

	@Override
	public void onDestroyView() {
		super.onDestroy();
	}
	
	@Override
	public void onDetach() {
		
		super.onDetach();
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ib_store_float_cancel:
			layoutFloat.setVisibility(View.GONE);
			Preferences.SetBoolean(getActivity(), CommonConstant.KEY_STORE_SCENE_FLOAT, false);
			break;
			
		case R.id.layout_store_float: {
			Intent intent = new Intent();
			intent.setClass(getActivity(), StoreSceneActivity.class);
			((FragmentActivity) getActivity()).startActivityForResult(intent,
					MainActivity.TO_STORE_SCENE);
		}
			break;
			
		case R.id.view_recommend_left:
			recommendClick(Data.recommendInfo.leftItemInfo);
			break;
			
		case R.id.view_recommend_right_1:
			recommendClick(Data.recommendInfo.rightTopItemInfo);
			break;
			
		case R.id.view_recommend_right_2: 
			recommendClick(Data.recommendInfo.rightBottomItemInfo);
			break;
			
		case R.id.tv_recommend_msg: {
			List<String> msgList = new ArrayList<String>();
			for (NewsInfo info : Data.recommendInfo.newsList) {
				msgList.add(info.name);
			}
			new AlertDialog.Builder(getActivity())
			.setTitle(R.string.choose_news)
			.setIcon(android.R.drawable.ic_dialog_info)                
			.setSingleChoiceItems((String[]) msgList.toArray(
					new String[msgList.size()]), 0, 
			  new DialogInterface.OnClickListener() {
			                            
			     public void onClick(DialogInterface dialog, int which) {
			        dialog.dismiss();
			        Intent intent = new Intent();
					intent.setClass(getActivity(), NewsDetailActivity.class);
					intent.putExtra("news_id", Data.recommendInfo.newsList.get(which).id);
					startActivity(intent);
			     }
			  }
			)
			.show();
		}
			break;
			
		case R.id.btn_menu:
			parentHandler.sendEmptyMessage(MainMenuView.SLIDE);
			break;
			
		case R.id.tv_city:
			parentHandler.sendEmptyMessage(MainActivity.CHOSE_CITY);
			break;
			
		default:
			break;
		}
	}
	
	private void recommendClick(RecommendItemInfo info) {
		switch (info.type) {
		case RecommendItemInfo.RECOMMEND_STORE: {
			Intent intent = new Intent();
			intent.putExtra("store_id", info.content);
			intent.setClass(getActivity(), StoreDetailActivity.class);
			startActivity(intent);
		}
			break;
			
		case RecommendItemInfo.RECOMMEND_NEWS:{
			Intent intent = new Intent();
			intent.setClass(getActivity(), NewsDetailActivity.class);
			intent.putExtra("news_id", info.content);
			startActivity(intent);
		}
			break;
			
		case RecommendItemInfo.RECOMMEND_WEB:{
			Intent intent = new Intent();
			intent.setClass(getActivity(), WebActivity.class);
			intent.putExtra("url", info.content);
			startActivity(intent);
		}
		break;
		
		default:
			break;
		}
	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		
	}

	boolean complete = false;
	@Override
	public void onLoadMore() {
		listViewContent.stopLoadMore();
		Intent intent = new Intent();
		intent.putExtra("classify_id", ClassifyItemView.CLASSIFY_GD);
		intent.putExtra("classify_name", getResources().getString(R.string.classify_gd));
		intent.setClass(getActivity(), StoreListActivity.class);
		startActivity(intent);
	}
}
