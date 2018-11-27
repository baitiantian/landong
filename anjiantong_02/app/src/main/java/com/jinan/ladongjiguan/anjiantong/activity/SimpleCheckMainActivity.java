package com.jinan.ladongjiguan.anjiantong.activity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
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

import com.jinan.ladongjiguan.anjiantong.PublicClass.AssetsDatabaseManager;
import com.jinan.ladongjiguan.anjiantong.R;
import com.jinan.ladongjiguan.anjiantong.utils.SharedPreferencesUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SimpleCheckMainActivity extends BaseActivity implements View.OnClickListener, View.OnTouchListener {
    @BindView(R.id.examine_page_back)
    LinearLayout examinePageBack;
    @BindView(R.id.title_layout)
    TextView titleLayout;
    @BindView(R.id.delete_layout)
    LinearLayout deleteLayout;
    @BindView(R.id.tx_table)
    TextView txTable;
    @BindView(R.id.list_01)
    ListView list01;
    @BindView(R.id.Linear_layout_1)
    LinearLayout LinearLayout1;

    @Override
    protected void initView() {
        setContentView(R.layout.simple_check_main_layout);
        ButterKnife.bind(this);
        titleLayout.setText("快速执法");
    }

    @Override
    protected void init() {
        examinePageBack.setOnClickListener(this);
        examinePageBack.setOnTouchListener(this);
        /**
         * 分辨构建列表
         * */
        planDate2("2");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.examine_page_back://返回键
                finish();
                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
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
//                    if (SharedPreferencesUtil.getBooleanData(this, getIntent().getStringExtra("PlanId")+"isDone", false)) {
//                        listItem.put("Business", cursor.getString(cursor.getColumnIndex("BusinessName"))+"（已执行）");
//                    }else {
//                        listItem.put("Business", cursor.getString(cursor.getColumnIndex("BusinessName")));
//                    }
                    listItem.put("Business",cursor.getString(cursor.getColumnIndex("BusinessName")));
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
                String s_c ="SELECT* FROM ELL_GroupInfo WHERE PlanId = ?";

                Cursor cursor1 = db.rawQuery(s_c,
                        new String[]{c.getString(c.getColumnIndex("PlanId"))});
                while (cursor1.moveToNext()) {
                    try {
                        s_c = "SELECT* FROM Base_User WHERE UserId = ?";
                        Cursor cursor2 = db.rawQuery(s_c,
                                new String[]{cursor1.getString(cursor1.getColumnIndex("Headman"))});
                        cursor2.moveToFirst();

                        listItem.put("Headman",cursor2.getString(cursor2.getColumnIndex("RealName")));

                        cursor2.close();
                    } catch (Exception e) {
                        Log.e("人员用户数据库报错", e.toString(),e);
                    }

                }
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
            // 创建一个SimpleAdapter
            simpleAdapter = new SimpleAdapter(this, listItems,
                    R.layout.check_up_list_04_item,
                    new String[]{"Business", "CheckType", "Headman" ,"Address"},
                    new int[]{R.id.tx_add_check_up_01, R.id.tx_add_check_up_02,
                            R.id.tx_add_check_up_04, R.id.tx_add_check_up_03});
            // 为ListView设置Adapter
            list01.setAdapter(simpleAdapter);

            // 为ListView的列表项的单击事件绑定事件监听器
            list01.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                // 第position项被单击时激发该方法
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    Intent intent = new Intent();
                intent.setClass(SimpleCheckMainActivity.this, CheckUpDateActivity.class);
                intent.putExtra("name", "8");
                intent.putExtra("Simple", "1");
                intent.putExtra("id", listItems.get(position).get("PlanId").toString());
                startActivity(intent);
                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);

                }
            });


            c.close();
        } catch (Exception e) {
            Log.e("数据库报错", e.toString());
        }
    }


}
