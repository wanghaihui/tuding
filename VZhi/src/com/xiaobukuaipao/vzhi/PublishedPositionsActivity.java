package com.xiaobukuaipao.vzhi;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.CustomShareBoard;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.media.SinaShareContent;
import com.umeng.socialize.weixin.media.CircleShareContent;
import com.umeng.socialize.weixin.media.WeiXinShareContent;
import com.xiaobukuaipao.vzhi.adapter.PublishedPositionAdapter;
import com.xiaobukuaipao.vzhi.domain.JobInfo;
import com.xiaobukuaipao.vzhi.event.ApiConstants;
import com.xiaobukuaipao.vzhi.event.InfoResult;
import com.xiaobukuaipao.vzhi.util.GlobalConstants;
import com.xiaobukuaipao.vzhi.util.VToast;
import com.xiaobukuaipao.vzhi.view.refresh.PullToRefreshBase.Mode;
import com.xiaobukuaipao.vzhi.view.refresh.PullToRefreshListView;
import com.xiaobukuaipao.vzhi.wrap.PositionWrapActivity;

/**
 * 我发布的职位列表
 */
public class PublishedPositionsActivity extends PositionWrapActivity {
	
	private RelativeLayout mEmptyView;
	private PullToRefreshListView mPullListView;
	private ArrayList<JobInfo> mJobInfoList;
	private PublishedPositionAdapter mPublishedPosAdapter;
	
	// 刷新Id
	private Integer minRefreshId = -1;
	
	public static final int POSITION_REFRESH = 1;
	public static final int POSITION_OPEN_OR_CLOSE = 2;
	public static final int POSITION_SHARE = 3;
	
