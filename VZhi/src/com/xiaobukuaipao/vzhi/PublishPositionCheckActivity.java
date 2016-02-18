package com.xiaobukuaipao.vzhi;

import android.content.Intent;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.xiaobukuaipao.vzhi.util.GlobalConstants;
import com.xiaobukuaipao.vzhi.util.SpannableKeyWordBuilder;
import com.xiaobukuaipao.vzhi.wrap.ProfileWrapActivity;

/**
 * 编辑职位信息
 */
public class PublishPositionCheckActivity extends ProfileWrapActivity implements OnClickListener {
	
	public static final String TAG = PublishPositionCheckActivity.class.getSimpleName();
	public static final int SCAN_CODE = 1;
	
	private TextView mPublishTips;
	@Override
	public void initUIAndData() {
		setContentView(R.layout.activity_publish_position_check);
		setHeaderMenuByCenterTitle(R.string.publish_position_str);
		setHeaderMenuByLeft(this);
		//
		mPublishTips = (TextView) findViewById(R.id.publish_tips);
		String website = getString(R.string.website);
		
		SpannableKeyWordBuilder builder = new SpannableKeyWordBuilder(getString(R.string.publish_position_check_tips,website));
		builder.setMode(SpannableKeyWordBuilder.MODE_KEYWORD);
		builder.setKeywords(website);
		mPublishTips.setText(builder.build());
		
		findViewById(R.id.publish_position_code_toweb).setOnClickListener(this);
		findViewById(R.id.publish_position_phone).setOnClickListener(this);
	}


	@Override
	public void onEventMainThread(Message msg) {
	}

	@Override
	public void onClick(View v) {
		
		Intent intent = null;
		switch (v.getId()) {
		case R.id.publish_position_code_toweb:
			MobclickAgent.onEvent(PublishPositionCheckActivity.this,"smfbzw");
			intent = new Intent(this, TCodeScannerActivity.class);
			startActivityForResult(intent, SCAN_CODE);
			break;
		case R.id.publish_position_phone:
			MobclickAgent.onEvent(PublishPositionCheckActivity.this,"sjfbzw");
			intent = new Intent(this, PublishPositionActivity.class);
			startActivity(intent);
			break;
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case SCAN_CODE:
			if (resultCode == RESULT_OK) {
				String result = data.getStringExtra("scan_result");
				Intent intent = new Intent();
				intent.putExtra(GlobalConstants.INNER_URL, result);
				intent.setClass(this, WebSearchActivity.class);
				startActivity(intent);
			} else if (resultCode == RESULT_CANCELED) {
				
			}
			break;
		default:
			break;
		}
	}
}
