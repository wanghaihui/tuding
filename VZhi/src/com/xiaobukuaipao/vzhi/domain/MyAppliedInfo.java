package com.xiaobukuaipao.vzhi.domain;

import com.alibaba.fastjson.JSONObject;
import com.xiaobukuaipao.vzhi.util.GlobalConstants;

public class MyAppliedInfo {
	Integer id;
	Integer attrs;
	Integer status;// 是否关闭 0 表示关闭
	String salary;
	String city;
	String corp;
	String position;
	Integer applynum;
	Integer readnum;
	JSONObject publisher;// json
	String applytime;
	String applystatus;
	
	public MyAppliedInfo() {
	}
	
	public MyAppliedInfo(JSONObject jsonObject) {
		
		if(jsonObject.getInteger(GlobalConstants.JSON_ID) != null){
			id = jsonObject.getInteger(GlobalConstants.JSON_ID);
		}
		if(jsonObject.getInteger(GlobalConstants.JSON_ATTRS) != null){
			attrs = jsonObject.getInteger(GlobalConstants.JSON_ATTRS);
		}
		if(jsonObject.getInteger(GlobalConstants.JSON_STATUS) != null){
			status = jsonObject.getInteger(GlobalConstants.JSON_STATUS);
		}
		if(jsonObject.getJSONObject(GlobalConstants.JSON_SALARY) != null){
			JSONObject jsonSalary = jsonObject.getJSONObject(GlobalConstants.JSON_SALARY);
			if(jsonSalary != null){
				salary = jsonSalary.getString(GlobalConstants.JSON_NAME);
			}
		}
		if(jsonObject.getString(GlobalConstants.JSON_CITY) != null){
			city = jsonObject.getString(GlobalConstants.JSON_CITY);
		}
		if(jsonObject.getJSONObject(GlobalConstants.JSON_POSITION) != null){
			JSONObject jsonPosition = jsonObject.getJSONObject(GlobalConstants.JSON_POSITION);
			if(jsonPosition != null){
				position = jsonPosition.getString(GlobalConstants.JSON_NAME);
			}
		}
		if(jsonObject.getString(GlobalConstants.JSON_APPLYSTATUS) != null){
			applystatus = jsonObject.getString(GlobalConstants.JSON_APPLYSTATUS);
		}
		
		if(jsonObject.getJSONObject(GlobalConstants.JSON_CORP) != null){
			JSONObject jsonCorp = jsonObject.getJSONObject(GlobalConstants.JSON_CORP);
			if(jsonCorp != null){
				corp = jsonCorp.getString(GlobalConstants.JSON_NAME);
			}
		}
		if(jsonObject.getInteger(GlobalConstants.JSON_APPLYNUM) != null){
			applynum = jsonObject.getInteger(GlobalConstants.JSON_APPLYNUM);
		}
		if(jsonObject.getInteger(GlobalConstants.JSON_READNUM) != null){
			readnum= jsonObject.getInteger(GlobalConstants.JSON_READNUM);
		}
		
		if(jsonObject.getJSONObject(GlobalConstants.JSON_PUBLISHER) != null){
			publisher= jsonObject.getJSONObject(GlobalConstants.JSON_PUBLISHER);
		}
		
	}
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getAttrs() {
		return attrs;
	}

	public void setAttrs(Integer attrs) {
		this.attrs = attrs;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getSalary() {
		return salary;
	}

	public void setSalary(String salary) {
		this.salary = salary;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCorp() {
		return corp;
	}

	public void setCorp(String corp) {
		this.corp = corp;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public Integer getApplynum() {
		return applynum;
	}

	public void setApplynum(Integer applynum) {
		this.applynum = applynum;
	}

	public Integer getReadnum() {
		return readnum;
	}

	public void setReadnum(Integer readnum) {
		this.readnum = readnum;
	}


	public JSONObject getPublisher() {
		return publisher;
	}

	public void setPublisher(JSONObject publisher) {
		this.publisher = publisher;
	}

	public String getApplytime() {
		return applytime;
	}

	public void setApplytime(String applytime) {
		this.applytime = applytime;
	}

	public String getApplystatus() {
		return applystatus;
	}

	public void setApplystatus(String applystatus) {
		this.applystatus = applystatus;
	}
}
