package com.jinan.ladongjiguan.anjiantong.activity;

import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.jinan.ladongjiguan.anjiantong.PublicClass.AssetsDatabaseManager;
import com.jinan.ladongjiguan.anjiantong.PublicClass.CustomProgressDialog;
import com.jinan.ladongjiguan.anjiantong.PublicClass.DoubleDatePickerDialog;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ReviewPlanDateActivity extends BaseActivity implements View.OnTouchListener, View.OnClickListener {


    @BindView(R.id.examine_page_back)
    LinearLayout examinePageBack;
    @BindView(R.id.title_layout)
    TextView titleLayout;
    @BindView(R.id.check_enterprise_01)
    TextView checkEnterprise01;
    @BindView(R.id.check_enterprise_02)
    TextView checkEnterprise02;
    @BindView(R.id.check_enterprise_03)
    TextView checkEnterprise03;
    @BindView(R.id.check_enterprise_04)
    TextView checkEnterprise04;
    @BindView(R.id.check_enterprise_05)
    TextView checkEnterprise05;
    @BindView(R.id.list_problem)
    ListView listProblem;
    @BindView(R.id.tx_list_table)
    LinearLayout txListTable;
    @BindView(R.id.bt_check_up_date_05)
    Button btCheckUpDate05;
    @BindView(R.id.l_bt_02)
    LinearLayout lBt02;
    @BindView(R.id.check_enterprise_06)
    EditText checkEnterprise06;
    @BindView(R.id.from_time)
    TextView fromTime;
    @BindView(R.id.end_time)
    TextView endTime;
    @BindView(R.id.l_search_time)
    LinearLayout lSearchTime;
    @BindView(R.id.e_car_num_6)
    Spinner eCarNum6;
    private String WEB_SERVER_URL;
    private CustomProgressDialog progressDialog = null;//加载页
    private String s_from_time;//开始时间
    private Date d_from_time;
    private String s_end_time;//结束时间
    private Date d_end_time;
    private String string = "";//下拉列表选择的
    private String BusinessId = "";//企业ID
    // 创建一个List集合，List集合的元素是Map
    private List<Map<String, Object>> listItems = new ArrayList<>();
    public static ReviewPlanDateActivity reviewPlanDateActivity;//用于保存计划时关闭

    @Override
    protected void initView() {
        setContentView(R.layout.review_plan_date_layout);
        ButterKnife.bind(this);
        titleLayout.setText("制定复查计划");
        WEB_SERVER_URL = SharedPreferencesUtil.getStringData(this, "IPString", "");
        reviewPlanDateActivity = this;

        /**
         * 下拉列表
         * */
        Resources res = getResources();
        final String[] strings01 = res.getStringArray(R.array.s_car_num_10);
        eCarNum6.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView v1 = (TextView) view;
//                v1.setTextColor(ContextCompat.getColor(ReviewBusinessListActivity.this, R.color.main_color_3));
                v1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                string = "";
                if(position != 0){
                    string = strings01[position];
                    httpDate(string,0);
                    BusinessId = "";
                    checkEnterprise01.setText("--点击选择--");
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        try{

            if(getIntent().getStringExtra("BusinessId").length()>0){
                checkEnterprise01.setText(getIntent().getStringExtra("name"));
                BusinessId = getIntent().getStringExtra("BusinessId");
                httpDate(BusinessId,1);
             }

        }catch (Exception e){
            Log.e("企业ID数据传输报错",e.toString(),e);
        }
    }

    @Override
    protected void init() {
        examinePageBack.setOnClickListener(this);
        examinePageBack.setOnTouchListener(this);
        btCheckUpDate05.setOnClickListener(this);
        lSearchTime.setOnClickListener(this);
        listProblem.setVisibility(View.GONE);
        checkEnterprise01.setOnClickListener(this);
        checkEnterprise01.setOnTouchListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.examine_page_back://返回键
                onBackPressed();
                break;
            case R.id.bt_check_up_date_05://取消键
                onBackPressed();
                break;
            case R.id.l_search_time://时间选择器
                Calendar c = Calendar.getInstance();
                // 最后一个false表示不显示日期，如果要显示日期，最后参数可以是true或者不用输入
                new DoubleDatePickerDialog(ReviewPlanDateActivity.this, 0, new DoubleDatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker startDatePicker, int startYear, int startMonthOfYear,
                                          int startDayOfMonth, DatePicker endDatePicker, int endYear, int endMonthOfYear,
                                          int endDayOfMonth) {
                        s_from_time = String.format("%d-%d-%d", startYear, startMonthOfYear + 1, startDayOfMonth);
                        d_from_time = StringOrDate.stringToDate(s_from_time);
                        fromTime.setText(s_from_time);
                        s_end_time = String.format("%d-%d-%d", endYear, endMonthOfYear + 1, endDayOfMonth);
                        d_end_time = StringOrDate.stringToDate(s_end_time);
                        endTime.setText(s_end_time);
                    }
                }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE), true).show();
                break;
            case R.id.check_enterprise_01://选择企业
                Intent intent = new Intent();
                intent.setClass(ReviewPlanDateActivity.this, EnterpriseInformationActivity.class);
                intent.putExtra("state", "check");
                startActivityForResult(intent, 0);
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

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
    }
    /**
     * 返回企业信息
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 0:
                if (resultCode == 0) {
                    checkEnterprise01.setText(data.getStringExtra("name"));
                    BusinessId = data.getStringExtra("BusinessId");
                    if(BusinessId.length()>0){

                        httpDate(BusinessId,1);
                    }
                    eCarNum6.setSelection(0);
                }
                break;

            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    /**
     * 获取企业信息
     */
    protected void httpDate(String s,int n) {
        // 初始化，只需要调用一次
        AssetsDatabaseManager.initManager(getApplication());
        // 获取管理对象，因为数据库需要通过管理对象才能够获取
        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
        // 通过管理对象获取数据库
        final SQLiteDatabase db = mg.getDatabase("users.db");
//        try {
//            Cursor c = db.rawQuery("SELECT* FROM ELL_Business WHERE BusinessId = ?",
////                    new String[]{getIntent().getStringExtra("businessid")});
//                    new String[]{"123123"});
//            c.moveToFirst();
//            checkEnterprise01.setText(c.getString(c.getColumnIndex("BusinessName")));
//            checkEnterprise02.setText(c.getString(c.getColumnIndex("Address")));
//            checkEnterprise03.setText(c.getString(c.getColumnIndex("LegalPerson")));
//            checkEnterprise04.setText(c.getString(c.getColumnIndex("LegalPersonPost")));
//            checkEnterprise05.setText(c.getString(c.getColumnIndex("LegalPersonPhone")));
//            c.close();
//        } catch (Exception e) {
//            Log.e("数据库数据报错", e.toString());
//        }

        //加载页添加
        if (progressDialog == null) {
            progressDialog = CustomProgressDialog.createDialog(this);
        }
        progressDialog.show();
        HashMap<String, String> properties = new HashMap<>();
        String[] strings = SharedPreferencesUtil.getStringData(this, "DepartmentId", null).split("-");
        if(n == 0){//企业类型查询

            properties.put("Xml", "<Request><data  code='GetBusinessByDanger'><no><BusinessType>" +
                    s + "</BusinessType><CompanyCode>"+
                    strings[0]+"</CompanyCode></no></data></Request>");
        }else if(n == 1){//企业id查询

            properties.put("Xml", "<Request><data  code='GetReviewInfoByBusinessID'><no><BusinessID>" +
                    s + "</BusinessID></no></data></Request>");
        }
        properties.put("Token", "");
        Log.d("企业隐患发出数据", properties.toString());
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
                            listProblem.setVisibility(View.VISIBLE);
//                            Log.d("重名补全接收数据",jsonObj.toString());
                            listItems = new ArrayList<>();
                            if (jsonObj.has("DocumentElement") && jsonObj.toString().substring(jsonObj.toString().length() - 3, jsonObj.toString().length() - 2).equals("}")) {
                                obj = jsonObj.getJSONObject("DocumentElement").getJSONObject("Table");
                                Map<String, Object> listItem = new HashMap<>();
                                try{
                                    Cursor c = db.rawQuery("SELECT* FROM ELL_Business WHERE BusinessId = ?",
                                            new String[]{obj.getString("businessid")});
                                    c.moveToFirst();
                                    String s1 = c.getString(c.getColumnIndex("BusinessName"));
                                    c.close();
                                    listItem.put("documentid", obj.getString("documentid"));
                                    listItem.put("documentnumber", obj.getString("documentnumber"));
                                    listItem.put("documenttype", obj.getString("documenttype"));
                                    listItem.put("businessid", obj.getString("businessid"));
                                    listItem.put("zgqx", obj.getString("zgqx"));
                                    listItem.put("address","");
                                    if(obj.has("address")){

                                        listItem.put("address", obj.getString("address"));
                                    }
                                    if(obj.has("yhzgqtp")){
                                        listItem.put("yhzgqtp", obj.getString("yhzgqtp"));
                                    }
                                    listItem.put("BusinessName", s1);
                                    if(BusinessId.length()==0){

                                        listItems.add(listItem);
                                    }else if(BusinessId.equals(obj.getString("businessid"))){
                                        listItems.add(listItem);
                                    }
                                }catch (Exception e){
                                    Log.e("数据库出错",e.toString());
                                }
                            } else if (jsonObj.has("DocumentElement") && jsonObj.toString().substring(jsonObj.toString().length() - 3, jsonObj.toString().length() - 2).equals("]")) {
                                array = jsonObj.getJSONObject("DocumentElement").getJSONArray("Table");
                                for (int i = 0; i < array.length(); i++) {
                                    obj = array.getJSONObject(i);
                                    Map<String, Object> listItem = new HashMap<>();
                                    try{
                                        Cursor c = db.rawQuery("SELECT* FROM ELL_Business WHERE BusinessId = ?",
                                                new String[]{obj.getString("businessid")});
                                        c.moveToFirst();
                                        String s1 = c.getString(c.getColumnIndex("BusinessName"));
                                        c.close();
                                        listItem.put("documentid", obj.getString("documentid"));
                                        listItem.put("documentnumber", obj.getString("documentnumber"));
                                        listItem.put("documenttype", obj.getString("documenttype"));
                                        listItem.put("businessid", obj.getString("businessid"));
                                        listItem.put("zgqx", obj.getString("zgqx"));
                                        listItem.put("address","");
                                        if(obj.has("address")){

                                            listItem.put("address", obj.getString("address"));
                                        }
//                                        if(obj.has("yhzgqtp")){
//                                            listItem.put("yhzgqtp", obj.getString("yhzgqtp"));
//                                        }
                                        listItem.put("BusinessName", s1);
                                        if(BusinessId.length()==0){

                                            listItems.add(listItem);
                                        }else if(BusinessId.equals(obj.getString("businessid"))){
                                            listItems.add(listItem);
                                        }
                                    }catch (Exception e){
                                        Log.e("数据库出错",e.toString());
                                    }

                                }


                            }
                            SimpleAdapter simpleAdapter = new SimpleAdapter(ReviewPlanDateActivity.this, listItems,
                                    R.layout.review_plan_date_item,
//                                    new String[] {"documentnumber"},
                                    new String[]{"BusinessName","documentnumber","zgqx"},
                                    new int[]{R.id.enterprise_name,R.id.document_number,R.id.check_up_date});
                            listProblem.setAdapter(simpleAdapter);
                            Utility.setListViewHeightBasedOnChildren(listProblem);
                            listProblem.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                                    if(!fromTime.getText().toString().equals("--点击选择--")&&checkEnterprise06.getText().length()>0){
                                    Intent intent = new Intent();
                                    intent.putExtra("DocumentId", listItems.get(position).get("documentid").toString());
                                    intent.putExtra("documentnumber", listItems.get(position).get("documentnumber").toString());
                                    intent.putExtra("BusinessId", listItems.get(position).get("businessid").toString());
//                                        intent.putExtra("StartTime",s_from_time);
//                                        intent.putExtra("EndTime",s_end_time);
                                    intent.putExtra("address",listItems.get(position).get("address").toString());
//                                    intent.putExtra("yhzgqtp",listItems.get(position).get("yhzgqtp").toString());
                                    intent.setClass(ReviewPlanDateActivity.this, ReviewHiddenActivity.class);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
//                                    }else {
//                                        Toast.makeText(ReviewPlanDateActivity.this, "请完善信息", Toast.LENGTH_SHORT).show();
//                                    }
                                }
                            });

                            //关闭加载页
                            if (progressDialog != null) {
                                progressDialog.dismiss();
                                progressDialog = null;
                            }
                        } else {
                            Toast.makeText(ReviewPlanDateActivity.this, "没有数据", Toast.LENGTH_SHORT).show();
//关闭加载页
                            if (progressDialog != null) {
                                progressDialog.dismiss();
                                progressDialog = null;
                            }
                            init();
                        }
                    } catch (JSONException e) {
                        //关闭加载页
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                            progressDialog = null;
                        }
                        Toast.makeText(ReviewPlanDateActivity.this, "提交失败", Toast.LENGTH_SHORT).show();
                        Log.e("JSON exception", e.getMessage());
                        e.printStackTrace();
                        init();
                    }
                } else {
                    //关闭加载页
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                        progressDialog = null;
                    }
                    init();
                    Toast.makeText(ReviewPlanDateActivity.this, "网络未响应，提交失败", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

}
