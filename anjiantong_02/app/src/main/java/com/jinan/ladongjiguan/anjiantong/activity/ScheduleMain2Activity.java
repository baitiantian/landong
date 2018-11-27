package com.jinan.ladongjiguan.anjiantong.activity;

import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;
import com.jinan.ladongjiguan.anjiantong.R;

import java.text.DecimalFormat;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ScheduleMain2Activity extends BaseActivity implements OnChartValueSelectedListener, View.OnClickListener, View.OnTouchListener {
    @BindView(R.id.examine_page_back)
    LinearLayout mExaminePageBack;
    @BindView(R.id.title_layout)
    TextView titleLayout;
    @BindView(R.id.schedule_piechart)
    PieChart mChart;

    private TextView tvX, tvY;
    private String aP;//计划数
    private String aF;//实际完成数
    private String mArea;//地区
    private String TAG = ScheduleMain2Activity.class.getSimpleName();


    @Override
    protected void initView() {
        setContentView(R.layout.activity_schedule_main2);
        ButterKnife.bind(this);
        titleLayout.setText("计划完成率");
        aP = getIntent().getStringExtra("c[0]") == null ? "0" : getIntent().getStringExtra("c[0]");
        aF = getIntent().getStringExtra("d[0]") == null ? "0" : getIntent().getStringExtra("d[0]");
        aF = Float.parseFloat(aF)>=Float.parseFloat(aP)?aP:aF;
        mArea = getIntent().getStringExtra("b[0]") == null ? "0" : getIntent().getStringExtra("b[0]");
        Log.e(TAG + "-initView", "aP为" + aP + ";aF为" + aF + ";area为" + mArea);
        initChart();
    }

    @Override
    protected void init() {
        mExaminePageBack.setOnClickListener(this);
        mExaminePageBack.setOnTouchListener(this);
    }

    private void initChart() {

        mChart = (PieChart) findViewById(R.id.schedule_piechart);
        mChart.setUsePercentValues(true);
        mChart.getDescription().setEnabled(false);
        mChart.setExtraOffsets(5, 10, 5, 5);

        mChart.setDragDecelerationFrictionCoef(0.95f);
        mChart.setCenterTextRadiusPercent(50);
        mChart.setCenterTextTypeface(mTfLight);
        //设置圆心处的内容
        mChart.setCenterText(generateCenterSpannableText());
        //是否中心留白
        mChart.setDrawHoleEnabled(true);
        mChart.setHoleColor(Color.WHITE);

        mChart.setTransparentCircleColor(Color.WHITE);
        mChart.setTransparentCircleAlpha(110);

        mChart.setHoleRadius(58f);
        mChart.setTransparentCircleRadius(61f);

        mChart.setDrawCenterText(true);

        mChart.setRotationAngle(0);
        // enable rotation of the chart by touch可以手动旋转
        mChart.setRotationEnabled(true);
        mChart.setHighlightPerTapEnabled(true);
//设置比例块换行...  
//        mChart.setWordWrapEnabled(true);
//        mLegend.setDirection(Legend.LegendDirection.LEFT_TO_RIGHT);
        // mChart.setDrawUnitsInChart(true);

        // add a selection listener
        mChart.setOnChartValueSelectedListener(this);

        setData(4, 100);

        mChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);
        // mChart.spin(2000, 0, 360);

        //设置图例
        Legend l = mChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(0f);
        l.setYOffset(0f);
        // TODO: 2018/11/9 换行
//        l.setWordWrapEnabled(true);
//        l.setDirection(Legend.LegendDirection.LEFT_TO_RIGHT);

        // entry label styling 环形图上的文字设置透明
//        mChart.setEntryLabelColor(Color.WHITE);
        mChart.setEntryLabelColor(Color.TRANSPARENT);
        mChart.setEntryLabelTypeface(mTfRegular);
        mChart.setEntryLabelTextSize(12f);
    }

    private void setData(int count, float range) {

        float mult = range;

        ArrayList<PieEntry> entries = new ArrayList<PieEntry>();

        // NOTE: The order of the entries when being added to the entries array determines their position around the center of
        // the chart.环形上文字和数字的设置
        entries.add(0, new PieEntry(Float.parseFloat(aF.trim()), "已完成计划", getResources().getDrawable(R.drawable.scan)));
        entries.add(1, new PieEntry(Float.parseFloat(aP.trim()) - Float.parseFloat(aF.trim()), "未完成计划", getResources().getDrawable(R.drawable.scan)));

        PieDataSet dataSet = new PieDataSet(entries, "Election Results");

        dataSet.setDrawIcons(false);

        dataSet.setSliceSpace(3f);
        dataSet.setIconsOffset(new MPPointF(0, 40));
        dataSet.setSelectionShift(5f);
        // TODO: 2018/11/9 jia 
//        dataSet.setValueTextColor(Color.TRANSPARENT);
        // add a lot of colors

        ArrayList<Integer> colors = new ArrayList<Integer>();

        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);
        colors.add(ColorTemplate.getHoloBlue());

        dataSet.setColors(colors);
        //dataSet.setSelectionShift(0f);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        // 环形图上的数字设置透明
//        data.setValueTextColor(Color.WHITE);
        data.setValueTextColor(Color.TRANSPARENT);
        data.setValueTypeface(mTfLight);
        mChart.setData(data);

        // undo all highlights
        mChart.highlightValues(null);

        mChart.invalidate();
    }

    private SpannableString generateCenterSpannableText() {
//        float finish = Float.parseFloat(aF.trim());
//        ArrayList<PieEntry> entries = new ArrayList<PieEntry>();
//        entries.add(0, new PieEntry(Float.parseFloat(aF.trim()), ""+mArea, getResources().getDrawable(R.drawable.scan)));
//        PieDataSet dataSet = new PieDataSet(entries, "");
//        PieData data = new PieData(dataSet);
//        data.setValueFormatter(new PercentFormatter());
//        String a = data.getDataSet().getLabel();
//        Log.e(TAG+"-generateCenter","圆心文字为"+a);
//        SpannableString s = new SpannableString(a);
        float f = Float.parseFloat(aF.trim())*100/Float.parseFloat(aP.trim());
        DecimalFormat percentFormatter = new DecimalFormat("###,###,##0.0");
        String finish = percentFormatter.format(f) + " %";
        SpannableString s = new SpannableString(mArea + "完成率" + finish);
        s.setSpan(new RelativeSizeSpan(1.7f), 0, s.length(), 0);
        /*s.setSpan(new RelativeSizeSpan(1.7f), 0, mArea.length(), 0);
        s.setSpan(new StyleSpan(Typeface.NORMAL), mArea.length(), s.length(), 0);*/
        s.setSpan(new ForegroundColorSpan(ColorTemplate.getHoloBlue()), 0, s.length(), 0);
//        s.setSpan(new RelativeSizeSpan(.8f), 14, s.length() - 15, 0);
//        s.setSpan(new StyleSpan(Typeface.ITALIC), s.length() - 7, s.length(), 0);
//        s.setSpan(new ForegroundColorSpan(ColorTemplate.getHoloBlue()), s.length() - 7, s.length(), 0);
        return s;
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {

        if (e == null)
            return;
        Log.i("VAL SELECTED",
                "Value: " + e.getY() + ", index: " + h.getX()
                        + ", DataSet index: " + h.getDataSetIndex());
    }

    @Override
    public void onNothingSelected() {
        Log.i("PieChart", "nothing selected");
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

}
