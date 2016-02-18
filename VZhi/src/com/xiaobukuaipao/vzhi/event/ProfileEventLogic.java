package com.xiaobukuaipao.vzhi.event;

import java.util.HashMap;
import java.util.Map;

import android.net.Uri;

import com.android.volley.Request.Method;
import com.xiaobukuaipao.vzhi.R;
import com.xiaobukuaipao.vzhi.event.parser.ResultParser;
import com.xiaobukuaipao.vzhi.util.GlobalConstants;
import com.xiaobukuaipao.vzhi.util.Logcat;
import com.xiaobukuaipao.vzhi.util.StringUtils;
import com.xiaobukuaipao.vzhi.volley.InfoResultRequest;

public class ProfileEventLogic extends BaseEventLogic {
	/**
	 * 得到个人信息完成状态
	 */
	public void getCompletion() {
		InfoResultRequest infoResultRequest = new InfoResultRequest(R.id.post_resume_complete_status_get,
				ApiConstants.POST_RESUME_COMPLETE_STATUS_GET, null,
				new ResultParser(), this);
		sendRequest(infoResultRequest, R.id.post_resume_complete_status_get);
	}
	/**
	 * 得到当前登录用户的基本信息
	 */
	public void getUserBasicInfo() {
		InfoResultRequest infoResultRequest = new InfoResultRequest(
				R.id.profile_basic_info, ApiConstants.PROFILE_BASIC_INFO, null,
				new ResultParser(), this);
		sendRequest(infoResultRequest, R.id.profile_basic_info);
	}

	/**
	 * 获取用户的profile信息
	 * 
	 * @param ownerid
	 */
	public void requestPersonalProfileInfo(String ownerid) {
		InfoResultRequest infoResultRequest = new InfoResultRequest(
				R.id.personalProfileHttp, ApiConstants.PROFILE_PERSONAL_PROFILE
						+ "?" + "ownerid=" + ownerid, new ResultParser(), this);
		// 第二个参数可以不设置, 取消请求用
		sendRequest(infoResultRequest, R.id.personalProfileHttp);
	}

	/**
	 * @param ownerid
	 */
	public void getProfileOhterInfo(String ownerid ,String jid) {
		String params = "?" + "ownerid=" + ownerid;
		if(jid != null){
			params += "&jid=" + jid;
		}
		InfoResultRequest infoResultRequest = new InfoResultRequest(
				R.id.profile_other_info, ApiConstants.PROFILE_PERSONAL_PROFILE + params , new ResultParser(), this);
		sendRequest(infoResultRequest, R.id.profile_other_info);
	}
	/**
	 * 得到公司
	 */
	public void getCorps(String corp, int seq) {
		InfoResultRequest infoResultRequest = new InfoResultRequest(seq, ApiConstants.REGISTER_POSITION_CORP_GET+ "?corp=" + Uri.encode(corp), null,new ResultParser(), this);
		sendRequest(infoResultRequest, seq);
	}

	/**
	 * 得到公司全称
	 */
	public void getCorpsFullname(String corp, int seq) {
		InfoResultRequest infoResultRequest = new InfoResultRequest(seq, ApiConstants.REGISTER_HR_FULLCORPNAMES_GET + "?corp=" + Uri.encode(corp), null,
				new ResultParser(), this);
		sendRequest(infoResultRequest, seq);
	}
	/**
	 * 设置基本信息
	 */
	public void setBasicInfo(String realavatar, String nickavatar, String realname, String nickname, Integer gender, Integer age,
			String city, Integer expryear, String email, String mobile,Integer identity) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("realavatar", realavatar);
		map.put("nickavatar", nickavatar);
		map.put("realname", realname);
		map.put("nickname", nickname);
		if(gender != -1)
			map.put("gender", String.valueOf(gender));
		if(age != -1)
			map.put("age", String.valueOf(age));
		map.put("city", city);
		if(expryear != -1)
			map.put("expryear",String.valueOf(expryear));
		map.put("email", email);
		map.put("mobile", mobile);
		if(identity != -1)
			map.put("identity", String.valueOf(identity));
		
