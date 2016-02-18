package com.xiaobukuaipao.vzhi.services;

import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

public class ImServiceHelper {
	private static final String TAG = ImServiceHelper.class.getSimpleName();

	public interface OnImServiceListner {
		void onAction(String action, Intent intent,BroadcastReceiver broadcastReceiver);
	}
	
	private class ImBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context ctx, Intent intent) {
			String action = intent.getAction();
			Log.i(TAG, "onReceiver action: " + action);
			ImServiceHelper.this.listener.onAction(action, intent, this);
		}
	}
	
	private ImBroadcastReceiver imReceiver = new ImBroadcastReceiver();
	public OnImServiceListner listener;
	private boolean registered = false;
	
	public void registerActions(Context ctx, List<String> actions, OnImServiceListner actionlistener) {
		listener = actionlistener;
		
		if (actions != null) {
			IntentFilter intentFilter = new IntentFilter();

			for (String action : actions) {
				intentFilter.addAction(action);
			}

			ctx.registerReceiver(imReceiver, intentFilter);
			registered = true;
		}

	}

	public void unregisterActions(Context ctx) {
		if (registered) {
			registered = false;
			ctx.unregisterReceiver(imReceiver);
		}
	}
	
}
