package com.xiaobukuaipao.vzhi.util;


public class GlobalConstants {

	
	
	// 调用百度地图
	public static final String BAIDU_LOCATION__PREFIX_URL = "http://api.map.baidu.com/geocoder?location=";

	public static final String BAIDU_LOCATION__END_URL = "&output=json&key=A5oeGESXf8NI0mTCciA41GGP";
	
	public static final String URL_PATTERN = "(http):\\/\\/[0-9a-zA-Z_\\%\\&\\?\\.\\+\\$/\\=\\-]+";
	// public static final String URL_PATTERN = "^(http|www|ftp|)?(://)?(\\w+(-\\w+)*)(\\.(\\w+(-\\w+)*))*((:\\d+)?)(/(\\w+(-\\w+)*))*(\\.?(\\w)*)(\\?)?(((\\w*%)*(\\w*\\?)*(\\w*:)*(\\w*\\+)*(\\w*\\.)*(\\w*&)*(\\w*-)*(\\w*=)*(\\w*%)*(\\w*\\?)*(\\w*:)*(\\w*\\+)*(\\w*\\.)*(\\w*&)*(\\w*-)*(\\w*=)*)*(\\w*)*)$";
	
	/**
	 * sharedpreferences
	 */
	public static final String PREFERENCE_NAME = "job_booking_sp";
	public static final String PREFERENCE_APPLY_ONE_WORD = "one_word";
	public static final String PREFERENCE_APPLY_ONE_WORD_CHECK = "one_word_check";
	public static final String PREFERENCE_APPLY_REVIEW = "apply_review";
	public static final String PREFERENCE_FIRSTIN_GROUP = "first_in_group";
	public static final String PREFERENCE_MSG_SOUND = "msg_sound";
	public static final String PREFERENCE_MSG_VIBRATION = "msg_vibration";
	public static final String PREFERENCE_READ_MOBILE = "read_mobile";
	public static final String PREFERENCE_READ_EMAIL = "read_email";
	
	/****
	 * 用户注册的信息
	 */
	public static final String REGISTER_MOBILE = "register_mobile";
	public static final String TOKEN = "token";

	public static final String UID = "uid";
	public static final String DID = "did";
	public static final String NICKNAME = "nickname";

	public static final String DEFAULT_CITY = "北京";

	public static final int CAMERA_CAPTURE = 1;
	public static final int CAMERA_CAPTURE_REALAVATAR = 2;
	public static final int CAMERA_CAPTURE_NICKAVATAR = 3;

	public static final String CLIP_PHOTO = "clip_photo";
	public static final String CLIP_PHOTO_URL = "clip_photo_url";

	public static final String GUIDE_EDUCATION_OBJECT = "guide_education_object";
	public static final String GUIDE_EDUCATION_TIME = " ~ ";
	
	public static final String GUIDE_JOB_EXPERIENCE_OBJECT = "guide_job_experience_object";
	public static final String GUIDE_JOB_DEFAULT_TIME = " ~ ";

	public static final String SEQ_STRING = "seq_tring";
	
	// 应用程序的包名
	public static final String APPLICATION_PACKAGE_NAME = "com.xiaobukuaipao.vzhi";
	
	/**
	 * Parcelable
	 */
	public static final String PARCEL_USER_BASIC = "parcel_user_basic";
	/**
	 * Serializable
	 */
	public static final String SERIALIZABLE_USER_INTENTION = "serializable_user_intention";
	
	
	/**
	 * Baidu Push
	 */
	public static final String PUSH_KEY = "A5oeGESXf8NI0mTCciA41GGP";
	
	/**
	 * JSON字段
	 */
	public static final String JSON_CORP = "corp";
	public static final String JSON_PUBLISHER_JOBS = "publisherJobs";
	public static final String JSON_PUBLISHER = "publisher";
	public static final String JSON_JOBS = "jobs";
	public static final String JSON_JOB = "job";
	public static final String JSON_JOBEDUS = "jobedus";
	public static final String JSON_JOBEXPRS = "jobexprs";

	public static final String JSON_ID = "id";
	public static final String JSON_NAME = "name";
	public static final String JSON_SUBNAME = "subname";
	public static final String JSON_STATE = "state";
	public static final String JSON_LOGO = "logo";
	public static final String JSON_BENEFITS = "benefits";
	public static final String JSON_LNAME = "lname";
	public static final String JSON_SCALE = "scale";
	

