package com.jinan.landongjiguan.shuangkong.Activities;

import android.content.Intent;
import android.view.View;


import com.jinan.landongjiguan.shuangkong.PublicClasses.MonIndicator;
import com.jinan.landongjiguan.shuangkong.R;

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
        MonIndicator monIndicator = this.findViewById(R.id.monIndicator);
        monIndicator.setColors(new int[]{0xFF6f78ca, 0xFF6f78ca, 0xFF6f78ca, 0xFF6f78ca, 0xFF6f78ca});

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
