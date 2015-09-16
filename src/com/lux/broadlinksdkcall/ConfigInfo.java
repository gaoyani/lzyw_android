package com.lux.broadlinksdkcall;

public class ConfigInfo {

	private String mac;
	private int type;
	private String codeValue;

	/**
	 * @return the name
	 */
	public String getMac() {
		return mac;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setMac(String mac) {
		this.mac = mac;
	}

	/**
	 * @return the age
	 */
	public int getType() {
		return type;
	}

	/**
	 * @param age
	 *            the age to set
	 */
	public void setType(int type) {
		this.type = type;
	}

	public String getCodeValue() {
		return codeValue;
	}

	public void setCodeValue(String codeValue) {
		this.codeValue = codeValue;
	}
}
