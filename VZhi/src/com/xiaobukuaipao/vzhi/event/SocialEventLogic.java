package com.xiaobukuaipao.vzhi.event;

import java.util.HashMap;
import java.util.Map;

import com.android.volley.Request.Method;
import com.xiaobukuaipao.vzhi.R;
import com.xiaobukuaipao.vzhi.event.parser.ResultParser;
import com.xiaobukuaipao.vzhi.volley.InfoResultRequest;

public class SocialEventLogic extends BaseEventLogic {
	
	
	/**
	 * 获取好友列表
	 */
	public void getBuddyList(){
		InfoResultRequest infoResultRequest = new InfoResultRequest(R.id.social_buddy_list_get,ApiConstants.BUDDY_LIST_GET,null,new ResultParser(), this);
		sendRequest(infoResultRequest, R.id.social_buddy_list_get);
	}
	
	
	/**
	 * 打招呼
	 * @param receiver
	 * @param greetingid
	 * 	只有回应招呼的时候会用到
	 */
	public void sendGreeting(String receiver,String greetingid){
		Map<String,String> map = new HashMap<String,String>();
		map.put("receiver", receiver);
		map.put("greetingid", greetingid);
		
		InfoResultRequest infoResultRequest = new InfoResultRequest(R.id.social_stranger_send_greeting,ApiConstants.STRANGER_GREETING_SEND,
				Method.POST, map,null,new ResultParser(), this);
		sendRequest(infoResultRequest, R.id.social_stranger_send_greeting);
	}
	
	/**
	 * 提问
	 */
	public void sendQuestion(String receiver,String question) {
		Map<String,String> map = new HashMap<String,String>();
		map.put("receiver", receiver);
		map.put("question", question);
		
		InfoResultRequest infoResultRequest = new InfoResultRequest(R.id.social_stranger_send_question,ApiConstants.STRANGER_QUESTION_SEND,Method.POST, map,null,new ResultParser(), this);
		sendRequest(infoResultRequest, R.id.social_stranger_send_question);
	}
	/**
	 * 回答
	 */
	public void answerQuestion(String questionid,String answer) {
		Map<String,String> map = new HashMap<String,String>();
		map.put("questionid", questionid);
		map.put("answer", answer);
		
		InfoResultRequest infoResultRequest = new InfoResultRequest(R.id.social_stranger_answer_question,ApiConstants.STRANGER_ANSWER_SEND,Method.POST, map,null,new ResultParser(), this);
		sendRequest(infoResultRequest, R.id.social_stranger_answer_question);
	}
	
	/**
	 * 解除好友
	 */
	public void unbindBuddy(String buddyid) {
		Map<String,String> map = new HashMap<String,String>();
		map.put("buddyid", buddyid);
		
		InfoResultRequest infoResultRequest = new InfoResultRequest(R.id.social_unbind_buddy,ApiConstants.BUDDY_INVITATION_UNBIND,Method.POST, map,null,new ResultParser(), this);
		sendRequest(infoResultRequest, R.id.social_unbind_buddy);
	}
	
	/**
	 * 接受好友请求
	 */
	public void invitationAccept(String buddyid) {
		Map<String,String> map = new HashMap<String,String>();
		map.put("buddyid", buddyid);
		
		InfoResultRequest infoResultRequest = new InfoResultRequest(R.id.social_stranger_buddy_accept,ApiConstants.BUDDY_INVITATION_ACCEPT,Method.POST, map,null,new ResultParser(), this);
		sendRequest(infoResultRequest, R.id.social_stranger_buddy_accept);
	}
	/**
	 * 发送好友请求
	 */
	public void sendInvitation(String buddyid) {
		Map<String,String> map = new HashMap<String,String>();
		map.put("buddyid", buddyid);
		
		InfoResultRequest infoResultRequest = new InfoResultRequest(R.id.social_send_invitation,ApiConstants.BUDDY_INVITATION_SEND,Method.POST, map,null,new ResultParser(), this);
		sendRequest(infoResultRequest, R.id.social_send_invitation);
	}
	
