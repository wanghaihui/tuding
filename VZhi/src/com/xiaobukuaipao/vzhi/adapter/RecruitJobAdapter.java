package com.xiaobukuaipao.vzhi.adapter;

import java.util.List;

import android.content.Context;

import com.xiaobukuaipao.vzhi.R;
import com.xiaobukuaipao.vzhi.domain.JobInfo;
import com.xiaobukuaipao.vzhi.util.GlobalConstants;

public class RecruitJobAdapter extends CommonAdapter<JobInfo> {

	public RecruitJobAdapter(Context mContext, List<JobInfo> mDatas,
			int mItemLayoutId) {
		super(mContext, mDatas, mItemLayoutId);
	}

	@Override
	public void convert(final ViewHolder viewHolder,final JobInfo item, final int position) {
		if (item != null) {
			viewHolder.setText(R.id.job_title, item.getPosition().getString(GlobalConstants.JSON_NAME));
			viewHolder.setText(R.id.job_time, item.getRefreshtime());
			viewHolder.setText(R.id.basic_info, item.getSalary() + " / " + item.getExpr() + " / " + item.getCity());
			viewHolder.setText(R.id.high_light, item.getHighlights());
		}
	}

}
