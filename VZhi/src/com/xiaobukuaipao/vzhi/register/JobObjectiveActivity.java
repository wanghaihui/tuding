package com.xiaobukuaipao.vzhi.register;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Message;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.xiaobukuaipao.vzhi.AppActivityManager;
import com.xiaobukuaipao.vzhi.MainRecruitActivity;
import com.xiaobukuaipao.vzhi.PersonalEditPageActivity;
import com.xiaobukuaipao.vzhi.R;
import com.xiaobukuaipao.vzhi.domain.JobInfo;
import com.xiaobukuaipao.vzhi.domain.user.Intention;
import com.xiaobukuaipao.vzhi.event.InfoResult;
import com.xiaobukuaipao.vzhi.fragment.RecruitPageFragment;
import com.xiaobukuaipao.vzhi.util.GlobalConstants;
import com.xiaobukuaipao.vzhi.util.StringUtils;
import com.xiaobukuaipao.vzhi.util.VToast;
import com.xiaobukuaipao.vzhi.widget.FormItemByLineLayout;
import com.xiaobukuaipao.vzhi.widget.FormItemByLineLayout.OnEditCompleteListener;
import com.xiaobukuaipao.vzhi.widget.FormItemByLineView;
import com.xiaobukuaipao.vzhi.wrap.RegisterWrapActivity;

public class JobObjectiveActivity extends RegisterWrapActivity {


	public List<JobInfo> mJobInfoList = null;

	private TextView mIndustry;
	private TextView mIntentSalary;
	private TextView mCompanyType;
	private TextView mJobState;
	private TextView mHotCities;
	private TextView mIntentPos;

//	private AlertDialog mAlertDialog;

	private FormItemByLineView jobCategory;
	private FormItemByLineLayout jobSalary;
	private FormItemByLineView jobCorpType;
	private FormItemByLineView jobStates;
	private FormItemByLineView jobHotCities;
	private FormItemByLineView jobIntentPos;
	private Intention mIntention;
	
	// 行业方向
	private HashMap<String, String> mIndustryMap;
	// 求职意向
	private ArrayList<HashMap<String, String>> mIntentPositionList;
	//意向公司的類型
	private ArrayList<HashMap<String,String>> mCorpTypes;
	//求职心态
	private HashMap<String, String> mStateMap;
	
	// Post传值
	// 行业方向
	private String industry = null;
	// 意向岗位
	private String intentPos = null;
	// 期望月薪
	private int mSalary = -1;
	// 公司类型
	private String mCortType = null;
	// 目标城市
	private String intentCity = null;
	// 求职心态
	private String jobState = null;
	
	
	private String objectiveSource = null;
	
	private boolean intentionGuide = false;


