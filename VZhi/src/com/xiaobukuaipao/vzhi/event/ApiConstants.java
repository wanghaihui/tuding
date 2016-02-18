package com.xiaobukuaipao.vzhi.event;

/**
 * 
 */
public final class ApiConstants {
	
	public static final boolean DEBUG = false;
	/**
	 *  后台服务的URL
	 */
	public static final String PREFIX = "http://";
	
	// public static final int API_PORT = 8000;
	public static final int WEB_PORT = 80;
	
	public static final String DOMAIN = DEBUG ? "192.168.1.107" : "api.jobooking.cn";
	
	public static final String LOCALE_DOMAIN = "vpin.wcip.net";
	
	public static final String SHARE_DOMAIN = "jobooking.cn";
	
	public static final int PORT = DEBUG ? 8202 : 8000;
	
	public static final String SERVER_ADDRESS = PREFIX + DOMAIN + ":" + PORT + "/";
	
	public static final String WEB_ADDRESS = PREFIX + DOMAIN  + ":" + WEB_PORT  + "/";
	
	public static final String COPYRIGHTS = WEB_ADDRESS + "copyrights.html";
	
	public static final String ABOUT = WEB_ADDRESS + "about.html";
	
	public static final String CONTACT = WEB_ADDRESS + "contact.html";
	
	public static final String JOBINFO = PREFIX + SHARE_DOMAIN +"/wap/jd/";
	
	public static final String AWARD = PREFIX + SHARE_DOMAIN +"/award/job/list";
	
	
	
	
	/**
	 * 大登录
	 */
	public static final String REGISTER_NORMAL_LOGIN = SERVER_ADDRESS	
			+ "user/passport/1/login";
	
	/**
	 * 小登录
	 */
	public static final String REGISTER_FAST_LOGIN = SERVER_ADDRESS
			+ "user/passport/1/ticket/update";
			
	// register
	
	/**
	 * 获取求职者（白领）用户注册时，基本信息是否完整
	 */
	public static final String REGISTER_BASIC_COMPLETION = SERVER_ADDRESS 
			+ "user/profile/1/basic/completion/get";
	
	/**
	 * 获取验证码
	 * 
	 * POST
	 * 
	 * @param mobile
	 *            电话号
	 */
	public static final String REGISTER_CODE_GET = SERVER_ADDRESS
			+ "user/register/1/verifycode/get";
	/**
	 * 校验验证码
	 * 
	 * POST
	 * 
	 * @param mobile
	 *            电话号
	 * @param verifycode
	 *            校验码
	 */
	public static final String REGISTER_CODE_VERIFY = SERVER_ADDRESS
			+ "user/register/1/verifycode/verify";
	/**
	 * 设置身份
	 * 
	 * POST
	 * 
	 * @param identity
	 *            身份(0:求职者 1:招聘者)
	 * 
	 */
	public static final String REGISTER_IDEN_SET = SERVER_ADDRESS
			+ "user/register/1/identity/set";
	/**
	 * 设置密码
	 * 
	 * POST
	 * 
	 * @param passwd
	 *            密码
	 */
	public static final String REGISTER_PSWD_SET = SERVER_ADDRESS
			+ "user/register/1/passwd/set";
	/**
	 * 获取默认头像URL列表
	 * 
	 * GET
	 */
	public static final String REGISTER_AVATARS_GET = SERVER_ADDRESS
			+ "user/register/1/avatar/get";
	
	
	/**
	 * 设置基本信息
	 * 
	 * POST
	 * 
	 * @param avatar
	 *            头像
	 * @param nickname
	 *            昵称
	 * @param gender
	 *            性别
	 * @param age
	 *            年龄
	 * @param city
	 *            所在地
	 */
	public static final String REGISTER_BASIC_SET = SERVER_ADDRESS
			+ "user/register/1/basic/set";

	/**
	 * 上传虚拟头像
	 * 
	 * @param nickavatar
	 *            文件参数名
	 */
	public static final String UPLOAD_NICK_AVATAR = SERVER_ADDRESS
			+ "user/register/1/avatar/nick/upload";
	/**
	 * 上传真实头像
	 * 
	 * @param realavatar
	 *            文件参数名
	 */
	public static final String UPLOAD_REAL_AVATAR = SERVER_ADDRESS
			+ "user/register/1/avatar/real/upload";

