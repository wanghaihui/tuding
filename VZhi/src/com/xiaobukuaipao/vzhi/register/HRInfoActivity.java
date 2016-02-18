package com.xiaobukuaipao.vzhi.register;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Environment;
import android.os.Message;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout.LayoutParams;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.android.volley.toolbox.Volley;
import com.xiaobukuaipao.vzhi.R;
import com.xiaobukuaipao.vzhi.cache.CommonBitmapMemoryCache;
import com.xiaobukuaipao.vzhi.contentprovider.GeneralContentProvider;
import com.xiaobukuaipao.vzhi.database.UserInfoTable;
import com.xiaobukuaipao.vzhi.domain.user.BasicInfo;
import com.xiaobukuaipao.vzhi.event.InfoResult;
import com.xiaobukuaipao.vzhi.util.GlobalConstants;
import com.xiaobukuaipao.vzhi.util.StringUtils;
import com.xiaobukuaipao.vzhi.util.VToast;
import com.xiaobukuaipao.vzhi.view.LoadingDialog;
import com.xiaobukuaipao.vzhi.widget.FormItemByLineLayout;
import com.xiaobukuaipao.vzhi.widget.FormItemByLineView;
import com.xiaobukuaipao.vzhi.widget.NumberPicker;
import com.xiaobukuaipao.vzhi.widget.NumberPicker.OnValueChangeListener;
import com.xiaobukuaipao.vzhi.widget.SegmentedRadioGroup;
import com.xiaobukuaipao.vzhi.widget.SimpleNumberDialog;
import com.xiaobukuaipao.vzhi.wrap.RegisterWrapActivity;

