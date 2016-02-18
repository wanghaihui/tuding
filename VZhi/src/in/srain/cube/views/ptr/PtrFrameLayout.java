package in.srain.cube.views.ptr;

import in.srain.cube.views.ptr.util.PtrLocalDisplay;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;
import android.widget.TextView;

import com.xiaobukuaipao.vzhi.R;

/**
 * This layout view for "Pull to Refresh(Ptr)" support all of the view, you can contain everything you want.
 * support: pull to refresh / release to refresh / auto refresh / keep header view while refreshing / hide header view while refreshing
 * It defines {@link in.srain.cube.views.ptr.PtrUIHandler}, which allows you customize the UI easily.
 */
public class PtrFrameLayout extends ViewGroup {
	
	private static final String TAG = PtrFrameLayout.class.getSimpleName();
	
    // status enum
	// byte -- 8位
    public final static byte PTR_STATUS_INIT = 1;
    public final static byte PTR_STATUS_PREPARE = 2;
    public final static byte PTR_STATUS_LOADING = 3;
    public final static byte PTR_STATUS_COMPLETE = 4;

    private final static int POS_START = 0;
    private static final boolean DEBUG_LAYOUT = false;
    public static boolean DEBUG = false;
    
    private static int ID = 1;
    
    // auto refresh status
    private static byte STATUS_AUTO_SCROLL_AT_ONCE = 0x01;
    private static byte STATUS_AUTO_SCROLL_LATER = 0x02;
    
    protected final String LOG_TAG = "ptr-frame-" + ++ID;
    
    // 内容View
    protected View mContent;
    protected int mOffsetToRefresh = 0;
    
    // optional config for define header and content in xml file
    private int mHeaderId = 0;
    private int mContainerId = 0;
    
    // config
    private float mResistance = 1.7f;
    private int mDurationToClose = 200;
    // 关闭头部的时间间隔
    private int mDurationToCloseHeader = 1000;
    
    private float mRatioOfHeaderHeightToRefresh = 1.2f;
    
    private boolean mKeepHeaderWhenRefresh = true;
    private boolean mPullToRefresh = false;
    
    // 头View
    private View mHeaderView;
    
    // UI Handler Holder模式
    private PtrUIHandlerHolder mPtrUIHandlerHolder = PtrUIHandlerHolder.create();
    
    private PtrHandler mPtrHandler;
    // working parameters
    private ScrollChecker mScrollChecker;
    
    // 上次移动的点
    private PointF mPtLastMove = new PointF();
    
    // 当前的位置
    private int mCurrentPos = 0;
    
    private int mLastPos = 0;
    // Touch Slop
    private int mPagingTouchSlop;
    // Header的高度
    private int mHeaderHeight;

    private byte mStatus = PTR_STATUS_INIT;
    
    private boolean mIsUnderTouch = false;
    private boolean mDisableWhenHorizontalMove = false;
    
    // 自动滚动刷新TAG
    private int mAutoScrollRefreshTag = 0x00;
    
    private int mPressedPos = 0;

    // disable when detect moving horizontally
    private boolean mPreventForHorizontal = false;

    // intercept(拦截) child event while working
    private boolean mInterceptEventWhileWorking = false;
    // 按下事件
    private MotionEvent mDownEvent;
    // 上次移动事件
    private MotionEvent mLastMoveEvent;

    private PtrUIHandlerHook mRefreshCompleteHook;

    private int mLoadingMinTime = 500;
    private long mLoadingStartTime = 0;
    
    public PtrFrameLayout(Context context) {
        this(context, null);
    }

    public PtrFrameLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PtrFrameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // 初始化
        PtrLocalDisplay.init(getContext());
        
        // 类型数组
        // 得到样式属性
        TypedArray arr = context.obtainStyledAttributes(attrs, R.styleable.PtrFrameLayout, 0, 0);
        
