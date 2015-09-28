package com.huiwei.roomreservation.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.map.Text;
import com.huiwei.commonlib.SyncImageLoader;
import com.huiwei.roomreservation.R;
import com.huiwei.roomreservation.activity.LoginActivity;
import com.huiwei.roomreservation.activity.MainActivity;
import com.huiwei.roomreservation.activity.RoomReservationActivity;
import com.huiwei.roomreservation.activity.RoomDetailActivity;
import com.huiwei.roomreservation.activity.ServiceReservationActivity;
import com.huiwei.roomreservationlib.info.RoomInfo;
import com.huiwei.roomreservationlib.info.ServiceBaseInfo;
import com.huiwei.roomreservationlib.info.ServiceInfo;
import com.huiwei.roomreservationlib.info.StoreDetailInfo;
import com.huiwei.roomreservationlib.data.Data;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

public class RoomItemAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private Context mContext;
//	private SyncImageLoader syncImageLoader;
	private int type = StoreDetailInfo.TYPE_ROOM;
	
	private DisplayImageOptions options;
	protected ImageLoader imageLoader = ImageLoader.getInstance();

	public RoomItemAdapter(Context context) {
		mInflater = LayoutInflater.from(context);
		mContext = context;
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
	
	public void setListType(int type) {
		this.type = type;
	}
	
	@Override
	public int getCount() {
		if (type == StoreDetailInfo.TYPE_ROOM) {
			return Data.storeDetailInfo.roomList.size();
		} else if (type == StoreDetailInfo.TYPE_SERVICE){
			return Data.storeDetailInfo.serviceList.size();
		}
		
		return Data.storeDetailInfo.artificerList.size();
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
			convertView = mInflater.inflate(R.layout.room_item, null);
			
			viewHolder = new ViewHolder();
			viewHolder.icon = (ImageView) convertView.findViewById(R.id.iv_room_icon);
			viewHolder.name = (TextView) convertView.findViewById(R.id.tv_name);
			viewHolder.nameTitle = (TextView) convertView.findViewById(R.id.tv_name_title);
			viewHolder.otherInfo = (TextView) convertView.findViewById(R.id.tv_other_info);
			viewHolder.otherTitle = (TextView) convertView.findViewById(R.id.tv_other_title);
			viewHolder.price = (TextView) convertView.findViewById(R.id.tv_price);
			viewHolder.priceType = (TextView) convertView.findViewById(R.id.tv_price_type);
			viewHolder.consumeOriginal = (TextView) convertView.findViewById(R.id.tv_consume_original);
			viewHolder.consume = (TextView) convertView.findViewById(R.id.tv_consume);
			viewHolder.consumeTitle = (TextView) convertView.findViewById(R.id.tv_consume_title);
			viewHolder.reservation = (Button) convertView.findViewById(R.id.btn_reservation);
			
			viewHolder.consumeOriginal.getPaint().setFlags(Paint. STRIKE_THRU_TEXT_FLAG); 
			
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder)convertView.getTag();
		}
		
		ServiceBaseInfo info = null;
		if (type == StoreDetailInfo.TYPE_ROOM) {
			info = Data.storeDetailInfo.roomList.get(position);
			viewHolder.consumeOriginal.setVisibility(View.GONE);
		} else if (type == StoreDetailInfo.TYPE_SERVICE){
			info = Data.storeDetailInfo.serviceList.get(position);
			viewHolder.consumeOriginal.setText(info.consumeOriginal);
			viewHolder.consumeOriginal.setVisibility(View.VISIBLE);
		}
		
		viewHolder.name.setText(info.name);
		viewHolder.nameTitle.setText(info.nameTitle+"：");
		viewHolder.otherInfo.setText(String.valueOf(info.otherInfo));
		viewHolder.otherTitle.setText(String.valueOf(info.otherTitle+"："));
		viewHolder.price.setText(info.price+convertView.getResources().getString(R.string.yuan));
		viewHolder.priceType.setText(info.priceType+convertView.getResources().getString(R.string.colon));
		viewHolder.consume.setText(info.consumeValue);
		viewHolder.consumeTitle.setText(info.consumeTitle+"：");
		
		setReservationBtn(info.bookable, viewHolder.reservation);
		
		viewHolder.reservation.setTag(position);
		viewHolder.reservation.setOnClickListener(clickListener);
		
//		viewHolder.icon.setTag(info.iconUrl);
//		loadImage(viewHolder.icon, info.iconUrl);
		imageLoader.displayImage(info.iconUrl, viewHolder.icon, options, null);
		
		return convertView;
	}
	
	private void setReservationBtn(boolean bookable, Button reservation) {
		if (bookable) {
			reservation.setText(mContext.getResources().
					getString(R.string.room_bookable_button));
			reservation.setBackgroundResource(
					R.drawable.button_orange_selector);
		} else {
			reservation.setText(mContext.getResources().
					getString(R.string.room_booked_full_button));
			reservation.setBackgroundResource(
					R.drawable.reservation_invalid);
		}
	}
	
	OnClickListener clickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			boolean bookable = true;
			Class<?> cls = RoomReservationActivity.class;
			if (type == StoreDetailInfo.TYPE_ROOM) {
				Data.roomInfo = Data.storeDetailInfo.roomList.get((Integer) (v.getTag()));
				bookable = Data.roomInfo.bookable;
				
			} else if (type == StoreDetailInfo.TYPE_SERVICE){
				Data.serviceInfo = Data.storeDetailInfo.serviceList.get((Integer) (v.getTag()));
				bookable = Data.serviceInfo.bookable;
				cls = ServiceReservationActivity.class;
			}
			
			if (Data.memberInfo.isLogin) {
				if (bookable) {
					Intent intent = new Intent();
					intent.setClass(mContext, cls);
					mContext.startActivity(intent);
				}
			} else {
				Intent intent = new Intent();
				intent.setClass(mContext, LoginActivity.class);
				 ((Activity) mContext).startActivityForResult(intent, MainActivity.TO_LOGIN);
				Toast.makeText(mContext, mContext.getResources().getString(R.string.unlogin), 
						Toast.LENGTH_SHORT).show();
			}
		}
	};

//	private void loadImage(final ImageView iconView, final String iconUrl) {
//		if (iconUrl != null && !iconUrl.equals("")) {			
//			syncImageLoader.loadImage(iconUrl,
//					new SyncImageLoader.OnImageLoadListener() {
//
//						@Override
//						public void onImageLoad(Bitmap bitmap) {
//							if (iconView.getTag() != null
//									&& iconView.getTag().equals(iconUrl)) {
//								if (bitmap == null) {
//									iconView.setImageBitmap(null);
//									iconView
//											.setBackgroundResource(R.drawable.default_icon);
//								} else {
//									iconView.setImageBitmap(bitmap);
//									iconView.setBackgroundDrawable(null);
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
//		} else {
//			iconView.setImageBitmap(null);
//			iconView.setBackgroundResource(R.drawable.default_icon);
//		}
//	}
	
	public static class ViewHolder {
		ImageView icon;
		TextView name;
		TextView nameTitle;
		TextView otherInfo;
		TextView otherTitle;
		TextView price;
		TextView priceType;
		TextView consumeTitle;
		TextView consume;
		TextView consumeOriginal;
		Button reservation;
	}
}
