package com.xiaobukuaipao.vzhi;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

public class DesignFragmentActivity extends BaseFragmentActivity {

	private TextView textTitleView;
	/**
	 * ActionBar 左侧菜单显示
	 */
	public void setHeaderMenuByLeft(final Activity activity) {
		
		FrameLayout frameLayout = (FrameLayout) activity.findViewById(R.id.menu_bar_back_btn_layout);
		frameLayout.setVisibility(View.VISIBLE);
		frameLayout.setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						backActivity();
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
		textTitleView = (TextView) findViewById(R.id.ivTitleName);
		textTitleView.setText(getResources().getString(title));
	}
	
	/**
	 * ActionBar中间的Title
	 * 
	 * @param title
	 */
	public void setHeaderMenuByCenterTitle(String title) {
		textTitleView = (TextView) findViewById(R.id.ivTitleName);
		textTitleView.setText(title);
	}
	
	public void backActivity() {

	}
	
	/**
	 * ActionBar 左侧菜单显示
	 */
	public void setHeaderMenuByRight(final Activity activity) {
		
		FrameLayout frameLayout = (FrameLayout) activity.findViewById(R.id.menu_bar_setting_btn_layout);
		frameLayout.setVisibility(View.VISIBLE);
		frameLayout.setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						popupSetting();
					}
				});
	}
	
	public void popupSetting() {
		
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AppActivityManager.getInstance().addActivity(this);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.updateOnlineConfig(this);//上传策略
	    MobclickAgent.onResume(this);          //统计时长
	    
	}
	
	@Override
	protected void onPause() {
	    super.onPause();
	    MobclickAgent.onPause(this);
	}
	
	@Override
	protected void onDestroy() {
		AppActivityManager.getInstance().finishActivity(this);
		super.onDestroy();
	}
	
	public TextView getTitleView() {
		return textTitleView;
	}

}
