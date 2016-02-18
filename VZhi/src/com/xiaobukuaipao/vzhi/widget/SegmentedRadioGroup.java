package com.xiaobukuaipao.vzhi.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.xiaobukuaipao.vzhi.R;

public class SegmentedRadioGroup extends RadioGroup {

	public SegmentedRadioGroup(Context context) {
		super(context);
	}

	public SegmentedRadioGroup(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	// 完成填充
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		changeButtonsImages();
	}

	/**
	 * only for gender
	 * 
	 * @param i
	 */
	public void  setButton(int i){
		int childCount = super.getChildCount();
		if(i > childCount || i < 0){
			return;
		}
		
		if(getChildAt(i) instanceof RadioButton){
			RadioButton radio1 = (RadioButton)findViewById(R.id.button_one);
			RadioButton radio0 = (RadioButton)findViewById(R.id.button_two);
			if(i == 1){
				radio1.setChecked(true);
			}else{
				radio0.setChecked(true);
			}
		}
	}
	
	private void changeButtonsImages(){
		int count = super.getChildCount();
		if (count > 1) {
			super.getChildAt(0).setBackgroundResource(R.drawable.segment_radio_left);
			for(int i=1; i < count-1; i++) {
				super.getChildAt(i).setBackgroundResource(R.drawable.segment_radio_middle);
			}
			super.getChildAt(count-1).setBackgroundResource(R.drawable.segment_radio_right);
		} else if (count == 1) {
			super.getChildAt(0).setBackgroundResource(R.drawable.segment_button);
		}
	}
}

