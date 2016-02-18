package com.xiaobukuaipao.vzhi.register;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

import com.xiaobukuaipao.vzhi.AppActivityManager;
import com.xiaobukuaipao.vzhi.BaseFragmentActivity;
import com.xiaobukuaipao.vzhi.R;
import com.xiaobukuaipao.vzhi.util.GlobalConstants;
import com.xiaobukuaipao.vzhi.util.Logcat;
import com.xiaobukuaipao.vzhi.view.ClipImageView;

/**
 * 裁剪图片
 *
 */
public class ImageClipActivity extends BaseFragmentActivity {
	
	public static final String TAG = ImageClipActivity.class.getSimpleName();
	
	private ClipImageView imageView;
	private Bundle bundle = null;
	private String clipImageurl = null;

	private View mRotate;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clip_image);

        setHeaderMenuByLeft(this);
        setHeaderMenuByRight(R.string.general_save);
        
        onClickListenerBySaveDataAndRedirectActivity(R.id.menu_bar_right);
        findViews();
        getDatas();
    }        
	
	private void findViews() {
		imageView = (ClipImageView) findViewById(R.id.src_pic);
		mRotate = findViewById(R.id.rotate);
		mRotate.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				imageView.rotate(90);
			}
		});
	}
	
	private void getDatas() {
		bundle = getIntent().getBundleExtra(GlobalConstants.CLIP_PHOTO);
		clipImageurl = getIntent().getStringExtra(GlobalConstants.CLIP_PHOTO_URL);
		Logcat.d("@@@", "ClipImageActivity:" + clipImageurl);
		// 获取相机返回的数据，并转换为Bitmap图片格式
		if (bundle != null) {
			Log.i(TAG, "bundle != null");
			Bitmap bitmap = (Bitmap) bundle.getParcelable("data");
			imageView.setImageBitmap(bitmap);
		} else {
		}
		
		if (clipImageurl != null) {
			// Picasso.with(this).load(new File(clipImageurl)).into(imageView);
			loadImage(clipImageurl,  imageView);
		}
	}
	//首先获取图片被旋转的角度
	public static int readPictureDegree(String imagePath) {  
	    int imageDegree = 0;  
	    try {  
	        ExifInterface exifInterface = new ExifInterface(imagePath);  
	        int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);  
	        switch (orientation) {  
	        case ExifInterface.ORIENTATION_ROTATE_90:  
	            imageDegree = 90;  
	            break;  
	        case ExifInterface.ORIENTATION_ROTATE_180:  
	            imageDegree = 180;  
	            break;  
	        case ExifInterface.ORIENTATION_ROTATE_270:  
	            imageDegree = 270;  
	            break;  
	        }  
	    } catch (IOException e) {  
	        e.printStackTrace();  
	    }  
	    return imageDegree;  
	}
	
	public static Bitmap rotaingImageView(int angle, Bitmap mBitmap) {  
		  
	    Matrix matrix = new Matrix();  
	    matrix.postRotate(angle);  
	  
	    Bitmap b = Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(), mBitmap.getHeight(), matrix, true);  
	  
	    if (b != null && b != mBitmap) {  
	        mBitmap.recycle();  
	        b = mBitmap;  
	    }  
	    return mBitmap;  
	}  
	
	void loadImage(final String path, final ImageView imageView)
	{
		
		
		
		ImageSize imageSize = getImageViewWidth(imageView);
		
		int reqWidth = imageSize.width;
		int reqHeight = imageSize.height;
		
		Log.i(TAG, "reqWidth : " + reqWidth);
		Log.i(TAG, "reqHeight : " + reqHeight);
		
		Bitmap bm = decodeSampledBitmapFromResource(path, reqWidth,reqHeight);
		if (bm != null) {
			imageView.setImageBitmap(bm);
		}
	}
	
	/**
	 * 根据ImageView获得适当的压缩的宽和高
	 * 
	 * @param imageView
	 * @return
	 */
	private ImageSize getImageViewWidth(ImageView imageView)
	{
		ImageSize imageSize = new ImageSize();
		final DisplayMetrics displayMetrics = imageView.getContext()
				.getResources().getDisplayMetrics();
		final LayoutParams params = imageView.getLayoutParams();

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
	private static int getImageViewFieldValue(Object object, String fieldName)
	{
		int value = 0;
		try
		{
			Field field = ImageView.class.getDeclaredField(fieldName);
			field.setAccessible(true);
			int fieldValue = (Integer) field.get(object);
			if (fieldValue > 0 && fieldValue < Integer.MAX_VALUE)
			{
				value = fieldValue;
			}
		} catch (Exception e)
		{
		}
		return value;
	}
	
	private Bitmap decodeSampledBitmapFromResource(String pathName,
			int reqWidth, int reqHeight) {
		Bitmap bitmap = null;
		// 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(pathName, options);
		// 调用上面定义的方法计算inSampleSize值
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);
		
		Log.i(TAG, "inSampleSize : " + options.inSampleSize);
		// 使用获取到的inSampleSize值再次解析图片
		options.inJustDecodeBounds = false;
		
		try {
			bitmap = BitmapFactory.decodeFile(pathName, options);
			// 某些手机照完照片 ，成像的时候会导致图片旋转
			bitmap = rotaingImageView(readPictureDegree(pathName), bitmap);
		} catch (OutOfMemoryError e) {
			System.gc();
			e.printStackTrace();
			if (bitmap != null && bitmap.isRecycled()) {
				bitmap.recycle();
				bitmap = null;
			}
		}

		return bitmap;
	}
	
	private int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight)
	{
		// 源图片的宽度
		int width = options.outWidth;
		int height = options.outHeight;
		int inSampleSize = 1;

		if (width > reqWidth && height > reqHeight)
		{
			// 计算出实际宽度和目标宽度的比率
			int widthRatio = Math.round((float) width / (float) reqWidth);
			int heightRatio = Math.round((float) width / (float) reqWidth);
			inSampleSize = Math.max(widthRatio, heightRatio);
		}
		return inSampleSize;
	}
	
	@Override
    public void confirmNextAction() {
		// 在这里执行保存裁剪后的图片的操作
		Bitmap bitmap = imageView.clip();
		
		String sDir = Environment.getExternalStorageDirectory() +  "/xiaobukuaipao/picture/avatar";
		File destDir = new File(sDir);
		if (!destDir.exists()) {
		   destDir.mkdirs();
		}
		
		String bitmapPath = sDir +  "/personal_avatar.jpg";
		File bitmapFile = new File(bitmapPath);
		if(!bitmapFile.exists()) {
			try {
				bitmapFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		
		// 先将返回后的图片保存到本地
		try {       
			FileOutputStream out = new FileOutputStream(bitmapFile);       
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
			out.flush();
			out.close();
		} catch (Exception e) {       
			e.printStackTrace();
		}
		
		Intent intent = getIntent();
		//白领基本信息
		intent = new Intent(this, PurposeActivity.class);
		intent.putExtra("bitmap_path", bitmapPath);
		setResult(RESULT_OK, intent);
		bitmap.recycle();
		AppActivityManager.getInstance().finishActivity(ImageClipActivity.this);
    }
	
	private class ImageSize
	{
		int width;
		int height;
	}
	
}
