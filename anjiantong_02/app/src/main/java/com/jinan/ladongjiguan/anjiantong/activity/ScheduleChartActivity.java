package com.jinan.ladongjiguan.anjiantong.activity;


import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.jinan.ladongjiguan.anjiantong.PublicClass.AssetsDatabaseManager;
import com.jinan.ladongjiguan.anjiantong.PublicClass.DayAxisValueFormatter;
import com.jinan.ladongjiguan.anjiantong.adapter.SearchAdapter;
import com.jinan.ladongjiguan.anjiantong.adapter.TimeSelectAdapter;
import com.jinan.ladongjiguan.anjiantong.utils.SharedPreferencesUtil;
import com.jinan.ladongjiguan.anjiantong.utils.WebServiceUtils;
import com.jinan.ladongjiguan.anjiantong.R;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;
import org.ksoap2.serialization.SoapObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ScheduleChartActivity extends BaseActivity implements View.OnClickListener, View.OnTouchListener {

    @BindView(R.id.examine_page_back)
    LinearLayout examinePageBack;
    @BindView(R.id.title_layout)
    TextView titleLayout;
    @BindView(R.id.schedule_chart_barchart)
    BarChart mChart;
    @BindView(R.id.schedule_chart_spinner)
    Spinner mSpinner;
    @BindView(R.id.schedule_chart_spinner_time)
    Spinner mSpinnerTime;

    private String WEB_SERVER_URL;
    private List<Float> mPlannumList = new ArrayList<>();
    private List<Float> mActrualnumList = new ArrayList<>();
    private List<String> mAreaNameList = new ArrayList<>();
    private List<Float> mPlannumList_after = new ArrayList<>();
    private List<Float> mActrualnumList_after = new ArrayList<>();
    private List<String> mAreaNameList_after = new ArrayList<>();
    private List<String> mAreaCodeList = new ArrayList<>();
    private List<String> mAreaCodeList_after = new ArrayList<>();
    private List<String> mAreaCodeList_defined = new ArrayList<>();
    //520200六盘水   520203六枝   520222盘州   520221水城   520201钟山
    private String[] mAreaCodeArray_defined = {"520200", "520203", "520222", "520221", "520201", "520202", "520204"};//mAreaCodeArray_defined本地定7个数值

    private TimeSelectAdapter mSpinnerTimeAdapter;
    private List<Object> mTimeDatas2;
    private String mSpinnerText = "";//下拉列表选择的
    private String mSpinnerTimeText = "";//下拉列表选择的
    private int mLastSpinnerTimeText;//前一年
    private int mFinalSpinnerTimeText;//后一年

    private String TAG = ScheduleChartActivity.class.getSimpleName();
    private IAxisValueFormatter mXAxisValueFormatter;
    private String[] mAreaNameArray;
    private SQLiteDatabase mDb;

    @Override
    protected void initView() {
        setContentView(R.layout.activity_schedule_chart);
        ButterKnife.bind(this);
        titleLayout.setText("计划进度");
        mSpinnerTimeText = Calendar.getInstance().get(Calendar.YEAR) + "";
        mLastSpinnerTimeText = Integer.parseInt(mSpinnerTimeText) - 1;
        mFinalSpinnerTimeText = Integer.parseInt(mSpinnerTimeText) + 1;
        Log.e(TAG + "-initView", "mSpinnerTimeText为" + mSpinnerTimeText + ";Last为" + mLastSpinnerTimeText + ";Final为" + mFinalSpinnerTimeText);
        WEB_SERVER_URL = SharedPreferencesUtil.getStringData(this, "IPString", "");
        /**
         * 下拉列表
         * */
        Resources res = getResources();
        final String[] spinnerText = res.getStringArray(R.array.s_car_num_17);
        mSpinner.setDropDownVerticalOffset(60);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mSpinnerText = "";
                Log.e(TAG, "position-mSpinner为" + position);
                mSpinnerText = spinnerText[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        final String[] spinnerTimeText = {mLastSpinnerTimeText + "",mSpinnerTimeText,  mFinalSpinnerTimeText + ""};
        mSpinnerTimeAdapter = new TimeSelectAdapter(ScheduleChartActivity.this, spinnerTimeText);

        mSpinnerTime.setAdapter(mSpinnerTimeAdapter);
        mSpinnerTime.setDropDownVerticalOffset(60);
        mSpinnerTime.setGravity(Gravity.CENTER_HORIZONTAL);
        mSpinnerTime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mSpinnerTimeText = "";
                Log.e(TAG, "position-mSpinnerTime为" + position);
                String a = mSpinnerTimeAdapter.getItem(position).toString();
                mSpinnerTimeText = mSpinnerTime.getItemAtPosition(position).toString();
                Log.e(TAG+"-onItemSelected","mSpinnerTimeText-"+mSpinnerTimeText+";a为"+a);
                httpQuest();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mSpinnerTime.setSelection(1);
    }

    private void httpQuest() {
        Log.e(TAG, "httpQuest-选择的行业类型为" + mSpinnerText + ";选择的时间为" + mSpinnerTimeText);
        // 初始化，只需要调用一次
        AssetsDatabaseManager.initManager(getApplication());
        // 获取管理对象，因为数据库需要通过管理对象才能够获取
        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
        mDb = mg.getDatabase("users.db");
        //添加参数
        HashMap<String, String> properties = new HashMap<>();
        properties.put("Xml", "<Request><data  code='select-AnnualPlan'><no><year>" + mSpinnerTimeText + "</year><region>null</region><businessType>null</businessType></no></data></Request>");
        properties.put("Token", "");
        WebServiceUtils.callWebService(WEB_SERVER_URL, "DynamicInvoke", properties, new WebServiceUtils.WebServiceCallBack() {
            @Override
            public void callBack(SoapObject result) {
//                Log.e(TAG,"resulr"+result);
                if (result != null) {
                    try {
                        Log.e(TAG, "result为" + result);
                        Object detail = result.getProperty("DynamicInvokeResult");
                        JSONObject jsonObj;
                        jsonObj = XML.toJSONObject(detail.toString());
                        if (jsonObj.has("DocumentElement")) {
                            mChart.setVisibility(View.VISIBLE);
                            JSONArray array;
                            JSONObject obj;
                            mPlannumList = new ArrayList<>();
                            mActrualnumList = new ArrayList<>();
                            mAreaNameList = new ArrayList<>();
                            mPlannumList_after = new ArrayList<>();
                            mActrualnumList_after = new ArrayList<>();
                            mAreaNameList_after = new ArrayList<>();
                            mAreaCodeList = new ArrayList<>();
                            mAreaCodeList_after = new ArrayList<>();
                            mAreaCodeList_defined = new ArrayList<>();
                            if (jsonObj.has("DocumentElement") && jsonObj.toString().substring(jsonObj.toString().length() - 3, jsonObj.toString().length() - 2).equals("}")) {
                                obj = jsonObj.getJSONObject("DocumentElement").getJSONObject("Table");
                                setData(obj);
                            } else if (jsonObj.has("DocumentElement") && jsonObj.toString().substring(jsonObj.toString().length() - 3, jsonObj.toString().length() - 2).equals("]")) {
                                array = jsonObj.getJSONObject("DocumentElement").getJSONArray("Table");
                                for (int i = 0; i < array.length(); i++) {
                                    obj = array.getJSONObject(i);
                                    setData(obj);
                                }
                            }
                            paixu();
                            mAreaNameArray = mAreaNameList_after.toArray(new String[mAreaNameList_after.size()]);
                            initChart();
                            setChartData();

                        } else {
                            Toast.makeText(ScheduleChartActivity.this, "没有数据", Toast.LENGTH_SHORT).show();
                            mChart.setVisibility(View.GONE);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "捕捉异常" + e.getMessage(), e);
                    }
                } else {
                    Toast.makeText(ScheduleChartActivity.this, "网络响应失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //520200六盘水   520203六枝   520222盘州   520221水城   520201钟山
    private void paixu() {
        try {
            mAreaCodeList_defined = Arrays.asList(mAreaCodeArray_defined);
            Collections.sort(mAreaCodeList_after, new Comparator<String>() {
                @Override
                public int compare(String o1, String o2) {
                    int a = mAreaCodeList_defined.indexOf(o1);
                    int b = mAreaCodeList_defined.indexOf(o2);
                    return a - b;
                }
            });

            for (int i = 0; i < mAreaCodeList_after.size(); i++) {//排序好的
                for (int j = 0; j < mAreaCodeList.size(); j++) {//未排序的
                    if (mAreaCodeList.get(j).equals(mAreaCodeList_after.get(i))) {//0-0  1-0  2-0
                        mPlannumList_after.set(i, mPlannumList.get(j));
                        mActrualnumList_after.set(i, mActrualnumList.get(j));
                        mAreaNameList_after.set(i, mAreaNameList.get(j));
                    }
                }
            }
        } catch (Exception e) {
            Toast.makeText(ScheduleChartActivity.this, "数据返回异常", Toast.LENGTH_SHORT).show();
        }
    }

    private void setData(JSONObject object) {
        try {
            Cursor cursor = mDb.rawQuery("SELECT* FROM Base_Company WHERE CompanyId = ?",
                    new String[]{object.getString("regioncode")});
            cursor.moveToFirst();
            String nameAll2 = cursor.getString(cursor.getColumnIndex("FullName"));
            String name2;
            if (nameAll2.contains("安全")) {
                name2 = nameAll2.substring(0, nameAll2.indexOf("安全"));
            } else {
                name2 = nameAll2;
            }
            mAreaNameList.add(name2);
            mAreaNameList_after.add(name2);
            mAreaCodeList.add(object.getString("regioncode"));
            mAreaCodeList_after.add(object.getString("regioncode"));
            cursor.close();
            mPlannumList.add(Float.valueOf(object.getString("annualplancounts")));
            mPlannumList_after.add(Float.valueOf(object.getString("annualplancounts")));
            try {
                mActrualnumList.add(Float.valueOf(object.getString("plancounts")));
                mActrualnumList_after.add(Float.valueOf(object.getString("plancounts")));
            } catch (Exception e) {
                mActrualnumList.add(0f);
                mActrualnumList_after.add(0f);
            }
        } catch (Exception e) {
            Log.e(TAG, "setData-异常" + e.getMessage().toString(), e);
        }

    }

    private void initChart() {
        mChart.getDescription().setEnabled(false);
        mChart.setPinchZoom(true);//设置按比例放缩柱状图
        mChart.setExtraBottomOffset(10);
        mChart.setExtraTopOffset(30);

        //x坐标轴设置
        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(true);
        xAxis.setDrawLabels(true);//是否画x轴坐标
        xAxis.setGranularity(1f);//防止间距小时挤在一块
        xAxis.setLabelCount(mAreaNameList_after.size());
        xAxis.setAxisLineWidth(1f);
        xAxis.setCenterAxisLabels(true);//设置标签居中
        xAxis.setValueFormatter(new IndexAxisValueFormatter(mAreaNameList_after));//mAreaNameList

        //y轴设置
        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setDrawGridLines(true);
        leftAxis.setDrawLabels(true);//是否画y轴坐标
        leftAxis.setAxisLineWidth(1f);//设置y轴宽度

        //设置坐标轴最大最小值
        Float yMin1 = Collections.min(mPlannumList_after);//mPlannumList
        Float yMin2 = Collections.min(mActrualnumList_after);//mActrualnumList
        Float yMax1 = Collections.max(mPlannumList_after);//mPlannumList
        Float yMax2 = Collections.max(mActrualnumList_after);
        Float yMin = Double.valueOf((yMin1 < yMin2 ? yMin1 : yMin2) * 0.1).floatValue();
        Float yMax = Double.valueOf((yMax1 > yMax2 ? yMax1 : yMax2) * 1.1).floatValue();
        leftAxis.setAxisMaximum(yMax);
        leftAxis.setAxisMinimum(yMin);

        mChart.getAxisRight().setEnabled(false);
        //图例设置
        Legend legend = mChart.getLegend();
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);
        legend.setDirection(Legend.LegendDirection.LEFT_TO_RIGHT);
        legend.setForm(Legend.LegendForm.SQUARE);
        legend.setTextSize(12f);

        //设置柱状图数据
        setChartData();
        mChart.animateX(1500);//数据显示动画，从左往右依次显示
        mChart.invalidate();
    }

    private void setChartData() {
        // 为了使 柱状图成为可滑动的,将水平方向 放大 2.5倍
        if (mPlannumList_after.size() > 3) {//mPlannumList
            Matrix mMatrix = new Matrix();
            mMatrix.postScale(1.5f, 1f);
            mChart.getViewPortHandler().refresh(mMatrix, mChart, false);
            mChart.animateY(1000);
        } else {
            mChart.animateY(1000);
        }

        mXAxisValueFormatter = new DayAxisValueFormatter(mChart, mAreaNameArray);//mAreaNameArray
        float groupSpace = 0.04f;
        float barSpace = 0.03f;
        float barWidth = 0.45f;
        // (0.45 + 0.03) * 2 + 0.04 = 1，即一个间隔为一组，包含两个柱图 -> interval per "group"

        ArrayList<BarEntry> entries1 = new ArrayList<>();
        ArrayList<BarEntry> entries2 = new ArrayList<>();

        for (int i = 0; i < mPlannumList_after.size(); ++i) {//mPlannumList
            entries1.add(new BarEntry(i, mPlannumList_after.get(i)));
            entries2.add(new BarEntry(i, mActrualnumList_after.get(i)));
        }

        BarDataSet dataset1, dataset2;

        if (mChart.getData() != null && mChart.getData().getDataSetCount() > 0) {
            dataset1 = (BarDataSet) mChart.getData().getDataSetByIndex(0);
//            Log.e(TAG,""+mChart.getData().getDataSetByIndex(0).toString()+"-"+mChart.getData().getDataSetByIndex(1));
            dataset2 = (BarDataSet) mChart.getData().getDataSetByIndex(1);
            dataset1.setValues(entries1);
            dataset2.setValues(entries2);
            mChart.getData().notifyDataChanged();
            mChart.notifyDataSetChanged();
        } else {
            dataset1 = new BarDataSet(entries1, "计划完成数");
            dataset2 = new BarDataSet(entries2, "实际完成数");
            dataset1.setColor(Color.rgb(60, 220, 78));
            dataset1.setValueTextColor(Color.rgb(60, 220, 78));
            dataset1.setValueTextSize(16f);
            dataset2.setColors(new int[]{Color.rgb(61, 165, 255)});
            dataset2.setValueTextColor(Color.rgb(61, 165, 255));
            dataset2.setValueTextSize(16f);

            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
            dataSets.add(dataset1);
            dataSets.add(dataset2);

            BarData data = new BarData(dataSets);
            data.setValueTextSize(14f);
            data.setBarWidth(0.9f);
            data.setValueFormatter(new IValueFormatter() {
                @Override
                public String getFormattedValue(float value, Entry entry, int i, ViewPortHandler viewPortHandler) {
                    return double2String(value, 2);
                }
            });

            mChart.setData(data);
        }

        mChart.getBarData().setBarWidth(barWidth);
        mChart.getXAxis().setAxisMinimum(0f);
        // barData.getGroupWith(...) is a helper that calculates the width each group needs based on the provided parameters
//        mChart.getXAxis().setAxisMaximum(Float.parseFloat(mChart.getBarData().getGroupWidth(groupSpace, barSpace) * xAxisValue.size() + xAxisValue.get(0)));
        mChart.getXAxis().setAxisMaximum(mChart.getBarData().getGroupWidth(groupSpace, barSpace) * mAreaNameList.size());
//        mChart.groupBars(Float.parseFloat(xAxisValue.get(0)), groupSpace, barSpace);
        mChart.groupBars(0, groupSpace, barSpace);

        mChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                String area = mXAxisValueFormatter.getFormattedValue(e.getX(), null);
                Log.e(TAG, "" + e.getX() + "area-1为" + area);
                for (int i = 0; i < mAreaNameList_after.size(); i++) {
                    Intent intent = new Intent();
                    Bundle bundle;
                    if (area.equals(mAreaNameList_after.get(i))) {
                        bundle = new Bundle();
                        bundle.putString("c[0]", mPlannumList_after.get(i).toString());
                        bundle.putString("d[0]", mActrualnumList_after.get(i).toString());
                        bundle.putString("b[0]", mAreaNameList_after.get(i));
                        Log.e(TAG, "-onValueSelectedc[]" + mPlannumList_after.get(i) + "d[]" + mActrualnumList_after.get(i) + "b[]" + mAreaNameList_after.get(i));
                        intent.putExtras(bundle);
                        intent.setClass(ScheduleChartActivity.this, ScheduleMain2Activity.class);
                        startActivity(intent, bundle);
                    }
                }
            }

            @Override
            public void onNothingSelected() {

            }
        });
    }

    @Override
    protected void init() {
        examinePageBack.setOnClickListener(this);
        examinePageBack.setOnTouchListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.examine_page_back://返回键
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

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
    }

    public String double2String(double d, int num) {
        NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setMaximumFractionDigits(num);//保留两位小数
        nf.setGroupingUsed(false);//去掉数值中的千位分隔符

        String temp = nf.format(d);
        if (temp.contains(".")) {
            String s1 = temp.split("\\.")[0];
            String s2 = temp.split("\\.")[1];
            for (int i = s2.length(); i > 0; --i) {
                if (!s2.substring(i - 1, i).equals("0")) {
                    return s1 + "." + s2.substring(0, i);
                }
            }
            return s1;
        }
        return temp;
    }
}
