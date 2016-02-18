package com.xiaobukuaipao.vzhi.im;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.xiaobukuaipao.vzhi.ChatGroupActivity;
import com.xiaobukuaipao.vzhi.ChatPersonActivity;
import com.xiaobukuaipao.vzhi.contentprovider.GeneralContentProvider;
import com.xiaobukuaipao.vzhi.database.ContactUserTable;
import com.xiaobukuaipao.vzhi.database.CookieTable;
import com.xiaobukuaipao.vzhi.database.MessageGroupTable;
import com.xiaobukuaipao.vzhi.database.MessageSummaryTable;
import com.xiaobukuaipao.vzhi.database.MessageTable;
import com.xiaobukuaipao.vzhi.event.GroupMessageEvent;
import com.xiaobukuaipao.vzhi.event.MessageSummaryChangeEvent;
import com.xiaobukuaipao.vzhi.event.P2PMessageEvent;
import com.xiaobukuaipao.vzhi.util.GlobalConstants;
import com.xiaobukuaipao.vzhi.util.StringUtils;

import de.greenrobot.event.EventBus;

public class ImDbManager extends ImManager {

	public static final String TAG = ImDbManager.class.getSimpleName();

	private static final ImDbManager dbManager = new ImDbManager();
	
	// private GeneralEventLogic mGeneralEventLogic;
	
	private ImDbManager() {
		// mGeneralEventLogic = new GeneralEventLogic();
	}
	
	public static ImDbManager getInstance() {
		return dbManager;
	}
	
	/**
	 * 保存数据到数据库中--同步方法--枷锁
	 * @param message
	 */
	public synchronized void saveTextMessageToDatabase(String message) {
		long uid = 0;
		
		Cursor cookieCursor = context.getContentResolver().query(GeneralContentProvider.COOKIE_CONTENT_URI, null, null, null, null);
		if (cookieCursor != null && cookieCursor.moveToFirst()) {
			uid = (long) cookieCursor.getInt(cookieCursor.getColumnIndex(CookieTable.COLUMN_ID));
			cookieCursor.close();
		}
		
		JSONObject jsonObject = (JSONObject) JSONObject.parse(message);
		
		ContentValues values = new ContentValues();
		values.put(MessageTable.COLUMN_MESSAGE_ID, jsonObject.getLong(GlobalConstants.JSON_MID));
		values.put(MessageTable.COLUMN_MESSAGE_DIALOG_ID, jsonObject.getLong(GlobalConstants.JSON_DID));
		values.put(MessageTable.COLUMN_MESSAGE_FROM_USER_ID, jsonObject.getString(GlobalConstants.JSON_SENDER));
		values.put(MessageTable.COLUMN_MESSAGE_TO_USER_ID, jsonObject.getString(GlobalConstants.JSON_RECEIVER));
		values.put(MessageTable.COLUMN_MESSAGE_TYPE, jsonObject.getInteger(GlobalConstants.JSON_TYPE));
		values.put(MessageTable.COLUMN_MESSAGE_DISPLAY_TYPE, ImUtils.matchDisplayType(jsonObject.getInteger(GlobalConstants.JSON_TYPE)));
		
		values.put(MessageTable.COLUMN_MESSAGE_SUMMARY, jsonObject.getString(GlobalConstants.JSON_SUMMARY));
		values.put(MessageTable.COLUMN_MESSAGE_OVERVIEW, jsonObject.getString(GlobalConstants.JSON_BODY));
		values.put(MessageTable.COLUMN_CREATED, jsonObject.getLong(GlobalConstants.JSON_CREATETIME));
		
		if (jsonObject.getInteger(GlobalConstants.JSON_READSTATUS) != null) {
			values.put(MessageTable.COLUMN_MESSAGE_READ_STATUS, jsonObject.getInteger(GlobalConstants.JSON_READSTATUS));
		}
		// 是否显示ProgressBar
		if (jsonObject.getString(GlobalConstants.JSON_SENDER).equals(String.valueOf(uid))) {
			values.put(MessageTable.COLUMN_MESSAGE_STATUS, 1);
		}
		
		if (jsonObject.getLong(GlobalConstants.JSON_READTIME) != null) {
			values.put(MessageTable.COLUMN_READTIME, jsonObject.getLong(GlobalConstants.JSON_READTIME));
		}
		
		EventBus.getDefault().post(new P2PMessageEvent(1));
		
		// 插入数据库
		context.getContentResolver().insert(GeneralContentProvider.MESSAGE_CONTENT_URI, values);
		values.clear();
		
		Log.i(TAG, "save text : 1");
		
		// mGeneralEventLogic.getInstance().post(new P2PMessageEvent("1"));
	}
	
	/**
	 * 更新数据库信息
	 */
	public synchronized void updateMessageToDatabase(String message) {
		
		long uid = 0;
		Cursor cookieCursor = context.getContentResolver().query(GeneralContentProvider.COOKIE_CONTENT_URI, null, null, null, null);
		if (cookieCursor != null && cookieCursor.moveToNext()) {
			uid = (long) cookieCursor.getInt(cookieCursor.getColumnIndex(CookieTable.COLUMN_ID));
			cookieCursor.close();
		}
		
		
		JSONObject jsonObject = (JSONObject) JSONObject.parse(message);
		
		long mid = jsonObject.getLong(GlobalConstants.JSON_MID);
		long did = jsonObject.getLong(GlobalConstants.JSON_DID);
		
		long readstatus = jsonObject.getInteger(GlobalConstants.JSON_READSTATUS);
		long readtime = jsonObject.getLong(GlobalConstants.JSON_READTIME);
		
		ContentValues values = new ContentValues();
		values.put(MessageTable.COLUMN_MESSAGE_READ_STATUS, readstatus);
		values.put(MessageTable.COLUMN_READTIME, readtime);
		
		Cursor cursor = context.getContentResolver().query(GeneralContentProvider.MESSAGE_CONTENT_URI, null, 
				MessageTable.COLUMN_MESSAGE_DIALOG_ID + " = ?" + " AND " + MessageTable.COLUMN_MESSAGE_READ_STATUS + " = ?" + " AND " + MessageTable.COLUMN_MESSAGE_FROM_USER_ID + " = ?", 
				new String[] {String.valueOf(did), String.valueOf(0), String.valueOf(uid)}, MessageTable.COLUMN_MESSAGE_ID);
		
		long updateMid = 0;
		long updateDid = 0;
		// 遍历
		if (cursor != null && cursor.moveToFirst()) {
			do {
				updateMid = cursor.getInt(cursor .getColumnIndexOrThrow(MessageTable.COLUMN_MESSAGE_ID));
				updateDid = cursor.getInt(cursor .getColumnIndexOrThrow(MessageTable.COLUMN_MESSAGE_DIALOG_ID));
				// readstatus == 1
				values.put(MessageTable.COLUMN_MESSAGE_READ_STATUS, 1);
				
				context.getContentResolver().update(GeneralContentProvider.MESSAGE_CONTENT_URI, values, 
						MessageTable.COLUMN_MESSAGE_ID + " = ?" + " AND " + MessageTable.COLUMN_MESSAGE_DIALOG_ID + " = ?", 
						new String[] {String.valueOf(updateMid), String.valueOf(updateDid)});
				values.clear();
				
			} while (cursor.moveToNext());
			
			cursor.close();
		}
		
		EventBus.getDefault().post(new P2PMessageEvent(2));
	}
	
