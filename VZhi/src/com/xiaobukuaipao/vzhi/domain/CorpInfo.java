package com.xiaobukuaipao.vzhi.domain;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xiaobukuaipao.vzhi.util.GlobalConstants;
import com.xiaobukuaipao.vzhi.util.StringUtils;

public class CorpInfo {
	private String id;
	private String name;
	// 公司Logo--Url
	private String logo;
	// 公司状态--创业OR上市
	private JSONObject type;
	
	private JSONArray benefits;
	
	private JSONObject industry;
	
	private String lname;
	
	private JSONObject scale;
	
	private String website;
	
	private String city;
	
	private String address;
	
	private String desc;
	
	
	
	public CorpInfo() {
	}
	
	public CorpInfo(JSONObject jsonObject) {
		if (jsonObject != null) {
			if (jsonObject.getString(GlobalConstants.JSON_ID) != null) {
				id = jsonObject.getString(GlobalConstants.JSON_ID);
			}
			
			if (jsonObject.getString(GlobalConstants.JSON_NAME) != null) {
				name = jsonObject.getString(GlobalConstants.JSON_NAME);
			}
			
			if (jsonObject.getJSONObject(GlobalConstants.JSON_TYPE) != null) {
				type = jsonObject.getJSONObject(GlobalConstants.JSON_TYPE);
			}
			
			if (jsonObject.getString(GlobalConstants.JSON_LOGO) != null) {
				logo = jsonObject.getString(GlobalConstants.JSON_LOGO);
			}
			
			if (jsonObject.getJSONArray(GlobalConstants.JSON_BENEFITS) != null) {
				benefits = jsonObject.getJSONArray(GlobalConstants.JSON_BENEFITS);
			}
			
			if (jsonObject.getString(GlobalConstants.JSON_CITY) != null) {
				city = jsonObject.getString(GlobalConstants.JSON_CITY);
			}
			
			if (jsonObject.getString(GlobalConstants.JSON_ADDRESS) != null) {
				address = jsonObject.getString(GlobalConstants.JSON_ADDRESS);
			}
			
			if (jsonObject.getString(GlobalConstants.JSON_DESC) != null) {
				desc = jsonObject.getString(GlobalConstants.JSON_DESC);
			}
			
			if (jsonObject.getJSONObject(GlobalConstants.JSON_INDUSTRY) != null) {
				industry = jsonObject.getJSONObject(GlobalConstants.JSON_INDUSTRY);
			}
			
			if (jsonObject.getString(GlobalConstants.JSON_LNAME) != null) {
				lname = jsonObject.getString(GlobalConstants.JSON_LNAME);
			}
			
			if (jsonObject.getJSONObject(GlobalConstants.JSON_SCALE) != null) {
				scale = jsonObject.getJSONObject(GlobalConstants.JSON_SCALE);
			}
			
			if (jsonObject.getString(GlobalConstants.JSON_WEBSITE) != null) {
				website = jsonObject.getString(GlobalConstants.JSON_WEBSITE);
			}
			
			
			
		}
	}
	
	/////////////////////////////////////////////////////////////////////////////////////
	public void setId(String id) {
		this.id = id;
	}
	public String getId() {
		return this.id;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return this.name;
	}
	
	public void setType(JSONObject type) {
		this.type = type;
	}
	public String getType() {
		String name = "";
		if(type != null){
			name = type.getString(GlobalConstants.JSON_NAME);
		}
		return name;
	}
	
	public Integer getTypeId() {
		Integer id = null;
		if(type != null){
			id = type.getInteger(GlobalConstants.JSON_ID);
		}
		return id;
	}
	public void setLogo(String logo) {
		this.logo = logo;
	}
	public String getLogo() {
		return this.logo;
	}
	
	public void setBenefits(JSONArray benefits) {
		this.benefits = benefits;
	}
	public JSONArray getBenefits() {
		return this.benefits;
	}

	public String getCity() {
		return StringUtils.isEmpty(city)? "": city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getDesc() {
		return StringUtils.isEmpty(desc)? "": desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getIndustry() {
		String name = "";
		if(industry != null){
			name = industry.getString(GlobalConstants.JSON_NAME);
		}
		return name;
	}

	public String getIndustryId() {
		String name = "";
		if(industry != null){
			name = industry.getString(GlobalConstants.JSON_ID);
		}
		return name;
	}
	
	public void setIndustry(JSONObject industry) {
		this.industry = industry;
	}

	public String getLname() {
		return StringUtils.isEmpty(lname)? "": lname;
	}

	public void setLname(String lname) {
		this.lname = lname;
	}

	public String getScale() {
		String name = "";
		if(scale != null){
			name = scale.getString(GlobalConstants.JSON_NAME);
		}
		return name;
	}

	public void setScale(JSONObject scale) {
		this.scale = scale;
	}

	public String getWebsite() {
		return StringUtils.isEmpty(website)? "": website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
	
	
	/////////////////////////////////////////////////////////////////////////////////////
	
}
