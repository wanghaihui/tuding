package com.xiaobukuaipao.vzhi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.content.Intent;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.umeng.analytics.MobclickAgent;
import com.xiaobukuaipao.vzhi.adapter.ContactsSortAdapter;
import com.xiaobukuaipao.vzhi.domain.social.ContactsSortModel;
import com.xiaobukuaipao.vzhi.event.InfoResult;
import com.xiaobukuaipao.vzhi.util.CharacterParser;
import com.xiaobukuaipao.vzhi.util.GlobalConstants;
import com.xiaobukuaipao.vzhi.util.JsonParser;
import com.xiaobukuaipao.vzhi.util.PinyinComparator;
import com.xiaobukuaipao.vzhi.util.VToast;
import com.xiaobukuaipao.vzhi.widget.A5EditText;
import com.xiaobukuaipao.vzhi.widget.SideBar;
import com.xiaobukuaipao.vzhi.wrap.SocialWrapActivity;

/**
 * 联系人列表<br>
 * 联系人列表的每次进来需要刷新的操作太不友好了
 * 
 * 所以:
 * 1.联系人信息保存在本地,对于解除好友操作和增加好友操作做统一的数据库管理
 * 2.如果联系人有所更改都会直接显示出来,网络拉取数据有所改变才会刷新联系人列表
 * @since 2015年01月05日20:28:06
 */
