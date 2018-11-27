package com.jinan.ladongjiguan.anjiantong.activity;


import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Process;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.jinan.ladongjiguan.anjiantong.PublicClass.AssetsDatabaseManager;
import com.jinan.ladongjiguan.anjiantong.PublicClass.CustomProgressDialog;
import com.jinan.ladongjiguan.anjiantong.R;
import com.jinan.ladongjiguan.anjiantong.Service.MessageService;
import com.jinan.ladongjiguan.anjiantong.fragments.DatumFragment;
import com.jinan.ladongjiguan.anjiantong.fragments.HomeFragment;
import com.jinan.ladongjiguan.anjiantong.fragments.SearchFragment;
import com.jinan.ladongjiguan.anjiantong.fragments.SettingFragment;
import com.jinan.ladongjiguan.anjiantong.utils.SharedPreferencesUtil;
import com.lzy.widget.AlphaIndicator;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.jinan.ladongjiguan.anjiantong.fragments.HomeFragment.L_SAOYISAO;
/**
 * V1.0
 * 这个APP因为大部分时间都是加班赶工出来的，所以BUG超级多。
 * 没错，看我的注释，超级多。
 * 好多代码我已不知道写的什么了，因为需求太多太杂加班赶工。
 * 好多缺陷我知道但是依然这么写，因为需求太多太杂加班赶工。
 * 好多的代码应有注释但我没写全，因为需求太多太杂加班赶工。
 * 好多代码应该抽出做成工具类我没这么做，因为需求太多太杂加班赶工。
 * ……算了，反正项目都验收了，再有什么需求随随便便应付了事吧。
 * For the brave souls who get this far: You are the chosen ones,
 * the valiant knights of programming who toil away, without rest,
 * fixing our most awful code. To you, true saviors, kings of men,
 * I say this: never gonna give you up, never gonna let you down,
 * never gonna run around and desert you. Never gonna make you cry,
 * never gonna say goodbye. Never gonna tell a lie and hurt you.
 *
 * V2.0
 * 修改并优化的过程就像是在一个shift山上努力地挖个直通这个shift山中心的洞，然后爬进去并在这个洞中拉一坨shift。
 * 最后把洞用之前挖出来的shift堵上，完工。
 * 为啥是shift？当然是为了增加这个注释的趣味性咯~毕竟已经够恶心了……
 * o(*￣︶￣*)o
 */

public class MainActivity extends BaseActivity implements View.OnClickListener, View.OnTouchListener {
    @BindView(R.id.viewPager)
    ViewPager viewPager;
    @BindView(R.id.alphaIndicator)
    AlphaIndicator alphaIndicator;
    //    @BindView(R.id.bt_home_01)
//    LinearLayout btHome01;
//    @BindView(R.id.bt_home_02)
//    LinearLayout btHome02;
//    @BindView(R.id.bt_home_03)
//    LinearLayout btHome03;
//    @BindView(R.id.bt_home_04)
//    LinearLayout btHome04;
//    @BindView(R.id.bt_home_05)
//    LinearLayout btHome05;
//    @BindView(R.id.bt_home_06)
//    LinearLayout btHome06;
//    @BindView(R.id.bt_home_07)
//    LinearLayout btHome07;

//    @BindView(R.id.bt_home_08)
//    LinearLayout btHome08;

//    @BindView(R.id.bt_home_09)
//    LinearLayout btHome09;
//    @BindView(R.id.bt_home_10)
//    LinearLayout btHome10;
//    @BindView(R.id.bt_home_11)
//    LinearLayout btHome11;
//    @BindView(R.id.bt_home_12)
//    LinearLayout btHome12;
//    @BindView(R.id.activity_main)
//    LinearLayout activityMain;
//    @BindView(R.id.t_user_name)
//    TextView tUserName;
//    @BindView(R.id.message)
//    LinearLayout message;
    private long exitTime = 0;
    public static final String FTP_CONNECT_SUCCESSS = "ftp连接成功";
    public static final String FTP_CONNECT_FAIL = "ftp连接失败";
    public static final String FTP_DISCONNECT_SUCCESS = "ftp断开连接";
    public static final String FTP_FILE_NOTEXISTS = "ftp上文件不存在";

    public static final String FTP_UPLOAD_SUCCESS = "ftp文件上传成功";
    public static final String FTP_UPLOAD_FAIL = "ftp文件上传失败";
    public static final String FTP_UPLOAD_LOADING = "ftp文件正在上传";

