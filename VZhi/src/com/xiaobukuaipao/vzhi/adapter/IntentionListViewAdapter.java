package com.xiaobukuaipao.vzhi.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.widget.CheckBox;

import com.xiaobukuaipao.vzhi.R;

public class IntentionListViewAdapter extends CommonAdapter<HashMap<String, String>> {
	
	private static final String TAG = IntentionListViewAdapter.class.getName();
	
	// 控制ListView中CheckBox的选择
	public static HashMap<Integer, Boolean> isSelected;
	
	private HashMap<String, String> mPositions;
	
	private HashMap<String, String> mCheckPositions;
	
	public static int MAX_CHECK = 3;
	public static int currentCheck = 0;
	
	@SuppressLint("UseSparseArrays")
	public IntentionListViewAdapter(Context context, List<HashMap<String, String>> mJobInfoList, List<HashMap<String, String>> mCheckList, int itemLayoutId) {
		super(context, mJobInfoList, itemLayoutId);
		mPositions = new HashMap<String, String>();
		mCheckPositions = new HashMap<String, String>();
		
		for(int i=0; i < mCheckList.size(); i++) {
			for (Map.Entry<String, String> entry : mCheckList.get(i).entrySet()) {
				mCheckPositions.put(entry.getKey(), entry.getValue());
		    }
		}
		
		currentCheck = 0;
		
		isSelected = new HashMap<Integer, Boolean>();  
        for (int i = 0; i < mJobInfoList.size(); i++) {  
            isSelected.put(i, false);  
        }
        
        
        // 统计当前选择的个数
        for(int i=0; i < mJobInfoList.size(); i++) {
        	for (Map.Entry<String, String> entry : mJobInfoList.get(i).entrySet()) {
        		if (mCheckPositions.size() > 0) {
	        		if (entry.getValue().equals(mCheckPositions.get(entry.getKey()))) {
	        			isSelected.put(i, true);
	        			currentCheck++;
	        		}
        		}
		    }
        }
        
        // 如果是suggest，重新生成的ListView, 此时currentCheck=mCheckList.size()
        if (mCheckList.size() > 0) {
        	currentCheck = mCheckList.size();
        }
        
        Log.i(TAG, "currentCheck : "  + currentCheck);
	}
	
	

	@Override
	public void convert(final ViewHolder viewHolder, final HashMap<String, String> item, final int position) {
		String positionName = null;
		for(Map.Entry<String, String> entry : item.entrySet()) {
			positionName = entry.getValue();
			mPositions.put(entry.getKey(), entry.getValue());
	    }
		
		// 怎么统计
		/*for(int i=0; i < mCheckPositions.size(); i++) {
			for (String entry : mCheckPositions.keySet()) {
				if (mPositions.containsKey(entry)) {
					isSelected.put(i, true);
					getChecked();
					Log.i(TAG, "current check" + currentCheck);
				}
			}
		}*/
		
		viewHolder.setText(R.id.job_position, positionName);
		
		final CheckBox checkBox = viewHolder.getView(R.id.job_position_select);
		checkBox.setClickable(false);
		checkBox.setFocusable(false);
		checkBox.setFocusableInTouchMode(false);
		checkBox.setChecked(isSelected.get(position));
		
		/*checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (currentCheck == MAX_CHECK) {
					checkBox.setChecked(false)
				}
				if (isChecked) {
					// 如果选中
					currentCheck++;
					Message msg = Message.obtain();
					msg.what = IntentionPosActivity.LISTVIEW_CHECK;
					msg.obj = item;
					IntentionPosActivity.mHandler.sendMessage(msg);
				} else {
					// 如果取消
					Message msg = Message.obtain();
					msg.what = IntentionPosActivity.LISTVIEW_UNCHECK;
					msg.obj = item;
					IntentionPosActivity.mHandler.sendMessage(msg);
				}
			}
		});*/
	}
	
	/*private Integer getChecked() {
		for(Map.Entry<Integer, Boolean> entry : isSelected.entrySet()) {
			if (entry.getValue()) {
				currentCheck++;
			}
	    }
		return currentCheck;
	}*/

}
