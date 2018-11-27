package com.jinan.ladongjiguan.anjiantong.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.jinan.ladongjiguan.anjiantong.PublicClass.AssetsDatabaseManager;
import com.jinan.ladongjiguan.anjiantong.utils.WifiUtil;
import com.jinan.ladongjiguan.anjiantong.adapter.CheckUpDateListAdapter;
import com.jinan.ladongjiguan.anjiantong.adapter.ReviewPlanUpDateListAdapter;
import com.jinan.ladongjiguan.anjiantong.PublicClass.WifiPermissions;
import com.jinan.ladongjiguan.anjiantong.R;
import com.jinan.ladongjiguan.anjiantong.connect.WifiAdmin;
import com.jinan.ladongjiguan.anjiantong.utils.CommonUtils;
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
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import static com.jinan.ladongjiguan.anjiantong.activity.CheckUpMainActivity.IP_DuiFangde;


public class ReviewPlanUpActivity extends BaseActivity implements View.OnClickListener, View.OnTouchListener {


    @BindView(R.id.examine_page_back)
    LinearLayout examinePageBack;
    @BindView(R.id.title_layout)
    TextView titleLayout;
    @BindView(R.id.bt_add_check)
    Button btAddCheck;
    @BindView(R.id.l_add_check_up)
    LinearLayout lAddCheckUp;
    @BindView(R.id.tx_table)
    TextView txTable;
    @BindView(R.id.list_01)
    ListView list01;
    @BindView(R.id.Linear_layout_1)
    LinearLayout LinearLayout1;
    private List<HashMap<String, Object>> listItems = new ArrayList<>();//计划表
    private CheckUpDateListAdapter listAdapter;//表适配
    public boolean UdpReceiveOut = true;//8秒后跳出udp接收线程
    //LanDongRootFolder此程序自己的文件目录
    String LanDongRootFolder = "/sdcard/LanDong/";
    // 显示离线文件传输的日志提醒的Textview，默认情况下文本为空
    private static String LOG_TAG = "WifiBroadcastActivity";
    private boolean wifiFlag = true;//扫描wifi的子线程的标志位，如果已经连接上正确的wifi热点，线程将结束
    private String address;
    private WifiAdmin wifiAdmin;
    private WifiManager wifiManager_01 = null;
    private ArrayList<String> arraylist = new ArrayList<String>();
    private boolean update_wifi_flag = true;
    String ip;
    public static ReviewPlanUpActivity mActivity_planup;
    public static final int DEFAULT_PORT = 43708;
    private static final int MAX_DATA_PACKET_LENGTH = 40;
    private byte[] buffer = new byte[MAX_DATA_PACKET_LENGTH];
    public boolean run = false;//判断是否接收到TCP返回，若接收到则不再继续接受
    public boolean show = false;//判断是否是由于超时而退出线程，若是则显示dialog
    private boolean tcpout = false;
    private boolean a = false;
    //开启wifi ... ...
    private WifiManager wifiManager = null;
    private ImageView iv_scanning;
    private LinearLayout Linear_root;

