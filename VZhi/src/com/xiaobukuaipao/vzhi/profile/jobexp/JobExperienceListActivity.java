package com.xiaobukuaipao.vzhi.profile.jobexp;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Message;
import android.text.InputFilter;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.ScrollView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xiaobukuaipao.vzhi.AppActivityManager;
import com.xiaobukuaipao.vzhi.R;
import com.xiaobukuaipao.vzhi.adapter.CommonAdapter;
import com.xiaobukuaipao.vzhi.adapter.ViewHolder;
import com.xiaobukuaipao.vzhi.domain.user.Experience;
import com.xiaobukuaipao.vzhi.event.InfoResult;
import com.xiaobukuaipao.vzhi.manager.GeneralDbManager;
import com.xiaobukuaipao.vzhi.register.JobObjectiveActivity;
import com.xiaobukuaipao.vzhi.util.ActivityQueue;
import com.xiaobukuaipao.vzhi.util.GlobalConstants;
import com.xiaobukuaipao.vzhi.util.StringUtils;
import com.xiaobukuaipao.vzhi.util.VToast;
import com.xiaobukuaipao.vzhi.view.ListViewForScrollView;
import com.xiaobukuaipao.vzhi.widget.DateDialog;
import com.xiaobukuaipao.vzhi.widget.FormItemByLineLayout;
import com.xiaobukuaipao.vzhi.widget.FormItemByLineLayout.OnEditCompleteListener;
import com.xiaobukuaipao.vzhi.widget.FormItemByLineView;
import com.xiaobukuaipao.vzhi.wrap.ProfileWrapActivity;