	// Position--当前开启或者关闭的职位
	private int position = -1;
	/**
	 * 自定义的分享面板
	 */
	private CustomShareBoard customShareBoard;
	/**
	 * 获取系统中分享服务实例
	 */
	private UMSocialService mController = UMServiceFactory.getUMSocialService("com.xiaobukuaipao.vzhi");
	private WeakHandler mHandler = new WeakHandler(PublishedPositionsActivity.this) {
		
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			
			switch (msg.what) {
				case POSITION_REFRESH:
					// 刷新数据
					refreshPositionList((JobInfo) msg.obj);
					break;
				case POSITION_OPEN_OR_CLOSE:
					if (((JobInfo) msg.obj).getStatus() == 1) {
						// 说明现在是开启状态，要进行关闭操作
						closePublishedPosition((JobInfo) msg.obj, msg.arg1);
					} else {
						// 说明此时是关闭状态，需要进行开启操作
						openPublishedPosition((JobInfo) msg.obj, msg.arg1);
					}
					break;
				case POSITION_SHARE:
					JobInfo jobInfo = (JobInfo) msg.obj;
					MobclickAgent.onEvent(PublishedPositionsActivity.this, "fxzw");
//					使用友盟分享职位信息
//					UMImage localImage = new UMImage(PublishedPositionsActivity.this, "");
					
					String url = ApiConstants.JOBINFO + jobInfo.getId();
					
					//微信分享
					WeiXinShareContent weixinContent = new WeiXinShareContent();
					weixinContent.setTitle(jobInfo.getPosition().getString(GlobalConstants.JSON_NAME));
					weixinContent.setShareContent(jobInfo.getHighlights());
					weixinContent.setTargetUrl(url);
					
//					weixinContent.setShareImage(localImage);
					mController.setShareMedia(weixinContent);

					//微信朋友圈分享
					CircleShareContent circleMedia = new CircleShareContent();
					circleMedia.setShareContent(jobInfo.getHighlights());
					circleMedia.setTitle(jobInfo.getPosition().getString(GlobalConstants.JSON_NAME));
//					circleMedia.setShareImage(localImage);
					circleMedia.setTargetUrl(url);
					mController.setShareMedia(circleMedia);
					
					//新浪分享
					SinaShareContent sinaShareContent = new SinaShareContent();
					sinaShareContent.setShareContent(jobInfo.getHighlights());
					sinaShareContent.setTitle(jobInfo.getPosition().getString(GlobalConstants.JSON_NAME));
//					sinaShareContent.setShareImage(localImage);
					sinaShareContent.setTargetUrl(url);
					mController.setShareMedia(sinaShareContent);
					customShareBoard.showAtLocation(getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);
					WindowManager.LayoutParams lp = getWindow().getAttributes();
					lp.alpha =0.65f;
					getWindow().setAttributes(lp);			
					break;
			}
		}
	};
	
	// Handler
	static class WeakHandler extends Handler {
		private WeakReference<PublishedPositionsActivity> mOuter;
		public WeakHandler(PublishedPositionsActivity activity) {
			mOuter = new WeakReference<PublishedPositionsActivity>(activity);
		}
		
		public void handleMessage(Message msg) {
			PublishedPositionsActivity outer = mOuter.get();
			if (outer != null) {
				// Do something
			}
		}
	}
	
	/**
	 * 刷新职位列表页
	 * @param jobInfo
	 */
	private void refreshPositionList(JobInfo jobInfo) {
		mPositionEventLogic.refreshPublishedPositions(jobInfo.getId());
	}
	
	private void closePublishedPosition(JobInfo jobInfo, int position) {
		this.position = position;
		mPositionEventLogic.closePublishedPositions(jobInfo.getId());
	}
	
	private void openPublishedPosition(JobInfo jobInfo, int position) {
		this.position = position;
		mPositionEventLogic.openPublishedPositions(jobInfo.getId());
	}
	

	public void initUIAndData() {
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		setContentView(R.layout.activity_published_position);
		setHeaderMenuByLeft(this);
		setHeaderMenuByCenterTitle(R.string.published_position_str);

		mEmptyView = (RelativeLayout) findViewById(R.id.empty_view);
		mPullListView = (PullToRefreshListView) findViewById(R.id.published_positions_list);
		mPullListView.setMode(Mode.PULL_FROM_END);
		mJobInfoList = new ArrayList<JobInfo>();
		mPublishedPosAdapter = new PublishedPositionAdapter(this, mJobInfoList, R.layout.published_position_item, mHandler);
		mPullListView.setAdapter(mPublishedPosAdapter);
		mPullListView.setEmptyView(mEmptyView);
		// 初始值传-1
		mPositionEventLogic.getPublishedPositions(String.valueOf(minRefreshId));
		setUIListeners();
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		// 此时，重新刷新数据
		mJobInfoList.clear();
		mPositionEventLogic.getPublishedPositions(String.valueOf(minRefreshId));
	}

	private void setUIListeners() {
		/*mPullListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (((JobInfo) parent.getItemAtPosition(position)).getStatus() == 1) {
					// 当前职位是开启的，才能进入候选人接收箱
					Intent intent = new Intent(PublishedPositionsActivity.this, CandidateSelectActivity.class);
					intent.putExtra("current_position", position-1);
					intent.putParcelableArrayListExtra("total_published_job_list", mJobInfoList);
					startActivity(intent);
				}
			}
		});*/
		
		customShareBoard = new CustomShareBoard(PublishedPositionsActivity.this);
		customShareBoard.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss() {
				WindowManager.LayoutParams lp = getWindow().getAttributes();
				lp.alpha = 1.0f;
				getWindow().setAttributes(lp);
			}
		});
	}
	
	// 执行网络请求后
	@Override
	public void onEventMainThread(Message msg) {
		if (msg.obj instanceof InfoResult) {
			InfoResult infoResult = (InfoResult) msg.obj;
			switch (msg.what) {
				case R.id.position_get_published_positions:
					// 此时返回JSON数据
					JSONObject mJSONResult = (JSONObject) JSONObject.parse(infoResult.getResult());
					if(mJSONResult != null){
						// 解析得到的JSON数8据
						JSONArray mJobJSONArray = mJSONResult.getJSONArray(GlobalConstants.JSON_JOBS);
						if(mJobJSONArray != null){
						// 只有存在数据的时候才展示
						if (mJobJSONArray.size() > 0) {
								if (minRefreshId == -1) {
									mJobInfoList.clear();
								}
								for(int i=0; i < mJobJSONArray.size(); i++) {
									JobInfo mJob = new JobInfo(mJobJSONArray.getJSONObject(i));
									mJobInfoList.add(mJob);
								}
								mPublishedPosAdapter.notifyDataSetChanged();
							} else {
								if (minRefreshId == -1) {
									mEmptyView.setVisibility(View.VISIBLE);
								}
							}
						}
					}
					break;
					
				case R.id.position_refresh_published_positions:
					if (infoResult.getMessage().getStatus() == 0) {
						// 此时刷新成功
						minRefreshId = -1;
						mPositionEventLogic.getPublishedPositions(String.valueOf(minRefreshId));
					}
					VToast.show(this, infoResult.getMessage().getMsg());
					break;
				case R.id.position_close_published_positions:
					if (infoResult.getMessage().getStatus() == 0) {
						// 此时关闭成功
						// 更新position处的数据
						mJobInfoList.get(position).setStatus(0);
						mPublishedPosAdapter.notifyDataSetChanged();
					}
					VToast.show(this, infoResult.getMessage().getMsg());
					break;
					
				case R.id.position_open_published_positions:
					if (infoResult.getMessage().getStatus() == 0) {
						// 此时打开成功
						// 更新position处的数据
						mJobInfoList.get(position).setStatus(1);
						mPublishedPosAdapter.notifyDataSetChanged();
					}
					VToast.show(this, infoResult.getMessage().getMsg());
					break;
			}
		}
	}

}
