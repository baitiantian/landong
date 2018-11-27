package com.jinan.ladongjiguan.anjiantong.activity;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Handler;

import com.jinan.ladongjiguan.anjiantong.PublicClass.AssetsDatabaseManager;
import com.jinan.ladongjiguan.anjiantong.PublicClass.ImageTools;
import com.jinan.ladongjiguan.anjiantong.utils.WifiUtil;
import com.jinan.ladongjiguan.anjiantong.utils.SharedPreferencesUtil;
import com.jinan.ladongjiguan.anjiantong.R;
import com.jinan.ladongjiguan.anjiantong.connect.WifiApAdmin;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
//When I wrote this, only God and I understood what I was doing
//Now, God only knows
public class Files_Trans_Activity_01 extends Activity implements View.OnClickListener {

    @BindView(R.id.examine_page_back)
    LinearLayout examinePageBack;
    @BindView(R.id.title_layout)
    TextView titleLayout;
    @BindView(R.id.tx_table)
    TextView txTable;
    @BindView(R.id.tv_ip)
    TextView tvIp;
    @BindView(R.id.tv_ip_address)
    TextView tvIpAddress;
    @BindView(R.id.lv)
    ListView lv;
    @BindView(R.id.user_num)
    TextView userNum;
    @BindView(R.id.image)
    ImageView image;
    private EditText txtIP;
    private Button btnSend;
    private ServerSocket server;
    private Files_Trans_Activity_01.Socket_Manager socket_Manager;
    private boolean out_recieve = true;
    private WifiManager wifiManager;
    private String isCreateHot;
    private ListView ipListView;
    private Files_Trans_Activity_01.Myadapter adapter;
    private ArrayList<HashMap<String, Object>> iplist;
    private static Boolean aBoolean = true;
    private String File_Name = "";

    private JSONArray jsonArray;
    private int size;
    private int port = 0;
    private String createJsonPath00 = "";
    private  Timer timer = new Timer();
    private String sJson = "";
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                //当发送的消息是0的时候，刷新listview
                case 0:
                    adapter.notifyDataSetChanged();
                    String s1 = "本机IP地址：" + getLocalIPAddress();
                    tvIpAddress.setText(s1);
                    break;
                case 1:
                    String s = "本机IP地址：" + getLocalIPAddress();
                    tvIpAddress.setText(s);
                    break;
                case 2:
                    Toast.makeText(getApplicationContext(), msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    String[] strings = File_Name.split(";");
                    if (strings.length > 2) {
                        Log.d("文件", "已连接上" + strings[1] + "(" + strings[2] + ")" + iplist.size());

                        HashMap<String, Object> map = new HashMap<>();
                        map.put("IP", strings[0]);
                        map.put("name", strings[1] + "(" + strings[2] + ")");
                        map.put("send", "0");
                        if (iplist.size() <= 0) {
                            iplist.add(map);
                            adapter = new Myadapter();
                            ipListView.setAdapter(adapter);
                        }
                        for (int i = 0; i < iplist.size(); i++) {
                            if (iplist.get(i).get("IP").equals(strings[0])) {
                                break;
                            } else if (!iplist.get(i).get("IP").equals(strings[0]) && i == iplist.size() - 1) {
                                iplist.add(map);
                                adapter = new Myadapter();
                                ipListView.setAdapter(adapter);
                            }
                        }
                        String s2 = iplist.size() + "人";
                        userNum.setText(s2);

//                        adapter.notifyDataSetChanged();
                        Toast.makeText(Files_Trans_Activity_01.this, "已连接上" + strings[1] + "(" + strings[2] + ")", Toast.LENGTH_LONG).show();
                        File file;
                        if (File_Name.length() > 0) {
                            String pathdir = Environment.getExternalStorageDirectory().getPath() + "/LanDong";
                            file = new File(pathdir + "/" + File_Name);
                            if (file.exists() && file.isFile()) {
                                file.delete();
                            }
                        }
                    } else {
                        if (titleLayout.getText().toString().substring(2, 4).equals("问题")) {
                            upDateCheck();

                        } else if (titleLayout.getText().toString().substring(2, 4).equals("计划")) {
                            Toast.makeText(Files_Trans_Activity_01.this, "计划接收", Toast.LENGTH_LONG).show();
                            upDatePlan();

                        }
                    }

//                    Toast.makeText(Files_Trans_Activity.this, titleLayout.getText().toString().substring(2,4), Toast.LENGTH_LONG).show();
                    break;
                case 4:
                    Toast.makeText(Files_Trans_Activity_01.this, "有图片接收", Toast.LENGTH_LONG).show();
                    break;
                case 5:
//                    Toast.makeText(Files_Trans_Activity.this, "有图片接收", Toast.LENGTH_LONG).show();
                    adapter.notifyDataSetChanged();
                    break;
                case 6:
                    //开始一个定时任务
                    Log.d("IP数据",getLocalIPAddress());
                    timer = new Timer();
                    TimerTask timerTask = new TimerTask() {
                        @Override
                        public void run() {
                            String[] strings = getLocalIPAddress().split("\\.");

                            if(strings.length>3){

                                Log.d("IP数据1",strings[3]);
                                if(strings[2].equals("43")&&!strings[3].equals("1")){

                                    createJsonPath00 = Environment.getExternalStorageDirectory().getPath() + "/LanDong/" +
                                            getLocalIPAddress() + ";" + sJson;
                                    writeFile(createJsonPath00, new JSONObject());
                                    handler.sendEmptyMessage(1);
                                    socket_Manager.SendFile(createJsonPath00, "192.168.43.1", Integer.parseInt("9999"));
                                }else if(aBoolean){
                                    handler.sendEmptyMessage(6);
                                }
                            }else if(aBoolean){
                                handler.sendEmptyMessage(6);
                            }


                        }
                    };
                    timer.schedule(timerTask, 1000);
                    break;
                case 7://手动连接WiFi热点
                    startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS)); //直接进入手机中的wifi网络设置界面
                    Toast.makeText(Files_Trans_Activity_01.this, "连接热点失败，请手动连接", Toast.LENGTH_LONG).show();
                    break;
                case 8://切换WiFi热点
                    WifiUtil.getIns().init(getApplicationContext());
                    WifiUtil.getIns().changeToWifi("LanDong","123456789");
                    break;
                default:
                    break;
            }


        }

    };
    private Button createJson;
    private String createJsonPath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.files_trans_main);
        ButterKnife.bind(this);
        examinePageBack.setOnClickListener(this);
        aBoolean = true;
        initView();
        //判断是创建热点，还是连接热点
        isCreateHot = getIntent().getStringExtra("isCreateHot");
        //获得连接热点的ip
