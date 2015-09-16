package com.lux.broadlinksdkcall;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Environment;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import cn.com.broadlink.blnetwork.BLNetwork;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.huiwei.roomreservation.R;

public class FakeActivity {

	private BLNetwork mBlNetwork;
	private String api_id = "api_id";
	private String command = "command";
	private String licenseValue = "IDqOTOuVhMNQz8XWEc2wqmrjuYeTDGtBlMkm6AT1mmKKNLTrl45x4KzHGywehG/TzmSMIDnemvSlaNMSyYceBTJnNVQ10LKQ9sNzVIBX21r87yx+quE=";

	private String CODE = "code";
	private String MSG = "msg";
	private Context context = null;
	private String selectDeviceMac;
	private DeviceInfo selectedDevice;
	private ArrayList<DeviceInfo> deviceArrayList;
	// RM2 Control
	private Button mBtnRM2Study, mBtnRM2Code, mBtnRM2Send;
	private TextView mTvRM2CodeResult;
	private String mRM2SendData = "";
	private String mRM2SendDataTV = "";
	private String mRm2SendDataService1="";
	private String mRm2SendDataService2="";
	private String mRm2SendDataService3="";

	private int mCurrentRM1Password;
	private String mConfigInfoStr;
	private String mFileName = Environment.getExternalStorageDirectory()
			.getAbsolutePath() + "/lux" + "/devinfo.txt";
	private String mMac = "b4:43:0d:38:43:9f";
	public static int mDevFindFlag = 0;

