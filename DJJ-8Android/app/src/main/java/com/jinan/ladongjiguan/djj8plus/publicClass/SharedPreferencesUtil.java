package com.jinan.ladongjiguan.djj8plus.publicClass;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesUtil {
    public static String CONFIG =  "config";
    private static SharedPreferences sharedPreferences;

    public static void saveBooleanData(Context context, String key, boolean value){
        if(sharedPreferences==null){
            sharedPreferences = context.getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
        }
        sharedPreferences.edit().putBoolean(key, value).commit();
    }

    public static boolean getBooleanData(Context context, String key, boolean defValue){
        if(sharedPreferences==null){
            sharedPreferences = context.getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
        }
        return sharedPreferences.getBoolean(key, defValue);
    }

    public static void saveStringData(Context context, String key, String value){
        if(sharedPreferences==null){
            sharedPreferences = context.getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
        }
        sharedPreferences.edit().putString(key, value).commit();
    }

    public static String getStringData(Context context, String key, String defValue){
        if(sharedPreferences==null){
            sharedPreferences = context.getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
        }
        return sharedPreferences.getString(key, defValue);
    }
}
