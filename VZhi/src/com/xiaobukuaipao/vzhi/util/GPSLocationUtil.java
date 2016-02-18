package com.xiaobukuaipao.vzhi.util;

import android.location.Criteria;
import android.location.LocationManager;

public class GPSLocationUtil {
    public static String getLocationProvider(LocationManager locationManager) {
    	// Criteria -- 标准
        Criteria criteria = new Criteria();
        // 经纬度的精准性
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        // 是否需要海拔
        criteria.setAltitudeRequired(false);
        // 是否需要方位
        criteria.setBearingRequired(false);
        // 是否需要资费
        criteria.setCostAllowed(true);
        // 电量要求
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        // 得到一个最好的提供者
        return locationManager.getBestProvider(criteria, true);
    }
}
