package com.jinan.ladongjiguan.anjiantong.activity;

import android.annotation.SuppressLint;
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
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;


public class PrintOut07Activity extends BaseActivity implements View.OnClickListener, View.OnTouchListener {

    @BindView(R.id.examine_page_back)
    LinearLayout examinePageBack;
    @BindView(R.id.title_layout)
    TextView titleLayout;
    @BindView(R.id.delete_layout)
    LinearLayout deleteLayout;
    @BindView(R.id.s_print_out_01)
    Spinner sPrintOut01;
    @BindView(R.id.s_print_out_02)
    Spinner sPrintOut02;
    @BindView(R.id.et_print_out_02)
    EditText etPrintOut02;
    @BindView(R.id.bt_print_out_00)
    Button btPrintOut00;
    @BindView(R.id.bt_print_out_01)
    Button btPrintOut01;
    @BindView(R.id.et_print_out_05)
    EditText etPrintOut05;
    @BindView(R.id.bt_print_out_02)
    Button btPrintOut02;
    @BindView(R.id.tv_print_out_07)
    TextView tvPrintOut07;
    @BindView(R.id.tv_print_out_07_02)
    TextView tvPrintOut0702;
    @BindView(R.id.printf_text)
    TextView printfText;
    private ArrayList<HashMap<String, Object>> peopleList = new ArrayList<>();//执法人员表
    private Map<String, String> checkmap = new HashMap<>();
    private String DocumentId = UUID.randomUUID().toString();//文书主键
    private String DocumentNumber = String.valueOf(System.currentTimeMillis());
    private String WEB_SERVER_URL;
    private CustomProgressDialog progressDialog = null;//加载页
    // 意见书模板文集地址
    private String zgfcyjsPath = "assets/template/zgfcyjs.pdf";
    // 创建生成的文件地址
    private String newzgfcyjsPath = "/sdcard/00_linshiwenshu/linshi_zgfcyjs/整改复查意见书" + DocumentNumber + "(临时1).pdf";
    private String CompanyId = "";
    private boolean flag;
    private FTPUtils ftpUtils = null;
    private AcroFields fields;//模板中的标签
    private PdfStamper ps;
    private OutputStream fos;
    private ByteArrayOutputStream bos;
    private String xpdf;//续页的pdf
    //    private int j;//行数
    private BaseFont bf;
    private ArrayList<BaseFont> fontList;
    private String str;
    private String zgfcyjs_zuizhong;
    private SimpleDateFormat sdf1;
    private SimpleDateFormat sdf2;
    private SimpleDateFormat sdf3;
    private Cursor c;
    private Cursor cursor;
    private String zfry2_code;
    private String zfry1_code;
    private String year;
    private String month;
    private String day;
    private String businessName;
    private String year_num;
    private SimpleDateFormat sdf4;
    private SimpleDateFormat sdf5;
    private SimpleDateFormat sdf6;
    private Boolean aBoolean;
    private String ImagePath = "";
    private String ImageName = "";
    private int image_num = 0;
    @SuppressLint("HandlerLeak")
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
                    Toast.makeText(PrintOut07Activity.this, "上传失败", Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    upImage(image_num);
                    image_num++;
                    break;
                default:
                    break;
            }

        }

    };

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

    @Override
    protected void initView() {
        setContentView(R.layout.print_out_07_layout);
        ButterKnife.bind(this);
        titleLayout.setText("整改复查意见书");
        WEB_SERVER_URL = SharedPreferencesUtil.getStringData(this, "IPString", "");
        new Thread() {
            @Override
            public void run() {
                ftpUtils = FTPUtils.getInstance();
                flag = ftpUtils.initFTPSetting(MainActivity.IP, 21, "ftpuser", "ld123456");
            }
        }.start();
        etPrintOut05.setText("责令限期整改");

        File lsws = new File("/sdcard/00_linshiwenshu/linshi_zgfcyjs");
        File zsws = new File("/sdcard/00_zhengshiwenshu/zhengshi_zgfcyjs");
        //如果文件夹不存在则创建
        if (!lsws.exists() && !lsws.isDirectory()) {
            lsws.mkdirs();
        }
        if (!zsws.exists() && !zsws.isDirectory()) {
            zsws.mkdirs();
        }
        DocumentId = "3" + getIntent().getStringExtra("ReviewId").substring(1, getIntent().getStringExtra("ReviewId").length());

    }

    @Override
    protected void init() {
        examinePageBack.setOnClickListener(this);
        etPrintOut02.setText(getIntent().getStringExtra("Problems"));
        String s2 = etPrintOut02.getText().toString() + "(以下空白)";
        printfText.setText(s2);
        printfText.getViewTreeObserver().addOnGlobalLayoutListener(new OnTvGlobalLayoutListener());

        etPrintOut02.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String s2 = etPrintOut02.getText().toString() + "(以下空白)";
                printfText.setText(s2);
                printfText.getViewTreeObserver().addOnGlobalLayoutListener(new OnTvGlobalLayoutListener());

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        getDate();
        btPrintOut00.setOnClickListener(this);
        btPrintOut01.setOnClickListener(this);
        btPrintOut02.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        str = printfText.getText().toString();
        arr = str.split("\n");//该准备数据啦
        switch (v.getId()) {
            case R.id.examine_page_back://返回键
                finish();
                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                break;
            case R.id.bt_print_out_00://取消键
                finish();
                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                break;
            case R.id.bt_print_out_01://出具临时文书键
                checkmap.put("jueding", etPrintOut05.getText().toString());
                Date curDate = new Date(System.currentTimeMillis());//获取当前时间
                sdf1 = new SimpleDateFormat("yyyy");
                checkmap.put("y", sdf1.format(curDate));
                sdf2 = new SimpleDateFormat("MM");
                checkmap.put("m", sdf2.format(curDate));
                sdf3 = new SimpleDateFormat("dd");
                checkmap.put("d", sdf3.format(curDate));
                if (arr.length > 23) {
                    Toast.makeText(PrintOut07Activity.this, "请精简所述内容", Toast.LENGTH_LONG).show();
                    return;
                }
                //
                //对小于9行的部分填充数据
                if (arr.length <= 9) {

                    for (int i = 0; i < 9; i++) {
                        if (i < arr.length) {
                            checkmap.put(i + "", arr[i]);
                        } else {
                            checkmap.put(i + "", "");
                        }

                    }
                }
                //如果分行以后行数大于11，两页的pdf页码分别设置为1和2
                if (arr.length > 9) {
                    checkmap.put("s", "2");
                } else {
                    checkmap.put("s", "1");
                }
                checkmap.put("i", "1");

                //如果分行之后的行数大于11，加载续页的模板并填充数据
                if (arr.length > 9) {
                    //加载续页
                    final String pdfx = "assets/template/zgfcyjst.pdf";
                    final Map<String, String> datax = new HashMap<String, String>();
                    xpdf = "/sdcard/00_linshiwenshu/linshi_zgfcyjs/整改复查意见书" + DocumentNumber + "(临时2).pdf";
                    //第一页添加数据
                    for (int i = 0; i < 9; i++) {

                        checkmap.put(i + "", arr[i]);
                    }
                    //第二页添加数据
                    for (int i = 9; i < arr.length; i++) {

                        datax.put(i + "", arr[i]);
                    }
                    datax.put("s", "2");
                    datax.put("i", "2");
                    datax.put("y", sdf1.format(curDate));
                    datax.put("m", sdf2.format(curDate));
                    datax.put("d", sdf3.format(curDate));
                    datax.put("jueding", etPrintOut05.getText().toString());
                    datax.put("year", year);
                    datax.put("month", month);
                    datax.put("day", day);
                    datax.put("qiye_name", businessName);
                    datax.put("year_num", year_num);
                    datax.put("zfry2_code", zfry2_code);
                    datax.put("zfry1_code", zfry1_code);
                    FileUtils.deleteFile(xpdf);
//                    readpdfandFillData(pdfx, datax, xpdf);
                    CommonUtils.readpdfandFillData(bos,fos,xpdf,ps,bf,fontList,fields,pdfx, datax);
                }
                checkmap.put("y", sdf1.format(curDate));
                checkmap.put("m", sdf2.format(curDate));
                checkmap.put("d", sdf3.format(curDate));
                FileUtils.deleteFile(newzgfcyjsPath);
//                readpdfandFillData(zgfcyjsPath, checkmap, newzgfcyjsPath);
                CommonUtils.readpdfandFillData(bos,fos,newzgfcyjsPath,ps,bf,fontList,fields,zgfcyjsPath, checkmap);
                if (arr.length > 9) {
                    zgfcyjs_zuizhong = "/sdcard/00_linshiwenshu/linshi_zgfcyjs/整改复查意见书" + DocumentNumber + "(临时).pdf";
                    FileUtils.deleteFile(zgfcyjs_zuizhong);
                    combinepdf(zgfcyjs_zuizhong, newzgfcyjsPath);
                    FileUtils.deleteFile(newzgfcyjsPath);
                    FileUtils.deleteFile(xpdf);
                    CommonUtils.doOpenPdf(PrintOut07Activity.this,zgfcyjs_zuizhong);

                } else {
                    CommonUtils.doOpenPdf(PrintOut07Activity.this,newzgfcyjsPath);

                }
                break;
            case R.id.bt_print_out_02:
                //判断网络连接
                if (CommonUtils.isNetworkConnected(PrintOut07Activity.this)) {
                    if (arr.length > 23) {
                        Toast.makeText(PrintOut07Activity.this, "请精简所述内容", Toast.LENGTH_LONG).show();
                    } else {

                        dialogDoneDelete();
                    }
                } else {
                    Toast.makeText(PrintOut07Activity.this, "请确定网络是否连接", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
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
                if (SharedPreferencesUtil.getStringData(PrintOut07Activity.this, getIntent().getStringExtra("ReviewId"), null) == null) {
                    getNum();
                } else {
                    if (progressDialog == null) {
                        progressDialog = CustomProgressDialog.createDialog(PrintOut07Activity.this);
                    }
                    progressDialog.show();
                    DocumentNumber = SharedPreferencesUtil.getStringData(PrintOut07Activity.this, getIntent().getStringExtra("ReviewId"), null);
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

    protected void makePDF() {

        DocumentNumber = SharedPreferencesUtil.getStringData(PrintOut07Activity.this, getIntent().getStringExtra("ReviewId"), null);

        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        sdf4 = new SimpleDateFormat("yyyy");
        checkmap.put("y", sdf4.format(curDate));
        sdf5 = new SimpleDateFormat("MM");
        checkmap.put("m", sdf5.format(curDate));
        sdf6 = new SimpleDateFormat("dd");
        checkmap.put("d", sdf6.format(curDate));
        String[] strings = checkmap.get("jibie").split("安");
        newzgfcyjsPath = "/sdcard/00_zhengshiwenshu/整改复查意见正式文书/" + DocumentId + "(1).pdf";
        checkmap.put("wenshu_code", "（" + strings[0] + "）安监管复查[" +
                checkmap.get("y") + "]" + checkmap.get("BusinessType") +
                DocumentNumber + "号");
        if (arr.length > 23) {
            Toast.makeText(PrintOut07Activity.this, "请精简所述内容", Toast.LENGTH_LONG).show();
            return;
        }
        //对小于9行的部分填充数据
        if (arr.length <= 9) {

            for (int i = 0; i < 9; i++) {
                if (i < arr.length) {
                    checkmap.put(i + "", arr[i]);
                } else {
                    checkmap.put(i + "", "");
                }

            }
        }
        //如果分行以后行数大于11，两页的pdf页码分别设置为1和2
        if (arr.length > 9) {
            checkmap.put("s", "2");
        } else {
            checkmap.put("s", "1");
        }
        checkmap.put("i", "1");

        //如果分行之后的行数大于11，加载续页的模板并填充数据
        if (arr.length >= 9) {
            //加载续页
            final String pdfx = "assets/template/zgfcyjst.pdf";
            final Map<String, String> datax = new HashMap<String, String>();
            xpdf = "/sdcard/00_zhengshiwenshu/zhengshi_zgfcyjs/整改复查意见书" + DocumentNumber + "(2).pdf";
            //第一页添加数据
            for (int i = 0; i < 9; i++) {

                checkmap.put(i + "", arr[i]);
            }
            //第二页添加数据
            for (int i = 9; i < arr.length; i++) {

                datax.put(i + "", arr[i]);

            }
            datax.put("s", "2");
            datax.put("i", "2");
            datax.put("y", sdf4.format(curDate));
            datax.put("m", sdf5.format(curDate));
            datax.put("d", sdf6.format(curDate));
            datax.put("jueding", etPrintOut05.getText().toString());
            datax.put("year", year);
            datax.put("month", month);
            datax.put("day", day);
            datax.put("qiye_name", businessName);
            datax.put("year_num", year_num);
            datax.put("zfry2_code", zfry2_code);
            datax.put("zfry1_code", zfry1_code);
            FileUtils.deleteFile(xpdf);
//            readpdfandFillData(pdfx, datax, xpdf);
            CommonUtils.readpdfandFillData(bos,fos,xpdf,ps,bf,fontList,fields,pdfx, datax);
        }
        newzgfcyjsPath = "/sdcard/00_zhengshiwenshu/zhengshi_zgfcyjs/整改复查意见书" + DocumentNumber + "(1).pdf";
        FileUtils.deleteFile(newzgfcyjsPath);
//        readpdfandFillData(zgfcyjsPath, checkmap, newzgfcyjsPath);
        CommonUtils.readpdfandFillData(bos,fos,newzgfcyjsPath,ps,bf,fontList,fields,zgfcyjsPath, checkmap);

        if (arr.length > 9) {
            zgfcyjs_zuizhong = "/sdcard/00_zhengshiwenshu/zhengshi_zgfcyjs/整改复查意见书" + DocumentNumber + ".pdf";
            FileUtils.deleteFile(zgfcyjs_zuizhong);
            combinepdf(zgfcyjs_zuizhong, newzgfcyjsPath);
            FileUtils.deleteFile(newzgfcyjsPath);
            FileUtils.deleteFile(xpdf);

        }
        //保存文书
        saveDate();
//      Log.d("秘钥",jsonObj.getJSONObject("DocumentElement").getJSONObject("Table").getString("secretkey"));


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
     * 打开数据库获取数据
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
            c = db.rawQuery("SELECT* FROM ELL_ReviewInfo WHERE ReviewId = ?",
                    new String[]{getIntent().getStringExtra("ReviewId")});
            c.moveToFirst();
            year = c.getString(c.getColumnIndex("AddTime")).substring(0, 4);
            checkmap.put("year", year);
            month = c.getString(c.getColumnIndex("AddTime")).substring(5, 7);
            checkmap.put("month", month);
            day = c.getString(c.getColumnIndex("AddTime")).substring(8, 10);
            checkmap.put("day", day);
            CompanyId = c.getString(c.getColumnIndex("CompanyId"));
            //获取安检机构信息
            Cursor cursor4 = db.rawQuery("SELECT* FROM Base_Company WHERE CompanyId = ?",
                    new String[]{c.getString(c.getColumnIndex("CompanyId"))});
            cursor4.moveToFirst();
            checkmap.put("jibie", cursor4.getString(cursor4.getColumnIndex("FullName")));
            CompanyId = cursor4.getString(cursor4.getColumnIndex("ParentId"));
            cursor4.close();
            //获取企业信息
            cursor = db.rawQuery("SELECT* FROM ELL_Business WHERE BusinessId = ?",
                    new String[]{c.getString(c.getColumnIndex("BusinessId"))});
            cursor.moveToFirst();
            businessName = cursor.getString(cursor.getColumnIndex("BusinessName"));
            checkmap.put("qiye_name", businessName);
            checkmap.put("BusinessType", cursor.getString(cursor.getColumnIndex("BusinessType")));
            Log.e("企业类型数据", checkmap.get("BusinessType"));
            checkmap.put("$FUNTIONARY$", "");

            String s = "本机关于 " + checkmap.get("year") + "年 " + checkmap.get("month") + " 月 " + checkmap.get("day") + " 日作出了";

            tvPrintOut07.setText(s);
            year_num = c.getString(c.getColumnIndex("documentnumber"));
            checkmap.put("year_num", year_num);
            s = "的决定[" + checkmap.get("year_num")
                    + "]，经对你单位整改情况进行复查，提出如下意见：";
            tvPrintOut0702.setText(s);
            //获取执法人员信息
            ArrayList<HashMap<String, Object>> UserIDList = new ArrayList<>();
            Cursor cursor1 = db.rawQuery("SELECT* FROM ELL_ReviewGroupInfo WHERE ReviewId = ?",
                    new String[]{getIntent().getStringExtra("ReviewId")});
            int x = 0;
            while (cursor1.moveToNext()) {
                HashMap<String, Object> listItem = new HashMap<>();

                if (x == 0) {
                    listItem.put("UserId", cursor1.getString(cursor1.getColumnIndex("Headman")));
                    UserIDList.add(listItem);
                }
//                Log.e("查询的人员数据", listItem.toString());
                Cursor cursor2 = db.rawQuery("SELECT* FROM ELL_ReviewMember WHERE GroupId = ?",
                        new String[]{cursor1.getString(cursor1.getColumnIndex("GroupId"))});
                while (cursor2.moveToNext()) {
                    HashMap<String, Object> listItem01 = new HashMap<>();
                    listItem01.put("UserId", cursor2.getString(cursor2.getColumnIndex("Member")));
                    UserIDList.add(listItem01);
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
                    zfry1_code = peopleList.get(position).get("Code").toString();
                    checkmap.put("zfry1_code", zfry1_code);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            sPrintOut02.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    zfry2_code = peopleList.get(position).get("Code").toString();
                    checkmap.put("zfry2_code", zfry2_code);
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
            Log.e("数据库报错", e.toString());
        }
    }

    /**
     * 获取文书编号
     */
    protected void getNum() {
        if (progressDialog == null) {
            progressDialog = CustomProgressDialog.createDialog(this);
        }
        progressDialog.show();
        checkmap.put("jueding", etPrintOut05.getText().toString());
        str = etPrintOut02.getText().toString() + "(以下空白)";
        //添加参数
        HashMap<String, String> properties = new HashMap<>();
        properties.put("Xml", "<Request><data  code='GetDocumentCode'><no><DocumentType>3</DocumentType><CompanyId>" +
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
//                      Log.d("用户名发过去的结果",""+result);
                        Object detail = result.getProperty("DynamicInvokeResult");
                        JSONObject jsonObj;
                        jsonObj = XML.toJSONObject(detail.toString());
//                      Log.d("秘钥",jsonObj.getJSONObject("DocumentElement").getJSONObject("Table").getString("secretkey"));
                        DocumentNumber = jsonObj.getJSONObject("DocumentElement").getJSONObject("Table").getString("Column1");
                        SharedPreferencesUtil.saveStringData(PrintOut07Activity.this, getIntent().getStringExtra("ReviewId"), DocumentNumber);
                        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
                        sdf4 = new SimpleDateFormat("yyyy");
                        checkmap.put("y", sdf4.format(curDate));
                        sdf5 = new SimpleDateFormat("MM");
                        checkmap.put("m", sdf5.format(curDate));
                        sdf6 = new SimpleDateFormat("dd");
                        checkmap.put("d", sdf6.format(curDate));
                        String[] strings = checkmap.get("jibie").split("安");
                        newzgfcyjsPath = "/sdcard/00_正式文书/整改复查意见正式文书/" + DocumentId + "(1).pdf";
                        checkmap.put("wenshu_code", "（" + strings[0] + "）安监管复查[" +
                                checkmap.get("y") + "]" + checkmap.get("BusinessType") +
                                DocumentNumber + "号");
                        if (arr.length > 23) {
                            Toast.makeText(PrintOut07Activity.this, "请精简所述内容", Toast.LENGTH_LONG).show();
                            return;
                        }
                        //对小于9行的部分填充数据
                        if (arr.length <= 9) {

                            for (int i = 0; i < 9; i++) {
                                if (i < arr.length) {
                                    checkmap.put(i + "", arr[i]);
                                } else {
                                    checkmap.put(i + "", "");
                                }

                            }
                        }
                        //如果分行以后行数大于11，两页的pdf页码分别设置为1和2
                        if (arr.length > 9) {
                            checkmap.put("s", "2");
                        } else {
                            checkmap.put("s", "1");
                        }
                        checkmap.put("i", "1");

                        //如果分行之后的行数大于11，加载续页的模板并填充数据
                        if (arr.length >= 9) {
                            //加载续页
                            final String pdfx = "assets/template/zgfcyjst.pdf";
                            final Map<String, String> datax = new HashMap<String, String>();
                            xpdf = "/sdcard/00_zhengshiwenshu/zhengshi_zgfcyjs/整改复查意见书" + DocumentNumber + "(2).pdf";
                            //第一页添加数据
                            for (int i = 0; i < 9; i++) {

                                checkmap.put(i + "", arr[i]);
                            }
                            //第二页添加数据
                            for (int i = 9; i < arr.length; i++) {

                                datax.put(i + "", arr[i]);

                            }

                            datax.put("s", "2");
                            datax.put("i", "2");

                            datax.put("y", sdf4.format(curDate));
                            datax.put("m", sdf5.format(curDate));
                            datax.put("d", sdf6.format(curDate));
                            datax.put("jueding", etPrintOut05.getText().toString());
                            datax.put("year", year);
                            datax.put("month", month);
                            datax.put("day", day);
                            datax.put("qiye_name", businessName);
                            datax.put("year_num", year_num);
                            datax.put("zfry2_code", zfry2_code);
                            datax.put("zfry1_code", zfry1_code);
                            FileUtils.deleteFile(xpdf);
//                            readpdfandFillData(pdfx, datax, xpdf);
                            CommonUtils.readpdfandFillData(bos,fos,xpdf,ps,bf,fontList,fields,pdfx, datax);
                        }
                        newzgfcyjsPath = "/sdcard/00_zhengshiwenshu/zhengshi_zgfcyjs/整改复查意见书" + DocumentNumber + "(1).pdf";
                        FileUtils.deleteFile(newzgfcyjsPath);

//                        readpdfandFillData(zgfcyjsPath, checkmap, newzgfcyjsPath);
                        CommonUtils.readpdfandFillData(bos,fos,newzgfcyjsPath,ps,bf,fontList,fields,zgfcyjsPath, checkmap);

                        if (arr.length > 9) {

                            zgfcyjs_zuizhong = "/sdcard/00_zhengshiwenshu/zhengshi_zgfcyjs/整改复查意见书" + DocumentNumber + ".pdf";
                            FileUtils.deleteFile(zgfcyjs_zuizhong);
                            combinepdf(zgfcyjs_zuizhong, newzgfcyjsPath);
                            FileUtils.deleteFile(newzgfcyjsPath);
                            FileUtils.deleteFile(xpdf);
                        }
                        //保存文书
                        saveDate();
                    } catch (JSONException e) {
                        //关闭加载页
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                            progressDialog = null;
                        }
                        Toast.makeText(PrintOut07Activity.this, "提交失败", Toast.LENGTH_SHORT).show();
                        Log.e("JSON exception", e.getMessage());
                        e.printStackTrace();
                    }
                } else {
                    //关闭加载页
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                        progressDialog = null;
                    }
                    Toast.makeText(PrintOut07Activity.this, "网络未响应，提交失败", Toast.LENGTH_SHORT).show();

                }
            }
        });

    }

    /**
     * 保存文书信息到数据库
     */
    protected void saveDate() {
        // 初始化，只需要调用一次
        AssetsDatabaseManager.initManager(getApplication());
        // 获取管理对象，因为数据库需要通过管理对象才能够获取
        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
        // 通过管理对象获取数据库
        SQLiteDatabase db = mg.getDatabase("users.db");
//        try {
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        String[] strings1 = checkmap.get("jibie").split("安");
        ContentValues values = new ContentValues();
        values.put("DocumentId", DocumentId);
        values.put("PlanId", getIntent().getStringExtra("PlanId"));
        values.put("DocumentNumber",
                "（" + strings1[0] + "）安监管复查[" + checkmap.get("y") + "]" +
                        checkmap.get("BusinessType") + DocumentNumber + "号");
        values.put("Problem", etPrintOut02.getText().toString());
        values.put("AddTime", sdf.format(curDate));
        values.put("PlanType", "复查计划");
        db.replace("ELL_Document", null, values);
        Log.d("存入文书数据", "REPLACE INTO ELL_Document VALUES('" +
                DocumentId + "','" +
                getIntent().getStringExtra("ReviewId") + "','" +
                "（" + strings1[0] + "）安监管复查[" + checkmap.get("y") + "]" + checkmap.get("BusinessType") + DocumentNumber + "号" + "','" +
                getIntent().getStringExtra("Problems") + "','" +
                sdf.format(curDate) + "','" +
                "复查计划" + "')");
        //隐患表（图片）
        String[] strings = getIntent().getStringExtra("HiddenDangerId").split(",");
        for (int i2 = 0; i2 < strings.length; i2++) {

            Cursor cursor6 = db.rawQuery("SELECT* FROM ELL_Photo WHERE CheckId = ?",
                    new String[]{strings[i2]});

            if (i2 == 0) {

                if (cursor6.getCount() != 0) {
                    int i = 0;
                    while (cursor6.moveToNext()){
                        if(i==0){
                            ImagePath = "/sdcard/LanDong/" + cursor6.getString(cursor6.getColumnIndex("Address"));
                            ImageName = cursor6.getString(cursor6.getColumnIndex("Address"));

                        }else {
                            ImagePath = ImagePath+";"+"/sdcard/LanDong/" + cursor6.getString(cursor6.getColumnIndex("Address"));
                            ImageName = ImageName+";"+cursor6.getString(cursor6.getColumnIndex("Address"));

                        }
                        i++;
                    }
//                        cursor6.moveToFirst();
//                        ImagePath = "/sdcard/LanDong/" + cursor6.getString(cursor6.getColumnIndex("Address"));
//                        ImageName = cursor6.getString(cursor6.getColumnIndex("Address"));
                }

            } else if (i2 > 0) {

                if (ImagePath.length() > 0 && cursor6.getCount() != 0) {
                    while (cursor6.moveToNext()){

                        ImagePath = ImagePath+";"+"/sdcard/LanDong/" + cursor6.getString(cursor6.getColumnIndex("Address"));
                        ImageName = ImageName+";"+cursor6.getString(cursor6.getColumnIndex("Address"));

                    }
//                        cursor6.moveToFirst();
//                        ImagePath = ImagePath + ";" + "/sdcard/LanDong/" + cursor6.getString(cursor6.getColumnIndex("Address"));
//                        ImageName = ImageName + ";" + cursor6.getString(cursor6.getColumnIndex("Address"));
                } else if (ImagePath.length() == 0 & cursor6.getCount() != 0) {
                    int i = 0;
                    while (cursor6.moveToNext()){
                        if(i==0){
                            ImagePath = "/sdcard/LanDong/" + cursor6.getString(cursor6.getColumnIndex("Address"));
                            ImageName = cursor6.getString(cursor6.getColumnIndex("Address"));

                        }else {
                            ImagePath = ImagePath+";"+"/sdcard/LanDong/" + cursor6.getString(cursor6.getColumnIndex("Address"));
                            ImageName = ImageName+";"+cursor6.getString(cursor6.getColumnIndex("Address"));

                        }
                        i++;
                    }
//                        cursor6.moveToFirst();
//                        ImagePath = "/mnt/sdcard/LanDong/" + cursor6.getString(cursor6.getColumnIndex("Address"));
//                        ImageName = cursor6.getString(cursor6.getColumnIndex("Address"));
                }
            }
//            if (i2 == 0) {
//
//                if (cursor6.getCount() != 0) {
//                    cursor6.moveToFirst();
//                    ImagePath = "/sdcard/LanDong/" + cursor6.getString(cursor6.getColumnIndex("Address"));
//                    ImageName = cursor6.getString(cursor6.getColumnIndex("Address"));
//                }
//
//            } else if (i2 > 0) {
//
//                if (ImagePath.length() > 0 && cursor6.getCount() != 0) {
//                    cursor6.moveToFirst();
//                    ImagePath = ImagePath + ";" + "/sdcard/LanDong/" + cursor6.getString(cursor6.getColumnIndex("Address"));
//                    ImageName = ImageName + ";" + cursor6.getString(cursor6.getColumnIndex("Address"));
//                } else if (ImagePath.length() == 0 & cursor6.getCount() != 0) {
//                    cursor6.moveToFirst();
//                    ImagePath = "/sdcard/LanDong/" + cursor6.getString(cursor6.getColumnIndex("Address"));
//                    ImageName = cursor6.getString(cursor6.getColumnIndex("Address"));
//                }
//            }
            cursor6.close();
        }
        upLoad();//上传文书
    }

    /**
     * 上传各类表数据
     */
    protected void dateUp() {
        /**
         * 处理定义各类数据
         * */
        /*文书表*/
        String DocumentNumber = "";
        String Problem = "";
        String AddTime = "";
        /*复查计划表*/
        String RDocumentId = "";
        String BusinessId = "";
        String StatTime = "";
        String EndTime = "";
        String CompanyId = "";
        String AddUser = "";
        String Address = "";
        String RAddTime = "";
        String BusinessType = checkmap.get("BusinessType");
        /*分组信息*/
        String GroupId = "";
        String Headman = "";
        String GReviewId = "";
        /*组员信息*/
        String MemberId = "";
        String MGroupId = "";
        String Member = "";
        /*复查分组隐患*/
        String ReviewDangerInfoId = "";
        String RHiddenDangerId = "";
        String RGroupId = "";
        /*隐患信息*/
        String HiddenDangerId = "";
        String disposeresult = "";
        String YHZGHTP = "";
        /*新增需要区别计划隐患所属部门字段*/
        String DepartmentCode_Plan = "";
        String DepartmentCode_Danger = "";
        // 初始化，只需要调用一次
        AssetsDatabaseManager.initManager(getApplication());
        // 获取管理对象，因为数据库需要通过管理对象才能够获取
        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
        // 通过管理对象获取数据库
        SQLiteDatabase db = mg.getDatabase("users.db");
            /*计划*/
        Cursor cursor = db.rawQuery("SELECT* FROM ELL_ReviewInfo WHERE ReviewId = ?",
                new String[]{getIntent().getStringExtra("ReviewId")});
        cursor.moveToFirst();
        RDocumentId = cursor.getString(cursor.getColumnIndex("DocumentId"));
        BusinessId = cursor.getString(cursor.getColumnIndex("BusinessId"));
        StatTime = cursor.getString(cursor.getColumnIndex("StartTime"));
        EndTime = cursor.getString(cursor.getColumnIndex("EndTime"));
        Cursor cursor6 = db.rawQuery("SELECT* FROM Base_Company WHERE CompanyId = ?",
                new String[]{cursor.getString(cursor.getColumnIndex("CompanyId"))});
        cursor6.moveToFirst();
        CompanyId = cursor6.getString(cursor6.getColumnIndex("ParentId"));
        cursor6.close();
        AddUser = cursor.getString(cursor.getColumnIndex("AddUser"));
        Address = cursor.getString(cursor.getColumnIndex("Address"));
        RAddTime = cursor.getString(cursor.getColumnIndex("AddTime"));
        cursor.close();
            /*文书*/
        Cursor cursor1 = db.rawQuery("SELECT* FROM ELL_Document WHERE DocumentId = ?",
                new String[]{DocumentId});
        cursor1.moveToFirst();
        DocumentNumber = cursor1.getString(cursor1.getColumnIndex("DocumentNumber"));
        Problem = cursor1.getString(cursor1.getColumnIndex("Problem"));
        AddTime = cursor1.getString(cursor1.getColumnIndex("AddTime"));
        cursor1.close();
            /*分组*/
        Cursor cursor2 = db.rawQuery("SELECT* FROM ELL_ReviewGroupInfo WHERE ReviewId = ?",
                new String[]{getIntent().getStringExtra("ReviewId")});
        int i = 0;
        int i1 = 0;
        while (cursor2.moveToNext()) {
            if (i == 0) {
                GroupId = cursor2.getString(cursor2.getColumnIndex("GroupId"));
                GReviewId = getIntent().getStringExtra("ReviewId");
                Headman = cursor2.getString(cursor2.getColumnIndex("Headman"));
                Cursor c = db.rawQuery("SELECT* FROM Base_User WHERE UserId = ?", new String[]{Headman});
                c.moveToFirst();
                DepartmentCode_Plan = c.getString(c.getColumnIndex("DepartmentId"));
                c.close();
            } else if (i > 0) {
                GroupId = GroupId + ";" + cursor2.getString(cursor2.getColumnIndex("GroupId"));
                GReviewId = GReviewId + ";" + getIntent().getStringExtra("ReviewId");
                Headman = Headman + ";" + cursor2.getString(cursor2.getColumnIndex("Headman"));
            }
                /*组员信息*/
            Cursor cursor3 = db.rawQuery("SELECT* FROM ELL_ReviewMember WHERE GroupId = ?",
                    new String[]{cursor2.getString(cursor2.getColumnIndex("GroupId"))});

            while (cursor3.moveToNext()) {
                Log.d("i1的数据", i1 + "");
                if (i1 == 0) {
                    MemberId = cursor3.getString(cursor3.getColumnIndex("MemberId"));
                    MGroupId = cursor2.getString(cursor2.getColumnIndex("GroupId"));
                    Member = cursor3.getString(cursor3.getColumnIndex("Member"));
                } else if (i1 > 0) {
                    MemberId = MemberId + ";" + cursor3.getString(cursor3.getColumnIndex("MemberId"));
                    MGroupId = MGroupId + ";" + cursor2.getString(cursor2.getColumnIndex("GroupId"));
                    Member = Member + ";" + cursor3.getString(cursor3.getColumnIndex("Member"));
                }
                Log.d("MemberId的数据", MemberId);
                i1++;
            }
            cursor3.close();
                /*复查分组隐患*/
            Cursor cursor4 = db.rawQuery("SELECT* FROM ELL_ReviewDangerInfo WHERE GroupId = ?",
                    new String[]{cursor2.getString(cursor2.getColumnIndex("GroupId"))});
            int i2 = 0;
            while (cursor4.moveToNext()) {
                if(ReviewDangerInfoId.length()>0){
                    ReviewDangerInfoId = ReviewDangerInfoId + ";" + cursor4.getString(cursor4.getColumnIndex("ReviewDangerInfoId"));
                    RHiddenDangerId = RHiddenDangerId + ";" + cursor4.getString(cursor4.getColumnIndex("HiddenDangerId"));
                    RGroupId = RGroupId + ";" + cursor4.getString(cursor4.getColumnIndex("GroupId"));

                }else {
                    if (i2 == 0) {
                        ReviewDangerInfoId = cursor4.getString(cursor4.getColumnIndex("ReviewDangerInfoId"));
                        RHiddenDangerId = cursor4.getString(cursor4.getColumnIndex("HiddenDangerId"));
                        RGroupId = cursor4.getString(cursor4.getColumnIndex("GroupId"));
                    } else if (i2 > 0) {
                        ReviewDangerInfoId = ReviewDangerInfoId + ";" + cursor4.getString(cursor4.getColumnIndex("ReviewDangerInfoId"));
                        RHiddenDangerId = RHiddenDangerId + ";" + cursor4.getString(cursor4.getColumnIndex("HiddenDangerId"));
                        RGroupId = RGroupId + ";" + cursor4.getString(cursor4.getColumnIndex("GroupId"));
                    }
                }

                i2++;

            }
            cursor4.close();
            i++;
        }
        cursor2.close();
            /*隐患表（问题表）*/
        String[] strings = getIntent().getStringExtra("HiddenDangerId").split(",");
        for (int i3 = 0; i3 < strings.length; i3++) {
            Cursor cursor5 = db.rawQuery("SELECT* FROM ELL_HiddenDanger WHERE HiddenDangerId = ?",
                    new String[]{strings[i3]});
            cursor5.moveToFirst();
            Cursor cursor7 = db.rawQuery("SELECT* FROM ELL_Photo WHERE CheckId = ?",
                    new String[]{strings[i3]});
            if (i3 == 0) {
                HiddenDangerId = strings[i3];

                disposeresult = cursor5.getString(cursor5.getColumnIndex("disposeresult"));
                String[] strings1 = cursor5.getString(cursor5.getColumnIndex("AddUsers")).split(",");
                for (int i4 = 0; i4 < strings1.length; i4++) {
                    Cursor c = db.rawQuery("SELECT* FROM Base_User WHERE UserId = ?", new String[]{strings1[i3]});
                    c.moveToFirst();
                    if (i4 == 0) {
                        DepartmentCode_Danger = c.getString(c.getColumnIndex("DepartmentId"));//默认平板持有者单位为此隐患复查单位
                    }
                    c.close();
                }
                if (cursor7.getCount() != 0) {
                    int i2 = 0;
                    while (cursor7.moveToNext()){
                        if(i2 == 0){
                             if(cursor7.getString(cursor7.getColumnIndex("Address")).substring(0,1).equals("2")){
                                 ImagePath = "/sdcard/LanDong/" + cursor7.getString(cursor7.getColumnIndex("Address"));
                                 ImageName = cursor7.getString(cursor7.getColumnIndex("Address"));

                                 YHZGHTP = cursor7.getString(cursor7.getColumnIndex("Address")) + ".jpg";
                                i2++;
                            }



                        }else {
                             if(cursor7.getString(cursor7.getColumnIndex("Address")).substring(0,1).equals("2")){
                                 ImagePath = ImagePath+";"+"/sdcard/LanDong/" + cursor7.getString(cursor7.getColumnIndex("Address"));
                                 ImageName = ImageName+";"+cursor7.getString(cursor7.getColumnIndex("Address"));
                                 YHZGHTP =YHZGHTP+","+ cursor7.getString(cursor7.getColumnIndex("Address")) + ".jpg";
                                i2++;
                            }
                        }

                    }
//                    cursor7.moveToFirst();
//                    ImagePath = "/sdcard/LanDong/" + cursor7.getString(cursor7.getColumnIndex("Address"));
//                    ImageName = cursor7.getString(cursor7.getColumnIndex("Address"));
//                    YHZGHTP = cursor7.getString(cursor7.getColumnIndex("Address")) + ".jpg";
                } else {
                    YHZGHTP = "";
                }
            } else if (i3 > 0) {
                HiddenDangerId = HiddenDangerId + ";" + strings[i3];

                disposeresult = disposeresult + ";" + cursor5.getString(cursor5.getColumnIndex("disposeresult"));
                String[] strings1 = cursor5.getString(cursor5.getColumnIndex("AddUsers")).split(",");

                for (int i4 = 0; i4 < strings1.length; i4++) {
                    Cursor c = db.rawQuery("SELECT* FROM Base_User WHERE UserId = ?", new String[]{strings1[i3]});
                    c.moveToFirst();
                    if (i4 == 0) {
                        DepartmentCode_Danger = DepartmentCode_Danger+";"+c.getString(c.getColumnIndex("DepartmentId"));//默认平板持有者单位为此隐患复查单位
                    }
                    c.close();
                }
                if (ImagePath.length() > 0 && cursor7.getCount() != 0) {
                    int i2 = 0;
                    while (cursor7.moveToNext()){
                        if(i2==0){
                             if(cursor7.getString(cursor7.getColumnIndex("Address")).substring(0,1).equals("2")){
                                 ImagePath = ImagePath + ";" + "/sdcard/LanDong/" + cursor7.getString(cursor7.getColumnIndex("Address"));
                                 ImageName = ImageName + ";" + cursor7.getString(cursor7.getColumnIndex("Address"));

                                 YHZGHTP =YHZGHTP+";"+ cursor7.getString(cursor7.getColumnIndex("Address")) + ".jpg";
                                i2++;
                            }
                        }else {
                            if(cursor7.getString(cursor7.getColumnIndex("Address")).substring(0,1).equals("2")){
                                ImagePath = ImagePath + ";" + "/sdcard/LanDong/" + cursor7.getString(cursor7.getColumnIndex("Address"));
                                ImageName = ImageName + ";" + cursor7.getString(cursor7.getColumnIndex("Address"));

                                YHZGHTP =YHZGHTP+","+ cursor7.getString(cursor7.getColumnIndex("Address")) + ".jpg";
                                i2++;
                            }
                        }
                    }
//                    cursor7.moveToFirst();
//                    ImagePath = ImagePath + ";" + "/sdcard/LanDong/" + cursor7.getString(cursor7.getColumnIndex("Address"));
//                    ImageName = ImageName + ";" + cursor7.getString(cursor7.getColumnIndex("Address"));
//                    YHZGHTP = YHZGHTP + ";" + cursor7.getString(cursor7.getColumnIndex("Address")) + ".jpg";
                } else if (ImagePath.length() == 0 & cursor7.getCount() != 0) {
                    int i2 = 0;
                    while (cursor7.moveToNext()){
                        if(i2==0){
                            if(cursor7.getString(cursor7.getColumnIndex("Address")).substring(0,1).equals("2")){
                                ImagePath = "/sdcard/LanDong/" + cursor7.getString(cursor7.getColumnIndex("Address"));
                                ImageName = cursor7.getString(cursor7.getColumnIndex("Address"));

                                YHZGHTP =YHZGHTP+";"+ cursor7.getString(cursor7.getColumnIndex("Address")) + ".jpg";
                                i2++;
                            }
                        }else {
                            if(cursor7.getString(cursor7.getColumnIndex("Address")).substring(0,1).equals("2")){
                                ImagePath = ImagePath + ";"+"/sdcard/LanDong/" + cursor7.getString(cursor7.getColumnIndex("Address"));
                                ImageName = ImageName + ";" + cursor7.getString(cursor7.getColumnIndex("Address"));

                                YHZGHTP  =YHZGHTP+","+cursor7.getString(cursor7.getColumnIndex("Address")) + ".jpg";
                                i2++;
                            }
                        }

                    }
//                    cursor7.moveToFirst();
//                    ImagePath = "/sdcard/LanDong/" + cursor7.getString(cursor7.getColumnIndex("Address"));
//                    ImageName = cursor7.getString(cursor7.getColumnIndex("Address"));
//                    YHZGHTP = YHZGHTP + ";" + cursor7.getString(cursor7.getColumnIndex("Address")) + ".jpg";
                } else {
                    YHZGHTP = YHZGHTP + ";" + "";
                }
            }
            cursor7.close();
            cursor5.close();
        }

        //添加参数
        HashMap<String, String> properties = new HashMap<>();
        properties.put("Xml", "<Request><data  code='add-ReviewInfo'><no><BusinessType>" +
                BusinessType + "</BusinessType><YHZGHTP>" +
                YHZGHTP + "</YHZGHTP><DocumentId>" +
                DocumentId + "</DocumentId><PlanId>" +
                getIntent().getStringExtra("ReviewId") + "</PlanId><DocumentNumber>" +
                DocumentNumber + "</DocumentNumber><Problem>" +
                Problem + "</Problem><AddTime>" +
                AddTime + "</AddTime><DocumentType>" +
                "3" + "</DocumentType><PlanType>" +
                "复查计划" + "</PlanType><ReviewId>" +
                getIntent().getStringExtra("ReviewId") + "</ReviewId><RDocumentId>" +
                RDocumentId + "</RDocumentId><BusinessId>" +
                BusinessId + "</BusinessId><StatTime>" +
                StatTime + "</StatTime><EndTime>" +
                EndTime + "</EndTime><Address>" +
                Address + "</Address><CompanyId>" +
                CompanyId + "</CompanyId><AddUser>" +
                AddUser + "</AddUser><RAddTime>" +
                RAddTime + "</RAddTime><GroupId>" +
                GroupId + "</GroupId><GReviewId>" +
                GReviewId + "</GReviewId><Headman>" +
                Headman + "</Headman><MemberId>" +
                MemberId + "</MemberId><MGroupId>" +
                MGroupId + "</MGroupId><Member>" +
                Member + "</Member><ReviewDangerInfoId>" +
                ReviewDangerInfoId + "</ReviewDangerInfoId><RHiddenDangerId>" +
                RHiddenDangerId + "</RHiddenDangerId><RGroupId>" +
                RGroupId + "</RGroupId><HiddenDangerId>" +
                HiddenDangerId + "</HiddenDangerId><disposeresult>" +
                disposeresult + "</disposeresult><DepartmentCode_Plan>" +
                DepartmentCode_Plan + "</DepartmentCode_Plan><DepartmentCode_Danger>" +
                DepartmentCode_Danger + "</DepartmentCode_Danger></no></data></Request>");
        properties.put("Token", "");
        Log.e("各类表上传数据", properties.toString());
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
//                        Log.d("返回数据",jsonObj.getJSONObject("Response").getString("result"));
                        if (detail.toString().equals("True")) {
                            Toast.makeText(PrintOut07Activity.this, "上传成功", Toast.LENGTH_SHORT).show();
                            if (arr.length > 9) {
                                CommonUtils.doOpenPdf(PrintOut07Activity.this,zgfcyjs_zuizhong);
                            } else {
                                CommonUtils.doOpenPdf(PrintOut07Activity.this,newzgfcyjsPath);

                            }
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
                            Toast.makeText(PrintOut07Activity.this, "此文书已上传，请不要重复上传", Toast.LENGTH_SHORT).show();

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
                        Toast.makeText(PrintOut07Activity.this, "提交失败", Toast.LENGTH_SHORT).show();
                        Log.e("JSON exception", e.getMessage());
                        e.printStackTrace();
                    }
                } else {
                    //关闭加载页
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                        progressDialog = null;
                    }
                    Toast.makeText(PrintOut07Activity.this, "网络未响应，提交失败", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    /**
     * 上传文书
     */
    protected void upLoad() {
        if (flag) {
            //上传文件
            new Thread() {
                @Override
                public void run() {
//                    Log.d("上传路径",newzgfcyjsPath); //输出错误消息
//                    Log.d("上传文件名",DocumentId); //输出错误消息
                    if (arr.length > 9) {
                        aBoolean = ftpUtils.uploadFile(zgfcyjs_zuizhong, DocumentId + ".pdf");
                    } else {
                        aBoolean = ftpUtils.uploadFile(newzgfcyjsPath, DocumentId + ".pdf");
                    }
                    if (aBoolean && ImagePath.length() == 0) {
                        handler.sendEmptyMessage(0);
                    } else if (!aBoolean && ImagePath.length() == 0) {
                        handler.sendEmptyMessage(1);
                    } else if (ImagePath.length() > 0) {
                        image_num = 0;
                        handler.sendEmptyMessage(2);
                    }
//                    Log.d("上传Word",aBoolean.toString());
                }
            }.start();
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//
//                    // 上传
//                    File file = new File(newzgfcyjsPath);
//                    try {
//
//                        //单文件上传
//                        new FTP().uploadSingleFile(file, "/fff",new FTP.UploadProgressListener(){
//
//                            @Override
//                            public void onUploadProgress(String currentStep,long uploadSize,File file) {
//
//                                Log.d("上传1", currentStep);
//                                if(currentStep.equals(PrintOut01Activity.FTP_UPLOAD_SUCCESS)){
//                                    Log.d("上传2", "-----shanchuan--successful");
//                                } else if(currentStep.equals(PrintOut01Activity.FTP_UPLOAD_LOADING)){
//                                    long fize = file.length();
//                                    float num = (float)uploadSize / (float)fize;
//                                    int result = (int)(num * 100);
//                                    Log.d("上传3", "-----shangchuan---"+result + "%");
//                                }
//                            }
//                        });
//                    } catch (IOException e) {
//
//                        e.printStackTrace();
//                    }
//
//                }
//            }).start();
//            //关闭加载页
//            if (progressDialog != null) {
//                progressDialog.dismiss();
//                progressDialog = null;
//            }
        } else {
            Toast.makeText(PrintOut07Activity.this, "上传失败", Toast.LENGTH_SHORT).show();
            if (progressDialog != null) {
                progressDialog.dismiss();
                progressDialog = null;
            }
        }
    }

    /**
     * 合并pdf
     */
    private void combinepdf(String combinepath, String pdf) {
        try {
            List<InputStream> pdfs = new ArrayList<InputStream>();
            pdfs.add(new FileInputStream(pdf));
            pdfs.add(new FileInputStream(xpdf));
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

            // Create Readers for the pdfs.
            for (InputStream pdf : pdfs) {
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

            // Loop through the PDF files and add to the output.
            for (PdfReader pdfReader : readers) {
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


//    //对整行的数据进行抽取，且格式化
//    @NonNull
//    private String[] formatData(int sum) {
//        //每行设置的字符数是34，将剩下的字符串平分，获得一共的行数
//        j = (str.length() - sum) / 34 + 1;
//        //使用数组保存每行的数据
//        String arr[] = new String[j];
//        for (int i = 0; i < j; i++) {
//
//            if (i < j - 1) {
//                //halfToFull将半角字符转化为全角，对于不生效的双引号做一个替换
//                arr[i] = halfToFull(str.substring(sum + 34 * i, sum + (i + 1) * 34)).replace("“", "“  ");
//            } else {
//                //对最后一行单独做处理
//                arr[i] = halfToFull(str.substring(sum + 34 * i, str.length())).replace("“", "“  ");
//            }
//            arr[i] = arr[i].replace("”", "  ” ");
//        }
//        return arr;
//    }


    // 功能：字符串半角转换为全角
// 说明：半角空格为32,全角空格为12288.
//       其他字符半角(33-126)与全角(65281-65374)的对应关系是：均相差65248
// 输入参数：input -- 需要转换的字符串
// 输出参数：无：
// 返回值: 转换后的字符串
    public String halfToFull(String input) {
        char[] c = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == 32) //半角空格
            {
                c[i] = (char) 12288;
                continue;
            }

            //根据实际情况，过滤不需要转换的符号
            //if (c[i] == 46) //半角点号，不转换
            // continue;

            if (c[i] > 32 && c[i] < 127)    //其他符号都转换为全角
                c[i] = (char) (c[i] + 65248);
        }
        return new String(c);
    }


    /**
     * 上传图片
     */
    protected void upImage(final int num) {
        if (flag) {
            //上传文件
            final String[] strings = ImagePath.split(";");
            final String[] strings1 = ImageName.split(";");
            if (strings.length > num) {
                new Thread() {
                    @Override
                    public void run() {
                        Log.d(num + "上传图片路径", strings[num]); //输出错误消息
                        Log.d(num + "上传图片文件名", strings1[num]); //输出错误消息
                        if (flag) {

                            aBoolean = ftpUtils.uploadFile(strings[num] + ".jpg", strings1[num] + ".jpg");
                            if (aBoolean) {
                                handler.sendEmptyMessage(2);
                            } else {
                                handler.sendEmptyMessage(1);
                            }
                        }

                        Log.d(num + "上传图片", aBoolean.toString());
                    }
                }.start();

            } else {
                handler.sendEmptyMessage(0);
            }

        } else {
            Toast.makeText(PrintOut07Activity.this, "上传文件失败", Toast.LENGTH_SHORT).show();
            if (progressDialog != null) {
                progressDialog.dismiss();
                progressDialog = null;
            }
        }
    }

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
}
