package com.xiaobukuaipao.vzhi.adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.xiaobukuaipao.vzhi.AnoPersonInfoActivity;
import com.xiaobukuaipao.vzhi.CandidateActivity;
import com.xiaobukuaipao.vzhi.ChatPersonActivity;
import com.xiaobukuaipao.vzhi.PersonalShowPageActivity;
import com.xiaobukuaipao.vzhi.R;
import com.xiaobukuaipao.vzhi.domain.CandidateInfo;
import com.xiaobukuaipao.vzhi.util.GlobalConstants;
import com.xiaobukuaipao.vzhi.util.SpannableKeyWordBuilder;
import com.xiaobukuaipao.vzhi.util.StringUtils;
import com.xiaobukuaipao.vzhi.view.RoundedImageView;

public class AllContactCandidateAdapter extends CommonAdapter<CandidateInfo> {

	
	private Context mContext;
	private String dot;
		
	public AllContactCandidateAdapter(Context mContext, List<CandidateInfo> mDatas, int mItemLayoutId) {
		super(mContext, mDatas, mItemLayoutId);
		this.mContext = mContext;
		dot = mContext.getResources().getString(R.string.general_tips_dot);
	}

	// 状态优先级 未读 已读 感兴趣 已联系 面试邀请已发送 接受面试 忽略面试
	@Override
	public void convert(final ViewHolder viewHolder, final CandidateInfo item, final int position) {
		
		if (item != null) {
			viewHolder.getConvertView().setOnClickListener(new OnAvatarClick(item.getBasic().getString(GlobalConstants.JSON_ID), item.getApplyjob().getString(GlobalConstants.JSON_ID), item.getIsreal(), (item.getContactstatus() & 1), item.getContactstatus(), position));
			viewHolder.setText(R.id.position, "应聘:  "+ item.getApplyjob().getJSONObject(GlobalConstants.JSON_POSITION).getString(GlobalConstants.JSON_NAME));
			if(item.getBasic() != null){
				final RoundedImageView mAvatar = (RoundedImageView) viewHolder.getView(R.id.avatar);
				if(item.getIsreal() == 1){
					if (!StringUtils.isEmpty(item.getBasic().getString(GlobalConstants.JSON_REALAVATAR))) {
						Picasso.with(mContext).load(item.getBasic().getString(GlobalConstants.JSON_REALAVATAR)).placeholder(R.drawable.general_user_avatar).into(mAvatar);
					}
				}else {
					if (!StringUtils.isEmpty(item.getBasic().getString(GlobalConstants.JSON_NICKAVATAR))) {
						Picasso.with(mContext).load(item.getBasic().getString(GlobalConstants.JSON_NICKAVATAR)).placeholder(R.drawable.general_user_avatar).into(mAvatar);
					} else {
						mAvatar.setImageResource(R.drawable.general_user_avatar);
					}
				}
				
				// mAvatar.setBorderColor(mContext.getResources().getColor(item.getBasic().getInteger(GlobalConstants.JSON_GENDER) == 1 ? R.color.boy_border_color : R.color.girl_border_color));
				
				SpannableKeyWordBuilder  sb = new SpannableKeyWordBuilder();
				if(item.getIsreal() == 1){
					if (!StringUtils.isEmpty(item.getBasic().getString(GlobalConstants.JSON_REALNAME))) {
						viewHolder.setText(R.id.name, item.getBasic().getString(GlobalConstants.JSON_REALNAME));
					}
				}else {
					sb.append(item.getBasic().getString(GlobalConstants.JSON_NICKNAME));     
					sb.setKeyColor(mContext.getResources().getColor(R.color.general_color_999999));
					sb.appendKeyWord( "  " + mContext.getResources().getString(R.string.nick_guide));
					viewHolder.setText(R.id.name, sb);
				}
				sb.delete(0, sb.length());
				viewHolder.setText(R.id.time, item.getApplytime());
				viewHolder.setText(R.id.expr_and_city, item.getBasic().getJSONObject(GlobalConstants.JSON_EXPRYEAR).getString(GlobalConstants.JSON_NAME) + dot + item.getBasic().getString(GlobalConstants.JSON_CITY));
				
				//工作经历
				if(item.getBasic().getJSONObject(GlobalConstants.JSON_POSITION) != null){
					String positionName = item.getBasic().getJSONObject(GlobalConstants.JSON_POSITION).getString(GlobalConstants.JSON_NAME);
					if(StringUtils.isNotEmpty(positionName)){
						sb.append(positionName);
					}
				}

				if(StringUtils.isNotEmpty(item.getBasic().getString(GlobalConstants.JSON_CORP))){
					sb.append(dot);
					sb.append(item.getBasic().getString(GlobalConstants.JSON_CORP));
				}
				if(StringUtils.isNotEmpty(item.getBasic().getString(GlobalConstants.JSON_INDUSTRY))){
					sb.append(dot);
					sb.append(item.getBasic().getString(GlobalConstants.JSON_INDUSTRY));
				}
				
				if(StringUtils.isNotEmpty(sb.toString())){
					viewHolder.setText(R.id.position_info, sb.toString());
				}
				viewHolder.getView(R.id.edu).setVisibility(StringUtils.isNotEmpty(sb.toString()) ? View.VISIBLE: View.GONE);
				
				//教育背景
				sb.delete(0, sb.toString().length());
				if(StringUtils.isNotEmpty(item.getBasic().getString(GlobalConstants.JSON_SCHOOL))){
					sb.append(item.getBasic().getString(GlobalConstants.JSON_SCHOOL));
				}
				if(StringUtils.isNotEmpty(item.getBasic().getString(GlobalConstants.JSON_MAJOR))){
					sb.append(dot);
					sb.append(item.getBasic().getString(GlobalConstants.JSON_MAJOR));
				}
				if(StringUtils.isNotEmpty(item.getBasic().getString(GlobalConstants.JSON_DEGREE))){
					sb.append(dot);
					sb.append(item.getBasic().getString(GlobalConstants.JSON_DEGREE));
				}
				if(StringUtils.isNotEmpty(sb.toString())){
					viewHolder.setText(R.id.edu_info, sb.toString());
				}
				
				viewHolder.getView(R.id.edu).setVisibility(StringUtils.isNotEmpty(sb.toString()) ? View.VISIBLE: View.GONE);
			}
			makeSign(viewHolder, item);
			
//			// 先判断是否是已读，之后是否感兴趣，之后是否已联系
//			if (item.getReadstatus() > 0) {
//				viewHolder.getView(R.id.published_pos_lable).setVisibility(View.VISIBLE);
//				// 已读
//				viewHolder.setImageResource(R.id.published_pos_lable, R.drawable.applied_readed);
//				
//				if ((item.getContactstatus() & 1) == 1) {
//					viewHolder.setImageResource(R.id.published_pos_lable, R.drawable.applier_star);
//				}
//				
//				if (((item.getContactstatus() & 2) >> 1) == 1 || ((item.getContactstatus() & 16) >> 4) == 1) {
//					viewHolder.setImageResource(R.id.published_pos_lable, R.drawable.applied_contacted);
//				}
//			} else {
//				// 未读
//				viewHolder.getView(R.id.published_pos_lable).setVisibility(View.INVISIBLE);
//			}
//			// 发送面试时间
//			if (((item.getContactstatus() & 2) >> 1) == 1) {
//				viewHolder.getView(R.id.apply_time_layout).setVisibility(View.VISIBLE);
//				
//				if (((item.getContactstatus() & 4) >> 2) == 0 && ((item.getContactstatus() & 8) >> 3) == 0) {
//					// 既没接受面试，也没忽略面试
//					// viewHolder.setImageResource(R.id.apply_time_icon, R.drawable.general_contact_blue);
//					viewHolder.setText(R.id.apply_time, mContext.getResources().getString(R.string.interview_has_send));
//					viewHolder.setTextColor(R.id.apply_time, Color.rgb(153, 153, 153));
//				} else if (((item.getContactstatus() & 4) >> 2) == 1 && ((item.getContactstatus() & 8) >> 3) == 0) {
//					// 接受面试
//					StringBuilder sb = new StringBuilder();
//					if (!StringUtils.isEmpty(item.getBasic().getString(GlobalConstants.JSON_REALNAME))) {
//						sb.append(item.getBasic().getString(GlobalConstants.JSON_REALNAME));
//					} else {
//						sb.append(item.getBasic().getString(GlobalConstants.JSON_NICKNAME));
//					}
//					
//					sb.append(mContext.getResources().getString(R.string.interview_has_accepted));
//					sb.append(item.getInterviewTime());
//					viewHolder.setText(R.id.apply_time, sb.toString());
//					viewHolder.setTextColor(R.id.apply_time, Color.rgb(255, 0, 0));
//				} else if (((item.getContactstatus() & 4) >> 2) == 0 && ((item.getContactstatus() & 8) >> 3) == 1) {
//					// 忽略面试
//					// 接受面试
//					StringBuilder sb = new StringBuilder();
//					if (!StringUtils.isEmpty(item.getBasic().getString(GlobalConstants.JSON_REALNAME))) {
//						sb.append(item.getBasic().getString(GlobalConstants.JSON_REALNAME));
//					} else {
//						sb.append(item.getBasic().getString(GlobalConstants.JSON_NICKNAME));
//					}
//					sb.append(mContext.getResources().getString(R.string.interview_has_ignored));
//					viewHolder.setText(R.id.apply_time, sb.toString());
//					
//					viewHolder.setTextColor(R.id.apply_time, Color.rgb(255, 0, 0));
//				}
//			} else {
//				viewHolder.getView(R.id.apply_time_layout).setVisibility(View.GONE);
//			}
			
		}
	}

