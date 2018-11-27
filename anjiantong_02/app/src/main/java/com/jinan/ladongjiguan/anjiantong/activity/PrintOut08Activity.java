package com.jinan.ladongjiguan.anjiantong.activity;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfStamper;
import com.jinan.ladongjiguan.anjiantong.PublicClass.AssetsDatabaseManager;
import com.jinan.ladongjiguan.anjiantong.PublicClass.CustomProgressDialog;
import com.jinan.ladongjiguan.anjiantong.utils.FTPUtils;
import com.jinan.ladongjiguan.anjiantong.utils.SharedPreferencesUtil;
import com.jinan.ladongjiguan.anjiantong.utils.WebServiceUtils;
import com.jinan.ladongjiguan.anjiantong.R;
import com.jinan.ladongjiguan.anjiantong.utils.CommonUtils;
import com.jinan.ladongjiguan.anjiantong.utils.FileUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.ksoap2.serialization.SoapObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;


public class PrintOut08Activity extends BaseActivity implements View.OnClickListener, View.OnTouchListener {
    @BindView(R.id.examine_page_back)
    LinearLayout examinePageBack;
    @BindView(R.id.title_layout)
    TextView titleLayout;
    @BindView(R.id.et_print_out_07)
    EditText etPrintOut07;
    @BindView(R.id.s_print_out_01)
    Spinner sPrintOut01;
    @BindView(R.id.s_print_out_02)
    Spinner sPrintOut02;
    @BindView(R.id.et_print_out_03)
    EditText etPrintOut03;
    @BindView(R.id.et_print_out_04)
    EditText etPrintOut04;
    @BindView(R.id.et_print_out_05)
    EditText etPrintOut05;
    @BindView(R.id.et_print_out_01)
    EditText etPrintOut01;
    @BindView(R.id.et_print_out_02)
    EditText etPrintOut02;
    @BindView(R.id.et_print_out_06)
    EditText etPrintOut06;
    @BindView(R.id.bt_print_out_00)
    Button btPrintOut00;
    @BindView(R.id.bt_print_out_01)
    Button btPrintOut01;
    @BindView(R.id.bt_print_out_02)
    Button btPrintOut02;
    @BindView(R.id.printf_text)
    TextView printfText;
    private String DocumentNumber = "";//文书编号
    private String DocumentId = "";//文书id
    private Map<String, String> map = new HashMap<>();
    private String fill_2_01="";
    private String fill_2_02="";
    private ArrayList<HashMap<String, Object>> peopleList = new ArrayList<>();//执法人员表
    private String WEB_SERVER_URL;
    private CustomProgressDialog progressDialog = null;//加载页
    private String CompanyId = "";
    private boolean flag;
    private FTPUtils ftpUtils = null;
    private String newxcjcfaPath;
    private ByteArrayOutputStream bos;
    private OutputStream fos;
    private PdfStamper ps;
    private BaseFont bf;
    private ArrayList<BaseFont> fontList;
    private AcroFields fields;//模板中的标签
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    dateUp();
                    break;
                case 1:
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                        progressDialog = null;
                    }
                    Toast.makeText(PrintOut08Activity.this, "文书上传失败，请重新上传", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }

        }

    };
    @Override
    protected void initView() {
        setContentView(R.layout.print_out_08_layout);
        ButterKnife.bind(this);
        titleLayout.setText("制定现场检查方案文书");
        getDate();
        WEB_SERVER_URL = SharedPreferencesUtil.getStringData(this, "IPString", "");
        File lsws = new File("/sdcard/00_linshiwenshu/linshi_xcjcfa");
        File zsws = new File("/mnt/sdcard/00_zhengshiwenshu/zhengshi_xcjcfa");
//如果文件夹不存在则创建
        if (!lsws.exists() && !lsws.isDirectory()) {
            lsws.mkdirs();
        }
        if (!zsws.exists() && !zsws.isDirectory()) {
            zsws.mkdirs();
        }
        etPrintOut07.setText("00");
        if(SharedPreferencesUtil.getStringData(this,getIntent().getStringExtra("PlanId"),null)!=null){
            DocumentNumber = SharedPreferencesUtil.getStringData(this,getIntent().getStringExtra("PlanId"),null);
            etPrintOut07.setText(DocumentNumber);
        }
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
        examinePageBack.setOnClickListener(this);
        examinePageBack.setOnTouchListener(this);
        btPrintOut00.setOnClickListener(this);
        btPrintOut01.setOnClickListener(this);
        btPrintOut02.setOnClickListener(this);
        DocumentId = "8"+getIntent().getStringExtra("PlanId").substring(1,getIntent().getStringExtra("PlanId").length());

    }

    @Override
    public void onClick(View v) {
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        SimpleDateFormat sdf4 = new SimpleDateFormat("yyyyMMdd", Locale.CHINA);
        switch (v.getId()) {
            case R.id.examine_page_back://返回键
                finish();
                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                break;
            case R.id.bt_print_out_00://取消键
                finish();
                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                break;
            case R.id.bt_print_out_01://出具文书键
                if(etPrintOut07.getText().length()>0){
                    String newxcjcfaPath = "/sdcard/00_linshiwenshu/linshi_xcjcfa/"+ etPrintOut03.getText().toString()+
                            sdf4.format(curDate)+ "(临时).pdf";
                    map.put("number",etPrintOut07.getText().toString());
                    map.put("BusinessName",etPrintOut03.getText().toString());
                    map.put("fill_6",etPrintOut04.getText().toString());
                    map.put("fill_2",fill_2_01+fill_2_02);
                    String s = "";
                    for(int i = 0;i<peopleList.size();i++){
                        s = s+peopleList.get(i).get("RealName").toString()+
                                peopleList.get(i).get("Code").toString();
                    }
                    map.put("fill_2",s);
                    map.put("fill_3",etPrintOut01.getText().toString());
                    map.put("fill_4",etPrintOut02.getText().toString());
                    map.put("fill_7",etPrintOut06.getText().toString());
                    map.put("name",etPrintOut05.getText().toString());
//                    Log.d("生成PDF数据",map.toString());
                    FileUtils.deleteFile(newxcjcfaPath);

                    String xcjcfaPath = "assets/template/xcjcfa.pdf";
//                    readpdfandFillData(xcjcfaPath, map, newxcjcfaPath);
                    CommonUtils.readpdfandFillData(bos,fos,newxcjcfaPath,ps,bf,fontList,fields,xcjcfaPath, map);
                    CommonUtils.doOpenPdf(PrintOut08Activity.this,newxcjcfaPath);
                }else {
                    Toast.makeText(this, "请完善信息", Toast.LENGTH_SHORT).show();

                }

                break;
            case R.id.bt_print_out_02:
                if (CommonUtils.isNetworkConnected(PrintOut08Activity.this)) {
                    dialogDoneDelete();
                }else {
                    Toast.makeText(PrintOut08Activity.this, "请确定网络是否连接", Toast.LENGTH_SHORT).show();
                }
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
     * 获取数据
     */
    protected void getDate() {
        // 初始化，只需要调用一次
        AssetsDatabaseManager.initManager(getApplication());
        // 获取管理对象，因为数据库需要通过管理对象才能够获取
        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
        // 通过管理对象获取数据库
        SQLiteDatabase db = mg.getDatabase("users.db");
        try {
            // 对数据库进行操作
            //获取计划信息
            Cursor c = db.rawQuery("SELECT* FROM ELL_EnforcementPlan WHERE PlanId = ?",
                    new String[]{getIntent().getStringExtra("PlanId")});
            c.moveToFirst();
            //获取安检机构信息
            Cursor cursor4 = db.rawQuery("SELECT* FROM Base_Company WHERE CompanyId = ?",
                    new String[]{c.getString(c.getColumnIndex("CompanyId"))});
            cursor4.moveToFirst();
            String[] jiebie = cursor4.getString(cursor4.getColumnIndex("FullName")).split("安");
            map.put("Company", jiebie[0]);
            map.put("fill_1",c.getString(c.getColumnIndex("EndTime")));
            CompanyId = cursor4.getString(cursor4.getColumnIndex("ParentId"));
            cursor4.close();
            //获取企业信息
            Cursor cursor = db.rawQuery("SELECT* FROM ELL_Business WHERE BusinessId = ?",
                    new String[]{c.getString(c.getColumnIndex("BusinessId"))});
            cursor.moveToFirst();
            etPrintOut03.setText(cursor.getString(cursor.getColumnIndex("BusinessName")));
            etPrintOut04.setText(cursor.getString(cursor.getColumnIndex("Address")));
            etPrintOut05.setText(cursor.getString(cursor.getColumnIndex("LegalPerson")));

            map.put("BusinessType", cursor.getString(cursor.getColumnIndex("BusinessType")));

            //获取执法人员信息
            ArrayList<HashMap<String, Object>> UserIDList = new ArrayList<>();
            Cursor cursor1 = db.rawQuery("SELECT* FROM ELL_GroupInfo WHERE PlanId = ?",
                    new String[]{getIntent().getStringExtra("PlanId")});
            int x = 0;
            while (cursor1.moveToNext()) {
                HashMap<String, Object> listItem = new HashMap<>();

                if( x == 0){
                    listItem.put("UserId", cursor1.getString(cursor1.getColumnIndex("Headman")));
                    UserIDList.add(listItem);
                }

//                Log.d("查询的人员数据", listItem.toString());
                Cursor cursor2 = db.rawQuery("SELECT* FROM ELL_Member WHERE GroupId = ?",
                        new String[]{cursor1.getString(cursor1.getColumnIndex("GroupId"))});
                while (cursor2.moveToNext()) {
                    HashMap<String, Object> listItem01 = new HashMap<>();
                    listItem01.put("UserId", cursor2.getString(cursor2.getColumnIndex("Member")));
                    UserIDList.add(listItem01);
//                    Log.e("查询的人员数据", listItem01.toString());
                }
                cursor2.close();
                x++;
            }
            peopleList = new ArrayList<>();
            for (int i = 0; i < UserIDList.size(); i++) {
                HashMap<String, Object> listItem = new HashMap<>();
                Cursor cursor3 = db.rawQuery("SELECT* FROM Base_User WHERE UserId = ?",
                        new String[]{UserIDList.get(i).get("UserId").toString()});
                cursor3.moveToFirst();
                listItem.put("RealName" + "Code", cursor3.getString(cursor3.getColumnIndex("RealName")) + "(" +
                        cursor3.getString(cursor3.getColumnIndex("Code")) + ")");
                listItem.put("RealName", cursor3.getString(cursor3.getColumnIndex("RealName")));
                listItem.put("Code", cursor3.getString(cursor3.getColumnIndex("Code")));
                if(i==0){
                    peopleList.add(listItem);
                }else {

                    for(int i1=0;i1<peopleList.size();i1++){
                        if(peopleList.get(i1).get("Code").equals(cursor3.getString(cursor3.getColumnIndex("Code")))){
                            break;
                        }else if(i1==peopleList.size()-1){
                            peopleList.add(listItem);
                        }
                    }
                }

                cursor3.close();
            }
            //下拉列表
            SimpleAdapter simpleAdapter = new SimpleAdapter(this, peopleList,
                    R.layout.login_spinner_item,
                    new String[]{"RealName" + "Code"},
                    new int[]{R.id.text});
            sPrintOut01.setAdapter(simpleAdapter);
            sPrintOut02.setAdapter(simpleAdapter);
            sPrintOut01.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    fill_2_01 = peopleList.get(position).get("RealName").toString()+
                            peopleList.get(position).get("Code").toString();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            sPrintOut02.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    fill_2_02 = peopleList.get(position).get("RealName").toString()+
                            peopleList.get(position).get("Code").toString();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            sPrintOut02.setSelection(1);
            cursor1.close();
            cursor.close();
            c.close();
        } catch (Exception e) {
            Log.e("print01数据库报错", e.toString());
        }

    }

    /**
     * 提醒是否上传
     */
    protected void dialogDoneDelete() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("请注意！");
        builder.setMessage("此文书只可上传一次，请仔细确认后再上传。是否将该计划文书上传至服务器？");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(SharedPreferencesUtil.getStringData(PrintOut08Activity.this,getIntent().getStringExtra("PlanId"),null)==null){
                    getNum();
                }else {
                    if (progressDialog == null) {
                        progressDialog = CustomProgressDialog.createDialog(PrintOut08Activity.this);
                    }
                    progressDialog.show();
                    DocumentNumber = SharedPreferencesUtil.getStringData(PrintOut08Activity.this,getIntent().getStringExtra("PlanId"),null);
                    makePDF();
                }

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
     * 获取文书编号
     * */
    protected void getNum(){

        if (progressDialog == null) {
            progressDialog = CustomProgressDialog.createDialog(this);
        }
        progressDialog.show();

        //添加参数
        HashMap<String, String> properties = new HashMap<>();
        properties.put("Xml", "<Request><data  code='GetDocumentCode'><no><DocumentType>1</DocumentType><CompanyId>" +
                CompanyId + "</CompanyId><BusinessType>" +
                map.get("BusinessType") + "</BusinessType></no></data></Request>");
        properties.put("Token", "");
        Log.e("文书上传数据", properties.toString());
        WebServiceUtils.callWebService(WEB_SERVER_URL, "DynamicInvoke", properties, new WebServiceUtils.WebServiceCallBack() {

            //WebService接口返回的数据回调到这个方法中
            @Override
            public void callBack(SoapObject result) {
                if (result != null) {
                    Log.d("文书编号返回数据", result.toString());
                    try {
                        Object detail = result.getProperty("DynamicInvokeResult");
                        JSONObject jsonObj;
                        jsonObj = XML.toJSONObject(detail.toString());
//                      Log.d("秘钥",jsonObj.getJSONObject("DocumentElement").getJSONObject("Table").getString("secretkey"));
                        DocumentNumber = jsonObj.getJSONObject("DocumentElement").getJSONObject("Table").getString("Column1");
                        SharedPreferencesUtil.saveStringData(PrintOut08Activity.this,getIntent().getStringExtra("PlanId"),DocumentNumber);
                        makePDF();

                    } catch (JSONException e) {
                        //关闭加载页
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                            progressDialog = null;
                        }
                        Toast.makeText(PrintOut08Activity.this, "提交失败", Toast.LENGTH_SHORT).show();
                        Log.e("JSON exception", e.getMessage());
                        e.printStackTrace();
                    }
                } else {
                    //关闭加载页
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                        progressDialog = null;
                    }
                    Toast.makeText(PrintOut08Activity.this, "网络未响应，提交失败", Toast.LENGTH_SHORT).show();

                }
            }
        });

    }

    /**
     * 生成PDF
     * */
    protected void  makePDF(){
        Log.d("文书编号数据",DocumentNumber);
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        SimpleDateFormat sdf4 = new SimpleDateFormat("yyyyMMdd", Locale.CHINA);
        newxcjcfaPath = "/sdcard/00_zhengshiwenshu/zhengshi_xcjcfa/"+ etPrintOut03.getText().toString()+
                sdf4.format(curDate)+ ".pdf";
        map.put("number",etPrintOut07.getText().toString());
        map.put("BusinessName",etPrintOut03.getText().toString());
        map.put("fill_6",etPrintOut04.getText().toString());
        map.put("fill_2",fill_2_01+fill_2_02);
        String s = "";
        for(int i = 0;i<peopleList.size();i++){
            s = s+peopleList.get(i).get("RealName").toString()+
                    peopleList.get(i).get("Code").toString();
        }
        map.put("fill_2",s);
        map.put("fill_3",etPrintOut01.getText().toString());
        map.put("fill_4",etPrintOut02.getText().toString());
        map.put("fill_7",etPrintOut06.getText().toString());
        map.put("name",etPrintOut05.getText().toString());
//                    Log.d("生成PDF数据",map.toString());
        FileUtils.deleteFile(newxcjcfaPath);

        String xcjcfaPath = "assets/template/xcjcfa.pdf";
//        readpdfandFillData(xcjcfaPath, map, newxcjcfaPath);
        CommonUtils.readpdfandFillData(bos,fos,newxcjcfaPath,ps,bf,fontList,fields,xcjcfaPath, map);
        //保存文书
        saveDate(newxcjcfaPath);

    }
    /**
     * 保存文书信息到数据库
     */
    protected void saveDate(String s) {
        // 初始化，只需要调用一次
        AssetsDatabaseManager.initManager(getApplication());
        // 获取管理对象，因为数据库需要通过管理对象才能够获取
        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
        // 通过管理对象获取数据库
        SQLiteDatabase db = mg.getDatabase("users.db");
        try {
            Date curDate = new Date(System.currentTimeMillis());//获取当前时间
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
            ContentValues values = new ContentValues();
            values.put("DocumentId", DocumentId);
            values.put("PlanId", getIntent().getStringExtra("PlanId"));
            values.put("DocumentNumber",
                    "（" + map.get("Company") + "）安监检查[" +
                            sdf.format(curDate).substring(0, 4) + "]" +
                            map.get("BusinessType") + DocumentNumber + "号" );
            values.put("Problem","检查内容："+etPrintOut01.getText().toString()+"\n"+
                    "检查方式："+ etPrintOut02.getText().toString() +"\n"+
                    "备注："+ etPrintOut03.getText().toString()+ "\n");
            values.put("AddTime", sdf.format(curDate) );
            values.put("PlanType", "执法计划");
            db.replace("ELL_Document", null, values);
            Log.d("存入文书数据", "REPLACE INTO ELL_Document VALUES('" +
                    DocumentId + "','" +
                    getIntent().getStringExtra("PlanId") + "','" +
                    "（" + map.get("Company") + "）安监检查[" + sdf.format(curDate).substring(0, 4) + "]" +
                    map.get("BusinessType") + DocumentNumber + "号" + "','" +
                    "检查内容："+etPrintOut01.getText().toString()+"\n"+
                    "检查方式："+ etPrintOut02.getText().toString() +"\n"+
                    "备注："+ etPrintOut03.getText().toString()+ "\n"+"','" +
                    sdf.format(curDate) + "','" +
                    "执法计划" + "')");
            upLoad(s);//上传文书
        } catch (Exception e) {
            Log.e("存入数据库报错", e.toString());
        }
    }
    /**
     * 上传文书
     */
    protected void upLoad(final String s) {


        if (flag) {
            //上传文件

            new Thread() {
                @Override
                public void run() {
                    Log.d("上传路径", s); //输出错误消息
                    Log.d("上传文件名", DocumentId); //输出错误消息

                    Boolean aBoolean = ftpUtils.uploadFile(s, DocumentId + ".pdf");//xcjcjl_zuizhong

                    if (aBoolean ) {
                        handler.sendEmptyMessage(0);
                    } else {
                        handler.sendEmptyMessage(1);
                    }
                }
            }.start();
        } else {
            Toast.makeText(PrintOut08Activity.this, "上传失败", Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * 上传文书表数据
     */
    protected void dateUp() {
        /**
         * 处理定义各类数据
         * */
        /*文书表*/
        String DocumentNumber = "";
        String Problem = "";
        String AddTime = "";

        // 初始化，只需要调用一次
        AssetsDatabaseManager.initManager(getApplication());
        // 获取管理对象，因为数据库需要通过管理对象才能够获取
        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
        // 通过管理对象获取数据库
        SQLiteDatabase db = mg.getDatabase("users.db");
        try {
            /*计划*/
            Cursor cursor = db.rawQuery("SELECT* FROM ELL_EnforcementPlan WHERE PlanId = ?",
                    new String[]{getIntent().getStringExtra("PlanId")});
            cursor.moveToFirst();

            Cursor cursor5 = db.rawQuery("SELECT* FROM Base_Company WHERE CompanyId = ?",
                    new String[]{cursor.getString(cursor.getColumnIndex("CompanyId"))});
            cursor5.moveToFirst();
            CompanyId = cursor5.getString(cursor5.getColumnIndex("ParentId"));
            cursor5.close();

            cursor.close();
            /*文书*/
            Cursor cursor1 = db.rawQuery("SELECT* FROM ELL_Document WHERE DocumentId = ?",
                    new String[]{DocumentId});
            cursor1.moveToFirst();
            DocumentNumber = cursor1.getString(cursor1.getColumnIndex("DocumentNumber"));
            Problem = cursor1.getString(cursor1.getColumnIndex("Problem"));
            AddTime = cursor1.getString(cursor1.getColumnIndex("AddTime"));
            cursor1.close();


        } catch (Exception e) {
            Log.e("数据库填补数据报错", e.toString());
        }
        //添加参数
        HashMap<String, String> properties = new HashMap<>();
        properties.put("Xml", "<Request><data  code='add-document'><no><DocumentId>" +
                DocumentId+"</DocumentId><PlanId>" +
                getIntent().getStringExtra("PlanId")+"</PlanId><DocumentNumber>" +
                DocumentNumber+"</DocumentNumber><Problem>" +
                Problem+"</Problem><AddTime>" +
                AddTime+"</AddTime><DocumentType>" +
                "8"+"</DocumentType><PlanType>" +
                "执法计划"+"</PlanType></no></data></Request>");
        properties.put("Token", "");
        Log.e("各类表上传数据", properties.toString());
        WebServiceUtils.callWebService(WEB_SERVER_URL, "DynamicInvoke", properties, new WebServiceUtils.WebServiceCallBack() {

            //WebService接口返回的数据回调到这个方法中
            @Override
            public void callBack(SoapObject result) {
                if (result != null) {
                    Log.d("各类表上传返回数据", result.toString());
                    try {
//                                       Log.d("用户名发过去的结果",""+result);
                        Object detail = result.getProperty("DynamicInvokeResult");
                        JSONObject jsonObj;
                        jsonObj = XML.toJSONObject(detail.toString());
                        Log.d("各类表上传返回数据", jsonObj.getJSONObject("Response").getString("result"));
                        if(jsonObj.getJSONObject("Response").getString("result").equals("true")){
                            Toast.makeText(PrintOut08Activity.this, "上传成功", Toast.LENGTH_SHORT).show();
                            CommonUtils.doOpenPdf(PrintOut08Activity.this,newxcjcfaPath);

                            if (progressDialog != null) {
                                progressDialog.dismiss();
                                progressDialog = null;
                            }
                            finish();
                            overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                        } else {
                            if (progressDialog != null) {
                                progressDialog.dismiss();
                                progressDialog = null;
                            }
                            Toast.makeText(PrintOut08Activity.this, "此文书已上传，请不要重复上传", Toast.LENGTH_SHORT).show();

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
                        Toast.makeText(PrintOut08Activity.this, "提交失败", Toast.LENGTH_SHORT).show();
                        Log.e("JSON exception", e.getMessage());
                        e.printStackTrace();
                    }
                } else {
                    //关闭加载页
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                        progressDialog = null;
                    }
                    Toast.makeText(PrintOut08Activity.this, "网络未响应，提交失败", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }
}
