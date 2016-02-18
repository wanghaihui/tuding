package com.xiaobukuaipao.vzhi.register;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Message;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import com.xiaobukuaipao.vzhi.R;
import com.xiaobukuaipao.vzhi.contentprovider.GeneralContentProvider;
import com.xiaobukuaipao.vzhi.database.UserInfoTable;
import com.xiaobukuaipao.vzhi.event.InfoResult;
import com.xiaobukuaipao.vzhi.util.GlobalConstants;
import com.xiaobukuaipao.vzhi.util.StringUtils;
import com.xiaobukuaipao.vzhi.util.VToast;
import com.xiaobukuaipao.vzhi.widget.LabelInputLayout;
import com.xiaobukuaipao.vzhi.wrap.RegisterWrapActivity;

public class PasswordSettingActivity extends RegisterWrapActivity {
	private LabelInputLayout mMobile;
	private LabelInputLayout mPasswd;

	private TextView mPswdTips;

	@Override
	public void initUIAndData() {
		setContentView(R.layout.activity_password_setting);
		setHeaderMenuByLeft(this);
		setHeaderMenuByCenterTitle(R.string.title_password_setting);
		setHeaderMenuByRight(R.string.general_finished);
		onClickListenerBySaveDataAndRedirectActivity(R.id.menu_bar_right);
		mMobile = (LabelInputLayout) findViewById(R.id.password_setting_phone);
		mPasswd = (LabelInputLayout) findViewById(R.id.password_setting_pword);
		mPasswd.setEdit(true);
		mPasswd.getInfoEdit().setHint(getString(R.string.input_password));
		mPasswd.getInfoEdit().setPassword(true);
		
		if (getIntent().getStringExtra(GlobalConstants.REGISTER_MOBILE) == null) {
			String[] projection = { UserInfoTable.COLUMN_MOBILE };
			Cursor cursor = getContentResolver().query(GeneralContentProvider.USERINFO_CONTENT_URI,projection, null, null, null);
			String savedMobile = null;
			if (cursor != null && cursor.moveToFirst()) {
				savedMobile = cursor.getString(cursor.getColumnIndexOrThrow(UserInfoTable.COLUMN_MOBILE));
				// always close the cursor
				cursor.close();
			}
			
			if (savedMobile != null) {
				mMobile.getInfoInput().setText(StringUtils.formatPhoneNumber(savedMobile));
			}
		} else {
			mMobile.getInfoInput().setText(StringUtils.formatPhoneNumber(getIntent().getStringExtra(GlobalConstants.REGISTER_MOBILE)));
		}
		mMobile.getInfoInput().setTextColor(getResources().getColor(R.color.pswd_setting_phone));
		mMobile.getInfoTitle().setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
		mMobile.getInfoInput().setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
		mPasswd.getInfoTitle().setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
		
		mMobile.getInfoTitle().setText(getResources().getString(R.string.password_setting_phone));
		mPasswd.getInfoTitle().setText(getResources().getString(R.string.password_setting_pword));
		mPswdTips = (TextView) findViewById(R.id.pword_setting_tip);
		String tips = getResources().getString(R.string.pword_setting_tip);
		SpannableStringBuilder builder = new SpannableStringBuilder(tips);
		builder.setSpan(new ClickableSpan() {

			@Override
			public void updateDrawState(TextPaint ds) {
				ds.bgColor = 0;
				ds.linkColor = getResources().getColor(R.color.tips);
				super.updateDrawState(ds);
				ds.setUnderlineText(false);
			}

			@Override
			public void onClick(View widget) {
				Spannable spannable = ((Spannable) ((TextView) widget)
						.getText());
				Selection.removeSelection(spannable);
			}
		}, 10, 16, SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE);
		mPswdTips.setMovementMethod(LinkMovementMethod.getInstance());
		mPswdTips.setText(builder);
	}

	public void onEventMainThread(Message msg) {
		if (msg.obj instanceof InfoResult) {
			InfoResult infoResult = (InfoResult) msg.obj;
			switch (msg.what) {
			case R.id.register_set_psw:
				if (infoResult.getMessage().getStatus() == 0) {

					// 密码注册成功
					// 将密码保存到数据库中
					ContentValues values = new ContentValues();
					values.put(UserInfoTable.COLUMN_PASSWORD, mPasswd
							.getInfoEdit().getText().toString().toString());
					getContentResolver().update(
							GeneralContentProvider.USERINFO_CONTENT_URI,
							values, null, null);

					String[] projection = { UserInfoTable.COLUMN_PASSWORD };
					Cursor cursor = getContentResolver().query(
							GeneralContentProvider.USERINFO_CONTENT_URI,
							projection, null, null, null);
					if (cursor != null && cursor.moveToFirst()) {
						cursor.getString(cursor.getColumnIndexOrThrow(UserInfoTable.COLUMN_PASSWORD));
						// always close the cursor
						cursor.close();
					}

					Intent intent = new Intent(this, PurposeActivity.class);
					startActivity(intent);
				} else {
				}
				VToast.show(this, infoResult.getMessage().getMsg());
				break;
			case R.id.register_upload_avatar:
				if (infoResult.getMessage().getStatus() == 0) {
				}
				VToast.show(this, infoResult.getMessage().getMsg());
				break;
			}

		}

	}

	public boolean isPassswd(String pswd) {
		return pswd.length() > 6;
	}

	@Override
	public void confirmNextAction() {
		mRegisterEventLogic.cancel(R.id.register_set_psw);
		mRegisterEventLogic.setPassword(mPasswd.getInfoEdit().toString().toString());
	}

}
