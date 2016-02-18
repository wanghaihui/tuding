package com.xiaobukuaipao.vzhi.im;

public class ImMessageManager extends ImManager {

	public static final String TAG = ImMessageManager.class.getSimpleName();

	private static final ImMessageManager messageManager = new ImMessageManager();
	
	private ImMessageManager() {
		
	}
	
	public static ImMessageManager getInstance() {
		return messageManager;
	}
	
}
