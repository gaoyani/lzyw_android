package com.luxs.common_ui;

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
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.huiwei.roomreservation.R;
import com.luxs.config.G;
import com.luxs.fileoperategroup.FileBean;
import com.luxs.fileoperategroup.FileLister;
import com.luxs.fileoperategroup.FileManager;
import com.luxs.fileoperategroup.FileSqliter;
import com.luxs.img.ImgManager;
import com.luxs.provider.FileProviderMateData.TableFileMateData;
import com.luxs.utils.Utils;

public class SearchFileActivity  extends Activity{
	Context context;
	Activity activity;

	EditText keywordsEdit;
	ImageButton btnSearch;
	ImageView tabCurrent;
	float distance;
	
	//列表变量
	ListView listView;
	List<FileBean> fileData = new ArrayList<FileBean>();
	FileAdapter fileAdapter;
	View loadingView;
	
	FileManager fileManager = new FileManager();
	FileBean fileBean;
	boolean hasData = true;
	boolean isLoading = false;
	int currentPage = 1;
	int currentView = G.IMAGE;
	
	float currentX = 0;
	float imageX = 0;
	float musicX = 0;
	float videoX = 0;

	ImgManager imgManager = new ImgManager();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_push_search);
		initUI();
	}
	
	private void initUI(){
    	context = this;
		activity = this;
		btnSearch = (ImageButton)findViewById(R.id.btnSearch);
		keywordsEdit = (EditText)findViewById(R.id.keywords);
		keywordsEdit.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				String keywordsStr = keywordsEdit.getText().toString();
				if("".equals(keywordsStr)) {
					btnSearch.setBackgroundResource(R.drawable.btn_search_disabled);
				}else{
					btnSearch.setBackgroundResource(R.drawable.btn_search);
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
			}
		});
		
    	//初始化
    	LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
    	loadingView = layoutInflater.inflate(R.layout.list_loading, null);
    	
    	Display mDisplay = getWindowManager().getDefaultDisplay();
    	int width = mDisplay.getWidth();
    	tabCurrent = (ImageView)findViewById(R.id.tabCurrent);
    	LayoutParams params=(LayoutParams)tabCurrent.getLayoutParams();
    	distance = width / 3;
    	params.width = (int)(distance);
    	tabCurrent.setLayoutParams(params);
    	musicX = distance * 1;
    	videoX = distance * 2;
    	
    	//绑定列表
    	listView = (ListView)findViewById(R.id.listView);

    	//加载数据
    	fileBean = new FileBean();
    	fileBean.setActivity(activity);
    	fileBean.setContext(context);
    	fileBean.setOrderBy("date_added DESC"); 
    	fileBean.setPageSize(G.PAGE_SIZE);
    	
    	//绑定适配器
    	listView.addFooterView(loadingView);
    	fileAdapter = new FileAdapter(fileData);
    	listView.setAdapter(fileAdapter);
    	listView.removeFooterView(loadingView);
    	listView.setOnScrollListener(new FileListOnScrollListener());
    	listView.setOnItemLongClickListener(new FileOnItemLongClickListener(fileData));
    	listView.setOnItemClickListener(new FileListOnItemClickListener(fileData));
    }
	
	private void anim(View view,float x1,float x2,float y1,float y2){
		Animation translateAnimation = new TranslateAnimation(x1, x2, y1, y2);  
		translateAnimation.setDuration(500);  
		translateAnimation.setFillAfter(true);
		view.startAnimation(translateAnimation);  
		currentX = x2; 
	}
	
	//加载数据
    private void loadData(){
    	if(!isLoading){
    		String keywords = keywordsEdit.getText().toString();
    		if("".equals(keywords)){
    			//Utils.toast(activity, "请输入关键字！");
    			keywordsEdit.requestFocus();
    			return;
    		}
    		String selection = MediaStore.Images.Media.DISPLAY_NAME + " LIKE ?" ;
    		String[] selectionArgs = new String[]{"%" + keywords + "%"} ;
    		fileBean.setFileType(currentView);
        	fileBean.setSelection(selection);
        	fileBean.setSelectionArgs(selectionArgs);
			fileBean.setCurrentPage(currentPage);
			fileManager.setIlister(new FileLister());
			listView.addFooterView(loadingView);
    		new Thread(new loadDataThread()).start();
    	}
    }
    
    /**
     * 更新列表UI
     */
    private Handler loadDataHandler = new Handler(){
    	public void handleMessage(Message msg) {
    		List<FileBean> tempLists = (List<FileBean>) msg.obj;
    		Utils.log("tempLists:" + tempLists);
    		switch (msg.what) {
			case G.LOAD_SUCCESS:
				fileData.addAll(tempLists);
				listView.removeFooterView(loadingView);
				fileAdapter.notifyDataSetChanged();
				currentPage ++;
				break;
			case G.LOAD_FAILED:
				if(currentPage == 1) Utils.toast(activity, "暂无数据！");
				hasData = false;
				listView.removeFooterView(loadingView);
				break;
			default:
				break;
			}
    		isLoading = false;
    	};
    };
    
  	//加载笑话线程
  	private class loadDataThread extends Thread{
  		@Override
  		public void run() {
  			try{
  				isLoading = true;
  				List<FileBean> tempLists = (List<FileBean>) fileManager.getList(fileBean);
  				Utils.log("tempLists" + tempLists);
  				if(tempLists != null){
	  				Message msg = loadDataHandler.obtainMessage();
	  				msg.arg1 = fileBean.getFileType();
	  				msg.what = G.LOAD_SUCCESS;
	  				msg.obj = tempLists;
	  				msg.sendToTarget();
  				}else{
  					Message msg = loadDataHandler.obtainMessage();
  					msg.arg1 = fileBean.getFileType();
	  				msg.what = G.LOAD_FAILED;
	  				msg.sendToTarget();
  				}  				
  			}catch (Exception e) {
  				Message msg = loadDataHandler.obtainMessage();
  				msg.what = G.LOAD_FAILED;
  				msg.sendToTarget();
				e.printStackTrace();
			}
  		}
  	}
    
  	
	
	//适配器
    private class FileAdapter extends BaseAdapter{
    	List<FileBean> dataList;
    	public FileAdapter(List<FileBean> dataList) {
			this.dataList = dataList;
		}
		@Override
		public int getCount() {
			if(dataList != null){
				return dataList.size();
			}else{
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
				viewHolder.image = (ImageView) convertView.findViewById(R.id.image);
				viewHolder.name = (TextView) convertView.findViewById(R.id.name);
				viewHolder.type = (ImageView) convertView.findViewById(R.id.type);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}	
			viewHolder.image.setTag(dataList.get(position).getFilePath());
			imgManager.setImgFromSDCord(dataList.get(position), listView, context);
			
			viewHolder.name.setText(dataList.get(position).getFileName());
			
			switch(dataList.get(position).getFileType()){
			case G.IMAGE:
				viewHolder.type.setVisibility(View.GONE);
				break;
			case G.MUSIC:
				viewHolder.type.setBackgroundResource(R.drawable.icon_file_music);
				viewHolder.type.setVisibility(View.VISIBLE);
				break;
			case G.VIDEO:
				viewHolder.type.setBackgroundResource(R.drawable.icon_file_video);
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
    
    class ViewHolder{
    	ImageView image;
    	TextView name;
    	ImageView type;
    }
    
    /**
  	 * 长按监听器
  	 *
  	 */
  	class FileOnItemLongClickListener implements OnItemLongClickListener{
  		List<FileBean> dataList;
  		public FileOnItemLongClickListener(List<FileBean> dataList) {
  			this.dataList = dataList;
		}
		@Override
		public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
				int position, long arg3) {
			if(dataList.size() > 0 && position >= 0 && position < dataList.size()){
				FileBean fileBean = dataList.get(position);
				if(fileBean.getFileType() != G.FOLDER) setCommonAlert(fileBean);
			}
			return false;
		}
  		
  	}
  	
  	/**
  	 * 设置、取消常用弹出框
  	 * @param fileBean
  	 */
  	private void setCommonAlert(FileBean fileBean){
  		AlertDialog.Builder builder = new AlertDialog.Builder(context);
  		if(fileBean.getFileStatus() != G.COMMON) builder.setTitle(R.string.set_common);
  		else builder.setTitle(R.string.cancel_common);
  		SetCommonListener pl = new SetCommonListener(fileBean);
    	builder.setPositiveButton(getString(R.string.ok), pl);
    	builder.setNegativeButton(getString(R.string.cancel), pl);
    	AlertDialog alertDialog = builder.create();
    	alertDialog.show();
    }
    
  	/**
  	 * 设置、取消常用
  	 */
    public class SetCommonListener implements DialogInterface.OnClickListener{
    	
    	FileBean fileBean;
    	public SetCommonListener(FileBean fileBean) {
			this.fileBean = fileBean;
		}
    	
		@Override
		public void onClick(DialogInterface v, int btnId) {
			if(btnId == DialogInterface.BUTTON_POSITIVE){
				setCommon(fileBean);
			}
		}
    	
    }
    
    private void setCommon(FileBean fileBean){
    	fileManager.setSqliter(new FileSqliter());
    	fileBean.setContext(context);
  		fileBean.setActivity(activity);
    	if(fileBean.getFileStatus() != G.COMMON) {
			fileBean.setSelection(TableFileMateData.PATH + " = ?");
			fileBean.setSelectionArgs(new String[]{fileBean.getFilePath()});
			int hasData = fileManager.getLocalCount(fileBean);
			if(hasData > 0) {
				Utils.toast(activity, getString(R.string.isacommon));
				return;
			}
			fileBean.setFileStatus(G.COMMON);
			/*Calendar calendar = Calendar.getInstance();
			String time = DateTimeUtils.getDateTimeString(calendar);
			fileBean.setAddTime(time);*/
			long result = fileManager.insert(fileBean);
			if(result != G.SQL_ERROR) {
				fileBean.setFileStatus(G.COMMON);
				Utils.toast(activity, getString(R.string.setting_success));
			}
			else {
				fileBean.setFileStatus(G.NOT_COMMON);
				Utils.toast(activity, getString(R.string.setting_failed));
			}
		}else{
			fileBean.setSelection(TableFileMateData.PATH + " = ?");
			fileBean.setSelectionArgs(new String[]{fileBean.getFilePath()});
			int result = fileManager.delete(fileBean);
			if(result != G.SQL_ERROR) {
				fileBean.setFileStatus(G.NOT_COMMON);
				Utils.toast(activity, getString(R.string.setting_success));
			}
			else Utils.toast(activity, getString(R.string.setting_failed));
		} 
    	
    	fileAdapter.notifyDataSetChanged();
    }

    //列表滚动监听器
  	class FileListOnScrollListener implements OnScrollListener{
  		
  		@Override
  		public void onScroll(AbsListView view, int firstVisibleItem,
  				int visibleItemCount, int totalItemCount) {
  			int lastVisibleItem = firstVisibleItem + visibleItemCount;
  			if(lastVisibleItem == 0 || lastVisibleItem < totalItemCount || totalItemCount < G.PAGE_SIZE) {
  				return;
  			}
  			if(hasData)	{
  				if(isLoading == false){
  					//isMore = true;
  					loadData();	
  				}
  			}
  		}

  		@Override
  		public void onScrollStateChanged(AbsListView view, int scrollState) {
  			
  		}
  		
  	}
  	
  	//点击监听器
  	class FileListOnItemClickListener implements OnItemClickListener{
  		List<FileBean> dataList;
  		public FileListOnItemClickListener(List<FileBean> dataList) {
  			this.dataList = dataList;
		}
  		
  		@Override
  		public void onItemClick(AdapterView<?> arg0, View arg1, int position,
  				long arg3) {
  			
  			if(dataList.size() > 0 && position >= 0 && position < dataList.size()){
  				FileBean tempBean = dataList.get(position);
  				Utils.log("getFileType:" + tempBean.getFileType());
				switch (tempBean.getFileType()) {
				case G.IMAGE:
					Utils.toast(activity, "图片：" + tempBean.getFileName() + "\n" + 
							"路径：" + tempBean.getFilePath()
							);
					if(tempBean.getFileStatus() != G.COMMON) setCommon(tempBean);
					break;
				case G.MUSIC:
					Utils.toast(activity, "音乐：" + tempBean.getFileName() + "\n" + 
							"路径：" + tempBean.getFilePath()
							);
					if(tempBean.getFileStatus() != G.COMMON) setCommon(tempBean);
					break;
				case G.VIDEO:
					Utils.toast(activity, "视频：" + tempBean.getFileName() + "\n" + 
							"路径：" + tempBean.getFilePath()
							);
					if(tempBean.getFileStatus() != G.COMMON) setCommon(tempBean);
					break;
				case G.CLOUD:
					Utils.toast(activity, "云资源：" + tempBean.getFileName() + "\n" + 
							"路径：" + tempBean.getFilePath()
							);
					break;
				case G.FOLDER:
					Intent intent = new Intent();
					intent.putExtra("type", tempBean.getFolderType());
					intent.putExtra("path", tempBean.getFilePath());
					intent.putExtra("name", tempBean.getFileName());
					intent.setClass(context, SearchFileActivity.class);
					startActivity(intent);
					Utils.log("FOLDER");
					break;

				default:
					break;
				}
  			}
  		}
  		
  	}

	public void doClick(View view) {
		switch (view.getId()) {
		case R.id.btnBack:
			finish();
			break;
		case R.id.btnSearch:
			Utils.showHideInputMethod(context);
			fileData.clear();
			fileAdapter.notifyDataSetChanged();
			loadData();
			break;	
		case R.id.tabImage:
			anim(tabCurrent, currentX, imageX, 0f, 0f);
			currentPage = 1;
			currentView = G.IMAGE;
			fileData.clear();
			fileAdapter.notifyDataSetChanged();
			hasData = true;
			loadData();
			break;
		case R.id.tabMusic:
			anim(tabCurrent, currentX, musicX, 0f, 0f);
			currentPage = 1;
			currentView = G.MUSIC;
			fileData.clear();
			fileAdapter.notifyDataSetChanged();
			hasData = true;
			loadData();
			break;
		case R.id.tabVideo:
			anim(tabCurrent, currentX, videoX, 0f, 0f);
			currentPage = 1;
			currentView = G.VIDEO;
			fileData.clear();
			fileAdapter.notifyDataSetChanged();
			hasData = true;
			loadData();
			break;
		default:
			break;
		}
	}
}
