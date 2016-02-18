package com.xiaobukuaipao.vzhi.domain.user;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xiaobukuaipao.vzhi.util.GlobalConstants;

public class PublicUserProfile extends UserProfile {

	public JSONArray jobs;
	public Integer isbuddy; 
	
	public PublicUserProfile() {
		jobs = null;
	}

	public PublicUserProfile(JSONObject jsonObject) {
		super(jsonObject);
		if (jsonObject.getJSONArray(GlobalConstants.JSON_JOBS) != null) {
			jobs = jsonObject.getJSONArray(GlobalConstants.JSON_JOBS);
		}
		if(jsonObject.getInteger(GlobalConstants.JSON_ISBUDDY) != null){
			isbuddy = jsonObject.getInteger(GlobalConstants.JSON_ISBUDDY);
		}
	}

	public void setJobs(JSONArray jobs) {
		this.jobs = jobs;
	}

	public JSONArray getJobs() {
		return this.jobs;
	}

	public Integer getIsbuddy() {
		return isbuddy;
	}

	public void setIsbuddy(Integer isbuddy) {
		this.isbuddy = isbuddy;
	}

	
}
