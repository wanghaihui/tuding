package com.xiaobukuaipao.vzhi.register;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.xiaobukuaipao.vzhi.R;
import com.xiaobukuaipao.vzhi.adapter.VirtualAvatarAdapter;
import com.xiaobukuaipao.vzhi.event.InfoResult;
import com.xiaobukuaipao.vzhi.util.GlobalConstants;
import com.xiaobukuaipao.vzhi.view.NestedGridView;
import com.xiaobukuaipao.vzhi.wrap.RegisterWrapActivity;

public class RegisterBaseInfoAvatarActivity extends RegisterWrapActivity implements OnItemClickListener {

	private NestedGridView mGridAvatarF, mGridAvatarM;
	private List<String> mFAvatarList;
	private List<String> mMAvatarList;
	
	private Runnable backRunnable = new Runnable() {
		@Override
		public void run() {
			onBackPressed();
		}
	};
	
	
	private Handler mHandler = new Handler();
	
	
	@Override
	public void initUIAndData() {
		setContentView(R.layout.activity_register_baseinfo_avatar);
		setHeaderMenuByLeft(this);
		setHeaderMenuByCenterTitle(R.string.register_avatar);
		
		// 萌叔
		mGridAvatarF = (NestedGridView) findViewById(R.id.baseinfo_grid_avatar_f);
		// 萌妹
		mGridAvatarM = (NestedGridView) findViewById(R.id.baseinfo_grid_avatar_m);
		
		mFAvatarList = new ArrayList<String>();
		mMAvatarList = new ArrayList<String>();
		// Get Avatars
		mRegisterEventLogic.getAvatars();
		
		mGridAvatarF.setOnItemClickListener(this);
		mGridAvatarM.setOnItemClickListener(this);
	}

	
	@Override
	public void confirmNextAction() {
		Intent intent = new Intent(this, PurposeActivity.class);
		startActivity(intent);
	}

	// GridView Item Click
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Intent data = new Intent();
		switch (parent.getId()) {
			case R.id.baseinfo_grid_avatar_f:
				data.putExtra(RegisterBaseInfoActivity.CHOOSE_AVATAR, mFAvatarList.get(position));
				break;
			case R.id.baseinfo_grid_avatar_m:
				data.putExtra(RegisterBaseInfoActivity.CHOOSE_AVATAR, mMAvatarList.get(position));
				break;
			default:
				break;
		}
		
		setResult(RESULT_OK, data);
		// 待一秒返回
		mHandler.removeCallbacks(backRunnable);
		mHandler.postDelayed(backRunnable, 100);
	}
	
	public void onEventMainThread(Message msg) {
		
		switch (msg.what) {
			case R.id.register_get_avatar:
				if (msg.obj instanceof InfoResult) {
					InfoResult infoResult = (InfoResult) msg.obj;
					
					// 女人
					JSONObject jsonObject = (JSONObject) JSONObject.parse(infoResult.getResult());
					JSONArray mWmArray = jsonObject.getJSONObject(GlobalConstants.JSON_AVATARS).
							getJSONArray("0");
					for(int i=0; i < mWmArray.size(); i++) {
						mFAvatarList.add(mWmArray.getString(i));
					}
					
					// 男人
					JSONArray mMArray = jsonObject.getJSONObject(GlobalConstants.JSON_AVATARS).getJSONArray("1");
					for(int i=0; i < mMArray.size(); i++) {
						mMAvatarList.add(mMArray.getString(i));
					}
					
					mGridAvatarF.setAdapter(new VirtualAvatarAdapter(this, mFAvatarList, R.layout.item_grid_avatar));
					mGridAvatarM.setAdapter(new VirtualAvatarAdapter(this, mMAvatarList, R.layout.item_grid_avatar));
					
                } else if (msg.obj instanceof VolleyError) {
                    // 可提示网络错误，具体类型有TimeoutError ServerError
                    // Logger.w("TestActivity", (VolleyError)msg.obj);
                }
				break;
			default:
				break;
		}
	}
	
}
