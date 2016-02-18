package com.xiaobukuaipao.vzhi.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.squareup.picasso.Picasso;
import com.xiaobukuaipao.vzhi.R;
import com.xiaobukuaipao.vzhi.WebSearchActivity;
import com.xiaobukuaipao.vzhi.adapter.RecruitListViewAdapter2;
import com.xiaobukuaipao.vzhi.domain.PublishJobsInfo;
import com.xiaobukuaipao.vzhi.event.InfoResult;
import com.xiaobukuaipao.vzhi.event.IntentionCompleteEvent;
import com.xiaobukuaipao.vzhi.event.PositionEventLogic;
import com.xiaobukuaipao.vzhi.manager.GeneralDbManager;
import com.xiaobukuaipao.vzhi.register.JobObjectiveActivity;
import com.xiaobukuaipao.vzhi.util.FileCache;
import com.xiaobukuaipao.vzhi.util.GlobalConstants;
import com.xiaobukuaipao.vzhi.util.NetworkUtils;
import com.xiaobukuaipao.vzhi.util.VToast;
import com.xiaobukuaipao.vzhi.view.refresh.PullToRefreshBase;
import com.xiaobukuaipao.vzhi.view.refresh.PullToRefreshBase.Mode;
import com.xiaobukuaipao.vzhi.view.refresh.PullToRefreshBase.OnLastItemVisibleListener;
import com.xiaobukuaipao.vzhi.view.refresh.PullToRefreshBase.OnRefreshListener2;
import com.xiaobukuaipao.vzhi.view.refresh.PullToRefreshListView;

import de.greenrobot.event.EventBus;

/**
 * 职位列表,是本应用最核心的列表页之一,代表着本应用带给用户的第一印象
 * 1.第一版列表页,带有公司福利,根据公司聚合人,根据人聚合职位
 * 2.现在这版列表页,去掉了根据公司聚合人的功能,去掉公司福利,稍微强化了一点"职位诱惑"
 * 
 * 
 * @since 2015年01月08日14:25:03
 */
public class RecruitPageFragment extends BaseFragment {
	public static final String TAG = RecruitPageFragment.class.getSimpleName();
	
	public static final int REQUEST_CODE = 100;

	/**
	 * 父布局容器
	 */
	private View mFragment;
	/**
	 * 可刷新列表
	 */
	private PullToRefreshListView mPullToRefreshListView;
	/**
	 * 职位网络数据管理器
	 */
	private PositionEventLogic mPositionEventLogic;
	/**
	 * 发布的职位信息数据容器
	 */
	private List<PublishJobsInfo> mPublishJobsInfo;
	/**
	 * 职位信息的适配器
	 */
	private RecruitListViewAdapter2 mAdapter;
	/**
	 * 加载更多
	 */
	private boolean loadMore;
	/**
	 * 下拉加载
	 */
	private boolean pullToRefresh;
	
	private final int defaultMinrefreshId = -1;
	/**
	 * 翻页标识符
	 */
	private Integer minrefreshid = defaultMinrefreshId;
	/**
	 * 翻页计数，用于防止使用同一个id做下拉刷新，这样会导致同样的数据，请求多次
	 */
	private int requestCount = 0;
	
	/**
	 * 是否完善求职意向
	 */
	private RelativeLayout mIntentionGuide;
	private ImageButton mIntentionBtn;

	private String mFileName = "recruit_list.json";
	public RecruitPageFragment() {
		mPositionEventLogic = new PositionEventLogic();
		mPositionEventLogic.register(this);
	}
	
	public RecruitPageFragment(Activity activity) {
		this.activity = activity;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mFragment = inflater.inflate(R.layout.activity_main_tab_recruit, container, false);
		return mFragment;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		EventBus.getDefault().register(this);
		initUIAndData();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}
	
