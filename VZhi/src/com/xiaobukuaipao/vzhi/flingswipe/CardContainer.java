package com.xiaobukuaipao.vzhi.flingswipe;

import java.util.Random;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;

import com.xiaobukuaipao.vzhi.R;
import com.xiaobukuaipao.vzhi.flingswipe.Orientations.Orientation;
import com.xiaobukuaipao.vzhi.util.DisplayUtil;
import com.xiaobukuaipao.vzhi.widget.ViewPager;

public class CardContainer extends AdapterView<ListAdapter> {
	
	private static final String TAG = CardContainer.class.getSimpleName();
	
	private int MAX_VISIBLE = 3;
    private int MIN_ADAPTER_STACK = 6;
    private float ROTATION_DEGREES = 15.f;
    
	// 方向
	private Orientation mOrientation;
	// Gravity
	private int mGravity;
	
	private static final double DISORDERED_MAX_ROTATION_RADIANS = Math.PI / 64;
	private final Random mRandom = new Random();
	
	// 活跃的卡片--active card
	private View mActiveCard = null;
	
	private OnFlingListener mFlingListener;
	private OnItemClickListener mOnItemClickListener;
    private FlingCardListener flingCardListener;
    
    private ListAdapter mListAdapter;
    private AdapterDataSetObserver mDataSetObserver;
    
    // Stack 中的最后一个对象
    private int LAST_OBJECT_IN_STACK = 0;
    
    private boolean mInLayout = false;
    
    private int heightMeasureSpec;
    private int widthMeasureSpec;
    
    private ViewPager viewPager;
	
	public CardContainer(Context context) {
		this(context, null);
		
	}
	
	public CardContainer(Context context, AttributeSet attrs) {
		this(context, attrs, R.attr.SwipeFlingStyle);
	}

