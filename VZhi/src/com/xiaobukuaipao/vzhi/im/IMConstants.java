package com.xiaobukuaipao.vzhi.im;

public class IMConstants {
	
	/**
	 * Socket连接部分的类型号
	 */
	// 连接上socket服务器后的登录信息
	public static final int TYPE_LOGIN = 9901;
	// 登录成功后服务器端下发登录成功消息
	public static final int TYPE_LOGIN_OK = 9902;
	// T票校验失败后，服务器下发登录失败消息
	public static final int TYPE_LOGIN_FAIL = 9903;
	// 多端登录时，向原socket下发的逐出消息
	public static final int TYPE_ANOTHER_LOGIN = 9904;
	// 客户端已准备好接受socket消息
	public static final int TYPE_CLIENT_READY = 9905;
	// 客户端上报某条消息已读
	public static final int TYPE_MSG_READ = 9906;
	
	/**
	 * 消息类型
	 */
	public static final int TYPE_P2P_TEXT = 1001;
	
	public static final int TYPE_P2P_REAL_CARD = 1016;
	public static final int TYPE_P2P_NICK_CARD = 1015;
	
	public static final int TYPE_P2P_JOB_INVITATION = 1010;
	public static final int TYPE_P2P_JOB_INVITATION_ACCEPT = 1011;
	public static final int TYPE_P2P_JOB_INVITATION_IGNORE = 1012;
	
	public static final int TYPE_P2P_INTERVIEW_INVITATION = 1007;
	public static final int TYPE_P2P_INTERVIEW_ACCEPT = 1008;
	public static final int TYPE_P2P_INTERVIEW_IGNORE = 1009;
	
	public static final int TYPE_P2P_JOB_SUMMARY = 1013;
	
	public static final int TYPE_P2P_BUDDY_INVITATION_ACCEPT = 1006;
	
	public static final int TYPE_P2P_JOB_APPLY = 1017;
	public static final int TYPE_P2P_JOB_APPLY_READ = 1018;
	public static final int TYPE_P2P_JOB_APPLY_INTEREST = 1019;
	
	public static final int TYPE_P2P_JOB_APPLY_CONTACT_READ = 1020;
	
	// 群组
	public static final int TYPE_GROUP_TEXT = 2001;
	public static final int TYPE_GROUP_JOIN = 2003;
	public static final int TYPE_GROUP_QUIT = 2004;
	public static final int TYPE_GROUP_INTERVIEW_INVITATION = 2007;
	public static final int TYPE_GROUP_INTERVIEW_ACCEPT = 2008;
	public static final int TYPE_GROUP_INTERVIEW_IGNORE = 2009;
	public static final int TYPE_GROUP_JOB_APPLY_READ = 2018;
	public static final int TYPE_GROUP_JOB_APPLY_INTEREST = 2019;
	public static final int TYPE_GROUP_JOB_APPLY_CONTACT_READ = 2020;
	
	/**
	 * 群组的显示类型
	 */
	public static final int DISPLAY_TYPE_GROUP_TEXT = 0x0001;
	public static final int DISPLAY_TYPE_GROUP_JOIN = 0x0002;
	public static final int DISPLAY_TYPE_GROUP_QUIT = 0x0003;
	public static final int DISPLAY_TYPE_GROUP_JOB_APPLY_READ = 0x0004;
	public static final int DISPLAY_TYPE_GROUP_JOB_APPLY_INTEREST = 0x0005;
	public static final int DISPLAY_TYPE_GROUP_PROMPT_TEXT = 0x0006;
	
	// 陌生人-- 所有的，这个字段是自己设的
	public static final int TYPE_LETTER_STRANGER = 3000;
	// 打招呼消息
	public static final int TYPE_LETTER_GREETING = 3101;
	// 提问消息
	public static final int TYPE_LETTER_QUESTION = 3102;
	// 提问被回答的消息
	public static final int TYPE_LETTER_ANSWER = 3103;
	// 好友请求消息
	public static final int TYPE_LETTER_BUDDY_INVITATION = 3104;
	// 已接受好友请求消息
	/*public static final int TYPE_LETTER_BUDDY_INVITATION_ACCEPT = 3105;*/
	// 职位推荐消息
	public static final int TYPE_LETTER_JOB_RECOMMEND = 3107;
	// 职位投递提醒
	public static final int TYPE_LETTER_JOB_APPLY_NOTIFY = 3108;
	// 群推荐消息
	public static final int TYPE_LETTER_GROUP_RECOMMEND = 3109;
	
	/**
	 * 消息Display类型--此类型根据以上的消息类型数据来进行设值
	 */
	public static final int DISPLAY_TYPE_TEXT = 0x0001;
	public static final int DISPLAY_TYPE_REAL_PROFILE_CARD = 0x0002;
	public static final int DISPLAY_TYPE_NICK_PROFILE_CARD = 0x0003;
	public static final int DISPLAY_TYPE_JOB_INVITATION = 0x0004;
	public static final int DISPLAY_TYPE_PROMPT_TEXT = 0x0005;
	public static final int DISPLAY_TYPE_INTERVIEW_INVITATION = 0x0006;
	public static final int DISPLAY_TYPE_JOB_SUMMARY = 0x0007;
	public static final int DISPLAY_TYPE_COMPOSE_PROMPT_TEXT = 0x0008;
	public static final int DISPLAY_TYPE_COMPOSE_JSON_PROMPT_TEXT = 0x0009;
	
	/**
	 * 消息列表Display类型--6种
	 */
	public static final int DISPLAY_LIST_TYPE_JOBAPPLY = 0x0001;
	public static final int DISPLAY_LIST_TYPE_STRANGERLETTER = 0x0002;
	public static final int DISPLAY_LIST_TYPE_JOBRECOMMEND = 0x0003;
	public static final int DISPLAY_LIST_TYPE_GROUPRECOMMEND = 0x0004;
	public static final int DISPLAY_LIST_TYPE_DIALOG_PERSON = 0x0005;
	public static final int DISPLAY_LIST_TYPE_DIALOG_GROUP = 0x0006;
	
	/**
	 * 陌生人收件箱消息类型
	 */
	public static final int STRANGER_TYPE_GREETING = 3101;
	public static final int STRANGER_TYPE_QUESTION = 3102;
	public static final int STRANGER_TYPE_ANSWER = 3103;
	public static final int STRANGER_TYPE_INVITATION = 3104;
	
	// 图片文件
	public static final int FILE_SAVE_TYPE_IMAGE = 0x0013;
	// 语音文件
	public static final int FILE_SAVE_TYPE_AUDIO = 0x0014;
	
	public static String MD5_KEY = "%032xxnXiaobukuaipao";
	public static final String DEFAULT_IMAGE_FORMAT = ".jpg";
	
}
