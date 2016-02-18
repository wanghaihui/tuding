package com.xiaobukuaipao.vzhi.event;

public class TFailedEvent {
	private final String tFailed;
	
	public TFailedEvent(final String tFailed) {
		this.tFailed = tFailed;
	}
	
	public String getTFailed() {
		return tFailed;
	}
}
