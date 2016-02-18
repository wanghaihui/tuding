package com.xiaobukuaipao.vzhi.im;

import java.util.ArrayList;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Message;
import android.os.PowerManager;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.xiaobukuaipao.vzhi.AppActivityManager;
import com.xiaobukuaipao.vzhi.ChatPersonActivity;
import com.xiaobukuaipao.vzhi.R;
import com.xiaobukuaipao.vzhi.VZhiApplication;
import com.xiaobukuaipao.vzhi.contentprovider.GeneralContentProvider;
import com.xiaobukuaipao.vzhi.database.CookieTable;
import com.xiaobukuaipao.vzhi.database.MultiUserDatabaseHelper;
import com.xiaobukuaipao.vzhi.event.ImEventLogic;
import com.xiaobukuaipao.vzhi.event.InfoResult;
import com.xiaobukuaipao.vzhi.manager.GeneralDbManager;
import com.xiaobukuaipao.vzhi.register.LoginActivity;
import com.xiaobukuaipao.vzhi.register.SplashActivity;
import com.xiaobukuaipao.vzhi.services.ImService;
import com.xiaobukuaipao.vzhi.services.ImServiceHelper;
import com.xiaobukuaipao.vzhi.services.ImServiceHelper.OnImServiceListner;
import com.xiaobukuaipao.vzhi.util.GlobalConstants;
import com.xiaobukuaipao.vzhi.util.Logcat;
import com.xiaobukuaipao.vzhi.util.NetworkUtils;
import com.xiaobukuaipao.vzhi.util.StringUtils;

public class ImLoginManager extends ImManager implements OnImServiceListner {
	public static final String TAG = ImLoginManager.class.getSimpleName();

	private static final ImLoginManager loginManager = new ImLoginManager();
	
	private SocketThread loginServerThread;
	
	public final static String SOCKET_MSG_END = "\r\n";
	
	private ImServiceHelper imServiceHelper = new ImServiceHelper();
	
	private int connectTime = 0;
	
	private boolean everLogined = false;
	
	private boolean isLogingin = false;
	
	// 百度云推送
	private String userId = null;
	private String channelId = null;
	
	private ImEventLogic mImEventLogic;
	
	public void setIsLogingin(boolean isLogingin) {
		this.isLogingin = isLogingin;
	}
	
