package com.jinan.ladongjiguan.djj8plus.publicClass;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;

import com.jinan.ladongjiguan.djj8plus.R;

import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.Date;

public class DoubleDatePickerDialog extends AlertDialog implements OnClickListener, OnDateChangedListener {

    private static final String START_YEAR = "start_year";
    private static final String END_YEAR = "end_year";
    private static final String START_MONTH = "start_month";
    private static final String END_MONTH = "end_month";
    private static final String START_DAY = "start_day";
    private static final String END_DAY = "end_day";

    private final DatePicker mDatePicker_start;
    private final DatePicker mDatePicker_end;
    private final OnDateSetListener mCallBack;
    private String TAG = DoubleDatePickerDialog.class.getSimpleName();

    /**
     * The callback used to indicate the user is done filling in the date.
     */
    public interface OnDateSetListener {


        void onDateSet(DatePicker startDatePicker, int startYear, int startMonthOfYear, int startDayOfMonth,
                       DatePicker endDatePicker, int endYear, int endMonthOfYear, int endDayOfMonth);
    }


    public DoubleDatePickerDialog(Context context, OnDateSetListener callBack, int year, int monthOfYear, int dayOfMonth) {
        this(context, 0, callBack, year, monthOfYear, dayOfMonth);
    }

    public DoubleDatePickerDialog(Context context, int theme, OnDateSetListener callBack, int year, int monthOfYear,
                                  int dayOfMonth) {
        this(context, 0, callBack, year, monthOfYear, dayOfMonth, true);
    }

    /**
     * @param context     The context the dialog is to run in.
     * @param theme       the theme to apply to this dialog
     * @param callBack    How the parent is notified that the date is set.
     * @param year        The initial year of the dialog.
     * @param monthOfYear The initial month of the dialog.
     * @param dayOfMonth  The initial day of the dialog.
     */
    public DoubleDatePickerDialog(Context context, int theme, OnDateSetListener callBack, int year, int monthOfYear,
                                  int dayOfMonth, boolean isDayVisible) {
        super(context, theme);

        mCallBack = callBack;

        Context themeContext = getContext();
        setButton(BUTTON_POSITIVE, "确 定", this);
        setButton(BUTTON_NEGATIVE, "取 消", this);
        // setButton(BUTTON_POSITIVE,
        // themeContext.getText(android.R.string.date_time_done), this);
        setIcon(0);

        LayoutInflater inflater = (LayoutInflater) themeContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_double_date_picker, null);
        setView(view);
        mDatePicker_start = (DatePicker) view.findViewById(R.id.dialog_double_datepicker_start);
        mDatePicker_end = (DatePicker) view.findViewById(R.id.dialog_double_datepicker_end);
        mDatePicker_start.init(year, monthOfYear, dayOfMonth, this);
        mDatePicker_end.init(year, monthOfYear, dayOfMonth, this);
        // updateTitle(year, monthOfYear, dayOfMonth);

