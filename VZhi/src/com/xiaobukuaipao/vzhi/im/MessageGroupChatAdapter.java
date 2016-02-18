package com.xiaobukuaipao.vzhi.im;

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

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;
import com.xiaobukuaipao.vzhi.GroupAnoInfoActivity;
import com.xiaobukuaipao.vzhi.GroupRealInfoActivity;
import com.xiaobukuaipao.vzhi.R;
import com.xiaobukuaipao.vzhi.WebSearchActivity;
import com.xiaobukuaipao.vzhi.cache.CommonBitmapMemoryCache;
import com.xiaobukuaipao.vzhi.contentprovider.GeneralContentProvider;
import com.xiaobukuaipao.vzhi.database.ContactUserTable;
import com.xiaobukuaipao.vzhi.database.CookieTable;
import com.xiaobukuaipao.vzhi.database.MessageGroupTable;
import com.xiaobukuaipao.vzhi.database.UserInfoTable;
import com.xiaobukuaipao.vzhi.domain.user.ImUser;
import com.xiaobukuaipao.vzhi.util.GlobalConstants;
import com.xiaobukuaipao.vzhi.util.StringUtils;
import com.xiaobukuaipao.vzhi.util.TimeHandler;
import com.xiaobukuaipao.vzhi.view.RoundedImageView;
// import com.android.volley.toolbox.ImageLoader;
// import com.nostra13.universalimageloader.core.ImageLoader;

public class MessageGroupChatAdapter extends CursorAdapter {
	public static final String TAG = MessageGroupChatAdapter.class.getSimpleName();
	
	public static final int MESSAGE_TYPE_GROUP_INVALID = -1;
	// Text
	public static final int MESSAGE_TYPE_GROUP_MINE_TEXT = 0x00;
	public static final int MESSAGE_TYPE_GROUP_OTHER_TEXT = 0x01;
	
	// 某人加入群组
	public static final int MESSAGE_TYPE_PROMPT_TEXT = 0x02;
	
	private Context context = null;
	private LayoutInflater inflater = null;
	
	private long sendId = 0;
	
	// Volley加载图片
	// 请求队列
	private RequestQueue mQueue;
	private ImageLoader mImageLoader;
	private ImageListener mListener;
	
	private Handler mHandler;
	
	private String mineAvatar = null;
	
	public OnResendClickListener mOnResendClickListener;
	
	private int anonymous = -1;
	
	public void setOnResendClickListener(OnResendClickListener mOnResendClickListener) {
		this.mOnResendClickListener = mOnResendClickListener;
	}
	