	public void setEverLogined(boolean everLogined) {
		this.everLogined = everLogined;
	}
	public boolean getEverLogined() {
		return this.everLogined;
	}
	
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getUserId() {
		return this.userId;
	}
	
	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}
	public String getChannelId() {
		return this.channelId;
	}
	
	// 重传策略--基本次数是三次
	private final int RECONNECT_TIME_BASE = 3;
	private final int RECONNECT_TIME_SECOND = 10;
	private boolean reconnecting = false;
	private int reconnect_index = 0;
	// 前三次立即重连
	private int interval = 0;
	
	private ImLoginManager() {
		mImEventLogic = new ImEventLogic();
		mImEventLogic.register(this);
	}
	
	public static ImLoginManager getInstance() {
		return loginManager;
	}
	
	
	public void register() {
		List<String> actions = new ArrayList<String>();
		actions.add(ImActions.ACTION_RECONNECT_TUDING);
		// actions.add(ConnectivityManager.CONNECTIVITY_ACTION);
		imServiceHelper.registerActions(context, actions, this);
	}
	
	public void unRegister() {
		imServiceHelper.unregisterActions(context);
	}
	
	public void connectServer(String server) {
		Log.i(TAG, "connectServier " + connectTime + "times" );
		connectTime++;
		// 首先获得IP和Port
		String ip = null, port = null;
		if (server != null && server.length() >0) {
			String ipPort[] = server.split(":");
			ip = ipPort[0];
			port = ipPort[1];
		}
		
		loginServerThread = new SocketThread(ip, Integer.valueOf(port).intValue(), new LoginServerHandler());
		loginServerThread.start();
	}
	
	public void disconnectServer() {

		if (loginServerThread != null) {
			Log.i(TAG, "socket is shut down");
			loginServerThread.close();
		}
		
	}
	
	// 如果没有过登录，则进行首次登录
	public void firstLogin() {
		if (!everLogined) {
			mImEventLogic.getSocketIpAndPort();
			isLogingin = true;
		}
	}
	
	public boolean relogin() {
		Log.i(TAG, "relogin");
		
		if (isLogingin) {
			return false;
		}
		if (GeneralContentProvider.dbHelper != null) {
			mImEventLogic.getSocketIpAndPort();
		}
		
		isLogingin = true;
		return true;
	}
	
	public void onEventMainThread(Message msg) {
		if (msg.obj instanceof InfoResult) {
			InfoResult infoResult = (InfoResult) msg.obj;
			/*if(StringUtils.isNotEmpty(infoResult.getResult())){
				JSONObject parseObject = JSONObject.parseObject(infoResult.getResult());
				
				if(parseObject != null){
					
					Integer status = parseObject.getInteger(GlobalConstants.JSON_STATUS);
					
					if(status != null){
						if(status  == 100){
							
							
							
						
						}
					}
				}*/
			
			
			switch (msg.what) {
					
				case R.id.socket_ip_and_port:
					JSONObject mJSONResult = (JSONObject) JSONObject.parse(infoResult.getResult());
					String server = mJSONResult.getString(GlobalConstants.JSON_SERVER);
					if (!StringUtils.isEmpty(server)) {
						// 启动聊天服务
						Log.i(TAG, "is empty");
						ImLoginManager.getInstance().connectServer(server);
					} else {
						boolean flag = false;
						
						if (StringUtils.isNotEmpty(infoResult.getResult())) {
							JSONObject parseObject = JSONObject.parseObject(infoResult.getResult());
							
							if(parseObject != null){
								
								Integer status = parseObject.getInteger(GlobalConstants.JSON_STATUS);
								
								if(status != null){
									if(status  == 100){
										flag = true;
									}
								}
							}
							if (!flag) {
								mImEventLogic.getSocketIpAndPort();
							}
						}
					}
					break;
				/*case R.id.social_logout:
					if (infoResult.getMessage().getStatus() == 0) {
						// 先清除Cookie
						GeneralDbManager.getInstance().removeCookieInfo();
						// 关闭所有的数据库
						MultiUserDatabaseHelper.getInstance().closeDatabase();
						
						ImLoginManager.getInstance().setEverLogined(false);
						//  停止ImService服务
						Intent stopImServiceIntent = new Intent(context, ImService.class);
						context.stopService(stopImServiceIntent);
						
						AppActivityManager.getInstance().finishAllActivity();
						Intent intent = new Intent(context, LoginActivity.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						intent.putExtra("another_login", true);
						context.startActivity(intent);
					} else {
						
					}
					break;*/
			}
		} else {
			// 重新登录
			// 将以前的Http请求取消
			mImEventLogic.cancel(R.id.socket_ip_and_port);
			Log.i(TAG, "Http Failed");
			onLoginFailed();
		}
	}
	
	public void onLoginServerConnected() {
		reconnecting = false;
		everLogined = true;
		isLogingin = false;
		// 连接上socket服务器后发送登录消息
		sendLoginMessage();
	}
	
	// 断开连接
	public void onLoginServerDisconnected() {
		// 登录失败
		Log.i(TAG, "onLoginServer Disconnected");
		onLoginFailed();
	}
	
	// 未连接
	public void onLoginServerUnconnected() {
		Log.i(TAG, "onLoginServer Unconnected");
		onLoginFailed();
	}
	
	// Socket连接失败
	public void onLoginFailed() {
		Log.i(TAG, "onLoginFailed");
		
		if (GeneralContentProvider.dbHelper != null) {
			// 此时进行重连策略
			reconnecting = true;
			isLogingin = false;
			everLogined = false;
			if (reconnecting) {
				Log.i(TAG, "login failed and reconnect");
				// Exponential backoff strategy--指数退避策略
				// scheduleReconnect(RECONNECT_TIME_BASE * (2 << reconnect_index++));
				// 安排重连
				if (reconnect_index < RECONNECT_TIME_BASE) {
					// 前三次是0秒
					scheduleReconnect(interval);
				} else {
					// 最大五分钟
					if (interval < 10) {
						if (reconnect_index < RECONNECT_TIME_SECOND) {
							scheduleReconnect(5);
						} else {
							interval = interval +5;
							scheduleReconnect(interval);
						}
					} else {
						scheduleReconnect(interval);
					}
				}
				
				reconnect_index++;
			}
		}
		/*if (reconnecting) {
			// Exponential backoff strategy
			scheduleReconnect(RECONNECT_TIME_BASE * (2 << reconnect_index++));
		}*/
	}
	
	// 安排重连
	private void scheduleReconnect(int seconds) {
		Intent intent = new Intent(ImActions.ACTION_RECONNECT_TUDING);
		PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
		
		if (pi == null) {
			return;
		}
		
		Log.i(TAG, "schedule reconnect");
		AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		// AlarmManager.RTC_WAKEUP表示闹钟在睡眠状态下会唤醒系统并执行提示功能，该状态下闹钟使用绝对时间，状态值为0
		am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + seconds * 1000, pi);
		
		// 通过计时器
		/*java.util.Timer timer = new java.util.Timer(true);
		
		TimerTask task = new TimerTask() {   
			public void run() {
				relogin();
			}
		};
		
		timer.schedule(task, seconds * 1000);
		timer.cancel();*/
	}
	
	private void sendLoginMessage() {
		if (loginServerThread != null) {
			// 此时从数据库Cookie表取出uid和T票
			Long uid = null;
			String[] projection = { CookieTable.COLUMN_ID };
			Cursor cursor = context.getContentResolver().query(GeneralContentProvider.COOKIE_CONTENT_URI, projection, null, null, null);
			if (cursor != null && cursor.moveToFirst()) {
				uid = (long) cursor.getInt(cursor.getColumnIndexOrThrow(CookieTable.COLUMN_ID));
				// always close the cursor
				cursor.close();
			}
			
			StringBuilder data = new StringBuilder();
			data.append("{\"type\":");
			data.append(IMConstants.TYPE_LOGIN);
			data.append(",");
			data.append("\"uid\":");
			data.append(uid);
			data.append(",");
			data.append("\"t\":");
			data.append("\"");
			data.append(VZhiApplication.mCookie_T);
			data.append("\"");
			
			if (userId != null) {
				data.append(",");
				data.append("\"userId\":");
				data.append("\"");
				data.append(userId);
				data.append("\"");
			}
			
			if (channelId != null) {
				data.append(",");
				data.append("\"channelId\":");
				data.append("\"");
				data.append(channelId);
				data.append("\"");
			}
			
			data.append(",");
			data.append("\"os\":");
			data.append("\"");
			data.append("android");
			data.append("\"");
			
			data.append("}");
			data.append(SOCKET_MSG_END);
			
			loginServerThread.sendPacket(data.toString());
		}
	}
	
	/**
	 * 发送推送验证，以便服务器可以向客户端推送消息
	 */
	public void sendClientReady() {
		if (loginServerThread != null) {
			StringBuilder data = new StringBuilder();
			data.append("{\"type\":");
			data.append(IMConstants.TYPE_CLIENT_READY);
			data.append(",");
			data.append("\"body\":");
			data.append("\"ready\"");
			data.append("}");
			data.append(SOCKET_MSG_END);
			
			loginServerThread.sendPacket(data.toString());
		}
	}
	
	
	/**
	 * 多端登录冲突
	 * @param message
	 */
	public void conflictAnotherLogin() {
		Logcat.d("test", "conflictAnotherLogin");
		// 首先退出登录
		// mImEventLogic.logout("android", ImLoginManager.getInstance().getUserId(), ImLoginManager.getInstance().getChannelId());
		
		// 此时，先关闭service，之后，弹出提示框，提示用户是重新登录还是取消; 如果重新登录，则打开Service; 否则，跳转到登录界面LoginActivity
		// Intent stopImServiceIntent = new Intent(context, ImService.class);
		// context.stopService(stopImServiceIntent);
		
		
		
		// 先清除Cookie
		GeneralDbManager.getInstance().removeCookieInfo();
		
		SharedPreferences sp = context.getSharedPreferences("tuding_uid", Context.MODE_PRIVATE);  
        sp.edit().putLong("uid", Long.valueOf(0)).commit();
        sp.edit().putBoolean(GlobalConstants.INTENTION_COMPLETE, false).commit();
		
		ImLoginManager.getInstance().setEverLogined(false);
		//  停止ImService服务
		Intent stopImServiceIntent = new Intent(context, ImService.class);
		context.stopService(stopImServiceIntent);
		
		AppActivityManager.getInstance().finishAllActivity();
		
		Intent intent = new Intent(context, LoginActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra("another_login", true);
		context.startActivity(intent);
		
		// 关闭所有的数据库
		MultiUserDatabaseHelper.getInstance().closeDatabase();
	}
	
	/**
	 * 登录失败请求
	 * @param message
	 */
	public void loginFailed() {
		Logcat.d("test", "loginFailed");
		// 首先退出登录
		// mImEventLogic.logout("android", ImLoginManager.getInstance().getUserId(), ImLoginManager.getInstance().getChannelId());
		
		// 此时，先关闭service，之后，弹出提示框，提示用户是重新登录还是取消; 如果重新登录，则打开Service; 否则，跳转到登录界面LoginActivity
		// Intent stopImServiceIntent = new Intent(context, ImService.class);
		// context.stopService(stopImServiceIntent);
		// 先清除Cookie
		GeneralDbManager.getInstance().removeCookieInfo();
		// 关闭所有的数据库
		MultiUserDatabaseHelper.getInstance().closeDatabase();
		
		SharedPreferences sp = context.getSharedPreferences("tuding_uid", Context.MODE_PRIVATE);  
        sp.edit().putLong("uid", Long.valueOf(0)).commit();
        sp.edit().putBoolean(GlobalConstants.INTENTION_COMPLETE, false).commit();
		
		ImLoginManager.getInstance().setEverLogined(false);
		//  停止ImService服务
		Intent stopImServiceIntent = new Intent(context, ImService.class);
		context.stopService(stopImServiceIntent);
		
		AppActivityManager.getInstance().finishAllActivity();
		
		Intent intent = new Intent(context, SplashActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
		
	}
	
	// 发送是否已读
	public void sendReadStatus(String message) {
		JSONObject jsonObject = (JSONObject) JSONObject.parse(message);
		long mid = jsonObject.getLong(GlobalConstants.JSON_MID);
		long did = jsonObject.getLong(GlobalConstants.JSON_DID);
		
		// 判断当前的Activity是否是ChatPersonActivity--下面是两种方式，可能会存在问题，待测
		ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        RunningTaskInfo info = manager.getRunningTasks(1).get(0);
        // String shortClassName = info.topActivity.getShortClassName();    //类名
        String className = info.topActivity.getClassName();              //完整类名
        // String packageName = info.topActivity.getPackageName();          //包名
		
		// 然后，判断ChatPersonActivity是否是foreground/visible activity
		if (className.equals("com.xiaobukuaipao.vzhi.ChatPersonActivity") && StringUtils.isNotEmpty(ChatPersonActivity.did) && (did == Long.valueOf(ChatPersonActivity.did))) {
			// 如果当前的Activity是ChatPersonActivity，此时通过socket告知服务器这条消息被读
			StringBuilder data = new StringBuilder();
			data.append("{\"type\":");
			data.append(IMConstants.TYPE_MSG_READ);
			data.append(",");
			data.append("\"mid\":");
			data.append(mid);
			data.append(",");
			data.append("\"did\":");
			data.append(did);
			data.append("}");
			data.append(SOCKET_MSG_END);
			
			loginServerThread.sendPacket(data.toString());
		}
	}
	
	
	@Override
	public void onAction(String action, Intent intent, BroadcastReceiver broadcastReceiver) {
		if (action.equals(ImActions.ACTION_RECONNECT_TUDING)) {
			Log.i(TAG, "action reconnection");
			if (!everLogined) {
				// 如果没有登录，执行此Action
				Log.i(TAG, "此时没有登录");
				handleReconnectServer();
			}
		}
	}
	
	private void handleReconnectServer() {
		PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
		PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "tuding_reconnecting_wakelock");
		wl.acquire();
		
		try {
			// 重新登录
			Log.i(TAG, "此时，断线重连机制");
			relogin();
		} finally {
			// 释放
			wl.release();
		}
	}
	
	private void handleNetworkActivityChangedAction() {
		if (NetworkUtils.isNetWorkAvalible(context)) {
			Log.i(TAG, "reconnect network is available");
			reconnect();
		} else {
			Log.i(TAG, "reconnect network is unavailable");
		}
	}
	
	private void reconnect() {
		Log.i(TAG, "reconnect reconnect the server");
		if (!everLogined) {
			return;
		} else {
			// so reconnect will use the initial reconnecting time
			reconnect_index = 0;
			
			mImEventLogic.cancel(R.id.socket_ip_and_port);
			
			if (relogin()) {
				reconnecting = true;
			}
		}
		
		/*if (IMLoginManager.instance().relogin()) {
			reconnecting = true;
		}*/
	}

}
