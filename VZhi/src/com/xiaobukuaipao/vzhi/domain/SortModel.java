package com.xiaobukuaipao.vzhi.domain;

/**
 * 联系人或者城市的单元
 */
public class SortModel {
	
	// 存储的数据如:姓名
	private String name;
	// 存储拼音首字母大写如:X
    private String sortLetters; 

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getSortLetters() {
        return sortLetters;
    }
    public void setSortLetters(String sortLetters) {
        this.sortLetters = sortLetters;
    }
    
}
