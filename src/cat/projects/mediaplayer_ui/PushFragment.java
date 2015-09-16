package cat.projects.mediaplayer_ui;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.huiwei.roomreservation.R;
import com.huiwei.roomreservation.activity.StoreSceneActivity;
import com.luxs.common_ui.SearchFileActivity;
import com.luxs.config.G;
import com.luxs.fileoperategroup.CloudLister;
import com.luxs.fileoperategroup.CommonLister;
import com.luxs.fileoperategroup.FileBean;
import com.luxs.fileoperategroup.FileManager;
import com.luxs.fileoperategroup.FileSqliter;
import com.luxs.fileoperategroup.FolderLister;
import com.luxs.fragmentutil.FragmentTouchListener;
import com.luxs.img.ImgManager;
import com.luxs.provider.FileProviderMateData.TableFileMateData;
import com.luxs.setting.SettingManager;
import com.luxs.utils.Utils;

import dalvik.system.VMRuntime;

/**
 * 推送页面
 */
public class PushFragment extends Fragment implements OnGestureListener,
		FragmentTouchListener, OnClickListener {

	private static final String TAG = "PushActivity";
	Context context;
	Activity activity;

	// 滑动变量
	GestureDetector detector;
	ViewFlipper flipper;

	// 列表变量
	View commonView;
	View imageView;
	View musicView;
	View videoView;
	View cloudView;
	ListView commonListView;
	ListView imageListView;
	ListView musicListView;
	ListView videoListView;
	ListView cloudListView;
	List<FileBean> commonData = new ArrayList<FileBean>();
	List<FileBean> imageData = new ArrayList<FileBean>();
	List<FileBean> musicData = new ArrayList<FileBean>();
	List<FileBean> videoData = new ArrayList<FileBean>();
	List<FileBean> cloudData = new ArrayList<FileBean>();
	FileAdapter commonAdapter;
	FileAdapter imageAdapter;
	FileAdapter musicAdapter;
	FileAdapter videoAdapter;
	FileAdapter cloudAdapter;
	View loadingView;
	int currentView = G.COMMON;
	ImageView tabCurrent;
	Animation currentFlagAnim;
	float currentX = 0;
	float commonX = 0;
	float imageX = 0;
	float musicX = 0;
	float videoX = 0;
	float cloudX = 0;

	// 数据变量
	FileManager fileManager = new FileManager();
	FileBean fileBean;
	boolean hasCommonData = true;
	boolean hasImageData = true;
	boolean hasMusicData = true;
	boolean hasVideoData = true;
	boolean hasCloudData = true;
	boolean isLoading = false;

	// 当前分页变量
	int currentCommonPage = 1;
	int currentImagePage = 1;
	int currentMusicPage = 1;
	int currentVideoPage = 1;
	int currentCloudPage = 1;

	ImgManager imgManager = new ImgManager();
	SettingManager settingManager = new SettingManager();
	int defaultCount;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.activity_push, container, false);

		initUI(view);

		final int HEAP_SIZE = 8 * 1024 * 1024;
		VMRuntime.getRuntime().setMinimumHeapSize(HEAP_SIZE);
		((StoreSceneActivity) getActivity()).setFragmentTouchListener(this);
		registreceiver();
		return view;
	}

	private void initUI(View view) {
		context = getActivity();
		activity = getActivity();

		// 实例化手势
		detector = new GestureDetector(this);

		// 设置标签条宽度
		Display mDisplay = getActivity().getWindowManager().getDefaultDisplay();
		int width = mDisplay.getWidth();
		tabCurrent = (ImageView) view.findViewById(R.id.tabCurrent);
		LayoutParams params = (LayoutParams) tabCurrent.getLayoutParams();
		float distance = width / 5;
		params.width = (int) (distance);
		tabCurrent.setLayoutParams(params);

		// 设置每个标签条的X坐标位置
		imageX = distance * 1;
		musicX = distance * 2;
		videoX = distance * 3;
		cloudX = distance * 4;

		((Button) view.findViewById(R.id.tabCommon)).setOnClickListener(this);
		((Button) view.findViewById(R.id.tabImage)).setOnClickListener(this);
		((Button) view.findViewById(R.id.tabMusic)).setOnClickListener(this);
		((Button) view.findViewById(R.id.tabVideo)).setOnClickListener(this);
		((Button) view.findViewById(R.id.tabCloud)).setOnClickListener(this);

		// 初始化滑动页面
		LayoutInflater layoutInflater = (LayoutInflater) getActivity()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		commonView = layoutInflater.inflate(R.layout.file_list, null);
		imageView = layoutInflater.inflate(R.layout.file_list, null);
		musicView = layoutInflater.inflate(R.layout.file_list, null);
		videoView = layoutInflater.inflate(R.layout.file_list, null);
		cloudView = layoutInflater.inflate(R.layout.file_list, null);
		loadingView = layoutInflater.inflate(R.layout.list_loading, null);
		commonView.setId(G.COMMON);
		imageView.setId(G.IMAGE);
		musicView.setId(G.MUSIC);
		videoView.setId(G.VIDEO);
		cloudView.setId(G.CLOUD);

		// 将滑动页面加入滑动框里
		flipper = (ViewFlipper) view.findViewById(R.id.flipper);
		flipper.addView(commonView);
		flipper.addView(imageView);
		flipper.addView(musicView);
		flipper.addView(videoView);
		flipper.addView(cloudView);

		// 绑定列表
		commonListView = (ListView) commonView.findViewById(R.id.listView);
		imageListView = (ListView) imageView.findViewById(R.id.listView);
		musicListView = (ListView) musicView.findViewById(R.id.listView);
		videoListView = (ListView) videoView.findViewById(R.id.listView);
		cloudListView = (ListView) cloudView.findViewById(R.id.listView);

		// 加载数据
		fileBean = new FileBean();
		fileBean.setActivity(activity);
		fileBean.setContext(context);
		fileBean.setOrderBy("date_added DESC");

		// 绑定适配器
		commonAdapter = new FileAdapter(commonData, G.COMMON);
		commonListView.addFooterView(loadingView);
		commonListView.setAdapter(commonAdapter);
		// commonListView.setOnScrollListener(new FileListOnScrollListener());
		commonListView
				.setOnItemLongClickListener(new FileOnItemLongClickListener(
						commonData));
		commonListView.setOnItemClickListener(new FileListOnItemClickListener(
				commonData));
		loadData();

		imageListView.addFooterView(loadingView);
		imageAdapter = new FileAdapter(imageData, G.IMAGE);
		imageListView.setAdapter(imageAdapter);
		imageListView.setOnScrollListener(new FileListOnScrollListener());
		imageListView
				.setOnItemLongClickListener(new FileOnItemLongClickListener(
						imageData));
		imageListView.setOnItemClickListener(new FileListOnItemClickListener(
				imageData));

		musicListView.addFooterView(loadingView);
		musicAdapter = new FileAdapter(musicData, G.MUSIC);
		musicListView.setAdapter(musicAdapter);
		musicListView.setOnScrollListener(new FileListOnScrollListener());
		musicListView
				.setOnItemLongClickListener(new FileOnItemLongClickListener(
						musicData));
		musicListView.setOnItemClickListener(new FileListOnItemClickListener(
				musicData));

		videoListView.addFooterView(loadingView);
		videoAdapter = new FileAdapter(videoData, G.VIDEO);
		videoListView.setAdapter(videoAdapter);
		videoListView.setOnScrollListener(new FileListOnScrollListener());
		videoListView
				.setOnItemLongClickListener(new FileOnItemLongClickListener(
						videoData));
		videoListView.setOnItemClickListener(new FileListOnItemClickListener(
				videoData));

		cloudListView.addFooterView(loadingView);
		cloudAdapter = new FileAdapter(cloudData, G.CLOUD);
		cloudListView.setAdapter(cloudAdapter);
		cloudListView.setOnScrollListener(new FileListOnScrollListener());
		cloudListView
				.setOnItemLongClickListener(new FileOnItemLongClickListener(
						cloudData));
		cloudListView.setOnItemClickListener(new FileListOnItemClickListener(
				cloudData));
	}

	/*
	 * @Override protected void onResume() {
	 * commonAdapter.notifyDataSetChanged(); super.onResume(); }
	 * 
	 * @Override protected void onDestroy() { // TODO Auto-generated method stub
	 * super.onDestroy(); unregisterReceiver(yaoReceiver); }
	 */

	/**
	 * 加载数据
	 */
	boolean isNull;
	private void loadData() {
		if (!isLoading) {
			loadingView.setVisibility(View.VISIBLE);
			switch (currentView) {
			case G.COMMON:
				Log.i(TAG, "getCount="+commonListView.getAdapter().getCount());
				int n=commonListView.getAdapter().getCount();
				fileManager.setSqliter(new FileSqliter());
				if (fileManager.query(fileBean)==null) {
					isNull=true;
					fileBean.setCurrentPage(currentCommonPage);
					fileBean.setPageSize(settingManager
							.getPushCommonCount(activity));
					fileBean.setFileType(G.IMAGE);
					fileManager.setIlister(new FolderLister());
                    
				} else {

					fileBean.setCurrentPage(currentCommonPage);
					fileBean.setPageSize(settingManager
							.getPushCommonCount(activity));
					fileManager.setIlister(new CommonLister());
				}
				break;
			case G.IMAGE:
				fileBean.setCurrentPage(currentImagePage);
				fileBean.setPageSize(G.PAGE_SIZE);
				fileBean.setFileType(G.IMAGE);
				fileManager.setIlister(new FolderLister());
				break;
			case G.MUSIC:
				fileBean.setCurrentPage(currentMusicPage);
				fileBean.setPageSize(G.PAGE_SIZE);
				fileBean.setFileType(G.MUSIC);
				fileManager.setIlister(new FolderLister());
				break;
			case G.VIDEO:
				fileBean.setCurrentPage(currentVideoPage);
				fileBean.setPageSize(G.PAGE_SIZE);
				fileBean.setFileType(G.VIDEO);
				fileManager.setIlister(new FolderLister());
				break;
			case G.CLOUD:
				fileBean.setCurrentPage(currentCloudPage);
				fileBean.setPageSize(G.PAGE_SIZE);
				fileBean.setFileType(G.CLOUD);
				fileManager.setIlister(new CloudLister());
				break;
			default:
				break;
			}
			new Thread(new loadDataThread()).start();

		}
	}

	/**
	 * 加载数据线程
	 */
	private class loadDataThread extends Thread {
		@Override
		public void run() {
			try {
				isLoading = true;
				List<FileBean> tempLists = (List<FileBean>) fileManager
						.getList(fileBean);
				Utils.log("LUXS:返回数据:" + tempLists);
				if (tempLists != null) {
					Message msg = loadDataHandler.obtainMessage();
					msg.arg1 = fileBean.getFileType();
					msg.what = G.LOAD_SUCCESS;
					msg.obj = tempLists;
					msg.sendToTarget();
				} else {
					Message msg = loadDataHandler.obtainMessage();
					msg.arg1 = fileBean.getFileType();
					msg.what = G.LOAD_FAILED;
					msg.sendToTarget();
				}
			} catch (Exception e) {
				Message msg = loadDataHandler.obtainMessage();
				msg.what = G.LOAD_FAILED;
				msg.sendToTarget();
				e.printStackTrace();
			}
		}
	}

	/**
	 * 更新列表UI
	 */
	private Handler loadDataHandler = new Handler() {
		public void handleMessage(Message msg) {
			List<FileBean> tempLists = (List<FileBean>) msg.obj;
			Utils.log("LUXS列表：" + tempLists);
			loadingView.setVisibility(View.GONE);
			switch (msg.what) {
			case G.LOAD_SUCCESS:

				switch (msg.arg1) {
				case G.COMMON:
					commonData.clear();
					commonAdapter.notifyDataSetChanged();
					commonData.addAll(tempLists);
					commonAdapter.notifyDataSetChanged();
					break;
				case G.IMAGE:
					if(isNull){
						commonData.clear();
						commonAdapter.notifyDataSetChanged();
						commonData.addAll(tempLists);
						commonAdapter.notifyDataSetChanged();
						isNull=false;
					}else {
						imageData.addAll(tempLists);
						imageAdapter.notifyDataSetChanged();
						currentImagePage++;
					}
					break;
				case G.MUSIC:
					musicData.addAll(tempLists);
					musicAdapter.notifyDataSetChanged();
					currentMusicPage++;
					break;
				case G.VIDEO:
					videoData.addAll(tempLists);
					videoAdapter.notifyDataSetChanged();
					currentVideoPage++;
					break;
				case G.CLOUD:
					cloudData.addAll(tempLists);
					cloudAdapter.notifyDataSetChanged();
					currentCloudPage++;
					break;
				default:
					break;
				}

				break;
			case G.LOAD_FAILED:
				switch (msg.arg1) {
				case G.COMMON:
					commonData.clear();
					commonAdapter.notifyDataSetChanged();
					hasCommonData = false;
					break;
				case G.IMAGE:
					hasImageData = false;
					break;
				case G.MUSIC:
					hasMusicData = false;
					break;
				case G.VIDEO:
					hasVideoData = false;
					break;
				case G.CLOUD:
					hasCloudData = false;
					break;
				default:
					break;
				}
				break;
			default:
				break;
			}
			isLoading = false;
		};
	};

	// 适配器
	private class FileAdapter extends BaseAdapter {
		List<FileBean> dataList;
		int type;

		public FileAdapter(List<FileBean> dataList, int type) {
			this.dataList = dataList;
			this.type = type;
		}

		@Override
		public int getCount() {
			if (dataList != null) {
				if (currentView == G.COMMON
						&& dataList.size() > settingManager
								.getPushCommonCount(activity))
					return settingManager.getPushCommonCount(activity);
				return dataList.size();
			} else {
				return 0;
			}
		}

		@Override
		public Object getItem(int position) {
			return dataList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		LayoutInflater inflater;

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder;
			if (convertView == null) {
				if (inflater == null) {
					inflater = (LayoutInflater) getActivity().getSystemService(
							Context.LAYOUT_INFLATER_SERVICE);
				}
				viewHolder = new ViewHolder();
				convertView = inflater.inflate(R.layout.file_list_item, null);
				viewHolder.image = (ImageView) convertView
						.findViewById(R.id.image);
				viewHolder.name = (TextView) convertView
						.findViewById(R.id.name);
				viewHolder.type = (ImageView) convertView
						.findViewById(R.id.type);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}

			// 装载图片
			viewHolder.image.setTag(dataList.get(position).getFilePath());
			switch (type) {
			case G.COMMON:
				imgManager.setImgFromSDCord(dataList.get(position),
						commonListView, context);
				break;
			case G.IMAGE:
				imgManager.setImgFromSDCord(dataList.get(position),
						imageListView, context);
				break;
			case G.MUSIC:
				imgManager.setImgFromSDCord(dataList.get(position),
						musicListView, context);
				break;
			case G.VIDEO:
				imgManager.setImgFromSDCord(dataList.get(position),
						videoListView, context);
				break;
			case G.CLOUD:
				imgManager.setImgFromSDCord(dataList.get(position),
						cloudListView, context);
				break;
			default:
				break;
			}

			// 设置文件名称
			viewHolder.name.setText(dataList.get(position).getFileName());

			// 设置文件类型
			switch (dataList.get(position).getFileType()) {
			case G.IMAGE:
				viewHolder.type.setVisibility(View.GONE);
				break;
			case G.MUSIC:
				viewHolder.type
						.setBackgroundResource(R.drawable.icon_file_music);
				viewHolder.type.setVisibility(View.VISIBLE);
				break;
			case G.VIDEO:
				viewHolder.type
						.setBackgroundResource(R.drawable.icon_file_video);
				viewHolder.type.setVisibility(View.VISIBLE);
				break;
			case G.FOLDER:
				viewHolder.type.setBackgroundResource(R.drawable.icon_folder);
				viewHolder.type.setVisibility(View.VISIBLE);
				break;
			}
			return convertView;
		}

	}

	class ViewHolder {
		ImageView image;
		TextView name;
		ImageView type;
	}

	/**
	 * 列表滚动监听器
	 */
	class FileListOnScrollListener implements OnScrollListener {

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
			int lastVisibleItem = firstVisibleItem + visibleItemCount;
			if (lastVisibleItem == 0 || lastVisibleItem < totalItemCount
					|| totalItemCount < G.PAGE_SIZE) {
				return;
			}
			if (isLoading == false) {
				switch (currentView) {
				case G.IMAGE:
					if (hasImageData) {
						loadData();
						imageAdapter.notifyDataSetChanged();
					}
					break;
				case G.MUSIC:
					if (hasMusicData) {
						loadData();
						musicAdapter.notifyDataSetChanged();
					}
					break;
				case G.VIDEO:
					if (hasVideoData) {
						loadData();
						videoAdapter.notifyDataSetChanged();
					}
					break;
				case G.CLOUD:
					if (hasCloudData) {
						loadData();
						cloudAdapter.notifyDataSetChanged();
					}
					break;
				default:
					break;
				}

			}
		}

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {

		}

	}

	/**
	 * 列表长按监听器
	 */
	class FileOnItemLongClickListener implements OnItemLongClickListener {
		List<FileBean> dataList;

		public FileOnItemLongClickListener(List<FileBean> dataList) {
			this.dataList = dataList;
		}

		@Override
		public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
				int position, long arg3) {
			if (dataList.size() > 0 && position >= 0
					&& position < dataList.size()) {
				FileBean fileBean = dataList.get(position);
				if (fileBean.getFileType() != G.FOLDER)
					setCommonAlert(fileBean);
			}
			return false;
		}

	}

	/**
	 * 设置、取消常用弹出框
	 * 
	 * @param fileBean
	 */
	private void setCommonAlert(FileBean fileBean) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		if (fileBean.getFileStatus() != G.COMMON)
			builder.setTitle(R.string.set_common);
		else
			builder.setTitle(R.string.cancel_common);
		SetCommonListener pl = new SetCommonListener(fileBean);
		builder.setPositiveButton(getString(R.string.ok), pl);
		builder.setNegativeButton(getString(R.string.cancel), pl);
		AlertDialog alertDialog = builder.create();
		alertDialog.show();
	}

	/**
	 * 设置/取消常用监听器
	 */
	public class SetCommonListener implements DialogInterface.OnClickListener {

		FileBean fileBean;

		public SetCommonListener(FileBean fileBean) {
			this.fileBean = fileBean;
		}

		@Override
		public void onClick(DialogInterface v, int btnId) {
			if (btnId == DialogInterface.BUTTON_POSITIVE) {
				setCommon(fileBean);
			}
		}

	}

	/**
	 * 执行设置/取消常用
	 * 
	 * @param fileBean
	 */
	private void setCommon(FileBean fileBean) {
		fileManager.setSqliter(new FileSqliter());
		fileBean.setContext(context);
		fileBean.setActivity(activity);
		if (fileBean.getFileStatus() != G.COMMON) {
			fileBean.setSelection(TableFileMateData.PATH + " = ?");
			fileBean.setSelectionArgs(new String[] { fileBean.getFilePath() });
			int hasData = fileManager.getLocalCount(fileBean);
			if (hasData > 0) {
				Utils.toast(activity, getString(R.string.isacommon));
				return;
			}
			fileBean.setFileStatus(G.COMMON);
			long result = fileManager.insert(fileBean);
			if (result != G.SQL_ERROR) {
				fileBean.setFileStatus(G.COMMON);
				Utils.toast(activity, getString(R.string.setting_success));
			} else {
				fileBean.setFileStatus(G.NOT_COMMON);
				Utils.toast(activity, getString(R.string.setting_failed));
			}
		} else {
			fileBean.setSelection(TableFileMateData.PATH + " = ?");
			fileBean.setSelectionArgs(new String[] { fileBean.getFilePath() });
			int result = fileManager.delete(fileBean);
			if (result != G.SQL_ERROR) {
				fileBean.setFileStatus(G.NOT_COMMON);
				Utils.toast(activity, getString(R.string.setting_success));
			} else
				Utils.toast(activity, getString(R.string.setting_failed));
		}

		switch (currentView) {
		case G.COMMON:
			loadData();
			break;
		case G.IMAGE:
			imageAdapter.notifyDataSetChanged();
			break;
		case G.MUSIC:
			musicAdapter.notifyDataSetChanged();
			break;
		case G.VIDEO:
			videoAdapter.notifyDataSetChanged();
			break;
		case G.CLOUD:
			cloudAdapter.notifyDataSetChanged();
			break;
		default:
			break;
		}
	}

	/**
	 * 列表点击监听器
	 */
	class FileListOnItemClickListener implements OnItemClickListener {
		List<FileBean> dataList;

		public FileListOnItemClickListener(List<FileBean> dataList) {
			this.dataList = dataList;
		}

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position,
				long arg3) {
			if (dataList.size() > 0 && position >= 0
					&& position < dataList.size()) {

				FileBean tempBean = dataList.get(position);
				if (null == tempBean) {
					Toast.makeText(context, "写入数据有误", Toast.LENGTH_LONG).show();
					return;
				}
				Utils.log("getFileType:" + tempBean.getFileType());
				switch (tempBean.getFileType()) {
				case G.IMAGE:
					Utils.toast(activity, "图片：" + tempBean.getFileName() + "\n"
							+ "路径：" + tempBean.getFilePath());

					Intent intentImg = new Intent();
					intentImg.putExtra("type", tempBean.getFileType());
					intentImg.putExtra("path", tempBean.getFilePath());
					intentImg.putExtra("name", tempBean.getFileName());
					intentImg.putExtra("dataList", (Serializable) dataList);
					intentImg.putExtra("position", position);
					intentImg.setClass(context, DMCPlayerActivity.class);
					startActivity(intentImg);
					if (tempBean.getFileStatus() != G.COMMON) {
						setCommon(tempBean);
					}

					break;
				case G.MUSIC:
					Utils.toast(activity, "音乐：" + tempBean.getFileName() + "\n"
							+ "路径：" + tempBean.getFilePath());

					Intent intentMusic = new Intent();
					intentMusic.putExtra("type", tempBean.getFileType());
					intentMusic.putExtra("path", tempBean.getFilePath());
					intentMusic.putExtra("name", tempBean.getFileName());
					intentMusic.putExtra("dataList", (Serializable) dataList);
					intentMusic.putExtra("position", position);
					intentMusic.setClass(context, DMCPlayerActivity.class);
					startActivity(intentMusic);
					if (tempBean.getFileStatus() != G.COMMON) {
						setCommon(tempBean);
					}
					break;
				case G.VIDEO:
					Utils.toast(activity, "视频：" + tempBean.getFileName() + "\n"
							+ "路径：" + tempBean.getFilePath());

					Intent intentVideo = new Intent();
					intentVideo.putExtra("type", tempBean.getFileType());
					intentVideo.putExtra("path", tempBean.getFilePath());
					intentVideo.putExtra("name", tempBean.getFileName());
					intentVideo.putExtra("dataList", (Serializable) dataList);
					intentVideo.putExtra("position", position);
					intentVideo.setClass(context, DMCPlayerActivity.class);
					startActivity(intentVideo);
					if (tempBean.getFileStatus() != G.COMMON) {
						setCommon(tempBean);
					}
					break;
				case G.CLOUD:
					Utils.toast(activity, "云资源：" + tempBean.getFileName()
							+ "\n" + "路径：" + tempBean.getFilePath());
					break;
				case G.FOLDER:
					Intent intent = new Intent();
					intent.putExtra("type", tempBean.getFolderType());
					intent.putExtra("path", tempBean.getFilePath());
					intent.putExtra("name", tempBean.getFileName());
					intent.setClass(context, FileListActivity.class);
					startActivity(intent);
					Utils.log("FOLDER");
					break;

				default:
					break;
				}
			}
		}

	}

	// onTouch need to do more thing.
	/*
	 * @Override public boolean onTouchEvent(MotionEvent event) {
	 * super.onTouchEvent(event); return this.detector.onTouchEvent(event); }
	 * 
	 * @Override public boolean dispatchTouchEvent(MotionEvent ev) {
	 * detector.onTouchEvent(ev); super.dispatchTouchEvent(ev); return true; }
	 */

	@Override
	public boolean onDown(MotionEvent arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * 左右滑动
	 */
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		if (e1.getX() > (e2.getX() + 200)) {
			this.flipper.setInAnimation(AnimationUtils.loadAnimation(context,
					R.anim.left_in));
			this.flipper.setOutAnimation(AnimationUtils.loadAnimation(context,
					R.anim.left_out));
			View view = this.flipper.getCurrentView();
			switch (view.getId()) {
			case G.COMMON:
				currentView = G.IMAGE;
				anim(tabCurrent, G.IMAGE, currentX, imageX, 0f, 0f);
				break;
			case G.IMAGE:
				currentView = G.MUSIC;
				anim(tabCurrent, G.MUSIC, currentX, musicX, 0f, 0f);
				break;
			case G.MUSIC:
				currentView = G.VIDEO;
				anim(tabCurrent, G.VIDEO, currentX, videoX, 0f, 0f);
				break;
			case G.VIDEO:
				currentView = G.CLOUD;
				anim(tabCurrent, G.CLOUD, currentX, cloudX, 0f, 0f);
				break;
			case G.CLOUD:
				currentView = G.COMMON;
				anim(tabCurrent, G.COMMON, currentX, commonX, 0f, 0f);
				break;
			default:
				break;
			}
			this.flipper.showNext();
			return true;
		} else if (e1.getX() < (e2.getX() - 200)) {
			this.flipper.setInAnimation(AnimationUtils.loadAnimation(context,
					R.anim.right_in));
			this.flipper.setOutAnimation(AnimationUtils.loadAnimation(context,
					R.anim.right_out));
			View view = this.flipper.getCurrentView();
			switch (view.getId()) {
			case G.COMMON:
				currentView = G.CLOUD;
				anim(tabCurrent, G.CLOUD, currentX, cloudX, 0f, 0f);
				break;
			case G.IMAGE:
				currentView = G.COMMON;
				anim(tabCurrent, G.COMMON, currentX, commonX, 0f, 0f);
				break;
			case G.MUSIC:
				currentView = G.IMAGE;
				anim(tabCurrent, G.IMAGE, currentX, imageX, 0f, 0f);
				break;
			case G.VIDEO:
				currentView = G.MUSIC;
				anim(tabCurrent, G.MUSIC, currentX, musicX, 0f, 0f);
				break;
			case G.CLOUD:
				currentView = G.VIDEO;
				anim(tabCurrent, G.VIDEO, currentX, videoX, 0f, 0f);
				break;
			default:
				break;
			}
			this.flipper.showPrevious();
			return true;
		}
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * 左右滑动动画
	 * 
	 * @param currentView
	 * @param toView
	 */
	private void flipperAnim(int currentView, int toView) {
		if (currentView < toView && toView < 5) {
			this.flipper.setInAnimation(AnimationUtils.loadAnimation(context,
					R.anim.left_in));
			this.flipper.setOutAnimation(AnimationUtils.loadAnimation(context,
					R.anim.left_out));
		} else {
			this.flipper.setInAnimation(AnimationUtils.loadAnimation(context,
					R.anim.right_in));
			this.flipper.setOutAnimation(AnimationUtils.loadAnimation(context,
					R.anim.right_out));
		}
		flipper.setDisplayedChild(toView);
	}

	/**
	 * 当前标签条动画
	 * 
	 * @param view
	 * @param currentTab
	 * @param x1
	 * @param x2
	 * @param y1
	 * @param y2
	 */
	private void anim(View view, final int currentTab, float x1, float x2,
			float y1, float y2) {
		Animation translateAnimation = new TranslateAnimation(x1, x2, y1, y2);
		translateAnimation.setDuration(500);
		translateAnimation.setFillAfter(true);
		translateAnimation.setAnimationListener(new AnimationListener() {

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
				switch (currentTab) {
				case G.COMMON:
					loadData();
					break;
				case G.IMAGE:
					if (imageData.size() < 1)
						loadData();
					break;
				case G.MUSIC:
					if (musicData.size() < 1)
						loadData();
					break;
				case G.VIDEO:
					if (videoData.size() < 1)
						loadData();
					break;
				case G.CLOUD:
					if (cloudData.size() < 1)
						loadData();
					break;
				}
			}
		});
		view.startAnimation(translateAnimation);
		currentX = x2;
	}

	/**
	 * 按钮点击事件
	 * 
	 * @param view
	 */
	public void doClick(View view) {
		int tempView = currentView;
		switch (view.getId()) {
		case R.id.tabCommon:
			currentView = G.COMMON;
			anim(tabCurrent, G.COMMON, currentX, commonX, 0f, 0f);
			flipperAnim(tempView, G.COMMON);
			break;
		case R.id.tabImage:
			currentView = G.IMAGE;
			anim(tabCurrent, G.IMAGE, currentX, imageX, 0f, 0f);
			flipperAnim(tempView, G.IMAGE);
			break;
		case R.id.tabMusic:
			currentView = G.MUSIC;
			anim(tabCurrent, G.MUSIC, currentX, musicX, 0f, 0f);
			flipperAnim(tempView, G.MUSIC);
			break;
		case R.id.tabVideo:
			currentView = G.VIDEO;
			anim(tabCurrent, G.VIDEO, currentX, videoX, 0f, 0f);
			flipperAnim(tempView, G.VIDEO);
			break;
		case R.id.tabCloud:
			currentView = G.CLOUD;
			anim(tabCurrent, G.CLOUD, currentX, cloudX, 0f, 0f);
			flipperAnim(tempView, G.CLOUD);
			break;
		case R.id.btnSearch:
			Utils.toActivity(activity, SearchFileActivity.class);
			break;

		default:
			break;
		}
	}

	private BroadcastReceiver yaoReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			Random random = new Random();
			// sec(random.nextInt(3));
			pushthis(sec(random.nextInt(3)), random);

		}
	};

	private List<FileBean> sec(int type) {
		List<FileBean> list = new ArrayList<FileBean>();
		Log.i(TAG, "类型:" + type);
		switch (type) {
		case 0:
			list = imageData;
			break;
		case 1:
			list = musicData;
			break;
		case 2:
			list = videoData;
			break;

		default:
			break;
		}

		return list;
	}

	private void pushthis(List<FileBean> list, Random random) {

		int dex = (int) (Math.random() * (list.size() - 1));
		if (list.size() == 0) {
			return;
		}
		Log.i(TAG, "dex:" + dex);
		FileBean tempBean = list.get(dex);
		Intent intent = new Intent();
		intent.putExtra("type", tempBean.getFileType());
		intent.putExtra("path", tempBean.getFilePath());
		intent.putExtra("name", tempBean.getFileName());
		intent.putExtra("dataList", (Serializable) list);
		intent.putExtra("position", dex);
		intent.setClass(context, DMCPlayerActivity.class);
		startActivity(intent);

	}

	/**
	 * 注册摇一摇广播
	 */
	private void registreceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(G.ACTION_YAOYIYAO);
		// registerReceiver(yaoReceiver, filter);

	}

	@Override
	public void onFragmentTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		detector.onTouchEvent(event);
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		doClick(view);
	}

}
