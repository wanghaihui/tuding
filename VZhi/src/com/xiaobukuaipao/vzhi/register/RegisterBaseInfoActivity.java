package com.xiaobukuaipao.vzhi.register;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.android.volley.toolbox.Volley;
import com.xiaobukuaipao.vzhi.R;
import com.xiaobukuaipao.vzhi.cache.CommonBitmapMemoryCache;
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

public class RegisterBaseInfoActivity extends RegisterWrapActivity implements
		OnClickListener, OnCheckedChangeListener {

	public static final String CHOOSE_AVATAR = "choose_avatar";

	public RoundedImageView mAvatar;
	public RoundedImageView mLastAvatar;
	public LayoutParams mLayoutParams;

	public FormItemByLineLayout mNickName;
	public FormItemByLineView mAge, mLocation;	

	private PopupWindow mPopupWindow;
	private LinearLayout mPopupLayout;

	// 当前选择的头像--实际上代表url
	private String mCurrentChooseAvatar = null;
	private int gender = 1;
	private int age = -1;
	private String city;

	// 请求队列
	private RequestQueue mQueue;
	private ImageLoader mImageLoader;
	private ImageListener mListener;

	// 保存数据库

	private TextView mGenderTitle;
	// 横向RadioGroup性别选择框
	private SegmentedRadioGroup mGender;

	private Bundle bundle;

	private Handler mHandler = new Handler();

//	private List<String> mAvatarsMList;
//	private List<String> mAvatarsFList;

	private long mAtime = 200;

	/**
	 * 下一步
	 */
	private Runnable mNextRunnable = new Runnable() {

		@Override
		public void run() {
			mRegisterEventLogic.cancel(R.id.register_set_iden);
			mRegisterEventLogic.setIdentity(identity);
			mRegisterEventLogic.setBasicInfo(mCurrentChooseAvatar, mNickName
					.getFormContent().getText().toString(), gender, age, city);
		}
	};

	private LoadingDialog loadingDialog;

	private View contentView;
	private int identity;

	@Override
	public void initUIAndData() {
		identity = getIntent().getIntExtra(GlobalConstants.IDENTITY, -1);
		
		contentView = View.inflate(this, R.layout.activity_register_baseinfo, null);
		setContentView(contentView);
		setHeaderMenuByLeft(this);
		setHeaderMenuByCenterTitle(R.string.register_baseinfo);
		// 设置右边走的菜单
		setHeaderMenuByRight(R.string.general_tips_next);

		mPopupLayout = (LinearLayout) View.inflate(this,R.layout.popup_baseinfo_head, null);

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

		// 网络请求
		mQueue = Volley.newRequestQueue(this);
		mImageLoader = new ImageLoader(mQueue, new CommonBitmapMemoryCache());

//		mAvatarsMList = new ArrayList<String>();
//		mAvatarsFList = new ArrayList<String>();

		mGenderTitle = (TextView) findViewById(R.id.info_gender_title);
		mGenderTitle.setText(getResources().getString(R.string.register_gender));
		mGender = (SegmentedRadioGroup) findViewById(R.id.segment_text);
		mGender.setOnCheckedChangeListener(this);

		mAvatar = (RoundedImageView) findViewById(R.id.baseinfo_user_head);
		mAvatar.setScaleType(ScaleType.CENTER_INSIDE);
		mAvatar.setCornerRadius(getResources().getDimension(
				R.dimen.image_radius));
		mAvatar.setBorderColor(getResources().getColor(R.color.white));
		mAvatar.mutateBackground(true);
		mAvatar.setOval(true);

		mAvatar.setImageResource(R.drawable.general_user_avatar);
		mAvatar.setAdjustViewBounds(true);
		mAvatar.setMaxHeight(150);
		mAvatar.setMaxWidth(150);

		onClickListenerBySaveDataAndRedirectActivity(R.id.menu_bar_right);

		mAvatar.setOnClickListener(this);
		mPopupLayout.findViewById(R.id.popup_base_takephoto).setOnClickListener(this);
		mPopupLayout.findViewById(R.id.popup_base_imgs).setOnClickListener(this);
		mPopupLayout.findViewById(R.id.popup_base_avatar).setOnClickListener(this);
		mPopupLayout.findViewById(R.id.popup_base_cancel).setOnClickListener(this);

		mNickName = (FormItemByLineLayout) findViewById(R.id.baseinfo_nickname);
		mAge = (FormItemByLineView) findViewById(R.id.baseinfo_age);
		mLocation = (FormItemByLineView) findViewById(R.id.baseinfo_location);

		mNickName.getFormLabel().setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
		mAge.getFormLabel().setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
		mLocation.getFormLabel().setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);

		mNickName.setOnClickListener(this);
		mGender.setOnClickListener(this);
		mAge.setOnClickListener(this);
		mLocation.setOnClickListener(this);

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
			Bitmap bitmap = BitmapFactory.decodeByteArray(bis, 0, bis.length);
			
			if (bitmap != null) {
				// 在这里将选择的图片传到服务器上
				mRegisterEventLogic.uploadAvatar(filePah);
				loadingDialog = new LoadingDialog(this, R.style.loading_dialog);
				loadingDialog.setLoadingTipStr(getString(R.string.general_tips_uploading));
				
				mAvatar.setImageBitmap(bitmap);
				findViewById(R.id.icon_plus).setVisibility(View.GONE);
			}
			
			out.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
        
        
	}

	@Override
	public void confirmNextAction() {

		if (StringUtils.isEmpty(mNickName.getInfoEdit().getText().toString())) {
			VToast.show(this, getString(R.string.general_tips_fill_tips, mNickName.getFormLabelText()));
			return;
		}

		if (age == -1) {
			VToast.show(this, getString(R.string.general_tips_fill_tips, mAge.getFormLabelText()));
			return;
		}

		if (StringUtils.isEmpty(city)) {
			VToast.show(this, getString(R.string.general_tips_choose_tips, mLocation.getFormLabelText()));
			return;
		}

		if (mCurrentChooseAvatar == null) {
			mLastAvatar = new RoundedImageView(this);
			mLastAvatar.setScaleType(ScaleType.FIT_CENTER);
			mLastAvatar.setLayoutParams(new LayoutParams(100, 100));
			mLastAvatar.setOval(true);

			mPopupWindow.setContentView(mLastAvatar);
			mPopupWindow.setAnimationStyle(android.R.style.Animation_Toast);
			mPopupWindow.setHeight(200);
			mPopupWindow.setWidth(200);
			mPopupWindow.setOutsideTouchable(false);
			mPopupWindow.setFocusable(false);
			mPopupWindow.showAtLocation(getWindow().getDecorView().getRootView(), Gravity.CENTER, 0, 0);
			WindowManager.LayoutParams lp = getWindow().getAttributes();
			lp.alpha = 0.5f;
			getWindow().setAttributes(lp);

			// Get Avatars
			mRegisterEventLogic.getAvatars();

		} else {
			mHandler.removeCallbacks(mNextRunnable);
			mHandler.postDelayed(mNextRunnable, mAtime);
		}

	}

	// 执行网络请求
	public void onEventMainThread(Message msg) {
		if (msg.obj instanceof InfoResult) {
			InfoResult infoResult = (InfoResult) msg.obj;
			switch (msg.what) {
			case R.id.register_set_base:
				if (infoResult.getMessage().getStatus() == 0) {
					// 在这里将头像URL，昵称，性别，年龄，所在地，保存到数据库中
					ContentValues values = new ContentValues();
					// 匿名头像
					values.put(UserInfoTable.COLUMN_AVATAR,mCurrentChooseAvatar);
					values.put(UserInfoTable.COLUMN_NICKNAME, mNickName.getFormContent().getText().toString());
					values.put(UserInfoTable.COLUMN_GENDER, gender);
					values.put(UserInfoTable.COLUMN_AGE, age);
					values.put(UserInfoTable.COLUMN_LOCATION, city);
					getContentResolver().update(GeneralContentProvider.USERINFO_CONTENT_URI,
							values, null, null);

					Intent intent = new Intent(RegisterBaseInfoActivity.this, JobObjectiveActivity.class);
					startActivity(intent);
				}
				
				VToast.show(this, getString(R.string.register_upload_success));
				break;

			// 在这里执行拍照和选择的图片进行上传
			case R.id.register_upload_avatar:
				if (infoResult.getMessage().getStatus() == 0) {
					mCurrentChooseAvatar = infoResult.getMessage().getNickavatar();
					mHandler.postDelayed(mNextRunnable, mAtime);
				}
				if(loadingDialog.isShowing()){
					loadingDialog.dismiss();
				}
				VToast.show(this,infoResult.getMessage().getMsg());
				break;
//			case R.id.register_get_avatar:
//				if (msg.obj instanceof InfoResult) {
//					mAvatarsMList = new ArrayList<String>();
//					mAvatarsFList = new ArrayList<String>();
//					// 女人
//					JSONObject jsonObject = (JSONObject) JSONObject.parse(infoResult.getResult());
//					if(jsonObject != null){
//						JSONArray mWmArray = jsonObject.getJSONObject(GlobalConstants.JSON_AVATARS).getJSONArray("0");
//						if(mWmArray != null){
//							for (int i = 0; i < mWmArray.size(); i++) {
//								mAvatarsFList.add(mWmArray.getString(i));
//							}
//						}
//						// 男人
//						JSONArray mMArray = jsonObject.getJSONObject(GlobalConstants.JSON_AVATARS).getJSONArray("1");
//						if(mMArray != null){
//							for (int i = 0; i < mMArray.size(); i++) {
//								mAvatarsMList.add(mMArray.getString(i));
//							}
//						}
//					}
//				} else if (msg.obj instanceof VolleyError) {
//					// 可提示网络错误，具体类型有TimeoutError ServerError
//				}
//				break;
			}
		}else if(msg.obj instanceof VolleyError){
			VToast.show(this, getString(R.string.general_tips_network_unknow));
			
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
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
			try {
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				startActivityForResult(intent, GlobalConstants.CAMERA_CAPTURE);
			} catch (ActivityNotFoundException anfe) {
				// display an error message
				String errorMessage = "设备不支持拍照";
				VToast.show(this,errorMessage);
			}
			break;

		case R.id.popup_base_imgs:
			Intent intent = new Intent(RegisterBaseInfoActivity.this,
					PhotoPickerActivity.class);
			intent.putExtra(GlobalConstants.PHOTO_DOOR,
					GlobalConstants.PHOTO_WHITECOLLAR);
			startActivity(intent);
			break;

		case R.id.popup_base_avatar:
			startActivityForResult(new Intent(RegisterBaseInfoActivity.this,
					RegisterBaseInfoAvatarActivity.class), 101);
			break;

		case R.id.baseinfo_nickname:
			// 在这里执行后，NickName保存在mNickName.getFormContent()
			mNickName.setEdit(true);
			break;
		case R.id.baseinfo_age:
			SimpleNumberDialog dialog = new SimpleNumberDialog(
					RegisterBaseInfoActivity.this);
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
		case R.id.baseinfo_location:
			startActivityForResult(new Intent(this,RegisterResidentSearchActivity.class), 102);
			break;
		default:
			break;
		}
	}

	@Override
	protected void onPause() {
		if (mPopupWindow.isShowing())
			mPopupWindow.dismiss();
		super.onPause();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);
		if (data != null)
			if (resultCode == RESULT_OK) {

				if (requestCode == 101) {
					// 101 选择虚拟头像
					mCurrentChooseAvatar = data.getStringExtra(CHOOSE_AVATAR);
					if (mCurrentChooseAvatar != null) {
						mListener = ImageLoader.getImageListener(mAvatar, R.drawable.general_user_avatar, R.drawable.general_user_avatar);
						mImageLoader.get(mCurrentChooseAvatar, mListener);
						findViewById(R.id.icon_plus).setVisibility(View.GONE);
					}
				} else if (requestCode == 102) {
					city = data
							.getStringExtra(RegisterResidentSearchActivity.BACKDATA_KEY);
					if (StringUtils.isNotEmpty(city)) {
						mLocation.setFormContent(city);
					}
				} else if (requestCode == GlobalConstants.CAMERA_CAPTURE) {
					// get the Uri for the captured image
					bundle = data.getExtras();

					Intent intent = new Intent(RegisterBaseInfoActivity.this,
							ClipImageActivity.class);
					intent.putExtra(GlobalConstants.PHOTO_DOOR,
							GlobalConstants.PHOTO_WHITECOLLAR);
					intent.putExtra(GlobalConstants.CLIP_PHOTO, bundle);
					startActivity(intent);

				}
			}
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
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
}
