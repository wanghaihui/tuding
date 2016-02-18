package com.xiaobukuaipao.vzhi.manager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.SparseArray;

import com.xiaobukuaipao.vzhi.contentprovider.GeneralContentProvider;
import com.xiaobukuaipao.vzhi.database.CookieTable;
import com.xiaobukuaipao.vzhi.database.SuggestTable;
import com.xiaobukuaipao.vzhi.database.UserInfoTable;
import com.xiaobukuaipao.vzhi.domain.user.UserBasic;
import com.xiaobukuaipao.vzhi.util.Logcat;

/**
 * 统一的Database Manager, 不管理IM模块
 */
public class GeneralDbManager {

	public static final String TAG = GeneralDbManager.class.getSimpleName();

	private static final GeneralDbManager dbManager = new GeneralDbManager();
	
	private Context context;
	
	private GeneralDbManager() {
		
	}
	
	public static GeneralDbManager getInstance() {
		return dbManager;
	}
	
	public void setContext(Context context) {
		if (context == null) {
			throw new RuntimeException("context is null");
		}
		this.context = context;
	}
	
	/**
	 * 保存Cookie
	 * @param uid
	 * @param mobile
	 * @param p
	 * @param domain
	 * @param path
	 * @param loginTime
	 * @param expire
	 */
	public synchronized void insertToDatabase(Long uid, String mobile, String p,
			String domain, String path, Long loginTime, Long expire) {

		ContentValues values = new ContentValues();
		values.put(CookieTable.COLUMN_ID, uid);
		values.put(CookieTable.COLUMN_MOBILE, mobile);
		values.put(CookieTable.COLUMN_P, p);
		values.put(CookieTable.COLUMN_LOGINTIME, loginTime);
		values.put(CookieTable.COLUMN_EXPIRE, expire);
		values.put(CookieTable.COLUMN_DOMAIN, domain);
		values.put(CookieTable.COLUMN_PATH, path);
		// 此时先清空一下数据库，防止数据库已经存在Cookie
		Cursor cursor = context.getContentResolver().query(GeneralContentProvider.COOKIE_CONTENT_URI, null, null, null,null);
		if (cursor != null && cursor.moveToFirst()) {
			context.getContentResolver().delete(GeneralContentProvider.COOKIE_CONTENT_URI, null, null);
			cursor.close();
		}
		
		// 插入数据库
		context.getContentResolver().insert(GeneralContentProvider.COOKIE_CONTENT_URI, values);

		// 此时已经的得到用户的uid和mobile信息，可以将用户的数据存入用户的UserInfo表中
		
		Cursor cursorUser = context.getContentResolver().query(GeneralContentProvider.USERINFO_CONTENT_URI, null, null, null,null);
		if (cursorUser != null && cursorUser.moveToFirst()) {
			context.getContentResolver().delete(GeneralContentProvider.USERINFO_CONTENT_URI, null, null);
			cursorUser.close();
		}
		
		values.clear();
		values.put(UserInfoTable.COLUMN_ID, uid);
		values.put(UserInfoTable.COLUMN_MOBILE, mobile);
		context.getContentResolver().insert(
				GeneralContentProvider.USERINFO_CONTENT_URI, values);
		values.clear();
		
	}
	
	/**
	 * 插入UserInfo Table
	 */
	public synchronized void insertToUserInfoTable(UserBasic userBasic) {
		Cursor cursor = context.getContentResolver().query(GeneralContentProvider.USERINFO_CONTENT_URI, null, null, null, null);
		if (cursor != null && cursor.moveToFirst()) {
			context.getContentResolver().delete(GeneralContentProvider.USERINFO_CONTENT_URI, null, null);
			cursor.close();
		}
		
		ContentValues values = new ContentValues();
		values.put(UserInfoTable.COLUMN_ID, Long.valueOf(userBasic.getId()));
		values.put(UserInfoTable.COLUMN_MOBILE, userBasic.getMobile());
		values.put(UserInfoTable.COLUMN_NAME, userBasic.getRealname());
		values.put(UserInfoTable.COLUMN_NICKNAME, userBasic.getNickname());
		values.put(UserInfoTable.COLUMN_GENDER, userBasic.getGender());
		values.put(UserInfoTable.COLUMN_AGE, userBasic.getAge());
		// 匿名头像
		values.put(UserInfoTable.COLUMN_AVATAR, userBasic.getNickavatar());
		// 实名头像
		values.put(UserInfoTable.COLUMN_REALAVATAR, userBasic.getRealavatar());
		values.put(UserInfoTable.COLUMN_LOCATION, userBasic.getCity());
		values.put(UserInfoTable.COLUMN_EMAIL, userBasic.getPersonalemail());
		// 插入数据库
		context.getContentResolver().insert(GeneralContentProvider.USERINFO_CONTENT_URI,
				values);
		values.clear();
	}
	
