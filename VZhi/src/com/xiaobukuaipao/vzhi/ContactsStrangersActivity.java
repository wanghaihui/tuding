package com.xiaobukuaipao.vzhi;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.xiaobukuaipao.vzhi.adapter.CommonAdapter;
import com.xiaobukuaipao.vzhi.adapter.ViewHolder;
import com.xiaobukuaipao.vzhi.cache.CommonBitmapMemoryCache;
import com.xiaobukuaipao.vzhi.domain.social.Sender;
import com.xiaobukuaipao.vzhi.domain.social.StrangerCardInfo;
import com.xiaobukuaipao.vzhi.event.InfoResult;
import com.xiaobukuaipao.vzhi.im.IMConstants;
import com.xiaobukuaipao.vzhi.im.ImDbManager;
import com.xiaobukuaipao.vzhi.util.GlobalConstants;
import com.xiaobukuaipao.vzhi.util.SpannableKeyWordBuilder;
import com.xiaobukuaipao.vzhi.util.StringUtils;
import com.xiaobukuaipao.vzhi.util.TimeHandler;
import com.xiaobukuaipao.vzhi.util.VToast;
import com.xiaobukuaipao.vzhi.view.RoundedNetworkImageView;
import com.xiaobukuaipao.vzhi.view.refresh.PullToRefreshBase;
import com.xiaobukuaipao.vzhi.view.refresh.PullToRefreshBase.Mode;
import com.xiaobukuaipao.vzhi.view.refresh.PullToRefreshBase.OnRefreshListener2;
import com.xiaobukuaipao.vzhi.view.refresh.PullToRefreshListView;
import com.xiaobukuaipao.vzhi.wrap.SocialWrapActivity;

/**
 * 陌生人收件箱<br>
 * 负责处理好友请求,打招呼,以及提问,回复等功能,列表有待完善
 * 
 * @since 2015年01月05日19:52:34
 */
public class ContactsStrangersActivity extends SocialWrapActivity {
	
	static final String TAG = ContactsStrangersActivity.class.getSimpleName();
	/**
	 * 收件箱的刷新列表控件
	 */
	private PullToRefreshListView mStrangerList;
	/**
	 * 收件箱列表
	 */
	private StrangerListAdapter mStrangerListAdapter;
	/**
	 * 收件箱数据容器
	 */
	private List<StrangerCardInfo> mDatas;
	/**
	 * 临时的缓存类,当用户有下一步操作导致刷新上一步信息的时候会用到,例如加好友,网络请求成功之后会刷新这个临时的view,并清空引用
	 */
	private View tmp;
	/**
	 * 默认的刷新标识符
	 */
	private final Integer defaultMinletterid = -1;
	/**
	 * 刷新标识符,用于翻页刷新
	 */
	private Integer minletterid = defaultMinletterid;
	/**
	 * 是否上拉加载
	 */
	private boolean loadMore;
	/**
	 * 是否下拉刷新
	 */
	private boolean pullToRefresh ;
	/**
	 * Volley提供的网络请求队列
	 */
	private RequestQueue mQueue;
	/**
	 * 图片加载器
	 */
	private ImageLoader mImageLoader;