	/**
	 * 对卡片进行HR动作标记
	 * 
	 * @param viewHolder
	 * @param item
	 */
	private void makeSign(final ViewHolder viewHolder, final CandidateInfo item) {
//		Logcat.d("test", "-----------------------" + item.getBasic().getString(GlobalConstants.JSON_ID) +"----"+  item.getApplyjob().getString(GlobalConstants.JSON_ID));
		int status = item.getReadstatus() > 0 ? 0 : -1;// -1 表示未读,0表示已读
//		Logcat.d("test", "contactstatus : " + item.getContactstatus());
		if(item.getContactstatus() == 1){//感兴趣
			status = 1;
		}
		if((item.getContactstatus() & 16)  >> 4 == 1){//已联系
			status = 2;
		}
		if((item.getContactstatus() & 2) >> 1 == 1){//约面试
			status = 3;
		}
		if((item.getContactstatus() & 4) >> 2 == 1){//接受面试
			status = 4;
		}
		if((item.getContactstatus() & 8) >> 3 == 1){//忽略面试
			status = 5;
		}
//		Logcat.d("test", "getContactstatus : " + status);
		TextView statusTextView = viewHolder.getView(R.id.status);
		switch (status) {
		case -1:
			statusTextView.setText(R.string.status_0_33cc33);
			statusTextView.setTextColor(Color.rgb(0x33, 0xcc, 0x33));
			break;
		case 0:
			statusTextView.setText(R.string.status_1_cccccc);
			statusTextView.setTextColor(Color.rgb(0xcc, 0xcc, 0xcc));
			break;
		case 1:
			statusTextView.setText(R.string.status_2_2cc0eb);
			statusTextView.setTextColor(Color.rgb(0x2c, 0xc0, 0xeb));
			break;
		case 2:
			statusTextView.setText(R.string.status_3_2cc0eb);
			statusTextView.setTextColor(Color.rgb(0x2c, 0xc0, 0xeb));
			break;
		case 3:
			statusTextView.setText(R.string.status_4_2cc0eb);
			statusTextView.setTextColor(Color.rgb(0x2c, 0xc0, 0xeb));
			break;
		case 4:
			statusTextView.setText(mContext.getString(R.string.status_5_ff9999, item.getInterviewTime()));
			statusTextView.setTextColor(Color.rgb(0xff, 0x99, 0x99));
			break;
		case 5:
			statusTextView.setText(R.string.status_6_cccccc);
			statusTextView.setTextColor(Color.rgb(0xcc, 0xcc, 0xcc));
			break;
		default:
			statusTextView.setText(R.string.status_0_33cc33);
			statusTextView.setTextColor(Color.rgb(0x33, 0xcc, 0x33));
			break;
		}
	}
	class OnAvatarClick implements OnClickListener{

