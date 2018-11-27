package com.jinan.ladongjiguan.anjiantong.activity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Paint;
import android.os.Bundle;
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
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
import com.jinan.ladongjiguan.anjiantong.R;
import com.jinan.ladongjiguan.anjiantong.utils.CommonUtils;
import com.jinan.ladongjiguan.anjiantong.utils.FTPUtils;
import com.jinan.ladongjiguan.anjiantong.utils.FileUtils;
import com.jinan.ladongjiguan.anjiantong.utils.SharedPreferencesUtil;
import com.jinan.ladongjiguan.anjiantong.utils.WebServiceUtils;

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
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PrintOut02Activity extends BaseActivity implements View.OnClickListener, View.OnTouchListener {


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
    //    @BindView(R.id.tv_print_out_01)
//    TextView tvPrintOut01;
    @BindView(R.id.et_print_out_03)
    EditText etPrintOut03;
    @BindView(R.id.et_print_out_04)
    EditText etPrintOut04;
    @BindView(R.id.et_print_out_05)
    EditText etPrintOut05;
    @BindView(R.id.bt_print_out_02)
    Button btPrintOut02;
    @BindView(R.id.et_print_out_06)
    EditText etPrintOut06;
    @BindView(R.id.printf_text)
    TextView printfText;
    @BindView(R.id.et_print_out_08)
    EditText etPrintOut08;
    @BindView(R.id.spin_province)
    Spinner spinProvince;
    @BindView(R.id.print_out_02_edit)
    EditText mEditText;
    @BindView(R.id.print_out_02_btn)
    Button mBtn;
    @BindView(R.id.spin_city)
    Spinner spinCity;
    @BindView(R.id.check_box_02)
    CheckBox checkBox02;
    @BindView(R.id.l_need)
    LinearLayout lNeed;
    @BindView(R.id.spin_01)
    Spinner spin01;
    @BindView(R.id.l_department)
    LinearLayout lDepartment;
    @BindView(R.id.spin_group)
    Spinner spinGroup;
    @BindView(R.id.l_group)
    LinearLayout lGroup;
    @BindView(R.id.del_text)
    TextView delText;

    private String TAG = PrintOut02Activity.class.getSimpleName();
    private String UserDefined = "0";//新增修改企业标
    private ArrayList<HashMap<String, Object>> peopleList = new ArrayList<>();//执法人员表
    private Map<String, String> checkmap = new HashMap<>();
    private String DocumentId = UUID.randomUUID().toString();//文书主键
    private String DocumentNumber = String.valueOf(System.currentTimeMillis());
    private String WEB_SERVER_URL;
    private CustomProgressDialog progressDialog = null;//加载页
    //责令限期整改指令书 模板地址
    private String rectifyPath = "assets/template/zlxqzgzls.pdf";
    // 责令限期整改指令书创建生成的文件地址
    private String newrectifyPath = "/sdcard/00_linshiwenshu/linshi_zlxqzgzls/责令限期整改指令书" + DocumentNumber + "(临时1).pdf";
    private String CompanyId = "";
    private AcroFields fields;//模板中的标签
    private PdfStamper ps;
    private OutputStream fos;
    private ByteArrayOutputStream bos;
    private String xpdf;//续页的pdf
    private String xpdfx;//续页的pdf
    //    private int j;//行数
    private String ImagePath = "";
    private String ImageName = "";
    private int image_num = 0;
    private BaseFont bf;
    private ArrayList<BaseFont> fontList;
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
                    Toast.makeText(PrintOut02Activity.this, "文书上传失败，请重新上传", Toast.LENGTH_SHORT).show();
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
    private boolean flag;
    private FTPUtils ftpUtils = null;
    private String str;
    private SimpleDateFormat sdf1;
    private SimpleDateFormat sdf2;
    private SimpleDateFormat sdf3;
    private String rectify_zuizhong;
    private String rectify_zuizhongx;
    private Boolean aBoolean;
    private String wenshu_code;
    private String qiye_name;
    private String bumen1;
    private String bumen2;
    private String bumen3;
    private String zfyr1_code;
    private String zfry2_code;
    private String[] strings1;
    private String[] strings;
    private int checkNum = 0;
    private String zfyr1 = "";
    private String zfyr2 = "";
    private Boolean upDateBoolean = false;
    private String message = SharedPreferencesUtil.getStringData(this, "userId", "");
    private List<Map<String, Object>> listItems = new ArrayList<>();
    private List<Map<String, Object>> listItems1 = new ArrayList<>();
    private List<Map<String, Object>> listItems2 = new ArrayList<>();
    private String mFullName;
    private String mCompanyId;
    private String Department = "";
    private String DepartmentId = "";
    private int typAnInt = 0;//判断是否委托复查以及委托部门还是企业集团复查 0：不委托 1：委托部门 2：委托集团
    private String s_Group = "";//企业集团
    private String s_GroupId = "";//企业集团id
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
    private String needStr = "0";

    @Override
    protected void initView() {
        setContentView(R.layout.print_out_02_layout);
        ButterKnife.bind(this);
        titleLayout.setText("制定限期整改指令文书");
        WEB_SERVER_URL = SharedPreferencesUtil.getStringData(this, "IPString", "");
        new Thread() {
            @Override
            public void run() {
                ftpUtils = FTPUtils.getInstance();
                flag = ftpUtils.initFTPSetting(MainActivity.IP, 21, "ftpuser", "ld123456");
            }
        }.start();

        File lsws = new File("/sdcard/00_linshiwenshu/linshi_zlxqzgzls");
        File zsws = new File("/sdcard/00_zhengshiwenshu/zhengshi_zlxqzgzls");
//如果文件夹不存在则创建
        if (!lsws.exists() && !lsws.isDirectory()) {
            lsws.mkdirs();

        }
        if (!zsws.exists() && !zsws.isDirectory()) {
            zsws.mkdirs();
        }
        DocumentId = "1" + getIntent().getStringExtra("PlanId").substring(1, getIntent().getStringExtra("PlanId").length());
        checkNum = getIntent().getStringExtra("CheckId").split(",").length;
        setSpinner();
    }

    @Override
    protected void init() {
        examinePageBack.setOnClickListener(this);
        btPrintOut01.setOnClickListener(this);
        btPrintOut00.setOnClickListener(this);
        mBtn.setOnClickListener(this);
        String s = "\"" + getIntent().getStringExtra("Problems") + "\" 等" + checkNum + "条隐患（详见你单位同日现场检查记录书）作出如下处理。\n"
                + getIntent().getStringExtra("Problems_1");
        etPrintOut02.setText(s);
        etPrintOut02.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String s2 = "     " + "     " + etPrintOut02.getText().toString() + "(以下空白)";
                if (needStr.equals("1")) {
                    s2 = "     " + "     " + etPrintOut02.getText().toString() + "\n" +
                            "以上处理决定委托" + mFullName + Department + "进行督促落实。" + "(以下空白)";
                }
                printfText.setText(s2);
                printfText.getViewTreeObserver().addOnGlobalLayoutListener(new OnTvGlobalLayoutListener());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        etPrintOut01.setText(DocumentNumber);
        btPrintOut02.setOnClickListener(this);

        getDate();
        strings = getIntent().getStringExtra("DayNum").split("-");
        checkBox02.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    lNeed.setVisibility(View.VISIBLE);
                    needStr = "1";
                    String s2 = "     " + "     " + etPrintOut02.getText().toString() + "\n" +
                                "以上处理决定委托" + mFullName + Department + "进行督促落实。" + "(以下空白)";

                    printfText.setText(s2);
                    printfText.getViewTreeObserver().addOnGlobalLayoutListener(new OnTvGlobalLayoutListener());

                } else if(!isChecked){
                    lNeed.setVisibility(View.GONE);
                    needStr = "0";
                    String s2 = "     " + "     " + etPrintOut02.getText().toString() + "(以下空白)";
                    printfText.setText(s2);
                    printfText.getViewTreeObserver().addOnGlobalLayoutListener(new OnTvGlobalLayoutListener());

                }
            }
        });


    }

    @Override
    public void onClick(View v) {
        qiye_name = etPrintOut06.getText().toString();
        checkmap.put("qiye_name", qiye_name);
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
            case R.id.print_out_02_btn://查询键
                if (mEditText.getText().length() > 0) {
                    // 初始化，只需要调用一次
                    AssetsDatabaseManager.initManager(PrintOut02Activity.this);
                    // 获取管理对象，因为数据库需要通过管理对象才能够获取
                    AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
                    // 通过管理对象获取数据库
                    final SQLiteDatabase db = mg.getDatabase("users.db");
                    try {//Base_Department
                        Log.e(TAG + "-onClick-0", "mFullName为" + mFullName);
                        Cursor b = db.rawQuery("SELECT* FROM Base_Department WHERE FullName like ? AND CompanyId = ?",
                                new String[]{"%" + mEditText.getText().toString() + "%", mCompanyId});
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
                        SimpleAdapter simpleAdapter = new SimpleAdapter(PrintOut02Activity.this, listItems2,
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
                break;
            case R.id.bt_print_out_01://出具文书键
                strings1 = getIntent().getStringExtra("DayNum").split("-");
                checkmap.put("wtbh", getIntent().getStringExtra("rectify"));
                Date curDate = new Date(System.currentTimeMillis());//获取当前时间
                sdf1 = new SimpleDateFormat("yyyy");
                checkmap.put("y", sdf1.format(curDate));
                sdf2 = new SimpleDateFormat("MM");
                checkmap.put("m", sdf2.format(curDate));
                sdf3 = new SimpleDateFormat("dd");
                checkmap.put("d", sdf3.format(curDate));
                bumen1 = etPrintOut03.getText().toString();
                checkmap.put("bumen1", bumen1);
                bumen2 = etPrintOut04.getText().toString();
                checkmap.put("bumen2", bumen2);
                bumen3 = etPrintOut05.getText().toString();
                checkmap.put("bumen3", bumen3);

                int sum = 0;
                if (arr.length > 30) {
                    Toast.makeText(this, "文档容不下，请精简输入内容", Toast.LENGTH_SHORT).show();
                    return;
                }
                //对小于8行的部分填充数据
                if (arr.length <= 8) {

                    for (int i = 0; i < arr.length; i++) {

                        checkmap.put(i + "", arr[i]);
                    }
                }
                //如果分行以后行数大于11，两页的pdf页码分别设置为1和2
                if (arr.length > 8 && arr.length <= 19) {
                    checkmap.put("s", "2");
                } else if (arr.length > 19) {
                    checkmap.put("s", "3");
                } else {
                    checkmap.put("s", "1");
                }
                checkmap.put("i", "1");
                //如果分行之后的行数大于11，加载续页的模板并填充数据
                if (arr.length > 8) {
                    //加载续页
                    final String pdfx = "assets/template/zlxqzgzlst.pdf";
                    final Map<String, String> datax = new HashMap<String, String>();
                    xpdf = "/sdcard/00_linshiwenshu/linshi_zlxqzgzls/" + etPrintOut06.getText() + DocumentNumber + "(临时2).pdf";
                    //第一页添加数据
                    for (int i = 0; i < 8; i++) {

                        checkmap.put(i + "", arr[i]);
                    }
                    //第二页添加数据
                    for (int i = 8; i < arr.length; i++) {

                        datax.put(i + "", arr[i]);

                    }
                    datax.put("qiye_name", qiye_name);
                    datax.put("wtbh", getIntent().getStringExtra("rectify"));
                    datax.put("y", sdf1.format(curDate));
                    datax.put("m", sdf2.format(curDate));
                    datax.put("d", sdf3.format(curDate));
                    datax.put("bumen1", bumen1);
                    datax.put("bumen2", bumen2);
                    datax.put("bumen3", bumen3);
                    datax.put("zfry1_code", zfyr1_code);
                    datax.put("zfry2_code", zfry2_code);

                    if (arr.length > 19) {
                        datax.put("s", "3");
                    } else {
                        datax.put("s", "2");
                    }
                    datax.put("i", "2");

                    FileUtils.deleteFile(xpdf);
//                    readpdfandFillData(pdfx, datax, xpdf);
                    CommonUtils.readpdfandFillData(bos, fos, xpdf, ps, bf, fontList, fields, pdfx, datax);
                    if (arr.length > 19) {
                        //加载续页
                        final String pdfxx = "assets/template/zlxqzgzlstx.pdf";
                        final Map<String, String> dataxx = new HashMap<String, String>();
                        xpdfx = "/sdcard/00_linshiwenshu/linshi_zlxqzgzls/" + etPrintOut06.getText() + DocumentNumber + "(临时3).pdf";

                        //第三页添加数据
                        for (int i = 18; i < arr.length; i++) {
                            dataxx.put(i + "", arr[i]);
                        }
                        dataxx.put("s", "3");
                        dataxx.put("i", "3");
                        dataxx.put("qiye_name", qiye_name);
                        dataxx.put("wtbh", getIntent().getStringExtra("rectify"));
                        dataxx.put("y", sdf1.format(curDate));
                        dataxx.put("m", sdf2.format(curDate));
                        dataxx.put("d", sdf3.format(curDate));
                        dataxx.put("bumen1", bumen1);
                        dataxx.put("bumen2", bumen2);
                        dataxx.put("bumen3", bumen3);
                        dataxx.put("zfry1_code", zfyr1_code);
                        dataxx.put("zfry2_code", zfry2_code);
                        FileUtils.deleteFile(xpdfx);
//                        readpdfandFillData(pdfxx, dataxx, xpdfx);
                        CommonUtils.readpdfandFillData(bos, fos, xpdfx, ps, bf, fontList, fields, pdfxx, dataxx);

                    }
                }

//                readpdfandFillData(rectifyPath, checkmap, newrectifyPath);
                CommonUtils.readpdfandFillData(bos, fos, newrectifyPath, ps, bf, fontList, fields, rectifyPath, checkmap);
                if (arr.length > 8) {

                    rectify_zuizhong = "/sdcard/00_linshiwenshu/linshi_zlxqzgzls/" + etPrintOut06.getText() + DocumentNumber + "(临时).pdf";
                    FileUtils.deleteFile(rectify_zuizhong);
                    combinepdf(rectify_zuizhong, newrectifyPath, xpdf);
                    FileUtils.deleteFile(newrectifyPath);
                    FileUtils.deleteFile(xpdf);
                    if (arr.length > 19) {
                        rectify_zuizhongx = "/sdcard/00_linshiwenshu/linshi_zlxqzgzls/" + etPrintOut06.getText() + DocumentNumber + "(临时4).pdf";
                        FileUtils.deleteFile(rectify_zuizhongx);
                        combinepdf(rectify_zuizhongx, rectify_zuizhong, xpdfx);
                        FileUtils.deleteFile(rectify_zuizhong);
                        FileUtils.deleteFile(xpdfx);
                        CommonUtils.doOpenPdf(PrintOut02Activity.this, rectify_zuizhongx);
                    } else {
                        CommonUtils.doOpenPdf(PrintOut02Activity.this, rectify_zuizhong);
                    }

                } else {
                    CommonUtils.doOpenPdf(PrintOut02Activity.this, newrectifyPath);

                }
                break;
            case R.id.bt_print_out_02:
                if (CommonUtils.isNetworkConnected(PrintOut02Activity.this)) {
                    checkmap.put("people_3", etPrintOut08.getText().toString());

                    checkmap.put("people_1", zfyr1);
                    checkmap.put("people_2", zfyr2);
                    if (arr.length > 30) {
                        Toast.makeText(this, "文档容不下，请精简输入内容", Toast.LENGTH_SHORT).show();
                    } else {
                        dialogDoneDelete();
                    }
                } else {
                    Toast.makeText(PrintOut02Activity.this, "请确定网络是否连接", Toast.LENGTH_SHORT).show();

                }
                break;
            default:
                break;
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
        checkmap.put("bumen1", etPrintOut03.getText().toString());
        checkmap.put("bumen2", etPrintOut04.getText().toString());
        checkmap.put("bumen3", etPrintOut05.getText().toString());
        //添加参数
        HashMap<String, String> properties = new HashMap<>();
        properties.put("Xml", "<Request><data  code='GetDocumentCode'><no><DocumentType>1</DocumentType><CompanyId>" +
                CompanyId + "</CompanyId><BusinessType>" +
                getIntent().getStringExtra("BusinessType") + "</BusinessType></no></data></Request>");
        properties.put("Token", "");
        Log.d("文书上传数据", properties.toString());
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
                        DocumentNumber = jsonObj.getJSONObject("DocumentElement").getJSONObject("Table").getString("Column1");
                        SharedPreferencesUtil.saveStringData(PrintOut02Activity.this, getIntent().getStringExtra("PlanId"), DocumentNumber);
                        if (upDateBoolean) {
                            Toast.makeText(PrintOut02Activity.this, "上传成功", Toast.LENGTH_SHORT).show();
                            if (arr.length > 8) {
                                if (arr.length > 19) {
                                    CommonUtils.doOpenPdf(PrintOut02Activity.this, rectify_zuizhongx);
                                } else {
                                    CommonUtils.doOpenPdf(PrintOut02Activity.this, rectify_zuizhong);
                                }
                            } else {
                                CommonUtils.doOpenPdf(PrintOut02Activity.this, newrectifyPath);
                            }
                            if (progressDialog != null) {
                                progressDialog.dismiss();
                                progressDialog = null;
                            }
                            finish();
                            overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                        } else {

                            //保存文书
                            saveDate();
                        }

                    } catch (JSONException e) {
                        //关闭加载页
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                            progressDialog = null;
                        }
                        Toast.makeText(PrintOut02Activity.this, "提交失败", Toast.LENGTH_SHORT).show();
                        Log.e("JSON exception", e.getMessage());
                        e.printStackTrace();
                    }
                } else {
                    //关闭加载页
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                        progressDialog = null;
                    }
                    Toast.makeText(PrintOut02Activity.this, "网络未响应，提交失败", Toast.LENGTH_SHORT).show();

                }
            }
        });

    }

    /**
     * 生成PDF
     */
    protected void makePDF() {
        Log.d("文书编号数据", DocumentNumber);
        str = etPrintOut02.getText().toString() + "(以下空白)";
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        SimpleDateFormat sdf4 = new SimpleDateFormat("yyyy", Locale.CHINA);
        checkmap.put("y", sdf4.format(curDate));
        String[] strings = getIntent().getStringExtra("DayNum").split("-");
        String[] strings_code = checkmap.get("jibie").split("安");
        String wenshu_code = "（" + strings_code[0] + "）安监管责改[" + sdf4.format(curDate).substring(0, 4) + "]" + getIntent().getStringExtra("BusinessType") + DocumentNumber + "号";
        checkmap.put("wenshu_code", wenshu_code);
        SimpleDateFormat sdf5 = new SimpleDateFormat("MM", Locale.CHINA);
        checkmap.put("m", sdf5.format(curDate));
        SimpleDateFormat sdf6 = new SimpleDateFormat("dd", Locale.CHINA);
        checkmap.put("d", sdf6.format(curDate));
        checkmap.put("wtbh", getIntent().getStringExtra("rectify"));
        bumen1 = etPrintOut03.getText().toString();
        checkmap.put("bumen1", bumen1);
        bumen2 = etPrintOut04.getText().toString();
        checkmap.put("bumen2", bumen2);
        bumen3 = etPrintOut05.getText().toString();
        checkmap.put("bumen3", bumen3);
        int sum = 0;
        newrectifyPath = "/sdcard/00_zhengshiwenshu/zhengshi_zlxqzgzls/" + etPrintOut06.getText() + checkmap.get("y") + checkmap.get("m") + checkmap.get("d") + DocumentNumber + "(1).pdf";
        //对小于7行的部分填充数据
        if (arr.length <= 8) {

            for (int i = 0; i < arr.length; i++) {
                checkmap.put(i + "", arr[i]);
            }
        }
        //如果分行以后行数大于11，两页的pdf页码分别设置为1和2
        if (arr.length > 8 && arr.length <= 19) {
            checkmap.put("s", "2");
        } else if (arr.length > 19) {
            checkmap.put("s", "3");
        } else {
            checkmap.put("s", "1");
        }
        checkmap.put("i", "1");
        //如果分行之后的行数大于11，加载续页的模板并填充数据
        if (arr.length > 8) {
            //加载续页
            final String pdfx = "assets/template/zlxqzgzlst.pdf";
            final Map<String, String> datax = new HashMap<String, String>();
            xpdf = "/sdcard/00_zhengshiwenshu/zhengshi_zlxqzgzls/" + etPrintOut06.getText() + DocumentNumber + "(2).pdf";
            //第一页添加数据
            for (int i = 0; i < 8; i++) {

                checkmap.put(i + "", arr[i]);
            }
            //第二页添加数据
            for (int i = 8; i < arr.length; i++) {

                datax.put(i + "", arr[i]);

            }
            datax.put("wenshu_code", wenshu_code);
            datax.put("qiye_name", etPrintOut06.getText().toString());
            datax.put("wtbh", getIntent().getStringExtra("rectify"));
            datax.put("y", sdf4.format(curDate));
            datax.put("m", sdf5.format(curDate));
            datax.put("d", sdf6.format(curDate));
            datax.put("bumen1", etPrintOut03.getText().toString());
            datax.put("bumen2", etPrintOut04.getText().toString());
            datax.put("bumen3", etPrintOut05.getText().toString());
            datax.put("zfry1_code", zfyr1_code);
            datax.put("zfry2_code", zfry2_code);
            datax.put("people_1", checkmap.get("people_1"));
            datax.put("people_2", checkmap.get("people_2"));
            datax.put("people_3", checkmap.get("people_3"));
            if (arr.length > 19) {
                datax.put("s", "3");
            } else {
                datax.put("s", "2");
            }
            datax.put("i", "2");

            FileUtils.deleteFile(xpdf);
//            readpdfandFillData(pdfx, datax, xpdf);
            CommonUtils.readpdfandFillData(bos, fos, xpdf, ps, bf, fontList, fields, pdfx, datax);
            if (arr.length > 19) {
                //加载续页
                final String pdfxx = "assets/template/zlxqzgzlstx.pdf";
                final Map<String, String> dataxx = new HashMap<String, String>();
                xpdfx = "/sdcard/00_zhengshiwenshu/zhengshi_zlxqzgzls/" + etPrintOut06.getText() + DocumentNumber + "(3).pdf";

                //第三页添加数据
                for (int i = 18; i < arr.length; i++) {

                    dataxx.put(i + "", arr[i]);

                }
                dataxx.put("s", "3");
                dataxx.put("i", "3");
                dataxx.put("people_1", checkmap.get("people_1"));
                dataxx.put("people_2", checkmap.get("people_2"));
                dataxx.put("people_3", checkmap.get("people_3"));
                dataxx.put("qiye_name", qiye_name);
                dataxx.put("wtbh", getIntent().getStringExtra("rectify"));
                dataxx.put("y", sdf4.format(curDate));
                dataxx.put("m", sdf5.format(curDate));
                dataxx.put("d", sdf6.format(curDate));
                dataxx.put("bumen1", bumen1);
                dataxx.put("bumen2", bumen2);
                dataxx.put("bumen3", bumen3);
                dataxx.put("zfry1_code", zfyr1_code);
                dataxx.put("zfry2_code", zfry2_code);
                FileUtils.deleteFile(xpdfx);

//                readpdfandFillData(pdfxx, dataxx, xpdfx);
                CommonUtils.readpdfandFillData(bos, fos, xpdfx, ps, bf, fontList, fields, pdfxx, dataxx);

            }

        }

        checkmap.put("i", "1");
        FileUtils.deleteFile(newrectifyPath);
//        readpdfandFillData(rectifyPath, checkmap, newrectifyPath);
        CommonUtils.readpdfandFillData(bos, fos, newrectifyPath, ps, bf, fontList, fields, rectifyPath, checkmap);
        if (arr.length > 8) {

            rectify_zuizhong = "/sdcard/00_zhengshiwenshu/zhengshi_zlxqzgzls/" + etPrintOut06.getText() + DocumentNumber + ".pdf";
            FileUtils.deleteFile(rectify_zuizhong);
            combinepdf(rectify_zuizhong, newrectifyPath, xpdf);
            FileUtils.deleteFile(newrectifyPath);
            FileUtils.deleteFile(xpdf);
            if (arr.length > 19) {
                rectify_zuizhongx = "/sdcard/00_zhengshiwenshu/zhengshi_zlxqzgzls/" + etPrintOut06.getText() + DocumentNumber + "(4).pdf";
                FileUtils.deleteFile(rectify_zuizhongx);
                combinepdf(rectify_zuizhongx, rectify_zuizhong, xpdfx);
                FileUtils.deleteFile(rectify_zuizhong);
                FileUtils.deleteFile(xpdfx);
            }
        }
        if (upDateBoolean) {
            Toast.makeText(PrintOut02Activity.this, "上传成功", Toast.LENGTH_SHORT).show();
            if (arr.length > 8) {
                if (arr.length > 19) {
                    CommonUtils.doOpenPdf(PrintOut02Activity.this, rectify_zuizhongx);
                } else {
                    CommonUtils.doOpenPdf(PrintOut02Activity.this, rectify_zuizhong);
                }
            } else {
                CommonUtils.doOpenPdf(PrintOut02Activity.this, newrectifyPath);
            }
            if (progressDialog != null) {
                progressDialog.dismiss();
                progressDialog = null;
            }
            finish();
            overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
        } else {

            //保存文书

            saveDate();
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
            newrectifyPath = "/sdcard/00_linshiwenshu/linshi_zlxqzgzls/" + cursor4.getString(cursor4.getColumnIndex("FullName")) +
                    "安监管责改" + DocumentNumber + "号(临时).pdf";
            cursor4.close();
            //获取企业信息
            Cursor cursor = db.rawQuery("SELECT* FROM ELL_Business WHERE BusinessId = ?",
                    new String[]{c.getString(c.getColumnIndex("BusinessId"))});
            cursor.moveToFirst();
            etPrintOut06.setText(cursor.getString(cursor.getColumnIndex("BusinessName")));
            etPrintOut08.setText(cursor.getString(cursor.getColumnIndex("LegalPerson")));
            checkmap.put("BusinessType1", cursor.getString(cursor.getColumnIndex("BusinessType1")));

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
                    checkmap.put("zfry1_code", zfyr1_code);
                    zfyr1 = peopleList.get(position).get("RealName").toString();
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
                    zfyr2 = peopleList.get(position).get("RealName").toString();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            cursor1.close();
            cursor.close();
            c.close();
            sPrintOut02.setSelection(1);
            String s2 = "     " + "     " + etPrintOut02.getText().toString() + "(以下空白)";
            if (needStr.equals("1")) {
                s2 = "     " + "     " + etPrintOut02.getText().toString() + "\n" +
                        "以上处理决定委托" + mFullName + Department + "进行督促落实。" + "(以下空白)";
            }
            printfText.setText(s2);
            printfText.getViewTreeObserver().addOnGlobalLayoutListener(new OnTvGlobalLayoutListener());

        } catch (Exception e) {
            Log.e("数据库报错", e.toString());
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
        try {
            Date curDate = new Date(System.currentTimeMillis());//获取当前时间
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
            String[] strings1 = checkmap.get("jibie").split("安");
            ContentValues values = new ContentValues();
            values.put("DocumentId", DocumentId);
            values.put("PlanId", getIntent().getStringExtra("PlanId"));
            values.put("DocumentNumber",
                    "（" + strings1[0] + "）安监管责改[" + sdf.format(curDate).substring(0, 4) + "]" +
                            getIntent().getStringExtra("BusinessType") + DocumentNumber + "号");
            values.put("Problem", etPrintOut02.getText().toString());
            values.put("AddTime", sdf.format(curDate));
            values.put("PlanType", "执法计划");
            db.replace("ELL_Document", null, values);
            /*隐患表（图片）*/
            String[] strings = getIntent().getStringExtra("CheckId").split(",");
            for (int i2 = 0; i2 < strings.length; i2++) {

                Cursor cursor6 = db.rawQuery("SELECT* FROM ELL_Photo WHERE CheckId = ?",
                        new String[]{strings[i2]});
                if (i2 == 0) {

                    if (cursor6.getCount() != 0) {
                        int i = 0;
                        while (cursor6.moveToNext()) {
                            if (i == 0) {
                                ImagePath = "/sdcard/LanDong/" + cursor6.getString(cursor6.getColumnIndex("Address"));
                                ImageName = cursor6.getString(cursor6.getColumnIndex("Address"));

                            } else {
                                ImagePath = ImagePath + ";" + "/sdcard/LanDong/" + cursor6.getString(cursor6.getColumnIndex("Address"));
                                ImageName = ImageName + ";" + cursor6.getString(cursor6.getColumnIndex("Address"));

                            }
                            i++;
                        }
//                        cursor6.moveToFirst();
//                        ImagePath = "/sdcard/LanDong/" + cursor6.getString(cursor6.getColumnIndex("Address"));
//                        ImageName = cursor6.getString(cursor6.getColumnIndex("Address"));
                    }

                } else if (i2 > 0) {

                    if (ImagePath.length() > 0 && cursor6.getCount() != 0) {
                        while (cursor6.moveToNext()) {

                            ImagePath = ImagePath + ";" + "/sdcard/LanDong/" + cursor6.getString(cursor6.getColumnIndex("Address"));
                            ImageName = ImageName + ";" + cursor6.getString(cursor6.getColumnIndex("Address"));

                        }
//                        cursor6.moveToFirst();
//                        ImagePath = ImagePath + ";" + "/sdcard/LanDong/" + cursor6.getString(cursor6.getColumnIndex("Address"));
//                        ImageName = ImageName + ";" + cursor6.getString(cursor6.getColumnIndex("Address"));
                    } else if (ImagePath.length() == 0 & cursor6.getCount() != 0) {
                        int i = 0;
                        while (cursor6.moveToNext()) {
                            if (i == 0) {
                                ImagePath = "/sdcard/LanDong/" + cursor6.getString(cursor6.getColumnIndex("Address"));
                                ImageName = cursor6.getString(cursor6.getColumnIndex("Address"));

                            } else {
                                ImagePath = ImagePath + ";" + "/sdcard/LanDong/" + cursor6.getString(cursor6.getColumnIndex("Address"));
                                ImageName = ImageName + ";" + cursor6.getString(cursor6.getColumnIndex("Address"));

                            }
                            i++;
                        }
//                        cursor6.moveToFirst();
//                        ImagePath = "/mnt/sdcard/LanDong/" + cursor6.getString(cursor6.getColumnIndex("Address"));
//                        ImageName = cursor6.getString(cursor6.getColumnIndex("Address"));
                    }
                }

                cursor6.close();

            }
            Log.e("存入文书数据", "REPLACE INTO ELL_Document VALUES('" +
                    DocumentId + "','" +
                    getIntent().getStringExtra("PlanId") + "','" +
                    "（" + strings1[0] + "）安监检记[" + sdf.format(curDate).substring(0, 4) + "]" + getIntent().getStringExtra("BusinessType") + DocumentNumber + "号" + "','" +
                    etPrintOut02.getText().toString() + "','" +
                    sdf.format(curDate) + "','" +
                    "执法计划" + "')");
            businessUpDate();
        } catch (Exception e) {
            Log.e("存入数据库报错", e.toString(), e);
        }
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
        String LOCALE_RESPONSIBLE = SharedPreferencesUtil.getStringData(this, getIntent().getStringExtra("PlanId") + "LOCALE_RESPONSIBLE", "");
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
                    Log.e("MemberId的数据", MemberId);
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
                        while (cursor6.moveToNext()) {

                            if (i3 == 0) {
                                ImagePath = "/sdcard/LanDong/" + cursor6.getString(cursor6.getColumnIndex("Address"));
                                ImageName = cursor6.getString(cursor6.getColumnIndex("Address"));
                                YHZGQTP = cursor6.getString(cursor6.getColumnIndex("Address")) + ".jpg";

                            } else {
                                ImagePath = ImagePath + ";" + "/sdcard/LanDong/" + cursor6.getString(cursor6.getColumnIndex("Address"));
                                ImageName = ImageName + ";" + cursor6.getString(cursor6.getColumnIndex("Address"));

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
                        while (cursor6.moveToNext()) {
                            if (i3 == 0) {
                                ImagePath = ImagePath + ";" + "/sdcard/LanDong/" + cursor6.getString(cursor6.getColumnIndex("Address"));
                                ImageName = ImageName + ";" + cursor6.getString(cursor6.getColumnIndex("Address"));

                                YHZGQTP = YHZGQTP + ";" + cursor6.getString(cursor6.getColumnIndex("Address")) + ".jpg";

                            } else {
                                ImagePath = ImagePath + ";" + "/sdcard/LanDong/" + cursor6.getString(cursor6.getColumnIndex("Address"));
                                ImageName = ImageName + ";" + cursor6.getString(cursor6.getColumnIndex("Address"));

                                YHZGQTP = YHZGQTP + "," + cursor6.getString(cursor6.getColumnIndex("Address")) + ".jpg";

                            }
                            i3++;
                        }
                    } else if (ImagePath.length() == 0 & cursor6.getCount() != 0) {
                        int i3 = 0;
                        while (cursor6.moveToNext()) {
                            if (i3 == 0) {
                                ImagePath = "/sdcard/LanDong/" + cursor6.getString(cursor6.getColumnIndex("Address"));
                                ImageName = cursor6.getString(cursor6.getColumnIndex("Address"));
                                YHZGQTP = YHZGQTP + ";" + cursor6.getString(cursor6.getColumnIndex("Address")) + ".jpg";

                            } else {
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
                cursor4.close();
                cursor6.close();
            }

        } catch (Exception e) {
            Log.e("数据库填补数据报错", e.toString());
        }
        GOVERNMENT = etPrintOut03.getText().toString();
        SharedPreferencesUtil.saveStringData(this,
                getIntent().getStringExtra("PlanId") + "GOVERNMENT", GOVERNMENT);
        REVIEW_GOVERNMENT = etPrintOut04.getText().toString();
        SharedPreferencesUtil.saveStringData(this,
                getIntent().getStringExtra("PlanId") + "REVIEW_GOVERNMENT", REVIEW_GOVERNMENT);
        COURT = etPrintOut05.getText().toString();
        SharedPreferencesUtil.saveStringData(this,
                getIntent().getStringExtra("PlanId") + "COURT", COURT);
        CHECK_ENTRUST = typAnInt+"";//是否委托复查
        SharedPreferencesUtil.saveStringData(this,
                getIntent().getStringExtra("PlanId") + "CHECK_ENTRUST", CHECK_ENTRUST);
        switch (typAnInt){
            case 0://是否委托复查
                CHECK_ENTRUST_DEPART = "";
                ENTRUST_DEPARTMENT_ID = "";
                break;
            case 1://委托部门复查
                CHECK_ENTRUST_DEPART = Department;
                ENTRUST_DEPARTMENT_ID = DepartmentId;
                break;
            case 2://委托集团复查
                CHECK_ENTRUST_DEPART = s_Group;
                ENTRUST_DEPARTMENT_ID = s_GroupId;
                break;
            default:
                break;


        }

        SharedPreferencesUtil.saveStringData(this,
                getIntent().getStringExtra("PlanId") + "CHECK_ENTRUST_DEPART", CHECK_ENTRUST_DEPART);

        SharedPreferencesUtil.saveStringData(this,
                getIntent().getStringExtra("PlanId") + "ENTRUST_DEPARTMENT_ID", ENTRUST_DEPARTMENT_ID);
        LOCALE_RESPONSIBLE = etPrintOut08.getText().toString();
        SharedPreferencesUtil.saveStringData(this,
                getIntent().getStringExtra("PlanId") + "LOCALE_RESPONSIBLE", LOCALE_RESPONSIBLE);
        //添加参数
        HashMap<String, String> properties = new HashMap<>();
        properties.put("Xml", "<Request><data  code='add-Proc1'><no>" +
                "<GPDBSJ>" +
                GPDBSJ + "</GPDBSJ><GPDBJB>" +
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
                "1" + "</DocumentType><GroupId>" +
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
        Log.e("各类表上传数据", properties.toString());
        WebServiceUtils.callWebService(WEB_SERVER_URL, "DynamicInvoke", properties, new WebServiceUtils.WebServiceCallBack() {

            //WebService接口返回的数据回调到这个方法中
            @Override
            public void callBack(SoapObject result) {
                if (result != null) {
                    Log.d("各类表上传返回数据", result.toString());
                    try {
                        Object detail = result.getProperty("DynamicInvokeResult");
                        JSONObject jsonObj;
                        jsonObj = XML.toJSONObject(detail.toString());
                        if (detail.toString().equals("True")) {
                            flag = false;
                            upDateBoolean = true;
                            checkmap.put("people_1", "");
                            checkmap.put("people_2", "");
                            checkmap.put("people_3", "");
                            makePDF();
                            SharedPreferencesUtil.saveBooleanData(PrintOut02Activity.this, getIntent().getStringExtra("PlanId") + "isDone", true);
                        } else {
                            if (progressDialog != null) {
                                progressDialog.dismiss();
                                progressDialog = null;
                            }
                            Toast.makeText(PrintOut02Activity.this, "此文书已上传，请不要重复上传", Toast.LENGTH_SHORT).show();
                            SharedPreferencesUtil.saveBooleanData(PrintOut02Activity.this, getIntent().getStringExtra("PlanId") + "isDone", true);
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
                        Toast.makeText(PrintOut02Activity.this, "提交失败", Toast.LENGTH_SHORT).show();
                        Log.e("JSON exception", e.getMessage());
                        e.printStackTrace();
                    }
                } else {
                    //关闭加载页
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                        progressDialog = null;
                    }
                    Toast.makeText(PrintOut02Activity.this, "网络未响应，提交失败", Toast.LENGTH_SHORT).show();

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
                    Log.d("上传路径", newrectifyPath); //输出错误消息
                    Log.d("上传文件名", DocumentId); //输出错误消息
                    if (arr.length > 8) {
                        if (arr.length > 19) {
                            aBoolean = ftpUtils.uploadFile(rectify_zuizhongx, DocumentId + ".pdf");
                        } else {
                            aBoolean = ftpUtils.uploadFile(rectify_zuizhong, DocumentId + ".pdf");
                        }

                    } else {
                        aBoolean = ftpUtils.uploadFile(newrectifyPath, DocumentId + ".pdf");//xcjcjl_zuizhong
                    }
                    if (aBoolean && ImagePath.length() == 0) {
                        handler.sendEmptyMessage(0);
                    } else if (!aBoolean && ImagePath.length() == 0) {
                        handler.sendEmptyMessage(1);
                    } else if (ImagePath.length() > 0) {
                        image_num = 0;
                        handler.sendEmptyMessage(2);
                    }
                    Log.d("上传Word", aBoolean.toString());
                }
            }.start();
        } else {
            Toast.makeText(PrintOut02Activity.this, "上传文件失败", Toast.LENGTH_SHORT).show();
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
     * 功能：字符串半角转换为全角
     * 说明：半角空格为32,全角空格为12288.
     * 其他字符半角(33-126)与全角(65281-65374)的对应关系是：均相差65248
     * 输入参数：input -- 需要转换的字符串
     * 输出参数：无：
     * 返回值: 转换后的字符串
     */
    public String halfToFull(String input) {
        char[] c = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == 32) //半角空格
            {
                c[i] = (char) 12288;
                continue;
            }
            //根据实际情况，过滤不需要转换的符号
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
            Toast.makeText(PrintOut02Activity.this, "上传图片失败", Toast.LENGTH_SHORT).show();
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
     * 上传企业信息（区分是否修改新增）
     */
    protected void businessUpDate() {
        // 初始化，只需要调用一次
        AssetsDatabaseManager.initManager(getApplication());
        // 获取管理对象，因为数据库需要通过管理对象才能够获取
        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
        // 通过管理对象获取数据库
        SQLiteDatabase db = mg.getDatabase("users.db");
        /*计划*/
        Cursor cursor = db.rawQuery("SELECT* FROM ELL_EnforcementPlan WHERE PlanId = ?",
                new String[]{getIntent().getStringExtra("PlanId")});
        cursor.moveToFirst();
        String BusinessId = cursor.getString(cursor.getColumnIndex("BusinessId"));
        cursor.close();
        Cursor cursor1 = db.rawQuery("SELECT* FROM ELL_Business WHERE BusinessId = ?",
                new String[]{BusinessId});
        cursor1.moveToFirst();
        UserDefined = cursor1.getString(cursor1.getColumnIndex("UserDefined"));
        cursor1.close();
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
                    "<LegalPerson>" + cursor1.getString(cursor1.getColumnIndex("LegalPerson")) + "</LegalPerson>" +
                    "<LegalPersonPhone>" + cursor1.getString(cursor1.getColumnIndex("LegalPersonPhone")) + "</LegalPersonPhone>" +
                    "<RespPerson>" + cursor1.getString(cursor1.getColumnIndex("SafetyOfficer")) + "</RespPerson>" +
                    "<RespPersonPhone>" + cursor1.getString(cursor1.getColumnIndex("SafetyOfficerPhone")) + "</RespPersonPhone>" +
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
                    "<BusinessLicense>" + cursor1.getString(cursor1.getColumnIndex("RegistrationNumber")) + "</BusinessLicense>" +
                    "<ValidFlag>0</ValidFlag>" +
                    "<BusinessType>" + cursor1.getString(cursor1.getColumnIndex("BusinessType")) + "</BusinessType>" +
                    "<LegalPersonPost></LegalPersonPost>" +
                    "<Address>" + cursor1.getString(cursor1.getColumnIndex("Address")) + "</Address>" +
                    "<UploadUserID>" + SharedPreferencesUtil.getStringData(this, "userId", "") + "</UploadUserID></no></data></Request>");

        } else {
            properties.put("Xml", "<Request><data  code='upload_Business'><no>" +
                    "<OperateKind>" + s + "</OperateKind>" +
                    "<BusinessId>" + BusinessId + "</BusinessId>" +
                    "<BusinessName>" + cursor1.getString(cursor1.getColumnIndex("BusinessName")) + "</BusinessName>" +
                    "<BusinessType>" + cursor1.getString(cursor1.getColumnIndex("BusinessType")) + "</BusinessType>" +
                    "<LegalPerson>" + cursor1.getString(cursor1.getColumnIndex("LegalPerson")) + "</LegalPerson>" +
                    "<LegalPersonPhone>" + cursor1.getString(cursor1.getColumnIndex("LegalPersonPhone")) + "</LegalPersonPhone>" +
                    "<OrgCode>" + cursor1.getString(cursor1.getColumnIndex("OrgCode")) + "</OrgCode>" +
                    "<SafetyOfficer>" + cursor1.getString(cursor1.getColumnIndex("SafetyOfficer")) + "</SafetyOfficer>" +
                    "<SafetyOfficerPhone>" + cursor1.getString(cursor1.getColumnIndex("SafetyOfficerPhone")) + "</SafetyOfficerPhone>" +
                    "<Address>" + cursor1.getString(cursor1.getColumnIndex("Address")) + "</Address>" +
                    "<ValidFlag>0</ValidFlag>" +
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
//                  Log.e("用户名发过去的结果",""+result);
                        Object detail = result.getProperty("DynamicInvokeResult");
                        JSONObject jsonObj;
                        jsonObj = XML.toJSONObject(detail.toString());
                        if (detail.toString().equals("True")) {
                            ContentValues values = new ContentValues();
                            values.put("UserDefined", "0");
                            String whereClause = "BusinessId=?";
                            String[] whereArgs = new String[]{BusinessId};
                            db.update("ELL_Business", values, whereClause, whereArgs);
                            upLoad();
                        } else {
                            if (progressDialog != null) {
                                progressDialog.dismiss();
                                progressDialog = null;
                            }
                            Toast.makeText(PrintOut02Activity.this, "上传数据失败", Toast.LENGTH_SHORT).show();

                        }
                    } catch (Exception e) {
                        //关闭加载页
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                            progressDialog = null;
                        }
                        Toast.makeText(PrintOut02Activity.this, "提交失败", Toast.LENGTH_SHORT).show();
//                        Log.e("JSON exception", e.getMessage());
                        e.printStackTrace();
                    }
                } else {
                    //关闭加载页
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                        progressDialog = null;
                    }
                    Toast.makeText(PrintOut02Activity.this, "网络未响应，提交失败", Toast.LENGTH_SHORT).show();

                }
            }
        });
        cursor1.close();
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
                if (SharedPreferencesUtil.getStringData(PrintOut02Activity.this, getIntent().getStringExtra("PlanId"), null) == null) {
                    getNum();
                } else {
                    if (progressDialog == null) {
                        progressDialog = CustomProgressDialog.createDialog(PrintOut02Activity.this);
                    }
                    progressDialog.show();
                    DocumentNumber = SharedPreferencesUtil.getStringData(PrintOut02Activity.this, getIntent().getStringExtra("PlanId"), null);
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
                listItem.put("CompanyId", c.getString(c.getColumnIndex("CompanyId")));
                listItem.put("FullName", c.getString(c.getColumnIndex("FullName")));
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
                        mCompanyId = listItems.get(position).get("CompanyId").toString();
                        mFullName = listItems.get(position).get("FullName").toString();
                        // 对数据库进行操作
                        Cursor a = db.rawQuery("SELECT* FROM Base_Department WHERE CompanyId = ?",
                                new String[]{mCompanyId});
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
//                                    Log.d("传过来的数据",i3+"");
                            }
                            listItem.put("DepartmentId", a.getString(a.getColumnIndex("Id")));
                            listItem.put("FullName", a.getString(a.getColumnIndex("FullName")));
                            listItems1.add(listItem);
                            i2++;
                        }
                        SimpleAdapter simpleAdapter = new SimpleAdapter(PrintOut02Activity.this, listItems1,
                                R.layout.login_spinner_item,
                                new String[]{"FullName"},
                                new int[]{R.id.text});
                        spinCity.setAdapter(simpleAdapter);
                        spinCity.setSelection(i3);
                        /**
                         * 下拉列表 部门
                         * */
                        spinCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                                try {
                                    Department = listItems1.get(position).get("FullName").toString();
                                    DepartmentId = listItems1.get(position).get("DepartmentId").toString();
                                    String s2 = "     " + "     " + etPrintOut02.getText().toString() + "(以下空白)";
                                    if (needStr.equals("1")) {
                                        s2 = "     " + "     " + etPrintOut02.getText().toString() + "\n" +
                                                "以上处理决定委托" + mFullName + Department + "进行督促落实。" + "(以下空白)";
                                    }
                                    printfText.setText(s2);
                                    printfText.getViewTreeObserver().addOnGlobalLayoutListener(new OnTvGlobalLayoutListener());

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
        } catch (Exception e) {
            Log.e("数据库报错", e.toString(),e);
        }
        /**
         * 下拉列表 集团
         * */
        try{
            Cursor cursor2 = db.rawQuery("SELECT* FROM ELL_Group", null);
            cursor2.moveToFirst();
            final List<HashMap<String,Object>> mapHashMap = new ArrayList<>();
            HashMap<String, Object> listItem1;
            while (cursor2.moveToNext()){
                listItem1 = new HashMap<>();
                listItem1.put("BusinessId", cursor2.getString(cursor2.getColumnIndex("BusinessId")));
                listItem1.put("BusinessName", cursor2.getString(cursor2.getColumnIndex("BusinessName")));
                mapHashMap.add(listItem1);
            }
            cursor2.close();
            SimpleAdapter simpleAdapter = new SimpleAdapter(PrintOut02Activity.this, mapHashMap,
                    R.layout.login_spinner_item,
                    new String[]{"BusinessName"},
                    new int[]{R.id.text});
            spinGroup.setAdapter(simpleAdapter);
            spinGroup.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    s_Group = mapHashMap.get(position).get("BusinessName").toString();
                    s_GroupId = mapHashMap.get(position).get("BusinessId").toString();
                    String s2 = "     " + "     " + etPrintOut02.getText().toString() + "(以下空白)";
                    if (needStr.equals("1")) {
                        s2 = "     " + "     " + etPrintOut02.getText().toString() + "\n" +
                                "以上处理决定委托" + s_Group + "进行督促落实。" + "(以下空白)";
                    }
                    printfText.setText(s2);
                    printfText.getViewTreeObserver().addOnGlobalLayoutListener(new OnTvGlobalLayoutListener());

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }catch (Exception e){
            Log.e("数据库报错", e.toString(),e);
        }
        /**
         * 下拉列表 选择部门还是企业
         * */
        try{
            spin01.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if(position==0){
                        lDepartment.setVisibility(View.VISIBLE);
                        lGroup.setVisibility(View.GONE);
                        typAnInt = 1;
                        String s2 = "     " + "     " + etPrintOut02.getText().toString() + "(以下空白)";
                        if (needStr.equals("1")) {
                            s2 = "     " + "     " + etPrintOut02.getText().toString() + "\n" +
                                    "以上处理决定委托" + mFullName + Department + "进行督促落实。" + "(以下空白)";
                        }
                        printfText.setText(s2);
                        printfText.getViewTreeObserver().addOnGlobalLayoutListener(new OnTvGlobalLayoutListener());
                    }else if(position==1){
                        lDepartment.setVisibility(View.GONE);
                        lGroup.setVisibility(View.VISIBLE);
                        typAnInt = 2;
                        String s2 = "     " + "     " + etPrintOut02.getText().toString() + "(以下空白)";
                        if (needStr.equals("1")) {
                            s2 = "     " + "     " + etPrintOut02.getText().toString() + "\n" +
                                    "以上处理决定委托" + s_Group + "进行督促落实。" + "(以下空白)";
                        }
                        printfText.setText(s2);
                        printfText.getViewTreeObserver().addOnGlobalLayoutListener(new OnTvGlobalLayoutListener());
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }catch(Exception e){
            Log.e("数据库报错", e.toString(),e);
        }
    }

    //对整行的数据进行抽取，且格式化
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

}