        if (arr != null) {
            mHeaderId = arr.getResourceId(R.styleable.PtrFrameLayout_ptr_header, mHeaderId);
            mContainerId = arr.getResourceId(R.styleable.PtrFrameLayout_ptr_content, mContainerId);

            mResistance = arr.getFloat(R.styleable.PtrFrameLayout_ptr_resistance, mResistance);
            
            mDurationToClose = arr.getInt(R.styleable.PtrFrameLayout_ptr_duration_to_close, mDurationToClose);
            mDurationToCloseHeader = arr.getInt(R.styleable.PtrFrameLayout_ptr_duration_to_close_header, mDurationToCloseHeader);
            
            mRatioOfHeaderHeightToRefresh = arr.getFloat(R.styleable.PtrFrameLayout_ptr_ratio_of_header_height_to_refresh, mRatioOfHeaderHeightToRefresh);
            mKeepHeaderWhenRefresh = arr.getBoolean(R.styleable.PtrFrameLayout_ptr_keep_header_when_refresh, mKeepHeaderWhenRefresh);
            
            mPullToRefresh = arr.getBoolean(R.styleable.PtrFrameLayout_ptr_pull_to_fresh, mPullToRefresh);
            
            arr.recycle();
        }

        mScrollChecker = new ScrollChecker();

