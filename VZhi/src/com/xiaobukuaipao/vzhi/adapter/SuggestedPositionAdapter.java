package com.xiaobukuaipao.vzhi.adapter;

import java.util.List;

import android.content.Context;

import com.xiaobukuaipao.vzhi.R;
import com.xiaobukuaipao.vzhi.domain.KeyWords;

public class SuggestedPositionAdapter extends CommonAdapter<KeyWords> {

	public SuggestedPositionAdapter(Context mContext, List<KeyWords> mDatas, int mItemLayoutId) {
		super(mContext, mDatas, mItemLayoutId);
		
	}

	@Override
	public void convert(ViewHolder viewHolder, KeyWords item, int position) {
		viewHolder.setText(R.id.content, item.getName());
	}
	
}
