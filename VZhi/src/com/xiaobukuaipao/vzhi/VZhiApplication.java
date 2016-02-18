package com.xiaobukuaipao.vzhi;

import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import android.app.ActivityManager;
import android.content.Context;
import android.database.Cursor;

import com.baidu.frontia.FrontiaApplication;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.PicassoTools;
import com.umeng.analytics.MobclickAgent;
import com.umeng.analytics.onlineconfig.UmengOnlineConfigureListener;
import com.xiaobukuaipao.vzhi.contentprovider.GeneralContentProvider;
import com.xiaobukuaipao.vzhi.database.CookieTable;
import com.xiaobukuaipao.vzhi.im.ImDbManager;
import com.xiaobukuaipao.vzhi.manager.GeneralDbManager;
import com.xiaobukuaipao.vzhi.notification.ImNotificationManager;
import com.xiaobukuaipao.vzhi.util.GlobalConstants;
import com.xiaobukuaipao.vzhi.util.Logcat;
import com.xiaobukuaipao.vzhi.util.SharedPreferencesUtil;

public class VZhiApplication extends FrontiaApplication {
	
	private static final String TAG = VZhiApplication.class.getSimpleName();
	private static VZhiApplication sInstance;

	// 全局变量--Cookie的T票
	public static String mCookie_T = null;
	
	@Override
	public void onCreate() {
		super.onCreate();
		init();
		
		MobclickAgent.setDebugMode(false);
		MobclickAgent.setCatchUncaughtExceptions(true);
		MobclickAgent.updateOnlineConfig(this);
		MobclickAgent.setOnlineConfigureListener(new UmengOnlineConfigureListener(){
		  @Override
		  public void onDataReceived(JSONObject data) {
		  }
		});
		
	}
	
	private void clear(Context context){
	     ActivityManager activityManger=(ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
	        List<ActivityManager.RunningAppProcessInfo> list=activityManger.getRunningAppProcesses();
	        if(list!=null)
	        for(int i=0;i<list.size();i++)
	        {
	            ActivityManager.RunningAppProcessInfo apinfo=list.get(i);
	            
	            System.out.println("pid---->>>>>>>"+apinfo.pid);
	            System.out.println("processName->> "+apinfo.processName);
	            System.out.println("importance-->>"+apinfo.importance);
	            String[] pkgList=apinfo.pkgList;
	            
	            if(apinfo.importance>ActivityManager.RunningAppProcessInfo.IMPORTANCE_SERVICE)
	            {
	               // Process.killProcess(apinfo.pid);
	                for(int j=0;j<pkgList.length;j++)
	                {
	                    //2.2以上是过时的,请用killBackgroundProcesses代替
	                 /**清理不可用的内容空间**/
	                 //activityManger.restartPackage(pkgList[j]);
	                    activityManger.killBackgroundProcesses(pkgList[j]);
	                } 
	            } 
	        }
	}
	
	
	@Override
	public void onLowMemory() {
		super.onLowMemory();
		clear(this);
	}
	
	public void init() {
		sInstance = this;
		Context ctx = getApplicationContext();
		SharedPreferencesUtil.getInstance().build(ctx);
		ImDbManager.getInstance().setContext(ctx);
		GeneralDbManager.getInstance().setContext(ctx);
		ImNotificationManager.getInstance().setContext(ctx);
		initPicasso(ctx);
	}

	public static VZhiApplication getInstance() {
		return sInstance;
	}

	/**
	 * Adds session cookie to headers if exists.
	 * 
	 * @param headers
	 */
	public final void addSessionCookie(Map<String, String> headers) {
		String[] projection = { CookieTable.COLUMN_ID,
				CookieTable.COLUMN_MOBILE, CookieTable.COLUMN_P };
		Cursor cursor = getApplicationContext().getContentResolver().query(GeneralContentProvider.COOKIE_CONTENT_URI, projection, null,
				null, null);
		if (cursor != null && cursor.moveToFirst()) {
			if (cursor.getCount() > 0) {
				cursor.moveToFirst();
				Long uid = (long) cursor.getInt(cursor.getColumnIndexOrThrow(CookieTable.COLUMN_ID));
				String mobile = cursor.getString(cursor.getColumnIndexOrThrow(CookieTable.COLUMN_MOBILE));
				String p = cursor.getString(cursor.getColumnIndexOrThrow(CookieTable.COLUMN_P));
				StringBuilder builder = new StringBuilder();
				builder.append("uid=");
				builder.append(uid);
				builder.append("\n");
				builder.append("p=");
				builder.append(p);
				builder.append("\n");
				if (mCookie_T != null) {
					builder.append("t=");
					builder.append(mCookie_T);
					builder.append("\n");
				}
				builder.append("mobile=");
				builder.append(mobile);

				headers.put(GlobalConstants.COOKIE_KEY, builder.toString());
				Logcat.d(TAG, headers.toString());
			} else {
				Logcat.d(TAG, " < 000000000000000000");
			}
			// always close the cursor
			cursor.close();
		}
		
	}
	
	/**
	 * 初始化Picasso
	 * @param context
	 */
	private void initPicasso(Context context) {
		PicassoTools.clearCache(Picasso.with(context));
	}
	
}
