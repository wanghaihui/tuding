package com.xiaobukuaipao.vzhi;

import java.util.Calendar;

import android.content.Intent;
import android.os.Message;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;

import com.alibaba.fastjson.JSONObject;
import com.xiaobukuaipao.vzhi.domain.AuditionInfo;
import com.xiaobukuaipao.vzhi.event.InfoResult;
import com.xiaobukuaipao.vzhi.util.GlobalConstants;
import com.xiaobukuaipao.vzhi.util.Logcat;
import com.xiaobukuaipao.vzhi.util.StringUtils;
import com.xiaobukuaipao.vzhi.util.VToast;
import com.xiaobukuaipao.vzhi.widget.FormItemByLineLayout;
import com.xiaobukuaipao.vzhi.widget.FormItemByLineView;
import com.xiaobukuaipao.vzhi.widget.SimpleDateDialog;
import com.xiaobukuaipao.vzhi.widget.SimpleTimeDialog;
import com.xiaobukuaipao.vzhi.widget.SimpleTimeDialog.OnTimeSetListener;
import com.xiaobukuaipao.vzhi.wrap.RecruitWrapActivity;

public class AuditionActivity extends RecruitWrapActivity implements OnClickListener{
	public static final String TAG = AuditionActivity.class.getSimpleName();

	private String jobid = "";
	private String uid = "";
	private Integer isreal = -1;
	private FormItemByLineView mAudiPosition;
	private FormItemByLineView mAudiCorp;
	private FormItemByLineView mAudiDate;
	private FormItemByLineView mAudiTime;
	private FormItemByLineLayout mAudiLocation;
	private FormItemByLineLayout mAudiHr;
	private FormItemByLineLayout mAudiMobile;
	private FormItemByLineLayout mAudiEmail;
	private EditText mAudiAddition;
	private Button mSend;
	private ScrollView mLayout;
	
	private int mYear;
	private int mMonth;
	private int mDay;

	private int mHour;
	private int mMinute;
	
	private SimpleDateDialog mDatePicker;
	private SimpleTimeDialog mtTimePicker;
	
	private Calendar mCalendar = Calendar.getInstance();
	@Override
	public void initUIAndData() {
		setContentView(R.layout.activity_audition);
		setHeaderMenuByCenterTitle(R.string.my_publish_audition);
		setHeaderMenuByLeft(this);
		mLayout = (ScrollView) findViewById(R.id.layout);
		mLayout.setVisibility(View.GONE);
		
		jobid = getIntent().getStringExtra(GlobalConstants.JOB_ID);
		uid = getIntent().getStringExtra(GlobalConstants.UID);
		isreal = getIntent().getIntExtra(GlobalConstants.ISREAL, isreal);

		mPositionEventLogic.getInterview(jobid);
		
		mAudiPosition = (FormItemByLineView) findViewById(R.id.audition_position);
		mAudiCorp = (FormItemByLineView) findViewById(R.id.audition_corp);
		mAudiDate = (FormItemByLineView) findViewById(R.id.audition_date);
		mAudiTime = (FormItemByLineView) findViewById(R.id.audition_time);
		mAudiLocation = (FormItemByLineLayout) findViewById(R.id.audition_location);
		mAudiHr = (FormItemByLineLayout) findViewById(R.id.audition_hr);
		mAudiMobile = (FormItemByLineLayout) findViewById(R.id.audition_mobile);
		mAudiEmail = (FormItemByLineLayout) findViewById(R.id.audition_email);
		mAudiAddition = (EditText) findViewById(R.id.audition_addition);
		mSend = (Button) findViewById(R.id.audition_send);
		mSend.setOnClickListener(this);
		mAudiDate.setOnClickListener(this);
		mAudiTime.setOnClickListener(this);
		mAudiLocation.setOnClickListener(this);
		mAudiHr.setOnClickListener(this);
		mAudiMobile.setOnClickListener(this);
		mAudiEmail.setOnClickListener(this);
		
	}

