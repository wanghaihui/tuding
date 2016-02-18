package com.xiaobukuaipao.vzhi;

import java.util.ArrayList;
import java.util.List;

import android.R.anim;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.ViewTreeObserver.OnScrollChangedListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.android.volley.toolbox.Volley;
import com.umeng.analytics.MobclickAgent;
import com.xiaobukuaipao.vzhi.adapter.EducationsAdapter;
import com.xiaobukuaipao.vzhi.adapter.ExperiencesAdapter;
import com.xiaobukuaipao.vzhi.contentprovider.GeneralContentProvider;
import com.xiaobukuaipao.vzhi.database.CookieTable;
import com.xiaobukuaipao.vzhi.domain.user.Education;
import com.xiaobukuaipao.vzhi.domain.user.Experience;
import com.xiaobukuaipao.vzhi.domain.user.Intention;
import com.xiaobukuaipao.vzhi.domain.user.PrivateUserProfile;
import com.xiaobukuaipao.vzhi.domain.user.UserBasic;
import com.xiaobukuaipao.vzhi.event.InfoResult;
import com.xiaobukuaipao.vzhi.manager.GeneralDbManager;
import com.xiaobukuaipao.vzhi.profile.ProfileSkillActivity;
import com.xiaobukuaipao.vzhi.profile.eduexp.EducationListActivity;
import com.xiaobukuaipao.vzhi.profile.jobexp.JobExperienceListActivity;
import com.xiaobukuaipao.vzhi.register.JobObjectiveActivity;
import com.xiaobukuaipao.vzhi.util.GlobalConstants;
import com.xiaobukuaipao.vzhi.util.Logcat;
import com.xiaobukuaipao.vzhi.util.StringUtils;
import com.xiaobukuaipao.vzhi.util.VToast;
import com.xiaobukuaipao.vzhi.view.NestedListView;
import com.xiaobukuaipao.vzhi.view.ProgressBarCircularIndeterminate;
import com.xiaobukuaipao.vzhi.view.RoundedNetworkImageView;
import com.xiaobukuaipao.vzhi.wrap.ProfileWrapActivity;

/**
 *	个人profile编辑页面
 *
 *	@since 2015年01月05日14:52:57
 */
public class PersonalEditPageActivity extends ProfileWrapActivity implements OnScrollChangedListener {
	
