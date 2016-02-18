package com.xiaobukuaipao.vzhi.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.xiaobukuaipao.vzhi.R;

public class A5EditText extends EditText {
	private Drawable imgInable;
	private Drawable imgAble;
	private Context mContext;
	private boolean isError;
	private boolean isPassword;
	private boolean isChild;

	public boolean isPassword() {
		return isPassword;
	}

	public void setPassword(boolean isPassword) {
		this.isPassword = isPassword;
		if (isPassword) {
			setInputType(InputType.TYPE_CLASS_TEXT| InputType.TYPE_TEXT_VARIATION_PASSWORD);
		}
	}

	public boolean isChild() {
		return isChild;
	}

	public void setChild(boolean isChild) {
		this.isChild = isChild;
	}

	public boolean isError() {
		return isError;
	}

	public void setError(boolean isError) {
		this.isError = isError;
	}

	public A5EditText(Context context) {
		super(context);
		mContext = context;
	}

	public A5EditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		TypedArray attributesArray = context.obtainStyledAttributes(attrs,
				R.styleable.A5EditText);
		isError = attributesArray.getBoolean(R.styleable.A5EditText_isError,
				false);
		isChild = attributesArray.getBoolean(R.styleable.A5EditText_isChild,
				false);
		attributesArray.recycle();
		initUI();
	}

	public A5EditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;

		TypedArray attributesArray = context.obtainStyledAttributes(attrs,
				R.styleable.A5EditText, defStyle, 0);
		isError = attributesArray.getBoolean(R.styleable.A5EditText_isError,
				false);
		isChild = attributesArray.getBoolean(R.styleable.A5EditText_isChild,
				false);
		attributesArray.recycle();

		initUI();
	}

	private void initUI() {
		setHeight((int) getResources().getDimension(R.dimen.text_field_height));
		setPadding(0,
				(int) getResources().getDimension(R.dimen.text_field_padding),
				0, (int) getResources()
						.getDimension(R.dimen.text_field_padding));
		setDrawable();
		setSelection(0);
		drawBackgroundDrawable();
		imgAble = mContext.getResources().getDrawable(R.drawable.delete_gray);
		
		addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				setDrawable();
				drawBackgroundDrawable();
			}
		});
		setFocusable(true);
		// setFocusableInTouchMode(true);
		invalidate();

	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (onDrawListener != null) {
			onDrawListener.onDraws();
		}
		super.onDraw(canvas);
		drawBackgroundDrawable();

	}

	public OnDrawListener onDrawListener;

	public interface OnDrawListener {
		void onDraws();
	}

	private void drawBackgroundDrawable() {
		if (!isChild) {
			if (isError) {
				setBackgroundResource(R.drawable.textfield_red);
			} else {
				if (isFocused()) {
					setBackgroundResource(R.drawable.textfield_blue);
				} else {
					setBackgroundResource(R.drawable.textfield_grey);
				}
			}

		} else {

		}
	}

	private void setDrawable() {
		if (!isPassword) {
			if (length() < 1)
				setCompoundDrawablesWithIntrinsicBounds(null, null, imgInable,
						null);
			else
				setCompoundDrawablesWithIntrinsicBounds(null, null, imgAble,
						null);
		}

		setFocusable(true);
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (imgAble != null && event.getAction() == MotionEvent.ACTION_UP) {
			int eventX = (int) event.getRawX();
			int eventY = (int) event.getRawY();
			Rect rect = new Rect();
			getGlobalVisibleRect(rect);
			rect.left = rect.right - 50;
			if (rect.contains(eventX, eventY))
				if (!isPassword) {
					setText("");
				}
			invalidate();
		}
		return super.onTouchEvent(event);
	}

	@Override
	protected void onFocusChanged(boolean gainFocus, int direction,
			Rect previouslyFocusedRect) {

		if (gainFocus) {
			InputMethodManager imm = (InputMethodManager) getContext()
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			if (isFocused()) {
				imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
			}
		}
		super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
	}
}
