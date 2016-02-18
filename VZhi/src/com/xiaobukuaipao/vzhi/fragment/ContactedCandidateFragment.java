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
import com.xiaobukuaipao.vzhi.adapter.ContactedCandidateAdapter;
import com.xiaobukuaipao.vzhi.domain.CandidateInfo;
import com.xiaobukuaipao.vzhi.domain.JobInfo;
import com.xiaobukuaipao.vzhi.event.CandidateEvent;
import com.xiaobukuaipao.vzhi.event.InfoResult;
import com.xiaobukuaipao.vzhi.util.GlobalConstants;
import com.xiaobukuaipao.vzhi.util.Logcat;
import com.xiaobukuaipao.vzhi.view.ProgressBarCircularIndeterminate;
import com.xiaobukuaipao.vzhi.view.refresh.PullToRefreshBase;
import com.xiaobukuaipao.vzhi.view.refresh.PullToRefreshBase.Mode;
import com.xiaobukuaipao.vzhi.view.refresh.PullToRefreshBase.OnRefreshListener2;
import com.xiaobukuaipao.vzhi.view.refresh.PullToRefreshListView;

public class ContactedCandidateFragment extends PositionWrapFragment {
	private static final String TAG = ContactedCandidateFragment.class.getSimpleName();
	
	private View view;
	
	private PullToRefreshListView mPullListView;
	private List<CandidateInfo> mContactedCandidateList;
	private ContactedCandidateAdapter mContactedCandidateAdapter;
	// 当前的位置
	private Integer actualPosition;
	private ArrayList<JobInfo> mOpenedPositions;
	
	// 是否上拉刷新
	private boolean isPullUpRefresh = false;
	
	private final int defaultMinapplyid = -1;
	
	private int minapplyid = defaultMinapplyid;
	
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
	
	
	public ContactedCandidateFragment() {
		
	}
	
	public ContactedCandidateFragment(Activity activity) {
		this.activity = activity;
	}

	public ContactedCandidateFragment(Integer actualPosition, ArrayList<JobInfo> mOpenedPositions) {
		this.actualPosition = actualPosition;
		this.mOpenedPositions = mOpenedPositions;
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
		mContactedCandidateList = new ArrayList<CandidateInfo>();
		
		mContactedCandidateAdapter = new ContactedCandidateAdapter(ContactedCandidateFragment.this.getActivity(), mContactedCandidateList, R.layout.contacted_candidate_item);
		mPullListView.setAdapter(mContactedCandidateAdapter);
		if(mOpenedPositions != null && !mOpenedPositions.isEmpty() && actualPosition != null )
		// 得到当前的未处理的候选人
			mPositionEventLogic.getContactedCandidate(mOpenedPositions.get(actualPosition).getId(), String.valueOf(defaultMinapplyid));
		mPullListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				isPullUpRefresh = true;
				if(mOpenedPositions != null && !mOpenedPositions.isEmpty() && actualPosition != null )
					mPositionEventLogic.getContactedCandidate(mOpenedPositions.get(actualPosition).getId(), String.valueOf(minapplyid));
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
				case R.id.position_contacted_candidate:
					mLoading.setVisibility(View.GONE);
					mEmptyView.setVisibility(View.VISIBLE);
					mEmptyView.setOnClickListener(null);
					mEmptyView.setText(R.string.candidate_has_nocard);
					Log.i(TAG, infoResult.getResult());
					// 此时返回JSON数据
					JSONObject mJSONResult = (JSONObject) JSONObject.parse(infoResult.getResult());
					if (mJSONResult != null) {
						JSONArray mDataArray = mJSONResult.getJSONArray(GlobalConstants.JSON_DATA);
						if(mDataArray != null){
							if (!isPullUpRefresh) {
								mContactedCandidateList.clear();
							}
							if (mDataArray.size() > 0) {
								for(int i=0; i < mDataArray.size(); i++) {
									mContactedCandidateList.add(new CandidateInfo(mDataArray.getJSONObject(i)));
								}
							}
							mContactedCandidateAdapter.notifyDataSetChanged();
						}
						if(mJSONResult.getInteger(GlobalConstants.JSON_MINAPPLYID) != null){
							minapplyid = mJSONResult.getInteger(GlobalConstants.JSON_MINAPPLYID);
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
					mEmptyView.setVisibility(View.GONE);
					mLoading.setVisibility(View.VISIBLE);
					handler.postDelayed(runnable, 500);
				}
			});
		}
	}
	
	/**
	 * 通过eventbus下发的信息来刷新
	 */
	public void onEvent(CandidateEvent candidate){
		boolean flag = false;//在列表中不存在已联系
		if(candidate != null){
			for(CandidateInfo info : mContactedCandidateList){
				if(info.getApplyjob().getString(GlobalConstants.JSON_ID) != null && info.getApplyjob().getString(GlobalConstants.JSON_ID).equals(candidate.getJid())
						&& info.getBasic().getString(GlobalConstants.JSON_ID) != null && info.getBasic().getString(GlobalConstants.JSON_ID).equals(candidate.getUid())){
					info.setContactstatus(candidate.getContactStatus());
					info.setReadstatus(candidate.getContactStatus());
					flag = true;
					break;
				}
			}
		}
		
		mContactedCandidateAdapter.notifyDataSetChanged();
		if(!flag){
			// 得到当前的未处理的候选人
			mPositionEventLogic.getContactedCandidate(mOpenedPositions.get(actualPosition).getId(), String.valueOf(defaultMinapplyid));
		}
		
		Logcat.d("@@@", "contected candidate jid: " + candidate.getJid() + "  contected status:" + candidate.getContactStatus() );
	}
}
