package com.xiaobukuaipao.vzhi;

import java.util.ArrayList;
import java.util.List;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xiaobukuaipao.vzhi.adapter.CommonAdapter;
import com.xiaobukuaipao.vzhi.adapter.SuggestedPositionAdapter;
import com.xiaobukuaipao.vzhi.adapter.ViewHolder;
import com.xiaobukuaipao.vzhi.domain.KeyWords;
import com.xiaobukuaipao.vzhi.domain.Profession;
import com.xiaobukuaipao.vzhi.event.InfoResult;
import com.xiaobukuaipao.vzhi.util.GlobalConstants;
import com.xiaobukuaipao.vzhi.util.StringUtils;
import com.xiaobukuaipao.vzhi.view.EditTextWithDel;
import com.xiaobukuaipao.vzhi.view.NestedGridView;
import com.xiaobukuaipao.vzhi.wrap.PositionWrapActivity;

public class SearchPositionPreviewActivity extends PositionWrapActivity {
	private static final String TAG = SearchPositionPreviewActivity.class.getSimpleName();

	private List<Profession> mGridList;
	private NestedGridView mGridView;
	private SearchPositionAdapter mSearchPreviewAdapter;
	
	private List<KeyWords> mSuggestedList;
	private NestedGridView mSuggestedGridView;
	private SuggestedPositionAdapter mSuggestedPositionAdapter;
	
	private View mSearchLayout;
	private EditTextWithDel mSearchBar;
	
	private TextView mHotCityView;
	
	@Override
	public void initUIAndData() {
		setContentView(R.layout.activity_search_position_preview);
		setHeaderMenuByLeft(this);
		
		mSearchLayout = (View) findViewById(R.id.menu_bar_search_btn_layout);
		mSearchBar = (EditTextWithDel) findViewById(R.id.search_position_edit);
		
		mHotCityView = (TextView) findViewById(R.id.hot_city_text);
		
		// 测试堆大小
		ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		int heapSize = manager.getMemoryClass();
		
		Log.d(TAG, "heap size : " + heapSize);
		
		mGridList = new ArrayList<Profession>();
		initGridList();
		mGridView = (NestedGridView) findViewById(R.id.search_industries);
		mSearchPreviewAdapter = new SearchPositionAdapter(this, mGridList, R.layout.item_search_preview);
		mGridView.setAdapter(mSearchPreviewAdapter);
		
		mSuggestedList = new ArrayList<KeyWords>();
		mSuggestedGridView = (NestedGridView) findViewById(R.id.suggested_position);
		mSuggestedPositionAdapter = new SuggestedPositionAdapter(this, mSuggestedList, R.layout.item_suggested_position);
		mSuggestedGridView.setAdapter(mSuggestedPositionAdapter);
		
		// 获取热门职位
		mPositionEventLogic.getSuggestedPosition();
		
		setUIListeners();
	}
	
	private void initGridList() {
		mGridList.add(new Profession("position-10100000000", "技术"));
		mGridList.add(new Profession("position-10200000000", "产品"));
		mGridList.add(new Profession("position-10300000000", "设计"));
		mGridList.add(new Profession("position-10400000000", "运营"));
		mGridList.add(new Profession("position-10500000000", "市场与销售"));
		mGridList.add(new Profession("position-10600000000", "职能"));
	}
	
	private void setUIListeners() {
		mGridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(SearchPositionPreviewActivity.this, SearchPositionActivity.class);
				intent.putExtra("profession", (Profession) parent.getItemAtPosition(position));
				startActivity(intent);
			}
		});
		
		mSuggestedGridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(SearchPositionPreviewActivity.this, SearchPositionActivity.class);
				intent.putExtra("key", ((KeyWords) parent.getItemAtPosition(position)).getName());
				startActivity(intent);
			}
		});
		
		// 搜索
		mSearchLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if (StringUtils.isNotEmpty(mSearchBar.getText().toString())) {
					Intent intent = new Intent(SearchPositionPreviewActivity.this, SearchPositionActivity.class);
					intent.putExtra("key", mSearchBar.getText().toString());
					startActivity(intent);
				}
			}
		});
		
		mSearchBar.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				switch (actionId) {
					case EditorInfo.IME_ACTION_SEARCH:
						if (StringUtils.isNotEmpty(mSearchBar.getText().toString())) {
							Intent intent = new Intent(SearchPositionPreviewActivity.this, SearchPositionActivity.class);
							intent.putExtra("key", mSearchBar.getText().toString());
							startActivity(intent);
						}
						break;
				}
				return false;
			}
		});
		
	}

	@Override
	public void onEventMainThread(Message msg) {
		
		if (msg.obj instanceof InfoResult) {
			InfoResult infoResult = (InfoResult) msg.obj;
			
			switch (msg.what) {
				case R.id.get_suggested_position:
					Log.d(TAG, infoResult.getResult());
					JSONObject positions = (JSONObject) JSONObject.parse(infoResult.getResult());
					JSONArray mPositionArray = positions.getJSONArray(GlobalConstants.JSON_POSITIONS);
					
					for (int i = 0; i < mPositionArray.size(); i++) {
						mSuggestedList.add(new KeyWords(mPositionArray.getJSONObject(i).getString(GlobalConstants.JSON_ID), 
								mPositionArray.getJSONObject(i).getString(GlobalConstants.JSON_NAME)));
					}
					
					mHotCityView.setVisibility(View.VISIBLE);
					
					mSuggestedPositionAdapter.notifyDataSetChanged();
					
					break;
				default:
					break;
			}
		}
	}
	
	
	class SearchPositionAdapter extends CommonAdapter<Profession> {

		public SearchPositionAdapter(Context mContext, List<Profession> mDatas, int mItemLayoutId) {
			super(mContext, mDatas, mItemLayoutId);
			
		}

		@Override
		public void convert(ViewHolder viewHolder, Profession item, int position) {
			viewHolder.setText(R.id.content, item.getName());
		}
	}
	
	
	
}
