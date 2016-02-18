package com.xiaobukuaipao.vzhi.database;

import android.content.Context;
import android.util.Log;

import com.xiaobukuaipao.vzhi.contentprovider.GeneralContentProvider;
import com.xiaobukuaipao.vzhi.util.Logcat;
import com.xiaobukuaipao.vzhi.util.StringUtils;

/**
 * 多用户数据库帮助类
 */
public class MultiUserDatabaseHelper {
	
	public static final String TAG = MultiUserDatabaseHelper.class.getSimpleName();
	// 需要判断是否为空
	private GeneralDBHelper mGeneralDBHelper = null;
	
	private static final MultiUserDatabaseHelper mMultiUserHelper = new MultiUserDatabaseHelper();
	
	private MultiUserDatabaseHelper() {
		
	}
	
	public static MultiUserDatabaseHelper getInstance() {
		
		return mMultiUserHelper;
	}
	
	public GeneralDBHelper getGeneralDBHelper() {
		return mGeneralDBHelper;
	}
	
	// 创建或者打开数据库
	public void createOrOpenDatabase(Context context, String uid) {
		if (!StringUtils.isEmpty(uid)) {
			// 此时，存在用户的uid
			StringBuilder dbNameBuilder = new StringBuilder();
			dbNameBuilder.append("tuding_");
			dbNameBuilder.append(uid);
			dbNameBuilder.append(".db");
			Logcat.d("@@@", "dName:  " + dbNameBuilder.toString());
			
			if (mGeneralDBHelper == null) {
				mGeneralDBHelper = new GeneralDBHelper(context, dbNameBuilder.toString());
				GeneralContentProvider.dbHelper = mGeneralDBHelper;
			} else {
				GeneralContentProvider.dbHelper = mGeneralDBHelper;
			}
		} else {
			Log.i(TAG, "uid is empty");
		}
	}
	
	// 当用户退出登录的时候，关闭数据库
	public void closeDatabase() {
		if (mGeneralDBHelper != null) {
			// Close any open database object
			mGeneralDBHelper.close();
			mGeneralDBHelper = null;
			Log.i(TAG, "general db helper close");
		}
		
		GeneralContentProvider.dbHelper = null;
	}
	
}