	public CardContainer(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
		// 设置方向
		setOrientation(Orientation.Disordered);
		// 对该view中内容的限定
		setGravity(Gravity.CENTER);
		
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SwipeFlingAdapterView, defStyle, 0);
		MAX_VISIBLE = a.getInt(R.styleable.SwipeFlingAdapterView_max_visible, MAX_VISIBLE);
        MIN_ADAPTER_STACK = a.getInt(R.styleable.SwipeFlingAdapterView_min_adapter_stack, MIN_ADAPTER_STACK);
        ROTATION_DEGREES = a.getFloat(R.styleable.SwipeFlingAdapterView_rotation_degrees, ROTATION_DEGREES);
        a.recycle();
	}

	// 设置方向
	@SuppressLint("NewApi")
	public void setOrientation(Orientation orientation) {
		if (orientation == null) {
			throw new NullPointerException("orientation may not be null");
		}
		
		if (mOrientation != orientation) {
			this.mOrientation = orientation;
			if (orientation ==Orientation.Disordered) {
				for(int i=0; i < getChildCount(); i++) {
					View child = getChildAt(i);
					child.setRotation(getDisorderedRotation());
				}
			} else {
				for(int i=0; i < getChildCount(); i++) {
					View child = getChildAt(i);
					child.setRotation(0);
				}
			}
			
			// requestLayout：当view确定自身已经不再适合现有的区域时，该view本身调用这个方法要求parent view重新调用onMeasure onLayout来对重新设置自己位置
			requestLayout();
		}
	}
	
	private float getDisorderedRotation() {
		return (float) Math.toDegrees(mRandom.nextGaussian() * DISORDERED_MAX_ROTATION_RADIANS);
	}
	
	public void setGravity(int gravity) {
		mGravity = gravity;
	}
	
	public int getGravity() {
		return mGravity;
	}
	
	
	public void setViewPager(ViewPager viewPager) {
		this.viewPager = viewPager;
	}
	public ViewPager getViewPager() {
		return this.viewPager;
	}
	
	////////////////////////////////////////////////////////////////////////////////
	// 必须实现的方法
	@Override
	public ListAdapter getAdapter() {
		return mListAdapter;
	}

	@Override
	public void setAdapter(ListAdapter adapter) {
		if (mListAdapter != null && mDataSetObserver != null) {
			mListAdapter.unregisterDataSetObserver(mDataSetObserver);
            mDataSetObserver = null;
        }

		mListAdapter = adapter;

        if (mListAdapter != null  && mDataSetObserver == null) {
            mDataSetObserver = new AdapterDataSetObserver();
            mListAdapter.registerDataSetObserver(mDataSetObserver);
        }
	}
	
	@Override
	public View getSelectedView() {
		return mActiveCard;
	}

	@Override
	public void setSelection(int position) {
		throw new UnsupportedOperationException("Not Supported");
	}
	////////////////////////////////////////////////////////////////////////////////
	
	@Override
    public void requestLayout() {
        if (!mInLayout) {
            super.requestLayout();
        }
    }

	// 三大基本方法--onMeasure, onLayout, onDraw
	// 测量
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		
		this.widthMeasureSpec = widthMeasureSpec;
	    this.heightMeasureSpec = heightMeasureSpec;
		
		int requestedWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
		int requestedHeight = getMeasuredHeight() - getPaddingTop() - getPaddingBottom();
		
		int childWidth, childHeight;
		
		if (mOrientation == Orientation.Disordered) {
			int R1, R2;
			if (requestedWidth >= requestedHeight) {
				R1 = requestedHeight;
				R2 = requestedWidth;
			} else {
				R1 = requestedWidth;
				R2 = requestedHeight;
			}
			childWidth = (int) ((R1 * Math.cos(DISORDERED_MAX_ROTATION_RADIANS) - R2 * Math.sin(DISORDERED_MAX_ROTATION_RADIANS)) / Math.cos(2 * DISORDERED_MAX_ROTATION_RADIANS));
			childHeight = (int) ((R2 * Math.cos(DISORDERED_MAX_ROTATION_RADIANS) - R1 * Math.sin(DISORDERED_MAX_ROTATION_RADIANS)) / Math.cos(2 * DISORDERED_MAX_ROTATION_RADIANS));
		} else {
			childWidth = requestedWidth;
			childHeight = requestedHeight;
		}
		
		int childWidthMeasureSpec, childHeightMeasureSpec;
		childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(childWidth, MeasureSpec.AT_MOST);
		childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(childHeight, MeasureSpec.AT_MOST);
		
		for(int i=0; i < getChildCount(); i++) {
			View child = getChildAt(i);
			assert child != null;
			child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
		}
	}

	// 布局
	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		
		// if we don't have an adapter, we don't need to do anything
        if (mListAdapter == null) {
            return;
        }
        
        mInLayout = true;
        final int adapterCount = mListAdapter.getCount();

        if(adapterCount == 0) {
            removeAllViewsInLayout();
        }else {
            View topCard = getChildAt(LAST_OBJECT_IN_STACK);
            
            if (mActiveCard!=null && topCard!=null && topCard==mActiveCard) {
                removeViewsInLayout(0, LAST_OBJECT_IN_STACK);
                layoutChildren(1, adapterCount);
            } else {
                // Reset the UI and set top view listener
                removeAllViewsInLayout();
                layoutChildren(0, adapterCount);
                setTopView();
            }
        }

        mInLayout = false;
        
        if(adapterCount < MAX_VISIBLE) mFlingListener.onAdapterAboutToEmpty(adapterCount);
		
	}
	
	
	@SuppressLint("NewApi")
	private void layoutChildren(int startingIndex, int adapterCount){
		// 布局子View
        while (startingIndex < Math.min(adapterCount, MAX_VISIBLE) ) {
            View newUnderChild = mListAdapter.getView(startingIndex, null, this);
            
            if(mOrientation == Orientation.Disordered) {
            	// 设置旋转角度
            	// newUnderChild.setRotation(getDisorderedRotation());
			}
            
            if (newUnderChild.getVisibility() != GONE) {
            	Log.i(TAG, "startingIndex : " + startingIndex);
                makeAndAddView(newUnderChild, startingIndex);
                LAST_OBJECT_IN_STACK = startingIndex;
            }
            startingIndex++;
        }
    }
	
	 @SuppressLint("NewApi")
	private void makeAndAddView(View child, int startingIndex) {
		ViewGroup.LayoutParams lp = (ViewGroup.LayoutParams) child.getLayoutParams();
	    addViewInLayout(child, 0, lp, true);
	    
		final boolean needToMeasure = child.isLayoutRequested();
	    if (needToMeasure) {
	    	Log.i(TAG, "need to measure");
	        int childWidthSpec = getChildMeasureSpec(this.widthMeasureSpec,
	                getPaddingLeft() + getPaddingRight(),
	                lp.width);
	        int childHeightSpec = getChildMeasureSpec(this.heightMeasureSpec,
	                getPaddingTop() + getPaddingBottom(),
	                lp.height);
	        child.measure(childWidthSpec, childHeightSpec);
	    } else {
	    	Log.i(TAG, "not need to measure");
	        cleanupLayoutState(child);
	    }


        int w = child.getMeasuredWidth();
        int h = child.getMeasuredHeight();

        int childLeft;
        int childTop;
        switch (mGravity & Gravity.HORIZONTAL_GRAVITY_MASK) {
            case Gravity.CENTER_HORIZONTAL:
            	Log.i(TAG, "Gravity.CENTER_HORIZONTAL");
                childLeft = (getWidth() + getPaddingLeft() - getPaddingRight()  - w) / 2;
                break;
            case Gravity.END:
            	Log.i(TAG, "Gravity.END");
                childLeft = getWidth() + getPaddingRight() - w;
                break;
            case Gravity.START:
            default:
            	Log.i(TAG, "Gravity.START");
                childLeft = getPaddingLeft();
                break;
        }
        
        switch (mGravity) {
            case Gravity.CENTER_VERTICAL:
            	Log.i(TAG, "Gravity.CENTER_VERTICAL");
                childTop = (getHeight() + getPaddingTop() - getPaddingBottom()  - h) / 2;
                break;
            case Gravity.BOTTOM:
            	Log.i(TAG, "Gravity.BOTTOM");
                childTop = getHeight() - getPaddingBottom() - h;
                break;
            case Gravity.TOP:
            default:
            	Log.i(TAG, "Gravity.TOP");
                childTop = getPaddingTop();
                break;
        }

        // 布局
        Log.i(TAG, "childLeft : " + childLeft);
        Log.i(TAG, "childTop : " + childTop);
        Log.i(TAG, "childLeft : " + childLeft + " w: " + w);
        Log.i(TAG, "childTop : " + childTop + " h: " + h);
        child.layout(childLeft + startingIndex * DisplayUtil.dip2px(getContext(), 4), 
        		childTop, 
        		childLeft + w - startingIndex * DisplayUtil.dip2px(getContext(), 4), 
        		childTop + h - (MAX_VISIBLE - startingIndex) * DisplayUtil.dip2px(getContext(), 4)
        		);
    }
	
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	    *  Set the top view and add the fling listener
	    */
	// 设置顶部Top View
    private void setTopView() {
    	
        if(getChildCount()>0){
            mActiveCard = getChildAt(LAST_OBJECT_IN_STACK);
            if(mActiveCard!=null) {
            	
                flingCardListener = new FlingCardListener(mActiveCard, mListAdapter.getItem(0),
                        ROTATION_DEGREES, new FlingCardListener.FlingListener() {

                            @Override
                            public void onCardExited() {
                                mActiveCard = null;
                                mFlingListener.removeFirstObjectInAdapter();
                            }

                            @Override
                            public void leftExit(Object dataObject) {
                                mFlingListener.onLeftCardExit(dataObject);
                            }

                            @Override
                            public void rightExit(Object dataObject) {
                                mFlingListener.onRightCardExit(dataObject);
                            }

                            @Override
                            public void onClick(Object dataObject) {
                                if(mOnItemClickListener!=null)
                                    mOnItemClickListener.onItemClicked(0, dataObject);

                            }

							@Override
							public void onCardEntered() {
								mFlingListener.onCardEntered();
							}
                        }, viewPager);

                	mActiveCard.setOnTouchListener(flingCardListener);
            }
        }
    }
    
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Adapter数据集--观察者
	private class AdapterDataSetObserver extends DataSetObserver {
        @Override
        public void onChanged() {
            requestLayout();
        }
        
        @Override
        public void onInvalidated() {
            requestLayout();
        }
    }
	
	// 设置监听器
	public void setFlingListener(OnFlingListener onFlingListener) {
		this.mFlingListener = onFlingListener;
	}
	
	public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
		this.mOnItemClickListener = onItemClickListener;
	}
	
	public interface OnItemClickListener {
		public void onItemClicked(int itemPosition, Object dataObject);
	}
	
	public interface OnFlingListener {
		// 第一个对象进入
		public void onCardEntered();
		// 移除第一个对象
		public void removeFirstObjectInAdapter();
		// 卡片从左边退出
		public void onLeftCardExit(Object dataObject);
		// 卡片从右边退出
		public void onRightCardExit(Object dataObject);
		// 当Adapter中的卡片为空
		public void onAdapterAboutToEmpty(int itemsInAdapter);
	}
	
}
