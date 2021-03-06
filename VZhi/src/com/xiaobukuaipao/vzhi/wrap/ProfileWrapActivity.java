package com.xiaobukuaipao.vzhi.wrap;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;

import com.xiaobukuaipao.vzhi.AppActivityManager;
import com.xiaobukuaipao.vzhi.BaseFragmentActivity;
import com.xiaobukuaipao.vzhi.event.DbHelperEvent;
import com.xiaobukuaipao.vzhi.event.PositionEventLogic;
import com.xiaobukuaipao.vzhi.event.ProfileEventLogic;
import com.xiaobukuaipao.vzhi.event.RegisterEventLogic;
import com.xiaobukuaipao.vzhi.event.SocialEventLogic;
import com.xiaobukuaipao.vzhi.register.SplashActivity;


public abstract class ProfileWrapActivity extends BaseFragmentActivity {
	private static final String TAG = ProfileWrapActivity.class.getSimpleName();
	// 保存的数据
	protected Bundle mSavedInstanceState;
	protected ProfileEventLogic mProfileEventLogic;
	protected RegisterEventLogic mRegisterEventLogic;
	protected PositionEventLogic mPositionEventLogic;
	protected SocialEventLogic mSocialEventLogic;
	protected View mParentView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		handleSavedInstanceState(savedInstanceState);

		//配置网络访问
		mProfileEventLogic = new ProfileEventLogic();
		mRegisterEventLogic = new RegisterEventLogic();
		mPositionEventLogic = new PositionEventLogic();
		mSocialEventLogic = new SocialEventLogic();
		mSocialEventLogic.register(this);
		mRegisterEventLogic.register(this);
		mProfileEventLogic.register(this);
		mPositionEventLogic.register(this);
		
		//EventBus.getDefault().register(this);
		// 初始化数据
		initUIAndData();
	}

	public void handleSavedInstanceState(Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			if (savedInstanceState.getBoolean(STATE_RELOGIN)) {
				Intent intent = new Intent(this, SplashActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
		}
	}

	public Bundle getSavedInstanceState() {
		return mSavedInstanceState;
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		Log.i("DataTest", "onSaveInstanceState");
		outState.putBoolean(STATE_RELOGIN, true);
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		if (savedInstanceState.getBoolean(STATE_RELOGIN)) {
			AppActivityManager.getInstance().finishAllActivity();
			
			Intent intent = new Intent(this, SplashActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
		}
	}

	public abstract void initUIAndData();

	public abstract void onEventMainThread(Message msg);

	@Override
	public void setContentView(int layoutResID) {
		mParentView = View.inflate(this, layoutResID, null);
		super.setContentView(layoutResID);
	}

	@Override
	public void setContentView(View view) {
		mParentView = view;
		super.setContentView(view);
	}
	

	@Override
	protected void onDestroy() {
		mProfileEventLogic.unregister(this);
		mRegisterEventLogic.unregister(this);
		mPositionEventLogic.unregister(this);
		mSocialEventLogic.unregister(this);
		
		super.onDestroy();
	}
	
	/**
	 * EventBus回调
	 * @param event
	 */
	public void onEventMainThread(DbHelperEvent event) {
		
		ActivityManager manager = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
        RunningTaskInfo info = manager.getRunningTasks(1).get(0);
        String className = info.topActivity.getClassName();              //完整类名
        
        if (className.equals("com.xiaobukuaipao.vzhi.LoginActivity")) {
        	
        } else {
			AppActivityManager.getInstance().finishAllActivity();
			
			Log.i(TAG, "onEventMainThread DbHelperEvent");
			
			Intent intent = new Intent(this, SplashActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
        }
	}
	
	/*public void onEventMainThread(TFailedEvent event) {
		MultiUserDatabaseHelper.getInstance().closeDatabase();
		
		SharedPreferences sp = this.getSharedPreferences("tuding_uid", Context.MODE_PRIVATE);  
        sp.edit().putLong("uid", Long.valueOf(0)).commit();
		
		// 此时，将已经登录的标志清空
		ImLoginManager.getInstance().setEverLogined(false);
		
		//  停止ImService服务
		Intent stopImServiceIntent = new Intent(this, ImService.class);
		stopService(stopImServiceIntent);
		
		AppActivityManager.getInstance().appExit(this);
		
		Intent intent = new Intent(this, LoginActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}*/

}
