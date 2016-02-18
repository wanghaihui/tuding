package com.xiaobukuaipao.vzhi.im;

import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.xiaobukuaipao.vzhi.notification.ImNotificationManager;
import com.xiaobukuaipao.vzhi.util.GlobalConstants;

/**
 * 消息分发器
 */
public class ImMessageDispatcher {
	
	public static final String TAG = ImMessageDispatcher.class.getSimpleName();
	
	/**
	 * message--都是JSON String
	 * @param message
	 */
	public static void dispatch(String message) {
		if (message == null) {
			return;
		}
		// Socket类型
		Integer type;
		
		JSONObject jsonObject = (JSONObject) JSONObject.parse(message);
		type = jsonObject.getInteger(GlobalConstants.JSON_TYPE);
		
		if (type != null) {
			switch (type) {
				case IMConstants.TYPE_LOGIN_OK:
					// 此时，与socket验票成功，继续向服务端发送消息，以便服务器向客户端推送消息
					ImLoginManager.getInstance().sendClientReady();
					break;
				case IMConstants.TYPE_LOGIN_FAIL:
					// 失败情况要重点处理
					// 此时验票失败--服务器会在1分钟后断开连接，可在这1分钟内可以通过http登录接口拿新的t票，再次尝试socket登录
					ImLoginManager.getInstance().loginFailed();
					break;
				case IMConstants.TYPE_ANOTHER_LOGIN:
					// 多端登录进行处理
					// Toast--您的账号已在其他地方登陆
					ImLoginManager.getInstance().conflictAnotherLogin();
					break;
					
				case IMConstants.TYPE_P2P_TEXT:
					// 1.将此消息加入到MessageTable
					ImDbManager.getInstance().saveTextMessageToDatabase(message);
					// 2.将此消息加入到MessageSummaryTable
					ImDbManager.getInstance().saveTextMessageSummaryToDatabase(message);
					// 此时，处理额外的操作，比如设置是否已读等
					ImLoginManager.getInstance().sendReadStatus(message);
					
					// 接收到一条消息，此时首先发送Notification
					ImNotificationManager.getInstance().showP2PTextNotification(message);
					break;
				case IMConstants.TYPE_P2P_REAL_CARD:
					ImDbManager.getInstance().saveTextMessageToDatabase(message);
					break;
				case IMConstants.TYPE_P2P_NICK_CARD:
					ImDbManager.getInstance().saveTextMessageToDatabase(message);
					break;
				case IMConstants.TYPE_P2P_JOB_INVITATION:
					ImDbManager.getInstance().saveTextMessageToDatabase(message);
					break;
				case IMConstants.TYPE_P2P_JOB_INVITATION_ACCEPT:
					ImDbManager.getInstance().saveTextMessageToDatabase(message);
					break;
				case IMConstants.TYPE_P2P_JOB_INVITATION_IGNORE:
					ImDbManager.getInstance().saveTextMessageToDatabase(message);
					break;
				case IMConstants.TYPE_P2P_INTERVIEW_INVITATION:
					// 面试邀请
					ImDbManager.getInstance().saveTextMessageToDatabase(message);
					Log.i(TAG, "save text interview invitation");
					ImDbManager.getInstance().saveTextMessageSummaryToDatabase(message);
					ImNotificationManager.getInstance().showP2PTextNotification(message);
					break;
				case IMConstants.TYPE_P2P_JOB_SUMMARY:
					ImDbManager.getInstance().saveTextMessageToDatabase(message);
					break;
					
				case IMConstants.TYPE_MSG_READ:
					// 此时，代表发送的消息已读，所以要更新数据库信息
					ImDbManager.getInstance().updateMessageToDatabase(message);
					break;
					
				case IMConstants.TYPE_LETTER_GREETING:
					// 打招呼消息
					ImDbManager.getInstance().insertOrUpdateStrangerMessageListToDatabase(message);
					// 然后，发送Notification
					ImNotificationManager.getInstance().showCommonTextNotification(message);
					break;
				case IMConstants.TYPE_LETTER_QUESTION:
					// 提问消息
					ImDbManager.getInstance().insertOrUpdateStrangerMessageListToDatabase(message);
					ImNotificationManager.getInstance().showCommonTextNotification(message);
					break;
				case IMConstants.TYPE_LETTER_ANSWER:
					// 提问被回答的消息
					ImDbManager.getInstance().insertOrUpdateStrangerMessageListToDatabase(message);
					ImNotificationManager.getInstance().showCommonTextNotification(message);
					break;
				case IMConstants.TYPE_LETTER_BUDDY_INVITATION:
					// 好友请求消息
					// ImDbManager.getInstance().saveFriendMessageSummaryToDatabase(message);
					ImDbManager.getInstance().insertOrUpdateStrangerMessageListToDatabase(message);
					// 加好友
					ImNotificationManager.getInstance().showCommonTextNotification(message);
					break;
				case IMConstants.TYPE_LETTER_JOB_RECOMMEND:
					// 职位推荐
					ImDbManager.getInstance().insertOrUpdateJobRecommendMessageListToDatabase(message);
					ImNotificationManager.getInstance().showCommonTextNotification(message);
					break;
				case IMConstants.TYPE_LETTER_JOB_APPLY_NOTIFY:
					// 职位投递提醒消息
					ImDbManager.getInstance().saveJobApplyMessageSummaryToDatabase(message);
					// 弹出Notification
					ImNotificationManager.getInstance().showCommonTextNotification(message);
					break;
					
				case IMConstants.TYPE_LETTER_GROUP_RECOMMEND:
					// 群推荐消息
					ImDbManager.getInstance().insertOrUpdateGroupRecommendMessageListToDatabase(message);
					ImNotificationManager.getInstance().showCommonTextNotification(message);
					break;
					
				case IMConstants.TYPE_GROUP_TEXT:
					// 加入群的消息和群消息都是一种类型
					// 1.将此消息加入到MessageGroupTable
					ImDbManager.getInstance().saveGroupTextMessageToDatabase(message);
					// 2.将此消息加入到MessageSummaryTable
					ImDbManager.getInstance().saveGroupTextMessageSummaryToDatabase(message);
					ImNotificationManager.getInstance().showGroupTextNotification(message);
					break;
					
				case IMConstants.TYPE_GROUP_JOIN:
					// 加入群
					// 1.将此消息加入到MessageGroupTable
					ImDbManager.getInstance().saveGroupTextMessageToDatabase(message);
					ImDbManager.getInstance().saveGroupTextMessageSummaryToDatabase(message);
					ImNotificationManager.getInstance().showGroupTextNotification(message);
					break;
					
				case IMConstants.TYPE_GROUP_QUIT:
					ImDbManager.getInstance().saveGroupTextMessageToDatabase(message);
					ImDbManager.getInstance().saveGroupTextMessageSummaryToDatabase(message);
					// ImNotificationManager.getInstance().showGroupTextNotification(message);
					break;
					
				case IMConstants.TYPE_GROUP_INTERVIEW_INVITATION:
					ImDbManager.getInstance().saveGroupTextMessageToDatabase(message);
					Log.i(TAG, "group interview invitation");
					ImDbManager.getInstance().saveGroupTextMessageSummaryToDatabase(message);
					// ImNotificationManager.getInstance().showGroupTextNotification(message);
					break;
				case IMConstants.TYPE_GROUP_INTERVIEW_ACCEPT:
					ImDbManager.getInstance().saveGroupTextMessageToDatabase(message);
					ImDbManager.getInstance().saveGroupTextMessageSummaryToDatabase(message);
					// ImNotificationManager.getInstance().showGroupTextNotification(message);
					break;
				case IMConstants.TYPE_GROUP_INTERVIEW_IGNORE:
					ImDbManager.getInstance().saveGroupTextMessageToDatabase(message);
					ImDbManager.getInstance().saveGroupTextMessageSummaryToDatabase(message);
					// ImNotificationManager.getInstance().showGroupTextNotification(message);
					break;
				case IMConstants.TYPE_GROUP_JOB_APPLY_READ:
					ImDbManager.getInstance().saveGroupTextMessageToDatabase(message);
					ImDbManager.getInstance().saveGroupTextMessageSummaryToDatabase(message);
					// ImNotificationManager.getInstance().showGroupTextNotification(message);
					break;
				case IMConstants.TYPE_GROUP_JOB_APPLY_INTEREST:
					ImDbManager.getInstance().saveGroupTextMessageToDatabase(message);
					ImDbManager.getInstance().saveGroupTextMessageSummaryToDatabase(message);
					// ImNotificationManager.getInstance().showGroupTextNotification(message);
					break;
				case IMConstants.TYPE_GROUP_JOB_APPLY_CONTACT_READ:
					ImDbManager.getInstance().saveGroupTextMessageToDatabase(message);
					ImDbManager.getInstance().saveGroupTextMessageSummaryToDatabase(message);
					// ImNotificationManager.getInstance().showGroupTextNotification(message);
					break;
					
				case IMConstants.TYPE_P2P_BUDDY_INVITATION_ACCEPT:
					// ImDbManager.getInstance().saveFriendMessageSummaryToDatabase(message);
					// 接受好友请求, 之后弹出的是P2P聊天
					// ImDbManager.getInstance().insertOrUpdateStrangerMessageListToDatabase(message);
					// 1.将此消息加入到MessageTable
					ImDbManager.getInstance().saveTextMessageToDatabase(message);
					// 2.将此消息加入到MessageSummaryTable
					ImDbManager.getInstance().saveTextMessageSummaryToDatabase(message);
					// 接收到一条消息，此时首先发送Notification
					ImNotificationManager.getInstance().showP2PTextNotification(message);
					break;
					
				case IMConstants.TYPE_P2P_JOB_APPLY:
					ImDbManager.getInstance().saveTextMessageToDatabase(message);
					ImDbManager.getInstance().saveTextMessageSummaryToDatabase(message);
					ImNotificationManager.getInstance().showP2PTextNotification(message);
					break;
					
				case IMConstants.TYPE_P2P_JOB_APPLY_READ:
					// 简历被HR查看
					ImDbManager.getInstance().saveTextMessageToDatabase(message);
					ImDbManager.getInstance().saveTextMessageSummaryToDatabase(message);
					ImNotificationManager.getInstance().showP2PTextNotification(message);
					break;
					
				case IMConstants.TYPE_P2P_JOB_APPLY_INTEREST:
					// 简历被HR感兴趣
					ImDbManager.getInstance().saveTextMessageToDatabase(message);
					ImDbManager.getInstance().saveTextMessageSummaryToDatabase(message);
					ImNotificationManager.getInstance().showP2PTextNotification(message);
					break;
					
				case IMConstants.TYPE_P2P_INTERVIEW_ACCEPT:
					// 面试邀请被接受
					ImDbManager.getInstance().saveTextMessageToDatabase(message);
					ImDbManager.getInstance().saveTextMessageSummaryToDatabase(message);
					ImNotificationManager.getInstance().showP2PTextNotification(message);
					break;
				case IMConstants.TYPE_P2P_INTERVIEW_IGNORE:
					// 面试邀请被拒绝
					ImDbManager.getInstance().saveTextMessageToDatabase(message);
					ImDbManager.getInstance().saveTextMessageSummaryToDatabase(message);
					ImNotificationManager.getInstance().showP2PTextNotification(message);
					break;
					
				case IMConstants.TYPE_P2P_JOB_APPLY_CONTACT_READ:
					// HR在投递场景下进入求职者profile在联系方式上“点击查看”，投递者会接收到socket下发的消息
					ImDbManager.getInstance().saveTextMessageToDatabase(message);
					ImDbManager.getInstance().saveTextMessageSummaryToDatabase(message);
					ImNotificationManager.getInstance().showP2PTextNotification(message);
					break;
			}
		} else {
			// 当不存在type时，进行别的判断, 比如此时下发的是P2P文本信息
			Log.i(TAG, message);
		}
	}
	
}
