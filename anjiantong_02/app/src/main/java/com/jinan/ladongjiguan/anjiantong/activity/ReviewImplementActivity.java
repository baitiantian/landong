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
import android.view.WindowManager;
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
import com.jinan.ladongjiguan.anjiantong.utils.GPSUtil;
import com.jinan.ladongjiguan.anjiantong.utils.WifiUtil;
import com.jinan.ladongjiguan.anjiantong.adapter.ProblemListAdapter;
import com.jinan.ladongjiguan.anjiantong.utils.SharedPreferencesUtil;
import com.jinan.ladongjiguan.anjiantong.PublicClass.Utility;
import com.jinan.ladongjiguan.anjiantong.PublicClass.WifiPermissions;
import com.jinan.ladongjiguan.anjiantong.R;
import com.jinan.ladongjiguan.anjiantong.connect.WifiAdmin;
import com.jinan.ladongjiguan.anjiantong.connect.WifiApAdmin;
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
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ReviewImplementActivity extends BaseActivity implements View.OnClickListener, View.OnTouchListener {
    @BindView(R.id.examine_page_back)
    LinearLayout examinePageBack;
    @BindView(R.id.title_layout)
    TextView titleLayout;
    @BindView(R.id.check_enterprise_01)
    TextView checkEnterprise01;
    @BindView(R.id.check_enterprise_02)
    TextView checkEnterprise02;
    @BindView(R.id.check_enterprise_03)
    TextView checkEnterprise03;
    @BindView(R.id.check_enterprise_04)
    TextView checkEnterprise04;
    @BindView(R.id.check_enterprise_05)
    TextView checkEnterprise05;
    @BindView(R.id.check_enterprise_07)
    TextView checkEnterprise07;
    @BindView(R.id.from_time_1)
    TextView fromTime1;
    @BindView(R.id.end_time_1)
    TextView endTime1;
    @BindView(R.id.check_enterprise_12)
    TextView checkEnterprise12;
    @BindView(R.id.l_check_up_date_02)
    LinearLayout lCheckUpDate02;
    @BindView(R.id.list_problem)
    ListView listProblem;
    @BindView(R.id.bt_check_up_date_04)
    Button btCheckUpDate04;
    @BindView(R.id.tx_list_table)
    LinearLayout txListTable;
    @BindView(R.id.bt_check_up_date_03)
    Button btCheckUpDate03;
    @BindView(R.id.bt_check_up_date_01)
    Button btCheckUpDate01;
    @BindView(R.id.delete_layout)
    LinearLayout deleteLayout;
    @BindView(R.id.bt_all)
    Button btAll;
    @BindView(R.id.review_implement_linear)
    LinearLayout reviewImplementLinear;

    // 创建一个List集合，List集合的元素是Map
    private List<HashMap<String, Object>> listItems = new ArrayList<>();
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
    public static ReviewImplementActivity mActivity_implement;
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
    private String s4 = "";//传输的问题列表
    private PopupWindow popupWindow;//雷达界面
    private String state;//形态
    private String otherCompany = "";
    private String BusinessType = "";
    private ArrayList<String> listStr = null;//文书数据
    private ProblemListAdapter listAdapter;
    private String userId = "";
    private String TAG = ReviewImplementActivity.class.getSimpleName();

    private WifiManager wifiManager_01 = null;
    private WifiManager mWifiManager01 = null;
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
                    Toast.makeText(ReviewImplementActivity.this, "getConnected的集合为空", Toast.LENGTH_LONG).show();
                case 1:
                    break;
                case 2:
                    UdpReceiveOut = false;
                    if (!udpout) {
                        if (wifiAdmin.setWifiApEnabled(wifiManager,true,WifiConfiguration.KeyMgmt.WPA_PSK)) {
                            //热点开启成功
//                            Toast.makeText(getApplicationContext(), "WIFI热点开启成功,热点名称为:" + Constant.HOST_SPOT_SSID + "\n" + "密码为：" + Constant.HOST_SPOT_PASS_WORD, Toast.LENGTH_LONG).show();
                            //这里可以设置为当用户连接到自己开的热点后，就跳转到文件发送界面，并将连接到自己热点设备的IP传过去

                            startNew startNew = new startNew();
                            startNew.start();
                        } else {
                            //热点开启失败
                            WifiPermissions.isHasPermissions(getApplicationContext());
                            Toast.makeText(getApplicationContext(), "WIFI热点开启失败", Toast.LENGTH_LONG).show();
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
                        Toast.makeText(ReviewImplementActivity.this, "正在热点模式下搜索设备", Toast.LENGTH_LONG).show();
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
                    WifiUtil.getIns().UpdateWifiList(ReviewImplementActivity.this,wifiAdmin,arraylist,false,"192.168.43.1",state,s4);
                    break;
                case 10:
                    update_wifi_flag = false;
                    break;
                case 11:
                    createConnect();
                    break;
                case 12:
                    seekAndJoin();
                    break;
                default:
                    tcpout = true;

                    if (CommonUtils.isIp((String) (msg.obj))) {
                        Toast.makeText(ReviewImplementActivity.this, "这是一个IP，地址为：" + (msg.obj), Toast.LENGTH_LONG).show();

                        IP_DuiFangde = (String) (msg.obj);
                        //跳转到文件发送界面

//                        Log.i("TAG", "2222222222222222222222222222222");
                        Intent intent_filetrans = new Intent(ReviewImplementActivity.this, Files_Trans_Activity_01.class);
                        intent_filetrans.putExtra("isCreateHot", "0");//0代表客户端
                        intent_filetrans.putExtra("isShowListView", false);
                        intent_filetrans.putExtra("state", state);
                        intent_filetrans.putExtra("id", s4);
                        startActivity(intent_filetrans);

                    } else {
                        Toast.makeText(ReviewImplementActivity.this, "这不是一个IP，内容为：" + msg.obj, Toast.LENGTH_LONG).show();
                    }
//                    popView.f
                    break;
            }
        }

    };
    private View popView;

    @Override
    protected void initView() {
        setContentView(R.layout.review_implement_layout);
        ButterKnife.bind(this);
        mActivity_implement = this;
        titleLayout.setText("执行计划");
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        Linear_root = (LinearLayout) findViewById(R.id.review_implement_linear);
        userId = SharedPreferencesUtil.getStringData(this, "userId", "");
        wifiManager = (WifiManager) super.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        //初始化wifiAdmin
        wifiAdmin = new WifiAdmin(this);
        ip = address;
        deleteLayout.setVisibility(View.VISIBLE);

    }

    @Override
    protected void init() {
        examinePageBack.setOnClickListener(this);
        examinePageBack.setOnTouchListener(this);
        checkEnterprise12.setOnClickListener(this);
        checkEnterprise12.setOnTouchListener(this);
        btCheckUpDate03.setOnClickListener(this);
        btCheckUpDate04.setOnClickListener(this);
        btCheckUpDate01.setOnClickListener(this);
        deleteLayout.setOnClickListener(this);
        btAll.setOnClickListener(this);
        openGroup();
        openDate();
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.examine_page_back://返回键
                onBackPressed();
                break;
            case R.id.check_enterprise_12://查看人员分组情况
                intent.setClass(this, ReviewPeopleGroupCheckActivity.class);
                intent.putExtra("ReviewId", getIntent().getStringExtra("ReviewId"));
                startActivity(intent);
                break;
            case R.id.bt_check_up_date_03://汇总问题
                state = "wt";
                createConnect();
                break;
            case R.id.bt_check_up_date_04://上传问题
                s4 = "";
                state = "wt";
                listStr = new ArrayList<>();
                for (int i = 0; i < listItems.size(); i++) {
                    if (ProblemListAdapter.isSelected.get(i)) {
                        listStr.add(listItems.get(i).get("hiddendangerid").toString());
                        if (listStr.size() == 1) {
                            s4 = listItems.get(i).get("hiddendangerid").toString();
                        } else {
                            s4 = s4 + "," + listItems.get(i).get("hiddendangerid").toString();
                        }
                    }
                }
                if (s4.length() > 0) {
//                    seekAndJoin();
//                    dialog01();
//                    LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    if (Build.VERSION.SDK_INT >= 23 && !GPSUtil.getIns().isOpen(ReviewImplementActivity.this)) {
////                        Toast.makeText(this, "正在打开GPS定位，请稍后", Toast.LENGTH_SHORT).show();
//                        Settings.Secure.putInt(getApplicationContext().getContentResolver(), Settings.Secure.LOCATION_MODE, android.provider.Settings.Secure.LOCATION_MODE_BATTERY_SAVING);
//
////                        Toast.makeText(this, "已打开GPS定位，请再度点击", Toast.LENGTH_SHORT).show();
//                        wifiManager = (WifiManager) super.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
//                        //初始化wifiAdmin
//                        wifiAdmin = new WifiAdmin(this);
//                        ip = address;
//                        dialog01();
                        Toast.makeText(this, "请手动打开GPS定位，否则会影响使用", Toast.LENGTH_SHORT).show();

                    } else {
                        wifiManager = (WifiManager) super.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                        //初始化wifiAdmin
                        wifiAdmin = new WifiAdmin(this);
                        ip = address;
                        dialog01();
                    }
                } else {
                    Toast.makeText(ReviewImplementActivity.this, "请选择需要上传的问题", Toast.LENGTH_SHORT).show();
                }