	public static final String JSON_CORPLOGO = "corplogo";
	public static final String JSON_CORPID = "corpid";
	
	public static final String JSON_GENDER = "gender";
	public static final String JSON_AVATAR = "avatar";
	public static final String JSON_IDENTITY = "identity";
	public static final String JSON_CERTIFY = "certify";
	public static final String JSON_INTRODUCE = "introduce";
	public static final String JSON_TITLE = "title";
	public static final String JSON_SALARY = "salary";
	public static final String JSON_EXPR = "expr";
	public static final String JSON_EDU = "edu";
	public static final String JSON_CITY = "city";
	public static final String JSON_HIGHLIGHTS = "highlights";
	public static final String JSON_REFRESHTIME = "refreshtime";
	public static final String JSON_ATTRS = "attrs";

	public static final String JSON_DUTY = "duty";
	public static final String JSON_REQUIRE = "require";
	public static final String JSON_VIEWNUM = "viewnum";
	public static final String JSON_APPLYNUM = "applynum";
	public static final String JSON_READNUM = "readnum";
	
	public static final String JSON_HEADCOUNT = "headcount";

	public static final String JSON_DATA = "data";
	public static final String JSON_MINREFRESHID = "minrefreshid";
	public static final String JSON_MINAPPLYID = "minapplyid";
	public static final String JSON_NOMORE = "nomore";
	public static final String JSON_MINHOTID = "minhotid";
	
	public static final String JSON_USERPROFILE = "userprofile";
	public static final String JSON_BASIC = "basic";
	public static final String JSON_SKILLS = "skills";
	public static final String JSON_INTENTION = "intention";
	public static final String JSON_CONTACT = "contact";
	public static final String JSON_AGE = "age";
	public static final String JSON_INDUSTRY = "industry";
	public static final String JSON_CITIES = "cities";
	public static final String JSON_CORPTYPE = "corptypes";
	public static final String JSON_CORPBENIFITS = "corpbenefits";
	public static final String JSON_CONTACTSTATUS = "contactstatus";
	public static final String JSON_ISREAL = "isreal";
	public static final String JSON_WORDS = "words";
	public static final String JSON_MESSAGE = "message";
	
	public static final String JSON_CORPS = "corps";

	public static final String JSON_WORKEXPRID = "workexprid";
	public static final String JSON_UID = "uid";
	public static final String JSON_STATUS = "status";
	public static final String JSON_BEGIN = "begin";
	public static final String JSON_BEGINDATE = "begindate";
	public static final String JSON_EDUEXPRID = "eduexprid";
	public static final String JSON_ENDDATE = "enddate";
	public static final String JSON_END = "end";
	public static final String JSON_DESC = "desc";
	public static final String JSON_SCHOOL = "school";
	public static final String JSON_SCHOOLS = "schools";
	public static final String JSON_MAJOR = "major";
	public static final String JSON_MAJORS = "majors";
	public static final String JSON_DEGREE = "degree";
	public static final String JSON_DEGREES = "degrees";
	public static final String JSON_MOBILE = "mobile";
	public static final String JSON_EMAIL = "email";
	public static final String JSON_PUBLIC = "public";
	public static final String JSON_USERCARD = "usercard";
	
	public static final String JSON_INDUSTRIES = "industries";
	public static final String JSON_CORPTYPES = "corptypes";
	public static final String JSON_CORPSCALES= "corpscales";
	public static final String JSON_JOBSEEKERSTATES = "jobseekerstates";
	public static final String JSON_POSITIONS = "positions";

	public static final String JSON_EXPRYEAR = "expryear";
	public static final String JSON_NICKAVATAR = "nickavatar";
	public static final String JSON_NICKNAME = "nickname";
	public static final String JSON_REALAVATAR = "realavatar";
	public static final String JSON_REALNAME = "realname";
	public static final String JSON_PERSON = "person";
	
	public static final String JSON_USERBASIC = "userbasic";
	
	public static final String JSON_TYPE = "type";
	public static final String JSON_POSITION = "position";
	public static final String JSON_MINSALARY = "minsalary";
	
