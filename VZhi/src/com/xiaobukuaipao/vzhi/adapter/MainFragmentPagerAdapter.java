package com.xiaobukuaipao.vzhi.adapter;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.xiaobukuaipao.vzhi.fragment.ChatPageFragment;
import com.xiaobukuaipao.vzhi.fragment.ListPageFragment;
import com.xiaobukuaipao.vzhi.fragment.PersonPageFragment;

public class MainFragmentPagerAdapter extends FragmentPagerAdapter {

	private int TAB_SIZE = 3;
	private Activity activity;
	

	public MainFragmentPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	public void setActivity(Activity activity) {
		this.activity = activity;
	}

	public Activity getActivity() {
		return activity;
	}

	// 主要实现这两个方法
	@Override
	public Fragment getItem(int position) {
		switch (position) {
		case 0:
			//TODO  涉及　notification　刷新页面 ?
//			return new RecruitPageFragment(activity);
			return new ListPageFragment(activity);
		case 1:
			return new ChatPageFragment(activity);
		case 2:
			return new PersonPageFragment(activity);
		default:
			return null;
		}
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		// 防止销毁fragment
		super.destroyItem(container, position, object);
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		return super.instantiateItem(container, position);
	}

	@Override
	public int getCount() {
		return TAB_SIZE;
	}
}
