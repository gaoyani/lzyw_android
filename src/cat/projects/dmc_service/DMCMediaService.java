package cat.projects.dmc_service;

import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.Parcelable;
import android.os.RemoteException;
import android.util.Log;
import cat.projects.constant.DMCConstants;
import cat.projects.httpserver.NanoHTTPD;
import cat.projects.mediaplayer_ui.DMCRenderListActivity;

import com.dlna.datadefine.DLNA_ConnectionInfo;
import com.dlna.datadefine.DLNA_DeviceData;
import com.dlna.datadefine.DLNA_MediaInfo;
import com.dlna.datadefine.DLNA_PositionInfo;
import com.dlna.datadefine.DLNA_TransportInfo;
import com.dlna.datadefine.DLNA_TransportSettings;
import com.dlna.dmc.UpnpController;
import com.dlna.dmc.UpnpControllerInterface;
import com.luxs.config.G;
import com.luxs.connect.ConnectManager;
import com.luxs.utils.CodeUtils;

public class DMCMediaService extends Service {
	private static final String TAG = "DMCMediaService";

	private final Messenger mMessenger = new Messenger(
			new IncomingUIMsgHandler());
	private Messenger cMessenger;

	private static UpnpController upnpInstance = null;
	public List<DLNA_DeviceData> mDmrList = new ArrayList<DLNA_DeviceData>();
	public DLNA_DeviceData mCurrentDmr = null;
	private NanoHTTPD nh = null;

	public MyServiceHandler mServerHandler = new MyServiceHandler();
	private MyServiceBroadcastReceiver mServerReceiver = new MyServiceBroadcastReceiver();
	private final int MSG_SHOW_DMRLIST = 0x1001;
	private boolean mIsDlnaFounding = false; // record the first time called.
	private String mCurrentDMRName = "";
	private int mTotalTime = 0;
	private int mCurrentTime = 0;
	private Timer mGlobalTimer;
	private boolean mIsPlaying = false;
	public String mDLNAServerName = "geniusgithub";
	private UPnPListener upnpListener = new UPnPListener();
	public static int mIsThirdPartyDlna = 0;

	static {
		System.loadLibrary("codec");
	}

	public DMCMediaService() {
	}

	public static UpnpController getUpnpController() {
		// TODO Auto-generated method stub
		return upnpInstance;
	}

	public void onCreate() {
		super.onCreate();
		mGlobalTimer = new Timer(true);
		initControllerService();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("android.dmc.renderlist");
		registerReceiver(mServerReceiver, intentFilter);

		ConnectManager.setConnectMangerContext(getApplicationContext());
		Log.d(TAG, "MediaRenderService onCreate");
	}

	private void initControllerService() {
		startEngine();
	}

	public void setCurrentPlayer(DLNA_DeviceData _dmr) {
		mCurrentDmr = _dmr;
		if (mCurrentDmr != null) {
			upnpInstance.SetMR(_dmr);
		}
	}