	/**
	 * 求职意向设置
	 * 
	 * @param industry
	 *            即用户选择的行业列表中对应的id值
	 * @param minsalary
	 *            为最低期望薪酬，单位为K；
	 * @param corptypes
	 *            是期望公司类型，多选，对应公司类型列表的id值，用","连接
	 * @param state
	 *            为求职心态列表中对应的id值;
	 * @param cities
	 *            为城市列表中对应的id值，多选，以,连接；
	 * @param positions
	 *            为意向岗位列表对应id值，多选，以,相连，如果是用户自己填写的内容，则以用户自己填写内容为id）
	 * 
	 */
	public static final String REGISTER_INTENTION = SERVER_ADDRESS
			+ "user/register/1/intention/set";

	/**
	 * 设置hr的基本信息
	 */
	public static final String REGISTER_HR_BASIC_SET = SERVER_ADDRESS
			+ "user/register/1/hrbasic/set";

	/**
	 * 设置HR邮箱
	 */
	public static final String REGISTER_HR_EMAIL_CHECK = SERVER_ADDRESS
			+ "user/register/1/hr/email/check";
	
	/**
	 * 获取HR基本信息
	 */
	public static final String REGISTER_HR_BASIC_GET = SERVER_ADDRESS
			+ "user/profile/1/hr/basic/get";
	
	/**
	 * 公司规模字典
	 */
	public static final String CORP_SCALES_GET = SERVER_ADDRESS
			+"dict/corp/1/scale/get";
	
	/**
	 * 获取公司全称suggest
	 */
	public static final String REGISTER_HR_FULLCORPNAMES_GET  = SERVER_ADDRESS
			+ "corp/1/lname/suggest/get";
	
	
	/**
	 * 设置公司信息
	 */
	public static final String REGISTER_HR_CORP_SET = SERVER_ADDRESS
			+ "corp/1/corp/set";
	
	/**
	 * 给hr发送验证邮件
	 */
	public static final String REGISTER_HR_VERIFY_EMAIL_SEND = SERVER_ADDRESS
			+ "user/register/1/hr/email/verify/send";
	
	/**
	 * 按邮箱域名获取对应公司信息
	 */
	public static final String REGISTER_EMAIL_CORPSREC_GET = SERVER_ADDRESS
			+ "corp/1/corp/email/domain/get";
	
//	/**
//	 * 在注册时为HR增加工作经历
//	 */
//	public static final String REGISTER_HR_WORKEXP_ADD = SERVER_ADDRESS
//			+ "user/register/1/hr/workexpr/add";
	public static final String REGISTER_HR_WORKEXP_SET = SERVER_ADDRESS
	+ "user/register/1/hr/workexpr/set";
	/**
	 * 行业方向字典
	 */
	public static final String REGISTER_POSITION_INDUSTRY = SERVER_ADDRESS
			+ "dict/position/1/industry/get";

	/**
	 * 公司类型字典
	 */
	public static final String REGISTER_POSITION_CORP_TYPE = SERVER_ADDRESS
			+ "dict/corp/1/type/get";

	/**
	 * 求职意愿中的公司类型列表
	 */
	public static final String REGISTER_INTENTION_CORP_TYPE = SERVER_ADDRESS
			+ "dict/corp/1/ptype/get";
	
	/**
	 * 公司字典
	 */
	public static final String REGISTER_POSITION_CORP_GET = SERVER_ADDRESS
			+ "corp/1/suggest/get";
	
	/**
	 * 公司详细信息
	 */
	public static final String REGISTER_CORP_GET = SERVER_ADDRESS
			+ "corp/1/corp/get";
	
	/**
	 * 上传logo
	 */
	public static final String REGISTER_CORP_LOGO_UPLOAD = SERVER_ADDRESS
			+ "corp/1/logo/upload";
	/**
	 * 根据公司获取职位列表
	 */
	public static final String REGISTER_CORP_POSITIONS_GET = SERVER_ADDRESS
			+ "job/list/1/corp/all/get";

	/**
	 * 获取公司福利字典
	 */
	public static final String REGISTER_CORP_BENIFITS_GET = SERVER_ADDRESS
			+ "dict/corp/1/benefit/suggest/get";
	
