package com.xiaobukuaipao.vzhi.photo;

import android.os.Environment;

/**
 * SD卡操作工具类
 * 
 */
public class SDcardUtil {

    /**
     * 是否有SD卡
     * 
     * @return
     */
    public static boolean hasExternalStorage() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

}
