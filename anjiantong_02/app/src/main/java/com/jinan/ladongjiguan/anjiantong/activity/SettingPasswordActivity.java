package com.jinan.ladongjiguan.anjiantong.activity;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.jinan.ladongjiguan.anjiantong.PublicClass.AssetsDatabaseManager;
import com.jinan.ladongjiguan.anjiantong.PublicClass.CustomProgressDialog;
import com.jinan.ladongjiguan.anjiantong.utils.SharedPreferencesUtil;
import com.jinan.ladongjiguan.anjiantong.utils.WebServiceUtils;
import com.jinan.ladongjiguan.anjiantong.R;
import com.jinan.ladongjiguan.anjiantong.utils.CommonUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.ksoap2.serialization.SoapObject;

import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by baitiantian on 2017/4/24.
 */

public class SettingPasswordActivity extends BaseActivity implements View.OnClickListener, View.OnTouchListener {


    @BindView(R.id.examine_page_back)
    LinearLayout examinePageBack;
    @BindView(R.id.title_layout)
    TextView titleLayout;
    @BindView(R.id.delete_layout)
    LinearLayout deleteLayout;
    @BindView(R.id.et_password_01)
    EditText etPassword01;
    @BindView(R.id.et_password_02)
    EditText etPassword02;
    @BindView(R.id.tv_password_01)
    TextView tvPassword01;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.bt_password_01)
    Button btPassword01;
    private int GRADE_SCORE = 0;

    private String WEB_SERVER_URL;
    private CustomProgressDialog progressDialog = null;//加载页
    private String Id = UUID.randomUUID().toString();//日志主键
    @Override
    protected void initView() {
        setContentView(R.layout.setting_password_layout);
        ButterKnife.bind(this);
        titleLayout.setText("修改密码");
    }

    @Override
    protected void init() {
        /**
         * 点击事件
         * */
        examinePageBack.setOnClickListener(this);
        btPassword01.setOnClickListener(this);

        /**
         * 触摸事件
         * */
        examinePageBack.setOnTouchListener(this);
        /**
         * 监听键盘输入事件
         * */
        etPassword02.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String password = etPassword02.getText().toString();
                tvPassword01.setText(passwordStrong(password));
                setProgressBarColour(GRADE_SCORE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.examine_page_back://返回键
                onBackPressed();
                break;
            case R.id.bt_password_01:
                if(etPassword01.getText().length()>0&&etPassword02.getText().length()>0){
                    if(CommonUtils.isNetworkConnected(this)){
                        passWord();
                    }else {
                        new AlertDialog.Builder(this)
                                .setTitle("网络未连接")
                                .setMessage("网络连接失败，请检查网络是否打开")
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        finish();
                                    }
                                }).show();
                    }

                }else {
                    Toast.makeText(this, "请完善信息", Toast.LENGTH_SHORT).show();
                }
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

    /**
     * 判断密码强度
     *
     * @return Z = 字母 S = 数字 T = 特殊字符
     */
    private String passwordStrong(String passwordStr) {
        if (TextUtils.equals("", passwordStr)) {
            return "请输入新密码";
        }
        String regexZ = "\\d*";
        String regexS = "[a-zA-Z]+";
        String regexT = "\\W+$";
        String regexZT = "\\D*";
        String regexST = "[\\d\\W]*";
        String regexZS = "\\w*";
        String regexZST = "[\\w\\W]*";

        if (passwordStr.matches(regexZ)) {
            GRADE_SCORE = 20;
            return "弱";
        }
        if (passwordStr.matches(regexS)) {
            GRADE_SCORE = 20;
            return "弱";
        }
        if (passwordStr.matches(regexT)) {
            GRADE_SCORE = 20;
            return "弱";
        }
        if (passwordStr.matches(regexZT)) {
            GRADE_SCORE = 60;
            return "中";
        }
        if (passwordStr.matches(regexST)) {
            GRADE_SCORE = 60;
            return "中";
        }
        if (passwordStr.matches(regexZS)) {
            GRADE_SCORE = 60;
            return "中";
        }
        if (passwordStr.matches(regexZST)) {
            GRADE_SCORE = 90;
            return "强";
        }
        return passwordStr;
    }

    /**
     * 设置progressBar值
     *
     * @param score
     */
    private void setProgressBarColour(int score) {

        int color = 0;
        if (score < 30) {
            color = getResources().getColor(R.color.bg_red);
        } else if (score < 70) {
            color = getResources().getColor(R.color.bg_orangef);
        } else {
            color = getResources().getColor(R.color.bg_green11);
        }
        ClipDrawable d = new ClipDrawable(new ColorDrawable(color), Gravity.
                LEFT, ClipDrawable.HORIZONTAL);

        progressBar.setProgressDrawable(d);
        progressBar.setProgress(0);
        progressBar.setProgress(score);
    }

    /**
     * 密码加密
     * */
    public static String string2MD5(String inStr){
        MessageDigest md5;
        try{
            md5 = MessageDigest.getInstance("MD5");
        }catch (Exception e){
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
        for (int i = 0; i < md5Bytes.length; i++){
            int val = ((int) md5Bytes[i]) & 0xff;
            if (val < 16)
                hexValue.append("0");
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString();

    }

    /**
     * 修改密码
     * */
    protected void passWord(){
        //加载页添加
        if (progressDialog == null){
            progressDialog = CustomProgressDialog.createDialog(this);
        }
        progressDialog.show();
        WEB_SERVER_URL= SharedPreferencesUtil.getStringData(this,"IPString","");
        String Code =  SharedPreferencesUtil.getStringData(this,"Code","");
        String Password = string2MD5(etPassword01.getText().toString());
        String NewPwd = string2MD5(etPassword02.getText().toString());
        //添加参数
        HashMap<String, String> properties = new HashMap<>();
        properties.put("Xml", "<Request><data  code='update-Pwd'><no><Code>" +
                Code+"</Code><Password>" +
                Password+"</Password><NewPwd>" +
                NewPwd+"</NewPwd></no></data></Request>");
        properties.put("Token", "");
//        Log.d("发送数据",properties.toString());
        WebServiceUtils.callWebService(WEB_SERVER_URL, "DynamicInvoke", properties, new WebServiceUtils.WebServiceCallBack() {

            //WebService接口返回的数据回调到这个方法中
            @Override
            public void callBack(SoapObject result) {
                if(result != null){
//                    Log.d("返回数据",result.toString());
                    try {
                        Object detail = result.getProperty("DynamicInvokeResult");
                        JSONObject jsonObj;
                        jsonObj = XML.toJSONObject(detail.toString());
                        JSONObject obj;
                        obj = jsonObj.getJSONObject("Response");
//                        Log.e("处理后的数据",obj.getString("result"));
                        if(obj.getString("result").equals("true")){
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
                            WifiManager wifiMgr = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                            WifiInfo info = (null == wifiMgr ? null : wifiMgr.getConnectionInfo());
                            if (null != info) {
                                macAddress = info.getMacAddress();
                            }
                            SharedPreferencesUtil.saveBooleanData(SettingPasswordActivity.this, "Login", false);
                            ContentValues values  = new ContentValues();
                            values.put("DateId",Id);
                            values.put("UserId",SharedPreferencesUtil.getStringData(SettingPasswordActivity.this,"userId",null));
                            values.put("Mac",macAddress);
                            values.put("LoginTime",SharedPreferencesUtil.getStringData(SettingPasswordActivity.this,"LoginTime",null));
                            values.put("ExitTime",OutTime);
                            db.replace("ELL_Date",null,values);
                            Intent intent = new Intent();
                            intent.setClass(SettingPasswordActivity.this, LoginActivity.class);
                            startActivity(intent);
                            SettingActivity.activity.finish();
                            onBackPressed();

                            Toast.makeText(SettingPasswordActivity.this,"密码修改成功，请重新登录", Toast.LENGTH_SHORT).show();

                        }
                        if (progressDialog != null){
                            progressDialog.dismiss();
                            progressDialog = null;
                        }
                    } catch (JSONException e) {
                        //关闭加载页
                        if (progressDialog != null){
                            progressDialog.dismiss();
                            progressDialog = null;
                        }
                        Toast.makeText(SettingPasswordActivity.this,"提交失败", Toast.LENGTH_SHORT).show();
                        Log.e("JSON exception", e.getMessage());
                        e.printStackTrace();
                    }
                }else {
                    //关闭加载页
                    if (progressDialog != null){
                        progressDialog.dismiss();
                        progressDialog = null;
                    }
                    Toast.makeText(SettingPasswordActivity.this,"网络未响应，提交失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
