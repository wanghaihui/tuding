package com.xiaobukuaipao.vzhi.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.xiaobukuaipao.vzhi.R;
import com.xiaobukuaipao.vzhi.fragment.JobObjectivePositionFragment;
import com.xiaobukuaipao.vzhi.view.AutoResizeTextView;

public class IntentionGridViewAdapter extends CommonAdapter<HashMap<String, String>> {
	
	final String DOUBLE_BYTE_SPACE = "\u3000";
	private HashMap<String, String> mPositions;
	
	public IntentionGridViewAdapter(Context context, List<HashMap<String, String>> mJobInfoList, int itemLayoutId) {
		super(context, mJobInfoList, itemLayoutId);
		mPositions = new HashMap<String, String>();
	}
	// 得到ViewHolder的唯一方式
	private ViewHolder getViewHolder(int position, View convertView,
			ViewGroup parent) {
		convertView = null;//去除复用
		return ViewHolder.getViewHolder(mContext, convertView, parent,mItemLayoutId, position);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder viewHolder = getViewHolder(position, convertView,
				parent);
		convert(viewHolder, getItem(position), position);
		return viewHolder.getConvertView();
	}
	
	@Override
	public void convert(final ViewHolder viewHolder, final HashMap<String, String> item, final int position) {
		// Androids versions: 3.1 - 4.04 have a bug, that setTextSize() inside of TextView works only for the 1st time 
		String fixString = "";
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB_MR1
				   && android.os.Build.VERSION.SDK_INT <= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {  
				    fixString = DOUBLE_BYTE_SPACE;
				}
		
		String positionName = null;
		for (Map.Entry<String, String> entry : item.entrySet()) {
			positionName = entry.getValue();
			mPositions.put(entry.getKey(), entry.getValue());
	    }
		AutoResizeTextView view = viewHolder.getView(R.id.job_position);
		view.setText(fixString + positionName);
		view.resetTextSize();
		final ImageView mImageView = viewHolder.getView(R.id.job_position_del);
		
		// 设置ImageView的点击事件
		// 特别注意：如果删除一个GridView中的项，要考虑到如果当前的ListView中也存在这一项，那就需要将ListView的这一项由check状态变为uncheck状态
		mImageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v)
			{
				/*mDatas.remove(position);
				// 刷新View数据
				notifyDataSetChanged();*/
				Message msg = Message.obtain();
				msg.what = JobObjectivePositionFragment.GRIDVIEW_DELETE;
				msg.obj = item;
				JobObjectivePositionFragment.mHandler.sendMessage(msg);
			}
		});
	}
	

}
