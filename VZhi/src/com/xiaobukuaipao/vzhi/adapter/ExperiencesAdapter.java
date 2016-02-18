package com.xiaobukuaipao.vzhi.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.xiaobukuaipao.vzhi.R;
import com.xiaobukuaipao.vzhi.domain.user.Experience;
import com.xiaobukuaipao.vzhi.util.StringUtils;

public class ExperiencesAdapter extends CommonAdapter<Experience> {

	public ExperiencesAdapter(Context mContext, List<Experience> mDatas,
			int mItemLayoutId) {
		super(mContext, mDatas, mItemLayoutId);
	}

	@Override
	public void convert(final ViewHolder viewHolder, final Experience item, final int position) {
		if (item != null) {
			viewHolder.setText(R.id.job_expr_date_time, item.getBeginStr() + " - " + (StringUtils.isEmpty(item.getEndStr()) ? mContext.getString(R.string.date_dialog_tonow) : item.getEndStr()));
			
			StringBuilder sb = new StringBuilder();
			
			boolean flag = false;
			if(StringUtils.isNotEmpty(item.getPosition())){
				sb.append(item.getPosition());
				flag = true;
			}
			
			if(StringUtils.isNotEmpty(item.getCorp())){
				if(flag){
					sb.append(mContext.getString(R.string.general_tips_dot));
				}
				sb.append(item.getCorp());
			}
			viewHolder.setText(R.id.job_expr_title_company, sb.toString());
			
			TextView desc = viewHolder.getView(R.id.job_expr_desc);
			desc.setVisibility(View.GONE);
			if(StringUtils.isNotEmpty(item.getDesc())){
				desc.setVisibility(View.VISIBLE);
				desc.setText(item.getDesc());
			}
		}
	}
}
