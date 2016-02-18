package com.xiaobukuaipao.vzhi.receiver;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.baidu.frontia.api.FrontiaPushMessageReceiver;
import com.xiaobukuaipao.vzhi.MainRecruitActivity;
import com.xiaobukuaipao.vzhi.im.IMConstants;
import com.xiaobukuaipao.vzhi.im.ImLoginManager;
import com.xiaobukuaipao.vzhi.notification.ImNotificationManager;
import com.xiaobukuaipao.vzhi.util.GlobalConstants;
import com.xiaobukuaipao.vzhi.util.Utils;

/**
 * Push消息处理receiver。请编写您需要的回调函数， 一般来说： onBind是必须的，用来处理startWork返回值；
 * onMessage用来接收透传消息； onSetTags、onDelTags、onListTags是tag相关操作的回调；
 * onNotificationClicked在通知被点击时回调； onUnbind是stopWork接口的返回值回调
 * 
 * 返回值中的errorCode，解释如下： 
 *  0 - Success
 *  10001 - Network Problem
 *  30600 - Internal Server Error
 *  30601 - Method Not Allowed 
 *  30602 - Request Params Not Valid
 *  30603 - Authentication Failed 
 *  30604 - Quota Use Up Payment Required 
 *  30605 - Data Required Not Found 
 *  30606 - Request Time Expires Timeout 
 *  30607 - Channel Token Timeout 
 *  30608 - Bind Relation Not Found 
 *  30609 - Bind Number Too Many
 *  	
 * 当您遇到以上返回错误时，如果解释不了您的问题，请用同一请求的返回值requestId和errorCode联系我们追查问题。
 *  
 */
public class MyPushMessageReceiver extends FrontiaPushMessageReceiver {
    /** TAG to Log */
    public static final String TAG = MyPushMessageReceiver.class
            .getSimpleName();

    /**
     * 调用PushManager.startWork后，sdk将对push
     * server发起绑定请求，这个过程是异步的。绑定请求的结果通过onBind返回。 如果您需要用单播推送，需要把这里获取的channel
     * id和user id上传到应用server中，再调用server接口用channel id和user id给单个手机或者用户推送。
     * 
     * @param context
     *            BroadcastReceiver的执行Context
     * @param errorCode
     *            绑定接口返回值，0 - 成功
     * @param appid
     *            应用id。errorCode非0时为null
     * @param userId
     *            应用user id。errorCode非0时为null
     * @param channelId
     *            应用channel id。errorCode非0时为null
     * @param requestId
     *            向服务端发起的请求id。在追查问题时有用；
     * @return none
     */
    @Override
    public void onBind(Context context, int errorCode, String appid,
            String userId, String channelId, String requestId) {
        String responseString = "onBind errorCode=" + errorCode + " appid="
                + appid + " userId=" + userId + " channelId=" + channelId
                + " requestId=" + requestId;
        
        // Log.d(TAG, responseString);
        Log.i(TAG, "baidu push : " + responseString);
        
        // 绑定成功，设置已绑定flag，可以有效的减少不必要的绑定请求
        if (errorCode == 0) {
        	ImLoginManager.getInstance().setUserId(userId);
        	ImLoginManager.getInstance().setChannelId(channelId);
            Utils.setBind(context, true);
        }
        
        // Demo更新界面展示代码，应用请在这里加入自己的处理逻辑
        // updateContent(context, responseString);
    }