	public static final String JSON_COMPLETE = "complete";
	public static final String JSON_DETAIL = "detail";
	public static final String JSON_EDUEXPR = "eduexpr";
	public static final String JSON_EDUEXPRS="eduexprs";
	public static final String JSON_WORKEXPR = "workexpr";
	public static final String JSON_WORKEXPRS = "workexprs";
	
	public static final String JSON_YEAR = "year";
	public static final String JSON_MONTH = "month";
	
	public static final String JSON_ALLNUM = "allnum";
	public static final String JSON_UNREADNUM = "unreadnum";
	public static final String JSON_CONTACTCOUNT = "contactCount";
	
	public static final String JSON_APPLYSTATUS = "applystatus";
	
	public static final String JSON_APPLYJOB = "applyjob";
	public static final String JSON_APPLYTIME = "applytime";
	public static final String JSON_POSTSCRIPT = "postscript";
	
	public static final String JSON_CORPEMAIL="corpemail";
	
	
	
	public static final String JSON_HASAPPLIED = "hasapplied";
	public static final String JSON_BUDDYLIST = "buddylist";
	public static final String JSON_UNREADCOUNT = "unreadcount";
	
	public static final String JSON_ISANSWER = "isanswer";
	public static final String JSON_ISQUESTION = "isquestion";
	public static final String JSON_ISBUDDYINVITATION = "isbuddyinvitation";
	public static final String JSON_ISGREETING = "isgreeting";
	public static final String JSON_ISREPLAY = "isreply";
	public static final String JSON_HASREPLY = "hasreply";
	public static final String JSON_INVITATIONID = "invitationid";
	public static final String JSON_GREETINGID = "greetingid";
	public static final String JSON_HASACCEPT = "hasaccept";
	public static final String JSON_HASREFUSE = "hasrefuse";
	public static final String JSON_ANSWER = "answer";
	public static final String JSON_ANSWERID = "answerid";
	public static final String JSON_QUESTION = "question";
	public static final String JSON_QUESTIONID= "questionid";
	public static final String JSON_TIME = "time";
	public static final String JSON_SENDER = "sender";
	public static final String JSON_MINLETTERID = "minletterid";
	
	public static final String JSON_ISBUDDY = "isbuddy";
	
	public static final String JSON_WEBSITE = "website";
	
	public static final String JSON_SERVER = "server";
	
	public static final String JSON_BODY = "body";
	public static final String JSON_MID = "mid";
	public static final String JSON_DID = "did";
	public static final String JSON_CMID = "cmid";
	public static final String JSON_RECEIVER = "receiver";
	public static final String JSON_CREATETIME = "createtime";
	public static final String JSON_JID = "jid";
	
	public static final String JSON_RECRUIT = "recruit";
	
	public static final String JSON_INTERVIEW = "interview";
	public static final String JSON_HR = "hr";
	public static final String JSON_DATE = "date";
	public static final String JSON_ADDRESS = "address";
	
	public static final String JSON_CONTACTPRIVACY = "contactprivacy";
	public static final String JSON_PROFILEPRIVACY = "profileprivacy";
	
	public static final String JSON_READSTATUS = "readstatus";
	public static final String JSON_READTIME = "readtime";
	public static final String JSON_INTERVIEWTIME = "interviewtime";
	
	public static final String JSON_MINMSGID = "minmsgid";
	
	public static final String JSON_JOB_APPLY = "jobapply";
	public static final String JSON_STRANGER_LETTER = "strangerletter";
	public static final String JSON_JOB_RECOMMEND ="jobrecommend";
	public static final String JSON_GROUP_RECOMMEND = "grouprecommend";
	
	public static final  String JSON_MSG = "msg";
	
	public static final String JSON_P2P = "p2p";
	
	public static final String JSON_GROUP = "group";
	public static final String JSON_ENTITYID = "entityid";
	public static final String JSON_GID = "gid";
	public static final String JSON_MEMNUM = "memnum";
	public static final String JSON_HASBLOCK = "hasblock";
	public static final String JSON_MEMBERS = "members";
	public static final String JSON_ALLOWADD = "allowadd";
	
	public static final String JSON_SUMMARY = "summary";
	
	public static final String JSON_OFFENCE_REPORT_REASONS = "offencereportreasons";
	
	public static final String JSON_HASREAD = "hasread";
	
