package com.jinan.ladongjiguan.anjiantong.activity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.jinan.ladongjiguan.anjiantong.PublicClass.AssetsDatabaseManager;
import com.jinan.ladongjiguan.anjiantong.PublicClass.CustomProgressDialog;
import com.jinan.ladongjiguan.anjiantong.utils.GPSUtil;
import com.jinan.ladongjiguan.anjiantong.utils.WifiUtil;
import com.jinan.ladongjiguan.anjiantong.utils.SharedPreferencesUtil;
import com.jinan.ladongjiguan.anjiantong.utils.WebServiceUtils;
import com.jinan.ladongjiguan.anjiantong.PublicClass.WifiPermissions;
import com.jinan.ladongjiguan.anjiantong.R;
import com.jinan.ladongjiguan.anjiantong.connect.WifiAdmin;
import com.jinan.ladongjiguan.anjiantong.connect.WifiApAdmin;
import com.jinan.ladongjiguan.anjiantong.utils.CommonUtils;

import org.json.JSONObject;
import org.json.XML;
import org.ksoap2.serialization.SoapObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;


public class CheckUpMainActivity extends BaseActivity implements View.OnClickListener, View.OnTouchListener {

    @BindView(R.id.examine_page_back)
    LinearLayout examinePageBack;
    @BindView(R.id.title_layout)
    TextView titleLayout;
    @BindView(R.id.bt_check_up_01)
    LinearLayout btCheckUp01;
    @BindView(R.id.bt_check_up_02)
    LinearLayout btCheckUp02;
    @BindView(R.id.delete_layout)
    LinearLayout deleteLayout;
    @BindView(R.id.bt_check_up_03)
    LinearLayout btCheckUp03;
    @BindView(R.id.bt_check_up_04)
    LinearLayout btCheckUp04;
    @BindView(R.id.bt_check_up_05)
    LinearLayout btCheckUp05;
    @BindView(R.id.Linear_layout)
    android.widget.LinearLayout LinearLayout;
    private WifiManager wifiManager_01 = null;
    //用以存储传送到文件发送界面的IP，即接收方的IP
    public static String IP_DuiFangde;
    public static String Device_ID = "";
    public boolean UdpReceiveOut = true;//8秒后跳出udp接收线程
    //LanDongRootFolder此程序自己的文件目录
    String LanDongRootFolder = "/sdcard/LanDong/";
    private long mExitTime;
    // 显示离线文件传输的日志提醒的Textview，默认情况下文本为空
    private static String LOG_TAG = "WifiBroadcastActivity";
    private boolean wifiFlag = true;//扫描wifi的子线程的标志位，如果已经连接上正确的wifi热点，线程将结束
    private String address;
    private WifiAdmin wifiAdmin;
    private ArrayList<String> arraylist = new ArrayList<String>();
    private boolean update_wifi_flag = true;
    String ip;
    public static final int DEFAULT_PORT = 43708;
    private static final int MAX_DATA_PACKET_LENGTH = 40;
    private byte[] buffer = new byte[MAX_DATA_PACKET_LENGTH];
    public boolean run = false;//判断是否接收到TCP返回，若接收到则不再继续接受
    public boolean show = false;//判断是否是由于超时而退出线程，若是则显示dialog
    private boolean tcpout = false;
    private boolean a = false;
    private String WEB_SERVER_URL;
    private CustomProgressDialog progressDialog = null;//加载页
    //开启wifi ... ...
    private WifiManager wifiManager = null;
    private ImageView iv_scanning;
    private LinearLayout Linear_root;
    public static CheckUpMainActivity mActivity_main;

    Socket socket = null;
    static DatagramSocket udpSocket = null;
    static DatagramPacket udpPacket = null;
    private boolean udpout = false;
    private PopupWindow popupWindow;//雷达效果

