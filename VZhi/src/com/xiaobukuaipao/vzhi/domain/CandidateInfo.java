package com.xiaobukuaipao.vzhi.domain;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xiaobukuaipao.vzhi.util.GlobalConstants;

public class CandidateInfo {
	private JSONObject applyjob;
	private String applytime;
	private JSONObject basic;
	private String postscript;
	private JSONArray skills;
	//第1位:感兴趣 第2位:约面试 第3位:接受面试 第4位:忽略面试 第5位:聊一聊
	private int contactstatus;
	
	private String interviewtime;
	
	private int isreal;
	
	// 已读状态--0表示未读，大于0表示已读
	private int readstatus;
	
	public CandidateInfo() {
		applyjob = null;
		applytime = null;
		basic = null; 
		postscript = null;
		skills = null;
		interviewtime = null;
	}
	
	public CandidateInfo(JSONObject jsonObject) {
		if (jsonObject.getJSONObject(GlobalConstants.JSON_APPLYJOB) != null) {
			applyjob = jsonObject.getJSONObject(GlobalConstants.JSON_APPLYJOB);
		}
		if (jsonObject.getString(GlobalConstants.JSON_APPLYTIME) != null) {
			applytime = jsonObject.getString(GlobalConstants.JSON_APPLYTIME);
		}
		if (jsonObject.getJSONObject(GlobalConstants.JSON_BASIC) != null) {
			basic = jsonObject.getJSONObject(GlobalConstants.JSON_BASIC);
		}
		if (jsonObject.getString(GlobalConstants.JSON_POSTSCRIPT) != null) {
			postscript = jsonObject.getString(GlobalConstants.JSON_POSTSCRIPT);
		}
		if (jsonObject.getJSONArray(GlobalConstants.JSON_SKILLS) != null) {
			skills = jsonObject.getJSONArray(GlobalConstants.JSON_SKILLS);
		}
		if (jsonObject.getInteger(GlobalConstants.JSON_CONTACTSTATUS) != null) {
			contactstatus = jsonObject.getInteger(GlobalConstants.JSON_CONTACTSTATUS);
		}
		if (jsonObject.getInteger(GlobalConstants.JSON_ISREAL) != null) {
			isreal = jsonObject.getInteger(GlobalConstants.JSON_ISREAL);
		}
		
		if (jsonObject.getInteger(GlobalConstants.JSON_READSTATUS) != null) {
			readstatus = jsonObject.getInteger(GlobalConstants.JSON_READSTATUS);
		}
		
		if (jsonObject.getString(GlobalConstants.JSON_INTERVIEWTIME) != null) {
			interviewtime = jsonObject.getString(GlobalConstants.JSON_INTERVIEWTIME);
		}
	}
	
	public void setApplyjob(JSONObject applyjob) {
		this.applyjob = applyjob;
	}
	public JSONObject getApplyjob() {
		return this.applyjob;
	}
	
	public void setApplytime(String applytime) {
		this.applytime = applytime;
	}
	public String getApplytime() {
		return this.applytime;
	}
	
	public void setBasic(JSONObject basic) {
		this.basic = basic;
	}
	public JSONObject getBasic() {
		return this.basic;
	}
	
	public void setPostscript(String postscript) {
		this.postscript = postscript;
	}
	public String getPostScript() {
		return this.postscript;
	}
	
	public void setSkills(JSONArray skills) {
		this.skills = skills;
	}
	public JSONArray getSkills() {
		return this.skills;
	}

	/**
	 *   "contactstatus": 7,    #联系状态，没有这个字段表示没联系过。从低位到高位的含义：第1位:感兴趣 第2位:约面试 第3位:接受面试 第4位:忽略面试 第5位:聊一聊
	 * @return
	 */
	public int getContactstatus() {
		return contactstatus;
	}

	/**
	 *   "contactstatus": 7,    #联系状态，没有这个字段表示没联系过。从低位到高位的含义：第1位:感兴趣 第2位:约面试 第3位:接受面试 第4位:忽略面试 第5位:聊一聊
	 * @param contactstatus
	 */
	public void setContactstatus(int contactstatus) {
		this.contactstatus = contactstatus;
	}

	public int getIsreal() {
		return isreal;
	}

	public void setIsreal(int isreal) {
		this.isreal = isreal;
	}
	
	public void setReadstatus(int readstatus) {
		this.readstatus = readstatus;
	}
	public int getReadstatus() {
		return this.readstatus;
	}
	
	public void setInterviewTime(String interviewtime) {
		this.interviewtime = interviewtime;
	}
	public String getInterviewTime() {
		return this.interviewtime;
	}
	
}
