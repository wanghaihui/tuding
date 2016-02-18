package com.xiaobukuaipao.vzhi;

import android.R.anim;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;

import com.android.volley.VolleyError;
import com.xiaobukuaipao.vzhi.event.InfoResult;
import com.xiaobukuaipao.vzhi.util.StringUtils;
import com.xiaobukuaipao.vzhi.util.VToast;
import com.xiaobukuaipao.vzhi.view.LoadingDialog;
import com.xiaobukuaipao.vzhi.wrap.ProfileWrapActivity;

import de.greenrobot.event.EventBus;

public class SigningIntroduceActivity extends ProfileWrapActivity implements OnClickListener, TextWatcher{

	private LoadingDialog loadingDialog;
	private EditText mEditArea;
	private View mPublishBtn;
	private CheckBox mStatusCheckbox;
	private String signed;
	
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
		mStatusCheckbox.setVisibility(View.INVISIBLE);
		
		if(signed != null){
			mEditArea.setText(signed);
		}
	}

	@Override
	public void onEventMainThread(Message msg) {
		if(msg.obj instanceof InfoResult){
			switch (msg.what) {
			case R.id.profile_sign_introduce:
				VToast.show(this, getString(R.string.general_tips_publish_success));
				EventBus.getDefault().post(RESULT_OK);
				finish();
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
		loadingDialog.show();
		mProfileEventLogic.setIntroduce(mEditArea.getText().toString());
	}
	
	
	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(anim.fade_in, R.anim.slide_out_to_bottom);
	}

}
