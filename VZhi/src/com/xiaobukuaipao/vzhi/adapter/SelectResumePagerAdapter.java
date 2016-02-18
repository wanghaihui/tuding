package com.xiaobukuaipao.vzhi.adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import com.xiaobukuaipao.vzhi.R;
import com.xiaobukuaipao.vzhi.domain.JobInfo;
import com.xiaobukuaipao.vzhi.fragment.AllCandidateFragment;
import com.xiaobukuaipao.vzhi.fragment.ContactedCandidateFragment;
import com.xiaobukuaipao.vzhi.fragment.UndisposedCandidateFragment;
import com.xiaobukuaipao.vzhi.widget.ViewPager;

public class SelectResumePagerAdapter extends FragmentStatePagerAdapter {

	private static final String TAG = SelectResumePagerAdapter.class.getSimpleName();
	
	private List<Integer> mCountList;
	
	// 全部在招职位
	private ArrayList<JobInfo> mOpenedPositions;
	
	private Integer actualPosition;
	
	private int TAB_SIZE = 3;
	private Activity activity;
	private ViewPager viewPager;
	
	public void setActivity(Activity activity) {
		this.activity = activity;
	}
	
	public void setViewPager(ViewPager viewPager) {
		this.viewPager = viewPager;
	}
	
	public List<Integer> getRealCount() {
		if (mCountList != null) {
			return mCountList;
		} else {
			mCountList = new ArrayList<Integer>();
			return mCountList;
		}
	}
	
	private final int[] titleIds = {R.string.undisposed, R.string.all_can, R.string.contact_can};

	@Override
	public CharSequence getPageTitle(int position) {
		//给每个tabstrip赋值，计算剩余数量
		return activity.getResources().getString(titleIds[position], mCountList.get(position));
	}
	
	public SelectResumePagerAdapter(FragmentManager fm) {
		super(fm);
	}
	
	public SelectResumePagerAdapter(FragmentManager fm, List<Integer> mCountList, ArrayList<JobInfo> mOpenedPositions, int actualPosition) {
		this(fm);
		
		this.mCountList = mCountList;
		this.mOpenedPositions = mOpenedPositions;
		this.actualPosition = actualPosition;
	}
	
	public int getItemPosition(Object object) {
	    return POSITION_NONE;
	}

	@Override
	public Fragment getItem(int position) {
		switch (position) {
			case 0:
				// 未处理的候选人
				Log.i(TAG, "PagerAdapter : "  + this.actualPosition);
				return new UndisposedCandidateFragment(actualPosition, mOpenedPositions);
			case 1:
				return new AllCandidateFragment(actualPosition, mOpenedPositions);
			case 2:
				return new ContactedCandidateFragment(actualPosition, mOpenedPositions);
			default:
				return null;
		}
	}

	@Override
	public int getCount() {
		return TAB_SIZE;
	}

}
