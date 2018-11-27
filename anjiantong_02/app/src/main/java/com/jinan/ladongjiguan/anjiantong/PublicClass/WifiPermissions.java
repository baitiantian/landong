package com.jinan.ladongjiguan.anjiantong.PublicClass;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.widget.Toast;


public class WifiPermissions {
    /***
     * 手动开启热点权限
     * */
    public static void isHasPermissions(Context context) {
        boolean result = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.System.canWrite(context)) {
                Toast.makeText(context, "打开热点需要启用“修改系统设置”权限，请手动开启", Toast.LENGTH_SHORT).show();

                //清单文件中需要android.permission.WRITE_SETTINGS，否则打开的设置页面开关是灰色的
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" + context.getPackageName()));
                //判断系统能否处理，部分ROM无此action，如魅族Flyme
                if (intent.resolveActivity(context.getPackageManager()) != null) {
                    context.startActivity(intent);
                } else {
                    //打开应用详情页
                    intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.setData(Uri.parse("package:" + context.getPackageName()));
                    if (intent.resolveActivity(context.getPackageManager()) != null) {
                        context.startActivity(intent);
                    }
                }
            } else {
                result = true;
            }
        } else {
            result = true;
        }
    }
}
