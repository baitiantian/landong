package com.jinan.ladongjiguan.anjiantong.activity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import com.jinan.ladongjiguan.anjiantong.R;
import com.jinan.ladongjiguan.anjiantong.utils.CommonUtils;
import com.jinan.ladongjiguan.anjiantong.utils.FileUtils;

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

/**
 * Created by baitiantian on 2017/4/18.
 */

public class PrintOut04Activity extends BaseActivity implements View.OnClickListener, View.OnTouchListener {


    @BindView(R.id.examine_page_back)
    LinearLayout examinePageBack;
    @BindView(R.id.title_layout)
    TextView titleLayout;
    @BindView(R.id.delete_layout)
    LinearLayout deleteLayout;
    @BindView(R.id.et_print_out_01)
    EditText etPrintOut01;
    @BindView(R.id.et_print_out_02)
    EditText etPrintOut02;
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
    @BindView(R.id.et_print_out_08)
    EditText etPrintOut08;
    @BindView(R.id.bt_print_out_00)
    Button btPrintOut00;
    @BindView(R.id.bt_print_out_01)
    Button btPrintOut01;
    @BindView(R.id.et_print_out_09)
    EditText etPrintOut09;
    @BindView(R.id.et_print_out_10)
    EditText etPrintOut10;
    @BindView(R.id.et_print_out_11)
    EditText etPrintOut11;
    @BindView(R.id.et_print_out_12)
    EditText etPrintOut12;
    @BindView(R.id.et_print_out_13)
    EditText etPrintOut13;
    @BindView(R.id.et_print_out_14)
    EditText etPrintOut14;
    @BindView(R.id.bt_print_out_02)
    Button btPrintOut02;
    @BindView(R.id.printf_text)
    TextView printfText;
    private Map<String, String> map = new HashMap<>();
    private String DocumentId = UUID.randomUUID().toString();//文书主键
    private String DocumentNumber = String.valueOf(System.currentTimeMillis());

    // 单位处罚模板文集地址
    private String xzcfjdsdwPath = "assets/template/xzcfjdsdw.pdf";
    // 创建生成的文件地址
    private String newxzcfjdsdwPath = "/mnt/sdcard/00_linshiwenshu/linshi_xzcfjdsdw/行政处罚决定书" + DocumentNumber + "号(单位1).pdf";

