package com.xiaobukuaipao.vzhi;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.xiaobukuaipao.vzhi.adapter.CommonAdapter;
import com.xiaobukuaipao.vzhi.adapter.ViewHolder;
import com.xiaobukuaipao.vzhi.cache.CommonBitmapMemoryCache;
import com.xiaobukuaipao.vzhi.domain.social.GroupInfo;
import com.xiaobukuaipao.vzhi.domain.user.BasicInfo;
import com.xiaobukuaipao.vzhi.event.InfoResult;
import com.xiaobukuaipao.vzhi.im.ImDbManager;
import com.xiaobukuaipao.vzhi.util.GlobalConstants;
import com.xiaobukuaipao.vzhi.util.StringUtils;
import com.xiaobukuaipao.vzhi.util.VToast;
import com.xiaobukuaipao.vzhi.view.CheckBox;
import com.xiaobukuaipao.vzhi.view.CheckBox.OnCheckListener;
import com.xiaobukuaipao.vzhi.view.HorizontalListView;
import com.xiaobukuaipao.vzhi.view.LoadingDialog;
import com.xiaobukuaipao.vzhi.view.RoundedNetworkImageView;
import com.xiaobukuaipao.vzhi.wrap.SocialWrapActivity;

/**
 * 群设置
 * 
 * @since 2015年01月06日10:54:40
 */
public class GroupSettingActivity extends SocialWrapActivity implements OnClickListener, OnCheckListener, OnTouchListener{

	private HorizontalListView mMembersAvatar;
	private GroupMembersAdapter membersAdapter;
	private List<BasicInfo> mDatas;
	private CheckBox mSettingCbox;
	
	private String gid;
	private RoundedNetworkImageView mGroupLogo;
	
	// 请求队列
	private RequestQueue mQueue;
	private ImageLoader mImageLoader;
	
	private TextView mGroupName;
	private TextView mGroupMark;
	private TextView mGroupDesc;
	private TextView mGroupMemnum;
	
	private GroupInfo groupInfo;
	private View mLayout;
	private View mProgress;
	private LoadingDialog mLoadingDialog;
	@Override
	public void initUIAndData() {
		setContentView(R.layout.activity_chat_group_setting);
		setHeaderMenuByCenterTitle(R.string.group_chat_setting);
		setHeaderMenuByLeft(this);
		
		
		mQueue = Volley.newRequestQueue(this);
		mImageLoader = new ImageLoader(mQueue, new CommonBitmapMemoryCache());
		
		mLayout = findViewById(R.id.layout);
		mProgress = findViewById(R.id.progress);
		
		gid = getIntent().getStringExtra(GlobalConstants.GID);
		
		mLoadingDialog = new LoadingDialog(this, R.style.loading_dialog);
		
		
		mGroupLogo = (RoundedNetworkImageView) findViewById(R.id.group_logo);
		mGroupName = (TextView) findViewById(R.id.group_name);
		mGroupMark = (TextView) findViewById(R.id.group_mark);
		mGroupDesc = (TextView) findViewById(R.id.group_desc);
		mGroupMemnum = (TextView) findViewById(R.id.group_members_num);
		
		//成员
		mDatas = new ArrayList<BasicInfo>();
		mMembersAvatar = (HorizontalListView) findViewById(R.id.group_members_avatar);
		
		membersAdapter = new GroupMembersAdapter(this, mDatas, R.layout.item_group_members_avatar);
		mMembersAvatar.setAdapter(membersAdapter);
		
		mSettingCbox = (CheckBox) findViewById(R.id.group_msg_setting_cb);
		mSettingCbox.setOncheckListener(this);
		mSettingCbox.setOutsideChecked(true);
		mSettingCbox.setOnTouchListener(this);
		
		mLayout.setVisibility(View.INVISIBLE);
		mProgress.setVisibility(View.VISIBLE);
		
		mSocialEventLogic.getGroupinfo(gid);
		
		findViewById(R.id.group_quit).setOnClickListener(this);
	}
	
