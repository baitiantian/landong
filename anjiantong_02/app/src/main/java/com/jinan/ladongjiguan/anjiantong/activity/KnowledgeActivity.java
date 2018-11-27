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


public class KnowledgeActivity extends BaseActivity implements View.OnTouchListener, View.OnClickListener {


    @BindView(R.id.examine_page_back)
    LinearLayout examinePageBack;
    @BindView(R.id.title_layout)
    TextView titleLayout;
    @BindView(R.id.delete_layout)
    LinearLayout deleteLayout;
    @BindView(R.id.bt_knowledge_01)
    Button btKnowledge01;
    @BindView(R.id.bt_knowledge_02)
    Button btKnowledge02;
    @BindView(R.id.bt_knowledge_03)
    LinearLayout btKnowledge03;
    @BindView(R.id.bt_knowledge_04)
    LinearLayout btKnowledge04;
    @BindView(R.id.bt_knowledge_05)
    Button btKnowledge05;
    @BindView(R.id.bt_knowledge_06)
    LinearLayout btKnowledge06;
    @BindView(R.id.bt_knowledge_07)
    Button btKnowledge07;

    @Override
    protected void initView() {
        setContentView(R.layout.knowledge_main_layout);
        ButterKnife.bind(this);
        titleLayout.setText("安全知识库");
    }

    @Override
    protected void init() {

        /**
         * 点击事件
         * */
        examinePageBack.setOnClickListener(this);
        btKnowledge01.setOnClickListener(this);
        btKnowledge02.setOnClickListener(this);
        btKnowledge03.setOnClickListener(this);
        btKnowledge04.setOnClickListener(this);
        btKnowledge05.setOnClickListener(this);
        btKnowledge06.setOnClickListener(this);
        btKnowledge07.setOnClickListener(this);
        /**
         * 触摸事件
         * */
        examinePageBack.setOnTouchListener(this);
    }


    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.examine_page_back://返回键
                onBackPressed();
                break;
            case R.id.bt_knowledge_01:
                intent.setClass(this,SearchActivity.class);
                intent.putExtra("date_state","1");
                startActivity(intent);
                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                break;
            case R.id.bt_knowledge_02:
                intent.setClass(this,SearchActivity.class);
                intent.putExtra("date_state","2");
                startActivity(intent);
                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                break;
            case R.id.bt_knowledge_03:
                intent.setClass(this,AccidentCaseActivity.class);
                intent.putExtra("date_state","3");
                startActivity(intent);
                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                break;
            case R.id.bt_knowledge_04:
                intent.setClass(this,FangHuoActivity.class);
                intent.putExtra("date_state","4");
                startActivity(intent);
                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                break;
            case R.id.bt_knowledge_05:
                intent.setClass(this,BlankPageActivity.class);
                intent.putExtra("date_state","5");
                startActivity(intent);
                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                break;
            case R.id.bt_knowledge_06:
                intent.setClass(this,AccidentDisposalActivity.class);
                intent.putExtra("date_state","6");
                startActivity(intent);
                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                break;
            case R.id.bt_knowledge_07:
                intent.setClass(this,LawItemActivity.class);
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
