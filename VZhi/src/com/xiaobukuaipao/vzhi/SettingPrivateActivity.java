package com.xiaobukuaipao.vzhi;

import android.content.Intent;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.xiaobukuaipao.vzhi.event.InfoResult;
import com.xiaobukuaipao.vzhi.util.GlobalConstants;
import com.xiaobukuaipao.vzhi.util.StringUtils;
import com.xiaobukuaipao.vzhi.util.VToast;
import com.xiaobukuaipao.vzhi.widget.FormItemByLineView;
import com.xiaobukuaipao.vzhi.wrap.ProfileWrapActivity;

public class SettingPrivateActivity extends ProfileWrapActivity implements OnClickListener {


	private FormItemByLineView mBaseinfo;
	private FormItemByLineView mContactinfo;
	private View mLayout;

	@Override
	public void initUIAndData() {
        setContentView(R.layout.activity_setting_private);
        setHeaderMenuByCenterTitle(R.string.setting_private_str);
		setHeaderMenuByLeft(this);
        mBaseinfo = (FormItemByLineView) findViewById(R.id.setting_private_baseinfo);
        mContactinfo = (FormItemByLineView) findViewById(R.id.setting_private_contact);
        
        mBaseinfo.setOnClickListener(this);
        mContactinfo.setOnClickListener(this);
        
        mLayout = findViewById(R.id.layout);
        mLayout.setVisibility(View.GONE);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		mProfileEventLogic.getPrivacy();
	}
	
	@Override
	public void onEventMainThread(Message msg) {
		if(msg.obj instanceof InfoResult){
			InfoResult infoResult = (InfoResult) msg.obj;
			switch (msg.what) {
			case R.id.setting_privacy_get:
				JSONObject json = (JSONObject) JSONObject.parse(infoResult.getResult());
				if(json != null){
					JSONObject prifileprivacy = json.getJSONObject(GlobalConstants.JSON_PROFILEPRIVACY);
					if(prifileprivacy != null){
						String  name = prifileprivacy.getString(GlobalConstants.JSON_NAME);
						if(StringUtils.isNotEmpty(name))
							mBaseinfo.setFormContent(name);
					}
					JSONObject contactprivacy = json.getJSONObject(GlobalConstants.JSON_CONTACTPRIVACY);
					if(contactprivacy != null){
						String  name = contactprivacy.getString(GlobalConstants.JSON_NAME);
						if(StringUtils.isNotEmpty(name))
							mContactinfo.setFormContent(name);
					}
					if(mLayout.getVisibility() != View.VISIBLE){
						Animation loadAnimation = AnimationUtils.loadAnimation(this,R.anim.anim_fide_in_for_recruit);
						mLayout.startAnimation(loadAnimation);
						mLayout.setVisibility(View.VISIBLE);
					}

				}
				break;
			default:
				break;
			}
			
		}else if(msg.obj instanceof VolleyError){
			VToast.show(this, getString(R.string.general_tips_network_unknow));
		}
	}

	@Override
	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
		case R.id.setting_private_baseinfo:
			intent = getIntent();
			intent.setClass(this, SettingPrivateListActivity.class);
			intent.putExtra(GlobalConstants.TITLE, mBaseinfo.getFormLabelText());
			intent.putExtra(GlobalConstants.TYPE, 0);
			intent.putExtra(GlobalConstants.NAME, mBaseinfo.getFormContentText());
			startActivity(intent);
			break;
		case R.id.setting_private_contact:
			intent = getIntent();
			intent.setClass(this, SettingPrivateListActivity.class);
			intent.putExtra(GlobalConstants.TITLE, mContactinfo.getFormLabelText());
			intent.putExtra(GlobalConstants.TYPE, 1);
			intent.putExtra(GlobalConstants.NAME, mContactinfo.getFormContentText());
			startActivity(intent);
			break;
		default:
			break;
		}
		
	}
}
