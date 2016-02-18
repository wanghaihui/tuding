package com.xiaobukuaipao.vzhi.register;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.xiaobukuaipao.vzhi.AppActivityManager;
import com.xiaobukuaipao.vzhi.MainRecruitActivity;
import com.xiaobukuaipao.vzhi.R;
import com.xiaobukuaipao.vzhi.event.InfoResult;
import com.xiaobukuaipao.vzhi.util.GlobalConstants;
import com.xiaobukuaipao.vzhi.util.SpannableKeyWordBuilder;
import com.xiaobukuaipao.vzhi.util.VToast;
import com.xiaobukuaipao.vzhi.view.LoadingDialog;
import com.xiaobukuaipao.vzhi.wrap.RegisterWrapActivity;

public class EmailVerifyActivity extends RegisterWrapActivity implements OnClickListener {

	private String email;
	
	private String corp;

	private TextView mEmailVerify;
	
	private AlertDialog.Builder mDialog;
	
	private AlertDialog mDDialog;

	private LoadingDialog loadingDialog;

	@Override
	public void initUIAndData() {
		setContentView(R.layout.activity_email_verify);

		setHeaderMenuByLeft(this);
		setHeaderMenuByCenterTitle(R.string.title_email_verify);
		setHeaderMenuByRight(R.string.register_hr_baseinfo_finished).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				AppActivityManager.getInstance().finishAllActivity();
				Intent intent = getIntent().setClass(EmailVerifyActivity.this, MainRecruitActivity.class);
				intent.putExtra(GlobalConstants.PAGE, 2);
				startActivity(intent);
			}
		});
		
		email = getIntent().getStringExtra(GlobalConstants.NAME);
		corp = getIntent().getStringExtra(GlobalConstants.CORP_ID);
				
		mEmailVerify = (TextView) findViewById(R.id.tv_email_verify);
		mEmailVerify.setText(email);
		
		findViewById(R.id.btn_retry_send_email).setOnClickListener(this);
		findViewById(R.id.btn_not_received_email).setOnClickListener(this);
		mDialog = new AlertDialog.Builder(this);
		loadingDialog = new LoadingDialog(this, R.style.loading_dialog);
	}

	@Override
	public void onEventMainThread(Message msg) {
		if(msg.obj instanceof InfoResult){
			InfoResult infoResult = (InfoResult) msg.obj;
			switch (msg.what) {
			case R.id.register_send_hr_verify_email:
				if(infoResult.getMessage().getStatus() == 0){
					VToast.show(this, infoResult.getMessage().getMsg());
				}else{
					
					
				}
				
				if(loadingDialog.isShowing()){
					loadingDialog.dismiss();
				}
				
				
				break;
			case R.id.hr_setting_set_recruit_status:
				if(infoResult.getMessage().getStatus() == 0){

				}
				
				break;
			}
		}
	}	

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_retry_send_email:
			if(!loadingDialog.isShowing())
				loadingDialog.show();
			mRegisterEventLogic.cancel(R.id.register_send_hr_verify_email);
			mRegisterEventLogic.sendHrVerifyEmail(corp, email);
			break;
		case R.id.btn_not_received_email:
			if(mDDialog != null && !mDDialog.isShowing()){
				SpannableKeyWordBuilder builder = new SpannableKeyWordBuilder(getString(R.string.not_received_email_tips));
				builder.setKeyColor(getResources().getColor(R.color.general_color_blue));
				builder.setMode(SpannableKeyWordBuilder.MODE_NUMBER);
				
				mDialog.setCancelable(false);
				mDialog.setTitle(getString(R.string.delete_dialog_title));
				mDialog.setMessage(builder.build());
				mDialog.setPositiveButton(getString(R.string.btn_confirm), new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
					}
				});
				
				mDialog.setNegativeButton(getString(R.string.btn_cancel), new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
	
				mDDialog = mDialog.create();
				mDDialog.show();
			}
			
			break;

		default:
			break;
		}
		
	}
	
}
