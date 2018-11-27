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

public class DataTransmissionFileMainActivity extends BaseActivity implements View.OnClickListener, View.OnTouchListener {
    @BindView(R.id.examine_page_back)
    LinearLayout examinePageBack;
    @BindView(R.id.title_layout)
    TextView titleLayout;
    @BindView(R.id.bt_data_putout)
    LinearLayout btDataPutout;
    @BindView(R.id.bt_date_up)
    LinearLayout btDateUp;

    @Override
    protected void initView() {
        setContentView(R.layout.data_transmission_file_main_layout);
        ButterKnife.bind(this);
        titleLayout.setText("查看本地文书");
        /**
         * 点击事件
         * */
        examinePageBack.setOnClickListener(this);
        btDateUp.setOnClickListener(this);
        btDataPutout.setOnClickListener(this);
        /**
         * 触摸事件
         * */
        examinePageBack.setOnTouchListener(this);
    }

    @Override
    protected void init() {

    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();

        switch (v.getId()) {
            case R.id.examine_page_back://返回键
                onBackPressed();
                break;
            case R.id.bt_date_up://查看本地正式文书
                intent.setClass(this,DataTransmissionFileActivity.class);
                intent.putExtra("state","zs");
                startActivity(intent);
                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                break;
            case R.id.bt_data_putout://查看本地临时文书
                intent.setClass(this,DataTransmissionFileActivity.class);
                intent.putExtra("state","ls");
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


    /**
     * 重写返回键
     * */
    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.zoomin, R.anim.zoomout);

    }
}