	static final String TAG = PersonalEditPageActivity.class.getSimpleName();
	/**
	 * 编辑页面父容器
	 */
	private ScrollView mPersonalEditPage;
	/**
	 * 
	 */
	private View topLayout;
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
	/**
	 * 头像
	 */
	private RoundedNetworkImageView mAvatar;
	/**
	 * 意向职位
	 */
	private TextView mPosition;
	/**
	 * 意向行业
	 */
	private TextView mVocation;
	/**
	 * 意向城市
	 */
	private TextView mIntentCity;
	/**
	 * 意向薪水
	 */
	private TextView mSalary;
	/**
	 * 意向的公司类型
	 */
	private TextView mCompanyType;
	/**
	 * 目前的求职心态
	 */
	private TextView mJobState;
	/**
	 * 编辑基本信息
	 */
	private ImageButton mEditBasciInfo;
	/**
	 * 编辑求职意向
	 */
	private ImageButton mEditJobObjective;
	/**
	 * 添加工作经历
	 */
	private ImageButton mEditJobExper;
	/**
	 * 添加教育经历
	 */
	private ImageButton mEditEduExper;
	/**
	 * 编辑技能
	 */
	private ImageButton mEditSkills;
	/**
	 * 匹配item总高度的工作经历列表
	 */
	private NestedListView mExprListView;
	/**
	 * 匹配item总高度的教育经历列表
	 */
	private NestedListView mEduListView;
	/**
	 * 自适应行宽技能标签
	 */
	private TextView  mSkillsView;
	/**
	 * 工作经历适配器
	 */
	private ExperiencesAdapter mExprAdapter;
	/**
	 * 教育经历适配器
	 */
	private EducationsAdapter mEduAdapter;
	/**
	 * Volley提供的网络请求队列
	 */
	private RequestQueue mQueue;
	private ImageLoader mImageLoader;
	/**
	 * 序列化传递类
	 */
	private UserBasic mUserBasic;
	/**
	 * 求职意向
	 */
	private Intention mIntention;
	/**
	 * 工作经验数据容器
	 */
	private List<Experience> mExperiences;
	/**
	 * 编辑或者删除的经历
	 */
	private Experience experience;
	/**
	 * 教育经历数据容器
	 */
	private List<Education> mEducations;
	/**
	 * 编辑或者删除的教育
	 */
	private Education education;
	/**
	 * 当前uid
	 */
	private Long uid;
	/**
	 * 已完善求职意向
	 */
	private LinearLayout mIntentContent;
	/**
	 * 未完善求职意向
	 */
	private TextView mNoAddIntention;
	/**
	 * 未完善工作经历
	 */
	private TextView mNoAddJobExperience;
	/**
	 * 未完善教育背景
	 */
	private TextView mNoAddEduBg;
	/**
	 * 未完善技能标签
	 */
	private TextView mNoAddSkills;
	/**
	 * 是否更改,用于从其他页面跳转到此页面时,判断是否需要刷新
	 */
	public static boolean isUpdate  = false;
	private ProgressBarCircularIndeterminate mLoading;
	private View mMenuBar;
	private View mSignEdit;
	private TextView mPersonalSign;
	

	
	public void initUIAndData() {
		setContentView(R.layout.activity_main_tab_person_edit);
		setHeaderMenuByLeft(this);
		setHeaderMenuByCenterTitle(R.string.person_profile_str);
		
		mMenuBar = findViewById(R.id.register_menu_bar_id);
		mMenuBar.setBackgroundColor(Color.argb(0x99, 0x2c, 0xc0, 0xeb));
		
		mPersonalEditPage = (ScrollView) findViewById(R.id.personal_edit_page);
		mPersonalEditPage.getViewTreeObserver().addOnScrollChangedListener(this);
		mPersonalEditPage.setVisibility(View.INVISIBLE);
		mPersonalEditPage.smoothScrollTo(0, 0);
		
		
		mLoading = (ProgressBarCircularIndeterminate) findViewById(R.id.loading);
		mLoading.setVisibility(View.VISIBLE);
		
		//此处获取顶部名片卡,并且根据背景图片的大小自动匹配卡片是否需要裁剪还是拉伸
		topLayout = findViewById(R.id.top);
		topLayout.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			
			@SuppressWarnings("deprecation")
			@Override
			public void onGlobalLayout() {
				topLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
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
//				if(resource.getHeight() > topLayout.getHeight()){
//					h = topLayout.getHeight();
//				}else{
//					h = resource.getHeight();
//				}
				bitmap = Bitmap.createBitmap(resource, 0, 0, w, h); 
				topLayout.setBackgroundDrawable(new BitmapDrawable(getResources(), bitmap));
			}
		});
		
		
		//初始化页面控件
		mNickName = (TextView) findViewById(R.id.person_nickname);
		mLocExpr = (TextView) findViewById(R.id.person_loc_and_expr);
		mPositionCorp = (TextView) findViewById(R.id.person_position_corp);
		mAvatar = (RoundedNetworkImageView) findViewById(R.id.person_avatar);
		
		
		mSignEdit = findViewById(R.id.sign_edit);
		mPersonalSign = (TextView) findViewById(R.id.personal_sign);

		mPosition = (TextView) findViewById(R.id.person_position);
		mVocation = (TextView) findViewById(R.id.person_vocation);
		mIntentCity = (TextView) findViewById(R.id.person_intent_city);
		mSalary = (TextView) findViewById(R.id.person_salary);
		mCompanyType = (TextView) findViewById(R.id.person_company_type);
		mJobState = (TextView) findViewById(R.id.person_job_state);

		mEditBasciInfo = (ImageButton) findViewById(R.id.basic_info_edit);
		mEditJobObjective = (ImageButton) findViewById(R.id.job_objective_edit);
		mEditJobExper = (ImageButton) findViewById(R.id.job_expr_edit);
		mEditEduExper = (ImageButton) findViewById(R.id.edu_expr_edit);
		mEditSkills = (ImageButton) findViewById(R.id.skills_edit);
		mExprListView = (NestedListView) findViewById(R.id.job_experience);
		mEduListView = (NestedListView) findViewById(R.id.edu_experience);
		
		mSkillsView = (TextView) findViewById(R.id.personal_skills);
		
		mIntentContent = (LinearLayout) findViewById(R.id.intention_position_content);
		mNoAddIntention = (TextView) findViewById(R.id.not_add_intention);
		
		mNoAddJobExperience = (TextView) findViewById(R.id.not_add_job_experience);
		mNoAddEduBg = (TextView) findViewById(R.id.not_add_edu_bg);
		mNoAddSkills = (TextView) findViewById(R.id.not_add_skills);

		mExprListView.setEmptyView(mNoAddJobExperience);
		mEduListView.setEmptyView(mNoAddEduBg);
		
		//从本地数据库获取id
		String[] projection = { CookieTable.COLUMN_ID };
		Cursor cursor = getApplicationContext().getContentResolver().query(GeneralContentProvider.COOKIE_CONTENT_URI, projection, null, null, null);
		if (cursor != null && cursor.moveToFirst()) {
			if (cursor.getCount() > 0) {
				cursor.moveToFirst();
				uid = (long) cursor.getInt(cursor.getColumnIndexOrThrow(CookieTable.COLUMN_ID));
			} else {
				Logcat.e(TAG, "current cookie must not null");
			}
			// always close the cursor
			cursor.close();
		}
		
		//拉取展示信息
