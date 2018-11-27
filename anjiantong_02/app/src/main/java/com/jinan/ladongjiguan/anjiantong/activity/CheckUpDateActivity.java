package com.jinan.ladongjiguan.anjiantong.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
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
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.jinan.ladongjiguan.anjiantong.PublicClass.AssetsDatabaseManager;
import com.jinan.ladongjiguan.anjiantong.PublicClass.CustomProgressDialog;
import com.jinan.ladongjiguan.anjiantong.PublicClass.DoubleDatePickerDialog;
import com.jinan.ladongjiguan.anjiantong.utils.GPSUtil;
import com.jinan.ladongjiguan.anjiantong.PublicClass.StringOrDate;
import com.jinan.ladongjiguan.anjiantong.PublicClass.Utility;
import com.jinan.ladongjiguan.anjiantong.PublicClass.WifiPermissions;
import com.jinan.ladongjiguan.anjiantong.utils.WifiUtil;
import com.jinan.ladongjiguan.anjiantong.R;
import com.jinan.ladongjiguan.anjiantong.adapter.ProblemListAdapter;
import com.jinan.ladongjiguan.anjiantong.connect.WifiAdmin;
import com.jinan.ladongjiguan.anjiantong.connect.WifiApAdmin;
import com.jinan.ladongjiguan.anjiantong.utils.CommonUtils;
import com.jinan.ladongjiguan.anjiantong.utils.FTPUtils;
import com.jinan.ladongjiguan.anjiantong.utils.SharedPreferencesUtil;
import com.jinan.ladongjiguan.anjiantong.utils.WebServiceUtils;

import org.json.JSONObject;
import org.json.XML;
import org.ksoap2.serialization.SoapObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 别问我怎么做到的打开修改界面信息无误的，我忘了
 * 也别指望优化这段逻辑代码，会屎……
 */
public class CheckUpDateActivity extends BaseActivity implements View.OnClickListener, View.OnTouchListener {


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
    @BindView(R.id.check_enterprise_06)
    EditText checkEnterprise06;
    @BindView(R.id.from_time)
    TextView fromTime;
    @BindView(R.id.end_time)
    TextView endTime;
    @BindView(R.id.l_search_time)
    LinearLayout lSearchTime;
    @BindView(R.id.e_car_num_7)
    Spinner eCarNum7;
    @BindView(R.id.check_enterprise_10)
    Spinner checkEnterprise10;
    @BindView(R.id.check_enterprise_11)
    TextView checkEnterprise11;
    @BindView(R.id.bt_check_up_date_01)
    Button btCheckUpDate01;
    @BindView(R.id.bt_check_up_date_02)
    Button btCheckUpDate02;
    @BindView(R.id.bt_check_up_date_05)
    Button btCheckUpDate05;
    @BindView(R.id.bt_check_up_date_06)
    Button btCheckUpDate06;
    @BindView(R.id.l_bt_02)
    LinearLayout lBt02;
    @BindView(R.id.l_bt_01)
    LinearLayout lBt01;
    @BindView(R.id.list_problem)
    ListView listProblem;
    @BindView(R.id.l_check_up_date_01)
    LinearLayout lCheckUpDate01;
    @BindView(R.id.check_enterprise_07)
    TextView checkEnterprise07;
    @BindView(R.id.from_time_1)
    TextView fromTime1;
    @BindView(R.id.end_time_1)
    TextView endTime1;
    @BindView(R.id.check_enterprise_08)
    TextView checkEnterprise08;
    @BindView(R.id.check_enterprise_9)
    TextView checkEnterprise9;
    @BindView(R.id.check_enterprise_12)
    TextView checkEnterprise12;
    @BindView(R.id.l_check_up_date_02)
    LinearLayout lCheckUpDate02;
    @BindView(R.id.delete_layout)
    LinearLayout deleteLayout;
    @BindView(R.id.tx_list_table)
    LinearLayout txTable;
    @BindView(R.id.bt_check_up_date_03)
    Button btCheckUpDate03;
    @BindView(R.id.bt_check_up_date_04)
    Button btCheckUpDate04;
    @BindView(R.id.check_enterprise_06_01)
    EditText checkEnterprise0601;
    @BindView(R.id.check_enterprise_07_01)
    TextView checkEnterprise0701;
    @BindView(R.id.check_box_02)
    CheckBox checkBox02;
    @BindView(R.id.et_check_enterprise_10)
    EditText etCheckEnterprise10;
    @BindView(R.id.check_enterprise_13)
    Spinner checkEnterprise13;
    @BindView(R.id.bt_check_up_date_07)
    Button btCheckUpDate07;
    @BindView(R.id.Linear_layout_2)
    LinearLayout LinearLayout2;
    @BindView(R.id.del_text)
    TextView delText;
    @BindView(R.id.et_check_enterprise_0)
    EditText etCheckEnterprise0;
//    @BindView(R.id.button1)
//    Button mBtn1;
//    @BindView(R.id.button2)
//    Button mBtn2;
//    @BindView(R.id.button3)
//    Button mBtn3;

    private String s_from_time = "";//开始时间
    private Date d_from_time;
    private String s_end_time = "";//结束时间
    private Date d_end_time;
    private String BusinessId = "";//企业ID
    private String CheckType;//检查类型
    private String PlanId = UUID.randomUUID().toString();//计划主键
    private String HeadmanID = "";//组长id
    private String Headman = "";
    private String Address = "";//路线
    private String Members = "";//组员们
    private List<HashMap<String, Object>> listItems = new ArrayList<>();//问题表
    private ProblemListAdapter listAdapter;//问题表适配
    private ArrayList<String> listStr = null;//文书数据
    private String CompanyId;//单位编号
    private ArrayList<Map<String, Object>> listItems1;//单位列表
    private ArrayList<Map<String, Object>> listItems2;//单位列表2
    public static CheckUpDateActivity mActivity_date;
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
    //开启wifi ... ...
    private WifiManager wifiManager = null;
    private ImageView iv_scanning;
//    private LinearLayout Linear_root;

