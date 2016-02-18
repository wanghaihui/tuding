package com.xiaobukuaipao.vzhi.domain;

import android.os.Parcel;
import android.os.Parcelable;

import com.alibaba.fastjson.JSONObject;
import com.xiaobukuaipao.vzhi.util.GlobalConstants;

public class JobInfo implements Parcelable {
	// Id
	private String id;
	private Integer jobId;
	// 职位
	private JSONObject position;
	// 薪资
	private String salary;
	// 工作经验
	private JSONObject expr;
	private String city;
	// 诱惑
	private String highlights;
	private String refreshtime;
	// 职位的属性，表示何种类型的职位--比如悬赏职位。。等
	private Integer attrs;
	private JSONObject edu;
	
	// 这些字段是发布的职位列表
	// 1表示正常状态 0表示已关闭
	private Integer status;
	private Integer allnum;
	private Integer unreadnum;

	public JobInfo() {
		id = "";
		position = null;
		salary = null;
		expr = null;
		city = null;
		highlights = null;
		refreshtime = null;
		// 默认是0, 不确定功能
		attrs = 0;
		// 默认开启
		status = 1;
		allnum = 0;
		unreadnum = 0;
	}

	public JobInfo(JSONObject jsonObject) {
		if (jsonObject != null) {
			if (jsonObject.getString(GlobalConstants.JSON_ID) != null) {
				id = jsonObject.getString(GlobalConstants.JSON_ID);
			}
			if (jsonObject.getInteger(GlobalConstants.JSON_ID) != null) {
				jobId = jsonObject.getInteger(GlobalConstants.JSON_ID);
			}
			if (jsonObject.getJSONObject(GlobalConstants.JSON_POSITION) != null) {
				position = jsonObject.getJSONObject(GlobalConstants.JSON_POSITION);
			}
			if (jsonObject.getJSONObject(GlobalConstants.JSON_SALARY) != null) {
				salary = jsonObject.getJSONObject(GlobalConstants.JSON_SALARY).getString(GlobalConstants.JSON_NAME);
			}
			if (jsonObject.getJSONObject(GlobalConstants.JSON_EXPR) != null) {
				expr = jsonObject.getJSONObject(GlobalConstants.JSON_EXPR);
			}
			if (jsonObject.getJSONObject(GlobalConstants.JSON_EDU) != null) {
				edu = jsonObject.getJSONObject(GlobalConstants.JSON_EDU);
			}
			if (jsonObject.getString(GlobalConstants.JSON_CITY) != null) {
				city = jsonObject.getString(GlobalConstants.JSON_CITY);
			}
			if (jsonObject.getString(GlobalConstants.JSON_HIGHLIGHTS) != null) {
				highlights = jsonObject
						.getString(GlobalConstants.JSON_HIGHLIGHTS);
			}
			if (jsonObject.getString(GlobalConstants.JSON_REFRESHTIME) != null) {
				refreshtime = jsonObject
						.getString(GlobalConstants.JSON_REFRESHTIME);
			}
			if (jsonObject.getInteger(GlobalConstants.JSON_ATTRS) != null) {
				attrs = jsonObject.getInteger(GlobalConstants.JSON_ATTRS);
			}
			if (jsonObject.getInteger(GlobalConstants.JSON_STATUS) != null) {
				status = jsonObject.getInteger(GlobalConstants.JSON_STATUS);
			}
			if (jsonObject.getInteger(GlobalConstants.JSON_ALLNUM) != null) {
				allnum = jsonObject.getInteger(GlobalConstants.JSON_ALLNUM);
			}
			if (jsonObject.getInteger(GlobalConstants.JSON_UNREADNUM) != null) {
				unreadnum = jsonObject.getInteger(GlobalConstants.JSON_UNREADNUM);
			}
		}
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return this.id;
	}

	public void setPosition(JSONObject position) {
		this.position = position;
	}

	public JSONObject getPosition() {
		return this.position;
	}

	public void setSalary(String salary) {
		this.salary = salary;
	}

	public String getSalary() {
		return this.salary;
	}

	public void setExpr(JSONObject expr) {
		this.expr = expr;
	}

	public JSONObject getExpr() {
		return this.expr;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCity() {
		return this.city;
	}

	public void setHighlights(String highlights) {
		this.highlights = highlights;
	}

	public String getHighlights() {
		return this.highlights;
	}

	public void setAttrs(Integer attrs) {
		this.attrs = attrs;
	}

	public Integer getAttrs() {
		return this.attrs;
	}

	public String getRefreshtime() {
		return refreshtime;
	}

	public void setRefreshtime(String refreshtime) {
		this.refreshtime = refreshtime;
	}
	
	public void setStatus(Integer status) {
		this.status = status;
	}
	public Integer getStatus() {
		return this.status;
	}
	
	public void setAllnum(Integer allnum) {
		this.allnum = allnum;
	}
	public Integer getAllnum() {
		return this.allnum;
	}
	
	public void setUnreadnum(Integer unreadnum) {
		this.unreadnum = unreadnum;
	}
	public Integer getUnreadnum() {
		return this.unreadnum;
	}
	
	public JSONObject getEdu() {
		return edu;
	}
	public String getEduName() {
		String name = "";
		if(edu != null){
			if(edu.getString(GlobalConstants.JSON_NAME) != null){
				name = edu.getString(GlobalConstants.JSON_NAME);
			}
		}
		return name;
	}

	public void setEdu(JSONObject edu) {
		this.edu = edu;
	}

	public Integer getJobId() {
		return jobId;
	}

	public void setJobId(Integer jobId) {
		this.jobId = jobId;
	}

	/////////////////////////////////////////////////////////////////////////////////////
	// 序列化
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(id);
		dest.writeString(position.toString());
		dest.writeString(salary);
		dest.writeString(expr.toString());
		dest.writeString(city);
		dest.writeString(highlights);
		dest.writeString(refreshtime);
		dest.writeInt(attrs);
		
		dest.writeInt(status);
		dest.writeInt(allnum);
		dest.writeInt(unreadnum);
	}
	//////////////////////////////////////////////////////////////////////////////////////
	
	// 重写CREATOR
	public static final Parcelable.Creator<JobInfo> CREATOR = new Parcelable.Creator<JobInfo>() {

		// 从Parcel中创建出类的实例的功能
		@Override
		public JobInfo createFromParcel(Parcel source) {
			JobInfo jobInfo = new JobInfo();
			
			jobInfo.id = source.readString();
			jobInfo.position = JSONObject.parseObject(source.readString());
			jobInfo.salary = source.readString();
			jobInfo.expr = JSONObject.parseObject(source.readString());
			jobInfo.city = source.readString();
			jobInfo.highlights = source.readString();
			jobInfo.refreshtime = source.readString();
			jobInfo.attrs = source.readInt();
			jobInfo.status = source.readInt();
			jobInfo.allnum = source.readInt();
			jobInfo.unreadnum = source.readInt();
			return jobInfo;
		}

		// 创建一个类型为T，长度为size的数组，仅一句话(return new T[size])即可--估计本方法是供外部类反序列化本类数组使用
		@Override
		public JobInfo[] newArray(int size) {
			return new JobInfo[size];
		}
	};

}
