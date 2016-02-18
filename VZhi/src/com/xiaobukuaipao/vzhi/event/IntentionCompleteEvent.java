package com.xiaobukuaipao.vzhi.event;

public class IntentionCompleteEvent {
	private boolean complete;
	
	public IntentionCompleteEvent( boolean complete) {
		this.complete = complete;
	}
	
	public boolean getComplete() {
		return complete;
	}
}
