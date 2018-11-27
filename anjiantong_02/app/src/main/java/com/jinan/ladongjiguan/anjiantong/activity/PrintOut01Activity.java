package com.jinan.ladongjiguan.anjiantong.activity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
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
import com.jinan.ladongjiguan.anjiantong.utils.DateTimePickDialogUtil;
import com.jinan.ladongjiguan.anjiantong.utils.FTPUtils;
import com.jinan.ladongjiguan.anjiantong.PublicClass.PrintOut01Dialog;
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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;


public class PrintOut01Activity extends BaseActivity implements View.OnClickListener, View.OnTouchListener {


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
    @BindView(R.id.et_print_out_01)
    EditText etPrintOut01;
    @BindView(R.id.et_print_out_02)
    EditText etPrintOut02;
    @BindView(R.id.bt_print_out_00)
    Button btPrintOut00;
    @BindView(R.id.bt_print_out_01)
    Button btPrintOut01;
    @BindView(R.id.bt_print_out_02)
    Button btPrintOut02;
    @BindView(R.id.from_time)
    TextView fromTime;
    @BindView(R.id.end_time)
    TextView endTime;
    @BindView(R.id.et_print_out_03)
    EditText etPrintOut03;
    @BindView(R.id.et_print_out_04)
    EditText etPrintOut04;
    @BindView(R.id.et_print_out_05)
    EditText etPrintOut05;
    @BindView(R.id.et_print_out_06)
    EditText etPrintOut06;
    @BindView(R.id.et_print_out_07)
    EditText etPrintOut07;
    @BindView(R.id.printf_text)
    TextView printfText;
    @BindView(R.id.bt_03)
    Button bt03;
    @BindView(R.id.bt_04)
    Button bt04;
    @BindView(R.id.et_print_out_08)
    EditText etPrintOut08;

    private ArrayList<HashMap<String, Object>> peopleList = new ArrayList<>();//执法人员表
    private Map<String, String> checkmap = new HashMap<>();
    private String DocumentId;//文书主键
    private String DocumentNumber = String.valueOf(System.currentTimeMillis());
    private String WEB_SERVER_URL;
    private CustomProgressDialog progressDialog = null;//加载页
    // 检查记录模板文集地址
    private String checkPath = "assets/template/xcjcjl.pdf";
    // 创建生成的文件地址
    private String newcheckPath = "/sdcard/00_linshiwenshu/linshi_xcjcjl/现场检查记录书" + DocumentNumber + "(临时1).pdf";
    private String CompanyId = "";
    private boolean flag;
    private FTPUtils ftpUtils = null;
    private int year_01;
    private int month_01;
    private int day_01;
    private int hour_01;
    private int minute_01;
    private int year_02;
    private int month_02;
    private int day_02;
    private int hour_02;
    private int minute_02;
    private String initEndDateTime = ""; // 初始化结束时间
    private AcroFields fields;//模板中的标签
    private PdfStamper ps;
    private OutputStream fos;
    private ByteArrayOutputStream bos;
    private BaseFont bf;
    private ArrayList<BaseFont> fontList;
    private int j = 0;//行数
    private String ImagePath = "";
    private String ImageName = "";
    private int image_num = 0;
    private int mp3_num = 0;//音频计数
    private int mp4_num = 0;//视频计数
    private String[] checkId;
    private static String TAG = PrintOut01Activity.class.getSimpleName();

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
                    Toast.makeText(PrintOut01Activity.this, "上传失败", Toast.LENGTH_SHORT).show();
                    flag = false;
                    break;
                case 2:
                    upImage(image_num);
                    image_num++;
                    break;
                case 3:
                    Log.e("上传音频计数",checkId.length+":"+mp3_num);
                    //如果最后一条隐患有音频
                    if(checkId.length==mp3_num){
                        handler.sendEmptyMessage(4);
                        break;
                    }
                    File file = new File("/sdcard/anjiantong/mp3/"+  checkId[mp3_num] + ".mp3");

                    if(checkId.length>mp3_num&&file.exists()){//如果隐患有音频
                        upMP3(checkId[mp3_num]);
                    }else if(checkId.length>mp3_num+1&&!file.exists()){//如果非最后一条隐患没有音频
                        handler.sendEmptyMessage(3);
                    }else if(checkId.length==mp3_num+1&&!file.exists()){//如果最后一条隐患没有音频
                        handler.sendEmptyMessage(4);
                    }
                    mp3_num++;
                    break;
                case 4:
                    Log.e("上传视频计数",checkId.length+":"+mp4_num);
                    //如果最后一条隐患有视频
                    if(checkId.length==mp4_num){
                        dateUp();
//                        Toast.makeText(PrintOut01Activity.this, "上传完成", Toast.LENGTH_SHORT).show();

                        break;
                    }
                    File file1 =  new File("/sdcard/anjiantong/video/" + checkId[mp4_num] + ".mp4");