	/**
	 * 插入或者更新消息列表的数据库
	 * @author xiaobu
	 */
	public synchronized void insertOrUpdateStrangerMessageListToDatabase(String message) {
		JSONObject jsonObject = (JSONObject) JSONObject.parse(message);
		
		ContentValues values = new ContentValues();
		values.put(MessageSummaryTable.COLUMN_MESSAGE_TYPE, jsonObject.getLong(GlobalConstants.JSON_TYPE));
		values.put(MessageSummaryTable.COLUMN_MESSAGE_OVERVIEW, jsonObject.getString(GlobalConstants.JSON_BODY));
		values.put(MessageSummaryTable.COLUMN_UPDATED, jsonObject.getLong(GlobalConstants.JSON_CREATETIME));
		values.put(MessageSummaryTable.COLUMN_MESSAGE_FROM_USER_ID, jsonObject.getLong(GlobalConstants.JSON_SENDER));
		values.put(MessageSummaryTable.COLUMN_MESSAGE_TO_USER_ID, jsonObject.getLong(GlobalConstants.JSON_RECEIVER));
		
		// display type
		values.put(MessageSummaryTable.COLUMN_MESSAGE_DISPLAY_TYPE, IMConstants.DISPLAY_LIST_TYPE_STRANGERLETTER);
		
		// 首先查表判断该消息是否存在
		Cursor cursor = context.getContentResolver().query(GeneralContentProvider.MESSAGE_SUMMARY_CONTENT_URI, null, 
				MessageSummaryTable.COLUMN_MESSAGE_DISPLAY_TYPE + " = ?", new String[] { String.valueOf(IMConstants.DISPLAY_LIST_TYPE_STRANGERLETTER) }, null);
		
		if (cursor != null && cursor.moveToFirst()) {
			// 此时，消息已经存在--执行更新操作
			long unRead = cursor.getLong(cursor.getColumnIndex(MessageSummaryTable.COLUMN_UNREADCOUNT));
			// unread
			values.put(MessageSummaryTable.COLUMN_UNREADCOUNT, unRead + 1);
			context.getContentResolver().update(GeneralContentProvider.MESSAGE_SUMMARY_CONTENT_URI, values, 
					MessageSummaryTable.COLUMN_MESSAGE_DISPLAY_TYPE + " = ?", new String[] { String.valueOf(IMConstants.DISPLAY_LIST_TYPE_STRANGERLETTER) });
			cursor.close();
		} else {
			// 消息不存在，执行插入操作
			values.put(MessageSummaryTable.COLUMN_UNREADCOUNT, 1);
			context.getContentResolver().insert(GeneralContentProvider.MESSAGE_SUMMARY_CONTENT_URI, values);
		}
		
		values.clear();
	}
	
	/**
	 * 职位推荐
	 * @author xiaobu
	 */
	public synchronized void insertOrUpdateJobRecommendMessageListToDatabase(String message) {
		JSONObject jsonObject = (JSONObject) JSONObject.parse(message);
		
		ContentValues values = new ContentValues();
		values.put(MessageSummaryTable.COLUMN_MESSAGE_TYPE, jsonObject.getLong(GlobalConstants.JSON_TYPE));
		values.put(MessageSummaryTable.COLUMN_MESSAGE_OVERVIEW, jsonObject.getString(GlobalConstants.JSON_BODY));
		values.put(MessageSummaryTable.COLUMN_UPDATED, jsonObject.getLong(GlobalConstants.JSON_CREATETIME));
		values.put(MessageSummaryTable.COLUMN_MESSAGE_FROM_USER_ID, jsonObject.getLong(GlobalConstants.JSON_SENDER));
		values.put(MessageSummaryTable.COLUMN_MESSAGE_TO_USER_ID, jsonObject.getLong(GlobalConstants.JSON_RECEIVER));
		
		// display type
		values.put(MessageSummaryTable.COLUMN_MESSAGE_DISPLAY_TYPE, IMConstants.DISPLAY_LIST_TYPE_JOBRECOMMEND);
		
		// 首先查表判断该消息是否存在
		Cursor cursor = context.getContentResolver().query(GeneralContentProvider.MESSAGE_SUMMARY_CONTENT_URI, null, 
				MessageSummaryTable.COLUMN_MESSAGE_DISPLAY_TYPE + " = ?", new String[] { String.valueOf(IMConstants.DISPLAY_LIST_TYPE_JOBRECOMMEND) }, null);
		
		if (cursor != null && cursor.moveToFirst()) {
			// 此时，消息已经存在--执行更新操作
			long unRead = cursor.getLong(cursor.getColumnIndex(MessageSummaryTable.COLUMN_UNREADCOUNT));
			// unread
			values.put(MessageSummaryTable.COLUMN_UNREADCOUNT, unRead + 1);
			context.getContentResolver().update(GeneralContentProvider.MESSAGE_SUMMARY_CONTENT_URI, values, 
					MessageSummaryTable.COLUMN_MESSAGE_DISPLAY_TYPE + " = ?", new String[] { String.valueOf(IMConstants.DISPLAY_LIST_TYPE_JOBRECOMMEND) });
			cursor.close();
		} else {
			// 消息不存在，执行插入操作
			values.put(MessageSummaryTable.COLUMN_UNREADCOUNT, 1);
			context.getContentResolver().insert(GeneralContentProvider.MESSAGE_SUMMARY_CONTENT_URI, values);
		}
		
		values.clear();
	}
	
	/**
	 * HR接收到投递提醒消息
	 * @author xiaobu
	 */
	public synchronized void insertOrUpdateJobApplyMessageListToDatabase(String message) {
		JSONObject jsonObject = (JSONObject) JSONObject.parse(message);
		
		ContentValues values = new ContentValues();
		values.put(MessageSummaryTable.COLUMN_MESSAGE_TYPE, jsonObject.getLong(GlobalConstants.JSON_TYPE));
		values.put(MessageSummaryTable.COLUMN_MESSAGE_OVERVIEW, jsonObject.getString(GlobalConstants.JSON_BODY));
		values.put(MessageSummaryTable.COLUMN_UPDATED, jsonObject.getLong(GlobalConstants.JSON_CREATETIME));
		values.put(MessageSummaryTable.COLUMN_MESSAGE_FROM_USER_ID, jsonObject.getLong(GlobalConstants.JSON_SENDER));
		values.put(MessageSummaryTable.COLUMN_MESSAGE_TO_USER_ID, jsonObject.getLong(GlobalConstants.JSON_RECEIVER));
		
		// display type
		values.put(MessageSummaryTable.COLUMN_MESSAGE_DISPLAY_TYPE, IMConstants.DISPLAY_LIST_TYPE_JOBAPPLY);
		
		// 首先查表判断该消息是否存在
		Cursor cursor = context.getContentResolver().query(GeneralContentProvider.MESSAGE_SUMMARY_CONTENT_URI, null, 
				MessageSummaryTable.COLUMN_MESSAGE_DISPLAY_TYPE + " = ?", new String[] { String.valueOf(IMConstants.DISPLAY_LIST_TYPE_JOBAPPLY) }, null);
		
		if (cursor != null && cursor.moveToFirst()) {
			// 此时，消息已经存在--执行更新操作
			long unRead = cursor.getLong(cursor.getColumnIndex(MessageSummaryTable.COLUMN_UNREADCOUNT));
			// unread
			values.put(MessageSummaryTable.COLUMN_UNREADCOUNT, unRead + 1);
			context.getContentResolver().update(GeneralContentProvider.MESSAGE_SUMMARY_CONTENT_URI, values, 
					MessageSummaryTable.COLUMN_MESSAGE_DISPLAY_TYPE + " = ?", new String[] { String.valueOf(IMConstants.DISPLAY_LIST_TYPE_JOBAPPLY) });
			cursor.close();
		} else {
			// 消息不存在，执行插入操作
			values.put(MessageSummaryTable.COLUMN_UNREADCOUNT, 1);
			context.getContentResolver().insert(GeneralContentProvider.MESSAGE_SUMMARY_CONTENT_URI, values);
		}
		
		values.clear();
	}
	
	/**
	 * 群推荐消息
	 * @author xiaobu
	 */
	public synchronized void insertOrUpdateGroupRecommendMessageListToDatabase(String message) {
		JSONObject jsonObject = (JSONObject) JSONObject.parse(message);
		
		ContentValues values = new ContentValues();
		values.put(MessageSummaryTable.COLUMN_MESSAGE_TYPE, jsonObject.getLong(GlobalConstants.JSON_TYPE));
		values.put(MessageSummaryTable.COLUMN_MESSAGE_OVERVIEW, jsonObject.getString(GlobalConstants.JSON_BODY));
		values.put(MessageSummaryTable.COLUMN_UPDATED, jsonObject.getLong(GlobalConstants.JSON_CREATETIME));
		values.put(MessageSummaryTable.COLUMN_MESSAGE_TO_USER_ID, jsonObject.getLong(GlobalConstants.JSON_RECEIVER));
		
		// display type
		values.put(MessageSummaryTable.COLUMN_MESSAGE_DISPLAY_TYPE, IMConstants.DISPLAY_LIST_TYPE_GROUPRECOMMEND);
		
		// 首先查表判断该消息是否存在
		Cursor cursor = context.getContentResolver().query(GeneralContentProvider.MESSAGE_SUMMARY_CONTENT_URI, null, 
				MessageSummaryTable.COLUMN_MESSAGE_DISPLAY_TYPE + " = ?", new String[] { String.valueOf(IMConstants.DISPLAY_LIST_TYPE_GROUPRECOMMEND) }, null);
		
		if (cursor != null && cursor.moveToFirst()) {
			// 此时，消息已经存在--执行更新操作
			long unRead = cursor.getLong(cursor.getColumnIndex(MessageSummaryTable.COLUMN_UNREADCOUNT));
			// unread
			values.put(MessageSummaryTable.COLUMN_UNREADCOUNT, unRead + 1);
			context.getContentResolver().update(GeneralContentProvider.MESSAGE_SUMMARY_CONTENT_URI, values, 
					MessageSummaryTable.COLUMN_MESSAGE_DISPLAY_TYPE + " = ?", new String[] { String.valueOf(IMConstants.DISPLAY_LIST_TYPE_GROUPRECOMMEND) });
			cursor.close();
		} else {
			// 消息不存在，执行插入操作
			values.put(MessageSummaryTable.COLUMN_UNREADCOUNT, 1);
			context.getContentResolver().insert(GeneralContentProvider.MESSAGE_SUMMARY_CONTENT_URI, values);
		}
		
		values.clear();
	}
	
