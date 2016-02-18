package com.xiaobukuaipao.vzhi.im;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.CursorAdapter;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.squareup.picasso.Picasso;
import com.xiaobukuaipao.vzhi.AnoPersonInfoActivity;
import com.xiaobukuaipao.vzhi.JobPositionInfoActivity;
import com.xiaobukuaipao.vzhi.PersonAnoInfoActivity;
import com.xiaobukuaipao.vzhi.PersonRealInfoActivity;
import com.xiaobukuaipao.vzhi.PersonalShowPageActivity;
import com.xiaobukuaipao.vzhi.R;
import com.xiaobukuaipao.vzhi.WebSearchActivity;
import com.xiaobukuaipao.vzhi.contentprovider.GeneralContentProvider;
import com.xiaobukuaipao.vzhi.database.ContactUserTable;
import com.xiaobukuaipao.vzhi.database.CookieTable;
import com.xiaobukuaipao.vzhi.database.MessageTable;
import com.xiaobukuaipao.vzhi.domain.user.ImUser;
import com.xiaobukuaipao.vzhi.util.GlobalConstants;
import com.xiaobukuaipao.vzhi.util.StringUtils;
import com.xiaobukuaipao.vzhi.util.TimeHandler;
import com.xiaobukuaipao.vzhi.view.RoundedImageView;

/**
 * 消息适配器
 */
public class MessageChatAdapter extends CursorAdapter {
	
	public static final String TAG = MessageChatAdapter.class.getSimpleName();
	
	public static final int MESSAGE_TYPE_INVALID = -1;
	// Text
	public static final int MESSAGE_TYPE_MINE_TEXT = 0x00;
	public static final int MESSAGE_TYPE_OTHER_TEXT = 0x01;
	// 实名卡片
	public static final int MESSAGE_TYPE_MINE_REAL_PROFILE_CARD = 0x02;
	public static final int MESSAGE_TYPE_OTHER_REAL_PROFILE_CARD = 0x03;
	// 匿名卡片
	public static final int MESSAGE_TYPE_MINE_NICK_PROFILE_CARD = 0x04;
	public static final int MESSAGE_TYPE_OTHER_NICK_PROFILE_CARD = 0x05;
	// 职位邀请
	public static final int MESSAGE_TYPE_MINE_JOB_INVITATION = 0x06;
	public static final int MESSAGE_TYPE_OTHER_JOB_INVITATION = 0x07;
	
	// Prompt Text--提示Text
	public static final int MESSAGE_TYPE_PROMPT_TEXT = 0x08;

	// 面试邀请
	public static final int MESSAGE_TYPE_MINE_INTERVIEW_INVITATION = 0x09;
	public static final int MESSAGE_TYPE_OTHER_INTERVIEW_INVITATION = 0x0A;
	
	// 聊一聊
	public static final int MESSAGE_TYPE_MINE_JOB_SUMMARY = 0x0B;
	public static final int MESSAGE_TYPE_OTHER_JOB_SUMMARY = 0x0C;
	
	// Time
	public static final int MESSAGE_TYPE_TIME_TITLE = 0x0D;
	
	// 组合类型
	public static final int MESSAGE_TYPE_COMPOSE_PROMPT_TEXT = 0x0E;
	
	public static final int MESSAGE_TYPE_COMPOSE_JSON_PROMPT_TEXT = 0x0F;
	
	// 在这里扩展更多的消息种类
	// 名片
	// 职位邀约
	// 简历
	
	// View Type Count
	private static final int VIEW_TYPE_COUNT = 16;
	
	public static final String HISTORY_DIVIDER_TAG = "history_divider_tag";
	
	private Context mContext = null;
	protected LayoutInflater inflater = null;
	// 维护一个消息列表
	protected List<Object> messageList = new ArrayList<Object>();
	
	private long sendId = 0;
//	private String mineAvatar;
	
	public static final int TYPE_CLICK_JOB_INVITATION = 1;
	public static final int TYPE_CLICK_JOB_INVITATION_INTEREST = 11;
	public static final int TYPE_CLICK_JOB_INVITATION_UNINTEREST = 12;
	public static final int TYPE_CLICK_INTERVIEW_INVITATION = 2;
	public static final int TYPE_CLICK_INTERVIEW_INVITATION_ACCEPT = 21;
	public static final int TYPE_CLICK_INTERVIEW_INVITATION_IGNORE = 22;
	
	private Handler mHandler;
	
	// Volley加载图片
	// 请求队列
//	private RequestQueue mQueue;
//	private ImageLoader mImageLoader;
//	private ImageListener mListener;
	
	public OnCardContentClickListener mOnCardContentClickListener;
	
	public void setOnCardContentClickListener(OnCardContentClickListener mOnCardContentClickListener) {
		this.mOnCardContentClickListener = mOnCardContentClickListener;
	}
	
	public OnResendClickListener mOnResendClickListener;
	
	public void setOnResendClickListener(OnResendClickListener mOnResendClickListener) {
		this.mOnResendClickListener = mOnResendClickListener;
	}
	
	// 缺省的构造函数--构建
	public MessageChatAdapter(Context context, Cursor c, int flags, Handler mHandler) {
		super(context, c, flags);
		this.mContext = context;
		this.mHandler = mHandler;
		
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		Cursor cookieCursor = context.getContentResolver().query(GeneralContentProvider.COOKIE_CONTENT_URI, null, null, null, null);
		if (cookieCursor != null && cookieCursor.moveToFirst()) {
			sendId = (long) cookieCursor.getInt(cookieCursor.getColumnIndex(CookieTable.COLUMN_ID));
			cookieCursor.close();
		}
		
//		mQueue = Volley.newRequestQueue(mContext);
//		mImageLoader = new ImageLoader(mQueue, new CommonBitmapMemoryCache());
	}
	
	/**
	 * Elements(元素) are populated in(填充) bindView()--绑定View
	 */
	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		
		final int displayType = this.getItemViewType(cursor);
		
