package com.xiaobukuaipao.vzhi.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.TextView;

import com.xiaobukuaipao.vzhi.R;
import com.xiaobukuaipao.vzhi.widget.NumberPicker.OnValueChangeListener;

public class SimpleNumberDialog extends Dialog {

	private TextView mTitleView;
	private NumberPicker mNumberPicker;
	
	private String mTitle;

	private String[] mStrings;
	
	private int maxValue;
	private int minValue;
	private int value;

	public SimpleNumberDialog(Context context) {
		super(context, R.style.dialog);
	}

	private OnValueChangeListener onValueChangeListener;

	public NumberPicker getNumberPicker() {
		return mNumberPicker;
	}

	public void setmNuberPicker(NumberPicker mNumberPicker) {
		this.mNumberPicker = mNumberPicker;
	}

	public OnValueChangeListener getOnValueChangeListener() {
		return onValueChangeListener;
	}

	public void setOnValueChangeListener(
			OnValueChangeListener onValueChangeListener) {
		this.onValueChangeListener = onValueChangeListener;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_number_picker);
		mTitleView = (TextView) findViewById(R.id.pick_dialog_title);
		mNumberPicker = (NumberPicker) findViewById(R.id.pick_dialog_content);
		if(mStrings != null){
			mNumberPicker.setDisplayedValues(mStrings);
		}
		mNumberPicker.setMaxValue(maxValue);
		mNumberPicker.setMinValue(minValue);
		mNumberPicker.setOnValueChangedListener(onValueChangeListener);
		mNumberPicker.setValue(value);

		
	}

	public int getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(int maxValue) {
		this.maxValue = maxValue;
	}

	public int getMinValue() {
		return minValue;
	}

	public void setMinValue(int minValue) {
		this.minValue = minValue;
	}

	public String getTitle() {
		return mTitle;
	}

	public void setTitle(String mTitle) {
		this.mTitle = mTitle;
	}
	public void setValue(int value) {
		this.value = value;
	}
	public int getValue() {
		return value;
	}
	

	public String[] getStrings() {
		return mStrings;
	}

	public void setStrings(String... mStrings) {
		this.mStrings = mStrings;
	}


	@Override
	public void show() {
		super.show();
		if(mTitle != null)
			mTitleView.setText(mTitle);
		getWindow().setGravity(Gravity.CENTER); 
		WindowManager windowManager = getWindow().getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		DisplayMetrics outMetrics = new DisplayMetrics();
		display.getMetrics(outMetrics);
		WindowManager.LayoutParams dialogLp = getWindow().getAttributes();
		dialogLp.width = outMetrics.widthPixels - 100;
		getWindow().setAttributes(dialogLp);
	}
}
