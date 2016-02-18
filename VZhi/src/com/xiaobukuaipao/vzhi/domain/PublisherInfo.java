package com.xiaobukuaipao.vzhi.domain;

import com.alibaba.fastjson.JSONObject;
import com.xiaobukuaipao.vzhi.util.GlobalConstants;

public class PublisherInfo {
	private String id;
	private String name;
	private Integer gender;
	// 头像Url
	private String avatar;
	private Integer identity;
	private Integer certify;
	
	private String corpemail;
	
	private String position;
	private String corp;
	
	
	
	public PublisherInfo() {
		
	}
	
	public PublisherInfo(JSONObject jsonObject) {
		if (jsonObject != null) {
			if (jsonObject.getString(GlobalConstants.JSON_ID) != null) {
				id = jsonObject.getString(GlobalConstants.JSON_ID);
			}
			if (jsonObject.getString(GlobalConstants.JSON_NAME) != null) {
				name = jsonObject.getString(GlobalConstants.JSON_NAME);
			}
			if (jsonObject.getInteger(GlobalConstants.JSON_GENDER) != null) {
				gender = jsonObject.getInteger(GlobalConstants.JSON_GENDER);
			}
			if (jsonObject.getString(GlobalConstants.JSON_AVATAR) != null) {
				avatar = jsonObject.getString(GlobalConstants.JSON_AVATAR);
			}
			if (jsonObject.getInteger(GlobalConstants.JSON_IDENTITY) != null) {
				identity = jsonObject.getInteger(GlobalConstants.JSON_IDENTITY);
			}
			if (jsonObject.getInteger(GlobalConstants.JSON_CERTIFY) != null) {
				certify = jsonObject.getInteger(GlobalConstants.JSON_CERTIFY);
			}
			if (jsonObject.getString(GlobalConstants.JSON_CORPEMAIL) != null) {
				corpemail = jsonObject.getString(GlobalConstants.JSON_CORPEMAIL);
			}
			if (jsonObject.getString(GlobalConstants.JSON_POSITION) != null) {
				position = jsonObject.getString(GlobalConstants.JSON_POSITION);
			}
			if (jsonObject.getString(GlobalConstants.JSON_CORP) != null) {
				corp = jsonObject.getString(GlobalConstants.JSON_CORP);
			}
		}
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////
	public void setId(String id) {
		this.id = id;
	}
	public String getId() {
		return this.id;
	}
	public Integer getIntId() {
		Integer id = -1;
		try {
			id = Integer.parseInt(this.id);
		} catch (Exception e) {
			id = -1;
		}
		return id;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return this.name;
	}
	
	public void setGender(Integer gender) {
		this.gender = gender;
	}
	public Integer getGender() {
		return this.gender;
	}
	
	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
	public String getAvatar() {
		return this.avatar;
	}
	
	public void setIdentity(Integer identity) {
		this.identity = identity;
	}
	public Integer getIdentity() {
		return this.identity;
	}
	
	public void setCertify(Integer certify) {
		this.certify = certify;
	}
	public Integer getCertify() {
		return this.certify;
	}

	public String getCorpemail() {
		return corpemail;
	}

	public void setCorpemail(String corpemail) {
		this.corpemail = corpemail;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public String getCorp() {
		return corp;
	}

	public void setCorp(String corp) {
		this.corp = corp;
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////
}
