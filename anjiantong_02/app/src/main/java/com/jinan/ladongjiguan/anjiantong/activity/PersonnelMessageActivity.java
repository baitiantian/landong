package com.jinan.ladongjiguan.anjiantong.activity;



import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jinan.ladongjiguan.anjiantong.PublicClass.CustomProgressDialog;
import com.jinan.ladongjiguan.anjiantong.utils.SharedPreferencesUtil;
import com.jinan.ladongjiguan.anjiantong.utils.WebServiceUtils;
import com.jinan.ladongjiguan.anjiantong.R;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.ksoap2.serialization.SoapObject;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;


public class PersonnelMessageActivity extends BaseActivity implements View.OnClickListener, View.OnTouchListener {


    @BindView(R.id.cat_title)
    TextView catTitle;
    @BindView(R.id.user_real_name)
    TextView userRealName;
    @BindView(R.id.img_user_gender)
    ImageView imgUserGender;
    @BindView(R.id.user_gender)
    TextView userGender;
    @BindView(R.id.user_age)
    TextView userAge;
    @BindView(R.id.user_id)
    TextView userId;
    @BindView(R.id.create_user_name)
    TextView createUserName;
    @BindView(R.id.user_telephone)
    TextView userTelephone;
    @BindView(R.id.setting_user_message_exit)
    Button settingUserMessageExit;
    private String WEB_SERVER_URL;
    private CustomProgressDialog progressDialog = null;//加载页
    @Override
    protected void initView() {
        setContentView(R.layout.activity_avatar_toolbar_sample);
        ButterKnife.bind(this);
        //退出按钮
        Button setting_user_message_exit = (Button) findViewById(R.id.setting_user_message_exit);
        setting_user_message_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
            }
        });
    }

    @Override
    protected void init() {
        //加载页添加
        if (progressDialog == null){
            progressDialog = CustomProgressDialog.createDialog(this);
        }
        progressDialog.show();
        WEB_SERVER_URL= SharedPreferencesUtil.getStringData(this,"IPString","");
        //添加参数
        HashMap<String, String> properties = new HashMap<>();
        properties.put("Xml", "<Request><data  code='select-qryUserByuserid'><no><userid>" +
                getIntent().getStringExtra("userid")+"</userid></no></data></Request>");
        properties.put("Token", "");
//        Log.d("发送数据",properties.toString());
        WebServiceUtils.callWebService(WEB_SERVER_URL, "DynamicInvoke", properties, new WebServiceUtils.WebServiceCallBack() {

            //WebService接口返回的数据回调到这个方法中
            @Override
            public void callBack(SoapObject result) {
                if(result != null){
//                    Log.d("返回数据",result.toString());
                    try {
//                                       Log.d("用户名发过去的结果",""+result);
                        Object detail = result.getProperty("DynamicInvokeResult");
                        JSONObject jsonObj;
                        jsonObj = XML.toJSONObject(detail.toString());
                        JSONObject obj;
                        obj =  jsonObj.getJSONObject("DocumentElement").getJSONObject("Table");
//                        Log.d("返回数据",obj.getString("realname"));
                        String s = obj.getString("realname");
                        userRealName.setText(s);
                        catTitle.setText(obj.getString("realname"));
                        userGender.setText(obj.getString("gender"));
                        if(obj.getString("gender").equals("女")){
                            ImageView imageView = (ImageView)findViewById(R.id.img_user_gender);
                            imageView.setImageResource(R.drawable.img_user_gender_woman);
                        }
                        if(obj.has("age")){
                            userAge.setText(obj.getString("age"));
                        }
                        userId.setText(obj.getString("idcard"));
                        userTelephone.setText(obj.getString("telephone"));
                        createUserName.setText(obj.getString("code"));
                        //关闭加载页
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
                        Toast.makeText(PersonnelMessageActivity.this,"提交失败", Toast.LENGTH_SHORT).show();
                        Log.e("JSON exception", e.getMessage());
                        e.printStackTrace();
                    }
                }else {
                    //关闭加载页
                    if (progressDialog != null){
                        progressDialog.dismiss();
                        progressDialog = null;
                    }
                    Toast.makeText(PersonnelMessageActivity.this,"网络未响应，提交失败", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
    }


}
