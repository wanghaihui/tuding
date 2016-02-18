package com.xiaobukuaipao.vzhi.adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.squareup.picasso.Picasso;
import com.umeng.analytics.MobclickAgent;
import com.xiaobukuaipao.vzhi.CorpInfoActivity;
import com.xiaobukuaipao.vzhi.JobPositionInfoActivity;
import com.xiaobukuaipao.vzhi.PersonalShowPageActivity;
import com.xiaobukuaipao.vzhi.R;
import com.xiaobukuaipao.vzhi.domain.JobInfo;
import com.xiaobukuaipao.vzhi.domain.PublishJobsInfo;
import com.xiaobukuaipao.vzhi.domain.PublisherInfo;
import com.xiaobukuaipao.vzhi.manager.GeneralDbManager;
import com.xiaobukuaipao.vzhi.util.GlobalConstants;
import com.xiaobukuaipao.vzhi.util.SpannableKeyWordBuilder;
import com.xiaobukuaipao.vzhi.util.StringUtils;
import com.xiaobukuaipao.vzhi.util.VToast;
import com.xiaobukuaipao.vzhi.view.RoundedImageView;

public class RecruitListViewAdapter2 extends CommonAdapter<PublishJobsInfo> {
	public static final String TAG = RecruitListViewAdapter2.class.getSimpleName();
	
	public Context mContext;
	
	private WindowManager wm;
	private DisplayMetrics metric;
	
	// 通用对象
	LinearLayout.LayoutParams lp;
	RelativeLayout.LayoutParams rlp;
	
	public RecruitListViewAdapter2(Context mContext, List<PublishJobsInfo> mDatas, int mItemLayoutId) {
		super(mContext, mDatas, mItemLayoutId);
		this.mContext = mContext;
		
		lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		rlp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, 
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		
		wm = (WindowManager) this.mContext.getSystemService(Context.WINDOW_SERVICE);
		metric = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(metric);
	}

