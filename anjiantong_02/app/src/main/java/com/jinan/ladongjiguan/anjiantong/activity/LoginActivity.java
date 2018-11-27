package com.jinan.ladongjiguan.anjiantong.activity;


import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.jinan.ladongjiguan.anjiantong.PublicClass.AssetsDatabaseManager;
import com.jinan.ladongjiguan.anjiantong.PublicClass.CustomProgressDialog;
import com.jinan.ladongjiguan.anjiantong.PublicClass.DialogNormalDialog;
import com.jinan.ladongjiguan.anjiantong.PublicClass.GetMac;
import com.jinan.ladongjiguan.anjiantong.PublicClass.UpdateManager;
import com.jinan.ladongjiguan.anjiantong.R;
import com.jinan.ladongjiguan.anjiantong.utils.CommonUtils;
import com.jinan.ladongjiguan.anjiantong.utils.SharedPreferencesUtil;
import com.jinan.ladongjiguan.anjiantong.utils.WebServiceUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.ksoap2.serialization.SoapObject;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.security.MessageDigest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.jinan.ladongjiguan.anjiantong.activity.MainActivity.IP;
import static com.jinan.ladongjiguan.anjiantong.activity.MainActivity.mainActivity;


public class LoginActivity extends BaseActivity implements View.OnClickListener {
    @BindView(R.id.user_id)
    AutoCompleteTextView userId;
    @BindView(R.id.user_password)
    EditText userPassword;
    @BindView(R.id.login_bt)
    Button loginBt;
    @BindView(R.id.login_setting)
    Button loginSetting;
    @BindView(R.id.e_car_num_5)
    Spinner eCarNum5;
    @BindView(R.id.e_car_num_4)
    Spinner eCarNum4;
    @BindView(R.id.auto_save_password)
    CheckBox autoSavePassword;
    @BindView(R.id.nothing)
    CheckBox nothing;
    private Boolean aBoolean = true;//是否保存密码
    // 创建一个List集合，List集合的元素是Map
    private List<Map<String, Object>> listItems = new ArrayList<>();
    private List<Map<String, Object>> listItems1 = new ArrayList<>();
    private long exitTime = 0;
    private String code = "";//执法号
    private int ePasswordNum = 0;
    private Boolean bl_login_ip;
    private String WEB_SERVER_URL;
    private CustomProgressDialog progressDialog = null;//加载页
    private Boolean getaBoolean = true;//检测补全用的。
    private String getCode;
    private Boolean aBoolean1 = true;
    public final static int REQUEST_READ_PHONE_STATE = 1;
    private String TAG = LoginActivity.class.getSimpleName();

    @Override
    protected void initView() {
        setContentView(R.layout.login_layout);
        ButterKnife.bind(this);
//        Log.d("密码",string2MD5("123456"));
        SharedPreferencesUtil.saveStringData(LoginActivity.this, "IPString", IP);
//        Log.d("数据", "http://" + IP + ":801/Index.asmx");
        /**
         * 欢迎页面
         * */
        Intent intent = new Intent();
        intent.setClass(this, WelcomeActivity.class);
        startActivityForResult(intent, 0);
        /**
         * 检查是否登录过
         * */
        boolean bl_login = SharedPreferencesUtil.getBooleanData(this, "Login", false);
        if (bl_login) {
            userId.setText(SharedPreferencesUtil.getStringData(this, "Account", ""));
            userPassword.setText(SharedPreferencesUtil.getStringData(this, "userPassword", ""));
            code = SharedPreferencesUtil.getStringData(this, "Code", "");
//            dialogDoneDelete();
        }
        /**
         * 保存密码CheckBox
         * */
        autoSavePassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                aBoolean = isChecked;
            }
        });


        int permissionPhone = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
        int permissionWrite = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permissionLocation = ContextCompat.checkSelfPermission(this, Manifest.permission_group.LOCATION);
//        int permissionWrite2 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_SETTINGS);
        if (permissionPhone != PackageManager.PERMISSION_GRANTED && permissionWrite != PackageManager.PERMISSION_GRANTED && permissionLocation != PackageManager.PERMISSION_GRANTED/*&&permissionWrite2 !=PackageManager.PERMISSION_GRANTED*/) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_WIFI_STATE/*,Manifest.permission.WRITE_SETTINGS*/},
                    REQUEST_READ_PHONE_STATE);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.System.canWrite(LoginActivity.this)) {
            Toast.makeText(LoginActivity.this, "请在该设置页面勾选，才可以使用热点功能", Toast.LENGTH_SHORT).show();
            Uri selfPackageUri = Uri.parse("package:" + LoginActivity.this.getPackageName());
            Intent intent2 = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS, selfPackageUri);
            startActivity(intent2);
        } else {
            TelephonyManager telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
            getCode = telephonyManager.getDeviceId();
            String macAddress = null;
            WifiManager wifiMgr = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = (null == wifiMgr ? null : wifiMgr.getConnectionInfo());
            if (null != info) {
                macAddress = info.getMacAddress();
            }
            getCode = MD5("720" + GetMac.getMac(getApplicationContext()) + "ld2017");
        }
    }

    @Override
    protected void init() {
//        WEB_SERVER_URL= "http://218.201.222.159:801/Index.asmx";
//        upDateCheckDetailed();
        /**
         * 登录按钮
         * */
        loginBt.setOnClickListener(this);
        /**
         * 配置按钮
         * */
        loginSetting.setOnClickListener(this);
        /**
         * 配置下拉列表
         * */
//        Resources res = getResources();
//        String[] strings = res.getStringArray(R.array.s_car_num_13);
//        for (String string : strings) {
//            Map<String, Object> map = new HashMap<>();
//            map.put("FullName", string);
//            listItems1.add(map);
//        }
//        SimpleAdapter simpleAdapter = new SimpleAdapter(this, listItems1,
//                R.layout.login_spinner_item,
//                new String[]{"FullName"},
//                new int[]{R.id.text});
//        eCarNum5.setAdapter(simpleAdapter);
//        eCarNum5.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//
//                TextView v1 = (TextView) view.findViewById(R.id.text);
//                v1.setTextColor(Color.WHITE);
//                v1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
//                if (position == 1) {
//                    SharedPreferencesUtil.saveStringData(LoginActivity.this, "IPString", "http://192.130.1.22:802/Index.asmx");
//                } else if (position == 0) {
//                    SharedPreferencesUtil.saveStringData(LoginActivity.this, "IPString", "http://218.201.222.159:801/Index.asmx");
//                }
//
//                WEB_SERVER_URL = SharedPreferencesUtil.getStringData(LoginActivity.this, "IPString", "");
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });
        /**
         * 输入结束启动补全
         * */
        userId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                listItems = new ArrayList<>();//为啥不能添加这段代码，因为添加程序就会崩，不知道为啥会崩，总之就是会崩
