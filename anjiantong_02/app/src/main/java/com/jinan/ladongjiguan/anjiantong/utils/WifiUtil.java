package com.jinan.ladongjiguan.anjiantong.utils;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.jinan.ladongjiguan.anjiantong.activity.Files_Trans_Activity;
import com.jinan.ladongjiguan.anjiantong.activity.Files_Trans_Activity_01;
import com.jinan.ladongjiguan.anjiantong.connect.WifiAdmin;

import static com.jinan.ladongjiguan.anjiantong.activity.CheckUpMainActivity.mActivity_main;
import static com.jinan.ladongjiguan.anjiantong.activity.CheckUpDateActivity.mActivity_date;
import static com.jinan.ladongjiguan.anjiantong.activity.ReviewActivity.mActivity_review;
import static com.jinan.ladongjiguan.anjiantong.activity.ReviewImplementActivity.mActivity_implement;
import static com.jinan.ladongjiguan.anjiantong.activity.ReviewPlanUpActivity.mActivity_planup;
import static com.jinan.ladongjiguan.anjiantong.activity.UpCheckUpDateActivity.mActivity_update;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.WIFI_SERVICE;

public class WifiUtil {
    /**
     * 切换到指定wifi
     *
     * @param wifiName 指定的wifi名字
     * @param wifiPwd  wifi密码，如果已经保存过密码，可以传入null
     * @return
     */
    public boolean changeToWifi(String wifiName, String wifiPwd) {
        if (mWifiManager == null) {
            Log.i(TAG, " ***** init first ***** ");
            return false;
        }

        String __wifiName__ = "\"" + wifiName + "\"";
        printCurWifiInfo();

        List wifiList = mWifiManager.getConfiguredNetworks();
        boolean bFindInList = false;
        for (int i = 0; i < wifiList.size(); ++i) {
            WifiConfiguration wifiInfo0 = (WifiConfiguration) wifiList.get(i);

            // 先找到对应的wifi
            if (__wifiName__.equals(wifiInfo0.SSID) || wifiName.equals(wifiInfo0.SSID)) {
                // 1、 先启动，可能已经输入过密码，可以直接启动
                Log.i(TAG, " set wifi 1 = " + wifiInfo0.SSID);
                return doChange2Wifi(wifiInfo0.networkId);
            }

        }

        // 2、如果wifi还没有输入过密码，尝试输入密码，启动wifi
        if (!bFindInList) {
            WifiConfiguration wifiNewConfiguration = createWifiInfo(wifiName, wifiPwd);//使用wpa2的wifi加密方式
            int newNetworkId = mWifiManager.addNetwork(wifiNewConfiguration);
            if (newNetworkId == -1) {
                Log.e(TAG, "操作失败,需要您到手机wifi列表中取消对设备连接的保存");
            } else {
                return doChange2Wifi(newNetworkId);
            }
        }

        return false;
    }

    private boolean doChange2Wifi(int newNetworkId) {
        // 如果wifi权限没打开（1、先打开wifi，2，使用指定的wifi
        if (!mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(true);
        }

        boolean enableNetwork = mWifiManager.enableNetwork(newNetworkId, true);
        if (!enableNetwork) {
            Log.e(TAG, "切换到指定wifi失败");
            return false;
        } else {
            Log.e(TAG, "切换到指定wifi成功");
            return true;
        }
    }

    /**
     * 创建 WifiConfiguration，这里创建的是wpa2加密方式的wifi
     *
     * @param ssid     wifi账号
     * @param password wifi密码
     * @return
     */
    private WifiConfiguration createWifiInfo(String ssid, String password) {
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + ssid + "\"";
        config.preSharedKey = "\"" + password + "\"";
        config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
        config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
        config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
        config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        config.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
        config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
        config.status = WifiConfiguration.Status.ENABLED;
        return config;
    }

    public static final String TAG = "WifiUtil";

    public void printCurWifiInfo() {
        if (mWifiManager == null) {
            return;
        }

        WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
        Log.i(TAG, "cur wifi = " + wifiInfo.getSSID());
        Log.i(TAG, "cur getNetworkId = " + wifiInfo.getNetworkId());
    }


    private WifiManager mWifiManager;

    // 单例
    private static final WifiUtil ourInstance = new WifiUtil();

    public static WifiUtil getIns() {
        return ourInstance;
    }

