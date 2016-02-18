package com.xiaobukuaipao.vzhi;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Message;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.xiaobukuaipao.vzhi.contentprovider.GeneralContentProvider;
import com.xiaobukuaipao.vzhi.database.CookieTable;
import com.xiaobukuaipao.vzhi.wrap.ProfileWrapActivity;

/**
 *	账户设置
 */
public class SettingAccountActivity extends ProfileWrapActivity implements OnClickListener{
	
	private String mobile;
	private TextView mCurNumber;
	
	@Override
	public void initUIAndData() {
        setContentView(R.layout.activity_setting_account);
        setHeaderMenuByCenterTitle(R.string.setting_account_str);
        setHeaderMenuByLeft(this);
        
        mCurNumber = (TextView) findViewById(R.id.setting_account_mobile_number);
        
        findViewById(R.id.setting_account_mobile).setOnClickListener(this);
        findViewById(R.id.setting_account_pswd).setOnClickListener(this);
        findViewById(R.id.setting_forget_password).setOnClickListener(this);
		
	}

	@Override
	protected void onResume() {
		//读取数据库 获取mobile
		getMobile();
		
		SpannableStringBuilder builder = new SpannableStringBuilder(getString(R.string.setting_cur_mobile, mobile));
		StyleSpan span = new StyleSpan(Typeface.BOLD);
		builder.setSpan(span, 7, 7 + mobile.length(), SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE);
		mCurNumber.setText(builder);
		
		super.onResume();
	}

	private synchronized void getMobile() {
		Cursor cookieCursor = getContentResolver().query(GeneralContentProvider.COOKIE_CONTENT_URI, null, null, null, null);
		while (cookieCursor.moveToNext()) {
			mobile = cookieCursor.getString(cookieCursor.getColumnIndex(CookieTable.COLUMN_MOBILE));
		}
		cookieCursor.close();
	}
	
	@Override
	public void onEventMainThread(Message msg) {
		
	}

	@Override
	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
		case R.id.setting_account_mobile:
			intent = getIntent();
			intent.setClass(this, SettingPhoneActivity.class);
			startActivity(intent);
			break;
		case R.id.setting_account_pswd:
			intent = getIntent();
			intent.setClass(this, SettingPasswordActivity.class);
			startActivity(intent);
			break;
		case R.id.setting_forget_password:
			intent = getIntent();
			intent.setClass(this,SettingResetPswdActivity.class);
			startActivity(intent);
			break;
		default:
			break;
		}
	}

}
