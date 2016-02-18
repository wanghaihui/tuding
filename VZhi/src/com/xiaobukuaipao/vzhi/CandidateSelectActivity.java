package com.xiaobukuaipao.vzhi;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.FrameLayout;

import com.alibaba.fastjson.JSONObject;
import com.xiaobukuaipao.vzhi.adapter.SelectResumePagerAdapter;
import com.xiaobukuaipao.vzhi.domain.JobInfo;
import com.xiaobukuaipao.vzhi.event.CandidateEvent;
import com.xiaobukuaipao.vzhi.event.InfoResult;
import com.xiaobukuaipao.vzhi.fragment.BaseFragment.OnItemSelectedListener;
import com.xiaobukuaipao.vzhi.fragment.BrowserCandidateFragment.BrowserCandidateListener;
import com.xiaobukuaipao.vzhi.fragment.UndisposedCandidateFragment.UndisposedCandidateListener;
import com.xiaobukuaipao.vzhi.util.GlobalConstants;
import com.xiaobukuaipao.vzhi.util.Logcat;
import com.xiaobukuaipao.vzhi.widget.PagerSlidingTabStrip;
import com.xiaobukuaipao.vzhi.widget.ViewPager;
import com.xiaobukuaipao.vzhi.wrap.PositionWrapActivity;

import de.greenrobot.event.EventBus;

/**
 * 候选人接收箱
 * 处理步骤：
 * 1.先得到我发布的职位列表页里的所有有Intent传过来的数据
 * 2.将得到的开启职位列表和position传到BrowserCandidateFragment中
 * 3.职位列表的单选问题
 * 4.未处理,全部,已联系三个数据的更新--PagerSlidingTabStrip.notifyDataSetChanged
 * 5.BrowserCandidateListener接口返回的JobInfo数据，传到SelectResumePagerAdapter中，然后在SelectResumePagerAdapter构建ViewPager中嵌套的三个Fragment
 * 6.UndisposedCandidateFragment数据的填充
 * 7.
 * @author haihui.wang
 */

public class CandidateSelectActivity extends PositionWrapActivity implements OnItemSelectedListener, BrowserCandidateListener, UndisposedCandidateListener{
	
	/** 
     * PagerSlidingTabStrip的实例 
     */  
    private PagerSlidingTabStrip tabs;  
    
    /** 
     * 获取当前屏幕的密度 
     */  
    private DisplayMetrics dm;
    
    // 当前Job Position的未处理的数量，全部的数量和已联系的数量
    private List<Integer> mCountList;
    
    private SelectResumePagerAdapter mBrowserPagerAdapter;
    
    private int undisposed = 0;
    private int all = 0;
    private int contact = 0;
    
    private ViewPager pager;
    
    // 基本的变量都改为私有的
    // private SlidingPaneLayout mSlidingLayout;
    
    // 第一次的position
    private int jobPosition;
    // 传过来的整个的我发布的职位列表，包含开启的和关闭的
    private ArrayList<JobInfo> mTotalJobPositions;
    // position--传进来的position在开启的职位列表中的实际位置
    private Integer actualPosition;
    // 筛选出当前开启的职位列表
    private ArrayList<JobInfo> mOpenedPositions;
    
    public static Handler mHandler = new Handler() {
    	/*public void handleMessage(Message msg) {
    		if (mBrowserPagerAdapter != null) {
    			mDatas = mBrowserPagerAdapter.getRealCount();
    		}
    		switch (msg.what) {
				default:
					break;
    		}
    	}*/
    };
	
	private void getIntentDatas() {
		jobPosition = getIntent().getIntExtra("current_position", 0);
		mTotalJobPositions = getIntent().getParcelableArrayListExtra("total_published_job_list");

		// 进行筛选
		mOpenedPositions = new ArrayList<JobInfo>();
		// 全部
		mOpenedPositions.add(new JobInfo());
		
		if (mTotalJobPositions != null) {
			for(int i=0; i < mTotalJobPositions.size(); i++) {
				if (mTotalJobPositions.get(i).getStatus() == 1) {
					// 表明此职位是开启的
					mOpenedPositions.add(mTotalJobPositions.get(i));
				}
			}
			
			for(int i=0; i < mOpenedPositions.size(); i++) {
				if (mOpenedPositions.get(i).getId().equals(mTotalJobPositions.get(jobPosition).getId())) {
					actualPosition = i;
					break;
				}
			}
		} else {
			actualPosition = 0;
		}
	}
	
	@Override
	public void initUIAndData() {
		
		setContentView(R.layout.activity_select_candidate);

		setHeaderMenuByLeft(this);
		setHeaderMenuByCenterTitle(R.string.candidate_storage);
		
		getIntentDatas();
		
		dm = getResources().getDisplayMetrics();
		// 得到整个的SlidingPaneLayout
		// mSlidingLayout = (SlidingPaneLayout) findViewById(R.id.sliding_pane_layout);
		// 添加左侧导航栏
		// getSupportFragmentManager().beginTransaction().add(R.id.sliding_pane, new BrowserCandidateFragment(actualPosition, mOpenedPositions), "browser").commit();
		// ViewPager
        pager = (ViewPager) findViewById(R.id.pager);  
        pager.setOffscreenPageLimit(3);
        
        tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);  
        
        mCountList = new ArrayList<Integer>();
        mCountList.add(undisposed);
        mCountList.add(all);
        mCountList.add(contact);
        
        // 设置Adapter
        mBrowserPagerAdapter = new SelectResumePagerAdapter(getSupportFragmentManager(), mCountList, mOpenedPositions, actualPosition);
        mBrowserPagerAdapter.setActivity(CandidateSelectActivity.this);
        mBrowserPagerAdapter.setViewPager(pager);
        pager.setAdapter(mBrowserPagerAdapter);
        //pager.setSlidingPaneLayout(mSlidingLayout);
        
