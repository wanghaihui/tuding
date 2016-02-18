package com.xiaobukuaipao.vzhi;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ScrollView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.xiaobukuaipao.vzhi.adapter.CorpPositionsAdapter;
import com.xiaobukuaipao.vzhi.cache.CommonBitmapMemoryCache;
import com.xiaobukuaipao.vzhi.domain.CorpInfo;
import com.xiaobukuaipao.vzhi.domain.JobInfo;
import com.xiaobukuaipao.vzhi.event.InfoResult;
import com.xiaobukuaipao.vzhi.register.ImageClipActivity;
import com.xiaobukuaipao.vzhi.register.ImagePickerActivity;
import com.xiaobukuaipao.vzhi.register.RegisterResidentSearchActivity;
import com.xiaobukuaipao.vzhi.util.GlobalConstants;
import com.xiaobukuaipao.vzhi.util.StringUtils;
import com.xiaobukuaipao.vzhi.util.VToast;
import com.xiaobukuaipao.vzhi.view.LoadingDialog;
import com.xiaobukuaipao.vzhi.view.RoundedNetworkImageView;
import com.xiaobukuaipao.vzhi.widget.FormItemByLineLayout;
import com.xiaobukuaipao.vzhi.widget.FormItemByLineView;
import com.xiaobukuaipao.vzhi.wrap.RecruitWrapActivity;

/**
 * 公司信息编辑页面
 * 
 * @since 2015年01月05日11:24:43
 */
public class CorpInfoEditActivity extends RecruitWrapActivity implements OnItemClickListener, OnClickListener{