//        iplist = getConnectIp();

        boolean isShowListView = getIntent().getBooleanExtra("isShowListView", false);
        //如果是创建热点，就显示listview,否则就不显示
        if (isShowListView) {
            ipListView = (ListView) findViewById(R.id.lv);
            //设置ip列表的listview
//            setClientIpsadapter();
        }
        wifiManager = (WifiManager) super.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        out_recieve = true;
        btnSetListnener();


        Thread listener = new Thread(new Runnable() {
            @Override
            public void run() {
                // 绑定端口
                port = 9999;
                while (port > 9000) {
                    try {
                        server = new ServerSocket(port);// 初始化server
                        break;
                    } catch (Exception e) {
                        port--;
                    }
                }

                if (server != null) { // 如果server不空
                    socket_Manager = new Socket_Manager(server);// 初始化socketManager
//                    Message.obtain(handler, 1, "本机IP地址：" + GetIpAddress()/* + " 监听端口:" + port */).sendToTarget();// 不知道这句干嘛
                    while (aBoolean) { // 接收文件，死循环

                        socket_Manager.ReceiveFile();// 定义一个字符串response

                    }
                } else {
//                    Message.obtain(handler, 1, "未能绑定端口").sendToTarget();
                }
            }
        });
        listener.start();

        iplist = new ArrayList<>();
        if (isCreateHot.equals("1")) {
            if (getIntent().getStringExtra("state").equals("jh")) {
                titleLayout.setText("分发计划");
                JsonControl("jh");//生成计划json文件
            } else {
                titleLayout.setText("汇总问题");
            }
            String s1 = "本机IP地址：" + getLocalIPAddress();
            if (Build.VERSION.SDK_INT >= 23) {
                try {
                    ComponentName comp = new ComponentName("com.jinan.ladongjiguan.wifidemo","com.jinan.ladongjiguan.wifidemo.MainActivity");
                    Intent it = new Intent();
                    it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//需要加这个不然会报错
                    it.setComponent(comp);
                    startActivity(it);
                }catch (Exception e){
                    Toast.makeText(this, "设置热点失败，请手动设置。", Toast.LENGTH_SHORT).show();
                    openAPUI();
                }
            }
            tvIpAddress.setText(s1);
        } else {
            if (getIntent().getStringExtra("state").equals("jh")) {
                titleLayout.setText("接收计划");
            } else {
                titleLayout.setText("上传问题");
                JsonControl("wt");//生成问题json文件
            }
            tvIp.setVisibility(View.VISIBLE);
            tvIp.setOnClickListener(this);
            if (getIntent().getStringExtra("state").equals("jh")) {
                titleLayout.setText("接收计划");
            } else {
                titleLayout.setText("上传问题");
                JsonControl("wt");//生成问题json文件
            }
            tvIp.setVisibility(View.VISIBLE);
            tvIp.setOnClickListener(this);
            tvIp.setText("组长");
            sJson = SharedPreferencesUtil.getStringData(this, "Account", "") + ";" +
                    SharedPreferencesUtil.getStringData(this, "Code", null) + ";" + ".json";


            timer = new Timer();
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {

                    String s = getLocalIPAddress();
                    Log.d("IP数据",s);
                    Log.d("IP数据2",s.split("\\.").length+"");
                    if(s.split("\\.").length>3){
                        String[] strings = s.split("\\.");
                        Log.d("IP数据0",s);
                        if(strings[2].equals("43")&&!strings[3].equals("1")){

                            createJsonPath00 = Environment.getExternalStorageDirectory().getPath() + "/LanDong/" +
                                    getLocalIPAddress() + ";" + sJson;
                            writeFile(createJsonPath00, new JSONObject());
                            handler.sendEmptyMessage(1);
                            socket_Manager.SendFile(createJsonPath00, "192.168.43.1", Integer.parseInt("9999"));
                        }else {
                            handler.sendEmptyMessage(6);
                        }
                    }else {
                        handler.sendEmptyMessage(6);
                    }


                }
            };
            //开始一个定时任务
            timer.schedule(timerTask, 2000);
