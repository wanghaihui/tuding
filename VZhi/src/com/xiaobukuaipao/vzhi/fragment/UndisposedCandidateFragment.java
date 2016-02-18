package com.xiaobukuaipao.vzhi.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xiaobukuaipao.vzhi.AnoPersonInfoActivity;
import com.xiaobukuaipao.vzhi.AuditionActivity;
import com.xiaobukuaipao.vzhi.ChatPersonActivity;
import com.xiaobukuaipao.vzhi.PersonalShowPageActivity;
import com.xiaobukuaipao.vzhi.R;
import com.xiaobukuaipao.vzhi.adapter.UndisposedAdapter;
import com.xiaobukuaipao.vzhi.contentprovider.GeneralContentProvider;
import com.xiaobukuaipao.vzhi.database.MessageSummaryTable;
import com.xiaobukuaipao.vzhi.domain.CandidateInfo;
import com.xiaobukuaipao.vzhi.domain.JobInfo;
import com.xiaobukuaipao.vzhi.event.CandidateEvent;
import com.xiaobukuaipao.vzhi.event.InfoResult;
import com.xiaobukuaipao.vzhi.flingswipe.CardContainer;
import com.xiaobukuaipao.vzhi.im.IMConstants;
import com.xiaobukuaipao.vzhi.util.GlobalConstants;
import com.xiaobukuaipao.vzhi.util.Logcat;
import com.xiaobukuaipao.vzhi.util.StringUtils;
import com.xiaobukuaipao.vzhi.view.ProgressBarCircularIndeterminate;

import de.greenrobot.event.EventBus;

public class UndisposedCandidateFragment extends PositionWrapFragment {
	private static final String TAG = UndisposedCandidateFragment.class.getSimpleName();
	
	private View view;
	// CardContainer--AdapterView--继承自ViewGroup
	private CardContainer cardContainerView;
	
	// 当前的位置
	private Integer actualPosition;
	private ArrayList<JobInfo> mOpenedPositions;
	
	private List<CandidateInfo> mUndisposedCandidate;
	
	private UndisposedAdapter mUndisposedAdapter;

	private TextView mCandidateInterest;
	private TextView mCandidateInterview;
	private TextView mCandidateChat;
	
	private Animation animation;
	
	private ProgressBarCircularIndeterminate mProgressBar;
	private TextView mCandidateEmpty;
	
	private LinearLayout mBottomLayout;
	
	public UndisposedCandidateFragment() {
		
	}
	
	public UndisposedCandidateFragment(Integer actualPosition, ArrayList<JobInfo> mOpenedPositions) {
		this.actualPosition = actualPosition;
		this.mOpenedPositions = mOpenedPositions;
	}
	
