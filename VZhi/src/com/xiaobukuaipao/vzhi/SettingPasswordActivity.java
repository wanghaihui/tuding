package com.xiaobukuaipao.vzhi;

import android.os.Message;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;

import com.xiaobukuaipao.vzhi.event.InfoResult;
import com.xiaobukuaipao.vzhi.util.StringUtils;
import com.xiaobukuaipao.vzhi.util.VToast;
import com.xiaobukuaipao.vzhi.view.LoadingDialog;
import com.xiaobukuaipao.vzhi.widget.FormItemByLineLayout;
import com.xiaobukuaipao.vzhi.wrap.ProfileWrapActivity;

public class SettingPasswordActivity extends ProfileWrapActivity implements
		OnClickListener {

	private FormItemByLineLayout mOldPswd;
	private FormItemByLineLayout mNewPswd;
	private FormItemByLineLayout mNewPswd2;

	private LoadingDialog loadingDialog;
	@Override
	public void initUIAndData() {
		setContentView(R.layout.activity_setting_password);
		setHeaderMenuByCenterTitle(R.string.setting_modify_password);
		setHeaderMenuByRight(R.string.general_tips_save).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//验证密码 然后再发送请求
				if(StringUtils.isEmpty(mOldPswd.getFormContentText())){
					VToast.show(v.getContext(),getString(R.string.setting_pswd_tips,mOldPswd.getFormLabelText()));
					return;
				}
				if(!StringUtils.isPassword(mOldPswd.getFormContentText())){
					VToast.show(v.getContext(),getString(R.string.setting_pswd_tips3,mOldPswd.getFormLabelText()));
					return;
				}
				if(StringUtils.isEmpty(mNewPswd.getFormContentText())){
					VToast.show(v.getContext(),getString(R.string.setting_pswd_tips,mNewPswd.getFormLabelText()));
					return;
				}

				if(!StringUtils.isPassword(mNewPswd.getFormContentText())){
					VToast.show(v.getContext(),getString(R.string.setting_pswd_tips3,mNewPswd.getFormLabelText()));
					return;
				}
				if(StringUtils.isEmpty(mNewPswd2.getFormContentText())){
					VToast.show(v.getContext(),getString(R.string.setting_pswd_tips,mNewPswd2.getFormLabelText()));
					return;
				}
				
				if(!mNewPswd.getFormContentText().equals(mNewPswd2.getFormContentText())){
					VToast.show(v.getContext(),getString(R.string.setting_pswd_tips2,mNewPswd.getFormLabelText(),mNewPswd2.getFormLabelText()));
					return;
				}
				mProfileEventLogic.setPswdWithOldPswd(mOldPswd.getFormContentText(), mNewPswd.getFormContentText());
				loadingDialog.show();
			}
		});
		setHeaderMenuByLeft(this);
		
		mOldPswd = (FormItemByLineLayout) findViewById(R.id.setting_orin_password);
		mNewPswd = (FormItemByLineLayout) findViewById(R.id.setting_new_password);
		mNewPswd2 = (FormItemByLineLayout) findViewById(R.id.setting_new_password2);
		
		mOldPswd.getInfoEdit().setPassword(true);
		mNewPswd.getInfoEdit().setPassword(true);
		mNewPswd2.getInfoEdit().setPassword(true);
		
		mOldPswd.getFormContent().setInputType(InputType.TYPE_CLASS_TEXT| InputType.TYPE_TEXT_VARIATION_PASSWORD);
		mNewPswd.getFormContent().setInputType(InputType.TYPE_CLASS_TEXT| InputType.TYPE_TEXT_VARIATION_PASSWORD);
		mNewPswd2.getFormContent().setInputType(InputType.TYPE_CLASS_TEXT| InputType.TYPE_TEXT_VARIATION_PASSWORD);
		
		mOldPswd.setOnClickListener(this);
		mNewPswd.setOnClickListener(this);
		mNewPswd2.setOnClickListener(this);
		
		mNewPswd.getFormContent().addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				
			}
		});
		
		loadingDialog = new LoadingDialog(this);
	}

	@Override
	public void onEventMainThread(Message msg) {
		if(msg.obj instanceof InfoResult){
			InfoResult infoResult = (InfoResult) msg.obj;
			switch (msg.what) {
			case R.id.register_get_code:
				if(infoResult.getMessage().getStatus() == 0){
					
				}
				VToast.show(this, infoResult.getMessage().getMsg());
				
				break;
			case R.id.setting_password_set_with_oldpswd:
				if(infoResult.getMessage().getStatus() == 0){
					AppActivityManager.getInstance().finishActivity(SettingPasswordActivity.this);
				}
				loadingDialog.dismiss();
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
		case R.id.setting_orin_password:
			mOldPswd.setEdit(true);
			break;
		case R.id.setting_new_password:
			mNewPswd.setEdit(true);
			break;
		case R.id.setting_new_password2:
			mNewPswd2.setEdit(true);
			break;
		default:
			break;
		}
	}
}
