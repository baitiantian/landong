package com.jinan.ladongjiguan.anjiantong.activity;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.jinan.ladongjiguan.anjiantong.utils.SharedPreferencesUtil;
import com.jinan.ladongjiguan.anjiantong.R;

import butterknife.BindView;
import butterknife.ButterKnife;


public class LoginSettingIPActivity extends BaseActivity implements View.OnClickListener{
    @BindView(R.id.login_date_up)
    Button loginDateUp;
    private EditText setting_ip;

    @Override
    protected void initView() {
        setContentView(R.layout.login_setting_ip_layout);
        ButterKnife.bind(this);
        /**
         * 返回键
         * */
        LinearLayout login_back = (LinearLayout) findViewById(R.id.login_back);
        login_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(0, R.anim.out_to_right);
            }
        });
        /**
         * 设置键
         * */
        Button login_setting = (Button) findViewById(R.id.login_setting);
        boolean bl_login_ip = SharedPreferencesUtil.getBooleanData(this, "LoginIP", false);
        setting_ip = (EditText) findViewById(R.id.setting_ip);
        if (bl_login_ip) {
            String WEB_SERVER_URL = SharedPreferencesUtil.getStringData(this, "IPString", "");
            setting_ip.setText(WEB_SERVER_URL.substring(7, WEB_SERVER_URL.length() - 11));
        }
        login_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (setting_ip.getText().length() > 0) {
                    SharedPreferencesUtil.saveBooleanData(LoginSettingIPActivity.this, "LoginIP", true);
                    SharedPreferencesUtil.saveStringData(LoginSettingIPActivity.this, "IPString",
                            "http://" + setting_ip.getText().toString() + "/Index.asmx");
                    finish();
                    overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                } else {
                    Toast.makeText(LoginSettingIPActivity.this, "请输入合法IP地址", Toast.LENGTH_SHORT).show();
                }
            }
        });
        /**
         * 数据更新
         * */
        loginDateUp.setOnClickListener(this);
    }

    @Override
    protected void init() {

    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(0, R.anim.out_to_right);
    }

    @Override
    public void onClick(View v) {

    }
}
