package com.xiaobukuaipao.vzhi.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.xiaobukuaipao.vzhi.R;
import com.xiaobukuaipao.vzhi.event.InfoResult;
import com.xiaobukuaipao.vzhi.event.SocialEventLogic;
import com.xiaobukuaipao.vzhi.im.ImLoginManager;
import com.xiaobukuaipao.vzhi.util.GlobalConstants;
import com.xiaobukuaipao.vzhi.util.StringUtils;

/**
 * 注意几个比较重要的方法, 和几个比较重要的事件处理
 * 1.网络断了---如果用户进入应用后，ImService打开，此时断开Wifi, 3G, 4G, 用户接收不到消息，原因是此时Socket断开连接; 
 *  此后，用户重连了Wifi, 3G, 4G, 此时要发广播，让Socket重新连接
 *  2.Socket断了--如果服务器端存在问题，造成连接Socket失败，此时要进行重新连接策略
 */

public class ImService extends Service {
	
	public final static String TAG = ImService.class.getSimpleName();
	
	// This is the object that receives interactions from clients.  See
    // RemoteService for a more complete example.
    private final IBinder mBinder = new ImServiceBinder();
    
    private SocialEventLogic mSocialEventLogic;
    
    private ConnectionChangeReceiver connectChangeReciver = null;
    private ReconnectionReceiver reconnectReceiver = null;
    
	/**
     * Class for clients to access.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with
     * IPC.
     */
	public class ImServiceBinder extends Binder {
		public ImService getService() {
			return ImService.this;
		}
	}
	
	/**
	 * Service必须实现的方法
	 */
	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	/**
	 * onCreate只在开启Service的时候，执行一次
	 */
	@Override
	public void onCreate() {
		super.onCreate();
		 IntentFilter filter = new IntentFilter();
        // filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        // filter.addAction(ImActions.ACTION_RECONNECT);
        connectChangeReciver = new ConnectionChangeReceiver();
        
        // 注册BroadcastReceiver
        registerReceiver(connectChangeReciver, filter);
		
		// 临时性的写法
		// make the service foreground, so stop "360 yi jian qingli"(a clean tool) to stop our app
		// startForeground((int)System.currentTimeMillis(),new Notification());
		
		Log.i(TAG, "onCreate");
		mSocialEventLogic = new SocialEventLogic();
		mSocialEventLogic.register(this);
		// 此时登录成功，进入主页面，
		Log.i(TAG, "on create service");
		// mSocialEventLogic.getSocketIpAndPort();
		ImLoginManager.getInstance().firstLogin();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// 断开Socket连接
		ImLoginManager.getInstance().disconnectServer();
		
		mSocialEventLogic.unregister(this);
		// 取消注册BroadcastReceiver
		unregisterReceiver(connectChangeReciver);
		ImLoginManager.getInstance().unRegister();
		
		Log.i(TAG, "ImService destroy");
	}
	
	/**
	 * EventBus的回调方法
	 * @param msg
	 */
	public void onEventMainThread(Message msg) {
		if (msg.obj instanceof InfoResult) {
			InfoResult infoResult = (InfoResult) msg.obj;
			switch (msg.what) {
				case R.id.socket_ip_and_port:
					JSONObject mJSONResult = (JSONObject) JSONObject.parse(infoResult.getResult());
					String server = mJSONResult.getString(GlobalConstants.JSON_SERVER);
					// 此时，登录成功，并且成功获得了IM服务器的IP和Port
					if (!StringUtils.isEmpty(server)) {
						// 启动聊天服务
						ImLoginManager.getInstance().connectServer(server);
					} else {
						mSocialEventLogic.getSocketIpAndPort();
					}
					break;
			}
		}
	}
	
	// START_STICKY -- 粘性的
	// 如果service进程被kill掉，保留service的状态为开始状态，但不保留递送的intent对象
	// 随后系统会尝试重新创建service，由于服务状态为开始状态，所以创建服务后一定会调用onStartCommand(Intent,int,int)方法
	// 如果在此期间没有任何启动命令被传递到service，那么参数Intent将为null
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {	
		// 多次调用startService()方法并不会导致多次创建服务, 只会执行多次的onStartCommand
		Context ctx = getApplicationContext();
		// ImNotificationManager.getInstance().setContext(ctx);
		// ImLoginManager.getInstance().setContext(ctx);
		// ImLoginManager.getInstance().register();
		// 非粘性的 -- 使用这个返回值时，如果在执行完onStartCommand后，服务被异常kill掉，系统不会自动重启该服务
		ImLoginManager.getInstance().setContext(ctx);
		
		ImLoginManager.getInstance().register();
		return START_NOT_STICKY;
	}
	
