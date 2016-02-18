package com.xiaobukuaipao.vzhi;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ScrollView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.xiaobukuaipao.vzhi.cache.CommonBitmapMemoryCache;
import com.xiaobukuaipao.vzhi.domain.user.PublicUserProfile;
import com.xiaobukuaipao.vzhi.domain.user.UserBasic;
import com.xiaobukuaipao.vzhi.event.InfoResult;
import com.xiaobukuaipao.vzhi.im.ImDbManager;
import com.xiaobukuaipao.vzhi.util.GlobalConstants;
import com.xiaobukuaipao.vzhi.util.VToast;
import com.xiaobukuaipao.vzhi.view.CheckBox;
import com.xiaobukuaipao.vzhi.view.RoundedNetworkImageView;
import com.xiaobukuaipao.vzhi.wrap.SocialWrapActivity;

public class PersonRealInfoActivity extends SocialWrapActivity implements OnClickListener {

	public static final String TAG = GroupRealInfoActivity.class.getSimpleName();
	private ScrollView mRealCardLayout;

	private RoundedNetworkImageView mAvatar;
	private TextView mName;
	private TextView mAge;
	private TextView mCity;
	private TextView mExpr;
	private TextView mPosition;
	private TextView mCorpAndInds;
	private CheckBox mSettingCbox;

	// 请求队列
	private RequestQueue mQueue;
	private ImageLoader mImageLoader;
	
	private long uid = -1;
	
	private long did = -1;
	
