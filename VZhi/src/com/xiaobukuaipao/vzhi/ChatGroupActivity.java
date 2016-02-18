package com.xiaobukuaipao.vzhi;

import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.android.volley.toolbox.Volley;
import com.xiaobukuaipao.vzhi.cache.CommonBitmapMemoryCache;
import com.xiaobukuaipao.vzhi.contentprovider.GeneralContentProvider;
import com.xiaobukuaipao.vzhi.database.ContactUserTable;
import com.xiaobukuaipao.vzhi.database.CookieTable;
import com.xiaobukuaipao.vzhi.database.MessageGroupTable;
import com.xiaobukuaipao.vzhi.database.MessageSummaryTable;
import com.xiaobukuaipao.vzhi.domain.user.BasicInfo;
import com.xiaobukuaipao.vzhi.domain.user.ImUser;
import com.xiaobukuaipao.vzhi.domain.user.UserBasic;
import com.xiaobukuaipao.vzhi.event.GroupMessageEvent;
import com.xiaobukuaipao.vzhi.event.InfoResult;
import com.xiaobukuaipao.vzhi.im.IMConstants;
import com.xiaobukuaipao.vzhi.im.ImDbManager;
import com.xiaobukuaipao.vzhi.im.ImUtils;
import com.xiaobukuaipao.vzhi.im.MessageGroupChatAdapter;
import com.xiaobukuaipao.vzhi.im.MessageGroupChatAdapter.OnResendClickListener;
import com.xiaobukuaipao.vzhi.manager.GeneralDbManager;
import com.xiaobukuaipao.vzhi.register.ImageClipActivity;
import com.xiaobukuaipao.vzhi.register.ImagePickerActivity;
import com.xiaobukuaipao.vzhi.register.RegisterBaseInfoAvatarActivity;
import com.xiaobukuaipao.vzhi.util.GlobalConstants;
import com.xiaobukuaipao.vzhi.util.Logcat;
import com.xiaobukuaipao.vzhi.util.SharedPreferencesUtil;
import com.xiaobukuaipao.vzhi.util.StringUtils;
import com.xiaobukuaipao.vzhi.util.VToast;
import com.xiaobukuaipao.vzhi.view.ProgressBarCircularIndeterminate;
import com.xiaobukuaipao.vzhi.view.RoundedImageView;
import com.xiaobukuaipao.vzhi.wrap.ChatWrapActivity;

/**
 * 1.当进群的时候，首先拉一下个人的基本信息，并更新到数据库中
 * 2.获得群的基本资料
 */