public class ContactsActivity extends SocialWrapActivity implements OnClickListener,
		OnItemClickListener {

	/**
	 * 对联系排序的列表
	 */
	private ListView mSortListView;
	/**
	 * 侧边栏的拖动栏
	 */
	private SideBar mSideBar;
	/**
	 * 拖动侧边栏提示的文字信息
	 */
	private TextView mLetterPopup;
	/**
	 * 搜索框
	 */
	private A5EditText mSearchEditText;
	/**
	 * 字符解析器,将字符解析成为拼音
	 */
	private CharacterParser characterParser;
	/**
	 * 拼音比较器,通过比较器排序
	 */
	private PinyinComparator pinyinComparator;
	/**
	 * 联系人适配器
	 */
	private ContactsSortAdapter mAdapter;
	/**
	 * 联系人排序过后的容器
	 */
	private List<ContactsSortModel> mNameList;
	/**
	 * 陌生消息提示的数量
	 */
	private TextView mStrangerNum;
	/**
	 * 拉取的数据预先保存的格式容器
	 */
	private List<Map<String,Object>> mDatas;

	public void initUIAndData() {
		setContentView(R.layout.activity_contacts);
		
		setHeaderMenuByCenterTitle(R.string.personal_contacts);
		setHeaderMenuByLeft(this);
		//使搜索框弹出软键盘不再挤压布局
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		//初始化陌生人消息数量的UI
		mStrangerNum = (TextView) findViewById(R.id.stranger_num);
		mStrangerNum.setVisibility(View.GONE);
		
		mSortListView = (ListView) findViewById(R.id.contacts_list);
		characterParser = CharacterParser.getInstance();
		pinyinComparator = new PinyinComparator();

		mSideBar = (SideBar) findViewById(R.id.sidrbar);
		mLetterPopup = (TextView) findViewById(R.id.dialog);
		
		// 设置Pop Dialog
		mSideBar.setTextDialog(mLetterPopup);
		mNameList = new ArrayList<ContactsSortModel>();
		mAdapter = new ContactsSortAdapter(this, mNameList,R.layout.activity_contacts_item);
		mSortListView.setAdapter(mAdapter);

		mSearchEditText = (A5EditText) findViewById(R.id.filter_edit);
		mSearchEditText.setFocusableInTouchMode(true);
		mSearchEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				filterData(s.toString());
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				
			}
		});

		findViewById(R.id.stranger_layout).setOnClickListener(this);
		mSideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {

			@Override
			public void onTouchingLetterChanged(String s) {
				int position = mAdapter.getPositionForSection(s.charAt(0));
				if (position != -1) {
					mSortListView.setSelection(position);
				}
			}
		});
		
		mSortListView.setOnItemClickListener(this);
		mDatas = new ArrayList<Map<String,Object>>();
	}
	@Override
	protected void onResume() {
		//在resume里拉取数据的好处是,每次页面被从后台恢复都会加载数据,而大部分对本页面有影响的操作都会将本页面压到后台
		mSocialEventLogic.getBuddyList();
		mSocialEventLogic.getStrangerCount();
		super.onResume();
	}
	
	/**
	 * 将xml数据转换成List SortModel对象
	 * 
	 * @param data
	 * @return
	 */
	private List<ContactsSortModel> filledData(List<Map<String,Object>> data) {
		List<ContactsSortModel> mSortList = new ArrayList<ContactsSortModel>();
		for (int i = 0; i < data.size(); i++) {
			ContactsSortModel sortModel = new ContactsSortModel();
			sortModel.setName((String) data.get(i).get("name"));
			String pinyin = characterParser.getSelling((String) data.get(i).get("name"));
			
			String sortString = pinyin.substring(0, 1).toUpperCase(Locale.getDefault());
			
			if (sortString.matches("[A-Z]")) {
				sortModel.setSortLetters(sortString.toUpperCase(Locale.getDefault()));
			} else {
				sortModel.setSortLetters("#");
			}
			
			sortModel.setId((Integer) data.get(i).get("id"));
			sortModel.setAvatar((String) data.get(i).get("avatar"));
			sortModel.setCity((String) data.get(i).get("city"));
			sortModel.setGender((Integer) data.get(i).get("gender"));
			sortModel.setCorp((String) data.get(i).get("corp"));
			sortModel.setPosition((String) data.get(i).get("position"));
			
			mSortList.add(sortModel);
		}
		return mSortList;

	}

	/**
	 * 填充数据,更新列表
	 * 
	 * @param filterStr
	 */
	private void filterData(String filterStr) {
		List<ContactsSortModel> filterDateList = new ArrayList<ContactsSortModel>();

		if (TextUtils.isEmpty(filterStr)) {
			filterDateList = mNameList;
		} else {
			filterDateList.clear();
			for (ContactsSortModel sortModel : mNameList) {
				String name = sortModel.getName();
				if (name.indexOf(filterStr.toString()) != -1|| characterParser.getSelling(name).startsWith(filterStr.toString())) {
					filterDateList.add(sortModel);
				}
			}
		}
		
		Collections.sort(filterDateList, pinyinComparator);
		mAdapter.updateListView(filterDateList);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.stranger_layout:
				MobclickAgent.onEvent(this,"msr");
				Intent intent = new Intent(this,ContactsStrangersActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				break;
			default:
				break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Intent intent = new Intent(this, PersonalShowPageActivity.class);
		intent.putExtra(GlobalConstants.UID, String.valueOf(mNameList.get(position).getId()));
		startActivity(intent);
	}

	@Override
	public void onEventMainThread(Message msg) {
		if(msg.obj instanceof InfoResult){
			InfoResult infoResult = (InfoResult) msg.obj;
			switch (msg.what) {
			case R.id.social_buddy_list_get:
				JSONObject jsonObject = (JSONObject) JSONObject.parse(infoResult.getResult());
				if(jsonObject != null){
					//获得了数字排序的 map 
					JSONObject buddylist = jsonObject.getJSONObject(GlobalConstants.JSON_BUDDYLIST);
					Map<String, Object> map = JsonParser.parseJsonObject(buddylist);
					mDatas.clear();
					for(Map.Entry<String, Object> entry : map.entrySet()){
						if(entry.getValue() instanceof JSONObject){
						}
						if(entry.getValue() instanceof JSONArray){
							mDatas.addAll(JsonParser.parseJsonArray((JSONArray) entry.getValue()));
						}
					}
					mNameList.clear();
					mNameList.addAll(filledData(mDatas));
					mAdapter.notifyDataSetChanged();
					filterData("");
				}
				break;
			case R.id.social_stranger_news_count:
				JSONObject countJson = (JSONObject) JSONObject.parse(infoResult.getResult());
				if(countJson != null){
					Integer count = countJson.getInteger(GlobalConstants.JSON_UNREADCOUNT);
					if(count != null && count > 0){
						if(count > 99 ){
							count = 99;
						}
						mStrangerNum.setText(String.valueOf(count));
						mStrangerNum.setVisibility(View.VISIBLE);
					}else{
						mStrangerNum.setVisibility(View.GONE);
					}
				}
				break;
			default:
				break;
			}
			
		}else if(msg.obj instanceof VolleyError){
			VToast.show(this, getString(R.string.general_tips_network_unknow));
		}
		
	}
}
