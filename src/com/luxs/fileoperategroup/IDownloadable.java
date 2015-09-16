package com.luxs.fileoperategroup;


public interface IDownloadable{
	int downloadToSDCard(FileBean fileBean);
	String downloadStr(FileBean fileBean);
}
