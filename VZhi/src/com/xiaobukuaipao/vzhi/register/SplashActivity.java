package com.xiaobukuaipao.vzhi.register;

import java.util.HashMap;
import java.util.Map;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.xiaobukuaipao.vzhi.AppActivityManager;
import com.xiaobukuaipao.vzhi.MainRecruitActivity;
import com.xiaobukuaipao.vzhi.R;
import com.xiaobukuaipao.vzhi.VZhiApplication;
import com.xiaobukuaipao.vzhi.contentprovider.GeneralContentProvider;
import com.xiaobukuaipao.vzhi.database.CookieTable;
import com.xiaobukuaipao.vzhi.database.MultiUserDatabaseHelper;
import com.xiaobukuaipao.vzhi.domain.user.UserBasic;
import com.xiaobukuaipao.vzhi.event.InfoResult;
import com.xiaobukuaipao.vzhi.manager.GeneralDbManager;
import com.xiaobukuaipao.vzhi.util.GlobalConstants;
import com.xiaobukuaipao.vzhi.util.StringUtils;
import com.xiaobukuaipao.vzhi.wrap.SplashWrapActivity;

public class SplashActivity extends SplashWrapActivity {
	
	private static final int SHOW_TIME_MIN = 1000;// 最小显示时间
	private long mStartTime;// 开始时间
	
	private long mFirstConnectionTime;
	@SuppressWarnings("unused")
	private long mSecondConnectionTime;
	
	private Handler mHandler = new Handler();
	private AlertDialog mAlertDialog;
	
	private Complete status;
	private UserBasic userBasic;
	private Runnable loginRunnable = new Runnable() {
		@Override
		public void run() {
			Intent intent = new Intent(SplashActivity.this, MainRecruitActivity.class);
			if (getIntent().getIntExtra(GlobalConstants.PAGE, 0) == 1) {
				intent.putExtra(GlobalConstants.PAGE, 1);
			}
			startActivity(intent);
			AppActivityManager.getInstance().finishActivity(SplashActivity.this);
		}
	};
	
	private Runnable registerRunnable = new Runnable() {
		@Override
		public void run() {
			Intent intent = new Intent(SplashActivity.this, StartRegister.class);
			startActivity(intent);
			AppActivityManager.getInstance().finishActivity(SplashActivity.this);
		}
	};


	@Override
	public void initUIAndData() {
		getWindow().getDecorView().findViewById(android.R.id.content).setBackgroundResource(R.drawable.guide_one);
		mStartTime = System.currentTimeMillis();//记录开始时间
		
		// 处理Push Notification
		if (!StringUtils.isEmpty(getIntent().getStringExtra("uid"))) {
			Log.i(TAG, "splash activity : " + getIntent().getStringExtra("uid"));
			MultiUserDatabaseHelper.getInstance().createOrOpenDatabase(getApplicationContext(), getIntent().getStringExtra("uid"));
		}
		
		// uid会单独保存在一个SharedPreferences中
		SharedPreferences sp = this.getSharedPreferences("tuding_uid", MODE_PRIVATE);
		if (sp.getLong("uid", 0) > 0) {
			Log.i(TAG, "Shared Preferences : " + String.valueOf(sp.getLong("uid", 0)));
			MultiUserDatabaseHelper.getInstance().createOrOpenDatabase(getApplicationContext(), String.valueOf(sp.getLong("uid", 0)));
		} else {
			Log.i(TAG, "Shared Preferences : " + String.valueOf(sp.getLong("uid", 0)));
		}
		
		// 首先判断是否存在Cookie
		if (GeneralDbManager.getInstance().isExistCookie()) {
			Log.i(TAG, "is exist cookie");
			mRegisterEventLogic.fastLogin();
			mFirstConnectionTime = System.currentTimeMillis();
		} else {
			Log.i(TAG, "is not exist cookie");
			long loadingTime = System.currentTimeMillis() - mStartTime;// 计算一下总共花费的时间
	        if (loadingTime < SHOW_TIME_MIN) {// 如果比最小显示时间还短，就延时进入MainActivity，否则直接进入
	          mHandler.postDelayed(registerRunnable, SHOW_TIME_MIN - loadingTime);
	        } else {
	          mHandler.post(registerRunnable);
	        }
		}
	}
	
