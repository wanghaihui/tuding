package com.xiaobukuaipao.vzhi.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.xiaobukuaipao.vzhi.util.A5Utils;

public abstract class CustomView extends RelativeLayout {
	
	protected final static String MATERIALDESIGNXML = "http://schemas.android.com/apk/res-auto";
	protected final static String ANDROIDXML = "http://schemas.android.com/apk/res/android";
	
	protected final int disabledBackgroundColor = Color.parseColor("#E2E2E2");
	protected int beforeBackground;
	// 最小的宽度
	protected int minWidth;
	// 最小的高度
	protected int minHeight;
	
	// 背景颜色
	protected int backgroundColor;
	protected int backgroundResId = -1;// view背景的形状资源
	
	// Indicate(指示) if user touched this view the last time
	public boolean isLastTouch = false;
	
	// 此构造函数走XML
	public CustomView(Context context, AttributeSet attrs) {
		super(context, attrs);
		onInitDefaultValues();
		//onInitAttributes(attrs);
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		
		if (enabled) {
			setBackgroundColor(beforeBackground);
		} else {
			setBackgroundColor(disabledBackgroundColor);
		}
	}
	
	

	protected abstract void onInitDefaultValues();
	
	
	// Set atributtes of XML to View
	protected void setAttributes(AttributeSet attrs) {
		// 设置最小的高度
		setMinimumHeight(A5Utils.dpToPx(minHeight, getResources()));
		// 设置最小的宽度
		setMinimumWidth(A5Utils.dpToPx(minWidth, getResources()));
		
		if (backgroundResId != -1 && !isInEditMode()) {
			setBackgroundResource(backgroundResId);
		}
		// 设置背景属性
		setBackgroundAttributes(attrs);
	}
	
	/**
	 * 设置背景色
	 * Set background Color
	 */
	protected void setBackgroundAttributes(AttributeSet attrs) {
		int bacgroundColor = attrs.getAttributeResourceValue(ANDROIDXML,"background",-1);
		if(bacgroundColor != -1){
			setBackgroundColor(getResources().getColor(bacgroundColor));
		}else{
			// Color by hexadecimal
			int background = attrs.getAttributeIntValue(ANDROIDXML, "background", -1);
			if(background != -1 && !isInEditMode()) {
				setBackgroundColor(background);
			}else {
				setBackgroundColor(backgroundColor);// 如果没有设置，就用这个颜色
			}
		}
	}
	
	
	/**
	 * Make a dark color to press effect
	 * @return
	 */
	protected int makePressColor(int alpha) {
		int r = (backgroundColor >> 16) & 0xFF;
		int g = (backgroundColor >> 8) & 0xFF;
		int b = (backgroundColor >> 0) & 0xFF;
		r = (r - 30 < 0) ? 0 : r - 30;
		g = (g - 30 < 0) ? 0 : g - 30;
		b = (b - 30 < 0) ? 0 : b - 30;
		return Color.argb(alpha, r, g, b);
	}

}
