package com.xiaobukuaipao.vzhi.fragment;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
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
import com.xiaobukuaipao.vzhi.R;
import com.xiaobukuaipao.vzhi.adapter.CommonAdapter;
import com.xiaobukuaipao.vzhi.adapter.SuggestAdapter;
import com.xiaobukuaipao.vzhi.adapter.ViewHolder;
import com.xiaobukuaipao.vzhi.domain.user.Suggest;
import com.xiaobukuaipao.vzhi.event.InfoResult;
import com.xiaobukuaipao.vzhi.util.GlobalConstants;
import com.xiaobukuaipao.vzhi.util.RandomUtils;
import com.xiaobukuaipao.vzhi.util.StringUtils;
import com.xiaobukuaipao.vzhi.view.NestedListView;
import com.xiaobukuaipao.vzhi.widget.A5EditText;
/**
 *	公司福利
 *
 * @since 2015年01月13日17:32:29
 */
public class CompanyBenefitsFragment extends CallBackFragment implements OnClickListener {
    private Bundle mBundle;
    private View mParentView;
    
    public static final int GRIDVIEW_DELETE = 3;
    
    private A5EditText mAddTagEditText;
    
    private GridView mBenefitsGridView;
    
    private List<Suggest> mBenifits;
	private List<String> mChoosedBenifits;
	private BenifitsAdapter mBenefitsAdapter;
	private SuggestAdapter mSuggestAdapter;
	private NestedListView mBenefitsSuggests;
	private Button mAddBtn;
	private int seq;
	
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case GRIDVIEW_DELETE:
				mChoosedBenifits.remove(msg.arg1);
				mBenefitsAdapter.notifyDataSetChanged();
				break;
				
			default:
				break;
			}
		};
	};
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	mBundle = getArguments();
    	mParentView =  inflater.inflate(R.layout.fragment_company_benefits, container, false);
    	mBenifits = new ArrayList<Suggest>();
    	mChoosedBenifits = new ArrayList<String>();
    	Bundle bundle = getArguments();
    	String benifits = bundle.getString(GlobalConstants.NAME);
    	if(StringUtils.isNotEmpty(benifits)){
        	for(String s : benifits.split(",")){
        		mChoosedBenifits.add(s);
        	}
    	}
    	return mParentView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHeaderMenuByLeft();
        setHeaderMenuByCenterTitle(R.string.corp_edit_benifits);
		setHeaderMenuByRight(R.string.general_tips_save).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						
						if (!mChoosedBenifits.isEmpty()) {
							StringBuilder benifits = new StringBuilder();
							Iterator<String> iterator = mChoosedBenifits.iterator();
							for (; iterator.hasNext(); benifits.append(",")) {
								benifits.append(iterator.next());
							}
							//处理下逗号
							mBundle.putString(GlobalConstants.NAME, benifits.substring(0, benifits.length() - 1));
							onBackRequest.onBackData(mBundle);
						}
					}
				});
		
        mAddTagEditText = (A5EditText) mParentView.findViewById(R.id.profile_add_tag_edittext);
        mBenefitsGridView = (GridView) mParentView.findViewById(R.id.profile_add_tag_gridview);
		
		
		mBenefitsAdapter = new BenifitsAdapter(getActivity(), mChoosedBenifits, R.layout.item_tag);
		mBenefitsGridView.setAdapter(mBenefitsAdapter);
		
		mAddTagEditText.addTextChangedListener(mSuggestTextWatcher);
		
		mAddBtn = (Button) mParentView.findViewById(R.id.profile_add_tag_btn);
		mAddBtn.setEnabled(false);
		mAddBtn.setOnClickListener(this);
		
		mSuggestAdapter = new SuggestAdapter(getActivity(), mBenifits, R.layout.item_suggest_selection);
		
		mBenefitsSuggests = (NestedListView) mParentView.findViewById(R.id.profile_tag_list);
		mBenefitsSuggests.setAdapter(mSuggestAdapter);
		mBenefitsSuggests.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
				mAddTagEditText.setText(mBenifits.get(position).getName());
			}
		});
		
		
		getCorpBenefits("");	
    }
    
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
				   getCorpBenefits(s);				   
			   }
			   
			   mAddBtn.setEnabled(s.length() > 0);
		   }

		   @Override
		   public void afterTextChanged(Editable s) {
			   
		   }
	 };
    
	private void getCorpBenefits(CharSequence s) {
		seq = RandomUtils.getRandom(Integer.MAX_VALUE);
		mRegisterEventLogic.getCorpBenifits(s.toString(), seq);
	}
    
    public class BenifitsAdapter extends CommonAdapter<String> {
		
		public BenifitsAdapter(Context context, List<String> mJobInfoList, int itemLayoutId) {
			super(context, mJobInfoList, itemLayoutId);
		}

		@Override
		public void convert(final ViewHolder viewHolder, final String item, final int position) {
			viewHolder.setText(R.id.job_position, item);
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
				}
			});
		}
	}
    
	@Override
	public void onEventMainThread(Message msg) {
		if (msg.obj instanceof InfoResult) {
			InfoResult infoResult = (InfoResult) msg.obj;
			if(seq == msg.what){
				// 由于要多次用到，所以每次进来时都要清空一下
				mBenifits.clear();
				JSONObject jsonObject = (JSONObject) JSONObject.parse(infoResult.getResult());
				if(jsonObject != null){
					JSONArray jsonArray = jsonObject.getJSONArray(GlobalConstants.JSON_BENEFITS);
					if(jsonArray != null){
						for(int i=0; i < jsonArray.size(); i++) {
							Suggest suggest = new Suggest();
							suggest.setId(jsonArray.getJSONObject(i).getString(GlobalConstants.JSON_ID));
							suggest.setName(jsonArray.getJSONObject(i).getString(GlobalConstants.JSON_NAME));
							mBenifits.add(suggest);
						}
						mSuggestAdapter.notifyDataSetChanged();
					}
				}
			}
		}
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.profile_add_tag_btn:
			String tag = mAddTagEditText.getText().toString();
			if (!mChoosedBenifits.contains(tag)){
				mChoosedBenifits.add(tag);
			}
			mAddTagEditText.setText("");
			mBenefitsAdapter.notifyDataSetChanged();
			break;
		default:
			break;
		}
	}
}
