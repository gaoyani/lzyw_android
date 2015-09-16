package com.luxs.connect;

import com.luxs.fileoperategroup.FileBean;

public class ServerBean extends FileBean {
	private int id;
	private String ip;
	private String name="";

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
