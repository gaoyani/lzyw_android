package com.huiwei.roomreservation.common;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.huiwei.commonlib.CommonFunction;
import com.huiwei.roomreservationlib.data.Constant;
import com.huiwei.roomreservationlib.data.Data;
import com.huiwei.roomreservationlib.data.TaskHttpClient;
import com.huiwei.roomreservationlib.info.ComplaintInfo;

public class RequestCodeTask extends AsyncTask<String, Void, Integer> {
	Context context = null;
	Handler handler;
	Remote remote;

	public RequestCodeTask(Context context, Handler handler) {
		this.context = context;
		this.handler = handler;
	}

	@Override
	protected Integer doInBackground(String... params) {
		int flag = Constant.SUCCESS;

		try {
			HttpPost httpPost = new HttpPost(params[0]);
			JSONObject param = new JSONObject();
			// param.put("user_id", Data.memberInfo.id);
			// param.put("business_id", Data.storeDetailInfo.id);
			// param.put("reason", complaintInfo.reason);
			// param.put("content", complaintInfo.content);
			param.put("ssid", CommonFunction.getSSID(context));
			param.put("location", "" + Data.memberInfo.longitude + ","
					+ Data.memberInfo.latitude);
			httpPost.setEntity(new StringEntity(param.toString(), HTTP.UTF_8));
			TaskHttpClient taskClient = new TaskHttpClient();
			HttpResponse httpResponse = taskClient.client.execute(httpPost);
			int code = httpResponse.getStatusLine().getStatusCode();
			if (code == HttpStatus.SC_OK) {
				String retSrc = EntityUtils.toString(httpResponse.getEntity());
				remote = new Remote();
				if (!retSrc.equals("[]")) {
					String decode = new String(retSrc.getBytes(), "UTF-8");
					JSONTokener jsonParser = new JSONTokener(decode);
					JSONArray remJSON = (JSONArray) jsonParser.nextValue();
					JSONObject remoteJSON = remJSON.getJSONObject(0);//(JSONObject) jsonParser.nextValue();
					remote.setSid(remoteJSON.getString("sid"));
					remote.setsName(remoteJSON.getString("sname"));
					//remote.setRoom(remoteJSON.getString("room"));
					JSONObject myKey = remoteJSON.getJSONObject("key");
					remote.setRedName(myKey.getString("redname"));     
					remote.setRedCode(myKey.getString("redcode"));
					remote.setBlueName(myKey.getString("bluename"));
					remote.setBlueCode(myKey.getString("bluecode"));
					remote.setGreenName(myKey.getString("greenname"));
					remote.setGreenCode(myKey.getString("greencode"));
					remote.setYellowName(myKey.getString("yellowname"));
					remote.setYellowCode(myKey.getString("yellowcode"));
				} else {
					remote.setSid("CHINA-DTV-N16");
					remote.setsName("权金城");
					//remote.setRoom("A305");
					remote.setRedName("服务一");
					remote.setRedCode("26000000000");
					remote.setBlueName("服务二");
					remote.setBlueCode("26000000000");
					remote.setGreenName("服务三");
					remote.setGreenCode("26000000000");
					remote.setYellowName("服务四");
					remote.setYellowCode("26000000000");

				}
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
		Message message = new Message();
		message.what = 0x101;
		message.obj = remote;
		handler.sendMessage(message);
		Log.i("onPostExecute", result.toString());
	}
}
