package com.xiaobukuaipao.vzhi.adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.xiaobukuaipao.vzhi.AnoPersonInfoActivity;
import com.xiaobukuaipao.vzhi.PersonalShowPageActivity;
import com.xiaobukuaipao.vzhi.R;
import com.xiaobukuaipao.vzhi.domain.user.BasicInfo;
import com.xiaobukuaipao.vzhi.domain.user.UserProfile;
import com.xiaobukuaipao.vzhi.manager.GeneralDbManager;
import com.xiaobukuaipao.vzhi.util.GlobalConstants;
import com.xiaobukuaipao.vzhi.util.StringUtils;
import com.xiaobukuaipao.vzhi.util.VToast;
import com.xiaobukuaipao.vzhi.view.RoundedImageView;

public class SearchTalentAdapter extends CommonAdapter<UserProfile> {

	public SearchTalentAdapter(Context mContext, List<UserProfile> mDatas,
			int mItemLayoutId) {
		super(mContext, mDatas, mItemLayoutId);
		this.mContext = mContext;
	}

	@Override
	public void convert(final ViewHolder viewHolder, final UserProfile item, final int position) {
		RoundedImageView avatar = viewHolder.getView(R.id.avatar);
		TextView name = viewHolder.getView(R.id.name);//人物名称
		TextView title = viewHolder.getView(R.id.position);//人物职位
		TextView corp = viewHolder.getView(R.id.corp);//所在公司
		TextView exprAndCity = viewHolder.getView(R.id.expr_and_city);//经验和所在地
		TextView skills = viewHolder.getView(R.id.skills);//技能
		TextView words = viewHolder.getView(R.id.words);//介绍
		//初始化
		name.setVisibility(View.GONE);
		exprAndCity.setVisibility(View.GONE);
		title.setVisibility(View.GONE);
		corp.setVisibility(View.GONE);
		skills.setVisibility(View.GONE);
		words.setVisibility(View.GONE);
		
		if(item.getBasic() != null){
			final BasicInfo basicInfo = new BasicInfo(item.getBasic());
			
			// avatar.setBorderColor(getResources().getColor(basicInfo.getGender() == 1 ? R.color.bg_blue : R.color.bg_pink));
			
			if(StringUtils.isNotEmpty(basicInfo.getName())){
				//用 给的是真实名称还是匿名来判断名字卡片是否是匿名
				name.setVisibility(View.VISIBLE);
				name.setText(basicInfo.getName());
				if(StringUtils.isNotEmpty(basicInfo.getRealAvatar())){
					Picasso.with(mContext).load(basicInfo.getRealAvatar()).placeholder(R.drawable.general_user_avatar).into(avatar);
				}else{
					avatar.setImageResource(R.drawable.general_user_avatar);
				}
			}
			
			if(StringUtils.isNotEmpty(basicInfo.getNickname())){
				name.setVisibility(View.VISIBLE);
				name.setText(R.string.general_tips_ano);
				if(StringUtils.isNotEmpty(basicInfo.getAvatar())){
					Picasso.with(mContext).load(basicInfo.getAvatar()).placeholder(R.drawable.general_default_ano).into(avatar);
				}else{
					avatar.setImageResource(R.drawable.general_default_ano);
				}
			}
			
			if(StringUtils.isNotEmpty(basicInfo.getCorp())){
				corp.setVisibility(View.VISIBLE);
				corp.setText(basicInfo.getCorp());
			}
			
			boolean splite = false;
			StringBuilder builder = new StringBuilder();
			if(StringUtils.isNotEmpty(basicInfo.getCity())){
				builder.append(basicInfo.getCity());
				splite = true;
			}
			if(StringUtils.isNotEmpty(basicInfo.getExprName())){
				if(splite){
					builder.append(" / ");
				}
				splite = true;
				builder.append(basicInfo.getExprName());
			}
			if(splite){
				exprAndCity.setVisibility(View.VISIBLE);
			}
			
			exprAndCity.setText(builder.toString());
			
			if(StringUtils.isNotEmpty(basicInfo.getPosition())){
				title.setVisibility(View.VISIBLE);
				title.setText(basicInfo.getPosition());
			}
			
			if(item.getSkills() != null){
				builder.delete(0, builder.length());
				skills.setVisibility(View.VISIBLE);
				for (int i = 0; i < item.getSkills().size(); i++) {
					String skillname = item.getSkills().getJSONObject(i).getString(GlobalConstants.JSON_NAME);
					builder.append(skillname);
					if(i != item.getSkills().size() - 1){
						builder.append("，");
					}
				}
				skills.setText(mContext.getResources().getString(R.string.general_tips_default_skills, builder.toString()));
			}

			if(StringUtils.isNotEmpty(item.getWords())){
				StringBuilder sb = new StringBuilder();
				sb.append(mContext.getResources().getString(R.string.left_quotation));
				sb.append(" ");
				sb.append(item.getWords());
				sb.append(" ");
				sb.append(mContext.getResources().getString(R.string.right_quotation));
				words.setText(sb.toString());
				words.setVisibility(View.VISIBLE);
			}
			
			viewHolder.getConvertView().setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if(GeneralDbManager.getInstance().isExistCookie()){
						if(StringUtils.isNotEmpty(basicInfo.getName())){
							//用 给的是真实名称还是匿名来判断名字卡片是否是匿名
							Intent intent = new Intent();
							intent.setClass(mContext, PersonalShowPageActivity.class);
							intent.putExtra(GlobalConstants.UID,basicInfo.getId());
							mContext.startActivity(intent);
							
						}
						if(StringUtils.isNotEmpty(basicInfo.getNickname())){
							Intent intent = new Intent();
							intent.setClass(mContext, AnoPersonInfoActivity.class);
							intent.putExtra(GlobalConstants.UID, basicInfo.getId());
							mContext.startActivity(intent);
						}
						
					}else{
						VToast.show(mContext, mContext.getResources().getString(R.string.general_tips_default_unlogin));
					}
				}
			});
		}
	}

}