//            String s1 = "本机IP地址：" + getLocalIPAddress();
//            tvIpAddress.setText(s1);
            userNum.setText("1人");
        }
    }

    private void btnSetListnener() {
        createJson.setOnClickListener(this);
        btnSend.setOnClickListener(this);
        tvIp.setOnClickListener(this);
    }

    /*初始化控件*/
    private void initView() {
        txtIP = (EditText) findViewById(R.id.txtIP);
        // 将对方的IP地址直接写入txtIP这个EditText控件
        txtIP.setText(CheckUpMainActivity.IP_DuiFangde);
        btnSend = (Button) findViewById(R.id.btnSend);
        createJson = (Button) findViewById(R.id.btn_json);

    }

    /*获取客户端的ip地址*/
    private void setClientIpsadapter() {
        //显示列表
        adapter = new Files_Trans_Activity_01.Myadapter();
        ipListView.setAdapter(adapter);
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
//                iplist = getConnectIp();
                handler.sendEmptyMessage(0);
            }
        };
        //开始一个定时任务
        timer.schedule(timerTask, 1000, 500);
        /*检测连接热点是否正确*/
        Timer timeLanDong = new Timer();
        TimerTask taskLanDong = new TimerTask() {
            @Override
            public void run() {
                WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                Log.d("wifiInfo", wifiInfo.toString());
                Log.d("SSID",wifiInfo.getSSID());
                if(!wifiInfo.getSSID().equals("LanDong")){
//                    handler.sendEmptyMessage(7); //直接进入手机中的wifi网络设置界面
                    handler.sendEmptyMessage(8); //直接切换
                }
            }
        };
        timeLanDong.schedule(taskLanDong,7000);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {    //生成json文件
            case R.id.examine_page_back:
                onBackPressed();
                break;
            case R.id.btn_json:
                JsonControl("1");
                break;
            //发送文件
            case R.id.btnSend:

                //可以在这里判断是客户端还是服务器
                //如果是1的话，表示是服务器
                if ("1".equals(isCreateHot)) {
                    final String ipAddress = txtIP.getText().toString();
                    final int port = Integer.parseInt("9999");
                    //下发的文件，这个地方fileName可以改成.json文件
//                    String fileName="list.xml";
                    //从指定的路径中取文件
//                    createJsonPath=Environment.getExternalStorageDirectory().getPath() +"/LanDong/"+fileName;
                    socket_Manager.SendFile(createJsonPath, ipAddress, port);
                    Toast.makeText(Files_Trans_Activity_01.this, "我是服务端", Toast.LENGTH_LONG).show();

                }
                //如果是一个客户端，得到是热点的ip地址，隐藏listview
                else {
                    //这是客户端的操作
                    Toast.makeText(Files_Trans_Activity_01.this, "我是客户端", Toast.LENGTH_LONG).show();
                    final String hotipAddress = CheckUpMainActivity.IP_DuiFangde;
                    final int port = Integer.parseInt("9999");
                    //这是客户端要上传的文件，后期要改成.json文件
//                    String fileName="lis2t.xml";
//                    String path=Environment.getExternalStorageDirectory().getPath() +"/LanDong/"+fileName;
                    socket_Manager.SendFile(createJsonPath, hotipAddress, port);
                }

                break;
            case R.id.tv_ip:
                final String hotipAddress = "192.168.43.1";
                final int port = Integer.parseInt("9999");
                //这是客户端要上传的文件，后期要改成.json文件
//                    String fileName="lis2t.xml";
//                    String path=Environment.getExternalStorageDirectory().getPath() +"/LanDong/"+fileName;
                socket_Manager.SendFile(createJsonPath, hotipAddress, port);
                image.setVisibility(View.VISIBLE);
                break;
            case R.id.delete_layout:
                if(iplist.size()>0){
                    Toast.makeText(Files_Trans_Activity_01.this, "开始分发", Toast.LENGTH_LONG).show();

                    for(int i=0;i<iplist.size();i++){
                        Timer timer = new Timer();
                        final int finalI = i;
                        TimerTask timerTask = new TimerTask() {
                            @Override
                            public void run() {
                                socket_Manager.SendFile(createJsonPath, iplist.get(finalI).get("IP").toString(), Integer.parseInt("9999"));
                                HashMap<String, Object> map = new HashMap<>();
                                map.put("IP", iplist.get(finalI).get("IP").toString());
                                map.put("name", iplist.get(finalI).get("name").toString());
                                map.put("send", "1");
                                iplist.set(finalI, map);
                                handler.sendEmptyMessage(5);
                            }
                        };
                        timer.schedule(timerTask,500*(i+1));
                    }
                }else {
                    Toast.makeText(Files_Trans_Activity_01.this, "没有连接的对象", Toast.LENGTH_LONG).show();
                }
                break;
            default:
                break;
        }


    }


    class Myadapter extends BaseAdapter {

        @Override
        public int getCount() {
            return iplist.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(Files_Trans_Activity_01.this).inflate(R.layout.list_item, null);
                holder = new ViewHolder();
                holder.tv = (TextView) convertView.findViewById(R.id.tv);
                holder.imageView = (ImageView) convertView.findViewById(R.id.send);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            Log.d("文件数据", iplist.get(position).toString());
            holder.tv.setText(iplist.get(position).get("name").toString());
            holder.tv.setOnClickListener(listViewItemListener(position));
            if (iplist.get(position).get("send").toString().equals("1")) {
                holder.imageView.setVisibility(View.VISIBLE);
            }
            return convertView;

        }
    }

    /*ListView设置条目点击*/
    @NonNull
    private View.OnClickListener listViewItemListener(final int position) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(titleLayout.getText().toString().substring(2, 4).equals("计划")){

                    txtIP.setText(iplist.get(position).get("name").toString());
                    final String ipAddress = iplist.get(position).get("IP").toString();
                    final int port = Integer.parseInt("9999");
                    socket_Manager.SendFile(createJsonPath, ipAddress, port);
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("IP", iplist.get(position).get("IP").toString());
                    map.put("name", iplist.get(position).get("name").toString());
                    map.put("send", "1");
                    iplist.set(position, map);
                    adapter.notifyDataSetChanged();
                }
