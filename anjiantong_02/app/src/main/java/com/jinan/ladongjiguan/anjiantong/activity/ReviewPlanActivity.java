package com.jinan.ladongjiguan.anjiantong.activity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.jinan.ladongjiguan.anjiantong.PublicClass.AssetsDatabaseManager;
import com.jinan.ladongjiguan.anjiantong.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ReviewPlanActivity extends BaseActivity implements View.OnClickListener, View.OnTouchListener {


    @BindView(R.id.examine_page_back)
    LinearLayout examinePageBack;
    @BindView(R.id.title_layout)
    TextView titleLayout;
    @BindView(R.id.tx_table)
    TextView txTable;
    @BindView(R.id.list_02)
    ListView list02;

    @Override
    protected void initView() {
        setContentView(R.layout.review_plan_layout);
        ButterKnife.bind(this);
        titleLayout.setText("执行复查计划");
    }

    @Override
    protected void init() {
        examinePageBack.setOnClickListener(this);
        examinePageBack.setOnTouchListener(this);
        openDate();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.examine_page_back://返回键
                onBackPressed();
                break;
            case R.id.bt_add_check:
                Intent intent = new Intent();
                intent.setClass(this, ReviewBusinessListActivity.class);
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
     * 打开复查计划表
     * */
    protected void openDate(){

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
            Cursor c = db.rawQuery("SELECT* FROM ELL_ReviewInfo", null);
            while (c.moveToNext()) {
                listItem = new HashMap<>();
                // 查找企业名称
                Cursor cursor = db.rawQuery("SELECT* FROM ELL_Business WHERE BusinessId = ?",
                        new String[]{c.getString(c.getColumnIndex("BusinessId"))} );
                cursor.moveToFirst();

                listItem.put("Business",cursor.getString(cursor.getColumnIndex("BusinessName")));
                listItem.put("ReviewId",c.getString(c.getColumnIndex("ReviewId")));
                listItem.put("Address",c.getString(c.getColumnIndex("Address")));
                listItem.put("StatTime",c.getString(c.getColumnIndex("StartTime")));
                listItem.put("EndTime",c.getString(c.getColumnIndex("EndTime")));
                listItems.add(listItem);
                cursor.close();
            }
            // 创建一个SimpleAdapter
            simpleAdapter = new SimpleAdapter(ReviewPlanActivity.this, listItems,
                    R.layout.review_plan_list_item,
                    new String[]{"Business","StatTime", "EndTime","Address"},
                    new int[]{R.id.tx_add_check_up_01,
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
//                    if(getIntent().getStringExtra("state").equals("1")){
                        Intent intent = new Intent();
                        intent.setClass(ReviewPlanActivity.this, ReviewImplementActivity.class);
                        intent.putExtra("name", "1");
                        intent.putExtra("ReviewId", listItems.get(position).get("ReviewId").toString());
                        startActivity(intent);
                        overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
//                    }

                }
            });


            c.close();
        } catch (Exception e) {
            Log.e("2数据库报错", e.toString());
        }
    }

    /**
     * 返回刷新
     * */
    @Override
    protected void onResume() {
        init();
        super.onResume();
    }
}