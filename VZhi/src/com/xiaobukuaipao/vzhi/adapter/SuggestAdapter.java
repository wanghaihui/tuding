package com.xiaobukuaipao.vzhi.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;

import com.xiaobukuaipao.vzhi.R;
import com.xiaobukuaipao.vzhi.domain.user.Suggest;

public class SuggestAdapter extends CommonAdapter<Suggest>{

	public SuggestAdapter(Context mContext, List<Suggest> mDatas,
			int mItemLayoutId) {
		super(mContext, mDatas, mItemLayoutId);
	}

	@Override
	public void convert(ViewHolder viewHolder, Suggest item, int position) {
		viewHolder.setText(android.R.id.text1, item.getName());
		viewHolder.getView(R.id.divider).setVisibility( position == getCount() - 1 ? View.INVISIBLE : View.VISIBLE);
	}
}