public class JobExperienceListActivity extends ProfileWrapActivity implements
		OnClickListener {
	public static final String TAG = JobExperienceListActivity.class.getSimpleName();
	
	private Experience mExperience;
	private ListViewForScrollView mAlreadyList;
	private PopupWindow mPopupWindow;
	private LinearLayout mPopupLayout;
	private List<Experience> mListExp;
	private JobExpListAdapter mJobExpListAdapter;

	private FormItemByLineView mCompany;
	private FormItemByLineView mName;
	private FormItemByLineView mTime;
	private FormItemByLineLayout mSalary;
	private FormItemByLineView mDesc;

	private DateDialog mDateDialog;

	private Bundle mBundle;
	private Class<?> next;
	
	// 从个人信息页面过来添加工作经历
	private boolean mPersonalAddExperience;
	// 从个人信息页面过来编辑工作信息
	private boolean mPersonalEditExperience;
	
	// 从注册流程过来的
	private boolean mRegisterPersonalExperience;

	private boolean mPersonalCompleteExperience;
	
	private Context mContext;
	
	private boolean isShowing = false;
	@Override
	public void handleSavedInstanceState(Bundle savedInstanceState) {
		super.handleSavedInstanceState(savedInstanceState);
		Bundle bundle = this.getIntent().getExtras();
		if (bundle == null)
			return;
	}

	@Override
	public void initUIAndData() {
		setContentView(R.layout.activity_jobexp_list);
		mContext = this;
		// 得到Intent信息
		mPersonalAddExperience = getIntent().getBooleanExtra("personal_experience_add", false);
		mPersonalEditExperience = getIntent().getBooleanExtra("personal_experience_edit", false);
		mPersonalCompleteExperience = getIntent().getBooleanExtra("personal_experience_complete", false);
		
		mRegisterPersonalExperience = getIntent().getBooleanExtra("personal_experience_register", false);
		
		mBundle = getIntent().getExtras();
		if(mPersonalCompleteExperience){
			if (mBundle == null) {
				mBundle = new Bundle();
			} 
			mExperience = new Experience();
			mBundle.putParcelable(GlobalConstants.GUIDE_JOB_EXPERIENCE_OBJECT,mExperience);
		}else {
			if (mPersonalAddExperience) {
				// 此时是增加工作经验
				mBundle.clear();
				mExperience = new Experience();
				mBundle.putParcelable(GlobalConstants.GUIDE_JOB_EXPERIENCE_OBJECT,mExperience);
			} else {
				// 此时是编辑当前工作经历
				mExperience = mBundle.getParcelable(GlobalConstants.GUIDE_JOB_EXPERIENCE_OBJECT);
			}
		}
		
		setHeaderMenuByLeft(this);
		setHeaderMenuByCenterTitle(R.string.register_jobexp_complete);
		
		// 判断是下一步还是保存
		if (mPersonalAddExperience || mPersonalEditExperience) {
			if (mRegisterPersonalExperience) {
				setHeaderMenuByRight(R.string.general_tips_next);
			} else {
				setHeaderMenuByRight(R.string.general_save);
			}
		} else {
			setHeaderMenuByRight(R.string.general_tips_next);
		}
		((ScrollView) findViewById(R.id.scrollview)).smoothScrollTo(0, 0);
		mPopupLayout = (LinearLayout) View.inflate(this,R.layout.popup_jobexp_next, null);
		mPopupWindow = new PopupWindow();
		mPopupWindow.setBackgroundDrawable(new ColorDrawable(0));
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
				getWindow().setAttributes(lp);
			}
		});

		mPopupLayout.findViewById(R.id.popup_jobexp_on).setOnClickListener(this);
		mPopupLayout.findViewById(R.id.popup_jobexp_next).setOnClickListener(this);

		// 已经添加的工作经历
		mAlreadyList = (ListViewForScrollView) findViewById(R.id.register_jobexp_already_add);
		mListExp = new ArrayList<Experience>();
		
		mJobExpListAdapter = new JobExpListAdapter(this, mListExp,R.layout.item_job_experience);
		mAlreadyList.setAdapter(mJobExpListAdapter);
		mAlreadyList.setEmptyView(findViewById(R.id.empty_view));
		mAlreadyList.setOnItemClickListener(mExpItemClick);
		
		
		mCompany = (FormItemByLineView) findViewById(R.id.register_jobexp_company);
		mName = (FormItemByLineView) findViewById(R.id.register_jobexp_name);
		mTime = (FormItemByLineView) findViewById(R.id.register_jobexp_time);
		mSalary = (FormItemByLineLayout) findViewById(R.id.register_jobexp_salary);
		mDesc = (FormItemByLineView) findViewById(R.id.register_jobexp_desc);
		mCompany.setOnClickListener(this);
		mName.setOnClickListener(this);
		mTime.setOnClickListener(this);
		mSalary.setOnClickListener(this);
		mDesc.setOnClickListener(this);
		mDesc.setEnabled(false);
