package com.xiaobukuaipao.vzhi.register;

import java.util.HashMap;
import java.util.Map;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.xiaobukuaipao.vzhi.AppActivityManager;
import com.xiaobukuaipao.vzhi.LookLookActivity;
import com.xiaobukuaipao.vzhi.R;
import com.xiaobukuaipao.vzhi.VZhiApplication;
import com.xiaobukuaipao.vzhi.WebSearchActivity;
import com.xiaobukuaipao.vzhi.database.MultiUserDatabaseHelper;
import com.xiaobukuaipao.vzhi.event.ApiConstants;
import com.xiaobukuaipao.vzhi.event.InfoResult;
import com.xiaobukuaipao.vzhi.manager.GeneralDbManager;
import com.xiaobukuaipao.vzhi.util.GlobalConstants;
import com.xiaobukuaipao.vzhi.util.StringUtils;
import com.xiaobukuaipao.vzhi.util.VToast;
import com.xiaobukuaipao.vzhi.view.MaterialLoadingDialog;
import com.xiaobukuaipao.vzhi.wrap.RegisterWrapActivity;

public class RegisterExActivity extends RegisterWrapActivity {
	
	private EditText mMobileEdit;
	private EditText mVerifyCode;
	private EditText mPassword;
	
	private Button mVerifyBtn;
	private Button mRegister;
	
	private TextView mContractTv;
	private CheckBox mContractCbox;
	
	// 60秒倒计时
	private int mTime = 60;
	private Handler timerHandler = new Handler();
	private Runnable mTimerRunnable = new Runnable() {
		
		public void run() {
			mTime--;
			if (mTime > 0) {
				timerHandler.postDelayed(mTimerRunnable, 1000);
				mVerifyBtn.setText(getString(R.string.general_second, mTime));
				mVerifyBtn.setText(getString(R.string.retry_send_num,mTime));
				mVerifyBtn.setSelected(true);
			} else {
				mVerifyBtn.setText(RegisterExActivity.this.getResources().getString(R.string.retry_send));
				mVerifyBtn.setSelected(false);
				if (!mVerifyBtn.isEnabled()) {
					mVerifyBtn.setEnabled(true);
				}
			}
		};
		
	};
	
	public void chooseRedirectActivity(View view) {
		switch (view.getId()) {
			case R.id.login_btn:
				Intent intent = new Intent(RegisterExActivity.this, LoginActivity.class);
				startActivity(intent);
				AppActivityManager.getInstance().finishActivity(RegisterExActivity.this);
				break;
			case R.id.look_btn:
				intent = new Intent(RegisterExActivity.this, LookLookActivity.class);
				startActivity(intent);
				break;
		}
	}

