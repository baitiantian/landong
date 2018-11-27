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

public class ShowDateActivity extends BaseActivity implements View.OnTouchListener, View.OnClickListener {


    @BindView(R.id.examine_page_back)
    LinearLayout examinePageBack;
    @BindView(R.id.title_layout)
    TextView titleLayout;
    @BindView(R.id.list_01)
    ListView list01;
    // 创建一个List集合，List集合的元素是Map
    private List<Map<String, Object>> listItems = new ArrayList<>();
    @Override
    protected void initView() {
        setContentView(R.layout.show_date_layout);
        ButterKnife.bind(this);
        titleLayout.setText("本机使用记录");
    }

    @Override
    protected void init() {
        examinePageBack.setOnClickListener(this);
        examinePageBack.setOnTouchListener(this);
        openDate();
    }
    /***
     * 打开数据库查看本机使用记录
     * */
    protected void openDate(){
        // 初始化，只需要调用一次
        AssetsDatabaseManager.initManager(getApplication());
        // 获取管理对象，因为数据库需要通过管理对象才能够获取
        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
        // 通过管理对象获取数据库
        SQLiteDatabase db = mg.getDatabase("users.db");
        try {
            Cursor cursor = db.rawQuery("SELECT* FROM ELL_Date",null);
            cursor.moveToLast();
            while (cursor.moveToPrevious()){
                Map<String,Object> map = new HashMap<>();
                map.put("UserId",cursor.getString(cursor.getColumnIndex("UserId")));
                map.put("LoginTime",cursor.getString(cursor.getColumnIndex("LoginTime")));
                map.put("ExitTime",cursor.getString(cursor.getColumnIndex("ExitTime")));
                Cursor cursor1 = db.rawQuery("SELECT* FROM Base_User WHERE UserId = ?",
                        new String[]{cursor.getString(cursor.getColumnIndex("UserId"))});
                cursor1.moveToFirst();
                map.put("RealName",cursor1.getString(cursor1.getColumnIndex("RealName")));
                cursor1.close();
                listItems.add(map);
            }
            cursor.close();
            SimpleAdapter simpleAdapter = new SimpleAdapter(this,listItems,R.layout.show_date_list_item,
                    new String[]{"RealName","LoginTime","ExitTime"},
                    new int[]{R.id.show_date_01,R.id.show_date_02,R.id.show_date_03});
            list01.setAdapter(simpleAdapter);

        }catch (Exception e){
            Log.e("数据库报错",e.toString());
        }

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
