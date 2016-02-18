package com.xiaobukuaipao.vzhi.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xiaobukuaipao.vzhi.CandidateActivity;
import com.xiaobukuaipao.vzhi.PublishPositionActivity;
import com.xiaobukuaipao.vzhi.PublishedPositionsActivity;
import com.xiaobukuaipao.vzhi.R;
import com.xiaobukuaipao.vzhi.domain.JobInfo;
import com.xiaobukuaipao.vzhi.util.GlobalConstants;
import com.xiaobukuaipao.vzhi.view.DrawableCenterTextView;

public class PublishedPositionAdapter extends CommonAdapter<JobInfo> {
	
	private Context mContext;
	private Handler mHandler;
	public PublishedPositionAdapter(Context mContext, List<JobInfo> mDatas,
			int mItemLayoutId, Handler handler) {
		super(mContext, mDatas, mItemLayoutId);
		this.mContext = mContext;
		mHandler = handler;
	}

	@Override
	public void convert(final ViewHolder viewHolder,final JobInfo item, final int position) {
		
		StringBuilder sb = new StringBuilder();
		// 职位信息
		viewHolder.setText(R.id.published_position, item.getPosition().getString(GlobalConstants.JSON_NAME));
		
		// 添加基本的Job信息--工资，工作经验，城市
		sb.append(item.getSalary());
		sb.append(" / ");
		sb.append(item.getExpr().getString(GlobalConstants.JSON_NAME));
		sb.append(" / ");
		sb.append(item.getCity());
		viewHolder.setText(R.id.position_basic_info, sb.toString());
		// 清空
		sb.delete(0, sb.toString().length());
		
		// 添加刷新时间
		viewHolder.setText(R.id.refresh_time, item.getRefreshtime());
		
		// 添加投递人数
		viewHolder.setText(R.id.post_num, String.valueOf(item.getAllnum()));
		
		// 未读人数
		viewHolder.setText(R.id.unread_num, String.valueOf(item.getUnreadnum()));
		
		if (item.getStatus() == 1) {
			// 此时代表职位开启
			// ((ImageView) viewHolder.getView(R.id.published_pos_lable)).setImageResource(R.drawable.published_position_label_open);
			((TextView) viewHolder.getView(R.id.published_position)).setTextColor(Color.rgb(26, 33, 56));
			((TextView) viewHolder.getView(R.id.position_basic_info)).setTextColor(Color.rgb(102, 102, 102));
			((TextView) viewHolder.getView(R.id.gong)).setTextColor(Color.rgb(153, 153, 153));
			((TextView) viewHolder.getView(R.id.post_num)).setTextColor(Color.rgb(255, 102, 102));
			((TextView) viewHolder.getView(R.id.post_prompt)).setTextColor(Color.rgb(153, 153, 153));
			((TextView) viewHolder.getView(R.id.unread_num)).setBackgroundResource(R.drawable.filled_circle_red);
			((TextView) viewHolder.getView(R.id.unread_num)).setTextColor(Color.rgb(255, 255, 255));
			
			// 刷新
			((DrawableCenterTextView) viewHolder.getView(R.id.refresh)).setCompoundDrawablesWithIntrinsicBounds(
					mContext.getResources().getDrawable(R.drawable.position_refresh_open), null, null, null);
			((DrawableCenterTextView) viewHolder.getView(R.id.refresh)).setTextColor(Color.rgb(153, 153, 153));
			// 编辑
			((DrawableCenterTextView) viewHolder.getView(R.id.edit)).setCompoundDrawablesWithIntrinsicBounds(mContext.getResources().getDrawable(R.drawable.position_edit_open), null, null, null);
			((DrawableCenterTextView) viewHolder.getView(R.id.edit)).setTextColor(Color.rgb(153, 153, 153));
			((DrawableCenterTextView) viewHolder.getView(R.id.edit)).setOnClickListener(new JobEdit(item.getId()));
			// 分享
			((DrawableCenterTextView) viewHolder.getView(R.id.share)).setCompoundDrawablesWithIntrinsicBounds(
					mContext.getResources().getDrawable(R.drawable.position_share_open), null, null, null);
			((DrawableCenterTextView) viewHolder.getView(R.id.share)).setTextColor(Color.rgb(153, 153, 153));
			((DrawableCenterTextView) viewHolder.getView(R.id.share)).setEnabled(true);
			// 开启开关
			((DrawableCenterTextView) viewHolder.getView(R.id.open_close)).setCompoundDrawablesWithIntrinsicBounds(
					mContext.getResources().getDrawable(R.drawable.published_position_toggle), null, null, null);
			((DrawableCenterTextView) viewHolder.getView(R.id.open_close)).setText(mContext.getResources().getString(R.string.open_position));
			((DrawableCenterTextView) viewHolder.getView(R.id.open_close)).setTextColor(Color.rgb(153, 153, 153));
			
			// 遮罩
			((RelativeLayout) viewHolder.getView(R.id.shade)).setVisibility(View.VISIBLE);
			
			viewHolder.getView(R.id.unread_num).setVisibility(item.getUnreadnum() == 0 ? View.GONE : View.VISIBLE);
		} else {
			// 此时代表职位关闭
			// ((ImageView) viewHolder.getView(R.id.published_pos_lable)).setImageResource(R.drawable.published_position_label_close);
			((TextView) viewHolder.getView(R.id.published_position)).setTextColor(Color.rgb(192, 192, 192));
			((TextView) viewHolder.getView(R.id.position_basic_info)).setTextColor(Color.rgb(192, 192, 192));
			((TextView) viewHolder.getView(R.id.gong)).setTextColor(Color.rgb(192, 192, 192));
			((TextView) viewHolder.getView(R.id.post_num)).setTextColor(Color.rgb(192, 192, 192));
			((TextView) viewHolder.getView(R.id.post_prompt)).setTextColor(Color.rgb(192, 192, 192));
			((TextView) viewHolder.getView(R.id.unread_num)).setBackgroundResource(R.drawable.filled_circle_gray);
			((TextView) viewHolder.getView(R.id.unread_num)).setTextColor(Color.rgb(192, 192, 192));
			// 刷新
			((DrawableCenterTextView) viewHolder.getView(R.id.refresh)).setCompoundDrawablesWithIntrinsicBounds(
					mContext.getResources().getDrawable(R.drawable.position_refresh_close), null, null, null);
			((DrawableCenterTextView) viewHolder.getView(R.id.refresh)).setTextColor(Color.rgb(192, 192, 192));
			// 编辑
			((DrawableCenterTextView) viewHolder.getView(R.id.edit)).setCompoundDrawablesWithIntrinsicBounds(
					mContext.getResources().getDrawable(R.drawable.position_edit_close), null, null, null);
			((DrawableCenterTextView) viewHolder.getView(R.id.edit)).setTextColor(Color.rgb(192, 192, 192));
			((DrawableCenterTextView) viewHolder.getView(R.id.edit)).setOnClickListener(null);
			// 分享
			((DrawableCenterTextView) viewHolder.getView(R.id.share)).setCompoundDrawablesWithIntrinsicBounds(
					mContext.getResources().getDrawable(R.drawable.position_share_close), null, null, null);
			((DrawableCenterTextView) viewHolder.getView(R.id.share)).setTextColor(Color.rgb(192, 192, 192));
			((DrawableCenterTextView) viewHolder.getView(R.id.share)).setEnabled(false);
			// 关闭开关
			((DrawableCenterTextView) viewHolder.getView(R.id.open_close)).setCompoundDrawablesWithIntrinsicBounds(
					mContext.getResources().getDrawable(R.drawable.published_position_toggle_close), null, null, null);
			((DrawableCenterTextView) viewHolder.getView(R.id.open_close)).setText(mContext.getResources().getString(R.string.close_position));
			((DrawableCenterTextView) viewHolder.getView(R.id.open_close)).setTextColor(Color.rgb(242, 242, 242));
			
			// 遮罩
			((RelativeLayout) viewHolder.getView(R.id.shade)).setVisibility(View.GONE);
			viewHolder.getView(R.id.unread_num).setVisibility(View.GONE);
		}
		
		
		final DrawableCenterTextView mRefreshView = (DrawableCenterTextView) viewHolder.getView(R.id.refresh);
		mRefreshView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Message msg = Message.obtain();
				msg.what = PublishedPositionsActivity.POSITION_REFRESH;
				msg.obj = item;
				msg.arg1 = position;
				mHandler.sendMessage(msg);
			}
		});
		
		final DrawableCenterTextView mOpenCloseView = (DrawableCenterTextView) viewHolder.getView(R.id.open_close);
		mOpenCloseView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Message msg = Message.obtain();
				msg.what = PublishedPositionsActivity.POSITION_OPEN_OR_CLOSE;
				msg.obj = item;
				msg.arg1 = position;
				mHandler.sendMessage(msg);
			}
		});
		
		final RelativeLayout mPositionLayout = (RelativeLayout) viewHolder.getView(R.id.position_info_layout);
		mPositionLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if (item.getStatus() == 1) {
					// 当前职位是开启的，才能进入候选人接收箱
					// Intent intent = new Intent(mContext, CandidateSelectActivity.class);
					Intent intent = new Intent(mContext, CandidateActivity.class);
					// intent.putExtra("current_position", position-1);
					intent.putExtra("current_position", position);
					intent.putExtra("published_position", true);
					intent.putExtra("unread_count", item.getUnreadnum());
					intent.putParcelableArrayListExtra("total_published_job_list", (ArrayList<? extends Parcelable>) mDatas);
					mContext.startActivity(intent);
				}
			}
		});
		
		final DrawableCenterTextView mShareView = (DrawableCenterTextView) viewHolder.getView(R.id.share);
		mShareView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Message msg = Message.obtain();
				msg.what = PublishedPositionsActivity.POSITION_SHARE;
				msg.obj = item;
				msg.arg1 = position;
				mHandler.sendMessage(msg);
			}
		});
	}

	class JobEdit implements OnClickListener{

		String jid;
		public JobEdit(String jid){
			this.jid = jid;
		}
		
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(mContext,PublishPositionActivity.class);
			intent.putExtra(GlobalConstants.JOB_INFO, Integer.parseInt(jid));
			mContext.startActivity(intent);
		}
		
	}
	
}
