package com.xiaobukuaipao.vzhi.register;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.xiaobukuaipao.vzhi.AppActivityManager;
import com.xiaobukuaipao.vzhi.BaseFragmentActivity;
import com.xiaobukuaipao.vzhi.R;
import com.xiaobukuaipao.vzhi.adapter.SortAdapter;
import com.xiaobukuaipao.vzhi.domain.SortModel;
import com.xiaobukuaipao.vzhi.event.GPSLocationEventLogic;
import com.xiaobukuaipao.vzhi.event.InfoResult;
import com.xiaobukuaipao.vzhi.util.CharacterParser;
import com.xiaobukuaipao.vzhi.util.CityPullParser;
import com.xiaobukuaipao.vzhi.util.Logcat;
import com.xiaobukuaipao.vzhi.util.PinyinComparator;
import com.xiaobukuaipao.vzhi.view.LinesTextView;
import com.xiaobukuaipao.vzhi.widget.A5EditText;
import com.xiaobukuaipao.vzhi.widget.SideBar;

/**
 * 获取城市定位
 * 
 * 采用网络定位,通过网络获取location,然后定位城市以及更详细的区域
 * 
 * @author hongxin.bai
 * @since 2015年02月09日14:40:28
 * 
 */
public class RegisterResidentSearchActivity extends BaseFragmentActivity implements LocationListener {

	private static final String TAG = RegisterResidentSearchActivity.class.getSimpleName();

	public static final String BACKDATA_KEY = "back_key";
	/**
	 * 城市列表
	 */
	private ListView mSortListView;
	/**
	 * 定位管理器
	 */
	private LocationManager mLocationManager;
	/**
	 * 我定位的城市
	 */
	private TextView myLocationText;

	private Location location;
	
	private ProgressBar mProgressBar;
	/**
	 * 字符解析器
	 */
	private CharacterParser characterParser;

	private PinyinComparator pinyinComparator;

	private SideBar mSideBar;

	private TextView mTextDialog;

	private SortAdapter adapter;

	private A5EditText mA5EditText;

