package com.xiaobukuaipao.vzhi.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

public class CustomScrollview extends ScrollView{

	public CustomScrollview(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public CustomScrollview(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public CustomScrollview(Context context) {
		super(context);
	}

	@Override
	public void scrollTo(int x, int y) {
		super.scrollTo(x, y);
	}
		

}