    public void init(Context context) {
        mWifiManager = (WifiManager) context.getApplicationContext().getSystemService(WIFI_SERVICE);
    }

    public String GetIpAddress(Context context) {
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int i = wifiInfo.getIpAddress();
        String a = (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF) + "." + ((i >> 24) & 0xFF);
        String b = "0.0.0.0";
        if (a.equals(b)) {
            a = "192.168.43.1";// 当手机当作WIFI热点的时候，自身IP地址为192.168.43.1
        }
        return a;
    }

    //type 0新建wifi列表
    //type 1动态更新wifi列表
    public void UpdateWifiList(final Context context, WifiAdmin wifiAdmin, ArrayList<String> list, boolean update_wifi_flag, String IP_DuiFangde, final String state, final String s4) {
        wifiAdmin.startScan();
        wifiAdmin.lookUpScan();
        list.clear();

        for (ScanResult e : wifiAdmin.getWifiList()) {

            if (e.SSID.equals("LanDong"))//如果热点名有LanDong且不为空且不重复
            {
                //关闭wifi列表更新
                update_wifi_flag = false;

                //这一段输入密码，现阶段设置为默认123456789
                CreatConnection(wifiAdmin, false, "LanDong", "123456789", 3);//这里输入密码
                //更新这个IP地址
                IP_DuiFangde = "192.168.43.1";
                //设置点击后跳转到文件发送与接收界面，还要有一个判断，判断点击的是不是LanDong热点，这里暂时就不判断了，后期会更改为只显示LanDong这个热点
//                Log.i("TAG", "333333333333333444444444444444");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            while (GetIpAddress(context).equals("192.168.43.1")) {
                                Thread.sleep(500);
                            }
                            if (context.equals(mActivity_date)) {
                                Intent intent_filetrans = new Intent(context, Files_Trans_Activity.class);
                                intent_filetrans.putExtra("isCreateHot", "0");//0代表客户端
                                intent_filetrans.putExtra("isShowListView", false);
                                intent_filetrans.putExtra("state", state);
                                intent_filetrans.putExtra("id", s4);
                                context.startActivity(intent_filetrans);
                            } else if (context.equals(mActivity_main)) {
                                Intent intent_filetrans = new Intent(context, Files_Trans_Activity.class);
                                intent_filetrans.putExtra("isCreateHot", "0");//0代表客户端
                                intent_filetrans.putExtra("isShowListView", false);
                                intent_filetrans.putExtra("state", "jh");
                                context.startActivity(intent_filetrans);
                            } else if (context.equals(mActivity_review)) {
                                Intent intent_filetrans = new Intent(context, Files_Trans_Activity_01.class);
                                intent_filetrans.putExtra("isCreateHot", "0");//0代表客户端
                                intent_filetrans.putExtra("isShowListView", false);
                                intent_filetrans.putExtra("state", "jh");
                                context.startActivity(intent_filetrans);
                            }else if(context.equals(mActivity_implement)){
                                Intent intent_filetrans = new Intent(context, Files_Trans_Activity_01.class);
                                intent_filetrans.putExtra("isCreateHot", "0");//0代表客户端
                                intent_filetrans.putExtra("isShowListView", false);
                                intent_filetrans.putExtra("state", state);
                                intent_filetrans.putExtra("id", s4);
                                context.startActivity(intent_filetrans);
                            }else if(context.equals(mActivity_planup)){
                                Intent intent_filetrans = new Intent(context, Files_Trans_Activity_01.class);
                                context.startActivity(intent_filetrans);
                            }else if(context.equals(mActivity_update)){
                                Intent intent_filetrans = new Intent(context, Files_Trans_Activity.class);
                                context.startActivity(intent_filetrans);
                            }

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();// 空线程延时

                break;
            }
        }

    }


    public void CreatConnection(final WifiAdmin wifiAdmin, boolean wifiFlag, final String name, final String key, final int type) {
        final boolean[] a = {wifiFlag};
        new Thread(new Runnable()//匿名内部类的调用方式
        {
            @Override
            public void run() {
                wifiAdmin.openWifi();
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                wifiAdmin.addNetwork(wifiAdmin.CreateWifiInfo(name, key, type));

                a[0] = false;//关闭扫描wifi热点的子线程
            }
        }).start();// 建立链接线程

    }
}