        // 如果要隐藏当前日期，则使用下面方法。
        if (!isDayVisible) {
            hidDay(mDatePicker_start);
            hidDay(mDatePicker_end);
        }
    }

    /**
     * 隐藏DatePicker中的日期显示
     *
     * @param mDatePicker
     */
    private void hidDay(DatePicker mDatePicker) {
        Field[] datePickerfFields = mDatePicker.getClass().getDeclaredFields();
        for (Field datePickerField : datePickerfFields) {
            if ("mDaySpinner".equals(datePickerField.getName())) {
                datePickerField.setAccessible(true);
                Object dayPicker = new Object();
                try {
                    dayPicker = datePickerField.get(mDatePicker);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }
                // datePicker.getCalendarView().setVisibility(View.GONE);
                ((View) dayPicker).setVisibility(View.GONE);
            }
        }
    }

    public void onClick(DialogInterface dialog, int which) {
        // Log.d(this.getClass().getSimpleName(), String.format("which:%d",
        // which));
        // 如果是“取 消”按钮，则返回，如果是“确 定”按钮，则往下执行
        if (which == BUTTON_POSITIVE) {
            tryNotifyDateSet();
        } else if (which == BUTTON_NEGATIVE) {
            Log.e(TAG + "-onClick", "tab为BUTTON_NEGATIVE");
            if (mCallBack != null) {
                mDatePicker_start.clearFocus();
                mDatePicker_end.clearFocus();
                Calendar calendar = Calendar.getInstance();
                mCallBack.onDateSet(mDatePicker_start, 1990, 0, 1, mDatePicker_end, calendar.get(calendar.YEAR),
                        calendar.get(calendar.MONTH), calendar.get(calendar.DATE));
            }
        } else if (which == BUTTON_NEUTRAL) {
            Log.e(TAG + "-onClick", "tab为BUTTON_NEUTRAL");
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (mCallBack != null) {
            mDatePicker_start.clearFocus();
            mDatePicker_end.clearFocus();
            Calendar calendar = Calendar.getInstance();
            mCallBack.onDateSet(mDatePicker_start, 1990, 0, 1, mDatePicker_end, calendar.get(calendar.YEAR),
                    calendar.get(calendar.MONTH), calendar.get(calendar.DATE));
        }
    }

    @Override
    public void onDateChanged(DatePicker view, int year, int month, int day) {
        if (view.getId() == R.id.dialog_double_datepicker_start)
            mDatePicker_start.init(year, month, day, this);
        if (view.getId() == R.id.dialog_double_datepicker_end)
            mDatePicker_end.init(year, month, day, this);
        // updateTitle(year, month, day);
    }

    /**
     * 获得开始日期的DatePicker
     *
     * @return The calendar view.
     */
    public DatePicker getDatePickerStart() {
        return mDatePicker_start;
    }

    /**
     * 获得结束日期的DatePicker
     *
     * @return The calendar view.
     */
    public DatePicker getDatePickerEnd() {
        return mDatePicker_end;
    }

    /**
     * Sets the start date.
     *
     * @param year        The date year.
     * @param monthOfYear The date month.
     * @param dayOfMonth  The date day of month.
     */
    public void updateStartDate(int year, int monthOfYear, int dayOfMonth) {
        mDatePicker_start.updateDate(year, monthOfYear, dayOfMonth);
    }

    /**
     * Sets the end date.
     *
     * @param year        The date year.
     * @param monthOfYear The date month.
     * @param dayOfMonth  The date day of month.
     */
    public void updateEndDate(int year, int monthOfYear, int dayOfMonth) {
        mDatePicker_end.updateDate(year, monthOfYear, dayOfMonth);
    }

    private void tryNotifyDateSet() {
        if (mCallBack != null) {
            mDatePicker_start.clearFocus();
            mDatePicker_end.clearFocus();
            Log.e(TAG + "-tryNotifyDateSet", "getYear()为" + mDatePicker_start.getYear() + ";" + mDatePicker_start.getMonth() + ";" + mDatePicker_start.getDayOfMonth());
            mCallBack.onDateSet(mDatePicker_start, mDatePicker_start.getYear(), mDatePicker_start.getMonth(),
                    mDatePicker_start.getDayOfMonth(), mDatePicker_end, mDatePicker_end.getYear(),
                    mDatePicker_end.getMonth(), mDatePicker_end.getDayOfMonth());
        }
    }

    @Override
    protected void onStop() {
        // tryNotifyDateSet();
        super.onStop();
    }

    @Override
    public Bundle onSaveInstanceState() {
        Bundle state = super.onSaveInstanceState();
        state.putInt(START_YEAR, mDatePicker_start.getYear());
        state.putInt(START_MONTH, mDatePicker_start.getMonth());
        state.putInt(START_DAY, mDatePicker_start.getDayOfMonth());
        state.putInt(END_YEAR, mDatePicker_end.getYear());
        state.putInt(END_MONTH, mDatePicker_end.getMonth());
        state.putInt(END_DAY, mDatePicker_end.getDayOfMonth());
        return state;
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        int start_year = savedInstanceState.getInt(START_YEAR);
        int start_month = savedInstanceState.getInt(START_MONTH);
        int start_day = savedInstanceState.getInt(START_DAY);
        mDatePicker_start.init(start_year, start_month, start_day, this);

        int end_year = savedInstanceState.getInt(END_YEAR);
        int end_month = savedInstanceState.getInt(END_MONTH);
        int end_day = savedInstanceState.getInt(END_DAY);
        mDatePicker_end.init(end_year, end_month, end_day, this);

    }
}