package com.xiaobukuaipao.vzhi;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Message;
import android.text.TextPaint;
import android.view.View;
import android.view.View.OnClickListener;

import com.alibaba.fastjson.JSONObject;
import com.xiaobukuaipao.vzhi.domain.HrInfo;
import com.xiaobukuaipao.vzhi.event.ApiConstants;
import com.xiaobukuaipao.vzhi.event.InfoResult;
import com.xiaobukuaipao.vzhi.register.CompanyInfoActivity;
import com.xiaobukuaipao.vzhi.register.HRInfoJobExprActivity;
import com.xiaobukuaipao.vzhi.util.GlobalConstants;
import com.xiaobukuaipao.vzhi.util.SpannableKeyWordBuilder;
import com.xiaobukuaipao.vzhi.util.StringUtils;
import com.xiaobukuaipao.vzhi.util.VToast;
import com.xiaobukuaipao.vzhi.view.LoadingDialog;
import com.xiaobukuaipao.vzhi.widget.FormItemByLineLayout;
import com.xiaobukuaipao.vzhi.widget.FormItemByLineView;
import com.xiaobukuaipao.vzhi.wrap.ProfileWrapActivity;

/**
 * 招聘方信息设置
 * 
 */
public class RecruitServiceSettingActivity extends ProfileWrapActivity implements OnClickListener {
	private FormItemByLineView mCorpEmail;
	private FormItemByLineView mCorpDetail;
	private FormItemByLineLayout mCorpDest;
	private AlertDialog.Builder mDialog;
	private LoadingDialog loadingDialog;
	/**
	 * 邮箱是否被验证
	 */
	private boolean mEmailIsChecked = false;
	/**
	 * 公司信息是否完善
	 */
	private boolean mCoprsDetailIsComplete = false;
	
	private HrInfo hrInfo;
	
