package com.xiaobukuaipao.vzhi.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.ListView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xiaobukuaipao.vzhi.R;
import com.xiaobukuaipao.vzhi.adapter.CommonAdapter;
import com.xiaobukuaipao.vzhi.adapter.ViewHolder;
import com.xiaobukuaipao.vzhi.event.InfoResult;
import com.xiaobukuaipao.vzhi.util.GlobalConstants;


public class JobObjectiveCorpTypeFragment extends CallBackFragment {
    private Bundle mBundle;
    private View view;
    private List<HashMap<String,String>> mCorpTypes;
	private ListView mListView;
	private CorpTypeAdapter mAdapter;
	private List<CorpType> mCorpTypesFromOutside;
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	mBundle = getArguments();
    	view =  inflater.inflate(R.layout.fragment_jobobjective_corp_type, container, false);
    	return view;
    }


    @SuppressWarnings("unchecked")
	@Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setHeaderMenuByLeft();
        setHeaderMenuByCenterTitle(R.string.register_jobexp_company);
		setHeaderMenuByRight(R.string.general_tips_save).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						onBackRequest.onBackData(mBundle);
					}
				});
		
		mCorpTypes = (List<HashMap<String, String>>) mBundle.getSerializable("intent_corptype");
		if(mCorpTypes == null){
			mCorpTypes = new ArrayList<HashMap<String,String>>();
		}
		
		mCorpTypesFromOutside = new ArrayList<CorpType>();
		
		mListView = (ListView) view.findViewById(R.id.intention_listview);
		
		mAdapter = new CorpTypeAdapter(getActivity(), mCorpTypesFromOutside, R.layout.item_intention_corptype);
		
		mListView.setAdapter(mAdapter);
		
		mRegisterEventLogic.getIntentionCorpTypes();
		
		
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				HashMap<String, String> map = new HashMap<String, String>();
				map.put(String.valueOf(mCorpTypesFromOutside.get(position).id), mCorpTypesFromOutside.get(position).name);
				if(mCorpTypesFromOutside.get(position).id == 0){
					mCorpTypes.clear();
					mCorpTypes.add(map);
					for (int i = 0; i < mCorpTypesFromOutside.size(); i++) {//其他选项选中
						mCorpTypesFromOutside.get(i).checked = false;
					}
					mCorpTypesFromOutside.get(position).checked = true;
				}else{
					for (int i = 0; i < mCorpTypes.size(); i++) {//把"全部"item剔除
						if(mCorpTypes.get(i).containsKey(String.valueOf(0))){
							mCorpTypes.remove(i);
							for (int j = 0; j < mCorpTypesFromOutside.size(); j++) {
								if(mCorpTypesFromOutside.get(j).id == 0){//置"全部"为false
									mCorpTypesFromOutside.get(j).checked = false;
									break;
								}
							}
							break;
						}
					}
					
					if(mCorpTypesFromOutside.get(position).checked){
						mCorpTypesFromOutside.get(position).checked = false;
						mCorpTypes.remove(map);
					}else{
						mCorpTypesFromOutside.get(position).checked = true;
						mCorpTypes.add(map);
					}
				}

				mAdapter.notifyDataSetChanged();
			}
		});
    }


	@Override
	public void onEventMainThread(Message msg) {
		if (msg.obj instanceof InfoResult) {
			InfoResult infoResult = (InfoResult) msg.obj;
			switch (msg.what) {
			case R.id.register_get_intention_corp_type:
				JSONObject corpObject = (JSONObject) JSONObject.parseObject(infoResult.getResult());
				if(corpObject != null){
					JSONArray corpArray = corpObject.getJSONArray(GlobalConstants.JSON_CORPTYPES);
					if(corpArray != null){
						mCorpTypesFromOutside.clear();
						for (int i = 0; i < corpArray.size(); i++) {
							JSONObject jsonObject = corpArray.getJSONObject(i);
							CorpType corpType = new CorpType(jsonObject);//初始化公司类型字典item
							for(HashMap<String, String> map : mCorpTypes){
								if(map.containsKey(String.valueOf(corpType.id))){//判断是否是已选项 
									corpType.checked = true;
								}
							}
							mCorpTypesFromOutside.add(corpType);
			
						}
						mAdapter.notifyDataSetChanged();
					}
				}
				break;
			}
		}
	}

	class CorpTypeAdapter extends CommonAdapter<CorpType>{

		public CorpTypeAdapter(Context mContext, List<CorpType> mDatas, int mItemLayoutId) {
			super(mContext, mDatas, mItemLayoutId);
		}

		@Override
		public void convert(ViewHolder viewHolder, CorpType item, int position) {
			viewHolder.setText(R.id.job_corptype, item.name);
			viewHolder.setText(R.id.job_corptype_desc, item.subname);
			CheckBox cbox = viewHolder.getView(R.id.job_corptype_select);
			cbox.setChecked(item.checked);

			viewHolder.getView(R.id.divider).setVisibility(position == getCount() - 1? View.INVISIBLE : View.VISIBLE);
		}
	}
	
	class CorpType{
		Integer id;
		String name;
		String subname;
		boolean checked;
		
		CorpType(JSONObject jsonObject){
			if(jsonObject != null){
				if(jsonObject.getInteger(GlobalConstants.JSON_ID) != null){
					id = jsonObject.getInteger(GlobalConstants.JSON_ID);
				}
				if(jsonObject.getString(GlobalConstants.JSON_NAME) != null){
					name = jsonObject.getString(GlobalConstants.JSON_NAME);
				}
				if(jsonObject.getString(GlobalConstants.JSON_SUBNAME) != null){
					subname = jsonObject.getString(GlobalConstants.JSON_SUBNAME);
				}
			}
			
		}
	}
}
