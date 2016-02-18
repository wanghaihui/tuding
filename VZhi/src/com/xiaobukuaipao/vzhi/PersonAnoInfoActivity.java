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
import android.widget.ScrollView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.xiaobukuaipao.vzhi.cache.CommonBitmapMemoryCache;
import com.xiaobukuaipao.vzhi.domain.user.AnonyBasicInfo;
import com.xiaobukuaipao.vzhi.domain.user.PrivateUserProfile;
import com.xiaobukuaipao.vzhi.event.InfoResult;
import com.xiaobukuaipao.vzhi.im.ImDbManager;
import com.xiaobukuaipao.vzhi.util.GlobalConstants;
import com.xiaobukuaipao.vzhi.util.StringUtils;
import com.xiaobukuaipao.vzhi.util.VToast;
import com.xiaobukuaipao.vzhi.view.CheckBox;
import com.xiaobukuaipao.vzhi.view.RoundedNetworkImageView;
import com.xiaobukuaipao.vzhi.wrap.ProfileWrapActivity;

/**
 *	p2p聊天的时候 查看匿名名片
 */
public class PersonAnoInfoActivity extends ProfileWrapActivity implements OnClickListener {

	public static final String TAG = GroupAnoInfoActivity.class.getSimpleName();
	private ScrollView mAnoCardLayout;

	private RoundedNetworkImageView mAvatar;
	private TextView mName;
	private TextView mAge;
	private TextView mCity;
	private TextView mPositionAndExpr;
	private TextView mCorpAndInds;
	private CheckBox mSettingCbox;
	
	// 请求队列
	private RequestQueue mQueue;
	private ImageLoader mImageLoader;
	
	private long uid = -1;
	
	private long did = -1;
	
	private AnonyBasicInfo mNormalInfo;
	/**
	 * 初始化UI数据
	 */
	@SuppressLint("ClickableViewAccessibility")
	public void initUIAndData() {
		//处理一些状态值
		uid = getIntent().getLongExtra(GlobalConstants.UID, uid);
		did = getIntent().getLongExtra(GlobalConstants.DID, did);
		
		setContentView(R.layout.activity_person_anonymous_card);
		setHeaderMenuByCenterTitle(R.string.person_chat_member_card);
		setHeaderMenuByLeft(this);
		mAnoCardLayout = (ScrollView) findViewById(R.id.anonymou_card_layout);
		mAnoCardLayout.setVisibility(View.INVISIBLE);

		mAvatar = (RoundedNetworkImageView) findViewById(R.id.ano_card_avatar);
		mName = (TextView) findViewById(R.id.ano_card_name);
		mAge = (TextView) findViewById(R.id.ano_card_basicinfo_age);
		mCity = (TextView) findViewById(R.id.ano_card_basicinfo_city);
		
		mPositionAndExpr = (TextView) findViewById(R.id.ano_card_position_expr);
		mCorpAndInds = (TextView) findViewById(R.id.ano_card_company);

		mQueue = Volley.newRequestQueue(this);
		mImageLoader = new ImageLoader(mQueue, new CommonBitmapMemoryCache());
		
		mSettingCbox = (CheckBox) findViewById(R.id.group_msg_setting_cb);
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
		
		findViewById(R.id.person_ano_baseinfo).setOnClickListener(this);
	}
	
	private void blackSomebody() {
		AlertDialog.Builder builder = new AlertDialog.Builder(PersonAnoInfoActivity.this);  
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
		if(uid == -1){
			mProfileEventLogic.getAnoCard("");
		}else{
			mProfileEventLogic.getAnoCard(String.valueOf(uid));
		}
		super.onResume();
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
			case R.id.ano_card_get:
				// 将返回的JSON数据展示出来
				JSONObject mJSONResult = (JSONObject) JSONObject.parse(infoResult.getResult());
				PrivateUserProfile mUserProfile = new PrivateUserProfile(mJSONResult);
				mNormalInfo = new AnonyBasicInfo(mUserProfile.getBasic());
				//匿名名片基本信息
				if (StringUtils.isNotEmpty(mNormalInfo.getNickname())) {
					mName.setText(mNormalInfo.getNickname());
				}
				if (mNormalInfo.getAge() != -1) {
					mAge.setText(getString(R.string.general_people_age,mNormalInfo.getAge()));
				}
				if (StringUtils.isNotEmpty(mNormalInfo.getCity())) {
					mCity.setText(mNormalInfo.getCity());
				}
				
				
				StringBuilder sb = new StringBuilder();
				if (!StringUtils.isEmpty(mNormalInfo.getPosition())) {
					sb.append(mNormalInfo.getPosition());
				}
				
				if (mNormalInfo.getExpr() != null) {
					if(!StringUtils.isEmpty(mNormalInfo.getPosition())){
						sb.append(" / ");
					}
					sb.append(mNormalInfo.getExprName());
				}
				
				mPositionAndExpr.setText(sb.toString());
				sb.delete(0, sb.length());
				if (mNormalInfo.getCorp() != null) {
					sb.append(mNormalInfo.getCorp());
				}
				if (mNormalInfo.getIndustry() != null) {
					sb.append(" / ");
					sb.append(mNormalInfo.getIndustry());
				}
				mCorpAndInds.setText(sb.toString());
				
				mAvatar.setDefaultImageResId(R.drawable.general_default_ano);
				mAvatar.setImageUrl(mNormalInfo.getAvatar(), mImageLoader);
				// 访问网络请求成功，才显示整体的View
				mAnoCardLayout.setVisibility(View.VISIBLE);
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
			VToast.show(this, getString(R.string.general_tips_network_unknow));
		}
	}


	@Override
	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
			case R.id.action_chat:
				if(uid == -1){
					return;
				}
				long id = uid;
				
				Intent chat = getIntent();
				chat.setClass(this, ChatPersonActivity.class);
				chat.putExtra("sender", id);
				chat.putExtra("receiverIsreal", 0);
				chat.putExtra("mineIsreal", 0);
				if (!StringUtils.isEmpty(mNormalInfo.getNickname())) {
					chat.putExtra("dname", mNormalInfo.getNickname());
				}
				startActivity(chat);
			break;
			case R.id.menu_bar_right:
				
				Intent data = getIntent();
				int jobId = data.getIntExtra(GlobalConstants.JOB_ID, -1);
				String oneword = data.getStringExtra(GlobalConstants.JOB_ONEWORD);
				if(jobId == -1){
					return;
				}
				mPositionEventLogic.cancel(R.id.post_resume_nick_apply);
				mPositionEventLogic.applyNickFile(String.valueOf(jobId), oneword);
				
				break;
			case R.id.group_msg_setting_cb:
				mSettingCbox.setChecked(!mSettingCbox.isChecked());
				
				break;
			case R.id.person_ano_baseinfo:
				intent = new Intent();
				intent.setClass(this, AnoPersonInfoActivity.class);
				intent.putExtra(GlobalConstants.UID, String.valueOf(uid));
				startActivity(intent);
				break;
			default:
				break;
		}
	}

}
