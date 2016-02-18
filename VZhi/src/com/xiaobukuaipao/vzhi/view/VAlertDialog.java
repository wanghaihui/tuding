package com.xiaobukuaipao.vzhi.view;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;

public class VAlertDialog extends AlertDialog{

	public VAlertDialog(Context context, boolean cancelable,
			OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
		init();
	}

	public VAlertDialog(Context context, int theme) {
		super(context, theme);
		init();
	}

	public VAlertDialog(Context context) {
		super(context);
		init();
	}

	private void init(){
		getWindow().getDecorView().setBackgroundColor(Color.TRANSPARENT);
		
		
	}
	
	
}
