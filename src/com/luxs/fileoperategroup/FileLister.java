package com.luxs.fileoperategroup;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.luxs.config.G;
import com.luxs.provider.FileProviderMateData.TableFileMateData;
import com.luxs.utils.Utils;

/**
 * 获取乐推送文件列表
 */
public class FileLister implements Ilister {

	@Override
	public List<?> getList(FileBean fileBean) {
		if (fileBean == null)
			return null;

		// 设置URI
		Uri uri = null;
		switch (fileBean.getFileType()) {
		case G.IMAGE:
			uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
			break;
		case G.MUSIC:
			uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
			break;
		case G.VIDEO:
			uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
			break;
		default:
			break;
		}

		// 获取数据
		// Cursor cursor = fileBean.getActivity().managedQuery(uri, null,
		// fileBean.getSelection(), fileBean.getSelectionArgs(),
		// fileBean.getOrderBy());
		Cursor cursor = fileBean
				.getActivity()
				.getApplicationContext()
				.getContentResolver()
				.query(uri, null, fileBean.getSelection(),
						fileBean.getSelectionArgs(), fileBean.getOrderBy());
		

		if (cursor == null)
			return null;

		// 判断是否为最后一页
		int pageSize = fileBean.getPageSize();
		int currentPage = fileBean.getCurrentPage();
		int totalCount = cursor.getCount();
		int totalPage = totalCount % pageSize == 0 ? totalCount / pageSize
				: totalCount / pageSize + 1;
		if (currentPage > totalPage) {
			cursor.close();
			return null;
		}

		int startIndex = (currentPage - 1) * pageSize;
		int endIndex = currentPage * pageSize - 1;
		if (endIndex >= totalCount)
			endIndex = totalCount - 1;
		List<FileBean> resultList = new ArrayList<FileBean>();
		for (int i = startIndex; cursor != null && i <= endIndex; i++) {
			cursor.moveToPosition(i);
			String path = cursor.getString(cursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
			String name = cursor.getString(cursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.TITLE));
			FileBean tempBean = new FileBean();
			tempBean.setFileThumb(path);
			tempBean.setFilePath(path);
			tempBean.setFileName(name);
			tempBean.setFileType(fileBean.getFileType());
			if (hasFileData(fileBean.getContext(), path))
				tempBean.setFileStatus(G.COMMON);
			else
				tempBean.setFileStatus(G.NOT_COMMON);
			resultList.add(tempBean);
		}
		if (cursor != null) {
			cursor.close();
			cursor = null;
		}

		return resultList;
	}

	private boolean hasFileData(Context context, String path) {
		FileManager fileManager = new FileManager();
		fileManager.setSqliter(new FileSqliter());
		FileBean fileBean = new FileBean();
		fileBean.setContext(context);
		fileBean.setSelection(TableFileMateData.PATH + " = ?");
		fileBean.setSelectionArgs(new String[] { path });
		int hasData = fileManager.getLocalCount(fileBean);
		if (hasData > 0)
			return true;
		else
			return false;
	}

	@Override
	public List<?> getList(FileBean fileBean, List<NameValuePair> postParameters) {
		return null;
	}

	@Override
	public int getCount(FileBean fileBean) {
		return 0;
	}

	@Override
	public int getRemoteCount(FileBean fileBean) {
		return 0;
	}

}
