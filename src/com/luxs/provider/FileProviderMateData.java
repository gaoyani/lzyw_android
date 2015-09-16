package com.luxs.provider;

import android.net.Uri;
import android.provider.BaseColumns;

public class FileProviderMateData {
	public static final String AUTHORTY = "com.luxs.provider.FileProvider";
	
	public static final class TableFileMateData implements BaseColumns{
		public static final String TABLE_NAME = "files";
		public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORTY + "/" + TABLE_NAME);
		public static final String CONTENT_URI_ID = "content://" + AUTHORTY + "/" + TABLE_NAME + "/";
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.zhaidian.file.";
		public static final String CONTENT_TYPE_ITEM = "vnd.android.cursor.item/vnd.zhaidian.provider.file";

		public static final String NAME = "name";
		public static final String PATH = "path";
		public static final String TYPE = "type";
		public static final String ADD_DATE = "date_added";
		public static final String STATUS = "status";
		

		public static final String DEFAULT_SORT_ORDER = _ID;
	}
}