	@Override
	public void onEventMainThread(Message msg) {
		if(msg.obj instanceof  InfoResult){
			InfoResult infoResult = (InfoResult)msg.obj;
			switch (msg.what) {
			case R.id.hr_get_interview_temp:
				
				JSONObject jobj = (JSONObject) JSONObject.parse(infoResult.getResult());
				AuditionInfo auditionInfo = new AuditionInfo(jobj);
				mAudiPosition.setFormContent(auditionInfo.getCorpPosition());
				mAudiCorp.setFormContent(auditionInfo.getCorpName());
				mAudiDate.setFormContent(auditionInfo.getDate());
				mAudiTime.setFormContent(auditionInfo.getTime());
				mAudiLocation.setFormContent(auditionInfo.getLocation());
				mAudiHr.setFormContent(auditionInfo.getHrName());
				mAudiMobile.setFormContent(auditionInfo.getHrMobile());
				mAudiEmail.setFormContent(auditionInfo.getHrEmail());
				mAudiAddition.setText(auditionInfo.getPostscript());
				
				if(mLayout.getVisibility() != View.VISIBLE){
					Animation loadAnimation = AnimationUtils.loadAnimation(this,R.anim.anim_fide_in_for_recruit);
					mLayout.startAnimation(loadAnimation);
					mLayout.setVisibility(View.VISIBLE);
				}
				break;
			case R.id.hr_send_interview:
				if(infoResult.getMessage().getStatus() == 0){
					// 此时发送面试通知成功
					Log.i(TAG, infoResult.getResult());
					Intent intent = getIntent();
					intent.putExtra(GlobalConstants.CONTACT_STATUS, intent.getIntExtra(GlobalConstants.CONTACT_STATUS, 0) | 2);
					setResult(RESULT_OK, intent);
					Logcat.d("@@@", "hr_send_interview: " + intent.getIntExtra(GlobalConstants.CONTACT_STATUS, 0));
					AppActivityManager.getInstance().finishActivity(AuditionActivity.this);
					// finish();
				}
				VToast.show(this, infoResult.getMessage().getMsg());
				
				
				break;
			default:
				break;
			}
			
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.audition_send:
			//验证为是否为空
			
			//前两个一定不会为空
			String position = mAudiPosition.getFormContent().getText().toString();
			String corpname = mAudiCorp.getFormContent().getText().toString();
			if(StringUtils.isEmpty(position) || StringUtils.isEmpty(corpname)){
				//除非服务器没有给这个字段 否则不会走到这里
				break;
			}
			
			String date = mAudiDate.getFormContent().getText().toString();
			String time = mAudiTime.getFormContent().getText().toString();
			String location = mAudiLocation.getFormContent().getText().toString();
			String hr = mAudiHr.getFormContent().getText().toString();
			String mobile = mAudiMobile.getFormContent().getText().toString();
			String email = mAudiEmail.getFormContent().getText().toString();
			String addition = mAudiAddition.getText().toString();
			
			if(StringUtils.isEmpty(date)){
				VToast.show(this, getString(R.string.audition_tips,mAudiDate.getFormLabelText()));
				return;
			}
			if(StringUtils.isEmpty(time)){
				VToast.show(this, getString(R.string.audition_tips,mAudiTime.getFormLabelText()));
				return;
			}
			if(StringUtils.isEmpty(location)){
				VToast.show(this, getString(R.string.audition_tips,mAudiLocation.getFormLabelText()));
				return;
			}
			if(StringUtils.isEmpty(hr)){
				VToast.show(this, getString(R.string.audition_tips,mAudiHr.getFormLabelText()));
				return;
			}
			if(StringUtils.isEmpty(mobile)){
				VToast.show(this, getString(R.string.audition_tips,mAudiMobile.getFormLabelText()));
				return;
			}
			if(StringUtils.isEmpty(email)){
				VToast.show(this, getString(R.string.audition_tips,mAudiEmail.getFormLabelText()));
				return;
			}
			mPositionEventLogic.cancel(R.id.hr_send_interview);
			mPositionEventLogic.sendInterview(uid, isreal, jobid, date, time, location, hr, mobile, email, addition);
			
			break;
		case R.id.audition_date:
			mYear = mCalendar.get(Calendar.YEAR);
			mMonth = mCalendar.get(Calendar.MONTH);
			mDay = mCalendar.get(Calendar.DAY_OF_MONTH);
			mDatePicker = new SimpleDateDialog(this, mDateSetListener, mYear, mMonth, mDay);
			mDatePicker.setTitle(getString(R.string.audition_date));
			mDatePicker.setOnlyForeGround(true);
			mDatePicker.show();
			
			break;
		case R.id.audition_time:
			mHour = mCalendar.get(Calendar.HOUR_OF_DAY);
			mMinute = mCalendar.get(Calendar.MINUTE);
			mtTimePicker = new SimpleTimeDialog(this,  mTimeSetListener, mHour, mMinute, true);
			mtTimePicker.setTitle(getString(R.string.audition_time));
			mtTimePicker.setHalf(true);
			mtTimePicker.show(); 
			break;
		case R.id.audition_location:
			mAudiLocation.setEdit(true);
			break;
		case R.id.audition_hr:
			mAudiHr.setEdit(true);
			break;
		case R.id.audition_mobile:
			mAudiMobile.setEdit(true);
			mAudiMobile.getInfoEdit().setInputType(InputType.TYPE_CLASS_PHONE);
			break;
		case R.id.audition_email:
			mAudiEmail.setEdit(true);
			mAudiEmail.getInfoEdit().setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
			break;
		default:
			break;
		}
		
	}
	private SimpleDateDialog.OnDateSetListener mDateSetListener = new SimpleDateDialog.OnDateSetListener() {

		@Override
		public void onDateSet(com.xiaobukuaipao.vzhi.widget.DatePicker view,
				int frontYear, int frontMonthOfYear, int frontDayOfMonth,
				int backYear, int backMonthOnYear, int backDayOfMonth) {
			String date = StringUtils.formatDate(frontYear,frontMonthOfYear,frontDayOfMonth);
			mAudiDate.setFormContent(date);
			mAudiDate.getFormContent().setTextColor(getResources().getColor(R.color.general_color_1A2138));
		}
	};
	
	private SimpleTimeDialog.OnTimeSetListener mTimeSetListener = new OnTimeSetListener() {
		
		@Override
		public void onTimeSet(com.xiaobukuaipao.vzhi.widget.TimePicker view,
				int hourOfDay, int minute) {
			String[] min = getResources().getStringArray(R.array.time_picker_minute);
			String time = StringUtils.formatTime(hourOfDay, Integer.parseInt(min[minute]));
			mAudiTime.setFormContent(time);
			mAudiTime.getFormContent().setTextColor(getResources().getColor(R.color.general_color_1A2138));
		}
	};
}