	/**
	 * 获取陌生人消息未度数
	 */
	public void getStrangerCount(){
		
		InfoResultRequest infoResultRequest = new InfoResultRequest(R.id.social_stranger_news_count,ApiConstants.STRANGER_LIST_COUNT, null,new ResultParser(), this);
		sendRequest(infoResultRequest, R.id.social_stranger_news_count);
	}
	
	/**
	 * 获取陌生人收件箱列表
	 * @param minletterid
	 */
	public void getStrangerList(Integer minletterid){
		
		String param = "?minletterid=" +minletterid;
		
		InfoResultRequest infoResultRequest = new InfoResultRequest(R.id.social_stranger_list, ApiConstants.STRANGER_LIST_GET + param, null, new ResultParser(), this);
		sendRequest(infoResultRequest, R.id.social_stranger_list);
	}
	
	public void cancel(Object tag){
		cancelAll(tag);
	}
	
	/**
	 * 得到Server的IP和Port
	 */
	public void getSocketIpAndPort() {
		InfoResultRequest infoResultRequest = new InfoResultRequest(R.id.socket_ip_and_port, ApiConstants.SOCKET_IP_PORT_GET, 
				null, new ResultParser(), this);
		sendRequest(infoResultRequest, R.id.socket_ip_and_port);
	}
	
