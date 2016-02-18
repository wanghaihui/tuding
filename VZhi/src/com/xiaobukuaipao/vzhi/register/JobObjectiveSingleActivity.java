package com.xiaobukuaipao.vzhi.register;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;

import com.xiaobukuaipao.vzhi.AppActivityManager;
import com.xiaobukuaipao.vzhi.R;
import com.xiaobukuaipao.vzhi.fragment.CallBackFragment;
import com.xiaobukuaipao.vzhi.fragment.JobObjectiveCorpTypeFragment;
import com.xiaobukuaipao.vzhi.fragment.JobObjectiveIndustryFragment;
import com.xiaobukuaipao.vzhi.fragment.JobObjectivePositionFragment;
import com.xiaobukuaipao.vzhi.fragment.JobObjectiveStateFragment;
import com.xiaobukuaipao.vzhi.fragment.OnBackRequest;
import com.xiaobukuaipao.vzhi.util.GlobalConstants;
import com.xiaobukuaipao.vzhi.wrap.ProfileWrapActivity;

public class JobObjectiveSingleActivity extends ProfileWrapActivity implements
		OnBackRequest {

	@Override
	public void initUIAndData() {
		setContentView(R.layout.activity_job_objective_select_info);
		CallBackFragment fragment = null;
		switch (getIntent().getIntExtra(GlobalConstants.SEQ_STRING, 0)) {
		case 1:
			fragment = new JobObjectiveIndustryFragment();
			break;
		case 2:
			fragment = new JobObjectivePositionFragment();
			break;
		case 3:
			fragment = new JobObjectiveCorpTypeFragment();
			break;
		case 4:
			fragment = new JobObjectiveStateFragment();
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
		AppActivityManager.getInstance().finishActivity(JobObjectiveSingleActivity.this);
	}

	@Override
	public void onEventMainThread(Message msg) {
		
	}
}
