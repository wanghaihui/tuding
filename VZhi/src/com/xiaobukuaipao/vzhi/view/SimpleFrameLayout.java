package com.xiaobukuaipao.vzhi.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RemoteViews;

import com.xiaobukuaipao.vzhi.R;

@RemoteViews.RemoteView
public class SimpleFrameLayout extends ViewGroup {
	
//	private int mLeftWidth;
//	private int mRightWidth;
//	
//	private final Rect mTmpContainerRect = new Rect();
//	private final Rect mTmpChildRect = new Rect();
	
	public SimpleFrameLayout(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	public SimpleFrameLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		// TODO Auto-generated constructor stub
	}
	
	public SimpleFrameLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	
	// Subclasses that do not scroll should generally override this method and return false.
	@Override
	public boolean shouldDelayChildPressedState() {
		// TODO Auto-generated method stub
		return false;
	}
	
	
	
	/**
     * Ask all children to measure themselves and compute the measurement of this
     * layout based on the children.
     */
	@SuppressLint("NewApi")
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		int count = getChildCount();
		
//		mLeftWidth = 0;
//		mRightWidth = 0;
		
		int maxHeight = 0;
		int maxWidth = 0;
		int childState = 0;
		
		for (int i = 0; i < count; i++) {
			final View child = getChildAt(i);
			if (child.getVisibility() != GONE) {
				measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);
				final LayoutParams lp = (LayoutParams) child.getLayoutParams();
				if (lp.position == LayoutParams.POSITION_LEFT) {
//					mLeftWidth += Math.max(maxWidth, child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin);
				} else if (lp.position == LayoutParams.POSITION_RIGHT) {
//					mRightWidth += Math.max(maxWidth, child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin);
				} else {
					maxWidth = Math.max(maxWidth, child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin);
				}
				
				maxHeight = Math.max(maxHeight, child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin);
				childState = combineMeasuredStates(childState, child.getMeasuredState());
			}
		}
	}

	// 继承ViewGroup必须重写的函数
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// TODO Auto-generated method stub
		
	}

	
	public static class LayoutParams extends MarginLayoutParams {

//		public int gravity = Gravity.TOP | Gravity.START;
		public static int POSITION_MIDDLE = 0;
		public static int POSITION_LEFT = 1;
		public static int POSITION_RIGHT = 2;
		
		public int position = POSITION_MIDDLE;
		
		public LayoutParams(Context c, AttributeSet attrs) {
			super(c, attrs);
			// TODO Auto-generated constructor stub
			TypedArray a = c.obtainStyledAttributes(attrs, R.styleable.CustomLayoutLP);
//			gravity = a.getInt(R.styleable.CustomLayoutLP_android_layout_gravity, gravity);
			position = a.getInt(R.styleable.CustomLayoutLP_layout_position, position);
			a.recycle();
		}
		
		public LayoutParams(int width, int height) {
			super(width, height);
		}
		
		public LayoutParams(ViewGroup.LayoutParams source) {
			super(source);
		}
	}
}
