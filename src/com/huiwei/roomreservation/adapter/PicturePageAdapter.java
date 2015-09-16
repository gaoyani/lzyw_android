package com.huiwei.roomreservation.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.huiwei.commonlib.SyncImageLoader;
import com.huiwei.roomreservation.R;

public class PicturePageAdapter extends PagerAdapter {  
	List<View> views;
	List<String> imgUrl;
	private SyncImageLoader imageLoader;
	  
	public PicturePageAdapter(Context context, List<View> views, List<String> imgUrl) {
		this.views = views;
		this.imgUrl = imgUrl;
		imageLoader = new SyncImageLoader(context);
	}
	
    @Override  
    public int getCount() {  
        return views.size();  
    }  

    @Override  
    public boolean isViewFromObject(View view, Object obj) {  
        return view == obj; // key  
    }  

    @Override  
    public void destroyItem(ViewGroup container, int position, Object object) {  
        container.removeView((View) object); 
    }  

    @Override  
    public Object instantiateItem(ViewGroup container, int position) {  
        System.out.println("pos:" + position);  
        View view = views.get(position);  
        
        ImageView img = (ImageView) view.findViewById(R.id.iv_picture);  
        img.setTag(imgUrl.get(position));
        ProgressBar bar = (ProgressBar) view.findViewById(R.id.pb_picture); 
        loadImage(img, bar, imgUrl.get(position));
        container.addView(view);  
        return views.get(position); 
    }  
    
    private void loadImage(final ImageView iconView, final ProgressBar pb, final String iconUrl) {
		if (iconUrl != null && !iconUrl.equals("")) {
			imageLoader.loadImage(iconUrl,
					new SyncImageLoader.OnImageLoadListener() {

						@Override
						public void onImageLoad(Bitmap bitmap) {
							
							pb.setVisibility(View.GONE);
							
							if (iconView.getTag() != null
									&& iconView.getTag().equals(iconUrl)) {
								if (bitmap == null) {
									iconView.setImageBitmap(null);
									iconView.setBackgroundResource(R.drawable.default_icon);
								} else {
									int height = iconView.getHeight();
									int width = (bitmap.getWidth()*height)/bitmap.getHeight();
									RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(width, height);
									lp.addRule(RelativeLayout.CENTER_IN_PARENT);
									iconView.setLayoutParams(lp);
									iconView.setImageBitmap(bitmap);
									iconView.setBackgroundDrawable(null);
								}
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