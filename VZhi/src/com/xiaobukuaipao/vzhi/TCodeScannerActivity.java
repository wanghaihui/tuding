package com.xiaobukuaipao.vzhi;

import android.app.Activity;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.zxing.client.android.CaptureActivity;
import com.xiaobukuaipao.vzhi.util.SpannableKeyWordBuilder;
public final  class TCodeScannerActivity extends CaptureActivity implements SurfaceHolder.Callback {

	private TextView mCodeTips;

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		Window window = getWindow();
		window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.activity_tcode_scanner);
		setHeaderMenuByCenterTitle(R.string.twcode_scanner);
		setHeaderMenuByLeft(this);
		
		mCodeTips = (TextView) findViewById(R.id.code_tips);
		String website = "jobooking.cn";
		SpannableKeyWordBuilder keyWordBuilder = new SpannableKeyWordBuilder(getString(R.string.twcode_scanner_tips,website));
		keyWordBuilder.setKeywords(website);
		keyWordBuilder.setMode(SpannableKeyWordBuilder.MODE_KEYWORD);
		mCodeTips.setText(keyWordBuilder.build());
		
		
		
		
	}

	/**
	 * ActionBar 左侧菜单显示
	 */
	public void setHeaderMenuByLeft(final Activity activity) {
		FrameLayout frameLayout = (FrameLayout) findViewById(R.id.menu_bar_back_btn_layout);
		frameLayout.setVisibility(View.VISIBLE);
		frameLayout.setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View view) {
						activity.onBackPressed();
					}
				});
	}
	
	/**
	 * ActionBar中间的Title
	 * 
	 * @param title
	 */
	public void setHeaderMenuByCenterTitle(int title) {
		TextView textTitleView = (TextView) findViewById(R.id.ivTitleName);
		textTitleView.setText(getResources().getString(title));
	}

	/**
	 * ActionBar中间的Title
	 * 
	 * @param title
	 */
	public void setHeaderMenuByCenterTitle(String title) {
		TextView textTitleView = (TextView) findViewById(R.id.ivTitleName);
		textTitleView.setText(title);
	}
}