	/**
	 * 公司id,由上级页面下发,本页面不存在没有id的可能,所有id都会有服务器端生成并下发过来
	 */
	private String corpId;
	/**
	 * 公司的标志,是服务器下发的url,回传的也是服务器下发的url
	 */
	private RoundedNetworkImageView mCorpLogo;
	/**
	 * 通过id获取的公司基本信息
	 */
	private CorpInfo mCorpInfo;
	/**
	 * Volley提供的请求队列
	 */
	private RequestQueue mQueue;
	private ImageLoader mImageLoader;
	/**
	 * 工作信息的数据容器
	 */
	private List<JobInfo> mDatas;
	private CorpPositionsAdapter positionsAdapter;
	private ScrollView mLayout;
	/**
	 * 公司的表单控件
	 */
	private FormItemByLineView mCorpName;
	private FormItemByLineLayout mCorpSimpName;
	private FormItemByLineView mCorpType;
	private FormItemByLineView mCorpIndustry;
	private FormItemByLineView mCorpScale;
	private FormItemByLineLayout mCorpWebsite;
	private FormItemByLineView mCorpBenifits;
	private FormItemByLineView mCorpLocation;
	private FormItemByLineView mCorpLocationDetail;
	private FormItemByLineView mCorpDesc;
	/**
	 * 可选择的item对话框,包括单选多选
	 */
	private AlertDialog mAlertDialog;
	/**
	 * 公司类型的列表容器
	 */
	private Map<Integer, String> mCorpTypesMap;
	/**
	 * 公司规模的列表容器
	 */
	private Map<Integer, String> mCorpScalesMap;
	/**
	 * 加载等待对话框
	 */
	private LoadingDialog loadingDialog;
	/**
	 * 当前的公司logo
	 */
	private String mCurChooseLogo;
	/**
	 * 当前公司的地址
	 */
	private String mLocationDetail;
	/**
	 * 当前公司的简介
	 */
	private String mDesc;
	// 行业方向
	private HashMap<String, String> mIndustryMap;
	/**
	 * 当前公司的行业id
	 */
	private String mIndustryId;
	/**
	 * 选中的公司类型,默认为-1
	 */
	private int mType = -1;
	/**
	 * 选中的公司规模,默认为-1
	 */
	private int mScale = -1;
	/**
	 * 手动给map排序
	 */
	private Comparator<Integer> comparator = new Comparator<Integer>() {

		@Override
		public int compare(Integer lhs, Integer rhs) {
			
			return lhs > rhs ? -1:1;
		}
	};
	@Override
	public void initUIAndData() {
		setContentView(R.layout.activity_company_edit_info);
		setHeaderMenuByLeft(this);
		setHeaderMenuByCenterTitle(R.string.corp_info);
		setHeaderMenuByRight(R.string.general_tips_save).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//验证表单,并提交数据
				if(StringUtils.isEmpty(mCorpSimpName.getFormContentText())){
					VToast.show(CorpInfoEditActivity.this, getString(R.string.general_tips_fill_tips,mCorpSimpName.getFormLabelText()));
					return;
				}
				if(StringUtils.isEmpty(mCorpType.getFormContentText())){
					VToast.show(CorpInfoEditActivity.this, getString(R.string.general_tips_fill_tips,mCorpType.getFormLabelText()));
					return;
				}

				if(StringUtils.isEmpty(mCorpIndustry.getFormContentText())){
					VToast.show(CorpInfoEditActivity.this, getString(R.string.general_tips_fill_tips,mCorpIndustry.getFormLabelText()));
					return;
				}
				if(StringUtils.isEmpty(mCorpScale.getFormContentText())){
					VToast.show(CorpInfoEditActivity.this, getString(R.string.general_tips_fill_tips,mCorpScale.getFormLabelText()));
					return;
				}
//				if(StringUtils.isEmpty(mCorpBenifits.getFormContentText())){
//					VToast.show(CorpInfoEditActivity.this, getString(R.string.general_tips_fill_tips,mCorpBenifits.getFormLabelText()));
//					return;
//				}
				if(StringUtils.isEmpty(mCorpLocation.getFormContentText())){
					VToast.show(CorpInfoEditActivity.this, getString(R.string.general_tips_fill_tips,mCorpLocation.getFormLabelText()));
					return;
				}
				if(StringUtils.isEmpty(mCorpLocationDetail.getFormContentText())){
					VToast.show(CorpInfoEditActivity.this, getString(R.string.general_tips_fill_tips,mCorpLocationDetail.getFormLabelText()));
					return;
				}
				// 初始化loading对话框
				loadingDialog = new LoadingDialog(CorpInfoEditActivity.this, R.style.loading_dialog);
				loadingDialog.setLoadingTipStr(getString(R.string.general_tips_saving));
				loadingDialog.show();
				
				mRegisterEventLogic.setCorpInfo(corpId, 
						mCorpSimpName.getFormContentText(), 
						mType, 
						mCurChooseLogo, 
						mCorpLocation.getFormContentText(), 
						mLocationDetail,
						mIndustryId, 
						mScale,
						mCorpBenifits.getFormContentText(),
						mCorpWebsite.getFormContentText(),
						mDesc);
			}
		});
		
		// 初始化图片网络访问请求
		mQueue = Volley.newRequestQueue(this);
		mImageLoader = new ImageLoader(mQueue, new CommonBitmapMemoryCache());
		// 获取滚动容器 并滑到顶端
		mLayout = (ScrollView) findViewById(R.id.layout);
		mLayout.smoothScrollTo(0, 0);
		mLayout.setVisibility(View.INVISIBLE);
		
		mCorpLogo = (RoundedNetworkImageView) findViewById(R.id.corp_logo);
		// 获取公司全称并将全称设置为不可编辑
		mCorpName = (FormItemByLineView) findViewById(R.id.corp_name);
		mCorpName.getFormLabel().setTextColor(getResources().getColor(R.color.general_color_999999));
		mCorpName.getFormLabel().setSingleLine(false);
		mCorpName.getFormLabel().setMaxLines(2);
		mCorpName.getFormContent().setVisibility(View.GONE);
		
		//初始化表单
		mCorpSimpName = (FormItemByLineLayout) findViewById(R.id.corp_simple_name);
		mCorpType = (FormItemByLineView) findViewById(R.id.corp_type);
		mCorpScale = (FormItemByLineView) findViewById(R.id.corp_scale);
		mCorpWebsite= (FormItemByLineLayout) findViewById(R.id.corp_website);
		mCorpIndustry = (FormItemByLineView) findViewById(R.id.corp_industry);
		mCorpBenifits = (FormItemByLineView) findViewById(R.id.corp_benifits);
		mCorpLocation = (FormItemByLineView) findViewById(R.id.corp_location);
		mCorpLocationDetail = (FormItemByLineView) findViewById(R.id.corp_location_detail);
		mCorpDesc = (FormItemByLineView) findViewById(R.id.corp_desc);
		
		mCorpLogo.setOnClickListener(this);
		mCorpSimpName.setOnClickListener(this);
		mCorpType.setOnClickListener(this);
		mCorpScale.setOnClickListener(this);
		mCorpWebsite.setOnClickListener(this);
		mCorpIndustry.setOnClickListener(this);
		mCorpBenifits.setOnClickListener(this);
		mCorpLocation.setOnClickListener(this);
		mCorpLocationDetail.setOnClickListener(this);
		mCorpDesc.setOnClickListener(this);
		
		// 初始化容器以及选择对话框
		mCorpTypesMap = new TreeMap<Integer, String>(comparator);
		mCorpScalesMap = new TreeMap<Integer,String>(comparator);
		mAlertDialog = new AlertDialog.Builder(this).create();
		// 初始化Map
		mIndustryMap = new HashMap<String, String>();
		
		mRegisterEventLogic.getCorpTypes();
		mRegisterEventLogic.getCorpScale();
		
		// 获取公司id
		corpId = getIntent().getStringExtra(GlobalConstants.ID);
		
		if(StringUtils.isNotEmpty(corpId)){
			// 拉取公司信息
			mRegisterEventLogic.getCorpInfo(corpId);
		}else{
			Log.e("@@@", "must have corpid in CorpInfoEditActivity");
		}
	}

	@Override
	public void onEventMainThread(Message msg) {
		
		if(msg.obj instanceof InfoResult){
			InfoResult infoResult = (InfoResult) msg.obj;
			switch (msg.what) {
			case R.id.register_get_corp:
				JSONObject jsonObject = (JSONObject) JSONObject.parse(infoResult.getResult());
				if(jsonObject != null){
					mCorpInfo = new CorpInfo(jsonObject.getJSONObject(GlobalConstants.JSON_CORP));
					
					if(StringUtils.isNotEmpty(mCorpInfo.getLname())){
						mCorpName.setFormLabel(mCorpInfo.getLname());
					}
					if(StringUtils.isNotEmpty(mCorpInfo.getName())){
						mCorpSimpName.setFormContent(mCorpInfo.getName());
					}
					
					mCorpLogo.setDefaultImageResId(R.drawable.default_corp_logo);
					mCorpLogo.setImageUrl(mCorpInfo.getLogo(), mImageLoader);
					mCurChooseLogo = mCorpInfo.getLogo();
					
					if(mCorpInfo.getBenefits() != null){
						StringBuilder sb = new StringBuilder();
						for(int i = 0; i < mCorpInfo.getBenefits().size() ; i ++ ){
							sb.append(mCorpInfo.getBenefits().getJSONObject(i).getString(GlobalConstants.JSON_NAME));
							sb.append(",");
						}
						mCorpBenifits.setFormContent(sb.substring(0, sb.length() - 1));
					}
					if(StringUtils.isNotEmpty(mCorpInfo.getIndustry())){
						mCorpIndustry.setFormContent(mCorpInfo.getIndustry());
						mIndustryId = mCorpInfo.getIndustryId();
						mIndustryMap.put(mCorpInfo.getIndustryId(), mCorpInfo.getIndustry());
					}
					if(StringUtils.isNotEmpty(mCorpInfo.getType())){
						mCorpType.setFormContent(mCorpInfo.getType());
					}
					if(StringUtils.isNotEmpty(mCorpInfo.getScale())){
						mCorpScale.setFormContent(mCorpInfo.getScale());
					}
					if(StringUtils.isNotEmpty(mCorpInfo.getWebsite())){
						mCorpWebsite.setFormContent(mCorpInfo.getWebsite());
					}
					if(StringUtils.isNotEmpty(mCorpInfo.getCity())){
						mCorpLocation.setFormContent(mCorpInfo.getCity());
					}
					if(StringUtils.isNotEmpty(mCorpInfo.getAddress())){
						mCorpLocationDetail.setFormContent(getString(R.string.general_tips_alfill));
						mLocationDetail = mCorpInfo.getAddress();
					}
					
					if(StringUtils.isNotEmpty(mCorpInfo.getDesc())){
						mCorpDesc.setFormContent(getString(R.string.general_tips_alfill));
						mDesc = mCorpInfo.getDesc();
					}
				}
				// 访问网络请求成功，才显示整体的View
				if(mLayout.getVisibility() != View.VISIBLE){
					Animation loadAnimation = AnimationUtils.loadAnimation(this,R.anim.anim_fide_in_for_recruit);
					mLayout.startAnimation(loadAnimation);
					mLayout.setVisibility(View.VISIBLE);
				}
				
				break;
			case R.id.register_get_corp_position:
				JSONObject jobsObject = (JSONObject) JSONObject.parse(infoResult.getResult());
				if(jobsObject != null){
					JSONArray jsonArray = jobsObject.getJSONArray(GlobalConstants.JSON_JOBS);
					if(jsonArray != null){
						for(int i = 0 ; i < jsonArray.size() ; i ++ ){
							JobInfo info = new JobInfo(jsonArray.getJSONObject(i));
							mDatas.add(info);
						}
						positionsAdapter.notifyDataSetChanged();
					}
				}
				break;
			case R.id.register_get_corp_type:
				JSONObject corpTypeObject = (JSONObject) JSONObject.parseObject(infoResult.getResult());
				if(corpTypeObject != null){
					JSONArray corpArray = corpTypeObject.getJSONArray(GlobalConstants.JSON_CORPTYPES);
					if(corpArray != null){
						for (int i = 0; i < corpArray.size(); i++) {
							mCorpTypesMap.put(corpArray.getJSONObject(i).getInteger(GlobalConstants.JSON_ID),corpArray.getJSONObject(i).getString(GlobalConstants.JSON_NAME));
						}
					}
				}
				break;
			case R.id.register_get_corp_scale:
				JSONObject corpScaleObject = (JSONObject) JSONObject.parseObject(infoResult.getResult());
				if(corpScaleObject != null){
					JSONArray corpArray = corpScaleObject.getJSONArray(GlobalConstants.JSON_CORPSCALES);
					if(corpArray != null){
						for (int i = 0; i < corpArray.size(); i++) {
							mCorpScalesMap.put(corpArray.getJSONObject(i).getInteger(GlobalConstants.JSON_ID),corpArray.getJSONObject(i).getString(GlobalConstants.JSON_NAME));
						}
					}
				}
				break;
			case R.id.register_upload_corp_logo:
				if(infoResult.getMessage().getStatus() == 0){
					mCurChooseLogo = infoResult.getMessage().getLogo();
					mCorpLogo.setImageUrl(mCurChooseLogo, mImageLoader);
				}
				if(loadingDialog.isShowing()){
					loadingDialog.dismiss();
				}
				break;
			case R.id.register_set_corp:
				if(infoResult.getMessage().getStatus() == 0){
					AppActivityManager.getInstance().finishActivity(CorpInfoEditActivity.this);
					// finish();
				}
				if(loadingDialog.isShowing()){
					loadingDialog.dismiss();
				}
				break;
			default:
				break;
			}
		}else if(msg.obj instanceof VolleyError){
			if(loadingDialog.isShowing()){
				loadingDialog.dismiss();
			}
			VToast.show(this, getString(R.string.general_tips_network_unknow));
		}
		
	}
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Intent intent = new Intent();
		intent.putExtra(GlobalConstants.JOB_ID, mDatas.get(position).getJobId());
		intent.setClass(CorpInfoEditActivity.this, JobPositionInfoActivity.class);
		startActivity(intent);
	}

	@Override
	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
		case R.id.corp_logo:
			intent = new Intent(this,ImagePickerActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//			intent.putExtra(GlobalConstants.PHOTO_DOOR,GlobalConstants.PHOTO_PROFILE);
//			intent = new Intent();
            /* 开启Pictures画面Type设定为image */  
//            intent.setType("image/*");  
            /* 使用Intent.ACTION_GET_CONTENT这个Action */  
//            intent.setAction(Intent.ACTION_GET_CONTENT);   
            /* 取得相片后返回本画面 */  
//            startActivityForResult(intent, 1);  
			startActivityForResult(intent,103);
			
			
			break;
		case R.id.corp_simple_name:
			mCorpSimpName.setEdit(true);
			break;
		case R.id.corp_type:
			if (!mCorpTypesMap.isEmpty()) {
				selectCompanyType();
			}
			break;
		case R.id.corp_scale:
			if (!mCorpScalesMap.isEmpty()) {
				selectCorpScale();
			}
			break;
		case R.id.corp_website:
			mCorpWebsite.setEdit(true);
			mCorpWebsite.getInfoEdit().setText(mCorpWebsite.getFormContentText());
			mCorpWebsite.getInfoEdit().setSelection(mCorpWebsite.getFormContentText().length());
			break;
		case R.id.corp_industry:
			intent = new Intent();
			intent.putExtra("industry_selsect", mIndustryMap);  
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.setClass(this, CorpInfoEditSingleActivity.class);
			intent.putExtra(GlobalConstants.SEQ_STRING, 1);
			startActivityForResult(intent,1);
			break;
		case R.id.corp_benifits:
			intent = new Intent();
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.setClass(this, CorpInfoEditSingleActivity.class);
			intent.putExtra(GlobalConstants.SEQ_STRING, 2);
			if(StringUtils.isNotEmpty(mCorpBenifits.getFormContentText())){
				intent.putExtra(GlobalConstants.NAME, mCorpBenifits.getFormContentText());
			}
			startActivityForResult(intent,2);
			break;
		case R.id.corp_location:
			intent = new Intent();
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.setClass(this,RegisterResidentSearchActivity.class);
			startActivityForResult(intent, 102);
			break;
		case R.id.corp_location_detail:
			intent = new Intent();
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.setClass(this, CorpInfoEditSingleActivity.class);
			intent.putExtra(GlobalConstants.SEQ_STRING, 4);
			intent.putExtra(GlobalConstants.FILL, mLocationDetail);
			startActivityForResult(intent,4);
			break;
		case R.id.corp_desc:
			intent = new Intent();
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.setClass(this, CorpInfoEditSingleActivity.class);
			intent.putExtra(GlobalConstants.SEQ_STRING, 3);
			intent.putExtra(GlobalConstants.FILL, mDesc);
			startActivityForResult(intent,3);
			break;
		default:
			break;
		}
	}
	
	private int choose;
	/**
	 * 选择公司类型
	 */
	private void selectCompanyType() {
		final String[] corpTypes = mCorpTypesMap.values().toArray(new String[]{});
		final Integer[] corpTypeid = mCorpTypesMap.keySet().toArray(new Integer[]{});
		if (!mAlertDialog.isShowing()) {
			choose = 0;
			mAlertDialog = new AlertDialog.Builder(this).setTitle(mCorpType.getFormLabelText()).setSingleChoiceItems(corpTypes,choose,new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					choose = which;
				}
			}).setPositiveButton(R.string.btn_confirm, new DialogInterface.OnClickListener() {
						
				public void onClick(DialogInterface dialog, int which) {
						mCorpType.setFormContent(corpTypes[choose]);
						mType = corpTypeid[choose];
				}
			}).setNegativeButton(R.string.btn_cancel, null).create();
			mAlertDialog.show();
		}
	}
	
	private void selectCorpScale(){
		final String[] corpScales = mCorpScalesMap.values().toArray(new String[]{});
		final Integer[] corpScaleid = mCorpScalesMap.keySet().toArray(new Integer[]{});
		if (!mAlertDialog.isShowing()) {
			choose = 0;
			mAlertDialog = new AlertDialog.Builder(this).setTitle(mCorpScale.getFormLabelText()).setSingleChoiceItems(corpScales,0,new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					choose = which;
				}
			}).setPositiveButton(R.string.btn_confirm, new DialogInterface.OnClickListener() {
						
				public void onClick(DialogInterface dialog, int which) {
					mCorpScale.setFormContent(corpScales[choose]);
					mScale = corpScaleid[choose];
				}
			}).setNegativeButton(R.string.btn_cancel, null).create();
			mAlertDialog.show();
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode== RESULT_OK){
			switch (requestCode) {
			case 1:
				String industry = data.getStringExtra(GlobalConstants.NAME);
				if(StringUtils.isNotEmpty(industry)){
					mCorpIndustry.setFormContent(industry);
					mIndustryId = data.getStringExtra(GlobalConstants.ID);
				}
				break;
			case 2:
				String benifits = data.getStringExtra(GlobalConstants.NAME);
				if(StringUtils.isNotEmpty(benifits)){
					mCorpBenifits.setFormContent(benifits);
				}
				break;
			case 3:
				mDesc = data.getStringExtra(GlobalConstants.FILL);
				if(StringUtils.isNotEmpty(mDesc)){
					mCorpDesc.setFormContent(getString(R.string.general_tips_alfill));
				}
				break;
			case 4:
				mLocationDetail = data.getStringExtra(GlobalConstants.FILL);
				if(StringUtils.isNotEmpty(mLocationDetail)){
					mCorpLocationDetail.setFormContent(getString(R.string.general_tips_alfill));
				}
				break;
			case 102:
				String city = data.getStringExtra(RegisterResidentSearchActivity.BACKDATA_KEY);
				if (StringUtils.isNotEmpty(city)) {
					mCorpLocation.setFormContent(city);
				}
				break;
			case 103:
				// get the Uri for the captured image
				Bundle bundle = data.getExtras();
				Intent intent = new Intent(this,ImageClipActivity.class);
				String clipUrl = data.getStringExtra(GlobalConstants.CLIP_PHOTO_URL);
				if(StringUtils.isNotEmpty(clipUrl)){
					intent.putExtra(GlobalConstants.CLIP_PHOTO_URL, clipUrl);
				}
				intent.putExtra(GlobalConstants.CLIP_PHOTO, bundle);
				startActivityForResult(intent, 104);
				
				break;
			case 104:
				setIntent(data);
				processExtraData();
				break;
			default:
				break;
			}
		}
	}
	/**
	 * 处理额外数据
	 */
	private void processExtraData() {
		String filePah = getIntent().getStringExtra("bitmap_path");
		if(StringUtils.isEmpty(filePah))
			return;
		BufferedInputStream in = null;
		ByteArrayOutputStream out = null;
		try {
			in = new BufferedInputStream(new FileInputStream(filePah));
			out = new ByteArrayOutputStream(1024);    
		       
	        byte[] temp = new byte[1024];        
	        int size = 0;        
	        while ((size = in.read(temp)) != -1) {        
	            out.write(temp, 0, size);        
	        }        
			in.close();
			
			byte[] bis = out.toByteArray();  
			Bitmap bitmap = BitmapFactory.decodeByteArray(bis, 0, bis.length);
			
			if (bitmap != null) {
				// 在这里将选择的图片传到服务器上
				mRegisterEventLogic.uploadCorpLogo(filePah);
				
				// 初始化loading对话框
				loadingDialog = new LoadingDialog(CorpInfoEditActivity.this, R.style.loading_dialog);
				//设置提示框提示信息
				loadingDialog.setLoadingTipStr(getString(R.string.general_tips_uploading));
				loadingDialog.show();
				
				mCorpLogo.setImageBitmap(bitmap);
			}
			
			out.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	    
	    
	}
}