	public void startPlay(String filename) {

		String res_uri = filename;
		String metainfo = "";
		boolean auth = true;
		int fmt = DMCUtil.fileFmt(res_uri);
		int slash = res_uri.lastIndexOf('/');
		if (slash > 0 && slash < res_uri.length()) {
			nh.setUri(res_uri);
			String fileName = res_uri.substring(slash + 1);
			String s = nh.encodeUri(fileName);
			res_uri = "http://" + DMCUtil.getLocalIpAddr(mCurrentDmr.localip)
					+ ":" + nh.getPort() + "/" + s;
		}
		if (mIsThirdPartyDlna == 1) {
			if (fmt == DMCUtil.CONTENT_FMT_VIDEO) {
				metainfo = DMCUtil.getDidlFmtString(filename,
						"object.item.videoItem.movie", "video/mpeg", res_uri, -1,
						auth, null);
			} else if (fmt == DMCUtil.CONTENT_FMT_AUDIO) {
				metainfo = DMCUtil.getDidlFmtString(filename,
						"object.item.audioItem.music", "audio/mpeg", res_uri, -1,
						auth, null);
			} else if (fmt == DMCUtil.CONTENT_FMT_PHOTO) {
				metainfo = DMCUtil.getDidlFmtString(filename,
						"object.item.imageItem.photo", "image/jpg", res_uri, -1,
						auth, null);
			}
			// Log.i(TAG, "发送:"+upnpInstance.SetCustomURI(res_uri, metainfo));
			upnpInstance.SetCustomURI(res_uri, metainfo);
			return;
		}
		// 获取随机码
		if ("".equals(G.CURRENT_VALUE)) {
			CodeUtils.getSuijiCode(getApplicationContext());
		}
		String passwd = G.CURRENT_VALUE;
		Log.i(TAG, "加密前:" + passwd);
		passwd = DMCMediaService.cd(passwd, 1);
		Log.i(TAG, "加密后:" + passwd);
		if (fmt == DMCUtil.CONTENT_FMT_VIDEO) {
			metainfo = DMCUtil.getDidlFmtString(filename,
					"object.item.videoItem.movie", "video/mpeg", res_uri, -1,
					auth, passwd);
		} else if (fmt == DMCUtil.CONTENT_FMT_AUDIO) {
			metainfo = DMCUtil.getDidlFmtString(filename,
					"object.item.audioItem.music", "audio/mpeg", res_uri, -1,
					auth, passwd);
		} else if (fmt == DMCUtil.CONTENT_FMT_PHOTO) {
			metainfo = DMCUtil.getDidlFmtString(filename,
					"object.item.imageItem.photo", "image/jpg", res_uri, -1,
					auth, passwd);
		}
		// Log.i(TAG, "发送:"+upnpInstance.SetCustomURI(res_uri, metainfo));
		upnpInstance.SetCustomURI(res_uri, metainfo);
	}

	public void pausePlay() {
		if (mCurrentDmr != null)
			upnpInstance.Pause();
	}

	public void resumePlay() {
		if (mCurrentDmr != null)
			upnpInstance.Play();
	}

	public void stopplay() {
		if (mCurrentDmr != null) {
			upnpInstance.Pause();
			upnpInstance.Stop();
		}
	}

	public void resumePlay(int pos) {
		if (pos >= 0) {
			String tstring = DMCUtil.convertToTime(pos); // 0-duration, 秒为单位
			if (mCurrentDmr != null)
				upnpInstance.Seek(tstring);
		}
	}

	public void stopPlay() {
		if (mCurrentDmr != null)
			upnpInstance.Stop();
	}

