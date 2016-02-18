package com.xiaobukuaipao.vzhi.domain.social;

import com.alibaba.fastjson.JSONObject;
import com.xiaobukuaipao.vzhi.util.GlobalConstants;

/**
 * 陌生人收件箱 发送人
 */
public class Sender {

	private Integer id;
	private Integer identity;
	private String name;
	private String avatar;
	private String position;
	private String city;
	private String corp;
	private Integer gender;
	private Integer certify;

	public Sender(JSONObject jsonObject) {
		if(jsonObject.getInteger(GlobalConstants.JSON_ID) != null){
			id = jsonObject.getInteger(GlobalConstants.JSON_ID);
		}
		if(jsonObject.getInteger(GlobalConstants.JSON_IDENTITY) != null){
			identity = jsonObject.getInteger(GlobalConstants.JSON_IDENTITY);
		}
		if (jsonObject.getString(GlobalConstants.JSON_NAME) != null) {
			name = jsonObject.getString(GlobalConstants.JSON_NAME);
		}
		if (jsonObject.getString(GlobalConstants.JSON_AVATAR) != null) {
			avatar = jsonObject.getString(GlobalConstants.JSON_AVATAR);
		}
		if (jsonObject.getString(GlobalConstants.JSON_POSITION) != null) {
			position = jsonObject.getString(GlobalConstants.JSON_POSITION);
		}
		if (jsonObject.getString(GlobalConstants.JSON_CITY) != null) {
			city = jsonObject.getString(GlobalConstants.JSON_CITY);
		}
		if (jsonObject.getString(GlobalConstants.JSON_CORP) != null) {
			corp = jsonObject.getString(GlobalConstants.JSON_CORP);
		}
		if (jsonObject.getInteger(GlobalConstants.JSON_GENDER) != null) {
			gender = jsonObject.getInteger(GlobalConstants.JSON_GENDER);
		}
		if (jsonObject.getInteger(GlobalConstants.JSON_CERTIFY) != null) {
			certify = jsonObject.getInteger(GlobalConstants.JSON_CERTIFY);
		}
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getIdentity() {
		return identity;
	}

	public void setIdentity(Integer identity) {
		this.identity = identity;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
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

	public Integer getGender() {
		return gender;
	}

	public void setGender(Integer gender) {
		this.gender = gender;
	}

	public Integer getCertify() {
		return certify;
	}

	public void setCertify(Integer certify) {
		this.certify = certify;
	}

}
