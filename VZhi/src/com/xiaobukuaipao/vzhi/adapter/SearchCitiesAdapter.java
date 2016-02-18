package com.xiaobukuaipao.vzhi.adapter;

import java.util.List;

import android.content.Context;

import com.xiaobukuaipao.vzhi.R;

public class SearchCitiesAdapter extends CommonAdapter<String> {
	
	public SearchCitiesAdapter(Context mContext, List<String> mDatas, int mItemLayoutId) {
		super(mContext, mDatas, mItemLayoutId);
	}

	@Override
	public void convert(ViewHolder viewHolder, String item, int position) {
		viewHolder.setText(R.id.content, item);
	}
}