	/*public final static String EXTRA_KEY = "json";
	public final static String KEY_STATUS = "status";
	public final static String KEY_MSG_BODY = "body";
	public final static String KEY_MSG_TYPE = "type";
	
	public final static String SOCKET_MSG_END = "\r\n";
	
	public final static String ACTION_SOCK_MSG = "com.xiaobukuaipao.vzhi.action.BROADCAST_RECEIVER_SOCK_MSG";
	public final static String ACTION_SOCK_FAIL = "com.xiaobukuaipao.vzhi.action.BROADCAST_RECEIVER_SOCK_FAIL";
	public final static String BROADCAST_RECV_PERMISSION = "com.xiaobukuaipao.vzhi.imservice.permission.RECV_BROADCAST";
	
	public final static int IM_MESSAGE_P2P = 1001;
	public final static int IM_MESSAGE_TYPING = 1002;
	public final static int IM_MESSAGE_RESUME = 1003;
	public final static int IM_MESSAGE_RESUME_LIKE = 1005;
	public final static int IM_MESSAGE_RESUME_IGNORE = 1006;
	public final static int IM_MESSAGE_JOB_INVITATION = 1007;
	public final static int IM_MESSAGE_GROUP = 2001;
	public final static int IM_MESSAGE_GROUP_JOIN = 2004;
	public final static int IM_MESSAGE_GROUP_RESUME_LIKE = 2005;
	public final static int IM_MESSAGE_GROUP_JOB_INVITATION = 2007;
	public final static int IM_MESSAGE_LOGIN = 9990;
	public final static int IM_MESSAGE_READ_ERROR_DATA = 9991;
	public final static int IM_MESSAGE_EMAIL_VERIFY = 9992;
	
	private String serverHost = "192.168.1.107";
	private int serverPort = 8001;
	
	private final int SOCKET_RECONNECT_WAIT = 1000;
	private volatile Socket socket;
	
	private final IBinder binder = new ImServiceBinder();
	
	private ImServiceThread imServiceThread;
	
	private NetworkReceiver receiver;
	
	public ImService() {
	}
	
	
	public class ImServiceBinder extends Binder {
		public ImService getService() {
			return ImService.this;
		}
	}

	@Override
	public void onCreate() {
		Log.i(TAG, "Service create.");
		super.onCreate();
		imServiceThread = new ImServiceThread();
		imServiceThread.start();

		IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
		receiver = new NetworkReceiver();
		registerReceiver(receiver, filter);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}
	
	@Override
	public void onDestroy() {
		Log.i(TAG, "Service destroy.");
		if (imServiceThread != null) {
			imServiceThread.setRunning(false);
		}
		doSocketShutdownRead();
		doSocketClose();
		if (receiver != null) {
			unregisterReceiver(receiver);
		}
		super.onDestroy();
	}

	private boolean isNetworkConnected() {
		ConnectivityManager connMgr = 
				(ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			return true;
		} else {
			return false;
		}
	}
	
	private void broadcastSocketFailed(String msg) {
		Log.i(TAG, "broadcastSocketFailed");
		Intent intent = new Intent(ACTION_SOCK_MSG);
		intent.putExtra(EXTRA_KEY, msg);
		sendBroadcast(intent, BROADCAST_RECV_PERMISSION);
	}
	
	private void broadcastSockMsg(String msg) {
		Log.i(TAG, "broadcastSockMsg");
		Intent intent = new Intent(ACTION_SOCK_MSG);
		intent.putExtra(EXTRA_KEY, msg);
		sendBroadcast(intent, BROADCAST_RECV_PERMISSION);
	}
	
	private int doSocketConnect() {
		if (socket != null && socket.isConnected()) {
			return 0;
		}

		for (int i = 0; i < 4; ++i) {
			try {
				if (socket == null) {
					socket = new Socket();
				}
				socket.connect(new InetSocketAddress(serverHost, serverPort), 5000);
				//socket.setSoTimeout(30*60*1000);
			} catch (IOException e) {
				Log.e(TAG, "socket connect failed. exception:" + e);
			}
			if (socket == null) {
				Log.e(TAG, "socket set to null in other thread when connecting.");
				return -3;
			}
			if (!socket.isConnected()) {
				if (isNetworkConnected() && imServiceThread.isRunning()) {
					Log.i(TAG, "network connected but socket connect fail, maybe the server down. retry");
					try {
						Thread.sleep(SOCKET_RECONNECT_WAIT);
					} catch (Exception e) {
					}
					doSocketClose();
					//发http post请求拿最新的serverHost，serverPort
					//。。。
					continue;
				} else {
					Log.e(TAG, "network disconnected.");
					return -1;
				}
			} else {
				Log.i(TAG, "socket connect successfully. server endpoint:" + serverHost + ":" + serverPort);
				login();
				return 0;
			}
		}
		
		Log.e(TAG, "network connected but socket connect fail, maybe the server down. give up");
		return -2;
	}
	
	private boolean doSocketShutdownRead() {
		if (socket == null || socket.isClosed() || !socket.isConnected()) {
			return true;
		}

		try {
			socket.shutdownInput();
			Log.i(TAG, "socket shutodownInput successfully.");
		} catch (IOException e) {
			Log.e(TAG, "socket shutodownInput exception:" + e);
			return false;
		}
		return true;
	}
	
	private boolean doSocketClose() {
		if (socket == null) {
			return true;
		}
		
		if (socket.isClosed()) {
			socket = null;
			return true;
		}

		try {
			socket.close();
			socket = null;
			Log.i(TAG, "socket close successfully.");
		} catch (IOException e) {
			Log.e(TAG, "socket close exception:" + e);
			return false;
		}
		return true;
	}
	
	private boolean doSocketRead() {
		if (socket == null || !socket.isConnected()) {
			return false;
		}
		Log.i(TAG, "begin doSocketRead");
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			String line = in.readLine();
			
			if (line != null) {
				Log.i(TAG, "get socket data:" + line);
				broadcastSockMsg(line);
			} else {
				Log.i(TAG, "get socket data null");
				doSocketClose();
			}
		} catch (SocketTimeoutException e) {
			Log.i(TAG, "socket exception:" + e);
		} catch (IOException e) {
			Log.e(TAG, "socket exception:" + e);
			doSocketClose();
			return false;
		}
		return true;
	}
	
	private boolean doSocketWrite(String data) {
		if (socket == null || !socket.isConnected()) {
			return false;
		}
		Log.i(TAG, "begin doSocketWrite");

		OutputStream outStream = null;
		try {
			outStream = socket.getOutputStream();
			PrintWriter out = new PrintWriter(outStream, true);
			out.print(data + SOCKET_MSG_END);
			out.flush();
		} catch (IOException e) {
			Log.e(TAG, "socket exception:" + e);
			return false;
		}
		return true;
	}
	
	private boolean login() {
		if (socket == null || !socket.isConnected()) {
			return false;
		}
		
		Log.i(TAG, "begin login");
		
		StringBuilder data = new StringBuilder();
		data.append("{\"uid\":27,\"t\":\"tt\",\"v\":\"1.0.0\",\"os\":\"Android ");
		data.append(android.os.Build.VERSION.SDK_INT).append(" ")
			.append(android.os.Build.VERSION.RELEASE).append(" ")
			.append(android.os.Build.MANUFACTURER).append(" ")
			.append(android.os.Build.PRODUCT).append("\"");
		data.append("}");
		return doSocketWrite(data.toString());
	}
	
	public boolean writeToSocket(String data) {
		return doSocketWrite(data);
	}

	
	public class ImServiceThread extends Thread {
		private volatile boolean isRunning = false;

		@Override
		public void run() {
			isRunning = true;
			for (; isRunning;) {
				int connectStatus = doSocketConnect();
				if (connectStatus == -1) {
					broadcastSocketFailed(
							"{\"" + KEY_STATUS + "\":-11,\"" + KEY_MSG_BODY + "\":\"网络连接失败，请检查网络设置\"}");
					break;
				} else if (connectStatus == -2) {
					broadcastSocketFailed(
							"{\"" + KEY_STATUS + "\":-12,\"" + KEY_MSG_BODY+ "\":\"连接服务器失败，请稍等\"}");
				} else if (connectStatus == 0) {
					doSocketRead();
				} else {
					break;
				}
			}
			isRunning = false;
		}
		
		public boolean isRunning() {
			return isRunning;
		}
		
		public void setRunning(boolean isRunning) {
			this.isRunning = isRunning;
		}
	}
	
	public class NetworkReceiver extends BroadcastReceiver {
	      
		@Override
		public void onReceive(Context context, Intent intent) {
			if (isNetworkConnected()) {
				if (imServiceThread == null || !imServiceThread.isAlive()) {
					imServiceThread = new ImServiceThread();
					imServiceThread.start();
				} else if (!imServiceThread.isRunning()) {
					imServiceThread.setRunning(true);
				}
			}
		}
	}*/
	
}
