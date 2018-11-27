package com.jinan.ladongjiguan.anjiantong.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jinan.ladongjiguan.anjiantong.R;

import butterknife.BindView;
import butterknife.ButterKnife;


public class CountActivity extends BaseActivity implements View.OnTouchListener, View.OnClickListener {

    @BindView(R.id.examine_page_back)
    LinearLayout examinePageBack;
    @BindView(R.id.title_layout)
    TextView titleLayout;
    @BindView(R.id.bt_count_01)
    LinearLayout btCount01;
    @BindView(R.id.bt_count_02)
    LinearLayout btCount02;
    @BindView(R.id.bt_count_03)
    LinearLayout btCount03;

    @Override
    protected void initView() {
        setContentView(R.layout.count_main_layout);
        ButterKnife.bind(this);
        titleLayout.setText("统计查询");
    }

    @Override
    protected void init() {
        examinePageBack.setOnClickListener(this);
        examinePageBack.setOnTouchListener(this);
        btCount01.setOnClickListener(this);
        btCount02.setOnClickListener(this);
        btCount03.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.examine_page_back://返回键
                onBackPressed();
                break;
            case R.id.bt_count_01://隐患统计查询
                intent.setClass(this, CountMainActivity.class);
                intent.putExtra("state", "1");
                startActivity(intent);
                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                break;
            case R.id.bt_count_02://计划统计查询
                intent.setClass(this, CountMainActivity.class);
                intent.putExtra("state", "2");
                startActivity(intent);
                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                break;
            case R.id.bt_count_03://计划进度查询
                intent.setClass(this, ScheduleChartActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}
