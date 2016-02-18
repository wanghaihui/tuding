package com.xiaobukuaipao.vzhi.contentprovider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import com.xiaobukuaipao.vzhi.database.ContactUserTable;
import com.xiaobukuaipao.vzhi.database.CookieTable;
import com.xiaobukuaipao.vzhi.database.GeneralDBHelper;
import com.xiaobukuaipao.vzhi.database.MessageGroupTable;
import com.xiaobukuaipao.vzhi.database.MessageSummaryTable;
import com.xiaobukuaipao.vzhi.database.MessageTable;
import com.xiaobukuaipao.vzhi.database.SuggestTable;
import com.xiaobukuaipao.vzhi.database.UserInfoTable;
import com.xiaobukuaipao.vzhi.event.DbHelperEvent;
import com.xiaobukuaipao.vzhi.util.Logcat;

import de.greenrobot.event.EventBus;

public class GeneralContentProvider extends ContentProvider {
	public static final String TAG = GeneralContentProvider.class.getSimpleName();
	
	// Used for the UriMatcher
	private static final int COOKIE = 10;
	private static final int USERINFO = 20;
	private static final int MESSAGE = 30;
	private static final int MESSAGE_SUMMARY = 40;
	private static final int CONTACT_USER = 50;
	private static final int MESSAGE_GROUP = 60;
	private static final int SUGGEST = 70;

	// 可能会改
	private static final String AUTHORITY = "com.xiaobukuaipao.vzhi.contentprovider";

