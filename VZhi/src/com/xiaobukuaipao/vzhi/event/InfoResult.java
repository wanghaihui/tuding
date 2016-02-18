package com.xiaobukuaipao.vzhi.event;

import java.lang.reflect.Type;

import com.android.volley.NetworkResponse;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class InfoResult {

	private String result;

	private NetworkResponse response;

	public InfoResult() {
		
	}

	public InfoResult(String result) {
		this.result = result;
	}
	
	public String getResult() {
		return result;
	}

	public InfoResult setResult(String result) {
		this.result = result;
		return this;
	}

	public NetworkResponse getResponse() {
		return response;
	}

	public InfoResult setResponse(NetworkResponse response) {
		this.response = response;
		return this;
	}

	// {"status":0,"msg":"验证码校验成功"}
	public class InfoMsg {
		
		public int status;
		public String msg;
		public String nickavatar;
		public String realavatar;
		public String logo;


		public int getStatus() {
			return status;
		}

		public void setStatus(int status) {
			this.status = status;
		}

		public String getMsg() {
			return msg;
		}

		public void setMsg(String msg) {
			this.msg = msg;
		}
		public String getNickavatar() {
			return nickavatar;
		}

		public void setNickavatar(String nickavatar) {
			this.nickavatar = nickavatar;
		}

		public String getRealavatar() {
			return realavatar;
		}

		public void setRealavatar(String realavatar) {
			this.realavatar = realavatar;
		}

		public String getLogo() {
			return logo;
		}

		public void setLogo(String logo) {
			this.logo = logo;
		}

	}

	/**
	 * 获取信息
	 * 
	 * @return
	 */
	public InfoMsg getMessage() {
		Gson gson = new Gson();
		Type type = new TypeToken<InfoMsg>() {
		}.getType();
		InfoMsg fromJson = gson.fromJson(result, type);
		return fromJson;
	}
}
