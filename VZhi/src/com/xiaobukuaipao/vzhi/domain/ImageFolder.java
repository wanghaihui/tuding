package com.xiaobukuaipao.vzhi.domain;

/**
 * 图片文件夹
 * @author haihui.wang
 *
 */

// 当前文件夹的路径，当前文件夹包含多少张图片，第一张图片的路径用于做文件夹的图标，文件夹的名称
public class ImageFolder {
	/**
	 * 图片的文件夹路径
	 */
	private String dir;
	
	/**
	 * 第一张图片的路径
	 */
	private String firstImagePath;
	
	/**
	 * 文件夹的名称
	 */
	private String name;
	
	/**
	 * 图片的数量
	 */
	private int count;
	
	public void setDir(String dir) {
		this.dir = dir;
		int lastIndexOf = this.dir.lastIndexOf("/");
		this.name = this.dir.substring(lastIndexOf);
	}
	
	public String getDir() {
		return dir;
	}
	
	public String getName() {
		return name;
	}
	
	public void setFirstImagePath(String firstImagePath) {
		this.firstImagePath = firstImagePath;
	}
	
	public String getFirstImagePath() {
		return firstImagePath;
	}
	
	public void setCount(int count) {
		this.count = count;
	}
	
	public int getCount() {
		return this.count;
	}
	
}
