package com.xiaobukuaipao.vzhi.register;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.xiaobukuaipao.vzhi.R;
import com.xiaobukuaipao.vzhi.adapter.CommonAdapter;
import com.xiaobukuaipao.vzhi.adapter.ViewHolder;
import com.xiaobukuaipao.vzhi.domain.user.Experience;
import com.xiaobukuaipao.vzhi.event.InfoResult;
import com.xiaobukuaipao.vzhi.util.GlobalConstants;
import com.xiaobukuaipao.vzhi.util.StringUtils;
import com.xiaobukuaipao.vzhi.util.VToast;
import com.xiaobukuaipao.vzhi.view.NestedListView;
import com.xiaobukuaipao.vzhi.widget.FormItemByLineView;
import com.xiaobukuaipao.vzhi.wrap.RegisterWrapActivity;

/**
 * 选择招聘身份
 *
 */
public class HRInfoJobExprActivity extends RegisterWrapActivity implements OnCheckedChangeListener, OnClickListener {

	private NestedListView mSelectJobExprs;
	private List<Experience> mListExp;
	private JobExpListAdapter mJobExpListAdapter;
	private FormItemByLineView mJobExpr;
	
	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		intent.setClass(HRInfoJobExprActivity.this, CompanyInfoActivity.class);
		intent.putExtra(GlobalConstants.RECRUIT_SETTING, true);
		startActivity(intent);
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		// 得到已经存在的工作经历
		mProfileEventLogic.getExperiences();
	}

	@Override
	public void initUIAndData() {
		setContentView(R.layout.activity_hr_info_select_jobexpr);
		setHeaderMenuByCenterTitle(R.string.recruit_service_select_recruit);
		setHeaderMenuByLeft(this);
		
		mSelectJobExprs = (NestedListView) findViewById(R.id.hr_select_jobexpr);
		mListExp = new ArrayList<Experience>();
		mJobExpListAdapter = new JobExpListAdapter(this, mListExp,R.layout.item_job_experience);
		
		mSelectJobExprs.setAdapter(mJobExpListAdapter);
		
		mSelectJobExprs.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				Intent intent = getIntent();
				intent.setClass(HRInfoJobExprActivity.this, CompanyInfoActivity.class);
				intent.putExtra(GlobalConstants.RECRUIT_SETTING, true);
				intent.putExtra(GlobalConstants.JOB_INFO, mListExp.get(position));
				startActivity(intent);
				
			}
		});
		
		mJobExpr = (FormItemByLineView) findViewById(R.id.add_new_expr);
		mJobExpr.getFormLabel().setTextColor(getResources().getColor(R.color.general_color_FF9999));
		mJobExpr.setOnClickListener(this);
	}

	@Override
	public void onEventMainThread(Message msg) {
		if (msg.obj instanceof InfoResult) {
			InfoResult infoResult = (InfoResult) msg.obj;
			switch (msg.what) {
			case R.id.register_get_experiences:
				JSONObject experience = (JSONObject) JSONObject.parseObject(infoResult.getResult());
				if(experience != null){
					mListExp.clear();
					JSONArray experienceArray = experience.getJSONArray(GlobalConstants.JSON_WORKEXPRS);
					if(experienceArray!=null){
						for (int i = 0; i < experienceArray.size(); i++) {
							Experience expr = new Experience(experienceArray.getJSONObject(i));
							mListExp.add(expr);
						}
					}
				}
				mJobExpListAdapter.notifyDataSetChanged();
				break;
			}
		}else if (msg.obj instanceof VolleyError) {
			VToast.show(this, getString(R.string.general_tips_network_unknow));
		}
	}
	
	
	class JobExpListAdapter extends CommonAdapter<Experience> {

		public JobExpListAdapter(Context mContext, List<Experience> mDatas,
				int mItemLayoutId) {
			super(mContext, mDatas, mItemLayoutId);
		}
		
		@Override
		public void convert(ViewHolder viewHolder, Experience item, int position) {
			FormItemByLineView layout = (FormItemByLineView) viewHolder.getView(R.id.content);
			StringBuilder time = new StringBuilder();
			
			time.append(item.getBeginStr());
			time.append("~");
			time.append(StringUtils.isEmpty(item.getEndStr()) ? getString(R.string.date_dialog_tonow) : item.getEndStr());
			
			layout.setFormLabel(time.toString());
			layout.getFormContent().setTextColor(getResources().getColor(R.color.general_color_666666));
			layout.setFormContent(item.getPosition());
			
			viewHolder.getView(R.id.divider).setVisibility(position == getCount() - 1 ? View.GONE : View.VISIBLE);
			
		}

	}
}
