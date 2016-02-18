package com.xiaobukuaipao.vzhi.database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class UserInfoTable {

	public static final String TAG = UserInfoTable.class.getSimpleName();
	// Database table
	public static final String TABLE_USERINFO =  "userinfo";
	
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_MOBILE = "mobile";
	public static final String COLUMN_PASSWORD = "password";
	public static final String COLUMN_NAME = "name";
	public static final String COLUMN_NICKNAME = "nickname";
	public static final String COLUMN_GENDER = "gender";
	public static final String COLUMN_AGE = "age";
	public static final String COLUMN_AVATAR = "avatar";
	public static final String COLUMN_REALAVATAR = "realavatar";
	public static final String COLUMN_LOCATION = "location";
	public static final String COLUMN_EMAIL = "email";
	public static final String COLUMN_IDENTITY = "identity";
	public static final String COLUMN_WORKEXPR = "workexpr";
	
	// Database Creation SQL Statement
	private static final String DATABASE_CREATE = "create table "
			+ TABLE_USERINFO
			+ "(" 
			+ COLUMN_ID + " integer primary key autoincrement, "
			+ COLUMN_MOBILE + " text not null, "
			+ COLUMN_PASSWORD + " text, "
			+ COLUMN_NAME + " text, "
			+ COLUMN_NICKNAME + " text, "
			+ COLUMN_IDENTITY + " integer, "
			+ COLUMN_GENDER + " integer, "
			+ COLUMN_AGE + " integer, "
			+ COLUMN_AVATAR + " text, "
			+ COLUMN_REALAVATAR + " text, "
			+ COLUMN_LOCATION + " text, "
			+ COLUMN_EMAIL + " text, "
			+ COLUMN_WORKEXPR + " integer DEFAULT 0 "
			+ ");";
			
	
	public static void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
	}
	
	public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
		
		database.execSQL("DROP TABLE IF EXISTS " + TABLE_USERINFO);
		onCreate(database);
	}
}
