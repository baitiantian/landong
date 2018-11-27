package com.jinan.ladongjiguan.anjiantong.utils;

import android.content.Context;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;

/**
 * Created by guo on 2018/9/20.
 */

public class GPSUtil {
    // 单例
    private static final GPSUtil ourInstance = new GPSUtil();

    public static GPSUtil getIns() {
        return ourInstance;
    }
    //判断gps是否处于打开状态
    public boolean isOpen(Context context) {
        if (Build.VERSION.SDK_INT < 19) {
            LocationManager myLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            return myLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } else {
            int state = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE, Settings.Secure.LOCATION_MODE_OFF);
            if (state == Settings.Secure.LOCATION_MODE_OFF) {
                return false;
            } else {
                return true;
            }
        }
    }

}
