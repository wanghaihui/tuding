package com.xiaobukuaipao.vzhi.profile;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.provider.MediaStore;
import android.text.InputType;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.android.volley.toolbox.Volley;
import com.xiaobukuaipao.vzhi.R;
import com.xiaobukuaipao.vzhi.cache.CommonBitmapMemoryCache;
import com.xiaobukuaipao.vzhi.domain.user.BasicInfo;
import com.xiaobukuaipao.vzhi.event.InfoResult;
import com.xiaobukuaipao.vzhi.register.ImageClipActivity;
import com.xiaobukuaipao.vzhi.register.ImagePickerActivity;
import com.xiaobukuaipao.vzhi.register.RegisterResidentSearchActivity;
import com.xiaobukuaipao.vzhi.util.ActivityQueue;
import com.xiaobukuaipao.vzhi.util.GlobalConstants;
import com.xiaobukuaipao.vzhi.util.Logcat;
import com.xiaobukuaipao.vzhi.util.StringUtils;
import com.xiaobukuaipao.vzhi.util.VToast;
import com.xiaobukuaipao.vzhi.widget.FormItemByLineLayout;
import com.xiaobukuaipao.vzhi.widget.FormItemByLineView;
import com.xiaobukuaipao.vzhi.widget.NumberPicker;
import com.xiaobukuaipao.vzhi.widget.NumberPicker.OnValueChangeListener;
import com.xiaobukuaipao.vzhi.widget.SegmentedRadioGroup;
import com.xiaobukuaipao.vzhi.widget.SimpleNumberDialog;
import com.xiaobukuaipao.vzhi.wrap.ProfileWrapActivity;

/**
 * 完善基本信息encodeParameters
 */
