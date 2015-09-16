package com.huiwei.roomreservation.view;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huiwei.commonlib.SyncImageLoader;
import com.huiwei.roomreservation.R;

public class MemberImgView extends RelativeLayout implements OnClickListener {
	
	private static final int TAKE_PHOTO = 20;
	private static final int GET_PHOTO = 21;
	private static final int CUT_PHOTO = 22;

	private Context mContext;
	private ImageView img;
	private TextView edit;
	private File file = null;
	private String filePath = "";
	
	public MemberImgView(Context context) {
		super(context);
		mContext = context;
	}
	
	public MemberImgView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
	}
	
	@Override
	protected void onFinishInflate() {
		// TODO Auto-generated method stub
		super.onFinishInflate();
		
		img = (ImageView)findViewById(R.id.iv_icon);
		edit = (TextView)findViewById(R.id.tv_edit);
	}
	
	public void setImgClickable(boolean clickable) {
		if (clickable) {
			img.setOnClickListener(this);
			edit.setOnClickListener(this);
		}
	}
	
	public String getImgFilePath() {
		return filePath;
	}
	
	public void setIconEditTipVisibility(int visibility) {
		((TextView)findViewById(R.id.tv_edit)).setVisibility(visibility);
	}
	
	public void loadImage(final String iconUrl) {
		if (iconUrl == null || iconUrl.length() == 0) {
			img.setVisibility(View.GONE);
			return;
		}
		
		SyncImageLoader imageLoader = new SyncImageLoader(mContext);
		if (iconUrl != null && !iconUrl.equals("")) {
			imageLoader.loadImage(iconUrl,
					new SyncImageLoader.OnImageLoadListener() {
						
						@Override
						public void onImageLoad(Bitmap bitmap) {
							if (bitmap == null) {
								img.setImageBitmap(null);
								img.setBackgroundResource(R.drawable.default_icon);
							} else {
								img.setImageBitmap(bitmap);
								img.setBackgroundDrawable(null);
							}
						}
						
						@Override
						public void onError() {
							img.setImageBitmap(null);
							img.setBackgroundResource(R.drawable.default_icon);
						}
					}); 
			
		} else {
			img.setImageBitmap(null);
			img.setBackgroundResource(R.drawable.default_icon);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		
		case R.id.iv_icon:
		case R.id.tv_edit:
			getPhoto();
			break;

		default:
			break;
		}
	}
	
	public void getPhoto() {
		String[] items = getResources().getStringArray(R.array.item);
		new AlertDialog.Builder(mContext)
				.setItems(items, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						String path = Environment.getExternalStorageDirectory()
								.toString() + "/lezi";
						File path1 = new File(path);
						if (!path1.exists()) {
							path1.mkdirs();
						}
						file = new File(path1, System.currentTimeMillis() + ".jpg");
						filePath = file.getAbsolutePath();
						  
						if (which == 0) {
							Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
							intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
							((Activity)mContext).startActivityForResult(intent, TAKE_PHOTO);
						} else {
							Intent intent = new Intent();
							intent.setType("image/*");
							intent.setAction(Intent.ACTION_GET_CONTENT);
							((Activity)mContext).startActivityForResult(intent, GET_PHOTO);
						}
					}

				}).setNegativeButton(R.string.cancel, null).show();
	}
	
	public void getPhotoResult(int requestCode, Intent data) {
		img.setVisibility(View.VISIBLE);
		if (requestCode == CUT_PHOTO) {
			if (data != null) {
				Bundle extras = data.getExtras();
				if (extras != null) {
					Bitmap photo = extras.getParcelable("data");
					saveBmp(photo);
				}
			}
		} else  {
			Uri uri;
			if (data == null) {
				uri = Uri.fromFile(file);
			} else {
				uri = data.getData();
			}
			if (uri != null)
				startPhotoZoom(uri);
		}
	}
	
	public void startPhotoZoom(Uri uri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", 100);
		intent.putExtra("outputY", 100);
		intent.putExtra("return-data", true);
		((Activity)mContext).startActivityForResult(intent, CUT_PHOTO);
	}
	
	private void saveBmp(Bitmap bmp) {
		try {
			img.setImageBitmap(bmp);
			img.setBackgroundDrawable(null);
			File fileImg = new File(filePath);
			FileOutputStream fOut = null;
			try {
				fOut = new FileOutputStream(fileImg);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			bmp.compress(Bitmap.CompressFormat.PNG, 100, fOut);
			try {
				fOut.flush();
				fOut.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			file = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
	
