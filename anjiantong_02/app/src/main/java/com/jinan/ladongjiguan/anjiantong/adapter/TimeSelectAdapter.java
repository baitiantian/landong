package com.jinan.ladongjiguan.anjiantong.adapter;

import android.content.Context;
import android.database.DataSetObserver;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.jinan.ladongjiguan.anjiantong.PublicClass.ViewHolder;
import com.jinan.ladongjiguan.anjiantong.R;
import com.jinan.ladongjiguan.anjiantong.view.Bean;

import org.w3c.dom.Text;

import java.util.List;
import java.util.Map;

/**
 * Created by wangfuchun on 2018/11/7.
 */

public class TimeSelectAdapter extends BaseAdapter {


    private String[] mDatas;
    public Context mContext;
    private String TAG = TimeSelectAdapter.class.getSimpleName();


    public TimeSelectAdapter(Context context,String[] datas) {
        this.mDatas = datas;
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return mDatas.length;
    }

    @Override
    public Object getItem(int position) {
        return mDatas[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView textView = new TextView(mContext);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,17);
        textView.setPadding(20,15,20,15);
        textView.setText(mDatas[position]);
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(mContext.getResources().getColor(R.color.main_color_3));
        return textView;
    }

}