//                Log.d("问题上传数据", listStr.toString());
                break;

            case R.id.bt_check_up_date_01://制定文书
                String s = "";
                String s1 = "";
                String s2 = "";
                String s3 = "";
                Boolean aBoolean = true;//判断是否有未执行复查的问题
                listStr = new ArrayList<>();
                for (int i = 0; i < listItems.size(); i++) {
                    if (ProblemListAdapter.isSelected.get(i)) {
                        listStr.add(listItems.get(i).get("hiddendangerid").toString());
                        if (listStr.size() == 1) {
                            s = listItems.get(i).get("hiddendangerid").toString();
                            s1 = listStr.size() + ". " + listItems.get(i).get("yhms").toString() +
                                    "(" + listItems.get(i).get("disposeresult").toString() + ")";
                            s2 = "1";
                            s3 = listItems.get(i).get("zgqx").toString();

                        } else {
                            s = s + "," + listItems.get(i).get("hiddendangerid").toString();
                            s1 = s1 + "\n" + (i + 1) + ". " + listItems.get(i).get("yhms").toString() +
                                    "(" + listItems.get(i).get("disposeresult").toString() + ")";
                            if (s2.length() > 0) {
                                s2 = s2 + "、" + listStr.size();
                                s3 = listItems.get(i).get("zgqx").toString();
                            } else if (s2.length() == 0) {
                                s2 = listStr.size() + "";
                                s3 = listItems.get(i).get("zgqx").toString();
                            }
                        }
                        if( listItems.get(i).get("realname").toString().equals("暂无")){
                            aBoolean = false;
                        }
                    }
                }
