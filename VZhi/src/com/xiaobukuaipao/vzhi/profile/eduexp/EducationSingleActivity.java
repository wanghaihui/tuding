package com.xiaobukuaipao.vzhi.profile.eduexp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;

import com.xiaobukuaipao.vzhi.AppActivityManager;
import com.xiaobukuaipao.vzhi.R;
import com.xiaobukuaipao.vzhi.fragment.CallBackFragment;
import com.xiaobukuaipao.vzhi.fragment.EducationDegreeFragment;
import com.xiaobukuaipao.vzhi.fragment.EducationMajorFragment;
import com.xiaobukuaipao.vzhi.fragment.EducationSchoolFrament;
import com.xiaobukuaipao.vzhi.fragment.OnBackRequest;
import com.xiaobukuaipao.vzhi.util.GlobalConstants;
import com.xiaobukuaipao.vzhi.util.Logcat;
import com.xiaobukuaipao.vzhi.wrap.ProfileWrapActivity;

public class EducationSingleActivity extends ProfileWrapActivity implements
		OnBackRequest {

	private final static String  TAG = EducationSingleActivity.class.getSimpleName();
	@Override
	public void backActivity() {
		
	}


	@Override
	public void onBackData(Bundle bundle) {
		Intent intent = new Intent();
		intent.putExtras(bundle);
		setResult(RESULT_OK, intent);
		AppActivityManager.getInstance().finishActivity(EducationSingleActivity.this);
		// finish();
	}

	@Override
	public void initUIAndData() {
		setContentView(R.layout.activity_education_info);
		CallBackFragment fragment = null;
		switch (getIntent().getIntExtra(GlobalConstants.SEQ_STRING, 0)) {
		case R.id.register_edu_degree_id:
			fragment = new EducationDegreeFragment();
			break;
		case R.id.register_edu_school_id:
			fragment = new EducationSchoolFrament();
			break;
		case R.id.register_edu_major_id:
			fragment = new EducationMajorFragment();
			break;
		}
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			fragment.setArguments(extras);
			getSupportFragmentManager().beginTransaction().replace(R.id.common_fragment_section_id, fragment).commit();
		}else{
			try {
				throw new Throwable("extras must not null");
			} catch (Throwable e) {
				Logcat.d(TAG, e.getMessage());
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onEventMainThread(Message msg) {
		
	}
}
