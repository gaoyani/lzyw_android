package com.luxs.fileoperategroup;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;

import com.luxs.config.G;
import com.luxs.sdcord.SDManager;



public class FileDownloader implements IDownloader,Serializable {
	private static final long serialVersionUID = 1L;
	
	SDManager sdManager = new SDManager();
	int downloadPercent = 0;
	long currentSize = 0;
    int totalSize = 0;
    
	@Override
	public int downloadToSDCard(FileBean fileBean) {
		
		String fileName = fileBean.getFileName();
		String path = fileBean.getFilePath();
		String urlStr = fileBean.getFileUrl();

		
		File file = null;
		OutputStream outputStream = null;
		if(!sdManager.isDirExist(path)){
			sdManager.createDir(path);
		}
		if(sdManager.isDirExist(path)){
			if(sdManager.isFileExist(fileName, path)){
				file = new File(path + fileName);
			}else{
				file = sdManager.createFile(fileName, path);
			}			
			if(file == null){
				return G.LOAD_FAILED;
			}else{
				try {
					URL url = new URL(urlStr);
					HttpURLConnection connection = (HttpURLConnection) url.openConnection();
					InputStream inputStream = connection.getInputStream();
					totalSize = connection.getContentLength();
					outputStream = new FileOutputStream(file);
					byte buffer[] = new byte[4 * 1024];
					int temp;
					while((temp = inputStream.read(buffer)) != -1){
						outputStream.write(buffer,0,temp);
						currentSize += temp; 
						if((int)(currentSize*100/totalSize)-1 >= downloadPercent){
							downloadPercent += 1;
							fileBean.setFileStatus(downloadPercent);
						}		
					}
					outputStream.flush();
				}catch(Exception e) {
					return G.LOAD_FAILED;
				}finally{
					try {
						outputStream.close();
					} catch (Exception e) {
						return G.LOAD_FAILED;
					}
				}
				return G.LOAD_SUCCESS;
			}
		}else{
			return G.LOAD_FAILED;
		}
	}


	@Override
	public String downloadStr(FileBean fileBean) {
		return null;
	}
	

}
