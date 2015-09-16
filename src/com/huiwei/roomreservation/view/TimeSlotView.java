package com.huiwei.roomreservation.view;

import java.util.ArrayList;
import java.util.List;

import android.R.integer;
import android.content.Context;
import android.os.Handler;
import android.text.InputType;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.huiwei.commonlib.CommonFunction;
import com.huiwei.roomreservation.R;
import com.huiwei.roomreservationlib.info.TimeSlotInfo;

public class TimeSlotView extends LinearLayout implements OnClickListener {
	
	private Context context;
	private List<TimeSlotInfo> infoList;
	private List<Button> buttons = new ArrayList<Button>();
	private boolean isSingleSelction = false;
	private Handler handler;
	
	public TimeSlotView(Context context) {
		super(context);
		this.context = context;
	}
	
	public TimeSlotView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
	}
	
	@Override
	protected void onFinishInflate() {
		// TODO Auto-generated method stub
		super.onFinishInflate();
	}
	
	public void setClickable(boolean clickable) {
		if (clickable) {
			for (Button button : buttons) {
				button.setOnClickListener(this);
			}
		} else {
			for (Button button : buttons) {
				button.setOnClickListener(null);
			}
		}
	}
	
	public void initView(int col, List<TimeSlotInfo> infoList) {
		removeAllViews();
		buttons.clear();
		this.infoList = infoList;
		int row = infoList.size()%col == 0 ? infoList.size()/col : 
			infoList.size()/col+1;
		
		int number = 0;
		for (int i=0; i<row; i++) {
			LinearLayout layout = new LinearLayout(context);
			layout.setOrientation(LinearLayout.HORIZONTAL);
			
			for (int j=0; j<col; j++) {
				Button button = new Button(context);
				button.setTag(number);
				button.setTextSize(TypedValue.COMPLEX_UNIT_PX,
		                getResources().getDimensionPixelSize(R.dimen.size_11sp));
				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
						LayoutParams.MATCH_PARENT, CommonFunction.dip2px(context, 30));
				lp.weight = 1;
				lp.leftMargin = CommonFunction.dip2px(context, 2);
				
				if (infoList.size() > number) {
					TimeSlotInfo info = infoList.get(number);
					button.setText(info.time);
					if (info.isBookable) {
						button.setBackgroundResource(R.drawable.type_unselected);
					} else {
						button.setBackgroundResource(R.drawable.reservation_invalid);
					}
					buttons.add(button);
				} else {
					button.setText("08:00-09:00");
					button.setVisibility(View.INVISIBLE);
				}
				
				layout.addView(button, lp);
				number++;
			}
			LinearLayout.LayoutParams layoutLP = new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			layoutLP.topMargin = CommonFunction.dip2px(context, 2);
			addView(layout, layoutLP);
		}

		if (isSingleSelction && infoList.get(0).isBookable) {
			buttons.get(0).setBackgroundResource(R.drawable.type_single_selected);
		}
	}
	
	public void setSelections(List<String> idList) {
		for (int i = 0; i < infoList.size(); i++) {
			TimeSlotInfo info = infoList.get(i);
			for (String id : idList) {
				if (info.id.equals(id)) {
					info.isReserved = true;
					buttons.get(i).setBackgroundResource(R.drawable.type_selected);
				}
			}
		}
	}
	
	public void setSelection(String id) {
		for (int i = 0; i < infoList.size(); i++) {
			TimeSlotInfo info = infoList.get(i);
			if (info.id.equals(id)) {
				info.isReserved = true;
				buttons.get(i).setBackgroundResource(R.drawable.type_single_selected);
			}
		}
	}
	
	public void setSingleSel(boolean isSingleSel) {
		isSingleSelction = isSingleSel;
		if (buttons.size() != 0 && infoList.get(0).isBookable) {
			buttons.get(0).setBackgroundResource(R.drawable.type_single_selected);
		}
	}
	
	public void setHandler(Handler handler) {
		this.handler = handler;
	}

	@Override
	public void onClick(View v) {
		int index = (Integer)v.getTag();
		resetButton((Button)v, infoList.get(index));
	}
	
	private void resetButton(Button btn, TimeSlotInfo info) {
		if (!info.isBookable)
			return;
		
		if (isSingleSelction) {
			for (int i=0; i<buttons.size(); i++) {
				Button button = buttons.get(i);
				if (infoList.get(i).isBookable) {
					button.setBackgroundResource(R.drawable.type_unselected);
					infoList.get(i).isReserved = false;
				}
			}
			info.isReserved = true;
			btn.setBackgroundResource(R.drawable.type_single_selected);
		} else {
			if (info.isBookable) {
				btn.setBackgroundResource(info.isReserved ? 
						R.drawable.type_unselected : R.drawable.type_selected);
				info.isReserved = !info.isReserved;
			}
		}
		
		if (handler != null) {
			handler.sendEmptyMessage(0);
		}
	}
}
	
