package com.huiwei.roomreservation.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.huiwei.roomreservation.R;
import com.huiwei.roomreservationlib.info.ScoreInfo;
import com.huiwei.roomreservationlib.data.Data;

public class ScoreItemAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private Context mContext;

	public ScoreItemAdapter(Context context) {
		mInflater = LayoutInflater.from(context);
		mContext = context;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return Data.scoreList.size();
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
			convertView = mInflater.inflate(R.layout.member_score_item, null);

			viewHolder = new ViewHolder();
			viewHolder.title = (TextView) convertView
					.findViewById(R.id.tv_store_name);
			viewHolder.time = (TextView) convertView
					.findViewById(R.id.tv_time);
			viewHolder.price = (TextView) convertView
					.findViewById(R.id.tv_price);
			viewHolder.type = (TextView) convertView
					.findViewById(R.id.tv_type);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		ScoreInfo info = Data.scoreList.get(position);
		viewHolder.title.setText(info.name);
		viewHolder.time.setText(info.time);
		viewHolder.price.setText(info.price);
		viewHolder.type.setText(info.type);

		return convertView;
	}

	public static class ViewHolder {
		TextView title;
		TextView time;
		TextView price;
		TextView type;
	}
}
