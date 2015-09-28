package com.huiwei.roomreservation.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.huiwei.roomreservation.R;
import com.huiwei.roomreservation.activity.CommentActivity;
import com.huiwei.roomreservation.activity.PaymentChoseActivity;
import com.huiwei.roomreservation.activity.RoomReservationActivity;
import com.huiwei.roomreservationlib.data.Constant;
import com.huiwei.roomreservationlib.data.Data;
import com.huiwei.roomreservationlib.data.OrderManager;
import com.huiwei.roomreservationlib.info.CommentInfo;
import com.huiwei.roomreservationlib.info.OrderInfo;
import com.huiwei.roomreservationlib.info.StoreInfo;
import com.huiwei.roomreservationlib.task.order.OperationOrderTask;

public class OrderItemAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private Context mContext;
	private List<OrderInfo> orderInfoList = new ArrayList<OrderInfo>();

	public OrderItemAdapter(Context context) {
		mInflater = LayoutInflater.from(context);
		mContext = context;
	}
	
	public void setData(List<OrderInfo> orderInfoList) {
		if (orderInfoList != null) {
			this.orderInfoList.clear();
			this.orderInfoList.addAll(orderInfoList);
			notifyDataSetChanged();
		}
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return orderInfoList.size();
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
			convertView = mInflater.inflate(R.layout.member_order_item, null);
			
			viewHolder = new ViewHolder();
			viewHolder.orderID = (TextView) convertView.findViewById(R.id.tv_order_id);
			viewHolder.orderTime = (TextView) convertView.findViewById(R.id.tv_order_time);
			viewHolder.orderInfo = (TextView) convertView.findViewById(R.id.tv_order_info);
			viewHolder.price = (TextView) convertView.findViewById(R.id.tv_price);
			viewHolder.status = (TextView) convertView.findViewById(R.id.tv_status);
			viewHolder.storeName = (TextView) convertView.findViewById(R.id.tv_store_name);
			viewHolder.phoneNum = (TextView) convertView.findViewById(R.id.tv_phone_number);
			viewHolder.btn1 = (Button) convertView.findViewById(R.id.btn_1);
			viewHolder.btn2 = (Button) convertView.findViewById(R.id.btn_2);
			viewHolder.btn3 = (Button) convertView.findViewById(R.id.btn_3);
			
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder)convertView.getTag();
		}
		
		OrderInfo info = orderInfoList.get(position);
		viewHolder.orderID.setText(info.orderID);
		viewHolder.orderTime.setText(info.time);
		viewHolder.orderInfo.setText(info.info);
		viewHolder.price.setText(info.price+convertView.getResources().getString(R.string.yuan));
		viewHolder.status.setText(info.state);
		viewHolder.storeName.setText(info.storeName);
		viewHolder.orderID.setText(info.orderID);
		viewHolder.phoneNum.setText(info.phoneNumber);
		viewHolder.btn1.setTag(R.id.tag_index, position);
		viewHolder.btn2.setTag(R.id.tag_index, position);
		viewHolder.btn3.setTag(R.id.tag_index, position);
		setButtons(info, viewHolder.btn1, viewHolder.btn2, viewHolder.btn3);
		
		return convertView;
	}
	
	private void setButtons(OrderInfo info, Button btn1, Button btn2, Button btn3) {
		if (info.operations.size() == 3) {
			btn1.setVisibility(View.VISIBLE);
			btn2.setVisibility(View.VISIBLE);
			btn3.setVisibility(View.VISIBLE);
			setOperation(info.operations.get(0), btn1);
			setOperation(info.operations.get(1), btn2);
			setOperation(info.operations.get(2), btn3);
		} else if (info.operations.size() == 2) {
			btn1.setVisibility(View.VISIBLE);
			btn2.setVisibility(View.VISIBLE);
			btn3.setVisibility(View.GONE);
			setOperation(info.operations.get(0), btn1);
			setOperation(info.operations.get(1), btn2);
		} else if (info.operations.size() == 1) {
			btn1.setVisibility(View.VISIBLE);
			btn2.setVisibility(View.GONE);
			btn3.setVisibility(View.GONE);
			setOperation(info.operations.get(0), btn1);
		} else {
			btn1.setVisibility(View.GONE);
			btn2.setVisibility(View.GONE);
			btn3.setVisibility(View.GONE);
		}
	}
	
	private void setOperation(int operation, Button btn) {
		btn.setTag(R.id.tag_operation, operation);
		
		switch (operation) {
		case OrderInfo.ORDER_CANCEL:
			btn.setBackgroundResource(R.drawable.button_orange_selector);
			btn.setText(mContext.getResources().getString(R.string.order_cancel));
			btn.setOnClickListener(operationClickListener);
			break;
			
		case OrderInfo.ORDER_COMMENT:
			btn.setBackgroundResource(R.drawable.button_blue_selector);
			btn.setText(mContext.getResources().getString(R.string.order_comment));
			btn.setOnClickListener(commentClickListener);
			break;
			
		case OrderInfo.ORDER_CONFIRM:
			btn.setBackgroundResource(R.drawable.button_blue_selector);
			btn.setText(mContext.getResources().getString(R.string.order_confirm));
			btn.setOnClickListener(operationClickListener);
			break;
	
		case OrderInfo.ORDER_DELETE:
			btn.setBackgroundResource(R.drawable.button_pink_selector);
			btn.setText(mContext.getResources().getString(R.string.order_delete));
			btn.setOnClickListener(operationClickListener);
			break;
	
		case OrderInfo.ORDER_REORDER:
			btn.setBackgroundResource(R.drawable.button_orange_selector);
			btn.setText(mContext.getResources().getString(R.string.order_reorder));
			btn.setOnClickListener(reorderClickListener);
			break;
	
		case OrderInfo.ORDER_PAY:
			btn.setBackgroundResource(R.drawable.button_pink_selector);
			btn.setText(mContext.getResources().getString(R.string.order_pay));
			btn.setOnClickListener(payClickListener);
			break;

		default:
			break;
		}
	}
	
	private OnClickListener operationClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			int position = (Integer)(v.getTag(R.id.tag_index));
			OrderInfo info = orderInfoList.get(position);
			OperationOrderTask task = new OperationOrderTask(mContext, operationHandler, 
					info, (Integer)(v.getTag(R.id.tag_operation)));
			task.execute();
		}
	};
	
	private Handler operationHandler = new Handler() {
		public void dispatchMessage(Message msg) {
			if (msg.what == Constant.SUCCESS) {
				Toast.makeText(mContext, mContext.getString(
						R.string.order_operation_success), Toast.LENGTH_SHORT).show();
				OrderInfo orderInfo = (OrderInfo) msg.obj;
				OrderManager.operationOrder(orderInfo);
				notifyDataSetChanged();
			} else {
				Toast.makeText(mContext, mContext.getString(
						R.string.order_operation_fail), Toast.LENGTH_SHORT).show();
			}
		};
	};
	
	private OnClickListener commentClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			int position = (Integer)(v.getTag(R.id.tag_index));
			Data.orderDetialInfo.copy(orderInfoList.get(position));
			Intent intent = new Intent();
			intent.putExtra("comment_type", CommentInfo.COMMENT_ORDER);
			intent.setClass(mContext, CommentActivity.class);
			mContext.startActivity(intent);
		}
	};
	
	private OnClickListener reorderClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			int position = (Integer)(v.getTag(R.id.tag_index));
			Data.orderDetialInfo.copy(orderInfoList.get(position));
			Intent intent = new Intent();
			intent.putExtra("reservation_via", RoomReservationActivity.RESERVATION_ORDER);
			intent.setClass(mContext, RoomReservationActivity.class);
			mContext.startActivity(intent);
		}
	};
	
	private OnClickListener payClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			int position = (Integer)(v.getTag(R.id.tag_index));
			Data.orderDetialInfo.copy(orderInfoList.get(position));
			Intent intent = new Intent();
			intent.putExtra("order_id", Data.orderDetialInfo.id);
			intent.putExtra("order_sn", Data.orderDetialInfo.orderID);
			intent.putExtra("store_name", Data.orderDetialInfo.storeName);
			intent.putExtra("price_type", Data.orderDetialInfo.info);
			intent.putExtra("price", Data.orderDetialInfo.price);
			intent.putExtra("is_from_order", true);
			intent.setClass(mContext, PaymentChoseActivity.class);
			mContext.startActivity(intent);
		}
	};
	
	public static class ViewHolder {
		TextView orderID;
		TextView orderTime;
		TextView orderInfo;
		TextView storeName;
		TextView phoneNum;
		TextView price;
		TextView status;
		Button btn1;
		Button btn2;
		Button btn3;
	}
}
