package com.huiwei.roomreservation.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.huiwei.roomreservation.R;
import com.huiwei.roomreservation.activity.LoginActivity;
import com.huiwei.roomreservation.activity.MainActivity;
import com.huiwei.roomreservation.adapter.ViewPagerAdapter;
import com.huiwei.roomreservation.view.MainMenuView;
import com.huiwei.roomreservation.view.MemberInfoView;
import com.huiwei.roomreservation.view.ViewPagerListView;
import com.huiwei.roomreservationlib.data.Data;

public class MemberFragment extends Fragment implements OnClickListener {
	
	public static final int MEMBER_INFO = 0;
	public static final int MEMBER_ORDER = 1;
	public static final int MEMBER_COMMENT = 2;
	public static final int MEMBER_SCORE = 3;
	
	private MemberInfoView memberInfoView;
	private List<Button> buttonList = new ArrayList<Button>();
	private List<View> viewList = new ArrayList<View>();
	private ViewPager viewPager;
	private int curView = MEMBER_INFO;
	private Handler parentHandler;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_member,
				container, false);
		((ImageButton)view.findViewById(R.id.btn_menu)).setOnClickListener(this);
		((ImageButton)view.findViewById(R.id.btn_return)).setOnClickListener(this);
		initButtons(view);
		initViews(view);
		
		if (Data.memberInfo.isLogin) {
			fillData();
		}
		
		setCurView();
		
		Log.d("MemberFragment", "onCreateView");
		return view;
	}
	
	public void setParentHandler(Handler handler) {
		parentHandler = handler;
	}
	
	private void initButtons(View view) {
		Button btn1 = (Button) view.findViewById(R.id.btn_1);
		btn1.setTag(MEMBER_INFO);
		buttonList.add(btn1);
		btn1.setBackgroundResource(R.drawable.top_tab_down);
		btn1.setOnClickListener(this);

		Button btn2 = (Button) view.findViewById(R.id.btn_2);
		btn2.setTag(MEMBER_ORDER);
		buttonList.add(btn2);
		btn2.setOnClickListener(this);
		
		Button btn3 = (Button) view.findViewById(R.id.btn_3);
		btn3.setTag(MEMBER_COMMENT);
		buttonList.add(btn3);
		btn3.setOnClickListener(this);
		
		Button btn4 = (Button) view.findViewById(R.id.btn_4);
		btn4.setTag(MEMBER_SCORE);
		buttonList.add(btn4);
		btn4.setOnClickListener(this);
	}
	
	private void initViews(View view) {
		viewPager = (ViewPager) view.findViewById(R.id.viewPager);
		viewList.clear();
		memberInfoView = (MemberInfoView) LayoutInflater.from(getActivity()).
				inflate(R.layout.member_info_view, null);
		viewList.add(memberInfoView);
		addView(ViewPagerListView.TYPE_ORDER);
		addView(ViewPagerListView.TYPE_COMMENT);
		addView(ViewPagerListView.TYPE_SCORE);
		viewPager.setAdapter(new ViewPagerAdapter(viewList));
		viewPager.setCurrentItem(0);
		viewPager.setOnPageChangeListener(pageChangeListener);
	}
	
	private void addView(int type) {
		ViewPagerListView listView = (ViewPagerListView) LayoutInflater.from(
				getActivity()).inflate(R.layout.view_pager_list_view, null);
		viewList.add(listView);
	}
	
	private OnPageChangeListener pageChangeListener = new OnPageChangeListener() {
		
		@Override
		public void onPageSelected(int index) {
			resetTabButton(index);
		}
		
		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onPageScrollStateChanged(int arg0) {
			// TODO Auto-generated method stub
			
		}
	};

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}
	
	@Override
	public void onResume() {
		if (Data.memberInfo.isLogin) {
			((MemberInfoView)viewList.get(MEMBER_INFO)).getMemberInfo();
			((ViewPagerListView)viewList.get(MEMBER_ORDER)).reloadOrderList();
			((ViewPagerListView)viewList.get(MEMBER_SCORE)).reloadScoreList();
			((ViewPagerListView)viewList.get(MEMBER_COMMENT)).reloadCommentList();
		} else {
			Intent intent = new Intent();
			intent.setClass(getActivity(), LoginActivity.class);
			((FragmentActivity) getActivity()).startActivityForResult(intent, MainActivity.TO_LOGIN);
		}
		
		super.onResume();
	} 
	
	@Override
	public void onHiddenChanged(boolean hidden) {
		Log.d("hidden", hidden ? "true" : "false");
		super.onHiddenChanged(hidden);
	}

	private void fillData() {
//		((MemberInfoView)viewList.get(MEMBER_INFO)).getMemberInfo();
		((ViewPagerListView)viewList.get(MEMBER_ORDER)).setListType(
				ViewPagerListView.TYPE_ORDER, null);
		((ViewPagerListView)viewList.get(MEMBER_COMMENT)).setListType(
				ViewPagerListView.TYPE_COMMENT, null);
		((ViewPagerListView)viewList.get(MEMBER_SCORE)).setListType(
				ViewPagerListView.TYPE_SCORE, null);
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
		case R.id.btn_1: 			
		case R.id.btn_2:
		case R.id.btn_3:
		case R.id.btn_4: 
			curView = (Integer) v.getTag();
			setCurView();
			break;
			
		case R.id.btn_menu:
			parentHandler.sendEmptyMessage(MainMenuView.SLIDE);
			break;
			
		case R.id.btn_return:
			parentHandler.sendEmptyMessage(MainActivity.TO_MAIN);
			break;
			
		default:
			break;
		}
	}
	
	public void setCurViewFlag(int flag) {
		curView = flag;
	}
	
	private void setCurView() {
		if (viewPager != null) {
			viewPager.setCurrentItem(curView);
			resetTabButton(curView);
		}
	}
	
	private void resetTabButton(int index) {
		for (int i=0; i<buttonList.size(); i++) {
			Button button = buttonList.get(i);
			int curIndex = (Integer)button.getTag();
			if (curIndex == index) {
				button.setBackgroundResource(R.drawable.top_tab_down);
			} else {
				button.setBackgroundResource(R.drawable.top_tab_up);
			}
		}
	}

	public void getPhotoResult(int requestCode, Intent data) {
		memberInfoView.getPhotoResult(requestCode, data);
	}
}
