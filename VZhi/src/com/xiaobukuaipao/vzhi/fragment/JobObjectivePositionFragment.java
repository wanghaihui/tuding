package com.xiaobukuaipao.vzhi.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xiaobukuaipao.vzhi.R;
import com.xiaobukuaipao.vzhi.adapter.IntentionGridViewAdapter;
import com.xiaobukuaipao.vzhi.adapter.IntentionListViewAdapter;
import com.xiaobukuaipao.vzhi.event.InfoResult;
import com.xiaobukuaipao.vzhi.util.GlobalConstants;
import com.xiaobukuaipao.vzhi.util.StringUtils;
import com.xiaobukuaipao.vzhi.widget.A5EditText;


public class JobObjectivePositionFragment extends CallBackFragment {

    public static final int LISTVIEW_CHECK = 1;
	public static final int LISTVIEW_UNCHECK = 2;
	public static final int GRIDVIEW_DELETE = 3;
	
	private static TextView mSelectedNumView;
	
	private A5EditText mEditSuggest;
	
	private ListView mJobIntentView;
	private ListView mJobListView;
	
	private static IntentionGridViewAdapter mIntentionGridAdapter;
	private static IntentionListViewAdapter mIntentionListAdapter;
	
    private Bundle mBundle;
    private View view;
	