	public interface OnReceiveSocketMessageListener {
		public void onReceiveSocketMessage();
	}
	
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * 保存群组消息
	 * @param message
	 */
	public synchronized void saveGroupTextMessageToDatabase (String message) {
		JSONObject jsonObject = (JSONObject) JSONObject.parse(message);
		
		ContentValues values = new ContentValues();
		values.put(MessageGroupTable.COLUMN_MESSAGE_ID, jsonObject.getLong(GlobalConstants.JSON_MID));
		values.put(MessageGroupTable.COLUMN_MESSAGE_GROUP_ID, jsonObject.getLong(GlobalConstants.JSON_GID));
		
		values.put(MessageGroupTable.COLUMN_MESSAGE_FROM_USER_ID, jsonObject.getString(GlobalConstants.JSON_SENDER));
		
		values.put(MessageGroupTable.COLUMN_MESSAGE_TYPE, jsonObject.getInteger(GlobalConstants.JSON_TYPE));
		values.put(MessageGroupTable.COLUMN_MESSAGE_DISPLAY_TYPE, ImUtils.matchDisplayGroupType(jsonObject.getInteger(GlobalConstants.JSON_TYPE)));
		
		values.put(MessageGroupTable.COLUMN_MESSAGE_SUMMARY, jsonObject.getString(GlobalConstants.JSON_SUMMARY));
		values.put(MessageGroupTable.COLUMN_MESSAGE_OVERVIEW, jsonObject.getString(GlobalConstants.JSON_BODY));
		
		values.put(MessageGroupTable.COLUMN_CREATED, jsonObject.getLong(GlobalConstants.JSON_CREATETIME));
		
		// 插入数据库
		context.getContentResolver().insert(GeneralContentProvider.MESSAGE_GROUP_CONTENT_URI, values);
		values.clear();
		
		EventBus.getDefault().post(new GroupMessageEvent(1));
	}
	
	/**
	 * 将P2P消息摘要，保存到消息列表table中----在这里还要根据ChatPersonActivity和did判断是否提示红点
	 */
	public synchronized void saveTextMessageSummaryToDatabase (String message) {
		long uid = 0;
		Cursor cookieCursor = context.getContentResolver().query(GeneralContentProvider.COOKIE_CONTENT_URI, null, null, null, null);
		if (cookieCursor != null && cookieCursor.moveToFirst()) {
			uid = (long) cookieCursor.getInt(cookieCursor.getColumnIndex(CookieTable.COLUMN_ID));
			cookieCursor.close();
		}
		
		JSONObject jsonObject = (JSONObject) JSONObject.parse(message);
		
		ContentValues values = new ContentValues();
		values.put(MessageSummaryTable.COLUMN_MESSAGE_TYPE, jsonObject.getLong(GlobalConstants.JSON_TYPE));
		values.put(MessageSummaryTable.COLUMN_MESSAGE_OVERVIEW, jsonObject.getString(GlobalConstants.JSON_SUMMARY));
		values.put(MessageSummaryTable.COLUMN_UPDATED, jsonObject.getLong(GlobalConstants.JSON_CREATETIME));
		values.put(MessageSummaryTable.COLUMN_MESSAGE_TO_USER_ID, jsonObject.getLong(GlobalConstants.JSON_RECEIVER));
		values.put(MessageSummaryTable.COLUMN_MESSAGE_FROM_USER_ID, jsonObject.getLong(GlobalConstants.JSON_SENDER));
		
		values.put(MessageSummaryTable.COLUMN_MESSAGE_DIALOG_ID, jsonObject.getLong(GlobalConstants.JSON_DID));
		
		// display type
		values.put(MessageSummaryTable.COLUMN_MESSAGE_DISPLAY_TYPE, IMConstants.DISPLAY_LIST_TYPE_DIALOG_PERSON);
		
		// 首先查表判断该消息是否存在
		Cursor cursor = context.getContentResolver().query(GeneralContentProvider.MESSAGE_SUMMARY_CONTENT_URI, null, 
				MessageSummaryTable.COLUMN_MESSAGE_DISPLAY_TYPE + " = ?" + " AND " + MessageSummaryTable.COLUMN_MESSAGE_DIALOG_ID + " = ?", 
				new String[] { String.valueOf(IMConstants.DISPLAY_LIST_TYPE_DIALOG_PERSON), jsonObject.getString(GlobalConstants.JSON_DID)}, null);
		
		if (cursor != null && cursor.moveToFirst()) {
			// 此时，消息已经存在--执行更新操作
			long unRead = cursor.getLong(cursor.getColumnIndex(MessageSummaryTable.COLUMN_UNREADCOUNT));
			
			ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
	        RunningTaskInfo info = manager.getRunningTasks(1).get(0);
	        String className = info.topActivity.getClassName();              //完整类名
	        
	        if (className.equals("com.xiaobukuaipao.vzhi.ChatPersonActivity") && jsonObject.getString(GlobalConstants.JSON_DID).equals(ChatPersonActivity.did)) {
	        	values.put(MessageSummaryTable.COLUMN_UNREADCOUNT, 0);
	        } else {
	        	values.put(MessageSummaryTable.COLUMN_UNREADCOUNT, unRead + 1);
	        }
			
	        if (jsonObject.getLong(GlobalConstants.JSON_SENDER) != uid) {
				context.getContentResolver().update(GeneralContentProvider.MESSAGE_SUMMARY_CONTENT_URI, values, 
						MessageSummaryTable.COLUMN_MESSAGE_DISPLAY_TYPE + " = ?" + " AND " + MessageSummaryTable.COLUMN_MESSAGE_DIALOG_ID + " = ?", 
						new String[] { String.valueOf(IMConstants.DISPLAY_LIST_TYPE_DIALOG_PERSON), jsonObject.getString(GlobalConstants.JSON_DID)});
	        }
			cursor.close();
		} else {
			// 消息不存在，执行插入操作
			if (jsonObject.getLong(GlobalConstants.JSON_SENDER) != uid) {
				values.put(MessageSummaryTable.COLUMN_UNREADCOUNT, 1);
				context.getContentResolver().insert(GeneralContentProvider.MESSAGE_SUMMARY_CONTENT_URI, values);
			}
		}
		
		values.clear();
		
	}
	
	
	public synchronized void saveGroupTextMessageSummaryToDatabase(String message) {
		long uid = 0;
		Cursor cookieCursor = context.getContentResolver().query(GeneralContentProvider.COOKIE_CONTENT_URI, null, null, null, null);
		if (cookieCursor != null && cookieCursor.moveToFirst()) {
			uid = (long) cookieCursor.getInt(cookieCursor.getColumnIndex(CookieTable.COLUMN_ID));
			cookieCursor.close();
		}
		
		JSONObject jsonObject = (JSONObject) JSONObject.parse(message);
		
		ContentValues values = new ContentValues();
		values.put(MessageSummaryTable.COLUMN_MESSAGE_TYPE, jsonObject.getLong(GlobalConstants.JSON_TYPE));
		values.put(MessageSummaryTable.COLUMN_MESSAGE_OVERVIEW, jsonObject.getString(GlobalConstants.JSON_SUMMARY));
		values.put(MessageSummaryTable.COLUMN_UPDATED, jsonObject.getLong(GlobalConstants.JSON_CREATETIME));
		values.put(MessageSummaryTable.COLUMN_MESSAGE_FROM_USER_ID, jsonObject.getLong(GlobalConstants.JSON_SENDER));
		
		values.put(MessageSummaryTable.COLUMN_MESSAGE_GROUP_ID, jsonObject.getLong(GlobalConstants.JSON_GID));
		
		// display type
		values.put(MessageSummaryTable.COLUMN_MESSAGE_DISPLAY_TYPE, IMConstants.DISPLAY_LIST_TYPE_DIALOG_GROUP);
		
		// 首先查表判断该消息是否存在
		Cursor cursor = context.getContentResolver().query(GeneralContentProvider.MESSAGE_SUMMARY_CONTENT_URI, null, 
				MessageSummaryTable.COLUMN_MESSAGE_DISPLAY_TYPE + " = ?" + " AND " + MessageSummaryTable.COLUMN_MESSAGE_GROUP_ID + " = ?", 
				new String[] { String.valueOf(IMConstants.DISPLAY_LIST_TYPE_DIALOG_GROUP), jsonObject.getString(GlobalConstants.JSON_GID)}, null);
		
		if (cursor != null && cursor.moveToFirst()) {
			// 此时，消息已经存在--执行更新操作
			long unRead = cursor.getLong(cursor.getColumnIndex(MessageSummaryTable.COLUMN_UNREADCOUNT));
			// unread
			ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
	        RunningTaskInfo info = manager.getRunningTasks(1).get(0);
	        String className = info.topActivity.getClassName();              //完整类名
	        
			// if (AppActivityManager.getInstance().getCurrentActivity() != null && AppActivityManager.getInstance().getCurrentActivity().getClass().equals(ChatGroupActivity.class)) {
	        if (className.equals("com.xiaobukuaipao.vzhi.ChatGroupActivity") && jsonObject.getString(GlobalConstants.JSON_GID).equals(ChatGroupActivity.groupId)) {
				values.put(MessageSummaryTable.COLUMN_UNREADCOUNT, 0);
			} else {
				values.put(MessageSummaryTable.COLUMN_UNREADCOUNT, unRead + 1);
			}
			
			context.getContentResolver().update(GeneralContentProvider.MESSAGE_SUMMARY_CONTENT_URI, values, 
					MessageSummaryTable.COLUMN_MESSAGE_DISPLAY_TYPE + " = ?" + " AND " + MessageSummaryTable.COLUMN_MESSAGE_GROUP_ID + " = ?", 
					new String[] { String.valueOf(IMConstants.DISPLAY_LIST_TYPE_DIALOG_GROUP), jsonObject.getString(GlobalConstants.JSON_GID)});
			cursor.close();
		} else {
			// 消息不存在，执行插入操作
			if (jsonObject.getLong(GlobalConstants.JSON_SENDER) == uid) {
				values.put(MessageSummaryTable.COLUMN_UNREADCOUNT, 0);
			} else {
				values.put(MessageSummaryTable.COLUMN_UNREADCOUNT, 1);
			}
			
			context.getContentResolver().insert(GeneralContentProvider.MESSAGE_SUMMARY_CONTENT_URI, values);
		}
		
		values.clear();
		
	}

