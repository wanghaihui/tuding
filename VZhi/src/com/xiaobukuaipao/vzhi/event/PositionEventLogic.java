package com.xiaobukuaipao.vzhi.event;

import java.util.HashMap;
import java.util.Map;

import android.net.Uri;

import com.android.volley.Request.Method;
import com.xiaobukuaipao.vzhi.R;
import com.xiaobukuaipao.vzhi.event.parser.ResultParser;
import com.xiaobukuaipao.vzhi.volley.InfoResultRequest;

/**
 * 职位相关的业务逻辑
 * @author xiaobu
 *
 */
public class PositionEventLogic extends BaseEventLogic {
	
	/**
	 * 获取职位列表
	 * @param minrefreshid
	 */
	public void requestRecruitList(String minrefreshid) {
	
		InfoResultRequest infoResultRequest = new InfoResultRequest(R.id.recruitListHttp, ApiConstants.POSITION_RECRUIT_LIST + "?" + "minrefreshid=" + minrefreshid, 
				Method.GET,null, null, new ResultParser(), this);
		// 第二个参数可以不设置, 取消请求用
		sendRequest(infoResultRequest, R.id.recruitListHttp);
	}
	
	public void getAwardPositions(){
		
		InfoResultRequest infoResultRequest = new InfoResultRequest(R.id.position_get_award, ApiConstants.GET_AWARD_POSITIONS, 
				Method.GET,null, null, new ResultParser(), this);
		// 第二个参数可以不设置, 取消请求用
		sendRequest(infoResultRequest, R.id.position_get_award);
		
		
	}
	
	public void sendAwardCount(String jid){
		
		Map<String, String> map = new HashMap<String, String>();
		map.put("jid", jid);
		
		InfoResultRequest infoResultRequest = new InfoResultRequest(R.id.award_shared_count, 
				ApiConstants.AWARD_SHARED_COUNT, Method.POST, map, null, new ResultParser(), this);
		sendRequest(infoResultRequest, R.id.award_shared_count);
	}
	
	
	/**
	 * 获取我发布的职位列表
	 */
	public void getPublishedPositions(String minrefreshid) {
		
		Map<String, String> map = new HashMap<String, String>();
		map.put("minrefreshid", minrefreshid);
		
		InfoResultRequest infoResultRequest = new InfoResultRequest(R.id.position_get_published_positions, 
				ApiConstants.GET_POSITIONED_POSITIONS, Method.GET,  map,null,new ResultParser(), this);
		sendRequest(infoResultRequest, R.id.position_get_published_positions);
	}
	
	/**
	 * 获取职位信息
	 * 
	 * @param jid
	 */
	public void getPositionInfo(String jid){
		
		InfoResultRequest infoResultRequest = new InfoResultRequest(R.id.position_detail, 
				ApiConstants.POSITION_POSITION_INFO + "?jid=" + jid,null,new ResultParser(), this);
		sendRequest(infoResultRequest, R.id.position_detail);
		
	}
	
	
	/**
	 * 刷新职位
	 */
	public void refreshPublishedPositions(String jid) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("jid", jid);
		
