package com.xiaobukuaipao.vzhi.volley;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.xiaobukuaipao.vzhi.VZhiApplication;
import com.xiaobukuaipao.vzhi.event.IEventLogic;
import com.xiaobukuaipao.vzhi.event.InfoResult;
import com.xiaobukuaipao.vzhi.util.Logcat;
import com.xiaobukuaipao.vzhi.volley.InfoResultRequest.ResponseParserListener;
import com.xiaobukuaipao.vzhi.volley.multipart.FilePart;
import com.xiaobukuaipao.vzhi.volley.multipart.MultiPartRequest;
import com.xiaobukuaipao.vzhi.volley.multipart.MultipartEntity;

public class SubMutiPartRequest extends MultiPartRequest<InfoResult> implements
		Listener<InfoResult> {
	/** 请求id */
	private int requestId;
	/** 回调业务层做解析Bean封装 */
	private ResponseParserListener parserListener;
	/** 分发解析好的数据到业务层 */
	private IEventLogic logic;

	private MultipartEntity entity;

	/** 请求头部 */
	private Map<String, String> headers;
	
	public SubMutiPartRequest(int method, String url,
			ErrorListener errorListener) {
		super(method, url, errorListener);
	}

	public SubMutiPartRequest(final int requestId, int method, String url,
			ResponseParserListener parseListener, final IEventLogic logic,
			MultipartEntity entity) {
		
		this(method, url, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				logic.onResult(requestId, error);
			}
		});
		
		this.parserListener = parseListener;
		this.requestId = requestId;
		this.logic = logic;
		this.entity = entity;
	}

	@Override
	protected Response<InfoResult> parseNetworkResponse(NetworkResponse response) {
		try {
			String str = new String(response.data,HttpHeaderParser.parseCharset(response.headers));
			InfoResult infoResult = parserListener.doParse(str);
			if (infoResult == null) // 解析失败
			{
				return Response.error(new VolleyError(
						"parse response error >>> " + str));
			} else {// 解析成功
				Logcat.d("@@@", response.headers.toString());
				infoResult.setResponse(response);
				return Response.success(infoResult,
						HttpHeaderParser.parseCacheHeaders(response));
			}

		} catch (UnsupportedEncodingException e) {
			return Response.error(new VolleyError(
					"UnsupportedEncodingException"));
		} catch (Exception e) {
			return Response.error(new VolleyError("Exception is >>> "
					+ e.getMessage()));
		}
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
	
	@Override
	public byte[] getBody() throws AuthFailureError {
		File file = new File(getFilesToUpload().get("filename"));
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		if (entity != null) {
			try {
				entity.addPart(new FilePart(getFilesToUpload().get("name"), file, file.getName(), "multipart/form-data"));
				entity.writeTo(baos);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			String str = new String(baos.toByteArray());
		}
		return baos.toByteArray();
	}

	@Override
	public String getBodyContentType() {
		return "multipart/form-data; boundary=\"" + entity.getBoundary() + '"';
	}
	
	@Override
	public void onResponse(InfoResult response) {
		logic.onResult(requestId, response);
	}

	@Override
	protected void deliverResponse(InfoResult response) {
		onResponse(response);
	}

}
