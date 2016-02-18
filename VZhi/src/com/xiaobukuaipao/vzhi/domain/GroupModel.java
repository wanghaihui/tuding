package com.xiaobukuaipao.vzhi.domain;

import com.alibaba.fastjson.JSONObject;
import com.xiaobukuaipao.vzhi.util.GlobalConstants;

public class GroupModel {
	private String gid;
	private String name;
	private int type;
	private int memnum;
	private String logo;
	private String desc;
	private long createtime;
	
	public GroupModel(JSONObject jsonObject) {
		if (jsonObject.getString(GlobalConstants.JSON_GID) != null) {
			gid = jsonObject.getString(GlobalConstants.JSON_GID);
		}
		
		if (jsonObject.getString(GlobalConstants.JSON_NAME) != null) {
			name = jsonObject.getString(GlobalConstants.JSON_NAME);
		}
		
		if (jsonObject.getInteger(GlobalConstants.JSON_TYPE) != null) {
			type = jsonObject.getInteger(GlobalConstants.JSON_TYPE);
		}
		
		if (jsonObject.getInteger(GlobalConstants.JSON_MEMNUM) != null) {
			memnum = jsonObject.getInteger(GlobalConstants.JSON_MEMNUM);
		}
		
		if (jsonObject.getString(GlobalConstants.JSON_LOGO) != null) {
			logo = jsonObject.getString(GlobalConstants.JSON_LOGO);
		}
		
		if (jsonObject.getString(GlobalConstants.JSON_DESC) != null) {
			desc = jsonObject.getString(GlobalConstants.JSON_DESC);
		}
		
		if (jsonObject.getLong(GlobalConstants.JSON_CREATETIME) != null) {
			createtime = jsonObject.getLong(GlobalConstants.JSON_CREATETIME);
		}
 	}
	
	public void setGid(String gid) {
		this.gid = gid;
	}
	public String getGid() {
		return this.gid;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return this.name;
	}
	
	public void setType(int type) {
		this.type = type;
	}
	public int getType() {
		return this.type;
	}
	
	public void setMemnum(int memnum) {
		this.memnum = memnum;
	}
	public int getMemnum() {
		return this.memnum;
	}
	
	public void setLogo(String logo) {
		this.logo = logo;
	}
	public String getLogo() {
		return this.logo;
	}
	
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public String getDesc() {
		return this.desc;
	}
	
	public void setCreatetime(long createtime) {
		this.createtime = createtime;
	}
	public long getCreatetime() {
		return this.createtime;
	}

	@Override
	public String toString() {
		return "GroupModel [gid=" + gid + ", name=" + name + ", type=" + type
				+ ", memnum=" + memnum + ", logo=" + logo + ", desc=" + desc
				+ ", createtime=" + createtime + "]";
	}
	
	
	
}