	@Override
	public void onEventMainThread(Message msg) {
		
		if (msg.obj instanceof InfoResult) {
			
			InfoResult infoResult = (InfoResult) msg.obj;
			
			switch (msg.what) {
				case R.id.register_fast_login:
					if (infoResult.getMessage().getStatus() == 0) {
						// VToast.show(this, infoResult.getMessage().getMsg());
						Log.i(TAG, "fast login getStatus = 0");
						Log.i(TAG, "interrupt time : " + (System.currentTimeMillis() - mFirstConnectionTime));
						// 此时，在这里发送验证码成功，会通过短信的形式返回
						checkSessionCookie(infoResult.getResponse().headers);
						// GeneralUserManager.getInstance().setMobile(GeneralDbManager.getInstance().getTableSuffixFromCookie());
						// 首先，删除UserInfoTable
						GeneralDbManager.getInstance().removeUserInfo();
						// 然后获取UserInfo
						mProfileEventLogic.getUserBasicInfo();
					} else {
						Log.i(TAG, "fast login getStatus != 0");
						AppActivityManager.getInstance().finishAllActivity();
						Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
						startActivity(intent);
					}
					break;
				case R.id.profile_basic_info:
					JSONObject jsonObject = (JSONObject) JSONObject.parse(infoResult.getResult());
					if (jsonObject == null) {
						return;
					}
					if(jsonObject.getJSONObject(GlobalConstants.JSON_USERBASIC) == null){
						return;
					}
					Log.i(TAG, infoResult.getResult());
					userBasic = new UserBasic(jsonObject.getJSONObject(GlobalConstants.JSON_USERBASIC));
					GeneralDbManager.getInstance().insertToUserInfoTable(userBasic);
					mRegisterEventLogic.getRegisterCompletion();
//					if(userBasic.getIdentity() != -1){
//						if(userBasic.getIdentity() == 0){ // 判断白领注册信息是否完善
//							mRegisterEventLogic.getRegisterCompletion();
//						}else{
//							mProfileEventLogic.getRecruitServiceStatus();
//						}
//					}else{
//						Log.e(TAG, "identity not exsist");
//					}
					break;
//				case R.id.hr_setting_get_recruit_status:
				case R.id.register_completion_get:
					JSONObject seeker = JSONObject.parseObject(infoResult.getResult());
					if (seeker != null) {
						JSONObject complete = seeker.getJSONObject(GlobalConstants.JSON_COMPLETE);
						if(complete != null){
							status = new Complete(complete);
							SharedPreferences sp = this.getSharedPreferences("tuding_uid", Context.MODE_PRIVATE);
							sp.edit().putBoolean(GlobalConstants.INTENTION_COMPLETE, status.intention == 1).commit();
							
//							if (StringUtils.isNotEmpty(complete.getString(GlobalConstants.JSON_INTENTION))) {
//								// 白领
//								sp.edit().putBoolean(GlobalConstants.INTENTION_COMPLETE, 
//										complete.getInteger(GlobalConstants.JSON_INTENTION) == 1 ? true : false).commit();
//							} else {
//								// HR
//								sp.edit().putBoolean(GlobalConstants.INTENTION_COMPLETE, true).commit();
//							}
							
						}
					}
					getCompletion();
			}
		} else if(msg.obj instanceof VolleyError){
			if (!GeneralDbManager.getInstance().isExistCookie()) {
				// 此时，登录成功
				Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				finish();

			} else {
				// 此时，登录成功
		        mHandler.postDelayed(loginRunnable,614);
			}
		}
	}

