package com.huiwei.roomreservation.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
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
import android.widget.Toast;

import com.huiwei.roomreservation.R;
import com.huiwei.roomreservationlib.data.Constant;
import com.huiwei.roomreservationlib.data.Data;
import com.huiwei.roomreservationlib.info.CommentInfo;
import com.huiwei.roomreservationlib.task.SubmitCommentTask;

public class MemberCommentItemAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private Context mContext;

	public MemberCommentItemAdapter(Context context) {
		mInflater = LayoutInflater.from(context);
		mContext = context;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return Data.commentList.size();
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
			convertView = mInflater.inflate(R.layout.member_comment_item, null);

			viewHolder = new ViewHolder();
			viewHolder.title = (TextView) convertView
					.findViewById(R.id.tv_store_name);
			viewHolder.info = (TextView) convertView
					.findViewById(R.id.tv_info);
			viewHolder.comment = (Button) convertView
					.findViewById(R.id.btn_comment);
			viewHolder.ratingBarDisplay = (RatingBar)convertView
					.findViewById(R.id.ratingBar_display);
			viewHolder.starsDisplay = (TextView)convertView
					.findViewById(R.id.tv_stars_display);
			viewHolder.layoutComment = (RelativeLayout) convertView
					.findViewById(R.id.layout_comment);
			viewHolder.ratingBarInput = (RatingBar)convertView
					.findViewById(R.id.ratingBar);
			viewHolder.starsInput = (TextView)convertView
					.findViewById(R.id.tv_stars);
			viewHolder.commentInput = (EditText)convertView
					.findViewById(R.id.et_comment);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		CommentInfo info = Data.commentList.get(position);
		viewHolder.title.setText(info.name);
		viewHolder.info.setText(info.info);
		viewHolder.comment.setTag(position);
		viewHolder.comment.setOnClickListener(new SubmitClickListener(viewHolder));
		viewHolder.ratingBarInput.setTag(position);
		viewHolder.ratingBarInput.setOnRatingBarChangeListener(ratingBarChangeListener);
		if (info.isComment) {
			viewHolder.ratingBarDisplay.setVisibility(View.VISIBLE);
			viewHolder.starsDisplay.setVisibility(View.VISIBLE);
			viewHolder.ratingBarDisplay.setRating(info.stars);
			viewHolder.starsDisplay.setText("("+String.format("%.1f", info.stars)+
					convertView.getResources().getString(R.string.star)+")");
			viewHolder.comment.setText(convertView.getResources().getString(R.string.comment_already));
			viewHolder.comment.setBackgroundResource(R.drawable.reservation_invalid);
			viewHolder.layoutComment.setVisibility(View.GONE);
		} else {
			viewHolder.ratingBarDisplay.setVisibility(View.GONE);
			viewHolder.starsDisplay.setVisibility(View.GONE);
			viewHolder.comment.setBackgroundResource(R.drawable.button_orange_selector);
			if (info.isStartComment) {
				viewHolder.ratingBarInput.setRating(info.stars);
				viewHolder.starsInput.setText("("+String.format("%.1f", info.stars)+
						convertView.getResources().getString(R.string.star)+")");
				viewHolder.comment.setText(convertView.getResources().getString(R.string.comment_commit));
				viewHolder.commentInput.setText("");
				viewHolder.layoutComment.setVisibility(View.VISIBLE);
			} else {
				viewHolder.comment.setText(convertView.getResources().getString(R.string.comment));
				viewHolder.layoutComment.setVisibility(View.GONE);
			}
		}

		return convertView;
	}
	
	private OnRatingBarChangeListener ratingBarChangeListener = new OnRatingBarChangeListener() {

		@Override
		public void onRatingChanged(RatingBar ratingBar, float arg1, boolean arg2) {
			int pos = (Integer) ratingBar.getTag();
			CommentInfo info = Data.commentList.get(pos);
			info.stars = ratingBar.getRating();
			notifyDataSetChanged();
		}
	};
	
	class SubmitClickListener implements OnClickListener {
		private ViewHolder viewHolder;
	    public SubmitClickListener(ViewHolder holder) {
	    	viewHolder = holder;
	    }
	    
		@Override
		public void onClick(View arg0) {
			int pos = (Integer) viewHolder.comment.getTag();
			CommentInfo info = Data.commentList.get(pos);
			if (!info.isComment) {
				if (info.isStartComment) {
					info.stars = (float)viewHolder.ratingBarInput.getRating();
					info.comment = viewHolder.commentInput.getText().toString();
					SubmitCommentTask task = new SubmitCommentTask(mContext, submitHandler,
							info, CommentInfo.COMMENT_ORDER);
					task.execute();
				} else {
					info.isStartComment = true;
					notifyDataSetChanged();
				}
			}
		}
	}
	
	private Handler submitHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == Constant.SUCCESS) {		
				Toast.makeText(mContext, mContext.getString(
						R.string.comment_success), Toast.LENGTH_SHORT).show();
				CommentInfo info = (CommentInfo)msg.obj;
				info.isStartComment = false;
				info.isComment = true;
				CommentInfo oldInfo = Data.findCommentInfo(info.orderID);
				if (oldInfo != null) {
					Data.commentList.remove(oldInfo);
					Data.commentList.add(info);
				}
				
				notifyDataSetChanged();
			} else {
				Toast.makeText(mContext, mContext.getResources().getString(
						R.string.comment_fail), Toast.LENGTH_SHORT).show();
			}
			
		};
	};

	public static class ViewHolder {
		TextView title;
		TextView info;
		Button comment;
		RatingBar ratingBarDisplay;
		TextView starsDisplay;
		RelativeLayout layoutComment;
		RatingBar ratingBarInput;
		TextView starsInput;
		EditText commentInput;
	}
}
