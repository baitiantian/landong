package com.jinan.ladongjiguan.anjiantong.activity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.content.ContextCompat;
import android.util.Log;
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

import com.jinan.ladongjiguan.anjiantong.PublicClass.AssetsDatabaseManager;
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


public class PersonnelManagementActivity extends BaseActivity implements View.OnClickListener, View.OnTouchListener {


    @BindView(R.id.examine_page_back)
    LinearLayout examinePageBack;
    @BindView(R.id.title_layout)
    TextView titleLayout;
    @BindView(R.id.e_car_num_6)
    Spinner eCarNum6;
    @BindView(R.id.e_car_num_7)
    Spinner eCarNum7;
    @BindView(R.id.et_name)
    EditText etName;
    @BindView(R.id.list_01)
    ListView list01;
    @BindView(R.id.button_01)
    Button button01;
    private List<Map<String, Object>> listItems;//单位下拉列表配置
    private List<Map<String, Object>> listItems1;//部门下拉列表配置
    private List<Map<String, Object>> listItems2;//查询到的人员列表配置
    private String Companyid;//单位ID
    private String departmentid;//部门id
    private String WEB_SERVER_URL;
    private CustomProgressDialog progressDialog = null;//加载页
    private String department = "";//部门名称
    private String TAG = PersonnelManagementActivity.class.getSimpleName();
    @Override
    protected void initView() {
        setContentView(R.layout.personnel_management_layout);
        ButterKnife.bind(this);
        titleLayout.setText("执法人员");
        setSpinner();//配置下拉列表
    }

