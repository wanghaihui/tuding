package com.xiaobukuaipao.vzhi.domain.social;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xiaobukuaipao.vzhi.domain.user.BasicInfo;
import com.xiaobukuaipao.vzhi.util.GlobalConstants;

public class GroupInfo {
	
	private JSONObject group;
	
	/**
	 * #当前用户是否设置了消息免打扰，1为设置，没有hasblock或hasblock为0表示没有设置
	 */
	private Integer hasblock;
	
	private JSONArray members;

	public GroupInfo(JSONObject jsonObject){
		if(jsonObject != null){
			if(jsonObject.getJSONObject(GlobalConstants.JSON_GROUP) != null){
				group = jsonObject.getJSONObject(GlobalConstants.JSON_GROUP);
			}
			
			if(jsonObject.getInteger(GlobalConstants.JSON_HASBLOCK) != null){
				hasblock = jsonObject.getInteger(GlobalConstants.JSON_HASBLOCK);
			}
			
			if(jsonObject.getJSONArray(GlobalConstants.JSON_MEMBERS) != null){
				members = jsonObject.getJSONArray(GlobalConstants.JSON_MEMBERS);
			}
		}
		
	}
	
	
	public JSONObject getGroup() {
		return group;
	}

	public void setGroup(JSONObject group) {
		this.group = group;
	}

	public Integer getHasblock() {
		return hasblock;
	}

	public void setHasblock(Integer hasblock) {
		this.hasblock = hasblock;
	}

	public JSONArray getMembers() {
		return members;
	}

	public void setMembers(JSONArray members) {
		this.members = members;
	}
	
	public long getCreatetime(){
		long createtime = 0;
		if(group != null){
			if(group.getLong(GlobalConstants.JSON_CREATETIME) != null){
				createtime = group.getLong(GlobalConstants.JSON_CREATETIME);
			}
		}
		
		return createtime;
	}
	
	/**
	 * #如果是投递群，则有这个字段，表示关联的实体（即职位）的id
	 * @return
	 */
	public int getEntityid(){
		int entityid = 0;
		if(group != null){
			if(group.getInteger(GlobalConstants.JSON_ENTITYID) != null){
				entityid = group.getInteger(GlobalConstants.JSON_ENTITYID);
			}
		}
		return entityid;
	}
	
	public int getGid(){
		int gid = 0;
		if(group != null){
			if(group.getInteger(GlobalConstants.JSON_GID) != null){
				gid = group.getInteger(GlobalConstants.JSON_GID);
			}
		}
		return gid;
	}

	public String getLogo(){
		String logo = null;
		if(group != null){
			if(group.getString(GlobalConstants.JSON_LOGO) != null){
				logo = group.getString(GlobalConstants.JSON_LOGO);
			}
		}
		return logo;
	}
	
	/**
	 * #当前群成员数，上限值对于职位投递群无限制，兴趣群为200
	 * @return
	 */
	public int getMemnum(){
		int memnum = 0;
		if(group != null){
			if(group.getInteger(GlobalConstants.JSON_MEMNUM) != null){
				memnum = group.getInteger(GlobalConstants.JSON_MEMNUM);
			}
		}
		return memnum;
	}
	
	public String getName(){
		String name = null;
		if(group != null){
			if(group.getString(GlobalConstants.JSON_NAME) != null){
				name = group.getString(GlobalConstants.JSON_NAME);
			}
		}
		return name;
	}

	/**
	 * #群的类型号，1001为职位投递群，2001为实名兴趣群
	 * @return
	 */
	public int getType(){
		int type = 0;
		if(group != null){
			if(group.getInteger(GlobalConstants.JSON_TYPE) != null){
				type = group.getInteger(GlobalConstants.JSON_TYPE);
			}
		}
		return type;
	}
	
	/**
	 * #群描述
	 * @return
	 */
	public String getDesc(){
		String desc = null;
		if(group != null){
			if(group.getString(GlobalConstants.JSON_DESC) != null){
				desc = group.getString(GlobalConstants.JSON_DESC);
			}
		}
		return desc;
	}
	
	
	/**
	 * 	
	 * @return
	 */
	public List<BasicInfo> getMembersList(){
		List<BasicInfo> list = null;
		if(members != null){
			list = new ArrayList<BasicInfo>();
			for (int i = 0; i < members.size(); i++) {
				list.add(new BasicInfo(members.getJSONObject(i)));
			}
		}
		return list;
	}

}
