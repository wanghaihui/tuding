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
import com.xiaobukuaipao.vzhi.widget.NumberPicker.Formatter;
import com.xiaobukuaipao.vzhi.widget.NumberPicker.OnValueChangeListener;

public class SimpleSalaryNumberDialog extends Dialog {

	private TextView mTitleView;
	private NumberPicker mNumberPicker;
	
	private String mTitle;

	public SimpleSalaryNumberDialog(Context context) {
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
		setContentView(R.layout.dialog_number_salary_picker);
		mTitleView = (TextView) findViewById(R.id.pick_dialog_title);
		
		mNumberPicker = (NumberPicker) findViewById(R.id.pick_dialog_content);
		mNumberPicker.setMaxValue(50);
		mNumberPicker.setMinValue(1);
		mNumberPicker.setOnValueChangedListener(onValueChangeListener);
		mNumberPicker.setFormatter(new Formatter() {
			
			@Override
			public String format(int value) {
				return value + " K";
			}
		});
	}

	public String getTitle() {
		return mTitle;
	}

	public void setTitle(String mTitle) {
		this.mTitle = mTitle;
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
