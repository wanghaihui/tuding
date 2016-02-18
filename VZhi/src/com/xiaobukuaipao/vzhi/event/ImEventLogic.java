package com.xiaobukuaipao.vzhi.event;

import java.util.HashMap;
import java.util.Map;

import com.android.volley.Request.Method;
import com.xiaobukuaipao.vzhi.R;
import com.xiaobukuaipao.vzhi.event.parser.ResultParser;
import com.xiaobukuaipao.vzhi.volley.InfoResultRequest;

public class ImEventLogic extends BaseEventLogic {

	/**
	 * 取消网络请求
	 * @param tag
	 */
	public void cancel(Object tag){
		cancelAll(tag);
	}
	
	/**
	 * 得到Server的IP和Port
	 */
	public void getSocketIpAndPort() {
		InfoResultRequest infoResultRequest = new InfoResultRequest(R.id.socket_ip_and_port, ApiConstants.SOCKET_IP_PORT_GET, 
				null, new ResultParser(), this);
		sendRequest(infoResultRequest, R.id.socket_ip_and_port);
	}
	
	public void logout(String os, String userId, String channelId) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("os", os);
		map.put("userId", userId);
		map.put("channelId", channelId);
		
		InfoResultRequest infoResultRequest = new InfoResultRequest(R.id.social_logout, 
				ApiConstants.SOCIAL_LOGOUT, Method.POST,  map, null, new ResultParser(), this);
		// 第二个参数可以不设置, 取消请求用
		sendRequest(infoResultRequest, R.id.social_logout);
	}
}
