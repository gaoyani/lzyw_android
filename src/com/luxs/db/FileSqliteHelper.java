package com.luxs.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import com.luxs.provider.FileProviderMateData.TableFileMateData;
import com.luxs.provider.IPsProviderMateData.TableMateData;

public class FileSqliteHelper extends SQLiteOpenHelper {

	public FileSqliteHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		db.execSQL("CREATE TABLE " + TableMateData.TABLE_NAME + "(" +
				TableMateData._ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
				TableMateData.IP + " VARCHAR(15) NOT NULL," +
				TableMateData.NAME + " VARCHAR(50)," +
				TableMateData.STATUS + " INTEGER" +
				");"
				);
		
		db.execSQL("CREATE TABLE " + TableFileMateData.TABLE_NAME + "(" +
				TableFileMateData._ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
				TableFileMateData.NAME + " VARCHAR(50) NOT NULL," +
				TableFileMateData.PATH + " VARCHAR(200) NOT NULL," +
				TableFileMateData.TYPE + " INTEGER NOT NULL," +
				TableFileMateData.ADD_DATE + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL," +
				TableFileMateData.STATUS + " INTEGER" +
				");"
				);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
		
	}

}
