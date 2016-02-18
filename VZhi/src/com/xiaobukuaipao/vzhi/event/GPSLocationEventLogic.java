package com.xiaobukuaipao.vzhi.event;

import android.util.Log;

import com.xiaobukuaipao.vzhi.R;
import com.xiaobukuaipao.vzhi.event.parser.ResultParser;
import com.xiaobukuaipao.vzhi.util.GlobalConstants;
import com.xiaobukuaipao.vzhi.volley.InfoResultRequest;

public class GPSLocationEventLogic extends BaseEventLogic {
	private static final String TAG = GPSLocationEventLogic.class.getSimpleName();
	public void getPlaceByPostion(String lat, String lng) {
		
		StringBuffer urlStr = new StringBuffer(GlobalConstants.BAIDU_LOCATION__PREFIX_URL);
        urlStr.append(lat).append(",").append(lng) .append(GlobalConstants.BAIDU_LOCATION__END_URL);
        
        Log.i(TAG, urlStr.toString());
        // Json
		InfoResultRequest infoResultRequest = new InfoResultRequest(R.id.gpsLocationHttp, urlStr.toString(), new ResultParser(), this);
        // 第二个参数可以不设置, 取消请求用
        sendRequest(infoResultRequest, R.id.gpsLocationHttp);
    }
	
	/**
     * @param lat 纬度
     * @param lng 经度
     * @return
     */
    /*public static String getPlaceByPostion(String lat, String lng) {
        StringBuffer urlStr = new StringBuffer(GlobleConstants.BAIDU_LOCATION__PREFIX_URL);
        urlStr.append(lat).append(",").append(lng)
                .append(GlobleConstants.BAIDU_LOCATION__END_URL);

        String result = HttpUtil.executeHttpGet(urlStr.toString());
        if (StringUtils.isEmpty(result)) {
            return GlobleConstants.DEFAULT_CITY;
        }

        JSONObject jsonObject = JSONObject.fromObject(result);
        try {
            return jsonObject.getJSONObject("result").getJSONObject("addressComponent").get("city").toString();
        } catch (Exception e) {
            e.printStackTrace();
            return GlobleConstants.DEFAULT_CITY;
        }
    }*/
}