	public void setActivity(Activity activity) {
		//盒子
		mRM2SendData = "260058000001299214361313121312121312131114121312131212371336141213361337133613371312131213361436131312121336141212371336141213121237133614121336130005430001294913000c4f0001284914000d05";
		//电视
		mRM2SendDataTV = "2600580000012893131212141211141214111213111313111437123813351438113713361511123912121213131113121213121214111312143612371437123713371337133613371400053f0001274a13000c4d0001274b12000d05";
		//红色按键
		mRm2SendDataService1="d70734002d0c110001a62e0b2d0b2e0b122712272d0b2e0b2d0c2d0c2d0c112711282d0c11272d0c102811272d0c11272d0d10271128112700000000";
		//投影
		mRm2SendDataService2="2600580000012894131311141114121312131237121312141138133714361238133812131237133713121338121213381212141312121212143613121436131213381237143613381300051c0001284912000c550001284912000d05";
		
		mRm2SendDataService3="2600d000093b091909190a1909190a18093d0a3a091a083c09190b190819093b091a090005e70b3a091a091a0919081a083c0a180b18093c0a19083d073d083d08190a3b090005a1093b091a081a091b071a081a0a3a0b3b08190a3b0919091a0a19083c0a19090005ec0a3b0a19081a081a081a0b3a0a190819093c0a190a3b0a3a093b0b180a3a0a0005a5093c09190a190a18091a081a093c0a3a0a180a3c08190a1a0819093c0a180a0005ec093c09190a18091b0819093d091a0918093c0919083d093b093d08190a3b0900088707000d050000000000000000";
		context = activity;
		mBlNetwork = BLNetwork.getInstanceBLNetwork(context);
		initView();
//		readFile(mFileName);
		if (mMac != null) {
			if (mDevFindFlag == 0) {
				probeList();
			}

		}
	}
	public void sendCode(String code){
		RM2SendService1(mMac,code);
	}
	public void sendPower(){
		
		new AlertDialog.Builder(context)
		.setIcon(android.R.drawable.ic_dialog_info).setTitle("是否触发电源键")
		.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub
			    RM2Send(mMac);
				RM2SendTV(mMac);
				RM2SendService2(mMac);
				RM2SendService3(mMac);
			}
		}).setNegativeButton("取消", null).show();
	}

	public void onCreate() {
     
	}

	// Probe List
	@SuppressWarnings("unchecked")
	public void probeList() {
		Log.d("FakeActivity", "probeList");
		JsonObject in = new JsonObject();
		JsonObject out = new JsonObject();
		JsonArray listJsonArray = new JsonArray();
		String probeOut;
		in.addProperty(api_id, 11);
		in.addProperty(command, "probe_list");
		String string = in.toString();
		probeOut = mBlNetwork.requestDispatch(string);
		out = new JsonParser().parse(probeOut).getAsJsonObject();
		listJsonArray = out.get("list").getAsJsonArray();

		Gson gson = new Gson();
		Type listType = new TypeToken<ArrayList<DeviceInfo>>() {
		}.getType();
		deviceArrayList = (ArrayList<DeviceInfo>) gson.fromJson(listJsonArray,
				listType);
		if (deviceArrayList.size() > 0) {
			Log.d("FakeActivity", "deviceArrayList is not 0");
			for (int i = 0; i < deviceArrayList.size(); i++) {
				if (deviceArrayList.get(i).getMac().equals(mMac)) { // 已知mac
					selectDeviceMac = deviceArrayList.get(i).getMac();
					selectedDevice = deviceArrayList.get(i);
					Log.e("selectDeviceMac", selectDeviceMac);
					addDevice();
					mDevFindFlag = 1;
					deviceArrayList.clear();
				}
			}
		} else {
			Toast.makeText(context, "R.string.toast_probe_no_device",
					Toast.LENGTH_SHORT).show();
		}

	}

	public void addDevice() {
		JsonObject in = new JsonObject();
		JsonObject out = new JsonObject();
		in.addProperty(api_id, 12);
		in.addProperty(command, "device_add");
		in.addProperty("mac", selectedDevice.getMac());
		in.addProperty("type", selectedDevice.getType());
		in.addProperty("name", selectedDevice.getName());
		in.addProperty("lock", selectedDevice.getLock());
		in.addProperty("password", selectedDevice.getPassword());
		mCurrentRM1Password = selectedDevice.getPassword();
		in.addProperty("id", selectedDevice.getId());
		in.addProperty("subdevice", selectedDevice.getSubdevice());
		in.addProperty("key", selectedDevice.getKey());
		String string = in.toString();
		String outString;
		outString = mBlNetwork.requestDispatch(string);
		out = new JsonParser().parse(outString).getAsJsonObject();

	}

	private void RM2Study(String mac) {
		JsonObject in = new JsonObject();
		JsonObject out = new JsonObject();
		String outString;
		in.addProperty(api_id, 132);
		in.addProperty(command, "rm2_study");
		in.addProperty("mac", mac);
		String inString = in.toString();
		outString = mBlNetwork.requestDispatch(inString);
		out = new JsonParser().parse(outString).getAsJsonObject();
		int code = out.get(CODE).getAsInt();
		if (0 == code) {
			Toast.makeText(context, "R.string.toast_rm2_study_success",
					Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(context, "R.string.toast_rm2_study_fail",
					Toast.LENGTH_SHORT).show();
		}

	}

	private void RM2Code(String mac) {
		JsonObject in = new JsonObject();
		JsonObject out = new JsonObject();
		String outString;
		in.addProperty(api_id, 133);
		in.addProperty(command, "rm2_code");
		in.addProperty("mac", mac);
		String inString = in.toString();

		outString = mBlNetwork.requestDispatch(inString);
		out = new JsonParser().parse(outString).getAsJsonObject();
		int code = out.get(CODE).getAsInt();
		if (0 == code) {
			mRM2SendData = out.get("data").getAsString();
			mTvRM2CodeResult.setText(mRM2SendData);
			Toast.makeText(context, "R.string.toast_rm2_code_success",
					Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(context, "R.string.toast_rm2_code_fail",
					Toast.LENGTH_SHORT).show();
		}
	}

	private void RM2Send(String mac) {
		Log.d("", "RM2Send start");
		JsonObject in = new JsonObject();
		JsonObject out = new JsonObject();
		String outString;
		in.addProperty(api_id, 134);
		in.addProperty(command, "rm2_send");
		in.addProperty("mac", mac);
		in.addProperty("data", mRM2SendData);
		String inString = in.toString();

		outString = mBlNetwork.requestDispatch(inString);
		out = new JsonParser().parse(outString).getAsJsonObject();
		int code = out.get(CODE).getAsInt();

	}
	private void RM2SendTV(String mac) {
		Log.d("", "RM2Send start");
		JsonObject in = new JsonObject();
		JsonObject out = new JsonObject();
		String outString;
		in.addProperty(api_id, 134);
		in.addProperty(command, "rm2_send");
		in.addProperty("mac", mac);
		in.addProperty("data", mRM2SendDataTV);
		String inString = in.toString();
		
		outString = mBlNetwork.requestDispatch(inString);
		out = new JsonParser().parse(outString).getAsJsonObject();
		int code = out.get(CODE).getAsInt();
		
	}
	private void RM2SendService1(String mac,String getcode) {
		Log.d("", "RM2SendPH start");
		JsonObject in = new JsonObject();
		JsonObject out = new JsonObject();
		String outString;
		in.addProperty(api_id, 134);
		in.addProperty(command, "rm2_send");
		in.addProperty("mac", mac);
		in.addProperty("data", getcode);
		String inString = in.toString();
		
		outString = mBlNetwork.requestDispatch(inString);
		out = new JsonParser().parse(outString).getAsJsonObject();
		int code = out.get(CODE).getAsInt();
		
	}
	private void RM2SendService2(String mac) {
		Log.d("", "RM2SendPH start");
		JsonObject in = new JsonObject();
		JsonObject out = new JsonObject();
		String outString;
		in.addProperty(api_id, 134);
		in.addProperty(command, "rm2_send");
		in.addProperty("mac", mac);
		in.addProperty("data", mRm2SendDataService2);
		String inString = in.toString();
		
		outString = mBlNetwork.requestDispatch(inString);
		out = new JsonParser().parse(outString).getAsJsonObject();
		int code = out.get(CODE).getAsInt();
		
	}
	private void RM2SendService3(String mac) {
		Log.d("", "RM2SendPH start");
		JsonObject in = new JsonObject();
		JsonObject out = new JsonObject();
		String outString;
		in.addProperty(api_id, 134);
		in.addProperty(command, "rm2_send");
		in.addProperty("mac", mac);
		in.addProperty("data", mRm2SendDataService3);
		String inString = in.toString();
		
		outString = mBlNetwork.requestDispatch(inString);
		out = new JsonParser().parse(outString).getAsJsonObject();
		int code = out.get(CODE).getAsInt();
		
	}

	public void initView() {
		NetworkUtil networkUtil = new NetworkUtil(context);
		networkUtil.startScan();
		String ssid = networkUtil.getWiFiSSID();
	}

	public void readFile(String filePath) {
		try {
			String encoding = "UTF-8";
			Gson gson = new Gson();
			File file = new File(filePath);
			if (file.isFile() && file.exists()) {
				InputStreamReader read = new InputStreamReader(
						new FileInputStream(file), encoding);
				BufferedReader bufferedReader = new BufferedReader(read);
				String lineTxt = null;
				while ((lineTxt = bufferedReader.readLine()) != null) {
					System.out.println(lineTxt);
					mConfigInfoStr = lineTxt;
					ConfigInfo configinfo = gson.fromJson(mConfigInfoStr,
							ConfigInfo.class);
					//mMac = configinfo.getMac();
					// mRM2SendData = configinfo.getCodeValue();
					mRM2SendData = "260058000001299214361313121312121312131114121312131212371336141213361337133613371312131213361436131312121336141212371336141213121237133614121336130005430001294913000c4f0001284914000d05";
					mRM2SendDataTV = "2600580000012893131212141211141214111213111313111437123813351438113713361511123912121213131113121213121214111312143612371437123713371337133613371400053f0001274a13000c4d0001274b12000d05";
					Log.d("FakeActivity", "mMac is" + mMac);
					Log.d("FakeActivity", "mRM2SendData is" + mRM2SendData);
				}
				read.close();
			} else {
				System.out.println("找不到指定的文件");
			}
		} catch (Exception e) {
			System.out.println("读取文件内容出错");
			e.printStackTrace();
		}
	}

}
