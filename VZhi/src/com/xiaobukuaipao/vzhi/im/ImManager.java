package com.xiaobukuaipao.vzhi.im;

import android.content.Context;

public class ImManager {

	protected Context context;
	public void setContext(Context context) {
		if (context == null) {
			throw new RuntimeException("context is null");
		}
		this.context = context;
	}
	
}
