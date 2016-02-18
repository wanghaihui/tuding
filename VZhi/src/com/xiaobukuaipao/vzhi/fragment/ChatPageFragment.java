package com.xiaobukuaipao.vzhi.fragment;

import java.lang.ref.WeakReference;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.alibaba.fastjson.JSONObject;
import com.xiaobukuaipao.vzhi.MainRecruitActivity;
import com.xiaobukuaipao.vzhi.R;
import com.xiaobukuaipao.vzhi.contentprovider.GeneralContentProvider;
import com.xiaobukuaipao.vzhi.database.ContactUserTable;
import com.xiaobukuaipao.vzhi.database.MessageSummaryTable;
import com.xiaobukuaipao.vzhi.domain.user.ImUser;
import com.xiaobukuaipao.vzhi.event.InfoResult;
import com.xiaobukuaipao.vzhi.event.MessageSummaryChangeEvent;
import com.xiaobukuaipao.vzhi.event.SocialEventLogic;
import com.xiaobukuaipao.vzhi.im.ImDbManager;
import com.xiaobukuaipao.vzhi.im.MessageListAdapter;
import com.xiaobukuaipao.vzhi.util.GlobalConstants;
import com.xiaobukuaipao.vzhi.util.StringUtils;

import de.greenrobot.event.EventBus;

/**
 * 聊天界面<br>
 * <b>消息类型</b>:<br>
 * <p>
 * 1.职位推荐<br>
 * 2.新增投递提醒<br>
 * 3.群推荐<br>
 * 4.陌生人消息<br>
 * 5.个人消息<br>
 * 6.群消息<br>
 * </p>
 * @author hongxin.bai
 * 
 */
public class ChatPageFragment extends BaseFragment implements LoaderCallbacks<Cursor> {
	public static final String TAG = ChatPageFragment.class.getSimpleName();
	
	private SocialEventLogic mSocialEventLogic;
	private View view;
	
	// 不支持下拉刷新
	private ListView mCombineList;
	
	// 是否是第一次进入
	private boolean isFristEnter = true;
	
	private MessageListAdapter adapter;
	
	private MainRecruitActivity mainRecruitActivity;
	
	// Handler
	static class WeakHandler extends Handler {
		private WeakReference<ChatPageFragment> mOuter;
		public WeakHandler(ChatPageFragment fragment) {
			mOuter = new WeakReference<ChatPageFragment>(fragment);
		}
		
		public void handleMessage(Message msg) {
			ChatPageFragment outer = mOuter.get();
			if (outer != null) {
				// Do something
			}
		}
	}
	
