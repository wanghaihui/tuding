package com.xiaobukuaipao.vzhi.register;

import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Message;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.umeng.analytics.MobclickAgent;
import com.xiaobukuaipao.vzhi.R;
import com.xiaobukuaipao.vzhi.RecruitServiceActivity;
import com.xiaobukuaipao.vzhi.RecruitServiceSettingActivity;
import com.xiaobukuaipao.vzhi.domain.HrInfo;
import com.xiaobukuaipao.vzhi.domain.user.Experience;
import com.xiaobukuaipao.vzhi.event.InfoResult;
import com.xiaobukuaipao.vzhi.profile.jobexp.JobExperienceSingleActivity;
import com.xiaobukuaipao.vzhi.util.GlobalConstants;
import com.xiaobukuaipao.vzhi.util.SpannableKeyWordBuilder;
import com.xiaobukuaipao.vzhi.util.StringUtils;
import com.xiaobukuaipao.vzhi.util.VToast;
import com.xiaobukuaipao.vzhi.view.LoadingDialog;
import com.xiaobukuaipao.vzhi.widget.DateDialog;
import com.xiaobukuaipao.vzhi.widget.FormItemByLineLayout;
import com.xiaobukuaipao.vzhi.widget.FormItemByLineLayout.OnEditCompleteListener;
import com.xiaobukuaipao.vzhi.widget.FormItemByLineView;
import com.xiaobukuaipao.vzhi.wrap.RegisterWrapActivity;

