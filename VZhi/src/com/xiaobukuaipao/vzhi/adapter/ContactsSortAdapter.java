package com.xiaobukuaipao.vzhi.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.xiaobukuaipao.vzhi.R;
import com.xiaobukuaipao.vzhi.cache.CommonBitmapMemoryCache;
import com.xiaobukuaipao.vzhi.domain.social.ContactsSortModel;
import com.xiaobukuaipao.vzhi.util.StringUtils;
import com.xiaobukuaipao.vzhi.view.RoundedNetworkImageView;

/**
 *	联系人排序适配器
 *
 * @since 2015年01月05日20:46:35
 */
public class ContactsSortAdapter extends CommonAdapter<ContactsSortModel> implements SectionIndexer{

	private RequestQueue mQueue;
	private ImageLoader mImageLoader;
	
	public ContactsSortAdapter(Context mContext, List<ContactsSortModel> list) {
		super(mContext, list);
	}
	
	public ContactsSortAdapter(Context mContext, List<ContactsSortModel> list, int layoutId) {
		super(mContext, list,layoutId);
		mQueue = Volley.newRequestQueue(mContext);
		mImageLoader = new ImageLoader(mQueue, new CommonBitmapMemoryCache());
	}
	/**
	 * @param list
	 */
	public void updateListView(List<ContactsSortModel> list){
		this.mDatas = list;
		notifyDataSetChanged();
	}

	public int getCount() {
		return this.mDatas.size();
	}

	public ContactsSortModel getItem(int position) {
		return mDatas.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	/**
	 */
	public int getSectionForPosition(int position) {
        return mDatas.get(position).getSortLetters().charAt(0);
	}
	@Override
	public void convert(com.xiaobukuaipao.vzhi.adapter.ViewHolder viewHolder,
			ContactsSortModel item, int position) {
		TextView letters = viewHolder.getView(R.id.catalog);
		
		// 根据内容的实际位置得到分组的assci码的值（也就是ABCD...）
		int section = getSectionForPosition(position);
		
		if(position == getPositionForSection(section)){
			letters.setVisibility(View.VISIBLE);
			letters.setText(item.getSortLetters());
			viewHolder.getView(R.id.line).setVisibility(View.GONE);
		}else{
			letters.setVisibility(View.GONE);
			viewHolder.getView(R.id.line).setVisibility(View.VISIBLE);
		}
		String dot = mContext.getResources().getString(R.string.general_tips_dot);
		viewHolder.setText(R.id.title, item.getName());
		boolean splite = false;
		StringBuilder builder = new StringBuilder();
		if(StringUtils.isNotEmpty(item.getPosition())){
			builder.append(item.getPosition());
			splite = true;
		}
		if(StringUtils.isNotEmpty(item.getCorp())){
			if(splite){
				builder.append(dot);
			}
			builder.append(item.getCorp());
			splite = true;
		}else{
		}
		
		if(StringUtils.isNotEmpty(item.getCity())){
			if(splite){
				builder.append(dot);
			}
			builder.append(item.getCity());
		}
		
		viewHolder.setText(R.id.tag, builder.toString());
		RoundedNetworkImageView avatar = viewHolder.getView(R.id.avatar);
		avatar.setDefaultImageResId(R.drawable.general_user_avatar);
		avatar.setImageUrl(item.getAvatar(), mImageLoader);
		avatar.setBorderColor(mContext.getResources().getColor(item.getGender() == 1 ? R.color.bg_blue : R.color.bg_pink));
	}
    /**
     * 根据分类的首字母的Char ascii值获取其第一次出现该首字母对应内容的位置
     * @param section
     * @return
     */
	@SuppressLint("DefaultLocale")
	public int getPositionForSection(int section) {
		for (int i = 0; i < getCount(); i++) {
			String sortStr = mDatas.get(i).getSortLetters();
			char firstChar = sortStr.toUpperCase().charAt(0);
			if (firstChar == section) {
				return i;
			}
		}
		
		return -1;
	}
	
	/**
	 *
	 * @param str
	 * @return
	 */
	@SuppressLint("DefaultLocale")
	public String getAlpha(String str) {
		String  sortStr = str.trim().substring(0, 1).toUpperCase();
		if (sortStr.matches("[A-Z]")) {
			return sortStr;
		} else {
			return "#";
		}
	}

	@Override
	public Object[] getSections() {
		return null;
	}
}
