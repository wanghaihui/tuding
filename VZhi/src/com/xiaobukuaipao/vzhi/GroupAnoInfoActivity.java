package com.xiaobukuaipao.vzhi;

import android.content.Intent;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
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
import com.xiaobukuaipao.vzhi.util.GlobalConstants;
import com.xiaobukuaipao.vzhi.util.StringUtils;
import com.xiaobukuaipao.vzhi.util.VToast;
import com.xiaobukuaipao.vzhi.view.RoundedNetworkImageView;
import com.xiaobukuaipao.vzhi.wrap.ProfileWrapActivity;

/**
 * 群匿名卡片
 * 
 * @since 2015年01月06日11:22:33
 */
public class GroupAnoInfoActivity extends ProfileWrapActivity implements OnClickListener {

	public static final String TAG = GroupAnoInfoActivity.class.getSimpleName();
	private ScrollView mAnoCardLayout;

	private RoundedNetworkImageView mAvatar;
	private TextView mName;
	private TextView mAge;
	private TextView mCity;
	private TextView mPositionAndExpr;
	private TextView mCorpAndInds;
	
	// 请求队列
	private RequestQueue mQueue;
	private ImageLoader mImageLoader;
	
	private long uid = -1;
	
	private AnonyBasicInfo mNormalInfo;
	/**
	 * 初始化UI数据
	 */
	public void initUIAndData() {
		//处理一些状态值
		uid = getIntent().getLongExtra(GlobalConstants.UID, uid);
		
		setContentView(R.layout.activity_group_anonymous_card);
		setHeaderMenuByCenterTitle(R.string.group_chat_member_card);
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
		
		findViewById(R.id.action_chat).setOnClickListener(this);
		findViewById(R.id.action_friend).setOnClickListener(this);
		findViewById(R.id.action_report).setOnClickListener(this);
		findViewById(R.id.group_ano_baseinfo).setOnClickListener(this);
		
		mProfileEventLogic.getAnoCard(uid != -1 ? String.valueOf(uid) : "");
		
		findViewById(R.id.ano_card_tab).setVisibility(View.GONE);
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

				if (mUserProfile.getIsbuddy() == 1) {//如果为好友就不显示下面的菜单栏,也无法点击头像去个人profile
					findViewById(R.id.ano_card_tab).setVisibility(View.GONE);
					findViewById(R.id.group_ano_baseinfo).setEnabled(false);
				} else {
					findViewById(R.id.ano_card_tab).setVisibility(View.VISIBLE);
					findViewById(R.id.group_ano_baseinfo).setEnabled(true);
				}
				
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
			case R.id.post_resume_nick_apply:
				Intent intent = getIntent();
				if (infoResult.getMessage().getStatus() == 0) {
					intent.putExtra(GlobalConstants.JOB_HASAPPLIED, 1);
				}
				VToast.show(this, infoResult.getMessage().getMsg());
				setResult(RESULT_OK, intent);
				AppActivityManager.getInstance().finishActivity(GroupAnoInfoActivity.this);
				break;
			case R.id.social_stranger_send_greeting:
				if(infoResult.getMessage().getStatus() == 0){
					
				}
				VToast.show(this, infoResult.getMessage().getMsg());
				break;
			case R.id.social_send_invitation:
				if(infoResult.getMessage().getStatus() == 0){
					
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
		case R.id.action_chat:
			if(uid == -1){
				return;
			}
			long id = uid;
			
			Intent chat = new Intent();
			chat.setClass(this, ChatPersonActivity.class);
			chat.putExtra("sender", id);
			chat.putExtra("receiverIsreal", 0);
			chat.putExtra("mineIsreal", 0);
			if (!StringUtils.isEmpty(mNormalInfo.getNickname())) {
				chat.putExtra("dname", mNormalInfo.getNickname());
			}
			startActivity(chat);
			break;
		case R.id.action_question:
			
			break;
		case R.id.action_friend:
			mSocialEventLogic.cancel(R.id.social_send_invitation);
			mSocialEventLogic.sendInvitation(String.valueOf(uid));
			break;
		case R.id.action_report:
			Intent intentOfficier = new Intent(GroupAnoInfoActivity.this, OfficierActivity.class);
			if (uid != -1) {
				intentOfficier.putExtra("report_id", String.valueOf(uid));
			}
			startActivity(intentOfficier);
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
		case R.id.menu_bar_right2:
			intent = getIntent();
			intent.setClass(this, PersonalEditPageActivity.class);
			startActivity(intent);
			
			break;
		case R.id.group_msg_setting_cb:
			break;
		case R.id.group_ano_baseinfo:
			
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
