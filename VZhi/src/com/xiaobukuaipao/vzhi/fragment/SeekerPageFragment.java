package com.xiaobukuaipao.vzhi.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.squareup.picasso.Picasso;
import com.xiaobukuaipao.vzhi.AnoPersonInfoActivity;
import com.xiaobukuaipao.vzhi.PersonalShowPageActivity;
import com.xiaobukuaipao.vzhi.R;
import com.xiaobukuaipao.vzhi.adapter.CommonAdapter;
import com.xiaobukuaipao.vzhi.adapter.ViewHolder;
import com.xiaobukuaipao.vzhi.domain.user.BasicInfo;
import com.xiaobukuaipao.vzhi.domain.user.UserProfile;
import com.xiaobukuaipao.vzhi.event.InfoResult;
import com.xiaobukuaipao.vzhi.event.PositionEventLogic;
import com.xiaobukuaipao.vzhi.manager.GeneralDbManager;
import com.xiaobukuaipao.vzhi.util.FileCache;
import com.xiaobukuaipao.vzhi.util.GlobalConstants;
import com.xiaobukuaipao.vzhi.util.Logcat;
import com.xiaobukuaipao.vzhi.util.NetworkUtils;
import com.xiaobukuaipao.vzhi.util.StringUtils;
import com.xiaobukuaipao.vzhi.util.VToast;
import com.xiaobukuaipao.vzhi.view.RoundedImageView;
import com.xiaobukuaipao.vzhi.view.refresh.PullToRefreshBase;
import com.xiaobukuaipao.vzhi.view.refresh.PullToRefreshBase.Mode;
import com.xiaobukuaipao.vzhi.view.refresh.PullToRefreshBase.OnLastItemVisibleListener;
import com.xiaobukuaipao.vzhi.view.refresh.PullToRefreshBase.OnRefreshListener2;
import com.xiaobukuaipao.vzhi.view.refresh.PullToRefreshListView;

import de.greenrobot.event.EventBus;

/**
 * 招人才<br>
 * 逛一逛中的招人才列表页,在首页为需要人才的HR推荐
 * 
 *@since 2015年01月04日12:18:27
 */
public class SeekerPageFragment extends BaseFragment {
	
	/**
	 * 向碎片里填充的布局,再context中起到父容器的作用
	 */
	private View mFragment;
	/**
	 * 刷新列表
	 */
	private PullToRefreshListView mPullToRefreshListView;
	/**
	 * 用户列表适配器
	 */
	private SeekerAdapter mAdapter;
	/**
	 * 职位信息网络驱动管理器
	 */
	private PositionEventLogic mPositionEventLogic;
	/**
	 * 展示用户的列表数据
	 */
	private List<UserProfile> mUserProfiles;
	/**
	 * 是否需要上拉加载
	 */
	private boolean loadMore = false;
	/**
	 * 是否需要下拉刷新
	 */
	private boolean pullToRefresh = false;
	/**
	 * 默认翻页标志
	 */
	private final int defaultMinhotid = -1;
	/**
	 * 翻页标志
	 */
	private Integer minhotid= defaultMinhotid;
	/**
	 * 翻页计数，用于防止使用同一个id做下拉刷新，这样会导致同样的数据，请求多次
	 */
	private int requestCount = 0;
	/**
	 * 
	 */
	private String mFileName = "seeker_list.json";
	