	/**
	 * 获取本人uid
	 * @param context
	 * @return
	 */
	public synchronized int getUid(Context context){
		int uid = -1;
		Cursor cursor = context.getContentResolver().query(GeneralContentProvider.COOKIE_CONTENT_URI, 
				null, null, null, null);
		if (cursor != null && cursor.moveToFirst()) {
			uid = cursor.getInt(cursor.getColumnIndex(CookieTable.COLUMN_ID));
			cursor.close();
		}
		
		return uid;
	}
	
	
	/**
	 * 加好友Socket
	 * @param message
	 */
	public synchronized void saveFriendMessageSummaryToDatabase(String message) {
		
		JSONObject jsonObject = (JSONObject) JSONObject.parse(message);
		if (jsonObject != null) {
			ContentValues values = new ContentValues();
			// 陌生人消息
			values.put(MessageSummaryTable.COLUMN_MESSAGE_OVERVIEW, 
					jsonObject.getString(GlobalConstants.JSON_BODY));
			
			values.put(MessageSummaryTable.COLUMN_UPDATED, jsonObject.getLong(GlobalConstants.JSON_CREATETIME));
			
			// 首先查表判断该消息是否存在
			Cursor cursor = context.getContentResolver().query(GeneralContentProvider.MESSAGE_SUMMARY_CONTENT_URI, null, 
					MessageSummaryTable.COLUMN_MESSAGE_DISPLAY_TYPE + " = ?", 
					new String[] { String.valueOf(IMConstants.DISPLAY_LIST_TYPE_STRANGERLETTER) }, null);
			
			if (cursor != null && cursor.moveToFirst()) {
				// 此时，陌生人列表已经存在--执行更新操作
				long unRead = cursor.getLong(cursor.getColumnIndex(MessageSummaryTable.COLUMN_UNREADCOUNT));
				
				values.put(MessageSummaryTable.COLUMN_UNREADCOUNT, unRead + 1);
				
				context.getContentResolver().update(GeneralContentProvider.MESSAGE_SUMMARY_CONTENT_URI, values, 
						MessageSummaryTable.COLUMN_MESSAGE_DISPLAY_TYPE + " = ?", 
						new String[] { String.valueOf(IMConstants.DISPLAY_LIST_TYPE_STRANGERLETTER) });
				cursor.close();
			} else {
				// 消息不存在，执行插入操作
				values.put(MessageSummaryTable.COLUMN_UNREADCOUNT, 1);
				context.getContentResolver().insert(GeneralContentProvider.MESSAGE_SUMMARY_CONTENT_URI, values);
			}
			
			// 清空
			values.clear();
		}
	}
	
	/**
	 * 职位投递提醒
	 */
	public synchronized void saveJobApplyMessageSummaryToDatabase(String message) {
		JSONObject jsonObject = (JSONObject) JSONObject.parse(message);
		if (jsonObject != null) {
			ContentValues values = new ContentValues();
			// 陌生人消息
			values.put(MessageSummaryTable.COLUMN_MESSAGE_OVERVIEW, 
					jsonObject.getString(GlobalConstants.JSON_BODY));
			
			values.put(MessageSummaryTable.COLUMN_UPDATED, jsonObject.getLong(GlobalConstants.JSON_CREATETIME));
			
			// 首先查表判断该消息是否存在
			Cursor cursor = context.getContentResolver().query(GeneralContentProvider.MESSAGE_SUMMARY_CONTENT_URI, null, 
					MessageSummaryTable.COLUMN_MESSAGE_DISPLAY_TYPE + " = ?", 
					new String[] { String.valueOf(IMConstants.DISPLAY_LIST_TYPE_JOBAPPLY) }, null);
			
			if (cursor != null && cursor.moveToFirst()) {
				// 此时，陌生人列表已经存在--执行更新操作
				long unRead = cursor.getLong(cursor.getColumnIndex(MessageSummaryTable.COLUMN_UNREADCOUNT));
				
				values.put(MessageSummaryTable.COLUMN_UNREADCOUNT, unRead + 1);
				
				context.getContentResolver().update(GeneralContentProvider.MESSAGE_SUMMARY_CONTENT_URI, values, 
						MessageSummaryTable.COLUMN_MESSAGE_DISPLAY_TYPE + " = ?", 
						new String[] { String.valueOf(IMConstants.DISPLAY_LIST_TYPE_JOBAPPLY) });
				cursor.close();
			} else {
				// 消息不存在，执行插入操作
				values.put(MessageSummaryTable.COLUMN_UNREADCOUNT, 1);
				context.getContentResolver().insert(GeneralContentProvider.MESSAGE_SUMMARY_CONTENT_URI, values);
			}
			
			// 清空
			values.clear();
		}
	}
	
