package com.xiaobukuaipao.vzhi.adapter;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.umeng.analytics.MobclickAgent;
import com.xiaobukuaipao.vzhi.ChatGroupActivity;
import com.xiaobukuaipao.vzhi.R;
import com.xiaobukuaipao.vzhi.cache.CommonBitmapMemoryCache;
import com.xiaobukuaipao.vzhi.domain.GroupModel;
import com.xiaobukuaipao.vzhi.util.StringUtils;
import com.xiaobukuaipao.vzhi.view.RoundedNetworkImageView;

public class RecruitGroupAdapter extends CommonAdapter<GroupModel> {

	// 请求队列
	private RequestQueue mQueue;
	private ImageLoader mImageLoader;

	public RecruitGroupAdapter(Context mContext, List<GroupModel> mDatas, int mItemLayoutId) {
		super(mContext, mDatas, mItemLayoutId);
		mQueue = Volley.newRequestQueue(mContext);
		mImageLoader = new ImageLoader(mQueue, new CommonBitmapMemoryCache());
	}

	@Override
	public void convert(ViewHolder viewHolder, GroupModel item, int position) {
		viewHolder.getConvertView().setOnClickListener(new OnClick(item));
		RoundedNetworkImageView groupAvatar = viewHolder.getView(R.id.group_avatar);
		groupAvatar.setDefaultImageResId( R.drawable.group_recruit);
		groupAvatar.setImageUrl(item.getLogo(), mImageLoader);
		
		if(StringUtils.isNotEmpty(item.getName())){
			viewHolder.setText(R.id.recruit_group_name, item.getName());
		}
		if(item.getMemnum() != -1){
			viewHolder.setText(R.id.group_member_num,mContext.getString(R.string.general_people_num , item.getMemnum())) ;
		}
		if(StringUtils.isNotEmpty(item.getDesc())){
			viewHolder.setText(R.id.recruit_group_info, item.getDesc());
		}
	}
	
	class OnClick implements OnClickListener{

		GroupModel groupModel;
		
		public OnClick(GroupModel groupModel){
			this.groupModel = groupModel;
		}
		
		@Override
		public void onClick(View v) {
			HashMap<String,String> map = new HashMap<String,String>();
			map.put("groudId",String.valueOf(groupModel.getGid()));
			MobclickAgent.onEvent(mContext,"qdj");
			
			Intent intent = new Intent();
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.setClass(v.getContext(), ChatGroupActivity.class);
			intent.putExtra("gid", groupModel.getGid());
			intent.putExtra("gname", groupModel.getName());
			v.getContext().startActivity(intent);
		}
	}
}
