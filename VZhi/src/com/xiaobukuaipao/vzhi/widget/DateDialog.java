package com.xiaobukuaipao.vzhi.widget;

import java.util.Calendar;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.xiaobukuaipao.vzhi.R;
import com.xiaobukuaipao.vzhi.widget.NumberPicker.Formatter;
import com.xiaobukuaipao.vzhi.widget.NumberPicker.OnValueChangeListener;

/**
 * 日期选择对话框
 * @author hongxin.bai
 *
 */
public class DateDialog extends Dialog implements OnClickListener{

	private NumberPicker mFrontYear;
	private NumberPicker mFrontMonth;
	private NumberPicker mBackYear;
	private NumberPicker mBackMonth;
	
	private int mFrontCurYearNum = Calendar.getInstance().get(Calendar.YEAR);
	private int mFrontYearNumMax =  mFrontCurYearNum;
	private int mFrontYearNumMin = mFrontCurYearNum - 30;
	
	private int mBackCurYearNum = Calendar.getInstance().get(Calendar.YEAR);
	private int mBackYearNumMax = mBackCurYearNum;
	@SuppressWarnings("unused")
	private int mBackYearNumMin = mFrontCurYearNum;//结束时间的最小值是　开始时间的最大值
	private int mBackYearToNow = mBackYearNumMax + 1;//至今
	
	private int mFrontCurMonthNum = Calendar.getInstance().get(Calendar.MONTH) + 1;
	private int mFrontMonthNumMax  = 12;
	private int mFrontMonthNumMin = 1;
	
	private int mBackCurMonthNum = Calendar.getInstance().get(Calendar.MONTH) + 1;
	private int mBackMonthNumMax  = 12;
	private int mBackMonthNumMin = 1;
	private int mBackMonthToNow = mBackMonthNumMax + 1;//至今
	
	private boolean mBackToNow = false;
	
	private View mFrontLayout;
	private View mBackLayout;
	private View mBtnBack;
	private View mBtnConfirm;


	public DateDialog(Context context, boolean cancelable,
			OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
	}

	public DateDialog(Context context, int theme) {
		super(context, theme);
	}

	public DateDialog(Context context) {
		super(context,R.style.dialog);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_date);
		
		mFrontLayout = findViewById(R.id.datePickerLayout);
		mBackLayout = findViewById(R.id.datePickerBackLayout);
		
		mFrontYear = (NumberPicker) findViewById(R.id.front_year);
		mFrontMonth = (NumberPicker) findViewById(R.id.front_month);
		
		mBackYear = (NumberPicker) findViewById(R.id.back_year);
		mBackMonth = (NumberPicker) findViewById(R.id.back_month);
		
		
		mFrontYear.setFormatter(new Formatter() {
			
			@Override
			public String format(int value) {
				
				
				
				return  getContext().getString(R.string.date_dialog_year,value);
			}
		});
		
		mFrontMonth.setFormatter(new Formatter() {
			
			@Override
			public String format(int value) {
				
				
				return getContext().getString(R.string.date_dialog_month,value);
			}
		});
		
		mFrontYear.setMaxValue(mFrontYearNumMax);
		mFrontYear.setMinValue(mFrontYearNumMin);
		mFrontYear.setValue(mFrontCurYearNum);
		mFrontYear.setWrapSelectorWheel(false);
		
		mFrontMonth.setMaxValue(mFrontMonthNumMax);
		mFrontMonth.setMinValue(mFrontMonthNumMin);
		mFrontMonth.setValue(mFrontCurMonthNum);
		mFrontMonth.setWrapSelectorWheel(true);
		
		mBackYear.setFormatter(new Formatter() {
			
			@Override
			public String format(int value) {
				if(value == mBackYearNumMax + 1){
					return getContext().getString(R.string.date_dialog_tonow);
				}
				
				return getContext().getString(R.string.date_dialog_year, value);
			}
		});
		
		mBackMonth.setFormatter(new Formatter() {
			
			@Override
			public String format(int value) {
				if(value == mBackMonthNumMax + 1){
					return getContext().getString(R.string.date_dialog_none);
				}
				return getContext().getString(R.string.date_dialog_month, value);
			}
		});
		
