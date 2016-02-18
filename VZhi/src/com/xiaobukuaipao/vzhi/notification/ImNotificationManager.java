package com.xiaobukuaipao.vzhi.notification;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.xiaobukuaipao.vzhi.ChatGroupActivity;
import com.xiaobukuaipao.vzhi.ChatPersonActivity;
import com.xiaobukuaipao.vzhi.MainRecruitActivity;
import com.xiaobukuaipao.vzhi.R;
import com.xiaobukuaipao.vzhi.event.InfoResult;
import com.xiaobukuaipao.vzhi.event.SocialEventLogic;
import com.xiaobukuaipao.vzhi.im.ImDbManager;
import com.xiaobukuaipao.vzhi.manager.GeneralDbManager;
import com.xiaobukuaipao.vzhi.register.SplashActivity;
import com.xiaobukuaipao.vzhi.util.GlobalConstants;
import com.xiaobukuaipao.vzhi.util.StringUtils;

public class ImNotificationManager {

	public static final String TAG = ImNotificationManager.class.getSimpleName();
	
	private static final int SOCKET_NOTIFICATION = 0;
	private static final int PUSH_NOTIFICATION = 1;
	
	private int notificationType = SOCKET_NOTIFICATION;
	
	// Http操作
	private SocialEventLogic mSocialEventLogic;
	
	private JSONObject jsonObject;
	
	private NotificationManager mNotificationManager;

	private static final ImNotificationManager notificationManager = new ImNotificationManager();
	
	private ImNotificationManager() {
		
	}
	
	public static ImNotificationManager getInstance() {
		return notificationManager;
	}
	
	private Context context;
	public void setContext(Context context) {
		if (context == null) {
			throw new RuntimeException("context is null");
		}
		this.context = context;
		
		mSocialEventLogic = new SocialEventLogic();
		mSocialEventLogic.register(this);
		mNotificationManager =
			    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
	}
	
