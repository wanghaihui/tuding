package com.xiaobukuaipao.vzhi.adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.xiaobukuaipao.vzhi.CorpInfoActivity;
import com.xiaobukuaipao.vzhi.JobPositionInfoActivity;
import com.xiaobukuaipao.vzhi.PersonalShowPageActivity;
import com.xiaobukuaipao.vzhi.R;
import com.xiaobukuaipao.vzhi.cache.CommonBitmapMemoryCache;
import com.xiaobukuaipao.vzhi.domain.CorpInfo;
import com.xiaobukuaipao.vzhi.domain.CorpPublishJobsInfo;
import com.xiaobukuaipao.vzhi.domain.JobInfo;
import com.xiaobukuaipao.vzhi.domain.PublisherInfo;
import com.xiaobukuaipao.vzhi.manager.GeneralDbManager;
import com.xiaobukuaipao.vzhi.util.DisplayUtil;
import com.xiaobukuaipao.vzhi.util.GlobalConstants;
import com.xiaobukuaipao.vzhi.util.SpannableKeyWordBuilder;
import com.xiaobukuaipao.vzhi.util.StringUtils;
import com.xiaobukuaipao.vzhi.util.VToast;
import com.xiaobukuaipao.vzhi.view.RoundedNetworkImageView;

public class RecruitListViewAdapter extends CommonAdapter<CorpPublishJobsInfo> {
	public static final String TAG = RecruitListViewAdapter.class.getSimpleName();
	
	public Context mContext;
	// 请求队列
	private RequestQueue mQueue;
	private ImageLoader mImageLoader;
	
	private WindowManager wm;
	private DisplayMetrics metric;
	
	// 通用对象
	LinearLayout.LayoutParams lp;
	RelativeLayout.LayoutParams rlp;
	
	public RecruitListViewAdapter(Context mContext, List<CorpPublishJobsInfo> mDatas, int mItemLayoutId) {
		super(mContext, mDatas, mItemLayoutId);
		this.mContext = mContext;
		
		mQueue = Volley.newRequestQueue(mContext);
		mImageLoader = new ImageLoader(mQueue, new CommonBitmapMemoryCache());
		
		lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		rlp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, 
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		
		wm = (WindowManager) this.mContext.getSystemService(Context.WINDOW_SERVICE);
		metric = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(metric);
	}

