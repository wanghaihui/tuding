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
import com.xiaobukuaipao.vzhi.util.StringUtils;
import com.xiaobukuaipao.vzhi.widget.A5EditText;


/**
 * 开通招聘服务 公司信息
 * 
 * @since 2015年01月13日18:09:57
 */
public class HrCompanyFragment extends CallBackFragment {
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
    private List<Suggest> mCorpname;
    /**
     * 
     */
    private SuggestAdapter mAdapter;
	/**
	 * 
	 */
	private ListView mListview;
	/**
	 * 访问请求序列化，通过随机数，给访问的网络请求标示，按照用户当前输入确定的字段来匹配请求结果
	 */
	private int seq;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	mBundle = getArguments();
    	mParentView =  inflater.inflate(R.layout.fragment_job_company, container, false);
    	mCorpname = new ArrayList<Suggest>();
    	return mParentView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setHeaderMenuByLeft();
        setHeaderMenuByCenterTitle(R.string.register_jobexp_company);
		setHeaderMenuByRight(R.string.general_tips_save).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						mBundle.putString(GlobalConstants.NAME, mEditText.getText().toString());
						onBackRequest.onBackData(mBundle);
					}
				});
        mEditText= (A5EditText) getActivity().findViewById(R.id.edittext1);
    	if(mBundle != null && StringUtils.isNotEmpty(mBundle.getString(GlobalConstants.NAME))){
			mEditText.setText(mBundle.getString(GlobalConstants.NAME));
		}
        mEditText.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				//当输入长度为3的时候才开始加载suggest
				if(s.toString().getBytes().length >= 3){
				
					if(mCorpname.size() > 0){
						if(!mCorpname.contains(s.toString())){
							mProfileEventLogic.getCorpsFullname(s.toString(), seq);
						}
					}else{
						mProfileEventLogic.getCorpsFullname(s.toString(), seq);
					}
				}else{
					mCorpname.clear();
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
        
 		mAdapter = new SuggestAdapter(getActivity(),mCorpname,R.layout.item_normal_suggest);
 		mListview = (ListView) mParentView.findViewById(R.id.suggest);
 		mListview.setAdapter(mAdapter);
 		mListview.setOnItemClickListener(new OnItemClickListener() {
 			
 			@Override
 			public void onItemClick(AdapterView<?> parent, View view,
 					int position, long id) {
 				mEditText.setText(mCorpname.get(position).getName());
 			}
 		});  
    }


	@Override
	public void onEventMainThread(Message msg) {
		if (msg.obj instanceof InfoResult) {
			InfoResult infoResult = (InfoResult) msg.obj;
			if(seq == msg.what){
				JSONObject company = (JSONObject) JSONObject.parseObject(infoResult.getResult());
				if(company != null){
					JSONArray companyArray = company.getJSONArray(GlobalConstants.JSON_CORPS);
					if(companyArray != null){
						mCorpname.clear();
						for (int i = 0; i < companyArray.size(); i++) {
							mCorpname.add(new Suggest(companyArray.getJSONObject(i)));
						}
						mAdapter.notifyDataSetChanged();
					}
				}
			}
		}
	}
}
