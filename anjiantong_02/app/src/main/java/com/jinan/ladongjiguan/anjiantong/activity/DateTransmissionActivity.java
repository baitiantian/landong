package com.jinan.ladongjiguan.anjiantong.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jinan.ladongjiguan.anjiantong.PublicClass.AssetsDatabaseManager;
import com.jinan.ladongjiguan.anjiantong.PublicClass.CustomProgressDialog;
import com.jinan.ladongjiguan.anjiantong.utils.SharedPreferencesUtil;
import com.jinan.ladongjiguan.anjiantong.utils.WebServiceUtils;
import com.jinan.ladongjiguan.anjiantong.R;
import com.jinan.ladongjiguan.anjiantong.utils.CommonUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.ksoap2.serialization.SoapObject;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;


public class DateTransmissionActivity extends BaseActivity implements View.OnTouchListener, View.OnClickListener {

    @BindView(R.id.examine_page_back)
    LinearLayout examinePageBack;
    @BindView(R.id.title_layout)
    TextView titleLayout;
    @BindView(R.id.bt_date_up)
    LinearLayout btDateUp;
    @BindView(R.id.bt_date_down)
    LinearLayout btDateDown;
    @BindView(R.id.bt_data_putout)
    LinearLayout btDataPutout;
    private String WEB_SERVER_URL;
    private CustomProgressDialog progressDialog = null;//加载页
    private String TAG = DateTransmissionActivity.class.getSimpleName();

    @Override
    protected void initView() {
        setContentView(R.layout.date_transmission_layout);
        ButterKnife.bind(this);
        /***
         * 显示标题
         * */
        titleLayout.setText("数据传输");
        WEB_SERVER_URL = SharedPreferencesUtil.getStringData(this, "IPString", "");
    }

