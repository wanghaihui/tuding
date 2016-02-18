package com.xiaobukuaipao.vzhi.widget;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xiaobukuaipao.vzhi.R;
public class LabelInputLayout extends RelativeLayout implements OnClickListener {

	private Context context;

	public TextView mInfoTitle;
	public TextView mInfoInput;
	public A5EditText mInfoEdit;

	private ImageView mInputImg;

	public LabelInputLayout(Context context) {
		this(context, null);
	}

	public LabelInputLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public LabelInputLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
		initViews();
	}

	// 初始化Views
	private void initViews() {
		View.inflate(context, R.layout.label_input_layout, this);
		mInfoTitle = (TextView) findViewById(R.id.info_input_title);
		mInfoInput = (TextView) findViewById(R.id.info_input_edit);
		mInfoEdit = (A5EditText) findViewById(R.id.info_input_edittv);
		mInputImg = (ImageView) findViewById(R.id.info_input_img);
		mInfoEdit.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if(mInfoEdit.isPassword()){
					mInputImg.setVisibility(VISIBLE);
					mInputImg.setOnClickListener(LabelInputLayout.this);
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				
			}
		});
	}

	public boolean isEdit() {
		return mInfoEdit.getVisibility() == GONE;
	}

	public void setEdit(boolean isEdit) {
		if (isEdit) {
			mInfoInput.setVisibility(GONE);
			mInfoEdit.setVisibility(VISIBLE);
			mInfoEdit.setText(mInfoInput.getText().toString());
		} else {
			mInfoInput.setVisibility(VISIBLE);
			mInfoEdit.setVisibility(GONE);
		}

	}

	// 得到Info Title
	public TextView getInfoTitle() {
		return mInfoTitle;
	}

	public TextView getInfoInput() {
		return mInfoInput;
	}

	public A5EditText getInfoEdit() {
		return mInfoEdit;
	}

	boolean flag = true;
	@Override
	public void onClick(View v) {
		if(flag){
			mInputImg.setImageResource(R.drawable.see_blue);
			mInfoEdit.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
			flag =false;
		}else{
			mInfoEdit.setTransformationMethod(PasswordTransformationMethod.getInstance());
			mInputImg.setImageResource(R.drawable.see_gray);
			flag = true;
		}
		mInfoEdit.setSelection(mInfoEdit.getText().toString().length());
	}

}
