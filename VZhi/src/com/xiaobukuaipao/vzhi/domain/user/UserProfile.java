package com.xiaobukuaipao.vzhi.domain.user;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xiaobukuaipao.vzhi.util.GlobalConstants;

public class UserProfile {
	public JSONObject basic;
	public JSONArray skills;
	public JSONObject expr;
	public JSONObject edu;
	public JSONObject contact;
	private String words;
	public UserProfile() {
		basic = null;
		skills = null;
		expr = null;
		edu = null;
		contact = null;
		words = null;
	}
	
	public UserProfile(JSONObject jsonObject) {
		if (jsonObject != null) {
			if (jsonObject.getJSONObject(GlobalConstants.JSON_BASIC) != null) {
				basic = jsonObject.getJSONObject(GlobalConstants.JSON_BASIC);
			}
			
			if (jsonObject.getJSONArray(GlobalConstants.JSON_SKILLS) != null) {
				skills = jsonObject.getJSONArray(GlobalConstants.JSON_SKILLS);
			}
			
			if (jsonObject.getJSONObject(GlobalConstants.JSON_EXPR) != null) {
				expr = jsonObject.getJSONObject(GlobalConstants.JSON_EXPR);
			}
			
			if (jsonObject.getJSONObject(GlobalConstants.JSON_EDU) != null) {
				edu = jsonObject.getJSONObject(GlobalConstants.JSON_EDU);
			}
			
			if (jsonObject.getJSONObject(GlobalConstants.JSON_CONTACT) != null) {
				contact = jsonObject.getJSONObject(GlobalConstants.JSON_CONTACT);
			}
			if(jsonObject.getString(GlobalConstants.JSON_WORDS) != null){
				words = jsonObject.getString(GlobalConstants.JSON_WORDS);
			}
		}
	}
	
	
	public void setBasic(JSONObject basic) {
		this.basic = basic;
	}
	public JSONObject getBasic() {
		return this.basic;
	}
	
	public void setSkills(JSONArray skills) {
		this.skills = skills;
	}
	public JSONArray getSkills() {
		return this.skills;
	}
	
	
	
	public void setExpr(JSONObject expr) {
		this.expr = expr;
	}
	public JSONObject getExpr() {
		return this.expr;
	}
	
	public void setEdu(JSONObject edu) {
		this.edu = edu;
	}
	public JSONObject getEdu() {
		return this.edu;
	}
	
	public void setContact(JSONObject contact) {
		this.contact = contact;
	}
	public JSONObject getContact() {
		return this.contact;
	}
	
	public String getWords() {
		return words;
	}

	public void setWords(String words) {
		this.words = words;
	}
	
}
