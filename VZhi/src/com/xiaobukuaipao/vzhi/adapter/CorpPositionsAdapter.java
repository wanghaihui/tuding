package com.xiaobukuaipao.vzhi.adapter;

import java.util.List;

import android.content.Context;
import android.widget.TextView;

import com.xiaobukuaipao.vzhi.R;
import com.xiaobukuaipao.vzhi.domain.JobInfo;
import com.xiaobukuaipao.vzhi.util.GlobalConstants;
import com.xiaobukuaipao.vzhi.util.SpannableKeyWordBuilder;

public class CorpPositionsAdapter extends CommonAdapter<JobInfo>{

	public CorpPositionsAdapter(Context mContext, List<JobInfo> mDatas,
			int mItemLayoutId) {
		super(mContext, mDatas, mItemLayoutId);
	}

	@Override
	public void convert(ViewHolder viewHolder, JobInfo item, int position) {
		TextView title = viewHolder.getView(R.id.position_title);
		title.setText(item.getPosition().getString(GlobalConstants.JSON_NAME));
		
		TextView desc = viewHolder.getView(R.id.position_detail);
		SpannableKeyWordBuilder builder = new SpannableKeyWordBuilder();
		
		builder.appendKeyWord(item.getSalary());
		builder.append("/");
		builder.append(item.getEduName());
		builder.append("/");
		builder.append(item.getExpr().getString(GlobalConstants.JSON_NAME));
		builder.append("/");
		builder.append(item.getCity());
		desc.setText(builder);
		
		TextView time = viewHolder.getView(R.id.position_time);
		time.setText(item.getRefreshtime());
	}
}
