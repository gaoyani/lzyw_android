package com.huiwei.roomreservation.view;

import java.security.PublicKey;

import android.R.integer;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huiwei.commonlib.SyncImageLoader;
import com.huiwei.roomreservation.R;
import com.huiwei.roomreservationlib.info.RoomInfo;
import com.huiwei.roomreservationlib.info.StoreDetailInfo;
import com.huiwei.roomreservationlib.info.StoreInfo;
import com.huiwei.roomreservationlib.data.Data;

public class RoomInfoView extends RelativeLayout {
	
	private Context context;
	public RoomInfoView(Context context) {
		super(context);
		this.context = context;
	}
	
	public RoomInfoView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
	}
	
	@Override
	protected void onFinishInflate() {
		// TODO Auto-generated method stub
		super.onFinishInflate();
	}
	
	public void setInfo(String storeName, String phoneNumber, String roomName, 
			String roomInfo, String price, String priceType, String iconUrl) {
		((TextView)findViewById(R.id.tv_store_name)).setText(storeName);
		
		((TextView)findViewById(R.id.tv_room_name)).setText(roomName+":");
		((TextView)findViewById(R.id.tv_room_info)).setText(roomInfo);
		((TextView)findViewById(R.id.tv_price_type)).setText(priceType);
		((TextView)findViewById(R.id.tv_room_price)).setText(
				context.getResources().getString(R.string.yuan)+price);
		
		if (phoneNumber.equals("")) {
			((RelativeLayout)findViewById(R.id.layout_phone_number)).setVisibility(View.GONE);
		} else {
			((TextView)findViewById(R.id.tv_phone_number)).setText(phoneNumber);
		}
	
		loadImage((ImageView)findViewById(R.id.iv_icon),iconUrl);
	}
	
	private void loadImage(final ImageView iconView, final String iconUrl) {
		SyncImageLoader imageLoader = new SyncImageLoader(context);
		if (iconUrl != null && !iconUrl.equals("")) {
			imageLoader.loadImage(iconUrl,
					new SyncImageLoader.OnImageLoadListener() {
						
						@Override
						public void onImageLoad(Bitmap bitmap) {
							if (bitmap == null) {
								iconView.setImageBitmap(null);
								iconView.setBackgroundResource(R.drawable.default_icon);
							} else {
								iconView.setImageBitmap(bitmap);
								iconView.setBackgroundDrawable(null);
							}
						}
						
						@Override
						public void onError() {
							iconView.setImageBitmap(null);
							iconView.setBackgroundResource(R.drawable.default_icon);
						}
					});
			
		} else {
			iconView.setImageBitmap(null);
			iconView.setBackgroundResource(R.drawable.default_icon);
		}
	}
}
	
