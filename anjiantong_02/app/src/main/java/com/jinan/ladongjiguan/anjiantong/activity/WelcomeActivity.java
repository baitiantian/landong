package com.jinan.ladongjiguan.anjiantong.activity;


import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.view.View;

import com.jinan.ladongjiguan.anjiantong.PublicClass.GetMac;
import com.jinan.ladongjiguan.anjiantong.PublicClass.MonIndicator;
import com.jinan.ladongjiguan.anjiantong.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.util.Timer;
import java.util.TimerTask;

public class WelcomeActivity extends BaseActivity {
    @Override
    protected void initView() {
        setContentView(R.layout.activity_splash);
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                      | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        MonIndicator monIndicator = (MonIndicator) this.findViewById(R.id.monIndicator);
        monIndicator.setColors(new int[]{0xFF02083c, 0xFFffe189, 0xFF6f78ca, 0xFFfd8c35, 0xFF3f415a});

    }

    @Override
    protected void init() {
        /**
         * 对比验证硬件
         * */

//
//        TelephonyManager telephonyManager=(TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
//        String Imei=telephonyManager.getDeviceId();

        String macAddress = null;
        String macAddressCode = null;
        WifiManager wifiMgr = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = (null == wifiMgr ? null : wifiMgr.getConnectionInfo());
        if (null != info) {
            macAddress = info.getMacAddress();
        }
        String ImeiCode=MD5("720"+ GetMac.getMac(getApplicationContext())+"ld2017");
        macAddressCode = MD5( ImeiCode+"ld720");
        try {
            File urlFile = new File("/sdcard/anjiantong.txt");
            InputStreamReader isr = new InputStreamReader(new FileInputStream(urlFile), "UTF-8");
            BufferedReader br = new BufferedReader(isr);
            String str = "";
            String mimeTypeLine = null ;
//            Log.d("授权码",ImeiCode);
            while ((mimeTypeLine = br.readLine()) != null) {
                str = str+mimeTypeLine;
            }
//            Log.d("读取授权码",str);
            if(macAddressCode.equals(str)&&!str.equals("7FBC011C34085007D3075927A7BFB949")){
                /**
                 * 欢迎界面持续时间
                 * */
                Timer timer = new Timer();
                TimerTask task = new TimerTask() {

                    @Override
                    public void run() {
                        Intent intent = new Intent();
                        setResult(0,intent);
                        finish();
                        overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                    }
                };
                timer.schedule(task, 3000);
            }else if(str.equals("7FBC011C34085007D3075927A7BFB949")){
                /**
                 * 欢迎界面持续时间
                 * */
                Timer timer = new Timer();
                TimerTask task = new TimerTask() {

                    @Override
                    public void run() {
                        Intent intent = new Intent();
                        setResult(2,intent);
                        finish();
                        overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                    }
                };
                timer.schedule(task, 3000);
            }else {
                /**
                 * 欢迎界面持续时间
                 * */
                Timer timer = new Timer();
                TimerTask task = new TimerTask() {

                    @Override
                    public void run() {
                        Intent intent = new Intent();
                        setResult(1,intent);
                        finish();
                        overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                    }
                };
                timer.schedule(task, 3000);
            }
        } catch (Exception e) {
            Log.e("授权获取失败",e.toString());
            /**
             * 欢迎界面持续时间
             * */
            Timer timer = new Timer();
            TimerTask task = new TimerTask() {

                @Override
                public void run() {
                    Intent intent = new Intent();
                    setResult(1,intent);
                    finish();
                    overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                }
            };  
            timer.schedule(task, 3000);
            e.printStackTrace();
        }
    }
    @Override
    public void onBackPressed() {

    }

    /**
     * 验证硬件
     * */
    public final static String MD5(String s) {
        char hexDigits[]={'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
        try {
            byte[] btInput = s.getBytes();
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            mdInst.update(btInput);
            // 获得密文
            byte[] md = mdInst.digest();
            // 把密文转换成十六进制的字符串形式
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }



}
