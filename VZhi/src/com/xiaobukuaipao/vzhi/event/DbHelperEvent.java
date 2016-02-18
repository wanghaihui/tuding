package com.xiaobukuaipao.vzhi.event;

public class DbHelperEvent {
	private final String dbHelper;
	
	public DbHelperEvent(final String dbHelper) {
		this.dbHelper = dbHelper;
	}
	
	public String getDbHelper() {
		return dbHelper;
	}
}
