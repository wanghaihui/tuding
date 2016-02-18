package com.xiaobukuaipao.vzhi.domain;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xiaobukuaipao.vzhi.util.GlobalConstants;

public class CorpPublishJobsInfo {
	public JSONObject mCorp;
	public JSONArray mPublisherJobs;
	
	public CorpPublishJobsInfo() {
		
	}
	
	public CorpPublishJobsInfo(JSONObject jsonObject) {
		if (jsonObject != null) {
			if (jsonObject.getJSONObject(GlobalConstants.JSON_CORP) != null) {
				mCorp = jsonObject.getJSONObject(GlobalConstants.JSON_CORP);
			}
			
			if (jsonObject.getJSONArray(GlobalConstants.JSON_PUBLISHER_JOBS) != null) {
				mPublisherJobs = jsonObject.getJSONArray(GlobalConstants.JSON_PUBLISHER_JOBS);
			}
		}
	}
	
	public void setCorp(JSONObject mCorp) {
		this.mCorp = mCorp;
	}
	public JSONObject getCorp() {
		return this.mCorp;
	}
	
	public void setPublisherJobs(JSONArray mPublisherJobs) {
		this.mPublisherJobs = mPublisherJobs;
	}
	public JSONArray getPublisherJobs() {
		return this.mPublisherJobs;
	}
	
}
