package com.jinan.ladongjiguan.anjiantong.PublicClass;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import java.io.File;

/**
 * Created by guo on  17/12/12.
 */

public class HouzhuiClass {
    private static String TAG = HouzhuiClass.class.getSimpleName();

    public static final String[][] MIME_MapTable =
            {
                    // --{后缀名， MIME类型}   --
                    {".pdf", "application/pdf"},
                    {".doc", "application/msword"},
                    {".txt", "text/plain"}

                 /*   {".asc", "text/plain"},*/
                 /*   {".c", "text/plain"},*/
                 /*   {".conf", "text/plain"},*/
                 /*   {".cpp", "text/plain"},*/
//                    {".xml", "text/xml"},
                   /* {".xsit", "text/xml"},*/
                   /* {".xsl", "text/xml"},*/
                   /* {".xul", "text/xul"},*/
//                    {".txt", "text/plain"},
                    /*{".rc", "text/plain"},*/
                    /*{".prop", "text/plain"},*/
                    /*{".log", "text/plain"},*/
                    /*{".java", "text/plain"},*/
                    /*{".h", "text/plain"},*/
//                    {".wps", "application/vnd.ms-works"},
//                    {".pot", "application/vnd.ms-powerpoint"},
//                    {".pps", "application/vnd.ms-powerpoint"},
//                    {".ppt", "application/vnd.ms-powerpoint"}
//                    {"", "*/*"}
            };

    public static void show(String path) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
    }
      public static Intent showOpenTypeDialog(String param) {
        Log.e(TAG, "showOpenTypeDialog-param为" + param);
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(android.content.Intent.ACTION_VIEW);
        Uri uri = Uri.fromFile(new File(param));
         String fileMimeType = getMIMEType(param);
        intent.setDataAndType(uri, fileMimeType);
//        intent.setDataAndType(uri, "*/*");
        return intent;
    }
    /**
     * --获取文件类型 --
     */
    public static String getMIMEType(String filePath) {
        String type = "*/*";
        String fName = filePath;

        int dotIndex = fName.lastIndexOf(".");
        if (dotIndex < 0) {
            return type;
        }

        String end = fName.substring(dotIndex, fName.length()).toLowerCase();
        if (end == "") {
            return type;
        }
        Log.e(TAG, "MIME_MapTable的长度为" + MIME_MapTable.length + "");
        for (int i = 0; i < MIME_MapTable.length; i++) {
            if (end.equals(MIME_MapTable[i][0])) {
                type = MIME_MapTable[i][1];
            }
        }
        return type;
    }
}