package com.xiaobukuaipao.vzhi.domain.user;

import android.os.Parcel;
import android.os.Parcelable;

import com.alibaba.fastjson.JSONObject;
import com.xiaobukuaipao.vzhi.util.GlobalConstants;
import com.xiaobukuaipao.vzhi.util.StringUtils;

//new
public class Education implements Parcelable {
	private String id;
	private JSONObject begin; 
	private JSONObject end;
	private Suggest school;
	private Suggest major;
	private Suggest degree;

	public Education() {
	}

	public Education(Parcel in){
		id = in.readString();
		begin = (JSONObject) in.readSerializable();
		end = (JSONObject) in.readSerializable();
		school = in.readParcelable(Suggest.class.getClassLoader());
		major = in.readParcelable(Suggest.class.getClassLoader());
		degree = in.readParcelable(Suggest.class.getClassLoader());
		
	}
	
	public Education(JSONObject jsonObject) {
		if (jsonObject.getString(GlobalConstants.JSON_EDUEXPRID) != null) {
			id = jsonObject.getString(GlobalConstants.JSON_EDUEXPRID);
		}
		if (jsonObject.getJSONObject(GlobalConstants.JSON_BEGINDATE) != null) {
			begin = jsonObject.getJSONObject(GlobalConstants.JSON_BEGINDATE);
		}
		if (jsonObject.getJSONObject(GlobalConstants.JSON_ENDDATE) != null) {
			end = jsonObject.getJSONObject(GlobalConstants.JSON_ENDDATE);
		} 
		if (jsonObject.getJSONObject(GlobalConstants.JSON_SCHOOL) != null) {
			school = new Suggest(jsonObject.getJSONObject(GlobalConstants.JSON_SCHOOL));
		}
		if (jsonObject.getJSONObject(GlobalConstants.JSON_MAJOR) != null) {
			major = new Suggest(jsonObject.getJSONObject(GlobalConstants.JSON_MAJOR));
		}
		if (jsonObject.getJSONObject(GlobalConstants.JSON_DEGREE) != null) {
			degree = new Suggest(jsonObject.getJSONObject(GlobalConstants.JSON_DEGREE));
		}
	}

	public static final Parcelable.Creator<Education> CREATOR = new Parcelable.Creator<Education>() {
		public Education createFromParcel(Parcel in) {
			return new Education(in);
		}

		public Education[] newArray(int size) {
			return new Education[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(id);
		dest.writeSerializable(begin);
		dest.writeSerializable(end);
		dest.writeParcelable(school,flags);
		dest.writeParcelable(major,flags);
		dest.writeParcelable(degree,flags);
		
	}
	public boolean hasEmptyValue(){
		return degree == null||school == null ||major == null ||begin == null;
	}
	
	public boolean isEmpty(){
		return degree == null && school == null && major == null && begin == null;
		
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Suggest getDegree() {
		return degree;
	}

	public void setDegree(Suggest degree) {
		this.degree = degree;
	}

	public JSONObject getBegin() {
		return begin;
	}

	public String getBeginStr(){
		return StringUtils.formatDate(getBeginYear(), getBeginMonth());
	}
	
	public void setBegin(JSONObject begin) {
		this.begin = begin;
	}

	public int getBeginYear(){
		int year = -1;
		if(begin !=  null){
			if(begin.getInteger(GlobalConstants.JSON_YEAR) != null){
				year = begin.getInteger(GlobalConstants.JSON_YEAR);
			}
		}
		return year;
	}
	
	public int getBeginMonth(){
		int month = -1;
		if(begin != null){
			if(begin.getInteger(GlobalConstants.JSON_MONTH) != null){
				month = begin.getInteger(GlobalConstants.JSON_MONTH);
			}
		}
		return month;
	}
	
	public JSONObject getEnd() {
		return end;
	}

	public String getEndStr(){
		if(getEnd() == null){
			return null;
		}
		return StringUtils.formatDate(getEndYear(), getEndMonth());
	}
	
	public void setEnd(JSONObject end) {
		this.end = end;
	}

	public int getEndYear(){
		int year = -1;
		if(end !=  null){
			if(end.getInteger(GlobalConstants.JSON_YEAR) != null){
				year = end.getInteger(GlobalConstants.JSON_YEAR);
			}
		}
		return year;
	}
	
	public int getEndMonth(){
		int month = -1;
		if(end != null){
			if(end.getInteger(GlobalConstants.JSON_MONTH) != null){
				month = end.getInteger(GlobalConstants.JSON_MONTH);
			}
		}
		return month;
	}
	
	public Suggest getSchool() {
		return school;
	}

	public void setSchool(Suggest school) {
		this.school = school;
	}

	public Suggest getMajor() {
		return major;
	}

	public void setMajor(Suggest major) {
		this.major = major;
	}

	
	
}
