package com.lunduimohao.landongjiguang.lunduimohao.activity;

import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import com.lunduimohao.landongjiguang.lunduimohao.R;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class DataActivity extends BaseActivity implements View.OnClickListener{
    private String[] temp = {"1","2","3","4"};
    @Override
    protected void initView() {
        setContentView(R.layout.data_main_layout);
        TextView title_layout = (TextView)findViewById(R.id.title_layout);
        String s = getIntent().getStringExtra("name")+"查询结果";
        title_layout.setText(s);
        /**
         * 返回键
         * */
        findViewById(R.id.examine_page_back).setOnClickListener(this);
        /**
         * 保存数据键
         * */
        findViewById(R.id.save_data).setOnClickListener(this);
        /**
         * 读取显示数据
         * */
        temp = getIntent().getStringExtra("date").split(",");
        String s1;
        TextView data_1 = (TextView)findViewById(R.id.data_1);
        s1 = temp[1]+" mm";
        data_1.setText(s1);
        TextView data_2 = (TextView)findViewById(R.id.data_2);
        s1 = temp[2]+" mm";
        data_2.setText(s1);
        TextView data_3 = (TextView)findViewById(R.id.data_3);
        s1 = temp[3]+" mm";
        data_3.setText(s1);
        TextView data_4 = (TextView)findViewById(R.id.data_4);
        s1 = temp[4]+" mm";
        data_4.setText(s1);
        /**
         * 监测对象数据显示
         * */
        TextView t_car_num_1 = (TextView)findViewById(R.id.t_car_num_1);
        TextView t_car_num_2 = (TextView)findViewById(R.id.t_car_num_2);
        TextView t_car_num_3 = (TextView)findViewById(R.id.t_car_num_3);
        TextView t_car_num_4 = (TextView)findViewById(R.id.t_car_num_4);
        t_car_num_1.setText(getIntent().getStringExtra("wheel_data").subSequence(0,3));
        t_car_num_2.setText(getIntent().getStringExtra("wheel_data").subSequence(3,5));
        t_car_num_3.setText(getIntent().getStringExtra("wheel_data").subSequence(5,6));
        if(getIntent().getStringExtra("wheel_data").subSequence(6, 7).equals("0")){
            t_car_num_4.setText("左侧轮");
        }else {
            t_car_num_4.setText("右侧轮");
        }
    }

    @Override
    protected void init() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.examine_page_back:
                finish();
                overridePendingTransition(0, R.anim.zoomout);
                break;
            case R.id.save_data:
                dialog();
            default:
                break;
        }
    }
    /**
     * 弹出确认对话框
     * */
    protected void dialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("确认保存");
        builder.setMessage("是否保存本次测试数据到文件夹 TreadWear 下的 date.db ？");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                saveDate();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Toast.makeText(DataActivity.this, "已取消", Toast.LENGTH_SHORT).show();
            }
        });
        builder.show();
    }
    /**
     * 保存数据
     * */
    protected void saveDate(){
        SimpleDateFormat sDateFormat = new SimpleDateFormat(
                "yyyy年MM月dd日HH时mm分", Locale.getDefault());
        String date = sDateFormat.format(new java.util.Date());
        //创建文件夹
        File file = new File("/sdcard/TreadWear");
        boolean isDirectoryCreated=file.exists();
        if (!isDirectoryCreated) {
            isDirectoryCreated= file.mkdir();
        }
        if(isDirectoryCreated) {
            SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase("/sdcard/TreadWear/date.db",null);
            //创建一个表
            try {
                db.execSQL("create table wheel_date(" +
                        "WheelId varchar(50) NOT NULL primary key," +
                        "TreadWear varchar(50) NULL," +
                        "WheelThick varchar(50) NULL," +
                        "RimWidth varchar(50) NULL," +
                        "RimThick varchar(50) NULL," +
                        "Time varchar(50) NOT NULL,"+
                        "AllDate varchar(50) NOT NULL)");
            }catch (Exception e) {
                //This happens on every launch that isn't the first one.
                Log.w("一般第一次不发生的错误", "Error while creating db: " + e.toString());
            }
            /**
             * 更新插入数据
             * */
            try {
                db.execSQL("REPLACE INTO wheel_date VALUES('"+
                        getIntent().getStringExtra("wheel_date") +"','"+
                        temp[1] +"','"+
                        temp[2] +"','"+
                        temp[3] +"','"+
                        temp[4] +"','"+
                        date +"','1')");
            }catch (Exception e) {
                //This happens on every launch that isn't the first one.
                Log.w("一般不会发生的错误", "Error while REPLACE INTO db: " + e.toString());
            }
            Toast.makeText(DataActivity.this, "数据已保存", Toast.LENGTH_SHORT).show();
            db.close();
            finish();
            overridePendingTransition(0, R.anim.zoomout);
        }else {
            Toast.makeText(DataActivity.this, "保存数据失败", Toast.LENGTH_SHORT).show();
        }
    }
}
