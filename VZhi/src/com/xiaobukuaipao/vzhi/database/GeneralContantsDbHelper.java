package com.xiaobukuaipao.vzhi.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class GeneralContantsDbHelper extends SQLiteOpenHelper{
	// Database table
	public static final String TABLE_CONSTANCE = "constance";
	
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_UID = "uid";
	public static final String COLUMN_TIME = "time";
	// Database Creation SQL Statement
	private static final String DATABASE_CREATE = "create table "
			+ TABLE_CONSTANCE
			+ "(" 
			+ COLUMN_ID + " integer primary key autoincrement, "
			+ COLUMN_UID + " text not null,"
			+ COLUMN_TIME + "text"
			+ ");";
	private static final String DATABASE_DROP = "DROP TABLE IF EXISTS " 
			+ TABLE_CONSTANCE;
	// 数据库名字
	private static final String DATABASE_NAME = "constants.db";
	// 数据库的版本号--当数据库升级时，该字段+1
	private static final int DATABASE_VERSION = 1;
	
	
	public GeneralContantsDbHelper(Context context) {
		this(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	 
	private GeneralContantsDbHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DATABASE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
		db.execSQL(DATABASE_DROP);
		db.execSQL(DATABASE_CREATE);
	}

	public void insert(String uid, String time){
		ContentValues values = new ContentValues();
		values.put(COLUMN_ID, uid);
		values.put(COLUMN_TIME, time);
		getWritableDatabase().insertOrThrow(TABLE_CONSTANCE, null, values);
	}
	
	public String  getUid(){
		
		return null;
	}
}
