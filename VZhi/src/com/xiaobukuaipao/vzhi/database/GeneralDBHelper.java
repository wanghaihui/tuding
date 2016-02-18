package com.xiaobukuaipao.vzhi.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import com.xiaobukuaipao.vzhi.util.Logcat;

public class GeneralDBHelper extends SQLiteOpenHelper {
	
	
	// 数据库名字
	// private static final String DATABASE_NAME = "tuding.db";
	private static final String TAG = "GeneralDBHelper";
	
	// 数据库的版本号--当数据库升级时，该字段+1
	private static final int DATABASE_VERSION = 1;
	
	
	// 在这里创建数据库
	public GeneralDBHelper(Context context, String dName) {
		super(context,dName, null, DATABASE_VERSION);
	}
	
	/**
	 * 构造网
	 * @param context 上下文
	 * @param name 数据库名字
	 * @param factory 可选的数据库游标工厂类，当查询(query)被提交时，该对象会被调用来实例化一个游标
	 * @param version 数据库版本
	 * @param errorHandler
	 */
	public GeneralDBHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
	}
	

	// Method is called during creation of the database
	@Override
	public void onCreate(SQLiteDatabase database) {
		CookieTable.onCreate(database);
		UserInfoTable.onCreate(database);
		MessageTable.onCreate(database);
		MessageSummaryTable.onCreate(database);
		ContactUserTable.onCreate(database);
		MessageGroupTable.onCreate(database);
		SuggestTable.onCreate(database);
	}

	// Method is called during an upgrade of the database
	// e.g. if you increase the database version
	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		CookieTable.onUpgrade(database, oldVersion, newVersion);
		UserInfoTable.onUpgrade(database, oldVersion, newVersion);
		MessageTable.onUpgrade(database, oldVersion, newVersion);
		MessageSummaryTable.onUpgrade(database, oldVersion, newVersion);
		ContactUserTable.onUpgrade(database, oldVersion, newVersion);
		MessageGroupTable.onUpgrade(database, oldVersion, newVersion);
		SuggestTable.onUpgrade(database, oldVersion, newVersion);
	}

	@Override
	public SQLiteDatabase getReadableDatabase() {
		return super.getReadableDatabase();
	}

	@Override
	public void onOpen(SQLiteDatabase db) {
		super.onOpen(db);
		
		Logcat.d(TAG, "database open");
	}	
	
	
}