	private static final String COOKIE_BASE_PATH = "cookie";
	public static final Uri COOKIE_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + COOKIE_BASE_PATH);
	
	private static final String USERINFO_BASE_PATH = "userinfo";
	public static final Uri USERINFO_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + USERINFO_BASE_PATH);
	
	private static final String MESSAGE_BASE_PATH = "messages";
	public static final Uri MESSAGE_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + MESSAGE_BASE_PATH);
	
	private static final String MESSAGE_SUMMARY_BASE_PATH = "message_summary";
	public static final Uri MESSAGE_SUMMARY_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + MESSAGE_SUMMARY_BASE_PATH);
	
	private static final String CONTACT_USER_BASE_PATH = "contact_user";
	public static final Uri CONTACT_USER_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + CONTACT_USER_BASE_PATH);
	
	private static final String MESSAGE_GROUP_BASE_PATH = "message_group";
	public static final Uri MESSAGE_GROUP_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + MESSAGE_GROUP_BASE_PATH);

	private static final String SUGGEST_BASE_PATH = "suggest";
	public static final Uri SUGGEST_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + SUGGEST_BASE_PATH);
	
	private static final UriMatcher sURIMatcher = new UriMatcher(
			UriMatcher.NO_MATCH);
	
	public static GeneralDBHelper dbHelper;
	
	static {
		sURIMatcher.addURI(AUTHORITY, COOKIE_BASE_PATH, COOKIE);
		sURIMatcher.addURI(AUTHORITY, USERINFO_BASE_PATH, USERINFO);
		sURIMatcher.addURI(AUTHORITY, MESSAGE_BASE_PATH, MESSAGE);
		sURIMatcher.addURI(AUTHORITY, MESSAGE_SUMMARY_BASE_PATH, MESSAGE_SUMMARY);
		sURIMatcher.addURI(AUTHORITY, CONTACT_USER_BASE_PATH, CONTACT_USER);
		sURIMatcher.addURI(AUTHORITY, MESSAGE_GROUP_BASE_PATH, MESSAGE_GROUP);
		sURIMatcher.addURI(AUTHORITY, SUGGEST_BASE_PATH, SUGGEST);
	}

	// ////////////////////////////////////////////////////////////////////////////////////////////////////
	// ContentProvider必须实现的方法
	@Override
	public boolean onCreate() {
		// 最先执行
		Log.i(TAG, "mDatabase is create in provider");
		// mDatabase = MultiUserDatabaseHelper.getInstance().getGeneralDBHelper();
		return false;
	}
	
	// 查询语句
	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		// Using SQLiteQueryBuilder instead of query() method
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		
		int uriType = sURIMatcher.match(uri);
		switch (uriType) {
			case COOKIE:
				// Check if the caller has requested a column which does not exists
				checkCookieColumns(projection);
				queryBuilder.setTables(CookieTable.TABLE_COOKIE);
				break;
			case USERINFO:
				checkUserInfoColumns(projection);
				queryBuilder.setTables(UserInfoTable.TABLE_USERINFO);
				break;
			case MESSAGE:
				// 临时空缺--以后补上
				// checkMessageColumns(projection);
				Log.i(TAG, "query message");
				queryBuilder.setTables(MessageTable.TABLE_MESSAGES);
				break;
			case MESSAGE_SUMMARY:
				queryBuilder.setTables(MessageSummaryTable.TABLE_MESSAGE_SUMMARY);
				break;
			case CONTACT_USER:
				queryBuilder.setTables(ContactUserTable.TABLE_CONTACT_USER);
				break;
			case MESSAGE_GROUP:
				queryBuilder.setTables(MessageGroupTable.TABLE_MESSAGE_GROUP);
				break;
			case SUGGEST:
				queryBuilder.setTables(SuggestTable.TABLE_SUGGEST);
				break;
			default:
				throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		
		if (dbHelper == null) {
			Log.i(TAG, "dbHelper is null");
			// 此时，需要重新登录
			EventBus.getDefault().post(new DbHelperEvent("GeneralContentProvider GeneralDBHelper"));
			Log.i(TAG, "GeneralContentProvider GeneralDBHelper");
			return null;
		}
		
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		
		if (db == null) {
			Logcat.e(TAG, "GeneralContentProvider: writedatabase is null");
			return null;
		}
		
		Cursor cursor = queryBuilder.query(db, projection, selection,selectionArgs, null, null, sortOrder);
		// Make sure that potential listeners are getting notified
		cursor.setNotificationUri(getContext().getContentResolver(), uri);
		return cursor;
		
		
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}

	// 插入语句
	@Override
	public Uri insert(Uri uri, ContentValues values) {
		int uriType = sURIMatcher.match(uri);
		
		if (dbHelper == null){
			EventBus.getDefault().post(new DbHelperEvent("GeneralContentProvider GeneralDBHelper"));
			return null;
		}
		
		SQLiteDatabase sqlDB = dbHelper.getWritableDatabase();
		long id = 0;
		
		switch (uriType) {
			case COOKIE:
				id = sqlDB.insert(CookieTable.TABLE_COOKIE, null, values);
				getContext().getContentResolver().notifyChange(uri, null);
				return Uri.parse(COOKIE_BASE_PATH + "/" + id);
			case USERINFO:
				id = sqlDB.insert(UserInfoTable.TABLE_USERINFO, null, values);
				getContext().getContentResolver().notifyChange(uri, null);
				return Uri.parse(USERINFO_BASE_PATH + "/" + id);
			case MESSAGE:
				id = sqlDB.insert(MessageTable.TABLE_MESSAGES, null, values);
				getContext().getContentResolver().notifyChange(uri, null);
				return Uri.parse(MESSAGE_BASE_PATH + "/" + id);
			case MESSAGE_SUMMARY:
				id = sqlDB.insert(MessageSummaryTable.TABLE_MESSAGE_SUMMARY, null, values);
				getContext().getContentResolver().notifyChange(uri, null);
				return Uri.parse(MESSAGE_SUMMARY_BASE_PATH + "/" + id);
			case CONTACT_USER:
				id = sqlDB.insert(ContactUserTable.TABLE_CONTACT_USER, null, values);
				getContext().getContentResolver().notifyChange(uri, null);
				return Uri.parse(CONTACT_USER_BASE_PATH + "/" + id);
			case MESSAGE_GROUP:
				id = sqlDB.insert(MessageGroupTable.TABLE_MESSAGE_GROUP, null, values);
				getContext().getContentResolver().notifyChange(uri, null);
				return Uri.parse(MESSAGE_GROUP_BASE_PATH + "/" + id);
			case SUGGEST:
				id =sqlDB.insert(SuggestTable.TABLE_SUGGEST, null, values);
				getContext().getContentResolver().notifyChange(uri, null);
				return Uri.parse(SUGGEST_BASE_PATH + "/" + id);
			default:
				throw new IllegalArgumentException("Unknown URI: " + uri);
		}
	}

	// 删除语句
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int uriType = sURIMatcher.match(uri);
		Log.i(TAG, "uriType : " + uriType);
		if (dbHelper == null) {
			EventBus.getDefault().post(new DbHelperEvent("GeneralContentProvider GeneralDBHelper"));
			return 0;
		}
		
		int rowsDeleted = 0;
		if (dbHelper != null) {
			SQLiteDatabase sqlDB = dbHelper.getWritableDatabase();
			
			switch (uriType) {
				case COOKIE:
					rowsDeleted = sqlDB.delete(CookieTable.TABLE_COOKIE, selection,selectionArgs);
					break;
				case USERINFO:
					rowsDeleted = sqlDB.delete(UserInfoTable.TABLE_USERINFO, selection,selectionArgs);
					break;
				case MESSAGE:
					rowsDeleted = sqlDB.delete(MessageTable.TABLE_MESSAGES, selection, selectionArgs);
					break;
				case MESSAGE_SUMMARY:
					rowsDeleted = sqlDB.delete(MessageSummaryTable.TABLE_MESSAGE_SUMMARY, selection, selectionArgs);
					break;
				case CONTACT_USER:
					rowsDeleted = sqlDB.delete(ContactUserTable.TABLE_CONTACT_USER, selection, selectionArgs);
					break;
				case MESSAGE_GROUP:
					rowsDeleted = sqlDB.delete(MessageGroupTable.TABLE_MESSAGE_GROUP, selection, selectionArgs);
					break;
				case SUGGEST:
					rowsDeleted = sqlDB.delete(SuggestTable.TABLE_SUGGEST, selection, selectionArgs);
					break;
				default:
					throw new IllegalArgumentException("Unknown URI: " + uri);
			}
		}

		getContext().getContentResolver().notifyChange(uri, null);
		return rowsDeleted;
	}

	
	// 更新语句
	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		int uriType = sURIMatcher.match(uri);
		if (dbHelper == null) {
			EventBus.getDefault().post(new DbHelperEvent("GeneralContentProvider GeneralDBHelper"));
			return 0;
		}
		
		int rowsUpdated = 0;
		
		if (dbHelper != null) {
			SQLiteDatabase sqlDB = dbHelper.getWritableDatabase();
			
			switch (uriType) {
				case COOKIE:
					rowsUpdated = sqlDB.update(CookieTable.TABLE_COOKIE, values,selection, selectionArgs);
					break;
				case USERINFO:
					rowsUpdated = sqlDB.update(UserInfoTable.TABLE_USERINFO, values,selection, selectionArgs);
					break;
				case MESSAGE:
					rowsUpdated = sqlDB.update(MessageTable.TABLE_MESSAGES, values, selection, selectionArgs);
					break;
				case MESSAGE_SUMMARY:
					rowsUpdated = sqlDB.update(MessageSummaryTable.TABLE_MESSAGE_SUMMARY, values, selection, selectionArgs);
					break;
				case CONTACT_USER:
					rowsUpdated = sqlDB.update(ContactUserTable.TABLE_CONTACT_USER, values, selection, selectionArgs);
					break;
				case MESSAGE_GROUP:
					rowsUpdated = sqlDB.update(MessageGroupTable.TABLE_MESSAGE_GROUP, values, selection, selectionArgs);
					break;
				case SUGGEST:
					rowsUpdated = sqlDB.update(SuggestTable.TABLE_SUGGEST, values, selection, selectionArgs);
					break;
				default:
					throw new IllegalArgumentException("Unknown URI: " + uri);
			}
		}
		
		getContext().getContentResolver().notifyChange(uri, null);
		return rowsUpdated;
	}

	@Override
	public int bulkInsert(Uri uri, ContentValues[] values) {
		int uriType = sURIMatcher.match(uri);
		
		SQLiteDatabase sqlDB = dbHelper.getWritableDatabase();
		int numValues = 0;
		switch (uriType) {
		case SUGGEST:
			    sqlDB.beginTransaction(); //开始事务
			    try {
			        //数据库操作
			       numValues = values.length;
			       for (int i = 0; i < numValues; i++) {
			           insert(uri, values[i]);
			       }
			       sqlDB.setTransactionSuccessful(); //别忘了这句 Commit
			    } finally {
			    	sqlDB.endTransaction(); //结束事务
			    }
			break;
		default:
			break;
		}
	  
	    return numValues;
	}  
	
	@Override
	public ContentProviderResult[] applyBatch(ArrayList<ContentProviderOperation> operations)
			throws OperationApplicationException {
		return super.applyBatch(operations);
	}
	
	// ////////////////////////////////////////////////////////////////////////////////////////////////////



	// 检测Cookie列是否不存在
	private void checkCookieColumns(String[] projection) {
		String[] available = { CookieTable.COLUMN_MOBILE,
				CookieTable.COLUMN_EXPIRE, CookieTable.COLUMN_P,
				CookieTable.COLUMN_DOMAIN, CookieTable.COLUMN_PATH,
				CookieTable.COLUMN_LOGINTIME, CookieTable.COLUMN_ID };

		if (projection != null) {
			// HashSet--便于快速查找--默认的选择，对速度进行了优化
			HashSet<String> requestedColumns = new HashSet<String>(Arrays.asList(projection));
			HashSet<String> availableColumns = new HashSet<String>(Arrays.asList(available));
			// Check if all columns which are requested are available
			if (!availableColumns.containsAll(requestedColumns)) {
				throw new IllegalArgumentException("Unknown columns in projection");
			}
		}
	}
	
	// 检测UserInfo列是否不存在
	private void checkUserInfoColumns(String[] projection) {
		String[] available = {UserInfoTable.COLUMN_ID, UserInfoTable.COLUMN_MOBILE,
				UserInfoTable.COLUMN_PASSWORD,
				UserInfoTable.COLUMN_NAME,
				UserInfoTable.COLUMN_NICKNAME,
				UserInfoTable.COLUMN_GENDER,
				UserInfoTable.COLUMN_AGE,
				UserInfoTable.COLUMN_AVATAR,
				UserInfoTable.COLUMN_LOCATION,
				UserInfoTable.COLUMN_EMAIL, 
				UserInfoTable.COLUMN_IDENTITY};
		if (projection != null) {
			// HashSet--便于快速查找--默认的选择，对速度进行了优化
			HashSet<String> requestedColumns = new HashSet<String>(Arrays.asList(projection));
			HashSet<String> availableColumns = new HashSet<String>(Arrays.asList(available));
			// Check if all columns which are requested are available
			if (!availableColumns.containsAll(requestedColumns)) {
				throw new IllegalArgumentException("Unknown columns in projection");
			}
		}
	}
	

}
