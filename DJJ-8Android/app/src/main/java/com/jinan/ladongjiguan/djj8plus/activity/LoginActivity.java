package com.jinan.ladongjiguan.djj8plus.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;


import com.jinan.ladongjiguan.djj8plus.R;
import com.jinan.ladongjiguan.djj8plus.publicClass.AssetsDatabaseManager;
import com.jinan.ladongjiguan.djj8plus.publicClass.SharedPreferencesUtil;
import com.jinan.ladongjiguan.djj8plus.publicClass.isConnect;

import java.io.File;
import java.io.RandomAccessFile;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.jinan.ladongjiguan.djj8plus.activity.MainActivity.mMainActivity;

public class LoginActivity extends BaseActivity implements View.OnClickListener {
    @BindView(R.id.login_layout_tv_user)
    AutoCompleteTextView mTvUser;
    @BindView(R.id.login_layout_et_password)
    EditText mEtPassword;
    @BindView(R.id.login_layout_cb_password)
    CheckBox mCbPassword;
    @BindView(R.id.login_layout_cb_ca)
    CheckBox mCbCa;//CA加密
    @BindView(R.id.login_layout_bt_login)
    Button mBtLogin;

    private int mPasswordNum = 0;
    private boolean mBoolean = true;//是否勾选“保存密码"
    private String mUser = "admin";
    private String mPassword = "123456";
    private int REQUEST_READ_PHONE_STATE = 1001;
    private String TAG = LoginActivity.class.getSimpleName();

    @Override
    protected void initView() {
        setContentView(R.layout.login_layout);
        ButterKnife.bind(this);
        /**
         * 欢迎页面
         * */
        Intent intent = new Intent();
        intent.setClass(this, WelcomeActivity.class);
        startActivityForResult(intent, 0);

        /**
         * 检查是否登录过
         * */
        boolean bl_login = SharedPreferencesUtil.getBooleanData(this, "Login", false);
        if (bl_login) {
            mTvUser.setText(SharedPreferencesUtil.getStringData(this, "user", ""));
            mEtPassword.setText(SharedPreferencesUtil.getStringData(this, "userPassword", ""));
        }
        /**
         * 保存密码CheckBox
         * */
        mCbPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mBoolean = isChecked;
            }
        });
        /**
         * 权限申请
         * */
        int permissionPhone = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
        int permissionWrite = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permissionLocation = ContextCompat.checkSelfPermission(this, Manifest.permission_group.LOCATION);
        if (permissionPhone != PackageManager.PERMISSION_GRANTED && permissionWrite != PackageManager.PERMISSION_GRANTED && permissionLocation != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_WIFI_STATE}, REQUEST_READ_PHONE_STATE);
        }
    }

    @Override
    protected void init() {

    }


    @OnClick(R.id.login_layout_bt_login)
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.login_layout_bt_login://点击登录
                login();
                break;
            default:
                break;
        }
    }

    /**
     * 登录
     */
    private void login() {
        if (mTvUser.getText().toString().isEmpty()) {
            Toast.makeText(LoginActivity.this, "请输入用户名", Toast.LENGTH_SHORT).show();
        } else if (mEtPassword.getText().toString().isEmpty()) {
            Toast.makeText(LoginActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
        } else {
            Log.e(TAG + "-login", "user为" + mTvUser.getText().toString() + ";password为" + mEtPassword.getText().toString());
            if (!mTvUser.getText().toString().equals(mUser)) {
                Toast.makeText(LoginActivity.this, "用户名错误", Toast.LENGTH_SHORT).show();
            } else if (!mEtPassword.getText().toString().equals(mPassword)) {
                Toast.makeText(LoginActivity.this, "密码错误", Toast.LENGTH_SHORT).show();
            } else {
                SharedPreferencesUtil.saveStringData(LoginActivity.this, "user", mTvUser.getText().toString());
                if (!mBoolean) {
                    SharedPreferencesUtil.saveStringData(this, "userPassword", "");
                } else {
                    SharedPreferencesUtil.saveStringData(LoginActivity.this, "userPassword", mEtPassword.getText().toString());
                }
                SharedPreferencesUtil.saveBooleanData(LoginActivity.this, "Login", true);
                Toast.makeText(LoginActivity.this, "登陆成功", Toast.LENGTH_SHORT).show();
                mBoolean = true;
                finish();
            }
        }
    }

    /**
     * 密码加密
     */
    public static String string2MD5(String inStr) {
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            System.out.println(e.toString());
            e.printStackTrace();
            return "";
        }
        char[] charArray = inStr.toCharArray();
        byte[] byteArray = new byte[charArray.length];

        for (int i = 0; i < charArray.length; i++)
            byteArray[i] = (byte) charArray[i];
        byte[] md5Bytes = md5.digest(byteArray);
        StringBuffer hexValue = new StringBuffer();
        for (byte md5Byte : md5Bytes) {
            int val = ((int) md5Byte) & 0xff;
            if (val < 16)
                hexValue.append("0");
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString();

    }

    /**
     * 获取两个日期之间的间隔天数
     */
    public static int getGapCount(Date startDate, Date endDate) {
        Calendar fromCalendar = Calendar.getInstance();
        fromCalendar.setTime(startDate);
        fromCalendar.set(Calendar.HOUR_OF_DAY, 0);
        fromCalendar.set(Calendar.MINUTE, 0);
        fromCalendar.set(Calendar.SECOND, 0);
        fromCalendar.set(Calendar.MILLISECOND, 0);

        Calendar toCalendar = Calendar.getInstance();
        toCalendar.setTime(endDate);
        toCalendar.set(Calendar.HOUR_OF_DAY, 0);
        toCalendar.set(Calendar.MINUTE, 0);
        toCalendar.set(Calendar.SECOND, 0);
        toCalendar.set(Calendar.MILLISECOND, 0);

        return (int) ((toCalendar.getTime().getTime() - fromCalendar.getTime().getTime()) / (1000 * 60 * 60 * 24));
    }

    /**
     * 欢迎页面返回
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