	public static final String JSON_EXPRBEGINDATE = "exprbegindate";
	public static final String JSON_EXPRENDDATE = "exprenddate";
	public static final String JSON_EDUBEGINDATE = "edubegindate";
	public static final String JSON_EDUENDDATE = "eduenddate";
	
	public static final String JSON_START = "start";
	
	/**
	 * Setting
	 */
	public static final String SETTING_ACCOUNT_MODIFY = "account_modify";
	public static final int SETTING_ACCOUNT_MODIFY_PHONE = 0;
	public static final int SETTING_ACCOUNT_MODIFY_PASSWORD = 1;

	/**
	 * Setting Fragment
	 */
	public static final int SETTING_MODIFY_MOBILE = 1;
	public static final int SETTING_VERIFY_MOBILE = 2;
	public static final int SETTING_MODIFY_PASSWORD = 3;
	
	
	/**
	 * Cookie
	 */
	public static final String SET_COOKIE_KEY = "Set-Cookie";
	public static final String COOKIE_KEY = "Cookie";
	public static final String COOKIE_UID = "uid";
	public static final String COOKIE_P = "p";
	public static final String COOKIE_T = "t";
	public static final String COOKIE_MOBILE = "mobile";
	public static final String COOKIE_DOMAIN = "Domain";
	public static final String COOKIE_PATH = "Path";
	public static final String COOKIE_EXPIRE = "Max-Age";
	
	
	/**
	 * Avatars
	 */
	public static final String JSON_AVATARS = "avatars";
	
	/**
	 * 拍照和照片选择入口
	 */
	public static final String PHOTO_DOOR = "photo_door";
	public static final String PHOTO_PICKER = "photo_picker";
	public static final String PHOTO_PROFILE = "photo_picker_profile";
	public static final String PHOTO_PROFILE_PICKER = "photo_profile_picker";
	public static final String PHOTO_MODIFY_PROFILE = "photo_modify_profile";
	public static final String PHOTO_WHITECOLLAR = "photo_whitecollar";
	public static final String PHOTO_HR = "photo_hr";
	
	/**
	 * avatar url
	 */
	public static final String URL_NICK_AVATAR = "nickavatar";
	public static final String URL_REAL_AVATAR = "realavatar";
	
	
	/**
	 * editjob
	 */
	public static final String JOB_INFO = "jobinfo";
	public static final String JOB_ID = "jobid";
	public static final String JOB_REVIEW = "jobreview";
	public static final String JOB_ONEWORD = "joboneword";
	public static final String JOB_HASAPPLIED = "jobhasapplied";
	public static final String CANDIDATE_VIEW = "candidateview";
	public static final String OWNER_ID = "ownerid";

	//activity intent params
	public static final String STRANGER_REPLY = "stranger_reply";
	
	public static final String CORP_ID = "copr_id";
	
	public static final String IDENTITY = "identity";
	
	public static final String INNER_URL = "url";
	
	public static final String ISREAL = "isreal";
	
	public static final String IS_CONTACTED = "is_contacted";
	
	public static final String CONTACT_STATUS = "contact_status";
	
	public static final String POSITION = "position";
	
	public static final String TITLE = "title";

	public static final String TYPE = "tyPe";
	
	public static final String ID = "id";
	
	public static final String NAME = "name";
	
	public static final String GID = "gid";
	
	public static final String PAGE = "page";
	
	public static final String RECRUIT_SETTING = "recruitSetting";
	
	public static final String RECRUIT_INFO_SETTING_INFO = "recruitInfoSettingInfo";
	
	public static final String RECRUIT_INFO_SETTING_RESET = "recruitInofSettingReset";
	
	public static final String RECRUIT_INFO_SETTING_VRFY = "recruitInofSettingVrfy";
	
	public static final String EMAIL = "email";
	
	public static final String FILL = "fill";
	
	
	
	
	
	//suggest table type
	
	
	
	public static final int SUGGEST_INTENTION_HOT_CITIES = 1001;
	
	public static final int SUGGEST_INTENTION_CORP_TYPE = 1002;
	
	public static final int SUGGEST_INTENTION_SEEKER_STAT = 1003;
	
	public static final int SUGGEST_DEGREE = 2001;
	
	public static final int SUGGEST_EXPR = 2002;
	
	
	//db name
	
	public static final String DBNAME = "tuding_db";
	
	// SharedPreferences
	public static final String INTENTION_COMPLETE = "intention_complete";
}
