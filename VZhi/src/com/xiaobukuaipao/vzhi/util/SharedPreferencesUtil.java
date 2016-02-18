package com.xiaobukuaipao.vzhi.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public final class SharedPreferencesUtil {

	private final String TAG = SharedPreferencesUtil.class.getSimpleName();
	
	private static SharedPreferencesUtil sInstance;
	
	private SharedPreferences preferences;
	
	private SharedPreferencesUtil(){}
	
	public synchronized static SharedPreferencesUtil getInstance(){
		if(sInstance == null){
			sInstance = new SharedPreferencesUtil();
		}
		
		return sInstance;
	}
	
	public synchronized void build(Context context){
		if(context == null){
			Log.e(TAG,"context must not null");
			return;
		}
		preferences = context.getSharedPreferences(GlobalConstants.PREFERENCE_NAME, Context.MODE_PRIVATE);
	}
	
	public void putString(String value,String key) {
		Editor edit = preferences.edit();
		edit.putString(key, value).commit();
	}

	public String getString(String key) {
		return  preferences.getString(key, "");
	}
	
	public void putBoolean(boolean value ,String key) {
		Logcat.d("@@@", "putBoolean:" + value + " key:" + key);
		Editor edit = preferences.edit();
		edit.putBoolean(key, value).commit();
	}
	
	public boolean getBoolean(String key) {
		Logcat.d("@@@", "getBoolean: key:" + key);
		return preferences.getBoolean(key, false);
	}
	
	public void putSettingMessage(boolean value ,String key){
		Logcat.d("@@@", "putBoolean:" + value + " key:" + key);
		Editor edit = preferences.edit();
		edit.putBoolean(key, value).commit();
	}
	
	public boolean getSettingMessage(String key) {
		Logcat.d("@@@", "getBoolean: key:" + key);
		return preferences.getBoolean(key, true);
	}
}
