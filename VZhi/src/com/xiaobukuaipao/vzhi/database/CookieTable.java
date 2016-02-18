package com.xiaobukuaipao.vzhi.database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class CookieTable {

	public static final String TAG = CookieTable.class.getSimpleName();
	// Database table
	public static final String TABLE_COOKIE = "cookie";
	
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_MOBILE = "mobile";
	public static final String COLUMN_P = "p";
	public static final String COLUMN_DOMAIN = "domain";
	public static final String COLUMN_PATH = "path";
	public static final String COLUMN_LOGINTIME = "login_time";
	public static final String COLUMN_EXPIRE = "expire";
	
	// Database Creation SQL Statement
	private static final String DATABASE_CREATE = "create table "
			+ TABLE_COOKIE
			+ "(" 
			+ COLUMN_ID + " integer primary key autoincrement, "
			+ COLUMN_MOBILE + " text not null, "
			+ COLUMN_P + " text, "
			+ COLUMN_DOMAIN + " text, "
			+ COLUMN_PATH + " text, "
			+ COLUMN_LOGINTIME + " integer, "
			+ COLUMN_EXPIRE + " integer"
			+ ");";
			
	
	public static void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
	}
	
	public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
		
		database.execSQL("DROP TABLE IF EXISTS " + TABLE_COOKIE);
		onCreate(database);
	}
}
