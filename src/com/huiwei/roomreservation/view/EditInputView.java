package com.huiwei.roomreservation.view;

import android.R.integer;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.huiwei.roomreservation.R;
import com.huiwei.roomreservationlib.info.TimeSlotInfo;

public class EditInputView extends RelativeLayout {
	
	private Context context;
	private ImageView icon, delete;
	private EditText input;
	
	public EditInputView(Context context) {
		super(context);
		this.context = context;
	}
	
	public EditInputView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
	}
	
	@Override
	protected void onFinishInflate() {
		// TODO Auto-generated method stub
		super.onFinishInflate();
		
		icon = (ImageView)findViewById(R.id.iv_icon);
		delete = (ImageView)findViewById(R.id.iv_delete);
		input = (EditText)findViewById(R.id.et_input);
	}

	
	public void setInfo(String inputHint, String inputInfo, int inputType, int iconID) {
		input.setHint(inputHint);
		input.setText(inputInfo);
		input.setInputType(inputType);
		delete.setVisibility((inputInfo == null || inputInfo.length() == 0) ? View.GONE : View.VISIBLE);
		icon.setBackgroundResource(iconID);
		input.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence str, int arg1, int arg2, int arg3) {
				delete.setVisibility(str.length() == 0 ? View.GONE : View.VISIBLE);
			}
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable arg0) {
			}
		});
		
		delete.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				input.setText("");
			}
		});
	}
	
	public void disableInput() {
		input.setKeyListener(null);
		delete.setVisibility(View.GONE);
	}
	
	public String getText() {
		return input.getText().toString();
	}
	
}
	
