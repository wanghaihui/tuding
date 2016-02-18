package com.xiaobukuaipao.vzhi;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView.OnEditorActionListener;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xiaobukuaipao.vzhi.adapter.SearchCitiesAdapter;
import com.xiaobukuaipao.vzhi.adapter.SearchJobAdapter;
import com.xiaobukuaipao.vzhi.adapter.SearchKeyWordsAdapter;
import com.xiaobukuaipao.vzhi.adapter.SearchTalentAdapter;
import com.xiaobukuaipao.vzhi.domain.KeyWords;
import com.xiaobukuaipao.vzhi.domain.Profession;
import com.xiaobukuaipao.vzhi.domain.Salary;
import com.xiaobukuaipao.vzhi.domain.SearchJob;
import com.xiaobukuaipao.vzhi.domain.user.UserProfile;
import com.xiaobukuaipao.vzhi.event.InfoResult;
import com.xiaobukuaipao.vzhi.util.DisplayUtil;
import com.xiaobukuaipao.vzhi.util.GlobalConstants;
import com.xiaobukuaipao.vzhi.util.StringUtils;
import com.xiaobukuaipao.vzhi.view.EditTextWithDel;
import com.xiaobukuaipao.vzhi.view.ProgressBarCircularIndeterminate;
import com.xiaobukuaipao.vzhi.view.refresh.PullToRefreshBase;
import com.xiaobukuaipao.vzhi.view.refresh.PullToRefreshListView;
import com.xiaobukuaipao.vzhi.view.refresh.PullToRefreshBase.OnLastItemVisibleListener;
import com.xiaobukuaipao.vzhi.view.refresh.PullToRefreshBase.OnRefreshListener2;
import com.xiaobukuaipao.vzhi.wrap.PositionWrapActivity;

public class SearchTalentActivity extends PositionWrapActivity {
	
	private TextView mCitySelector;
	private String mCity = null;
	private TextView mExprSelector;
	private String mExpr = null;
	private TextView mEduSelector;
	private String mEdu = null;
	private TextView mIntentionStatusSelector;
	private String mIntentionStatus = null;
	
	private PopupWindow popCityWindow;
	private PopupWindow popExprWindow;
	private PopupWindow popEduWindow;
	private PopupWindow popIntentionStatusWindow;

	private Profession profession;
	private String keyWord;
	
	private EditTextWithDel mSearchView;
	private View mSearchBtn;
	
	private String start = null;
	
	private List<UserProfile> mSearchTalentList;
	private PullToRefreshListView mSearchTalentListView;
	private SearchTalentAdapter mSearchTalentAdapter;
	
	private ProgressBarCircularIndeterminate mProgressBar;
	
	/**
	 * 下拉加载
	 */
	private boolean pullToRefresh = false;
	/**
	 * 加载更多
	 */
	private boolean loadMore = false;
	
	private View mEmptyView;
	
	private List<String> citiesList;
	private List<KeyWords> exprList;
	private List<KeyWords> eduList;
	private List<KeyWords> intentionStatusList;
	
