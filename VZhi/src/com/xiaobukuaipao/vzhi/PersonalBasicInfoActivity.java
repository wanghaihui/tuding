package com.xiaobukuaipao.vzhi;

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
import android.app.AlertDialog;
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
import android.text.InputType;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.squareup.picasso.Picasso;
import com.xiaobukuaipao.vzhi.domain.user.UserBasic;
import com.xiaobukuaipao.vzhi.event.InfoResult;
import com.xiaobukuaipao.vzhi.manager.GeneralDbManager;
import com.xiaobukuaipao.vzhi.register.ClipImageActivity;
import com.xiaobukuaipao.vzhi.register.PhotoPickerActivity;
import com.xiaobukuaipao.vzhi.register.RegisterBaseInfoActivity;
import com.xiaobukuaipao.vzhi.register.RegisterBaseInfoAvatarActivity;
import com.xiaobukuaipao.vzhi.register.RegisterResidentSearchActivity;
import com.xiaobukuaipao.vzhi.util.GlobalConstants;
import com.xiaobukuaipao.vzhi.util.StringUtils;
import com.xiaobukuaipao.vzhi.util.VToast;
import com.xiaobukuaipao.vzhi.view.LoadingDialog;
import com.xiaobukuaipao.vzhi.view.RoundedImageView;
import com.xiaobukuaipao.vzhi.widget.FormItemByLineLayout;
import com.xiaobukuaipao.vzhi.widget.FormItemByLineView;
import com.xiaobukuaipao.vzhi.widget.NumberPicker;
import com.xiaobukuaipao.vzhi.widget.NumberPicker.OnValueChangeListener;
import com.xiaobukuaipao.vzhi.widget.SegmentedRadioGroup;
import com.xiaobukuaipao.vzhi.widget.SimpleNumberDialog;
import com.xiaobukuaipao.vzhi.widget.VerifyUserAvatar;
import com.xiaobukuaipao.vzhi.wrap.ProfileWrapActivity;

public class PersonalBasicInfoActivity extends ProfileWrapActivity implements OnClickListener, OnCheckedChangeListener {
	public final static String TAG = PersonalBasicInfoActivity.class.getSimpleName();;

	private TextView mAvatarTitle;
	private RoundedImageView mNickAvatar;
	private VerifyUserAvatar mRealAvatar;

	private RelativeLayout mAvatarLayout;
	private FormItemByLineLayout mRealNameLayout;
	private FormItemByLineLayout mNickNameLayout;
	
	private TextView mGenderTitle;
	// 横向RadioGroup性别选择框
	private SegmentedRadioGroup mGender;
	
	private FormItemByLineView mAgeLayout;
	private int age = -1;
	
	private FormItemByLineView mExprLayout;
	private FormItemByLineView mLocationLayout;
	private FormItemByLineView mMobileLayout;
	private FormItemByLineLayout mEmailLayout;
	
	// 得到上层返回的数据
	private UserBasic mUserBasic;

	// 保存要上传的数据
	private String mPostRealAvatar;
	private String mPostNickAvatar;
	private String mPostRealName;
	private String mPostNickName;
	private int mPostGender;
	private int mPostAge;
	private int mPostExpr;
	private String mPostCities;
	private String mPostEmail;
	private String mPostPhone;
	
	
	private PopupWindow mPopupWindow;
	private LinearLayout mPopupLayout;
	
	// 变量--保存来确认是选择的修改真实头像还是匿名头像
	private boolean realAvatar;
	
	private Button mBaseAvatar;

	private LoadingDialog loadingDialog;
	
	private boolean isShowing = false;
	
	// 文件名
	private String mCurrentPhotoPath;
	
	private File createImageFile() throws IOException {
		// Create an image file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",Locale.getDefault()).format(new Date());
		String imageFileName = "JPEG_" + timeStamp + "_";
		File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
		File image = File.createTempFile(imageFileName, ".jpg", storageDir);
		
		mCurrentPhotoPath = image.getAbsolutePath();
		return image;
	}
	