	/**
	 * 初始化UI数据
	 */
	@SuppressLint("ClickableViewAccessibility")
	public void initUIAndData() {
		//处理一些状态值
		uid = getIntent().getLongExtra(GlobalConstants.UID, uid);
		did = getIntent().getLongExtra(GlobalConstants.DID, did);
		
		setContentView(R.layout.activity_person_real_card);
		setHeaderMenuByCenterTitle(R.string.person_chat_member_card);
		setHeaderMenuByLeft(this);
		mRealCardLayout = (ScrollView) findViewById(R.id.real_card_layout);
		mRealCardLayout.setVisibility(View.INVISIBLE);

		mAvatar = (RoundedNetworkImageView) findViewById(R.id.real_card_avatar);
		mName = (TextView) findViewById(R.id.real_card_name);
		mAge = (TextView) findViewById(R.id.real_card_basicinfo_age);
		mCity = (TextView) findViewById(R.id.real_card_basicinfo_city);
		mExpr = (TextView) findViewById(R.id.real_card_basicinfo_expr);
		
		mPosition = (TextView) findViewById(R.id.real_card_position);
		mCorpAndInds = (TextView) findViewById(R.id.real_card_company);

		mQueue = Volley.newRequestQueue(this);
		mImageLoader = new ImageLoader(mQueue, new CommonBitmapMemoryCache());
		mSettingCbox = (CheckBox) findViewById(R.id.group_msg_setting_cb);
		
		findViewById(R.id.person_real_baseinfo).setOnClickListener(this);
		
		mSettingCbox.setOutsideChecked(true);
		mSettingCbox.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP){
					if ((event.getX() <= mSettingCbox.getWidth() && event.getX() >= 0)
							&& (event.getY() <= mSettingCbox.getHeight() && event.getY() >= 0)) {
						blackSomebody(); 
					}
				}
				return mSettingCbox.onTouchEvent(event);
			}
		});
		
	}
	
	private void blackSomebody() {
		AlertDialog.Builder builder = new AlertDialog.Builder(PersonRealInfoActivity.this);  
        builder.setTitle(R.string.general_tips);  
        builder.setMessage(R.string.delete_this_person);  
        builder.setPositiveButton(R.string.btn_confirm,  
                new DialogInterface.OnClickListener() {  
                    public void onClick(DialogInterface dialog, int whichButton) {  
                    	requestBlackSomebody();
                    }  
                });  
        builder.setNegativeButton(R.string.btn_cancel,  
                new DialogInterface.OnClickListener() {  
                    public void onClick(DialogInterface dialog, int whichButton) {  
                    	
                    }  
                });  
        builder.show();
	}
	
	private void requestBlackSomebody() {
		if (uid > 0) {
			mSocialEventLogic.requestBlackSomebody(uid);
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if (uid == -1) {
			mProfileEventLogic.getProfileOhterInfo("", null);
		} else {
			mProfileEventLogic.getProfileOhterInfo(uid + "" , null);
		}
	}
	/**
	 * EventBus订阅者事件通知的函数, UI线程
	 * 
	 * @param msg
	 */
	public void onEventMainThread(Message msg) {
		if (msg.obj instanceof InfoResult) {
			InfoResult infoResult = (InfoResult) msg.obj;
			switch (msg.what) {
				case R.id.profile_other_info:
					// 将返回的JSON数据展示出来
					JSONObject mJSONResult = (JSONObject) JSONObject.parse(infoResult.getResult());
					PublicUserProfile mUserProfile = new PublicUserProfile(mJSONResult.getJSONObject(GlobalConstants.JSON_USERPROFILE));
					if (mUserProfile != null) {
	
						// 个人信息
						UserBasic mBasicinfo = new UserBasic(mUserProfile.getBasic());
						if (mBasicinfo.getRealname() != null) {
							mName.setText(mBasicinfo.getRealname());
						}else{
							mName.setVisibility(View.GONE);
						}
						if (mBasicinfo.getAge() != -1) {
							mAge.setText(getString(R.string.other_profile_age, mBasicinfo.getAge()));
						}else{
							mAge.setVisibility(View.GONE);
						}
						if (mBasicinfo.getCity() != null) {
							mCity.setText(mBasicinfo.getCity());
						}else {
							mCity.setVisibility(View.GONE);
						}
						if (mBasicinfo.getExpryear() != null) {
							mExpr.setText(mBasicinfo.getExpryear());
						}else{
							mExpr.setVisibility(View.GONE);
						}
						if (mBasicinfo.getTitle() != null) {
							mPosition.setText(mBasicinfo.getTitle());
						}else{
							mPosition.setVisibility(View.GONE);
						}
						
						StringBuilder sb = new StringBuilder();
						if (mBasicinfo.getCorp() != null) {
							sb.append(mBasicinfo.getCorp());
						}
						mCorpAndInds.setText(sb.toString());
						
						mAvatar.setDefaultImageResId(R.drawable.general_user_avatar);
						mAvatar.setImageUrl(mBasicinfo.getRealavatar(), mImageLoader);
						// 访问网络请求成功，才显示整体的View
						mRealCardLayout.setVisibility(View.VISIBLE);
					}
					break;
				case R.id.social_black_somebody:
					if (infoResult.getMessage().getStatus() == 0) {
						// 销毁PersonRealInfoActivity
						AppActivityManager.getInstance().finishActivity();
						// 销毁ChatPersonActivity
						AppActivityManager.getInstance().finishActivity();
						// 删除数据库中与此人的所有消息记录
						if (did > 0) {
							ImDbManager.getInstance().deleteP2PMessage(did);
						}
					}
					VToast.show(this, infoResult.getMessage().getMsg());
					break;
				default:
					break;
			}
		} else if (msg.obj instanceof VolleyError) {
			
		}
	}


	@Override
	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
			case R.id.menu_bar_right:
				
				intent = getIntent();
				int jobId = intent.getIntExtra(GlobalConstants.JOB_ID, -1);
				String oneword = intent.getStringExtra(GlobalConstants.JOB_ONEWORD);
				if(jobId == -1){
					return;
				}
				mPositionEventLogic.cancel(R.id.post_resume_nick_apply);
				mPositionEventLogic.applyNickFile(String.valueOf(jobId), oneword);
				
				break;
			case R.id.person_real_baseinfo:
				intent = new Intent();
				intent.setClass(this, PersonalShowPageActivity.class);
				intent.putExtra(GlobalConstants.UID, String.valueOf(uid));
				startActivity(intent);
				
				break;
			case R.id.group_msg_setting_cb:
				mSettingCbox.setChecked(!mSettingCbox.isChecked());
				Log.i(TAG, "checkbox check");
				
				break;
			default:
				break;
		}
	}

}