    Socket socket = null;
    static DatagramSocket udpSocket = null;
    static DatagramPacket udpPacket = null;
    private boolean udpout = false;
    private String s4 = "";//传输的问题列表
    private PopupWindow popupWindow;//雷达界面
    private String state;//形态
    private String otherCompany = "";
    private String BusinessType = "";
    private WifiManager wifiManager_01 = null;
    private WifiManager mWifiManager01 = null;
    private String WEB_SERVER_URL;
    private CustomProgressDialog progressDialog = null;//加载页
    private boolean flag;
    private FTPUtils ftpUtils = null;
    private int BusinessState = 0;
    private PopupWindow popupwindow;
    private ArrayList<String> dateList = new ArrayList<>();
    /**
     * 判断联合执法目前其他单位是否为其他的int
     */
    private int i_checkEnterprise13 = 0;
    private String TAG = CheckUpDateActivity.class.getSimpleName();
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
                    Toast.makeText(CheckUpDateActivity.this, "getConnected的集合为空", Toast.LENGTH_LONG).show();
                case 1:
                    break;
                case 2:
                    UdpReceiveOut = false;
                    if (!udpout) {
                        if (wifiAdmin.setWifiApEnabled(wifiManager, true, 4)) {
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
                        Toast.makeText(CheckUpDateActivity.this, "正在热点模式下搜索设备", Toast.LENGTH_LONG).show();
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
                    WifiUtil.getIns().UpdateWifiList(CheckUpDateActivity.this,wifiAdmin,arraylist,false,"192.168.43.1",state,s4);
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
                        Toast.makeText(CheckUpDateActivity.this, "这是一个IP，地址为：" + (msg.obj), Toast.LENGTH_LONG).show();

                        IP_DuiFangde = (String) (msg.obj);
                        //跳转到文件发送界面
                        Intent intent_filetrans = new Intent(CheckUpDateActivity.this, Files_Trans_Activity.class);
                        intent_filetrans.putExtra("isCreateHot", "0");//0代表客户端
                        intent_filetrans.putExtra("isShowListView", false);
                        intent_filetrans.putExtra("state", state);
                        intent_filetrans.putExtra("id", s4);
                        startActivity(intent_filetrans);

                    } else {
                        Toast.makeText(CheckUpDateActivity.this, "这不是一个IP，内容为：" + msg.obj, Toast.LENGTH_LONG).show();
                    }
                    break;
            }
        }

    };

    private View popView;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                        progressDialog = null;
                    }
                    Toast.makeText(CheckUpDateActivity.this, "上传成功", Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                        progressDialog = null;
                    }
                    Toast.makeText(CheckUpDateActivity.this, "上传失败", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;

            }
        }
    };

    @Override
    protected void initView() {
        setContentView(R.layout.check_up_date_layout);
        ButterKnife.bind(this);
        mActivity_date = this;
        examinePageBack.setOnClickListener(this);//返回键
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
//        Linear_root = (LinearLayout) findViewById(R.id.Linear_layout_2);
        wifiManager = (WifiManager) super.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        //初始化wifiAdmin
        wifiAdmin = new WifiAdmin(this);
        ip = address;
        etCheckEnterprise10.setVisibility(View.GONE);
        txTable.setVisibility(View.GONE);
        listProblem.setVisibility(View.GONE);
        WEB_SERVER_URL = SharedPreferencesUtil.getStringData(this, "IPString", "");
        // 初始化，只需要调用一次
        AssetsDatabaseManager.initManager(getApplication());
        // 获取管理对象，因为数据库需要通过管理对象才能够获取
        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
        // 通过管理对象获取数据库
        final SQLiteDatabase db = mg.getDatabase("users.db");
        Cursor c = db.rawQuery("SELECT* FROM Base_Company", null);
        // 创建一个List集合，List集合的元素是Map

        listItems1 = new ArrayList<>();
        listItems2 = new ArrayList<>();
        Map<String, Object> listItem;
        int i1 = 0;
        int Selected = 0;
        try {
            while (c.moveToNext()) {
                listItem = new HashMap<>();
                listItem.put("CompanyId", c.getString(c.getColumnIndex("CompanyId")));
                listItem.put("FullName", c.getString(c.getColumnIndex("FullName")));
                listItems1.add(listItem);
                listItems2.add(listItem);
//            Log.d("对比数据", c.getString(c.getColumnIndex("FullName")) + SharedPreferencesUtil.getStringData(this, "Company", null));
                if (c.getString(c.getColumnIndex("FullName")).equals(SharedPreferencesUtil.getStringData(this, "Company", null))) {
                    Selected = i1;
                }
                i1++;
            }
        } catch (Exception e) {
            listItem = new HashMap<>();
            listItem.put("CompanyId", "000001");
            listItem.put("FullName", "未知，请更新数据");
            listItems1.add(listItem);
            listItems2.add(listItem);
        }

        SimpleAdapter simpleAdapter = new SimpleAdapter(this, listItems1,//执法单位
                R.layout.login_spinner_item,
                new String[]{"FullName"},
                new int[]{R.id.text});
        Log.d(TAG + "-initView", "listItems1数据：" + listItems1.toString());
        checkEnterprise10.setAdapter(simpleAdapter);
        checkEnterprise10.setSelection(Selected);
        checkEnterprise10.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                CompanyId = listItems1.get(position).get("CompanyId").toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        listItem = new HashMap<>();
        listItem.put("CompanyId", "000000");
        listItem.put("FullName", "其他");
        listItems2.add(listItem);
        Log.d("数据", listItems2.toString());
        c.close();
        //绑定适配器和值
        simpleAdapter = new SimpleAdapter(this, listItems2,//联合执法
                R.layout.login_spinner_item,
                new String[]{"FullName"},
                new int[]{R.id.text});
        checkEnterprise13.setAdapter(simpleAdapter);
/**
 * 下拉列表
 * */
        Resources res = getResources();
        final String[] strings = res.getStringArray(R.array.s_car_num_7);
        List<HashMap<String, Object>> list = new ArrayList<>();
        for (String string : strings) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("company", string);
            list.add(map);
        }
        SimpleAdapter simpleAdapter1 = new SimpleAdapter(this, list,//检查类型
                R.layout.login_spinner_item,
                new String[]{"company"},
                new int[]{R.id.text});
        eCarNum7.setAdapter(simpleAdapter1);
        eCarNum7.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position != 8 && position != 9) {
                    CheckType = strings[position];
                    checkEnterprise13.setVisibility(View.INVISIBLE);
                    etCheckEnterprise10.setVisibility(View.GONE);
                    etCheckEnterprise10.setText("");
                    etCheckEnterprise0.setVisibility(View.GONE);
                    etCheckEnterprise0.setText("");
                    otherCompany = "";
                } else if (position == 8) {
                    etCheckEnterprise0.setVisibility(View.GONE);
                    etCheckEnterprise0.setText("");
                    CheckType = strings[position];
                    checkEnterprise13.setVisibility(View.VISIBLE);
//                    checkEnterprise13.setSelection(0);
                    checkEnterprise13.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            if (listItems2.get(position).get("FullName").toString().equals("其他")) {
                                etCheckEnterprise10.setVisibility(View.VISIBLE);
                                i_checkEnterprise13 = position;
                                etCheckEnterprise10.setText("");
                            } else {
                                etCheckEnterprise10.setVisibility(View.GONE);
//                                etCheckEnterprise10.setText(listItems2.get(position).get("FullName").toString());
                                otherCompany = listItems2.get(position).get("FullName").toString();
                            }

                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                    checkEnterprise13.setSelection(0);
                } else if (position == 9) {
                    CheckType = strings[position];
                    checkEnterprise13.setVisibility(View.INVISIBLE);
                    etCheckEnterprise10.setVisibility(View.GONE);
                    etCheckEnterprise10.setText("");
                    otherCompany = "";
                    etCheckEnterprise0.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        new Thread() {
            @Override
            public void run() {
                ftpUtils = FTPUtils.getInstance();
                flag = ftpUtils.initFTPSetting(MainActivity.IP, 21, "ftpuser", "ld123456");
            }
        }.start();
    }

    @Override
    protected void init() {

        btCheckUpDate02.setOnClickListener(this);
        btCheckUpDate05.setOnClickListener(this);
        btCheckUpDate06.setOnClickListener(this);
        btCheckUpDate01.setOnClickListener(this);
        checkEnterprise12.setOnClickListener(this);
        checkEnterprise12.setOnTouchListener(this);
        btCheckUpDate03.setOnClickListener(this);
        btCheckUpDate04.setOnClickListener(this);
        btCheckUpDate07.setOnClickListener(this);
        String s;
        switch (getIntent().getStringExtra("name")) {
            case "5":
                titleLayout.setText("制定计划");
                /**
                 * 点击事件
                 * */
                lSearchTime.setOnClickListener(this);
                checkEnterprise01.setOnClickListener(this);
                checkEnterprise11.setOnClickListener(this);
                /**
                 * 触摸事件
                 * */
                examinePageBack.setOnTouchListener(this);
                checkEnterprise01.setOnTouchListener(this);
                checkEnterprise11.setOnTouchListener(this);
                /**
                 * 隐藏
                 * */
                lBt01.setVisibility(View.GONE);
                lCheckUpDate02.setVisibility(View.GONE);
                btCheckUpDate01.setVisibility(View.GONE);
                btCheckUpDate03.setVisibility(View.GONE);
                break;
            case "2":
                titleLayout.setText("查看计划");
                s = "企业" + getIntent().getStringExtra("enterprise");
                checkEnterprise01.setText(s);
                openPlanDate(getIntent().getStringExtra("id"));
                lBt02.setVisibility(View.GONE);
                lCheckUpDate01.setVisibility(View.GONE);
                problemList(getIntent().getStringExtra("id"));
                /**
                 * 计划ID
                 * */
                PlanId = getIntent().getStringExtra("id");
                deleteLayout.setVisibility(View.VISIBLE);
                deleteLayout.setOnClickListener(this);
                delText.setText("菜单");
                break;
            case "3":
                titleLayout.setText("查看计划");
                s = "企业" + getIntent().getStringExtra("enterprise");
                checkEnterprise01.setText(s);
                s = "小组" + getIntent().getStringExtra("enterprise");
                checkEnterprise11.setText(s);
                lBt02.setVisibility(View.GONE);
                lCheckUpDate01.setVisibility(View.GONE);
                /**
                 * 计划ID
                 * */
                PlanId = getIntent().getStringExtra("id");
                deleteLayout.setVisibility(View.VISIBLE);
                deleteLayout.setOnClickListener(this);
                delText.setText("菜单");
                break;
            case "4":
                titleLayout.setText("查看计划");
                s = "企业" + getIntent().getStringExtra("enterprise");
                checkEnterprise01.setText(s);
                s = "小组" + getIntent().getStringExtra("enterprise");
                checkEnterprise11.setText(s);
                lBt02.setVisibility(View.GONE);
                lCheckUpDate01.setVisibility(View.GONE);
                /**
                 * 计划ID
                 * */
                PlanId = getIntent().getStringExtra("id");
                deleteLayout.setVisibility(View.VISIBLE);
                deleteLayout.setOnClickListener(this);
                delText.setText("菜单");
                break;
            case "1":
                titleLayout.setText("修改计划");
                /**
                 * 点击事件
                 * */
                lSearchTime.setOnClickListener(this);
                checkEnterprise01.setOnClickListener(this);
                checkEnterprise11.setOnClickListener(this);
                deleteLayout.setVisibility(View.VISIBLE);
                deleteLayout.setOnClickListener(this);
                /**
                 * 触摸事件
                 * */
                examinePageBack.setOnTouchListener(this);
                checkEnterprise01.setOnTouchListener(this);
                checkEnterprise11.setOnTouchListener(this);
                deleteLayout.setOnTouchListener(this);
                /**
                 * 隐藏
                 * */
                lBt01.setVisibility(View.GONE);
                lCheckUpDate02.setVisibility(View.GONE);
                /**
                 * 计划ID
                 * */
                PlanId = getIntent().getStringExtra("id");
                openPlanDate(getIntent().getStringExtra("id"));
                btCheckUpDate01.setVisibility(View.GONE);
                btCheckUpDate03.setVisibility(View.GONE);
                break;
            case "6":
                titleLayout.setText("制定计划");
                /**
                 * 点击事件
                 * */
                lSearchTime.setOnClickListener(this);
                checkEnterprise01.setOnClickListener(this);
                checkEnterprise11.setOnClickListener(this);
                /**
                 * 触摸事件
                 * */
                examinePageBack.setOnTouchListener(this);
                checkEnterprise01.setOnTouchListener(this);
                checkEnterprise11.setOnTouchListener(this);
                /**
                 * 隐藏
                 * */
                lBt01.setVisibility(View.GONE);
                lCheckUpDate02.setVisibility(View.GONE);
                btCheckUpDate01.setVisibility(View.GONE);
                btCheckUpDate03.setVisibility(View.GONE);
                checkEnterprise01.setText(getIntent().getStringExtra("BusinessName"));
                date(getIntent().getStringExtra("BusinessId"));
                BusinessId = getIntent().getStringExtra("BusinessId");

                break;
            case "7":
                titleLayout.setText("制定职业卫生计划");
                /**
                 * 点击事件
                 * */
                lSearchTime.setOnClickListener(this);
                checkEnterprise01.setOnClickListener(this);
                checkEnterprise11.setOnClickListener(this);
                /**
                 * 触摸事件
                 * */
                examinePageBack.setOnTouchListener(this);
                checkEnterprise01.setOnTouchListener(this);
                checkEnterprise11.setOnTouchListener(this);
                /**
                 * 隐藏
                 * */
                lBt01.setVisibility(View.GONE);
                lCheckUpDate02.setVisibility(View.GONE);
                btCheckUpDate01.setVisibility(View.GONE);
                btCheckUpDate03.setVisibility(View.GONE);
                eCarNum7.setSelection(2);
                break;
            case "8":
                titleLayout.setText("制定计划");
                /**
                 * 点击事件
                 * */
                lSearchTime.setOnClickListener(this);
                checkEnterprise01.setOnClickListener(this);
                checkEnterprise11.setOnClickListener(this);
                /**
                 * 触摸事件
                 * */
                examinePageBack.setOnTouchListener(this);
                checkEnterprise01.setOnTouchListener(this);
                checkEnterprise11.setOnTouchListener(this);
                deleteLayout.setOnTouchListener(this);
                /**
                 * 隐藏
                 * */
                lBt01.setVisibility(View.GONE);
                lCheckUpDate02.setVisibility(View.GONE);
                /**
                 * 计划ID
                 * */
                PlanId = getIntent().getStringExtra("id");
                openPlanDate(getIntent().getStringExtra("id"));
                btCheckUpDate01.setVisibility(View.GONE);
                btCheckUpDate03.setVisibility(View.GONE);
                break;
            default:
                break;

        }


//        checkBox02.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked) {
//                    checkEnterprise13.setVisibility(View.VISIBLE);
//                    checkEnterprise13.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                        @Override
//                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                            if(listItems1.get(position).get("FullName").toString().equals("其他")){
//                                etCheckEnterprise10.setVisibility(View.VISIBLE);
//                                etCheckEnterprise10.setText("");
//                            }else {
//                                etCheckEnterprise10.setVisibility(View.GONE);
//                                etCheckEnterprise10.setText(listItems1.get(position).get("FullName").toString());
//                            }
//
//                        }
//
//                        @Override
//                        public void onNothingSelected(AdapterView<?> parent) {
//
//                        }
//                    });
//
//                } else {
//                    checkEnterprise13.setVisibility(View.INVISIBLE);
//                    etCheckEnterprise10.setVisibility(View.GONE);
//                    etCheckEnterprise10.setText("");
//                }
//            }
//        });

    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.l_search_time://时间选择器
                Calendar c = Calendar.getInstance();
                // 最后一个false表示不显示日期，如果要显示日期，最后参数可以是true或者不用输入
                new DoubleDatePickerDialog(CheckUpDateActivity.this, 0, new DoubleDatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker startDatePicker, int startYear, int startMonthOfYear,
                                          int startDayOfMonth, DatePicker endDatePicker, int endYear, int endMonthOfYear,
                                          int endDayOfMonth) {

                        if (startYear > endYear) {
                            Toast.makeText(CheckUpDateActivity.this, "开始日期不能晚于结束日期", Toast.LENGTH_LONG).show();
                            return;
                        } else if (startYear == endYear) {
                            if (startMonthOfYear > endMonthOfYear) {
                                Toast.makeText(CheckUpDateActivity.this, "开始日期不能晚于结束日期", Toast.LENGTH_LONG).show();
                                return;
                            } else if (startMonthOfYear == endMonthOfYear) {
                                if (startDayOfMonth > endDayOfMonth) {
                                    Toast.makeText(CheckUpDateActivity.this, "开始日期不能晚于结束日期", Toast.LENGTH_LONG).show();
                                    return;
                                } else {
                                    s_from_time = String.format("%d-%d-%d", startYear, startMonthOfYear + 1, startDayOfMonth);
                                    d_from_time = StringOrDate.stringToDate(s_from_time);
                                    fromTime.setText(s_from_time);
                                    s_end_time = String.format("%d-%d-%d", endYear, endMonthOfYear + 1, endDayOfMonth);
                                    d_end_time = StringOrDate.stringToDate(s_end_time);
                                    endTime.setText(s_end_time);
                                }
                            } else {
                                s_from_time = String.format("%d-%d-%d", startYear, startMonthOfYear + 1, startDayOfMonth);
                                d_from_time = StringOrDate.stringToDate(s_from_time);
                                fromTime.setText(s_from_time);
                                s_end_time = String.format("%d-%d-%d", endYear, endMonthOfYear + 1, endDayOfMonth);
                                d_end_time = StringOrDate.stringToDate(s_end_time);
                                endTime.setText(s_end_time);
                            }

                        } else {
                            s_from_time = String.format("%d-%d-%d", startYear, startMonthOfYear + 1, startDayOfMonth);
                            d_from_time = StringOrDate.stringToDate(s_from_time);
                            fromTime.setText(s_from_time);
                            s_end_time = String.format("%d-%d-%d", endYear, endMonthOfYear + 1, endDayOfMonth);
                            d_end_time = StringOrDate.stringToDate(s_end_time);
                            endTime.setText(s_end_time);
                        }

                    }
                }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE), true).show();
                break;
            case R.id.examine_page_back://返回键
                onBackPressed();
                break;
            case R.id.check_enterprise_01://选择企业
                intent.setClass(CheckUpDateActivity.this, EnterpriseInformationActivity.class);
                intent.putExtra("state", "check");
                startActivityForResult(intent, 0);
                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                break;
            case R.id.check_enterprise_11://选择执法人员
                intent.setClass(CheckUpDateActivity.this, PeopleGroupActivity.class);
                intent.putExtra("state", "3");
                intent.putExtra("id", PlanId);
                startActivityForResult(intent, 1);
                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                break;
            case R.id.check_enterprise_12://查看人员分组
                intent.setClass(CheckUpDateActivity.this, PeopleGroupActivity.class);
                intent.putExtra("state", "0");
                intent.putExtra("id", PlanId);
                startActivityForResult(intent, 1);
                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                break;
            case R.id.bt_check_up_date_01://制定文书
                String s = "";
                String s1 = "";
                String s2 = "";
                String s3 = "";
                String j = "";
                String j1 = "";
                String j2 = "";
                String j3 = "";
                String s6 = "";
                int num = 0;
                listStr = new ArrayList<>();
                for (int i = 0; i < listItems.size(); i++) {
                    Date curDate = new Date(System.currentTimeMillis());//获取当前时间
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
                    if (ProblemListAdapter.isSelected.get(i)) {
                        String[] strings = listItems.get(i).get("DayNum").toString().split("-");
                        listStr.add(listItems.get(i).get("CheckId").toString());
                        num++;
                        Date date1 = null;
                        int i1 = 0;
                        try {
                            date1 = sdf.parse(listItems.get(i).get("DayNum").toString());
                            i1 = StringOrDate.getGapCount(curDate, date1);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        s6 = listItems.get(0).get("YHDD").toString() +
                                listItems.get(0).get("YHBW").toString()
                                + listItems.get(0).get("Problem").toString();
                        if (listStr.size() == 1) {
                            s = listItems.get(i).get("CheckId").toString();
                            s1 = listStr.size() + ". " + listItems.get(i).get("YHDD").toString() +
                                    listItems.get(i).get("YHBW").toString()
                                    + listItems.get(i).get("Problem").toString() + "责令" +
                                    listItems.get(i).get("Dispose").toString() + "。";
                            j1 = "第" + listStr.size();
                            if (i1 > 0) {
                                s1 = listStr.size() + ". " + listItems.get(i).get("YHDD").toString() +
                                        listItems.get(i).get("YHBW").toString()
                                        + listItems.get(i).get("Problem").toString() + "责令" +
                                        strings[0] + "年" + strings[1] + "月" + strings[2] + "日"
                                        + "前整改完毕。";
                                s2 = "1";
                                s3 = listItems.get(i).get("DayNum").toString();
                                j1 = "";
                                j2 = "第" + listStr.size();
                                j3 = "责令" + strings[0] + "年" + strings[1] + "月" + strings[2] + "日";
                            }

                        } else {
                            s = s + "," + listItems.get(i).get("CheckId").toString();

//                            s1 = s1 + "\n" + (i + 1) + ". " + listItems.get(i).get("Problem").toString();
                            if (s2.length() > 0 && i1 > 0) {
                                s1 = s1 + "\n" + (i + 1) + ". " + listItems.get(i).get("YHDD").toString() +
                                        listItems.get(i).get("YHBW").toString()
                                        + listItems.get(i).get("Problem").toString() + "责令" +
                                        strings[0] + "年" + strings[1] + "月" + strings[2] + "日" + "前整改完毕。";
                                s2 = s2 + "、" + listStr.size();
                                s3 = s3 + "、" + listItems.get(i).get("DayNum").toString();
                                j2 = j2 + "、" + listStr.size();
                                j3 = j3 + "、" + strings[0] + "年" + strings[1] + "月" + strings[2] + "日";

                            } else if (s2.length() == 0 && i1 > 0) {
                                s1 = s1 + "\n" + (i + 1) + ". " + listItems.get(i).get("YHDD").toString() +
                                        listItems.get(i).get("YHBW").toString()
                                        + listItems.get(i).get("Problem").toString() + "责令" +
                                        strings[0] + "年" + strings[1] + "月" + strings[2] + "日" + "前整改完毕。";
                                s2 = listStr.size() + "";
                                s3 = listItems.get(i).get("DayNum").toString();
                                j2 = "第" + listStr.size();
                                j3 = "责令" + strings[0] + "年" + strings[1] + "月" + strings[2] + "日";

                            } else {
                                s1 = s1 + "\n" + (i + 1) + ". " + listItems.get(i).get("YHDD").toString() +
                                        listItems.get(i).get("YHBW").toString()
                                        + listItems.get(i).get("Problem").toString() + "责令" +
                                        listItems.get(i).get("Dispose").toString() + "。";
                                if (j1.length() > 0) {
                                    j1 = j1 + "、" + listStr.size();
                                } else {
                                    j1 = "第" + listStr.size();
                                }

                            }
                        }

                    }
                }
                if (j1.length() > 0 && j2.length() > 0) {
                    j = "1." + j1 + "项责令立即整改。\n2." + j2 + "项" + j3 + "前整改完毕。";
                } else if (j1.length() > 0 && j2.length() == 0) {
                    j = "1." + j1 + "项责令立即整改。";
                } else if (j1.length() == 0 && j2.length() > 0) {
                    j = "1." + j2 + "项" + j3 + "前整改完毕。";
                }
                String[] dateStrings = new String[dateList.size()];
                for (int i = 0; i < listItems.size(); i++) {
                    Date curDate = new Date(System.currentTimeMillis());//获取当前时间
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
                    if (ProblemListAdapter.isSelected.get(i)) {
                        String[] strings = listItems.get(i).get("DayNum").toString().split("-");
                        listStr.add(listItems.get(i).get("CheckId").toString());
                        num++;
                        Date date1 = null;
                        int i1 = 0;
                        try {
                            date1 = sdf.parse(listItems.get(i).get("DayNum").toString());
                            i1 = StringOrDate.getGapCount(curDate, date1);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        for (int i2 = 0; i2 < dateList.size(); i2++) {
                            if (listItems.get(i).get("DayNum").equals(dateList.get(i2)) && i1 > 0) {
                                if (dateStrings[i2] != null) {
                                    dateStrings[i2] = dateStrings[i2] + "、" + (i + 1);
                                } else {
                                    dateStrings[i2] = (i + 1) + "";
                                }
                            }
                        }


                    }
                }
                String s5 = "";
                int x = 1;
                if (j1.length() > 1) {
                    s5 = "1." + j1 + "项责令立即整改。";
                    x = 2;
                }

                for (int i = 0; i < dateStrings.length; i++) {
                    String[] strings = dateList.get(i).split("-");
                    if (dateStrings[i] != null && s5.length() > 0) {
                        s5 = s5 + "\n" + x + ".第" + dateStrings[i] + "项责令" +
                                strings[0] + "年" + strings[1] + "月" + strings[2] + "日" + "前整改完毕。";
                        x++;
                    } else if (dateStrings[i] != null) {
                        s5 = x + ".第" + dateStrings[i] + "项责令" +
                                strings[0] + "年" + strings[1] + "月" + strings[2] + "日" + "前整改完毕。";
                        x++;
                    }
                }
                Log.d("文书数据", s5);
//                Log.d("数据BusinessType", BusinessType);
//                Log.d("文书数据", listStr.toString() + BusinessType + ";?");
                if (s.length() > 0) {

                    intent.setClass(this, PrintOutActivity.class);
                    intent.putExtra("PlanId", getIntent().getStringExtra("id"));
                    intent.putExtra("CheckId", s);
                    intent.putExtra("Problems", s6);
//                    intent.putExtra("rectify", s2);
                    intent.putExtra("rectify", num + "");
                    intent.putExtra("DayNum", s3);
//                    intent.putExtra("Problems_1", j);
                    intent.putExtra("Problems_1", s5);
                    intent.putExtra("BusinessType", BusinessType);
                    startActivity(intent);

                } else {
                    Toast.makeText(CheckUpDateActivity.this, "请选择需要上传的问题", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.bt_check_up_date_02://添加问题
                switch (BusinessType) {
                    case "煤矿":
                        BusinessState = 0;
                        break;
                    case "非煤矿山":
                        BusinessState = 5;
                        break;
                    case "烟花爆竹":
                        BusinessState = 3;
                        break;
                    case "危险化学品":
                        BusinessState = 1;
                        break;
                    case "冶金工贸":
                        BusinessState = 8;
                        break;
                    case "其他":
                        BusinessState = 9;
                        break;
                    default:
                        break;
                }
                if (!checkEnterprise11.getText().equals("--点击选择--")) {
                    intent.setClass(CheckUpDateActivity.this, AddCheckProblemActivity.class);
                    intent.putExtra("state", "1");
                    intent.putExtra("name", getIntent().getStringExtra("state"));//区分是否是职业卫生检查
                    intent.putExtra("id", getIntent().getStringExtra("id"));
                    intent.putExtra("BusinessName", checkEnterprise01.getText().toString());
//                Log.e("数据",BusinessState+"");
                    intent.putExtra("BusinessState", BusinessState);
                    startActivity(intent);
                    overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                } else {
                    Toast.makeText(this, "请先制定人员分组", Toast.LENGTH_SHORT).show();
                }
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
                        listStr.add(listItems.get(i).get("CheckId").toString());
                        if (listStr.size() == 1) {
                            s4 = listItems.get(i).get("CheckId").toString();
                        } else {
                            s4 = s4 + "," + listItems.get(i).get("CheckId").toString();
                        }
                    }
                }
                if (s4.length() > 0) {
//                    seekAndJoin();
//                    LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    if (Build.VERSION.SDK_INT >= 23 && !GPSUtil.getIns().isOpen(CheckUpDateActivity.this)) {
                        Toast.makeText(this, "请手动打开GPS定位，否则会影响使用", Toast.LENGTH_SHORT).show();
                    } else {
                        wifiManager = (WifiManager) super.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                        //初始化wifiAdmin
                        wifiAdmin = new WifiAdmin(this);
                        ip = address;
                        dialog01(s4);
                    }

                } else {
                    Toast.makeText(CheckUpDateActivity.this, "请选择需要上传的问题", Toast.LENGTH_SHORT).show();
                }

//                Log.d("问题上传数据", listStr.toString());
                break;
            case R.id.bt_check_up_date_05://取消计划制定
                tcpout = true;
                onBackPressed();
                break;
            case R.id.bt_check_up_date_06://保存计划制定
                if (checkEnterprise06.getText().length() > 0 && BusinessId.length() > 0 &&
                        !CheckType.equals("-请选择-") && !fromTime.getText().toString().equals("--点击选择--")) {
                    if (listItems2.size() == i_checkEnterprise13 + 1
                            && etCheckEnterprise10.getText().toString().length() > 0) {
                        otherCompany = etCheckEnterprise10.getText().toString();
                    }
                    if (CheckType.equals("其他")) {
                        CheckType = etCheckEnterprise0.getText().toString();
                    }
                    saveDate(BusinessId);
                    state = "jh";


                } else {
                    Toast.makeText(CheckUpDateActivity.this, "请完善内容", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.bt_check_up_date_07://全选按钮
                for (int i = 0; i < listItems.size(); i++) {
                    ProblemListAdapter.isSelected.put(i, true);
                    listAdapter.notifyDataSetChanged();
                }
                break;
            case R.id.delete_layout:
                switch (getIntent().getStringExtra("name")) {
                    case "1":
                        dialog(getIntent().getStringExtra("id"));
                        break;
                    case "2":
                        if (popupwindow != null && popupwindow.isShowing()) {
                            popupwindow.dismiss();
                            return;
                        } else {
                            initmPopupWindowView();
                            popupwindow.showAsDropDown(v, 0, 5);
                        }
                        break;
                    case "3":
                        if (popupwindow != null && popupwindow.isShowing()) {
                            popupwindow.dismiss();
                            return;
                        } else {
                            initmPopupWindowView();
                            popupwindow.showAsDropDown(v, 0, 5);
                        }
                        break;
                    case "4":
                        if (popupwindow != null && popupwindow.isShowing()) {
                            popupwindow.dismiss();
                            return;
                        } else {
                            initmPopupWindowView();
                            popupwindow.showAsDropDown(v, 0, 5);
                        }
                        break;
                    default:
                        break;
                }

                break;
            case R.id.button1://复制计划
                intent.setClass(CheckUpDateActivity.this, CheckUpDateActivity.class);
                intent.putExtra("name", "8");
                intent.putExtra("id", getIntent().getStringExtra("id"));
                startActivity(intent);
                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                onBackPressed();
                break;
            case R.id.button2://指定现场检查方案文书
                intent.setClass(this, PrintOut08Activity.class);
                intent.putExtra("PlanId", getIntent().getStringExtra("id"));
                intent.putExtra("BusinessType", BusinessType);
                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                startActivity(intent);
                break;
            case R.id.button3://删除计划
                dialog(getIntent().getStringExtra("id"));
                break;
            default:
                break;
        }
    }

    /**
     * 弹出菜单
     */
    public void initmPopupWindowView() {

        // // 获取自定义布局文件pop.xml的视图
        View customView = getLayoutInflater().inflate(R.layout.popview_item_1,
                null, false);
        // 创建PopupWindow实例,200,150分别是宽度和高度
        popupwindow = new PopupWindow(customView, 250, getWallpaperDesiredMinimumHeight());
        // 设置动画效果 [R.style.AnimationFade 是自己事先定义好的]
        popupwindow.setAnimationStyle(R.style.AnimationFade);
        popupwindow.setFocusable(true);
        // 自定义view添加触摸事件
        customView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (popupwindow != null && popupwindow.isShowing()) {
                    popupwindow.dismiss();
                    popupwindow = null;
                }

                return false;
            }
        });

        /** 在这里可以实现自定义视图的功能 */
        Button btton2 = (Button) customView.findViewById(R.id.button2);
        btton2.setOnClickListener(this);
        btton2.setOnTouchListener(this);
        Button btton1 = (Button) customView.findViewById(R.id.button1);
        btton1.setOnClickListener(this);
        btton1.setOnTouchListener(this);
        Button btton3 = (Button) customView.findViewById(R.id.button3);
        btton3.setOnClickListener(this);
        btton3.setOnTouchListener(this);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 0:
                if (resultCode == 0) {
                    checkEnterprise01.setText(data.getStringExtra("name"));
                    date(data.getStringExtra("BusinessId"));
                    BusinessId = data.getStringExtra("BusinessId");
                }
                break;
            case 1:
                if (resultCode == 0) {
//                    if (Headman.length() > 0) {
//                        HeadmanID = HeadmanID + ":" + data.getStringExtra("nameHeadmanID");
//                        Address = Address + ":" + data.getStringExtra("Address");
//                        Members = Members + ":" + data.getStringExtra("nameMembersID");
//                        Headman = Headman + "," + data.getStringExtra("nameHeadman");
//                        checkEnterprise11.setText(Headman);
//                        Log.d("数据", HeadmanID + Address + Members);
//                    } else {
//                    HeadmanID = data.getStringExtra("nameHeadmanID");
//                    Address = data.getStringExtra("Address");
//                    Members = data.getStringExtra("nameMembersID");
                    Headman = data.getStringExtra("nameHeadman");
                    checkEnterprise11.setText(Headman);
//                        Log.d("数据", HeadmanID + Address + Members);
//                    }
                    init();
                }
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 打开企业信息数据库
     */
    protected void date(String s) {

        // 初始化，只需要调用一次
        AssetsDatabaseManager.initManager(getApplication());
        // 获取管理对象，因为数据库需要通过管理对象才能够获取
        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
        // 通过管理对象获取数据库
        SQLiteDatabase db = mg.getDatabase("users.db");

        try {
            // 对数据库进行操作
            String s_c = "SELECT* FROM ELL_Business WHERE BusinessId = ?";
            Cursor c = db.rawQuery(s_c, new String[]{s});
            c.moveToFirst();
            // 填入数据
            String s1 = c.getString(c.getColumnIndex("BusinessName"));
            checkEnterprise01.setText(s1);
            checkEnterprise02.setText(c.getString(c.getColumnIndex("Address")));
            checkEnterprise03.setText(c.getString(c.getColumnIndex("LegalPerson")));
            checkEnterprise04.setText(c.getString(c.getColumnIndex("LegalPersonPost")));
            checkEnterprise05.setText(c.getString(c.getColumnIndex("LegalPersonPhone")));
            c.close();
        } catch (Exception e) {
            Log.e("数据库报错", e.toString(), e);
            checkEnterprise01.setText("未知，请更新数据");
            checkEnterprise02.setText("未知，请更新数据");
            checkEnterprise03.setText("未知，请更新数据");
            checkEnterprise04.setText("未知，请更新数据");
            checkEnterprise05.setText("未知，请更新数据");
        }
    }

    /**
     * 保存计划写入数据库
     */
    protected void saveDate(String s) {
        // 初始化，只需要调用一次
        AssetsDatabaseManager.initManager(getApplication());
        // 获取管理对象，因为数据库需要通过管理对象才能够获取
        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
        // 通过管理对象获取数据库
        SQLiteDatabase db = mg.getDatabase("users.db");
        try {
//            PlanId = UUID.randomUUID().toString();//计划主键
            DateFormat df = new SimpleDateFormat(
                    "yyyy-MM-dd", Locale.getDefault());
            String date = df.format(new Date());
            Date curDate = StringOrDate.stringToDate(date);//获取当前时间
            String userId = SharedPreferencesUtil.getStringData(this, "userId", "");
            db.execSQL("REPLACE INTO ELL_EnforcementPlan VALUES('" +
                    PlanId + "','" +
                    s + "','" +
                    CheckType + "','" +
                    d_from_time + "','" +
                    d_end_time + "','" +
                    CompanyId + "','" +
                    userId + "','" + curDate + "','" +
                    checkEnterprise06.getText().toString() + "','" +
                    checkEnterprise0601.getText().toString() + "','" +
                    otherCompany + "')");

//            /**
//             * 判断是否保存分组
//             * */
//            if (HeadmanID.length() > 0) {
//                String[] temp = HeadmanID.split(":");
//                String[] strings = Address.split(":");
//                String[] strings1 = Members.split(":");
//                for (int i = 0; i < temp.length; i++) {
//                    if (temp[i].length() > 0) {
//
////                        saveGroup(PlanId, temp[i], strings[i], strings1[i], "1");
//                        Log.d("保存de分组数据", PlanId + temp[i] + strings[i] + strings1[i]);
//                    }
//                }
//
//            } else {
            Toast.makeText(CheckUpDateActivity.this, "计划已保存", Toast.LENGTH_SHORT).show();
            dialogGroup(PlanId);
//            }
        } catch (Exception e) {
            Log.e("数据库报错", e.toString(), e);
            Toast.makeText(CheckUpDateActivity.this, "请完善信息", Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * 在复制计划同时复制分组
     */
    protected void saveGroup(String s, String headmanID, String address, String members, String state) {
        // 初始化，只需要调用一次
        AssetsDatabaseManager.initManager(getApplication());
        // 获取管理对象，因为数据库需要通过管理对象才能够获取
        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
        // 通过管理对象获取数据库
        SQLiteDatabase db = mg.getDatabase("users.db");
        switch (state) {
            case "1"://分组信息与计划一同保存
                try {
                    String GroupId = UUID.randomUUID().toString();//分组主键
                    db.execSQL("REPLACE INTO ELL_GroupInfo VALUES('" +
                            GroupId + "','" +
                            s + "','" +
                            headmanID + "','" +
                            address + "')");
                    String[] strings = members.split(",");
                    for (String string : strings) saveMember(GroupId, string);
                } catch (Exception e) {
                    Log.e("数据库报错", e.toString(), e);
                }
                break;
            case "2"://分组信息在修改中保存
                break;
            default:
                break;

        }

    }

    /**
     * 保存组员数据
     */
    protected void saveMember(String gourp, String users) {
        // 初始化，只需要调用一次
        AssetsDatabaseManager.initManager(getApplication());
        // 获取管理对象，因为数据库需要通过管理对象才能够获取
        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
        // 通过管理对象获取数据库
        SQLiteDatabase db = mg.getDatabase("users.db");
        try {
            String memberId = UUID.randomUUID().toString();//分组主键
            db.execSQL("REPLACE INTO ELL_Member VALUES('" +
                    memberId + "','" +
                    gourp + "','" +
                    users + "')");
        } catch (Exception e) {
            Log.e("数据库报错", e.toString(), e);
        }

    }


    /**
     * 修改计划时打开执法计划数据库
     */
    protected void openPlanDate(String s) {

        // 初始化，只需要调用一次
        AssetsDatabaseManager.initManager(getApplication());
        // 获取管理对象，因为数据库需要通过管理对象才能够获取
        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
        // 通过管理对象获取数据库
        SQLiteDatabase db = mg.getDatabase("users.db");
        try {
            // 对数据库进行操作
            String s_c = "SELECT* FROM ELL_EnforcementPlan WHERE PlanId = ?";
            Cursor cursor = db.rawQuery(s_c,
                    new String[]{s});
            cursor.moveToFirst();
            checkEnterprise0701.setText(cursor.getString(cursor.getColumnIndex("EnforceAccording")));
            checkEnterprise07.setText(cursor.getString(cursor.getColumnIndex("Address")));
            checkEnterprise0601.setText(cursor.getString(cursor.getColumnIndex("EnforceAccording")));
            s_c = "SELECT* FROM ELL_Business WHERE BusinessId = ?";
            Cursor c = db.rawQuery(s_c,
                    new String[]{cursor.getString(cursor.getColumnIndex("BusinessId"))});
            c.moveToFirst();
            BusinessType = c.getString(c.getColumnIndex("BusinessType"));
            s_c = "SELECT* FROM ELL_GroupInfo WHERE PlanId = ?";
            Cursor cursor1 = db.rawQuery(s_c,
                    new String[]{s});
            // 填入数据
            Resources res = getResources();
            String[] strings = res.getStringArray(R.array.s_car_num_7);
            for (int i = 0; i < strings.length; i++) {
                if (strings[i].equals(cursor.getString(cursor.getColumnIndex("CheckType")))) {
                    eCarNum7.setSelection(i);
                    CheckType = strings[i];
                    break;
                } else if (i == 9) {
                    eCarNum7.setSelection(9);
                    CheckType = strings[9];
                    etCheckEnterprise0.setText(cursor.getString(cursor.getColumnIndex("CheckType")));
                }
            }

            checkEnterprise08.setText(cursor.getString(cursor.getColumnIndex("CheckType")));
            checkEnterprise06.setText(cursor.getString(cursor.getColumnIndex("Address")));
            fromTime.setText(cursor.getString(cursor.getColumnIndex("StatTime")));
            fromTime1.setText(cursor.getString(cursor.getColumnIndex("StatTime")));
            d_from_time = StringOrDate.stringToDate(cursor.getString(cursor.getColumnIndex("StatTime")));
            endTime.setText(cursor.getString(cursor.getColumnIndex("EndTime")));
            endTime1.setText(cursor.getString(cursor.getColumnIndex("EndTime")));
            d_end_time = StringOrDate.stringToDate(cursor.getString(cursor.getColumnIndex("EndTime")));
            String s1 = c.getString(c.getColumnIndex("BusinessName"));
            checkEnterprise01.setText(s1);
            BusinessId = cursor.getString(cursor.getColumnIndex("BusinessId"));
            checkEnterprise02.setText(c.getString(c.getColumnIndex("Address")));
            checkEnterprise03.setText(c.getString(c.getColumnIndex("LegalPerson")));
            checkEnterprise04.setText(c.getString(c.getColumnIndex("LegalPersonPost")));
            checkEnterprise05.setText(c.getString(c.getColumnIndex("LegalPersonPhone")));
            int i = 0;
            String s2 = "";
            while (cursor1.moveToNext()) {
                try {
                    s_c = "SELECT* FROM Base_User WHERE UserId = ?";
                    Cursor cursor2 = db.rawQuery(s_c,
                            new String[]{cursor1.getString(cursor1.getColumnIndex("Headman"))});
                    cursor2.moveToFirst();
//                    if (i == 0) {
                    s2 = cursor2.getString(cursor2.getColumnIndex("RealName")) + "组";
//                    } else {
//                        s2 = s2 + "," + cursor2.getString(cursor2.getColumnIndex("RealName")) + "组";
//                    }
                    i++;
                    cursor2.close();
                } catch (Exception e) {
                    Log.e("人员用户数据库报错", e.toString());
                }

            }
            checkEnterprise11.setText("--点击选择--");
            checkEnterprise12.setText("--暂无--");
            if (s2.length() > 0) {
                checkEnterprise12.setText(s2);
                checkEnterprise11.setText(s2);
                Headman = s2;
            }
            /**
             * 判断是否为组长
             * */
            String userId = SharedPreferencesUtil.getStringData(this, "userId", "");
            cursor1.moveToFirst();
            try {
                if (!cursor1.getString(cursor1.getColumnIndex("Headman")).equals(userId)) {
                    btCheckUpDate03.setVisibility(View.GONE);
                    btCheckUpDate01.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                btCheckUpDate03.setVisibility(View.GONE);
                btCheckUpDate01.setVisibility(View.GONE);
            }

            /**
             * 执法单位、联合执法
             * */
            Cursor cursor3 = db.rawQuery("SELECT* FROM Base_Company", null);
            int x = 0;
            while (cursor3.moveToNext()) {
                if (cursor.getString(cursor.getColumnIndex("CompanyId")).
                        equals(cursor3.getString(cursor3.getColumnIndex("CompanyId")))) {
                    checkEnterprise10.setSelection(x);
                    CompanyId = cursor.getString(cursor.getColumnIndex("CompanyId"));
                    Log.d("数据", cursor.getString(cursor.getColumnIndex("OtherCompany")) + "结束");
                    if (cursor.getString(cursor.getColumnIndex("OtherCompany")).length() > 0) {
                        s1 = cursor3.getString(cursor3.getColumnIndex("FullName")) + "\n" +
                                cursor.getString(cursor.getColumnIndex("OtherCompany"));
                        checkBox02.setChecked(true);
                        checkEnterprise13.setVisibility(View.VISIBLE);
                        Cursor cursor4 = db.rawQuery("SELECT* FROM Base_Company", null);
                        i = 0;
                        int i1 = 0;
                        while (cursor4.moveToNext()) {
                            if (cursor.getString(cursor.getColumnIndex("OtherCompany")).
                                    equals(cursor4.getString(cursor4.getColumnIndex("FullName")))) {
                                checkEnterprise13.setSelection(i);
                                etCheckEnterprise10.setVisibility(View.GONE);
                                break;
                            } else if (cursor4.getString(cursor4.getColumnIndex("FullName")).equals("其他")) {
                                checkEnterprise13.setSelection(i1);
                                etCheckEnterprise10.setVisibility(View.VISIBLE);
                            } else {
                                checkEnterprise13.setSelection(i1 + 1);
                                etCheckEnterprise10.setVisibility(View.VISIBLE);
                            }
                            i++;
                            i1++;
                        }
                        cursor4.close();
//                        etCheckEnterprise10.setVisibility(View.VISIBLE);
//                        checkEnterprise13.setSelection(6);
                        etCheckEnterprise10.setText(cursor.getString(cursor.getColumnIndex("OtherCompany")));
                    } else {
                        s1 = cursor3.getString(cursor3.getColumnIndex("FullName"));
                    }

                    otherCompany = cursor.getString(cursor.getColumnIndex("OtherCompany"));
                    checkEnterprise9.setText(s1);
                    break;
                }
                x++;
            }

            cursor3.close();
            etCheckEnterprise10.setText(cursor.getString(cursor.getColumnIndex("OtherCompany")));
            if (CheckType.equals("其他")) {
                etCheckEnterprise0.setText(cursor.getString(cursor.getColumnIndex("CheckType")));
            }
            cursor.close();
            c.close();


            if (getIntent().getStringExtra("name").equals("8")) {
                PlanId = UUID.randomUUID().toString();//计划主键
                DateFormat df = new SimpleDateFormat(
                        "yyyy-MM-dd", Locale.getDefault());
                //获取当前时间
                String date = df.format(new Date(System.currentTimeMillis()));
                fromTime.setText(date);
                fromTime1.setText(date);
                d_from_time = StringOrDate.stringToDate(date);
                endTime.setText(date);
                endTime1.setText(date);
                d_end_time = StringOrDate.stringToDate(date);
                try {
                    if (getIntent().getStringExtra("Simple").equals("1")) {
                        eCarNum7.setSelection(1);
                    }
                } catch (Exception e) {
                    Log.e("数据非快速执法", e.toString(), e);
                }
                /*打开原计划分组信息*/
                Cursor c2 = db.rawQuery("SELECT* FROM ELL_GroupInfo WHERE PlanId = ?",
                        new String[]{getIntent().getStringExtra("id")});
                while (c2.moveToNext()) {
                    /*组员信息*/
                    Cursor c3 = db.rawQuery("SELECT* FROM ELL_Member WHERE GroupId = ?",
                            new String[]{c2.getString(c2.getColumnIndex("GroupId"))});
                    int i1 = 0;
                    String MemberId = "";
                    while (c3.moveToNext()) {
                        if (i1 == 0) {
                            MemberId = c3.getString(c3.getColumnIndex("Member"));

                        } else if (i1 > 0) {
                            MemberId = MemberId + "," + c3.getString(c3.getColumnIndex("Member"));
                        }
                        Log.d("Member的数据", MemberId);
                        i1++;
                    }

                    saveGroup(PlanId, c2.getString(c2.getColumnIndex("Headman")), c2.getString(c2.getColumnIndex("Address")), MemberId, "1");

                }

            }
            cursor1.close();
        } catch (Exception e) {
            Log.e("打开数据库报错", e.toString(), e);
        }
    }

    /**
     * 弹出确认对话框
     */
    protected void dialog(final String s) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("确认删除");
        builder.setMessage("是否删除计划 ？");
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
                Toast.makeText(CheckUpDateActivity.this, "已取消", Toast.LENGTH_SHORT).show();
            }
        });
        builder.show();
    }

    /**
     * 弹出确认对话框
     */
    protected void dialogProblem(final String s, final int i) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("删除或修改");
        builder.setMessage("请选择删除或者修改此项问题。");
        builder.setPositiveButton("删除", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                deleterProblemDate(s);
            }
        });
        builder.setNegativeButton("修改", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent intent = new Intent();
                intent.setClass(CheckUpDateActivity.this, AddCheckProblemActivity.class);
                intent.putExtra("id", getIntent().getStringExtra("id"));
                intent.putExtra("name", getIntent().getStringExtra("state"));//区分是否是职业卫生检查
                intent.putExtra("CheckId", listItems.get(i).get("CheckId").toString());
                intent.putExtra("state", "2");
                intent.putExtra("BusinessName", checkEnterprise01.getText().toString());
                startActivity(intent);
            }
        });
        builder.show();
    }

    /**
     * 删除问题数据
     */
    protected void deleterProblemDate(String s) {
        // 初始化，只需要调用一次
        AssetsDatabaseManager.initManager(getApplication());
        // 获取管理对象，因为数据库需要通过管理对象才能够获取
        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
        // 通过管理对象获取数据库
        SQLiteDatabase db = mg.getDatabase("users.db");
        try {
            db.delete("ELL_CheckInfo", "CheckId = ?", new String[]{s});
            Toast.makeText(this, "已删除", Toast.LENGTH_SHORT).show();
            init();
        } catch (Exception e) {
            Log.e("删除问题数据库报错", e.toString());
        }
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
            String s_c = "SELECT* FROM ELL_GroupInfo WHERE PlanId = ?";
            Cursor cursor = db.rawQuery(s_c,
                    new String[]{s});
            while (cursor.moveToNext()) {
                db.delete("ELL_Member", "GroupId = ?", new String[]{cursor.getString(cursor.getColumnIndex("GroupId"))});
            }
            cursor.close();
            db.delete("ELL_EnforcementPlan", "PlanId = ?", new String[]{s});
            db.delete("ELL_GroupInfo", "PlanId = ?", new String[]{s});
            db.delete("ELL_CheckInfo", "PlanId = ?", new String[]{s});
            Toast.makeText(this, "已删除", Toast.LENGTH_SHORT).show();
            onBackPressed();
        } catch (Exception e) {
            Log.e("删除计划数据库报错", e.toString());
        }
    }

    /**
     * 添加检查表OPPP
     */
    protected void problemList(String s) {
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
            SimpleAdapter simpleAdapter;
            //对数据库进行操作
            String s_c = "SELECT* FROM ELL_CheckInfo WHERE PlanId = ?";
            Cursor cursor = db.rawQuery(s_c,
                    new String[]{s});
            int i = 0;
            while (cursor.moveToNext()) {
                listItem = new HashMap<>();
                try {
                    String[] strings = cursor.getString(cursor.getColumnIndex("MemberId")).split(",");
                    String s1 = "";
                    for (String string : strings) {
                        s_c = "SELECT* FROM Base_User WHERE UserId = ?";
                        Cursor cursor2 = db.rawQuery(s_c,
                                new String[]{string});
                        cursor2.moveToFirst();

                        String s2 = "";
                        try {
                            s2 = cursor2.getString(cursor2.getColumnIndex("RealName"));
                        } catch (Exception e) {
                            Log.e("问题发现人数据表报错", e.toString());
                        }
                        if (s1.length() > 0) {
                            s1 = s1 + "," + s2;
                        } else {
                            s1 = s2;
                        }
                        cursor2.close();
                    }
                    listItem.put("RealName", s1);
                    listItem.put("Problem", cursor.getString(cursor.getColumnIndex("Problem")));
                    listItem.put("CheckResult", cursor.getString(cursor.getColumnIndex("CheckResult")));
                    listItem.put("DisposeResult", cursor.getString(cursor.getColumnIndex("DisposeResult")));
                    listItem.put("DayNum", cursor.getString(cursor.getColumnIndex("DayNum")));
                    listItem.put("CheckId", cursor.getString(cursor.getColumnIndex("CheckId")));
                    listItem.put("Dispose", cursor.getString(cursor.getColumnIndex("Dispose")));
                    listItem.put("YHDD", cursor.getString(cursor.getColumnIndex("YHDD")));
                    listItem.put("YHBW", cursor.getString(cursor.getColumnIndex("YHBW")));
                    listItem.put("AddTime", cursor.getString(cursor.getColumnIndex("AddTime")));
                } catch (Exception e) {
                    Log.e("打开问题数据库报错", e.toString());
                }
                i++;
                listItems.add(listItem);
            }
            if (i != 0) {
                txTable.setVisibility(View.VISIBLE);
//                simpleAdapter = new SimpleAdapter(CheckUpDateActivity.this, listItems,
//                        R.layout.problem_list_item,
//                        new String[]{"Problem", "CheckResult", "DisposeResult", "DayNum"},
//                        new int[]{R.id.tx_problem_01, R.id.tx_problem_02, R.id.tx_problem_03, R.id.tx_problem_04});
                listAdapter = new ProblemListAdapter(CheckUpDateActivity.this, listItems, R.layout.problem_list_item,
                        new String[]{"Problem", "CheckResult", "DisposeResult", "DayNum", "YHDD", "RealName", "AddTime"},
                        new int[]{R.id.tx_problem_01, R.id.tx_problem_02, R.id.tx_problem_03,
                                R.id.tx_problem_04, R.id.tx_problem_05, R.id.tx_problem_06, R.id.tx_problem_07});
                listProblem.setAdapter(listAdapter);
                listProblem.setVisibility(View.VISIBLE);
//                Log.d("问题数据库", listItems.get(0).toString());
                //获得ListView的高度
                Utility.setListViewHeightBasedOnChildren(listProblem);

                listProblem.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        dialogProblem(listItems.get(position).get("CheckId").toString(), position);
//                        Intent intent = new Intent();
//                        intent.setClass(CheckUpDateActivity.this, AddCheckProblemActivity.class);
//                        intent.putExtra("id", getIntent().getStringExtra("id"));
//                        intent.putExtra("name", getIntent().getStringExtra("state"));//区分是否是职业卫生检查
//                        intent.putExtra("CheckId", listItems.get(position).get("CheckId").toString());
//                        intent.putExtra("state", "2");
//                        startActivity(intent);

                    }
                });
            } else {
                txTable.setVisibility(View.GONE);
            }

            cursor.close();
            dateList = new ArrayList<>();
            s_c = "SELECT DISTINCT DayNum FROM ELL_CheckInfo WHERE PlanId = ?";
            Cursor cursor1 = db.rawQuery(s_c,
                    new String[]{s});
            while (cursor1.moveToNext()) {
                Log.d("检查表去重日期数据", cursor1.getString(cursor1.getColumnIndex("DayNum")));
                dateList.add(cursor1.getString(cursor1.getColumnIndex("DayNum")));
                Log.d("检查表去重日期数据", dateList.size() + "");
            }
            cursor1.close();
        } catch (Exception e) {
            Log.e("添加问题检查表划数据库报错", e.toString());
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
     * 监听有没有wifi接入到wifi热点的线程
     */
    public class startNew extends Thread {
        public void run() {
            Intent intent_filetrans2 = new Intent(CheckUpDateActivity.this, Files_Trans_Activity.class);
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
//        popupWindow.showAtLocation(Linear_root, Gravity.CENTER, 0, 0);
        popupWindow.showAtLocation(LinearLayout2, Gravity.CENTER, 0, 0);
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
        /*if (mNetworkInfo != null) {*/
        wifiManager_01 = (WifiManager) super.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager_01.getConnectionInfo();
        int ipAddress = wifiInfo == null ? 0 : wifiInfo.getIpAddress();
        if (wifiManager_01.isWifiEnabled() && ipAddress != 0) {
            dialogWifi();
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

            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                onBackPressed();
            }
        });
        builder.show();
    }

    /**
     * 弹出确认对话框
     */
    protected void dialogWifi() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("请先关闭WiFi");
        builder.setMessage("是否关闭WiFi ？");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                wifiManager_01.setWifiEnabled(false);
                dialog.dismiss();