    Socket socket = null;
    static DatagramSocket udpSocket = null;
    static DatagramPacket udpPacket = null;
    private boolean udpout = false;
    private String s = "";//需要传输的计划ID
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
                    Toast.makeText(ReviewPlanUpActivity.this, "getConnected的集合为空", Toast.LENGTH_LONG).show();
                case 1:
                    break;
                case 2:
                    UdpReceiveOut = false;
                    if (!udpout) {
                        if (wifiAdmin.setWifiApEnabled(wifiManager,true,WifiConfiguration.KeyMgmt.WPA_PSK)) {
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
                        Toast.makeText(ReviewPlanUpActivity.this, "正在热点模式下搜索设备", Toast.LENGTH_LONG).show();
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
                    WifiUtil.getIns().UpdateWifiList(ReviewPlanUpActivity.this,wifiAdmin,arraylist,false,"192.168.43.1",null,null);
                    break;
                case 10:
                    update_wifi_flag = false;
                    break;
                default:
                    tcpout = true;

                    if (CommonUtils.isIp((String) (msg.obj))) {
                        Toast.makeText(ReviewPlanUpActivity.this, "这是一个IP，地址为：" + (msg.obj), Toast.LENGTH_LONG).show();

                        IP_DuiFangde = (String) (msg.obj);
                        //跳转到文件发送界面
//                        Log.i("TAG", "2222222222222222222222222222222");
                        Intent intent_filetrans = new Intent(ReviewPlanUpActivity.this, Files_Trans_Activity_01.class);
                        intent_filetrans.putExtra("isCreateHot", "0");//0代表客户端
                        intent_filetrans.putExtra("isShowListView", false);
                        startActivity(intent_filetrans);

                    } else {
                        Toast.makeText(ReviewPlanUpActivity.this, "这不是一个IP，内容为：" + msg.obj, Toast.LENGTH_LONG).show();
                    }
//                    popView.f
                    break;
            }
        }

    };
    private View popView;

    @Override
    protected void initView() {
        setContentView(R.layout.review_plan_up_layout);
        ButterKnife.bind(this);
        mActivity_planup = this;
        titleLayout.setText("分发复查计划");
        Linear_root = (LinearLayout) findViewById(R.id.Linear_layout_1);
        wifiManager = (WifiManager) super.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        //初始化wifiAdmin
        wifiAdmin = new WifiAdmin(this);
        ip = address;
    }

    @Override
    protected void init() {
        examinePageBack.setOnClickListener(this);
        examinePageBack.setOnTouchListener(this);
        btAddCheck.setOnClickListener(this);
        openDate();
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.examine_page_back://返回键
                onBackPressed();
                break;
            case R.id.bt_add_check:
                s = "";
                for (int i = 0; i < listItems.size(); i++) {
                    if (ReviewPlanUpDateListAdapter.isSelected.get(i)) {

                        if (s.length() > 0) {
                            s = s + "," + listItems.get(i).get("ReviewId").toString();
                        } else {
                            s = listItems.get(i).get("ReviewId").toString();
                        }
                    }
                }
                if (s.length() > 0) {
                    createConnect();
//                    Log.d("选择分发的计划",s);
                } else {
                    Toast.makeText(ReviewPlanUpActivity.this, "没有选择需要分发的计划", Toast.LENGTH_LONG).show();
                }

                break;
            default:
                break;
        }
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            v.setBackgroundColor(ContextCompat.getColor(this, R.color.main_color_3));
        } else if (event.getAction() == MotionEvent.ACTION_CANCEL) {
            v.setBackgroundColor(ContextCompat.getColor(this, R.color.main_color_2));
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            v.setBackgroundColor(ContextCompat.getColor(this, R.color.main_color_2));
        }
        return false;
    }


    /**
     * 打开复查计划表
     */
    protected void openDate() {

        // 初始化，只需要调用一次
        AssetsDatabaseManager.initManager(getApplication());
        // 获取管理对象，因为数据库需要通过管理对象才能够获取
        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
        // 通过管理对象获取数据库
        SQLiteDatabase db = mg.getDatabase("users.db");
        try {
            // 创建一个List集合，List集合的元素是Map
            listItems = new ArrayList<>();
            HashMap<String, Object> listItem;
            // 创建一个SimpleAdapter
            ReviewPlanUpDateListAdapter simpleAdapter;
            // 对数据库进行操作
            Cursor c = db.rawQuery("SELECT* FROM ELL_ReviewInfo", null);
            while (c.moveToNext()) {
                listItem = new HashMap<>();
                // 查找企业名称
                String s_c = "SELECT* FROM ELL_Business WHERE BusinessId = ?";
                Cursor cursor = db.rawQuery(s_c,
                        new String[]{c.getString(c.getColumnIndex("BusinessId"))});
                cursor.moveToFirst();

                listItem.put("Business", cursor.getString(cursor.getColumnIndex("BusinessName")));
                listItem.put("ReviewId", c.getString(c.getColumnIndex("ReviewId")));
                listItem.put("Address", c.getString(c.getColumnIndex("Address")));
                listItem.put("StatTime", c.getString(c.getColumnIndex("StartTime")));
                listItem.put("EndTime", c.getString(c.getColumnIndex("EndTime")));
                listItems.add(listItem);
                cursor.close();
            }
            // 创建一个SimpleAdapter
            simpleAdapter = new ReviewPlanUpDateListAdapter(ReviewPlanUpActivity.this, listItems,
                    R.layout.review_plan_up_list_item,
                    new String[]{"Business", "StatTime", "EndTime", "Address"},
                    new int[]{R.id.tx_add_check_up_01,
                            R.id.tx_add_check_up_04, R.id.tx_add_check_up_05, R.id.tx_add_check_up_03});
            // 为ListView设置Adapter
            list01.setAdapter(simpleAdapter);

            // 为ListView的列表项的单击事件绑定事件监听器
            list01.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                // 第position项被单击时激发该方法
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        final int position, long id) {
//                Intent intent = new Intent();
//                intent.setClass(EnterpriseInformationActivity.this, OrderDetailActivity.class);
//                startActivity(intent);
//                overridePendingTransition(R.anim.in_from_right,android.R.anim.fade_out);
//                    if(getIntent().getStringExtra("state").equals("1")){
                    Intent intent = new Intent();
                    intent.setClass(ReviewPlanUpActivity.this, ReviewImplementActivity.class);
                    intent.putExtra("name", "1");
                    intent.putExtra("ReviewId", listItems.get(position).get("ReviewId").toString());
                    startActivity(intent);
                    overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
//                    }

                }
            });


            c.close();
        } catch (Exception e) {
            Log.e("2数据库报错", e.toString());
        }
    }

    /*点击创建连接，两台设备建立连接*/
    private void createConnect() {
        ConnectivityManager mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();

        //判断网络连接状态
//        if (mNetworkInfo != null) {
        wifiManager_01 = (WifiManager) super.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager_01.getConnectionInfo();
        int ipAddress = wifiInfo == null ? 0 : wifiInfo.getIpAddress();
        if (wifiManager_01.isWifiEnabled() && ipAddress != 0) {
            dialogWifi_01();
        } else {

            //显示雷达扫描界面
            showPopupWindow();

            //设置"正在接受UDP请求，请求连接的设备将在此显示。。。"的提示
            /** 开启UDP接受线程*/
            UdpReceive udpreceive = new UdpReceive();
            udpreceive.start();

        }
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


    /**
     * 监听有没有wifi接入到wifi热点的线程
     */
    public class startNew extends Thread {
        public void run() {

//            while(getConnectIp().size()==0){
//               getConnectIp();
//            }
//
//            IP_DuiFangde = getConnectIp().get(0);
//            System.out.println("++++++++++++++getConnectDeviceIP()======================================="+getConnectDeviceIP());
//            //跳转到文件发送界面
//            if (isIp(IP_DuiFangde)&&IP_DuiFangde!=null) {
            // 如果是一个IP就跳转到文件发送界面

            Intent intent_filetrans2 = new Intent(ReviewPlanUpActivity.this, Files_Trans_Activity_01.class);
            intent_filetrans2.putExtra("isCreateHot", "1");//代表服务器显示的界面
//                intent_filetrans2.putStringArrayListExtra("iplist",getConnectIp());
            intent_filetrans2.putExtra("isShowListView", true);
            intent_filetrans2.putExtra("state", "jh");
            intent_filetrans2.putExtra("id", s);
//            Log.d("传输的复查计划",s);
            startActivity(intent_filetrans2);
//            } else {
//                //如果不是一个IP就弹出消息提示一下
//                System.out.println("没有连接热点的客户端");
//            }
//            恢复按钮为可点击
//            设置按钮可点击
//            while (true){
//                if(getConnectIp().size()>0)//m没有解决的bug,
//                {
//                    Intent intent_filetrans2 = new Intent(MainActivity.this, Files_Trans_Activity.class);
//               intent_filetrans2.putExtra("isCreateHot","1");//代表服务器显示的界面
//                    intent_filetrans2.putStringArrayListExtra("iplist",getConnectIp());
//                startActivity(intent_filetrans2);
//                    break;
//                }
//            }


        }

    }

    /**
     * UDP广播线程
     */
    public class BroadCastUdp extends Thread {
        private String dataString;
        private DatagramSocket udpSocket;
        public volatile boolean exit = false;

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
//                Log.i("tag", "show");
                if (run) {
                    run = false;
                    break;
                }
            }
//            Log.i("tag", "show");
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
     * 弹出确认对话框
     */
    protected void dialogWifi_01() {
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
                dialogWifiClose_01();
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
     * 弹出确认分发对话框
     */
    protected void dialogWifiClose_01() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("已关闭WiFi");
        builder.setMessage("是否继续？");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                createConnect();
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
                            Intent intent_filetrans = new Intent(ReviewPlanUpActivity.this, Files_Trans_Activity_01.class);
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


   /* void CreatConnection(final String name, final String key, final int type) {
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

    /*public String GetIpAddress() {
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
}
