package com.xiaobukuaipao.vzhi.database;

import android.database.sqlite.SQLiteDatabase;

/**
 * 消息列表--对应的表
 * @author xiaobu
 */
public class MessageSummaryTable {

	public static final String TAG = MessageSummaryTable.class.getSimpleName();

	// 消息列表摘要
	public static final String TABLE_MESSAGE_SUMMARY =  "messageSummary";
	
	// 自带的自增序列Id
	public static final String COLUMN_ID = "_id";
	// 本条消息所在的对话ID
	// did--与个人聊天的did
	public static final String COLUMN_MESSAGE_DIALOG_ID = "msgDid";
	
	// gid--与群组聊天的gid
	public static final String COLUMN_MESSAGE_GROUP_ID = "msgGid";
    
    // 消息发送者ID
    // sender
    public static final String COLUMN_MESSAGE_FROM_USER_ID = "fromUserId";
	// 消息接受者ID
    // receiver
    public static final String COLUMN_MESSAGE_TO_USER_ID = "toUserId";
    // 消息类型 图文消息 1
    // 消息展示类型
    // 文本, 图片, 语音, 名片, 邀约, 简历
    public static final String COLUMN_MESSAGE_TYPE = "msgType";
    // 具体的显示类型
    public static final String COLUMN_MESSAGE_DISPLAY_TYPE = "displayType";
    // 消息预览
    // body
    public static final String COLUMN_MESSAGE_OVERVIEW = "overview";
    // 消息加载状态
    public static final String COLUMN_MESSAGE_STATUS = "status";
    // 消息是否已读或已展现
    public static final String COLUMN_MESSAGE_READ_STATUS = "readstatus";
    // 创建时间列
    // createtime
    public static final String COLUMN_CREATED = "created";
    // 更新时间列 -- 根据更新时间进行排序
    public static final String COLUMN_UPDATED = "updated";
    
    // 未读数
    public static final String COLUMN_UNREADCOUNT = "unreadcount";
    
    public static final String COLUMN_CHANGED = "changed";
    
    // 数据库更新时，表名后缀
    public static final String TEMP_SUFFIX = "_temp_suffix";
    
    // Database Creation SQL Statement
    // 创建数据库语句
    public static final String CREATE_MESSAGES =
            "CREATE TABLE IF NOT EXISTS " + TABLE_MESSAGE_SUMMARY + " ("
                    + COLUMN_ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
                    + COLUMN_MESSAGE_DIALOG_ID + " INTEGER, "
                    + COLUMN_MESSAGE_GROUP_ID + " INTEGER, "
                    + COLUMN_MESSAGE_FROM_USER_ID + " VARCHAR(20), "
                    + COLUMN_MESSAGE_TO_USER_ID + " VARCHAR(20), "
                    + COLUMN_MESSAGE_TYPE + " INTEGER NOT NULL DEFAULT 1, "
                    + COLUMN_MESSAGE_DISPLAY_TYPE + " INTEGER NOT NULL DEFAULT 1, "
                    + COLUMN_MESSAGE_OVERVIEW + " VARCHAR(1024), "
                    + COLUMN_MESSAGE_STATUS + " INTEGER NOT NULL DEFAULT 0, "
                    + COLUMN_MESSAGE_READ_STATUS + " INTEGER NOT NULL DEFAULT 0, "
                    + COLUMN_CREATED + " INTEGER DEFAULT 0, "
                    + COLUMN_UPDATED + " INTEGER DEFAULT 0, "
                    + COLUMN_UNREADCOUNT + " INTEGER DEFAULT 0, "
                    + COLUMN_CHANGED + " INTEGER DEFAULT 0"
                    +");";
    
    public static void onCreate(SQLiteDatabase database) {
		database.execSQL(CREATE_MESSAGES);
	}
	
	public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		// 更改数据库版本的操作 暂时先直接删除旧表
		//Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
		database.execSQL("DROP TABLE IF EXISTS " + CREATE_MESSAGES);
		onCreate(database);
		
		// 升级，此时先创建新表，然后保存旧表的数据之后，最后删除旧表
		// 由于这里可能会涉及到数据库的更新，所以应该特别注意
		// updateTable(database);
	}
}
