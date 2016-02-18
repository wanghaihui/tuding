package com.xiaobukuaipao.vzhi.register;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;

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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout.LayoutParams;

import com.xiaobukuaipao.vzhi.AppActivityManager;
import com.xiaobukuaipao.vzhi.MainRecruitActivity;
import com.xiaobukuaipao.vzhi.R;
import com.xiaobukuaipao.vzhi.contentprovider.GeneralContentProvider;
import com.xiaobukuaipao.vzhi.database.UserInfoTable;
import com.xiaobukuaipao.vzhi.event.InfoResult;
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
import com.xiaobukuaipao.vzhi.wrap.RegisterWrapActivity;

/**
 *  动画
 *  
 *  1.点击一次动画即开始，点击第二次无法触发动画 重置所有结束动画状态
 *  2.点击返回关闭动画开始，只有当关闭动画结束才能触发当前页面的finish 重置所有开始动画　状态
 *  3.点击动画开始　第二层的页面就开始加载
 */
public class PurposeActivity extends RegisterWrapActivity {

	private RoundedImageView mAvatar;
	
	private FormItemByLineLayout mRealName;
	private FormItemByLineView mAge, mLocation;	

	private PopupWindow mPopupWindow;
	private LinearLayout mPopupLayout;

	// 当前选择的头像--实际上代表url
	private String mCurrentChooseAvatar = null;
	private int gender = 1;
	private int age = -1;
	private String city;

	// 横向RadioGroup性别选择框
	private SegmentedRadioGroup mGender;

	private LoadingDialog loadingDialog;
//	/**
//	 * 延时500ms
//	 */
//	private long delayTime = 500;
//	/**
//	 * 屏蔽点击
//	 */
//	private boolean block =  true;
	
	
	// 文件名
	private String mCurrentPhotoPath;
	
	@SuppressLint("SimpleDateFormat")
	private File createImageFile() throws IOException {
		// Create an image file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		String imageFileName = "JPEG_" + timeStamp;
		File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
		File image = File.createTempFile(imageFileName, ".jpg", storageDir);
		
		mCurrentPhotoPath = image.getAbsolutePath();
		return image;
	}
	
	@Override
	public void initUIAndData() {
		setContentView(R.layout.activity_purpose);
		setHeaderMenuByLeft(this);
		setHeaderMenuByCenterTitle(R.string.title_hr_info);
		setHeaderMenuByRight(R.string.register_hr_baseinfo_finished).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(StringUtils.isEmpty(mCurrentChooseAvatar)){
					VToast.show(PurposeActivity.this, getString(R.string.general_tips_fill_tips,getString(R.string.hr_info_portrait)));
					return;
				}
				if(StringUtils.isEmpty(mRealName.getFormContentText())){
					VToast.show(PurposeActivity.this, getString(R.string.general_tips_fill_tips,mRealName.getFormLabelText()));
					return;
				}
				if(StringUtils.isEmpty(mAge.getFormContentText())){
					VToast.show(PurposeActivity.this, getString(R.string.general_tips_fill_tips,mAge.getFormLabelText()));
					return;
				}
				if(StringUtils.isEmpty(mLocation.getFormContentText())){
					VToast.show(PurposeActivity.this, getString(R.string.general_tips_fill_tips,mLocation.getFormLabelText()));
					return;
				}
				mProfileEventLogic.cancel(R.id.profile_basic_info_set);
				mProfileEventLogic.setBasicInfo(mCurrentChooseAvatar, "", mRealName.getFormContentText(), "", gender, age, mLocation.getFormContentText(), -1, "", "",-1);
			}
		});
		
		
