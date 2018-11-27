package com.jinan.ladongjiguan.anjiantong.activity;

import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.jinan.ladongjiguan.anjiantong.PublicClass.AssetsDatabaseManager;
import com.jinan.ladongjiguan.anjiantong.PublicClass.Utility;
import com.jinan.ladongjiguan.anjiantong.R;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ReviewPeopleGroupCheckActivity extends BaseActivity implements View.OnClickListener, View.OnTouchListener {

    @BindView(R.id.examine_page_back)
    LinearLayout examinePageBack;
    @BindView(R.id.title_layout)
    TextView titleLayout;
    @BindView(R.id.list_people_group)
    ListView listPeopleGroup;
    @BindView(R.id.l_people_group)
    LinearLayout lPeopleGroup;
    @BindView(R.id.setting_user_message_exit)
    Button settingUserMessageExit;
    @BindView(R.id.tx_add_check_up_01)
    TextView txAddCheckUp01;

    @Override
    protected void initView() {
        setContentView(R.layout.review_people_group_check_layout);
        ButterKnife.bind(this);
        titleLayout.setText("查看人员分组");
    }

    @Override
    protected void init() {
        settingUserMessageExit.setOnClickListener(this);
        examinePageBack.setOnClickListener(this);
        examinePageBack.setOnTouchListener(this);
        openPeopleGroupDate();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.examine_page_back://返回键
                onBackPressed();
                break;
            case R.id.setting_user_message_exit://退出
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
     * 打开组员信息
     */
    protected void openPeopleGroupDate() {
        // 初始化，只需要调用一次
        AssetsDatabaseManager.initManager(getApplication());
        // 获取管理对象，因为数据库需要通过管理对象才能够获取
        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
        // 通过管理对象获取数据库
        SQLiteDatabase db = mg.getDatabase("users.db");
//        try {
        Cursor cursor1 = db.rawQuery("SELECT* FROM ELL_ReviewGroupInfo WHERE ReviewId = ?",
                new String[]{getIntent().getStringExtra("ReviewId")});
        ArrayList<HashMap<String, Object>> hashMaps = new ArrayList<>();
        while (cursor1.moveToNext()) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("GroupId", cursor1.getString(cursor1.getColumnIndex("GroupId")));

            Cursor cursor2 = db.rawQuery("SELECT* FROM Base_User WHERE UserId = ?",
                    new String[]{cursor1.getString(cursor1.getColumnIndex("Headman"))});
            cursor2.moveToFirst();
            map.put("Headman", cursor2.getString(cursor2.getColumnIndex("RealName")));
            Cursor cursor = db.rawQuery("SELECT* FROM ELL_ReviewMember WHERE GroupId = ?",
                    new String[]{cursor1.getString(cursor1.getColumnIndex("GroupId"))});
            int i = 0;
            String s2 = "";
            while (cursor.moveToNext()) {
                Cursor cursor3 = db.rawQuery("SELECT* FROM Base_User WHERE UserId = ?",
                        new String[]{cursor.getString(cursor.getColumnIndex("Member"))});
                cursor3.moveToFirst();
                if (i == 0) {
                    s2 = cursor3.getString(cursor3.getColumnIndex("RealName"));
                } else {
                    s2 = s2 + "," + cursor3.getString(cursor3.getColumnIndex("RealName"));
                }
                i++;
                cursor3.close();
            }
            map.put("Member", s2);
            Cursor cursor4 = db.rawQuery("SELECT* FROM ELL_ReviewDangerInfo WHERE GroupId = ?",
                    new String[]{cursor1.getString(cursor1.getColumnIndex("GroupId"))});
            String s = "";
            int i1 = 0;
            while (cursor4.moveToNext()) {
                Cursor cursor5 = db.rawQuery("SELECT* FROM ELL_HiddenDanger WHERE HiddenDangerId = ?",
                        new String[]{cursor4.getString(cursor4.getColumnIndex("HiddenDangerId"))});
                cursor5.moveToFirst();
                if (i1 == 0) {
                    s = cursor5.getString(cursor5.getColumnIndex("yhdd"));
                } else {
                    s = s + "-->" + cursor5.getString(cursor5.getColumnIndex("yhdd"));
                }
                i1++;
                cursor5.close();
            }
            map.put("Address", s);
//            Log.d("路线数据",s);
            cursor.close();
            cursor2.close();
            cursor4.close();
            hashMaps.add(map);
        }
        SimpleAdapter simpleAdapter = new SimpleAdapter(this, hashMaps,
                R.layout.check_up_list_05_item,
                new String[]{"Headman", "Member", "Address"},
                new int[]{R.id.tx_add_check_up_01, R.id.tx_add_check_up_02, R.id.tx_add_check_up_03});
        if (hashMaps.size() > 0) {
            lPeopleGroup.setVisibility(View.VISIBLE);
            txAddCheckUp01.setText(hashMaps.get(0).get("Headman").toString());
            listPeopleGroup.setAdapter(simpleAdapter);
            Utility.setListViewHeightBasedOnChildren(listPeopleGroup);
            listPeopleGroup.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    dialogDoneDelete();


                }
            });
        }
        cursor1.close();
//        } catch (Exception e) {
//            Log.e("数据库报错", e.toString());
//        }
    }

    /**
     * 弹出无法删除对话框（偷懒了这是）
     */
    protected void dialogDoneDelete() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("无法删除");
        builder.setMessage("此状态下无法删除分组。");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

}
