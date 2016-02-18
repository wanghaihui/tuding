package com.xiaobukuaipao.vzhi.fragment;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
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
import com.xiaobukuaipao.vzhi.adapter.CommonAdapter;
import com.xiaobukuaipao.vzhi.adapter.ViewHolder;
import com.xiaobukuaipao.vzhi.domain.user.Education;
import com.xiaobukuaipao.vzhi.domain.user.Suggest;
import com.xiaobukuaipao.vzhi.event.InfoResult;
import com.xiaobukuaipao.vzhi.util.GlobalConstants;
import com.xiaobukuaipao.vzhi.widget.FormItemByLineLayout;
@SuppressLint("NewApi")
public class EducationDegreeFragment extends CallBackFragment implements
		OnItemClickListener {
	private View view;
	private ListView mDegrees;
	private Bundle mBundle;
	private Education mEducation;
	private List<Suggest> degrees;
	private String choosedDegree;
	private DegreeAdapter adapter;
	
	private boolean[] mChecks;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mBundle = getArguments();
		mEducation = (Education) mBundle.getParcelable(GlobalConstants.GUIDE_EDUCATION_OBJECT);
		view = inflater.inflate(R.layout.fragment_backgroud_education,
				container, false);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setHeaderMenuByLeft();
		setHeaderMenuByCenterTitle(R.string.register_edu_choose_degree);
		mDegrees = (ListView) view.findViewById(R.id.register_edu_list);
		
		mProfileEventLogic.getDegree();
		mDegrees.setOnItemClickListener(this);
		degrees = new ArrayList<Suggest>();
		adapter = new DegreeAdapter(getActivity().getApplicationContext(), degrees,R.layout.item_edu_degree); 
		mDegrees.setAdapter(adapter);
	}

	@Override
	public void onEventMainThread(Message msg) {
		if (msg.obj instanceof InfoResult) {
			InfoResult infoResult = (InfoResult) msg.obj;
			switch (msg.what) {
			case R.id.register_get_degree:
				JSONObject degree = (JSONObject) JSONObject.parseObject(infoResult.getResult());
				JSONArray degreeArray = degree.getJSONArray(GlobalConstants.JSON_DEGREES);
				degrees.clear();
				for (int i = 0; i < degreeArray.size(); i++) {
					JSONObject jsonObject = degreeArray.getJSONObject(i);
					Suggest suggest = new Suggest(jsonObject);
					degrees.add(suggest);
				}
				mChecks = new boolean[degrees.size()];
				if(mEducation.getDegree() != null){
					choosedDegree = mEducation.getDegree().getName();
				}
				
				for(int i = 0; i < mChecks.length ; i ++ ){
					if(degrees.get(i).getName().equals(choosedDegree)){
						mChecks[i] = true;
					}
				}
				
				adapter.notifyDataSetChanged();
				
				break;
			}
		}
	}


	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		
		for(int i = 0; i < mChecks.length ; i ++ ){
			mChecks[i] = false;
		}
		mChecks[position] = true;
		mEducation.setDegree(degrees.get(position));
		adapter.notifyDataSetChanged();
		onBackRequest.onBackData(mBundle);
	}
	
	public class DegreeAdapter extends CommonAdapter<Suggest> {

		public DegreeAdapter(Context mContext, List<Suggest> mDatas,
				int mItemLayoutId) {
			super(mContext, mDatas, mItemLayoutId);
		}

		@Override
		public void convert(final ViewHolder viewHolder, final Suggest item,
				final int position) {
			FormItemByLineLayout convertView = (FormItemByLineLayout) viewHolder.getConvertView();
			convertView.setFormLabel(item.getName());
			convertView.getCheckBox().setChecked(mChecks[position]);
		}

	}
}
