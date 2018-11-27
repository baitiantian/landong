package com.jinan.ladongjiguan.anjiantong.activity;


import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jinan.ladongjiguan.anjiantong.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CountMainActivity extends BaseActivity implements View.OnTouchListener, View.OnClickListener {
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
    @BindView(R.id.bt_count_04)
    LinearLayout btCount04;
    public static final String  FTPID = "http://218.201.222.159:4005/Ftp/ftpdata/";
//    public static final String  FTPID = "http://218.201.222.159:802/Ftp/ftpdata/";//20180925前端口
//    public static final String  FTPID = "http://192.130.1.22:802/Ftp/ftpdata/";//内网
    @Override
    protected void initView() {
        setContentView(R.layout.count_layout);
        ButterKnife.bind(this);
        switch (getIntent().getStringExtra("state")){
            case "1":
                titleLayout.setText("隐患统计查询");
                break;
            case "2":
                titleLayout.setText("计划统计查询");
                break;
            default:
                break;
        }

    }

    @Override
    protected void init() {
        examinePageBack.setOnClickListener(this);
        examinePageBack.setOnTouchListener(this);
        btCount01.setOnClickListener(this);
        btCount02.setOnClickListener(this);
        btCount03.setOnClickListener(this);
        btCount04.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        intent.putExtra("state",getIntent().getStringExtra("state"));
        switch (v.getId()) {
            case R.id.examine_page_back://返回键
                onBackPressed();
                break;
            case R.id.bt_count_01:
                intent.setClass(this, BlankPageActivity.class);
                intent.putExtra("date_state", "1");
                startActivity(intent);
                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                break;
            case R.id.bt_count_02:
                intent.setClass(this, BlankPageActivity.class);
                intent.putExtra("date_state", "2");
                startActivity(intent);
                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                break;
            case R.id.bt_count_03:
                intent.setClass(this, BlankPageActivity.class);
                intent.putExtra("date_state", "3");
                startActivity(intent);
                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                break;
            case R.id.bt_count_04:
                intent.setClass(this, CountEnterpriseActivity.class);
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

}
