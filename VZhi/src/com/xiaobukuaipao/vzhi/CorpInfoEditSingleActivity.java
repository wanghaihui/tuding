package com.xiaobukuaipao.vzhi;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;

import com.xiaobukuaipao.vzhi.fragment.CallBackFragment;
import com.xiaobukuaipao.vzhi.fragment.CompanyAddressFragment;
import com.xiaobukuaipao.vzhi.fragment.CompanyBenefitsFragment;
import com.xiaobukuaipao.vzhi.fragment.CompanyDescripFragment;
import com.xiaobukuaipao.vzhi.fragment.CompanyIndustryFragment;
import com.xiaobukuaipao.vzhi.fragment.OnBackRequest;
import com.xiaobukuaipao.vzhi.util.GlobalConstants;
import com.xiaobukuaipao.vzhi.wrap.ProfileWrapActivity;

public class CorpInfoEditSingleActivity extends ProfileWrapActivity implements
		OnBackRequest {

	@Override
	public void initUIAndData() {
		setContentView(R.layout.activity_company_edit_select_info);
		CallBackFragment fragment = null;
		switch (getIntent().getIntExtra(GlobalConstants.SEQ_STRING, 0)) {
			case 1:
				fragment = new CompanyIndustryFragment();
				break;
			case 2:
				fragment = new CompanyBenefitsFragment();
				break;
			case 3:
				fragment = new CompanyDescripFragment();
				break;
			case 4:
				fragment = new CompanyAddressFragment();
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
		AppActivityManager.getInstance().finishActivity(CorpInfoEditSingleActivity.this);
		// finish();
	}

	@Override
	public void finish() {
		super.finish();
	}

	@Override
	public void onEventMainThread(Message msg) {

	}
}