	@Override
	public void initUIAndData() {
		setContentView(R.layout.activity_register_ex);
		
		mMobileEdit = (EditText) findViewById(R.id.mobile_edit);
		mVerifyCode = (EditText) findViewById(R.id.verify_code_edit);
		mPassword = (EditText) findViewById(R.id.pswd_edit);
		
		mVerifyBtn = (Button) findViewById(R.id.verify_retry);
		mVerifyBtn.setText(this.getResources().getString(R.string.get_verify_code));
		
		materialLoadingDialog = new MaterialLoadingDialog(this, R.style.material_loading_dialog);
		materialLoadingDialog.setLoadingTipStr(this.getResources().getString(R.string.loading));
		
		mRegister = (Button) findViewById(R.id.register_btn);
		
		mContractCbox = (CheckBox) findViewById(R.id.contract_cbox);
		mContractTv = (TextView) findViewById(R.id.contract_tv);
		
		String contract = getString(R.string.agree);
		SpannableStringBuilder builder = new SpannableStringBuilder(contract);
		builder.setSpan(new ClickableSpan() {
			
			@Override
			public void onClick(View widget) {
				Intent intent = new Intent();
				intent.putExtra(GlobalConstants.INNER_URL, ApiConstants.COPYRIGHTS);
				intent.setClass(RegisterExActivity.this, WebSearchActivity.class);
				startActivity(intent);
			}

			@Override
			public void updateDrawState(TextPaint ds) {
				ds.linkColor = getResources().getColor(R.color.base_interface_color);
				super.updateDrawState(ds);
				ds.setUnderlineText(false);
			}
			
			
		}, 6, contract.length()	, SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE);
		mContractTv.setMovementMethod(LinkMovementMethod.getInstance());
		mContractTv.setText(builder);
		
		onClickListenerBySaveDataAndRedirectActivity(R.id.login_btn);
		onClickListenerBySaveDataAndRedirectActivity(R.id.look_btn);
		
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
				
				mVerifyBtn.setSelected(false);
				mTime = 0;
				
				switch (s.length()) {
				case 3:
				case 8:
					if (count == 1) {
						mMobileEdit.setText(s + " ");
						mMobileEdit.setSelection(s.length() + 1);
					}
					if (count == 0) {
						mMobileEdit.setText(
								s.subSequence(0, s.length() - 1));
						mMobileEdit.setSelection(s.length() - 1);
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
		
		mVerifyCode.addTextChangedListener(new TextWatcher() {

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
		
		// 验证码
		mVerifyBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				String phoneStr = mMobileEdit.getText().toString().replaceAll("\\s+", "");
				
				Log.i(TAG, phoneStr);
				
				if (StringUtils.isCellphone(phoneStr)) {
					mRegisterEventLogic.cancel(R.id.register_get_code);
					mRegisterEventLogic.getVeryCode(phoneStr);
				} else {
					VToast.show(RegisterExActivity.this, getString(R.string.register_dialog_phone));
					return;
				}

				if (mVerifyBtn.isEnabled()) {
					mVerifyBtn.setEnabled(false);
					// 执行发送验证码请求
					timerHandler.post(mTimerRunnable);
					mTime = 60;
				}
				
			}
		});
		
		
		mRegister.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String phoneStr = mMobileEdit.getText().toString().replaceAll("\\s+", "");
				String verifyCode = mVerifyCode.getText().toString();
				String pswdStr = mPassword.getText().toString();
				
				if(!mContractCbox.isChecked()){
					VToast.show(RegisterExActivity.this, getString(R.string.agree_front));
					return;
				}
				
				if(StringUtils.isEmpty(phoneStr)){
					VToast.show(RegisterExActivity.this, getString(R.string.general_tips_mobile_number_not_none));
					return;
				}
				if (!StringUtils.isCellphone(phoneStr)) {
					VToast.show(RegisterExActivity.this, getString(R.string.general_tips_mobile_number_formatter_error));
					return;
				}
				if(StringUtils.isEmpty(verifyCode)){
					VToast.show(RegisterExActivity.this, getString(R.string.general_tips_verifycode_not_none));
					return;
				}
				if(!StringUtils.isVerifyCode(verifyCode)){
					VToast.show(RegisterExActivity.this, getString(R.string.general_tips_verifycode_formatter_error));
					return;
				}
				
				if(StringUtils.isEmpty(pswdStr)){
					VToast.show(RegisterExActivity.this, getString(R.string.general_tips_password_not_none));
					return;
				}
				
				if (!StringUtils.isPassword(pswdStr)){
					VToast.show(RegisterExActivity.this, getString(R.string.general_tips_password_formatter_error));
					return;
				}
				materialLoadingDialog.show();
				mRegisterEventLogic.cancel(R.id.register_ver_code);
				mRegisterEventLogic.sendVeryCode(mMobileEdit.getText().toString().replace(" ", ""), mVerifyCode.getText().toString());
			}
			
		});
		
	}
	
	@Override
	public void onEventMainThread(Message msg) {
		if (msg.obj instanceof InfoResult) {
			InfoResult infoResult = (InfoResult) msg.obj;
			switch (msg.what) {
				case R.id.register_get_code:
					if (infoResult.getMessage().getStatus() == 0) {
					
						// 此时，在这里发送验证码成功，会通过短信的形式返回
						
					} else {
						
					}
					VToast.show(this, infoResult.getMessage().getMsg());
					// dismiss progress dialog
					break;
				case R.id.register_ver_code:
					if (infoResult.getMessage().getStatus() == 0) {
						checkSessionCookie(infoResult.getResponse().headers);
						// 之后，进行密码验证
						mRegisterEventLogic.cancel(R.id.register_set_psw);
						mRegisterEventLogic.setPassword(mPassword.getText().toString());
					} else {
						VToast.show(this, infoResult.getMessage().getMsg());
						if(materialLoadingDialog.isShowing()){
							materialLoadingDialog.dismiss();
						}
					}
					break;
					
				case R.id.register_set_psw:
					if (infoResult.getMessage().getStatus() == 0) {

						// 密码注册成功
						// 将密码保存到数据库中
						GeneralDbManager.getInstance().saveUserPasswordToDatabase(mPassword.getText().toString());
						
						if(materialLoadingDialog.isShowing()){
							materialLoadingDialog.dismiss();
						}
						Intent intent = new Intent(this, PurposeActivity.class);
						startActivity(intent);
					} else {
						
						if(materialLoadingDialog.isShowing()){
							materialLoadingDialog.dismiss();
						}
					}
					VToast.show(this, infoResult.getMessage().getMsg());
					break;
			}
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
			
			MultiUserDatabaseHelper.getInstance().createOrOpenDatabase(getApplicationContext(), customHeaders
					.get(GlobalConstants.COOKIE_UID));
			
			// 将Cookie插入到数据库中
			GeneralDbManager.getInstance().insertToDatabase(Long.valueOf(customHeaders
					.get(GlobalConstants.COOKIE_UID)), customHeaders
					.get(GlobalConstants.COOKIE_MOBILE), customHeaders
					.get(GlobalConstants.COOKIE_P), customHeaders
					.get(GlobalConstants.COOKIE_DOMAIN), customHeaders
					.get(GlobalConstants.COOKIE_PATH), Long.valueOf(System
							.currentTimeMillis() / 1000), Long.valueOf(0));
		}
	}
}


