package com.xiaobukuaipao.vzhi.util;

import java.util.LinkedList;

public class ActivityQueue {
	
	private static LinkedList<Class<?>> activityQueue ;

	private static boolean isTail;
	
	public static void buildQueue(){
		activityQueue = new LinkedList<Class<?>>();
	}
	
	public static boolean isExist(){
		return activityQueue != null;
	}
	
	public static Class<?> poll(){
		return activityQueue.poll();
	}
	public static Class<?> getFirst(){
		
		return activityQueue.getFirst();
	}
	public static void push(Class<?> activity){
		if(activityQueue	!=	null)
			activityQueue.add(activity);
	}
	
	public static void clear(){
		if(activityQueue	!=	null)
			activityQueue.clear();
	}
	
	public static boolean isEmpty(){
		return activityQueue.isEmpty();
	}
	
	public static Class<?> next(Class<?> activity){
		Class<?> tmp = null;
		int key = 0;
		if(activity == null){
			return tmp;
		}
		for(int index = 0; index < activityQueue.size() ; index ++ ){
			if(activityQueue.get(index).equals(activity)){
				key = index;
			}
		}
		key ++;//next step
		if(key == activityQueue.size()){
			return tmp; 
		}
		tmp = activityQueue.get(key);
		return tmp;
	}
	
	public static boolean hasNext(Class<?> activity){
		int key = 0;
		if(activity == null){
			return false;
		}
		for(int index = 0; index < activityQueue.size() ; index ++ ){
			if(activityQueue.get(index).equals(activity)){
				key = index;
			}
		}
		key ++;//next step
		if(key == activityQueue.size() - 1){
			isTail = true;
			return false;
		}
		return true;
	}
	
	public static boolean isTail() {
		return isTail;
	}
	
	public static void destroy(){
		isTail = false;
		if(activityQueue	!=	null)
			activityQueue.clear();
		activityQueue = null;
	}
}