	private WeakHandler mHandler = new WeakHandler(ChatPageFragment.this) {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case 1:
					ImUser user = (ImUser) msg.obj;
					// 获取个人的头像和名字信息
					getPersonAvatarAndName(user.getUid(), user.getDid());
					break;
				case 2:
					ImUser group = (ImUser) msg.obj;
					//  获取群组的头像和名字信息
					getGroupAvatarAndName(group.getGid());
					break;
				case 3:
					updateMessageSummary();
					break;
			}
		}
	};
	
	// 此时，实名头像更新
	private void  updateMessageSummary() {
		getLoaderManager().restartLoader(0, null, this);
	}


	public ChatPageFragment() {
		
	}

	public ChatPageFragment(Activity activity) {
		this.activity = activity;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_main_tab_chat,container, false);
		
		mSocialEventLogic = new SocialEventLogic();
		mSocialEventLogic.register(this);
		
		EventBus.getDefault().register(this);
		
		//获得请求
		this.view = view;
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initUIAndData();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.mainRecruitActivity = (MainRecruitActivity) activity;
	}
	
	private void initUIAndData() {
		mCombineList = (ListView) view.findViewById(R.id.chat_message_combine_list);
		
		mCombineList.setEmptyView(getActivity().findViewById(R.id.empty));
		
		// ImDbManager.getInstance().setMessageSummaryHandler(mHandler);
		
		setUIListeners();
		
		// 此时，执行网络请求，获取收件箱消息摘要
		mSocialEventLogic.getLetterSummary();
		// 继续取消息摘要--P2P和群
		mSocialEventLogic.getMessageSummary();
		
		getLoaderManager().initLoader(0, null, this);
	}
	
	private void setUIListeners() {
		
	}
	
	private void getPersonAvatarAndName(long uid, long did) {
		mSocialEventLogic.getPersonAvatarAndName(uid, did);
	}
	
	private void getGroupAvatarAndName(long gid) {
		mSocialEventLogic.getGroupAvatarAndName(gid);
	}
	
	@Override
	public void onEventMainThread(Message msg) {
		super.onEventMainThread(msg);
		if (msg.obj instanceof InfoResult) {
			InfoResult infoResult = (InfoResult) msg.obj;
			switch (msg.what) {
				case R.id.social_get_letter_summary:
					// 获取收件箱消息列表--之后将此消息摘要写入数据库
					ImDbManager.getInstance().insertLetterIntoMessageSummaryTable(infoResult.getResult());
					break;
				case R.id.social_get_message_summary:
					Log.i(TAG, "message summary : " + infoResult.getResult());
					ImDbManager.getInstance().insertP2POrGroupIntoMessageSummaryTable(infoResult.getResult());
					break;
				case R.id.social_get_person_avatar:
					// 将获得的头像和名字信息，插入到ContactUserTable表中，同时，重新加载CursorLoader
					insertPersonInfoIntoContactUserTable(infoResult.getResult());
					break;
				case R.id.social_get_group_avatar:
					// 将获得的头像和名字信息，插入到ContactUserTable表中，同时，重新加载CursorLoader
					Log.i(TAG, infoResult.getResult());
					insertGroupInfoIntoContactUserTable(infoResult.getResult());
					break;
				default:
					break;
			}
			
		}
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		mSocialEventLogic.unregister(this);
		EventBus.getDefault().unregister(this);
	}
	
	/**
	 * 插入联系人表
	 * 个人的话，1是实名，0是匿名
	 * @param userMessage
	 */
	private synchronized void insertPersonInfoIntoContactUserTable(String userMessage) {
		JSONObject jsonObject = (JSONObject) JSONObject.parse(userMessage);
		if (jsonObject != null) {
			ContentValues values = new ContentValues();
			if (jsonObject.getLong(GlobalConstants.JSON_ID) != null) {
				values.put(ContactUserTable.COLUMN_UID, jsonObject.getLong(GlobalConstants.JSON_ID));
			}
			
			if (jsonObject.getLong(GlobalConstants.JSON_ISREAL) != null) {
				values.put(ContactUserTable.COLUMN_ISREAL, jsonObject.getLong(GlobalConstants.JSON_ISREAL));
				// 如果是实名
				if (jsonObject.getLong(GlobalConstants.JSON_ISREAL) == 1) {
					if (jsonObject.getString(GlobalConstants.JSON_AVATAR) != null) {
						values.put(ContactUserTable.COLUMN_AVATAR, jsonObject.getString(GlobalConstants.JSON_AVATAR));
					}
					
					if (jsonObject.getString(GlobalConstants.JSON_NAME) != null) {
						values.put(ContactUserTable.COLUMN_NAME, jsonObject.getString(GlobalConstants.JSON_NAME));
					}
				} else {
					if (jsonObject.getString(GlobalConstants.JSON_AVATAR) != null) {
						values.put(ContactUserTable.COLUMN_NICKAVATAR, jsonObject.getString(GlobalConstants.JSON_AVATAR));
					}
					
					if (jsonObject.getString(GlobalConstants.JSON_NAME) != null) {
						values.put(ContactUserTable.COLUMN_NICKNAME, jsonObject.getString(GlobalConstants.JSON_NAME));
					}
				}
			}
			
			if (StringUtils.isNotEmpty(jsonObject.getString(GlobalConstants.JSON_DID))) {
				values.put(ContactUserTable.COLUMN_DID, Long.valueOf(jsonObject.getString(GlobalConstants.JSON_DID)));
			}
			
			if (values.size() > 0) {
				if (StringUtils.isNotEmpty(jsonObject.getString(GlobalConstants.JSON_DID))) {
					Cursor cursor = this.getActivity().getContentResolver().query(GeneralContentProvider.CONTACT_USER_CONTENT_URI, null, 
							ContactUserTable.COLUMN_UID + " = ?" + " AND " + ContactUserTable.COLUMN_DID + " = ?", 
							new String[] { String.valueOf(jsonObject.getLong(GlobalConstants.JSON_ID)) ,
							String.valueOf(jsonObject.getString(GlobalConstants.JSON_DID)) }, null);
					
					if (cursor != null && cursor.moveToFirst()) {
						// 执行更新操作
						this.getActivity().getContentResolver().update(GeneralContentProvider.CONTACT_USER_CONTENT_URI, values, 
								ContactUserTable.COLUMN_UID  + " = ?" + " AND " + ContactUserTable.COLUMN_DID + " = ?", 
								new String[] { String.valueOf(jsonObject.getLong(GlobalConstants.JSON_ID)), String.valueOf(jsonObject.getString(GlobalConstants.JSON_DID)) });
						cursor.close();
						adapter.notifyDataSetChanged();
						getLoaderManager().restartLoader(0, null, this);
					} else {
						// 插入不用考虑
						this.getActivity().getContentResolver().insert(GeneralContentProvider.CONTACT_USER_CONTENT_URI, values);
						adapter.notifyDataSetChanged();
						getLoaderManager().restartLoader(0, null, this);
					}
				}
			}
			
			values.clear();
		}
	}
	
	/**
	 * 群的话，1是匿名，0是实名
	 * @param groupMessage
	 */
	private void insertGroupInfoIntoContactUserTable(String groupMessage) {
		
		JSONObject jsonObject = (JSONObject) JSONObject.parse(groupMessage);
		JSONObject groupObject = jsonObject.getJSONObject(GlobalConstants.JSON_GROUP);
		
		if (groupObject != null) {
			ContentValues values = new ContentValues();
			
			if (groupObject.getLong(GlobalConstants.JSON_GID) != null) {
				values.put(ContactUserTable.COLUMN_GID, groupObject.getLong(GlobalConstants.JSON_GID));
			}
			if (groupObject.getString(GlobalConstants.JSON_NAME) != null) {
				values.put(ContactUserTable.COLUMN_NAME, groupObject.getString(GlobalConstants.JSON_NAME));
			}
			if (groupObject.getString(GlobalConstants.JSON_LOGO) != null) {
				values.put(ContactUserTable.COLUMN_AVATAR, groupObject.getString(GlobalConstants.JSON_LOGO));
			}
			
			if (groupObject.getInteger(GlobalConstants.JSON_TYPE) == 1001) {
				// 匿名
				values.put(ContactUserTable.COLUMN_ISREAL, 1);
			} else if (groupObject.getInteger(GlobalConstants.JSON_TYPE) == 2001) {
				// 实名
				values.put(ContactUserTable.COLUMN_ISREAL, 0);
			}
			
			values.put(ContactUserTable.COLUMN_ISGROUP, 1);
			
			if (values.size() > 0) {
				this.getActivity().getContentResolver().insert(GeneralContentProvider.CONTACT_USER_CONTENT_URI, values);
				// 此时，执行完插入数据库操作
				// 重新加载CursorLoader
				adapter.notifyDataSetChanged();
				getLoaderManager().restartLoader(0, null, this);
			}
			
			values.clear();
		}
	}
	
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		// 按照更新时间排序
		return new CursorLoader(this.getActivity(), GeneralContentProvider.MESSAGE_SUMMARY_CONTENT_URI, null, null, null, MessageSummaryTable.COLUMN_UPDATED + " desc");
	}
	
	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		
		if (cursor != null && cursor.moveToFirst()) {
			int unreadCount = ImDbManager.getInstance().getTotalUnreadCount(cursor);
			Log.i(TAG, "unreadCount : " + unreadCount);
			mainRecruitActivity.onMsgUnread(1, unreadCount);//聊天
			cursor.moveToFirst();
		}
		
		if (isFristEnter) {
			adapter = new MessageListAdapter(this.getActivity(), cursor, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER, mHandler);
			mCombineList.setAdapter(adapter);
			isFristEnter = false;
		}
		
		adapter.swapCursor(cursor);

	}
	
	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		adapter.swapCursor(null);
	}
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * EventBus回调
	 * @param event
	 */
	public void onEvent(MessageSummaryChangeEvent event) {
		Message msg = Message.obtain();
		msg.what = event.getWhat();
		mHandler.sendMessage(msg);
	}
}