	/**
	 * 求职心态字典
	 */
	public static final String REGISTER_POSITION_JOBSTATES = SERVER_ADDRESS
			+ "dict/user/1/jobseekerstate/get";

	/**
	 * 意向岗位
	 */
	public static final String REGISTER_POSITION_INTENT_POS = SERVER_ADDRESS
			+ "dict/position/1/suggest/get";

	/**
	 * 设置求职意向
	 */
	public static final String REGISTER_POSITION_INTENT_jOB = SERVER_ADDRESS
			+ "user/register/1/intention/set";
	/**
	 * 学历字典
	 */
	public static final String REGISTER_DEGREE_GET = SERVER_ADDRESS
			+ "dict/school/1/degree/get";
	/**
	 * 学校字典
	 */
	public static final String REGISTER_SCHOOL_GET = SERVER_ADDRESS
			+ "dict/school/1/suggest/get";
	/**
	 * 专业字典
	 */
	public static final String REGISTER_MAJOR_GET = SERVER_ADDRESS
			+ "dict/major/1/suggest/get";

	/**
	 * 职位字典
	 */
	public static final String REGISTER_POSITION_GET = SERVER_ADDRESS
			+ "dict/position/1/suggest/get";

	/**
	 * 获取基本信息
	 */
	public static final String REGISTER_BASICINFO_GET = SERVER_ADDRESS
			+ "user/profile/1/basic/get";
	/**
	 * 获取工作经历
	 */
	public static final String REGISTER_EXPERIENCES_GET = SERVER_ADDRESS
			+ "user/profile/1/workexpr/get";
	/**
	 * 获取教育经历
	 */
	public static final String REGISTER_EDUCATIONS_GET = SERVER_ADDRESS
			+ "user/profile/1/eduexpr/get";
	
	/**
	 * 获得当前用户的基本信息
	 */
	public static final String PROFILE_BASIC_INFO = SERVER_ADDRESS
			+ "user/profile/1/basic/get";

	/**
	 * 获得profile信息 会在服务器端判断是否是否是看自己的信息
	 */
	public static final String PROFILE_PERSONAL_PROFILE = SERVER_ADDRESS
			+ "user/profile/1/get";

	/**
	 * 设置基本信息
	 */
	public static final String PROFILE_BASICINFO_SET = SERVER_ADDRESS
			+ "user/profile/1/basic/set";

	/**
	 * 添加工作经历
	 */
	public static final String PROFILE_WORKEXPR_ADD = SERVER_ADDRESS
			+ "user/profile/1/workexpr/add";

	/**
	 * 修改工作经历
	 */
	public static final String PROFILE_WORKEXPR_MODIFY = SERVER_ADDRESS
			+ "user/profile/1/workexpr/set";

	/**
	 * 删除工作经历
	 */
	public static final String PROFILE_WORKEXPR_DELETE = SERVER_ADDRESS
			+ "user/profile/1/workexpr/del";

	/**
	 * 添加教育经历
	 */
	public static final String PROFILE_EDUCATION_ADD = SERVER_ADDRESS
			+ "user/profile/1/eduexpr/add";

	/**
	 * 修改教育经历
	 */
	public static final String PROFILE_EDUCATION_MODIFY = SERVER_ADDRESS
			+ "user/profile/1/eduexpr/set";

	/**
	 * 删除教育经历
	 */
	public static final String PROFILE_EDU_DELETE = SERVER_ADDRESS
			+ "user/profile/1/eduexpr/del";

	/**
	 * 获取当前用户技能
	 */
	public static final String PROFILE_TECH_GET = SERVER_ADDRESS
			+ "user/profile/1/skills/get";

	/**
	 * 技能字典
	 */
	public static final String PROFILE_TECHS_GET = SERVER_ADDRESS
			+ "dict/skill/1/suggest/get";

	/**
	 * 设置技能
	 */
	public static final String PROFILE_TECHS_SET = SERVER_ADDRESS
			+ "user/profile/1/skills/set";
	
	/**
	 * 获取用户隐私设置
	 */
	public static final String PROFILE_PRIVACY_GET = SERVER_ADDRESS
			+ "user/profile/1/privacy/get";
	
