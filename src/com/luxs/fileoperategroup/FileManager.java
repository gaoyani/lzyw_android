package com.luxs.fileoperategroup;

import java.util.List;

import org.apache.http.NameValuePair;

import com.huiwei.roomreservation.R;
import com.luxs.config.G;
import com.luxs.provider.FileProviderMateData.TableFileMateData;
import com.luxs.utils.Utils;

public class FileManager implements ISqliteable,Ilistable,IDownloadable{

	ISqliter sqliter;
	Ilister ilister;
	IDownloader downloader;

	public ISqliter getSqliter() {
		return sqliter;
	}

	public void setSqliter(ISqliter sqliter) {
		this.sqliter = sqliter;
	}

	public Ilister getIlister() {
		return ilister;
	}

	public void setIlister(Ilister ilister) {
		this.ilister = ilister;
	}
	
	public IDownloader getDownloader() {
		return downloader;
	}

	public void setDownloader(IDownloader downloader) {
		this.downloader = downloader;
	}

	public List<?> setSelected(List<?> listData) {
		List<FileBean> list = (List<FileBean>) listData;
		for(int i = 0 ; i < list.size() ; i ++){
			list.get(i).setSelected(true);
		}
		return list;
	}
	
	public List<?> setUnSelected(List<?> listData) {
		List<FileBean> list = (List<FileBean>) listData;
		for(int i = 0 ; i < list.size() ; i ++){
			list.get(i).setSelected(false);
		}
		return list;
	}

	@Override
	public List<?> query(FileBean fileBean) {
		return sqliter.query(fileBean);
	}

	@Override
	public long insert(FileBean fileBean) {
		return sqliter.insert(fileBean);
	}

	@Override
	public int update(FileBean fileBean) {
		return sqliter.update(fileBean);
	}

	@Override
	public int delete(FileBean fileBean) {
		return sqliter.delete(fileBean);
	}

	@Override
	public int getLocalCount(FileBean fileBean) {
		return sqliter.getLocalCount(fileBean);
	}

	@Override
	public int getPageCount(FileBean fileBean) {
		return sqliter.getPageCount(fileBean);
	}

	@Override
	public List<?> getList(FileBean fileBean) {
		return ilister.getList(fileBean);
	}

	@Override
	public List<?> getList(FileBean fileBean, List<NameValuePair> postParameters) {
		return ilister.getList(fileBean, postParameters);
	}
	
	@Override
	public int getCount(FileBean fileBean) {
		return ilister.getCount(fileBean);
	}

	@Override
	public int getRemoteCount(FileBean fileBean) {
		return ilister.getRemoteCount(fileBean);
	}
	
	public void setCommon(FileBean fileBean){
    	this.setSqliter(new FileSqliter());
    	
    	if(fileBean.getFileStatus() != G.COMMON) {
			fileBean.setSelection(TableFileMateData.PATH + " = ?");
			fileBean.setSelectionArgs(new String[]{fileBean.getFilePath()});
			int hasData = this.getLocalCount(fileBean);
			if(hasData > 0) {
				Utils.toast(fileBean.getActivity(), fileBean.getActivity().getString(R.string.isacommon));
				return;
			}
			fileBean.setFileStatus(G.COMMON);
			/*Calendar calendar = Calendar.getInstance();
			String time = DateTimeUtils.getDateTimeString(calendar);
			fileBean.setAddTime(time);*/
			long result = this.insert(fileBean);
			if(result != G.SQL_ERROR) {
				fileBean.setFileStatus(G.COMMON);
				Utils.toast(fileBean.getActivity(), fileBean.getActivity().getString(R.string.setting_success));
			}
			else Utils.toast(fileBean.getActivity(), fileBean.getActivity().getString(R.string.setting_failed));
		}else{
			fileBean.setSelection(TableFileMateData.PATH + " = ?");
			fileBean.setSelectionArgs(new String[]{fileBean.getFilePath()});
			int result = this.delete(fileBean);
			if(result != G.SQL_ERROR) {
				fileBean.setFileStatus(G.NOT_COMMON);
				Utils.toast(fileBean.getActivity(), fileBean.getActivity().getString(R.string.setting_success));
			}
			else Utils.toast(fileBean.getActivity(), fileBean.getActivity().getString(R.string.setting_failed));
		} 
    }

	@Override
	public int downloadToSDCard(FileBean fileBean) {
		return downloader.downloadToSDCard(fileBean);
	}

	@Override
	public String downloadStr(FileBean fileBean) {
		return downloader.downloadStr(fileBean);
	}
}
