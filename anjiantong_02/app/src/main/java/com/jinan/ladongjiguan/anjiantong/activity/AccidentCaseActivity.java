package com.jinan.ladongjiguan.anjiantong.activity;

import android.content.Intent;
import android.content.res.Resources;
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
import android.widget.Spinner;
import android.widget.TextView;

import com.jinan.ladongjiguan.anjiantong.PublicClass.AssetsDatabaseManager;
import com.jinan.ladongjiguan.anjiantong.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;



public class AccidentCaseActivity extends BaseActivity implements View.OnClickListener, View.OnTouchListener {
    @BindView(R.id.examine_page_back)
    LinearLayout examinePageBack;
    @BindView(R.id.title_layout)
    TextView titleLayout;
    @BindView(R.id.e_car_num_6)
    Spinner eCarNum6;
    @BindView(R.id.bt_review)
    Button btReview;
    @BindView(R.id.list_01)
    ListView list01;
    private String string = "";//下拉列表选择的
    // 创建一个List集合，List集合的元素是Map
    private List<Map<String, Object>> listItems = new ArrayList<>();
    @Override
    protected void initView() {
        setContentView(R.layout.accident_case_layout);
        ButterKnife.bind(this);
        titleLayout.setText("事故案例");
        /**
         * 下拉列表
         * */
        Resources res = getResources();
        final String[] strings01 = res.getStringArray(R.array.s_car_num_14);
        eCarNum6.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                string = strings01[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    protected void init() {
        btReview.setOnClickListener(this);
        examinePageBack.setOnTouchListener(this);
        examinePageBack.setOnClickListener(this);
        list01.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.examine_page_back://返回键
                onBackPressed();
                break;
            case R.id.bt_review:
                list01.setVisibility(View.VISIBLE);
                getDate();
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
     * 打开事故案例数据库
     * */
    protected void getDate(){
// 初始化，只需要调用一次
        AssetsDatabaseManager.initManager(getApplication());
        // 获取管理对象，因为数据库需要通过管理对象才能够获取
        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
        // 通过管理对象获取数据库
        SQLiteDatabase db = mg.getDatabase("users.db");

        try {
            Cursor cursor = db.rawQuery("SELECT* FROM accident WHERE type2 = ?",
                    new String[]{string});
            listItems = new ArrayList<>();
            while (cursor.moveToNext()){
                Map<String,Object> map = new HashMap<>();
                map.put("title",cursor.getString(cursor.getColumnIndex("title")));
                map.put("id",cursor.getString(cursor.getColumnIndex("id")));
                listItems.add(map);
            }
            SimpleAdapter simpleAdapter = new SimpleAdapter(AccidentCaseActivity.this, listItems,
                    R.layout.enterprise_information_list_item,
                    new String[] {"title"},
                    new int[] {R.id.enterprise_name});
            list01.setAdapter(simpleAdapter);
            list01.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent();
                    intent.putExtra("id",listItems.get(position).get("id").toString());
                    intent.setClass(AccidentCaseActivity.this,AccidentCaseDateActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                }
            });
            cursor.close();
        } catch (Exception e) {
            Log.e("打开化学品数据库报错", e.toString());
        }
    }
}
