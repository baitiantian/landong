package com.jinan.ladongjiguan.anjiantong.activity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jinan.ladongjiguan.anjiantong.PublicClass.AssetsDatabaseManager;
import com.jinan.ladongjiguan.anjiantong.utils.SharedPreferencesUtil;
import com.jinan.ladongjiguan.anjiantong.R;
import com.jinan.ladongjiguan.anjiantong.utils.CommonUtils;
import com.jinan.ladongjiguan.anjiantong.utils.FileUtils;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.Range;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by baitiantian on 2017/4/18.
 */

public class PrintOut03Activity extends BaseActivity implements View.OnClickListener, View.OnTouchListener {

    // 单位告知模板文集地址
    private static String no2unitPath = "/mnt/sdcard/doc/notify2unit.doc";
    // 创建生成的文件地址
    private static String newno2unitPath = "/mnt/sdcard/doc/newnotify2unit.doc";
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
    @BindView(R.id.bt_print_out_00)
    Button btPrintOut00;
    @BindView(R.id.bt_print_out_01)
    Button btPrintOut01;
    @BindView(R.id.printf_text)
    TextView printfText;
    private Map<String, String> map = new HashMap<>();
    private String DocumentId = UUID.randomUUID().toString();//文书主键
    private String DocumentNumber = String.valueOf(System.currentTimeMillis());

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
        setContentView(R.layout.print_out_03_layout);
        ButterKnife.bind(this);
        titleLayout.setText("制定单位处罚告知文书");
        getDate();
        etPrintOut01.setText(DocumentNumber);
    }

    @Override
    protected void init() {
        examinePageBack.setOnClickListener(this);
        examinePageBack.setOnTouchListener(this);
        btPrintOut00.setOnClickListener(this);
        btPrintOut01.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
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
                map.put("$CASE$", etPrintOut02.getText().toString());
                map.put("$PROOF2$", "");
                Date curDate = new Date(System.currentTimeMillis());//获取当前时间
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
                map.put("$Y$", sdf.format(curDate));
                sdf = new SimpleDateFormat("MM");
                map.put("$M$", sdf.format(curDate));
                sdf = new SimpleDateFormat("dd");
                map.put("$D$", sdf.format(curDate));
                map.put("$PROOF1$", etPrintOut03.getText().toString());
                map.put("$TIAOKUAN$", etPrintOut04.getText().toString());
                map.put("$LAW$", etPrintOut05.getText().toString());
                map.put("$PUNISH$", etPrintOut06.getText().toString());
                map.put("$TNUM$", etPrintOut01.getText().toString());
                newno2unitPath = "/mnt/sdcard/doc/" + map.get("$JIBIE$") +
                        "安监管罚告" + map.get("$Y$") + "煤" + DocumentNumber + "号(单位).doc";
                try {
                    InputStream inputStream = getAssets().open("notify2unit.doc");
                    FileUtils.writeFile(new File(no2unitPath), inputStream);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //获取模板文件
                File rectifyFile = new File(no2unitPath);
                //创建生成的文件
                FileUtils.deleteFile(newno2unitPath);
                File newrectifyFile = new File(newno2unitPath);
                writeDoc(rectifyFile, newrectifyFile, map);
                //查看
                CommonUtils.doOpenPdf(PrintOut03Activity.this,newno2unitPath);
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
            Cursor c = db.rawQuery("SELECT* FROM ELL_EnforcementPlan WHERE PlanId = ?",
                    new String[]{getIntent().getStringExtra("PlanId")});
            c.moveToFirst();
            //获取安检机构信息
            Cursor cursor4 = db.rawQuery("SELECT* FROM Base_Company WHERE CompanyId = ?",
                    new String[]{c.getString(c.getColumnIndex("CompanyId"))});
            cursor4.moveToFirst();
            map.put("$JIBIE$", cursor4.getString(cursor4.getColumnIndex("FullName")));

            map.put("$ADDRESS1$", " 六盘水市");
            map.put("$ADDRESS2$", "六盘水市开投大厦7楼704室 ");
            map.put("$POSTCODE$", "553000");
            cursor4.close();
            Cursor cursor = db.rawQuery("SELECT* FROM ELL_Business WHERE BusinessId = ?",
                    new String[]{c.getString(c.getColumnIndex("BusinessId"))});
            cursor.moveToFirst();
            map.put("$UNIT$", cursor.getString(cursor.getColumnIndex("BusinessName")));
            map.put("$NAME$", SharedPreferencesUtil.getStringData(this, "Account", null));
            map.put("$PHONE$", SharedPreferencesUtil.getStringData(this, "Mobile", null));
            cursor.close();
            c.close();
        } catch (Exception e) {
            Log.e("数据库报错", e.toString());
        }
    }

    /**
     * demoFile 模板文件
     * newFile 生成文件
     * map 要填充的数据
     */
    public void writeDoc(File demoFile, File newFile, Map<String, String> map) {
        try {
            FileInputStream in = new FileInputStream(demoFile);
            HWPFDocument hdt = new HWPFDocument(in);
            // 读取word文本内容
            Range range = hdt.getRange();

            // 替换文本内容
            for (Map.Entry<String, String> entry : map.entrySet()) {
                range.replaceText(entry.getKey(), entry.getValue());
            }
            ByteArrayOutputStream ostream = new ByteArrayOutputStream();
            FileOutputStream out = new FileOutputStream(newFile, true);
            hdt.write(ostream);
            // 输出字节流
            out.write(ostream.toByteArray());
            out.close();
            ostream.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
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
