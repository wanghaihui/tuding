package com.xiaobukuaipao.vzhi.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class MessageTable {
	
	public static final String TAG = MessageTable.class.getSimpleName();

	// 消息主表
	public static final String TABLE_MESSAGES =  "messages";
	
	// 自带的自增序列Id
	public static final String COLUMN_ID = "_id";
	// 消息存储唯一ID
	// mid
	public static final String COLUMN_MESSAGE_ID = "msgId";
	// 本条消息所在的对话ID
	// did
	public static final String COLUMN_MESSAGE_DIALOG_ID = "msgDid";
	// 消息存储ID，当图文混排时，该字段有用，为该图文消息的第一条消息的ID，否则为-1
    public static final String COLUMN_MESSAGE_PARENT_ID = "msgParentId";
    
    // topic id
    public static final String COLUMN_MESSAGE_TOPIC_ID = "topicId";
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
    // 消息摘要
    public static final String COLUMN_MESSAGE_SUMMARY = "summary";
    // 消息预览
    // body
    public static final String COLUMN_MESSAGE_OVERVIEW = "overview";
    // 消息加载状态--0表示正在加载，1表示加载完毕
    public static final String COLUMN_MESSAGE_STATUS = "status";
    // 消息是否已读或已展现
    public static final String COLUMN_MESSAGE_READ_STATUS = "readstatus";
    // 创建时间列
    // createtime
    public static final String COLUMN_CREATED = "created";
    // 更新时间列
    public static final String COLUMN_UPDATED = "updated";
    
    // 已读时间
    public static final String COLUMN_READTIME = "readtime";
    
    // 此消息是否是一个断点
    public static final String COLUMN_INTERRUPT = "interrupt";
    
    // 数据库更新时，表名后缀
    public static final String TEMP_SUFFIX = "_temp_suffix";
    
    // Database Creation SQL Statement
    // 创建数据库语句
    public static final String CREATE_MESSAGES =
            "CREATE TABLE IF NOT EXISTS " + TABLE_MESSAGES + " ("
                    + COLUMN_ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
                    + COLUMN_MESSAGE_ID + " INTEGER NOT NULL, "
                    + COLUMN_MESSAGE_DIALOG_ID + " INTEGER NOT NULL, "
                    + COLUMN_MESSAGE_PARENT_ID + " INTEGER NOT NULL DEFAULT -1, "
                    + COLUMN_MESSAGE_TOPIC_ID + " INTEGER, "
                    + COLUMN_MESSAGE_FROM_USER_ID + " VARCHAR(20), "
                    + COLUMN_MESSAGE_TO_USER_ID + " VARCHAR(20), "
                    + COLUMN_MESSAGE_TYPE + " INTEGER NOT NULL DEFAULT 1, "
                    + COLUMN_MESSAGE_DISPLAY_TYPE + " INTEGER NOT NULL DEFAULT 1, "
                    + COLUMN_MESSAGE_SUMMARY + " VARCHAR(50), "
                    + COLUMN_MESSAGE_OVERVIEW + " VARCHAR(1024), "
                    + COLUMN_MESSAGE_STATUS + " INTEGER NOT NULL DEFAULT 0, "
                    + COLUMN_MESSAGE_READ_STATUS + " INTEGER NOT NULL DEFAULT 0, "
                    + COLUMN_CREATED + " INTEGER DEFAULT 0, "
                    + COLUMN_UPDATED + " INTEGER DEFAULT 0, "
                    + COLUMN_READTIME + " INTEGER DEFAULT 0, "
                    + COLUMN_INTERRUPT + " INTEGER DEFAULT 0"
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
	
	@SuppressWarnings("unused")
	private static void updateTable(SQLiteDatabase database) {
		// 此时进行数据库的更新和升级
		database.beginTransaction();
		   try {
			   
			   // rename the table
	            String alterMessageTableSql = getAlterTableSql(TABLE_MESSAGES);
	            database.execSQL(alterMessageTableSql);
	            
	            // creat table
	            // 升级时改成新表名
	            database.execSQL(CREATE_MESSAGES);
	            // 记得用旧表名
	            String selectColumns = getColumnNames(database, TABLE_MESSAGES + TEMP_SUFFIX);
	            String updateMessagesSql = "insert into " + TABLE_MESSAGES + " ("
	                    + selectColumns + ") "
	                    + "select " + selectColumns + "" + " " + " from " + TABLE_MESSAGES
	                    + TEMP_SUFFIX;
	            database.equals(updateMessagesSql);
	            
	         // drop the oldtable
	            String dropMessageTableSql = getDropTableSql(TABLE_MESSAGES + TEMP_SUFFIX);
	            database.execSQL(dropMessageTableSql);
	            
			   database.setTransactionSuccessful();
		   } finally {
			   database.endTransaction();
		   }
	}
	
	private static String getAlterTableSql(String oldTableName) {
		// ALTER TABLE来重新命名现有的表的基本语法如下：ALTER TABLE database_name.table_name RENAME TO new_table_name;
        return "alter table " + oldTableName + " rename to " + oldTableName + TEMP_SUFFIX;
    }
	
	private static String getDropTableSql(String tableName) {
        return "drop table if exists " + tableName;
    }
	
	// 获取升级前表中的字段
    private static String getColumnNames(SQLiteDatabase db, String tableName)
    {
        StringBuffer sbSelect = null;
        Cursor c = null;
        try {
            c = db.rawQuery("PRAGMA table_info(" + tableName + ")", null);
            
            if (null != c) {
                int columnIndex = c.getColumnIndex("name");
                if (-1 == columnIndex) {
                    return null;
                }

                int index = 0;
                // 并标记最后一个不加逗号，
                // 由于index从0开始，所有这里不用加2,只加1
                int pos = c.getCount() + 1;
                // 字段总列数，增加2列需要加2
                sbSelect = new StringBuffer(c.getCount() + 2);
                for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                    sbSelect.append(c.getString(columnIndex));
                    index++;
                    if (index < pos) {
                        sbSelect.append(",");
                    }
                }
            }
        } catch (Exception e) {
        	
        } finally {
            if (null != c) {
                c.close();
            }
        }

        return sbSelect.toString();
    }
	
}
