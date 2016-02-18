package com.xiaobukuaipao.vzhi;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.ViewTreeObserver.OnScrollChangedListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ScrollView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.umeng.analytics.MobclickAgent;
import com.xiaobukuaipao.vzhi.cache.CommonBitmapMemoryCache;
import com.xiaobukuaipao.vzhi.domain.user.AnonyBasicInfo;
import com.xiaobukuaipao.vzhi.domain.user.Intention;
import com.xiaobukuaipao.vzhi.domain.user.PrivateUserProfile;
import com.xiaobukuaipao.vzhi.event.InfoResult;
import com.xiaobukuaipao.vzhi.im.ImDbManager;
import com.xiaobukuaipao.vzhi.manager.GeneralDbManager;
import com.xiaobukuaipao.vzhi.util.GlobalConstants;
import com.xiaobukuaipao.vzhi.util.Logcat;
import com.xiaobukuaipao.vzhi.util.StringUtils;
import com.xiaobukuaipao.vzhi.util.VToast;
import com.xiaobukuaipao.vzhi.view.LoadingDialog;
import com.xiaobukuaipao.vzhi.view.RoundedNetworkImageView;
import com.xiaobukuaipao.vzhi.wrap.ProfileWrapActivity;

/**
 *	匿名profile 只有在点击头像时进入匿名名片
 *
 *	@since 2015年01月05日21:05:37
 */
public class AnoPersonInfoActivity extends ProfileWrapActivity implements OnClickListener, OnGlobalLayoutListener, OnScrollChangedListener {

	public static final String TAG = AnoPersonInfoActivity.class.getSimpleName();
	
	private View mMenuBar;
	
	private ScrollView mAnoCardLayout;
	
	private View topLayout;
	
	
	private RoundedNetworkImageView mAvatar;
	private TextView mName;
	private TextView mLocAndExpr;
	private TextView mPositionCorpAndIndustry;
//	private TextView mPositionAndExpr;
//	private TextView mCorpAndInds;
	
	private TextView mDegreeAndSch;
	private TextView mMajor;
	
	
	private TextView mIntentPosition;
	private TextView mVocation;
	private TextView mSalary;
	private TextView mIntentCity;

	private Intention mIntention;

	private TextView mSkillsText;
	
	private View mSkillsViewContainer;
	// 请求队列
	private RequestQueue mQueue;
	
	private ImageLoader mImageLoader;
	
	/**
	 * 判断是否是从 投递确认过来的
	 */
	private boolean mIsCardReview = false;
	/**
	 * 
	 */
	private String uid;
	/**
	 * 判断是否是从 候选人过来的
	 */
	private boolean mIsCandidate = false;
	/**
	 * 
	 */
	private AnonyBasicInfo mNormalInfo;
	
	private boolean mSelf = false;
	/**
	 * 是否完善信息的提示对话框
	 */
	private AlertDialog mAlertDialog;
	
	private int mPressedId = -1;
	/**
	 * loading对话框
	 */
	private LoadingDialog loadingDialog;
	