//		mLayout =  findViewById(R.id.layout);
		mAvatar = (RoundedImageView) findViewById(R.id.hr_info_protrait);
		mAvatar.setScaleType(ScaleType.CENTER_INSIDE);
		mAvatar.setCornerRadius(getResources().getDimension(R.dimen.image_radius));
		mAvatar.setBorderColor(getResources().getColor(R.color.white));
		mAvatar.mutateBackground(true);
		mAvatar.setOval(true);
		mAvatar.setAdjustViewBounds(true);
		mAvatar.setMaxHeight(150);
		mAvatar.setMaxWidth(150);
		
		mRealName = (FormItemByLineLayout) findViewById(R.id.hr_info_name);
		mAge = (FormItemByLineView)findViewById(R.id.hr_info_age);
		mLocation = (FormItemByLineView)findViewById(R.id.hr_info_city);
		mGender = (SegmentedRadioGroup) findViewById(R.id.hr_segment_text);
		mRealName.getInfoEdit().addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				
			}
		});
		
		mGender.setOnCheckedChangeListener(onCheckListener);
		mRealName.setOnClickListener(onClickListener);
		mAge.setOnClickListener(onClickListener);
		mLocation.setOnClickListener(onClickListener);
		mAvatar.setOnClickListener(onClickListener);
		
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
		
		mPopupLayout.findViewById(R.id.popup_base_takephoto).setOnClickListener(onClickListener);
		mPopupLayout.findViewById(R.id.popup_base_imgs).setOnClickListener(onClickListener);
		mPopupLayout.findViewById(R.id.popup_base_cancel).setOnClickListener(onClickListener);
		
//		StateListDrawable upDrawable = new StateListDrawable();
//		//“-”号，当XML的设定是false时，就需要使用资源符号的负值来设定
//        //Pressed
//        upDrawable.addState(new int[]{ android.R.attr.state_pressed},getResources().getDrawable(R.drawable.purpose_up_pressed));
//        upDrawable.addState(new int[]{ -android.R.attr.state_pressed}, getResources().getDrawable(R.drawable.purpose_up));
//        
//        StateListDrawable downDrawable = new StateListDrawable();
//		//“-”号，当XML的设定是false时，就需要使用资源符号的负值来设定
//        //Pressed
//        downDrawable.addState(new int[]{ android.R.attr.state_pressed}, getResources().getDrawable(R.drawable.purpose_down_pressed));
//        downDrawable.addState(new int[]{ -android.R.attr.state_pressed}, getResources().getDrawable(R.drawable.purpose_down));
      
        
//		up = (ImageView) findViewById(R.id.purpose_up_img);
//		down = (ImageView) findViewById(R.id.purpose_down_img);
//		
//		up.setImageDrawable(upDrawable);
//		down.setImageDrawable(downDrawable);
//		
//		
//		new Handler().postDelayed(new Runnable() {
//			
//			@Override
//			public void run() {
//				block = false;
//			}
//		}, delayTime);
	}
	
//	@Override
//	public boolean dispatchTouchEvent(MotionEvent event) {
//
//		if (block) {
//			return true;
//		}
//		
//		if(!firstTips){
//			if(isFinish){
//				return super.dispatchTouchEvent(event);
//			}else{
//				return true;//屏蔽了下面的事件
//			}
//		}
////		Logcat.d("@@@", "dispatchTouchEvent:" + event.getAction());
//		if(event.getAction()  == MotionEvent.ACTION_DOWN){
//			//已知两点 求直线方程  x1,y1 x2,y2  y1 = k * x1 + b ,  y2 = k * x2 + b 
//			float x = event.getX();
//			float y = event.getY();
//			
//			int width = mLayout.getWidth();
//			int height = mLayout.getHeight();
//			
//			float y1 = height * 0.377f;//第一个点的y
//			
//			//y2 = k * width +  height * 0.589f;
//			float b = height * 0.589f;// 根据第二个点 得到b
//			float k = (y1 - b) / width;
//			float y2 = k * x + b;
//			 if(y > y2){
//				// 招聘者
//				 identity = 1;
//				 down.setPressed(true);
//				 MobclickAgent.onEvent(PurposeActivity.this,"djzrc");
//			 }else{
//				// 求职者
//				 identity = 0;
//				 up.setPressed(true);
//				 MobclickAgent.onEvent(PurposeActivity.this, "djzgz");
//			 }
//		}else if(event.getAction()  == MotionEvent.ACTION_UP){
//			up.setPressed(false);
//			down.setPressed(false);
//			firstTips = false;
//			initIdentityUIAndData();
//
//
//			Animation downAnim = AnimationUtils.loadAnimation(mLayout.getContext(), R.anim.slide_out_to_bottom_for_purpose);
//			Animation upAnim = AnimationUtils.loadAnimation(mLayout.getContext(), R.anim.slide_out_to_top_for_purpose);
//			up.startAnimation(upAnim);
//			down.startAnimation(downAnim);
//			downAnim.setAnimationListener(new AnimationListener() {
//				
//				@Override
//				public void onAnimationStart(Animation animation) {
//					isFinish = false;
//				}
//				
//				@Override
//				public void onAnimationRepeat(Animation animation) {
//					
//				}
//				
//				@Override
//				public void onAnimationEnd(Animation animation) {
//					findViewById(R.id.layout).setVisibility(View.GONE);
//					isFinish = true;
//					secondTips = true;
//				}
//			});
//		}
//		
//		return true;
//	}
	