//                Log.d("数据BusinessType", BusinessType);
//                Log.d("文书数据", listStr.toString() + BusinessType + ";?");
                if(s.length()>0&&aBoolean){
                    intent.setClass(this, PrintOut07Activity.class);
                    intent.putExtra("ReviewId", getIntent().getStringExtra("ReviewId"));
                    intent.putExtra("HiddenDangerId", s);
                    intent.putExtra("Problems", s1);
                    intent.putExtra("rectify", s2);
                    intent.putExtra("zgqx", s3);
                    intent.putExtra("BusinessType", BusinessType);

                    startActivity(intent);
                }else {
                    Toast.makeText(ReviewImplementActivity.this, "请正确选择需要上传的问题", Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.delete_layout://删除
                dialog(getIntent().getStringExtra("ReviewId"));
                break;
            case R.id.bt_all://全选
                for (int i = 0; i < listItems.size(); i++) {
                    ProblemListAdapter.isSelected.put(i, true);
                    listAdapter.notifyDataSetChanged();
                }
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


    /**
     * 打开复查计划表数据
     */
    protected void openDate() {
        // 初始化，只需要调用一次
        AssetsDatabaseManager.initManager(getApplication());
        // 获取管理对象，因为数据库需要通过管理对象才能够获取
        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
        // 通过管理对象获取数据库
        SQLiteDatabase db = mg.getDatabase("users.db");
//        try {
        String s_c = "SELECT* FROM ELL_ReviewInfo WHERE ReviewId = ?";
        Cursor cursor = db.rawQuery(s_c,
                new String[]{getIntent().getStringExtra("ReviewId")});
        cursor.moveToFirst();

        fromTime1.setText(cursor.getString(cursor.getColumnIndex("StartTime")));
        endTime1.setText(cursor.getString(cursor.getColumnIndex("EndTime")));
        checkEnterprise07.setText(cursor.getString(cursor.getColumnIndex("Address")));
        s_c = "SELECT* FROM ELL_Business WHERE BusinessId = ?";
        Cursor c = db.rawQuery(s_c,
                new String[]{cursor.getString(cursor.getColumnIndex("BusinessId"))});
        c.moveToFirst();
        checkEnterprise01.setText(c.getString(c.getColumnIndex("BusinessName")));
        checkEnterprise02.setText(c.getString(c.getColumnIndex("Address")));
        checkEnterprise03.setText(c.getString(c.getColumnIndex("LegalPerson")));
        checkEnterprise04.setText(c.getString(c.getColumnIndex("LegalPersonPost")));
        checkEnterprise05.setText(c.getString(c.getColumnIndex("LegalPersonPhone")));


        c.close();
        cursor.close();
        openHiddenDate();
//        } catch (Exception e) {
//            Log.e("数据库数据报错", e.toString());
//        }

    }

    /**
     * 打开组员
     */
    protected void openGroup() {
        // 初始化，只需要调用一次
        AssetsDatabaseManager.initManager(getApplication());
        // 获取管理对象，因为数据库需要通过管理对象才能够获取
        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
        // 通过管理对象获取数据库
        SQLiteDatabase db = mg.getDatabase("users.db");
        String s_c = "SELECT* FROM ELL_ReviewGroupInfo WHERE ReviewId = ?";
        Cursor cursor1 = db.rawQuery(s_c,
                new String[]{getIntent().getStringExtra("ReviewId")});
        String s2 = "--点击选择--";
        int i = 0;
        while (cursor1.moveToNext()) {
            try {
                s_c = "SELECT* FROM Base_User WHERE UserId = ?";
                Cursor cursor2 = db.rawQuery(s_c,
                        new String[]{cursor1.getString(cursor1.getColumnIndex("Headman"))});
                cursor2.moveToFirst();
//                if (i == 0) {
                s2 = cursor2.getString(cursor2.getColumnIndex("RealName")) + "组";
//                } else {
//                    s2 = s2 + "," + cursor2.getString(cursor2.getColumnIndex("RealName")) + "组";
//                }
                i++;
                cursor2.close();
            } catch (Exception e) {
                Log.e("人员用户数据库报错", e.toString());
            }
        }
        checkEnterprise12.setText(s2);
        cursor1.close();
    }

    /**
     * 打开匹配的隐患表
     */
    protected void openHiddenDate() {
        // 初始化，只需要调用一次
        AssetsDatabaseManager.initManager(getApplication());
        // 获取管理对象，因为数据库需要通过管理对象才能够获取
        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
        // 通过管理对象获取数据库
        SQLiteDatabase db = mg.getDatabase("users.db");
        try {
            String s_c = "SELECT* FROM ELL_HiddenDanger WHERE ReviewId = ?";
            Cursor cursor = db.rawQuery(s_c,
                    new String[]{getIntent().getStringExtra("ReviewId")});
            listItems = new ArrayList<>();
            HashMap<String, Object> listItem;
            int i = 0;
            while (cursor.moveToNext()) {
                listItem = new HashMap<>();
                try {
                    listItem.put("hiddendangerid", cursor.getString(cursor.getColumnIndex("HiddenDangerId")));
                    listItem.put("yhdd", cursor.getString(cursor.getColumnIndex("yhdd")));
                    listItem.put("yhms", cursor.getString(cursor.getColumnIndex("yhms")));
                    listItem.put("zgqx", cursor.getString(cursor.getColumnIndex("zgqx")).substring(0, 10));
                    listItem.put("zglx", cursor.getString(cursor.getColumnIndex("zglx")));
                    listItem.put("checkresult", cursor.getString(cursor.getColumnIndex("checkresult")));
                    listItem.put("disposeresult", cursor.getString(cursor.getColumnIndex("disposeresult")));
                    Log.e("TAG", "复查隐患  :" + cursor.getString(cursor.getColumnIndex("disposeresult")));
                    //查找检查人名称
//主要检查人
                    String[] strings = cursor.getString(cursor.getColumnIndex("AddUsers")).split(",");
                    String s1 = "";
                    for (String string : strings) {
                        s_c = "SELECT*FROM Base_User WHERE UserId = ?";
                        Cursor cursorAddUsers = db.rawQuery(s_c,
                                new String[]{string});
                        if (null != cursorAddUsers) {
                            cursorAddUsers.moveToNext();
                        }
                        String s2 = "";
                        try {
                            s2 = cursorAddUsers.getString(cursorAddUsers.getColumnIndex("RealName"));
                        } catch (Exception e) {
                            Log.e("问题发现人数据表报错", e.toString());
                        }
                        if (s1.length() > 0) {
                            s1 = s1 + "," + s2;
                        } else {
                            s1 = s2;
                        }
                        Log.e(TAG, "s1为： " + s1);

                        cursorAddUsers.close();
                    }
                    if(s1.length()>1){

                        listItem.put("realname", s1);
                    }else {
                        listItem.put("realname", "暂无");
                    }
                    listItem.put("zglx1", "1");

                } catch (Exception e) {
                    Log.e(TAG, "查找检查人报错" + e.toString());
                }
                i++;
//全部检查人
        /*        Cursor cursor1 = db.rawQuery("SELECT* FROM ELL_ReviewGroupInfo WHERE ReviewId = ?",
                        new String[]{getIntent().getStringExtra("ReviewId")});
                while (cursor1.moveToNext()) {
                    Cursor cursor2 = db.rawQuery("SELECT* FROM Base_User WHERE UserId = ?",
                            new String[]{cursor1.getString(cursor1.getColumnIndex("Headman"))});
                    cursor2.moveToFirst();
                    Cursor cursor0 = db.rawQuery("SELECT* FROM ELL_ReviewMember WHERE GroupId = ?",
                            new String[]{cursor1.getString(cursor1.getColumnIndex("GroupId"))});
                    int i = 0;
                    String s2 = "";
                    while (cursor0.moveToNext()) {
                        Cursor cursor3 = db.rawQuery("SELECT* FROM Base_User WHERE UserId = ?",
                                new String[]{cursor0.getString(cursor0.getColumnIndex("Member"))});
                        cursor3.moveToFirst();
                        if (i == 0) {
                            s2 = cursor3.getString(cursor3.getColumnIndex("RealName"));
                        } else {
                            s2 = s2 + "," + cursor3.getString(cursor3.getColumnIndex("RealName"));
                        }
                        i++;
                        cursor3.close();
                    }
                    listItem.put("realname", cursor2.getString(cursor2.getColumnIndex("RealName"))+","+s2);
                    cursor0.close();
                    cursor2.close();
                }*/


                listItems.add(listItem);
//                cursor1.close();

            }
            cursor.close();
            listAdapter = new ProblemListAdapter(ReviewImplementActivity.this, listItems, R.layout.review_implement_hidden_list_item,
                    new String[]{"yhms", "checkresult", "disposeresult", "zgqx", "yhdd", "realname", "zglx1"},
                    new int[]{R.id.tx_problem_01, R.id.tx_problem_02, R.id.tx_problem_03,
                            R.id.tx_problem_04, R.id.tx_problem_05, R.id.tx_problem_06, R.id.tx_problem_07});
            listProblem.setAdapter(listAdapter);
            Utility.setListViewHeightBasedOnChildren(listProblem);
            listProblem.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent();
                    intent.putExtra("hiddendangerid", listItems.get(position).get("hiddendangerid").toString());
                    intent.putExtra("BusinessName", checkEnterprise01.getText().toString());
                    intent.setClass(ReviewImplementActivity.this, ReviewHiddenImplementActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                }
            });
        } catch (Exception e) {
            Log.e("数据库数据报错", e.toString());
        }
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
    /**
     * 监听有没有wifi接入到wifi热点的线程
     */
    public class startNew extends Thread {
        public void run() {
            Intent intent_filetrans2 = new Intent(ReviewImplementActivity.this, Files_Trans_Activity_01.class);
            intent_filetrans2.putExtra("isCreateHot", "1");//代表服务器显示的界面
            intent_filetrans2.putExtra("isShowListView", true);
            intent_filetrans2.putExtra("state", state);
            intent_filetrans2.putExtra("id", s4);
            startActivity(intent_filetrans2);
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

    /*点击创建连接，两台设备建立连接*/
    private void createConnect() {
        ConnectivityManager mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
        //判断网络连接状态
        if (mNetworkInfo != null) {
            Toast.makeText(this, "请先断开网络，再次点击“创建连接”重试", Toast.LENGTH_LONG).show();
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
     * 弹出确认对话框
     */
    protected void dialogGroup(final String s) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("分发计划");
        builder.setMessage("计划已保存，是否进行分发 ？");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                s4 = s;
                createConnect();
//                finish();
//                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
            }
        });
        builder.show();
    }

    /**
     * 弹出确认对话框
     */
    protected void dialog(final String s) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("确认删除");
        builder.setMessage("是否删除复查计划 ？");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                deleterPlaneDate(s);
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Toast.makeText(ReviewImplementActivity.this, "已取消", Toast.LENGTH_SHORT).show();
            }
        });
        builder.show();
    }

    /**
     * 删除数据
     */
    protected void deleterPlaneDate(String s) {
        // 初始化，只需要调用一次
        AssetsDatabaseManager.initManager(getApplication());
        // 获取管理对象，因为数据库需要通过管理对象才能够获取
        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
        // 通过管理对象获取数据库
        SQLiteDatabase db = mg.getDatabase("users.db");
        try {
            db.delete("ELL_ReviewInfo", "ReviewId = ?", new String[]{s});
            Toast.makeText(this, "已删除", Toast.LENGTH_SHORT).show();
            finish();
        } catch (Exception e) {
            Log.e("删除复查计划数据库报错", e.toString());
        }
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
        if (udpSocket != null) {
            udpSocket.close();
        }
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
     * 弹出确认汇总问题对话框
     */
    protected void dialog01() {
        final ConnectivityManager mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
        mWifiManager01 = (WifiManager) super.getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("汇总问题");
        builder.setMessage("请选择将问题汇总到的地方。");
        builder.setPositiveButton("组长平板", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                WifiInfo wifiInfo = mWifiManager01.getConnectionInfo();
                int ipAddress = wifiInfo == null ? 0 : wifiInfo.getIpAddress();
                if (mWifiManager01.isWifiEnabled() && ipAddress != 0) {
                    dialogWifi_01(1);
                } else {
                    seekAndJoin();
                }
                dialog.dismiss();
//                seekAndJoin();
            }
        });
