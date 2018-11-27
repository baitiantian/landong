package com.jinan.ladongjiguan.anjiantong.activity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
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


public class HazardousChemicalsActivity extends BaseActivity implements View.OnClickListener, View.OnTouchListener {


    @BindView(R.id.examine_page_back)
    LinearLayout examinePageBack;
    @BindView(R.id.title_layout)
    TextView titleLayout;
    @BindView(R.id.check_enterprise_01)
    TextView checkEnterprise01;
    @BindView(R.id.check_enterprise_02)
    TextView checkEnterprise02;
    @BindView(R.id.check_enterprise_03)
    TextView checkEnterprise03;
    @BindView(R.id.check_enterprise_04)
    TextView checkEnterprise04;
    @BindView(R.id.check_enterprise_05)
    TextView checkEnterprise05;
    @BindView(R.id.check_enterprise_06)
    TextView checkEnterprise06;
    @BindView(R.id.check_enterprise_07)
    TextView checkEnterprise07;
    @BindView(R.id.check_enterprise_08)
    TextView checkEnterprise08;
    @BindView(R.id.check_enterprise_09)
    TextView checkEnterprise09;
    @BindView(R.id.check_enterprise_10)
    TextView checkEnterprise10;
    @BindView(R.id.check_enterprise_11)
    TextView checkEnterprise11;
    @BindView(R.id.check_enterprise_12)
    TextView checkEnterprise12;
    @BindView(R.id.check_enterprise_13)
    TextView checkEnterprise13;
    @BindView(R.id.check_enterprise_14)
    TextView checkEnterprise14;
    @BindView(R.id.check_enterprise_15)
    TextView checkEnterprise15;

    @Override
    protected void initView() {
        setContentView(R.layout.hazardous_chemicals_main_layout);
        ButterKnife.bind(this);
        titleLayout.setText(getIntent().getStringExtra("zhname"));
    }

    @Override
    protected void init() {
        /**
         * 点击事件
         * */
        examinePageBack.setOnClickListener(this);


        /**
         * 触摸事件
         * */
        examinePageBack.setOnTouchListener(this);

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
     * 获取化学品详细信息
     */
    protected void getDate() {
        // 初始化，只需要调用一次
        AssetsDatabaseManager.initManager(getApplication());
        // 获取管理对象，因为数据库需要通过管理对象才能够获取
        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
        // 通过管理对象获取数据库
        SQLiteDatabase db = mg.getDatabase("users.db");

        try {
            Cursor cursor = db.rawQuery("SELECT* FROM HUAXUEPINS WHERE zhname = ?",
                    new String[]{getIntent().getStringExtra("zhname")});
            cursor.moveToFirst();
            checkEnterprise01.setText(cursor.getString(cursor.getColumnIndex("enname")));
            checkEnterprise02.setText(cursor.getString(cursor.getColumnIndex("zhbm")));
            checkEnterprise03.setText(cursor.getString(cursor.getColumnIndex("enbm")));
            checkEnterprise04.setText(cursor.getString(cursor.getColumnIndex("fzs")));
            checkEnterprise05.setText(cursor.getString(cursor.getColumnIndex("wgxz")));
            checkEnterprise06.setText(cursor.getString(cursor.getColumnIndex("rd")));
            checkEnterprise07.setText(cursor.getString(cursor.getColumnIndex("fd")));
            checkEnterprise08.setText(cursor.getString(cursor.getColumnIndex("pfjc")));
            checkEnterprise09.setText(cursor.getString(cursor.getColumnIndex("yjjc")));
            checkEnterprise10.setText(cursor.getString(cursor.getColumnIndex("xr")));
            checkEnterprise11.setText(cursor.getString(cursor.getColumnIndex("sr")));
            checkEnterprise12.setText(cursor.getString(cursor.getColumnIndex("cyzysx")));
            checkEnterprise13.setText(cursor.getString(cursor.getColumnIndex("xlcl")));
            checkEnterprise14.setText(cursor.getString(cursor.getColumnIndex("zyyt")));
            checkEnterprise15.setText(cursor.getString(cursor.getColumnIndex("qt")));
            cursor.close();
        } catch (Exception e) {
            Log.e("打开化学品数据库报错", e.toString());
        }
    }


}