	public UndisposedCandidateFragment(Activity activity) {
		this.activity = activity;
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		/*try {
			mUndisposedCandidateListener = (UndisposedCandidateListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnItemSelectedListener");
        }*/
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_undisposed_candidate, container, false);
		this.view = view;
		return view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Log.i(TAG, "actualPosition" + actualPosition);
		initUIAndData();
	}

	public void initUIAndData() {
		animation = AnimationUtils.loadAnimation(getActivity(), R.anim.anim_scale_in);
		
		cardContainerView = (CardContainer) view.findViewById(R.id.candidate_card_container);
		mCandidateInterest = (TextView) view.findViewById(R.id.candidate_interest);
		mCandidateInterest.setVisibility(View.VISIBLE);
		mCandidateInterview = (TextView) view.findViewById(R.id.candidate_interview);
		mCandidateInterview.setVisibility(View.GONE);
		
		mCandidateChat = (TextView) view.findViewById(R.id.candidate_chat);
		
		mProgressBar = (ProgressBarCircularIndeterminate) view.findViewById(R.id.progressBarCircularIndeterminate);
		
		mCandidateEmpty = (TextView) view.findViewById(R.id.candidate_empty);
		mBottomLayout = (LinearLayout) view.findViewById(R.id.candidate_bottom_layout);
		
		mUndisposedCandidate = new ArrayList<CandidateInfo>();
		
		mUndisposedAdapter = new UndisposedAdapter(UndisposedCandidateFragment.this.getActivity(), mUndisposedCandidate, R.layout.select_resume_card){
			@Override
			public void notifyDataSetChanged() {
				refreshLocalUnreadCandidate();
				super.notifyDataSetChanged();
			}
		};
		cardContainerView.setAdapter(mUndisposedAdapter);
		
		// 得到当前的未处理的候选人
		mPositionEventLogic.getUnreadCandidate(mOpenedPositions.get(actualPosition).getId());
		
		mProgressBar.setVisibility(View.GONE);
		
		setUIListener();
		// 感兴趣或者通知面试
		// view.findViewById(R.id.candidate_interview).setOnClickListener(this);
	}

	private void setUIListener() {
		
		cardContainerView.setFlingListener(new CardContainer.OnFlingListener() {
			
            @Override
            public void removeFirstObjectInAdapter() {
                // this is the simplest way to delete an object from the Adapter (/AdapterView)
            	if (mUndisposedCandidate.size() > 0) {
            		// 临时屏蔽
            		mPositionEventLogic.readCandidate(mUndisposedCandidate.get(0).getApplyjob().getString(GlobalConstants.JSON_ID), 
            				mUndisposedCandidate.get(0).getBasic().getString(GlobalConstants.JSON_ID));
					// cardContainerView.getSelectedView();	
            	} else {
            		mUndisposedCandidate.clear();
            		// Log.i(TAG, "get unread candidate");
            		mPositionEventLogic.getUnreadCandidate(mOpenedPositions.get(actualPosition).getId());
            	}
            	
            }

            @Override
            public void onLeftCardExit(Object dataObject) {
                // Do something on the left!
                // You also have access to the original object.
                // If you want to use it just cast it (String) dataObject
            }

            @Override
            public void onRightCardExit(Object dataObject) {
            	
            }
            
            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
                // Ask for more data here
            	Log.i(TAG, "get unread candidate");
            	Log.i(TAG, "undisposed candidate count : " + mUndisposedCandidate.size());
            	// 此时继续加载
            	// mProgressBar.setVisibility(View.VISIBLE);
            	// mPositionEventLogic.getUnreadCandidate(mOpenedPositions.get(actualPosition).getId());
            }

			@Override
			public void onCardEntered() {
				/*mPositionEventLogic.readCandidate(mUndisposedCandidate.get(0).getApplyjob().getString(GlobalConstants.JSON_ID), 
        				mUndisposedCandidate.get(0).getBasic().getString(GlobalConstants.JSON_ID));*/
			}
        });
		
        // Optionally add an OnItemClickListener
		cardContainerView.setOnItemClickListener(new CardContainer.OnItemClickListener() {
            @Override
            public void onItemClicked(int itemPosition, Object dataObject) {

            	Intent intent = new Intent();
    			intent.setClass(getActivity(), mUndisposedCandidate.get(itemPosition).getIsreal() == 1 ? PersonalShowPageActivity.class : AnoPersonInfoActivity.class);
    			intent.putExtra(GlobalConstants.CANDIDATE_VIEW, true);
    			intent.putExtra(GlobalConstants.JOB_ID, mUndisposedCandidate.get(itemPosition).getApplyjob().getString(GlobalConstants.JSON_ID));
    			intent.putExtra(GlobalConstants.UID, mUndisposedCandidate.get(itemPosition).getBasic().getString(GlobalConstants.JSON_ID));
    			intent.putExtra(GlobalConstants.ISREAL, mUndisposedCandidate.get(itemPosition).getIsreal());
    			intent.putExtra(GlobalConstants.IS_CONTACTED, (mUndisposedCandidate.get(itemPosition).getContactstatus() & 1));
				intent.putExtra(GlobalConstants.CONTACT_STATUS, mUndisposedCandidate.get(itemPosition).getContactstatus());
    			getActivity().startActivityForResult(intent,201);
            }
        });
		
		
		// 感兴趣
		mCandidateInterest.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				// 执行感兴趣操作
				if (mUndisposedCandidate.size() > 0) {
					
					mPositionEventLogic.interestCandidate(mUndisposedCandidate.get(0).getApplyjob().getString(GlobalConstants.JSON_ID), 
	        				mUndisposedCandidate.get(0).getBasic().getString(GlobalConstants.JSON_ID));
					
					// TODO 动画出现卡顿的原因 每次卡片离开容器要重绘每个卡片的位置 当重绘没有结束的时候就运行动画 就会产生卡顿现象
					getActivity().runOnUiThread(new Runnable() {
						@Override
						public void run() {
							View check = cardContainerView.getSelectedView().findViewById(R.id.check);
							check.startAnimation(animation);
							check.setVisibility(View.VISIBLE);
						}
					});
					
				}
			}
		});
		
		
		// 约面试
		mCandidateInterview.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if (mUndisposedCandidate.size() > 0) {
					CandidateInfo candidateInfo = mUndisposedCandidate.get(cardContainerView.getFirstVisiblePosition());
					String jid = candidateInfo.getApplyjob().getString(GlobalConstants.JSON_ID);
					String uid = candidateInfo.getBasic().getString(GlobalConstants.JSON_ID);		
					Integer isreal = candidateInfo.getIsreal();
					if(StringUtils.isNotEmpty(jid)&& StringUtils.isNotEmpty(uid)){
						// 执行约面试操作
						Intent intent = new Intent();
						intent.setClass(getActivity(), AuditionActivity.class);
						intent.putExtra(GlobalConstants.JOB_ID, jid);
						intent.putExtra(GlobalConstants.UID, uid);
						intent.putExtra(GlobalConstants.ISREAL, isreal);
						getActivity().startActivityForResult(intent,201);
					}
				}
			}
		});
		
		// 聊一聊
		mCandidateChat.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Log.i(TAG, "candidate chat");
				if(mUndisposedCandidate.size() > 0) {
					Log.i(TAG, "undisposed candidate size ");
					CandidateInfo candidateInfo = mUndisposedCandidate.get(cardContainerView.getFirstVisiblePosition());
					String jid = candidateInfo.getApplyjob().getString(GlobalConstants.JSON_ID);
					String receiverid = candidateInfo.getBasic().getString(GlobalConstants.JSON_ID);		
					Integer receiverisreal = candidateInfo.getIsreal();
					if (StringUtils.isNotEmpty(jid) && StringUtils.isNotEmpty(receiverid)) {
						// 执行聊一聊操作
						mSocialEventLogic.candidateChat(jid, receiverid, receiverisreal);
					}
				}
			}
		});
	}

	@Override
	public void onResume() {
		super.onResume();
	}
	
	@Override
	public void onEventMainThread(Message msg) {
		if (msg.obj instanceof InfoResult) {
			InfoResult infoResult = (InfoResult) msg.obj;
			switch (msg.what) {
				// 统计候选人信息
				case R.id.position_unread_candidate:
					// 此时返回JSON数据
					JSONObject mJSONResult = (JSONObject) JSONObject.parse(infoResult.getResult());
					
					Log.i(TAG, "unread candidate : " + infoResult.getResult());
					
					JSONArray mDataArray = mJSONResult.getJSONArray(GlobalConstants.JSON_DATA);
					
					mProgressBar.setVisibility(View.GONE);
					
					mUndisposedCandidate.clear();
					
					if (mDataArray != null && mDataArray.size() > 0) {
						for(int i=0; i < mDataArray.size(); i++) {
							mUndisposedCandidate.add(new CandidateInfo(mDataArray.getJSONObject(i)));
						}
						
						mCandidateEmpty.setVisibility(View.GONE);
						mBottomLayout.setVisibility(View.VISIBLE);
					} else {
						// 此时, 已经不存在候选人
						mCandidateEmpty.setVisibility(View.VISIBLE);
						mBottomLayout.setVisibility(View.GONE);
					}
					
					mUndisposedAdapter.notifyDataSetChanged();
					
					break;
					
				case R.id.position_read_candidate:
					if (infoResult.getMessage().getStatus() == 0) {
						
						// 候选人已读
						// mUndisposedCandidateListener.onChangePositionListener(mUndisposedCandidate.get(0).getApplyjob().getString(GlobalConstants.JSON_ID));
	            		
						mUndisposedCandidate.remove(0);
		                mUndisposedAdapter.notifyDataSetChanged();
	            		
	        			mCandidateInterest.setVisibility(View.VISIBLE);
	        			mCandidateInterview.setVisibility(View.GONE);

						// 此时，六张卡片全部读完
						if (mUndisposedCandidate.size() == 0) {
							mProgressBar.setVisibility(View.VISIBLE);
			            	mPositionEventLogic.getUnreadCandidate(mOpenedPositions.get(actualPosition).getId());
						}else{
							Logcat.d("@@@", "mUndisposedCandidate.get(0).getContactstatus(): " + mUndisposedCandidate.get(0).getContactstatus());
			        		if ((mUndisposedCandidate.get(cardContainerView.getFirstVisiblePosition()).getContactstatus() & 1) == 1) {
			        			mCandidateInterest.setVisibility(View.GONE);
			        			mCandidateInterview.setVisibility(View.VISIBLE);
			        		}
						}
						
						refreshLocalUnreadCandidate();
					}
					
					Toast.makeText(UndisposedCandidateFragment.this.getActivity(), "已读", Toast.LENGTH_SHORT).show();
					break;
					
				case R.id.position_interest_candidate:
					if (infoResult.getMessage().getStatus() == 0) {
						// 如果感兴趣设置成功，那么变为约面试
						mCandidateInterest.setVisibility(View.GONE);
						mCandidateInterview.setVisibility(View.VISIBLE);
						
						CandidateInfo candidateInfo = mUndisposedCandidate.get(cardContainerView.getFirstVisiblePosition());
						CandidateEvent event = new CandidateEvent();
						event.setContactStatus(candidateInfo.getContactstatus() | 1);
						if(candidateInfo.getApplyjob().getString(GlobalConstants.JSON_ID) != null){
							event.setJid(candidateInfo.getApplyjob().getString(GlobalConstants.JSON_ID));
						}
						if(candidateInfo.getBasic().getString(GlobalConstants.JSON_ID) != null){
							event.setUid(candidateInfo.getBasic().getString(GlobalConstants.JSON_ID));
						}
						EventBus.getDefault().post(event);
						
						// mUndisposedCandidateListener.onChangePositionListener(mUndisposedCandidate.get(0).getApplyjob().getString(GlobalConstants.JSON_ID));
					}
					break;
					
				case R.id.social_candidate_chat:
					// 聊一聊
					if (infoResult.getMessage().getStatus() == 0) {
						// 此时会返回一个did, 接着可以打开一个对话dialog
						Log.i(TAG, infoResult.getResult());
						// mUndisposedCandidateListener.onChangePositionListener(mUndisposedCandidate.get(0).getApplyjob().getString(GlobalConstants.JSON_ID));
						// 聊一聊之后，更新全部和已联系界面
						// mAllAndContactedStatusListener.onChangeStatusListener();
						
						CandidateInfo candidateInfo = mUndisposedCandidate.get(cardContainerView.getFirstVisiblePosition());
						String receiverid = candidateInfo.getBasic().getString(GlobalConstants.JSON_ID);		
						Integer receiverisreal = candidateInfo.getIsreal();
						String name;
						
						if (receiverisreal == 1) {
							name = candidateInfo.getBasic().getString(GlobalConstants.JSON_REALNAME);
						} else {
							name = candidateInfo.getBasic().getString(GlobalConstants.JSON_NICKNAME);
						}
						CandidateEvent event = new CandidateEvent();
						event.setContactStatus(candidateInfo.getContactstatus() | 16);
						if(candidateInfo.getApplyjob().getString(GlobalConstants.JSON_ID) != null){
							event.setJid(candidateInfo.getApplyjob().getString(GlobalConstants.JSON_ID));
						}
						if(candidateInfo.getBasic().getString(GlobalConstants.JSON_ID) != null){
							event.setUid(candidateInfo.getBasic().getString(GlobalConstants.JSON_ID));
						}
						EventBus.getDefault().post(event);
						
						Intent intent = new Intent(this.getActivity(), ChatPersonActivity.class);
						intent.putExtra("sender", Long.valueOf(receiverid));
						intent.putExtra("is_real", receiverisreal);
						intent.putExtra("dname", name);
						intent.putExtra("is_candidate", true);
						this.getActivity().startActivity(intent);
					}
					
					break;
			}
		}
	}

	private void refreshLocalUnreadCandidate() {
		int count  = 0;
		if(mUndisposedCandidate != null && mUndisposedCandidate.size() > 0){
			for (int i = 0; i < mUndisposedCandidate.size(); i++) {
				if(mUndisposedCandidate.get(i).getContactstatus() == 0){
					count ++;
				}
			}
		}
		// 更新数据库
		ContentValues values = new ContentValues();
		values.put(MessageSummaryTable.COLUMN_UNREADCOUNT, count);
		Logcat.d("@@@", "read count : " + count);
		getActivity().getContentResolver().update(GeneralContentProvider.MESSAGE_SUMMARY_CONTENT_URI, values, 
				MessageSummaryTable.COLUMN_MESSAGE_DISPLAY_TYPE + " = ?", new String[] { String.valueOf(IMConstants.DISPLAY_LIST_TYPE_JOBAPPLY) });
		values.clear();
	}

	// 侧边栏点击监听器
	public interface UndisposedCandidateListener {
		// 传入JobInfo的id
		public void onChangePositionListener(String jid);
	}
	
	// 感兴趣，聊一聊
	public interface AllAndContactedStatusListener {
		public void onChangeStatusListener();
	}
	
	/**
	 * 
	 */
	public void onEvent(CandidateEvent candidate){
		if(candidate != null){
			for(CandidateInfo info : mUndisposedCandidate){
				if (info.getApplyjob().getString(GlobalConstants.JSON_ID) != null && info.getApplyjob().getString(GlobalConstants.JSON_ID).equals(candidate.getJid())
						&& info.getBasic().getString(GlobalConstants.JSON_ID) != null && info.getBasic().getString(GlobalConstants.JSON_ID).equals(candidate.getUid())) {
					info.setContactstatus(candidate.getContactStatus());
					info.setReadstatus(candidate.getContactStatus());
					break;
				}
			}	
		}

		//候选人处理页面 如果状态返回是感兴趣 就把约面试显示出来
		if ((candidate.getContactStatus() & 1) == 1) {
			mCandidateInterest.setVisibility(View.GONE);
			mCandidateInterview.setVisibility(View.VISIBLE);
		}

		refreshLocalUnreadCandidate();
		
		
	}
	
}
