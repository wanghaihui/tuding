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

public class ConnectGroupAdapter extends CommonAdapter<GroupModel> {
	// 请求队列
	private RequestQueue mQueue;
	private ImageLoader mImageLoader;
//	private ImageListener mListener;
	
	public ConnectGroupAdapter(Context mContext, List<GroupModel> mDatas, int mItemLayoutId) {
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
			viewHolder.setText(R.id.connect_group_name, item.getName());
		}
		if(item.getMemnum() != -1){
			viewHolder.setText(R.id.group_member_num,mContext.getString(R.string.general_people_num , item.getMemnum())) ;
		}
		if(StringUtils.isNotEmpty(item.getDesc())){
			viewHolder.setText(R.id.connect_group_info, item.getDesc());
		}
		
//		// 未读数
//		long unReadCount = cursor.getLong(cursor.getColumnIndex(MessageSummaryTable.COLUMN_UNREADCOUNT));
//		if (unReadCount > 0) {
//			viewHolder.mUnread.setVisibility(View.VISIBLE);
//			viewHolder.mUnread.setText(String.valueOf(unReadCount));
//		} else {
//			viewHolder.mUnread.setVisibility(View.GONE);
//		}
//		
//		// 群组点击事件
//		final long gid = cursor.getInt(cursor.getColumnIndex(MessageSummaryTable.COLUMN_MESSAGE_GROUP_ID));
//		final long unRead = cursor.getInt(cursor.getColumnIndex(MessageSummaryTable.COLUMN_UNREADCOUNT));
//		
//		// 个人点击事件
//		viewHolder.mMessageListItem.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View view) {
//				final String name = cursorContactGroup.getString(cursorContactGroup.getColumnIndex(ContactUserTable.COLUMN_NAME));
//				Intent intent = new Intent();
//				intent.setClass(mContext, ChatGroupActivity.class);
//				intent.putExtra("gid", String.valueOf(gid));
//				if (name != null) {
//					intent.putExtra("gname", name);
//				}
//				mContext.startActivity(intent);
//				
//				// 更新数据库
//				if (unRead > 0) {
//					ContentValues values = new ContentValues();
//					values.put(MessageSummaryTable.COLUMN_UNREADCOUNT, 0);
//					mContext.getContentResolver().update(GeneralContentProvider.MESSAGE_SUMMARY_CONTENT_URI, values, 
//							MessageSummaryTable.COLUMN_MESSAGE_DISPLAY_TYPE + " = ?" + " AND " + MessageSummaryTable.COLUMN_MESSAGE_GROUP_ID + " = ?", 
//							new String[] { String.valueOf(IMConstants.DISPLAY_LIST_TYPE_DIALOG_GROUP), String.valueOf(gid) });
//					values.clear();
//				}
//			}
//		});
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