    /**
     * handler用于子线程更新
     */
    @SuppressLint("HandlerLeak")
    private Handler handler1 = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    Toast.makeText(CheckUpMainActivity.this, "getConnected的集合为空", Toast.LENGTH_LONG).show();
                case 1:
                    break;
                case 2:
                    UdpReceiveOut = false;
                    if (!udpout) {
                        if (wifiAdmin.setWifiApEnabled(wifiManager,true,4)) {
                            //热点开启成功
//                            Toast.makeText(getApplicationContext(), "WIFI热点开启成功,热点名称为:" + Constant.HOST_SPOT_SSID + "\n" + "密码为：" + Constant.HOST_SPOT_PASS_WORD, Toast.LENGTH_LONG).show();
                            //这里可以设置为当用户连接到自己开的热点后，就跳转到文件发送界面，并将连接到自己热点设备的IP传过去
                            //getConnectDeviceIP()返回的值前面自带IP加一个回车 字样，如IP 192.168.0.111 所以需要截取一下才可以
                            startNew startNew = new startNew();
                            startNew.start();
                        } else {
                            //热点开启失败
                            Toast.makeText(getApplicationContext(), "WIFI热点开启失败", Toast.LENGTH_LONG).show();
                            WifiPermissions.isHasPermissions(getApplicationContext());
                        }
                    }
                    break;
                case 3:
                    if (!udpout) {
                    }
                    break;
                default:
                    break;
            }
        }
    };
    @SuppressLint("HandlerLeak")
    private Handler handler2 = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 7:
                    if (!a) {
                        Toast.makeText(CheckUpMainActivity.this, "正在热点模式下搜索设备", Toast.LENGTH_LONG).show();
                        tcpout = true;
                        //更新并显示WIFI列表，此时还需要判断WIFI是否打开，可以直接写在UpdateWifiList()里面
                        // UpdateWifiList(0);
                    }
                    a = false;
                    break;
                case 8:
                    break;
                case 9:
//                    UpdateWifiList();
                    WifiUtil.getIns().UpdateWifiList(CheckUpMainActivity.this,wifiAdmin,arraylist,false,"192.168.43.1","jh",null);
                    break;
                case 10:
                    update_wifi_flag = false;
                    break;
                case 12:
                    seekAndJoin();
                    break;
                default:
                    tcpout = true;
                    if (CommonUtils.isIp((String) (msg.obj))) {
                        Toast.makeText(CheckUpMainActivity.this, "这是一个IP，地址为：" + (msg.obj), Toast.LENGTH_LONG).show();

                        IP_DuiFangde = (String) (msg.obj);

                        //跳转到文件发送界面
//                        Log.i("IP", IP_DuiFangde);
                        Intent intent_filetrans = new Intent(CheckUpMainActivity.this, Files_Trans_Activity.class);
                        intent_filetrans.putExtra("isCreateHot", "0");//0代表客户端
                        intent_filetrans.putExtra("isShowListView", false);
                        intent_filetrans.putExtra("state", "jh");
                        startActivity(intent_filetrans);

                    } else {
                        Toast.makeText(CheckUpMainActivity.this, "这不是一个IP，内容为：" + msg.obj, Toast.LENGTH_LONG).show();
                    }