	public void onEventMainThread(Message msg) {
		if (msg.obj instanceof InfoResult) {
			InfoResult infoResult = (InfoResult) msg.obj;
			
			ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
	        RunningTaskInfo info = manager.getRunningTasks(1).get(0);
	        //String shortClassName = info.topActivity.getShortClassName();    //类名
	        String className = info.topActivity.getClassName();              //完整类名
	        
			switch (msg.what) {
					
				case R.id.social_get_person_avatar:
					// 将获得的头像和名字信息，插入到ContactUserTable表中，同时，重新加载CursorLoader
					Log.i(TAG, infoResult.getResult());
					if (notificationType == SOCKET_NOTIFICATION) {
						ImDbManager.getInstance().insertPersonInfoIntoContactUserTable(infoResult.getResult());
					}

					if (mNotificationManager == null) {
						return;
					}
			        
			        if (className.equals("com.xiaobukuaipao.vzhi.ChatPersonActivity") && 
			        		(jsonObject.getLongValue(GlobalConstants.JSON_DID) == Long.valueOf(ChatPersonActivity.did))) {
			        	// 如果是现在正在聊天的界面，则不弹Notification
			        } else {
						NotificationCompat.Builder mBuilder =
						        new NotificationCompat.Builder(this.context)
						        .setSmallIcon(R.drawable.ic_launcher)
						        .setContentTitle(StringUtils.isEmpty(((JSONObject) JSONObject.parse(infoResult.getResult())).getString(GlobalConstants.JSON_NAME)) ? "途盯招聘" : 
						        	((JSONObject) JSONObject.parse(infoResult.getResult())).getString(GlobalConstants.JSON_NAME))
						        .setContentText(jsonObject.getString(GlobalConstants.JSON_SUMMARY))
						        .setAutoCancel(true);
						
						/*if (SharedPreferencesUtil.getInstance().getSettingMessage(GlobalConstants.PREFERENCE_MSG_SOUND + "_" + uid)) {
							mBuilder.setDefaults(Notification.DEFAULT_SOUND);
						} else if (SharedPreferencesUtil.getInstance().getSettingMessage(GlobalConstants.PREFERENCE_MSG_VIBRATION + "_" + uid)) {
							//delay 0ms, vibrate 200ms, delay 250ms, vibrate 200ms 
							long[] vibrate = {0, 200, 250, 200};
							mBuilder.setVibrate(vibrate);
						}*/
						
						if (jsonObject.containsKey("hasblock")  && jsonObject.getInteger("hasblock") == 1) {
							// 不谈Notification, 也不响铃
							
						} else {
							if (jsonObject.containsKey("notring") && jsonObject.getInteger("notring") == 1) {
								// 弹Notification, 但不响铃
								long[] vibrate = {0, 200, 250, 200};
								mBuilder.setVibrate(vibrate);
							} else {
								// 弹Notification, 响铃
								mBuilder.setDefaults(Notification.DEFAULT_SOUND);
								long[] vibrate = {0, 200, 250, 200};
								mBuilder.setVibrate(vibrate);
							}
							
							Intent resultIntent = null;
							if (notificationType == SOCKET_NOTIFICATION) {
								resultIntent = new Intent(this.context, MainRecruitActivity.class);
							} else {
								resultIntent = new Intent(this.context, SplashActivity.class);
								resultIntent.putExtra("uid", jsonObject.getString(GlobalConstants.JSON_RECEIVER));
							}
							
							resultIntent.putExtra(GlobalConstants.PAGE, 1);
							resultIntent.putExtra(GlobalConstants.TYPE, Integer.parseInt(jsonObject.getString(GlobalConstants.JSON_TYPE)));
							resultIntent.putExtra("did", jsonObject.getLongValue(GlobalConstants.JSON_DID));
							resultIntent.putExtra("dname", ((JSONObject) JSONObject.parse(infoResult.getResult())).getString(GlobalConstants.JSON_NAME));
							resultIntent.putExtra("sender", jsonObject.getLongValue(GlobalConstants.JSON_SENDER));
							
							
							TaskStackBuilder stackBuilder = TaskStackBuilder.create(this.context);
							stackBuilder.addParentStack(MainRecruitActivity.class);
							stackBuilder.addNextIntent(resultIntent);
							PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
							
							mBuilder.setContentIntent(resultPendingIntent);
							mNotificationManager.notify(Integer.parseInt(jsonObject.getString(GlobalConstants.JSON_DID)), mBuilder.build());
						}
						
			        }
			        
					break;
					
				case R.id.social_get_group_person_avatar:
					if (notificationType == SOCKET_NOTIFICATION) {
						ImDbManager.getInstance().insertPersonInfoIntoContactUserTable(infoResult.getResult());
					}
					
					if (mNotificationManager == null) {
						return;
					}
					
					if (className.equals("com.xiaobukuaipao.vzhi.ChatGroupActivity") && 
			        		(jsonObject.getLongValue(GlobalConstants.JSON_GID) == Long.valueOf(ChatGroupActivity.groupId))) {
			        	// 如果是现在正在聊天的界面，则不弹Notification
			        } else {
						NotificationCompat.Builder mBuilder =
						        new NotificationCompat.Builder(this.context)
						        .setSmallIcon(R.drawable.ic_launcher)
						        .setContentTitle(StringUtils.isEmpty(((JSONObject) JSONObject.parse(infoResult.getResult())).getString(GlobalConstants.JSON_NAME)) ? "途盯招聘" : 
						        	((JSONObject) JSONObject.parse(infoResult.getResult())).getString(GlobalConstants.JSON_NAME))
						        .setContentText(jsonObject.getString(GlobalConstants.JSON_SUMMARY))
						        .setAutoCancel(true);
						
						/*if (SharedPreferencesUtil.getInstance().getSettingMessage(GlobalConstants.PREFERENCE_MSG_SOUND + "_" + uid)) {
							mBuilder.setDefaults(Notification.DEFAULT_SOUND);
						} else if (SharedPreferencesUtil.getInstance().getSettingMessage(GlobalConstants.PREFERENCE_MSG_VIBRATION + "_" + uid)) {
							//delay 0ms, vibrate 200ms, delay 250ms, vibrate 200ms 
							long[] vibrate = {0, 200, 250, 200};
							mBuilder.setVibrate(vibrate);
						}*/
						
						if (jsonObject.containsKey("hasblock")  && jsonObject.getInteger("hasblock") == 1) {
							// 不弹Notification, 也不响铃
							
						} else {
							if (jsonObject.containsKey("notring") && jsonObject.getInteger("notring") == 1) {
								// 弹Notification, 但不响铃
								long[] vibrate = {0, 200, 250, 200};
								mBuilder.setVibrate(vibrate);
							} else {
								// 弹Notification, 响铃
								mBuilder.setDefaults(Notification.DEFAULT_SOUND);
								long[] vibrate = {0, 200, 250, 200};
								mBuilder.setVibrate(vibrate);
							}
							
							Intent resultIntent = null;
							if (notificationType == SOCKET_NOTIFICATION) {
								resultIntent = new Intent(this.context, MainRecruitActivity.class);
							} else {
								resultIntent = new Intent(this.context, SplashActivity.class);
								resultIntent.putExtra("uid", jsonObject.getString(GlobalConstants.JSON_RECEIVER));
							}
							resultIntent.putExtra(GlobalConstants.PAGE, 1);
							resultIntent.putExtra(GlobalConstants.TYPE, Integer.parseInt(jsonObject.getString(GlobalConstants.JSON_TYPE)));
							resultIntent.putExtra("gid", jsonObject.getLongValue(GlobalConstants.JSON_GID));
							
							TaskStackBuilder stackBuilder = TaskStackBuilder.create(this.context);
							stackBuilder.addParentStack(MainRecruitActivity.class);
							stackBuilder.addNextIntent(resultIntent);
							PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
							mBuilder.setContentIntent(resultPendingIntent);
							mNotificationManager.notify(Integer.parseInt(jsonObject.getString(GlobalConstants.JSON_GID)), mBuilder.build());
						}
						
			        }
					break;
			}
		} else {
			
		}
	}
	
	
	/**
	 * 发送文本消息的Notification--会显示对方的名字
	 */
	public void showP2PTextNotification(String message) {
		// 首先，聊天的did, sender, summary
		jsonObject = (JSONObject) JSONObject.parse(message);

		if (jsonObject == null) {
			return;
		}
		
		notificationType = SOCKET_NOTIFICATION;
		
		// 先根据sender, did来获得对方的name
		if (!jsonObject.getString(GlobalConstants.JSON_SENDER).equals(GeneralDbManager.getInstance().getUidFromCookie())) {
			mSocialEventLogic.getPersonAvatarAndName(jsonObject.getLongValue(GlobalConstants.JSON_SENDER), 
					jsonObject.getLongValue(GlobalConstants.JSON_DID));
		}
	}
	
