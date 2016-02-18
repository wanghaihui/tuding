package com.xiaobukuaipao.vzhi.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ReconnectionReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		/*PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
		PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "tuding_reconnecting_wakelock");
		wl.acquire();

		try {
			// 此时代表网络连接-startImService--此时会继续执行Socket连接请求
            Intent intentService = new Intent(context, ImService.class);
            context.startService(intentService);
		} finally {
			wl.release();
		}*/
		
	}

}