//                if(userId.getText().toString().length()>1&&listItems.size()>0){
//                    userId.setActivated(true);
//                    listItems = new ArrayList<>();
//                }
                if (!CommonUtils.isNetworkConnected(LoginActivity.this) && getaBoolean) {
                    userIdFind(userId.getText().toString());
                } else if (CommonUtils.isNetworkConnected(LoginActivity.this)&&getaBoolean) {
                    WEB_SERVER_URL = SharedPreferencesUtil.getStringData(LoginActivity.this, "IPString", "");
                    code = "";
                    httpUserIdFind(userId.getText().toString());
                }else {

                    getaBoolean = true;
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        /**
         * 获取单位部门信息数据库
         */
//        // 初始化，只需要调用一次
//        AssetsDatabaseManager.initManager(getApplication());
//        // 获取管理对象，因为数据库需要通过管理对象才能够获取
//        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
//        // 通过管理对象获取数据库
//        final SQLiteDatabase db = mg.getDatabase("users.db");
//        Cursor c;
//        try {
//            // 对数据库进行操作
//            c = db.rawQuery("SELECT* FROM Base_Company",null);
//            // 创建一个List集合，List集合的元素是Map
//
//            listItems = new ArrayList<>();
//            Map<String, Object> listItem ;
//            while (c.moveToNext()) {
//                listItem = new HashMap<>();
//                listItem.put("CompanyId",c.getString(c.getColumnIndex("CompanyId")));
//                listItem.put("FullName",c.getString(c.getColumnIndex("FullName")));
//                listItems.add(listItem);
//            }
//
//            /**
//             * 下拉列表 科单位
//             * */
//            //绑定适配器和值
//            SimpleAdapter simpleAdapter = new SimpleAdapter(this, listItems,
//                    R.layout.login_spinner_item,
//                    new String[] {"FullName"},
//                    new int[] {R.id.text});
//            eCarNum5.setAdapter(simpleAdapter);
//            eCarNum5.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                @Override
//                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                    TextView v1 = (TextView)view.findViewById(R.id.text);
//                    v1.setTextColor(Color.WHITE);
//                    v1.setTextSize(TypedValue.COMPLEX_UNIT_SP,14);
//
//                    // 对数据库进行操作
//                    Cursor a = db.rawQuery("SELECT* FROM Base_Department WHERE CompanyId = ?",
//                            new String[]{listItems.get(position).get("CompanyId").toString()});
//                    // 创建一个List集合，List集合的元素是Map
//
//                    listItems1 = new ArrayList<>();
//                    Map<String, Object> listItem ;
//                    while (a.moveToNext()) {
//                        listItem = new HashMap<>();
//                        listItem.put("DepartmentId",a.getString(a.getColumnIndex("DepartmentId")));
//                        listItem.put("FullName",a.getString(a.getColumnIndex("FullName")));
//                        listItems1.add(listItem);
//                    }
//                    SimpleAdapter simpleAdapter = new SimpleAdapter(LoginActivity.this, listItems1,
//                            R.layout.login_spinner_item,
//                            new String[] {"FullName"},
//                            new int[] {R.id.text});
//                    eCarNum4.setAdapter(simpleAdapter);
//                    /**
//                     * 下拉列表 科室
//                     * */
//                    eCarNum4.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                        @Override
//                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                            TextView v1 = (TextView)view.findViewById(R.id.text);
//                            v1.setTextColor(Color.WHITE);
//                            v1.setTextSize(TypedValue.COMPLEX_UNIT_SP,14);
//                        }
//
//                        @Override
//                        public void onNothingSelected(AdapterView<?> parent) {
//
//                        }
//                    });
//                    a.close();
//                }
//
//                @Override
//                public void onNothingSelected(AdapterView<?> parent) {
//
//                }
//            });
//
//            c.close();
//        }catch (Exception e){
//            Log.e("数据库报错",e.toString());
//        }

    }

    /**
     * 点击事件
     */
    @Override
    public void onClick(View v) {
        Intent intent = new Intent();

        switch (v.getId()) {
            case R.id.login_bt://点击登录
                if (!CommonUtils.isNetworkConnected(LoginActivity.this) && ePasswordNum < 9) {
                    login();
                } else if (ePasswordNum < 9) {
                    WEB_SERVER_URL = SharedPreferencesUtil.getStringData(LoginActivity.this, "IPString", "");
                    httpLogin();
                } else if (ePasswordNum >= 9) {
                    Toast.makeText(this, "已清空数据库，请重新登陆并下载数据", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.login_setting://点击其他
                intent.setClass(LoginActivity.this, LoginSettingIPActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.in_from_right, android.R.anim.fade_out);
                break;
            default:
                break;
        }
    }

    /**
     * 密码加密
     */
    public static String string2MD5(String inStr) {
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            System.out.println(e.toString());
            e.printStackTrace();
            return "";
        }
        char[] charArray = inStr.toCharArray();
        byte[] byteArray = new byte[charArray.length];

        for (int i = 0; i < charArray.length; i++)
            byteArray[i] = (byte) charArray[i];
        byte[] md5Bytes = md5.digest(byteArray);
        StringBuffer hexValue = new StringBuffer();
        for (byte md5Byte : md5Bytes) {
            int val = ((int) md5Byte) & 0xff;
            if (val < 16)
                hexValue.append("0");
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString();

    }

    /**
     * 本地登录对比
     */
    protected void login() {
        if (userId.getText().length() > 0 && userPassword.getText().length() > 0) {
            date(code, userPassword.getText().toString());

        } else {
            Toast.makeText(this, "请输入完整的用户名与密码", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 打开用户信息数据库
     */
    protected void date(String Code, String p) {

        // 初始化，只需要调用一次
        AssetsDatabaseManager.initManager(getApplication());
        // 获取管理对象，因为数据库需要通过管理对象才能够获取
        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
        // 通过管理对象获取数据库
        SQLiteDatabase db = mg.getDatabase("users.db");

        try {
            // 对数据库进行操作
            Cursor c = db.rawQuery("SELECT* FROM Base_User WHERE Code = ?", new String[]{Code});
            c.moveToFirst();

            // 填入数据
            String s1 = c.getString(c.getColumnIndex("Password"));
            if (s1.equals(string2MD5(p))) {
                SharedPreferencesUtil.saveBooleanData(this, "Login", true);
                SharedPreferencesUtil.saveStringData(this, "Account", userId.getText().toString());
                SharedPreferencesUtil.saveStringData(this, "userPassword", userPassword.getText().toString());
                SharedPreferencesUtil.saveStringData(this, "userId", c.getString(c.getColumnIndex("UserId")));
                SharedPreferencesUtil.saveStringData(this, "DepartmentId", c.getString(c.getColumnIndex("DepartmentId")));
                SharedPreferencesUtil.saveStringData(this, "CompanyId", c.getString(c.getColumnIndex("DepartmentId")).substring(0, 6));
                SharedPreferencesUtil.saveStringData(this, "Code", Code);
                SharedPreferencesUtil.saveStringData(this, "Mobile", c.getString(c.getColumnIndex("Mobile")));
                if (!aBoolean) {
                    SharedPreferencesUtil.saveStringData(this, "Account", "");
                    SharedPreferencesUtil.saveStringData(this, "userPassword", "");
                    SharedPreferencesUtil.saveStringData(this, "Code", "");
                }
                Cursor cursor = db.rawQuery("SELECT* FROM Base_User WHERE UserId = ?",
                        new String[]{SharedPreferencesUtil.getStringData(this, "userId", null)});
                cursor.moveToFirst();
                String authenddate = cursor.getString(cursor.getColumnIndex("AuthEndDate"));
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'+08:00'", Locale.CHINA);//小写的mm表示的是分钟
                Date date = new Date(System.currentTimeMillis());
                Date date1 = sdf.parse(authenddate);
                int i = getGapCount(date, date1);
                if (i < 90 && i > 1 && aBoolean1) {
                    dialogDoneDelete(i);
                    aBoolean1 = false;
                } else if (i < 1) {
                    dialogDone();
                } else if (c.getString(c.getColumnIndex("ValidFlag")).equals("0")) {
//                    Intent intent = new Intent();
//                    intent.putExtra("Account", userId.getText().toString());
//                    setResult(0, intent);
                    finish();
                    overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                } else {
                    Toast.makeText(this, "无效用户", Toast.LENGTH_SHORT).show();
                }
            } else {
                if (ePasswordNum >= 9) {
                    Toast.makeText(this, "已清空数据库，请重新登陆并下载数据", Toast.LENGTH_SHORT).show();
                    db.execSQL("DELETE FROM Base_User");
                    String pathdir = Environment.getExternalStorageDirectory().getPath() + "/LanDong";
                    File dir = new File(pathdir);
                    deleteDirWihtFile(dir);
                } else {
                    ePasswordNum++;
                    Toast.makeText(this, "用户名密码错误，您还有" + (10 - ePasswordNum) + "次机会", Toast.LENGTH_SHORT).show();
                }
            }
            c.close();
        } catch (Exception e) {
            Log.e("数据库报错", e.toString());
            ePasswordNum++;
            Toast.makeText(this, "请检查网络，您还有" + (10 - ePasswordNum) + "次机会", Toast.LENGTH_SHORT).show();

        }
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
        // 判断2次点击事件时间
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
//            Intent intent = new Intent();
//            setResult(1, intent);
            finish();
            mainActivity.finish();
        }
    }

    /**
     * 补全用户名
     */
    private void userIdFind(String s) {
        code = "";

        // 初始化，只需要调用一次
        AssetsDatabaseManager.initManager(getApplication());
        // 获取管理对象，因为数据库需要通过管理对象才能够获取
        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
        // 通过管理对象获取数据库
        SQLiteDatabase db = mg.getDatabase("users.db");
        try {
            Cursor c = db.rawQuery("SELECT* FROM Base_User WHERE RealName = ?", new String[]{s});
            Map<String, Object> listItem;
            int i = 0;
            while (c.moveToNext()) {
                listItem = new HashMap<>();
                listItem.put("RealName", s);
                listItem.put("Code", c.getString(c.getColumnIndex("Code")));
                listItem.put("RealName" + "Code", c.getString(c.getColumnIndex("RealName")) + c.getString(c.getColumnIndex("Code")));
                listItems.add(listItem);
                i++;
            }
            if (i > 1) {
                SimpleAdapter simpleAdapter = new SimpleAdapter(LoginActivity.this, listItems,
                        R.layout.login_spinner_item,
                        new String[]{"RealName" + "Code"},
                        new int[]{R.id.text});
                userId.setAdapter(simpleAdapter);
                code = listItems.get(0).get("Code").toString();

                userId.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        getaBoolean = false;

//                        Log.d("list数据",listItems.toString());
                        userId.setText(listItems.get(position).get("RealName").toString());
//                        code = listItems.get(position).get("Code").toString();
                        code = listItems.get(position).get("Code").toString();


                    }
                });
//                userId.setActivated(false);
                getaBoolean = false;
            } else if(getaBoolean){
                c.moveToFirst();
                try {
                    code = c.getString(c.getColumnIndex("Code"));
                    getaBoolean = false;
                } catch (Exception e){
                    Log.e("补全数据库报错", e.toString(),e);
                }
            }
            c.close();

        } catch (Exception e) {
            Log.e("补全数据库报错", e.toString(),e);
        }
    }

    /**
     * 联网登录
     */
    protected void httpLogin() {
        // 初始化，只需要调用一次
        AssetsDatabaseManager.initManager(getApplication());
        // 获取管理对象，因为数据库需要通过管理对象才能够获取
        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
        // 通过管理对象获取数据库
        final SQLiteDatabase db = mg.getDatabase("users.db");
        //加载页添加
        if (progressDialog == null) {
            progressDialog = CustomProgressDialog.createDialog(LoginActivity.this);
        }
        progressDialog.show();
        progressDialog.setTitile("获取用户信息");
        //添加参数
        HashMap<String, String> properties = new HashMap<>();
        properties.put("Xml", "<Request><data  code='select-Login'><no><Code>" + code +
                "</Code><Password>" + string2MD5(userPassword.getText().toString()) + "</Password></no></data></Request> ");
        properties.put("Token", "");
        Log.d("登录数据", properties.toString() + WEB_SERVER_URL);
        WebServiceUtils.callWebService(WEB_SERVER_URL, "DynamicInvoke", properties, new WebServiceUtils.WebServiceCallBack() {
            @Override
            public void callBack(SoapObject result) {
                if (result != null) {
                    Log.d("登录返回数据", result.toString());
                    try {
                        Object detail = result.getProperty("DynamicInvokeResult");
                        JSONObject jsonObj;
                        JSONArray array;
                        JSONObject obj = null;
                        jsonObj = XML.toJSONObject(detail.toString());
//                       Log.d("秘钥",jsonObj.getJSONObject("DocumentElement").getJSONObject("Table").getString("secretkey"));
                        if (jsonObj.has("DocumentElement")) {
                            if ( jsonObj.toString().substring(jsonObj.toString().length() - 3, jsonObj.toString().length() - 2).equals("}")) {
                                obj = jsonObj.getJSONObject("DocumentElement").getJSONObject("Table");
                            }else if ( jsonObj.toString().substring(jsonObj.toString().length() - 3, jsonObj.toString().length() - 2).equals("]")){
                                array = jsonObj.getJSONObject("DocumentElement").getJSONArray("Table");
                                obj = array.getJSONObject(0);
                            }
                            SharedPreferencesUtil.saveBooleanData(LoginActivity.this, "Login", true);
                            SharedPreferencesUtil.saveStringData(LoginActivity.this, "Account", userId.getText().toString());
                            SharedPreferencesUtil.saveStringData(LoginActivity.this, "userPassword", userPassword.getText().toString());
                            SharedPreferencesUtil.saveStringData(LoginActivity.this, "userId", obj.getString("userid"));
                            String[] strings = obj.getString("departmentcode").split("-");
                            SharedPreferencesUtil.saveStringData(LoginActivity.this, "CompanyId", strings[0]);
                            SharedPreferencesUtil.saveStringData(LoginActivity.this, "DepartmentId", obj.getString("departmentcode"));
                            SharedPreferencesUtil.saveStringData(LoginActivity.this, "Code", obj.getString("code"));
                            SharedPreferencesUtil.saveStringData(LoginActivity.this, "Mobile", obj.getString("mobile"));
                            if (!aBoolean) {
                                SharedPreferencesUtil.saveStringData(LoginActivity.this, "Account", "");
                                SharedPreferencesUtil.saveStringData(LoginActivity.this, "userPassword", "");
                                SharedPreferencesUtil.saveStringData(LoginActivity.this, "Code", "");
                            }
                            String authstartdate = "";
                            String authenddate = "";
                            String workingdate = "";
                            String age = "0";
                            String education = "";
                            String idcard = "";
                            String departmentid = "";
                            String gender = "男";
                            String mobile = "";
                            String workingyear = "0";
                            String maritalstatus = "";
                            String physiclalstatus = "";
//                            JSONObject obj = jsonObj.getJSONObject("DocumentElement").getJSONObject("Table");
                            //新添职位
                            if (obj.has("headship")) {

                                SharedPreferencesUtil.saveStringData(LoginActivity.this, "Headship", obj.getString("headship"));
                            } else {
                                SharedPreferencesUtil.saveStringData(LoginActivity.this, "Headship", "");
                            }
//                            Log.d("职位数据",SharedPreferencesUtil.getStringData(LoginActivity.this, "Headship","1")+"0");
                            if (obj.has("authstartdate")) {
                                authstartdate = obj.getString("authstartdate");
                            }
                            if (obj.has("authenddate")) {
                                authenddate = obj.getString("authenddate");
                            }
                            if (obj.has("workingdate")) {
                                workingdate = obj.getString("workingdate");
                            }
                            if (obj.has("age")) {
                                age = obj.getString("age");
                            }
                            if (obj.has("education")) {
                                education = obj.getString("education");
                            }
                            if (obj.has("idcard")) {
                                idcard = obj.getString("idcard");
                            }
                            if (obj.has("departmentcode")) {
                                departmentid = obj.getString("departmentcode");
                            }
                            if (obj.has("gender")) {
                                gender = obj.getString("gender");
                            }
                            if (obj.has("mobile")) {
                                mobile = obj.getString("mobile");
                            }
                            if (obj.has("workingyear")) {
                                workingyear = obj.getString("workingyear");
                            }
                            if (obj.has("maritalstatus")) {
                                maritalstatus = obj.getString("maritalstatus");
                            }
                            if (obj.has("physiclalstatus")) {
                                physiclalstatus = obj.getString("physiclalstatus");
                            }
                            ContentValues values = new ContentValues();
                            values.put("UserId", obj.getString("userid"));
                            if (departmentid.length() > 6) {
                                strings = departmentid.split("-");
                                values.put("CompanyId", strings[0]);
                            } else {
                                values.put("CompanyId", departmentid);
                            }
                            values.put("DepartmentId", departmentid);
                            if (obj.has("code")) {

                                values.put("Code", obj.getString("code"));
                            }
                            if (obj.has("password")) {

                                values.put("Password", obj.getString("password"));
                            }
                            if (obj.has("realname")) {

                                values.put("RealName", obj.getString("realname"));
                            }
                            values.put("Gender", gender);
                            values.put("Mobile", mobile);
                            values.put("AuthStartDate", authstartdate);
                            SharedPreferencesUtil.saveStringData(LoginActivity.this, "AuthStartDate", authstartdate.substring(0,10));

                            values.put("AuthEndDate", authenddate);
                            SharedPreferencesUtil.saveStringData(LoginActivity.this, "AuthEndDate", authenddate.substring(0,10));

                            values.put("ModifyIndex", "0");
                            if (obj.has("validflag")) {

                                values.put("ValidFlag", obj.getString("validflag"));
                            }
                            values.put("IDCard", idcard);
                            values.put("Age", age);
                            values.put("Education", education);
                            values.put("WorkingYear", workingyear);
                            values.put("WorkingDate", workingdate);
                            values.put("MaritalStatus", maritalstatus);
                            values.put("PhysiclalStatus", physiclalstatus);

                            if(departmentid.length()>0&&values.get("Code").toString().length()>0&&
                                    values.get("RealName").toString().length()>0&&
                                    values.get("Password").toString().length()>0&&
                                    authstartdate.length()>0&&
                                    authenddate.length()>0){
                                SharedPreferencesUtil.saveStringData(LoginActivity.this, "AuthStartDate", authstartdate.substring(0,10));
                                SharedPreferencesUtil.saveStringData(LoginActivity.this, "AuthEndDate", authenddate.substring(0,10));

                                db.replace("Base_User", null, values);
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'+08:00'", Locale.CHINA);//小写的mm表示的是分钟
                                Date date = new Date(System.currentTimeMillis());
                                Date date1 = sdf.parse(authenddate);
                                int i = getGapCount(date, date1);
                                if (i < 90 && i > 1 && aBoolean1) {
                                    dialogDoneDelete(i);
                                    aBoolean1 = false;
                                    //关闭加载页
                                    if (progressDialog != null) {
                                        progressDialog.dismiss();
                                        progressDialog = null;
                                    }
                                } else if (i < 1) {
                                    //关闭加载页
                                    if (progressDialog != null) {
                                        progressDialog.dismiss();
                                        progressDialog = null;
                                    }
                                    dialogDone();
                                } else {
                                    if (obj.has("validflag") && obj.getString("validflag").equals("0")) {
                                        upDate();
                                    } else {
                                        Toast.makeText(LoginActivity.this, "无效用户", Toast.LENGTH_SHORT).show();

                                    }

                                }
                            }else {
                                Toast.makeText(LoginActivity.this, "无效用户", Toast.LENGTH_SHORT).show();

                            }



                        } else {
                            //关闭加载页
                            if (progressDialog != null) {
                                progressDialog.dismiss();
                                progressDialog = null;
                            }
                            if (ePasswordNum >= 9) {
                                Toast.makeText(LoginActivity.this, "已清空数据库，请重新登陆并下载数据", Toast.LENGTH_SHORT).show();
                                db.execSQL("DELETE FROM Base_User");
                                String pathdir = Environment.getExternalStorageDirectory().getPath() + "/LanDong";
                                File dir = new File(pathdir);
                                deleteDirWihtFile(dir);
                            } else {
                                ePasswordNum++;
                                Toast.makeText(LoginActivity.this, "用户名密码错误，您还有" + (10 - ePasswordNum) + "次机会", Toast.LENGTH_SHORT).show();
                            }
                        }

                    } catch (JSONException e) {
                        //关闭加载页
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                            progressDialog = null;
                        }
                        Toast.makeText(LoginActivity.this, "提交失败，信息缺失", Toast.LENGTH_SHORT).show();
                        Log.e("JSON exception", e.getMessage(), e);
                        e.printStackTrace();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } else {
                    //关闭加载页
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                        progressDialog = null;
                    }
                    Toast.makeText(LoginActivity.this, "网络未响应，提交失败", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    /**
     * 联网重名补全
     */
    protected void httpUserIdFind(final String s) {
        //添加参数
        HashMap<String, String> properties = new HashMap<>();
        properties.put("Xml", "<Request><data  code='select-LoginbyName'><no><RealName>" +
                s + "</RealName></no></data></Request> ");
        properties.put("Token", "");
        Log.d("重名补全发送数据",properties.toString());
        //通过工具类调用WebService接口
        WebServiceUtils.callWebService(WEB_SERVER_URL, "DynamicInvoke", properties, new WebServiceUtils.WebServiceCallBack() {
            @Override
            public void callBack(SoapObject result) {
                if (result != null) {

                    try {
                        Object detail = result.getProperty("DynamicInvokeResult");
                        JSONObject jsonObj;
                        Log.d("结果数据",""+detail);
                        jsonObj = XML.toJSONObject(detail.toString());
                        JSONArray array;
                        JSONObject obj;
                        Log.d("重名补全接收数据",jsonObj.toString());
                        if (jsonObj.has("DocumentElement") && jsonObj.toString().substring(jsonObj.toString().length() - 3, jsonObj.toString().length() - 2).equals("}")) {
                            obj = jsonObj.getJSONObject("DocumentElement").getJSONObject("Table");
                            code = obj.getString("code");
                        } else if (jsonObj.has("DocumentElement") && jsonObj.toString().substring(jsonObj.toString().length() - 3, jsonObj.toString().length() - 2).equals("]")) {
                            array = jsonObj.getJSONObject("DocumentElement").getJSONArray("Table");
                            listItems = new ArrayList<>();
                            for (int i = 0; i < array.length(); i++) {
                                obj = array.getJSONObject(i);
                                Map<String, Object> listItem = new HashMap<>();
                                if (obj.has("code")) {

                                    listItem.put("Code", obj.getString("code"));
                                    listItem.put("RealName" + "Code", s + obj.getString("code"));
                                }
                                listItem.put("RealName", s);
                                listItems.add(listItem);
                            }
                            SimpleAdapter simpleAdapter = new SimpleAdapter(LoginActivity.this, listItems,
                                    R.layout.login_spinner_item,
                                    new String[]{"RealName" + "Code"},
                                    new int[]{R.id.text});
                            userId.setAdapter(simpleAdapter);
                            code = listItems.get(0).get("Code").toString();
                            Log.d("重名补全Code数据", listItems.toString());
                            userId.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    getaBoolean = false;

                                    userId.setText(listItems.get(position).get("RealName").toString());
                                    code = listItems.get(position).get("Code").toString();
                                    listItems = new ArrayList<>();
                                }
                            });
                            getaBoolean = false;
                            userId.setText(listItems.get(0).get("RealName").toString());

                        }
                    } catch (JSONException e) {
                        Log.e("JSON exception", e.getMessage());
                        e.printStackTrace();
                    }
                }

            }
        });


    }

    /**
     * 更新数据库
     */
    protected void upDate() {
        upDateBusiness();
        // TODO: 2018/7/31 wangfuchun测试
        upDataLaw();
    }

    /**
     * 更新自由裁量数据库
     */
    protected void upDateCheckDetailed() {

        // 初始化，只需要调用一次
        AssetsDatabaseManager.initManager(getApplication());
        // 获取管理对象，因为数据库需要通过管理对象才能够获取
        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
        // 通过管理对象获取数据库
        final SQLiteDatabase db = mg.getDatabase("users.db");
        /**
         * 添加数据库字段列
         * */
        try {
            db.execSQL("ALTER TABLE cm_jcxm ADD COLUMN ValidFlag");//添加自由裁量库一级分类有效标
            db.execSQL("ALTER TABLE cm_jcnr ADD COLUMN ValidFlag");//添加自由裁量库二级分类有效标
            db.execSQL("ALTER TABLE cm_chkitem ADD COLUMN ValidFlag");//添加自由裁量库内容有效标
            db.execSQL("ALTER TABLE cm_jcxm ADD COLUMN ModifyIndex");//添加自由裁量库一级分类更新标
            db.execSQL("ALTER TABLE cm_jcnr ADD COLUMN ModifyIndex");//添加自由裁量库二级分类更新标
            db.execSQL("ALTER TABLE cm_chkitem ADD COLUMN ModifyIndex");//添加自由裁量库内容更新标
        } catch (Exception e) {
            Log.e("数据库列表已存在", e.toString(),e);
        }
        //添加参数
        HashMap<String, String> properties = new HashMap<>();
        properties.put("Xml", "<Request><data  code='select-CheckItem'><no></no></data></Request>");
        properties.put("Token", "");
        Log.d("发出的数据", properties.toString());
        WebServiceUtils.callWebService(WEB_SERVER_URL, "DynamicInvoke", properties, new WebServiceUtils.WebServiceCallBack() {

            //WebService接口返回的数据回调到这个方法中
            @Override
            public void callBack(SoapObject result) {
                if (result != null) {
                    Log.d("登录返回数据", result.toString());
                    try {

                        Object detail = result.getProperty("DynamicInvokeResult");
                        JSONObject jsonObj;
                        jsonObj = XML.toJSONObject(detail.toString());
                        JSONArray array;
                        JSONObject obj;

                        if (jsonObj.has("DocumentElement") && jsonObj.toString().substring(jsonObj.toString().length() - 3, jsonObj.toString().length() - 2).equals("}")) {
                            obj = jsonObj.getJSONObject("DocumentElement").getJSONObject("Table");
                            ContentValues values = new ContentValues();
                            if (obj.has("checkitemid")) {
                                values.put("id", obj.getString("checkitemid"));
                            }
                            if (obj.has("checkitemname")) {
                                values.put("jcxm", obj.getString("checkitemname"));
                            }
                            if (obj.has("checklistid")) {
                                values.put("tblid", obj.getString("checklistid"));
                            }
                            values.put("orderno", "0");
                            db.replace("cm_jcxm", null, values);

                        } else if (jsonObj.has("DocumentElement") && jsonObj.toString().substring(jsonObj.toString().length() - 3, jsonObj.toString().length() - 2).equals("]")) {
                            array = jsonObj.getJSONObject("DocumentElement").getJSONArray("Table");
                            for (int i = 0; i < array.length(); i++) {
                                obj = array.getJSONObject(i);
                                ContentValues values = new ContentValues();
                                if (obj.has("checkitemid")) {
                                    values.put("id", obj.getString("checkitemid"));
                                }
                                if (obj.has("checkitemname")) {
                                    values.put("jcxm", obj.getString("checkitemname"));
                                }
                                if (obj.has("checklistid")) {
                                    values.put("tblid", obj.getString("checklistid"));
                                }
                                values.put("orderno", "" + i);
                                db.replace("cm_jcxm", null, values);

                            }


                        }
                    } catch (JSONException e) {
                        //关闭加载页
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                            progressDialog = null;
                        }
                        Toast.makeText(LoginActivity.this, "提交失败,数据错误缺失", Toast.LENGTH_SHORT).show();
                        Log.e("JSON exception", e.getMessage());
                        e.printStackTrace();
                    }
                } else {
                    //关闭加载页
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                        progressDialog = null;
                    }
                    Toast.makeText(LoginActivity.this, "网络未响应，提交失败", Toast.LENGTH_SHORT).show();
                }
            }
        });

        properties = new HashMap<>();
        properties.put("Xml", "<Request><data  code='select-Checkdetailed'><no></no></data></Request>");
        properties.put("Token", "");
        WebServiceUtils.callWebService(WEB_SERVER_URL, "DynamicInvoke", properties, new WebServiceUtils.WebServiceCallBack() {

            //WebService接口返回的数据回调到这个方法中
            @Override
            public void callBack(SoapObject result) {
                if (result != null) {
                    Log.d("登录返回数据", result.toString());
                    try {

                        Object detail = result.getProperty("DynamicInvokeResult");
                        JSONObject jsonObj;
                        jsonObj = XML.toJSONObject(detail.toString());
                        JSONArray array;
                        JSONObject obj;

                        if (jsonObj.has("DocumentElement") && jsonObj.toString().substring(jsonObj.toString().length() - 3, jsonObj.toString().length() - 2).equals("}")) {
                            obj = jsonObj.getJSONObject("DocumentElement").getJSONObject("Table");
                            String[] strings = {""};
                            if (obj.has("checkcontent")) {
                                strings = obj.getString("checkcontent").split("条");
                            }
                            String s = "";
                            for (int y = 1; y < strings.length; y++) {
                                s = s + strings[y];
                            }
                            ContentValues values = new ContentValues();
                            if (obj.has("checkdetailedid")) {

                                values.put("id", obj.getString("checkdetailedid"));
                            }
                            values.put("jcxm", "");
                            values.put("jcnr", "");
                            if (obj.has("require")) {

                                values.put("jcbz", obj.getString("require"));
                            }
                            if (obj.has("method")) {

                                values.put("jcfs", obj.getString("method"));
                            }
                            values.put("wfxw", s);
                            if (obj.has("law")) {

                                values.put("wftk", obj.getString("law"));
                            }
                            if (obj.has("law")) {

                                values.put("tknr", obj.getString("law"));
                            }
                            if (obj.has("law")) {

                                values.put("cfyj", obj.getString("law"));
                            }
                            if (obj.has("penaltyange")) {

                                values.put("yjnr", obj.getString("penaltyange"));
                            }
                            values.put("jytk", "");
                            values.put("cfzlhfd", "");
                            values.put("xcclfs", "");
                            if (obj.has("checkitemid")) {

                                values.put("jcxmid", obj.getString("checkitemid"));
                            }
                            if (obj.has("checkdetailedid")) {

                                values.put("jcnrid", obj.getString("checkdetailedid"));
                            }
                            if (obj.has("checkdetailedid")) {

                                values.put("jcbzid", obj.getString("checkdetailedid"));
                            }
                            values.put("jcfsid", "0");
                            values.put("tblid", "10");
                            values.put("cftp", "");
                            db.replace("cm_chkitem", null, values);
                            values = new ContentValues();
                            if (obj.has("checkdetailedid")) {

                                values.put("id", obj.getString("checkdetailedid"));
                            }

                            values.put("jcnr", s);
                            values.put("tblid", "10");
                            values.put("orderno", "0");
                            if (obj.has("checkitemid")) {

                                values.put("jcxmid", obj.getString("checkitemid"));
                            }
                            db.replace("cm_jcnr", null, values);
                        } else if (jsonObj.has("DocumentElement") && jsonObj.toString().substring(jsonObj.toString().length() - 3, jsonObj.toString().length() - 2).equals("]")) {
                            array = jsonObj.getJSONObject("DocumentElement").getJSONArray("Table");
                            for (int i = 0; i < array.length(); i++) {
                                obj = array.getJSONObject(i);
//
//                                String[] strings = obj.getString("checkcontent").split("条");
                                String[] strings = {""};
                                if (obj.has("checkcontent")) {
                                    strings = obj.getString("checkcontent").split("条");
                                }
                                String s = "";
                                for (int y = 1; y < strings.length; y++) {
                                    s = s + strings[y];
                                }
                                ContentValues values = new ContentValues();
//                                values.put("id", obj.getString("checkdetailedid"));
//                                values.put("jcxm", "");
//                                values.put("jcnr", "");
//                                values.put("jcbz", obj.getString("require"));
//                                values.put("jcfs", obj.getString("method"));
//                                values.put("wfxw", s);
//                                values.put("wftk", obj.getString("law"));
//                                values.put("tknr", obj.getString("law"));
//                                values.put("cfyj", obj.getString("law"));
//                                values.put("yjnr", obj.getString("penaltyange"));
//                                values.put("jytk", "");
//                                values.put("cfzlhfd", "");
//                                values.put("xcclfs", "");
//                                values.put("jcxmid", obj.getString("checkitemid"));
//                                values.put("jcnrid", obj.getString("checkdetailedid"));
//                                values.put("jcbzid", obj.getString("checkdetailedid"));
//                                values.put("jcfsid", "0");
//                                values.put("tblid", "10");
//                                values.put("cftp", "");
//                                db.replace("cm_chkitem", null, values);
//                                values = new ContentValues();
//                                values.put("id", obj.getString("checkdetailedid"));
//                                values.put("jcnr", s);
//                                values.put("tblid", "10");
//                                values.put("orderno", "" + i);
//                                values.put("jcxmid", obj.getString("checkitemid"));
                                if (obj.has("checkdetailedid")) {

                                    values.put("id", obj.getString("checkdetailedid"));
                                }
                                values.put("jcxm", "");
                                values.put("jcnr", "");
                                if (obj.has("require")) {

                                    values.put("jcbz", obj.getString("require"));
                                }
                                if (obj.has("method")) {

                                    values.put("jcfs", obj.getString("method"));
                                }
                                values.put("wfxw", s);
                                if (obj.has("law")) {

                                    values.put("wftk", obj.getString("law"));
                                }
                                if (obj.has("law")) {

                                    values.put("tknr", obj.getString("law"));
                                }
                                if (obj.has("law")) {

                                    values.put("cfyj", obj.getString("law"));
                                }
                                if (obj.has("penaltyange")) {

                                    values.put("yjnr", obj.getString("penaltyange"));
                                }
                                values.put("jytk", "");
                                values.put("cfzlhfd", "");
                                values.put("xcclfs", "");
                                if (obj.has("checkitemid")) {

                                    values.put("jcxmid", obj.getString("checkitemid"));
                                }
                                if (obj.has("checkdetailedid")) {

                                    values.put("jcnrid", obj.getString("checkdetailedid"));
                                }
                                if (obj.has("checkdetailedid")) {

                                    values.put("jcbzid", obj.getString("checkdetailedid"));
                                }
                                values.put("jcfsid", "0");
                                values.put("tblid", "10");
                                values.put("cftp", "");
                                db.replace("cm_chkitem", null, values);
                                values = new ContentValues();
                                if (obj.has("checkdetailedid")) {

                                    values.put("id", obj.getString("checkdetailedid"));
                                }

                                values.put("jcnr", s);
                                values.put("tblid", "10");
                                values.put("orderno", "0");
                                if (obj.has("checkitemid")) {

                                    values.put("jcxmid", obj.getString("checkitemid"));
                                }
                                db.replace("cm_jcnr", null, values);
                            }


                        }

                    } catch (JSONException e) {
                        //关闭加载页
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                            progressDialog = null;
                        }
                        Toast.makeText(LoginActivity.this, "提交失败,数据错误缺失", Toast.LENGTH_SHORT).show();
                        Log.e("JSON exception", e.getMessage());
                        e.printStackTrace();
                    }
                    upDataLaw();
                } else {
                    //关闭加载页
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                        progressDialog = null;
                    }
                    Toast.makeText(LoginActivity.this, "网络未响应，提交失败", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    /**
     * 更新法律法规
     * valitFlag 1  代表数据是删除的； 0表示没变
     */
    protected void upDataLaw() {
        // 初始化，只需要调用一次
        AssetsDatabaseManager.initManager(getApplication());
        // 获取管理对象，因为数据库需要通过管理对象才能够获取
        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
        // 通过管理对象获取数据库
        final SQLiteDatabase db = mg.getDatabase("users.db");
        Cursor c = db.rawQuery("SELECT* FROM ELL_Law", null);
        int ModifyIndex = 0;//区别有没有新数据
        while (c.moveToNext()) {
//                Log.d("更新标数据",ModifyIndex+"");
            try {
                String modifyIndex = c.getString(c.getColumnIndex("ModifyIndex"));
                if (ModifyIndex < Integer.parseInt(modifyIndex)) {
                    ModifyIndex = Integer.parseInt(modifyIndex);
                }
            } catch (Exception e) {
                Log.e(TAG+"获取更新标数据库报错", e.toString());
            }
        }
        Log.e(TAG+"-upDataLaw","ModifyIndex为"+ModifyIndex);
        //添加参数
        HashMap<String, String> properties = new HashMap<>();
        properties.put("Xml", "<Request><data  code='select-Law'><no><modifyIndex>"+ModifyIndex+"</modifyIndex></no></data></Request>");
        properties.put("Token", "");
        Log.d(TAG+"-upDataLaw", "发出的数据为"+properties.toString());
        WebServiceUtils.callWebService(WEB_SERVER_URL, "DynamicInvoke", properties, new WebServiceUtils.WebServiceCallBack() {

            //WebService接口返回的数据回调到这个方法中
            @Override
            public void callBack(SoapObject result) {
                if (result != null) {
                    Log.e(TAG+"-upDataLaw", "返回数据为"+result.toString());
                    try {
                        Object detail = result.getProperty("DynamicInvokeResult");
                        JSONObject jsonObj;
                        jsonObj = XML.toJSONObject(detail.toString());
                        JSONArray array;
                        JSONObject obj;

                        if (jsonObj.has("DocumentElement") && jsonObj.toString().substring(jsonObj.toString().length() - 3, jsonObj.toString().length() - 2).equals("}")) {
                            obj = jsonObj.getJSONObject("DocumentElement").getJSONObject("Table");
                            ContentValues values = new ContentValues();
                            if (obj.has("lawid")) {
                                values.put("LawId", obj.getString("lawid"));
                            }
                            if(obj.has("lawname")){
                                values.put("LawName",obj.getString("lawname"));
                            }
                            if(obj.has("lawtitle")){
                                values.put("LawTitle",obj.getString("lawtitle"));
                            }
                            if(obj.has("addtime")){
                                values.put("AddTime",obj.getString("addtime"));
                            }
                            if(obj.has("lawcontent")){
                                values.put("LawContent",obj.getString("lawcontent"));
                            }
                            if(obj.has("modifyindex")){
                                values.put("ModifyIndex",obj.getString("modifyindex"));
                            }
                            if(obj.has("validflag")){
                                values.put("ValidFlag",obj.getString("validflag"));
                            }
                            db.replace("ELL_Law", null, values);

                            if (obj.has("validflag") && obj.has("lawid") && obj.getString("validflag").equals("1")) {
                                String[] args = {String.valueOf(obj.getString("lawid"))};
                                db.delete("ELL_Law", "LawId=?", args);
                            }

                        } else if (jsonObj.has("DocumentElement") && jsonObj.toString().substring(jsonObj.toString().length() - 3, jsonObj.toString().length() - 2).equals("]")) {
                            array = jsonObj.getJSONObject("DocumentElement").getJSONArray("Table");
                            for (int i = 0; i < array.length(); i++) {
                                obj = array.getJSONObject(i);
                                ContentValues values = new ContentValues();
                                if (obj.has("lawid")) {
                                    values.put("LawId", obj.getString("lawid"));
                                }
                                if(obj.has("lawname")){
                                    values.put("LawName",obj.getString("lawname"));
                                }
                                if(obj.has("lawtitle")){
                                    values.put("LawTitle",obj.getString("lawtitle"));
                                }
                                if(obj.has("addtime")){
                                    values.put("AddTime",obj.getString("addtime"));
                                }
                                if(obj.has("lawcontent")){
                                    values.put("LawContent",obj.getString("lawcontent"));
                                }
                                if(obj.has("modifyindex")){
                                    values.put("ModifyIndex",obj.getString("modifyindex"));
                                }
                                if(obj.has("validflag")){
                                    values.put("ValidFlag",obj.getString("validflag"));
                                }
                                db.replace("ELL_Law", null, values);

                                if (obj.has("validflag") && obj.has("lawid") && obj.getString("validflag").equals("1")) {
                                    String[] args = {String.valueOf(obj.getString("lawid"))};
                                    db.delete("ELL_Law", "LawId=?", args);
                                }
                            }

                        }
                        upDateBusiness();
                    } catch (JSONException e) {
                        //关闭加载页
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                            progressDialog = null;
                        }
                        Toast.makeText(LoginActivity.this, "提交失败,数据错误缺失", Toast.LENGTH_SHORT).show();
                        Log.e("JSON exception", e.getMessage());
                        e.printStackTrace();
                    }
                } else {
                    //关闭加载页
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                        progressDialog = null;
                    }
                    Toast.makeText(LoginActivity.this, "网络未响应，提交失败", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    /**
     * 更新企业信息
     */
    protected void upDateBusiness() {
        progressDialog.setTitile("获取各类企业信息");
        // 初始化，只需要调用一次
        AssetsDatabaseManager.initManager(getApplication());
        // 获取管理对象，因为数据库需要通过管理对象才能够获取
        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
        // 通过管理对象获取数据库
        final SQLiteDatabase db = mg.getDatabase("users.db");
        Cursor c = db.rawQuery("SELECT* FROM ELL_Business", null);
        try {
            db.execSQL("ALTER TABLE ELL_Business ADD COLUMN BusinessType1");//添加企业类型1
        }catch (Exception e){
            Log.e("BusinessType1数据库列表已存在", e.toString(),e);
        }
        int ModifyIndex = 0;
        while (c.moveToNext()) {
//                Log.d("更新标数据",ModifyIndex+"");
            try {
                if (ModifyIndex < Integer.parseInt(c.getString(c.getColumnIndex("ModifyIndex")))) {
                    ModifyIndex = Integer.parseInt(c.getString(c.getColumnIndex("ModifyIndex")));
                }
            } catch (Exception e) {
                Log.e("获取更新标数据库报错", e.toString());
            }


        }

        c.close();
        //添加参数
        HashMap<String, String> properties = new HashMap<>();
        properties.put("Xml", "<Request><data  code='update-Business-allEX'><no><ModifyIndex>" +
                ModifyIndex + "</ModifyIndex></no></data></Request>");
        properties.put("Token", "");
        WebServiceUtils.callWebService(WEB_SERVER_URL, "DynamicInvoke", properties, new WebServiceUtils.WebServiceCallBack() {

            //WebService接口返回的数据回调到这个方法中
            @Override
            public void callBack(SoapObject result) {
                if (result != null) {
//                    Log.d("非煤企业返回数据",result.toString());
                    try {

                        Object detail = result.getProperty("DynamicInvokeResult");
                        JSONObject jsonObj;
                        jsonObj = XML.toJSONObject(detail.toString());
                        JSONArray array;
                        JSONObject obj;

//                      Log.d("秘钥",jsonObj.getJSONObject("DocumentElement").getJSONObject("Table").getString("secretkey"));
                        if (jsonObj.has("DocumentElement") && jsonObj.toString().substring(jsonObj.toString().length() - 3, jsonObj.toString().length() - 2).equals("}")) {
                            obj = jsonObj.getJSONObject("DocumentElement").getJSONObject("Table");

                            ContentValues values = new ContentValues();
                            if (obj.has("businessid")) {
                                values.put("BusinessId", obj.getString("businessid"));
                            } else {
                                values.put("BusinessId", "");
                            }
                            if (obj.has("businessname")) {
                                values.put("BusinessName", obj.getString("businessname"));
                            } else {
                                values.put("BusinessName", "");
                            }
                            if (obj.has("businesstype")) {
                                values.put("BusinessType", obj.getString("businesstype"));
                            } else {
                                values.put("BusinessType", "");
                            }
                            if (obj.has("businesstype1")) {
                                values.put("BusinessType1", obj.getString("businesstype1"));
                            } else {
                                values.put("BusinessType1", "");
                            }
                            if (obj.has("legalperson")) {
                                values.put("LegalPerson", obj.getString("legalperson"));
                            } else {
                                values.put("LegalPerson", "");
                            }
                            if (obj.has("legalpersonpost")) {
                                values.put("LegalPersonPost", obj.getString("legalpersonpost"));
                            } else {
                                values.put("LegalPersonPost", "");
                            }
                            if (obj.has("legalpersonphone")) {
                                values.put("LegalPersonPhone", obj.getString("legalpersonphone"));
                            } else {
                                values.put("LegalPersonPhone", "");
                            }
//                            values.put("RegistrationNumber",obj.getString("registrationnumber"));
                            if (obj.has("safetyofficer")) {
                                values.put("SafetyOfficer", obj.getString("safetyofficer"));
                            } else {
                                values.put("SafetyOfficer", "");
                            }
                            if (obj.has("safetyofficerphone")) {
                                values.put("SafetyOfficerPhone", obj.getString("safetyofficerphone"));
                            } else {
                                values.put("SafetyOfficerPhone", "");
                            }
                            if (obj.has("address")) {
                                values.put("Address", obj.getString("address"));
                            } else {
                                values.put("Address", "");
                            }
                            values.put("Emphases", "1");
                            if (obj.has("modifyindex")) {
                                values.put("ModifyIndex", obj.getString("modifyindex"));
                            } else {
                                values.put("ModifyIndex", "");
                            }
                            if (obj.has("validflag")) {
                                values.put("ValidFlag", obj.getString("validflag"));
                            } else {
                                values.put("ValidFlag", "");
                            }
                            values.put("ModifyIndex2", "0");
                            if (obj.has("orgcode")) {
                                values.put("OrgCode", obj.getString("orgcode"));
                            } else {
                                values.put("OrgCode", "");
                            }
                            values.put("UserDefined", "0");
                            // TODO: 2018/9/29 判断是更新企业表还是集团表 
                            db.replace("ELL_Business", null, values);
                        } else if (jsonObj.has("DocumentElement") && jsonObj.toString().substring(jsonObj.toString().length() - 3, jsonObj.toString().length() - 2).equals("]")) {
                            array = jsonObj.getJSONObject("DocumentElement").getJSONArray("Table");
                            for (int i = 0; i < array.length(); i++) {
                                obj = array.getJSONObject(i);
                                ContentValues values = new ContentValues();
                                if (obj.has("businessid")) {
                                    values.put("BusinessId", obj.getString("businessid"));
                                } else {
                                    values.put("BusinessId", "");
                                }
                                if (obj.has("businessname")) {
                                    values.put("BusinessName", obj.getString("businessname"));
                                } else {
                                    values.put("BusinessName", "");
                                }
                                if (obj.has("businesstype")) {
                                    values.put("BusinessType", obj.getString("businesstype"));
                                } else {
                                    values.put("BusinessType", "");
                                }
                                if (obj.has("businesstype1")) {
                                    values.put("BusinessType1", obj.getString("businesstype1"));
                                } else {
                                    values.put("BusinessType1", "");
                                }
                                if (obj.has("legalperson")) {
                                    values.put("LegalPerson", obj.getString("legalperson"));
                                } else {
                                    values.put("LegalPerson", "");
                                }
                                if (obj.has("legalpersonpost")) {
                                    values.put("LegalPersonPost", obj.getString("legalpersonpost"));
                                } else {
                                    values.put("LegalPersonPost", "");
                                }
                                if (obj.has("legalpersonphone")) {
                                    values.put("LegalPersonPhone", obj.getString("legalpersonphone"));
                                } else {
                                    values.put("LegalPersonPhone", "");
                                }
                                if (obj.has("safetyofficer")) {
                                    values.put("SafetyOfficer", obj.getString("safetyofficer"));
                                } else {
                                    values.put("SafetyOfficer", "");
                                }
                                if (obj.has("safetyofficerphone")) {
                                    values.put("SafetyOfficerPhone", obj.getString("safetyofficerphone"));
                                } else {
                                    values.put("SafetyOfficerPhone", "");
                                }
                                if (obj.has("address")) {
                                    values.put("Address", obj.getString("address"));
                                } else {
                                    values.put("Address", "");
                                }
                                values.put("Emphases", "1");
                                if (obj.has("modifyindex")) {
                                    values.put("ModifyIndex", obj.getString("modifyindex"));
                                } else {
                                    values.put("ModifyIndex", "");
                                }
                                values.put("ModifyIndex2", "0");
                                if (obj.has("validflag")) {
                                    values.put("ValidFlag", obj.getString("validflag"));
                                } else {
                                    values.put("ValidFlag", "");
                                }
                                if (obj.has("orgcode")) {
                                    values.put("OrgCode", obj.getString("orgcode"));
                                } else {
                                    values.put("OrgCode", "");
                                }
                                values.put("UserDefined", "0");
                                // TODO: 2018/9/29 判断更新企业表还是集团表 
                                db.replace("ELL_Business", null, values);
                            }


                        }
                        upDataGroupBusiness();
                    } catch (JSONException e) {
                        //关闭加载页
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                            progressDialog = null;
                        }
                        Toast.makeText(LoginActivity.this, "提交失败,数据错误缺失", Toast.LENGTH_SHORT).show();
                        Log.e("报错-upDateBusiness", e.getMessage(), e);
                        e.printStackTrace();
                    }

                } else {
                    //关闭加载页
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                        progressDialog = null;
                    }
                    Toast.makeText(LoginActivity.this, "网络未响应，提交失败", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }
    /**
     * 更新集团信息
     */
    protected void upDataGroupBusiness() {
        progressDialog.setTitile("获取各类集团信息");
        // 初始化，只需要调用一次
        AssetsDatabaseManager.initManager(getApplication());
        // 获取管理对象，因为数据库需要通过管理对象才能够获取
        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
        // 通过管理对象获取数据库
        final SQLiteDatabase db = mg.getDatabase("users.db");
        Cursor c = db.rawQuery("SELECT* FROM ELL_Group", null);
        try {
            db.execSQL("ALTER TABLE ELL_Group ADD COLUMN BusinessType1");//添加企业类型1
        }catch (Exception e){
            Log.e("BusinessType1数据库列表已存在", e.toString(),e);
        }
        int ModifyIndex = 0;
        while (c.moveToNext()) {
//                Log.d("更新标数据",ModifyIndex+"");
            try {
                if (ModifyIndex < Integer.parseInt(c.getString(c.getColumnIndex("ModifyIndex")))) {
                    ModifyIndex = Integer.parseInt(c.getString(c.getColumnIndex("ModifyIndex")));
                }
            } catch (Exception e) {
                Log.e("获取更新标数据库报错", e.toString());
                ModifyIndex = 0;
            }


        }

        c.close();
        //添加参数
        HashMap<String, String> properties = new HashMap<>();
        properties.put("Xml", "<Request><data  code='update-Business-GroupBusiness'><no><ModifyIndex>" +
                ModifyIndex + "</ModifyIndex></no></data></Request>");
        properties.put("Token", "");
        Log.d("集团发送数据",properties.toString());
        WebServiceUtils.callWebService(WEB_SERVER_URL, "DynamicInvoke", properties, new WebServiceUtils.WebServiceCallBack() {

            //WebService接口返回的数据回调到这个方法中
            @Override
            public void callBack(SoapObject result) {
                if (result != null) {
                    Log.d("集团返回数据",result.toString());
                    try {

                        Object detail = result.getProperty("DynamicInvokeResult");
                        JSONObject jsonObj;
                        jsonObj = XML.toJSONObject(detail.toString());
                        JSONArray array;
                        JSONObject obj;

//                      Log.d("秘钥",jsonObj.getJSONObject("DocumentElement").getJSONObject("Table").getString("secretkey"));
                        if (jsonObj.has("DocumentElement") && jsonObj.toString().substring(jsonObj.toString().length() - 3, jsonObj.toString().length() - 2).equals("}")) {
                            obj = jsonObj.getJSONObject("DocumentElement").getJSONObject("Table");

                            ContentValues values = new ContentValues();
                            if (obj.has("businessid")) {
                                values.put("BusinessId", obj.getString("businessid"));
                            } else {
                                values.put("BusinessId", "");
                            }
                            if (obj.has("businessname")) {
                                values.put("BusinessName", obj.getString("businessname"));
                            } else {
                                values.put("BusinessName", "");
                            }
                            if (obj.has("businesstype")) {
                                values.put("BusinessType", obj.getString("businesstype"));
                            } else {
                                values.put("BusinessType", "");
                            }
                            if (obj.has("businesstype1")) {
                                values.put("BusinessType1", obj.getString("businesstype1"));
                            } else {
                                values.put("BusinessType1", "");
                            }
                            if (obj.has("legalperson")) {
                                values.put("LegalPerson", obj.getString("legalperson"));
                            } else {
                                values.put("LegalPerson", "");
                            }
                            if (obj.has("legalpersonpost")) {
                                values.put("LegalPersonPost", obj.getString("legalpersonpost"));
                            } else {
                                values.put("LegalPersonPost", "");
                            }
                            if (obj.has("legalpersonphone")) {
                                values.put("LegalPersonPhone", obj.getString("legalpersonphone"));
                            } else {
                                values.put("LegalPersonPhone", "");
                            }
//                            values.put("RegistrationNumber",obj.getString("registrationnumber"));
                            if (obj.has("safetyofficer")) {
                                values.put("SafetyOfficer", obj.getString("safetyofficer"));
                            } else {
                                values.put("SafetyOfficer", "");
                            }
                            if (obj.has("safetyofficerphone")) {
                                values.put("SafetyOfficerPhone", obj.getString("safetyofficerphone"));
                            } else {
                                values.put("SafetyOfficerPhone", "");
                            }
                            if (obj.has("address")) {
                                values.put("Address", obj.getString("address"));
                            } else {
                                values.put("Address", "");
                            }
                            values.put("Emphases", "1");
                            if (obj.has("modifyindex")) {
                                values.put("ModifyIndex", obj.getString("modifyindex"));
                            } else {
                                values.put("ModifyIndex", "");
                            }
                            if (obj.has("validflag")) {
                                values.put("ValidFlag", obj.getString("validflag"));
                            } else {
                                values.put("ValidFlag", "");
                            }
                            values.put("ModifyIndex2", "0");
                            if (obj.has("orgcode")) {
                                values.put("OrgCode", obj.getString("orgcode"));
                            } else {
                                values.put("OrgCode", "");
                            }
                            values.put("UserDefined", "0");
                            db.replace("ELL_Group", null, values);
                        } else if (jsonObj.has("DocumentElement") && jsonObj.toString().substring(jsonObj.toString().length() - 3, jsonObj.toString().length() - 2).equals("]")) {
                            array = jsonObj.getJSONObject("DocumentElement").getJSONArray("Table");
                            for (int i = 0; i < array.length(); i++) {
                                obj = array.getJSONObject(i);
                                ContentValues values = new ContentValues();
                                if (obj.has("businessid")) {
                                    values.put("BusinessId", obj.getString("businessid"));
                                } else {
                                    values.put("BusinessId", "");
                                }
                                if (obj.has("businessname")) {
                                    values.put("BusinessName", obj.getString("businessname"));
                                } else {
                                    values.put("BusinessName", "");
                                }
                                if (obj.has("businesstype")) {
                                    values.put("BusinessType", obj.getString("businesstype"));
                                } else {
                                    values.put("BusinessType", "");
                                }
                                if (obj.has("businesstype1")) {
                                    values.put("BusinessType1", obj.getString("businesstype1"));
                                } else {
                                    values.put("BusinessType1", "");
                                }
                                if (obj.has("legalperson")) {
                                    values.put("LegalPerson", obj.getString("legalperson"));
                                } else {
                                    values.put("LegalPerson", "");
                                }
                                if (obj.has("legalpersonpost")) {
                                    values.put("LegalPersonPost", obj.getString("legalpersonpost"));
                                } else {
                                    values.put("LegalPersonPost", "");
                                }
                                if (obj.has("legalpersonphone")) {
                                    values.put("LegalPersonPhone", obj.getString("legalpersonphone"));
                                } else {
                                    values.put("LegalPersonPhone", "");
                                }
                                if (obj.has("safetyofficer")) {
                                    values.put("SafetyOfficer", obj.getString("safetyofficer"));
                                } else {
                                    values.put("SafetyOfficer", "");
                                }
                                if (obj.has("safetyofficerphone")) {
                                    values.put("SafetyOfficerPhone", obj.getString("safetyofficerphone"));
                                } else {
                                    values.put("SafetyOfficerPhone", "");
                                }
                                if (obj.has("address")) {
                                    values.put("Address", obj.getString("address"));
                                } else {
                                    values.put("Address", "");
                                }
                                values.put("Emphases", "1");
                                if (obj.has("modifyindex")) {
                                    values.put("ModifyIndex", obj.getString("modifyindex"));
                                } else {
                                    values.put("ModifyIndex", "");
                                }
                                values.put("ModifyIndex2", "0");
                                if (obj.has("validflag")) {
                                    values.put("ValidFlag", obj.getString("validflag"));
                                } else {
                                    values.put("ValidFlag", "");
                                }
                                if (obj.has("orgcode")) {
                                    values.put("OrgCode", obj.getString("orgcode"));
                                } else {
                                    values.put("OrgCode", "");
                                }
                                values.put("UserDefined", "0");
                                db.replace("ELL_Group", null, values);
                            }


                        }
                        upDateCoalBusiness();
                    } catch (JSONException e) {
                        //关闭加载页
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                            progressDialog = null;
                        }
                        Toast.makeText(LoginActivity.this, "提交失败,数据错误缺失", Toast.LENGTH_SHORT).show();
                        Log.e("报错-upDateBusinessGroup", e.getMessage(), e);
                        e.printStackTrace();
                    }

                } else {
                    //关闭加载页
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                        progressDialog = null;
                    }
                    Toast.makeText(LoginActivity.this, "网络未响应，提交失败", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }
    /**
     * 更新煤矿企业信息
     */
    protected void upDateCoalBusiness() {
        // 初始化，只需要调用一次
        AssetsDatabaseManager.initManager(getApplication());
        // 获取管理对象，因为数据库需要通过管理对象才能够获取
        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
        // 通过管理对象获取数据库
        final SQLiteDatabase db = mg.getDatabase("users.db");
        Cursor c = db.rawQuery("SELECT* FROM ELL_Business", null);
        int ModifyIndex = 0;
        while (c.moveToNext()) {
//                Log.d("更新标数据",ModifyIndex+"");
            try {
                if (ModifyIndex < Integer.parseInt(c.getString(c.getColumnIndex("ModifyIndex2")))) {
                    ModifyIndex = Integer.parseInt(c.getString(c.getColumnIndex("ModifyIndex2")));
                }
            } catch (Exception e) {
                Log.e("煤矿获取更新标数据库报错", e.toString());
            }

        }
        //去除测试数据标
        if (!SharedPreferencesUtil.getBooleanData(LoginActivity.this, "validFlag", false)) {
            SharedPreferencesUtil.saveBooleanData(LoginActivity.this, "validFlag", true);
            ModifyIndex = 0;
            Log.e("煤矿获取更新标数据库报错", ModifyIndex + "");
        }
        c.close();
        //添加参数
        HashMap<String, String> properties = new HashMap<>();
        properties.put("Xml", "<Request><data  code='select-CoalBusinessEX'><no><ModifyIndex>" +
                ModifyIndex + "</ModifyIndex></no></data></Request>");
        properties.put("Token", "");
//        Log.d("煤矿发送数据",properties.toString());
        WebServiceUtils.callWebService(WEB_SERVER_URL, "DynamicInvoke", properties, new WebServiceUtils.WebServiceCallBack() {

            //WebService接口返回的数据回调到这个方法中
            @Override
            public void callBack(SoapObject result) {
                if (result != null) {
//                    Log.d("煤矿返回数据",result.toString());
                    try {

                        Object detail = result.getProperty("DynamicInvokeResult");
                        JSONObject jsonObj;
                        jsonObj = XML.toJSONObject(detail.toString());
                        JSONArray array;
                        JSONObject obj;

//                      Log.d("秘钥",jsonObj.getJSONObject("DocumentElement").getJSONObject("Table").getString("secretkey"));
                        if (jsonObj.has("DocumentElement") && jsonObj.toString().substring(jsonObj.toString().length() - 3, jsonObj.toString().length() - 2).equals("}")) {
                            obj = jsonObj.getJSONObject("DocumentElement").getJSONObject("Table");
                            ContentValues values = new ContentValues();
                            if (obj.has("businessid")) {
                                values.put("BusinessId", obj.getString("businessid"));
                            } else {
                                values.put("BusinessId", "");
                            }
                            if (obj.has("businessid")) {
                                values.put("OrgCode", obj.getString("businessid"));
                            } else {
                                values.put("OrgCode", "");
                            }
                            if (obj.has("businessname")) {
                                values.put("BusinessName", obj.getString("businessname"));
                            } else {
                                values.put("BusinessName", "");
                            }
                            if (obj.has("businesstype")) {
                                values.put("BusinessType", obj.getString("businesstype"));
                            } else {
                                values.put("BusinessType", "");
                            }
                            if (obj.has("businesstype1")) {
                                values.put("BusinessType1", obj.getString("businesstype1"));
                            } else {
                                values.put("BusinessType1", "");
                            }
                            if (obj.has("legalperson")) {
                                values.put("LegalPerson", obj.getString("legalperson"));
                            } else {
                                values.put("LegalPerson", "");
                            }
                            if (obj.has("legalpersonpost")) {
                                values.put("LegalPersonPost", obj.getString("legalpersonpost"));
                            } else {
                                values.put("LegalPersonPost", "");
                            }
                            if (obj.has("legalpersonphone")) {
                                values.put("LegalPersonPhone", obj.getString("legalpersonphone"));
                            } else {
                                values.put("LegalPersonPhone", "");
                            }
                            if (obj.has("businesslicense")) {
                                values.put("RegistrationNumber", obj.getString("businesslicense"));
                            } else {
                                values.put("RegistrationNumber", "");
                            }
                            if (obj.has("respperson")) {
                                values.put("SafetyOfficer", obj.getString("respperson"));
                            } else {
                                values.put("SafetyOfficer", "");
                            }
                            if (obj.has("resppersonphone")) {
                                values.put("SafetyOfficerPhone", obj.getString("resppersonphone"));
                            } else {
                                values.put("SafetyOfficerPhone", "");
                            }
                            if (obj.has("address")) {
                                if (obj.getString("address").equals("{\"xml:space\":\"preserve\"}")) {
                                    values.put("Address", "");
                                } else {
                                    values.put("Address", obj.getString("address"));
                                }
                            } else {
                                values.put("Address", "");
                            }

                            values.put("Emphases", "1");
                            values.put("ModifyIndex", "0");
                            if (obj.has("modifyindex")) {
                                values.put("ModifyIndex2", obj.getString("modifyindex"));
                            } else {
                                values.put("ModifyIndex2", "");
                            }
                            if (obj.has("validflag")) {
                                values.put("ValidFlag", obj.getString("validflag"));
                            } else {
                                values.put("ValidFlag", "");
                            }
                            values.put("UserDefined", "0");
                            if (obj.has("mineshaftcond")) {
                                values.put("MineShaftCond", obj.getString("mineshaftcond"));
                            } else {
                                values.put("MineShaftCond", "");
                            }
                            if (obj.has("mineshafttype")) {
                                values.put("MineShaftType", obj.getString("mineshafttype"));
                            } else {
                                values.put("MineShaftType", "");
                            }
                            if (obj.has("geologicalreserves")) {
                                values.put("GeologicalReserves", obj.getString("geologicalreserves"));
                            } else {
                                values.put("GeologicalReserves", "");
                            }
                            if (obj.has("workablereserves")) {
                                values.put("WorkableReserves", obj.getString("workablereserves"));
                            } else {
                                values.put("WorkableReserves", "");
                            }
                            if (obj.has("designprodcapacity")) {
                                values.put("DesignProdCapacity", obj.getString("designprodcapacity"));
                            } else {
                                values.put("DesignProdCapacity", "");
                            }
                            if (obj.has("checkprodcapacity")) {
                                values.put("CheckProdCapacity", obj.getString("checkprodcapacity"));
                            } else {
                                values.put("CheckProdCapacity", "");
                            }
                            if (obj.has("area")) {
                                values.put("Area", obj.getString("area"));
                            } else {
                                values.put("Area", "");
                            }
                            if (obj.has("wthdraw")) {
                                values.put("Wthdraw", obj.getString("wthdraw"));
                            } else {
                                values.put("Wthdraw", "");
                            }
                            if (obj.has("coaltype")) {
                                values.put("CoalType", obj.getString("coaltype"));
                            } else {
                                values.put("CoalType", "");
                            }
                            if (obj.has("workableseam")) {
                                values.put("WorkableSeam", obj.getString("workableseam"));
                            } else {
                                values.put("WorkableSeam", "");
                            }
                            if (obj.has("explorecraft")) {
                                values.put("ExploreCraft", obj.getString("explorecraft"));
                            } else {
                                values.put("ExploreCraft", "");
                            }
                            if (obj.has("contractphone")) {
                                values.put("ContractPhone", obj.getString("contractphone"));
                            } else {
                                values.put("ContractPhone", "");
                            }
                            if (obj.has("exploremode")) {
                                values.put("ExploreMode", obj.getString("exploremode"));
                            } else {
                                values.put("ExploreMode", "");
                            }
                            if (obj.has("riskappraisal")) {
                                values.put("RiskAppraisal", obj.getString("riskappraisal"));
                            } else {
                                values.put("RiskAppraisal", "");
                            }
                            if (obj.has("gaslevel")) {
                                values.put("GasLevel", obj.getString("gaslevel"));
                            } else {
                                values.put("GasLevel", "");
                            }
                            if (obj.has("coalseamlevel")) {
                                values.put("CoalSeamLevel", obj.getString("coalseamlevel"));
                            } else {
                                values.put("CoalSeamLevel", "");
                            }
                            if (obj.has("explosion")) {
                                values.put("Explosion", obj.getString("explosion"));
                            } else {
                                values.put("Explosion", "");
                            }
                            if (obj.has("ventilatemode")) {
                                values.put("VentilateMode", obj.getString("ventilatemode"));
                            } else {
                                values.put("VentilateMode", "");
                            }
                            db.replace("ELL_Business", null, values);
                        } else if (jsonObj.has("DocumentElement") && jsonObj.toString().substring(jsonObj.toString().length() - 3, jsonObj.toString().length() - 2).equals("]")) {
                            array = jsonObj.getJSONObject("DocumentElement").getJSONArray("Table");
                            for (int i = 0; i < array.length(); i++) {
                                obj = array.getJSONObject(i);
                                ContentValues values = new ContentValues();
                                if (obj.has("businessid")) {
                                    values.put("BusinessId", obj.getString("businessid"));
                                } else {
                                    values.put("BusinessId", "");
                                }
                                if (obj.has("businessname")) {
                                    values.put("BusinessName", obj.getString("businessname"));
                                } else {
                                    values.put("BusinessName", "");
                                }
                                if (obj.has("businesstype")) {
                                    values.put("BusinessType", obj.getString("businesstype"));
                                } else {
                                    values.put("BusinessType", "");
                                }
                                if (obj.has("businesstype1")) {
                                    values.put("BusinessType1", obj.getString("businesstype1"));
                                } else {
                                    values.put("BusinessType1", "");
                                }
                                if (obj.has("legalperson")) {
                                    values.put("LegalPerson", obj.getString("legalperson"));
                                } else {
                                    values.put("LegalPerson", "");
                                }
                                if (obj.has("legalpersonpost")) {
                                    values.put("LegalPersonPost", obj.getString("legalpersonpost"));
                                } else {
                                    values.put("LegalPersonPost", "");
                                }
                                if (obj.has("legalpersonphone")) {
                                    values.put("LegalPersonPhone", obj.getString("legalpersonphone"));
                                } else {
                                    values.put("LegalPersonPhone", "");
                                }
                                if (obj.has("businesslicense")) {
                                    values.put("RegistrationNumber", obj.getString("businesslicense"));
                                } else {
                                    values.put("RegistrationNumber", "");
                                }
                                if (obj.has("respperson")) {
                                    values.put("SafetyOfficer", obj.getString("respperson"));
                                } else {
                                    values.put("SafetyOfficer", "");
                                }
                                if (obj.has("resppersonphone")) {
                                    values.put("SafetyOfficerPhone", obj.getString("resppersonphone"));
                                } else {
                                    values.put("SafetyOfficerPhone", "");
                                }
                                if (obj.has("address")) {
                                    if (obj.getString("address").equals("{\"xml:space\":\"preserve\"}")) {
                                        values.put("Address", "");
                                    } else {
                                        values.put("Address", obj.getString("address"));
                                    }
                                } else {
                                    values.put("Address", "");
                                }

                                values.put("Emphases", "1");
                                values.put("ModifyIndex", "0");
                                if (obj.has("modifyindex")) {
                                    values.put("ModifyIndex2", obj.getString("modifyindex"));
                                } else {
                                    values.put("ModifyIndex2", "");
                                }
                                if (obj.has("validflag")) {
                                    values.put("ValidFlag", obj.getString("validflag"));
                                } else {
                                    values.put("ValidFlag", "");
                                }
                                values.put("UserDefined", "0");
                                if (obj.has("mineshaftcond")) {
                                    values.put("MineShaftCond", obj.getString("mineshaftcond"));
                                } else {
                                    values.put("MineShaftCond", "");
                                }
                                if (obj.has("mineshafttype")) {
                                    values.put("MineShaftType", obj.getString("mineshafttype"));
                                } else {
                                    values.put("MineShaftType", "");
                                }
                                if (obj.has("geologicalreserves")) {
                                    values.put("GeologicalReserves", obj.getString("geologicalreserves"));
                                } else {
                                    values.put("GeologicalReserves", "");
                                }
                                if (obj.has("workablereserves")) {
                                    values.put("WorkableReserves", obj.getString("workablereserves"));
                                } else {
                                    values.put("WorkableReserves", "");
                                }
                                if (obj.has("designprodcapacity")) {
                                    values.put("DesignProdCapacity", obj.getString("designprodcapacity"));
                                } else {
                                    values.put("DesignProdCapacity", "");
                                }
                                if (obj.has("checkprodcapacity")) {
                                    values.put("CheckProdCapacity", obj.getString("checkprodcapacity"));
                                } else {
                                    values.put("CheckProdCapacity", "");
                                }
                                if (obj.has("area")) {
                                    values.put("Area", obj.getString("area"));
                                } else {
                                    values.put("Area", "");
                                }
                                if (obj.has("wthdraw")) {
                                    values.put("Wthdraw", obj.getString("wthdraw"));
                                } else {
                                    values.put("Wthdraw", "");
                                }
                                if (obj.has("coaltype")) {
                                    values.put("CoalType", obj.getString("coaltype"));
                                } else {
                                    values.put("CoalType", "");
                                }
                                if (obj.has("workableseam")) {
                                    values.put("WorkableSeam", obj.getString("workableseam"));
                                } else {
                                    values.put("WorkableSeam", "");
                                }
                                if (obj.has("explorecraft")) {
                                    values.put("ExploreCraft", obj.getString("explorecraft"));
                                } else {
                                    values.put("ExploreCraft", "");
                                }
                                if (obj.has("contractphone")) {
                                    values.put("ContractPhone", obj.getString("contractphone"));
                                } else {
                                    values.put("ContractPhone", "");
                                }
                                if (obj.has("exploremode")) {
                                    values.put("ExploreMode", obj.getString("exploremode"));
                                } else {
                                    values.put("ExploreMode", "");
                                }
                                if (obj.has("riskappraisal")) {
                                    values.put("RiskAppraisal", obj.getString("riskappraisal"));
                                } else {
                                    values.put("RiskAppraisal", "");
                                }
                                if (obj.has("gaslevel")) {
                                    values.put("GasLevel", obj.getString("gaslevel"));
                                } else {
                                    values.put("GasLevel", "");
                                }
                                if (obj.has("coalseamlevel")) {
                                    values.put("CoalSeamLevel", obj.getString("coalseamlevel"));
                                } else {
                                    values.put("CoalSeamLevel", "");
                                }
                                if (obj.has("explosion")) {
                                    values.put("Explosion", obj.getString("explosion"));
                                } else {
                                    values.put("Explosion", "");
                                }
                                if (obj.has("ventilatemode")) {
                                    values.put("VentilateMode", obj.getString("ventilatemode"));
                                } else {
                                    values.put("VentilateMode", "");
                                }
                                db.replace("ELL_Business", null, values);
                            }

                        }
                        upDateCompany();
                    } catch (JSONException e) {
                        //关闭加载页
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                            progressDialog = null;
                        }
                        Toast.makeText(LoginActivity.this, "提交失败,数据错误缺失", Toast.LENGTH_SHORT).show();
                        Log.e("报错-upDateCoalBusiness", e.getMessage(), e);
                        e.printStackTrace();
                    }

                } else {
                    //关闭加载页
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                        progressDialog = null;
                    }
                    Toast.makeText(LoginActivity.this, "网络未响应，提交失败", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    /**
     * 更新机构信息
     */
    protected void upDateCompany() {
        progressDialog.setTitile("数据加载");
        //添加参数
        HashMap<String, String> properties = new HashMap<>();
        properties.put("Xml", "<Request><data  code='select-Company'><no></no></data></Request>");
        properties.put("Token", "");
        WebServiceUtils.callWebService(WEB_SERVER_URL, "DynamicInvoke", properties, new WebServiceUtils.WebServiceCallBack() {

            //WebService接口返回的数据回调到这个方法中
            @Override
            public void callBack(SoapObject result) {
                if (result != null) {
                    Log.e("登录返回数据", result.toString());
                    try {
                        Object detail = result.getProperty("DynamicInvokeResult");
                        JSONObject jsonObj;
                        jsonObj = XML.toJSONObject(detail.toString());
                        JSONArray array;
                        JSONObject obj;
                        // 初始化，只需要调用一次
                        AssetsDatabaseManager.initManager(getApplication());
                        // 获取管理对象，因为数据库需要通过管理对象才能够获取
                        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
                        // 通过管理对象获取数据库
                        SQLiteDatabase db = mg.getDatabase("users.db");
//                      Log.d("秘钥",jsonObj.getJSONObject("DocumentElement").getJSONObject("Table").getString("secretkey"));
                        if (jsonObj.has("DocumentElement") && jsonObj.toString().substring(jsonObj.toString().length() - 3, jsonObj.toString().length() - 2).equals("}")) {
                            obj = jsonObj.getJSONObject("DocumentElement").getJSONObject("Table");
                            ContentValues values = new ContentValues();
                            if (obj.has("code")) {
                                values.put("CompanyId", obj.getString("code"));
                            } else {
                                values.put("CompanyId", "");
                            }
                            if (obj.has("companyid")) {
                                values.put("ParentId", obj.getString("companyid"));
                            }
                            if (obj.has("fullname")) {
                                values.put("FullName", obj.getString("fullname"));
                            }
                            db.replace("Base_Company", null, values);
                            if (obj.has("validflag") && obj.has("code") && obj.getString("validflag").equals("1")) {
                                String[] args = {String.valueOf(obj.getString("code"))};
                                db.delete("Base_Company", "CompanyId=?", args);
                            }
//                            db.execSQL("REPLACE INTO Base_Company VALUES('" +
//                                    obj.getString("code") + "','" +
//                                    obj.getString("companyid") + "','" +
//                                    obj.getString("fullname") + "')");
                        } else if (jsonObj.has("DocumentElement") && jsonObj.toString().substring(jsonObj.toString().length() - 3, jsonObj.toString().length() - 2).equals("]")) {
                            array = jsonObj.getJSONObject("DocumentElement").getJSONArray("Table");
                            for (int i = 0; i < array.length(); i++) {
                                obj = array.getJSONObject(i);
                                ContentValues values = new ContentValues();
                                if (obj.has("code")) {
                                    values.put("CompanyId", obj.getString("code"));
                                } else {
                                    values.put("CompanyId", "");
                                }
                                if (obj.has("companyid")) {
                                    values.put("ParentId", obj.getString("companyid"));
                                }
                                if (obj.has("fullname")) {
                                    values.put("FullName", obj.getString("fullname"));
                                }
                                db.replace("Base_Company", null, values);
                                if (obj.has("validflag") && obj.has("code") && obj.getString("validflag").equals("1")) {
                                    String[] args = {String.valueOf(obj.getString("code"))};
                                    db.delete("Base_Company", "CompanyId=?", args);
                                }
//                                db.execSQL("REPLACE INTO Base_Company VALUES('" +
//                                        obj.getString("code") + "','" +
//                                        obj.getString("companyid") + "','" +
//                                        obj.getString("fullname") + "')");
                            }

                        }

                        upDateDepartment();
                    } catch (JSONException e) {
                        //关闭加载页
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                            progressDialog = null;
                        }
                        Toast.makeText(LoginActivity.this, "提交失败,数据错误缺失", Toast.LENGTH_SHORT).show();
                        Log.e("JSON exception", e.getMessage());
                        e.printStackTrace();
                    }

                } else {
                    //关闭加载页
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                        progressDialog = null;
                    }
                    Toast.makeText(LoginActivity.this, "网络未响应，提交失败", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    /**
     * 更新部门信息
     */
    protected void upDateDepartment() {
        //添加参数
        HashMap<String, String> properties = new HashMap<>();
        properties.put("Xml", "<Request><data  code='delect-Department'><no></no></data></Request>");
        properties.put("Token", "");
        WebServiceUtils.callWebService(WEB_SERVER_URL, "DynamicInvoke", properties, new WebServiceUtils.WebServiceCallBack() {

            //WebService接口返回的数据回调到这个方法中
            @Override
            public void callBack(SoapObject result) {
                if (result != null) {
                    Log.d("登录返回数据", result.toString());
                    try {
                        Object detail = result.getProperty("DynamicInvokeResult");
                        JSONObject jsonObj;
                        jsonObj = XML.toJSONObject(detail.toString());
                        JSONArray array;
                        JSONObject obj;
                        // 初始化，只需要调用一次
                        AssetsDatabaseManager.initManager(getApplication());
                        // 获取管理对象，因为数据库需要通过管理对象才能够获取
                        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
                        // 通过管理对象获取数据库
                        SQLiteDatabase db = mg.getDatabase("users.db");
                        try {
                            db.execSQL("ALTER TABLE Base_Department ADD COLUMN Id");//添加企业类型1
                        }catch (Exception e){
                            Log.e("Id数据库列表已存在", e.toString(),e);
                        }
//                      Log.d("秘钥",jsonObj.getJSONObject("DocumentElement").getJSONObject("Table").getString("secretkey"));
                        if (jsonObj.has("DocumentElement") && jsonObj.toString().substring(jsonObj.toString().length() - 3, jsonObj.toString().length() - 2).equals("}")) {
                            obj = jsonObj.getJSONObject("DocumentElement").getJSONObject("Table");
                            ContentValues values = new ContentValues();
                            if (obj.has("code")) {
                                values.put("DepartmentId", obj.getString("code"));
//                                values.put("CompanyId",obj.getString("code").substring(0, 6));
                                String[] strings = obj.getString("parentid").split("-");
                                values.put("CompanyId", strings[0]);

                            } else {
                                values.put("DepartmentId", "");
                                values.put("CompanyId", "");
                            }
                            if (obj.has("fullname")) {
                                values.put("FullName", obj.getString("fullname"));
                            }
                            if(obj.has("departmentid")){
                                values.put("Id", obj.getString("departmentid"));
                            }
                            db.replace("Base_Department", null, values);

                            if (obj.has("validflag") && obj.has("code") && obj.getString("validflag").equals("1")) {
                                String[] args = {String.valueOf(obj.getString("code"))};
                                db.delete("Base_Department", "DepartmentId=?", args);
                            }
//                            db.execSQL("REPLACE INTO Base_Department VALUES('" +
//                                    obj.getString("code") + "','" +
//                                    obj.getString("code").substring(0, 6) + "','" +
//                                    obj.getString("fullname") + "')");

                        } else if (jsonObj.has("DocumentElement") && jsonObj.toString().substring(jsonObj.toString().length() - 3, jsonObj.toString().length() - 2).equals("]")) {
                            array = jsonObj.getJSONObject("DocumentElement").getJSONArray("Table");
                            for (int i = 0; i < array.length(); i++) {
                                obj = array.getJSONObject(i);
                                ContentValues values = new ContentValues();
                                if (obj.has("code")) {
                                    values.put("DepartmentId", obj.getString("code"));
//                                    values.put("CompanyId",obj.getString("code").substring(0, 6));
                                    String[] strings = obj.getString("parentid").split("-");
//                                    Log.e("LoginActivity", "string[0]为" + strings[0]);
                                    values.put("CompanyId", strings[0]);
                                } else {
                                    values.put("DepartmentId", "");
                                    values.put("CompanyId", "");
                                }
                                if (obj.has("fullname")) {
                                    values.put("FullName", obj.getString("fullname"));
                                }
                                if(obj.has("departmentid")){
                                    values.put("Id", obj.getString("departmentid"));
                                }
                                db.replace("Base_Department", null, values);
                                if (obj.has("validflag") && obj.has("code") && obj.getString("validflag").equals("1")) {
                                    String[] args = {String.valueOf(obj.getString("code"))};
                                    db.delete("Base_Department", "DepartmentId=?", args);
                                }
//                                db.execSQL("REPLACE INTO Base_Department VALUES('" +
//                                        obj.getString("code") + "','" +
//                                        obj.getString("code").substring(0, 6) + "','" +
//                                        obj.getString("fullname") + "')");

                            }

                        }
                        upDateUser();
                    } catch (JSONException e) {
                        //关闭加载页
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                            progressDialog = null;
                        }
                        Toast.makeText(LoginActivity.this, "提交失败,数据错误缺失", Toast.LENGTH_SHORT).show();
                        Log.e("JSON exception", e.getMessage());
                        e.printStackTrace();
                    }

                } else {
                    //关闭加载页
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                        progressDialog = null;
                    }
                    Toast.makeText(LoginActivity.this, "网络未响应，提交失败", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    /**
     * 更新用户信息
     */
    protected void upDateUser() {
        progressDialog.setTitile("准备登录");
        // 初始化，只需要调用一次
        AssetsDatabaseManager.initManager(getApplication());
        // 获取管理对象，因为数据库需要通过管理对象才能够获取
        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
        // 通过管理对象获取数据库
        final SQLiteDatabase db = mg.getDatabase("users.db");
        Cursor c = db.rawQuery("SELECT* FROM Base_User ", null);
        int ModifyIndex = 0;
        while (c.moveToNext()) {
            try {
                if (ModifyIndex < Integer.parseInt(c.getString(c.getColumnIndex("ModifyIndex")))) {
                    ModifyIndex = Integer.parseInt(c.getString(c.getColumnIndex("ModifyIndex")));
                }
            } catch (Exception e) {
                Log.e("用户获取更新标数据库报错", e.toString());
            }


        }
        c.close();
        //添加参数
        HashMap<String, String> properties = new HashMap<>();
        properties.put("Xml", "<Request><data  code='update-User'><no><ModifyIndex>" +
                ModifyIndex + "</ModifyIndex></no></data></Request>");
        properties.put("Token", "");
//        Log.d("用户发送数据",properties.toString());
        WebServiceUtils.callWebService(WEB_SERVER_URL, "DynamicInvoke", properties, new WebServiceUtils.WebServiceCallBack() {

            //WebService接口返回的数据回调到这个方法中
            @Override
            public void callBack(SoapObject result) {
                if (result != null) {
//                    Log.d("用户信息返回数据",result.toString());
                    try {

                        Object detail = result.getProperty("DynamicInvokeResult");
                        JSONObject jsonObj;
                        jsonObj = XML.toJSONObject(detail.toString());
                        JSONArray array;
                        JSONObject obj;
                        String authstartdate = "";
                        String authenddate = "";
                        String workingdate = "";
                        String age = "0";
                        String education = "";
                        String idcard = "";
                        String departmentid = "";
                        String gender = "男";
                        String mobile = "";
                        String workingyear = "0";
                        String maritalstatus = "";
                        String physiclalstatus = "";
//                                        Log.d("秘钥",jsonObj.getJSONObject("DocumentElement").getJSONObject("Table").getString("secretkey"));
                        if (jsonObj.has("DocumentElement") && jsonObj.toString().substring(jsonObj.toString().length() - 3, jsonObj.toString().length() - 2).equals("}")) {
                            obj = jsonObj.getJSONObject("DocumentElement").getJSONObject("Table");
                            if (obj.has("authstartdate")) {
                                authstartdate = obj.getString("authstartdate");
                            }
                            if (obj.has("authenddate")) {
                                authenddate = obj.getString("authenddate");
                            }
                            if (obj.has("workingdate")) {
                                workingdate = obj.getString("workingdate");
                            }
                            if (obj.has("age")) {
                                age = obj.getString("age");
                            }
                            if (obj.has("education")) {
                                education = obj.getString("education");
                            }
                            if (obj.has("idcard")) {
                                idcard = obj.getString("idcard");
                            }
                            if (obj.has("departmentcode")) {
                                departmentid = obj.getString("departmentcode");
                            }
                            if (obj.has("gender")) {
                                gender = obj.getString("gender");
                            }
                            if (obj.has("mobile")) {
                                mobile = obj.getString("mobile");
                            }
                            if (obj.has("workingyear")) {
                                workingyear = obj.getString("workingyear");
                            }
                            if (obj.has("maritalstatus")) {
                                maritalstatus = obj.getString("maritalstatus");
                            }
                            if (obj.has("physiclalstatus")) {
                                physiclalstatus = obj.getString("physiclalstatus");
                            }
                            ContentValues values = new ContentValues();
                            values.put("UserId", obj.getString("userid"));
                            if (departmentid.length() > 6) {
                                String[] strings = departmentid.split("-");
                                values.put("CompanyId", strings[0]);
                            } else {
                                values.put("CompanyId", departmentid);
                            }
                            values.put("DepartmentId", departmentid);
                            if (obj.has("code")) {
                                values.put("Code", obj.getString("code"));
                            } else {
                                values.put("Code", "");
                            }
                            if (obj.has("password")) {
                                values.put("Password", obj.getString("password"));
                            } else {
                                values.put("Password", "");
                            }
                            if (obj.has("realname")) {
                                values.put("RealName", obj.getString("realname"));
                            } else {
                                values.put("RealName", "");
                            }
                            values.put("Gender", gender);
                            values.put("Mobile", mobile);
                            values.put("AuthStartDate", authstartdate);
                            values.put("AuthEndDate", authenddate);
                            if (obj.has("modifyindex")) {
                                values.put("ModifyIndex", obj.getString("modifyindex"));
                            } else {
                                values.put("ModifyIndex", "");
                            }
                            if (obj.has("validflag")) {
                                values.put("ValidFlag", obj.getString("validflag"));
                            } else {
                                values.put("ValidFlag", "");
                            }
                            values.put("IDCard", idcard);
                            values.put("Age", age);
                            values.put("Education", education);
                            values.put("WorkingYear", workingyear);
                            values.put("WorkingDate", workingdate);
                            values.put("MaritalStatus", maritalstatus);
                            values.put("PhysiclalStatus", physiclalstatus);

                            if(departmentid.length()>0&&values.get("Code").toString().length()>0&&values.get("RealName").toString().length()>0&&values.get("Password").toString().length()>0){
                                db.replace("Base_User", null, values);
                            }
                        } else if (jsonObj.has("DocumentElement") && jsonObj.toString().substring(jsonObj.toString().length() - 3, jsonObj.toString().length() - 2).equals("]")) {
                            array = jsonObj.getJSONObject("DocumentElement").getJSONArray("Table");
                            for (int i = 0; i < array.length(); i++) {
                                obj = array.getJSONObject(i);
                                if (obj.has("authstartdate")) {
                                    authstartdate = obj.getString("authstartdate");
                                }
                                if (obj.has("authenddate")) {
                                    authenddate = obj.getString("authenddate");
                                }
                                if (obj.has("workingdate")) {
                                    workingdate = obj.getString("workingdate");
                                }
                                if (obj.has("age")) {
                                    age = obj.getString("age");
                                }
                                if (obj.has("education")) {
                                    education = obj.getString("education");
                                }
                                if (obj.has("idcard")) {
                                    idcard = obj.getString("idcard");
                                }
                                if (obj.has("departmentcode")) {
                                    departmentid = obj.getString("departmentcode");
                                }
                                if (obj.has("gender")) {
                                    gender = obj.getString("gender");
                                }
                                if (obj.has("mobile")) {
                                    mobile = obj.getString("mobile");
                                }
                                if (obj.has("workingyear")) {
                                    workingyear = obj.getString("workingyear");
                                }
                                if (obj.has("maritalstatus")) {
                                    maritalstatus = obj.getString("maritalstatus");
                                }
                                if (obj.has("physiclalstatus")) {
                                    physiclalstatus = obj.getString("physiclalstatus");
                                }
                                ContentValues values = new ContentValues();
                                values.put("UserId", obj.getString("userid"));
                                if (departmentid.length() > 6) {
                                    String[] strings = departmentid.split("-");
                                    values.put("CompanyId", strings[0]);
                                } else {
                                    values.put("CompanyId", departmentid);
                                }

                                values.put("DepartmentId", departmentid);
                                if (obj.has("code")) {
                                    values.put("Code", obj.getString("code"));
                                } else {
                                    values.put("Code", "");
                                }
                                if (obj.has("password")) {
                                    values.put("Password", obj.getString("password"));
                                } else {
                                    values.put("Password", "");
                                }
                                if (obj.has("realname")) {
                                    values.put("RealName", obj.getString("realname"));
                                } else {
                                    values.put("RealName", "");
                                }
                                values.put("Gender", gender);
                                values.put("Mobile", mobile);
                                values.put("AuthStartDate", authstartdate);
                                values.put("AuthEndDate", authenddate);
                                if (obj.has("modifyindex")) {
                                    values.put("ModifyIndex", obj.getString("modifyindex"));
                                } else {
                                    values.put("ModifyIndex", "");
                                }
                                if (obj.has("validflag")) {
                                    values.put("ValidFlag", obj.getString("validflag"));
                                } else {
                                    values.put("ValidFlag", "");
                                }
                                values.put("IDCard", idcard);
                                values.put("Age", age);
                                values.put("Education", education);
                                values.put("WorkingYear", workingyear);
                                values.put("WorkingDate", workingdate);
                                values.put("MaritalStatus", maritalstatus);
                                values.put("PhysiclalStatus", physiclalstatus);
                                if(departmentid.length()>0&&values.get("Code").toString().length()>0&&values.get("RealName").toString().length()>0&&values.get("Password").toString().length()>0){
                                    db.replace("Base_User", null, values);
                                }
                            }

                        }
//                        Intent intent = new Intent();
//                        intent.putExtra("Account", userId.getText().toString());
//                        setResult(0, intent);
                        finish();
                        overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                    } catch (JSONException e) {
                        //关闭加载页
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                            progressDialog = null;
                        }
                        Toast.makeText(LoginActivity.this, "提交失败,数据错误缺失", Toast.LENGTH_SHORT).show();
                        Log.e("报错-upDateUser", e.getMessage());
                        e.printStackTrace();
                    }

                } else {
                    //关闭加载页
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                        progressDialog = null;
                    }
                    Toast.makeText(LoginActivity.this, "网络未响应，提交失败", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    /**
     * 提醒执法账号快过期了
     */
    protected void dialogDoneDelete(int i) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("请注意！");
        builder.setMessage("您的执法证距离过期还剩" + i + "天。");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });
        builder.show();
    }

    /**
     * 提醒执法账号已过期了
     */
    protected void dialogDone() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("请注意！");
        builder.setMessage("您的执法证已过期！");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                Intent intent = new Intent();
//                setResult(1, intent);
                finish();
                mainActivity.finish();
                dialog.dismiss();
            }
        });
        builder.setCancelable(false);
        builder.show();
    }

    /**
     * 提醒非法硬件
     */
    protected void dialogE() {

        String macAddress = null;
        String macAddressCode = null;
        WifiManager wifiMgr = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = (null == wifiMgr ? null : wifiMgr.getConnectionInfo());
        if (null != info) {
            macAddress = info.getMacAddress();
        }
        macAddressCode = MD5("720" + GetMac.getMac(getApplicationContext()) + "ld2017");
        String filePath = "/sdcard/";
        String fileName = "Landong.txt";


        //生成文件夹之后，再生成文件，不然会出错
        makeFilePath(filePath, fileName);

        String strFilePath = filePath + fileName;
        // 每次写入时，都换行写
        String strContent = macAddressCode + "\r\n";
        try {
            File file = new File(strFilePath);
            if (file.exists()) {
                file.delete();
            }
            if (!file.exists()) {
//                Log.d("TestFile", "Create the file:" + strFilePath);
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            RandomAccessFile raf = new RandomAccessFile(file, "rwd");
            raf.seek(file.length());
            raf.write(strContent.getBytes());
            raf.close();
        } catch (Exception e) {
            Log.e("TestFile", "Error on write File:" + e);
        }
        final DialogNormalDialog dialog = new DialogNormalDialog(LoginActivity.this);
        final EditText editText = (EditText) dialog.getEditText();//方法在CustomDialog中实现
        dialog.setMessage("【" + getCode + "】" + "该终端未授权或注册码错误，请联系供应商输入注册码！");
        dialog.setOnPositiveListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                Intent intent = new Intent();
//                setResult(1, intent);
                finish();
                mainActivity.finish();
                dialog.dismiss();
            }
        });
        dialog.setOnNegativeListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText.getText().length() > 0) {
                    initData(editText.getText().toString());
                    dialog.dismiss();
                }
            }
        });
        dialog.setCancelable(false);
        dialog.show();
    }

    /**
     * 检测硬件
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 0:
                UpdateManager manager = new UpdateManager(this, WEB_SERVER_URL);
                try {
                    manager.checkUpdate();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (resultCode == 1) {
                    dialogE();
                } else if (resultCode == 2) {
                    String macAddress = null;
                    String macAddressCode = null;
                    WifiManager wifiMgr = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                    WifiInfo info = (null == wifiMgr ? null : wifiMgr.getConnectionInfo());
                    if (null != info) {
                        macAddress = info.getMacAddress();
                    }
                    macAddressCode = MD5("720" + GetMac.getMac(getApplicationContext()) + "ld2017");
                    macAddressCode = MD5(macAddressCode + "ld720");
                    String filePath = "/sdcard/";
                    String fileName = "anjiantong.txt";
                    writeTxtToFile(macAddressCode, filePath, fileName);

                }
                break;
            default:
                break;
        }
    }


    /**
     * 验证硬件
     */
    public final static String MD5(String s) {
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
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


    /**
     * 获取两个日期之间的间隔天数
     *
     * @return
     */
    public static int getGapCount(Date startDate, Date endDate) {
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
    }

    /**
     * 删除文件
     */
    public static void deleteDirWihtFile(File dir) {
        if (dir == null || !dir.exists() || !dir.isDirectory())
            return;
        for (File file : dir.listFiles()) {
            if (file.isFile())
                file.delete(); // 删除所有文件
            else if (file.isDirectory())
                deleteDirWihtFile(file); // 递规的方式删除文件夹
        }
        dir.delete();// 删除目录本身
    }

    private void initData(String s) {
        String filePath = "/sdcard/";
        String fileName = "anjiantong.txt";

        writeTxtToFile(s, filePath, fileName);
    }

    // 将字符串写入到文本文件中
    public void writeTxtToFile(String strcontent, String filePath, String fileName) {
        //生成文件夹之后，再生成文件，不然会出错
        makeFilePath(filePath, fileName);

        String strFilePath = filePath + fileName;
        // 每次写入时，都换行写
        String strContent = strcontent + "\r\n";
        try {
            File file = new File(strFilePath);
            if (file.exists()) {
                file.delete();
            }
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            RandomAccessFile raf = new RandomAccessFile(file, "rwd");
            raf.seek(file.length());
            raf.write(strContent.getBytes());
            raf.close();
            /**
             * 欢迎页面
             * */
            Intent intent = new Intent();
            intent.setClass(this, WelcomeActivity.class);
            startActivityForResult(intent, 0);
        } catch (Exception e) {
            Log.e("TestFile", "Error on write File:" + e);
            /**
             * 欢迎页面
             * */
            Intent intent = new Intent();
            intent.setClass(this, WelcomeActivity.class);
            startActivityForResult(intent, 0);
        }
    }

    // 生成文件
    public File makeFilePath(String filePath, String fileName) {
        File file = null;
        makeRootDirectory(filePath);
        try {
            file = new File(filePath + fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    // 生成文件夹
    public static void makeRootDirectory(String filePath) {
        File file = null;
        try {
            file = new File(filePath);
            if (!file.exists()) {
                file.mkdir();
            }
        } catch (Exception e) {
            Log.i("error:", e + "");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_READ_PHONE_STATE:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    //TODO
                }
                break;

            default:
                break;
        }
    }

}