	@Override
	public void initUIAndData() {
		setContentView(R.layout.activity_job_objective);
		setHeaderMenuByLeft(this);
		setHeaderMenuByCenterTitle(R.string.title_job_objective);
		setHeaderMenuByRight(R.string.general_save);
		
		mIntention = (Intention) getIntent().getSerializableExtra(GlobalConstants.SERIALIZABLE_USER_INTENTION);
		objectiveSource = getIntent().getStringExtra("objective_source");
		
		intentionGuide = getIntent().getBooleanExtra("intention_guide", false);
		
		jobCategory = (FormItemByLineView) findViewById(R.id.job_objective_category);
		jobSalary = (FormItemByLineLayout) findViewById(R.id.job_objective_salary);
		jobSalary.getInfoEdit().setInputType(EditorInfo.TYPE_CLASS_NUMBER | EditorInfo.TYPE_NUMBER_FLAG_DECIMAL);
		jobSalary.getInfoEdit().setFilters(new InputFilter[]{new InputFilter.LengthFilter(2)});//最多二位
		
		
		jobCorpType = (FormItemByLineView) findViewById(R.id.job_objective_company_type);
		jobStates = (FormItemByLineView) findViewById(R.id.job_objective_jobstate);
		jobHotCities = (FormItemByLineView) findViewById(R.id.job_objective_intention_city);
		jobIntentPos = (FormItemByLineView) findViewById(R.id.job_objective_intention_pos);

		onClickListenerBySaveDataAndRedirectActivity(R.id.job_objective_salary);
		onClickListenerBySaveDataAndRedirectActivity(R.id.job_objective_intention_city);
		onClickListenerBySaveDataAndRedirectActivity(R.id.job_objective_intention_pos);
		onClickListenerBySaveDataAndRedirectActivity(R.id.job_objective_category);
		onClickListenerBySaveDataAndRedirectActivity(R.id.job_objective_company_type);
		onClickListenerBySaveDataAndRedirectActivity(R.id.job_objective_jobstate);
		
		mIndustry = jobCategory.getFormContent();
		
		mIntentSalary = jobSalary.getFormContent();
		
		mCompanyType = jobCorpType.getFormContent();
		
		mJobState = jobStates.getFormContent();
		
		mHotCities = jobHotCities.getFormContent();
		
		mIntentPos = jobIntentPos.getFormContent();

		// 初始化Map
		mIndustryMap = new HashMap<String, String>();
		// 求职意向 
		mIntentPositionList = new ArrayList<HashMap<String, String>>();
		//
		mCorpTypes = new ArrayList<HashMap<String,String>>();
		
		mStateMap = new HashMap<String, String>();
		// 如果mIntention中没有数据，或者mIntention为null
		if (mIntention != null) {
			// 行业方向
			if (mIntention.getIndustry() != null && mIntention.getIndustry().getString(GlobalConstants.JSON_ID) != null) {
				mIndustryMap.put(mIntention.getIndustry().getString(GlobalConstants.JSON_ID), mIntention.getIndustry().getString(GlobalConstants.JSON_NAME));
				// 赋值
				mIndustry.setText(mIntention.getIndustry().getString(GlobalConstants.JSON_NAME));
				industry = mIntention.getIndustry().getString(GlobalConstants.JSON_NAME);
			}
			// 意向岗位
			if (mIntention.getPositions() != null && mIntention.getPositions().size() > 0) {
				StringBuilder sb = new StringBuilder();
				for (int i=0; i < mIntention.getPositions().size(); i++) {
					HashMap<String, String> map = new HashMap<String, String>();
					map.put(mIntention.getPositions().getJSONObject(i).getString(GlobalConstants.JSON_ID), mIntention.getPositions().getJSONObject(i).getString(GlobalConstants.JSON_NAME));
					sb.append(mIntention.getPositions().getJSONObject(i).getString(GlobalConstants.JSON_NAME));
					sb.append(",");
					mIntentPositionList.add(map);
				}
				mIntentPos.setText(sb.toString().substring(0, sb.toString().length() - 1));
				intentPos = sb.toString().substring(0, sb.toString().length() - 1);
			}
			// 期望月薪
			if (mIntention.getMinsalary() != null && mIntention.getMinsalary().getString(GlobalConstants.JSON_NAME).length() > 0) {
					mIntentSalary.setText(mIntention.getMinsalary().getInteger(GlobalConstants.JSON_ID) + "K");
					mSalary = mIntention.getMinsalary().getInteger(GlobalConstants.JSON_ID);
			}
			
			// 求职心态
			if (mIntention.getStatus() != null && mIntention.getStatus().getString(GlobalConstants.JSON_NAME).length() > 0) {
				mStateMap.put(mIntention.getStatus().getString(GlobalConstants.JSON_ID), mIntention.getStatus().getString(GlobalConstants.JSON_NAME));
				mJobState.setText(mIntention.getStatus().getString(GlobalConstants.JSON_NAME));
				jobState = mIntention.getStatus().getString(GlobalConstants.JSON_ID);
			}
			
			// 公司类型
			if (mIntention.getCorptypes() != null && mIntention.getCorptypes().size() > 0) {
				
				StringBuilder sb = new StringBuilder();
				StringBuilder sbId = new StringBuilder();
				for (int i=0; i < mIntention.getCorptypes().size(); i++) {
					HashMap<String, String> map = new HashMap<String, String>();
					map.put(mIntention.getCorptypes().getJSONObject(i).getString(GlobalConstants.JSON_ID), mIntention.getCorptypes().getJSONObject(i).getString(GlobalConstants.JSON_NAME));
					sbId.append(mIntention.getCorptypes().getJSONObject(i).getString(GlobalConstants.JSON_ID));
					sb.append(mIntention.getCorptypes().getJSONObject(i).getString(GlobalConstants.JSON_NAME));
					sbId.append(",");
					sb.append(",");
					mCorpTypes.add(map);
				}
				
				mCompanyType.setText(sb.toString().substring(0, sb.toString().length() - 1));
				mCortType = sbId.toString().substring(0, sbId.toString().length() - 1);
			}
			
			// 目标城市
			if (mIntention.getCities() != null && mIntention.getCities().size() > 0) {
				StringBuilder sb = new StringBuilder();
				for (int i=0; i < mIntention.getCities().size(); i++) {
					sb.append(mIntention.getCities().get(i));
					sb.append(",");
				}
				mHotCities.setText(sb.toString().substring(0, sb.toString().length() - 1));
				intentCity = sb.toString().substring(0, sb.toString().length() - 1);
			}
			
		} else {
		}
	}

