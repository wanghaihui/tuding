package com.xiaobukuaipao.vzhi.domain.social;

import android.os.Parcel;
import android.os.Parcelable;

import com.alibaba.fastjson.JSONObject;
import com.xiaobukuaipao.vzhi.util.GlobalConstants;

/**
 * 陌生人收件箱
 */
public class StrangerCardInfo implements Parcelable {

	private Integer type = -1;
	
	private Integer isreply = -1;
	private Integer hasreply = -1;
	private Integer invitationid = -1;
	private Integer greetingid = -1;
	private Integer hasaccept = 0;
	private Integer hasrefuse = 0;
	private String answer = "";
	private Integer answerid = -1;
	private String question = "";
	private Integer questionid = -1;
	
	private JSONObject sender;
	private Long time = -1L;
	/**
	 * time 
	 */
	private boolean isDisplay;
	private String htime;
	
	public static final Parcelable.Creator<StrangerCardInfo> CREATOR = new Parcelable.Creator<StrangerCardInfo>() {
		public StrangerCardInfo createFromParcel(Parcel in) {
			return new StrangerCardInfo(in);
		}

		public StrangerCardInfo[] newArray(int size) {
			return new StrangerCardInfo[size];
		}
	};

	private StrangerCardInfo(Parcel in) {
		type = in.readInt();
		isreply = in.readInt();
		hasreply = in.readInt();
		invitationid = in.readInt();
		greetingid = in.readInt();
		hasaccept = in.readInt();
		hasrefuse = in.readInt();
		answer = in.readString();
		answerid = in.readInt();
		question = in.readString();
		questionid = in.readInt();
		sender = (JSONObject) in.readSerializable();
		time = in.readLong();
		
	}

	public StrangerCardInfo(JSONObject jsonObject) {
		if (jsonObject.getInteger(GlobalConstants.JSON_TYPE) != null) {
			type = jsonObject.getInteger(GlobalConstants.JSON_TYPE);
		}
		if (jsonObject.getInteger(GlobalConstants.JSON_ISREPLAY) != null) {
			isreply = jsonObject.getInteger(GlobalConstants.JSON_ISREPLAY);
		}
		if (jsonObject.getInteger(GlobalConstants.JSON_HASREPLY) != null) {
			hasreply = jsonObject.getInteger(GlobalConstants.JSON_HASREPLY);
		}
		if (jsonObject.getInteger(GlobalConstants.JSON_GREETINGID) != null) {
			greetingid = jsonObject.getInteger(GlobalConstants.JSON_GREETINGID);
		}
		if (jsonObject.getInteger(GlobalConstants.JSON_INVITATIONID) != null) {
			invitationid = jsonObject.getInteger(GlobalConstants.JSON_INVITATIONID);
		}
		if (jsonObject.getInteger(GlobalConstants.JSON_HASACCEPT) != null) {
			hasaccept = jsonObject.getInteger(GlobalConstants.JSON_HASACCEPT);
		}
		if (jsonObject.getInteger(GlobalConstants.JSON_HASREFUSE) != null) {
			hasrefuse = jsonObject.getInteger(GlobalConstants.JSON_HASREFUSE);
		}
		if (jsonObject.getString(GlobalConstants.JSON_ANSWER) != null) {
			answer = jsonObject.getString(GlobalConstants.JSON_ANSWER);
		}
		if (jsonObject.getInteger(GlobalConstants.JSON_ANSWERID) != null) {
			answerid = jsonObject.getInteger(GlobalConstants.JSON_ANSWERID);
		}
		if (jsonObject.getString(GlobalConstants.JSON_QUESTION) != null) {
			question = jsonObject.getString(GlobalConstants.JSON_QUESTION);
		}
		if (jsonObject.getInteger(GlobalConstants.JSON_QUESTIONID) != null) {
			questionid = jsonObject.getInteger(GlobalConstants.JSON_QUESTIONID);
		}
		if (jsonObject.getJSONObject(GlobalConstants.JSON_SENDER) != null) {
			sender = jsonObject.getJSONObject(GlobalConstants.JSON_SENDER);
		}
		if (jsonObject.getLong(GlobalConstants.JSON_TIME) != null) {
			time = jsonObject.getLong(GlobalConstants.JSON_TIME);
		}
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	public Integer getAnswerid() {
		return answerid;
	}

	public void setAnswerid(Integer answerid) {
		this.answerid = answerid;
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public Integer getQuestionid() {
		return questionid;
	}

	public void setQuestionid(Integer questionid) {
		this.questionid = questionid;
	}

	public JSONObject getSender() {
		return sender;
	}

	public void setSender(JSONObject sender) {
		this.sender = sender;
	}

	public Long getTime() {
		return time;
	}

	public void setTime(Long time) {
		this.time = time;
	}


	public Integer getInvitationid() {
		return invitationid;
	}

	public void setInvitationid(Integer invitationid) {
		this.invitationid = invitationid;
	}

	public Integer getGreetingid() {
		return greetingid;
	}

	public void setGreetingid(Integer greetingid) {
		this.greetingid = greetingid;
	}

	public Integer getHasaccept() {
		return hasaccept;
	}

	public void setHasaccept(Integer hasaccept) {
		this.hasaccept = hasaccept;
	}

	public Integer getHasrefuse() {
		return hasrefuse;
	}

	public void setHasrefuse(Integer hasrefuse) {
		this.hasrefuse = hasrefuse;
	}

	public Integer getHasreply() {
		return hasreply;
	}

	public void setHasreply(Integer hasreply) {
		this.hasreply = hasreply;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(type);
		dest.writeInt(isreply);
		dest.writeInt(hasreply);
		dest.writeInt(invitationid);
		dest.writeInt(greetingid);
		dest.writeInt(hasaccept);
		dest.writeInt(hasrefuse);
		dest.writeString(answer);
		dest.writeInt(answerid);
		dest.writeString(question);
		dest.writeInt(questionid);
		dest.writeSerializable(sender);
		dest.writeLong(time);
	}

	public boolean isDisplay() {
		return isDisplay;
	}

	public void setDisplay(boolean isDisplay) {
		this.isDisplay = isDisplay;
	}

	public String getHtime() {
		return htime;
	}

	public void setHtime(String htime) {
		this.htime = htime;
	}
	
	public String getSenderId(){
		String senderId = null;
		if(sender != null){
			if(sender.getString(GlobalConstants.JSON_ID) != null)
				senderId = sender.getString(GlobalConstants.JSON_ID);
		}
		return senderId;
	}

	public Integer getIsreply() {
		return isreply;
	}

	public void setIsreply(Integer isreply) {
		this.isreply = isreply;
	}
	
}
