package com.huiwei.roomreservation.common;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import com.huiwei.roomreservation.R;
import com.huiwei.roomreservationlib.data.Constant;
import com.huiwei.roomreservationlib.data.TaskHttpClient;

public class RequestShareTextTask extends AsyncTask<String, Void, Integer> {
	Context context = null;
	String shareText = "";
	Handler handler;
	boolean isShareOut = true;

	public RequestShareTextTask(Context context, Handler handler, boolean isShareOut) {
		this.context = context;
		this.handler = handler;
		this.isShareOut = isShareOut;
	}

	@Override
	protected Integer doInBackground(String... params) {
		int flag = Constant.SUCCESS;

		try {
			HttpPost httpPost = new HttpPost(params[0]);
			TaskHttpClient taskClient = new TaskHttpClient();
			HttpResponse httpResponse = taskClient.client.execute(httpPost);
			int code = httpResponse.getStatusLine().getStatusCode();
			if (code == HttpStatus.SC_OK) {
				shareText = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
			} else {
				flag = Constant.OTHER_ERROR;
			}
		} catch (Exception e) {
			e.printStackTrace();
			flag = Constant.OTHER_ERROR;
		}

		return flag;
	}

	@Override
	protected void onPostExecute(Integer result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		if(result==Constant.SUCCESS && isShareOut){
			Intent shareIntent = new Intent();
			shareIntent.setAction(Intent.ACTION_SEND);
			shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
			shareIntent.setType("text/plain");
			
			context.startActivity(Intent.createChooser(shareIntent, context.getResources().getString(R.string.leshare)));
		}
		
		if (handler != null) {
			Message msg = new Message();
			msg.obj = shareText;
			msg.what = result;
			handler.sendMessage(msg);
		}
	}
}