	@Override
	public void chooseRedirectActivity(View view) {

		switch (view.getId()) {
		case R.id.job_objective_category:
			selectIndustry();
			break;
		case R.id.job_objective_intention_pos:
			selectIntentJob();
			break;
		case R.id.job_objective_salary:
			selectIntentSalary();
			break;
		case R.id.job_objective_company_type:
			selectCompanyType();
			break;
		case R.id.job_objective_intention_city:
			selectIntentCity();
			break;
		case R.id.job_objective_jobstate:
			selectJobState();
			break;
		case R.id.menu_bar_right:
			saveJobInfosAndEnterMainPage();
		}
	}

	private void saveJobInfosAndEnterMainPage() {
		
		if (industry == null) {
			VToast.show(this, getString(R.string.general_tips_choose_tips,jobCategory.getFormLabelText()));
			return;
		}

		if (intentPos == null) {
			VToast.show(this, getString(R.string.general_tips_choose_tips,jobIntentPos.getFormLabelText()));
			return;
		}

		if (mSalary == -1) {
			VToast.show(this, getString(R.string.general_tips_choose_tips,jobSalary.getFormLabelText()));
			return;
		}
		
		if (mCortType == null) {
			VToast.show(this, getString(R.string.general_tips_choose_tips,jobCorpType.getFormLabelText()));
			return;
		}
		
		if (intentCity == null) {
			VToast.show(this, getString(R.string.general_tips_choose_tips,jobIntentPos.getFormLabelText()));
			return;
		}
		
		if (jobState == null) {
			VToast.show(this, getString(R.string.general_tips_choose_tips,jobStates.getFormLabelText()));
			return;
		}
		
		Log.i(TAG, "industry" + industry);
		Log.i(TAG, "intentPos" + intentPos);
		Log.i(TAG, "salary=" + mSalary);
		Log.i(TAG, "corpType=" + mCortType);
		Log.i(TAG, "intentCity=" + intentCity);
		Log.i(TAG, "jobState=" + jobState);
		
		mRegisterEventLogic.setIntentJobInfo(industry, intentPos, mSalary, mCortType, intentCity, jobState);
	}


	/**
	 * 选择行业方向
	 */
	private void selectIndustry() {
		Intent intent = new Intent(JobObjectiveActivity.this, JobObjectiveSingleActivity.class);
		intent.putExtra("industry_selsect", mIndustryMap);  
		intent.putExtra(GlobalConstants.SEQ_STRING, 1);
		startActivityForResult(intent, 102);
	}

	/**
	 * 选择意向工作 -- 求职意向
	 */
	private void selectIntentJob() {
		Intent intent = new Intent(JobObjectiveActivity.this, JobObjectiveSingleActivity.class);
		intent.putExtra("intent_pos", mIntentPositionList);
		intent.putExtra(GlobalConstants.SEQ_STRING, 2);
		startActivityForResult(intent, 101);
	}

	/**
	 * 选择期望工资
	 */
	private void selectIntentSalary() {
		jobSalary.setEdit(true);
		jobSalary.setCompleteListener(new OnEditCompleteListener() {
			@Override
			public void onEditComplete() {
				String salary = jobSalary.getInfoEdit().getText().toString();
				if(StringUtils.isNotEmpty(salary)){
					mSalary = Integer.parseInt(salary);
					mIntentSalary.setText(mSalary + "K");
				}else{
					mSalary = -1;
				}
			}
		});
	}

	/**
	 * 选择公司类型
	 */
	private void selectCompanyType() {
		Intent intent = new Intent(JobObjectiveActivity.this, JobObjectiveSingleActivity.class);
		intent.putExtra("intent_corptype", mCorpTypes);
		intent.putExtra(GlobalConstants.SEQ_STRING, 3);
		startActivityForResult(intent, 104);
	}