    private AcroFields fields;//模板中的标签
    private PdfStamper ps;
    private OutputStream fos;
    private ByteArrayOutputStream bos;
    private String xpdf;//续页的pdf
//    private int j;//行数
    private BaseFont bf;
    private ArrayList<BaseFont> fontList;
    private String str;
    private String[] arr;
    private SimpleDateFormat sdf1;
    private SimpleDateFormat sdf2;
    private SimpleDateFormat sdf3;
    private String substring;

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
        setContentView(R.layout.print_out_04_layout);
        ButterKnife.bind(this);
        titleLayout.setText("制定单位行政处罚决定文书");
        File lsws = new File("/mnt/sdcard/00_linshiwenshu/linshi_xzcfjdsdw");
        File zsws = new File("/mnt/sdcard/00_zhengshiwenshu/zhengshi_xzcfjdsdw");
//如果文件夹不存在则创建
        if (!lsws.exists() && !lsws.isDirectory()) {
            lsws.mkdirs();
        }
        if (!zsws.exists() && !zsws.isDirectory()) {
            zsws.mkdirs();
        }
    }

    @Override
    protected void init() {
        examinePageBack.setOnClickListener(this);
        etPrintOut01.setText(DocumentNumber);
        btPrintOut00.setOnClickListener(this);
        btPrintOut01.setOnClickListener(this);
        examinePageBack.setOnTouchListener(this);
        getDate();
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
            case R.id.bt_print_out_01://出具文书键
                str = etPrintOut02.getText().toString();
                map.put("$TNUM$", etPrintOut01.getText().toString());
                map.put("gd1", etPrintOut03.getText().toString());
                map.put("gd2", etPrintOut04.getText().toString());
                map.put("xzcf", etPrintOut05.getText().toString());
                map.put("bumen1", etPrintOut06.getText().toString());
                map.put("bumen2", etPrintOut07.getText().toString());
                map.put("bumen3", etPrintOut08.getText().toString());
                map.put("bcfdw", etPrintOut09.getText().toString());
                map.put("dz", etPrintOut10.getText().toString());
                map.put("yb", etPrintOut11.getText().toString());
                map.put("fzr", etPrintOut12.getText().toString());
                map.put("zw", etPrintOut13.getText().toString());
                map.put("lxdh", etPrintOut14.getText().toString());
                Date curDate = new Date(System.currentTimeMillis());//获取当前时间
                sdf1 = new SimpleDateFormat("yyyy", Locale.CHINA);
                map.put("y", sdf1.format(curDate));
                sdf2 = new SimpleDateFormat("MM", Locale.CHINA);
                map.put("m", sdf2.format(curDate));
                sdf3 = new SimpleDateFormat("dd", Locale.CHINA);
                map.put("d", sdf3.format(curDate));
//                newxzcfjdsdwPath = "/mnt/sdcard/00_linshiwenshu/linshi_xzcfjdsdw/" + map.get("jibie") +
//                        "安监管罚" + map.get("y") + "煤" + DocumentNumber + "号(单位1).pdf";

//                int sum = 24;
//                //第一行单独做处理
//                if (str.length() < 24) {
//                    substring = halfToFull(str);
//
//                } else {
//                    substring = halfToFull(str.substring(0, sum));
//                    arr = formatData(sum);
//
//                }
                map.put("0", substring);


                if (arr.length > 17) {
                    Toast.makeText(this, "文档容不下，请精简输入内容", Toast.LENGTH_SHORT).show();
                    return;
                }

                //对小于11行的部分填充数据
                if (arr.length <= 6) {

                    for (int i = 0; i < 6; i++) {
                        if (i < arr.length) {
                            map.put(i + 1 + "", arr[i]);
                        } else {
                            map.put(i + 1 + "", "");
                        }

                    }
                }
                //如果分行以后行数大于11，两页的pdf页码分别设置为1和2
                if (arr.length > 6) {
                    map.put("s", "2");
                } else {
                    map.put("s", "1");
                }
                map.put("i", "1");

                //如果分行之后的行数大于11，加载续页的模板并填充数据
                if (arr.length > 6) {
                    //加载续页
                    final String pdfx = "assets/template/xzcfjdsdwt.pdf";
                    final Map<String, String> datax = new HashMap<String, String>();
                    xpdf = "/mnt/sdcard/00_linshiwenshu/linshi_xzcfjdsdw/行政处罚决定书" + DocumentNumber + "（单位2).pdf";
                    //第一页添加数据
                    for (int i = 0; i < 6; i++) {

                        map.put(i + 1 + "", arr[i]);
                    }
                    //第二页添加数据
                    for (int i = 6; i < arr.length; i++) {

                        datax.put(i + 1 + "", arr[i]);

                    }
                    datax.put("s", "2");
                    datax.put("i", "2");

                    datax.put("y", sdf1.format(curDate));
                    datax.put("m", sdf2.format(curDate));
                    datax.put("d", sdf3.format(curDate));
                    datax.put("gd1", etPrintOut03.getText().toString());
                    datax.put("gd2", etPrintOut04.getText().toString());
                    datax.put("xzcf", etPrintOut05.getText().toString());
                    datax.put("bumen1", etPrintOut06.getText().toString());
                    datax.put("bumen2", etPrintOut07.getText().toString());
                    datax.put("bumen3", etPrintOut08.getText().toString());
                    datax.put("bcfdw", etPrintOut09.getText().toString());
                    datax.put("dz", etPrintOut10.getText().toString());
                    datax.put("yb", etPrintOut11.getText().toString());
                    datax.put("fzr", etPrintOut12.getText().toString());
                    datax.put("zw", etPrintOut13.getText().toString());
                    datax.put("lxdh", etPrintOut14.getText().toString());
                    datax.put("danwei", "待定");
                    datax.put("zhanghao", "待定");
//
                    FileUtils.deleteFile(xpdf);

//                    readpdfandFillData(pdfx, datax, xpdf);
                    CommonUtils.readpdfandFillData(bos,fos,xpdf,ps,bf,fontList,fields,pdfx, map);


                }

//                checkmap.put("y", year_02 + "");
//
//                checkmap.put("m", month_02 + "");
//
//                checkmap.put("d", day_02 + "");
                FileUtils.deleteFile(newxzcfjdsdwPath);

//                readpdfandFillData(xzcfjdsdwPath, map, newxzcfjdsdwPath);
                CommonUtils.readpdfandFillData(bos,fos,newxzcfjdsdwPath,ps,bf,fontList,fields,xzcfjdsdwPath, map);

                if (arr.length > 6) {

                    String xzcfjdsdw_zuizhong = "/mnt/sdcard/00_linshiwenshu/linshi_xzcfjdsdw/ 行政处罚决定书" + DocumentNumber + "号(单位).pdf";
                    FileUtils.deleteFile(xzcfjdsdw_zuizhong);
                    combinepdf(xzcfjdsdw_zuizhong, newxzcfjdsdwPath);
                    FileUtils.deleteFile(newxzcfjdsdwPath);
                    FileUtils.deleteFile(xpdf);
                    CommonUtils.doOpenPdf(PrintOut04Activity.this,xzcfjdsdw_zuizhong);

                } else {
                    CommonUtils.doOpenPdf(PrintOut04Activity.this,newxzcfjdsdwPath);

                }


                break;
            case R.id.bt_print_out_02:
                Toast.makeText(this, "上传正在完善", Toast.LENGTH_SHORT).show();
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
            Cursor c = db.rawQuery("SELECT* FROM ELL_EnforcementPlan WHERE PlanId = ?",
                    new String[]{getIntent().getStringExtra("PlanId")});
            c.moveToFirst();
            //获取安检机构信息
            Cursor cursor4 = db.rawQuery("SELECT* FROM Base_Company WHERE CompanyId = ?",
                    new String[]{c.getString(c.getColumnIndex("CompanyId"))});
            cursor4.moveToFirst();
            String[] strings = cursor4.getString(cursor4.getColumnIndex("FullName")).split("安");
            map.put("jibie", strings[0]);
            map.put("bumen1", " 六盘水市");
            map.put("$ADDRESS2$", "六盘水市开投大厦7楼704室 ");//没对上号

            map.put("danwei", "待定");
            map.put("zhanghao", "待定");
//            map.put("$POSTCODE$", "553531");//邮编，记得数据库添加
            Cursor cursor = db.rawQuery("SELECT* FROM ELL_Business WHERE BusinessId = ?",
                    new String[]{c.getString(c.getColumnIndex("BusinessId"))});
            cursor.moveToFirst();
            //获取企业信息
            etPrintOut09.setText(cursor.getString(cursor.getColumnIndex("BusinessName")));
            etPrintOut10.setText(cursor.getString(cursor.getColumnIndex("Address")));
            etPrintOut11.setText("553531");
            etPrintOut12.setText(cursor.getString(cursor.getColumnIndex("LegalPerson")));
            etPrintOut13.setText(cursor.getString(cursor.getColumnIndex("LegalPersonPost")));
            etPrintOut14.setText(cursor.getString(cursor.getColumnIndex("LegalPersonPhone")));
            cursor.close();
            cursor4.close();
            c.close();
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
        } catch (Exception e) {
            Log.e("数据库报错", e.toString());
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

    /**
     * 合并pdf
     */
    private void combinepdf(String combinepath, String pdf) {
        try {
            List<InputStream> pdfs = new ArrayList<InputStream>();
            pdfs.add(new FileInputStream(pdf));
            pdfs.add(new FileInputStream(xpdf));
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
