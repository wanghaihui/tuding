package com.xiaobukuaipao.vzhi;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.alibaba.fastjson.JSONObject;
import com.umeng.analytics.MobclickAgent;
import com.xiaobukuaipao.vzhi.event.InfoResult;
import com.xiaobukuaipao.vzhi.register.HRInfoActivity;
import com.xiaobukuaipao.vzhi.util.GlobalConstants;
import com.xiaobukuaipao.vzhi.util.VToast;
import com.xiaobukuaipao.vzhi.view.CheckBox;
import com.xiaobukuaipao.vzhi.view.CheckBox.OnCheckListener;
import com.xiaobukuaipao.vzhi.wrap.RecruitWrapActivity;

/**
 * 招聘服务
 * 
 * @author xiaobu
 *
 */
public class RecruitServiceActivity extends RecruitWrapActivity implements OnClickListener, OnCheckListener{

	private CheckBox mCbox;
	private Complete status;
	private RelativeLayout mSettingLayout;
	private RelativeLayout mPublishLayout;
	private RelativeLayout mPublishedLayout;
	private LinearLayout mLayout;
	private boolean isLoaded = false;
	@SuppressLint("ClickableViewAccessibility")
	@Override
	public void initUIAndData() {
		setContentView(R.layout.activity_recruit_service);
		setHeaderMenuByLeft(this);
		setHeaderMenuByCenterTitle(R.string.recruit_service);
		mSettingLayout = (RelativeLayout) findViewById(R.id.recruit_service_setting_layout);
		mSettingLayout.setOnClickListener(this);
		
		mPublishLayout = (RelativeLayout) findViewById(R.id.recruit_service_publish_layout);
		mPublishLayout.setOnClickListener(this);
		
		mPublishedLayout = (RelativeLayout) findViewById(R.id.recruit_service_my_published_layout);
		mPublishedLayout.setOnClickListener(this);
		
		mLayout = (LinearLayout) findViewById(R.id.layout);
		mLayout.setVisibility(View.GONE);
		
		
		mCbox = (CheckBox) findViewById(R.id.recruit_service_cbox);
		mCbox.setOncheckListener(this);
		mCbox.setOutsideChecked(true);
		mCbox.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP){
					if ((event.getX() <= mCbox.getWidth() && event.getX() >= 0)
							&& (event.getY() <= mCbox.getHeight() && event.getY() >= 0)) {
						switchRecruitService(); 
					}
				}
				return mCbox.onTouchEvent(event);
			}
		});
		
		
		
	}

	@Override
	public void onEventMainThread(Message msg) {
		if(msg.obj instanceof InfoResult){
			InfoResult infoResult = (InfoResult) msg.obj;
			switch (msg.what) {
			case R.id.hr_setting_get_recruit_status:
				JSONObject jsonObj = (JSONObject) JSONObject.parse(infoResult.getResult());
				if(jsonObj != null){
					JSONObject jsonObject  = jsonObj.getJSONObject(GlobalConstants.JSON_COMPLETE);
					this.status = new Complete(jsonObject);
					if(status != null){
						
						if(status.basic == 0 || status.hr == 0){
							mCbox.setChecked(false);
							mSettingLayout.setEnabled(false);
							mPublishLayout.setEnabled(false);
							mPublishedLayout.setEnabled(false);
							findViewById(R.id.recruit_service_setting_tv).setVisibility(View.VISIBLE);
							findViewById(R.id.recruit_service_publish_tv).setVisibility(View.VISIBLE);
							findViewById(R.id.recruit_service_my_published_tv).setVisibility(View.VISIBLE);
							findViewById(R.id.recruit_service_open_icon).setEnabled(false);
							findViewById(R.id.recruit_service_setting_icon).setEnabled(false);
							findViewById(R.id.recruit_service_publish_icon).setEnabled(false);
							findViewById(R.id.recruit_service_my_published_icon).setEnabled(false);
							findViewById(R.id.recruit_service_open).setEnabled(false);
							findViewById(R.id.recruit_service_setting).setEnabled(false);
							findViewById(R.id.recruit_service_publish).setEnabled(false);
							findViewById(R.id.recruit_service_my_published).setEnabled(false);
						}else{
							mCbox.setChecked(true);
							mSettingLayout.setEnabled(true);
							mPublishLayout.setEnabled(true);
							mPublishedLayout.setEnabled(true);
							if(status.certify == 0 || status.corp == 0){
								findViewById(R.id.recruit_service_setting_tv).setVisibility(View.VISIBLE);
								findViewById(R.id.recruit_service_publish_tv).setVisibility(View.VISIBLE);
								findViewById(R.id.recruit_service_my_published_tv).setVisibility(View.VISIBLE);
								findViewById(R.id.recruit_service_publish).setEnabled(false);
								findViewById(R.id.recruit_service_my_published).setEnabled(false);
								findViewById(R.id.recruit_service_publish_icon).setEnabled(false);
								findViewById(R.id.recruit_service_my_published_icon).setEnabled(false);
								mPublishLayout.setEnabled(false);
								mPublishedLayout.setEnabled(false);
							}else{
								findViewById(R.id.recruit_service_setting_tv).setVisibility(View.INVISIBLE);
								findViewById(R.id.recruit_service_publish_tv).setVisibility(View.INVISIBLE);
								findViewById(R.id.recruit_service_my_published_tv).setVisibility(View.INVISIBLE);
								findViewById(R.id.recruit_service_publish).setEnabled(true);
								findViewById(R.id.recruit_service_my_published).setEnabled(true);
								findViewById(R.id.recruit_service_publish_icon).setEnabled(true);
								findViewById(R.id.recruit_service_my_published_icon).setEnabled(true);
								mPublishLayout.setEnabled(true);
								mPublishedLayout.setEnabled(true);
							}
							findViewById(R.id.recruit_service_open_icon).setEnabled(true);
							findViewById(R.id.recruit_service_setting_icon).setEnabled(true);
							findViewById(R.id.recruit_service_open).setEnabled(true);
							findViewById(R.id.recruit_service_setting).setEnabled(true);
						}
						if(mLayout.getVisibility() != View.VISIBLE){
							Animation loadAnimation = AnimationUtils.loadAnimation(this,R.anim.anim_fide_in_for_recruit);
							mLayout.startAnimation(loadAnimation);
							mLayout.setVisibility(View.VISIBLE);
							isLoaded = true;
						}
					}
				}
				
				break;
			case R.id.hr_setting_set_recruit_status:
				if(infoResult.getMessage().getStatus() == 0){
					mProfileEventLogic.getRecruitServiceStatus();
				}
				
				VToast.show(this, infoResult.getMessage().getMsg());
				break;
			default:
				break;
			}
			
		}
	}

	@Override
	protected void onResume() {
		mProfileEventLogic.getRecruitServiceStatus();
		super.onResume();
	}
	@Override
	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
		case R.id.recruit_service_setting_layout:
			intent = new Intent(this,RecruitServiceSettingActivity.class);
			break;
		case R.id.recruit_service_publish_layout:
			intent = new Intent(this, PublishPositionCheckActivity.class);
			break;
		case R.id.recruit_service_my_published_layout:
			intent = new Intent(this, PublishedPositionsActivity.class);
			break;
		default:
			break;
		}
		if(intent != null){
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
		}
	}

	private void switchRecruitService() {
		MobclickAgent.onEvent(RecruitServiceActivity.this,"ktzpfw");
		final int state = mCbox.isChecked() ? 0 : 1;
		if(state == 0){
			AlertDialog alertDialog = new AlertDialog.Builder(this).setTitle(R.string.general_tips).setMessage(R.string.recruit_service_tips4).setPositiveButton(R.string.btn_confirm, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					mProfileEventLogic.cancel(R.id.hr_setting_set_recruit_status);
					mProfileEventLogic.setRecruitServiceStatus(state);
					
				}
			}).setNegativeButton(R.string.btn_cancel, null).create();
			alertDialog.show();
			
		}else{
			AlertDialog alertDialog = new AlertDialog.Builder(this).setTitle(R.string.general_tips).setMessage(R.string.recruit_service_tips3).setPositiveButton(R.string.recruit_service_tips2, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Intent intent = new Intent();
					intent.setClass(RecruitServiceActivity.this, HRInfoActivity.class);
					intent.putExtra(GlobalConstants.RECRUIT_SETTING, true);
					startActivity(intent);
				}
			}).setNegativeButton(R.string.btn_cancel, null).create();
			alertDialog.show();
		
		}
	}

	@Override
	public void onCheck(boolean isChecked) {
		if(isLoaded){
			
		}
	}
	
	class  Complete{
		
		public Complete(JSONObject jsonObject){
			if(jsonObject != null){
				if(jsonObject.getInteger(GlobalConstants.JSON_BASIC) != null){
					basic = jsonObject.getInteger(GlobalConstants.JSON_BASIC);
				}
				if(jsonObject.getInteger(GlobalConstants.JSON_HR) != null){
					hr = jsonObject.getInteger(GlobalConstants.JSON_HR);
				}
				if(jsonObject.getInteger(GlobalConstants.JSON_CERTIFY) != null){
					certify = jsonObject.getInteger(GlobalConstants.JSON_CERTIFY);
				}
				if(jsonObject.getInteger(GlobalConstants.JSON_CORP) != null){
					corp = jsonObject.getInteger(GlobalConstants.JSON_CORP);
				}
				
			}
		}
		int basic;
		int hr;
		int certify;
		int corp;
		
	}

}
