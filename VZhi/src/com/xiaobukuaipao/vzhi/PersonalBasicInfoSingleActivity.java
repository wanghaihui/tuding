package com.xiaobukuaipao.vzhi;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.xiaobukuaipao.vzhi.util.Logcat;

public class PersonalBasicInfoSingleActivity  extends BaseFragmentActivity{
	
	private final static String TAG = "PersonalBasicInfoSingleActivity";
	
	private EditText mSingleEdit;
	private TextView mSingleTitle;
	
	public final static String SINGLE_EDIT = "edit";
	public final static String SINGLE_TITLE= "title";
	public final static int RESULT_OK = 20;
	public final static int RESULT_CANCEL = 21;
	private Intent mIntent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_personal_basicinfo_single);
		mIntent = getIntent();
		setHeaderMenuByLeft(this);
		setHeaderMenuByCenterTitle(R.string.person_profile_edit);
		setHeaderMenuByRight(0);
		mSingleEdit = (EditText) findViewById(R.id.info_single_edit);
		mSingleTitle = (TextView) findViewById(R.id.info_single_title);
		
		mSingleTitle.setText(mIntent.getStringExtra(SINGLE_TITLE));
		
		mSingleEdit.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				mIntent.putExtra(SINGLE_EDIT, s.toString());
				Logcat.d(TAG, s.toString());
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
			}
		});
		
		findViewById(R.id.menu_bar_right).setOnClickListener(mOnClickListener);
		findViewById(R.id.menu_bar_back_btn_layout).setOnClickListener(mOnClickListener);
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putString(SINGLE_EDIT, mSingleEdit.getText().toString());
		outState.putString(SINGLE_TITLE, mIntent.getStringExtra(SINGLE_TITLE));
		super.onSaveInstanceState(outState);
		
		
	}
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		mSingleEdit.setText(savedInstanceState.getString(SINGLE_EDIT));
		mSingleTitle.setText(savedInstanceState.getString(SINGLE_TITLE));
		super.onRestoreInstanceState(savedInstanceState);
	}
	
	private OnClickListener mOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.menu_bar_right:
				setResult(RESULT_OK, mIntent);
				break;
			case R.id.menu_bar_back_btn_layout:
				setResult(RESULT_CANCEL);
				break;
			default:
				break;
			}
			AppActivityManager.getInstance().finishActivity(PersonalBasicInfoSingleActivity.this);
		}
	};
	
}
