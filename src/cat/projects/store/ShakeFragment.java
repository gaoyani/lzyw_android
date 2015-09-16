package cat.projects.store;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import cat.projects.store.ShakeListener.OnShakeListener;

import com.huiwei.roomreservation.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.AbsoluteLayout;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 摇一�?
 * 
 * @author lyl
 * 
 */
@SuppressWarnings("deprecation")
public class ShakeFragment extends Fragment implements OnClickListener {

	private static final String TAG = "YaoYiYao";
	private LinearLayout linearLayout;
	// private SensorManager sensorManager;
	private Vibrator vibrator;
	private static long[] bing = new long[4];
	static {
		bing[0] = 100;
		bing[1] = 300;
		bing[2] = 100;
		bing[3] = 300;
	}

	private AlphaAnimation mAlphaAnimation;

	// private GridView yaoGridView;
	// private List<Map<String, Object>> list = new ArrayList<>();
	// private YaoAdapter yaoAdapter;
	private RelativeLayout yaoLayout;
	private ImageView yaopic;
	private int[] imgs = { R.drawable.img0, R.drawable.img1, R.drawable.img2,
			R.drawable.img3, R.drawable.img5, R.drawable.img6, R.drawable.img7,
			R.drawable.img8 };

	private List<Integer> colors = new ArrayList<Integer>();
	// private AbsoluteLayout mAbsoluteLayout;

	// private ImageView[] imageViews = new ImageView[5];

	private TextView notice;

	private int width = 0;
	private int height = 0;

