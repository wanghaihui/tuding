package com.xiaobukuaipao.vzhi.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.xiaobukuaipao.vzhi.R;
import com.xiaobukuaipao.vzhi.util.DisplayUtil;

public class SideBar extends View {

	private OnTouchingLetterChangedListener onTouchingLetterChangedListener;

	public static String[] alphabet = { "A", "B", "C", "D", "E", "F", "G", "H",
			"I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U",
			"V", "W", "X", "Y", "Z", "#" };

	private TextView mTextDialog;
	private int choose = -1;

	private Paint paint = new Paint();

	private int height;
	
	private int divider = 0;
	public SideBar(Context context) {
		super(context);
		initUiAndData();
	}

	public SideBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		initUiAndData();
	}

	public SideBar(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initUiAndData();
	}

	private void initUiAndData() {
		setBackgroundColor(0);
	}
	public void setTextDialog(TextView mTextDialog) {
		this.mTextDialog = mTextDialog;
	}

	public void setOnTouchingLetterChangedListener(
			OnTouchingLetterChangedListener onTouchingLetterChangedListener) {
		this.onTouchingLetterChangedListener = onTouchingLetterChangedListener;
	}

	@TargetApi(16)
	public boolean dispatchTouchEvent(MotionEvent event) {
		// 得到当前的Action
		final int action = event.getAction();
		// 得到Y坐标
		final float y = event.getY();
		// 当前的字母位置
		final int position = (int) (y / getHeight() * alphabet.length);
		// 老的位置
		final int oldChoose = choose;
		// 监听器
		final OnTouchingLetterChangedListener listener = onTouchingLetterChangedListener;

		switch (action) {
		case MotionEvent.ACTION_UP:

			setBackgroundColor(0);
			choose = -1;
			invalidate();
			if (mTextDialog != null) {
				mTextDialog.setVisibility(View.INVISIBLE);
			}

			break;
		default:
			// 设置背景资源
			setBackgroundResource(R.drawable.sidebar_background_default);
			if (oldChoose != position) {
				if (position >= 0 && position < alphabet.length) {
					// 监听器实现
					if (listener != null) {
						listener.onTouchingLetterChanged(alphabet[position]);
					}

					if (mTextDialog != null) {
						mTextDialog.setText(alphabet[position]);
						mTextDialog.setVisibility(View.VISIBLE);
					}

					choose = position;
					// 刷新
					invalidate();
				}
			}
			break;
		}

		return true;
	}
	@Override
	protected void onDraw(Canvas canvas) {
		height = getHeight();
		int width = getWidth();
		int singleHeight = height / alphabet.length ;
		singleHeight -= divider;
		for (int i = 0; i < alphabet.length; i++) {
			paint.setColor(Color.rgb(0x66, 0x66, 0x66));
			paint.setAntiAlias(true);
			// 字号大小12sp
			paint.setTextSize(DisplayUtil.sp2px(getContext(), 12));

			// 如果i是选中的
			if (i == choose) {
				paint.setFakeBoldText(true);
			}

			float xPos = (width - paint.measureText(alphabet[i])) / 2;
			float yPos = singleHeight * i + singleHeight;
			canvas.drawText(alphabet[i], xPos,yPos, paint);
			paint.reset();
			
			super.onDraw(canvas);
		}
	}

	// 字母是否改变的监听器接口
	public interface OnTouchingLetterChangedListener {
		public void onTouchingLetterChanged(String s);
	}

}