        tabs.setViewPager(pager);
        //tabs.setSlidingPaneLayout(mSlidingLayout);
        setTabsValue();
        
        // 执行网络请求--候选人接受箱统计信息
        // 正确路径
        Log.i(TAG, "actual position : " + actualPosition);
        Log.i(TAG, "job id : " + mOpenedPositions.get(actualPosition).getId());
        mPositionEventLogic.statisticsCandidate(mOpenedPositions.get(actualPosition).getId());
        
        setUIListeners();
	}
	
	/**
	 * ActionBar 左侧菜单显示
	 */
	public void setHeaderMenuByLeft(final Activity activity) {
		FrameLayout frameLayout = (FrameLayout) findViewById(R.id.menu_bar_back_btn_layout);
		frameLayout.setVisibility(View.VISIBLE);
		frameLayout.setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						backActivity();
						activity.onBackPressed();
					}
				});
	}
	
	@Override
	public void onBackPressed() {
		if (getIntent().getBooleanExtra("published_position", false)) {
			Intent intent = new Intent();
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setClass(this, PublishedPositionsActivity.class);
			startActivity(intent);
		} else {
			super.onBackPressed();
		}
	}
	
	private void setUIListeners() {
		// SlidingPaneLayout 控制ViewPager
		/*mSlidingLayout.setPanelSlideListener(new PanelSlideListener() {
			@Override
			public void onPanelSlide(View panel, float slideOffset) {
				
			}
			@Override
			public void onPanelOpened(View panel) {
				pager.setSlidingPanelOpening(true);
				pager.requestDisallowInterceptTouchEvent(true);
			}
			
			@Override
			public void onPanelClosed(View panel) {
				pager.setSlidingPanelOpening(false);
				pager.requestDisallowInterceptTouchEvent(false);
			}
		});*/
	}
	
	/**
	 * 对PagerSlidingTabStrip的各项属性进行赋值。
	 */
	private void setTabsValue() {
		// 设置Tab是自动填充满屏幕的
		tabs.setShouldExpand(true);
		// 设置Tab的背景色
		tabs.setBackgroundColor(this.getResources().getColor(R.color.general_back_color));
		// 设置Tab的分割线是透明的
		tabs.setDividerColor(Color.TRANSPARENT);
		// 设置Tab底部线的高度
		tabs.setUnderlineHeight((int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, 1, dm));
		// 设置Tab Indicator的高度
		tabs.setIndicatorHeight((int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, 2, dm));
		// 设置Tab标题文字的大小
		tabs.setTextSize((int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_SP, 16, dm));
		// 设置Tab Indicator的颜色
		tabs.setIndicatorColor(Color.parseColor("#0099FF"));
		// 设置选中Tab文字的颜色 (这是我自定义的一个方法)
		tabs.setSelectedTextColor(Color.parseColor("#0099FF"));
		// 取消点击Tab时的背景色
		tabs.setTabBackground(0);
	}
	
	/**
	 * 更换职位监听器，实现BrowserCandidateListener接口
	 */
	@Override
	public void onChangePositionListener(JobInfo mJobInfo, Integer position) {
		// 先将SlidingPane隐藏
		// mSlidingLayout.closePane();
		if (mJobInfo != null) {
			mPositionEventLogic.statisticsCandidate(mJobInfo.getId());
			
			actualPosition = position;
			mBrowserPagerAdapter.notifyDataSetChanged();
		}
	}
	
	@Override
	public void onEventMainThread(Message msg) {
		if (msg.obj instanceof InfoResult) {
			InfoResult infoResult = (InfoResult) msg.obj;
			switch (msg.what) {
				// 统计候选人信息
				case R.id.position_statistics_candidate:
					// 此时返回JSON数据
					JSONObject mJSONResult = (JSONObject) JSONObject.parse(infoResult.getResult());
					
					undisposed = mJSONResult.getInteger(GlobalConstants.JSON_UNREADNUM);
					all = mJSONResult.getInteger(GlobalConstants.JSON_ALLNUM);
					contact = mJSONResult.getInteger(GlobalConstants.JSON_CONTACTCOUNT);
					alterTabstrip();
					break;
			}
		}
	}

	private void alterTabstrip() {
		// 更新SelectResumePagerAdapter里面的数据
		mCountList.clear();
		mCountList.add(undisposed);
		mCountList.add(all);
		mCountList.add(contact);
		
		tabs.notifyDataSetChanged();
	}

	@Override
	public void onItemSelected(int position) {
		
	}

	@Override
	public void onChangePositionListener(String jid) {
		// 先将SlidingPane隐藏
		// mSlidingLayout.closePane();
		if (jid != null) {
			mPositionEventLogic.statisticsCandidate(jid);
		}
	}
	
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == Activity.RESULT_OK){
			//刷新三个碎片页面
			CandidateEvent candidateEvent = new CandidateEvent();
			candidateEvent.setContactStatus(data.getIntExtra(GlobalConstants.CONTACT_STATUS, 0));
			candidateEvent.setJid(data.getStringExtra(GlobalConstants.JOB_ID));
			candidateEvent.setUid(data.getStringExtra(GlobalConstants.UID));
			EventBus.getDefault().post(candidateEvent);
			
			if((candidateEvent.getContactStatus() & 16) >> 4 == 1){
				mPositionEventLogic.statisticsCandidate(candidateEvent.getJid());
			}
			// 
			Logcat.d("@@@", "candidate select requset code:" + requestCode);
			Logcat.d("@@@", "candidate select contact status:" + data.getIntExtra(GlobalConstants.CONTACT_STATUS, 0));
		}
	}
}

