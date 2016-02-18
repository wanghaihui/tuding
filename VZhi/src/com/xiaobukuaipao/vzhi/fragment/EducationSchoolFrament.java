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
 * 教育经历学校
 * 
 * @since 2015年01月13日17:15:45
 */
public class EducationSchoolFrament extends CallBackFragment {
	/**
	 * 
	 */
	private A5EditText mChooseSchool;
	/**
	 * 父布局
	 */
	private View mParentView;
	/**
	 * 
	 */
	private Bundle mBundle;
	/**
	 * 需要去完善的教育经历
	 */
	private Education mEducation;
    /**
     * suggest数据容器
     */
    private List<Suggest> mSchools;
	/**
	 * 
	 */
	private SuggestAdapter mAdapter;
	/**
	 * 
	 */
	private ListView mSchoolList;
	/**
	 * 访问请求序列化，通过随机数，给访问的网络请求标示，按照用户当前输入确定的字段来匹配请求结果
	 */
	private int seq;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		//初始化数据
		mBundle = getArguments();
		mEducation = (Education) mBundle.getParcelable(GlobalConstants.GUIDE_EDUCATION_OBJECT);
		mParentView = inflater.inflate(R.layout.fragment_school_education, container, false);
		mSchools = new ArrayList<Suggest>();
		return mParentView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		setHeaderMenuByLeft();
		setHeaderMenuByCenterTitle(R.string.register_edu_fill_school);
		setHeaderMenuByRight(R.string.general_tips_save).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						mEducation.getSchool().setName(mChooseSchool.getText().toString());
						onBackRequest.onBackData(mBundle);
					}
				});

		mChooseSchool = (A5EditText) mParentView.findViewById(R.id.auto_school_text);
		mAdapter = new SuggestAdapter(getActivity(),mSchools,R.layout.item_normal_suggest);
        mSchoolList = (ListView) mParentView.findViewById(R.id.school_list);
        mSchoolList.setAdapter(mAdapter);
        mSchoolList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				mEducation.setSchool(mSchools.get(position));
				mChooseSchool.setText(mEducation.getSchool().getName());
			}
		});
		if(mEducation.getSchool() != null){
			mChooseSchool.setText(StringUtils.isEmpty(mEducation.getSchool().getName()) ? "" :mEducation.getSchool().getName());
		}else{
			mEducation.setSchool(new Suggest());
		}
		
		mChooseSchool.addTextChangedListener(new TextWatcher() {
				
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
					//当输入长度为3的时候才开始加载suggest
					if(s.toString().getBytes().length >= 0){
						if(mSchools.size() > 0){
							if(!mSchools.contains(mEducation.getSchool())){
								seq = RandomUtils.getRandom(Integer.MAX_VALUE);
								mProfileEventLogic.getSchools(s.toString(), seq);
							}else{
								mEducation.setSchool(new Suggest("",s.toString()));
							}
						}else{
							seq =  RandomUtils.getRandom(Integer.MAX_VALUE);
							mProfileEventLogic.getSchools(s.toString(), seq);
						}	
					}else{
						mSchools.clear();
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
				JSONObject school = (JSONObject) JSONObject.parseObject(infoResult.getResult());
				if(school != null){
					JSONArray schoolArray = school.getJSONArray(GlobalConstants.JSON_SCHOOLS);
					if(schoolArray != null){
						mSchools.clear();
						for (int i = 0; i < schoolArray.size(); i++) {
							mSchools.add(new Suggest(schoolArray.getJSONObject(i)));
						}
						mAdapter.notifyDataSetChanged();
					}
				}
			}
		}
	}
}