	private void initUIAndData() {
		mPublishJobsInfo = new ArrayList<PublishJobsInfo>();
		// 设置显示数据的Adapter
		
		mIntentionGuide = (RelativeLayout) mFragment.findViewById(R.id.intention_guide);
		mIntentionBtn = (ImageButton) mFragment.findViewById(R.id.job_objective_edit);
		
		mIntentionGuide.setVisibility(GeneralDbManager.getInstance().isExistCookie() && !getActivity().getSharedPreferences("tuding_uid", Activity.MODE_PRIVATE).getBoolean(GlobalConstants.INTENTION_COMPLETE, false) ? View.VISIBLE : View.GONE);
		mIntentionGuide.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				
				Intent intent = new Intent(RecruitPageFragment.this.getActivity(), JobObjectiveActivity.class);
				intent.putExtra("intention_guide", true);
				RecruitPageFragment.this.startActivityForResult(intent, REQUEST_CODE);
			}
		});
		
		mIntentionBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				
				Intent intent = new Intent(RecruitPageFragment.this.getActivity(), JobObjectiveActivity.class);
				intent.putExtra("intention_guide", true);
				getActivity().startActivityForResult(intent, REQUEST_CODE);
			}
		});
		
		// mAdapter = new RecruitListViewAdapter(getActivity(), mCorpPublishJobsInfo, R.layout.recruit_list_crop_item);
		mAdapter = new RecruitListViewAdapter2(getActivity(), mPublishJobsInfo, R.layout.recruit_list_crop_item2);
		
		mPullToRefreshListView = (PullToRefreshListView) mFragment.findViewById(R.id.recruitMessageList);
		mPullToRefreshListView.setMode(Mode.PULL_FROM_START);
		mPullToRefreshListView.setEmptyView(getActivity().findViewById(R.id.empty1));
		mPullToRefreshListView.setAdapter(mAdapter);
		
		mPullToRefreshListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {

					@Override
					public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
						pullToRefresh = true;
						mPositionEventLogic.requestRecruitList(String.valueOf(defaultMinrefreshId));
					}

					@Override
					public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
						if (minrefreshid != null && minrefreshid != -1&& minrefreshid != 0) {
							loadMore = true;
						}
					}
				});
		
		mPullToRefreshListView.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {

			@Override
			public void onLastItemVisible() {
				if(!NetworkUtils.isNetWorkAvalible(getActivity())){
					VToast.show(getActivity(), getActivity().getString(R.string.general_tips_network_unknow));
				}else{
					requestCount++;//防止多次刷新操作 导致数据重复
				}
				if (minrefreshid != null && minrefreshid != -1&& minrefreshid != 0  && requestCount <= 1) {
					loadMore = true;
					mPositionEventLogic.requestRecruitList(String.valueOf(minrefreshid));
				}
			}
		});
		
		
		String readData = FileCache.readData(getActivity(), mFileName);
		invalidateUI(readData);
		
		mPositionEventLogic.requestRecruitList(String.valueOf(defaultMinrefreshId));
		mPositionEventLogic.getAwardPositions();
	}
	
	
	public void onEventMainThread(Message msg) {
		switch (msg.what) {
			case R.id.recruitListHttp:
				if (msg.obj instanceof InfoResult) {
					InfoResult infoResult = (InfoResult) msg.obj;
					
					invalidateUI(infoResult.getResult());
				} else if (msg.obj instanceof VolleyError) {
					// 可提示网络错误，具体类型有TimeoutError ServerError
					mPullToRefreshListView.onRefreshComplete();
				}
				break;
			case R.id.position_get_award:
				if (msg.obj instanceof InfoResult) {
					InfoResult infoResult = (InfoResult) msg.obj;
					 JSONObject jsonObj = (JSONObject) JSONObject.parse(infoResult.getResult());
					 JSONObject activity = jsonObj.getJSONObject("activity");
					 if(activity != null){
						final String url = activity.getString("url");
						String img = activity.getString("img");
						ads(url, img);
					 }
				}else if(msg.obj instanceof VolleyError){
					
				}
				break;
			default:
				break;
		}
	}

	private void ads(final String url, String img) {
		ImageView ads = new ImageView(getActivity());
		ads.setLayoutParams(new android.widget.AbsListView.LayoutParams(android.widget.AbsListView.LayoutParams.MATCH_PARENT, 
				android.widget.AbsListView.LayoutParams.WRAP_CONTENT));
		ads.setAdjustViewBounds(true);
		ads.setFitsSystemWindows(true);
		ads.setScaleType(ScaleType.FIT_XY);
		ads.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				String prefix = "http://";
				String content_url = url;
				
				if(!content_url.startsWith(prefix)){
					content_url = prefix + content_url;
				}
				
				intent.putExtra(GlobalConstants.INNER_URL, content_url);
				intent.setClass(v.getContext(), WebSearchActivity.class);
				startActivity(intent);
			}
		});
		mPullToRefreshListView.getRefreshableView().addHeaderView(ads);
		Picasso.with(getActivity()).load(img).into(ads);
	}

	/**
	 * @param infoResult
	 */
	protected void invalidateUI(String json) {
		
		JSONObject jsonObject = (JSONObject) JSONObject.parse(json);
		if(jsonObject != null){
			if (pullToRefresh) {
				mPublishJobsInfo.clear();
				pullToRefresh = false;
				mPullToRefreshListView.onRefreshComplete();
			}
			
			JSONArray jsonArray = (JSONArray) jsonObject.getJSONArray(GlobalConstants.JSON_DATA);
			if(jsonArray != null){
				for (int i = 0; i < jsonArray.size(); i++) {
					JSONObject publisherJobs = jsonArray.getJSONObject(i).getJSONObject(GlobalConstants.JSON_PUBLISHER_JOBS);
					if(publisherJobs != null){
						mPublishJobsInfo.add(new PublishJobsInfo(publisherJobs));
					}
				}
				mAdapter.notifyDataSetChanged();
				
				// 如果一个用户发的职位太多，但最后只聚合成3个，此时自动下拉一列
				if (jsonArray.size() < 3) {
					mPositionEventLogic.requestRecruitList(String.valueOf(jsonObject.getInteger(GlobalConstants.JSON_MINREFRESHID)));
				}
			}else{
				mPullToRefreshListView.onRefreshComplete();
			}
			
			if (loadMore) {
				loadMore = false;
				mPullToRefreshListView.onRefreshComplete();
			}
			//初始化 计数器
			requestCount = 0;
			if(minrefreshid == -1){
				// 增加文件缓存
				FileCache.saveData(getActivity(), json, mFileName);
			}
			
			minrefreshid = jsonObject.getInteger(GlobalConstants.JSON_MINREFRESHID);
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mPositionEventLogic.unregister(this);
		EventBus.getDefault().unregister(this);
	}
	
	public void onEvent(IntentionCompleteEvent event){
		mIntentionGuide.setVisibility(event.getComplete() ? View.GONE : View.VISIBLE);
	}
}
