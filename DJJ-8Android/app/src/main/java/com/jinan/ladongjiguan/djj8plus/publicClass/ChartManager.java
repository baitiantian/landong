package com.jinan.ladongjiguan.djj8plus.publicClass;

import android.graphics.Color;
import android.util.Log;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.jinan.ladongjiguan.djj8plus.bean.SearchBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by wangfuchun on 2018/7/23.
 */

public class ChartManager {
    private LineChart mLineChart;
    private YAxis leftAxis;
    private YAxis rightAxis;
    private XAxis xAxis;
    private LineData lineData;
    private LineDataSet lineDataSet;
    private List<ILineDataSet> lineDataSets = new ArrayList<>();
    private List<SearchBean> mDataModels = new ArrayList<>();
    private List<String> mXValueDate = new ArrayList<>();
    private List<String> mYValueName = new ArrayList<>();//所有的折线的名字
    private List<String> timeList = new ArrayList<>(); //存储x轴的时间
    private float mMin;
    private String TAG = ChartManager.class.getSimpleName();
    private final Random mRandom;

    //多条曲线
//    public ChartManager(LineChart mLineChart, List<String> xValueDate, List<SearchBean> dataModels, float min) {
    public ChartManager(LineChart lineChart, List<String> xValueDate, List<String> yValueName, float min) {
        this.mLineChart = lineChart;
        leftAxis = mLineChart.getAxisLeft();
        rightAxis = mLineChart.getAxisRight();
        rightAxis.setDrawAxisLine(false);
        rightAxis.setEnabled(false);
        xAxis = mLineChart.getXAxis();
        this.mXValueDate = xValueDate;
//        this.mDataModels = dataModels;
        this.mYValueName = yValueName;
        this.mMin = min;
        mRandom = new Random();
        initLineChart();
        initLineDataSet();
    }

    /**
     * 初始化LineChar
     */
    private void initLineChart() {

        mLineChart.setDrawGridBackground(false);
        //显示边界
        mLineChart.setDrawBorders(true);
        //折线图例 标签 设置
        Legend legend = mLineChart.getLegend();
        legend.setForm(Legend.LegendForm.LINE);
        legend.setTextSize(11f);
        //显示位置
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);

        //X轴设置显示位置在底部
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(mXValueDate.size());
        xAxis.setAxisMaximum(mXValueDate.size() - 1);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                Log.e(TAG+"-initLineChart","xAxis-value为"+value);
                return timeList.get((int) value % timeList.size());
            }
        });

        //保证Y轴从0开始，不然会上移一点
        leftAxis.setAxisMinimum(mMin);
        leftAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                Log.e(TAG + "-initLineChart", "value为" + value);
                return value + "";
            }
        });
    }

    /**
     * 初始化折线（多条线）
     */
    private void initLineDataSet() {

//        for (int i = 0; i < mDataModels.size(); i++) {
        for (int i = 0; i < mYValueName.size(); i++) {
            int R = mRandom.nextInt(256);
            int G = mRandom.nextInt(256);
            int B = mRandom.nextInt(256);
//            lineDataSet = new LineDataSet(null, "折线" + i);
            lineDataSet = new LineDataSet(null, "-" + mYValueName.get(i));
            lineDataSet.setColor(Color.rgb(R, G, B));
            lineDataSet.setLineWidth(1.5f);
            lineDataSet.setCircleRadius(1.5f);
            lineDataSet.setColor(Color.rgb(R, G, B));
            lineDataSet.setDrawFilled(true);
            lineDataSet.setCircleColor(Color.rgb(R, G, B));
            lineDataSet.setHighLightColor(Color.rgb(R, G, B));
            lineDataSet.setValueTextColor(Color.rgb(R, G, B));
            lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
            lineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
            lineDataSet.setValueTextSize(10f);
            lineDataSet.setValueFormatter(new IValueFormatter() {
                @Override
                public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                    Log.e(TAG + "-initLineDataSet", "lineDataSet-value为" + value);
                    return value + "";
                }
            });
            lineDataSets.add(lineDataSet);
        }
        //添加一个空的 LineData
        lineData = new LineData();
        mLineChart.setData(lineData);
        mLineChart.invalidate();
    }

    /**
     * 动态添加数据（多条折线图）
     *
     * @param yy y轴所有的数据集合
     */
    public void addEntry(Float[][] yy) {
        if (lineDataSets.get(0).getEntryCount() == 0) {
            lineData = new LineData(lineDataSets);
            mLineChart.setData(lineData);
        }
        if (mXValueDate != null) {
            timeList.clear();
            for (int i = 0; i < mXValueDate.size(); i++) {
                Log.e(TAG + "-addEntry", "-mName.get(i)为" + mXValueDate.get(i));
                timeList.add(mXValueDate.get(i));
            }
        }

//        for (int i = 0; i < mDataModels.size(); i++) {
        for (int i = 0; i < mYValueName.size(); i++) {
            for (int j = 0; j < yy[i].length; j++) {
                Log.e(TAG + "-addEntry", "yy[i][j]为" + yy[i][j]);
                Entry entry = new Entry(j, yy[i][j]);
                lineData.addEntry(entry, i);
                lineData.notifyDataChanged();
                mLineChart.notifyDataSetChanged();
                mLineChart.setVisibleXRangeMaximum(6);
                mLineChart.moveViewToX((float) lineData.getEntryCount() - 5f);
            }
        }
    }

    /**
     * 设置Y轴值
     *
     * @param max
     * @param min
     * @param labelCount
     */
    public void setYAxis(float max, float min, int labelCount) {
        if (max < min) {
            return;
        }
        leftAxis.setAxisMaximum(max);
        leftAxis.setAxisMinimum(min);
        leftAxis.setLabelCount(labelCount, false);
        mLineChart.invalidate();
    }

    /**
     * 设置高限制线
     *
     * @param high
     * @param name
     */
    public void setHightLimitLine(float high, String name, int color) {
        if (name == null) {
            name = "高限制线";
        }
        LimitLine hightLimit = new LimitLine(high, name);
        hightLimit.setLineWidth(4f);
        hightLimit.setTextSize(10f);
        hightLimit.setLineColor(color);
        hightLimit.setTextColor(color);
        leftAxis.addLimitLine(hightLimit);
        mLineChart.invalidate();
    }

    /**
     * 设置低限制线
     *
     * @param low
     * @param name
     */
    public void setLowLimitLine(int low, String name) {
        if (name == null) {
            name = "低限制线";
        }
        LimitLine hightLimit = new LimitLine(low, name);
        hightLimit.setLineWidth(4f);
        hightLimit.setTextSize(10f);
        leftAxis.addLimitLine(hightLimit);
        mLineChart.invalidate();
    }

    /**
     * 设置描述信息
     *
     * @param str
     */
    public void setDescription(String str) {
        Description description = new Description();
        description.setText(str);
        mLineChart.setDescription(description);
        mLineChart.invalidate();
    }
}
