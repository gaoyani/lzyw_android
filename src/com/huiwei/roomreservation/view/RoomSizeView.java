package com.huiwei.roomreservation.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.huiwei.roomreservation.R;
import com.huiwei.roomreservationlib.info.RoomSizeInfo;
import com.huiwei.roomreservationlib.info.TimeSlotInfo;

public class RoomSizeView extends RelativeLayout implements OnClickListener {
	
	private Context context;
	private Button size1, size2, size3;
	private RoomSizeInfo info1, info2, info3;
	
	public RoomSizeView(Context context) {
		super(context);
		this.context = context;
	}
	
	public RoomSizeView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
	}
	
	@Override
	protected void onFinishInflate() {
		// TODO Auto-generated method stub
		super.onFinishInflate();
		
		size1 = (Button)findViewById(R.id.btn_1);
		size2 = (Button)findViewById(R.id.btn_2);
		size3 = (Button)findViewById(R.id.btn_3);
		
		if (size1 != null) {
			size1.setOnClickListener(this);
			size2.setOnClickListener(this);
			size3.setOnClickListener(this);
		}
	}
	
	public void setRoomSizeInfo(RoomSizeInfo info1, RoomSizeInfo info2,
			RoomSizeInfo info3) {
		this.info1 = info1;
		this.info2 = info2;
		this.info3 = info3;
		
		initButton(size1, info1);
		initButton(size2, info2);
		initButton(size3, info3);
	}
	
	private void initButton(Button btn, RoomSizeInfo info) {
		if (info != null) {
			btn.setText(info.name);
		} else {
			btn.setVisibility(View.INVISIBLE);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_1:
			resetButton(size1, info1);
			break;
			
		case R.id.btn_2:
			resetButton(size2, info2);
			break;
			
		case R.id.btn_3:
			resetButton(size3, info3);
			break;

		default:
			break;
		}
		
	}
	
	private void resetButton(Button btn, RoomSizeInfo info) {
		btn.setBackgroundResource(info.isSelected ? R.drawable.type_unselected
				: R.drawable.type_selected);
		info.isSelected = !info.isSelected;
	}
}
	
