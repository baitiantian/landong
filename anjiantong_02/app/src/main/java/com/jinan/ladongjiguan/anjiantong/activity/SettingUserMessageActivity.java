package com.jinan.ladongjiguan.anjiantong.activity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import com.jinan.ladongjiguan.anjiantong.PublicClass.AssetsDatabaseManager;
import com.jinan.ladongjiguan.anjiantong.utils.SharedPreferencesUtil;
import com.jinan.ladongjiguan.anjiantong.R;


public class SettingUserMessageActivity extends BaseActivity {

    @Override
    protected void initView() {
        setContentView(R.layout.activity_avatar_toolbar_sample);
        //返回箭头
//        LinearLayout setting_user_message_back = (LinearLayout)findViewById(R.id.setting_user_message_back);
//        setting_user_message_back.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });
        //退出按钮
        Button setting_user_message_exit = (Button)findViewById(R.id.setting_user_message_exit);
        setting_user_message_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
            }
        });
    }

    @Override
    protected void init() {
        TextView user_real_name = (TextView)findViewById(R.id.user_real_name);
        TextView user_gender = (TextView)findViewById(R.id.user_gender);
        TextView create_user_name = (TextView)findViewById(R.id.create_user_name);
        TextView user_age = (TextView)findViewById(R.id.user_age);
        TextView user_id = (TextView)findViewById(R.id.user_id);
        TextView user_telephone = (TextView)findViewById(R.id.user_telephone);


        TextView cat_title = (TextView)findViewById(R.id.cat_title);
        // 初始化，只需要调用一次
        AssetsDatabaseManager.initManager(getApplication());
        // 获取管理对象，因为数据库需要通过管理对象才能够获取
        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
        // 通过管理对象获取数据库
        SQLiteDatabase db = mg.getDatabase("users.db");
        try {
            Cursor cursor = db.rawQuery("SELECT* FROM Base_User WHERE UserId = ?",
                    new String[]{SharedPreferencesUtil.getStringData(this,"userId",null)});
            cursor.moveToFirst();
            user_real_name.setText(cursor.getString(cursor.getColumnIndex("RealName")));
            cat_title.setText(cursor.getString(cursor.getColumnIndex("RealName")));
            user_gender.setText(cursor.getString(cursor.getColumnIndex("Gender")));
            if(cursor.getString(cursor.getColumnIndex("Gender")).equals("女")){
                ImageView imageView = (ImageView)findViewById(R.id.img_user_gender);
                imageView.setImageResource(R.drawable.img_user_gender_woman);
            }
            user_age.setText(cursor.getString(cursor.getColumnIndex("Age")));
            user_id.setText(cursor.getString(cursor.getColumnIndex("IDCard")));
            create_user_name.setText(cursor.getString(cursor.getColumnIndex("Code")));
            user_telephone.setText(cursor.getString(cursor.getColumnIndex("Mobile")));
            cursor.close();
        }catch (Exception e){
            Log.e("数据库报错", e.toString());
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
    }
}
