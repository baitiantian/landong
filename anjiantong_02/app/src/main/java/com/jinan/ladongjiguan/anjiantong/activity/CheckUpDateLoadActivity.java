package com.jinan.ladongjiguan.anjiantong.activity;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.jinan.ladongjiguan.anjiantong.PublicClass.AssetsDatabaseManager;
import com.jinan.ladongjiguan.anjiantong.PublicClass.CustomProgressDialog;
import com.jinan.ladongjiguan.anjiantong.PublicClass.FTP;
import com.jinan.ladongjiguan.anjiantong.utils.FTPUtils;
import com.jinan.ladongjiguan.anjiantong.utils.SharedPreferencesUtil;
import com.jinan.ladongjiguan.anjiantong.PublicClass.Utility;
import com.jinan.ladongjiguan.anjiantong.utils.WebServiceUtils;
import com.jinan.ladongjiguan.anjiantong.R;
import com.jinan.ladongjiguan.anjiantong.utils.CommonUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.ksoap2.serialization.SoapObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.jinan.ladongjiguan.anjiantong.activity.CountMainActivity.FTPID;
/**
 * 此处下载文件走的是FTP并非是http
 * */
public class CheckUpDateLoadActivity extends BaseActivity implements View.OnTouchListener, View.OnClickListener {


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
    @BindView(R.id.check_enterprise_07_01)
    TextView checkEnterprise0701;
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
    @BindView(R.id.l_check_up_date_02)
    LinearLayout lCheckUpDate02;
    @BindView(R.id.bt_check_up_date_05)
    Button btCheckUpDate05;
    @BindView(R.id.l_bt_02)
    LinearLayout lBt02;
    @BindView(R.id.Linear_layout_2)
    LinearLayout LinearLayout2;
    @BindView(R.id.check_enterprise_12)
    TextView checkEnterprise12;
    @BindView(R.id.list)
    ListView list;
    @BindView(R.id.list_problem)
    ListView listProblem;
    @BindView(R.id.tx_list_table)
    LinearLayout txListTable;
    @BindView(R.id.list_01)
    ListView list01;
    @BindView(R.id.tx_list_table_num)
    TextView txListTableNum;
    private CustomProgressDialog progressDialog = null;//加载页
    private String WEB_SERVER_URL;
    private String Planid = "";//计划id
    private List<Map<String, Object>> listItems_2 = new ArrayList<>();//隐患信息集合
    private String Dangerid = "";
    private List<Map<String, Object>> listItems = new ArrayList<>();//文书合集
    private String filePath;//文件的保存路径
    private DownloadManager downloadManager;
    private long mTaskId;
    private FTPUtils ftpUtils = null;
    private Boolean flag = false;
    private String TAG = CheckUpDateLoadActivity.class.getSimpleName();
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    Toast.makeText(CheckUpDateLoadActivity.this, "下载完成", Toast.LENGTH_SHORT).show();
                    CommonUtils.doOpenPdf(CheckUpDateLoadActivity.this, "/sdcard/Download/" + filePath);

                    break;
                case 1:
                    Toast.makeText(CheckUpDateLoadActivity.this, "下载失败", Toast.LENGTH_SHORT).show();