	/**
	 * 插入陌生人, 职位推荐, 群推荐, 简历投递到Summary Table
	 * @param messageSummary
	 */
	public synchronized void insertLetterIntoMessageSummaryTable(String messageSummary) {
		JSONObject jsonObject = (JSONObject) JSONObject.parse(messageSummary);
		if (jsonObject != null) {
			ContentValues values = new ContentValues();
			
			if (jsonObject.getJSONObject(GlobalConstants.JSON_JOB_APPLY) != null) {
				// 插入数据库或者更新数据库 -- 先更新数据库，如果返回0，则说明不存在，此时执行插入操作; 如果返回不为0, 此时说明已经存在，所以执行更新操作
				// 新增投递简历
				values.put(MessageSummaryTable.COLUMN_MESSAGE_OVERVIEW, 
						jsonObject.getJSONObject(GlobalConstants.JSON_JOB_APPLY).getString(GlobalConstants.JSON_MSG));
				values.put(MessageSummaryTable.COLUMN_UPDATED, 
						jsonObject.getJSONObject(GlobalConstants.JSON_JOB_APPLY).getLong(GlobalConstants.JSON_TIME));
				values.put(MessageSummaryTable.COLUMN_UNREADCOUNT, 
						jsonObject.getJSONObject(GlobalConstants.JSON_JOB_APPLY).getLong(GlobalConstants.JSON_UNREADCOUNT));
				
				if (context.getContentResolver().update(GeneralContentProvider.MESSAGE_SUMMARY_CONTENT_URI, values, 
						MessageSummaryTable.COLUMN_MESSAGE_DISPLAY_TYPE + " = ?", new String[] { String.valueOf(IMConstants.DISPLAY_LIST_TYPE_JOBAPPLY)}) <= 0) {
					// 此时没更新，所以执行插入操作
					values.put(MessageSummaryTable.COLUMN_MESSAGE_TYPE, IMConstants.TYPE_LETTER_JOB_APPLY_NOTIFY);
					values.put(MessageSummaryTable.COLUMN_MESSAGE_DISPLAY_TYPE, IMConstants.DISPLAY_LIST_TYPE_JOBAPPLY);
					values.put(MessageSummaryTable.COLUMN_CREATED, jsonObject.getJSONObject(GlobalConstants.JSON_JOB_APPLY).getLong(GlobalConstants.JSON_TIME));
					// 插入到数据库
					context.getContentResolver().insert(GeneralContentProvider.MESSAGE_SUMMARY_CONTENT_URI, values);
				}
				// 清空
				values.clear();
			}
			
			if (jsonObject.getJSONObject(GlobalConstants.JSON_STRANGER_LETTER) != null) {
				// 陌生人消息
				values.put(MessageSummaryTable.COLUMN_MESSAGE_OVERVIEW, 
						jsonObject.getJSONObject(GlobalConstants.JSON_STRANGER_LETTER).getString(GlobalConstants.JSON_MSG));
				values.put(MessageSummaryTable.COLUMN_UPDATED, 
						jsonObject.getJSONObject(GlobalConstants.JSON_STRANGER_LETTER).getLong(GlobalConstants.JSON_TIME));
				values.put(MessageSummaryTable.COLUMN_UNREADCOUNT, 
						jsonObject.getJSONObject(GlobalConstants.JSON_STRANGER_LETTER).getLong(GlobalConstants.JSON_UNREADCOUNT));
				
				if (context.getContentResolver().update(GeneralContentProvider.MESSAGE_SUMMARY_CONTENT_URI, values, 
						MessageSummaryTable.COLUMN_MESSAGE_DISPLAY_TYPE + " = ?", new String[] { String.valueOf(IMConstants.DISPLAY_LIST_TYPE_STRANGERLETTER) }) <= 0) {
					// 此时没更新，所以执行插入操作
					values.put(MessageSummaryTable.COLUMN_MESSAGE_TYPE, IMConstants.TYPE_LETTER_STRANGER);
					values.put(MessageSummaryTable.COLUMN_MESSAGE_DISPLAY_TYPE, IMConstants.DISPLAY_LIST_TYPE_STRANGERLETTER);
					values.put(MessageSummaryTable.COLUMN_CREATED, jsonObject.getJSONObject(GlobalConstants.JSON_STRANGER_LETTER).getLong(GlobalConstants.JSON_TIME));
					// 插入到数据库
					context.getContentResolver().insert(GeneralContentProvider.MESSAGE_SUMMARY_CONTENT_URI, values);
				}
				// 清空
				values.clear();
			}
			
			if (jsonObject.getJSONObject(GlobalConstants.JSON_JOB_RECOMMEND) != null) {
				// 职位推荐
				values.put(MessageSummaryTable.COLUMN_MESSAGE_OVERVIEW, 
						jsonObject.getJSONObject(GlobalConstants.JSON_JOB_RECOMMEND).getString(GlobalConstants.JSON_MSG));
				values.put(MessageSummaryTable.COLUMN_UPDATED, 
						jsonObject.getJSONObject(GlobalConstants.JSON_JOB_RECOMMEND).getLong(GlobalConstants.JSON_TIME));
				values.put(MessageSummaryTable.COLUMN_UNREADCOUNT, 
						jsonObject.getJSONObject(GlobalConstants.JSON_JOB_RECOMMEND).getLong(GlobalConstants.JSON_UNREADCOUNT));
				
				if (context.getContentResolver().update(GeneralContentProvider.MESSAGE_SUMMARY_CONTENT_URI, values, 
						MessageSummaryTable.COLUMN_MESSAGE_DISPLAY_TYPE + " = ?", new String[] { String.valueOf(IMConstants.DISPLAY_LIST_TYPE_JOBRECOMMEND)}) <= 0) {
					// 此时没更新，所以执行插入操作
					values.put(MessageSummaryTable.COLUMN_MESSAGE_TYPE, IMConstants.TYPE_LETTER_JOB_RECOMMEND);
					values.put(MessageSummaryTable.COLUMN_MESSAGE_DISPLAY_TYPE, IMConstants.DISPLAY_LIST_TYPE_JOBRECOMMEND);
					values.put(MessageSummaryTable.COLUMN_CREATED, jsonObject.getJSONObject(GlobalConstants.JSON_JOB_RECOMMEND).getLong(GlobalConstants.JSON_TIME));
					// 插入到数据库
					context.getContentResolver().insert(GeneralContentProvider.MESSAGE_SUMMARY_CONTENT_URI, values);
				}
				// 清空
				values.clear();
			}
			
			if (jsonObject.getJSONObject(GlobalConstants.JSON_GROUP_RECOMMEND) != null) {
				// 群推荐
				values.put(MessageSummaryTable.COLUMN_MESSAGE_OVERVIEW, 
						jsonObject.getJSONObject(GlobalConstants.JSON_GROUP_RECOMMEND).getString(GlobalConstants.JSON_MSG));
				values.put(MessageSummaryTable.COLUMN_UPDATED, 
						jsonObject.getJSONObject(GlobalConstants.JSON_GROUP_RECOMMEND).getLong(GlobalConstants.JSON_TIME));
				values.put(MessageSummaryTable.COLUMN_UNREADCOUNT, 
						jsonObject.getJSONObject(GlobalConstants.JSON_GROUP_RECOMMEND).getLong(GlobalConstants.JSON_UNREADCOUNT));
				
				if (context.getContentResolver().update(GeneralContentProvider.MESSAGE_SUMMARY_CONTENT_URI, values, 
						MessageSummaryTable.COLUMN_MESSAGE_DISPLAY_TYPE + " = ?", new String[] { String.valueOf(IMConstants.DISPLAY_LIST_TYPE_GROUPRECOMMEND)}) <= 0) {
					// 此时没更新，所以执行插入操作
					values.put(MessageSummaryTable.COLUMN_MESSAGE_TYPE, IMConstants.TYPE_LETTER_GROUP_RECOMMEND);
					values.put(MessageSummaryTable.COLUMN_MESSAGE_DISPLAY_TYPE, IMConstants.DISPLAY_LIST_TYPE_GROUPRECOMMEND);
					values.put(MessageSummaryTable.COLUMN_CREATED, jsonObject.getJSONObject(GlobalConstants.JSON_GROUP_RECOMMEND).getLong(GlobalConstants.JSON_TIME));
					// 插入到数据库
					context.getContentResolver().insert(GeneralContentProvider.MESSAGE_SUMMARY_CONTENT_URI, values);
				}
				// 清空
				values.clear();
			}
		}
	}
	
	
	/**
	 * 个人和群组 Summary
	 * @param messageSummary
	 */
	public synchronized void insertP2POrGroupIntoMessageSummaryTable(String messageSummary) {
		JSONObject jsonObject = (JSONObject) JSONObject.parse(messageSummary);
		if (jsonObject != null) {
			ContentValues values = new ContentValues();
			
			if (jsonObject.getJSONArray(GlobalConstants.JSON_GROUP) != null && 
					jsonObject.getJSONArray(GlobalConstants.JSON_GROUP).size() > 0) {
				// 群组
				for (int i = 0; i < jsonObject.getJSONArray(GlobalConstants.JSON_GROUP).size(); i++) {
					values.put(MessageSummaryTable.COLUMN_MESSAGE_GROUP_ID, 
							jsonObject.getJSONArray(GlobalConstants.JSON_GROUP).getJSONObject(i).getString(GlobalConstants.JSON_GID));

					values.put(MessageSummaryTable.COLUMN_UNREADCOUNT, 
							jsonObject.getJSONArray(GlobalConstants.JSON_GROUP).getJSONObject(i).getLong(GlobalConstants.JSON_UNREADCOUNT));
					
					values.put(MessageSummaryTable.COLUMN_MESSAGE_OVERVIEW, 
							jsonObject.getJSONArray(GlobalConstants.JSON_GROUP).getJSONObject(i).getJSONObject(GlobalConstants.JSON_MSG).getString(GlobalConstants.JSON_SUMMARY));
					
					values.put(MessageSummaryTable.COLUMN_UPDATED, 
							jsonObject.getJSONArray(GlobalConstants.JSON_GROUP).getJSONObject(i).getJSONObject(GlobalConstants.JSON_MSG).getLong(GlobalConstants.JSON_CREATETIME));
					
					values.put(MessageSummaryTable.COLUMN_MESSAGE_FROM_USER_ID, 
							jsonObject.getJSONArray(GlobalConstants.JSON_GROUP).getJSONObject(i).getJSONObject(GlobalConstants.JSON_MSG).getLong(GlobalConstants.JSON_SENDER));
					
					values.put(MessageSummaryTable.COLUMN_MESSAGE_TYPE, 
							jsonObject.getJSONArray(GlobalConstants.JSON_GROUP).getJSONObject(i).getJSONObject(GlobalConstants.JSON_MSG).getLong(GlobalConstants.JSON_TYPE));
					
					// display type
					values.put(MessageSummaryTable.COLUMN_MESSAGE_DISPLAY_TYPE, IMConstants.DISPLAY_LIST_TYPE_DIALOG_GROUP);
					
					if (context.getContentResolver().update(GeneralContentProvider.MESSAGE_SUMMARY_CONTENT_URI, values, 
							MessageSummaryTable.COLUMN_MESSAGE_DISPLAY_TYPE + " = ?" + " AND " + MessageSummaryTable.COLUMN_MESSAGE_GROUP_ID + " = ?", 
							new String[] { String.valueOf(IMConstants.DISPLAY_LIST_TYPE_DIALOG_GROUP), 
							jsonObject.getJSONArray(GlobalConstants.JSON_GROUP).getJSONObject(i).getString(GlobalConstants.JSON_GID)}) <= 0) {
						// 此时没更新成功，执行插入操作
						values.put(MessageSummaryTable.COLUMN_CREATED, 
								jsonObject.getJSONArray(GlobalConstants.JSON_GROUP).getJSONObject(i).getJSONObject(GlobalConstants.JSON_MSG).getLong(GlobalConstants.JSON_CREATETIME));
						// 插入到数据库
						context.getContentResolver().insert(GeneralContentProvider.MESSAGE_SUMMARY_CONTENT_URI, values);
					}
					
					values.clear();
				}
			}
			
			if (jsonObject.getJSONArray(GlobalConstants.JSON_P2P) != null && 
					jsonObject.getJSONArray(GlobalConstants.JSON_P2P).size() > 0) {
				// 个人
				for (int i = 0; i < jsonObject.getJSONArray(GlobalConstants.JSON_P2P).size(); i++) {
					values.put(MessageSummaryTable.COLUMN_MESSAGE_DIALOG_ID, 
							jsonObject.getJSONArray(GlobalConstants.JSON_P2P).getJSONObject(i).getString(GlobalConstants.JSON_DID));

					values.put(MessageSummaryTable.COLUMN_UNREADCOUNT, 
							jsonObject.getJSONArray(GlobalConstants.JSON_P2P).getJSONObject(i).getLong(GlobalConstants.JSON_UNREADCOUNT));
					
					values.put(MessageSummaryTable.COLUMN_MESSAGE_OVERVIEW, 
							jsonObject.getJSONArray(GlobalConstants.JSON_P2P).getJSONObject(i).getJSONObject(GlobalConstants.JSON_MSG).getString(GlobalConstants.JSON_SUMMARY));
					
					values.put(MessageSummaryTable.COLUMN_UPDATED, 
							jsonObject.getJSONArray(GlobalConstants.JSON_P2P).getJSONObject(i).getJSONObject(GlobalConstants.JSON_MSG).getLong(GlobalConstants.JSON_CREATETIME));
					
					values.put(MessageSummaryTable.COLUMN_MESSAGE_FROM_USER_ID, 
							jsonObject.getJSONArray(GlobalConstants.JSON_P2P).getJSONObject(i).getJSONObject(GlobalConstants.JSON_MSG).getLong(GlobalConstants.JSON_SENDER));
					
					values.put(MessageSummaryTable.COLUMN_MESSAGE_TO_USER_ID, 
							jsonObject.getJSONArray(GlobalConstants.JSON_P2P).getJSONObject(i).getJSONObject(GlobalConstants.JSON_MSG).getLong(GlobalConstants.JSON_RECEIVER));
					
					values.put(MessageSummaryTable.COLUMN_MESSAGE_TYPE, 
							jsonObject.getJSONArray(GlobalConstants.JSON_P2P).getJSONObject(i).getJSONObject(GlobalConstants.JSON_MSG).getLong(GlobalConstants.JSON_TYPE));
					
					// display type
					values.put(MessageSummaryTable.COLUMN_MESSAGE_DISPLAY_TYPE, IMConstants.DISPLAY_LIST_TYPE_DIALOG_PERSON);
					
					if (context == null) {
						Log.i(TAG, "context is null");
					} else {
						Log.i(TAG, "context is not null");
					}
					
					if (context.getContentResolver().update(GeneralContentProvider.MESSAGE_SUMMARY_CONTENT_URI, values, 
							MessageSummaryTable.COLUMN_MESSAGE_DISPLAY_TYPE + " = ?" + " AND " + MessageSummaryTable.COLUMN_MESSAGE_DIALOG_ID + " = ?", 
							new String[] { String.valueOf(IMConstants.DISPLAY_LIST_TYPE_DIALOG_PERSON), 
							jsonObject.getJSONArray(GlobalConstants.JSON_P2P).getJSONObject(i).getString(GlobalConstants.JSON_DID)}) <= 0) {
						// 此时没更新成功，执行插入操作
						values.put(MessageSummaryTable.COLUMN_CREATED, 
								jsonObject.getJSONArray(GlobalConstants.JSON_P2P).getJSONObject(i).getJSONObject(GlobalConstants.JSON_MSG).getLong(GlobalConstants.JSON_CREATETIME));
						// 插入到数据库
						context.getContentResolver().insert(GeneralContentProvider.MESSAGE_SUMMARY_CONTENT_URI, values);
					}
					
					values.clear();
				}
			}
		}
	}
	
	
	
