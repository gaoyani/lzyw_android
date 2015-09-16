package com.luxs.fileoperategroup;


public interface IDownloader {
	int downloadToSDCard(FileBean fileBean);
	String downloadStr(FileBean fileBean);
}
