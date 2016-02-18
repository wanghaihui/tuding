package com.xiaobukuaipao.vzhi.im;

import java.io.Serializable;

/**
 * 消息实体
 */
public class MessageInfo implements Serializable {

	/**
	 * 序列号UID
	 */
	private static final long serialVersionUID = 1L;
	
	// 消息的拥有者
	protected String ownerId;

	// 消息是发送还是接收
	private Boolean isSend = false;
	// 发送信息的用户id
	private String msgFromUserId = "";
	// 发送信息的用户名
	private String msgFromName = "";
	// 发送信息的用户昵称
	private String msgFromUserNick = "";
	// 用户头像URL链接
	private String msgFromUserAvatar = "";
	
	// 消息类型
	private int msgType;
	
	// 消息显示类型，本地显示使用
	private int displayType = IMConstants.DISPLAY_TYPE_TEXT;
	
	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}
	
	public String getOwnerId() {
		return this.ownerId;
	}
	
	public void setMsgFromUserId(String msgFromUserId) {
		this.msgFromUserId = msgFromUserId;
	}
	
	public String getMsgFromUserId() {
		return this.msgFromUserId;
	}
	
	public void setMsgFromName(String msgFromName) {
		this.msgFromName = msgFromName;
	}
	
	public String getMsgFromName() {
		return this.msgFromName;
	}
	
	public void setMsgFromUserNick(String msgFromUserNick) {
		this.msgFromUserNick = msgFromUserNick;
	}
	
	public String getMsgFromUserNick() {
		return this.msgFromUserNick;
	}
	
	public void setMsgFromUserAvatar(String msgFromUserAvatar) {
		this.msgFromUserAvatar = msgFromUserAvatar;
	}
	
	public String getMsgFromUserAvatar() {
		return this.msgFromUserAvatar;
	}
	
	public void setDisplayType(int displayType) {
		this.displayType = displayType;
	}
	
	public int getDisplayType() {
		return this.displayType;
	}
}
