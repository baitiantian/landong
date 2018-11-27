package com.jinan.ladongjiguan.anjiantong.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jinan.ladongjiguan.anjiantong.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CoalSafetyQuiryActivity extends BaseActivity implements View.OnTouchListener, View.OnClickListener {


    @BindView(R.id.examine_page_back)
    LinearLayout examinePageBack;
    @BindView(R.id.title_layout)
    TextView titleLayout;
    @BindView(R.id.webview)
    WebView webview;

    @Override
    protected void initView() {
        setContentView(R.layout.coal_safety_quiry_layout);
        ButterKnife.bind(this);
        titleLayout.setText("煤安查询");
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void init() {
        examinePageBack.setOnClickListener(this);
        examinePageBack.setOnTouchListener(this);

        WebSettings settings = webview.getSettings();
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        //����WebView���ԣ��ܹ�ִ��Javascript�ű�
        settings.setJavaScriptEnabled(true);
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        //������Ҫ��ʾ����ҳ
        webview.loadUrl("http://www.kma.cn/Home/Search/List.aspx?t1=search&t2=1");
        //����Web��ͼ
        webview.setWebViewClient(new HelloWebViewClient ());
    }
    //Web��ͼ
    private class HelloWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }
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
