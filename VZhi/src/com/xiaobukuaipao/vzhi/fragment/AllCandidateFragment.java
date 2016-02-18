package com.xiaobukuaipao.vzhi.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.xiaobukuaipao.vzhi.R;
import com.xiaobukuaipao.vzhi.adapter.AllContactCandidateAdapter;
import com.xiaobukuaipao.vzhi.domain.CandidateInfo;
import com.xiaobukuaipao.vzhi.domain.JobInfo;
import com.xiaobukuaipao.vzhi.event.CandidateEvent;
import com.xiaobukuaipao.vzhi.event.InfoResult;
import com.xiaobukuaipao.vzhi.fragment.UndisposedCandidateFragment.AllAndContactedStatusListener;
import com.xiaobukuaipao.vzhi.util.GlobalConstants;
import com.xiaobukuaipao.vzhi.util.Logcat;
import com.xiaobukuaipao.vzhi.view.ProgressBarCircularIndeterminate;
import com.xiaobukuaipao.vzhi.view.refresh.PullToRefreshBase;
import com.xiaobukuaipao.vzhi.view.refresh.PullToRefreshBase.Mode;
import com.xiaobukuaipao.vzhi.view.refresh.PullToRefreshBase.OnRefreshListener2;
import com.xiaobukuaipao.vzhi.view.refresh.PullToRefreshListView;

public class AllCandidateFragment extends PositionWrapFragment implements AllAndContactedStatusListener {
	public static final String TAG = AllCandidateFragment.class.getSimpleName();
	
	private View view;
	
	private PullToRefreshListView mPullListView;
	private List<CandidateInfo> mAllCandidateList;
	private AllContactCandidateAdapter mAllCandidateAdapter;
	// 当前的位置
	private Integer actualPosition;
	private ArrayList<JobInfo> mOpenedPositions;
	
	// 是否上拉刷新
	private boolean isPullUpRefresh = false;
	
	private int minapplyid = -1;

	private TextView mEmptyView;
	
	private ProgressBarCircularIndeterminate mLoading;

	private Handler handler;
	
	private Runnable runnable = new Runnable() {
		
		@Override
		public void run() {
			if(mOpenedPositions != null && !mOpenedPositions.isEmpty() && actualPosition != null )
				mPositionEventLogic.getAllCandidate(mOpenedPositions.get(actualPosition).getId(), String.valueOf(minapplyid));
		}
	};
	
	public AllCandidateFragment() {
		
	}
	
	public AllCandidateFragment(Activity activity) {
		this.activity = activity;
	}
	
	public AllCandidateFragment(Integer actualPosition, ArrayList<JobInfo> mOpenedPositions) {
		this.actualPosition = actualPosition;
		this.mOpenedPositions = mOpenedPositions;
		
		Log.i(TAG, "all actual position " + this.actualPosition);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if (savedInstanceState != null) {
			return;
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_allcontact_candidate, container, false);
        this.view = view;
        return view;
	}
	
	

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initUIAndData();
	}

	
	@Override
	protected void initUIAndData() {
		mPullListView = (PullToRefreshListView) view.findViewById(R.id.all_candidate_list);
		mPullListView.setMode(Mode.PULL_FROM_END);
		mAllCandidateList = new ArrayList<CandidateInfo>();
		
		mAllCandidateAdapter = new AllContactCandidateAdapter(AllCandidateFragment.this.getActivity(), mAllCandidateList, R.layout.allcontact_candidate_item);
		mPullListView.setAdapter(mAllCandidateAdapter);
		
		if(mOpenedPositions != null && !mOpenedPositions.isEmpty() && actualPosition != null )
			mPositionEventLogic.getAllCandidate(mOpenedPositions.get(actualPosition).getId(), String.valueOf(minapplyid));
		
		mPullListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
			}
			
			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				isPullUpRefresh = true;
				if(mOpenedPositions != null && !mOpenedPositions.isEmpty() && actualPosition != null )
					mPositionEventLogic.getAllCandidate(mOpenedPositions.get(actualPosition).getId(), String.valueOf(minapplyid));
			}
			
		});
		mEmptyView = (TextView) view.findViewById(R.id.emptyTv);
		mLoading = (ProgressBarCircularIndeterminate) view.findViewById(R.id.loading);
		mPullListView.setEmptyView(view.findViewById(R.id.empty));
		mEmptyView.setVisibility(View.GONE);
		handler = new Handler();
		
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onEventMainThread(Message msg) {
		if (msg.obj instanceof InfoResult) {
			InfoResult infoResult = (InfoResult) msg.obj;
			switch (msg.what) {
				// 统计候选人信息
				case R.id.position_all_candidate:
					mLoading.setVisibility(View.GONE);
					mEmptyView.setVisibility(View.VISIBLE);
					mEmptyView.setOnClickListener(null);
					mEmptyView.setText(R.string.candidate_has_nocard);
					// 此时返回JSON数据
					JSONObject mJSONResult = (JSONObject) JSONObject.parse(infoResult.getResult());
					if(mJSONResult != null){
						JSONArray mDataArray = mJSONResult.getJSONArray(GlobalConstants.JSON_DATA);
						if(mDataArray != null){
							if (!isPullUpRefresh) {
								mAllCandidateList.clear();
							}
							if (mDataArray.size() > 0) {
								for(int i=0; i < mDataArray.size(); i++) {
									mAllCandidateList.add(new CandidateInfo(mDataArray.getJSONObject(i)));
								}
							}
							mAllCandidateAdapter.notifyDataSetChanged();
						}
						
						if(mJSONResult.getInteger(GlobalConstants.JSON_MINAPPLYID) != null){
							minapplyid = mJSONResult.getInteger(GlobalConstants.JSON_MINAPPLYID);
							
							Log.i(TAG, "minapplyid : " + minapplyid);
						}
						
						if (isPullUpRefresh) {
							isPullUpRefresh = false;
							mPullListView.onRefreshComplete();
						}
						
						if(minapplyid == 0){
							mPullListView.setMode(Mode.DISABLED);
						}
					}
				
					break;
			}
		}else if (msg.obj instanceof VolleyError) {
			mLoading.setVisibility(View.GONE);
			mEmptyView.setVisibility(View.VISIBLE);
			mEmptyView.setText("网络访问失败,点击重试...");
			mEmptyView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					handler.postDelayed(runnable, 500);
					mEmptyView.setVisibility(View.GONE);
					mLoading.setVisibility(View.VISIBLE);
				}
			});
		}
	}

	
	// 此时，执行下拉刷新
	@Override
	public void onChangeStatusListener() {
		mAllCandidateList.clear();
		
		mPositionEventLogic.getAllCandidate(mOpenedPositions.get(actualPosition).getId(), String.valueOf(-1));
	}
	
	/**
	 * 
	 */
	public void onEvent(CandidateEvent candidate){
		if(candidate != null){
			for(CandidateInfo info : mAllCandidateList){
				if(info.getApplyjob().getString(GlobalConstants.JSON_ID) != null && info.getApplyjob().getString(GlobalConstants.JSON_ID).equals(candidate.getJid())
						&& info.getBasic().getString(GlobalConstants.JSON_ID) != null && info.getBasic().getString(GlobalConstants.JSON_ID).equals(candidate.getUid())){
					
					info.setContactstatus(candidate.getContactStatus());
					info.setReadstatus(candidate.getContactStatus());
					break;
				}
			}	
		}

		mAllCandidateAdapter.notifyDataSetChanged();
		Logcat.d("@@@", "contected candidate jid: " + candidate.getJid() + "  contected status:" + candidate.getContactStatus() );
	}
}