//		mProfileEventLogic.requestPersonalProfileInfo(String.valueOf(uid));//1
		
		mQueue = Volley.newRequestQueue(this);
		// 此时不缓存
		mImageLoader = new ImageLoader(mQueue, new ImageCache() {

			@Override
			public Bitmap getBitmap(String url) {
				return null;
			}

			@Override
			public void putBitmap(String url, Bitmap bitmap) {
			}
		});
		
		setUIListener();
	}
	
	private void editBasic() {
		MobclickAgent.onEvent(PersonalEditPageActivity.this,"bjxx");
		Intent intent = new Intent(PersonalEditPageActivity.this,PersonalBasicInfoActivity.class);
		if (mUserBasic != null) {
			intent.putExtra(GlobalConstants.PARCEL_USER_BASIC, mUserBasic);
		}
		startActivity(intent);
	}
	
	private void setUIListener() {
		mSignEdit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(PersonalEditPageActivity.this, SigningIntroduceActivity.class);
				intent.putExtra("sign", mPersonalSign.getText().toString());
				startActivityForResult(intent, 100);
				overridePendingTransition(R.anim.slide_in_from_bottom, anim.fade_out);
			}
		});
		
		
		mEditBasciInfo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				editBasic();
			}
		});

		mEditJobObjective.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				MobclickAgent.onEvent(PersonalEditPageActivity.this,"qzyxws");
				Intent intent = new Intent(PersonalEditPageActivity.this,JobObjectiveActivity.class);
				intent.putExtra("objective_source", "PersonalEditPageActivity");
				if (mIntention != null) {
					intent.putExtra(GlobalConstants.SERIALIZABLE_USER_INTENTION, mIntention);
				}
				startActivity(intent);
			}
		});

		mNoAddIntention.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				MobclickAgent.onEvent(PersonalEditPageActivity.this,"qzyxws");
				Intent intent = new Intent(PersonalEditPageActivity.this,JobObjectiveActivity.class);
				intent.putExtra("objective_source", "PersonalEditPageActivity");
				if (mIntention != null) {
					intent.putExtra(GlobalConstants.SERIALIZABLE_USER_INTENTION, mIntention);
				}
				startActivity(intent);
			}
		});
		
		mEditJobExper.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				MobclickAgent.onEvent(PersonalEditPageActivity.this,"gzjltj");
				Intent intent = new Intent(PersonalEditPageActivity.this,JobExperienceListActivity.class);
				intent.putExtra("personal_experience_add", true);
				startActivity(intent);
			}
		});
		
		
		mNoAddJobExperience.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				MobclickAgent.onEvent(PersonalEditPageActivity.this,"gzjltj");
				Intent intent = new Intent(PersonalEditPageActivity.this,JobExperienceListActivity.class);
				intent.putExtra("personal_experience_add", true);
				startActivity(intent);
			}
		});

		mEditEduExper.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				MobclickAgent.onEvent(PersonalEditPageActivity.this,"jybjtj");
				Intent intent = new Intent(PersonalEditPageActivity.this,EducationListActivity.class);
				intent.putExtra("personal_education_add", true);
				startActivity(intent);
			}
		});
		mNoAddEduBg.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				MobclickAgent.onEvent(PersonalEditPageActivity.this,"jybjtj");
				Intent intent = new Intent(PersonalEditPageActivity.this,EducationListActivity.class);
				intent.putExtra("personal_education_add", true);
				startActivity(intent);
			}
		});
		mEduListView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					final int position, long id) {

				final String[] clickActions = PersonalEditPageActivity.this.getResources().getStringArray(R.array.long_click_action);
				AlertDialog.Builder builder = new AlertDialog.Builder(PersonalEditPageActivity.this);
				builder.setTitle(PersonalEditPageActivity.this.getResources().getString(R.string.register_edu_str))
						.setItems(clickActions,
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										if (which == 0) {
											
											// 编辑
											Intent intent = new Intent(PersonalEditPageActivity.this, EducationListActivity.class);
											Bundle bundle = new Bundle();
											education = mEducations.get(position);
											
										    bundle.putParcelable(GlobalConstants.GUIDE_EDUCATION_OBJECT, education);
										    intent.putExtra("personal_education_edit", true);
											intent.putExtras(bundle);
											startActivity(intent);
											
										} else {
											// delete job experience
											education = mEducations.get(position);
											mProfileEventLogic.deleteEducation(education.getId());
										}
										
									}

								});
				AlertDialog dialog = builder.create();
				dialog.setCancelable(true);
				dialog.setCanceledOnTouchOutside(true);
				dialog.show();
				return false;
			}
		});
		mExprListView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					final int position, long id) {

				if(mExperiences.get(position).getCertify() != null && mExperiences.get(position).getCertify() == 1){
					VToast.show(PersonalEditPageActivity.this, getString(R.string.unjobs));
					return true;
				}
				
				final String[] clickActions = getResources().getStringArray(R.array.long_click_action);
				AlertDialog.Builder builder = new AlertDialog.Builder(PersonalEditPageActivity.this);
				builder.setTitle(getResources().getString(R.string.user_guide_job_detail_center)).setItems(
						clickActions, new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								if (which == 0) {
									// 编辑
									Intent intent = new Intent(PersonalEditPageActivity.this,JobExperienceListActivity.class);
									Bundle bundle = new Bundle();
									experience = mExperiences.get(position);
								    bundle.putParcelable(GlobalConstants.GUIDE_JOB_EXPERIENCE_OBJECT, experience);
								    intent.putExtra("personal_experience_edit", true);
									intent.putExtras(bundle);
									startActivity(intent);
								} else {
									// delete job experience
									experience = mExperiences.get(position);
									mProfileEventLogic.deleteExperience(experience.getId());
								}
							}
						});
				AlertDialog dialog = builder.create();
				dialog.setCancelable(true);
				dialog.setCanceledOnTouchOutside(true);
				dialog.show();
				return false;
			}
		});
		
		mEditSkills.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				MobclickAgent.onEvent(PersonalEditPageActivity.this,"jnbqws");
				Intent intent = new Intent(PersonalEditPageActivity.this,ProfileSkillActivity.class);
				intent.putExtra("personal_profile_edit", true);
				startActivity(intent);
			}
		});
		
		mNoAddSkills.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				MobclickAgent.onEvent(PersonalEditPageActivity.this,"jnbqws");
				Intent intent = new Intent(PersonalEditPageActivity.this,ProfileSkillActivity.class);
				intent.putExtra("personal_profile_edit", true);
				startActivity(intent);
			}
		});
		
		
	}

	int alpha = 0;
	@Override
	public void onScrollChanged() {
		// 当profile滚动到 banner完全消失了 就会让menubar从透明变为不透明
		float a = mPersonalEditPage.getScrollY() / (float) (topLayout.getHeight() - mMenuBar.getHeight()); 
//		Logcat.d("test", "mPersonalEditPage.getScrollY():" + mPersonalEditPage.getScrollY() + "  topLayout.getHeight() " + topLayout.getHeight());
		alpha = (int) ((a > 1 ? 1 : a) * 0xff);
		alpha = alpha < 0 ? 0 : alpha; 
//		Logcat.d("test", "alpha " + alpha);
		mMenuBar.setBackgroundColor(Color.argb(alpha, 0x2c, 0xc0, 0xeb));
		getTitleView().setTextColor(Color.argb(alpha, 0xff, 0xff, 0xff));
	}
	
	public void confirmNextAction() {
		
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
			case R.id.personalProfileHttp:
				JSONObject mJSONResult = (JSONObject) JSONObject.parse(infoResult.getResult());
				if(mJSONResult != null){
					PrivateUserProfile mUserProfile = new PrivateUserProfile(mJSONResult.getJSONObject(GlobalConstants.JSON_USERPROFILE));
					// 个人信息
					mUserBasic = new UserBasic(mUserProfile.getBasic());
					
					if(mUserProfile.getContact() != null){
						if(mUserProfile.getContact().getJSONObject(GlobalConstants.JSON_DATA) != null){
							JSONObject contact = mUserProfile.getContact().getJSONObject(GlobalConstants.JSON_DATA);
							if(contact.getString(GlobalConstants.JSON_MOBILE) != null){
								mUserBasic.setMobile(contact.getString(GlobalConstants.JSON_MOBILE));
							}
							if(contact.getString(GlobalConstants.JSON_EMAIL) != null){
								mUserBasic.setPersonalemail(contact.getString(GlobalConstants.JSON_EMAIL));
							}
						}
					}
					
					if (!StringUtils.isEmpty(mUserBasic.getRealname())) {
						mNickName.setText(mUserBasic.getRealname());
					} else {
						if (!StringUtils.isEmpty(mUserBasic.getNickname())) {
							mNickName.setText(mUserBasic.getNickname());
						} else {
							mNickName.setText(this.getResources().getString(R.string.not_add_str));
						}
					}
					

					
					StringBuilder sb = new StringBuilder();
					boolean flag = false;
					if(StringUtils.isNotEmpty(mUserBasic.getCity())){
						sb.append(mUserBasic.getCity());
						flag = true;
					}
					if(StringUtils.isNotEmpty(mUserBasic.getExpryear())){
						if(flag){
							sb.append(getString(R.string.general_tips_dot));
						}
						sb.append(mUserBasic.getExpryear());
					}
					
					if(StringUtils.isNotEmpty(sb.toString())){
						mLocExpr.setText(sb.toString());
					}else{
						mLocExpr.setVisibility(View.GONE);
					}

					flag = false;
					sb.delete(0, sb.length());
					if(StringUtils.isNotEmpty(mUserBasic.getTitle())){
						sb.append(mUserBasic.getTitle());
						flag = true;
					}
					if(StringUtils.isNotEmpty(mUserBasic.getCorp())){
						if(flag){
							sb.append(getString(R.string.general_tips_dot));
						}
						sb.append(mUserBasic.getCorp());
					}
					
					if(StringUtils.isNotEmpty(sb.toString())){
						mPositionCorp.setText(sb.toString());
					}else{
						mPositionCorp.setVisibility(View.INVISIBLE);
					}
					
					
					//设置默认头像
					mAvatar.setDefaultImageResId(R.drawable.general_user_avatar);
					//优先加载真实头像
					if (!StringUtils.isEmpty(mUserBasic.getRealavatar())) {
						mAvatar.setImageUrl(mUserBasic.getRealavatar(), mImageLoader);
					} else { 
						mAvatar.setImageUrl(mUserBasic.getNickavatar(), mImageLoader);
					}
					
					//个人签名
					if(StringUtils.isNotEmpty(mUserBasic.getIntroduce())){
						mPersonalSign.setText(mUserBasic.getIntroduce());
					}
					
					sb.delete(0, sb.length());
					
					
					// 求职意向
					if (mUserProfile.getIntention() != null) {
						mIntentContent.setVisibility(View.VISIBLE);
						mNoAddIntention.setVisibility(View.GONE);
						
						mIntention = new Intention(mUserProfile.getIntention());
						
						sb.append(getResources().getString(R.string.intention_position_str));
						sb.append(" : ");
						
						if (mIntention.getPositions() != null) {
							mPosition.setVisibility(View.VISIBLE);
							if (mIntention.getPositions().size() > 0) {
								for (int i = 0; i < mIntention.getPositions().size() - 1; i++) {
									sb.append(mIntention.getPositions().getJSONObject(i).getString(GlobalConstants.JSON_NAME));
									sb.append(",   ");
								}
								sb.append(mIntention.getPositions().getJSONObject(mIntention.getPositions().size() - 1).getString(GlobalConstants.JSON_NAME));
								mPosition.setText(sb.toString());
							}
						} else {
							mPosition.setVisibility(View.GONE);
						}
						
						// 行业
						if (mIntention.getIndustry() != null) {
							if (mIntention.getIndustry().getString(GlobalConstants.JSON_NAME) != null || mIntention.getIndustry().getString(GlobalConstants.JSON_NAME).length() > 0) {
								mVocation.setText(getResources().getString(R.string.intention_industry_str) + " : " + mIntention.getIndustry().getString(GlobalConstants.JSON_NAME));
								mVocation.setVisibility(View.VISIBLE);
							}
						} else {
							mVocation.setVisibility(View.GONE);
						}
						
						// StringBuilder清空
						sb.delete(0, sb.length());
						sb.append(getResources().getString(R.string.intention_cities_str));
						sb.append(" : ");
						if (mIntention.getCities() != null && mIntention.getCities().size() > 0) {
							for (int i = 0; i < mIntention.getCities().size() - 1; i++) {
								sb.append(mIntention.getCities().get(i));
								sb.append(getString(R.string.general_tips_spilte));
							}
							sb.append(mIntention.getCities().get(mIntention.getCities().size() - 1));
							mIntentCity.setText(sb.toString());
							mIntentCity.setVisibility(View.VISIBLE);
						} else {
							mIntentCity.setVisibility(View.GONE);
						}
		
						if (mIntention.getMinsalary() != null || mIntention.getMinsalary().getString(GlobalConstants.JSON_NAME).length() > 0) {
							mSalary.setText(getResources().getString(R.string.intention_salary_str) + " : " + mIntention.getMinsalary().getString(GlobalConstants.JSON_NAME));
							mSalary.setVisibility(View.VISIBLE);
						} else {
							mSalary.setVisibility(View.GONE);
						}
						sb.delete(0, sb.length());
						
						if (mIntention.getCorptypes() != null && mIntention.getCorptypes().size() > 0) {
							for(int i=0; i < mIntention.getCorptypes().size() - 1; i++) {
								sb.append(mIntention.getCorptypes().getJSONObject(i).getString(GlobalConstants.JSON_NAME));
								sb.append(", ");
							}
							sb.append(mIntention.getCorptypes().getJSONObject(mIntention.getCorptypes().size() - 1).getString(GlobalConstants.JSON_NAME));
							mCompanyType.setText(getString(R.string.intention_corptype_str)+ " : " + sb.toString());
							mCompanyType.setVisibility(View.VISIBLE);
						} else {
							mCompanyType.setVisibility(View.GONE);
						}
						
						if (mIntention.getStatus() != null && StringUtils.isNotEmpty(mIntention.getStatus().getString(GlobalConstants.JSON_NAME))) {
							mJobState.setText(getString(R.string.intention_status_str)+ " : " + mIntention.getStatus().getString(GlobalConstants.JSON_NAME));
							mJobState.setVisibility(View.VISIBLE);
						} else {
							mJobState.setVisibility(View.GONE);
						}
					} else {
						mIntentContent.setVisibility(View.GONE);
						mNoAddIntention.setVisibility(View.VISIBLE);
					}
					
					// append job experiences
					mExperiences = new ArrayList<Experience>();
					JSONArray mExprArray = mUserProfile.getExpr().getJSONArray(GlobalConstants.JSON_DATA);
					if (mExprArray != null) {
						mExprListView.setVisibility(View.VISIBLE);
						mNoAddJobExperience.setVisibility(View.GONE);
						
						if (mExprArray.size() > 0) {
							for (int i = 0; i < mExprArray.size(); i++) {
								mExperiences.add(new Experience(mExprArray.getJSONObject(i)));
							}
							mExprAdapter = new ExperiencesAdapter(this, mExperiences,R.layout.job_experience_item);
							mExprListView.setAdapter(mExprAdapter);
						}
					} else {
						mExprListView.setVisibility(View.GONE);
						mNoAddJobExperience.setVisibility(View.VISIBLE);
					}
					
					// append educations--教育经历
					mEducations = new ArrayList<Education>();
					JSONArray mEduArray = mUserProfile.getEdu().getJSONArray(GlobalConstants.JSON_DATA);
					if (mEduArray != null) {
						mNoAddEduBg.setVisibility(View.GONE);
						mEduListView.setVisibility(View.VISIBLE);
						
						if (mEduArray.size() > 0) {
							for (int i = 0; i < mEduArray.size(); i++) {
								mEducations.add(new Education(mEduArray.getJSONObject(i)));
							}
							mEduAdapter = new EducationsAdapter(this, mEducations,R.layout.edu_experience_item);
							mEduListView.setAdapter(mEduAdapter);
						}
					} else {
						mNoAddEduBg.setVisibility(View.VISIBLE);
						mEduListView.setVisibility(View.GONE);
					}
	
					sb.delete(0, sb.length());
					if (mUserProfile.getSkills() != null && mUserProfile.getSkills().size() > 0) {
						mNoAddSkills.setVisibility(View.GONE);
						mSkillsView.setVisibility(View.VISIBLE);
						
						String splite = getString(R.string.general_tips_spilte);
						for (int i = 0; i < mUserProfile.skills.size(); i++) {
							sb.append(mUserProfile.skills.getJSONObject(i).getString(GlobalConstants.JSON_NAME));
							sb.append(splite);
						}
						mSkillsView.setText(sb.substring(0, sb.length() - splite.length()));
						
					} else {
						mNoAddSkills.setVisibility(View.VISIBLE);
						mSkillsView.setVisibility(View.GONE);
					}
					// 访问网络请求成功，才显示整体的View
					mPersonalEditPage.setVisibility(View.VISIBLE);
					mLoading.setVisibility(View.INVISIBLE);
				}
				break;
			case R.id.profile_workexpr_delete:
				
				if(infoResult.getMessage().getStatus() == 0){
					// 此时删除成功，将删除的数据从Adapter中删除
					for(int i=0; i < mExperiences.size(); i++) {
						if(mExperiences.get(i).getId().equals(experience.getId())) {
							mExperiences.remove(i);
						}
					}
					GeneralDbManager.getInstance().setWorkPerform(mExperiences.isEmpty() ? 0 : 1);
					// 更新Adapter
					mExprAdapter.notifyDataSetChanged();
				}
				VToast.show(this, infoResult.getMessage().getMsg());
				break;
				
			case R.id.profile_edu_delete:
				if(infoResult.getMessage().getStatus() == 0){
					for(int i=0; i < mEducations.size(); i++) {
						if(mEducations.get(i).getId().equals(education.getId())) {
							mEducations.remove(i);
						}
					}
					// 更新Adapter
					mEduAdapter.notifyDataSetChanged();
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
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		// 在这里重新执行网络请求
		mProfileEventLogic.requestPersonalProfileInfo(String.valueOf(uid));
	}
	
	@Override
	protected void onResume() {
		// 在这里重新执行网络请求
		mProfileEventLogic.requestPersonalProfileInfo(String.valueOf(uid));
		super.onResume();
	}
	
	@Override
	public void backActivity() {
		super.backActivity();
		setResult(RESULT_OK, getIntent());
	}

}
