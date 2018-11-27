package com.jinan.ladongjiguan.anjiantong.activity;


import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;
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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.jinan.ladongjiguan.anjiantong.PublicClass.StringOrDate.getGapCount;


public class PrintOut09Activity extends BaseActivity implements View.OnClickListener, View.OnTouchListener {
    @BindView(R.id.examine_page_back)
    LinearLayout examinePageBack;
    @BindView(R.id.title_layout)
    TextView titleLayout;
    @BindView(R.id.s_print_out_01)
    Spinner sPrintOut01;
    @BindView(R.id.s_print_out_02)
    Spinner sPrintOut02;
    @BindView(R.id.et_print_out_01)
    EditText etPrintOut01;
    @BindView(R.id.et_print_out_06)
    EditText etPrintOut06;
    @BindView(R.id.et_print_out_07)
    EditText etPrintOut07;
    @BindView(R.id.et_print_out_08)
    EditText etPrintOut08;
    @BindView(R.id.et_print_out_02)
    EditText etPrintOut02;
    @BindView(R.id.et_print_out_03)
    EditText etPrintOut03;
    @BindView(R.id.et_print_out_04)
    EditText etPrintOut04;
    @BindView(R.id.et_print_out_05)
    EditText etPrintOut05;
    @BindView(R.id.bt_print_out_00)
    Button btPrintOut00;
    @BindView(R.id.bt_print_out_01)
    Button btPrintOut01;
    @BindView(R.id.bt_print_out_02)
    Button btPrintOut02;
    @BindView(R.id.printf_text)
    TextView printfText;
    @BindView(R.id.printf_text_1)
    TextView printfText1;
    @BindView(R.id.et_print_out_09)
    EditText etPrintOut09;
    @BindView(R.id.spin_province)
    Spinner spinProvince;
//    @BindView(R.id.print_out_09_edit)
//    EditText mEditText;
//    @BindView(R.id.print_out_09_btn)
//    Button mBtn;
    @BindView(R.id.spin_city)
    Spinner spinCity;
    private String DocumentNumber = "00";
    //现场处理措施决定书 模板地址
    private String rectifyPath = "assets/template/xcclcsjds.pdf";
    // 现场处理措施决定书创建生成的文件地址
    private String newrectifyPath = "/mnt/sdcard/00_linshiwenshu/linshi_xcclcsjds/现场处理措施决定书" + DocumentNumber + "(临时1).pdf";
    private ArrayList<HashMap<String, Object>> peopleList = new ArrayList<>();//执法人员表
    private Map<String, String> checkmap = new HashMap<>();
    private String zfyr1_code;
    private String zfry2_code;
    private SimpleDateFormat sdf1;
    private SimpleDateFormat sdf2;
    private SimpleDateFormat sdf3;
    private String bumen1;
    private String bumen2;
    private String bumen3;
    private String message = SharedPreferencesUtil.getStringData(this, "userId", "");
    private List<Map<String, Object>> listItems = new ArrayList<>();
    private List<Map<String, Object>> listItems1 = new ArrayList<>();//单位：如  管理局
    private List<Map<String, Object>> listItems2 = new ArrayList<>();//科室：如  局领导
    private String CompanyId;
    private String Department;
    private String TAG = PrintOut09Activity.class.getSimpleName();

    private String autoSplitText(final TextView tv) {
        final String rawText = tv.getText().toString(); //原始文本
        final Paint tvPaint = tv.getPaint(); //paint，包含字体等信息
        final float tvWidth = tv.getWidth() - tv.getPaddingLeft() - tv.getPaddingRight(); //控件可用宽度

        //将原始文本按行拆分
        String[] rawTextLines = rawText.replaceAll("\r", "").split("\n");
        StringBuilder sbNewText = new StringBuilder();
        for (String rawTextLine : rawTextLines) {
            if (tvPaint.measureText(rawTextLine) <= tvWidth) {
                //如果整行宽度在控件可用宽度之内，就不处理了
                sbNewText.append(rawTextLine);
            } else {
                //如果整行宽度超过控件可用宽度，则按字符测量，在超过可用宽度的前一个字符处手动换行
                float lineWidth = 0;
                for (int cnt = 0; cnt != rawTextLine.length(); ++cnt) {
                    char ch = rawTextLine.charAt(cnt);
                    lineWidth += tvPaint.measureText(String.valueOf(ch));
                    if (lineWidth <= tvWidth) {
                        sbNewText.append(ch);
                    } else {
                        sbNewText.append("\n");
                        lineWidth = 0;
                        --cnt;
                    }
                }
            }
            sbNewText.append("\n");
        }

        //把结尾多余的\n去掉
        if (!rawText.endsWith("\n")) {
            sbNewText.deleteCharAt(sbNewText.length() - 1);
        }
        return sbNewText.toString();
    }

