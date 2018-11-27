package com.jinan.ladongjiguan.anjiantong.activity;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jinan.ladongjiguan.anjiantong.R;
import com.lidong.pdf.PDFView;
import com.lidong.pdf.listener.OnPageChangeListener;

import butterknife.BindView;
import butterknife.ButterKnife;


public class HelpActivity extends AppCompatActivity implements OnPageChangeListener, View.OnTouchListener, View.OnClickListener {
    @BindView(R.id.examine_page_back)
    LinearLayout examinePageBack;
    @BindView(R.id.title_layout)
    TextView titleLayout;
    @BindView(R.id.pdfView)
    PDFView pdfView;
    @BindView(R.id.text)
    TextView text;
    @BindView(R.id.del_text)
    TextView delText;
    @BindView(R.id.delete_layout)
    LinearLayout deleteLayout;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.examine_page_back://返回键
                finish();
                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                break;

            default:
                break;
        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_layout);
        ButterKnife.bind(this);
        switch (getIntent().getStringExtra("state")){
            case "0":
                pdfView.fromAsset("helper.pdf").defaultPage(0).onPageChange(this).load();
                titleLayout.setText("用户使用手册");
                examinePageBack.setOnClickListener(this);
                break;
            case "1":
                pdfView.fromAsset("mkaqgc.pdf").defaultPage(0).onPageChange(this).load();
                titleLayout.setText("煤矿安全规程");
                examinePageBack.setOnClickListener(this);
                break;
            default:
                break;

        }



    }

    @Override
    public void onPageChanged(int page, int pageCount) {
        text.setText(page + "/" + pageCount);
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

}
