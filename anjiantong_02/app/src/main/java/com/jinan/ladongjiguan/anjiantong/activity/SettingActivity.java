package com.jinan.ladongjiguan.anjiantong.activity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v4.content.ContextCompat;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jinan.ladongjiguan.anjiantong.PublicClass.AssetsDatabaseManager;
import com.jinan.ladongjiguan.anjiantong.utils.SharedPreferencesUtil;
import com.jinan.ladongjiguan.anjiantong.PublicClass.UpdateManager;
import com.jinan.ladongjiguan.anjiantong.R;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.jinan.ladongjiguan.anjiantong.activity.MainActivity.mainActivity;


public class SettingActivity extends BaseActivity implements View.OnClickListener, View.OnTouchListener {


    @BindView(R.id.examine_page_back)
    LinearLayout examinePageBack;
    @BindView(R.id.title_layout)
    TextView titleLayout;
    @BindView(R.id.user_name)
    TextView userName;
    @BindView(R.id.setting_user)
    LinearLayout settingUser;
    @BindView(R.id.setting_historic_records)
    LinearLayout settingHistoricRecords;
    @BindView(R.id.up_date)
    LinearLayout upDate;
    @BindView(R.id.setting_edition_number)
    TextView settingEditionNumber;
    @BindView(R.id.setting_exit_login)
    Button settingExitLogin;
    @BindView(R.id.delete_layout)
    LinearLayout deleteLayout;
    @BindView(R.id.change_user_password)
    LinearLayout changeUserPassword;
    @BindView(R.id.show_date)
    LinearLayout showDate;
    @BindView(R.id.help)
    LinearLayout help;
    @BindView(R.id.coal_safety_quiry)
    LinearLayout coalSafetyQuiry;
    private String url;
    public static SettingActivity activity;
    private String Id = MainActivity.Id;//日志主键
    private String path = "assets/helper.pdf";

    @Override
    protected void initView() {
        setContentView(R.layout.setting_layout);
        ButterKnife.bind(this);
        activity = this;
        /***
         * 显示标题
         * */
        titleLayout.setText("系统设置");
        /**
         * 显示版本号
         * */
        String s = "V" + getVersion();
        settingEditionNumber.setText(s);
        /**
         * 显示用户名
         * */
        userName.setText(getIntent().getStringExtra("userName"));
        url = SharedPreferencesUtil.getStringData(this, "IPString", "");

    }

    @Override
    protected void init() {
        /**
         * 点击事件
         * */
        examinePageBack.setOnClickListener(this);
        settingUser.setOnClickListener(this);
        settingHistoricRecords.setOnClickListener(this);
        settingExitLogin.setOnClickListener(this);
        changeUserPassword.setOnClickListener(this);
        upDate.setOnClickListener(this);
        showDate.setOnClickListener(this);
        coalSafetyQuiry.setOnClickListener(this);
        help.setOnTouchListener(this);
        /**
         * 触摸事件
         * */
        examinePageBack.setOnTouchListener(this);
        settingUser.setOnTouchListener(this);
        settingHistoricRecords.setOnTouchListener(this);
        changeUserPassword.setOnTouchListener(this);
        upDate.setOnTouchListener(this);
        showDate.setOnTouchListener(this);
        help.setOnClickListener(this);
        coalSafetyQuiry.setOnTouchListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.examine_page_back://返回键
                onBackPressed();
                break;
            case R.id.setting_exit_login://退出登录
                // 初始化，只需要调用一次
                AssetsDatabaseManager.initManager(getApplication());
                // 获取管理对象，因为数据库需要通过管理对象才能够获取
                AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
                // 通过管理对象获取数据库
                SQLiteDatabase db = mg.getDatabase("users.db");
                Date curDate = new Date(System.currentTimeMillis());//获取当前时间
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
                String OutTime = sdf.format(curDate);

                String macAddress = null;
                WifiManager wifiMgr = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                WifiInfo info = (null == wifiMgr ? null : wifiMgr.getConnectionInfo());
                if (null != info) {
                    macAddress = info.getMacAddress();
                }
//                intent.setClass(this, MainActivity.class);

                ContentValues values = new ContentValues();
                values.put("DateId", Id);
                values.put("UserId", SharedPreferencesUtil.getStringData(this, "userId", null));
                values.put("Mac", macAddress);
                values.put("LoginTime", SharedPreferencesUtil.getStringData(this, "LoginTime", null));
                values.put("ExitTime", OutTime);
                db.replace("ELL_Date", null, values);
                SharedPreferencesUtil.saveStringData(this, "Account","");
                SharedPreferencesUtil.saveStringData(this, "userPassword","");
                SharedPreferencesUtil.saveStringData(this,"Code","");
                mainActivity.finish();
//                startActivity(intent);
//                finish();
//                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                break;
            case R.id.change_user_password://修改密码
                intent.setClass(this, SettingPasswordActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                break;
            case R.id.setting_user:
                intent.setClass(this, SettingUserMessageActivity.class);
                intent.putExtra("date_state", "13");
                startActivity(intent);
                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                break;
            case R.id.up_date://升级版本
                Toast.makeText(this, "开始检查更新", Toast.LENGTH_LONG).show();
                UpdateManager manager = new UpdateManager(this, url);
                try {
                    manager.checkUpdate();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.show_date://本机使用记录
                intent.setClass(this, ShowDateActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                break;
            case R.id.help:
                intent.setClass(this, HelpActivity.class);
                intent.putExtra("state","0");
                startActivity(intent);
                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                break;
            case R.id.coal_safety_quiry:
                intent.setClass(this,CoalSafetyQuiryActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
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
     * 获取版本号
     *
     * @return 当前应用的版本号
     */
    public String getVersion() {
        try {
            PackageManager manager = getPackageManager();
            PackageInfo info = manager.getPackageInfo(getPackageName(), 0);
            return info.versionName;
        } catch (Exception e) {
            e.printStackTrace();
            return "null";
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
    }

}
