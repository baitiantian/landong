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
import android.widget.TextView;
import android.widget.Toast;

import com.jinan.ladongjiguan.anjiantong.PublicClass.AssetsDatabaseManager;
import com.jinan.ladongjiguan.anjiantong.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LawItemActivity extends BaseActivity implements View.OnClickListener, View.OnTouchListener {
    @BindView(R.id.examine_page_back)
    LinearLayout examinePageBack;
    @BindView(R.id.title_layout)
    TextView titleLayout;
    @BindView(R.id.et_name)
    EditText etName;
    @BindView(R.id.bt_review)
    Button btReview;
    @BindView(R.id.list_01)
    ListView list01;
    private List<Map<String,Object>> maps = new ArrayList<>();
    @Override
    protected void initView() {
        setContentView(R.layout.law_item_layout);
        ButterKnife.bind(this);
        titleLayout.setText("法规反查");
    }



    @Override
    protected void init() {
        examinePageBack.setOnTouchListener(this);
        examinePageBack.setOnClickListener(this);
        btReview.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.examine_page_back://返回键
                onBackPressed();
                break;
            case R.id.bt_review:
                if(etName.getText().toString().length()>0){
                    lawDate();
                }else {
                    Toast.makeText(this, "请输入需要搜索的内容", Toast.LENGTH_SHORT).show();
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


    protected void lawDate(){
        // 初始化，只需要调用一次
        AssetsDatabaseManager.initManager(getApplication());
        // 获取管理对象，因为数据库需要通过管理对象才能够获取
        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
        // 通过管理对象获取数据库
        SQLiteDatabase db = mg.getDatabase("users.db");
        try {
            maps = new ArrayList<>();
            Cursor cursor = db.rawQuery("SELECT* FROM basiclawsdtl WHERE ITEMCONTENT like ? ",
                    new String[]{"%"+etName.getText().toString()+"%"});
            while (cursor.moveToNext()){
                Cursor cursor1 = db.rawQuery("SELECT* FROM basiclawsmain WHERE SYSNO = ? ",
                        new String[]{cursor.getString(cursor.getColumnIndex("LAWSYSNO"))});
                cursor1.moveToFirst();
                Map<String,Object> map = new HashMap<>();
                map.put("ITEMCONTENT",cursor.getString(cursor.getColumnIndex("ITEMCONTENT")));
                map.put("LAWSYSNO",cursor.getString(cursor.getColumnIndex("LAWSYSNO")));
                map.put("MAINTITLE",cursor1.getString(cursor1.getColumnIndex("MAINTITLE")));
                if(cursor.getString(cursor.getColumnIndex("ITEMCONTENT")).length()>8){
                    maps.add(map);
                }

                cursor1.close();
            }
            cursor.close();
            SimpleAdapter simpleAdapter = new SimpleAdapter(LawItemActivity.this,maps,
                    R.layout.law_date_item,
                    new String[] {"ITEMCONTENT"},
                    new int[] {R.id.enterprise_name});
            list01.setAdapter(simpleAdapter);
            list01.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent();
                    intent.setClass(LawItemActivity.this,LawDateActivity.class);
                    intent.putExtra("SYSNO",maps.get(position).get("LAWSYSNO").toString());
                    intent.putExtra("MAINTITLE",maps.get(position).get("MAINTITLE").toString());
                    startActivity(intent);
                    overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                }
            });
        }catch (Exception e){
            Log.e("法律数据库报错", e.toString());
        }
    }
}
