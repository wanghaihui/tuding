package com.xiaobukuaipao.vzhi.im;

public class NetStateManager {
	// 是否联网--默认联网
	private boolean mbOnline = true;

    private NetStateManager() {
    	
    }

    private static class SingletonHolder {
        private static NetStateManager instance = new NetStateManager();
    }

    public static NetStateManager getInstance() {
        return SingletonHolder.instance;
    }

    public void setState(boolean bOnline) {
        mbOnline = bOnline;
    }

    public boolean isOnline() {
        return mbOnline;
    }
}
