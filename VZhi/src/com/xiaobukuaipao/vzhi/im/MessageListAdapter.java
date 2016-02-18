package com.xiaobukuaipao.vzhi.im;

import java.util.HashMap;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.umeng.analytics.MobclickAgent;
import com.xiaobukuaipao.vzhi.CandidateActivity;
import com.xiaobukuaipao.vzhi.ChatGroupActivity;
import com.xiaobukuaipao.vzhi.ChatPersonActivity;
import com.xiaobukuaipao.vzhi.ContactsStrangersActivity;
import com.xiaobukuaipao.vzhi.GroupRecActivity;
import com.xiaobukuaipao.vzhi.PositionRecActivity;
import com.xiaobukuaipao.vzhi.R;
import com.xiaobukuaipao.vzhi.contentprovider.GeneralContentProvider;
import com.xiaobukuaipao.vzhi.database.ContactUserTable;
import com.xiaobukuaipao.vzhi.database.MessageSummaryTable;
import com.xiaobukuaipao.vzhi.domain.user.ImUser;
import com.xiaobukuaipao.vzhi.im.ImItemController.ItemActionListener;
import com.xiaobukuaipao.vzhi.util.StringUtils;
import com.xiaobukuaipao.vzhi.util.TimeHandler;
import com.xiaobukuaipao.vzhi.view.RoundedImageView;

public class MessageListAdapter extends CursorAdapter {
	
	public static final String TAG = MessageListAdapter.class.getSimpleName();
	
	public static final int MESSAGE_LIST_TYPE_INVALID = -1;
	// Text
	public static final int MESSAGE_LIST_TYPE_JOB_APPLY = 0x00;
	public static final int MESSAGE_LIST_TYPE_STRANGER_LETTER = 0x01;
	public static final int MESSAGE_LIST_TYPE_JOB_RECOMMEND = 0x02;
	public static final int MESSAGE_LIST_TYPE_GROUP_RECOMMEND = 0x03;
	public static final int MESSAGE_LIST_TYPE_PERSON = 0x04;
	public static final int MESSAGE_LIST_TYPE_GROUP = 0x05;
	
	private Context mContext;
	private LayoutInflater inflater = null;
	
//	private long sendId = 0;
	
	// Volley加载图片
	// 请求队列
	
	private Handler mHandler;
	
	// 缺省的构造函数--构建
	public MessageListAdapter(Context context, Cursor c, int flags, Handler handler) {
		super(context, c, flags);
		
		this.mContext = context;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
//		Cursor cookieCursor = context.getContentResolver().query(GeneralContentProvider.COOKIE_CONTENT_URI, null, null, null, null);
//		if (cookieCursor != null && cookieCursor.moveToFirst()) {
//			sendId = (long) cookieCursor.getInt(cookieCursor.getColumnIndex(CookieTable.COLUMN_ID));
//			cookieCursor.close();
//		}
		
		mHandler = handler;
	}

	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		final int displayType = this.getItemViewType(cursor);
		final MessageListHolder viewHolder = (MessageListHolder) view.getTag();
		