		Logcat.d("@@@", ApiConstants.PROFILE_BASICINFO_SET);
		
		InfoResultRequest infoResultRequest = new InfoResultRequest(R.id.profile_basic_info_set, ApiConstants.PROFILE_BASICINFO_SET, Method.POST,  map,null,new ResultParser(), this);
		// 第二个参数可以不设置, 取消请求用
		sendRequest(infoResultRequest, R.id.profile_basic_info_set);
	}

	
	/**
	 *添加工作经历
	 */
	public void addExperience(String corp,String position,String begindate,String enddate,String salary,String workdesc) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("corp", corp);
		map.put("position", position);
		map.put("begindate", begindate);
		map.put("enddate", enddate);
		map.put("salary", salary);
		map.put("workdesc", workdesc);
		
		InfoResultRequest infoResultRequest = new InfoResultRequest(R.id.profile_workexpr_add, ApiConstants.PROFILE_WORKEXPR_ADD, Method.POST,  map,null,new ResultParser(), this);
		// 第二个参数可以不设置, 取消请求用
		sendRequest(infoResultRequest, R.id.profile_workexpr_add);
	}
	
	/**
	 * 修改工作经历
	 */
	public void modifyExperience(String id, String corp,String position,String begindate,String enddate,String salary,String workdesc) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("workexprid", id);
		map.put("corp", corp);
		map.put("position", position);
		map.put("begindate", begindate);
		map.put("enddate", enddate);
		map.put("salary", salary);
		map.put("workdesc", workdesc);
		
		InfoResultRequest infoResultRequest = new InfoResultRequest(R.id.profile_workexpr_modify, 
				ApiConstants.PROFILE_WORKEXPR_MODIFY, Method.POST,  map,null,new ResultParser(), this);
		// 第二个参数可以不设置, 取消请求用
		sendRequest(infoResultRequest, R.id.profile_workexpr_modify);
	}
	
	/**
	 * 删除工作经历
	 */
	public void deleteExperience(String id) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("workexprid", id);
		
		InfoResultRequest infoResultRequest = new InfoResultRequest(R.id.profile_workexpr_delete, 
				ApiConstants.PROFILE_WORKEXPR_DELETE, Method.POST,  map,null,new ResultParser(), this);
		// 第二个参数可以不设置, 取消请求用
		sendRequest(infoResultRequest, R.id.profile_workexpr_delete);
	}
	
	
	/**
	 *添加教育经历
	 */
	public void addEducation(String school,String major,String begindate,String enddate,String degree) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("school", school);
		map.put("major", major);
		map.put("begindate", begindate);
		map.put("enddate", enddate);
		map.put("degree", degree);
		
		InfoResultRequest infoResultRequest = new InfoResultRequest(R.id.profile_edu_add, ApiConstants.PROFILE_EDUCATION_ADD, Method.POST,  map,null,new ResultParser(), this);
		// 第二个参数可以不设置, 取消请求用
		sendRequest(infoResultRequest, R.id.profile_edu_add);
	}
	
	/**
	 * 修改教育经历
	 */
	public void modifyEducation(String id, String school,String major,String begindate,String enddate,String degree) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("eduexprid", id);
		map.put("school", school);
		map.put("major", major);
		map.put("begindate", begindate);
		map.put("enddate", enddate);
		map.put("degree", degree);
		
		InfoResultRequest infoResultRequest = new InfoResultRequest(R.id.profile_edu_modify, ApiConstants.PROFILE_EDUCATION_MODIFY, Method.POST,  map,null,new ResultParser(), this);
		// 第二个参数可以不设置, 取消请求用
		sendRequest(infoResultRequest, R.id.profile_edu_modify);
	}
	
	/**
	 * 删除教育经历
	 */
	public void deleteEducation(String id) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("eduexprid", id);
		
		InfoResultRequest infoResultRequest = new InfoResultRequest(R.id.profile_edu_delete, 
				ApiConstants.PROFILE_EDU_DELETE, Method.POST,  map,null,new ResultParser(), this);
		// 第二个参数可以不设置, 取消请求用
		sendRequest(infoResultRequest, R.id.profile_edu_delete);
	}
	
	/**
	 * 得到学历
	 */
	public void getDegree() {
		InfoResultRequest infoResultRequest = new InfoResultRequest(
				R.id.register_get_degree, ApiConstants.REGISTER_DEGREE_GET,
				null, new ResultParser(), this);
		sendRequest(infoResultRequest, R.id.register_get_degree);
	}

	/**
	 * 得到学校
	 */
	public void getSchools(String school, int seq) {
		InfoResultRequest infoResultRequest = new InfoResultRequest(seq, ApiConstants.REGISTER_SCHOOL_GET
						+ "?school=" + Uri.encode(school), null,new ResultParser(), this);
		sendRequest(infoResultRequest, seq);
	}

	/**
	 * 得到专业
	 */
	public void getMajors(String major , int seq) {
		InfoResultRequest infoResultRequest = new InfoResultRequest(seq, ApiConstants.REGISTER_MAJOR_GET
						+ "?major=" + Uri.encode(major), null,
				new ResultParser(), this);
		sendRequest(infoResultRequest, seq);
	}

	/**
	 * 得到职位
	 */
	public void getPositions(String position , int seq) {
		InfoResultRequest infoResultRequest = new InfoResultRequest(seq, ApiConstants.REGISTER_POSITION_GET + "?position=" + Uri.encode(position), null,
				new ResultParser(), this);
		sendRequest(infoResultRequest, seq);
	}

	/**
	 * 得到基本信息
	 */
	public void getBasicinfo() {
		InfoResultRequest infoResultRequest = new InfoResultRequest(
				R.id.register_get_basicinfo,
				ApiConstants.REGISTER_BASICINFO_GET, null, new ResultParser(),
				this);
		sendRequest(infoResultRequest, R.id.register_get_experiences);
	}

	/**
	 * 得到工作经历
	 */
	public void getExperiences() {
		InfoResultRequest infoResultRequest = new InfoResultRequest(
				R.id.register_get_experiences,
				ApiConstants.REGISTER_EXPERIENCES_GET, null,
				new ResultParser(), this);
		sendRequest(infoResultRequest, R.id.register_get_experiences);
	}

	/**
	 * 得到教育经历
	 */
	public void getEducations() {
		InfoResultRequest infoResultRequest = new InfoResultRequest(
				R.id.register_get_educations,
				ApiConstants.REGISTER_EDUCATIONS_GET, null, new ResultParser(),
				this);
		sendRequest(infoResultRequest, R.id.register_get_educations);
	}
	
	/**
	 * 设置当前用户的技能
	 * @param tags
	 */
	public void setMyTag(String[] tags){
		Map<String,String> map = new HashMap<String,String>();
		StringBuilder sb = new StringBuilder();
		for(String s : tags){
			sb.append(s);
			sb.append(",");
		}
		if(sb.length() > 0){
			map.put(GlobalConstants.JSON_SKILLS, sb.substring(0,sb.length() - 1));
		}else{
			map.put(GlobalConstants.JSON_SKILLS, "");
		}
		InfoResultRequest infoResultRequest = new InfoResultRequest(R.id.profile_mytag_set, ApiConstants.PROFILE_TECHS_SET, Method.POST, map,null, new ResultParser(),this);
		sendRequest(infoResultRequest, R.id.profile_mytag_set);
	}
	
	/**
	 * 得到当前用户的技能
	 */
	public void getMyTag(){
		InfoResultRequest infoResultRequest = new InfoResultRequest(R.id.profile_mytag_get,ApiConstants.PROFILE_TECH_GET, null, new ResultParser(),this);
		sendRequest(infoResultRequest, R.id.profile_mytag_get);
	}
	/**
	 * 得到技能标签
	 */
	public void getTechTag(String skills, int seq){
		String params = "";
		if(StringUtils.isNotEmpty(skills)){
			params = "?skill=" + Uri.encode(skills);
		}
		InfoResultRequest infoResultRequest = new InfoResultRequest(seq,ApiConstants.PROFILE_TECHS_GET + params , null, new ResultParser(),this);
		sendRequest(infoResultRequest, seq);
	}
	
	
	/**
	 * 获得匿名卡片
	 *  @param ownerid
	 */
	public void getAnoCard(String ownerid) {
		InfoResultRequest infoResultRequest = new InfoResultRequest(R.id.ano_card_get,ApiConstants.POST_RESUME_ANO_CARD_GET+(StringUtils.isEmpty(ownerid)? "" : ("?ownerid="+ownerid)),new ResultParser(), this);
		// 第二个参数可以不设置, 取消请求用
		sendRequest(infoResultRequest, R.id.ano_card_get);
	}
	
	/**
	 * 我投递的列表
	 * 
	 * @param minrefreshid #为0表示本页是最后一页
	 */
	public void getMyPosted(int minapplyid){
		
		Logcat.d("@@@", ApiConstants.POSITION_MY_SENDED + "?minapplyid=" + minapplyid);
		
		InfoResultRequest infoResultRequest = new InfoResultRequest(R.id.profile_position_posted, 
				ApiConstants.POSITION_MY_SENDED + "?minapplyid=" + minapplyid,null,new ResultParser(), this);
		// 第二个参数可以不设置, 取消请求用
		sendRequest(infoResultRequest, R.id.profile_position_posted);
	}
	
	/**
	 * 获取招聘服务状态
	 */
	public void getRecruitServiceStatus(){
		
		InfoResultRequest infoResultRequest = new InfoResultRequest(R.id.hr_setting_get_recruit_status, 
				ApiConstants.HR_SETTING_GET_RECRUIT,null,new ResultParser(), this);
		// 第二个参数可以不设置, 取消请求用
		sendRequest(infoResultRequest, R.id.hr_setting_get_recruit_status);
		
	}
	
	
	/**
	 * 设置招聘服务状态
	 */
	public void setRecruitServiceStatus(int status){
		
		Map<String, String> map = new HashMap<String, String>();
		map.put("status", status + "");
		InfoResultRequest infoResultRequest = new InfoResultRequest(R.id.hr_setting_set_recruit_status, 
				ApiConstants.HR_SETTING_SET_RECRUIT,Method.POST,map,null,new ResultParser(), this);
		// 第二个参数可以不设置, 取消请求用
		sendRequest(infoResultRequest, R.id.hr_setting_set_recruit_status);
		
	}
	