public class ProfileCompleteActivity extends ProfileWrapActivity implements
		OnClickListener, OnCheckedChangeListener {

	private TextView mCompleteTips;
	
	private FormItemByLineLayout mCompleteRealname;
	private FormItemByLineLayout mCompleteNickname;
	private SegmentedRadioGroup mCompleteGender;

	private FormItemByLineView mCompleteAge;
	private FormItemByLineView mCompleteExpr;
	private FormItemByLineView mCompleteCity;
	private FormItemByLineLayout mCompleteEmail;
	private FormItemByLineView mCompleteMobile;
	private String[] mExperiences ;
	
	private BasicInfo mBasicInfo = new BasicInfo();
	private Class<?> next;

	private PopupWindow mPopupWindow;
	private LinearLayout mPopupLayout;

	private ImageView realavatar;
	// 请求队列
	private RequestQueue mQueue;
	private ImageLoader mImageLoader;
	private ImageListener mListener;
//	private Bundle bundle;
	
	private Context mContext;
	
	private boolean isShowing = false;
	
	// 文件名
	private String mCurrentPhotoPath;
	
	private File createImageFile() throws IOException {
		// Create an image file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
		String imageFileName = "JPEG_" + timeStamp + "_";
		File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
		File image = File.createTempFile(imageFileName, ".jpg", storageDir);
		
		mCurrentPhotoPath = image.getAbsolutePath();
		return image;
	}
		
	@Override
	public void initUIAndData() {
		
		setContentView(R.layout.activity_complete_resume_baseinfo);
		setHeaderMenuByLeft(this);
		setHeaderMenuByCenterTitle(R.string.post_resume_complete_baseinfo);
		setHeaderMenuByRight(R.string.general_tips_next).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						Logcat.d("@@@", mBasicInfo.toString());
						// fill data
						if(mCompleteRealname.isChanged())
							mBasicInfo.setName(mCompleteRealname.getInfoEdit().getText().toString());
						if(mCompleteNickname.isChanged())
							mBasicInfo.setNickname(mCompleteNickname.getInfoEdit().getText().toString());
						if(mCompleteEmail.isChanged())
							mBasicInfo.setEmail(mCompleteEmail.getInfoEdit().getText().toString());
						
						if(StringUtils.isEmpty(mBasicInfo.getName())){
							VToast.show(mContext, getString(R.string.general_tips_fill_tips,mCompleteRealname.getFormLabel().getText().toString()));
							return;
						}
						if(StringUtils.isEmpty(mBasicInfo.getNickname())){
							VToast.show(mContext, getString(R.string.general_tips_fill_tips,mCompleteNickname.getFormLabel().getText().toString()));
							return;
						}
						if(mBasicInfo.getAge() == -1){
							VToast.show(mContext, getString(R.string.general_tips_fill_tips,mCompleteAge.getFormLabel().getText().toString()));
							return;
						}
						if(mBasicInfo.getExpr() == -1){
							VToast.show(mContext, getString(R.string.general_tips_fill_tips,mCompleteExpr.getFormLabel().getText().toString()));
							return;
						}
						if(StringUtils.isEmpty(mBasicInfo.getCity())){
							VToast.show(mContext, getString(R.string.general_tips_fill_tips,mCompleteCity.getFormLabel().getText().toString()));
							return;
						}
						
						String reg = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$";
						if(StringUtils.isEmpty(mBasicInfo.getEmail())){
						}else if(!Pattern.matches(reg, mBasicInfo.getEmail())){
							VToast.show(mContext, getString(R.string.post_resume_email_tips));
							return;
						}
						
						mProfileEventLogic.setBasicInfo(
								mBasicInfo.getRealAvatar(),
								mBasicInfo.getAvatar(), mBasicInfo.getName(),
								mBasicInfo.getNickname(),
								mBasicInfo.getGender(), mBasicInfo.getAge(),
								mBasicInfo.getCity(), mBasicInfo.getExpr(),
								mBasicInfo.getEmail(), mBasicInfo.getMobile(),-1);
					}
				});
		
		mContext = this;
		mExperiences = getResources().getStringArray(R.array.job_experience);	
		// 网络请求
		mQueue = Volley.newRequestQueue(this);
		mImageLoader = new ImageLoader(mQueue, new CommonBitmapMemoryCache());
		mCompleteTips = (TextView) findViewById(R.id.complete_tips);
		mCompleteTips.setText(R.string.post_resume_complete_tips);

		mPopupLayout = (LinearLayout) View.inflate(this,R.layout.popup_profile_basicinfo_realavatar, null);
		mPopupWindow = new PopupWindow();
		mPopupWindow.setBackgroundDrawable(new ColorDrawable(0));
		mPopupWindow.setTouchInterceptor(new OnTouchListener() {

			@SuppressLint("ClickableViewAccessibility")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
					if (mPopupWindow.isShowing())
						mPopupWindow.dismiss();
					return true;
				}
				return false;
			}
		});

		mPopupWindow.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				WindowManager.LayoutParams lp = getWindow().getAttributes();
				lp.alpha = 1f;
				getWindow().setAttributes(lp);
			}
		});

		mPopupLayout.findViewById(R.id.popup_base_takephoto).setOnClickListener(this);
		mPopupLayout.findViewById(R.id.popup_base_imgs).setOnClickListener(this);
		mPopupLayout.findViewById(R.id.popup_base_cancel).setOnClickListener(this);

		realavatar = (ImageView) findViewById(R.id.baseinfo_user_head);
		realavatar.setOnClickListener(this);
		
		mCompleteRealname = (FormItemByLineLayout) findViewById(R.id.post_resume_complete_realname);
		mCompleteRealname.setOnClickListener(this);
		mCompleteNickname = (FormItemByLineLayout) findViewById(R.id.post_resume_complete_nickname);
		mCompleteNickname.setOnClickListener(this);
		mCompleteGender = (SegmentedRadioGroup) findViewById(R.id.post_resume_complete_gender_radiogroup);
		mCompleteGender.setOnCheckedChangeListener(this);
		mCompleteAge = (FormItemByLineView) findViewById(R.id.post_resume_complete_age);
		mCompleteAge.setOnClickListener(this);
		mCompleteExpr = (FormItemByLineView) findViewById(R.id.post_resume_complete_exp);
		mCompleteExpr.setOnClickListener(this);
		mCompleteCity = (FormItemByLineView) findViewById(R.id.post_resume_complete_city);
		mCompleteCity.setOnClickListener(this);
		mCompleteEmail = (FormItemByLineLayout) findViewById(R.id.post_resume_complete_email);
		mCompleteEmail.setOnClickListener(this);
		
		mCompleteMobile = (FormItemByLineView) findViewById(R.id.post_resume_complete_mobile);

		mProfileEventLogic.getBasicinfo();

	}


	@Override
	public void onEventMainThread(Message msg) {
		if (msg.obj instanceof InfoResult) {
			InfoResult infoResult = (InfoResult) msg.obj;
			switch (msg.what) {
			case R.id.register_get_basicinfo:
				JSONObject basicinfo = (JSONObject) JSONObject.parseObject(infoResult.getResult());
				mBasicInfo = new BasicInfo(basicinfo.getJSONObject(GlobalConstants.JSON_USERBASIC));
				if (StringUtils.isNotEmpty(mBasicInfo.getName())) {
					mCompleteRealname.setFormContent(mBasicInfo.getName());
				}
				if (StringUtils.isNotEmpty(mBasicInfo.getNickname())) {
					mCompleteNickname.setFormContent(mBasicInfo.getNickname());
				}
				if (mBasicInfo.getGender() != -1) {
					mCompleteGender.setButton(mBasicInfo.getGender());
				}
				if (mBasicInfo.getAge() != -1) {
					mCompleteAge.setFormContent("" + mBasicInfo.getAge());
				}
				if (mBasicInfo.getExpr() != -1) {
					mCompleteExpr.setFormContent(mExperiences[mBasicInfo.getExpr()]);
				}
				if (StringUtils.isNotEmpty(mBasicInfo.getCity())) {
					mCompleteCity.setFormContent(mBasicInfo.getCity());
				}
				if (StringUtils.isNotEmpty(mBasicInfo.getEmail())) {
					mCompleteEmail.setFormContent(mBasicInfo.getEmail());
				}
				if (StringUtils.isNotEmpty(mBasicInfo.getMobile())) {
					mCompleteMobile.setFormContent(mBasicInfo.getMobile());
				}
				if (StringUtils.isNotEmpty(mBasicInfo.getRealAvatar())) {
					mListener = ImageLoader.getImageListener(realavatar, R.drawable.general_user_avatar, R.drawable.general_user_avatar);
					mImageLoader.get(mBasicInfo.getRealAvatar(), mListener);
					findViewById(R.id.icon_plus).setVisibility(View.GONE);
				}

				break;
			case R.id.profile_basic_info_set:
				if (infoResult.getMessage().getStatus() == 0) {
					Intent intent = getIntent();
					if (ActivityQueue.hasNext(ProfileCompleteActivity.class)){
						next = ActivityQueue.next(ProfileCompleteActivity.class);
						intent.setClass(this, next);
						startActivity(intent);
					}
				}
				VToast.show(this, infoResult.getMessage().getMsg());
				break;
			case R.id.register_upload_realavatar:
				if (infoResult.getMessage().getStatus() == 0) {
					// 此时会返回URL
					JSONObject jsonObject = (JSONObject) JSONObject
							.parse(infoResult.getResult());
					mBasicInfo.setRealAvatar(jsonObject
							.getString(GlobalConstants.URL_REAL_AVATAR));
				}
				VToast.show(this, infoResult.getMessage().getMsg());
				break;
			}
		}

	}


	@Override
	public void onBackPressed() {
		super.onBackPressed();
		mCompleteRealname.setFocusable(false);
	}

	@Override
	public void onClick(View v) {
		SimpleNumberDialog dialog = null;
		switch (v.getId()) {
		case R.id.post_resume_complete_realname:
			mCompleteRealname.setEdit(true);
			break;
		case R.id.post_resume_complete_nickname:
			mCompleteNickname.setEdit(true);
			break;
		case R.id.post_resume_complete_age:
			if(isShowing){
				return;
			}else{
				isShowing = true;
				if (mBasicInfo.getAge() == -1)// set default age
					mBasicInfo.setAge(22);
				dialog = new SimpleNumberDialog(this);
				dialog.setTitle(getString(R.string.register_age));
				dialog.setMaxValue(50);
				dialog.setMinValue(18);
				dialog.setValue(mBasicInfo.getAge());
				dialog.setOnValueChangeListener(new OnValueChangeListener() {
					@Override
					public void onValueChange(NumberPicker picker, int oldVal,
							int newVal) {
						// 给年龄赋值
						mBasicInfo.setAge(newVal);
					}
				});
				// 设置年龄从22岁开始
				dialog.setOnCancelListener(new OnCancelListener() {

					@Override
					public void onCancel(DialogInterface dialog) {
						mCompleteAge.getFormContent().setTextColor(0xff000000);
						mCompleteAge.setFormContent("" + mBasicInfo.getAge());
						isShowing = false;
					}
				});
				dialog.show();
			}

			break;
		case R.id.post_resume_complete_exp:
			if(isShowing){
				return;
			}else{
				isShowing = true;
				int index = 0;
				if (mBasicInfo.getExpr() == -1) {
					mBasicInfo.setExpr(index);
				}
				dialog = new SimpleNumberDialog(this);
				dialog.setTitle(getString(R.string.register_expr));
				dialog.setMaxValue(mExperiences.length - 1);
				dialog.setMinValue(0);
				dialog.setStrings(mExperiences);
				
				for (int i = 0; i < mExperiences.length; i++) {
					if (mExperiences[i].equals(mExperiences[mBasicInfo.getExpr()])) {
						dialog.setValue(i);
						index = i;
					}
				}
				
				dialog.setValue(index);
				dialog.setOnValueChangeListener(new OnValueChangeListener() {
					@Override
					public void onValueChange(NumberPicker picker, int oldVal,
							int newVal) {
						mBasicInfo.setExpr(newVal);
					}
				});
				dialog.setOnCancelListener(new OnCancelListener() {
	
					@Override
					public void onCancel(DialogInterface dialog) {
						if (mBasicInfo.getExpr() != -1){
							mCompleteExpr.getFormContent().setTextColor(0xff000000);
							mCompleteExpr.setFormContent(mExperiences[mBasicInfo.getExpr()]+ "经验");
							isShowing = false;
						}
					}
				});
				dialog.show();
			}
			break;
		case R.id.post_resume_complete_city:
			startActivityForResult(new Intent(this,RegisterResidentSearchActivity.class), 102);
			break;
		case R.id.post_resume_complete_email:
			mCompleteEmail.getInfoEdit().setInputType(
					InputType.TYPE_CLASS_TEXT
							| InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
			mCompleteEmail.setEdit(true);
			break;
		case R.id.baseinfo_user_head:
			mPopupWindow.setAnimationStyle(android.R.style.Animation_InputMethod);
			mPopupWindow.setContentView(mPopupLayout);
			mPopupWindow.setHeight(LayoutParams.WRAP_CONTENT);
			mPopupWindow.setWidth(LayoutParams.MATCH_PARENT);
			mPopupWindow.setOutsideTouchable(true);
			mPopupWindow.setFocusable(true);
			WindowManager.LayoutParams lp = getWindow().getAttributes();
			lp.alpha = 0.5f;
			getWindow().setAttributes(lp);
			mPopupWindow.showAtLocation(getWindow().getDecorView()
					.getRootView(), Gravity.BOTTOM, 0, 0);
			break;
		case R.id.popup_base_cancel:
			mPopupWindow.dismiss();
			break;
		case R.id.popup_base_takephoto:
			/*try {
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				intent.putExtra(GlobalConstants.PHOTO_DOOR,GlobalConstants.PHOTO_PROFILE);
				startActivityForResult(intent, GlobalConstants.CAMERA_CAPTURE);
			} catch (ActivityNotFoundException anfe) {
				// display an error message
				String errorMessage = "设备不支持拍照";
				VToast.show(this, errorMessage);
			}*/
			Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			// Ensure that there's a camera activity to handle the intent
			if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
				// Create the File where the photo should go
				File photoFile = null;
				try {
					photoFile = createImageFile();
				} catch (IOException ex) {
					// Error occurred while creating the File
				}
				
				// Continue only if the File was successfully created
				if (photoFile != null) {
					takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
					startActivityForResult(takePictureIntent, GlobalConstants.CAMERA_CAPTURE);
				}
			}
			
			if(mPopupWindow.isShowing())
				mPopupWindow.dismiss();
			break;
		case R.id.popup_base_imgs:
			Intent intent = new Intent(this,ImagePickerActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivityForResult(intent,103);
			
			if(mPopupWindow.isShowing())
				mPopupWindow.dismiss();
			break;
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (requestCode == 102) {
				if (data != null) {
					mBasicInfo.setCity(data.getStringExtra(RegisterResidentSearchActivity.BACKDATA_KEY));
					if (StringUtils.isNotEmpty(mBasicInfo.getCity())) {	
						mCompleteCity.getFormContent().setTextColor(0xff000000);
						mCompleteCity.setFormContent(mBasicInfo.getCity());
					}
				}
			}else if (requestCode == GlobalConstants.CAMERA_CAPTURE) {
				Intent intent = new Intent(ProfileCompleteActivity.this,ImageClipActivity.class);
				intent.putExtra(GlobalConstants.CLIP_PHOTO_URL, mCurrentPhotoPath);
				startActivityForResult(intent, 104);
				
			}else if(requestCode == 103){
				Bundle bundle = data.getExtras();
				Intent intent = new Intent(this,ImageClipActivity.class);
				String clipUrl = data.getStringExtra(GlobalConstants.CLIP_PHOTO_URL);
				if(StringUtils.isNotEmpty(clipUrl)){
					intent.putExtra(GlobalConstants.CLIP_PHOTO_URL, clipUrl);
				}
				intent.putExtra(GlobalConstants.CLIP_PHOTO, bundle);
				startActivityForResult(intent, 104);
				
//				
//				String filePah = data.getStringExtra("bitmap_path");
//				Bitmap bitmap = decodeBitmap(filePah);

			}else if(requestCode == 104){
				setIntent(data);
				processExtraData();
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
				mRegisterEventLogic.uploadRealAvatar(filePah);
				realavatar.setImageBitmap(bitmap);
				findViewById(R.id.icon_plus).setVisibility(View.GONE); 
			}
			
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	    
	}

	// 实现RadioGroup的Check选择接口
	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		if (group == mCompleteGender) {
			if (checkedId == R.id.button_one) {
				// 这里的数据会保存在数据库里--性别男--gender = 1
				mBasicInfo.setGender(1);
			} else if (checkedId == R.id.button_two) {
				// 这里的数据会保存在数据库里--性别女--gender = 0
				mBasicInfo.setGender(0);
			}
		}
	}
	
}
