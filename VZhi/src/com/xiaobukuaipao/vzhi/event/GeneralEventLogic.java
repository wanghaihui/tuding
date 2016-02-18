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
import com.xiaobukuaipao.vzhi.volley.SubMutiPartRequest;
import com.xiaobukuaipao.vzhi.volley.multipart.MultipartEntity;

public class GeneralEventLogic extends BaseEventLogic {

	// 注册
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * 获取验证码
	 * 
	 * @param mobile
	 *            电话号码
	 */
	public void getVeryCode(String mobile) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("mobile", mobile);
		InfoResultRequest infoResultRequest = new InfoResultRequest(
				R.id.register_get_code, ApiConstants.REGISTER_CODE_GET,
				Method.POST, map, null, new ResultParser(), this);
		sendRequest(infoResultRequest, R.id.register_get_code);
	}

	/**
	 * 验证 验证码
	 * 
	 * @param mobile
	 *            电话号码
	 * @param verifycode
	 *            验证码
	 */
	public void sendVeryCode(String mobile, String verifycode) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("mobile", mobile);
		map.put("verifycode", verifycode);
		InfoResultRequest infoResultRequest = new InfoResultRequest(
				R.id.register_ver_code, ApiConstants.REGISTER_CODE_VERIFY,
				Method.POST, map, null, new ResultParser(), this);
		sendRequest(infoResultRequest, R.id.register_ver_code);
	}

	/**
	 * 设置密码
	 * 
	 * @param password
	 */
	public void setPassword(String passwd) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("passwd", passwd);
		InfoResultRequest infoResultRequest = new InfoResultRequest(
				R.id.register_set_psw, ApiConstants.REGISTER_PSWD_SET,
				Method.POST, map, null, new ResultParser(), this);
		sendRequest(infoResultRequest, R.id.register_set_psw);
	}

	/**
	 * 设置身份
	 * 
	 * @param identity
	 *            身份(0:求职者 1:招聘者)
	 */
	public void setIdentity(int identity) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("identity", identity + "");
		InfoResultRequest infoResultRequest = new InfoResultRequest(
				R.id.register_set_iden, ApiConstants.REGISTER_IDEN_SET,
				Method.POST, map, null, new ResultParser(), this);
		sendRequest(infoResultRequest, R.id.register_set_iden);
	}

	/**
	 * 获取头像列表
	 */
	public void getAvatars() {
		InfoResultRequest infoResultRequest = new InfoResultRequest(
				R.id.register_get_avatar, ApiConstants.REGISTER_AVATARS_GET,
				null, new ResultParser(), this);
		sendRequest(infoResultRequest, R.id.register_get_avatar);
	}

	/**
	 * 设置基本信息
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
	public void setBasicInfo(String avatar, String nickname, int gender,
			int age, String city) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("avatar", avatar);
		map.put("nickname", nickname);
		map.put("gender", gender + "");
		map.put("age", age + "");
		map.put("city", city);

		InfoResultRequest infoResultRequest = new InfoResultRequest(
				R.id.register_set_base, ApiConstants.REGISTER_BASIC_SET,
				Method.POST, map, null, new ResultParser(), this);

		sendRequest(infoResultRequest, R.id.register_set_base);
	}

	/**
	 * 上传虚拟头像
	 * 
	 * @param avatar
	 * @param filePath
	 */
	public void uploadAvatar(String filePath) {

		SubMutiPartRequest subMutiPartRequest = new SubMutiPartRequest(
				R.id.register_upload_avatar, Method.POST,
				ApiConstants.UPLOAD_NICK_AVATAR, new ResultParser(), this,
				new MultipartEntity());
		subMutiPartRequest.addFile("name", "nickavatar");
		subMutiPartRequest.addFile("filename", filePath);

		sendRequest(subMutiPartRequest, R.id.register_upload_avatar);
	}

	/**
	 * 上传真实头像
	 * 
	 * @param avatar
	 * @param filePath
	 */
	public void uploadRealAvatar(String filePath) {
		SubMutiPartRequest subMutiPartRequest = new SubMutiPartRequest(
				R.id.register_upload_realavatar, Method.POST,
				ApiConstants.UPLOAD_REAL_AVATAR, new ResultParser(), this,
				new MultipartEntity());
		subMutiPartRequest.addFile("name", "realavatar");
		subMutiPartRequest.addFile("filename", filePath);

		sendRequest(subMutiPartRequest, R.id.register_upload_realavatar);
	}

	/**
	 * 上传真实头像
	 * 
	 * @param logo
	 * @param filePath
	 */
	public void uploadCorpLogo(String filePath) {

		SubMutiPartRequest subMutiPartRequest = new SubMutiPartRequest(
				R.id.register_upload_corp_logo, Method.POST,
				ApiConstants.REGISTER_CORP_LOGO_UPLOAD, new ResultParser(),
				this, new MultipartEntity());
		subMutiPartRequest.addFile("name", "logo");
		subMutiPartRequest.addFile("filename", filePath);

		sendRequest(subMutiPartRequest, R.id.register_upload_corp_logo);
	}

	/**
	 * 得到行业信息
	 */
	public void getIndustries() {
		InfoResultRequest infoResultRequest = new InfoResultRequest(
				R.id.register_get_industry,
				ApiConstants.REGISTER_POSITION_INDUSTRY, null,
				new ResultParser(), this);
		sendRequest(infoResultRequest, R.id.register_get_industry);
	}

	/**
	 * 得到公司类型
	 */
	public void getCorpTypes() {
		InfoResultRequest infoResultRequest = new InfoResultRequest(
				R.id.register_get_corp_type,
				ApiConstants.REGISTER_POSITION_CORP_TYPE, null,
				new ResultParser(), this);
		sendRequest(infoResultRequest, R.id.register_get_corp_type);
	}

	/**
	 * 求职意愿中的公司类型列表
	 */
	public void getIntentionCorpTypes() {
		InfoResultRequest infoResultRequest = new InfoResultRequest(
				R.id.register_get_intention_corp_type,
				ApiConstants.REGISTER_INTENTION_CORP_TYPE, null,
				new ResultParser(), this);
		sendRequest(infoResultRequest, R.id.register_get_intention_corp_type);
	}

	/**
	 * 得到公司规模
	 */
	public void getCorpScale() {
		InfoResultRequest infoResultRequest = new InfoResultRequest(
				R.id.register_get_corp_scale, ApiConstants.CORP_SCALES_GET,
				null, new ResultParser(), this);
		sendRequest(infoResultRequest, R.id.register_get_corp_scale);
	}

	/**
	 * 得到公司福利字典
	 */
	public void getCorpBenifits(String benefit) {
		String param = "";
		if (StringUtils.isNotEmpty(benefit)) {
			param = "?benefit=" + Uri.encode(benefit);
		}
		InfoResultRequest infoResultRequest = new InfoResultRequest(
				R.id.register_get_corp_benifits,
				ApiConstants.REGISTER_CORP_BENIFITS_GET + param, null,
				new ResultParser(), this);
		sendRequest(infoResultRequest, R.id.register_get_corp_benifits);
	}

	/**
	 * 得到求职心态
	 */
	public void getJobSeekerStates() {
		InfoResultRequest infoResultRequest = new InfoResultRequest(
				R.id.register_get_jobstates,
				ApiConstants.REGISTER_POSITION_JOBSTATES, null,
				new ResultParser(), this);
		sendRequest(infoResultRequest, R.id.register_get_jobstates);
	}

	/**
	 * 得到意向岗位
	 */
	public void getIntentPosition(String search) {
		InfoResultRequest infoResultRequest = new InfoResultRequest(
				R.id.register_get_intent_pos,
				ApiConstants.REGISTER_POSITION_INTENT_POS + "?" + "position="
						+ search, Method.GET, null, null, new ResultParser(),
				this);

		sendRequest(infoResultRequest, R.id.register_get_intent_pos);
	}

	/**
	 * @param industry
	 * @param intentPos
	 * @param salary
	 * @param cortType
	 * @param intentCity
	 * @param jobState
	 */
	public void setIntentJobInfo(String industry, String intentPos, int salary,
			String cortType, String intentCity, String jobState) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("industry", industry);
		params.put("positions", intentPos);
		params.put("minsalary", salary + "");
		params.put("corptypes", cortType);
		params.put("state", jobState);
		params.put("cities", intentCity);

		InfoResultRequest infoResultRequest = new InfoResultRequest(
				R.id.register_set_intent_job,
				ApiConstants.REGISTER_POSITION_INTENT_jOB, Method.POST, params,
				null, new ResultParser(), this);
		sendRequest(infoResultRequest, R.id.register_set_intent_job);
	}

	/**
	 * 获得公司的详细信息
	 * 
	 * @param corp
	 */
	public void getCorpInfo(String corp) {
		InfoResultRequest infoResultRequest = new InfoResultRequest(
				R.id.register_get_corp, ApiConstants.REGISTER_CORP_GET
						+ "?corp=" + corp, null, new ResultParser(), this);
		sendRequest(infoResultRequest, R.id.register_get_corp);
	}

	/**
	 * 获得公司的职位信息
	 * 
	 * @param corp
	 */
	public void getCorpPositions(String corp, int minrefreshid) {

		InfoResultRequest infoResultRequest = new InfoResultRequest(
				R.id.register_get_corp_position,
				ApiConstants.REGISTER_CORP_POSITIONS_GET + "?corp=" + corp
						+ "&minrefreshid=" + minrefreshid, null,
				new ResultParser(), this);
		sendRequest(infoResultRequest, R.id.register_get_corp_position);
	}

	/**
	 * 设置公司信息
	 * 
	 * @param corp
	 * @param name
	 * @param type
	 * @param logo
	 * @param city
	 * @param industry
	 * @param scale
	 * @param benefits
	 * @param website
	 * @param desc
	 */
	public void setCorpInfo(String corp, String name, Integer type,
			String logo, String city, String address, String industry, Integer scale,
			String benefits, String website, String desc) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("corp", corp);
		params.put("name", name);
		if (type != -1) {
			params.put("type", type + "");
		}
		params.put("logo", logo);
		params.put("city", city);
		params.put("address", address);
		if (scale != -1) {
			params.put("scale", scale + "");
		}
		params.put("industry", industry);
		params.put("benefits", benefits);
		params.put("website", website);
		params.put("desc", desc);

		InfoResultRequest infoResultRequest = new InfoResultRequest(
				R.id.register_set_corp, ApiConstants.REGISTER_HR_CORP_SET,
				Method.POST, params, null, new ResultParser(), this);
		sendRequest(infoResultRequest, R.id.register_set_corp);
	}

	/**
	 * 设置hr的基本信息
	 */
	public void setHrBasicInfo(String avatar, String realname, String gender,
			String age, String city) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("avatar", avatar);
		params.put("realname", realname);
		params.put("gender", gender);
		params.put("age", age);
		params.put("city", city);

		InfoResultRequest infoResultRequest = new InfoResultRequest(
				R.id.register_set_hr_basicinfo,
				ApiConstants.REGISTER_HR_BASIC_SET, Method.POST, params, null,
				new ResultParser(), this);
		sendRequest(infoResultRequest, R.id.register_set_hr_basicinfo);
	}

	/**
	 * 设置HR邮箱
	 */
	public void checkHrCorpEmail(String email) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("email", email);
		InfoResultRequest infoResultRequest = new InfoResultRequest(
				R.id.register_check_hr_corp_email,
				ApiConstants.REGISTER_HR_EMAIL_CHECK, Method.POST, params,
				null, new ResultParser(), this);
		sendRequest(infoResultRequest, R.id.register_check_hr_corp_email);
	}

	/**
	 * 按邮箱域名获取对应公司信息
	 */
	public void getHrCorpEmail(String emaildomain) {
		String params = "";
		if (StringUtils.isNotEmpty(emaildomain)) {
			params = "?emaildomain=" + emaildomain;
		}
		Logcat.d("@@@", params);
		InfoResultRequest infoResultRequest = new InfoResultRequest(
				R.id.register_get_email_corpsrecl,
				ApiConstants.REGISTER_EMAIL_CORPSREC_GET + params, null,
				new ResultParser(), this);
		sendRequest(infoResultRequest, R.id.register_get_email_corpsrecl);
	}

	/**
	 * 给hr发送验证邮件
	 * 
	 * @param corp
	 * @param email
	 */
	public void sendHrVerifyEmail(String corp, String email) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("corp", corp);
		params.put("email", email);

		InfoResultRequest infoResultRequest = new InfoResultRequest(
				R.id.register_send_hr_verify_email,
				ApiConstants.REGISTER_HR_VERIFY_EMAIL_SEND, Method.POST,
				params, null, new ResultParser(), this);
		sendRequest(infoResultRequest, R.id.register_send_hr_verify_email);
	}

	/**
	 * 設置hr工作經歷
	 * @param workexprid
	 * @param email
	 * @param corplname
	 * @param corpname
	 * @param position
	 * @param begindate
	 * @param endDate
	 * @param workdesc
	 */
	public void setHrRegisterWorkexp(String workexprid, String email,
			String corplname, String corpname, String position,
			String begindate, String endDate, String workdesc) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("workexprid", workexprid);
		params.put("email", email);
		params.put("corplname", corplname);
		params.put("corpname", corpname);
		params.put("position", position);
		params.put("begindate", begindate);
		params.put("endDate", endDate);
		params.put("workdesc", workdesc);

		InfoResultRequest infoResultRequest = new InfoResultRequest(
				R.id.register_set_hr_register_workexp,
				ApiConstants.REGISTER_HR_WORKEXP_SET, Method.POST, params,
				null, new ResultParser(), this);
		sendRequest(infoResultRequest, R.id.register_set_hr_register_workexp);
	}

	/**
	 * 大登录--用手机号、短信验证码或密码登录
	 */
	public void normalLogin(String mobile, String verifycode, String passwd) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("mobile", mobile);
		params.put("verifycode", verifycode);
		params.put("passwd", passwd);

		InfoResultRequest infoResultRequest = new InfoResultRequest(
				R.id.register_normal_login, ApiConstants.REGISTER_NORMAL_LOGIN,
				Method.POST, params, null, new ResultParser(), this);
		sendRequest(infoResultRequest, R.id.register_normal_login);
	}

	/**
	 * 小登录--刷新T票
	 */
	public void fastLogin() {
		Map<String, String> params = new HashMap<String, String>();

		InfoResultRequest infoResultRequest = new InfoResultRequest(
				R.id.register_fast_login, ApiConstants.REGISTER_FAST_LOGIN,
				Method.POST, params, null, new ResultParser(), this);
		sendRequest(infoResultRequest, R.id.register_fast_login);
	}

	/**
	 * 获取求职者（白领）用户注册时，基本信息是否完整
	 */
	public void getRegisterCompletion() {

		InfoResultRequest infoResultRequest = new InfoResultRequest(
				R.id.register_completion_get,
				ApiConstants.REGISTER_BASIC_COMPLETION, null,
				new ResultParser(), this);
		sendRequest(infoResultRequest, R.id.register_completion_get);
	}

	public void cancel(Object tag) {
		cancelAll(tag);
	}
	
	// 个人profile
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
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
	public void getProfileOhterInfo(String ownerid) {
		InfoResultRequest infoResultRequest = new InfoResultRequest(
				R.id.profile_other_info, ApiConstants.PROFILE_PERSONAL_PROFILE
						+ "?" + "ownerid=" + ownerid, new ResultParser(), this);
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
	public void setBasicInfo(String realavatar, String nickavatar,
			String realname, String nickname, Integer gender, Integer age,
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
	public void getTechTag(String skills){
		String params = "";
		if(StringUtils.isNotEmpty(skills)){
			params = "?skill=" + Uri.encode(skills);
		}
		InfoResultRequest infoResultRequest = new InfoResultRequest(R.id.profile_tag_get,ApiConstants.PROFILE_TECHS_GET + params , null, new ResultParser(),this);
		sendRequest(infoResultRequest, R.id.profile_tag_get);
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
	
	// 聊天 IM Social
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////
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
		
		InfoResultRequest infoResultRequest = new InfoResultRequest(R.id.social_stranger_send_greeting,ApiConstants.STRANGER_GREETING_SEND,Method.POST, map,null,new ResultParser(), this);
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
