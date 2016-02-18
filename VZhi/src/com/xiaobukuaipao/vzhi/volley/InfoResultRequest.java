package com.xiaobukuaipao.vzhi.volley;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.xiaobukuaipao.vzhi.VZhiApplication;
import com.xiaobukuaipao.vzhi.event.IEventLogic;
import com.xiaobukuaipao.vzhi.event.InfoResult;
import com.xiaobukuaipao.vzhi.event.TFailedEvent;
import com.xiaobukuaipao.vzhi.register.SplashActivity;
import com.xiaobukuaipao.vzhi.util.GlobalConstants;
import com.xiaobukuaipao.vzhi.util.Logcat;
import com.xiaobukuaipao.vzhi.util.StringUtils;

import de.greenrobot.event.EventBus;

/**
 * 重写Request实现InfoResult结果类型的网络请求, 做了以下扩展 <br>
 * 1、提供设置请求头header <br>
 * 2、提供post方式设置消息体body
 */
public class InfoResultRequest extends Request<InfoResult> implements Listener<InfoResult> {

	public static final String TAG = InfoResultRequest.class.getSimpleName();
	/** 请求编码 */
	private static final String PROTOCOL_CHARSET = "utf-8";
	/** 请求id */
	private int requestId;
	/** 请求头部 */
	private Map<String, String> headers;
	/** 回调业务层做解析Bean封装 */
	private ResponseParserListener parserListener;
	/** 分发解析好的数据到业务层 */
	private IEventLogic logic;
	/** 请求体(参数设置等) */
	private String body;
	private Map<String, String> params;
	
	/** Socket timeout in milliseconds for requests */
    private static final int InfoResult_TIMEOUT_MS = 10000;

    /** Default number of retries for requests */
    private static final int InfoResult_MAX_RETRIES = 0;

    /** Default backoff multiplier for requests */
    private static final float InfoResult_BACKOFF_MULT = 2f;
	
	public InfoResultRequest(final int requestId, String url, int method,
			ResponseParserListener parseListener, final IEventLogic logic) {
		
		this(requestId, url, method, null, null, parseListener, logic);
	}

	public InfoResultRequest(final int requestId, String url,
			ResponseParserListener parseListener, final IEventLogic logic) {
		this(requestId, url, Method.GET, null, null, parseListener, logic);
	}

	public InfoResultRequest(final int requestId, String url,
			Map<String, String> headers, ResponseParserListener parseListener,
			final IEventLogic logic) {
		
		this(requestId, url, Method.GET, null, headers, parseListener, logic);
	}
	