	/**
	 * 群组中更新发送的消息
	 */
	public synchronized void updateSendMessageGroupTable(String jsonMessage) {
		if (jsonMessage != null && jsonMessage.length() > 0) {
					
			ContentValues values = new ContentValues();
			
			JSONObject jsonObject = JSONObject.parseObject(jsonMessage);
			// Mid递增顺序--可以作为排序字段，且永远不为null
			values.put(MessageGroupTable.COLUMN_MESSAGE_ID, jsonObject.getLong(GlobalConstants.JSON_MID));
			
			values.put(MessageGroupTable.COLUMN_CREATED, jsonObject.getLong(GlobalConstants.JSON_CREATETIME));
			// 1代表发送成功
			values.put(MessageGroupTable.COLUMN_MESSAGE_STATUS, 1);
			
			// 插入数据库
			context.getContentResolver().update(GeneralContentProvider.MESSAGE_GROUP_CONTENT_URI, values, 
					MessageGroupTable.COLUMN_ID + " = ?" + " AND " + MessageGroupTable.COLUMN_MESSAGE_GROUP_ID + " = ?", 
					new String[] { jsonObject.getString(GlobalConstants.JSON_CMID), jsonObject.getString(GlobalConstants.JSON_GID) });
		}
	}
	
	/**
	 * 清空职位推荐消息的未读数
	 */
	public synchronized void cleanJobRecommendCount() {
		ContentValues values = new ContentValues();
		values.put(MessageSummaryTable.COLUMN_UNREADCOUNT, 0);
		context.getContentResolver().update(GeneralContentProvider.MESSAGE_SUMMARY_CONTENT_URI, values, 
				MessageSummaryTable.COLUMN_MESSAGE_DISPLAY_TYPE + " = ?", new String[] { String.valueOf(IMConstants.DISPLAY_LIST_TYPE_JOBRECOMMEND) });
		values.clear();
	}
	