        // Returns a configuration for the specified context
        final ViewConfiguration conf = ViewConfiguration.get(getContext());
        // getScaledTouchSlop是一个距离,表示滑动的时候,手的移动要大于这个距离才开始移动控
        // mPagingTouchSlop = conf.getScaledTouchSlop() * 2;
        mPagingTouchSlop = conf.getScaledTouchSlop();
    }

    /**
     * 完成填充
     */
    @Override
    protected void onFinishInflate() {
    	// 得到子类的数量
        final int childCount = getChildCount();
        // 如果子View的数量大于2
        if (childCount > 2) {
            throw new IllegalStateException("PtrFrameLayout only can host 2 elements");
        } else if (childCount == 2) {
        	
            if (mHeaderId != 0 && mHeaderView == null) {
                mHeaderView = findViewById(mHeaderId);
            }
            
            if (mContainerId != 0 && mContent == null) {
                mContent = findViewById(mContainerId);
            }

            // not specify(指定) header or content
            if (mContent == null || mHeaderView == null) {
            	
                View child1 = getChildAt(0);
                View child2 = getChildAt(1);
                
                if (child1 instanceof PtrUIHandler) {
                    mHeaderView = child1;
                    mContent = child2;
                } else if (child2 instanceof PtrUIHandler) {
                    mHeaderView = child2;
                    mContent = child1;
                } else {
                    // both are not specified
                    if (mContent == null && mHeaderView == null) {
                        mHeaderView = child1;
                        mContent = child2;
                    }
                    // only one is specified
                    else {
                        if (mHeaderView == null) {
                            mHeaderView = mContent == child1 ? child2 : child1;
                        } else {
                            mContent = mHeaderView == child1 ? child2 : child1;
                        }
                    }
                }
            }
        } else if (childCount == 1) {
            mContent = getChildAt(0);
        } else {
            TextView errorView = new TextView(getContext());
            errorView.setClickable(true);
            errorView.setTextColor(0xffff6600);
            errorView.setGravity(Gravity.CENTER);
            errorView.setTextSize(20);
            errorView.setText("The content view in PtrFrameLayout is empty. Do you forget to specify its id in xml layout file?");
            mContent = errorView;
            addView(mContent);
        }
        
        super.onFinishInflate();
    }
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {    	
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        
        if (mHeaderView != null) {
            measureChildWithMargins(mHeaderView, widthMeasureSpec, 0, heightMeasureSpec, 0);
            
            MarginLayoutParams lp = (MarginLayoutParams) mHeaderView.getLayoutParams();
            
            mHeaderHeight = mHeaderView.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;
            
            // 下拉刷新拉过的距离
            mOffsetToRefresh = (int) (mHeaderHeight * mRatioOfHeaderHeightToRefresh);
        }
        
        if (mContent != null) {
            measureContentView(mContent, widthMeasureSpec, heightMeasureSpec);
            
            if (DEBUG && DEBUG_LAYOUT) {
                ViewGroup.MarginLayoutParams lp = (MarginLayoutParams) mContent.getLayoutParams();
            }
        }
    }

    /**
     * 测量内容View
     * @param child
     * @param parentWidthMeasureSpec
     * @param parentHeightMeasureSpec
     */
    private void measureContentView(View child,
                                    int parentWidthMeasureSpec,
                                    int parentHeightMeasureSpec) {
        final MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();

        final int childWidthMeasureSpec = getChildMeasureSpec(parentWidthMeasureSpec,
                getPaddingLeft() + getPaddingRight() + lp.leftMargin + lp.rightMargin, lp.width);
        
        final int childHeightMeasureSpec = getChildMeasureSpec(parentHeightMeasureSpec,
                getPaddingTop() + getPaddingBottom() + lp.topMargin, lp.height);
        
        child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
    }

    /**
     * 布局
     */
    @Override
    protected void onLayout(boolean flag, int i, int j, int k, int l) {
    	// 布局子View
        layoutChildren();
    }

    private void layoutChildren() {
        int offsetX = mCurrentPos;
        
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();

        if (mHeaderView != null) {
            MarginLayoutParams lp = (MarginLayoutParams) mHeaderView.getLayoutParams();
            
            final int left = paddingLeft + lp.leftMargin;
            final int top = paddingTop + lp.topMargin + offsetX - mHeaderHeight;
            
            final int right = left + mHeaderView.getMeasuredWidth();
            final int bottom = top + mHeaderView.getMeasuredHeight();
            
            mHeaderView.layout(left, top, right, bottom);
        }
        
        if (mContent != null) {
            MarginLayoutParams lp = (MarginLayoutParams) mContent.getLayoutParams();
            
            final int left = paddingLeft + lp.leftMargin;
            final int top = paddingTop + lp.topMargin + offsetX;
            
            final int right = left + mContent.getMeasuredWidth();
            final int bottom = top + mContent.getMeasuredHeight();
            
            mContent.layout(left, top, right, bottom);
        }
    }
    
    // 上级
    public boolean dispatchTouchEventSupper(MotionEvent e) {
        return super.dispatchTouchEvent(e);
    }
    
    /**
     * Pass the touch screen motion event down to the target view, or this view if it is the target
     */
    // 只要你触摸到了任何一个控件，就一定会调用该控件的dispatchTouchEvent方法
    @Override
    public boolean dispatchTouchEvent(MotionEvent e) {
    	
    	// 如果View不可用
        if (!isEnabled() || mContent == null || mHeaderView == null) {
            return dispatchTouchEventSupper(e);
        }
        
        int action = e.getAction();
        
        switch (action) {
        	
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mIsUnderTouch = false;
                
                if (mCurrentPos > POS_START) {

                    onRelease(false);
                    
                    if (mCurrentPos != mPressedPos) {
                        return true;
                    }
                    
                    return dispatchTouchEventSupper(e);
                } else {
                    return dispatchTouchEventSupper(e);
                }
                
            case MotionEvent.ACTION_DOWN:
                mDownEvent = e;
                mPtLastMove.set(e.getX(), e.getY());
                // 如果在滚动，则停止工作
                mScrollChecker.abortIfWorking();

                // 正在Touch
                mIsUnderTouch = true;
                mPreventForHorizontal = false;
                
                if (mInterceptEventWhileWorking && mCurrentPos > POS_START) {
                    // do nothing, intercept child event
                } else {
                    dispatchTouchEventSupper(e);
                }
                
                // 按下的位置=当前的位置
                mPressedPos = mCurrentPos;
                
                return true;

            case MotionEvent.ACTION_MOVE:
                mLastMoveEvent = e;
                float offsetX = e.getX() - mPtLastMove.x;
                float offsetY = (int) (e.getY() - mPtLastMove.y);

                mPtLastMove.set(e.getX(), e.getY());
                
                if (mDisableWhenHorizontalMove && !mPreventForHorizontal && (Math.abs(offsetX) > mPagingTouchSlop || Math.abs(offsetX) > 3 * Math.abs(offsetY))) {
                    if (frameIsNotMoved()) {
                        mPreventForHorizontal = true;
                    }
                }
                
                if (mPreventForHorizontal) {
                    return dispatchTouchEventSupper(e);
                }

                boolean moveDown = offsetY > 0;
                boolean moveUp = !moveDown;
                boolean canMoveUp = mCurrentPos > POS_START;

                if (DEBUG) {
                    boolean canMoveDown = mPtrHandler != null && mPtrHandler.checkCanDoRefresh(this, mContent, mHeaderView);
                }

                // disable move when header not reach top
                if (moveDown && mPtrHandler != null && !mPtrHandler.checkCanDoRefresh(this, mContent, mHeaderView)) {
                    return dispatchTouchEventSupper(e);
                }

                if ((moveUp && canMoveUp) || moveDown) {
                    offsetY = (float) ((double) offsetY / mResistance);
                    movePos(offsetY);
                    return true;
                }
                
        }
        
        return dispatchTouchEventSupper(e);
    }

    
    /**
     * if deltaY > 0, move the content down
     *
     * @param deltaY
     */
    private void movePos(float deltaY) {
        // has reached the top
        if ((deltaY < 0 && mCurrentPos == POS_START)) {
            return;
        }

        int to = mCurrentPos + (int) deltaY;

        // over top
        if (to < POS_START) {
            to = POS_START;
        }

        mCurrentPos = to;
        updatePos();
        mLastPos = mCurrentPos;
    }

    private void updatePos() {
        int change = mCurrentPos - mLastPos;
        if (change == 0) {
            return;
        }

        // leave initiated position
        if (mLastPos == POS_START && mCurrentPos != POS_START && mPtrUIHandlerHolder.hasHandler()) {
            if (mStatus == PTR_STATUS_INIT) {
                mStatus = PTR_STATUS_PREPARE;
                mPtrUIHandlerHolder.onUIRefreshPrepare(this);
            }

            // send cancel event to children
            if (mIsUnderTouch && mInterceptEventWhileWorking) {
                sendCancelEvent();
            }
        }

        // back to initiated position
        if (mLastPos != POS_START && mCurrentPos == POS_START) {
            tryToNotifyReset();

            // recover event to children
            if (mIsUnderTouch && mInterceptEventWhileWorking) {
                sendDownEvent();
            }
        }

        // Pull to Refresh
        if (mStatus == PTR_STATUS_PREPARE) {
            // reach fresh height while moving from top to bottom
            if (mIsUnderTouch && mAutoScrollRefreshTag == 0 && mPullToRefresh
                    && mLastPos < mOffsetToRefresh && mCurrentPos >= mOffsetToRefresh) {
                tryToPerformRefresh();
            }
            // reach header height while auto refresh
            if (mAutoScrollRefreshTag == STATUS_AUTO_SCROLL_LATER && mLastPos < mHeaderHeight && mCurrentPos >= mHeaderHeight) {
                tryToPerformRefresh();
            }
        }

        mHeaderView.offsetTopAndBottom(change);
        mContent.offsetTopAndBottom(change);
        invalidate();

        final float oldPercent = mHeaderHeight == 0 ? 0 : mLastPos * 1f / mHeaderHeight;
        final float currentPercent = mHeaderHeight == 0 ? 0 : mCurrentPos * 1f / mHeaderHeight;
        if (mPtrUIHandlerHolder.hasHandler()) {
            mPtrUIHandlerHolder.onUIPositionChange(this, mIsUnderTouch, mStatus, mLastPos, mCurrentPos, oldPercent, currentPercent);
        }
        onPositionChange(mIsUnderTouch, mStatus, mLastPos, mCurrentPos, oldPercent, currentPercent);
    }

    
    protected void onPositionChange(boolean isInTouching, byte status, int lastPosition, int currentPosition, float oldPercent, float currentPercent) {
    	
    }

    
    @SuppressWarnings("unused")
    public int getHeaderHeight() {
        return mHeaderHeight;
    }

    
    //  释放
    private void onRelease(boolean stayForLoading) {

    	// 试着去执行刷新
        tryToPerformRefresh();

        if (mStatus == PTR_STATUS_LOADING) {
        	
            // keep header for fresh
            if (mKeepHeaderWhenRefresh) {
                // scroll header back
                if (mCurrentPos > mHeaderHeight && !stayForLoading) {
                    mScrollChecker.tryToScrollTo(mHeaderHeight, mDurationToClose);
                } else {
                    // do nothing
                }
            } else {
            	// 此时, 将头部隐藏
                tryScrollBackToTopWhileLoading();
            }
            
        } else {
            if (mStatus == PTR_STATUS_COMPLETE) {
            	// 通知UI刷新完成
                notifyUIRefreshComplete(false);
            } else {
                tryScrollBackToTopAbortRefresh();
            }
        }
        
    }

    /**
     * please DO REMEMBER resume the hook
     *
     * @param hook
     */
    public void setRefreshCompleteHook(PtrUIHandlerHook hook) {
        mRefreshCompleteHook = hook;
        hook.setResumeAction(new Runnable() {
            @Override
            public void run() {
                notifyUIRefreshComplete(true);
            }
        });
    }

    /**
     * Scroll back to to if is not under touch
     */
    private void tryScrollBackToTop() {
        if (!mIsUnderTouch) {
            mScrollChecker.tryToScrollTo(POS_START, mDurationToCloseHeader);
        }
    }

    /**
     * just make easier to understand
     */
    // 自动移动到顶部
    private void tryScrollBackToTopWhileLoading() {
        tryScrollBackToTop();
    }

    /**
     * just make easier to understand
     */
    // 完成之后滚会到顶部
    private void tryScrollBackToTopAfterComplete() {
        tryScrollBackToTop();
    }

    /**
     * just make easier to understand
     */
    // 完成之后滚会到顶部
    private void tryScrollBackToTopAbortRefresh() {
        tryScrollBackToTop();
    }

    /**
     * 试着执行刷新
     * @return
     */
    private boolean tryToPerformRefresh() {
    	// 只有status处于Prepare阶段, 才去执行刷新
        if (mStatus != PTR_STATUS_PREPARE) {
            return false;
        }
        
        // 两种刷新的情况
        if ((mCurrentPos >= mHeaderHeight && mAutoScrollRefreshTag > 0) || mCurrentPos >= mOffsetToRefresh) {
        	// 状态变为加载状态
            mStatus = PTR_STATUS_LOADING;
            // 执行刷新
            performRefresh();
        }
        
        return false;
    }

    /**
     * 执行刷新
     */
    private void performRefresh() {
    	// 加载的起始时间
        mLoadingStartTime = System.currentTimeMillis();
        
        // 头部刷新
        if (mPtrUIHandlerHolder.hasHandler()) {
            mPtrUIHandlerHolder.onUIRefreshBegin(this);
        }
        
        // 内容刷新--Content
        if (mPtrHandler != null) {
            mPtrHandler.onRefreshBegin(this);
        }
    }

    /**
     * If at the top and not in loading, reset
     */
    // 通知Reset
    private boolean tryToNotifyReset() {
        if ((mStatus == PTR_STATUS_COMPLETE || mStatus == PTR_STATUS_PREPARE) && mCurrentPos == POS_START) {
            if (mPtrUIHandlerHolder.hasHandler()) {
                mPtrUIHandlerHolder.onUIReset(this);
            }
            mStatus = PTR_STATUS_INIT;
            mAutoScrollRefreshTag = 0;
            return true;
        }
        return false;
    }

    /**
     *  下拉滚动强制完成停止
     */
    protected void onPtrScrollAbort() {
        if (mCurrentPos > 0 && mAutoScrollRefreshTag > 0) {
        	// 自动释放
            onRelease(true);
        }
    }

    /**
     * 下拉刷新滚动完成
     */
    protected void onPtrScrollFinish() {
        if (mCurrentPos > 0 && mAutoScrollRefreshTag > 0) {
            onRelease(true);
        }
    }

    private boolean frameIsNotMoved() {
        return mCurrentPos == POS_START;
    }

    /**
     * Call this when data is loaded
     */
    final public void refreshComplete() {

        if (mRefreshCompleteHook != null) {
            mRefreshCompleteHook.reset();
        }

        int delay = (int) (mLoadingMinTime - (System.currentTimeMillis() - mLoadingStartTime));
        if (delay <= 0) {
            performRefreshComplete();
        } else {
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    performRefreshComplete();
                }
            }, delay);
        }
    }

    private void performRefreshComplete() {
        mStatus = PTR_STATUS_COMPLETE;

        // if is auto refresh do nothing, wait scroller stop
        if (mScrollChecker.mIsRunning && mAutoScrollRefreshTag > 0) {
            // do nothing
            return;
        }

        notifyUIRefreshComplete(false);
    }

    /**
     * 通知UI刷新完成
     * @param ignoreHook
     */
    private void notifyUIRefreshComplete(boolean ignoreHook) {
        /**
         * after hook operation is done, will call {@link #notifyUIRefreshComplete} and ignore hook
         */
        if (mCurrentPos != POS_START && !ignoreHook && mRefreshCompleteHook != null) {

            mRefreshCompleteHook.takeOver();
            return;
        }
        
        if (mPtrUIHandlerHolder.hasHandler()) {
            mPtrUIHandlerHolder.onUIRefreshComplete(this);
        }
        
        // 完成之后, 滚回到顶部
        tryScrollBackToTopAfterComplete();
        
        tryToNotifyReset();
    }
    

    public void autoRefresh() {
        autoRefresh(true, mDurationToCloseHeader);
    }

    public void autoRefresh(boolean atOnce) {
        autoRefresh(atOnce, mDurationToCloseHeader);
    }

    public void autoRefresh(boolean atOnce, int duration) {

        if (mStatus != PTR_STATUS_INIT) {
            return;
        }

        mAutoScrollRefreshTag = atOnce ? STATUS_AUTO_SCROLL_AT_ONCE : STATUS_AUTO_SCROLL_LATER;

        mStatus = PTR_STATUS_PREPARE;
        if (mPtrUIHandlerHolder.hasHandler()) {
            mPtrUIHandlerHolder.onUIRefreshPrepare(this);
        }
        mScrollChecker.tryToScrollTo(mOffsetToRefresh, duration);
        if (atOnce) {
            mStatus = PTR_STATUS_LOADING;
            performRefresh();
        }
    }

    /**
     * It's useful when working with viewpager.
     *
     * @param disable
     */
    // 这是可用的--当与ViewPager一起使用时
    public void disableWhenHorizontalMove(boolean disable) {
        mDisableWhenHorizontalMove = disable;
    }

    /**
     * loading will last at least for so long
     *
     * @param time
     */
    public void setLoadingMinTime(int time) {
        mLoadingMinTime = time;
    }

    /**
     * It's useful when you want to intercept event while moving the frame
     *
     * @param yes
     */
    public void setInterceptEventWhileWorking(boolean yes) {
        mInterceptEventWhileWorking = yes;
    }

    public View getContentView() {
        return mContent;
    }

    public void setPtrHandler(PtrHandler ptrHandler) {
        mPtrHandler = ptrHandler;
    }

    public void addPtrUIHandler(PtrUIHandler ptrUIHandler) {
        PtrUIHandlerHolder.addHandler(mPtrUIHandlerHolder, ptrUIHandler);
    }

    public void removePtrUIHandler(PtrUIHandler ptrUIHandler) {
        mPtrUIHandlerHolder = PtrUIHandlerHolder.removeHandler(mPtrUIHandlerHolder, ptrUIHandler);

    }

    public float getResistance() {
        return mResistance;
    }

    public void setResistance(float resistance) {
        mResistance = resistance;
    }

    public float getDurationToClose() {
        return mDurationToClose;
    }

    public void setDurationToClose(int duration) {
        mDurationToClose = duration;
    }

    public long getDurationToCloseHeader() {
        return mDurationToCloseHeader;
    }

    public void setDurationToCloseHeader(int duration) {
        mDurationToCloseHeader = duration;
    }

    public void setRatioOfHeaderHeightToRefresh(float ratio) {
        mRatioOfHeaderHeightToRefresh = ratio;
        mOffsetToRefresh = (int) (mHeaderHeight * mRatioOfHeaderHeightToRefresh);
    }

    public int getOffsetToRefresh() {
        return mOffsetToRefresh;
    }

    public void setOffsetToRefresh(int offset) {
        mOffsetToRefresh = offset;
    }

    public float getRatioOfHeaderToHeightRefresh() {
        return mRatioOfHeaderHeightToRefresh;
    }

    public boolean isKeepHeaderWhenRefresh() {
        return mKeepHeaderWhenRefresh;
    }

    public void setKeepHeaderWhenRefresh(boolean keepOrNot) {
        mKeepHeaderWhenRefresh = keepOrNot;
    }

    public boolean isPullToRefresh() {
        return mPullToRefresh;
    }

    public void setPullToRefresh(boolean pullToRefresh) {
        mPullToRefresh = pullToRefresh;
    }

    public View getHeaderView() {
        return mHeaderView;
    }

    /**
     * 设置头部
     * @param header
     */
    public void setHeaderView(View header) {
        if (mHeaderView != null && header != null && mHeaderView != header) {
            removeView(mHeaderView);
        }
        ViewGroup.LayoutParams lp = header.getLayoutParams();
        if (lp == null) {
            lp = new LayoutParams(-1, -2);
            header.setLayoutParams(lp);
        }
        mHeaderView = header;
        addView(header);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    @Override
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void sendCancelEvent() {
        MotionEvent e = MotionEvent.obtain(mDownEvent.getDownTime(), mDownEvent.getEventTime() + ViewConfiguration.getLongPressTimeout(), MotionEvent.ACTION_CANCEL, mDownEvent.getX(), mDownEvent.getY(), mDownEvent.getMetaState());
        dispatchTouchEventSupper(e);
    }

    private void sendDownEvent() {
        final MotionEvent last = mLastMoveEvent;
        MotionEvent e = MotionEvent.obtain(last.getDownTime(), last.getEventTime(), MotionEvent.ACTION_DOWN, last.getX(), last.getY(), last.getMetaState());
        dispatchTouchEventSupper(e);
    }

    /**
     * 
     * @author xiaobu
     */
    public static class LayoutParams extends MarginLayoutParams {

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(MarginLayoutParams source) {
            super(source);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }
    }
    
    // 滚动检测--实现Runnable接口
    class ScrollChecker implements Runnable {
    	// 上次的滑动Y轴的值
        private int mLastFlingY;
        
        // 滑动帮助类
        private Scroller mScroller;
        // 是否正在运行
        private boolean mIsRunning = false;
        
        private int mStart;
        private int mTo;

        public ScrollChecker() {
            mScroller = new Scroller(getContext());
        }

        public void run() {
        	// 是否结束？
            boolean finish = !mScroller.computeScrollOffset() || mScroller.isFinished();
            // 得到当前的Y坐标
            int curY = mScroller.getCurrY();
            // 滑动的距离 = 当前的Y值 - 上次滑动到的Y值
            int deltaY = curY - mLastFlingY;
            
            // 如果没完成
            if (!finish) {
                mLastFlingY = curY;
                // 移动距离
                movePos(deltaY);
                // the Runnable to be added to the message queue
                post(this);
            } else {
                finish();
            }
        }

        private void finish() {
            reset();
            // 下拉刷新滚动完成
            onPtrScrollFinish();
        }

        private void reset() {
            mIsRunning = false;
            mLastFlingY = 0;
            // Removes the specified Runnable from the message queue
            removeCallbacks(this);
        }

        /**
         * 滚动的时候，强制停止
         */
        public void abortIfWorking() {
        	// 如果正在运行
            if (mIsRunning) {
            	// 如果mScroller没停止
                if (!mScroller.isFinished()) {
                	// 强制停止
                    mScroller.forceFinished(true);
                }
                
                // 下拉滚动也强制完成
                onPtrScrollAbort();
                // 复位
                reset();
            }
        }

        // 试着去滚动
        public void tryToScrollTo(int to, int duration) {
            if (mCurrentPos == to) {
                return;
            }
            
            mStart = mCurrentPos;
            mTo = to;
            int distance = to - mStart;
            
            removeCallbacks(this);
            
            mLastFlingY = 0;
            mScroller = new Scroller(getContext());
            mScroller.startScroll(0, 0, 0, distance, duration);
            post(this);
            mIsRunning = true;
        }
        
    }
}