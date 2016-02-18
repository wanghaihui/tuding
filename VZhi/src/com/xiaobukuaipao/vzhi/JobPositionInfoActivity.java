package com.xiaobukuaipao.vzhi;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.CustomShareBoard;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.media.SinaShareContent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.UMSsoHandler;
import com.umeng.socialize.weixin.media.CircleShareContent;
import com.umeng.socialize.weixin.media.WeiXinShareContent;
import com.xiaobukuaipao.vzhi.cache.CommonBitmapMemoryCache;
import com.xiaobukuaipao.vzhi.domain.CorpInfo;
import com.xiaobukuaipao.vzhi.domain.JobDetailInfo;
import com.xiaobukuaipao.vzhi.domain.PublisherInfo;
import com.xiaobukuaipao.vzhi.domain.user.BasicInfo;
import com.xiaobukuaipao.vzhi.event.ApiConstants;
import com.xiaobukuaipao.vzhi.event.InfoResult;
import com.xiaobukuaipao.vzhi.im.ImDbManager;
import com.xiaobukuaipao.vzhi.manager.GeneralDbManager;
import com.xiaobukuaipao.vzhi.profile.ProfileCompleteActivity;
import com.xiaobukuaipao.vzhi.profile.ProfileSkillActivity;
import com.xiaobukuaipao.vzhi.profile.eduexp.EducationListActivity;
import com.xiaobukuaipao.vzhi.profile.jobexp.JobExperienceListActivity;
import com.xiaobukuaipao.vzhi.util.ActivityQueue;
import com.xiaobukuaipao.vzhi.util.GlobalConstants;
import com.xiaobukuaipao.vzhi.util.SharedPreferencesUtil;
import com.xiaobukuaipao.vzhi.util.SpannableKeyWordBuilder;
import com.xiaobukuaipao.vzhi.util.StringUtils;
import com.xiaobukuaipao.vzhi.util.VToast;
import com.xiaobukuaipao.vzhi.view.LinesTextView;
import com.xiaobukuaipao.vzhi.view.LoadingDialog;
import com.xiaobukuaipao.vzhi.view.RoundedNetworkImageView;
import com.xiaobukuaipao.vzhi.wrap.RecruitWrapActivity;

/**
 * 职位详细信息<br>
 * 将职位的重点信息展示出来,包括对求职者的要求,给予的福利待遇,以及该职位的特点,另外再本页负责投递逻辑,以及职位分享的逻辑
 * 
 * @since 2015年01月05日11:55:48
 */
public class JobPositionInfoActivity extends RecruitWrapActivity implements OnClickListener, OnCheckedChangeListener {
	/**
	 * 公司的logo
	 */
	private RoundedNetworkImageView mCorpLogoView;
	/**
	 * 职位名称
	 */
	private TextView mRecruitPosition;
	/**
	 * 查看人数
	 */
	private TextView mViewNum;
	/**
	 * 投递人数
	 */
	private TextView mApplyNum;
	/**
	 * hr已读人数, 已读人数必然小于等于投递人数,否则是垃圾数据
	 */
	private TextView mReadNum;
	/**
	 * 职位发布或者是刷新时间
	 */
	private TextView mRefreshTime;
	/**
	 * 职位薪水待遇
	 */
	private TextView mPositionSalary;
	/**
	 * 职位所在城市
	 */
	private TextView mPositionCity;
	/**
	 * 职位教育要求
	 */
	private TextView mPositionEdu;
	/**
	 * 职位经验要求
	 */
	private TextView mPositionExpr;
	/**
	 * 职位招聘的人数
	 */
	private TextView mPositionNum; 
	/**
	 * 职位的诱惑
	 */
	private TextView mPositionHighlights;
	/**
	 * 公司名称
	 */
	private TextView mRecruitCorp;
	/**
	 * 职位描述,岗位职责等等
	 */
	private TextView mPositionDesc;
	/**
	 * 公司信息
	 */
	private CorpInfo mCorpInfo;
	/**
	 * 发布者信息
	 */
	private PublisherInfo mPublisherInfo;
	/**
	 * 职位详细信息
	 */
	private JobDetailInfo mJobDetailInfo;
	/**
	 * Volley提供的请求队列
	 */
	private RequestQueue mQueue;
	private ImageLoader mImageLoader;
	/**
	 * 福利标签
	 */
	private LinesTextView mBenefits;
	/**
	 * 投递弹出的对话框
	 */
	private PopupWindow mPopupWindow;
	/**
	 * 投递对话框容器
	 */
	private LinearLayout mPopupLayout;
	/**
	 * 是否记住一句话推荐自己的checkbox
	 */
	private CheckBox mPostRemmber;
	/**
	 * 是否要预览投递简历的checkbox
	 */
	private CheckBox mPostConfirm;

