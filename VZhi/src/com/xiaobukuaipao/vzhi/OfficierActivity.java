package com.xiaobukuaipao.vzhi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ListView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.xiaobukuaipao.vzhi.adapter.CommonAdapter;
import com.xiaobukuaipao.vzhi.adapter.ViewHolder;
import com.xiaobukuaipao.vzhi.event.InfoResult;
import com.xiaobukuaipao.vzhi.util.GlobalConstants;
import com.xiaobukuaipao.vzhi.util.VToast;
import com.xiaobukuaipao.vzhi.wrap.ProfileWrapActivity;

/**
 * 举报
 */
public class OfficierActivity extends ProfileWrapActivity {
	
	private ListView mReportListView;
	// 行业单选--所以使用HashMap存储--由于要传递数据，所以使用HashMap，不用Map
	private HashMap<String, String> mSelectedMap;
	private List<HashMap<String, String>> mOfficierList;
	
	private OfficierAdapter mOfficierAdapter;
	
	private EditText mAddingEditText;
	
	private String uid;
			
	@Override
	public void initUIAndData() {
		setContentView(R.layout.activity_officier);
		
		uid = getIntent().getStringExtra("report_id");
		
		setHeaderMenuByCenterTitle(R.string.officier_str);
		setHeaderMenuByLeft(this);
		setHeaderMenuByRight(R.string.general_post).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				String reason = "1";
				Iterator<Map.Entry<String, String>> iter = mSelectedMap.entrySet().iterator();  
				if (iter.hasNext()) {  
				    Map.Entry<String, String> entry = (Map.Entry<String, String>) iter.next();  
				    reason = (String) entry.getKey();  
				}  
				mProfileEventLogic.reportSomebody(uid, reason, mAddingEditText.getText().toString());
			}
			
		});
		
		mReportListView = (ListView) findViewById(R.id.officier_list);
		
		mAddingEditText = (EditText) findViewById(R.id.adding_text_id);
		
		mOfficierList = new ArrayList<HashMap<String, String>>();
		if (mSelectedMap == null) {
        	mSelectedMap = new HashMap<String, String>();
        }
		
		mProfileEventLogic.getOfficierReport();
        
		mOfficierAdapter = new OfficierAdapter(this, mOfficierList, R.layout.item_suggest_selection);
		mReportListView.setAdapter(mOfficierAdapter);
        
        setUIListeners();
	}
	
	private void setUIListeners() {
		mReportListView.setOnItemClickListener(new OnItemClickListener() {

			@SuppressWarnings("unchecked")
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// 待定
    			mSelectedMap = (HashMap<String, String>) parent.getItemAtPosition(position);
    			mOfficierAdapter.notifyDataSetChanged();
			}
		});
	}

	@Override
	public void onEventMainThread(Message msg) {
		if (msg.obj instanceof InfoResult) {
			InfoResult infoResult = (InfoResult) msg.obj;
			switch (msg.what) {
				case R.id.position_get_officier_report:
					JSONObject jsonObject = (JSONObject) JSONObject.parseObject(infoResult.getResult());
					if (jsonObject != null){
						JSONArray jsonArray = jsonObject.getJSONArray(GlobalConstants.JSON_OFFENCE_REPORT_REASONS);
						if(jsonArray != null){
							for (int i = 0; i < jsonArray.size(); i++) {
								JSONObject jsonItemObject = jsonArray.getJSONObject(i);
								Integer id = jsonItemObject.getInteger(GlobalConstants.JSON_ID);
								String name = jsonItemObject.getString(GlobalConstants.JSON_NAME);
								HashMap<String, String> map = new HashMap<String, String>();
								map.put(String.valueOf(id), name);
								mOfficierList.add(map);
							}
							mOfficierAdapter.notifyDataSetChanged();
						}
					}
					break;
					
				case R.id.position_add_offencereport:
					if (infoResult.getMessage().getStatus() == 0) {
						AppActivityManager.getInstance().finishActivity(OfficierActivity.this);
						// finish();
					}
					VToast.show(this, infoResult.getMessage().getMsg());
					break;
				default:
					break;
			}
		} else if (msg.obj instanceof VolleyError) {
			
		}
	}
	
	class OfficierAdapter extends CommonAdapter<HashMap<String, String>>{

		public OfficierAdapter(Context mContext,List<HashMap<String, String>> mDatas, int mItemLayoutId) {
			super(mContext, mDatas, mItemLayoutId);
		}

		@Override
		public void convert(ViewHolder viewHolder, HashMap<String, String> item, int position) {
			CheckedTextView checkedText = viewHolder.getView(android.R.id.text1);
			for(Map.Entry<String, String> entry : item.entrySet()) {
				checkedText.setText(entry.getValue());
			}
			checkedText.setChecked(item.equals(mSelectedMap));
			
			viewHolder.getView(R.id.divider).setVisibility(position == getCount() -1 ? View.INVISIBLE : View.VISIBLE);
			
		}
		
	}

}