	public SeekerPageFragment() {
	} 
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_seeker_page, container, false);
		this.mFragment = view;
		initUIAndData();
		return view;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		EventBus.getDefault().register(this);
	}
	
	private void initUIAndData() {

		mUserProfiles = new ArrayList<UserProfile>();
		
		
		// 设置显示数据的Adapter
		mAdapter = new SeekerAdapter(getActivity(),mUserProfiles, R.layout.item_seeker);
		
		mPullToRefreshListView = (PullToRefreshListView) mFragment.findViewById(R.id.good_seeker_list);
		mPullToRefreshListView.setMode(Mode.PULL_FROM_START);
		mPullToRefreshListView.setEmptyView(mFragment.findViewById(R.id.empty2));
		mPullToRefreshListView.setAdapter(mAdapter);
		
		mPositionEventLogic = new PositionEventLogic();
		mPositionEventLogic.register(this);
		
		String readData = FileCache.readData(getActivity(), mFileName);
		if(readData != null){
			invalidateUI(readData);
		}
		mPositionEventLogic.getSeekers(String.valueOf(defaultMinhotid));
		
		mPullToRefreshListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {

					@Override
					public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
						pullToRefresh = true;
						requestCount = defaultMinhotid;
						mPositionEventLogic.getSeekers(String.valueOf(defaultMinhotid));
					}

					@Override
					public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
						if (minhotid != null && minhotid != 0) {
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
//				Logcat.d("@@@", "requestCount: " + requestCount + "   minhotid: " + minhotid);
				if (minhotid != null && minhotid != 0 && minhotid != -1 && requestCount <= 1) {
					loadMore = true;
					mPositionEventLogic.getSeekers(String.valueOf(minhotid));
					
				}
			}
		});
	}

	public void onEventMainThread(Message msg) {
		switch (msg.what) {
		case R.id.look_seekers_get:
			if (msg.obj instanceof InfoResult) {
				InfoResult infoResult = (InfoResult) msg.obj;
				invalidateUI(infoResult.getResult());
				
			} else if (msg.obj instanceof VolleyError) {
				mPullToRefreshListView.onRefreshComplete();
				// 可提示网络错误，具体类型有TimeoutError ServerError
			}
			break;
		default:
			break;
		}
	}

	/**
	 * @param json
	 */
	protected void invalidateUI(String json) {
		Logcat.i("@@@", json);
		JSONObject jsonObject = (JSONObject) JSONObject.parse(json);
		if(jsonObject != null){
			
			if (pullToRefresh) {
				mUserProfiles.clear();
				pullToRefresh = false;
				mPullToRefreshListView.onRefreshComplete();
			}
			JSONArray jsonArray = jsonObject.getJSONArray(GlobalConstants.JSON_DATA);
			if(jsonArray != null){
				for (int i = 0; i < jsonArray.size(); i++) {
					UserProfile userProfile = new UserProfile(jsonArray.getJSONObject(i));
					if(!mUserProfiles.contains(userProfile)){
						mUserProfiles.add(userProfile);
					}
				}
				mAdapter.notifyDataSetChanged();
			}else{
				mPullToRefreshListView.onRefreshComplete();
			}
			
			if (loadMore) {
				loadMore = false;
				mPullToRefreshListView.onRefreshComplete();
			}
			
			//初始化 计数器
			requestCount = 0;
			if(minhotid == -1){
				// 增加文件缓存
				FileCache.saveData(getActivity(), json, mFileName);
			}
			minhotid = jsonObject.getInteger(GlobalConstants.JSON_MINHOTID);
//			mPullToRefreshListView.setMode(minhotid == null || minhotid == 0 ? Mode.PULL_FROM_START : Mode.BOTH);
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mPositionEventLogic.unregister(this);
		EventBus.getDefault().unregister(this);
	}

	
	class SeekerAdapter extends CommonAdapter<UserProfile>{

		public SeekerAdapter(Context mContext, List<UserProfile> mDatas,
				int mItemLayoutId) {
			super(mContext, mDatas, mItemLayoutId);
		}

		@Override
		public void convert(final ViewHolder viewHolder, final UserProfile item,
				final int position) {
			RoundedImageView avatar = viewHolder.getView(R.id.avatar);
			TextView name = viewHolder.getView(R.id.name);//人物名称
			TextView title = viewHolder.getView(R.id.position);//人物职位
			TextView corp = viewHolder.getView(R.id.corp);//所在公司
			TextView exprAndCity = viewHolder.getView(R.id.expr_and_city);//经验和所在地
			TextView skills = viewHolder.getView(R.id.skills);//技能
			TextView words = viewHolder.getView(R.id.words);//介绍
			//初始化
			name.setVisibility(View.GONE);
			exprAndCity.setVisibility(View.GONE);
			title.setVisibility(View.GONE);
			corp.setVisibility(View.GONE);
			skills.setVisibility(View.GONE);
			words.setVisibility(View.GONE);
			
			if(item.getBasic() != null){
				final BasicInfo basicInfo = new BasicInfo(item.getBasic());
				
				// avatar.setBorderColor(getResources().getColor(basicInfo.getGender() == 1 ? R.color.bg_blue : R.color.bg_pink));
				
				if(StringUtils.isNotEmpty(basicInfo.getName())){
					//用 给的是真实名称还是匿名来判断名字卡片是否是匿名
					name.setVisibility(View.VISIBLE);
					name.setText(basicInfo.getName());
					if(StringUtils.isNotEmpty(basicInfo.getRealAvatar())){
						Picasso.with(mContext).load(basicInfo.getRealAvatar()).placeholder(R.drawable.general_user_avatar).into(avatar);
					}else{
						avatar.setImageResource(R.drawable.general_user_avatar);
					}
				}
				
				if(StringUtils.isNotEmpty(basicInfo.getNickname())){
					name.setVisibility(View.VISIBLE);
					name.setText(R.string.general_tips_ano);
					if(StringUtils.isNotEmpty(basicInfo.getAvatar())){
						Picasso.with(mContext).load(basicInfo.getAvatar()).placeholder(R.drawable.general_default_ano).into(avatar);
					}else{
						avatar.setImageResource(R.drawable.general_default_ano);
					}
				}
				
				if(StringUtils.isNotEmpty(basicInfo.getCorp())){
					corp.setVisibility(View.VISIBLE);
					corp.setText(basicInfo.getCorp());
				}
				
				boolean splite = false;
				StringBuilder builder = new StringBuilder();
				if(StringUtils.isNotEmpty(basicInfo.getCity())){
					builder.append(basicInfo.getCity());
					splite = true;
				}
				if(StringUtils.isNotEmpty(basicInfo.getExprName())){
					if(splite){
						builder.append(" / ");
					}
					splite = true;
					builder.append(basicInfo.getExprName());
				}
				if(splite){
					exprAndCity.setVisibility(View.VISIBLE);
				}
				
				exprAndCity.setText(builder.toString());
				
				if(StringUtils.isNotEmpty(basicInfo.getPosition())){
					title.setVisibility(View.VISIBLE);
					title.setText(basicInfo.getPosition());
				}
				
				if(item.getSkills() != null){
					builder.delete(0, builder.length());
					skills.setVisibility(View.VISIBLE);
					for (int i = 0; i < item.getSkills().size(); i++) {
						String skillname = item.getSkills().getJSONObject(i).getString(GlobalConstants.JSON_NAME);
						builder.append(skillname);
						if(i != item.getSkills().size() - 1){
							builder.append("，");
						}
					}
					skills.setText(getString(R.string.general_tips_default_skills, builder.toString()));
				}

				if(StringUtils.isNotEmpty(item.getWords())){
					StringBuilder sb = new StringBuilder();
					sb.append(getString(R.string.left_quotation));
					sb.append(" ");
					sb.append(item.getWords());
					sb.append(" ");
					sb.append(getString(R.string.right_quotation));
					words.setText(sb.toString());
					words.setVisibility(View.VISIBLE);
				}
				
				viewHolder.getConvertView().setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						if(GeneralDbManager.getInstance().isExistCookie()){
							if(StringUtils.isNotEmpty(basicInfo.getName())){
								//用 给的是真实名称还是匿名来判断名字卡片是否是匿名
								Intent intent = new Intent();
								intent.setClass(getActivity(), PersonalShowPageActivity.class);
								intent.putExtra(GlobalConstants.UID,basicInfo.getId());
								startActivity(intent);
								
							}
							if(StringUtils.isNotEmpty(basicInfo.getNickname())){
								Intent intent = new Intent();
								intent.setClass(getActivity(), AnoPersonInfoActivity.class);
								intent.putExtra(GlobalConstants.UID, basicInfo.getId());
								startActivity(intent);
							}
							
						}else{
							VToast.show(getActivity(), getString(R.string.general_tips_default_unlogin));
						}
					}
				});
			}
		}
	}
	
	public void onEvent(Integer result){
		if(result == Activity.RESULT_OK){
			pullToRefresh = true;
			mPositionEventLogic.getSeekers(String.valueOf(defaultMinhotid));
		}
	}
}
