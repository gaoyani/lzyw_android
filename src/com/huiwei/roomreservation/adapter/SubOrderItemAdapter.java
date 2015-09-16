package com.huiwei.roomreservation.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.huiwei.commonlib.SyncImageLoader;
import com.huiwei.roomreservation.R;
import com.huiwei.roomreservation.activity.RoomReservationActivity;
import com.huiwei.roomreservationlib.data.Constant;
import com.huiwei.roomreservationlib.data.Data;
import com.huiwei.roomreservationlib.data.OrderManager;
import com.huiwei.roomreservationlib.info.OrderDetialInfo.SubOrderInfo;
import com.huiwei.roomreservationlib.info.OrderDetialInfo;
import com.huiwei.roomreservationlib.info.OrderInfo;
import com.huiwei.roomreservationlib.task.order.OperationOrderTask;
import com.huiwei.roomreservationlib.task.order.OperationSubOrderTask;

public class SubOrderItemAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private Context mContext;

	public SubOrderItemAdapter(Context context) {
		mInflater = LayoutInflater.from(context);
		mContext = context;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return Data.orderDetialInfo.subOrderList.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ViewHolder viewHolder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.sub_order_item, null);
			
			viewHolder = new ViewHolder();
			viewHolder.orderID = (TextView) convertView.findViewById(R.id.tv_order_id);
			viewHolder.orderInfo = (TextView) convertView.findViewById(R.id.tv_order_info);
			viewHolder.price = (TextView) convertView.findViewById(R.id.tv_price);
			viewHolder.status = (TextView) convertView.findViewById(R.id.tv_status);
			viewHolder.btn1 = (Button) convertView.findViewById(R.id.btn_1);
			viewHolder.btn2 = (Button) convertView.findViewById(R.id.btn_2);
			
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder)convertView.getTag();
		}
		
		SubOrderInfo info = Data.orderDetialInfo.subOrderList.get(position);
		viewHolder.orderID.setText(info.orderID);
		viewHolder.orderInfo.setText(info.info);
		viewHolder.price.setText(info.price+convertView.getResources().getString(R.string.yuan));
		viewHolder.status.setText(info.state);
		viewHolder.orderID.setText(info.orderID);
		viewHolder.btn1.setTag(position);
		viewHolder.btn2.setTag(position);
		viewHolder.btn1.setTag(R.id.tag_index, position);
		viewHolder.btn2.setTag(R.id.tag_index, position);
		setButtons(info, viewHolder.btn1, viewHolder.btn2);
		
		return convertView;
	}
	
	private void setButtons(SubOrderInfo info, Button btn1, Button btn2) {
		if (info.operations.size() == 2) {
			btn1.setVisibility(View.VISIBLE);
			btn2.setVisibility(View.VISIBLE);
			setOperation(info.operations.get(0), btn1);
			setOperation(info.operations.get(1), btn2);
		} else if (info.operations.size() == 1) {
			btn1.setVisibility(View.VISIBLE);
			btn2.setVisibility(View.GONE);
			setOperation(info.operations.get(0), btn1);
		} else {
			btn1.setVisibility(View.GONE);
			btn2.setVisibility(View.GONE);
		}
	}
	
	private void setOperation(int operation, Button btn) {
		btn.setTag(R.id.tag_operation, operation);
		switch (operation) {
		case SubOrderInfo.SUB_ORDER_CANCEL:
			btn.setBackgroundResource(R.drawable.button_orange_selector);
			btn.setText(mContext.getResources().getString(R.string.order_cancel));
			btn.setOnClickListener(operationClickListener);
			break;
	
		case SubOrderInfo.SUB_ORDER_DELETE:
			btn.setBackgroundResource(R.drawable.button_pink_selector);
			btn.setText(mContext.getResources().getString(R.string.sub_order_delete));
			btn.setOnClickListener(operationClickListener);
			break;
	
		case SubOrderInfo.SUB_ORDER_REORDER:
			btn.setBackgroundResource(R.drawable.button_orange_selector);
			btn.setText(mContext.getResources().getString(R.string.sub_order_reorder));
			btn.setOnClickListener(reorderClickListener);
			break;

		default:
			break;
		}
	}
	
	private OnClickListener operationClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			int position = (Integer)(v.getTag(R.id.tag_index));
			SubOrderInfo info = Data.orderDetialInfo.subOrderList.get(position);
			OperationSubOrderTask task = new OperationSubOrderTask(mContext, operationHandler, 
					info, (Integer)(v.getTag(R.id.tag_operation)));
			task.execute();
		}
	};
	
	private Handler operationHandler = new Handler() {
		public void dispatchMessage(Message msg) {
			if (msg.what == Constant.SUCCESS) {
				Toast.makeText(mContext, mContext.getString(
						R.string.order_operation_success), Toast.LENGTH_SHORT).show();
				SubOrderInfo orderInfo = (SubOrderInfo) msg.obj;
				OrderManager.operationSubOrder(orderInfo);
				notifyDataSetChanged();
			} else if (msg.what == Constant.DATA_ERROR){
				Toast.makeText(mContext, (String)msg.obj, Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(mContext, mContext.getString(
						R.string.order_operation_fail), Toast.LENGTH_SHORT).show();
			}
		};
	};
	
	private OnClickListener reorderClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			int position = (Integer)(v.getTag(R.id.tag_index));
			Intent intent = new Intent();
			intent.putExtra("sub_order_id", Data.orderDetialInfo.subOrderList.get(position).id);
			intent.putExtra("reservation_via", RoomReservationActivity.RESERVATION_SUB_ORDER);
			intent.setClass(mContext, RoomReservationActivity.class);
			mContext.startActivity(intent);
		}
	};
	
	public static class ViewHolder {
		TextView orderID;
		TextView orderInfo;
		TextView price;
		TextView status;
		Button btn1;
		Button btn2;
	}
}