	private TextView mPersonalSign;
	/**
	 * 初始化UI数据
	 */
	public void initUIAndData() {
		// 接收控制字段
		uid = getIntent().getStringExtra(GlobalConstants.UID);
		mIsCardReview = getIntent().getBooleanExtra(GlobalConstants.JOB_REVIEW, mIsCardReview);
		mIsCandidate = getIntent().getBooleanExtra(GlobalConstants.CANDIDATE_VIEW, false);
		
		setContentView(R.layout.activity_anonymous_card);
		setHeaderMenuByCenterTitle(R.string.ano_card);
		setHeaderMenuByLeft(this);
		
		mMenuBar = findViewById(R.id.register_menu_bar_id);
		mMenuBar.setBackgroundColor(Color.argb(0x99, 0x2c, 0xc0, 0xeb));
		
		mAnoCardLayout = (ScrollView) findViewById(R.id.anonymou_card_layout);
		mAnoCardLayout.getViewTreeObserver().addOnScrollChangedListener(this);
		mAnoCardLayout.setVisibility(View.INVISIBLE);
		mAnoCardLayout.smoothScrollTo(0, 0);
		
		topLayout = findViewById(R.id.top);
		topLayout.getViewTreeObserver().addOnGlobalLayoutListener(this);
		
		findViewById(R.id.ano_card_bottom).setVisibility(View.GONE);
		findViewById(R.id.ano_card_tab).setVisibility(View.VISIBLE);
		
		mPersonalSign = (TextView) findViewById(R.id.personal_sign);
		
		mAvatar = (RoundedNetworkImageView) findViewById(R.id.ano_card_avatar);
		mName = (TextView) findViewById(R.id.ano_card_name);
		mLocAndExpr = (TextView) findViewById(R.id.ano_card_loc_and_expr);
		mPositionCorpAndIndustry = (TextView) findViewById(R.id.ano_card_position_corp_and_industry);
		
		mDegreeAndSch = (TextView) findViewById(R.id.ano_card_degree_school);
		mMajor = (TextView) findViewById(R.id.ano_card_major);
		
		mIntentPosition = (TextView) findViewById(R.id.person_position);
		mVocation = (TextView) findViewById(R.id.person_vocation);
		mIntentCity = (TextView) findViewById(R.id.person_intent_city);
		mSalary = (TextView) findViewById(R.id.person_salary);
		
		mSkillsText = (TextView) findViewById(R.id.personal_skills);
		mSkillsViewContainer = findViewById(R.id.ano_card_skills);
		
		
		mQueue = Volley.newRequestQueue(this);
		mImageLoader = new ImageLoader(mQueue, new CommonBitmapMemoryCache());

		//对界面更改,底部状态栏,和顶部菜单
		if (mIsCardReview) {
			findViewById(R.id.ano_card_bottom).setVisibility(View.GONE);
			setHeaderMenuByRight2(R.string.general_tips_edit).setOnClickListener(this);
			setHeaderMenuByRight(R.string.general_tips_post).setOnClickListener(this);
		}
		
		
		if(StringUtils.isNotEmpty(uid) && uid.equals(String.valueOf(ImDbManager.getInstance().getUid(this)))){
			findViewById(R.id.ano_card_bottom).setVisibility(View.GONE);
			mSelf = true;
		}
		
		
		// Candidate
		findViewById(R.id.action_interest).setOnClickListener(this);
		findViewById(R.id.action_interview).setOnClickListener(this);
		findViewById(R.id.action_have_a_chat).setOnClickListener(this);
		findViewById(R.id.action_report_another).setOnClickListener(this);
		findViewById(R.id.action_chat).setOnClickListener(this);
		findViewById(R.id.action_friend).setOnClickListener(this);
		findViewById(R.id.action_report).setOnClickListener(this);
		// 候选人接收箱过来的
		if (mIsCandidate) {
			findViewById(R.id.ano_card_tab).setVisibility(View.VISIBLE);
			findViewById(R.id.candidate_ano_card_tab).setVisibility(View.VISIBLE);
		}		
		loadingDialog = new LoadingDialog(this);
		
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void onGlobalLayout() {
		topLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
		refreshBgTitlr(topLayout);
	}

	@SuppressWarnings("deprecation")
	private void refreshBgTitlr(final View topLayout) {
		//设置基本信息的背景图片
		Bitmap resource = BitmapFactory.decodeResource(getResources(), R.drawable.person_blur_background2); 
		Bitmap bitmap = null;
		int w = 0;
		int h = 0;
		if(resource.getWidth() > topLayout.getWidth() ){//如果超出就切割否則就用原圖
			w = topLayout.getWidth();
		}else{
			w = resource.getWidth();
		} 
		h = resource.getHeight();
//		if(resource.getHeight() > topLayout.getHeight()){
//			h = topLayout.getHeight();
//		}else{
//			h = resource.getHeight();
//		}
		bitmap = Bitmap.createBitmap(resource, 0, 0, w, h); 
		topLayout.setBackgroundDrawable(new BitmapDrawable(getResources(), bitmap));
	}
	
	int alpha = 0;
	@Override
	public void onScrollChanged() {
		// 当profile滚动到 banner完全消失了 就会让menubar从透明变为不透明
		float a = mAnoCardLayout.getScrollY() / (float) (topLayout.getHeight() - mMenuBar.getHeight()); 
//		Logcat.d("test", "mPersonalEditPage.getScrollY():" + mPersonalEditPage.getScrollY() + "  topLayout.getHeight() " + topLayout.getHeight());
		alpha = (int) ((a > 1 ? 1 : a) * 0xff);
		alpha = alpha < 0 ? 0 : alpha; 
//		Logcat.d("test", "alpha " + alpha);
		mMenuBar.setBackgroundColor(Color.argb(alpha, 0x2c, 0xc0, 0xeb));
		getTitleView().setTextColor(Color.argb(alpha, 0xff, 0xff, 0xff));
	}
	
	public static String sumStrs(int length , String s){
		if(length <= 0)
			return "";
		Logcat.d("test", "length:" + length + "   s:" + s);
		StringBuilder builder = new StringBuilder();
		for(int i = 0; i < length ; i ++){
			builder.append(s);
		}
		return builder.toString();
		
	}
	
	@Override
	protected void onResume() {
		if(StringUtils.isEmpty(uid)){
			// 看自己的
			mProfileEventLogic.getAnoCard(GeneralDbManager.getInstance().getUidFromCookie());
		}else{
			mProfileEventLogic.getAnoCard(uid);
		}
		super.onResume();
	}
	
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
					
					if (mNormalInfo.getNickname() != null) {
						mName.setText(getResources().getString(R.string.general_tips_ano) + "  " + mNormalInfo.getNickname());
					}else{
						mName.setText(getResources().getString(R.string.not_add_str));
					}
					
					StringBuilder sb = new StringBuilder();
					boolean flag = false;
					if(StringUtils.isNotEmpty(mNormalInfo.getCity())){
						sb.append(mNormalInfo.getCity());
						flag = true;
					}
					if(StringUtils.isNotEmpty(mNormalInfo.getExpryear())){
						if(flag){
							sb.append(getString(R.string.general_tips_dot));
						}
						sb.append(mNormalInfo.getExpryear());
					}
					
					if(StringUtils.isNotEmpty(sb.toString())){
						mLocAndExpr.setText(sb.toString());
					}else{
						mLocAndExpr.setVisibility(View.GONE);
					}
					
					flag = false;
					sb.delete(0, sb.length());
					if(StringUtils.isNotEmpty(mNormalInfo.getPosition())){
						sb.append(mNormalInfo.getPosition());
						flag = true;
					}
					if(StringUtils.isNotEmpty(mNormalInfo.getCorp())){
						if(flag){
							sb.append(getString(R.string.general_tips_dot));
						}
						sb.append(mNormalInfo.getCorp());
					}
					if(StringUtils.isNotEmpty(mNormalInfo.getIndustry())){
						if(flag){
							sb.append(getString(R.string.general_tips_dot));
						}
						sb.append(mNormalInfo.getIndustry());
					}
					
					if(StringUtils.isNotEmpty(sb.toString())){
						mPositionCorpAndIndustry.setText(sb.toString());
					}else{
						mPositionCorpAndIndustry.setVisibility(View.INVISIBLE);
					}
					
					mAvatar.setDefaultImageResId(R.drawable.general_user_avatar);
					mAvatar.setImageUrl(mNormalInfo.getAvatar(), mImageLoader);
					
					//个人签名
					if(StringUtils.isNotEmpty(mNormalInfo.getIntroduce())){
						mPersonalSign.setText(mNormalInfo.getIntroduce());
					}
					//技能标签
					
					sb.delete(0, sb.length());
					if (mUserProfile.getSkills() != null && mUserProfile.getSkills().size() > 0) {
						mSkillsText.setVisibility(View.VISIBLE);
						
						String splite = getString(R.string.general_tips_spilte);
						for (int i = 0; i < mUserProfile.skills.size(); i++) {
							sb.append(mUserProfile.skills.getJSONObject(i).getString(GlobalConstants.JSON_NAME));
							sb.append(splite);
						}
						mSkillsText.setText(sb.substring(0, sb.length() - splite.length()));
						
					} else {
						mSkillsViewContainer.setVisibility(View.GONE);
					}
					
					//加载教育背景
					
					sb.delete(0, sb.length());
					if(StringUtils.isNotEmpty(mNormalInfo.getDegree()) && StringUtils.isNotEmpty(mNormalInfo.getSchool()) && StringUtils.isNotEmpty(mNormalInfo.getMajor())){
						sb.append(mNormalInfo.getDegree());
						sb.append("/");
						sb.append(mNormalInfo.getSchool());
						mDegreeAndSch.setText(sb.toString());
						mMajor.setText(mNormalInfo.getMajor());
					}else{
						findViewById(R.id.ano_card_edu_experience).setVisibility(View.GONE);
					}
					
					//加载求职意向
					
					if (mUserProfile.getIntention() != null) {
						mIntention = new Intention(mUserProfile.getIntention());
					}
					if(mIntention != null){
						sb.delete(0, sb.length());
						sb.append(getResources().getString(R.string.intention_position));
						sb.append(" : ");
						if(mIntention.getPositions() != null){
							if (mIntention.getPositions().size() > 0) {
								for (int i = 0; i < mIntention.getPositions().size() - 1; i++) {
									sb.append(mIntention.getPositions().getJSONObject(i).getString(GlobalConstants.JSON_NAME));
									sb.append(", ");
								}
								sb.append(mIntention.getPositions().getJSONObject(mIntention.getPositions().size() - 1).getString(GlobalConstants.JSON_NAME));
								mIntentPosition.setText(sb.toString());
							}
						}else{
							mIntentPosition.setVisibility(View.GONE);
						}
						
						// 行业
						if (mIntention.getIndustry() != null && mIntention.getIndustry().getString(GlobalConstants.JSON_NAME) != null && mIntention.getIndustry().getString(GlobalConstants.JSON_NAME).length() > 0) {
							mVocation.setText(getResources().getString(R.string.intention_industry)+ " : " + mIntention.getIndustry().getString(GlobalConstants.JSON_NAME));
						}else{
							mVocation.setVisibility(View.GONE);
						}
						//城市
						sb.delete(0, sb.length());
						sb.append(getResources().getString(
								R.string.intention_city));
						sb.append(" : ");
						if (mIntention.getCities() != null && mIntention.getCities().size() > 0) {
							for (int i = 0; i < mIntention.getCities().size() - 1; i++) {
								sb.append(mIntention.getCities().get(i));
								sb.append(", ");
							}
							sb.append(mIntention.getCities().get(mIntention.getCities().size() - 1));
							mIntentCity.setText(sb.toString());
						}else{
							mIntentCity.setVisibility(View.GONE);
						}
						//薪水
						if (mIntention.getMinsalary() != null || mIntention.getMinsalary().getString(GlobalConstants.JSON_NAME).length() > 0) {
							mSalary.setText(getResources().getString(R.string.intention_salary)+ " : " + mIntention.getMinsalary().getString(GlobalConstants.JSON_NAME));
						} else {
							mSalary.setVisibility(View.GONE);
						}
					
					}else{
						findViewById(R.id.ano_card_intention).setVisibility(View.GONE);
					}
					
					// 访问网络请求成功，才显示整体的View
					if (mAnoCardLayout.getVisibility() != View.VISIBLE) {
						Animation loadAnimation = AnimationUtils.loadAnimation(this,R.anim.anim_fide_in_for_recruit);
						mAnoCardLayout.startAnimation(loadAnimation);
						mAnoCardLayout.setVisibility(View.VISIBLE);
					}
					
					// 如果是候选人接收箱过来的
					if (mIsCandidate) {
						if (getIntent().getIntExtra(GlobalConstants.IS_CONTACTED, 0) == 1) {
							// 说明已经感兴趣
							findViewById(R.id.action_interest).setVisibility(View.GONE);
						} else {
							// 说明不敢兴趣
							findViewById(R.id.action_interest).setVisibility(View.VISIBLE);
						}
						
						findViewById(R.id.ano_card_tab).setVisibility(View.GONE);
						findViewById(R.id.candidate_ano_card_tab).setVisibility(View.VISIBLE);
					}
					
					if (!mIsCardReview&& !mSelf) {
						findViewById(R.id.ano_card_bottom).setVisibility(View.VISIBLE);
					}
					
				break;
			case R.id.post_resume_nick_apply:
				Intent intent = getIntent();
				if (infoResult.getMessage().getStatus() == 0) {
					intent.putExtra(GlobalConstants.JOB_HASAPPLIED, 1);
				}
				VToast.show(this, infoResult.getMessage().getMsg());
				setResult(RESULT_OK, intent);
				AppActivityManager.getInstance().finishActivity(AnoPersonInfoActivity.this);
				// finish();
				break;
			case R.id.social_send_invitation:
				if(infoResult.getMessage().status == 0){
					
				}
				VToast.show(this, infoResult.getMessage().getMsg());
				break;
			case R.id.social_stranger_send_greeting:
				if(infoResult.getMessage().status == 0){
					
				}
				VToast.show(this, infoResult.getMessage().getMsg());
				break;
				
			case R.id.position_interest_candidate:
				if (infoResult.getMessage().getStatus() == 0) {
					// 如果感兴趣设置成功
					intent = getIntent();
					
					intent.putExtra(GlobalConstants.CONTACT_STATUS, intent.getIntExtra(GlobalConstants.CONTACT_STATUS, 0) |1);
					Logcat.d("@@@", "position_interest_candidate" + intent.getIntExtra(GlobalConstants.CONTACT_STATUS, 0));
					setResult(RESULT_OK, intent);

				}
				VToast.show(this, infoResult.getMessage().getMsg());
				break;
				
			case R.id.social_candidate_chat:
				// 聊一聊
				if (infoResult.getMessage().getStatus() == 0) {
					// 此时会返回一个did, 接着可以打开一个对话dialog
					long id = Long.parseLong(uid);
					int contactStatus = getIntent().getIntExtra(GlobalConstants.CONTACT_STATUS, 0) | 16;
					getIntent().putExtra(GlobalConstants.CONTACT_STATUS , contactStatus);
					setResult(RESULT_OK, getIntent());
					Intent intentChat = new Intent(this, ChatPersonActivity.class);
					intentChat.putExtra("sender", id);
					intentChat.putExtra("is_real", getIntent().getIntExtra(GlobalConstants.ISREAL,0));
					if (!StringUtils.isEmpty(mName.getText().toString())) {
						intentChat.putExtra("dname", mName.getText().toString());
					}
					if(mIsCandidate){
						intentChat.putExtra("is_candidate", true);
					}
					startActivity(intentChat);
				}
				
				break;
			case R.id.post_resume_complete_status_get:
				JSONObject completeResult = (JSONObject) JSONObject.parse(infoResult.getResult());
				if(completeResult != null){
					JSONObject detail = completeResult.getJSONObject(GlobalConstants.JSON_DETAIL);
					
					if (detail != null) {
						int work = detail.getInteger(GlobalConstants.JSON_WORKEXPR);
						if (work == 0) {
							showCompleteDialog();
							break;
						}
					}
					GeneralDbManager.getInstance().setWorkPerform(1);
					lowPressed();
				}
				break;
			default:
				break;
			}
		} else if (msg.obj instanceof VolleyError) {
			if(loadingDialog.isShowing()){
				loadingDialog.dismiss();
			}
			VToast.show(this, getString(R.string.general_tips_network_unknow));
		}
	}
	private void showCompleteDialog() {
		if(mAlertDialog != null && mAlertDialog.isShowing()) return;
		
		mAlertDialog = new AlertDialog.Builder(AnoPersonInfoActivity.this)
		.setTitle(R.string.general_tips)
		.setMessage(R.string.complete_workexpr_info).setNegativeButton(R.string.general_tips_to_complete,new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog,int which) {
				Intent intent = getIntent();
				intent.setClass(AnoPersonInfoActivity.this,PersonalEditPageActivity.class);
				startActivity(intent);
			}
		})
		.setPositiveButton(getString(R.string.btn_cancel),null).create();
		mAlertDialog.show();
	}
	
	private void lowPressed() {
		switch (mPressedId) {
		case R.id.action_friend:
			mSocialEventLogic.cancel(R.id.social_send_invitation);
			mSocialEventLogic.sendInvitation(String.valueOf(uid));
			break;
		case R.id.action_chat:
			if(StringUtils.isNotEmpty(uid)){
				long id = Long.parseLong(uid);
				Intent chat = new Intent();
				chat.setClass(this, ChatPersonActivity.class);
				chat.putExtra("sender", id);
				chat.putExtra("receiverIsreal", 0);
				if (!StringUtils.isEmpty(mNormalInfo.getNickname())) {
					chat.putExtra("dname", mNormalInfo.getNickname());
				}
				startActivity(chat);
			}
			break;
		default:
			break;
		}
		mPressedId = -1;
	}


	@Override
	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
		case R.id.action_greet:
			MobclickAgent.onEvent(AnoPersonInfoActivity.this, "dzh");
			mSocialEventLogic.cancel(R.id.social_stranger_send_greeting);
			mSocialEventLogic.sendGreeting(String.valueOf(uid), null);
			break;
		case R.id.action_question:
			break;
		case R.id.action_friend:
			MobclickAgent.onEvent(AnoPersonInfoActivity.this, "jhy");
			mPressedId = R.id.action_friend;
			completionQuery();
			break;
			
		case R.id.action_report_another:
		case R.id.action_report:
			MobclickAgent.onEvent(AnoPersonInfoActivity.this, "jb");
			if(StringUtils.isNotEmpty(uid)){
				Intent intentOfficier = new Intent(this, OfficierActivity.class);
				intentOfficier.putExtra("report_id", String.valueOf(uid));
				startActivity(intentOfficier);
			}
			break;
		case R.id.action_chat:
			mPressedId = R.id.action_chat;
			completionQuery();
			break;
		case R.id.menu_bar_right:
			
			Intent data = getIntent();
			int jobId = data.getIntExtra(GlobalConstants.JOB_ID, -1);
			String oneword = data.getStringExtra(GlobalConstants.JOB_ONEWORD);
			if(jobId == -1){
				return;
			}
			MobclickAgent.onEvent(AnoPersonInfoActivity.this, "tdjlnm");
			mPositionEventLogic.cancel(R.id.post_resume_nick_apply);
			mPositionEventLogic.applyNickFile(String.valueOf(jobId), oneword);
			
			break;
		case R.id.menu_bar_right2:
			intent = getIntent();
			intent.setClass(this, PersonalEditPageActivity.class);
			startActivity(intent);
			break;
			
		case R.id.action_interest:
			// 感兴趣
			mPositionEventLogic.interestCandidate(getIntent().getStringExtra(GlobalConstants.JOB_ID), getIntent().getStringExtra(GlobalConstants.UID));
			break;
			
		case R.id.action_interview:
			// 约面试
			Intent intentView = getIntent();
			intentView.setClass(this, AuditionActivity.class);
			intentView.putExtra(GlobalConstants.JOB_ID, getIntent().getStringExtra(GlobalConstants.JOB_ID));
			intentView.putExtra(GlobalConstants.UID, getIntent().getStringExtra(GlobalConstants.UID));
			intentView.putExtra(GlobalConstants.ISREAL, getIntent().getIntExtra(GlobalConstants.ISREAL, 0));
			startActivityForResult(intentView,202);
			break;
		case R.id.action_have_a_chat:

			// 执行聊一聊操作
			mSocialEventLogic.candidateChat(getIntent().getStringExtra(GlobalConstants.JOB_ID), getIntent().getStringExtra(GlobalConstants.UID), getIntent().getIntExtra(GlobalConstants.ISREAL, 0));
			break;
		default:
			break;
		}
	}
	
	private void completionQuery() {
		if(GeneralDbManager.getInstance().getWorkPerform() == 1){
			lowPressed();
		}else{
			mProfileEventLogic.cancel(R.id.post_resume_complete_status_get);
			mProfileEventLogic.getCompletion();
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == Activity.RESULT_OK){
			if(requestCode == 202){
				setIntent(data);
				setResult(RESULT_OK, getIntent());
			}
		}
	}
}
