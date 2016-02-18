package com.xiaobukuaipao.vzhi.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * 通用的Adapter
 * 
 * @author haihui.wang
 * 
 * @param <T>
 */
public abstract class CommonAdapter<T> extends BaseAdapter {

	protected LayoutInflater mLayoutInflater;
	protected Context mContext;
	protected List<T> mDatas;
	protected final int mItemLayoutId;

	public CommonAdapter(Context mContext, List<T> mDatas, int mItemLayoutId) {
		this.mContext = mContext;
		this.mDatas = mDatas;
		this.mItemLayoutId = mItemLayoutId;
	}

	public CommonAdapter(Context mContext, List<T> mDatas) {
		this.mContext = mContext;
		this.mDatas = mDatas;
		mItemLayoutId = 0;
	}
	
	public List<T> getList() {
		if (mDatas != null) {
			return mDatas;
		}
		return null;
	}

	@Override
	public int getCount() {
		return mDatas.size();
	}

	@Override
	public T getItem(int position) {
		return mDatas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder viewHolder = getViewHolder(position, convertView,
				parent);
		convert(viewHolder, getItem(position), position);
		return viewHolder.getConvertView();
	}

	public abstract void convert(ViewHolder viewHolder, T item, int position);

	// 得到ViewHolder的唯一方式
	private ViewHolder getViewHolder(int position, View convertView,
			ViewGroup parent) {
		return ViewHolder.getViewHolder(mContext, convertView, parent,
				mItemLayoutId, position);
	}
}
