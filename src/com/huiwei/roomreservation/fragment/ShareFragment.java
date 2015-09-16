package com.huiwei.roomreservation.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.huiwei.commonlib.SyncImageLoader;
import com.huiwei.roomreservation.R;
import com.huiwei.roomreservation.activity.LoginActivity;
import com.huiwei.roomreservation.activity.MainActivity;
import com.huiwei.roomreservation.activity.ReservationDoneActivity;
import com.huiwei.roomreservation.activity.StoreDetailActivity;
import com.huiwei.roomreservation.common.CommonConstant;
import com.huiwei.roomreservation.common.RequestShareTextTask;
import com.huiwei.roomreservation.view.MainMenuView;
import com.huiwei.roomreservationlib.data.Constant;
import com.huiwei.roomreservationlib.data.Data;
import com.huiwei.roomreservationlib.data.UrlConstant;
import com.huiwei.roomreservationlib.info.QRInfo;
import com.huiwei.roomreservationlib.task.CollectStoreTask;
import com.huiwei.roomreservationlib.task.main.GetQRTask;
import com.huiwei.roomreservationlib.task.main.UploadRecommendTask;
import com.luxs.utils.ShareUtlis;

public class ShareFragment extends Fragment implements OnClickListener {

	private ImageView imgQRCode;
	private EditText phoneNum, name;
	private TextView tip;
	private ProgressBar pBar;
	private QRInfo qrInfo;
	private int clickBtnID;
	private Handler parentHandler;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) { 
		View view = inflater.inflate(R.layout.fragment_share,
				container, false);
		
		imgQRCode = (ImageView)view.findViewById(R.id.iv_qr_code);
		phoneNum = (EditText)view.findViewById(R.id.et_phone);
		name = (EditText)view.findViewById(R.id.et_name);
		pBar = (ProgressBar)view.findViewById(R.id.pb);
		tip = (TextView)view.findViewById(R.id.tv_qr_tip);
		((ImageView)view.findViewById(R.id.iv_contacts)).setOnClickListener(this);
		((Button)view.findViewById(R.id.btn_next_step)).setOnClickListener(this);
		((Button)view.findViewById(R.id.btn_share)).setOnClickListener(this);
		((ImageButton)view.findViewById(R.id.btn_menu)).setOnClickListener(this);
		((ImageButton)view.findViewById(R.id.btn_return)).setOnClickListener(this);
		
		startQRTask();
		
		return view;
	}
	
	public void setParentHandler(Handler handler) {
		parentHandler = handler;
	}
	
	@Override
	public void onResume() {
		super.onResume();
	}

	private void startQRTask() {
		GetQRTask task = new GetQRTask(getActivity(), handler);
		task.execute();
	}
	
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == Constant.SUCCESS) {
				qrInfo = (QRInfo)(msg.obj);
				tip.setText(qrInfo.note);;
				loadImage();
			}
			
			pBar.setVisibility(View.GONE);
		};
	};

	public void loadImage() {
		SyncImageLoader imageLoader = new SyncImageLoader(getActivity());
		if (qrInfo.qrUrl != null && !qrInfo.qrUrl.equals("")) {
			imageLoader.loadImage(qrInfo.qrUrl,
					new SyncImageLoader.OnImageLoadListener() {
						
						@Override
						public void onImageLoad(Bitmap bitmap) {
							if (bitmap == null) {
								imgQRCode.setImageBitmap(null);
								imgQRCode.setBackgroundResource(R.drawable.default_icon);
							} else {
								imgQRCode.setImageBitmap(bitmap);
								imgQRCode.setBackgroundDrawable(null);
							}
						}
						
						@Override
						public void onError() {
							imgQRCode.setImageBitmap(null);
							imgQRCode.setBackgroundResource(R.drawable.default_icon);
						}
					}); 
			
		} else {
			imgQRCode.setImageBitmap(null);
			imgQRCode.setBackgroundResource(R.drawable.default_icon);
		}
	}
	

	@Override
	public void onDestroy() {

		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		clickBtnID = v.getId();
		switch (v.getId()) {
		case R.id.iv_contacts: 
			getActivity().startActivityForResult(new Intent(
		             Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI), MainActivity.TO_CONTRATS);
			break;
			
		case R.id.btn_next_step: {
			if (checkInput() && checkLogin()) {
				startRecommendTask();
			}
		}
			break;
			
		case R.id.btn_share: {
			if (checkLogin())
				shareTo();
		}
			break;
			
		case R.id.btn_menu:
			parentHandler.sendEmptyMessage(MainMenuView.SLIDE);
			break;
			
		case R.id.btn_return:
			parentHandler.sendEmptyMessage(MainActivity.TO_MAIN);
			break;

		default:
			break;
		}
		
	}
	
	private boolean checkLogin() {
		if (Data.memberInfo.isLogin)
			return true;

		Intent intent = new Intent();
		intent.setClass(getActivity(), LoginActivity.class);
		startActivity(intent);
		Toast.makeText(getActivity(),
				getResources().getString(R.string.unlogin),
				Toast.LENGTH_SHORT).show();

		return false;
	}
	
	private boolean checkInput() {
		if (phoneNum.getText().toString().length() != 11) {
			Toast.makeText(getActivity(), getResources().getString(R.string.phone_number_format_error), 
					Toast.LENGTH_SHORT).show();
			return false;
		}
		
		if (name.getText().toString().length() == 0) {
			Toast.makeText(getActivity(), getResources().getString(R.string.please_input_referrer_name), 
					Toast.LENGTH_SHORT).show();
			return false;
		}
		
		return true;
	}
	
	private void startRecommendTask() {
		UploadRecommendTask task = new UploadRecommendTask(getActivity(), 
				uploadHandler, phoneNum.getText().toString(), name.getText().toString());
		task.execute();
	}
	
	private Handler uploadHandler = new Handler() {
		public void dispatchMessage(Message msg) {
			if (msg.what == Constant.SUCCESS) {
				shareTo();
			} else if (msg.what == Constant.DATA_ERROR) {
				Toast.makeText(getActivity(), (String) msg.obj, Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(getActivity(), getResources().getString(
						R.string.upload_info_fail), Toast.LENGTH_SHORT).show();
			}
		};
	};
	
	private void shareTo() {
		RequestShareTextTask shareTextTask=new RequestShareTextTask(getActivity(), handlerShare, true);
		shareTextTask.execute(UrlConstant.SHARE_LEZI);
	}
	
	private Handler handlerShare = new Handler() {
		public void dispatchMessage(Message msg) {
			if (msg.what == Constant.SUCCESS) {
				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				builder.setMessage("分享成功");
				builder.setPositiveButton(R.string.ok, null);
				builder.create().show();
			} else {
				Toast.makeText(getActivity(), 
						R.string.share_error, Toast.LENGTH_LONG).show();
			}
		};
	};
	
	public void setContract(Intent data) {
		Uri contactData = data.getData();

		Cursor cursor = getActivity().managedQuery(contactData, null, null,
				null, null);
		cursor.moveToFirst();
		String username = cursor.getString(cursor
				.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
		String contactId = cursor.getString(cursor
				.getColumnIndex(ContactsContract.Contacts._ID));
		int iPhoneCnt = cursor.getInt(cursor
				.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

		Cursor phone = getActivity().getContentResolver().query(
				ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
				null,
				ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = "
						+ contactId, null, null);
		phone.moveToFirst();
		String usernumber = phone.getString(phone
				.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
		name.setText(username);
		phoneNum.setText(usernumber.replace("-", ""));

	}

}