	public InfoResultRequest(final int requestId, String url, int method,
			Map<String, String> params, Map<String, String> headers,
			ResponseParserListener parseListener, final IEventLogic logic) {

		super(method, url, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				logic.onResult(requestId, error);
			}
		});
		
		// 如果是Post请求, 重传机制改变为0次重传, 待测试
		if (method == Method.POST) {
			setRetryPolicy(
	                new DefaultRetryPolicy(InfoResult_TIMEOUT_MS, InfoResult_MAX_RETRIES, InfoResult_BACKOFF_MULT));
		}
		
		this.params = params;
		this.headers = headers;
		this.parserListener = parseListener;
		this.requestId = requestId;
		this.logic = logic;
	}

	/**
	 * 必须重载,
	 * 
	 * 如果这个返回空,就不走getPramas
	 */
	@Override
	public byte[] getBody() throws AuthFailureError {
		try {
			return body == null ? super.getBody() : body.getBytes(PROTOCOL_CHARSET);
		} catch (UnsupportedEncodingException uee) {
			VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s",body, PROTOCOL_CHARSET);
			return null;
		}
	}

	@Override
	protected Map<String, String> getParams() throws AuthFailureError {
		if (!params.isEmpty()) {
			removeNullValue();
			return params;
		}
		return super.getParams();
	}

	/**
	 * 去除空的参数
	 */
	public void removeNullValue() {
		List<String> keyset = new ArrayList<String>();
		for (Map.Entry<String, String> entry : params.entrySet()) {
			Logcat.d(TAG, "k:" +entry.getKey() + " v:" + entry.getValue());
			if(filter(entry.getKey())){
				continue;
			}
			if (StringUtils.isEmpty(entry.getValue())) {
				keyset.add(entry.getKey());
			}
		}
		
		for (String key : keyset) {
			params.remove(key);
		}
		
	}
	
	/**
	 * 过滤掉 需要设置为空的参数 例如技能标签
	 * 
	 * @param key
	 * @return
	 */
	private boolean filter(String key){
		if(GlobalConstants.JSON_SKILLS.equals(key))
			return true;
		if(GlobalConstants.JSON_POSITIONS.equals(key))
			return true;
		return false;
	}
	
	@Override
	public void onResponse(InfoResult response) {
		logic.onResult(requestId, response);
	}
	
	@Override
	public Map<String, String> getHeaders() throws AuthFailureError {
		
		if (headers == null || headers.equals(Collections.emptyMap())) {
			headers = super.getHeaders();
			if (headers == null || headers.equals(Collections.emptyMap())) {
				headers = new HashMap<String, String>();
			}
		}
		
		VZhiApplication.getInstance().addSessionCookie(headers);
		
		return headers;
	}
	
	/**
	 * 解析网络返回数据----这里已经获得了网络请求的返回字符串
	 * 
	 * @see com.android.volley.Request#parseNetworkResponse(com.android.volley.NetworkResponse)
	 */
	@Override
	protected Response<InfoResult> parseNetworkResponse(NetworkResponse response) {
		
		responseFilter(response);
		
		try {
			String str = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
			InfoResult infoResult = parserListener.doParse(str);
			if (infoResult == null) { // 解析失败
				return Response.error(new VolleyError("parse response error >>> " + str));
			} else { // 解析成功
				infoResult.setResponse(response);
				
				return Response.success(infoResult, HttpHeaderParser.parseCacheHeaders(response));
			}
		} catch (UnsupportedEncodingException e) {
			return Response.error(new VolleyError("UnsupportedEncodingException"));
		} catch (Exception e) {
			return Response.error(new VolleyError("Exception is >>> "	+ e.getMessage()));
		}
	}

	/**
	 * response 过滤
	 * @param response
	 */
	private void responseFilter(NetworkResponse response) {
		Logcat.d(TAG, "status code : " + response.statusCode);
		
		switch (response.statusCode) {
			case 301:
			case 302:
			case 303:
			case 304:
			case 305:
			case 306:
			case 307:
				Intent intent = new Intent(VZhiApplication.getInstance(), SplashActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				VZhiApplication.getInstance().startActivity(intent);
				break;
			default:
				break;
		}
		
	}

	/**
	 * 分发Response
	 */
	@Override
	protected void deliverResponse(InfoResult response) {
		if(StringUtils.isNotEmpty(response.getResult())){
			Logcat.d("@@@", response.getResult());
			JSONObject parseObject = JSONObject.parseObject(response.getResult());
			
			if(parseObject != null){
				Object object = parseObject.get(GlobalConstants.JSON_STATUS);
				if(object != null && object instanceof Integer){
					Integer status = (Integer) object;
					if (status != null) {
						if (status  == 100) {
							if (VZhiApplication.mCookie_T != null) {
								EventBus.getDefault().post(new TFailedEvent("status = 100 and T Failed"));
								Log.i(TAG, "status = 100 and T Failed");
							}
						}
					}	
				}
			}
		}
		
		this.onResponse(response);
	}

	/**
	 * 通知调用者根据各自业务解析响应字符串到InfoResult
	 */
	public interface ResponseParserListener {
		InfoResult doParse(final String response) throws Exception;
	}

}
