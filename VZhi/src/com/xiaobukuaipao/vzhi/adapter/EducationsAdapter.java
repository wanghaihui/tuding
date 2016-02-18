package com.xiaobukuaipao.vzhi.adapter;

import java.util.List;

import android.content.Context;

import com.xiaobukuaipao.vzhi.R;
import com.xiaobukuaipao.vzhi.domain.user.Education;

public class EducationsAdapter extends CommonAdapter<Education> {

	public EducationsAdapter(Context mContext, List<Education> mDatas,
			int mItemLayoutId) {
		super(mContext, mDatas, mItemLayoutId);
	}

	@Override
	public void convert(final ViewHolder viewHolder, final Education item, final int position) {
		String dot = mContext.getString(R.string.general_tips_dot);
		viewHolder.setText(R.id.edu_date_time, item.getBeginStr() + " - " + (item.getEnd() != null ? item.getEndStr() : mContext.getString(R.string.date_dialog_tonow)));
		viewHolder.setText(R.id.edu_university_major_degree, item.getSchool().getName() + dot + item.getMajor().getName()  + dot +  item.getDegree().getName());
	}

}
