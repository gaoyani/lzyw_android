package com.huiwei.roomreservation.adapter;

import java.nio.channels.AsynchronousCloseException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import com.huiwei.commonlib.FileManager;
import com.huiwei.commonlib.SyncImageLoader;
import com.huiwei.roomreservation.R;
import com.huiwei.roomreservation.activity.LoginActivity;
import com.huiwei.roomreservation.activity.MainActivity;
import com.huiwei.roomreservation.common.CommonConstant;
import com.huiwei.roomreservationlib.data.Data;
import com.huiwei.roomreservationlib.info.StoreInfo;
import com.huiwei.roomreservationlib.task.CollectStoreTask;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import android.R.bool;
import android.R.integer;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

public class StoreItemAdapter extends BaseAdapter {
	
	private LayoutInflater mInflater;
	private Context mContext;
	private List<StoreInfo> storeInfoList = new ArrayList<StoreInfo>();
	private Handler handler;
//	private SyncImageLoader syncImageLoader;
	
	private DisplayImageOptions options;
	protected ImageLoader imageLoader = ImageLoader.getInstance();

	public StoreItemAdapter(Context context, Handler handler) {
		mInflater = LayoutInflater.from(context);
		mContext = context;
		this.handler = handler;
//		syncImageLoader = new SyncImageLoader(mContext);
		
		options = new DisplayImageOptions.Builder()
		.showImageOnLoading(R.drawable.default_icon)
		.showImageForEmptyUri(R.drawable.default_icon)
		.showImageOnFail(R.drawable.default_icon)
		.cacheInMemory(true)
		.cacheOnDisc(true)
		.considerExifParams(true)
		.displayer(new RoundedBitmapDisplayer(5))
		.build();
	}
	
	public void setData(List<StoreInfo> storeInfoList) {
		if (storeInfoList != null) {
			this.storeInfoList.clear();
			this.storeInfoList.addAll(storeInfoList);
			notifyDataSetChanged();
		}
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return storeInfoList.size();
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
			convertView = mInflater.inflate(R.layout.store_item, null);
			
			viewHolder = new ViewHolder();
			viewHolder.icon = (ImageView) convertView.findViewById(R.id.iv_store_icon);
			viewHolder.title = (TextView) convertView.findViewById(R.id.tv_store_name);
			viewHolder.cpp = (TextView) convertView.findViewById(R.id.tv_cpp);
			viewHolder.rb = (RatingBar) convertView.findViewById(R.id.ratingBar);
			viewHolder.address = (TextView) convertView.findViewById(R.id.tv_address);
			viewHolder.distance = (TextView) convertView.findViewById(R.id.tv_distance);
			viewHolder.favorite = (ImageView) convertView.findViewById(R.id.iv_favorite);
			
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder)convertView.getTag();
		}
		
		if (storeInfoList.size() == 0)
			return convertView;
		
		StoreInfo info = storeInfoList.get(position);
		viewHolder.title.setText(info.name);
		viewHolder.cpp.setText(info.cpp+convertView.getResources().getString(R.string.yuan));
		viewHolder.rb.setRating(info.stars);
		viewHolder.address.setText(info.address);
		viewHolder.distance.setText(info.distance);
		viewHolder.favorite.setBackgroundResource(info.favorite ? 
				R.drawable.heart_act : R.drawable.heart);
		
		viewHolder.favorite.setTag(position);
		viewHolder.favorite.setOnClickListener(onClickListener);
		
//		viewHolder.icon.setTag(info.iconUrl);
//		loadImage(viewHolder.icon, info);
		imageLoader.displayImage(info.iconUrl, viewHolder.icon, options, null);
		
		return convertView;
	}
	
	OnClickListener onClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if (Data.memberInfo.isLogin) {
				int index = (Integer) v.getTag();
				StoreInfo info = storeInfoList.get(index);

				handler.sendEmptyMessage(CommonConstant.START_COLLECT);
				collectStoreTask(info.id, !info.favorite);
			} else {
				Intent intent = new Intent();
				intent.setClass(mContext, LoginActivity.class);
				 ((Activity) mContext).startActivity(intent);
				Toast.makeText(mContext, mContext.getResources().getString(
						R.string.please_login_collect), Toast.LENGTH_SHORT).show();
			}
		}
	};
	
	private void collectStoreTask(String id, boolean isCollect) {
		CollectStoreTask task = new CollectStoreTask(mContext, handler, id, isCollect);
		task.execute();
	}
	
//	private void loadImage(final ImageView iconView, final StoreInfo info) {
//		
//		if (info.iconUrl != null && !info.iconUrl.equals("")) {			
//			syncImageLoader.loadImage(info.iconUrl,
//					new SyncImageLoader.OnImageLoadListener() {
//						@Override
//						public void onImageLoad(Bitmap bitmap) {
//							if (iconView.getTag() != null
//									&& iconView.getTag().equals(info.iconUrl)) {
//								if (bitmap == null) {
//									iconView.setImageBitmap(null);
//									iconView
//											.setBackgroundResource(R.drawable.default_icon);
//								} else {
//									Log.d("StoreItemAdapter", info.localPicPath);
//									iconView.setImageBitmap(bitmap);
//									iconView.setBackgroundDrawable(null);
//									if (info.localPicPath != null || info.localPicPath.length() != 0) {
//										if (FileManager.saveBitmap(info.localPicPath, bitmap,
//												CompressFormat.JPEG)) {
//											info.localPicPath = "";
////											Log.d("StoreItemAdapter", info.localPicPath);
////											Message msg = new Message();
////											msg.obj = info;
////											iconLoadHandler.sendMessage(msg);
//										}
//									}
//								}
//							}
//						}
//
//						@Override
//						public void onError() {
//							iconView.setImageBitmap(null);
//							iconView
//									.setBackgroundResource(R.drawable.default_icon);
//						}
//					});
//
//			
//		} else {
//			iconView.setImageBitmap(null);
//			iconView.setBackgroundResource(R.drawable.default_icon);
//		}
//	}
//	
//	private Handler iconLoadHandler = new Handler() {
//		public void dispatchMessage(android.os.Message msg) {
//			StoreInfo info = (StoreInfo)msg.obj;
//			if (info != null) {
////				info.iconUrl = info.localPicPath;
//				info.localPicPath = "";
//			}
//			
//		};
//	};
	
	public static class ViewHolder {
		ImageView icon;
		TextView title;
		TextView cpp;
		RatingBar rb;
		TextView address,distance;
		ImageView favorite;
	}
}