                    if(checkId.length>mp4_num&&file1.exists()){//如果隐患有视频
                        upMP4(checkId[mp4_num]);
                    }else if(checkId.length>mp4_num+1&&!file1.exists()){//如果非最后一条隐患没有视频
                        handler.sendEmptyMessage(4);
                    }else if(checkId.length==mp4_num+1&&!file1.exists()){//如果最后一条隐患没有视频
                        dateUp();
//                        Toast.makeText(PrintOut01Activity.this, "上传完成", Toast.LENGTH_SHORT).show();

                    }
                    mp4_num++;
                    break;
                default:
                    break;
            }

        }

    };
    private String str;
    private String xpdf;
    private String xpdfx;
    private String xcjcjl_zuizhong;
    private String xcjcjl_zuizhongx;
    private String[] arr;
    private Boolean aBoolean;
    private String UserDefined = "0";//新增修改企业标
    private Boolean upDateBoolean = false;

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

    @Override
    protected void initView() {
        setContentView(R.layout.print_out_01_layout);
        ButterKnife.bind(this);
        titleLayout.setText("制定现场检查记录文书");
        WEB_SERVER_URL = SharedPreferencesUtil.getStringData(this, "IPString", "");
        new Thread() {
            @Override
            public void run() {
                ftpUtils = FTPUtils.getInstance();
                flag = ftpUtils.initFTPSetting(MainActivity.IP, 21, "ftpuser", "ld123456");
            }
        }.start();

        // 获取当前的年、月、日、小时、分钟
        Calendar c = Calendar.getInstance();
        year_01 = c.get(Calendar.YEAR);
        month_01 = c.get(Calendar.MONTH) + 1;
        day_01 = c.get(Calendar.DAY_OF_MONTH);
        hour_01 = c.get(Calendar.HOUR_OF_DAY);
        minute_01 = c.get(Calendar.MINUTE);
        year_02 = c.get(Calendar.YEAR);
        month_02 = c.get(Calendar.MONTH) + 1;
        day_02 = c.get(Calendar.DAY_OF_MONTH);
        hour_02 = c.get(Calendar.HOUR_OF_DAY);
        minute_02 = c.get(Calendar.MINUTE);
        initEndDateTime = year_01 + "年" + month_01 + "月" + day_01 + "日 " + hour_01 + ":" + minute_01;


        File lsws = new File("/sdcard/00_linshiwenshu/linshi_xcjcjl");
        File zsws = new File("/sdcard/00_zhengshiwenshu/zhengshi_xcjcjl");
//如果文件夹不存在则创建
        if (!lsws.exists() && !lsws.isDirectory()) {
            lsws.mkdirs();
        }
        if (!zsws.exists() && !zsws.isDirectory()) {
            zsws.mkdirs();
        }
        DocumentId = "2" + getIntent().getStringExtra("PlanId").substring(1, getIntent().getStringExtra("PlanId").length());
    }

    @Override
    protected void init() {
        /**
         * 点击事件
         * */
        examinePageBack.setOnClickListener(this);
        btPrintOut00.setOnClickListener(this);
        etPrintOut02.setText(getIntent().getStringExtra("Problems"));
        btPrintOut01.setOnClickListener(this);
        btPrintOut02.setOnClickListener(this);
        fromTime.setOnClickListener(this);
        fromTime.setOnTouchListener(this);
        endTime.setOnClickListener(this);
        endTime.setOnTouchListener(this);
        bt03.setOnClickListener(this);
        bt04.setOnClickListener(this);
        /**
         * 触摸事件
         * */
        examinePageBack.setOnTouchListener(this);

        /**
         * 获取数据
         * */
        getDate();
    }

    @Override
    public void onClick(View v) {
        if (!fromTime.getText().toString().equals("--点击选择--") && !endTime.getText().toString().equals("--点击选择--")) {
            String[] strings;
            strings = fromTime.getText().toString().split("年");
            year_01 = Integer.valueOf(strings[0]);
            strings = strings[1].split("月");
            month_01 = Integer.valueOf(strings[0]);
            strings = strings[1].split("日");
            day_01 = Integer.valueOf(strings[0]);
            strings = strings[1].split(":");
            hour_01 = Integer.valueOf(strings[0]);
            minute_01 = Integer.valueOf(strings[1]);
            strings = endTime.getText().toString().split("年");
            year_02 = Integer.valueOf(strings[0]);
            strings = strings[1].split("月");
            month_02 = Integer.valueOf(strings[0]);
            strings = strings[1].split("日");
            day_02 = Integer.valueOf(strings[0]);
            strings = strings[1].split(":");
            hour_02 = Integer.valueOf(strings[0]);
            minute_02 = Integer.valueOf(strings[1]);
        }
        checkmap.put("preyear", year_01 + "");
        checkmap.put("premonth", month_01 + "");
        checkmap.put("preday", day_01 + "");
        checkmap.put("prehour", hour_01 + "");
        checkmap.put("preminute", minute_01 + "");
        checkmap.put("lastday", day_02 + "");
        checkmap.put("lasthour", hour_02 + "");
        checkmap.put("lastminute", minute_02 + "");
        checkmap.put("bjcdw", etPrintOut03.getText().toString());
        checkmap.put("dz", etPrintOut04.getText().toString());
        checkmap.put("fzr", etPrintOut05.getText().toString());
        checkmap.put("zw", etPrintOut06.getText().toString());
        checkmap.put("lxdh", etPrintOut07.getText().toString());

        str = printfText.getText().toString();
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
                checkmap.put("y", year_02 + "");

                checkmap.put("m", month_02 + "");

                checkmap.put("d", day_02 + "");


                //模板中第一行能容纳27个字符
                //第一行单独做处理
                arr = str.split("\n");
//                Log.d("打印数据长度",arr.length+"");
                if (arr.length > 55) {
                    Toast.makeText(this, "文档容不下，请精简输入内容", Toast.LENGTH_SHORT).show();
                    return;
                }

                //对小于11行的部分填充数据
                if (arr.length <= 11) {

                    for (int i = 0; i < 11; i++) {
                        if (i < arr.length) {
                            checkmap.put(i + 1 + "", arr[i]);
                        } else {
                            checkmap.put(i + 1 + "", "");
                        }

                    }
                }
                //如果分行以后行数大于11，两页的pdf页码分别设置为1和2
                if (arr.length > 11 && arr.length <= 33) {
                    checkmap.put("s", "2");
                } else if (arr.length > 33) {
                    checkmap.put("s", "3");
                } else {
                    checkmap.put("s", "1");
                }
                checkmap.put("i", "1");

                //如果分行之后的行数大于11，加载续页的模板并填充数据
                if (arr.length > 11) {
                    //加载续页
                    final String pdfx = "assets/template/xcjcjlx.pdf";
                    final Map<String, String> datax = new HashMap<String, String>();
                    xpdf = "/sdcard/00_linshiwenshu/linshi_xcjcjl/" + etPrintOut03.getText() + DocumentNumber + "(临时2).pdf";
                    //第一页添加数据
                    for (int i = 0; i < 11; i++) {

                        checkmap.put(i + 1 + "", arr[i]);
                    }
                    //第二页添加数据
                    for (int i = 11; i < arr.length; i++) {

                        datax.put(i + 1 + "", arr[i]);

                    }
                    if (arr.length > 33) {
                        datax.put("s", "3");
                    } else {
                        datax.put("s", "2");
                    }

                    datax.put("i", "2");
                    datax.put("y", year_02 + "");
                    datax.put("m", month_02 + "");
                    datax.put("d", day_02 + "");
                    FileUtils.deleteFile(xpdf);

                    readpdfandFillData(pdfx, datax, xpdf);
                    if (arr.length > 33) {
                        //加载续页
                        final String pdfxx = "assets/template/xcjcjlxx.pdf";
                        final Map<String, String> dataxx = new HashMap<String, String>();
                        xpdfx = "/sdcard/00_linshiwenshu/linshi_xcjcjl/" + etPrintOut03.getText() + DocumentNumber + "(临时3).pdf";

                        //第三页添加数据
                        for (int i = 33; i < arr.length; i++) {

                            dataxx.put(i + 1 + "", arr[i]);

                        }
                        dataxx.put("s", "3");
                        dataxx.put("i", "3");

                        dataxx.put("y", year_02 + "");
                        dataxx.put("m", month_02 + "");
                        dataxx.put("d", day_02 + "");
                        FileUtils.deleteFile(xpdfx);

                        readpdfandFillData(pdfxx, dataxx, xpdfx);

                    }


                }


//                checkmap.put("y", year_02 + "");
//
//                checkmap.put("m", month_02 + "");
//
//                checkmap.put("d", day_02 + "");
                FileUtils.deleteFile(newcheckPath);

                readpdfandFillData(checkPath, checkmap, newcheckPath);

                if (arr.length > 11) {

                    xcjcjl_zuizhong = "/sdcard/00_linshiwenshu/linshi_xcjcjl/" + etPrintOut03.getText() + year_02 + month_02 + day_02 + DocumentNumber + "(临时).pdf";
                    FileUtils.deleteFile(xcjcjl_zuizhong);
                    combinepdf(xcjcjl_zuizhong, newcheckPath, xpdf);
                    FileUtils.deleteFile(newcheckPath);
                    FileUtils.deleteFile(xpdf);
                    if (arr.length >= 33) {
                        xcjcjl_zuizhongx = "/sdcard/00_linshiwenshu/linshi_xcjcjl/" + etPrintOut03.getText() + year_02 + month_02 + day_02 + DocumentNumber + "(临时4).pdf";
                        FileUtils.deleteFile(xcjcjl_zuizhongx);
                        combinepdf(xcjcjl_zuizhongx, xcjcjl_zuizhong, xpdfx);
                        FileUtils.deleteFile(xcjcjl_zuizhong);
                        FileUtils.deleteFile(xpdfx);
                        CommonUtils.doOpenPdf(PrintOut01Activity.this,xcjcjl_zuizhongx);
                    } else {
                        CommonUtils.doOpenPdf(PrintOut01Activity.this,xcjcjl_zuizhong);
                    }


                } else {
                    CommonUtils.doOpenPdf(PrintOut01Activity.this,newcheckPath);

                }


                break;
            case R.id.bt_print_out_02:
                if (isConnect(PrintOut01Activity.this)) {
                    checkmap.put("people_3", etPrintOut08.getText().toString());
                    String s3 = "";
                    String s4 = "";
                    for (int i = 0; i < peopleList.size(); i++) {
                        if (i == 0) {
                            s3 = peopleList.get(0).get("RealName").toString();

                        } else if (i < 6) {
                            s3 = s3 + "、" + peopleList.get(i).get("RealName").toString();
                        } else if (i == 6) {
                            s4 = peopleList.get(i).get("RealName").toString();
                        } else if (i > 6) {
                            s4 = s4 + "、" + peopleList.get(i).get("RealName").toString();
                        }
                    }
                    checkmap.put("people_1", s3);
                    checkmap.put("people_2", s4);
                    arr = str.split("\n");
                    if (arr.length > 55) {
                        Toast.makeText(PrintOut01Activity.this, "文档容不下，请精简输入内容", Toast.LENGTH_SHORT).show();
                    } else {
                        dialogDoneDelete();

                    }

                } else {
                    Toast.makeText(PrintOut01Activity.this, "请确定网络是否连接", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.from_time:
                DateTimePickDialogUtil dateTimePicKDialog = new DateTimePickDialogUtil(
                        PrintOut01Activity.this, initEndDateTime);
                dateTimePicKDialog.dateTimePicKDialog(fromTime);
                break;
            case R.id.end_time:
                DateTimePickDialogUtil dateTimePicKDialog_1 = new DateTimePickDialogUtil(
                        PrintOut01Activity.this, initEndDateTime);
                dateTimePicKDialog_1.dateTimePicKDialog(endTime);
                break;
            case R.id.bt_03:
                String s = SharedPreferencesUtil.getStringData(this, "PrintOut01", "");
                String[] strings = s.split("\\|");
                if (strings.length < 10 && strings.length >= 1) {
                    s = SharedPreferencesUtil.getStringData(this, "PrintOut01", null) +
                            "|" + etPrintOut01.getText().toString();
                } else if (strings.length < 1) {
                    s = etPrintOut01.getText().toString();
                } else {
                    s = etPrintOut01.getText().toString();
                    for (int i = 0; i < 9; i++) {
                        s = s + "|" + strings[i];
                    }
                }
                SharedPreferencesUtil.saveStringData(this, "PrintOut01", s);
                Toast.makeText(PrintOut01Activity.this, "已保存", Toast.LENGTH_SHORT).show();
                break;
            case R.id.bt_04:
                if (SharedPreferencesUtil.getStringData(this, "PrintOut01", null) != null) {
                    new PrintOut01Dialog(this).show();
                } else {
                    Toast.makeText(PrintOut01Activity.this, "暂无数据", Toast.LENGTH_SHORT).show();

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

            checkmap.put("jccs", c.getString(c.getColumnIndex("Address")));
            CompanyId = c.getString(c.getColumnIndex("CompanyId"));
            //获取安检机构信息
            Cursor cursor4 = db.rawQuery("SELECT* FROM Base_Company WHERE CompanyId = ?",
                    new String[]{c.getString(c.getColumnIndex("CompanyId"))});
            cursor4.moveToFirst();
            String[] jiebie = cursor4.getString(cursor4.getColumnIndex("FullName")).split("安");
            checkmap.put("jibie", jiebie[0]);
            CompanyId = cursor4.getString(cursor4.getColumnIndex("ParentId"));
            cursor4.close();
            //获取企业信息
            Cursor cursor = db.rawQuery("SELECT* FROM ELL_Business WHERE BusinessId = ?",
                    new String[]{c.getString(c.getColumnIndex("BusinessId"))});
            cursor.moveToFirst();
            etPrintOut03.setText(cursor.getString(cursor.getColumnIndex("BusinessName")));
            etPrintOut04.setText(cursor.getString(cursor.getColumnIndex("Address")));
            etPrintOut05.setText(cursor.getString(cursor.getColumnIndex("LegalPerson")));
            etPrintOut08.setText(cursor.getString(cursor.getColumnIndex("LegalPerson")));
            etPrintOut06.setText(cursor.getString(cursor.getColumnIndex("LegalPersonPost")));
            etPrintOut07.setText(cursor.getString(cursor.getColumnIndex("LegalPersonPhone")));
            checkmap.put("BusinessType1", cursor.getString(cursor.getColumnIndex("BusinessType1")));

            checkmap.put("BusinessType", cursor.getString(cursor.getColumnIndex("BusinessType")));
            String s = c.getString(c.getColumnIndex("EnforceAccording")) + "当日，我们在该企业安全生产负责人 " + cursor.getString(cursor.getColumnIndex("SafetyOfficer")) +
                    "的陪同下进行了安全检查，发现企业存在以下问题：";
            etPrintOut01.setText(s);
            etPrintOut01.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    String s2 = "    " + "    " + etPrintOut01.getText().toString() + "\n" +
                            etPrintOut02.getText().toString() + "(以下空白)";
                    printfText.setText(s2);
                    printfText.getViewTreeObserver().addOnGlobalLayoutListener(new OnTvGlobalLayoutListener());

                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
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
                    checkmap.put("zfry1", peopleList.get(position).get("RealName").toString());
                    checkmap.put("zfzh1", peopleList.get(position).get("Code").toString());
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            sPrintOut02.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    checkmap.put("zfry2", peopleList.get(position).get("RealName").toString());
                    checkmap.put("zfzh2", peopleList.get(position).get("Code").toString());
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            sPrintOut02.setSelection(1);
            cursor1.close();
            cursor.close();
            c.close();
            //重新制定具体问题
            String[] strings = getIntent().getStringExtra("CheckId").split(",");
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
            etPrintOut02.setText(s1);
            etPrintOut02.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    String s2 = "    " + "    " + etPrintOut01.getText().toString() + "\n" +
                            etPrintOut02.getText().toString() + "(以下空白)";
                    printfText.setText(s2);
                    printfText.getViewTreeObserver().addOnGlobalLayoutListener(new OnTvGlobalLayoutListener());
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
//            s2 = autoSplitText(printfText);
//            printfText.setText(s2);
            String s2 = "    " + "    " + etPrintOut01.getText().toString() + "\n" +
                    etPrintOut02.getText().toString() + "(以下空白)";
            printfText.setText(s2);
            printfText.getViewTreeObserver().addOnGlobalLayoutListener(new OnTvGlobalLayoutListener());
        } catch (Exception e) {
            Log.e("print01数据库报错", e.toString());
        }

    }

    /**
     * 判断网络连接
     */
    public static boolean isConnect(Context context) {
        // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
        try {
            ConnectivityManager connectivity = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity != null) {
                // 获取网络连接管理的对象
                NetworkInfo info = connectivity.getActiveNetworkInfo();
                if (info != null && info.isConnected()) {
                    // 判断当前网络是否已经连接
                    if (info.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
//            Log.v("error",e.toString());
        }
        return false;
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
        properties.put("Xml", "<Request><data  code='GetDocumentCode'><no><DocumentType>2</DocumentType><CompanyId>" +
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
                        SharedPreferencesUtil.saveStringData(PrintOut01Activity.this, getIntent().getStringExtra("PlanId"), DocumentNumber);
//                        str = etPrintOut01.getText().toString() + "\n" +
//                                etPrintOut02.getText().toString() + "(以下空白)";
//                        str = printfText.getText().toString();
//                        str = printfText.getText().toString();


                        makePDF();
                    } catch (JSONException e) {
                        //关闭加载页
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                            progressDialog = null;
                        }
                        Toast.makeText(PrintOut01Activity.this, "提交失败", Toast.LENGTH_SHORT).show();
                        Log.e("JSON exception", e.getMessage());
                        e.printStackTrace();
                    }
                } else {
                    //关闭加载页
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                        progressDialog = null;
                    }
                    Toast.makeText(PrintOut01Activity.this, "网络未响应，提交失败", Toast.LENGTH_SHORT).show();

                }
            }
        });

    }

    protected void makePDF() {
        checkmap.put("y", year_02 + "");

        checkmap.put("m", month_02 + "");

        checkmap.put("d", day_02 + "");
        String[] strings = checkmap.get("jibie").split("安");
        checkmap.put("wenshu_code", "（" + strings[0] + "）安监检记[" + year_02 + "]" + checkmap.get("BusinessType") + DocumentNumber + "号");

        //模板中第一行能容纳27个字符
//                        int sum = 27;
        //第一行单独做处理
//                        String substring = halfToFull(str.substring(0, sum));
//                        checkmap.put("0", substring);
//                        arr = formatData(sum);

        //对小于11行的部分填充数据
//                        if (j <= 11) {
        if (arr.length <= 11) {
            for (int i = 0; i < 11; i++) {
                if (i < arr.length) {
                    checkmap.put(i + 1 + "", arr[i]);
                } else {
                    checkmap.put(i + 1 + "", "");
                }

            }
        }
        //如果分行以后行数大于11，两页的pdf页码分别设置为1和2
        //如果分行以后行数大于11，两页的pdf页码分别设置为1和2
        if (arr.length > 11 && arr.length <= 33) {
            checkmap.put("s", "2");
        } else if (arr.length > 33) {
            checkmap.put("s", "3");
        } else {
            checkmap.put("s", "1");
        }
        checkmap.put("i", "1");

        //如果分行之后的行数大于11，加载续页的模板并填充数据
        if (arr.length > 11) {
            //加载续页
            final String pdfx = "assets/template/xcjcjlx.pdf";
            final Map<String, String> datax = new HashMap<String, String>();
            xpdf = "/sdcard/00_zhengshiwenshu/zhengshi_xcjcjl/" + etPrintOut03.getText() + DocumentNumber + "(2).pdf";
            //第一页添加数据
            for (int i = 0; i < 11; i++) {

                checkmap.put(i + 1 + "", arr[i]);
            }
            //第二页添加数据
            for (int i = 11; i < arr.length; i++) {

                datax.put(i + 1 + "", arr[i]);

            }
            if (arr.length > 33) {
                datax.put("s", "3");
            } else {
                datax.put("s", "2");
            }
            datax.put("i", "2");
            datax.put("y", year_02 + "");
            datax.put("m", month_02 + "");
            datax.put("d", day_02 + "");
            datax.put("people_1", checkmap.get("people_1"));
            datax.put("people_2", checkmap.get("people_2"));
            datax.put("people_3", checkmap.get("people_3"));
            FileUtils.deleteFile(xpdf);

            readpdfandFillData(pdfx, datax, xpdf);
            if (arr.length > 33) {
                //加载续页
                final String pdfxx = "assets/template/xcjcjlxx.pdf";
                final Map<String, String> dataxx = new HashMap<>();
                xpdfx = "/sdcard/00_zhengshiwenshu/zhengshi_xcjcjl/" + etPrintOut03.getText() + DocumentNumber + "(3).pdf";

                //第三页添加数据
                for (int i = 33; i < arr.length; i++) {

                    dataxx.put(i + 1 + "", arr[i]);

                }
                dataxx.put("s", "3");
                dataxx.put("i", "3");
                dataxx.put("people_1", checkmap.get("people_1"));
                dataxx.put("people_2", checkmap.get("people_2"));
                dataxx.put("people_3", checkmap.get("people_3"));
                dataxx.put("y", year_02 + "");
                dataxx.put("m", month_02 + "");
                dataxx.put("d", day_02 + "");
                FileUtils.deleteFile(xpdfx);

                readpdfandFillData(pdfxx, dataxx, xpdfx);

            }

        }


        checkmap.put("y", year_02 + "");

        checkmap.put("m", month_02 + "");

        checkmap.put("d", day_02 + "");
        newcheckPath = "/mnt/sdcard/00_zhengshiwenshu/zhengshi_xcjcjl/" + etPrintOut03.getText() + year_02 + month_02 + day_02 + DocumentNumber + "(1).pdf";
        FileUtils.deleteFile(newcheckPath);

        readpdfandFillData(checkPath, checkmap, newcheckPath);

        if (arr.length > 11) {

            xcjcjl_zuizhong = "/sdcard/00_zhengshiwenshu/zhengshi_xcjcjl/" + etPrintOut03.getText() + year_02 + month_02 + day_02 + DocumentNumber + ".pdf";
            FileUtils.deleteFile(xcjcjl_zuizhong);
            combinepdf(xcjcjl_zuizhong, newcheckPath, xpdf);
            FileUtils.deleteFile(newcheckPath);
            FileUtils.deleteFile(xpdf);
            if (arr.length >= 33) {
                xcjcjl_zuizhongx = "/dcard/00_zhengshiwenshu/zhengshi_xcjcjl/现场检查记录书" + year_02 + month_02 + day_02 + DocumentNumber + "(4).pdf";
                FileUtils.deleteFile(xcjcjl_zuizhongx);
                combinepdf(xcjcjl_zuizhongx, xcjcjl_zuizhong, xpdfx);
                FileUtils.deleteFile(xcjcjl_zuizhong);
                FileUtils.deleteFile(xpdfx);
            }
        }
        if (upDateBoolean) {
            if (arr.length > 11 && arr.length <= 33) {
                CommonUtils.doOpenPdf(PrintOut01Activity.this,xcjcjl_zuizhong);

            } else if (arr.length > 33) {
                CommonUtils.doOpenPdf(PrintOut01Activity.this,xcjcjl_zuizhongx);
            } else {
                CommonUtils.doOpenPdf(PrintOut01Activity.this,newcheckPath);
            }
            if (progressDialog != null) {
                progressDialog.dismiss();
                progressDialog = null;
            }
            Toast.makeText(PrintOut01Activity.this, "上传成功", Toast.LENGTH_SHORT).show();
            finish();
            overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
        } else {
            //保存文书
            saveDate();
        }

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
        String[] strings1 = checkmap.get("jibie").split("安");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        ContentValues values = new ContentValues();
        values.put("DocumentId", DocumentId);
        values.put("PlanId", getIntent().getStringExtra("PlanId"));
        values.put("DocumentNumber", "（" + strings1[0] + "）安监检记[" + year_02 + "]" + checkmap.get("BusinessType") + DocumentNumber + "号");
        values.put("Problem", etPrintOut02.getText().toString());
        values.put("AddTime", sdf.format(curDate));
        values.put("PlanType", "执法计划");
        db.replace("ELL_Document", null, values);
        /*隐患表（图片）*/
        String[] strings = getIntent().getStringExtra("CheckId").split(",");
        checkId = strings;
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
        businessUpDate();
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
            db.delete("ELL_EnforcementPlan", "PlanId = ?", new String[]{s});
            Toast.makeText(this, "已删除", Toast.LENGTH_SHORT).show();
            finish();
        } catch (Exception e) {
            Log.e("删除计划数据库报错", e.toString());
        }
    }

    /**
     * 上传企业信息（区分是否修改新增）
     */
    protected void businessUpDate() {
        // 初始化，只需要调用一次
        AssetsDatabaseManager.initManager(getApplication());
        // 获取管理对象，因为数据库需要通过管理对象才能够获取
        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
        // 通过管理对象获取数据库
        SQLiteDatabase db = mg.getDatabase("users.db");
//        try {
            /*计划*/
        Cursor cursor = db.rawQuery("SELECT* FROM ELL_EnforcementPlan WHERE PlanId = ?",
                new String[]{getIntent().getStringExtra("PlanId")});
        cursor.moveToFirst();
        String BusinessId = cursor.getString(cursor.getColumnIndex("BusinessId"));
        cursor.close();
        try {
            Cursor cursor1 = db.rawQuery("SELECT* FROM ELL_Business WHERE BusinessId = ?",
                    new String[]{BusinessId});
            cursor1.moveToFirst();
            UserDefined = cursor1.getString(cursor1.getColumnIndex("UserDefined"));
            if(!(UserDefined.length() >0)){
                UserDefined = "0";
            }
            cursor1.close();
        }catch (Exception e){
            Log.e("企业新增数据库报错",e.toString());
        }


        switch (UserDefined) {
            case "0":

                upLoad();
                break;
            case "1":
                businessDateUp(UserDefined, BusinessId);
                break;
            case "2":
                businessDateUp(UserDefined, BusinessId);
                break;
            default:
                break;
        }
//        }catch (Exception e){
//            Log.e("打开企业信息数据库报错",e.toString());
//        }

    }

    /**
     * 上传企业信息(区分好后进行上传)
     */
    protected void businessDateUp(String s, final String BusinessId) {
        // 初始化，只需要调用一次
        AssetsDatabaseManager.initManager(getApplication());
        // 获取管理对象，因为数据库需要通过管理对象才能够获取
        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
        // 通过管理对象获取数据库
        final SQLiteDatabase db = mg.getDatabase("users.db");
//加载页添加
        if (progressDialog == null) {
            progressDialog = CustomProgressDialog.createDialog(this);
        }
        progressDialog.show();
        Cursor cursor1 = db.rawQuery("SELECT* FROM ELL_Business WHERE BusinessId = ?",
                new String[]{BusinessId});
        cursor1.moveToFirst();
        //添加参数
        HashMap<String, String> properties = new HashMap<>();
        if (checkmap.get("BusinessType").equals("煤矿")) {
            properties.put("Xml", "<Request><data  code='upload_CoalBusiness'><no>" +
                    "<OperateKind>" + s + "</OperateKind>" +
                    "<BusinessId>" + BusinessId + "</BusinessId>" +
                    "<BusinessName>" + cursor1.getString(cursor1.getColumnIndex("BusinessName")) + "</BusinessName>" +
//                    "<Countyowned></Countyowned>" +
//                    "<Region></Region>" +
//                    "<ParentCompany></ParentCompany>" +
//                    "<EconomyNature></EconomyNature>" +
//                    "<ExploreYear></ExploreYear>" +
                    "<LegalPerson>" + cursor1.getString(cursor1.getColumnIndex("LegalPerson")) + "</LegalPerson>" +
                    "<LegalPersonPhone>" + cursor1.getString(cursor1.getColumnIndex("LegalPersonPhone")) + "</LegalPersonPhone>" +
                    "<RespPerson>" + cursor1.getString(cursor1.getColumnIndex("SafetyOfficer")) + "</RespPerson>" +
                    "<RespPersonPhone>" + cursor1.getString(cursor1.getColumnIndex("SafetyOfficerPhone")) + "</RespPersonPhone>" +
//                    "<SafetyOfficer></SafetyOfficer>" +
//                    "<SafetyOfficerPhone></SafetyOfficerPhone>" +
                    "<MineShaftCond>" + cursor1.getString(cursor1.getColumnIndex("MineShaftCond")) + "</MineShaftCond>" +
                    "<MineShaftType>" + cursor1.getString(cursor1.getColumnIndex("MineShaftType")) + "</MineShaftType>" +
                    "<GeologicalReserves>" + cursor1.getString(cursor1.getColumnIndex("GeologicalReserves")) + "</GeologicalReserves>" +
                    "<WorkableReserves>" + cursor1.getString(cursor1.getColumnIndex("WorkableReserves")) + "</WorkableReserves>" +
                    "<DesignProdCapacity>" + cursor1.getString(cursor1.getColumnIndex("DesignProdCapacity")) + "</DesignProdCapacity>" +
                    "<CheckProdCapacity>" + cursor1.getString(cursor1.getColumnIndex("CheckProdCapacity")) + "</CheckProdCapacity>" +
                    "<Area>" + cursor1.getString(cursor1.getColumnIndex("Area")) + "</Area>" +
                    "<Wthdraw>" + cursor1.getString(cursor1.getColumnIndex("Wthdraw")) + "</Wthdraw>" +
                    "<CoalType>" + cursor1.getString(cursor1.getColumnIndex("CoalType")) + "</CoalType>" +
                    "<WorkableSeam>" + cursor1.getString(cursor1.getColumnIndex("WorkableSeam")) + "</WorkableSeam>" +
                    "<ExploreCraft>" + cursor1.getString(cursor1.getColumnIndex("ExploreCraft")) + "</ExploreCraft>" +
                    "<ContractPhone>" + cursor1.getString(cursor1.getColumnIndex("ContractPhone")) + "</ContractPhone>" +
                    "<ExploreMode>" + cursor1.getString(cursor1.getColumnIndex("ExploreMode")) + "</ExploreMode>" +
                    "<RiskAppraisal>" + cursor1.getString(cursor1.getColumnIndex("RiskAppraisal")) + "</RiskAppraisal>" +
                    "<GasLevel>" + cursor1.getString(cursor1.getColumnIndex("GasLevel")) + "</GasLevel>" +
                    "<CoalSeamLevel>" + cursor1.getString(cursor1.getColumnIndex("CoalSeamLevel")) + "</CoalSeamLevel>" +
                    "<Explosion>" + cursor1.getString(cursor1.getColumnIndex("Explosion")) + "</Explosion>" +
                    "<VentilateMode>" + cursor1.getString(cursor1.getColumnIndex("VentilateMode")) + "</VentilateMode>" +
//                    "<SafeLicense></SafeLicense>" +
//                    "<SafeAuthStart></SafeAuthStart>" +
//                    "<SafeAuthEnd></SafeAuthEnd>" +
//                    "<ExpolerLicense></ExpolerLicense>" +
//                    "<ExpolerEnd></ExpolerEnd>" +
                    "<BusinessLicense>" + cursor1.getString(cursor1.getColumnIndex("RegistrationNumber")) + "</BusinessLicense>" +
//                    "<BusinessEnd></BusinessEnd>" +
//                    "<BarmasterLicense></BarmasterLicense>" +
//                    "<BarmasterEnd></BarmasterEnd>" +
                    "<ValidFlag>0</ValidFlag>" +
                    "<BusinessType>" + cursor1.getString(cursor1.getColumnIndex("BusinessType")) + "</BusinessType>" +
//                    "<LegalPersonPost></LegalPersonPost>" +
                    "<Address>" + cursor1.getString(cursor1.getColumnIndex("Address")) + "</Address>" +
//                    "<CJGCPMT></CJGCPMT>" +
//                    "<KJTFXTT></KJTFXTT>" +
                    "<UploadUserID>" + SharedPreferencesUtil.getStringData(this, "userId", "") + "</UploadUserID></no></data></Request>");

        } else {
            properties.put("Xml", "<Request><data  code='upload_Business'><no>" +
                    "<OperateKind>" + s + "</OperateKind>" +
                    "<BusinessId>" + BusinessId + "</BusinessId>" +
                    "<BusinessName>" + cursor1.getString(cursor1.getColumnIndex("BusinessName")) + "</BusinessName>" +
//                    "<Region></Region>" +
                    "<BusinessType>" + cursor1.getString(cursor1.getColumnIndex("BusinessType")) + "</BusinessType>" +
                    "<LegalPerson>" + cursor1.getString(cursor1.getColumnIndex("LegalPerson")) + "</LegalPerson>" +
                    "<LegalPersonPhone>" + cursor1.getString(cursor1.getColumnIndex("LegalPersonPhone")) + "</LegalPersonPhone>" +
//                    "<RegistrationNumber></RegistrationNumber>" +
                    "<OrgCode>" + cursor1.getString(cursor1.getColumnIndex("OrgCode")) + "</OrgCode>" +
                    "<SafetyOfficer>" + cursor1.getString(cursor1.getColumnIndex("SafetyOfficer")) + "</SafetyOfficer>" +
                    "<SafetyOfficerPhone>" + cursor1.getString(cursor1.getColumnIndex("SafetyOfficerPhone")) + "</SafetyOfficerPhone>" +
                    "<Address>" + cursor1.getString(cursor1.getColumnIndex("Address")) + "</Address>" +
                    "<ValidFlag>0</ValidFlag>" +
//                    "<EconomicType></EconomicType>" +
//                    "<Reportingunit></Reportingunit>" +
//                    "<PostalCode></PostalCode>" +
//                    "<IndustryOwner></IndustryOwner>" +
//                    "<RegulaDepart></RegulaDepart>" +
//                    "<BusinessLicense></BusinessLicense>" +
//                    "<LicenseStart></LicenseStart>" +
//                    "<LicenseEnd></LicenseEnd>" +
//                    "<LicenseScope></LicenseScope>" +
//                    "<ContractPerson></ContractPerson>" +
//                    "<ContractPhone></ContractPhone>" +
                    "<LegalPersonPost>" + cursor1.getString(cursor1.getColumnIndex("LegalPersonPost")) + "</LegalPersonPost>" +
                    "<UploadUserID>" + SharedPreferencesUtil.getStringData(this, "userId", "") + "</UploadUserID></no></data></Request>");

        }

        properties.put("Token", "");
        Log.e("企业信息上传数据", properties.toString());
        WebServiceUtils.callWebService(WEB_SERVER_URL, "DynamicInvoke", properties, new WebServiceUtils.WebServiceCallBack() {

            //WebService接口返回的数据回调到这个方法中
            @Override
            public void callBack(SoapObject result) {
                if (result != null) {
                    Log.e("企业信息上传返回数据", result.toString());
                    try {
//                                       Log.d("用户名发过去的结果",""+result);
                        Object detail = result.getProperty("DynamicInvokeResult");
                        JSONObject jsonObj;
                        jsonObj = XML.toJSONObject(detail.toString());
//                        Log.e("各类表上传返回数据", detail.toString());
//                                if(jsonObj.getJSONObject("Response").getString("result").equals("true")){
                        if (detail.toString().equals("True")) {
                            ContentValues values = new ContentValues();
                            values.put("UserDefined", "0");
                            String whereClause = "BusinessId=?";
                            String[] whereArgs = new String[]{BusinessId};
                            db.update("ELL_Business", values, whereClause, whereArgs);
                            Log.d("修改企业数据", "" + values);
                            upLoad();
                        } else {
                            if (progressDialog != null) {
                                progressDialog.dismiss();
                                progressDialog = null;
                            }
                            Toast.makeText(PrintOut01Activity.this, "上传数据失败", Toast.LENGTH_SHORT).show();

                        }
                    } catch (Exception e) {
                        //关闭加载页
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                            progressDialog = null;
                        }
                        Toast.makeText(PrintOut01Activity.this, "提交失败", Toast.LENGTH_SHORT).show();
//                        Log.e("JSON exception", e.getMessage());
                        e.printStackTrace();
                    }
                } else {
                    //关闭加载页
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                        progressDialog = null;
                    }
                    Toast.makeText(PrintOut01Activity.this, "网络未响应，提交失败", Toast.LENGTH_SHORT).show();

                }
            }
        });
        cursor1.close();
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
        /*计划表*/
        String BusinessId = "";
        String CheckType = "";
        String StatTime = "";
        String EndTime = "";
        String CompanyId = "";
        String AddUser = "";
        String Address = "";
        String EnforceAccording = "";
        String BusinessType = checkmap.get("BusinessType1");
        String OtherCompany = "";
        /*分组信息*/
        String GroupId = "";
        String Headman = "";
        String GAddress = "";
        /*组员信息*/
        String MemberId = "";
        String MGroupId = "";
        String Member = "";
        /*隐患信息*/
        String CheckId = "";
        String CheckType1 = "";
        String Subject = "";
        String CheckResult = "";
        String Dispose = "";
        String DayNum = "";
        String DisposeResult = "";
        String Problem1 = "";
        String CMemberId = "";
        String IsSelect = "";
        String YHDD = "";
        String GPDBSJ = "";
        String GPDBJB = "";
        String GPDBDW = "";
        String YHBW = "";
        String HAddTime = "";
        String YHZGQTP = "";
        /*新增需要区别计划隐患所属部门字段*/
        String DepartmentCode_Plan = "";
        String DepartmentCode_Danger = "";
        /**
         * 补充的文书字段
         * */
        String COMPANYADDRESS = SharedPreferencesUtil.getStringData(this,
                getIntent().getStringExtra("PlanId") + "COMPANYADDRESS", "");
        String REPRESENTATIVE = SharedPreferencesUtil.getStringData(this,
                getIntent().getStringExtra("PlanId") + "REPRESENTATIVE", "");
        String POST = SharedPreferencesUtil.getStringData(this,
                getIntent().getStringExtra("PlanId") + "POST", "");
        String PLACE = SharedPreferencesUtil.getStringData(this,
                getIntent().getStringExtra("PlanId") + "PLACE", "");

        String PHONE = SharedPreferencesUtil.getStringData(this,
                getIntent().getStringExtra("PlanId") + "PHONE", "");
        String START_DATE = SharedPreferencesUtil.getStringData(this,
                getIntent().getStringExtra("PlanId") + "START_DATE", "");
        String END_DATE = SharedPreferencesUtil.getStringData(this,
                getIntent().getStringExtra("PlanId") + "END_DATE", "");
        String GOVERNMENT = SharedPreferencesUtil.getStringData(this,
                getIntent().getStringExtra("PlanId") + "GOVERNMENT", "");
        String REVIEW_GOVERNMENT = SharedPreferencesUtil.getStringData(this,
                getIntent().getStringExtra("PlanId") + "REVIEW_GOVERNMENT", "");
        String COURT = SharedPreferencesUtil.getStringData(this,
                getIntent().getStringExtra("PlanId") + "COURT", "");
        String DISPOSE_PERSON_ONE = SharedPreferencesUtil.getStringData(this,
                getIntent().getStringExtra("PlanId") + "DISPOSE_PERSON_ONE", "");
        String DISPOSE_PAPERS_ONE = SharedPreferencesUtil.getStringData(this,
                getIntent().getStringExtra("PlanId") + "DISPOSE_PAPERS_ONE", "");
        String DISPOSE_PERSON_TWO = SharedPreferencesUtil.getStringData(this,
                getIntent().getStringExtra("PlanId") + "DISPOSE_PERSON_TWO", "");
        String DISPOSE_PAPERS_TWO = SharedPreferencesUtil.getStringData(this,
                getIntent().getStringExtra("PlanId") + "DISPOSE_PAPERS_TWO", "");
        String CHECK_ENTRUST = SharedPreferencesUtil.getStringData(this,
                getIntent().getStringExtra("PlanId") + "CHECK_ENTRUST", "");
        String CHECK_ENTRUST_DEPART = SharedPreferencesUtil.getStringData(this,
                getIntent().getStringExtra("PlanId") + "CHECK_ENTRUST_DEPART", "");
        String ENTRUST_DEPARTMENT_ID = SharedPreferencesUtil.getStringData(this,
                getIntent().getStringExtra("PlanId") + "ENTRUST_DEPARTMENT_ID", "");
        String CHECK_PERSON = SharedPreferencesUtil.getStringData(this,
                getIntent().getStringExtra("PlanId") + "CHECK_PERSON", "");
        String LOCALE_RESPONSIBLE = SharedPreferencesUtil.getStringData(this,
                getIntent().getStringExtra("PlanId") + "LOCALE_RESPONSIBLE", "");
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
            BusinessId = cursor.getString(cursor.getColumnIndex("BusinessId"));
            CheckType = cursor.getString(cursor.getColumnIndex("CheckType"));
            StatTime = cursor.getString(cursor.getColumnIndex("StatTime"));
            EndTime = cursor.getString(cursor.getColumnIndex("EndTime"));
            Cursor cursor5 = db.rawQuery("SELECT* FROM Base_Company WHERE CompanyId = ?",
                    new String[]{cursor.getString(cursor.getColumnIndex("CompanyId"))});
            cursor5.moveToFirst();
            CompanyId = cursor5.getString(cursor5.getColumnIndex("ParentId"));
            cursor5.close();
            AddUser = cursor.getString(cursor.getColumnIndex("AddUser"));
            Address = cursor.getString(cursor.getColumnIndex("Address"));
            EnforceAccording = cursor.getString(cursor.getColumnIndex("EnforceAccording"));
            OtherCompany = cursor.getString(cursor.getColumnIndex("OtherCompany"));
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
            Cursor cursor2 = db.rawQuery("SELECT* FROM ELL_GroupInfo WHERE PlanId = ?",
                    new String[]{getIntent().getStringExtra("PlanId")});
            int i = 0;
            int i1 = 0;

            while (cursor2.moveToNext()) {
                if (i == 0) {
                    GroupId = cursor2.getString(cursor2.getColumnIndex("GroupId"));
                    Headman = cursor2.getString(cursor2.getColumnIndex("Headman"));
                    GAddress = cursor2.getString(cursor2.getColumnIndex("Address"));
                    Cursor c = db.rawQuery("SELECT* FROM Base_User WHERE UserId = ?", new String[]{Headman});
                    c.moveToFirst();
                    DepartmentCode_Plan = c.getString(c.getColumnIndex("DepartmentId"));
                    c.close();

                } else if (i > 0) {
                    GroupId = GroupId + ";" + cursor2.getString(cursor2.getColumnIndex("GroupId"));
                    Headman = Headman + ";" + cursor2.getString(cursor2.getColumnIndex("Headman"));
                    GAddress = GAddress + ";" + cursor2.getString(cursor2.getColumnIndex("Address"));
                }
                /*组员信息*/
                Cursor cursor3 = db.rawQuery("SELECT* FROM ELL_Member WHERE GroupId = ?",
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
                i++;
            }
            cursor2.close();
            /*隐患表（问题表）*/
            String[] strings = getIntent().getStringExtra("CheckId").split(",");
            for (int i2 = 0; i2 < strings.length; i2++) {
                Cursor cursor4 = db.rawQuery("SELECT* FROM ELL_CheckInfo WHERE CheckId = ?",
                        new String[]{strings[i2]});
                cursor4.moveToFirst();
                Cursor cursor6 = db.rawQuery("SELECT* FROM ELL_Photo WHERE CheckId = ?",
                        new String[]{strings[i2]});
                if (i2 == 0) {
                    CheckId = strings[i2];
                    CheckType1 = cursor4.getString(cursor4.getColumnIndex("CheckType"));
                    Subject = cursor4.getString(cursor4.getColumnIndex("Subject"));
                    CheckResult = cursor4.getString(cursor4.getColumnIndex("CheckResult"));
                    Dispose = cursor4.getString(cursor4.getColumnIndex("Dispose"));
                    DayNum = cursor4.getString(cursor4.getColumnIndex("DayNum"));
                    DisposeResult = cursor4.getString(cursor4.getColumnIndex("DisposeResult"));
                    Problem1 = cursor4.getString(cursor4.getColumnIndex("Problem"));
                    CMemberId = cursor4.getString(cursor4.getColumnIndex("MemberId"));
                    IsSelect = cursor4.getString(cursor4.getColumnIndex("IsSelect"));
                    YHDD = cursor4.getString(cursor4.getColumnIndex("YHDD"));
                    GPDBSJ = cursor4.getString(cursor4.getColumnIndex("GPDBSJ"));
                    GPDBJB = cursor4.getString(cursor4.getColumnIndex("GPDBJB"));
                    GPDBDW = cursor4.getString(cursor4.getColumnIndex("GPDBDW"));
                    YHBW = cursor4.getString(cursor4.getColumnIndex("YHBW"));
                    HAddTime = cursor4.getString(cursor4.getColumnIndex("AddTime"));
                    String[] strings1 = CMemberId.split(",");
                    for (int i3 = 0; i3 < strings1.length; i3++) {
                        Cursor c = db.rawQuery("SELECT* FROM Base_User WHERE UserId = ?", new String[]{strings1[i3]});
                        c.moveToFirst();
                        if (i3 == 0) {
                            DepartmentCode_Danger = c.getString(c.getColumnIndex("DepartmentId"));
                        } else {
                            String[] strings2 = DepartmentCode_Danger.split(",");
                            Boolean aBoolean = true;
                            for (int i4 = 0; i4 < strings2.length; i4++) {
                                if (strings2[i4].equals(c.getString(c.getColumnIndex("DepartmentId")))) {
                                    aBoolean = false;
                                } else if (aBoolean && i4 + 1 == strings2.length && !strings2[i4].equals(c.getString(c.getColumnIndex("DepartmentId")))) {
                                    DepartmentCode_Danger = DepartmentCode_Danger + "," + c.getString(c.getColumnIndex("DepartmentId"));
                                }
                            }
                        }
                        c.close();
                    }

                    if (cursor6.getCount() != 0) {
                        int i3 = 0;
                        while (cursor6.moveToNext()){

                            if(i3 == 0){
                                ImagePath = "/sdcard/LanDong/" + cursor6.getString(cursor6.getColumnIndex("Address"));
                                ImageName = cursor6.getString(cursor6.getColumnIndex("Address"));
                                YHZGQTP = cursor6.getString(cursor6.getColumnIndex("Address")) + ".jpg";

                            }else {
                                ImagePath =ImagePath+ ";" + "/sdcard/LanDong/" + cursor6.getString(cursor6.getColumnIndex("Address"));
                                ImageName =ImageName+";"+ cursor6.getString(cursor6.getColumnIndex("Address"));

                                YHZGQTP = YHZGQTP + "," + cursor6.getString(cursor6.getColumnIndex("Address")) + ".jpg";

                            }
                            i3++;

                        }
                    } else {
                        YHZGQTP = "";
                    }

                } else if (i2 > 0) {
                    CheckId = CheckId + ";" + strings[i2];
                    CheckType1 = CheckType1 + ";" + cursor4.getString(cursor4.getColumnIndex("CheckType"));
                    Subject = Subject + ";" + cursor4.getString(cursor4.getColumnIndex("Subject"));
                    CheckResult = CheckResult + ";" + cursor4.getString(cursor4.getColumnIndex("CheckResult"));
                    Dispose = Dispose + ";" + cursor4.getString(cursor4.getColumnIndex("Dispose"));
                    DayNum = DayNum + ";" + cursor4.getString(cursor4.getColumnIndex("DayNum"));
                    DisposeResult = DisposeResult + ";" + cursor4.getString(cursor4.getColumnIndex("DisposeResult"));
                    Problem1 = Problem1 + ";" + cursor4.getString(cursor4.getColumnIndex("Problem"));
                    CMemberId = CMemberId + ";" + cursor4.getString(cursor4.getColumnIndex("MemberId"));
                    IsSelect = IsSelect + ";" + cursor4.getString(cursor4.getColumnIndex("IsSelect"));
                    YHDD = YHDD + ";" + cursor4.getString(cursor4.getColumnIndex("YHDD"));
                    GPDBSJ = GPDBSJ + ";" + cursor4.getString(cursor4.getColumnIndex("GPDBSJ"));
                    GPDBJB = GPDBJB + ";" + cursor4.getString(cursor4.getColumnIndex("GPDBJB"));
                    GPDBDW = GPDBDW + ";" + cursor4.getString(cursor4.getColumnIndex("GPDBDW"));
                    YHBW = YHBW + ";" + cursor4.getString(cursor4.getColumnIndex("YHBW"));
                    HAddTime = HAddTime + ";" + cursor4.getString(cursor4.getColumnIndex("AddTime"));
                    String DepartmentCode_Danger_01 = "";
                    String[] strings1 = cursor4.getString(cursor4.getColumnIndex("MemberId")).split(",");
                    for (int i3 = 0; i3 < strings1.length; i3++) {
                        Cursor c = db.rawQuery("SELECT* FROM Base_User WHERE UserId = ?", new String[]{strings1[i3]});
                        c.moveToFirst();
                        if (i3 == 0) {
                            DepartmentCode_Danger_01 = c.getString(c.getColumnIndex("DepartmentId"));
                        } else {
                            String[] strings2 = DepartmentCode_Danger_01.split(",");
                            Boolean aBoolean = true;
                            for (int i4 = 0; i4 < strings2.length; i4++) {
                                if (strings2[i4].equals(c.getString(c.getColumnIndex("DepartmentId")))) {
                                    aBoolean = false;
                                } else if (aBoolean && i4 + 1 == strings2.length && !strings2[i4].equals(c.getString(c.getColumnIndex("DepartmentId")))) {
                                    DepartmentCode_Danger_01 = DepartmentCode_Danger_01 + "," + c.getString(c.getColumnIndex("DepartmentId"));
                                }
                            }
                        }
                        c.close();
                    }
                    DepartmentCode_Danger = DepartmentCode_Danger + ";" + DepartmentCode_Danger_01;
                    if (ImagePath.length() > 0 && cursor6.getCount() != 0) {
                        int i3 = 0;
                        while (cursor6.moveToNext()){
                            if(i3 == 0){
                                ImagePath = ImagePath + ";" + "/sdcard/LanDong/" + cursor6.getString(cursor6.getColumnIndex("Address"));
                                ImageName = ImageName + ";" + cursor6.getString(cursor6.getColumnIndex("Address"));

                                YHZGQTP = YHZGQTP + ";" + cursor6.getString(cursor6.getColumnIndex("Address")) + ".jpg";

                            }else {
                                ImagePath = ImagePath + ";" + "/sdcard/LanDong/" + cursor6.getString(cursor6.getColumnIndex("Address"));
                                ImageName = ImageName + ";" + cursor6.getString(cursor6.getColumnIndex("Address"));

                                YHZGQTP = YHZGQTP + "," + cursor6.getString(cursor6.getColumnIndex("Address")) + ".jpg";

                            }
                            i3++;
                        }
                    } else if (ImagePath.length() == 0 & cursor6.getCount() != 0) {
                        int i3 = 0;
                        while (cursor6.moveToNext()){
                            if(i3 == 0){
                                ImagePath = "/sdcard/LanDong/" + cursor6.getString(cursor6.getColumnIndex("Address"));
                                ImageName = cursor6.getString(cursor6.getColumnIndex("Address"));
                                YHZGQTP = YHZGQTP + ";" + cursor6.getString(cursor6.getColumnIndex("Address")) + ".jpg";

                            }else {
                                ImagePath = ImagePath + ";" + "/sdcard/LanDong/" + cursor6.getString(cursor6.getColumnIndex("Address"));
                                ImageName = ImageName + ";" + cursor6.getString(cursor6.getColumnIndex("Address"));

                                YHZGQTP = YHZGQTP + "," + cursor6.getString(cursor6.getColumnIndex("Address")) + ".jpg";

                            }
                            i3++;
                        }
                    } else {
                        YHZGQTP = YHZGQTP + ";" + "";
                    }
                }

                cursor6.close();
                cursor4.close();
            }

        } catch (Exception e) {
            Log.e("数据库填补数据报错", e.toString());
        }
        COMPANYADDRESS = etPrintOut04.getText().toString();
        SharedPreferencesUtil.saveStringData(this,
                getIntent().getStringExtra("PlanId") + "COMPANYADDRESS", COMPANYADDRESS);
        REPRESENTATIVE = etPrintOut05.getText().toString();
        SharedPreferencesUtil.saveStringData(this,
                getIntent().getStringExtra("PlanId") + "REPRESENTATIVE", REPRESENTATIVE);
        PLACE = etPrintOut04.getText().toString();
        SharedPreferencesUtil.saveStringData(this,
                getIntent().getStringExtra("PlanId") + "PLACE", PLACE);
        POST = etPrintOut06.getText().toString();
        SharedPreferencesUtil.saveStringData(this,
                getIntent().getStringExtra("PlanId") + "POST", POST);
        PHONE = etPrintOut07.getText().toString();
        SharedPreferencesUtil.saveStringData(this,
                getIntent().getStringExtra("PlanId") + "PHONE", PHONE);
        START_DATE = year_01 + "-" + month_01 + "-" + day_01 + " " + hour_01 + ":" + minute_01;
        SharedPreferencesUtil.saveStringData(this,
                getIntent().getStringExtra("PlanId") + "START_DATE", START_DATE);
        END_DATE = year_02 + "-" + month_02 + "-" + day_02 + " " + hour_02 + ":" + minute_02;
        SharedPreferencesUtil.saveStringData(this,
                getIntent().getStringExtra("PlanId") + "END_DATE", END_DATE);
        DISPOSE_PERSON_ONE = checkmap.get("zfry1");
        SharedPreferencesUtil.saveStringData(this,
                getIntent().getStringExtra("PlanId") + "DISPOSE_PERSON_ONE", DISPOSE_PERSON_ONE);
        DISPOSE_PAPERS_ONE = checkmap.get("zfzh1");
        SharedPreferencesUtil.saveStringData(this,
                getIntent().getStringExtra("PlanId") + "DISPOSE_PAPERS_ONE", DISPOSE_PAPERS_ONE);
        DISPOSE_PERSON_TWO = checkmap.get("zfry2");
        SharedPreferencesUtil.saveStringData(this,
                getIntent().getStringExtra("PlanId") + "DISPOSE_PERSON_TWO", DISPOSE_PERSON_TWO);
        DISPOSE_PAPERS_TWO = checkmap.get("zfzh2");
        SharedPreferencesUtil.saveStringData(this,
                getIntent().getStringExtra("PlanId") + "DISPOSE_PAPERS_TWO", DISPOSE_PAPERS_TWO);

        CHECK_PERSON = checkmap.get("people_1") + checkmap.get("people_2");
        SharedPreferencesUtil.saveStringData(this,
                getIntent().getStringExtra("PlanId") + "CHECK_PERSON", CHECK_PERSON);
        LOCALE_RESPONSIBLE = etPrintOut08.getText().toString();
        SharedPreferencesUtil.saveStringData(this,
                getIntent().getStringExtra("PlanId") + "LOCALE_RESPONSIBLE", LOCALE_RESPONSIBLE);
        //添加参数
        HashMap<String, String> properties = new HashMap<>();
        properties.put("Xml", "<Request><data  code='add-Proc1'><no>" +
                "<GPDBSJ>" + GPDBSJ + "</GPDBSJ><GPDBJB>" +
                GPDBJB + "</GPDBJB><GPDBDW>" +
                GPDBDW + "</GPDBDW><YHBW>" +
                YHBW + "</YHBW><YHZGQTP>" +
                YHZGQTP + "</YHZGQTP><HAddTime>" +
                HAddTime + "</HAddTime><DocumentId>" +
                DocumentId + "</DocumentId><PlanId>" +
                getIntent().getStringExtra("PlanId") + "</PlanId><DocumentNumber>" +
                DocumentNumber + "</DocumentNumber><Problem>" +
                Problem + "</Problem><AddTime>" +
                AddTime + "</AddTime><BusinessId>" +
                BusinessId + "</BusinessId><CheckType>" +
                CheckType + "</CheckType><StatTime>" +
                StatTime + "</StatTime><EndTime>" +
                EndTime + "</EndTime><CompanyId>" +
                CompanyId + "</CompanyId><AddUser>" +
                AddUser + "</AddUser><Address>" +
                Address + "</Address><EnforceAccording>" +
                EnforceAccording + "</EnforceAccording><BusinessType>" +
                BusinessType + "</BusinessType><OtherCompany>" +
                OtherCompany + "</OtherCompany><DocumentType>" +
                "2" + "</DocumentType><GroupId>" +
                GroupId + "</GroupId><Headman>" +
                Headman + "</Headman><GAddress>" +
                GAddress + "</GAddress><MemberId>" +
                MemberId + "</MemberId><MGroupId>" +
                MGroupId + "</MGroupId><Member>" +
                Member + "</Member><CheckId>" +
                CheckId + "</CheckId><CheckType1>" +
                CheckType1 + "</CheckType1><Subject>" +
                Subject + "</Subject><CheckResult>" +
                CheckResult + "</CheckResult><Dispose>" +
                Dispose + "</Dispose><DayNum>" +
                DayNum + "</DayNum><DisposeResult>" +
                DisposeResult + "</DisposeResult><Problem1>" +
                Problem1 + "</Problem1><CMemberId>" +
                CMemberId + "</CMemberId><IsSelect>" +
                IsSelect + "</IsSelect><YHDD>" +
                YHDD + "</YHDD><PlanType>执法计划</PlanType><COMPANYADDRESS>" +
                COMPANYADDRESS + "</COMPANYADDRESS><REPRESENTATIVE>" +
                REPRESENTATIVE + "</REPRESENTATIVE><POST>" +
                POST + "</POST><PHONE>" +
                PHONE + "</PHONE><PLACE>" +
                PLACE + "</PLACE><START_DATE>" +
                START_DATE + "</START_DATE><END_DATE>" +
                END_DATE + "</END_DATE><GOVERNMENT>" +
                GOVERNMENT + "</GOVERNMENT><REVIEW_GOVERNMENT>" +
                REVIEW_GOVERNMENT + "</REVIEW_GOVERNMENT><COURT>" +
                COURT + "</COURT><DISPOSE_PERSON_ONE>" +
                DISPOSE_PERSON_ONE + "</DISPOSE_PERSON_ONE><DISPOSE_PAPERS_ONE>" +
                DISPOSE_PAPERS_ONE + "</DISPOSE_PAPERS_ONE><DISPOSE_PERSON_TWO>" +
                DISPOSE_PERSON_TWO + "</DISPOSE_PERSON_TWO><DISPOSE_PAPERS_TWO>" +
                DISPOSE_PAPERS_TWO + "</DISPOSE_PAPERS_TWO><CHECK_ENTRUST>" +
                CHECK_ENTRUST + "</CHECK_ENTRUST><CHECK_ENTRUST_DEPART>" +
                CHECK_ENTRUST_DEPART + "</CHECK_ENTRUST_DEPART><ENTRUST_DEPARTMENT_ID>" +
                ENTRUST_DEPARTMENT_ID + "</ENTRUST_DEPARTMENT_ID><CHECK_PERSON>" +
                CHECK_PERSON + "</CHECK_PERSON><LOCALE_RESPONSIBLE>" +
                LOCALE_RESPONSIBLE + "</LOCALE_RESPONSIBLE><DepartmentCode_Plan>" +
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
//                                Log.e("各类表上传返回数据", detail.toString());
//                                if(jsonObj.getJSONObject("Response").getString("result").equals("true")){
                        if (detail.toString().equals("True")) {
                            flag = false;
                            upDateBoolean = true;
                            checkmap.put("people_1", "");
                            checkmap.put("people_2", "");
                            checkmap.put("people_3", "");
                            makePDF();
                            SharedPreferencesUtil.saveBooleanData(PrintOut01Activity.this, getIntent().getStringExtra("PlanId")+"isDone", true);
//                            Toast.makeText(PrintOut01Activity.this, "上传各类数据表成功", Toast.LENGTH_SHORT).show();
//                                    onBackPressed();
                        } else {
                            if (progressDialog != null) {
                                progressDialog.dismiss();
                                progressDialog = null;
                            }
                            Toast.makeText(PrintOut01Activity.this, "此文书已上传，请不要重复上传", Toast.LENGTH_SHORT).show();
                            SharedPreferencesUtil.saveBooleanData(PrintOut01Activity.this, getIntent().getStringExtra("PlanId")+"isDone", true);
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
                        Toast.makeText(PrintOut01Activity.this, "提交失败", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                } else {
                    //关闭加载页
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                        progressDialog = null;
                    }
                    Toast.makeText(PrintOut01Activity.this, "网络未响应，提交失败", Toast.LENGTH_SHORT).show();
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
                    Log.e("1上传路径", newcheckPath); //输出错误消息
                    Log.e("1上传文件名", DocumentId); //输出错误消息
                    if (arr.length > 11) {
                        aBoolean = ftpUtils.uploadFile(xcjcjl_zuizhong, DocumentId + ".pdf");
                        if (arr.length > 33) {
                            aBoolean = ftpUtils.uploadFile(xcjcjl_zuizhongx, DocumentId + ".pdf");
                        }
                    } else {
                        aBoolean = ftpUtils.uploadFile(newcheckPath, DocumentId + ".pdf");//xcjcjl_zuizhong
                    }

                    if (aBoolean && ImagePath.length() == 0) {
//                        handler.sendEmptyMessage(0);
                        handler.sendEmptyMessage(3);
                    } else if (!aBoolean && ImagePath.length() == 0) {
                        handler.sendEmptyMessage(1);
                    } else if (ImagePath.length() > 0) {
                        image_num = 0;

                        handler.sendEmptyMessage(2);
                    }
                    mp3_num = 0;
                    mp4_num = 0;
                    Log.e("1上传Word", aBoolean.toString());
                }
            }.start();
        } else {
            Toast.makeText(PrintOut01Activity.this, "上传文件失败", Toast.LENGTH_SHORT).show();
        }

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
                handler.sendEmptyMessage(3);
