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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.jinan.ladongjiguan.anjiantong.PublicClass.AssetsDatabaseManager;
import com.jinan.ladongjiguan.anjiantong.utils.SharedPreferencesUtil;
import com.jinan.ladongjiguan.anjiantong.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;


public class CheckUpActivity extends BaseActivity implements View.OnClickListener, View.OnTouchListener {


    @BindView(R.id.examine_page_back)
    LinearLayout examinePageBack;
    @BindView(R.id.title_layout)
    TextView titleLayout;
    @BindView(R.id.bt_add_check)
    Button btAddCheck;
    @BindView(R.id.l_add_check_up)
    LinearLayout lAddCheckUp;
    @BindView(R.id.list_02)
    ListView list02;
    @BindView(R.id.tx_table)
    TextView txTable;
    private Intent intent = new Intent();

    @Override
    protected void initView() {
        setContentView(R.layout.check_up_layout);
        ButterKnife.bind(this);
        /**
         * 标题
         * */
        switch (getIntent().getStringExtra("state")) {
            case "1":
                titleLayout.setText("制定修改计划");
                break;
            case "2":
                titleLayout.setText("执行计划");
                lAddCheckUp.setVisibility(View.GONE);
                break;
            case "3":
                titleLayout.setText("人员分组");
                btAddCheck.setText("制定人员分组");
                txTable.setText("人员分组列表");
                break;
            case "4":
                titleLayout.setText("执法人员");
                txTable.setText("人员分组列表");
                lAddCheckUp.setVisibility(View.GONE);
                break;
            case "5"://企业详情入口
                titleLayout.setText("执行计划");
                lAddCheckUp.setVisibility(View.GONE);
                break;
            case "6":
                titleLayout.setText("职业卫生检查");
                txTable.setText("计划列表");
//                lAddCheckUp.setVisibility(View.GONE);
                break;
            case "0":
                titleLayout.setText("出具文书");
                lAddCheckUp.setVisibility(View.GONE);
                txTable.setText("请选择计划");
                break;
            case "9":
                titleLayout.setText("出具文书");
                lAddCheckUp.setVisibility(View.GONE);
                txTable.setText("请选择计划");
                break;
            default:
                break;
        }
//        Toast.makeText(this, "界面完成", Toast.LENGTH_SHORT).show();
        /**
         * 分辨构建列表
         * */
        addItem(getIntent().getStringExtra("state"));
    }

