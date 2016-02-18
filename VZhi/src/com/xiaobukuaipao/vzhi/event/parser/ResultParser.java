package com.xiaobukuaipao.vzhi.event.parser;

import com.alibaba.fastjson.JSONException;
import com.xiaobukuaipao.vzhi.event.InfoResult;
import com.xiaobukuaipao.vzhi.volley.InfoResultRequest.ResponseParserListener;

/**
 * A abstract class that parse logic status with type json
 */
public class ResultParser implements ResponseParserListener {
	/**
	 * 解析服务器结果的状态信息(业务成功与失败, 对应错误码和描述信息等)
	 * 
	 * @param response
	 * @return InfoResult
	 * @throws JSONException
	 *             , Exception
	 */
	public InfoResult doParse(final String response) throws JSONException {
		InfoResult infoResult = new InfoResult(response);
		return infoResult;
	}

}