package com.xiaobukuaipao.vzhi;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Message;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ScrollView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.xiaobukuaipao.vzhi.adapter.CorpPositionsAdapter;
import com.xiaobukuaipao.vzhi.cache.CommonBitmapMemoryCache;
import com.xiaobukuaipao.vzhi.domain.CorpInfo;
import com.xiaobukuaipao.vzhi.domain.JobInfo;
import com.xiaobukuaipao.vzhi.event.InfoResult;
import com.xiaobukuaipao.vzhi.util.GlobalConstants;
import com.xiaobukuaipao.vzhi.util.StringUtils;
import com.xiaobukuaipao.vzhi.view.LinesTextView;
import com.xiaobukuaipao.vzhi.view.ListViewForScrollView;
import com.xiaobukuaipao.vzhi.view.RoundedNetworkImageView;
import com.xiaobukuaipao.vzhi.wrap.RecruitWrapActivity;

public class CorpInfoActivity extends RecruitWrapActivity implements OnItemClickListener{

	private String corpId;
	
	private RoundedNetworkImageView mCorpLogo;
	private TextView mCorpTitle;
	private TextView mCorpName;
	private LinesTextView mBenefits;
	
	private TextView mIndustry;
	private TextView mType;
	private TextView mScale;
	private TextView mCity;
	private TextView mHomepage;
	
	private TextView mDesc;
	
	private ListViewForScrollView mPositions;

	private CorpInfo mCorpInfo;
	// 请求队列
	private RequestQueue mQueue;
	private ImageLoader mImageLoader;

	private List<String> mBenefitsList;
	
	private List<JobInfo> mDatas;

	private CorpPositionsAdapter positionsAdapter;

	private ScrollView mLayout;
	
	@Override
	public void initUIAndData() {
		setContentView(R.layout.activity_company_detail_info);
		setHeaderMenuByLeft(this);
		setHeaderMenuByCenterTitle(R.string.corp_info);
		mLayout = (ScrollView) findViewById(R.id.layout);
		mLayout.setVisibility(View.INVISIBLE);
		mLayout.smoothScrollTo(0, 0);
		
		corpId = getIntent().getStringExtra(GlobalConstants.CORP_ID);
		
		mCorpLogo = (RoundedNetworkImageView) findViewById(R.id.logo_corp);
		mCorpTitle = (TextView) findViewById(R.id.recruit_corp);
		mCorpName = (TextView) findViewById(R.id.recruit_corp_ltd);
		mBenefits = (LinesTextView) findViewById(R.id.corp_benefits);
		
		
		mIndustry = (TextView) findViewById(R.id.corp_industry);
		mType = (TextView) findViewById(R.id.corp_type);
		mScale = (TextView) findViewById(R.id.corp_scale);
		mCity = (TextView) findViewById(R.id.corp_city);
		mHomepage = (TextView) findViewById(R.id.corp_homepage);
		mDesc = (TextView) findViewById(R.id.corp_desc);
		mPositions = (ListViewForScrollView) findViewById(R.id.corp_position_list);
		
		// 福利标签
		mBenefitsList = new ArrayList<String>();
		mQueue = Volley.newRequestQueue(this);
		mImageLoader = new ImageLoader(mQueue, new CommonBitmapMemoryCache());
		
		mDatas = new ArrayList<JobInfo>();
		positionsAdapter = new CorpPositionsAdapter(this,mDatas,R.layout.item_corp_position);
		
		mPositions.setAdapter(positionsAdapter);
		mPositions.setOnItemClickListener(this);
		
		if(StringUtils.isNotEmpty(corpId)){
			mRegisterEventLogic.getCorpInfo(corpId);
			mRegisterEventLogic.getCorpPositions(corpId, -1);
		}

	}

