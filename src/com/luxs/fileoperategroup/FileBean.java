package com.luxs.fileoperategroup;

import java.io.Serializable;

import android.app.Activity;
import android.content.Context;

/**
 * FileBean 文件类
 * @version 1.0
 * @author zhaidian.net
 * @date 2013-02-14
 */
public class FileBean implements Serializable{
	protected static final long serialVersionUID = 1L;
	//上下文
	protected Activity activity;
	//上下文
	protected Context context;
	//文件ID
	protected int fileId;
	//文件名称
	protected String fileName;
	//文件大小
	protected int fileSize;
	//文件类型
	protected int fileType;
	//文件夹类型
	protected int folderType;
	//文件缩略图
	protected String fileThumb;
	//本地存储地址
	protected String filePath;
	//创建时间
	protected String addTime;
	//文件URL
	protected String fileUrl;
	//文件列表URL
	protected String fileListUrl;
	//文件详细页URL
	protected String fileDetailUrl;
	//文件数量URL
	protected String fileCountUrl;
	//添加文件URL
	protected String addFileUrl;
	//文件状态
	protected int fileStatus;
	//查询数据库条件
	protected String selection;
	//查询数据库条件参数
	protected String[] selectionArgs;
	//查询数据库排序参数
	protected String orderBy;
	//当前列表页码
	protected int currentPage = 0;
	//列表每页显示记录数量
	protected int pageSize = 0;
	//返回状态
	protected int code;
	//数量
	protected int count;
	//是否选中
	protected boolean selected = false;
	
	public Activity getActivity() {
		return activity;
	}
	public void setActivity(Activity activity) {
		this.activity = activity;
	}
	public Context getContext() {
		return context;
	}
	public void setContext(Context context) {
		this.context = context;
	}
	public int getFileId() {
		return fileId;
	}
	public void setFileId(int fileId) {
		this.fileId = fileId;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public int getFileSize() {
		return fileSize;
	}
	public void setFileSize(int fileSize) {
		this.fileSize = fileSize;
	}
	public int getFileType() {
		return fileType;
	}
	public void setFileType(int fileType) {
		this.fileType = fileType;
	}
	public int getFolderType() {
		return folderType;
	}
	public void setFolderType(int folderType) {
		this.folderType = folderType;
	}
	public String getFileThumb() {
		return fileThumb;
	}
	public void setFileThumb(String fileThumb) {
		this.fileThumb = fileThumb;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public String getAddTime() {
		return addTime;
	}
	public void setAddTime(String addTime) {
		this.addTime = addTime;
	}
	public String getFileUrl() {
		return fileUrl;
	}
	public void setFileUrl(String fileUrl) {
		this.fileUrl = fileUrl;
	}
	public String getFileListUrl() {
		return fileListUrl;
	}
	public void setFileListUrl(String fileListUrl) {
		this.fileListUrl = fileListUrl;
	}
	public String getFileDetailUrl() {
		return fileDetailUrl;
	}
	public void setFileDetailUrl(String fileDetailUrl) {
		this.fileDetailUrl = fileDetailUrl;
	}
	public String getFileCountUrl() {
		return fileCountUrl;
	}
	public void setFileCountUrl(String fileCountUrl) {
		this.fileCountUrl = fileCountUrl;
	}
	public String getAddFileUrl() {
		return addFileUrl;
	}
	public void setAddFileUrl(String addFileUrl) {
		this.addFileUrl = addFileUrl;
	}
	public int getFileStatus() {
		return fileStatus;
	}
	public void setFileStatus(int fileStatus) {
		this.fileStatus = fileStatus;
	}
	public String getSelection() {
		return selection;
	}
	public void setSelection(String selection) {
		this.selection = selection;
	}
	public String[] getSelectionArgs() {
		return selectionArgs;
	}
	public void setSelectionArgs(String[] selectionArgs) {
		this.selectionArgs = selectionArgs;
	}
	public String getOrderBy() {
		return orderBy;
	}
	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}
	public int getCurrentPage() {
		return currentPage;
	}
	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public boolean isSelected() {
		return selected;
	}
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	
	
}
