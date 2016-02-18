package com.xiaobukuaipao.vzhi.domain.user;

import android.os.Parcel;
import android.os.Parcelable;

import com.alibaba.fastjson.JSONObject;
import com.xiaobukuaipao.vzhi.util.GlobalConstants;
import com.xiaobukuaipao.vzhi.util.StringUtils;

public class Experience implements Parcelable {
	public String id;
	
	public JSONObject begin; 
	
	public JSONObject end;
	// 公司
	public String corp;
	// 职位
	public String position;
	// 显示
	public JSONObject salary;
	
	public String desc;

	public Integer certify;
	
	public Experience(){
		
	}
	
	public Experience(Parcel in) {
		id = in.readString();
		begin = (JSONObject) in.readSerializable();
		end = (JSONObject) in.readSerializable();
		corp = in.readString();
		position = in.readString();
		salary = (JSONObject) in.readSerializable();
		desc = in.readString();
	}

	public Experience(JSONObject jsonObject) {
		if (jsonObject.getString(GlobalConstants.JSON_WORKEXPRID) != null) {
			id = jsonObject.getString(GlobalConstants.JSON_WORKEXPRID);
		}
		if (jsonObject.getJSONObject(GlobalConstants.JSON_BEGINDATE) != null) {
			begin = jsonObject.getJSONObject(GlobalConstants.JSON_BEGINDATE);
		}
		if (jsonObject.getJSONObject(GlobalConstants.JSON_ENDDATE) != null) {
			end = jsonObject.getJSONObject(GlobalConstants.JSON_ENDDATE);
		}
		if (jsonObject.getJSONObject(GlobalConstants.JSON_CORP) != null) {
			corp = jsonObject.getJSONObject(GlobalConstants.JSON_CORP).getString(GlobalConstants.JSON_NAME);
		}
		if (jsonObject.getJSONObject(GlobalConstants.JSON_POSITION) != null) {
			position = jsonObject.getJSONObject(GlobalConstants.JSON_POSITION).getString(GlobalConstants.JSON_NAME);
		}
		if (jsonObject.getJSONObject(GlobalConstants.JSON_SALARY) != null) {
			salary = jsonObject.getJSONObject(GlobalConstants.JSON_SALARY);
		}
		if (jsonObject.getString(GlobalConstants.JSON_DESC) != null) {
			desc = jsonObject.getString(GlobalConstants.JSON_DESC);
		}
		if(jsonObject.getInteger(GlobalConstants.JSON_CERTIFY) != null){
			certify = jsonObject.getInteger(GlobalConstants.JSON_CERTIFY);
		}
	}

	public static final Parcelable.Creator<Experience> CREATOR = new Parcelable.Creator<Experience>() {
		public Experience createFromParcel(Parcel in) {
			return new Experience(in);
		}

		public Experience[] newArray(int size) {
			return new Experience[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}
	public boolean hasEmptyValue(){
		return StringUtils.isEmpty(corp)||StringUtils.isEmpty(position)||begin == null;
	}
	
	public boolean isEmpty(){
		return StringUtils.isEmpty(corp) && StringUtils.isEmpty(position) && begin == null;
		
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(id);
		dest.writeSerializable(begin);
		dest.writeSerializable(end);
		dest.writeString(corp);
		dest.writeString(position);
		dest.writeSerializable(salary);
		dest.writeString(desc);
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return this.id;
	}

	public String getCorp() {
		return corp;
	}
	public void setCorp(String corp) {
		this.corp = corp;
	}

	public String getPosition() {
		return position;
	}
	public void setPosition(String position) {
		this.position = position;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}
	
	public String getDesc() {
		return this.desc;
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
	
	public JSONObject getSalary() {
		return salary;
	}

	public void setSalary(JSONObject salary) {
		this.salary = salary;
	}

	public String getSalaryName(){
		String name = null;
		if(salary != null){
			if(salary.getString(GlobalConstants.JSON_NAME) != null){
				name = salary.getString(GlobalConstants.JSON_NAME);
			}
		}
		return name;
	}
	
	public int getSalaryId(){
		int id = -1;
		if(salary != null){
			if(salary.getInteger(GlobalConstants.JSON_ID) != null){
				id = salary.getInteger(GlobalConstants.JSON_ID);
			}
		}
		return id;
	}

	public Integer getCertify() {
		return certify;
	}

	public void setCertify(Integer certify) {
		this.certify = certify;
	}
	
	
}
