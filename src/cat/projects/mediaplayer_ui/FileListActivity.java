package cat.projects.mediaplayer_ui;

import java.io.Serializable;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import cat.projects.dmc_service.DMCMediaService;

import com.huiwei.commonlib.MD5;
import com.huiwei.roomreservation.R;
import com.luxs.common_ui.SettingConnectActivity;
import com.luxs.config.G;
import com.luxs.connect.ConnectManager;
import com.luxs.fileoperategroup.FileBean;
import com.luxs.fileoperategroup.FileLister;
import com.luxs.fileoperategroup.FileManager;
import com.luxs.fileoperategroup.FileSqliter;
import com.luxs.img.ImgManager;
import com.luxs.provider.FileProviderMateData.TableFileMateData;
import com.luxs.utils.Utils;

/**
 * 乐推送列表页
 */
public class FileListActivity extends Activity {
	Context context;
	Activity activity;

	TextView title;
	// 列表变量
	ListView listView;
	List<FileBean> fileData = new ArrayList<FileBean>();
	FileAdapter fileAdapter;
	View loadingView;

	FileManager fileManager = new FileManager();
	FileBean fileBean;
	boolean hasData = true;
	boolean isLoading = false;
	int currentPage = 1;

	ImgManager imgManager = new ImgManager();
	// 长按短按差别
	private boolean los = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_push_file_list);
		initUI();
		// FileBean tempBean = dataLists.get(1);
		// if (tempBean.getFileStatus() == G.COMMON) {
		// setCommon(tempBean);
		// }
	}

	List<FileBean> dataLists;

	private void initUI() {
		context = this;
		activity = this;

		title = (TextView) findViewById(R.id.title);

		// 初始化
		LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		loadingView = layoutInflater.inflate(R.layout.list_loading, null);

		// 绑定列表
		listView = (ListView) findViewById(R.id.listView);

		// 加载数据
		int type = getIntent().getIntExtra("type", 0);
		String path = getIntent().getStringExtra("path");
		String name = getIntent().getStringExtra("name");
		title.setText(name);
		String selection = "_data LIKE ?";
		String[] selectionArgs = new String[] { path + "%" };
		fileBean = new FileBean();
		fileBean.setActivity(activity);
		fileBean.setContext(context);
		fileBean.setFileType(type);
		fileBean.setSelection(selection);
		fileBean.setSelectionArgs(selectionArgs);
		fileBean.setOrderBy("date_added DESC");
		fileBean.setPageSize(G.PAGE_SIZE);
		loadData();

		// 绑定适配器
		fileAdapter = new FileAdapter(fileData);
		listView.setAdapter(fileAdapter);
		listView.setOnScrollListener(new FileListOnScrollListener());
		listView.setOnItemLongClickListener(new FileOnItemLongClickListener(
				fileData));
		listView.setOnItemClickListener(new FileListOnItemClickListener(
				fileData));

	}

	/**
	 * 加载数据
	 */
	private void loadData() {
		if (!isLoading) {
			fileBean.setCurrentPage(currentPage);
			fileManager.setIlister(new FileLister());
			listView.addFooterView(loadingView);
			new Thread(new loadDataThread()).start();
		}
	}

	/**
	 * 更新列表UI
	 */
	private Handler loadDataHandler = new Handler() {
		public void handleMessage(Message msg) {
			List<FileBean> tempLists = (List<FileBean>) msg.obj;
			Utils.log("LUXS列表：" + tempLists);
			switch (msg.what) {
			case G.LOAD_SUCCESS:
				fileData.addAll(tempLists);
				listView.removeFooterView(loadingView);
				fileAdapter.notifyDataSetChanged();
				currentPage++;
				break;
			case G.LOAD_FAILED:
				hasData = false;
				listView.removeFooterView(loadingView);
				break;
			default:
				break;
			}
			isLoading = false;
		};
	};

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
	 * 列表适配器
	 */
	private class FileAdapter extends BaseAdapter {
		List<FileBean> dataList;

		public FileAdapter(List<FileBean> dataList) {
			this.dataList = dataList;
		}

		@Override
		public int getCount() {
			if (dataList != null) {
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
					inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
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

			viewHolder.image.setTag(dataList.get(position).getFilePath());
			imgManager.setImgFromSDCord(dataList.get(position), listView,
					context);

			viewHolder.name.setText(dataList.get(position).getFileName());

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
	 * 长按监听器
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
				los = false;
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
	 * 设置、取消常用监听器
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
	 * 执行设置、取消常用
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
				if (!los) {
					Utils.toast(activity, getString(R.string.setting_success));
				}

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
				if (!los) {
					Utils.toast(activity, getString(R.string.setting_success));
				}

			} else
				Utils.toast(activity, getString(R.string.setting_failed));
		}

		fileAdapter.notifyDataSetChanged();
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
			if (hasData) {
				if (isLoading == false) {
					loadData();
				}
			}
		}

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {

		}

	}

	/**
	 * 列表点击监听器
	 */
	class FileListOnItemClickListener implements OnItemClickListener {
		List<FileBean> dataList;

		public FileListOnItemClickListener(List<FileBean> dataList) {
			this.dataList =  dataList;
		}

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position,
				long arg3) {

			los = true;
			try {
				if (!ConnectManager.isConnected() && DMCMediaService.mIsThirdPartyDlna == 0) {
					// start connect UI.
//					Intent intent = new Intent();
//					intent.putExtra("type", 0);
//					intent.setClass(FileListActivity.this,
//							SettingConnectActivity.class);
//					startActivity(intent);
//					return;
				}
			} catch (SocketException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			if (dataList.size() > 0 && position >= 0
					&& position < dataList.size()) {
				FileBean tempBean = dataList.get(position);
				Utils.log("getFileType:" + tempBean.getFileType());
				switch (tempBean.getFileType()) {
				case G.IMAGE:
					Utils.toast(activity, "图片：" + tempBean.getFileName() + "\n"
							+ "路径：" + tempBean.getFilePath());

					Intent intentImg = new Intent();
					intentImg.putExtra("type", tempBean.getFileType());
					intentImg.putExtra("path", tempBean.getFilePath());
					intentImg.putExtra("name", tempBean.getFileName());
//					Bundle mBundle = new Bundle();
//					mBundle.putSerializable("dataList", dataList);
//					intentImg.putExtras(mBundle);
                    intentImg.putExtra("dataList", (Serializable)dataList);

					// intentImg.putExtra("dataList", (Serializable) dataList);
					intentImg.putExtra("position", position);
					intentImg.setClass(context, DMCPlayerActivity.class);
					intentImg.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					try {
						startActivity(intentImg);
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}

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
					try {
						startActivity(intentMusic);
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}

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
					intentVideo.setClass(context, DMCPlayerActivity.class);
					intentVideo.putExtra("position", position);
					try {
						startActivity(intentVideo);
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}

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

	/**
	 * 按钮点击事件
	 * 
	 * @param view
	 */
	public void doClick(View view) {
		switch (view.getId()) {
		case R.id.btnBack:
			activity.finish();
			break;
		default:
			break;
		}
	}
}
