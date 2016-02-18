
package com.xiaobukuaipao.vzhi.wxapi;

import android.util.Log;

import com.tencent.mm.sdk.modelbase.BaseReq;
import com.umeng.socialize.weixin.view.WXCallbackActivity;

public class WXEntryActivity extends WXCallbackActivity {
	@Override
	public void onReq(BaseReq req) {
		super.onReq(req);
		
		Log.d("WXEntryActivity", "onReq");
	}
	
}
