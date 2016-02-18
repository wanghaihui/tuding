package com.xiaobukuaipao.vzhi;

import android.R.anim;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.xiaobukuaipao.vzhi.event.InfoResult;
import com.xiaobukuaipao.vzhi.manager.GeneralDbManager;
import com.xiaobukuaipao.vzhi.util.GlobalConstants;
import com.xiaobukuaipao.vzhi.util.StringUtils;
import com.xiaobukuaipao.vzhi.util.VToast;
import com.xiaobukuaipao.vzhi.view.LoadingDialog;
import com.xiaobukuaipao.vzhi.wrap.ProfileWrapActivity;

import de.greenrobot.event.EventBus;

public class SigningSelfActivity extends ProfileWrapActivity implements OnClickListener, TextWatcher{

	private LoadingDialog loadingDialog;
	private EditText mEditArea;
	private View mPublishBtn;
	private CheckBox mStatusCheckbox;
	private String signed;
	/**
	 * 是否完善信息的提示对话框
	 */
	private AlertDialog mAlertDialog;
	
	@Override
	public void initUIAndData() {
		setContentView(R.layout.activity_signing_self);
		setHeaderMenuByLeft(this);
		setHeaderMenuByCenterTitle(R.string.personal_sign);
		
		signed = getIntent().getStringExtra("sign");
		
		isAutoHideSoftInput = false;
		
		loadingDialog = new LoadingDialog(this, R.style.loading_dialog);
		loadingDialog.setLoadingTipStr(getString(R.string.general_tips_publishing));
		mEditArea = (EditText) findViewById(R.id.edit_area);
		mEditArea.addTextChangedListener(this);

		mPublishBtn = findViewById(R.id.sign_publish);
		mPublishBtn.setOnClickListener(this);
		
		mStatusCheckbox = (CheckBox) findViewById(R.id.sign_status);
		
		if(signed != null){
			mEditArea.setText(signed);
		}
	}

	@Override
	public void onEventMainThread(Message msg) {
		if(msg.obj instanceof InfoResult){
			InfoResult infoResult = (InfoResult) msg.obj;
			switch (msg.what) {
			
			case R.id.setting_privacy_profile_set:
				mProfileEventLogic.setSignself(mStatusCheckbox.isChecked() ? "1" : "0", mEditArea.getText().toString());
				break;
				
			case R.id.profile_sign_self:
				VToast.show(this, getString(R.string.general_tips_publish_success));
				EventBus.getDefault().post(RESULT_OK);
				finish();
				break;
			case R.id.post_resume_complete_status_get:
				JSONObject completeResult = (JSONObject) JSONObject.parse(infoResult.getResult());
				if(completeResult != null){
					JSONObject detail = completeResult.getJSONObject(GlobalConstants.JSON_DETAIL);
					
					if (detail != null) {
						int work = detail.getInteger(GlobalConstants.JSON_WORKEXPR);
						if (work == 0) {
							showCompleteDialog();
							break;
						}
					}
					GeneralDbManager.getInstance().setWorkPerform(1);
					lowPressed();
				}
				break;
			default:
				break;
			}
		}else if(msg.obj instanceof VolleyError){
			VToast.show(this, getString(R.string.general_tips_network_unknow));
		}
		
		if(loadingDialog.isShowing()) loadingDialog.dismiss();

	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		//统计长度  暂时没添加
	}

	@Override
	public void afterTextChanged(Editable s) {
		
	}

	@Override
	public void onClick(View v) {
		if(StringUtils.isEmpty(mEditArea.getText().toString())){
			VToast.show(this, getString(R.string.general_tips_publishing_null));
			return;
		}
		completionQuery();
	}
	
	
	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(anim.fade_in, R.anim.slide_out_to_bottom);
	}

	private void completionQuery() {
		loadingDialog.show();
		if(GeneralDbManager.getInstance().getWorkPerform() == 1){
			// 如果個人信息完善
			lowPressed();
		}else{
			mProfileEventLogic.cancel(R.id.post_resume_complete_status_get);
			mProfileEventLogic.getCompletion();
		}
	}
	
	/**
	 * 發布自我推薦
	 */
	private void lowPressed() {
		if (mStatusCheckbox.isChecked()) {
			// 實名
			mProfileEventLogic.setPrivacyProfile(String.valueOf(3));
		} else {
			// 匿名
			mProfileEventLogic.setSignself(mStatusCheckbox.isChecked()?"1":"0", mEditArea.getText().toString());
		}
	}
	
	private void showCompleteDialog() {
		if(mAlertDialog != null && mAlertDialog.isShowing()) return;
		
		mAlertDialog = new AlertDialog.Builder(SigningSelfActivity.this)
		.setTitle(R.string.general_tips)
		.setMessage(R.string.complete_workexpr_info).setNegativeButton(R.string.general_tips_to_complete,new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog,int which) {
				Intent intent = getIntent();
				intent.setClass(SigningSelfActivity.this,PersonalEditPageActivity.class);
				startActivity(intent);
			}
		})
		.setPositiveButton(getString(R.string.btn_cancel),null).create();
		mAlertDialog.show();
	}
}
