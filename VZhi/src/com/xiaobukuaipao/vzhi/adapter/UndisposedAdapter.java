package com.xiaobukuaipao.vzhi.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.xiaobukuaipao.vzhi.R;
import com.xiaobukuaipao.vzhi.domain.CandidateInfo;
import com.xiaobukuaipao.vzhi.util.GlobalConstants;
import com.xiaobukuaipao.vzhi.util.StringUtils;


public class UndisposedAdapter extends CommonAdapter<CandidateInfo> {

	public UndisposedAdapter(Context mContext, List<CandidateInfo> mDatas, int mItemLayoutId) {
		super(mContext, mDatas, mItemLayoutId);
		
	}

	@Override
	public void convert(final ViewHolder viewHolder, final CandidateInfo item, final int position) {
		if (item != null) {
			final ImageView mAvatar = (ImageView) viewHolder.getView(R.id.avatar);
			
			if (!StringUtils.isEmpty(item.getBasic().getString(GlobalConstants.JSON_REALAVATAR))) {
				Picasso.with(mContext).load(item.getBasic().getString(GlobalConstants.JSON_REALAVATAR)).placeholder(R.drawable.general_user_avatar).into(mAvatar);
			} else {
				if (!StringUtils.isEmpty(item.getBasic().getString(GlobalConstants.JSON_NICKAVATAR))) {
					Picasso.with(mContext).load(item.getBasic().getString(GlobalConstants.JSON_NICKAVATAR)).placeholder(R.drawable.general_user_avatar).into(mAvatar);
				} else {
					mAvatar.setImageResource(R.drawable.general_user_avatar);
				}
			}
			
			// 时间
			viewHolder.setText(R.id.refresh_time, item.getApplytime());
			
			// 姓名
			if (!StringUtils.isEmpty(item.getBasic().getString(GlobalConstants.JSON_REALNAME))) {
				viewHolder.setText(R.id.name, item.getBasic().getString(GlobalConstants.JSON_REALNAME));
			} else {
				viewHolder.setText(R.id.name, item.getBasic().getString(GlobalConstants.NICKNAME));
			}
			
			final TextView mNickGuide = (TextView) viewHolder.getView(R.id.nick_guide);
			if (item.getIsreal() == 0) {
				// 匿名简历
				mNickGuide.setVisibility(View.VISIBLE);
			} else {
				// 实名简历
				mNickGuide.setVisibility(View.INVISIBLE);
			}
			
			if (item.getBasic() != null && item.getBasic().getJSONObject(GlobalConstants.JSON_POSITION) != null) {
				viewHolder.setText(R.id.title, item.getBasic().getJSONObject(GlobalConstants.JSON_POSITION).getString(GlobalConstants.JSON_NAME));
			}
			
			StringBuilder sb = new StringBuilder();
			sb.append(item.getBasic().getString(GlobalConstants.JSON_CITY));
			sb.append(mContext.getResources().getString(R.string.general_tips_dot));
			if (item.getBasic() != null && item.getBasic().getJSONObject(GlobalConstants.JSON_EXPRYEAR) != null) {
				sb.append(item.getBasic().getJSONObject(GlobalConstants.JSON_EXPRYEAR).getString(GlobalConstants.JSON_NAME));
			}
			viewHolder.setText(R.id.city_expr, sb.toString());
			
			viewHolder.setText(R.id.apply_job, item.getApplyjob().getJSONObject(GlobalConstants.JSON_POSITION).getString(GlobalConstants.JSON_NAME));
			
			
			// 自我介绍
			final RelativeLayout mSelfIntroduce = (RelativeLayout) viewHolder.getView(R.id.self_introduce);
			if (!StringUtils.isEmpty(item.getPostScript())) {
				mSelfIntroduce.setVisibility(View.VISIBLE);
				viewHolder.setText(R.id.postscript, item.getPostScript());
			} else {
				mSelfIntroduce.setVisibility(View.GONE);
			}
			
			// 工作经历
			sb.delete(0, sb.toString().length());
			if (item.getBasic() != null && item.getBasic().getJSONObject(GlobalConstants.JSON_POSITION) != null) {
				sb.append(item.getBasic().getJSONObject(GlobalConstants.JSON_POSITION).getString(GlobalConstants.JSON_NAME));
			}
			sb.append(mContext.getResources().getString(R.string.general_tips_dot));
			
			sb.append(item.getBasic().getString(GlobalConstants.JSON_CORP));
			
			viewHolder.setText(R.id.title_corp, sb.toString());
			
			sb.delete(0, sb.toString().length());
			sb.append(item.getBasic().getJSONObject(GlobalConstants.JSON_EXPRBEGINDATE).getString(GlobalConstants.JSON_YEAR));
			sb.append(".");
			sb.append(item.getBasic().getJSONObject(GlobalConstants.JSON_EXPRBEGINDATE).getString(GlobalConstants.JSON_MONTH));
			sb.append(" - ");
			
			if (item.getBasic().getJSONObject(GlobalConstants.JSON_EXPRENDDATE) == null) {
				sb.append("至今");
			} else {
				sb.append(item.getBasic().getJSONObject(GlobalConstants.JSON_EXPRENDDATE).getString(GlobalConstants.JSON_YEAR));
				sb.append(".");
				sb.append(item.getBasic().getJSONObject(GlobalConstants.JSON_EXPRENDDATE).getString(GlobalConstants.JSON_MONTH));
			}
			
			viewHolder.setText(R.id.experience_time, sb.toString());
			
			// 教育经历
			sb.delete(0, sb.toString().length());
			sb.append(item.getBasic().getString(GlobalConstants.JSON_SCHOOL));
			sb.append(mContext.getResources().getString(R.string.general_tips_dot));
			sb.append(item.getBasic().getString(GlobalConstants.JSON_MAJOR));
			sb.append(mContext.getResources().getString(R.string.general_tips_dot));
			sb.append(item.getBasic().getString(GlobalConstants.JSON_DEGREE));
			
			viewHolder.setText(R.id.education, sb.toString());
			
			sb.delete(0, sb.toString().length());
			sb.append(item.getBasic().getJSONObject(GlobalConstants.JSON_EDUBEGINDATE).getString(GlobalConstants.JSON_YEAR));
			sb.append(".");
			sb.append(item.getBasic().getJSONObject(GlobalConstants.JSON_EDUBEGINDATE).getString(GlobalConstants.JSON_MONTH));
			sb.append(" - ");
			
			if (item.getBasic().getJSONObject(GlobalConstants.JSON_EDUENDDATE) == null) {
				sb.append("至今");
			} else {
				sb.append(item.getBasic().getJSONObject(GlobalConstants.JSON_EDUENDDATE).getString(GlobalConstants.JSON_YEAR));
				sb.append(".");
				sb.append(item.getBasic().getJSONObject(GlobalConstants.JSON_EDUENDDATE).getString(GlobalConstants.JSON_MONTH));
			}
			
			viewHolder.setText(R.id.education_time, sb.toString());
			
			// 技能标签
			sb.delete(0, sb.length());
			if (item.getSkills() != null && item.getSkills().size() > 0) {
				for (int i = 0; i < item.getSkills().size() - 1; i++) {
					sb.append(item.getSkills().getJSONObject(i).getString(GlobalConstants.JSON_NAME));
					sb.append(",  ");
				}
				
				sb.append(item.getSkills().getJSONObject(item.getSkills().size() - 1).getString(GlobalConstants.JSON_NAME));
			}
			
			viewHolder.setText(R.id.skills, sb.toString());
	
		}

	}
}
