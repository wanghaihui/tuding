package com.xiaobukuaipao.vzhi.domain.user;

import android.os.Parcel;
import android.os.Parcelable;

import com.alibaba.fastjson.JSONObject;
import com.xiaobukuaipao.vzhi.util.GlobalConstants;

/**
 * 用户的基本数据信息
 * @author xiaobu
 *
 */
public class UserBasic implements Parcelable {
	private String id;
	private int age;
	private String city;
	/**
	 * 工作经验的id
	 */
	private int exprid;
	/**
	 * 工作经验name
	 */
	private String expryear;
	/**
	 * 性别id
	 */
	private int gender;
	
	/**
	 * 公司名称
	 */
	private String corp;
	/**
	 * 虚拟头像
	 */
	private String nickavatar;
	private String nickname;
	/**
	 * 真实头像
	 */
	private String realavatar;
	private String realname;
	/**
	 * 职位名称
	 */
	private String title;
	
	// mobile
	private String mobile;
	// email--个人邮箱
	private  String personalemail;
	// email--企业邮箱
	private String corpemail;
	
	private String introduce;
	/**
	 * 身份标示
	 */
	private int identity;
	public UserBasic() {
		id = null;
		age = -1;
		city = null;
		exprid = -1;
		expryear = null;
		gender = 1;
		corp = null;
		nickavatar = null;
		nickname = null;
		realavatar = null;
		realname = null;
		title = null;
		
		mobile = null;
		personalemail = null;
		corpemail = null;
		
		identity = -1;
		
		introduce = null;
	}
	
	public UserBasic(JSONObject jsonObject) {
		if (jsonObject.getString(GlobalConstants.JSON_ID) != null) {
			id = jsonObject.getString(GlobalConstants.JSON_ID);
		}
		
		if (jsonObject.getInteger(GlobalConstants.JSON_AGE) != null) {
			age = jsonObject.getInteger(GlobalConstants.JSON_AGE);
		}
		
		if (jsonObject.getString(GlobalConstants.JSON_CITY) != null) {
			city = jsonObject.getString(GlobalConstants.JSON_CITY);
		}
		
		if (jsonObject.getJSONObject(GlobalConstants.JSON_EXPRYEAR) != null) {
			exprid = jsonObject.getJSONObject(GlobalConstants.JSON_EXPRYEAR).getInteger(GlobalConstants.JSON_ID);
			expryear = jsonObject.getJSONObject(GlobalConstants.JSON_EXPRYEAR).getString(GlobalConstants.JSON_NAME);
		}
		
		if (jsonObject.getInteger(GlobalConstants.JSON_GENDER) != null) {
			gender = jsonObject.getInteger(GlobalConstants.JSON_GENDER);
		}
		
		if (jsonObject.getString(GlobalConstants.JSON_CORP) != null) {
			corp = jsonObject.getString(GlobalConstants.JSON_CORP);
		}
		
		if (jsonObject.getString(GlobalConstants.JSON_NICKAVATAR) != null) {
			nickavatar = jsonObject.getString(GlobalConstants.JSON_NICKAVATAR);
		}
		
		if (jsonObject.getString(GlobalConstants.JSON_NICKNAME) != null) {
			nickname = jsonObject.getString(GlobalConstants.JSON_NICKNAME);
		}
		
		if (jsonObject.getString(GlobalConstants.JSON_REALAVATAR) != null) {
			realavatar = jsonObject.getString(GlobalConstants.JSON_REALAVATAR);
		}
		
		if (jsonObject.getString(GlobalConstants.JSON_REALNAME) != null) {
			realname = jsonObject.getString(GlobalConstants.JSON_REALNAME);
		}
		
		if (jsonObject.getString(GlobalConstants.JSON_TITLE) != null) {
			title = jsonObject.getString(GlobalConstants.JSON_TITLE);
		}
		
		if (jsonObject.getString(GlobalConstants.JSON_MOBILE) != null) {
			mobile = jsonObject.getString(GlobalConstants.JSON_MOBILE);
		}
		
		if (jsonObject.getJSONObject(GlobalConstants.JSON_EMAIL) != null) {
			personalemail = jsonObject.getJSONObject(GlobalConstants.JSON_EMAIL).getString(GlobalConstants.JSON_PERSON);
			corpemail = jsonObject.getJSONObject(GlobalConstants.JSON_EMAIL).getString(GlobalConstants.JSON_CORP);
		}
		
		if(jsonObject.getInteger(GlobalConstants.JSON_IDENTITY) != null){
			identity = jsonObject.getInteger(GlobalConstants.JSON_IDENTITY);
		}
		
		if(jsonObject.getString(GlobalConstants.JSON_INTRODUCE) != null){
			introduce = jsonObject.getString(GlobalConstants.JSON_INTRODUCE);
		}
	}
	
