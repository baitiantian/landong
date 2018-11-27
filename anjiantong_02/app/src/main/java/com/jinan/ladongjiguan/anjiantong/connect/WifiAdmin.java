/**
 * wifi管理工具类
 */
package com.jinan.ladongjiguan.anjiantong.connect;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.os.Build;
import android.util.Log;

import java.lang.reflect.Method;
import java.util.List;

public class WifiAdmin {
    private ConnectivityManager mCM;
    //定义WifiManager对象
    private WifiManager mWifiManager;
    private ConnectivityManager mConnectManager;
    //定义WifiInfo对象
    private WifiInfo mWifiInfo;
    //扫描出的网络连接列表
    private List<ScanResult> mWifiList;
    //网络连接列表
    private List<WifiConfiguration> mWifiConfiguration;
    //定义一个WifiLock
    WifiLock mWifiLock;
    private static String TAG = WifiAdmin.class.getSimpleName();

    //构造器
    public WifiAdmin(Context context) {
        mCM = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        //取得WifiManager对象
        mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        //取得WifiInfo对象
        mWifiInfo = mWifiManager.getConnectionInfo();
    }

    //打开WIFI
    public void openWifi() {
        Log.e("WifiAdmin-1","111");
        if (!mWifiManager.isWifiEnabled()) {
            Log.e("WifiAdmin-2","222");
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.N_MR1){//7.1
                Log.e(TAG,"-----7.1系统----");
                mWifiManager.setWifiEnabled(true);
            }else {
                mWifiManager.setWifiEnabled(true);
            }
        }
    }

    public void startScan() {

        mWifiManager.startScan();
        //得到扫描结果
        mWifiList = mWifiManager.getScanResults();
        //得到配置好的网络连接
        mWifiConfiguration = mWifiManager.getConfiguredNetworks();
    }

    //得到网络列表
    public List<ScanResult> getWifiList() {
        return mWifiList;
    }

    //查看扫描结果
    public StringBuilder lookUpScan() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < mWifiList.size(); i++) {
            stringBuilder.append("Index_" + new Integer(i + 1).toString() + ":");
            // 将ScanResult信息转换成一个字符串包
            // 其中把包括：BSSID、SSID、capabilities、frequency、level
            stringBuilder.append((mWifiList.get(i)).toString());
            stringBuilder.append("/n");
        }
        return stringBuilder;
    }


    //添加一个网络并连接
