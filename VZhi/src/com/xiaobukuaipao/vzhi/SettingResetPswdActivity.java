package com.xiaobukuaipao.vzhi;

import android.database.Cursor;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.xiaobukuaipao.vzhi.contentprovider.GeneralContentProvider;
import com.xiaobukuaipao.vzhi.database.CookieTable;
import com.xiaobukuaipao.vzhi.event.InfoResult;
import com.xiaobukuaipao.vzhi.util.StringUtils;
import com.xiaobukuaipao.vzhi.util.VToast;
import com.xiaobukuaipao.vzhi.wrap.ProfileWrapActivity;

public class SettingResetPswdActivity extends ProfileWrapActivity implements
		OnClickListener {
	
	private EditText mNewMobile;
	private EditText mVerifyCode;
	private EditText mPassword;
	
	private Button mVerifyBtn;
	
	private boolean isVerifyCode = false;
	private boolean isPassword = false;
	
	private String mobile;
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
				mVerifyBtn.setText(SettingResetPswdActivity.this.getResources().getString(R.string.retry_send));
				mVerifyBtn.setSelected(false);
				if (!mVerifyBtn.isEnabled()) {
					mVerifyBtn.setEnabled(true);
				}
			}
		};
	};
	
	@Override
	public void initUIAndData() {
		setContentView(R.layout.activity_setting_reset_pswd);
		setHeaderMenuByCenterTitle(R.string.setting_forget_password_reset);
		setHeaderMenuByLeft(this);
		setHeaderMenuByRight(R.string.general_tips_finished).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(allCompleted()){
					mProfileEventLogic.cancel(R.id.setting_newmobile_set);
					mProfileEventLogic.setPswdWithVercode(mNewMobile.getText().toString().replaceAll("\\s+", ""),
							mPassword.getText().toString(),
							mVerifyCode.getText().toString());
				}
			}
		});
		
		mNewMobile = (EditText) findViewById(R.id.mobile_edit);
		mVerifyCode = (EditText) findViewById(R.id.verify_code_edit);
		mPassword = (EditText) findViewById(R.id.pswd_edit);
		
		mVerifyBtn = (Button) findViewById(R.id.verify_retry);
		mVerifyBtn.setText(this.getResources().getString(R.string.get_verify_code));
		mVerifyBtn.setOnClickListener(this);
		
		//读取数据库 获取mobile
		Cursor cookieCursor = getContentResolver().query(GeneralContentProvider.COOKIE_CONTENT_URI, null, null, null, null);
		while (cookieCursor.moveToNext()) {
			mobile = cookieCursor.getString(cookieCursor.getColumnIndex(CookieTable.COLUMN_MOBILE));
		}
		cookieCursor.close();
		mNewMobile.setText(mobile);
		
//		//增加体验 在固定的字符数之间添加空格
//		mNewMobile.addTextChangedListener(new TextWatcher() {
//			
//			@Override
//			public void onTextChanged(CharSequence s, int start, int before,
//					int count) {
//				
//				if (StringUtils.isCellphone(s.toString().replaceAll("\\s+", ""))) {
//					// 可以
//					isMobile = true;
//				} else {
//					isMobile = false;
//				}
//				
//				mVerifyBtn.setSelected(false);
//				mTime = 0;
//				
//				switch (s.length()) {
//				case 3:
//				case 8:
//					if (count == 1) {
//						mNewMobile.setText(s + " ");
//						mNewMobile.setSelection(s.length() + 1);
//					}
//					if (count == 0) {
//						mNewMobile.setText(s.subSequence(0, s.length() - 1));
//						mNewMobile.setSelection(s.length() - 1);
//					}
//					break;
//				default:
//					break;
//				}
//			}
//
//			@Override
//			public void beforeTextChanged(CharSequence s, int start, int count,
//					int after) {
//
//			}
//
//			@Override
//			public void afterTextChanged(Editable s) {
//				
//			}
//
//		});
		
		mVerifyCode.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (StringUtils.isVerifyCode(s.toString())) {
					// 可以
					isVerifyCode = true;
				} else {
					isVerifyCode = false;
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
				if (StringUtils.isPassword(s.toString())) {
					// 可以
					isPassword = true;
				} else {
					isPassword = false;
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
	}

	@Override
	public void onEventMainThread(Message msg) {
		if(msg.obj instanceof InfoResult){
			InfoResult infoResult = (InfoResult) msg.obj;
			switch (msg.what) {
			case R.id.setting_password_vercode_send:
				if(infoResult.getMessage().getStatus() == 0){
					
				}
				VToast.show(this, infoResult.getMessage().getMsg());
				
				break;
			case R.id.setting_password_set:
				if(infoResult.getMessage().getStatus() == 0){
					AppActivityManager.getInstance().finishActivity(SettingResetPswdActivity.this);
				}
				VToast.show(this, infoResult.getMessage().getMsg());
				break;
			default:
				break;
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.verify_retry:
			String newmobile = mNewMobile.getText().toString().replaceAll("\\s+", "");
			if(StringUtils.isEmpty(newmobile)){
				VToast.show(this, getString(R.string.general_tips_mobile_number_not_none));
				return;
			}
			
			if(!StringUtils.isCellphone(newmobile)){
				VToast.show(this, getString(R.string.general_tips_mobile_number_formatter_error));
				return;
			}

			if (mVerifyBtn.isEnabled()) {
				mVerifyBtn.setEnabled(false);
				// 执行发送验证码请求
				timerHandler.post(mTimerRunnable);
				mTime = 60;
			}
			mProfileEventLogic.sendVercodeMobile(newmobile);
			break;
		default:
			break;
		}
	}
	
	private boolean allCompleted() {
		if(StringUtils.isEmpty(mVerifyCode.getText().toString())){
			VToast.show(this, getString(R.string.general_tips_verifycode_not_none));
			return false;
		}
		
		if(StringUtils.isEmpty(mPassword.getText().toString())){
			VToast.show(this, getString(R.string.general_tips_password_not_none));
			return false;
		}
		
		
		if(!isVerifyCode){
			VToast.show(this, getString(R.string.general_tips_verifycode_formatter_error));
			return false;
		}
		
		if(!isPassword){
			VToast.show(this, getString(R.string.general_tips_password_formatter_error));
			return false;
		}
		return true;
	}
}