	private EditText mPostOneWord;
	/**
	 * 真实头像,投递真实简历
	 */
	private RoundedNetworkImageView mPostRealHead;
	/**
	 * 匿名头像,投递匿名简历
	 */
	private RoundedNetworkImageView mPostAvatar;
	/**
	 * 是否要预览投递简历
	 */
	private boolean reviewFile;
	/**
	 * 一句话推荐自己
	 */
	private String oneWord ;
	/**
	 *  0 未投递 1已投递
	 */
	private int hasApplied = 0;
	/**
	 * 职位id,不可能为空
	 */
	private int jobId = -1;
	/**
	 * 获取uid,用于给一句话推荐的本地存储做编号
	 */
	private String uid;
	/**
	 * 投递简历父容器
	 */
	private View btnPostResume;
	/**
	 * 投递简历按钮图片,用于置为已投递
	 */
	private ImageView btnPostResumeImg;
	/**
	 * 投递简历按钮
	 */
	private TextView btnPostResumeTv;
	/**
	 * 福利
	 */
	private List<String> mBenefitsList;
	/**
	 * 总体容器
	 */
	private View mLayout;
	/**
	 * 判断一下是否是自己发布的职位,跟本地对比
	 */
	private boolean isMe;
	/**
	 * loading对话框
	 */
	private LoadingDialog loadingDialog;
	/**
	 * 自定义的分享面板
	 */
	private CustomShareBoard customShareBoard;
	/**
	 * 获取系统中分享服务实例
	 */
	private UMSocialService mController = UMServiceFactory.getUMSocialService("com.xiaobukuaipao.vzhi");
	/**
	 * 是否完善信息的提示对话框
	 */
	private AlertDialog mAlertDialog;
	private boolean isChecked;
	