//	public Bitmap readBitmapAutoSize(String filePath, int outWidth,
//			int outHeight) {
//		// outWidth和outHeight是目标图片的最大宽度和高度，用作限制
//		FileInputStream fs = null;
//		BufferedInputStream bs = null;
//		try {
//			fs = new FileInputStream(filePath);
//			bs = new BufferedInputStream(fs);
//			BitmapFactory.Options options = setBitmapOption(filePath, outWidth,
//					outHeight);
//			return BitmapFactory.decodeStream(bs, null, options);
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			try {
//				bs.close();
//				fs.close();
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//		return null;
//	}
	
//	private BitmapFactory.Options setBitmapOption(String file, int width, int height) {  
//        BitmapFactory.Options opt = new BitmapFactory.Options();  
//        BitmapFactory.Options opts = new BitmapFactory.Options();  
//        //设置图片的DPI为当前手机的屏幕dpi  
//        opts.inTargetDensity = getResources().getDisplayMetrics().densityDpi;    
//        opts.inScaled = true;  
//        
//        opt.inJustDecodeBounds = true;            
//                //设置只是解码图片的边距，此操作目的是度量图片的实际宽度和高度  
//        BitmapFactory.decodeFile(file, opt);  
//  
//        int outWidth = opt.outWidth; //获得图片的实际高和宽  
//        int outHeight = opt.outHeight;  
//        opt.inDither = false;  
//        opt.inPreferredConfig = Bitmap.Config.RGB_565;      
//                //设置加载图片的颜色数为16bit，默认是RGB_8888，表示24bit颜色和透明通道，但一般用不上  
//        opt.inSampleSize = 1;                            
//                //设置缩放比,1表示原比例，2表示原来的四分之一....  
//                //计算缩放比  
//        if (outWidth != 0 && outHeight != 0 && width != 0 && height != 0) {  
//            int sampleSize = (outWidth / width + outHeight / height) / 2;  
//            opt.inSampleSize = sampleSize;  
//        }  
//  
//        opt.inJustDecodeBounds = false;//最后把标志复原  
//        return opt;  
//    }  
	
	@Override
	public void onLowMemory() {
		super.onLowMemory();
		System.gc();
		
	}

	public void onEventMainThread(Message msg) {
		if (msg.obj instanceof InfoResult) {
			InfoResult infoResult = (InfoResult) msg.obj;
			switch (msg.what) {
			case R.id.profile_basic_info_set:
				if (infoResult.getMessage().getStatus() == 0) {
//					if (identity == 0) {
						// 选择身份后，白领进入的是先添加一段工作经历
//						Intent intent = new Intent(PurposeActivity.this,JobExperienceListActivity.class);
//						intent.putExtra("personal_experience_add", true);
//						intent.putExtra("personal_experience_register", true);
//						startActivity(intent);
//						Intent intent = new Intent(PurposeActivity.this,JobObjectiveActivity.class);
//						startActivity(intent);

//					} else if(identity == 1) {
//						Intent intent = new Intent(PurposeActivity.this,CompanyInfoActivity.class);
//						startActivity(intent);
//					}
//					SharedPreferences sp = this.getSharedPreferences("tuding_uid", Context.MODE_PRIVATE);
//					sp.edit().putBoolean(GlobalConstants.INTENTION_COMPLETE, false).commit();
					
					Intent intent = new Intent(PurposeActivity.this, MainRecruitActivity.class);
					startActivity(intent);
					AppActivityManager.getInstance().finishAllActivity();
					// 在这里将头像URL，昵称，性别，年龄，所在地，保存到数据库中
					ContentValues values = new ContentValues();
					// 匿名头像
					values.put(UserInfoTable.COLUMN_AVATAR, mCurrentChooseAvatar);
					values.put(UserInfoTable.COLUMN_GENDER, gender);
//					values.put(UserInfoTable.COLUMN_IDENTITY, identity);
					values.put(UserInfoTable.COLUMN_REALAVATAR, mCurrentChooseAvatar);
					values.put(UserInfoTable.COLUMN_AGE, age);
					values.put(UserInfoTable.COLUMN_LOCATION, city);
					getContentResolver().update(GeneralContentProvider.USERINFO_CONTENT_URI, values, null, null);
				} else {
					VToast.show(this, infoResult.getMessage().getMsg());
				}
				break;
			case R.id.register_upload_realavatar:
				
				if(infoResult.getMessage().getStatus() == 0){
					mCurrentChooseAvatar = infoResult.getMessage().getRealavatar();
				} else {
					
				}
				
				VToast.show(this, infoResult.getMessage().getMsg());
				
				loadingDialog.dismiss();
				break;
			}
		} else {
			Log.i(TAG, "msg.obj is not instanceof InfoResult");
		}
	}

//	@Override
//	public void onBackPressed() {
//		if(isFinish){
//			if(secondTips){
//				secondTips = false;
//				findViewById(R.id.layout).setVisibility(View.VISIBLE);
//				
//				View up = findViewById(R.id.purpose_up_img);
//				View down = findViewById(R.id.purpose_down_img);
//				Animation downAnim = AnimationUtils.loadAnimation(this, R.anim.slide_in_from_bottom_for_purpose);
//				Animation upAnim = AnimationUtils.loadAnimation(this, R.anim.slide_in_from_top_for_purpose);
//				
//				up.startAnimation(upAnim);
//				down.startAnimation(downAnim);
//				downAnim.setAnimationListener(new AnimationListener() {
//					
//					@Override
//					public void onAnimationStart(Animation animation) {
//						isFinish = false;
//					}
//					
//					@Override
//					public void onAnimationRepeat(Animation animation) {
//						
//					}
//					
//					@Override
//					public void onAnimationEnd(Animation animation) {
//						isFinish = true;
//						firstTips = true;
//					}
//				});
//			}else{
//				super.onBackPressed();
//			}
//		}
//	}
	
	
//	private void initIdentityUIAndData() {
//		//初始化表单数据
//		mAvatar.setImageResource(R.drawable.general_user_avatar);
//		mRealName.reset();
//		mAge.reset();
//		mLocation.reset();
//		mGender.check(R.id.hr_button_one);
//		findViewById(R.id.hr_icon_plus).setVisibility(View.VISIBLE);
//		mCurrentChooseAvatar = "";
//		gender = 1;
//		age = -1;
//		city = "";
//	}
	
	
	private OnClickListener onClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
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
				Intent intent = new Intent(PurposeActivity.this, PhotoPickerActivity.class);
				intent.putExtra(GlobalConstants.PHOTO_DOOR, GlobalConstants.PHOTO_PROFILE);
				startActivityForResult(intent, GlobalConstants.CAMERA_CAPTURE);
				break;
			case R.id.hr_info_age:
				SimpleNumberDialog dialog = new SimpleNumberDialog(PurposeActivity.this);
				age = age == -1 ? 26 : age;
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
				// 设置年龄从26岁开始
				dialog.setOnCancelListener(new OnCancelListener() {

					@Override
					public void onCancel(DialogInterface dialog) {
						mAge.setFormContent(String.valueOf(age));
					}
				});
				dialog.show();
				break;
			case R.id.hr_info_city:
				startActivityForResult(new Intent(PurposeActivity.this,RegisterResidentSearchActivity.class), 102);
				break;
			case R.id.hr_info_name:
				mRealName.setEdit(true);
				break;
			default:
				break;
			}		
		}
	};
	
	private OnCheckedChangeListener onCheckListener = new OnCheckedChangeListener() {
		
		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			if (group == mGender) {
				if (checkedId == R.id.hr_button_one) {
					// 这里的数据会保存在数据库里--性别男--gender = 1
					gender = 1;
				} else if (checkedId == R.id.hr_button_two) {
					// 这里的数据会保存在数据库里--性别女--gender = 0
					gender = 0;
				}
			}
		}
	};
	
	@Override
	protected void onPause() {
		if (mPopupWindow.isShowing()) {
			mPopupWindow.dismiss();
		}
		super.onPause();
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
				Intent intent = new Intent(PurposeActivity.this, ClipImageActivity.class);
				intent.putExtra(GlobalConstants.CLIP_PHOTO_URL, mCurrentPhotoPath);
				intent.putExtra(GlobalConstants.PHOTO_DOOR, GlobalConstants.PHOTO_PROFILE);
				startActivityForResult(intent, 103);
			} else if(requestCode == 103){
				if (data != null) {
					setIntent(data);
					processExtraData();
				}
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
		
		if(StringUtils.isEmpty(filePah)) {
			return;
		}
		
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
			
			if (filePah != null) {
				// 在这里将选择的图片传到服务器上
				Log.i(TAG, "Purpose filePath : " + filePah);
				mRegisterEventLogic.uploadRealAvatar(filePah);
				
				loadingDialog = new LoadingDialog(this, R.style.loading_dialog);
				loadingDialog.setLoadingTipStr(getString(R.string.general_tips_uploading));
				loadingDialog.show();
				
				mAvatar.setImageBitmap(bitmap);
				findViewById(R.id.hr_icon_plus).setVisibility(View.GONE);
			}
			
			out.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	
	void loadImage(final String path, final ImageView imageView){
		ImageSize imageSize = getImageViewWidth(imageView);
		
		int reqWidth = imageSize.width;
		int reqHeight = imageSize.height;
		
		Bitmap bm = decodeSampledBitmapFromResource(path, reqWidth,reqHeight);
		imageView.setImageBitmap(bm);
	}
	
	/**
	 * 根据ImageView获得适当的压缩的宽和高
	 * 
	 * @param imageView
	 * @return
	 */
	private ImageSize getImageViewWidth(ImageView imageView){
		ImageSize imageSize = new ImageSize();
		final DisplayMetrics displayMetrics = imageView.getContext()
				.getResources().getDisplayMetrics();
		final android.view.ViewGroup.LayoutParams params = imageView.getLayoutParams();

		int width = params.width == LayoutParams.WRAP_CONTENT ? 0 : imageView
				.getWidth(); // Get actual image width
		if (width <= 0)
			width = params.width; // Get layout width parameter
		if (width <= 0)
			width = getImageViewFieldValue(imageView, "mMaxWidth"); // Check
																	// maxWidth
																	// parameter
		if (width <= 0)
			width = displayMetrics.widthPixels;
		int height = params.height == LayoutParams.WRAP_CONTENT ? 0 : imageView
				.getHeight(); // Get actual image height
		if (height <= 0)
			height = params.height; // Get layout height parameter
		if (height <= 0)
			height = getImageViewFieldValue(imageView, "mMaxHeight"); // Check
																		// maxHeight
																		// parameter
		if (height <= 0)
			height = displayMetrics.heightPixels;
		imageSize.width = width;
		imageSize.height = height;
		return imageSize;

	}
	
	/**
	 * 反射获得ImageView设置的最大宽度和高度
	 * 
	 * @param object
	 * @param fieldName
	 * @return
	 */
	private static int getImageViewFieldValue(Object object, String fieldName){
		int value = 0;
		try{
			Field field = ImageView.class.getDeclaredField(fieldName);
			field.setAccessible(true);
			int fieldValue = (Integer) field.get(object);
			if (fieldValue > 0 && fieldValue < Integer.MAX_VALUE)
			{
				value = fieldValue;
			}
		} catch (Exception e){
		}
		return value;
	}
	
	private Bitmap decodeSampledBitmapFromResource(String pathName,
			int reqWidth, int reqHeight){
		// 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(pathName, options);
		// 调用上面定义的方法计算inSampleSize值
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);
		// 使用获取到的inSampleSize值再次解析图片
		options.inJustDecodeBounds = false;
		Bitmap bitmap = BitmapFactory.decodeFile(pathName, options);

		return bitmap;
	}
	
	private int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight){
		// 源图片的宽度
		int width = options.outWidth;
		int height = options.outHeight;
		int inSampleSize = 1;

		if (width > reqWidth && height > reqHeight){
			// 计算出实际宽度和目标宽度的比率
			int widthRatio = Math.round((float) width / (float) reqWidth);
			int heightRatio = Math.round((float) width / (float) reqWidth);
			inSampleSize = Math.max(widthRatio, heightRatio);
		}
		return inSampleSize;
	}
	
	private class ImageSize{
		int width;
		int height;
	}
}
