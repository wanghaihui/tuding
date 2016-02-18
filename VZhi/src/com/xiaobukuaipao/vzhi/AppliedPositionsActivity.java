package com.xiaobukuaipao.vzhi;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.xiaobukuaipao.vzhi.adapter.CommonAdapter;
import com.xiaobukuaipao.vzhi.adapter.ViewHolder;
import com.xiaobukuaipao.vzhi.cache.CommonBitmapMemoryCache;
import com.xiaobukuaipao.vzhi.domain.MyAppliedInfo;
import com.xiaobukuaipao.vzhi.domain.PublisherInfo;
import com.xiaobukuaipao.vzhi.event.InfoResult;
import com.xiaobukuaipao.vzhi.util.DisplayUtil;
import com.xiaobukuaipao.vzhi.util.GlobalConstants;
import com.xiaobukuaipao.vzhi.util.SpannableKeyWordBuilder;
import com.xiaobukuaipao.vzhi.util.StringUtils;
import com.xiaobukuaipao.vzhi.util.VToast;
import com.xiaobukuaipao.vzhi.view.RoundedNetworkImageView;
import com.xiaobukuaipao.vzhi.view.refresh.PullToRefreshBase;
import com.xiaobukuaipao.vzhi.view.refresh.PullToRefreshBase.Mode;
import com.xiaobukuaipao.vzhi.view.refresh.PullToRefreshBase.OnRefreshListener2;
import com.xiaobukuaipao.vzhi.view.refresh.PullToRefreshListView;
import com.xiaobukuaipao.vzhi.wrap.ProfileWrapActivity;

/**
 * 我投递的职位
 * 
 * @since 2015年01月04日20:12:10
 */
public class AppliedPositionsActivity extends ProfileWrapActivity implements OnItemClickListener {

	/**
	 * 刷新列表控件
	 */
	private PullToRefreshListView mRefreshListView;
	/**
	 * 我投递的信息列表
	 */
	private List<MyAppliedInfo>  mApplieds;
	/**
	 * 我投递的列表适配器
	 */
	private AppliedAdapter mAdapter;
	/**
	 * Volley提供的网络请求队列
	 */
	private RequestQueue mQueue;
	/**
	 * 图片加载器,启动线程等功能
	 */
	private ImageLoader mImageLoader;
	/**
	 * 默认翻页标志位
	 */
	private final int defaultMinApplyId = -1;
	/**
	 * 翻页标志位
	 */
	private int minApplyId = defaultMinApplyId;
	/**
	 * 是否下拉刷新
	 */
	private boolean pullToRefresh = false;
	
	@Override
	public void initUIAndData() {
		setContentView(R.layout.activity_applied_positions);
		
		setHeaderMenuByLeft(this);
		setHeaderMenuByCenterTitle(R.string.applied_positions_str);
		
		//初始化适配器以及容器
		mApplieds = new ArrayList<MyAppliedInfo>();
		mAdapter = new AppliedAdapter(this, mApplieds, R.layout.item_applied);
		
		mRefreshListView = (PullToRefreshListView) findViewById(R.id.applied_list);
		mRefreshListView.setAdapter(mAdapter);
		mRefreshListView.setEmptyView(findViewById(R.id.empty_view));
		mRefreshListView.setOnItemClickListener(this);
		mRefreshListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {
			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				pullToRefresh = true;
				mProfileEventLogic.getMyPosted(defaultMinApplyId);
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				mProfileEventLogic.getMyPosted(minApplyId);
			}
		});
