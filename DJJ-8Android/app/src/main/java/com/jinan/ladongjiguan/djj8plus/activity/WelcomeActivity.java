package com.jinan.ladongjiguan.djj8plus.activity;

import android.content.Intent;
import android.view.View;


import com.jinan.ladongjiguan.djj8plus.R;

import java.util.Timer;
import java.util.TimerTask;

public class WelcomeActivity extends BaseActivity {
    @Override
    protected void initView() {
        setContentView(R.layout.activity_splash);
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

    }

    @Override
    protected void init() {
/**
 * 欢迎界面持续时间
 * */
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {

            @Override
            public void run() {
                Intent intent = new Intent();
                setResult(0,intent);
                finish();
                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
            }
        };
        timer.schedule(task, 3000);
    }
}