//                handler.sendEmptyMessage(0);
            }

        } else {
            Toast.makeText(PrintOut01Activity.this, "上传图片失败", Toast.LENGTH_SHORT).show();
        }

    }
    /**
     * 上传音频
     */
    protected void upMP3(final String MP3) {
        if (flag) {
            //上传文件


            new Thread() {
                    @Override
                    public void run() {
                        Log.d( "上传音频文件名", MP3); //输出错误消息
                        if (flag) {

                            aBoolean = ftpUtils.uploadFile("/sdcard/anjiantong/mp3/"+MP3 + ".mp3", MP3 + ".mp3");
                            if (aBoolean) {
                                handler.sendEmptyMessage(3);
                            } else {
                                handler.sendEmptyMessage(1);
                            }
                        }

                        Log.d( "上传音频", aBoolean.toString());
                    }
                }.start();



        } else {
            Toast.makeText(PrintOut01Activity.this, "上传音频失败", Toast.LENGTH_SHORT).show();
        }

    }
    /**
     * 上传视频
     */
    protected void upMP4(final String MP4) {
        if (flag) {
            //上传文件


            new Thread() {
                @Override
                public void run() {
                    Log.d( "上传视频文件名", MP4); //输出错误消息
                    if (flag) {

                        aBoolean = ftpUtils.uploadFile("/sdcard/anjiantong/video/"+MP4 + ".mp4", MP4 + ".mp4");
                        if (aBoolean) {
                            handler.sendEmptyMessage(4);
                        } else {
                            handler.sendEmptyMessage(1);
                        }
                    }

                    Log.d( "上传视频", aBoolean.toString());
                }
            }.start();



        } else {
            Toast.makeText(PrintOut01Activity.this, "上传视频失败", Toast.LENGTH_SHORT).show();
        }

    }

    private void readpdfandFillData(String pdftemp, Map<String, String> data, String pdf) {
        PdfReader reader = null;
        try {
            reader = new PdfReader(pdftemp);
            bos = new ByteArrayOutputStream();
        /* 将要生成的目标PDF文件名称 */
            ps = new PdfStamper(reader, bos);
            //添加仿宋的字体
            bf = BaseFont.createFont("assets/fonts/simfang.ttf", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
            fontList = new ArrayList<BaseFont>();
            fontList.add(bf);
        /* 取出报表模板中的所有字段 */
            fields = ps.getAcroFields();
            fields.setSubstitutionFonts(fontList);

            //填充数据
            for (Iterator it = data.keySet().iterator(); it.hasNext(); ) {
                String key = (String) it.next();
                String value = data.get(key);
                fields.setField(key, value);
            }
            //插如图片
//            CommonUtils.insertImage(ps,"/sdcard/template/sign.jpg");
            // 必须要调用这个，否则文档不会生成的
            ps.setFormFlattening(true);
            ps.close();
            fos = new FileOutputStream(pdf);
            fos.write(bos.toByteArray());
            fos.flush();
            fos.close();
            bos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    //对整行的数据进行抽取，且格式化
    @NonNull
    private String[] formatData(int sum) {
        //每行设置的字符数是34，将剩下的字符串平分，获得一共的行数
        j = (str.length() - sum) % 34 == 0 ? (str.length() - sum) / 34 : (str.length() - sum) / 34 + 1;
        //使用数组保存每行的数据
        String arr[] = new String[j];
        for (int i = 0; i < j; i++) {

            if (i < j - 1) {
                //halfToFull将半角字符转化为全角，对于不生效的双引号做一个替换
                arr[i] = halfToFull(str.substring(sum + 34 * i, sum + (i + 1) * 34)).replace("“", "“  ");
            } else {
                //对最后一行单独做处理
                arr[i] = halfToFull(str.substring(sum + 34 * i, str.length())).replace("“", "“  ");
            }
            arr[i] = arr[i].replace("”", "  ” ");
        }
        return arr;
    }

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
     * 合并pdf
     */
    private void combinepdf(String combinepath, String pdf, String s) {
        try {
            List<InputStream> pdfs = new ArrayList<InputStream>();
            pdfs.add(new FileInputStream(pdf));
            pdfs.add(new FileInputStream(s));
//            String zlxqzgzls_zuizhong = "/mnt/sdcard/doc/pdf/xcjcjlzuizhongban.pdf";
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
     * 提醒是否上传
     */
    protected void dialogDoneDelete() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("请注意！");
        builder.setMessage("此文书只可上传一次，请仔细确认后再上传。是否将该计划文书上传至服务器？");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (SharedPreferencesUtil.getStringData(PrintOut01Activity.this, getIntent().getStringExtra("PlanId"), null) == null) {
                    getNum();
                } else {
                    if (progressDialog == null) {
                        progressDialog = CustomProgressDialog.createDialog(PrintOut01Activity.this);
                    }
                    progressDialog.show();
                    DocumentNumber = SharedPreferencesUtil.getStringData(PrintOut01Activity.this, getIntent().getStringExtra("PlanId"), null);
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
}