	public void showP2PPushTextNotification(String message) {
		// 首先，聊天的did, sender, summary
		jsonObject = (JSONObject) JSONObject.parse(message);

		if (jsonObject == null) {
			return;
		}
		
		notificationType = PUSH_NOTIFICATION;
		
		// 先根据sender, did来获得对方的name
		if (!jsonObject.getString(GlobalConstants.JSON_SENDER).equals(GeneralDbManager.getInstance().getUidFromCookie())) {
			mSocialEventLogic.getPersonAvatarAndName(jsonObject.getLongValue(GlobalConstants.JSON_SENDER), 
					jsonObject.getLongValue(GlobalConstants.JSON_DID));
		}
	}
	
	/**
	 * 群组的Notification
	 */
	public void showGroupTextNotification(String message) {
		// 首先，聊天的did, sender, summary
		jsonObject = (JSONObject) JSONObject.parse(message);

		if (jsonObject == null) {
			return;
		}
		
		notificationType = SOCKET_NOTIFICATION;
		
		Log.i(TAG, " group text : " + message);
		
		// 先根据sender, did来获得对方的name
		if (!jsonObject.getString(GlobalConstants.JSON_SENDER).equals(GeneralDbManager.getInstance().getUidFromCookie())) {
			mSocialEventLogic.getGroupPersonAvatarAndName(jsonObject.getLongValue(GlobalConstants.JSON_SENDER), 
					jsonObject.getLongValue(GlobalConstants.GID));
		}
	}
	
