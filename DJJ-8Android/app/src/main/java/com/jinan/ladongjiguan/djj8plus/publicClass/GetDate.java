package com.jinan.ladongjiguan.djj8plus.publicClass;

import android.content.Context;

import java.util.Calendar;


public class GetDate {
    public static String Date(){
        String date = "";
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;// Java月份从0开始算
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        date = year+"年"+month+"月"+day+"日";
        calendar.set(Calendar.YEAR, year);//指定年份
        calendar.set(Calendar.MONTH, month - 1);//指定月份 Java月份从0开始算
        int daysCountOfMonth = calendar.getActualMaximum(Calendar.DATE);//获取指定年份中指定月份有几天

        //获取指定年份月份中指定某天是星期几
        calendar.set(Calendar.DAY_OF_MONTH, day);  //指定日
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        switch (dayOfWeek)
        {
            case 1:
                date=date+"  星期日";
                break;
            case 2:
                date=date+"  星期一";
                break;
            case 3:
                date=date+"  星期二";
                break;
            case 4:
                date=date+"  星期三";
                break;
            case 5:
                date=date+"  星期四";
                break;
            case 6:
                date=date+"  星期五";
                break;
            case 7:
                date=date+"  星期六";
                break;
        }
        return date;
    }
}