	public void initUIAndData() {
		
		setContentView(R.layout.activity_personal_basic_info);
		
		setHeaderMenuByLeft(this);
		setHeaderMenuByCenterTitle(R.string.register_baseinfo);
		setHeaderMenuByRight(R.string.general_save);
		
		mUserBasic = getIntent().getParcelableExtra(GlobalConstants.PARCEL_USER_BASIC);
		
		mPopupLayout = (LinearLayout) View.inflate(this,R.layout.popup_baseinfo_head, null);
		
		mBaseAvatar = (Button) mPopupLayout.findViewById(R.id.popup_base_avatar);
		
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
		
		mAvatarTitle = (TextView) findViewById(R.id.info_avatar_title);
		mNickAvatar = (RoundedImageView) findViewById(R.id.info_nickavatar_edit);
		mRealAvatar = (VerifyUserAvatar) findViewById(R.id.info_realavatar_edit);
		mAvatarLayout = (RelativeLayout) findViewById(R.id.info_avatar_layout);
		
		mRealAvatar.getAvatar().setBorderWidth(0f);
		mNickAvatar.setBorderWidth(0f);
		
		mRealNameLayout = (FormItemByLineLayout) findViewById(R.id.real_name);
		mRealNameLayout.getFormLabel().setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
		mRealNameLayout.getFormLabel().setTextColor(getResources().getColor(R.color.general_color_666666));
		mRealNameLayout.setOnClickListener(this);
		
		mNickNameLayout = (FormItemByLineLayout) findViewById(R.id.nick_name);
		mNickNameLayout.getFormLabel().setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
		mNickNameLayout.getFormLabel().setTextColor(getResources().getColor(R.color.general_color_666666));
		mNickNameLayout.setOnClickListener(this);
		
		mGenderTitle = (TextView) findViewById(R.id.info_gender_title);
		mGenderTitle.setText(getResources().getString(R.string.register_gender));
		mGender = (SegmentedRadioGroup) findViewById(R.id.segment_text);
		mGender.setOnCheckedChangeListener(this);
		
		mAgeLayout = (FormItemByLineView) findViewById(R.id.age);
		mAgeLayout.getFormLabel().setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
		mAgeLayout.getFormLabel().setTextColor(getResources().getColor(R.color.general_color_666666));
		
		mExprLayout = (FormItemByLineView) findViewById(R.id.expr);
		mExprLayout.getFormLabel().setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
		mExprLayout.getFormLabel().setTextColor(getResources().getColor(R.color.general_color_666666));
		
		mLocationLayout = (FormItemByLineView) findViewById(R.id.location);
		mLocationLayout.getFormLabel().setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
		mLocationLayout.getFormLabel().setTextColor(getResources().getColor(R.color.general_color_666666));
		
		mMobileLayout = (FormItemByLineView) findViewById(R.id.mobile);
		mMobileLayout.getFormLabel().setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
		mMobileLayout.getFormLabel().setTextColor(getResources().getColor(R.color.general_color_666666));
		
		mEmailLayout = (FormItemByLineLayout) findViewById(R.id.email);
		mEmailLayout.getFormLabel().setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
		mEmailLayout.getFormLabel().setTextColor(getResources().getColor(R.color.general_color_666666));

		mAvatarTitle.setText(getResources().getString(R.string.hr_info_portrait));


		mAvatarLayout.setOnClickListener(this);
		mRealNameLayout.setOnClickListener(this);
		mNickNameLayout.setOnClickListener(this);

		mAgeLayout.setOnClickListener(this);
		// 工作经验
		mExprLayout.setOnClickListener(this);
		mLocationLayout.setOnClickListener(this);
		mMobileLayout.setOnClickListener(this);
		mEmailLayout.setOnClickListener(this);

		mRealAvatar.getAvatar().setBorderWidth(0f);
		
		if (!StringUtils.isEmpty(mUserBasic.getNickavatar())) {
			Picasso.with(this).load(mUserBasic.getRealavatar()).placeholder(R.drawable.general_user_avatar).into(mRealAvatar.getAvatar());
		} else {
			mNickAvatar.setImageResource(R.drawable.general_user_avatar);
		}
		if (!StringUtils.isEmpty(mUserBasic.getRealavatar())) {
			Picasso.with(this).load(mUserBasic.getNickavatar()).placeholder(R.drawable.general_user_avatar).into(mNickAvatar);
		} else {
			mRealAvatar.getAvatar().setImageResource(R.drawable.general_user_avatar);
		}
		
		// 真实头像
		mPostRealAvatar = mUserBasic.getRealavatar();
		mPostNickAvatar  = mUserBasic.getNickavatar();
		
		// 真实姓名
		if (!StringUtils.isEmpty(mUserBasic.getRealname())) {
			mRealNameLayout.getRightTipTV().setText(mUserBasic.getRealname());
			mPostRealName = mUserBasic.getRealname();
		}
		
		// 昵称
		if (!StringUtils.isEmpty(mUserBasic.getNickname())) {
			mNickNameLayout.getRightTipTV().setText(mUserBasic.getNickname());
			mPostNickName = mUserBasic.getNickname();
		}
		
		// 性别
		mGender.setButton(mUserBasic.getGender());
		mPostGender = mUserBasic.getGender();
		
		// 年龄
		
		if (mUserBasic.getAge() > 0) {
			mAgeLayout.getFormContent().setText(Integer.toString(mUserBasic.getAge()));
			mPostAge = mUserBasic.getAge();
		}
		
		// 工作经验
		if (!StringUtils.isEmpty(mUserBasic.getExpryear())) {
			mExprLayout.getFormContent().setText(mUserBasic.getExpryear());
			mPostExpr = mUserBasic.getExprid();
		}
		
		// 所在地
		if (!StringUtils.isEmpty(mUserBasic.getCity())) {
			mLocationLayout.getFormContent().setText(mUserBasic.getCity());
			mPostCities = mUserBasic.getCity();
		}
		
		// 邮箱
		if (!StringUtils.isEmpty(mUserBasic.getPersonalemail())) {
			mEmailLayout.getFormContent().setText(mUserBasic.getPersonalemail());
			mPostEmail = mUserBasic.getPersonalemail();
		}
		
		// 手机
		if (!StringUtils.isEmpty(mUserBasic.getMobile())) {
			mMobileLayout.getFormContent().setText(mUserBasic.getMobile());
			// 给手机号赋值，不能编辑
			mPostPhone = mUserBasic.getMobile();
		}
		
		mBaseAvatar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				startActivityForResult(new Intent(PersonalBasicInfoActivity.this,RegisterBaseInfoAvatarActivity.class), 102);
				if (mPopupWindow.isShowing()) {
					mPopupWindow.dismiss();
				}
			}
		});
	}
	
	// 点击保存按钮
	public void confirmNextAction() {
		// 给姓名赋值
		mPostRealName = mRealNameLayout.getRightTipTV().getText().toString();
		mPostNickName = mNickNameLayout.getRightTipTV().getText().toString();
		
		// 验证邮箱--不能不符合格式
		String reg = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$";
		if(StringUtils.isEmpty(mEmailLayout.getFormContent().getText().toString())){
			
			
		}else if(!Pattern.matches(reg, mEmailLayout.getFormContent().getText().toString())){
			VToast.show(this, getString(R.string.post_resume_email_tips));
			return;
		}
		
		mPostEmail = mEmailLayout.getFormContent().getText().toString();
		
		
		mUserBasic.setRealavatar(mPostRealAvatar);
		mUserBasic.setNickavatar(mPostNickAvatar);
		mUserBasic.setRealname(mPostRealName);
		mUserBasic.setNickname(mPostNickName);
		mUserBasic.setGender(mPostGender);
		mUserBasic.setAge(mPostAge);
		mUserBasic.setCity(mPostCities);
		mUserBasic.setExprid(mPostExpr);
		mUserBasic.setPersonalemail(mPostEmail);
		mUserBasic.setMobile(mPostPhone);
		
		GeneralDbManager.getInstance().updateToUserInfoTable(mUserBasic);
		
		// 
		loadingDialog = new LoadingDialog(this, R.style.loading_dialog);
		loadingDialog.setLoadingTipStr(getString(R.string.general_tips_saving));
		loadingDialog.show();
		mProfileEventLogic.setBasicInfo(
				mPostRealAvatar,
				mPostNickAvatar, 
				mPostRealName,
				mPostNickName,
				mPostGender, mPostAge,
				mPostCities, mPostExpr,
				mPostEmail, mPostPhone,
				-1);
	}

		@Override
		public void onClick(View v) {
			Intent intent = null;
			switch (v.getId()) {
			case R.id.info_avatar_layout:
				if(isShowing){
					return;
				}else{
					isShowing = true;
					LinearLayout layout = (LinearLayout) View.inflate(this,R.layout.user_basic_two_avatar,null);
					final AlertDialog dialog = new AlertDialog.Builder(PersonalBasicInfoActivity.this).create();
					dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
						
						@Override
						public void onDismiss(DialogInterface dialog) {
							isShowing = false;
						}
					});
					dialog.show();
					dialog.setCanceledOnTouchOutside(true);
					dialog.getWindow().setContentView(layout);
					
					RelativeLayout mDialogRealLayout = (RelativeLayout) layout.findViewById(R.id.dialog_real_layout);
					VerifyUserAvatar mDialogRealAvatar = (VerifyUserAvatar) layout.findViewById(R.id.dialog_real_avatar);
					RelativeLayout mDialogAnonyLayout = (RelativeLayout) layout.findViewById(R.id.dialog_annoy_layout);
					RoundedImageView mDialogNickAvatar = (RoundedImageView) layout.findViewById(R.id.dialog_annoy_avatar);
					
					mDialogRealAvatar.getAvatar().setBorderWidth(0f);
					mDialogNickAvatar.setBorderWidth(0f);
					
					if (!StringUtils.isEmpty(mUserBasic.getRealavatar())) {
						Picasso.with(this).load(mUserBasic.getRealavatar()).placeholder(R.drawable.general_user_avatar).into(mDialogRealAvatar.getAvatar());
					} else {
						mDialogRealAvatar.getAvatar().setImageResource(R.drawable.general_user_avatar);
					}
					
					if (!StringUtils.isEmpty(mUserBasic.getNickavatar())) {
						Picasso.with(this).load(mUserBasic.getNickavatar()).placeholder(R.drawable.general_user_avatar).into(mDialogNickAvatar);
					} else {
						mDialogNickAvatar.setImageResource(R.drawable.general_user_avatar);
					}
					
					mDialogRealLayout.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							
							realAvatar = true;
							
							mBaseAvatar.setVisibility(View.GONE);
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
							dialog.dismiss();
						}
						
					});
					mDialogAnonyLayout.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							realAvatar = false;
							
							mBaseAvatar.setVisibility(View.VISIBLE);
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
							dialog.dismiss();
						}
					});
				}
				
			

				break;
			case R.id.real_name:
				// 在这里执行后，NickName保存在mNickName.getFormContent()
				mRealNameLayout.setEdit(true);
				break;
			case R.id.nick_name:
				mNickNameLayout.setEdit(true);
				break;
			case R.id.age:
				if(isShowing){
					return;
				}else{
					isShowing = true;
					SimpleNumberDialog ageDialog = new SimpleNumberDialog(
							PersonalBasicInfoActivity.this);
					age = age == -1 ? 26 : age;
					ageDialog.setValue(age);
					ageDialog.setMaxValue(50);
					ageDialog.setMinValue(18);
					ageDialog.setTitle(getString(R.string.register_age));
					ageDialog.setOnValueChangeListener(new OnValueChangeListener() {
						@Override
						public void onValueChange(NumberPicker picker, int oldVal,
								int newVal) {
							// 给年龄赋值
							age = newVal;
						}
					});
					// 设置年龄从22岁开始
					ageDialog.setOnCancelListener(new OnCancelListener() {
						@Override
						public void onCancel(DialogInterface dialog) {
							mAgeLayout.setFormContent("" + age);
							
							// 给上传年龄赋新值
							mPostAge = age;
							isShowing = false;
						}
					});
					ageDialog.show();
				}
				break;
				
			case R.id.expr:
				if(isShowing){
					return;
				}else{
					isShowing = true;
					int index = 0;
					final String[] array = getResources().getStringArray(R.array.job_experience);
					SimpleNumberDialog exprDialog = new SimpleNumberDialog(this);
					exprDialog.setTitle(getString(R.string.register_expr));
					exprDialog.setMaxValue(array.length - 1);
					exprDialog.setMinValue(0);
					exprDialog.setStrings(array);
					for (int i = 0; i < array.length; i++) {
						if (array[i].equals(array[mUserBasic.getExprid()])) {
							exprDialog.setValue(i);
							index = i;
						}
					}
					exprDialog.setValue(index);
					exprDialog.setOnValueChangeListener(new OnValueChangeListener() {
						@Override
						public void onValueChange(NumberPicker picker, int oldVal,
								int newVal) {
							mUserBasic.setExprid(newVal);
						}
					});
					exprDialog.setOnCancelListener(new OnCancelListener() {
	
						@Override
						public void onCancel(DialogInterface dialog) {
							if (mUserBasic.getExprid() != -1){
								mExprLayout.getFormContent().setTextColor(0xff000000);
								mExprLayout.setFormContent(array[mUserBasic.getExprid()]);
								
								mPostExpr = mUserBasic.getExprid();
								isShowing = false;
							}
						}
					});
					exprDialog.show();
				}
				break;
			case R.id.location:
				intent = new Intent(PersonalBasicInfoActivity.this,
						RegisterResidentSearchActivity.class);
				
				startActivityForResult(intent, 101);
				
				break;
			case R.id.email:
				mEmailLayout.getInfoEdit().setInputType(
						InputType.TYPE_CLASS_TEXT
								| InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
				mEmailLayout.setEdit(true);
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
				
				if(mPopupWindow.isShowing())
					mPopupWindow.dismiss();
				break;
			case R.id.popup_base_imgs:
				intent = new Intent(this, PhotoPickerActivity.class);
				intent.putExtra(GlobalConstants.PHOTO_DOOR,
						GlobalConstants.PHOTO_MODIFY_PROFILE);
				startActivity(intent);
				if(mPopupWindow.isShowing())
					mPopupWindow.dismiss();
				break;
				
			default:
				break;
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
		 * @throws FileNotFoundException 
		 */
		private void processExtraData() {
			String filePah = getIntent().getStringExtra("bitmap_path");
			
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
					if (realAvatar) {
						mRegisterEventLogic.uploadRealAvatar(filePah);
						mRealAvatar.getAvatar().setImageBitmap(bitmap);
					} else {
						mRegisterEventLogic.uploadAvatar(filePah);
						mNickAvatar.setImageBitmap(bitmap);
					}
					loadingDialog = new LoadingDialog(this, R.style.loading_dialog);
					loadingDialog.setLoadingTipStr(getString(R.string.general_tips_uploading));
					loadingDialog.show();
				} else {
					Log.i(TAG, "Bitmap is null");
				}
				
				out.close();

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
	        
	        
		}
		
		
		@Override
		public void onEventMainThread(Message msg) {
			if (msg.obj instanceof InfoResult) {
				InfoResult infoResult = (InfoResult) msg.obj;
				switch (msg.what) {
					case R.id.profile_basic_info_set:
						if (infoResult.getMessage().getStatus() == 0) {
							Intent intent = new Intent(PersonalBasicInfoActivity.this, PersonalEditPageActivity.class);
							startActivity(intent);
						}
						if(loadingDialog.isShowing()){
							loadingDialog.dismiss();
						}
						VToast.show(this, infoResult.getMessage().getMsg());
						break;
					case R.id.register_upload_realavatar:
						if (infoResult.getMessage().getStatus() == 0) {
							// 此时会返回URL
							if(StringUtils.isNotEmpty(infoResult.getMessage().getRealavatar())){
								mPostRealAvatar = infoResult.getMessage().getRealavatar();
							}
						}
						if(loadingDialog.isShowing()){
							loadingDialog.dismiss();
						}
						VToast.show(this, infoResult.getMessage().getMsg());
						break;
					case R.id.register_upload_avatar:
						if (infoResult.getMessage().getStatus() == 0) {
							// 此时会返回URL
							if(StringUtils.isNotEmpty(infoResult.getMessage().getNickavatar())){
								mPostNickAvatar = infoResult.getMessage().getNickavatar();
							}
						}
						if(loadingDialog.isShowing()){
							loadingDialog.dismiss();
						}
						VToast.show(this,infoResult.getMessage().getMsg());
						break;
				}
			}else if(msg.obj instanceof VolleyError){
				VToast.show(this, getString(R.string.general_tips_network_unknow));
				if(loadingDialog.isShowing()){
					loadingDialog.dismiss();
				}
			}
		}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (RESULT_OK == resultCode) {
			switch (requestCode) {
				case 101:
					if (data != null) {
						this.mLocationLayout.getFormContent().setText(data.getStringExtra(RegisterResidentSearchActivity.BACKDATA_KEY));
						// 赋新值
						mPostCities = data.getStringExtra(RegisterResidentSearchActivity.BACKDATA_KEY);
					}
					break;
					
				case GlobalConstants.CAMERA_CAPTURE:
					Intent intent = new Intent(PersonalBasicInfoActivity.this,ClipImageActivity.class);
					intent.putExtra(GlobalConstants.CLIP_PHOTO_URL, mCurrentPhotoPath);
					intent.putExtra(GlobalConstants.PHOTO_DOOR, GlobalConstants.PHOTO_MODIFY_PROFILE);
					startActivity(intent);
					break;
				case 102:
					// 102 选择虚拟头像
					if (data != null) {
						mPostNickAvatar = data.getStringExtra(RegisterBaseInfoActivity.CHOOSE_AVATAR);
						if (mPostNickAvatar != null) {
							Picasso.with(this).load(mPostNickAvatar).placeholder(R.drawable.general_user_avatar).into(mNickAvatar);
						}
					}
					break;
				default:
					break;
			}
		}

	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		if (group == mGender) {
			if (checkedId == R.id.button_one) {
				// 这里的数据会保存在数据库里--性别男--gender = 1
//				gender = 1;
				mPostGender = 1;
			} else if (checkedId == R.id.button_two) {
				// 这里的数据会保存在数据库里--性别女--gender = 0
//				gender = 0;
				mPostGender = 0;
			}
		}
	}

}