	/**
	 * 选择目标城市
	 */
	@SuppressLint("NewApi")
	private void selectIntentCity() {
		Intent intent = new Intent(this,JobObjectiveCityActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivityForResult(intent, 103);
	}

	/**
	 * 选择求职心态
	 */
	private void selectJobState() {
		
		Intent intent = new Intent(JobObjectiveActivity.this, JobObjectiveSingleActivity.class);
		intent.putExtra("intent_jobstate", mStateMap);
		intent.putExtra(GlobalConstants.SEQ_STRING, 4);
		startActivityForResult(intent, 105);
		
	}

	@Override
	public void onEventMainThread(Message msg) {
		if (msg.obj instanceof InfoResult) {
			InfoResult infoResult = (InfoResult) msg.obj;
			switch (msg.what) {
			case R.id.register_set_intent_job:
				if(infoResult.getMessage().getStatus() == 0){
				}else{
					VToast.show(this, infoResult.getMessage().getMsg());
				}
				if (!StringUtils.isEmpty(objectiveSource)) {
					Intent intent = new Intent(JobObjectiveActivity.this,PersonalEditPageActivity.class);
					startActivity(intent);
				} else if (intentionGuide) {
					// 代表求职意向完善
					Intent mIntent = new Intent();
                    mIntent.putExtra("intention_complete", true);
                    setResult(RecruitPageFragment.REQUEST_CODE, mIntent);
                    AppActivityManager.getInstance().finishActivity(JobObjectiveActivity.this);
				} else {
					AppActivityManager.getInstance().finishAllActivity();
					Intent intent = new Intent(JobObjectiveActivity.this,MainRecruitActivity.class);
					startActivity(intent);
					AppActivityManager.getInstance().finishActivity(JobObjectiveActivity.this);
				}
				break;
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);
		
		if (data != null)
			if (resultCode == RESULT_OK) {
				if (requestCode == 101) {
					// 在这里获得选择的意向岗位信息
					mIntentPositionList = (ArrayList<HashMap<String, String>>) data.getSerializableExtra("intent_pos_back");
					if (mIntentPositionList.size() > 0) {
						StringBuilder sb = new StringBuilder();
						StringBuilder sbId = new StringBuilder();
						for(int i=0; i < mIntentPositionList.size(); i++) {
							for (Map.Entry<String, String> entry : mIntentPositionList.get(i).entrySet()) {
								sb.append(entry.getValue());
								sbId.append(entry.getKey());
								sb.append(",");
								sbId.append(",");
						    }
						}
						// TextView设置
						mIntentPos.setText(sb.toString().substring(0, sb.toString().length() - 1));
						intentPos = sbId.toString().substring(0, sbId.toString().length() - 1);
					}else{
						mIntentPos.setText("");
						intentPos = null;
					}
				}
				if (requestCode == 102) {
					mIndustryMap = (HashMap<String, String>) data.getSerializableExtra("industry_selsect_back");
					for(Map.Entry<String, String> entry : mIndustryMap.entrySet()) {
						mIndustry.setText(entry.getValue());
						industry= entry.getKey();
					}
				}
				if(requestCode == 103){
					String city = data.getStringExtra(RegisterResidentSearchActivity.BACKDATA_KEY);
					if (StringUtils.isNotEmpty(city)) {
						mHotCities.setText(city);
						intentCity = city;
					}
				}
				
				if(requestCode == 104){
					// 在这里获得选择的意向信息
					mCorpTypes = (ArrayList<HashMap<String, String>>) data.getSerializableExtra("intent_corptype");
					if (mCorpTypes.size() > 0) {
						StringBuilder sb = new StringBuilder();
						StringBuilder sbId = new StringBuilder();
						for(int i=0; i < mCorpTypes.size(); i++) {
							for (Map.Entry<String, String> entry : mCorpTypes.get(i).entrySet()) {
								sb.append(entry.getValue());
								sbId.append(entry.getKey());
								sb.append(",");
								sbId.append(",");
						    }
						}
						// TextView设置
						mCompanyType.setText(sb.toString().substring(0, sb.toString().length() - 1));
						mCortType = sbId.toString().substring(0, sbId.toString().length() - 1);
					}else{
						mCompanyType.setText("");
						mCortType = null;
					}
				}
				
				if (requestCode == 105) {
					mStateMap = (HashMap<String, String>) data.getSerializableExtra("intent_jobstate");
					for(Map.Entry<String, String> entry : mStateMap.entrySet()) {
						mJobState.setText(entry.getValue());
						jobState= entry.getKey();
					}
				}
			} 
	}
	
	
	
}
