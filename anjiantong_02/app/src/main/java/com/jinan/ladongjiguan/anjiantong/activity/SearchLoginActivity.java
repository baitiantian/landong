package com.jinan.ladongjiguan.anjiantong.activity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.jinan.ladongjiguan.anjiantong.PublicClass.AssetsDatabaseManager;
import com.jinan.ladongjiguan.anjiantong.PublicClass.CustomProgressDialog;
import com.jinan.ladongjiguan.anjiantong.PublicClass.DialogNormalDialog;
import com.jinan.ladongjiguan.anjiantong.utils.SharedPreferencesUtil;
import com.jinan.ladongjiguan.anjiantong.utils.WebServiceUtils;
import com.jinan.ladongjiguan.anjiantong.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.ksoap2.serialization.SoapObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.security.MessageDigest;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchLoginActivity extends BaseActivity implements View.OnClickListener, View.OnTouchListener {

    @BindView(R.id.login_bt)
    Button loginBt;
    private String WEB_SERVER_URL;
    private CustomProgressDialog progressDialog = null;//加载页
    @Override
    protected void initView() {
        setContentView(R.layout.search_login_layout);
        ButterKnife.bind(this);
        if(SharedPreferencesUtil.getStringData(SearchLoginActivity.this,"IPString",null)==null){
//            SharedPreferencesUtil.saveStringData(SearchLoginActivity.this,"IPString","http://218.201.222.159:801/Index.asmx");
            SharedPreferencesUtil.saveStringData(SearchLoginActivity.this,"IPString","http://218.201.222.159:4003/Index.asmx");

        }


//        getDate();
//
    }

    @Override
    protected void init() {
        loginBt.setOnClickListener(this);
        loginBt.setOnTouchListener(this);
    }

    @Override
    public void onClick(View v) {
        WEB_SERVER_URL= SharedPreferencesUtil.getStringData(this,"IPString","");
        upDate();
    }
    /**
     * 硬件验证
     * */
    protected void getDate() {
        /**
         * 对比验证硬件
         * */

//
//        TelephonyManager telephonyManager=(TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
//        String Imei=telephonyManager.getDeviceId();

        String macAddress = null;
        String macAddressCode = null;
        WifiManager wifiMgr = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = (null == wifiMgr ? null : wifiMgr.getConnectionInfo());
        if (null != info) {
            macAddress = info.getMacAddress();
        }
        String ImeiCode = MD5("720" + macAddress + "ld2017");
        macAddressCode = MD5(ImeiCode + "ld720");
        try {
            File urlFile = new File("/sdcard/anjiantong.txt");
            InputStreamReader isr = new InputStreamReader(new FileInputStream(urlFile), "UTF-8");
            BufferedReader br = new BufferedReader(isr);
            String str = "";
            String mimeTypeLine = null;
//            Log.d("授权码", ImeiCode);
            while ((mimeTypeLine = br.readLine()) != null) {
                str = str + mimeTypeLine;
            }
//            Log.d("读取授权码", str);
            if (!macAddressCode.equals(str) && !str.equals("7FBC011C34085007D3075927A7BFB949")) {
                dialogE();
            } else if (str.equals("7FBC011C34085007D3075927A7BFB949")) {
                 macAddress = null;
                 macAddressCode = null;
                 wifiMgr = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                 info = (null == wifiMgr ? null : wifiMgr.getConnectionInfo());
                if (null != info) {
                    macAddress = info.getMacAddress();
                }
                macAddressCode = MD5("720"+macAddress+"ld2017");
                macAddressCode = MD5( macAddressCode+"ld720");
                String filePath = "/sdcard/";
                String fileName = "anjiantong.txt";
                writeTxtToFile(macAddressCode, filePath, fileName);
            }


        }catch(Exception e){
            Log.e("授权获取失败", e.toString());
            Toast.makeText(SearchLoginActivity.this,"授权获取失败", Toast.LENGTH_SHORT).show();
            dialogE();

        }

    }
    /**
     * 提醒非法硬件
     * */
    protected void dialogE(){

        String macAddress = null;
        String macAddressCode = null;
        WifiManager wifiMgr = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = (null == wifiMgr ? null : wifiMgr.getConnectionInfo());
        if (null != info) {
            macAddress = info.getMacAddress();
        }
        macAddressCode = MD5("720"+macAddress+"ld2017");
        String filePath = "/sdcard/";
        String fileName = "Landong.txt";



        //生成文件夹之后，再生成文件，不然会出错
        makeFilePath(filePath, fileName);

        String strFilePath = filePath+fileName;
        // 每次写入时，都换行写
        String strContent = macAddressCode + "\r\n";
        try {
            File file = new File(strFilePath);
            if(file.exists()){
                file.delete();
            }
            if (!file.exists()) {
//                Log.d("TestFile", "Create the file:" + strFilePath);
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            RandomAccessFile raf = new RandomAccessFile(file, "rwd");
            raf.seek(file.length());
            raf.write(strContent.getBytes());
            raf.close();
        } catch (Exception e) {
            Log.e("TestFile", "Error on write File:" + e);
        }
        final DialogNormalDialog dialog = new DialogNormalDialog(this);
        final EditText editText = (EditText) dialog.getEditText();//方法在CustomDialog中实现
        TelephonyManager telephonyManager=(TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        String getCode ;
         macAddress = null;
         wifiMgr = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
         info = (null == wifiMgr ? null : wifiMgr.getConnectionInfo());
        if (null != info) {
            macAddress = info.getMacAddress();
        }
        getCode = MD5("720"+macAddress+"ld2017");
        dialog.setMessage("【"+getCode+"】"+"该终端未授权或注册码错误，请联系供应商输入注册码！");
        dialog.setOnPositiveListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                setResult(1,intent);
                finish();
                dialog.dismiss();
            }
        });
        dialog.setOnNegativeListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editText.getText().length()>0){
                    initData(editText.getText().toString());
                    dialog.dismiss();
                }
            }
        });
        dialog.setCancelable(false);
        dialog.show();
    }
    private void initData(String s) {
        String filePath = "/sdcard/";
        String fileName = "anjiantong.txt";

        writeTxtToFile(s, filePath, fileName);
    }

    // 生成文件
    public File makeFilePath(String filePath, String fileName) {
        File file = null;
        makeRootDirectory(filePath);
        try {
            file = new File(filePath + fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    // 生成文件夹
    public static void makeRootDirectory(String filePath) {
        File file = null;
        try {
            file = new File(filePath);
            if (!file.exists()) {
                file.mkdir();
            }
        } catch (Exception e) {
            Log.i("error:", e+"");
        }
    }
    // 将字符串写入到文本文件中
    public void writeTxtToFile(String strcontent, String filePath, String fileName) {
        //生成文件夹之后，再生成文件，不然会出错
        makeFilePath(filePath, fileName);

        String strFilePath = filePath+fileName;
        // 每次写入时，都换行写
        String strContent = strcontent + "\r\n";
        try {
            File file = new File(strFilePath);
            if(file.exists()){
                file.delete();
            }
            if (!file.exists()) {
//                Log.d("TestFile", "Create the file:" + strFilePath);
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            RandomAccessFile raf = new RandomAccessFile(file, "rwd");
            raf.seek(file.length());
            raf.write(strContent.getBytes());
            raf.close();
            /**
             * 欢迎页面
             * */
            Intent intent = new Intent();
            intent.setClass(this, WelcomeActivity.class);
            startActivityForResult(intent,0);
        } catch (Exception e) {
            Log.e("TestFile", "Error on write File:" + e);
            /**
             * 欢迎页面
             * */
            Intent intent = new Intent();
            intent.setClass(this, WelcomeActivity.class);
            startActivityForResult(intent,0);
        }
    }
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }
    /**
     * 更新数据库
     * */
    protected void upDate(){
        //加载页添加
        if (progressDialog == null) {
            progressDialog = CustomProgressDialog.createDialog(this);
        }
        progressDialog.show();
        upDateBusiness();
//        upDateCoalBusiness();


    }
    /**
     * 更新企业信息
     * */
    protected void upDateBusiness(){
        // 初始化，只需要调用一次
        AssetsDatabaseManager.initManager(getApplication());
        // 获取管理对象，因为数据库需要通过管理对象才能够获取
        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
        // 通过管理对象获取数据库
        final SQLiteDatabase db = mg.getDatabase("users.db");
        Cursor c = db.rawQuery("SELECT* FROM ELL_Business", null);
        int ModifyIndex = 0;
        while (c.moveToNext()){
//                Log.d("更新标数据",ModifyIndex+"");
            try {
                if(ModifyIndex < Integer.parseInt(c.getString(c.getColumnIndex("ModifyIndex")))){
                    ModifyIndex = Integer.parseInt(c.getString(c.getColumnIndex("ModifyIndex")));
                }
            }catch (Exception e){
                Log.e("获取更新标数据库报错",e.toString());
            }


        }

        c.close();
        //添加参数
        HashMap<String, String> properties = new HashMap<>();
        properties.put("Xml", "<Request><data  code='update-Business-allEX'><no><ModifyIndex>" +
                ModifyIndex+"</ModifyIndex></no></data></Request>");
        properties.put("Token", "");
        WebServiceUtils.callWebService(WEB_SERVER_URL, "DynamicInvoke", properties, new WebServiceUtils.WebServiceCallBack() {

            //WebService接口返回的数据回调到这个方法中
            @Override
            public void callBack(SoapObject result) {
                if(result != null){
//                    Log.d("非煤企业返回数据",result.toString());
                    try {

                        Object detail = result.getProperty("DynamicInvokeResult");
                        JSONObject jsonObj;
                        jsonObj = XML.toJSONObject(detail.toString());
                        JSONArray array;
                        JSONObject obj;

//                                        Log.d("秘钥",jsonObj.getJSONObject("DocumentElement").getJSONObject("Table").getString("secretkey"));
                        if(jsonObj.has("DocumentElement")&&jsonObj.toString().substring(jsonObj.toString().length()-3,jsonObj.toString().length()-2).equals("}")){
                            obj = jsonObj.getJSONObject("DocumentElement").getJSONObject("Table");

//                            db.execSQL("REPLACE INTO ELL_Business VALUES('"+
//                                    obj.getString("businessid") +"','"+
//                                    obj.getString("businessname") +"','"+
//                                    obj.getString("businesstype") +"','"+
//                                    obj.getString("legalperson") +"','"+
//                                    obj.getString("legalperson") +"','"+
//                                    obj.getString("legalpersonphone") +"','"+
//                                    obj.getString("registrationnumber") +"','"+
//                                    obj.getString("orgcode") +"','"+
//                                    obj.getString("safetyofficer") +"','"+
//                                    obj.getString("safetyofficerphone") +"','"+
//                                    obj.getString("address") +"','"+
//                                    obj.getString("addtime") +"','"+
//                                    "1" +"','"+
//                                    obj.getString("modifyindex") +"','"+
//                                    obj.getString("validflag")+"')");
                            ContentValues values  = new ContentValues();
                            values.put("BusinessId",obj.getString("businessid"));
                            values.put("BusinessName",obj.getString("businessname"));
                            values.put("BusinessType",obj.getString("businesstype"));
                            values.put("LegalPerson",obj.getString("legalperson"));
                            values.put("LegalPersonPost",obj.getString("legalperson"));
                            values.put("LegalPersonPhone",obj.getString("legalpersonphone"));
//                            values.put("RegistrationNumber",obj.getString("registrationnumber"));
                            values.put("SafetyOfficer",obj.getString("safetyofficer"));
                            values.put("SafetyOfficerPhone",obj.getString("safetyofficerphone"));
//                            Log.d("企业地址数据",obj.getString("address"));
                            values.put("Address",obj.getString("address"));
                            values.put("Emphases","1");
                            values.put("ModifyIndex",obj.getString("modifyindex"));
                            values.put("ValidFlag",obj.getString("validflag"));
                            values.put("ModifyIndex2","0");
                            values.put("OrgCode",obj.getString("orgcode"));
                            values.put("UserDefined","0");
                            db.replace("ELL_Business",null,values);
                        }else if(jsonObj.has("DocumentElement")&&jsonObj.toString().substring(jsonObj.toString().length()-3,jsonObj.toString().length()-2).equals("]")){
                            array =  jsonObj.getJSONObject("DocumentElement").getJSONArray("Table");
                            for (int i=0;i<array.length();i++){
                                obj = array.getJSONObject(i);
//                                db.execSQL("REPLACE INTO ELL_Business VALUES('"+
//                                        obj.getString("businessid") +"','"+
//                                        obj.getString("businessname") +"','"+
//                                        obj.getString("businesstype") +"','"+
//                                        obj.getString("legalperson") +"','"+
//                                        obj.getString("legalperson") +"','"+
//                                        obj.getString("legalpersonphone") +"','"+
//                                        obj.getString("registrationnumber") +"','"+
//                                        obj.getString("orgcode") +"','"+
//                                        obj.getString("safetyofficer") +"','"+
//                                        obj.getString("safetyofficerphone") +"','"+
//                                        obj.getString("address") +"','"+
//                                        obj.getString("addtime") +"','"+
//                                        "1" +"','"+
//                                        obj.getString("modifyindex") +"','"+
//                                        obj.getString("validflag")+"')");
                                ContentValues values  = new ContentValues();
                                values.put("BusinessId",obj.getString("businessid"));
                                values.put("BusinessName",obj.getString("businessname"));
                                values.put("BusinessType",obj.getString("businesstype"));
                                values.put("LegalPerson",obj.getString("legalperson"));
                                values.put("LegalPersonPost",obj.getString("legalperson"));
                                values.put("LegalPersonPhone",obj.getString("legalpersonphone"));
//                                values.put("RegistrationNumber",obj.getString("registrationnumber"));
                                values.put("SafetyOfficer",obj.getString("safetyofficer"));
                                values.put("SafetyOfficerPhone",obj.getString("safetyofficerphone"));
//                                Log.d("企业地址数据",obj.getString("address"));
                                values.put("Address",obj.getString("address"));
                                values.put("Emphases","1");
                                values.put("ModifyIndex",obj.getString("modifyindex"));
                                values.put("ModifyIndex2","0");
                                values.put("ValidFlag",obj.getString("validflag"));
                                values.put("OrgCode",obj.getString("orgcode"));
                                values.put("UserDefined","0");
                                db.replace("ELL_Business",null,values);
                            }


                        }
//                        for (int i =0;i<10;i++){

                        upDateCoalBusiness();
//                        }
//                        upDateDepartment();
                    } catch (JSONException e) {
                        //关闭加载页
                        if (progressDialog != null){
                            progressDialog.dismiss();
                            progressDialog = null;
                        }
                        Toast.makeText(SearchLoginActivity.this,"提交失败,数据错误缺失", Toast.LENGTH_SHORT).show();
                        Log.e("JSON exception", e.getMessage());
                        e.printStackTrace();
                    }

                }else {
                    //关闭加载页
                    if (progressDialog != null){
                        progressDialog.dismiss();
                        progressDialog = null;
                    }
                    Toast.makeText(SearchLoginActivity.this,"网络未响应，提交失败", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }
    /**
     * 更新煤矿企业信息
     * */
    protected void upDateCoalBusiness(){
        // 初始化，只需要调用一次
        AssetsDatabaseManager.initManager(getApplication());
        // 获取管理对象，因为数据库需要通过管理对象才能够获取
        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
        // 通过管理对象获取数据库
        final SQLiteDatabase db = mg.getDatabase("users.db");
        Cursor c = db.rawQuery("SELECT* FROM ELL_Business", null);
        int ModifyIndex = 0;
        while (c.moveToNext()){
//                Log.d("更新标数据",ModifyIndex+"");
            try {
                if(ModifyIndex < Integer.parseInt(c.getString(c.getColumnIndex("ModifyIndex2")))){
                    ModifyIndex = Integer.parseInt(c.getString(c.getColumnIndex("ModifyIndex2")));
                }
            }catch (Exception e){
                Log.e("煤矿获取更新标数据库报错",e.toString());
            }


        }

        c.close();
        //添加参数
        HashMap<String, String> properties = new HashMap<>();
        properties.put("Xml", "<Request><data  code='select-CoalBusinessEX'><no><ModifyIndex>" +
                ModifyIndex + "</ModifyIndex></no></data></Request>");
        properties.put("Token", "");
//        Log.d("煤矿发送数据",properties.toString());
        WebServiceUtils.callWebService(WEB_SERVER_URL, "DynamicInvoke", properties, new WebServiceUtils.WebServiceCallBack() {

            //WebService接口返回的数据回调到这个方法中
            @Override
            public void callBack(SoapObject result) {
                if(result != null){
//                    Log.d("煤矿返回数据",result.toString());
                    try {

                        Object detail = result.getProperty("DynamicInvokeResult");
                        JSONObject jsonObj;
                        jsonObj = XML.toJSONObject(detail.toString());
                        JSONArray array;
                        JSONObject obj;

//                                        Log.d("秘钥",jsonObj.getJSONObject("DocumentElement").getJSONObject("Table").getString("secretkey"));
                        if(jsonObj.has("DocumentElement")&&jsonObj.toString().substring(jsonObj.toString().length()-3,jsonObj.toString().length()-2).equals("}")){
                            obj = jsonObj.getJSONObject("DocumentElement").getJSONObject("Table");

//                            db.execSQL("REPLACE INTO ELL_Business VALUES('"+
//                                    obj.getString("businessid") +"','"+
//                                    obj.getString("businessname") +"','"+
//                                    obj.getString("businesstype") +"','"+
//                                    obj.getString("legalperson") +"','"+
//                                    obj.getString("legalperson") +"','"+
//                                    obj.getString("legalpersonphone") +"','"+
//                                    "" +"','"+
//                                    "" +"','"+
//                                    obj.getString("respperson") +"','"+
//                                    obj.getString("resppersonphone") +"','"+
//                                    obj.getString("address") +"','"+
//                                    "2017-6-1" +"','"+
//                                    "1" +"','"+
//                                    "0" +"','"+
//                                    "0"+"')");
                            ContentValues values  = new ContentValues();
                            values.put("BusinessId",obj.getString("businessid"));
                            values.put("BusinessName",obj.getString("businessname"));
                            values.put("BusinessType",obj.getString("businesstype"));
                            values.put("LegalPerson",obj.getString("legalperson"));
                            values.put("LegalPersonPost",obj.getString("legalperson"));
                            values.put("LegalPersonPhone",obj.getString("legalpersonphone"));
                            values.put("RegistrationNumber",obj.getString("businesslicense"));
                            values.put("SafetyOfficer",obj.getString("respperson"));
                            values.put("SafetyOfficerPhone",obj.getString("resppersonphone"));
                            if(obj.getString("address").equals("{\"xml:space\":\"preserve\"}")){
                                values.put("Address","");
                            }else {
                                values.put("Address",obj.getString("address"));
                            }
                            values.put("Emphases","1");
                            values.put("ModifyIndex","0");
                            values.put("ModifyIndex2",obj.getString("modifyindex"));
                            values.put("ValidFlag","0");
                            values.put("UserDefined","0");
                            values.put("MineShaftCond",obj.getString("mineshaftcond"));
                            values.put("MineShaftType",obj.getString("mineshafttype"));
                            values.put("GeologicalReserves",obj.getString("geologicalreserves"));
                            values.put("WorkableReserves",obj.getString("workablereserves"));
                            values.put("DesignProdCapacity",obj.getString("designprodcapacity"));
                            values.put("CheckProdCapacity",obj.getString("checkprodcapacity"));
                            values.put("Area",obj.getString("area"));
                            values.put("Wthdraw",obj.getString("wthdraw"));
                            values.put("CoalType",obj.getString("coaltype"));
                            values.put("WorkableSeam",obj.getString("workableseam"));
                            values.put("ExploreCraft",obj.getString("explorecraft"));
                            values.put("ContractPhone",obj.getString("contractphone"));
                            values.put("ExploreMode",obj.getString("exploremode"));
                            values.put("RiskAppraisal",obj.getString("riskappraisal"));
                            values.put("GasLevel",obj.getString("gaslevel"));
                            values.put("CoalSeamLevel",obj.getString("coalseamlevel"));
                            values.put("Explosion",obj.getString("explosion"));
                            values.put("VentilateMode",obj.getString("ventilatemode"));
                            db.replace("ELL_Business",null,values);
                        }else if(jsonObj.has("DocumentElement")&&jsonObj.toString().substring(jsonObj.toString().length()-3,jsonObj.toString().length()-2).equals("]")){
                            array =  jsonObj.getJSONObject("DocumentElement").getJSONArray("Table");
                            for (int i=0;i<array.length();i++){
                                obj = array.getJSONObject(i);
//                                db.execSQL("REPLACE INTO ELL_Business VALUES('"+
//                                        obj.getString("businessid") +"','"+
//                                        obj.getString("businessname") +"','"+
//                                        obj.getString("businesstype") +"','"+
//                                        obj.getString("legalperson") +"','"+
//                                        obj.getString("legalperson") +"','"+
//                                        obj.getString("legalpersonphone") +"','"+
//                                       "" +"','"+
//                                        "" +"','"+
//                                        obj.getString("respperson") +"','"+
//                                        obj.getString("resppersonphone") +"','"+
//                                        obj.getString("address") +"','"+
//                                        "2017-6-1" +"','"+
//                                        "1" +"','"+
//                                        "0" +"','"+
//                                        "0"+"')");
                                ContentValues values  = new ContentValues();
                                values.put("BusinessId",obj.getString("businessid"));
                                values.put("BusinessName",obj.getString("businessname"));
                                values.put("BusinessType",obj.getString("businesstype"));
                                values.put("LegalPerson",obj.getString("legalperson"));
                                values.put("LegalPersonPost",obj.getString("legalperson"));
                                values.put("LegalPersonPhone",obj.getString("legalpersonphone"));
                                values.put("RegistrationNumber",obj.getString("businesslicense"));
                                values.put("SafetyOfficer",obj.getString("respperson"));
                                values.put("SafetyOfficerPhone",obj.getString("resppersonphone"));
                                if(obj.getString("address").equals("{\"xml:space\":\"preserve\"}")){
                                    values.put("Address","");
                                }else {
                                    values.put("Address",obj.getString("address"));
                                }

                                values.put("Emphases","1");
                                values.put("ModifyIndex","0");
                                values.put("ModifyIndex2",obj.getString("modifyindex"));
                                values.put("ValidFlag","0");
                                values.put("UserDefined","0");
                                values.put("MineShaftCond",obj.getString("mineshaftcond"));
                                values.put("MineShaftType",obj.getString("mineshafttype"));
                                values.put("GeologicalReserves",obj.getString("geologicalreserves"));
                                values.put("WorkableReserves",obj.getString("workablereserves"));
                                values.put("DesignProdCapacity",obj.getString("designprodcapacity"));
                                values.put("CheckProdCapacity",obj.getString("checkprodcapacity"));
                                values.put("Area",obj.getString("area"));
                                values.put("Wthdraw",obj.getString("wthdraw"));
                                values.put("CoalType",obj.getString("coaltype"));
                                values.put("WorkableSeam",obj.getString("workableseam"));
                                values.put("ExploreCraft",obj.getString("explorecraft"));
                                values.put("ContractPhone",obj.getString("contractphone"));
                                values.put("ExploreMode",obj.getString("exploremode"));
                                values.put("RiskAppraisal",obj.getString("riskappraisal"));
                                values.put("GasLevel",obj.getString("gaslevel"));
                                values.put("CoalSeamLevel",obj.getString("coalseamlevel"));
                                values.put("Explosion",obj.getString("explosion"));
                                values.put("VentilateMode",obj.getString("ventilatemode"));
                                db.replace("ELL_Business",null,values);
                            }

                        }
                        upDateCompany();
//                        upDateDepartment();
                    } catch (JSONException e) {
                        //关闭加载页
                        if (progressDialog != null){
                            progressDialog.dismiss();
                            progressDialog = null;
                        }
                        Toast.makeText(SearchLoginActivity.this,"提交失败,数据错误缺失", Toast.LENGTH_SHORT).show();
                        Log.e("JSON exception", e.getMessage());
                        e.printStackTrace();
                    }

                }else {
                    //关闭加载页
                    if (progressDialog != null){
                        progressDialog.dismiss();
                        progressDialog = null;
                    }
                    Toast.makeText(SearchLoginActivity.this,"网络未响应，提交失败", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }
    /**
     * 更新机构信息
     * */
    protected void upDateCompany(){

        //添加参数
        HashMap<String, String> properties = new HashMap<>();
        properties.put("Xml", "<Request><data  code='select-Company'><no></no></data></Request>");
        properties.put("Token", "");
        WebServiceUtils.callWebService(WEB_SERVER_URL, "DynamicInvoke", properties, new WebServiceUtils.WebServiceCallBack() {

            //WebService接口返回的数据回调到这个方法中
            @Override
            public void callBack(SoapObject result) {
                if(result != null){
//                    Log.d("登录返回数据",result.toString());
                    try {
//                                       Log.d("用户名发过去的结果",""+result);
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
//                                       Log.d("用户名发过去的结果",""+result);
//                                        Log.d("秘钥",jsonObj.getJSONObject("DocumentElement").getJSONObject("Table").getString("secretkey"));
                        if(jsonObj.has("DocumentElement")&&jsonObj.toString().substring(jsonObj.toString().length()-3,jsonObj.toString().length()-2).equals("}")){
                            obj = jsonObj.getJSONObject("DocumentElement").getJSONObject("Table");
                            db.execSQL("REPLACE INTO Base_Company VALUES('"+
                                    obj.getString("code") +"','"+
                                    obj.getString("companyid") +"','"+
                                    obj.getString("fullname")+"')");
//                            ContentValues values  = new ContentValues();

                        }else if(jsonObj.has("DocumentElement")&&jsonObj.toString().substring(jsonObj.toString().length()-3,jsonObj.toString().length()-2).equals("]")){
                            array =  jsonObj.getJSONObject("DocumentElement").getJSONArray("Table");
                            for (int i=0;i<array.length();i++){
                                obj = array.getJSONObject(i);
                                db.execSQL("REPLACE INTO Base_Company VALUES('"+
                                        obj.getString("code") +"','"+
                                        obj.getString("companyid") +"','"+
                                        obj.getString("fullname")+"')");
                            }

                        }

                        upDateDepartment();
                    } catch (JSONException e) {
                        //关闭加载页
                        if (progressDialog != null){
                            progressDialog.dismiss();
                            progressDialog = null;
                        }
                        Toast.makeText(SearchLoginActivity.this,"提交失败,数据错误缺失", Toast.LENGTH_SHORT).show();
                        Log.e("JSON exception", e.getMessage());
                        e.printStackTrace();
                    }

                }else {
                    //关闭加载页
                    if (progressDialog != null){
                        progressDialog.dismiss();
                        progressDialog = null;
                    }
                    Toast.makeText(SearchLoginActivity.this,"网络未响应，提交失败", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }
    /**
     * 更新部门信息
     * */
    protected void upDateDepartment(){
        //添加参数
        HashMap<String, String> properties = new HashMap<>();
        properties.put("Xml", "<Request><data  code='delect-Department'><no></no></data></Request>");
        properties.put("Token", "");
        WebServiceUtils.callWebService(WEB_SERVER_URL, "DynamicInvoke", properties, new WebServiceUtils.WebServiceCallBack() {

            //WebService接口返回的数据回调到这个方法中
            @Override
            public void callBack(SoapObject result) {
                if(result != null){
//                    Log.d("登录返回数据",result.toString());
                    try {
//                                       Log.d("用户名发过去的结果",""+result);
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
//                                       Log.d("用户名发过去的结果",""+result);
//                                        Log.d("秘钥",jsonObj.getJSONObject("DocumentElement").getJSONObject("Table").getString("secretkey"));
                        if(jsonObj.has("DocumentElement")&&jsonObj.toString().substring(jsonObj.toString().length()-3,jsonObj.toString().length()-2).equals("}")){
                            obj = jsonObj.getJSONObject("DocumentElement").getJSONObject("Table");
                            db.execSQL("REPLACE INTO Base_Department VALUES('"+
                                    obj.getString("code") +"','"+
                                    obj.getString("code").substring(0,6) +"','"+
                                    obj.getString("fullname")+"')");

                        }else if(jsonObj.has("DocumentElement")&&jsonObj.toString().substring(jsonObj.toString().length()-3,jsonObj.toString().length()-2).equals("]")){
                            array =  jsonObj.getJSONObject("DocumentElement").getJSONArray("Table");
                            for (int i=0;i<array.length();i++){
                                obj = array.getJSONObject(i);
                                db.execSQL("REPLACE INTO Base_Department VALUES('"+
                                        obj.getString("code") +"','"+
                                        obj.getString("code").substring(0,6) +"','"+
                                        obj.getString("fullname")+"')");

                            }

                        }
                        upDateUser();
//                        Intent intent = new Intent();
//                        intent.putExtra("Account",userId.getText().toString());
//                        setResult(0,intent);
//                        finish();
//                        overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                    } catch (JSONException e) {
                        //关闭加载页
                        if (progressDialog != null){
                            progressDialog.dismiss();
                            progressDialog = null;
                        }
                        Toast.makeText(SearchLoginActivity.this,"提交失败,数据错误缺失", Toast.LENGTH_SHORT).show();
                        Log.e("JSON exception", e.getMessage());
                        e.printStackTrace();
                    }

                }else {
                    //关闭加载页
                    if (progressDialog != null){
                        progressDialog.dismiss();
                        progressDialog = null;
                    }
                    Toast.makeText(SearchLoginActivity.this,"网络未响应，提交失败", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    /**
     * 更新用户信息
     * */
    protected void upDateUser(){
        // 初始化，只需要调用一次
        AssetsDatabaseManager.initManager(getApplication());
        // 获取管理对象，因为数据库需要通过管理对象才能够获取
        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
        // 通过管理对象获取数据库
        final SQLiteDatabase db = mg.getDatabase("users.db");
        Cursor c = db.rawQuery("SELECT* FROM Base_User ", null);
        int ModifyIndex = 0;
        while (c.moveToNext()){
//                Log.d("更新标数据",ModifyIndex+"");
            try {
                if(ModifyIndex < Integer.parseInt(c.getString(c.getColumnIndex("ModifyIndex")))){
                    ModifyIndex = Integer.parseInt(c.getString(c.getColumnIndex("ModifyIndex")));
                }
            }catch (Exception e){
                Log.e("用户获取更新标数据库报错",e.toString());
            }


        }
        c.close();
        //添加参数
        HashMap<String, String> properties = new HashMap<>();
        properties.put("Xml", "<Request><data  code='update-User'><no><ModifyIndex>" +
                ModifyIndex+"</ModifyIndex></no></data></Request>");
        properties.put("Token", "");
//        Log.d("用户发送数据",properties.toString());
        WebServiceUtils.callWebService(WEB_SERVER_URL, "DynamicInvoke", properties, new WebServiceUtils.WebServiceCallBack() {

            //WebService接口返回的数据回调到这个方法中
            @Override
            public void callBack(SoapObject result) {
                if(result != null){
//                    Log.d("用户信息返回数据",result.toString());
                    try {

                        Object detail = result.getProperty("DynamicInvokeResult");
                        JSONObject jsonObj;
                        jsonObj = XML.toJSONObject(detail.toString());
                        JSONArray array;
                        JSONObject obj;
                        String authstartdate = "";
                        String authenddate = "";
                        String workingdate = "";
                        String age = "0";
                        String education = "";
                        String idcard = "";
                        String departmentid = "";
                        String gender = "男";
                        String mobile = "";
                        String workingyear = "0";
                        String maritalstatus = "";
                        String physiclalstatus = "";
//                                        Log.d("秘钥",jsonObj.getJSONObject("DocumentElement").getJSONObject("Table").getString("secretkey"));
                        if(jsonObj.has("DocumentElement")&&jsonObj.toString().substring(jsonObj.toString().length()-3,jsonObj.toString().length()-2).equals("}")){
                            obj = jsonObj.getJSONObject("DocumentElement").getJSONObject("Table");
                            if(obj.has("authstartdate")){
                                authstartdate = obj.getString("authstartdate");
                            }
                            if(obj.has("authenddate")){
                                authenddate = obj.getString("authenddate");
                            }
                            if(obj.has("workingdate")){
                                workingdate = obj.getString("workingdate");
                            }
                            if(obj.has("age")){
                                age = obj.getString("age");
                            }
                            if(obj.has("education")){
                                education = obj.getString("education");
                            }
                            if(obj.has("idcard")){
                                idcard = obj.getString("idcard");
                            }
                            if(obj.has("departmentid")){
                                departmentid = obj.getString("departmentid");
                            }
                            if(obj.has("gender")){
                                gender = obj.getString("gender");
                            }
                            if (obj.has("mobile")) {
                                mobile = obj.getString("mobile");
                            }
                            if (obj.has("workingyear")){
                                workingyear = obj.getString("workingyear");
                            }
                            if(obj.has("maritalstatus")){
                                maritalstatus = obj.getString("maritalstatus");
                            }
                            if(obj.has("physiclalstatus")){
                                physiclalstatus = obj.getString("physiclalstatus");
                            }
//                            db.execSQL("REPLACE INTO Base_User VALUES('"+
//                                    obj.getString("userid") +"','"+
//                                    obj.getString("companyid") +"','"+
//                                    obj.getString("departmentid") +"','"+
//                                    obj.getString("code") +"','"+
//                                    obj.getString("password") +"','"+
//                                    obj.getString("realname") +"','"+
//                                    obj.getString("gender") +"','"+
//                                    obj.getString("mobile") +"','"+
//                                    authstartdate +"','"+
//                                    authenddate +"','"+
//                                    obj.getString("modifyindex") +"','"+
//                                    obj.getString("validflag") +"','"+
//                                    obj.getString("idcard") +"','"+
//                                    obj.getString("age") +"','"+
//                                    obj.getString("education") +"','"+
//                                    obj.getString("workingyear") +"','"+
//                                    workingdate +"','"+
//                                    obj.getString("maritalstatus") +"','"+
//                                    obj.getString("physiclalstatus")+"')");
                            ContentValues values  = new ContentValues();
                            values.put("UserId",obj.getString("userid"));
                            if(departmentid.length()>6){
                                values.put("CompanyId",departmentid.substring(0,6));
                            }else {
                                values.put("CompanyId",departmentid);
                            }
                            values.put("DepartmentId",departmentid);
                            values.put("Code",obj.getString("code"));
                            values.put("Password",obj.getString("password"));
                            values.put("RealName",obj.getString("realname"));
                            values.put("Gender",gender);
                            values.put("Mobile",mobile);
                            values.put("AuthStartDate",authstartdate);
                            values.put("AuthEndDate",authenddate);
                            values.put("ModifyIndex",obj.getString("modifyindex"));
                            values.put("ValidFlag",obj.getString("validflag"));
                            values.put("IDCard",idcard);
                            values.put("Age",age);
                            values.put("Education",education);
                            values.put("WorkingYear",workingyear);
                            values.put("WorkingDate",workingdate);
                            values.put("MaritalStatus",maritalstatus);
                            values.put("PhysiclalStatus",physiclalstatus);

                            db.replace("Base_User",null,values);
                        }else if(jsonObj.has("DocumentElement")&&jsonObj.toString().substring(jsonObj.toString().length()-3,jsonObj.toString().length()-2).equals("]")){
                            array =  jsonObj.getJSONObject("DocumentElement").getJSONArray("Table");
                            for (int i=0;i<array.length();i++){
                                obj = array.getJSONObject(i);
                                if(obj.has("authstartdate")){
                                    authstartdate = obj.getString("authstartdate");
                                }
                                if(obj.has("authenddate")){
                                    authenddate = obj.getString("authenddate");
                                }
                                if(obj.has("workingdate")){
                                    workingdate = obj.getString("workingdate");
                                }
                                if(obj.has("age")){
                                    age = obj.getString("age");
                                }
                                if(obj.has("education")){
                                    education = obj.getString("education");
                                }
                                if(obj.has("idcard")){
                                    idcard = obj.getString("idcard");
                                }
                                if(obj.has("departmentid")){
                                    departmentid = obj.getString("departmentid");
                                }
                                if(obj.has("gender")){
                                    gender = obj.getString("gender");
                                }
                                if (obj.has("mobile")) {
                                    mobile = obj.getString("mobile");
                                }
                                if (obj.has("workingyear")){
                                    workingyear = obj.getString("workingyear");
                                }
                                if(obj.has("maritalstatus")){
                                    maritalstatus = obj.getString("maritalstatus");
                                }
                                if(obj.has("physiclalstatus")){
                                    physiclalstatus = obj.getString("physiclalstatus");
                                }
//                                db.execSQL("REPLACE INTO Base_User VALUES('"+
//                                        obj.getString("userid") +"','"+
//                                        obj.getString("companyid") +"','"+
//                                        obj.getString("departmentid") +"','"+
//                                        obj.getString("code") +"','"+
//                                        obj.getString("password") +"','"+
//                                        obj.getString("realname") +"','"+
//                                        obj.getString("gender") +"','"+
//                                        obj.getString("mobile") +"','"+
//                                        authstartdate +"','"+
//                                        authenddate +"','"+
//                                        obj.getString("modifyindex") +"','"+
//                                        obj.getString("validflag") +"','"+
//                                        obj.getString("idcard") +"','"+
//                                        obj.getString("age") +"','"+
//                                        obj.getString("education") +"','"+
//                                        obj.getString("workingyear") +"','"+
//                                        workingdate +"','"+
//                                        obj.getString("maritalstatus") +"','"+
//                                        obj.getString("physiclalstatus")+"')");
                                ContentValues values  = new ContentValues();
                                values.put("UserId",obj.getString("userid"));
                                if(departmentid.length()>6){
                                    values.put("CompanyId",departmentid.substring(0,6));
                                }else {
                                    values.put("CompanyId",departmentid);
                                }

                                values.put("DepartmentId",departmentid);
                                values.put("Code",obj.getString("code"));
                                values.put("Password",obj.getString("password"));
                                values.put("RealName",obj.getString("realname"));
                                values.put("Gender",gender);
                                values.put("Mobile",mobile);
                                values.put("AuthStartDate",authstartdate);
                                values.put("AuthEndDate",authenddate);
                                values.put("ModifyIndex",obj.getString("modifyindex"));
                                values.put("ValidFlag",obj.getString("validflag"));
                                values.put("IDCard",idcard);
                                values.put("Age",age);
                                values.put("Education",education);
                                values.put("WorkingYear",workingyear);
                                values.put("WorkingDate",workingdate);
                                values.put("MaritalStatus",maritalstatus);
                                values.put("PhysiclalStatus",physiclalstatus);

                                db.replace("Base_User",null,values);
                            }

                        }

                        Intent intent = new Intent();
                        intent.setClass(SearchLoginActivity.this,CountMainActivity.class);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                    } catch (JSONException e) {
                        //关闭加载页
                        if (progressDialog != null){
                            progressDialog.dismiss();
                            progressDialog = null;
                        }
                        Toast.makeText(SearchLoginActivity.this,"提交失败,数据错误缺失", Toast.LENGTH_SHORT).show();
                        Log.e("JSON exception", e.getMessage());
                        e.printStackTrace();
                    }

                }else {
                    //关闭加载页
                    if (progressDialog != null){
                        progressDialog.dismiss();
                        progressDialog = null;
                    }
                    Toast.makeText(SearchLoginActivity.this,"网络未响应，提交失败", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    /**
     * 验证硬件
     * */
    public final static String MD5(String s) {
        char hexDigits[]={'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
        try {
            byte[] btInput = s.getBytes();
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            mdInst.update(btInput);
            // 获得密文
            byte[] md = mdInst.digest();
            // 把密文转换成十六进制的字符串形式
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