		// 此时是正常的聊天文本
		if (displayType == MESSAGE_TYPE_MINE_TEXT) {
			// 此时是本人
			TextMessageHolder viewHolder = (TextMessageHolder) view.getTag();
			
			// 个人头像
			bindMinePortrait(viewHolder, context, cursor);
			
			// 个人的文本聊天内容
			// String content = cursor.getString(cursor.getColumnIndex(MessageTable.COLUMN_MESSAGE_OVERVIEW));
			// Intent intent = new Intent(context, CorpSomeActivity.class);
			final String url = cursor.getString(cursor.getColumnIndex(MessageTable.COLUMN_MESSAGE_OVERVIEW));
			SpannableString content = new SpannableString(cursor.getString(cursor.getColumnIndex(MessageTable.COLUMN_MESSAGE_OVERVIEW)));
			Log.i(TAG, "text content : " + content);
			Pattern pattern = Pattern.compile(GlobalConstants.URL_PATTERN); //正则匹配
	        Matcher matcher = pattern.matcher(content);
	        int start, end;
	        while (matcher.find()) {
	              start = matcher.start();
	              end = matcher.end();
	              Log.i(TAG, "start : " + start);
	              Log.i(TAG, "end : " + end);
	              final int subStart = start;
	              final int subEnd = end;
	              // content.setSpan(new ForegroundColorSpan(Color.RED), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	              // content.setSpan(intent, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	              content.setSpan(new ClickableSpan() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(mContext, WebSearchActivity.class);
						intent.putExtra(GlobalConstants.INNER_URL, url.substring(subStart, subEnd));
						mContext.startActivity(intent);
					}
	            	  
	              }, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	        }
	        
			viewHolder.messageContent.setText(content);
			// viewHolder.messageContent.setAutoLinkMask(Linkify.ALL); 
			viewHolder.messageContent.setMovementMethod(LinkMovementMethod.getInstance());  
			
			// 在这里，读取当前Cursor的已读信息
			long readstatus = (long) cursor.getInt(cursor.getColumnIndex(MessageTable.COLUMN_MESSAGE_READ_STATUS));
			if (readstatus == 1) {
				viewHolder.mReadStatus.setVisibility(View.VISIBLE);
			} else {
				viewHolder.mReadStatus.setVisibility(View.GONE);
			}
			
			// 设置时间戳
			bindMineTime(viewHolder, context, cursor);
			
			bindMineProgressBar(viewHolder, context, cursor);
			
		} else if (displayType == MESSAGE_TYPE_OTHER_TEXT) {
			// 此时是Others
			TextMessageHolder viewHolder = (TextMessageHolder) view.getTag();
			// 设置对方的头像
			bindOthersPortrait(viewHolder, context, cursor);
			
			// 对方的文本聊天内容
			/*String content = cursor.getString(cursor.getColumnIndex(MessageTable.COLUMN_MESSAGE_OVERVIEW));
			viewHolder.messageContent.setText(content);*/
			final String url = cursor.getString(cursor.getColumnIndex(MessageTable.COLUMN_MESSAGE_OVERVIEW));
			SpannableString content = new SpannableString(cursor.getString(cursor.getColumnIndex(MessageTable.COLUMN_MESSAGE_OVERVIEW))); 
			Pattern pattern = Pattern.compile(GlobalConstants.URL_PATTERN); //正则匹配
	        Matcher matcher = pattern.matcher(content);
	        int start, end;
	        while (matcher.find()) {
	              start = matcher.start();
	              end = matcher.end();
	              
	              final int subStart = start;
	              final int subEnd = end;
	              content.setSpan(new ClickableSpan() {
						@Override
						public void onClick(View v) {
							Intent intent = new Intent(mContext, WebSearchActivity.class);
							intent.putExtra(GlobalConstants.INNER_URL, url.substring(subStart, subEnd));
							mContext.startActivity(intent);
						}
		              }, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	        }
			viewHolder.messageContent.setText(content);
			viewHolder.messageContent.setMovementMethod(LinkMovementMethod.getInstance());
			
			// 设置时间戳
			bindMineTime(viewHolder, context, cursor);
			
		} else if (displayType == MESSAGE_TYPE_MINE_REAL_PROFILE_CARD) {
			RealProfileCardHolder viewHolder = (RealProfileCardHolder) view.getTag();
			bindRealProfileCardHolder(viewHolder, view, context, cursor);
		} else if (displayType == MESSAGE_TYPE_OTHER_REAL_PROFILE_CARD) {
			RealProfileCardHolder viewHolder = (RealProfileCardHolder) view.getTag();
			bindRealProfileCardHolder(viewHolder, view, context, cursor);
		} else if (displayType == MESSAGE_TYPE_MINE_NICK_PROFILE_CARD) {
			NickProfileCardHolder viewHolder = (NickProfileCardHolder) view.getTag();
			bindNickProfileCardHolder(viewHolder, view, context, cursor);
		} else if (displayType == MESSAGE_TYPE_OTHER_NICK_PROFILE_CARD) {
			NickProfileCardHolder viewHolder = (NickProfileCardHolder) view.getTag();
			bindNickProfileCardHolder(viewHolder, view, context, cursor);
		} else if (displayType == MESSAGE_TYPE_MINE_JOB_INVITATION) {
			// 职位邀请--自己的
			JobInvitationHolderBase viewHolder = (JobInvitationHolderBase) view.getTag();
			// 个人头像
			bindMinePortrait(viewHolder, context, cursor);
						
			bindMineJobInvitationHolder(viewHolder, view, context, cursor);
			// 设置时间戳
			bindMineTime(viewHolder, context, cursor);
		} else if (displayType == MESSAGE_TYPE_OTHER_JOB_INVITATION) {
			// 职位邀请--别人的
			JobInvitationHolder viewHolder = (JobInvitationHolder) view.getTag();
			// 设置对方的头像
			bindOthersPortrait(viewHolder, context, cursor);
			bindOtherJobInvitationHolder(viewHolder, view, context, cursor);
			// 设置时间戳
			bindMineTime(viewHolder, context, cursor);
		} else if (displayType == MESSAGE_TYPE_PROMPT_TEXT) {
			PromptMessageHolder viewHolder = (PromptMessageHolder) view.getTag();
			bindPromptMessageHolder(viewHolder, view, context, cursor);
		} else if (displayType == MESSAGE_TYPE_MINE_INTERVIEW_INVITATION) {
			InterviewInvitationHolderBase viewHolder = (InterviewInvitationHolderBase) view.getTag();
			
			// 个人头像
			bindMinePortrait(viewHolder, context, cursor);
						
			bindMineInterviewInvitationHolder(viewHolder, view, context, cursor);
			// 设置时间戳
			bindMineTime(viewHolder, context, cursor);
		} else if (displayType == MESSAGE_TYPE_OTHER_INTERVIEW_INVITATION) {
			InterviewInvitationHolder viewHolder = (InterviewInvitationHolder) view.getTag();
			// 设置对方的头像
			bindOthersPortrait(viewHolder, context, cursor);
			bindOtherInterviewInvitationHolder(viewHolder, view, context, cursor);
			// 设置时间戳
			// bindMineTime(viewHolder, context, cursor);
		} else if (displayType == MESSAGE_TYPE_MINE_JOB_SUMMARY) {
			JobSummaryHolder viewHolder = (JobSummaryHolder) view.getTag();
			bindJobSummaryHolder(viewHolder, view, context, cursor);
		} else if (displayType == MESSAGE_TYPE_OTHER_JOB_SUMMARY) {
			JobSummaryHolder viewHolder = (JobSummaryHolder) view.getTag();
			bindJobSummaryHolder(viewHolder, view, context, cursor);
		} else if (displayType == MESSAGE_TYPE_COMPOSE_PROMPT_TEXT) {
			PromptComposeMessageHolder viewHolder = (PromptComposeMessageHolder) view.getTag();
			bindPromptComposeMessageHolder(viewHolder, view, context, cursor);
		} else if (displayType == MESSAGE_TYPE_COMPOSE_JSON_PROMPT_TEXT) {
			PromptComposeMessageHolder viewHolder = (PromptComposeMessageHolder) view.getTag();
			bindPromptComposeJsonMessageHolder(viewHolder, view, context, cursor);
		}
	}

	/**
	 * View is created in newView() method--创建View
	 */
	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		// 所有需要被赋值的holder都是基于MessageHolderBase的
		MessageHolderBase holder = null;
		// 这时，从数据库表中获得本行中的消息类型列表
		final int displayType = this.getItemViewType(cursor);
		
		View convertView = null;
		
		// 此时是正常的聊天文本
		if (displayType == MESSAGE_TYPE_MINE_TEXT) {
			// 此时是本人
			convertView = inflater.inflate(R.layout.mine_text_message, parent, false);
			holder = new TextMessageHolder();
			convertView.setTag(holder);
			initMessageHolderBase(holder, convertView);
			
			initTextMessageHolder((TextMessageHolder) holder, convertView);
		} else if (displayType == MESSAGE_TYPE_OTHER_TEXT) {
			// 此时，设置对方头像
			
			// 此时是Others
			convertView = inflater.inflate(R.layout.other_text_message, parent, false);
			holder = new TextMessageHolder();
			convertView.setTag(holder);
			initMessageHolderBase((TextMessageHolder) holder, convertView);
			initTextMessageHolder((TextMessageHolder) holder, convertView);
		} else if (displayType == MESSAGE_TYPE_MINE_REAL_PROFILE_CARD) {
			convertView = inflater.inflate(R.layout.mine_real_card_message, parent, false);
			holder = new RealProfileCardHolder();
			convertView.setTag(holder);
			initRealProfileCardHolder((RealProfileCardHolder) holder, convertView);
		} else if (displayType == MESSAGE_TYPE_OTHER_REAL_PROFILE_CARD) {
			convertView = inflater.inflate(R.layout.other_real_card_message, parent, false);
			holder = new RealProfileCardHolder();
			convertView.setTag(holder);
			initRealProfileCardHolder((RealProfileCardHolder) holder, convertView);
		} else if (displayType == MESSAGE_TYPE_MINE_NICK_PROFILE_CARD) {
			convertView = inflater.inflate(R.layout.mine_nick_card_message, parent, false);
			holder = new NickProfileCardHolder();
			convertView.setTag(holder);
			initNickProfileCardHolder((NickProfileCardHolder) holder, convertView);
		} else if (displayType == MESSAGE_TYPE_OTHER_NICK_PROFILE_CARD) {
			convertView = inflater.inflate(R.layout.other_nick_card_message, parent, false);
			holder = new NickProfileCardHolder();
			convertView.setTag(holder);
			initNickProfileCardHolder((NickProfileCardHolder) holder, convertView);
		} else if (displayType == MESSAGE_TYPE_MINE_JOB_INVITATION) {
			// 职位邀约--自己的
			convertView = inflater.inflate(R.layout.mine_job_invitation_message, parent, false);
			holder = new JobInvitationHolder();
			convertView.setTag(holder);
			initMessageHolderBase((JobInvitationHolder) holder, convertView);
			initMineJobInvitationHolder((JobInvitationHolder) holder, convertView);
		} else if (displayType == MESSAGE_TYPE_OTHER_JOB_INVITATION) {
			// 职位邀约--别人的
			convertView = inflater.inflate(R.layout.other_job_invitation_message, parent, false);
			holder = new JobInvitationHolder();
			convertView.setTag(holder);
			initMessageHolderBase((JobInvitationHolder) holder, convertView);
			initOtherJobInvitationHolder((JobInvitationHolder) holder, convertView);
		} else if (displayType == MESSAGE_TYPE_PROMPT_TEXT) {
			convertView = inflater.inflate(R.layout.prompt_text_message, parent, false);
			PromptMessageHolder promptHolder = new PromptMessageHolder();
			convertView.setTag(promptHolder);
			initPromptMessageHolder(promptHolder, convertView);
		} else if (displayType == MESSAGE_TYPE_MINE_INTERVIEW_INVITATION) {
			convertView = inflater.inflate(R.layout.mine_interview_invitation_message, parent, false);
			holder = new InterviewInvitationHolderBase();
			convertView.setTag(holder);
			
			initMessageHolderBase((InterviewInvitationHolderBase) holder, convertView);
			initMineInterviewInvitationHolder((InterviewInvitationHolderBase) holder, convertView);
		} else if (displayType == MESSAGE_TYPE_OTHER_INTERVIEW_INVITATION) {
			// 面试邀请
			convertView = inflater.inflate(R.layout.other_interview_invitation_message, parent, false);
			holder = new InterviewInvitationHolder();
			convertView.setTag(holder);
			initMessageHolderBase((InterviewInvitationHolder) holder, convertView);
			initOtherInterviewInvitationHolder((InterviewInvitationHolder) holder, convertView);
			
		} else if (displayType == MESSAGE_TYPE_MINE_JOB_SUMMARY) {
			convertView = inflater.inflate(R.layout.mine_job_summary_message, parent, false);
			holder = new JobSummaryHolder();
			convertView.setTag(holder);
			initJobSummary((JobSummaryHolder) holder, convertView);
		} else if (displayType == MESSAGE_TYPE_OTHER_JOB_SUMMARY) {
			convertView = inflater.inflate(R.layout.other_job_summary_message, parent, false);
			holder = new JobSummaryHolder();
			convertView.setTag(holder);
			initJobSummary((JobSummaryHolder) holder, convertView);
		} else if (displayType == MESSAGE_TYPE_COMPOSE_PROMPT_TEXT) {
			convertView = inflater.inflate(R.layout.prompt_text_message, parent, false);
			
			PromptComposeMessageHolder promptHolder = new PromptComposeMessageHolder();
			convertView.setTag(promptHolder);
			initPromptComposeMessageHolder(promptHolder, convertView);
			
		} else if (displayType == MESSAGE_TYPE_COMPOSE_JSON_PROMPT_TEXT) {
			convertView = inflater.inflate(R.layout.compose_prompt_text_message, parent, false);
			
			PromptComposeMessageHolder promptHolder = new PromptComposeMessageHolder();
			convertView.setTag(promptHolder);
			initPromptComposeMessageHolder(promptHolder, convertView);
		}
		
		return convertView;
	} 
	
	/**
	 * 得到View的种类
	 */
	@Override
	public int getItemViewType(int position) {
		// 得到Position位置处的Cursor
		Cursor cursor = (Cursor) getItem(position);
		return this.getItemViewType(cursor);
	}
	
	private int getItemViewType(Cursor cursor) {
		
		final int type =  cursor.getInt(cursor.getColumnIndex(MessageTable.COLUMN_MESSAGE_DISPLAY_TYPE));
		
		if (type == IMConstants.DISPLAY_TYPE_TEXT) {
			// 普通文本信息
			if (sendId == cursor.getInt(cursor.getColumnIndex(MessageTable.COLUMN_MESSAGE_FROM_USER_ID))) {
				return MESSAGE_TYPE_MINE_TEXT;
			} else {
				return MESSAGE_TYPE_OTHER_TEXT;
			}
		} else if (type == IMConstants.DISPLAY_TYPE_REAL_PROFILE_CARD) {
			// 实名卡片
			if (sendId == cursor.getInt(cursor.getColumnIndex(MessageTable.COLUMN_MESSAGE_FROM_USER_ID))) {
				return MESSAGE_TYPE_MINE_REAL_PROFILE_CARD;
			} else {
				return MESSAGE_TYPE_OTHER_REAL_PROFILE_CARD;
			}
		} else if (type == IMConstants.DISPLAY_TYPE_NICK_PROFILE_CARD) {
			// 匿名卡片
			if (sendId == cursor.getInt(cursor.getColumnIndex(MessageTable.COLUMN_MESSAGE_FROM_USER_ID))) {
				return MESSAGE_TYPE_MINE_NICK_PROFILE_CARD;
			} else {
				return MESSAGE_TYPE_OTHER_NICK_PROFILE_CARD;
			}
		} else if (type == IMConstants.DISPLAY_TYPE_JOB_INVITATION) {
			// 职位邀请
			if (sendId == cursor.getInt(cursor.getColumnIndex(MessageTable.COLUMN_MESSAGE_FROM_USER_ID))) {
				return MESSAGE_TYPE_MINE_JOB_INVITATION;
			} else {
				return MESSAGE_TYPE_OTHER_JOB_INVITATION;
			}
		} else if (type == IMConstants.DISPLAY_TYPE_PROMPT_TEXT) {
			// 普通的消息提醒
			return MESSAGE_TYPE_PROMPT_TEXT;
		} else if (type == IMConstants.DISPLAY_TYPE_INTERVIEW_INVITATION) {
			// 面试邀请
			if (sendId == cursor.getInt(cursor.getColumnIndex(MessageTable.COLUMN_MESSAGE_FROM_USER_ID))) {
				return MESSAGE_TYPE_MINE_INTERVIEW_INVITATION;
			} else {
				return MESSAGE_TYPE_OTHER_INTERVIEW_INVITATION;
			}
		} else if (type == IMConstants.DISPLAY_TYPE_JOB_SUMMARY) {
			// 聊一聊里面的卡片信息--摘要
			if (sendId == cursor.getInt(cursor.getColumnIndex(MessageTable.COLUMN_MESSAGE_FROM_USER_ID))) {
				return MESSAGE_TYPE_MINE_JOB_SUMMARY;
			} else {
				return MESSAGE_TYPE_OTHER_JOB_SUMMARY;
			}
		}  else if (type == IMConstants.DISPLAY_TYPE_COMPOSE_PROMPT_TEXT) {
			return MESSAGE_TYPE_COMPOSE_PROMPT_TEXT;
		} else if (type == IMConstants.DISPLAY_TYPE_COMPOSE_JSON_PROMPT_TEXT) {
			return MESSAGE_TYPE_COMPOSE_JSON_PROMPT_TEXT;
		} else {
			return MESSAGE_TYPE_INVALID;
		}
		
	}

	/**
	 * 得到View的种类数目---还得再研究这里
	 */
	@Override
	public int getViewTypeCount() {
		return VIEW_TYPE_COUNT;
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private void initMessageHolderBase(MessageHolderBase holder, View convertView) {
		holder.portrait = (RoundedImageView) convertView.findViewById(R.id.user_portrait);
		holder.messageFailed = (ImageView) convertView.findViewById(R.id.message_state_failed);
		holder.loadingProgressBar = (ProgressBar) convertView.findViewById(R.id.progressBar1);
		holder.mTime = (TextView) convertView.findViewById(R.id.item_time);
	}
	
	private void initTextMessageHolder(TextMessageHolder holder, View convertView) {
		holder.mReadStatus = (TextView) convertView.findViewById(R.id.read_status);
		holder.messageContent = (TextView) convertView.findViewById(R.id.message_content);
	}
	
	private void initPromptMessageHolder(PromptMessageHolder holder, View convertView) {
		holder.mPrompt = (TextView) convertView.findViewById(R.id.message_content);
	}
	
	private void initPromptComposeMessageHolder(PromptComposeMessageHolder holder, View convertView) {
		holder.mPrompt = (TextView) convertView.findViewById(R.id.message_content);
		holder.mCompose = (TextView) convertView.findViewById(R.id.message_click_look);
	}
	
	private void initRealProfileCardHolder(RealProfileCardHolder holder, View convertView) {
		holder.mAvatar = (RoundedImageView) convertView.findViewById(R.id.profile_card_avatar);
		holder.mName = (TextView) convertView.findViewById(R.id.profile_card_name);
		holder.mPosition = (TextView) convertView.findViewById(R.id.profile_card_position);
		holder.mCorp = (TextView) convertView.findViewById(R.id.profile_card_corp);
	}
	
	private void initNickProfileCardHolder(NickProfileCardHolder holder, View convertView) {
		holder.mAvatar = (RoundedImageView) convertView.findViewById(R.id.profile_card_avatar);
		holder.mName = (TextView) convertView.findViewById(R.id.profile_card_name);
		holder.mPosition = (TextView) convertView.findViewById(R.id.profile_card_position);
		holder.mCorp = (TextView) convertView.findViewById(R.id.profile_card_corp);
	}
	
	
	private void initMineJobInvitationHolder(JobInvitationHolderBase holder, View convertView) {
		holder.mAvatar = (RoundedImageView) convertView.findViewById(R.id.corp_avatar);
		holder.mName = (TextView) convertView.findViewById(R.id.position_name);
		holder.mSalary = (TextView) convertView.findViewById(R.id.position_salary);
		holder.mCity = (TextView) convertView.findViewById(R.id.position_city);
		holder.mEduExpr = (TextView) convertView.findViewById(R.id.position_edu_expr);
		holder.mHighlight = (TextView) convertView.findViewById(R.id.corp_hightlight);
	}
	
	
	private void initOtherJobInvitationHolder(JobInvitationHolder holder, View convertView) {
		holder.mAvatar = (RoundedImageView) convertView.findViewById(R.id.corp_avatar);
		holder.mName = (TextView) convertView.findViewById(R.id.position_name);
		holder.mSalary = (TextView) convertView.findViewById(R.id.position_salary);
		holder.mCity = (TextView) convertView.findViewById(R.id.position_city);
		holder.mEduExpr = (TextView) convertView.findViewById(R.id.position_edu_expr);
		holder.mHighlight = (TextView) convertView.findViewById(R.id.corp_hightlight);
		holder.mPost = (TextView) convertView.findViewById(R.id.position_card_post);
		holder.mUninterest = (TextView) convertView.findViewById(R.id.position_card_uninterest);
	}
	
	// 面试邀请--mine
	private void initMineInterviewInvitationHolder(InterviewInvitationHolderBase holder, View convertView) {
		holder.mCorpName = (TextView) convertView.findViewById(R.id.audition_card_corp_name);
		holder.mCorpAvatar = (RoundedImageView) convertView.findViewById(R.id.audition_card_avatar);
		holder.mPosition = (TextView) convertView.findViewById(R.id.audition_card_name);
		holder.mSalary = (TextView)  convertView.findViewById(R.id.audition_salary);
		holder.mCity = (TextView) convertView.findViewById(R.id.audition_city);
//		holder.mEduExpr = (TextView) convertView.findViewById(R.id.audition_card_edu_expr);
		
		holder.mInterviewTime = (TextView) convertView.findViewById(R.id.audition_card_time);
		holder.mInterviewLocate = (TextView) convertView.findViewById(R.id.audition_card_locate);
		holder.mInterviewContact = (TextView) convertView.findViewById(R.id.audition_card_contacts);
		holder.mInterviewPhone = (TextView) convertView.findViewById(R.id.audition_card_mobile);
		holder.mInterviewEmail = (TextView) convertView.findViewById(R.id.audition_card_email);
		holder.mInterviewDesc = (TextView) convertView.findViewById(R.id.audition_card_desc);
	}
	
	// 面试邀请--other
	private void initOtherInterviewInvitationHolder(InterviewInvitationHolder holder, View convertView) {
		holder.mCorpName = (TextView) convertView.findViewById(R.id.audition_card_corp_name);
		holder.mCorpAvatar = (RoundedImageView) convertView.findViewById(R.id.audition_card_avatar);
		holder.mPosition = (TextView) convertView.findViewById(R.id.audition_card_name);
		holder.mSalary = (TextView)  convertView.findViewById(R.id.audition_salary);
		holder.mCity = (TextView) convertView.findViewById(R.id.audition_city);
//		holder.mEduExpr = (TextView) convertView.findViewById(R.id.audition_card_edu_expr);
		
		holder.mInterviewTime = (TextView) convertView.findViewById(R.id.audition_card_time);
		holder.mInterviewLocate = (TextView) convertView.findViewById(R.id.audition_card_locate);
		holder.mInterviewContact = (TextView) convertView.findViewById(R.id.audition_card_contacts);
		holder.mInterviewPhone = (TextView) convertView.findViewById(R.id.audition_card_mobile);
		holder.mInterviewEmail = (TextView) convertView.findViewById(R.id.audition_card_email);
		holder.mInterviewDesc = (TextView) convertView.findViewById(R.id.audition_card_desc);
		
		holder.mAccept = (TextView) convertView.findViewById(R.id.audition_card_accept);
		holder.mIgnore = (TextView) convertView.findViewById(R.id.audition_card_unaccept);
	}
	
	// 摘要信息--mine
	private void initJobSummary(JobSummaryHolder holder, View convertView) {
		holder.mAvatar = (RoundedImageView) convertView.findViewById(R.id.corp_avatar);
		holder.mName = (TextView) convertView.findViewById(R.id.position_name);
		holder.mSalary = (TextView) convertView.findViewById(R.id.position_salary);
		holder.mCity = (TextView) convertView.findViewById(R.id.position_city);
		holder.mEduExpr = (TextView) convertView.findViewById(R.id.position_edu_expr);
		holder.mHighlight = (TextView) convertView.findViewById(R.id.corp_hightlight);
	}
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private void bindMinePortrait(final MessageHolderBase viewHolder, Context context, Cursor cursor) {
		Cursor cursorContactUser = mContext.getContentResolver().query(GeneralContentProvider.CONTACT_USER_CONTENT_URI, null, 
				ContactUserTable.COLUMN_UID + " = ?" + " AND " + ContactUserTable.COLUMN_DID + " = ?", 
				new String[] { String.valueOf(sendId), 
				String.valueOf(cursor.getInt(cursor.getColumnIndex(MessageTable.COLUMN_MESSAGE_DIALOG_ID))) }, null);
		if (cursorContactUser != null && cursorContactUser.moveToFirst()) {
			// 首先设置头像
			boolean isReal = cursorContactUser.getLong(cursorContactUser.getColumnIndex(ContactUserTable.COLUMN_ISREAL)) == 1 ? true : false;
			String avatar = null;
			
			if (isReal) {
				avatar = cursorContactUser.getString(cursorContactUser.getColumnIndex(ContactUserTable.COLUMN_AVATAR));
			} else {
				avatar = cursorContactUser.getString(cursorContactUser.getColumnIndex(ContactUserTable.COLUMN_NICKAVATAR));
			}
			
			if (avatar != null && avatar.length() > 0) {
				if (isReal) {
					Picasso.with(mContext).load(avatar)
			        .placeholder(R.drawable.general_user_avatar)
			        .into(viewHolder.portrait);
				} else {
					Picasso.with(mContext).load(avatar)
			        .placeholder(R.drawable.general_default_ano)
			        .into(viewHolder.portrait);
				}
			} else {
				if (isReal) {
					viewHolder.portrait.setImageResource(R.drawable.general_user_avatar);
				} else {
					viewHolder.portrait.setImageResource(R.drawable.general_default_ano);
				}
			}
			
			cursorContactUser.close();
		} else {
			long did = cursor.getInt(cursor.getColumnIndex(MessageTable.COLUMN_MESSAGE_DIALOG_ID));
			
			ImUser user = new ImUser();
			user.setUid(sendId);
			user.setDid(did);
			// 否则，执行网络请求
			Message msg = Message.obtain();
			msg.what = 3;
			msg.obj = user;
			mHandler.sendMessage(msg);
		}
	}
	
	
	private void bindMineProgressBar(MessageHolderBase viewHolder, Context context, Cursor cursor) {
		
		long status = cursor.getInt(cursor.getColumnIndex(MessageTable.COLUMN_MESSAGE_STATUS));
		
		if (status == 0) {
			viewHolder.loadingProgressBar.setVisibility(View.VISIBLE);
		} else {
			viewHolder.loadingProgressBar.setVisibility(View.GONE);
			
			if (status == 2) {
				viewHolder.messageFailed.setVisibility(View.VISIBLE);
			} else {
				viewHolder.messageFailed.setVisibility(View.GONE);
			}
		}
		
		// body
		final String body = cursor.getString(cursor.getColumnIndex(MessageTable.COLUMN_MESSAGE_OVERVIEW));
		// _id
		final long cmid = cursor.getLong(cursor.getColumnIndex(MessageTable.COLUMN_ID));
		// groupId
		final long did = cursor.getLong(cursor.getColumnIndex(MessageTable.COLUMN_MESSAGE_DIALOG_ID));
		
		// 如果消息发送失败，此时，要进行消息的重发
		viewHolder.messageFailed.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());  
		        builder.setTitle("提示");  
		        builder.setMessage("重新发送该消息？");  
		        builder.setPositiveButton("确定",  
		                new DialogInterface.OnClickListener() {  
		                    public void onClick(DialogInterface dialog, int whichButton) {  
		                    	mOnResendClickListener.onResendClick(String.valueOf(did), String.valueOf(cmid), body);
		                    }  
		                });  
		        builder.setNegativeButton("取消",  
		                new DialogInterface.OnClickListener() {  
		                    public void onClick(DialogInterface dialog, int whichButton) {  
		                    }  
		                });  
		        builder.show(); 
			}
		});
		
	}
	
	private void bindMineTime(MessageHolderBase viewHolder, Context context, Cursor cursor) {
		// 时间
		long previous;
		
		if (cursor.moveToPrevious()) {
			previous =  cursor.getLong(cursor.getColumnIndex(MessageTable.COLUMN_CREATED));
			Log.i(TAG, "previous : " + previous);
			Log.i(TAG, "previous content : " + cursor.getString(cursor.getColumnIndex(MessageTable.COLUMN_MESSAGE_OVERVIEW)));
			cursor.moveToNext();
			
			Log.i(TAG, "create time :" + cursor.getLong(cursor.getColumnIndex(MessageTable.COLUMN_CREATED)));
			Log.i(TAG, "sub time : " + (cursor.getLong(cursor.getColumnIndex(MessageTable.COLUMN_CREATED)) - previous));
			
			Log.i(TAG, "get min : " + TimeHandler.getInstance(mContext).getMin() * 5);
			
			if ((cursor.getLong(cursor.getColumnIndex(MessageTable.COLUMN_CREATED)) - previous) > TimeHandler.getInstance(mContext).getMin() * 5) {
				viewHolder.mTime.setVisibility(View.VISIBLE);
				String time = TimeHandler.getInstance(mContext).time2str(cursor.getLong(cursor.getColumnIndex(MessageTable.COLUMN_CREATED)));
				viewHolder.mTime.setText(time);
			} else {
				viewHolder.mTime.setVisibility(View.GONE);
			}
			
		} else {
			cursor.moveToNext();
			
			viewHolder.mTime.setVisibility(View.VISIBLE);
			String time = TimeHandler.getInstance(mContext).time2str(cursor.getLong(cursor.getColumnIndex(MessageTable.COLUMN_CREATED)));
			viewHolder.mTime.setText(time);
		}
	}
	
	private void bindOthersPortrait(final MessageHolderBase viewHolder, Context context, Cursor cursor) {
		Cursor cursorContactUser = mContext.getContentResolver().query(GeneralContentProvider.CONTACT_USER_CONTENT_URI, null, 
				ContactUserTable.COLUMN_UID + " = ?" + " AND " + ContactUserTable.COLUMN_DID + " = ?", 
				new String[] { String.valueOf(cursor.getInt(cursor.getColumnIndex(MessageTable.COLUMN_MESSAGE_FROM_USER_ID))), 
				String.valueOf(cursor.getInt(cursor.getColumnIndex(MessageTable.COLUMN_MESSAGE_DIALOG_ID))) }, null);
		
		if (cursorContactUser != null && cursorContactUser.moveToFirst()) {
			Log.i(TAG, "cursorContactUser is not null");
			
			Log.i(TAG, "chat person did : " + cursorContactUser.getLong(cursorContactUser.getColumnIndex(ContactUserTable.COLUMN_DID)));
			// 此时，此联系人已经存在数据库表中
			// 首先设置头像
			boolean isReal = cursorContactUser.getLong(cursorContactUser.getColumnIndex(ContactUserTable.COLUMN_ISREAL)) == 1 ? true : false;
			String avatar = null;
			if (isReal) {
				avatar = cursorContactUser.getString(cursorContactUser.getColumnIndex(ContactUserTable.COLUMN_AVATAR));
			} else {
				avatar = cursorContactUser.getString(cursorContactUser.getColumnIndex(ContactUserTable.COLUMN_NICKAVATAR));
			}
			if (avatar != null && avatar.length() > 0) {
				/*mListener = ImageLoader.getImageListener(viewHolder.portrait, R.drawable.general_user_avatar,R.drawable.general_user_avatar);
				mImageLoader.get(avatar, mListener);*/
				if (isReal) {
					Picasso.with(mContext).load(avatar)
			        .placeholder(R.drawable.general_user_avatar)
			        .into(viewHolder.portrait);
				} else {
					Picasso.with(mContext).load(avatar)
			        .placeholder(R.drawable.general_default_ano)
			        .into(viewHolder.portrait);
				}
			} else {
				if (isReal) {
					viewHolder.portrait.setImageResource(R.drawable.general_user_avatar);
				} else {
					viewHolder.portrait.setImageResource(R.drawable.general_default_ano);
				}
			}
		} else {
			Log.i(TAG, "cursorContactUser is null");
			// 此时，从网络端下载数据
			// ownerid和did
			long uid = cursor.getInt(cursor.getColumnIndex(MessageTable.COLUMN_MESSAGE_FROM_USER_ID));
			long did = cursor.getInt(cursor.getColumnIndex(MessageTable.COLUMN_MESSAGE_DIALOG_ID));
			
			ImUser user = new ImUser();
			user.setUid(uid);
			user.setDid(did);
			// 否则，执行网络请求
			Message msg = Message.obtain();
			msg.what = 3;
			msg.obj = user;
			mHandler.sendMessage(msg);
		}
		
		final long uid = cursor.getInt(cursor.getColumnIndex(MessageTable.COLUMN_MESSAGE_FROM_USER_ID));
		final long did = cursor.getInt(cursor.getColumnIndex(MessageTable.COLUMN_MESSAGE_DIALOG_ID));
		// 点击头像之后，执行的操作
		viewHolder.portrait.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				// 首先，获得这个人实名还是匿名
				long isReal = 0;
				/*Cursor cursorUser = mContext.getContentResolver().query(GeneralContentProvider.CONTACT_USER_CONTENT_URI, null, 
						ContactUserTable.COLUMN_UID + " = ?", new String[] { String.valueOf(uid)}, null);*/
				Cursor cursorUser = mContext.getContentResolver().query(GeneralContentProvider.CONTACT_USER_CONTENT_URI, null, 
						ContactUserTable.COLUMN_UID + " = ?" + " AND " + ContactUserTable.COLUMN_DID + " = ?", 
						new String[] { String.valueOf(uid), 
						String.valueOf(did) }, null);
				if (cursorUser != null && cursorUser.moveToFirst()) {
					isReal = cursorUser.getLong(cursorUser.getColumnIndex(ContactUserTable.COLUMN_ISREAL));
					cursorUser.close();
				}
				if (isReal == 1) {
					Intent intent = new Intent(view.getContext(), PersonRealInfoActivity.class);
					intent.putExtra(GlobalConstants.UID, uid);
					intent.putExtra(GlobalConstants.DID, did);
					mContext.startActivity(intent);
				} else {
					Intent intent = new Intent(view.getContext(), PersonAnoInfoActivity.class);
					intent.putExtra(GlobalConstants.UID, uid);
					intent.putExtra(GlobalConstants.DID, did);
					mContext.startActivity(intent);
				}
			}
		});
		
	}
	
	private void bindRealProfileCardHolder(RealProfileCardHolder viewHolder, View view, Context context, Cursor cursor) {
		String body = cursor.getString(cursor.getColumnIndex(MessageTable.COLUMN_MESSAGE_OVERVIEW));
		JSONObject jsonObject = (JSONObject) JSONObject.parse(body);
		
		if (jsonObject.getJSONObject(GlobalConstants.JSON_BASIC) != null) {
			JSONObject basic = jsonObject.getJSONObject(GlobalConstants.JSON_BASIC);
			// 设置头像--由于是实名--所以用真实头像，否则显示默认头像
			
			if (basic.getInteger(GlobalConstants.JSON_GENDER) == 1) {
				viewHolder.mAvatar.setBorderColor(mContext.getResources().getColor(R.color.boy_border_color));
			} else {
				viewHolder.mAvatar.setBorderColor(mContext.getResources().getColor(R.color.girl_border_color));
			}
			
			if (basic.getString(GlobalConstants.JSON_REALAVATAR) != null && basic.getString(GlobalConstants.JSON_REALAVATAR).length() > 0) {
				/*mListener = ImageLoader.getImageListener(viewHolder.portrait, R.drawable.general_user_avatar,R.drawable.general_user_avatar);
				mImageLoader.get(mineAvatar, mListener);*/
			} else {
				viewHolder.mAvatar.setImageResource(R.drawable.general_user_avatar);
			}
			
			// 设置个人姓名
			if (basic.getString(GlobalConstants.JSON_REALNAME) != null) {
				viewHolder.mName.setText(basic.getString(GlobalConstants.JSON_REALNAME));
			} else {
				viewHolder.mName.setText("未完善");
			}
			
			// 设置个人职位
			if (basic.getJSONObject(GlobalConstants.JSON_POSITION) != null) {
				if (basic.getJSONObject(GlobalConstants.JSON_POSITION).getString(GlobalConstants.JSON_NAME) != null) {
					viewHolder.mPosition.setText(basic.getJSONObject(GlobalConstants.JSON_POSITION).getString(GlobalConstants.JSON_NAME));
				} else {
					viewHolder.mPosition.setText("未完善");
				}
			}
			
			// 设置公司信息
			if (basic.getString(GlobalConstants.JSON_CORP) != null) {
				viewHolder.mCorp.setText(basic.getString(GlobalConstants.JSON_CORP));
			} else {
				viewHolder.mCorp.setText("未完善");
			}
		}
	}
	
	
	private void bindNickProfileCardHolder(NickProfileCardHolder viewHolder, View view, Context context, Cursor cursor) {
		String body = cursor.getString(cursor.getColumnIndex(MessageTable.COLUMN_MESSAGE_OVERVIEW));
		JSONObject jsonObject = (JSONObject) JSONObject.parse(body);
		if (jsonObject.getJSONObject(GlobalConstants.JSON_BASIC) != null) {
			JSONObject basic = jsonObject.getJSONObject(GlobalConstants.JSON_BASIC);
			// 设置头像--由于是实名--所以用真实头像，否则显示默认头像
			
			if (basic.getInteger(GlobalConstants.JSON_GENDER) == 1) {
				viewHolder.mAvatar.setBorderColor(mContext.getResources().getColor(R.color.boy_border_color));
			} else {
				viewHolder.mAvatar.setBorderColor(mContext.getResources().getColor(R.color.girl_border_color));
			}
			
			if (basic.getString(GlobalConstants.JSON_NICKAVATAR) != null && basic.getString(GlobalConstants.JSON_NICKAVATAR).length() > 0) {
				/*mListener = ImageLoader.getImageListener(viewHolder.portrait, R.drawable.general_user_avatar,R.drawable.general_user_avatar);
				mImageLoader.get(mineAvatar, mListener);*/
			} else {
				viewHolder.mAvatar.setImageResource(R.drawable.general_user_avatar);
			}
			
			// 设置个人姓名
			if (basic.getString(GlobalConstants.JSON_NICKNAME) != null) {
				viewHolder.mName.setText(basic.getString(GlobalConstants.JSON_NICKNAME));
			} else {
				viewHolder.mName.setText("未完善");
			}
			
			// 设置个人职位
			if (basic.getJSONObject(GlobalConstants.JSON_POSITION) != null) {
				if (basic.getJSONObject(GlobalConstants.JSON_POSITION).getString(GlobalConstants.JSON_NAME) != null) {
					viewHolder.mPosition.setText(basic.getJSONObject(GlobalConstants.JSON_POSITION).getString(GlobalConstants.JSON_NAME));
				} else {
					viewHolder.mPosition.setText("未完善");
				}
			}
			
			// 设置公司信息
			if (basic.getString(GlobalConstants.JSON_CORP) != null) {
				viewHolder.mCorp.setText(basic.getString(GlobalConstants.JSON_CORP));
			} else {
				viewHolder.mCorp.setText("未完善");
			}
		}

	}
	
	private void bindMineJobInvitationHolder(JobInvitationHolderBase viewHolder, View view, Context context, Cursor cursor) {
		String body = cursor.getString(cursor.getColumnIndex(MessageTable.COLUMN_MESSAGE_OVERVIEW));
		final JSONObject jsonObject = (JSONObject) JSONObject.parse(body);
		// 设置公司Logo
		if (jsonObject.getJSONObject(GlobalConstants.JSON_CORP) != null) {
			if ( StringUtils.isNotEmpty(jsonObject.getJSONObject(GlobalConstants.JSON_CORP).getString(GlobalConstants.JSON_LOGO))) {
				/*mListener = ImageLoader.getImageListener(viewHolder.mAvatar, R.drawable.general_user_avatar,R.drawable.general_user_avatar);
				mImageLoader.get(jsonObject.getJSONObject(GlobalConstants.JSON_CORP).getString(GlobalConstants.JSON_LOGO), mListener);*/
				Picasso.with(mContext).load(jsonObject.getJSONObject(GlobalConstants.JSON_CORP).getString(GlobalConstants.JSON_LOGO))
		        .placeholder(R.drawable.default_corp_logo)
		        .into(viewHolder.mAvatar);
			} else {
				viewHolder.mAvatar.setImageResource(R.drawable.default_corp_logo);
			}
		}
		
		if (jsonObject.getJSONObject(GlobalConstants.JSON_JOB) != null) {
			StringBuilder sb = new StringBuilder();
			
			if (jsonObject.getJSONObject(GlobalConstants.JSON_JOB).getString(GlobalConstants.JSON_POSITION) != null) {
				viewHolder.mName.setText(jsonObject.getJSONObject(GlobalConstants.JSON_JOB).getString(GlobalConstants.JSON_POSITION));
			}
			
			if (jsonObject.getJSONObject(GlobalConstants.JSON_JOB).getString(GlobalConstants.JSON_SALARY) != null) {
				sb.append(jsonObject.getJSONObject(GlobalConstants.JSON_JOB).getString(GlobalConstants.JSON_SALARY));
			}
			viewHolder.mSalary.setText(sb.toString());
			sb.delete(0, sb.toString().length());
			
			if (jsonObject.getJSONObject(GlobalConstants.JSON_JOB).getString(GlobalConstants.JSON_CITY) != null) {
				sb.append(jsonObject.getJSONObject(GlobalConstants.JSON_JOB).getString(GlobalConstants.JSON_CITY));
			}
			viewHolder.mCity.setText(sb.toString());
			sb.delete(0, sb.toString().length());
			
			if (jsonObject.getJSONObject(GlobalConstants.JSON_JOB).getString(GlobalConstants.JSON_EDU) != null) {
				sb.append(jsonObject.getJSONObject(GlobalConstants.JSON_JOB).getString(GlobalConstants.JSON_EDU));
				sb.append(" / ");
			}
			if (jsonObject.getJSONObject(GlobalConstants.JSON_JOB).getString(GlobalConstants.JSON_EXPR) != null) {
				sb.append(jsonObject.getJSONObject(GlobalConstants.JSON_JOB).getString(GlobalConstants.JSON_EXPR));
			}
			viewHolder.mEduExpr.setText(sb.toString());
			
			// 设置职位诱惑
			if (jsonObject.getJSONObject(GlobalConstants.JSON_JOB).getString(GlobalConstants.JSON_HIGHLIGHTS) != null) {
				viewHolder.mHighlight.setText(jsonObject.getJSONObject(GlobalConstants.JSON_JOB).getString(GlobalConstants.JSON_HIGHLIGHTS));
			} else {
				viewHolder.mHighlight.setText("建议完善");
			}
			
		}
	}
	
	/**
	 * 绑定职位邀请Holder
	 * @param viewHolder
	 * @param view
	 * @param context
	 * @param cursor
	 */
	private void bindOtherJobInvitationHolder(JobInvitationHolder viewHolder, View view, Context context, Cursor cursor) {
		String body = cursor.getString(cursor.getColumnIndex(MessageTable.COLUMN_MESSAGE_OVERVIEW));
		final JSONObject jsonObject = (JSONObject) JSONObject.parse(body);
		// 设置公司Logo
		if (jsonObject.getJSONObject(GlobalConstants.JSON_CORP) != null) {
			if (StringUtils.isNotEmpty(jsonObject.getJSONObject(GlobalConstants.JSON_CORP).getString(GlobalConstants.JSON_LOGO))) {
				/*mListener = ImageLoader.getImageListener(viewHolder.mAvatar, R.drawable.general_user_avatar,R.drawable.general_user_avatar);
				mImageLoader.get(jsonObject.getJSONObject(GlobalConstants.JSON_CORP).getString(GlobalConstants.JSON_LOGO), mListener);*/
				Picasso.with(mContext).load(jsonObject.getJSONObject(GlobalConstants.JSON_CORP).getString(GlobalConstants.JSON_LOGO))
		        .placeholder(R.drawable.default_corp_logo)
		        .into(viewHolder.mAvatar);
			} else {
				viewHolder.mAvatar.setImageResource(R.drawable.default_corp_logo);
			}
		}
		
		if (jsonObject.getJSONObject(GlobalConstants.JSON_JOB) != null) {
			StringBuilder sb = new StringBuilder();
			
			if (jsonObject.getJSONObject(GlobalConstants.JSON_JOB).getString(GlobalConstants.JSON_POSITION) != null) {
				viewHolder.mName.setText(jsonObject.getJSONObject(GlobalConstants.JSON_JOB).getString(GlobalConstants.JSON_POSITION));
			}
			
			if (jsonObject.getJSONObject(GlobalConstants.JSON_JOB).getString(GlobalConstants.JSON_SALARY) != null) {
				sb.append(jsonObject.getJSONObject(GlobalConstants.JSON_JOB).getString(GlobalConstants.JSON_SALARY));
			}
			viewHolder.mSalary.setText(sb.toString());
			sb.delete(0, sb.toString().length());
			
			if (jsonObject.getJSONObject(GlobalConstants.JSON_JOB).getString(GlobalConstants.JSON_CITY) != null) {
				sb.append(jsonObject.getJSONObject(GlobalConstants.JSON_JOB).getString(GlobalConstants.JSON_CITY));
			}
			viewHolder.mCity.setText(sb.toString());
			sb.delete(0, sb.toString().length());
			
			if (jsonObject.getJSONObject(GlobalConstants.JSON_JOB).getString(GlobalConstants.JSON_EDU) != null) {
				sb.append(jsonObject.getJSONObject(GlobalConstants.JSON_JOB).getString(GlobalConstants.JSON_EDU));
				sb.append(" / ");
			}
			if (jsonObject.getJSONObject(GlobalConstants.JSON_JOB).getString(GlobalConstants.JSON_EXPR) != null) {
				sb.append(jsonObject.getJSONObject(GlobalConstants.JSON_JOB).getString(GlobalConstants.JSON_EXPR));
			}
			viewHolder.mEduExpr.setText(sb.toString());
			
			// 设置职位诱惑
			if (jsonObject.getJSONObject(GlobalConstants.JSON_JOB).getString(GlobalConstants.JSON_HIGHLIGHTS) != null) {
				viewHolder.mHighlight.setText(jsonObject.getJSONObject(GlobalConstants.JSON_JOB).getString(GlobalConstants.JSON_HIGHLIGHTS));
			} else {
				viewHolder.mHighlight.setText("建议完善");
			}
			
			viewHolder.mPost.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View view) {
					Intent intent = new Intent();
					intent.putExtra(GlobalConstants.JOB_ID, Integer.valueOf(jsonObject.getJSONObject(GlobalConstants.JSON_JOB).getString(GlobalConstants.JSON_ID)));
					intent.setClass(mContext, JobPositionInfoActivity.class);
					mContext.startActivity(intent);
				}
				
			});
		}
		
		final long did = (long) cursor.getInt(cursor.getColumnIndex(MessageTable.COLUMN_MESSAGE_DIALOG_ID));
		final long mid = (long) cursor.getInt(cursor.getColumnIndex(MessageTable.COLUMN_MESSAGE_ID));
		
		// 感兴趣
		/*viewHolder.mInterest.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				mOnCardContentClickListener.onCardContentClick(String.valueOf(did), String.valueOf(mid), TYPE_CLICK_JOB_INVITATION, TYPE_CLICK_JOB_INVITATION_INTEREST);
			}
		});*/
		
		// 不感兴趣
		viewHolder.mUninterest.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				mOnCardContentClickListener.onCardContentClick(String.valueOf(did), String.valueOf(mid), TYPE_CLICK_JOB_INVITATION, TYPE_CLICK_JOB_INVITATION_UNINTEREST);
			}
		});
		
	}
	
	private void bindPromptMessageHolder(PromptMessageHolder viewHolder, View view, Context context, Cursor cursor) {
		viewHolder.mPrompt.setText(cursor.getString(cursor.getColumnIndex(MessageTable.COLUMN_MESSAGE_OVERVIEW)));
	}
	
	private void bindPromptComposeMessageHolder(PromptComposeMessageHolder viewHolder, View view, Context context, Cursor cursor) {
		String body = cursor.getString(cursor.getColumnIndex(MessageTable.COLUMN_MESSAGE_OVERVIEW));
		viewHolder.mPrompt.setText(body);
	}
	
	private void bindPromptComposeJsonMessageHolder(PromptComposeMessageHolder viewHolder, View view, Context context, Cursor cursor) {
		String body = cursor.getString(cursor.getColumnIndex(MessageTable.COLUMN_MESSAGE_OVERVIEW));
		JSONObject jsonObject = (JSONObject) JSONObject.parse(body);
		Log.i(TAG, body);
		final long uid = jsonObject.getLongValue(GlobalConstants.JSON_UID);
		final long jid = jsonObject.getLongValue(GlobalConstants.JSON_JID);
		final long did = cursor.getInt(cursor.getColumnIndex(MessageTable.COLUMN_MESSAGE_DIALOG_ID));
		
		viewHolder.mPrompt.setText(jsonObject.getString(GlobalConstants.JSON_BODY));
		
		if (uid == sendId) {
			viewHolder.mCompose.setVisibility(View.GONE);
		} else {
			viewHolder.mCompose.setVisibility(View.VISIBLE);
		}
		
		viewHolder.mCompose.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if (uid != sendId) {
					
					// 首先，获得这个人实名还是匿名
					long isReal = 0;
					Cursor cursorUser = mContext.getContentResolver().query(GeneralContentProvider.CONTACT_USER_CONTENT_URI, null, 
							ContactUserTable.COLUMN_UID + " = ?" + " AND " + ContactUserTable.COLUMN_DID + " = ?", 
							new String[] { String.valueOf(uid), 
							String.valueOf(did) }, null);
					
					if (cursorUser != null && cursorUser.moveToFirst()) {
						isReal = cursorUser.getLong(cursorUser.getColumnIndex(ContactUserTable.COLUMN_ISREAL));
						cursorUser.close();
					}
					
					if (isReal == 1) {
						Intent intent = new Intent();
		    			intent.setClass(mContext, PersonalShowPageActivity.class);
		    			intent.putExtra(GlobalConstants.CANDIDATE_VIEW, true);
		    			intent.putExtra(GlobalConstants.JOB_ID, String.valueOf(jid));
		    			intent.putExtra(GlobalConstants.UID, String.valueOf(uid));
		    			mContext.startActivity(intent);
					} else {
						Intent intent = new Intent();
						intent.setClass(mContext, AnoPersonInfoActivity.class);
						intent.putExtra(GlobalConstants.CANDIDATE_VIEW, true);
						intent.putExtra(GlobalConstants.JOB_ID, String.valueOf(jid));
						intent.putExtra(GlobalConstants.UID, String.valueOf(uid));
						mContext.startActivity(intent);
					}
					
				}
			}
			
		});
	}
	
	// 绑定面试邀请--mine
	private void bindMineInterviewInvitationHolder(InterviewInvitationHolderBase viewHolder, View view, Context context, Cursor cursor) {
		String body = cursor.getString(cursor.getColumnIndex(MessageTable.COLUMN_MESSAGE_OVERVIEW));
		final JSONObject jsonObject = (JSONObject) JSONObject.parse(body);
		
		// 设置公司信息
		if (jsonObject.getJSONObject(GlobalConstants.JSON_CORP) != null) {
			// 公司名字
			if (jsonObject.getJSONObject(GlobalConstants.JSON_CORP).getString(GlobalConstants.JSON_NAME) != null) {
				viewHolder.mCorpName.setText(jsonObject.getJSONObject(GlobalConstants.JSON_CORP).getString(GlobalConstants.JSON_NAME));
			}
			
			// 公司LOGO
			if (jsonObject.getJSONObject(GlobalConstants.JSON_CORP).getString(GlobalConstants.JSON_LOGO) != null && 
					jsonObject.getJSONObject(GlobalConstants.JSON_CORP).getString(GlobalConstants.JSON_LOGO).length() > 0) {
				/*mListener = ImageLoader.getImageListener(viewHolder.mCorpAvatar, R.drawable.default_corp_logo,R.drawable.default_corp_logo);
				mImageLoader.get(jsonObject.getJSONObject(GlobalConstants.JSON_CORP).getString(GlobalConstants.JSON_LOGO), mListener);*/
			} else {
				viewHolder.mCorpAvatar.setImageResource(R.drawable.default_corp_logo);
			}
		}
		
		// Job信息
		if (jsonObject.getJSONObject(GlobalConstants.JSON_JOB) != null) {
			// 职位
			if (jsonObject.getJSONObject(GlobalConstants.JSON_JOB).getString(GlobalConstants.JSON_POSITION) != null) {
				viewHolder.mPosition.setText(jsonObject.getJSONObject(GlobalConstants.JSON_JOB).getString(GlobalConstants.JSON_POSITION));
			}
			
			//  工资
			if (jsonObject.getJSONObject(GlobalConstants.JSON_JOB).getString(GlobalConstants.JSON_SALARY) != null) {
				viewHolder.mSalary.setText(jsonObject.getJSONObject(GlobalConstants.JSON_JOB).getString(GlobalConstants.JSON_SALARY));
			}
			
			// 城市
			if (jsonObject.getJSONObject(GlobalConstants.JSON_JOB).getString(GlobalConstants.JSON_CITY) != null) {
				viewHolder.mCity.setText(jsonObject.getJSONObject(GlobalConstants.JSON_JOB).getString(GlobalConstants.JSON_CITY));
			}
			
//			// 教育和工作经历
//			StringBuilder sb = new StringBuilder();
//			if (jsonObject.getJSONObject(GlobalConstants.JSON_JOB).getString(GlobalConstants.JSON_EDU) != null) {
//				sb.append(jsonObject.getJSONObject(GlobalConstants.JSON_JOB).getString(GlobalConstants.JSON_EDU));
//			}
//			
//			if (jsonObject.getJSONObject(GlobalConstants.JSON_JOB).getString(GlobalConstants.JSON_EXPR) != null) {
//				sb.append(" / ");
//				sb.append(jsonObject.getJSONObject(GlobalConstants.JSON_JOB).getString(GlobalConstants.JSON_EXPR));
//			}
//			
//			viewHolder.mEduExpr.setText(sb.toString());
		}
		
		
		// 面试信息
		if (jsonObject.getJSONObject(GlobalConstants.JSON_INTERVIEW) != null) {
			// 面试时间
			StringBuilder sb = new StringBuilder();
			sb.append(context.getResources().getString(R.string.msg_chat_audition_time));
			if (jsonObject.getJSONObject(GlobalConstants.JSON_INTERVIEW).getString(GlobalConstants.JSON_DATE) != null) {
				sb.append(jsonObject.getJSONObject(GlobalConstants.JSON_INTERVIEW).getString(GlobalConstants.JSON_DATE));
				sb.append(" ");
			}
			
			if (jsonObject.getJSONObject(GlobalConstants.JSON_INTERVIEW).getString(GlobalConstants.JSON_TIME) != null) {
				sb.append(jsonObject.getJSONObject(GlobalConstants.JSON_INTERVIEW).getString(GlobalConstants.JSON_TIME));
			}
			viewHolder.mInterviewTime.setText(sb.toString());
			
			sb.delete(0, sb.toString().length());
			// 面试地点
			if (jsonObject.getJSONObject(GlobalConstants.JSON_INTERVIEW).getString(GlobalConstants.JSON_ADDRESS) != null) {
				sb.append(context.getResources().getString(R.string.msg_chat_audition_locate));
				sb.append(jsonObject.getJSONObject(GlobalConstants.JSON_INTERVIEW).getString(GlobalConstants.JSON_ADDRESS));
				viewHolder.mInterviewLocate.setText(sb.toString());
			}
			
			// 附言
			sb.delete(0, sb.toString().length());
			if (jsonObject.getJSONObject(GlobalConstants.JSON_INTERVIEW).getString(GlobalConstants.JSON_POSTSCRIPT) != null) {
				sb.append(context.getResources().getString(R.string.msg_chat_audition_desc));
				sb.append(jsonObject.getJSONObject(GlobalConstants.JSON_INTERVIEW).getString(GlobalConstants.JSON_POSTSCRIPT));
				viewHolder.mInterviewDesc.setText(sb.toString());
			}
		}
		
		// hr
		if (jsonObject.getJSONObject(GlobalConstants.JSON_HR) != null) {
			StringBuilder sb = new StringBuilder();
			if (jsonObject.getJSONObject(GlobalConstants.JSON_HR).getString(GlobalConstants.JSON_NAME) != null) {
				sb.append(context.getResources().getString(R.string.msg_chat_audition_contacts));
				sb.append(jsonObject.getJSONObject(GlobalConstants.JSON_HR).getString(GlobalConstants.JSON_NAME));
				viewHolder.mInterviewContact.setText(sb.toString());
			}
			
			sb.delete(0, sb.toString().length());
			if (jsonObject.getJSONObject(GlobalConstants.JSON_HR).getString(GlobalConstants.JSON_MOBILE) != null) {
				sb.append(context.getResources().getString(R.string.msg_chat_audition_mobile));
				sb.append(jsonObject.getJSONObject(GlobalConstants.JSON_HR).getString(GlobalConstants.JSON_MOBILE));
				viewHolder.mInterviewPhone.setText(sb.toString());
			}
			
			sb.delete(0, sb.toString().length());
			if (jsonObject.getJSONObject(GlobalConstants.JSON_HR).getString(GlobalConstants.JSON_EMAIL) != null) {
				sb.append(jsonObject.getJSONObject(GlobalConstants.JSON_HR).getString(GlobalConstants.JSON_EMAIL));
				viewHolder.mInterviewEmail.setText(sb.toString());
			}
		}	
		
	}
	
	private void bindOtherInterviewInvitationHolder(InterviewInvitationHolder viewHolder, View view, Context context, Cursor cursor) {
		String body = cursor.getString(cursor.getColumnIndex(MessageTable.COLUMN_MESSAGE_OVERVIEW));
		final JSONObject jsonObject = (JSONObject) JSONObject.parse(body);
		
		Log.i(TAG, "other interview invitation : " + body);
		
		// 设置公司信息
		if (jsonObject.getJSONObject(GlobalConstants.JSON_CORP) != null) {
			// 公司名字
			if (jsonObject.getJSONObject(GlobalConstants.JSON_CORP).getString(GlobalConstants.JSON_NAME) != null) {
				viewHolder.mCorpName.setText(jsonObject.getJSONObject(GlobalConstants.JSON_CORP).getString(GlobalConstants.JSON_NAME));
			}
			
			// 公司LOGO
			if (jsonObject.getJSONObject(GlobalConstants.JSON_CORP).getString(GlobalConstants.JSON_LOGO) != null && 
					jsonObject.getJSONObject(GlobalConstants.JSON_CORP).getString(GlobalConstants.JSON_LOGO).length() > 0) {
				/*mListener = ImageLoader.getImageListener(viewHolder.mCorpAvatar, R.drawable.default_corp_logo,R.drawable.default_corp_logo);
				mImageLoader.get(jsonObject.getJSONObject(GlobalConstants.JSON_CORP).getString(GlobalConstants.JSON_LOGO), mListener);*/
			} else {
				viewHolder.mCorpAvatar.setImageResource(R.drawable.default_corp_logo);
			}
		}
		
		// Job信息
		if (jsonObject.getJSONObject(GlobalConstants.JSON_JOB) != null) {
			// 职位
			if (jsonObject.getJSONObject(GlobalConstants.JSON_JOB).getString(GlobalConstants.JSON_POSITION) != null) {
				viewHolder.mPosition.setText(jsonObject.getJSONObject(GlobalConstants.JSON_JOB).getString(GlobalConstants.JSON_POSITION));
			}
			
			//  工资
			if (jsonObject.getJSONObject(GlobalConstants.JSON_JOB).getString(GlobalConstants.JSON_SALARY) != null) {
				viewHolder.mSalary.setText(jsonObject.getJSONObject(GlobalConstants.JSON_JOB).getString(GlobalConstants.JSON_SALARY));
			}
			
			// 城市
			if (jsonObject.getJSONObject(GlobalConstants.JSON_JOB).getString(GlobalConstants.JSON_CITY) != null) {
				viewHolder.mCity.setText(jsonObject.getJSONObject(GlobalConstants.JSON_JOB).getString(GlobalConstants.JSON_CITY));
			}
		}
		
		
		// 面试信息
		if (jsonObject.getJSONObject(GlobalConstants.JSON_INTERVIEW) != null) {
			// 面试时间
			StringBuilder sb = new StringBuilder();
			sb.append(context.getResources().getString(R.string.msg_chat_audition_time));
			if (jsonObject.getJSONObject(GlobalConstants.JSON_INTERVIEW).getString(GlobalConstants.JSON_DATE) != null) {
				sb.append(jsonObject.getJSONObject(GlobalConstants.JSON_INTERVIEW).getString(GlobalConstants.JSON_DATE));
				sb.append(" ");
			}
			
			if (jsonObject.getJSONObject(GlobalConstants.JSON_INTERVIEW).getString(GlobalConstants.JSON_TIME) != null) {
				sb.append(jsonObject.getJSONObject(GlobalConstants.JSON_INTERVIEW).getString(GlobalConstants.JSON_TIME));
			}
			viewHolder.mInterviewTime.setText(sb.toString());
			
			sb.delete(0, sb.toString().length());
			// 面试地点
			if (jsonObject.getJSONObject(GlobalConstants.JSON_INTERVIEW).getString(GlobalConstants.JSON_ADDRESS) != null) {
				sb.append(context.getResources().getString(R.string.msg_chat_audition_locate));
				sb.append(jsonObject.getJSONObject(GlobalConstants.JSON_INTERVIEW).getString(GlobalConstants.JSON_ADDRESS));
				viewHolder.mInterviewLocate.setText(sb.toString());
			}
			
			// 附言
			sb.delete(0, sb.toString().length());
			if (jsonObject.getJSONObject(GlobalConstants.JSON_INTERVIEW).getString(GlobalConstants.JSON_POSTSCRIPT) != null) {
				sb.append(context.getResources().getString(R.string.msg_chat_audition_desc));
				sb.append(jsonObject.getJSONObject(GlobalConstants.JSON_INTERVIEW).getString(GlobalConstants.JSON_POSTSCRIPT));
				viewHolder.mInterviewDesc.setText(sb.toString());
			}
		}
		
		// hr
		if (jsonObject.getJSONObject(GlobalConstants.JSON_HR) != null) {
			StringBuilder sb = new StringBuilder();
			if (jsonObject.getJSONObject(GlobalConstants.JSON_HR).getString(GlobalConstants.JSON_NAME) != null) {
				sb.append(context.getResources().getString(R.string.msg_chat_audition_contacts));
				sb.append(jsonObject.getJSONObject(GlobalConstants.JSON_HR).getString(GlobalConstants.JSON_NAME));
				viewHolder.mInterviewContact.setText(sb.toString());
			}
			
			sb.delete(0, sb.toString().length());
			if (jsonObject.getJSONObject(GlobalConstants.JSON_HR).getString(GlobalConstants.JSON_MOBILE) != null) {
				sb.append(context.getResources().getString(R.string.msg_chat_audition_mobile));
				sb.append(jsonObject.getJSONObject(GlobalConstants.JSON_HR).getString(GlobalConstants.JSON_MOBILE));
				viewHolder.mInterviewPhone.setText(sb.toString());
			}
			
			sb.delete(0, sb.toString().length());
			if (jsonObject.getJSONObject(GlobalConstants.JSON_HR).getString(GlobalConstants.JSON_EMAIL) != null) {
				sb.append(jsonObject.getJSONObject(GlobalConstants.JSON_HR).getString(GlobalConstants.JSON_EMAIL));
				viewHolder.mInterviewEmail.setText(sb.toString());
			}
		}	
			
		// 添加点击事件
		
		final long did = (long) cursor.getInt(cursor.getColumnIndex(MessageTable.COLUMN_MESSAGE_DIALOG_ID));
		final long mid = (long) cursor.getInt(cursor.getColumnIndex(MessageTable.COLUMN_MESSAGE_ID));
		
		// 接受邀请
		viewHolder.mAccept.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				mOnCardContentClickListener.onCardContentClick(String.valueOf(did), String.valueOf(mid), 
						TYPE_CLICK_INTERVIEW_INVITATION, TYPE_CLICK_INTERVIEW_INVITATION_ACCEPT);
			}
		});
		
		// 忽略邀请
		viewHolder.mIgnore.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				mOnCardContentClickListener.onCardContentClick(String.valueOf(did), String.valueOf(mid), 
						TYPE_CLICK_INTERVIEW_INVITATION, TYPE_CLICK_INTERVIEW_INVITATION_IGNORE);
			}
		});
			
	}
	
	/**
	 * 摘要信息
	 * @author xiaobu
	 */
	private void bindJobSummaryHolder(JobSummaryHolder viewHolder, View view, Context context, Cursor cursor) {
		String body = cursor.getString(cursor.getColumnIndex(MessageTable.COLUMN_MESSAGE_OVERVIEW));
		final JSONObject jsonObject = (JSONObject) JSONObject.parse(body);
		// 设置公司Logo
		if (jsonObject.getJSONObject(GlobalConstants.JSON_CORP) != null) {
			if (jsonObject.getJSONObject(GlobalConstants.JSON_CORP).getString(GlobalConstants.JSON_LOGO) != null 
					&& jsonObject.getJSONObject(GlobalConstants.JSON_CORP).getString(GlobalConstants.JSON_LOGO).length() > 0) {
				/*mListener = ImageLoader.getImageListener(viewHolder.mAvatar, R.drawable.general_user_avatar,R.drawable.general_user_avatar);
				mImageLoader.get(jsonObject.getJSONObject(GlobalConstants.JSON_CORP).getString(GlobalConstants.JSON_LOGO), mListener);*/
			} else {
				viewHolder.mAvatar.setImageResource(R.drawable.default_corp_logo);
			}
		}
		
		if (jsonObject.getJSONObject(GlobalConstants.JSON_JOB) != null) {
			StringBuilder sb = new StringBuilder();
			
			if (jsonObject.getJSONObject(GlobalConstants.JSON_JOB).getJSONObject(GlobalConstants.JSON_POSITION).getString(GlobalConstants.JSON_NAME) != null) {
				viewHolder.mName.setText(jsonObject.getJSONObject(GlobalConstants.JSON_JOB).getJSONObject(GlobalConstants.JSON_POSITION).getString(GlobalConstants.JSON_NAME));
			}
			
			if (jsonObject.getJSONObject(GlobalConstants.JSON_JOB).getString(GlobalConstants.JSON_SALARY) != null) {
				sb.append(jsonObject.getJSONObject(GlobalConstants.JSON_JOB).getString(GlobalConstants.JSON_SALARY));
			}
			viewHolder.mSalary.setText(sb.toString());
			sb.delete(0, sb.toString().length());
			
			if (jsonObject.getJSONObject(GlobalConstants.JSON_JOB).getString(GlobalConstants.JSON_CITY) != null) {
				sb.append(jsonObject.getJSONObject(GlobalConstants.JSON_JOB).getString(GlobalConstants.JSON_CITY));
			}
			viewHolder.mCity.setText(sb.toString());
			sb.delete(0, sb.toString().length());
			
			if (jsonObject.getJSONObject(GlobalConstants.JSON_JOB).getString(GlobalConstants.JSON_EDU) != null) {
				sb.append(jsonObject.getJSONObject(GlobalConstants.JSON_JOB).getString(GlobalConstants.JSON_EDU));
				sb.append(" / ");
			}
			if (jsonObject.getJSONObject(GlobalConstants.JSON_JOB).getString(GlobalConstants.JSON_EXPR) != null) {
				sb.append(jsonObject.getJSONObject(GlobalConstants.JSON_JOB).getString(GlobalConstants.JSON_EXPR));
			}
			viewHolder.mEduExpr.setText(sb.toString());
			
			// 设置职位诱惑
			if (jsonObject.getJSONObject(GlobalConstants.JSON_JOB).getString(GlobalConstants.JSON_HIGHLIGHTS) != null) {
				viewHolder.mHighlight.setText(jsonObject.getJSONObject(GlobalConstants.JSON_JOB).getString(GlobalConstants.JSON_HIGHLIGHTS));
			} else {
				viewHolder.mHighlight.setText("建议完善");
			}
		}
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private static class MessageHolderBase {
		/**
		 * 头像
		 */
		RoundedImageView portrait;
		/**
		 * 消息状态
		 */
		ImageView messageFailed;
		
		ProgressBar loadingProgressBar;
		
		// 是否已读
		TextView mReadStatus;
		
		// 时间
		TextView mTime;
	}

	private static class TextMessageHolder extends MessageHolderBase {
		/**
		 * 文字消息体
		 */
		TextView messageContent;
	}
	
	/**
	 * 实名卡片
	 * @author xiaobu
	 *
	 */
	private static class RealProfileCardHolder extends MessageHolderBase {
		// 个人头像
		RoundedImageView mAvatar;
		TextView mName;
		TextView mPosition;
		TextView mCorp;
	}
	
	/**
	 * 匿名卡片
	 * @author xiaobu
	 *
	 */
	private static class NickProfileCardHolder extends MessageHolderBase {
		// 个人头像
		RoundedImageView mAvatar;
		TextView mName;
		TextView mPosition;
		TextView mCorp;
	}
	
	/**
	 * 职位邀请
	 * @author xiaobu
	 *
	 */
	private static class JobInvitationHolderBase extends MessageHolderBase {
		// 公司头像
		RoundedImageView mAvatar;
		TextView mName;
		TextView mSalary;
		TextView mCity;
		TextView mEduExpr;
		TextView mHighlight;
	}
	
	private static class JobInvitationHolder extends JobInvitationHolderBase {
		// 投简历
		TextView mPost;
		// 不感兴趣
		TextView mUninterest;
	}
	
	
	private static class PromptMessageHolder {
		TextView mPrompt;
	}
	
	private static class PromptComposeMessageHolder extends PromptMessageHolder {
		TextView mCompose;
	}
	
	/**
	 * 面试邀请--mine
	 * @author xiaobu
	 */
	private static class InterviewInvitationHolderBase extends MessageHolderBase {
		TextView mCorpName;
		RoundedImageView mCorpAvatar;
		TextView mPosition;
		TextView mSalary;
		TextView mCity;
//		TextView mEduExpr;
		
		TextView mInterviewTime;
		TextView mInterviewLocate;
		TextView mInterviewContact;
		TextView mInterviewPhone;
		TextView mInterviewEmail;
		TextView mInterviewDesc;
		
	}
	
	/**
	 * 面试邀请--other
	 * @author xiaobu
	 */
	private static class InterviewInvitationHolder extends InterviewInvitationHolderBase {
		// 接受邀请
		TextView mAccept;
		// 忽略邀请
		TextView mIgnore;
	}
	
	/**
	 * 摘要信息
	 * @author xiaobu
	 */
	private static class JobSummaryHolder extends MessageHolderBase {
		RoundedImageView mAvatar;
		TextView mName;
		TextView mSalary;
		TextView mCity;
		TextView mEduExpr;
		TextView mHighlight;
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// 暂时采用接口方式来实现卡片中事件的点击事件
	public interface OnCardContentClickListener {
		public void onCardContentClick(String did, String mid, int cardType, int actionType);
	}
	
	// 重发接口
	public interface OnResendClickListener {
		public void onResendClick(String did, String cmid, String message);
	}
	
}
