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

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.LinearLayout;

import com.xiaobukuaipao.vzhi.R;

/**
 * A simple dialog containing an {@link android.widget.DatePicker}.
 *
 * <p>See the <a href="{@docRoot}guide/topics/ui/controls/pickers.html">Pickers</a>
 * guide.</p>
 */
public class DatePickerDialog extends AlertDialog implements OnClickListener,
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
    
    private final Button mBackToFront;
    
    private final DatePicker mDatePicker;
    private final DatePicker mDatePickerBack;
    
    private final OnDateSetListener mCallBack;

    /**
     * The callback used to indicate the user is done filling in the date.
     */
    public interface OnDateSetListener {

        /**
         * @param view The view associated with this listener.
         * @param year The year that was set.
         * @param monthOfYear The month that was set (0-11) for compatibility
         *  with {@link java.util.Calendar}.
         * @param dayOfMonth The day of the month that was set.
         */
        void onDateSet(DatePicker view, int frontYear, int frontMonthOfYear, int frontDayOfMonth, 
        		int backYear, int backMonthOnYear, int backDayOfMonth);
    }
    
    /**
     * @param context The context the dialog is to run in.
     * @param callBack How the parent is notified that the date is set.
     * @param year The initial year of the dialog.
     * @param monthOfYear The initial month of the dialog.
     * @param dayOfMonth The initial day of the dialog.
     */
    public DatePickerDialog(Context context,
            OnDateSetListener callBack,
            int year,
            int monthOfYear,
            int dayOfMonth) {
        this(context, Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB ? R.style.Theme_Dialog_Alert : 0, callBack, year, monthOfYear, dayOfMonth);
    }

    /**
     * @param context The context the dialog is to run in.
     * @param theme the theme to apply to this dialog
     * @param callBack How the parent is notified that the date is set.
     * @param year The initial year of the dialog.
     * @param monthOfYear The initial month of the dialog.
     * @param dayOfMonth The initial day of the dialog.
     */
    public DatePickerDialog(Context context,
            int theme,
            OnDateSetListener callBack,
            int year,
            int monthOfYear,
            int dayOfMonth) {
        super(context, theme);
        
        mContext = context;

        mCallBack = callBack;

        Context themeContext = getContext();
        setButton(BUTTON_POSITIVE, themeContext.getText(R.string.date_time_done), this);
        setIcon(0);
        setButton(BUTTON_NEGATIVE, themeContext.getText(R.string.date_time_cancel), this);
        setIcon(1);

        View view = View.inflate(mContext,R.layout.dialog_date_picker, null);
        
        setView(view);
        
        mDatePickerLayout = (LinearLayout) view.findViewById(R.id.datePickerLayout);
        mDatePickerBackLayout = (LinearLayout) view.findViewById(R.id.datePickerBackLayout);
        
        mBackToFront = (Button) mDatePickerBackLayout.findViewById(R.id.btn_back);
        
        mDatePicker = (DatePicker) view.findViewById(R.id.datePicker);
        mDatePickerBack = (DatePicker) view.findViewById(R.id.datePickerBack);
        
        mBackToFront.setOnClickListener(new android.view.View.OnClickListener() {

			@Override
			public void onClick(View view) {
				
	    		mDatePickerLayout.setVisibility(View.VISIBLE);
	    		mDatePickerBackLayout.setVisibility(View.INVISIBLE);
			}
        	
        });
        mDatePicker.init(year, monthOfYear, dayOfMonth, this);
        mDatePickerBack.init(year, monthOfYear, dayOfMonth, this);
        
        // updateTitle(year, monthOfYear, dayOfMonth);
    }
    

    // 重新OnClickListener的onClick方法
    public void onClick(DialogInterface dialog, int which) {
    	switch (which) {
	    	case BUTTON_POSITIVE:   
	    		if (mDatePickerBackLayout.getVisibility() == View.INVISIBLE) {
		    		try {    
		    			// 找准SuperClass
		    	    	Field field =  DatePickerDialog.this.getClass().getSuperclass().getSuperclass().getDeclaredField("mShowing" ); 
		    	    	field.setAccessible(true); 
		    	    	// 设置mShowing值，欺骗android系统
		    	    	field.set(this, false);	    
		    	    	DatePickerDialog.this.dismiss();
		    	    } catch (Exception e) { 
		    	    	
		    	    }
		    		
		    		Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.anim_left_in);
		    		LayoutAnimationController lac = new LayoutAnimationController(animation);
		    		mDatePickerBackLayout.setLayoutAnimation(lac);
		    		
		    		mDatePickerBackLayout.setVisibility(View.VISIBLE);
		    		mDatePickerLayout.setVisibility(View.INVISIBLE);

	    		} else {
	    			try {    
		    			// 找准SuperClass
		    	    	Field field =  DatePickerDialog.this.getClass().getSuperclass().getSuperclass().getDeclaredField("mShowing" ); 
		    	    	field.setAccessible(true); 
		    	    	// 设置mShowing值，欺骗android系统
		    	    	field.set(this, true);	    
		    	    	DatePickerDialog.this.dismiss();
		    	    } catch (Exception e) { 
		    	    	
		    	    }
	    			
	    			// 此时--第二个DialogView显示
		    		tryNotifyDateSet();
	    		}
	    		break;
	    	case BUTTON_NEGATIVE:
	    		try {    
	    			// 找准SuperClass
	    	    	Field field =  DatePickerDialog.this.getClass().getSuperclass().getSuperclass().getDeclaredField("mShowing" ); 
	    	    	field.setAccessible(true); 
	    	    	// 设置mShowing值，欺骗android系统
	    	    	field.set(this, true);	    
	    	    	DatePickerDialog.this.dismiss();
	    	    } catch (Exception e) { 
	    	    	
	    	    }
	    		break;
    	}
        // 
    }

    public void onDateChanged(DatePicker view, int year,
            int month, int day) {
    	if (mDatePickerLayout.getVisibility() == View.VISIBLE) {
    		mDatePicker.init(year, month, day, this);
    	}
    	
    	if (mDatePickerBackLayout.getVisibility() == View.VISIBLE) {
    		mDatePickerBack.init(year, month, day, this);
    	}
        // updateTitle(year, month, day);
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
     * @param year The date year.
     * @param monthOfYear The date month.
     * @param dayOfMonth The date day of month.
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
                    mDatePicker.getMonth(), mDatePicker.getDayOfMonth(), mDatePickerBack.getYear(), mDatePickerBack.getMonth(),
                    mDatePickerBack.getDayOfMonth());
        }
    }

    /*private void updateTitle(int year, int month, int day) {
        if (!mDatePicker.getCalendarViewShown()) {
            mCalendar.set(Calendar.YEAR, year);
            mCalendar.set(Calendar.MONTH, month);
            mCalendar.set(Calendar.DAY_OF_MONTH, day);
            String title = DateUtils.formatDateTime(getContext(),
                    mCalendar.getTimeInMillis(),
                    DateUtils.FORMAT_SHOW_DATE
                            | DateUtils.FORMAT_SHOW_WEEKDAY
                            | DateUtils.FORMAT_SHOW_YEAR
                            | DateUtils.FORMAT_ABBREV_MONTH
                            | DateUtils.FORMAT_ABBREV_WEEKDAY);
            setTitle(title);
            mTitleNeedsUpdate = true;
        } else {
            if (mTitleNeedsUpdate) {
                mTitleNeedsUpdate = false;
                setTitle(R.string.date_picker_dialog_title);
            }
        }
    }*/

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
    
}

