package com.luxs.fileoperategroup;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.luxs.config.G;
import com.luxs.provider.FileProviderMateData.TableFileMateData;
import com.luxs.utils.Utils;
/**
 * 文件数据库操作实现类，功能：插入/删除/更新消息数据。
 * @author zhaidian.net
 * @version 1.0
 */
public class FileSqliter implements ISqliter {


	@Override
	public List<?> query(FileBean fileBean) {
		
			Log.i("FileSqliter", "查询");
		
		if (fileBean != null) {
			String selection = fileBean.getSelection();
			String[] selectionArgs = fileBean.getSelectionArgs();
			String orderBy = fileBean.getOrderBy();
			
			int startPosition = 0;
			int pageSize = 10;
			if(fileBean.getCurrentPage() != 0){
				startPosition = (fileBean.getCurrentPage() - 1 ) * fileBean.getPageSize();
			}
			if(fileBean.getPageSize() != 0){
				pageSize = fileBean.getPageSize();
			}

			Uri uri = TableFileMateData.CONTENT_URI;
			ContentResolver cr = fileBean.getContext().getContentResolver();
			Cursor cursor = cr.query(uri, null, selection, selectionArgs, orderBy);
			Utils.log("LUXS:常用Cursor:" + cursor);
			List<FileBean> resultLists = null;
			if(cursor.getCount() > 0){
				resultLists = new ArrayList<FileBean>();
				for(int i = 0 , j = startPosition ; i < pageSize && cursor.moveToPosition(j) ; i++ , j++){
					FileBean tempBean = new FileBean();
					tempBean.setFileId(cursor.getInt(cursor.getColumnIndex(TableFileMateData._ID)));
					tempBean.setFileName(cursor.getString(cursor.getColumnIndex(TableFileMateData.NAME)));
					tempBean.setFileThumb(cursor.getString(cursor.getColumnIndex(TableFileMateData.PATH)));
					tempBean.setFilePath(cursor.getString(cursor.getColumnIndex(TableFileMateData.PATH)));
					tempBean.setFileType(cursor.getInt(cursor.getColumnIndex(TableFileMateData.TYPE)));
					tempBean.setFileStatus(cursor.getInt(cursor.getColumnIndex(TableFileMateData.STATUS)));
					resultLists.add(tempBean);
				}
			}
			cursor.close();
			return resultLists;
		}else{
			return null;
		}
			
	}

	@Override
	public long insert(FileBean fileBean) {

		if (fileBean != null) {
			ContentValues values = new ContentValues();
			FileBean tempBean = (FileBean) fileBean;
			values.put(TableFileMateData.NAME, tempBean.getFileName());
			values.put(TableFileMateData.PATH, tempBean.getFilePath());
			values.put(TableFileMateData.TYPE, tempBean.getFileType());
			//values.put(TableFileMateData.ADD_DATE, tempBean.getAddTime());
			values.put(TableFileMateData.STATUS, tempBean.getFileStatus());
			
			ContentResolver cr = fileBean.getContext().getContentResolver();
		    Uri uri = TableFileMateData.CONTENT_URI;
			Uri insertedUri = cr.insert(uri, values);
			
			long result = 0;
			if(insertedUri != null){
				result = G.SQL_INSERT_SUCCESS;
			}
			return result;
		} else {
			return G.SQL_ERROR;
		}
	
	}

	@Override
	public int update(FileBean fileBean) {

		if (fileBean != null) {
			String selection = fileBean.getSelection();
			String[] selectionArgs = fileBean.getSelectionArgs();
//Log.d(G.TAG,"selection:" + selection + " | selectionArgs:" + selectionArgs[0]);
			ContentValues values = new ContentValues();
			FileBean tempBean = (FileBean) fileBean;

			values.put(TableFileMateData.STATUS, tempBean.getFileStatus());

			ContentResolver cr = fileBean.getContext().getContentResolver();
		    Uri uri = TableFileMateData.CONTENT_URI;
		    int result = cr.update(uri, values, selection, selectionArgs);
			return result;
		} else {
			return G.SQL_ERROR;
		}
		
	}

	@Override
	public int delete(FileBean fileBean) {

		if (fileBean != null) {
			String selection = fileBean.getSelection();
			String[] selectionArgs = fileBean.getSelectionArgs();

			ContentResolver cr = fileBean.getContext().getContentResolver();
		    Uri uri = TableFileMateData.CONTENT_URI;
		    int result = cr.delete(uri, selection, selectionArgs);				
			return result;
		} else {
			return G.SQL_ERROR;
		}
		
	}

	@Override
	public int getLocalCount(FileBean fileBean) {

		if (fileBean != null) {
			String selection = fileBean.getSelection();
			String[] selectionArgs = fileBean.getSelectionArgs();
//Log.d(G.TAG,"selection:" + selection + " | selectionArgs:" + selectionArgs[0]);
			Uri uri = TableFileMateData.CONTENT_URI;
			ContentResolver cr = fileBean.getContext().getContentResolver();
			Cursor cursor = cr.query(uri, null, selection, selectionArgs, null);
			int count = cursor.getCount();
			cursor.close();
			return count;
		} else {
			return 0;
		}
	
	}

	@Override
	public int getPageCount(FileBean fileBean) {

		if (fileBean != null) {
			int count = getLocalCount(fileBean);
			if (count == 0) {
				return 0;
			} else {
				int allPage = count % fileBean.getPageSize() == 0 ? count / fileBean.getPageSize() : count / fileBean.getPageSize() + 1;
				return allPage;
			}
		} else {
			return 0;
		}
		
	}
	
}
