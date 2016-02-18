package com.xiaobukuaipao.vzhi.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class JsonParser {

	public static Map<String,Object> parseJsonObject(JSONObject jsonObject){
		if(jsonObject == null){
			return null;
		}
		
		Map<String,Object> map = new HashMap<String, Object>();
		Set<String> keySet = jsonObject.keySet();
		
		for(String key : keySet){
			Object object = jsonObject.get(key);
			map.put(key, object);
		}
		
		return map;
	}
	
	public static List<Map<String,Object>> parseJsonArray(JSONArray jsonArray){
		if(jsonArray == null){
			return null;
		}
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		
		for(int i = 0; i <jsonArray.size() ; i ++){
			Map<String, Object> map = parseJsonObject(jsonArray.getJSONObject(i));
			list.add(map);
		}
		return list;
	}
}