	private void switchBlock() {
		final int state = mSettingCbox.isChecked() ? 0 : 1;
		if(state == 0){
			mSocialEventLogic.cancel(R.id.social_group_unblock);
			mSocialEventLogic.unblockGroup(gid);
		}else{
			mSocialEventLogic.cancel(R.id.social_group_block);
			mSocialEventLogic.blockGroup(gid);
		}
	}
	@Override
	public void onEventMainThread(Message msg) {
		if(msg.obj instanceof InfoResult){
			InfoResult infoResult = (InfoResult) msg.obj;
			switch (msg.what) {
			case R.id.social_group_get_info:
				JSONObject jsonObject = JSONObject.parseObject(infoResult.getResult());
				if(jsonObject != null){
					groupInfo = new GroupInfo(jsonObject);
					if(StringUtils.isNotEmpty(groupInfo.getName())){
						mGroupName.setText(groupInfo.getName());
					}
					
					mDatas.clear();
					if(groupInfo.getMembersList() != null){
						mDatas.addAll(groupInfo.getMembersList());
					}
					membersAdapter.notifyDataSetChanged();
					
					if(groupInfo.getType() == 1001){
						mGroupMark.setText(getString(R.string.group_setting_type_1001));
						mGroupLogo.setDefaultImageResId(R.drawable.group_recruit);
					}else if(groupInfo.getType() == 2001){
						mGroupMark.setText(getString(R.string.group_setting_type_2001));
						mGroupLogo.setDefaultImageResId(R.drawable.group_connect);
					}else{
						mGroupMark.setVisibility(View.GONE);
					}
					
					mGroupMemnum.setText(String.valueOf(groupInfo.getMemnum()));
					mGroupLogo.setImageUrl(groupInfo.getLogo(), mImageLoader);
					
					if(StringUtils.isNotEmpty(groupInfo.getDesc())){
						mGroupDesc.setText(groupInfo.getDesc());
					}
					
					if(groupInfo.getHasblock() != null && groupInfo.getHasblock() == 1){
						mSettingCbox.setChecked(true);
					}
					if(mLayout.getVisibility() != View.VISIBLE){
						Animation loadAnimation = AnimationUtils.loadAnimation(this,R.anim.anim_fide_in_for_recruit);
						mLayout.startAnimation(loadAnimation);
						mLayout.setVisibility(View.VISIBLE);
						mProgress.setVisibility(View.GONE);
					}
				}
				break;
			case R.id.social_group_quit:
				if(infoResult.getMessage().getStatus() == 0){
					setResult(RESULT_OK);
					// 从数据库中删除信息
					ImDbManager.getInstance().deleteGroupMessage(Long.valueOf(gid));
					AppActivityManager.getInstance().finishActivity(GroupSettingActivity.this);
				}
				if(mLoadingDialog.isShowing()){
					mLoadingDialog.dismiss();
				}
				VToast.show(this, infoResult.getMessage().getMsg());
				break;
			case R.id.social_group_block:
				if(infoResult.getMessage().getStatus() == 0){
					mSettingCbox.setChecked(true);
					VToast.show(this, getString(R.string.general_tips_group_msg_setting_open));
				}
				break;
			case R.id.social_group_unblock:
				if(infoResult.getMessage().getStatus() == 0){
					mSettingCbox.setChecked(false);
					VToast.show(this, getString(R.string.general_tips_group_msg_setting_close));
				}
				break;
			default:
				break;
			}
			
		}else if(msg.obj instanceof VolleyError){
			VToast.show(this, getString(R.string.general_tips_network_unknow));
			if(mLoadingDialog.isShowing()){
				mLoadingDialog.dismiss();
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.group_quit:
			mSocialEventLogic.cancel(R.id.social_group_quit);
			mSocialEventLogic.quitGroup(gid);
			mLoadingDialog.show();
			break;
		}
		
	}
	
	
	class GroupMembersAdapter extends CommonAdapter<BasicInfo> {

		public GroupMembersAdapter(Context mContext, List<BasicInfo> mDatas,
				int mItemLayoutId) {
			super(mContext, mDatas, mItemLayoutId);
		}

		@Override
		public void convert(ViewHolder viewHolder, BasicInfo item, int position) {
			RoundedNetworkImageView avatar = viewHolder.getView(R.id.avatar);
			
			if(groupInfo.getType() == 1001){
				avatar.setDefaultImageResId(R.drawable.general_default_ano);
			}else if(groupInfo.getType() == 2001){
				avatar.setDefaultImageResId(R.drawable.general_user_avatar);
			}
			avatar.setImageUrl(item.getGroupAvatar(), mImageLoader);
			avatar.setBorderColor(getResources().getColor(item.getGender() == 1 ? R.color.bg_blue : R.color.bg_pink));
		}
	}

	@Override
	public void onCheck(boolean isChecked) {
		
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_UP){
			if ((event.getX() <= mSettingCbox.getWidth() && event.getX() >= 0) && (event.getY() <= mSettingCbox.getHeight() && event.getY() >= 0)) {
				switchBlock(); 
			}
		}
		return mSettingCbox.onTouchEvent(event);
	}
	
}
