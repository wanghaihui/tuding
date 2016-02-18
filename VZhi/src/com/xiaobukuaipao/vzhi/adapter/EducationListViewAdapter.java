package com.xiaobukuaipao.vzhi.adapter;

import java.util.List;

import android.content.Context;

import com.xiaobukuaipao.vzhi.R;
import com.xiaobukuaipao.vzhi.domain.user.Education;
import com.xiaobukuaipao.vzhi.widget.FormItemByLineView;

public class EducationListViewAdapter extends CommonAdapter<Education> {


    public EducationListViewAdapter(Context mContext, List<Education> mDatas,
			int mItemLayoutId) {
		super(mContext, mDatas, mItemLayoutId);
	}


	@Override
	public void convert(ViewHolder viewHolder, Education item, int position) {
		((FormItemByLineView)viewHolder.getView(R.id.user_time_section_id)).setFormLabel(item.getBegin() + "--" + item.getEnd());
		((FormItemByLineView)viewHolder.getView(R.id.user_school_section_id)).setFormLabel(item.getSchool().getName());
		((FormItemByLineView)viewHolder.getView(R.id.user_degree_section_id)).setFormLabel(item.getDegree().getName());
		((FormItemByLineView)viewHolder.getView(R.id.user_major_section_id)).setFormLabel(item.getMajor().getName());
	}
}