public class ChatGroupActivity extends ChatWrapActivity implements OnClickListener, 
								LoaderCallbacks<Cursor>, /*IXListViewListener, */OnResendClickListener {
	
	public static final String TAG = ChatGroupActivity.class.getSimpleName();
	//  从服务器端最多拉取的聊天数量，实际拉取的数量可能小于30条
	private static final int MAX_PULL_NUM = 30;
	// 本地，每次从数据库中取的条数--30条
	private static final int MAX_PULL_DATABASE_NUM = 30;
	
	public static final String CHOOSE_AVATAR = "choose_avatar";
	
	private PopupWindow mPopupWindow;
	private LinearLayout mPopupLayout;
	
	// 当前选择的头像--实际上代表url
	private String mCurrentChooseAvatar = null;
	
	// 请求队列
	private RequestQueue mQueue;
	private ImageLoader mImageLoader;
	private ImageListener mListener;
	private RoundedImageView mAvatar;
	private EditText  mNickName;

	private boolean isClickModify = false;
	private LinearLayout mPopupMainMenu;
	private LinearLayout mPopupRechooseMenu;
	
	private ListView mChatList;
	
	private EditText mInputEdit;
	private Button mAddBtn;
	private Button mSendBtn;
	
	private ContentValues values;
	
	private boolean isFirstEnter = true;
	
	private TextView mRealProfileCard, mPositionCard, mResumeCard;
	
	// Gid -- 最重要的参数
	public static String groupId;
	private String groupName;
	
	// 从数据库中读取的消息数
	private int mTotalMessage;
	
	// CursorAdapter
	private MessageGroupChatAdapter adapter;
	
	// minmsgid -- 用于获取更老的信息
	// minmsgid = 0 表示这是最后一页
	private long minmsgid = 0;
	
	// 发送的Text消息
	private String textMessage;
	
	// 是否发送text消息
	private boolean isSendMessage = false;
	// 是否下拉刷新
	private boolean isPullToRefresh = false;
	
	// 当前发送的信息的_id
	@SuppressWarnings("unused")
	private long _id = 0; 
	
	// 当前用户的ID
	private long uid;
	
	private String nickAvatar = null;
	
	private ProgressBarCircularIndeterminate mProgressBar;
	
	private PtrClassicFrameLayout mPtrFrame;
	
	// 下拉刷新时，列表中存在的消息数
	private int messageCountBeforeRefresh;
	
	// Handler
	static class WeakHandler extends Handler {
		private WeakReference<ChatGroupActivity> mOuter;
		public WeakHandler(ChatGroupActivity activity) {
			mOuter = new WeakReference<ChatGroupActivity>(activity);
		}
		
		public void handleMessage(Message msg) {
			ChatGroupActivity outer = mOuter.get();
			if (outer != null) {
				// Do something
			}
		}
	}
	
	private WeakHandler mHandler = new WeakHandler(ChatGroupActivity.this) {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case 1:
					mTotalMessage++;
					if (StringUtils.isNotEmpty(groupId)) {
						getSupportLoaderManager().restartLoader(0, null, ChatGroupActivity.this);
					}
					Log.i(TAG, "receive total message : " + mTotalMessage);
					break;
				case 2:
					// 下载群组中的人的头像
					ImUser group = (ImUser) msg.obj;
					if (group != null) {
						Log.i(TAG, "group uid : " + group.getUid());
						getGroupPersonAvatarAndName(group.getUid(), group.getGid());
					}
					break;
			}
		}
	};
	
	private void getGroupPersonAvatarAndName(long uid, long gid) {
		mGeneralEventLogic.getGroupPersonAvatarAndName(uid, gid);
	}
	
	@SuppressLint("ClickableViewAccessibility")
	@Override
	public void initUIAndData() {
		setContentView(R.layout.activity_chat_group);
		setHeaderMenuByLeft(this);
		
		// 赋值--从入口处获得参数
		groupId = getIntent().getStringExtra("gid");
		
		NotificationManager manger = (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);
		manger.cancel(Integer.valueOf(groupId));
		
		groupName = getIntent().getStringExtra("gname");
		if (!StringUtils.isEmpty(groupName)) {
			setHeaderMenuByCenterTitle(groupName);
		} else {
			mGeneralEventLogic.getGroupAvatarAndName(Long.valueOf(groupId));
		}
		
		mProgressBar = (ProgressBarCircularIndeterminate) findViewById(R.id.progressBarCircularIndeterminate);
		
		mPopupLayout = (LinearLayout) View.inflate(this, R.layout.popup_first_into_group, null);
		
		mPopupWindow = new PopupWindow();
		mPopupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		mPopupWindow.setBackgroundDrawable(new ColorDrawable(0));
		mPopupWindow.setClippingEnabled(true);
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
		
		mPopupLayout.findViewById(R.id.popup_base_takephoto).setOnClickListener(this);
		mPopupLayout.findViewById(R.id.popup_base_imgs).setOnClickListener(this);
		mPopupLayout.findViewById(R.id.popup_base_avatar).setOnClickListener(this);
		mPopupLayout.findViewById(R.id.popup_base_cancel).setOnClickListener(this);
		
		mAvatar = (RoundedImageView) mPopupLayout.findViewById(R.id.popup_avatar);
		mNickName = (EditText) mPopupLayout.findViewById(R.id.popup_nickname);
		mPopupLayout.findViewById(R.id.popup_rechoose).setOnClickListener(this);
		mPopupLayout.findViewById(R.id.popup_reedit).setOnClickListener(this);
		mPopupLayout.findViewById(R.id.popup_go_on).setOnClickListener(this);
		mPopupMainMenu = (LinearLayout) mPopupLayout.findViewById(R.id.popup_main_menu);
		mPopupRechooseMenu = (LinearLayout) mPopupLayout.findViewById(R.id.popup_rechoose_menu);
		
		mQueue = Volley.newRequestQueue(this);
		mImageLoader = new ImageLoader(mQueue, new CommonBitmapMemoryCache());
		
		mInputEdit = (EditText) findViewById(R.id.chat_input_edit);
		mInputEdit.addTextChangedListener(mTextWatcher);
		
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
		
		mAddBtn = (Button) findViewById(R.id.chat_add_btn);
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
		mResumeCard = (TextView) findViewById(R.id.tv_interview_card);
		
		mAddBtn.setOnClickListener(this);
		mSendBtn.setOnClickListener(this);
		mRealProfileCard.setOnClickListener(this);
		mPositionCard.setOnClickListener(this);
		mResumeCard.setOnClickListener(this);
		
		findViewById(R.id.menu_bar_right).setOnClickListener(this);
		
		// 初始化下拉刷新
		// 首先不允许加载更多
		/*mChatList.setPullLoadEnable(false);
		// 允许下拉
		mChatList.setPullRefreshEnable(true);
		// 设置监听器
		mChatList.setXListViewListener(this);
		mChatList.pullRefreshing();*/
		mChatList.setDividerHeight(0);
		
		Cursor cookieCursor = this.getContentResolver().query(GeneralContentProvider.COOKIE_CONTENT_URI, null, null, null, null);
		if (cookieCursor != null && cookieCursor.moveToFirst()) {
			uid = (long) cookieCursor.getInt(cookieCursor.getColumnIndex(CookieTable.COLUMN_ID));
			cookieCursor.close();
		}
		
		// ImDbManager.getInstance().setHandler(mHandler);
		
		mGeneralEventLogic.getBasicinfo();
		// 首先拉取对方的头像信息
		if (!StringUtils.isEmpty(groupId)) {
			mGeneralEventLogic.getGroupAvatarAndName(Long.valueOf(groupId));
		}
		
		if (!StringUtils.isEmpty(groupId)) {
			getNewMessageFromServer(groupId);
		}
		
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
		
		mPopupWindow.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss() {
			
				if(isClickModify){
					mNickName.setEnabled(false);
        			mGeneralEventLogic.setBasicInfo("", "", "", mNickName.getText().toString(), -1, -1, "", -1, "", "",-1);
				}
			}
		});
	}
	
	private void getNewMessageFromServer(String groupId) {
		Cursor cursor = this.getContentResolver().query(GeneralContentProvider.MESSAGE_GROUP_CONTENT_URI, null, 
				MessageGroupTable.COLUMN_MESSAGE_GROUP_ID + " = ?", 
				new String[] { groupId }, MessageGroupTable.COLUMN_MESSAGE_ID + " asc");
		if (cursor != null && cursor.moveToFirst()) {
			// 此时数据渲染完毕, 根据当前的Cursor找到底部的指针，此时开始网络请求
			if (cursor.moveToLast()) {
				// 此时，表示Cursor移动到最后一个元素
				long mid = cursor.getInt(cursor .getColumnIndexOrThrow(MessageGroupTable.COLUMN_MESSAGE_ID));
				long gid = cursor.getInt(cursor .getColumnIndexOrThrow(MessageGroupTable.COLUMN_MESSAGE_GROUP_ID));
				// 此时，拿聊天框最新消息的请求
				mGeneralEventLogic.getNewGroupChatMessages(String.valueOf(gid), String.valueOf(mid));
				mProgressBar.setVisibility(View.VISIBLE);
			} else {
				mGeneralEventLogic.getNewGroupChatMessages(groupId, String.valueOf(-1));
				mProgressBar.setVisibility(View.VISIBLE);
			}
			cursor.close();
		} else {
			mGeneralEventLogic.getNewGroupChatMessages(groupId, String.valueOf(-1));
			mProgressBar.setVisibility(View.VISIBLE);
		}
	}
	
	public synchronized void initTotalMessage() {
		// 缺省是每次加30个
		mTotalMessage = 0;
		
		if (!StringUtils.isEmpty(groupId)) {
			
			int totalMessageInDatabase = 0;
			Cursor cursorTotal = this.getContentResolver().query(GeneralContentProvider.MESSAGE_GROUP_CONTENT_URI, null, 
					MessageGroupTable.COLUMN_MESSAGE_GROUP_ID + " = ?", 
					new String[] {groupId}, MessageGroupTable.COLUMN_MESSAGE_ID + " asc");
			
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
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if (isClickModify) {
			mGeneralEventLogic.getBasicinfo();
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
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			
		}
		
		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			
		}
	};
	
	private InputMethodManager imm;
	
	private void showPopupWindow() {
			mPopupWindow.setAnimationStyle(android.R.style.Animation_InputMethod);
			mPopupWindow.setWidth(LayoutParams.MATCH_PARENT);
			mPopupWindow.setHeight(LayoutParams.MATCH_PARENT);
			mPopupWindow.setContentView(mPopupLayout);
			mPopupWindow.setOutsideTouchable(true);
			mPopupWindow.setFocusable(true);
			mPopupWindow.showAtLocation(getWindow().getDecorView().getRootView(), Gravity.BOTTOM, 0, 0);
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
			mInputEdit.requestFocus();
			
			long mid = 0;
			// 先查找数据中，找到最后一条数据的_id和mid
			Cursor cursor = this.getContentResolver().query(GeneralContentProvider.MESSAGE_GROUP_CONTENT_URI, 
					null, MessageGroupTable.COLUMN_MESSAGE_GROUP_ID + " = ?", 
					new String[] { groupId }, MessageGroupTable.COLUMN_MESSAGE_ID + " asc");
			
			if (cursor != null && cursor.moveToFirst()) {
				cursor.moveToLast();
				_id = cursor.getInt(cursor.getColumnIndex(MessageGroupTable.COLUMN_ID));
				mid = cursor.getInt(cursor.getColumnIndex(MessageGroupTable.COLUMN_MESSAGE_ID));
				cursor.close();
			}		
			
			ContentValues values = new ContentValues();
			// Mid递增顺序--可以作为排序字段，且永远不为null
			values.put(MessageGroupTable.COLUMN_MESSAGE_ID, mid + 1);
			values.put(MessageGroupTable.COLUMN_MESSAGE_GROUP_ID, Long.valueOf(groupId));
			values.put(MessageGroupTable.COLUMN_MESSAGE_FROM_USER_ID, uid);
			values.put(MessageGroupTable.COLUMN_MESSAGE_TYPE, IMConstants.TYPE_P2P_TEXT);
			values.put(MessageGroupTable.COLUMN_MESSAGE_DISPLAY_TYPE, ImUtils.matchDisplayType(IMConstants.TYPE_P2P_TEXT));
			// 代表发送text--返回的Json中不含body
			values.put(MessageGroupTable.COLUMN_MESSAGE_OVERVIEW, textMessage);
			
			isSendMessage = true;
			
			// 此时，正在加载
			values.put(MessageGroupTable.COLUMN_MESSAGE_STATUS, 0);
			
			// 保存当前消息的插入时间--可能以后需要根据时间来排序聊天信息
			values.put(MessageGroupTable.COLUMN_CREATED, System.currentTimeMillis());
			
			// 插入数据库
			getContentResolver().insert(GeneralContentProvider.MESSAGE_GROUP_CONTENT_URI, values);
			values.clear();
			
			mTotalMessage++;
			
			Cursor cursorInsert = this.getContentResolver().query(GeneralContentProvider.MESSAGE_GROUP_CONTENT_URI, 
					null, MessageGroupTable.COLUMN_MESSAGE_GROUP_ID + " = ?", 
					new String[] { groupId }, MessageGroupTable.COLUMN_MESSAGE_ID + " asc");
			
			if (cursorInsert != null && cursorInsert.moveToFirst()) {
				cursorInsert.moveToLast();
				mGeneralEventLogic.sendGroupTextMessage(groupId, textMessage, 
						String.valueOf(cursorInsert.getInt(cursorInsert.getColumnIndex(MessageGroupTable.COLUMN_ID))));
				cursorInsert.close();
				
				getSupportLoaderManager().restartLoader(0, null, ChatGroupActivity.this);
			}
		} else {
			Toast.makeText(this, "您还没有填写消息内容", Toast.LENGTH_SHORT).show();
		}
		
		// 4.当数据添加到数据库后，ListView列表会刷新，之后填充数据，然后显示ProgressDialog表示消息是否发送成功
		// 5.当消息发送成功后, ProgressDialog消失，否则变成警告Dialog, 提示用户消息未发送成功，用户可以选择重发或者删除
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		Cursor cursor = this.getContentResolver().query(GeneralContentProvider.MESSAGE_GROUP_CONTENT_URI, null, 
				MessageGroupTable.COLUMN_MESSAGE_GROUP_ID + " = ?", 
				new String[] { groupId }, null);
		// 消息总数
		// 每次加载30条
		Uri uri = GeneralContentProvider.MESSAGE_GROUP_CONTENT_URI;
		if (cursor.getCount() > MAX_PULL_DATABASE_NUM) {
			cursor.close();
			return new CursorLoader(this, uri, null, MessageGroupTable.COLUMN_MESSAGE_GROUP_ID + " = ?", 
					new String[] { groupId }, MessageGroupTable.COLUMN_MESSAGE_ID + " asc limit " + mTotalMessage + " offset " + (cursor.getCount() - mTotalMessage));
		} else {
			cursor.close();
			return new CursorLoader(this, uri, null, MessageGroupTable.COLUMN_MESSAGE_GROUP_ID + " = ?", 
					new String[] { groupId }, MessageGroupTable.COLUMN_MESSAGE_ID + " asc limit " + mTotalMessage);
		}
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		if (isFirstEnter) {
			adapter = new MessageGroupChatAdapter(this, cursor, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER, mHandler);
			adapter.setOnResendClickListener(this);
			mChatList.setAdapter(adapter);
			
			// 用完后，cursor移动到顶部
			cursor.moveToFirst();
			
			mChatList.setSelection(mChatList.getBottom());
			isFirstEnter = false;
		}
		
		adapter.swapCursor(cursor);
		
		// ListView显示最底部--两种方式
		// mChatList.setSelection(adapter.getCount() - 1);
		if (isSendMessage) {
			// 如果是发送消息，则定位到底部
			mChatList.setSelection(mChatList.getBottom());
			isSendMessage = false;
			
			Log.i(TAG, "send message");
			
		} else if (isPullToRefresh) {
			mPtrFrame.refreshComplete();
			
			if (adapter.getCount() > messageCountBeforeRefresh) {
				mChatList.clearFocus();
				mChatList.post(new Runnable() {
				    @Override
				    public void run() {
				    	mChatList.setSelection(adapter.getCount() - messageCountBeforeRefresh - 1);
				    }
				});
			} else {
				mChatList.clearFocus();
				mChatList.post(new Runnable() {
				    @Override
				    public void run() {
				    	mChatList.setSelection(adapter.getCount() - messageCountBeforeRefresh);
				    }
				});
			}
			
			Log.i(TAG, "is pull to refresh");
			isPullToRefresh = false;
		} else {
			
			Log.i(TAG, "others");
			mChatList.setSelection(mChatList.getBottom());
		}
		
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		adapter.swapCursor(null);
	}
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	@Override
	public void onEventMainThread(Message msg) {
		
		if (msg.obj instanceof InfoResult) {
			
			InfoResult infoResult = (InfoResult) msg.obj;
			switch (msg.what) {
			case R.id.register_get_basicinfo:
				JSONObject basicinfo = (JSONObject) JSONObject.parseObject(infoResult.getResult());
				BasicInfo basicInfo = new BasicInfo(basicinfo.getJSONObject(GlobalConstants.JSON_USERBASIC));
				UserBasic userBasic = new UserBasic(basicinfo.getJSONObject(GlobalConstants.JSON_USERBASIC));
				
				if (StringUtils.isNotEmpty(basicInfo.getAvatar())) {
					mListener = ImageLoader.getImageListener(mAvatar,R.drawable.general_user_avatar, R.drawable.general_user_avatar);
					mImageLoader.get(basicInfo.getAvatar(), mListener);
				}
				
				if (StringUtils.isNotEmpty(basicInfo.getNickname())) {
					mNickName.setText(basicInfo.getNickname());
				}
				if (basicInfo.getGender() == 1) {
					mAvatar.setBorderColor(getResources().getColor(R.color.boy_border_color));
				} else {
					mAvatar.setBorderColor(getResources().getColor(R.color.girl_border_color));
				}
				isClickModify = false;
				// 更新个人信息数据库
				GeneralDbManager.getInstance().insertToUserInfoTable(userBasic);
				break;
				
			case R.id.profile_basic_info_set:
				if(infoResult.getMessage().getStatus() == 0){
					if (!StringUtils.isEmpty(nickAvatar)) {
						// 更新数据库
						GeneralDbManager.getInstance().updateUserTableNickName(nickAvatar);
						getSupportLoaderManager().restartLoader(0, null, this);
					}
				}
				
				VToast.show(this, infoResult.getMessage().getMsg());
				break;
			case R.id.register_upload_avatar:
				if (infoResult.getMessage().getStatus() == 0) {
					// 此时会返回URL
					JSONObject jsonObject = (JSONObject) JSONObject.parse(infoResult.getResult());
					if(jsonObject != null){
						nickAvatar = jsonObject.getString(GlobalConstants.URL_NICK_AVATAR);
						mGeneralEventLogic.setBasicInfo("",  nickAvatar , "", "", -1, -1, "", -1, "", "", -1);
					}
				}
				VToast.show(this, infoResult.getMessage().getMsg());
				break;
				
			case R.id.social_get_new_group_messages:
				
				// 此时，获取聊天框的最新消息
				JSONObject newGroupMessageObject = JSONObject.parseObject(infoResult.getResult());
				Log.i(TAG, infoResult.getResult());
				if (newGroupMessageObject.getJSONArray(GlobalConstants.JSON_DATA) != null) {
					// 此时有数据
					insertIntoMessageGroupTable(newGroupMessageObject.getJSONArray(GlobalConstants.JSON_DATA));
				}
				
				if (newGroupMessageObject.getString(GlobalConstants.JSON_MINMSGID) != null) {
					minmsgid = Long.valueOf(newGroupMessageObject.getString(GlobalConstants.JSON_MINMSGID));
				} else {
					minmsgid = 0;
				}
				
				// 同步方法
				initTotalMessage();
				
				// Prepare the loader.  Either re-connect with an existing one,
		        // or start a new one.
				if (!StringUtils.isEmpty(groupId)) {
					getSupportLoaderManager().initLoader(0, null, this);
				}
				
				mProgressBar.setVisibility(View.GONE);
				break;
				
			case R.id.social_send_group_text:
				// 此时发送群文本消息，返回值判断
				if (infoResult.getMessage().getStatus() == 0) {
					// 3.将此message添加到数据库中
					// insertIntoMessageGroupTable(infoResult.getResult());
					if (StringUtils.isNotEmpty(infoResult.getResult())) {
						
						// 发送网络请求成功
						Log.i(TAG, "send group text : " + infoResult.getResult());
						
						updateSendMessageTable(infoResult.getResult());
					}
				} else {
					Log.i(TAG, "Http Status : " + infoResult.getMessage().getStatus());
					updateSendMessageFailedTable();
				}
//				VToast.show(this, infoResult.getMessage().getMsg());
				break;
				
			case R.id.social_get_group_old_messages:
				JSONObject oldGroupMessageObject = JSONObject.parseObject(infoResult.getResult());
				Log.i(TAG, infoResult.getResult());
				if (oldGroupMessageObject.getJSONArray(GlobalConstants.JSON_DATA) != null) {
					// 此时有数据
					insertIntoMessageGroupTable(oldGroupMessageObject.getJSONArray(GlobalConstants.JSON_DATA));
					isPullToRefresh = true;
				}
				
				if (oldGroupMessageObject.getString(GlobalConstants.JSON_MINMSGID) != null) {
					minmsgid = Long.valueOf(oldGroupMessageObject.getString(GlobalConstants.JSON_MINMSGID));
				} else {
					minmsgid = 0;
				}
				break;
				
			case R.id.social_get_group_avatar:
				Log.i(TAG, infoResult.getResult());
				// 实名匿名群
				JSONObject jsonObject = (JSONObject) JSONObject.parse(infoResult.getResult());
				
				if (jsonObject.getJSONObject(GlobalConstants.JSON_GROUP).getInteger(GlobalConstants.JSON_TYPE) == 1001) {
					// 此时，该群是匿名群
					if(StringUtils.isEmpty(SharedPreferencesUtil.getInstance().getString(groupId))) {
						// 第一次进来
						getWindow().getDecorView().getRootView().getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
							
							@SuppressWarnings("deprecation")
							@Override
							public void onGlobalLayout() {
								getWindow().getDecorView().getRootView().getViewTreeObserver().removeGlobalOnLayoutListener(this);
								showPopupWindow();
								SharedPreferencesUtil.getInstance().putString(groupId, groupId);
							}
						});
					}
				}
				
				// 设置群的名字
				setHeaderMenuByCenterTitle(((JSONObject) JSONObject.parse(infoResult.getResult())).
						getJSONObject(GlobalConstants.JSON_GROUP).getString(GlobalConstants.JSON_NAME));
				
				insertGroupInfoIntoContactUserTable(infoResult.getResult());
				break;
				
			case R.id.social_get_group_person_avatar:
				Log.i(TAG, infoResult.getResult());
				insertPersonInfoIntoContactUserTable(infoResult.getResult());
				break;
			}
		} else {
			// 此时消息未发送失败
			Log.i(TAG, "login failed");
			
			updateSendMessageFailedTable();
			
			switch (msg.what) {
				case R.id.social_get_new_group_messages:
					// 同步方法
					initTotalMessage();
					// Prepare the loader.  Either re-connect with an existing one,
			        // or start a new one.
					if (!StringUtils.isEmpty(groupId)) {
						getSupportLoaderManager().initLoader(0, null, this);
					}
					break;
			}
			
			// 先判断以下是否断网，如果是，则将所有自己正在发送的状态置为发送失败
			// 获得网络连接服务
			try {
	            // 获得网络连接服务
	            ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
	            
	            if (null != cm) {
	                NetworkInfo ni = cm.getActiveNetworkInfo();
	                if (null != ni && ni.isAvailable()) {
	                    // 网络连接成功
	                }
	            }
	            
	        } catch (Exception e) {
	            Toast.makeText(this, "网络连接失败", Toast.LENGTH_SHORT).show();
	        }
		}
	}
	
	
	private void updateSendMessageTable (String jsonMessage) {
		if (jsonMessage != null && jsonMessage.length() > 0) {
			JSONObject jsonObject = JSONObject.parseObject(jsonMessage);
			ContentValues values = new ContentValues();
			
			ImDbManager.getInstance().updateSendMessageGroupTable(jsonMessage);
			
			Cursor cursor = this.getContentResolver().query(GeneralContentProvider.MESSAGE_GROUP_CONTENT_URI, 
					null, MessageGroupTable.COLUMN_ID + " = ?" + " AND " + MessageGroupTable.COLUMN_MESSAGE_GROUP_ID + " = ?", 
					new String[] { jsonObject.getString(GlobalConstants.JSON_CMID), jsonObject.getString(GlobalConstants.JSON_GID) }, null);
			
			cursor.close();
			
			values.clear();
			
			values.put(MessageSummaryTable.COLUMN_MESSAGE_TYPE, jsonObject.getLong(GlobalConstants.JSON_TYPE));
			values.put(MessageSummaryTable.COLUMN_MESSAGE_OVERVIEW, textMessage);
			values.put(MessageSummaryTable.COLUMN_UPDATED,  jsonObject.getLong(GlobalConstants.JSON_CREATETIME));
			values.put(MessageSummaryTable.COLUMN_MESSAGE_TO_USER_ID, groupId);
			// display type
			values.put(MessageSummaryTable.COLUMN_MESSAGE_DISPLAY_TYPE, IMConstants.DISPLAY_LIST_TYPE_DIALOG_GROUP);
			
			// 首先查表判断该消息是否存在
			Cursor cursorSummary = this.getContentResolver().query(GeneralContentProvider.MESSAGE_SUMMARY_CONTENT_URI, null, 
					MessageSummaryTable.COLUMN_MESSAGE_DISPLAY_TYPE + " = ?" + " AND " + MessageSummaryTable.COLUMN_MESSAGE_GROUP_ID + " = ?", 
					new String[] { String.valueOf(IMConstants.DISPLAY_LIST_TYPE_DIALOG_GROUP), jsonObject.getString(GlobalConstants.JSON_GID)}, null);
			
			if (cursorSummary != null && cursorSummary.moveToFirst()) {
				// 此时，消息已经存在--执行更新操作
				values.put(MessageSummaryTable.COLUMN_UNREADCOUNT, 0);
				this.getContentResolver().update(GeneralContentProvider.MESSAGE_SUMMARY_CONTENT_URI, values, 
						MessageSummaryTable.COLUMN_MESSAGE_DISPLAY_TYPE + " = ?" + " AND " + MessageSummaryTable.COLUMN_MESSAGE_GROUP_ID + " = ?", 
						new String[] { String.valueOf(IMConstants.DISPLAY_LIST_TYPE_DIALOG_GROUP), jsonObject.getString(GlobalConstants.JSON_GID)});
				
				isSendMessage = true;
				cursorSummary.close();
			} else {
				values.put(MessageSummaryTable.COLUMN_UNREADCOUNT, 0);
				values.put(MessageSummaryTable.COLUMN_MESSAGE_GROUP_ID, jsonObject.getString(GlobalConstants.JSON_GID));
				this.getContentResolver().insert(GeneralContentProvider.MESSAGE_SUMMARY_CONTENT_URI, values);
			}
			
			values.clear();
		}
	}
	
	private synchronized void updateSendMessageFailedTable() {
		
		ContentValues values = new ContentValues();
		// 2代表发送失败
		values.put(MessageGroupTable.COLUMN_MESSAGE_STATUS, 2);
		
		// 检查所有的状态为ProgressBar的消息，将他们都置为2
		// 0 表示 progressbar状态
		Cursor cursor = getContentResolver().query(GeneralContentProvider.MESSAGE_GROUP_CONTENT_URI, null, 
				MessageGroupTable.COLUMN_MESSAGE_GROUP_ID + " = ?" + " AND " + MessageGroupTable.COLUMN_MESSAGE_STATUS + " = ?"
				+ " AND " + MessageGroupTable.COLUMN_MESSAGE_FROM_USER_ID + " = ?", 
				new String[] { groupId,  String.valueOf(0), String.valueOf(uid)}, null);
		
		if (cursor != null && cursor.moveToFirst()) {
			do {
				long mid = cursor.getInt(cursor.getColumnIndex(MessageGroupTable.COLUMN_MESSAGE_ID));
				// 更新数据库
				getContentResolver().update(GeneralContentProvider.MESSAGE_GROUP_CONTENT_URI, values, 
						MessageGroupTable.COLUMN_MESSAGE_ID + " = ?" + " AND " + MessageGroupTable.COLUMN_MESSAGE_GROUP_ID + " = ?", 
						new String[] { String.valueOf(mid), groupId});
			} while (cursor.moveToNext());
				
			cursor.close();
		}
		
		values.clear();
	}
	
	/**
	 * 将个人信息插入到ContactUser表中
	 * @param userMessage
	 */
	private synchronized void insertPersonInfoIntoContactUserTable(String userMessage) {
		JSONObject jsonObject = (JSONObject) JSONObject.parse(userMessage);
		if (jsonObject != null) {
			ContentValues values = new ContentValues();
			if (jsonObject.getLong(GlobalConstants.JSON_ID) != null) {
				values.put(ContactUserTable.COLUMN_UID, jsonObject.getLong(GlobalConstants.JSON_ID));
			}
			
			if (jsonObject.getString(GlobalConstants.JSON_AVATAR) != null) {
				Log.i(TAG, "avatar : " + jsonObject.getString(GlobalConstants.JSON_AVATAR));
			}
			
			if (jsonObject.getLong(GlobalConstants.JSON_ISREAL) != null) {
				values.put(ContactUserTable.COLUMN_ISREAL, jsonObject.getLong(GlobalConstants.JSON_ISREAL));
				
				if (jsonObject.getLong(GlobalConstants.JSON_ISREAL) == 1) {
					// 实名个人
					if (jsonObject.getString(GlobalConstants.JSON_AVATAR) != null) {
						values.put(ContactUserTable.COLUMN_AVATAR, jsonObject.getString(GlobalConstants.JSON_AVATAR));
					}
					
					if (jsonObject.getString(GlobalConstants.JSON_NAME) != null) {
						values.put(ContactUserTable.COLUMN_NAME, jsonObject.getString(GlobalConstants.JSON_NAME));
					}
				} else {
					// 匿名个人
					if (jsonObject.getString(GlobalConstants.JSON_AVATAR) != null) {
						values.put(ContactUserTable.COLUMN_NICKAVATAR, jsonObject.getString(GlobalConstants.JSON_AVATAR));
					}
					
					if (jsonObject.getString(GlobalConstants.JSON_NAME) != null) {
						values.put(ContactUserTable.COLUMN_NICKNAME, jsonObject.getString(GlobalConstants.JSON_NAME));
					}
				}
			}
			
			if (StringUtils.isNotEmpty(jsonObject.getString(GlobalConstants.JSON_GID))) {
				values.put(ContactUserTable.COLUMN_GID, Long.valueOf(jsonObject.getString(GlobalConstants.JSON_GID)));
			}
			
			if (values.size() > 0) {
				
				 if (StringUtils.isNotEmpty(jsonObject.getString(GlobalConstants.JSON_GID))) {
						Cursor cursor = this.getContentResolver().query(GeneralContentProvider.CONTACT_USER_CONTENT_URI, null, 
								ContactUserTable.COLUMN_UID + " = ?" + " AND " + ContactUserTable.COLUMN_GID + " = ?", 
								new String[] { String.valueOf(jsonObject.getLong(GlobalConstants.JSON_ID)) ,
										String.valueOf(jsonObject.getString(GlobalConstants.JSON_GID)) }, null);
						
						if (cursor != null && cursor.moveToFirst()) {
							this.getContentResolver().update(GeneralContentProvider.CONTACT_USER_CONTENT_URI, values, ContactUserTable.COLUMN_UID + " = ?" + " AND " + ContactUserTable.COLUMN_GID + " = ?", 
								new String[] { String.valueOf(jsonObject.getLong(GlobalConstants.JSON_ID)) ,
										String.valueOf(jsonObject.getString(GlobalConstants.JSON_GID)) });
							cursor.close();
							adapter.notifyDataSetChanged();
						} else {
							// 插入不用考虑
							this.getContentResolver().insert(GeneralContentProvider.CONTACT_USER_CONTENT_URI, values);
							adapter.notifyDataSetChanged();
						}
						
					}
			}
			
			values.clear();
		}
	}
	
	/**
	 * 将群组信息加入到ContactUser中
	 * @param groupMessage
	 */
	private synchronized void insertGroupInfoIntoContactUserTable(String groupMessage) {
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
			
			Cursor cursor = this.getContentResolver().query(GeneralContentProvider.CONTACT_USER_CONTENT_URI, null, 
					ContactUserTable.COLUMN_UID + " = ?", new String[] { String.valueOf(jsonObject.getLong(GlobalConstants.JSON_GID)) }, null);
			
			if (cursor != null && cursor.moveToFirst()) {
				// 执行更新操作
				this.getContentResolver().update(GeneralContentProvider.CONTACT_USER_CONTENT_URI, values, 
						ContactUserTable.COLUMN_UID  + " = ?", new String[] { String.valueOf(jsonObject.getLong(GlobalConstants.JSON_GID)) });
				cursor.close();
			} else {
				if (values.size() > 0) {
					this.getContentResolver().insert(GeneralContentProvider.CONTACT_USER_CONTENT_URI, values);
				}
			}
			
			values.clear();
		}
	}
	
	// 保存JSONArray
	private synchronized void insertIntoMessageGroupTable(JSONArray mMessageArray) {
		
		if (mMessageArray != null && mMessageArray.size() > 0) {
			
			boolean interrupt = false;
			// 此时，插入一条断点, 此时说明从服务器端拉了最多的30条数据，说明服务器端可能存在更多的消息数量，所以需要插入一个断点
			if (mMessageArray.size() == MAX_PULL_NUM) {
				interrupt = true;
			}
			
			long sendId = 0;
			Cursor cursor = getContentResolver().query(GeneralContentProvider.COOKIE_CONTENT_URI, null, null, null,
					null);
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
			Cursor cursorTotal = this.getContentResolver().query(GeneralContentProvider.MESSAGE_GROUP_CONTENT_URI, null, 
					MessageGroupTable.COLUMN_MESSAGE_GROUP_ID + " = ?", 
					new String[] {groupId}, MessageGroupTable.COLUMN_MESSAGE_ID + " asc");
			
			ContentValues cValues = new ContentValues();
			
			if (cursorTotal != null && cursorTotal.moveToLast()) {
				
				do {
					long mid = cursorTotal.getLong(cursorTotal.getColumnIndex(MessageGroupTable.COLUMN_MESSAGE_ID));
					
					// 这里涉及到一个问题，数据库的查询和更新谁快
					cValues.put(MessageGroupTable.COLUMN_INTERRUPT, 0);
					
					this.getContentResolver().update(GeneralContentProvider.MESSAGE_GROUP_CONTENT_URI, cValues, 
							MessageGroupTable.COLUMN_MESSAGE_ID + " = ?", new String[] {String.valueOf(mid)});
					
				} while (cursorTotal.moveToPrevious() && 
						(cursorTotal.getLong(cursorTotal.getColumnIndex(MessageGroupTable.COLUMN_MESSAGE_ID)) >= midStart));
				cValues.clear();
				cursorTotal.close();
			}

			for (int i = mMessageArray.size() - 1; i >= 0; i--) {
				
				JSONObject jsonObject = mMessageArray.getJSONObject(i);
				// Mid递增顺序--可以作为排序字段，且永远不为null
				values.put(MessageGroupTable.COLUMN_MESSAGE_ID, jsonObject.getLong(GlobalConstants.JSON_MID));
				values.put(MessageGroupTable.COLUMN_MESSAGE_GROUP_ID, jsonObject.getLong(GlobalConstants.JSON_GID));
				
				if (jsonObject.getString(GlobalConstants.JSON_SENDER) != null) {
					values.put(MessageGroupTable.COLUMN_MESSAGE_FROM_USER_ID, jsonObject.getString(GlobalConstants.JSON_SENDER));
				} else {
					values.put(MessageGroupTable.COLUMN_MESSAGE_FROM_USER_ID, sendId);
				}
				
				if (jsonObject.getString(GlobalConstants.JSON_RECEIVER) != null) {
					values.put(MessageGroupTable.COLUMN_MESSAGE_TO_USER_ID, jsonObject.getString(GlobalConstants.JSON_RECEIVER));
				}
				
				values.put(MessageGroupTable.COLUMN_MESSAGE_TYPE, jsonObject.getInteger(GlobalConstants.JSON_TYPE));
				values.put(MessageGroupTable.COLUMN_MESSAGE_DISPLAY_TYPE, ImUtils.matchDisplayGroupType(jsonObject.getInteger(GlobalConstants.JSON_TYPE)));
				
				if (jsonObject.getString(GlobalConstants.JSON_BODY) != null) {
					// 此时返回的Json中含有body
					values.put(MessageGroupTable.COLUMN_MESSAGE_OVERVIEW, jsonObject.getString(GlobalConstants.JSON_BODY));
				} else {
					// 代表发送text--返回的Json中不含body
					values.put(MessageGroupTable.COLUMN_MESSAGE_OVERVIEW, textMessage);
				}
				// 保存当前消息的插入时间--可能以后需要根据时间来排序聊天信息
				// values.put(MessageGroupTable.COLUMN_CREATED, System.currentTimeMillis());
				values.put(MessageGroupTable.COLUMN_CREATED, jsonObject.getLong(GlobalConstants.JSON_CREATETIME));
				
				if (jsonObject.getInteger(GlobalConstants.JSON_READSTATUS) != null) {
					values.put(MessageGroupTable.COLUMN_MESSAGE_READ_STATUS, jsonObject.getInteger(GlobalConstants.JSON_READSTATUS));
				}
				
				if (jsonObject.getLong(GlobalConstants.JSON_SENDER) == uid) {
					values.put(MessageGroupTable.COLUMN_MESSAGE_STATUS, 1);
				}
				
				if (jsonObject.getLong(GlobalConstants.JSON_READTIME) != null) {
					values.put(MessageGroupTable.COLUMN_READTIME, jsonObject.getLong(GlobalConstants.JSON_READTIME));
				}
				
				
				if (i == mMessageArray.size() - 1 && interrupt) {
					// 此时，插入的是最小的mid消息
					values.put(MessageGroupTable.COLUMN_INTERRUPT, 1);
					// 插入数据库
					getContentResolver().insert(GeneralContentProvider.MESSAGE_GROUP_CONTENT_URI, values);
					mTotalMessage++;
				} else {
					// 插入数据库
					getContentResolver().insert(GeneralContentProvider.MESSAGE_GROUP_CONTENT_URI, values);
					mTotalMessage++;
				}
				// 此时已经的得到用户的uid和mobile信息，可以将用户的数据存入用户的UserInfo表中
				values.clear();
				
			}
			
			getSupportLoaderManager().restartLoader(0, null, this);
		}
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.menu_bar_right:
			Intent intent = new Intent();
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.putExtra(GlobalConstants.GID, groupId);
			intent.setClass(this, GroupSettingActivity.class);
			startActivityForResult(intent, 110);
			
			break;
		case R.id.popup_rechoose:
			isClickModify = true;
			mPopupMainMenu.setVisibility(View.GONE);
			mPopupRechooseMenu.setVisibility(View.VISIBLE);
			Animation rigthout = AnimationUtils.loadAnimation(this, R.anim.anim_right_out);
			Animation rightin = AnimationUtils.loadAnimation(this, R.anim.anim_right_in);
			mPopupMainMenu.startAnimation(rigthout);
			mPopupRechooseMenu.startAnimation(rightin);
			
			break;
		case R.id.popup_reedit:

			isClickModify = true;
			mNickName.setEnabled(true);
			mNickName.setCursorVisible(true);
			mNickName.requestFocus();
			mNickName.setSelection(mNickName.getText().toString().length());
			
			mNickName.addTextChangedListener(new TextWatcher() {
				
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
					
				}
				
				@Override
				public void beforeTextChanged(CharSequence s, int start, int count,
						int after) {
					
				}
				
				@Override
				public void afterTextChanged(Editable s) {
					
				}
			});
			
			mNickName.setOnEditorActionListener(new OnEditorActionListener() {  
				@Override
				public boolean onEditorAction(TextView v, int actionId,
						KeyEvent event) {
					
					  /*判断是否是“GO”键*/  
	                if (actionId == EditorInfo.IME_ACTION_DONE) { 
	                	
	    				mNickName.setEnabled(false);
	        			mGeneralEventLogic.setBasicInfo("", "", "", mNickName.getText().toString(), -1, -1, "", -1, "", "",-1);
	                    return true;  
	                }
	                
					return false;
				}  
	        });  
			
			// 显示或者隐藏输入法
			imm = (InputMethodManager)  getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
			break;
		case R.id.popup_go_on:
			if(mPopupWindow.isShowing()){
				mPopupWindow.dismiss();
			}
			
			
			break;
		case R.id.popup_base_cancel:
			Animation leftout = AnimationUtils.loadAnimation(this, R.anim.anim_left_out);
			Animation leftin = AnimationUtils.loadAnimation(this, R.anim.anim_left_in);
			mPopupMainMenu.startAnimation(leftin);
			mPopupRechooseMenu.startAnimation(leftout);
			
			mPopupMainMenu.setVisibility(View.VISIBLE);
			mPopupRechooseMenu.setVisibility(View.GONE);
			break;
		case R.id.popup_base_takephoto:
			Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			// Ensure that there's a camera activity to handle the intent
			if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
				// Create the File where the photo should go
				File photoFile = null;
				try {
					photoFile = createImageFile();
				} catch (IOException ex) {
					// Error occurred while creating the File
				}
				
				// Continue only if the File was successfully created
				if (photoFile != null) {
					takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
					startActivityForResult(takePictureIntent, GlobalConstants.CAMERA_CAPTURE);
				}
			}
			break;

		case R.id.popup_base_imgs:
			intent = new Intent(this, ImagePickerActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivityForResult(intent,103);
			break;

		case R.id.popup_base_avatar:
			startActivityForResult(new Intent(this,RegisterBaseInfoAvatarActivity.class), 101);
			break;
		case R.id.chat_send_btn:
			// 发送信息
			sendChatMessage();
			break;
		case R.id.tv_real_profile_card:
			// 发送个人名片
			// sendProfileCard();
			break;
		case R.id.tv_position_card:
			// 发送职位信息
			// sendPositionCard();
			break;
		case R.id.tv_interview_card:
			
			break;
		default:
			break;
		}
	}
	// 文件名
	private String mCurrentPhotoPath;
	
	private File createImageFile() throws IOException {
		// Create an image file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
		String imageFileName = "JPEG_" + timeStamp + "_";
		File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
		File image = File.createTempFile(imageFileName, ".jpg", storageDir);
		
		mCurrentPhotoPath = image.getAbsolutePath();
		return image;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);
			if (resultCode == RESULT_OK) {
				if (requestCode == 101) {
					// 101 选择虚拟头像
					mCurrentChooseAvatar = data.getStringExtra(CHOOSE_AVATAR);
					if (mCurrentChooseAvatar != null) {
						mListener = ImageLoader.getImageListener(mAvatar,R.drawable.general_user_avatar, R.drawable.general_user_avatar);
						mImageLoader.get(mCurrentChooseAvatar, mListener);
						mGeneralEventLogic.setBasicInfo("",  mCurrentChooseAvatar , "", "", -1, -1, "", -1, "", "",-1);
					}
					
				} else if (requestCode == GlobalConstants.CAMERA_CAPTURE) {
					Logcat.d("@@@", "requestCode : " + requestCode);
					
					Intent intent = new Intent(ChatGroupActivity.this,ImageClipActivity.class);
					intent.putExtra(GlobalConstants.CLIP_PHOTO_URL, mCurrentPhotoPath);
					startActivityForResult(intent, 104);
					
				}else if(requestCode == 103){
					Bundle bundle = data.getExtras();
					Intent intent = new Intent(this, ImageClipActivity.class);
					String clipUrl = data.getStringExtra(GlobalConstants.CLIP_PHOTO_URL);
					if(StringUtils.isNotEmpty(clipUrl)){
						intent.putExtra(GlobalConstants.CLIP_PHOTO_URL, clipUrl);
					}
					intent.putExtra(GlobalConstants.CLIP_PHOTO, bundle);
					startActivityForResult(intent, 104);
				}else if(requestCode == 104){
					setIntent(data);
					processExtraData();
				}else if(requestCode == 110){
					setResult(210);
					AppActivityManager.getInstance().finishActivity(ChatGroupActivity.this);
				}
			}
	}
	/**
	 * 处理额外数据
	 */
	private void processExtraData() {
		String filePah = getIntent().getStringExtra("bitmap_path");
		if(StringUtils.isEmpty(filePah))
			return;
		BufferedInputStream in = null;
		ByteArrayOutputStream out = null;
		try {
			in = new BufferedInputStream(new FileInputStream(filePah));
			out = new ByteArrayOutputStream(1024);    
		       
	        byte[] temp = new byte[1024];        
	        int size = 0;        
	        while ((size = in.read(temp)) != -1) {        
	            out.write(temp, 0, size);        
	        }        
			in.close();
			
			byte[] bis = out.toByteArray();  
			Bitmap bitmap = BitmapFactory.decodeByteArray(bis, 0, bis.length);

			if (bitmap != null) {
				// 在这里将选择的图片传到服务器上
				mGeneralEventLogic.uploadAvatar(filePah);
				mAvatar.setImageBitmap(bitmap);
			}
			
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	    
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * 下拉刷新更新数据
	 */
	private void updateData() {
		isPullToRefresh = true;
		messageCountBeforeRefresh = mChatList.getCount();
		
		Log.i(TAG, "messageCountBeforeRefresh : " + messageCountBeforeRefresh);
		// 下拉刷新
		if (minmsgid == 0) {
			long totalMessageInDatabase = 0;
			
			Cursor cursorTotal = this.getContentResolver().query(GeneralContentProvider.MESSAGE_GROUP_CONTENT_URI, null, 
					MessageGroupTable.COLUMN_MESSAGE_GROUP_ID + " = ?", 
					new String[] {groupId}, MessageGroupTable.COLUMN_MESSAGE_ID + " asc");
			
			if (cursorTotal != null && cursorTotal.moveToFirst()) {
				totalMessageInDatabase = cursorTotal.getCount();
				long mid = cursorTotal.getLong(cursorTotal.getColumnIndex(MessageGroupTable.COLUMN_MESSAGE_ID));
				ContentValues values = new ContentValues();
				values.put(MessageGroupTable.COLUMN_INTERRUPT, 1);
				
				getContentResolver().update(GeneralContentProvider.MESSAGE_GROUP_CONTENT_URI, values, 
						MessageGroupTable.COLUMN_MESSAGE_GROUP_ID + " = ?" + " AND " + MessageGroupTable.COLUMN_MESSAGE_ID + " = ?", 
						new String[] {groupId, String.valueOf(mid)});
				
				values.clear();
				cursorTotal.close();
			}
			
			// 不从网络拉数据, 直接从数据库里读, 此时，由于数据库中存在断点，所以，需要判断，取的这20条数据中，是否存在断点
			long tempMessage;
			if (totalMessageInDatabase > MAX_PULL_DATABASE_NUM) {
				tempMessage = mTotalMessage;
			} else {
				tempMessage = totalMessageInDatabase;
			}
			
			long futureMessage = tempMessage + MAX_PULL_DATABASE_NUM;
			
			// 统计数据库中存在的数量
			long inDatabase = 0;
			// 是否存在断点
			boolean existInterrupt = false;
			// 获得断点处的mid
			long interruptMid = 0;
			
			Cursor cursorInterrupt;
			if (totalMessageInDatabase > MAX_PULL_DATABASE_NUM) {
				
				if (totalMessageInDatabase > futureMessage) {
					cursorInterrupt = this.getContentResolver().query(GeneralContentProvider.MESSAGE_GROUP_CONTENT_URI, null, 
							MessageGroupTable.COLUMN_MESSAGE_GROUP_ID + " = ?", 
							new String[] { groupId }, MessageGroupTable.COLUMN_MESSAGE_ID + " desc limit " + futureMessage + " offset " + (totalMessageInDatabase - futureMessage));
				} else {
					cursorInterrupt = this.getContentResolver().query(GeneralContentProvider.MESSAGE_GROUP_CONTENT_URI, null, 
							MessageGroupTable.COLUMN_MESSAGE_GROUP_ID + " = ?", 
							new String[] { groupId }, MessageGroupTable.COLUMN_MESSAGE_ID + " desc limit " + totalMessageInDatabase);
				}
			} else {
				cursorInterrupt = this.getContentResolver().query(GeneralContentProvider.MESSAGE_GROUP_CONTENT_URI, null, 
						MessageGroupTable.COLUMN_MESSAGE_GROUP_ID + " = ?", 
						new String[] { groupId }, MessageGroupTable.COLUMN_MESSAGE_ID + " desc");
			}
			
			if (cursorInterrupt != null && cursorInterrupt.moveToFirst()) {
				
				do {
					// 1表示是断点
					if (cursorInterrupt.getInt(cursorInterrupt.getColumnIndex(MessageGroupTable.COLUMN_INTERRUPT)) == 1) {
						// 获得断点处的mid
						existInterrupt = true;
						interruptMid = cursorInterrupt.getInt(cursorInterrupt.getColumnIndex(MessageGroupTable.COLUMN_MESSAGE_ID));
						
						ContentValues values = new ContentValues();
						values.put(MessageGroupTable.COLUMN_INTERRUPT, 0);
						// 同时，将此处的断点清空
						getContentResolver().update(GeneralContentProvider.MESSAGE_GROUP_CONTENT_URI, values, 
								MessageGroupTable.COLUMN_MESSAGE_GROUP_ID + " = ?" + " AND " + MessageGroupTable.COLUMN_MESSAGE_ID + " = ?", 
								new String[] {groupId, String.valueOf(interruptMid)});
						
						values.clear();
						break;
					} else {
						inDatabase++;
					}
				} while (cursorInterrupt.moveToNext());
				
				cursorInterrupt.close();
			}
			
			if (!existInterrupt) {
				mTotalMessage += MAX_PULL_DATABASE_NUM;
				getSupportLoaderManager().restartLoader(0, null, this);
			} else {
				// 说明，此时存在断点，此时，先将断点以下(即mid > 该断点的mid)的部分执行restartLoader
				// 之后，执行网络请求得到old chat message
				
				if (mTotalMessage < totalMessageInDatabase) {
					if ((totalMessageInDatabase - MAX_PULL_DATABASE_NUM) > MAX_PULL_DATABASE_NUM) {
						mTotalMessage += MAX_PULL_DATABASE_NUM;
					} else {
						mTotalMessage += (totalMessageInDatabase - MAX_PULL_DATABASE_NUM);
					}
					getSupportLoaderManager().restartLoader(0, null, this);
				} else {
					// 此时，从网络拉数据
					mGeneralEventLogic.getOldGroupChatMessages(groupId, String.valueOf(interruptMid));
				}
			}
		} else {
			// 此时，从网络拉数据
			mGeneralEventLogic.getOldGroupChatMessages(groupId, String.valueOf(minmsgid));
		}
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public void onResendClick(String gid, String message, String cmid) {
		mGeneralEventLogic.sendGroupTextMessage(gid, message, cmid);
		textMessage = message;
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		
		if(StringUtils.isNotEmpty(groupId)){
			ImDbManager.getInstance().cleanMessageListOtherGroupCount(Long.valueOf(groupId));
		}
		// static变量释放引用
		groupId = null;
		
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setClass(this, MainRecruitActivity.class);
		intent.putExtra(GlobalConstants.PAGE, 1);
		startActivity(intent);
	}
	
	/**
	 * 重写onDestroy
	 */
	@Override
	protected void onDestroy() {
		if (groupId != null) {
			ImDbManager.getInstance().cleanMessageListOtherGroupCount(Long.valueOf(groupId));
		}
		// static变量释放引用
		groupId = null;
		
		super.onDestroy();
	}
	
	/**
	 * EventBus回调
	 * @param event
	 */
	public void onEvent(GroupMessageEvent event) {
		Message msg = Message.obtain();
		Log.i(TAG, "msg what : " + msg.what);
		msg.what = event.getWhat();
		mHandler.sendMessage(msg);
	}

}
