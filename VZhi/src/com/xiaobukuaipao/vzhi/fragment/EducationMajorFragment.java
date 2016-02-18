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
import com.xiaobukuaipao.vzhi.domain.user.Education;
import com.xiaobukuaipao.vzhi.domain.user.Suggest;
import com.xiaobukuaipao.vzhi.event.InfoResult;
import com.xiaobukuaipao.vzhi.util.GlobalConstants;
import com.xiaobukuaipao.vzhi.util.RandomUtils;
import com.xiaobukuaipao.vzhi.util.StringUtils;
import com.xiaobukuaipao.vzhi.widget.A5EditText;

/**
 * 教育经历专业
 * 
 * @since 2015年01月13日17:24:30
 */
public class EducationMajorFragment extends CallBackFragment {

    /**
     * 
     */
    private A5EditText mChooseMajor;
    /**
     * 
     */
    private View mParentView;
	/**
	 * 
	 */
	private Bundle mBundle;
	/**
	 * 
	 */
	private Education mEducation;
    /**
     * 
     */
    private List<Suggest> mMajors;
    /**
     * 
     */
    private SuggestAdapter mAdapter;
	/**
	 * 
	 */
	private ListView mMajorList;
	/**
	 * 访问请求序列化，通过随机数，给访问的网络请求标示，按照用户当前输入确定的字段来匹配请求结果
	 */
	private int seq;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mBundle = getArguments();
		mEducation = (Education) mBundle.getParcelable(GlobalConstants.GUIDE_EDUCATION_OBJECT);
		mParentView = inflater.inflate(R.layout.fragment_major_education,container, false);
		mMajors = new ArrayList<Suggest>();
		return mParentView;
	}


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setHeaderMenuByLeft();
        setHeaderMenuByCenterTitle(R.string.register_edu_fill_major);
		setHeaderMenuByRight(R.string.general_tips_save).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						mEducation.getMajor().setName(mChooseMajor.getText().toString());
						onBackRequest.onBackData(mBundle);
					}
				});

		mChooseMajor = (A5EditText) mParentView.findViewById(R.id.auto_major_text);
		mAdapter = new SuggestAdapter(getActivity(),mMajors,R.layout.item_normal_suggest);
        mMajorList = (ListView) mParentView.findViewById(R.id.major_list);
        mMajorList.setAdapter(mAdapter);
        mMajorList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				mEducation.setMajor(mMajors.get(position));
				mChooseMajor.setText(mEducation.getMajor().getName());
				mEducation.getMajor().setId(mMajors.get(position).getId());//
			}
		});
        
        if(mEducation.getMajor() != null){
        	mChooseMajor.setText(StringUtils.isEmpty(mEducation.getMajor().getName()) ? "" : mEducation.getMajor().getName());
        }else{
			mEducation.setMajor(new Suggest());
		}
        
        mChooseMajor.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				//当输入长度为3的时候才开始加载suggest
				if(s.toString().getBytes().length >= 0){
					if(mMajors.size() > 0){
						if(!mMajors.contains(mEducation.getMajor())){
							seq = RandomUtils.getRandom(Integer.MAX_VALUE);
							mProfileEventLogic.getMajors(s.toString(), seq);
						}else{
							mEducation.setMajor(new Suggest("",s.toString()));
						}
					}else{
						seq = RandomUtils.getRandom(Integer.MAX_VALUE);
						mProfileEventLogic.getMajors(s.toString(), seq);
					}
				}else{
					mMajors.clear();
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
        
        
    }


	@Override
	public void onEventMainThread(Message msg) {
		if (msg.obj instanceof InfoResult) {
			InfoResult infoResult = (InfoResult) msg.obj;
			if(seq == msg.what){
				JSONObject major = (JSONObject) JSONObject.parseObject(infoResult.getResult());
				if(major != null){
					JSONArray majorArray = major.getJSONArray(GlobalConstants.JSON_MAJORS);
					if(majorArray != null){
						mMajors.clear();
						for (int i = 0; i < majorArray.size(); i++) {
							mMajors.add(new Suggest(majorArray.getJSONObject(i)));
						}
						mAdapter.notifyDataSetChanged();
					}
				}
			}
		}
	}
}
