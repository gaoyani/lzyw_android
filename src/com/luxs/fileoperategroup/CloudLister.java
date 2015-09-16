package com.luxs.fileoperategroup;

import java.util.List;

import org.apache.http.NameValuePair;

public class CloudLister implements Ilister {

	@Override
	public List<?> getList(FileBean fileBean) {
		return null;
	}

	@Override
	public List<?> getList(FileBean fileBean, List<NameValuePair> postParameters) {
		//获取云端数据
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
