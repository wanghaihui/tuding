package com.xiaobukuaipao.vzhi.domain.user;

import android.os.Parcel;
import android.os.Parcelable;

import com.alibaba.fastjson.JSONObject;
import com.xiaobukuaipao.vzhi.util.GlobalConstants;

/**
 * 基本信息
 * 
 * @author hongxin.bai
 * 
 */
public class BasicInfo implements Parcelable {
	public final static String BASICINFO = "basicinfo";

	private String id = "";
	private String mobile = "";
	private String nickname = "";
	private String name = "";
	private Integer gender = -1;
	private Integer age = -1;
	private String city = "";
	private Integer expr = -1;
	private String corp = "";
	private String passwd = "";
	private Integer identity = -1;
	private String avatar = "";
	private String email = "";
	private String realAvatar = "";
	private String position = "";

	private String groupAvatar = "";
	
	private String exprName;
	
	private String industry;
	
	private String introduce;
	
	
	
	
	
	public BasicInfo(Parcel in) {
		id = in.readString();
		mobile = in.readString();
		name = in.readString();
		nickname = in.readString();
		gender = in.readInt();
		age = in.readInt();
		city = in.readString();
		expr = in.readInt();
		corp = in.readString();
		passwd = in.readString();
		identity = in.readInt();
		avatar = in.readString();
		email = in.readString();
		realAvatar = in.readString();
		position = in.readString();
		introduce = in.readString();
	}

	public static final Parcelable.Creator<BasicInfo> CREATOR = new Parcelable.Creator<BasicInfo>() {
		public BasicInfo createFromParcel(Parcel in) {
			return new BasicInfo(in);
		}

		public BasicInfo[] newArray(int size) {
			return new BasicInfo[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(id);
		dest.writeString(mobile);
		dest.writeString(name);
		dest.writeString(nickname);
		dest.writeInt(gender);
		dest.writeInt(age);
		dest.writeString(city);
		dest.writeInt(expr);
		dest.writeString(corp);
		dest.writeString(passwd);
		dest.writeInt(identity);
		dest.writeString(avatar);
		dest.writeString(email);
		dest.writeString(realAvatar);
		dest.writeString(position);
		dest.writeString(introduce);
	}

	public BasicInfo() {

	}

	public BasicInfo(JSONObject jsonObject) {
		if (jsonObject.getString(GlobalConstants.JSON_ID) != null) {
			id = jsonObject.getString(GlobalConstants.JSON_ID);
		}
		if (jsonObject.getString(GlobalConstants.JSON_REALNAME) != null) {
			name = jsonObject.getString(GlobalConstants.JSON_REALNAME);
		}
		if (jsonObject.getString(GlobalConstants.JSON_NICKNAME) != null) {
			nickname = jsonObject.getString(GlobalConstants.JSON_NICKNAME);
		}
		if (jsonObject.getInteger(GlobalConstants.JSON_GENDER) != null) {
			gender = jsonObject.getInteger(GlobalConstants.JSON_GENDER);
		}
		if (jsonObject.getInteger(GlobalConstants.JSON_AGE) != null) {
			age = jsonObject.getInteger(GlobalConstants.JSON_AGE);
		}
		if (jsonObject.getString(GlobalConstants.JSON_CITY) != null) {
			city = jsonObject.getString(GlobalConstants.JSON_CITY);
		}
		if (jsonObject.getString(GlobalConstants.JSON_CORP) != null) {
			corp = jsonObject.getString(GlobalConstants.JSON_CORP);
		}
		if (jsonObject.getString(GlobalConstants.JSON_MOBILE) != null) {
			mobile = jsonObject.getString(GlobalConstants.JSON_MOBILE);
		}
		if (jsonObject.getString(GlobalConstants.JSON_CITY) != null) {
			city = jsonObject.getString(GlobalConstants.JSON_CITY);
		}
		if (jsonObject.getString(GlobalConstants.JSON_REALAVATAR) != null) {
			realAvatar = jsonObject.getString(GlobalConstants.JSON_REALAVATAR);
		}
		if (jsonObject.getString(GlobalConstants.JSON_NICKAVATAR) != null) {
			avatar = jsonObject.getString(GlobalConstants.JSON_NICKAVATAR);
		}
		JSONObject emailJsonObj = jsonObject.getJSONObject(GlobalConstants.JSON_EMAIL);
		if (emailJsonObj != null) {
			if (emailJsonObj.getString(GlobalConstants.JSON_PERSON) != null) {
				email = emailJsonObj.getString(GlobalConstants.JSON_PERSON);
			}
		}
		
		JSONObject exprJsonObj = jsonObject.getJSONObject(GlobalConstants.JSON_EXPRYEAR);
		if (exprJsonObj != null) {
			if (exprJsonObj.getString(GlobalConstants.JSON_NAME) != null) {
				expr = exprJsonObj.getInteger(GlobalConstants.JSON_ID);
			}
			if(exprJsonObj.getString(GlobalConstants.JSON_NAME) != null){
				exprName =  exprJsonObj.getString(GlobalConstants.JSON_NAME);
			}
		}
		
		JSONObject posiJsonObj = jsonObject.getJSONObject(GlobalConstants.JSON_POSITION);
		if (posiJsonObj != null) {
			if (posiJsonObj.getString(GlobalConstants.JSON_NAME) != null) {
				position = posiJsonObj.getString(GlobalConstants.JSON_NAME);
			}

		}
		
		if (jsonObject.getString(GlobalConstants.JSON_AVATAR) != null) {
			groupAvatar = jsonObject.getString(GlobalConstants.JSON_AVATAR);
		}
		
		if(jsonObject.getString(GlobalConstants.JSON_INDUSTRY) != null){
			industry = jsonObject.getString(GlobalConstants.JSON_INDUSTRY);
		}
		if(jsonObject.getString(GlobalConstants.JSON_INTRODUCE) != null){
			introduce = jsonObject.getString(GlobalConstants.JSON_INTRODUCE);
		}
		
	}

	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getGender() {
		return gender;
	}

	public void setGender(Integer gender) {
		this.gender = gender;
	}

	public int getAge() {
		return age == -1 ? 0 : age;
	}

	public void setAge(Integer age) {
		this.age = age;
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

	public String getPasswd() {
		return passwd;
	}

	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}

	public Integer getIdentity() {
		return identity;
	}

	public void setIdentity(Integer identity) {
		this.identity = identity;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getRealAvatar() {
		return realAvatar;
	}

	public void setRealAvatar(String realAvatar) {
		this.realAvatar = realAvatar;
	}

	public Integer getExpr() {
		return expr;
	}

	public void setExpr(Integer expr) {
		this.expr = expr;
	}

	public String getExprName() {
		return exprName;
	}

	public void setExprName(String exprName) {
		this.exprName = exprName;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public String getGroupAvatar() {
		return groupAvatar;
	}

	public void setGroupAvatar(String groupAvatar) {
		this.groupAvatar = groupAvatar;
	}

	public String getIndustry() {
		return industry;
	}

	public void setIndustry(String industry) {
		this.industry = industry;
	}

	public String getIntroduce() {
		return introduce;
	}

	public void setIntroduce(String introduce) {
		this.introduce = introduce;
	}

	
}