	/**
	 * 获取个人资料隐私设置选项列表
	 */
	public static final String PROFILE_PRIVACY_PROFILE_LEVELS_GET = SERVER_ADDRESS
			+ "user/profile/1/privacy/profile/levels/get";
	
	/**
	 * 获取联系方式隐私设置选项列表
	 */
	public static final String PROFILE_PRIVACY_CONTACT_LEVELS_GET = SERVER_ADDRESS
			+ "user/profile/1/privacy/contact/levels/get";
	
	/**
	 * 联系方式隐私设置
	 */
	public static final String PROFILE_PRIVACY_CONTACT_SET = SERVER_ADDRESS
			+ "user/profile/1/privacy/contact/level/set";

	/**
	 * 个人资料隐私设置
	 */
	public static final String PROFILE_PRIVACY_PROFILE_SET = SERVER_ADDRESS
			+ "user/profile/1/privacy/profile/level/set";
	
	/**
	 * 从投递场景进入到求职者的profile， "点击查看"联系方式
	 */
	public static final String PROFILE_TIP_PERSON_CONTACTS = SERVER_ADDRESS
			+ "user/profile/1/contact/read/set";
	
	/**
	 * 完善个人信息
	 */
	public static final String POST_RESUME_COMPLETE_STATUS_GET = SERVER_ADDRESS
			+ "user/profile/1/completion/get";

	public static final String POST_RESUME_ANO_CARD_GET = SERVER_ADDRESS
			+ "user/profile/1/card/nick/get";

	// 以下是职位Position相关API
	
	public static final String GET_AWARD_POSITIONS = SERVER_ADDRESS
			+ "job/award/1/activity/get";
	
	public static final String AWARD_SHARED_COUNT= SERVER_ADDRESS
			+ "job/award/1/share/add";
	
	
	public static final String GET_POSITIONED_POSITIONS = SERVER_ADDRESS
			+ "job/list/1/publish/all/get";

	/**
	 * 刷新发布的职位列表
	 */
	public static final String REFRESH_POSITIONED_POSITIONS = SERVER_ADDRESS
			+ "job/update/1/refresh";

	/**
	 * 关闭当前发布的职位
	 */
	public static final String CLOSE_POSITIONED_POSITIONS = SERVER_ADDRESS
			+ "job/update/1/close";

	/**
	 * 打开当前发布的职位
	 */
	public static final String OPEN_POSITIONED_POSITIONS = SERVER_ADDRESS
			+ "job/update/1/open";

	/**
	 * 统计候选人信息
	 */
	public static final String STATISTICS_CANDIDATE = SERVER_ADDRESS
			+ "job/apply/1/count";

	public static final String POSITION_RECRUIT_LIST = SERVER_ADDRESS
			+ "job/list/1/all/get";

	/**
	 * 已投递列表
	 */
	public static final String POSITION_MY_SENDED = SERVER_ADDRESS
			+ "job/list/1/user/all/get";

	/**
	 * 职位详情页
	 */
	public static final String POSITION_POSITION_INFO = SERVER_ADDRESS
			+ "job/desc/1/get";

	/**
	 * 创建发布职位
	 */
	public static final String POSITION_POSITION_CREATE = SERVER_ADDRESS
			+ "job/update/1/create";
	
	/**
	 * 修改发布职位
	 */
	public static final String POSITION_POSITION_UPDATE = SERVER_ADDRESS
			+ "job/update/1/set";
	/**
	 * 获取学历要求选项列表
	 */
	public static final String POSITION_JOBEDUS_GET = SERVER_ADDRESS
			+ "dict/job/1/edu/get";
	
	/**
	 * 发布职位时获取工作经验要求选项列表
	 */
	public static final String POSITION_JOBEXPRS_GET = SERVER_ADDRESS
			+ "dict/job/1/expr/get";
	/**
	 * 获得未处理的候选人信息
	 */
	public static final String UNREAD_CANDIDATE = SERVER_ADDRESS
			+ "job/apply/1/unread/get";

	/**
	 * 获得全部的候选人信息
	 */
	public static final String ALL_CANDIDATE = SERVER_ADDRESS
			+ "job/apply/1/all/get";
	
