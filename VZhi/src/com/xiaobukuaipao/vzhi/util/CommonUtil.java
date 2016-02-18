package com.xiaobukuaipao.vzhi.util;

import java.io.File;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;

import com.xiaobukuaipao.vzhi.im.IMConstants;


/**
 * 通用的工具类
 * @author haihui.wang
 *
 */
public class CommonUtil {
	
	/**
	 * 检测网络状态
	 * @param context
	 * @return
	 */
    public static boolean checkNetWorkStatus(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netinfo = cm.getActiveNetworkInfo();
        return (netinfo != null && netinfo.isConnected()) ? true : false;
    }
    
    /**
     * 获得当前的版本名--android:versionName
     * @param context
     * @return
     */
    public static int getAppVersionName(Context context) {
    	// context为当前Activity上下文
    	try {  
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);  
            return info.versionCode;  
        } catch (NameNotFoundException e) {  
            e.printStackTrace();  
        }  
        return 1;  
    }
    
    /**
     * 得到当前的SD卡缓存目录
     * @param context
     * @param uniqueName
     * @return
     */
    @SuppressLint("NewApi")
	public File getDiskCacheDir(Context context, String uniqueName) {  
        String cachePath;  
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())  
                || !Environment.isExternalStorageRemovable()) {  
            cachePath = context.getExternalCacheDir().getPath();  
        } else {  
            cachePath = context.getCacheDir().getPath();  
        }  
        return new File(cachePath + File.separator + uniqueName);  
    }
    
    
    public static String getMd5Path(String url, int type) {
        if (null == url) {
            return null;
        }
        
        String path = getSavePath(type) + StringUtils.getMd5(url)
                + IMConstants.DEFAULT_IMAGE_FORMAT;
        File file = new File(path);
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
        
        return path;
    }
    
    public static String getSavePath(int type) {
        String path;
        String floder = (type == IMConstants.FILE_SAVE_TYPE_IMAGE) ? "images"
                : "audio";
        if (CommonUtil.checkSDCard()) {
            path = Environment.getExternalStorageDirectory().toString()
                    + File.separator + "xiaobukuaipao" + File.separator + floder
                    + File.separator;

        } else {
            path = Environment.getDataDirectory().toString() + File.separator
                    + "MGJ-IM" + File.separator + floder + File.separator;
        }
        return path;
    }
    
    /**
     * @Description 判断存储卡是否存在
     * @return
     */
    public static boolean checkSDCard() {
        if (android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED)) {
            return true;
        }
        return false;
    }
}
