package com.xiaobukuaipao.vzhi.widget;

/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.lang.reflect.Field;
import java.util.Calendar;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xiaobukuaipao.vzhi.R;

/**
 * A simple dialog containing an {@link android.widget.DatePicker}.
 * 
 * <p>
 * See the <a href="{@docRoot}guide/topics/ui/controls/pickers.html">Pickers</a>
 * guide.
 * </p>
 */
public class SimpleDateDialog extends Dialog implements OnClickListener,
		DatePicker.OnDateChangedListener {

	private Context mContext;

	private static final String YEAR_FRONT = "year_front";
	private static final String MONTH_FRONT = "month_front";
	private static final String DAY_FRONT = "day_front";

	private static final String YEAR_BACK = "year_back";
	private static final String MONTH_BACK = "month_back";
	private static final String DAY_BACK = "day_back";

	private final LinearLayout mDatePickerLayout;
	private final LinearLayout mDatePickerBackLayout;

	private final Button mBack;
	private final Button mNext;

	private final DatePicker mDatePicker;
	private final DatePicker mDatePickerBack;

	private final OnDateSetListener mCallBack;
	
	private boolean isForeground;

	private boolean onlyYearMonth;
	
	private TextView title;
	
	private boolean isOnlyForeGround;
	
	/**
	 * 为工作经历设置起止年月
	 */
	private boolean jobTime;
	
	/**
	 * 为教育经历设置起止年月
	 */
	private boolean eduTime;
	
	/**
	 * hr在职时间
	 */
	private boolean hrDate;
	
	private int year;
	@SuppressWarnings("unused")
	private int monthOfYear;
	private int dayOfMonth;
	
	/**
	 * The callback used to indicate the user is done filling in the date.
	 */
	public interface OnDateSetListener {

		/**
		 * @param view
		 *            The view associated with this listener.
		 * @param year
		 *            The year that was set.
		 * @param monthOfYear
		 *            The month that was set (0-11) for compatibility with
		 *            {@link java.util.Calendar}.
		 * @param dayOfMonth
		 *            The day of the month that was set.
		 */
		void onDateSet(DatePicker view, int frontYear, int frontMonthOfYear,
				int frontDayOfMonth, int backYear, int backMonthOnYear,
				int backDayOfMonth);
	}

	/**
	 * @param context
	 *            The context the dialog is to run in.
	 * @param theme
	 *            the theme to apply to this dialog
	 * @param callBack
	 *            How the parent is notified that the date is set.
	 * @param year
	 *            The initial year of the dialog.
	 * @param monthOfYear
	 *            The initial month of the dialog.
	 * @param dayOfMonth
	 *            The initial day of the dialog.
	 */
	public SimpleDateDialog(Context context, OnDateSetListener callBack,
			int year, int monthOfYear, int dayOfMonth) {
		super(context, R.style.dialog);

		this.year = year;
		this.monthOfYear = monthOfYear;
		this.dayOfMonth = dayOfMonth;
		
		mContext = context;

		mCallBack = callBack;

		setContentView(R.layout.dialog_date_picker);

		mDatePickerLayout = (LinearLayout) findViewById(R.id.datePickerLayout);

		mDatePickerBackLayout = (LinearLayout) findViewById(R.id.datePickerBackLayout);
		
		title = (TextView) mDatePickerLayout.findViewById(R.id.title);
		
		mBack = (Button)findViewById(R.id.btn_back);
		mBack.setVisibility(View.INVISIBLE);
		mNext = (Button)findViewById(R.id.btn_next);
		if(isOnlyForeGround){
			mNext.setVisibility(View.INVISIBLE);
		}
		
		mDatePicker = (DatePicker) findViewById(R.id.datePicker);
		mDatePickerBack = (DatePicker) findViewById(R.id.datePickerBack);
		mBack.setOnClickListener(new android.view.View.OnClickListener() {

					@Override
					public void onClick(View view) {
						try {
							// 找准SuperClass
							Field field = SimpleDateDialog.this.getClass().getSuperclass()
									.getSuperclass().getDeclaredField("mShowing");
							field.setAccessible(true);
							// 设置mShowing值，欺骗android系统
							field.set(this, true);
							SimpleDateDialog.this.dismiss();
						} catch (Exception e) {

						}
						Animation in = AnimationUtils.loadAnimation(mContext,
								R.anim.anim_left_in);
						Animation out = AnimationUtils.loadAnimation(mContext,
								R.anim.anim_left_out);
						mDatePickerLayout.startAnimation(in);
						mDatePickerBackLayout.startAnimation(out);
						mDatePickerLayout.setVisibility(View.VISIBLE);
						mDatePickerBackLayout.setVisibility(View.INVISIBLE);
						mBack.setVisibility(View.INVISIBLE);
						mNext.setVisibility(View.VISIBLE);
					}

				});
		mNext.setOnClickListener(new android.view.View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (mDatePickerBackLayout.getVisibility() == View.INVISIBLE) {
					try {
						// 找准SuperClass
						Field field = SimpleDateDialog.this.getClass()
								.getSuperclass().getSuperclass()
								.getDeclaredField("mShowing");
						field.setAccessible(true);
						// 设置mShowing值，欺骗android系统
						field.set(this, false);
						SimpleDateDialog.this.dismiss();
					} catch (Exception e) {

					}
					Animation in = AnimationUtils.loadAnimation(mContext,
							R.anim.anim_right_in);
					Animation out = AnimationUtils.loadAnimation(mContext,
							R.anim.anim_right_out);

					mDatePickerBackLayout.startAnimation(in);
					mDatePickerLayout.startAnimation(out);
					mDatePickerBackLayout.setVisibility(View.VISIBLE);
					mDatePickerLayout.setVisibility(View.INVISIBLE);
					mBack.setVisibility(View.VISIBLE);
					mNext.setVisibility(View.INVISIBLE);
					isForeground = true;
				} else {
					try {
						// 找准SuperClass
						Field field = SimpleDateDialog.this.getClass().getSuperclass().getSuperclass().getDeclaredField("mShowing");
						field.setAccessible(true);
						// 设置mShowing值，欺骗android系统
						field.set(this, true);
						SimpleDateDialog.this.dismiss();
					} catch (Exception e) {

					}

					// 此时--第二个DialogView显示
					tryNotifyDateSet();
				}
			}
		});
		mDatePicker.init(year, monthOfYear, dayOfMonth, this);
		mDatePickerBack.init(year, monthOfYear, dayOfMonth, this);
		setOnCancelListener(new OnCancelListener() {
			
			@Override
			public void onCancel(DialogInterface dialog) {
					tryNotifyDateSet();
			}
		});
	}

	@Override
	public void show() {
		if(onlyYearMonth){
			mDatePicker.findViewById(R.id.day).setVisibility(View.GONE);
			mDatePickerBack.findViewById(R.id.day).setVisibility(View.GONE);
		}
		super.show();
	}
	@Override
	public void setOnCancelListener(OnCancelListener listener) {
		super.setOnCancelListener(listener);
		
	}
	public void onDateChanged(DatePicker view, int year, int month, int day) {

		
		if(eduTime){
			if(isDateAfter(mDatePicker)){
	            Calendar mCalendar = Calendar.getInstance();
	            view.init(mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH), this);
	        }
			if(isDateBefore()){
				mDatePickerBack.init(mDatePicker.getYear(), mDatePicker.getMonth(), mDatePicker.getDayOfMonth(), this);
			}
		}else if(jobTime){
			 Calendar mCalendar = Calendar.getInstance();
			 //TODO 增加0 表示 至今

			if(isDateAfter(view)){
	            view.init(mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH), this);
	        }
			if(isDateBefore()){
				mDatePickerBack.init(mDatePicker.getYear(), mDatePicker.getMonth(), mDatePicker.getDayOfMonth(), this);
			}
		}else if(hrDate){
		    Calendar mCalendar = Calendar.getInstance();
			if(isDateAfter(view)){
	            view.init(mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH), this);
	        }
			
		}

		// updateTitle(year, month, day);
	}
	
	
	// 判断开始时间是否在结束时间前边
	private boolean isDateBefore(){
	     Calendar mCalendar = Calendar.getInstance();
	     Calendar tempCalendar = Calendar.getInstance();
	     tempCalendar.set(mDatePicker.getYear(), mDatePicker.getMonth() , mDatePicker.getDayOfMonth(), 0, 0, 0);
	     mCalendar.set(mDatePickerBack.getYear(), mDatePickerBack.getMonth() , mDatePickerBack.getDayOfMonth(), 0, 0, 0);
	     if(tempCalendar.after(mCalendar))
	            return true;
	        else 
	            return false;
	}
	// 判断tempView是否在当前时间前 
	private boolean isDateAfter(DatePicker tempView) {
	        Calendar mCalendar = Calendar.getInstance();
	        Calendar tempCalendar = Calendar.getInstance();
	        tempCalendar.set(tempView.getYear(), tempView.getMonth() , tempView.getDayOfMonth(), 0, 0, 0);
	        if(tempCalendar.after(mCalendar))
	            return true;
	        else 
	            return false;
	 }
	/**
	 * Gets the {@link DatePicker} contained in this dialog.
	 * 
	 * @return The calendar view.
	 */
	public DatePicker getDatePicker() {
		return mDatePicker;
	}

	public DatePicker getDatePickerBack() {
		return mDatePickerBack;
	}

	/**
	 * Sets the current date.
	 * 
	 * @param year
	 *            The date year.
	 * @param monthOfYear
	 *            The date month.
	 * @param dayOfMonth
	 *            The date day of month.
	 */
	public void updateDate(int year, int monthOfYear, int dayOfMonth) {
		mDatePicker.updateDate(year, monthOfYear, dayOfMonth);
	}

	/**
	 * 通知数据结合改变
	 */
	private void tryNotifyDateSet() {
		if (mCallBack != null) {
			mDatePicker.clearFocus();
			mCallBack.onDateSet(mDatePicker, mDatePicker.getYear(),
					mDatePicker.getMonth(), mDatePicker.getDayOfMonth(),
					mDatePickerBack.getYear(), mDatePickerBack.getMonth(),
					mDatePickerBack.getDayOfMonth());
		}
	}


	@Override
	public Bundle onSaveInstanceState() {
		Bundle state = super.onSaveInstanceState();
		state.putInt(YEAR_FRONT, mDatePicker.getYear());
		state.putInt(MONTH_FRONT, mDatePicker.getMonth());
		state.putInt(DAY_FRONT, mDatePicker.getDayOfMonth());

		state.putInt(YEAR_BACK, mDatePickerBack.getYear());
		state.putInt(MONTH_BACK, mDatePickerBack.getMonth());
		state.putInt(DAY_BACK, mDatePickerBack.getDayOfMonth());
		return state;
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		int yearFront = savedInstanceState.getInt(YEAR_FRONT);
		int monthFront = savedInstanceState.getInt(MONTH_FRONT);
		int dayFront = savedInstanceState.getInt(DAY_FRONT);

		int yearBack = savedInstanceState.getInt(YEAR_BACK);
		int monthBack = savedInstanceState.getInt(MONTH_BACK);
		int dayBack = savedInstanceState.getInt(DAY_BACK);

		mDatePicker.init(yearFront, monthFront, dayFront, this);
		mDatePickerBack.init(yearBack, monthBack, dayBack, this);
	}


	@Override
	public void onClick(DialogInterface dialog, int which) {
		
	}

	/**
	 * 返回是否选择了结束日期
	 * 
	 * @return 
	 */
	public boolean isForeground() {
		return isForeground;
	}

	public boolean isOnlyYearMonth() {
		return onlyYearMonth;
	}

	public void setOnlyYearMonth(boolean onlyYearMonth) {
		
		this.onlyYearMonth = onlyYearMonth;
	}

	public boolean isJobTime() {
		return jobTime;
	}

	public void setJobTime(boolean jobTime) {
		this.jobTime = jobTime;
	}

	public boolean isEduTime() {
		return eduTime;
	}

	public void setEduTime(boolean eduTime) {
		this.eduTime = eduTime;
		if(eduTime){
			mDatePicker.init(year, 9 - 1, dayOfMonth, this);
			mDatePickerBack.init(year + 4, 7 - 1, dayOfMonth, this);
			
		}
	}

	public boolean isHrDate() {
		return hrDate;
	}

	public void setHrDate(boolean hrDate) {
		this.hrDate = hrDate;
	}

	public void setTitle(String title) {
		this.title.setText(title);
	}

	public void setOnlyForeGround(boolean isOnlyForeGround) {
		this.isOnlyForeGround = isOnlyForeGround;
		if(isOnlyForeGround){
			mNext.setVisibility(View.GONE);
		}
	}
}