	/**
	 * 开启聊天对话框， 创建或者获取对话id
	 * @param isreal
	 */
	public void createOrGetDialogId(int isreal, String receiverid, int receiverisreal) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("isreal", String.valueOf(isreal));
		map.put("receiverid", String.valueOf(receiverid));
		map.put("receiverisreal", String.valueOf(receiverisreal));
		InfoResultRequest infoResultRequest = new InfoResultRequest(R.id.social_get_dialog_id, ApiConstants.SOCIAL_GET_DIALOG_ID, Method.POST,  map,null,new ResultParser(), this);
		// 第二个参数可以不设置, 取消请求用
		sendRequest(infoResultRequest, R.id.social_get_dialog_id);
	}
	
	/**
	 * 发送P2P文本聊天消息
	 */
	public void sendTextMessage(int isreal, String did, String receiverid, int receiverisreal, String topicid, String cmid, String body) {
		
		Map<String, String> map = new HashMap<String, String>();
		map.put("isreal", String.valueOf(isreal));
		map.put("did", did);
		map.put("receiverid", receiverid);
		map.put("receiverisreal", String.valueOf(receiverisreal));
		map.put("topicid", topicid);
		map.put("cmid", cmid);
		map.put("body", body);
		
		InfoResultRequest infoResultRequest = new InfoResultRequest(R.id.social_send_text_message, 
				ApiConstants.SOCIAL_SEND_P2P_MESSAGE, Method.POST,  map, null, new ResultParser(), this);
		// 第二个参数可以不设置, 取消请求用
		sendRequest(infoResultRequest, R.id.social_send_text_message);
	}
	
	/**
	 * 发送实名卡片信息
	 */
	public void sendRealProfileCard(int isreal, String did, String receiverid, int receiverisreal, long ownerid) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("isreal", String.valueOf(isreal));
		map.put("did", did);
		map.put("receiverid", receiverid);
		map.put("receiverisreal", String.valueOf(receiverisreal));
		map.put("ownerid", String.valueOf(ownerid));
		
		InfoResultRequest infoResultRequest = new InfoResultRequest(R.id.social_send_real_profile_card, 
				ApiConstants.SOCIAL_SEND_REAL_PROFILE_CARD, Method.POST,  map, null, new ResultParser(), this);
		// 第二个参数可以不设置, 取消请求用
		sendRequest(infoResultRequest, R.id.social_send_real_profile_card);
	}
	
	/**
	 * 发送匿名卡片信息
	 */
	public void sendNickProfileCard(int isreal, String did, String receiverid, int receiverisreal, long ownerid) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("isreal", String.valueOf(isreal));
		map.put("did", did);
		map.put("receiverid", receiverid);
		map.put("receiverisreal", String.valueOf(receiverisreal));
		map.put("ownerid", String.valueOf(ownerid));
		
		InfoResultRequest infoResultRequest = new InfoResultRequest(R.id.social_send_nick_profile_card, 
				ApiConstants.SOCIAL_SEND_NICK_PROFILE_CARD, Method.POST,  map, null, new ResultParser(), this);
		// 第二个参数可以不设置, 取消请求用
		sendRequest(infoResultRequest, R.id.social_send_nick_profile_card);
	}
	
	/**
	 * 发送职位邀请
	 */
	public void sendPositionInvitation(String receiverid, int receiverisreal, String jid) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("receiverid", receiverid);
		map.put("receiverisreal", String.valueOf(receiverisreal));
		map.put("jid", jid);
		
		InfoResultRequest infoResultRequest = new InfoResultRequest(R.id.social_send_position_invitation, 
				ApiConstants.SOCIAL_SEND_POSITION_INVITATION, Method.POST,  map, null, new ResultParser(), this);
		// 第二个参数可以不设置, 取消请求用
		sendRequest(infoResultRequest, R.id.social_send_position_invitation);
	}
	
	/**
	 * 对职位邀请感兴趣
	 */
	public void interestJobInvitation(String did, String mid) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("did", did);
		map.put("mid", mid);
		
		InfoResultRequest infoResultRequest = new InfoResultRequest(R.id.social_interest_job_invitation, 
				ApiConstants.SOCIAL_INTEREST_JOB_INVITATION, Method.POST,  map, null, new ResultParser(), this);
		// 第二个参数可以不设置, 取消请求用
		sendRequest(infoResultRequest, R.id.social_interest_job_invitation);
	}
	
	/**
	 * 对职位邀请不感兴趣
	 */
	public void uninterestJobInvitation(String did, String mid) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("did", did);
		map.put("mid", mid);
		
		InfoResultRequest infoResultRequest = new InfoResultRequest(R.id.social_uninterest_job_invitation, 
				ApiConstants.SOCIAL_UNINTEREST_JOB_INVITATION, Method.POST,  map, null, new ResultParser(), this);
		// 第二个参数可以不设置, 取消请求用
		sendRequest(infoResultRequest, R.id.social_uninterest_job_invitation);
	}
	
	/**
	 * 接受面试邀请
	 */
	public void acceptInterviewInvitation(String did, String mid) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("did", did);
		map.put("mid", mid);
		
		InfoResultRequest infoResultRequest = new InfoResultRequest(R.id.social_accept_interview_invitation, 
				ApiConstants.SOCIAL_ACCEPT_INTERVIEW_INVITATION, Method.POST,  map, null, new ResultParser(), this);
		// 第二个参数可以不设置, 取消请求用
		sendRequest(infoResultRequest, R.id.social_accept_interview_invitation);
	}
	
	/**
	 * 忽略面试邀请
	 */
	public void ignoreInterviewInvitation(String did, String mid) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("did", did);
		map.put("mid", mid);
		
		InfoResultRequest infoResultRequest = new InfoResultRequest(R.id.social_ignore_interview_invitation, 
				ApiConstants.SOCIAL_IGNORE_INTERVIEW_INVITATION, Method.POST,  map, null, new ResultParser(), this);
		// 第二个参数可以不设置, 取消请求用
		sendRequest(infoResultRequest, R.id.social_ignore_interview_invitation);
	}
	
	/**
	 * 与候选人进行聊一聊操作
	 */
	public void candidateChat(String jid, String receiverid, Integer receiverisreal) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("jid", jid);
		map.put("receiverid", receiverid);
		map.put("receiverisreal", String.valueOf(receiverisreal));
		
		InfoResultRequest infoResultRequest = new InfoResultRequest(R.id.social_candidate_chat, 
				ApiConstants.SOCIAL_CANDIDATE_CHAT, Method.POST,  map, null, new ResultParser(), this);
		// 第二个参数可以不设置, 取消请求用
		sendRequest(infoResultRequest, R.id.social_candidate_chat);
	}
	
	/**
	 * 发送已读消息的请求
	 */
	public void getReadstatus(String did, String mid) {
		Map<String, String> map = new HashMap<String, String>();
		/*map.put("did", did);
		map.put("mid", mid);*/
		
		InfoResultRequest infoResultRequest = new InfoResultRequest(R.id.social_get_read_status, 
				ApiConstants.SOCIAL_GET_READ_STATUS + "?did=" + did + "&mid=" + mid, Method.GET,  map, null, new ResultParser(), this);
		// 第二个参数可以不设置, 取消请求用
		sendRequest(infoResultRequest, R.id.social_get_read_status);
	}
	
	/**
	 * 发送获取聊天框最新消息的请求
	 */
	public void getNewChatMessages(String did, String maxmsgid) {
		Map<String, String> map = new HashMap<String, String>();
		/*map.put("did", did);
		map.put("maxmsgid", maxmsgid);*/
		
		InfoResultRequest infoResultRequest = new InfoResultRequest(R.id.social_get_new_messages, 
				ApiConstants.SOCIAL_GET_NEW_MESSAGES + "?did=" + did + "&maxmsgid=" + maxmsgid, Method.GET,  map, null, new ResultParser(), this);
		// 第二个参数可以不设置, 取消请求用
		sendRequest(infoResultRequest, R.id.social_get_new_messages);
	}
	
	/**
	 * 发送获取群聊天框最新消息的请求
	 */
	public void getNewGroupChatMessages(String gid, String maxmsgid) {
		Map<String, String> map = new HashMap<String, String>();
		/*map.put("did", did);
		map.put("maxmsgid", maxmsgid);*/
		
		InfoResultRequest infoResultRequest = new InfoResultRequest(R.id.social_get_new_group_messages, 
				ApiConstants.SOCIAL_GET_NEW_GROUP_MESSAGES + "?gid=" + gid + "&maxmsgid=" + maxmsgid, Method.GET,  map, null, new ResultParser(), this);
		// 第二个参数可以不设置, 取消请求用
		sendRequest(infoResultRequest, R.id.social_get_new_group_messages);
	}
	
	/**
	 * 发送获取历史消息的请求
	 */
	public void getOldChatMessages(String did, String minmsgid) {
		Map<String, String> map = new HashMap<String, String>();
		/*map.put("did", did);
		map.put("maxmsgid", maxmsgid);*/
		
		InfoResultRequest infoResultRequest = new InfoResultRequest(R.id.social_get_old_messages, 
				ApiConstants.SOCIAL_GET_OLD_MESSAGES + "?did=" + did + "&minmsgid=" + minmsgid, Method.GET,  map, null, new ResultParser(), this);
		// 第二个参数可以不设置, 取消请求用
		sendRequest(infoResultRequest, R.id.social_get_old_messages);
	}
	
	/**
	 * 发送获取群历史消息的请求
	 */
	public void getOldGroupChatMessages(String gid, String minmsgid) {
		Map<String, String> map = new HashMap<String, String>();
		/*map.put("did", did);
		map.put("maxmsgid", maxmsgid);*/
		
		InfoResultRequest infoResultRequest = new InfoResultRequest(R.id.social_get_group_old_messages, 
				ApiConstants.SOCIAL_GET_GROUP_OLD_MESSAGES + "?gid=" + gid + "&minmsgid=" + minmsgid, Method.GET,  map, null, new ResultParser(), this);
		// 第二个参数可以不设置, 取消请求用
		sendRequest(infoResultRequest, R.id.social_get_group_old_messages);
	}
	
	
	/**
	 * 获取收件箱消息摘要--4种消息摘要
	 */
	public void getLetterSummary() {
		Map<String, String> map = new HashMap<String, String>();
		
		InfoResultRequest infoResultRequest = new InfoResultRequest(R.id.social_get_letter_summary, 
				ApiConstants.SOCIAL_GET_LETTER_SUMMARY, Method.GET,  map, null, new ResultParser(), this);
		// 第二个参数可以不设置, 取消请求用
		sendRequest(infoResultRequest, R.id.social_get_letter_summary);
	}
	
	/**
	 * 获取消息摘要--包括P2P和群
	 */
	public void getMessageSummary() {
		Map<String, String> map = new HashMap<String, String>();
		
		InfoResultRequest infoResultRequest = new InfoResultRequest(R.id.social_get_message_summary, 
				ApiConstants.SOCIAL_GET_MESSAGE_SUMMARY, Method.GET,  map, null, new ResultParser(), this);
		// 第二个参数可以不设置, 取消请求用
		sendRequest(infoResultRequest, R.id.social_get_message_summary);
	}
	
	
	
	/**
	 * 加入实名群（兴趣群）
	 * 
	 * @param gid
	 */
	public void joinGroup(String gid){
		Map<String, String> map = new HashMap<String, String>();
		map.put("gid", gid);
		
		InfoResultRequest infoResultRequest = new InfoResultRequest(R.id.social_group_join, 
				ApiConstants.SOCIAL_JOIN_GROUP, Method.POST,  map, null, new ResultParser(), this);
		// 第二个参数可以不设置, 取消请求用
		sendRequest(infoResultRequest, R.id.social_group_join);
	}
	
	/**
	 * 退出群
	 * @param gid
	 */
	public void quitGroup(String gid){
		Map<String, String> map = new HashMap<String, String>();
		map.put("gid", gid);
		
		InfoResultRequest infoResultRequest = new InfoResultRequest(R.id.social_group_quit, 
				ApiConstants.SOCIAL_QUIT_GROUP, Method.POST,  map, null, new ResultParser(), this);
		// 第二个参数可以不设置, 取消请求用
		sendRequest(infoResultRequest, R.id.social_group_quit);
	}
	
	/**
	 * 设置消息免打扰
	 * @param gid
	 */
	public void blockGroup(String gid){
		Map<String, String> map = new HashMap<String, String>();
		map.put("gid", gid);
		
		InfoResultRequest infoResultRequest = new InfoResultRequest(R.id.social_group_block, 
				ApiConstants.SOCIAL_BLOCK_GROUP, Method.POST,  map, null, new ResultParser(), this);
		// 第二个参数可以不设置, 取消请求用
		sendRequest(infoResultRequest, R.id.social_group_block);
	}
	
	/**
	 * 解除设置消息免打扰
	 * @param gid
	 */
	public void unblockGroup(String gid){
		Map<String, String> map = new HashMap<String, String>();
		map.put("gid", gid);
		
		InfoResultRequest infoResultRequest = new InfoResultRequest(R.id.social_group_unblock, 
				ApiConstants.SOCIAL_UNBLOCK_GROUP, Method.POST,  map, null, new ResultParser(), this);
		// 第二个参数可以不设置, 取消请求用
		sendRequest(infoResultRequest, R.id.social_group_unblock);
	}
	
	/**
	 * 获取群资料
	 * @param gid
	 */
	public void getGroupinfo(String gid){
		
		InfoResultRequest infoResultRequest = new InfoResultRequest(R.id.social_group_get_info, 
				ApiConstants.SOCIAL_GET_GROUP_INFO + "?gid=" + gid ,null, new ResultParser(), this);
		// 第二个参数可以不设置, 取消请求用
		sendRequest(infoResultRequest, R.id.social_group_get_info);
	}
	
	/**
	 * 获取联系人的头像和名字信息
	 */
	public void getPersonAvatarAndName(long ownerid, long did) {
		InfoResultRequest infoResultRequest = new InfoResultRequest(R.id.social_get_person_avatar, 
				ApiConstants.SOCIAL_GET_PERSON_AVATAR + "?ownerid=" + ownerid + "&did=" + did ,null, new ResultParser(), this);
		// 第二个参数可以不设置, 取消请求用
		sendRequest(infoResultRequest, R.id.social_get_person_avatar);
	}
	
	public void getGroupPersonAvatarAndName(long ownerid, long gid) {
		InfoResultRequest infoResultRequest = new InfoResultRequest(R.id.social_get_group_person_avatar, 
				ApiConstants.SOCIAL_GET_PERSON_AVATAR + "?ownerid=" + ownerid + "&gid=" + gid ,null, new ResultParser(), this);
		// 第二个参数可以不设置, 取消请求用
		sendRequest(infoResultRequest, R.id.social_get_group_person_avatar);
	}
	
	/**
	 * 获取群组的头像和名字信息
	 */
	public void getGroupAvatarAndName(long gid) {
		InfoResultRequest infoResultRequest = new InfoResultRequest(R.id.social_get_group_avatar, 
				ApiConstants.SOCIAL_GET_GROUP_AVATAR + "?gid=" + gid ,null, new ResultParser(), this);
		// 第二个参数可以不设置, 取消请求用
		sendRequest(infoResultRequest, R.id.social_get_group_avatar);
	}
	
	/**
	 * 发送群文本消息
	 */
	public void sendGroupTextMessage(String gid, String body, String cmid) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("gid", gid);
		map.put("body", body);
		map.put("cmid", cmid);
		
		InfoResultRequest infoResultRequest = new InfoResultRequest(R.id.social_send_group_text, 
				ApiConstants.SOCIAL_SEND_GROUP_TEXT, Method.POST,  map, null, new ResultParser(), this);
		// 第二个参数可以不设置, 取消请求用
		sendRequest(infoResultRequest, R.id.social_send_group_text);
	}
	
	
	/**
	 * 获取群推荐列表
	 */
	public void getGetGroupRecommend(String mingrouprecommendid) {
		InfoResultRequest infoResultRequest = new InfoResultRequest(R.id.social_get_group_recommend, 
				ApiConstants.SOCIAL_GET_GROUP_RECOMMEND + "?mingrouprecommendid=" + mingrouprecommendid, null, new ResultParser(), this);
		// 第二个参数可以不设置, 取消请求用
		sendRequest(infoResultRequest, R.id.social_get_group_recommend);
	}
	
	/**
	 * 获取职位推荐列表
	 * 
	 * @param minjobrecommendid
	 */
	public void getRecPositionInfo(String minjobrecommendid){
		
		InfoResultRequest infoResultRequest = new InfoResultRequest(R.id.social_get_position_recommend, 
				ApiConstants.SOCIAL_GET_POSITION_RECOMMEND + "?minjobrecommendid=" + minjobrecommendid,null,new ResultParser(), this);
		sendRequest(infoResultRequest, R.id.social_get_position_recommend);
	}
	/**
	 * @param minmsgid
	 */
	public void getAppliedGroupList(String minmsgid){
		InfoResultRequest infoResultRequest = new InfoResultRequest(R.id.social_group_get_nick_list, 
				ApiConstants.SOCIAL_GET_NICK_GROUP_LIST + "?minmsgid=" + minmsgid, null, new ResultParser(), this);
		// 第二个参数可以不设置, 取消请求用
		sendRequest(infoResultRequest, R.id.social_group_get_nick_list);
	}
	
	/**
	 * @param minmsgid
	 */
	public void getRealGroupList(String minmsgid){
		InfoResultRequest infoResultRequest = new InfoResultRequest(R.id.social_group_get_real_list, 
				ApiConstants.SOCIAL_GET_REAL_GROUP_LIST + "?minmsgid=" + minmsgid, null, new ResultParser(), this);
		// 第二个参数可以不设置, 取消请求用
		sendRequest(infoResultRequest, R.id.social_group_get_real_list);
	}
	
	public void logout(String os, String userId, String channelId) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("os", os);
		map.put("userId", userId);
		map.put("channelId", channelId);
		
		InfoResultRequest infoResultRequest = new InfoResultRequest(R.id.social_logout, 
				ApiConstants.SOCIAL_LOGOUT, Method.POST,  map, null, new ResultParser(), this);
		// 第二个参数可以不设置, 取消请求用
		sendRequest(infoResultRequest, R.id.social_logout);
	}
	
	public void requestBlackSomebody(long blackuid) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("blackuid", String.valueOf(blackuid));
		
		InfoResultRequest infoResultRequest = new InfoResultRequest(R.id.social_black_somebody, 
				ApiConstants.SOCIAL_BLACK_SOMEBODY, Method.POST,  map, null, new ResultParser(), this);
		// 第二个参数可以不设置, 取消请求用
		sendRequest(infoResultRequest, R.id.social_black_somebody);
	}
}
