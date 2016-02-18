package com.xiaobukuaipao.vzhi.fragment;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.xiaobukuaipao.vzhi.R;
import com.xiaobukuaipao.vzhi.adapter.JobPositionStringAdapter;
import com.xiaobukuaipao.vzhi.domain.JobInfo;

/**
 * 候选人接收箱界面的左侧侧滑栏
 * @author xiaobu
 */
public class BrowserCandidateFragment extends Fragment {
	
	public BrowserCandidateListener mBrowserCandidateListener;
	
	private View view;
	
	// 开启的职位列表
	private ListView mPositionListView;
	private JobPositionStringAdapter mAdapter;
	
	// 当前的Position
	private int mJobPosition;
	// 传过来的所有开启的职位列表
	private ArrayList<JobInfo> mOpenedPositions;
	
	private HashMap<Integer, Boolean> isSelected;
	
	// 构造函数
	public BrowserCandidateFragment() {
		
	}
	// 构造函数--两个
	public BrowserCandidateFragment(int jobPosition, ArrayList<JobInfo> mOpenedPositions) {
		this.mJobPosition = jobPosition;
		this.mOpenedPositions = mOpenedPositions;
	}
	
	// 先Attach Activity
	@Override
	public void onAttach(Activity activity) {
		if (!(activity instanceof BrowserCandidateListener)) {
			throw new ClassCastException();
		}
		mBrowserCandidateListener = (BrowserCandidateListener) activity;
		super.onAttach(activity);
	}
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.browser_candidate, container, false);
		return view;
	}
	
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initUIAndData();
		setUIListeners();
	}
	
	private void initUIAndData() {
		mPositionListView = (ListView) view.findViewById(R.id.position_listview);
		isSelected = new HashMap<Integer, Boolean>();
		// 设置显示数据的Adapter
        mAdapter = new JobPositionStringAdapter(this.getActivity(), mOpenedPositions, R.layout.job_pos_list_item, mJobPosition, isSelected);
        mPositionListView.setAdapter(mAdapter);
	}
	
	private void setUIListeners() {
		mPositionListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				JobInfo selectedJobInfo = (JobInfo) parent.getItemAtPosition(position);

				isSelected.clear();
				for(int i=0; i < mOpenedPositions.size(); i++) {
					if (i == position) {
						isSelected.put(position, true);
					} else {
						isSelected.put(i, false);
					}
				}
				mJobPosition = position;
				mAdapter.notifyDataSetChanged();
				
				// 将JobInfo数据传到外层的CandidateSelectActivity中
				mBrowserCandidateListener.onChangePositionListener(selectedJobInfo, position);
			}
			
		});
	}
	
	/*public void setBrowserCandidateListener(BrowserCandidateListener mBrowserCandidateListener) {
		this.mBrowserCandidateListener = mBrowserCandidateListener;
	}*/

	// 侧边栏点击监听器
	public interface BrowserCandidateListener {
		// 传入JobInfo的id
		public void onChangePositionListener(JobInfo mJobInfo, Integer position);
	}
}