		mBackYear.setMaxValue(mBackYearNumMax + 1);
		mBackYear.setMinValue(mFrontCurYearNum);//开始时间的当前时间是　结束时间的最小值
		mBackYear.setValue(mBackCurYearNum);
		mBackYear.setWrapSelectorWheel(false);
		
		mBackMonth.setMaxValue(mBackMonthNumMax);
		
		if(mFrontCurYearNum >= mBackCurYearNum){
			mBackMonth.setMinValue(mFrontCurMonthNum);//开始时间的当前时间是　结束时间的最小值
		}else{
			mBackMonth.setMinValue(mBackMonthNumMin);
		}
		mBackMonth.setValue(mBackCurMonthNum);
		mBackMonth.setWrapSelectorWheel(true);

		mBtnConfirm = findViewById(R.id.btn_confirm);
		mBtnBack = findViewById(R.id.btn_back);
		findViewById(R.id.btn_back_layout).setVisibility(View.INVISIBLE);
		
		if(mBackYear.getValue() == mBackYearToNow || mBackMonth.getValue() == mBackMonthToNow){
			mBackToNow = true;
			if(mBackCurMonthNum > mBackMonthNumMax){
				mBackCurMonthNum = mBackMonthNumMax;
			}	
			mBackMonth.setMaxValue(mBackMonthToNow);
			mBackMonth.setMinValue(mBackMonthToNow);
			mBackMonth.setValue(mBackMonthToNow);
		}
		
		
		mBtnConfirm.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(mFrontLayout.getVisibility() != View.VISIBLE){
					if(dateSetListener != null){
						dateSetListener.onDateSet(mFrontCurYearNum, mFrontCurMonthNum, mBackCurYearNum, mBackCurMonthNum , mBackToNow);
					}
					dismiss();
				}else{
					Animation in = AnimationUtils.loadAnimation(getContext(), R.anim.anim_right_in);
					Animation out = AnimationUtils.loadAnimation(getContext(), R.anim.anim_right_out);
					mBackLayout.startAnimation(in);
					mFrontLayout.startAnimation(out);
					mBackLayout.setVisibility(View.VISIBLE);
					findViewById(R.id.btn_back_layout).setVisibility(View.VISIBLE);
					mFrontLayout.setVisibility(View.INVISIBLE);
				}
			}
		});
		
		
		
		mBtnBack.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(mBackLayout.getVisibility() != View.VISIBLE){
					dismiss();
				}else{
					Animation in = AnimationUtils.loadAnimation(getContext(), R.anim.anim_left_in);
					Animation out = AnimationUtils.loadAnimation(getContext(), R.anim.anim_left_out);
					mFrontLayout.startAnimation(in);
					mBackLayout.startAnimation(out);
					mFrontLayout.setVisibility(View.VISIBLE);
					findViewById(R.id.btn_back_layout).setVisibility(View.INVISIBLE);
					mBackLayout.setVisibility(View.INVISIBLE);
				}
			}
		});
		
		
		mFrontYear.setOnValueChangedListener(new OnValueChangeListener() {
			
			@Override
			public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
				if(newVal == Calendar.getInstance().get(Calendar.YEAR)){//今年选择最大的月份有限制
					mFrontMonth.setMaxValue(Calendar.getInstance().get(Calendar.MONTH) + 1);
				}else{
					mFrontMonth.setMaxValue(mFrontMonthNumMax);
				}
				mFrontMonth.setWrapSelectorWheel(true);
				mBackYear.setMinValue(newVal);
				mFrontCurYearNum = newVal;
			}
		});
		
		mFrontMonth.setOnValueChangedListener(new OnValueChangeListener() {
			
			@Override
			public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
				mFrontCurMonthNum = newVal;
				if(mFrontCurYearNum == mBackCurYearNum){
					mBackMonth.setMinValue(newVal);
				}else{
					mBackMonth.setMinValue(mBackMonthNumMin);
				}
			}
		});
		
		mBackYear.setOnValueChangedListener(new OnValueChangeListener() {
			
			@Override
			public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
				
				if(newVal == Calendar.getInstance().get(Calendar.YEAR)){//今年选择最大的月份有限制
					mBackMonth.setMaxValue(Calendar.getInstance().get(Calendar.MONTH) + 1);
				}else{
					mBackMonth.setMaxValue(mBackMonthNumMax);
				}
				if(newVal == mBackYearToNow){//用户选择了至今选项
					mBackToNow = true;
					mBackMonth.setMaxValue(mBackMonthToNow);
					mBackMonth.setMinValue(mBackMonthToNow);
					mBackMonth.setValue(mBackMonthToNow);
				}else{
					if(mFrontCurYearNum == newVal){ //开始时间　等于结束时间
						mBackMonth.setMinValue(mFrontMonth.getValue());
						if(mBackCurMonthNum > mBackMonthNumMax){
							mBackCurMonthNum = mBackMonthNumMax;
						}
					}else{
						mBackMonth.setMinValue(mBackMonthNumMin);
						mBackCurMonthNum = mBackMonth.getValue();
					}
					mBackToNow = false;
					mBackMonth.setMaxValue(mBackMonthNumMax);
					mBackMonth.setValue(mBackCurMonthNum);
					
				}
				mBackMonth.setWrapSelectorWheel(true);
				mBackCurYearNum = newVal;
			}
		});
		
		mBackMonth.setOnValueChangedListener(new OnValueChangeListener() {
			@Override
			public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
				mBackCurMonthNum = newVal;
			}
		});
		
		super.setOnDismissListener(new OnDismissListener() {
			
			@Override
			public void onDismiss(DialogInterface dialog) {
				if(mFrontLayout.getVisibility() != View.VISIBLE){
					if(dateSetListener != null){
						dateSetListener.onDateSet(mFrontCurYearNum, mFrontCurMonthNum, mBackCurYearNum, mBackCurMonthNum , mBackToNow);
					}
				}
				if(onDismissListener != null){
					onDismissListener.onDismiss(dialog);
				}
			}
		});
	}
	
	
	OnDismissListener onDismissListener = null;
	
	
	@Override
	public void setOnDismissListener(OnDismissListener listener) {
		onDismissListener = listener;
	}

	@Override
	public void show() {
		super.show();
	}
	

	@Override
	public void onClick(DialogInterface dialog, int which) {
		
	}

	public interface OnDateSetListener{
		
		/**
		 * @param frontYear
		 * 	开始时间年
		 * @param frontMonth
		 * 	开始时间月
		 * @param backYear
		 * 	结束时间年
		 * @param backMonth
		 * 	结束时间月
		 * @param backToNow
		 * 	是否是至今
		 */
		void onDateSet(int frontYear, int frontMonth, int backYear, int backMonth, boolean backToNow);
	}
	
	private OnDateSetListener dateSetListener;


	public OnDateSetListener getDateSetListener() {
		return dateSetListener;
	}

	public void setDateSetListener(OnDateSetListener dateSetListener) {
		this.dateSetListener = dateSetListener;
	}

	public int getFrontCurYearNum() {
		return mFrontCurYearNum;
	}

	public void setFrontCurYearNum(int mFrontCurYearNum) {
		this.mFrontCurYearNum = mFrontCurYearNum;
	}

	public int getBackCurYearNum() {
		return mBackCurYearNum;
	}

	public void setBackCurYearNum(int mBackCurYearNum) {
		this.mBackCurYearNum = mBackCurYearNum;
	}

	public int getFrontCurMonthNum() {
		return mFrontCurMonthNum;
	}

	public void setFrontCurMonthNum(int mFrontCurMonthNum) {
		this.mFrontCurMonthNum = mFrontCurMonthNum;
	}

	public int getBackCurMonthNum() {
		return mBackCurMonthNum;
	}

	public void setBackCurMonthNum(int mBackCurMonthNum) {
		this.mBackCurMonthNum = mBackCurMonthNum;
	}

	public int getBackYearToNow() {
		return mBackYearToNow;
	}

	public int getBackMonthToNow() {
		return mBackMonthToNow;
	}
	
	
	
}
