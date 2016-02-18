package com.xiaobukuaipao.vzhi.profile.eduexp;

import java.util.ArrayList;
import java.util.Calendar;
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
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.ScrollView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xiaobukuaipao.vzhi.AppActivityManager;
import com.xiaobukuaipao.vzhi.R;
import com.xiaobukuaipao.vzhi.adapter.CommonAdapter;
import com.xiaobukuaipao.vzhi.adapter.ViewHolder;
import com.xiaobukuaipao.vzhi.domain.user.Education;
import com.xiaobukuaipao.vzhi.event.InfoResult;
import com.xiaobukuaipao.vzhi.util.ActivityQueue;
import com.xiaobukuaipao.vzhi.util.GlobalConstants;
import com.xiaobukuaipao.vzhi.util.Logcat;
import com.xiaobukuaipao.vzhi.util.StringUtils;
import com.xiaobukuaipao.vzhi.util.VToast;
import com.xiaobukuaipao.vzhi.view.ListViewForScrollView;
import com.xiaobukuaipao.vzhi.widget.DateDialog;
import com.xiaobukuaipao.vzhi.widget.FormItemByLineView;
import com.xiaobukuaipao.vzhi.wrap.ProfileWrapActivity;

import de.greenrobot.event.EventBus;

public class EducationListActivity extends ProfileWrapActivity implements
		OnClickListener {

	private ListViewForScrollView mAlreadyList;
	private PopupWindow mPopupWindow;
	private LinearLayout mPopupLayout;
	private List<Education> mListEdu;
	private EduListAdapter mEduListAdapter;

	private FormItemByLineView mDegree;
	private FormItemByLineView mMajor;
	private FormItemByLineView mSchool;
	private FormItemByLineView mTime;

	private DateDialog mDateDialog;

	private Education mEducation;
	private Bundle mBundle;
	private Class<?> next;

	// 从个人信息页面过来添加工作经历
	private boolean mPersonalAddEducation;
	// 从个人信息页面过来编辑工作信息
	private boolean mPersonalEditEducation;
		
	private boolean mPersonalCompleteEducation;
	private Context mContext;
	
	private boolean isShowing = false;
	
	private int mEditPoint = -1;
	@Override
	public void initUIAndData() {
		setContentView(R.layout.activity_education);
		
		mContext = this;
		mPersonalAddEducation = getIntent().getBooleanExtra("personal_education_add", false);
		mPersonalEditEducation = getIntent().getBooleanExtra("personal_education_edit", false);
		mPersonalCompleteEducation = getIntent().getBooleanExtra("personal_education_complete", false);
		mBundle = getIntent().getExtras();
		if(mPersonalCompleteEducation){
			if (mBundle == null) {
				mBundle = new Bundle();
			} 
			mEducation = new Education();
			mBundle.putParcelable(GlobalConstants.GUIDE_EDUCATION_OBJECT, mEducation);
		}else {
			if (mPersonalAddEducation) {
				// 此时是增加工作经验
				mBundle.clear();
				mEducation = new Education();
				mBundle.putParcelable(GlobalConstants.GUIDE_EDUCATION_OBJECT, mEducation);
			} else {
				// 此时是编辑当前工作经历
				mEducation = mBundle.getParcelable(GlobalConstants.GUIDE_EDUCATION_OBJECT);
			}
		}
		
		setHeaderMenuByLeft(this);
		setHeaderMenuByCenterTitle(R.string.register_edu_complete);
		
		// 判断是下一步还是保存
		if (mPersonalAddEducation || mPersonalEditEducation) {
			setHeaderMenuByRight(R.string.general_save);
		} else {
			setHeaderMenuByRight(R.string.general_tips_next);
		}
		
		((ScrollView) findViewById(R.id.scrollview)).smoothScrollTo(0, 0);
		mPopupLayout = (LinearLayout) View.inflate(this,R.layout.popup_edu_next, null);
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

		mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {

			@Override
			public void onDismiss() {
				WindowManager.LayoutParams lp = getWindow().getAttributes();
				lp.alpha = 1.0f;
				getWindow().setAttributes(lp);
			}
		});

		mPopupLayout.findViewById(R.id.popup_edu_on).setOnClickListener(this);
		mPopupLayout.findViewById(R.id.popup_edu_next).setOnClickListener(this);
		// 已经添加的教育经历
		mAlreadyList = (ListViewForScrollView) findViewById(R.id.register_edu_already_add);
		// 已经添加的教育经历历List列表
		mListEdu = new ArrayList<Education>();

		mEduListAdapter = new EduListAdapter(this, mListEdu, R.layout.item_edu);
		mAlreadyList.setAdapter(mEduListAdapter);
		mAlreadyList.setEmptyView(findViewById(R.id.empty_view));

		mMajor = (FormItemByLineView) findViewById(R.id.register_edu_major_id);
		mSchool = (FormItemByLineView) findViewById(R.id.register_edu_school_id);
		mDegree = (FormItemByLineView) findViewById(R.id.register_edu_degree_id);
		mTime = (FormItemByLineView) findViewById(R.id.register_edu_time_id);
		mMajor.setOnClickListener(this);
		mSchool.setOnClickListener(this);
		mDegree.setOnClickListener(this);
		mTime.setOnClickListener(this);
		
		
		// 从个人信息界面过来编辑
		if (mEducation != null && mPersonalEditEducation) {
			mMajor.getFormContent().setText(mEducation.getMajor().getName());
			mSchool.getFormContent().setText(mEducation.getSchool().getName());
			mTime.getFormContent().setText(mEducation.getBeginStr() + "~" + (mEducation.getEnd() != null ? mEducation.getEndStr() : getString(R.string.date_dialog_tonow)));
			mDegree.getFormContent().setText(mEducation.getDegree().getName());
		}else{
			// 得到已经存在的工作经历
			mProfileEventLogic.getEducations();
		}
		if(mListEdu.isEmpty()){
			findViewById(R.id.register_edu_already_add_layout).setVisibility(View.GONE);
		}else{
			findViewById(R.id.register_edu_already_add_layout).setVisibility(View.VISIBLE);
		}
		
		mAlreadyList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					final int position, long id) {
				final String[] clickActions = getResources().getStringArray(R.array.long_click_action);
				AlertDialog.Builder builder = new AlertDialog.Builder(EducationListActivity.this);
				builder.setTitle(EducationListActivity.this.getResources().getString(R.string.register_edu_str))
						.setItems(clickActions,new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										
										Education education = mListEdu.get(position);
										if(StringUtils.isEmpty(education.getId())){
											return;
										}
										if (which == 0) {
											mEditPoint = position;
											Logcat.d("@@@", "modify degree 1:" + education.getDegree().getName());
											// 编辑
											Intent intent = new Intent(EducationListActivity.this, EducationListActivity.class);
											intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
											Bundle bundle = new Bundle();
										    bundle.putParcelable(GlobalConstants.GUIDE_EDUCATION_OBJECT, education);
										    intent.putExtra("personal_education_edit", true);
											intent.putExtras(bundle);
											startActivity(intent);
										} else {
											// delete job experience
											curOptrId = education.getId();
											mProfileEventLogic.deleteEducation(education.getId());
										}
										
									}

								});
				AlertDialog dialog = builder.create();
				dialog.setCancelable(true);
				dialog.setCanceledOnTouchOutside(true);
				dialog.show();
				
			}

		});
		EventBus.getDefault().register(this);
	}

	private String curOptrId;
	@Override
	public void onEventMainThread(Message msg) {

		if (msg.obj instanceof InfoResult) {
			InfoResult infoResult = (InfoResult) msg.obj;
			switch (msg.what) {
			case R.id.profile_edu_add:
				if (infoResult.getMessage().getStatus() == 0) {
					// 如果是从个人信息编辑页过来的
					if (mPersonalAddEducation) {
						AppActivityManager.getInstance().finishActivity(EducationListActivity.this);
						// finish();
					} 
					// 得到已经存在的工作经历
					mProfileEventLogic.getEducations();
				}
				VToast.show(this, infoResult.getMessage().getMsg());
				break;
				
			case R.id.profile_edu_modify:
				if (infoResult.getMessage().getStatus() == 0) {
					// 如果是从个人信息编辑页过来的
					if (mPersonalEditEducation) {
						Logcat.d("@@@", "modify degree 2:" + mEducation.getDegree().getName());
						EventBus.getDefault().post(mEducation);
						AppActivityManager.getInstance().finishActivity(EducationListActivity.this);
						
					} 
				}
				VToast.show(this, infoResult.getMessage().getMsg());
				
				break;
			case R.id.register_get_educations:
				JSONObject education = (JSONObject) JSONObject.parseObject(infoResult.getResult());
				if(education != null){
					mListEdu.clear();
					JSONArray educationArray = education.getJSONArray(GlobalConstants.JSON_EDUEXPRS);
					if(educationArray!=null){
						for (int i = 0; i < educationArray.size(); i++) {
							Education expr = new Education(educationArray.getJSONObject(i));
							mListEdu.add(expr);
						}
					}
					if(mListEdu.isEmpty()){
						findViewById(R.id.register_edu_already_add_layout).setVisibility(View.GONE);
					}else{
						findViewById(R.id.register_edu_already_add_layout).setVisibility(View.VISIBLE);
					}
					mEduListAdapter.notifyDataSetChanged();
				}
				break;
			case R.id.profile_edu_delete:
				if(infoResult.getMessage().getStatus() == 0 ){
					for(int i=0; i < mListEdu.size(); i++) {
						if(mListEdu.get(i).getId().equals(curOptrId)) {
							mListEdu.remove(i);
						}
					}
					// 更新Adapter
					mEduListAdapter.notifyDataSetChanged();
				}else{
					
				}
				VToast.show(EducationListActivity.this,infoResult.getMessage().getMsg());
				break;
			}
		}
	}

	@Override
	public void chooseRedirectActivity(View view) {
		switch (view.getId()) {
		case R.id.menu_bar_right:
			if(mListEdu.isEmpty() && mEducation.hasEmptyValue()){
				if(mEducation.getDegree() == null ||StringUtils.isEmpty(mEducation.getDegree().getName())){
					VToast.show(mContext, getString(R.string.general_tips_fill_tips,mDegree.getFormLabel().getText().toString()));
					return;
				}
				
				if(mEducation.getSchool() == null ||StringUtils.isEmpty(mEducation.getSchool().getName())){
					VToast.show(mContext, getString(R.string.general_tips_fill_tips,mSchool.getFormLabel().getText().toString()));
					return;
				}
				
				if(mEducation.getMajor() == null || StringUtils.isEmpty(mEducation.getMajor().getName())){
					VToast.show(mContext, getString(R.string.general_tips_fill_tips,mMajor.getFormLabel().getText().toString()));
					return;
				}
				
				if(mEducation.getBegin() != null){
					VToast.show(mContext, getString(R.string.general_tips_fill_tips,mTime.getFormLabel().getText().toString()));
					return;
				}
			}
			if (!mEducation.isEmpty() && mEducation.hasEmptyValue()) {
				Builder builder = new AlertDialog.Builder(this);
				builder.setMessage(R.string.general_tips_edit_close);
				builder.setPositiveButton(getString(R.string.btn_confirm),
						new AlertDialog.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,int which) {
								showPopupWindow();
								mEducation = new Education();
								mBundle.putParcelable(GlobalConstants.GUIDE_EDUCATION_OBJECT, mEducation);
								mDegree.reset();
								mSchool.reset();
								mMajor.reset();
								mTime.reset();
							}
						});
				builder.setNegativeButton(getString(R.string.btn_cancel), null);
				builder.show();
			} else {
				if (mPersonalAddEducation || mPersonalEditEducation) {
					// 执行上传服务器请求
					if (mPersonalAddEducation) {
						// 此时是编辑界面
						if (mEducation != null && !mEducation.hasEmptyValue()) {
							mProfileEventLogic.addEducation(
									!StringUtils.isEmpty(mEducation.getSchool().getId()) ? mEducation.getSchool().getId() : mEducation.getSchool().getName(),
									!StringUtils.isEmpty(mEducation.getMajor().getId()) ? mEducation.getMajor().getId() : mEducation.getMajor().getName(),
									mEducation.getBeginStr(), 
									mEducation.getEnd() != null ? mEducation.getEndStr() : "",
									mEducation.getDegree().getId());
						}
					} else {
						// 修改界面
						mProfileEventLogic.modifyEducation(mEducation.getId(), 
								!StringUtils.isEmpty(mEducation.getSchool().getId()) ? mEducation.getSchool().getId() : mEducation.getSchool().getName(),
								!StringUtils.isEmpty(mEducation.getMajor().getId()) ? mEducation.getMajor().getId() : mEducation.getMajor().getName(),
								mEducation.getBeginStr(), 
								mEducation.getEnd() != null ? mEducation.getEndStr() : "",
								mEducation.getDegree().getId());
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
		mPopupWindow.showAtLocation(getWindow().getDecorView().getRootView(),
				Gravity.BOTTOM, 0, 0);
	}

	@Override
	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
		case R.id.popup_edu_on:
			if (mEducation != null && !mEducation.hasEmptyValue()) {
				mProfileEventLogic.addEducation(
						!StringUtils.isEmpty(mEducation.getSchool().getId()) ? mEducation.getSchool().getId() : mEducation.getSchool().getName(),
						!StringUtils.isEmpty(mEducation.getMajor().getId()) ? mEducation.getMajor().getId() : mEducation.getMajor().getName(),
						mEducation.getBeginStr(), 
						mEducation.getEnd() != null ? mEducation.getEndStr() : "",
						mEducation.getDegree().getId());
				mListEdu.add(mEducation);
				mEduListAdapter.notifyDataSetChanged();
			}
			if(mListEdu.isEmpty()){
				findViewById(R.id.register_edu_already_add_layout).setVisibility(View.GONE);
			}else{
				findViewById(R.id.register_edu_already_add_layout).setVisibility(View.VISIBLE);
			}
			mEducation = new Education();
			mBundle.putParcelable(GlobalConstants.GUIDE_EDUCATION_OBJECT,mEducation);
			mDegree.reset();
			mSchool.reset();
			mMajor.reset();
			mTime.reset();
			mPopupWindow.dismiss();
			break;
		case R.id.popup_edu_next:
			if (mEducation != null && !mEducation.hasEmptyValue()) {
				mProfileEventLogic.addEducation(
						!StringUtils.isEmpty(mEducation.getSchool().getId()) ? mEducation.getSchool().getId() : mEducation.getSchool().getName(),
						!StringUtils.isEmpty(mEducation.getMajor().getId()) ? mEducation.getMajor().getId() : mEducation.getMajor().getName(),
						mEducation.getBeginStr(), 
						mEducation.getEnd() != null ? mEducation.getEndStr() : "",
						mEducation.getDegree().getId());
				mListEdu.add(mEducation);
				mEduListAdapter.notifyDataSetChanged();
			}
			if(mListEdu.isEmpty()){
				findViewById(R.id.register_edu_already_add_layout).setVisibility(View.GONE);
			}else{
				findViewById(R.id.register_edu_already_add_layout).setVisibility(View.VISIBLE);
			}
			mEducation = new Education();
			mBundle.putParcelable(GlobalConstants.GUIDE_EDUCATION_OBJECT,mEducation);
			mDegree.reset();
			mSchool.reset();
			mMajor.reset();
			mTime.reset();
			intent = getIntent();
			if (ActivityQueue.hasNext(EducationListActivity.class)){
				next = ActivityQueue.next(EducationListActivity.class);
				intent.setClass(this, next);
				startActivity(intent);
			}

			break;
		case R.id.register_edu_degree_id:
		case R.id.register_edu_school_id:
		case R.id.register_edu_major_id:
			intent = new Intent(EducationListActivity.this,EducationSingleActivity.class);
			intent.putExtras(mBundle);
			intent.putExtra(GlobalConstants.SEQ_STRING, v.getId());
			startActivityForResult(intent, 1);
			break;
		case R.id.register_edu_time_id:
			if(isShowing){
				return;
			}else{
				isShowing = true;
				mDateDialog = new DateDialog(this);
				if(mEducation.getBegin() != null){
					mDateDialog.setFrontCurYearNum(mEducation.getBeginYear());
					mDateDialog.setFrontCurMonthNum(mEducation.getBeginMonth());
				}else{//设置默认开始的年月
					mDateDialog.setFrontCurMonthNum(9);
					mDateDialog.setFrontCurYearNum(Calendar.getInstance().get(Calendar.YEAR) - 4);
				}

				if(mEducation.getEnd() != null){
					mDateDialog.setBackCurYearNum(mEducation.getEndYear());
					mDateDialog.setBackCurMonthNum(mEducation.getEndMonth());
				}else{//设置默认结束的年月
					mDateDialog.setBackCurYearNum(Calendar.getInstance().get(Calendar.YEAR));
					mDateDialog.setBackCurMonthNum(7);
				}
				
				mDateDialog.setDateSetListener(new DateDialog.OnDateSetListener() {
					
					@Override
					public void onDateSet(int frontYear, int frontMonth, int backYear,
							int backMonth, boolean backToNow) {
						
						JSONObject begin = new JSONObject();
						begin.put(GlobalConstants.JSON_YEAR, frontYear);
						begin.put(GlobalConstants.JSON_MONTH, frontMonth);
						mEducation.setBegin(begin);
						
						StringBuilder time = new StringBuilder();
						time.append(mEducation.getBeginStr());
						time.append("~");
						if(backToNow){
							time.append(getString(R.string.date_dialog_tonow));
							mEducation.setEnd(null);//传空表示至今
						}else{
							JSONObject end = new JSONObject();
							end.put(GlobalConstants.JSON_YEAR, backYear);
							end.put(GlobalConstants.JSON_MONTH, backMonth);
							mEducation.setEnd(end);
							time.append(mEducation.getEndStr());
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
		default:
			break;
		}

	}

	class EduListAdapter extends CommonAdapter<Education> {

		public EduListAdapter(Context mContext, List<Education> mDatas,
				int mItemLayoutId) {
			super(mContext, mDatas, mItemLayoutId);
		}

		@Override
		public void convert(ViewHolder viewHolder, Education item, int position) {
			FormItemByLineView time = (FormItemByLineView) viewHolder.getView(R.id.register_edu_time);
			time.setFormLabel(item.getBeginStr() + "~" + (item.getEnd() != null ? item.getEndStr(): getString(R.string.date_dialog_tonow)));

			FormItemByLineView schoolAndDegree = (FormItemByLineView) viewHolder.getView(R.id.register_edu_school_degree);
			schoolAndDegree.setFormLabel(item.getSchool().getName());
			schoolAndDegree.setFormContent(item.getDegree().getName());
			schoolAndDegree.getFormContent().setTextColor(getResources().getColor(R.color.black));
			schoolAndDegree.getFormContent().setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);

			FormItemByLineView major = (FormItemByLineView) viewHolder.getView(R.id.register_edu_major);
			major.setFormLabel(item.getMajor().getName());
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case 1:
				mBundle = data.getExtras();
				mEducation = (Education) mBundle.getParcelable(GlobalConstants.GUIDE_EDUCATION_OBJECT);

				if (mEducation.getDegree() != null && !StringUtils.isEmpty(mEducation.getDegree().getName())) {
					mDegree.getFormContent().setTextColor(getResources().getColor(R.color.black));
					mDegree.getFormContent().setText(mEducation.getDegree().getName());
				}else{
					mDegree.reset();
				}

				if (mEducation.getSchool() != null && !StringUtils.isEmpty(mEducation.getSchool().getName())) {
					mSchool.getFormContent().setTextColor(getResources().getColor(R.color.black));
					mSchool.getFormContent().setText(mEducation.getSchool().getName());
				}else{
					mSchool.reset();
				}
				
				if (mEducation.getMajor() != null && !StringUtils.isEmpty(mEducation.getMajor().getName())) {
					mMajor.getFormContent().setTextColor(getResources().getColor(R.color.black));
					mMajor.getFormContent().setText(mEducation.getMajor().getName());
				}else{
					mMajor.reset();
				}
				
				break;
			default:
				break;
			}
		}
	}

	public void onEvent(Education edutcation){
		Logcat.d("@@@", "modify degree 3:" + edutcation.getDegree().getName());
		mListEdu.set(mEditPoint, edutcation);
		mEduListAdapter.notifyDataSetChanged();
		mEditPoint = -1;
	}
	
	
	@Override
	public void confirmNextAction() {

	}


	@Override
	protected void onPause() {
		if (mPopupWindow.isShowing())
			mPopupWindow.dismiss();
		super.onPause();
	}

	@Override
	protected void onDestroy() {

		EventBus.getDefault().unregister(this);
		super.onDestroy();
	}
}
