package com.xiaobukuaipao.vzhi.adapter;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.xiaobukuaipao.vzhi.R;
import com.xiaobukuaipao.vzhi.domain.JobInfo;
import com.xiaobukuaipao.vzhi.util.GlobalConstants;

/**
 * 职位列表
 * @author xiaobu
 */
public class JobPositionStringAdapter extends CommonAdapter<JobInfo> {
	public static final String TAG = JobPositionStringAdapter.class.getSimpleName();

	// 当前选择的Position
	private Integer mCurrentPosition;
	
	// 控制ListView中CheckBox的选择
	private HashMap<Integer, Boolean> isSelected;
	
	public JobPositionStringAdapter(Context mContext, List<JobInfo> mDatas, int mItemLayoutId, Integer mJobPosition, HashMap<Integer, Boolean> isSelected) {
		super(mContext, mDatas, mItemLayoutId);
		
		mCurrentPosition = mJobPosition;
		
		this.isSelected = isSelected;  
		
        for (int i = 0; i < mDatas.size(); i++) {
        	if (i == mCurrentPosition) {
        		isSelected.put(i, true);
        	} else {
        		isSelected.put(i, false);
        	}
        }
	}

	@Override
	public void convert(final ViewHolder viewHolder, final JobInfo item, final int position) {
		if (item != null) {
			if (item.getId().equals("")) {
				viewHolder.setText(R.id.job_position, "全部");
			} else {
				viewHolder.setText(R.id.job_position, item.getPosition().getString(GlobalConstants.JSON_NAME));
			}
			
			final ImageView imageView = viewHolder.getView(R.id.checked_marker);
			if (isSelected == null) {
				Log.i(TAG, "isSelected is null");
			} else {
				Log.i(TAG, "isSelected is not null");
			}
			if (isSelected.get(position)) {
				imageView.setVisibility(View.VISIBLE);
			} else {
				imageView.setVisibility(View.INVISIBLE);
			}
		}
	}

}
