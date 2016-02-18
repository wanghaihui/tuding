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

public class DoubleSalaryNumberDialog extends Dialog {

	private TextView mTitleView;
	private NumberPicker mNumberPickerBegin;
	private NumberPicker mNumberPickerEnd;
	
	private String mTitle;
	private int maxValue = 50;
	private int minValue = 1;
	private int begin = 1;
	private int end = 1;
	
	public DoubleSalaryNumberDialog(Context context) {
		super(context, R.style.dialog);
	}


	
	private OnValueChangeListener onBeginValueChangeListener = new OnValueChangeListener() {
		
		@Override
		public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
			begin = picker.getValue();
			if(newVal > mNumberPickerEnd.getValue()){
				mNumberPickerEnd.setValue(newVal);
				end = newVal;
			}
		}
	};
	private OnValueChangeListener onEndValueChangeListener = new OnValueChangeListener() {
		
		@Override
		public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
			
			if(newVal < mNumberPickerBegin.getValue()){
				picker.setValue(oldVal);
			}
			end = picker.getValue();
		}
	};


	
	public int getBegin() {
		return begin;
	}

	public void setBegin(int begin) {
		this.begin = begin;
	}

	public int getEnd() {
		return end;
	}

	public void setEnd(int end) {
		this.end = end;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_number_salary_double_picker);
		mTitleView = (TextView) findViewById(R.id.pick_dialog_title);
		
		// 显示的信息
		String[] currentScoreArray = new String[maxValue];
		for (int i = 0; i < maxValue; i++) {
		    currentScoreArray[i] = (i+1)+ "  " + "K";
		}
		mNumberPickerBegin = (NumberPicker) findViewById(R.id.picker_begin);
		mNumberPickerBegin.setMaxValue(maxValue);
		mNumberPickerBegin.setMinValue(minValue);
		mNumberPickerBegin.setValue(begin);
		mNumberPickerBegin.setDisplayedValues(currentScoreArray);
		mNumberPickerBegin.setOnValueChangedListener(onBeginValueChangeListener);
	
		mNumberPickerEnd = (NumberPicker) findViewById(R.id.picker_end);
		
		mNumberPickerEnd.setMaxValue(maxValue);
		mNumberPickerEnd.setMinValue(minValue);
		mNumberPickerEnd.setValue(end);
		mNumberPickerEnd.setDisplayedValues(currentScoreArray);
		mNumberPickerEnd.setOnValueChangedListener(onEndValueChangeListener);
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
