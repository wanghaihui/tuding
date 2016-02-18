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
import com.xiaobukuaipao.vzhi.adapter.ConnectGroupAdapter;
import com.xiaobukuaipao.vzhi.domain.GroupModel;
import com.xiaobukuaipao.vzhi.event.InfoResult;
import com.xiaobukuaipao.vzhi.event.SocialEventLogic;
import com.xiaobukuaipao.vzhi.util.GlobalConstants;
import com.xiaobukuaipao.vzhi.view.refresh.PullToRefreshBase;
import com.xiaobukuaipao.vzhi.view.refresh.PullToRefreshBase.Mode;
import com.xiaobukuaipao.vzhi.view.refresh.PullToRefreshBase.OnRefreshListener2;
import com.xiaobukuaipao.vzhi.view.refresh.PullToRefreshListView;

/**
 * 人脉群
 * 
 * @author hongxin.bai
 *
 */
public class PersonConnectGroupFragment extends BaseFragment{

	private View view;
	private PullToRefreshListView mPullListView;
	private ConnectGroupAdapter mAdapter;
	private List<GroupModel> mDatas;
	private SocialEventLogic mSocialEventLogic;
	private Integer minmsgid = -1;
	private boolean pullToRefresh;
	private boolean loadMore;
	
	public PersonConnectGroupFragment(){

	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public void onDestroy() {
		mSocialEventLogic.unregister(this);
		super.onDestroy();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_connect_group, container, false);
		this.view = view;
		return view;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initUIAndData();
	}

	private void initUIAndData() {
		
		mDatas = new ArrayList<GroupModel>();
		mAdapter = new ConnectGroupAdapter(getActivity(), mDatas,R.layout.connect_group_item);
		mPullListView = (PullToRefreshListView) view.findViewById(R.id.connect_group_list);
		// 设置显示数据的Adapter
		mPullListView.setAdapter(mAdapter);
		mPullListView.setEmptyView(view.findViewById(R.id.empty));
		mPullListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				pullToRefresh = true;
				mSocialEventLogic.getRealGroupList(String.valueOf(-1));
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
					loadMore = true;
					mSocialEventLogic.getRealGroupList(String.valueOf(minmsgid));
			}
		});
		
		mSocialEventLogic = new SocialEventLogic();
		mSocialEventLogic.register(this);
		mSocialEventLogic.getRealGroupList(String.valueOf(-1));
	}

	public void onEventMainThread(Message msg) {
		if (msg.obj instanceof InfoResult) {
			InfoResult infoResult = (InfoResult) msg.obj;
			switch (msg.what) {
			case R.id.social_group_get_real_list:
				JSONObject jsonObject = JSONObject.parseObject(infoResult.getResult());
				if (jsonObject != null) {
					// 解析公司
					if (pullToRefresh) {
						mDatas.clear();
						pullToRefresh = false;
						mPullListView.onRefreshComplete();
					}
					JSONArray jsonArray = jsonObject.getJSONArray(GlobalConstants.JSON_DATA);
					if (jsonArray != null) {
						for(int i = 0 ; i < jsonArray.size() ; i ++){
							JSONObject modelObject = jsonArray.getJSONObject(i);
							if(modelObject != null){
								GroupModel groupModel = new GroupModel(modelObject);
								mDatas.add(groupModel);
							}
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
}
