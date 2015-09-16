package com.luxs.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class MyDatabase {
	private static final String DB_NAME = "rc";
	private static final int DB_VERSION = 1;
	private static SQLiteDatabase db = null;

	public static SQLiteDatabase getDatabaseInstance(Context context){
		if(db == null){
			FileSqliteHelper dh = new FileSqliteHelper(context,DB_NAME,null,DB_VERSION);
			db = dh.getWritableDatabase();
		}
		return db;
	}
	
}
