package com.xiaobukuaipao.vzhi.domain.social;

import com.xiaobukuaipao.vzhi.domain.SortModel;

/**
 * 联系人排序实体
 */
public class ContactsSortModel extends SortModel{
	
	private Integer id;
	private String avatar;
	private String city;
	private String corp;
	private Integer gender;
	private String position;
	
	public ContactsSortModel() {
		super();
	}
	
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getAvatar() {
		return avatar;
	}
	public void setAvatar(String avatar) {
		this.avatar = avatar;
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
	public String getPosition() {
		return position;
	}
	public void setPosition(String position) {
		this.position = position;
	}
}