//                if(ImagePath.length()>0){
//                    socket_Manager.SendFile(ImagePath, ipAddress, port);
//                }
            }
        };
    }


    class ViewHolder {
        public TextView tv;
        public ImageView imageView;
    }

    @Override
    public void finish() {
        //退出这个页面时，清空IP_DuiFangde的值
        CheckUpMainActivity.IP_DuiFangde = null;
        super.finish();

    }
    /**
     * 获取本机ip方法
     */
    private String getLocalIPAddress() {
        try {
            for (Enumeration en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = (NetworkInterface) en.nextElement();
                for (Enumeration enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = (InetAddress) enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress()) {

                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) {

        }
        return "192.168.43.1";
    }

    public String GetIpAddress() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int i = wifiInfo.getIpAddress();
        String a = (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF) + "." + ((i >> 24) & 0xFF);
        String b = "0.0.0.0";
        if (a.equals(b)) {
            a = "192.168.43.1";// 当手机当作WIFI热点的时候，自身IP地址为192.168.43.1
        }
        return a;
    }


    private class Socket_Manager {
        private ServerSocket server;
        private int length;
        private byte[] sendBytes;
        private Socket socket;
        private DataOutputStream dos;
        private FileInputStream fis;
        private boolean bool;

        public Socket_Manager(ServerSocket server) {
            this.server = server;
        }


        // 接收文件
        public void ReceiveFile() {

            try {
                // 接收文件名
                Socket socket = server.accept();
                //接受到的文件存放在SD卡LanDong目录下面
                String pathdir = Environment.getExternalStorageDirectory().getPath() + "/LanDong";
                byte[] inputByte = null;
                long length = 0;
                DataInputStream dis = null;
                FileOutputStream fos = null;
                String filePath;
                long L;

                try {

                    dis = new DataInputStream(socket.getInputStream());
                    File f = new File(pathdir);
                    if (!f.exists()) {
                        f.mkdir();
                    }
                    File_Name = dis.readUTF();
                    filePath = pathdir + "/" + File_Name;
                    fos = new FileOutputStream(new File(filePath));
                    inputByte = new byte[1024];
                    L = f.length();
                    System.out.println("文件路径：" + filePath);
                    double rfl = 0;
                    L = dis.readLong();
                    System.out.println("文件长度" + L + "kB");
                    System.out.println("开始接收数据...");


                    while ((length = dis.read(inputByte, 0, inputByte.length)) > 0&&aBoolean) {
                        rfl += length;
                        fos.write(inputByte, 0, (int) length);
                        System.out.println("rfl:" + rfl);
                        fos.flush();

                    }
                    fos.close();
                    dis.close();
                    socket.close();
                    System.out.println("完成接收：" + filePath);
                    //接受完成信号
                    handler.sendEmptyMessage(3);


                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        //String fileName,
        public void SendFile(final String path, final String ipAddress, final int port) {

            length = 0;
            sendBytes = null;
            socket = null;
            dos = null;
            fis = null;
            bool = false;
            new Thread(new Runnable() {
                public void run() {
                    try {
                        File file = new File(path); // 要传输的文件路径
                        long l = file.length();
                        socket = new Socket();
                        socket.connect(new InetSocketAddress(ipAddress, port));
                        dos = new DataOutputStream(socket.getOutputStream());
                        fis = new FileInputStream(path);
                        sendBytes = new byte[1024];
                        dos.writeUTF(file.getName());// 传递文件名
                        dos.flush();
                        dos.writeLong((long) file.length() / 1024 + 1);
                        dos.flush();

                        while ((length = fis.read(sendBytes, 0, sendBytes.length)) > 0&&aBoolean) {
                            dos.write(sendBytes, 0, length);
                            dos.flush();
                        }


                    } catch (Exception e) {
                        System.out.println("客户端文件传输异常");
                        bool = false;
                        e.printStackTrace();
                        SendFile(path,ipAddress,port);
                    } finally {
                        if (dos != null)
                            try {
                                dos.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        if (fis != null)
                            try {
                                fis.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        if (socket != null)
                            try {
                                socket.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                    }
                }

            }).start();
            System.out.println(bool ? "成功" : "失败");
//            return fileName + " 发送完成";

        }

    }


    /**
     * 点击两次退出程序
     */
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//
//            WifiApAdmin.closeWifiAp(wifiManager);
//            File file = new File(createJsonPath);
//            if(file.exists()&&file.isFile()){
//                file.delete();
//            }
//
//            finish();
//
//            return true;
//        }
//        return super.onKeyDown(keyCode, event);
//    }
    @Override
    public void onBackPressed() {
        WifiApAdmin.closeWifiAp(wifiManager);
        File file;
        if (createJsonPath.length() > 0) {
            file = new File(createJsonPath);
            if (file.exists() && file.isFile()) {
                file.delete();
            }
        } else if (File_Name.length() > 0) {
            String pathdir = Environment.getExternalStorageDirectory().getPath() + "/LanDong";
            file = new File(pathdir + "/" + File_Name);
            if (file.exists() && file.isFile()) {
                file.delete();
            }
        }
        aBoolean = false;
        port = 0;
        try {
            socket_Manager.server.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finish();
    }

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
            while ((line = br.readLine()) != null&&aBoolean) {
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


    public void JsonControl(String s) {

        // 初始化，只需要调用一次
        AssetsDatabaseManager.initManager(getApplication());
        // 获取管理对象，因为数据库需要通过管理对象才能够获取
        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
        // 通过管理对象获取数据库
        SQLiteDatabase db = mg.getDatabase("users.db");
        JSONObject appObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        String[] strings = getIntent().getStringExtra("id").split(",");
        try {
//            appObject.put("type", "type");
//            appObject.put("username", "username");
//            appObject.put("password", "password");
//            appObject.put("uniqueMark", "uniqueMark");
//            appObject.put("userId", "userId");
//            appObject.put("sendTime", "sendTime");
            switch (s) {
                case "jh"://生成分发计划所用json

                    for (String string1 : strings) {

                        Cursor cursor = db.rawQuery("SELECT* FROM ELL_ReviewInfo WHERE ReviewId = ?",
                                new String[]{string1});
                        cursor.moveToFirst();
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("ReviewId", cursor.getString(cursor.getColumnIndex("ReviewId")));
                        jsonObject.put("BusinessId", cursor.getString(cursor.getColumnIndex("BusinessId")));
                        jsonObject.put("DocumentId", cursor.getString(cursor.getColumnIndex("DocumentId")));
                        jsonObject.put("CompanyId", cursor.getString(cursor.getColumnIndex("CompanyId")));
                        jsonObject.put("AddUser", cursor.getString(cursor.getColumnIndex("AddUser")));
                        jsonObject.put("AddTime", cursor.getString(cursor.getColumnIndex("AddTime")));
                        jsonObject.put("Address", cursor.getString(cursor.getColumnIndex("Address")));
                        jsonObject.put("StartTime", cursor.getString(cursor.getColumnIndex("StartTime")));
                        jsonObject.put("EndTime", cursor.getString(cursor.getColumnIndex("EndTime")));
                        jsonObject.put("documentnumber", cursor.getString(cursor.getColumnIndex("documentnumber")));
                        Cursor cursor1 = db.rawQuery("SELECT* FROM ELL_ReviewGroupInfo WHERE ReviewId = ?",
                                new String[]{cursor.getString(cursor.getColumnIndex("ReviewId"))});
                        JSONArray jsonArray1 = new JSONArray();
                        while (cursor1.moveToNext()) {
                            JSONObject jsonObject1 = new JSONObject();
                            jsonObject1.put("GroupId", cursor1.getString(cursor1.getColumnIndex("GroupId")));
                            jsonObject1.put("ReviewId", cursor1.getString(cursor1.getColumnIndex("ReviewId")));
                            jsonObject1.put("Headman", cursor1.getString(cursor1.getColumnIndex("Headman")));
                            Cursor cursor2 = db.rawQuery("SELECT* FROM ELL_ReviewMember WHERE GroupId = ?",
                                    new String[]{cursor1.getString(cursor1.getColumnIndex("GroupId"))});
                            JSONArray jsonArray2 = new JSONArray();
                            while (cursor2.moveToNext()) {
                                JSONObject jsonObject2 = new JSONObject();
                                jsonObject2.put("MemberId", cursor2.getString(cursor2.getColumnIndex("MemberId")));
                                jsonObject2.put("GroupId", cursor2.getString(cursor2.getColumnIndex("GroupId")));
                                jsonObject2.put("Member", cursor2.getString(cursor2.getColumnIndex("Member")));
                                jsonArray2.put(jsonObject2);
                            }
                            Cursor cursor4 = db.rawQuery("SELECT* FROM ELL_ReviewDangerInfo WHERE GroupId = ?",
                                    new String[]{cursor1.getString(cursor1.getColumnIndex("GroupId"))});
                            JSONArray jsonArray4 = new JSONArray();
                            while (cursor4.moveToNext()) {
                                JSONObject jsonObject4 = new JSONObject();
                                jsonObject4.put("ReviewDangerInfoId", cursor4.getString(cursor4.getColumnIndex("ReviewDangerInfoId")));
                                jsonObject4.put("HiddenDangerId", cursor4.getString(cursor4.getColumnIndex("HiddenDangerId")));
                                jsonObject4.put("GroupId", cursor4.getString(cursor4.getColumnIndex("GroupId")));
                                jsonArray4.put(jsonObject4);
                            }
                            cursor4.close();
                            cursor2.close();
                            jsonObject1.put("ELL_ReviewDangerInfo", jsonArray4);
                            jsonObject1.put("ELL_ReviewMember", jsonArray2);
                            jsonArray1.put(jsonObject1);
                        }
                        jsonObject.put("ELL_ReviewGroupInfo", jsonArray1);
                        cursor1.close();
                        Cursor cursor3 = db.rawQuery("SELECT* FROM ELL_HiddenDanger WHERE ReviewId = ?",
                                new String[]{string1});
                        JSONArray jsonArray3 = new JSONArray();
                        while (cursor3.moveToNext()) {
                            JSONObject jsonObject3 = new JSONObject();
                            jsonObject3.put("HiddenDangerId", cursor3.getString(cursor3.getColumnIndex("HiddenDangerId")));
                            jsonObject3.put("ReviewId", cursor3.getString(cursor3.getColumnIndex("ReviewId")));
                            jsonObject3.put("yhdd", cursor3.getString(cursor3.getColumnIndex("yhdd")));
                            jsonObject3.put("yhms", cursor3.getString(cursor3.getColumnIndex("yhms")));
                            jsonObject3.put("zgqx", cursor3.getString(cursor3.getColumnIndex("zgqx")));
                            jsonObject3.put("zglx", cursor3.getString(cursor3.getColumnIndex("zglx")));
                            jsonObject3.put("checkresult", cursor3.getString(cursor3.getColumnIndex("checkresult")));
                            jsonObject3.put("disposeresult", cursor3.getString(cursor3.getColumnIndex("disposeresult")));
                            jsonObject3.put("YHZGHTP", cursor3.getString(cursor3.getColumnIndex("YHZGHTP")));
                            jsonObject3.put("AddUsers", cursor3.getString(cursor3.getColumnIndex("AddUsers")));
                            jsonArray3.put(jsonObject3);
                        }
                        cursor3.close();
                        cursor.close();
                        jsonObject.put("ELL_HiddenDanger", jsonArray3);
                        jsonArray.put(jsonObject);
                    }
                    appObject.put("ELL_ReviewInfo", jsonArray);
                    break;
                case "wt":
                    for (String string : strings) {

                        Cursor cursor = db.rawQuery("SELECT* FROM ELL_HiddenDanger WHERE HiddenDangerId = ?",
                                new String[]{string});
                        cursor.moveToFirst();
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("HiddenDangerId", cursor.getString(cursor.getColumnIndex("HiddenDangerId")));
                        jsonObject.put("ReviewId", cursor.getString(cursor.getColumnIndex("ReviewId")));
                        jsonObject.put("yhdd", cursor.getString(cursor.getColumnIndex("yhdd")));
                        jsonObject.put("yhms", cursor.getString(cursor.getColumnIndex("yhms")));
                        jsonObject.put("zgqx", cursor.getString(cursor.getColumnIndex("zgqx")));
                        jsonObject.put("zglx", cursor.getString(cursor.getColumnIndex("zglx")));
                        jsonObject.put("checkresult", cursor.getString(cursor.getColumnIndex("checkresult")));
                        jsonObject.put("disposeresult", cursor.getString(cursor.getColumnIndex("disposeresult")));
                        jsonObject.put("YHZGHTP", cursor.getString(cursor.getColumnIndex("YHZGHTP")));
                        jsonObject.put("AddUsers", cursor.getString(cursor.getColumnIndex("AddUsers")));
                        Cursor cursor1 = db.rawQuery("SELECT* FROM ELL_Photo WHERE CheckId = ?",
                                new String[]{string});

                        if (cursor1.getCount() != 0) {
//                            cursor1.moveToFirst();
//                            JSONObject jsonObject1 = new JSONObject();
//                            jsonObject1.put("PhotoId", cursor1.getString(cursor1.getColumnIndex("PhotoId")));
//                            jsonObject1.put("CheckId", cursor1.getString(cursor1.getColumnIndex("CheckId")));
//                            jsonObject1.put("Address", cursor1.getString(cursor1.getColumnIndex("Address")));
//                            Bitmap bitmap = ImageTools.getPhotoFromSDCard("/sdcard/LanDong", cursor1.getString(cursor1.getColumnIndex("Address")));
//                            String s1 = convertIconToString(bitmap);
//                            jsonObject1.put("bitmap", s1);
//                            cursor1.close();
//                            jsonObject.put("ELL_Photo", jsonObject1);
                            JSONArray jsonArrayPhoto = new JSONArray();
                            while (cursor1.moveToNext()){
                                JSONObject jsonObject1 = new JSONObject();
                                jsonObject1.put("PhotoId", cursor1.getString(cursor1.getColumnIndex("PhotoId")));
                                jsonObject1.put("CheckId", cursor1.getString(cursor1.getColumnIndex("CheckId")));
                                jsonObject1.put("Address", cursor1.getString(cursor1.getColumnIndex("Address")));
                                Bitmap bitmap = ImageTools.getPhotoFromSDCard("/sdcard/LanDong", cursor1.getString(cursor1.getColumnIndex("Address")));
                                String s1 = convertIconToString(bitmap);
                                jsonObject1.put("bitmap", s1);

                                jsonArrayPhoto.put(jsonObject1);
                            }
                            jsonObject.put("ELL_Photo", jsonArrayPhoto);
                        }
                        cursor1.close();
                        cursor.close();
                        jsonArray.put(jsonObject);
                    }
                    appObject.put("ELL_HiddenDanger", jsonArray);
                    break;
                default:
                    break;

            }
            // 文件存储路径
//            String createJsonPath = System.getProperty("catalina.home") + "\\jsondata.json";
            createJsonPath = Environment.getExternalStorageDirectory().getPath() + "/LanDong/" +
                    SharedPreferencesUtil.getStringData(this, "Code", null) + ".json";
            // 将数据存储到json文件
            writeFile(createJsonPath, appObject);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("数据报错",e.toString());
        }

    }

    /**
     * 把Json格式的字符串写到文件
     *
     * @param filePath
     * @throws IOException
     */
    public static void writeFile(String filePath, JSONObject appObject) {

        try {
            File file = new File(filePath);
            // 如果文件不存在、则创建该文件
            if (!file.exists()) {
                file.createNewFile();
            }
            JSONObject jsonObj;
            // 获取JSON数据字符串
            String str = ReadFile(filePath);
            if (str.length() > 0) {
                // 获取JSON对象
                jsonObj = new JSONObject(str);
                // 获取JSON集合对象
                JSONArray arr = null;
                try {
                    arr = jsonObj.getJSONArray("appdata");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                arr.put(appObject);
            } else {
                JSONArray appArray = new JSONArray();
                appArray.put(appObject);
                jsonObj = new JSONObject();
                jsonObj.accumulate("appdata", appArray);
            }
            FileWriter fileWriter = new FileWriter(filePath);
            PrintWriter out = new PrintWriter(fileWriter);
            out.write(jsonObj.toString());
            out.println();

            fileWriter.close();
            out.close();


        } catch (Exception e) {
            e.printStackTrace();
            Log.e("生成数据文件报错",e.toString());
        }

    }



    /**
     * 读JSON文件，返回字符串
     */
    public static String ReadFile(String path) {
        File file = new File(path);
        BufferedReader reader = null;
        // 构造最后返回的json串
        String laststr = "";
        try {
            // 以行为单位读取文件内容，一次读一整行：
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            // 一次读入一行，直到读入null为文件结束
            while ((tempString = reader.readLine()) != null&&aBoolean) {
                // 拼接数据信息
                laststr = laststr + tempString;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
        return laststr;
    }

    /**
     * 更新计划数据库
     */
    protected void upDatePlan() {

        // 初始化，只需要调用一次
        AssetsDatabaseManager.initManager(getApplication());
        // 获取管理对象，因为数据库需要通过管理对象才能够获取
        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
        // 通过管理对象获取数据库
        SQLiteDatabase db = mg.getDatabase("users.db");
        if (File_Name.length() > 0) {
            String pathdir = Environment.getExternalStorageDirectory().getPath() + "/LanDong";

            // 获取JSON数据的字符串
            String JsonContext = ReadFile(pathdir + "/" + File_Name);
            if (JsonContext.length() > 0) {
                // 获取文件JSON对象
                JSONObject json = null;
                try {
                    json = new JSONObject(JsonContext);
                    JSONArray Array = json.getJSONArray("appdata");
                    JSONArray jsonArray = Array.getJSONObject(0).getJSONArray("ELL_ReviewInfo");
//                    Log.d("所有的json数据", jsonArray + "");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
//                        Log.d("计划表的json数据", jsonObject + "");
//                        db.execSQL("REPLACE INTO ELL_ReviewInfo VALUES('" +
//                                jsonObject.getString("ReviewId") + "','" +
//                                jsonObject.getString("DocumentId") + "','" +
//                                jsonObject.getString("BusinessId") + "','" +
//                                jsonObject.getString("StartTime") + "','" +
//                                jsonObject.getString("EndTime") + "','" +
//                                jsonObject.getString("Address") + "','" +
//                                jsonObject.getString("CompanyId") + "','" +
//                                jsonObject.getString("AddUser") + "','" +
//                                jsonObject.getString("AddTime") + "','" +
//                                jsonObject.getString("documentnumber") + "')");
                        JSONArray jsonArray1 = jsonObject.getJSONArray("ELL_ReviewGroupInfo");
                        ContentValues values = new ContentValues();
                        values.put("ReviewId", jsonObject.getString("ReviewId"));
                        values.put("DocumentId", jsonObject.getString("DocumentId"));
                        values.put("BusinessId", jsonObject.getString("BusinessId"));
                        values.put("StartTime", jsonObject.getString("StartTime"));
                        values.put("EndTime", jsonObject.getString("EndTime"));
                        values.put("Address", jsonObject.getString("Address"));
                        values.put("CompanyId", jsonObject.getString("CompanyId"));
                        values.put("AddUser", jsonObject.getString("AddUser"));
                        values.put("AddTime", jsonObject.getString("AddTime"));
                        values.put("documentnumber", jsonObject.getString("documentnumber"));
                        db.replace("ELL_ReviewInfo", null, values);
                        for (int i1 = 0; i1 < jsonArray1.length(); i1++) {
                            JSONObject jsonObject1 = jsonArray1.getJSONObject(i1);
//                            db.execSQL("REPLACE INTO ELL_ReviewGroupInfo VALUES('" +
//                                    jsonObject1.getString("GroupId") + "','" +
//                                    jsonObject1.getString("ReviewId") + "','" +
//                                    jsonObject1.getString("Headman") + "')");
                            ContentValues values1 = new ContentValues();
                            values1.put("GroupId",jsonObject1.getString("GroupId"));
                            values1.put("ReviewId",jsonObject1.getString("ReviewId"));
                            values1.put("Headman",jsonObject1.getString("Headman"));
                            db.replace("ELL_ReviewGroupInfo", null, values1);
                            JSONArray jsonArray2 = jsonObject1.getJSONArray("ELL_ReviewMember");
                            for (int i2 = 0; i2 < jsonArray2.length(); i2++) {
                                JSONObject jsonObject2 = jsonArray2.getJSONObject(i2);
//                                db.execSQL("REPLACE INTO ELL_ReviewMember VALUES('" +
//                                        jsonObject2.getString("MemberId") + "','" +
//                                        jsonObject2.getString("GroupId") + "','" +
//                                        jsonObject2.getString("Member") + "')");
                                ContentValues values2 = new ContentValues();
                                values2.put("MemberId", jsonObject2.getString("MemberId"));
                                values2.put("GroupId",jsonObject2.getString("GroupId"));
                                values2.put("Member",jsonObject2.getString("Member"));
                                db.replace("ELL_ReviewMember", null, values2);
                            }
                            JSONArray jsonArray3 = jsonObject1.getJSONArray("ELL_ReviewDangerInfo");
                            for (int i3 = 0; i3 < jsonArray2.length(); i3++) {
                                JSONObject jsonObject3 = jsonArray3.getJSONObject(i3);
//                                db.execSQL("REPLACE INTO ELL_ReviewDangerInfo VALUES('" +
//                                        jsonObject3.getString("ReviewDangerInfoId") + "','" +
//                                        jsonObject3.getString("HiddenDangerId") + "','" +
//                                        jsonObject3.getString("GroupId") + "')");
                                ContentValues values3 = new ContentValues();
                                values3.put("ReviewDangerInfoId",jsonObject3.getString("ReviewDangerInfoId"));
                                values3.put("HiddenDangerId",jsonObject3.getString("HiddenDangerId"));
                                values3.put("GroupId", jsonObject3.getString("GroupId"));
                                db.replace("ELL_ReviewDangerInfo", null, values3);
                            }
                        }
                        JSONArray jsonArray4 = jsonObject.getJSONArray("ELL_HiddenDanger");
                        for(int i4 = 0;i4<jsonArray4.length();i4++){
                            JSONObject jsonObject4 = jsonArray4.getJSONObject(i4);
                            ContentValues values4 = new ContentValues();
                            values4.put("HiddenDangerId", jsonObject4.getString("HiddenDangerId"));
                            values4.put("ReviewId", jsonObject4.getString("ReviewId"));
                            values4.put("yhdd", jsonObject4.getString("yhdd"));
                            values4.put("yhms", jsonObject4.getString("yhms"));
                            values4.put("zgqx", jsonObject4.getString("zgqx"));
                            values4.put("zglx", jsonObject4.getString("zglx"));
                            values4.put("checkresult", jsonObject4.getString("checkresult"));
                            values4.put("disposeresult", jsonObject4.getString("disposeresult"));
                            values4.put("YHZGHTP", jsonObject4.getString("YHZGHTP"));
                            values4.put("AddUsers", jsonObject4.getString("AddUsers"));
                            db.replace("ELL_HiddenDanger", null, values4);
//                            db.execSQL("REPLACE INTO ELL_HiddenDanger VALUES('" +
//                                    jsonObject4.getString("HiddenDangerId") + "','" +
//                                    jsonObject4.getString("ReviewId") + "','" +
//                                    jsonObject4.getString("yhdd") + "','" +
//                                    jsonObject4.getString("yhms") + "','" +
//                                    jsonObject4.getString("zgqx") + "','" +
//                                    jsonObject4.getString("zglx") + "','" +
//                                    jsonObject4.getString("checkresult") + "','" +
//                                    jsonObject4.getString("disposeresult") + "','" +
//                                    jsonObject4.getString("YHZGHTP") + "')");

                        }

                    }
                    image.setVisibility(View.VISIBLE);
                } catch (JSONException e) {
                    Log.e("写入数据库报错", e + "");

                }


            }
        }
    }

    protected void upDateCheck() {
        // 初始化，只需要调用一次
        AssetsDatabaseManager.initManager(getApplication());
        // 获取管理对象，因为数据库需要通过管理对象才能够获取
        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
        // 通过管理对象获取数据库
        SQLiteDatabase db = mg.getDatabase("users.db");
        if (File_Name.length() > 0) {
            String pathdir = Environment.getExternalStorageDirectory().getPath() + "/LanDong";

            // 获取JSON数据的字符串
            String JsonContext = ReadFile(pathdir + "/" + File_Name);
            if (JsonContext.length() > 0) {
                // 获取文件JSON对象
                JSONObject json = null;
                try {
                    json = new JSONObject(JsonContext);
                    JSONArray Array = json.getJSONArray("appdata");
                    JSONArray jsonArray = Array.getJSONObject(0).getJSONArray("ELL_HiddenDanger");
//                    Log.d("所有的json数据", jsonArray + "");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
//                        Log.d("问题表的json数据", jsonObject + "");
                        ContentValues values = new ContentValues();
                        values.put("HiddenDangerId", jsonObject.getString("HiddenDangerId"));
                        values.put("ReviewId", jsonObject.getString("ReviewId"));
                        values.put("yhdd", jsonObject.getString("yhdd"));
                        values.put("yhms", jsonObject.getString("yhms"));
                        values.put("zgqx", jsonObject.getString("zgqx"));
                        values.put("zglx", jsonObject.getString("zglx"));
                        values.put("checkresult", jsonObject.getString("checkresult"));
                        values.put("disposeresult", jsonObject.getString("disposeresult"));
                        values.put("YHZGHTP", jsonObject.getString("YHZGHTP"));
                        values.put("AddUsers", jsonObject.getString("AddUsers"));
                        db.replace("ELL_HiddenDanger", null, values);

                        if(jsonObject.has("ELL_Photo")){
//                            JSONObject jsonObject1 = jsonObject.getJSONObject("ELL_Photo");
////                            db.execSQL("REPLACE INTO ELL_Photo VALUES('" +
////                                    jsonObject1.getString("PhotoId") + "','" +
////                                    jsonObject1.getString("CheckId") + "','" +
////                                    jsonObject1.getString("Address") + "')");
//                            ContentValues values3 = new ContentValues();
//                            values3.put("PhotoId", jsonObject1.getString("PhotoId"));
//                            values3.put("CheckId",jsonObject1.getString("CheckId"));
//                            values3.put("Address", jsonObject1.getString("Address"));
//                            db.replace("ELL_Photo", null, values3);
//                            Bitmap bitmap = convertStringToIcon(jsonObject1.getString("bitmap"));
//                            ImageTools.savePhotoToSDCard(bitmap, "/sdcard/LanDong", jsonObject1.getString("Address"));
                            JSONArray jsonArrayPhoto = jsonObject.getJSONArray("ELL_Photo");
                            for (int x = 0; x < jsonArrayPhoto.length(); x++){

                                JSONObject jsonObject1 =jsonArrayPhoto.getJSONObject(x);
                                values = new ContentValues();
                                values.put("PhotoId", jsonObject1.getString("PhotoId"));
                                values.put("CheckId", jsonObject1.getString("CheckId"));
                                values.put("Address", jsonObject1.getString("Address"));
                                db.replace("ELL_Photo", null, values);
//                            db.execSQL("REPLACE INTO ELL_Photo VALUES('" +
//                                    jsonObject1.getString("PhotoId") + "','" +
//                                    jsonObject1.getString("CheckId") + "','" +
//                                    jsonObject1.getString("Address") + "')");
                                Bitmap bitmap = convertStringToIcon(jsonObject1.getString("bitmap"));
                                ImageTools.savePhotoToSDCard(bitmap, "/sdcard/LanDong", jsonObject1.getString("Address"));
                            }
                        }

                    }
//                    Log.d("文件名切割", File_Name.substring(0, File_Name.length() - 5));
                    Cursor cursor = db.rawQuery("SELECT* FROM Base_User WHERE Code = ?",
                            new String[]{File_Name.substring(0, File_Name.length() - 5)});
                    cursor.moveToFirst();
                    String s = "";
                    try {
                        s = cursor.getString(cursor.getColumnIndex("RealName"));
                    }catch (Exception e){
                        Log.e("打开用户数据错误",e.toString());
                    }
                    Toast.makeText(Files_Trans_Activity_01.this, s +
                            File_Name.substring(0, File_Name.length() - 5) +
                            "的问题汇总完成", Toast.LENGTH_LONG).show();
                    cursor.close();

                } catch (JSONException e) {
                    Log.e("写入数据库报错", e + "");

                }


            }
        }
    }

    @Override
    protected void onDestroy() {
        aBoolean = false;
        port = 0;
        super.onDestroy();
    }

    /**
     * 图片转成string
     *
     * @param bitmap
     * @return
     */
    public static String convertIconToString(Bitmap bitmap)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();// outputstream
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] appicon = baos.toByteArray();// 转为byte数组
        return Base64.encodeToString(appicon, Base64.DEFAULT);

    }

    /**
     * string转成bitmap
     *
     * @param st
     */
    public static Bitmap convertStringToIcon(String st)
    {
        // OutputStream out;
        Bitmap bitmap = null;
        try
        {
            // out = new FileOutputStream("/sdcard/aa.jpg");
            byte[] bitmapArray;
            bitmapArray = Base64.decode(st, Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0,
                    bitmapArray.length);
            // bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            return bitmap;
        }
        catch (Exception e)
        {
            return null;
        }
    }
    /**
     * 打开网络共享与热点设置页面
     */
    private void openAPUI() {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
        startActivity(intent);
    }
}