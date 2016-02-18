package com.xiaobukuaipao.vzhi.adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.util.Linkify;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;


public class EnhancedViewHolder {
	private final SparseArray<View> views;
	private final Context context;
	private int position;
	private View convertView;
	
	/** Package private field to retain the associated user object and detect a change */
	Object associatedObject;
	
	private EnhancedViewHolder(Context context, ViewGroup parent, int layoutId, int position) {
		this.context = context;
		this.position = position;
		this.views = new SparseArray<View>();
		convertView = LayoutInflater.from(context).inflate(layoutId, parent, false);
		convertView.setTag(this);
	}
	
	/**
	 * This method is the only entry point to get a EnhancedViewHolder
	 * @param context
	 * @param convertView
	 * @param parent
	 * @param layoutId
	 * @return
	 */
	public static EnhancedViewHolder get(Context context, View convertView, ViewGroup parent, int layoutId) {
		return get(context, convertView, parent, layoutId, -1);
	}
	
	private static EnhancedViewHolder get(Context context, View convertView, ViewGroup parent, int layoutId, int position) {
		if (convertView == null) {
			return new EnhancedViewHolder(context, parent, layoutId, position);
		}
		
		EnhancedViewHolder mEnhanced = (EnhancedViewHolder) convertView.getTag();
		mEnhanced.position = position;
		return mEnhanced;
	}
	
	/**
	 * 得到View的唯一方式
	 * @param viewId
	 * @return
	 */
	public <T extends View> T getView(int viewId) {
		return retrieveView(viewId);
	}

	/**
	 * 设置TextView
	 * @param viewId
	 * @param value
	 * @return
	 */
	public EnhancedViewHolder setText(int viewId, String value) {
		TextView view = retrieveView(viewId);
		view.setText(value);
		return this;
	}
	
	/**
	 *  设置ImageView
	 * @param viewId
	 * @param imageResId
	 * @return
	 */
	public EnhancedViewHolder setImageResource(int viewId, int imageResId) {
		ImageView view = retrieveView(viewId);
		view.setImageResource(imageResId);
		return this;
	}
	
	/**
	 * 设置背景颜色
	 * @param viewId
	 * @param color
	 * @return
	 */
	public EnhancedViewHolder setBackgroundColor(int viewId, int color) {
		View view = retrieveView(viewId);
		view.setBackgroundColor(color);
		return this;
	}
	
	/**
	 * 设置View的背景
	 * @param viewId
	 * @param backgroundRes
	 * @return
	 */
	public EnhancedViewHolder setBackgroundRes(int viewId, int backgroundRes) {
		View view = retrieveView(viewId);
		view.setBackgroundResource(backgroundRes);
		return this;
	}
	
	/**
	 * 设置文本颜色
	 * @param viewId
	 * @param textColor
	 * @return
	 */
	public EnhancedViewHolder setTextColor(int viewId, int textColor) {
		TextView view = retrieveView(viewId);
		view.setTextColor(textColor);
		return this;
	}
	
	/**
	 * 设置文本颜色
	 * @param viewId
	 * @param textColorRes
	 * @return
	 */
	public EnhancedViewHolder setTextColorRes(int viewId, int textColorRes) {
		TextView view = retrieveView(viewId);
		view.setTextColor(context.getResources().getColor(textColorRes));
		return this;
	}
	
	/**
	 * 设置Drawable形式ImageView
	 * @param viewId
	 * @param drawable
	 * @return
	 */
	public EnhancedViewHolder setImageDrawable(int viewId, Drawable drawable) {
		ImageView view = retrieveView(viewId);
		view.setImageDrawable(drawable);
		return this;
	}
	
	/**
	 * 从Url下载图片，设置到ImageView上
	 * @param viewId
	 * @param imageUrl
	 * @return
	 */
	public EnhancedViewHolder setImageUrl(int viewId, String imageUrl) {
		ImageView view = retrieveView(viewId);
		Picasso.with(context).load(imageUrl).into(view);
		return this;
	}
	
	public EnhancedViewHolder setImageBuilder(int viewId, RequestCreator requestBuilder) {
		ImageView view = retrieveView(viewId);
		requestBuilder.into(view);
		return this;
	}
	
	/**
	 * 设置Bitmap
	 * @param viewId
	 * @param bitmap
	 * @return
	 */
	public EnhancedViewHolder setImageBitmap(int viewId, Bitmap bitmap) {
		ImageView view = retrieveView(viewId);
		view.setImageBitmap(bitmap);
		return this;
	}
	
	/**
	 * 设置透明度
	 * @param viewId
	 * @param value
	 * @return
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public EnhancedViewHolder setAlpha(int viewId, float value) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			retrieveView(viewId).setAlpha(value);
		} else {
			// Pre-honeycomb hack to set Alpha value
			AlphaAnimation alpha = new AlphaAnimation(value, value);
			alpha.setDuration(0);
			alpha.setFillAfter(true);
			retrieveView(viewId).startAnimation(alpha);
		}
		
		return this;
	}
	
	/**
	 * 设置可变性
	 * @param viewId
	 * @param visible
	 * @return
	 */
	public EnhancedViewHolder setVisible(int viewId, boolean visible) {
		View view = retrieveView(viewId);
		view.setVisibility(visible ? View.VISIBLE : View.GONE);
		return this;
	}
	
