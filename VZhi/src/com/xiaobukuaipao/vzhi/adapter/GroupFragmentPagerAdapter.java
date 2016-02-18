package com.xiaobukuaipao.vzhi.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.xiaobukuaipao.vzhi.fragment.PersonConnectGroupFragment;
import com.xiaobukuaipao.vzhi.fragment.PersonRecruitGroupFragment;

public class GroupFragmentPagerAdapter extends FragmentPagerAdapter {

	private int TAB_SIZE = 2;
	private String[] titles;
	public String[] getTitles() {
		return titles;
	}

	public void setTitles(String[] titles) {
		this.titles = titles;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return titles[position];
	}

	public GroupFragmentPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int position) {
		switch (position) {
		case 0:
			return new PersonRecruitGroupFragment();
		case 1:
			return new PersonConnectGroupFragment();
		default:
			return null;
		}
	}

	@Override
	public int getCount() {
		return TAB_SIZE;
	}

}
