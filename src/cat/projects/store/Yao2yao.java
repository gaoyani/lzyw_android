package cat.projects.store;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cat.projects.store.YaoAdapter;
import cat.projects.store.ShakeListener.OnShakeListener;

import com.huiwei.roomreservation.R;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ListView;

public class Yao2yao extends Activity {

	private ListView mListView;
	private List<Map<String, Object>> list=new ArrayList<Map<String,Object>>();
	private YaoListadapter mListadapter;
	private boolean taskflag=false;
	private boolean scrollflag=false;
	private ShakeListener shakeListener;
	private Vibrator vibrator;
	private LinearLayout linearLayout;
	private int mm=0;
	
	private boolean ccccc=false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_newyao);
		initView();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		taskflag=true;
		scrollflag=true;
		shakeListener.stop();
	}
	
	private void initView(){
		shakeListener=new ShakeListener(Yao2yao.this);
		shakeListener.setOnShake(mShakeListener);
		vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
		mListView=(ListView)findViewById(R.id.yao_list_v);
		mListView.setScrollbarFadingEnabled(true);
		linearLayout = (LinearLayout) findViewById(R.id.yao_my_line);
		mListadapter=new YaoListadapter(list, this);
		mListView.setAdapter(mListadapter);
		for (int i = 0; i < mListadapter.imgs.length; i++) {
			Map<String, Object> map=new HashMap<String, Object>();
			list.add(map);
		}
		
		mListadapter.notifyDataSetChanged();
		new scrollAsyncTask().execute(0);
		
	}
	
	
	public class scrollAsyncTask extends AsyncTask<Integer, Integer, Void> {

		@Override
		protected Void doInBackground(Integer... params) {
			// TODO Auto-generated method stub
			int count=0;
			while (!taskflag) {
				try {
					Thread.sleep(40);
					if (scrollflag) {
						
						if (count==list.size()-1) {
							count=0;
						}else {
							count++;
						}
						if (ccccc) {
							mm++;
							
						}
						publishProgress(count);
					}
					
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			

		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
			Log.i("计时", "mm:"+mm);
			if (mm>100) {
				Log.i("计时", "mm:"+mm);
				shakeListener.start();
				linearLayout.setVisibility(View.GONE);
				scrollflag=false;
				mm=0;
				ccccc=false;
			}
			if (values[0]==0) {
			mListView.setSelection(0);	
			}else {
				mListView.smoothScrollToPosition(values[0]);
			}
			
			
			
		}

		
		
	};
	
	
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
		protected void onProgressUpdate(Integer... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
			shakeListener.start();
			linearLayout.setVisibility(View.GONE);
			scrollflag=false;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			shakeListener.start();
			linearLayout.setVisibility(View.GONE);
			scrollflag=false;

		}

	};
	
	public static long[] bing = new long[4];
private OnShakeListener mShakeListener=new OnShakeListener() {
		
		@Override
		public void onShake() {
			// TODO Auto-generated method stub
			shakeListener.stop();
			scrollflag=true;
			linearLayout.setVisibility(View.VISIBLE);
			vibrator.vibrate( bing, -1);
			ccccc=true;
			
		}
	};
	
}
