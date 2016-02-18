package com.xiaobukuaipao.vzhi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;
import com.xiaobukuaipao.vzhi.adapter.MainFragmentPagerAdapter;
import com.xiaobukuaipao.vzhi.event.InfoResult;
import com.xiaobukuaipao.vzhi.fragment.BaseFragment.OnItemSelectedListener;
import com.xiaobukuaipao.vzhi.im.IMConstants;
import com.xiaobukuaipao.vzhi.im.ImDbManager;
import com.xiaobukuaipao.vzhi.services.ImService;
import com.xiaobukuaipao.vzhi.util.GlobalConstants;
import com.xiaobukuaipao.vzhi.wrap.RecruitWrapActivity;

/**
 * <p>主页</p>
 * 
 * @since 2015年01月04日11:04:30
 */
public class MainRecruitActivity extends RecruitWrapActivity implements OnPageChangeListener, OnClickListener, OnItemSelectedListener, IMsgUnreadListener {
	
	static final String TAG = MainRecruitActivity.class.getSimpleName();

	/**
	 * 主要显示的滑动页面
	 */
	private ViewPager mViewPager;
	/**
	 * 下方菜单栏,带有点击效果的三个按钮
	 */
	private LinearLayout mTabGroup;
	/**
	 * 进入Pager,跳转到第几页
	 */
	private int curPage = 0;
	/**
	 * 传递消息类型,从下发的消息中获取,再下发到系统<br>
	 * <b>进入消息列表后，应该跳到的详细Activity</b>
	 */
	private int type;

	/**
	 * 消息栏的提示标志
	 */
	private View mTabChatTips;
	/**
	 * 我的信息栏的提示标志
	 */
	private View mTabPersonTips;
	/**
	 * 页面适配器
	 */
	private MainFragmentPagerAdapter mPagerAdapter;
	
