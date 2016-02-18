package com.xiaobukuaipao.vzhi.register;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;

import com.xiaobukuaipao.vzhi.AppActivityManager;
import com.xiaobukuaipao.vzhi.R;
import com.xiaobukuaipao.vzhi.fragment.CallBackFragment;
import com.xiaobukuaipao.vzhi.fragment.HrCompanyFragment;
import com.xiaobukuaipao.vzhi.fragment.OnBackRequest;
import com.xiaobukuaipao.vzhi.util.GlobalConstants;
import com.xiaobukuaipao.vzhi.wrap.ProfileWrapActivity;

public class HrSingleActivity extends ProfileWrapActivity implements
		OnBackRequest {

	@Override
	public void initUIAndData() {
		setContentView(R.layout.activity_hr_select_info);
		CallBackFragment fragment = null;
		switch (getIntent().getIntExtra(GlobalConstants.SEQ_STRING, 0)) {
		case 1:
			fragment = new HrCompanyFragment();
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
		AppActivityManager.getInstance().finishActivity(HrSingleActivity.this);
		// finish();
	}

	@Override
	public void onEventMainThread(Message msg) {

	}
}