    @Override
    protected void init() {
        /**
         * 点击事件
         * */
        examinePageBack.setOnClickListener(this);
        btDateUp.setOnClickListener(this);
        btDateDown.setOnClickListener(this);
        btDataPutout.setOnClickListener(this);
        /**
         * 触摸事件
         * */
        examinePageBack.setOnTouchListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.examine_page_back://返回键
                onBackPressed();
                break;
            case R.id.bt_date_up://上传数据
                if (CommonUtils.isNetworkConnected(this)) {
                    businessUpDate();
                } else {
                    Toast.makeText(this, "请确定网络是否连接", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.bt_date_down://下载数据
                if (CommonUtils.isNetworkConnected(this)) {
                    //加载页添加
                    if (progressDialog == null) {
                        progressDialog = CustomProgressDialog.createDialog(this);
                    }
                    progressDialog.show();
                    upDateBusiness();
                } else {
                    Toast.makeText(this, "请确定网络是否连接", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.bt_data_putout://查看本地文书
                intent.setClass(this,DataTransmissionFileMainActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                break;
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
     * 更新企业信息
     */
    protected void upDateBusiness() {
        // 初始化，只需要调用一次
        AssetsDatabaseManager.initManager(getApplication());
        // 获取管理对象，因为数据库需要通过管理对象才能够获取
        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
        // 通过管理对象获取数据库
        final SQLiteDatabase db = mg.getDatabase("users.db");
        Cursor c = db.rawQuery("SELECT* FROM ELL_Business", null);
        try {
            db.execSQL("ALTER TABLE ELL_Business ADD COLUMN BusinessType1");//添加企业类型1
        }catch (Exception e){
            Log.e("BusinessType1数据库列表已存在", e.toString(),e);
        }
        int ModifyIndex = 0;
        while (c.moveToNext()) {
//                Log.e("更新标数据",ModifyIndex+"");
            try {
                if (ModifyIndex < Integer.parseInt(c.getString(c.getColumnIndex("ModifyIndex")))) {
                    ModifyIndex = Integer.parseInt(c.getString(c.getColumnIndex("ModifyIndex")));
                }
            } catch (Exception e) {
                Log.e("获取更新标数据库报错", e.toString());
            }


        }
        progressDialog.setTitile("下载企业信息");
        c.close();
        //添加参数
        HashMap<String, String> properties = new HashMap<>();
        properties.put("Xml", "<Request><data  code='update-Business-all'><no></no></data></Request>");
        properties.put("Token", "");
        WebServiceUtils.callWebService(WEB_SERVER_URL, "DynamicInvoke", properties, new WebServiceUtils.WebServiceCallBack() {

            //WebService接口返回的数据回调到这个方法中
            @Override
            public void callBack(SoapObject result) {
                if (result != null) {
//                    Log.e("登录返回数据",result.toString());
                    try {
                        Object detail = result.getProperty("DynamicInvokeResult");
                        JSONObject jsonObj;
                        jsonObj = XML.toJSONObject(detail.toString());
                        JSONArray array;
                        JSONObject obj;
//                      Log.e("秘钥",jsonObj.getJSONObject("DocumentElement").getJSONObject("Table").getString("secretkey"));
                        if (jsonObj.has("DocumentElement") && jsonObj.toString().substring(jsonObj.toString().length() - 3, jsonObj.toString().length() - 2).equals("}")) {
                            obj = jsonObj.getJSONObject("DocumentElement").getJSONObject("Table");
                            ContentValues values = new ContentValues();
                            if(obj.has("businessid")) {
                                values.put("BusinessId", obj.getString("businessid"));
                            }else {
                                values.put("BusinessId", "");
                            }
                            if(obj.has("businessname")) {
                                values.put("BusinessName", obj.getString("businessname"));
                            }else {
                                values.put("BusinessName", "");
                            }
                            if(obj.has("businesstype")) {
                                values.put("BusinessType", obj.getString("businesstype"));
                            }else {
                                values.put("BusinessType", "");
                            }
                            if (obj.has("businesstype1")) {
                                values.put("BusinessType1", obj.getString("businesstype1"));
                            } else {
                                values.put("BusinessType1", "");
                            }
                            if(obj.has("legalperson")) {
                                values.put("LegalPerson", obj.getString("legalperson"));
                            }else {
                                values.put("LegalPerson", "");
                            }
                            if(obj.has("legalpersonpost")) {
                                values.put("LegalPersonPost", obj.getString("legalpersonpost"));
                            }else {
                                values.put("LegalPersonPost","");
                            }
                            if(obj.has("legalpersonphone")) {
                                values.put("LegalPersonPhone", obj.getString("legalpersonphone"));
                            }else {
                                values.put("LegalPersonPhone","");
                            }
//                            values.put("RegistrationNumber",obj.getString("registrationnumber"));
                            if(obj.has("safetyofficer")) {
                                values.put("SafetyOfficer", obj.getString("safetyofficer"));
                            }else {
                                values.put("SafetyOfficer","");
                            }
                            if(obj.has("safetyofficerphone")) {
                                values.put("SafetyOfficerPhone", obj.getString("safetyofficerphone"));
                            }else {
                                values.put("SafetyOfficerPhone","");
                            }
                            if(obj.has("address")) {
                                values.put("Address", obj.getString("address"));
                            }else {
                                values.put("Address","");
                            }
                            values.put("Emphases", "1");
                            if(obj.has("validflag")) {
                                values.put("ValidFlag", obj.getString("validflag"));
                            }else {
                                values.put("ValidFlag","");
                            }
                            if(obj.has("orgcode")) {
                                values.put("OrgCode", obj.getString("orgcode"));
                            }else {
                                values.put("OrgCode","");
                            }
                            db.replace("ELL_Business", null, values);
                        } else if (jsonObj.has("DocumentElement") && jsonObj.toString().substring(jsonObj.toString().length() - 3, jsonObj.toString().length() - 2).equals("]")) {
                            array = jsonObj.getJSONObject("DocumentElement").getJSONArray("Table");
                            for (int i = 0; i < array.length(); i++) {
                                obj = array.getJSONObject(i);
                                ContentValues values = new ContentValues();
                                if(obj.has("businessid")) {
                                    values.put("BusinessId", obj.getString("businessid"));
                                }else {
                                    values.put("BusinessId", "");
                                }
                                if(obj.has("businessname")) {
                                    values.put("BusinessName", obj.getString("businessname"));
                                }else {
                                    values.put("BusinessName", "");
                                }
                                if(obj.has("businesstype")) {
                                    values.put("BusinessType", obj.getString("businesstype"));
                                }else {
                                    values.put("BusinessType", "");
                                }
                                if (obj.has("businesstype1")) {
                                    values.put("BusinessType1", obj.getString("businesstype1"));
                                } else {
                                    values.put("BusinessType1", "");
                                }
                                if(obj.has("legalperson")) {
                                    values.put("LegalPerson", obj.getString("legalperson"));
                                }else {
                                    values.put("LegalPerson", "");
                                }
                                if(obj.has("legalpersonpost")) {
                                    values.put("LegalPersonPost", obj.getString("legalpersonpost"));
                                }else {
                                    values.put("LegalPersonPost","");
                                }
                                if(obj.has("legalpersonphone")) {
                                    values.put("LegalPersonPhone", obj.getString("legalpersonphone"));
                                }else {
                                    values.put("LegalPersonPhone","");
                                }
                                if(obj.has("safetyofficer")) {
                                    values.put("SafetyOfficer", obj.getString("safetyofficer"));
                                }else {
                                    values.put("SafetyOfficer","");
                                }
                                if(obj.has("safetyofficerphone")) {
                                    values.put("SafetyOfficerPhone", obj.getString("safetyofficerphone"));
                                }else {
                                    values.put("SafetyOfficerPhone","");
                                }
                                if(obj.has("address")) {
                                    values.put("Address", obj.getString("address"));
                                }else {
                                    values.put("Address","");
                                }
                                values.put("Emphases", "1");
                                if(obj.has("validflag")) {
                                    values.put("ValidFlag", obj.getString("validflag"));
                                }else {
                                    values.put("ValidFlag","");
                                }
                                if(obj.has("orgcode")) {
                                    values.put("OrgCode", obj.getString("orgcode"));
                                }else {
                                    values.put("OrgCode","");
                                }
                                db.replace("ELL_Business", null, values);
                            }


                        }
                        upDataGroupBusiness();
                    } catch (JSONException e) {
                        //关闭加载页
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                            progressDialog = null;
                        }
                        Toast.makeText(DateTransmissionActivity.this, "提交失败", Toast.LENGTH_SHORT).show();
                        Log.e(TAG+"报错-upDateBusiness", e.getMessage());
                        e.printStackTrace();
                    }

                } else {
                    //关闭加载页
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                        progressDialog = null;
                    }
                    Toast.makeText(DateTransmissionActivity.this, "网络未响应，提交失败", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }
    /**
     * 更新集团信息
     */
    protected void upDataGroupBusiness() {
        progressDialog.setTitile("获取各类集团信息");
        // 初始化，只需要调用一次
        AssetsDatabaseManager.initManager(getApplication());
        // 获取管理对象，因为数据库需要通过管理对象才能够获取
        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
        // 通过管理对象获取数据库
        final SQLiteDatabase db = mg.getDatabase("users.db");
        Cursor c = db.rawQuery("SELECT* FROM ELL_Group", null);
        try {
            db.execSQL("ALTER TABLE ELL_Group ADD COLUMN BusinessType1");//添加企业类型1
        }catch (Exception e){
            Log.e("BusinessType1数据库列表已存在", e.toString(),e);
        }
        int ModifyIndex = 0;
        while (c.moveToNext()) {
//                Log.d("更新标数据",ModifyIndex+"");
            try {
                if (ModifyIndex < Integer.parseInt(c.getString(c.getColumnIndex("ModifyIndex")))) {
                    ModifyIndex = Integer.parseInt(c.getString(c.getColumnIndex("ModifyIndex")));
                }
            } catch (Exception e) {
                Log.e("获取更新标数据库报错", e.toString());
                ModifyIndex = 0;
            }


        }

        c.close();
        //添加参数
        HashMap<String, String> properties = new HashMap<>();
        properties.put("Xml", "<Request><data  code='update-Business-GroupBusiness'><no><ModifyIndex>0</ModifyIndex></no></data></Request>");
        properties.put("Token", "");
        Log.d("集团发送数据",properties.toString());
        WebServiceUtils.callWebService(WEB_SERVER_URL, "DynamicInvoke", properties, new WebServiceUtils.WebServiceCallBack() {

            //WebService接口返回的数据回调到这个方法中
            @Override
            public void callBack(SoapObject result) {
                if (result != null) {
                    Log.d("集团返回数据",result.toString());
                    try {

                        Object detail = result.getProperty("DynamicInvokeResult");
                        JSONObject jsonObj;
                        jsonObj = XML.toJSONObject(detail.toString());
                        JSONArray array;
                        JSONObject obj;

//                      Log.d("秘钥",jsonObj.getJSONObject("DocumentElement").getJSONObject("Table").getString("secretkey"));
                        if (jsonObj.has("DocumentElement") && jsonObj.toString().substring(jsonObj.toString().length() - 3, jsonObj.toString().length() - 2).equals("}")) {
                            obj = jsonObj.getJSONObject("DocumentElement").getJSONObject("Table");

                            ContentValues values = new ContentValues();
                            if (obj.has("businessid")) {
                                values.put("BusinessId", obj.getString("businessid"));
                            } else {
                                values.put("BusinessId", "");
                            }
                            if (obj.has("businessname")) {
                                values.put("BusinessName", obj.getString("businessname"));
                            } else {
                                values.put("BusinessName", "");
                            }
                            if (obj.has("businesstype")) {
                                values.put("BusinessType", obj.getString("businesstype"));
                            } else {
                                values.put("BusinessType", "");
                            }
                            if (obj.has("businesstype1")) {
                                values.put("BusinessType1", obj.getString("businesstype1"));
                            } else {
                                values.put("BusinessType1", "");
                            }
                            if (obj.has("legalperson")) {
                                values.put("LegalPerson", obj.getString("legalperson"));
                            } else {
                                values.put("LegalPerson", "");
                            }
                            if (obj.has("legalpersonpost")) {
                                values.put("LegalPersonPost", obj.getString("legalpersonpost"));
                            } else {
                                values.put("LegalPersonPost", "");
                            }
                            if (obj.has("legalpersonphone")) {
                                values.put("LegalPersonPhone", obj.getString("legalpersonphone"));
                            } else {
                                values.put("LegalPersonPhone", "");
                            }
//                            values.put("RegistrationNumber",obj.getString("registrationnumber"));
                            if (obj.has("safetyofficer")) {
                                values.put("SafetyOfficer", obj.getString("safetyofficer"));
                            } else {
                                values.put("SafetyOfficer", "");
                            }
                            if (obj.has("safetyofficerphone")) {
                                values.put("SafetyOfficerPhone", obj.getString("safetyofficerphone"));
                            } else {
                                values.put("SafetyOfficerPhone", "");
                            }
                            if (obj.has("address")) {
                                values.put("Address", obj.getString("address"));
                            } else {
                                values.put("Address", "");
                            }
                            values.put("Emphases", "1");
                            if (obj.has("modifyindex")) {
                                values.put("ModifyIndex", obj.getString("modifyindex"));
                            } else {
                                values.put("ModifyIndex", "");
                            }
                            if (obj.has("validflag")) {
                                values.put("ValidFlag", obj.getString("validflag"));
                            } else {
                                values.put("ValidFlag", "");
                            }
                            values.put("ModifyIndex2", "0");
                            if (obj.has("orgcode")) {
                                values.put("OrgCode", obj.getString("orgcode"));
                            } else {
                                values.put("OrgCode", "");
                            }
                            values.put("UserDefined", "0");
                            db.replace("ELL_Group", null, values);
                        } else if (jsonObj.has("DocumentElement") && jsonObj.toString().substring(jsonObj.toString().length() - 3, jsonObj.toString().length() - 2).equals("]")) {
                            array = jsonObj.getJSONObject("DocumentElement").getJSONArray("Table");
                            for (int i = 0; i < array.length(); i++) {
                                obj = array.getJSONObject(i);
                                ContentValues values = new ContentValues();
                                if (obj.has("businessid")) {
                                    values.put("BusinessId", obj.getString("businessid"));
                                } else {
                                    values.put("BusinessId", "");
                                }
                                if (obj.has("businessname")) {
                                    values.put("BusinessName", obj.getString("businessname"));
                                } else {
                                    values.put("BusinessName", "");
                                }
                                if (obj.has("businesstype")) {
                                    values.put("BusinessType", obj.getString("businesstype"));
                                } else {
                                    values.put("BusinessType", "");
                                }
                                if (obj.has("businesstype1")) {
                                    values.put("BusinessType1", obj.getString("businesstype1"));
                                } else {
                                    values.put("BusinessType1", "");
                                }
                                if (obj.has("legalperson")) {
                                    values.put("LegalPerson", obj.getString("legalperson"));
                                } else {
                                    values.put("LegalPerson", "");
                                }
                                if (obj.has("legalpersonpost")) {
                                    values.put("LegalPersonPost", obj.getString("legalpersonpost"));
                                } else {
                                    values.put("LegalPersonPost", "");
                                }
                                if (obj.has("legalpersonphone")) {
                                    values.put("LegalPersonPhone", obj.getString("legalpersonphone"));
                                } else {
                                    values.put("LegalPersonPhone", "");
                                }
                                if (obj.has("safetyofficer")) {
                                    values.put("SafetyOfficer", obj.getString("safetyofficer"));
                                } else {
                                    values.put("SafetyOfficer", "");
                                }
                                if (obj.has("safetyofficerphone")) {
                                    values.put("SafetyOfficerPhone", obj.getString("safetyofficerphone"));
                                } else {
                                    values.put("SafetyOfficerPhone", "");
                                }
                                if (obj.has("address")) {
                                    values.put("Address", obj.getString("address"));
                                } else {
                                    values.put("Address", "");
                                }
                                values.put("Emphases", "1");
                                if (obj.has("modifyindex")) {
                                    values.put("ModifyIndex", obj.getString("modifyindex"));
                                } else {
                                    values.put("ModifyIndex", "");
                                }
                                values.put("ModifyIndex2", "0");
                                if (obj.has("validflag")) {
                                    values.put("ValidFlag", obj.getString("validflag"));
                                } else {
                                    values.put("ValidFlag", "");
                                }
                                if (obj.has("orgcode")) {
                                    values.put("OrgCode", obj.getString("orgcode"));
                                } else {
                                    values.put("OrgCode", "");
                                }
                                values.put("UserDefined", "0");
                                db.replace("ELL_Group", null, values);
                            }


                        }
                        upDateCoalBusiness();
                    } catch (JSONException e) {
                        //关闭加载页
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                            progressDialog = null;
                        }
                        Toast.makeText(DateTransmissionActivity.this, "提交失败,数据错误缺失", Toast.LENGTH_SHORT).show();
                        Log.e("报错-upDateBusinessGroup", e.getMessage(), e);
                        e.printStackTrace();
                    }

                } else {
                    //关闭加载页
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                        progressDialog = null;
                    }
                    Toast.makeText(DateTransmissionActivity.this, "网络未响应，提交失败", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }
    /**
     * 更新煤矿企业信息
     */
    protected void upDateCoalBusiness() {
        // 初始化，只需要调用一次
        AssetsDatabaseManager.initManager(getApplication());
        // 获取管理对象，因为数据库需要通过管理对象才能够获取
        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
        // 通过管理对象获取数据库
        final SQLiteDatabase db = mg.getDatabase("users.db");
        Cursor c = db.rawQuery("SELECT* FROM ELL_Business", null);
        int ModifyIndex = 0;
        while (c.moveToNext()) {
//                Log.e("更新标数据",ModifyIndex+"");
            try {
                if (ModifyIndex < Integer.parseInt(c.getString(c.getColumnIndex("ModifyIndex")))) {
                    ModifyIndex = Integer.parseInt(c.getString(c.getColumnIndex("ModifyIndex")));
                }
            } catch (Exception e) {
                Log.e(TAG+"获取更新标数据库报错", e.toString());
            }


        }

        c.close();
        //添加参数
        HashMap<String, String> properties = new HashMap<>();
        properties.put("Xml", "<Request><data code='select-CoalBusiness'><no></no></data></Request>");
        properties.put("Token", "");
        WebServiceUtils.callWebService(WEB_SERVER_URL, "DynamicInvoke", properties, new WebServiceUtils.WebServiceCallBack() {

            //WebService接口返回的数据回调到这个方法中
            @Override
            public void callBack(SoapObject result) {
                if (result != null) {
                    Log.d("返回数据", result.toString());
                    try {
                        Object detail = result.getProperty("DynamicInvokeResult");
                        JSONObject jsonObj;
                        jsonObj = XML.toJSONObject(detail.toString());
                        JSONArray array;
                        JSONObject obj;
//                      Log.e("秘钥",jsonObj.getJSONObject("DocumentElement").getJSONObject("Table").getString("secretkey"));
                        if (jsonObj.has("DocumentElement") && jsonObj.toString().substring(jsonObj.toString().length() - 3, jsonObj.toString().length() - 2).equals("}")) {
                            obj = jsonObj.getJSONObject("DocumentElement").getJSONObject("Table");
                            ContentValues values = new ContentValues();
                            if(obj.has("businessid")) {
                                values.put("BusinessId", obj.getString("businessid"));
                            }else {
                                values.put("BusinessId", "");
                            }
                            if(obj.has("businessname")) {
                                values.put("BusinessName", obj.getString("businessname"));
                            }else {
                                values.put("BusinessName", "");
                            }
                            if(obj.has("businesstype")) {
                                values.put("BusinessType", obj.getString("businesstype"));
                            }else {
                                values.put("BusinessType", "");
                            }
                            if (obj.has("businesstype1")) {
                                values.put("BusinessType1", obj.getString("businesstype1"));
                            } else {
                                values.put("BusinessType1", "");
                            }
                            if(obj.has("legalperson")) {
                                values.put("LegalPerson", obj.getString("legalperson"));
                            }else {
                                values.put("LegalPerson", "");
                            }
                            if(obj.has("legalpersonpost")) {
                                values.put("LegalPersonPost", obj.getString("legalpersonpost"));
                            }else {
                                values.put("LegalPersonPost","");
                            }
                            if(obj.has("legalpersonphone")) {
                                values.put("LegalPersonPhone", obj.getString("legalpersonphone"));
                            }else {
                                values.put("LegalPersonPhone","");
                            }
                            if(obj.has("businesslicense")) {
                                values.put("RegistrationNumber", obj.getString("businesslicense"));
                            }else {
                                values.put("RegistrationNumber","");
                            }
                            if(obj.has("respperson")) {
                                values.put("SafetyOfficer", obj.getString("respperson"));
                            }else {
                                values.put("SafetyOfficer","");
                            }
                            if(obj.has("resppersonphone")) {
                                values.put("SafetyOfficerPhone", obj.getString("resppersonphone"));
                            }else {
                                values.put("SafetyOfficerPhone","");
                            }
                            if(obj.has("address")) {
                                if (obj.getString("address").equals("{\"xml:space\":\"preserve\"}")) {
                                    values.put("Address", "");
                                } else {
                                    values.put("Address", obj.getString("address"));
                                }
                            }else {
                                values.put("Address","");
                            }
                            values.put("Emphases", "1");
                            if(obj.has("validflag")) {
                                values.put("ValidFlag", obj.getString("validflag"));
                            }else {
                                values.put("ValidFlag","");
                            }
                            if(obj.has("mineshaftcond")) {
                                values.put("MineShaftCond", obj.getString("mineshaftcond"));
                            }else {
                                values.put("MineShaftCond","");
                            }
                            if(obj.has("mineshafttype")) {
                                values.put("MineShaftType", obj.getString("mineshafttype"));
                            }else {
                                values.put("MineShaftType","");
                            }
                            if(obj.has("geologicalreserves")) {
                                values.put("GeologicalReserves", obj.getString("geologicalreserves")+ "万吨");
                            }else {
                                values.put("GeologicalReserves","");
                            }
                            if(obj.has("workablereserves")) {
                                values.put("WorkableReserves", obj.getString("workablereserves")+ "万吨");
                            }else {
                                values.put("WorkableReserves","");
                            }
                            if(obj.has("designprodcapacity")) {
                                values.put("DesignProdCapacity", obj.getString("designprodcapacity") + "万吨/年");
                            }else {
                                values.put("DesignProdCapacity","");
                            }
                            if(obj.has("checkprodcapacity")) {
                                values.put("CheckProdCapacity", obj.getString("checkprodcapacity") + "万吨/年");
                            }else {
                                values.put("CheckProdCapacity","");
                            }
                            if(obj.has("area")) {
                                values.put("Area", obj.getString("area")+ "平方千米");
                            }else {
                                values.put("Area","");
                            }
                            if(obj.has("wthdraw")) {
                                values.put("Wthdraw", obj.getString("wthdraw") + "万吨/年");
                            }else {
                                values.put("Wthdraw","");
                            }
                            if(obj.has("coaltype")) {
                                values.put("CoalType", obj.getString("coaltype"));
                            }else {
                                values.put("CoalType","");
                            }
                            if(obj.has("workableseam")) {
                                values.put("WorkableSeam", obj.getString("workableseam"));
                            }else {
                                values.put("WorkableSeam","");
                            }
                            if(obj.has("explorecraft")) {
                                values.put("ExploreCraft", obj.getString("explorecraft"));
                            }else {
                                values.put("ExploreCraft","");
                            }
                            if(obj.has("contractphone")) {
                                values.put("ContractPhone", obj.getString("contractphone"));
                            }else {
                                values.put("ContractPhone","");
                            }
                            if(obj.has("exploremode")) {
                                values.put("ExploreMode", obj.getString("exploremode"));
                            }else {
                                values.put("ExploreMode","");
                            }
                            if(obj.has("riskappraisal")) {
                                values.put("RiskAppraisal", obj.getString("riskappraisal"));
                            }else {
                                values.put("RiskAppraisal","");
                            }
                            if(obj.has("gaslevel")) {
                                values.put("GasLevel", obj.getString("gaslevel"));
                            }else {
                                values.put("GasLevel","");
                            }
                            if(obj.has("coalseamlevel")) {
                                values.put("CoalSeamLevel", obj.getString("coalseamlevel"));
                            }else {
                                values.put("CoalSeamLevel","");
                            }
                            if(obj.has("explosion")) {
                                values.put("Explosion", obj.getString("explosion"));
                            }else {
                                values.put("Explosion","");
                            }
                            if(obj.has("ventilatemode")) {
                                values.put("VentilateMode", obj.getString("ventilatemode"));
                            }else {
                                values.put("VentilateMode","");
                            }
                            db.replace("ELL_Business", null, values);
                        } else if (jsonObj.has("DocumentElement") && jsonObj.toString().substring(jsonObj.toString().length() - 3, jsonObj.toString().length() - 2).equals("]")) {
                            array = jsonObj.getJSONObject("DocumentElement").getJSONArray("Table");
                            for (int i = 0; i < array.length(); i++) {
                                obj = array.getJSONObject(i);
                                ContentValues values = new ContentValues();
                                if(obj.has("businessid")) {
                                    values.put("BusinessId", obj.getString("businessid"));
                                }else {
                                    values.put("BusinessId","");
                                }
                                if(obj.has("businessname")) {
                                    values.put("BusinessName", obj.getString("businessname"));
                                }else {
                                    values.put("BusinessName","");
                                }
                                if(obj.has("businesstype")) {
                                    values.put("BusinessType", obj.getString("businesstype"));
                                }else {
                                    values.put("BusinessType","");
                                }
                                if (obj.has("businesstype1")) {
                                    values.put("BusinessType1", obj.getString("businesstype1"));
                                } else {
                                    values.put("BusinessType1", "");
                                }
                                if(obj.has("legalperson")) {
                                    values.put("LegalPerson", obj.getString("legalperson"));
                                }else {
                                    values.put("LegalPerson", "");
                                }
                                if(obj.has("legalpersonpost")) {
                                    values.put("LegalPersonPost", obj.getString("legalpersonpost"));
                                }else {
                                    values.put("LegalPersonPost","");
                                }
                                if(obj.has("legalpersonphone")) {
                                    values.put("LegalPersonPhone", obj.getString("legalpersonphone"));
                                }else {
                                    values.put("LegalPersonPhone","");
                                }
                                if(obj.has("businesslicense")) {
                                    values.put("RegistrationNumber", obj.getString("businesslicense"));
                                }else {
                                    values.put("RegistrationNumber","");
                                }
                                if(obj.has("respperson")) {
                                    values.put("SafetyOfficer", obj.getString("respperson"));
                                }else {
                                    values.put("SafetyOfficer","");
                                }
                                if(obj.has("resppersonphone")) {
                                    values.put("SafetyOfficerPhone", obj.getString("resppersonphone"));
                                }else {
                                    values.put("SafetyOfficerPhone","");
                                }
                                if(obj.has("address")) {
                                    if (obj.getString("address").equals("{\"xml:space\":\"preserve\"}")) {
                                        values.put("Address", "");
                                    } else {
                                        values.put("Address", obj.getString("address"));
                                    }
                                }else {
                                    values.put("Address","");
                                }
                                values.put("Emphases", "1");
                                if(obj.has("validflag")) {
                                    values.put("ValidFlag", obj.getString("validflag"));
                                }else {
                                    values.put("ValidFlag","");
                                }
                                if(obj.has("mineshaftcond")) {
                                    values.put("MineShaftCond", obj.getString("mineshaftcond"));
                                }else {
                                    values.put("MineShaftCond","");
                                }
                                if(obj.has("mineshafttype")) {
                                    values.put("MineShaftType", obj.getString("mineshafttype"));
                                }else {
                                    values.put("MineShaftType","");
                                }
                                if(obj.has("geologicalreserves")) {
                                    values.put("GeologicalReserves", obj.getString("geologicalreserves"));
                                }else {
                                    values.put("GeologicalReserves","");
                                }
                                if(obj.has("workablereserves")) {
                                    values.put("WorkableReserves", obj.getString("workablereserves"));
                                }else {
                                    values.put("WorkableReserves","");
                                }
                                if(obj.has("designprodcapacity")) {
                                    values.put("DesignProdCapacity", obj.getString("designprodcapacity"));
                                }else {
                                    values.put("DesignProdCapacity","");
                                }
                                if(obj.has("checkprodcapacity")) {
                                    values.put("CheckProdCapacity", obj.getString("checkprodcapacity"));
                                }else {
                                    values.put("CheckProdCapacity","");
                                }
                                if(obj.has("area")) {
                                    values.put("Area", obj.getString("area") );
                                }else {
                                    values.put("Area","");
                                }
                                if(obj.has("wthdraw")) {
                                    values.put("Wthdraw", obj.getString("wthdraw"));
                                }else {
                                    values.put("Wthdraw","");
                                }
                                if(obj.has("coaltype")) {
                                    values.put("CoalType", obj.getString("coaltype"));
                                }else {
                                    values.put("CoalType","");
                                }
                                if(obj.has("workableseam")) {
                                    values.put("WorkableSeam", obj.getString("workableseam"));
                                }else {
                                    values.put("WorkableSeam","");
                                }
                                if(obj.has("explorecraft")) {
                                    values.put("ExploreCraft", obj.getString("explorecraft"));
                                }else {
                                    values.put("ExploreCraft","");
                                }
                                if(obj.has("contractphone")) {
                                    values.put("ContractPhone", obj.getString("contractphone"));
                                }else {
                                    values.put("ContractPhone","");
                                }
                                if(obj.has("exploremode")) {
                                    values.put("ExploreMode", obj.getString("exploremode"));
                                }else {
                                    values.put("ExploreMode","");
                                }
                                if(obj.has("riskappraisal")) {
                                    values.put("RiskAppraisal", obj.getString("riskappraisal"));
                                }else {
                                    values.put("RiskAppraisal","");
                                }
                                if(obj.has("gaslevel")) {
                                    values.put("GasLevel", obj.getString("gaslevel"));
                                }else {
                                    values.put("GasLevel","");
                                }
                                if(obj.has("coalseamlevel")) {
                                    values.put("CoalSeamLevel", obj.getString("coalseamlevel"));
                                }else {
                                    values.put("CoalSeamLevel","");
                                }
                                if(obj.has("explosion")) {
                                    values.put("Explosion", obj.getString("explosion"));
                                }else {
                                    values.put("Explosion","");
                                }
                                if(obj.has("ventilatemode")) {
                                    values.put("VentilateMode", obj.getString("ventilatemode"));
                                }else {
                                    values.put("VentilateMode","");
                                }
                                db.replace("ELL_Business", null, values);
                            }

                        }
                        upDateCompany();
                    } catch (JSONException e) {
                        //关闭加载页
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                            progressDialog = null;
                        }
                        Toast.makeText(DateTransmissionActivity.this, "提交失败", Toast.LENGTH_SHORT).show();
                        Log.e(TAG+"报错+upDateCoalBusiness", e.getMessage());
                        e.printStackTrace();
                    }

                } else {
                    //关闭加载页
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                        progressDialog = null;
                    }
                    Toast.makeText(DateTransmissionActivity.this, "网络未响应，提交失败", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    /**
     * 更新机构信息
     */
    protected void upDateCompany() {
        progressDialog.setTitile("更新机构信息");
        //添加参数
        HashMap<String, String> properties = new HashMap<>();
        properties.put("Xml", "<Request><data  code='select-Company'><no></no></data></Request>");
        properties.put("Token", "");
        WebServiceUtils.callWebService(WEB_SERVER_URL, "DynamicInvoke", properties, new WebServiceUtils.WebServiceCallBack() {

            //WebService接口返回的数据回调到这个方法中
            @Override
            public void callBack(SoapObject result) {
                if (result != null) {
//                    Log.e("登录返回数据",result.toString());
                    try {
                        Object detail = result.getProperty("DynamicInvokeResult");
                        JSONObject jsonObj;
                        jsonObj = XML.toJSONObject(detail.toString());
                        JSONArray array;
                        JSONObject obj;
                        // 初始化，只需要调用一次
                        AssetsDatabaseManager.initManager(getApplication());
                        // 获取管理对象，因为数据库需要通过管理对象才能够获取
                        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
                        // 通过管理对象获取数据库
                        SQLiteDatabase db = mg.getDatabase("users.db");
//                      Log.e("秘钥",jsonObj.getJSONObject("DocumentElement").getJSONObject("Table").getString("secretkey"));
                        if (jsonObj.has("DocumentElement") && jsonObj.toString().substring(jsonObj.toString().length() - 3, jsonObj.toString().length() - 2).equals("}")) {
                            obj = jsonObj.getJSONObject("DocumentElement").getJSONObject("Table");
//                            db.execSQL("REPLACE INTO Base_Company VALUES('" +
//                                    obj.getString("code") + "','" +
//                                    obj.getString("parentid") + "','" +
//                                    obj.getString("fullname") + "')");
                            ContentValues values = new ContentValues();
                            if( obj.has("code")){
                                values.put("CompanyId",obj.getString("code"));
                            }else {
                                values.put("CompanyId","");
                            }
                            if( obj.has("companyid")){
                                values.put("ParentId",obj.getString("companyid"));
                            }
                            if(obj.has("fullname")){
                                values.put("FullName",obj.getString("fullname"));
                            }
//                            db.replace("Base_Company",null,values);
                            if(obj.has("validflag")&&obj.has("code")&&obj.getString("validflag").equals("1")){
                                String[] args = {String.valueOf(obj.getString("code"))};
                                db.delete("Base_Company","CompanyId=?",args);
                            }else {
                                db.replace("Base_Company",null,values);
                            }

                        } else if (jsonObj.has("DocumentElement") && jsonObj.toString().substring(jsonObj.toString().length() - 3, jsonObj.toString().length() - 2).equals("]")) {
                            array = jsonObj.getJSONObject("DocumentElement").getJSONArray("Table");
                            for (int i = 0; i < array.length(); i++) {
                                obj = array.getJSONObject(i);
//                                db.execSQL("REPLACE INTO Base_Company VALUES('" +
//                                        obj.getString("code") + "','" +
//                                        obj.getString("parentid") + "','" +
//                                        obj.getString("fullname") + "')");
                                ContentValues values = new ContentValues();
                                if( obj.has("code")){
                                    values.put("CompanyId",obj.getString("code"));
                                }else {
                                    values.put("CompanyId","");
                                }
                                if( obj.has("companyid")){
                                    values.put("ParentId",obj.getString("companyid"));
                                }
                                if(obj.has("fullname")){
                                    values.put("FullName",obj.getString("fullname"));
                                }
//                                db.replace("Base_Company",null,values);
                                if(obj.has("validflag")&&obj.has("code")&&obj.getString("validflag").equals("1")){
                                    String[] args = {String.valueOf(obj.getString("code"))};
                                    db.delete("Base_Company","CompanyId=?",args);
                                }else {
                                    db.replace("Base_Company",null,values);
                                }
                            }
                        }
                        upDateDepartment();
                    } catch (JSONException e) {
                        //关闭加载页
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                            progressDialog = null;
                        }
                        Toast.makeText(DateTransmissionActivity.this, "提交失败", Toast.LENGTH_SHORT).show();
                        Log.e("JSON exception", e.getMessage(),e);
                        e.printStackTrace();
                    }

                } else {
                    //关闭加载页
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                        progressDialog = null;
                    }
                    Toast.makeText(DateTransmissionActivity.this, "网络未响应，提交失败", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    /**
     * 更新部门信息
     */
    protected void upDateDepartment() {
        //添加参数
        HashMap<String, String> properties = new HashMap<>();
        properties.put("Xml", "<Request><data  code='delect-Department'><no></no></data></Request>");
        properties.put("Token", "");
        WebServiceUtils.callWebService(WEB_SERVER_URL, "DynamicInvoke", properties, new WebServiceUtils.WebServiceCallBack() {

            //WebService接口返回的数据回调到这个方法中
            @Override
            public void callBack(SoapObject result) {
                if (result != null) {
//                    Log.e("登录返回数据",result.toString());
                    try {
                        Object detail = result.getProperty("DynamicInvokeResult");
                        JSONObject jsonObj;
                        jsonObj = XML.toJSONObject(detail.toString());
                        JSONArray array;
                        JSONObject obj;
                        // 初始化，只需要调用一次
                        AssetsDatabaseManager.initManager(getApplication());
                        // 获取管理对象，因为数据库需要通过管理对象才能够获取
                        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
                        // 通过管理对象获取数据库
                        SQLiteDatabase db = mg.getDatabase("users.db");
//                      Log.e("秘钥",jsonObj.getJSONObject("DocumentElement").getJSONObject("Table").getString("secretkey"));
                        if (jsonObj.has("DocumentElement") && jsonObj.toString().substring(jsonObj.toString().length() - 3, jsonObj.toString().length() - 2).equals("}")) {
                            obj = jsonObj.getJSONObject("DocumentElement").getJSONObject("Table");
//                            db.execSQL("REPLACE INTO Base_Department VALUES('" +
//                                    obj.getString("code") + "','" +
//                                    obj.getString("code").substring(0, 6) + "','" +
//                                    obj.getString("fullname") + "')");
                            ContentValues values = new ContentValues();
                            if (obj.has("code")){
                                values.put("DepartmentId",obj.getString("code"));
//                                values.put("CompanyId",obj.getString("code").substring(0, 6));
                                String[] strings = obj.getString("parentid").split("-");
                                Log.e("LoginActivity","string[0]为"+strings[0]);
                                values.put("CompanyId", strings[0]);
                            }else {
                                values.put("DepartmentId","");
                                values.put("CompanyId","");
                            }
                            if (obj.has("fullname")){
                                values.put("FullName",obj.getString("fullname"));
                            }
//                            db.replace("Base_Department",null,values);
                            if(obj.has("validflag")&&obj.has("code")&&obj.getString("validflag").equals("1")){
                                String[] args = {String.valueOf(obj.getString("code"))};
                                db.delete("Base_Department","DepartmentId=?",args);
                            }else {
                                db.replace("Base_Department",null,values);
                            }

                        } else if (jsonObj.has("DocumentElement") && jsonObj.toString().substring(jsonObj.toString().length() - 3, jsonObj.toString().length() - 2).equals("]")) {
                            array = jsonObj.getJSONObject("DocumentElement").getJSONArray("Table");
                            for (int i = 0; i < array.length(); i++) {
                                obj = array.getJSONObject(i);
//                                db.execSQL("REPLACE INTO Base_Department VALUES('" +
//                                        obj.getString("code") + "','" +
//                                        obj.getString("code").substring(0, 6) + "','" +
//                                        obj.getString("fullname") + "')");
                                ContentValues values = new ContentValues();
                                if (obj.has("code")){
                                    values.put("DepartmentId",obj.getString("code"));
//                                    values.put("CompanyId",obj.getString("code").substring(0, 6));
                                    String[] strings = obj.getString("parentid").split("-");
                                    Log.e("LoginActivity","string[0]为"+strings[0]);
                                    values.put("CompanyId", strings[0]);
                                }else {
                                    values.put("DepartmentId","");
                                    values.put("CompanyId","");
                                }
                                if (obj.has("fullname")){
                                    values.put("FullName",obj.getString("fullname"));
                                }

                                if(obj.has("validflag")&&obj.has("code")&&obj.getString("validflag").equals("1")){
                                    String[] args = {String.valueOf(obj.getString("code"))};
                                    db.delete("Base_Department","DepartmentId=?",args);
                                }else {
                                    db.replace("Base_Department",null,values);
                                }
                            }

                        }
                        upDateUser();
                    } catch (JSONException e) {
                        //关闭加载页
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                            progressDialog = null;
                        }
                        Toast.makeText(DateTransmissionActivity.this, "提交失败", Toast.LENGTH_SHORT).show();
                        Log.e(TAG+"报错+upDateDepartment", e.getMessage(),e);
                        e.printStackTrace();
                    }

                } else {
                    //关闭加载页
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                        progressDialog = null;
                    }
                    Toast.makeText(DateTransmissionActivity.this, "网络未响应，提交失败", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    /**
     * 更新用户信息
     */
    protected void upDateUser() {
        progressDialog.setTitile("下载用户信息");
        // 初始化，只需要调用一次
        AssetsDatabaseManager.initManager(getApplication());
        // 获取管理对象，因为数据库需要通过管理对象才能够获取
        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
        // 通过管理对象获取数据库
        final SQLiteDatabase db = mg.getDatabase("users.db");
        Cursor c = db.rawQuery("SELECT* FROM Base_User ", null);
        int ModifyIndex = 0;
        while (c.moveToNext()) {
            if (ModifyIndex < Integer.parseInt(c.getString(c.getColumnIndex("ModifyIndex")))) {
                ModifyIndex = Integer.parseInt(c.getString(c.getColumnIndex("ModifyIndex")));
            }
        }
        c.close();
        //添加参数
        HashMap<String, String> properties = new HashMap<>();
        properties.put("Xml", "<Request><data  code='update-User'><no><ModifyIndex>0</ModifyIndex></no></data></Request>");
        properties.put("Token", "");
//        Log.e("登录数据",properties.toString());
        WebServiceUtils.callWebService(WEB_SERVER_URL, "DynamicInvoke", properties, new WebServiceUtils.WebServiceCallBack() {

            //WebService接口返回的数据回调到这个方法中
            @Override
            public void callBack(SoapObject result) {
                if (result != null) {
                    try {

                        Object detail = result.getProperty("DynamicInvokeResult");
                        JSONObject jsonObj;
                        jsonObj = XML.toJSONObject(detail.toString());
                        JSONArray array;
                        JSONObject obj;
//                        String authstartdate = "";
//                        String authenddate = "";
//                        String workingdate = "";
//                      Log.e("秘钥",jsonObj.getJSONObject("DocumentElement").getJSONObject("Table").getString("secretkey"));
                        if (jsonObj.has("DocumentElement") && jsonObj.toString().substring(jsonObj.toString().length() - 3, jsonObj.toString().length() - 2).equals("}")) {
                            obj = jsonObj.getJSONObject("DocumentElement").getJSONObject("Table");
                           /* if (obj.has("authstartdate")) {
                                authstartdate = obj.getString("authstartdate");
                            }
                            if (obj.has("authenddate")) {
                                authenddate = obj.getString("authenddate");
                            }
                            if (obj.has("workingdate")) {
                                workingdate = obj.getString("workingdate");
                            }*/

                            ContentValues values = new ContentValues();
                            if(obj.has("userid")) {
                                values.put("UserId", obj.getString("userid"));
                            }else {
                                values.put("UserId","");
                            }
                            if(obj.has("departmentcode")&&obj.getString("departmentcode").length() > 6) {
                                String[] strings = obj.getString("departmentcode").split("-");
                                values.put("CompanyId", strings[0]);
                            }else {
                                values.put("CompanyId","");
                            }
                            if(obj.has("departmentcode")){
                                values.put("DepartmentId", obj.getString("departmentcode"));
                            }else {
                                values.put("DepartmentId","");
                            }
                            if(obj.has("code")){
                                values.put("Code", obj.getString("code"));
                            }else {
                                values.put("Code","");
                            }
                            if(obj.has("password")){
                                values.put("Password", obj.getString("password"));
                            }else {
                                values.put("Password","");
                            }
                            if(obj.has("realname")){
                                values.put("RealName", obj.getString("realname"));
                            }else {
                                values.put("RealName","");
                            }
                            if(obj.has("gender")){
                                values.put("Gender", obj.getString("gender"));
                            }else {
                                values.put("Gender","");
                            }
                            if(obj.has("mobile")){
                                values.put("Mobile", obj.getString("mobile"));
                            }else {
                                values.put("Mobile","");
                            }
                            if(obj.has("authstartdate")){
                                values.put("AuthStartDate", obj.getString("authstartdate"));
                            }else {
                                values.put("AuthStartDate","");
                            }
                            if(obj.has("authenddate")){
                                values.put("AuthEndDate", obj.getString("authenddate"));
                            }else {
                                values.put("AuthEndDate","");
                            }
                            if(obj.has("modifyindex")){
                                values.put("ModifyIndex", obj.getString("modifyindex"));
                            }else {
                                values.put("ModifyIndex","");
                            }
                            if(obj.has("validflag")){
                                values.put("ValidFlag", obj.getString("validflag"));
                            }else {
                                values.put("ValidFlag","");
                            }
                            if(obj.has("idcard")){
                                values.put("IDCard", obj.getString("idcard"));
                            }else {
                                values.put("IDCard","");
                            }
                            if(obj.has("age")){
                                values.put("Age", obj.getString("age"));
                            }else {
                                values.put("Age","");
                            }
                            if(obj.has("education")){
                                values.put("Education", obj.getString("education"));
                            }else {
                                values.put("Education","");
                            }
                            if(obj.has("workingyear")){
                                values.put("WorkingYear", obj.getString("workingyear"));
                            }else {
                                values.put("WorkingYear","");
                            }
                            if(obj.has("workingdate")){
                                values.put("WorkingDate", obj.getString("workingdate"));
                            }else {
                                values.put("WorkingDate","");
                            }
                            if(obj.has("maritalstatus")){
                                values.put("MaritalStatus", obj.getString("maritalstatus"));
                            }else {
                                values.put("MaritalStatus","");
                            }
                            if(obj.has("physiclalstatus")){
                                values.put("PhysiclalStatus", obj.getString("physiclalstatus"));
                            }else {
                                values.put("PhysiclalStatus","");
                            }
                            if(values.get("DepartmentId").toString().length()>0&&values.get("Code").toString().length()>0&&values.get("RealName").toString().length()>0&&values.get("Password").toString().length()>0){
                                db.replace("Base_User", null, values);
                            }

                        } else if (jsonObj.has("DocumentElement") && jsonObj.toString().substring(jsonObj.toString().length() - 3, jsonObj.toString().length() - 2).equals("]")) {
                            array = jsonObj.getJSONObject("DocumentElement").getJSONArray("Table");
                            for (int i = 0; i < array.length(); i++) {
                                obj = array.getJSONObject(i);
                                ContentValues values = new ContentValues();
                                if(obj.has("userid")) {
                                    values.put("UserId", obj.getString("userid"));
                                }else {
                                    values.put("UserId","");
                                }
                                if(obj.has("departmentcode")&&obj.getString("departmentcode").length() > 6) {
                                    String[] strings = obj.getString("departmentcode").split("-");
                                    values.put("CompanyId", strings[0]);
                                }else {
                                    values.put("CompanyId","");
                                }
                                if(obj.has("departmentcode")){
                                    values.put("DepartmentId", obj.getString("departmentcode"));
                                }else {
                                    values.put("DepartmentId","");
                                }
                                if(obj.has("code")){
                                    values.put("Code", obj.getString("code"));
                                }else {
                                    values.put("Code","");
                                }
                                if(obj.has("password")){
                                    values.put("Password", obj.getString("password"));
                                }else {
                                    values.put("Password","");
                                }
                                if(obj.has("realname")){
                                    values.put("RealName", obj.getString("realname"));
                                }else {
                                    values.put("RealName","");
                                }
                                if(obj.has("gender")){
                                    values.put("Gender", obj.getString("gender"));
                                }else {
                                    values.put("Gender","");
                                }
                                if(obj.has("mobile")){
                                    values.put("Mobile", obj.getString("mobile"));
                                }else {
                                    values.put("Mobile","");
                                }
                                if(obj.has("authstartdate")){
                                    values.put("AuthStartDate", obj.getString("authstartdate"));
                                }else {
                                    values.put("AuthStartDate","");
                                }
                                if(obj.has("authenddate")){
                                    values.put("AuthEndDate", obj.getString("authenddate"));
                                }else {
                                    values.put("AuthEndDate","");
                                }
                                if(obj.has("modifyindex")){
                                    values.put("ModifyIndex", obj.getString("modifyindex"));
                                }else {
                                    values.put("ModifyIndex","");
                                }
                                if(obj.has("validflag")){
                                    values.put("ValidFlag", obj.getString("validflag"));
                                }else {
                                    values.put("ValidFlag","");
                                }
                                if(obj.has("idcard")){
                                    values.put("IDCard", obj.getString("idcard"));
                                }else {
                                    values.put("IDCard","");
                                }
                                if(obj.has("age")){
                                    values.put("Age", obj.getString("age"));
                                }else {
                                    values.put("Age","");
                                }
                                if(obj.has("education")){
                                    values.put("Education", obj.getString("education"));
                                }else {
                                    values.put("Education","");
                                }
                                if(obj.has("workingyear")){
                                    values.put("WorkingYear", obj.getString("workingyear"));
                                }else {
                                    values.put("WorkingYear","");
                                }
                                if(obj.has("workingdate")){
                                    values.put("WorkingDate", obj.getString("workingdate"));
                                }else {
                                    values.put("WorkingDate","");
                                }
                                if(obj.has("maritalstatus")){
                                    values.put("MaritalStatus", obj.getString("maritalstatus"));
                                }else {
                                    values.put("MaritalStatus","");
                                }
                                if(obj.has("physiclalstatus")){
                                    values.put("PhysiclalStatus", obj.getString("physiclalstatus"));
                                }else {
                                    values.put("PhysiclalStatus","");
                                }
                                if(values.get("DepartmentId").toString().length()>0&&values.get("Code").toString().length()>0&&values.get("RealName").toString().length()>0&&values.get("Password").toString().length()>0){
                                    db.replace("Base_User", null, values);
                                }
                            }

                        }
                        Toast.makeText(DateTransmissionActivity.this, "更新完成", Toast.LENGTH_SHORT).show();

                        finish();
                        overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                    } catch (JSONException e) {
                        //关闭加载页
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                            progressDialog = null;
                        }
                        Toast.makeText(DateTransmissionActivity.this, "提交失败", Toast.LENGTH_SHORT).show();
                        Log.e(TAG+"报错+upDateUser", e.getMessage());
                        e.printStackTrace();
                    }

                } else {
                    //关闭加载页
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                        progressDialog = null;
                    }
                    Toast.makeText(DateTransmissionActivity.this, "网络未响应，提交失败", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }


    /**
     * 上传企业信息（区分是否修改新增）
     */
    protected void businessUpDate() {

        // 初始化，只需要调用一次
        AssetsDatabaseManager.initManager(getApplication());
        // 获取管理对象，因为数据库需要通过管理对象才能够获取
        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
        // 通过管理对象获取数据库
        SQLiteDatabase db = mg.getDatabase("users.db");
//        try {;
        Cursor cursor1 = db.rawQuery("SELECT* FROM ELL_Business", null);
        while (cursor1.moveToNext()) {
            try {
                if (!cursor1.getString(cursor1.getColumnIndex("UserDefined")).equals("0")) {
                    businessDateUp(cursor1.getString(cursor1.getColumnIndex("UserDefined")), cursor1.getString(cursor1.getColumnIndex("BusinessId")), cursor1.getString(cursor1.getColumnIndex("BusinessType")));
                }
            } catch (Exception e) {
                Log.e("获得UserDefined数据报错", e.toString());
            }

        }
        cursor1.close();
    }

    /**
     * 上传企业信息(区分好后进行上传)
     */
    protected void businessDateUp(String s, final String BusinessId, String BusinessType) {

        if (progressDialog == null) {
            progressDialog = CustomProgressDialog.createDialog(this);
        }
        progressDialog.show();
        // 初始化，只需要调用一次
        AssetsDatabaseManager.initManager(getApplication());
        // 获取管理对象，因为数据库需要通过管理对象才能够获取
        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
        // 通过管理对象获取数据库
        final SQLiteDatabase db = mg.getDatabase("users.db");
//加载页添加

        Cursor cursor1 = db.rawQuery("SELECT* FROM ELL_Business WHERE BusinessId = ?",
                new String[]{BusinessId});
        cursor1.moveToFirst();
        //添加参数
        HashMap<String, String> properties = new HashMap<>();
        if (BusinessType.equals("煤矿")) {
            properties.put("Xml", "<Request><data  code='upload_CoalBusiness'><no>" +
                    "<OperateKind>" + s + "</OperateKind>" +
                    "<BusinessId>" + BusinessId + "</BusinessId>" +
                    "<BusinessName>" + cursor1.getString(cursor1.getColumnIndex("BusinessName")) + "</BusinessName>" +
                    "<LegalPerson>" + cursor1.getString(cursor1.getColumnIndex("LegalPerson")) + "</LegalPerson>" +
                    "<LegalPersonPhone>" + cursor1.getString(cursor1.getColumnIndex("LegalPersonPhone")) + "</LegalPersonPhone>" +
                    "<RespPerson>" + cursor1.getString(cursor1.getColumnIndex("SafetyOfficer")) + "</RespPerson>" +
                    "<RespPersonPhone>" + cursor1.getString(cursor1.getColumnIndex("SafetyOfficerPhone")) + "</RespPersonPhone>" +
                    "<MineShaftCond>" + cursor1.getString(cursor1.getColumnIndex("MineShaftCond")) + "</MineShaftCond>" +
                    "<MineShaftType>" + cursor1.getString(cursor1.getColumnIndex("MineShaftType")) + "</MineShaftType>" +
                    "<GeologicalReserves>" + cursor1.getString(cursor1.getColumnIndex("GeologicalReserves")) + "</GeologicalReserves>" +
                    "<WorkableReserves>" + cursor1.getString(cursor1.getColumnIndex("WorkableReserves")) + "</WorkableReserves>" +
                    "<DesignProdCapacity>" + cursor1.getString(cursor1.getColumnIndex("DesignProdCapacity")) + "</DesignProdCapacity>" +
                    "<CheckProdCapacity>" + cursor1.getString(cursor1.getColumnIndex("CheckProdCapacity")) + "</CheckProdCapacity>" +
                    "<Area>" + cursor1.getString(cursor1.getColumnIndex("Area")) + "</Area>" +
                    "<Wthdraw>" + cursor1.getString(cursor1.getColumnIndex("Wthdraw")) + "</Wthdraw>" +
                    "<CoalType>" + cursor1.getString(cursor1.getColumnIndex("CoalType")) + "</CoalType>" +
                    "<WorkableSeam>" + cursor1.getString(cursor1.getColumnIndex("WorkableSeam")) + "</WorkableSeam>" +
                    "<ExploreCraft>" + cursor1.getString(cursor1.getColumnIndex("ExploreCraft")) + "</ExploreCraft>" +
                    "<ContractPhone>" + cursor1.getString(cursor1.getColumnIndex("ContractPhone")) + "</ContractPhone>" +
                    "<ExploreMode>" + cursor1.getString(cursor1.getColumnIndex("ExploreMode")) + "</ExploreMode>" +
                    "<RiskAppraisal>" + cursor1.getString(cursor1.getColumnIndex("RiskAppraisal")) + "</RiskAppraisal>" +
                    "<GasLevel>" + cursor1.getString(cursor1.getColumnIndex("GasLevel")) + "</GasLevel>" +
                    "<CoalSeamLevel>" + cursor1.getString(cursor1.getColumnIndex("CoalSeamLevel")) + "</CoalSeamLevel>" +
                    "<Explosion>" + cursor1.getString(cursor1.getColumnIndex("Explosion")) + "</Explosion>" +
                    "<VentilateMode>" + cursor1.getString(cursor1.getColumnIndex("VentilateMode")) + "</VentilateMode>" +
                    "<BusinessLicense>" + cursor1.getString(cursor1.getColumnIndex("RegistrationNumber")) + "</BusinessLicense>" +
                    "<ValidFlag>0</ValidFlag>" +
                    "<BusinessType>" + cursor1.getString(cursor1.getColumnIndex("BusinessType")) + "</BusinessType>" +
                    "<LegalPersonPost></LegalPersonPost>" +
                    "<Address>" + cursor1.getString(cursor1.getColumnIndex("Address")) + "</Address>" +
                    "<UploadUserID>" + SharedPreferencesUtil.getStringData(this, "userId", "") + "</UploadUserID></no></data></Request>");

        } else {
            properties.put("Xml", "<Request><data  code='upload_Business'><no>" +
                    "<OperateKind>" + s + "</OperateKind>" +
                    "<BusinessId>" + BusinessId + "</BusinessId>" +
                    "<BusinessName>" + cursor1.getString(cursor1.getColumnIndex("BusinessName")) + "</BusinessName>" +
                    "<BusinessType>" + cursor1.getString(cursor1.getColumnIndex("BusinessType")) + "</BusinessType>" +
                    "<LegalPerson>" + cursor1.getString(cursor1.getColumnIndex("LegalPerson")) + "</LegalPerson>" +
                    "<LegalPersonPhone>" + cursor1.getString(cursor1.getColumnIndex("LegalPersonPhone")) + "</LegalPersonPhone>" +
                    "<OrgCode>" + cursor1.getString(cursor1.getColumnIndex("OrgCode")) + "</OrgCode>" +
                    "<SafetyOfficer>" + cursor1.getString(cursor1.getColumnIndex("SafetyOfficer")) + "</SafetyOfficer>" +
                    "<SafetyOfficerPhone>" + cursor1.getString(cursor1.getColumnIndex("SafetyOfficerPhone")) + "</SafetyOfficerPhone>" +
                    "<Address>" + cursor1.getString(cursor1.getColumnIndex("Address")) + "</Address>" +
                    "<ValidFlag>0</ValidFlag>" +
                    "<LegalPersonPost>" + cursor1.getString(cursor1.getColumnIndex("LegalPersonPost")) + "</LegalPersonPost>" +
                    "<UploadUserID>" + SharedPreferencesUtil.getStringData(this, "userId", "") + "</UploadUserID></no></data></Request>");
        }
        properties.put("Token", "");
        Log.e("企业信息上传数据", properties.toString());
        WebServiceUtils.callWebService(WEB_SERVER_URL, "DynamicInvoke", properties, new WebServiceUtils.WebServiceCallBack() {

            //WebService接口返回的数据回调到这个方法中
            @Override
            public void callBack(SoapObject result) {
                if (result != null) {
                    Log.e("企业信息上传返回数据", result.toString());
                    try {
                        Object detail = result.getProperty("DynamicInvokeResult");
                        if (detail.toString().equals("True")) {
                            ContentValues values = new ContentValues();
                            values.put("UserDefined", "0");
                            String whereClause = "BusinessId=?";
                            String[] whereArgs = new String[]{BusinessId};
                            db.update("ELL_Business", values, whereClause, whereArgs);
                            Log.d("修改企业数据", "" + values);
                            Toast.makeText(DateTransmissionActivity.this, "上传数据成功", Toast.LENGTH_SHORT).show();
                            if (progressDialog != null) {
                                progressDialog.dismiss();
                                progressDialog = null;
                            }
                        } else {
                            if (progressDialog != null) {
                                progressDialog.dismiss();
                                progressDialog = null;
                            }
                            Toast.makeText(DateTransmissionActivity.this, "上传数据失败", Toast.LENGTH_SHORT).show();

                        }
                    } catch (Exception e) {
                        //关闭加载页
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                            progressDialog = null;
                        }
                        Toast.makeText(DateTransmissionActivity.this, "提交失败", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                } else {
                    //关闭加载页
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                        progressDialog = null;
                    }
                    Toast.makeText(DateTransmissionActivity.this, "网络未响应，提交失败", Toast.LENGTH_SHORT).show();

                }
            }
        });
        cursor1.close();
    }

  /**
   * 重写返回键
   * */
    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.zoomin, R.anim.zoomout);

    }
}