	public void showGroupPushTextNotification(String message) {
		// 首先，聊天的did, sender, summary
		jsonObject = (JSONObject) JSONObject.parse(message);

		if (jsonObject == null) {
			return;
		}
		
		notificationType = PUSH_NOTIFICATION;
		
		Log.i(TAG, " group text : " + message);
		
		// 先根据sender, did来获得对方的name
		if (!jsonObject.getString(GlobalConstants.JSON_SENDER).equals(GeneralDbManager.getInstance().getUidFromCookie())) {
			mSocialEventLogic.getGroupPersonAvatarAndName(jsonObject.getLongValue(GlobalConstants.JSON_SENDER), 
					jsonObject.getLongValue(GlobalConstants.GID));
		}
	}
	
	
	/**
	 * 显示聊天的推送Notification
	 * @param message
	 */
	public void showCommonTextNotification(String message) {
		// 首先，聊天的did, sender, summary
		JSONObject jsonObject = (JSONObject) JSONObject.parse(message);
		
		/*NotificationManager mNotificationManager =
			    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);*/
		
		if (mNotificationManager == null) {
			return;
		}
		
		if (jsonObject == null) {
			return;
		}
		
		// 先根据sender来获得对方的name
		NotificationCompat.Builder mBuilder =
		        new NotificationCompat.Builder(this.context)
		        .setSmallIcon(R.drawable.ic_launcher)
		        .setContentTitle("途盯招聘")
		        .setContentText(jsonObject.getString(GlobalConstants.JSON_SUMMARY))
		        .setAutoCancel(true);
		
		/*if (SharedPreferencesUtil.getInstance().getSettingMessage(GlobalConstants.PREFERENCE_MSG_SOUND + "_" + uid)) {
			mBuilder.setDefaults(Notification.DEFAULT_SOUND);
		} else if (SharedPreferencesUtil.getInstance().getSettingMessage(GlobalConstants.PREFERENCE_MSG_VIBRATION + "_" + uid)) {
			//delay 0ms, vibrate 200ms, delay 250ms, vibrate 200ms 
			long[] vibrate = {0, 200, 250, 200};
			mBuilder.setVibrate(vibrate);
		}*/
		
		if (jsonObject.containsKey("hasblock")  && jsonObject.getInteger("hasblock") == 1) {
			// 不谈Notification, 也不响铃
			
		} else {
			if (jsonObject.containsKey("notring") && jsonObject.getInteger("notring") == 1) {
				// 弹Notification, 但不响铃
				long[] vibrate = {0, 200, 250, 200};
				mBuilder.setVibrate(vibrate);
			} else {
				// 弹Notification, 响铃
				mBuilder.setDefaults(Notification.DEFAULT_SOUND);
				long[] vibrate = {0, 200, 250, 200};
				mBuilder.setVibrate(vibrate);
			}
			
			// Creates an explicit intent for an Activity in your app
			// Intent resultIntent = new Intent(this.context, MainRecruitActivity.class);
			
			Intent resultIntent = new Intent(this.context, MainRecruitActivity.class);
			// 1--代表chat页
			resultIntent.putExtra(GlobalConstants.PAGE, 1);
			// 消息类型
			resultIntent.putExtra(GlobalConstants.TYPE, Integer.parseInt(jsonObject.getString(GlobalConstants.JSON_TYPE)));
			
			// The stack builder object will contain an artificial back stack for the
			// started Activity.
			// This ensures that navigating backward from the Activity leads out of
			// your application to the Home screen.
			TaskStackBuilder stackBuilder = TaskStackBuilder.create(this.context);
			// Adds the back stack for the Intent (but not the Intent itself)
			stackBuilder.addParentStack(MainRecruitActivity.class);
			// Adds the Intent that starts the Activity to the top of the stack
			stackBuilder.addNextIntent(resultIntent);
			PendingIntent resultPendingIntent =
			        stackBuilder.getPendingIntent(
			            0,
			            PendingIntent.FLAG_UPDATE_CURRENT
			        );
			mBuilder.setContentIntent(resultPendingIntent);
			
			// mId allows you to update the notification later on.
			mNotificationManager.notify(Integer.parseInt(jsonObject.getString(GlobalConstants.JSON_TYPE)), mBuilder.build());
			/*PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			mBuilder.setContentIntent(pendingIntent);
			Notification notification = mBuilder.build();
			mNotificationManager.notify(Integer.parseInt(jsonObject.getString(GlobalConstants.JSON_DID)), notification);*/
		}
		
	}
	
	
	/**
	 * Push
	 * @param message
	 */
	public void showCommonPushTextNotification(String message) {
		// 首先，聊天的did, sender, summary
		JSONObject jsonObject = (JSONObject) JSONObject.parse(message);
		
		/*NotificationManager mNotificationManager =
			    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);*/
		
		if (mNotificationManager == null) {
			return;
		}
		
		if (jsonObject == null) {
			return;
		}
		
		// 先根据sender来获得对方的name
		NotificationCompat.Builder mBuilder =
		        new NotificationCompat.Builder(this.context)
		        .setSmallIcon(R.drawable.ic_launcher)
		        .setContentTitle("途盯招聘")
		        .setContentText(jsonObject.getString(GlobalConstants.JSON_SUMMARY))
		        .setAutoCancel(true);
		
		/*if (SharedPreferencesUtil.getInstance().getSettingMessage(GlobalConstants.PREFERENCE_MSG_SOUND + "_" + uid)) {
			mBuilder.setDefaults(Notification.DEFAULT_SOUND);
		} else if (SharedPreferencesUtil.getInstance().getSettingMessage(GlobalConstants.PREFERENCE_MSG_VIBRATION + "_" + uid)) {
			//delay 0ms, vibrate 200ms, delay 250ms, vibrate 200ms 
			long[] vibrate = {0, 200, 250, 200};
			mBuilder.setVibrate(vibrate);
		}*/
		
		if (jsonObject.containsKey("hasblock")  && jsonObject.getInteger("hasblock") == 1) {
			// 不谈Notification, 也不响铃
			
		} else {
			if (jsonObject.containsKey("notring") && jsonObject.getInteger("notring") == 1) {
				// 弹Notification, 但不响铃
				long[] vibrate = {0, 200, 250, 200};
				mBuilder.setVibrate(vibrate);
			} else {
				// 弹Notification, 响铃
				mBuilder.setDefaults(Notification.DEFAULT_SOUND);
				long[] vibrate = {0, 200, 250, 200};
				mBuilder.setVibrate(vibrate);
			}
			
			// Creates an explicit intent for an Activity in your app
			// Intent resultIntent = new Intent(this.context, MainRecruitActivity.class);
			
			Intent resultIntent = new Intent(this.context, SplashActivity.class);
			// 1--代表chat页
			resultIntent.putExtra(GlobalConstants.PAGE, 1);
			// 消息类型
			resultIntent.putExtra(GlobalConstants.TYPE, Integer.parseInt(jsonObject.getString(GlobalConstants.JSON_TYPE)));
			
			// The stack builder object will contain an artificial back stack for the
			// started Activity.
			// This ensures that navigating backward from the Activity leads out of
			// your application to the Home screen.
			TaskStackBuilder stackBuilder = TaskStackBuilder.create(this.context);
			// Adds the back stack for the Intent (but not the Intent itself)
			stackBuilder.addParentStack(MainRecruitActivity.class);
			// Adds the Intent that starts the Activity to the top of the stack
			stackBuilder.addNextIntent(resultIntent);
			PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
			            0,
			            PendingIntent.FLAG_UPDATE_CURRENT
			        );
			mBuilder.setContentIntent(resultPendingIntent);
			
			// mId allows you to update the notification later on.
			mNotificationManager.notify(Integer.parseInt(jsonObject.getString(GlobalConstants.JSON_TYPE)), mBuilder.build());
			/*PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			mBuilder.setContentIntent(pendingIntent);
			Notification notification = mBuilder.build();
			mNotificationManager.notify(Integer.parseInt(jsonObject.getString(GlobalConstants.JSON_DID)), notification);*/
		}
		
	}
	
}