		InfoResultRequest infoResultRequest = new InfoResultRequest(R.id.position_refresh_published_positions, 
				ApiConstants.REFRESH_POSITIONED_POSITIONS, Method.POST, map, null, new ResultParser(), this);
		sendRequest(infoResultRequest, R.id.position_refresh_published_positions);
	}
	
	/**
	 * 关闭当前职位
	 */
	public void closePublishedPositions(String jid) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("jid", jid);
		
		InfoResultRequest infoResultRequest = new InfoResultRequest(R.id.position_close_published_positions, 
				ApiConstants.CLOSE_POSITIONED_POSITIONS, Method.POST, map, null, new ResultParser(), this);
		sendRequest(infoResultRequest, R.id.position_close_published_positions);
	}
	
	
	
	/**
	 * 开启当前职位
	 */
	public void openPublishedPositions(String jid) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("jid", jid);
		
		InfoResultRequest infoResultRequest = new InfoResultRequest(R.id.position_open_published_positions, 
				ApiConstants.OPEN_POSITIONED_POSITIONS, Method.POST, map, null, new ResultParser(), this);
		sendRequest(infoResultRequest, R.id.position_open_published_positions);
	}
	
	/**
	 * 统计候选人
	 */
	public void statisticsCandidate(String jid) {
		Map<String, String> map = new HashMap<String, String>();
		// map.put("jid", jid);
		
		InfoResultRequest infoResultRequest = new InfoResultRequest(R.id.position_statistics_candidate, 
				ApiConstants.STATISTICS_CANDIDATE + "?jid=" + jid, Method.GET, map, null, new ResultParser(), this);
		sendRequest(infoResultRequest, R.id.position_statistics_candidate);
	}
	
	/**
	 * 得到未处理的候选人信息
	 */
	public void getUnreadCandidate(String jid) {
		Map<String, String> map = new HashMap<String, String>();
		// map.put("jid", jid);
		
		InfoResultRequest infoResultRequest = new InfoResultRequest(R.id.position_unread_candidate, 
				ApiConstants.UNREAD_CANDIDATE + "?jid=" + jid, Method.GET, map, null, new ResultParser(), this);
		sendRequest(infoResultRequest, R.id.position_unread_candidate);
	}
	
	/**
	 * 获取全部的候选人信息
	 */
	public void getAllCandidate(String jid, String minapplyid) {
		Map<String, String> map = new HashMap<String, String>();
		// map.put("jid", jid);
		// map.put("minapplyid", minapplyid);
		
		InfoResultRequest infoResultRequest = new InfoResultRequest(R.id.position_all_candidate, 
				ApiConstants.ALL_CANDIDATE + "?jid=" + jid + "&minapplyid=" + minapplyid, Method.GET, map, null, new ResultParser(), this);
		sendRequest(infoResultRequest, R.id.position_all_candidate);
	}
	
	/**
	 * 得到已联系额候选人
	 */
	public void getContactedCandidate(String jid, String minapplyid) {
		Map<String, String> map = new HashMap<String, String>();
		// map.put("jid", jid);
		// map.put("minapplyid", minapplyid);
		
		InfoResultRequest infoResultRequest = new InfoResultRequest(R.id.position_contacted_candidate, 
				ApiConstants.CONTACTED_CANDIDATE + "?jid=" + jid + "&minapplyid=" + minapplyid, Method.GET, map, null, new ResultParser(), this);
		sendRequest(infoResultRequest, R.id.position_contacted_candidate);
	}
	
	/**
	 * 处理候选人
	 */
	public void readCandidate(String jid, String ownerid) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("jid", jid);
		map.put("ownerid", ownerid);
		
		InfoResultRequest infoResultRequest = new InfoResultRequest(R.id.position_read_candidate, 
				ApiConstants.READ_CANDIDATE, Method.POST, map, null, new ResultParser(), this);
		sendRequest(infoResultRequest, R.id.position_read_candidate);
	}
	
	/**
	 * 对候选人感兴趣
	 */
	public void interestCandidate(String jid, String ownerid) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("jid", jid);
		map.put("ownerid", ownerid);
		
		InfoResultRequest infoResultRequest = new InfoResultRequest(R.id.position_interest_candidate, 
				ApiConstants.INTEREST_CANDIDATE, Method.POST, map, null, new ResultParser(), this);
		sendRequest(infoResultRequest, R.id.position_interest_candidate);
	}
	
	/**
	 * 投递匿名简历
	 */
	public void applyNickFile(String jid,String ps){
		Map<String, String> map = new HashMap<String, String>();
		map.put("jid", jid);
		map.put("ps", ps);
		
		InfoResultRequest infoResultRequest = new InfoResultRequest(R.id.post_resume_nick_apply, 
				ApiConstants.POSITION_NICK_APPLY, Method.POST, map, null, new ResultParser(), this);
		sendRequest(infoResultRequest, R.id.post_resume_nick_apply);
	}
	
	/**
	 * 投递真实简历
	 */
	public void applyRealFile(String jid,String ps){
		Map<String, String> map = new HashMap<String, String>();
		map.put("jid", jid);
		map.put("ps", ps);
		
		InfoResultRequest infoResultRequest = new InfoResultRequest(R.id.post_resume_real_apply, 
				ApiConstants.POSITION_REAL_APPLY, Method.POST, map, null, new ResultParser(), this);
		sendRequest(infoResultRequest, R.id.post_resume_real_apply);
	}
	
	/**
	 * 获取职位最近面试通知信息
	 * @param jid
	 */
	public void getInterview(String jid){
		
		Map<String, String> map = new HashMap<String, String>();
		map.put("jid", jid);
		
		InfoResultRequest infoResultRequest = new InfoResultRequest(R.id.hr_get_interview_temp, 
				ApiConstants.HR_GET_INTERVIEW_TEMP+"?jid=" + jid ,Method.GET,map, null, new ResultParser(), this);
		sendRequest(infoResultRequest, R.id.hr_get_interview_temp);
	}
	
	/**
	 *  发送面试通知
	 * 
	 * @param receiverid
	 * @param receiverisreal
	 * @param jid
	 * @param date
	 * @param time
	 * @param address
	 * @param hrname
	 * @param hrmobile
	 * @param hremail
	 * @param ps
	 */
	public void sendInterview(String receiverid,Integer receiverisreal,String jid,String date,String time,String address,String hrname,String hrmobile,String hremail,String ps){
		Map<String, String> map = new HashMap<String, String>();
		map.put("receiverid", receiverid);
		map.put("receiverisreal", receiverisreal + "");
		map.put("jid", jid);
		map.put("date", date);
		map.put("time", time);
		map.put("address", address);
		map.put("hrname", hrname);
		map.put("hrmobile", hrmobile);
		map.put("hremail", hremail);
		map.put("ps", ps);
		
		InfoResultRequest infoResultRequest = new InfoResultRequest(R.id.hr_send_interview, 
				ApiConstants.HR_SEND_INTERVIEW,Method.POST,map, null, new ResultParser(), this);
		sendRequest(infoResultRequest, R.id.hr_send_interview);
	}
	
	
	/**
	 * 创建发布职位
	 * 
	 * @param position
	 * @param salarybegin
	 * @param salaryend
	 * @param expr
	 * @param city
	 * @param edu
	 * @param highlights
	 * @param desc
	 * @param headcount
	 */
	public void createPosition(String position,Integer salarybegin,Integer salaryend,String expr,String city,String edu,String highlights,String desc,Integer headcount){
		
		Map<String, String> map = new HashMap<String, String>();
		map.put("position", position);
		if(salarybegin != -1){
			map.put("salarybegin", String.valueOf(salarybegin));
		}
		if(salaryend != -1){
			map.put("salaryend", String.valueOf(salaryend));
		}
		map.put("expr", expr);
		map.put("city", city);
		map.put("edu", edu);
		map.put("highlights", highlights);
		map.put("desc", desc);
		map.put("headcount", String.valueOf(headcount));
		
		InfoResultRequest infoResultRequest = new InfoResultRequest(R.id.position_create_position, 
				ApiConstants.POSITION_POSITION_CREATE,Method.POST,map, null, new ResultParser(), this);
		sendRequest(infoResultRequest, R.id.position_create_position);
		
		
	}
	
	/**
	 * 修改发布职位
	 * 
	 * @param jid
	 * @param position
	 * @param salarybegin
	 * @param salaryend
	 * @param expr
	 * @param city
	 * @param edu
	 * @param highlights
	 * @param desc
	 * @param headcount
	 */
	public void updatePosition(String jid,String position,Integer salarybegin,Integer salaryend,String expr,String city,String edu,String highlights,String desc,Integer headcount,String industry){
		
		Map<String, String> map = new HashMap<String, String>();
		map.put("jid", jid);
		map.put("position", position);
		if(salarybegin != -1){
			map.put("salarybegin", String.valueOf(salarybegin));
		}
		if(salarybegin != -1){
			map.put("salaryend", String.valueOf(salaryend));
		}
		map.put("expr", expr);
		map.put("city", city);
		map.put("edu", edu);
		map.put("highlights", highlights);
		map.put("desc", desc);
		if(headcount != -1){
			map.put("headcount", String.valueOf(headcount));
		}
		map.put("industry", industry);
		map.put("isAward", "0");
		
		InfoResultRequest infoResultRequest = new InfoResultRequest(R.id.position_update_position, 
				ApiConstants.POSITION_POSITION_UPDATE, Method.POST, map, null, new ResultParser(), this);
		sendRequest(infoResultRequest, R.id.position_update_position);
		
		
	}
	
	/**
	 * 获取“招人才”的人才列表
	 * 
	 * @param minhotid
	 */
	public void getSeekers(String minhotid){
		
		InfoResultRequest infoResultRequest = new InfoResultRequest(R.id.look_seekers_get, ApiConstants.LOOK_SEEKERS + "?&minhotid=" + minhotid , null, new ResultParser(), this);
		sendRequest(infoResultRequest, R.id.look_seekers_get);
	}
	
	/**
	 * 获取热门职位
	 * @param tag
	 */
	public void getSuggestedPosition() {
		InfoResultRequest infoResultRequest = new InfoResultRequest(R.id.get_suggested_position, 
				ApiConstants.GET_SUGGESTED_POSITION , null, new ResultParser(), this);
		sendRequest(infoResultRequest, R.id.get_suggested_position);
	}
	
	/**
	 * 搜索职位
	 * @param tag
	 */
	public void getSearchJob(String profession, String key, String city, String expr, String salarybegin, String salaryend, String start) {
		StringBuilder sb = new StringBuilder();
		
		if (profession != null) {
			sb.append("profession=" + Uri.encode(profession));
		}
		
		if (key != null) {
			sb.append("key=" + Uri.encode(key));
		}
		
		if (city != null) {
			sb.append("&city=" + Uri.encode(city));
		}
		
		if (expr != null) {
			sb.append("&expr=" + Uri.encode(expr));
		}
		
		if (salarybegin != null) {
			sb.append("&salarybegin=" + Uri.encode(salarybegin));
		}
		
		if (salaryend != null) {
			sb.append("&salaryend=" + Uri.encode(salaryend));
		}
		
		if (start != null) {
			sb.append("&start=" + Uri.encode(start));
		}
		
		InfoResultRequest infoResultRequest = new InfoResultRequest(R.id.get_search_job, 
				ApiConstants.GET_SEARCH_JOB + "?" + sb.toString(), null, new ResultParser(), this);
		
		sendRequest(infoResultRequest, R.id.get_search_job);
	}
	
	/**
	 * 搜索人才
	 * @param tag
	 */
	public void getSearchTalent(String profession, String key, String city, String expr, String edu, String intentionstatus, String start) {
		StringBuilder sb = new StringBuilder();
		
		if (profession != null) {
			sb.append("profession=" + Uri.encode(profession));
		}
		
		if (key != null) {
			sb.append("key=" + Uri.encode(key));
		}
		
		if (city != null) {
			sb.append("&city=" + Uri.encode(city));
		}
		
		if (expr != null) {
			sb.append("&expr=" + Uri.encode(expr));
		}
		
		if (edu != null) {
			sb.append("&edu=" + Uri.encode(edu));
		}
		
		if (intentionstatus != null) {
			sb.append("&intentionstatus=" + Uri.encode(intentionstatus));
		}
		
		if (start != null) {
			sb.append("&start=" + Uri.encode(start));
		}
		
		InfoResultRequest infoResultRequest = new InfoResultRequest(R.id.get_search_talent, 
				ApiConstants.GET_SEARCH_TALENT + "?" + sb.toString(), null, new ResultParser(), this);
		
		sendRequest(infoResultRequest, R.id.get_search_talent);
	}
	
	public void cancel(Object tag){
		cancelAll(tag);
	}
}
