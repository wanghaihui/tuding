package com.xiaobukuaipao.vzhi.domain.user;

import java.io.Serializable;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xiaobukuaipao.vzhi.util.GlobalConstants;

public class Intention implements Serializable {
	/**
	 * 缺省序列号
	 */
	private static final long serialVersionUID = 1L;
	public JSONArray cities;
	public JSONArray corptypes;
	public JSONObject industry;
	public JSONObject minsalary;
	public JSONArray positions;
	public JSONObject status;
	
	public Intention() {
		cities = null;
		corptypes = null;
		industry = null;
		minsalary = null;
		positions = null;
		status = null;
	}
	
	public Intention(JSONObject jsonObject) {
		if (jsonObject != null) {
			if (jsonObject.getJSONArray(GlobalConstants.JSON_CITIES) != null) {
				cities = jsonObject.getJSONArray(GlobalConstants.JSON_CITIES);
			}
			if (jsonObject.getJSONArray(GlobalConstants.JSON_CORPTYPE) != null) {
				corptypes = jsonObject.getJSONArray(GlobalConstants.JSON_CORPTYPE);
			}
			if (jsonObject.getJSONObject(GlobalConstants.JSON_INDUSTRY) != null) {
				industry = jsonObject.getJSONObject(GlobalConstants.JSON_INDUSTRY);
			}
			
			if (jsonObject.getJSONObject(GlobalConstants.JSON_MINSALARY) != null) {
				minsalary = jsonObject.getJSONObject(GlobalConstants.JSON_MINSALARY);
			}
			
			if (jsonObject.getJSONArray(GlobalConstants.JSON_POSITIONS) != null) {
				positions = jsonObject.getJSONArray(GlobalConstants.JSON_POSITIONS);
			}
			
			if (jsonObject.getJSONObject(GlobalConstants.JSON_STATUS) != null) {
				status = jsonObject.getJSONObject(GlobalConstants.JSON_STATUS);
			}
		}
	}
	
	public void setCities(JSONArray cities) {
		this.cities = cities;
	}
	public JSONArray getCities() {
		return this.cities;
	}
	
	public void setCorptypes(JSONArray corptypes) {
		this.corptypes = corptypes;
	}
	public JSONArray getCorptypes() {
		return this.corptypes;
	}
	
	public void setIndustry(JSONObject industry) {
		this.industry = industry;
	}
	public JSONObject getIndustry() {
		return this.industry;
	}
	
	public void setMinsalary(JSONObject minsalary) {
		this.minsalary = minsalary;
	}
	public JSONObject getMinsalary() {
		return this.minsalary;
	}
	
	public void setPositions(JSONArray positions) {
		this.positions = positions;
	}
	public JSONArray getPositions() {
		return this.positions;
	}
	
	public void setStatus(JSONObject status) {
		this.status = status;
	}
	public JSONObject getStatus() {
		return this.status;
	}
}
