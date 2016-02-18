package com.xiaobukuaipao.vzhi.wrap;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.xiaobukuaipao.vzhi.AppActivityManager;
import com.xiaobukuaipao.vzhi.BaseFragmentActivity;
import com.xiaobukuaipao.vzhi.event.DbHelperEvent;
import com.xiaobukuaipao.vzhi.event.ProfileEventLogic;
import com.xiaobukuaipao.vzhi.event.RegisterEventLogic;
import com.xiaobukuaipao.vzhi.register.SplashActivity;
import com.xiaobukuaipao.vzhi.view.MaterialLoadingDialog;

/**
 * 基本信息可以不用序列化，因为此时，所有的activity都通过ActivityManager进行管理，所以只要程序不被杀死，回退后都能保证数据存在
 * 继承BaseActivity 同时，基本信息改为数据库存储，注册多少基本信息，存储多少
 * 
 * 在使用序列化传输数据时,思路清晰便与从用户输入中辨别数据类型
 * 
 */
public abstract class RegisterWrapActivity extends BaseFragmentActivity {

	public final static String TAG = RegisterWrapActivity.class.getSimpleName();

	// 保存的数据
	protected Bundle mSavedInstanceState;
	protected RegisterEventLogic mRegisterEventLogic;
	protected ProfileEventLogic mProfileEventLogic;
	protected View mParentView;
	
	protected MaterialLoadingDialog materialLoadingDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/*mSavedInstanceState = savedInstanceState;

		if (getIntent() != null) {
			if (mSavedInstanceState == null) {
				mSavedInstanceState = getIntent().getExtras();
			}
			if (mSavedInstanceState == null) {
				mSavedInstanceState = new Bundle();
			}
		}*/

		handleSavedInstanceState(savedInstanceState);
		
		mRegisterEventLogic = new RegisterEventLogic();
		mProfileEventLogic = new ProfileEventLogic();
		mRegisterEventLogic.register(this);
		mProfileEventLogic.register(this);
		
		//EventBus.getDefault().register(this);

		// 初始化数据
		initUIAndData();
	}

	public void handleSavedInstanceState(Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			if (savedInstanceState.getBoolean(STATE_RELOGIN)) {
				AppActivityManager.getInstance().finishAllActivity();
				
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
		mRegisterEventLogic.unregister(this);
		mProfileEventLogic.unregister(this);
		
		//EventBus.getDefault().unregister(this);
		super.onDestroy();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			if (getCurrentFocus() != null
					&& getCurrentFocus().getWindowToken() != null) {
				InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
				manager.hideSoftInputFromWindow(getCurrentFocus()
						.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			}
		}
		return super.onTouchEvent(event);
	}
	
	/**
	 * EventBus回调
	 * @param event
	 */
	public void onEventMainThread(DbHelperEvent event) {
		/*AppActivityManager.getInstance().finishAllActivity();
		
		Log.i(TAG, "onEventMainThread DbHelperEvent");
		
		Intent intent = new Intent(this, SplashActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);*/
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