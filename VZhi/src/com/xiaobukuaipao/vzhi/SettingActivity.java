package com.xiaobukuaipao.vzhi;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;

import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.umeng.update.UpdateStatus;
import com.xiaobukuaipao.vzhi.database.MultiUserDatabaseHelper;
import com.xiaobukuaipao.vzhi.event.ApiConstants;
import com.xiaobukuaipao.vzhi.event.InfoResult;
import com.xiaobukuaipao.vzhi.im.ImLoginManager;
import com.xiaobukuaipao.vzhi.manager.GeneralDbManager;
import com.xiaobukuaipao.vzhi.register.LoginActivity;
import com.xiaobukuaipao.vzhi.services.ImService;
import com.xiaobukuaipao.vzhi.util.GlobalConstants;
import com.xiaobukuaipao.vzhi.widget.FormItemByLineView;
import com.xiaobukuaipao.vzhi.wrap.ProfileWrapActivity;

public class SettingActivity extends ProfileWrapActivity implements OnClickListener {
	private FormItemByLineView mSettingUpdateLayout;

	private AlertDialog mAlertDialog;
	@Override
	public void initUIAndData() {
		setContentView(R.layout.activity_setting);
        setHeaderMenuByCenterTitle(R.string.setting_title_str);
        setHeaderMenuByLeft(this);
        
        mSettingUpdateLayout = (FormItemByLineView) findViewById(R.id.setting_update_layout);
        try {
			PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), 0);
			
			mSettingUpdateLayout.setFormContent(getString(R.string.setting_version_str, info.versionName));
			
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
        
        findViewById(R.id.setting_newmsg_layout).setOnClickListener(this);
		findViewById(R.id.setting_account_layout).setOnClickListener(this);
		findViewById(R.id.setting_private_layout).setOnClickListener(this);
		findViewById(R.id.setting_about_layout).setOnClickListener(this);
		findViewById(R.id.setting_update_layout).setOnClickListener(this);
		findViewById(R.id.logout).setOnClickListener(this);
		
		UmengUpdateAgent.setDeltaUpdate(true);
		UmengUpdateAgent.setUpdateAutoPopup(false);
		UmengUpdateAgent.update(this);
		UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {

			@Override
			public void onUpdateReturned(
					int updateStatus,
					UpdateResponse updateInfo) {
				switch (updateStatus) {
				case UpdateStatus.Yes: // has update
					mSettingUpdateLayout.setFormContent("发现更新" + updateInfo.version);
					mSettingUpdateLayout.getFormContent().setTextColor(getResources().getColor(R.color.general_color_red));
					break;
				case UpdateStatus.No: // has no
										// update
//					Toast.makeText(SettingActivity.this,"没有更新",Toast.LENGTH_SHORT).show();
					break;
				case UpdateStatus.NoneWifi: // none
											// wifi
//					Toast.makeText(UpdateExampleConfig.mContext,"没有wifi连接， 只在wifi下更新",Toast.LENGTH_SHORT).show();
					break;
				case UpdateStatus.Timeout: // time
											// out
//					Toast.makeText(UpdateExampleConfig.mContext,"超时",Toast.LENGTH_SHORT).show();
					break;
				}
			}

		});
		
		mAlertDialog = new AlertDialog.Builder(this).setAdapter(ArrayAdapter.createFromResource(this, R.array.logout_array, android.R.layout.simple_list_item_1), new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case 0:
//					Logcat.d("@@@", "logcat: close");
					AppActivityManager.getInstance().finishAllActivity();
					
					break;
				case 1:
//					Logcat.d("@@@", "logcat: logout");
					// 退出登录
					//mSocialEventLogic.logout("android", ImLoginManager.getInstance().getUserId(), ImLoginManager.getInstance().getChannelId());
					//当app更新之后 会把 ImLoginManager 中的变量置空
					mSocialEventLogic.logout("android", GeneralDbManager.getInstance().getUidFromCookie(), ImLoginManager.getInstance().getChannelId());
					
					break;

				default:
					break;
				}
			}
		}).create();
	}

	@Override
	public void onEventMainThread(Message msg) {
		
		if (msg.obj instanceof InfoResult) {
			InfoResult infoResult = (InfoResult) msg.obj;
			switch (msg.what) {
				case R.id.social_logout:
					
					if (infoResult.getMessage().getStatus() == 0) {
						
					} else {
						// VToast.show(this, infoResult.getMessage().getMsg());
					}
					
					// 先清除Cookie
					GeneralDbManager.getInstance().removeCookieInfo();
					
					// 关闭所有的数据库
					MultiUserDatabaseHelper.getInstance().closeDatabase();
					
					SharedPreferences sp = this.getSharedPreferences("tuding_uid", Context.MODE_PRIVATE);  
			        sp.edit().putLong("uid", Long.valueOf(0)).commit();
			        sp.edit().putBoolean(GlobalConstants.INTENTION_COMPLETE, false).commit();
					
					// 此时，将已经登录的标志清空
					ImLoginManager.getInstance().setEverLogined(false);
					
					//  停止ImService服务
					Intent stopImServiceIntent = new Intent(SettingActivity.this, ImService.class);
					stopService(stopImServiceIntent);
					
					AppActivityManager.getInstance().finishAllActivity();
					// AppActivityManager.getInstance().appExit(this);
					Intent intent = getIntent();
					intent.setClass(SettingActivity.this, LoginActivity.class);
					// intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(intent);
					// dismiss progress dialog
					break;
			}
		} else {
			// 先清除Cookie
			GeneralDbManager.getInstance().removeCookieInfo();
			
			// 关闭所有的数据库
			MultiUserDatabaseHelper.getInstance().closeDatabase();
			
			SharedPreferences sp = this.getSharedPreferences("tuding_uid", Context.MODE_PRIVATE);  
	        sp.edit().putLong("uid", Long.valueOf(0)).commit();
	        sp.edit().putBoolean(GlobalConstants.INTENTION_COMPLETE, false).commit();
	        
			// 此时，将已经登录的标志清空
			ImLoginManager.getInstance().setEverLogined(false);
			
			//  停止ImService服务
			Intent stopImServiceIntent = new Intent(SettingActivity.this, ImService.class);
			stopService(stopImServiceIntent);
			
			AppActivityManager.getInstance().finishAllActivity();
			// AppActivityManager.getInstance().appExit(this);
			Intent intent = getIntent();
			intent.setClass(SettingActivity.this, LoginActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
			
			
		}
	}
	
	@Override
	public void onClick(View v) {
		Intent intent = null;
		
		switch (v.getId()) {
		case R.id.setting_account_layout:
			intent = new Intent(this, SettingAccountActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			break;
		case R.id.setting_private_layout:
			intent = new Intent(this, SettingPrivateActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			break;
		case R.id.setting_newmsg_layout:
			intent = new Intent(this, SettingMsgActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			break;
		case R.id.setting_about_layout:
			intent = new Intent();
			intent.putExtra(GlobalConstants.INNER_URL, ApiConstants.ABOUT);
			intent.setClass(this, WebSearchActivity.class);
			startActivity(intent);
			
			break;
		case R.id.setting_update_layout:
			UmengUpdateAgent.setUpdateAutoPopup(true);
			UmengUpdateAgent.setDeltaUpdate(true);
			UmengUpdateAgent.forceUpdate(this);
			break;
		case R.id.logout:
			if(!mAlertDialog.isShowing())
				mAlertDialog.show();
			break;
		default:
			break;
		}
		
	}


}
