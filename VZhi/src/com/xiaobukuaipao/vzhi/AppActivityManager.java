package com.xiaobukuaipao.vzhi;

import java.util.Stack;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.util.Log;

public class AppActivityManager {
	
	public static final String TAG = AppActivityManager.class.getSimpleName();
	private static Stack<Activity> activityStack;
	
	private static AppActivityManager instance = new AppActivityManager();
	
	private AppActivityManager() {
		if (activityStack == null) {
			activityStack = new Stack<Activity>();
		}
	}
	
	/**
	 * 单例模式
	 */
	public static AppActivityManager getInstance() {
		return instance;
	}
	
	/**
	 * 添加到栈中
	 */
	public void addActivity(Activity activity) {
		if (activityStack == null) {
			activityStack = new Stack<Activity>();
		}
		activityStack.add(activity);
	}
	
	/**
	 * 获取指定类名的Activity
	 */
	@SuppressWarnings("unchecked")
	public <T extends Activity> T getActivity(Class<T> cls) {
		for (Activity activity : activityStack) {
			if (activity.getClass().equals(cls)) {
				return (T) activity;
			}
		}
		return null;
	}
	
	/**
	 * 获得当前的Activity(最后一个压入栈的)
	 */
	public Activity getCurrentActivity() {
		return activityStack.lastElement();
	}
	
	/**
	 * 结束当前的Activity
	 */
	public void finishActivity() {
		Activity activity = activityStack.lastElement();
		finishActivity(activity);
	}
	
	public void finishActivity(Activity activity) {
		if (activity != null) {
			Log.i(TAG, "finish activity : " + activity.getLocalClassName());
			activityStack.remove(activity);
			activity.finish();
			activity = null;
		}
	}
	
	/**
	 * 结束指定类名的Activity
	 */
	public void finishActivity(Class<?> cls) {
		for(Activity activity : activityStack) {
			if (activity.getClass().equals(cls)) {
				finishActivity(activity);
			}
		}
	}
	
	/**
	 * 结束所有的Activity
	 */
	public void finishAllActivity() {
		Log.i(TAG, "stack activity size : " + activityStack.size());
		/*for (int i=0, size=activityStack.size(); i < size; i++) {
			Log.i(TAG, "stack activity size : " + activityStack.size());
			if (null != activityStack.get(i)) {
				activityStack.get(i).finish();
			}
		}*/
		while (!activityStack.isEmpty()) {
			activityStack.pop().finish();
		}
		
		// 清空栈
		activityStack.clear();
	}
	
	/**
	 * 退出应用程序
	 */
	@SuppressWarnings("deprecation")
	public void appExit(Context context) {
		try {
			finishAllActivity();
			ActivityManager activityMgr = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
			// 废弃的API--2.2
			activityMgr.restartPackage(context.getPackageName());
			System.exit(0);
		} catch (Exception e) {
			
		}
	}
}
