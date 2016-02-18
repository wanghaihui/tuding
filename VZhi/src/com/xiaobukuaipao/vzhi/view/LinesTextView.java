package com.xiaobukuaipao.vzhi.view;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

public class LinesTextView extends LinearLayout {


	private int background;
	private List<String> linesText;
	private int textColor;
	private int textSize = -1;
	private int type = TypedValue.COMPLEX_UNIT_SP;
	
	private Handler mHandler = new Handler();
	
	private int index = 0;
	private int childCountW = 0;
	private int paddingLeft = 3;
	private int paddingRight = 3;
	private int paddingTop = 3;
	private int paddingBottom = 3;
	
	private int marginRight = 15;
	private int marginTop = 15;
	private boolean singleLine = false;
	
	private int offset = 30;
	private boolean hasPadding = false;
	private Runnable runnable = new Runnable() {
		
		@Override
		public void run() {
			setEnabled(true);
			if (linesText != null && !linesText.isEmpty()) {
				removeAllViews();
				childCountW = 0 ;
				index = 0;
				LinearLayout l = new LinearLayout(getContext());
				l.setOrientation(LinearLayout.HORIZONTAL);
				LayoutParams lpi = new LayoutParams(LayoutParams.MATCH_PARENT,
						LayoutParams.WRAP_CONTENT);
				
				addView(l, index, lpi);
				
				for (int i = 0; i < linesText.size() ; i++) {
					TextView tv = new TextView(getContext());
					tv.setBackgroundResource(background);
					if(hasPadding)
						tv.setPadding(paddingLeft, paddingTop, paddingRight,paddingBottom);
					tv.setText(linesText.get(i));
					tv.setSingleLine();
					tv.setTextColor(textColor);
					if(textSize != -1){
						tv.setTextSize(type,textSize);
					}
					LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT,
							LayoutParams.WRAP_CONTENT);
					lp.rightMargin = marginRight;
					tv.measure(tv.getWidth(),  MeasureSpec.UNSPECIFIED);
					childCountW += (tv.getMeasuredWidth() + marginRight);
					
					if (childCountW > getWidth() - offset) {
						if(singleLine){
							return;
						}
						//新加第下一行
						childCountW = tv.getMeasuredWidth() + marginRight;
						index++;
						l = null;
						l = new LinearLayout(getContext());
						l.setOrientation(LinearLayout.HORIZONTAL);
						lpi.topMargin = marginTop;
						addView(l, index, lpi);
					}
					tv.setEnabled(true);
					tv.setOnClickListener(new OnClick(i));
					l.addView(tv, lp);
				}
			}
		}
	};
	private OnItemClickListener itemClickListener = null;
	
	@SuppressLint("NewApi")
	public LinesTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setOrientation(LinearLayout.VERTICAL);
		initUiAndDate();
	}

	public LinesTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setOrientation(LinearLayout.VERTICAL);
		initUiAndDate();
	}
	
	public LinesTextView(Context context, List<String> linesText, int resid) {
		super(context);
		this.linesText = linesText;
		setBackgroundResource(resid);
		setOrientation(LinearLayout.VERTICAL);
		initUiAndDate();
	}
	private void initUiAndDate() {
		getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			// 全局布局改变的时候
			@SuppressWarnings("deprecation")
			@Override
			public void onGlobalLayout() {
				getViewTreeObserver().removeGlobalOnLayoutListener(this);
				// Remove any pending posts of Runnable r that are in the message queue
				mHandler.removeCallbacks(runnable);
				// Causes the Runnable r to be added to the message queue
				mHandler.post(runnable);
			}
		});
	}
	public void setTextPadding(int left, int top, int right, int bottom) {
		paddingLeft = left;
		paddingRight = right;
		paddingTop = top;
		paddingBottom = bottom;
		hasPadding = true;
	}
	
	/**
	 * 设置右边距和上边距
	 * 
	 * @param top
	 * @param right
	 */
	public void setTextMargin(int top, int right){
		marginRight = right;
		marginTop = top;
	}
	public int getTextColor() {
		return textColor;
	}

	public void setTextColor(int textColor) {
		this.textColor = textColor;
	}
	public void setTextSize(int textSize) {
		this.textSize = textSize;
	}
	public void setTextSize(int type , int textSize) {
		this.textSize = textSize;
		this.type = type;
	}
	public void setOffset(int offset) {
		this.offset = offset;
	}

	public void setLinesText(List<String> linesText) {
		this.linesText = linesText;
		// 直接调用invalidate()方法, 请求重新draw(), 但只会绘制调用者本身
		// 用来刷新View的, 必须是在UI线程中进行工作
		requestLayout();
		initUiAndDate();
	}


	public List<String> getLinesText() {
		return linesText;
	}


	@Override
	public void setBackgroundResource(int resid) {
		if (resid <= 0) {
			return;
		}
		if (background == resid) {
			return;
		}
		
		background = resid;
	}

	public boolean isSingleLine() {
		return singleLine;
	}

	public void setSingleLine(boolean singleLine) {
		this.singleLine = singleLine;
	}

	public OnItemClickListener getItemClickListener() {
		return itemClickListener;
	}

	public void setItemClickListener(OnItemClickListener itemClickListener) {
		this.itemClickListener = itemClickListener;
	}
	
	class OnClick implements OnClickListener{

		int i;
		public OnClick(int i){
			this.i = i;
		}
		@Override
		public void onClick(View v) {
			if(itemClickListener != null)
				itemClickListener.onItemClick(null, v,  i, v.getId());
		}
	}
	
}