	/**
	 * 添加超链接
	 * @param viewId
	 * @return
	 */
	public EnhancedViewHolder linkify(int viewId) {
		TextView view = retrieveView(viewId);
		Linkify.addLinks(view, Linkify.ALL);
		return this;
	}
	
	/**
	 * 设置字体--Apply the typeface to the given viewId, and enable subpixel rendering
	 * @param viewId
	 * @param typeface
	 * @return
	 */
	public EnhancedViewHolder setTypeface(int viewId, Typeface typeface) {
		TextView view = retrieveView(viewId);
		view.setTypeface(typeface);
		view.setPaintFlags(view.getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);
		return this;
	}
	
	/**
	 * Apply the typeface to all the given viewIds, and enable subpixel rendering
	 * @param typeface
	 * @param viewIds
	 * @return
	 */
	public EnhancedViewHolder setTypeface(Typeface typeface, int... viewIds) {
		for (int viewId : viewIds) {
			TextView view = retrieveView(viewId);
			view.setTypeface(typeface);
			view.setPaintFlags(view.getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);
		}
		return this;
	}
	
	/**
	 * 设置进度条进度
	 * @param viewId
	 * @param progress
	 * @return
	 */
	public EnhancedViewHolder setProgress(int viewId, int progress) {
		ProgressBar view = retrieveView(viewId);
		view.setProgress(progress);
		return this;
	}
	
	/**
	 * Sets the progress and max of a ProgressBar
	 * @param viewId
	 * @param progress
	 * @param max
	 * @return
	 */
	public EnhancedViewHolder setProgress(int viewId, int progress, int max) {
		ProgressBar view = retrieveView(viewId);
		view.setMax(max);
		view.setProgress(progress);
		return this;
	}
	
	/**
	 * Sets the range of a ProgressBar to 0...max
	 * @param viewId
	 * @param max
	 * @return
	 */
	public EnhancedViewHolder setMax(int viewId, int max) {
		ProgressBar view = retrieveView(viewId);
		view.setMax(max);
		return this;
	}
	
	/**
	 * 设置等级和评价
	 * @param viewId
	 * @param rating
	 * @return
	 */
	public EnhancedViewHolder setRating(int viewId, float rating) {
		RatingBar view = retrieveView(viewId);
		view.setRating(rating);
		return this;
	}
	
	/**
	 * Sets the rating (the number of stars filled) and max of a RatingBar
	 * @param viewId
	 * @param rating
	 * @param max
	 * @return
	 */
	public EnhancedViewHolder setRating(int viewId, float rating, int max) {
		RatingBar view = retrieveView(viewId);
		view.setMax(max);
		view.setRating(rating);
		return this;
	}
	
	// 设置点击监听器
	public EnhancedViewHolder setOnClickListener(int viewId, View.OnClickListener listener) {
		View view = retrieveView(viewId);
		view.setOnClickListener(listener);
		return this;
	}
	
	// 设置Touch监听器
	public EnhancedViewHolder setOnTouchListener(int viewId, View.OnTouchListener listener) {
		View view = retrieveView(viewId);
		view.setOnTouchListener(listener);
		return this;
	}
	
	// 设置长按监听器
	public EnhancedViewHolder setOnLongClickListener(int viewId, View.OnLongClickListener listener) {
		View view = retrieveView(viewId);
		view.setOnLongClickListener(listener);
		return this;
	}
	
	/**
	 * 给View设置Tag
	 * @param viewId
	 * @param tag
	 * @return
	 */
	public EnhancedViewHolder setTag(int viewId, Object tag) {
		View view = retrieveView(viewId);
		view.setTag(tag);
		return this;
	}
	
	public EnhancedViewHolder setTag(int viewId, int key, Object tag) {
		View view = retrieveView(viewId);
		view.setTag(tag);
		return this;
	}
	
	/**
	 * 设置checked status
	 * @param viewId
	 * @param checked
	 * @return
	 */
	public EnhancedViewHolder setChecked(int viewId, boolean checked) {
		Checkable view = (Checkable) retrieveView(viewId);
		view.setChecked(checked);
		return this;
	}
	
	/**
	 * 设置Adapter
	 * @param viewId
	 * @param adapter
	 * @return
	 */
	public EnhancedViewHolder setAdapter(int viewId, Adapter adapter) {
		AdapterView<Adapter> view = retrieveView(viewId);
		view.setAdapter(adapter);
		return this;
	}
	
	/**
	 * Retrieve the convertView
	 * @return
	 */
	public View getView() {
		return convertView;
	}
	
	/**
     * Retrieve the overall position of the data in the list.
     * @throws IllegalArgumentException If the position hasn't been set at the construction of the this helper.
     */
	public int getPosition() {
		if (position == -1) {
			throw new IllegalStateException("Use EnhancedViewHolder constructor " + "with position if you need to retrieve the position.");
		}
		return position;
	}
	
	@SuppressWarnings("unchecked")
	private <T extends View> T retrieveView(int viewId) {
		View view = views.get(viewId);
		if (view == null) {
			view = convertView.findViewById(viewId);
			views.put(viewId, view);
		}
		return (T) view;
	}
	
	/** Retrieves the last converted object on this view. */
    public Object getAssociatedObject() {
        return associatedObject;
    }

    /** Should be called during convert */
    public void setAssociatedObject(Object associatedObject) {
        this.associatedObject = associatedObject;
    }
}
