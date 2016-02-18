package com.xiaobukuaipao.vzhi;

import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

import java.lang.ref.WeakReference;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xiaobukuaipao.vzhi.contentprovider.GeneralContentProvider;
import com.xiaobukuaipao.vzhi.database.ContactUserTable;
import com.xiaobukuaipao.vzhi.database.CookieTable;
import com.xiaobukuaipao.vzhi.database.MessageSummaryTable;
import com.xiaobukuaipao.vzhi.database.MessageTable;
import com.xiaobukuaipao.vzhi.domain.user.ImUser;
import com.xiaobukuaipao.vzhi.event.InfoResult;
import com.xiaobukuaipao.vzhi.event.P2PMessageEvent;
import com.xiaobukuaipao.vzhi.im.IMConstants;
import com.xiaobukuaipao.vzhi.im.ImDbManager;
import com.xiaobukuaipao.vzhi.im.ImUtils;
import com.xiaobukuaipao.vzhi.im.MessageChatAdapter;
import com.xiaobukuaipao.vzhi.im.MessageChatAdapter.OnCardContentClickListener;
import com.xiaobukuaipao.vzhi.im.MessageChatAdapter.OnResendClickListener;
import com.xiaobukuaipao.vzhi.util.GlobalConstants;
import com.xiaobukuaipao.vzhi.util.StringUtils;
import com.xiaobukuaipao.vzhi.util.VToast;
import com.xiaobukuaipao.vzhi.view.ProgressBarCircularIndeterminate;
import com.xiaobukuaipao.vzhi.wrap.ChatWrapActivity;

/**
 * 聊天结构
 * 1.现在进入聊天页，首先获取从上页传过来的receive id, did
 * 2.CursorLoader加载20条数据, 此时从数据库里读，同时判断最后一条的已读状态
 * 3.接着读取新的消息，如果存在最新的消息，此时将返回的数据，更新到Message表中; 如果不存在最新的消息，则不改变
 * 4.执行下拉刷新，拉取历史消息--
 * 情况1：如果此时，不存在历史消息，则从数据库里加载，每次20条，直至加载完毕                                                   
 * 情况2：如果存在历史消息，则先将历史消息，拉取完毕，并保存到数据库中，接着排序记载X+20条(X:已有的消息数量)消息
 *                                                                
 * 5.重点问题--数据库断层问题--待解决
 */
public class ChatPersonActivity extends ChatWrapActivity implements LoaderCallbacks<Cursor>, 
									OnCardContentClickListener, OnResendClickListener {
	
	public static final String TAG = ChatPersonActivity.class.getSimpleName();
	//  从服务器端最多拉取的聊天数量，实际拉取的数量可能小于30条
	private static final int MAX_PULL_NUM = 30;
	// 本地，每次从数据库中取的条数--30条
	private static final int MAX_PULL_DATABASE_NUM = 30;
	
	// private XListView mChatList;
	private ListView mChatList;
	
	private EditText mInputEdit;
//	private Button mAddBtn;
	private Button mSendBtn;
	
//	private LinearLayout mLayoutMore;
//	private LinearLayout mLayoutAdd;
	
	private ContentValues values;
	
	private TextView mRealProfileCard, mPositionCard, mInterviewCard, mNickProfileCard;
	
	// 此聊天框的did
	public static String did;
	private String otherName;
	// 发送的Text消息
	private String textMessage;
	
	// 当前用户的ID
	private long uid;
	
	// minmsgid -- 用于获取更老的信息
	// minmsgid = 0 表示这是最后一页
	private long minmsgid = 0;
	
	// 只有在第一次进入
	private boolean isFirstEnter = true;
	
	// 从数据库中读取的消息数
	private int mTotalMessage;
	
	private MessageChatAdapter adapter;
	
	private boolean isSendMessage = false;
	private boolean isRead = false;
	// 是否正在下拉刷新
	private boolean isPullToRefresh = false;
	
	// sender
	private long receiveId;
	// 0是匿名，1是实名
	private int receiverIsreal = -1;
	// 自己是实名还是匿名
	private int mineIsreal = -1;
	
	private long dialogId;
	
	private ProgressBarCircularIndeterminate mProgressBar;
	
	private PtrClassicFrameLayout mPtrFrame;
	
	// 下拉刷新时，列表中存在的消息数
	private int messageCountBeforeRefresh;
	
	// Handler
	static class WeakHandler extends Handler {
		private WeakReference<ChatPersonActivity> mOuter;
		public WeakHandler(ChatPersonActivity activity) {
			mOuter = new WeakReference<ChatPersonActivity>(activity);
		}
		
		public void handleMessage(Message msg) {
			ChatPersonActivity outer = mOuter.get();
			if (outer != null) {
				// Do something
			}
		}
	}
	
	private WeakHandler mHandler = new WeakHandler(ChatPersonActivity.this) {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case 1:
					mTotalMessage++;
					getSupportLoaderManager().restartLoader(0, null, ChatPersonActivity.this);
					Log.i(TAG, "receive total message : " + mTotalMessage);
					break;
				case 2:
					isRead = true;
					if (StringUtils.isNotEmpty(did)) {
						getSupportLoaderManager().restartLoader(0, null, ChatPersonActivity.this);
					}
					break;
				case 3:
					ImUser user = (ImUser) msg.obj;
					// 获取个人的头像和名字信息
					getPersonAvatarAndName(user.getUid(), user.getDid());
					break;
			}
		}
	};
	
	private void getPersonAvatarAndName(long uid, long did) {
		mGeneralEventLogic.getPersonAvatarAndName(uid, did);
	}
	
	@Override
	public void initUIAndData() {
		setContentView(R.layout.activity_chat_person);
		setHeaderMenuByLeft(this);
		
		// 在这里使用XListView，看看效果
		// mChatList = (XListView) findViewById(R.id.chat_list);
		mChatList = (ListView) findViewById(R.id.chat_list);
		
		mPtrFrame = (PtrClassicFrameLayout) findViewById(R.id.rotate_header_list_view_frame);
		mPtrFrame.setLastUpdateTimeRelateObject(this);
        mPtrFrame.setPtrHandler(new PtrHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                updateData();
            }

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
            }
            
        });
		
		mInputEdit = (EditText) findViewById(R.id.chat_input_edit);
//		mAddBtn = (Button) findViewById(R.id.chat_add_btn);
		mSendBtn = (Button) findViewById(R.id.chat_send_btn);
		
//		if (mInputEdit.getText().length() > 0) {
//			mAddBtn.setVisibility(View.GONE);
			mSendBtn.setVisibility(View.VISIBLE);