//                    popView.f
                    break;
            }
        }

    };
    private View popView;

    @Override
    protected void initView() {
        setContentView(R.layout.check_up_main_layout);
        ButterKnife.bind(this);
        mActivity_main = this;
        /***
         * 显示标题
         * */
        titleLayout.setText("监管执法");
        Linear_root = (LinearLayout) findViewById(R.id.Linear_layout);
//        wifiManager = (WifiManager) super.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
//        //初始化wifiAdmin
//        wifiAdmin = new WifiAdmin(this);
//        ip = address;
    }

    @Override
    protected void init() {
        /**
         * 点击事件
         * */
        examinePageBack.setOnClickListener(this);
        btCheckUp01.setOnClickListener(this);
        btCheckUp02.setOnClickListener(this);
        btCheckUp03.setOnClickListener(this);
        btCheckUp04.setOnClickListener(this);
        btCheckUp05.setOnClickListener(this);
        /**
         * 触摸事件
         * */
        examinePageBack.setOnTouchListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.examine_page_back://返回键
                onBackPressed();
                break;
            case R.id.bt_check_up_01://制定修改计划页面
                intent.setClass(this, CheckUpActivity.class);
                intent.putExtra("state", "1");
                startActivity(intent);
                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                break;
            case R.id.bt_check_up_02://执行执法页面
                intent.setClass(this, CheckUpActivity.class);
                intent.putExtra("state", "2");
                startActivity(intent);
                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                break;
            case R.id.bt_check_up_03://下载计划页面
//                intent.setClass(this, BlankPageActivity.class);
//                intent.putExtra("date_state", "2");
//                startActivity(intent);
//                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
//                seekAndJoin();

//                LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//                Log.d("GPS状态",manager.isProviderEnabled(LocationManager.GPS_PROVIDER)+"");
                if (Build.VERSION.SDK_INT >= 23 && !GPSUtil.getIns().isOpen(CheckUpMainActivity.this)) {
                    Toast.makeText(this, "请手动打开GPS定位，否则会影响使用", Toast.LENGTH_SHORT).show();
//                    Settings.Secure.putInt(getApplicationContext().getContentResolver(), Settings.Secure.LOCATION_MODE, android.provider.Settings.Secure.LOCATION_MODE_BATTERY_SAVING);
////                    Toast.makeText(this, "已打开GPS定位，请再度点击", Toast.LENGTH_SHORT).show();
//                    wifiManager = (WifiManager) super.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
//                    //初始化wifiAdmin
//                    wifiAdmin = new WifiAdmin(this);
//                    ip = address;
//                    dialog();
                } else {
                    wifiManager = (WifiManager) super.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                    //初始化wifiAdmin
                    wifiAdmin = new WifiAdmin(this);
                    ip = address;
                    dialog();
                }
                break;
            case R.id.bt_check_up_04://分发页面
                intent.setClass(this, UpCheckUpDateActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                break;
            case R.id.bt_check_up_05://出具现场检查方案
                intent.setClass(this, CheckUpActivity.class);
                intent.putExtra("state", "0");
                startActivity(intent);
                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                break;
            default:
                break;
        }

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            v.setBackgroundColor(ContextCompat.getColor(this, R.color.main_color_7));
        } else if (event.getAction() == MotionEvent.ACTION_CANCEL) {
            v.setBackgroundColor(ContextCompat.getColor(this, R.color.main_color_6));
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            v.setBackgroundColor(ContextCompat.getColor(this, R.color.main_color_6));
        }
        return false;
    }

    /*搜索并且连接设备*/
    private void seekAndJoin() {
        tcpout = false;
        update_wifi_flag = true;
        //显示雷达扫描界面
        showPopupWindow();

        //打开线程之前先判断热点是否是开的，如果热点是开的，就关掉热点，然后再开启wifi，如果热点本身是关的，就直接开启WIFI
        if (WifiApAdmin.isWifiApEnabled(wifiManager)) {
            WifiApAdmin.closeWifiAp(wifiManager);

        } else {
        }
        wifiManager.setWifiEnabled(true);
        Thread thread = new Thread(new TcpReceive());
        thread.start();

        BroadCastUdp bcu = new BroadCastUdp(address);
        bcu.start();
    }

    /**
     * 接受返回的TCP消息
     */
    private class TcpReceive implements Runnable {
        public void run() {
            while (true) {
                tcpout = false;
                Socket socket = null;
                ServerSocket ss = null;
                BufferedReader in = null;
                try {
//                    Log.i("TcpReceive", "ServerSocket +++++++++++++++++++++++++++++");
                    ss = new ServerSocket(8080);
                    socket = ss.accept();
//                    Log.i("TcpReceive", "connect ++++++++++++++++++++++++++++");
                    if (socket != null) {
                        run = true;
                        a = true;
                        in = new BufferedReader(new InputStreamReader(
                                socket.getInputStream()));
                        StringBuilder sb = new StringBuilder();
                        InetAddress inetAddress = socket.getInetAddress();
                        sb.append(inetAddress.getHostAddress());
                        String line = null;
                        while ((line = in.readLine()) != null) {
                            sb.append(line);
                        }
//                        Log.i("TcpReceive", "connect :" + sb.toString());
                        final String ipString = sb.toString().trim();// "192.168.0.104:8731";
                        Message msg = new Message();
                        msg.obj = ipString;

                        tcpout = true;
                        //这里是IP地址
                        handler2.sendMessage(msg);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (in != null)
                            in.close();
                        if (socket != null)
                            socket.close();
                        if (ss != null)
                            ss.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (tcpout) {
                    break;
                }
            }
        }
    }

    /**
     * 获取连接到手机热点设备的IP
     */
    StringBuilder resultList;
    ArrayList<String> connectedIP;

    public String getConnectDeviceIP() {

        try {
            connectedIP = getConnectIp();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        resultList = new StringBuilder();
        for (String ip : connectedIP) {
            resultList.append(ip);
            resultList.append("\n");
            try {
                connectedIP = getConnectIp();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        String textString = resultList.toString();
        return textString;

    }

    //从系统/proc/net/arp文件中读取出已连接的设备的信息
    //获取连接设备的IP
    public ArrayList<String> getConnectIp() {
        ArrayList<String> connectIpList = new ArrayList<String>();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader("/proc/net/arp"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String line;
        try {
            while ((line = br.readLine()) != null) {
                String[] splitted = line.split(" +");
                if (splitted != null && splitted.length >= 4) {

                    String ip = splitted[0];
                    if (!ip.equals("IP")) {
                        connectIpList.add(ip);
                    }

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return connectIpList;
    }

    //type 0新建wifi列表
    //type 1动态更新wifi列表
   /* void UpdateWifiList() {
        wifiAdmin.startScan();
        wifiAdmin.lookUpScan();
        arraylist.clear();

        for (ScanResult e : wifiAdmin.getWifiList()) {

            if (e.SSID.equals("LanDong"))//如果热点名有LanDong且不为空且不重复
            {
                //关闭wifi列表更新
                update_wifi_flag = false;

                //这一段输入密码，现阶段设置为默认123456789
                CreatConnection("LanDong", "123456789", 3);//这里输入密码
                //更新这个IP地址
                IP_DuiFangde = "192.168.43.1";
                //设置点击后跳转到文件发送与接收界面，还要有一个判断，判断点击的是不是LanDong热点，这里暂时就不判断了，后期会更改为只显示LanDong这个热点
//                Log.i("TAG", "333333333333333444444444444444");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            while (GetIpAddress().equals("192.168.43.1")) {
                                Thread.sleep(500);
                            }
                            Intent intent_filetrans = new Intent(CheckUpMainActivity.this, Files_Trans_Activity.class);
                            intent_filetrans.putExtra("isCreateHot", "0");//0代表客户端
                            intent_filetrans.putExtra("isShowListView", false);
                            intent_filetrans.putExtra("state", "jh");
                            startActivity(intent_filetrans);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();// 空线程延时

                break;
            }
        }

    }*/


    /*void CreatConnection(final String name, final String key, final int type) {
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
                wifiFlag = false;//关闭扫描wifi热点的子线程
            }
        }).start();// 建立链接线程

    }*/

   /* public String GetIpAddress() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int i = wifiInfo.getIpAddress();
        String a = (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF) + "." + ((i >> 24) & 0xFF);
        String b = "0.0.0.0";
        if (a.equals(b)) {
            a = "192.168.43.1";// 当手机当作WIFI热点的时候，自身IP地址为192.168.43.1
        }
        return a;
    }*/


    /**
     * 监听有没有wifi接入到wifi热点的线程
     */
    public class startNew extends Thread {
        public void run() {
            Intent intent_filetrans2 = new Intent(CheckUpMainActivity.this, Files_Trans_Activity.class);
            intent_filetrans2.putExtra("isCreateHot", "1");//代表服务器显示的界面
//                intent_filetrans2.putStringArrayListExtra("iplist",getConnectIp());
            intent_filetrans2.putExtra("isShowListView", true);
            startActivity(intent_filetrans2);
        }

    }

    /**
     * UDP广播线程
     */
    public class BroadCastUdp extends Thread {
        private String dataString;
        private DatagramSocket udpSocket;
        public boolean exit = false;
        public BroadCastUdp(String dataString) {
            this.dataString = dataString;
        }


        public void run() {

            show = false;
            /**计算时间标志*/
            long st = System.currentTimeMillis();
            while (!exit) {
                DatagramPacket dataPacket = null;
                try {

                    if (udpSocket == null) {
                        udpSocket = new DatagramSocket(null);
                        udpSocket.setReuseAddress(true);
                        udpSocket.bind(new InetSocketAddress(DEFAULT_PORT));
                    }
                    // udpSocket = new DatagramSocket(DEFAULT_PORT);
                    dataPacket = new DatagramPacket(buffer, MAX_DATA_PACKET_LENGTH);
                    byte[] data = dataString.getBytes();
                    dataPacket.setData(data);
                    dataPacket.setLength(data.length);
                    dataPacket.setPort(DEFAULT_PORT);
                    InetAddress broadcastAddr;
                    broadcastAddr = InetAddress.getByName("255.255.255.255");
                    dataPacket.setAddress(broadcastAddr);
                } catch (Exception e) {
                    Log.e(LOG_TAG, e.toString());
                }
                try {
                    udpSocket.send(dataPacket);
                    sleep(2);
                } catch (Exception e) {
                    Log.e(LOG_TAG, e.toString());
                }
                udpSocket.close();
                /**计算时间标志*/

                long et = System.currentTimeMillis();
                /**8秒后次线程自动销毁*/
                if ((et - st) > 8000) {
                    show = true;
                    break;
                }
                /**tcp返回值后停止发送udp*/
                Log.i("tag", "show");
                if (run) {
                    run = false;
                    break;
                }
            }
            Log.i("tag", "show");
            if (show) {
                //tcpout = true;
                //Message message = new Message();
                show = false;
                //不再进行UDP发送与接收后，扫描并显示WIFI列表
                handler2.sendEmptyMessage(7);
                // handler.sendMessage(message);
                new Thread(new Runnable()//同时开启一个动态更新wifi列表的线程，直到标志位update_wifi_flag被赋值false
                {
                    @Override
                    public void run() {
                        long st = System.currentTimeMillis();

                        while (update_wifi_flag) {
                            long et = System.currentTimeMillis();

                            /**10秒后次线程自动销毁*/
                            if ((et - st) > 15000) {
                                handler2.sendEmptyMessage(10);

                            }
                            handler2.sendEmptyMessage(9);
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).start();
            }
            handler2.sendEmptyMessage(8);

        }
    }

    /**
     * UDP接受线程类
     */
    public class UdpReceive extends Thread {
        public void run() {
            udpout = false;   //判断是否退出UDP接收线程的标志位
            byte[] data = new byte[256];
            try {
                udpSocket = new DatagramSocket(43708);
                udpPacket = new DatagramPacket(data, data.length);
            } catch (SocketException e1) {
                e1.printStackTrace();
            }

            /**8秒后发送消息更新UI*/
            handler1.sendEmptyMessageDelayed(3, 2000);
            handler1.sendEmptyMessageDelayed(2, 3000);//3秒后执行case2 也就是自动开启WIFI热点
            while (UdpReceiveOut) {

                try {

                    udpSocket.receive(udpPacket);
                } catch (IOException e) {

                    e.printStackTrace();
                }

                if (null != udpPacket.getAddress()) {

                    final String quest_ip = udpPacket.getAddress().toString();
                    Message msg = new Message();
                    msg.obj = quest_ip;
                    //quest_ip前面会有一个/符号，例如/192.168.0.1，这里对他进行截取，截取后就为真正的IP地址 如 192.168.0.1
                    msg.obj = quest_ip.substring(1);
                    handler1.sendMessage(msg);//msg中包含了IP地址
                    try {
                        final String ip = udpPacket.getAddress().toString().substring(1);
                        //恢复按钮为可点击
                        //设置按钮可点击
                        //fab_CreateConnection.setEnabled(true);.........................................这里不能更新UI

                        handler1.sendEmptyMessage(1);
                        socket = new Socket(ip, 8080);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            if (null != socket) {
//                                Log.i("tag", "socket close");
                                socket.close();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }
                /**搜索到设备后停止返回tcp并停止监听*/
                if (null != udpPacket.getAddress()) {
//                    Log.i("tag", "6666666666666666666666666");
                    udpout = true;
                    udpSocket.close();
                    break;//收到UDP请求后，跳出这个循环
                }
                if (!UdpReceiveOut) {
//                    Log.i("tag", "77777777777777777777777777777777777777");
                    udpSocket.close();
                    UdpReceiveOut = true;
                    break;
                }

            }

        }
    }

    /**
     * 雷达扫面界面的显示方法
     */
    private void showPopupWindow() {
        popView = View.inflate(getApplicationContext(), R.layout.layout_pop, null);
        iv_scanning = (ImageView) popView.findViewById(R.id.iv_scanning);
        initAnimation();
        popupWindow = new PopupWindow(popView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0xcc000000));
        popupWindow.showAtLocation(Linear_root, Gravity.CENTER, 0, 0);
    }

    /**
     * 雷达扫面界面的实现方法
     */
    private void initAnimation() {
        RotateAnimation rotateAnimation = new RotateAnimation(
                0, 360,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setInterpolator(new LinearInterpolator());
        rotateAnimation.setDuration(1500);
        rotateAnimation.setRepeatCount(RotateAnimation.INFINITE);
        iv_scanning.startAnimation(rotateAnimation);

    }

    @Override
    public void onBackPressed() {
        tcpout = true;
        udpout = true;
        run = true;
        UdpReceiveOut = false;
        update_wifi_flag = false;
        if (udpSocket != null) {
            udpSocket.close();
        }
        finish();
        overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
    }

    @Override
    protected void onDestroy() {
        tcpout = true;
        udpout = true;
        run = true;
        UdpReceiveOut = false;
        update_wifi_flag = false;
        super.onDestroy();
    }

    @Override
    protected void onResume() {

        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
            popupWindow = null;

        }
//        tcpout = true;
//        udpout = true;
//        run = true;
//        UdpReceiveOut = false;
//        update_wifi_flag = false;
        init();
        super.onResume();
    }


    /**
     * 弹出确认下载计划对话框
     */
    protected void dialog() {
        ConnectivityManager mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
        wifiManager_01 = (WifiManager) super.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager_01.getConnectionInfo();
        final int ipAddress = wifiInfo == null ? 0 : wifiInfo.getIpAddress();
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("下载计划");
        builder.setMessage("请选择获取计划的途径。");
        builder.setPositiveButton("组长平板", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (wifiManager_01.isWifiEnabled() && ipAddress != 0) {
                    dialogWifi_01();
                }else {
                    seekAndJoin();
                }
                /*if (mNetworkInfo == null) {
                    seekAndJoin();
                } else {
                    dialogWifi_01();
                }*/
            }
        });
        builder.setNegativeButton("网络端", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                httpDate();
            }
        });
        builder.show();
    }

    /**
     * 弹出确认对话框
     */
    protected void dialogWifi_01() {
//        wifiManager_01 = (WifiManager) super.getSystemService(Context.WIFI_SERVICE);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("请先关闭WiFi");
        builder.setMessage("是否关闭WiFi ？");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                if (wifiManager_01 != null) {
                    wifiManager_01.setWifiEnabled(false);
//                }
                dialog.dismiss();
//                dialogWifiClose_01();

                Timer timer = new Timer();
                TimerTask timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        handler2.sendEmptyMessage(12);
                    }
                };
                //开始一个定时任务
                timer.schedule(timerTask, 500);
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }


    /**
     * 从web端获取数据
     */
    protected void httpDate() {
        //加载页添加
        if (progressDialog == null) {
            progressDialog = CustomProgressDialog.createDialog(this);
        }
        progressDialog.show();
        WEB_SERVER_URL = SharedPreferencesUtil.getStringData(this, "IPString", "");
        String UserId = SharedPreferencesUtil.getStringData(this, "userId", null);
        //添加参数
        HashMap<String, String> properties = new HashMap<>();
        properties.put("Xml", "<Request><data code='proc-GetPlan'><no><UserId>" +
                UserId + "</UserId></no></data></Request>");
        properties.put("Token", "");
        Log.e("上传数据", properties.toString());
        WebServiceUtils.callWebService(WEB_SERVER_URL, "DynamicInvoke", properties, new WebServiceUtils.WebServiceCallBack() {

            //WebService接口返回的数据回调到这个方法中
            @Override
            public void callBack(SoapObject result) {
                if (result != null) {
                    Log.e("返回数据", result.toString());
                    try {
//                                       Log.d("用户名发过去的结果",""+result);
                        Object detail = result.getProperty("DynamicInvokeResult");
                        JSONObject jsonObj;
                        jsonObj = XML.toJSONObject(detail.toString());
                        JSONObject obj;
                        // 初始化，只需要调用一次
                        AssetsDatabaseManager.initManager(getApplication());
                        // 获取管理对象，因为数据库需要通过管理对象才能够获取
                        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
                        // 通过管理对象获取数据库
                        SQLiteDatabase db = mg.getDatabase("users.db");
//                                Log.e("各类表上传返回数据", jsonObj.getJSONObject("DocumentElement").getJSONObject("Table").toString());
                        if (jsonObj.has("DocumentElement")) {
                            obj = jsonObj.getJSONObject("DocumentElement").getJSONObject("Table");
                            ContentValues values = new ContentValues();
                            values.put("PlanId", obj.getString("planid"));
                            values.put("BusinessId", obj.getString("businessid"));
                            values.put("CheckType", obj.getString("checktype"));
                            String[] strings = obj.getString("stattime").split("T");
                            values.put("StatTime", strings[0]);
                            strings = obj.getString("endtime").split("T");
                            values.put("EndTime", strings[0]);
                            String s_c = "SELECT* FROM Base_Company WHERE ParentId = ?";
                            Cursor cursor = db.rawQuery(s_c,
                                    new String[]{obj.getString("companyid")});
                            cursor.moveToFirst();
                            values.put("CompanyId", cursor.getString(cursor.getColumnIndex("CompanyId")));
                            cursor.close();
                            values.put("AddUser", obj.getString("adduser"));
                            strings = obj.getString("addtime").split("T");
                            values.put("AddTime", strings[0]);
                            values.put("Address", obj.getString("address"));
                            values.put("EnforceAccording", obj.getString("enforceaccording"));
                            if (!obj.getString("othercompany").equals("&nbsp;")) {

                                values.put("OtherCompany", obj.getString("othercompany"));
                            } else {
                                values.put("OtherCompany", "");
                            }
                            db.replace("ELL_EnforcementPlan", null, values);
                            String[] groupid = obj.getString("groupid").split(";");
                            String[] headman = obj.getString("headman").split(";");
                            String[] gaddress = obj.getString("gaddress").split(";");
                            for (int i = 0; i < groupid.length; i++) {
                                values = new ContentValues();
                                values.put("GroupId", groupid[i]);
                                values.put("PlanId", obj.getString("planid"));
                                values.put("Headman", headman[i]);
                                values.put("Address", gaddress[i]);
                                db.replace("ELL_GroupInfo", null, values);
                            }
                            String[] memberid = obj.getString("memberid").split(";");
                            String[] mgroupid = obj.getString("mgroupid").split(";");
                            String[] member = obj.getString("member").split(";");
                            for (int i = 0; i < memberid.length; i++) {
                                values = new ContentValues();
                                values.put("MemberId", memberid[i]);
                                values.put("GroupId", mgroupid[i]);
                                values.put("Member", member[i]);
                                db.replace("ELL_Member", null, values);
                            }
                            Toast.makeText(CheckUpMainActivity.this, "下载计划成功", Toast.LENGTH_SHORT).show();

//                                    onBackPressed();
                        } else {
                            if (progressDialog != null) {
                                progressDialog.dismiss();
                                progressDialog = null;
                            }
                            Toast.makeText(CheckUpMainActivity.this, "没有可以下载的计划", Toast.LENGTH_SHORT).show();

                        }
                        //关闭加载页
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                            progressDialog = null;
                        }
                    } catch (Exception e) {
                        //关闭加载页
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                            progressDialog = null;
                        }
                        Toast.makeText(CheckUpMainActivity.this, "提交失败", Toast.LENGTH_SHORT).show();
                        Log.e("JSON exception", e.getMessage());
                        e.printStackTrace();
                    }
                } else {
                    //关闭加载页
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                        progressDialog = null;
                    }
                    Toast.makeText(CheckUpMainActivity.this, "网络未响应，提交失败", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }
    /**
     * 退出页面关闭GPS
     * */
    @Override
    public void finish() {
        if(Build.VERSION.SDK_INT >= 23 &&GPSUtil.getIns().isOpen(CheckUpMainActivity.this)){

            Settings.Secure.putInt(getApplicationContext().getContentResolver(), Settings.Secure.LOCATION_MODE, android.provider.Settings.Secure.LOCATION_MODE_OFF);
        }
        super.finish();
    }

    /**
     * 弹出确认分发对话框
     */
    protected void dialogWifiClose_01() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("已关闭WiFi");
        builder.setMessage("是否继续？");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                seekAndJoin();
                dialog.dismiss();

            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }
}
