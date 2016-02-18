package com.xiaobukuaipao.vzhi.photo;

import android.content.Context;
import android.graphics.Point;
import android.util.DisplayMetrics;

/**
 * 与设备相关的工具类
 * 
 */
public class DeviceUtil {
    private DeviceUtil() {
    }

    /**
     * 获取屏幕的宽高
     * 
     * @return
     */
    public static Point getDeviceSize(Context ctx) {
        DisplayMetrics dm = ctx.getResources().getDisplayMetrics();
        Point size = new Point();
        size.x = dm.widthPixels;
        size.y = dm.heightPixels;
        return size;
    }
}
