package com.xiaobukuaipao.vzhi.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;

import com.xiaobukuaipao.vzhi.R;

public class EditTextWithDel extends EditText implements OnFocusChangeListener {
	@SuppressWarnings("unused")
	private Drawable imgInable;
	private Drawable imgAble;
	private Drawable imgSearch;
    private Context mContext;

    public EditTextWithDel(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public EditTextWithDel(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        init();
    }

    public EditTextWithDel(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }
    
    private void init() {
        imgInable = mContext.getResources().getDrawable(R.drawable.delete_gray);
        // imgAble = mContext.getResources().getDrawable(R.drawable.delete);
        imgAble = mContext.getResources().getDrawable(R.drawable.delete_gray);
        imgSearch = mContext.getResources().getDrawable(R.drawable.search_bar_icon_normal);

        addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void afterTextChanged(Editable s) {
                setDrawable();
            }
        });
        
        setDrawable();
    }

    private void setDrawable() {
        if (length() < 1)
            setCompoundDrawablesWithIntrinsicBounds(imgSearch, null, null, null);
        else {
            setCompoundDrawablesWithIntrinsicBounds(imgSearch, null, imgAble, null);
        	setFocusable(true);   
        	setFocusableInTouchMode(true);   
        	requestFocus(); 
        }
    }
    
    @SuppressLint("ClickableViewAccessibility")
	@Override
    public boolean onTouchEvent(MotionEvent event) {
        /*if (imgAble != null && event.getAction() == MotionEvent.ACTION_UP) {
            int eventX = (int) event.getRawX();
            int eventY = (int) event.getRawY();
            
            Rect rect = new Rect();
            getGlobalVisibleRect(rect);
            rect.left = rect.right - 50;
            if(rect.contains(eventX, eventY))
                setText("");
        }*/
    	
    	if(imgAble != null && event.getAction() == MotionEvent.ACTION_UP) {
    		int x = (int) event.getX() ;
    		//判断触摸点是否在水平范围内
    		boolean isInnerWidth = (x > (getWidth() - getTotalPaddingRight())) &&
    		(x < (getWidth() - getPaddingRight()));
    		//获取删除图标的边界，返回一个Rect对象
    		Rect rect = imgAble.getBounds();
    		//获取删除图标的高度
    		int height = rect.height();
    		int y = (int) event.getY();
    		//计算图标底部到控件底部的距离
    		int distance = (getHeight() - height) /2;
    		//判断触摸点是否在竖直范围内(可能会有点误差)
    		//触摸点的纵坐标在distance到（distance+图标自身的高度）之内，则视为点中删除图标
    		boolean isInnerHeight = (y > distance) && (y < (distance + height));
    		if(isInnerWidth && isInnerHeight) {
    			setText("");
    		}
    	}
    	
        return super.onTouchEvent(event);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }
    
    @Override
    public void onFocusChange(View v, boolean hasFocus) {
	    if(hasFocus) {
	    	setDrawable();
	    }else {
	    	setCompoundDrawablesWithIntrinsicBounds(imgSearch, null, null, null);
	    }
    }
    
    
}
