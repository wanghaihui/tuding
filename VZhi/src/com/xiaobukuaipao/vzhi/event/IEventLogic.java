package com.xiaobukuaipao.vzhi.event;

public interface IEventLogic {

	/**
	 * 注册一个订阅者
	 * @param receiver
	 */
	void register(Object receiver);
	
	/**
	 * 解绑一个订阅者
	 * @param receiver
	 */
	void unregister(Object receiver);
	
	/**
	 * 解绑所有订阅者
	 */
	void unregisterAll();
	
	/**
	 * 封装所有内容，post给订阅者
	 * @param action
	 * @param response
	 */
	void onResult(int action, Object response);
}	
