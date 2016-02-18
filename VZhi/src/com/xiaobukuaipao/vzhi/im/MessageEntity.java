package com.xiaobukuaipao.vzhi.im;

import java.util.UUID;

/**
 * 消息实体基类
 * @author xiaobu
 *
 */
public class MessageEntity {
	// 序列号
	public int seqNo;
	
	// Message发送方
	public String fromId;
	// Message接收方
	public String toId;
	
	// 创建时间
	public int createTime;
	// 消息类型
	public byte type;
	
	// 消息长度
	public int msgLen;
	// 消息数据--字节数组
	public byte[] msgData;
	
	// 附加
	public String attach;
	
	// 消息Id
	public String msgId;
	// 会话Id
	public String sessionId;
	public int sessionType = -1;
	
	// 判断是否是自己
	public boolean isMyself() {
		return fromId.equals("数据库中的uid, cookie或者userinfo表里");
	}
	
	// 拷贝
	public void copy(MessageEntity anotherEntity) {
		seqNo = anotherEntity.seqNo;
		fromId = anotherEntity.fromId;
		toId = anotherEntity.toId;
		createTime = anotherEntity.createTime;
		type = anotherEntity.type;
		msgLen = anotherEntity.msgLen;
		msgData = anotherEntity.msgData;
		attach = anotherEntity.attach;
		// 生成新的Msg Id
		generateMsgId();
		sessionId = anotherEntity.sessionId;
		sessionType = anotherEntity.sessionType;
	}
	
	public void generateMsgId() {
		// 生成一个随机的UUID
		msgId = UUID.randomUUID().toString();
	}
	
	
}
