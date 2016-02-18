package com.xiaobukuaipao.vzhi.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckedTextView;
import android.widget.ListView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xiaobukuaipao.vzhi.R;
import com.xiaobukuaipao.vzhi.adapter.CommonAdapter;
import com.xiaobukuaipao.vzhi.adapter.ViewHolder;
import com.xiaobukuaipao.vzhi.event.InfoResult;
import com.xiaobukuaipao.vzhi.util.GlobalConstants;


public class JobObjectiveStateFragment extends CallBackFragment {
	
    private Bundle mBundle;
    private View view;
    private ListView mStateListView;
	// 行业单选--所以使用HashMap存储--由于要传递数据，所以使用HashMap，不用Map
	private HashMap<String, String> mSelectedMap;
	private List<HashMap<String, String>> mStateList;
	private StateAdapter mStateAdapter;
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	mBundle = getArguments();
    	view =  inflater.inflate(R.layout.fragment_jobobjective_industry_select, container, false);
    	return view;
    }

	
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setHeaderMenuByLeft();
        setHeaderMenuByCenterTitle(R.string.register_jobexp_company);
		initUIAndData();
    }

	
	@SuppressWarnings("unchecked")
	public void initUIAndData() {
		
        setHeaderMenuByCenterTitle(R.string.job_objective_jobstate);
        
        mStateListView = (ListView) view.findViewById(R.id.industry_list);
        
        mStateList = new ArrayList<HashMap<String, String>>();
        
        mSelectedMap = (HashMap<String, String>) mBundle.getSerializable("intent_jobstate");
        
        if (mSelectedMap == null) {
        	mSelectedMap = new HashMap<String, String>();
        }
        
        mRegisterEventLogic.getJobSeekerStates();
        
        mStateAdapter = new StateAdapter(getActivity(), mStateList, R.layout.item_suggest_selection);
        mStateListView.setAdapter(mStateAdapter);
        
        setUIListeners();
	}
	
	private void setUIListeners() {
		mStateListView.setOnItemClickListener(new OnItemClickListener() {

			@SuppressWarnings("unchecked")
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// 待定
    			mSelectedMap = (HashMap<String, String>) parent.getItemAtPosition(position);
				if (mSelectedMap.size() > 0) {
					mBundle.putSerializable("intent_jobstate", mSelectedMap);  
			        onBackRequest.onBackData(mBundle);
				}
				
			}
		});
	}

	@Override
	public void onEventMainThread(Message msg) {
		if (msg.obj instanceof InfoResult) {
			InfoResult infoResult = (InfoResult) msg.obj;
			switch (msg.what) {
				case R.id.register_get_jobstates:
					JSONObject jobStatesObject = (JSONObject) JSONObject.parseObject(infoResult.getResult());
					if (jobStatesObject != null) {
						JSONArray jobStatesArray = jobStatesObject.getJSONArray(GlobalConstants.JSON_JOBSEEKERSTATES);
						if (jobStatesArray != null) {
							for (int i = 0; i < jobStatesArray.size(); i++) {
								JSONObject jsonObject = jobStatesArray.getJSONObject(i);
								Integer id = jsonObject.getInteger(GlobalConstants.JSON_ID);
								String name = jsonObject.getString(GlobalConstants.JSON_NAME);
								HashMap<String, String> map = new HashMap<String, String>();
								map.put(String.valueOf(id), name);
								mStateList.add(map);
							}
							mStateAdapter.notifyDataSetChanged();
						}
					}
				break;
			}
		}
	}
	
	class StateAdapter extends CommonAdapter<HashMap<String, String>>{

		public StateAdapter(Context mContext,List<HashMap<String, String>> mDatas, int mItemLayoutId) {
			super(mContext, mDatas, mItemLayoutId);
		}

		@Override
		public void convert(ViewHolder viewHolder, HashMap<String, String> item, int position) {
			CheckedTextView checkedText = viewHolder.getView(android.R.id.text1);
			for(Map.Entry<String, String> entry : item.entrySet()) {
				checkedText.setText(entry.getValue());
			}
			if(item.equals(mSelectedMap)){
				checkedText.setChecked(true);
			}else{
				checkedText.setChecked(false);
			}
			
			if(position == getCount() -1){
				viewHolder.getView(R.id.divider).setVisibility(View.INVISIBLE);
			}else{
				viewHolder.getView(R.id.divider).setVisibility(View.VISIBLE);
			}
			
		}
		
	}
}
