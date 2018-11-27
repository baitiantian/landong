package com.jinan.landongjiguan.shuangkong.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;


import com.jinan.landongjiguan.shuangkong.PublicClasses.SharedPreferencesUtil;
import com.jinan.landongjiguan.shuangkong.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends BaseActivity implements View.OnClickListener {
    @BindView(R.id.login_tx_userid)
    AutoCompleteTextView mTvUserId;
    @BindView(R.id.login_et_password)
    EditText mEtPassword;
    @BindView(R.id.login_cb_password)
    CheckBox mCbPassword;
    @BindView(R.id.login_bt_login)
    Button mBtlogin;

    private boolean mBoolean = true;//是否勾选“保存密码"
    private String mUser = "admin";
    private String mPassword = "123456";
    private final int REQUEST_READ_PHONE_STATE = 1001;
    private String TAG = LoginActivity.class.getSimpleName();

    @Override
    protected void initView() {
        setContentView(R.layout.activity_login);
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
            mTvUserId.setText(SharedPreferencesUtil.getStringData(this, "user", ""));
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
         * 动态申请权限
         * */
        int permissionPhone = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
        int permissionWrite = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permissionLocation = ContextCompat.checkSelfPermission(this, Manifest.permission_group.LOCATION);
        if (permissionPhone != PackageManager.PERMISSION_GRANTED && permissionWrite != PackageManager.PERMISSION_GRANTED && permissionLocation != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_WIFI_STATE},
                    REQUEST_READ_PHONE_STATE);
        }
    }

    @Override
    protected void init() {
        /*点击事件*/
        mBtlogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.login_bt_login://点击登录
//                intent.putExtra("Account", mTvUserId.getText().toString());
//                setResult(0, intent);
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
        if (mTvUserId.getText().toString().isEmpty()) {
            Toast.makeText(LoginActivity.this, "请输入用户名", Toast.LENGTH_SHORT).show();
        } else if (mEtPassword.getText().toString().isEmpty()) {
            Toast.makeText(LoginActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
        } else {
            Log.e(TAG + "-login", "user为" + mTvUserId.getText().toString() + ";password为" + mEtPassword.getText().toString());
            if (!mTvUserId.getText().toString().equals(mUser)) {
                Toast.makeText(LoginActivity.this, "用户名错误", Toast.LENGTH_SHORT).show();
            } else if (!mEtPassword.getText().toString().equals(mPassword)) {
                Toast.makeText(LoginActivity.this, "密码错误", Toast.LENGTH_SHORT).show();
            } else {
                SharedPreferencesUtil.saveStringData(LoginActivity.this, "user", mTvUserId.getText().toString());
                if (!mBoolean) {
                    SharedPreferencesUtil.saveStringData(this, "userPassword", "");
//                    mBoolean = true;
                } else {
                    SharedPreferencesUtil.saveStringData(LoginActivity.this, "userPassword", mEtPassword.getText().toString());
//                    mBoolean = false;
                }
                SharedPreferencesUtil.saveBooleanData(LoginActivity.this, "Login", true);
                SharedPreferencesUtil.saveBooleanData(LoginActivity.this, "savePS", true);
                Toast.makeText(LoginActivity.this, "登陆成功", Toast.LENGTH_SHORT).show();
                mBoolean = true;
                finish();
            }
        }
    }

    /**
     * 欢迎页面返回
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_READ_PHONE_STATE:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    //TODO
                }
                break;

            default:
                break;
        }
    }
}
