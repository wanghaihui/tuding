package com.xiaobukuaipao.vzhi;

import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;

import com.xiaobukuaipao.vzhi.manager.GeneralDbManager;
import com.xiaobukuaipao.vzhi.util.GlobalConstants;
import com.xiaobukuaipao.vzhi.util.SharedPreferencesUtil;
import com.xiaobukuaipao.vzhi.view.CheckBox;
import com.xiaobukuaipao.vzhi.view.CheckBox.OnCheckListener;
import com.xiaobukuaipao.vzhi.wrap.ProfileWrapActivity;

public class SettingMsgActivity extends ProfileWrapActivity implements OnClickListener {
	private CheckBox mSoundBox;
	private CheckBox mVibrationBox;
	
	private String uid;
	
	@Override
	public void initUIAndData() {
		setContentView(R.layout.activity_setting_msg);
        setHeaderMenuByCenterTitle(R.string.setting_newmsg_str);
        setHeaderMenuByLeft(this);
        
        uid = GeneralDbManager.getInstance().getUidFromCookie();
        
        //TODO 消息提醒的状态应该　上传到接口
        mSoundBox = (CheckBox) findViewById(R.id.msg_setting_sound_cbox);
        mVibrationBox = (CheckBox) findViewById(R.id.msg_setting_vibration_cbox);
        //新消息提醒的声音设置
        mSoundBox.setChecked(SharedPreferencesUtil.getInstance().getSettingMessage(GlobalConstants.PREFERENCE_MSG_SOUND + "_" + uid));
        //新消息提醒的震动设置
        mVibrationBox.setChecked(SharedPreferencesUtil.getInstance().getSettingMessage(GlobalConstants.PREFERENCE_MSG_VIBRATION + "_" + uid));
        
        mSoundBox.setOnClickListener(this);
        mVibrationBox.setOnClickListener(this);
        mSoundBox.setOncheckListener(new OnCheckListener() {
			
			@Override
			public void onCheck(boolean isChecked) {
				SharedPreferencesUtil.getInstance().putSettingMessage(isChecked, GlobalConstants.PREFERENCE_MSG_SOUND + "_" + uid);
			}
		});
        mVibrationBox.setOncheckListener(new OnCheckListener() {
			
			@Override
			public void onCheck(boolean isChecked) {
				SharedPreferencesUtil.getInstance().putSettingMessage(isChecked, GlobalConstants.PREFERENCE_MSG_VIBRATION + "_" + uid);
			}
		});
	}

	@Override
	public void onEventMainThread(Message msg) {
		
	}

	@Override
	public void onClick(View v) {
		
		switch (v.getId()) {
		case R.id.msg_setting_sound_cbox:
			
			break;
		case R.id.msg_setting_vibration_cbox:
			
			break;
		default:
			break;
		}
		
	} 
}
