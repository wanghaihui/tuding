package com.xiaobukuaipao.vzhi.database;

import android.database.sqlite.SQLiteDatabase;

public class ContactUserTable {
	public static final String TAG = ContactUserTable.class.getSimpleName();
	
	// 消息列表摘要
	public static final String TABLE_CONTACT_USER =  "contactUser";
	
	// 自带的自增序列Id
	public static final String COLUMN_ID = "_id";
	
	// 联系人的uid
	public static final String COLUMN_UID = "uid";
	
	// 针对单聊did--此联系人在单聊中的对应的did
	public static final String COLUMN_DID = "did";
	
	// 群组的gid
	public static final String COLUMN_GID = "gid";
	
	// 联系人的名字
	public static final String COLUMN_NAME = "name";
	// 联系人的昵称
	public static final String COLUMN_NICKNAME = "nickname";
	
	// 联系人的头像url
	// 实名头像
	public static final String COLUMN_AVATAR = "avatar";
	
	// 匿名头像
	public static final String COLUMN_NICKAVATAR = "nickAvatar";
	
	// 此头像存储在本地的路径
	public static final String COLUMN_AVATAR_FILE_PATH = "avatarPath";
	
	// 实名还是匿名
	// 1是匿名，0是实名
	public static final String COLUMN_ISREAL = "isreal";
	
	public static final String COLUMN_ISGROUP = "isgroup";
	
	// Database Creation SQL Statement
    // 创建数据库语句
    public static final String CREATE_MESSAGES =
            "CREATE TABLE IF NOT EXISTS " + TABLE_CONTACT_USER + " ("
                    + COLUMN_ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
                    + COLUMN_UID + " INTEGER, "
                    + COLUMN_DID + " INTEGER, "
                    + COLUMN_GID + " INTEGER, "
                    + COLUMN_NAME + " VARCHAR(20), "
                    + COLUMN_NICKNAME + " VARCHAR(20), "
                    + COLUMN_AVATAR + " VARCHAR(20), "
                    + COLUMN_NICKAVATAR + " VARCHAR(20), "
                    + COLUMN_AVATAR_FILE_PATH + " VARCHAR(20), "
                    + COLUMN_ISREAL + " INTEGER DEFAULT 0, "
                    + COLUMN_ISGROUP + " INTEGER DEFAULT 0"
                    +");";
    
    public static void onCreate(SQLiteDatabase database) {
		database.execSQL(CREATE_MESSAGES);
	}
	
	public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		// 更改数据库版本的操作 暂时先直接删除旧表
		// Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
		database.execSQL("DROP TABLE IF EXISTS " + CREATE_MESSAGES);
		onCreate(database);
		
		// 升级，此时先创建新表，然后保存旧表的数据之后，最后删除旧表
		// 由于这里可能会涉及到数据库的更新，所以应该特别注意
		// updateTable(database);
	}
	
}