    @Override
    protected void init() {
        examinePageBack.setOnClickListener(this);
        examinePageBack.setOnTouchListener(this);
        button01.setOnClickListener(this);
        WEB_SERVER_URL= SharedPreferencesUtil.getStringData(this,"IPString","");
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.examine_page_back://返回键
                onBackPressed();
                break;
            case R.id.button_01://查询按钮
                httpPersonnel();
                break;
            default:
                break;
        }
    }
    /**
     * 配置下拉列表
     * */
    protected void setSpinner(){
        // 初始化，只需要调用一次
        AssetsDatabaseManager.initManager(this);
        // 获取管理对象，因为数据库需要通过管理对象才能够获取
        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
        // 通过管理对象获取数据库
        final SQLiteDatabase db = mg.getDatabase("users.db");
        Cursor c;
        try {
            // 对数据库进行操作
            c = db.rawQuery("SELECT* FROM Base_Company",null);
            // 创建一个List集合，List集合的元素是Map

            listItems = new ArrayList<>();
            Map<String, Object> listItem ;
            while (c.moveToNext()) {
                listItem = new HashMap<>();
                listItem.put("CompanyId",c.getString(c.getColumnIndex("CompanyId")));
                listItem.put("FullName",c.getString(c.getColumnIndex("FullName")));
                listItems.add(listItem);
            }

            /**
             * 下拉列表 单位
             * */
            //绑定适配器和值
            SimpleAdapter simpleAdapter = new SimpleAdapter(this, listItems,
                    R.layout.login_spinner_item,
                    new String[] {"FullName"},
                    new int[] {R.id.text});
            eCarNum6.setAdapter(simpleAdapter);
            Companyid = listItems.get(0).get("CompanyId").toString();
            Log.e(TAG+"Person","listItems.size为"+listItems.size());
            for (int i = 0; i < listItems.size(); i++) {
                Log.e(TAG+"-","Companyid为"+Companyid);
            }
            eCarNum6.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    try {
                        Log.e(TAG+"-Persoonnal","--"+listItems.get(position).get("CompanyId").toString());
                        // 对数据库进行操作
                        Cursor a = db.rawQuery("SELECT* FROM Base_Department WHERE CompanyId = ?",
                                new String[]{listItems.get(position).get("CompanyId").toString()});
                        // 创建一个List集合，List集合的元素是Map
                        Companyid = listItems.get(position).get("CompanyId").toString();
                        Log.e(TAG+"-","Companyid为"+Companyid);
                        listItems1 = new ArrayList<>();
                        Map<String, Object> listItem ;
                        while (a.moveToNext()) {
                            String s = a.getString(a.getColumnIndex("DepartmentId"));
                            Log.e(TAG+"-","DepartmentId"+s);
                            listItem = new HashMap<>();
                            listItem.put("DepartmentId",s);
                            listItem.put("FullName",a.getString(a.getColumnIndex("FullName")));
                            listItems1.add(listItem);
                        }
                        Log.e(TAG+"-Personnal","-listItems1.size为"+listItems1.size());
                        SimpleAdapter simpleAdapter = new SimpleAdapter(PersonnelManagementActivity.this, listItems1,
                                R.layout.login_spinner_item,
                                new String[] {"FullName"},
                                new int[] {R.id.text});
                        eCarNum7.setAdapter(simpleAdapter);
                        if(listItems1!=null) {
                            departmentid = listItems1.get(0).get("DepartmentId").toString();
                        }
                        Log.e(TAG+"-Personal","departmentid为"+departmentid);
                        /**
                         * 下拉列表 部门
                         * */
                        eCarNum7.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                                TextView v1 = (TextView)view.findViewById(R.id.text);
                                departmentid = listItems1.get(position).get("DepartmentId").toString();
                                department = listItems1.get(position).get("FullName").toString();
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                        a.close();
                    }catch (Exception e){
                        Log.e("数据库报错",e.toString(),e);
                    }

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            c.close();
        }catch (Exception e){
            Log.e("数据库报错",e.toString());
        }

    }

    /**
     * 人员查询显示
     * */
    protected void httpPersonnel(){
        // 初始化，只需要调用一次
        AssetsDatabaseManager.initManager(this);
        // 获取管理对象，因为数据库需要通过管理对象才能够获取
        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
        // 通过管理对象获取数据库
        final SQLiteDatabase db = mg.getDatabase("users.db");

        //加载页添加
        if (progressDialog == null){
            progressDialog = CustomProgressDialog.createDialog(this);
        }
        progressDialog.show();
        WEB_SERVER_URL= SharedPreferencesUtil.getStringData(this,"IPString","");
        //添加参数
        HashMap<String, String> properties = new HashMap<>();
        properties.put("Xml", "<Request><data  code='select-UseByCompanyid'><no><departmentcode>" +
                departmentid+"</departmentcode></no></data></Request> ");
        properties.put("Token", "");
        Log.d("发送数据",properties.toString());
        WebServiceUtils.callWebService(WEB_SERVER_URL, "DynamicInvoke", properties, new WebServiceUtils.WebServiceCallBack() {

            //WebService接口返回的数据回调到这个方法中
            @Override
            public void callBack(SoapObject result) {
                if(result != null){
                    Log.d("返回数据",result.toString());
                    try {
//                                       Log.d("用户名发过去的结果",""+result);
                        Object detail = result.getProperty("DynamicInvokeResult");
                        JSONObject jsonObj;
                        jsonObj = XML.toJSONObject(detail.toString());
                        JSONArray array;
                        JSONObject obj;
                        listItems2 = new ArrayList<>();
//                                        Log.d("秘钥",jsonObj.getJSONObject("DocumentElement").getJSONObject("Table").getString("secretkey"));
                        if(jsonObj.has("DocumentElement")&&jsonObj.toString().substring(jsonObj.toString().length()-3,jsonObj.toString().length()-2).equals("}")){
                            obj = jsonObj.getJSONObject("DocumentElement").getJSONObject("Table");
                            Map<String, Object> listItem = new HashMap<>();
                            listItem.put("userid",obj.getString("userid"));
                            listItem.put("RealName",obj.getString("realname"));
                            listItem.put("num",obj.getString("code"));
                            listItem.put("code",department);
                            listItems2.add(listItem);
                        }else if(jsonObj.has("DocumentElement")&&jsonObj.toString().substring(jsonObj.toString().length()-3,jsonObj.toString().length()-2).equals("]")){
                            array =  jsonObj.getJSONObject("DocumentElement").getJSONArray("Table");
                            listItems2 = new ArrayList<>();
                            for(int i=0; i<array.length();i++){
                                obj = array.getJSONObject(i);
                                Map<String, Object> listItem = new HashMap<>();
                                listItem.put("userid",obj.getString("userid"));
                                listItem.put("RealName",obj.getString("realname"));
                                listItem.put("num",obj.getString("code"));
                                listItem.put("code",department);
                                listItems2.add(listItem);
                            }

                        }else {
                            Toast.makeText(PersonnelManagementActivity.this,"没有所查找的数据", Toast.LENGTH_SHORT).show();

                        }
                        SimpleAdapter simpleAdapter = new SimpleAdapter(PersonnelManagementActivity.this, listItems2,
                                R.layout.personnel_management_list_item,
                                new String[] {"num","RealName","code"},
                                new int[] {R.id.enterprise_num,R.id.enterprise_name,R.id.enterprise_code});
                        list01.setAdapter(simpleAdapter);
                        list01.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Intent intent = new Intent();
                                intent.setClass(PersonnelManagementActivity.this,PersonnelMessageActivity.class);
                                intent.putExtra("userid",listItems2.get(position).get("userid").toString());
                                startActivity(intent);
                                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                            }
                        });
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
                        Toast.makeText(PersonnelManagementActivity.this,"提交失败", Toast.LENGTH_SHORT).show();
                        Log.e("JSON exception", e.getMessage());
                        e.printStackTrace();
                    }
                }else {
                    //关闭加载页
                    if (progressDialog != null){
                        progressDialog.dismiss();
                        progressDialog = null;
                    }
                    Toast.makeText(PersonnelManagementActivity.this,"网络未响应，提交失败", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }


    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
    }
}

