package com.xiaobukuaipao.vzhi.event;

public class GroupMessageEvent {
	private final int what;
	
	public GroupMessageEvent(final int what) {
		this.what = what;
	}
	
	public int getWhat() {
		return what;
	}
}