	@Override
	public void convert(final ViewHolder viewHolder, final PublishJobsInfo item, final int position) {
		
		final RelativeLayout mPubJobOne = (RelativeLayout) viewHolder.getView(R.id.publisher_job_one);
		final RelativeLayout mPubJobTwo = (RelativeLayout) viewHolder.getView(R.id.publisher_job_two);
		final RelativeLayout mPubJobThree = (RelativeLayout) viewHolder.getView(R.id.publisher_job_three);
		// 设置头像
//		final RoundedNetworkImageView mPublisherOneAvatar = (RoundedNetworkImageView) viewHolder.getView(R.id.publisher_avatar);
		final RoundedImageView mPublisherOneAvatar = (RoundedImageView) viewHolder.getView(R.id.publisher_avatar);
		
		final View mPub_1 = (View) viewHolder.getView(R.id.divider_pub_1);
		final View mPub_2 = (View) viewHolder.getView(R.id.divider_pub_2);
		
		
		SpannableKeyWordBuilder sb = new SpannableKeyWordBuilder();
		
		// 初始化为不显示
		mPubJobOne.setVisibility(View.GONE);
		mPubJobTwo.setVisibility(View.GONE);
		mPubJobThree.setVisibility(View.GONE);
		
		mPublisherOneAvatar.setVisibility(View.GONE);
		// 分割线
		mPub_1.setVisibility(View.GONE);
		mPub_2.setVisibility(View.GONE);
		
		TextView pooHightlight = (TextView) viewHolder.getView(R.id.poo_high_light);
		TextView potHightlight = (TextView) viewHolder.getView(R.id.pot_high_light);
		TextView potrHightlight = (TextView) viewHolder.getView(R.id.potr_high_light);
		pooHightlight.setVisibility(View.GONE);
		potHightlight.setVisibility(View.GONE);
		potrHightlight.setVisibility(View.GONE);
		
		
		final JSONObject mPublisherObjOne = item.getPublichser();
		// 用来设置头像
		final PublisherInfo mPublisherOne = new PublisherInfo(mPublisherObjOne);
		
		final JSONArray mJobsArrayOne = item.getJobs();
	
		JobInfo mJobInfoOne = null;
		
		TextView publihserCorp = viewHolder.getView(R.id.publisher_corpinfo);
		if(StringUtils.isNotEmpty(item.publichser.getString(GlobalConstants.JSON_CORP))){
			publihserCorp.setText(item.publichser.getString(GlobalConstants.JSON_CORP));
		}
		// publihserName.setOnClickListener(new OnAvatarClick(String.valueOf(mPublisherOne.getIntId())));
		
		StringBuilder sbPp = new StringBuilder();
		sbPp.append("发布人：");
		sbPp.append(mPublisherOne.getPosition());
		sbPp.append(" ");
		sbPp.append(mContext.getResources().getString(R.string.general_tips_dot));
		sbPp.append(" ");
		
		viewHolder.setText(R.id.publisher_position, sbPp.toString());
		
		TextView publihserName = viewHolder.getView(R.id.publisher_name);
		if(StringUtils.isNotEmpty(mPublisherOne.getName())){
			publihserName.setText(mPublisherOne.getName());
		}
		publihserName.setOnClickListener(new OnNameClick(String.valueOf(mPublisherOne.getIntId())));
		
		// 设置头像
		mPublisherOneAvatar.setVisibility(View.VISIBLE);
		mPublisherOneAvatar.setOnClickListener(new OnAvatarClick(item.publichser.getString(GlobalConstants.JSON_CORPID)));
//		Logcat.d("@@@", "avatar: " + item.publichser.getString(GlobalConstants.JSON_CORPLOGO));
		if (StringUtils.isNotEmpty(item.publichser.getString(GlobalConstants.JSON_CORPLOGO))) {
			Picasso.with(mContext).load(item.publichser.getString(GlobalConstants.JSON_CORPLOGO)).
			placeholder(R.drawable.default_corp_logo).into(mPublisherOneAvatar);
		} else {
			mPublisherOneAvatar.setImageResource(R.drawable.default_corp_logo);
		}
		
		sb.delete(0, sb.length());
		// 点击相应的工作进入工作详情
		for (int j=0; j < mJobsArrayOne.size(); j++) {
			switch (j) {
			case 0:
				mPubJobOne.setVisibility(View.VISIBLE);
				mJobInfoOne = new JobInfo(mJobsArrayOne.getJSONObject(0));
				viewHolder.setText(R.id.poo_job_title, mJobInfoOne.getPosition().getString(GlobalConstants.JSON_NAME));
				viewHolder.setText(R.id.poo_job_time, mJobInfoOne.getRefreshtime());
				viewHolder.setText(R.id.publisher_refreshtime, mJobInfoOne.getRefreshtime());
				
				sb.appendKeyWord(mJobInfoOne.getSalary());
				sb.append(" / ");
				sb.append(mJobInfoOne.getExpr().getString(GlobalConstants.JSON_NAME));
				sb.append(" / ");
				sb.append(mJobInfoOne.getCity());
				
				((TextView) viewHolder.getView(R.id.poo_basic_info)).setText(sb);
				
				sb.delete(0, sb.length());
				
				if(StringUtils.isNotEmpty(mJobInfoOne.getHighlights())){
					sb.append(mContext.getString(R.string.general_highlights_tips, mJobInfoOne.getHighlights()));
					pooHightlight.setText(sb.toString());
					pooHightlight.setVisibility(View.VISIBLE);
				}
				sb.delete(0, sb.length());
				mPubJobOne.setOnClickListener(new OnJobClick(mJobInfoOne.getJobId()));
				break;
			case 1:
				mPubJobTwo.setVisibility(View.VISIBLE);
				mPub_1.setVisibility(View.VISIBLE);
				mJobInfoOne = new JobInfo(mJobsArrayOne.getJSONObject(1));
				((TextView) viewHolder.getView(R.id.pot_job_title)).setText(mJobInfoOne.getPosition().getString(GlobalConstants.JSON_NAME));
				((TextView) viewHolder.getView(R.id.pot_job_time)).setText(mJobInfoOne.getRefreshtime());
				sb.appendKeyWord(mJobInfoOne.getSalary());
				sb.append(" / ");
				sb.append(mJobInfoOne.getExpr().getString(GlobalConstants.JSON_NAME));
				sb.append(" / ");
				sb.append(mJobInfoOne.getCity());
				
				((TextView) viewHolder.getView(R.id.pot_basic_info)).setText(sb);
				sb.delete(0, sb.length());
				if(StringUtils.isNotEmpty(mJobInfoOne.getHighlights())){
					sb.append(mContext.getString(R.string.general_highlights_tips, mJobInfoOne.getHighlights()));
					potHightlight.setText(sb.toString());
					potHightlight.setVisibility(View.VISIBLE);
				}

				sb.delete(0, sb.length());
				mPubJobTwo.setOnClickListener(new OnJobClick(mJobInfoOne.getJobId()));
				break;
			case 2:
				mPubJobThree.setVisibility(View.VISIBLE);
				mPub_2.setVisibility(View.VISIBLE);
				mJobInfoOne = new JobInfo(mJobsArrayOne.getJSONObject(2));
				((TextView) viewHolder.getView(R.id.potr_job_title)).setText(mJobInfoOne.getPosition().getString(GlobalConstants.JSON_NAME));
				((TextView) viewHolder.getView(R.id.potr_job_time)).setText(mJobInfoOne.getRefreshtime());
				sb.appendKeyWord(mJobInfoOne.getSalary());
				sb.append(" / ");
				sb.append(mJobInfoOne.getExpr().getString(GlobalConstants.JSON_NAME));
				sb.append(" / ");
				sb.append(mJobInfoOne.getCity());
				
				((TextView) viewHolder.getView(R.id.potr_basic_info)).setText(sb);
				sb.delete(0, sb.length());
				
				if(StringUtils.isNotEmpty(mJobInfoOne.getHighlights())){
					sb.append(mContext.getString(R.string.general_highlights_tips, mJobInfoOne.getHighlights()));
					potrHightlight.setText(sb.toString());
					potrHightlight.setVisibility(View.VISIBLE);
				}
				sb.delete(0, sb.length());
				
				mPubJobThree.setOnClickListener(new OnJobClick(mJobInfoOne.getJobId()));
				break;
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
	class OnAvatarClick implements OnClickListener{

		String id;
		public OnAvatarClick(String id){
			this.id = id; 
		}
		
		@Override
		public void onClick(View v) {
			/*if(GeneralDbManager.getInstance().isExistCookie()){
				MobclickAgent.onEvent(mContext, "rwtx");
				Intent intent = new Intent();
				intent.setClass(mContext, PersonalShowPageActivity.class);
				intent.putExtra(GlobalConstants.UID, id);
				mContext.startActivity(intent);
			}else{
				VToast.show(mContext, mContext.getString(R.string.general_tips_default_unlogin));
			}*/
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
	
	
}
