package com.jinan.ladongjiguan.djj8plus.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.jinan.ladongjiguan.djj8plus.R;
import com.jinan.ladongjiguan.djj8plus.bean.SearchBean;
import com.jinan.ladongjiguan.djj8plus.publicClass.AssetsDatabaseManager;
import com.jinan.ladongjiguan.djj8plus.publicClass.ChartManager;
import com.jinan.ladongjiguan.djj8plus.publicClass.SharedPreferencesUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MultiLineChartActivity extends BaseActivity implements OnChartValueSelectedListener {

    @BindView(R.id.activity_multiline_chart_chart)
    LineChart mChart;
    @BindView(R.id.examine_page_back)
    LinearLayout examinePageBack;
    @BindView(R.id.title_layout)
    TextView titleLayout;

    private AssetsDatabaseManager mMg;
    private SQLiteDatabase mDb;
    private String mState;
    private String mGanhaoId_get;
    private String mTunnelId_get;
    private Cursor mCursor1;
    private SearchBean mSearchBean1, mSearchBean2, mSearchBean3, mSearchBean5, mSearchBean6, mSearchBean7, mSearchBean8, mSearchBean9, mSearchBean10, mSearchBean11;
    private Cursor mCursor2;
    private Cursor mCursor11;
    private Cursor mCursor10;
    private Cursor mCursor9;
    private Cursor mCursor7;
    private Cursor mCursor6;
    private Cursor mCursor5;
    private Cursor mCursor3;
    private Cursor mCursor8;
    private List<SearchBean> mDataModels1 = new ArrayList<>();
    private List<SearchBean> mDataModels2 = new ArrayList<>();
    private List<SearchBean> mDataModels3 = new ArrayList<>();
    private List<SearchBean> mDataModels5 = new ArrayList<>();
    private List<SearchBean> mDataModels6 = new ArrayList<>();
    private List<SearchBean> mDataModels7 = new ArrayList<>();
    private List<SearchBean> mDataModels8 = new ArrayList<>();
    private List<SearchBean> mDataModels9 = new ArrayList<>();
    private List<SearchBean> mDataModels10 = new ArrayList<>();
    private List<SearchBean> mDataModels11 = new ArrayList<>();

    private float mMaxY1, mMinY1;
    private String TAG = MultiLineChartActivity.class.getSimpleName();
    private ChartManager mChartManager;
    private List<String> mXValueDate = new ArrayList<>(); //折线横坐标名字集合
    private List<String> mYValueName = new ArrayList<>(); //折线横坐标名字集合
    private List<Float> mYValue = new ArrayList<>();//y轴数据集合
    public static final int CHOOSE_BACK_SEARCH = 1010;
    public static String CHOOSE_STATE = "choose_state";
    public static String CHART_BACK = "chart_back";
    public static String CHOOSE_GANHAO = "choose_ganhao";
    public static String CHOOSE_TUNNELID = "choose_tunnelid";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_multi_line_chart);
        ButterKnife.bind(this);
        // 初始化，只需要调用一次
        AssetsDatabaseManager.initManager(MultiLineChartActivity.this);
        // 获取管理对象，因为数据库需要通过管理对象才能够获取
        mMg = AssetsDatabaseManager.getManager();
        // 通过管理对象获取数据库
        mDb = mMg.getDatabase("DJJ-8data.db");
        mState = getIntent().getStringExtra(CHOOSE_STATE) == null ? "" : getIntent().getStringExtra(CHOOSE_STATE);
        mGanhaoId_get = getIntent().getStringExtra(CHOOSE_GANHAO) == null ? "" : getIntent().getStringExtra(CHOOSE_GANHAO);
        mTunnelId_get = getIntent().getStringExtra(CHOOSE_TUNNELID) == null ? "" : getIntent().getStringExtra(CHOOSE_TUNNELID);
        Log.e(TAG + "-onCreate", "mState为" + mState + ";mTunnelId_get为" + mTunnelId_get);
        titleLayout.setText("历史数据曲线图");
        examinePageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        initChart();
    }

    private void initChart() {
        mChart.setOnChartValueSelectedListener(this);
        // no description text
        mChart.getDescription().setEnabled(false);
        // enable touch gestures
        mChart.setTouchEnabled(true);
        mChart.setDragDecelerationFrictionCoef(0.9f);
        // enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setDrawGridBackground(false);
        mChart.setHighlightPerDragEnabled(true);
        // if disabled, scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(true);
        mChart.setBackgroundColor(Color.LTGRAY);

        getData();
        mChart.animateX(2500);

    }

    /**
     * mCursor
     * mYValueName
     */
    private void getData() {
        switch (mState) {
            case "d1":
                mCursor1 = mDb.rawQuery("SELECT* FROM ELL_D1Data WHERE Num = ?",
                        new String[]{mGanhaoId_get});
                mXValueDate = new ArrayList<>();
                mYValueName = new ArrayList<>();
                mYValue = new ArrayList<>();
                mYValueName.add("导高");
                mYValueName.add("拉出值");
                mYValueName.add("轨距");
                mYValueName.add("超高");
                while (mCursor1.moveToNext()) {
                    String dataId = mCursor1.getString(mCursor1.getColumnIndex("DataId"));
                    Log.e(TAG + "-getData(1)", "dataId为" + dataId);
                    getNewData(mState);
                }
                Log.e(TAG + "-getData", "mDataModels1.size为" + mDataModels1.size());
                setChart(mYValue, mDataModels1);
                break;
            case "d2":
                mCursor2 = mDb.rawQuery("SELECT* FROM ELL_D2Data WHERE Num = ?",
                        new String[]{mGanhaoId_get});
                mXValueDate = new ArrayList<>();
                mYValueName = new ArrayList<>();
                mYValue = new ArrayList<>();
                mYValueName.add("导高");
                mYValueName.add("偏离值");
                mYValueName.add("内轨距");
                while (mCursor2.moveToNext()) {
                    String dataId = mCursor2.getString(mCursor2.getColumnIndex("DataId"));
                    Log.e(TAG + "-getData(2)", "dataId为" + dataId);
                    getNewData(mState);
                }
                Log.e(TAG + "-getData", "mDataModels2.size为" + mDataModels2.size());
                setChart(mYValue, mDataModels2);
                break;
            case "d3":
                mCursor3 = mDb.rawQuery("SELECT* FROM ELL_D3Data WHERE Num = ?",
                        new String[]{mGanhaoId_get});
                mXValueDate = new ArrayList<>();
                mYValueName = new ArrayList<>();
                mYValue = new ArrayList<>();
                mYValueName.add("红线标高");
                mYValueName.add("测量限界");
                while (mCursor3.moveToNext()) {
                    String dataId = mCursor3.getString(mCursor3.getColumnIndex("DataId"));
                    Log.e(TAG + "-getData(3)", "dataId为" + dataId);
                    getNewData(mState);
                }
                Log.e(TAG + "-getData", "mDataModels3.size为" + mDataModels3.size() + ";mYValue.size为" + mYValue.size());
                setChart(mYValue, mDataModels3);
                break;
            case "d5":
                mCursor5 = mDb.rawQuery("SELECT* FROM ELL_D5Data WHERE Num = ?",
                        new String[]{mGanhaoId_get});
                mXValueDate = new ArrayList<>();
                mYValueName = new ArrayList<>();
                mYValue = new ArrayList<>();
                mYValueName.add("非支抬高");
                mYValueName.add("非支偏离");
                while (mCursor5.moveToNext()) {
                    String dataId = mCursor5.getString(mCursor5.getColumnIndex("DataId"));
                    Log.e(TAG + "-getData(5)", "dataId为" + dataId);
                    getNewData(mState);
                }
                Log.e(TAG + "-getData", "mDataModels5.size为" + mDataModels5.size() + ";mYValue.size()为" + mYValue.size());
                setChart(mYValue, mDataModels5);
                break;
            case "d6":
                mCursor6 = mDb.rawQuery("SELECT* FROM ELL_D6Data WHERE Num = ?",
                        new String[]{mGanhaoId_get});
                mXValueDate = new ArrayList<>();
                mYValueName = new ArrayList<>();
                mYValue = new ArrayList<>();
                mYValueName.add("定位器坡度");
                mYValueName.add("高差");
                while (mCursor6.moveToNext()) {
                    String dataId = mCursor6.getString(mCursor6.getColumnIndex("DataId"));
                    Log.e(TAG + "-getData(6)", "dataId为" + dataId);
                    getNewData(mState);
                }
                Log.e(TAG + "-getData", "mDataModels6.size为" + mDataModels6.size() + ";mYValue.size()为" + mYValue.size());
                setChart(mYValue, mDataModels6);
                break;
            case "d7":
                mCursor7 = mDb.rawQuery("SELECT* FROM ELL_D7Data WHERE Num = ?",
                        new String[]{mGanhaoId_get});
                mXValueDate = new ArrayList<>();
                mYValueName = new ArrayList<>();
                mYValue = new ArrayList<>();
                mYValueName.add("高度1");
                mYValueName.add("高度2");
                mYValueName.add("高差");
                while (mCursor7.moveToNext()) {
                    String dataId = mCursor7.getString(mCursor7.getColumnIndex("DataId"));
                    Log.e(TAG + "-getData(7)", "dataId为" + dataId);
                    getNewData(mState);
                }
                Log.e(TAG + "-getData", "mDataModels7.size为" + mDataModels7.size());
                setChart(mYValue, mDataModels7);
                break;
            case "d8":
                mCursor8 = mDb.rawQuery("SELECT* FROM ELL_D8Data WHERE Num = ?",
                        new String[]{mGanhaoId_get});
                mXValueDate = new ArrayList<>();
                mYValueName = new ArrayList<>();
                mYValue = new ArrayList<>();
                mYValueName.add("高度1");
                mYValueName.add("线距");
                mYValueName.add("高差");
                while (mCursor8.moveToNext()) {
                    String dataId = mCursor8.getString(mCursor8.getColumnIndex("DataId"));
                    Log.e(TAG + "-getData(8)", "dataId为" + dataId);
                    getNewData(mState);
                }
                Log.e(TAG + "-getData", "mDataModels8.size为" + mDataModels8.size());
                setChart(mYValue, mDataModels8);
                break;
            case "d9":
                mCursor9 = mDb.rawQuery("SELECT* FROM ELL_D9Data WHERE Num = ?",
                        new String[]{mGanhaoId_get});
                mXValueDate = new ArrayList<>();
                mYValueName = new ArrayList<>();
                mYValue = new ArrayList<>();
                mYValueName.add("水平距");
                mYValueName.add("垂直距");
                while (mCursor9.moveToNext()) {
                    String dataId = mCursor9.getString(mCursor9.getColumnIndex("DataId"));
                    Log.e(TAG + "-getData(9)", "dataId为" + dataId);
                    getNewData(mState);
                }
                Log.e(TAG + "-getData", "mDataModels9.size为" + mDataModels9.size());
                setChart(mYValue, mDataModels9);
                break;
            case "dc":
                mCursor10 = mDb.rawQuery("SELECT* FROM ELL_DCData WHERE Num = ?",
                        new String[]{mGanhaoId_get});
                mXValueDate = new ArrayList<>();
                mYValueName = new ArrayList<>();
                mYValue = new ArrayList<>();
                mYValueName.add("支柱垂直度");
                while (mCursor10.moveToNext()) {
                    String dataId = mCursor10.getString(mCursor10.getColumnIndex("DataId"));
                    Log.e(TAG + "-getData(10)", "dataId为" + dataId);
                    getNewData(mState);
                }
                Log.e(TAG + "-getData", "mDataModels10.size为" + mDataModels10.size());
                setChart(mYValue, mDataModels10);
                break;
            case "de":
                mCursor11 = mDb.rawQuery("SELECT* FROM ELL_DEData WHERE Num = ?",
                        new String[]{mGanhaoId_get});
                mXValueDate = new ArrayList<>();
                mYValueName = new ArrayList<>();
                mYValue = new ArrayList<>();
                mYValueName.add("支柱跨距");
                while (mCursor11.moveToNext()) {
                    String dataId = mCursor11.getString(mCursor11.getColumnIndex("DataId"));
                    Log.e(TAG + "-getData(11)", "dataId为" + dataId);
                    getNewData(mState);
                }
                Log.e(TAG + "-getData", "mDataModels11.size为" + mDataModels11.size());
                setChart(mYValue, mDataModels11);
                break;

        }
    }

    /**
     * mSearchBean
     * mDataModels
     */
    private void getNewData(String state) {
        Log.e(TAG + "-getNewData", "state为" + state);
        if (state != "") {
            switch (state) {
                case "d1":
                    mSearchBean1 = new SearchBean();
                    mSearchBean1.name = "基础测量";
                    mSearchBean1.num = mCursor1.getString(mCursor1.getColumnIndex("Num"));
                    mSearchBean1.d1_dg = mCursor1.getString(mCursor1.getColumnIndex("D1_DG"));
                    mSearchBean1.d1_lcz = mCursor1.getString(mCursor1.getColumnIndex("D1_LCZ"));
                    mSearchBean1.d1_gj = mCursor1.getString(mCursor1.getColumnIndex("D1_GJ"));
                    mSearchBean1.d1_cg = mCursor1.getString(mCursor1.getColumnIndex("D1_CG"));
                    mSearchBean1.date = mCursor1.getString(mCursor1.getColumnIndex("Date"));
                    String date1 = mSearchBean1.date;
                    Log.e(TAG + "-getNewData-11", "date11为" + date1);
                    if (date1.contains("日")) {
//                        date1 = date1.substring(0,date1.indexOf("日")+1);
                        date1 = date1.substring(0, date1.indexOf("年")) + ". " + date1.substring(5, date1.indexOf("月")) + "." + date1.substring(8, date1.indexOf("日"));
                    }
                    Log.e(TAG + "-getNewData-12", "date12为" + date1);
                    mXValueDate.add(date1);
                    mSearchBean1.note = mCursor1.getString(mCursor1.getColumnIndex("Note"));
                    mYValue.add(Float.valueOf(mSearchBean1.d1_dg));
                    mYValue.add(Float.valueOf(mSearchBean1.d1_lcz));
                    mYValue.add(Float.valueOf(mSearchBean1.d1_gj));
                    mYValue.add(Float.valueOf(mSearchBean1.d1_cg));
                    mDataModels1.add(mSearchBean1);
                    break;
                case "d2":
                    mSearchBean2 = new SearchBean();
                    mSearchBean2.name = "线岔中心测量";
                    mSearchBean2.num = mCursor2.getString(mCursor2.getColumnIndex("Num"));
                    mSearchBean2.d1_dg = mCursor2.getString(mCursor2.getColumnIndex("D2_DG"));
                    mSearchBean2.d1_lcz = mCursor2.getString(mCursor2.getColumnIndex("D2_PLZ"));
                    mSearchBean2.d1_gj = mCursor2.getString(mCursor2.getColumnIndex("D2_NGJ"));
                    mSearchBean2.date = mCursor2.getString(mCursor2.getColumnIndex("Date"));
                    Log.e(TAG + "-getNewData-21", "date21为" + mSearchBean2.date);
                    String date2 = mSearchBean2.date;
                    if (date2.contains("日")) {
//                        date2 = date2.substring(0,date2.indexOf("日")+1);
                        date2 = date2.substring(0, date2.indexOf("年")) + ". " + date2.substring(5, date2.indexOf("月")) + "." + date2.substring(8, date2.indexOf("日"));
                    }
                    Log.e(TAG + "-getNewData-22", "date22为" + date2);
                    mXValueDate.add(date2);
                    mSearchBean2.note = mCursor2.getString(mCursor2.getColumnIndex("Note"));
                    mYValue.add(Float.valueOf(mSearchBean2.d1_dg));
                    mYValue.add(Float.valueOf(mSearchBean2.d1_lcz));
                    mYValue.add(Float.valueOf(mSearchBean2.d1_gj));
                    mDataModels2.add(mSearchBean2);
                    break;
                case "d3":
                    mSearchBean3 = new SearchBean();
                    mSearchBean3.name = "限界测量";
                    mSearchBean3.num = mCursor3.getString(mCursor3.getColumnIndex("Num"));
                    mSearchBean3.d1_dg = mCursor3.getString(mCursor3.getColumnIndex("D3_HXBG"));
                    mSearchBean3.d1_lcz = mCursor3.getString(mCursor3.getColumnIndex("D3_CLXJ"));
                    mSearchBean3.date = mCursor3.getString(mCursor3.getColumnIndex("Date"));
                    Log.e(TAG + "-getNewData-31", "date31为" + mSearchBean3.date);
                    String date3 = mSearchBean3.date;
                    if (date3.contains("日")) {
//                        date3 = date3.substring(0,date3.indexOf("日")+1);
                        date3 = date3.substring(0, date3.indexOf("年")) + ". " + date3.substring(5, date3.indexOf("月")) + "." + date3.substring(8, date3.indexOf("日"));
                    }
                    Log.e(TAG + "-getNewData-22", "date32为" + date3);
                    mXValueDate.add(date3);
                    mSearchBean3.note = mCursor3.getString(mCursor3.getColumnIndex("Note"));
                    mYValue.add(Float.valueOf(mSearchBean3.d1_dg));
                    mYValue.add(Float.valueOf(mSearchBean3.d1_lcz));
                    mDataModels3.add(mSearchBean3);
                    Log.e(TAG + "-d3", "1为" + mSearchBean3.d1_dg + ";2为" + mSearchBean3.d1_lcz);
                    break;
                case "d5":
                    mSearchBean5 = new SearchBean();
                    mSearchBean5.name = "非支测量";
                    mSearchBean5.num = mCursor5.getString(mCursor5.getColumnIndex("Num"));
                    mSearchBean5.d1_dg = mCursor5.getString(mCursor5.getColumnIndex("D5_FZTG"));
                    mSearchBean5.d1_lcz = mCursor5.getString(mCursor5.getColumnIndex("D5_FZPL"));
                    mSearchBean5.date = mCursor5.getString(mCursor5.getColumnIndex("Date"));
                    Log.e(TAG + "-getNewData-51", "date51为" + mSearchBean5.date);
                    String date5 = mSearchBean5.date;
                    if (date5.contains("日")) {
//                        date5 = date5.substring(0,date5.indexOf("日")+1);
                        date5 = date5.substring(0, date5.indexOf("年")) + ". " + date5.substring(5, date5.indexOf("月")) + "." + date5.substring(8, date5.indexOf("日"));
                    }
                    Log.e(TAG + "-getNewData-52", "date52为" + date5);
                    mXValueDate.add(date5);
                    mSearchBean5.note = mCursor5.getString(mCursor5.getColumnIndex("Note"));
                    mYValue.add(Float.valueOf(mSearchBean5.d1_dg));
                    mYValue.add(Float.valueOf(mSearchBean5.d1_lcz));
                    mDataModels5.add(mSearchBean5);
                    break;
                case "d6":
                    mSearchBean6 = new SearchBean();
                    mSearchBean6.name = "定位器测量";
                    mSearchBean6.num = mCursor6.getString(mCursor6.getColumnIndex("Num"));
                    mSearchBean6.d1_dg = mCursor6.getString(mCursor6.getColumnIndex("D6_DWQPD"));
                    mSearchBean6.d1_lcz = mCursor6.getString(mCursor6.getColumnIndex("D6_GC"));
                    mSearchBean6.date = mCursor6.getString(mCursor6.getColumnIndex("Date"));
                    Log.e(TAG + "-getNewData-61", "date为" + mSearchBean6.date);
                    String date6 = mSearchBean6.date;
                    if (date6.contains("日")) {
//                        date6 = date6.substring(0,date6.indexOf("日")+1);
                        date6 = date6.substring(0, date6.indexOf("年")) + ". " + date6.substring(5, date6.indexOf("月")) + "." + date6.substring(8, date6.indexOf("日"));
                    }
                    Log.e(TAG + "-getNewData-62", "date62为" + date6);
                    mXValueDate.add(date6);
                    mSearchBean6.note = mCursor6.getString(mCursor6.getColumnIndex("Note"));
                    mYValue.add(Float.valueOf(mSearchBean6.d1_dg));
                    mYValue.add(Float.valueOf(mSearchBean6.d1_lcz));
                    mDataModels6.add(mSearchBean6);
                    break;
                case "d7":
                    mSearchBean7 = new SearchBean();
                    mSearchBean7.name = "承力索高差测量";
                    mSearchBean7.num = mCursor7.getString(mCursor7.getColumnIndex("Num"));
                    mSearchBean7.d1_dg = mCursor7.getString(mCursor7.getColumnIndex("D7_GD1"));
                    mSearchBean7.d1_lcz = mCursor7.getString(mCursor7.getColumnIndex("D7_GD2"));
                    mSearchBean7.d1_gj = mCursor7.getString(mCursor7.getColumnIndex("D7_GC"));
                    mSearchBean7.date = mCursor7.getString(mCursor7.getColumnIndex("Date"));
                    Log.e(TAG + "-getNewData-71", "date71为" + mSearchBean7.date);
                    String date7 = mSearchBean7.date;
                    if (date7.contains("日")) {
//                        date7 = date7.substring(0,date7.indexOf("日")+1);
                        date7 = date7.substring(0, date7.indexOf("年")) + ". " + date7.substring(5, date7.indexOf("月")) + "." + date7.substring(8, date7.indexOf("日"));
                    }
                    Log.e(TAG + "-getNewData-72", "date72为" + date7);
                    mXValueDate.add(date7);
                    mSearchBean7.note = mCursor7.getString(mCursor7.getColumnIndex("Note"));
                    mYValue.add(Float.valueOf(mSearchBean7.d1_dg));
                    mYValue.add(Float.valueOf(mSearchBean7.d1_lcz));
                    mYValue.add(Float.valueOf(mSearchBean7.d1_gj));
                    mDataModels7.add(mSearchBean7);
                    break;
                case "d8":
                    mSearchBean8 = new SearchBean();
                    mSearchBean8.name = "500mm处高差测量";
                    mSearchBean8.num = mCursor8.getString(mCursor8.getColumnIndex("Num"));
                    mSearchBean8.d1_dg = mCursor8.getString(mCursor8.getColumnIndex("D8_GD1"));
                    mSearchBean8.d1_lcz = mCursor8.getString(mCursor8.getColumnIndex("D8_XJ"));
                    mSearchBean8.d1_gj = mCursor8.getString(mCursor8.getColumnIndex("D8_GC"));
                    mSearchBean8.date = mCursor8.getString(mCursor8.getColumnIndex("Date"));
                    Log.e(TAG + "-getNewData-81", "date81为" + mSearchBean8.date);
                    String date8 = mSearchBean8.date;
                    if (date8.contains("日")) {
//                        date8 = date8.substring(0,date8.indexOf("日")+1);
                        date8 = date8.substring(0, date8.indexOf("年")) + ". " + date8.substring(5, date8.indexOf("月")) + "." + date8.substring(8, date8.indexOf("日"));
                    }
                    Log.e(TAG + "-getNewData-82", "date82为" + date8);
                    mXValueDate.add(date8);
                    mSearchBean8.note = mCursor8.getString(mCursor8.getColumnIndex("Note"));
                    mYValue.add(Float.valueOf(mSearchBean8.d1_dg));
                    mYValue.add(Float.valueOf(mSearchBean8.d1_lcz));
                    mYValue.add(Float.valueOf(mSearchBean8.d1_gj));
                    mDataModels8.add(mSearchBean8);
                    break;
                case "d9":
                    mSearchBean9 = new SearchBean();
                    mSearchBean9.name = "自由测量";
                    mSearchBean9.num = mCursor9.getString(mCursor9.getColumnIndex("Num"));
                    mSearchBean9.d1_dg = mCursor9.getString(mCursor9.getColumnIndex("D9_SPJ"));
                    mSearchBean9.d1_lcz = mCursor9.getString(mCursor9.getColumnIndex("D9_CZJ"));
                    mSearchBean9.date = mCursor9.getString(mCursor9.getColumnIndex("Date"));
                    Log.e(TAG + "-getNewData-91", "date91为" + mSearchBean9.date);
                    String date9 = mSearchBean9.date;
                    if (date9.contains("日")) {
//                        date9 = date9.substring(0,date9.indexOf("日")+1);
                        date9 = date9.substring(0, date9.indexOf("年")) + ". " + date9.substring(5, date9.indexOf("月")) + "." + date9.substring(8, date9.indexOf("日"));
                    }
                    Log.e(TAG + "-getNewData-92", "date92为" + date9);
                    mXValueDate.add(date9);
                    mSearchBean9.note = mCursor9.getString(mCursor9.getColumnIndex("Note"));
                    mYValue.add(Float.valueOf(mSearchBean9.d1_dg));
                    mYValue.add(Float.valueOf(mSearchBean9.d1_lcz));
                    mDataModels9.add(mSearchBean9);
                    break;
                case "dc":
                    mSearchBean10 = new SearchBean();
                    mSearchBean10.name = "垂直测量";
                    mSearchBean10.num = mCursor10.getString(mCursor10.getColumnIndex("Num"));
                    mSearchBean10.d1_dg = mCursor10.getString(mCursor10.getColumnIndex("DC_ZZCZD"));
                    mSearchBean10.date = mCursor10.getString(mCursor10.getColumnIndex("Date"));
                    Log.e(TAG + "-getNewData-101", "date101为" + mSearchBean10.date);
                    String date10 = mSearchBean10.date;
                    if (date10.contains("日")) {
//                        date10 = date10.substring(0,date10.indexOf("日"));
                        date10 = date10.substring(0, date10.indexOf("年")) + ". " + date10.substring(5, date10.indexOf("月")) + "." + date10.substring(8, date10.indexOf("日"));
                        Log.e(TAG + "-getNewData-10", "date10为" + date10);
                    }
                    Log.e(TAG + "-getNewData-102", "date102为" + date10);
                    mXValueDate.add(date10);
                    mSearchBean10.note = mCursor10.getString(mCursor10.getColumnIndex("Note"));
                    mYValue.add(Float.valueOf(mSearchBean10.d1_dg));
                    mDataModels10.add(mSearchBean10);
                    break;
                case "de":
                    mSearchBean11 = new SearchBean();
                    mSearchBean11.name = "跨距测量";
                    mSearchBean11.num = mCursor11.getString(mCursor11.getColumnIndex("Num"));
                    mSearchBean11.d1_dg = mCursor11.getString(mCursor11.getColumnIndex("DE_ZZKD"));
                    mSearchBean11.date = mCursor11.getString(mCursor11.getColumnIndex("Date"));
                    Log.e(TAG + "-getNewData-11", "date为" + mSearchBean11.date);
                    String date11 = mSearchBean11.date;
                    if (date11.contains("日")) {
//                        date11 = date11.substring(0,date11.indexOf("日")+1);
                        date11 = date11.substring(0, date11.indexOf("年")) + ". " + date11.substring(5, date11.indexOf("月")) + "." + date11.substring(8, date11.indexOf("日"));
                    }
                    Log.e(TAG + "-getNewData-112", "date112为" + date11);
                    mXValueDate.add(date11);
                    mSearchBean11.note = mCursor11.getString(mCursor11.getColumnIndex("Note"));
                    mYValue.add(Float.valueOf(mSearchBean11.d1_dg));
                    mDataModels11.add(mSearchBean11);
                    break;
                default:
                    break;
            }
        }
    }

    private void setChart(List<Float> yValue, List<SearchBean> dataModels) {
        Float[] float1 = new Float[yValue.size()];
        for (int i = 0; i < yValue.size(); i++) {//9个数值
            float1[i] = yValue.get(i);
            Log.e(TAG + "-floats[i]", "floats[i]为" + float1[i]);
        }
        /**
         * 获取y轴最大值和最小值
         * */
        float temp = 0.0f;
        for (int i = 0; i < yValue.size(); i++) {//0,1,2
            for (int j = i + 1; j < yValue.size(); j++) {//1,2  2
                if (float1[i] > float1[j]) {
                    temp = float1[i];
                    float1[i] = float1[j];
                    float1[j] = temp;
                }
            }
        }
        for (int i = 0; i < float1.length; i++) {
            Log.e(TAG + "-排序后的数组", "为" + float1[i]);
        }
        mMaxY1 = float1[float1.length - 1];
        mMinY1 = float1[0];
        Log.e(TAG + "-setChart(max)", "max1为" + mMaxY1 + ";min1为" + mMinY1);
        if (mMinY1 >= 0f) {
            mMinY1 = 0f;
        } else {
            mMinY1 = mMinY1 - 200f;
        }
        mMaxY1 = mMaxY1 + 200f;
        Log.e(TAG + "-setChart", "max为" + mMaxY1 + ";min" + mMinY1);
        mChartManager = new ChartManager(mChart, mXValueDate, mYValueName, mMinY1);
        mChartManager.setYAxis(mMaxY1, mMinY1, (int) (mMaxY1 - mMinY1) / 10);
        /**
         * mYValue.size()=4;
         * mYValueName.size()=2;
         * mDataModels5.size()=2;
         * 四个数据：0，1，2，3
         * */
//        Float[][] floats = new Float[dataModels.size()][mYValueName.size()];
        Float[][] floats = new Float[mYValueName.size()][mXValueDate.size()];
//        for (int i = 0; i < dataModels.size(); i++) {
        for (int i = 0; i < mYValueName.size(); i++) {
//            for (int j = 0; j < mYValueName.size(); j++) {//4个数值
            for (int j = 0; j < mXValueDate.size(); j++) {//2个数值
//                floats[i][j] = yValue.get(j + i * mYValueName.size());//0,1  2,3  4,5  6,7
                floats[i][j] = yValue.get(i + j * mYValueName.size());//0,4  1,5  2,6  3,7
                Log.e(TAG + "-floats[i][j]", "floats[i][j]为" + floats[i][j]);
            }
        }
        mChartManager.addEntry(floats);
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        Log.e("Entry selected", e.toString());
        mChart.centerViewToAnimated(e.getX(), e.getY(), mChart.getData().getDataSetByIndex(h.getDataSetIndex())
                .getAxisDependency(), 500);
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void init() {

    }

    @Override
    public void onNothingSelected() {
        Log.i("Nothing selected", "Nothing selected.");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent();
        SharedPreferencesUtil.saveBooleanData(this, CHART_BACK, true);
        intent.putExtra(CHOOSE_STATE, mState);
        intent.putExtra(CHOOSE_GANHAO, mGanhaoId_get);
        intent.putExtra(CHOOSE_TUNNELID, mTunnelId_get);
        // Set result and finish this Activity
        setResult(Activity.RESULT_OK, intent);
        finish();
        overridePendingTransition(0, R.anim.zoomout);
    }
}
