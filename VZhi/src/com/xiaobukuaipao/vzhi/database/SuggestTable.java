package com.xiaobukuaipao.vzhi.database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * 存储一些服务器拉取过来的suggest列表
 * 
 * @author hongxin.bai
 *	@time 2014-12-17 11:08
 */
public class SuggestTable {

	public static final String TAG = SuggestTable.class.getSimpleName();
	// Database table
	public static final String TABLE_SUGGEST =  "suggest";
	
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_TYPE = "type";
	public static final String COLUMN_TYPE_ID = "type_id";
	public static final String COLUMN_TYPE_NAME = "type_name";
	public static final String COLUMN_TYPE_SUBNAME = "type_subname";
	
	// Database Creation SQL Statement
	private static final String DATABASE_CREATE = "create table "
			+ TABLE_SUGGEST
			+ "(" 
			+ COLUMN_ID + " integer primary key autoincrement, "
			+ COLUMN_TYPE + " integer not null,"
			+ COLUMN_TYPE_ID + " integer, "
			+ COLUMN_TYPE_NAME + " text,"
			+ COLUMN_TYPE_SUBNAME + " text"
			+ ");";
			
	
	public static void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
	}
	
	public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
		if(newVersion > oldVersion){
			database.execSQL("DROP TABLE IF EXISTS " + TABLE_SUGGEST);
			onCreate(database);
		}
	}
}
