package com.luxs.fileoperategroup;

import java.io.File;
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
 * 获取乐推送文件夹及根目录文件列表
 */
public class FolderLister implements Ilister {

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

		Cursor cursor = fileBean
				.getActivity()
				.getApplicationContext()
				.getContentResolver()
				.query(uri, null, fileBean.getSelection(),
						fileBean.getSelectionArgs(), fileBean.getOrderBy());
		
		if (cursor == null)
			return null;
		List<FileBean> tempList = new ArrayList<FileBean>();
		for (int i = 0; cursor != null && i < cursor.getCount(); i++) {
			
				cursor.moveToPosition(i);
				String path = null;
				switch (fileBean.getFileType()) {
				case G.IMAGE:
					path = cursor
							.getString(cursor
									.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
					break;
				case G.MUSIC:
					path = cursor
							.getString(cursor
									.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
					break;
				case G.VIDEO:
					path = cursor
							.getString(cursor
									.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
					break;
				default:
					break;
				}
				if (path == null)
					continue;
				File file = new File(path);
				String folderPath = file.getParent();
				FileBean tempBean = new FileBean();
				if (G.SD_ROOT_PATH.equals(folderPath)) {
					tempBean.setFileThumb(path);
					tempBean.setFilePath(path);
					tempBean.setFileName(cursor.getString(cursor
							.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)));
					tempBean.setFileType(fileBean.getFileType());
					if (hasFileData(fileBean.getContext(), path))
						tempBean.setFileStatus(G.COMMON);
					else
						tempBean.setFileStatus(G.NOT_COMMON);
					tempList.add(tempBean);
				} else if (!hasData(tempList, folderPath)) {
					try {
						String selection = "_data LIKE ?";
						String[] selectionArgs = new String[] { folderPath
								+ "%" };

						Cursor countCursor = fileBean
								.getActivity()
								.getApplicationContext()
								.getContentResolver()
								.query(uri, null, selection, selectionArgs,
										fileBean.getOrderBy());
						String name = folderPath.substring(folderPath
								.lastIndexOf(File.separator) + 1);
						tempBean.setFileThumb(path);
						tempBean.setFilePath(folderPath);
						tempBean.setFileName(name + " ("
								+ countCursor.getCount() + ")");
						tempBean.setFileType(G.FOLDER);
						tempBean.setFolderType(fileBean.getFileType());
						tempList.add(tempBean);
						countCursor.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			
		}

		if (tempList.size() == 0) {
			cursor.close();
			return null;
		}

		// 判断是否为最后一页
		int pageSize = fileBean.getPageSize();
		int currentPage = fileBean.getCurrentPage();
		int totalCount = tempList.size();
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
		for (int i = startIndex; i <= endIndex; i++) {
			resultList.add(tempList.get(i));
		}
		cursor.close();
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

	private boolean hasData(List<FileBean> list, String path) {
		for (int i = 0; list != null && i < list.size(); i++) {
			if (list.get(i).getFilePath().equals(path))
				return true;
		}
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
