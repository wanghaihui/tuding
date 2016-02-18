package com.xiaobukuaipao.vzhi.event;

import android.util.Log;

public class P2PMessageEvent {
	private static final String TAG = P2PMessageEvent.class.getSimpleName();
	private int what;
	
	public P2PMessageEvent( int what) {
		Log.i(TAG, "what : " + 1);
		this.what = what;
	}
	
	public int getWhat() {
		return what;
	}
}
