package com.xiaobukuaipao.vzhi.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Log;
import android.util.Xml;

public class CityPullParser implements XmlParser {
	private static final String TAG = CityPullParser.class.getSimpleName();
	List<String> cityList = null;
	String city = null;
	@Override
	public List<String> parse(InputStream inputStream) throws IOException {
		// 由android.util.Xml创建一个XmlPullParser实例
		XmlPullParser parser = Xml.newPullParser();   
		// 设置输入流 并指明编码方式  
        try {
			parser.setInput(inputStream, "UTF-8");
			
	        int eventType = parser.getEventType();
	        
	        while (eventType != XmlPullParser.END_DOCUMENT) {  
	            switch (eventType) {  
		            case XmlPullParser.START_DOCUMENT:  
		            	cityList = new ArrayList<String>();  
		                break;  
		            case XmlPullParser.START_TAG:  
		                if (parser.getName().equals("string")) {  
		                	eventType = parser.next();
		                	city = parser.getText();
		                }
		                break;  
		            case XmlPullParser.END_TAG:  
		                if (parser.getName().equals("string")) {  
		                	cityList.add(city);  
		                    city = null;      
		                }  
		                break;  
	            }  
	            eventType = parser.next();  
	        }  
        } catch (XmlPullParserException e) {
			e.printStackTrace();
		}       
        
        if (cityList == null) {
        	Log.i(TAG, "cityList is null");
        }
        return cityList;  
	}

}
