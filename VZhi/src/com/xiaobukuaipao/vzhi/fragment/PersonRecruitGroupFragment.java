package com.xiaobukuaipao.vzhi.fragment;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.xiaobukuaipao.vzhi.R;
import com.xiaobukuaipao.vzhi.adapter.RecruitGroupAdapter;
import com.xiaobukuaipao.vzhi.domain.GroupModel;
import com.xiaobukuaipao.vzhi.event.InfoResult;
import com.xiaobukuaipao.vzhi.event.SocialEventLogic;
import com.xiaobukuaipao.vzhi.util.GlobalConstants;
import com.xiaobukuaipao.vzhi.view.refresh.PullToRefreshBase;
import com.xiaobukuaipao.vzhi.view.refresh.PullToRefreshBase.Mode;
import com.xiaobukuaipao.vzhi.view.refresh.PullToRefreshBase.OnRefreshListener2;
import com.xiaobukuaipao.vzhi.view.refresh.PullToRefreshListView;

/**
 * 投递群
 * 
 * @author hongxin.bai
 *
 */
public class PersonRecruitGroupFragment extends BaseFragment{

	/**
	 * 向碎片里填充的布局,再context中起到父容器的作用
	 */
	private View mFragment;
	/**
	 * 刷新列表
	 */
	private PullToRefreshListView mPullListView;
	/**
	 * 列表适配器
	 */
	private RecruitGroupAdapter mAdapter;
	/**
	 * 列表数据
	 */
	private List<GroupModel> mDatas;
	/**
	 * 社交信息网络驱动管理器
	 */
	private SocialEventLogic mSocialEventLogic;
	/**
	 * 默认翻页标志
	 */
	private final Integer defaultMinmsgid = -1;
	/**
	 * 翻页标志
	 */
	private Integer minmsgid = defaultMinmsgid;
	/**
	 * 是否需要下拉刷新
	 */
	private boolean pullToRefresh;
	/**
	 * 是否需要上拉加载
	 */
	private boolean loadMore;
	
	public PersonRecruitGroupFragment(){

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_recruit_group, container, false);
        this.mFragment = view;
        return view;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initUIAndData();
	}
	
	private void initUIAndData() {
		//初始化适配器以及容器
		mDatas = new ArrayList<GroupModel>();
        mAdapter = new RecruitGroupAdapter(getActivity(), mDatas, R.layout.recruit_group_item);
        
		mPullListView = (PullToRefreshListView) mFragment.findViewById(R.id.recruit_group_list);
        mPullListView.setAdapter(mAdapter);
        mPullListView.setEmptyView(mFragment.findViewById(R.id.empty));
        mPullListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {
			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				pullToRefresh = true;
				mSocialEventLogic.getAppliedGroupList(String.valueOf(defaultMinmsgid));
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
					loadMore = true;
					mSocialEventLogic.getAppliedGroupList(String.valueOf(minmsgid));
			}
		});
		
		mSocialEventLogic = new SocialEventLogic();
		mSocialEventLogic.register(this);
		mSocialEventLogic.getAppliedGroupList(String.valueOf(defaultMinmsgid));
		
	}
	public void onEventMainThread(Message msg) {
		if (msg.obj instanceof InfoResult) {
			InfoResult infoResult = (InfoResult) msg.obj;
			switch (msg.what) {
			case R.id.social_group_get_nick_list:
				JSONObject jsonObject = JSONObject.parseObject(infoResult.getResult());
				if(jsonObject != null){
					// 解析公司
					if (pullToRefresh) {
						mDatas.clear();
						pullToRefresh = false;
						mPullListView.onRefreshComplete();
					}
					JSONArray jsonArray = jsonObject.getJSONArray(GlobalConstants.JSON_DATA);
					if(jsonArray != null){
						for(int i = 0 ; i < jsonArray.size() ; i ++){
							mDatas.add(new GroupModel(jsonArray.getJSONObject(i)));
						}
						mAdapter.notifyDataSetChanged();
						if (loadMore) {
							loadMore = false;
							mPullListView.onRefreshComplete();
						}
						minmsgid = jsonObject.getInteger(GlobalConstants.JSON_MINMSGID);
						if(minmsgid == 0){
							mPullListView.setMode(Mode.PULL_FROM_START);
						}else{
							mPullListView.setMode(Mode.BOTH);
						}
					}
				}
				break;

			default:
				break;
			}
		}else if (msg.obj instanceof VolleyError) {
			mPullListView.onRefreshComplete();
			// 可提示网络错误，具体类型有TimeoutError ServerError
		}
	}

	@Override
	public void onDestroy() {
		mSocialEventLogic.unregister(this);
		super.onDestroy();
	}
}