	public void setId(String id) {
		this.id = id;
	}
	public String getId() {
		return this.id;
	}
	
	public void setAge(int age) {
		this.age = age;
	}
	public int getAge() {
		return this.age;
	}
	
	public void setCity(String city) {
		this.city = city;
	}
	public String getCity() {
		return this.city;
	}
	
	public void setExprid(int exprid) {
		this.exprid = exprid;
	}
	public int getExprid() {
		return this.exprid;
	}
	
	public void setExpryear(String expryear) {
		this.expryear = expryear;
	}
	public String getExpryear() {
		return this.expryear;
	}
	
	public void setGender(int gender) {
		this.gender = gender;
	}
	public int getGender() {
		return this.gender;
	}
	
	public void setCorp(String corp) {
		this.corp = corp;
	}
	public String getCorp() {
		return this.corp;
	}
	
	public void setNickavatar(String nickavatar) {
		this.nickavatar = nickavatar;
	}
	public String getNickavatar() {
		return this.nickavatar;
	}
	
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getNickname() {
		return this.nickname;
	}
	
	public void setRealavatar(String realavatar) {
		this.realavatar = realavatar;
	}
	public String getRealavatar() {
		return this.realavatar;
	}
	
	public void setRealname(String realname) {
		this.realname = realname;
	}
	public String getRealname() {
		return this.realname;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	public String getTitle() {
		return this.title;
	}
	
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getMobile() {
		if(mobile != null){
			return mobile;
		}
		return "";
	}
	
	public void setPersonalemail(String personalemail) {
		this.personalemail = personalemail;
	}
	public String getPersonalemail() {
		if(personalemail != null){
			return personalemail;
		}
		return "";
	}
	
	public void setCorpemail(String corpemail) {
		this.corpemail = corpemail;
	}
	public String getCorpemail() {
		if(corpemail != null){
			return corpemail;
		}
		return "";
	}

	public int getIdentity() {
		return identity;
	}

	public void setIdentity(int identity) {
		this.identity = identity;
	}

	
	public String getIntroduce() {
		return introduce;
	}

	public void setIntroduce(String introduce) {
		this.introduce = introduce;
	}

	// 实现Parcelable接口
	///////////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(id);
		dest.writeInt(age);
		dest.writeString(city);
		dest.writeInt(exprid);
		dest.writeString(expryear);
		dest.writeInt(gender);
		dest.writeString(corp);
		dest.writeString(nickavatar);
		dest.writeString(nickname);
		dest.writeString(realavatar);
		dest.writeString(realname);
		dest.writeString(title);
		dest.writeString(mobile);
		dest.writeString(personalemail);
		dest.writeString(corpemail);
		dest.writeInt(identity);
	}
	///////////////////////////////////////////////////////////////////////////////////////////////////
	
	// 重写CREATOR
	public static final Parcelable.Creator<UserBasic> CREATOR = new Parcelable.Creator<UserBasic>() {

		// 从Parcel中创建出类的实例的功能
		@Override
		public UserBasic createFromParcel(Parcel source) {
			UserBasic userBasic = new UserBasic();
			userBasic.id = source.readString();
			userBasic.age = source.readInt();
			userBasic.city = source.readString();
			userBasic.exprid = source.readInt();
			userBasic.expryear = source.readString();
			userBasic.gender = source.readInt();
			userBasic.corp = source.readString();
			userBasic.nickavatar = source.readString();
			userBasic.nickname = source.readString();
			userBasic.realavatar = source.readString();
			userBasic.realname = source.readString();
			userBasic.title = source.readString();
			userBasic.mobile = source.readString();
			userBasic.personalemail = source.readString();
			userBasic.corpemail = source.readString();
			userBasic.identity = source.readInt();
			return userBasic;
		}

		// 创建一个类型为T，长度为size的数组，仅一句话(return new T[size])即可--估计本方法是供外部类反序列化本类数组使用
		@Override
		public UserBasic[] newArray(int size) {
			return new UserBasic[size];
		}
	};
}
