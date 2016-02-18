package com.xiaobukuaipao.vzhi.adapter;

import java.util.List;

import android.content.Context;

import com.xiaobukuaipao.vzhi.R;

public class SingleLineStringAdapter extends CommonAdapter<String> {

	public SingleLineStringAdapter(Context mContext, List<String> mDatas,
			int mItemLayoutId) {
		super(mContext, mDatas, mItemLayoutId);
	}
	
	@Override
	public int getCount() {
		return 3;
	}
	
	@Override
	public void convert(final ViewHolder viewHolder, final String item, final int position) {
		if (item != null) {
			viewHolder.setText(R.id.string_text, item);
		}
	}

}
