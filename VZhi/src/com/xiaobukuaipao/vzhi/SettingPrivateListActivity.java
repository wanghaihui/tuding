package com.xiaobukuaipao.vzhi;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xiaobukuaipao.vzhi.adapter.CommonAdapter;
import com.xiaobukuaipao.vzhi.adapter.ViewHolder;
import com.xiaobukuaipao.vzhi.event.InfoResult;
import com.xiaobukuaipao.vzhi.util.GlobalConstants;
import com.xiaobukuaipao.vzhi.util.VToast;
import com.xiaobukuaipao.vzhi.widget.FormItemByLineLayout;
import com.xiaobukuaipao.vzhi.wrap.ProfileWrapActivity;

public class SettingPrivateListActivity extends ProfileWrapActivity implements OnClickListener, OnItemClickListener {

	private String title;
	private ListView mPrivacyList;
	private PrivacyAdapter mAdapter;
	private ArrayList<String> mDatas;
	/**
	 * 0 代表资料 1 代表联系
	 */
	private int type = -1;
	
	private String tmpId = "";
	private boolean[]  mChecks;
	private JSONArray mTmpArray;
	@Override
	public void initUIAndData() {
		title = getIntent().getStringExtra(GlobalConstants.TITLE);
		type = getIntent().getIntExtra(GlobalConstants.TYPE, type);
		
        setContentView(R.layout.activity_setting_private_list);
        setHeaderMenuByCenterTitle(title);
        setHeaderMenuByLeft(this);
        
        mPrivacyList = (ListView) findViewById(R.id.setting_privacy_list);
        mPrivacyList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        mPrivacyList.setOnItemClickListener(this);
        mDatas = new ArrayList<String>();
        mAdapter = new PrivacyAdapter(this,mDatas,R.layout.item_privacy);
        mPrivacyList.setAdapter(mAdapter);
        if(type == 0){
        	mProfileEventLogic.getPrivacyProfileList();
        }else if(type == 1){
        	mProfileEventLogic.getPrivacyContactList();
        }else{
        	AppActivityManager.getInstance().finishActivity(SettingPrivateListActivity.this);
        }
        
	}

	@Override
	public void onEventMainThread(Message msg) {
		if(msg.obj instanceof InfoResult){
			InfoResult infoResult = (InfoResult) msg.obj;
			
			switch (msg.what) {
			case R.id.setting_privacy_profile_list_get:
				JSONObject profileLev = JSONObject.parseObject(infoResult.getResult());
				if(profileLev != null){
					mDatas.clear();
					mTmpArray = profileLev.getJSONArray(GlobalConstants.JSON_PROFILEPRIVACY);
					if(mTmpArray != null){
						for(int i = 0 ;  i < mTmpArray.size() ; i ++ ){
							mDatas.add(mTmpArray.getJSONObject(i).getString(GlobalConstants.JSON_NAME));
						}
						mChecks = new boolean[mDatas.size()];
						for(int i = 0 ; i <mChecks.length ; i ++ ){
							if(mDatas.get(i).equals(getIntent().getStringExtra(GlobalConstants.NAME))){
								mChecks[i] = true;
								tmpId = mTmpArray.getJSONObject(i).getString(GlobalConstants.JSON_ID);
							}
						}
						
						mAdapter.notifyDataSetChanged();
					}
				}
				Animation animation1 = AnimationUtils.loadAnimation(this, R.anim.anim_fide_in_for_recruit);
				mPrivacyList.startAnimation(animation1);
				break;
			case R.id.setting_privacy_contact_list_get:
				JSONObject contactLev = JSONObject.parseObject(infoResult.getResult());
				if(contactLev != null){
					mDatas.clear();
					mTmpArray = contactLev.getJSONArray(GlobalConstants.JSON_CONTACTPRIVACY);
					if(mTmpArray != null){
						for(int i = 0 ;  i < mTmpArray.size() ; i ++ ){
							mDatas.add(mTmpArray.getJSONObject(i).getString(GlobalConstants.JSON_NAME));
						}
						mChecks = new boolean[mDatas.size()];
						for(int i = 0 ; i <mChecks.length ; i ++ ){
							if(mDatas.get(i).equals(getIntent().getStringExtra(GlobalConstants.NAME))){
								mChecks[i] = true;
								tmpId = mTmpArray.getJSONObject(i).getString(GlobalConstants.JSON_ID);
							}
						}
						mAdapter.notifyDataSetChanged();
					}
				}
				Animation animation2 = AnimationUtils.loadAnimation(this, R.anim.anim_fide_in_for_recruit);
				mPrivacyList.startAnimation(animation2);
				break;
			case R.id.setting_privacy_contact_set:
				if(infoResult.getMessage().getStatus() == 0){
					AppActivityManager.getInstance().finishActivity(SettingPrivateListActivity.this);
				}
				VToast.show(this, infoResult.getMessage().getMsg());
				
				break;
			case R.id.setting_privacy_profile_set:
				if(infoResult.getMessage().getStatus() == 0){
					AppActivityManager.getInstance().finishActivity(SettingPrivateListActivity.this);
				}
				VToast.show(this, infoResult.getMessage().getMsg());
				break;
				
			default:
				break;
			}
		}
	}

	@Override
	public void onClick(View v) {
		
	}

	class PrivacyAdapter extends CommonAdapter<String>{

		public PrivacyAdapter(Context mContext, List<String> mDatas,
				int mItemLayoutId) {
			super(mContext, mDatas, mItemLayoutId);
		}

		@Override
		public void convert(ViewHolder viewHolder, String item, int position) {
			FormItemByLineLayout layout = viewHolder.getView(R.id.privacy_section_id);
			layout.setFormLabel(item);
			layout.getCheckBox().setChecked(mChecks[position]);
		}
		
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		for(int i = 0; i < mChecks.length ; i ++){
			mChecks[i] = false;
		}
		mChecks[position] = true;
		mAdapter.notifyDataSetChanged();
		tmpId = mTmpArray.getJSONObject(position).getString(GlobalConstants.JSON_ID);
		if(type == 0){
	        mProfileEventLogic.setPrivacyProfile(tmpId);
	    }else if(type == 1){
	    	mProfileEventLogic.setPrivacyContact(tmpId);
	    }else{
	    	AppActivityManager.getInstance().finishActivity(SettingPrivateListActivity.this);
	    }
	}
}
