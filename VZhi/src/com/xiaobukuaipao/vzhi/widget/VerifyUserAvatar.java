package com.xiaobukuaipao.vzhi.widget;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xiaobukuaipao.vzhi.R;
import com.xiaobukuaipao.vzhi.util.DisplayUtil;
import com.xiaobukuaipao.vzhi.view.RoundedImageView;

public class VerifyUserAvatar extends RelativeLayout {

	public RoundedImageView mAvatar;
	public TextView mVerify;
	public Context context;
	
	public VerifyUserAvatar(Context context) {
		this(context, null);
	}
	
	public VerifyUserAvatar(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	public VerifyUserAvatar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
		initChildView();
	}
	
	private void initChildView() {
		mAvatar  = new RoundedImageView(context);
		mAvatar.setId(11);
		mAvatar.setScaleType(ScaleType.CENTER_CROP);
		mAvatar.setCornerRadius(context.getResources().getDimension(R.dimen.image_radius));
		mAvatar.setBorderWidth(context.getResources().getDimension(R.dimen.image_border));
		mAvatar.mutateBackground(true);
		mAvatar.setOval(true);
		mVerify = new TextView(context);
		mVerify.setTextColor(Color.WHITE);
		mVerify.setPadding(DisplayUtil.dip2px(getContext(), 2), 0, DisplayUtil.dip2px(getContext(), 2), 0);
		
		setLayoutParams();
	}
	
	private void setLayoutParams() {
		LayoutParams lpImage = new LayoutParams(DisplayUtil.dip2px(getContext(), 60), DisplayUtil.dip2px(getContext(), 60));
//		LayoutParams lpImage = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
		
		lpImage.setMargins(0, 0, 0, 0);
		addView(mAvatar, lpImage);
		
		LayoutParams lpText = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		// TextView与父View的底部对齐
		lpText.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		lpText.addRule(RelativeLayout.CENTER_HORIZONTAL);
		addView(mVerify, lpText);
	}
	
	public RoundedImageView getAvatar() {
		return mAvatar;
	}
	
	public TextView getVerify() {
		return mVerify;
	}
}