	@Override
	public void convert(final ViewHolder viewHolder, final CorpPublishJobsInfo item, final int position) {
		
		final CorpInfo mCorpInfo = new CorpInfo(item.mCorp);
		// 公司名称
		if (mCorpInfo.getName() != null) {
			viewHolder.setText(R.id.recruit_crop, mCorpInfo.getName());
		}
		
		final RoundedNetworkImageView mCorpLogo = (RoundedNetworkImageView) viewHolder.getView(R.id.logo_crop_standard);

		mCorpLogo.setOnClickListener(new OnCorpClick(mCorpInfo.getId()));
		mCorpLogo.setDefaultImageResId(R.drawable.default_corp_logo);
		mCorpLogo.setImageUrl(mCorpInfo.getLogo(), mImageLoader);
		
		
		final TextView mBenefitsOne = (TextView) viewHolder.getView(R.id.benefits_one);
		final TextView mBenefitsTwo = (TextView) viewHolder.getView(R.id.benefits_two);
		final TextView mBenefitsThree = (TextView) viewHolder.getView(R.id.benefits_three);
		
		mBenefitsOne.setVisibility(View.GONE);
		mBenefitsTwo.setVisibility(View.GONE);
		mBenefitsThree.setVisibility(View.GONE);
		
		
		int childCountWidth = 0;
		
		// 公司福利
		if (mCorpInfo.getBenefits() != null && mCorpInfo.getBenefits().size() > 0) {
			
			for (int i=0; i < mCorpInfo.getBenefits().size(); i++) {
				switch (i) {
				case 0:
					mBenefitsOne.setVisibility(View.VISIBLE);
					mBenefitsOne.setText((String) mCorpInfo.getBenefits().getJSONObject(0).getString(GlobalConstants.JSON_NAME));
					
					mBenefitsOne.measure(mBenefitsOne.getWidth(),  MeasureSpec.UNSPECIFIED);
					childCountWidth += mBenefitsOne.getMeasuredWidth();
					if (childCountWidth > (metric.widthPixels - DisplayUtil.dip2px(mContext, 85))) {
						mBenefitsOne.setVisibility(View.GONE);
						childCountWidth = 0;
					}
					break;
				case 1:
					mBenefitsTwo.setVisibility(View.VISIBLE);
					mBenefitsTwo.setText((String) mCorpInfo.getBenefits().getJSONObject(1).getString(GlobalConstants.JSON_NAME));
					
					mBenefitsTwo.measure(mBenefitsTwo.getWidth(),  MeasureSpec.UNSPECIFIED);
					childCountWidth += mBenefitsTwo.getMeasuredWidth();
					if (childCountWidth > (metric.widthPixels - DisplayUtil.dip2px(mContext, 85))) {
						mBenefitsTwo.setVisibility(View.GONE);
						childCountWidth = 0;
					}
					break;
				case 2:
					mBenefitsThree.setVisibility(View.VISIBLE);
					mBenefitsThree.setText((String) mCorpInfo.getBenefits().getJSONObject(2).getString(GlobalConstants.JSON_NAME));
					
					mBenefitsThree.measure(mBenefitsThree.getWidth(),  MeasureSpec.UNSPECIFIED);
					childCountWidth += mBenefitsThree.getMeasuredWidth();
					if (childCountWidth > (metric.widthPixels - DisplayUtil.dip2px(mContext, 85))) {
						mBenefitsThree.setVisibility(View.GONE);
						childCountWidth = 0;
					}
					break;
				}
			}
		}
		
		// 默认缺省公司福利
		if (childCountWidth == 0) {
			mBenefitsOne.setVisibility(View.VISIBLE);
			mBenefitsOne.setText(R.string.general_tips_default_benefits);
		}
		
		// HR and Jobs
		viewHolder.getView(R.id.recruit_publisherJob_one).setVisibility(View.GONE);
		viewHolder.getView(R.id.recruit_publisherJob_two).setVisibility(View.GONE);
		viewHolder.getView(R.id.recruit_publisherJob_three).setVisibility(View.GONE);
		
		final RelativeLayout mPubOneJobOne = (RelativeLayout) viewHolder.getView(R.id.publisher_one_job_one);
		final RelativeLayout mPubOneJobTwo = (RelativeLayout) viewHolder.getView(R.id.publisher_one_job_two);
		final RelativeLayout mPubOneJobThree = (RelativeLayout) viewHolder.getView(R.id.publisher_one_job_three);
		
		final RelativeLayout mPubTwoJobOne = (RelativeLayout) viewHolder.getView(R.id.publisher_two_job_one);
		final RelativeLayout mPubTwoJobTwo = (RelativeLayout) viewHolder.getView(R.id.publisher_two_job_two);
		final RelativeLayout mPubTwoJobThree = (RelativeLayout) viewHolder.getView(R.id.publisher_two_job_three);
		
		final RelativeLayout mPubThreeJobOne = (RelativeLayout) viewHolder.getView(R.id.publisher_three_job_one);
		final RelativeLayout mPubThreeJobTwo = (RelativeLayout) viewHolder.getView(R.id.publisher_three_job_two);
		final RelativeLayout mPubThreeJobThree = (RelativeLayout) viewHolder.getView(R.id.publisher_three_job_three);
		
		// 设置头像
		final RoundedNetworkImageView mPublisherOneAvatar = (RoundedNetworkImageView) viewHolder.getView(R.id.publisher_one_avatar);
		final RoundedNetworkImageView mPublisherTwoAvatar = (RoundedNetworkImageView) viewHolder.getView(R.id.publisher_two_avatar);
		final RoundedNetworkImageView mPublisherThreeAvatar = (RoundedNetworkImageView) viewHolder.getView(R.id.publisher_three_avatar);
		
		final View mPubOne_1 = (View) viewHolder.getView(R.id.divider_pub_one_1);
		final View mPubOne_2 = (View) viewHolder.getView(R.id.divider_pub_one_2);
		final View mPubTwo_1 = (View) viewHolder.getView(R.id.divider_pub_two_1);
		final View mPubTwo_2 = (View) viewHolder.getView(R.id.divider_pub_three_1);
		final View mPubThree_1 = (View) viewHolder.getView(R.id.divider_pub_three_1);
		final View mPubThree_2 = (View) viewHolder.getView(R.id.divider_pub_three_2);
		
		final View mDividerPublisherJobOne = (View) viewHolder.getView(R.id.divider_publisherJob_one);
		final View mDividerPublisherJobTwo = (View) viewHolder.getView(R.id.divider_publisherJob_two);
		
		SpannableKeyWordBuilder sb = new SpannableKeyWordBuilder();
		
		// 初始化为不显示
		mPubOneJobOne.setVisibility(View.GONE);
		mPubOneJobTwo.setVisibility(View.GONE);
		mPubOneJobThree.setVisibility(View.GONE);
		mPubTwoJobOne.setVisibility(View.GONE);
		mPubTwoJobTwo.setVisibility(View.GONE);
		mPubTwoJobThree.setVisibility(View.GONE);
		mPubThreeJobOne.setVisibility(View.GONE);
		mPubThreeJobTwo.setVisibility(View.GONE);
		mPubThreeJobThree.setVisibility(View.GONE);
		mPublisherOneAvatar.setVisibility(View.GONE);
		mPublisherTwoAvatar.setVisibility(View.GONE);
		mPublisherThreeAvatar.setVisibility(View.GONE);
		// 分割线
		mPubOne_1.setVisibility(View.GONE);
		mPubOne_2.setVisibility(View.GONE);
		mPubTwo_1.setVisibility(View.GONE);
		mPubTwo_2.setVisibility(View.GONE);
		mPubThree_1.setVisibility(View.GONE);
		mPubThree_2.setVisibility(View.GONE);
		
		mDividerPublisherJobOne.setVisibility(View.GONE);
		mDividerPublisherJobTwo.setVisibility(View.GONE);
		
		for (int i=0; i < item.mPublisherJobs.size(); i++) {
			switch (i) {
				case 0:
					viewHolder.getView(R.id.recruit_publisherJob_one).setVisibility(View.VISIBLE);
	
					final JSONObject mPublisherObjOne = item.mPublisherJobs.getJSONObject(0).getJSONObject(GlobalConstants.JSON_PUBLISHER);
					// 用来设置头像
					final PublisherInfo mPublisherOne = new PublisherInfo(mPublisherObjOne);
					
					final JSONArray mJobsArrayOne = item.mPublisherJobs.getJSONObject(0).getJSONArray(GlobalConstants.JSON_JOBS);
					JobInfo mJobInfoOne = null;
					
					// 设置头像
					mPublisherOneAvatar.setVisibility(View.VISIBLE);
					mPublisherOneAvatar.setOnClickListener(new OnAvatarClick(String.valueOf(mPublisherOne.getIntId())));
					mPublisherOneAvatar.setBorderColor(mContext.getResources().getColor(mPublisherOne.getGender() == 1 ? R.color.boy_border_color : R.color.girl_border_color));
					mPublisherOneAvatar.setDefaultImageResId(R.drawable.general_user_avatar);
					mPublisherOneAvatar.setImageUrl(mPublisherOne.getAvatar(), mImageLoader);
					sb.delete(0, sb.length());
					
					// 点击相应的工作进入工作详情
					
					for (int j=0; j < mJobsArrayOne.size(); j++) {
						switch (j) {
							case 0:
								mPubOneJobOne.setVisibility(View.VISIBLE);
								
								mJobInfoOne = new JobInfo(mJobsArrayOne.getJSONObject(0));
								((TextView) viewHolder.getView(R.id.poo_job_title)).setText(mJobInfoOne.getPosition().getString(GlobalConstants.JSON_NAME));
								((TextView) viewHolder.getView(R.id.poo_job_time)).setText(mJobInfoOne.getRefreshtime());
								sb.appendKeyWord(mJobInfoOne.getSalary());
								sb.append(" / ");
								sb.append(mJobInfoOne.getExpr().getString(GlobalConstants.JSON_NAME));
								sb.append(" / ");
								sb.append(mJobInfoOne.getCity());
								
								((TextView) viewHolder.getView(R.id.poo_basic_info)).setText(sb);
								
								sb.delete(0, sb.length());
								
								if(StringUtils.isNotEmpty(mJobInfoOne.getHighlights())){
									sb.append(mContext.getString(R.string.general_highlights_tips, mJobInfoOne.getHighlights()));
									((TextView) viewHolder.getView(R.id.poo_high_light)).setText(sb.toString());
								}
								sb.delete(0, sb.length());
								mPubOneJobOne.setOnClickListener(new OnJobClick(mJobInfoOne.getJobId()));
								break;
							case 1:
								mPubOneJobTwo.setVisibility(View.VISIBLE);
								mPubOne_1.setVisibility(View.VISIBLE);
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
									((TextView) viewHolder.getView(R.id.pot_high_light)).setText(sb.toString());
								}

								sb.delete(0, sb.length());
								mPubOneJobTwo.setOnClickListener(new OnJobClick(mJobInfoOne.getJobId()));
								break;
							case 2:
								mPubOneJobThree.setVisibility(View.VISIBLE);
								mPubOne_2.setVisibility(View.VISIBLE);
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
									((TextView) viewHolder.getView(R.id.potr_high_light)).setText(sb.toString());
								}
								sb.delete(0, sb.length());
								
								mPubOneJobThree.setOnClickListener(new OnJobClick(mJobInfoOne.getJobId()));
								break;
						}
					}
					
					break;
				case 1:
					viewHolder.getView(R.id.recruit_publisherJob_two).setVisibility(View.VISIBLE);
					// 分割线
					mDividerPublisherJobOne.setVisibility(View.VISIBLE);
					
					final JSONObject mPublisherObjTwo = item.mPublisherJobs.getJSONObject(1).getJSONObject(GlobalConstants.JSON_PUBLISHER);
					// 用来设置头像
					final PublisherInfo mPublisherTwo = new PublisherInfo(mPublisherObjTwo);
					final JSONArray mJobsArrayTwo = item.mPublisherJobs.getJSONObject(1).getJSONArray(GlobalConstants.JSON_JOBS);
					JobInfo mJobInfoTwo = null;
					
					// 设置头像
					mPublisherTwoAvatar.setVisibility(View.VISIBLE);
					mPublisherTwoAvatar.setOnClickListener(new OnAvatarClick(String.valueOf(mPublisherTwo.getIntId())));
					
					mPublisherTwoAvatar.setBorderColor(mContext.getResources().getColor(mPublisherTwo.getGender() == 1 ? R.color.boy_border_color : R.color.girl_border_color));
					mPublisherTwoAvatar.setDefaultImageResId(R.drawable.general_user_avatar);
					mPublisherTwoAvatar.setImageUrl(mPublisherTwo.getAvatar(), mImageLoader);
					
					sb.delete(0, sb.length());
					
					for (int j=0; j < mJobsArrayTwo.size(); j++) {
						switch (j) {
							case 0:
								mPubTwoJobOne.setVisibility(View.VISIBLE);
								mJobInfoTwo = new JobInfo(mJobsArrayTwo.getJSONObject(0));
								((TextView) viewHolder.getView(R.id.pto_job_title)).setText(mJobInfoTwo.getPosition().getString(GlobalConstants.JSON_NAME));
								((TextView) viewHolder.getView(R.id.pto_job_time)).setText(mJobInfoTwo.getRefreshtime());
								sb.appendKeyWord(mJobInfoTwo.getSalary());
								sb.append(" / ");
								sb.append(mJobInfoTwo.getExpr().getString(GlobalConstants.JSON_NAME));
								sb.append(" / ");
								sb.append(mJobInfoTwo.getCity());
								
								((TextView) viewHolder.getView(R.id.pto_basic_info)).setText(sb);
								sb.delete(0, sb.length());
								
								if(StringUtils.isNotEmpty(mJobInfoTwo.getHighlights())){
									sb.append(mContext.getString(R.string.general_highlights_tips, mJobInfoTwo.getHighlights()));
									((TextView) viewHolder.getView(R.id.pto_high_light)).setText(sb.toString());
								}

								sb.delete(0, sb.length());
								//
								mPubTwoJobOne.setOnClickListener(new OnJobClick(mJobInfoTwo.getJobId()));
								break;
							case 1:
								mPubTwoJobTwo.setVisibility(View.VISIBLE);
								mPubTwo_1.setVisibility(View.VISIBLE);
								mJobInfoTwo = new JobInfo(mJobsArrayTwo.getJSONObject(1));
								((TextView) viewHolder.getView(R.id.ptt_job_title)).setText(mJobInfoTwo.getPosition().getString(GlobalConstants.JSON_NAME));
								((TextView) viewHolder.getView(R.id.ptt_job_time)).setText(mJobInfoTwo.getRefreshtime());
								sb.appendKeyWord(mJobInfoTwo.getSalary());
								sb.append(" / ");
								sb.append(mJobInfoTwo.getExpr().getString(GlobalConstants.JSON_NAME));
								sb.append(" / ");
								sb.append(mJobInfoTwo.getCity());
								
								((TextView) viewHolder.getView(R.id.ptt_basic_info)).setText(sb);
								sb.delete(0, sb.length());
								
								if(StringUtils.isNotEmpty(mJobInfoTwo.getHighlights())){
									sb.append(mContext.getString(R.string.general_highlights_tips, mJobInfoTwo.getHighlights()));
									((TextView) viewHolder.getView(R.id.ptt_high_light)).setText(sb.toString());
								}
								
								sb.delete(0, sb.length());
								
								mPubTwoJobTwo.setOnClickListener(new OnJobClick(mJobInfoTwo.getJobId()));
								break;
							case 2:
								mPubTwoJobThree.setVisibility(View.VISIBLE);
								mPubTwo_2.setVisibility(View.GONE);
								mJobInfoTwo = new JobInfo(mJobsArrayTwo.getJSONObject(2));
								((TextView) viewHolder.getView(R.id.pttr_job_title)).setText(mJobInfoTwo.getPosition().getString(GlobalConstants.JSON_NAME));
								((TextView) viewHolder.getView(R.id.pttr_job_time)).setText(mJobInfoTwo.getRefreshtime());
								sb.appendKeyWord(mJobInfoTwo.getSalary());
								sb.append(" / ");
								sb.append(mJobInfoTwo.getExpr().getString(GlobalConstants.JSON_NAME));
								sb.append(" / ");
								sb.append(mJobInfoTwo.getCity());
								
								((TextView) viewHolder.getView(R.id.pttr_basic_info)).setText(sb);
								sb.delete(0, sb.length());
								
								if(StringUtils.isNotEmpty(mJobInfoTwo.getHighlights())){
									sb.append(mContext.getString(R.string.general_highlights_tips, mJobInfoTwo.getHighlights()));
									((TextView) viewHolder.getView(R.id.pttr_high_light)).setText(sb.toString());
								}

								sb.delete(0, sb.length());
								
								mPubTwoJobThree.setOnClickListener(new OnJobClick(mJobInfoTwo.getJobId()));
								break;
						}
					}
					
					break;
				case 2:
					viewHolder.getView(R.id.recruit_publisherJob_three).setVisibility(View.VISIBLE);
					mDividerPublisherJobTwo.setVisibility(View.GONE);
					
					final JSONObject mPublisherObjThree = item.mPublisherJobs.getJSONObject(1).getJSONObject(GlobalConstants.JSON_PUBLISHER);
					// 用来设置头像
					final PublisherInfo mPublisherThree = new PublisherInfo(mPublisherObjThree);
					final JSONArray mJobsArrayThree = item.mPublisherJobs.getJSONObject(1).getJSONArray(GlobalConstants.JSON_JOBS);
					JobInfo mJobInfoThree = null;
					
					// 设置头像
					mPublisherThreeAvatar.setVisibility(View.VISIBLE);
					mPublisherThreeAvatar.setOnClickListener(new OnAvatarClick(String.valueOf(mPublisherThree.getIntId())));
					mPublisherThreeAvatar.setBorderColor(mContext.getResources().getColor(mPublisherThree.getGender() == 1 ? R.color.boy_border_color : R.color.girl_border_color));
					mPublisherOneAvatar.setDefaultImageResId(R.drawable.general_user_avatar);
					mPublisherOneAvatar.setImageUrl(mPublisherThree.getAvatar(), mImageLoader);
					sb.delete(0, sb.length());
					
					for (int j=0; j < mJobsArrayThree.size(); j++) {
						switch (j) {
							case 0:
								mPubThreeJobOne.setVisibility(View.VISIBLE);
								mJobInfoThree = new JobInfo(mJobsArrayThree.getJSONObject(0));
								((TextView) viewHolder.getView(R.id.ptro_job_title)).setText(mJobInfoThree.getPosition().getString(GlobalConstants.JSON_NAME));
								((TextView) viewHolder.getView(R.id.ptro_job_time)).setText(mJobInfoThree.getRefreshtime());
								sb.appendKeyWord(mJobInfoThree.getSalary());
								sb.append(" / ");
								sb.append(mJobInfoThree.getExpr().getString(GlobalConstants.JSON_NAME));
								sb.append(" / ");
								sb.append(mJobInfoThree.getCity());
								
								((TextView) viewHolder.getView(R.id.ptro_basic_info)).setText(sb);
								sb.delete(0, sb.length());
								
								if (StringUtils.isNotEmpty(mJobInfoThree.getHighlights())) {
									sb.append(mContext.getString(R.string.general_highlights_tips, mJobInfoThree.getHighlights()));
									((TextView) viewHolder.getView(R.id.ptro_high_light)).setText(sb.toString());
								}

								sb.delete(0, sb.length());
								
								mPubThreeJobOne.setOnClickListener(new OnJobClick(mJobInfoThree.getJobId()));
								break;
							case 1:
								mPubThreeJobTwo.setVisibility(View.VISIBLE);
								mPubThree_1.setVisibility(View.VISIBLE);
								mJobInfoThree = new JobInfo(mJobsArrayThree.getJSONObject(1));
								((TextView) viewHolder.getView(R.id.ptrt_job_title)).setText(mJobInfoThree.getPosition().getString(GlobalConstants.JSON_NAME));
								((TextView) viewHolder.getView(R.id.ptrt_job_time)).setText(mJobInfoThree.getRefreshtime());
								sb.appendKeyWord(mJobInfoThree.getSalary());
								sb.append(" / ");
								sb.append(mJobInfoThree.getExpr().getString(GlobalConstants.JSON_NAME));
								sb.append(" / ");
								sb.append(mJobInfoThree.getCity());
								
								((TextView) viewHolder.getView(R.id.ptrt_basic_info)).setText(sb);
								sb.delete(0, sb.length());
								
								if(StringUtils.isNotEmpty(mJobInfoThree.getHighlights())){
									sb.append(mContext.getString(R.string.general_highlights_tips, mJobInfoThree.getHighlights()));
									((TextView) viewHolder.getView(R.id.ptrt_high_light)).setText(sb.toString());
								}

								sb.delete(0, sb.length());
								
								mPubThreeJobTwo.setOnClickListener(new OnJobClick(mJobInfoThree.getJobId()));
								break;
							case 2:
								mPubThreeJobThree.setVisibility(View.VISIBLE);
								mPubThree_2.setVisibility(View.VISIBLE);
								mJobInfoThree = new JobInfo(mJobsArrayThree.getJSONObject(2));
								((TextView) viewHolder.getView(R.id.ptrtr_job_title)).setText(mJobInfoThree.getPosition().getString(GlobalConstants.JSON_NAME));
								((TextView) viewHolder.getView(R.id.ptrtr_job_time)).setText(mJobInfoThree.getRefreshtime());
								sb.appendKeyWord(mJobInfoThree.getSalary());
								sb.append(" / ");
								sb.append(mJobInfoThree.getExpr().getString(GlobalConstants.JSON_NAME));
								sb.append(" / ");
								sb.append(mJobInfoThree.getCity());
								
								((TextView) viewHolder.getView(R.id.ptrtr_basic_info)).setText(sb);
								sb.delete(0, sb.length());
								
								if(StringUtils.isNotEmpty(mJobInfoThree.getHighlights())){
									sb.append(mContext.getString(R.string.general_highlights_tips, mJobInfoThree.getHighlights()));
									((TextView) viewHolder.getView(R.id.ptrtr_high_light)).setText(sb.toString());
								}

								sb.delete(0, sb.length());
								
								mPubThreeJobThree.setOnClickListener(new OnJobClick(mJobInfoThree.getJobId()));
								break;
						}
					}
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
	class OnCorpClick implements OnClickListener{

		String id;
		public OnCorpClick(String id){
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
	class OnAvatarClick implements OnClickListener{

		String id;
		public OnAvatarClick(String id){
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
