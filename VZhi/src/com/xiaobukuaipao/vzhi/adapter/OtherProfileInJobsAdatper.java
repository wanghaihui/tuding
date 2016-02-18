package com.xiaobukuaipao.vzhi.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.xiaobukuaipao.vzhi.R;
import com.xiaobukuaipao.vzhi.domain.JobDetailInfo;
import com.xiaobukuaipao.vzhi.util.GlobalConstants;
import com.xiaobukuaipao.vzhi.util.SpannableKeyWordBuilder;
import com.xiaobukuaipao.vzhi.util.StringUtils;

public class OtherProfileInJobsAdatper extends CommonAdapter<JobDetailInfo>{

	public OtherProfileInJobsAdatper(Context mContext,
			List<JobDetailInfo> mDatas, int mItemLayoutId) {
		super(mContext, mDatas, mItemLayoutId);
	}

	@Override
	public void convert(ViewHolder viewHolder, JobDetailInfo item, int position) {
		TextView jobName = viewHolder.getView(R.id.item_profile_in_job_name);
		jobName.setText(item.getPosition().getString(GlobalConstants.JSON_NAME));
		
		TextView jobBasic = viewHolder.getView(R.id.item_profile_in_job_basic);
		SpannableKeyWordBuilder builder = new SpannableKeyWordBuilder();
		builder.appendKeyWord(item.getSalary());
		builder.append("    ");
		builder.append(item.getExprName());
		builder.append("    ");
		builder.append(item.getCity());
		jobBasic.setText(builder);
		
		TextView jobHightLight = viewHolder.getView(R.id.item_profile_in_job_highlight);
		jobHightLight.setVisibility(View.GONE);
		if(StringUtils.isNotEmpty(item.getHighlights())){
			jobHightLight.setVisibility(View.VISIBLE);
			jobHightLight.setText(mContext.getString(R.string.general_highlights_tips, item.getHighlights()));
		}
		viewHolder.setText(R.id.item_profile_in_job_refresh_time, item.getRefreshTime());
	}

}