	@Override
	public void initUIAndData() {
		setContentView(R.layout.activity_search_talent);
		setHeaderMenuByLeft(this);
		// 得到传过来的数据
		getIntents();
		
		mCitySelector = (TextView) findViewById(R.id.city_selector);
		mExprSelector = (TextView) findViewById(R.id.expr_selector);
		mEduSelector = (TextView) findViewById(R.id.edu_selector);
		mIntentionStatusSelector = (TextView) findViewById(R.id.job_state_selector);
		
		mSearchView = (EditTextWithDel) findViewById(R.id.search_talent_edit);
		mSearchBtn = (View) findViewById(R.id.menu_bar_search_btn_layout);
		
		mProgressBar = (ProgressBarCircularIndeterminate) findViewById(R.id.progressBarCircularIndeterminate);
		mEmptyView = (View) findViewById(R.id.not_matched_position);
		
		mSearchTalentList = new ArrayList<UserProfile>();
		mSearchTalentListView = (PullToRefreshListView) findViewById(R.id.search_talent_list);
		mSearchTalentAdapter = new SearchTalentAdapter(this, mSearchTalentList, R.layout.item_seeker);
		mSearchTalentListView.setAdapter(mSearchTalentAdapter);
		
		
		// 下拉刷新
		mSearchTalentListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				pullToRefresh = true;
				start = null;
				mPositionEventLogic.cancel(R.id.get_search_talent);
				
				if (profession == null) {
					// 此时, 根据key进行搜索
					if (keyWord != null) {
						mPositionEventLogic.getSearchTalent(null, keyWord, mCity, mExpr, mEdu, mIntentionStatus, start);
					}
				} else { 
					mPositionEventLogic.getSearchTalent(profession.getId(), null, mCity, mExpr, mEdu, mIntentionStatus, start);
				}
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
			}
		});
		// 加载更多
		mSearchTalentListView.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {

			@Override
			public void onLastItemVisible() {
					if (start != null && Integer.valueOf(start) > 0) {
						loadMore = true;
						mPositionEventLogic.cancel(R.id.get_search_talent);
						
						if (profession == null) {
							// 此时, 根据key进行搜索
							if (keyWord != null) {
								mPositionEventLogic.getSearchTalent(null, keyWord, mCity, mExpr, mEdu, mIntentionStatus, start);
							}
						} else { 
							mPositionEventLogic.getSearchTalent(profession.getId(), null, mCity, mExpr, mEdu, mIntentionStatus, start);
						}
						
					}
			}
		});
		
		initSearchDatas();
		
		setUiListeners();
		
		mPositionEventLogic.cancel(R.id.get_search_talent);
		if (profession == null) {
			// 此时, 根据key进行搜索
			if (keyWord != null) {
				mSearchView.setText(keyWord);
				mPositionEventLogic.getSearchTalent(null, keyWord, mCity, mExpr, mEdu, mIntentionStatus, start);
				mProgressBar.setVisibility(View.VISIBLE);
			}
		} else { 
			mSearchView.setText(profession.getName());
			mPositionEventLogic.getSearchTalent(profession.getId(), null, mCity, mExpr, mEdu, mIntentionStatus, start);
			mProgressBar.setVisibility(View.VISIBLE);
		}
	}
	
	private void getIntents() {
		profession = this.getIntent().getParcelableExtra("profession");
		keyWord = this.getIntent().getStringExtra("key");
	}
	
	private void initSearchDatas() {
		citiesList = new ArrayList<String>();
		String[] city=getResources().getStringArray(R.array.search_hot_cities);
		for (int i = 0; i < city.length; i++) {
			citiesList.add(city[i]);
		}
		
		exprList = new ArrayList<KeyWords>();
		exprList.add(new KeyWords("2", "一年以下"));
		exprList.add(new KeyWords("3", "1~3年"));
		exprList.add(new KeyWords("4", "3~5年"));
		exprList.add(new KeyWords("5", "5~10年"));
		exprList.add(new KeyWords("6", "10年以上"));
		exprList.add(new KeyWords("-1", "全部"));
		
		eduList = new ArrayList<KeyWords>();
		eduList.add(new KeyWords("1", "大专"));
		eduList.add(new KeyWords("2", "本科"));
		eduList.add(new KeyWords("3", "硕士"));
		eduList.add(new KeyWords("4", "博士"));
		eduList.add(new KeyWords("-1", "全部"));
		
		intentionStatusList = new ArrayList<KeyWords>();
		intentionStatusList.add(new KeyWords("1", "已离职，随时可以到岗"));
		intentionStatusList.add(new KeyWords("2", "在职，近期考虑换工作"));
		intentionStatusList.add(new KeyWords("3", "想了解更好的机会"));
		intentionStatusList.add(new KeyWords("4", "近期不换工作，可交流行情"));
		intentionStatusList.add(new KeyWords("5", "近期不换工作，挖人勿扰"));
		intentionStatusList.add(new KeyWords("-1", "全部"));
		
	}
	
	private void setUiListeners() {
		mCitySelector.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				popUpCityOverflow();
			}
		});
		
		mExprSelector.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				popUpExprOverflow();
			}
		});
		
		mEduSelector.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				popUpEduOverflow();
			}
		});
		
		mIntentionStatusSelector.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				popUpIntentionStatusOverflow();
			}
		});
		
		mSearchBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				if (StringUtils.isNotEmpty(mSearchView.getText().toString())) {
					performKeywordSearch();
				}
			}
			
		});
		
		mSearchView.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				switch (actionId) {
					case EditorInfo.IME_ACTION_SEARCH:
						if (StringUtils.isNotEmpty(mSearchView.getText().toString())) {
							performKeywordSearch();
						}
						break;
				}
				return false;
			}
		});
	}
	
	private void performKeywordSearch() {
		profession = null;
		keyWord = mSearchView.getText().toString();
		
		mCity = null;
		mExpr = null;
		mEdu = null;
		mIntentionStatus = null;
		start = null;
		
		mCitySelector.setText(this.getResources().getString(R.string.search_city));
		mExprSelector.setText(this.getResources().getString(R.string.search_expr));
		mEduSelector.setText(this.getResources().getString(R.string.search_edu));
		mIntentionStatusSelector.setText(this.getResources().getString(R.string.search_job_state));
		
		mPositionEventLogic.cancel(R.id.get_search_talent);
		if (profession == null) {
			// 此时, 根据key进行搜索
			if (keyWord != null) {
				mPositionEventLogic.getSearchTalent(null, keyWord, mCity, mExpr, mEdu, mIntentionStatus, start);
				mProgressBar.setVisibility(View.VISIBLE);
			}
		} else { 
			mPositionEventLogic.getSearchTalent(profession.getId(), null, mCity, mExpr, mEdu, mIntentionStatus, start);
			mProgressBar.setVisibility(View.VISIBLE);
		}
	}
	
	/**
	 * 弹出城市的Pop Window
	 */
	private void popUpCityOverflow() {
		/**
		* 定位PopupWindow，让它恰好显示在Action Bar的下方。 通过设置Gravity，确定PopupWindow的大致位置。
		* 首先获得状态栏的高度，再获取Action bar的高度，这两者相加设置y方向的offset样PopupWindow就显示在action
		* bar的下方了。 通过dp计算出px，就可以在不同密度屏幕统一X方向的offset.但是要注意不要让背景阴影大于所设置的offset，
		 * 否则阴影的宽度为offset.
		*/
		// 获取状态栏高度
		Rect frame = new Rect();
		getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
		// 状态栏高度：frame.top
		int xOffset = frame.top + DisplayUtil.dip2px(this, 96) + 1;//减去阴影宽度，适配UI.
		int yOffset = DisplayUtil.dip2px(this, 0f); //设置x方向offset为0dp 
		
		View parentView = getLayoutInflater().inflate(R.layout.activity_search_talent, null);
		View popView = getLayoutInflater().inflate(R.layout.action_overflow_city_popwindow, null);
		
		// popView即popupWindow的布局，ture设置focusAble
		popCityWindow = new PopupWindow(popView, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, true);
		// 必须设置BackgroundDrawable后setOutsideTouchable(true)才会有效
		// 这里在XML中定义背景，所以这里设置为null;
		// popCityWindow.setBackgroundDrawable(new BitmapDrawable(getResources(),  (Bitmap) null));
		popCityWindow.setBackgroundDrawable(new ColorDrawable(0));
		WindowManager.LayoutParams lp = getWindow().getAttributes();
		lp.alpha = 0.5f;
		getWindow().setAttributes(lp);
		
		popCityWindow.setTouchInterceptor(new OnTouchListener() {

			@SuppressLint("ClickableViewAccessibility")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
					if (popCityWindow.isShowing())
						popCityWindow.dismiss();
					return true;
				}
				return false;
			}
		});

		popCityWindow.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				WindowManager.LayoutParams lp = getWindow().getAttributes();
				lp.alpha = 1f;
				getWindow().setAttributes(lp);
			}
		});
		
		//点击外部关闭
		// popCityWindow.setOutsideTouchable(true);
		popCityWindow.setAnimationStyle(android.R.style.Animation_Dialog);    //设置一个动画。
		// 设置Gravity，让它显示在右上角
		popCityWindow.showAtLocation(parentView, Gravity.RIGHT | Gravity.TOP, yOffset, xOffset);
		
		ListView mCityListView = (ListView) popView.findViewById(R.id.city_list_view);
		SearchCitiesAdapter mSearchCitiesAdapter = new SearchCitiesAdapter(this, citiesList, R.layout.item_search_select);
		mCityListView.setAdapter(mSearchCitiesAdapter);
		
		mCityListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				mCity = (String) parent.getItemAtPosition(position);
				mCitySelector.setText(mCity);
				mPositionEventLogic.cancel(R.id.get_search_talent);
				
				start = null;
				
				if (profession == null) {
					// 此时, 根据key进行搜索
					if (keyWord != null) {
						mPositionEventLogic.getSearchTalent(null, keyWord, mCity, mExpr, mEdu, mIntentionStatus, start);
						mProgressBar.setVisibility(View.VISIBLE);
					}
				} else { 
					mPositionEventLogic.getSearchTalent(profession.getId(), null, mCity, mExpr, mEdu, mIntentionStatus, start);
					mProgressBar.setVisibility(View.VISIBLE);
				}
				
				// 去掉PopupWindow
				if (popCityWindow.isShowing()) popCityWindow.dismiss();
			}
		});
	}
	
	
	/**
	 * 弹出经验的Pop Window
	 */
	private void popUpExprOverflow() {
		/**
		* 定位PopupWindow，让它恰好显示在Action Bar的下方。 通过设置Gravity，确定PopupWindow的大致位置。
		* 首先获得状态栏的高度，再获取Action bar的高度，这两者相加设置y方向的offset样PopupWindow就显示在action
		* bar的下方了。 通过dp计算出px，就可以在不同密度屏幕统一X方向的offset.但是要注意不要让背景阴影大于所设置的offset，
		 * 否则阴影的宽度为offset.
		*/
		// 获取状态栏高度
		Rect frame = new Rect();
		getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
		// 状态栏高度：frame.top
		int xOffset = frame.top + DisplayUtil.dip2px(this, 96) + 1;//减去阴影宽度，适配UI.
		int yOffset = DisplayUtil.dip2px(this, 0f); //设置x方向offset为0dp 
		
		View parentView = getLayoutInflater().inflate(R.layout.activity_search_talent, null);
		View popView = getLayoutInflater().inflate(R.layout.action_overflow_expr_popwindow, null);
		
		// popView即popupWindow的布局，ture设置focusAble
		popExprWindow = new PopupWindow(popView, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, true);
		
		popExprWindow.setBackgroundDrawable(new ColorDrawable(0));
		
		WindowManager.LayoutParams lp = getWindow().getAttributes();
		lp.alpha = 0.5f;
		getWindow().setAttributes(lp);
		
		popExprWindow.setTouchInterceptor(new OnTouchListener() {

			@SuppressLint("ClickableViewAccessibility")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
					if (popExprWindow.isShowing())
						popExprWindow.dismiss();
					return true;
				}
				return false;
			}
		});

		popExprWindow.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				WindowManager.LayoutParams lp = getWindow().getAttributes();
				lp.alpha = 1f;
				getWindow().setAttributes(lp);
			}
		});
		// 必须设置BackgroundDrawable后setOutsideTouchable(true)才会有效
		// 这里在XML中定义背景，所以这里设置为null;
		//popExprWindow.setBackgroundDrawable(new BitmapDrawable(getResources(),  (Bitmap) null));
		//popExprWindow.setOutsideTouchable(true); //点击外部关闭
		
		popExprWindow.setAnimationStyle(android.R.style.Animation_Dialog);    //设置一个动画。
		// 设置Gravity，让它显示在右上角
		popExprWindow.showAtLocation(parentView, /*Gravity.RIGHT | */Gravity.TOP, yOffset, xOffset);
		
		ListView mExprListView = (ListView) popView.findViewById(R.id.expr_list_view);
		SearchKeyWordsAdapter mSearchExprAdapter = new SearchKeyWordsAdapter(this, exprList, R.layout.item_search_select);
		mExprListView.setAdapter(mSearchExprAdapter);
		mExprListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				KeyWords keyword = (KeyWords) parent.getItemAtPosition(position);
				mExpr = keyword.getId();
				mExprSelector.setText(keyword.getName());
				mPositionEventLogic.cancel(R.id.get_search_talent);
				
				start = null;
				
				if (profession == null) {
					// 此时, 根据key进行搜索
					if (keyWord != null) {
						mPositionEventLogic.getSearchTalent(null, keyWord, mCity, mExpr, mEdu, mIntentionStatus, start);
						mProgressBar.setVisibility(View.VISIBLE);
					}
				} else { 
					mPositionEventLogic.getSearchTalent(profession.getId(), null, mCity, mExpr, mEdu, mIntentionStatus, start);
					mProgressBar.setVisibility(View.VISIBLE);
				}
				
				// 去掉PopupWindow
				if (popExprWindow.isShowing()) popExprWindow.dismiss();
			}
			
		});
	}
	
	/**
	 * 弹出学历的Pop Window
	 */
	private void popUpEduOverflow() {
		/**
		* 定位PopupWindow，让它恰好显示在Action Bar的下方。 通过设置Gravity，确定PopupWindow的大致位置。
		* 首先获得状态栏的高度，再获取Action bar的高度，这两者相加设置y方向的offset样PopupWindow就显示在action
		* bar的下方了。 通过dp计算出px，就可以在不同密度屏幕统一X方向的offset.但是要注意不要让背景阴影大于所设置的offset，
		 * 否则阴影的宽度为offset.
		*/
		// 获取状态栏高度
		Rect frame = new Rect();
		getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
		// 状态栏高度：frame.top
		int xOffset = frame.top + DisplayUtil.dip2px(this, 96) + 1;//减去阴影宽度，适配UI.
		int yOffset = DisplayUtil.dip2px(this, 0f); //设置x方向offset为0dp 
		
		View parentView = getLayoutInflater().inflate(R.layout.activity_search_talent, null);
		View popView = getLayoutInflater().inflate(R.layout.action_overflow_edu_popwindow, null);
		
		// popView即popupWindow的布局，ture设置focusAble
		popEduWindow = new PopupWindow(popView, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, true);
		
		popEduWindow.setBackgroundDrawable(new ColorDrawable(0));
		
		WindowManager.LayoutParams lp = getWindow().getAttributes();
		lp.alpha = 0.5f;
		getWindow().setAttributes(lp);
		
		popEduWindow.setTouchInterceptor(new OnTouchListener() {

			@SuppressLint("ClickableViewAccessibility")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
					if (popEduWindow.isShowing())
						popEduWindow.dismiss();
					return true;
				}
				return false;
			}
		});

		popEduWindow.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				WindowManager.LayoutParams lp = getWindow().getAttributes();
				lp.alpha = 1f;
				getWindow().setAttributes(lp);
			}
		});
		
		popEduWindow.setAnimationStyle(android.R.style.Animation_Dialog);    //设置一个动画。
		// 设置Gravity，让它显示在右上角
		popEduWindow.showAtLocation(parentView, /*Gravity.RIGHT | */Gravity.TOP, yOffset, xOffset);
		
		ListView mExprListView = (ListView) popView.findViewById(R.id.edu_list_view);
		SearchKeyWordsAdapter mSearchEduAdapter = new SearchKeyWordsAdapter(this, eduList, R.layout.item_search_select);
		mExprListView.setAdapter(mSearchEduAdapter);
		mExprListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				KeyWords keyword = (KeyWords) parent.getItemAtPosition(position);
				mEdu = keyword.getId();
				mEduSelector.setText(keyword.getName());
				mPositionEventLogic.cancel(R.id.get_search_talent);
				
				start = null;
				
				if (profession == null) {
					// 此时, 根据key进行搜索
					if (keyWord != null) {
						mPositionEventLogic.getSearchTalent(null, keyWord, mCity, mExpr, mEdu, mIntentionStatus, start);
						mProgressBar.setVisibility(View.VISIBLE);
					}
				} else { 
					mPositionEventLogic.getSearchTalent(profession.getId(), null, mCity, mExpr, mEdu, mIntentionStatus, start);
					mProgressBar.setVisibility(View.VISIBLE);
				}
				
				// 去掉PopupWindow
				if (popEduWindow.isShowing()) popEduWindow.dismiss();
			}
			
		});
	}
	
	/**
	 * 弹出求职心态Pop Window
	 */
	private void popUpIntentionStatusOverflow() {
		/**
		* 定位PopupWindow，让它恰好显示在Action Bar的下方。 通过设置Gravity，确定PopupWindow的大致位置。
		* 首先获得状态栏的高度，再获取Action bar的高度，这两者相加设置y方向的offset样PopupWindow就显示在action
		* bar的下方了。 通过dp计算出px，就可以在不同密度屏幕统一X方向的offset.但是要注意不要让背景阴影大于所设置的offset，
		 * 否则阴影的宽度为offset.
		*/
		// 获取状态栏高度
		Rect frame = new Rect();
		getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
		// 状态栏高度：frame.top
		int xOffset = frame.top + DisplayUtil.dip2px(this, 96) + 1;//减去阴影宽度，适配UI.
		int yOffset = DisplayUtil.dip2px(this, 0f); //设置x方向offset为0dp 
		
		View parentView = getLayoutInflater().inflate(R.layout.activity_search_talent, null);
		View popView = getLayoutInflater().inflate(R.layout.action_overflow_intention_state_popwindow, null);
		
		// popView即popupWindow的布局，ture设置focusAble
		popIntentionStatusWindow = new PopupWindow(popView, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, true);
		
		popIntentionStatusWindow.setBackgroundDrawable(new ColorDrawable(0));
		
		WindowManager.LayoutParams lp = getWindow().getAttributes();
		lp.alpha = 0.5f;
		getWindow().setAttributes(lp);
		
		popIntentionStatusWindow.setTouchInterceptor(new OnTouchListener() {

			@SuppressLint("ClickableViewAccessibility")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
					if (popIntentionStatusWindow.isShowing())
						popIntentionStatusWindow.dismiss();
					return true;
				}
				return false;
			}
		});

		popIntentionStatusWindow.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				WindowManager.LayoutParams lp = getWindow().getAttributes();
				lp.alpha = 1f;
				getWindow().setAttributes(lp);
			}
		});
		
		popIntentionStatusWindow.setAnimationStyle(android.R.style.Animation_Dialog);    //设置一个动画。
		// 设置Gravity，让它显示在右上角
		popIntentionStatusWindow.showAtLocation(parentView, /*Gravity.RIGHT | */Gravity.TOP, yOffset, xOffset);
		
		ListView mExprListView = (ListView) popView.findViewById(R.id.intention_status_list_view);
		SearchKeyWordsAdapter mSearchIntentionStatusAdapter = new SearchKeyWordsAdapter(this, intentionStatusList, R.layout.item_search_select);
		mExprListView.setAdapter(mSearchIntentionStatusAdapter);
		mExprListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				KeyWords keyword = (KeyWords) parent.getItemAtPosition(position);
				mIntentionStatus = keyword.getId();
				mIntentionStatusSelector.setText(keyword.getName());
				mPositionEventLogic.cancel(R.id.get_search_talent);
				
				start = null;
				
				if (profession == null) {
					// 此时, 根据key进行搜索
					if (keyWord != null) {
						mPositionEventLogic.getSearchTalent(null, keyWord, mCity, mExpr, mEdu, mIntentionStatus, start);
						mProgressBar.setVisibility(View.VISIBLE);
					}
				} else { 
					mPositionEventLogic.getSearchTalent(profession.getId(), null, mCity, mExpr, mEdu, mIntentionStatus, start);
					mProgressBar.setVisibility(View.VISIBLE);
				}
				
				// 去掉PopupWindow
				if (popIntentionStatusWindow.isShowing()) popIntentionStatusWindow.dismiss();
			}
			
		});
	}

	@Override
	public void onEventMainThread(Message msg) {
		if (msg.obj instanceof InfoResult) {
			InfoResult infoResult = (InfoResult) msg.obj;
			
			switch (msg.what) {
				case R.id.get_search_talent:
					Log.d(TAG, infoResult.getResult());
					JSONObject talents = (JSONObject) JSONObject.parse(infoResult.getResult());
					if (talents != null) {
						
						if (pullToRefresh) {
							pullToRefresh = false;
							mSearchTalentListView.onRefreshComplete();
						}
						
						JSONArray mTalentArray = talents.getJSONArray(GlobalConstants.JSON_DATA);
						
						// 先清空
						if (!loadMore) {
							mSearchTalentList.clear();
						} else {
							loadMore = false;
							mSearchTalentListView.onRefreshComplete();
						}
						
						if (mTalentArray != null) {
							for (int i = 0; i < mTalentArray.size(); i++) {
								mSearchTalentList.add(new UserProfile(mTalentArray.getJSONObject(i)));
							}
						} else {
							mSearchTalentListView.onRefreshComplete();
						}
						
						mSearchTalentAdapter.notifyDataSetChanged();
						
						start = talents.getString(GlobalConstants.JSON_START);
						
						if (mSearchTalentList.size() == 0) {
							mEmptyView.setVisibility(View.VISIBLE);
						} else {
							mEmptyView.setVisibility(View.GONE);
						}
						
						mProgressBar.setVisibility(View.GONE);
					}
					break;
				default:
					break;
			}
		} else {
			mProgressBar.setVisibility(View.GONE);
		}
	}

}