	/**
	 * 获取已联系的候选人信息
	 */
	public static final String CONTACTED_CANDIDATE = SERVER_ADDRESS
			+ "job/apply/1/contact/get";

	/**
	 * 处理候选人
	 */
	public static final String READ_CANDIDATE = SERVER_ADDRESS
			+ "job/apply/1/read/set";
	
	/**
	 * 对候选人感兴趣 
	 */
	public static final String INTEREST_CANDIDATE = SERVER_ADDRESS
			+ "job/apply/1/interest/set";
	
	/**
	 * 真名投递
	 */
	public static final String POSITION_REAL_APPLY = SERVER_ADDRESS
			+ "job/apply/1/real/apply";

	/**
	 * 匿名投递
	 */
	public static final String POSITION_NICK_APPLY = SERVER_ADDRESS
			+ "job/apply/1/nick/apply";

	// social
	/**
	 * 发送好友请求
	 */
	public static final String BUDDY_INVITATION_SEND = SERVER_ADDRESS
			+ "relation/buddy/1/invitation/send";

	/**
	 * 接受好友请求
	 */
	public static final String BUDDY_INVITATION_ACCEPT = SERVER_ADDRESS
			+ "relation/buddy/1/invitation/accept";
	/**
	 * 解除好友
	 */
	public static final String BUDDY_INVITATION_UNBIND = SERVER_ADDRESS
			+ "relation/buddy/1/dismiss";

	/**
	 * 获取好友列表
	 */
	public static final String BUDDY_LIST_GET = SERVER_ADDRESS
			+ "relation/buddy/1/list/get";

	/**
	 * 打招呼
	 */
	public static final String STRANGER_GREETING_SEND = SERVER_ADDRESS
			+ "letter/stranger/1/greeting/send";

	/**
	 * 提问
	 */
	public static final String STRANGER_QUESTION_SEND = SERVER_ADDRESS
			+ "letter/stranger/1/question/send";

	/**
	 * 回答
	 */
	public static final String STRANGER_ANSWER_SEND = SERVER_ADDRESS
			+ "letter/stranger/1/answer/send";

	/**
	 * 陌生人收件箱
	 */
	public static final String STRANGER_LIST_GET = SERVER_ADDRESS
			+ "letter/stranger/1/list/get";

	/**
	 * 陌生人收件箱未读数
	 */
	public static final String STRANGER_LIST_COUNT = SERVER_ADDRESS
			+ "letter/stranger/1/count/unread/get";
	
	
	/**
	 * 获取socket服务器的IP和端口
	 */
	public static final String SOCKET_IP_PORT_GET = SERVER_ADDRESS
			+ "im/message/1/server/sock/alloc";

	/**
	 * 创建或者获取对话id
	 */
	public static final String SOCIAL_GET_DIALOG_ID = SERVER_ADDRESS
			+ "im/dialogue/1/add";
	
	/**
	 * 发送P2P文本聊天信息
	 */
	public static final String SOCIAL_SEND_P2P_MESSAGE = SERVER_ADDRESS
			+ "im/message/1/p2p/text/send";
	
	
	/**
	 * 发送实名卡片聊天信息
	 */
	public static final String SOCIAL_SEND_REAL_PROFILE_CARD = SERVER_ADDRESS
			+ "im/message/1/p2p/card/real/send";
	
	/**
	 * 发送匿名卡片聊天信息
	 */
	public static final String SOCIAL_SEND_NICK_PROFILE_CARD = SERVER_ADDRESS
			+ "im/message/1/p2p/card/nick/send";
	
	/**
	 * 发送职位邀请
	 */
	public static final String SOCIAL_SEND_POSITION_INVITATION = SERVER_ADDRESS
			+ "im/message/1/p2p/invitation/job/send";
	
	/**
	 * 对职位邀请感兴趣
	 */
	public static final String SOCIAL_INTEREST_JOB_INVITATION = SERVER_ADDRESS
			+ "im/message/1/p2p/invitation/job/accept";
	
	/**
	 * 对职位邀请不感兴趣
	 */
	public static final String SOCIAL_UNINTEREST_JOB_INVITATION = SERVER_ADDRESS
			+ "im/message/1/p2p/invitation/job/ignore";
	