	/**
	 * 清空消息列表中陌生人消息的未读数
	 */
	public synchronized void subMessageListStrangerCount() {
		long unreadCount = 0;
		Cursor cursor = context.getContentResolver().query(GeneralContentProvider.MESSAGE_SUMMARY_CONTENT_URI, 
				null, null, null, null);
		if (cursor != null && cursor.moveToFirst()) {
			unreadCount = cursor.getLong(cursor.getColumnIndex(MessageSummaryTable.COLUMN_UNREADCOUNT));
			cursor.close();
		}
		
		if (unreadCount > 0) {
			ContentValues values = new ContentValues();
			values.put(MessageSummaryTable.COLUMN_UNREADCOUNT, unreadCount -1 );
			context.getContentResolver().update(GeneralContentProvider.MESSAGE_SUMMARY_CONTENT_URI, values, 
					MessageSummaryTable.COLUMN_MESSAGE_DISPLAY_TYPE + " = ?", new String[] { String.valueOf(IMConstants.DISPLAY_LIST_TYPE_STRANGERLETTER) });
			values.clear();
		} else {
			ContentValues values = new ContentValues();
			values.put(MessageSummaryTable.COLUMN_UNREADCOUNT, 0);
			context.getContentResolver().update(GeneralContentProvider.MESSAGE_SUMMARY_CONTENT_URI, values, 
					MessageSummaryTable.COLUMN_MESSAGE_DISPLAY_TYPE + " = ?", new String[] { String.valueOf(IMConstants.DISPLAY_LIST_TYPE_STRANGERLETTER) });
			values.clear();
		}
	}
	
	
	public synchronized void cleanMessageListStrangerCount() {
		ContentValues values = new ContentValues();
		values.put(MessageSummaryTable.COLUMN_UNREADCOUNT, 0);
		context.getContentResolver().update(GeneralContentProvider.MESSAGE_SUMMARY_CONTENT_URI, values, 
				MessageSummaryTable.COLUMN_MESSAGE_DISPLAY_TYPE + " = ?", new String[] { String.valueOf(IMConstants.DISPLAY_LIST_TYPE_STRANGERLETTER) });
		values.clear();
	}
	
	/**
	 * 清空消息列表中与某人的消息的未读数
	 */
	public synchronized void cleanMessageListOtherCount(long did) {
		ContentValues values = new ContentValues();
		values.put(MessageSummaryTable.COLUMN_UNREADCOUNT, 0);
		Log.i(TAG, "cleanMessageListOtherCount : " + did);
		context.getContentResolver().update(GeneralContentProvider.MESSAGE_SUMMARY_CONTENT_URI, values, 
				MessageSummaryTable.COLUMN_MESSAGE_DISPLAY_TYPE + " = ?" + " AND " + MessageSummaryTable.COLUMN_MESSAGE_DIALOG_ID + " = ?", 
				new String[] { String.valueOf(IMConstants.DISPLAY_LIST_TYPE_DIALOG_PERSON), String.valueOf(did) });
		values.clear();
	}
	
	public synchronized void cleanMessageListOtherGroupCount(long gid) {
		ContentValues values = new ContentValues();
		values.put(MessageSummaryTable.COLUMN_UNREADCOUNT, 0);
		context.getContentResolver().update(GeneralContentProvider.MESSAGE_SUMMARY_CONTENT_URI, values, 
				MessageSummaryTable.COLUMN_MESSAGE_DISPLAY_TYPE + " = ?" + " AND " + MessageSummaryTable.COLUMN_MESSAGE_GROUP_ID + " = ?", 
				new String[] { String.valueOf(IMConstants.DISPLAY_LIST_TYPE_DIALOG_GROUP), String.valueOf(gid) });
		values.clear();
	}
	
	/**
	 * 保存个人信息到联系人表
	 * @param userMessage
	 */
	public synchronized boolean insertPersonInfoIntoContactUserTable(String userMessage) {
		boolean isAnonyToRealChanged = false;
		
		long uid = 0;
		Cursor cookieCursor = context.getContentResolver().query(GeneralContentProvider.COOKIE_CONTENT_URI, null, null, null, null);
		if (cookieCursor != null && cookieCursor.moveToFirst()) {
			uid = (long) cookieCursor.getInt(cookieCursor.getColumnIndex(CookieTable.COLUMN_ID));
			cookieCursor.close();
		}
		
		JSONObject jsonObject = (JSONObject) JSONObject.parse(userMessage);
		
		if (jsonObject != null) {
			
			ContentValues values = new ContentValues();
			
			if (jsonObject.getLong(GlobalConstants.JSON_ID) != null) {
				values.put(ContactUserTable.COLUMN_UID, jsonObject.getLong(GlobalConstants.JSON_ID));
			}
			
			if (jsonObject.getLong(GlobalConstants.JSON_ISREAL) != null) {
				values.put(ContactUserTable.COLUMN_ISREAL, jsonObject.getLong(GlobalConstants.JSON_ISREAL));
				
				if (jsonObject.getLong(GlobalConstants.JSON_ISREAL) == 1) {
					// 实名
					if (jsonObject.getString(GlobalConstants.JSON_AVATAR) != null) {
						values.put(ContactUserTable.COLUMN_AVATAR, jsonObject.getString(GlobalConstants.JSON_AVATAR));
					}
					
					if (jsonObject.getString(GlobalConstants.JSON_NAME) != null) {
						values.put(ContactUserTable.COLUMN_NAME, jsonObject.getString(GlobalConstants.JSON_NAME));
					}
				} else {
					// 匿名
					if (jsonObject.getString(GlobalConstants.JSON_AVATAR) != null) {
						Log.i(TAG, "nickavatar : " + jsonObject.getString(GlobalConstants.JSON_AVATAR));
						values.put(ContactUserTable.COLUMN_NICKAVATAR, jsonObject.getString(GlobalConstants.JSON_AVATAR));
					}
					
					if (jsonObject.getString(GlobalConstants.JSON_NAME) != null) {
						values.put(ContactUserTable.COLUMN_NICKNAME, jsonObject.getString(GlobalConstants.JSON_NAME));
					}
				}
			}
			
			if (StringUtils.isNotEmpty(jsonObject.getString(GlobalConstants.JSON_DID))) {
				values.put(ContactUserTable.COLUMN_DID, Long.valueOf(jsonObject.getString(GlobalConstants.JSON_DID)));
			} else if (StringUtils.isNotEmpty(jsonObject.getString(GlobalConstants.JSON_GID))) {
				values.put(ContactUserTable.COLUMN_GID, Long.valueOf(jsonObject.getString(GlobalConstants.JSON_GID)));
			}
			
			if (values.size() > 0) {
				
				if (StringUtils.isNotEmpty(jsonObject.getString(GlobalConstants.JSON_DID))) {
					Cursor cursor = context.getContentResolver().query(GeneralContentProvider.CONTACT_USER_CONTENT_URI, null, 
							ContactUserTable.COLUMN_UID + " = ?" + " AND " + ContactUserTable.COLUMN_DID + " = ?", 
							new String[] { String.valueOf(jsonObject.getLong(GlobalConstants.JSON_ID)) ,
							String.valueOf(jsonObject.getString(GlobalConstants.JSON_DID)) }, null);
					
					if (cursor != null && cursor.moveToFirst()) {
						boolean isChanged = cursor.getLong(cursor.getColumnIndex(ContactUserTable.COLUMN_ISREAL)) != jsonObject.getLong(GlobalConstants.JSON_ISREAL) ? true : false;
						// 执行更新操作
						context.getContentResolver().update(GeneralContentProvider.CONTACT_USER_CONTENT_URI, values, 
								ContactUserTable.COLUMN_UID  + " = ?" + " AND " + ContactUserTable.COLUMN_DID + " = ?", 
								new String[] { String.valueOf(jsonObject.getLong(GlobalConstants.JSON_ID)), String.valueOf(jsonObject.getString(GlobalConstants.JSON_DID)) });
						cursor.close();
						
						if (isChanged) {
							// 如果不是本人的实名匿名信息改变，则更新
							if (uid != jsonObject.getLong(GlobalConstants.JSON_ID)) {
								ContentValues changedValues = new ContentValues();
								changedValues.put(MessageSummaryTable.COLUMN_CHANGED, 1);
								Cursor cursorChanged = context.getContentResolver().query(GeneralContentProvider.MESSAGE_SUMMARY_CONTENT_URI, null, 
										MessageSummaryTable.COLUMN_MESSAGE_DIALOG_ID + " = ?" + " AND " + MessageSummaryTable.COLUMN_MESSAGE_DISPLAY_TYPE + " = ?", 
										new String[] { jsonObject.getString(GlobalConstants.JSON_DID),  String.valueOf(IMConstants.DISPLAY_LIST_TYPE_DIALOG_PERSON) }, null);
								if (cursorChanged != null && cursorChanged.moveToFirst()) {
									context.getContentResolver().update(GeneralContentProvider.MESSAGE_SUMMARY_CONTENT_URI, changedValues, 
											MessageSummaryTable.COLUMN_MESSAGE_DIALOG_ID + " = ?" + " AND " + MessageSummaryTable.COLUMN_MESSAGE_DISPLAY_TYPE + " = ?", 
											new String[] { jsonObject.getString(GlobalConstants.JSON_DID),  String.valueOf(IMConstants.DISPLAY_LIST_TYPE_DIALOG_PERSON) });
									cursorChanged.close();
									isAnonyToRealChanged = true;
								} else {
								}
								changedValues.clear();
							}
						} else {
						}
					} else {
						// 插入不用考虑
						context.getContentResolver().insert(GeneralContentProvider.CONTACT_USER_CONTENT_URI, values);
					}
				} else if (StringUtils.isNotEmpty(jsonObject.getString(GlobalConstants.JSON_GID))) {
					Cursor cursor = context.getContentResolver().query(GeneralContentProvider.CONTACT_USER_CONTENT_URI, null, 
							ContactUserTable.COLUMN_UID + " = ?" + " AND " + ContactUserTable.COLUMN_GID + " = ?", 
							new String[] { String.valueOf(jsonObject.getLong(GlobalConstants.JSON_ID)) ,
									String.valueOf(jsonObject.getString(GlobalConstants.JSON_GID)) }, null);
					
					if (cursor != null && cursor.moveToFirst()) {
						context.getContentResolver().update(GeneralContentProvider.CONTACT_USER_CONTENT_URI, values, ContactUserTable.COLUMN_UID + " = ?" + " AND " + ContactUserTable.COLUMN_GID + " = ?", 
							new String[] { String.valueOf(jsonObject.getLong(GlobalConstants.JSON_ID)) ,
									String.valueOf(jsonObject.getString(GlobalConstants.JSON_GID)) });
						cursor.close();
					} else {
						// 插入不用考虑
						context.getContentResolver().insert(GeneralContentProvider.CONTACT_USER_CONTENT_URI, values);
					}
				}
				
			}
			
			values.clear();
		}
		
		if (isAnonyToRealChanged) {
			EventBus.getDefault().post(new MessageSummaryChangeEvent(3));
		}
		
		return isAnonyToRealChanged;
		
	}
	
