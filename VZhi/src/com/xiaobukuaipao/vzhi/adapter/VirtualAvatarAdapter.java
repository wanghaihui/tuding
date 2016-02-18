package com.xiaobukuaipao.vzhi.adapter;

import java.util.List;

import android.content.Context;
import android.widget.ImageView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.android.volley.toolbox.Volley;
import com.xiaobukuaipao.vzhi.R;
import com.xiaobukuaipao.vzhi.cache.CommonBitmapMemoryCache;

/**
 * 白领--虚拟头像Adapter
 * @author xiaobu
 *
 */
public class VirtualAvatarAdapter extends CommonAdapter<String> {

	// 请求队列
	private RequestQueue mQueue;
	private ImageLoader mImageLoader;
	private ImageListener mListener;
		
	public VirtualAvatarAdapter(Context mContext, List<String> mDatas,
			int mItemLayoutId) {
		super(mContext, mDatas, mItemLayoutId);
		mQueue = Volley.newRequestQueue(mContext);
		mImageLoader = new ImageLoader(mQueue, new CommonBitmapMemoryCache());
	}

	@Override
	public void convert(final ViewHolder viewHolder, final String item, final int position) {
		
		final ImageView oneAvatar = (ImageView) viewHolder.getView(R.id.avatar_head);
		
		mListener = ImageLoader.getImageListener(oneAvatar,  R.drawable.general_user_avatar, R.drawable.general_user_avatar);
		mImageLoader.get(item, mListener);
	}

}
