package com.luxs.fileoperategroup;

import java.util.List;

import org.apache.http.NameValuePair;

/**
 * 列表接口
 * @author zhaidian.net
 * @version 1.0
 */
public interface Ilister {
	//获得列表
	List<?> getList(FileBean fileBean);
	
	//通过POST方式获得列表
	List<?> getList(FileBean fileBean,List<NameValuePair> postParameters);
	
	//通过本地记录总数量
	int getCount(FileBean fileBean);
	
	//通过服务器端记录总数量
	int getRemoteCount(FileBean fileBean);
}
