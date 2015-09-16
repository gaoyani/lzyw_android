package com.huiwei.roomreservation.adapter;

import java.util.ArrayList;
import java.util.List;

import com.huiwei.roomreservation.R;
import com.huiwei.roomreservation.common.SysApplication;
import com.huiwei.roomreservation.view.MainMenuView;
import com.huiwei.roomreservationlib.data.Data;

import android.R.integer;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MenuItemAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private Context mContext;
	private int selectItemId = -1;
	private List<Integer> iconIDList = new ArrayList<Integer>();
	private List<String> menuNameList = new ArrayList<String>();

	public MenuItemAdapter(Context context) {
		mInflater = LayoutInflater.from(context);
		mContext = context;
		
		iconIDList.add(R.drawable.menu_store);
		iconIDList.add(R.drawable.menu_member);
		iconIDList.add(R.drawable.menu_control);
		iconIDList.add(R.drawable.menu_push);
		iconIDList.add(R.drawable.menu_search);
		iconIDList.add(R.drawable.menu_share);
		iconIDList.add(R.drawable.menu_scan);
		iconIDList.add(R.drawable.menu_update);
		iconIDList.add(R.drawable.menu_setting);
		iconIDList.add(R.drawable.menu_about);
		
		menuNameList.add(mContext.getResources().getString(R.string.menu_store));
		menuNameList.add(mContext.getResources().getString(R.string.menu_member));
		menuNameList.add(mContext.getResources().getString(R.string.menu_control));
		menuNameList.add(mContext.getResources().getString(R.string.menu_push));
		menuNameList.add(mContext.getResources().getString(R.string.menu_search));
		menuNameList.add(mContext.getResources().getString(R.string.menu_share));
		menuNameList.add(mContext.getResources().getString(R.string.menu_scan));
		menuNameList.add(mContext.getResources().getString(R.string.menu_update));
		menuNameList.add(mContext.getResources().getString(R.string.menu_setting));
		menuNameList.add(mContext.getResources().getString(R.string.menu_about));
	}
	
	public void setMenuName(int index, int strID) {
		menuNameList.set(index, mContext.getResources().getString(strID));
	}

	public void setSelectItemID(int id) {
		selectItemId = id;
	}
	
	@Override
	public int getCount() {
		return iconIDList.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ViewHolder viewHolder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.main_menu_item, null);
			
			viewHolder = new ViewHolder();
			viewHolder.icon = (ImageView) convertView.findViewById(R.id.iv_menu_icon);
			viewHolder.title = (TextView) convertView.findViewById(R.id.tv_menu_name);
			viewHolder.rightIcon = (ImageView) convertView.findViewById(R.id.iv_icon);
			
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder)convertView.getTag();
		}
		
		viewHolder.icon.setBackgroundResource(iconIDList.get(position));
		viewHolder.title.setText(menuNameList.get(position));
		viewHolder.title.setTextColor(mContext.getResources().
				getColor(R.color.menu_text));
		
		if (position == MainMenuView.MENU_UPDATE && !SysApplication.isLatestVersion) {
			viewHolder.rightIcon.setVisibility(View.VISIBLE);
		} else {
			
			viewHolder.rightIcon.setVisibility(View.GONE);
		}
		
//		if (position == MainMenuView.MENU_STORE && 
//				(Data.gateway == null || Data.gateway.length() == 0)) {
//			viewHolder.icon.setBackgroundResource(R.drawable.menu_store_disable);
//			convertView.setBackgroundColor(mContext.getResources().
//					getColor(R.color.menu_item_disable));
//			viewHolder.title.setTextColor(mContext.getResources().
//					getColor(R.color.menu_text_disable));
//		} 
		
		if (position == selectItemId) {
			convertView.setBackgroundColor(mContext.getResources().
					getColor(R.color.menu_item_selected));;
		} else {
			convertView.setBackgroundColor(Color.TRANSPARENT);
		}
		
		return convertView;
	}
	
	public static class ViewHolder {
		ImageView icon;
		TextView title;
		ImageView rightIcon;
	}
}
