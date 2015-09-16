package com.huiwei.roomreservation.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huiwei.roomreservation.R;
import com.huiwei.roomreservationlib.info.CommentInfo;

public class CommentItemAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private Context mContext;
	private List<CommentInfo> commentList = null;
	private int commentType;

	public CommentItemAdapter(Context context, List<CommentInfo> commentList, int commentType) {
		mInflater = LayoutInflater.from(context);
		mContext = context;
		this.commentList = commentList;
		this.commentType = commentType;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return commentList.size();
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
			convertView = mInflater.inflate(R.layout.comment_item, null);

			viewHolder = new ViewHolder();
			viewHolder.nickname = (TextView) convertView
					.findViewById(R.id.tv_nickname);
			viewHolder.time = (TextView) convertView
					.findViewById(R.id.tv_time);
			viewHolder.comment = (TextView) convertView
					.findViewById(R.id.tv_comment);
			viewHolder.ratingBar = (RatingBar)convertView
					.findViewById(R.id.ratingBar);
			viewHolder.layer = (TextView) convertView
					.findViewById(R.id.tv_layer);
		
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		CommentInfo info = commentList.get(position);
		viewHolder.nickname.setText(info.name);
		viewHolder.time.setText(info.time);
		if (commentType == CommentInfo.COMMENT_NEWS) {
			viewHolder.ratingBar.setVisibility(View.GONE);
			viewHolder.layer.setText(""+(position+1)+"L");
		} else {
			viewHolder.ratingBar.setVisibility(View.VISIBLE);
			viewHolder.ratingBar.setProgress((int)(info.stars));
		}
		
		viewHolder.comment.setText(info.comment);
		
		return convertView;
	}
	
	public static class ViewHolder {
		TextView nickname;
		TextView time;
		TextView layer;
		RatingBar ratingBar;
		TextView comment;
	}
}