                    break;
                default:
                    break;
            }

        }

    };
    @Override
    protected void initView() {
        setContentView(R.layout.check_up_date_load_layout);
        ButterKnife.bind(this);
        titleLayout.setText("计划详情");
        WEB_SERVER_URL = SharedPreferencesUtil.getStringData(this, "IPString", "");
        /*初始化FTP*/
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
        btCheckUpDate05.setOnClickListener(this);
        examinePageBack.setOnClickListener(this);
        switch (getIntent().getStringExtra("state")) {
            case "1":
                getDate();
                break;
            case "2":
                // 初始化，只需要调用一次
                AssetsDatabaseManager.initManager(getApplication());
                // 获取管理对象，因为数据库需要通过管理对象才能够获取
                AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
                // 通过管理对象获取数据库
                SQLiteDatabase db = mg.getDatabase("users.db");
                Cursor c = db.rawQuery("SELECT* FROM ELL_Business WHERE BusinessId = ?",
                        new String[]{getIntent().getStringExtra("businessid")});
                c.moveToFirst();
                checkEnterprise01.setText(c.getString(c.getColumnIndex("BusinessName")));
                checkEnterprise02.setText(c.getString(c.getColumnIndex("Address")));
                checkEnterprise03.setText(c.getString(c.getColumnIndex("LegalPerson")));
                checkEnterprise04.setText(c.getString(c.getColumnIndex("LegalPersonPost")));
                checkEnterprise05.setText(c.getString(c.getColumnIndex("LegalPersonPhone")));
                checkEnterprise0701.setText(getIntent().getStringExtra("enforceaccording"));
                checkEnterprise9.setText(getIntent().getStringExtra("companyname"));
                checkEnterprise07.setText(getIntent().getStringExtra("address"));
                fromTime1.setText(getIntent().getStringExtra("statTime"));
                endTime1.setText(getIntent().getStringExtra("endTime"));
                checkEnterprise08.setText(getIntent().getStringExtra("planchecktype"));
//                checkEnterprise12.setText(cursor2.getString(cursor2.getColumnIndex("RealName")));
                Planid = getIntent().getStringExtra("PlanId");
                c.close();
                getGroupDate();
                break;
            default:
                break;
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.examine_page_back://返回
                onBackPressed();
                break;
            case R.id.bt_check_up_date_05:
                onBackPressed();
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
     * 获得数据
     */
    protected void getDate() {
        //加载页添加
        if (progressDialog == null) {
            progressDialog = CustomProgressDialog.createDialog(this);
        }
        progressDialog.show();

        //添加参数
        HashMap<String, String> properties = new HashMap<>();
        properties.put("Xml", "<Request><data  code='select-HiddendangerInfoByDangerid'><no><HiddenDangerId>" +
                getIntent().getStringExtra("id") + "</HiddenDangerId></no></data></Request>");
        properties.put("Token", "");
        WebServiceUtils.callWebService(WEB_SERVER_URL, "DynamicInvoke", properties, new WebServiceUtils.WebServiceCallBack() {

            //WebService接口返回的数据回调到这个方法中
            @Override
            public void callBack(SoapObject result) {
                if (result != null) {
                    Log.d("返回数据", result.toString());
                    try {

                        Object detail = result.getProperty("DynamicInvokeResult");
                        JSONObject jsonObj;
                        jsonObj = XML.toJSONObject(detail.toString());
                        JSONObject obj;
                        JSONArray array;
                        // 初始化，只需要调用一次
                        AssetsDatabaseManager.initManager(getApplication());
                        // 获取管理对象，因为数据库需要通过管理对象才能够获取
                        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
                        // 通过管理对象获取数据库
                        SQLiteDatabase db = mg.getDatabase("users.db");

                        if (jsonObj.has("DocumentElement") && jsonObj.toString().substring(jsonObj.toString().length() - 3, jsonObj.toString().length() - 2).equals("}")) {
                            obj = jsonObj.getJSONObject("DocumentElement").getJSONObject("Table");
                            // 对数据库进行操作
                            Cursor c = db.rawQuery("SELECT* FROM ELL_Business WHERE BusinessId = ?",
                                    new String[]{obj.getString("businessid")});
                            c.moveToFirst();
                            // 填入数据
                            String s1 = c.getString(c.getColumnIndex("BusinessName"));
                            checkEnterprise01.setText(s1);
                            checkEnterprise02.setText(c.getString(c.getColumnIndex("Address")));
                            checkEnterprise03.setText(c.getString(c.getColumnIndex("LegalPerson")));
                            checkEnterprise04.setText(c.getString(c.getColumnIndex("LegalPersonPost")));
                            checkEnterprise05.setText(c.getString(c.getColumnIndex("LegalPersonPhone")));
                            c.close();
                            checkEnterprise0701.setText(obj.getString("enforceaccording"));
                            checkEnterprise9.setText(obj.getString("companyname"));
                            if (obj.has("address")) {
                                checkEnterprise07.setText(obj.getString("address"));
                            }
                            fromTime1.setText(obj.getString("stattime").substring(0, 10));
                            endTime1.setText(obj.getString("endtime").substring(0, 10));
                            checkEnterprise08.setText(obj.getString("planchecktype"));
                            Cursor cursor2 = db.rawQuery("SELECT* FROM Base_User WHERE UserId = ?",
                                    new String[]{obj.getString("headman")});
                            cursor2.moveToFirst();
                            checkEnterprise12.setText(cursor2.getString(cursor2.getColumnIndex("RealName")));
                            cursor2.close();
                            Planid = obj.getString("planid");
                        } else if (jsonObj.has("DocumentElement") && jsonObj.toString().substring(jsonObj.toString().length() - 3, jsonObj.toString().length() - 2).equals("]")) {
                            array = jsonObj.getJSONObject("DocumentElement").getJSONArray("Table");
                            obj = array.getJSONObject(1);
                            // 对数据库进行操作
                            Cursor c = db.rawQuery("SELECT* FROM ELL_Business WHERE BusinessId = ?",
                                    new String[]{obj.getString("businessid")});
                            c.moveToFirst();
                            // 填入数据
                            String s1 = c.getString(c.getColumnIndex("BusinessName"));
                            checkEnterprise01.setText(s1);
                            checkEnterprise02.setText(c.getString(c.getColumnIndex("Address")));
                            checkEnterprise03.setText(c.getString(c.getColumnIndex("LegalPerson")));
                            checkEnterprise04.setText(c.getString(c.getColumnIndex("LegalPersonPost")));
                            checkEnterprise05.setText(c.getString(c.getColumnIndex("LegalPersonPhone")));
                            c.close();
                            checkEnterprise0701.setText(obj.getString("enforceaccording"));
                            checkEnterprise9.setText(obj.getString("companyname"));
                            if (obj.has("address")) {
                                checkEnterprise07.setText(obj.getString("address"));
                            }
                            fromTime1.setText(obj.getString("stattime").substring(0, 10));
                            endTime1.setText(obj.getString("endtime").substring(0, 10));
                            checkEnterprise08.setText(obj.getString("planchecktype"));
                            Cursor cursor2 = db.rawQuery("SELECT* FROM Base_User WHERE UserId = ?",
                                    new String[]{obj.getString("headman")});
                            cursor2.moveToFirst();
                           try{
                               checkEnterprise12.setText(cursor2.getString(cursor2.getColumnIndex("RealName")));
                           }catch (Exception e){
                               Log.e("获取用户数据报错", e.getMessage(),e);
                           }
                            cursor2.close();
                            Planid = obj.getString("planid");
                        }
                        getGroupDate();

                    } catch (JSONException e) {
                        //关闭加载页
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                            progressDialog = null;
                        }
                        Toast.makeText(CheckUpDateLoadActivity.this, "数据缺失", Toast.LENGTH_SHORT).show();
                        Log.e("JSON exception", e.getMessage(),e);
                        e.printStackTrace();
                    }
                } else {
                    //关闭加载页
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                        progressDialog = null;
                    }
                    Toast.makeText(CheckUpDateLoadActivity.this, "网络未响应，提交失败", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    /**
     * 获得组信息
     */
    protected void getGroupDate() {
        //添加参数
        HashMap<String, String> properties = new HashMap<>();
        properties.put("Xml", "<Request><data  code='select-MemberInfoByPlanid'><no><Planid>" +
                Planid + "</Planid></no></data></Request>");
        properties.put("Token", "");
        WebServiceUtils.callWebService(WEB_SERVER_URL, "DynamicInvoke", properties, new WebServiceUtils.WebServiceCallBack() {

            //WebService接口返回的数据回调到这个方法中
            @Override
            public void callBack(SoapObject result) {
                if (result != null) {
                    Log.d("返回数据", result.toString());
                    try {

                        Object detail = result.getProperty("DynamicInvokeResult");
                        JSONObject jsonObj;
                        jsonObj = XML.toJSONObject(detail.toString());
                        JSONObject obj;
                        JSONArray array;
                        // 初始化，只需要调用一次
                        AssetsDatabaseManager.initManager(getApplication());
                        // 获取管理对象，因为数据库需要通过管理对象才能够获取
                        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
                        // 通过管理对象获取数据库
                        SQLiteDatabase db = mg.getDatabase("users.db");
                        List<Map<String, Object>> maps = new ArrayList<>();

                        if (jsonObj.has("DocumentElement") && jsonObj.toString().substring(jsonObj.toString().length() - 3, jsonObj.toString().length() - 2).equals("}")) {
                            obj = jsonObj.getJSONObject("DocumentElement").getJSONObject("Table");
                            Map<String, Object> map = new HashMap<>();
                            String s = "组员：" + obj.getString("id").substring(0, obj.getString("id").length() - 1) + "\n" +
                                    "路线：" + obj.getString("address");
                            map.put("text", s);
                            maps.add(map);
                            checkEnterprise12.setText(obj.getString("headman"));
                        } else if (jsonObj.has("DocumentElement") && jsonObj.toString().substring(jsonObj.toString().length() - 3, jsonObj.toString().length() - 2).equals("]")) {
                            array = jsonObj.getJSONObject("DocumentElement").getJSONArray("Table");
                            for (int i = 0; i < array.length(); i++) {
                                obj = array.getJSONObject(i);
                                Map<String, Object> map = new HashMap<>();
                                String s = "组员：" + obj.getString("id").substring(0, obj.getString("id").length() - 1) + "\n" +
                                        "路线：" + obj.getString("address");
                                map.put("text", s);
                                maps.add(map);
                                checkEnterprise12.setText(obj.getString("headman"));
                            }
                        }
                        SimpleAdapter simpleAdapter = new SimpleAdapter(CheckUpDateLoadActivity.this, maps,
                                R.layout.enterprise_information_list_item, new String[]{"text"},
                                new int[]{R.id.enterprise_name});
                        list.setAdapter(simpleAdapter);
                        Utility.setListViewHeightBasedOnChildren(list);
                        //关闭加载页
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                            progressDialog = null;
                        }
                        switch (getIntent().getStringExtra("state")) {
                            case "1":
                                LinearLayout linearLayout = (LinearLayout) findViewById(R.id.l_list_01);
                                linearLayout.setVisibility(View.GONE);
                                break;
                            case "2":
                                getHiddenDanger();
                                txListTable.setVisibility(View.VISIBLE);
                                break;
                            default:
                                break;
                        }
                    } catch (JSONException e) {
                        //关闭加载页
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                            progressDialog = null;
                        }
                        Toast.makeText(CheckUpDateLoadActivity.this, "数据缺失", Toast.LENGTH_SHORT).show();
                        Log.e("JSON exception", e.getMessage());
                        e.printStackTrace();
                    }
                } else {
                    //关闭加载页
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                        progressDialog = null;
                    }
                    Toast.makeText(CheckUpDateLoadActivity.this, "网络未响应，提交失败", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    /**
     * 获得隐患
     */
    protected void getHiddenDanger() {
//加载页添加
        if (progressDialog == null) {
            progressDialog = CustomProgressDialog.createDialog(this);
        }
        progressDialog.show();

        //添加参数
        HashMap<String, String> properties = new HashMap<>();

        switch (getIntent().getStringExtra("XmlCod")) {
            case "1":
                properties.put("Xml", "<Request><data  code='1select-HiddenInfoByDepartID'><no><Planid>" +
                        Planid + "</Planid><departmentid>" +
                        getIntent().getStringExtra("XmlCodID") + "</departmentid><StartTime>" +
                        getIntent().getStringExtra("s_from_time") + "</StartTime><EndTime>" +
                        getIntent().getStringExtra("s_end_time") + "</EndTime></no></data></Request>");
                break;
            case "2":
                properties.put("Xml", "<Request><data  code='1select-HiddenInfoByDepartID'><no><Planid>" +
                        Planid + "</Planid><departmentid>" +
                        getIntent().getStringExtra("XmlCodID") + "</departmentid><StartTime>" +
                        getIntent().getStringExtra("s_from_time") + "</StartTime><EndTime>" +
                        getIntent().getStringExtra("s_end_time") + "</EndTime></no></data></Request>");
                break;
            case "3":
                properties.put("Xml", "<Request><data  code='1select-HiddenInfoByUserID'><no><Planid>" +
                        Planid + "</Planid><Userid>" +
                        getIntent().getStringExtra("XmlCodID") + "</Userid><StartTime>" +
                        getIntent().getStringExtra("s_from_time") + "</StartTime><EndTime>" +
                        getIntent().getStringExtra("s_end_time") + "</EndTime></no></data></Request>");
                break;
            case "4":
                properties.put("Xml", "<Request><data  code='1select-HiddenInfoByCompanyID'><no><Planid>" +
                        Planid + "</Planid><Businessid>" +
                        getIntent().getStringExtra("XmlCodID") + "</Businessid><StartTime>" +
                        getIntent().getStringExtra("s_from_time") + "</StartTime><EndTime>" +
                        getIntent().getStringExtra("s_end_time") + "</EndTime></no></data></Request>");
                break;
            default:
                break;
        }
        properties.put("Token", "");
        Log.d("发送数据", properties.toString());
        WebServiceUtils.callWebService(WEB_SERVER_URL, "DynamicInvoke", properties, new WebServiceUtils.WebServiceCallBack() {

            //WebService接口返回的数据回调到这个方法中
            @Override
            public void callBack(SoapObject result) {
                if (result != null) {
                    Log.d("返回数据", result.toString());
                    try {
//                                       Log.d("用户名发过去的结果",""+result);
                        Object detail = result.getProperty("DynamicInvokeResult");
                        JSONObject jsonObj;
                        jsonObj = XML.toJSONObject(detail.toString());
//                        System.out.println("===========================" + jsonObj);
                        JSONArray array;
                        JSONObject obj;
                        listItems_2 = new ArrayList<>();

                        // 初始化，只需要调用一次
                        AssetsDatabaseManager.initManager(getApplication());
                        // 获取管理对象，因为数据库需要通过管理对象才能够获取
                        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
                        // 通过管理对象获取数据库
                        SQLiteDatabase db = mg.getDatabase("users.db");
//                                        Log.d("秘钥",jsonObj.getJSONObject("DocumentElement").getJSONObject("Table").getString("secretkey"));
                        if (jsonObj.has("DocumentElement") && jsonObj.toString().substring(jsonObj.toString().length() - 3, jsonObj.toString().length() - 2).equals("}")) {
                            obj = jsonObj.getJSONObject("DocumentElement").getJSONObject("Table");
                            Map<String, Object> listItem = new HashMap<>();
//                            listItem.put("checktype", obj.getString("checktype"));
                            String s = obj.getString("yhms") + "(" + obj.getString("disposeresult") + ")";
                            listItem.put("YHMS", s);
                            Cursor c = db.rawQuery("SELECT* FROM ELL_Business WHERE BusinessId = ?",
                                    new String[]{obj.getString("businessid")});
                            c.moveToFirst();
                            listItem.put("hiddendangerid", c.getString(c.getColumnIndex("BusinessName")));
                            listItem.put("id", obj.getString("hiddendangerid"));
                            c.close();
                            if (obj.has("zgqx")) {
                                listItem.put("ZGQX", obj.getString("zgqx").substring(0, 10));
                            } else {
                                listItem.put("ZGQX", "");
                            }
                            if (obj.has("documentname")) {

                                listItem.put("documentname", obj.getString("documentname"));
                            } else {
                                listItem.put("documentname", "");
                            }
                            if (obj.has("addtime")) {
                                listItem.put("addtime", obj.getString("addtime").substring(0, 10));
                            } else {
                                listItem.put("addtime", "");
                            }
                            Dangerid = obj.getString("hiddendangerid");
                            listItems_2.add(listItem);
                        } else if (jsonObj.has("DocumentElement") && jsonObj.toString().substring(jsonObj.toString().length() - 3, jsonObj.toString().length() - 2).equals("]")) {
                            array = jsonObj.getJSONObject("DocumentElement").getJSONArray("Table");
                            listItems_2 = new ArrayList<>();
                            for (int i = 0; i < array.length(); i++) {
                                obj = array.getJSONObject(i);
                                Map<String, Object> listItem = new HashMap<>();
//                                listItem.put("checktype", obj.getString("checktype"));
                                String s = obj.getString("yhms") + "(" + obj.getString("disposeresult") + ")";
                                listItem.put("YHMS", s);
                                Cursor c = db.rawQuery("SELECT* FROM ELL_Business WHERE BusinessId = ?",
                                        new String[]{obj.getString("businessid")});
                                c.moveToFirst();
                                listItem.put("hiddendangerid", c.getString(c.getColumnIndex("BusinessName")));
                                listItem.put("id", obj.getString("hiddendangerid"));
                                Dangerid = obj.getString("hiddendangerid");
                                c.close();
                                if (obj.has("zgqx")) {
                                    listItem.put("ZGQX", obj.getString("zgqx").substring(0, 10));
                                } else {
                                    listItem.put("ZGQX", "");
                                }
                                if (obj.has("documentname")) {

                                    listItem.put("documentname", obj.getString("documentname"));
                                } else {
                                    listItem.put("documentname", "");
                                }
                                if (obj.has("addtime")) {
                                    listItem.put("addtime", obj.getString("addtime").substring(0, 10));
                                } else {
                                    listItem.put("addtime", "");
                                }
                                listItems_2.add(listItem);
                            }

                        }
                        listProblem.setAdapter(HiddenDangerAdapter());
                        Utility.setListViewHeightBasedOnChildren(listProblem);
//                        init();
                        txListTableNum.setText("隐患数：" + listItems_2.size());
                        httpDate();
                        //关闭加载页
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                            progressDialog = null;
                        }


                    } catch (JSONException e) {
                        //关闭加载页
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                            progressDialog = null;
                        }
                        Toast.makeText(CheckUpDateLoadActivity.this, "提交失败", Toast.LENGTH_SHORT).show();
                        Log.e("JSON exception", e.getMessage());
                        e.printStackTrace();
                    }
                } else {
                    //关闭加载页
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                        progressDialog = null;
                    }
                    Toast.makeText(CheckUpDateLoadActivity.this, "网络未响应，提交失败", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    @NonNull
    private BaseAdapter HiddenDangerAdapter() {
        return new BaseAdapter() {
            @Override
            public int getCount() {
                if (listItems_2 != null) {
                    return listItems_2.size();
                }
                return 0;
            }

            @Override
            public Object getItem(int position) {
                return listItems_2.get(position);
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {
                ViewHolder holder;
                if (convertView == null) { // 如果为空，就表示是第一次加载，还没有加入到缓存中
                    holder = new ViewHolder();
                    convertView = View.inflate(CheckUpDateLoadActivity.this, R.layout.hidden_danger_01_item, null);
//                    holder.type = (TextView) convertView.findViewById(R.id.type_tv);
                    holder.yhms = (TextView) convertView.findViewById(R.id.hiddenDanger_tv);
                    holder.hiddenDanger_tv = (TextView) convertView.findViewById(R.id.type_tv);
                    holder.zgqx = (TextView) convertView.findViewById(R.id.zgqx_tv);
                    holder.jcsj = (TextView) convertView.findViewById(R.id.jcsj_tv);
                    holder.ssws = (TextView) convertView.findViewById(R.id.ssws_tv);
                    convertView.setTag(holder); // 加入缓存
                } else {
                    holder = (ViewHolder) convertView.getTag(); // 如果ConvertView不为空，则表示在缓存中
                }
                holder.jcsj.setText(listItems_2.get(position).get("addtime").toString());
                holder.ssws.setText(listItems_2.get(position).get("documentname").toString());
                holder.zgqx.setText(listItems_2.get(position).get("ZGQX").toString());
                holder.yhms.setText(listItems_2.get(position).get("YHMS").toString());
                holder.hiddenDanger_tv.setText(listItems_2.get(position).get("hiddendangerid").toString());
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(CheckUpDateLoadActivity.this, HiddenDangerActivity.class);
                        intent.putExtra("id", listItems_2.get(position).get("id").toString());
                        startActivity(intent);
                    }
                });
                return convertView;
            }
        };
    }


    class ViewHolder {
        private TextView zgqx;
        private TextView yhms;
        private TextView hiddenDanger_tv;
        private TextView ssws;
        private TextView jcsj;

    }

    /**
     * 查询隐患详细信息
     */
    protected void httpDate() {
        //加载页添加
        if (progressDialog == null) {
            progressDialog = CustomProgressDialog.createDialog(this);
        }
        progressDialog.show();

        //添加参数
        HashMap<String, String> properties = new HashMap<>();
        properties.put("Xml", "<Request><data  code='select-HiddendangerInfoByDangerid'><no><HiddenDangerId>" +
                Dangerid + "</HiddenDangerId></no></data></Request>");
        properties.put("Token", "");
        WebServiceUtils.callWebService(WEB_SERVER_URL, "DynamicInvoke", properties, new WebServiceUtils.WebServiceCallBack() {

            //WebService接口返回的数据回调到这个方法中
            @Override
            public void callBack(SoapObject result) {
                if (result != null) {
                    Log.d("返回数据", result.toString());
                    try {
                        listItems = new ArrayList<>();
//                                       Log.d("用户名发过去的结果",""+result);
                        Object detail = result.getProperty("DynamicInvokeResult");
                        JSONObject jsonObj;
                        jsonObj = XML.toJSONObject(detail.toString());
                        JSONObject obj;
                        JSONArray array;

                        if (jsonObj.has("DocumentElement") && jsonObj.toString().substring(jsonObj.toString().length() - 3, jsonObj.toString().length() - 2).equals("}")) {
                            obj = jsonObj.getJSONObject("DocumentElement").getJSONObject("Table");

                            Map<String, Object> listItem = new HashMap<>();
                            if (obj.has("documentname")) {

                                listItem.put("documentname", obj.getString("documentname"));
                            } else {
                                listItem.put("documentname", "");
                            }
                            if (obj.has("documentid2") && obj.getString("documenttype").equals("1")) {
                                listItem.put("documentid", obj.getString("documentid2"));
                            } else {
                                listItem.put("documentid", obj.getString("documentid"));
                            }


                            listItems.add(listItem);
                        } else if (jsonObj.has("DocumentElement") && jsonObj.toString().substring(jsonObj.toString().length() - 3, jsonObj.toString().length() - 2).equals("]")) {
                            array = jsonObj.getJSONObject("DocumentElement").getJSONArray("Table");

                            for (int i = 0; i < array.length(); i++) {
                                obj = array.getJSONObject(i);
                                Map<String, Object> listItem = new HashMap<>();
                                if (obj.has("documentname")) {

                                    listItem.put("documentname", obj.getString("documentname"));
                                } else {
                                    listItem.put("documentname", "");
                                }
                                if (obj.has("documentid2") && obj.getString("documenttype").equals("1")) {
                                    listItem.put("documentid", obj.getString("documentid2"));
                                } else {
                                    listItem.put("documentid", obj.getString("documentid"));
                                }
                                listItems.add(listItem);
                            }

                        }


                        //关闭加载页
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                            progressDialog = null;
                        }
                        SimpleAdapter simpleAdapter = new SimpleAdapter(CheckUpDateLoadActivity.this, listItems,
                                R.layout.enterprise_information_list_item,
                                new String[]{"documentname"},
                                new int[]{R.id.enterprise_name});
                        list01.setAdapter(simpleAdapter);
                        Utility.setListViewHeightBasedOnChildren(list01);
                        list01.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, final int position, final long id) {
//                                Intent intent = new Intent();
//                                intent.putExtra("documentid", listItems.get(position).get("documentid").toString());
//                                intent.putExtra("state", "7");
////                                intent.setClass(HiddenDangerActivity.this,PdfActivity.class);
//                                intent.setAction(Intent.ACTION_VIEW);//为Intent设置动作
//                                intent.setData(Uri.parse(FTPID + listItems.get(position).get("documentid") + ".pdf"));//为Intent设置数据
                                Log.d("下载文书的数据", FTPID + listItems.get(position).get("documentid") + ".pdf");
//                                startActivity(intent);//将Intent传递给Activity
//                                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                                filePath = listItems.get(position).get("documentname") + ".pdf";
                                File file = new File("/mnt/sdcard/Download/" + filePath);
                                if (file.exists()) {
                                    file.delete();
                                }
//                                if(ftpUtils.downLoadFile("/mnt/sdcard/Download/" + filePath, listItems.get(position).get("documentid") + ".pdf")){
//                                    Toast.makeText(CheckUpDateLoadActivity.this, "下载成功", Toast.LENGTH_SHORT).show();
//                                    CommonUtils.doOpenPdf(CheckUpDateLoadActivity.this, "/sdcard/Download/" + filePath);
//
//                                }
                                Toast.makeText(CheckUpDateLoadActivity.this, "正在下载请稍等", Toast.LENGTH_SHORT).show();

                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {

                                        // 下载
                                        try {

                                            //单文件下载
                                            new FTP().downloadSingleFile( "/ftpdata/"+listItems.get(position).get("documentid") + ".pdf","/sdcard/Download/",filePath,new FTP.DownLoadProgressListener(){

                                                @Override
                                                public void onDownLoadProgress(String currentStep, long downProcess, File file) {
                                                    Log.d(TAG, currentStep);
                                                    if(currentStep.equals(MainActivity.FTP_DOWN_SUCCESS)){
                                                        Log.d(TAG, "-----xiazai--successful");
                                                        handler.sendEmptyMessage(0);
                                                    } else if(currentStep.equals(MainActivity.FTP_DOWN_LOADING)){
                                                        Log.d(TAG, "-----xiazai---"+downProcess + "%");
                                                    }else if(currentStep.equals(MainActivity.FTP_DOWN_FAIL)||currentStep.equals(MainActivity.FTP_CONNECT_FAIL)){
                                                        handler.sendEmptyMessage(1);
                                                    }
                                                }

                                            });

                                        } catch (Exception e) {
                                            // TODO Auto-generated catch block
                                            e.printStackTrace();
                                        }

                                    }
                                }).start();
//                                downloadAPK(FTPID + listItems.get(position).get("documentid") + ".pdf", filePath);

                            }
                        });
                    } catch (JSONException e) {
                        //关闭加载页
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                            progressDialog = null;
                        }
                        Toast.makeText(CheckUpDateLoadActivity.this, "数据缺失", Toast.LENGTH_SHORT).show();
                        Log.e("JSON exception", e.getMessage());
                        e.printStackTrace();
                    }
                } else {
                    //关闭加载页
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                        progressDialog = null;
                    }
                    Toast.makeText(CheckUpDateLoadActivity.this, "网络未响应，提交失败", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    //使用系统下载器下载
    private void downloadAPK(String versionUrl, String versionName) {
//创建下载任务
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(versionUrl));
        request.setAllowedOverRoaming(false);
//漫游网络是否可以下载
//设置文件类型，可以在下载结束后自动打开该文件
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        String mimeString = mimeTypeMap.getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(versionUrl));
        request.setMimeType(mimeString);
//在通知栏中显示，默认就是显示的
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        request.setVisibleInDownloadsUi(true);
        //sdcard的目录下的download文件夹，必须设置
        request.setDestinationInExternalPublicDir("/Download/", versionName);
        //request.setDestinationInExternalFilesDir(),也可以自己制定下载路径
        //将下载请求加入下载队列
        downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        //加入下载队列后会给该任务返回一个long型的id，
        //通过该id可以取消任务，重启任务等等，看上面源码中框起来的方法
        mTaskId = downloadManager.enqueue(request);
        //注册广播接收者，监听下载状态
        this.registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    //广播接受者，接收下载状态
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e(TAG,"00000000");
            checkDownloadStatus();
//检查下载状态
        }
    };

    protected void checkDownloadStatus() {
        Log.e(TAG,"-------");
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(mTaskId);
        //筛选下载任务，传入任务ID，可变参数
        Cursor c = downloadManager.query(query);
        if (c.moveToFirst()) {
            int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
            switch (status) {
                case DownloadManager.STATUS_PAUSED:
                    Log.e("下载数据", ">>>下载暂停");
                case DownloadManager.STATUS_PENDING:
                    Log.e("下载数据", ">>>下载延迟");
                case DownloadManager.STATUS_RUNNING:
                    Log.e("下载数据", ">>>正在下载");
                    break;
                case DownloadManager.STATUS_SUCCESSFUL:
                    Log.e(TAG+"下载数据", ">>>下载完成");
                    //下载完成安装APK
                    Toast.makeText(CheckUpDateLoadActivity.this, "加载中，请稍等", Toast.LENGTH_SHORT).show();
                    CommonUtils.doOpenPdf(CheckUpDateLoadActivity.this, "/mnt/sdcard/Download/" + filePath);

                    break;
                case DownloadManager.STATUS_FAILED:
                    Log.e("下载数据", ">>>下载失败");
                    break;
            }
        }

    }

}
