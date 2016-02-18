package com.xiaobukuaipao.vzhi.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaobukuaipao.vzhi.core.ImageLoader;
import com.xiaobukuaipao.vzhi.core.ImageLoader.Type;

/**
 * 一个通用的ViewHolder, 用在Adapter中
 * 
 * @author haihui.wang
 */
public class ViewHolder {
	
	// Views indexed with their IDs
	public final SparseArray<View> mViews;
	
	public View mConvertView;

	private int position;

	public ViewHolder(Context context, ViewGroup parent, int layoutId,
			int position) {
		this.position = position;
		mViews = new SparseArray<View>();
		mConvertView = LayoutInflater.from(context).inflate(layoutId, parent, false);
		mConvertView.setTag(this);
	}
	
	/**
	 * 得到一个ViewHolder对象
	 * 
	 * @param context
	 * @param convertView
	 * @param parent
	 * @param layoutId
	 * @param position
	 * @return
	 */
	public static ViewHolder getViewHolder(Context context, View convertView,
			ViewGroup parent, int layoutId, int position) {
		if (convertView == null ) {
			return new ViewHolder(context, parent, layoutId, position);
		}
		return (ViewHolder) convertView.getTag();
	}
	
	/**
	 * 得到一个ViewHolder对象
	 * 
	 * @param context
	 * @param convertView
	 * @param parent
	 * @param layoutId
	 * @param position
	 * @return
	 */
	public static ViewHolder getViewHolderForMessage(Context context, View convertView,
			ViewGroup parent, int layoutId, int position) {
		return new ViewHolder(context, parent, layoutId, position);
	}
	
	@SuppressWarnings("unchecked")
	public <T extends View> T getView(int mViewId) {
		View view = mViews.get(mViewId);
		if (view == null) {
			view = mConvertView.findViewById(mViewId);
			mViews.put(mViewId, view);
		}

		return (T) view;
	}

	public View getConvertView() {
		return mConvertView;
	}

	/**
	 * 为TextView设置字符串
	 * 
	 * @param viewId
	 * @param text
	 * @return
	 */
	public ViewHolder setText(int viewId, String text) {
		TextView view = getView(viewId);
		view.setText(text);
		return this;
	}
	
	public ViewHolder setTextColor(int viewId, int color) {
		TextView view = getView(viewId);
		view.setTextColor(color);
		return this;
	}

	public ViewHolder setText(int viewId, CharSequence text) {
		TextView view = getView(viewId);
		view.setText(text);
		return this;
	}

	/**
	 * 为ImageView设置图片
	 * 
	 * @param viewId
	 * @param drawableId
	 * @return
	 */
	public ViewHolder setImageResource(int viewId, int drawableId) {
		ImageView view = getView(viewId);
		view.setImageResource(drawableId);

		return this;
	}

	/**
	 * 为ImageView设置图片
	 * 
	 * @param viewId
	 * @param drawableId
	 * @return
	 */
	public ViewHolder setImageBitmap(int viewId, Bitmap bm) {
		ImageView view = getView(viewId);
		view.setImageBitmap(bm);
		return this;
	}

	/**
	 * 为ImageView设置图片
	 * 
	 * @param viewId
	 * @param drawableId
	 * @return
	 */
	public ViewHolder setImageByUrl(int viewId, String url) {
		ImageLoader.getInstance(3, Type.LIFO).loadImage(url,
				(ImageView) getView(viewId));
		return this;
	}

	public int getPosition() {
		return position;
	}
}
