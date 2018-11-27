package com.jinan.ladongjiguan.anjiantong.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jinan.ladongjiguan.anjiantong.R;

import butterknife.BindView;
import butterknife.ButterKnife;


public class LawActivity extends BaseActivity implements View.OnClickListener, View.OnTouchListener {


    @BindView(R.id.examine_page_back)
    LinearLayout examinePageBack;
    @BindView(R.id.title_layout)
    TextView titleLayout;
    @BindView(R.id.bt_date_up)
    LinearLayout btDateUp;
    @BindView(R.id.bt_date_down)
    LinearLayout btDateDown;

    @Override
    protected void initView() {
        setContentView(R.layout.law_layout);
        ButterKnife.bind(this);
        titleLayout.setText("法律法规");
    }

    @Override
    protected void init() {
        btDateUp.setOnClickListener(this);
        btDateDown.setOnClickListener(this);
        examinePageBack.setOnClickListener(this);
        examinePageBack.setOnTouchListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()){
            case R.id.examine_page_back://返回键
                onBackPressed();
                break;
            case R.id.bt_date_up://法规查询
//                intent.setClass(this,SearchActivity.class);
                intent.setClass(this,SearchRuleActivity.class);
                intent.putExtra("date_state","1");
                startActivity(intent);
                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                break;
            case R.id.bt_date_down://法规反查
//                intent.setClass(this,LawItemActivity.class);
                intent.setClass(this,CheckRuleActivity.class);
                intent.putExtra("date_state","7");
                startActivity(intent);
                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
    }
}
