package com.xiaobukuaipao.vzhi.view;

import android.content.Context;
import android.text.Layout;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.TextView;

/**
 * A textview containing clickable links.
 */
public class LinkedTextView extends TextView {
	public static final String TAG = LinkedTextView.class.getSimpleName();
	
	public LinkedTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		TextView widget = this;
		Object text = widget.getText();
		if (text instanceof Spanned) {
			Spanned buffer = (Spanned) text;
			
			int action = event.getAction();
			
			if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_DOWN) {
				int x = (int) event.getX();
				Log.i(TAG, "event getX : " + x);
				int y = (int) event.getY();
				Log.i(TAG, "event getY : " + y);
				
				x -= widget.getTotalPaddingLeft();
				Log.i(TAG, "view getTotalPaddingLeft : " + widget.getTotalPaddingLeft());
				Log.i(TAG, "x - view getTotalPaddingLeft :" + x);
				y -= widget.getTotalPaddingTop();
				Log.i(TAG, "view getTotalPaddingTop : " + widget.getTotalPaddingTop());
				Log.i(TAG, "y - view getTotalPaddingTop : " + y);
				
				x += widget.getScrollX();
				Log.i(TAG, "view getScrollX : " + widget.getScrollX());
				Log.i(TAG, "x + view getScrollX : " + x);
				y += widget.getScrollY();
				Log.i(TAG, "view getScrollY : " + widget.getScrollY());
				Log.i(TAG, "y + view getScrollY : " + y);
				
				Layout layout = widget.getLayout();
				int line = layout.getLineForVertical(y);
				Log.i(TAG, "layout line : " + line);
				int off = layout.getOffsetForHorizontal(line, x);
				Log.i(TAG, "layout off : " + off);
				
				ClickableSpan[] link = buffer.getSpans(off, off, ClickableSpan.class);
				
				if (x > layout.getLineRight(line)) {
					// Don't call the span
				} else if  (link.length != 0) {
					if (action == MotionEvent.ACTION_UP) {
						link[0].onClick(widget);
					}
					return true;
				}
				
			}
		}
		
		return super.onTouchEvent(event);
	}
	
	
	
}
