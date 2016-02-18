package com.xiaobukuaipao.vzhi;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeMap;

import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.android.volley.toolbox.Volley;
import com.xiaobukuaipao.vzhi.cache.CommonBitmapMemoryCache;
import com.xiaobukuaipao.vzhi.domain.CorpInfo;
import com.xiaobukuaipao.vzhi.domain.HrInfo;
import com.xiaobukuaipao.vzhi.domain.JobDetailInfo;
import com.xiaobukuaipao.vzhi.domain.PublisherInfo;
import com.xiaobukuaipao.vzhi.event.InfoResult;
import com.xiaobukuaipao.vzhi.register.RegisterResidentSearchActivity;
import com.xiaobukuaipao.vzhi.util.GlobalConstants;
import com.xiaobukuaipao.vzhi.util.StringUtils;
import com.xiaobukuaipao.vzhi.util.VToast;
import com.xiaobukuaipao.vzhi.view.LinesTextView;
import com.xiaobukuaipao.vzhi.view.LoadingDialog;
import com.xiaobukuaipao.vzhi.widget.DoubleSalaryNumberDialog;
import com.xiaobukuaipao.vzhi.widget.FormItemByLineLayout;
import com.xiaobukuaipao.vzhi.widget.FormItemByLineLayout.OnEditCompleteListener;
import com.xiaobukuaipao.vzhi.widget.FormItemByLineView;
import com.xiaobukuaipao.vzhi.widget.NumberPicker;
import com.xiaobukuaipao.vzhi.widget.NumberPicker.OnValueChangeListener;
import com.xiaobukuaipao.vzhi.widget.SimpleNumberDialog;
import com.xiaobukuaipao.vzhi.widget.SimpleSalaryNumberDialog;
import com.xiaobukuaipao.vzhi.wrap.ProfileWrapActivity;

/**
 * 编辑职位信息
 * @since 2015年01月07日16:49:00
 */
public class PublishPositionActivity extends ProfileWrapActivity implements OnClickListener {
	
	public static final String TAG = PublishPositionActivity.class.getSimpleName();
	
	private static final int REQUEST_CODE_POSITION = 1;
	private static final int REQUEST_CODE_CITY = 2;
	
	private ImageView mCorpLogo;
	private TextView mCorpName;
	private LinesTextView mBenefits;
	private ScrollView mPositionLayout;
	
	
	private FormItemByLineView mPositionName;
	private FormItemByLineView mSalary;
	private FormItemByLineLayout mRecruitNum;
	private FormItemByLineView mCity;
	private FormItemByLineView mJobExperience;
	private FormItemByLineView mEdu;
	private FormItemByLineView mHighLights;
	private FormItemByLineView mPositionDesc;
	private FormItemByLineView mNotifyEmail;
	
	private DoubleSalaryNumberDialog mSalaryPicker;
	
	private int mJobId = -1;
	
	private CorpInfo mCorpInfo;
	private PublisherInfo mPublisherInfo;
	private JobDetailInfo mJobDetailInfo;
	
	// 请求队列
	private RequestQueue mQueue;
	private ImageLoader mImageLoader;
	private ImageListener mListener;
	
	private int headCount = -1;
	
	private int beginSalary = -1;
	private int endSalary = -1;
	private int exprIndex = 0;
	private String[] exprArray;
	private int eduIndex = 0;
	private String[] eduArray;
	
	private TreeMap<Integer, String> exprMap;
	private TreeMap<Integer, String> eduMap;
	
	private String highLight;
	private String desc;
	
	private List<String> mBenefitsList;
	