//		mDesc.getFormLabel().setTextColor(0xffa2a6a7);

		mSalary.getInfoEdit().setInputType(EditorInfo.TYPE_CLASS_NUMBER | EditorInfo.TYPE_NUMBER_FLAG_DECIMAL);
		mSalary.getInfoEdit().setFilters(new InputFilter[]{new InputFilter.LengthFilter(2)});//最多二位
		
		if(mListExp.isEmpty()){
			findViewById(R.id.register_jobexp_already_add_layout).setVisibility(View.GONE);
		}else{
			findViewById(R.id.register_jobexp_already_add_layout).setVisibility(View.VISIBLE);
		}
	
		
	}

	@Override
	protected void onResume() {
		// 从个人信息界面过来编辑
		if (mExperience != null && mPersonalEditExperience) {
//			if(StringUtils.isNotEmpty(mExperience.getCorp())){
//				mCompany.getFormContent().setText(mExperience.getCorp());
//			}
//			if(StringUtils.isNotEmpty(mExperience.getPosition())){
//				mName.getFormContent().setText(mExperience.getPosition());
//			}

//			mDesc.setEnabled(true);
			boolean isEnableDesc = true;
			if (!StringUtils.isEmpty(mExperience.getCorp())) {
				mCompany.getFormContent().setText(mExperience.getCorp());
			} else {
				isEnableDesc = false;
			}
			if (!StringUtils.isEmpty(mExperience.getPosition())) {
				mName.getFormContent().setText(mExperience.getPosition());
			} else {
				isEnableDesc = false;
			}
			if(mExperience.getBegin() != null){
				mTime.getFormContent().setText(mExperience.getBeginStr() + "~" + (StringUtils.isEmpty(mExperience.getEndStr()) ? getString(R.string.date_dialog_tonow): mExperience.getEndStr()));
			}
			if(mExperience.getSalary() != null){
				mSalary.getFormContent().setText(mExperience.getSalaryId() + "K");
			}
			mDesc.setEnabled(isEnableDesc);
			if (!StringUtils.isEmpty(mExperience.getDesc())) {
				mDesc.getFormContent().setText(getString(R.string.general_tips_alfill));
			}
		}else{
			// 得到已经存在的工作经历
			mProfileEventLogic.getExperiences();
		}
		super.onResume();
	}
	@Override
	public void chooseRedirectActivity(View view) {
		switch (view.getId()) {
		case R.id.menu_bar_right:
			
			if (mListExp.isEmpty()) {
				Log.i(TAG, "mListExp is empty");
			}
			
			if(mListExp.isEmpty() && mExperience.hasEmptyValue()){
				if(StringUtils.isEmpty(mExperience.getCorp())){
					VToast.show(mContext, getString(R.string.general_tips_fill_tips,mCompany.getFormLabel().getText().toString()));
					return;
				}
				
				if(StringUtils.isEmpty(mExperience.getPosition())){
					VToast.show(mContext, getString(R.string.general_tips_fill_tips,mName.getFormLabel().getText().toString()));
					return;
				}
				
				if(mExperience.getBegin() == null){
					VToast.show(mContext, getString(R.string.general_tips_fill_tips,mTime.getFormLabel().getText().toString()));
					return;
				}
			}
			
			if (!mExperience.isEmpty() && mExperience.hasEmptyValue()) {
				if (mPersonalAddExperience || mPersonalEditExperience) {
					if (mRegisterPersonalExperience) {
						
						
						AlertDialog.Builder builder = new AlertDialog.Builder(JobExperienceListActivity.this);  
				        builder.setTitle(R.string.general_tips);  
				        builder.setMessage(R.string.register_job_experience_message);  
				        builder.setPositiveButton(R.string.btn_confirm,  
				                new DialogInterface.OnClickListener() {  
				                    public void onClick(DialogInterface dialog, int whichButton) {  
				                    	
				                    }  
				                });  
				        builder.setNegativeButton(R.string.btn_cancel,  
				                new DialogInterface.OnClickListener() {  
				                    public void onClick(DialogInterface dialog, int whichButton) {  
				                    	Intent intent = new Intent(JobExperienceListActivity.this,JobObjectiveActivity.class);
										startActivity(intent);
										
										mExperience = new Experience();
										mBundle.putParcelable(GlobalConstants.GUIDE_JOB_EXPERIENCE_OBJECT,mExperience);
										mCompany.reset();
										mName.reset();
										mTime.reset();
										mSalary.reset();
										mDesc.reset();
										mDesc.setEnabled(false);
										mDesc.getFormLabel().setTextColor(0xffa2a6a7);
				                    }  
				                });  
				        builder.show(); 
						
						
					} else {
						AppActivityManager.getInstance().finishActivity(JobExperienceListActivity.this);
						// finish();
					}
				}else{
					if(isShowing){
						return;
					}else{
						isShowing = true;
						Builder builder = new AlertDialog.Builder(this);
						builder.setMessage(R.string.general_tips_edit_close);
						builder.setPositiveButton(getString(R.string.btn_confirm),
								new AlertDialog.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										showPopupWindow();
										mExperience = new Experience();
										mBundle.putParcelable(GlobalConstants.GUIDE_JOB_EXPERIENCE_OBJECT,mExperience);
										mCompany.reset();
										mName.reset();
										mTime.reset();
										mSalary.reset();
										mDesc.reset();
										mDesc.setEnabled(false);
										mDesc.getFormLabel().setTextColor(0xffa2a6a7);
									}
								});
						builder.setNegativeButton(getString(R.string.btn_cancel), null);
						builder.create().setOnDismissListener(new DialogInterface.OnDismissListener() {
							
							@Override
							public void onDismiss(DialogInterface dialog) {
								isShowing = false;
							}
						});
						builder.show();
					}
				}
			} else {
				if (mPersonalAddExperience || mPersonalEditExperience) {
					// 执行上传服务器请求
					if (mPersonalAddExperience) {
						Log.i(TAG,  "mRegisterPersonalExperience mListExp is not empty1");
						if (mRegisterPersonalExperience && !mListExp.isEmpty()) {
							
							Log.i(TAG,  "mRegisterPersonalExperience mListExp is not empty2");
							//  如果是注册页过来的，并且List列表不为空
							if (mExperience != null && !mExperience.hasEmptyValue()) {
								mProfileEventLogic.addExperience(mExperience.getCorp(),
									mExperience.getPosition(), 
									mExperience.getBeginStr(),
									mExperience.getEndStr(), 
									String.valueOf(mExperience.getSalaryId()),
									mExperience.getDesc());
							} else {
								Intent intent = new Intent(JobExperienceListActivity.this,JobObjectiveActivity.class);
								startActivity(intent);
							}
							
						} else {
							// 此时是编辑界面
							if (mExperience != null && !mExperience.hasEmptyValue()) {
								mProfileEventLogic.addExperience(mExperience.getCorp(),
										mExperience.getPosition(), 
										mExperience.getBeginStr(),
										mExperience.getEndStr(), 
										String.valueOf(mExperience.getSalaryId()),
										mExperience.getDesc());
							}
						}
					} else {
						mProfileEventLogic.modifyExperience(mExperience.getId(), 
								mExperience.getCorp(),
								mExperience.getPosition(), 
								mExperience.getBeginStr(),
								mExperience.getEndStr(), 
								String.valueOf(mExperience.getSalaryId()),
								mExperience.getDesc());
					}
				} else {
					showPopupWindow();
				}
				
			}
			break;
		}

	}

	private void showPopupWindow() {
		mPopupWindow.setAnimationStyle(android.R.style.Animation_InputMethod);
		mPopupWindow.setContentView(mPopupLayout);
		mPopupWindow.setHeight(LayoutParams.WRAP_CONTENT);
		mPopupWindow.setWidth(LayoutParams.MATCH_PARENT);
		mPopupWindow.setOutsideTouchable(true);
		mPopupWindow.setFocusable(true);
		WindowManager.LayoutParams lp = getWindow().getAttributes();
		lp.alpha = 0.65f;
		getWindow().setAttributes(lp);
		mPopupWindow.showAtLocation(getWindow().getDecorView().getRootView(), Gravity.BOTTOM, 0, 0);
	}

	@Override
	public void confirmNextAction() {

	}

	@Override
	public void onEventMainThread(Message msg) {
		if (msg.obj instanceof InfoResult) {
			InfoResult infoResult = (InfoResult) msg.obj;
			switch (msg.what) {
			case R.id.register_get_experiences:
				JSONObject experience = (JSONObject) JSONObject.parseObject(infoResult.getResult());
				if(experience != null){
					mListExp.clear();
					JSONArray experienceArray = experience.getJSONArray(GlobalConstants.JSON_WORKEXPRS);
					if(experienceArray!=null){
						for (int i = 0; i < experienceArray.size(); i++) {
							Experience expr = new Experience(experienceArray.getJSONObject(i));
							mListExp.add(expr);
						}
					}
				}

				mJobExpListAdapter.notifyDataSetChanged();
				if(mListExp.isEmpty()){
					findViewById(R.id.register_jobexp_already_add_layout).setVisibility(View.GONE);
				}else{
					findViewById(R.id.register_jobexp_already_add_layout).setVisibility(View.VISIBLE);
				}
				break;
			case R.id.profile_workexpr_add:
				if (infoResult.getMessage().getStatus() == 0) {
					// 如果是从注册页过来的
					if (mRegisterPersonalExperience) {
						
						Intent intent = new Intent(JobExperienceListActivity.this,JobObjectiveActivity.class);
						startActivity(intent);
						
						mExperience = new Experience();
						mBundle.putParcelable(GlobalConstants.GUIDE_JOB_EXPERIENCE_OBJECT,mExperience);
						mCompany.reset();
						mName.reset();
						mTime.reset();
						mSalary.reset();
						mDesc.reset();
						mDesc.setEnabled(false);
						mDesc.getFormLabel().setTextColor(0xffa2a6a7);
						
					} else {
						// 如果是从个人信息编辑页过来的
						if (mPersonalAddExperience) {
							AppActivityManager.getInstance().finishActivity(JobExperienceListActivity.this);
						} 
					}
					GeneralDbManager.getInstance().setWorkPerform(1);
					// 得到已经存在的工作经历
					mProfileEventLogic.getExperiences();
				}
				VToast.show(this, infoResult.getMessage().getMsg());
				break;
				
			case R.id.profile_workexpr_modify:
				if (infoResult.getMessage().getStatus() == 0) {
					// 如果是从个人信息编辑页过来的
					if (mPersonalEditExperience) {
						AppActivityManager.getInstance().finishActivity(JobExperienceListActivity.this);
						// finish();
					} 
				}
				VToast.show(this, infoResult.getMessage().getMsg());
				break;
			case R.id.profile_workexpr_delete:
				// 此时删除成功，将删除的数据从Adapter中删除
				if(infoResult.getMessage().getStatus() == 0){
					for(int i=0; i < mListExp.size(); i++) {
						if(mListExp.get(i).getId().equals(curOptrId)) {
							mListExp.remove(i);
						}
					}
					GeneralDbManager.getInstance().setWorkPerform(mListExp.isEmpty() ? 0 : 1);
					// 更新Adapter
					mJobExpListAdapter.notifyDataSetChanged();
				}
				VToast.show(JobExperienceListActivity.this, infoResult.getMessage().getMsg());
				break;
			}
		}
	}

	// 需要注意一下intent的launch mode
	@Override
	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
		case R.id.popup_jobexp_on:
			
			if (mExperience != null && !mExperience.hasEmptyValue()) {
				mProfileEventLogic.addExperience(mExperience.getCorp(),
						mExperience.getPosition(), 
						mExperience.getBeginStr(),
						mExperience.getEndStr(), 
						String.valueOf(mExperience.getSalaryId()),
						mExperience.getDesc());
				mListExp.add(mExperience);
				mJobExpListAdapter.notifyDataSetChanged();
			}
			if(mListExp.isEmpty()){
				findViewById(R.id.register_jobexp_already_add_layout).setVisibility(View.GONE);
			}else{
				findViewById(R.id.register_jobexp_already_add_layout).setVisibility(View.VISIBLE);
			}
			mExperience = new Experience();
			mBundle.putParcelable(GlobalConstants.GUIDE_JOB_EXPERIENCE_OBJECT,mExperience);
			mCompany.reset();
			mName.reset();
			mTime.reset();
			mSalary.reset();
			mDesc.reset();
			mDesc.setEnabled(false);
			mDesc.getFormLabel().setTextColor(0xffa2a6a7);
			mPopupWindow.dismiss();
			break;
		case R.id.popup_jobexp_next:
			if (mExperience != null && !mExperience.hasEmptyValue()) {
				mProfileEventLogic.addExperience(mExperience.getCorp(),
						mExperience.getPosition(), 
						mExperience.getBeginStr(),
						mExperience.getEndStr(),
						String.valueOf(mExperience.getSalaryId()),
						mExperience.getDesc());
				mListExp.add(mExperience);
				mJobExpListAdapter.notifyDataSetChanged();
			}
			if(mListExp.isEmpty()){
				findViewById(R.id.register_jobexp_already_add_layout).setVisibility(View.GONE);
			}else{
				findViewById(R.id.register_jobexp_already_add_layout).setVisibility(View.VISIBLE);
			}
			mExperience = new Experience();
			mBundle.putParcelable(GlobalConstants.GUIDE_JOB_EXPERIENCE_OBJECT,mExperience);
			mCompany.reset();
			mName.reset();
			mTime.reset();
			mSalary.reset();
			mDesc.reset();
			mDesc.setEnabled(false);
			mDesc.getFormLabel().setTextColor(0xffa2a6a7);
			intent = getIntent();
			if (ActivityQueue.hasNext(JobExperienceListActivity.class)){
				next = ActivityQueue.next(JobExperienceListActivity.class);
				intent.setClass(this, next);
				startActivity(intent);
			}
			break;
		case R.id.register_jobexp_salary:
			
			mSalary.setEdit(true);
			mSalary.setCompleteListener(new OnEditCompleteListener() {
				@Override
				public void onEditComplete() {
					String salary = mSalary.getInfoEdit().getText().toString();
					if(StringUtils.isNotEmpty(salary)){
						mSalary.setFormContent(salary + "K");
						JSONObject jsonObject = new JSONObject();
						jsonObject.put(GlobalConstants.JSON_ID, Integer.parseInt(salary));
						mExperience.setSalary(jsonObject);
					}else{
						mSalary.setFormContent("");
						mExperience.setSalary(null);
					}

				}
			});
			break;
		case R.id.register_jobexp_time:
			if(isShowing){
				return;
			}else{
				isShowing = true;
				mDateDialog = new DateDialog(this);
				if(mExperience.getBegin() != null){
					mDateDialog.setFrontCurYearNum(mExperience.getBeginYear());
					mDateDialog.setFrontCurMonthNum(mExperience.getBeginMonth());
				}
				if(mExperience.getEnd() == null){
					mDateDialog.setBackCurYearNum(mDateDialog.getBackYearToNow());
					mDateDialog.setBackCurMonthNum(mDateDialog.getBackMonthToNow());
				}else{
					mDateDialog.setBackCurYearNum(mExperience.getEndYear());
					mDateDialog.setBackCurMonthNum(mExperience.getEndMonth());
				}

				mDateDialog.setDateSetListener(new DateDialog.OnDateSetListener() {
					
					@Override
					public void onDateSet(int frontYear, int frontMonth, int backYear,
							int backMonth, boolean backToNow) {
						
						JSONObject begin = new JSONObject();
						begin.put(GlobalConstants.JSON_YEAR, frontYear);
						begin.put(GlobalConstants.JSON_MONTH, frontMonth);
						mExperience.setBegin(begin);
						
						StringBuilder time = new StringBuilder();
						time.append(mExperience.getBeginStr());
						time.append("~");
						if(backToNow){
							time.append(getString(R.string.date_dialog_tonow));
							mExperience.setEnd(null);//传空表示至今
						}else{
							JSONObject end = new JSONObject();
							end.put(GlobalConstants.JSON_YEAR, backYear);
							end.put(GlobalConstants.JSON_MONTH, backMonth);
							mExperience.setEnd(end);
							time.append(mExperience.getEndStr());
						}
						mTime.getFormContent().setText(time.toString());
					}
				});
				mDateDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
					
					@Override
					public void onDismiss(DialogInterface dialog) {
						isShowing = false;
					}
				});
				mDateDialog.show();
			}
			break;
		case R.id.register_jobexp_company:
		case R.id.register_jobexp_name:
		case R.id.register_jobexp_desc:
			intent = new Intent(this, JobExperienceSingleActivity.class);
			intent.putExtras(mBundle);
			intent.putExtra(GlobalConstants.SEQ_STRING, v.getId());
			startActivityForResult(intent, 1);
			break;
	

		default:
			break;
		}
	}
	
	class JobExpListAdapter extends CommonAdapter<Experience> {

		public JobExpListAdapter(Context mContext, List<Experience> mDatas,
				int mItemLayoutId) {
			super(mContext, mDatas, mItemLayoutId);
		}
		
		@Override
		public void convert(ViewHolder viewHolder, Experience item, int position) {
			FormItemByLineView layout = (FormItemByLineView) viewHolder.getView(R.id.content);
			StringBuilder time = new StringBuilder();
			time.append(item.getBeginStr());
			time.append("~");
			if(StringUtils.isEmpty(item.getEndStr())){
				time.append(getString(R.string.date_dialog_tonow));
			}else{
				time.append(item.getEndStr());
			}
			layout.setFormLabel(time.toString());
			layout.getFormContent().setTextColor(getResources().getColor(R.color.general_color_666666));
			layout.setFormContent(item.getPosition());
			
			if(position == getCount() - 1){
				viewHolder.getView(R.id.divider).setVisibility(View.GONE);
			}else{
				viewHolder.getView(R.id.divider).setVisibility(View.VISIBLE);
			}
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case 1:
				mBundle = data.getExtras();
				mExperience = (Experience) mBundle.getParcelable(GlobalConstants.GUIDE_JOB_EXPERIENCE_OBJECT);
				boolean isEnableDesc = true;
				if (!StringUtils.isEmpty(mExperience.getCorp())) {
					mCompany.getFormContent().setText(mExperience.getCorp());
				} else {
					mCompany.reset();
					isEnableDesc = false;
				}
				if (!StringUtils.isEmpty(mExperience.getPosition())) {
					mName.getFormContent().setText(mExperience.getPosition());
				} else {
					mName.reset();
					isEnableDesc = false;
				}
				mDesc.setEnabled(isEnableDesc);
				mDesc.getFormLabel().setTextColor(getResources().getColor(R.color.general_color_1A2138));
				if (!StringUtils.isEmpty(mExperience.getDesc())) {
					mDesc.reset();
					mDesc.getFormContent().setText(getString(R.string.general_tips_alfill));
				}

				break;
			default:
				break;
			}
		}
	}


	@Override
	protected void onPause() {
		if (mPopupWindow.isShowing())
			mPopupWindow.dismiss();
		super.onPause();
	}
	
	private String curOptrId;
	
	/**
	 * JobExperience item LongClick
	 */
	private OnItemClickListener mExpItemClick = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view,
				final int position, long id) {
			final Experience experience = mListExp.get(position);
			if(experience.getCertify() != null && experience.getCertify() == 1){
				VToast.show(JobExperienceListActivity.this, getString(R.string.unjobs));
				return;
			}
			final String[] clickActions = getResources().getStringArray(
					R.array.long_click_action);
			AlertDialog.Builder builder = new AlertDialog.Builder(JobExperienceListActivity.this);
			builder.setTitle(getResources().getString(R.string.user_guide_job_detail_center)).setItems(
					clickActions, new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							
							if(StringUtils.isEmpty(experience.getId())){
								return;
							}
							if (which == 0) {
								// 编辑
								Intent intent = new Intent(JobExperienceListActivity.this,JobExperienceListActivity.class);
								intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								Bundle bundle = new Bundle();
							    bundle.putParcelable(GlobalConstants.GUIDE_JOB_EXPERIENCE_OBJECT, experience);
							    intent.putExtra("personal_experience_edit", true);
								intent.putExtras(bundle);
								startActivity(intent);
							} else {
								// delete job experience
								curOptrId = experience.getId();
								mProfileEventLogic.deleteExperience(experience.getId());
								
							}
						}
					});
			AlertDialog dialog = builder.create();
			dialog.setCancelable(true);
			dialog.setCanceledOnTouchOutside(true);
			dialog.show();
		}
	};
}
