package com.jinan.ladongjiguan.anjiantong.activity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.content.ContextCompat;
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

import butterknife.BindView;
import butterknife.ButterKnife;


public class CheckDetailedActivity extends BaseActivity implements View.OnClickListener, View.OnTouchListener {
    @BindView(R.id.examine_page_back)
    LinearLayout examinePageBack;
    @BindView(R.id.title_layout)
    TextView titleLayout;
    @BindView(R.id.delete_layout)
    LinearLayout deleteLayout;
    @BindView(R.id.l_check_detailed)
    ListView lCheckDetailed;
    @BindView(R.id.et_check_detailed)
    EditText etCheckDetailed;
    @BindView(R.id.bt_check_detailed)
    Button btCheckDetailed;

    private List<HashMap<String, Object>> lists = new ArrayList<>();

    @Override
    protected void initView() {
        setContentView(R.layout.check_detailed_layout);
        ButterKnife.bind(this);
        titleLayout.setText("请选择需参考的条例");
    }

    @Override
    protected void init() {
        examinePageBack.setOnClickListener(this);
        examinePageBack.setOnTouchListener(this);
        btCheckDetailed.setOnClickListener(this);
// 初始化，只需要调用一次
        AssetsDatabaseManager.initManager(getApplication());
        // 获取管理对象，因为数据库需要通过管理对象才能够获取
        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
        // 通过管理对象获取数据库
        SQLiteDatabase db = mg.getDatabase("users.db");
        Cursor cursor = db.rawQuery("SELECT* FROM cm_jcnr WHERE jcxmid = ? ORDER BY orderno",
                new String[]{getIntent().getStringExtra("CheckItemId")});
        lists = new ArrayList<>();
        HashMap<String, Object> map;
        while (cursor.moveToNext()) {
            map = new HashMap<>();
            map.put("CheckDetailedId", cursor.getString(cursor.getColumnIndex("id")));
            map.put("Content", cursor.getString(cursor.getColumnIndex("jcnr")));
            lists.add(map);
        }
        SimpleAdapter simpleAdapter = new SimpleAdapter(this, lists,
                R.layout.enterprise_information_list_item,
                new String[]{"Content"},
                new int[]{R.id.enterprise_name});
        lCheckDetailed.setAdapter(simpleAdapter);
        cursor.close();
        lCheckDetailed.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.putExtra("CheckDetailedId", lists.get(position).get("CheckDetailedId").toString());
                setResult(1, intent);
                intent = new Intent();
                intent.setClass(CheckDetailedActivity.this, CheckDetailedListActivity.class);
                intent.putExtra("CheckDetailedId", lists.get(position).get("CheckDetailedId").toString());
                intent.putExtra("Content", lists.get(position).get("Content").toString());
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.examine_page_back://返回
                onBackPressed();
                break;
            case R.id.bt_check_detailed:
                if(etCheckDetailed.getText().length()>0){
                    // 初始化，只需要调用一次
                    AssetsDatabaseManager.initManager(getApplication());
                    // 获取管理对象，因为数据库需要通过管理对象才能够获取
                    AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
                    // 通过管理对象获取数据库
                    SQLiteDatabase db = mg.getDatabase("users.db");
                    Cursor cursor = db.rawQuery("SELECT* FROM ELL_CheckDetailed WHERE CheckContent like ?",
                            new String[]{"%"+etCheckDetailed.getText().toString()+"%"});
                    lists = new ArrayList<>();
                    HashMap<String, Object> map;
                    while (cursor.moveToNext()) {
                        map = new HashMap<>();
                        map.put("CheckDetailedId", cursor.getString(cursor.getColumnIndex("CheckDetailedId")));
                        map.put("Content", cursor.getString(cursor.getColumnIndex("CheckContent")));
                        lists.add(map);
                    }
                    if(lists.size()>0){
                        SimpleAdapter simpleAdapter = new SimpleAdapter(this, lists,
                                R.layout.enterprise_information_list_item,
                                new String[]{"Content"},
                                new int[]{R.id.enterprise_name});
                        lCheckDetailed.setAdapter(simpleAdapter);
                        cursor.close();
                        lCheckDetailed.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Intent intent = new Intent();
                                intent.putExtra("CheckDetailedId", lists.get(position).get("CheckDetailedId").toString());
                                setResult(1, intent);
                                intent = new Intent();
                                intent.setClass(CheckDetailedActivity.this, CheckDetailedListActivity.class);
                                intent.putExtra("CheckDetailedId", lists.get(position).get("CheckDetailedId").toString());
                                startActivity(intent);
                                finish();
                            }
                        });
                    }else {
                        Toast.makeText(this, "没有可显示的内容", Toast.LENGTH_SHORT).show();
                    }

                }else {
                    Toast.makeText(this, "请输入关键字", Toast.LENGTH_SHORT).show();
                }
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
        setResult(0, new Intent());
        finish();
        overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
    }

}
