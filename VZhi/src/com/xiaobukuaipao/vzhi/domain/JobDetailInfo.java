package com.xiaobukuaipao.vzhi.domain;

import com.alibaba.fastjson.JSONObject;
import com.xiaobukuaipao.vzhi.util.GlobalConstants;

public class JobDetailInfo {
	public String id;
	public JSONObject position;
	public String salary;
	public JSONObject expr;
	public JSONObject edu;
	public String city;
	public String highlights;
	public String refreshtime;
	public Integer attrs;
	
	public String desc;
	public Integer viewnum;
	public Integer applynum;
	public Integer readnum;
	// 招聘人数
	public Integer headcount;
	
	public Integer status;
	
	private Integer salaryBegin;
	private Integer salaryEnd;
	
	public JobDetailInfo() {
		id = null;
		position = null;
		salary = null;
		expr = null;
		edu = null;
		city = null;
		highlights = null;
		refreshtime = null;
		attrs = null;
		desc = null;
		viewnum = null;
		applynum = null;
		readnum = null;
		headcount = null;
		status = null;
	}
	
	public JobDetailInfo(JSONObject jsonObject) {
		if (jsonObject != null) {
			if (jsonObject.getString(GlobalConstants.JSON_ID) != null) {
				id = jsonObject.getString(GlobalConstants.JSON_ID);
			}
			if (jsonObject.getJSONObject(GlobalConstants.JSON_POSITION) != null) {
				position = jsonObject.getJSONObject(GlobalConstants.JSON_POSITION);
			}
			if (jsonObject.getJSONObject(GlobalConstants.JSON_SALARY) != null) {
				salary = jsonObject.getJSONObject(GlobalConstants.JSON_SALARY).getString(GlobalConstants.JSON_NAME);
				salaryBegin = jsonObject.getJSONObject(GlobalConstants.JSON_SALARY).getInteger(GlobalConstants.JSON_BEGIN);
				salaryEnd = jsonObject.getJSONObject(GlobalConstants.JSON_SALARY).getInteger(GlobalConstants.JSON_END);
			}
			if (jsonObject.getJSONObject(GlobalConstants.JSON_EXPR) != null) {
				expr = jsonObject.getJSONObject(GlobalConstants.JSON_EXPR);
			}
			if (jsonObject.getJSONObject(GlobalConstants.JSON_EDU) != null) {
				edu = jsonObject.getJSONObject(GlobalConstants.JSON_EDU);
			}
			if (jsonObject.getString(GlobalConstants.JSON_CITY) != null) {
				city = jsonObject.getString(GlobalConstants.JSON_CITY);
			}
			if (jsonObject.getString(GlobalConstants.JSON_HIGHLIGHTS) != null) {
				highlights = jsonObject.getString(GlobalConstants.JSON_HIGHLIGHTS);
			}
			if (jsonObject.getString(GlobalConstants.JSON_REFRESHTIME) != null) {
				refreshtime = jsonObject.getString(GlobalConstants.JSON_REFRESHTIME);
			}
			if (jsonObject.getInteger(GlobalConstants.JSON_ATTRS) != null) {
				attrs = jsonObject.getInteger(GlobalConstants.JSON_ATTRS);
			}
			
			if (jsonObject.getString(GlobalConstants.JSON_DESC) != null) {
				desc = jsonObject.getString(GlobalConstants.JSON_DESC);
			}
			if (jsonObject.getInteger(GlobalConstants.JSON_VIEWNUM) != null) {
				viewnum = jsonObject.getInteger(GlobalConstants.JSON_VIEWNUM);
			}
			if (jsonObject.getInteger(GlobalConstants.JSON_APPLYNUM) != null) {
				applynum = jsonObject.getInteger(GlobalConstants.JSON_APPLYNUM);
			}
			if (jsonObject.getInteger(GlobalConstants.JSON_READNUM) != null) {
				readnum = jsonObject.getInteger(GlobalConstants.JSON_READNUM);
			}
			
			if (jsonObject.getInteger(GlobalConstants.JSON_HEADCOUNT) != null) {
				headcount = jsonObject.getInteger(GlobalConstants.JSON_HEADCOUNT);
			}
			
			if (jsonObject.getInteger(GlobalConstants.JSON_STATUS) != null) {
				status = jsonObject.getInteger(GlobalConstants.JSON_STATUS);
			}
		}
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////////
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
	public String getPositionName() {
		String name = "";
		if(position != null){
			if(position.getString(GlobalConstants.JSON_NAME) != null){
				name = position.getString(GlobalConstants.JSON_NAME);
			}
		}
		return name;
	}
	
	public void setSalary(String salary) {
		this.salary = salary;
	}
	public String getSalary() {
		return this.salary;
	}
	
	public void setExpr(JSONObject expr) {
		this.expr = expr;
	}
	public JSONObject getExpr() {
		return this.expr;
	}
	public String getExprName() {
		String name = "";
		if(expr != null){
			if(expr.getString(GlobalConstants.JSON_NAME) != null){
				name = expr.getString(GlobalConstants.JSON_NAME);
			}
		}
		return name;
	}
	public void setEdu(JSONObject edu) {
		this.edu = edu;
	}
	public JSONObject getEdu() {
		return this.edu;
	}
	public String getEduName() {
		String name = "";
		if(edu != null){
			if(edu.getString(GlobalConstants.JSON_NAME) != null){
				name = edu.getString(GlobalConstants.JSON_NAME);
			}
		}
		return name;
	}
	
	public void setCity(String city) {
		this.city = city;
	}
	public String getCity() {
		if(city != null){
			return city;
		}
		return "";
	}
	
	public void setHighlights(String highlights) {
		this.highlights = highlights;
	}
	public String getHighlights() {
		return this.highlights;
	}
	
	public void setRefreshTime(String refreshTime) {
		this.refreshtime = refreshTime;
	}
	public String getRefreshTime() {
		return this.refreshtime;
	}
	
	public void setAttrs(Integer attrs) {
		this.attrs = attrs;
	}
	public Integer getAttrs() {
		return this.attrs;
	}
	
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public String getDesc() {
		return this.desc;
	}
	
	public void setViewnum(Integer viewnum) {
		this.viewnum = viewnum;
	}
	public Integer getViewnum() {
		return this.viewnum;
	}
	
	public void setApplynum(Integer applynum) {
		this.applynum = applynum;
	}
	public Integer getApplynum() {
		return this.applynum;
	}
	
	public void setReadnum(Integer readnum) {
		this.readnum = readnum;
	}
	public Integer getReadnum() {
		return this.readnum;
	}
	
	public void setHeadcount(Integer headcount) {
		this.headcount = headcount;
	}
	public Integer getHeadcount() {
		return this.headcount;
	}
	
	public void setStatus(Integer status) {
		this.status = status;
	}
	public Integer getStatus() {
		return this.status;
	}

	public Integer getSalaryBegin() {
		return salaryBegin;
	}

	public void setSalaryBegin(Integer salaryBegin) {
		this.salaryBegin = salaryBegin;
	}

	public Integer getSalaryEnd() {
		return salaryEnd;
	}

	public void setSalaryEnd(Integer salaryEnd) {
		this.salaryEnd = salaryEnd;
	}
	
}