	@Override
	public void onPageScrollStateChanged(int arg0) {
		
	}
	
	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		
	}

	@Override
	public void onPageSelected(int position) {
		findViewById(R.id.ivTitleName).setVisibility(View.VISIBLE);
		findViewById(R.id.tabs).setVisibility(View.GONE);
		
		switch (position) {
			case 0:
				MobclickAgent.onEvent(this,"zwlb");
				findViewById(R.id.ivTitleName).setVisibility(View.GONE);
				findViewById(R.id.tabs).setVisibility(View.VISIBLE);
				break;
			case 1:
				MobclickAgent.onEvent(this,"xx");
				setHeaderMenuByCenterTitle(R.string.main_tab_chat);
				break;
			case 2:
				MobclickAgent.onEvent(this,"wo");
				setHeaderMenuByCenterTitle(R.string.main_tab_person);
				break;
			default:
				break;
		}
		
		for (int i = 0; i < mTabGroup.getChildCount(); i++) {
			mTabGroup.getChildAt(i).setEnabled(true);
		}
		
		mTabGroup.getChildAt(position).setEnabled(false);
	}

	@Override
	public void onClick(View v) {
		
		for (int i = 0; i < mTabGroup.getChildCount(); i++) {
			mTabGroup.getChildAt(i).setEnabled(true);
		}
		
		for (int i = 0; i < mTabGroup.getChildCount(); i++) {
			if (mTabGroup.getChildAt(i) == v) {
				mTabGroup.getChildAt(i).setEnabled(false);
				mViewPager.setCurrentItem(i);
			}
		}
		
	}

	@Override
	public void onItemSelected(int position) {
		
	}
	
	@Override
	public void initUIAndData() {
		setContentView(R.layout.activity_main_recruit);
		
		curPage = getIntent().getIntExtra(GlobalConstants.PAGE, curPage);
		type = getIntent().getIntExtra(GlobalConstants.TYPE, -1);
		mTabGroup = (LinearLayout) findViewById(R.id.main_tab_group);
		
		mViewPager = (ViewPager) findViewById(R.id.main_pager);
		
		mTabChatTips = findViewById(R.id.main_tab_chat_tips);
		mTabPersonTips = findViewById(R.id.main_tab_person_tips);
		
		for (int i = 0; i < mTabGroup.getChildCount(); i++) {
			mTabGroup.getChildAt(i).setOnClickListener(this);
		}
		
		mPagerAdapter = new MainFragmentPagerAdapter(getSupportFragmentManager());
		mPagerAdapter.setActivity(this);
		mViewPager.setOffscreenPageLimit(3);
		
		mViewPager.setOnPageChangeListener(this);
		mViewPager.setAdapter(mPagerAdapter);
		mViewPager.setCurrentItem(curPage);
		mTabGroup.getChildAt(curPage).setEnabled(false);
		
		if(curPage == 0){
			findViewById(R.id.ivTitleName).setVisibility(View.GONE);
			findViewById(R.id.tabs).setVisibility(View.VISIBLE);
		}
		
		openDetailByMessageType(type);
		// 先获得百度云推送的userId和channelId
		startPushNotification();
		// 延时两秒
		new Handler().postDelayed(new Runnable() {    
		    public void run() {    
		    	// 此时表示登录成功
				startImService();
		    }    
		 }, 2000);
		
		UmengUpdateAgent.setUpdateOnlyWifi(false);
		UmengUpdateAgent.forceUpdate(this);
//		UmengUpdateAgent.silentUpdate(this);
		UmengUpdateAgent.update(this);
	}


	
	@Override
	public void onEventMainThread(Message msg) {
		if (msg.obj instanceof InfoResult) {
		}
	}
	
	private void startImService() {
		Intent intent = new Intent(MainRecruitActivity.this, ImService.class);
		startService(intent);
	}
	
	private void startPushNotification() {
		PushManager.startWork(getApplicationContext(), PushConstants.LOGIN_TYPE_API_KEY,getMetaValue(MainRecruitActivity.this, "api_key"));
	}
	
	
	// 获取ApiKey
    public static String getMetaValue(Context context, String metaKey) {
        Bundle metaData = null;
        String apiKey = null;
        if (context == null || metaKey == null) {
            return null;
        }
        try {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(context.getPackageName(),PackageManager.GET_META_DATA);
            if (null != ai) {
                metaData = ai.metaData;
            }
            if (null != metaData) {
                apiKey = metaData.getString(metaKey);
            }
        } catch (NameNotFoundException e) {
        	
        }
        return apiKey;
    }
    
	public interface OnEventMainThreadListener {
		void onEventMainThread(Message msg);
	}
	
	/**
	 * 打开接收到消息的详细信息
	 */
	private void openDetailByMessageType(int type) {
		Intent intent = new Intent();
		switch (type) {
			case IMConstants.TYPE_LETTER_GREETING:
				// 打招呼
				intent.setClass(this, ContactsStrangersActivity.class);
				this.startActivity(intent);
				break;
			case IMConstants.TYPE_LETTER_QUESTION:
				// 提问问题
				intent.setClass(this, ContactsStrangersActivity.class);
				this.startActivity(intent);
				break;
			case IMConstants.TYPE_LETTER_ANSWER:
				// 提问被回答
				intent.setClass(this, ContactsStrangersActivity.class);
				this.startActivity(intent);
				break;
			case IMConstants.TYPE_LETTER_BUDDY_INVITATION:
				// 发送好友请求
				intent.setClass(this, ContactsStrangersActivity.class);
				this.startActivity(intent);
				break;
				
			case IMConstants.TYPE_P2P_TEXT:
				intent.setClass(this, ChatPersonActivity.class);
				intent.putExtra("did", getIntent().getLongExtra("did", -1));
				intent.putExtra("dname", getIntent().getStringExtra("dname"));
				intent.putExtra("sender", getIntent().getLongExtra("sender", -1));
				this.startActivity(intent);
				
				// 普通的P2P聊天
				ImDbManager.getInstance().cleanMessageListOtherCount(getIntent().getLongExtra("did", -1));
				break;
			case IMConstants.TYPE_P2P_BUDDY_INVITATION_ACCEPT:
				// 对方接受好友请求, 此时通过Notification, 打开的是与对方的聊天列表, 之后更新消息列表
				ImDbManager.getInstance().cleanMessageListOtherCount(getIntent().getLongExtra("did", -1));
				
				intent.setClass(this, ChatPersonActivity.class);
				intent.putExtra("did", getIntent().getLongExtra("did", -1));
				intent.putExtra("dname", getIntent().getStringExtra("dname"));
				intent.putExtra("sender", getIntent().getLongExtra("sender", -1));
				
				this.startActivity(intent);
				break;
				
			case IMConstants.TYPE_P2P_JOB_APPLY_INTEREST:
				// 对方接受好友请求, 此时通过Notification, 打开的是与对方的聊天列表, 之后更新消息列表
				ImDbManager.getInstance().cleanMessageListOtherCount(getIntent().getLongExtra("did", -1));
				
				intent.setClass(this, ChatPersonActivity.class);
				intent.putExtra("did", getIntent().getLongExtra("did", -1));
				intent.putExtra("dname", getIntent().getStringExtra("dname"));
				intent.putExtra("sender", getIntent().getLongExtra("sender", -1));
				
				this.startActivity(intent);
				break;
				
			case IMConstants.TYPE_LETTER_JOB_RECOMMEND:
				// 职位推荐
				intent.setClass(this, PositionRecActivity.class);
				this.startActivity(intent);
				break;
			case IMConstants.TYPE_LETTER_JOB_APPLY_NOTIFY:
				// 职位投递提醒消息
				intent.setClass(this, CandidateSelectActivity.class);
				intent.putExtra("current_position", 0);
				this.startActivity(intent);
				break;
			case IMConstants.TYPE_LETTER_GROUP_RECOMMEND:
				// 群推荐
				intent.setClass(this, GroupRecActivity.class);
				this.startActivity(intent);
				break;
				
			case IMConstants.TYPE_P2P_JOB_APPLY:
				// 收到投递简历
				ImDbManager.getInstance().cleanMessageListOtherCount(getIntent().getLongExtra("did", -1));
				
				intent.setClass(this, ChatPersonActivity.class);
				intent.putExtra("did", getIntent().getLongExtra("did", -1));
				intent.putExtra("dname", getIntent().getStringExtra("dname"));
				intent.putExtra("sender", getIntent().getLongExtra("sender", -1));
				
				this.startActivity(intent);
				break;
				
			case IMConstants.TYPE_GROUP_TEXT:
				// 群消息
				ImDbManager.getInstance().cleanMessageListOtherGroupCount(getIntent().getLongExtra("gid", -1));
				
				intent.setClass(this, ChatGroupActivity.class);
				intent.putExtra("gid", String.valueOf(getIntent().getLongExtra("gid", -1)));
				
				this.startActivity(intent);
				break;
				
			case IMConstants.TYPE_GROUP_JOIN:
				// 群消息
				ImDbManager.getInstance().cleanMessageListOtherGroupCount(getIntent().getLongExtra("gid", -1));
				
				intent.setClass(this, ChatGroupActivity.class);
				intent.putExtra("gid", String.valueOf(getIntent().getLongExtra("gid", -1)));
				
				this.startActivity(intent);
				break;
				
			case IMConstants.TYPE_P2P_INTERVIEW_INVITATION:
				// 普通的P2P聊天
				ImDbManager.getInstance().cleanMessageListOtherCount(getIntent().getLongExtra("did", -1));
				
				intent.setClass(this, ChatPersonActivity.class);
				intent.putExtra("did", getIntent().getLongExtra("did", -1));
				intent.putExtra("dname", getIntent().getStringExtra("dname"));
				intent.putExtra("sender", getIntent().getLongExtra("sender", -1));
				
				this.startActivity(intent);
				break;
				
			default:
				break;
		}
	}

	@Override
	public void onMsgUnread(int position, int count) {
		switch (position) {
			case 0:
				break;
			case 1:
				if (count > 0) {
					mTabChatTips.setVisibility(View.VISIBLE);
				} else {
					mTabChatTips.setVisibility(View.INVISIBLE);
				}
				break;
			case 2:
				if (count > 0) {
					mTabPersonTips.setVisibility(View.VISIBLE);
				} else {
					mTabPersonTips.setVisibility(View.INVISIBLE);
				}
				break;
			default:
				break;
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	protected void onNewIntent(Intent intent){
	    super.onNewIntent(intent);
	    setIntent(intent);
	    curPage = getIntent().getIntExtra(GlobalConstants.PAGE, curPage);
	    mViewPager.setCurrentItem(curPage, false);
	}
	
	/*long time = 0;
	@Override
	public void onBackPressed() {
		//
		if(System.currentTimeMillis() - time > 2000){
			time = System.currentTimeMillis();
			VToast.show(this, getString(R.string.general_tips_back_one_more));
			return;
		}
		
		AppActivityManager.getInstance().appExit(this);
		// super.onBackPressed();
	}*/
}
