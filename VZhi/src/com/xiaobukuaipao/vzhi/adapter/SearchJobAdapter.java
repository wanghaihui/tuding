package com.xiaobukuaipao.vzhi.adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.umeng.analytics.MobclickAgent;
import com.xiaobukuaipao.vzhi.CorpInfoActivity;
import com.xiaobukuaipao.vzhi.JobPositionInfoActivity;
import com.xiaobukuaipao.vzhi.PersonalShowPageActivity;
import com.xiaobukuaipao.vzhi.R;
import com.xiaobukuaipao.vzhi.domain.SearchJob;
import com.xiaobukuaipao.vzhi.manager.GeneralDbManager;
import com.xiaobukuaipao.vzhi.util.GlobalConstants;
import com.xiaobukuaipao.vzhi.util.SpannableKeyWordBuilder;
import com.xiaobukuaipao.vzhi.util.StringUtils;
import com.xiaobukuaipao.vzhi.util.VToast;
import com.xiaobukuaipao.vzhi.view.RoundedImageView;

public class SearchJobAdapter extends CommonAdapter<SearchJob> {

	public SearchJobAdapter(Context mContext, List<SearchJob> mDatas,
			int mItemLayoutId) {
		super(mContext, mDatas, mItemLayoutId);
	}

	@Override
	public void convert(final ViewHolder viewHolder, final SearchJob item, final int position) {
		final RelativeLayout mPubJobOne = (RelativeLayout) viewHolder.getView(R.id.publisher_job_one);
		
		final RoundedImageView mPublisherOneAvatar = (RoundedImageView) viewHolder.getView(R.id.publisher_avatar);
		
		SpannableKeyWordBuilder sb = new SpannableKeyWordBuilder();
		
		mPublisherOneAvatar.setOnClickListener(new OnAvatarClick(item.getCorp().getString(GlobalConstants.JSON_ID)));
		if (StringUtils.isNotEmpty(item.getCorp().getString(GlobalConstants.JSON_LOGO))) {
			Picasso.with(mContext).load(item.getCorp().getString(GlobalConstants.JSON_LOGO)).
			placeholder(R.drawable.default_corp_logo).into(mPublisherOneAvatar);
		} else {
			mPublisherOneAvatar.setImageResource(R.drawable.default_corp_logo);
		}
		
		if(StringUtils.isNotEmpty(item.getCorp().getString(GlobalConstants.JSON_NAME))){
			viewHolder.setText(R.id.publisher_corpinfo, item.getCorp().getString(GlobalConstants.JSON_NAME));
		}
		
		StringBuilder sbPp = new StringBuilder();
		sbPp.append("发布人：");
		sbPp.append(item.getPublisher().getString(GlobalConstants.JSON_POSITION));
		sbPp.append(" ");
		sbPp.append(mContext.getResources().getString(R.string.general_tips_dot));
		sbPp.append(" ");
		
		viewHolder.setText(R.id.publisher_position, sbPp.toString());
		
		TextView publihserName = viewHolder.getView(R.id.publisher_name);
		if(StringUtils.isNotEmpty(item.getPublisher().getString(GlobalConstants.JSON_NAME))){
			publihserName.setText(item.getPublisher().getString(GlobalConstants.JSON_NAME));
		}
		publihserName.setOnClickListener(new OnNameClick(item.getPublisher().getString(GlobalConstants.JSON_ID)));
		
		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		
		viewHolder.setText(R.id.poo_job_title, item.getPosition().getString(GlobalConstants.JSON_NAME));
		
		viewHolder.setText(R.id.publisher_refreshtime, item.getRefreshtime());
		
		sb.appendKeyWord(item.getSalary().getString(GlobalConstants.JSON_NAME));
		sb.append(" / ");
		sb.append(item.getExpr().getString(GlobalConstants.JSON_NAME));
		sb.append(" / ");
		sb.append(item.getCity());
		
		((TextView) viewHolder.getView(R.id.poo_basic_info)).setText(sb);
		
		sb.delete(0, sb.length());
		
		TextView pooHightlight = (TextView) viewHolder.getView(R.id.poo_high_light);
		if(StringUtils.isNotEmpty(item.getHighlights())){
			sb.append(mContext.getString(R.string.general_highlights_tips, item.getHighlights()));
			pooHightlight.setText(sb.toString());
			pooHightlight.setVisibility(View.VISIBLE);
		}
		sb.delete(0, sb.length());
		
		mPubJobOne.setOnClickListener(new OnJobClick(Integer.valueOf(item.getId())));
		
	}
	
	class OnAvatarClick implements OnClickListener{

		String id;
		public OnAvatarClick(String id){
			this.id = id; 
		}
		
		@Override
		public void onClick(View v) {
			if(GeneralDbManager.getInstance().isExistCookie()){
				Intent intent = new Intent();
				intent.putExtra(GlobalConstants.CORP_ID, id);
				intent.setClass(mContext, CorpInfoActivity.class);
				mContext.startActivity(intent);
			}else{
				VToast.show(mContext, mContext.getString(R.string.general_tips_default_unlogin));
			}
		}
	}
	
	class OnNameClick implements OnClickListener{

		String id;
		public OnNameClick(String id){
			this.id = id; 
		}
		
		@Override
		public void onClick(View v) {
			if(GeneralDbManager.getInstance().isExistCookie()){
				Intent intent = new Intent();
				intent.setClass(mContext, PersonalShowPageActivity.class);
				intent.putExtra(GlobalConstants.UID, id);
				mContext.startActivity(intent);
			}else{
				VToast.show(mContext, mContext.getString(R.string.general_tips_default_unlogin));
			}
		}
	}
	
	class OnJobClick implements OnClickListener{

		int id;
		public OnJobClick(int id){
			this.id = id; 
		}
		
		@Override
		public void onClick(View v) {
			MobclickAgent.onEvent(mContext, "gzjh");
			if(GeneralDbManager.getInstance().isExistCookie()){
				Intent intent = new Intent();
				intent.putExtra(GlobalConstants.JOB_ID, id);
				intent.setClass(mContext, JobPositionInfoActivity.class);
				mContext.startActivity(intent);
			}else{
				VToast.show(mContext, mContext.getString(R.string.general_tips_default_unlogin));
			}
		}
	}

}
