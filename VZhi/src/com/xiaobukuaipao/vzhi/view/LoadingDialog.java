package com.xiaobukuaipao.vzhi.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xiaobukuaipao.vzhi.R;


public class LoadingDialog extends Dialog {
		
	private RelativeLayout mParentLayout;
	private TextView mLoadingTips;
	
	private String loadingTipStr;
	
	public LoadingDialog(Context context, boolean cancelable,
			OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
	}

	public LoadingDialog(Context context, int theme) {
		super(context, theme);
	}

	public LoadingDialog(Context context) {
		super(context);
		
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initUiAndData();
	}
	
	private void initUiAndData() {	
		
		mParentLayout =  (RelativeLayout) View.inflate(getContext(), R.layout.material_dialog_loading, null);

		mLoadingTips = (TextView) mParentLayout.findViewById(R.id.dialog_loading_tips);
		mLoadingTips.setText(loadingTipStr);
		
		if(getWindow().getDecorView() instanceof ViewGroup){
			ViewGroup viewGroup = (ViewGroup)getWindow().getDecorView();
			viewGroup.removeAllViews();
			viewGroup.addView(mParentLayout);
			viewGroup.setBackgroundColor(0x0000);
		}
		
		setCanceledOnTouchOutside(false);
	}
	
	public void setLoadingTipStr(String loadingTipStr) {
		this.loadingTipStr = loadingTipStr;
	}
	public String getLoadingTipStr() {
		return loadingTipStr;
	}
}