	/**
	 * 接受面试邀请
	 */
	public static final String SOCIAL_ACCEPT_INTERVIEW_INVITATION = SERVER_ADDRESS
			+ "im/message/1/p2p/interview/accept";
	
	/**
	 * 忽略面试邀请
	 */
	public static final String SOCIAL_IGNORE_INTERVIEW_INVITATION = SERVER_ADDRESS
			+ "im/message/1/p2p/interview/ignore";
	
	/**
	 * 聊一聊
	 */
	public static final String SOCIAL_CANDIDATE_CHAT = SERVER_ADDRESS
			+ "im/message/1/p2p/invitation/talk/send";
	
	/**
	 * 获取已读消息
	 */
	public static final String SOCIAL_GET_READ_STATUS = SERVER_ADDRESS
			+ "im/message/1/p2p/readstatus/get";
	
	/**
	 * 获取聊天框最新消息的请求
	 */
	public static final String SOCIAL_GET_NEW_MESSAGES = SERVER_ADDRESS
			+ "im/message/1/p2p/new/get";
	
	/**
	 * 获取群聊天框最新消息的请求
	 */
	public static final String SOCIAL_GET_NEW_GROUP_MESSAGES = SERVER_ADDRESS
			+ "im/message/1/group/new/get";
	
	/**
	 * 获取聊天框历史消息的请求
	 */
	public static final String SOCIAL_GET_OLD_MESSAGES = SERVER_ADDRESS
			+ "im/message/1/p2p/old/get";
	
	/**
	 * 获取群聊天框的历史消息的请求
	 */
	public static final String SOCIAL_GET_GROUP_OLD_MESSAGES = SERVER_ADDRESS
			+ "im/message/1/group/old/get";
	
	/**
	 * 获取收件箱消息摘要
	 */
	public static final String SOCIAL_GET_LETTER_SUMMARY = SERVER_ADDRESS
			+ "letter/1/summary/get";
	
	/**
	 * 获取消息摘要--p2p和群
	 */
	public static final String SOCIAL_GET_MESSAGE_SUMMARY = SERVER_ADDRESS
			+ "im/message/1/summary/get";

	
	/**
	 * 加入实名群（兴趣群）
	 */
	public static final String SOCIAL_JOIN_GROUP = SERVER_ADDRESS
			+ "group/1/join";
	
	/**
	 * 获取用户加入的投递群列表
	 */
	public static final String SOCIAL_GET_NICK_GROUP_LIST = SERVER_ADDRESS
			+ "group/1/list/apply/get";
	
	/**
	 * 获取用户加入的实名群列表
	 */
	public static final String SOCIAL_GET_REAL_GROUP_LIST = SERVER_ADDRESS
			+ "group/1/list/real/get";
	
	/**
	 * 
	 */
	public static final String SOCIAL_BLACK_SOMEBODY = SERVER_ADDRESS
			+ "relation/blacklist/1/add";
	
	/**
	 * 退出登录
	 */
	public static final String SOCIAL_LOGOUT = SERVER_ADDRESS
			+ "user/passport/1/logout";
	
	/**
	 * 退出群
	 */
	public static final String SOCIAL_QUIT_GROUP = SERVER_ADDRESS
			+ "group/1/quit";
	
	/**
	 * 设置消息免打扰
	 */
	public static final String SOCIAL_BLOCK_GROUP = SERVER_ADDRESS
			+ "group/1/block";
	
	/**
	 * 解除设置消息免打扰
	 */
	public static final String SOCIAL_UNBLOCK_GROUP = SERVER_ADDRESS
			+ "group/1/unblock";
	
	/**
	 * 获取群资料
	 */
	public static final String SOCIAL_GET_GROUP_INFO = SERVER_ADDRESS
			+ "group/1/info/get";
	
	/**
	 * 获取联系人用户的头像和名字
	 */
	public static final String SOCIAL_GET_PERSON_AVATAR = SERVER_ADDRESS
			+ "im/dialogue/1/userinfo/get";
	
	/**
	 * 获取群组的头像和名字
	 */
	public static final String SOCIAL_GET_GROUP_AVATAR = SERVER_ADDRESS
			+ "group/1/basic/get";
	