    public static final String FTP_DOWN_LOADING = "ftp文件正在下载";
    public static final String FTP_DOWN_SUCCESS = "ftp文件下载成功";
    public static final String FTP_DOWN_FAIL = "ftp文件下载失败";

    public static final String FTP_DELETEFILE_SUCCESS = "ftp文件删除成功";
    public static final String FTP_DELETEFILE_FAIL = "ftp文件删除失败";
    public static final String IP = "http://" + "218.201.222.159" + ":4003/Index.asmx";
//    public static final String IP = "http://" + "218.201.222.159" + ":801/Index.asmx";//20180925前端口
    //        public static final String IP = "http://" + "192.168.2.220" + ":81/Index.asmx";//内网
    private String url;
    public static final String Id = UUID.randomUUID().toString();//日志主键
    public static MainActivity mainActivity;
    private CustomProgressDialog progressDialog = null;//加载页
    public static Boolean b_start = false;
    @Override
    protected void initView() {
//        setContentView(R.layout.activity_main);
        setContentView(R.layout.activity_main_new);
        ButterKnife.bind(this);
        mainActivity = this;
        viewPager.setAdapter(new MainActivity.MainAdapter(getSupportFragmentManager()));
        alphaIndicator.setViewPager(viewPager);

        try {
            String out_file_path = "/sdcard/LanDong";
            File dir = new File(out_file_path);
            if (!dir.exists()) {
                dir.mkdirs();
            }
        } catch (Exception e) {
            Log.e("生成文件报错", e.toString());
        }
        try {
            File sampleDir = new File("/sdcard/anjiantong/");//录制视频的保存地址
            if (!sampleDir.exists()) {
                sampleDir.mkdirs();
            }
        } catch (Exception e) {
            Log.e("生成文件报错", e.toString());
        }
    }

    @Override
    protected void init() {
        /**
         * 设置点击事件
         * */
//        btHome01.setOnClickListener(this);
//        btHome02.setOnClickListener(this);
//        btHome03.setOnClickListener(this);
//        btHome04.setOnClickListener(this);
//        btHome05.setOnClickListener(this);
//        btHome06.setOnClickListener(this);
//        btHome07.setOnClickListener(this);
//        btHome08.setOnClickListener(this);
//        btHome09.setOnClickListener(this);
//        btHome10.setOnClickListener(this);
//        btHome11.setOnClickListener(this);
//        btHome12.setOnClickListener(this);
//        message.setOnClickListener(this);
        /**
         * 设置触摸事件
         * */
//        btHome01.setOnTouchListener(this);
//        btHome02.setOnTouchListener(this);
//        btHome03.setOnTouchListener(this);
//        btHome04.setOnTouchListener(this);
//        btHome05.setOnTouchListener(this);
//        btHome06.setOnTouchListener(this);
//        btHome07.setOnTouchListener(this);
//        btHome08.setOnTouchListener(this);
//        btHome09.setOnTouchListener(this);
//        btHome10.setOnTouchListener(this);
//        btHome11.setOnTouchListener(this);
//        btHome12.setOnTouchListener(this);
    }

    /**
     * 捕捉返回事件按钮
     * <p>
     * 因为此 Activity 继承 TabActivity 用 onKeyDown 无响应，所以改用 dispatchKeyEvent
     * 一般的 Activity 用 onKeyDown 就可以了
     */

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (event.getAction() == KeyEvent.ACTION_DOWN && event.getRepeatCount() == 0) {
                this.exitApp();
            }
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    /**
     * 退出程序
     */

