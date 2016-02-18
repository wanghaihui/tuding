package com.xiaobukuaipao.vzhi.domain.user;

import android.os.Parcel;
import android.os.Parcelable;

import com.alibaba.fastjson.JSONObject;
import com.xiaobukuaipao.vzhi.util.GlobalConstants;

public class Suggest implements Parcelable {
	private String id;
	private String name;

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(id);
		dest.writeString(name);
	}

	public static final Parcelable.Creator<Suggest> CREATOR = new Parcelable.Creator<Suggest>() {
		public Suggest createFromParcel(Parcel in) {
			return new Suggest(in);
		}

		public Suggest[] newArray(int size) {
			return new Suggest[size];
		}
	};
	public Suggest(JSONObject jsonObject){
		id = jsonObject.getString(GlobalConstants.JSON_ID);
		name = jsonObject.getString(GlobalConstants.JSON_NAME);
	}
	
	public Suggest() {
	}
	
	
	public Suggest(String id, String name) {
		super();
		this.id = id;
		this.name = name;
	}

	private Suggest(Parcel in) {
		id = in.readString();
		name = in.readString();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
