package com.xiaobukuaipao.vzhi.profile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xiaobukuaipao.vzhi.PersonalEditPageActivity;
import com.xiaobukuaipao.vzhi.R;
import com.xiaobukuaipao.vzhi.adapter.CommonAdapter;
import com.xiaobukuaipao.vzhi.adapter.SuggestAdapter;
import com.xiaobukuaipao.vzhi.adapter.ViewHolder;
import com.xiaobukuaipao.vzhi.domain.user.Suggest;
import com.xiaobukuaipao.vzhi.event.InfoResult;
import com.xiaobukuaipao.vzhi.util.ActivityQueue;
import com.xiaobukuaipao.vzhi.util.GlobalConstants;
import com.xiaobukuaipao.vzhi.util.RandomUtils;
import com.xiaobukuaipao.vzhi.util.VToast;
import com.xiaobukuaipao.vzhi.view.NestedListView;
import com.xiaobukuaipao.vzhi.widget.A5EditText;
import com.xiaobukuaipao.vzhi.wrap.ProfileWrapActivity;

public class ProfileSkillActivity extends ProfileWrapActivity implements OnClickListener{
	public static final String TAG = ProfileSkillActivity.class.getSimpleName();
	
	private Class<?> next;

	public static final int GRIDVIEW_DELETE = 3;
	private A5EditText mAddTagEditText;
	private GridView mTagGridView;
	private List<Suggest> mChooseTags;
	private List<String> mChoosedTags;
	private TagAdapter mTagAdapter;
	private SuggestAdapter mSuggestAdapter;
	
	private Button mAddBtn;
	private NestedListView mProfileTag;
	private boolean mProfileTagEdit;
	
	private int seq;
	
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case GRIDVIEW_DELETE:
				mChoosedTags.remove(msg.arg1);
				mTagAdapter.notifyDataSetChanged();
				refreshMenuRight();
				 
