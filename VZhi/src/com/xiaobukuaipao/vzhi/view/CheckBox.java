package com.xiaobukuaipao.vzhi.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.xiaobukuaipao.vzhi.R;
import com.xiaobukuaipao.vzhi.util.A5Utils;


public class CheckBox extends CustomView {

	private Check checkView;

	private boolean idPressed = false;
	private boolean isChecked = false;
	
	private boolean outsideChecked = false;

	private OnCheckListener onCheckListener;

	public CheckBox(Context context, AttributeSet attrs) {
		super(context, attrs);
		setAttributes(attrs);
	}

	@Override
	protected void onInitDefaultValues() {
		minWidth = 48;
		minHeight = 48;
		backgroundColor = Color.parseColor("#4CAF50");// default color
		backgroundResId = R.drawable.background_checkbox;
	}
	
	@Override
	protected void setAttributes(AttributeSet attrs) {
		super.setAttributes(attrs);
		boolean isChecked = attrs.getAttributeBooleanValue(MATERIALDESIGNXML, "checked", false);
		if (isChecked) {
			post(new Runnable() {

				@Override
				public void run() {
					setChecked(true);
					setPressed(false);
					changeBackgroundColor(getResources().getColor(android.R.color.transparent));
				}
			});
		}

		float size = 24;
		String checkBoxSize = attrs.getAttributeValue(MATERIALDESIGNXML, "checkBoxSize");
		if (checkBoxSize != null) {
			size = A5Utils.dipOrDpToFloat(checkBoxSize);
		}
		checkView = new Check(getContext());
		setCheckBoxParams(size);
		addView(checkView);
	}

	private void setCheckBoxParams(float size) {
		RelativeLayout.LayoutParams params = new LayoutParams(
				A5Utils.dpToPx(size, getResources()), A5Utils.dpToPx(size, getResources()));
		params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
		checkView.setLayoutParams(params);
	}
	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (isEnabled()) {
			isLastTouch = true;
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				changeBackgroundColor((isChecked) ? makePressColor(70) : Color.parseColor("#446D6D6D"));
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				changeBackgroundColor(getResources().getColor(android.R.color.transparent));
				idPressed = false;
				if ((event.getX() <= getWidth() && event.getX() >= 0) && (event.getY() <= getHeight() && event.getY() >= 0)) {
					isLastTouch = false;
					if(!outsideChecked){
						isChecked = !isChecked;
						if (isChecked) {
//							step = 0;
						}
						checkView.changeBackground();
						if (onCheckListener != null)
							onCheckListener.onCheck(isChecked);
					}
				}
			}
		}
		return true;
	}
	Paint paint = new Paint();
	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (idPressed) {
			paint.setAntiAlias(true);
			paint.setColor((isChecked) ? makePressColor(70) : Color.parseColor("#446D6D6D"));
			canvas.drawCircle(getWidth() / 2, getHeight() / 2, getWidth() / 2, paint);
		}
		invalidate();
	}
	
	// Indicate step in check animation
//	private int step = 0;
//	private Rect src = new Rect();
//	private Rect dst = new Rect();
	// View that contains checkbox
	private class Check extends View {

		// 小妖精
//		private Bitmap sprite;

		@SuppressLint("DrawAllocation")
		public Check(Context context) {
			super(context);
			if (!isInEditMode()) {
				setBackgroundResource(R.drawable.checkbox_normal);
			}
//			sprite = BitmapFactory.decodeResource(context.getResources(), R.drawable.checkbox_pressed);
		}

		public void changeBackground() {
			if (isChecked) {
				setBackgroundResource(R.drawable.checkbox_pressed);
//				LayerDrawable layer = (LayerDrawable) getBackground();
//				// GradientDrawable 支持使用渐变色来绘制图形，通常可以用作Button或是背景图形
//				GradientDrawable shape = (GradientDrawable) layer.findDrawableByLayerId(R.id.shape_bacground);
//				shape.setColor(backgroundColor);
			} else {
				if (!isInEditMode()) {
					setBackgroundResource(R.drawable.checkbox_normal);
				}
			}
		}

//		@SuppressLint("DrawAllocation")
//		@Override
//		protected void onDraw(Canvas canvas) {
//			super.onDraw(canvas);
//
//			if (isChecked) {
//				if (step < 11){
//					step++;
//				}else{
//					return;
//				}
//			} else {
//				if (step >= 0){
//					step--;
//				}else{
//					changeBackground();
//					return;
//				}
//			}
//			Log.d("@@@", "step:" + step);
//			
//			src.set(40 * step, 0, (40 * step) + 40, 40);
//			dst.set(0, 0, this.getWidth() - 2, this.getHeight());
//			Log.d("@@@", "src:" + src + "   dst:" + dst);
//			if (!isInEditMode()) {
//				canvas.drawBitmap(sprite, src, dst, null);
//			}
//			invalidate();
//		}

	}

	private void changeBackgroundColor(int color) {
		if (!isInEditMode()) {
			LayerDrawable layer = (LayerDrawable) getBackground();
			GradientDrawable shape = (GradientDrawable) layer.findDrawableByLayerId(R.id.shape_bacground);
			shape.setColor(color);
		}
	}
	
	@Override
	public void setBackgroundColor(int color) {
		backgroundColor = color;
		if (isEnabled()) {
			 beforeBackground = backgroundColor;
		}
		changeBackgroundColor(getResources().getColor(android.R.color.transparent));
	}

	public void setChecked(boolean checked) {
		this.isChecked = checked;
		setPressed(false);
		changeBackgroundColor(getResources().getColor(android.R.color.transparent));
		if (checked) {
//			step = 0;
		}
//		if (checked) {
			checkView.changeBackground();
//		}
		if (onCheckListener != null)
			onCheckListener.onCheck(isChecked);
	}

	public boolean isChecked() {
		return isChecked;
	}
	
	public void setCheckBoxSize(float size) {
		setCheckBoxParams(size);
	}

	public void setOncheckListener(OnCheckListener onCheckListener) {
		this.onCheckListener = onCheckListener;
	}

	public interface OnCheckListener {
		public void onCheck(boolean isChecked);
	}

	public boolean isOutsideChecked() {
		return outsideChecked;
	}

	public void setOutsideChecked(boolean outsideChecked) {
		this.outsideChecked = outsideChecked;
	}

}