	@SuppressLint("HandlerLeak")
	class IncomingUIMsgHandler extends Handler {
		private String mPlayUri = "";

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case DMCConstants.MEDIA_DMC_CTL_MSG_GETLIST:
				if (!mIsDlnaFounding) { // first time play.
					List<DLNA_DeviceData> _dmrList = upnpInstance.GetMR();
					Log.i(TAG, "设备数量:" + _dmrList.size());
					if (_dmrList != null && _dmrList.size() > 0) {
						List<DLNA_DeviceData> _retList = new ArrayList<DLNA_DeviceData>();
						int size = _dmrList.size();
						for (int i = 0; i < size; i++) {
							DLNA_DeviceData devinst = DMCUtil
									.clone_DLNA_DeviceData(_dmrList.get(i));

							Log.v(TAG, "DLNA found DMR: " + devinst.localip
									+ "[" + devinst.devicename + "]");
							if (!devinst.devicename.startsWith("lux")
									&& (mIsThirdPartyDlna == 1)) {
								mIsDlnaFounding = true;
								mCurrentDMRName = devinst.devicename;
								setCurrentPlayer(devinst);
								mPlayUri = (String) msg.obj;

								cMessenger = msg.replyTo;
								Message play = Message.obtain(null,
										DMCConstants.MEDIA_DMC_CTL_MSG_PLAY,
										"hi string");
								mPlayUri = (String) msg.obj;
								startPlay(mPlayUri);
								Log.d(TAG, "Got message from UI, MSG_PLAY"
										+ mPlayUri);
								try {
									cMessenger.send(play);
								} catch (RemoteException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								return;
							}
							if (devinst.localip.equals(ConnectManager.myhost)) {
								mIsDlnaFounding = true;
								mCurrentDMRName = devinst.devicename;
								Log.d(TAG,
										"mCurrentDMRName is set when get list"
												+ mCurrentDMRName);
								setCurrentPlayer(devinst);
								mPlayUri = (String) msg.obj;

								cMessenger = msg.replyTo;
								Message play = Message.obtain(null,
										DMCConstants.MEDIA_DMC_CTL_MSG_PLAY,
										"hi string");
								mPlayUri = (String) msg.obj;
								startPlay(mPlayUri);
								Log.d(TAG, "Got message from UI, MSG_PLAY"
										+ mPlayUri);
								try {
									cMessenger.send(play);
								} catch (RemoteException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
							Log.v(TAG, "DLNA found DMR: " + devinst.localip
									+ "[" + devinst.devicename + "]");
							_retList.add(devinst);
						}
						mDmrList = _retList;
					}
					Log.v(TAG, "MEDIA MSG GETLIST was sent");
					if (!mIsDlnaFounding) {
						// send event to notify there is no dmr.
						cMessenger = msg.replyTo;
						Message noDMR = Message.obtain(null,
								DMCConstants.MEDIA_DMC_CTL_MSG_GETLIST,
								"hi string");

						Log.d(TAG, "No dmr was found");
						try {
							cMessenger.send(noDMR);
						} catch (RemoteException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}

				break;
			case DMCConstants.MEDIA_DMC_CTL_MSG_PLAY:
				// here need to send another type to configure when user not set
				// dmr
				if (!mIsDlnaFounding) { // first time play.
					List<DLNA_DeviceData> _dmrList = upnpInstance.GetMR();
					Log.i(TAG, "设备数量:" + _dmrList.size());
					if (_dmrList != null && _dmrList.size() > 0) {

						List<DLNA_DeviceData> _retList = new ArrayList<DLNA_DeviceData>();
						int size = _dmrList.size();
						for (int i = 0; i < size; i++) {

							DLNA_DeviceData devinst = DMCUtil
									.clone_DLNA_DeviceData(_dmrList.get(i));
							Log.v(TAG, "DLNA found DMR: " + devinst.localip
									+ "[" + devinst.devicename + "]");
							if (!devinst.devicename.startsWith("lux")
									&& (mIsThirdPartyDlna == 1)) {
								mIsDlnaFounding = true;
								mCurrentDMRName = devinst.devicename;
								setCurrentPlayer(devinst);
								cMessenger = msg.replyTo;
								Message play = Message.obtain(null,
										DMCConstants.MEDIA_DMC_CTL_MSG_PLAY,
										"hi string");
								mPlayUri = (String) msg.obj;
								startPlay(mPlayUri);
								Log.d(TAG, "Got message from UI, MSG_PLAY"
										+ mPlayUri);
								try {
									cMessenger.send(play);
								} catch (RemoteException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}

							if (devinst.localip.equals(ConnectManager.myhost)) {
								mIsDlnaFounding = true;
								mCurrentDMRName = devinst.devicename;
								Log.d(TAG, "mCurrentDMRName is set when play"
										+ mCurrentDMRName);
								setCurrentPlayer(devinst);
								mPlayUri = (String) msg.obj;
								// startPlay(mPlayUri);

								cMessenger = msg.replyTo;
								Message play = Message.obtain(null,
										DMCConstants.MEDIA_DMC_CTL_MSG_PLAY,
										"hi string");
								mPlayUri = (String) msg.obj;
								startPlay(mPlayUri);
								Log.d(TAG, "Got message from UI, MSG_PLAY"
										+ mPlayUri);
								try {
									cMessenger.send(play);
								} catch (RemoteException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
							_retList.add(devinst);
						}
						mDmrList = _retList;
					}
					Log.v(TAG, "MEDIA MSG GETLIST was sent");

					if (!mIsDlnaFounding) {
						// send event to notify there is no dmr.
						cMessenger = msg.replyTo;
						Message noDMR = Message.obtain(null,
								DMCConstants.MEDIA_DMC_CTL_MSG_GETLIST,
								"hi string");

						Log.d(TAG, "No dmr was found");
						try {
							cMessenger.send(noDMR);
						} catch (RemoteException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				} else {
					cMessenger = msg.replyTo;
					Message play = Message.obtain(null,
							DMCConstants.MEDIA_DMC_CTL_MSG_PLAY, "hi string");
					mPlayUri = (String) msg.obj;
					startPlay(mPlayUri);
					Log.d(TAG, "Got message from UI, MSG_PLAY" + mPlayUri);
					try {
						cMessenger.send(play);
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				break;

			case DMCConstants.MEDIA_DMC_CTL_MSG_PAUSE:
				cMessenger = msg.replyTo;

				Log.d(TAG, "Got message from UI, MSG_PAUSE");
				pausePlay();

				break;
			case DMCConstants.MEDIA_DMC_CTL_MSG_SEEK:
				cMessenger = msg.replyTo;
				Message seek = Message.obtain(null,
						DMCConstants.MEDIA_DMC_CTL_MSG_SEEK, "seek string");
				// mAddress = (String) msg.obj;
				int position = Integer.parseInt((String) msg.obj);
				resumePlay(position);
				Log.d(TAG, "Got message from UI, MSG_SEEK" + position);

				break;
			case DMCConstants.MEDIA_DMC_CTL_MSG_TOTALTIME:
				cMessenger = msg.replyTo;
				upnpInstance.GetPositionInfo();
				DMCUtil.DoSleep(200);
				Message totalTime = Message.obtain(null,
						DMCConstants.MEDIA_DMC_CTL_MSG_TOTALTIME, null);
				totalTime.obj = (mTotalTime);

				Log.d(TAG, "Got message from UI, MSG_TOTALTIME");

				try {
					cMessenger.send(totalTime);
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				break;
			case DMCConstants.MEDIA_DMC_CTL_STOP:
				Log.i(TAG, "停止播放");
				stopplay();
				break;
			default:
				super.handleMessage(msg);
			}
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mMessenger.getBinder();
	}

	@Override
	public void onDestroy() {
		Log.d(TAG, "MediaRenderService onDestroy");
		stopEngine();
		unregisterReceiver(mServerReceiver);
		super.onDestroy();
	}

	class MyServiceHandler extends Handler {

		public MyServiceHandler() {

		}

		public MyServiceHandler(Looper L) {
			super(L);
		}

		@Override
		public void handleMessage(Message msg) {
			Log.d("MyHandler", "handleMessage......");
			super.handleMessage(msg);
			switch (msg.arg1) {
			case MSG_SHOW_DMRLIST:
				mIsDlnaFounding = true;
				Intent intent = new Intent(getApplicationContext(),
						DMCRenderListActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.putParcelableArrayListExtra("renderlist",
						(ArrayList<? extends Parcelable>) mDmrList);
				startActivity(intent);
				break;
			}
		}
	}

	private class MyServiceBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			int position = intent.getIntExtra("renderlist", 0);
			setCurrentPlayer(mDmrList.get(position));
			mCurrentDMRName = mDmrList.get(position).devicename; // the user
																	// have
																	// already
			// chose the dmr.
		}
	}

	public class UPnPListener implements UpnpControllerInterface {

		@Override
		public void OnUpdateServerDevice() {
			// δ??
		}

		@Override
		public void OnUpdateRenderDevice() {
			List<DLNA_DeviceData> _dmrList = upnpInstance.GetMR(); // ???????DMR?豸????AirPin?????
			if (_dmrList != null && _dmrList.size() > 0) {
				List<DLNA_DeviceData> _retList = new ArrayList<DLNA_DeviceData>();
				int size = _dmrList.size();
				boolean flag = false;
				for (int i = 0; i < size; i++) {
					// ???clone????????????????????á?????JNI?????????Щ????clone???????????????list??UI???
					DLNA_DeviceData devinst = DMCUtil
							.clone_DLNA_DeviceData(_dmrList.get(i));
					Log.v(TAG, "DLNA found DMR: " + devinst.localip + "["
							+ devinst.devicename + "]");
					_retList.add(devinst);
					
					if (!devinst.devicename.startsWith("lux")
							&& (mIsThirdPartyDlna == 1)) {
						flag = true;
						break;
					}
					
					if ("".equals(mCurrentDMRName)) {
						if (devinst.localip.equals(ConnectManager.myhost)) {
							mCurrentDMRName = devinst.devicename;
						}
					}
					Log.d(TAG, "mCurrentDMRName is" + mCurrentDMRName);
					if (devinst.devicename.equals(mCurrentDMRName)) {
						flag = true;
					}

				}
				mDmrList = _retList;
				
				if (!flag) {
					mIsDlnaFounding = false;
					mIsPlaying = false;
					Message dmrlost = Message.obtain(null,
							DMCConstants.MEDIA_DMC_CTL_MSG_DMRLOST,
							"dmr lost string");
					// mAddress = (String) msg.obj;
					Log.d(TAG, "send message to UI, MSG_dmr lost");
					try {
						if (cMessenger != null) {
							cMessenger.send(dmrlost);
						}
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}
			// if dmrList lost, send event to notify chose a new one.

			Log.d(TAG, "Got message from UI, MSG_GETLIST");

		}

		@Override
		public void OnStopResult(int res, DLNA_DeviceData device) {
			// ????? Stop ??????res == 0 ??????
		}

		@Override
		public void OnPauseResult(int res, DLNA_DeviceData device) {
			// ????? Pause ??????res == 0 ??????
			if (res == 0) {
				Message pause = Message.obtain(null,
						DMCConstants.MEDIA_DMC_CTL_MSG_PAUSE, "pause string");
				// mAddress = (String) msg.obj;
				Log.d(TAG, "Got message from UI, MSG_PAUSE");
				try {
					if (cMessenger != null) {
						cMessenger.send(pause);
					}
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		@Override
		public void OnSetAVTransportURIResult(int res, DLNA_DeviceData device) {

			Log.i(TAG, "OnSetAVTransportURIResult:" + res);

			if (res == 0) {
				upnpInstance.Play(); // ?????????? Play

				try {
					ConnectManager.sendPlayStatue();
				} catch (SocketException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} // ???????????????????????????????????UI??????UI???????????

				// Play
			} else if (res == 401) {
				Intent intent = new Intent();
				intent.setAction(G.ACTION_ERRO_PASSWORD);
				sendBroadcast(intent);
				ConnectManager.sendGetPassword();

			}
		}

		@Override
		public void OnPlayResult(int res, DLNA_DeviceData device) {
			// ????? Play ??????res == 0
			// ????????????????UI???????????????????????
			Log.i(TAG, "OnPlayResult:" + res);
			if (res == 0) {
				Message play = Message.obtain(null,
						DMCConstants.MEDIA_DMC_CTL_MSG_PLAY, "hi string");
				try {
					if (cMessenger != null) {
						cMessenger.send(play);
					}
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		@Override
		public void OnGetPositionInfoResult(int res, DLNA_DeviceData device,
				DLNA_PositionInfo info) {
			Log.i(TAG, "OnGetPositionInfoResult:" + res);
			if (res == 0) {
				mTotalTime = (int) info.track_duration;
				mCurrentTime = (int) info.rel_time / 1000000;
				Log.d(TAG, info.abs_time + "..." + info.rel_time + "dddd"
						+ info.abs_count);
				Message totalTime = Message.obtain(null,
						DMCConstants.MEDIA_DMC_CTL_MSG_TOTALTIME, null);
				totalTime.obj = (mTotalTime);
				Log.d(TAG, "Got Total time form dmr" + mTotalTime);

				try {
					if (cMessenger != null)
						cMessenger.send(totalTime);
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Message currentTime = Message.obtain(null,
						DMCConstants.MEDIA_DMC_CTL_MSG_CURRENTTIME, null);
				currentTime.obj = (mCurrentTime);

				try {
					if (cMessenger != null)
						cMessenger.send(currentTime);
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		@Override
		public void OnGetTransportInfoResult(int res, DLNA_DeviceData device,
				DLNA_TransportInfo info) {
			Log.i(TAG, "OnGetTransportInfoResult:" + res);
			if (info.cur_transport_state.equalsIgnoreCase("PLAYING")) {
				// ??????
				mIsPlaying = true;
				Message play = Message.obtain(null,
						DMCConstants.MEDIA_DMC_CTL_MSG_PLAY, "hi string");
				try {
					if (cMessenger != null) {
						cMessenger.send(play);
					}
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if (info.cur_transport_state
					.equalsIgnoreCase("PAUSED_PLAYBACK")) {
				Message pause = Message.obtain(null,
						DMCConstants.MEDIA_DMC_CTL_MSG_PAUSE, "pause string");
				// mAddress = (String) msg.obj;
				Log.d(TAG, "Got message from UI, MSG_PAUSE");
				try {
					if (cMessenger != null) {
						cMessenger.send(pause);
					}
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// ?????
			} else if (info.cur_transport_state.equalsIgnoreCase("STOPPED")) {
				// ???
				mIsPlaying = false;
				Log.d(TAG, "playing session is over.");
			}
		}

		@Override
		public void OnSeekResult(int res, DLNA_DeviceData device) {
			// ????? Seek ??????res == 0 ??????
			Log.d(TAG, "getCurrent Seek result" + res);
		}

		@Override
		public void OnSetVolumeResult(int res, DLNA_DeviceData device) {
			// ????? SetVolume ??????res == 0 ??????
		}

		@Override
		public void OnGetVolumeResult(int res, DLNA_DeviceData device,
				String channel, int volume) {
			// ????? GetVolume ??????res == 0 ??????
			// volume ????豸??????? 0-100
		}

		@Override
		public void OnGetCurrentTransportActionsResult(int res,
				DLNA_DeviceData device) {
			// δ??
		}

		@Override
		public void OnGetDeviceCapabilitiesResult(int res,
				DLNA_DeviceData device) {
			// δ??
		}

		@Override
		public void OnGetMediaInfoResult(int res, DLNA_DeviceData device,
				DLNA_MediaInfo info) {
			// δ??
		}

		@Override
		public void OnMRStateVariablesChanged(int state, String value) {
			// δ??
		}

		@Override
		public void OnGetTransportSettingsResult(int res,
				DLNA_DeviceData device, DLNA_TransportSettings settings) {
			// δ??
		}

		@Override
		public void OnNextResult(int res, DLNA_DeviceData device) {
			// δ??
		}

		@Override
		public void OnPreviousResult(int res, DLNA_DeviceData device) {
			// δ??
		}

		@Override
		public void OnSetPlayModeResult(int res, DLNA_DeviceData device) {
			// δ??
		}

		@Override
		public void OnGetCurrentConnectionIDsResult(int res,
				DLNA_DeviceData device, String ids) {
			// δ??
		}

		@Override
		public void OnGetCurrentConnectionInfoResult(int res,
				DLNA_DeviceData device, DLNA_ConnectionInfo info) {
			// δ??
		}

		@Override
		public void OnGetProtocolInfoResult(int res, DLNA_DeviceData device,
				List<String> sources, List<String> sinks) {
			// δ??
		}

		@Override
		public void OnSetMuteResult(int res, DLNA_DeviceData device) {
			// δ??
		}

		@Override
		public void OnGetMuteResult(int res, DLNA_DeviceData device,
				String channel, boolean mute) {
			// δ??
		}

		@Override
		public void OnX_SlideShowResult(int res, DLNA_DeviceData device) {
			// δ??
		}

		@Override
		public void OnX_FastForwardResult(int res, DLNA_DeviceData device) {
			// δ??
		}

		@Override
		public void OnX_RewindResult(int res, DLNA_DeviceData device) {
			// δ??
		}

		@Override
		public void OnSetKeyResult(int res, DLNA_DeviceData device) {
			// δ??
			Log.i(TAG, "OnSetKeyResult:" + res);
		}

		@Override
		public void OnSetMouseResult(int res, DLNA_DeviceData device) {
			// δ??
		}

		@Override
		public void OnSetMessageResult(int res, DLNA_DeviceData device) {
			// δ??
		}
	}

	public boolean startEngine() {
		// TODO Auto-generated method stub
		new Thread() {
			public void run() {
				if (upnpInstance == null)
					upnpInstance = new UpnpController(); // ???????????
															// System.load()
				if (upnpInstance != null)
					upnpInstance.initUpnpProtocolControllerStack(upnpListener,
							"", "AirPinSender", true, "");
				if (nh == null) {
					try {
						nh = new NanoHTTPD(55666);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}
		}.start();
		// call the upnp interface every one second.
		TimerTask task = new TimerTask() {
			public void run() {

				if (upnpInstance != null) {
					upnpInstance.GetPositionInfo();
					if (mIsPlaying) {
						upnpInstance.GetTransportInfo();
					}
				}
				// Log.d(TAG, "upnp get positioninfo was called...");
			}
		};
		mGlobalTimer.schedule(task, 3000, 1000);
		return true;
	}

	public boolean stopEngine() {
		// TODO Auto-generated method stub
		mCurrentDmr = null;
		new Thread() {
			public void run() {
				Log.v(TAG, "UPnP Destroy...\n");
				if (nh != null) {
					nh.stop();
					nh = null;
				}
				if (upnpInstance != null) {
					upnpInstance.deinitUpnpProtocolControllerStack();
					upnpInstance = null;
				}
				Log.v(TAG, "UPnP Destroy done\n");
			}
		}.start();
		// while (eventHandler != null)
		DMCUtil.DoSleep(100);
		return true;
	}

	public static native String cd(String origString, int codec);

}
