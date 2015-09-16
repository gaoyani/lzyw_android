package com.luxs.fileoperategroup;

import java.util.List;


public interface ISqliter {
	List<?> query(FileBean fileBean);
	long insert(FileBean fileBean);
	int update(FileBean fileBean);
	int delete(FileBean fileBean);
	int getLocalCount(FileBean fileBean);
	int getPageCount(FileBean fileBean);
}
