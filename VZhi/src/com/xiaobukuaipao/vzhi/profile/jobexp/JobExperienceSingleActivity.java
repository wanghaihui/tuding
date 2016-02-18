package com.xiaobukuaipao.vzhi.profile.jobexp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;

import com.xiaobukuaipao.vzhi.AppActivityManager;
import com.xiaobukuaipao.vzhi.R;
import com.xiaobukuaipao.vzhi.fragment.CallBackFragment;
import com.xiaobukuaipao.vzhi.fragment.JobCompanyFragment;
import com.xiaobukuaipao.vzhi.fragment.JobDescripFragment;
import com.xiaobukuaipao.vzhi.fragment.JobNameFragment;
import com.xiaobukuaipao.vzhi.fragment.OnBackRequest;
import com.xiaobukuaipao.vzhi.util.GlobalConstants;
import com.xiaobukuaipao.vzhi.wrap.ProfileWrapActivity;

public class JobExperienceSingleActivity extends ProfileWrapActivity implements
		OnBackRequest {

	@Override
	public void initUIAndData() {
		setContentView(R.layout.activity_job_experience_info);
		CallBackFragment fragment = null;
		switch (getIntent().getIntExtra(GlobalConstants.SEQ_STRING, 0)) {
		case R.id.register_jobexp_company:
			fragment = new JobCompanyFragment();
			break;
		case R.id.register_jobexp_name:
			fragment = new JobNameFragment();
			break;
		case R.id.register_jobexp_desc:
			fragment = new JobDescripFragment();
			break;
		default:
			return;
		}
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			fragment.setArguments(extras);
			getSupportFragmentManager().beginTransaction().replace(R.id.common_fragment_section_id, fragment).commit();
		}else{
			try {
				throw new Throwable("extras must not null");
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}

	}

	@Override
	public void backActivity() {
	}


	@Override
	public void onBackData(Bundle bundle) {

		Intent intent = this.getIntent();
		intent.putExtras(bundle);
		setResult(RESULT_OK, intent);
		AppActivityManager.getInstance().finishActivity(JobExperienceSingleActivity.this);
		// finish();
	}


	@Override
	public void onEventMainThread(Message msg) {

	}
}
