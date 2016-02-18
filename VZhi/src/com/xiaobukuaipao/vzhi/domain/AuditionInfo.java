package com.xiaobukuaipao.vzhi.domain;

import com.alibaba.fastjson.JSONObject;
import com.xiaobukuaipao.vzhi.util.GlobalConstants;

/**
 * 面试通知信息
 */
public class AuditionInfo {
	
	private JSONObject corp;
	
	private JSONObject hr;
	
	private JSONObject interview;
	
	private JSONObject job;
	
	
	public AuditionInfo(JSONObject jsonObject){
		if(jsonObject != null){
			
			if(jsonObject.getJSONObject(GlobalConstants.JSON_CORP) != null){
				corp = jsonObject.getJSONObject(GlobalConstants.JSON_CORP);
			}
			if(jsonObject.getJSONObject(GlobalConstants.JSON_HR) != null){
				hr = jsonObject.getJSONObject(GlobalConstants.JSON_HR);
			}
			if(jsonObject.getJSONObject(GlobalConstants.JSON_INTERVIEW) != null){
				interview = jsonObject.getJSONObject(GlobalConstants.JSON_INTERVIEW);
			}
			if(jsonObject.getJSONObject(GlobalConstants.JSON_JOB) != null){
				job = jsonObject.getJSONObject(GlobalConstants.JSON_JOB);
			}
			
			
			
		}
	}
	//以下是面试通知表单需要的数据
	
	public String getCorpPosition(){
		return job != null ? job.getString(GlobalConstants.JSON_POSITION) : "";
	}
	
	public String getCorpName(){
		return corp != null ? this.corp.getString(GlobalConstants.JSON_NAME) : "";
	}
	
	public String getTime(){
		return interview != null ? this.interview.getString(GlobalConstants.JSON_TIME) : "";
	}
	
	public String getDate(){
		return interview != null ? this.interview.getString(GlobalConstants.JSON_DATE) : "";
	}
	
	public String getLocation(){
		return interview != null ? this.interview.getString(GlobalConstants.JSON_ADDRESS) : "";
	}
	
	public String getHrName(){
		return hr != null ? this.hr.getString(GlobalConstants.JSON_NAME) : "";
	}
	
	public String getHrMobile(){
		return hr != null ? this.hr.getString(GlobalConstants.JSON_MOBILE) : "";
	}
	
	public String getHrEmail(){
		return hr != null ? this.hr.getString(GlobalConstants.JSON_EMAIL) : "";
	}
	
	public String getPostscript(){
		return interview != null ? this.interview.getString(GlobalConstants.JSON_POSTSCRIPT) : "";
	}
	
	

	public JSONObject getCorp() {
		return corp;
	}

	public void setCorp(JSONObject corp) {
		this.corp = corp;
	}

	public JSONObject getHr() {
		return hr;
	}

	public void setHr(JSONObject hr) {
		this.hr = hr;
	}

	public JSONObject getInterview() {
		return interview;
	}

	public void setInterview(JSONObject interview) {
		this.interview = interview;
	}

	public JSONObject getJob() {
		return job;
	}

	public void setJob(JSONObject job) {
		this.job = job;
	}
}
