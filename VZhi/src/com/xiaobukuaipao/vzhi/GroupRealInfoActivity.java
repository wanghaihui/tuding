package com.xiaobukuaipao.vzhi;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.xiaobukuaipao.vzhi.cache.CommonBitmapMemoryCache;
import com.xiaobukuaipao.vzhi.domain.user.PublicUserProfile;
import com.xiaobukuaipao.vzhi.domain.user.UserBasic;
import com.xiaobukuaipao.vzhi.event.InfoResult;
import com.xiaobukuaipao.vzhi.im.ImDbManager;
import com.xiaobukuaipao.vzhi.util.GlobalConstants;
import com.xiaobukuaipao.vzhi.util.StringUtils;
import com.xiaobukuaipao.vzhi.util.VToast;
import com.xiaobukuaipao.vzhi.view.CheckBox;
import com.xiaobukuaipao.vzhi.view.RoundedNetworkImageView;
import com.xiaobukuaipao.vzhi.wrap.SocialWrapActivity;

/**
 * 群实名卡片
 * 
 * @since 2015年01月06日11:22:33
 */
public class GroupRealInfoActivity extends SocialWrapActivity implements OnClickListener {

	static final String TAG = GroupRealInfoActivity.class.getSimpleName();
	
	private ScrollView mRealCardLayout;

	private RoundedNetworkImageView mAvatar;
	private TextView mName;
	private TextView mAge;
	private TextView mCity;
	private TextView mExpr;
	private TextView mPosition;
	private TextView mCorpAndInds;
	private CheckBox mSettingCbox;

	// 请求队列
	private RequestQueue mQueue;
	private ImageLoader mImageLoader;

	private long uid = -1;
	
	private EditText mActionQuestionEdit;

	private TextView mQuestionSender;
	
	// 个人信息
	private UserBasic mBasicinfo;
	
