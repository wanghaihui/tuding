package com.xiaobukuaipao.vzhi.register;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.xiaobukuaipao.vzhi.AppActivityManager;
import com.xiaobukuaipao.vzhi.R;
import com.xiaobukuaipao.vzhi.event.InfoResult;
import com.xiaobukuaipao.vzhi.util.StringUtils;
import com.xiaobukuaipao.vzhi.util.VToast;
import com.xiaobukuaipao.vzhi.wrap.RegisterWrapActivity;

public class ForgetPswdActvity extends RegisterWrapActivity{
	private EditText mMobileEdit;
	private EditText mVerifyCode;
	private EditText mPassword;
	
	private Button mVerifyBtn;
	private Button mFind;
	
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
				mVerifyBtn.setText(ForgetPswdActvity.this.getResources().getString(R.string.retry_send));
				mVerifyBtn.setSelected(false);
				if (!mVerifyBtn.isEnabled()) {
					mVerifyBtn.setEnabled(true);
				}
			}
		};
	};
	
	public void chooseRedirectActivity(View view) {
		Intent intent = null;
		switch (view.getId()) {
			case R.id.register_btn:
				intent = new Intent(ForgetPswdActvity.this, RegisterExActivity.class);
				startActivity(intent);
				break;
			case R.id.login_btn:
				intent = new Intent(ForgetPswdActvity.this, LoginActivity.class);
				startActivity(intent);
				break;
		}
		AppActivityManager.getInstance().finishActivity(ForgetPswdActvity.this);
	}

	
	@Override
	public void initUIAndData() {
		setContentView(R.layout.activity_forget_pswd);
		
		onClickListenerBySaveDataAndRedirectActivity(R.id.register_btn);
		onClickListenerBySaveDataAndRedirectActivity(R.id.login_btn);
		mMobileEdit = (EditText) findViewById(R.id.mobile_edit);
		mVerifyCode = (EditText) findViewById(R.id.verify_code_edit);
		mPassword = (EditText) findViewById(R.id.pswd_edit);
		
		mVerifyBtn = (Button) findViewById(R.id.verify_retry);
		mVerifyBtn.setText(this.getResources().getString(R.string.get_verify_code));
		
		mFind = (Button) findViewById(R.id.find_btn);
		setUIListeners();
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
						mMobileEdit.setText(s.subSequence(0, s.length() - 1));
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
					mProfileEventLogic.cancel(R.id.setting_password_vercode_send);
					mProfileEventLogic.sendVercodeMobile(phoneStr);
				} else {
					VToast.show(ForgetPswdActvity.this, getString(R.string.register_dialog_phone));
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
		
		
		mFind.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String phoneStr = mMobileEdit.getText().toString().replaceAll("\\s+", "");
				String verifyCode = mVerifyCode.getText().toString();
				String pswdStr = mPassword.getText().toString();
				
				if(StringUtils.isEmpty(phoneStr)){
					VToast.show(ForgetPswdActvity.this, getString(R.string.general_tips_mobile_number_not_none));
					return;
				}
				if (!StringUtils.isCellphone(phoneStr)) {
					VToast.show(ForgetPswdActvity.this, getString(R.string.general_tips_mobile_number_formatter_error));
					return;
				}
				if(StringUtils.isEmpty(verifyCode)){
					VToast.show(ForgetPswdActvity.this, getString(R.string.general_tips_verifycode_not_none));
					return;
				}
				if(!StringUtils.isVerifyCode(verifyCode)){
					VToast.show(ForgetPswdActvity.this, getString(R.string.general_tips_verifycode_formatter_error));
					return;
				}
				
				if(StringUtils.isEmpty(pswdStr)){
					VToast.show(ForgetPswdActvity.this, getString(R.string.general_tips_password_not_none));
					return;
				}
				
				if (!StringUtils.isPassword(pswdStr)){
					VToast.show(ForgetPswdActvity.this, getString(R.string.general_tips_password_formatter_error));
					return;
				}
				mProfileEventLogic.cancel(R.id.setting_password_set);
				mProfileEventLogic.setPswdWithVercode(phoneStr, pswdStr, mVerifyCode.getText().toString());
			}
		});
		
	}
	@Override
	public void onEventMainThread(Message msg) {
		if (msg.obj instanceof InfoResult) {
			InfoResult infoResult = (InfoResult) msg.obj;
			switch (msg.what) {
				case R.id.setting_password_vercode_send:
					if (infoResult.getMessage().getStatus() == 0) {
						// 此时，在这里发送验证码成功，会通过短信的形式返回
						
					} else {
						mVerifyBtn.setSelected(false);
						mTime = 0;
					}
					VToast.show(this, infoResult.getMessage().getMsg());
					// dismiss progress dialog
					break;
				case R.id.setting_password_set:
					if (infoResult.getMessage().getStatus() == 0) {
						Intent intent = new Intent(this, LoginActivity.class);
						startActivity(intent);
						AppActivityManager.getInstance().finishActivity(ForgetPswdActvity.this);
					} else {
					}
					VToast.show(this, infoResult.getMessage().getMsg());
					break;
			}
		}
	}
}
