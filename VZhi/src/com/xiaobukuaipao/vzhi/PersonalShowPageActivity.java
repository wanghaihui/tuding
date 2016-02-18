package com.xiaobukuaipao.vzhi;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.ViewTreeObserver.OnScrollChangedListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.umeng.analytics.MobclickAgent;
import com.xiaobukuaipao.vzhi.adapter.EducationsAdapter;
import com.xiaobukuaipao.vzhi.adapter.ExperiencesAdapter;
import com.xiaobukuaipao.vzhi.adapter.OtherProfileInJobsAdatper;
import com.xiaobukuaipao.vzhi.cache.CommonBitmapMemoryCache;
import com.xiaobukuaipao.vzhi.domain.JobDetailInfo;
import com.xiaobukuaipao.vzhi.domain.user.Education;
import com.xiaobukuaipao.vzhi.domain.user.Experience;
import com.xiaobukuaipao.vzhi.domain.user.PublicUserProfile;
import com.xiaobukuaipao.vzhi.domain.user.UserBasic;
import com.xiaobukuaipao.vzhi.event.InfoResult;
import com.xiaobukuaipao.vzhi.im.ImDbManager;
import com.xiaobukuaipao.vzhi.manager.GeneralDbManager;
import com.xiaobukuaipao.vzhi.util.GlobalConstants;
import com.xiaobukuaipao.vzhi.util.Logcat;
import com.xiaobukuaipao.vzhi.util.StringUtils;
import com.xiaobukuaipao.vzhi.util.VToast;
import com.xiaobukuaipao.vzhi.view.ProgressBarCircularIndeterminate;
import com.xiaobukuaipao.vzhi.view.RoundedNetworkImageView;
import com.xiaobukuaipao.vzhi.wrap.ProfileWrapActivity;

/**
 *	个人profile展示页面<br>
 *	给其他人看的简历信息,是真实简历信息,可设置隐私操作等等
 *
 *	@since 2015年01月05日21:24:00
 */
