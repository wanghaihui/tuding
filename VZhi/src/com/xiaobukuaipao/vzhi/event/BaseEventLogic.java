package com.xiaobukuaipao.vzhi.event;

import java.util.ArrayList;
import java.util.List;

import android.os.Message;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.xiaobukuaipao.vzhi.VZhiApplication;

import de.greenrobot.event.EventBus;

/**
 * 1、统一的Volley请求队列以及Request的发送 <br>
 * 2、使用EventBus通讯订阅者与发送者 <br>
 * 3、提供订阅者注册、解绑和业务请求结果统一分发的实现 [<br>
 * 一、针对当前框架做了修改 每一个BaseEventLogic对象默认情况下使用不同的EventBus{@link #BaseLogic()},
 * 保证同一种类型的事件不会发送给多个订阅者(EventBus默认情况会发送个多个订阅者) <br>
 * 二、如果需要同一类型的事件发送给多个订阅者 使用{@link #BaseLogic(EventBus eventBus)}构造函数,
 * 为每个订阅者提供同一个EventBus对象(例如EventBus.getDefault()), 这样会出现以下情况 <br>
 * 1、msg的what相同的情况会被多个订阅者接受并处理 <br>
 * 2、msg的what不相同的话会被多个订阅者接受, 但不会被处理(具体需要业务层控制) ]
 */
public class BaseEventLogic implements IEventLogic {
	
	// 存储所有的订阅者
	private List<Object> subscribers = new ArrayList<Object>();
	
	// Volley请求队列
	private static RequestQueue requestQueue = Volley.newRequestQueue(VZhiApplication.getInstance().getApplicationContext());
	
	// Default EventBus
	private EventBus mEventBus;

	// 两个构造函数
	public BaseEventLogic() {
		this(new EventBus());
	}
	
	public BaseEventLogic(EventBus mEventBus) {
		if (mEventBus == null) {
			this.mEventBus = EventBus.getDefault();
		} else {
			this.mEventBus = mEventBus;
		}
	}

	public BaseEventLogic getInstance() {
		return new BaseEventLogic();
	}
	
	@Override
	public void register(Object receiver) {
		if (!subscribers.contains(receiver)) {
			mEventBus.register(receiver);
			subscribers.add(receiver);
		}
	}

	@Override
	public void unregister(Object receiver) {
		if (subscribers.contains(receiver)) {
			mEventBus.unregister(receiver);
			subscribers.remove(receiver);
		}
	}

	@Override
	public void unregisterAll() {
		for (Object subscriber : subscribers) {
			mEventBus.unregister(subscriber);
		}
		subscribers.clear();
	}

	/**
	 * 取消某一个网络请求
	 * 
	 * @param tag
	 *            某次请求的唯一标识
	 */
	protected void cancelAll(Object tag) {
		requestQueue.cancelAll(tag);
	}

	/**
	 * 发送网络请求
	 * 
	 * @param request
	 */
	protected <T> void sendRequest(Request<T> request) {
		sendRequest(request, null);
	}

	/**
	 * 发送网络请求，并给这个请求设置Tag
	 * 
	 * @param request
	 * @param tag
	 */
	// 泛型函数
	protected <T> void sendRequest(Request<T> request, Object tag) {
		request.setTag(tag);
		requestQueue.add(request);
	}

	/**
	 * 负责封装结果内容, post给订阅者
	 * 
	 * @param action
	 *            任务标识
	 * @param response
	 *            响应结果 instanceof VolleyError表示网络请求出错 instanceof
	 *            InfoResult表示网络请求成功
	 */
	@Override
	public void onResult(int action, Object response) {
		Message msg = new Message();
		msg.what = action;
		msg.obj = response;
		mEventBus.post(msg);
	}

	public static RequestQueue getRequestQueue() {
		return requestQueue;
	}

	public static void setRequestQueue(RequestQueue requestQueue) {
		BaseEventLogic.requestQueue = requestQueue;
	}
	
	public  void post(Object event) {
		mEventBus.post(event);
	}
	
}