	/**
	 * 更新UserInfo Table
	 */
	public synchronized void updateToUserInfoTable(UserBasic userBasic) {
		
		ContentValues values = new ContentValues();
		values.put(UserInfoTable.COLUMN_ID, Long.valueOf(userBasic.getId()));
		values.put(UserInfoTable.COLUMN_MOBILE, userBasic.getMobile());
		values.put(UserInfoTable.COLUMN_NAME, userBasic.getRealname());
		values.put(UserInfoTable.COLUMN_NICKNAME, userBasic.getNickname());
		values.put(UserInfoTable.COLUMN_GENDER, userBasic.getGender());
		values.put(UserInfoTable.COLUMN_AGE, userBasic.getAge());
		// 匿名头像
		values.put(UserInfoTable.COLUMN_AVATAR, userBasic.getNickavatar());
		// 实名头像
		values.put(UserInfoTable.COLUMN_REALAVATAR, userBasic.getRealavatar());
		values.put(UserInfoTable.COLUMN_LOCATION, userBasic.getCity());
		values.put(UserInfoTable.COLUMN_EMAIL, userBasic.getPersonalemail());
		
		// 更新数据库
		context.getContentResolver().update(GeneralContentProvider.USERINFO_CONTENT_URI, values, null, null);
		values.clear();
	}
	/**
	 * 更新UserInfo Table--匿名头像的url
	 */
	public synchronized void updateUserTableNickName(String nickname) {
		ContentValues values = new ContentValues();
		values.put(UserInfoTable.COLUMN_AVATAR, nickname);
		context.getContentResolver().update(GeneralContentProvider.USERINFO_CONTENT_URI, values, null, null);
		values.clear();
	}
	
	
	
	/**
	 * 保存用户密码
	 * @param password
	 */
	public synchronized void saveUserPasswordToDatabase(String password) {
		ContentValues values = new ContentValues();
		values.put(UserInfoTable.COLUMN_PASSWORD, password);
		context.getContentResolver().update(GeneralContentProvider.USERINFO_CONTENT_URI,values, null, null);

		String[] projection = { UserInfoTable.COLUMN_PASSWORD };
		Cursor cursor = context.getContentResolver().query(GeneralContentProvider.USERINFO_CONTENT_URI,
				projection, null, null, null);
		if (cursor != null && cursor.moveToFirst()) {
			cursor.getString(cursor.getColumnIndexOrThrow(UserInfoTable.COLUMN_PASSWORD));
			// always close the cursor
			cursor.close();
		}
	}
	
	// 首先判断数据库中是否存在Cookie
	public synchronized boolean isExistCookie() {
		// 此时先清空一下数据库，防止数据库已经存在Cookie
		
		Cursor cursor = context.getContentResolver().query(GeneralContentProvider.COOKIE_CONTENT_URI, null, null, null,
				null);
		
		if (cursor != null && cursor.moveToFirst()) {
			cursor.close();
			return true;
		}
		
		return false;
	}
	
	
	/**
	 * 清除Cookie
	 */
	public synchronized void removeCookieInfo() {
		// 此时先清空一下数据库，防止数据库已经存在Cookie
		Cursor cursor = context.getContentResolver().query(
				GeneralContentProvider.COOKIE_CONTENT_URI, null, null, null,
				null);
		if (cursor != null && cursor.moveToFirst()) {
			context.getContentResolver().delete(GeneralContentProvider.COOKIE_CONTENT_URI, null, null);
			cursor.close();
		}
	}
	
	/**
	 * 清除UserInfo
	 */
	public synchronized void removeUserInfo() {
		// 此时先清空一下数据库，防止数据库已经存在UserInfo
		Cursor cursor = context.getContentResolver().query(GeneralContentProvider.USERINFO_CONTENT_URI, 
				null, null, null, null);
		if (cursor != null && cursor.moveToFirst()) {
			context.getContentResolver().delete(GeneralContentProvider.USERINFO_CONTENT_URI, null, null);
			cursor.close();
		}
	}
	
	
	/**
	 * 从Cookie中获得用户的uid或者电话号码
	 */
	public synchronized String getTableSuffixFromCookie() {
		String mobile = "";
		Cursor cursor = context.getContentResolver().query(
				GeneralContentProvider.COOKIE_CONTENT_URI, null, null, null,
				null);
		if (cursor != null && cursor.moveToFirst()) {
			mobile = cursor.getString(cursor.getColumnIndex(CookieTable.COLUMN_MOBILE));
			cursor.close();
		}
		
		return mobile;
	}
	