    /**
     * 接收透传消息的函数。
     * 
     * @param context
     *            上下文
     * @param message
     *            推送的消息
     * @param customContentString
     *            自定义内容,为空或者json字符串
     */
    @Override
    public void onMessage(Context context, String message, String customContentString) {
        String messageString = "透传消息 message=\"" + message + "\" customContentString=" + customContentString;
        Log.d(TAG, messageString);

        // 自定义内容获取方式，mykey和myvalue对应透传消息推送时自定义内容中设置的键和值
        if (!TextUtils.isEmpty(customContentString)) {
            JSONObject customJson = null;
            try {
                customJson = new JSONObject(customContentString);
                String myvalue = null;
                if (customJson.isNull(GlobalConstants.PUSH_KEY)) {
                    myvalue = customJson.getString(GlobalConstants.PUSH_KEY);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        
        // 此时获得服务器端推送的Push消息
        try {
			JSONObject jsonObject = new JSONObject(message);
			Log.i(TAG, jsonObject.toString());
			
			int type = jsonObject.getInt(GlobalConstants.JSON_TYPE);
			
			showNotificationByType(type, message);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        

        // Demo更新界面展示代码，应用请在这里加入自己的处理逻辑
        // updateContent(context, messageString);
    }

    /**
     * 接收通知点击的函数。注：推送通知被用户点击前，应用无法通过接口获取通知的内容。
     * 
     * @param context
     *            上下文
     * @param title
     *            推送的通知的标题
     * @param description
     *            推送的通知的描述
     * @param customContentString
     *            自定义内容，为空或者json字符串
     */
    @Override
    public void onNotificationClicked(Context context, String title,
            String description, String customContentString) {
        String notifyString = "通知点击 title=\"" + title + "\" description=\""
                + description + "\" customContent=" + customContentString;
        Log.d(TAG, notifyString);

        // 自定义内容获取方式，mykey和myvalue对应通知推送时自定义内容中设置的键和值
        if (!TextUtils.isEmpty(customContentString)) {
            JSONObject customJson = null;
            try {
                customJson = new JSONObject(customContentString);
                String myvalue = null;
                
                if (customJson.isNull(GlobalConstants.PUSH_KEY)) {
                    myvalue = customJson.getString(GlobalConstants.PUSH_KEY);
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        // Demo更新界面展示代码，应用请在这里加入自己的处理逻辑
        updateContent(context, notifyString);
    }

    /**
     * setTags() 的回调函数。
     * 
     * @param context
     *            上下文
     * @param errorCode
     *            错误码。0表示某些tag已经设置成功；非0表示所有tag的设置均失败。
     * @param successTags
     *            设置成功的tag
     * @param failTags
     *            设置失败的tag
     * @param requestId
     *            分配给对云推送的请求的id
     */
    @Override
    public void onSetTags(Context context, int errorCode,
            List<String> sucessTags, List<String> failTags, String requestId) {
        String responseString = "onSetTags errorCode=" + errorCode
                + " sucessTags=" + sucessTags + " failTags=" + failTags
                + " requestId=" + requestId;
        Log.d(TAG, responseString);

        // Demo更新界面展示代码，应用请在这里加入自己的处理逻辑
        updateContent(context, responseString);
    }

    /**
     * delTags() 的回调函数。
     * 
     * @param context
     *            上下文
     * @param errorCode
     *            错误码。0表示某些tag已经删除成功；非0表示所有tag均删除失败。
     * @param successTags
     *            成功删除的tag
     * @param failTags
     *            删除失败的tag
     * @param requestId
     *            分配给对云推送的请求的id
     */
    @Override
    public void onDelTags(Context context, int errorCode,
            List<String> sucessTags, List<String> failTags, String requestId) {
        String responseString = "onDelTags errorCode=" + errorCode
                + " sucessTags=" + sucessTags + " failTags=" + failTags
                + " requestId=" + requestId;
        Log.d(TAG, responseString);

        // Demo更新界面展示代码，应用请在这里加入自己的处理逻辑
        updateContent(context, responseString);
    }

    /**
     * listTags() 的回调函数。
     * 
     * @param context
     *            上下文
     * @param errorCode
     *            错误码。0表示列举tag成功；非0表示失败。
     * @param tags
     *            当前应用设置的所有tag。
     * @param requestId
     *            分配给对云推送的请求的id
     */
    @Override
    public void onListTags(Context context, int errorCode, List<String> tags,
            String requestId) {
        String responseString = "onListTags errorCode=" + errorCode + " tags="
                + tags;
        Log.d(TAG, responseString);

        // Demo更新界面展示代码，应用请在这里加入自己的处理逻辑
        updateContent(context, responseString);
    }

    /**
     * PushManager.stopWork() 的回调函数。
     * 
     * @param context
     *            上下文
     * @param errorCode
     *            错误码。0表示从云推送解绑定成功；非0表示失败。
     * @param requestId
     *            分配给对云推送的请求的id
     */
    @Override
    public void onUnbind(Context context, int errorCode, String requestId) {
        String responseString = "onUnbind errorCode=" + errorCode
                + " requestId = " + requestId;
        Log.d(TAG, responseString);

        // 解绑定成功，设置未绑定flag，
        if (errorCode == 0) {
            Utils.setBind(context, false);
        }
        // Demo更新界面展示代码，应用请在这里加入自己的处理逻辑
        updateContent(context, responseString);
    }

    private void updateContent(Context context, String content) {
        Log.d(TAG, "updateContent");
        String logText = "" + Utils.logStringCache;

        if (!logText.equals("")) {
            logText += "\n";
        }

        SimpleDateFormat sDateFormat = new SimpleDateFormat("HH-mm-ss");
        logText += sDateFormat.format(new Date()) + ": ";
        logText += content;

        Utils.logStringCache = logText;
        
        Log.i(TAG, "点击百度Push ");
        Intent intent = new Intent();
         
        // 待完善
        intent.setClass(context.getApplicationContext(), MainRecruitActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // 1--代表chat页
        intent.putExtra(GlobalConstants.PAGE, 1);
        context.getApplicationContext().startActivity(intent);
    }
    
    
    
    /**
     * 根据不同的类型显示不同的推送
     */
    private void showNotificationByType(int type, String message) {
    	switch (type) {
			case IMConstants.TYPE_P2P_TEXT:
				// 接收到一条消息，此时首先发送Notification
				ImNotificationManager.getInstance().showP2PPushTextNotification(message);
				
				// 1.将此消息加入到MessageTable
				/*ImDbManager.getInstance().saveTextMessageToDatabase(message);
				// 2.将此消息加入到MessageSummaryTable
				ImDbManager.getInstance().saveTextMessageSummaryToDatabase(message);
				// 此时，处理额外的操作，比如设置是否已读等
				ImLoginManager.getInstance().sendReadStatus(message);*/
				
				break;
			case IMConstants.TYPE_P2P_JOB_INVITATION:
				//ImDbManager.getInstance().saveTextMessageToDatabase(message);
				break;
			case IMConstants.TYPE_P2P_JOB_INVITATION_ACCEPT:
				//ImDbManager.getInstance().saveTextMessageToDatabase(message);
				break;
			case IMConstants.TYPE_P2P_JOB_INVITATION_IGNORE:
				//ImDbManager.getInstance().saveTextMessageToDatabase(message);
				break;
			case IMConstants.TYPE_P2P_INTERVIEW_INVITATION:
				//ImDbManager.getInstance().saveTextMessageToDatabase(message);
				break;
			case IMConstants.TYPE_P2P_JOB_SUMMARY:
				//ImDbManager.getInstance().saveTextMessageToDatabase(message);
				break;
				
			case IMConstants.TYPE_MSG_READ:
				// 此时，代表发送的消息已读，所以要更新数据库信息
				//ImDbManager.getInstance().updateMessageToDatabase(message);
				break;
				
			case IMConstants.TYPE_LETTER_GREETING:
				// 打招呼消息
				//ImDbManager.getInstance().insertOrUpdateStrangerMessageListToDatabase(message);
				// 然后，发送Notification
				ImNotificationManager.getInstance().showCommonPushTextNotification(message);
				break;
			case IMConstants.TYPE_LETTER_QUESTION:
				// 提问消息
				//ImDbManager.getInstance().insertOrUpdateStrangerMessageListToDatabase(message);
				ImNotificationManager.getInstance().showCommonPushTextNotification(message);
				break;
			case IMConstants.TYPE_LETTER_ANSWER:
				// 提问被回答的消息
				//ImDbManager.getInstance().insertOrUpdateStrangerMessageListToDatabase(message);
				ImNotificationManager.getInstance().showCommonPushTextNotification(message);
				break;
			case IMConstants.TYPE_LETTER_BUDDY_INVITATION:
				// 好友请求消息
				// ImDbManager.getInstance().saveFriendMessageSummaryToDatabase(message);
				//ImDbManager.getInstance().insertOrUpdateStrangerMessageListToDatabase(message);
				// 加好友
				ImNotificationManager.getInstance().showCommonPushTextNotification(message);
				break;
			case IMConstants.TYPE_LETTER_JOB_RECOMMEND:
				// 职位推荐
				//ImDbManager.getInstance().insertOrUpdateJobRecommendMessageListToDatabase(message);
				ImNotificationManager.getInstance().showCommonPushTextNotification(message);
				break;
			case IMConstants.TYPE_LETTER_JOB_APPLY_NOTIFY:
				// 职位投递提醒消息
				//ImDbManager.getInstance().saveJobApplyMessageSummaryToDatabase(message);
				// 弹出Notification
				ImNotificationManager.getInstance().showCommonPushTextNotification(message);
				break;
				
			case IMConstants.TYPE_LETTER_GROUP_RECOMMEND:
				// 群推荐消息
				//ImDbManager.getInstance().insertOrUpdateGroupRecommendMessageListToDatabase(message);
				ImNotificationManager.getInstance().showCommonPushTextNotification(message);
				break;
				
			case IMConstants.TYPE_GROUP_TEXT:
				// 加入群的消息和群消息都是一种类型
				// 1.将此消息加入到MessageGroupTable
				//ImDbManager.getInstance().saveGroupTextMessageToDatabase(message);
				// 2.将此消息加入到MessageSummaryTable
				//ImDbManager.getInstance().saveGroupTextMessageSummaryToDatabase(message);
				ImNotificationManager.getInstance().showGroupPushTextNotification(message);
				break;
				
			case IMConstants.TYPE_GROUP_JOIN:
				// 加入群
				// 1.将此消息加入到MessageGroupTable
				//ImDbManager.getInstance().saveGroupTextMessageToDatabase(message);
				//ImDbManager.getInstance().saveGroupTextMessageSummaryToDatabase(message);
				ImNotificationManager.getInstance().showGroupPushTextNotification(message);
				break;
				
			case IMConstants.TYPE_GROUP_QUIT:
				//ImDbManager.getInstance().saveGroupTextMessageToDatabase(message);
				//ImDbManager.getInstance().saveGroupTextMessageSummaryToDatabase(message);
				// ImNotificationManager.getInstance().showGroupTextNotification(message);
				break;
				
			case IMConstants.TYPE_GROUP_INTERVIEW_INVITATION:
				//ImDbManager.getInstance().saveGroupTextMessageToDatabase(message);
				//ImDbManager.getInstance().saveGroupTextMessageSummaryToDatabase(message);
				// ImNotificationManager.getInstance().showGroupTextNotification(message);
				break;
			case IMConstants.TYPE_GROUP_INTERVIEW_ACCEPT:
				//ImDbManager.getInstance().saveGroupTextMessageToDatabase(message);
				//ImDbManager.getInstance().saveGroupTextMessageSummaryToDatabase(message);
				// ImNotificationManager.getInstance().showGroupTextNotification(message);
				break;
			case IMConstants.TYPE_GROUP_INTERVIEW_IGNORE:
				//ImDbManager.getInstance().saveGroupTextMessageToDatabase(message);
				//ImDbManager.getInstance().saveGroupTextMessageSummaryToDatabase(message);
				// ImNotificationManager.getInstance().showGroupTextNotification(message);
				break;
			case IMConstants.TYPE_GROUP_JOB_APPLY_READ:
				//ImDbManager.getInstance().saveGroupTextMessageToDatabase(message);
				//ImDbManager.getInstance().saveGroupTextMessageSummaryToDatabase(message);
				// ImNotificationManager.getInstance().showGroupTextNotification(message);
				break;
			case IMConstants.TYPE_GROUP_JOB_APPLY_INTEREST:
				//ImDbManager.getInstance().saveGroupTextMessageToDatabase(message);
				//ImDbManager.getInstance().saveGroupTextMessageSummaryToDatabase(message);
				// ImNotificationManager.getInstance().showGroupTextNotification(message);
				break;
			case IMConstants.TYPE_GROUP_JOB_APPLY_CONTACT_READ:
				//ImDbManager.getInstance().saveGroupTextMessageToDatabase(message);
				//ImDbManager.getInstance().saveGroupTextMessageSummaryToDatabase(message);
				// ImNotificationManager.getInstance().showGroupTextNotification(message);
				break;
				
			case IMConstants.TYPE_P2P_BUDDY_INVITATION_ACCEPT:
				// ImDbManager.getInstance().saveFriendMessageSummaryToDatabase(message);
				// 接受好友请求, 之后弹出的是P2P聊天
				// ImDbManager.getInstance().insertOrUpdateStrangerMessageListToDatabase(message);
				// 1.将此消息加入到MessageTable
				//ImDbManager.getInstance().saveTextMessageToDatabase(message);
				// 2.将此消息加入到MessageSummaryTable
				//ImDbManager.getInstance().saveTextMessageSummaryToDatabase(message);
				// 接收到一条消息，此时首先发送Notification
				ImNotificationManager.getInstance().showP2PPushTextNotification(message);
				break;
				
			case IMConstants.TYPE_P2P_JOB_APPLY:
				//ImDbManager.getInstance().saveTextMessageToDatabase(message);
				//ImDbManager.getInstance().saveTextMessageSummaryToDatabase(message);
				ImNotificationManager.getInstance().showP2PPushTextNotification(message);
				break;
				
			case IMConstants.TYPE_P2P_JOB_APPLY_READ:
				// 简历被HR查看
				//ImDbManager.getInstance().saveTextMessageToDatabase(message);
				//ImDbManager.getInstance().saveTextMessageSummaryToDatabase(message);
				ImNotificationManager.getInstance().showP2PPushTextNotification(message);
				break;
				
			case IMConstants.TYPE_P2P_JOB_APPLY_INTEREST:
				// 简历被HR感兴趣
				//ImDbManager.getInstance().saveTextMessageToDatabase(message);
				//ImDbManager.getInstance().saveTextMessageSummaryToDatabase(message);
				ImNotificationManager.getInstance().showP2PPushTextNotification(message);
				break;
				
			case IMConstants.TYPE_P2P_INTERVIEW_ACCEPT:
				// 面试邀请被接受
				//ImDbManager.getInstance().saveTextMessageToDatabase(message);
				//ImDbManager.getInstance().saveTextMessageSummaryToDatabase(message);
				ImNotificationManager.getInstance().showP2PPushTextNotification(message);
				break;
			case IMConstants.TYPE_P2P_INTERVIEW_IGNORE:
				// 面试邀请被拒绝
				//ImDbManager.getInstance().saveTextMessageToDatabase(message);
				//ImDbManager.getInstance().saveTextMessageSummaryToDatabase(message);
				ImNotificationManager.getInstance().showP2PPushTextNotification(message);
				break;
    	}
    }

}
