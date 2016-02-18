package com.xiaobukuaipao.vzhi.fragment;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
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

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xiaobukuaipao.vzhi.R;
import com.xiaobukuaipao.vzhi.adapter.SuggestAdapter;
import com.xiaobukuaipao.vzhi.domain.user.Suggest;
import com.xiaobukuaipao.vzhi.event.InfoResult;
import com.xiaobukuaipao.vzhi.util.GlobalConstants;
import com.xiaobukuaipao.vzhi.util.RandomUtils;
import com.xiaobukuaipao.vzhi.util.StringUtils;
import com.xiaobukuaipao.vzhi.widget.A5EditText;

/**
 *	发布职位 职位名称
 *
 *	@since 2015年01月13日18:03:31
 */
public class PositionNameFragment extends CallBackFragment {
	/**
	 * 
	 */
	private A5EditText mEditText;
    /**
     * 
     */
    private Bundle mBundle;
    /**
     * 
     */
    private View mParentView;
    /**
     * 
     */
    private List<Suggest> mPositions;
    /**
     * 
     */
    private SuggestAdapter mAdapter;
	/**
	 * 
	 */
	private ListView mListview;
	/**
	 * 
	 */
	private String positionName;
	/**
	 * 访问请求序列化，通过随机数，给访问的网络请求标示，按照用户当前输入确定的字段来匹配请求结果
	 */
	private int seq;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	mBundle = getArguments();
    	positionName = mBundle.getString(GlobalConstants.FILL);
    	mParentView =  inflater.inflate(R.layout.fragment_job_name, container, false);
    	mPositions = new ArrayList<Suggest>();
    	return mParentView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHeaderMenuByLeft();
        setHeaderMenuByCenterTitle(R.string.register_jobexp_name);
		setHeaderMenuByRight(R.string.general_tips_save).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						mBundle.putString(GlobalConstants.FILL,  mEditText.getText().toString());
						onBackRequest.onBackData(mBundle);
					}
				});
		
        mEditText = (A5EditText) getActivity().findViewById(R.id.edittext1);

 		mAdapter = new SuggestAdapter(getActivity(),mPositions,R.layout.item_normal_suggest);
 		mListview = (ListView) mParentView.findViewById(R.id.suggest);
 		mListview.setAdapter(mAdapter);
 		mListview.setOnItemClickListener(new OnItemClickListener() {

 			@Override
 			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
 				mEditText.setText(mPositions.get(position).getName());
 			}
 		});  
 		mEditText.setText(StringUtils.isEmpty(positionName) ? "" : positionName);
 		
 		mEditText.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				//当输入长度为3的时候才开始加载suggest
				if(s.toString().getBytes().length >= 0){
					if(mPositions.size() > 0){
						if(!mPositions.contains(s.toString())){
							seq = RandomUtils.getRandom(Integer.MAX_VALUE);
							mProfileEventLogic.getPositions(s.toString(), seq);
						}
					}else{
						seq = RandomUtils.getRandom(Integer.MAX_VALUE);
						mProfileEventLogic.getPositions(s.toString(), seq);
					}
				}else{
					mPositions.clear();
					mAdapter.notifyDataSetChanged();
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				
			}
		});
 		seq = RandomUtils.getRandom(Integer.MAX_VALUE);
        mProfileEventLogic.getPositions("", seq);
    }


	@Override
	public void onEventMainThread(Message msg) {
		if (msg.obj instanceof InfoResult) {
			InfoResult infoResult = (InfoResult) msg.obj;
			if(seq == msg.what){
				JSONObject position = (JSONObject) JSONObject.parseObject(infoResult.getResult());
				if(position != null){
					JSONArray positionArray = position.getJSONArray(GlobalConstants.JSON_POSITIONS);
					if(positionArray != null){
						mPositions.clear();
						for (int i = 0; i < positionArray.size(); i++) {
							mPositions.add(new Suggest(positionArray.getJSONObject(i)));
						}
						mAdapter.notifyDataSetChanged();
					}
				}
			}
		}
	}
}