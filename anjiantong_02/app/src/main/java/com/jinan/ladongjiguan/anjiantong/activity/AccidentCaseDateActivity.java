package com.jinan.ladongjiguan.anjiantong.activity;



import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jinan.ladongjiguan.anjiantong.PublicClass.AssetsDatabaseManager;
import com.jinan.ladongjiguan.anjiantong.R;


import butterknife.BindView;
import butterknife.ButterKnife;

public class AccidentCaseDateActivity extends BaseActivity implements View.OnTouchListener, View.OnClickListener {
    @BindView(R.id.examine_page_back)
    LinearLayout examinePageBack;
    @BindView(R.id.title_layout)
    TextView titleLayout;
    @BindView(R.id.accident_case_date_01)
    TextView accidentCaseDate01;
    @BindView(R.id.accident_case_date_02)
    TextView accidentCaseDate02;
    @BindView(R.id.accident_case_date_03)
    TextView accidentCaseDate03;

    @Override
    protected void initView() {
        setContentView(R.layout.accident_case_date_layout);
        ButterKnife.bind(this);
    }

    @Override
    protected void init() {
        examinePageBack.setOnTouchListener(this);
        examinePageBack.setOnClickListener(this);
        getDate();
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
            Cursor cursor = db.rawQuery("SELECT* FROM accident WHERE id = ?",
                    new String[]{getIntent().getStringExtra("id")});
            cursor.moveToFirst();
            if(!(cursor.getString(cursor.getColumnIndex("content"))+"").equals("null")){
                accidentCaseDate01.setText(cursor.getString(cursor.getColumnIndex("content")));
            }else if(!(cursor.getString(cursor.getColumnIndex("backgroud"))+"").equals("null")){
                accidentCaseDate01.setText(cursor.getString(cursor.getColumnIndex("backgroud")));
            }else if(!(cursor.getString(cursor.getColumnIndex("accidentgc"))+"").equals("null")){
                accidentCaseDate01.setText(cursor.getString(cursor.getColumnIndex("accidentgc")));
            }
            if(!(cursor.getString(cursor.getColumnIndex("accidentfx"))+"").equals("null")){
                accidentCaseDate02.setText(cursor.getString(cursor.getColumnIndex("accidentfx")));
            }
            if(!(cursor.getString(cursor.getColumnIndex("accidentcs"))+"").equals("null")){
                accidentCaseDate03.setText(cursor.getString(cursor.getColumnIndex("accidentcs")));
            }
            titleLayout.setText(cursor.getString(cursor.getColumnIndex("title")));
            cursor.close();
        } catch (Exception e) {
            Log.e("打开事故案例数据库报错", e.toString());
        }
    }
}
