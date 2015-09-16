package com.luxs.sdcord;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;

import org.apache.http.util.EncodingUtils;

import android.os.Environment;

/**
 * SDManager SD卡管理器
 * @version 1.0
 * @author zhaidian.net CaoLun
 * @date 2013-02-14
 */
public class SDManager implements Serializable {
	private static final long serialVersionUID = 1L;
	private String sdState;
	
	public SDManager() {
		this.sdState = Environment.getExternalStorageState();
	}
	
	/**
	 * 获取SD卡状态
	 * @return
	 */
	public String getsdState() {
		return sdState;
	}
	
	/**
	 * 设置SD卡状态
	 * @param sdState
	 */
	public void setsdState(String sdState) {
		this.sdState = sdState;
	}
	
	/**
	 * SD卡是否可用
	 * @return
	 */
	public boolean isSDCardAvailable() {
		if(sdState.equals(Environment.MEDIA_MOUNTED)){
			return true;
		}
		return false;
	}
	
	/**
	 * SD卡是否为只读
	 * @return
	 */
	public boolean isSDCardReadOnly() {
		if(sdState.equals(Environment.MEDIA_MOUNTED_READ_ONLY)){
			return true;
		}
		return false;
	}
	
	/**
	 * SD卡是否用USB与外部设置连接
	 * @return
	 */
	public boolean isSDCardShared() {
		if(sdState.equals(Environment.MEDIA_SHARED)){
			return true;
		}
		return false;
	}
	
	/**
	 * 目录是否存在
	 * @param path
	 * @return
	 */
	public boolean isDirExist(String path) {
		File file = new File(path);
		if(file.exists()){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * 文件是否存在
	 * @param path
	 * @return
	 */
	public boolean isFileExist(String path) {
		File file = new File(path);
		if(file.exists()){
			return true;
		}
		return false;
	}
	
	/**
	 * 文件是否存在
	 * @param fileName
	 * @param dir
	 * @return
	 */
	public boolean isFileExist(String fileName, String dir) {
		File file = new File(dir + fileName);
		if(file.exists()){
			return true;
		}
		return false;
	}
	
	/**
	 * 创建目录到SD卡
	 * @param dir
	 * @return
	 */
	public File createDir(String dir) {
		File dirFile = new File(dir);
		if(dirFile.mkdirs()){
		}else{
		}
		return dirFile;
	}
	
	/**
	 * 创建文件到SD卡
	 * @param fileName
	 * @param path
	 * @return
	 */
	public File createFile(String fileName, String path) {
		File file = new File(path + fileName);
		try {
			file.createNewFile();
			return file;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 删除指定文件
	 * @param fileName
	 * @param path
	 * @return
	 */
	public boolean deleteFile(String fileName, String path){
		File file = new File(path + fileName);
		try{
			return file.delete();
		}catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * 删除指定目录下的所有文件
	 * @param path
	 * @return
	 */
	public File deleteDirFile(String path) {
		if(!isDirExist(path)) return null;
		
		try{
			File dirFile = new File(path);
			File[] fileList = dirFile.listFiles();
			for (File file : fileList) {
				if(file.isFile()){
					file.delete();
				}else{
					deleteDirFile(file.getAbsolutePath());
				}
			}
			return dirFile;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 从SD卡读取字符串
	 * @param fileName
	 * @param path
	 * @return
	 */
	public String getStringFromSD(String fileName,String path){
		String resultStr = "";
		File file = new File(path + fileName);
		try {
			FileInputStream  fileInputStream = new FileInputStream(file);
			int length = fileInputStream.available();
			byte[] buffer = new byte[length];
			fileInputStream.read(buffer);
			resultStr = EncodingUtils.getString(buffer, "UTF-8");
			fileInputStream.close();
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return resultStr;
	}
	
	
    
}
