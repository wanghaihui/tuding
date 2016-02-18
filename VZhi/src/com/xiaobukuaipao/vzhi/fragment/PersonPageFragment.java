package com.xiaobukuaipao.vzhi.fragment;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.android.volley.toolbox.Volley;
import com.umeng.analytics.MobclickAgent;
import com.xiaobukuaipao.vzhi.AppliedPositionsActivity;
import com.xiaobukuaipao.vzhi.ContactsActivity;
import com.xiaobukuaipao.vzhi.GroupActivity;
import com.xiaobukuaipao.vzhi.MainRecruitActivity;
import com.xiaobukuaipao.vzhi.PersonalEditPageActivity;
import com.xiaobukuaipao.vzhi.R;
import com.xiaobukuaipao.vzhi.RecruitServiceActivity;
import com.xiaobukuaipao.vzhi.SettingActivity;
import com.xiaobukuaipao.vzhi.contentprovider.GeneralContentProvider;
import com.xiaobukuaipao.vzhi.database.MessageSummaryTable;
import com.xiaobukuaipao.vzhi.database.UserInfoTable;
import com.xiaobukuaipao.vzhi.domain.user.UserBasic;
import com.xiaobukuaipao.vzhi.event.InfoResult;
import com.xiaobukuaipao.vzhi.event.ProfileEventLogic;
import com.xiaobukuaipao.vzhi.im.IMConstants;
import com.xiaobukuaipao.vzhi.im.ImDbManager;
import com.xiaobukuaipao.vzhi.manager.GeneralDbManager;
import com.xiaobukuaipao.vzhi.util.GlobalConstants;
import com.xiaobukuaipao.vzhi.util.Logcat;
import com.xiaobukuaipao.vzhi.util.StringUtils;
import com.xiaobukuaipao.vzhi.view.RoundedNetworkImageView;

/**
 *	我的信息碎片<br>
 * 信息管理
 * @since 2015年01月05日20:54:07
 */
public class PersonPageFragment extends BaseFragment implements LoaderCallbacks<Cursor>, OnClickListener {

	static final String TAG = PersonPageFragment.class.getSimpleName();
	
	private View view;

	private ProfileEventLogic mProfileEventLogic;
	private RoundedNetworkImageView mAvatar;
	private TextView mAccountName;
	private TextView mAccount;
	
	private TextView mContactTip;
	
	private MainRecruitActivity mainRecruitActivity;

	// 请求队列
	private RequestQueue mQueue;
	private ImageLoader mImageLoader;

	private UserBasic userBasic;
	
	
	public PersonPageFragment() {

	}
	
