package com.jinan.ladongjiguan.anjiantong.activity;

import android.content.Intent;
import android.content.res.Resources;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.jinan.ladongjiguan.anjiantong.PublicClass.CustomProgressDialog;
import com.jinan.ladongjiguan.anjiantong.utils.SharedPreferencesUtil;
import com.jinan.ladongjiguan.anjiantong.utils.WebServiceUtils;
import com.jinan.ladongjiguan.anjiantong.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.ksoap2.serialization.SoapObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReviewBusinessListActivity extends BaseActivity implements View.OnTouchListener, View.OnClickListener {

    @BindView(R.id.examine_page_back)
    LinearLayout examinePageBack;
    @BindView(R.id.title_layout)
    TextView titleLayout;
    @BindView(R.id.list_01)
    ListView list01;
    @BindView(R.id.e_car_num_6)
    Spinner eCarNum6;
    @BindView(R.id.et_name)
    EditText etName;
    @BindView(R.id.bt_review)
    Button btReview;
    private String WEB_SERVER_URL;
    private String string = "";//下拉列表选择的
    private CustomProgressDialog progressDialog = null;//加载页
    // 创建一个List集合，List集合的元素是Map
    private List<Map<String, Object>> listItems = new ArrayList<>();
    public static ReviewBusinessListActivity reviewBusinessListActivity;//保存计划时关闭用
    @Override
    protected void initView() {
        setContentView(R.layout.review_business_list_layout);
        ButterKnife.bind(this);
        titleLayout.setText("选择企业");
        WEB_SERVER_URL = SharedPreferencesUtil.getStringData(this, "IPString", "");
        reviewBusinessListActivity = this;
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
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
//        getBusinessName();
    }

    @Override
    protected void init() {
        btReview.setOnClickListener(this);
        examinePageBack.setOnTouchListener(this);
        examinePageBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.examine_page_back://返回键
                onBackPressed();
                break;
            case R.id.bt_review:
                if(string.length()>0){
                    getBusinessName();
                }else {
                    Toast.makeText(ReviewBusinessListActivity.this, "请选择企业类型", Toast.LENGTH_SHORT).show();
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
     * 获取企业信息
     * */
    protected void getBusinessName(){
        //加载页添加
        if (progressDialog == null){
            progressDialog = CustomProgressDialog.createDialog(this);
        }
        progressDialog.show();
        String UserId = SharedPreferencesUtil.getStringData(this, "userId", "");
        //添加参数
        HashMap<String, String> properties = new HashMap<>();
        properties.put("Xml", "<Request><data  code='GetDisposeBusiness'><no><UserId>" +
                UserId+"</UserId><BusinessName>" +
                etName.getText().toString()+"</BusinessName><BusinessType>" +
                string + "</BusinessType></no></data></Request>");
        properties.put("Token", "");
        Log.d("企业隐患发出数据", properties.toString());
        WebServiceUtils.callWebService(WEB_SERVER_URL, "DynamicInvoke", properties, new WebServiceUtils.WebServiceCallBack() {

            //WebService接口返回的数据回调到这个方法中
            @Override
            public void callBack(SoapObject result) {
                if (result != null) {
//                    Log.d("企业隐患返回数据", result.toString());
                    try {
//                                       Log.d("用户名发过去的结果",""+result);
                        Object detail = result.getProperty("DynamicInvokeResult");
                        JSONObject jsonObj;
                        jsonObj = XML.toJSONObject(detail.toString());
//                                        Log.d("秘钥",jsonObj.getJSONObject("DocumentElement").getJSONObject("Table").getString("secretkey"));
                        if(jsonObj.has("DocumentElement")){
                            JSONArray array;
                            JSONObject obj;
//                            Log.d("重名补全接收数据",jsonObj.toString());
                            listItems = new ArrayList<>();
                            if(jsonObj.has("DocumentElement")&&jsonObj.toString().substring(jsonObj.toString().length()-3,jsonObj.toString().length()-2).equals("}")){
                                obj = jsonObj.getJSONObject("DocumentElement").getJSONObject("Table");
                                Map<String, Object> listItem = new HashMap<>();
                                listItem.put("businessname",obj.getString("businessname"));
                                listItem.put("businesstype",obj.getString("businesstype"));
                                listItem.put("businessid",obj.getString("businessid"));
                                listItems.add(listItem);
                            }else if(jsonObj.has("DocumentElement")&&jsonObj.toString().substring(jsonObj.toString().length()-3,jsonObj.toString().length()-2).equals("]")){
                                array =  jsonObj.getJSONObject("DocumentElement").getJSONArray("Table");
                                for(int i=0; i<array.length();i++){
                                    obj = array.getJSONObject(i);
                                    Map<String, Object> listItem = new HashMap<>();
                                    listItem.put("businessname",obj.getString("businessname"));
                                    listItem.put("businesstype",obj.getString("businesstype"));
                                    listItem.put("businessid",obj.getString("businessid"));
                                    listItems.add(listItem);
                                }


                            }
                            SimpleAdapter simpleAdapter = new SimpleAdapter(ReviewBusinessListActivity.this, listItems,
                                    R.layout.enterprise_information_list_item,
                                    new String[] {"businessname"},
                                    new int[] {R.id.enterprise_name});
                            list01.setAdapter(simpleAdapter);
                            list01.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    Intent intent = new Intent();
                                    intent.putExtra("businessid",listItems.get(position).get("businessid").toString());
                                    intent.putExtra("businesstype",listItems.get(position).get("businesstype").toString());
                                    intent.setClass(ReviewBusinessListActivity.this,ReviewPlanDateActivity.class);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                                }
                            });
                            //关闭加载页
                            if (progressDialog != null) {
                                progressDialog.dismiss();
                                progressDialog = null;
                            }
                        }else {
                            Toast.makeText(ReviewBusinessListActivity.this, "没有企业", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(ReviewBusinessListActivity.this, "提交失败", Toast.LENGTH_SHORT).show();
                        Log.e("JSON exception", e.getMessage());
                        e.printStackTrace();
                    }
                } else {
                    //关闭加载页
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                        progressDialog = null;
                    }
                    Toast.makeText(ReviewBusinessListActivity.this, "网络未响应，提交失败", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }
}
