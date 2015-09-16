package com.luxs.img;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore.Video.Thumbnails;

import com.luxs.config.G;
import com.luxs.fileoperategroup.FileBean;
import com.luxs.fileoperategroup.FileDownloader;
import com.luxs.fileoperategroup.FileManager;

public class ImageLoader {
	
	public void downloadImgToSdCard(final String imgName,final ImageCallback callback){

		final Handler handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				callback.imageLoaded();
			}
		};
		
		new Thread(){
			public void run() {
				FileBean fileBean = new FileBean();
				String imgUrl = G.URL_IMG + imgName;
				fileBean.setFileName(imgName);
				fileBean.setFileUrl(imgUrl);
				fileBean.setFilePath(G.SD_IMG_PATH);
				FileManager fileManager = new FileManager();
				fileManager.setDownloader(new FileDownloader());
				switch (fileManager.downloadToSDCard(fileBean)) {
				case G.LOAD_SUCCESS:
					handler.sendEmptyMessage(1);
					break;
				case G.LOAD_FAILED:
					
					break;
				default:
					break;
				}
				
			};
		}.start();
		
	}
	
	public void loadDrawableThread(final FileBean fileBean,final ImageCallback callback){

		final Handler handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				Drawable d = (Drawable) msg.obj;
				callback.imageLoaded(d);
			}
		};
		
		new Thread(){
			public void run() {
				Bitmap bitmap = null;
				switch (fileBean.getFileType()) {
				case G.IMAGE: 
					bitmap = getImageThumbnail(fileBean.getFileThumb(), 60, 60);
					break;
				case G.VIDEO:
					bitmap = getVideoThumbnail(fileBean.getFileThumb(), 60, 60, Thumbnails.MICRO_KIND);
					break;
				case G.FOLDER:
					bitmap = getImageThumbnail(fileBean.getFileThumb(), 60, 60);
					if(bitmap == null) bitmap = getVideoThumbnail(fileBean.getFileThumb(), 60, 60, Thumbnails.MICRO_KIND);
					break;

				default:
					break;
				}
				Message msg = handler.obtainMessage();
				if(bitmap != null){
					Drawable d = new BitmapDrawable(bitmap);
					msg.obj = d;
				}
				msg.sendToTarget();
			};
		}.start();
		
	}
	
	public Drawable loadDrawable(FileBean fileBean){

		Bitmap bitmap = null;
		Drawable drawable = null;
		switch (fileBean.getFileType()) {
		case G.IMAGE: 
			bitmap = getImageThumbnail(fileBean.getFileThumb(), 60, 60);
			break;
		case G.VIDEO:
			bitmap = getVideoThumbnail(fileBean.getFileThumb(), 60, 60, Thumbnails.MICRO_KIND);
			break;
		case G.FOLDER:
			bitmap = getImageThumbnail(fileBean.getFileThumb(), 60, 60);
			if(bitmap == null) bitmap = getVideoThumbnail(fileBean.getFileThumb(), 60, 60, Thumbnails.MICRO_KIND);
			break;

		default:
			break;
		}
		if(bitmap != null){
			drawable = new BitmapDrawable(bitmap);
		}
		return drawable;
		
	}
	
	/** 
     * 根据指定的图像路径和大小来获取缩略图 
     * 此方法有两点好处： 
     *     1. 使用较小的内存空间，第一次获取的bitmap实际上为null，只是为了读取宽度和高度， 
     *        第二次读取的bitmap是根据比例压缩过的图像，第三次读取的bitmap是所要的缩略图。 
     *     2. 缩略图对于原图像来讲没有拉伸，这里使用了2.2版本的新工具ThumbnailUtils，使 
     *        用这个工具生成的图像不会被拉伸。 
     * @param imagePath 图像的路径 
     * @param width 指定输出图像的宽度 
     * @param height 指定输出图像的高度 
     * @return 生成的缩略图 
     */  
	private Bitmap getImageThumbnail(String imagePath, int width, int height) {  
        Bitmap bitmap = null;  
        BitmapFactory.Options options = new BitmapFactory.Options();  
        options.inJustDecodeBounds = true;  
        // 获取这个图片的宽和高，注意此处的bitmap为null  
        bitmap = BitmapFactory.decodeFile(imagePath, options);  
        options.inJustDecodeBounds = false; // 设为 false  
        // 计算缩放比  
        int h = options.outHeight;  
        int w = options.outWidth;  
        int beWidth = w / width;  
        int beHeight = h / height;  
        int be = 1;  
        if (beWidth < beHeight) {  
            be = beWidth;  
        } else {  
            be = beHeight;  
        }  
        if (be <= 0) {  
            be = 1;  
        }  
        options.inSampleSize = be;  
        // 重新读入图片，读取缩放后的bitmap，注意这次要把options.inJustDecodeBounds 设为 false  
        bitmap = BitmapFactory.decodeFile(imagePath, options);  
        // 利用ThumbnailUtils来创建缩略图，这里要指定要缩放哪个Bitmap对象  
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,  
                ThumbnailUtils.OPTIONS_RECYCLE_INPUT);  
        return bitmap;  
    }  
	
	/** 
     * 获取视频的缩略图 
     * 先通过ThumbnailUtils来创建一个视频的缩略图，然后再利用ThumbnailUtils来生成指定大小的缩略图。 
     * 如果想要的缩略图的宽和高都小于MICRO_KIND，则类型要使用MICRO_KIND作为kind的值，这样会节省内存。 
     * @param videoPath 视频的路径 
     * @param width 指定输出视频缩略图的宽度 
     * @param height 指定输出视频缩略图的高度度 
     * @param kind 参照MediaStore.Images.Thumbnails类中的常量MINI_KIND和MICRO_KIND。 
     *            其中，MINI_KIND: 512 x 384，MICRO_KIND: 96 x 96 
     * @return 指定大小的视频缩略图 
     */  
    private Bitmap getVideoThumbnail(String videoPath, int width, int height,  
            int kind) {  
        Bitmap bitmap = null;  
        // 获取视频的缩略图  
        bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);  
        
        if(bitmap != null){
	        System.out.println("w"+bitmap.getWidth());  
	        System.out.println("h"+bitmap.getHeight());  
	        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,  
	                ThumbnailUtils.OPTIONS_RECYCLE_INPUT);  
        }
        return bitmap;  
    }  
	
	
}

