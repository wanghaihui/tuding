package com.xiaobukuaipao.vzhi.domain.user;

import com.alibaba.fastjson.JSONObject;
import com.xiaobukuaipao.vzhi.util.GlobalConstants;

public class AnonyBasicInfo extends BasicInfo {
	public static final String TAG = AnonyBasicInfo.class.getSimpleName();
	public String industry;
	public String edu;
	public String school;
	private String degree;
	private String major;
	/**
	 * 工作经验的id
	 */
	private int exprid;
	/**
	 * 工作经验name
	 */
	private String expryear;
	
	public AnonyBasicInfo() {
		super();
		industry = null;
		edu = null;
		school = null;
	}
	
	public AnonyBasicInfo(JSONObject jsonObject) {
		super(jsonObject);
		if (jsonObject.getString(GlobalConstants.JSON_INDUSTRY) != null) {
			industry = jsonObject.getString(GlobalConstants.JSON_INDUSTRY);
		}
		if (jsonObject.getString(GlobalConstants.JSON_EDU) != null) {
			edu = jsonObject.getString(GlobalConstants.JSON_EDU);
		}
		if (jsonObject.getString(GlobalConstants.JSON_SCHOOL) != null) {
			school = jsonObject.getString(GlobalConstants.JSON_SCHOOL);
		}
		if (jsonObject.getString(GlobalConstants.JSON_DEGREE) != null) {
			degree = jsonObject.getString(GlobalConstants.JSON_DEGREE);
		}
		if (jsonObject.getString(GlobalConstants.JSON_MAJOR) != null) {
			major = jsonObject.getString(GlobalConstants.JSON_MAJOR);
		}
		
		if (jsonObject.getJSONObject(GlobalConstants.JSON_EXPRYEAR) != null) {
			exprid = jsonObject.getJSONObject(GlobalConstants.JSON_EXPRYEAR).getInteger(GlobalConstants.JSON_ID);
			expryear = jsonObject.getJSONObject(GlobalConstants.JSON_EXPRYEAR).getString(GlobalConstants.JSON_NAME);
		}
	}
	
	public void setIndustry(String industry) {
		this.industry = industry;
	}
	public String getIndustry() {
		return this.industry;
	}
	
	public void setEdu(String edu) {
		this.edu = edu;
	}
	public String getEdu() {
		return this.edu;
	}
	
	public void setSchool(String school) {
		this.school = school;
	}
	public String getSchool() {
		return this.school;
	}

	public String getDegree() {
		return degree;
	}

	public void setDegree(String degree) {
		this.degree = degree;
	}

	public String getMajor() {
		return major;
	}

	public void setMajor(String major) {
		this.major = major;
	}
	public void setExprid(int exprid) {
		this.exprid = exprid;
	}
	public int getExprid() {
		return this.exprid;
	}
	
	public void setExpryear(String expryear) {
		this.expryear = expryear;
	}
	public String getExpryear() {
		return this.expryear;
	}
}
