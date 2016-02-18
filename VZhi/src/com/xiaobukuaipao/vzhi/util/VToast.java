package com.xiaobukuaipao.vzhi.util;

import android.content.Context;
import android.widget.Toast;

public class VToast {

	public static int time = 500;
	
	public static void show(Context context, String text){
		Toast toast = Toast.makeText(context, text, time);
		toast.show();
	}
}
