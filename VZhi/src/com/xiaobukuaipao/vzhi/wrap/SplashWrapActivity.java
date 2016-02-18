package com.xiaobukuaipao.vzhi.wrap;

import android.os.Bundle;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.xiaobukuaipao.vzhi.BaseFragmentActivity;
import com.xiaobukuaipao.vzhi.event.ProfileEventLogic;
import com.xiaobukuaipao.vzhi.event.RegisterEventLogic;
import com.xiaobukuaipao.vzhi.view.MaterialLoadingDialog;

/**
 * 基本信息可以不用序列化，因为此时，所有的activity都通过ActivityManager进行管理，所以只要程序不被杀死，回退后都能保证数据存在
 * 继承BaseActivity 同时，基本信息改为数据库存储，注册多少基本信息，存储多少
 * 
 * 在使用序列化传输数据时,思路清晰便与从用户输入中辨别数据类型
 * 
 */
public abstract class SplashWrapActivity extends BaseFragmentActivity {
	
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
		mSavedInstanceState = savedInstanceState;

		if (getIntent() != null) {
			if (mSavedInstanceState == null) {
				mSavedInstanceState = getIntent().getExtras();
			}
			if (mSavedInstanceState == null) {
				mSavedInstanceState = new Bundle();
			}
		}

		handleSavedInstanceState(mSavedInstanceState);

		mRegisterEventLogic = new RegisterEventLogic();
		mProfileEventLogic = new ProfileEventLogic();
		mRegisterEventLogic.register(this);
		mProfileEventLogic.register(this);

		// 初始化数据
		initUIAndData();
	}

	public void handleSavedInstanceState(Bundle savedInstanceState) {

	}

	public Bundle getSavedInstanceState() {
		return mSavedInstanceState;
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {

		outState = mSavedInstanceState;
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		mSavedInstanceState = savedInstanceState;
		handleSavedInstanceState(savedInstanceState);
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
	
}