	private List<SortModel> cityList;
	/**
	 * XML解析
	 */
	private CityPullParser xmlParser;
	private List<String> xmlCityList;
	private LinesTextView mHotCities;
	private GPSLocationEventLogic mGPSLocationLogic;
	private String[] hotCities;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register_resident);
		setHeaderMenuByLeft(this);
		setHeaderMenuByCenterTitle(R.string.register_place);
		onClickListenerBySaveDataAndRedirectActivity(R.id.city_loc_btn);
		
		myLocationText = (TextView) findViewById(R.id.city_loc_btn);
		mProgressBar = (ProgressBar) findViewById(R.id.city_loc_button_progress);
		mHotCities = (LinesTextView) findViewById(R.id.hot_cities);

		
		myLocationText.setText(R.string.location_loading);
		
		mGPSLocationLogic = new GPSLocationEventLogic();
		mGPSLocationLogic.register(this);

		// 访问系统的定位服务
		mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		List<String> allProviders = mLocationManager.getAllProviders();
        if (allProviders != null){
    		for (int i = 0; i < allProviders.size(); i++) {
    			if(mLocationManager.isProviderEnabled(allProviders.get(i))){
    				mLocationManager.requestLocationUpdates(allProviders.get(i), 1000, (float) 1000.0, this);
    				location = mLocationManager.getLastKnownLocation(allProviders.get(i));
    			}
    			Logcat.d("@@@", "location providers:" + allProviders.get(i));
    		}
        }
		
		if(location == null){
			Logcat.d("@@@", "location is null");
        	myLocationText.setText(R.string.location_failed);
			mProgressBar.setVisibility(View.GONE);
		} else{
			Logcat.d("@@@", "location not null");
			updateLocationPlace(location, myLocationText);
		}
		

		onClickListenerBySaveDataAndRedirectActivity(myLocationText.getId());

		// 添加热门城市
		hotCities = getResources().getStringArray(R.array.residents);
		mHotCities.setBackgroundResource(R.drawable.bg_corners_button_blue);
		mHotCities.setTextPadding(15, 8, 15, 8);
		int gridItemHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics());

		mHotCities.setTextMargin(gridItemHeight, gridItemHeight);
		mHotCities.setTextColor(getResources().getColor(R.color.white));
		mHotCities.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
		mHotCities.setOffset(0);
		mHotCities.setLinesText(Arrays.asList(hotCities));

		initViews();
	}


	// 初始化Views
	private void initViews() {
		characterParser = CharacterParser.getInstance();

		pinyinComparator = new PinyinComparator();

		mSideBar = (SideBar) findViewById(R.id.sidrbar);
		mTextDialog = (TextView) findViewById(R.id.dialog);
		// 设置Pop Dialog
		mSideBar.setTextDialog(mTextDialog);

		mSideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {

			@Override
			public void onTouchingLetterChanged(String s) {
				int position = adapter.getPositionForSection(s.charAt(0));
				if (position != -1) {
					mSortListView.setSelection(position);
				}
			}
		});

		mSortListView = (ListView) findViewById(R.id.country_lvcountry);
		mSortListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String clickName = ((SortModel) adapter.getItem(position)).getName();
				mA5EditText.setText(clickName);
				confirmNextAction();
			}
		});

		// 处理XML信息
		try {
			InputStream is = getResources().openRawResource(R.raw.city);

			xmlParser = new CityPullParser(); // 创建CityPullParser实例
			xmlCityList = xmlParser.parse(is); // 解析输入流

		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}

		String[] strArr = new String[xmlCityList.size()];
		cityList = filledData(xmlCityList.toArray(strArr));

		Collections.sort(cityList, pinyinComparator);
		adapter = new SortAdapter(this, cityList);
		mSortListView.setAdapter(adapter);

		mA5EditText = (A5EditText) findViewById(R.id.filter_edit);

		mA5EditText.addTextChangedListener(new TextWatcher() {

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

		setUIListener();
	}

	private void setUIListener() {
		mHotCities.setItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent it = new Intent();
				it.putExtra(BACKDATA_KEY, hotCities[position]);
				setResult(RESULT_OK, it);
				AppActivityManager.getInstance().finishActivity(
						RegisterResidentSearchActivity.this);
			}
		});

		mSortListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent it = new Intent();
				it.putExtra(BACKDATA_KEY, ((SortModel) parent
						.getItemAtPosition(position)).getName());
				setResult(Activity.RESULT_OK, it);
				AppActivityManager.getInstance().finishActivity(
						RegisterResidentSearchActivity.this);
			}

		});
	}
	
	/**
	 * 将xml数据转换成List SortModel对象
	 * 
	 * @param date
	 * @return
	 */
	private List<SortModel> filledData(String[] date) {
		List<SortModel> mSortList = new ArrayList<SortModel>();

		for (int i = 0; i < date.length; i++) {
			SortModel sortModel = new SortModel();
			sortModel.setName(date[i]);
			String pinyin = characterParser.getSelling(date[i]);
			String sortString = pinyin.substring(0, 1).toUpperCase();

			if (sortString.matches("[A-Z]")) {
				sortModel.setSortLetters(sortString.toUpperCase());
			} else {
				sortModel.setSortLetters("#");
			}

			mSortList.add(sortModel);
		}
		return mSortList;

	}

	/**
	 * @param filterStr
	 */
	private void filterData(String filterStr) {
		List<SortModel> filterDateList = new ArrayList<SortModel>();

		if (TextUtils.isEmpty(filterStr)) {
			filterDateList = cityList;
		} else {
			filterDateList.clear();
			for (SortModel sortModel : cityList) {
				String name = sortModel.getName();
				if (name.indexOf(filterStr.toString()) != -1
						|| characterParser.getSelling(name).startsWith(filterStr.toString())) {
					filterDateList.add(sortModel);
				}
			}
		}

		Collections.sort(filterDateList, pinyinComparator);
		adapter.updateListView(filterDateList);
	}

	private void updateLocationPlace(Location location,
			TextView myLocationButton) {
		// double geoLatitude = location.getLatitude() * 1E6;
		// double geoLongitude = location.getLongitude() * 1E6;
		double geoLatitude = location.getLatitude();
		double geoLongitude = location.getLongitude();

		Logcat.d("@@@","location:" + location.getLatitude() + "  "+ location.getLongitude());

		mGPSLocationLogic.getPlaceByPostion(String.valueOf(geoLatitude),
				String.valueOf(geoLongitude));
	}

	/**
	 * EventBus订阅者事件通知的函数, UI线程
	 * 
	 * @param msg
	 */
	public void onEventMainThread(Message msg) {
		switch (msg.what) {
		case R.id.gpsLocationHttp:
			if (msg.obj instanceof InfoResult) {
				// 访问网络请求成功，才显示整体的View
				InfoResult infoResult = (InfoResult) msg.obj;
				// 将返回的JSON数据展示出来
				JSONObject mJSONResult = (JSONObject) JSONObject.parse(infoResult.getResult());
				
				String city = mJSONResult.getJSONObject("result").getJSONObject("addressComponent").get("city").toString();
				if (city.contains("市")) {
					city = city.replace("市", "");
				}
				mProgressBar.setVisibility(View.GONE);
				myLocationText.setText(city);
				myLocationText.setBackgroundResource(R.drawable.bg_corners_button_blue);
				myLocationText.setPadding(15, 8, 15, 8);
				myLocationText.setTextColor(Color.WHITE);
				myLocationText.setVisibility(View.VISIBLE);
				myLocationText.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View view) {
						Intent it = new Intent();
						it.putExtra(BACKDATA_KEY, myLocationText.getText().toString());
						setResult(Activity.RESULT_OK, it);
						AppActivityManager.getInstance().finishActivity(
								RegisterResidentSearchActivity.this);
					}
				});
			} else if (msg.obj instanceof VolleyError) {
				// 可提示网络错误，具体类型有TimeoutError ServerError
				// Logger.w("TestActivity", (VolleyError)msg.obj);
			}
			break;
		default:
			break;
		}
	}
     
	
	public void onDestroy() {
		super.onDestroy();
		mGPSLocationLogic.unregister(this);
		mLocationManager.removeUpdates(this);
	}

	
	
	@Override
	public void onLocationChanged(Location location) {
		Logcat.d("@@@", "location: onLocationChanged" + location);
		this.location = location;
		if (location != null) {
			updateLocationPlace(location, myLocationText);
			Logcat.d("@@@", "location not null");
		}
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		Logcat.d("@@@", "location: onStatusChanged" + provider);
	}

	@Override
	public void onProviderEnabled(String provider) {
		Logcat.d("@@@", "location: onProviderEnabled" + provider);
	}

	@Override
	public void onProviderDisabled(String provider) {
		Logcat.d("@@@", "location: onProviderDisabled" +provider );
	}

}
