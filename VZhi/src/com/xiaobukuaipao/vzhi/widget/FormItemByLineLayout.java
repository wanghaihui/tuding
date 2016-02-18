package com.xiaobukuaipao.vzhi.widget;

import java.util.LinkedList;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.text.Editable;
import android.text.TextUtils.TruncateAt;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.xiaobukuaipao.vzhi.R;
import com.xiaobukuaipao.vzhi.util.StringUtils;
import com.xiaobukuaipao.vzhi.widget.A5EditText.OnDrawListener;


public class FormItemByLineLayout extends LinearLayout implements View.OnClickListener {
	private TextView leftLabelTv, rightTipTv;
	private A5EditText mInfoEdit;
	private boolean displayTip = true;
	private boolean displayLeftLable = true;
	private String leftLabel,rightTip;
	private boolean displayCbox = false;
	private boolean displayArrow = false;
	private static LinkedList<Object> list = new LinkedList<Object>();
	/**
	 * 值是否更改
	 */
	private boolean isChanged = false;
	private View child;
	private CheckBox mCheckBox;
	
	public FormItemByLineLayout(Context context) {
		this(context, null);
	}
	
	public FormItemByLineLayout(Context context, AttributeSet attrs) {
		super(context, attrs);

		TypedArray typedArray = context.obtainStyledAttributes(attrs,
				R.styleable.FormItemByLineLayout);
		
		leftLabel = typedArray.getString(R.styleable.FormItemByLineLayout_leftLabel);
		rightTip = typedArray.getString(R.styleable.FormItemByLineLayout_rightTip);
		displayTip = typedArray.getBoolean(R.styleable.FormItemByLineLayout_dispTip, displayTip);
		displayLeftLable = typedArray.getBoolean(R.styleable.FormItemByLineLayout_dispLabel, displayLeftLable);
		displayCbox = typedArray.getBoolean(R.styleable.FormItemByLineLayout_dispCbox, displayCbox);
		displayArrow = typedArray.getBoolean(R.styleable.FormItemByLineLayout_dispArrow, displayArrow);
		typedArray.recycle();
		
		View view = LayoutInflater.from(context).inflate(R.layout.form_item_by_layout, this, true);
		child = view.findViewById(R.id.form_item_view);
		if(displayCbox){
			mCheckBox = (CheckBox) view.findViewById(R.id.user_setting_item_view_checkbox);
			mCheckBox.setVisibility(VISIBLE);
			mCheckBox.setEnabled(false);
		}
		
		leftLabelTv = (TextView) view.findViewById(R.id.form_item_view_label);
		leftLabelTv.setEllipsize(TruncateAt.END);

		if (displayLeftLable) {
			leftLabelTv.setText(leftLabel);
		} else {
			leftLabelTv.setVisibility(View.INVISIBLE);
		}
		rightTipTv = (TextView) view.findViewById(R.id.user_setting_item_view_content);

		if (!displayTip) {
			rightTipTv.setVisibility(View.INVISIBLE);
		} else {
			if (!StringUtils.isEmpty(rightTip))rightTipTv.setHint(rightTip);
		}
		
		mInfoEdit = (A5EditText) findViewById(R.id.user_setting_item_view_edit);
		mInfoEdit.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
		mInfoEdit.setPadding(0,(int) getResources().getDimension(R.dimen.text_field_padding),0, (int) getResources().getDimension(R.dimen.text_field_padding));
		mInfoEdit.setChild(true);
		
		leftLabelTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
		
		findViewById(R.id.bottom_line).setVisibility(VISIBLE);
		
		setGravity(Gravity.CENTER_VERTICAL);
		mInfoEdit.setOnClickListener(this);
		mInfoEdit.onDrawListener = new OnDrawListener() {

			@Override
			public void onDraws() {
				// 先移除回调
				if (isEdit()) {
					if (mInfoEdit.isError()) {
						child.setBackgroundResource(R.drawable.textfield_red_1);
					} else {
						if (mInfoEdit.isFocused()) {
							child.setBackgroundResource(R.drawable.textfield_blue_1);
						} else {
							child.setBackgroundResource(R.drawable.textfield_gray_1);
						}
					}
					findViewById(R.id.bottom_line).setVisibility(GONE);
				} else {
					findViewById(R.id.bottom_line).setVisibility(VISIBLE);
				}
			}
		};

		mInfoEdit.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				isChanged = true;
				setFormContent(s.toString());
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
		
		// 焦点改变接口
		mInfoEdit.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus){
					if(!list.contains(this))
						list.add(this);
				}else{
					list.remove(this);
					if (list.isEmpty()) {
						InputMethodManager imm = (InputMethodManager) getContext()
								.getSystemService(Context.INPUT_METHOD_SERVICE);
						if (imm != null) {
							imm.hideSoftInputFromWindow(getWindowToken(), 0);
						}
					}
					setEdit(false);
				}
			}
		});
		
		mInfoEdit.setOnEditorActionListener(new OnEditorActionListener() {
			
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				switch (actionId) {
				case EditorInfo.IME_ACTION_SEND:
				case EditorInfo.IME_ACTION_GO:
				case EditorInfo.IME_ACTION_NEXT:
					
					setEdit(false);
					break;
				default:
					break;
				}
				return false;
			}
		});
	}

	public void setFormLabel(String text) {
		leftLabelTv.setText(text);
	}

	public void setFormContent(String text) {
		rightTipTv.setText(text);
	}

	public TextView getFormLabel() {
		return leftLabelTv;
	}
	public String getFormLabelText() {
		return leftLabelTv.getText().toString();
	}

	public TextView getFormContent() {
		return rightTipTv;
	}
	public String getFormContentText() {
		return rightTipTv.getText().toString();
	}

	public A5EditText getInfoEdit() {
		return mInfoEdit;
	}

	public boolean isEdit() {
		return mInfoEdit.getVisibility() != GONE;
	}
	
	public TextView getRightTipTV() {
		return this.rightTipTv;
	}

		
	@Override
	protected void onFocusChanged(boolean gainFocus, int direction,
			Rect previouslyFocusedRect) {
		super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
	
	}
	public void setEdit(boolean isEdit) {
		if (isEdit) {
			rightTipTv.setVisibility(GONE);
			mInfoEdit.setVisibility(VISIBLE);
			mInfoEdit.setCursorVisible(true);
			mInfoEdit.requestFocus();
			// 如果可以编辑，不显示Hint
			// mInfoEdit.setHint(rightTipTv.getText().toString());

			findViewById(R.id.bottom_line).setVisibility(GONE);
		} else {
			if (StringUtils.isEmpty(rightTipTv.getText().toString())|| StringUtils.isEquals(rightTipTv.getText().toString(),rightTip) ) {
				if(StringUtils.isEmpty(rightTip)){
					rightTip = getContext().getString(R.string.general_tips_add);
				}
				if(displayTip){
					rightTipTv.setHint(rightTip);
				}
//				rightTipTv.setText(rightTip);
				rightTipTv.setTextColor(0xffa2a6a7);
			} else {
				rightTipTv.setTextColor(0xff000000);
			}
			rightTipTv.setVisibility(VISIBLE);
			mInfoEdit.setVisibility(GONE);
			mInfoEdit.setCursorVisible(false);
			findViewById(R.id.bottom_line).setVisibility(VISIBLE);
			child.setBackgroundColor(0);
			
			if(completeListener != null){
				completeListener.onEditComplete();
			}
		}
		
	}

	public boolean isDisplayCbox() {
		return displayCbox;
	}

	public void setDisplayCbox(boolean displayCbox) {
		this.displayCbox = displayCbox;
	}

	public CheckBox getCheckBox() {
		return mCheckBox;
	}

	public void setCheckBox(CheckBox mCheckBox) {
		this.mCheckBox = mCheckBox;
	}

	public boolean isChanged() {
		return isChanged;
	}

	public void setChanged(boolean isChanged) {
		this.isChanged = isChanged;
	}

	@Override
	public void onClick(View v) {
		
		//TODO 焦点问题
		InputMethodManager imm = (InputMethodManager) getContext()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm != null) {
			imm.showSoftInput(mInfoEdit, 0);
		}
	}
	
	public interface OnEditCompleteListener{
		void onEditComplete();
	}
	public OnEditCompleteListener completeListener;

	public OnEditCompleteListener getCompleteListener() {
		return completeListener;
	}

	public void setCompleteListener(OnEditCompleteListener completeListener) {
		this.completeListener = completeListener;
	}
	
	public void reset(){
		getFormContent().setText("");
		if(rightTip != null){
			getFormContent().setHint(rightTip);
		}
		getInfoEdit().setText("");
	}
	
}