	public PersonPageFragment(Activity activity) {
		this.activity = activity;
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.activity_main_tab_person, container, false);
		mProfileEventLogic = new ProfileEventLogic();
		mProfileEventLogic.register(this);
		
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		initUIAndData();
	}
		
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.mainRecruitActivity = (MainRecruitActivity) activity;
		
	}
	
	public void initUIAndData() {

		mQueue = Volley.newRequestQueue(this.getActivity());
		// 这里不需要缓存图片
		mImageLoader = new ImageLoader(mQueue, new ImageCache() {

			@Override
			public Bitmap getBitmap(String url) {
				return null;
			}
			
			@Override
			public void putBitmap(String url, Bitmap bitmap) {
			}
			
		});

		mAvatar = (RoundedNetworkImageView) view.findViewById(R.id.my_avatar);
		mAccountName = (TextView) view.findViewById(R.id.my_nickname);
		mAccount = (TextView) view.findViewById(R.id.my_account);
		mContactTip = (TextView) view.findViewById(R.id.main_tab_person_tips);
		
		view.findViewById(R.id.my_base_layout).setOnClickListener(this);
		view.findViewById(R.id.my_contact_layout).setOnClickListener(this);
		view.findViewById(R.id.my_group_layout).setOnClickListener(this);
		view.findViewById(R.id.my_applied_layout).setOnClickListener(this);
		view.findViewById(R.id.my_recruit_layout).setOnClickListener(this);
		view.findViewById(R.id.my_setting_layout).setOnClickListener(this);
		getLoaderManager().initLoader(0, null, this);
		mainRecruitActivity.getContentResolver().registerContentObserver(GeneralContentProvider.USERINFO_CONTENT_URI, true, new PersonObserver(new Handler()));  
		initUserData();
		// 获得当前用户的基本信息
		mProfileEventLogic.getUserBasicInfo();
	}
	@Override
	public void onResume() {
		if (userBasic != null && !StringUtils.isEmpty(userBasic.getRealavatar())) {// 显示真实头像
			mAvatar.setImageUrl(userBasic.getRealavatar(), mImageLoader);
		}
		super.onResume();
	}
	
	
	private final class PersonObserver extends ContentObserver{  
        public PersonObserver(Handler handler) {  
            super(handler);  
        }  
        @Override  
        public void onChange(boolean selfChange) {  
        	initUserData();
        }
		
	}
	
	private void refresh() {
		// 显示头像--先判断是否存在realavatar, 如果存在，则显示; 否则显示nickavatar
		mAvatar.setDefaultImageResId(R.drawable.general_user_avatar);
		if (!StringUtils.isEmpty(userBasic.getRealavatar())) {// 显示真实头像
			mAvatar.setImageUrl(userBasic.getRealavatar(), mImageLoader);
		}
		
		if (!StringUtils.isEmpty(userBasic.getRealname())) {
			mAccountName.setText(userBasic.getRealname());
		} else {
			if (!StringUtils.isEmpty(userBasic.getNickname())) {
				mAccountName.setText(userBasic.getNickname());
			} else {
				
				if (mainRecruitActivity != null) {
					mAccountName.setText(mainRecruitActivity.getResources().getString(R.string.not_add_str));
				}
			}
		}
		mAccount.setText(userBasic.getMobile());
	}  
	
	private void initUserData() {
    	if(userBasic == null){
			userBasic = new UserBasic();
		}
		ContentResolver contentResolver = mainRecruitActivity.getContentResolver();
    	if(contentResolver != null){
			Cursor query = contentResolver.query(GeneralContentProvider.USERINFO_CONTENT_URI, null, null, null, null);
			if (query == null) {
				Log.i(TAG, "query is null");
			}
			if(query != null && query.moveToFirst()){
				userBasic.setNickavatar(query.getString(query.getColumnIndexOrThrow(UserInfoTable.COLUMN_AVATAR)));
				userBasic.setRealavatar(query.getString(query.getColumnIndexOrThrow(UserInfoTable.COLUMN_REALAVATAR)));
				userBasic.setRealname(query.getString(query.getColumnIndexOrThrow(UserInfoTable.COLUMN_NAME)));
				userBasic.setNickname(query.getString(query.getColumnIndexOrThrow(UserInfoTable.COLUMN_NICKNAME)));
				userBasic.setMobile(query.getString(query.getColumnIndexOrThrow(UserInfoTable.COLUMN_MOBILE)));
				query.close();
			}else{
				Logcat.e("@@@", "cursor must not null");
			}
    	}
		refresh();
	}
	
	public void onEventMainThread(Message msg) {
		// 执行网络请求--这里
		if (msg.obj instanceof InfoResult) {
			InfoResult infoResult = (InfoResult) msg.obj;
			switch (msg.what) {
				case R.id.profile_basic_info:
					
					JSONObject jsonObject = (JSONObject) JSONObject.parse(infoResult.getResult());
					if(jsonObject == null){
						return;
					}
					if(jsonObject.getJSONObject(GlobalConstants.JSON_USERBASIC) == null){
						return;
					}
					userBasic = new UserBasic(jsonObject.getJSONObject(GlobalConstants.JSON_USERBASIC));
					GeneralDbManager.getInstance().updateToUserInfoTable(userBasic);
					
					// 保存上次登录的uid
					
					SharedPreferences sp = getActivity().getSharedPreferences("tuding_uid", Context.MODE_PRIVATE);  
			        sp.edit().putLong("uid", Long.valueOf(userBasic.getId())).commit();
			        
					break;
			}
			
		}
	}

	@Override
	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
			case R.id.my_base_layout:
				MobclickAgent.onEvent(getActivity(),"xxdj");
				intent = new Intent(getActivity(), PersonalEditPageActivity.class);
				break;
			case R.id.my_contact_layout:
				intent = new Intent(getActivity(), ContactsActivity.class);
				break;
			case R.id.my_group_layout:
				intent = new Intent(getActivity(), GroupActivity.class);
				break;
			case R.id.my_applied_layout:
				intent = new Intent(getActivity(), AppliedPositionsActivity.class);
				break;
			case R.id.my_recruit_layout:
				intent = new Intent(getActivity(), RecruitServiceActivity.class);
				break;
			case R.id.my_setting_layout:
				intent = new Intent(getActivity(), SettingActivity.class);
				break;
			default:
				break;
		}
		if (intent != null) {
			getActivity().startActivity(intent);
		}
	}
	
	@Override
	public void onDestroy() {
		mProfileEventLogic.unregister(this);
		super.onDestroy();
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		// 按照更新时间排序
		return new CursorLoader(this.getActivity(), GeneralContentProvider.MESSAGE_SUMMARY_CONTENT_URI, 
				null, MessageSummaryTable.COLUMN_MESSAGE_DISPLAY_TYPE + " = ?", new String[] { String.valueOf(IMConstants.DISPLAY_LIST_TYPE_STRANGERLETTER) }, null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		
		if (cursor != null && cursor.moveToFirst()) {
			int unreadCount = ImDbManager.getInstance().getTotalUnreadCount(cursor);
			mainRecruitActivity.onMsgUnread(2, unreadCount);//聊天
			
			if (unreadCount > 0) {
				mContactTip.setVisibility(View.VISIBLE);
			} else {
				mContactTip.setVisibility(View.INVISIBLE);
			}
			
			cursor.moveToFirst();
		}
		
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		
	}
	//////////////////////////////////////////////////////////////////////////////////////////////////////////
}