//                dialogWifiClose();
                Timer timer = new Timer();
                TimerTask timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        handler2.sendEmptyMessage(11);
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
        setResult(0);
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
    protected void dialog01(final String s) {
        final ConnectivityManager mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
        mWifiManager01 = (WifiManager) super.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("汇总问题");
        builder.setMessage("请选择将问题汇总到的地方。");
        builder.setPositiveButton("组长平板", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                /*if (mNetworkInfo == null) {
                    seekAndJoin();
                } else {
                    dialogWifi_01();
                }*/
                WifiInfo wifiInfo = mWifiManager01.getConnectionInfo();
                int ipAddress = wifiInfo == null ? 0 : wifiInfo.getIpAddress();
                if (mWifiManager01.isWifiEnabled() && ipAddress != 0) {
                    dialogWifi_01(1);
                } else {
                    seekAndJoin();
                }

            }
        });
        builder.setNegativeButton("网络端", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                String[] strings = s.split(",");
                for (int i = 0; i < strings.length; i++) {
                    httpPlanDate(strings[i], strings.length - i);
                }

            }
        });
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
     * 校验计划是否为下发计划
     */
    protected void httpPlanDate(final String s, final int i) {
        // 初始化，只需要调用一次
        AssetsDatabaseManager.initManager(getApplication());
        // 获取管理对象，因为数据库需要通过管理对象才能够获取
        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
        // 通过管理对象获取数据库
        SQLiteDatabase db = mg.getDatabase("users.db");
        String s_c = "SELECT* FROM ELL_CheckInfo WHERE CheckId = ?";
        Cursor cursor = db.rawQuery(s_c,
                new String[]{s});
        cursor.moveToFirst();
        //加载页添加
        if (progressDialog == null) {
            progressDialog = CustomProgressDialog.createDialog(this);
        }
        progressDialog.show();
        //添加参数
        HashMap<String, String> properties = new HashMap<>();
        properties.put("Xml", "<Request><data  code='select-EPlanExist'><no><PlanId>" +
                cursor.getString(cursor.getColumnIndex("PlanId")) + "</PlanId></no></data></Request>");
        cursor.close();
        properties.put("Token", "");
        WebServiceUtils.callWebService(WEB_SERVER_URL, "DynamicInvoke", properties, new WebServiceUtils.WebServiceCallBack() {

            //WebService接口返回的数据回调到这个方法中
            @Override
            public void callBack(SoapObject result) {
                if (result != null) {
                    Log.e("各类表上传返回数据", result.toString());
                    try {
//                                       Log.d("用户名发过去的结果",""+result);
                        Object detail = result.getProperty("DynamicInvokeResult");
                        JSONObject jsonObj;
                        jsonObj = XML.toJSONObject(detail.toString());

                        if (jsonObj.getJSONObject("DocumentElement").getJSONObject("Table").getString("Column1").equals("0")) {
                            if (progressDialog != null) {
                                progressDialog.dismiss();
                                progressDialog = null;
                            }
                            Toast.makeText(CheckUpDateActivity.this, "请选择正确计划汇总问题", Toast.LENGTH_SHORT).show();
//
                        } else {
                            httpDate(s, i);
                        }

                    } catch (Exception e) {
                        //关闭加载页
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                            progressDialog = null;
                        }
                        Toast.makeText(CheckUpDateActivity.this, "提交失败", Toast.LENGTH_SHORT).show();
                        Log.e("JSON exception", e.getMessage());
                        e.printStackTrace();
                    }
                } else {
                    //关闭加载页
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                        progressDialog = null;
                    }
                    Toast.makeText(CheckUpDateActivity.this, "网络未响应，提交失败", Toast.LENGTH_SHORT).show();

                }
            }
        });

    }

    /**
     * 从web端获取数据
     */
    protected void httpDate(String s, final int i) {
        // 初始化，只需要调用一次
        AssetsDatabaseManager.initManager(getApplication());
        // 获取管理对象，因为数据库需要通过管理对象才能够获取
        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
        // 通过管理对象获取数据库
        SQLiteDatabase db = mg.getDatabase("users.db");
        String s_c = "SELECT* FROM ELL_CheckInfo WHERE CheckId = ?";
        Cursor cursor = db.rawQuery(s_c,
                new String[]{s});
        cursor.moveToFirst();
        String CheckId = cursor.getString(cursor.getColumnIndex("CheckId"));
        String PlanId = cursor.getString(cursor.getColumnIndex("PlanId"));
        String CheckType = cursor.getString(cursor.getColumnIndex("CheckType"));
        String Subject = cursor.getString(cursor.getColumnIndex("Subject"));
        String CheckResult = cursor.getString(cursor.getColumnIndex("CheckResult"));
        String Dispose = cursor.getString(cursor.getColumnIndex("Dispose"));
        String DayNum = cursor.getString(cursor.getColumnIndex("DayNum"));
        String Fine = cursor.getString(cursor.getColumnIndex("Fine"));
        String DisposeResult = cursor.getString(cursor.getColumnIndex("DisposeResult"));
        String Problem = cursor.getString(cursor.getColumnIndex("Problem"));
        String MemberId = cursor.getString(cursor.getColumnIndex("MemberId"));
        String IsSelect = cursor.getString(cursor.getColumnIndex("IsSelect"));
        String YHDD = cursor.getString(cursor.getColumnIndex("YHDD"));
        String GPDBSJ = cursor.getString(cursor.getColumnIndex("GPDBSJ"));
        String GPDBJB = cursor.getString(cursor.getColumnIndex("GPDBJB"));
        String GPDBDW = cursor.getString(cursor.getColumnIndex("GPDBDW"));
        String YHBW = cursor.getString(cursor.getColumnIndex("YHBW"));
        String AddTime = cursor.getString(cursor.getColumnIndex("AddTime"));
        String YHZGQTP = "";
        s_c = "SELECT* FROM ELL_Photo WHERE CheckId = ?";
        Cursor cursor1 = db.rawQuery(s_c,
                new String[]{s});
        if (cursor1.getCount() != 0) {
            cursor1.moveToFirst();
            YHZGQTP = cursor1.getString(cursor1.getColumnIndex("Address"));
        }
        cursor1.close();
        cursor.close();
        //加载页添加
        if (progressDialog == null) {
            progressDialog = CustomProgressDialog.createDialog(this);
        }
        progressDialog.show();

        //添加参数
        HashMap<String, String> properties = new HashMap<>();
        properties.put("Xml", "<Request><data  code='add-Checkinfo'><no><CheckId>" +
                CheckId + "</CheckId><PlanId>" +
                PlanId + "</PlanId><CheckType>" +
                CheckType + "</CheckType><Subject>" +
                Subject + "</Subject><CheckResult>" +
                CheckResult + "</CheckResult><Dispose>" +
                Dispose + "</Dispose><DayNum>" +
                DayNum + "</DayNum><Fine>" +
                Fine + "</Fine><DisposeResult>" +
                DisposeResult + "</DisposeResult><Problem>" +
                Problem + "</Problem><MemberId>" +
                MemberId + "</MemberId><IsSelect>" +
                IsSelect + "</IsSelect><YHDD>" +
                YHDD + "</YHDD><GPDBSJ>" +
                GPDBSJ + "</GPDBSJ><GPDBJB>" +
                GPDBJB + "</GPDBJB><YHZGQTP>" +
                YHZGQTP + "</YHZGQTP><GPDBDW>" +
                GPDBDW + "</GPDBDW><YHBW>" +
                YHBW + "</YHBW><AddTime>" +
                AddTime + "</AddTime></no></data></Request>");
        properties.put("Token", "");
        Log.e("各类表上传数据", properties.toString());
        final String finalYHZGQTP = YHZGQTP;
        WebServiceUtils.callWebService(WEB_SERVER_URL, "DynamicInvoke", properties, new WebServiceUtils.WebServiceCallBack() {

            //WebService接口返回的数据回调到这个方法中
            @Override
            public void callBack(SoapObject result) {
                if (result != null) {
                    Log.e("各类表上传返回数据", result.toString());
                    try {
//                                       Log.d("用户名发过去的结果",""+result);
                        Object detail = result.getProperty("DynamicInvokeResult");
                        JSONObject jsonObj;
                        jsonObj = XML.toJSONObject(detail.toString());
                        Log.e("各类表上传返回数据", jsonObj.getJSONObject("Response").getString("result"));
                        if (jsonObj.getJSONObject("Response").getString("result").equals("true")) {
//                        if (detail.toString().equals("True")) {
                            if (i == 1) {
                                if (finalYHZGQTP.length() > 0) {
                                    upImage(finalYHZGQTP);
                                } else {
                                    Toast.makeText(CheckUpDateActivity.this, "汇总问题成功", Toast.LENGTH_SHORT).show();
                                }
                            }
//                                    onBackPressed();
                        } else {
                            if (progressDialog != null) {
                                progressDialog.dismiss();
                                progressDialog = null;
                            }
                            Toast.makeText(CheckUpDateActivity.this, "问题已汇总", Toast.LENGTH_SHORT).show();
                            if (finalYHZGQTP.length() > 0) {
                                upImage(finalYHZGQTP);
                            }
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
                        Toast.makeText(CheckUpDateActivity.this, "提交失败", Toast.LENGTH_SHORT).show();
                        Log.e("JSON exception", e.getMessage());
                        e.printStackTrace();
                    }
                } else {
                    //关闭加载页
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                        progressDialog = null;
                    }
                    Toast.makeText(CheckUpDateActivity.this, "网络未响应，提交失败", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    /**
     * 上传图片
     */
    protected void upImage(final String address) {
        if (flag) {

            if (address.length() > 0) {
                new Thread() {
                    @Override
                    public void run() {
                        if (flag) {

                            Boolean aBoolean = ftpUtils.uploadFile("/sdcard/LanDong/" + address + ".jpg", address + ".jpg");
                            if (aBoolean) {
                                handler.sendEmptyMessage(0);

                            } else {
                                handler.sendEmptyMessage(1);

                            }
                        }
                    }
                }.start();

            } else {
                Toast.makeText(CheckUpDateActivity.this, "上传文件失败", Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(CheckUpDateActivity.this, "上传文件失败", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 退出页面关闭GPS
     */
    @Override
    public void finish() {
        if (Build.VERSION.SDK_INT >= 23 && GPSUtil.getIns().isOpen(CheckUpDateActivity.this)) {
            Settings.Secure.putInt(getApplicationContext().getContentResolver(), Settings.Secure.LOCATION_MODE, Settings.Secure.LOCATION_MODE_OFF);
        }
        super.finish();
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

                            Intent intent_filetrans = new Intent(CheckUpDateActivity.this, Files_Trans_Activity.class);
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

    }


    void CreatConnection(final String name, final String key, final int type) {
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
    }*/
/**
 * 获取两个日期之间的间隔天数
 *
 * @return
 */
    /*public static int getGapCount(Date startDate, Date endDate) {
        Calendar fromCalendar = Calendar.getInstance();
        fromCalendar.setTime(startDate);
        fromCalendar.set(Calendar.HOUR_OF_DAY, 0);
        fromCalendar.set(Calendar.MINUTE, 0);
        fromCalendar.set(Calendar.SECOND, 0);
        fromCalendar.set(Calendar.MILLISECOND, 0);

        Calendar toCalendar = Calendar.getInstance();
        toCalendar.setTime(endDate);
        toCalendar.set(Calendar.HOUR_OF_DAY, 0);
        toCalendar.set(Calendar.MINUTE, 0);
        toCalendar.set(Calendar.SECOND, 0);
        toCalendar.set(Calendar.MILLISECOND, 0);

        return (int) ((toCalendar.getTime().getTime() - fromCalendar.getTime().getTime()) / (1000 * 60 * 60 * 24));
    }*/
    /**
     * 弹出确认分发对话框
     */
    protected void dialogWifiClose() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("已关闭WiFi");
        builder.setMessage("是否继续分发任务？");
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
    /**
     * 获取连接到手机热点设备的IP
     */
   /* StringBuilder resultList;
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
    }*/

}