	private AlertDialog.Builder builder;
	/**
	 * 初始化UI数据
	 */
	public void initUIAndData() {
		//处理一些状态值
		uid = getIntent().getLongExtra(GlobalConstants.UID, uid);
		
		setContentView(R.layout.activity_group_real_card);
		setHeaderMenuByCenterTitle(R.string.group_chat_member_card);
		setHeaderMenuByLeft(this);
		mRealCardLayout = (ScrollView) findViewById(R.id.real_card_layout);
		mRealCardLayout.setVisibility(View.INVISIBLE);

		mAvatar = (RoundedNetworkImageView) findViewById(R.id.real_card_avatar);
		mName = (TextView) findViewById(R.id.real_card_name);
		mAge = (TextView) findViewById(R.id.real_card_basicinfo_age);
		mCity = (TextView) findViewById(R.id.real_card_basicinfo_city);
		mExpr = (TextView) findViewById(R.id.real_card_basicinfo_expr);
		
		mPosition = (TextView) findViewById(R.id.real_card_position);
		mCorpAndInds = (TextView) findViewById(R.id.real_card_company);

		mQueue = Volley.newRequestQueue(this);
		mImageLoader = new ImageLoader(mQueue, new CommonBitmapMemoryCache());
		mSettingCbox = (CheckBox) findViewById(R.id.group_msg_setting_cb);
		
		
		
		mSettingCbox.setOnClickListener(this);
		
		builder = new AlertDialog.Builder(this);
		
		mActionQuestionEdit = (EditText) findViewById(R.id.action_question_edit);//提问
		mActionQuestionEdit.setInputType(EditorInfo.TYPE_CLASS_TEXT);
		mQuestionSender = (TextView) findViewById(R.id.action_question_send);
		mQuestionSender.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String question = mActionQuestionEdit.getText().toString();
				mActionQuestionEdit.setText("");
				if(StringUtils.isNotEmpty(question)){
					mSocialEventLogic.cancel(R.id.social_stranger_answer_question);
					mSocialEventLogic.sendQuestion(String.valueOf(uid), question);
				}
				findViewById(R.id.action_question_edit_layout).setVisibility(View.GONE);
			}
		});
		findViewById(R.id.action_greet).setOnClickListener(this);
		findViewById(R.id.action_question).setOnClickListener(this);
		findViewById(R.id.action_friend).setOnClickListener(this);
		findViewById(R.id.action_report).setOnClickListener(this);
		findViewById(R.id.action_chat).setOnClickListener(this);
		findViewById(R.id.action_unbind).setOnClickListener(this);
		findViewById(R.id.group_real_baseinfo).setOnClickListener(this);
	}
	@Override
	protected void onResume() {
		super.onResume();
		if (uid == -1) {
			mProfileEventLogic.getProfileOhterInfo("",null);
		} else {
			mProfileEventLogic.getProfileOhterInfo(String.valueOf(uid), null);
		}
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
					if (mUserProfile.getIsbuddy() != null && mUserProfile.getIsbuddy() == 1) {
						findViewById(R.id.real_card_buddy).setVisibility(View.VISIBLE);
						findViewById(R.id.real_card_tab).setVisibility(View.GONE);
					} else {
						findViewById(R.id.real_card_buddy).setVisibility(View.GONE);
						findViewById(R.id.real_card_tab).setVisibility(View.VISIBLE);
					}
					// 个人信息
					mBasicinfo = new UserBasic(mUserProfile.getBasic());
		
					if (mBasicinfo.getRealname() != null) {
						mName.setText(mBasicinfo.getRealname());
					}
					if (mBasicinfo.getAge() != -1) {
						mAge.setText(getString(R.string.other_profile_age, mBasicinfo.getAge()));
					}
					if (mBasicinfo.getCity() != null) {
						mCity.setText(mBasicinfo.getCity());
					}
					if (mBasicinfo.getExpryear() != null) {
						mExpr.setText(mBasicinfo.getExpryear());
					}
					if (mBasicinfo.getTitle() != null) {
						mPosition.setText(mBasicinfo.getTitle());
					}
					
					StringBuilder sb = new StringBuilder();
					if (mBasicinfo.getCorp() != null) {
						sb.append(mBasicinfo.getCorp());
					}
					
					mCorpAndInds.setText(sb.toString());
					
					mAvatar.setDefaultImageResId(R.drawable.general_user_avatar);
					mAvatar.setImageUrl(mBasicinfo.getRealavatar(), mImageLoader);
					
					// 访问网络请求成功，才显示整体的View
					mRealCardLayout.setVisibility(View.VISIBLE);
				}

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
			case R.id.social_stranger_send_question:
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
			case R.id.social_get_dialog_id:
				JSONObject jsonObject = JSONObject.parseObject(infoResult.getResult());
				String did = jsonObject.getString(GlobalConstants.JSON_DID);
				
				if (!StringUtils.isEmpty(did)) {
					mProfileEventLogic.getProfileOhterInfo(String.valueOf(uid), null);
					// 解除好友成功
					// 将消息列表中与此人的聊天记录删除
					ImDbManager.getInstance().cleanChatHistoryInMessageList(Long.valueOf(did));
				}
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
		case R.id.action_greet:
			mSocialEventLogic.cancel(R.id.social_stranger_send_greeting);
			mSocialEventLogic.sendGreeting(String.valueOf(uid),null);
			break;
		case R.id.action_question:
			findViewById(R.id.action_question_edit_layout).setVisibility(View.VISIBLE);
			findViewById(R.id.action_question_edit_layout).requestFocus();
			mActionQuestionEdit.requestLayout();
			mActionQuestionEdit.setHint(getString(R.string.action_question_tips,mName.getText().toString()));
			mActionQuestionEdit.addTextChangedListener(new TextWatcher() {
				
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
					if(s.length() > 0){
						mQuestionSender.setEnabled(true);
					}else{
						mQuestionSender.setPressed(false);
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
		case R.id.action_friend:
			mSocialEventLogic.cancel(R.id.social_send_invitation);
			mSocialEventLogic.sendInvitation(String.valueOf(uid));
			break;
		case R.id.action_report:
			Intent intentOfficier = new Intent(GroupRealInfoActivity.this, OfficierActivity.class);
			if (uid != -1) {
				intentOfficier.putExtra("report_id", String.valueOf(uid));
			}
			startActivity(intentOfficier);
			break;
		case R.id.action_chat:
			if(uid != -1){
				long id = uid;
				Intent chat = getIntent();
				chat.setClass(this, ChatPersonActivity.class);
				chat.putExtra("sender", id);
				if (!StringUtils.isEmpty(mBasicinfo.getRealname())) {
					chat.putExtra("dname", mBasicinfo.getRealname());
				}
				chat.putExtra("receiverIsreal", 1);
				chat.putExtra("mineIsreal", 1);
				startActivity(chat);
			}
			break;
		case R.id.action_unbind:
			builder.setTitle(R.string.general_tips).setMessage(getString(R.string.action_unbind_tips, mBasicinfo.getRealname()));
			builder.setNegativeButton(R.string.btn_cancel, null);
			builder.setPositiveButton(R.string.btn_confirm, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					mSocialEventLogic.cancel(R.id.social_unbind_buddy);
					mSocialEventLogic.unbindBuddy(String.valueOf(uid));

				}
			});
			builder.show();
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
		case R.id.group_real_baseinfo:
			intent = new Intent();
			intent.setClass(this, PersonalShowPageActivity.class);
			intent.putExtra(GlobalConstants.UID, String.valueOf(uid));
			startActivity(intent);
			break;
		case R.id.group_msg_setting_cb:
			mSettingCbox.setChecked(!mSettingCbox.isChecked());
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
}
