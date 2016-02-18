package com.xiaobukuaipao.vzhi.adapter;

import java.util.List;

import android.content.Context;

import com.xiaobukuaipao.vzhi.R;

public class CommonSkillsAdapter extends CommonAdapter<String> {

	public CommonSkillsAdapter(Context mContext, List<String> mDatas, int mItemLayoutId) {
		super(mContext, mDatas, mItemLayoutId);
	}

	@Override
	public void convert(final ViewHolder viewHolder, final String item, final int position) {
		viewHolder.setText(R.id.job_skill, item);
	}

}