	/**
	 * 发送群文本消息
	 */
	public static final String SOCIAL_SEND_GROUP_TEXT = SERVER_ADDRESS
			+ "im/message/1/group/text/send";
	
	/**
	 * 获取群推荐列表
	 */
	public static final String SOCIAL_GET_GROUP_RECOMMEND = SERVER_ADDRESS
			+ "letter/1/group/recommend/list/get";
	
	/**
	 * 获取职位推荐列表
	 */
	public static final String SOCIAL_GET_POSITION_RECOMMEND = SERVER_ADDRESS
			+ "letter/1/job/recommend/list/get";
	// hr setting
	
	/**
	 * 获取当前用户招聘服务信息
	 */
	public static final String HR_SETTING_GET_RECRUIT = SERVER_ADDRESS
			+ "user/profile/1/hr/completion/get";
	
	/**
	 * 开启和关闭招聘服务
	 */
	public static final String HR_SETTING_SET_RECRUIT = SERVER_ADDRESS
			+ "user/profile/1/hr/status/set";
	
	
//	//TODO 以下接口还没有
//	/**
//	 * 重置招聘服务信息
//	 */
//	public static final String HR_SETTING_RESET_RECRUIT_SERVICE_SETTING = SERVER_ADDRESS
//			+"dict/corp/1/scale/get";
	
	
	//////////////////////////
	/**
	 * 获取职位最近面试通知信息
	 */
	public static final String HR_GET_INTERVIEW_TEMP = SERVER_ADDRESS
			+ "job/apply/1/interview/get";
	
	/**
	 * 发送面试通知
	 */
	public static final String HR_SEND_INTERVIEW = SERVER_ADDRESS
			+ "job/apply/1/interview/send";
	
	
	/// 
	
	//设置
	
	/**
	 * 找回密码获取验证码
	 */
	public static final String SETTING_PASSWORD_SEND_VERCODE = SERVER_ADDRESS
			+ "user/passport/1/passwd/find/verifycode/send";
	
	/**
	 * 更改密码
	 */
	public static final String SETTING_PASSWORD_SET = SERVER_ADDRESS
			+ "user/passport/1/passwd/change";
	
	/**
	 * 举报原因
	 */
	public static final String POSITION_GET_OFFICIER_REPORT = SERVER_ADDRESS
			+ "dict/user/1/offencereportreason/get";
	
	/**
	 * 举报用户
	 */
	public static final String POSITION_ADD_OFFENCEREPORT = SERVER_ADDRESS
			+ "user/profile/1/offencereport/add";
	
	/**
	 * 求带走
	 */
	public static final String SIGN_SELF = SERVER_ADDRESS
			+ "user/profile/1/sellself/set";
	
	/**
	 * 自我介绍
	 */
	public static final String SIGN_INTRODUCE= SERVER_ADDRESS
			+ "user/profile/1/introduce/set";
	
	/**
	 * 更改手机号时获取验证码
	 */
	public static final String SETTING_MOBILE_SEND_VERCODE = SERVER_ADDRESS
			+ "user/passport/1/mobile/change/verifycode/send";
	
	/**
	 * 更改手机号时填写注册码后确定
	 */
	public static final String SETTING_MOBILE_SET = SERVER_ADDRESS
			+ "user/passport/1/mobile/change/new/set";
	
	/**
	 * 找回密码填写验证码后设置新密码
	 */
	public static final String SETTING_PASSWORD_RESET = SERVER_ADDRESS
			+ "user/passport/1/passwd/find/new/set";
	
	
	/**
	 * 获取“招人才”的人才列表
	 */
	public static final String LOOK_SEEKERS = SERVER_ADDRESS
			+ "user/list/1/hot/get";
	
	/**
	 * 获取当前的热门职位
	 */
	public static final String GET_SUGGESTED_POSITION = SERVER_ADDRESS
			+ "dict/position/1/suggest/get";
	
	/**
	 * 获得当前职位
	 */
	public static final String GET_SEARCH_JOB = SERVER_ADDRESS
			+ "job/list/1/search/get";
	
	/**
	 * 获得当前人才
	 */
	public static final String GET_SEARCH_TALENT = SERVER_ADDRESS
			+ "user/list/1/search/get";
}
