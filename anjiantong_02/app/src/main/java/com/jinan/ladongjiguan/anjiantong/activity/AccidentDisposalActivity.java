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


public class AccidentDisposalActivity extends BaseActivity implements View.OnClickListener, View.OnTouchListener {
    @BindView(R.id.examine_page_back)
    LinearLayout examinePageBack;
    @BindView(R.id.title_layout)
    TextView titleLayout;
    @BindView(R.id.bt_knowledge_01)
    LinearLayout btKnowledge01;
    @BindView(R.id.bt_knowledge_02)
    LinearLayout btKnowledge02;
    @BindView(R.id.bt_knowledge_03)
    LinearLayout btKnowledge03;
    @BindView(R.id.bt_knowledge_04)
    LinearLayout btKnowledge04;
    @BindView(R.id.bt_knowledge_05)
    LinearLayout btKnowledge05;
    @BindView(R.id.bt_knowledge_06)
    LinearLayout btKnowledge06;
    @Override
    protected void initView() {
        setContentView(R.layout.accident_disposal_layout);
        ButterKnife.bind(this);
        titleLayout.setText("危险事故处置");
    }

    @Override
    protected void init() {
        examinePageBack.setOnClickListener(this);
        examinePageBack.setOnTouchListener(this);
        btKnowledge01.setOnClickListener(this);
        btKnowledge02.setOnClickListener(this);
        btKnowledge03.setOnClickListener(this);
        btKnowledge04.setOnClickListener(this);
        btKnowledge05.setOnClickListener(this);
        btKnowledge06.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        intent.setClass(this, PdfActivity.class);
        switch (v.getId()){
            case R.id.bt_knowledge_01:
                intent.putExtra("state","1");
                startActivity(intent);
                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                break;
            case R.id.bt_knowledge_02:
                intent.putExtra("state","2");
                startActivity(intent);
                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                break;
            case R.id.bt_knowledge_03:
                intent.putExtra("state","3");
                startActivity(intent);
                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                break;
            case R.id.bt_knowledge_04:
                intent.putExtra("state","4");
                startActivity(intent);
                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                break;
            case R.id.bt_knowledge_05:
                intent.putExtra("state","5");
                startActivity(intent);
                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                break;
            case R.id.bt_knowledge_06:
                intent.putExtra("state","6");
                startActivity(intent);
                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                break;
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
            v.setBackgroundColor(ContextCompat.getColor(this, R.color.main_color_3));
        } else if (event.getAction() == MotionEvent.ACTION_CANCEL) {
            v.setBackgroundColor(ContextCompat.getColor(this, R.color.main_color_2));
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            v.setBackgroundColor(ContextCompat.getColor(this, R.color.main_color_2));
        }
        return false;
    }
    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
    }

}
