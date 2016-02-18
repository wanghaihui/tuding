package com.xiaobukuaipao.vzhi.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xiaobukuaipao.vzhi.R;
import com.xiaobukuaipao.vzhi.adapter.IndustryListViewAdapter;
import com.xiaobukuaipao.vzhi.event.InfoResult;
import com.xiaobukuaipao.vzhi.util.GlobalConstants;


public class JobObjectiveIndustryFragment extends CallBackFragment {
	
    private Bundle mBundle;
    private View view;
    private ListView mIndustryListView;
	// 行业单选--所以使用HashMap存储--由于要传递数据，所以使用HashMap，不用Map
	private HashMap<String, String> mSelectedMap;
	
	private List<HashMap<String, String>> mIndustryList;
	
	private IndustryListViewAdapter mIntentionListAdapter;
    
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
		
        setHeaderMenuByCenterTitle(R.string.job_objective_category);
        
        mIndustryListView = (ListView) view.findViewById(R.id.industry_list);
        
        mIndustryList = new ArrayList<HashMap<String, String>>();
        
        mSelectedMap = (HashMap<String, String>) mBundle.getSerializable("industry_selsect");
        if (mSelectedMap == null) {
        	mSelectedMap = new HashMap<String, String>();
        }
        mRegisterEventLogic.getIndustries();
        
        setUIListeners();
	}
	
	private void setUIListeners() {
		mIndustryListView.setOnItemClickListener(new OnItemClickListener() {

			@SuppressWarnings("unchecked")
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (IndustryListViewAdapter.isSelected.get(position)) {
		    		IndustryListViewAdapter.isSelected.put(position, false);
		    		mIntentionListAdapter.notifyDataSetChanged();
		    	} else {
		    		if (0 == IndustryListViewAdapter.checkSelect()) {
		    			IndustryListViewAdapter.isSelected.put(position, true);
		    			mIntentionListAdapter.notifyDataSetChanged();
		    			// 待定
		    			mSelectedMap = (HashMap<String, String>) parent.getItemAtPosition(position);
		    		}
		    	}
				if (mSelectedMap.size() > 0) {
					mBundle.putSerializable("industry_selsect_back", mSelectedMap);  
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
				case R.id.register_get_industry:
					JSONObject industryObject = (JSONObject) JSONObject.parse(infoResult.getResult());
					if(industryObject != null){
						JSONArray industryArray = industryObject.getJSONArray(GlobalConstants.JSON_INDUSTRIES);
						if(industryArray != null){
							for (int i = 0; i < industryArray.size(); i++) {
								HashMap<String, String> mMap = new HashMap<String, String>();
								mMap.put(industryArray.getJSONObject(i).getString(GlobalConstants.JSON_ID),industryArray.getJSONObject(i).getString(GlobalConstants.JSON_NAME));
								mIndustryList.add(mMap);
							}
						}
					}
					mIntentionListAdapter = new IndustryListViewAdapter(getActivity(), mIndustryList, mSelectedMap, R.layout.intention_pos_list_item);
					mIndustryListView.setAdapter(mIntentionListAdapter);
					
				break;
			}
		}
	}
}
