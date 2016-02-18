package com.xiaobukuaipao.vzhi.domain.user;

import com.alibaba.fastjson.JSONObject;
import com.xiaobukuaipao.vzhi.util.GlobalConstants;

public class NormalBasicInfo extends BasicInfo {
	public String title;
	public String avatar;
	public Integer certify;

	public NormalBasicInfo(JSONObject jsonObject) {
		super(jsonObject);
		if (jsonObject.getString(GlobalConstants.JSON_TITLE) != null) {
			title = jsonObject.getString(GlobalConstants.JSON_TITLE);
		}
		if (jsonObject.getString(GlobalConstants.JSON_AVATAR) != null) {
			avatar = jsonObject.getString(GlobalConstants.JSON_AVATAR);
		}
		if (jsonObject.getInteger(GlobalConstants.JSON_CERTIFY) != null) {
			certify = jsonObject.getInteger(GlobalConstants.JSON_CERTIFY);
		}
	}

	public NormalBasicInfo() {
		super();
		title = null;
		avatar = null;
		certify = 0;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitle() {
		return this.title;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getAvatar() {
		return this.avatar;
	}

	public void setCertify(Integer certify) {
		this.certify = certify;
	}

	public Integer getCertify() {
		return this.certify;
	}

}