public class CompanyInfoActivity extends RegisterWrapActivity implements
		OnClickListener {

	private FormItemByLineView mInfoName;
	private FormItemByLineView mInfoTime;
	private FormItemByLineView mInfoPosition;
	private FormItemByLineView mInfoDesc;
	
	private FormItemByLineLayout mInfoEmail;
	private FormItemByLineView mInfoFullName;
	
	private AlertDialog.Builder mDialog;
	
	private boolean isShowing;
	
	private PopupWindow mPopupWindow;
	private LinearLayout mPopupLayout;
	private LinearLayout mRecCorps;
	
	private LoadingDialog loadingDialog;
	
	/**
	 * 是否从招聘服务设置来
	 */
	private boolean isFromRecruitSetting;
	
	/**
	 * 是否从招聘方信息设置来
	 */
	private boolean isFromRecruitInfoSettingInfo;
	
	/**
	 * 招聘方信息重置
	 */
	private boolean isFromRecruitInfoReset;
	
	private String mCorpFullName;
	private String mCorpEmail;
	
	private boolean isFinishing = false;
	
	private Experience mExperience;
	private Bundle mBundle;
	private DateDialog mDateDialog;
	
	
	/**
	 * 是否验证了身份
	 */
	private boolean isVerifiedEmail = false;
	@SuppressLint("NewApi")
	@Override
	public void initUIAndData() {
		//init data
		isFromRecruitSetting = getIntent().getBooleanExtra(GlobalConstants.RECRUIT_SETTING, false);
		isFromRecruitInfoSettingInfo = getIntent().getBooleanExtra(GlobalConstants.RECRUIT_INFO_SETTING_INFO, false);
		isFromRecruitInfoReset = getIntent().getBooleanExtra(GlobalConstants.RECRUIT_INFO_SETTING_RESET, false);
		mCorpFullName = getIntent().getStringExtra(GlobalConstants.NAME);
		mCorpEmail = getIntent().getStringExtra(GlobalConstants.EMAIL);
		mExperience = getIntent().getParcelableExtra(GlobalConstants.JOB_INFO);
		
		isVerifiedEmail = getIntent().getBooleanExtra(GlobalConstants.RECRUIT_INFO_SETTING_VRFY, false);
		
		setContentView(R.layout.activity_company_info);
		setHeaderMenuByLeft(this);
		setHeaderMenuByCenterTitle(R.string.title_company_info);
		
		mDialog = new AlertDialog.Builder(this);
		mDialog.setCancelable(false);

		loadingDialog = new LoadingDialog(this, R.style.loading_dialog);
		mInfoName = (FormItemByLineView) findViewById(R.id.company_info_ltd);
		mInfoTime = (FormItemByLineView) findViewById(R.id.company_info_time);
		mInfoPosition = (FormItemByLineView) findViewById(R.id.company_info_position);
		mInfoDesc = (FormItemByLineView) findViewById(R.id.company_info_desc);
		
		if(mExperience != null){
			fillWorkexpr();
		}else{
			mExperience = new Experience();
		}
		
		
		mInfoFullName = (FormItemByLineView) findViewById(R.id.company_info_name);
		mInfoEmail = (FormItemByLineLayout) findViewById(R.id.company_info_email);
		mInfoEmail.getInfoEdit().setInputType(InputType.TYPE_CLASS_TEXT| InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
		
		mInfoName.setOnClickListener(this);
		mInfoEmail.setOnClickListener(this);
		mInfoTime.setOnClickListener(this);
		mInfoPosition.setOnClickListener(this);
		mInfoDesc.setOnClickListener(this);
		mInfoFullName.setOnClickListener(this);
		
		mPopupLayout = (LinearLayout) View.inflate(this,R.layout.popup_corpname_rec, null);

		mPopupWindow = new PopupWindow();
		mPopupWindow.setBackgroundDrawable(new ColorDrawable(0));
		mPopupWindow.setTouchInterceptor(new OnTouchListener() {

			@SuppressLint("ClickableViewAccessibility")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
					if (mPopupWindow.isShowing())
						mPopupWindow.dismiss();
					return true;
				}
				return false;
			}
		});

		mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {

			@Override
			public void onDismiss() {
				WindowManager.LayoutParams lp = getWindow().getAttributes();
				lp.alpha = 1f;
				getWindow().setAttributes(lp);
			}
		});
		
		mPopupLayout.findViewById(R.id.cancel).setOnClickListener(this);
		mRecCorps = (LinearLayout) mPopupLayout.findViewById(R.id.corpname_rec_list);
		
		
		if(isFromRecruitInfoSettingInfo){//招聘方信息设置
			if(!isVerifiedEmail){//是否验证了邮件
				setHeadMenuRightOut();
			}
			
		}else{
			setHeadMenuRightIn();
		}
		if(isFromRecruitInfoSettingInfo){
			findViewById(R.id.layout).setVisibility(View.INVISIBLE);
			mProfileEventLogic.getHrBasic();
		}
	}

	private void fillWorkexpr() {
		mInfoName.setFormContent(mExperience.getCorp());
		if(mExperience.getEnd() != null){
			mInfoTime.setFormContent(mExperience.getBeginStr() + "~" + mExperience.getEndStr());
		}else{
			mInfoTime.setFormContent(mExperience.getBeginStr() + "~" + getString(R.string.date_dialog_tonow));
		}
		
		mInfoPosition.setFormContent(mExperience.getPosition());
		
		if(StringUtils.isNotEmpty(mExperience.getDesc())){
			mInfoDesc.setFormContent(getString(R.string.general_tips_alfill));
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
	}
	private void setHeadMenuRightOut() {
		setHeaderMenuByRight(R.string.general_tips_finished).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if(isShowing){
						return;
					}else{
						isShowing = true;
						mDialog.setTitle(R.string.general_tips).setMessage(R.string.recruit_service_setting_corp_detail_tips3);
						mDialog.setPositiveButton(R.string.btn_confirm, new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								//完成之后向服务器更新数据
								mRegisterEventLogic.cancel(R.id.register_add_hr_register_workexp);
								Log.i(TAG, "set Head Menu Right out");
								String workexprid = null;
								if(mExperience != null){
									workexprid = mExperience.getId();
								}
								mRegisterEventLogic.setHrRegisterWorkexp(workexprid, 
										mInfoEmail.getFormContentText(),
										mCorpFullName, 
										mInfoName.getFormContentText(),
										mInfoPosition.getFormContentText(), 
										mExperience.getBeginStr(), 
										mExperience.getEndStr(), 
										mExperience.getDesc());
								loadingDialog.show();
							}
						});
						mDialog.setNegativeButton(R.string.btn_cancel, null);
						AlertDialog dialog = mDialog.create();
						dialog.setOnDismissListener(new OnDismissListener() {
							
							@Override
							public void onDismiss(DialogInterface dialog) {
								isShowing = false;
							}
						});
						dialog.show();
					}
				}
			});
		mInfoEmail.setFormContent(mCorpEmail);
		mInfoName.setFormContent(mCorpFullName);
		if(isFinishing){//如果结束不发送请求
			return;
		}
		mRegisterEventLogic.cancel(R.id.register_check_hr_corp_email);
		mRegisterEventLogic.checkHrCorpEmail(mInfoEmail.getFormContentText());
	}
	@Override
	public void finish() {
		isFinishing = true;
		super.finish();
	}
	
	private void setHeadMenuRightIn() {
		setHeaderMenuByRight(R.string.general_tips_finished).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				sendVerifyEmail();
			}
		});
	}

	private void sendVerifyEmail() {
		String reg = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$";
		if(StringUtils.isEmpty(mInfoEmail.getFormContentText())){
			VToast.show(CompanyInfoActivity.this, getString(R.string.general_tips_fill_tips,mInfoEmail.getFormLabel().getText().toString()));
			return;
		}else if(!Pattern.matches(reg, mInfoEmail.getFormContentText())){
			VToast.show(CompanyInfoActivity.this, getString(R.string.post_resume_email_tips));
			return;
		}
		
		if(StringUtils.isEmpty(mInfoName.getFormContentText())){
			VToast.show(CompanyInfoActivity.this, getString(R.string.general_tips_fill_tips,mInfoName.getFormLabel().getText().toString()));
			return;
		}
		
		if(StringUtils.isEmpty(mInfoTime.getFormContentText())){
			VToast.show(CompanyInfoActivity.this, getString(R.string.general_tips_fill_tips,mInfoTime.getFormLabel().getText().toString()));
			return;
		}
		
		if(StringUtils.isEmpty(mInfoPosition.getFormContentText())){
			VToast.show(CompanyInfoActivity.this, getString(R.string.general_tips_fill_tips,mInfoPosition.getFormLabel().getText().toString()));
			return;
		}
		
		if(StringUtils.isEmpty(mInfoFullName.getFormContentText())){
			VToast.show(CompanyInfoActivity.this, getString(R.string.general_tips_fill_tips,mInfoFullName.getFormLabel().getText().toString()));
			return;
		}
		
		
		if(isShowing){
			return;
		}else{
			isShowing = true;
			SpannableKeyWordBuilder builder = new SpannableKeyWordBuilder(getString(R.string.email_tips,mInfoEmail.getFormContentText()));
			builder.setKeyColor(getResources().getColor(R.color.general_color_blue));
			builder.setMode(SpannableKeyWordBuilder.MODE_KEYWORD);
			builder.setKeywords(mInfoEmail.getFormContentText());
			
			mDialog.setCancelable(false);
			mDialog.setTitle(getString(R.string.email_title));
			mDialog.setMessage(builder.build());
			mDialog.setPositiveButton(getString(R.string.btn_confirm), new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog, int which) {
					loadingDialog.show();
					MobclickAgent.onEvent(CompanyInfoActivity.this, "yxyz");
					Log.i(TAG, "sendVerifyEmail");
					
					mRegisterEventLogic.cancel(R.id.register_add_hr_register_workexp);
					
					String workexprid = null;
					if(mExperience != null){
						workexprid = mExperience.getId();
					}
					mRegisterEventLogic.setHrRegisterWorkexp(workexprid, 
							mInfoEmail.getFormContentText(),
							mCorpFullName, 
							mInfoName.getFormContentText(),
							mInfoPosition.getFormContentText(), 
							mExperience.getBeginStr(), 
							mExperience.getEndStr(), 
							mExperience.getDesc());
				}
			});
			mDialog.setNegativeButton(getString(R.string.btn_cancel), new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					if(isFromRecruitInfoReset || isFromRecruitInfoSettingInfo){
						Intent intent = getIntent();
						intent.setClass(CompanyInfoActivity.this, RecruitServiceSettingActivity.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(intent);
					}
				}
			});
			AlertDialog dialog = mDialog.create();
			dialog.setOnDismissListener(new OnDismissListener() {
				
				@Override
				public void onDismiss(DialogInterface dialog) {
					isShowing = false;
				}
			});
			dialog.show();
		}
	}
	
	@Override
	public void onEventMainThread(Message msg) {
		if(msg.obj instanceof InfoResult){
			InfoResult infoResult = (InfoResult) msg.obj;
			switch (msg.what) {
			case R.id.register_check_hr_corp_email:
				if(infoResult.getMessage().getStatus() != 0){
					if(isFinishing){
						return;
					}
					if(isShowing){
						return;
					}else{
						isShowing = true;
						mDialog.setCancelable(false);
						mDialog.setTitle(getString(R.string.general_tips));
						mDialog.setMessage(infoResult.getMessage().getMsg());
						mDialog.setPositiveButton(getString(R.string.btn_confirm), new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog, int which) {
								
							}
						});
						mDialog.setNegativeButton(getString(R.string.btn_cancel), new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog, int which) {
							}
						});
						AlertDialog dialog = mDialog.create();
						dialog.setOnDismissListener(new OnDismissListener() {
							
							@Override
							public void onDismiss(DialogInterface dialog) {
								isShowing = false;
							}
						});
						dialog.show();
					}
					resetEmail();
				}else{
					mInfoName.setEnabled(true);
					mInfoTime.setEnabled(true);
					mInfoPosition.setEnabled(true);
				}
				break;
			case R.id.register_get_email_corpsrecl:
				boolean flag = false;
				JSONObject jsonObject = JSONObject.parseObject(infoResult.getResult());
				if(jsonObject != null){
					if(jsonObject.getInteger(GlobalConstants.JSON_ALLOWADD) != null){
						int allowadd = jsonObject.getInteger(GlobalConstants.JSON_ALLOWADD);
						if(allowadd == 1){//#allowadd为1表示，还可以向此邮箱域名增加公司，为0表示已经不能向此域名增加公司
							JSONArray jsonArray = jsonObject.getJSONArray(GlobalConstants.JSON_CORPS);
							if(jsonArray != null && jsonArray.size() > 0){
								for(int i = 0; i < jsonArray.size() && i < mRecCorps.getChildCount() ; i++){
									TextView view = (TextView) mRecCorps.getChildAt(i);
									view.setVisibility(View.VISIBLE);
									if(jsonArray.getJSONObject(i).getString(GlobalConstants.JSON_LNAME) != null){
										view.setText(jsonArray.getJSONObject(i).getString(GlobalConstants.JSON_LNAME));
										view.setOnClickListener(new OnClickRecCorps(jsonArray.getJSONObject(i).getString(GlobalConstants.JSON_LNAME)));
									}
								}
							}else{
								flag = true;
							}
						}else{
							flag = true;
						}
					}else{
						flag = true;
					}
				}else{
					flag = true;
				}
				if(flag){//没有推荐公司
					Intent intent = getIntent();
					intent.setClass(CompanyInfoActivity.this,HrSingleActivity.class);
					intent.putExtra(GlobalConstants.SEQ_STRING, 1);
					startActivityForResult(intent, 1);
					
				}else{
					mPopupWindow.setAnimationStyle(android.R.style.Animation_InputMethod);
					mPopupWindow.setContentView(mPopupLayout);
					mPopupWindow.setHeight(LayoutParams.WRAP_CONTENT);
					mPopupWindow.setWidth(LayoutParams.MATCH_PARENT);
					mPopupWindow.setOutsideTouchable(true);
					mPopupWindow.setFocusable(true);
					WindowManager.LayoutParams lp = getWindow().getAttributes();
					lp.alpha = 0.5f;
					getWindow().setAttributes(lp);
					mPopupWindow.showAtLocation(getWindow().getDecorView().getRootView(), Gravity.BOTTOM, 0, 0);
				}

				break;
			case R.id.register_send_hr_verify_email:
				break;
			case R.id.register_set_hr_register_workexp:
				if(loadingDialog.isShowing())
					loadingDialog.dismiss();
				if(infoResult.getMessage().getStatus() == 0){
					if(isFromRecruitSetting){//跳回招聘服务页面
						Intent intent = getIntent();
						intent.setClass(this, RecruitServiceActivity.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(intent);
						return;
					}
					if(isFromRecruitInfoReset || isFromRecruitInfoSettingInfo){//跳回招聘方信息设置页面
						Intent intent = getIntent();
						intent.setClass(CompanyInfoActivity.this, RecruitServiceSettingActivity.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(intent);
						return;
					}
					//直接跳到验证邮件页面
					Intent intent = getIntent();
					intent.setClass(CompanyInfoActivity.this,EmailVerifyActivity.class);
					intent.putExtra(GlobalConstants.NAME, mInfoEmail.getFormContentText());
					intent.putExtra(GlobalConstants.CORP_ID, mCorpFullName);
					startActivity(intent);
				}
				VToast.show(this, infoResult.getMessage().getMsg());
				break;
			case R.id.hr_setting_reset_recruit_serivce_setting:
				boolean resetSuccess  = false;
				if(infoResult.getMessage().getStatus() == 0){
					resetSuccess = true;
				}else{
					resetSuccess = false;
				}
		
				if(resetSuccess){
					sendVerifyEmail();
				}
				break;
			case R.id.register_get_hr_basicinfo:
				JSONObject hrbasic = JSONObject.parseObject(infoResult.getResult());
				if(hrbasic != null){
					HrInfo hrInfo = new HrInfo(hrbasic);
					if(hrInfo.getCertify() != 0){
						mInfoEmail.setEnabled(false);
						mInfoFullName.setEnabled(false);
						mInfoName.setEnabled(false);
						mInfoTime.setEnabled(false);
						mInfoPosition.setEnabled(false);
						mInfoDesc.setEnabled(false);
					}
					
					mExperience.setCorp(hrInfo.getCorpName());
					mExperience.setBegin(hrInfo.getBegin());
					mExperience.setEnd(hrInfo.getEnd());
					mExperience.setDesc(hrInfo.getWorkdesc());
					mExperience.setPosition(hrInfo.getPositionName());
					mExperience.setId(String.valueOf(hrInfo.getWorkexprid()));
					fillWorkexpr();
					
					if(StringUtils.isNotEmpty(hrInfo.getEmail())){
						mInfoEmail.setFormContent(hrInfo.getEmail());
					}
					if(StringUtils.isNotEmpty(hrInfo.getCorpLName())){
						mInfoFullName.setFormContent(hrInfo.getCorpLName());
					}
					
				}
				findViewById(R.id.layout).setVisibility(View.VISIBLE);
				break;
			default:
				break;
			}
		}
	}

	//推荐公司的点击事件
	class OnClickRecCorps implements OnClickListener {
		String name;
		public OnClickRecCorps(String name){
			this.name = name;
		}
		
		@Override
		public void onClick(View v) {
			mInfoFullName.setFormContent(name);
			mCorpFullName = name;
			mPopupWindow.dismiss();
		}
	}
	
	private void resetEmail() {
		mInfoName.setEnabled(false);
		mInfoTime.setEnabled(false);
		mInfoPosition.setEnabled(false);
		mInfoEmail.setEdit(true);
		mInfoEmail.getInfoEdit().setText(mInfoEmail.getFormContentText());
		mInfoEmail.getInfoEdit().setSelection(mInfoEmail.getFormContentText().length());
	}

	@Override
	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
		case R.id.company_info_name:
			String email = mInfoEmail.getFormContentText();
			String domain = email.substring(email.indexOf("@") + 1);
			mRegisterEventLogic.cancel(R.id.register_get_email_corpsrecl);
			mRegisterEventLogic.getHrCorpEmail(domain);
			break;
		case R.id.company_info_ltd:
			intent = new Intent(this, JobExperienceSingleActivity.class);
			mBundle = new Bundle();
			mBundle.putParcelable(GlobalConstants.GUIDE_JOB_EXPERIENCE_OBJECT,mExperience);
			intent.putExtras(mBundle);
			intent.putExtra(GlobalConstants.SEQ_STRING, R.id.register_jobexp_company);//複用佈局
			startActivityForResult(intent, 3);
			break;
		case R.id.company_info_time:
			if(isShowing){
				return;
			}else{
				isShowing = true;
				mDateDialog = new DateDialog(this);
				if(mExperience.getBegin() != null){
					mDateDialog.setFrontCurYearNum(mExperience.getBeginYear());
					mDateDialog.setFrontCurMonthNum(mExperience.getBeginMonth());
				}
				if(mExperience.getEnd() == null){
					mDateDialog.setBackCurYearNum(mDateDialog.getBackYearToNow());
					mDateDialog.setBackCurMonthNum(mDateDialog.getBackMonthToNow());
				}else{
					mDateDialog.setBackCurYearNum(mExperience.getEndYear());
					mDateDialog.setBackCurMonthNum(mExperience.getEndMonth());
				}

				mDateDialog.setDateSetListener(new DateDialog.OnDateSetListener() {
					
					@Override
					public void onDateSet(int frontYear, int frontMonth, int backYear,
							int backMonth, boolean backToNow) {
						
						JSONObject begin = new JSONObject();
						begin.put(GlobalConstants.JSON_YEAR, frontYear);
						begin.put(GlobalConstants.JSON_MONTH, frontMonth);
						mExperience.setBegin(begin);
						
						StringBuilder time = new StringBuilder();
						time.append(mExperience.getBeginStr());
						time.append("~");
						if(backToNow){
							time.append(getString(R.string.date_dialog_tonow));
							mExperience.setEnd(null);//传空表示至今
						}else{
							JSONObject end = new JSONObject();
							end.put(GlobalConstants.JSON_YEAR, backYear);
							end.put(GlobalConstants.JSON_MONTH, backMonth);
							mExperience.setEnd(end);
							time.append(mExperience.getEndStr());
						}
						mInfoTime.setFormContent(time.toString());
					}
				});
				mDateDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
					
					@Override
					public void onDismiss(DialogInterface dialog) {
						isShowing = false;
					}
				});
				mDateDialog.show();
			}

			break;
		case R.id.company_info_position:
			intent = new Intent(this, JobExperienceSingleActivity.class);
			mBundle = new Bundle();
			mBundle.putParcelable(GlobalConstants.GUIDE_JOB_EXPERIENCE_OBJECT,mExperience);
			intent.putExtras(mBundle);
			intent.putExtra(GlobalConstants.SEQ_STRING, R.id.register_jobexp_name);//複用佈局
			startActivityForResult(intent, 3);
			break;
		case R.id.company_info_desc:
			intent = new Intent(this, JobExperienceSingleActivity.class);
			mBundle = new Bundle();
			mBundle.putParcelable(GlobalConstants.GUIDE_JOB_EXPERIENCE_OBJECT,mExperience);
			intent.putExtras(mBundle);
			intent.putExtra(GlobalConstants.SEQ_STRING, R.id.register_jobexp_desc);//複用佈局
			startActivityForResult(intent, 3);
			
			break;
		case R.id.company_info_email:
			resetEmail();
			mInfoEmail.setCompleteListener(new OnEditCompleteListener() {
				
				@Override
				public void onEditComplete() {
					String reg = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$";
					if(StringUtils.isEmpty(mInfoEmail.getFormContentText())){
						VToast.show(CompanyInfoActivity.this, getString(R.string.general_tips_fill_tips,mInfoEmail.getFormLabel().getText().toString()));
						return;
					}else if(!Pattern.matches(reg, mInfoEmail.getFormContentText())){
						VToast.show(CompanyInfoActivity.this, getString(R.string.post_resume_email_tips));
						return;
					}
					if(isFinishing){//如果结束不发送请求
						return;
					}
					
					mRegisterEventLogic.cancel(R.id.register_check_hr_corp_email);
					mRegisterEventLogic.checkHrCorpEmail(mInfoEmail.getFormContentText());
				}
			});
			break;
		case R.id.cancel:
			intent = getIntent();
			intent.setClass(CompanyInfoActivity.this,HrSingleActivity.class);
			intent.putExtra(GlobalConstants.SEQ_STRING, 1);
			startActivityForResult(intent, 1);
			break;
		default:
			break;
		}
	}
	
	@Override
	protected void onPause() {
		if (mPopupWindow.isShowing())
			mPopupWindow.dismiss();
		super.onPause();
	}
	// 有数据返回
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode== RESULT_OK){
			switch (requestCode) {
			case 1:
				mCorpFullName = data.getStringExtra(GlobalConstants.NAME);
				if(StringUtils.isNotEmpty(mCorpFullName)){
					mInfoFullName.setFormContent(mCorpFullName);
				}
				break;
			case 2:
				String positionName = data.getStringExtra(GlobalConstants.NAME);
				if(StringUtils.isNotEmpty(positionName)){
					mInfoPosition.setFormContent(positionName);
				}
				
				break;
			case 3:
				mBundle = data.getExtras();
				mExperience = (Experience) mBundle.getParcelable(GlobalConstants.GUIDE_JOB_EXPERIENCE_OBJECT);
				if (!StringUtils.isEmpty(mExperience.getCorp())) {
					mInfoName.setFormContent(mExperience.getCorp());
				} else {
					mInfoName.reset();
				}
				if (!StringUtils.isEmpty(mExperience.getPosition())) {
					mInfoPosition.getFormContent().setText(mExperience.getPosition());
				} else {
					mInfoPosition.reset();
				}
				mInfoDesc.getFormLabel().setTextColor(getResources().getColor(R.color.general_color_1A2138));
				if (!StringUtils.isEmpty(mExperience.getDesc())) {
					mInfoDesc.getFormContent().setText(getString(R.string.general_tips_alfill));
				}else{
					mInfoDesc.reset();
				}
				break;
			default:
				break;
			}
			
		}
		
		
	}
}
