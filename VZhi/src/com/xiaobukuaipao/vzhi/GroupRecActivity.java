package com.xiaobukuaipao.vzhi;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.android.volley.toolbox.Volley;
import com.xiaobukuaipao.vzhi.adapter.CommonAdapter;
import com.xiaobukuaipao.vzhi.adapter.ViewHolder;
import com.xiaobukuaipao.vzhi.cache.CommonBitmapMemoryCache;
import com.xiaobukuaipao.vzhi.domain.GroupModel;
import com.xiaobukuaipao.vzhi.event.InfoResult;
import com.xiaobukuaipao.vzhi.util.GlobalConstants;
import com.xiaobukuaipao.vzhi.util.VToast;
import com.xiaobukuaipao.vzhi.view.LoadingDialog;
import com.xiaobukuaipao.vzhi.view.RoundedImageView;
import com.xiaobukuaipao.vzhi.view.refresh.PullToRefreshBase.Mode;
import com.xiaobukuaipao.vzhi.view.refresh.PullToRefreshListView;
import com.xiaobukuaipao.vzhi.wrap.SocialWrapActivity;


public class GroupRecActivity extends SocialWrapActivity{
	public static final String TAG = GroupRecActivity.class.getSimpleName();

	private PullToRefreshListView mRecList;
	private List<GroupModel> mDatas;
	private RecGroupsAdapter mGroupsAdapter;
	
	private int mingrouprecommendid = -1;
	private int tmpPosition = -1;
	private LoadingDialog loadingDialog;
	
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case 1:
					String gid = (String) msg.obj;
					joinInGroup(gid);
					break;
			}
		}
	};
	
	private void joinInGroup(String gid) {
		if(!loadingDialog.isShowing()){
			loadingDialog.show();
		}
		mSocialEventLogic.cancel(R.id.social_group_join);
		mSocialEventLogic.joinGroup(gid);
	}
	
	@Override
	public void initUIAndData() {
		setContentView(R.layout.activity_chat_group_rec);
		setHeaderMenuByCenterTitle(R.string.group_chat_rec);
		setHeaderMenuByLeft(this);
		loadingDialog = new LoadingDialog(this);
		mRecList = (PullToRefreshListView) findViewById(R.id.rec_group_list);
		mRecList.setMode(Mode.BOTH);
		
		mDatas = new ArrayList<GroupModel>();
		//TODO 获取socket的时候　讲列表中的该群未读数置０
		
		mGroupsAdapter = new RecGroupsAdapter(this,mDatas,R.layout.item_rec_groups, mHandler);
		
		mRecList.setAdapter(mGroupsAdapter);
		
		mSocialEventLogic.getGetGroupRecommend(String.valueOf(mingrouprecommendid));
	}

	@Override
	public void onEventMainThread(Message msg) {
		if (msg.obj instanceof InfoResult) {
			InfoResult infoResult = (InfoResult) msg.obj;
			if(infoResult.getResponse().statusCode != 200){
				if(loadingDialog.isShowing()){
					loadingDialog.dismiss();
				}
				return;
			}
			
			switch (msg.what) {
				case R.id.social_get_group_recommend:
					JSONObject jsonObject = (JSONObject) JSONObject.parse(infoResult.getResult());
					if(jsonObject != null){
						JSONArray groupJSONArray = jsonObject.getJSONArray(GlobalConstants.JSON_DATA);
						if(groupJSONArray != null){
							for (int i=0; i < groupJSONArray.size(); i++) {
								mDatas.add(new GroupModel(groupJSONArray.getJSONObject(i)));
							}
						}	
					}
					mGroupsAdapter.notifyDataSetChanged();
					break;
				case R.id.social_group_join:
					if (infoResult.getMessage().getStatus() == 0) {
						if(tmpPosition != -1){
							mDatas.remove(tmpPosition);
							mGroupsAdapter.notifyDataSetChanged();
							tmpPosition = -1;
						}
					}
					VToast.show(this, infoResult.getMessage().getMsg());
					if(loadingDialog.isShowing()){
						loadingDialog.dismiss();
					}
					break;
			}
		}
	}
	
	public class RecGroupsAdapter extends CommonAdapter<GroupModel> {

		// 请求队列
		private RequestQueue mQueue;
		private ImageLoader mImageLoader;
		private ImageListener mListener;
		
		private Handler mHandler;

		public RecGroupsAdapter(Context mContext, List<GroupModel> mDatas, int mItemLayoutId, Handler mHandler) {
			super(mContext, mDatas, mItemLayoutId);
			this.mHandler = mHandler;
			mQueue = Volley.newRequestQueue(mContext);
			mImageLoader = new ImageLoader(mQueue, new CommonBitmapMemoryCache());
		}

		@Override
		public void convert(final ViewHolder viewHolder, final GroupModel item, final int position) {
			if (item.getName() != null) {
				viewHolder.setText(R.id.group_name, item.getName());
			}
			
			final RoundedImageView mLogo = (RoundedImageView) viewHolder.getView(R.id.group_logo);
			if (item.getLogo() != null) {
				mListener = ImageLoader.getImageListener(mLogo, R.drawable.general_user_avatar,R.drawable.general_user_avatar);
				mImageLoader.get(item.getLogo(), mListener);
			}
			
			if (item.getDesc() != null) {
				viewHolder.setText(R.id.group_desc, item.getDesc());
			}
			
			final TextView mJoinIn = (TextView) viewHolder.getView(R.id.join_in);
			mJoinIn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View view) {
					Message msg = Message.obtain();
					msg.what = 1;
					msg.obj = item.getGid();
					mHandler.sendMessage(msg);
					tmpPosition = position;
				}
				
			});
			
		}
	}
}