//		mRefreshListView.setRefreshing();
//		mRefreshListView.setShowViewWhileRefreshing(true);
//		mRefreshListView.setScrollingWhileRefreshingEnabled(true);
//		mRefreshListView.setPullToRefreshOverScrollEnabled(true);
		
		
		
		mQueue = Volley.newRequestQueue(this);
		mImageLoader = new ImageLoader(mQueue, new CommonBitmapMemoryCache());
		mProfileEventLogic.getMyPosted(defaultMinApplyId);
	}
	
	class AppliedAdapter extends CommonAdapter<MyAppliedInfo>{

		
		public AppliedAdapter(Context mContext, List<MyAppliedInfo> mDatas, int mItemLayoutId) {
			super(mContext, mDatas, mItemLayoutId);
		}

		@Override
		public void convert(ViewHolder viewHolder, final MyAppliedInfo item, final int position) {
			
			viewHolder.setText(R.id.applied_title,item.getPosition());
			SpannableKeyWordBuilder skwb = new SpannableKeyWordBuilder();
			
			boolean splite = false;
			if(StringUtils.isNotEmpty(item.getSalary())){
				skwb.appendKeyWord(item.getSalary());
				splite = true;
			}
			if(StringUtils.isNotEmpty(item.getCorp())){
				if(splite){
					skwb.append("    ");
				}
				skwb.append(item.getCorp());
				splite = true;
			}
			if(StringUtils.isNotEmpty(item.getCity())){
				if(splite){
					skwb.append("    ");
				}
				skwb.append(item.getCity());
			}
			viewHolder.setText(R.id.applied_descrip,skwb.build());
			
			skwb.delete(0, skwb.length());
			skwb.setMode(SpannableKeyWordBuilder.MODE_NUMBER);
			String count = getString(R.string.applied_position_tips,item.getApplynum(),item.getReadnum());
			skwb.append(count);
			
			viewHolder.setText(R.id.applied_count, skwb.build());
			viewHolder.setText(R.id.applied_tag,item.getApplystatus());
			skwb.delete(0, skwb.length());
			// load publisher
			
			LinearLayout head = viewHolder.getView(R.id.applied_head);
			head.removeAllViews();
			RoundedNetworkImageView avatar = new RoundedNetworkImageView(mContext);
			avatar.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
			final PublisherInfo publisherInfo = new PublisherInfo(item.getPublisher());
			avatar.setBorderWidth(0f);
			avatar.setBackgroundColor(getResources().getColor(R.color.general_color_F2F2F2));
			avatar.setCornerRadius((float)DisplayUtil.px2dip(mContext, 5f));
			avatar.setDefaultImageResId(R.drawable.general_user_avatar);
			avatar.setImageUrl(publisherInfo.getAvatar(), mImageLoader);
			
			head.addView(avatar);
			head.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					Intent intent = new Intent();
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					intent.setClass(mContext, PersonalShowPageActivity.class);
					intent.putExtra(GlobalConstants.UID, String.valueOf(publisherInfo.getIntId()));
					startActivity(intent);
				}
			});
			
			viewHolder.getConvertView().setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent intent = new Intent();
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					intent.setClass(mContext, JobPositionInfoActivity.class);
					intent.putExtra(GlobalConstants.JOB_ID, item.getId());
					startActivity(intent);
				}
			});
			
		}
	}


	@Override
	public void onEventMainThread(Message msg) {
		if (msg.obj instanceof InfoResult) {
			InfoResult infoResult = (InfoResult) msg.obj;
			switch (msg.what) {
			case R.id.profile_position_posted:
				// 将返回的JSON数据展示出来
				JSONObject jsonResult = (JSONObject) JSONObject.parse(infoResult.getResult());
				if(jsonResult != null){
					JSONArray jsonArray = jsonResult.getJSONArray(GlobalConstants.JSON_DATA);
					if(jsonArray != null){
						minApplyId =  jsonResult.getInteger(GlobalConstants.JSON_MINAPPLYID);
						if (pullToRefresh) {
							mApplieds.clear();
							pullToRefresh = false;
						}
						
						for(int i = 0; i < jsonArray.size() ; i++ ){
							mApplieds.add(new MyAppliedInfo(jsonArray.getJSONObject(i)));
						}
						mAdapter.notifyDataSetChanged();
						
						//如果翻页标志为0,禁止上拉加载
						mRefreshListView.setMode(minApplyId == 0 ? Mode.PULL_FROM_START : Mode.BOTH);
					}
				}
				
				mRefreshListView.onRefreshComplete();
				break;
			default:
				break;
			}
			
		}else if(msg.obj instanceof VolleyError){
			VToast.show(this, getString(R.string.general_tips_network_unknow));
			mRefreshListView.onRefreshComplete();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		
	}
	
}
