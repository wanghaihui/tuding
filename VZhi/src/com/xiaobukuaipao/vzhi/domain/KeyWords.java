package com.xiaobukuaipao.vzhi.domain;

/**
 * 关键字--id+name
 * @author xiaobu1
 *
 */
public class KeyWords {
	
	private String id;
	private String name;
	
	public KeyWords(String id, String name) {
		this.id = id;
		this.name = name;
	}
	
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
	
}
