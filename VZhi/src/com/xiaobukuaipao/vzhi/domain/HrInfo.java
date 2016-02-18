package com.xiaobukuaipao.vzhi.domain;

import com.alibaba.fastjson.JSONObject;
import com.xiaobukuaipao.vzhi.util.GlobalConstants;
import com.xiaobukuaipao.vzhi.util.StringUtils;

public class HrInfo {

	private JSONObject begin;
	
	private JSONObject end;
	
	private Integer certify;
	
	private JSONObject corp;
	
	private String email;
	
	private Integer identity;
	
	private JSONObject position;

	private Integer workexprid;
	
	private String workdesc;
	
	public HrInfo(JSONObject jsonObject){
		if(jsonObject != null){
			if(jsonObject.getJSONObject(GlobalConstants.JSON_BEGINDATE) != null){
				begin = jsonObject.getJSONObject(GlobalConstants.JSON_BEGINDATE);
			}
			if(jsonObject.getJSONObject(GlobalConstants.JSON_ENDDATE) != null){
				end = jsonObject.getJSONObject(GlobalConstants.JSON_ENDDATE);
			}
			if(jsonObject.getInteger(GlobalConstants.JSON_CERTIFY) != null){
				certify = jsonObject.getInteger(GlobalConstants.JSON_CERTIFY);
			}
			if(jsonObject.getJSONObject(GlobalConstants.JSON_CORP) != null){
				corp = jsonObject.getJSONObject(GlobalConstants.JSON_CORP);
			}
			if(jsonObject.getString(GlobalConstants.JSON_EMAIL) != null){
				email = jsonObject.getString(GlobalConstants.JSON_EMAIL);
			}
			if(jsonObject.getInteger(GlobalConstants.JSON_IDENTITY) != null){
				identity = jsonObject.getInteger(GlobalConstants.JSON_IDENTITY);
			}
			if(jsonObject.getJSONObject(GlobalConstants.JSON_POSITION) != null){
				position = jsonObject.getJSONObject(GlobalConstants.JSON_POSITION);
			}
			if(jsonObject.getInteger(GlobalConstants.JSON_WORKEXPRID) != null){
				workexprid = jsonObject.getInteger(GlobalConstants.JSON_WORKEXPRID);
			}
			if(jsonObject.getString(GlobalConstants.JSON_DESC) != null){
				workdesc = jsonObject.getString(GlobalConstants.JSON_DESC);
			}
		}
	}
	

	public JSONObject getBegin() {
		return begin;
	}


	public Integer getCertify() {
		return certify;
	}

	public void setCertify(Integer certify) {
		this.certify = certify;
	}

	public JSONObject getCorp() {
		return corp;
	}

	public void setCorp(JSONObject corp) {
		this.corp = corp;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Integer getIdentity() {
		return identity;
	}

	public void setIdentity(Integer identity) {
		this.identity = identity;
	}

	public JSONObject getPosition() {
		return position;
	}

	public void setPosition(JSONObject position) {
		this.position = position;
	}
	
	public String getBeginStr(){
		return StringUtils.formatDate(getBeginYear(), getBeginMonth());
	}

	public void setBegin(JSONObject begin) {
		this.begin = begin;
	}

	public int getBeginYear(){
		int year = -1;
		if(begin !=  null){
			if(begin.getInteger(GlobalConstants.JSON_YEAR) != null){
				year = begin.getInteger(GlobalConstants.JSON_YEAR);
			}
		}
		return year;
	}
	
	public int getBeginMonth(){
		int month = -1;
		if(begin != null){
			if(begin.getInteger(GlobalConstants.JSON_MONTH) != null){
				month = begin.getInteger(GlobalConstants.JSON_MONTH);
			}
		}
		return month;
	}
	
	public JSONObject getEnd() {
		return end;
	}
	
	public String getEndStr(){
		if(getEnd() == null){
			return null;
		}
		return StringUtils.formatDate(getEndYear(), getEndMonth());
	}
	
	public void setEnd(JSONObject end) {
		this.end = end;
	}

	public int getEndYear(){
		int year = -1;
		if(end !=  null){
			if(end.getInteger(GlobalConstants.JSON_YEAR) != null){
				year = end.getInteger(GlobalConstants.JSON_YEAR);
			}
		}
		return year;
	}
	
	public int getEndMonth(){
		int month = -1;
		if(end != null){
			if(end.getInteger(GlobalConstants.JSON_MONTH) != null){
				month = end.getInteger(GlobalConstants.JSON_MONTH);
			}
		}
		return month;
	}
	
	
	public String getCorpName(){
		String name = null;
		if(corp != null){
			if(corp.getString(GlobalConstants.JSON_NAME) != null){
				name = corp.getString(GlobalConstants.JSON_NAME);
			}
		}
		return name;
	}
	
	public String getCorpLName(){
		String name = null;
		if(corp != null){
			if(corp.getString(GlobalConstants.JSON_LNAME) != null){
				name = corp.getString(GlobalConstants.JSON_LNAME);
			}
		}
		return name;
	}
	
	/**
	 * 
	 * 获得公司id
	 * @return
	 */
	public String getCorpId(){
		String id = null;
		if(corp != null){
			if(corp.getString(GlobalConstants.JSON_ID) != null){
				id = corp.getString(GlobalConstants.JSON_ID);
			}
		}
		return id;
	}
	
	public Integer getCorpComplete(){
		Integer complete = null;
		if(corp != null){
			if(corp.getInteger(GlobalConstants.JSON_COMPLETE) != null){
				complete = corp.getInteger(GlobalConstants.JSON_COMPLETE);
			}
		}
		return complete;
	}
	
	
	public String getPositionName(){
		String name = null;
		if(position != null){
			if(position.getString(GlobalConstants.JSON_NAME) != null){
				name = position.getString(GlobalConstants.JSON_NAME);
			}
		}

		return name;
	}

	public Integer getWorkexprid() {
		return workexprid;
	}

	public void setWorkexprid(Integer workexprid) {
		this.workexprid = workexprid;
	}

	public String getWorkdesc() {
		return workdesc;
	}

	public void setWorkdesc(String workdesc) {
		this.workdesc = workdesc;
	}
	
	
}
