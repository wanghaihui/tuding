package com.xiaobukuaipao.vzhi.domain;

public class Salary {
	private String begin;
	private String end;
	private String name;
	
	public Salary() {
		
	}
	
	public Salary(String begin, String end, String name) {
		this.begin = begin;
		this.end = end;
		this.name = name;
	}
	
	public void setBegin(String begin) {
		this.begin = begin;
	}
	public String getBegin() {
		return this.begin;
	}
	
	public void setEnd(String end) {
		this.end = end;
	}
	public String getEnd() {
		return this.end;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return this.name;
	}
	
}