    @Override
    protected void init() {
        /**
         * 点击事件
         * */
        btAddCheck.setOnClickListener(this);
        examinePageBack.setOnClickListener(this);
        /**
         * 触摸事件
         * */
//        examinePageBack.setOnTouchListener(this);
//        Toast.makeText(this, "事件完成", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onClick(View v) {
        //设置可查看权限
        String headShip= SharedPreferencesUtil.getStringData(CheckUpActivity.this, "Headship","1");

        switch (v.getId()) {
            case R.id.bt_add_check://制定计划
                Intent intent = new Intent();
                //判断权限
                if(getIntent().getStringExtra("state").equals("1")&&!headShip.equals("办公室主任")){
                    intent.setClass(CheckUpActivity.this, CheckUpDateActivity.class);
                    intent.putExtra("state", "add");
                    intent.putExtra("name", "5");
                    startActivityForResult(intent,0);
                    overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                }else if(getIntent().getStringExtra("state").equals("3")&&!headShip.equals("办公室主任")){
                    intent.setClass(CheckUpActivity.this, PeopleGroupActivity.class);
                    intent.putExtra("state", "group");
                    intent.putExtra("name", getIntent().getStringExtra("state"));
                    startActivityForResult(intent,1);
                    overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                }else if(getIntent().getStringExtra("state").equals("6")&&!headShip.equals("办公室主任")){
                    intent.setClass(CheckUpActivity.this, CheckUpDateActivity.class);
                    intent.putExtra("state", "add");
                    intent.putExtra("name", "7");
                    startActivityForResult(intent,0);
                    overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                }else {
                    Toast.makeText(CheckUpActivity.this, "您没有制定的权限", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.examine_page_back://返回键
                onBackPressed();
//                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
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

    /**
     * 构建列表
     */
    protected void addItem(final String s) {
        String[] s01 = new String[]{"浪潮科技有限公司","中兴科技有限公司","华为科技有限公司","起亚汽车有限公司"};
        String[] s02 = new String[]{"李娜","王伟","张哥","翟营"};
        String[] s03 = new String[]{"张三","李四","王五","赵六"};
        // 创建一个List集合，List集合的元素是Map
        List<Map<String, Object>> listItems = new ArrayList<>();
        Map<String, Object> listItem;


        // 创建一个SimpleAdapter
        SimpleAdapter simpleAdapter ;
        switch (s) {
            case "1"://监管执法 可修改
                planDate(s);
                break;
            case "2"://隐患监督 不可修改
                planDate2(s);
                break;
            case "3"://人员列表 （制定人员分组）
                for (int i = 0; i < s02.length; i++) {
                    listItem = new HashMap<>();
                    listItem.put("01", s02[i]);
                    String s1 = "";

                    for(int x = 0; x < s03.length; x++){
                        if(x == 0){
                            s1 = s03[x];
                        }else {

                            s1 = s03[x]+ "," + s1;
                        }
                    }
                    listItem.put("02", s1);
                    listItem.put("03", "路线" + i);
                    listItem.put("04", "时间" + i);
                    listItems.add(listItem);
                }
                // 创建一个SimpleAdapter
                simpleAdapter = new SimpleAdapter(CheckUpActivity.this, listItems,
                        R.layout.check_up_list_03_item,
                        new String[]{"01", "02", "03", "04"},
                        new int[]{R.id.tx_add_check_up_01, R.id.tx_add_check_up_02, R.id.tx_add_check_up_03,
                                R.id.tx_add_check_up_04});
                // 为ListView设置Adapter
                list02.setAdapter(simpleAdapter);
                break;
            case "4"://人员列表 （执法人员入口）
                for (int i = 0; i < s02.length; i++) {
                    listItem = new HashMap<>();
                    listItem.put("01", s02[i]);
                    String s1 = "";

                    for(int x = 0; x < s03.length; x++){
                        if(x == 0){
                            s1 = s03[x];
                        }else {

                            s1 = s03[x]+ "," + s1;
                        }
                    }
                    listItem.put("02", s1);
                    listItem.put("03", "路线" + i);
                    listItem.put("04", "时间" + i);
                    listItems.add(listItem);
                }
                // 创建一个SimpleAdapter
                simpleAdapter = new SimpleAdapter(CheckUpActivity.this, listItems,
                        R.layout.check_up_list_03_item,
                        new String[]{"01", "02", "03", "04"},
                        new int[]{R.id.tx_add_check_up_01, R.id.tx_add_check_up_02, R.id.tx_add_check_up_03,
                                R.id.tx_add_check_up_04});
                // 为ListView设置Adapter
                list02.setAdapter(simpleAdapter);
                break;
            case "5"://隐患监督 不可修改
                planDate2(s);
                break;
            case "6"://隐患监督 不可修改
                planDate2(s);
                break;
            case "0"://现场检查方案文书
                planDate(s);
                break;
            case "9"://现场检查方案文书
                planDate2(s);
                break;
            default:
                break;
        }
//        Toast.makeText(this, "数据填入完成", Toast.LENGTH_SHORT).show();
    }
    /**
     * 打开可修改计划表数据库
     * */
    protected void planDate(final String s){

        // 初始化，只需要调用一次
        AssetsDatabaseManager.initManager(getApplication());
        // 获取管理对象，因为数据库需要通过管理对象才能够获取
        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
        // 通过管理对象获取数据库
        SQLiteDatabase db = mg.getDatabase("users.db");

        try {
            // 创建一个List集合，List集合的元素是Map
            final List<Map<String, Object>> listItems = new ArrayList<>();
            Map<String, Object> listItem;
            // 创建一个SimpleAdapter
            SimpleAdapter simpleAdapter ;
            // 对数据库进行操作
            Cursor c = db.rawQuery("SELECT* FROM ELL_EnforcementPlan ORDER BY EndTime", null);
            while (c.moveToNext()) {
                listItem = new HashMap<>();
                String planId;
                // 查找企业名称
                try {
                    planId = c.getString(c.getColumnIndex("PlanId"));
                    String s1 = "SELECT* FROM ELL_Business WHERE BusinessId = ?";
                    Cursor cursor = db.rawQuery(s1,
                            new String[]{c.getString(c.getColumnIndex("BusinessId"))} );
                    cursor.moveToFirst();

                    if (SharedPreferencesUtil.getBooleanData(CheckUpActivity.this, c.getString(c.getColumnIndex("PlanId"))+"isDone", false)) {
                        listItem.put("Business", cursor.getString(cursor.getColumnIndex("BusinessName"))+"（已执行）");
                    }else {
                        listItem.put("Business", cursor.getString(cursor.getColumnIndex("BusinessName")));
                    }
                    cursor.close();
                }catch (Exception e){
                    listItem.put("Business","未知");
                }
                try{
                    listItem.put("CheckType",c.getString(c.getColumnIndex("CheckType")));
                }catch (Exception e){
                    listItem.put("CheckType","未知");
                }

                try{

                    listItem.put("StatTime",c.getString(c.getColumnIndex("StatTime")));
                }catch (Exception e){
                    listItem.put("StatTime","未知");
                }
                try{
                    listItem.put("EndTime",c.getString(c.getColumnIndex("EndTime")));

                }catch (Exception e){
                    listItem.put("EndTime","未知");
                }
                try{

                    listItem.put("PlanId",c.getString(c.getColumnIndex("PlanId")));
                }catch (Exception e){
                    listItem.put("PlanId","未知");
                }
                try{
                    listItem.put("Address",c.getString(c.getColumnIndex("Address")));

                }catch (Exception e){
                    listItem.put("Address","未知");
                }
                listItems.add(listItem);

            }
            Collections.reverse(listItems);
            // 创建一个SimpleAdapter
            simpleAdapter = new SimpleAdapter(CheckUpActivity.this, listItems,
                    R.layout.check_up_list_01_item,
                    new String[]{"Business", "CheckType",  "StatTime", "EndTime","Address"},
                    new int[]{R.id.tx_add_check_up_01, R.id.tx_add_check_up_02,
                            R.id.tx_add_check_up_04, R.id.tx_add_check_up_05,R.id.tx_add_check_up_03});
            // 为ListView设置Adapter
            list02.setAdapter(simpleAdapter);

            // 为ListView的列表项的单击事件绑定事件监听器
            list02.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                // 第position项被单击时激发该方法
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        final int position, long id) {
//                Intent intent = new Intent();
//                intent.setClass(EnterpriseInformationActivity.this, OrderDetailActivity.class);
//                startActivity(intent);
//                overridePendingTransition(R.anim.in_from_right,android.R.anim.fade_out);
                    if(getIntent().getStringExtra("state").equals("1")){
                        Intent intent = new Intent();
                        intent.setClass(CheckUpActivity.this, CheckUpDateActivity.class);
                        intent.putExtra("name", "1");
                        intent.putExtra("id", listItems.get(position).get("PlanId").toString());
                        startActivity(intent);
                        overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                    }else if(getIntent().getStringExtra("state").equals("0")){
                        Intent intent = new Intent();
                        intent.setClass(CheckUpActivity.this, PrintOut08Activity.class);
                        intent.putExtra("PlanId", listItems.get(position).get("PlanId").toString());
//                        intent.putExtra("BusinessType", BusinessType);
                        intent.putExtra("id", listItems.get(position).get("PlanId").toString());
                        startActivity(intent);
                        overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                    }

                }
            });


            c.close();
        } catch (Exception e) {
            Log.e("数据库报错", e.toString());
//            Toast.makeText(this, "数据库报错"+ e.toString(), Toast.LENGTH_SHORT).show();
        }
    }
    /**
     * 打开不可修改计划表数据库
     * */
    protected void planDate2(final String s){

        // 初始化，只需要调用一次
        AssetsDatabaseManager.initManager(getApplication());
        // 获取管理对象，因为数据库需要通过管理对象才能够获取
        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
        // 通过管理对象获取数据库
        SQLiteDatabase db = mg.getDatabase("users.db");

        try {
            // 创建一个List集合，List集合的元素是Map
            final List<Map<String, Object>> listItems = new ArrayList<>();
            Map<String, Object> listItem;
            // 创建一个SimpleAdapter
            SimpleAdapter simpleAdapter ;
            // 对数据库进行操作
            Cursor c = db.rawQuery("SELECT* FROM ELL_EnforcementPlan ORDER BY EndTime", null);
            while (c.moveToNext()) {
                listItem = new HashMap<>();
                // 查找企业名称
                try {
                    String s1 = "SELECT* FROM ELL_Business WHERE BusinessId = ?";
                    Cursor cursor = db.rawQuery(s1,
                            new String[]{c.getString(c.getColumnIndex("BusinessId"))} );
                    cursor.moveToFirst();
                    String planId = c.getString(c.getColumnIndex("PlanId"));
                    if (SharedPreferencesUtil.getBooleanData(CheckUpActivity.this, c.getString(c.getColumnIndex("PlanId"))+"isDone", false)) {
                        listItem.put("Business", cursor.getString(cursor.getColumnIndex("BusinessName"))+"（已执行）");
                    }else {
                        listItem.put("Business", cursor.getString(cursor.getColumnIndex("BusinessName")));
                    }
//                    listItem.put("Business",cursor.getString(cursor.getColumnIndex("BusinessName")));
                    cursor.close();
                }catch (Exception e){
                    listItem.put("Business","未知");
                }
                try{
                    listItem.put("CheckType",c.getString(c.getColumnIndex("CheckType")));
                }catch (Exception e){
                    listItem.put("CheckType","未知");
                }

                try{

                    listItem.put("StatTime",c.getString(c.getColumnIndex("StatTime")));
                }catch (Exception e){
                    listItem.put("StatTime","未知");
                }
                try{
                    listItem.put("EndTime",c.getString(c.getColumnIndex("EndTime")));

                }catch (Exception e){
                    listItem.put("EndTime","未知");
                }
                try{

                    listItem.put("PlanId",c.getString(c.getColumnIndex("PlanId")));
                }catch (Exception e){
                    listItem.put("PlanId","未知");
                }
                try{
                    listItem.put("Address",c.getString(c.getColumnIndex("Address")));

                }catch (Exception e){
                    listItem.put("Address","未知");
                }

                listItem.put("BusinessId",c.getString(c.getColumnIndex("BusinessId")));
                if(s.equals("2")){

                    listItems.add(listItem);
                }else if(s.equals("5")){
                    if(getIntent().getStringExtra("BusinessId").equals(c.getString(c.getColumnIndex("BusinessId")))){
                        listItems.add(listItem);
                    }
                }else if(s.equals("6")&&c.getString(c.getColumnIndex("CheckType")).equals("职业健康检查")){
                    listItems.add(listItem);
                }else if(s.equals("9")){
                    if(getIntent().getStringExtra("BusinessId").equals(c.getString(c.getColumnIndex("BusinessId")))){
                        listItems.add(listItem);
                    }
                }
            }
            Collections.reverse(listItems);
            // 创建一个SimpleAdapter
            simpleAdapter = new SimpleAdapter(CheckUpActivity.this, listItems,
                    R.layout.check_up_list_02_item,
                    new String[]{"Business", "CheckType",  "StatTime", "EndTime","Address"},
                    new int[]{R.id.tx_add_check_up_01, R.id.tx_add_check_up_02,
                            R.id.tx_add_check_up_04, R.id.tx_add_check_up_05, R.id.tx_add_check_up_03});
            // 为ListView设置Adapter
            list02.setAdapter(simpleAdapter);

            // 为ListView的列表项的单击事件绑定事件监听器
            list02.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                // 第position项被单击时激发该方法
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
//                Intent intent = new Intent();
//                intent.setClass(EnterpriseInformationActivity.this, OrderDetailActivity.class);
//                startActivity(intent);
//                overridePendingTransition(R.anim.in_from_right,android.R.anim.fade_out);
                    if(getIntent().getStringExtra("state").equals("2")||getIntent().getStringExtra("state").equals("5")||
                            getIntent().getStringExtra("state").equals("6")){
                        Intent intent = new Intent();
                        intent.setClass(CheckUpActivity.this, CheckUpDateActivity.class);
                        intent.putExtra("name", "2");
                        intent.putExtra("state", getIntent().getStringExtra("state"));
                        intent.putExtra("id", listItems.get(position).get("PlanId").toString());
                        intent.putExtra("BusinessId", listItems.get(position).get("BusinessId").toString());
                        startActivity(intent);
                        overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                    }else if(s.equals("9")){
                        Intent intent = new Intent();
                        intent.setClass(CheckUpActivity.this, PrintOut08Activity.class);
                        intent.putExtra("PlanId", listItems.get(position).get("PlanId").toString());
//                        intent.putExtra("BusinessType", BusinessType);
                        intent.putExtra("id", listItems.get(position).get("PlanId").toString());
                        intent.putExtra("BusinessId", listItems.get(position).get("BusinessId").toString());
                        startActivity(intent);
                        overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                    }

                }
            });


            c.close();
        } catch (Exception e) {
            Log.e("数据库报错", e.toString());
        }
    }

    /**
     * 返回刷新
     * */
    @Override
    protected void onResume() {
        addItem(getIntent().getStringExtra("state"));
        super.onResume();
    }

    @Override
    public void onBackPressed() {

        finish();
        overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        addItem(getIntent().getStringExtra("state"));
    }

}
