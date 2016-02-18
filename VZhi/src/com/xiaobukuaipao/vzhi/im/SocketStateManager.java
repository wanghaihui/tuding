package com.xiaobukuaipao.vzhi.im;

public class SocketStateManager {
	// Socket是否连接
	private boolean mbOnline = false;

    private SocketStateManager() {

    }

    private static class SingletonHolder {
        private static SocketStateManager instance = new SocketStateManager();
    }

    public static SocketStateManager getInstance() {
        return SingletonHolder.instance;
    }

    public void setState(boolean bOnline) {
        mbOnline = bOnline;
    }

    public boolean isOnline() {
        return mbOnline;
    }
}
