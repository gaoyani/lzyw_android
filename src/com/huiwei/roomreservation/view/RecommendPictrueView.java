package com.huiwei.roomreservation.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huiwei.commonlib.FileManager;
import com.huiwei.commonlib.SyncImageLoader;
import com.huiwei.roomreservation.R;
import com.huiwei.roomreservation.adapter.MenuItemAdapter;
import com.huiwei.roomreservationlib.info.RecommendInfo.RecommendItemInfo;
import com.huiwei.roomreservationlib.info.RoomInfo;
import com.huiwei.roomreservationlib.info.StoreInfo;
import com.huiwei.roomreservationlib.data.Data;

public class RecommendPictrueView extends RelativeLayout {
	
	private Context context;
	
	public RecommendPictrueView(Context context) {
		super(context);
		this.context = context;
	}
	
	public RecommendPictrueView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
	}
	
	@Override
	protected void onFinishInflate() {
		// TODO Auto-generated method stub
		super.onFinishInflate();
	}
	
	public void setInfo(RecommendItemInfo info) {
		((TextView)findViewById(R.id.tv_title)).setText(info.title);
		if (info.price != null && info.price.length() != 0) {
			((TextView)findViewById(R.id.tv_price)).setText(
					context.getResources().getString(R.string.yuan)+info.price);
		}
		
		loadImage((ImageView)findViewById(R.id.iv_picture), info);
	}
	
	private void loadImage(final ImageView iconView, final RecommendItemInfo info) {
		SyncImageLoader imageLoader = new SyncImageLoader(context);
		if (info.picUrl != null && !info.picUrl.equals("")) {
			imageLoader.loadImage(info.picUrl,
					new SyncImageLoader.OnImageLoadListener() {
						
						@Override
						public void onImageLoad(Bitmap bitmap) {
							if (bitmap == null) {
								iconView.setImageBitmap(null);
								iconView.setBackgroundResource(R.drawable.default_icon);
							} else {
								iconView.setImageBitmap(bitmap);
								iconView.setBackgroundDrawable(null);
								if (info.localPicPath != null && info.localPicPath.length() != 0) {
									if(FileManager.saveBitmap(info.localPicPath, bitmap,
											CompressFormat.JPEG)) {
										info.localPicPath = "";
//										Message msg = new Message();
//										msg.obj = info;
//										iconLoadHandler.sendMessage(msg);
									}
								}
							}
							
							((ProgressBar)findViewById(R.id.pb_picture)).setVisibility(View.GONE);
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
	
	private Handler iconLoadHandler = new Handler() {
		public void dispatchMessage(android.os.Message msg) {
			RecommendItemInfo info = (RecommendItemInfo)msg.obj;
			if (info != null) {
//				info.picUrl = info.localPicPath;
				info.localPicPath = "";
			}
			
		};
	};
}
	