	// 缺省的构造函数--构建
	public MessageGroupChatAdapter(Context context, Cursor cursor, int flags, Handler mHandler) {
		super(context, cursor, flags);
		
		this.context = context;
		this.mHandler = mHandler;
		
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		Cursor cookieCursor = context.getContentResolver().query(GeneralContentProvider.COOKIE_CONTENT_URI, null, null, null, null);
		if (cookieCursor != null && cookieCursor.moveToFirst()) {
			sendId = (long) cookieCursor.getInt(cookieCursor.getColumnIndex(CookieTable.COLUMN_ID));
			cookieCursor.close();
		}
		
		mQueue = Volley.newRequestQueue(mContext);
		mImageLoader = new ImageLoader(mQueue, new CommonBitmapMemoryCache());
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		final int displayType = this.getItemViewType(cursor);
		
		// 此时是正常的聊天文本
		if (displayType == MESSAGE_TYPE_GROUP_MINE_TEXT) {
			// 此时是本人
			TextMessageHolder viewHolder = (TextMessageHolder) view.getTag();
			// 个人头像
			bindMinePortrait(viewHolder, view, context, cursor);
			
			final String url = cursor.getString(cursor.getColumnIndex(MessageGroupTable.COLUMN_MESSAGE_OVERVIEW));
			SpannableString content = new SpannableString(cursor.getString(cursor.getColumnIndex(MessageGroupTable.COLUMN_MESSAGE_OVERVIEW)));
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
			
			// viewHolder.messageContent.setText(cursor.getString(cursor.getColumnIndex(MessageGroupTable.COLUMN_MESSAGE_OVERVIEW)));
			
			// 设置时间戳
			bindMineTime(viewHolder, context, cursor);
			bindMineProgressBar(viewHolder, context, cursor);
		} else if (displayType == MESSAGE_TYPE_GROUP_OTHER_TEXT) {
			// 此时是Others
			TextMessageHolder viewHolder = (TextMessageHolder) view.getTag();
			
			final String url = cursor.getString(cursor.getColumnIndex(MessageGroupTable.COLUMN_MESSAGE_OVERVIEW));
			SpannableString content = new SpannableString(cursor.getString(cursor.getColumnIndex(MessageGroupTable.COLUMN_MESSAGE_OVERVIEW)));
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
			
			// viewHolder.messageContent.setText(cursor.getString(cursor.getColumnIndex(MessageGroupTable.COLUMN_MESSAGE_OVERVIEW)));
			
			// 设置对方的头像
			bindOthersPortrait(viewHolder, view, context, cursor);
			// 设置时间戳
			bindMineTime(viewHolder, context, cursor);
		} else if (displayType == MESSAGE_TYPE_PROMPT_TEXT) {
			PromptMessageHolder viewHolder = (PromptMessageHolder) view.getTag();
			bindPromptMessageHolder(viewHolder, view, context, cursor);
		} 
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		// 所有需要被赋值的holder都是基于MessageHolderBase的
		MessageHolderBase holder = null;
		// 这时，从数据库表中获得本行中的消息类型列表
		final int displayType = this.getItemViewType(cursor);
		
		View convertView = null;
		
		// 此时是正常的聊天文本
		if (displayType == MESSAGE_TYPE_GROUP_MINE_TEXT) {
			// 此时是本人
			convertView = inflater.inflate(R.layout.mine_text_message, parent, false);
			holder = new TextMessageHolder();
			convertView.setTag(holder);
			initMessageHolderBase(holder, convertView);
			initTextMessageHolder((TextMessageHolder) holder, convertView);

		} else if (displayType == MESSAGE_TYPE_GROUP_OTHER_TEXT) {
			// 此时是Others
			convertView = inflater.inflate(R.layout.other_group_text_message, parent, false);
			holder = new TextMessageHolder();
			convertView.setTag(holder);
			initMessageHolderBase(holder, convertView);
			initTextMessageHolder((TextMessageHolder) holder, convertView);
			
		} else if (displayType == MESSAGE_TYPE_PROMPT_TEXT) {
			convertView = inflater.inflate(R.layout.prompt_text_message, parent, false);
			PromptMessageHolder promptHolder = new PromptMessageHolder();
			convertView.setTag(promptHolder);
			initPromptMessageHolder(promptHolder, convertView);
		}
		return convertView;
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private void initMessageHolderBase(MessageHolderBase holder, View convertView) {
		holder.portrait = (RoundedImageView) convertView.findViewById(R.id.user_portrait);
		holder.name = (TextView) convertView.findViewById(R.id.user_name);
		holder.messageFailed = (ImageView) convertView.findViewById(R.id.message_state_failed);
		holder.loadingProgressBar = (ProgressBar) convertView.findViewById(R.id.progressBar1);
		holder.mTime = (TextView) convertView.findViewById(R.id.item_time);
	}
	
	private void initTextMessageHolder(TextMessageHolder holder, View convertView) {
		holder.messageContent = (TextView) convertView.findViewById(R.id.message_content);
	}
	
	private void initPromptMessageHolder(PromptMessageHolder holder, View convertView) {
		holder.mPrompt = (TextView) convertView.findViewById(R.id.message_content);
	}
	
	private void bindMineTime(MessageHolderBase viewHolder, Context context, Cursor cursor) {
		// 时间
		long previous;
		
		if (cursor.moveToPrevious()) {
			previous =  cursor.getLong(cursor.getColumnIndex(MessageGroupTable.COLUMN_CREATED));
			cursor.moveToNext();
			
			if ((cursor.getLong(cursor.getColumnIndex(MessageGroupTable.COLUMN_CREATED)) - previous) > TimeHandler.getInstance(mContext).getMin() * 5) {
				viewHolder.mTime.setVisibility(View.VISIBLE);
				String time = TimeHandler.getInstance(mContext).time2str(cursor.getLong(cursor.getColumnIndex(MessageGroupTable.COLUMN_CREATED)));
				viewHolder.mTime.setText(time);
			} else {
				viewHolder.mTime.setVisibility(View.GONE);
			}
		} else {
			cursor.moveToNext();
			
			viewHolder.mTime.setVisibility(View.VISIBLE);
			String time = TimeHandler.getInstance(mContext).time2str(cursor.getLong(cursor.getColumnIndex(MessageGroupTable.COLUMN_CREATED)));
			viewHolder.mTime.setText(time);
		}
	}
	
	/**
	 * 绑定本人的头像--群组中
	 * @param viewHolder
	 * @param view
	 * @param context
	 * @param cursor
	 */
	private void bindMinePortrait(final MessageHolderBase viewHolder, View view, Context context, Cursor cursor) {
		long gid = cursor.getInt(cursor.getColumnIndex(MessageGroupTable.COLUMN_MESSAGE_GROUP_ID));
		boolean isAnonymous = ImDbManager.getInstance().getGroupIsRealInContactUserTable(gid) == 1 ? true : false;
		Cursor userCursor = context.getContentResolver().query(GeneralContentProvider.USERINFO_CONTENT_URI, null, null, null, null);
		
		if (userCursor != null && userCursor.moveToFirst()) {
			if (isAnonymous) {
				// 匿名
				mineAvatar = (String) userCursor.getString(userCursor.getColumnIndex(UserInfoTable.COLUMN_AVATAR));
			} else {
				// 实名
				mineAvatar = (String) userCursor.getString(userCursor.getColumnIndex(UserInfoTable.COLUMN_REALAVATAR));
			}
			userCursor.close();
		}
		
		if (mineAvatar != null) {
			if (isAnonymous) {
				/*Picasso.with(mContext).load(mineAvatar)
		        .placeholder(R.drawable.general_default_ano)
		        .into(viewHolder.portrait);*/
				mListener = ImageLoader.getImageListener(viewHolder.portrait,  
				        R.drawable.general_default_ano, R.drawable.general_default_ano);
			} else {
				/*Picasso.with(mContext).load(mineAvatar)
		        .placeholder(R.drawable.general_user_avatar)
		        .into(viewHolder.portrait);*/
				mListener = ImageLoader.getImageListener(viewHolder.portrait,  
				        R.drawable.general_user_avatar, R.drawable.general_user_avatar);
			}
			
			mImageLoader.get(mineAvatar, mListener);
		} else {
			if (isAnonymous) {
				viewHolder.portrait.setImageResource(R.drawable.general_default_ano);
			} else {
				viewHolder.portrait.setImageResource(R.drawable.general_user_avatar);
			}
		}
	}
	
	
	/**
	 * 绑定其他人的头像--群组中
	 * @param viewHolder
	 * @param view
	 * @param context
	 * @param cursor
	 */
	private void bindOthersPortrait(final MessageHolderBase viewHolder, View view, Context context, Cursor cursor) {
		
		final long gid = cursor.getInt(cursor.getColumnIndex(MessageGroupTable.COLUMN_MESSAGE_GROUP_ID));
		// 判断群组是否是实名群
		boolean isAnonymous = ImDbManager.getInstance().getGroupIsRealInContactUserTable(gid) == 1 ? true : false;
		
		Cursor cursorContactUser = mContext.getContentResolver().query(GeneralContentProvider.CONTACT_USER_CONTENT_URI, null, 
				ContactUserTable.COLUMN_UID + " = ?" + " AND " + ContactUserTable.COLUMN_GID + " = ?", 
				new String[] { String.valueOf(cursor.getInt(cursor.getColumnIndex(MessageGroupTable.COLUMN_MESSAGE_FROM_USER_ID))), 
				String.valueOf(cursor.getInt(cursor.getColumnIndex(MessageGroupTable.COLUMN_MESSAGE_GROUP_ID)))}, null);
		
		if (cursorContactUser != null && cursorContactUser.moveToFirst()) {
			String avatar = null;
			String name = null;
			
			if (isAnonymous) {
				avatar = cursorContactUser.getString(cursorContactUser.getColumnIndex(ContactUserTable.COLUMN_NICKAVATAR));
				name = cursorContactUser.getString(cursorContactUser.getColumnIndex(ContactUserTable.COLUMN_NICKNAME));
			} else {
				avatar = cursorContactUser.getString(cursorContactUser.getColumnIndex(ContactUserTable.COLUMN_AVATAR));
				name = cursorContactUser.getString(cursorContactUser.getColumnIndex(ContactUserTable.COLUMN_NAME));
			}
			
			if (StringUtils.isNotEmpty(name)) {
				viewHolder.name.setText(name);
			} else {
				if (isAnonymous) {
					viewHolder.name.setText("匿名");
				} else {
					viewHolder.name.setText("实名");
				}
			}
			
			if (avatar != null && avatar.length() > 0) {
				if (isAnonymous) {
					/*Picasso.with(mContext).load(avatar)
			        .placeholder(R.drawable.general_default_ano)
			        .into(viewHolder.portrait);*/
					mListener = ImageLoader.getImageListener(viewHolder.portrait,  
					        R.drawable.general_default_ano, R.drawable.general_default_ano);
				} else {
					/*Picasso.with(mContext).load(avatar)
			        .placeholder(R.drawable.general_user_avatar)
			        .into(viewHolder.portrait);*/
					mListener = ImageLoader.getImageListener(viewHolder.portrait,  
					        R.drawable.general_user_avatar, R.drawable.general_user_avatar);
				}
				
				mImageLoader.get(avatar, mListener);
			} else {
				if (isAnonymous) {
					viewHolder.portrait.setImageResource(R.drawable.general_default_ano);
					viewHolder.name.setText("匿名");
				} else {
					viewHolder.portrait.setImageResource(R.drawable.general_user_avatar);
					viewHolder.name.setText("实名");
				}
			}
			
			
			
			cursorContactUser.close();
			
		} else {
			// 此时，不存在此联系人
			long uid = cursor.getInt(cursor.getColumnIndex(MessageGroupTable.COLUMN_MESSAGE_FROM_USER_ID));
			
			ImUser user = new ImUser();
			user.setUid(uid);
			user.setGid(gid);
			
			// 否则，执行网络请求
			Message msg = Message.obtain();
			msg.what = 2;
			msg.obj = user;
			mHandler.sendMessage(msg);
		}
		
		final long uid = cursor.getInt(cursor.getColumnIndex(MessageGroupTable.COLUMN_MESSAGE_FROM_USER_ID));
		
		// 头像点击事件
		viewHolder.portrait.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				
				boolean isAnonymous = ImDbManager.getInstance().getGroupIsRealInContactUserTable(gid) == 1 ? true : false;
				if (isAnonymous) {
					Intent intent = new Intent(view.getContext(), GroupAnoInfoActivity.class);
					intent.putExtra(GlobalConstants.UID, uid);
					mContext.startActivity(intent);
				} else {
					Intent intent = new Intent(view.getContext(), GroupRealInfoActivity.class);
					intent.putExtra(GlobalConstants.UID, uid);
					mContext.startActivity(intent);
				}
			}
		});
		
	}
	
	private void bindPromptMessageHolder(PromptMessageHolder viewHolder, View view, Context context, Cursor cursor) {
		viewHolder.mPrompt.setText(cursor.getString(cursor.getColumnIndex(MessageGroupTable.COLUMN_MESSAGE_OVERVIEW)));
	}
	
	private void bindMineProgressBar(MessageHolderBase viewHolder, Context context, Cursor cursor) {
		
		long status = cursor.getInt(cursor.getColumnIndex(MessageGroupTable.COLUMN_MESSAGE_STATUS));
		
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
		final String body = cursor.getString(cursor.getColumnIndex(MessageGroupTable.COLUMN_MESSAGE_OVERVIEW));
		// _id
		final long cmid = cursor.getLong(cursor.getColumnIndex(MessageGroupTable.COLUMN_ID));
		// groupId
		final long groupId = cursor.getLong(cursor.getColumnIndex(MessageGroupTable.COLUMN_MESSAGE_GROUP_ID));
		
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
		                    	mOnResendClickListener.onResendClick(String.valueOf(groupId), body, String.valueOf(cmid));
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
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////
	
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
		final int type =  cursor.getInt(cursor.getColumnIndex(MessageGroupTable.COLUMN_MESSAGE_DISPLAY_TYPE));
		
		if (type == IMConstants.DISPLAY_TYPE_GROUP_TEXT) {
			// 普通文本信息
			if (sendId == cursor.getInt(cursor.getColumnIndex(MessageGroupTable.COLUMN_MESSAGE_FROM_USER_ID))) {
				return MESSAGE_TYPE_GROUP_MINE_TEXT;
			} else {
				return MESSAGE_TYPE_GROUP_OTHER_TEXT;
			}
		} else if (type == IMConstants.DISPLAY_TYPE_GROUP_JOIN) {
			return MESSAGE_TYPE_PROMPT_TEXT;
		} else if (type == IMConstants.DISPLAY_TYPE_GROUP_QUIT) {
			return MESSAGE_TYPE_PROMPT_TEXT;
		} else if (type == IMConstants.DISPLAY_TYPE_GROUP_JOB_APPLY_READ) {
			return MESSAGE_TYPE_PROMPT_TEXT;
		} else if (type == IMConstants.DISPLAY_TYPE_GROUP_JOB_APPLY_INTEREST) {
			return MESSAGE_TYPE_PROMPT_TEXT;
		} else if (type == IMConstants.DISPLAY_TYPE_GROUP_PROMPT_TEXT) {
			return MESSAGE_TYPE_PROMPT_TEXT;
		} else {
			return MESSAGE_TYPE_GROUP_INVALID;
		}
	}

	/**
	 * 得到View的种类数目---还得再研究这里
	 */
	@Override
	public int getViewTypeCount() {
		return 3;
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private static class MessageHolderBase {
		/**
		 * 头像
		 */
		RoundedImageView portrait;
		/**
		 * 用户名
		 */
		TextView name;
		/**
		 * 消息状态
		 */
		ImageView messageFailed;
		
		ProgressBar loadingProgressBar;
		
		// 时间
		TextView mTime;
	}
	
	private static class TextMessageHolder extends MessageHolderBase {
		/**
		 * 文字消息体
		 */
		TextView messageContent;
	}
	
	private static class PromptMessageHolder {
		TextView mPrompt;
	}
	
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	// 重发接口
	public interface OnResendClickListener {
		public void onResendClick(String gid, String message, String cmid);
	}
	
}
