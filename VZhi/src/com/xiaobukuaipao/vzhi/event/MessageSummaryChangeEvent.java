package com.xiaobukuaipao.vzhi.event;

public class MessageSummaryChangeEvent {
	private final int what;
	
	public MessageSummaryChangeEvent(final int what) {
		this.what = what;
	}
	
	public int getWhat() {
		return what;
	}
}