	// GridView中的五个元素 
	//改为三个元素　网格改为列表　匹配容器　
	private static ArrayList<HashMap<String, String>> mIntentPosList;
	private static ArrayList<HashMap<String, String>> mSuggestList;
	
	
	public static Handler mHandler = new Handler() {
		int position = 0;
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			
			switch (msg.what) {
				case LISTVIEW_CHECK:
					if (mIntentPosList.size() < 5) {
						mIntentPosList.add((HashMap<String, String>) msg.obj);
					}
					
					mIntentionGridAdapter.notifyDataSetChanged();
					updateData();
					break;
				case LISTVIEW_UNCHECK:
					position = 0;
					if (mIntentPosList.size() > 0 && mIntentPosList.size() <= IntentionListViewAdapter.MAX_CHECK) {
						for(int i=0; i < mIntentPosList.size(); i++) {
							for (Map.Entry<String, String> entry : mIntentPosList.get(i).entrySet()) {
								if (((HashMap<String, String>) msg.obj).containsKey(entry.getKey())) {
									position = i;
								}
						    }
						}
						mIntentPosList.remove(position);
					}
					mIntentionGridAdapter.notifyDataSetChanged();
					updateData();
					break;
				case GRIDVIEW_DELETE:
					position = 0;
					if (mIntentPosList.size() > 0 && mIntentPosList.size() <= IntentionListViewAdapter.MAX_CHECK) {
						for(int i=0; i < mIntentPosList.size(); i++) {
							for (Map.Entry<String, String> entry : mIntentPosList.get(i).entrySet()) {
								if (((HashMap<String, String>) msg.obj).containsKey(entry.getKey())) {
									position = i;
								}
						    }
						}
						mIntentPosList.remove(position);
					}
					mIntentionGridAdapter.notifyDataSetChanged();
					
					int mSuggestPosition = 0;
					for(int i=0; i < mSuggestList.size(); i++) {
						for (Map.Entry<String, String> entry : mSuggestList.get(i).entrySet()) {
							if (((HashMap<String, String>) msg.obj).containsKey(entry.getKey())) {
								mSuggestPosition = i;
							}
					    }
					}
					
					if (IntentionListViewAdapter.isSelected.get(mSuggestPosition)) {
						IntentionListViewAdapter.isSelected.put(mSuggestPosition, false);
						IntentionListViewAdapter.currentCheck--;
					}
					// 更新Suggest List
					mIntentionListAdapter.notifyDataSetChanged();
					
					updateData();
					break;
			}
		}
	};
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	mBundle = getArguments();
    	view =  inflater.inflate(R.layout.fragment_jobobjective_intention_pos, container, false);
    	return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    	setHeaderMenuByLeft();
        setHeaderMenuByCenterTitle(R.string.job_objective_intention_pos);
		setHeaderMenuByRight(R.string.general_tips_save).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
				        mBundle.putSerializable("intent_pos_back", mIntentPosList);  
						onBackRequest.onBackData(mBundle);
					}
				});
		initUIAndData();
    }

    @SuppressWarnings("unchecked")
	public void initUIAndData() {
        mEditSuggest = (A5EditText) view.findViewById(R.id.job_intent_suggest);
        
        mJobIntentView = (ListView) view.findViewById(R.id.intention_view);
		mJobListView = (ListView) view.findViewById(R.id.intention_listview);
		
		mIntentPosList = (ArrayList<HashMap<String, String>>) mBundle.getSerializable("intent_pos");
		
		mSuggestList = new ArrayList<HashMap<String, String>>();
		
		mSelectedNumView = (TextView) view.findViewById(R.id.selected_pos_num);
        mSelectedNumView.setText("" + mIntentPosList.size());
		
		// 执行网络请求
		mRegisterEventLogic.getIntentPosition("");
		
		mIntentionGridAdapter = new IntentionGridViewAdapter (getActivity(), mIntentPosList, R.layout.intention_pos_item);
		
		mJobIntentView.setAdapter(mIntentionGridAdapter);
		
		setUIListeners();
	}
	
	// 静态函数--更新数据
	private static void updateData() {
		mSelectedNumView.setText(" " + mIntentPosList.size());
	}

	private TextWatcher mSuggestTextWatcher = new TextWatcher() {
	      @Override
	      public void beforeTextChanged(CharSequence s, int start, int count, int after) {
	          
	      }
		          
		   @Override
		   public void onTextChanged(CharSequence s, int start, int before, int count) {
			   if(s.length() > 24){
				   mEditSuggest.setSelection(s.length());
			   }else{
				   mRegisterEventLogic.getIntentPosition(s.toString());
			   }
		   }
		   @Override
		   public void afterTextChanged(Editable s) {
			   
		   }
	 };
	
	private void setUIListeners() {
		// mEditSuggest.setLongClickable(false);
		// EditText Suggest
		// 添加监听器
		mEditSuggest.addTextChangedListener(mSuggestTextWatcher);
		
		mJobListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		
		mJobListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// 此时让EditText失去焦点
				// mEditSuggest.setFocusable(false);
				// 3
				if (IntentionListViewAdapter.currentCheck == IntentionListViewAdapter.MAX_CHECK) {
						if (IntentionListViewAdapter.isSelected.get(position)) {
							IntentionListViewAdapter.isSelected.put(position, false);
							IntentionListViewAdapter.currentCheck--;
							mIntentionListAdapter.notifyDataSetChanged();
							
							Message msg = Message.obtain();
							msg.what = LISTVIEW_UNCHECK;
							msg.obj = parent.getItemAtPosition(position);
							mHandler.sendMessage(msg);
						}
				} else if (IntentionListViewAdapter.currentCheck < IntentionListViewAdapter.MAX_CHECK) {
					
					if (IntentionListViewAdapter.isSelected.get(position)) {
						IntentionListViewAdapter.isSelected.put(position, false);
						IntentionListViewAdapter.currentCheck--;
						mIntentionListAdapter.notifyDataSetChanged();
						
						Message msg = Message.obtain();
						msg.what = LISTVIEW_UNCHECK;
						msg.obj = parent.getItemAtPosition(position);
						mHandler.sendMessage(msg);
					} else {
						IntentionListViewAdapter.isSelected.put(position, true);
						IntentionListViewAdapter.currentCheck++;
						mIntentionListAdapter.notifyDataSetChanged();
						
						Message msg = Message.obtain();
						msg.what = LISTVIEW_CHECK;
						msg.obj = parent.getItemAtPosition(position);
						mHandler.sendMessage(msg);
					}
					
				}
				
			}
		});
	}
	
	// 执行网络返回请求
	@Override
	public void onEventMainThread(Message msg) {
		if (msg.obj instanceof InfoResult) {
			InfoResult infoResult = (InfoResult) msg.obj;
			switch (msg.what) {
				case R.id.register_get_intent_pos:
					
					// 由于要多次用到，所以每次进来时都要清空一下
					mSuggestList.clear();
					String edit = mEditSuggest.getText().toString();
					boolean hasKeyword = false;
					
					JSONObject jsonObject = (JSONObject) JSONObject.parse(infoResult.getResult());
					
					if(jsonObject != null){
						JSONArray jsonArray = jsonObject.getJSONArray(GlobalConstants.JSON_POSITIONS);
						if(jsonArray != null){
							for(int i=0; i < jsonArray.size(); i++) {
								HashMap<String, String> mPosition = new HashMap<String, String>();
								mPosition.put(jsonArray.getJSONObject(i).getString(GlobalConstants.JSON_ID), jsonArray.getJSONObject(i).getString(GlobalConstants.JSON_NAME));
								mSuggestList.add(mPosition);
								if(edit.equals(jsonArray.getJSONObject(i).getString(GlobalConstants.JSON_NAME))){
									hasKeyword = true;
								}
							}
						}
					}

					if(StringUtils.isNotEmpty(edit) && (!hasKeyword || mSuggestList.isEmpty())){
						HashMap<String,String> hashMap = new HashMap<String, String>();
						hashMap.put(edit, edit);
						mSuggestList.add(0, hashMap);
					}
					// 给ListView赋值
					mIntentionListAdapter = new IntentionListViewAdapter(getActivity(), mSuggestList, mIntentPosList, R.layout.intention_pos_list_item);
					mJobListView.setAdapter(mIntentionListAdapter);
					mIntentionListAdapter.notifyDataSetChanged();
					// 焦点问题
					// mJobListView.requestFocusFromTouch();
					
					break;
			}
		}
	}
}
