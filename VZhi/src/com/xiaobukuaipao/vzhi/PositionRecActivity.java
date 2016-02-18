package com.xiaobukuaipao.vzhi;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.xiaobukuaipao.vzhi.adapter.RecruitListViewAdapter2;
import com.xiaobukuaipao.vzhi.domain.PublishJobsInfo;
import com.xiaobukuaipao.vzhi.event.InfoResult;
import com.xiaobukuaipao.vzhi.util.GlobalConstants;
import com.xiaobukuaipao.vzhi.view.refresh.PullToRefreshBase;
import com.xiaobukuaipao.vzhi.view.refresh.PullToRefreshBase.Mode;
import com.xiaobukuaipao.vzhi.view.refresh.PullToRefreshBase.OnLastItemVisibleListener;
import com.xiaobukuaipao.vzhi.view.refresh.PullToRefreshBase.OnRefreshListener2;
import com.xiaobukuaipao.vzhi.view.refresh.PullToRefreshListView;
import com.xiaobukuaipao.vzhi.wrap.SocialWrapActivity;

/**
 * 消息列表,职位推荐
 * 
 * @since 2015年01月09日15:23:06
 */
public class PositionRecActivity extends SocialWrapActivity{

	private PullToRefreshListView mPullToRefreshListView;
	
	private List<PublishJobsInfo> mPublishJobsInfo;
	
	private RecruitListViewAdapter2 mPositionsAdapter;
	
	private final int defaultMinjobrecommendid = -1;
	
	private Integer minjobrecommendid = defaultMinjobrecommendid;
	/**
	 * 翻页计数，用于防止使用同一个id做下拉刷新，这样会导致同样的数据，请求多次
	 */
	private int requestCount = 0;
	/**
	 * 加载更多
	 */
	private boolean loadMore;
	/**
	 * 下拉加载
	 */
	private boolean pullToRefresh;
	
	private WindowManager wm;
	
	private DisplayMetrics metric;
	// 通用对象
	LinearLayout.LayoutParams lp;
	
	RelativeLayout.LayoutParams rlp;
	@Override
	public void initUIAndData() {
		setContentView(R.layout.activity_chat_position_rec);
		setHeaderMenuByCenterTitle(R.string.job_recommend);
		setHeaderMenuByLeft(this);
		
		mPublishJobsInfo = new ArrayList<PublishJobsInfo>();
		mPositionsAdapter = new RecruitListViewAdapter2(this, mPublishJobsInfo, R.layout.recruit_list_crop_item2);

		mPullToRefreshListView = (PullToRefreshListView) findViewById(R.id.rec_group_list);
		mPullToRefreshListView.setMode(Mode.BOTH);
		mPullToRefreshListView.setAdapter(mPositionsAdapter);
		mPullToRefreshListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {

			@Override
			public void onPullDownToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				pullToRefresh = true;
				mSocialEventLogic.getRecPositionInfo(String.valueOf(defaultMinjobrecommendid));
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				if (minjobrecommendid != 0) {
					loadMore = true;
				}
			}
		});
		mPullToRefreshListView.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {
		
			@Override
			public void onLastItemVisible() {
				requestCount ++;
				if (minjobrecommendid != 0 && requestCount <= 1) {
					loadMore = true;
					mSocialEventLogic.getRecPositionInfo(String.valueOf(minjobrecommendid));
				}
			}
		});
		
		lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		rlp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		
		wm = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
		metric = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(metric);

		mSocialEventLogic.getRecPositionInfo(String.valueOf(defaultMinjobrecommendid));
	}

	@Override
	public void onEventMainThread(Message msg) {
		if(msg.obj instanceof InfoResult){
			InfoResult infoResult = (InfoResult) msg.obj;
			switch (msg.what) {
			case R.id.social_get_position_recommend:
				JSONObject jsonObject = (JSONObject) JSONObject.parse(infoResult.getResult());
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
						mPositionsAdapter.notifyDataSetChanged();
						
						// 如果一个用户发的职位太多，但最后只聚合成3个，此时自动下拉一列
						if (jsonArray.size() < 3) {
							mPositionEventLogic.requestRecruitList(String.valueOf(minjobrecommendid));
						}
					}else{
						mPullToRefreshListView.onRefreshComplete();
					}
					if (loadMore) {
						requestCount = 0;
						loadMore = false;
						mPullToRefreshListView.onRefreshComplete();
					}
					minjobrecommendid = jsonObject.getInteger(GlobalConstants.JSON_MINREFRESHID);
					
					mPullToRefreshListView.setMode(minjobrecommendid == null || minjobrecommendid == 0 || jsonObject.getString(GlobalConstants.JSON_NOMORE) != null ? Mode.PULL_FROM_START : Mode.BOTH);
				}
				break;
			default:
				break;
			}
			
		} else if (msg.obj instanceof VolleyError) {
			// 可提示网络错误，具体类型有TimeoutError ServerError
			mPullToRefreshListView.onRefreshComplete();
		}
	}
}