    private String[] arr;
    private String[] arr1;
    private AcroFields fields;//模板中的标签
    private PdfStamper ps;
    private OutputStream fos;
    private ByteArrayOutputStream bos;
    private String xpdf;//续页的pdf
    private String xpdfx;//续页的pdf
    private BaseFont bf;
    private ArrayList<BaseFont> fontList;
    private CustomProgressDialog progressDialog = null;//加载页
    private String DocumentId = "";//文书id
    private boolean flag;
    private FTPUtils ftpUtils = null;
    private String WEB_SERVER_URL;
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
                    Toast.makeText(PrintOut09Activity.this, "文书上传失败，请重新上传", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }

        }

    };

    @Override
    protected void initView() {
        setContentView(R.layout.print_out_09_layout);
        ButterKnife.bind(this);
        titleLayout.setText("现场处理措施决定书");
        WEB_SERVER_URL = SharedPreferencesUtil.getStringData(this, "IPString", "");
    }

    @Override
    protected void init() {
        getDate();
        setSpinner();
        examinePageBack.setOnClickListener(this);
        btPrintOut01.setOnClickListener(this);
        btPrintOut00.setOnClickListener(this);
        btPrintOut02.setOnClickListener(this);
//        mBtn.setOnClickListener(this);
        File lsws = new File("/mnt/sdcard/00_linshiwenshu/linshi_xcclcsjds");
        File zsws = new File("/mnt/sdcard/00_zhengshiwenshu/zhengshi_xcclcsjds");
//如果文件夹不存在则创建
        if (!lsws.exists() && !lsws.isDirectory()) {
            lsws.mkdirs();

        }
        if (!zsws.exists() && !zsws.isDirectory()) {
            zsws.mkdirs();
        }
        DocumentId = "9" + getIntent().getStringExtra("PlanId").substring(1, getIntent().getStringExtra("PlanId").length());
        new Thread() {
            @Override
            public void run() {
                ftpUtils = FTPUtils.getInstance();
                flag = ftpUtils.initFTPSetting(MainActivity.IP, 21, "ftpuser", "ld123456");
            }
        }.start();
    }

    @Override
    public void onClick(View v) {
        String str = printfText.getText().toString();
        Log.d("文书内容数据0", str);
        arr = str.split("\n");//该准备数据啦
        str = printfText1.getText().toString();
        Log.d("文书内容数据1", str);
        arr1 = str.split("\n");//该准备数据啦
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
                Date curDate = new Date(System.currentTimeMillis());//获取当前时间
                sdf1 = new SimpleDateFormat("yyyy", Locale.CHINA);
                checkmap.put("yyyy", sdf1.format(curDate));
                sdf2 = new SimpleDateFormat("MM", Locale.CHINA);
                checkmap.put("mm", sdf2.format(curDate));
                sdf3 = new SimpleDateFormat("dd", Locale.CHINA);
                checkmap.put("dd", sdf3.format(curDate));
                bumen1 = etPrintOut03.getText().toString();
                checkmap.put("bumen1", bumen1);
                bumen2 = etPrintOut04.getText().toString();
                checkmap.put("bumen2", bumen2);
                bumen3 = etPrintOut05.getText().toString();
                checkmap.put("bumen3", bumen3);
                checkmap.put("business", etPrintOut06.getText().toString());
                String num = "(" + bumen1 + ")安监管现决[" + checkmap.get("yyyy") + "]" +
                        getIntent().getStringExtra("BusinessType") + DocumentNumber + "号";
                int s = 1;
                s = s + (arr.length - 1) / 4;
                checkmap.put("s", s + "");
                checkmap.put("i", "1");
                checkmap.put("a_2", etPrintOut08.getText().toString());
                checkmap.put("num", num);
                if (s == 1) {
                    String a0_1 = arr[0];
                    String a0_2 = arr1[0];
                    checkmap.put("0", arr[0]);
                    checkmap.put("4", arr1[0]);
                    for (int i = 1; i < arr.length; i++) {
                        a0_1 = a0_1 + "\n" + arr[i];
//                        a0_2 = a0_2+"\n"+arr1[i];
                        checkmap.put(i + "", arr[i]);
                    }
                    for (int i = 4; i < arr1.length + 4; i++) {
                        checkmap.put(i + "", arr1[i - 4]);
                    }


                } else {
                    String a0_1 = arr[0];
                    String a0_2 = arr1[0];
                    checkmap.put("0", arr[0]);
                    checkmap.put("4", arr1[0]);
                    for (int i = 1; i < 4; i++) {
                        a0_1 = a0_1 + "\n" + arr[i];
                        checkmap.put(i + "", arr[i]);
                    }
                    if (arr1.length > 3) {
                        for (int i = 4; i < 7; i++) {
                            checkmap.put(i + "", arr1[i - 4]);
                        }

                    } else {
                        for (int i = 4; i < arr1.length + 4; i++) {
                            checkmap.put(i + "", arr1[i - 4]);
                        }
                    }
                }
                newrectifyPath = "/mnt/sdcard/00_linshiwenshu/linshi_xcclcsjds/" + checkmap.get("business") +
                        checkmap.get("yyyy") + checkmap.get("mm") + checkmap.get("dd") + "(临时).pdf";
                FileUtils.deleteFile(newrectifyPath);
//                readpdfandFillData(rectifyPath, checkmap, newrectifyPath);
                CommonUtils.readpdfandFillData(bos, fos, newrectifyPath, ps, bf, fontList, fields, rectifyPath, checkmap);
                for (int y = 2; y < s + 1; y++) {
                    for (int n = 0; n < 7; n++) {
                        checkmap.put(n + "", "");
                    }
                    int x = y - 1;
                    if (s - y == 0) {
                        checkmap.put("0", arr[x * 4]);

                        for (int i = 1; i < arr.length - x * 4; i++) {
                            checkmap.put(i + "", arr[x * 4 + i]);
                        }
                        if (arr1.length > x * 3 && arr1.length < y * 3) {
                            checkmap.put("4", arr1[x * 3]);
                            for (int i = 4; i < arr1.length + 4 - x * 3; i++) {
                                checkmap.put(i + "", arr1[x * 3 + i - 4]);
                            }
                        }


                    } else {
                        checkmap.put("0", arr[x * 4]);

                        for (int i = 1; i < 4; i++) {
                            checkmap.put(i + "", arr[x * 4 + i]);
                        }
                        if (arr1.length > x * 3) {
                            checkmap.put("4", arr1[x * 3]);
                            for (int i = 4; i < 7; i++) {

                                checkmap.put(i + "", arr1[x * 3 + i - 4]);
                            }
                        } else if (arr1.length > x * 3 && arr1.length < y * 3) {
                            checkmap.put("4", arr1[x * 3]);
                            for (int i = 4; i < arr1.length + 4 - x * 3; i++) {
                                checkmap.put(i + 4 + "", arr1[x * 3 + i - 4]);
                            }
                        }

                    }
                    checkmap.put("i", y + "");
                    rectifyPath = "assets/template/xcclcsjdst.pdf";
                    String xcclcsjdst = "/mnt/sdcard/00_linshiwenshu/linshi_xcclcsjds/现场处理措施决定书" + DocumentNumber + "(临时).pdf";
                    FileUtils.deleteFile(xcclcsjdst);
//                    readpdfandFillData(rectifyPath, checkmap, xcclcsjdst);
                    CommonUtils.readpdfandFillData(bos, fos, xcclcsjdst, ps, bf, fontList, fields, rectifyPath, checkmap);
                    String xcclcsjdst1 = "/mnt/sdcard/00_linshiwenshu/linshi_xcclcsjds/" + checkmap.get("business") +
                            checkmap.get("yyyy") + checkmap.get("mm") + checkmap.get("dd") + "(临时" + s + ").pdf";
                    FileUtils.deleteFile(xcclcsjdst1);
                    combinepdf(xcclcsjdst1, newrectifyPath, xcclcsjdst);
                    newrectifyPath = xcclcsjdst1;

                }
                CommonUtils.doOpenPdf(PrintOut09Activity.this, newrectifyPath);
                break;
            case R.id.bt_print_out_02:
                if (CommonUtils.isNetworkConnected(this)) {
                    dialogDoneDelete();
                } else {
                    Toast.makeText(this, "请确定网络是否连接", Toast.LENGTH_SHORT).show();

                }
                break;
            // TODO: 2018/9/30 14:57
            /*case R.id.print_out_09_btn:
                if (mEditText.getText().length() > 0) {
                    // 初始化，只需要调用一次
                    AssetsDatabaseManager.initManager(PrintOut09Activity.this);
                    // 获取管理对象，因为数据库需要通过管理对象才能够获取
                    AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
                    // 通过管理对象获取数据库
                    final SQLiteDatabase db = mg.getDatabase("users.db");
                    try {//Base_Department
                        Log.e(TAG + "-onClick-0", "CompanyId为" + CompanyId);
                        Cursor b = db.rawQuery("SELECT* FROM Base_Department WHERE FullName like ? AND CompanyId = ?",
                        new String[]{"%" + mEditText.getText().toString() + "%", CompanyId});
                        // 创建一个List集合，List集合的元素是Map

                        listItems2 = new ArrayList<>();
                        Map<String, Object> listItem;
                        while (b.moveToNext()) {
                            String fullName = b.getString(b.getColumnIndex("FullName"));
                            String departmentId = b.getString(b.getColumnIndex("DepartmentId"));
                            Log.e(TAG + "-onClick-1", "fullName为" + fullName);
                            listItem = new HashMap<>();
//                            listItem.put("CompanyId", companyId);
                            listItem.put("DepartmentId", departmentId);
                            listItem.put("FullName", fullName);
                            listItems2.add(listItem);
                        }
                        SimpleAdapter simpleAdapter = new SimpleAdapter(PrintOut09Activity.this, listItems2,
                                R.layout.login_spinner_item,
                                new String[]{"FullName"},
                                new int[]{R.id.text});
                        spinCity.setAdapter(simpleAdapter);
                        //下拉列表 科室
                        spinCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                Department = listItems2.get(position).get("DepartmentId").toString();
                                String fullName = listItems2.get(position).get("FullName").toString();
                                Log.e(TAG + "-onClick-2", "Department为" + Department + ";fullName为" + fullName);

                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                        b.close();
                    } catch (Exception e) {
                        Log.e("数据库报错", e.toString());
                    }
                }
                break;*/
            /**************************************************************************/
            default:
                break;
        }
    }

    //定义接口
    public interface DataBackListener {
        public void getData(String data, String id);
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
            CompanyId = c.getString(c.getColumnIndex("CompanyId"));
            //获取安检机构信息
            Cursor cursor4 = db.rawQuery("SELECT* FROM Base_Company WHERE CompanyId = ?",
                    new String[]{c.getString(c.getColumnIndex("CompanyId"))});
            cursor4.moveToFirst();
            checkmap.put("jibie", cursor4.getString(cursor4.getColumnIndex("FullName")));
            CompanyId = cursor4.getString(cursor4.getColumnIndex("ParentId"));
            String[] strings = checkmap.get("jibie").split("安");
            etPrintOut03.setText(strings[0]);
            if (strings[0].equals("六盘水市")) {
                etPrintOut04.setText("贵州省安全生产监督管理局");
                etPrintOut05.setText("六盘水市中级");
            } else {
                etPrintOut04.setText("六盘水市安全生产监督管理局");
                etPrintOut05.setText(strings[0]);
            }
            newrectifyPath = "/mnt/sdcard/00_linshiwenshu/linshi_xcclcsjds/" + cursor4.getString(cursor4.getColumnIndex("FullName")) +
                    "安监管现决" + DocumentNumber + "号(临时).pdf";
            cursor4.close();
            //获取企业信息
            Cursor cursor = db.rawQuery("SELECT* FROM ELL_Business WHERE BusinessId = ?",
                    new String[]{c.getString(c.getColumnIndex("BusinessId"))});
            cursor.moveToFirst();
            etPrintOut06.setText(cursor.getString(cursor.getColumnIndex("BusinessName")));
            checkmap.put("BusinessType", cursor.getString(cursor.getColumnIndex("BusinessType")));

            //获取执法人员信息
            ArrayList<HashMap<String, Object>> UserIDList = new ArrayList<>();
            Cursor cursor1 = db.rawQuery("SELECT* FROM ELL_GroupInfo WHERE PlanId = ?",
                    new String[]{getIntent().getStringExtra("PlanId")});
            int x = 0;
            while (cursor1.moveToNext()) {
                HashMap<String, Object> listItem = new HashMap<>();

                if (x == 0) {
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
//                    Log.d("查询的人员数据", listItem01.toString());
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
                if (i == 0) {
                    peopleList.add(listItem);
                } else {

                    for (int i1 = 0; i1 < peopleList.size(); i1++) {
                        if (peopleList.get(i1).get("Code").equals(cursor3.getString(cursor3.getColumnIndex("Code")))) {
                            break;
                        } else if (i1 == peopleList.size() - 1) {
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
                    zfyr1_code = peopleList.get(position).get("Code").toString();
                    checkmap.put("code1", zfyr1_code);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            sPrintOut02.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    zfry2_code = peopleList.get(position).get("Code").toString();
                    checkmap.put("code2", zfry2_code);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            cursor1.close();
            cursor.close();
            c.close();
            sPrintOut02.setSelection(1);
            //重新制定具体问题
            strings = getIntent().getStringExtra("CheckId").split(",");
            String s1 = "";
            for (int i = 0; i < strings.length; i++) {
                Cursor cursor2 = db.rawQuery("SELECT* FROM ELL_CheckInfo WHERE CheckId = ?",
                        new String[]{strings[i]});
                cursor2.moveToFirst();
                if (i != strings.length - 1) {
                    s1 = s1 + (i + 1) + ". " + cursor2.getString(cursor2.getColumnIndex("YHDD")) +
                            cursor2.getString(cursor2.getColumnIndex("YHBW")) +
                            cursor2.getString(cursor2.getColumnIndex("Problem")) + "\n";
                } else {
                    s1 = s1 + (i + 1) + ". " + cursor2.getString(cursor2.getColumnIndex("YHDD")) +
                            cursor2.getString(cursor2.getColumnIndex("YHBW")) +
                            cursor2.getString(cursor2.getColumnIndex("Problem"));
                }
                cursor2.close();
            }
            etPrintOut07.setText(s1);

            etPrintOut07.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    String s2 = etPrintOut07.getText().toString() + "(以下空白)";
                    printfText.setText(s2);
                    printfText.getViewTreeObserver().addOnGlobalLayoutListener(new OnTvGlobalLayoutListener());
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            String s = getIntent().getStringExtra("Problems_1");
            etPrintOut02.setText(s);
            etPrintOut02.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    String s2 = etPrintOut02.getText().toString() + "(以下空白)";
                    printfText1.setText(s2);
                    printfText1.getViewTreeObserver().addOnGlobalLayoutListener(new OnTvGlobalLayoutListener1());

                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            String s3 = etPrintOut07.getText().toString() + "(以下空白)";
            printfText.setText(s3);
            printfText.getViewTreeObserver().addOnGlobalLayoutListener(new OnTvGlobalLayoutListener());
            String s4 = etPrintOut02.getText().toString() + "(以下空白)";
            printfText1.setText(s4);
            printfText1.getViewTreeObserver().addOnGlobalLayoutListener(new OnTvGlobalLayoutListener1());


        } catch (Exception e) {
            Log.e("数据库报错", e.toString());
        }

    }


    /**
     * 适配换行用
     */
    private class OnTvGlobalLayoutListener implements ViewTreeObserver.OnGlobalLayoutListener {
        @Override
        public void onGlobalLayout() {
            printfText.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            final String newText = autoSplitText(printfText);
            if (!TextUtils.isEmpty(newText)) {
                printfText.setText(newText);
            }
        }
    }

    /**
     * 适配换行用
     */
    private class OnTvGlobalLayoutListener1 implements ViewTreeObserver.OnGlobalLayoutListener {
        @Override
        public void onGlobalLayout() {
            printfText1.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            final String newText = autoSplitText(printfText1);
            if (!TextUtils.isEmpty(newText)) {
                printfText1.setText(newText);
            }
        }
    }

    /**
     * 合并pdf
     */
    private void combinepdf(String combinepath, String pdf, String s) {
        try {
            List<InputStream> pdfs = new ArrayList<InputStream>();
            pdfs.add(new FileInputStream(pdf));
            pdfs.add(new FileInputStream(s));
            OutputStream output = new FileOutputStream(combinepath);
            concatPDFs(pdfs, output, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void concatPDFs(List<InputStream> streamOfPDFFiles,
                           OutputStream outputStream, boolean paginate) {

        Document document = new Document();
        try {
            List<InputStream> pdfs = streamOfPDFFiles;
            List<PdfReader> readers = new ArrayList<PdfReader>();
            int totalPages = 0;
            Iterator<InputStream> iteratorPDFs = pdfs.iterator();

            // Create Readers for the pdfs.
            while (iteratorPDFs.hasNext()) {
                InputStream pdf = iteratorPDFs.next();
                PdfReader pdfReader = new PdfReader(pdf);
                readers.add(pdfReader);
                totalPages += pdfReader.getNumberOfPages();
            }
            // Create a writer for the outputstream
            PdfWriter writer = PdfWriter.getInstance(document, outputStream);

            document.open();
            BaseFont bf = BaseFont.createFont(BaseFont.HELVETICA,
                    BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
            PdfContentByte cb = writer.getDirectContent(); // Holds the PDF
            // data

            PdfImportedPage page;
            int currentPageNumber = 0;
            int pageOfCurrentReaderPDF = 0;
            Iterator<PdfReader> iteratorPDFReader = readers.iterator();

            // Loop through the PDF files and add to the output.
            while (iteratorPDFReader.hasNext()) {
                PdfReader pdfReader = iteratorPDFReader.next();

                // Create a new page in the target for each source page.
                while (pageOfCurrentReaderPDF < pdfReader.getNumberOfPages()) {
                    document.newPage();
                    pageOfCurrentReaderPDF++;
                    currentPageNumber++;
                    page = writer.getImportedPage(pdfReader,
                            pageOfCurrentReaderPDF);
                    cb.addTemplate(page, 0, 0);

                    // Code for pagination.
                    if (paginate) {
                        cb.beginText();
                        cb.setFontAndSize(bf, 9);
                        cb.showTextAligned(PdfContentByte.ALIGN_CENTER, ""
                                        + currentPageNumber + " of " + totalPages, 520,
                                5, 0);
                        cb.endText();
                    }
                }
                pageOfCurrentReaderPDF = 0;
            }
            outputStream.flush();
            document.close();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (document.isOpen())
                document.close();
            try {
                if (outputStream != null)
                    outputStream.close();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
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
                if (SharedPreferencesUtil.getStringData(PrintOut09Activity.this, getIntent().getStringExtra("PlanId"), null) == null) {
                    getNum();
                } else {
                    if (progressDialog == null) {
                        progressDialog = CustomProgressDialog.createDialog(PrintOut09Activity.this);
                    }
                    progressDialog.show();
                    DocumentNumber = SharedPreferencesUtil.getStringData(PrintOut09Activity.this, getIntent().getStringExtra("PlanId"), null);
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
     * 生成PDF
     */
    protected void makePDF() {
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        sdf1 = new SimpleDateFormat("yyyy", Locale.CHINA);
        checkmap.put("yyyy", sdf1.format(curDate));
        sdf2 = new SimpleDateFormat("MM", Locale.CHINA);
        checkmap.put("mm", sdf2.format(curDate));
        sdf3 = new SimpleDateFormat("dd", Locale.CHINA);
        checkmap.put("dd", sdf3.format(curDate));
        bumen1 = etPrintOut03.getText().toString();
        checkmap.put("bumen1", bumen1);
        bumen2 = etPrintOut04.getText().toString();
        checkmap.put("bumen2", bumen2);
        bumen3 = etPrintOut05.getText().toString();
        checkmap.put("bumen3", bumen3);
        checkmap.put("business", etPrintOut06.getText().toString());
        String num = "(" + bumen1 + ")安监管现决[" + checkmap.get("yyyy") + "]" +
                getIntent().getStringExtra("BusinessType") + DocumentNumber + "号";
        int s = 1;
        s = s + (arr.length - 1) / 4;
        checkmap.put("s", s + "");
        checkmap.put("i", "1");
        checkmap.put("a_2", etPrintOut08.getText().toString());
        checkmap.put("num", num);
        if (s == 1) {
            String a0_1 = arr[0];
            String a0_2 = arr1[0];
            checkmap.put("0", arr[0]);
            checkmap.put("4", arr1[0]);
            for (int i = 1; i < arr.length; i++) {
                a0_1 = a0_1 + "\n" + arr[i];
                checkmap.put(i + "", arr[i]);
            }
            for (int i = 4; i < arr1.length + 4; i++) {
                checkmap.put(i + "", arr1[i - 4]);
            }


        } else {
            String a0_1 = arr[0];
            String a0_2 = arr1[0];
            checkmap.put("0", arr[0]);
            checkmap.put("4", arr1[0]);
            for (int i = 1; i < 4; i++) {
                a0_1 = a0_1 + "\n" + arr[i];
                checkmap.put(i + "", arr[i]);
            }
            if (arr1.length > 3) {
                for (int i = 4; i < 7; i++) {
                    checkmap.put(i + "", arr1[i - 4]);
                }

            } else {
                for (int i = 4; i < arr1.length + 4; i++) {
                    checkmap.put(i + "", arr1[i - 4]);
                }
            }
        }
//                Log.d("文书内容数据",checkmap.get("a_2"));
        newrectifyPath = "/mnt/sdcard/00_linshiwenshu/linshi_xcclcsjds/" + checkmap.get("business") +
                checkmap.get("yyyy") + checkmap.get("mm") + checkmap.get("dd") + ".pdf";
        FileUtils.deleteFile(newrectifyPath);
//        readpdfandFillData(rectifyPath, checkmap, newrectifyPath);
        CommonUtils.readpdfandFillData(bos, fos, newrectifyPath, ps, bf, fontList, fields, rectifyPath, checkmap);
        for (int y = 2; y < s + 1; y++) {
            for (int n = 0; n < 7; n++) {
                checkmap.put(n + "", "");
            }
            int x = y - 1;
            if (s - y == 0) {
                checkmap.put("0", arr[x * 4]);

                for (int i = 1; i < arr.length - x * 4; i++) {
                    checkmap.put(i + "", arr[x * 4 + i]);
                }
                if (arr1.length > x * 3 && arr1.length < y * 3) {
                    checkmap.put("4", arr1[x * 3]);
                    for (int i = 4; i < arr1.length + 4 - x * 3; i++) {
                        checkmap.put(i + "", arr1[x * 3 + i - 4]);
                    }
                }


            } else {
                checkmap.put("0", arr[x * 4]);

                for (int i = 1; i < 4; i++) {
                    checkmap.put(i + "", arr[x * 4 + i]);
                }
                if (arr1.length > x * 3) {
                    checkmap.put("4", arr1[x * 3]);
                    for (int i = 4; i < 7; i++) {

                        checkmap.put(i + "", arr1[x * 3 + i - 4]);
                    }
                } else if (arr1.length > x * 3 && arr1.length < y * 3) {
                    checkmap.put("4", arr1[x * 3]);
                    for (int i = 4; i < arr1.length + 4 - x * 3; i++) {
                        checkmap.put(i + 4 + "", arr1[x * 3 + i - 4]);
                    }
                }

            }
            checkmap.put("i", y + "");
            rectifyPath = "assets/template/xcclcsjdst.pdf";
            String xcclcsjdst = "/mnt/sdcard/00_linshiwenshu/linshi_xcclcsjds/现场处理措施决定书" + DocumentNumber + ".pdf";
            FileUtils.deleteFile(xcclcsjdst);
//            readpdfandFillData(rectifyPath, checkmap, xcclcsjdst);
            CommonUtils.readpdfandFillData(bos, fos, xcclcsjdst, ps, bf, fontList, fields, rectifyPath, checkmap);
            String xcclcsjdst1 = "/mnt/sdcard/00_linshiwenshu/linshi_xcclcsjds/" + checkmap.get("business") +
                    checkmap.get("yyyy") + checkmap.get("mm") + checkmap.get("dd") + "(" + s + ").pdf";
            FileUtils.deleteFile(xcclcsjdst1);
            combinepdf(xcclcsjdst1, newrectifyPath, xcclcsjdst);
            newrectifyPath = xcclcsjdst1;

        }
        saveDate(newrectifyPath);

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
            values.put("DocumentNumber", "（" + bumen1 + "）安监管现决[" +
                    sdf.format(curDate).substring(0, 4) + "]" +
                    getIntent().getStringExtra("BusinessType") + DocumentNumber + "号");
            values.put("Problem", "具体问题：" + etPrintOut07.getText().toString() + "\n" +
                    "处罚依据：" + etPrintOut08.getText().toString() + "\n" +
                    "决定：" + etPrintOut02.getText().toString() + "\n");
            values.put("AddTime", sdf.format(curDate));
            values.put("PlanType", "执法计划");
            db.replace("ELL_Document", null, values);
            Log.d("存入文书数据", "REPLACE INTO ELL_Document VALUES('" +
                    DocumentId + "','" +
                    getIntent().getStringExtra("PlanId") + "','" +
                    "（" + bumen1 + "）安监管现决[" + sdf.format(curDate).substring(0, 4) + "]" +
                    getIntent().getStringExtra("BusinessType") + DocumentNumber + "号" + "','" +
                    "具体问题：" + etPrintOut07.getText().toString() + "\n" +
                    "处罚依据：" + etPrintOut08.getText().toString() + "\n" +
                    "决定：" + etPrintOut02.getText().toString() + "\n" + "','" +
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

                    if (aBoolean) {
                        handler.sendEmptyMessage(0);
                    } else {
                        handler.sendEmptyMessage(1);
                    }
                }
            }.start();
        } else {
            Toast.makeText(PrintOut09Activity.this, "上传失败", Toast.LENGTH_SHORT).show();
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
                DocumentId + "</DocumentId><PlanId>" +
                getIntent().getStringExtra("PlanId") + "</PlanId><DocumentNumber>" +
                DocumentNumber + "</DocumentNumber><Problem>" +
                Problem + "</Problem><AddTime>" +
                AddTime + "</AddTime><DocumentType>" +
                "9" + "</DocumentType><PlanType>" +
                "执法计划" + "</PlanType></no></data></Request>");
        properties.put("Token", "");
        Log.d("各类表上传数据", properties.toString());
        WebServiceUtils.callWebService(WEB_SERVER_URL, "DynamicInvoke", properties, new WebServiceUtils.WebServiceCallBack() {

            //WebService接口返回的数据回调到这个方法中
            @Override
            public void callBack(SoapObject result) {
                if (result != null) {
                    Log.e("各类表上传返回数据", result.toString());
                    try {
                        Object detail = result.getProperty("DynamicInvokeResult");
                        JSONObject jsonObj;
                        jsonObj = XML.toJSONObject(detail.toString());
                        if (jsonObj.getJSONObject("Response").getString("result").equals("true")) {
                            Toast.makeText(PrintOut09Activity.this, "上传成功", Toast.LENGTH_SHORT).show();

                            CommonUtils.doOpenPdf(PrintOut09Activity.this, newrectifyPath);

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
                            Toast.makeText(PrintOut09Activity.this, "此文书已上传，请不要重复上传", Toast.LENGTH_SHORT).show();

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
                        Toast.makeText(PrintOut09Activity.this, "提交失败", Toast.LENGTH_SHORT).show();
                        Log.e("JSON exception", e.getMessage());
                        e.printStackTrace();
                    }
                } else {
                    //关闭加载页
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                        progressDialog = null;
                    }
                    Toast.makeText(PrintOut09Activity.this, "网络未响应，提交失败", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    /**
     * 获取文书编号
     */
    protected void getNum() {

        if (progressDialog == null) {
            progressDialog = CustomProgressDialog.createDialog(this);
        }
        progressDialog.show();

        //添加参数
        HashMap<String, String> properties = new HashMap<>();
        properties.put("Xml", "<Request><data  code='GetDocumentCode'><no><DocumentType>9</DocumentType><CompanyId>" +
                CompanyId + "</CompanyId><BusinessType>" +
                checkmap.get("BusinessType") + "</BusinessType></no></data></Request>");
        properties.put("Token", "");
        Log.d("文书上传数据", properties.toString());
        WebServiceUtils.callWebService(WEB_SERVER_URL, "DynamicInvoke", properties, new WebServiceUtils.WebServiceCallBack() {

            //WebService接口返回的数据回调到这个方法中
            @Override
            public void callBack(SoapObject result) {
                if (result != null) {
                    Log.d("文书编号返回数据", result.toString());
                    try {
//                                       Log.d("用户名发过去的结果",""+result);
                        Object detail = result.getProperty("DynamicInvokeResult");
                        JSONObject jsonObj;
                        jsonObj = XML.toJSONObject(detail.toString());
//                                        Log.d("秘钥",jsonObj.getJSONObject("DocumentElement").getJSONObject("Table").getString("secretkey"));
                        DocumentNumber = jsonObj.getJSONObject("DocumentElement").getJSONObject("Table").getString("Column1");
                        SharedPreferencesUtil.saveStringData(PrintOut09Activity.this, getIntent().getStringExtra("PlanId"), DocumentNumber);
                        makePDF();
                    } catch (JSONException e) {
                        //关闭加载页
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                            progressDialog = null;
                        }
                        Toast.makeText(PrintOut09Activity.this, "提交失败", Toast.LENGTH_SHORT).show();
                        Log.e("JSON exception", e.getMessage());
                        e.printStackTrace();
                    }
                } else {
                    //关闭加载页
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                        progressDialog = null;
                    }
                    Toast.makeText(PrintOut09Activity.this, "网络未响应，提交失败", Toast.LENGTH_SHORT).show();

                }
            }
        });

    }

    /**
     * 设置下拉框
     */
    private void setSpinner() {

        // 初始化，只需要调用一次
        AssetsDatabaseManager.initManager(getApplication());
        // 获取管理对象，因为数据库需要通过管理对象才能够获取
        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
        // 通过管理对象获取数据库
        final SQLiteDatabase db = mg.getDatabase("users.db");
        Cursor c;
        Cursor cursor;
        try {
            // 对数据库进行操作
            c = db.rawQuery("SELECT* FROM Base_Company", null);
            cursor = db.rawQuery("SELECT* FROM Base_User WHERE UserId = ?",
                    new String[]{message});
            cursor.moveToFirst();
            // 创建一个List集合，List集合的元素是Map
            int i = 0;
            int i1 = 0;
            listItems = new ArrayList<>();
            Map<String, Object> listItem;
            while (c.moveToNext()) {
                listItem = new HashMap<>();
                if (message.length() > 0 && c.getString(c.getColumnIndex("CompanyId")).equals(cursor.getString(cursor.getColumnIndex("CompanyId")))) {
                    i1 = i;
                }
                String fullName = c.getString(c.getColumnIndex("FullName"));
                Log.e(TAG + "-setSpinner-2", "fullName为" + fullName);
                listItem.put("CompanyId", c.getString(c.getColumnIndex("CompanyId")));
                listItem.put("FullName", fullName);
                listItems.add(listItem);
                i++;
            }

            /**
             * 下拉列表 单位
             * */
            //绑定适配器和值
            SimpleAdapter simpleAdapter = new SimpleAdapter(this, listItems,
                    R.layout.login_spinner_item,
                    new String[]{"FullName"},
                    new int[]{R.id.text});
            spinProvince.setAdapter(simpleAdapter);
            spinProvince.setSelection(i1);
            spinProvince.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    try {
                        CompanyId = listItems.get(position).get("CompanyId").toString();
                        Log.e(TAG + "-setSpinner-3", "CompanyId为" + CompanyId);
                        // 对数据库进行操作
                        Cursor a = db.rawQuery("SELECT* FROM Base_Department WHERE CompanyId = ?",
                                new String[]{CompanyId});
                        // 创建一个List集合，List集合的元素是Map
                        Cursor cursor1 = db.rawQuery("SELECT* FROM Base_User WHERE UserId = ?",
                                new String[]{message});
                        cursor1.moveToFirst();
                        // 创建一个List集合，List集合的元素是Map
                        int i2 = 0;
                        int i3 = 0;
                        listItems1 = new ArrayList<>();
                        Map<String, Object> listItem;
                        while (a.moveToNext()) {
                            listItem = new HashMap<>();
                            if (message.length() > 0 && a.getString(a.getColumnIndex("DepartmentId")).equals(cursor1.getString(cursor1.getColumnIndex("DepartmentId")))) {
                                i3 = i2;
                            }
                            String fullName = a.getString(a.getColumnIndex("FullName"));
                            Log.e(TAG + "-setSpinner-4", "fullName为" + fullName);
                            listItem.put("DepartmentId", a.getString(a.getColumnIndex("DepartmentId")));
                            listItem.put("FullName", fullName);
                            listItems1.add(listItem);
                            i2++;
                        }
                        SimpleAdapter simpleAdapter = new SimpleAdapter(PrintOut09Activity.this, listItems1,
                                R.layout.login_spinner_item,
                                new String[]{"FullName"},
                                new int[]{R.id.text});
                        spinCity.setAdapter(simpleAdapter);
                        spinCity.setSelection(i3);

                        /**
                         * 下拉列表 科室
                         * */
                        spinCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                                try {
                                    Department = listItems1.get(position).get("DepartmentId").toString();
                                } catch (Exception e) {
                                    Log.e("数据库报错", e.toString());
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                        cursor1.close();
                        a.close();
                    } catch (Exception e) {
                        Log.e("数据库报错", e.toString());
                    }

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            cursor.close();
            c.close();

            // TODO: 2018/9/30 修改
           /* mBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mEditText.getText().length()>0){
                        // 初始化，只需要调用一次
                        AssetsDatabaseManager.initManager(PrintOut09Activity.this);
                        // 获取管理对象，因为数据库需要通过管理对象才能够获取
                        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
                        // 通过管理对象获取数据库
                        final SQLiteDatabase db = mg.getDatabase("users.db");
                        try {//Base_Department
                            Cursor b = db.rawQuery("SELECT* FROM Base_Company WHERE RealName like ?  AND ValidFlag = 0",
                                    new String[]{"%"+mEditText.getText().toString()+"%"});
                            // 创建一个List集合，List集合的元素是Map

                            listItems2 = new ArrayList<>();
                            Map<String, Object> listItem ;
                            while (b.moveToNext()) {
                                listItem = new HashMap<>();
                                listItem.put("CompanyId",b.getString(b.getColumnIndex("CompanyId")));
                                listItem.put("FullName",b.getString(b.getColumnIndex("FullName")));

                                String authenddate = b.getString(b.getColumnIndex("AuthEndDate"));
                                SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'+08:00'", Locale.CHINA);//小写的mm表示的是分钟
                                Date date = new Date(System.currentTimeMillis());
                                Date date1 =  sdf.parse(authenddate);
                                int i = getGapCount(date,date1);
                                if(i>0){
                                    listItems2.add(listItem);
                                }
                            }
                            SimpleAdapter simpleAdapter = new SimpleAdapter(PrintOut09Activity.this, listItems2,
                                    R.layout.login_spinner_item,
                                    new String[] {"name"},
                                    new int[] {R.id.text});
                            spinCity.setAdapter(simpleAdapter);
                            spinCity.setSelection(i3);
                            *//**
             * 下拉列表 科室
             * * *//*
                            spinCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    Department = listItems2.get(position).get("DepartmentId").toString();

//                                                RealName = listItems2.get(position).get("RealName").toString();
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });
                            b.close();
                        }catch (Exception e){
                            Log.e("数据库报错",e.toString());
                        }
                    }
                }
            });*/
/*************************************************修改结束***************************************************************************/


        } catch (Exception e) {
            Log.e("数据库报错", e.toString());
        }

    }
}
