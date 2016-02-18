package com.xiaobukuaipao.vzhi.adapter;

import java.util.List;

import android.content.Context;

import com.xiaobukuaipao.vzhi.R;

public class CompanyBenefitsAdapter extends CommonAdapter<String> {

	public CompanyBenefitsAdapter(Context mContext, List<String> mDatas, int mItemLayoutId) {
		super(mContext, mDatas, mItemLayoutId);
		
	}

	@Override
	public void convert(final ViewHolder viewHolder, final String item, final int position) {
		viewHolder.setText(R.id.benefit_text, item);
	}

}
