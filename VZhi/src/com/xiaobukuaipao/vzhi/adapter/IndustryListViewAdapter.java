package com.xiaobukuaipao.vzhi.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.widget.CheckBox;

import com.xiaobukuaipao.vzhi.R;

public class IndustryListViewAdapter extends CommonAdapter<HashMap<String, String>> {

	// 控制ListView中CheckBox的选择
	public static HashMap<Integer, Boolean> isSelected;
		
	public IndustryListViewAdapter(Context mContext, List<HashMap<String, String>> mDatas, HashMap<String, String> selected, int mItemLayoutId) {
		super(mContext, mDatas, mItemLayoutId);
		
		isSelected = new HashMap<Integer, Boolean>();  
		
        for (int i = 0; i < mDatas.size(); i++) {  
            isSelected.put(i, false);  
        }
        
        for(int i=0; i < mDatas.size(); i++) {
        	for (Map.Entry<String, String> entry : mDatas.get(i).entrySet()) {
        		if (selected.size() > 0) {
	        		if (entry.getValue().equals(selected.get(entry.getKey()))) {
	        			isSelected.put(i, true);
	        		}
        		}
		    }
        }
	}

	@Override
	public void convert(final ViewHolder viewHolder, final HashMap<String, String> item, final int position) {
		String industryName = null;
		for(Map.Entry<String, String> entry : item.entrySet()) {
			industryName = entry.getValue();
	    }
		
		viewHolder.setText(R.id.job_position, industryName);
		
		final CheckBox checkBox = viewHolder.getView(R.id.job_position_select);
		checkBox.setClickable(false);
		checkBox.setFocusable(false);
		checkBox.setFocusableInTouchMode(false);
		checkBox.setChecked(isSelected.get(position));
	}
	
	
	public static  Integer checkSelect() {
		int count = 0;
		for(Map.Entry<Integer, Boolean> entry : isSelected.entrySet()) {
			if (entry.getValue()) {
				count++;
			}
	    }
		return count;
	}

}
