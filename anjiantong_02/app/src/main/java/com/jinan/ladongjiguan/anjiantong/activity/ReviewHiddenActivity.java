package com.jinan.ladongjiguan.anjiantong.activity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jinan.ladongjiguan.anjiantong.PublicClass.AssetsDatabaseManager;
import com.jinan.ladongjiguan.anjiantong.PublicClass.CustomProgressDialog;
import com.jinan.ladongjiguan.anjiantong.PublicClass.FTP;
import com.jinan.ladongjiguan.anjiantong.adapter.ProblemListAdapter;
import com.jinan.ladongjiguan.anjiantong.utils.SharedPreferencesUtil;
import com.jinan.ladongjiguan.anjiantong.PublicClass.StringOrDate;
import com.jinan.ladongjiguan.anjiantong.PublicClass.Utility;
import com.jinan.ladongjiguan.anjiantong.utils.WebServiceUtils;
import com.jinan.ladongjiguan.anjiantong.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.ksoap2.serialization.SoapObject;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ReviewHiddenActivity extends BaseActivity implements View.OnClickListener, View.OnTouchListener {


    @BindView(R.id.examine_page_back)
    LinearLayout examinePageBack;
    @BindView(R.id.title_layout)
    TextView titleLayout;
    @BindView(R.id.tx_table)
    TextView txTable;
    @BindView(R.id.list_02)
    ListView list02;
    @BindView(R.id.bt_add_check)
    Button btAddCheck;
    @BindView(R.id.l_add_check_up)
    LinearLayout lAddCheckUp;
    @BindView(R.id.bt_all)
    Button btAll;
    private String WEB_SERVER_URL;
    private CustomProgressDialog progressDialog = null;//加载页
    // 创建一个List集合，List集合的元素是Map
    private List<HashMap<String, Object>> listItems = new ArrayList<>();
    private String ReviewId = UUID.randomUUID().toString();//计划主键
    ArrayList<String> listStr = null;//被选中的隐患数据
    private Intent intent = new Intent();
    private  ProblemListAdapter listAdapter;
    private String TAG = ReviewHiddenActivity.class.getSimpleName();
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    Toast.makeText(ReviewHiddenActivity.this, "下载完成", Toast.LENGTH_SHORT).show();

                    break;
                case 1:
                    Toast.makeText(ReviewHiddenActivity.this, "下载失败", Toast.LENGTH_SHORT).show();

                    break;
                default:
                    break;
            }

        }

    };
    @Override
    protected void initView() {
        setContentView(R.layout.review_hidden_layout);
        ButterKnife.bind(this);
        WEB_SERVER_URL = SharedPreferencesUtil.getStringData(this, "IPString", "");
        titleLayout.setText("选择隐患");
    }

    @Override
    protected void init() {
        examinePageBack.setOnTouchListener(this);
        examinePageBack.setOnClickListener(this);
        btAddCheck.setOnClickListener(this);
        btAll.setOnClickListener(this);
        httpDate();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.examine_page_back://返回键
                onBackPressed();
                break;
            case R.id.bt_add_check://保存键
                setGroup();
                break;
            case R.id.bt_all://全选
                for(int i = 0;i<listItems.size();i++){
                    ProblemListAdapter.isSelected.put(i,true);
                    listAdapter.notifyDataSetChanged();
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
     * 从服务端获取隐患信息
     */
    protected void httpDate() {
        // 初始化，只需要调用一次
        AssetsDatabaseManager.initManager(getApplication());
        // 获取管理对象，因为数据库需要通过管理对象才能够获取
        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
        // 通过管理对象获取数据库
        final SQLiteDatabase db = mg.getDatabase("users.db");
        //加载页添加
        if (progressDialog == null) {
            progressDialog = CustomProgressDialog.createDialog(this);
        }
        progressDialog.show();
        HashMap<String, String> properties = new HashMap<>();
        properties.put("Xml", "<Request><data  code='select-GetDangerByDocumentid'><no><Documentid>" +
                getIntent().getStringExtra("DocumentId") + "</Documentid></no></data></Request>");
        properties.put("Token", "");
        Log.d("企业隐患数据", properties.toString());
        WebServiceUtils.callWebService(WEB_SERVER_URL, "DynamicInvoke", properties, new WebServiceUtils.WebServiceCallBack() {

            //WebService接口返回的数据回调到这个方法中
            @Override
            public void callBack(SoapObject result) {
                if (result != null) {
                    Log.d("企业隐患返回数据", result.toString());
                    try {
//                                       Log.d("用户名发过去的结果",""+result);
                        Object detail = result.getProperty("DynamicInvokeResult");
                        JSONObject jsonObj;
                        jsonObj = XML.toJSONObject(detail.toString());
//                                        Log.d("秘钥",jsonObj.getJSONObject("DocumentElement").getJSONObject("Table").getString("secretkey"));
                        if (jsonObj.has("DocumentElement")) {
                            JSONArray array;
                            JSONObject obj;
//                            Log.d("重名补全接收数据",jsonObj.toString());
                            listItems = new ArrayList<>();
                            if (jsonObj.has("DocumentElement") && jsonObj.toString().substring(jsonObj.toString().length() - 3, jsonObj.toString().length() - 2).equals("}")) {
                                obj = jsonObj.getJSONObject("DocumentElement").getJSONObject("Table");
                                HashMap<String, Object> listItem = new HashMap<>();
                                Cursor cursor2 = db.rawQuery("SELECT* FROM Base_User WHERE UserId = ?",
                                        new String[]{obj.getString("memberid")});
                                cursor2.moveToFirst();
//                                listItem.put("RealName", cursor2.getString(cursor2.getColumnIndex("RealName")));
                                listItem.put("RealName", "");
                                cursor2.close();
                                listItem.put("hiddendangerid", obj.getString("hiddendangerid"));
                                listItem.put("yhdd", obj.getString("yhdd"));
                                listItem.put("yhms", obj.getString("yhms"));
                                listItem.put("zgqx", obj.getString("zgqx").substring(0,10));
                                if(obj.has("zglx")){
                                    listItem.put("zglx", obj.getString("zglx"));
                                } else {
                                    listItem.put("zglx", "");
                                }
                                if(obj.has("yhzgqtp")){
                                    listItem.put("yhzgqtp", obj.getString("yhzgqtp"));
                                } else {
                                    listItem.put("yhzgqtp", "");
                                }
                                listItem.put("checkresult", obj.getString("checkresult"));
                                listItem.put("disposeresult", obj.getString("disposeresult"));
                                listItem.put("zglx1", "");
                                listItems.add(listItem);
                            } else if (jsonObj.has("DocumentElement") && jsonObj.toString().substring(jsonObj.toString().length() - 3, jsonObj.toString().length() - 2).equals("]")) {
                                array = jsonObj.getJSONObject("DocumentElement").getJSONArray("Table");
                                for (int i = 0; i < array.length(); i++) {
                                    obj = array.getJSONObject(i);
                                    HashMap<String, Object> listItem = new HashMap<>();
                                    Cursor cursor2 = db.rawQuery("SELECT* FROM Base_User WHERE UserId = ?",
                                            new String[]{obj.getString("memberid")});
                                    cursor2.moveToFirst();
//                                    listItem.put("RealName", cursor2.getString(cursor2.getColumnIndex("RealName")));
                                    listItem.put("RealName", "");
                                    cursor2.close();
                                    listItem.put("hiddendangerid", obj.getString("hiddendangerid"));
                                    listItem.put("yhdd", obj.getString("yhdd"));
                                    listItem.put("yhms", obj.getString("yhms"));
                                    listItem.put("zgqx", obj.getString("zgqx").substring(0,10));
                                    if(obj.has("zglx")){
                                        listItem.put("zglx", obj.getString("zglx"));
                                    } else {
                                        listItem.put("zglx", "");
                                    }
                                    if(obj.has("yhzgqtp")){
                                        listItem.put("yhzgqtp", obj.getString("yhzgqtp"));
                                    } else {
                                        listItem.put("yhzgqtp", "");
                                    }
                                    listItem.put("checkresult", obj.getString("checkresult"));
                                    listItem.put("disposeresult", obj.getString("disposeresult"));
                                    listItem.put("zglx1", "");
                                    listItems.add(listItem);
                                }


                            }
                             listAdapter = new ProblemListAdapter(ReviewHiddenActivity.this, listItems, R.layout.review_hidden_list_item,
                                    new String[]{"yhms", "checkresult", "disposeresult", "zgqx", "yhdd", "RealName", "zglx1"},
                                    new int[]{R.id.tx_problem_01, R.id.tx_problem_02, R.id.tx_problem_03,
                                            R.id.tx_problem_04, R.id.tx_problem_05, R.id.tx_problem_06, R.id.tx_problem_07});
                            list02.setAdapter(listAdapter);
                            Utility.setListViewHeightBasedOnChildren(list02);


                            //关闭加载页
                            if (progressDialog != null) {
                                progressDialog.dismiss();
                                progressDialog = null;
                            }
                        } else {
                            Toast.makeText(ReviewHiddenActivity.this, "没有数据", Toast.LENGTH_SHORT).show();
//关闭加载页
                            if (progressDialog != null) {
                                progressDialog.dismiss();
                                progressDialog = null;
                            }
                        }
                    } catch (JSONException e) {
                        //关闭加载页
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                            progressDialog = null;
                        }
                        Toast.makeText(ReviewHiddenActivity.this, "提交失败", Toast.LENGTH_SHORT).show();
                        Log.e("JSON exception", e.getMessage());
                        e.printStackTrace();
                    }
                } else {
                    //关闭加载页
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                        progressDialog = null;
                    }
                    Toast.makeText(ReviewHiddenActivity.this, "网络未响应，提交失败", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    /**
     * 保存复查计划表
     */
    protected void saveDate() {
        // 初始化，只需要调用一次
        AssetsDatabaseManager.initManager(getApplication());
        // 获取管理对象，因为数据库需要通过管理对象才能够获取
        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
        // 通过管理对象获取数据库
        SQLiteDatabase db = mg.getDatabase("users.db");
//        try{
        DateFormat df = new SimpleDateFormat(
                "yyyy-MM-dd", Locale.getDefault());
        String date = df.format(new Date());
        Date curDate = StringOrDate.stringToDate(date);//获取当前时间
        String userId = SharedPreferencesUtil.getStringData(this, "userId", "");
        String CompanyId = SharedPreferencesUtil.getStringData(this, "CompanyId", "");
        db.execSQL("REPLACE INTO ELL_ReviewInfo VALUES('" +
                ReviewId + "','" +
                getIntent().getStringExtra("DocumentId") + "','" +
                getIntent().getStringExtra("BusinessId") + "','" +
//                    getIntent().getStringExtra("StartTime") + "','" +
//                    getIntent().getStringExtra("EndTime") + "','" +
//                    getIntent().getStringExtra("Address")  + "','" +
                "-请选择-" + "','" +
                "-请选择-" + "','" +
                getIntent().getStringExtra("address") + "','" +
                CompanyId + "','" +
                userId + "','" +
                curDate + "','" +
                getIntent().getStringExtra("documentnumber") + "')");

//        Log.d("复查表数据", "REPLACE INTO ELL_ReviewInfo VALUES('" +
//                ReviewId + "','" +
//                getIntent().getStringExtra("DocumentId") + "','" +
//                getIntent().getStringExtra("BusinessId") + "','" +
////                    getIntent().getStringExtra("StartTime") + "','" +
////                    getIntent().getStringExtra("EndTime") + "','" +
////                    getIntent().getStringExtra("Address")  + "','" +
//                "" + "','" +
//                "" + "','" +
//                getIntent().getStringExtra("address") + "','" +
//                CompanyId + "','" +
//                userId + "','" +
//                curDate + "','" +
//                getIntent().getStringExtra("documentnumber") + "')");
        ReviewPlanDateActivity.reviewPlanDateActivity.finish();
//            ReviewBusinessListActivity.reviewBusinessListActivity.finish();
//        Toast.makeText(ReviewHiddenActivity.this, "已保存计划", Toast.LENGTH_SHORT).show();

        startActivity(intent);
        finish();
//        }catch (Exception e){
//            Log.e("数据库报错", e.toString());
//        }
    }

    /***
     * 隐患信息制定
     */
    protected void setGroup() {
        // 初始化，只需要调用一次
        AssetsDatabaseManager.initManager(getApplication());
        // 获取管理对象，因为数据库需要通过管理对象才能够获取
        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
        // 通过管理对象获取数据库
        SQLiteDatabase db = mg.getDatabase("users.db");
        String s = "";
        String s1 = "";
        String s2 = "";
        String s3 = "";
        String s4 = "";
        String s5 = "";
        listStr = new ArrayList<>();
        for (int i = 0; i < listItems.size(); i++) {
            if (ProblemListAdapter.isSelected.get(i)) {
                listStr.add(listItems.get(i).get("hiddendangerid").toString());
//                db.execSQL("REPLACE INTO ELL_HiddenDanger VALUES('" +
//                        listItems.get(i).get("hiddendangerid").toString() + "','" +
//                        ReviewId + "','" +
//                        listItems.get(i).get("yhdd").toString() + "','" +
//                        listItems.get(i).get("yhms").toString() + "','" +
//                        listItems.get(i).get("zgqx").toString() + "','" +
//                        listItems.get(i).get("zglx").toString() + "','" +
//                        listItems.get(i).get("checkresult").toString() + "','" +
//                        listItems.get(i).get("disposeresult").toString() + "','" +
//                        "" + "')");
                ContentValues values = new ContentValues();
                values.put("HiddenDangerId",  listItems.get(i).get("hiddendangerid").toString());
                values.put("ReviewId",ReviewId);
                values.put("yhdd", listItems.get(i).get("yhdd").toString());
                values.put("yhms",  listItems.get(i).get("yhms").toString());
                values.put("zgqx", listItems.get(i).get("zgqx").toString());
                values.put("zglx", listItems.get(i).get("zglx").toString());
                values.put("checkresult",listItems.get(i).get("checkresult").toString());
                values.put("disposeresult", listItems.get(i).get("disposeresult").toString());
                values.put("YHZGHTP", "");
                values.put("AddUsers", "");
                db.replace("ELL_HiddenDanger", null, values);

                String[] strings = listItems.get(i).get("yhzgqtp").toString().split(",");

                for (String string : strings) {
                    //下载图片
                    downPhoto(string, listItems.get(i).get("hiddendangerid").toString());
                }

//                Log.d("隐患存入数据库", "REPLACE INTO ELL_HiddenDanger VALUES('" +
//                        listItems.get(i).get("hiddendangerid").toString() + "','" +
//                        ReviewId + "','" +
//                        listItems.get(i).get("yhdd").toString() + "','" +
//                        listItems.get(i).get("yhms").toString() + "','" +
//                        listItems.get(i).get("zgqx").toString() + "','" +
//                        listItems.get(i).get("zglx").toString() + "','" +
//                        listItems.get(i).get("checkresult").toString() + "','" +
//                        listItems.get(i).get("disposeresult").toString() + "')");
                if (listStr.size() == 1) {
                    s = listItems.get(i).get("hiddendangerid").toString();
                    s1 = listItems.get(i).get("yhdd").toString();
                    s2 = listItems.get(i).get("yhms").toString();
                    s3 = listItems.get(i).get("zgqx").toString();
                    s4 = listItems.get(i).get("zglx").toString();
                    s5 = listItems.get(i).get("checkresult").toString();
                } else {
                    s = s + "," + listItems.get(i).get("hiddendangerid").toString();
                    s1 = s1 + "," + listItems.get(i).get("yhdd").toString();
                    s2 = s2 + "," + listItems.get(i).get("yhms").toString();
                    s3 = s3 + "," + listItems.get(i).get("zgqx").toString();
                    s4 = s4 + "," + listItems.get(i).get("zglx").toString();
                    s5 = s5 + "," + listItems.get(i).get("checkresult").toString();
                }

            }
        }
        if (s.length() > 0) {
            intent.putExtra("HiddenDangerId", s);
            intent.putExtra("yhdd", s1);
            intent.putExtra("yhms", s2);
            intent.putExtra("zgqx", s3);
            intent.putExtra("zglx", s4);
            intent.putExtra("checkresult", s5);
            intent.putExtra("ReviewId", ReviewId);
            intent.putExtra("documentnumber", getIntent().getStringExtra("documentnumber"));
            intent.setClass(ReviewHiddenActivity.this, ReviewModifyActivity.class);
            saveDate();
        } else {
            Toast.makeText(ReviewHiddenActivity.this, "请选择需要复查的隐患", Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * 下载整改前隐患图片
     * */
    private void downPhoto(final String s, final String id){


        File file = new File("/mnt/sdcard/Download/"+id+"/" + s);
        if (file.exists()) {
            file.delete();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {

                // 下载
                try {

                    //单文件下载
                    new FTP().downloadSingleFile( "/ftpdata/"+s,"/sdcard/Download/"+id+"/",s,new FTP.DownLoadProgressListener(){

                        @Override
                        public void onDownLoadProgress(String currentStep, long downProcess, File file) {
                            Log.d(TAG, currentStep);
                            if(currentStep.equals(MainActivity.FTP_DOWN_SUCCESS)){
                                Log.d(TAG, "-----xiazai--successful");
//                                handler.sendEmptyMessage(0);
                            } else if(currentStep.equals(MainActivity.FTP_DOWN_LOADING)){
                                Log.d(TAG, "-----xiazai---"+downProcess + "%");
                            }else if(currentStep.equals(MainActivity.FTP_DOWN_FAIL)||currentStep.equals(MainActivity.FTP_CONNECT_FAIL)){
//                                handler.sendEmptyMessage(1);
                            }
                        }

                    });

                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        }).start();
    }
}