		String id;
		String jid;
		Integer isReal;
		// 该候选人是否已联系
		Integer isContacted;
		Integer position;
		Integer contactStatus;
		public OnAvatarClick(String id, String jid, Integer isReal, Integer isContacted,Integer contactStatus, Integer position){
			this.id = id;
			this.jid = jid;
			this.isReal = isReal;
			this.isContacted = isContacted;
			this.position = position;
			this.contactStatus = contactStatus;
		}
		
		@Override
		public void onClick(View v) {
			Intent intent = new Intent();
			intent.setClass(mContext, isReal == 1 ? PersonalShowPageActivity.class : AnoPersonInfoActivity.class);
			intent.putExtra(GlobalConstants.UID, id);
			intent.putExtra(GlobalConstants.CANDIDATE_VIEW, true);
			intent.putExtra(GlobalConstants.JOB_ID, jid);
			intent.putExtra(GlobalConstants.ISREAL, isReal);
			intent.putExtra(GlobalConstants.IS_CONTACTED, isContacted);
			intent.putExtra(GlobalConstants.CONTACT_STATUS, contactStatus);
			((CandidateActivity)mContext).startActivityForResult(intent,1);
		}
	}
	class OnChatClick implements OnClickListener{

		String id;
		String name;
		Integer isReal;
		String jid;
		Integer contactStatus;
		public OnChatClick(String id, String name, Integer isReal, String jid,Integer contactStatus){
			this.id = id;
			this.name = name;
			this.isReal = isReal;
			this.jid = jid;
			this.isReal = isReal;
			this.contactStatus = contactStatus;
		}
		@Override
		public void onClick(View v) {
			Intent intent = new Intent();
			intent.setClass(mContext, ChatPersonActivity.class);
			intent.putExtra("sender", Long.valueOf(id));
			intent.putExtra("is_real", isReal);
			intent.putExtra("dname", name);
			intent.putExtra("is_candidate", true);
			mContext.startActivity(intent);
		}
	}
}