	private List<Map<String, Integer>> postionList = new ArrayList<Map<String, Integer>>();
	// 大按键位�?
	private List<Map<String, Integer>> bigpostionList = new ArrayList<Map<String, Integer>>();
	// 小按键位�?
	private List<Map<String, Integer>> smallpostionList = new ArrayList<Map<String, Integer>>();
	private AbsoluteLayout tagLayout;
	private Button[] buttons = new Button[5];
	private ShakeListener shakeListener;
	// 加载多个动画
	// private AnimationSet mAnimationSet = new AnimationSet(true);
	private List<List<Map<String, Integer>>> mainList = new ArrayList<List<Map<String, Integer>>>();
	private List<Map<String, Integer>> tpostionList = new ArrayList<Map<String, Integer>>();
	private List<Map<String, Integer>> cpostionList = new ArrayList<Map<String, Integer>>();
	private List<Map<String, Integer>> bpostionList = new ArrayList<Map<String, Integer>>();
	private List<String> tagList = new ArrayList<String>();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.activity_yaoyiyao_, container,false);
		initView(view);
		initSenor();
		return view;
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		// linearLayout.setVisibility(View.INVISIBLE);
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		shakeListener.start();
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		shakeListener.stop();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		// sensorManager.unregisterListener(bingEventListener);
		shakeListener.stop();
	}

	private void initView(View view) {
		gettag();
		tagLayout = (AbsoluteLayout) view.findViewById(R.id.yao_tag_layout);
		 DisplayMetrics dm = new DisplayMetrics();
		 getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
		 width = dm.widthPixels;
		 height = dm.heightPixels;
		getPosition();

		buttons[0] = (Button) view.findViewById(R.id.button1);
		buttons[1] = (Button) view.findViewById(R.id.button2);
		buttons[2] = (Button) view.findViewById(R.id.button3);
		buttons[3] = (Button) view.findViewById(R.id.button4);
		buttons[4] = (Button) view.findViewById(R.id.button5);
		getNewPosition();
		for (int i = 0; i < 5; i++) {
			buttons[i].setOnClickListener(bingClickListener);
		}
		settag();
		linearLayout = (LinearLayout) view.findViewById(R.id.yao_my_line);
		yaoLayout = (RelativeLayout) view.findViewById(R.id.center_layout);
		yaopic = (ImageView) view.findViewById(R.id.yao_center_imag);

		mAlphaAnimation = new AlphaAnimation(0, 1.0f);
		mAlphaAnimation.setDuration(3000);

		view.findViewById(R.id.yao_notice_txt).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						// changePostion();
						// changenewPostion();

					}
				});

	}

	private void initSenor() {
		// sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		vibrator = (Vibrator) getActivity().getSystemService(getActivity().getApplicationContext().VIBRATOR_SERVICE);

		// sensorManager.registerListener(bingEventListener,
		// sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
		// SensorManager.SENSOR_DELAY_NORMAL);

		shakeListener = new ShakeListener(getActivity().getApplicationContext());
		shakeListener.setOnShake(mShakeListener);

	}

	@SuppressWarnings("unused")
	private void sendbrocast() {

		Intent intent = new Intent();
		intent.setAction(G.ACTION_YAOYIYAO);
		getActivity().sendBroadcast(intent);

	}

	public class BingAsyncTask extends AsyncTask<Integer, Integer, Void> {

		@Override
		protected Void doInBackground(Integer... params) {
			// TODO Auto-generated method stub
			try {
				Thread.sleep(5000);
				publishProgress(params);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			shakeListener.start();
			int index = (int) (Math.random() * imgs.length);
			yaopic.setImageResource(imgs[index]);
			linearLayout.setVisibility(View.GONE);
			yaoLayout.startAnimation(mAlphaAnimation);

		}

	};

//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		// TODO Auto-generated method stub
//
//		if (keyCode == KeyEvent.KEYCODE_BACK) {
//
//			if (yaoLayout.isShown()) {
//				yaoLayout.setVisibility(View.GONE);
//				linearLayout.setVisibility(View.GONE);
//				tagLayout.setVisibility(View.VISIBLE);
//				return true;
//			}
//
//		}
//
//		return super.onKeyDown(keyCode, event);
//	}

	private void getPosition() {
		Log.i("位置", "x:" + width + "y:" + height);
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {

				Map<String, Integer> map = new HashMap<String, Integer>();
				map.put("x", i * width / 3 + 10);
				map.put("y", j * height / 4 + 60);

				if (j == 0) {
					tpostionList.add(map);
				} else if (j == 1) {
					cpostionList.add(map);
				} else {
					bpostionList.add(map);
				}

				postionList.add(map);
			}

		}

		mainList.add(tpostionList);
		mainList.add(cpostionList);
		mainList.add(bpostionList);
		Log.i(TAG, "大小:" + mainList.get(2));

	}

	private void changePostion() {
		linearLayout.setVisibility(View.VISIBLE);
		Collections.shuffle(mainList);
		int m = new Random().nextInt(2);
		setPostion(mainList.get(0).get(m).get("x"),
				mainList.get(0).get(m).get("y"), buttons[0]);
		List<Map<String, Integer>> twoostionList = new ArrayList<Map<String, Integer>>();
		for (int i = 1; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				twoostionList.add(mainList.get(i).get(j));
			}

		}

		Collections.shuffle(twoostionList);
		for (int i = 1; i < 5; i++) {
			setPostion(twoostionList.get(i).get("x"),
					twoostionList.get(i).get("y"), buttons[i]);
		}

		// for (int i = 0; i < 5; i++) {
		// setPostion(postionList.get(i).get("x"),
		// postionList.get(i).get("y"), buttons[i]);
		// }
	}

	@SuppressWarnings("unused")
	@Deprecated
	private void changenewPostion() {
		linearLayout.setVisibility(View.VISIBLE);
		Collections.shuffle(bigpostionList);
		Log.i(TAG, "坐标:" + bigpostionList.size());
		setPostion(bigpostionList.get(0).get("x"),
				bigpostionList.get(0).get("y"), buttons[0]);
		Log.i(TAG, "大坐�?" + bigpostionList.get(0));

		setPostion(bigpostionList.get(1).get("x"),
				bigpostionList.get(1).get("y"), buttons[2]);
		Log.i(TAG, "大坐�?" + bigpostionList.get(1));
		setPostion(bigpostionList.get(2).get("x"),
				bigpostionList.get(2).get("y"), buttons[5]);
		Log.i(TAG, "大坐�?" + bigpostionList.get(2));

		Collections.shuffle(smallpostionList);
		setPostion(smallpostionList.get(0).get("x"), smallpostionList.get(0)
				.get("y"), buttons[1]);
		Log.i(TAG, "小坐�?" + smallpostionList.get(0));
		setPostion(smallpostionList.get(1).get("x"), smallpostionList.get(1)
				.get("y"), buttons[3]);
		Log.i(TAG, "小坐�?" + smallpostionList.get(1));
		setPostion(smallpostionList.get(2).get("x"), smallpostionList.get(2)
				.get("y"), buttons[4]);
		Log.i(TAG, "小坐�?" + smallpostionList.get(2));
		setPostion(smallpostionList.get(3).get("x"), smallpostionList.get(3)
				.get("y"), buttons[6]);
		Log.i(TAG, "小坐�?" + smallpostionList.get(3));

	}

	private void setPostion(final int x, final int y, final Button button) {
		TranslateAnimation translateAnimation2 = new TranslateAnimation(0, x
				- button.getLeft(), 0, y - button.getTop());
		translateAnimation2.setDuration(3000);
		AnimationSet animationSet = new AnimationSet(true);
		animationSet.addAnimation(translateAnimation2);
		animationSet.addAnimation(mAlphaAnimation);
		animationSet.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				button.layout(x, y, x + button.getWidth(),
						y + button.getHeight());
				shakeListener.start();
			}
		});
		// translateAnimation2.setAnimationListener(new AnimationListener() {
		//
		// @Override
		// public void onAnimationStart(Animation animation) {
		// // TODO Auto-generated method stub
		// // button.startAnimation(mAlphaAnimation);
		// }
		//
		// @Override
		// public void onAnimationRepeat(Animation animation) {
		// // TODO Auto-generated method stub
		//
		// }
		//
		// @Override
		// public void onAnimationEnd(Animation animation) {
		// // TODO Auto-generated method stub
		// button.layout(x, y, x + button.getWidth(), y + button.getHeight());
		//
		// }
		// });
		button.startAnimation(animationSet);

		linearLayout.setVisibility(View.INVISIBLE);
	}

	private OnClickListener bingClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			getActivity().startActivity(new Intent(getActivity().getApplicationContext(), Yao2yao.class));
		}
	};

	private OnShakeListener mShakeListener = new OnShakeListener() {

		@Override
		public void onShake() {
			// TODO Auto-generated method stub
			shakeListener.stop();
			vibrator.vibrate(bing, -1);
			if (tagLayout.isShown()) {
				// changenewPostion();
				settag();
				changePostion();
			} else {
				linearLayout.setVisibility(View.VISIBLE);
				yaoLayout.setVisibility(View.VISIBLE);
				yaopic.setImageResource(R.drawable.help);
				new BingAsyncTask().execute(0);
				Animation mAnimation = AnimationUtils.loadAnimation(
						getActivity().getApplicationContext(), R.anim.shake);
				yaoLayout.startAnimation(mAnimation);
			}

		}
	};

	/**
	 * 设置坐标
	 */
	private void getNewPosition() {

		Map<String, Integer> map = new HashMap<String, Integer>();
		map.put("x", (int) (width * 0.15));
		map.put("y", (int) (height * 0.103));
		bigpostionList.add(map);

		Map<String, Integer> map1 = new HashMap<String, Integer>();
		map1.put("x", (int) (width * 0.309));
		map1.put("y", (int) (height * 0.35));
		bigpostionList.add(map1);

		Map<String, Integer> map2 = new HashMap<String, Integer>();
		map2.put("x", (int) (width * 0.124));
		map2.put("y", (int) (height * 0.507));
		bigpostionList.add(map2);

		Map<String, Integer> map3 = new HashMap<String, Integer>();
		map3.put("x", (int) (width * 0.7));
		map3.put("y", height / 9);
		smallpostionList.add(map3);

		Map<String, Integer> map4 = new HashMap<String, Integer>();
		map4.put("x", (int) (width * 0.82));
		map4.put("y", (int) (height * 0.2));
		smallpostionList.add(map4);

		Map<String, Integer> map5 = new HashMap<String, Integer>();
		map5.put("x", (int) (width * 0.83));
		map5.put("y", (int) (height * 0.46));
		smallpostionList.add(map5);

		Map<String, Integer> map6 = new HashMap<String, Integer>();
		map6.put("x", (int) (width * 0.6));
		map6.put("y", (int) (height * 0.58));
		smallpostionList.add(map6);

	}

	private void gettag() {
		tagList.add("特色服务");
		tagList.add("品牌套房");
		tagList.add("推拿按摩");
		tagList.add("茶水");
		tagList.add("美食");
		tagList.add("糕点");
		colors.add(getResources().getColor(R.color.tag0));
		colors.add(getResources().getColor(R.color.tag1));
		colors.add(getResources().getColor(R.color.tag2));
		colors.add(getResources().getColor(R.color.tag3));
		colors.add(getResources().getColor(R.color.tag4));
		colors.add(getResources().getColor(R.color.tag5));
		colors.add(getResources().getColor(R.color.tag6));
		colors.add(getResources().getColor(R.color.tag7));
		colors.add(getResources().getColor(R.color.tag8));
		colors.add(getResources().getColor(R.color.tag9));

	}

	private void settag() {
		for (int i = 0; i < 5; i++) {
			buttons[i].setText(tagList.get(i));
			buttons[i].setBackgroundColor(colors.get(i));
		}

		Collections.shuffle(colors);
		Collections.shuffle(tagList);
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub

	}

}