	@Override
	public void initUIAndData() {
		setContentView(R.layout.activity_recruit_service_setting);
        setHeaderMenuByCenterTitle(R.string.recruit_service_setting_title);
        setHeaderMenuByRight(R.string.general_tips_reset).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
			
					mDialog.setTitle(R.string.general_tips).setMessage(R.string.recruit_service_setting_corp_detail_tips2);
					mDialog.setPositiveButton(R.string.btn_confirm, new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							Intent intent = new Intent();
							intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							intent.putExtra(GlobalConstants.RECRUIT_INFO_SETTING_RESET, true);
							intent.setClass(RecruitServiceSettingActivity.this, HRInfoJobExprActivity.class);
							startActivity(intent);//必须要公司信息页面知道　是从招聘服务设置过来的
						}
					});
					mDialog.setNegativeButton(R.string.btn_cancel, null);
					mDialog.show();
				
			}
		});
        
        setHeaderMenuByLeft(this);
        findViewById(R.id.layout).setVisibility(View.INVISIBLE);
        
        
        mDialog = new AlertDialog.Builder(this);

		loadingDialog = new LoadingDialog(this, R.style.loading_dialog);
		
        mCorpEmail = (FormItemByLineView)findViewById(R.id.corp_email);
        float measureText = new TextPaint().measureText(mCorpEmail.getFormContentText());
        mCorpEmail.getFormContent().setWidth((int) measureText);
        mCorpDetail = (FormItemByLineView)findViewById(R.id.corp_detail);
        
        mCorpDest = (FormItemByLineLayout)findViewById(R.id.corp_dest);
        mCorpDest.getCheckBox().setChecked(true);
        
        mCorpEmail.setOnClickListener(this);
        mCorpDetail.setOnClickListener(this);
        mCorpDest.setOnClickListener(this);
        
        findViewById(R.id.email_resend).setOnClickListener(this);
        findViewById(R.id.email_not_recieve).setOnClickListener(this);
        
        
	}

	@Override
	protected void onResume() {
		mProfileEventLogic.getHrBasic();
		super.onResume();
	}
	@Override
	public void onEventMainThread(Message msg) {
		if(msg.obj instanceof InfoResult){
			InfoResult infoResult = (InfoResult) msg.obj;
			switch (msg.what) {
			case R.id.register_send_hr_verify_email:
				if(infoResult.getMessage().getStatus() == 0){}
				if(loadingDialog.isShowing()){
					loadingDialog.dismiss();
				}
				VToast.show(this, infoResult.getMessage().getMsg());
				break;
			case R.id.register_get_hr_basicinfo:
				JSONObject hrbasic = JSONObject.parseObject(infoResult.getResult());
				if(hrbasic != null){
					hrInfo = new HrInfo(hrbasic);
					if(StringUtils.isNotEmpty(hrInfo.getEmail())){
						mCorpEmail.setFormLabel(hrInfo.getEmail());
						if(hrInfo.getCertify() == 0){
							   mEmailIsChecked= false;
						       mCorpEmail.getFormContent().setTextColor(getResources().getColor(R.color.general_color_red));
						       mCorpEmail.getFormContent().setText(R.string.general_tips_uncheck);
						       findViewById(R.id.email_check_layout).setVisibility(View.VISIBLE);
						}else{
							   mEmailIsChecked= true;//邮箱通过验证
						       mCorpEmail.getFormContent().setTextColor(getResources().getColor(R.color.base_interface_color));
						       mCorpEmail.getFormContent().setText(R.string.general_tips_alcheck);
						       findViewById(R.id.email_check_layout).setVisibility(View.GONE);
						}
					}
					
					if(StringUtils.isNotEmpty(hrInfo.getCorpLName())){
						mCorpDetail.setFormLabel(hrInfo.getCorpLName());
					}
					
					// 修改 2015年02月06日17:58:43 由招聘的企业邮箱改为招聘身份]
					
					if(StringUtils.isNotEmpty(hrInfo.getPositionName())){
						mCorpEmail.setFormLabel(hrInfo.getPositionName());
					}
					
					
					
					if(hrInfo.getCorpComplete() == 0){
						mCorpDetail.getFormContent().setHint(R.string.general_tips_add);
					}else{
						mCoprsDetailIsComplete = true;
						mCorpDetail.getFormContent().setHint("");
					}
					
				}
				 findViewById(R.id.layout).setVisibility(View.VISIBLE);
				break;
			}
		}
	} 
	
	@Override
	public void onBackPressed() {
		if(!mCoprsDetailIsComplete){

				mDialog.setTitle(R.string.general_tips).setMessage(R.string.recruit_service_setting_corp_detail_tips1);
				mDialog.setPositiveButton(R.string.btn_confirm, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						back();
					}
				});
				mDialog.show();
			
		}else{
			back();
		}
		
	}
	
	private void back(){
		super.onBackPressed();
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.corp_email:
			Intent intent = getIntent();
			intent.setClass(v.getContext(), CompanyInfoActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.putExtra(GlobalConstants.NAME, mCorpDetail.getFormLabelText());
			// 
			intent.putExtra(GlobalConstants.RECRUIT_INFO_SETTING_VRFY, mEmailIsChecked);
			intent.putExtra(GlobalConstants.EMAIL, hrInfo.getEmail());
			intent.putExtra(GlobalConstants.RECRUIT_INFO_SETTING_INFO, true);
			startActivity(intent);//必须要公司信息页面知道　是从招聘服务设置过来的
			break;
		case R.id.corp_detail:
			if(mEmailIsChecked){
				Intent corp = new Intent(RecruitServiceSettingActivity.this, CorpInfoEditActivity.class);
				corp.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				corp.putExtra(GlobalConstants.ID, hrInfo.getCorpId());
				startActivity(corp);
			}else{
				String corpEmail = getString(R.string.recruit_service_setting_corp_email_tips3,mCorpEmail.getFormLabelText());
				SpannableKeyWordBuilder builder = new SpannableKeyWordBuilder(corpEmail);
				builder.setMode(SpannableKeyWordBuilder.MODE_KEYWORD);
				builder.setKeywords(mCorpEmail.getFormLabelText());
				
				mDialog.setTitle(R.string.general_tips).setMessage(builder.build());
				mDialog.setPositiveButton(R.string.btn_confirm, null);
				mDialog.show();
			}
			
			break;
		case R.id.corp_dest:
			
			break;
		case R.id.email_resend:
			if(!loadingDialog.isShowing()){
				loadingDialog.show();
			}
			mRegisterEventLogic.cancel(R.id.register_send_hr_verify_email);
			mRegisterEventLogic.sendHrVerifyEmail(hrInfo.getCorpId(), mCorpEmail.getFormLabelText());
			break;
		case R.id.email_not_recieve:

			intent = new Intent();
			intent.putExtra(GlobalConstants.INNER_URL, ApiConstants.CONTACT);
			intent.setClass(this, WebSearchActivity.class);
			startActivity(intent);
			
//				SpannableKeyWordBuilder builder = new SpannableKeyWordBuilder(getString(R.string.not_received_email_tips));
//				builder.setKeyColor(getResources().getColor(R.color.general_color_blue));
//				builder.setMode(SpannableKeyWordBuilder.MODE_NUMBER);
//				mDialog.setCancelable(false);
//				mDialog.setTitle(getString(R.string.delete_dialog_title));
//				mDialog.setMessage(builder.build());
//				mDialog.setPositiveButton(getString(R.string.btn_confirm), new DialogInterface.OnClickListener() {
//
//					public void onClick(DialogInterface dialog, int which) {
//						
//					}
//				});
//				mDialog.setNegativeButton(getString(R.string.btn_cancel), null);
//				mDialog.show();
			
			break;
		default:
			break;
		}
		
	}

	
}
