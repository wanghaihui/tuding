package com.xiaobukuaipao.vzhi.domain;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 行业分类
 * @author xiaobu1
 */
public class Profession implements Parcelable {
	
	private String id;
	private String name;
	
	public Profession() {
		this.id = "";
		this.name = "";
	}
	
	public Profession(String id, String name) {
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

	///////////////////////////////////////////////////////////////////////////////////////////////////
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(id);
		dest.writeString(name);
	}
	
	// 重写CREATOR
	public static final Parcelable.Creator<Profession> CREATOR = new Parcelable.Creator<Profession>() {

		// 从Parcel中创建出类的实例的功能
		@Override
		public Profession createFromParcel(Parcel source) {
			Profession profession = new Profession();
			
			profession.id = source.readString();
			profession.name = source.readString();
			return profession;
		}

		// 创建一个类型为T，长度为size的数组，仅一句话(return new T[size])即可--估计本方法是供外部类反序列化本类数组使用
		@Override
		public Profession[] newArray(int size) {
			return new Profession[size];
		}
	};
	
}
