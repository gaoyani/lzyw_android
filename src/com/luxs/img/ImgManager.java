package com.luxs.img;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import android.widget.ListView;

import com.huiwei.roomreservation.R;
import com.luxs.fileoperategroup.FileBean;
import com.luxs.sdcord.SDManager;
import com.luxs.utils.Utils;

public class ImgManager {
	//方法
	private SDManager sdManager = new SDManager();
	private ImageLoader imageLoader = new ImageLoader();
	
	/**
	 * 装载SD卡图片
	 * @param 
	 * @return
	 */
	public void setImgFromSDCord(final FileBean fileBean,final ListView listView,final Context context){
		if(sdManager.isFileExist(fileBean.getFileThumb())){
			imageLoader.loadDrawableThread(fileBean, new ImageCallback() {
				@Override
				public void imageLoaded(Drawable drawable) {
					if(drawable != null){
						Utils.log("drawable not null");
						ImageView imageView = (ImageView)listView.findViewWithTag(fileBean.getFilePath());
						if(imageView != null) imageView.setBackgroundDrawable(drawable);
					}else{
						Utils.log("drawable is null");
						ImageView imageView = (ImageView)listView.findViewWithTag(fileBean.getFilePath());
						if(imageView != null) imageView.setBackgroundColor(context.getResources().getColor(R.color.black8));
					}
				}
				@Override
				public void imageLoaded() {}
				
			});	
		}
	}

}