//	/**
//	 * 重置招聘服信息
//	 */
//	public void resetRecruitServiceSetting(){
//		InfoResultRequest infoResultRequest = new InfoResultRequest(R.id.hr_setting_reset_recruit_serivce_setting, 
//				ApiConstants.HR_SETTING_RESET_RECRUIT_SERVICE_SETTING,null,new ResultParser(), this);
//		// 第二个参数可以不设置, 取消请求用
//		sendRequest(infoResultRequest, R.id.hr_setting_reset_recruit_serivce_setting);
//		
//	}
	
	/**
	 * 获得隐私
	 */
	public void getPrivacy(){
		InfoResultRequest infoResultRequest = new InfoResultRequest(R.id.setting_privacy_get, 
				ApiConstants.PROFILE_PRIVACY_GET,null,new ResultParser(), this);
		// 第二个参数可以不设置, 取消请求用
		sendRequest(infoResultRequest, R.id.setting_privacy_get);
	}
	
	/**
	 * 获取个人资料隐私设置选项列表
	 */
	public void getPrivacyProfileList(){
		InfoResultRequest infoResultRequest = new InfoResultRequest(R.id.setting_privacy_profile_list_get, 
				ApiConstants.PROFILE_PRIVACY_PROFILE_LEVELS_GET,null,new ResultParser(), this);
		// 第二个参数可以不设置, 取消请求用
		sendRequest(infoResultRequest, R.id.setting_privacy_profile_list_get);
		
	}
	
	/**
	 * 获取联系方式隐私设置选项列表
	 */
	public void getPrivacyContactList(){
		InfoResultRequest infoResultRequest = new InfoResultRequest(R.id.setting_privacy_contact_list_get, 
				ApiConstants.PROFILE_PRIVACY_CONTACT_LEVELS_GET,null,new ResultParser(), this);
		// 第二个参数可以不设置, 取消请求用
		sendRequest(infoResultRequest, R.id.setting_privacy_contact_list_get);
	}
	
	/**
	 * 联系方式隐私设置
	 */
	public void setPrivacyContact(String level){
		Map<String, String> map = new HashMap<String, String>();
		map.put("level", level);
		InfoResultRequest infoResultRequest = new InfoResultRequest(R.id.setting_privacy_contact_set, 
				ApiConstants.PROFILE_PRIVACY_CONTACT_SET,Method.POST,map,null,new ResultParser(), this);
		// 第二个参数可以不设置, 取消请求用
		sendRequest(infoResultRequest, R.id.setting_privacy_contact_set);
	}
	
	/**
	 * 个人资料隐私设置
	 */
	public void setPrivacyProfile(String level){
		Map<String, String> map = new HashMap<String, String>();
		map.put("level", level);
		InfoResultRequest infoResultRequest = new InfoResultRequest(R.id.setting_privacy_profile_set, 
				ApiConstants.PROFILE_PRIVACY_PROFILE_SET,Method.POST,map,null,new ResultParser(), this);
		// 第二个参数可以不设置, 取消请求用
		sendRequest(infoResultRequest, R.id.setting_privacy_profile_set);
	}
	
	
	/**
	 * 从投递场景进入到求职者的profile， "点击查看"联系方式
	 * 
	 * @param ownerid
	 * @param jid
	 */
	public void readContact(String ownerid, String jid){
		Map<String,String> map = new HashMap<String,String>();
		map.put("ownerid", ownerid);
		map.put("jid", jid);
		InfoResultRequest infoResultRequest = new InfoResultRequest(R.id.setting_hr_read_contact, ApiConstants.PROFILE_TIP_PERSON_CONTACTS,
				Method.POST,map,null,
				new ResultParser(), this);
		sendRequest(infoResultRequest, R.id.setting_hr_read_contact);
	}
	
	/**
	 * 获取HR基本信息
	 */
	public void getHrBasic(){
		InfoResultRequest infoResultRequest = new InfoResultRequest(R.id.register_get_hr_basicinfo, 
				ApiConstants.REGISTER_HR_BASIC_GET,null,new ResultParser(), this);
		sendRequest(infoResultRequest, R.id.register_get_hr_basicinfo);
	}
	
	/**
	 * 获取学历要求选项列表
	 */
	public void getJobEdus(){
		InfoResultRequest infoResultRequest = new InfoResultRequest(R.id.position_get_edus, 
				ApiConstants.POSITION_JOBEDUS_GET,null,new ResultParser(), this);
		sendRequest(infoResultRequest, R.id.position_get_edus);
	}
	
	/**
	 * 发布职位时获取工作经验要求选项列表
	 */
	public void getJobExprs(){
		InfoResultRequest infoResultRequest = new InfoResultRequest(R.id.position_get_exprs, ApiConstants.POSITION_JOBEXPRS_GET,null,new ResultParser(), this);
		sendRequest(infoResultRequest, R.id.position_get_exprs);
	}
	
	/**
	 * 找回密码获取验证码
	 */
	public void sendVercodeMobile(String mobile){
		Map<String, String> map = new HashMap<String, String>();
		map.put("mobile", mobile);
		InfoResultRequest infoResultRequest = new InfoResultRequest(R.id.setting_password_vercode_send, 
				ApiConstants.SETTING_PASSWORD_SEND_VERCODE,Method.POST,map,null,new ResultParser(), this);
		// 第二个参数可以不设置, 取消请求用
		sendRequest(infoResultRequest, R.id.setting_password_vercode_send);
	}

	/**
	 * 找回密码填写验证码后设置新密码
	 */
	public void setPswdWithVercode(String mobile, String passwd, String verifycode){
		Map<String, String> map = new HashMap<String, String>();
		map.put("mobile", mobile);
		map.put("passwd", passwd);
		map.put("verifycode", verifycode);
		InfoResultRequest infoResultRequest = new InfoResultRequest(R.id.setting_password_set, 
				ApiConstants.SETTING_PASSWORD_RESET,Method.POST,map,null,new ResultParser(), this);
		// 第二个参数可以不设置, 取消请求用
		sendRequest(infoResultRequest, R.id.setting_password_set);
	}
	
	/**
	 * 更改手机号时获取验证码
	 */
	public void sendMobileVercode(String newmobile){
		Map<String, String> map = new HashMap<String, String>();
		map.put("newmobile", newmobile);
		InfoResultRequest infoResultRequest = new InfoResultRequest(R.id.setting_ver_code_for_mobile, 
				ApiConstants.SETTING_MOBILE_SEND_VERCODE,Method.POST,map,null,new ResultParser(), this);
		// 第二个参数可以不设置, 取消请求用
		sendRequest(infoResultRequest, R.id.setting_ver_code_for_mobile);
	}
	
	/**
	 * 更改手机号时填写注册码后确定
	 */
	public void setNewMobile(String newmobile, String passwd, String verifycode){
		Map<String, String> map = new HashMap<String, String>();
		map.put("newmobile", newmobile);
		map.put("passwd", passwd);
		map.put("verifycode", verifycode);
		InfoResultRequest infoResultRequest = new InfoResultRequest(R.id.setting_newmobile_set, 
				ApiConstants.SETTING_MOBILE_SET,Method.POST,map,null,new ResultParser(), this);
		// 第二个参数可以不设置, 取消请求用
		sendRequest(infoResultRequest, R.id.setting_newmobile_set);
	}
	
	/**
	 * 更改密码
	 */
	public void setPswdWithOldPswd(String passwd, String newpasswd){
		Map<String, String> map = new HashMap<String, String>();
		map.put("passwd", passwd);
		map.put("newpasswd", newpasswd);
		InfoResultRequest infoResultRequest = new InfoResultRequest(R.id.setting_password_set_with_oldpswd, 
				ApiConstants.SETTING_PASSWORD_SET,Method.POST,map,null,new ResultParser(), this);
		// 第二个参数可以不设置, 取消请求用
		sendRequest(infoResultRequest, R.id.setting_password_set_with_oldpswd);
	}
	
	public void cancel(Object tag){
		cancelAll(tag);
	}
	
	/**
	 * 举报原因
	 */
	public void getOfficierReport() {
		InfoResultRequest infoResultRequest = new InfoResultRequest(R.id.position_get_officier_report, 
				ApiConstants.POSITION_GET_OFFICIER_REPORT, null, new ResultParser(), this);
		sendRequest(infoResultRequest, R.id.position_get_officier_report);
	}
	
	/**
	 * 举报用户
	 */
	public void reportSomebody(String offenceuid, String reason, String desc) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("offenceuid", offenceuid);
		map.put("reason", reason);
		map.put("desc", desc);
		InfoResultRequest infoResultRequest = new InfoResultRequest(R.id.position_add_offencereport, 
				ApiConstants.POSITION_ADD_OFFENCEREPORT, Method.POST, map, null, new ResultParser(), this);
		// 第二个参数可以不设置, 取消请求用
		sendRequest(infoResultRequest, R.id.position_add_offencereport);
	}
	
	/**
	 * 求带走
	 */
	public void setSignself(String isreal, String words){
		Map<String, String> map = new HashMap<String, String>();
		map.put("isreal", isreal);
		map.put("words", words);
		InfoResultRequest infoResultRequest = new InfoResultRequest(R.id.profile_sign_self, 
				ApiConstants.SIGN_SELF, Method.POST, map, null, new ResultParser(), this);
		// 第二个参数可以不设置, 取消请求用
		sendRequest(infoResultRequest, R.id.profile_sign_self);
	}
	/**
	 * 个人介绍
	 */
	public void setIntroduce(String introduce){
		Map<String, String> map = new HashMap<String, String>();
		map.put("introduce", introduce);
		InfoResultRequest infoResultRequest = new InfoResultRequest(R.id.profile_sign_introduce, 
				ApiConstants.SIGN_INTRODUCE, Method.POST, map, null, new ResultParser(), this);
		// 第二个参数可以不设置, 取消请求用
		sendRequest(infoResultRequest, R.id.profile_sign_introduce);
	}
	
	
}