	private LoadingDialog loadingDialog;
	/**
	 * 手动给map排序
	 */
	private Comparator<Integer> comparator = new Comparator<Integer>() {

		@Override
		public int compare(Integer lhs, Integer rhs) {
			
			return lhs > rhs ? -1:1;
		}
	};
	@Override
	public void initUIAndData() {
		setContentView(R.layout.activity_publish_position);
		setHeaderMenuByCenterTitle(R.string.publish_position);
		setHeaderMenuByRight(R.string.general_tips_finished).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(StringUtils.isEmpty(mPositionName.getFormContentText())){
					VToast.show(PublishPositionActivity.this, getString(R.string.general_tips_fill_tips,mPositionName.getFormLabelText()));
					return;
				}
				if(StringUtils.isEmpty(mSalary.getFormContentText())){
					VToast.show(PublishPositionActivity.this, getString(R.string.general_tips_fill_tips,mSalary.getFormLabelText()));
					return;
				}
				if(StringUtils.isEmpty(mRecruitNum.getFormContentText())){
					VToast.show(PublishPositionActivity.this, getString(R.string.general_tips_fill_tips,mRecruitNum.getFormLabelText()));
					return;
				}
				if(StringUtils.isEmpty(mCity.getFormContentText())){
					VToast.show(PublishPositionActivity.this, getString(R.string.general_tips_fill_tips,mCity.getFormLabelText()));
					return;
				}
				if(StringUtils.isEmpty(mEdu.getFormContentText())){
					VToast.show(PublishPositionActivity.this, getString(R.string.general_tips_fill_tips,mEdu.getFormLabelText()));
					return;
				}
//				if(StringUtils.isEmpty(mHighLights.getFormContentText())){
//					VToast.show(PublishPositionActivity.this, getString(R.string.general_tips_fill_tips,mHighLights.getFormLabelText()));
//					return;
//				}
				if(StringUtils.isEmpty(mPositionDesc.getFormContentText())){
					VToast.show(PublishPositionActivity.this, getString(R.string.general_tips_fill_tips,mPositionDesc.getFormLabelText()));
					return;
				}
				loadingDialog.show();
				if(mJobId != -1){
					mPositionEventLogic.updatePosition(String.valueOf(mJobId),mPositionName.getFormContentText(),
							beginSalary, endSalary,
							String.valueOf(exprMap.keySet().toArray(new Integer[]{})[exprIndex]), 
							mCity.getFormContentText(),
							String.valueOf(eduMap.keySet().toArray(new Integer[]{})[eduIndex]), 
							highLight, desc, headCount,mCorpInfo.getIndustryId());
				}else{
					mPositionEventLogic.createPosition(mPositionName.getFormContentText(),
							beginSalary, endSalary,
							String.valueOf(exprMap.keySet().toArray(new Integer[]{})[exprIndex]), 
							mCity.getFormContentText(),
							String.valueOf(eduMap.keySet().toArray(new Integer[]{})[eduIndex]), 
							highLight, desc, headCount);
				}
			}
		});
		setHeaderMenuByLeft(this);
		loadingDialog = new LoadingDialog(this,R.style.loading_dialog);
		mPositionLayout = (ScrollView) findViewById(R.id.publish_position);
		
		exprMap = new TreeMap<Integer, String>(comparator);
		eduMap = new TreeMap<Integer, String>(comparator);
		
		mCorpLogo = (ImageView) findViewById(R.id.corp_logo);
		mCorpName = (TextView) findViewById(R.id.corp_name);
		mBenefits = (LinesTextView) findViewById(R.id.corp_benefits);
		mPositionName = (FormItemByLineView) findViewById(R.id.position_name);
		mSalary = (FormItemByLineView) findViewById(R.id.salary);
		mRecruitNum = (FormItemByLineLayout) findViewById(R.id.recruit_num);
		mCity = (FormItemByLineView) findViewById(R.id.city);
		mJobExperience = (FormItemByLineView) findViewById(R.id.job_experience);
		mEdu = (FormItemByLineView) findViewById(R.id.edu_require);
		mHighLights = (FormItemByLineView) findViewById(R.id.high_lights);
		mPositionDesc = (FormItemByLineView) findViewById(R.id.position_desc);
		mNotifyEmail = (FormItemByLineView) findViewById(R.id.notify_email);
		
		
		mRecruitNum.getInfoEdit().setInputType(EditorInfo.TYPE_CLASS_NUMBER);
		
		mNotifyEmail.getFormLabel().setTextColor(getResources().getColor(R.color.general_color_cccccc));
		mNotifyEmail.getFormContent().setTextColor(getResources().getColor(R.color.general_color_cccccc));
		
		mPositionName.setOnClickListener(this);
		mSalary.setOnClickListener(this);
		mRecruitNum.setOnClickListener(this);
		mCity.setOnClickListener(this);
		mJobExperience.setOnClickListener(this);
		mEdu.setOnClickListener(this);
		mHighLights.setOnClickListener(this);
		mPositionDesc.setOnClickListener(this);
		
		// 福利标签
		mBenefitsList = new ArrayList<String>();
		mQueue = Volley.newRequestQueue(this);
		mImageLoader = new ImageLoader(mQueue, new CommonBitmapMemoryCache());
		
		
		mJobId = getIntent().getIntExtra(GlobalConstants.JOB_INFO, mJobId);
		if(mJobId != -1 && mJobId != 0){//表示编辑页面
			setHeaderMenuByCenterTitle(R.string.publish_update_position);
			mPositionEventLogic.getPositionInfo(String.valueOf(mJobId));
			mPositionName.setEnabled(false);
		}
		mProfileEventLogic.getHrBasic();
		mProfileEventLogic.getJobEdus();
		mProfileEventLogic.getJobExprs();
		mPositionLayout.setVisibility(View.INVISIBLE);
	}



	@Override
	public void onEventMainThread(Message msg) {
		if (msg.obj instanceof InfoResult) {
			InfoResult infoResult = (InfoResult) msg.obj;
			switch (msg.what) {
			case R.id.position_detail:
					JSONObject position = (JSONObject) JSONObject.parse(infoResult.getResult());
					
					mCorpInfo = new CorpInfo(position.getJSONObject(GlobalConstants.JSON_CORP));
					mPublisherInfo = new PublisherInfo(position.getJSONObject(GlobalConstants.JSON_PUBLISHER));
					mJobDetailInfo = new JobDetailInfo(position.getJSONObject(GlobalConstants.JSON_JOB));
					
					//加载公司基本信息
					if (!StringUtils.isEmpty(mCorpInfo.getLogo())) {
						mListener = ImageLoader.getImageListener(mCorpLogo, R.drawable.default_corp_logo, R.drawable.default_corp_logo);
						mImageLoader.get(mCorpInfo.getLogo(), mListener);
					} else {
						mCorpLogo.setImageResource(R.drawable.default_corp_logo);
					}
					
					if(StringUtils.isNotEmpty(mCorpInfo.getName())){
						mCorpName.setText(mCorpInfo.getName());
					}
					
					if(mCorpInfo.getBenefits() != null){
						List<String> mBenefitsList = new ArrayList<String>();
						for (int i = 0; i < mCorpInfo.getBenefits().size(); i++) {
							mBenefitsList.add(mCorpInfo.getBenefits().getJSONObject(i).getString(GlobalConstants.JSON_NAME));
						}
						mBenefits.setTextColor(getResources().getColor(R.color.white));
						mBenefits.setBackgroundResource(R.drawable.general_benefit_bg);
						mBenefits.setLinesText(mBenefitsList);
					}
					if(StringUtils.isNotEmpty(mJobDetailInfo.getPosition().getString(GlobalConstants.JSON_NAME))){
						mPositionName.setFormContent(mJobDetailInfo.getPosition().getString(GlobalConstants.JSON_NAME));
					}
					if(StringUtils.isNotEmpty(mJobDetailInfo.getSalary())){
						mSalary.setFormContent(mJobDetailInfo.getSalary());
						beginSalary = mJobDetailInfo.getSalaryBegin();
						endSalary = mJobDetailInfo.getSalaryEnd();
					}
					if(StringUtils.isNotEmpty(getString(R.string.general_people_num,mJobDetailInfo.getHeadcount()))){
						headCount = mJobDetailInfo.getHeadcount();
						mRecruitNum.setFormContent(String.valueOf(mJobDetailInfo.getHeadcount()));
					}
					if(StringUtils.isNotEmpty(mJobDetailInfo.getCity())){
						mCity.setFormContent(mJobDetailInfo.getCity());
					}
					if(StringUtils.isNotEmpty(mJobDetailInfo.getEdu().getString(GlobalConstants.JSON_NAME))){
						mEdu.setFormContent(mJobDetailInfo.getEdu().getString(GlobalConstants.JSON_NAME));
					}
					if(StringUtils.isNotEmpty(mJobDetailInfo.getExpr().getString(GlobalConstants.JSON_NAME))){
						mJobExperience.setFormContent(mJobDetailInfo.getExpr().getString(GlobalConstants.JSON_NAME));
					}
					if(StringUtils.isNotEmpty(mPublisherInfo.getCorpemail())){
						mNotifyEmail.setFormContent(mPublisherInfo.getCorpemail());
						mNotifyEmail.getFormContent().setTextColor(getResources().getColor(R.color.general_color_999999));
					}
					if(StringUtils.isNotEmpty(mJobDetailInfo.getHighlights())){
						highLight = mJobDetailInfo.getHighlights();
						mHighLights.setFormContent(getString(R.string.general_tips_alfill));
					}
					if(StringUtils.isNotEmpty(mJobDetailInfo.getDesc())){
						desc = mJobDetailInfo.getDesc();
						mPositionDesc.setFormContent(getString(R.string.general_tips_alfill));
					}
				break;
			case R.id.register_get_hr_basicinfo:
				JSONObject hrbasic = JSONObject.parseObject(infoResult.getResult());
				if(hrbasic != null){
					HrInfo hrInfo = new HrInfo(hrbasic);
					mNotifyEmail.setFormContent(hrInfo.getEmail());
					mRegisterEventLogic.getCorpInfo(hrInfo.getCorpId());
				}
				break;
			case R.id.register_get_corp:
				JSONObject jsonObject = (JSONObject) JSONObject.parse(infoResult.getResult());
				if(jsonObject != null){
					mCorpInfo = new CorpInfo(jsonObject.getJSONObject(GlobalConstants.JSON_CORP));
					if(StringUtils.isNotEmpty(mCorpInfo.getLname())){
						mCorpName.setText(mCorpInfo.getLname());
					}
					if(StringUtils.isNotEmpty(mCorpInfo.getLogo())){
						// 加载图片
						mListener = ImageLoader.getImageListener(mCorpLogo,R.drawable.default_corp_logo, R.drawable.default_corp_logo);
						mImageLoader.get(mCorpInfo.getLogo(), mListener);
					}
					mBenefitsList.clear();
					if(mCorpInfo.getBenefits() != null){
						for (int i = 0; i < mCorpInfo.getBenefits().size(); i++) {
							mBenefitsList.add(mCorpInfo.getBenefits().getJSONObject(i).getString(GlobalConstants.JSON_NAME));
						}
						mBenefits.setTextColor(getResources().getColor(R.color.white));
						mBenefits.setBackgroundResource(R.drawable.general_benefit_bg);
						mBenefits.setLinesText(mBenefitsList);
					}
					// 访问网络请求成功，才显示整体的View
					if(mPositionLayout.getVisibility() != View.VISIBLE){
						Animation loadAnimation = AnimationUtils.loadAnimation(this,R.anim.anim_fide_in_for_recruit);
						mPositionLayout.startAnimation(loadAnimation);
						mPositionLayout.setVisibility(View.VISIBLE);
					}
				}
				break;
			case R.id.position_get_edus:
				JSONObject edus = JSONObject.parseObject(infoResult.getResult());
				if(edus != null){
					JSONArray jsonArray = edus.getJSONArray(GlobalConstants.JSON_JOBEDUS);
					if(jsonArray != null){
						for (int i = 0; i < jsonArray.size(); i++) {
							eduMap.put(jsonArray.getJSONObject(i).getInteger(GlobalConstants.JSON_ID),jsonArray.getJSONObject(i).getString(GlobalConstants.JSON_NAME));
						}
					}
					eduArray = eduMap.values().toArray(new String[]{});
				}
				break;
			case R.id.position_get_exprs:
				JSONObject exprs = JSONObject.parseObject(infoResult.getResult());
				if(exprs != null){
					JSONArray jsonArray = exprs.getJSONArray(GlobalConstants.JSON_JOBEXPRS);
					if(jsonArray != null){
						for (int i = 0; i < jsonArray.size(); i++) {
							exprMap.put(jsonArray.getJSONObject(i).getInteger(GlobalConstants.JSON_ID),jsonArray.getJSONObject(i).getString(GlobalConstants.JSON_NAME));
						}
					}
					exprArray = exprMap.values().toArray(new String[]{});
				}
				
				break;
			case R.id.position_create_position:
				if(infoResult.getMessage().getStatus() == 0){
					AppActivityManager.getInstance().finishActivity(PublishPositionActivity.this);
				}
				VToast.show(this, infoResult.getMessage().getMsg());
				if(loadingDialog.isShowing()){
					loadingDialog.dismiss();
				}
				break;
			case R.id.position_update_position:
				if(infoResult.getMessage().getStatus() == 0){
					Intent intent = new Intent(PublishPositionActivity.this, PublishedPositionsActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(intent);
					AppActivityManager.getInstance().finishActivity(PublishPositionActivity.this);
				}else{
					VToast.show(this, infoResult.getMessage().getMsg());
				}
				if(loadingDialog.isShowing()){
					loadingDialog.dismiss();
				}
				break;
			}
		}
	}

	@Override
	public void onClick(View v) {
		
		Intent intent = null;
		
		switch (v.getId()) {
		case R.id.position_name:
			intent = new Intent(PublishPositionActivity.this, PublishPositionSingleActivity.class);
			intent.putExtra(GlobalConstants.SEQ_STRING, 1);
			intent.putExtra(GlobalConstants.FILL, mPositionName.getFormContentText());
			startActivityForResult(intent,102);
			break;
		case R.id.salary:
			if(mSalaryPicker != null && mSalaryPicker.isShowing()){
				return;
			}
			mSalaryPicker = new DoubleSalaryNumberDialog(this);
			mSalaryPicker.setTitle(mSalary.getFormLabelText());
			mSalaryPicker.setOnCancelListener(new SimpleSalaryNumberDialog.OnCancelListener() {
						@Override
						public void onCancel(DialogInterface dialog) {
							beginSalary = mSalaryPicker.getBegin();
							endSalary = mSalaryPicker.getEnd();
							mSalary.setFormContent(beginSalary + "K"+ getString(R.string.general_tips_splite) + endSalary + "K");
						}
					});
			if(beginSalary != -1){
				mSalaryPicker.setBegin(beginSalary);
			}
			if(endSalary != -1){
				mSalaryPicker.setEnd(endSalary);
			}
			mSalaryPicker.show();
			break;
		case R.id.recruit_num:
			mRecruitNum.setEdit(true);
			mRecruitNum.getInfoEdit().setMaxEms(5);
			mRecruitNum.setCompleteListener(new OnEditCompleteListener() {
				
				@Override
				public void onEditComplete() {
					if(StringUtils.isNotEmpty(mRecruitNum.getFormContentText())){
						long parseLong = Long.parseLong(mRecruitNum.getFormContentText());
						if(parseLong == 0){
							mRecruitNum.setFormContent("1");
						}else if(parseLong > 10){
							mRecruitNum.setFormContent(getString(R.string.publish_position_recruit_num_tips));
						}
						headCount = (int) parseLong;
					}
				}
			});
			break;
		case R.id.city:
			intent = new Intent(PublishPositionActivity.this, RegisterResidentSearchActivity.class);
			startActivityForResult(intent, PublishPositionActivity.REQUEST_CODE_CITY);
			
			break;
		case R.id.job_experience:
			if(!exprMap.isEmpty()){
				SimpleNumberDialog exprDialog = new SimpleNumberDialog(this);
				exprDialog.setTitle(getString(R.string.job_expr_str));
				exprDialog.setMaxValue(exprArray.length - 1);
				exprDialog.setMinValue(0);
				exprDialog.setStrings(exprArray);
				exprDialog.setValue(exprIndex);
				exprDialog.setOnValueChangeListener(new OnValueChangeListener() {
					@Override
					public void onValueChange(NumberPicker picker, int oldVal,
							int newVal) {
						exprIndex = newVal;
					}
				});
				exprDialog.setOnCancelListener(new OnCancelListener() {

					@Override
					public void onCancel(DialogInterface dialog) {
						mJobExperience.setFormContent(exprArray[exprIndex]);
					}
				});
				exprDialog.show();
			}
			
			break;
		case R.id.edu_require:
			if(!exprMap.isEmpty()){
				SimpleNumberDialog eduDialog = new SimpleNumberDialog(this);
				eduDialog.setTitle(getString(R.string.edu_require_str));
				eduDialog.setMaxValue(eduArray.length - 1);
				eduDialog.setMinValue(0);
				eduDialog.setStrings(eduArray);
				eduDialog.setValue(eduIndex);
				eduDialog.setOnValueChangeListener(new OnValueChangeListener() {
					@Override
					public void onValueChange(NumberPicker picker, int oldVal,
							int newVal) {
						eduIndex = newVal;
					}
				});
				eduDialog.setOnCancelListener(new OnCancelListener() {
	
					@Override
					public void onCancel(DialogInterface dialog) {
						mEdu.setFormContent(eduArray[eduIndex]);
					}
				});
				eduDialog.show();
			}
			break;
		case R.id.high_lights:
			intent = new Intent(PublishPositionActivity.this, PublishPositionSingleActivity.class);
			intent.putExtra(GlobalConstants.SEQ_STRING, 2);
			intent.putExtra(GlobalConstants.FILL, highLight);
			startActivityForResult(intent,103);
			break;
		case R.id.position_desc:
			intent = new Intent(PublishPositionActivity.this, PublishPositionSingleActivity.class);
			intent.putExtra(GlobalConstants.SEQ_STRING, 3);
			intent.putExtra(GlobalConstants.FILL, desc);
			startActivityForResult(intent,104);
			break;
		default:
			break;
		}
	}
	// 返回Activity结果
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == RESULT_OK){
			switch (requestCode) {
			case REQUEST_CODE_POSITION:
				
				break;
			case REQUEST_CODE_CITY:
				mCity.setFormContent(data.getStringExtra(RegisterResidentSearchActivity.BACKDATA_KEY));
				break;
			case 102://职位名称
				mPositionName.setFormContent(data.getStringExtra(GlobalConstants.FILL));
				break;
			case 103://职位诱惑
				highLight = data.getStringExtra(GlobalConstants.FILL);
				if(StringUtils.isNotEmpty(highLight)){
					mHighLights.setFormContent(getString(R.string.general_tips_alfill));
				}
				break;
			case 104://职位描述
				desc = data.getStringExtra(GlobalConstants.FILL);
				if(StringUtils.isNotEmpty(desc)){
					mPositionDesc.setFormContent(getString(R.string.general_tips_alfill));
				}
				break;
			}
		}
	}
}