//        builder.setNegativeButton("网络端", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//                httpDate();
//            }
//        });
        builder.show();
    }

    /**
     * 弹出确认对话框
     */
    protected void dialogWifi_01(final int type) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("请先关闭WiFi");
        builder.setMessage("是否关闭WiFi ？");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mWifiManager01.setWifiEnabled(false);
                Timer timer = new Timer();
                TimerTask timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        handler2.sendEmptyMessage(12);
                    }
                };
                //开始一个定时任务
                timer.schedule(timerTask, 500);
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

    /**
     * 退出页面关闭GPS
     * */
    @Override
    public void finish() {
        if(Build.VERSION.SDK_INT >= 23 &&GPSUtil.getIns().isOpen(ReviewImplementActivity.this)){

            Settings.Secure.putInt(getApplicationContext().getContentResolver(), Settings.Secure.LOCATION_MODE, android.provider.Settings.Secure.LOCATION_MODE_OFF);
        }
        super.finish();
    }
    /**
     * 从web端获取数据
     */
    protected void httpDate() {

    }
    //type 0新建wifi列表
    //type 1动态更新wifi列表
    /*void UpdateWifiList() {
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

                            Intent intent_filetrans = new Intent(ReviewImplementActivity.this, Files_Trans_Activity_01.class);
                            intent_filetrans.putExtra("isCreateHot", "0");//0代表客户端
                            intent_filetrans.putExtra("isShowListView", false);
                            intent_filetrans.putExtra("state", state);
                            intent_filetrans.putExtra("id", s4);
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
