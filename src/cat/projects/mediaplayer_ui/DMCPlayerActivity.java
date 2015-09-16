package cat.projects.mediaplayer_ui;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import cat.projects.constant.DMCConstants;
import cat.projects.dmc_service.DMCMediaService;

import com.huiwei.roomreservation.R;
import com.luxs.adapter.BusinessFraAdapter;
import com.luxs.common_ui.SettingConnectActivity;
import com.luxs.config.G;
import com.luxs.fileoperategroup.FileBean;
import com.luxs.img.utility.LoadImageUtility;
import com.luxs.utils.Utils;

import dalvik.system.VMRuntime;

@SuppressLint("SdCardPath")
public class DMCPlayerActivity extends FragmentActivity implements
		OnPageChangeListener {
	private static final int MENU_EXIT = 0xCC882201;

	private static final String TAG = "DMCPlayerActivity";

	boolean inServer = false;
	boolean inStreaming = false;

	@SuppressLint("SdCardPath")
	final String resourceDirectory = "/sdcard/";

	private Messenger mServiceMessenger = null;
	private Messenger mMessenger;
	private boolean mBound;

	private static boolean isPlaying = false;

	private ImageButton mControlButton;
	private SeekBar mPlayerSeekBar;
	private TextView mTotalTimeText, mCurrentTimeText;
	private ImageButton mSearchButton, mQuitButton;
	private ImageView mImageView;
	private ImageButton mCotrolStartButton;
	private ImageButton mControlFastStepButton;

	private String mPlayUri = "";
	private int mCurrentTime, mTotalTime = 0;
	private boolean mUserTouchFlag = false;

	private List<FileBean> mCurrentLists;
	private int mCurrentListPosition = 0;
	// 自动播放
	private AutoPlayer autoPlayer;

	private boolean autoPlayerIsCalling = false;

	private ViewPager pager;

	private List<Fragment> pushFragments = new ArrayList<Fragment>();

	private BusinessFraAdapter pushAdapter;

	private String[] title;

	private Map<String, SoftReference<Bitmap>> imageCache = new HashMap<String, SoftReference<Bitmap>>();

	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final int HEAP_SIZE = 8 * 1024 * 1024;
		VMRuntime.getRuntime().setMinimumHeapSize(HEAP_SIZE);

		getPlayList();
		setNoWinTitle();
		getPlayUri();
		setContentView(R.layout.dmcplayer_layout);

		setupView();

		bindService(new Intent(this, DMCMediaService.class), mConnection,
				Context.BIND_AUTO_CREATE);
		getPlayTotalTime();
        initFilter();

	}

	/**
	 * 
	 */
	private void getPlayTotalTime() {
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub

				sendEventToService(DMCConstants.MEDIA_DMC_CTL_MSG_PLAY,
						mPlayUri);

				if (DMCPlayerActivity.this.getIntent().getIntExtra("type", 0) != G.IMAGE) {
					sendEventToService(
							DMCConstants.MEDIA_DMC_CTL_MSG_TOTALTIME, "");
					mControlButton.setImageDrawable(getResources().getDrawable(
							R.drawable.c_play));
				}

			}
		}, 1000);
	}

	/**
	 * get Play list
	 */
	@SuppressWarnings("unchecked")
	private void getPlayList() {
		mCurrentLists = (List<FileBean>) getIntent().getSerializableExtra(
				"dataList");
		mCurrentListPosition = getIntent().getIntExtra("position", 0);
	}

	/**
	 * remove the window title
	 */
	private void setNoWinTitle() {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		Window win = getWindow();
		win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		win.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
	}

	public void addBitmapToCache(String path) {
		try {
			Bitmap bitmap = BitmapFactory.decodeFile(path);
			SoftReference<Bitmap> softBitmap = new SoftReference<Bitmap>(bitmap);
			imageCache.put(path, softBitmap);
		} catch (OutOfMemoryError e) {
			Log.d(TAG, "OutOfMemoryError occurerd.");
			System.gc();
		}

	}

	public Bitmap getBitmapByPath(String path) {

		SoftReference<Bitmap> softBitmap = imageCache.get(path);
		if (softBitmap == null) {
			return null;
		}

		Bitmap bitmap = softBitmap.get();
		return bitmap;
	}

	/**
	 * 加载数据线程
	 */
	private class loadDataThread extends Thread {

		@Override
		public void run() {

			for (int i = 0; i < mCurrentLists.size(); i++) {
				PushPagerFra pagerFra = PushPagerFra.newInstance(mCurrentLists.get(i));
				pushFragments.add(pagerFra);
				pushAdapter.notifyDataSetChanged();
			}

			Message message = new Message();
			bingHandler.sendMessage(message);

		}
	}

	private void getPlayUri() {
		// TODO Auto-generated method stub
		mPlayUri = this.getIntent().getStringExtra("path");
		Log.d(TAG, "Got the paly uri, is" + mPlayUri);
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();

		sendEventToService(DMCConstants.MEDIA_DMC_CTL_STOP, "stop");
		Utils.send_back(DMCPlayerActivity.this);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

		Utils.send_back(DMCPlayerActivity.this);
		mImageView.setImageResource(R.drawable.icon);
		unbindService(mConnection);
        unregisterReceiver(receiver);
	}

	public void sendEventToService(int messageType, String param) {
		if (!mBound)
			return;
		// ��Service����һ��Message
		Message msg = Message.obtain(null, messageType);
		msg.obj = param;
		try {
			msg.replyTo = mMessenger;
			mServiceMessenger.send(msg);
			Log.d(TAG, "sendEvent to Service is sending");
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case DMCConstants.MEDIA_DMC_CTL_MSG_GETLIST:
				Log.d(TAG, "Get Render List");
				Toast.makeText(getApplicationContext(),
						getString(R.string.no_dmr), Toast.LENGTH_LONG).show();
				// start connect UI.
				Intent intent = new Intent();
				intent.putExtra("type", 0);
				intent.setClass(DMCPlayerActivity.this,
						SettingConnectActivity.class);
                Toast.makeText(DMCPlayerActivity.this,"请查看电视端是否开启Airplay",Toast.LENGTH_SHORT).show();
				startActivity(intent);
				break;

			case DMCConstants.MEDIA_DMC_CTL_MSG_PLAY:
				// Play button status changed.
				Log.d(TAG, "Got Message from Service MSG_PLAY");
				isPlaying = true;

				break;
			case DMCConstants.MEDIA_DMC_CTL_MSG_PAUSE:
				// Play button status changed.
				Log.d(TAG, "Got Message from Service MSG_PAUSE");
				isPlaying = false;

				break;
			case DMCConstants.MEDIA_DMC_CTL_MSG_TOTALTIME:
				mTotalTime = (Integer) (msg.obj);
				mTotalTimeText.setText(Utils.convertToTime(mTotalTime));
				mPlayerSeekBar.setMax(mTotalTime);
				break;
			case DMCConstants.MEDIA_DMC_CTL_MSG_CURRENTTIME:
				mCurrentTime = (Integer) (msg.obj);
				mCurrentTimeText.setText(Utils.convertToTime(mCurrentTime));
				if (!mUserTouchFlag) { // user clicked
					mPlayerSeekBar.setProgress(mCurrentTime);
				}

				Log.i(TAG, "当前:" + mCurrentTime);
				Log.i(TAG, "总进程:" + mTotalTime);
				if (mCurrentTime == mTotalTime && !autoPlayerIsCalling
						&& mTotalTime > 0) {
					send2next();
				}

				mUserTouchFlag = false;
				break;
			case DMCConstants.MEDIA_DMC_CTL_MSG_DMRLOST:
				Log.d(TAG, "dmr lost was received.");
//				finish();
				break;
			default:
				break;

			}
		}
	};

	private ServiceConnection mConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {

			mServiceMessenger = new Messenger(service);
			mMessenger = new Messenger(mHandler);
			mBound = true;
		}

		public void onServiceDisconnected(ComponentName className) {
			mServiceMessenger = null;
			mBound = false;
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu m) {
		m.add(0, MENU_EXIT, 0, "Exit");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem i) {
		switch (i.getItemId()) {
		case MENU_EXIT:
			finish();
			return true;
		default:
			return false;
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		sendEventToService(DMCConstants.MEDIA_DMC_CTL_STOP, "stop");
	}

	private void setupView() {

		pager = (ViewPager) findViewById(R.id.push_pager);
		getMtitle();
		pushAdapter = new BusinessFraAdapter(getSupportFragmentManager(),
				pushFragments, title);
		new loadDataThread().start();
		pager.setAdapter(null);
		pager.setAdapter(pushAdapter);
		pager.setOnPageChangeListener(this);

		mControlButton = (ImageButton) findViewById(R.id.dmcplayer_control_button);
		if (mCurrentLists.get(mCurrentListPosition).getFileType() == G.IMAGE) {
			mControlButton.setImageResource(R.drawable.c_play);
		}
		mControlButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				// Toast.makeText(DMCPlayerActivity.this, "点击",
				// Toast.LENGTH_LONG).show();
				if (mCurrentLists.get(mCurrentListPosition).getFileType() == G.IMAGE) {
					if (!autoPlayerIsCalling) {
						mControlButton.setImageResource(R.drawable.c_pause);
						autoPlayerIsCalling = true;
						new AutoPlayer().execute(G.IMAGE);
					} else {
						mControlButton.setImageResource(R.drawable.c_play);
						autoPlayerIsCalling = false;
					}
					return;
				} else {
					if (!isPlaying) {

						mControlButton.setImageDrawable(getResources()
								.getDrawable(R.drawable.c_play));
						sendEventToService(DMCConstants.MEDIA_DMC_CTL_MSG_PLAY,
								mPlayUri);
						sendEventToService(
								DMCConstants.MEDIA_DMC_CTL_MSG_TOTALTIME, "");
					} else {
						mControlButton.setImageDrawable(getResources()
								.getDrawable(R.drawable.c_pause));
						sendEventToService(
								DMCConstants.MEDIA_DMC_CTL_MSG_PAUSE, "pause");
					}
				}

			}
		});

		mPlayerSeekBar = (SeekBar) findViewById(R.id.dmcplayer_controlbar_seekbar);
		mPlayerSeekBar
				.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

					@Override
					public void onProgressChanged(SeekBar arg0, int arg1,
							boolean arg2) {
						// TODO Auto-generated method stub
						Log.d(TAG, "dmcPlayerSeekBar progress changed" + arg1);
						if (arg2) {
							sendEventToService(
									DMCConstants.MEDIA_DMC_CTL_MSG_SEEK,
									Integer.toString(arg1));
						}
					}

					@Override
					public void onStartTrackingTouch(SeekBar arg0) {
						// TODO Auto-generated method stub
						mUserTouchFlag = true;
					}

					@Override
					public void onStopTrackingTouch(SeekBar arg0) {
						// TODO Auto-generated method stub
						mUserTouchFlag = false;

					}

				});

		mSearchButton = (ImageButton) findViewById(R.id.dmcplayer_search);
		mSearchButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				sendEventToService(DMCConstants.MEDIA_DMC_CTL_MSG_PLAY,
						mPlayUri);
			}
		});

		mQuitButton = (ImageButton) findViewById(R.id.dmcplayer_quit);
		mQuitButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});

		mCotrolStartButton = (ImageButton) findViewById(R.id.dmcplayer_pre_one);
		mCotrolStartButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (mCurrentLists.size() == 0) {
					Log.d(TAG, "no common list");
					Toast.makeText(getApplicationContext(),
							getString(R.string.no_commonlist),
							Toast.LENGTH_LONG).show();
					return;
				}
				if (mCurrentListPosition >= 1) {
					mCurrentListPosition--;
					while (mCurrentLists.get(mCurrentListPosition)
							.getFileType() == G.FOLDER) {
						mCurrentListPosition--;
						if (mCurrentListPosition < 0) {
							mCurrentListPosition = 0;
							return;
						}
					}
				}
				Log.d(TAG, "mCurrentListPosition is " + mCurrentListPosition);

				// TODO Auto-generated method stub
				if (mCurrentListPosition >= 0
						&& mCurrentListPosition <= mCurrentLists.size() - 1) {
					mCurrentLists.get(mCurrentListPosition);
					if (mCurrentLists.get(mCurrentListPosition).getFilePath()
							.endsWith(".jpg")
							|| mCurrentLists.get(mCurrentListPosition)
									.getFilePath().endsWith(".png")) {
//						addBitmapToCache(mCurrentLists
//								.get(mCurrentListPosition).getFilePath());
//						mImageView.setImageBitmap(getBitmapByPath(mCurrentLists
//								.get(mCurrentListPosition).getFilePath()));
                        LoadImageUtility.loadSdImage(mImageView,mCurrentLists
                                .get(mCurrentListPosition).getFilePath());
						mTotalTimeText.setVisibility(View.GONE);
						mCurrentTimeText.setVisibility(View.GONE);
						mPlayerSeekBar.setVisibility(View.GONE);
					} else {
						mImageView.setImageResource(R.drawable.icon);
						mTotalTimeText.setVisibility(View.VISIBLE);
						mCurrentTimeText.setVisibility(View.VISIBLE);
						mPlayerSeekBar.setVisibility(View.VISIBLE);
					}
					if (mCurrentLists.get(mCurrentListPosition).getFileType() != G.FOLDER) {
						sendEventToService(DMCConstants.MEDIA_DMC_CTL_MSG_PLAY,
								mCurrentLists.get(mCurrentListPosition)
										.getFilePath());
						Log.d(TAG, "mCurrentListPosition send is "
								+ mCurrentListPosition);
					}

					/******* 推送 ********/
					pager.setCurrentItem(mCurrentListPosition);
					// sendEventToService(DMCConstants.MEDIA_DMC_CTL_MSG_PLAY,
					// mCurrentLists.get(mCurrentListPosition)
					// .getFilePath());
					// sendEventToService(
					// DMCConstants.MEDIA_DMC_CTL_MSG_TOTALTIME, "");

				}
			}
		});

		mControlFastStepButton = (ImageButton) findViewById(R.id.dmcplayer_next_one);
		mControlFastStepButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (mCurrentLists.size() == 0) {
					Log.d(TAG, "no common list");
					Toast.makeText(getApplicationContext(),
							getString(R.string.no_commonlist),
							Toast.LENGTH_LONG).show();
					return;
				}

				if (mCurrentListPosition <= mCurrentLists.size() - 2) {
					mCurrentListPosition++;
					while (mCurrentLists.get(mCurrentListPosition)
							.getFileType() == G.FOLDER) {
						mCurrentListPosition++;
						if (mCurrentListPosition > mCurrentLists.size() - 1) {
							mCurrentListPosition = mCurrentLists.size() - 1;
							return;
						}
					}
				}
				Log.d(TAG, "mCurrentListPosition is " + mCurrentListPosition);

				if (mCurrentListPosition > mCurrentLists.size() - 1) {
					mCurrentListPosition = mCurrentLists.size() - 1;
				}
				if (mCurrentListPosition <= mCurrentLists.size() - 1
						&& mCurrentListPosition >= 0) {
					mCurrentLists.get(mCurrentListPosition);
					if (mCurrentLists.get(mCurrentListPosition).getFilePath()
							.endsWith(".jpg")
							|| mCurrentLists.get(mCurrentListPosition)
									.getFilePath().endsWith(".png")) {
//						addBitmapToCache((mCurrentLists
//								.get(mCurrentListPosition).getFilePath()));
//						mImageView
//								.setImageBitmap(getBitmapByPath((mCurrentLists
//										.get(mCurrentListPosition)
//										.getFilePath())));

                        LoadImageUtility.loadSdImage(mImageView,mCurrentLists
                                .get(mCurrentListPosition).getFilePath());
						mTotalTimeText.setVisibility(View.GONE);
						mCurrentTimeText.setVisibility(View.GONE);
						mPlayerSeekBar.setVisibility(View.GONE);
					} else {
						mImageView.setImageResource(R.drawable.icon);
						mTotalTimeText.setVisibility(View.VISIBLE);
						mCurrentTimeText.setVisibility(View.VISIBLE);
						mPlayerSeekBar.setVisibility(View.VISIBLE);
					}
					if (mCurrentLists.get(mCurrentListPosition).getFileType() != G.FOLDER) {
						sendEventToService(DMCConstants.MEDIA_DMC_CTL_MSG_PLAY,
								mCurrentLists.get(mCurrentListPosition)
										.getFilePath());
						Log.d(TAG, "mCurrentListPosition send is "
								+ mCurrentListPosition);
					}

					/******* 推送 ********/
					pager.setCurrentItem(mCurrentListPosition);
					// sendEventToService(DMCConstants.MEDIA_DMC_CTL_MSG_PLAY,
					// mCurrentLists.get(mCurrentListPosition)
					// .getFilePath());
					// sendEventToService(
					// DMCConstants.MEDIA_DMC_CTL_MSG_TOTALTIME, "");

				}
			}

		});
		mTotalTimeText = (TextView) findViewById(R.id.TotalDurationLabel);
		mCurrentTimeText = (TextView) findViewById(R.id.CurrentDurationLabel);

		mImageView = (ImageView) findViewById(R.id.dmcplayer_show);
		if (this.getIntent().getIntExtra("type", 0) == G.IMAGE) {
//			addBitmapToCache((mPlayUri));
//			mImageView.setImageBitmap(getBitmapByPath(mPlayUri));
            LoadImageUtility.loadSdImage(mImageView,mCurrentLists
                    .get(mCurrentListPosition).getFilePath());
			mTotalTimeText.setVisibility(View.GONE);
			mCurrentTimeText.setVisibility(View.GONE);
			mPlayerSeekBar.setVisibility(View.GONE);
			Log.i(TAG, "计时卡死hi:");
			autoPlayer = new AutoPlayer();
			autoPlayer.execute(G.IMAGE);
			System.gc();

		}

	}

	private class AutoPlayer extends AsyncTask<Integer, Integer, Integer> {

		@Override
		protected Integer doInBackground(Integer... params) {
			// TODO Auto-generated method stub
			Log.i(TAG, "计时卡死hi:");
			int count = 0;

			// isPlaying is true, means it's ok to auto play the next one.
			while (autoPlayerIsCalling && isPlaying) {
				if (mCurrentLists.get(mCurrentListPosition).getFileType() != G.IMAGE) {
					continue;
				}
				if (mCurrentListPosition < mCurrentLists.size() - 1) {
					if (count != 0) {

						mCurrentListPosition++;
					}

				} else {
					mCurrentListPosition = 0;
				}
				count++;
				Log.i(TAG, "计时:" + count);

				publishProgress(mCurrentListPosition);
				try {

					Thread.sleep(10000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

			return mCurrentListPosition;
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
			pager.setCurrentItem(values[0]);
			// setBitmap(mCurrentLists.get(values[0]).getFilePath());

		}
	}

	private long time = 0;

	/**
	 * 推送下一个
	 */
	private void send2next() {

		if (System.currentTimeMillis() - time < 10000) {
			time = System.currentTimeMillis();
			return;
		}
		time = System.currentTimeMillis();
		Log.i(TAG, "当前位置:" + mCurrentListPosition);
		if (mCurrentListPosition < mCurrentLists.size() - 1) {
			mCurrentListPosition++;
		} else {
			mCurrentListPosition = 0;
		}
		pager.setCurrentItem(mCurrentListPosition);
		// sendEventToService(DMCConstants.MEDIA_DMC_CTL_MSG_PLAY, mCurrentLists
		// .get(mCurrentListPosition).getFilePath());
		// sendEventToService(DMCConstants.MEDIA_DMC_CTL_MSG_TOTALTIME, "");

	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onPageSelected(int arg0) {
		// TODO Auto-generated method stub
		Log.i(TAG, "onPageSelected==ID：" + arg0);
		mCurrentListPosition = arg0;
		if (isPlaying) {
			sendEventToService(DMCConstants.MEDIA_DMC_CTL_MSG_PLAY,
					mCurrentLists.get(mCurrentListPosition).getFilePath());
			isPlaying = false;
		}

	}

	private Handler bingHandler = new Handler(new Handler.Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			// TODO Auto-generated method stub

			pager.setCurrentItem(mCurrentListPosition);
			pushAdapter.notifyDataSetChanged();

			return false;
		}
	});

	private void getMtitle() {
		int length = mCurrentLists.size();
		title = new String[length];
		for (int i = 0; i < length; i++) {
			title[i] = mCurrentLists.get(i).getFileName();
		}
	}

    private void initFilter(){
        IntentFilter filter=new IntentFilter();
        filter.addAction(DMCConstants.Action.ACTION_PUSH_FILE_TV);
        registerReceiver(receiver,filter);
    }

    private BroadcastReceiver receiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action=intent.getAction();
            if (action.equals(DMCConstants.Action.ACTION_PUSH_FILE_TV)){
                sendEventToService(DMCConstants.MEDIA_DMC_CTL_MSG_PLAY,
                        mCurrentLists.get(mCurrentListPosition).getFilePath());
            }
        }
    };
}
