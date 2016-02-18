package com.xiaobukuaipao.vzhi.view;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xiaobukuaipao.vzhi.util.DisplayUtil;

public class SingleLineTextView extends LinearLayout {


	private Drawable background;
	private List<String> linesText;
	private int textColor;
	private boolean refreshFlag = false;
	
	private Handler mHandler = new Handler();
	
	private int index = 0;
	private int childCountW = 0;
	
	private Runnable runnable = new Runnable() {
		
		@SuppressWarnings("deprecation")
		@Override
		public void run() {
			if (linesText != null && !linesText.isEmpty()) {
				removeAllViews();
				
				childCountW = 0 ;
				index = 0;
				
				LinearLayout l = new LinearLayout(getContext());
				l.setOrientation(LinearLayout.HORIZONTAL);
				LayoutParams lpi = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
				
				addView(l, index, lpi);
				
				for (int i = 0; i < linesText.size() ; i++) {
					TextView tv = new TextView(getContext());
					
					if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN){
						tv.setBackground(background);
					}else{
						tv.setBackgroundDrawable(background);
					}
					
					tv.setText(linesText.get(i));
					tv.setSingleLine();
					tv.setTextColor(textColor);
					LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
					lp.rightMargin = 8;
					
					// MeasureSpec.UNSPECIFIED -- 表示开发人员可以将视图按照自己的意愿设置成任意的大小，没有任何限制
					tv.measure(tv.getWidth(),  MeasureSpec.UNSPECIFIED);
					
					childCountW += tv.getMeasuredWidth();
					
					if (childCountW > getWidth() - DisplayUtil.dip2px(getContext(), 55)) {
						/*childCountW = tv.getMeasuredWidth();
						index++;
						l = null;
						l = new LinearLayout(getContext());
						l.setOrientation(LinearLayout.HORIZONTAL);
						lpi.topMargin = 8;
						addView(l, index, lpi);*/
						break;
					}
					
					
					l.addView(tv, lp);
				}
			}
		}
	};
	@SuppressLint("NewApi")
	public SingleLineTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setOrientation(LinearLayout.VERTICAL);
		initUiAndDate();
	}

	public SingleLineTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setOrientation(LinearLayout.VERTICAL);
		initUiAndDate();
	}
	
	public SingleLineTextView(Context context, List<String> linesText, int resid) {
		super(context);
		this.linesText = linesText;
		setBackgroundResource(resid);
		setOrientation(LinearLayout.VERTICAL);
		initUiAndDate();
	}

	@Override
	public void invalidate() {
		super.invalidate();
		refreshFlag = true;
	}
	
	private void initUiAndDate() {
		getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			// 全局布局改变的时候
			@Override
			public void onGlobalLayout() {
				if (refreshFlag) {
					// Remove any pending posts of Runnable r that are in the message queue
					mHandler.removeCallbacks(runnable);
					// Causes the Runnable r to be added to the message queue
					mHandler.post(runnable);
					
					refreshFlag = false;
				}
			}
		});
	}
	
	
	public int getTextColor() {
		return textColor;
	}

	public void setTextColor(int textColor) {
		this.textColor = textColor;
	}

	public void setLinesText(List<String> linesText) {
		this.linesText = linesText;
		// 直接调用invalidate()方法, 请求重新draw(), 但只会绘制调用者本身
		// 用来刷新View的, 必须是在UI线程中进行工作
		invalidate();
	}



	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	public List<String> getLinesText() {
		return linesText;
	}

	@Override
	public void setBackground(Drawable background) {
		this.background = background;
	}

	@Override
	public void setBackgroundResource(int resid) {
		if (resid <= 0) {
			return;
		}
		if (background == getResources().getDrawable(resid)) {
			return;
		}
		setBackground(getResources().getDrawable(resid));
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);

	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
	}

}
