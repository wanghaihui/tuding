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
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.xiaobukuaipao.vzhi.AppActivityManager;
import com.xiaobukuaipao.vzhi.LookLookActivity;
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
import com.xiaobukuaipao.vzhi.util.NetworkUtils;
import com.xiaobukuaipao.vzhi.util.StringUtils;
import com.xiaobukuaipao.vzhi.util.VToast;
import com.xiaobukuaipao.vzhi.view.MaterialLoadingDialog;
import com.xiaobukuaipao.vzhi.wrap.RegisterWrapActivity;

public class LoginActivity extends RegisterWrapActivity {
	
	private EditText mMobileEdit;
	private EditText mPassword;
	
	private Button mLogin;
	
	private AlertDialog mAlertDialog;
	private Complete status;
	private UserBasic userBasic;
	
	public void chooseRedirectActivity(View view) {
		Intent intent = null;
		switch (view.getId()) {
			case R.id.register_btn:
				intent = new Intent(LoginActivity.this, RegisterExActivity.class);
				startActivity(intent);
				AppActivityManager.getInstance().finishActivity(LoginActivity.this);
				break;
			case R.id.look_btn:
				intent = new Intent(LoginActivity.this, LookLookActivity.class);
				startActivity(intent);
				break;
			case R.id.reset_pswd:
				intent = new Intent(LoginActivity.this, ForgetPswdActvity.class);
				startActivity(intent);
				AppActivityManager.getInstance().finishActivity(LoginActivity.this);
				break;
		}
		
	}

	// 初始化UI
	@Override
	public void initUIAndData() {
		setContentView(R.layout.activity_login);
		
		if (getIntent().getBooleanExtra("another_login", false)) {
			AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);  
	        builder.setTitle(R.string.general_tips);  
	        builder.setMessage(R.string.general_tips_offline);  
	        builder.setPositiveButton(R.string.btn_confirm,  
	                new DialogInterface.OnClickListener() {  
	                    public void onClick(DialogInterface dialog, int whichButton) {  
	                    	
	                    }  
	                });  
	        builder.setNegativeButton(R.string.btn_cancel,  
	                new DialogInterface.OnClickListener() {  
	                    public void onClick(DialogInterface dialog, int whichButton) {  
	                    }  
	                });  
	        builder.show(); 
		}
		
		mMobileEdit = (EditText) findViewById(R.id.mobile_edit);
		mPassword = (EditText) findViewById(R.id.pswd_edit);
		
		mLogin = (Button) findViewById(R.id.login_btn);
		
//		mUserPortrait = (RoundedImageView) findViewById(R.id.user_portrait);
		
		onClickListenerBySaveDataAndRedirectActivity(R.id.register_btn);
		onClickListenerBySaveDataAndRedirectActivity(R.id.look_btn);
		onClickListenerBySaveDataAndRedirectActivity(R.id.reset_pswd);
		
		materialLoadingDialog = new MaterialLoadingDialog(this, R.style.material_loading_dialog);
		materialLoadingDialog.setLoadingTipStr(this.getResources().getString(R.string.logingin));
		