		if (displayType == MESSAGE_LIST_TYPE_JOB_APPLY) {
			viewHolder.portrait.setImageResource(R.drawable.msg_new_reminder);
			viewHolder.mTitle.setText(mContext.getResources().getString(R.string.new_message_reminder));
			viewHolder.mReminder.setText(cursor.getString(cursor.getColumnIndex(MessageSummaryTable.COLUMN_MESSAGE_OVERVIEW)));
			String time = TimeHandler.getInstance(mContext).time2str(cursor.getLong(cursor.getColumnIndex(MessageSummaryTable.COLUMN_UPDATED)));
			viewHolder.mTime.setText(time);
			// TimeHandler.getInstance(mContext).resetType();
			final long unReadCount = cursor.getLong(cursor.getColumnIndex(MessageSummaryTable.COLUMN_UNREADCOUNT));
			if (unReadCount > 0) {
				viewHolder.mUnread.setVisibility(View.VISIBLE);
				viewHolder.mUnread.setText(String.valueOf(unReadCount));
			} else {
				viewHolder.mUnread.setVisibility(View.GONE);
			}
			
			viewHolder.mMessageListItem.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					Intent intent = new Intent();
					intent.setClass(mContext, CandidateActivity.class);
					intent.putExtra("current_position", 0);
					intent.putExtra("unread_count", Integer.valueOf(String.valueOf(unReadCount)));
					mContext.startActivity(intent);
				}
			});
			
		} else if (displayType == MESSAGE_LIST_TYPE_STRANGER_LETTER) {
			viewHolder.portrait.setImageResource(R.drawable.msg_stranger_news);
			viewHolder.mTitle.setText(mContext.getResources().getString(R.string.stranger_message));
			viewHolder.mReminder.setText(cursor.getString(cursor.getColumnIndex(MessageSummaryTable.COLUMN_MESSAGE_OVERVIEW)));
			String time = TimeHandler.getInstance(mContext).time2str(cursor.getLong(cursor.getColumnIndex(MessageSummaryTable.COLUMN_UPDATED)));
			viewHolder.mTime.setText(time);
			// TimeHandler.getInstance(mContext).resetType();
			long unReadCount = cursor.getLong(cursor.getColumnIndex(MessageSummaryTable.COLUMN_UNREADCOUNT));
			if (unReadCount > 0) {
				viewHolder.mUnread.setVisibility(View.VISIBLE);
				viewHolder.mUnread.setText(String.valueOf(unReadCount));
			} else {
				viewHolder.mUnread.setVisibility(View.GONE);
			}
			
			viewHolder.mMessageListItem.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					
					MobclickAgent.onEvent(mContext,"msr");
					
					Intent intent = new Intent();
					intent.setClass(mContext, ContactsStrangersActivity.class);
					mContext.startActivity(intent);
					
					// 更新数据库
					// 消息列表中的陌生人消息的未读数清空
					ImDbManager.getInstance().cleanMessageListStrangerCount();
					
				}
			});
			
			//长按陌生人消息
			viewHolder.mMessageListItem.setOnLongClickListener(new ImItemController().setActionListener(new ItemActionListener() {
				
				@Override
				public void onItemDelete() {
		        	ImDbManager.getInstance().deleteStrangerLetter();
				}
				
				@Override
				public String getTitle() {
					
					return viewHolder.mTitle.getText().toString();
				}
			}));
		} else if (displayType == MESSAGE_LIST_TYPE_JOB_RECOMMEND) {
			viewHolder.portrait.setImageResource(R.drawable.msg_recommended_posts);
			viewHolder.mTitle.setText(mContext.getResources().getString(R.string.job_recommend));
			viewHolder.mReminder.setText(cursor.getString(cursor.getColumnIndex(MessageSummaryTable.COLUMN_MESSAGE_OVERVIEW)));
			String time = TimeHandler.getInstance(mContext).time2str(cursor.getLong(cursor.getColumnIndex(MessageSummaryTable.COLUMN_UPDATED)));
			viewHolder.mTime.setText(time);
			// TimeHandler.getInstance(mContext).resetType();
			long unReadCount = cursor.getLong(cursor.getColumnIndex(MessageSummaryTable.COLUMN_UNREADCOUNT));
			if (unReadCount > 0) {
				viewHolder.mUnread.setVisibility(View.VISIBLE);
				viewHolder.mUnread.setText(String.valueOf(unReadCount));
			} else {
				viewHolder.mUnread.setVisibility(View.GONE);
			}
			
			viewHolder.mMessageListItem.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					Intent intent = new Intent();
					intent.setClass(mContext, PositionRecActivity.class);
					mContext.startActivity(intent);
					
					ImDbManager.getInstance().cleanJobRecommendCount();
				}
			});
			
			//长按陌生人消息
			viewHolder.mMessageListItem.setOnLongClickListener(new ImItemController().setActionListener(new ItemActionListener() {
				
				@Override
				public void onItemDelete() {
		        	ImDbManager.getInstance().deleteJobRecMessage();
				}
				
				@Override
				public String getTitle() {
					
					return viewHolder.mTitle.getText().toString();
				}
			}));
		} else if (displayType == MESSAGE_LIST_TYPE_GROUP_RECOMMEND) {
			viewHolder.portrait.setImageResource(R.drawable.msg_recommended_group);
			viewHolder.mTitle.setText(mContext.getResources().getString(R.string.group_recommend));
			viewHolder.mReminder.setText(cursor.getString(cursor.getColumnIndex(MessageSummaryTable.COLUMN_MESSAGE_OVERVIEW)));
			String time = TimeHandler.getInstance(mContext).time2str(cursor.getLong(cursor.getColumnIndex(MessageSummaryTable.COLUMN_UPDATED)));
			viewHolder.mTime.setText(time);
			// TimeHandler.getInstance(mContext).resetType();
			long unReadCount = cursor.getLong(cursor.getColumnIndex(MessageSummaryTable.COLUMN_UNREADCOUNT));
			if (unReadCount > 0) {
				viewHolder.mUnread.setVisibility(View.VISIBLE);
				viewHolder.mUnread.setText(String.valueOf(unReadCount));
			} else {
				viewHolder.mUnread.setVisibility(View.GONE);
			}
			
			viewHolder.mMessageListItem.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					Intent intent = new Intent();
					intent.setClass(mContext, GroupRecActivity.class);
					mContext.startActivity(intent);
					
					// 更新数据库
					ContentValues values = new ContentValues();
					values.put(MessageSummaryTable.COLUMN_UNREADCOUNT, 0);
					mContext.getContentResolver().update(GeneralContentProvider.MESSAGE_SUMMARY_CONTENT_URI, values, 
							MessageSummaryTable.COLUMN_MESSAGE_DISPLAY_TYPE + " = ?", new String[] { String.valueOf(IMConstants.DISPLAY_LIST_TYPE_GROUPRECOMMEND) });
					values.clear();
				}
			});
			
			//长按群推荐
			viewHolder.mMessageListItem.setOnLongClickListener(new ImItemController().setActionListener(new ItemActionListener() {
				
				@Override
				public void onItemDelete() {
					 // 首先将此人对应的Summary数据删除
		        	// 其次，将与此人的本地聊天数据库删除
		        	ImDbManager.getInstance().deleteGroupRecMessage();
				}
				
				@Override
				public String getTitle() {
					
					return viewHolder.mTitle.getText().toString();
				}
			}));
			
		} else if (displayType == MESSAGE_LIST_TYPE_PERSON) {
			// 此时，先查找数据库的ContactUserTable表，如果能查到当前Person的avatar和name信息，则赋值，否则，到UI线程执行网络请求，更新数据库表，之后，重新加载CursorLoader
			final Cursor cursorContactUser = mContext.getContentResolver().query(GeneralContentProvider.CONTACT_USER_CONTENT_URI, null, 
					ContactUserTable.COLUMN_UID + " = ?" + " AND " + ContactUserTable.COLUMN_DID + " = ?", 
					new String[] { String.valueOf(cursor.getInt(cursor.getColumnIndex(MessageSummaryTable.COLUMN_MESSAGE_FROM_USER_ID))), 
					String.valueOf(cursor.getInt(cursor.getColumnIndex(MessageSummaryTable.COLUMN_MESSAGE_DIALOG_ID)))}, null);
			
			if (cursorContactUser != null && cursorContactUser.moveToFirst()) {
				// 此时，此联系人已经存在数据库表中
				// bindPortraitHolder(viewHolder, view, context, cursor);
				boolean isReal = cursorContactUser.getLong(cursorContactUser.getColumnIndex(ContactUserTable.COLUMN_ISREAL)) == 1 ? true : false;
				
				// 首先设置头像
				String avatar = null;
				if (isReal) {
					avatar = cursorContactUser.getString(cursorContactUser.getColumnIndex(ContactUserTable.COLUMN_AVATAR));
				} else {
					avatar = cursorContactUser.getString(cursorContactUser.getColumnIndex(ContactUserTable.COLUMN_NICKAVATAR));
				}
				
				if (avatar != null && avatar.length() > 0) {
					if (isReal) {
						// mListener = ImageLoader.getImageListener(viewHolder.portrait, R.drawable.general_user_avatar, R.drawable.general_user_avatar);
						Picasso.with(mContext).load(avatar)
				        .placeholder(R.drawable.general_user_avatar)
				        .into(viewHolder.portrait);
					} else {
						// mListener = ImageLoader.getImageListener(viewHolder.portrait, R.drawable.general_default_ano, R.drawable.general_default_ano);
						Picasso.with(mContext).load(avatar)
				        .placeholder(R.drawable.general_default_ano)
				        .into(viewHolder.portrait);
					}
					// mImageLoader.get(avatar, mListener);
				} else {
					if (isReal) {
						viewHolder.portrait.setImageResource(R.drawable.general_user_avatar);
					} else {
						viewHolder.portrait.setImageResource(R.drawable.general_default_ano);
					}
				}
				
				String name = null;
				// 名字
				if (isReal) {
					name = cursorContactUser.getString(cursorContactUser.getColumnIndex(ContactUserTable.COLUMN_NAME));
				} else {
					name = cursorContactUser.getString(cursorContactUser.getColumnIndex(ContactUserTable.COLUMN_NICKNAME));
				}
				
				if (!StringUtils.isEmpty(name)) {
					viewHolder.mTitle.setText(name);
				} else {
					viewHolder.mTitle.setText("未完善");
				}
				
				// 一定要关闭Cursor
				cursorContactUser.close();
			} else {
				long uid = cursor.getInt(cursor.getColumnIndex(MessageSummaryTable.COLUMN_MESSAGE_FROM_USER_ID));
				long did = cursor.getInt(cursor.getColumnIndex(MessageSummaryTable.COLUMN_MESSAGE_DIALOG_ID));
				
				ImUser user = new ImUser();
				user.setUid(uid);
				user.setDid(did);
				// 否则，执行网络请求
				Message msg = Message.obtain();
				msg.what = 1;
				msg.obj = user;
				mHandler.sendMessage(msg);
			}
			
			viewHolder.mReminder.setText(cursor.getString(cursor.getColumnIndex(MessageSummaryTable.COLUMN_MESSAGE_OVERVIEW)));
			
			// 时间
			String time = TimeHandler.getInstance(mContext).time2str(cursor.getLong(cursor.getColumnIndex(MessageSummaryTable.COLUMN_UPDATED)));
			viewHolder.mTime.setText(time);
			
			// 未读数
			long unReadCount = cursor.getLong(cursor.getColumnIndex(MessageSummaryTable.COLUMN_UNREADCOUNT));
			if (unReadCount > 0) {
				viewHolder.mUnread.setVisibility(View.VISIBLE);
				viewHolder.mUnread.setText(String.valueOf(unReadCount));
			} else {
				viewHolder.mUnread.setVisibility(View.GONE);
			}
			
			final long did = cursor.getInt(cursor.getColumnIndex(MessageSummaryTable.COLUMN_MESSAGE_DIALOG_ID));
			final long unRead = cursor.getInt(cursor.getColumnIndex(MessageSummaryTable.COLUMN_UNREADCOUNT));
			final long sendId = cursor.getInt(cursor.getColumnIndex(MessageSummaryTable.COLUMN_MESSAGE_FROM_USER_ID));
			
			// 个人点击事件
			viewHolder.mMessageListItem.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					String name = getPersonName(did, sendId);
					MobclickAgent.onEvent(mContext,"dgxx");
					
					Intent intent = new Intent();
					intent.setClass(mContext, ChatPersonActivity.class);
					intent.putExtra("did", did);
					if (name != null) {
						intent.putExtra("dname", name);
					}
					intent.putExtra("sender", sendId);
					mContext.startActivity(intent);
					
					// 更新数据库
					if (unRead > 0) {
						ImDbManager.getInstance().cleanMessageListOtherCount(did);
					}
				}

			});
			
			
			viewHolder.mMessageListItem.setOnLongClickListener(new ImItemController().setActionListener(new ItemActionListener() {
				
				@Override
				public void onItemDelete() {
					 // 首先将此人对应的Summary数据删除
		        	// 其次，将与此人的本地聊天数据库删除
		        	ImDbManager.getInstance().deleteP2PMessage(did);
				}
				
				@Override
				public String getTitle() {
					String name = getPersonName(did, sendId);
					
					return name;
				}
			}));
			
		} else if (displayType == MESSAGE_LIST_TYPE_GROUP) {
			// 群组--首先查找在ContactUser表中是否存在此群组的信息
			final Cursor cursorContactGroup = mContext.getContentResolver().query(GeneralContentProvider.CONTACT_USER_CONTENT_URI, null, 
					ContactUserTable.COLUMN_GID + " = ?" + " AND " + ContactUserTable.COLUMN_ISGROUP + " = ?", 
					new String[] { String.valueOf(cursor.getInt(cursor.getColumnIndex(MessageSummaryTable.COLUMN_MESSAGE_GROUP_ID))), String.valueOf(1)}, null);
			
			if (cursorContactGroup != null && cursorContactGroup.moveToFirst()) {
				// 此时，此群组已经存在数据库表中--群组没有实名头像与匿名头像一说
				String avatar = cursorContactGroup.getString(cursorContactGroup.getColumnIndex(ContactUserTable.COLUMN_AVATAR));
				boolean isAnonymous = cursorContactGroup.getLong(cursorContactGroup.getColumnIndex(ContactUserTable.COLUMN_ISREAL)) == 1 ? true : false;
				
				if (avatar != null && avatar.length() > 0) {
					// bindGroupPortraitHolder(viewHolder, view, context, cursor);
					if (avatar != null && avatar.length() > 0) {
						
						if (isAnonymous) {
							/*mListener = ImageLoader.getImageListener(viewHolder.portrait, R.drawable.group_recruit,R.drawable.group_recruit);
							mImageLoader.get(avatar, mListener);*/
							Picasso.with(mContext).load(avatar)
					        .placeholder(R.drawable.group_recruit)
					        .into(viewHolder.portrait);
						} else {
							/*mListener = ImageLoader.getImageListener(viewHolder.portrait, R.drawable.group_connect,R.drawable.group_connect);
							mImageLoader.get(avatar, mListener);*/
							Picasso.with(mContext).load(avatar)
					        .placeholder(R.drawable.group_connect)
					        .into(viewHolder.portrait);
						}
					}
				} else {
					if (isAnonymous) {
						// 匿名，就是投递群
						viewHolder.portrait.setImageResource(R.drawable.group_recruit);
					} else {
						// 实名，就是兴趣群
						viewHolder.portrait.setImageResource(R.drawable.group_connect);
					}
				}
				
				String name = cursorContactGroup.getString(cursorContactGroup.getColumnIndex(ContactUserTable.COLUMN_NAME));
				if (!StringUtils.isEmpty(name)) {
					viewHolder.mTitle.setText(name);
				} else {
					viewHolder.mTitle.setText("未完善");
				}
				
				cursorContactGroup.close();
			} else {
				long uid = cursor.getInt(cursor.getColumnIndex(MessageSummaryTable.COLUMN_MESSAGE_FROM_USER_ID));
				long gid = cursor.getInt(cursor.getColumnIndex(MessageSummaryTable.COLUMN_MESSAGE_GROUP_ID));
				
				ImUser user = new ImUser();
				user.setGid(gid);
				user.setUid(uid);
				// 否则，执行网络请求
				Message msg = Message.obtain();
				msg.what = 2;
				msg.obj = user;
				mHandler.sendMessage(msg);
			}
			
			// Summary--摘要
			viewHolder.mReminder.setText(cursor.getString(cursor.getColumnIndex(MessageSummaryTable.COLUMN_MESSAGE_OVERVIEW)));
			
			// 时间
			String time = TimeHandler.getInstance(mContext).time2str(cursor.getLong(cursor.getColumnIndex(MessageSummaryTable.COLUMN_UPDATED)));
			viewHolder.mTime.setText(time);
			// 未读数
			long unReadCount = cursor.getLong(cursor.getColumnIndex(MessageSummaryTable.COLUMN_UNREADCOUNT));
			if (unReadCount > 0) {
				viewHolder.mUnread.setVisibility(View.VISIBLE);
				viewHolder.mUnread.setText(String.valueOf(unReadCount));
			} else {
				viewHolder.mUnread.setVisibility(View.GONE);
			}
			
			// 群组点击事件
			final long gid = cursor.getInt(cursor.getColumnIndex(MessageSummaryTable.COLUMN_MESSAGE_GROUP_ID));
			final long unRead = cursor.getInt(cursor.getColumnIndex(MessageSummaryTable.COLUMN_UNREADCOUNT));
			
			// 个人点击事件
			viewHolder.mMessageListItem.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {

					
					Cursor cursorContactGroup = mContext.getContentResolver().query(GeneralContentProvider.CONTACT_USER_CONTENT_URI, null, 
							ContactUserTable.COLUMN_GID + " = ?" + " AND " + ContactUserTable.COLUMN_ISGROUP + " = ?", 
							new String[] { String.valueOf(gid), String.valueOf(1)}, null);
					String name = null;
					if (cursorContactGroup != null && cursorContactGroup.moveToFirst()) {
						name = cursorContactGroup.getString(cursorContactGroup.getColumnIndex(ContactUserTable.COLUMN_NAME));
						cursorContactGroup.close();
					}
					
					HashMap<String,String> map = new HashMap<String,String>();
					map.put("groudId",String.valueOf(gid));
					MobclickAgent.onEvent(mContext,"qdj");
					
					Intent intent = new Intent();
					intent.setClass(mContext, ChatGroupActivity.class);
					intent.putExtra("gid", String.valueOf(gid));
					if (name != null) {
						intent.putExtra("gname", name);
					}
					mContext.startActivity(intent);
					
					// 更新数据库
					if (unRead > 0) {
						ContentValues values = new ContentValues();
						values.put(MessageSummaryTable.COLUMN_UNREADCOUNT, 0);
						mContext.getContentResolver().update(GeneralContentProvider.MESSAGE_SUMMARY_CONTENT_URI, values, 
								MessageSummaryTable.COLUMN_MESSAGE_DISPLAY_TYPE + " = ?" + " AND " + MessageSummaryTable.COLUMN_MESSAGE_GROUP_ID + " = ?", 
								new String[] { String.valueOf(IMConstants.DISPLAY_LIST_TYPE_DIALOG_GROUP), String.valueOf(gid) });
						values.clear();
					}
				}
			});
			
			
			viewHolder.mMessageListItem.setOnLongClickListener(new ImItemController().setActionListener(new ItemActionListener() {
				
				@Override
				public void onItemDelete() {
					ImDbManager.getInstance().deleteGroupMessage(gid);
				}
				
				@Override
				public String getTitle() {
					Cursor cursorContactGroup = mContext.getContentResolver().query(GeneralContentProvider.CONTACT_USER_CONTENT_URI, null, ContactUserTable.COLUMN_GID + " = ?" + " AND " + ContactUserTable.COLUMN_ISGROUP + " = ?", 
							new String[] { String.valueOf(gid), String.valueOf(1)}, null);
					String name = null;
					if (cursorContactGroup != null && cursorContactGroup.moveToFirst()) {
						name = cursorContactGroup.getString(cursorContactGroup.getColumnIndex(ContactUserTable.COLUMN_NAME));
						cursorContactGroup.close();
					}
					return name;
				}
			}));
			
		}
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		MessageListHolder holder = null;
		
		View convertView = null;
		
		convertView = inflater.inflate(R.layout.item_msg_def, parent, false);
		holder = new MessageListHolder();
		convertView.setTag(holder);
		initMessageListHolder((MessageListHolder) holder, convertView);
			
		return convertView;
	}
	//////////////////////////////////////////////////////////////////////////////////////////////////////////
	
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
		final int type =  cursor.getInt(cursor.getColumnIndex(MessageSummaryTable.COLUMN_MESSAGE_DISPLAY_TYPE));
		
		if (type == IMConstants.DISPLAY_LIST_TYPE_JOBAPPLY) {
			// 新增投递简历
			return MESSAGE_LIST_TYPE_JOB_APPLY;
		} else if (type == IMConstants.DISPLAY_LIST_TYPE_STRANGERLETTER) {
			// 陌生人提醒
			return MESSAGE_LIST_TYPE_STRANGER_LETTER;
		} else if (type == IMConstants.DISPLAY_LIST_TYPE_JOBRECOMMEND) {
			// 职位推荐
			return MESSAGE_LIST_TYPE_JOB_RECOMMEND;
		} else if (type == IMConstants.DISPLAY_LIST_TYPE_GROUPRECOMMEND) {
			// 群推荐
			return MESSAGE_LIST_TYPE_GROUP_RECOMMEND;
		} else if (type == IMConstants.DISPLAY_LIST_TYPE_DIALOG_PERSON) {
			// did
			return MESSAGE_LIST_TYPE_PERSON;
		} else if (type == IMConstants.DISPLAY_LIST_TYPE_DIALOG_GROUP) {
			// gid
			return MESSAGE_LIST_TYPE_GROUP;
		} else {
			return MESSAGE_LIST_TYPE_INVALID;
		}
	}

	/**
	 * 得到View的种类数目---还得再研究这里
	 */
	@Override
	public int getViewTypeCount() {
		return 6;
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private static class MessageListHolder {
		LinearLayout mMessageListItem;
		/**
		 * 头像
		 */
		RoundedImageView portrait;
		TextView mTitle;
		TextView mReminder;
		TextView mTime;
		TextView mUnread;
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private void initMessageListHolder(MessageListHolder holder, View convertView) {
		holder.mMessageListItem = (LinearLayout) convertView.findViewById(R.id.message_list_item);
		holder.portrait = (RoundedImageView) convertView.findViewById(R.id.msg_icon);
		holder.mTitle = (TextView) convertView.findViewById(R.id.msg_title);
		holder.mReminder = (TextView) convertView.findViewById(R.id.msg_reminder);
		holder.mTime = (TextView) convertView.findViewById(R.id.msg_time);
		holder.mUnread = (TextView) convertView.findViewById(R.id.msg_count);
	}
	

	/**
	 * 根据 对话框id和发送者id查询该人的姓名
	 * 
	 * @param did
	 * @param sendId
	 * @return
	 */
	private String getPersonName(final long did, final long sendId) {
		Cursor cursorContactUser = mContext.getContentResolver().query(GeneralContentProvider.CONTACT_USER_CONTENT_URI, null, 
				ContactUserTable.COLUMN_UID + " = ?" + " AND " + ContactUserTable.COLUMN_DID + " = ?", 
				new String[] { String.valueOf(sendId), 
				String.valueOf(did)}, null);
		
		String name = null;
		
		if (cursorContactUser != null && cursorContactUser.moveToFirst()) {
			// 此时，此联系人已经存在数据库表中
			boolean isReal = cursorContactUser.getLong(cursorContactUser.getColumnIndex(ContactUserTable.COLUMN_ISREAL)) == 1 ? true : false;
			// 名字
			if (isReal) {
				name = cursorContactUser.getString(cursorContactUser.getColumnIndex(ContactUserTable.COLUMN_NAME));
			} else {
				name = cursorContactUser.getString(cursorContactUser.getColumnIndex(ContactUserTable.COLUMN_NICKNAME));
			}
			// 一定要关闭Cursor
			cursorContactUser.close();
		} else {
			name = "未完善";
		}
		return name;
	}
	
	
	/**
	 * 绑定个人头像
	 * @param viewHolder
	 * @param view
	 * @param context
	 * @param cursor
	 */
	/*private synchronized void bindPortraitHolder(final MessageListHolder viewHolder, View view, Context context, Cursor cursor) {
		
		final Cursor cursorContactUser = mContext.getContentResolver().query(GeneralContentProvider.CONTACT_USER_CONTENT_URI, null, 
				ContactUserTable.COLUMN_UID + " = ?" + " AND " + ContactUserTable.COLUMN_DID + " = ?", 
				new String[] { String.valueOf(cursor.getInt(cursor.getColumnIndex(MessageSummaryTable.COLUMN_MESSAGE_FROM_USER_ID))), 
				String.valueOf(cursor.getInt(cursor.getColumnIndex(MessageSummaryTable.COLUMN_MESSAGE_DIALOG_ID)))}, null);
		
		if (cursorContactUser != null && cursorContactUser.moveToFirst()) {
			// 此时，此联系人已经存在数据库表中
			boolean isReal = cursorContactUser.getLong(cursorContactUser.getColumnIndex(ContactUserTable.COLUMN_ISREAL)) == 1 ? true : false;
			// 首先设置头像
			String avatar = null;
			if (isReal) {
				avatar = cursorContactUser.getString(cursorContactUser.getColumnIndex(ContactUserTable.COLUMN_AVATAR));
			} else {
				avatar = cursorContactUser.getString(cursorContactUser.getColumnIndex(ContactUserTable.COLUMN_NICKAVATAR));
			}
			
			if (avatar != null && avatar.length() > 0) {
				if (isReal) {
					mListener = ImageLoader.getImageListener(viewHolder.portrait, R.drawable.general_user_avatar, R.drawable.general_user_avatar);
				} else {
					mListener = ImageLoader.getImageListener(viewHolder.portrait, R.drawable.general_default_ano, R.drawable.general_default_ano);
				}
				mImageLoader.get(avatar, mListener);
			} else {
				if (isReal) {
					viewHolder.portrait.setImageResource(R.drawable.general_user_avatar);
				} else {
					viewHolder.portrait.setImageResource(R.drawable.general_default_ano);
				}
			}
			
			// 一定要关闭Cursor
			cursorContactUser.close();
		} 
	}*/
	
	/**
	 * 绑定群头像
	 * @param viewHolder
	 * @param view
	 * @param context
	 * @param cursor
	 */
	/*
	private synchronized void bindGroupPortraitHolder(final MessageListHolder viewHolder, View view, Context context, Cursor cursor) {
		final Cursor cursorContactUser = mContext.getContentResolver().query(GeneralContentProvider.CONTACT_USER_CONTENT_URI, null, 
				ContactUserTable.COLUMN_GID + " = ?" + " AND " + ContactUserTable.COLUMN_ISGROUP + " = ?", 
				new String[] { String.valueOf(cursor.getInt(cursor.getColumnIndex(MessageSummaryTable.COLUMN_MESSAGE_GROUP_ID))), 
				String.valueOf(1)}, null);
		
		if (cursorContactUser != null && cursorContactUser.moveToFirst()) {
			// 此时，此联系人已经存在数据库表中
			
			// 首先设置头像
			String avatar = cursorContactUser.getString(cursorContactUser.getColumnIndex(ContactUserTable.COLUMN_AVATAR));
			
			if (avatar != null && avatar.length() > 0) {
				boolean isAnonymous = cursorContactUser.getLong(cursorContactUser.getColumnIndex(ContactUserTable.COLUMN_ISREAL)) == 1 ? true : false;
				if (isAnonymous) {
					mListener = ImageLoader.getImageListener(viewHolder.portrait, R.drawable.group_recruit,R.drawable.group_recruit);
					mImageLoader.get(avatar, mListener);
					Picasso.with(mContext).load(avatar)
			        .placeholder(R.drawable.group_recruit)
			        .into(viewHolder.portrait);
				} else {
					mListener = ImageLoader.getImageListener(viewHolder.portrait, R.drawable.group_connect,R.drawable.group_connect);
					mImageLoader.get(avatar, mListener);
					Picasso.with(mContext).load(avatar)
			        .placeholder(R.drawable.group_connect)
			        .into(viewHolder.portrait);
				}
			}
			
			// 一定要关闭Cursor
			cursorContactUser.close();
		}
	}*/
	
}