	/**
	 * 读取消息列表中的所有未读信息数
	 */
	public synchronized int getTotalUnreadCount(Cursor cursor) {
		int totalUnread = 0;
		
		if (cursor != null && cursor.moveToFirst()) {
			do {
				totalUnread = (int) (totalUnread + cursor.getLong(cursor.getColumnIndex(MessageSummaryTable.COLUMN_UNREADCOUNT)));
			} while (cursor.moveToNext());
		}
		
		if (totalUnread > 99) {
			totalUnread = 99;
		}
		return totalUnread;
	}
	
	
	/**
	 * 当解除好友关系时，执行的操作
	 * @param uid
	 */
	public synchronized void cleanChatHistoryInMessageList(long did) {
		// 1.MessageTable中与此人的聊天记录删除
		// 2.MessageListTable中与此人的消息列表项
		
		context.getContentResolver().delete(GeneralContentProvider.MESSAGE_SUMMARY_CONTENT_URI, 
				MessageSummaryTable.COLUMN_MESSAGE_DISPLAY_TYPE + " = ?" + " AND " + MessageSummaryTable.COLUMN_MESSAGE_DIALOG_ID + " = ?", 
				new String[] {String.valueOf(IMConstants.DISPLAY_LIST_TYPE_DIALOG_PERSON), String.valueOf(did)});
		
		context.getContentResolver().delete(GeneralContentProvider.MESSAGE_CONTENT_URI, 
				MessageTable.COLUMN_MESSAGE_DIALOG_ID + " = ?", new String[] { String.valueOf(did) });
	}
	/**
	 * 删除MessageList中职位推荐消息
	 */
	public synchronized void deleteJobRecMessage() {
		context.getContentResolver().delete(GeneralContentProvider.MESSAGE_SUMMARY_CONTENT_URI, 
				MessageSummaryTable.COLUMN_MESSAGE_DISPLAY_TYPE + " = ?", 
				new String[] {String.valueOf(IMConstants.DISPLAY_LIST_TYPE_JOBRECOMMEND)});
	}
	
	/**
	 * 删除MessageList中陌生人消息
	 */
	public synchronized void deleteStrangerLetter() {
		context.getContentResolver().delete(GeneralContentProvider.MESSAGE_SUMMARY_CONTENT_URI, 
				MessageSummaryTable.COLUMN_MESSAGE_DISPLAY_TYPE + " = ?", 
				new String[] {String.valueOf(IMConstants.DISPLAY_LIST_TYPE_STRANGERLETTER)});
	}
	
	
	/**
	 * 删除MessageList中群推荐消息
	 */
	public synchronized void deleteGroupRecMessage() {
		context.getContentResolver().delete(GeneralContentProvider.MESSAGE_SUMMARY_CONTENT_URI, 
				MessageSummaryTable.COLUMN_MESSAGE_DISPLAY_TYPE + " = ?", 
				new String[] {String.valueOf(IMConstants.DISPLAY_LIST_TYPE_GROUPRECOMMEND)});
	}
	
	
	
	/**
	 * 删除MessageList中的P2P消息
	 */
	public synchronized void deleteP2PMessage(long did) {
		context.getContentResolver().delete(GeneralContentProvider.MESSAGE_SUMMARY_CONTENT_URI, 
				MessageSummaryTable.COLUMN_MESSAGE_DISPLAY_TYPE + " = ?" + " AND " + MessageSummaryTable.COLUMN_MESSAGE_DIALOG_ID + " = ?", 
				new String[] {String.valueOf(IMConstants.DISPLAY_LIST_TYPE_DIALOG_PERSON), String.valueOf(did)});
		
		context.getContentResolver().delete(GeneralContentProvider.MESSAGE_CONTENT_URI, 
				MessageTable.COLUMN_MESSAGE_DIALOG_ID + " = ?", new String[] { String.valueOf(did) });
	}
	
	/**
	 * 删除MessageList中的Group消息
	 */
	public synchronized void deleteGroupMessage(long gid) {
		context.getContentResolver().delete(GeneralContentProvider.MESSAGE_SUMMARY_CONTENT_URI, 
				MessageSummaryTable.COLUMN_MESSAGE_DISPLAY_TYPE + " = ?" + " AND " + MessageSummaryTable.COLUMN_MESSAGE_GROUP_ID + " = ?", 
				new String[] {String.valueOf(IMConstants.DISPLAY_LIST_TYPE_DIALOG_GROUP), String.valueOf(gid)});
		
		context.getContentResolver().delete(GeneralContentProvider.MESSAGE_GROUP_CONTENT_URI, 
				MessageGroupTable.COLUMN_MESSAGE_GROUP_ID + " = ?", new String[] { String.valueOf(gid) });
	}
	
	public synchronized long getGroupIsRealInContactUserTable(long gid) {
		long isReal = 0;
		Cursor cursor = context.getContentResolver().query(GeneralContentProvider.CONTACT_USER_CONTENT_URI, null, 
				ContactUserTable.COLUMN_GID + " =?" + " AND " + ContactUserTable.COLUMN_ISGROUP + " = ?", new String[] {String.valueOf(gid), String.valueOf(1)}, null);
		if (cursor != null && cursor.moveToFirst()) {
			isReal = cursor.getLong(cursor.getColumnIndex(ContactUserTable.COLUMN_ISREAL));
			cursor.close();
		}
		return isReal;
	}
	
}
