package com.xiaobukuaipao.vzhi.domain;

import com.alibaba.fastjson.JSONObject;
import com.xiaobukuaipao.vzhi.util.GlobalConstants;

public class SearchJob {
	private Integer attrs;
	private String city;
	private JSONObject corp;
	private JSONObject edu;
	private JSONObject expr;
	private String highlights;
	private String id;
	private JSONObject position;
	private JSONObject publisher;
	private String refreshtime;
	private JSONObject salary;
	private Integer status;
	
	public SearchJob() {
		attrs = 0;
		city = "";
		corp = null;
		edu = null;
		expr = null;
		highlights = "";
		id = "";
		position = null;
		publisher = null;
		refreshtime = "";
		salary = null;
		status = 0;
	}
	
	public SearchJob(JSONObject jsonObject) {
		if(jsonObject != null){
			if (jsonObject.getInteger(GlobalConstants.JSON_ATTRS) != null) {
				attrs = jsonObject.getInteger(GlobalConstants.JSON_ATTRS);
			}
			
			if (jsonObject.getString(GlobalConstants.JSON_CITY) != null) {
				city = jsonObject.getString(GlobalConstants.JSON_CITY);
			}
			
			if (jsonObject.getJSONObject(GlobalConstants.JSON_CORP) != null) {
				corp = jsonObject.getJSONObject(GlobalConstants.JSON_CORP);
			}
			
			if (jsonObject.getJSONObject(GlobalConstants.JSON_EDU) != null) {
				edu = jsonObject.getJSONObject(GlobalConstants.JSON_EDU);
			}
			
			if (jsonObject.getJSONObject(GlobalConstants.JSON_EXPR) != null) {
				expr = jsonObject.getJSONObject(GlobalConstants.JSON_EXPR);
			}
			
			if (jsonObject.getString(GlobalConstants.JSON_HIGHLIGHTS) != null) {
				highlights = jsonObject.getString(GlobalConstants.JSON_HIGHLIGHTS);
			}
			
			if (jsonObject.getString(GlobalConstants.JSON_ID) != null) {
				id = jsonObject.getString(GlobalConstants.JSON_ID);
			}
			
			if (jsonObject.getJSONObject(GlobalConstants.JSON_POSITION) != null) {
				position = jsonObject.getJSONObject(GlobalConstants.JSON_POSITION);
			}
			
			if (jsonObject.getJSONObject(GlobalConstants.JSON_PUBLISHER) != null) {
				publisher = jsonObject.getJSONObject(GlobalConstants.JSON_PUBLISHER);
			}
			
			if (jsonObject.getString(GlobalConstants.JSON_REFRESHTIME) != null) {
				refreshtime = jsonObject.getString(GlobalConstants.JSON_REFRESHTIME);
			}
			
			if (jsonObject.getJSONObject(GlobalConstants.JSON_SALARY) != null) {
				salary = jsonObject.getJSONObject(GlobalConstants.JSON_SALARY);
			}
			
			if (jsonObject.getInteger(GlobalConstants.JSON_STATUS) != null) {
				status = jsonObject.getInteger(GlobalConstants.JSON_STATUS);
			}
		}
	}

	public void setAttrs(Integer attrs) {
		this.attrs = attrs;
	}
	public Integer getAttrs() {
		return this.attrs;
	}
	
	public void setCity(String city) {
		this.city = city;
	}
	public String getCity() {
		return this.city;
	}
	
	public void setCorp(JSONObject corp) {
		this.corp = corp;
	}
	public JSONObject getCorp() {
		return this.corp;
	}
	
	public void setEdu(JSONObject edu) {
		this.edu = edu;
	}
	public JSONObject getEdu() {
		return this.edu;
	}
	
	public void setExpr(JSONObject expr) {
		this.expr = expr;
	}
	public JSONObject getExpr() {
		return this.expr;
	}
	
	public void setHighlights(String highlights) {
		this.highlights = highlights;
	}
	public String getHighlights() {
		return this.highlights;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	public String getId() {
		return this.id;
	}
	
	public void setPosition(JSONObject position) {
		this.position = position;
	}
	public JSONObject getPosition() {
		return this.position;
	}
	
	public void setPublisher(JSONObject publisher) {
		this.publisher = publisher;
	}
	public JSONObject getPublisher() {
		return this.publisher;
	}
	
	public void setRefreshtime(String refreshtime) {
		this.refreshtime = refreshtime;
	}
	public String getRefreshtime() {
		return this.refreshtime;
	}
	
	public void setSalary(JSONObject salary) {
		this.salary = salary;
	}
	public JSONObject getSalary() {
		return this.salary;
	}
	
	public void setStatus(Integer status) {
		this.status = status;
	}
	public Integer getStatus() {
		return this.status;
	}
	 
}