		setUIListeners();
		
	}
	
	@Override
	protected void onResume() {
		// 先清除Cookie
		GeneralDbManager.getInstance().removeCookieInfo();
		super.onResume();
	}
	
	private void setUIListeners() {
		mMobileEdit.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {	
				switch (s.length()) {
				case 3:
				case 8:
					if (count == 0) {
						mMobileEdit.setText(s.subSequence(0, s.length() - 1));
						mMobileEdit.setSelection(s.length() - 1);
					}
					if (count == 1) {
						mMobileEdit.setText(s + " " );
						mMobileEdit.setSelection(s.length() + 1);
					}
					break;
				default:
					break;
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
			}

			@Override
			public void afterTextChanged(Editable s) {
				
			}
		});
		
		
		mPassword.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
			}

			@Override
			public void afterTextChanged(Editable s) {
				
			}
		});
		
		
		mLogin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				String phoneStr = mMobileEdit.getText().toString().replaceAll("\\s+", "");
				String pswdStr = mPassword.getText().toString();
				if(StringUtils.isEmpty(phoneStr)){
					VToast.show(LoginActivity.this, getString(R.string.general_tips_mobile_number_not_none));
					return;
				}
				if (!StringUtils.isCellphone(phoneStr)) {
					VToast.show(LoginActivity.this, getString(R.string.general_tips_mobile_number_formatter_error));
					return;
				}
				if(StringUtils.isEmpty(pswdStr)){
					VToast.show(LoginActivity.this, getString(R.string.general_tips_password_not_none));
					return;
				}
				
				if (!StringUtils.isPassword(pswdStr)){
					VToast.show(LoginActivity.this, getString(R.string.general_tips_password_formatter_error));
					return;
				}
				
				materialLoadingDialog.show();
				
				mRegisterEventLogic.cancel(R.id.register_ver_code);
				mRegisterEventLogic.normalLogin(phoneStr, "", mPassword.getText().toString());
			}
		});
	}
	
	@Override
	public void onEventMainThread(Message msg) {
		if(msg.obj instanceof VolleyError){
			if(materialLoadingDialog.isShowing()){
				materialLoadingDialog.dismiss();
			}
			VToast.show(this, getString(R.string.general_tips_network_unknow));
		}
		
		if (msg.obj instanceof InfoResult) {
			// InfoResult里面包含NetworkResponse
			
			InfoResult infoResult = (InfoResult) msg.obj;
			switch (msg.what) {
				case R.id.register_normal_login:
					
					if (infoResult.getMessage().getStatus() == 0) {
						// 此时，在这里发送验证码成功，会通过短信的形式返回
						Log.i(TAG, infoResult.toString());
						checkSessionCookie(infoResult.getResponse().headers);
						// GeneralUserManager.getInstance().setMobile(GeneralDbManager.getInstance().getTableSuffixFromCookie());
						// 首先，删除UserInfoTable
						GeneralDbManager.getInstance().removeUserInfo();
						// 然后获取UserInfo
						mProfileEventLogic.getUserBasicInfo();
					} else {
						Log.i(TAG, "get status: " + infoResult.getMessage().getStatus());
						// 此时, status不为0, 所以可能是别的status
						if (materialLoadingDialog.isShowing()) {
							materialLoadingDialog.dismiss();
						}
						VToast.show(this, infoResult.getMessage().getMsg());
					}
					
					// dismiss progress dialog
					break;
				case R.id.profile_basic_info:
					JSONObject jsonObject = (JSONObject) JSONObject.parse(infoResult.getResult());
					if (jsonObject == null) {
						return;
					}
					if (jsonObject.getJSONObject(GlobalConstants.JSON_USERBASIC) == null){
						return;
					}
					
					userBasic = new UserBasic(jsonObject.getJSONObject(GlobalConstants.JSON_USERBASIC));
					
					GeneralDbManager.getInstance().insertToUserInfoTable(userBasic);
					
					if(materialLoadingDialog.isShowing()){
						materialLoadingDialog.dismiss();
					}
					mRegisterEventLogic.getRegisterCompletion();
//					if(userBasic.getIdentity() != -1){
//						if(userBasic.getIdentity() == 0){//判断白领注册信息是否完善
//							
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
					Log.i(TAG, "register completion : " + infoResult.getResult());
					if (seeker != null) {
						JSONObject complete = seeker.getJSONObject(GlobalConstants.JSON_COMPLETE);
						if(complete != null){
							status = new Complete(complete);
							SharedPreferences sp = this.getSharedPreferences("tuding_uid", Context.MODE_PRIVATE);
							sp.edit().putBoolean(GlobalConstants.INTENTION_COMPLETE, status.intention == 1).commit();
							
//							if (StringUtils.isNotEmpty(complete.getString(GlobalConstants.JSON_INTENTION))) {
//								// 白领
//								sp.edit().putBoolean(GlobalConstants.INTENTION_COMPLETE, complete.getInteger(GlobalConstants.JSON_INTENTION) == 1).commit();
//							} else {
//								// HR
//								sp.edit().putBoolean(GlobalConstants.INTENTION_COMPLETE, true).commit();
//							}
						}
					}
					getCompletion();
					break;
			}
		} else {
			// 未成功操作
			Log.i(TAG, "failed");
			if (NetworkUtils.isNetWorkAvalible(this)) {
				Toast.makeText(this, "网络未连接", Toast.LENGTH_SHORT).show();
			}
			
			if (materialLoadingDialog.isShowing()) {
				materialLoadingDialog.dismiss();
			}
		}
	}

	/**
	 * 判断基本信息是否完善
	 */
	private void getCompletion() {
//		boolean flag = false;
//		if(status.basic == 0){//基本信息不完善
//			flag = true;
//		}else{
//			if(userBasic.getIdentity() == 1 && status.hr == 0){//hr信息不完善
//				flag = true;
//			}else if(userBasic.getIdentity() == 0 && status.intention == 0){//求职意向可以不完善
//				flag = false;
//			}else{//登陆成功
//				flag = false;
//			}
//		}
		if (status.basic == 0) {
			mAlertDialog = new AlertDialog.Builder(this)
			.setCancelable(false)
			.setTitle(R.string.general_tips)
			.setMessage(R.string.register_tips)
			.setPositiveButton(R.string.general_tips_re_register, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if(status.basic == 0){//基本信息不完善
						Intent intent = new Intent(LoginActivity.this, PurposeActivity.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(intent);
					}
				}
			}).create();
			if(!mAlertDialog.isShowing()){
				mAlertDialog.show();
			}
		} else {
			Intent intent = new Intent(LoginActivity.this, MainRecruitActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			AppActivityManager.getInstance().finishActivity(LoginActivity.this);
			VToast.show(LoginActivity.this, getString(R.string.general_tips_login_success));
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
			Log.i(TAG, "cookie : " + cookie);
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
			
			Log.i(TAG, "uid : " + customHeaders
					.get(GlobalConstants.COOKIE_UID));
			MultiUserDatabaseHelper.getInstance().createOrOpenDatabase(getApplicationContext(), customHeaders
					.get(GlobalConstants.COOKIE_UID));
			
			if (customHeaders.get(GlobalConstants.COOKIE_EXPIRE) == null) {
				Log.i(TAG, "cookie expire is null");
			}
			// 将Cookie插入到数据库中
			insertToDatabase(Long.valueOf(customHeaders
					.get(GlobalConstants.COOKIE_UID)), customHeaders
					.get(GlobalConstants.COOKIE_MOBILE), customHeaders
					.get(GlobalConstants.COOKIE_P), customHeaders
					.get(GlobalConstants.COOKIE_DOMAIN), customHeaders
					.get(GlobalConstants.COOKIE_PATH), Long.valueOf(System.currentTimeMillis() / 1000), 
					Long.valueOf(0));
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
		Cursor cursor = getContentResolver().query(GeneralContentProvider.COOKIE_CONTENT_URI, null, null, null, null);
		if (cursor != null && cursor.moveToFirst()) {
			getContentResolver().delete(GeneralContentProvider.COOKIE_CONTENT_URI, null, null);
			cursor.close();
		}
		
		// 插入数据库
		getContentResolver().insert(GeneralContentProvider.COOKIE_CONTENT_URI,values);
		
	}

	
	class  Complete{
		
		public Complete(JSONObject jsonObject) {
			if (jsonObject != null) {
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