//		} else {
//			mAddBtn.setVisibility(View.VISIBLE);
//			mSendBtn.setVisibility(View.GONE);
//		}
//		mLayoutMore = (LinearLayout) findViewById(R.id.layout_more);
//		mLayoutAdd = (LinearLayout) findViewById(R.id.layout_add);
		
		mRealProfileCard = (TextView) findViewById(R.id.tv_real_profile_card);
		mPositionCard = (TextView) findViewById(R.id.tv_position_card);
		mInterviewCard = (TextView) findViewById(R.id.tv_interview_card);
		mNickProfileCard = (TextView) findViewById(R.id.tv_nick_profile_card);
		
		mProgressBar = (ProgressBarCircularIndeterminate) findViewById(R.id.progressBarCircularIndeterminate);
		
		Cursor cookieCursor = this.getContentResolver().query(GeneralContentProvider.COOKIE_CONTENT_URI, null, null, null, null);
		if (cookieCursor != null && cookieCursor.moveToFirst()) {
			uid = (long) cookieCursor.getInt(cookieCursor.getColumnIndex(CookieTable.COLUMN_ID));
			cookieCursor.close();
		}
		
		setUIListeners();
		// 初始化下拉刷新
		// 首先不允许加载更多
		// mChatList.setPullLoadEnable(false);
		// 允许下拉
		// mChatList.setPullRefreshEnable(true);
		// 设置监听器
		// mChatList.setXListViewListener(this);
		// mChatList.pullRefreshing();
		mChatList.setDividerHeight(0);
		
		did = null;
		receiveId = getIntent().getLongExtra("sender", -1);
		// 在这里，得到从上一层过来时带的did
		dialogId = getIntent().getLongExtra("did", -1);
		
		otherName = getIntent().getStringExtra("dname");
		
		receiverIsreal = getIntent().getIntExtra("receiverIsreal", -1);
		mineIsreal = getIntent().getIntExtra("mineIsreal", -1);
		
		if (!StringUtils.isEmpty(otherName)) {
			//设置聊天对象的名字
			setHeaderMenuByCenterTitle(otherName);
		} else {
			setHeaderMenuByCenterTitle("匿名");
		}
		
		if (dialogId == -1) {
			// 此时，根据receive id来获得did
			// 此时，已经开启了聊天对话框，创建或者获取对话id
			mGeneralEventLogic.createOrGetDialogId(mineIsreal, String.valueOf(receiveId), receiverIsreal);
		} else {
			did = String.valueOf(dialogId);
			
			NotificationManager manger = (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);
			manger.cancel(Integer.valueOf(did));
			
			// 此时，获得New Message
			Log.i(TAG, "first get new message from server");
			getNewMessageFromServer(did);
		}
		
		
		findViewById(R.id.menu_bar_right).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				long isReal = 0;
				Cursor cursorUser = getContentResolver().query(GeneralContentProvider.CONTACT_USER_CONTENT_URI, null, 
						ContactUserTable.COLUMN_UID + " = ?" + " AND " + ContactUserTable.COLUMN_DID + " = ?", 
						new String[] { String.valueOf(receiveId), 
						String.valueOf(did) }, null);
				if (cursorUser != null && cursorUser.moveToFirst()) {
					isReal = cursorUser.getLong(cursorUser.getColumnIndex(ContactUserTable.COLUMN_ISREAL));
					cursorUser.close();
				}
				Intent intent = new Intent(v.getContext(), isReal == 1 ? PersonRealInfoActivity.class : PersonAnoInfoActivity.class);
				intent.putExtra(GlobalConstants.UID, receiveId);
				intent.putExtra(GlobalConstants.DID, did);
				startActivity(intent);
				
			}
		});
	}
	
	private void getNewMessageFromServer(String did) {
		Cursor cursor = this.getContentResolver().query(GeneralContentProvider.MESSAGE_CONTENT_URI, null, 
				MessageTable.COLUMN_MESSAGE_DIALOG_ID + " = ?", 
				new String[] {did}, MessageTable.COLUMN_MESSAGE_ID + " asc");
		
		if (cursor != null && cursor.moveToFirst()) {
			// 此时数据渲染完毕, 根据当前的Cursor找到底部的指针，此时开始网络请求
			
			Log.i(TAG, "message count : " + cursor.getCount());
			
			if (cursor.moveToLast()) {
				// 此时，表示Cursor移动到最后一个元素
				// 接着判断这条消息是否是当前用户发的，如果是当前用户发的，则判断这条消息是否被已读
				long senderId = cursor.getInt(cursor .getColumnIndexOrThrow(MessageTable.COLUMN_MESSAGE_FROM_USER_ID));
				long readStatus = cursor.getInt(cursor .getColumnIndexOrThrow(MessageTable.COLUMN_MESSAGE_READ_STATUS));
				long mid = cursor.getInt(cursor .getColumnIndexOrThrow(MessageTable.COLUMN_MESSAGE_ID));
				
				if (uid == senderId) {
					// 说明，这条消息是当前用户发的, 并且这条消息未读
					if (readStatus == 0) {
						Log.i(TAG, "uid is mine and readstatus = 0");
						mGeneralEventLogic.getReadstatus(String.valueOf(did), String.valueOf(mid));
					} else {
						Log.i(TAG, "uid is other and readstatus = 1");
						mGeneralEventLogic.getNewChatMessages(String.valueOf(did), String.valueOf(mid));
						mProgressBar.setVisibility(View.VISIBLE);
					}
				} else {
					// 此时，拿聊天框最新消息的请求
					mGeneralEventLogic.getNewChatMessages(String.valueOf(did), String.valueOf(mid));
					mProgressBar.setVisibility(View.VISIBLE);
				}
				
			} else {
				// 此时，还没有聊天记录
				if (cursor.getCount() == 0) {
					Log.i(TAG, "get new chat message");
					mGeneralEventLogic.getNewChatMessages(String.valueOf(did), String.valueOf(-1));
					mProgressBar.setVisibility(View.VISIBLE);
				}
			}
			
			cursor.close();
		} else {
			Log.i(TAG, "cursor is null  and get new messgae");
			mGeneralEventLogic.getNewChatMessages(String.valueOf(did), String.valueOf(-1));
			mProgressBar.setVisibility(View.VISIBLE);
		}
	}
	
	public synchronized void initTotalMessage() {
		// 缺省是每次加30个
		mTotalMessage = 0;
		
		if (!StringUtils.isEmpty(did)) {
			
			int totalMessageInDatabase = 0;
			Cursor cursorTotal = this.getContentResolver().query(GeneralContentProvider.MESSAGE_CONTENT_URI, null, 
					MessageTable.COLUMN_MESSAGE_DIALOG_ID + " = ?", 
					new String[] {did}, MessageTable.COLUMN_MESSAGE_ID + " asc");
			
			if (cursorTotal != null && cursorTotal.moveToFirst()) {
				
				totalMessageInDatabase = cursorTotal.getCount();
				
				if (totalMessageInDatabase > MAX_PULL_DATABASE_NUM) {
					mTotalMessage +=  MAX_PULL_DATABASE_NUM;
				} else {
					mTotalMessage = totalMessageInDatabase;
				}
				
				cursorTotal.close();
			}
		}
		
		Log.i(TAG, "initTotalMessage : mTotalMessage = " + mTotalMessage);
	}
	
	
	@Override
	public void onResume() {
	    super.onResume();
	}

	@Override
	public void onEventMainThread(Message msg) {
		
		if (msg.obj instanceof InfoResult) {
			
			InfoResult infoResult = (InfoResult) msg.obj;
			switch (msg.what) {
				case R.id.social_get_dialog_id:
					JSONObject jsonObject = JSONObject.parseObject(infoResult.getResult());
					did = jsonObject.getString(GlobalConstants.JSON_DID);
					
					ImDbManager.getInstance().cleanMessageListOtherCount(Long.valueOf(did));
					// 此时，获得New Message
					getNewMessageFromServer(did);
					
					break;
				case R.id.social_send_text_message:
					if (infoResult.getMessage().getStatus() == 0) {
						// 成功--将此message更新到数据库中
						Log.i(TAG, infoResult.getResult());
						// insertIntoMessageTable(infoResult.getResult());
						updateSendMessageTable(infoResult.getResult());
					} else {
						updateSendMessageFailedTable();
					}
//					VToast.show(this,infoResult.getMessage().getMsg());
					break;
				case R.id.social_send_real_profile_card:
					if (infoResult.getMessage().getStatus() == 0) {
						// 3.将此message添加到数据库中
						insertIntoMessageTable(infoResult.getResult());
					}
					VToast.show(this,infoResult.getMessage().getMsg());
					break;
				case R.id.social_send_nick_profile_card:
					if (infoResult.getMessage().getStatus() == 0) {
						insertIntoMessageTable(infoResult.getResult());
					} else {
						// 失败
					}
					VToast.show(this,infoResult.getMessage().getMsg());
					break;
				case R.id.social_send_position_invitation:
					// 职位邀请
					if (infoResult.getMessage().getStatus() == 0) {
						insertIntoMessageTable(infoResult.getResult());
					}
					
					VToast.show(this, infoResult.getMessage().getMsg());
					break;
				case R.id.social_interest_job_invitation:
					// 对职位邀请感兴趣
					if (infoResult.getMessage().getStatus() == 0) {
						insertIntoMessageTable(infoResult.getResult());
					}
					VToast.show(this, infoResult.getMessage().getMsg());
					break;
				case R.id.social_uninterest_job_invitation:
					mProgressBar.setVisibility(View.GONE);
					// 对职位邀请不感兴趣
					if (infoResult.getMessage().getStatus() == 0) {
						insertIntoMessageTable(infoResult.getResult());
					}
					VToast.show(this, infoResult.getMessage().getMsg());
					break;
				case R.id.social_accept_interview_invitation:
					mProgressBar.setVisibility(View.GONE);
					// 接受面试邀请
					if (infoResult.getMessage().getStatus() == 0) {
						insertIntoMessageTable(infoResult.getResult());
					}
					VToast.show(this, infoResult.getMessage().getMsg());
					break;
				case R.id.social_ignore_interview_invitation:
					mProgressBar.setVisibility(View.GONE);
					// 忽略面试邀请
					if (infoResult.getMessage().getStatus() == 0) {
						insertIntoMessageTable(infoResult.getResult());
					}
					VToast.show(this, infoResult.getMessage().getMsg());
					break;
				case R.id.social_get_read_status:
					// 此时肯定会有返回数据
					JSONObject readStatusObject = JSONObject.parseObject(infoResult.getResult());
					
					Log.i(TAG, infoResult.getResult());
					
					if (readStatusObject.getInteger(GlobalConstants.JSON_READSTATUS) != null) {
						
						if (readStatusObject.getInteger(GlobalConstants.JSON_READSTATUS) == 1) {
							// 此时，表示这条消息被对方已读，客户端应该把这条消息以及聊天框中早于这条消息并且没有设置为已读状态的消息，都设置成已读，以及已读时间
							updateMessageReadstatus(readStatusObject.getString(GlobalConstants.JSON_DID), 
									readStatusObject.getString(GlobalConstants.JSON_MID), readStatusObject.getInteger(GlobalConstants.JSON_READSTATUS), 
									readStatusObject.getLong(GlobalConstants.JSON_READTIME), readStatusObject.getString(GlobalConstants.JSON_CMID));
							// 此时，请求最新消息的请求
							mGeneralEventLogic.getNewChatMessages(readStatusObject.getString(GlobalConstants.JSON_DID), 
									readStatusObject.getString(GlobalConstants.JSON_MID));
						} else {
							// 此时，请求最新消息的请求
							mGeneralEventLogic.getNewChatMessages(readStatusObject.getString(GlobalConstants.JSON_DID), 
									readStatusObject.getString(GlobalConstants.JSON_MID));
						}
					} else {
						// 此时，请求最新消息的请求
						mGeneralEventLogic.getNewChatMessages(readStatusObject.getString(GlobalConstants.JSON_DID), 
								readStatusObject.getString(GlobalConstants.JSON_MID));
					}
					break;
				case R.id.social_get_new_messages:
					// 同步方法
					initTotalMessage();
					
					// 此时，获取聊天框的最新消息
					JSONObject newMessageObject = JSONObject.parseObject(infoResult.getResult());
					Log.i(TAG, infoResult.getResult());
					if (newMessageObject.getJSONArray(GlobalConstants.JSON_DATA) != null) {
						// 此时有数据
						insertIntoMessageTable(newMessageObject.getJSONArray(GlobalConstants.JSON_DATA));
					}
					
					if (newMessageObject.getString(GlobalConstants.JSON_MINMSGID) != null) {
						minmsgid = Long.valueOf(newMessageObject.getString(GlobalConstants.JSON_MINMSGID));
					} else {
						minmsgid = 0;
					}
					
					// 首先拉取对方的头像信息
					mGeneralEventLogic.getPersonAvatarAndName(receiveId, dialogId);
					// 拉取自己的头像信息
					mGeneralEventLogic.getPersonAvatarAndName(uid, dialogId);
					
					// Prepare the loader.  Either re-connect with an existing one, or start a new one.
					if (!StringUtils.isEmpty(did)) {
						getSupportLoaderManager().initLoader(0, null, this);
					}
					
					mProgressBar.setVisibility(View.GONE);
					break;
					
				case R.id.social_get_old_messages:
					JSONObject oldMessageObject = JSONObject.parseObject(infoResult.getResult());
					Log.i(TAG, infoResult.getResult());
					if (oldMessageObject.getJSONArray(GlobalConstants.JSON_DATA) != null) {
						// 此时有数据
						insertIntoMessageTable(oldMessageObject.getJSONArray(GlobalConstants.JSON_DATA));
						isPullToRefresh = true;
					}
					
					if (oldMessageObject.getString(GlobalConstants.JSON_MINMSGID) != null) {
						minmsgid = Long.valueOf(oldMessageObject.getString(GlobalConstants.JSON_MINMSGID));
					} else {
						minmsgid = 0;
					}
					break;
					
				case R.id.social_get_person_avatar:
					// 将获得的头像和名字信息，插入到ContactUserTable表中，同时，重新加载CursorLoader
					Log.i(TAG, infoResult.getResult());
					ImDbManager.getInstance().insertPersonInfoIntoContactUserTable(infoResult.getResult());
					// 更新
					adapter.notifyDataSetChanged();
					break;
			}
		} else {
			// Http请求都没发出，所以很有可能是断线
			updateSendMessageFailedTable();
			
			Log.i(TAG, "Http failed");
			mProgressBar.setVisibility(View.GONE);
			
			switch (msg.what) {
				case R.id.social_get_new_messages:
					// 同步方法
					initTotalMessage();
					//ImDbManager.getInstance().setHandler(mHandler);
					
					// 首先拉取对方的头像信息
					mGeneralEventLogic.getPersonAvatarAndName(receiveId, dialogId);
					// 拉取自己的头像信息
					mGeneralEventLogic.getPersonAvatarAndName(uid, dialogId);
					
					// Prepare the loader.  Either re-connect with an existing one, or start a new one.
					if (!StringUtils.isEmpty(did)) {
						getSupportLoaderManager().initLoader(0, null, this);
					}
					break;
			}
		}
	}
	
	// 更新数据库中的已读状态
	private void updateMessageReadstatus(String did, String mid, int readstatus, long createtime, String cmid) {
		// 先进行查询
		// String.valueOf(0) -- 0代表未读
		Cursor cursor = this.getContentResolver().query(GeneralContentProvider.MESSAGE_CONTENT_URI, null, 
				MessageTable.COLUMN_MESSAGE_DIALOG_ID + " = ?" + " AND " + MessageTable.COLUMN_MESSAGE_READ_STATUS + " = ?" + " AND " + MessageTable.COLUMN_MESSAGE_FROM_USER_ID + " = ?", 
				new String[] {did, String.valueOf(0), String.valueOf(uid)}, MessageTable.COLUMN_MESSAGE_ID);
		
		Log.i(TAG, "Cursor Count : " + cursor.getCount());
		
		ContentValues values = new ContentValues();
		long updateMid = 0;
		long updateDid = 0;
		// 遍历
		if (cursor != null && cursor.moveToFirst()) {
			do {
				updateMid = cursor.getInt(cursor .getColumnIndexOrThrow(MessageTable.COLUMN_MESSAGE_ID));
				updateDid = cursor.getInt(cursor .getColumnIndexOrThrow(MessageTable.COLUMN_MESSAGE_DIALOG_ID));
				// readstatus == 1
				values.put(MessageTable.COLUMN_MESSAGE_READ_STATUS, 1);
				values.put(MessageTable.COLUMN_MESSAGE_STATUS, 1);
				
				if (StringUtils.isEmpty(cmid)) {
					Log.i(TAG, "cmid is empty");
					this.getContentResolver().update(GeneralContentProvider.MESSAGE_CONTENT_URI, values, 
							MessageTable.COLUMN_MESSAGE_ID + " = ?" + " AND " + MessageTable.COLUMN_MESSAGE_DIALOG_ID + " = ?" + " AND " + MessageTable.COLUMN_ID + " = ?", 
							new String[] {String.valueOf(updateMid), String.valueOf(updateDid), cmid});
				} else {
					Log.i(TAG, "update read status");
					this.getContentResolver().update(GeneralContentProvider.MESSAGE_CONTENT_URI, values, 
							MessageTable.COLUMN_MESSAGE_ID + " = ?" + " AND " + MessageTable.COLUMN_MESSAGE_DIALOG_ID + " = ?", 
							new String[] {String.valueOf(updateMid), String.valueOf(updateDid)});
				}
				values.clear();
				
			} while (cursor.moveToNext());
			
			cursor.close();
		}
		
	}
	
	@SuppressLint("ClickableViewAccessibility")
	private void setUIListeners() {
		mInputEdit.addTextChangedListener(mTextWatcher);
		
		/*mAddBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if (mLayoutMore.getVisibility() == View.VISIBLE) {
					mLayoutMore.setVisibility(View.GONE);
					mLayoutAdd.setVisibility(View.GONE);
				} else if (mLayoutMore.getVisibility() == View.GONE) {
					mLayoutMore.setVisibility(View.VISIBLE);
					mLayoutAdd.setVisibility(View.VISIBLE);
					Animation animation = AnimationUtils.loadAnimation(ChatPersonActivity.this, R.anim.anim_right_in);
					mLayoutMore.startAnimation(animation);
				}
			}
		});*/
		
		mSendBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 发送信息
				sendChatMessage();
			}
		});
		
		mRealProfileCard.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				// 发送个人名片
				sendRealProfileCard();
			}
		});
		
		mPositionCard.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 发送职位邀请
				sendPositionInvitation();
			}
		});
		
		mInterviewCard.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 发送面试邀请
				
			}
		});
		
		mNickProfileCard.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				// 发送匿名名片
				sendNickProfileCard();
			}
		});
		mChatList.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				if (imm != null && imm.isActive()) {
					imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
				}
				return false;
			}
		});
	}
	
	/**
	 * 发送普通的Text聊天信息
	 */
	private void sendChatMessage() {
		// 1.首先从EditText中得到text
		textMessage = mInputEdit.getText().toString();
		
		if (StringUtils.isNotEmpty(textMessage)) {
			// 同时将EditText清空
			mInputEdit.setText("");
			// 此时，先将数据保存到数据库中values = new ContentValues();
			
			long mid = 0;
			// 先查找数据中，找到最后一条数据的_id和mid
			Cursor cursor = this.getContentResolver().query(GeneralContentProvider.MESSAGE_CONTENT_URI, 
					null, MessageTable.COLUMN_MESSAGE_DIALOG_ID + " = ?", 
					new String[] { did }, MessageTable.COLUMN_ID + " asc");
			
			if (cursor != null && cursor.moveToFirst()) {
				cursor.moveToLast();
				mid = cursor.getInt(cursor.getColumnIndex(MessageTable.COLUMN_MESSAGE_ID));
				cursor.close();
			}		
			
			ContentValues values = new ContentValues();
			// Mid递增顺序--可以作为排序字段，且永远不为null
			values.put(MessageTable.COLUMN_MESSAGE_ID, mid + 1);
			values.put(MessageTable.COLUMN_MESSAGE_DIALOG_ID, Long.valueOf(did));
			
			values.put(MessageTable.COLUMN_MESSAGE_FROM_USER_ID, uid);
			
			values.put(MessageTable.COLUMN_MESSAGE_TYPE, IMConstants.TYPE_P2P_TEXT);
			values.put(MessageTable.COLUMN_MESSAGE_DISPLAY_TYPE, ImUtils.matchDisplayType(IMConstants.TYPE_P2P_TEXT));
			// 代表发送text--返回的Json中不含body
			values.put(MessageTable.COLUMN_MESSAGE_OVERVIEW, textMessage);
			isSendMessage = true;
			
			// 此时，正在加载
			values.put(MessageTable.COLUMN_MESSAGE_STATUS, 0);
			
			// 保存当前消息的插入时间--可能以后需要根据时间来排序聊天信息
			values.put(MessageTable.COLUMN_CREATED, System.currentTimeMillis());
			
			// 插入数据库
			getContentResolver().insert(GeneralContentProvider.MESSAGE_CONTENT_URI, values);
			values.clear();
			
			mTotalMessage++;
			
			Cursor cursorInsert = this.getContentResolver().query(GeneralContentProvider.MESSAGE_CONTENT_URI, 
					null, MessageTable.COLUMN_MESSAGE_DIALOG_ID + " = ?", 
					new String[] { did }, MessageTable.COLUMN_ID + " asc");
			
			if (cursorInsert != null && cursorInsert.moveToFirst()) {
				cursorInsert.moveToLast();
				// 第三个属性为receiver id
				// isReal
				mGeneralEventLogic.sendTextMessage(mineIsreal, did, "", receiverIsreal, "123", 
						String.valueOf(cursorInsert.getInt(cursor.getColumnIndex(MessageTable.COLUMN_ID))), textMessage);
				cursor.close();
				
				// 更新页面
				getSupportLoaderManager().restartLoader(0, null, ChatPersonActivity.this);
			}
			
		} else {
			Toast.makeText(this, "您还没有填写消息内容", Toast.LENGTH_SHORT).show();
		}

		// 4.当数据添加到数据库后，ListView列表会刷新，之后填充数据，然后显示ProgressDialog表示消息是否发送成功
		
		// 5.当消息发送成功后, ProgressDialog消失，否则变成警告Dialog, 提示用户消息未发送成功，用户可以选择重发或者删除
	}
	
	private void updateSendMessageTable (String jsonMessage) {
		if (jsonMessage != null && jsonMessage.length() > 0) {
			
			ContentValues values = new ContentValues();
			
			JSONObject jsonObject = JSONObject.parseObject(jsonMessage);
			// Mid递增顺序--可以作为排序字段，且永远不为null
			values.put(MessageTable.COLUMN_MESSAGE_ID, jsonObject.getLong(GlobalConstants.JSON_MID));
			
			values.put(MessageTable.COLUMN_CREATED, jsonObject.getLong(GlobalConstants.JSON_CREATETIME));
			// 1代表发送成功
			values.put(MessageTable.COLUMN_MESSAGE_STATUS, 1);
			
			// 插入数据库
			getContentResolver().update(GeneralContentProvider.MESSAGE_CONTENT_URI, values, 
					MessageTable.COLUMN_ID + " = ?" + " AND " + MessageTable.COLUMN_MESSAGE_DIALOG_ID + " = ?", 
					new String[] { jsonObject.getString(GlobalConstants.JSON_CMID), jsonObject.getString(GlobalConstants.JSON_DID) });
			
			values.clear();
			
			values.put(MessageSummaryTable.COLUMN_MESSAGE_TYPE, jsonObject.getLong(GlobalConstants.JSON_TYPE));
			
			values.put(MessageSummaryTable.COLUMN_MESSAGE_OVERVIEW, textMessage);
			
			values.put(MessageSummaryTable.COLUMN_UPDATED, jsonObject.getLong(GlobalConstants.JSON_CREATETIME));
			
			if (jsonObject.getLong(GlobalConstants.JSON_RECEIVER) != null) {
				values.put(MessageSummaryTable.COLUMN_MESSAGE_TO_USER_ID, jsonObject.getLong(GlobalConstants.JSON_RECEIVER));
			} else {
				values.put(MessageSummaryTable.COLUMN_MESSAGE_TO_USER_ID, uid);
			}
			
			// 也就是说，每次进入聊天框时，一定要将对方的id一起传过来
			if (receiveId > 0) {
				values.put(MessageSummaryTable.COLUMN_MESSAGE_FROM_USER_ID, receiveId);
			}
			
			// display type
			values.put(MessageSummaryTable.COLUMN_MESSAGE_DISPLAY_TYPE, IMConstants.DISPLAY_LIST_TYPE_DIALOG_PERSON);
			
			// 首先查表判断该消息是否存在
			Cursor cursorSummary = this.getContentResolver().query(GeneralContentProvider.MESSAGE_SUMMARY_CONTENT_URI, null, 
					MessageSummaryTable.COLUMN_MESSAGE_DISPLAY_TYPE + " = ?" + " AND " + MessageSummaryTable.COLUMN_MESSAGE_DIALOG_ID + " = ?", 
					new String[] { String.valueOf(IMConstants.DISPLAY_LIST_TYPE_DIALOG_PERSON), jsonObject.getString(GlobalConstants.JSON_DID)}, null);
			
			if (cursorSummary != null && cursorSummary.moveToFirst()) {
				// 此时，消息已经存在--执行更新操作
				values.put(MessageSummaryTable.COLUMN_UNREADCOUNT, 0);
				this.getContentResolver().update(GeneralContentProvider.MESSAGE_SUMMARY_CONTENT_URI, values, 
						MessageSummaryTable.COLUMN_MESSAGE_DISPLAY_TYPE + " = ?" + " AND " + MessageSummaryTable.COLUMN_MESSAGE_DIALOG_ID + " = ?", 
						new String[] { String.valueOf(IMConstants.DISPLAY_LIST_TYPE_DIALOG_PERSON), jsonObject.getString(GlobalConstants.JSON_DID)});
			
				isSendMessage = true;
				
				cursorSummary.close();
			} else {
				values.put(MessageSummaryTable.COLUMN_UNREADCOUNT, 0);
				values.put(MessageSummaryTable.COLUMN_MESSAGE_DIALOG_ID, jsonObject.getString(GlobalConstants.JSON_DID));
				this.getContentResolver().insert(GeneralContentProvider.MESSAGE_SUMMARY_CONTENT_URI, values);
			}

			values.clear();
			
		}
	}
	
	private void updateSendMessageFailedTable() {
		
		ContentValues values = new ContentValues();
		// 2代表发送失败
		values.put(MessageTable.COLUMN_MESSAGE_STATUS, 2);
		
		// 检查所有的状态为ProgressBar的消息，将他们都置为2
		// 0 表示 progressbar状态
		Cursor cursor = getContentResolver().query(GeneralContentProvider.MESSAGE_CONTENT_URI, null, 
				MessageTable.COLUMN_MESSAGE_DIALOG_ID + " = ?" + " AND " + MessageTable.COLUMN_MESSAGE_STATUS + " = ?"
				+ " AND " + MessageTable.COLUMN_MESSAGE_FROM_USER_ID + " = ?", 
				new String[] { did,  String.valueOf(0), String.valueOf(uid)}, null);
		
		if (cursor != null && cursor.moveToFirst()) {
			
			do {
				long mid = cursor.getInt(cursor.getColumnIndex(MessageTable.COLUMN_MESSAGE_ID));
				// 更新数据库
				getContentResolver().update(GeneralContentProvider.MESSAGE_CONTENT_URI, values, 
						MessageTable.COLUMN_MESSAGE_ID + " = ?" + " AND " + MessageTable.COLUMN_MESSAGE_DIALOG_ID + " = ?", 
						new String[] { String.valueOf(mid), did});
			} while (cursor.moveToNext());
			
			cursor.close();
			
			getSupportLoaderManager().restartLoader(0, null, ChatPersonActivity.this);
		}
		
		values.clear();
	}
	
	/**
	 * 发送实名卡片
	 */
	private void sendRealProfileCard() {
		/*long sendId = 0;
		Cursor cursor = getContentResolver().query(GeneralContentProvider.COOKIE_CONTENT_URI, null, null, null,
				null);
		if (cursor != null && cursor.moveToFirst()) {
			sendId = (long) cursor.getInt(cursor.getColumnIndex(CookieTable.COLUMN_ID));
			cursor.close();
		}*/
		
	}
	
	/**
	 * 发送匿名卡片
	 */
	private void sendNickProfileCard() {
		/*long sendId = 0;
		Cursor cursor = getContentResolver().query(GeneralContentProvider.COOKIE_CONTENT_URI, null, null, null,
				null);
		if (cursor != null && cursor.moveToFirst()) {
			sendId = (long) cursor.getInt(cursor.getColumnIndex(CookieTable.COLUMN_ID));
			cursor.close();
		}*/
	}
	
	/**
	 * 发送职位邀请
	 */
	private void sendPositionInvitation() {
		// receiverid=27&receiverisreal=1&jid=84
		// mSocialEventLogic.sendPositionInvitation("9385354", 1, "67");
	}
	
	/**
	 * 将Message保存到数据库中
	 * @param jsonMessage
	 */
	private synchronized void insertIntoMessageTable(String jsonMessage) {
		if (jsonMessage != null && jsonMessage.length() > 0) {
			long sendId = 0;
			Cursor cursor = getContentResolver().query(GeneralContentProvider.COOKIE_CONTENT_URI, null, null, null,
					null);
			if (cursor != null && cursor.moveToNext()) {
				sendId = (long) cursor.getInt(cursor.getColumnIndex(CookieTable.COLUMN_ID));
				cursor.close();
			}
			
			values = new ContentValues();
			
			JSONObject jsonObject = JSONObject.parseObject(jsonMessage);
			// Mid递增顺序--可以作为排序字段，且永远不为null
			values.put(MessageTable.COLUMN_MESSAGE_ID, jsonObject.getLong(GlobalConstants.JSON_MID));
			values.put(MessageTable.COLUMN_MESSAGE_DIALOG_ID, jsonObject.getLong(GlobalConstants.JSON_DID));
			
			if (jsonObject.getString(GlobalConstants.JSON_SENDER) != null) {
				values.put(MessageTable.COLUMN_MESSAGE_FROM_USER_ID, jsonObject.getString(GlobalConstants.JSON_SENDER));
			} else {
				values.put(MessageTable.COLUMN_MESSAGE_FROM_USER_ID, sendId);
			}
			
			if (jsonObject.getString(GlobalConstants.JSON_RECEIVER) != null) {
				values.put(MessageTable.COLUMN_MESSAGE_TO_USER_ID, jsonObject.getString(GlobalConstants.JSON_RECEIVER));
			}
			
			values.put(MessageTable.COLUMN_MESSAGE_TYPE, jsonObject.getInteger(GlobalConstants.JSON_TYPE));
			values.put(MessageTable.COLUMN_MESSAGE_DISPLAY_TYPE, ImUtils.matchDisplayType(jsonObject.getInteger(GlobalConstants.JSON_TYPE)));
			if (jsonObject.getString(GlobalConstants.JSON_BODY) != null) {
				// 此时返回的Json中含有body
				values.put(MessageTable.COLUMN_MESSAGE_OVERVIEW, jsonObject.getString(GlobalConstants.JSON_BODY));
			} else {
				// 代表发送text--返回的Json中不含body
				values.put(MessageTable.COLUMN_MESSAGE_OVERVIEW, textMessage);
				isSendMessage = true;
			}
			// 保存当前消息的插入时间--可能以后需要根据时间来排序聊天信息
			values.put(MessageTable.COLUMN_CREATED, System.currentTimeMillis());
			
			if (jsonObject.getInteger(GlobalConstants.JSON_READSTATUS) != null) {
				values.put(MessageTable.COLUMN_MESSAGE_READ_STATUS, jsonObject.getInteger(GlobalConstants.JSON_READSTATUS));
			}
			
			if (jsonObject.getLong(GlobalConstants.JSON_READTIME) != null) {
				values.put(MessageTable.COLUMN_READTIME, jsonObject.getLong(GlobalConstants.JSON_READTIME));
			}
			// 插入数据库
			getContentResolver().insert(GeneralContentProvider.MESSAGE_CONTENT_URI, values);
			
			// 如果是正在发送的消息，更新MessageSummaryTable表
			if (isSendMessage) {
				ContentValues values = new ContentValues();
				
				values.put(MessageSummaryTable.COLUMN_MESSAGE_TYPE, jsonObject.getLong(GlobalConstants.JSON_TYPE));
				
				values.put(MessageSummaryTable.COLUMN_MESSAGE_OVERVIEW, textMessage);
				
				values.put(MessageSummaryTable.COLUMN_UPDATED, System.currentTimeMillis());
				
				if (jsonObject.getLong(GlobalConstants.JSON_RECEIVER) != null) {
					values.put(MessageSummaryTable.COLUMN_MESSAGE_TO_USER_ID, jsonObject.getLong(GlobalConstants.JSON_RECEIVER));
				} else {
					values.put(MessageSummaryTable.COLUMN_MESSAGE_TO_USER_ID, sendId);
				}
				
				values.put(MessageSummaryTable.COLUMN_MESSAGE_FROM_USER_ID, receiveId);
				
				// display type
				values.put(MessageSummaryTable.COLUMN_MESSAGE_DISPLAY_TYPE, IMConstants.DISPLAY_LIST_TYPE_DIALOG_PERSON);
				
				// 首先查表判断该消息是否存在
				Cursor cursorSummary = this.getContentResolver().query(GeneralContentProvider.MESSAGE_SUMMARY_CONTENT_URI, null, 
						MessageSummaryTable.COLUMN_MESSAGE_DISPLAY_TYPE + " = ?" + " AND " + MessageSummaryTable.COLUMN_MESSAGE_DIALOG_ID + " = ?", 
						new String[] { String.valueOf(IMConstants.DISPLAY_LIST_TYPE_DIALOG_PERSON), jsonObject.getString(GlobalConstants.JSON_DID)}, null);
				
				if (cursorSummary != null && cursorSummary.moveToFirst()) {
					// 此时，消息已经存在--执行更新操作
					values.put(MessageSummaryTable.COLUMN_UNREADCOUNT, 0);
					this.getContentResolver().update(GeneralContentProvider.MESSAGE_SUMMARY_CONTENT_URI, values, 
							MessageSummaryTable.COLUMN_MESSAGE_DISPLAY_TYPE + " = ?" + " AND " + MessageSummaryTable.COLUMN_MESSAGE_DIALOG_ID + " = ?", 
							new String[] { String.valueOf(IMConstants.DISPLAY_LIST_TYPE_DIALOG_PERSON), jsonObject.getString(GlobalConstants.JSON_DID)});
					cursorSummary.close();
				}
				
				values.clear();
			}
			
			mTotalMessage++;
			// 此时已经的得到用户的uid和mobile信息，可以将用户的数据存入用户的UserInfo表中
			values.clear();
		}
		
	}

	
	// 保存JSONArray
	private synchronized void insertIntoMessageTable(JSONArray mMessageArray) {
		if (mMessageArray != null && mMessageArray.size() > 0) {
			
			boolean interrupt = false;
			// 此时，插入一条断点, 此时说明从服务器端拉了最多的30条数据，说明服务器端可能存在更多的消息数量，所以需要插入一个断点
			if (mMessageArray.size() == MAX_PULL_NUM) {
				interrupt = true;
			}
			
			long sendId = 0;
			Cursor cursor = getContentResolver().query(GeneralContentProvider.COOKIE_CONTENT_URI, null, null, null, null);
			if (cursor != null && cursor.moveToFirst()) {
				sendId = (long) cursor.getInt(cursor.getColumnIndex(CookieTable.COLUMN_ID));
				cursor.close();
			}
			
			values = new ContentValues();
			
			// 此时可以将它以上的interrupt置为0
			JSONObject jsonObjectTop = mMessageArray.getJSONObject(0);
			long midTop = jsonObjectTop.getLongValue(GlobalConstants.JSON_MID);
			
			// 所以，在数据库中，此断层之间的最大区域的最小mid为midTop + 1
			long midStart = midTop + 1;
			
			Cursor cursorTotal = this.getContentResolver().query(GeneralContentProvider.MESSAGE_CONTENT_URI, null, 
					MessageTable.COLUMN_MESSAGE_DIALOG_ID + " = ?", 
					new String[] {did}, MessageTable.COLUMN_MESSAGE_ID + " asc");
			
			ContentValues cValues = new ContentValues();
			
			if (cursorTotal != null && cursorTotal.moveToLast()) {
				do {
					long mid = cursorTotal.getLong(cursorTotal.getColumnIndex(MessageTable.COLUMN_MESSAGE_ID));
					
					cValues.put(MessageTable.COLUMN_INTERRUPT, 0);
					
					this.getContentResolver().update(GeneralContentProvider.MESSAGE_CONTENT_URI, cValues, 
							MessageTable.COLUMN_MESSAGE_ID + " = ?", new String[] {String.valueOf(mid)});
					
				} while (cursorTotal.moveToPrevious() && 
						(cursorTotal.getLong(cursorTotal.getColumnIndex(MessageTable.COLUMN_MESSAGE_ID)) >= midStart));
				cursorTotal.close();
				cValues.clear();
			}
			
			for (int i = mMessageArray.size() - 1; i >= 0; i--) {
				
				JSONObject jsonObject = mMessageArray.getJSONObject(i);
				// Mid递增顺序--可以作为排序字段，且永远不为null
				values.put(MessageTable.COLUMN_MESSAGE_ID, jsonObject.getLong(GlobalConstants.JSON_MID));
				values.put(MessageTable.COLUMN_MESSAGE_DIALOG_ID, jsonObject.getLong(GlobalConstants.JSON_DID));
				
				if (jsonObject.getString(GlobalConstants.JSON_SENDER) != null) {
					values.put(MessageTable.COLUMN_MESSAGE_FROM_USER_ID, jsonObject.getString(GlobalConstants.JSON_SENDER));
				} else {
					values.put(MessageTable.COLUMN_MESSAGE_FROM_USER_ID, sendId);
				}
				
				if (jsonObject.getString(GlobalConstants.JSON_RECEIVER) != null) {
					values.put(MessageTable.COLUMN_MESSAGE_TO_USER_ID, jsonObject.getString(GlobalConstants.JSON_RECEIVER));
				}
				
				values.put(MessageTable.COLUMN_MESSAGE_TYPE, jsonObject.getInteger(GlobalConstants.JSON_TYPE));
				values.put(MessageTable.COLUMN_MESSAGE_DISPLAY_TYPE, ImUtils.matchDisplayType(jsonObject.getInteger(GlobalConstants.JSON_TYPE)));
				if (jsonObject.getString(GlobalConstants.JSON_BODY) != null) {
					// 此时返回的Json中含有body
					values.put(MessageTable.COLUMN_MESSAGE_OVERVIEW, jsonObject.getString(GlobalConstants.JSON_BODY));
				} else {
					// 代表发送text--返回的Json中不含body
					values.put(MessageTable.COLUMN_MESSAGE_OVERVIEW, textMessage);
				}
				// 保存当前消息的插入时间--可能以后需要根据时间来排序聊天信息
				// values.put(MessageTable.COLUMN_CREATED, System.currentTimeMillis());
				values.put(MessageTable.COLUMN_CREATED, jsonObject.getLong(GlobalConstants.JSON_CREATETIME));
				
				if (jsonObject.getInteger(GlobalConstants.JSON_READSTATUS) != null) {
					values.put(MessageTable.COLUMN_MESSAGE_READ_STATUS, jsonObject.getInteger(GlobalConstants.JSON_READSTATUS));
				}
				
				if (jsonObject.getString(GlobalConstants.JSON_SENDER).equals(String.valueOf(uid))) {
					values.put(MessageTable.COLUMN_MESSAGE_STATUS, 1);
				}
				
				if (jsonObject.getLong(GlobalConstants.JSON_READTIME) != null) {
					values.put(MessageTable.COLUMN_READTIME, jsonObject.getLong(GlobalConstants.JSON_READTIME));
				}
				
				
				if (i == mMessageArray.size() - 1 && interrupt) {
					// 此时，插入的是最小的mid消息
					values.put(MessageTable.COLUMN_INTERRUPT, 1);
					// 插入数据库
					getContentResolver().insert(GeneralContentProvider.MESSAGE_CONTENT_URI, values);
					mTotalMessage++;
				} else {
					// 插入数据库
					getContentResolver().insert(GeneralContentProvider.MESSAGE_CONTENT_URI, values);
					Log.i(TAG, "insert into database --- mTotalMessage : " + mTotalMessage);
					mTotalMessage++;
				}
					
				// 此时已经的得到用户的uid和mobile信息，可以将用户的数据存入用户的UserInfo表中
				values.clear();
			}
			
			getSupportLoaderManager().restartLoader(0, null, this);
		}
	}
	
	TextWatcher mTextWatcher = new TextWatcher() {
		
		@Override
		public void afterTextChanged(Editable s) {
			// Text有变化之后，判断是否有输入数据
			if (s.length() > 0) {
//				mAddBtn.setVisibility(View.GONE);
//				mSendBtn.setVisibility(View.VISIBLE);
				mSendBtn.setEnabled(true);
			} else {
//				mAddBtn.setVisibility(View.VISIBLE);
//				mSendBtn.setVisibility(View.GONE);
				mSendBtn.setEnabled(false);
			}
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			
		}
			
	};
	
	//////////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		Cursor cursor = this.getContentResolver().query(GeneralContentProvider.MESSAGE_CONTENT_URI, null, 
				MessageTable.COLUMN_MESSAGE_DIALOG_ID + " = ?", 
				new String[] { did }, null);
		// 消息总数
		// 每次加载30条
		Uri uri = GeneralContentProvider.MESSAGE_CONTENT_URI;
		
		if (cursor.getCount() > MAX_PULL_DATABASE_NUM) {
			cursor.close();
			return new CursorLoader(this, uri, null, MessageTable.COLUMN_MESSAGE_DIALOG_ID + " = ?", 
					new String[] { did }, MessageTable.COLUMN_MESSAGE_ID + " asc limit " + mTotalMessage + " offset " + (cursor.getCount() - mTotalMessage));
		} else {
			cursor.close();
			Log.i(TAG, "onCreateLoader : mTotalMessage = " + mTotalMessage);
			return new CursorLoader(this, uri, null, MessageTable.COLUMN_MESSAGE_DIALOG_ID + " = ?", 
					new String[] { did }, MessageTable.COLUMN_MESSAGE_ID + " asc limit " + mTotalMessage);
		}
		
	}
	
	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		
		if (isFirstEnter) {
			
			adapter = new MessageChatAdapter(this, cursor, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER, mHandler);
			adapter.setOnCardContentClickListener(this);
			adapter.setOnResendClickListener(this);
			mChatList.setAdapter(adapter);
			
			// 用完后，cursor移动到顶部
			cursor.moveToFirst();
			
			mChatList.setSelection(adapter.getCount() - 1);
			isFirstEnter = false;
			
		}
		
		adapter.swapCursor(cursor);
		
		// ListView显示最底部--两种方式
		// mChatList.setSelection(adapter.getCount() - 1);
		if (isSendMessage || isRead) {
			cursor.moveToFirst();
			// 如果是发送消息，则定位到底部
			// mChatList.setSelection(mChatList.getBottom());
			Log.i(TAG, "is send message");
			mChatList.setSelection(adapter.getCount() - 1);
			
			isSendMessage = false;
			isRead = false;
		} else if (isPullToRefresh) {
			mPtrFrame.refreshComplete();
			
			Log.i(TAG, "pull to refresh");
			if (adapter.getCount() > messageCountBeforeRefresh) {
				mChatList.clearFocus();
				mChatList.post(new Runnable() {
				    @Override
				    public void run() {
				    	mChatList.setSelection(mChatList.getCount() - messageCountBeforeRefresh -1);
				    }
				});
			} else {
				mChatList.clearFocus();
				mChatList.post(new Runnable() {
				    @Override
				    public void run() {
				    	mChatList.setSelection(mChatList.getCount() - messageCountBeforeRefresh);
				    }
				});
			}
			
			isPullToRefresh = false;
		} else {
			Log.i(TAG, "selection bottom");
			mChatList.setSelection(adapter.getCount());
		}
		
	}
	
	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		adapter.swapCursor(null);
	}
	//////////////////////////////////////////////////////////////////////////////////////////////////
	
	@Override
	public void onCardContentClick(String did, String mid, int cardType, int actionType) {
		switch (cardType) {
			case MessageChatAdapter.TYPE_CLICK_JOB_INVITATION:
				if (actionType == MessageChatAdapter.TYPE_CLICK_JOB_INVITATION_INTEREST) {
					// 执行感兴趣操作
					mGeneralEventLogic.interestJobInvitation(did, mid);
				} else if (actionType == MessageChatAdapter.TYPE_CLICK_JOB_INVITATION_UNINTEREST) {
					// 执行不感兴趣操作
					mGeneralEventLogic.uninterestJobInvitation(did, mid);
					mProgressBar.setVisibility(View.VISIBLE);
				}
				break;
			case MessageChatAdapter.TYPE_CLICK_INTERVIEW_INVITATION:
				if (actionType == MessageChatAdapter.TYPE_CLICK_INTERVIEW_INVITATION_ACCEPT) {
					// 执行接受邀请操作
					mGeneralEventLogic.acceptInterviewInvitation(did, mid);
					mProgressBar.setVisibility(View.VISIBLE);
				} else if (actionType == MessageChatAdapter.TYPE_CLICK_INTERVIEW_INVITATION_IGNORE) {
					// 忽略邀请操作
					mGeneralEventLogic.ignoreInterviewInvitation(did, mid);
					mProgressBar.setVisibility(View.VISIBLE);
				}
				break;
		}
	}

	
	/////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * 下拉刷新更新数据
	 */
	private void updateData() {
		
		isPullToRefresh = true;
		// 此时，已经存在的消息数
		messageCountBeforeRefresh = mChatList.getCount();
		
		Log.i(TAG, "messageCountBeforeRefresh : " + messageCountBeforeRefresh);
		Log.i(TAG, "pull to refresh update data");
		
		// 此时下拉刷新
		if (minmsgid == 0) {
			
			int totalMessageInDatabase = 0;
			
			Cursor cursorTotal = this.getContentResolver().query(GeneralContentProvider.MESSAGE_CONTENT_URI, null, 
					MessageTable.COLUMN_MESSAGE_DIALOG_ID + " = ?", 
					new String[] {did}, MessageTable.COLUMN_MESSAGE_ID + " asc");
			
			if (cursorTotal != null && cursorTotal.moveToFirst()) {
				totalMessageInDatabase = cursorTotal.getCount();
				long mid = cursorTotal.getLong(cursorTotal.getColumnIndex(MessageTable.COLUMN_MESSAGE_ID));
				ContentValues values = new ContentValues();
				values.put(MessageTable.COLUMN_INTERRUPT, 1);
				
				getContentResolver().update(GeneralContentProvider.MESSAGE_CONTENT_URI, values, 
						MessageTable.COLUMN_MESSAGE_DIALOG_ID + " = ?" + " AND " + MessageTable.COLUMN_MESSAGE_ID + " = ?", 
						new String[] {did, String.valueOf(mid)});
				
				values.clear();
				cursorTotal.close();
			}			
			
			// 不从网络拉数据, 直接从数据库里读, 此时，由于数据库中存在断点，所以，需要判断，取的这30条数据中，是否存在断点
			
			long tempMessage;
			if (totalMessageInDatabase > MAX_PULL_DATABASE_NUM) {
				/*if ((totalMessageInDatabase - MAX_PULL_DATABASE_NUM) > MAX_PULL_DATABASE_NUM) {
					tempMessage = mTotalMessage;
				} else {
					tempMessage = mTotalMessage - MAX_PULL_DATABASE_NUM + (totalMessageInDatabase - MAX_PULL_DATABASE_NUM);
				}*/
				tempMessage = mTotalMessage;
			} else {
				tempMessage = totalMessageInDatabase;
			}
			
			long futureMessage = tempMessage + MAX_PULL_DATABASE_NUM;
			// long futureMessage = messageCountBeforeRefresh + MAX_PULL_DATABASE_NUM;
			
			// 统计数据库中存在的数量
			long inDatabase = 0;
			// 是否存在断点
			boolean existInterrupt = false;
			// 获得断点处的mid
			long interruptMid = 0;
			
			/*Log.i(TAG, "tempMessage : " + tempMessage);
			Log.i(TAG, "futureMessage : " + futureMessage);*/
			
			Cursor cursorInterrupt;
			
			if (totalMessageInDatabase > MAX_PULL_DATABASE_NUM) {
				if (totalMessageInDatabase > futureMessage) {
					cursorInterrupt = this.getContentResolver().query(GeneralContentProvider.MESSAGE_CONTENT_URI, null, 
							MessageTable.COLUMN_MESSAGE_DIALOG_ID + " = ?", 
							new String[] {did}, MessageTable.COLUMN_MESSAGE_ID + " desc limit " + futureMessage + " offset " + (totalMessageInDatabase - futureMessage));
				} else {
					cursorInterrupt = this.getContentResolver().query(GeneralContentProvider.MESSAGE_CONTENT_URI, null, 
							MessageTable.COLUMN_MESSAGE_DIALOG_ID + " = ?", 
							new String[] {did}, MessageTable.COLUMN_MESSAGE_ID + " desc limit " + totalMessageInDatabase);
				}
				
			} else {
				cursorInterrupt = this.getContentResolver().query(GeneralContentProvider.MESSAGE_CONTENT_URI, null, 
						MessageTable.COLUMN_MESSAGE_DIALOG_ID + " = ?", 
						new String[] {did}, MessageTable.COLUMN_MESSAGE_ID + " desc");
			}
			
			if (cursorInterrupt != null && cursorInterrupt.moveToFirst()) {
				
				do {
					// 1表示是断点
					if (cursorInterrupt.getInt(cursorInterrupt.getColumnIndex(MessageTable.COLUMN_INTERRUPT)) == 1) {
						// 获得断点处的mid
						existInterrupt = true;
						interruptMid = cursorInterrupt.getInt(cursorInterrupt.getColumnIndex(MessageTable.COLUMN_MESSAGE_ID));
						
						Log.i(TAG, "interrupt mid : " + interruptMid);
						Log.i(TAG, "interrupt name : " + cursorInterrupt.getString(cursorInterrupt.getColumnIndex(MessageTable.COLUMN_MESSAGE_OVERVIEW)));
						
						ContentValues values = new ContentValues();
						values.put(MessageTable.COLUMN_INTERRUPT, 0);
						// 同时，将此处的断点清空
						getContentResolver().update(GeneralContentProvider.MESSAGE_CONTENT_URI, values, 
								MessageTable.COLUMN_MESSAGE_DIALOG_ID + " = ?" + " AND " + MessageTable.COLUMN_MESSAGE_ID + " = ?", 
								new String[] {did, String.valueOf(interruptMid)});
						
						values.clear();
						break;
					} else {
						inDatabase++;
					}
					
				} while (cursorInterrupt.moveToNext());
				
				cursorInterrupt.close();
			}
			
			if (!existInterrupt) {
				Log.i(TAG, "此时，没有断点");
				mTotalMessage += MAX_PULL_DATABASE_NUM;
				getSupportLoaderManager().restartLoader(0, null, this);
			} else {
				Log.i(TAG, "此时，存在断点");
				// 说明，此时存在断点，此时，先将断点以下(即mid > 该断点的mid)的部分执行restartLoader
				// 之后，执行网络请求得到old chat message
				Log.i(TAG, "mTotalMessage : " + mTotalMessage);
				Log.i(TAG, "totalMessageInDatabase : " + totalMessageInDatabase);
				
				if (mTotalMessage < totalMessageInDatabase) {
					if ((totalMessageInDatabase - MAX_PULL_DATABASE_NUM) > MAX_PULL_DATABASE_NUM) {
						mTotalMessage += MAX_PULL_DATABASE_NUM;
					} else {
						mTotalMessage += (totalMessageInDatabase - MAX_PULL_DATABASE_NUM);
					}
					getSupportLoaderManager().restartLoader(0, null, this);
				} else {
					// 此时，说明还可以从网络上拉取
					Log.i(TAG, "此时，说明还可以从网络上拉取");
					// mTotalMessage += inDatabase;
					// Log.i(TAG, "inDatabase : " + inDatabase);
					Log.i(TAG, "mTotalMessage : " + mTotalMessage);
					
					// getSupportLoaderManager().restartLoader(0, null, this);
					
					// 此时，从网络拉数据
					mGeneralEventLogic.getOldChatMessages(did, String.valueOf(interruptMid));
				}
			}
			
		} else {
			// 此时，从网络拉数据
			mGeneralEventLogic.getOldChatMessages(did, String.valueOf(minmsgid));
		}
	}
	

	/**
	 * 重发
	 */
	@Override
	public void onResendClick(String did, String cmid, String message) {
		// 第三个属性为receiver id
		// isReal
		mGeneralEventLogic.sendTextMessage(mineIsreal, did, "", receiverIsreal, "123", cmid, message);
		textMessage = message;
	}
	
	@Override
	public void onBackPressed() {
		if (!getIntent().getBooleanExtra("is_candidate", false)) {
			Intent intent = new Intent();
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setClass(this, MainRecruitActivity.class);
			intent.putExtra(GlobalConstants.PAGE, 1);
			startActivity(intent);
		} else {
			super.onBackPressed();
		}
		
	}

	/**
	 * 重写onDestroy
	 */
	@Override
	protected void onDestroy() {
		ImDbManager.getInstance().cleanMessageListOtherCount(Long.valueOf(did));
		// static变量释放引用
		did = null;
		Log.i(TAG, "did is null");
		
		super.onDestroy();
	}
	
	/**
	 * EventBus回调
	 * @param event
	 */
	public void onEventMainThread(P2PMessageEvent event) {
		
		Message msg = Message.obtain();
		msg.what = event.getWhat();
		Log.i(TAG, "onEvent msg what : " + msg.what);
		mHandler.sendMessage(msg);
	}

}
