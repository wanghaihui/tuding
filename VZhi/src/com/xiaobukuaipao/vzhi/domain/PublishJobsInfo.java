package com.xiaobukuaipao.vzhi.domain;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xiaobukuaipao.vzhi.util.GlobalConstants;

public class PublishJobsInfo {
	public JSONObject publichser;
	
	public JSONArray jobs;
	
	public PublishJobsInfo() {
		
	}
	
	public PublishJobsInfo(JSONObject jsonObject) {
		if(jsonObject != null){
			if (jsonObject.getJSONObject(GlobalConstants.JSON_PUBLISHER) != null) {
				publichser = jsonObject.getJSONObject(GlobalConstants.JSON_PUBLISHER);
			}
			
			if (jsonObject.getJSONArray(GlobalConstants.JSON_JOBS) != null) {
				jobs = jsonObject.getJSONArray(GlobalConstants.JSON_JOBS);
			}
		}
	}

	public JSONObject getPublichser() {
		return publichser;
	}

	public void setPublichser(JSONObject publichser) {
		this.publichser = publichser;
	}

	public JSONArray getJobs() {
		return jobs;
	}

	public void setJobs(JSONArray jobs) {
		this.jobs = jobs;
	}
	
	
}