    private void exitApp() {
        // 初始化，只需要调用一次
        AssetsDatabaseManager.initManager(getApplication());
        // 获取管理对象，因为数据库需要通过管理对象才能够获取
        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
        // 通过管理对象获取数据库
        SQLiteDatabase db = mg.getDatabase("users.db");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        String outTime = sdf.format(curDate);


        // 判断2次点击事件时间
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(MainActivity.this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            String macAddress = null;
            WifiManager wifiMgr = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = (null == wifiMgr ? null : wifiMgr.getConnectionInfo());
            if (null != info) {
                macAddress = info.getMacAddress();
            }
            String us = SharedPreferencesUtil.getStringData(this, "userId", null);
            Log.e("MainActivity", "-----userId--" + us);
            ContentValues values = new ContentValues();
            values.put("DateId", Id);
            values.put("UserId", SharedPreferencesUtil.getStringData(this, "userId", null));
            values.put("Mac", macAddress);
            values.put("LoginTime", SharedPreferencesUtil.getStringData(this, "LoginTime", null));
            values.put("ExitTime", outTime);
            db.replace("ELL_Date", null, values);
            finish();
            overridePendingTransition(0, R.anim.zoomout);
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        //设置可查看权限
        String headShip = SharedPreferencesUtil.getStringData(MainActivity.this, "Headship", "1");
        switch (v.getId()) {
            case R.id.bt_home_01://企业信息
                intent.setClass(this, EnterpriseInformationActivity.class);
                intent.putExtra("state", "home");
                startActivity(intent);

                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                break;
            case R.id.bt_home_02://监管执法
                /*LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    Toast.makeText(MainActivity.this, "请打开GPS定位，否则导致程序异常", Toast.LENGTH_SHORT).show();
                } else {
                    intent.setClass(this, CheckUpMainActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                }*/
//                LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//                if (Build.VERSION.SDK_INT >= 23 && !manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//                    Toast.makeText(MainActivity.this, "请打开GPS定位，否则导致程序异常", Toast.LENGTH_SHORT).show();
//                } else {
                intent.setClass(this, CheckUpMainActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
//                }
                break;
            case R.id.bt_home_03://执法统计
                //判断权限
                if (headShip.equals("安监站监控员") ||
                        headShip.equals("驻矿员") ||
                        headShip.equals("其他") ||
                        headShip.equals("人事员") ||
                        headShip.equals("稽查员") ||
                        headShip.equals("调研员") ||
                        headShip.equals("办公室") ||
                        headShip.equals("工作人员") ||
                        headShip.equals("1")) {
                    Toast.makeText(MainActivity.this, "您没有查看的权限", Toast.LENGTH_SHORT).show();
                } else {

                    intent.setClass(this, CountActivity.class);
                    intent.putExtra("date_state", "8");
                    startActivity(intent);
                    overridePendingTransition(R.anim.zoomin, R.anim.zoomout);

                }
                break;
            case R.id.bt_home_04://执法人员
                //判断权限
                if (headShip.equals("安监站监控员") ||
                        headShip.equals("驻矿员") ||
                        headShip.equals("其他") ||
                        headShip.equals("人事员") ||
                        headShip.equals("稽查员") ||
                        headShip.equals("调研员") ||
                        headShip.equals("办公室") ||
                        headShip.equals("工作人员") ||
                        headShip.equals("1")) {
                    Toast.makeText(MainActivity.this, "您没有查看的权限", Toast.LENGTH_SHORT).show();
                } else {

                    intent.setClass(this, PersonnelManagementActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                }
                break;
            case R.id.bt_home_05://问题复查
                intent.setClass(this, ReviewActivity.class);
//                intent.putExtra("date_state","10");
                startActivity(intent);
                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                break;
            case R.id.bt_home_06://事故调查
                intent.setClass(this, AccidentInvestigationMainActivity.class);
                intent.putExtra("date_state", "11");
                startActivity(intent);
                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                break;
            case R.id.bt_home_07://安全知识库
                intent.setClass(this, KnowledgeActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                break;
            case R.id.bt_home_08://危化特性
                intent.setClass(this, SearchActivity.class);
                intent.putExtra("date_state", "2");
                startActivity(intent);
                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                break;
            case R.id.bt_home_09://职业卫生检查
                intent.setClass(this, CheckUpActivity.class);
                intent.putExtra("state", "6");
                intent.putExtra("date_state", "12");
                startActivity(intent);
                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                break;
            case R.id.bt_home_10://法律法规
//                intent.setClass(this,SearchActivity.class);
                intent.setClass(this, LawActivity.class);
                intent.putExtra("date_state", "1");
                startActivity(intent);
                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                break;
            case R.id.bt_home_11://数据管理
                intent.setClass(this, DateTransmissionActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                break;
            case R.id.bt_home_12://系统工具
                intent.setClass(this, SettingActivity.class);
                intent.putExtra("userName", SharedPreferencesUtil.getStringData(this, "Account", null));
                startActivity(intent);
                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                break;
            case R.id.message://消息提醒
                intent.setClass(this, MessageActivity.class);
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
            v.setBackgroundColor(ContextCompat.getColor(this, R.color.main_color_2));
        } else if (event.getAction() == MotionEvent.ACTION_CANCEL) {
            v.setBackgroundColor(ContextCompat.getColor(this, R.color.main_color_1));
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            v.setBackgroundColor(ContextCompat.getColor(this, R.color.main_color_1));
        }
        return false;
    }

    @SuppressLint({"SetTextI18n", "HardwareIds"})
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        Toast.makeText(this, resultCode+":"+requestCode, Toast.LENGTH_LONG).show();
//        switch (requestCode) {
//            case 0:
//                if (resultCode == 0) {
//                    // 初始化，只需要调用一次
//                    AssetsDatabaseManager.initManager(getApplication());
//                    // 获取管理对象，因为数据库需要通过管理对象才能够获取
//                    AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
//                    // 通过管理对象获取数据库
//                    SQLiteDatabase db = mg.getDatabase("users.db");
//                    Date curDate = new Date(System.currentTimeMillis());//获取当前时间
//                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
//                    String loginTime = sdf.format(curDate);
//                    SharedPreferencesUtil.saveStringData(this, "LoginTime", loginTime);
//                    String macAddress = null;
//                    WifiManager wifiMgr = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
//                    WifiInfo info = (null == wifiMgr ? null : wifiMgr.getConnectionInfo());
//                    if (null != info) {
//                        macAddress = info.getMacAddress();
//                    }
//                    ContentValues values = new ContentValues();
//                    values.put("DateId", Id);
//                    values.put("UserId", SharedPreferencesUtil.getStringData(this, "userId", null));
//                    values.put("Mac", macAddress);
//                    values.put("LoginTime", loginTime);
//                    values.put("ExitTime", "");
//                    db.replace("ELL_Date", null, values);
//                    /**
//                     * 添加数据库字段列
//                     * */
//                    try {
//                        db.execSQL("ALTER TABLE ELL_HiddenDanger ADD COLUMN AddUsers");//添加隐患复查人
//                    } catch (Exception e) {
//                        Log.e("数据库列表已存在", e.toString());
//                    }
//                    viewPager.setAdapter(new MainActivity.MainAdapter(getSupportFragmentManager()));
//                    alphaIndicator.setViewPager(viewPager);
////                    try {
////                        String[] strings = SharedPreferencesUtil.getStringData(this, "DepartmentId", null).split("-");
////                        Cursor cursor = db.rawQuery("SELECT* FROM Base_Company WHERE CompanyId = ?", new String[]{
//////                                SharedPreferencesUtil.getStringData(this, "DepartmentId", null).substring(0, 6)});
////                                strings[0]});
////                        cursor.moveToFirst();
////                        Cursor cursor1 = db.rawQuery("SELECT* FROM Base_Department WHERE DepartmentId = ?", new String[]{
////                                SharedPreferencesUtil.getStringData(this, "DepartmentId", null)});
////                        cursor1.moveToFirst();
//////                    Log.d("用户数据",cursor1.getString(cursor1.getColumnIndex("FullName")));
////                        tUserName.setText(cursor.getString(cursor.getColumnIndex("FullName")) +
////                                cursor1.getString(cursor1.getColumnIndex("FullName")) +
////                                "    " +
////                                data.getStringExtra("Account"));
////                        SharedPreferencesUtil.saveStringData(this, "Company", cursor.getString(cursor.getColumnIndex("FullName")));
////                        cursor.close();
////                        cursor1.close();
//////                        /**
//////                         * 启动消息推送服务
//////                         * */
//////                        if (CommonUtils.isNetworkConnected(MainActivity.this)) {
//////                            getMessage();
//////                        }
////                    } catch (Exception e) {
////                        Log.e("报错", e.toString());
////                        Toast.makeText(MainActivity.this, "数据异常", Toast.LENGTH_SHORT).show();
////                    }
//
//
//                } else {
//                    finish();
//                }
//                break;
//            case L_SAOYISAO:
//                if (resultCode == 1) {
//                    dataId( data.getStringExtra("BusinessId"));
//                    Toast.makeText(this,  data.getStringExtra("BusinessId"), Toast.LENGTH_LONG).show();
//                }
//                break;
//            default:
//                break;
//        }
//    }

    @Override
    public void finish() {
        stopService(new Intent(MainActivity.this, MessageService.class));
        super.finish();
        Process.killProcess(Process.myPid()); /**杀死这个应用的全部进程*/
    }

    /***
     * 获取消息数据
     */
//    protected void getMessage() {
//        //加载页添加
//        if (progressDialog == null) {
//            progressDialog = CustomProgressDialog.createDialog(this);
//        }
//        progressDialog.show();
//        String WEB_SERVER_URL = SharedPreferencesUtil.getStringData(this, "IPString", "");
//        HashMap<String, String> properties = new HashMap<>();
//        properties.put("Xml", "<Request><data code='select-Message'><no><Userid>" +
//                SharedPreferencesUtil.getStringData(this, "userId", null) + "</Userid></no></data></Request>");
//        properties.put("Token", "");
//        Log.d("发出的数据", properties.toString());
//        WebServiceUtils.callWebService(WEB_SERVER_URL, "DynamicInvoke", properties, new WebServiceUtils.WebServiceCallBack() {
//
//            //WebService接口返回的数据回调到这个方法中
//            @Override
//            public void callBack(SoapObject result) {
//                if (result != null) {
//                    Log.d("返回数据", result.toString());
//                    try {
//
//                        Object detail = result.getProperty("DynamicInvokeResult");
//                        JSONObject jsonObj;
//                        jsonObj = XML.toJSONObject(detail.toString());
//
//                        if (jsonObj.has("DocumentElement")) {
//                            message.setVisibility(View.VISIBLE);
//
//                        } else {
//                            message.setVisibility(View.INVISIBLE);
//                        }
//                        //关闭加载页
//                        if (progressDialog != null) {
//                            progressDialog.dismiss();
//                            progressDialog = null;
//                        }
//                    } catch (JSONException e) {
//                        //关闭加载页
//                        if (progressDialog != null) {
//                            progressDialog.dismiss();
//                            progressDialog = null;
//                        }
//                        Toast.makeText(MainActivity.this, "提交失败,数据错误缺失", Toast.LENGTH_SHORT).show();
//                        Log.e("JSON exception", e.getMessage());
//                        e.printStackTrace();
//                    }
//
//                } else {
//                    //关闭加载页
//                    if (progressDialog != null) {
//                        progressDialog.dismiss();
//                        progressDialog = null;
//                    }
//                    Toast.makeText(MainActivity.this, "网络未响应，提交失败", Toast.LENGTH_SHORT).show();
//
//                }
//            }
//        });
//    }

    private class MainAdapter extends FragmentPagerAdapter {

        private List<Fragment> fragments = new ArrayList<>();
        Fragment fragment = null;

        public MainAdapter(FragmentManager fm) {
            super(fm);
            fragment = new HomeFragment();
            fragments.add(fragment);
            fragment = new SearchFragment();
            fragments.add(fragment);
            fragment = new DatumFragment();
            fragments.add(fragment);
            fragment = new SettingFragment();
            fragments.add(fragment);
//            /**
//             * 登录界面
//             * */
//            Intent intent = new Intent();
//            intent.setClass(MainActivity.this, LoginActivity.class);
//            startActivity(intent);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }
//    /**
//     * 打开企业信息数据库
//     */
//    protected void dataId(String name) {
//
//        // 初始化，只需要调用一次
//        AssetsDatabaseManager.initManager(this);
//        // 获取管理对象，因为数据库需要通过管理对象才能够获取
//        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
//        // 通过管理对象获取数据库
//        SQLiteDatabase db = mg.getDatabase("users.db");
//
//        try {
//            // 对数据库进行操作
//            Cursor c = db.rawQuery("SELECT* FROM ELL_Business WHERE  BusinessId = ? AND ValidFlag = 0 ",
//                    new String[]{name});
//            if(c.getCount() == 0){
//                Toast.makeText(this, "无效二维码", Toast.LENGTH_LONG).show();
//            }else {
//                c.moveToFirst();
//                Intent intent = new Intent();
//                intent.putExtra("BusinessId", name);
//                intent.setClass(this, EnterpriseInformationDateActivity.class);
//                startActivity(intent);
//                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
//            }
//
//            c.close();
//        } catch (Exception e) {
//            Log.e("企业数据库报错", e.toString());
//        }
//    }

}