	private void login() {
		// 此时，更新Cookie成功
		mHandler.removeCallbacks(loginRunnable);
		// 此时，登录成功
		long loadingTime = System.currentTimeMillis() - mStartTime;// 计算一下总共花费的时间
		if (loadingTime < SHOW_TIME_MIN) {// 如果比最小显示时间还短，就延时进入MainActivity，否则直接进入
		  mHandler.postDelayed(loginRunnable, SHOW_TIME_MIN
		      - loadingTime);
		} else {
		  mHandler.post(loginRunnable);
		}
	}
	/**
	 * 判断基本信息是否完善
	 */
	private void getCompletion() {
		
		boolean flag = false;
		
		if(status.basic == 0){//基本信息不完善
			flag = true;
		}else{
			if(userBasic.getIdentity() == 1 && status.hr == 0) { //hr信息不完善
				flag = false;
			}else if(userBasic.getIdentity() == 0 && status.intention == 0){//求职意向可以不完善
				flag = false;
			}else{//登陆成功
				flag = false;
			}
		}
		if(flag){
			mAlertDialog = new AlertDialog.Builder(this)
			.setCancelable(false)
			.setTitle(R.string.general_tips)
			.setMessage(R.string.register_tips)
			.setPositiveButton(R.string.general_tips_re_register, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if (status.basic == 0) { // 基本信息不完善
						Intent intent = new Intent(SplashActivity.this, PurposeActivity.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(intent);
					}else{
					}
				}
			})
			.create();
			if(!mAlertDialog.isShowing()){
				mAlertDialog.show();
			}
		} else {
			login();
		}
		
	}
	/**
	 * Checks the response headers for session cookie and saves it if it finds
	 * it.
	 * 
	 * @param headers
	 *            Response Headers.
	 */
	private void checkSessionCookie(Map<String, String> headers) {
		if (headers.containsKey(GlobalConstants.SET_COOKIE_KEY)) {
			String cookie = headers.get(GlobalConstants.SET_COOKIE_KEY);
			Map<String, String> customHeaders = new HashMap<String, String>();
			String[] split = cookie.split("\n");

			// 四行--四个Cookie行--解析Cookie
			for (String sc : split) {
				if (cookie.length() > 0) {
					String[] splitCookie = sc.split(";");
					for (int i = 0; i < splitCookie.length; i++) {
						String[] entry = splitCookie[i].split("=");
						if (entry.length == 1) {
							// 当只有key时，value为null
							customHeaders.put(entry[0], null);
						} else {
							customHeaders.put(entry[0], entry[1]);
						}
					}
				}
			}

			VZhiApplication.mCookie_T = customHeaders.get(GlobalConstants.COOKIE_T);
			
			// 先创建数据库
			MultiUserDatabaseHelper.getInstance().createOrOpenDatabase(getApplicationContext(), customHeaders.get(GlobalConstants.COOKIE_UID));
			
			// 将Cookie插入到数据库中
			insertToDatabase(Long.valueOf(customHeaders
					.get(GlobalConstants.COOKIE_UID)), customHeaders
					.get(GlobalConstants.COOKIE_MOBILE), customHeaders
					.get(GlobalConstants.COOKIE_P), customHeaders
					.get(GlobalConstants.COOKIE_DOMAIN), customHeaders
					.get(GlobalConstants.COOKIE_PATH), Long.valueOf(System
							.currentTimeMillis() / 1000), Long.valueOf(0));
		}
	}
	
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
		Cursor cursor = getContentResolver().query(GeneralContentProvider.COOKIE_CONTENT_URI, null, null, null,null);
		
		if (cursor != null && cursor.moveToFirst()) {
			getContentResolver().delete(GeneralContentProvider.COOKIE_CONTENT_URI, null, null);
			cursor.close();
		}
		
		// 插入数据库
		getContentResolver().insert(GeneralContentProvider.COOKIE_CONTENT_URI, values);
		
	}
	
	class  Complete{
		
		public Complete(JSONObject jsonObject){
			if(jsonObject != null){
				if(jsonObject.getInteger(GlobalConstants.JSON_BASIC) != null){
					basic = jsonObject.getInteger(GlobalConstants.JSON_BASIC);
				}
				if(jsonObject.getInteger(GlobalConstants.JSON_HR) != null){
					hr = jsonObject.getInteger(GlobalConstants.JSON_HR);
				}
				if(jsonObject.getInteger(GlobalConstants.JSON_INTENTION) != null){
					intention = jsonObject.getInteger(GlobalConstants.JSON_INTENTION);
				}
			}
		}
		int basic = -1;
		int hr = -1;
		int intention = -1;
	}
}
