package com.jinan.ladongjiguan.djj8plus.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import java.util.Calendar;

public class TimeClockView extends View {
    private int width;
    private int height;
    private Paint mPaintLine;
    private Paint mPaintCircle;
    private Paint mPaintHour;
    private Paint mPaintMinute;
    private Paint mPaintSec;
    private TextPaint mPaintText;
    private Calendar mCalendar;
    public static final int START_ONDRAW = 0X23;

    //每隔一秒，在handler中调用一次重新绘制方法
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case START_ONDRAW:
                    mCalendar = Calendar.getInstance();
                    invalidate();//告诉UI主线程重新绘制
                    handler.sendEmptyMessageDelayed(START_ONDRAW, 1000);
                    break;
                default:
                    break;
            }
        }
    };

    public TimeClockView(Context context) {
        super(context);
    }

    public TimeClockView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mCalendar = Calendar.getInstance();

        mPaintLine = new Paint();
        mPaintLine.setColor(Color.GREEN);
        mPaintLine.setStrokeWidth(2);
        mPaintLine.setAntiAlias(true);//设置是否抗锯齿
        mPaintLine.setStyle(Paint.Style.STROKE);//设置绘制风格

        mPaintCircle = new Paint();
        mPaintCircle.setColor(Color.WHITE);//设置颜色
        mPaintCircle.setStrokeWidth(2);//设置线宽
        mPaintCircle.setAntiAlias(true);//设置是否抗锯齿
        mPaintCircle.setStyle(Paint.Style.FILL);//设置绘制风格

        mPaintText = new TextPaint();
        mPaintText.setColor(Color.WHITE);
        mPaintText.setStrokeWidth(5);
        mPaintText.setTextAlign(Paint.Align.CENTER);
        mPaintText.setTextSize(30);

        mPaintHour = new Paint();
        mPaintHour.setStrokeWidth(6);
        mPaintHour.setColor(Color.WHITE);
        mPaintHour.setAntiAlias(true);

        mPaintMinute = new Paint();
        mPaintMinute.setStrokeWidth(4);
        mPaintMinute.setColor(Color.WHITE);
        mPaintMinute.setAntiAlias(true);

        mPaintSec = new Paint();
        mPaintSec.setStrokeWidth(2);
        mPaintSec.setColor(Color.WHITE);
        mPaintSec.setAntiAlias(true);

        handler.sendEmptyMessage(START_ONDRAW);//向handler发送一个消息，让它开启重绘
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        height = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int circleRadius ; //模拟时钟的圆半径大小
        if (width > height) {
            circleRadius = height / 2 -10;
        } else {
            circleRadius = width / 2 -10;
        }
        //画出圆中心
        canvas.drawCircle(width / 2, height / 2, 5, mPaintCircle);
        //依次旋转画布，画出每个刻度和对应数字
        for (int i = 1; i <= 60; i++) {
            canvas.save();//保存当前画布
            if (i % 5 == 0) {
                //将画布进行以圆心以固定的角度旋转进行旋转
                canvas.rotate(360 / 60 * i, width / 2, height / 2);
                //设置字体大小，这里是以圆半径的十分之一大小
                mPaintText.setTextSize(circleRadius / 10);
                //如果绘制对应的数字时只进行一次旋转是不能达到目标的，需要再次以书写文字的地方在进行反向旋转这样写出来的就是正向的
                canvas.rotate(-360 / 60 * i, width / 2, height / 2 - circleRadius+5);
                canvas.drawText("" + i / 5, width / 2, height / 2 - circleRadius+circleRadius / 20 , mPaintText);
            } else {
                canvas.rotate(360 / 60 * i, width / 2, height / 2);
                //左起：起始位置x坐标，起始位置y坐标，终止位置x坐标，终止位置y坐标，画笔(一个Paint对象)
                canvas.drawCircle(width/2,height/2-circleRadius,2,mPaintCircle);
            }
            canvas.restore();
        }

        int minute = mCalendar.get(Calendar.MINUTE);//得到当前分钟数
        int hour = mCalendar.get(Calendar.HOUR);//得到当前小时数
        int sec = mCalendar.get(Calendar.SECOND);//得到当前秒数
        String time;
        if (sec < 10 && hour < 10 && minute < 10) { //都小于10
            time = "0" + hour + ":0" + minute + ":0" + sec; //02:02:02
        } else if (sec < 10 && hour < 10 && minute > 9) {//分钟大于9
            time = "0" + hour + ":" + minute + ":0" + sec; //02:12:02
        } else if (sec > 9 && hour < 10 && minute < 10) {//秒大于9
            time = "0" + hour + ":0" + minute + ":" + sec; //02:02:12
        } else if (sec < 10 && hour > 9 && minute < 10) {//时大于9
            time = hour + ":0" + minute + ":0" + sec; //12:02:02
        } else if (sec < 10 && hour > 9 && minute > 9) {//时分于9
            time = hour + ":" + minute + ":0" + sec; //12:12:02
        } else if (sec > 9 && hour > 9 && minute < 10) {//时秒大于9
            time = hour + ":0" + minute + ":" + sec; //12:02:12
        } else if (sec > 9 && hour < 10 && minute > 9) {//分秒大于9
            time = "0" + hour + ":" + minute + ":" + sec; //02:12:12
        } else {
            time = hour + ":" + minute + ":" + sec; //12:12:12
        }
        //绘制中心下方的时间显示
        mPaintText.setTextSize(circleRadius / 10 * 2);
        canvas.save();
        canvas.drawText(time, width / 2, height / 2 + circleRadius / 10 * 4, mPaintText);

        //绘制时分秒相应的指针
        float minuteDegree = minute / 60f * 360;//得到分针旋转的角度
        canvas.save();
        canvas.rotate(minuteDegree, width / 2, height / 2);
        canvas.drawLine(width / 2, height / 2 - circleRadius + circleRadius / 3, width / 2, height / 2 + circleRadius / 6, mPaintMinute);
        canvas.restore();

        float hourDegree = (hour * 60 + minute) / 12f / 60 * 360;//得到时钟旋转的角度
        canvas.save();
        canvas.rotate(hourDegree, width / 2, height / 2);
        canvas.drawLine(width / 2, height / 2 - circleRadius + circleRadius / 2, width / 2, height / 2 + circleRadius / 9, mPaintHour);
        canvas.restore();

        float secDegree = sec / 60f * 360;//得到秒针旋转的角度
        canvas.save();
        canvas.rotate(secDegree, width / 2, height / 2);
        canvas.drawLine(width / 2, height / 2 - circleRadius + 2, width / 2, height / 2 + circleRadius / 4, mPaintSec);
        canvas.restore();

    }
}