	@Override
	public void initUIAndData() {
		setContentView(R.layout.activity_contacts_strangers);
		setHeaderMenuByCenterTitle(R.string.stranger_str);
		setHeaderMenuByLeft(this);
		
		//初始化网络请求队列
		mQueue = Volley.newRequestQueue(this);
		mImageLoader = new ImageLoader(mQueue, new CommonBitmapMemoryCache());

		//初始化数据容器以及适配器
		mDatas = new ArrayList<StrangerCardInfo>();
		mStrangerListAdapter = new StrangerListAdapter(this, mDatas,R.layout.item_stranger);
		
		mStrangerList = (PullToRefreshListView) findViewById(R.id.contacts_stranger_list);
		mStrangerList.setEmptyView(findViewById(R.id.empty_view));
		mStrangerList.setAdapter(mStrangerListAdapter);
		mStrangerList.setOnRefreshListener(new OnRefreshListener2<ListView>() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				mSocialEventLogic.getStrangerList(defaultMinletterid);
				pullToRefresh = true;
				// 陌生人列表下拉刷新的时候，此时，清空消息列表中陌生人项的红点
				ImDbManager.getInstance().cleanMessageListStrangerCount();
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				loadMore = true;
				//拉取收件箱信息
				mSocialEventLogic.getStrangerList(minletterid);
			}
		});
		// 陌生人列表下拉刷新的时候，此时，清空消息列表中陌生人项的红点
		ImDbManager.getInstance().cleanMessageListStrangerCount();
		//拉取收件箱信息
		mSocialEventLogic.getStrangerList(defaultMinletterid);
	}

	@Override
	public void onEventMainThread(Message msg) {
		if (msg.obj instanceof InfoResult) {
			InfoResult infoResult = (InfoResult) msg.obj;
			switch (msg.what) {
			case R.id.social_stranger_list:
				if (pullToRefresh) {
					mDatas.clear();
					pullToRefresh = false;
					mStrangerList.onRefreshComplete();
				}
				if (loadMore) {
					loadMore = false;
					mStrangerList.onRefreshComplete();
				}
				JSONObject jsonObj = (JSONObject) JSONObject.parse(infoResult.getResult());
				if (jsonObj != null) {
					JSONArray jsonArray = jsonObj.getJSONArray(GlobalConstants.JSON_DATA);
					if (jsonArray != null) {
						for (int i = 0; i < jsonArray.size(); i++) {
							mDatas.add(new StrangerCardInfo(jsonArray.getJSONObject(i)));
						}
						mStrangerListAdapter.notifyDataSetChanged();
					}else{
						mStrangerList.onRefreshComplete();
					}
					//获取刷新符
					minletterid = jsonObj.getInteger(GlobalConstants.JSON_MINLETTERID);
					mStrangerList.setMode(minletterid == null || minletterid == 0 ? Mode.PULL_FROM_START : Mode.BOTH);
				}

				break;
			case R.id.social_stranger_buddy_accept:
				VToast.show(this, infoResult.getMessage().getMsg());
				if(tmp instanceof ImageView){
					((ImageView)tmp).setEnabled(false);
					((ImageView)tmp).setImageResource(R.drawable.stranger_finished);
					int position = (Integer) tmp.getTag();
					mDatas.get(position).setHasaccept(1);
					mStrangerListAdapter.notifyDataSetChanged();
					Log.i(TAG, "social_stranger_buddy_accept");
					ImDbManager.getInstance().subMessageListStrangerCount();
				}
				tmp = null;
				break;
			case R.id.social_stranger_send_greeting:
				VToast.show(this, infoResult.getMessage().getMsg());
				getGreeting();
				tmp = null;
				break;
			default:
				break;
			}
		}else if (msg.obj instanceof VolleyError) {
			mStrangerList.onRefreshComplete();
			VToast.show(this, getString(R.string.general_tips_network_unknow));
		}
	}

	private synchronized void getGreeting() {
		if(tmp instanceof ImageView){
			((ImageView)tmp).setEnabled(false);
			((ImageView)tmp).setImageResource(R.drawable.stranger_finished);
			int position = (Integer) tmp.getTag();
			mDatas.get(position).setHasreply(1);
			mStrangerListAdapter.notifyDataSetChanged();
			
			ImDbManager.getInstance().subMessageListStrangerCount();
		}
	}

	class StrangerListAdapter extends CommonAdapter<StrangerCardInfo> {

		public StrangerListAdapter(Context mContext, List<StrangerCardInfo> mDatas, int mItemLayoutId) {
			super(mContext, mDatas, mItemLayoutId);
		}

		@Override
		public void convert(final ViewHolder viewHolder, final StrangerCardInfo item, final int position) {
			final Sender strangerInfo = new Sender(item.getSender());
			SpannableKeyWordBuilder builder = new SpannableKeyWordBuilder();
			if(StringUtils.isNotEmpty(strangerInfo.getName())){
				builder.append(strangerInfo.getName());
			}
			
			//是否添加分割符的判断,作用是当信息中某项为空的时候,不会使得格式排版变丑
//			boolean splite = false;
			
			if(StringUtils.isNotEmpty(strangerInfo.getPosition())){
				
				builder.append("  " + strangerInfo.getPosition());
			
//				splite = true;
			}
//			if(StringUtils.isNotEmpty(strangerInfo.getCorp())){
//				if(splite){
//					builder.append(" / ");
//				}else {
//					builder.append("(");
//				}
//				builder.append(strangerInfo.getCorp());
//				splite = true;
//			}else{
//				
//			}
//			
//			if(StringUtils.isNotEmpty(strangerInfo.getCity())){
//				if(splite){
//					builder.append(" / ");
//				}else {
//					builder.append("(");
//				}
//				builder.append(strangerInfo.getCity());
//			}
			viewHolder.setText(R.id.stranger_name, builder.toString());
			TextView answer = viewHolder.getView(R.id.stranger_answer);
			answer.setVisibility(View.GONE);
			//初始化按钮
			ImageView back = viewHolder.getView(R.id.stranger_back);
			back.setVisibility(View.GONE);
			back.setEnabled(false);
			TextView questionIsReplied = viewHolder.getView(R.id.question_is_replied);
			questionIsReplied.setVisibility(View.GONE);
			
			TextView behavior = viewHolder.getView(R.id.stranger_behavior);
//			behavior.setTextColor(getResources().getColor(R.color.general_color_1A2138));
			
			View layout = viewHolder.getView(R.id.stranget_back_layout);
			layout.setVisibility(View.GONE);
			
			if (item.getType() == IMConstants.STRANGER_TYPE_ANSWER) {//回答的标志
				questionIsReplied.setVisibility(View.VISIBLE);
				questionIsReplied.setText(item.getQuestion());
				
				builder.delete(0, builder.length());
				builder.setKeyColor(getResources().getColor(R.color.stranger_answer_color));
				builder.appendKeyWord(getString(R.string.stranger_msg_s3));
				builder.append(item.getAnswer());
				behavior.setText(builder);
				back.setEnabled(true);
				back.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						tmp = v;
						Intent intent = new Intent(mContext,ReplyQuestionActivity.class);
						Bundle bundle = new Bundle();
						bundle.putParcelable(GlobalConstants.STRANGER_REPLY,item);
						intent.putExtras(bundle);
						startActivityForResult(intent, 105);
					}
				});
			} else if (item.getType() == IMConstants.STRANGER_TYPE_QUESTION) {//提问
				behavior.setText(item.getQuestion());
				if(item.getHasreply() == 1){
					behavior.setTextColor(getResources().getColor(R.color.general_color_999999));
					answer.setVisibility(View.VISIBLE);
					
					builder.delete(0, builder.length());
					builder.setKeyColor(getResources().getColor(R.color.stranger_answer_color));
					builder.appendKeyWord(getString(R.string.stranger_msg_s3) + "  ");
					builder.append(item.getAnswer());
					answer.setText(builder);
				}else{
					layout.setVisibility(View.VISIBLE);
					back.setVisibility(View.VISIBLE);
					back.setImageResource(R.drawable.stranger_answer);
					back.setEnabled(true);
					back.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							tmp = viewHolder.getConvertView();
							Intent intent = new Intent(mContext,ReplyQuestionActivity.class);
							Bundle bundle = new Bundle();
							bundle.putParcelable(GlobalConstants.STRANGER_REPLY,item);
							intent.putExtras(bundle);
							startActivityForResult(intent, 106);
						}
					});
				}
			} else if (item.getType() == IMConstants.STRANGER_TYPE_INVITATION) {//加好友
				layout.setVisibility(View.VISIBLE);
				back.setVisibility(View.VISIBLE);
				behavior.setText(getString(R.string.stranger_msg_invite));
				if (item.getHasaccept() == 1) {
					back.setImageResource(R.drawable.stranger_finished);
				} else if (item.getHasrefuse() == 1) {
					back.setImageResource(R.drawable.stranger_finished);
				} else {
					back.setImageResource(R.drawable.stranger_buddy);
					back.setEnabled(true);
					back.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							tmp = v;
							tmp.setTag(position);
							mSocialEventLogic.cancel(R.id.social_stranger_buddy_accept);
							mSocialEventLogic.invitationAccept(String.valueOf(strangerInfo.getId()));
						}
					});
				}
			} else if (item.getType() == IMConstants.STRANGER_TYPE_GREETING) {//打招呼
				layout.setVisibility(View.VISIBLE);
				back.setVisibility(View.VISIBLE);
				if(item.getIsreply() == 1){
					back.setImageResource(R.drawable.stranger_finished);
					viewHolder.setText(R.id.stranger_behavior,getString(R.string.stranger_msg_greeting_reply));
				}else{
					if(item.getHasreply() == 1){
						back.setImageResource(R.drawable.stranger_finished);
					}else{
						back.setImageResource(R.drawable.stranger_greeting);
						back.setEnabled(true);
						back.setOnClickListener(new OnClickListener() {
							
							@Override
							public void onClick(View v) {
								tmp = v;
								//
								tmp.setTag(position);
								mSocialEventLogic.cancel(R.id.social_stranger_send_greeting);
								mSocialEventLogic.sendGreeting(item.getSenderId(),String.valueOf(item.getGreetingid()));								
							}
						});
					}
					viewHolder.setText(R.id.stranger_behavior,getString(R.string.stranger_msg_greeting));
				}
			}
			
			//处理下时间
			TextView time = viewHolder.getView(R.id.item_time);
			time.setText(item.getHtime());
			time.setVisibility(item.isDisplay()? View.VISIBLE:View.GONE);

			View divider = viewHolder.getView(R.id.divider);
			divider.setVisibility(item.isDisplay() ?  View.GONE : View.VISIBLE);
			
			//加载头像
			RoundedNetworkImageView avatar = viewHolder.getView(R.id.stranger_avatar);
			
			avatar.setDefaultImageResId(R.drawable.general_user_avatar);
			avatar.setImageUrl(strangerInfo.getAvatar(), mImageLoader);
			// avatar.setBorderColor(mContext.getResources().getColor(strangerInfo.getGender() == 1 ? R.color.boy_border_color : R.color.girl_border_color));
			avatar.setBackgroundResource(strangerInfo.getGender() == 1 ? R.drawable.rectangle_circle_corner_male : R.drawable.rectangle_circle_corner_female);
			avatar.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent intent = new Intent();
					intent.setClass(mContext, PersonalShowPageActivity.class);
					intent.putExtra(GlobalConstants.UID, String.valueOf(strangerInfo.getId()));
					startActivity(intent);
				}
			});
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final ViewHolder viewHolder = getViewHolder(position, convertView, parent);
			convert(viewHolder, getItem(position), position);
			return viewHolder.getConvertView();
		}

		private ViewHolder getViewHolder(int position, View convertView, ViewGroup parent) {
			return ViewHolder.getViewHolder(mContext, convertView, parent, mItemLayoutId, position);
		}
		
		@Override
		public void notifyDataSetChanged() {
			TimeHandler handler = TimeHandler.getInstance(mContext);
			handler.setGapTime(handler.getMin() * 5);//每五分钟聚合一次
			handler.setCombineTime(handler.getDay() * 3);//3天前　开始
			for (int i = 0; i < mDatas.size(); i++) {
				mDatas.get(i).setHtime(handler.time2str(mDatas.get(i).getTime()));
				if (handler.isCombine()) {
					handler.setGapTime(handler.getDay());//每天聚合一次
				}
				mDatas.get(i).setDisplay(handler.isDisplay());
			}
			handler.reset();
			super.notifyDataSetChanged();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 105) {
			if(resultCode == RESULT_OK){
				tmp = null;
			}else{
				
			}
			
		} else if (requestCode == 106) {
			if(resultCode == RESULT_OK){
				tmp.findViewById(R.id.stranger_back).setVisibility(View.GONE);
				TextView answer = (TextView) tmp.findViewById(R.id.stranger_answer);
				answer.setVisibility(View.VISIBLE);
				answer.setText(getString(R.string.stranger_msg_s3) + data.getStringExtra("answer"));
				tmp = null;
				// 回答完成之后
				ImDbManager.getInstance().subMessageListStrangerCount();
			}else{
					
			}
		}
	}
}
