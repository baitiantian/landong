package com.jinan.ladongjiguan.anjiantong.activity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.content.ContextCompat;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.jinan.ladongjiguan.anjiantong.PublicClass.AssetsDatabaseManager;
import com.jinan.ladongjiguan.anjiantong.PublicClass.Utility;
import com.jinan.ladongjiguan.anjiantong.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class CheckDetailedListActivity extends BaseActivity implements View.OnClickListener, View.OnTouchListener {


    @BindView(R.id.examine_page_back)
    LinearLayout examinePageBack;
    @BindView(R.id.title_layout)
    TextView titleLayout;
    @BindView(R.id.delete_layout)
    LinearLayout deleteLayout;
    @BindView(R.id.tv_check_detailed_01)
    TextView tvCheckDetailed01;
    @BindView(R.id.tv_check_detailed_02)
    TextView tvCheckDetailed02;
    @BindView(R.id.tv_check_detailed_03)
    TextView tvCheckDetailed03;
    @BindView(R.id.tv_check_detailed_04)
    ListView tvCheckDetailed04;
    @BindView(R.id.tv_check_detailed_05)
    TextView tvCheckDetailed05;
    @BindView(R.id.tv_check_detailed_06)
    TextView tvCheckDetailed06;
    @BindView(R.id.tv_check_detailed_07)
    TextView tvCheckDetailed07;
    @BindView(R.id.tv_check_detailed_08)
    TextView tvCheckDetailed08;
    @BindView(R.id.l_law)
    LinearLayout lLaw;

    @Override
    protected void initView() {
        setContentView(R.layout.check_detailed_list);
        ButterKnife.bind(this);
    }

    @Override
    protected void init() {
        examinePageBack.setOnClickListener(this);
        examinePageBack.setOnTouchListener(this);
        AssetsDatabaseManager.initManager(getApplication());
        // 获取管理对象，因为数据库需要通过管理对象才能够获取
        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
        // 通过管理对象获取数据库
        SQLiteDatabase db = mg.getDatabase("users.db");
//        Cursor cursor = db.rawQuery("SELECT* FROM ELL_CheckDetailed WHERE CheckDetailedId = ?",
//                new String[]{getIntent().getStringExtra("CheckDetailedId")});
        Cursor cursor = db.rawQuery("SELECT* FROM cm_chkitem WHERE jcnrid = ?",
                new String[]{getIntent().getStringExtra("CheckDetailedId")});
        cursor.moveToFirst();
//        tvCheckDetailed01.setText(cursor.getString(cursor.getColumnIndex("CheckContent")));
//        tvCheckDetailed02.setText(cursor.getString(cursor.getColumnIndex("Require")));
//        tvCheckDetailed03.setText(cursor.getString(cursor.getColumnIndex("Method")));
//        tvCheckDetailed05.setText(cursor.getString(cursor.getColumnIndex("Law")));
//        tvCheckDetailed06.setText(cursor.getString(cursor.getColumnIndex("Penalty")));
//        tvCheckDetailed07.setText(cursor.getString(cursor.getColumnIndex("PenaltyAnge")));
//        tvCheckDetailed08.setText(cursor.getString(cursor.getColumnIndex("AmerceType")));
        tvCheckDetailed01.setText(getIntent().getStringExtra("Content"));
        tvCheckDetailed02.setText(cursor.getString(cursor.getColumnIndex("jcbz")));
        tvCheckDetailed03.setText(cursor.getString(cursor.getColumnIndex("jcfs")));
        tvCheckDetailed05.setText(cursor.getString(cursor.getColumnIndex("tknr")));
        tvCheckDetailed06.setText(cursor.getString(cursor.getColumnIndex("yjnr")));
        tvCheckDetailed07.setText(cursor.getString(cursor.getColumnIndex("cfzlhfd")));
        titleLayout.setText(getIntent().getStringExtra("Content"));
        if(tvCheckDetailed07.getText().toString().equals("null")){
            tvCheckDetailed07.setText("");
        }
        switch (cursor.getString(cursor.getColumnIndex("cftp"))+""){
            case "0":
                tvCheckDetailed08.setText("无");
                break;
            case "1":
                tvCheckDetailed08.setText("可并处");
                break;
            case "2":
                tvCheckDetailed08.setText("并处");
                break;
            default:
                break;
        }
//        Cursor cursor1 = db.rawQuery("SELECT* FROM ELL_BreakLaw WHERE CheckDetailedId = ?",
//                new String[]{getIntent().getStringExtra("CheckDetailedId")});
//        List<HashMap<String,Object>> lists = new ArrayList<>();
//        while (cursor1.moveToNext()){
//            HashMap<String,Object> map = new HashMap<>();
//            map.put("BreakContent",cursor1.getString(cursor1.getColumnIndex("BreakContent")));
//            lists.add(map);
//        }
        List<HashMap<String,Object>> lists = new ArrayList<>();
        String[] strings = cursor.getString(cursor.getColumnIndex("wfxw")).split("；");
        for(int i=0;i<strings.length;i++){
            HashMap<String,Object> map = new HashMap<>();
            map.put("BreakContent",strings[i]);
            lists.add(map);
        }
//        cursor1.close();
        // 创建一个SimpleAdapter
        SimpleAdapter simpleAdapter = new SimpleAdapter(this, lists,
                R.layout.enterprise_information_list_item,
                new String[] {"BreakContent"},
                new int[] {R.id.enterprise_name});
        tvCheckDetailed04.setAdapter(simpleAdapter);
        Utility.setListViewHeightBasedOnChildren(tvCheckDetailed04);
        tvCheckDetailed04.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onBackPressed();
            }
        });
        cursor.close();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.examine_page_back://返回
                onBackPressed();
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


}