public class PersonalShowPageActivity extends ProfileWrapActivity implements
		OnClickListener, OnGlobalLayoutListener, OnScrollChangedListener {
	
	private ScrollView mPersonalShow;

	private RoundedNetworkImageView mAvatar;

	// 请求队列
	private RequestQueue mQueue;
	private ImageLoader mImageLoader;
	private UserBasic mBasicinfo;
	/**
	 * 昵称
	 */
	private TextView mNickName;
	/**
	 * 工作地点和经验
	 */
	private TextView mLocExpr;
	/**
	 * 职位和公司
	 */
	private TextView mPositionCorp;
	private LinearLayout mProfileInJobs;
	private ListView mProfileInJobsListView;
	private ArrayList<JobDetailInfo> mProfileInJobsData;
	private OtherProfileInJobsAdatper mProfielInJobsAdatper;

	private LinearLayout mJobExpr;
	private View mHideJob;
	private ListView mJobListView;
	private ExperiencesAdapter mJobAdapter;
	private ArrayList<Experience> mExperiences;

	private LinearLayout mEduExpr;
	private View mHideEdu;
	private ListView mEduListView;
	private EducationsAdapter mEduAdapter;
	private ArrayList<Education> mEducations;

	private LinearLayout mProfileContact;
	private TextView mProfileContactMobile;
	private TextView mProfileContactEmail;
	private View mProfileContactHide;

	private String uid;
	private String username;
	private String jid;
	
	private boolean mSelf = false;
	/**
	 * 判断是否是从 投递确认过来的
	 */
	private boolean mIsCardReview = false;
	
	private boolean mIsCandidate = false;
	
	private AlertDialog.Builder mBuilder;

	private EditText mActionQuestionEdit;

	private TextView mQuestionSender;

	private ProgressBarCircularIndeterminate mLoading;

	/**
	 * 是否完善信息的提示对话框
	 */
	private AlertDialog mAlertDialog;
	
	private int mPressedId = -1;

	private View topLayout;
	
//	private boolean mIsReadMobile;
	
	private boolean mIsReadContacts;
	
	private TextView mSkillsView;
	
	private String mMobileNum;

	private String mUserEmail;

	private View mBottomBar;
	
	private View mMenuBar;

	private View mSkillsViewContainer;

//	private TextView mProfileTipContacts;

	private View mProfileTipsArrow;
	
	private View mProfileTipsLayout;

	private TextView mPersonalSign;
	@Override
	public void initUIAndData() {
		// 处理一些状态值
		mIsCardReview = getIntent().getBooleanExtra(GlobalConstants.JOB_REVIEW, mIsCardReview);
		mIsCandidate = getIntent().getBooleanExtra(GlobalConstants.CANDIDATE_VIEW, false);
		
		jid = getIntent().getStringExtra(GlobalConstants.JOB_ID);
		uid = getIntent().getStringExtra(GlobalConstants.UID);
		uid = StringUtils.isEmpty(uid) ? "" : uid;
		
		mSelf = uid.equals(String.valueOf(ImDbManager.getInstance().getUid(this)));
		
		setContentView(R.layout.activity_main_tab_personal_show);
		setHeaderMenuByLeft(this);
		setHeaderMenuByCenterTitle(R.string.other_profile_title);
		
		mMenuBar = findViewById(R.id.register_menu_bar_id);
		mMenuBar.setBackgroundColor(Color.argb(0x99, 0x2c, 0xc0, 0xeb));
		
		mPersonalShow = (ScrollView) findViewById(R.id.personal_show);
		mPersonalShow.getViewTreeObserver().addOnScrollChangedListener(this);
		mPersonalShow.setVisibility(View.INVISIBLE);
		mPersonalShow.smoothScrollTo(0, 0);
		
		mLoading = (ProgressBarCircularIndeterminate) findViewById(R.id.loading);
		mLoading.setVisibility(View.VISIBLE);
		
		mBottomBar = findViewById(R.id.other_profile_bottom);
		mBottomBar.setVisibility(View.GONE);
		
		topLayout = findViewById(R.id.top);
		topLayout.getViewTreeObserver().addOnGlobalLayoutListener(this);
		
		
		mQueue = Volley.newRequestQueue(this);
		mImageLoader = new ImageLoader(mQueue, new CommonBitmapMemoryCache());
		
		//初始化页面控件
		mNickName = (TextView) findViewById(R.id.other_profile_name);
		mLocExpr = (TextView) findViewById(R.id.other_profile_loc_and_expr);
		mPositionCorp = (TextView) findViewById(R.id.other_profile_position_corp);
		mAvatar = (RoundedNetworkImageView) findViewById(R.id.other_profile_avatar);

		mProfileContact = (LinearLayout) findViewById(R.id.other_profile_contacts);
		mProfileContactMobile = (TextView) findViewById(R.id.other_profile_contacts_mobile);
		mProfileContactEmail = (TextView) findViewById(R.id.other_profile_contacts_email);
		mProfileContactHide = findViewById(R.id.other_profile_contacts_hide);

		//hr点击查看联系方式
		//mProfileTipContacts = (TextView) findViewById(R.id.other_profile_contacts_title);
		mProfileTipsArrow = findViewById(R.id.other_profile_contacts_arrow);
		mProfileTipsLayout = findViewById(R.id.other_profile_contacts_layout);
		
		
		mPersonalSign = (TextView) findViewById(R.id.personal_sign);
		
		mProfileInJobs = (LinearLayout) findViewById(R.id.other_profile_in_jobs);
		mProfileInJobsListView = (ListView) findViewById(R.id.other_profile_in_jobs_listview);
		mProfileInJobsListView.smoothScrollToPosition(0, 0);
		
		mProfileInJobsData = new ArrayList<JobDetailInfo>();
		mProfielInJobsAdatper = new OtherProfileInJobsAdatper(this,mProfileInJobsData, R.layout.item_profile_in_jobs);
		mProfileInJobsListView.setAdapter(mProfielInJobsAdatper);
		
		mJobExpr = (LinearLayout) findViewById(R.id.other_profile_job_experience);
		mHideJob = findViewById(R.id.other_profile_job_experience_hide);
		mJobListView = (ListView) findViewById(R.id.other_profile_job_experience_listview);
		mJobListView.smoothScrollToPosition(0, 0);
		mExperiences = new ArrayList<Experience>();
		mJobAdapter = new ExperiencesAdapter(this, mExperiences,R.layout.job_experience_item);
		mJobListView.setAdapter(mJobAdapter);

		mEduExpr = (LinearLayout) findViewById(R.id.other_profile_edu_experience);
		mHideEdu = findViewById(R.id.other_profile_edu_experience_hide);
		mEduListView = (ListView) findViewById(R.id.other_profile_edu_experience_listview);
		mEduListView.smoothScrollToPosition(0, 0);
		mEducations = new ArrayList<Education>();
		mEduAdapter = new EducationsAdapter(this, mEducations,R.layout.edu_experience_item);
		mEduListView.setAdapter(mEduAdapter);
		
		mSkillsView = (TextView) findViewById(R.id.personal_skills);
		mSkillsViewContainer = findViewById(R.id.ano_card_skills);
		
		mActionQuestionEdit = (EditText) findViewById(R.id.action_question_edit);//提问
		mActionQuestionEdit.setInputType(EditorInfo.TYPE_CLASS_TEXT);

//		findViewById(R.id.action_greet).setOnClickListener(this);
//		findViewById(R.id.action_question).setOnClickListener(this);
		findViewById(R.id.action_friend).setOnClickListener(this);
		findViewById(R.id.action_report).setOnClickListener(this);
		findViewById(R.id.action_chat).setOnClickListener(this);
		findViewById(R.id.action_chat2).setOnClickListener(this);
		findViewById(R.id.action_unbind).setOnClickListener(this);
		findViewById(R.id.action_unbind).setEnabled(!"10000".equals(uid));
		
		// Candidate
		findViewById(R.id.action_interest).setOnClickListener(this);
		findViewById(R.id.action_interview).setOnClickListener(this);
		findViewById(R.id.action_have_a_chat).setOnClickListener(this);
		findViewById(R.id.action_report_another).setOnClickListener(this);
		
		// 对界面更改,底部状态栏,和顶部菜单
		if (mIsCardReview) {
			setHeaderMenuByRight2(R.string.general_tips_edit).setOnClickListener(this);
			setHeaderMenuByRight(R.string.general_tips_post).setOnClickListener(this);
		}
		
		// 候选人接收箱过来的
		if (mIsCandidate) {
			findViewById(R.id.candidate_profile_tab).setVisibility(View.VISIBLE);
			mProfileContact.setVisibility(View.INVISIBLE);
		}
		
		mBuilder = new AlertDialog.Builder(this);
		
		
		if (mSelf) {
			findViewById(R.id.other_profile_bottom).setVisibility(View.GONE);
		}
		
		mQuestionSender = (TextView) findViewById(R.id.action_question_send);
		mQuestionSender.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String question = mActionQuestionEdit.getText().toString();
				mActionQuestionEdit.setText("");
				if (StringUtils.isNotEmpty(question)) {
					mSocialEventLogic.cancel(R.id.social_stranger_send_question);
					mSocialEventLogic.sendQuestion(uid, question);
					
				}
				findViewById(R.id.action_question_edit_layout).setVisibility(View.GONE);
			}
		});
		mProfileInJobsListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				Intent intent = new Intent();
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.setClass(PersonalShowPageActivity.this, JobPositionInfoActivity.class);
				intent.putExtra(GlobalConstants.JOB_ID, Integer.parseInt(mProfileInJobsData.get(position).getId()));
				startActivity(intent);
				
			}
		});
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
		Bitmap resource = BitmapFactory.decodeResource(getResources(), R.drawable.person_blur_background); 
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
	
	
	/*
	 * 当提问的时候，点击其他部分会隐藏提问的输入框
	 * @see com.xiaobukuaipao.vzhi.BaseFragmentActivity#dispatchTouchEvent(android.view.MotionEvent)
	 */
	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		
		int[] leftTop = { 0, 0 };
		// 获取输入框当前的location位置
		mPersonalShow.getLocationInWindow(leftTop);
		int left = leftTop[0];
		int top = leftTop[1];
		int bottom = top + mPersonalShow.getHeight();
		int right = left + mPersonalShow.getWidth();
		if (event.getX() > left && event.getX() < right && event.getY() > top && event.getY() < bottom) {
			if (imm != null && imm.isActive()) {
				imm.hideSoftInputFromWindow(mPersonalShow.getWindowToken(), 0);
				findViewById(R.id.action_question_edit_layout).setVisibility(View.GONE);
			}
		} 
		return super.dispatchTouchEvent(event);
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
					mBasicinfo = new UserBasic(mUserProfile.getBasic());
					
					if (!StringUtils.isEmpty(mBasicinfo.getRealname())) {
						mNickName.setText(mBasicinfo.getRealname());
					} else {
						if (!StringUtils.isEmpty(mBasicinfo.getNickname())) {
							mNickName.setText(mBasicinfo.getNickname());
						} else {
							mNickName.setText(this.getResources().getString(R.string.not_add_str));
						}
					}
					
					StringBuilder sb = new StringBuilder();
					boolean flag = false;
					if(StringUtils.isNotEmpty(mBasicinfo.getCity())){
						sb.append(mBasicinfo.getCity());
						flag = true;
					}
					if(StringUtils.isNotEmpty(mBasicinfo.getExpryear())){
						if(flag){
							sb.append(getString(R.string.general_tips_dot));
						}
						sb.append(mBasicinfo.getExpryear());
					}
					
					if(StringUtils.isNotEmpty(sb.toString())){
						mLocExpr.setText(sb.toString());
					}else{
						mLocExpr.setVisibility(View.GONE);
					}

					flag = false;
					sb.delete(0, sb.length());
					if(StringUtils.isNotEmpty(mBasicinfo.getTitle())){
						sb.append(mBasicinfo.getTitle());
						flag = true;
					}
					if(StringUtils.isNotEmpty(mBasicinfo.getCorp())){
						if(flag){
							sb.append(getString(R.string.general_tips_dot));
						}
						sb.append(mBasicinfo.getCorp());
					}
					
					if(StringUtils.isNotEmpty(sb.toString())){
						mPositionCorp.setText(sb.toString());
					}else{
						mPositionCorp.setVisibility(View.INVISIBLE);
					}
					//个人签名
					if(StringUtils.isNotEmpty(mBasicinfo.getIntroduce())){
						mPersonalSign.setText(mBasicinfo.getIntroduce());
					}
					//技能标签
					
					sb.delete(0, sb.length());
					if (mUserProfile.getSkills() != null && mUserProfile.getSkills().size() > 0) {
						mSkillsView.setVisibility(View.VISIBLE);
						
						String splite = getString(R.string.general_tips_spilte);
						for (int i = 0; i < mUserProfile.skills.size(); i++) {
							sb.append(mUserProfile.skills.getJSONObject(i).getString(GlobalConstants.JSON_NAME));
							sb.append(splite);
						}
						mSkillsView.setText(sb.substring(0, sb.length() - splite.length()));
						
					} else {
						mSkillsViewContainer.setVisibility(View.GONE);
					}
					
					// 加载在招职位
					mProfileInJobsData.clear();
					JSONArray jobs = mUserProfile.getJobs();
					if (jobs != null && !jobs.isEmpty()) {
						for (int i = 0; i < jobs.size(); i++) {
							JobDetailInfo detailInfo = new JobDetailInfo(jobs.getJSONObject(i));
							mProfileInJobsData.add(detailInfo);
						}
						mProfielInJobsAdatper.notifyDataSetChanged();
						mProfileInJobs.setVisibility(View.VISIBLE);
					} 

					// 加载真实头像　
					mAvatar.setDefaultImageResId(R.drawable.general_user_avatar);
					mAvatar.setImageUrl(mBasicinfo.getRealavatar(), mImageLoader);
					
					// 加载联系方式
					JSONObject contact = mUserProfile.getContact();
					if (contact != null && contact.getBoolean(GlobalConstants.JSON_PUBLIC)) {
						JSONObject data = contact.getJSONObject(GlobalConstants.JSON_DATA);
						if (data != null) {
							if (data.getString(GlobalConstants.JSON_MOBILE) != null) {
								mMobileNum = data.getString(GlobalConstants.JSON_MOBILE);
								mProfileContactMobile.setText(mMobileNum);
							} else {
								mProfileContactMobile.setVisibility(View.GONE);
							}
							if (data.getString(GlobalConstants.JSON_EMAIL) != null) {
								mUserEmail = data.getString(GlobalConstants.JSON_EMAIL);
								mProfileContactEmail.setText(mUserEmail);//mIsCandidate & !mIsReadEmail ? mUserEmail.substring(0, showLen) + sumStrs(mUserEmail.length() - showLen, "*") : mUserEmail);
							} else {
								mProfileContactEmail.setVisibility(View.GONE);
							}
							
							if(mIsCandidate){
//								mIsReadMobile = data.getInteger(GlobalConstants.JSON_HASREAD) != null && data.getInteger(GlobalConstants.JSON_HASREAD) != 0;
								mIsReadContacts = data.getInteger(GlobalConstants.JSON_HASREAD) != null && data.getInteger(GlobalConstants.JSON_HASREAD) != 0;
							}
							mProfileContactHide.setVisibility(View.GONE);
							
							if(mIsCandidate && !mIsReadContacts){ //(|| !mIsReadMobile)
								Logcat.d("test", "联系方式未读");
								mProfileContact.setVisibility(View.INVISIBLE);
								mProfileTipsLayout.setOnClickListener(this);
								mProfileTipsArrow.setVisibility(View.VISIBLE);
							}else{
								Logcat.d("test", "联系方式已读");
								mProfileTipsArrow.setVisibility(View.GONE);
								mProfileContact.setVisibility(View.VISIBLE);
							}
						}
					} else {
						mProfileContact.setVisibility(View.GONE);
						mProfileContactHide.setVisibility(View.VISIBLE);
					}
					// 加载工作经历
					if (mUserProfile.getExpr() != null && mUserProfile.getExpr().getBoolean(GlobalConstants.JSON_PUBLIC)) {
						JSONArray mExprArray = mUserProfile.getExpr().getJSONArray(GlobalConstants.JSON_DATA);
						if (mExprArray != null && !mExprArray.isEmpty()) {
							mExperiences.clear();
							
							for (int i = 0; i < mExprArray.size(); i++) {
								mExperiences.add(new Experience(mExprArray.getJSONObject(i)));
							}
							mJobAdapter.notifyDataSetChanged();
							mJobListView.setVisibility(View.VISIBLE);
							mHideJob.setVisibility(View.GONE);
						} else {
							mJobExpr.setVisibility(View.GONE);
						}
					} else {
						mJobListView.setVisibility(View.GONE);
						mHideJob.setVisibility(View.VISIBLE);
					}
					// 加载教育经历
					if (mUserProfile.getEdu() != null && mUserProfile.getEdu().getBoolean(GlobalConstants.JSON_PUBLIC)) {
						JSONArray mEduArray = mUserProfile.getEdu().getJSONArray(GlobalConstants.JSON_DATA);
						if (mEduArray != null && !mEduArray.isEmpty()) {
							mEducations.clear();
							for (int i = 0; i < mEduArray.size(); i++) {
								mEducations.add(new Education(mEduArray.getJSONObject(i)));
							}
							mEduAdapter.notifyDataSetChanged();
							mEduListView.setVisibility(View.VISIBLE);
							mHideEdu.setVisibility(View.GONE);
						} else {
							mEduExpr.setVisibility(View.GONE);
						}
					} else {
						mEduListView.setVisibility(View.GONE);
						mHideEdu.setVisibility(View.VISIBLE);
					}
					//设置显示名称 优先显示真实名称
					if(!StringUtils.isEmpty(mBasicinfo.getNickname())){
						username = mBasicinfo.getNickname();
					}
					if(!StringUtils.isEmpty(mBasicinfo.getRealname())){
						username = mBasicinfo.getRealname();
					}
				}

				//处理根据当前页面状态设置所要显示的控件
				if(!mIsCardReview && !mSelf){
					
					findViewById(R.id.other_profile_bottom).setVisibility(View.VISIBLE);
					if (mUserProfile.getIsbuddy() != null && mUserProfile.getIsbuddy() == 1) {
						findViewById(R.id.other_profile_buddy).setVisibility(View.VISIBLE);
						findViewById(R.id.other_profile_tab).setVisibility(View.GONE);
					} else {
						findViewById(R.id.other_profile_buddy).setVisibility(View.GONE);
						findViewById(R.id.other_profile_tab).setVisibility(View.VISIBLE);
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
						
						findViewById(R.id.other_profile_tab).setVisibility(View.GONE);
						findViewById(R.id.candidate_profile_tab).setVisibility(View.VISIBLE);
					}
				}
				
				// 访问网络请求成功，才显示整体的View
				if (mPersonalShow.getVisibility() != View.VISIBLE) {
					Animation loadAnimation = AnimationUtils.loadAnimation(this,R.anim.anim_fide_in_for_recruit);
					mPersonalShow.startAnimation(loadAnimation);
					mPersonalShow.setVisibility(View.VISIBLE);
					mLoading.setVisibility(View.INVISIBLE);
					refreshBgTitlr(topLayout);
				}
				break;
			case R.id.post_resume_real_apply:
				Intent intent = getIntent();
				if (infoResult.getMessage().getStatus() == 0) {
					intent.putExtra(GlobalConstants.JOB_HASAPPLIED, 1);
				}
				VToast.show(this, infoResult.getMessage().getMsg());
				setResult(RESULT_OK, intent);
				AppActivityManager.getInstance().finishActivity(PersonalShowPageActivity.this);
				// finish();
				break;
			case R.id.social_send_invitation:
				if(infoResult.getMessage().getStatus() == 0){
					
				}
				VToast.show(this, infoResult.getMessage().getMsg());
				break;
			case R.id.social_stranger_send_greeting:
				if(infoResult.getMessage().getStatus() == 0){
					
				}
				VToast.show(this, infoResult.getMessage().getMsg());
				break;
			case R.id.social_unbind_buddy:
				if(infoResult.getMessage().getStatus() == 0){
					// 先获得did
					mSocialEventLogic.createOrGetDialogId(1, String.valueOf(uid), 1);
				}
				VToast.show(this, infoResult.getMessage().getMsg());
				break;
			case R.id.social_stranger_send_question:
				if(infoResult.getMessage().getStatus() == 0){
					
				}
				VToast.show(this, infoResult.getMessage().getMsg());
				break;
				
			case R.id.social_get_dialog_id:
				JSONObject jsonObject = JSONObject.parseObject(infoResult.getResult());
				String did = jsonObject.getString(GlobalConstants.JSON_DID);
				
				if (!StringUtils.isEmpty(did)) {
					mProfileEventLogic.getProfileOhterInfo(uid, null);
					// 解除好友成功
					// 将消息列表中与此人的聊天记录删除
					ImDbManager.getInstance().cleanChatHistoryInMessageList(Long.valueOf(did));
				}
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
					
					if (!StringUtils.isEmpty(mBasicinfo.getRealname())) {
						intentChat.putExtra("dname", mBasicinfo.getRealname());
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
					Logcat.d("test",infoResult.getResult());
//					int complete = completeResult.getInteger(GlobalConstants.JSON_COMPLETE);
//					if (complete == 0) {
					JSONObject detail = completeResult.getJSONObject(GlobalConstants.JSON_DETAIL);
					if(detail != null){
						int work = detail.getInteger(GlobalConstants.JSON_WORKEXPR);
						if (work == 0) {
							showCompleteDialog();
							break;
						}
					}
//					}
					GeneralDbManager.getInstance().setWorkPerform(1);
					lowPressed();
				}
				break;
			default:
				break;
			}
		} else if (msg.obj instanceof VolleyError) {
			VToast.show(this, getString(R.string.general_tips_network_unknow));
		}
	}
	
	private void showCompleteDialog() {
		if(mAlertDialog != null && mAlertDialog.isShowing()) return;
		
		mAlertDialog = new AlertDialog.Builder(PersonalShowPageActivity.this)
		.setTitle(R.string.general_tips)
		.setMessage(R.string.complete_workexpr_info).setNegativeButton(R.string.general_tips_to_complete,new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog,int which) {
				Intent intent = getIntent();
				intent.setClass(PersonalShowPageActivity.this,PersonalEditPageActivity.class);
				startActivity(intent);
			}
		})
		.setPositiveButton(getString(R.string.btn_cancel),null).create();
		mAlertDialog.show();
	}
	
	private void completionQuery() {
		if(GeneralDbManager.getInstance().getWorkPerform() == 1){
			lowPressed();
		}else{
			mProfileEventLogic.cancel(R.id.post_resume_complete_status_get);
			mProfileEventLogic.getCompletion();
		}
	}
	
	private void lowPressed() {
		switch (mPressedId) {
		case R.id.action_question:
			findViewById(R.id.action_question_edit_layout).setVisibility(View.VISIBLE);
			findViewById(R.id.action_question_edit_layout).requestFocus();
			findViewById(R.id.action_question_edit_layout).setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
				}
			});
			mActionQuestionEdit.requestLayout();
			mActionQuestionEdit.setHint(getString(R.string.action_question_tips,username));
			mActionQuestionEdit.addTextChangedListener(new TextWatcher() {
				
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
					if(s.length() > 0){
						mQuestionSender.setEnabled(true);
					}else{
						mQuestionSender.setEnabled(false);
					}
				}
				
				@Override
				public void beforeTextChanged(CharSequence s, int start, int count,
						int after) {
					
				}
				
				@Override
				public void afterTextChanged(Editable s) {
					
				}
			});
			InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
			if (imm != null) {
				imm.showSoftInput(mActionQuestionEdit, 0);
			}
			break;
		case R.id.action_chat2:
		case R.id.action_chat:
			if(StringUtils.isEmpty(uid)){
				return;
			}
			long id = Long.parseLong(uid);
			
			Intent chat = getIntent();
			chat.setClass(this, ChatPersonActivity.class);
			chat.putExtra("sender", id);
			chat.putExtra("receiverIsreal", 1);
			if (!StringUtils.isEmpty(mBasicinfo.getRealname())) {
				chat.putExtra("dname", mBasicinfo.getRealname());
			}
			startActivity(chat);
			break;
		case R.id.action_greet:
			mSocialEventLogic.cancel(R.id.social_stranger_send_greeting);
			mSocialEventLogic.sendGreeting(uid, null);
			break;
		case R.id.action_friend:
			mBuilder.setTitle(R.string.general_tips).setMessage(R.string.action_friend_tips);
			mBuilder.setNegativeButton(R.string.btn_cancel, null);
			mBuilder.setPositiveButton(R.string.btn_confirm, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					mSocialEventLogic.cancel(R.id.social_send_invitation);
					mSocialEventLogic.sendInvitation(uid);
				}
			});
			mBuilder.show();
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
			MobclickAgent.onEvent(PersonalShowPageActivity.this, "dzh");
			mPressedId = R.id.action_greet;
			completionQuery();
			break;
		case R.id.action_question:
			MobclickAgent.onEvent(PersonalShowPageActivity.this, "tw");
			mPressedId = R.id.action_question;
			completionQuery();
			break;
		case R.id.action_friend:
			MobclickAgent.onEvent(PersonalShowPageActivity.this, "jhy");
			mPressedId = R.id.action_friend;
			completionQuery();
			break;
		case R.id.action_chat:
		case R.id.action_chat2:
			mPressedId = R.id.action_chat;
			completionQuery();
			break;
		case R.id.action_report_another:
		case R.id.action_report:
			MobclickAgent.onEvent(PersonalShowPageActivity.this, "jb");
			Intent intentOfficier = new Intent(PersonalShowPageActivity.this, OfficierActivity.class);
			if (!StringUtils.isEmpty(uid)) {
				intentOfficier.putExtra("report_id", uid);
			}
			startActivity(intentOfficier);
			break;
		case R.id.action_unbind:
			mBuilder.setTitle(R.string.general_tips).setMessage(getString(R.string.action_unbind_tips,username));
			mBuilder.setNegativeButton(R.string.btn_cancel, null);
			mBuilder.setPositiveButton(R.string.btn_confirm, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					mSocialEventLogic.cancel(R.id.social_send_invitation);
					mSocialEventLogic.unbindBuddy(uid);
				}
			});
			mBuilder.show();
			break;
		case R.id.action_have_a_chat:
			// 执行聊一聊操作
			mSocialEventLogic.candidateChat(getIntent().getStringExtra(GlobalConstants.JOB_ID), getIntent().getStringExtra(GlobalConstants.UID), getIntent().getIntExtra(GlobalConstants.ISREAL, 0));
			break;
		case R.id.menu_bar_right:
			Intent data = getIntent();
			int jobId = data.getIntExtra(GlobalConstants.JOB_ID, -1);
			String oneword = data.getStringExtra(GlobalConstants.JOB_ONEWORD);
			MobclickAgent.onEvent(PersonalShowPageActivity.this, "tdjlsm");
			mPositionEventLogic.cancel(R.id.post_resume_real_apply);
			mPositionEventLogic.applyRealFile(jobId + "", oneword);
			
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
		case R.id.other_profile_contacts_layout://查看电话联系方式
			//设置服务器端 该简历联系方式已读
			mProfileEventLogic.readContact(uid, jid);
			//设置不可点击
			mProfileTipsLayout.setOnClickListener(null);
			//显示联系方式
			mProfileContact.setVisibility(View.VISIBLE);
			//隐藏箭头
			mProfileTipsArrow.setVisibility(View.GONE);
			break;
		default:
			break;
		}
	}

	@Override
	public void onBackPressed() {
		if(findViewById(R.id.action_question_edit_layout).getVisibility() == View.VISIBLE){
			findViewById(R.id.action_question_edit_layout).setVisibility(View.GONE);
			mActionQuestionEdit.setText("");
			return;
		}
		super.onBackPressed();
	}
	@Override
	protected void onResume() {
		super.onResume();
		if(mIsCandidate){
			mProfileEventLogic.getProfileOhterInfo(uid, jid);
		}else{
			mProfileEventLogic.getProfileOhterInfo(uid, null);
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
	
	@Override
	protected void onPause() {
		super.onPause();
	}




	int alpha = 0;
	@Override
	public void onScrollChanged() {
		// 当profile滚动到 banner完全消失了 就会让menubar从透明变为不透明
		float a = mPersonalShow.getScrollY() / (float) (topLayout.getHeight() - mMenuBar.getHeight()); 
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
}
