package com.huiwei.roomreservation.view;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.huiwei.roomreservation.R;
import com.huiwei.roomreservation.activity.LoginActivity;
import com.huiwei.roomreservation.activity.MainActivity;
import com.huiwei.roomreservation.activity.RechargeChoseActivity;
import com.huiwei.roomreservation.activity.setting.MemberInfoEditActivity;
import com.huiwei.roomreservationlib.data.Constant;
import com.huiwei.roomreservationlib.data.Data;
import com.huiwei.roomreservationlib.info.CardInfo;
import com.huiwei.roomreservationlib.task.member.MemberInfoTask;
import com.huiwei.roomreservationlib.task.pay.CreateRechargeOrderTask;

public class MemberInfoView extends RelativeLayout implements OnClickListener {
	
	private Context mContext;
	private MemberImgView memberImgView;
	private LoadingView loadingView;
	
	private String orderID;
	
	public MemberInfoView(Context context) {
		super(context);
		mContext = context;
	}
	
	public MemberInfoView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
	}
	
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		
		loadingView = (LoadingView)findViewById(R.id.loading);
		memberImgView = (MemberImgView)findViewById(R.id.view_member_img);
		Button editInfo = (Button)findViewById(R.id.btn_edit_info);
		Button recharge = (Button)findViewById(R.id.btn_recharge);
		Button exchange = (Button)findViewById(R.id.btn_exchange);
		if(editInfo != null) {
			editInfo.setOnClickListener(this);
			recharge.setOnClickListener(this);
			exchange.setOnClickListener(this);
			((TextView)findViewById(R.id.tv_member_name)).setOnClickListener(this);
			memberImgView.setImgClickable(false);
			memberImgView.setOnClickListener(this);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		
		case R.id.btn_edit_info: 
		case R.id.tv_member_name:
		case R.id.view_member_img: {
			Intent intent = new Intent();
			intent.setClass(mContext, MemberInfoEditActivity.class);
			mContext.startActivity(intent);
		}
			break;
			
		case R.id.btn_recharge:
			CreateRechargeOrderTask task = new CreateRechargeOrderTask(mContext, 
					createOrderHandler);
			task.execute();
			loadingView.setLoadingText(getResources().getString(R.string.loading));
			loadingView.setVisibility(View.VISIBLE);
			break;
			
		case R.id.btn_exchange:
			break;

		default:
			break;
		}
	}
	
	
	private Handler createOrderHandler = new Handler() {
		public void dispatchMessage(Message msg) {
			if (msg.what == Constant.SUCCESS) {
				orderID = (String) msg.obj;
				Intent intent = new Intent();
				intent.putExtra("order_id", orderID);
				intent.setClass(mContext, RechargeChoseActivity.class);
				mContext.startActivity(intent);
			} else if (msg.what == Constant.DATA_ERROR){
				Toast.makeText(mContext, (String) msg.obj, Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(mContext, getResources().getString(
						R.string.recharge_order_create_fail), Toast.LENGTH_SHORT).show();
			}
			
			loadingView.setVisibility(View.GONE);
		};
	};
	
	public void getMemberInfo() {
		loadingView.setLoadingText(getResources().getString(R.string.loading));
		loadingView.setVisibility(View.VISIBLE);
		MemberInfoTask task = new MemberInfoTask(mContext, taskHandler);
		task.execute();
	}
	
	private Handler taskHandler = new Handler() {
		public void dispatchMessage(android.os.Message msg) {
			if (msg.what == Constant.SUCCESS) {
				setMemberInfo();
			} else {
				if (Data.memberInfo.isAutoLogout) {
					Data.autoLogoutDialog
							.setOnDismissListener(new OnDismissListener() {

								@Override
								public void onDismiss(DialogInterface dialog) {
									Intent intent = new Intent();
									intent.setClass(mContext,
											LoginActivity.class);
									((FragmentActivity) mContext)
											.startActivityForResult(intent,
													MainActivity.TO_LOGIN);
									Data.memberInfo.isAutoLogout = false;
								}
							});

				}
			}

			loadingView.setVisibility(View.GONE);
		};
	};
	
	private void setMemberInfo() {
		memberImgView.loadImage(Data.memberInfo.imgUrl);
		memberImgView.setIconEditTipVisibility(View.VISIBLE);
		if (Data.memberInfo.nickName != null && 
				Data.memberInfo.nickName.length() != 0) {
			((TextView)findViewById(R.id.tv_member_name)).setText(
					Data.memberInfo.nickName);
		}
		
		((TextView)findViewById(R.id.tv_uidname)).setText(
				Data.memberInfo.uidName);
		((TextView)findViewById(R.id.tv_level)).setText(
				Data.memberInfo.curLevel);
		((TextView)findViewById(R.id.tv_cur_level)).setText(
				Data.memberInfo.curLevel);
		((TextView)findViewById(R.id.tv_next_level)).setText(
				Data.memberInfo.nextLevel);
		((ProgressBar)findViewById(R.id.pb_upgrade)).setMax(
				Data.memberInfo.nextLevelPoints-Data.memberInfo.curLevelPoints);
		((ProgressBar)findViewById(R.id.pb_upgrade)).setProgress(
				Data.memberInfo.curPoints-Data.memberInfo.curLevelPoints);
		((TextView)findViewById(R.id.tv_upgrade_tip)).setText(
				Html.fromHtml(Data.memberInfo.promotionTips));
		((TextView)findViewById(R.id.tv_account)).setText(
				Data.memberInfo.account);
		((TextView)findViewById(R.id.tv_integral)).setText(
				Data.memberInfo.grades);
		((TextView)findViewById(R.id.tv_consume_mouth)).setText(
				Data.memberInfo.mouthConsume);
		((TextView)findViewById(R.id.tv_consume_total)).setText(
				Data.memberInfo.totleConsume);
		
		((TextView)findViewById(R.id.tv_card_num)).setText(""+
				Data.memberInfo.cardList.size());
		LinearLayout layoutCardList = (LinearLayout)findViewById(R.id.layout_card_list);
		layoutCardList.removeAllViews();
		for (CardInfo cardInfo : Data.memberInfo.cardList) {
			MemberCardView view = (MemberCardView) LayoutInflater.from(mContext).
					inflate(R.layout.member_card_view, null);
			view.setInfo(cardInfo);
			layoutCardList.addView(view);
		}
	}
	
	public void getPhotoResult(int requestCode, Intent data) {
		memberImgView.getPhotoResult(requestCode, data);
	}
}
	
