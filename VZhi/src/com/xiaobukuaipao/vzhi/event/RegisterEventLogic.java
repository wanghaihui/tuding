package com.xiaobukuaipao.vzhi.event;

import java.util.HashMap;
import java.util.Map;

import android.net.Uri;

import com.android.volley.Request.Method;
import com.xiaobukuaipao.vzhi.R;
import com.xiaobukuaipao.vzhi.event.parser.ResultParser;
import com.xiaobukuaipao.vzhi.util.StringUtils;
import com.xiaobukuaipao.vzhi.volley.InfoResultRequest;
import com.xiaobukuaipao.vzhi.volley.SubMutiPartRequest;
import com.xiaobukuaipao.vzhi.volley.multipart.MultipartEntity;

/**
 * 注册流程中的一些请求(获取验证码,设置信息等等...)
 * 
 * @author hongxin.bai
 * 
 */
public class RegisterEventLogic extends BaseEventLogic {

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
	public void getCorpBenifits(String benefit , int seq) {
		String param = "";
		if (StringUtils.isNotEmpty(benefit)) {
			param = "?benefit=" + Uri.encode(benefit);
		}
		InfoResultRequest infoResultRequest = new InfoResultRequest(seq,ApiConstants.REGISTER_CORP_BENIFITS_GET + param, null,
				new ResultParser(), this);
		sendRequest(infoResultRequest, seq);
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
				ApiConstants.REGISTER_POSITION_INTENT_POS + "?" + "position="+ Uri.encode(search), Method.GET, null, null, new ResultParser(),
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
			params = "?emaildomain=" + Uri.encode(emaildomain);
		}
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

}