package com.huiwei.roomreservation.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huiwei.commonlib.CommonFunction;
import com.huiwei.roomreservation.R;
import com.huiwei.roomreservationlib.info.CardInfo;

public class MemberCardView extends RelativeLayout {
	
	private Context context;
	private ImageView cardBG;
	private TextView cardName, cardNumber, storeName;
	
	public MemberCardView(Context context) {
		super(context);
		this.context = context;
	}
	
	public MemberCardView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
	}
	
	@Override
	protected void onFinishInflate() {
		// TODO Auto-generated method stub
		super.onFinishInflate();
		
		cardBG = (ImageView)findViewById(R.id.iv_card);
		cardName = (TextView)findViewById(R.id.tv_card_name);
		cardNumber = (TextView)findViewById(R.id.tv_card_number);
		storeName = (TextView)findViewById(R.id.tv_store_name);
	}
	
	public void setInfo(CardInfo cardInfo) {
		
		cardBG.setBackgroundColor(CommonFunction.HexadecimalStrToColor(cardInfo.cardBG));
		cardName.setText(cardInfo.cardName);
		cardName.setTextColor(CommonFunction.HexadecimalStrToColor(cardInfo.cardNameColor));
		cardNumber.setText(cardInfo.cardNumber);
		cardNumber.setTextColor(CommonFunction.HexadecimalStrToColor(cardInfo.cardNumberColor));
		storeName.setText(cardInfo.storeName);
		storeName.setTextColor(CommonFunction.HexadecimalStrToColor(cardInfo.storeNameColor));
	} 

}
	