	public synchronized String getUidFromCookie() {
		String uid = "";
		Cursor cursor = context.getContentResolver().query(
				GeneralContentProvider.COOKIE_CONTENT_URI, null, null, null,
				null);
		if (cursor != null && cursor.moveToFirst()) {
			uid = cursor.getString(cursor.getColumnIndex(CookieTable.COLUMN_ID));
			cursor.close();
		}
		
		return uid;
	}
	
	/**
	 * 从UserInfo表中获得用户的头像
	 */
	public synchronized String getUserAvatar() {
		String avatar = null;
		Cursor cursor = context.getContentResolver().query(GeneralContentProvider.USERINFO_CONTENT_URI, null, null, null,
				null);
		
		if (cursor != null && cursor.moveToFirst()) {
			avatar = cursor.getString(cursor.getColumnIndex(UserInfoTable.COLUMN_REALAVATAR));
			cursor.close();
		}
		
		return avatar;
	}
	
	/**
	 * 获取工作经历是否完善
	 * 
	 * @return
	 */
	public synchronized int getWorkPerform(){
		int expr = 0;
		Cursor cursor = context.getContentResolver().query(GeneralContentProvider.USERINFO_CONTENT_URI, null, null, null,
				null);
		
		if (cursor != null && cursor.moveToFirst()) {
			expr = cursor.getInt(cursor.getColumnIndex(UserInfoTable.COLUMN_WORKEXPR));
			cursor.close();
		}
		Logcat.d("@@@", "status : " + expr);
		return expr;
	}
	
	/**
	 * 更新工作经历
	 * 
	 * @return
	 */
	public synchronized int setWorkPerform(int status){
		Logcat.d("@@@", "status : " + status);
		ContentValues values = new ContentValues();
		values.put(UserInfoTable.COLUMN_WORKEXPR, status);
		return context.getContentResolver().update(GeneralContentProvider.USERINFO_CONTENT_URI, values, null, null);
	}
	

	/**
	 * 将suggest保存到数据库
	 * 
	 * @param type
	 * @param typeId
	 * @param typeName
	 */
	public synchronized void insertToDatabase(int type, int typeId, String typeName) {
		ContentValues values = new ContentValues();
		values.put(SuggestTable.COLUMN_TYPE, type);
		values.put(SuggestTable.COLUMN_TYPE_ID, typeId);
		values.put(SuggestTable.COLUMN_TYPE_NAME, typeName);
//		values.put(SuggestTable.COLUMN_TYPE_SUBNAME,subname);,String subname
		// 插入数据库
		context.getContentResolver().insert(GeneralContentProvider.SUGGEST_CONTENT_URI,values);
	}
	
	/**
	 * @param type
	 * @param typeId
	 * @param typeName
	 */
	public synchronized void updateDatabase(int type, int typeId, String typeName){
		ContentValues values = new ContentValues();
		values.put(SuggestTable.COLUMN_TYPE, type);
		values.put(SuggestTable.COLUMN_TYPE_ID, typeId);
		values.put(SuggestTable.COLUMN_TYPE_NAME, typeName);
//		values.put(SuggestTable.COLUMN_TYPE_SUBNAME,subname);,String subname
		context.getContentResolver().update(GeneralContentProvider.SUGGEST_CONTENT_URI, values, null, null);
	}
	
	/**
	 * 
	 * @param type
	 * @return
	 */
	public synchronized 	SparseArray<String>  getSuggest(int type){
		
		Cursor cursor = context.getContentResolver().query(GeneralContentProvider.SUGGEST_CONTENT_URI, new String[]{ SuggestTable.COLUMN_TYPE_ID,SuggestTable.COLUMN_TYPE_NAME }, SuggestTable.COLUMN_TYPE + " = ?", new String[]{String.valueOf(type)}, null);
		SparseArray<String>  result = null;
		if(cursor != null){
			result = new SparseArray<String>();
			while(cursor.moveToNext()){
				int id = cursor.getInt(cursor.getColumnIndex(SuggestTable.COLUMN_TYPE_ID));
				String name = cursor.getString(cursor.getColumnIndex(SuggestTable.COLUMN_TYPE_NAME));
				result.put(id, name);
			}
			cursor.close();
		}
		return result;
	} 
	
	
	/**
	 * 
	 * @return
	 */
	public synchronized boolean suggestIsExsist(int type, int typeId){
		String where = SuggestTable.COLUMN_TYPE + " = ? and " + SuggestTable.COLUMN_TYPE_ID + " = ?";
		
		Cursor cursor = context.getContentResolver().query(GeneralContentProvider.SUGGEST_CONTENT_URI, null, where , new String[]{String.valueOf(type),String.valueOf(typeId)}, null);
		if(cursor != null && cursor.moveToNext()){
			cursor.close();
			return true;
		}
		
		return false;
	}
}
