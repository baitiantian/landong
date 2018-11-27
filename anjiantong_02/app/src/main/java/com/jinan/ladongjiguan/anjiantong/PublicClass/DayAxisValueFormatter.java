package com.jinan.ladongjiguan.anjiantong.PublicClass;

import android.util.Log;

import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

/**
 * Created by philipp on 02/06/16.
 */
public class DayAxisValueFormatter implements IAxisValueFormatter {

    private String TAG = DayAxisValueFormatter.class.getSimpleName();
    private BarLineChartBase<?> chart;
    private String[] mArea;
    private String[] mAreaNull = {""};

    public DayAxisValueFormatter(BarLineChartBase<?> chart, String[] area) {
        this.chart = chart;
        this.mArea = area;
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        Log.e(TAG, "mArea为" + mArea[(int) value]);
        if((int)value==mArea.length){
            return mAreaNull[0];
        }else{
            return mArea[(int)value];
        }
//      return mArea[(int) (value-1)];
    }
}