public class HRInfoActivity extends RegisterWrapActivity implements
		OnCheckedChangeListener, OnClickListener {
	private ImageView mAvatar;
	private int gender = 1;
	private FormItemByLineLayout mRealName;
	// 横向RadioGroup性别选择框
	private SegmentedRadioGroup mGender;
	
	// 请求队列
	private RequestQueue mQueue;
	private ImageLoader mImageLoader;
	private ImageListener mListener;
	
	private PopupWindow mPopupWindow;
	private LinearLayout mPopupLayout;

	// 当前选择的头像--实际上代表url
	private String mCurrentChooseAvatar = null;
	// 昵称
	private int age = -1;
	private String city;

	private FormItemByLineView mAge;
	private FormItemByLineView mLocation;
	
//	private Bundle bundle;
	
	private LoadingDialog loadingDialog;
	
	/**
	 * 是否从招聘服务来
	 */
	private boolean isFromRecruitSetting;
	
	// 文件名
	private String mCurrentPhotoPath;
	private Bitmap bitmap;
	
	private File createImageFile() throws IOException {
		// Create an image file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss" ,Locale.getDefault()).format(new Date());
		String imageFileName = "JPEG_" + timeStamp + "_";
		File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
		File image = File.createTempFile(imageFileName, ".jpg", storageDir);
		
		mCurrentPhotoPath = image.getAbsolutePath();
		return image;
	}
		
	@Override
	public void initUIAndData() {
		
		isFromRecruitSetting = 	getIntent().getBooleanExtra(GlobalConstants.RECRUIT_SETTING, false);
		
		setContentView(R.layout.activity_hr_info);
		setHeaderMenuByLeft(this);
		setHeaderMenuByCenterTitle(R.string.title_hr_info);
		setHeaderMenuByRight(R.string.general_tips_next);
		findViewById(R.id.layout).setVisibility(View.INVISIBLE);
		
		
		mGender = (SegmentedRadioGroup) findViewById(R.id.segment_text);
		mGender.setOnCheckedChangeListener(this);

		onClickListenerBySaveDataAndRedirectActivity(R.id.menu_bar_right);
		mAvatar = (ImageView) findViewById(R.id.hr_info_protrait);

		mRealName = (FormItemByLineLayout) findViewById(R.id.hr_info_name);
		mAge = (FormItemByLineView)findViewById(R.id.hr_info_age);
		mLocation = (FormItemByLineView)findViewById(R.id.hr_info_city);
		mRealName.setOnClickListener(this);
		mAge.setOnClickListener(this);
		mLocation.setOnClickListener(this);
		
		
		mPopupLayout = (LinearLayout) View.inflate(this,R.layout.popup_hrinfo_head, null);

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
		
		mAvatar.setOnClickListener(this);
		mPopupLayout.findViewById(R.id.popup_base_takephoto).setOnClickListener(this);
		mPopupLayout.findViewById(R.id.popup_base_imgs).setOnClickListener(this);
		mPopupLayout.findViewById(R.id.popup_base_cancel).setOnClickListener(this);
		
		
		if(isFromRecruitSetting){//从招聘服务设置过来，要拉取一遍当前信息
			mProfileEventLogic.getBasicinfo();
			// 网络请求
			mQueue = Volley.newRequestQueue(this);
			mImageLoader = new ImageLoader(mQueue, new CommonBitmapMemoryCache());
		}
		
	}

	@Override
	public void onEventMainThread(Message msg) {
		if(msg.obj instanceof InfoResult){
			InfoResult infoResult = (InfoResult) msg.obj;
			switch (msg.what) {
			case R.id.profile_basic_info_set:
				if (infoResult.getMessage().getStatus() == 0) {
					Intent intent = new Intent(HRInfoActivity.this,HRInfoJobExprActivity.class);
					intent.putExtra(GlobalConstants.RECRUIT_SETTING, true);
					startActivity(intent);
					// 在这里将头像URL，昵称，性别，年龄，所在地，保存到数据库中
					ContentValues values = new ContentValues();
					// 匿名头像
					values.put(UserInfoTable.COLUMN_AVATAR,mCurrentChooseAvatar);
					values.put(UserInfoTable.COLUMN_GENDER, gender);
					values.put(UserInfoTable.COLUMN_REALAVATAR, mCurrentChooseAvatar);
					values.put(UserInfoTable.COLUMN_AGE, age);
					values.put(UserInfoTable.COLUMN_LOCATION, city);
					getContentResolver().update(GeneralContentProvider.USERINFO_CONTENT_URI,values, null, null);
				}else{
					VToast.show(this, infoResult.getMessage().getMsg());
				}
				break;
			case R.id.register_upload_realavatar:
				if(infoResult.getMessage().getStatus() == 0){
					mCurrentChooseAvatar = infoResult.getMessage().getRealavatar();
					if (bitmap != null) {
						// 在这里将选择的图片传到服务器上
						mAvatar.setImageBitmap(bitmap);
						bitmap.recycle();
						findViewById(R.id.icon_plus).setVisibility(View.GONE);
					}
				}
				
				bitmap = null;
				VToast.show(this, infoResult.getMessage().getMsg());
				loadingDialog.dismiss();
				break;
			case R.id.register_get_basicinfo:
				JSONObject basicinfo = (JSONObject) JSONObject.parseObject(infoResult.getResult());
				if(basicinfo != null){
					BasicInfo mBasicInfo = new BasicInfo(basicinfo.getJSONObject(GlobalConstants.JSON_USERBASIC));
					if (StringUtils.isNotEmpty(mBasicInfo.getName())) {
						mRealName.setFormContent(mBasicInfo.getName());
					}
					if (mBasicInfo.getGender() != -1) {
						mGender.setButton(mBasicInfo.getGender());
					}
					if (mBasicInfo.getAge() != -1) {
						mAge.setFormContent(String.valueOf(mBasicInfo.getAge()));
					}
					if (StringUtils.isNotEmpty(mBasicInfo.getCity())) {
						mLocation.setFormContent(mBasicInfo.getCity());
					}
					
					if (StringUtils.isNotEmpty(mBasicInfo.getRealAvatar())) {
						mListener = ImageLoader.getImageListener(mAvatar,R.drawable.general_user_avatar, R.drawable.general_user_avatar);
						mImageLoader.get(mBasicInfo.getRealAvatar(), mListener);
						findViewById(R.id.icon_plus).setVisibility(View.GONE);
						mCurrentChooseAvatar = mBasicInfo.getRealAvatar();
					}
					findViewById(R.id.layout).setVisibility(View.VISIBLE);
				}
				
				break;
			default:
				break;
			}
		}else if(msg.obj instanceof VolleyError){
			VToast.show(this, getString(R.string.general_tips_network_unknow));
			if(loadingDialog.isShowing()){
				loadingDialog.dismiss();
			}
			if(bitmap != null){
				bitmap.recycle();
				bitmap = null;
			}
		}
	}
	
	protected void onNewIntent(Intent intent) {

		super.onNewIntent(intent);
		// must store the new intent unless getIntent() will return the old one
		setIntent(intent);
		processExtraData();

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
			bitmap = BitmapFactory.decodeByteArray(bis, 0, bis.length);
			
			
			if (bitmap != null) {
				// 在这里将选择的图片传到服务器上
				mRegisterEventLogic.uploadRealAvatar(filePah);
				loadingDialog = new LoadingDialog(this, R.style.loading_dialog);
				loadingDialog.setLoadingTipStr(getString(R.string.general_tips_uploading));
				loadingDialog.show();
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
		if (group == mGender) {
			if (checkedId == R.id.button_one) {
				// 这里的数据会保存在数据库里--性别男--gender = 1
				gender = 1;
			} else if (checkedId == R.id.button_two) {
				// 这里的数据会保存在数据库里--性别女--gender = 0
				gender = 0;
			}
		}
	}

	@Override
	public void confirmNextAction() {
		if(StringUtils.isEmpty(mRealName.getFormContentText())){
			VToast.show(this, getString(R.string.general_tips_fill_tips,mRealName.getFormLabelText()));
			return;
		}
		if(StringUtils.isEmpty(mAge.getFormContentText())){
			VToast.show(this, getString(R.string.general_tips_fill_tips,mAge.getFormLabelText()));
			return;
		}
		if(StringUtils.isEmpty(mLocation.getFormContentText())){
			VToast.show(this, getString(R.string.general_tips_fill_tips,mLocation.getFormLabelText()));
			return;
		}
		mProfileEventLogic.cancel(R.id.profile_basic_info_set);
		mProfileEventLogic.setBasicInfo(mCurrentChooseAvatar, "", mRealName.getFormContentText(), "", gender, age, mLocation.getFormContentText(), -1, "", "",-1);
	
	}
	@Override
	protected void onPause() {
		if (mPopupWindow.isShowing())
			mPopupWindow.dismiss();
		super.onPause();
	}
	

	@Override
	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
		case R.id.hr_info_protrait:
			mPopupWindow.setAnimationStyle(android.R.style.Animation_InputMethod);
			mPopupWindow.setContentView(mPopupLayout);
			mPopupWindow.setHeight(LayoutParams.WRAP_CONTENT);
			mPopupWindow.setWidth(LayoutParams.MATCH_PARENT);
			mPopupWindow.setOutsideTouchable(true);
			mPopupWindow.setFocusable(true);
			WindowManager.LayoutParams lp = getWindow().getAttributes();
			lp.alpha = 0.5f;
			getWindow().setAttributes(lp);
			mPopupWindow.showAtLocation(getWindow().getDecorView().getRootView(), Gravity.BOTTOM, 0, 0);
			break;
		case R.id.popup_base_cancel:
			mPopupWindow.dismiss();
			break;
		case R.id.popup_base_takephoto:
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
			break;

		case R.id.popup_base_imgs:
			intent = new Intent(this,PhotoPickerActivity.class);
			intent.putExtra(GlobalConstants.PHOTO_DOOR,GlobalConstants.PHOTO_HR);
			startActivity(intent);
			break;
		case R.id.hr_info_age:
			SimpleNumberDialog dialog = new SimpleNumberDialog(this);
			age = age == -1 ? 22 : age;
			dialog.setValue(age);
			dialog.setMaxValue(50);
			dialog.setMinValue(18);
			dialog.setTitle(getString(R.string.register_age));
			dialog.setOnValueChangeListener(new OnValueChangeListener() {
				@Override
				public void onValueChange(NumberPicker picker, int oldVal,
						int newVal) {
					// 给年龄赋值
					age = newVal;
				}
			});
			// 设置年龄从22岁开始
			dialog.setOnCancelListener(new OnCancelListener() {

				@Override
				public void onCancel(DialogInterface dialog) {
					mAge.setFormContent("" + age);
				}
			});
			dialog.show();
			break;
		case R.id.hr_info_city:
			intent = new Intent(this,RegisterResidentSearchActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivityForResult(intent, 102);
			break;
		case R.id.hr_info_name:
			mRealName.setEdit(true);
			//TODO show
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
					city = data.getStringExtra(RegisterResidentSearchActivity.BACKDATA_KEY);
					if (StringUtils.isNotEmpty(city)) {
						mLocation.setFormContent(city);
					}
				}
			} else if (requestCode == GlobalConstants.CAMERA_CAPTURE) {
				Intent intent = new Intent(this,ClipImageActivity.class);
				intent.putExtra(GlobalConstants.CLIP_PHOTO_URL, mCurrentPhotoPath);
				intent.putExtra(GlobalConstants.PHOTO_DOOR,GlobalConstants.PHOTO_HR);
				startActivity(intent);
			}
		}
	}
	
	
}
