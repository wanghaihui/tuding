package com.xiaobukuaipao.vzhi;

/**
 * 消息未读提示
 * 
 * @author hongxin.bai
 *
 */
public interface IMsgUnreadListener{
	/**
	 * @param position
	 * 	页面位置
	 * @param count
	 *		未读消息数 暂时可以不传
	 */
	void onMsgUnread(int position , int count);
}