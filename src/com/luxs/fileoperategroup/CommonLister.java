package com.luxs.fileoperategroup;

import java.util.List;

import org.apache.http.NameValuePair;
/**
 * 获取乐推送常用列表
 */
public class CommonLister implements Ilister {
	
	@Override
	public List<?> getList(FileBean fileBean) {
		if(fileBean == null) return null;
		
		FileManager fileManager = new FileManager();
		fileManager.setSqliter(new FileSqliter());
		return fileManager.query(fileBean);
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
