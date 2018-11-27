package com.jinan.ladongjiguan.anjiantong.activity;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
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

public class LawDateActivity extends BaseActivity implements View.OnClickListener, View.OnTouchListener {


    @BindView(R.id.examine_page_back)
    LinearLayout examinePageBack;
    @BindView(R.id.title_layout)
    TextView titleLayout;
    @BindView(R.id.list_01)
    ListView list01;
    private List<Map<String,Object>> maps = new ArrayList<>();

    @Override
    protected void initView() {
        setContentView(R.layout.law_date_layout);
        ButterKnife.bind(this);
        titleLayout.setText(getIntent().getStringExtra("MAINTITLE"));
    }

    @Override
    protected void init() {
        examinePageBack.setOnTouchListener(this);
        examinePageBack.setOnClickListener(this);
        lawDate();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.examine_page_back://返回键
                onBackPressed();
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

    protected void lawDate(){
        // 初始化，只需要调用一次
        AssetsDatabaseManager.initManager(getApplication());
        // 获取管理对象，因为数据库需要通过管理对象才能够获取
        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
        // 通过管理对象获取数据库
        SQLiteDatabase db = mg.getDatabase("users.db");
        try {
            maps = new ArrayList<>();
            Cursor cursor = db.rawQuery("SELECT* FROM basiclawsdtl WHERE LAWSYSNO = ?",
                    new String[]{getIntent().getStringExtra("SYSNO")});
            while (cursor.moveToNext()){
                Map<String,Object> map = new HashMap<>();
                map.put("ITEMCONTENT",cursor.getString(cursor.getColumnIndex("ITEMCONTENT")));
                maps.add(map);
            }
            cursor.close();
            SimpleAdapter simpleAdapter = new SimpleAdapter(LawDateActivity.this,maps,
                    R.layout.law_date_item,
                    new String[] {"ITEMCONTENT"},
                    new int[] {R.id.enterprise_name});
            list01.setAdapter(simpleAdapter);
        }catch (Exception e){
            Log.e("法律数据库报错", e.toString());
        }
    }
}
