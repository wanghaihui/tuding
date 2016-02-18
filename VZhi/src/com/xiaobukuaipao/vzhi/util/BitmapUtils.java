package com.xiaobukuaipao.vzhi.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;

public class BitmapUtils {

	public static Bitmap getBitmapFromUri(Context context, Uri uri)
	 {
		  try
		  {
			   // 读取uri所在的图片
			   Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
			   return bitmap;
		  } catch (Exception e) {
			  e.printStackTrace();
			  return null;
		  }
	 }
	
	/** 
	 * 根据图片字节数组，对图片可能进行二次采样，不致于加载过大图片出现内存溢出 
	 * @param bytes 
	 * @return 
	 */  
	public static Bitmap getBitmapByBytes(byte[] bytes){  
	      
	    //对于图片的二次采样,主要得到图片的宽与高  
	    int width = 0;  
	    int height = 0;  
	    int sampleSize = 1; //默认缩放为1  
	    BitmapFactory.Options options = new BitmapFactory.Options();  
	    options.inJustDecodeBounds = true;  //仅仅解码边缘区域  
	    //如果指定了inJustDecodeBounds，decodeByteArray将返回为空  
	    BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);  
	    //得到宽与高  
	    height = options.outHeight;  
	    width = options.outWidth;  
	  
	    //图片实际的宽与高，根据默认最大大小值，得到图片实际的缩放比例  
	    while ((height / sampleSize > 48)  
	            || (width / sampleSize > 48)) {  
	        sampleSize *= 2;  
	    }  
	  
	    //不再只加载图片实际边缘  
	    options.inJustDecodeBounds = false;  
	    //并且制定缩放比例  
	    options.inSampleSize = sampleSize;  
	    return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);  
	}
	
}
