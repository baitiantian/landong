package com.lunduimohao.landongjiguang.lunduimohao.activity;




import com.lunduimohao.landongjiguang.lunduimohao.R;

import java.util.Timer;
import java.util.TimerTask;

public class WelcomeActivity extends BaseActivity {
    @Override
    protected void initView() {
        setContentView(R.layout.activity_splash);
        /**
         * 欢迎界面持续时间
         * */
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {

            @Override
            public void run() {
                finish();
            }
        };
        timer.schedule(task, 3000);
    }

    @Override
    protected void init() {

    }
}