				break;
			default:
				break;
			}
		}
	};

	private TextWatcher mSuggestTextWatcher = new TextWatcher() {
	      @Override
	      public void beforeTextChanged(CharSequence s, int start, int count, int after) {
	          
	      }
		   @Override
		   public void onTextChanged(CharSequence s, int start, int before, int count) {
			   int maxLength = 16;//限制长度为16
			   
			   if(s.length() > maxLength){
				   mAddTagEditText.setText(s.subSequence(0, maxLength));
				   mAddTagEditText.setSelection(maxLength);
			   }else{
				   getTechTag(s);
			   }
			   
			   mAddBtn.setEnabled(s.length() > 0);
		   }
		   
			private void getTechTag(CharSequence s) {
				seq = RandomUtils.getRandom(Integer.MAX_VALUE);
				mProfileEventLogic.getTechTag(s.toString(), seq);
			}
			
		   @Override
		   public void afterTextChanged(Editable s) {
			   
		   }
	 };

	@Override
	public void initUIAndData() {
		mProfileTagEdit = getIntent().getBooleanExtra("personal_profile_edit", false);

		setContentView(R.layout.activity_profile_add_tag);
		setHeaderMenuByLeft(this);
        setHeaderMenuByCenterTitle(R.string.personal_tech_tag);
        setHeaderMenuByRight(mProfileTagEdit ? R.string.general_save : R.string.general_tips_jump).setOnClickListener(this);
        
        mAddTagEditText = (A5EditText) findViewById(R.id.profile_add_tag_edittext);
        mAddTagEditText.addTextChangedListener(mSuggestTextWatcher);
		
		mChooseTags = new ArrayList<Suggest>();
		mChoosedTags = new ArrayList<String>();
		
		mTagAdapter = new TagAdapter(this, mChoosedTags, R.layout.item_tag);
		
		mTagGridView = (GridView) findViewById(R.id.profile_add_tag_gridview);
		mTagGridView.setAdapter(mTagAdapter);
		
		mAddBtn = (Button) findViewById(R.id.profile_add_tag_btn);
		mAddBtn.setEnabled(false);
		mAddBtn.setOnClickListener(this);
		
		mSuggestAdapter = new SuggestAdapter(this, mChooseTags, R.layout.item_suggest_selection);
		
		mProfileTag = (NestedListView) findViewById(R.id.profile_tag_list);
		mProfileTag.setAdapter(mSuggestAdapter);
		mProfileTag.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
				mAddTagEditText.setText(mChooseTags.get(position).getName());
			}
		});
		//获取已经选择的热门技能
		mProfileEventLogic.getMyTag();
	}
	

	
	@Override
	public void onEventMainThread(Message msg) {
		if (msg.obj instanceof InfoResult) {
			InfoResult infoResult = (InfoResult) msg.obj;
			if(seq == msg.what){ //获取最后一次确定的suggest请求
				// 由于要多次用到，所以每次进来时都要清空一下
				mChooseTags.clear();
				JSONObject jsonObject = (JSONObject) JSONObject.parse(infoResult.getResult());
				if(jsonObject != null){
					JSONArray jsonArray = jsonObject.getJSONArray(GlobalConstants.JSON_SKILLS);
					for(int i=0; i < jsonArray.size(); i++) {
						Suggest suggest = new Suggest();
						suggest.setId(jsonArray.getJSONObject(i).getString(GlobalConstants.JSON_ID));
						suggest.setName(jsonArray.getJSONObject(i).getString(GlobalConstants.JSON_NAME));
						mChooseTags.add(suggest);
					}
					mSuggestAdapter.notifyDataSetChanged();
				}
				refreshMenuRight();
				          
			}
			
			switch (msg.what) {
				case R.id.profile_mytag_set:
					if(infoResult.getMessage().getStatus() == 0){
						if (mProfileTagEdit) {
							Intent intent = new Intent(ProfileSkillActivity.this,PersonalEditPageActivity.class);
							startActivity(intent);
						} else {
							Intent intent = getIntent();
							if (!ActivityQueue.hasNext(ProfileSkillActivity.class)){
								intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							}
							next = ActivityQueue.next(ProfileSkillActivity.class);
							intent.setClass(this, next);
							startActivity(intent);
						}
					}
					VToast.show(this, getString(R.string.general_tips_save_success));
					break;
				case R.id.profile_mytag_get:
					JSONObject jsonO = (JSONObject) JSONObject.parse(infoResult.getResult());
					if(jsonO != null){
						JSONArray jsonA = jsonO.getJSONArray(GlobalConstants.JSON_SKILLS);
						if(jsonA != null){
							for(int i=0; i < jsonA.size(); i++) {
								HashMap<String, String> map = new HashMap<String, String>();
								map.put(jsonA.getJSONObject(i).getString(GlobalConstants.JSON_ID), jsonA.getJSONObject(i).getString(GlobalConstants.JSON_NAME));
								mChoosedTags.add(jsonA.getJSONObject(i).getString(GlobalConstants.JSON_NAME));
							}
							
							refreshMenuRight();
							mTagAdapter.notifyDataSetChanged();
						}
					}
					break;
			}
		}
	}
	
	public class TagAdapter extends CommonAdapter<String> {
		
		
		public TagAdapter(Context context, List<String> mJobInfoList, int itemLayoutId) {
			super(context, mJobInfoList, itemLayoutId);
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final ViewHolder viewHolder = getViewHolder(position, convertView,
					parent);
			convert(viewHolder, getItem(position), position);
			return viewHolder.getConvertView();
		}
		// 得到ViewHolder的唯一方式
		private ViewHolder getViewHolder(int position, View convertView, ViewGroup parent) {
			convertView = null;
			return ViewHolder.getViewHolder(mContext, convertView, parent,mItemLayoutId, position);
		}
		@Override
		public void convert(final ViewHolder viewHolder, final String item, final int position) {
			viewHolder.setText(R.id.job_position,  item);
			final ImageView mImageView = viewHolder.getView(R.id.job_position_del);
			
			// 设置ImageView的点击事件
			// 特别注意：如果删除一个GridView中的项，要考虑到如果当前的ListView中也存在这一项，那就需要将ListView的这一项由check状态变为uncheck状态
			mImageView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v){
					Message message = mHandler.obtainMessage();
					message.what = GRIDVIEW_DELETE;
					message.arg1 = position;
					mHandler.sendMessage(message);
					Log.i(TAG, "position" + position);
				}
			});
		}
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.profile_add_tag_btn:
			String tag = mAddTagEditText.getText().toString();
			if (!mChoosedTags.contains(tag)){// && mChoosedTags.size() <= 10){
				mChoosedTags.add(tag);
				refreshMenuRight();
				mTagAdapter.notifyDataSetChanged();
			}
			mAddTagEditText.setText("");
			break;
		case R.id.menu_bar_right:
			mProfileEventLogic.setMyTag(mChoosedTags.toArray(new String[]{}));
			break;
		default:
			break;
		}
	}
	
	private void refreshMenuRight() {
		setHeaderMenuByRight(mProfileTagEdit ? R.string.general_save : (mChoosedTags.isEmpty() ? R.string.general_tips_jump : R.string.general_tips_next)).setOnClickListener(this);
	};
}
