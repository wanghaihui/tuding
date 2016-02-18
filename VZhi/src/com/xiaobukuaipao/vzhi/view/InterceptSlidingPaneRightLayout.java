package com.xiaobukuaipao.vzhi.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

public class InterceptSlidingPaneRightLayout extends LinearLayout {

	public InterceptSlidingPaneRightLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/**
	 * onInterceptTouchEvent()是ViewGroup的一个方法，目的是在系统向该ViewGroup及其各个childView触发onTouchEvent()之前对相关事件进行一次拦截
	 */
	// touch事件在onInterceptTouchEvent()和onTouchEvent以及各个childView间的传递机制完全取决于onInterceptTouchEvent()和onTouchEvent()的返回值
	// 关于返回值的问题，基本规则很清楚，如果return true,那么表示该方法消费了此次事件，如果return false，那么表示该方法并未处理完全，该事件仍然需要以某种方式传递下去继续等待处理
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		
		
		return super.onInterceptTouchEvent(ev);
	}
	
	

}
