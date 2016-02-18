package com.xiaobukuaipao.vzhi.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.xiaobukuaipao.vzhi.im.ImUtils;
import com.xiaobukuaipao.vzhi.im.NetStateManager;
import com.xiaobukuaipao.vzhi.im.SocketStateManager;

public class ConnectionChangeReceiver extends BroadcastReceiver {
	
	 // 是否断开连接
	 private boolean bDisconnected = false;
	 private NetStateManager nsmInstance = NetStateManager.getInstance();
	 private SocketStateManager ssmInstance = SocketStateManager.getInstance();

	@Override
	public void onReceive(Context context, Intent intent) {
		
		String action = intent.getAction();
		
		/*if (action.equals(ImActions.ACTION_RECONNECT)) {
			// 此时是Socket重连策略--可能存在问题，待测试
			PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
			PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "tuding_reconnecting_wakelock");
			wl.acquire();
			
			try {
				// 此时代表网络连接-startImService--此时会继续执行Socket连接请求
	            Intent intentService = new Intent(context, ImService.class);
	            context.startService(intentService);
			} finally {
				wl.release();
			}
		} else */
		
		if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
			
			boolean success = false;
	        try {
	            // 获得网络连接服务
	            ConnectivityManager cm = (ConnectivityManager) context
	                    .getSystemService(Context.CONNECTIVITY_SERVICE);
	            
	            if (null != cm) {
	                NetworkInfo ni = cm.getActiveNetworkInfo();
	                if (null != ni && ni.isAvailable()) {
	                    success = true;
	                }
	            }
	            
	        } catch (Exception e) {
	            success = false;
	        }
	        
	        
	        // 如果断开连接
	        if (!success) {
	        	// 未连接设为true
	            bDisconnected = true;
	            if (ImUtils.isInIm(context)) {
	                Toast.makeText(context, "网络未连接", Toast.LENGTH_SHORT).show();
	            }
	            nsmInstance.setState(false);
	            ssmInstance.setState(false);
	        } else {
	            if (bDisconnected) {
	                nsmInstance.setState(true);
	            }
	            bDisconnected = false;
	            
	            // 此时代表网络连接-startImService--此时会继续执行Socket连接请求
	            /*Intent intentService = new Intent(context, ImService.class);
	            context.startService(intentService);*/
	            // 此时连接socket
	            // Log.i(TAG, "ConnectionChangeReceiver relogin");
	            // ImLoginManager.getInstance().relogin();
	        }
		}
        
	}

}
