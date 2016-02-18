package com.xiaobukuaipao.vzhi.domain.user;

import com.alibaba.fastjson.JSONObject;
import com.xiaobukuaipao.vzhi.util.GlobalConstants;

public class PrivateUserProfile extends UserProfile {

	public JSONObject intention;
	private Integer isbuddy;
	
	public PrivateUserProfile() {
		super();
		intention = null;
	}

	public PrivateUserProfile(JSONObject jsonObject) {
		super(jsonObject);
		if (jsonObject != null) {
			if (jsonObject.getJSONObject(GlobalConstants.JSON_INTENTION) != null) {
				intention = jsonObject.getJSONObject(GlobalConstants.JSON_INTENTION);
			}
			if (jsonObject.getString(GlobalConstants.JSON_ISBUDDY) != null) {
				isbuddy = jsonObject.getInteger(GlobalConstants.JSON_ISBUDDY);
			}
		}
	}

	public void setIntention(JSONObject intention) {
		this.intention = intention;
	}

	public JSONObject getIntention() {
		return this.intention;
	}
	
	public int getIsbuddy() {
		if(isbuddy == null){
			return 0;
		}
		return isbuddy;
	}

	public void setIsbuddy(Integer isbuddy) {
		this.isbuddy = isbuddy;
	}
	
}
