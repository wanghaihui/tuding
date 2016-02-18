package com.xiaobukuaipao.vzhi;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;

import com.xiaobukuaipao.vzhi.domain.JobInfo;
import com.xiaobukuaipao.vzhi.event.CandidateEvent;
import com.xiaobukuaipao.vzhi.fragment.AllCandidateFragment;
import com.xiaobukuaipao.vzhi.fragment.BaseFragment.OnItemSelectedListener;
import com.xiaobukuaipao.vzhi.fragment.ContactedCandidateFragment;
import com.xiaobukuaipao.vzhi.fragment.UndisposedCandidateFragment;
import com.xiaobukuaipao.vzhi.util.DisplayUtil;
import com.xiaobukuaipao.vzhi.util.GlobalConstants;

import de.greenrobot.event.EventBus;

/**
 * 候选人接收箱
 */
public class CandidateActivity extends DesignFragmentActivity implements OnItemSelectedListener, OnClickListener {
	public static final String TAG = CandidateActivity.class.getSimpleName();
	
	private FragmentTransaction transaction;
	private UndisposedCandidateFragment undisposedFragment;
	private AllCandidateFragment allCandidateFragment;
	private ContactedCandidateFragment contactedCandidateFragment;
	
	// 第一次的position
    private int jobPosition;
    // 传过来的整个的我发布的职位列表，包含开启的和关闭的
    private ArrayList<JobInfo> mTotalJobPositions;
    // position--传进来的position在开启的职位列表中的实际位置
    private Integer actualPosition;
    // 筛选出当前开启的职位列表
    private ArrayList<JobInfo> mOpenedPositions;
    
    private int unreadCount;
    
    private PopupWindow popWind;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_candidate);
		
		setHeaderMenuByLeft(this);
		setHeaderMenuByRight(this);
		
		/* ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeAsUpIndicator(R.drawable.btn_return_img_selector);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setDisplayUseLogoEnabled(false); */
		
		getIntentDatas();
		initUIAndData();
	}
	
	/**
	 * 得到上层传来的数据
	 */
	private void getIntentDatas() {
		jobPosition = getIntent().getIntExtra("current_position", 0);
		mTotalJobPositions = getIntent().getParcelableArrayListExtra("total_published_job_list");
		unreadCount = getIntent().getIntExtra("unread_count", 0);

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
			for(int i=0; i < mOpenedPositions.size() && !mTotalJobPositions.isEmpty(); i++) {
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
	
	/**
	 * 设置按钮点击, 弹出PopupWindow
	 */
	public void popupSetting() {
		// 弹出自定义overflow
		popUpOverflow();
	}
	
	public void popUpOverflow() {
		/**
		* 定位PopupWindow，让它恰好显示在Action Bar的下方。 通过设置Gravity，确定PopupWindow的大致位置。
		* 首先获得状态栏的高度，再获取Action bar的高度，这两者相加设置y方向的offset样PopupWindow就显示在action
		* bar的下方了。 通过dp计算出px，就可以在不同密度屏幕统一X方向的offset.但是要注意不要让背景阴影大于所设置的offset，
		 * 否则阴影的宽度为offset.
		*/
		// 获取状态栏高度
		Rect frame = new Rect();
		getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
		// 状态栏高度：frame.top
		int xOffset = frame.top + DisplayUtil.dip2px(this, 48) + 1;//减去阴影宽度，适配UI.
		int yOffset = DisplayUtil.dip2px(this, 5f); //设置x方向offset为5dp
		
		View parentView = getLayoutInflater().inflate(R.layout.activity_candidate, null);
		View popView = getLayoutInflater().inflate(R.layout.action_overflow_popwindow, null);
		// popView即popupWindow的布局，ture设置focusAble
		popWind = new PopupWindow(popView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);

		// 必须设置BackgroundDrawable后setOutsideTouchable(true)才会有效
		// 这里在XML中定义背景，所以这里设置为null;
		popWind.setBackgroundDrawable(new BitmapDrawable(getResources(),  (Bitmap) null));
		popWind.setOutsideTouchable(true); //点击外部关闭。
		popWind.setAnimationStyle(android.R.style.Animation_Dialog);    //设置一个动画。
		// 设置Gravity，让它显示在右上角
		popWind.showAtLocation(parentView, Gravity.RIGHT | Gravity.TOP, yOffset, xOffset);
				
		popView.findViewById(R.id.undispose).setOnClickListener(this);
		popView.findViewById(R.id.all).setOnClickListener(this);
		popView.findViewById(R.id.contacted).setOnClickListener(this);
		
	}
	
	
	/**
	 * 初始化UI界面
	 */
	public void initUIAndData() {
		transaction = getSupportFragmentManager().beginTransaction();
		
		undisposedFragment = new UndisposedCandidateFragment(actualPosition, mOpenedPositions);
		allCandidateFragment = new AllCandidateFragment(actualPosition, mOpenedPositions);
		contactedCandidateFragment = new ContactedCandidateFragment(actualPosition, mOpenedPositions);
		
		if (unreadCount > 0) {
			setHeaderMenuByCenterTitle(R.string.candidate_storage_undisposed);
			
			transaction.add(R.id.main_pager, undisposedFragment, "undispose");
			transaction.hide(allCandidateFragment);
			transaction.hide(contactedCandidateFragment);
			transaction.show(undisposedFragment);
			
		} else {
			setHeaderMenuByCenterTitle(R.string.candidate_storage_all);
			
			transaction.add(R.id.main_pager, allCandidateFragment, "all");
			transaction.hide(undisposedFragment);
			transaction.hide(contactedCandidateFragment);
			transaction.show(allCandidateFragment);
			
		}
		
		transaction.commit();
	}
	
	@Override
	protected void onDestroy() {
		AppActivityManager.getInstance().finishActivity(this);
		super.onDestroy();
	}
	
	@Override
	public void onItemSelected(int position) {
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			
			case R.id.undispose:
				setHeaderMenuByCenterTitle(R.string.candidate_storage_undisposed);
				
				transaction = getSupportFragmentManager().beginTransaction();
				transaction.replace(R.id.main_pager, undisposedFragment, "undispose");
				transaction.hide(allCandidateFragment);
				transaction.hide(contactedCandidateFragment);
				transaction.show(undisposedFragment);
				transaction.commit();
				break;
			case R.id.all:
				setHeaderMenuByCenterTitle(R.string.candidate_storage_all);
				
				transaction = getSupportFragmentManager().beginTransaction();
				transaction.replace(R.id.main_pager, allCandidateFragment, "all");
				transaction.hide(undisposedFragment);
				transaction.hide(contactedCandidateFragment);
				transaction.show(allCandidateFragment);
				transaction.commit();
				break;
			case R.id.contacted:
				setHeaderMenuByCenterTitle(R.string.candidate_storage_contacted);
				
				transaction = getSupportFragmentManager().beginTransaction();
				transaction.replace(R.id.main_pager, contactedCandidateFragment, "contacted");
				transaction.hide(undisposedFragment);
				transaction.hide(allCandidateFragment);
				transaction.show(contactedCandidateFragment);
				transaction.commit();
				break;
		}
		
		if (popWind.isShowing()) popWind.dismiss();
		
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
		}
	}
}