	@Override
	public void onEventMainThread(Message msg) {
		
		if(msg.obj instanceof InfoResult){
			InfoResult infoResult = (InfoResult) msg.obj;
			switch (msg.what) {
			case R.id.register_get_corp:
				JSONObject jsonObject = (JSONObject) JSONObject.parse(infoResult.getResult());
				if(jsonObject != null){
					mCorpInfo = new CorpInfo(jsonObject.getJSONObject(GlobalConstants.JSON_CORP));
					
					mCorpName.setText(mCorpInfo.getLname());
					mCorpTitle.setText(mCorpInfo.getName());
					mCorpLogo.setDefaultImageResId(R.drawable.default_corp_logo);
					mCorpLogo.setImageUrl(mCorpInfo.getLogo(), mImageLoader);
					
					mBenefitsList.clear();
					if(mCorpInfo.getBenefits() != null){
						for (int i = 0; i < mCorpInfo.getBenefits().size(); i++) {
							mBenefitsList.add(mCorpInfo.getBenefits().getJSONObject(i).getString(GlobalConstants.JSON_NAME));
						}
						mBenefits.setTextColor(getResources().getColor(R.color.white));
						mBenefits.setBackgroundResource(R.drawable.general_benefit_bg);
						mBenefits.setLinesText(mBenefitsList);
						mBenefits.setVisibility(View.VISIBLE);
					}else{
						mBenefits.setVisibility(View.INVISIBLE);
					}
					
					mIndustry.setText(getString(R.string.corp_industry,mCorpInfo.getIndustry()));
					mType.setText(getString(R.string.corp_type,mCorpInfo.getType()));
					mScale.setText(getString(R.string.corp_scale,mCorpInfo.getScale()));
					mCity.setText(getString(R.string.corp_city,mCorpInfo.getCity()));
					
					SpannableStringBuilder stringBuilder = new SpannableStringBuilder(getString(R.string.corp_homepage,mCorpInfo.getWebsite()));
				
					stringBuilder.setSpan(new ClickableSpan() {
						
						@Override
						public void onClick(View widget) {
							
							Intent intent = new Intent();
//							intent.setAction(Intent.ACTION_VIEW);
//							intent.addCategory(Intent.CATEGORY_BROWSABLE);
							String prefix = "http://";
							String content_url = null;
							if(mCorpInfo.getWebsite().startsWith(prefix)){
								content_url = mCorpInfo.getWebsite();   
							}else{
								content_url = prefix + mCorpInfo.getWebsite();
							}
//							intent.setData(content_url);
							intent.putExtra(GlobalConstants.INNER_URL, content_url);
							intent.setClass(widget.getContext(), WebSearchActivity.class);
							startActivity(intent);
							
						}
						
						@Override
						public void updateDrawState(TextPaint ds) {
							super.updateDrawState(ds);
							ds.setUnderlineText(false);
						}
					}, 4, 4 + mCorpInfo.getWebsite().length(), SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE);
					
					
					mHomepage.setText(stringBuilder);
					mHomepage.setMovementMethod(LinkMovementMethod.getInstance());
					mDesc.setText(mCorpInfo.getDesc());
					
					// 访问网络请求成功，才显示整体的View
					if(mLayout.getVisibility() != View.VISIBLE){
						Animation loadAnimation = AnimationUtils.loadAnimation(this,R.anim.anim_fide_in_for_recruit);
						mLayout.startAnimation(loadAnimation);
						mLayout.setVisibility(View.VISIBLE);
					}
				}
				break;
			case R.id.register_get_corp_position:
				
				JSONObject jobsObject = (JSONObject) JSONObject.parse(infoResult.getResult());
				if(jobsObject != null){
					JSONArray jsonArray = jobsObject.getJSONArray(GlobalConstants.JSON_JOBS);
					if(jsonArray != null){
						for(int i = 0 ; i < jsonArray.size() ; i ++ ){
							JobInfo info = new JobInfo(jsonArray.getJSONObject(i));
							mDatas.add(info);
						}
						positionsAdapter.notifyDataSetChanged();
					}
				}
				break;
			default:
				break;
			}
			
		}
		
	}
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Intent intent = new Intent();
		intent.putExtra(GlobalConstants.JOB_ID, mDatas.get(position).getJobId());
		intent.setClass(CorpInfoActivity.this, JobPositionInfoActivity.class);
		startActivity(intent);
	}
}
