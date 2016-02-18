package com.xiaobukuaipao.vzhi.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xiaobukuaipao.vzhi.R;
import com.xiaobukuaipao.vzhi.util.StringUtils;

public class FormItemByLineView extends LinearLayout {
	private TextView formLabel, formContent;
	public A5EditText mInfoEdit;

	private boolean displayCenter = true;
	private boolean displayLeftLable = true;
	private boolean displayArrow = true;
	
	/**
	 * 左右　布局label和　text的布局不一样　具体看布局文件
	 */
	private boolean rightMoreLeft = false;
	private String centerTip;

	public FormItemByLineView(Context context) {
		this(context, null);
	}

	public void setFormLabel(String text) {
		formLabel.setText(text);
	}

	public void setFormContent(String text) {
		formContent.setText(text);
	}

	public FormItemByLineView(Context context, AttributeSet attrs) {
		super(context, attrs);

		TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.FormItemByLineView);
		String itemTitle = typedArray.getString(R.styleable.FormItemByLineView_itemTextLeftTitle);
		centerTip = typedArray.getString(R.styleable.FormItemByLineView_centerTip);
		displayCenter = typedArray.getBoolean(R.styleable.FormItemByLineView_displayCenter, displayCenter);
		displayLeftLable = typedArray.getBoolean(R.styleable.FormItemByLineView_displayLeftLable,displayLeftLable);
		displayArrow = typedArray.getBoolean(R.styleable.FormItemByLineView_displayArrow, displayArrow);
		rightMoreLeft = typedArray.getBoolean(R.styleable.FormItemByLineView_rightMoreLeft, rightMoreLeft);
		typedArray.recycle();
		View view = null;
		if(rightMoreLeft){
			view = LayoutInflater.from(context).inflate(R.layout.form_item_by_line2, this, true);
		}else{
			view = LayoutInflater.from(context).inflate(R.layout.form_item_by_line, this, true);
		}

		formLabel = (TextView) view.findViewById(R.id.form_item_view_label);
		formLabel.setEllipsize(TruncateAt.END);
		formLabel.setGravity(Gravity.CENTER_VERTICAL);

		if (displayLeftLable) {
			formLabel.setText(itemTitle);
		} else {
			formLabel.setVisibility(View.INVISIBLE);
		}

		formContent = (TextView) view.findViewById(R.id.user_setting_item_view_content);

		if (!displayCenter) {
			formContent.setVisibility(View.INVISIBLE);
		} else {
			if (!StringUtils.isEmpty(centerTip)){
				formContent.setHint(centerTip);
			}
		}
		if (!displayArrow) {
			view.findViewById(R.id.form_item_view_arrow).setVisibility(
					View.INVISIBLE);
		}
		mInfoEdit = (A5EditText) findViewById(R.id.user_setting_item_view_edit);
		formLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
	}

	public TextView getFormLabel() {
		return formLabel;
	}

	public TextView getFormContent() {
		return formContent;
	}
	
	public String getFormLabelText() {
		return formLabel.getText().toString();
	}

	public String getFormContentText() {
		return formContent.getText().toString();
	}
	
	public A5EditText getInfoEdit() {
		return mInfoEdit;
	}

	public boolean isEdit() {
		return mInfoEdit.getVisibility() == GONE;
	}

	public void setEdit(boolean isEdit) {
		if (isEdit) {
			formContent.setVisibility(GONE);
			mInfoEdit.setVisibility(VISIBLE);
			mInfoEdit.setHint(formContent.getText().toString());
		} else {
			formContent.setVisibility(VISIBLE);
			mInfoEdit.setVisibility(GONE);
		}
	}
	
	
	public void setDisplayArrow(boolean displayArrow) {
		this.displayArrow = displayArrow;
	}
	
	public void reset(){
		getFormContent().setText("");
		if(centerTip != null){
			getFormContent().setHint(centerTip);
		}
		getInfoEdit().setText("");
	}
}