	public void initUIAndData() {
		setContentView(R.layout.activity_jobposition_info);

		setTheme(R.style.VTheme_Stantard);
		
		setHeaderMenuByLeft(this);
		setHeaderMenuByCenterTitle(R.string.title_jobposition_info);
		
		uid = GeneralDbManager.getInstance().getUidFromCookie();
		jobId = getIntent().getIntExtra(GlobalConstants.JOB_ID, jobId);
		
		mLayout = findViewById(R.id.layout);
		mLayout.setVisibility(View.GONE);
		
		mPopupLayout = (LinearLayout) View.inflate(this,R.layout.popup_post_resume, null);
		mPopupWindow = new PopupWindow();
		mPopupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		mPopupWindow.setBackgroundDrawable(new ColorDrawable(0));
		mPopupWindow.setClippingEnabled(true);
		mPopupWindow.setTouchInterceptor(new OnTouchListener() {
			
			@SuppressLint("ClickableViewAccessibility")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
					if (mPopupWindow.isShowing())
						mPopupWindow.dismiss();
					return true;
				}
				return false;
			}
		});

		mPopupWindow.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				WindowManager.LayoutParams lp = getWindow().getAttributes();
				lp.alpha = 1.0f;
				lp.dimAmount = 0.65f;
				getWindow().setAttributes(lp);
			}
		});
		// 福利标签
		mBenefitsList = new ArrayList<String>();
		
		mPostRemmber = (CheckBox) mPopupLayout.findViewById(R.id.popup_post_resume_remember);
		mPostConfirm = (CheckBox) mPopupLayout.findViewById(R.id.popup_post_resume_confirm);
		
		mPostOneWord = (EditText) mPopupLayout.findViewById(R.id.popup_post_resume_one_word);
		mPostRealHead = (RoundedNetworkImageView) mPopupLayout.findViewById(R.id.popup_post_resume_realhead_img);
		mPostAvatar = (RoundedNetworkImageView) mPopupLayout.findViewById(R.id.popup_post_resume_avatar_img);
		mCorpLogoView = (RoundedNetworkImageView) findViewById(R.id.logo_corp);
		mRecruitPosition = (TextView) findViewById(R.id.recruit_position);
		mRecruitCorp = (TextView) findViewById(R.id.recruit_corp);
		mViewNum = (TextView) findViewById(R.id.view_num);
		mApplyNum = (TextView) findViewById(R.id.apply_num);
		mReadNum = (TextView) findViewById(R.id.read_num);
		mRefreshTime = (TextView) findViewById(R.id.refresh_time);
		mBenefits = (LinesTextView) findViewById(R.id.company_benefits_tv);
		mPositionSalary = (TextView) findViewById(R.id.position_salary);
		mPositionCity = (TextView) findViewById(R.id.position_city);
		mPositionEdu = (TextView) findViewById(R.id.position_edu);
		mPositionExpr = (TextView) findViewById(R.id.position_expr);
		mPositionNum = (TextView) findViewById(R.id.position_headcount);
		mPositionHighlights = (TextView) findViewById(R.id.position_highlights);
		mPositionDesc = (TextView) findViewById(R.id.position_desc);
		//投递按钮
		btnPostResume = findViewById(R.id.post_resume);
		btnPostResumeImg = (ImageView) findViewById(R.id.post_resume_img);
		btnPostResumeTv = (TextView) findViewById(R.id.post_resume_tv);
		
		mQueue = Volley.newRequestQueue(this);
		mImageLoader = new ImageLoader(mQueue, new CommonBitmapMemoryCache());
		
		mPostRemmber.setOnClickListener(this);
		mPostRemmber.setOnCheckedChangeListener(this);
		mPostConfirm.setOnCheckedChangeListener(this);
		
		mPopupLayout.findViewById(R.id.popup_post_resume_avatar).setOnClickListener(this);
		mPopupLayout.findViewById(R.id.popup_post_resume_realhead).setOnClickListener(this);
		//分享按钮
		findViewById(R.id.post_share).setOnClickListener(this);
		btnPostResume.setOnClickListener(this);

		loadingDialog = new LoadingDialog(this);
		
		mProfileEventLogic.getBasicinfo();
		mPostOneWord.addTextChangedListener(new TextWatcher() {
			
			private void subS(CharSequence s){
				if(s.toString().getBytes().length > 100){
					subS(s.subSequence(0, s.length() - 1));	
				}else{
					mPostOneWord.setText(s);
					mPostOneWord.setSelection(s.length());
				}
			}
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				int max = 100;
				if(s.toString().getBytes().length > max){
					subS(s);
				}
				
				SharedPreferencesUtil.getInstance().putString(mPostRemmber.isChecked() ? s.toString() : "", uid + GlobalConstants.PREFERENCE_APPLY_ONE_WORD);
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				
			}
		});
		mAlertDialog = new AlertDialog.Builder(this).create();
		// 预览简历选项默认第一次进来默认勾选
		reviewFile = SharedPreferencesUtil.getInstance().getSettingMessage(uid +GlobalConstants.PREFERENCE_APPLY_REVIEW);
		mPostConfirm.setChecked(reviewFile);
		// 拉取职位信息
		if(jobId != -1) mPositionEventLogic.getPositionInfo(String.valueOf(jobId));
	}

	@Override
	protected void onResume() {
		super.onResume();
		if(ActivityQueue.isExist() && ActivityQueue.isTail()){//从前边的页面返回的
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					showPopupWindow();
				}
			}, 300);
			ActivityQueue.destroy();
		}
	}
	
	public void onEventMainThread(Message msg) {
		
		if (msg.obj instanceof InfoResult) {
			InfoResult infoResult = (InfoResult) msg.obj;
			switch (msg.what) {
			case R.id.position_detail:
				// 将返回的JSON数据展示出来
				JSONObject positionResult = (JSONObject) JSONObject.parse(infoResult.getResult());
				if(positionResult != null){
					// 拆分公司信息,发布者信息,以及职位详情信息
					mCorpInfo = new CorpInfo(positionResult.getJSONObject(GlobalConstants.JSON_CORP));
					mPublisherInfo = new PublisherInfo(positionResult.getJSONObject(GlobalConstants.JSON_PUBLISHER));
					mJobDetailInfo = new JobDetailInfo(positionResult.getJSONObject(GlobalConstants.JSON_JOB));
					
					//设置公司logo
					mCorpLogoView.setDefaultImageResId(R.drawable.default_corp_logo);
					mCorpLogoView.setImageUrl(mCorpInfo.getLogo(), mImageLoader);
					mCorpLogoView.setOnClickListener(new OnCorpClick(mCorpInfo.getId()));
					
					//设置职位名称
					mRecruitPosition.setText(mJobDetailInfo.getPositionName());
					mRecruitCorp.setText(mCorpInfo.getName());
					
					SpannableKeyWordBuilder builderComp = new SpannableKeyWordBuilder();
					builderComp.setMode(SpannableKeyWordBuilder.MODE_NUMBER);
					builderComp.append(getString(R.string.view_num_str,mJobDetailInfo.getViewnum()));
					mViewNum.setText(builderComp.build());
					builderComp.delete(0, builderComp.length());
					
					builderComp.append(getString(R.string.apply_num_str,mJobDetailInfo.getApplynum()));
					mApplyNum.setText(builderComp.build());
					builderComp.delete(0, builderComp.length());
					builderComp.append(getString(R.string.read_num_str,mJobDetailInfo.getReadnum()));
					mReadNum.setText(builderComp.build());
	
					mRefreshTime.setText(mJobDetailInfo.refreshtime + "发布");
					
					mBenefitsList.clear();
					if(mCorpInfo.getBenefits() != null){
						for (int i = 0; i < mCorpInfo.getBenefits().size(); i++) {
							mBenefitsList.add(mCorpInfo.getBenefits().getJSONObject(i).getString(GlobalConstants.JSON_NAME));
						}
						mBenefits.setTextColor(getResources().getColor(R.color.white));
						mBenefits.setBackgroundResource(R.drawable.general_benefit_bg);
						mBenefits.setLinesText(mBenefitsList);
					}
					
					builderComp.setMode(SpannableKeyWordBuilder.MODE_KEYWORD);
					builderComp.delete(0, builderComp.length());
					builderComp.appendKeyWord(mJobDetailInfo.getSalary());
					mPositionSalary.setText(builderComp.build());
					
					mPositionCity.setText(mJobDetailInfo.getCity());
					mPositionEdu.setText(mJobDetailInfo.getEduName());
					
					builderComp.delete(0, builderComp.length());
					builderComp.append(mJobDetailInfo.getExprName());
					mPositionExpr.setText(builderComp.build());
	
					builderComp.delete(0, builderComp.length());
					builderComp.append(getString(R.string.head_count,mJobDetailInfo.getHeadcount()));
					mPositionNum.setText(builderComp.build());
					
					//职位诱惑
					if(StringUtils.isNotEmpty(mJobDetailInfo.highlights)){
						mPositionHighlights.setText(getResources().getString(R.string.general_highlights_str)+ ":"+ mJobDetailInfo.highlights);
					}else{
						mPositionHighlights.setVisibility(View.GONE);
					}
					//职位描述
					if(StringUtils.isNotEmpty(mJobDetailInfo.getDesc())){
						mPositionDesc.setText(mJobDetailInfo.getDesc());
					}else{
						mPositionDesc.setVisibility(View.GONE);
					}
					
					// 当前信息是否已经投递过
					if(positionResult.getInteger(GlobalConstants.JSON_HASAPPLIED) != null){
						hasApplied = positionResult.getInteger(GlobalConstants.JSON_HASAPPLIED);
					}
					
					//已投递
					if (hasApplied == 1) {
						btnPostResume.setEnabled(false);
						btnPostResumeImg.setImageResource(R.drawable.post_resume_grey);
						btnPostResumeTv.setText(getString(R.string.post_resume_alpost));
						btnPostResumeTv.setTextColor(getResources().getColor(R.color.font_gray));
					}else{
						btnPostResume.setEnabled(true);
						btnPostResumeImg.setImageResource(R.drawable.post_resume);
						btnPostResumeTv.setText(getString(R.string.post_resume));
						btnPostResumeTv.setTextColor(getResources().getColor(R.color.base_interface_color));
					}
					// 判断访问当前的职位是否是自己发布的
					isMe = mPublisherInfo.getId().equals(String.valueOf(ImDbManager.getInstance().getUid(this)));
				}
				// 访问网络请求成功，才显示整体的View
				if (mLayout.getVisibility() != View.VISIBLE) {
					Animation loadAnimation = AnimationUtils.loadAnimation(this,R.anim.anim_fide_in_for_recruit);
					mLayout.startAnimation(loadAnimation);
					mLayout.setVisibility(View.VISIBLE);
					findViewById(R.id.bottom_bar).startAnimation(loadAnimation);
					findViewById(R.id.bottom_bar).setVisibility(View.VISIBLE);
				}
					
				break;
			case R.id.register_get_basicinfo:
				JSONObject basicinfo = (JSONObject) JSONObject.parseObject(infoResult.getResult());
				if (basicinfo != null) {
					BasicInfo basicInfo = new BasicInfo(basicinfo.getJSONObject(GlobalConstants.JSON_USERBASIC));
					// 初始化投递者的头像
					mPostRealHead.setDefaultImageResId(R.drawable.general_user_avatar);
					mPostRealHead.setImageUrl(basicInfo.getRealAvatar(),mImageLoader);
					mPostRealHead.setBorderColor(getResources().getColor(basicInfo.getGender() == 1 ? R.color.boy_border_color : R.color.girl_border_color));
					
					mPostAvatar.setDefaultImageResId(R.drawable.general_default_ano);
					mPostAvatar.setImageUrl(basicInfo.getAvatar(), mImageLoader);
					mPostAvatar.setBorderColor(getResources().getColor(basicInfo.getGender() == 1 ? R.color.boy_border_color : R.color.girl_border_color));
				}
				break;
			case R.id.post_resume_complete_status_get:
				JSONObject completeResult = (JSONObject) JSONObject.parse(infoResult.getResult());
				if(completeResult != null){
					int complete = completeResult.getInteger(GlobalConstants.JSON_COMPLETE);
					ActivityQueue.destroy();
					ActivityQueue.buildQueue();
					if (complete == 0) {
						JSONObject detail = completeResult.getJSONObject(GlobalConstants.JSON_DETAIL);
						int basic = detail.getInteger(GlobalConstants.JSON_BASIC);
						int edu = detail.getInteger(GlobalConstants.JSON_EDUEXPR);
						int work = detail.getInteger(GlobalConstants.JSON_WORKEXPR);
						
						if (basic == 0) {
							ActivityQueue.push(ProfileCompleteActivity.class);
						}
						if (work == 0) {
							ActivityQueue.push(JobExperienceListActivity.class);
						}
						if (edu == 0) {
							ActivityQueue.push(EducationListActivity.class);
						}
						ActivityQueue.push(ProfileSkillActivity.class);
						ActivityQueue.push(JobPositionInfoActivity.class);
						
					}
					if (!ActivityQueue.isEmpty()) {// 存在了要去完善的信息,构建状态机
						if(!mAlertDialog.isShowing()){
							mAlertDialog = new AlertDialog.Builder(JobPositionInfoActivity.this)
							.setTitle(R.string.general_tips)
							.setMessage(R.string.complete_personal_info).setNegativeButton(R.string.general_tips_to_complete,new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,int which) {
									Intent intent = getIntent();
									intent.putExtra("personal_experience_complete", true);
									intent.putExtra("personal_education_complete", true);
									intent.setClass(JobPositionInfoActivity.this,ActivityQueue.getFirst());
									startActivity(intent);
								}
							})
							.setPositiveButton(getString(R.string.btn_cancel),null).create();
							mAlertDialog.show();
						}
					} else {
						showPopupWindow();
					}
				}
				break;
			case R.id.post_resume_real_apply:
				if (infoResult.getMessage().getStatus() == 0) {
					hasApplied = 1;
				}
				if (hasApplied == 1) {//已投递
					btnPostResume.setEnabled(false);
					btnPostResumeImg.setImageResource(R.drawable.post_resume_grey);
					btnPostResumeTv.setText(getString(R.string.post_resume_alpost));
					btnPostResumeTv.setTextColor(getResources().getColor(R.color.font_gray));
				}else{
					btnPostResume.setEnabled(true);
					btnPostResumeImg.setImageResource(R.drawable.post_resume);
					btnPostResumeTv.setText(getString(R.string.post_resume));
					btnPostResumeTv.setTextColor(getResources().getColor(R.color.general_color_blue2));
				}
				VToast.show(this, infoResult.getMessage().getMsg());
				if (mPopupWindow.isShowing()){
					mPopupWindow.dismiss();
				}
				if(loadingDialog.isShowing()){
					loadingDialog.dismiss();
				}
				break;
			case R.id.post_resume_nick_apply:
				if (infoResult.getMessage().getStatus() == 0) {
					hasApplied = 1;
				}
				if (hasApplied == 1) {//已投递
					btnPostResume.setEnabled(false);
					btnPostResumeImg.setImageResource(R.drawable.post_resume_grey);
					btnPostResumeTv.setText(getString(R.string.post_resume_alpost));
					btnPostResumeTv.setTextColor(getResources().getColor(R.color.font_gray));
				}else{
					btnPostResume.setEnabled(true);
					btnPostResumeImg.setImageResource(R.drawable.post_resume);
					btnPostResumeTv.setText(getString(R.string.post_resume));
					btnPostResumeTv.setTextColor(getResources().getColor(R.color.general_color_blue2));
				}
				VToast.show(this, infoResult.getMessage().getMsg());
				if (mPopupWindow.isShowing()){
					mPopupWindow.dismiss();
				}
				
				if(loadingDialog.isShowing()){
					loadingDialog.dismiss();
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

	private void showPopupWindow() {
		mPopupWindow.setAnimationStyle(android.R.style.Animation_InputMethod);
		mPopupWindow.setWidth(LayoutParams.MATCH_PARENT);
		mPopupWindow.setHeight(LayoutParams.WRAP_CONTENT);
		mPopupWindow.setContentView(mPopupLayout);
		mPopupWindow.setOutsideTouchable(true);
		mPopupWindow.setFocusable(true);
		WindowManager.LayoutParams lp = getWindow().getAttributes();
		lp.alpha = 0.65f;
		getWindow().setAttributes(lp);
		mPopupWindow.showAtLocation(getWindow().getDecorView().getRootView(), Gravity.BOTTOM, 0, 0);
		oneWord = SharedPreferencesUtil.getInstance().getString(uid + GlobalConstants.PREFERENCE_APPLY_ONE_WORD);
		isChecked = SharedPreferencesUtil.getInstance().getSettingMessage(uid + GlobalConstants.PREFERENCE_APPLY_ONE_WORD_CHECK);
		mPostRemmber.setChecked(isChecked);
		mPostOneWord.setText(mPostRemmber.isChecked() ? oneWord : "");
		
	}


	@Override
	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
		case R.id.post_resume:
			if(isMe){
				VToast.show(this, getString(R.string.general_tips_isme_postresume));
				return;
			}
			MobclickAgent.onEvent(JobPositionInfoActivity.this, "tdjl");
			mProfileEventLogic.cancel(R.id.post_resume_complete_status_get);
			mProfileEventLogic.getCompletion();
			break;
		case R.id.post_share:
			
			MobclickAgent.onEvent(JobPositionInfoActivity.this, "fxzw");
			// 使用友盟分享职位信息
			UMImage localImage = new UMImage(this, R.drawable.ic_launcher);
			
			String url = ApiConstants.JOBINFO + mJobDetailInfo.getId();
			
			// 微信分享
			WeiXinShareContent weixinContent = new WeiXinShareContent();
			weixinContent.setTitle(mJobDetailInfo.getPosition().getString(GlobalConstants.JSON_NAME));
			weixinContent.setShareContent(mJobDetailInfo.getHighlights());
			weixinContent.setTargetUrl(url);
			
			weixinContent.setShareImage(localImage);
			mController.setShareMedia(weixinContent);

			// 微信朋友圈分享
			CircleShareContent circleMedia = new CircleShareContent();
			circleMedia.setShareContent(mJobDetailInfo.getHighlights());
			circleMedia.setTitle(mJobDetailInfo.getPosition().getString(GlobalConstants.JSON_NAME));
			circleMedia.setShareImage(localImage);
			circleMedia.setTargetUrl(url);
			mController.setShareMedia(circleMedia);
			
			// 新浪分享
			SinaShareContent sinaShareContent = new SinaShareContent();
			sinaShareContent.setShareContent(mJobDetailInfo.getHighlights());
			sinaShareContent.setTitle(mJobDetailInfo.getPosition().getString(GlobalConstants.JSON_NAME));
			sinaShareContent.setShareImage(localImage);
			sinaShareContent.setTargetUrl(url);
			sinaShareContent.toUrlExtraParams();
			sinaShareContent.setAppWebSite(url);
			
			mController.setShareMedia(sinaShareContent);
			
			customShareBoard = new CustomShareBoard(JobPositionInfoActivity.this);
			customShareBoard.setOnDismissListener(new OnDismissListener() {
				@Override
				public void onDismiss() {
					WindowManager.LayoutParams lp = getWindow().getAttributes();
					lp.alpha = 1.0f;
					getWindow().setAttributes(lp);
				}
			});
			customShareBoard.showAtLocation(getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);
			WindowManager.LayoutParams lp = getWindow().getAttributes();
			lp.alpha =0.65f;
			getWindow().setAttributes(lp);
			break;
		case R.id.popup_post_resume_realhead:
			
			//真实投递
			if(reviewFile){
				intent = getIntent();
				//传递显示预览界面的标志
				intent.putExtra(GlobalConstants.JOB_REVIEW, true);
				intent.putExtra(GlobalConstants.JOB_ID, jobId);
				intent.putExtra(GlobalConstants.JOB_ONEWORD, mPostOneWord.getText().toString());
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.setClass(this, PersonalShowPageActivity.class);
				startActivityForResult(intent,101);
			}else{
				
				MobclickAgent.onEvent(JobPositionInfoActivity.this, "tdjlsm");
				oneWord = mPostOneWord.getText().toString();
				mPositionEventLogic.cancel(R.id.post_resume_real_apply);
				mPositionEventLogic.applyRealFile(mJobDetailInfo.getId(), oneWord);
				loadingDialog.show();
			}
			break;
		case R.id.popup_post_resume_avatar:
			//匿名投递
			if (reviewFile) {
				intent = getIntent();
				//传递显示预览界面的标志
				intent.putExtra(GlobalConstants.JOB_ONEWORD, mPostOneWord.getText().toString());
				intent.putExtra(GlobalConstants.JOB_ID, jobId);
				intent.putExtra(GlobalConstants.JOB_REVIEW, true);
				intent.setClass(this, AnoPersonInfoActivity.class);
				startActivityForResult(intent,101);
			} else {
				
				MobclickAgent.onEvent(JobPositionInfoActivity.this, "tdjlnm");
				oneWord = mPostOneWord.getText().toString();
				mPositionEventLogic.cancel(R.id.post_resume_nick_apply);
				mPositionEventLogic.applyNickFile(mJobDetailInfo.getId(), oneWord);
				loadingDialog.show();
			}
			break;
		case R.id.popup_post_resume_remember://记住一句话
			SharedPreferencesUtil.getInstance().putString(mPostRemmber.isChecked() ? mPostOneWord.getText().toString() : "", uid + GlobalConstants.PREFERENCE_APPLY_ONE_WORD);
			break;
		default:
			break;
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	    /**使用SSO授权必须添加如下代码 */
		UMSsoHandler ssoHandler = mController.getConfig().getSsoHandler(requestCode);
	    if(ssoHandler != null){
	       ssoHandler.authorizeCallBack(requestCode, resultCode, data);
	    }
		if (data != null){
			if (resultCode == RESULT_OK) {
				if (requestCode == 101) {
					hasApplied = data.getIntExtra(GlobalConstants.JOB_HASAPPLIED, -1);
					if(hasApplied == 1){//已投递
						btnPostResume.setEnabled(false);
						btnPostResumeImg.setImageResource(R.drawable.post_resume_grey);
						btnPostResumeTv.setText(getString(R.string.post_resume_alpost));
						btnPostResumeTv.setTextColor(getResources().getColor(R.color.font_gray));
					}else{
						btnPostResume.setEnabled(true);
						btnPostResumeImg.setImageResource(R.drawable.post_resume);
						btnPostResumeTv.setText(getString(R.string.post_resume));
						btnPostResumeTv.setTextColor(getResources().getColor(R.color.general_color_blue2));
					}
				}
			}
		}
	}
	
	@Override
	protected void onPause() {
		if (mPopupWindow.isShowing()){
			mPopupWindow.dismiss();
		}
		super.onPause();
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		switch (buttonView.getId()) {
		case R.id.popup_post_resume_confirm://投递预览
			reviewFile = isChecked;
			SharedPreferencesUtil.getInstance().putSettingMessage(reviewFile, uid +GlobalConstants.PREFERENCE_APPLY_REVIEW);
			break;
		case R.id.popup_post_resume_remember:
			SharedPreferencesUtil.getInstance().putSettingMessage(isChecked, uid + GlobalConstants.PREFERENCE_APPLY_ONE_WORD_CHECK);
			SharedPreferencesUtil.getInstance().putString(isChecked ? mPostOneWord.getText().toString() : "", uid + GlobalConstants.PREFERENCE_APPLY_ONE_WORD);
			break;
		default:
			break;
		}
	}
	
	class OnCorpClick implements OnClickListener{

		String id;
		public OnCorpClick(String id){
			this.id = id; 
		}
		
		@Override
		public void onClick(View v) {
			if(GeneralDbManager.getInstance().isExistCookie()){
				Intent intent = new Intent();
				intent.putExtra(GlobalConstants.CORP_ID, id);
				intent.setClass(JobPositionInfoActivity.this, CorpInfoActivity.class);
				startActivity(intent);
			}else{
				VToast.show(JobPositionInfoActivity.this, getString(R.string.general_tips_default_unlogin));
			}
		}
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}
}