//    public void addNetwork(WifiConfiguration wcg,List<WifiConfiguration> list) {
    public void addNetwork(WifiConfiguration wcg) {
        Log.e("WifiAdmin-3","333");
        int wcgID = mWifiManager.addNetwork(wcg);
        boolean b = mWifiManager.enableNetwork(wcgID, true);
        // TODO: 2018/1/3 2-王福春
        mWifiManager.saveConfiguration();
        boolean connected =mWifiManager.reconnect();
        Log.e(TAG,"connected为"+connected);
        System.out.println("a--" + wcgID);
        System.out.println("b--" + b);
    }

    public WifiConfiguration CreateWifiInfo(String SSID, String Password, int Type) {
        WifiConfiguration tempConfig = this.IsExsits(SSID);
        WifiConfiguration config = null;
        if (mWifiManager != null) {
            List<WifiConfiguration> existingConfigs = mWifiManager.getConfiguredNetworks();
            for (WifiConfiguration existingConfig : existingConfigs) {
                if (existingConfig == null) continue;
                if (existingConfig.SSID.equals("\"" + SSID + "\"")  /*&&  existingConfig.preSharedKey.equals("\""  +  password  +  "\"")*/) {
                    config = existingConfig;
                    break;
                }
            }
            Log.e(TAG,"config====tempConfig为"+config);
        }
        if (config == null) {
            config = new WifiConfiguration();
        }
            config.allowedAuthAlgorithms.clear();
            config.allowedGroupCiphers.clear();
            config.allowedKeyManagement.clear();
            config.allowedPairwiseCiphers.clear();
            config.allowedProtocols.clear();
            config.SSID = "\"" + SSID + "\"";

            Log.e(TAG, SSID + "," + Password + "," + Type);
        if (Type == 0) {// WIFICIPHER_NOPASSwifiCong.hiddenSSID = false;
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        } else if (Type == 1) {  //  WIFICIPHER_WEP
            config.hiddenSSID = true;
            config.wepKeys[0] = "\"" + Password + "\"";
            config.allowedAuthAlgorithms
                    .set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            config.allowedGroupCiphers
                    .set(WifiConfiguration.GroupCipher.WEP104);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        }else if (Type == 3) //WIFICIPHER_WPA
            {
                config.preSharedKey = "\"" + Password + "\"";
                config.hiddenSSID = true;
                config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
                config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
//                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
                config.allowedKeyManagement.set(4);
                config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
                //config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
                config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
                config.status = WifiConfiguration.Status.ENABLED;
            }
            Log.e(TAG, "config-------tempConfig为" + config);
        return config;
    }

    private WifiConfiguration IsExsits(String SSID) {
        List<WifiConfiguration> existingConfigs = mWifiManager.getConfiguredNetworks();
        for (WifiConfiguration existingConfig : existingConfigs) {
            if (existingConfig.SSID.equals("\"" + SSID + "\"")) {
                return existingConfig;
            }
        }
        return null;
    }

    /**
     * wifi热点开关的方法
     */
    public boolean setWifiApEnabled(WifiManager wifiManager,boolean enabled,int index) {
        if (enabled) { // disable WiFi in any case
            //wifi和热点不能同时打开，所以打开热点的时候需要关闭wifi
            wifiManager.setWifiEnabled(false);
        }
        try {
            //热点的配置类
            WifiConfiguration apConfig = new WifiConfiguration();
            //配置热点的名称(可以在名字后面加点随机数什么的)
            apConfig.SSID = Constant.HOST_SPOT_SSID;
            //配置热点的密码
            apConfig.preSharedKey = Constant.HOST_SPOT_PASS_WORD;

            /***配置热点的其他信息  加密方式**/
            apConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
//            apConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
//            //用WPA密码方式保护
//            apConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            apConfig.allowedKeyManagement.set(4);
//            apConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
//            apConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
//            apConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
//            apConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            //通过反射调用设置热点
//            Method method = wifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, Boolean.TYPE);
//            Method method = wifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, Boolean.TYPE);
            //打开wifi ap
//            mStartTetheringCallback = new OnStartTetheringCallback();
//            mCm.startTethering(TETHERING_WIFI, true, mStartTetheringCallback, mwifiHandler);
//            mWifiManager.setWifiApConfiguration(apConfig);
//
//            mWifiConfig = mWifiManager.getWifiApConfiguration();
//            mWifiConfig = mWifiManager.getWifiApConfiguration();
            if (Build.VERSION.SDK_INT >= 23) {
//                ComponentName comp = new ComponentName("com.jinan.ladongjiguan.wifidemo","com.jinan.ladongjiguan.wifidemo.MainActivity");
//                Intent it = new Intent();
//                it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//需要加这个不然会报错
//                it.setComponent(comp);
//                startActivity(it);
                return true;

            } /*else {*/
                Method method = wifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, Boolean.TYPE);
                return (boolean) method.invoke(wifiManager, apConfig, true);
//            }
            //返回热点打开状态
//            return (Boolean) method.invoke(wifiManager, apConfig, enabled);
        } catch (Exception e) {
//            Log.e("开启热点失败数据",e.getMessage());
            Log.e("开启热点失败数据1",e.toString(),e);
//            Log.e("开启热点失败数据2",e.getLocalizedMessage());
            e.printStackTrace();
            return false;
        }
    }

}  
