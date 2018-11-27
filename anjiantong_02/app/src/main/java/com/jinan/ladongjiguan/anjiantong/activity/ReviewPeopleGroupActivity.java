package com.jinan.ladongjiguan.anjiantong.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.jinan.ladongjiguan.anjiantong.PublicClass.AssetsDatabaseManager;
import com.jinan.ladongjiguan.anjiantong.PublicClass.CustomDialog;
import com.jinan.ladongjiguan.anjiantong.utils.SharedPreferencesUtil;
import com.jinan.ladongjiguan.anjiantong.PublicClass.Utility;
import com.jinan.ladongjiguan.anjiantong.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ReviewPeopleGroupActivity extends BaseActivity implements View.OnTouchListener, View.OnClickListener {

    @BindView(R.id.examine_page_back)
    LinearLayout examinePageBack;
    @BindView(R.id.title_layout)
    TextView titleLayout;
    @BindView(R.id.list_people_group)
    ListView listPeopleGroup;
    @BindView(R.id.l_people_group)
    LinearLayout lPeopleGroup;
    @BindView(R.id.tx_group_leader)
    TextView txGroupLeader;
    @BindView(R.id.tx_group_peoples)
    TextView txGroupPeoples;
    @BindView(R.id.l_line)
    ListView lLine;
    @BindView(R.id.bt_group_add)
    Button btGroupAdd;
    @BindView(R.id.bt_people_group_00)
    Button btPeopleGroup00;
    @BindView(R.id.bt_people_group_01)
    Button btPeopleGroup01;
    @BindView(R.id.l_add_people_group)
    LinearLayout lAddPeopleGroup;
    String nameMembers = "";
    String nameHeadman = "";
    String nameHeadmanID = SharedPreferencesUtil.getStringData(this,"userId","");
    String nameMembersID = "";
    public MyAdapter adapter;
    @BindView(R.id.tx_add_check_up_01)
    TextView txAddCheckUp01;
    @BindView(R.id.l_group_leader)
    LinearLayout lGroupLeader;
    private ArrayList<HashMap<String, Object>> hashMaps = new ArrayList<>();//已有人员分组

    @Override
    protected void initView() {
        setContentView(R.layout.review_people_group_layout);
        ButterKnife.bind(this);
        titleLayout.setText("制定复查计划分组");
        adapter = new MyAdapter(this);
        txGroupLeader.setText(SharedPreferencesUtil.getStringData(this,"Account",""));
        nameHeadman = SharedPreferencesUtil.getStringData(this,"Account","");
    }

    @Override
    protected void init() {
        examinePageBack.setOnClickListener(this);
        examinePageBack.setOnTouchListener(this);
        btPeopleGroup00.setOnClickListener(this);
        btGroupAdd.setOnClickListener(this);
        lLine.setAdapter(adapter);
        Utility.setListViewHeightBasedOnChildren(lLine);
//        txGroupLeader.setOnClickListener(this);
//        txGroupLeader.setOnTouchListener(this);
        txGroupPeoples.setOnTouchListener(this);
        txGroupPeoples.setOnClickListener(this);
        btPeopleGroup01.setOnClickListener(this);
        openPeopleGroupDate();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.examine_page_back://返回键
                onBackPressed();
                break;
            case R.id.bt_people_group_00://取消键
                onBackPressed();
                break;
            case R.id.tx_group_leader://选择组长
                dialog(v, "组长");
                break;
            case R.id.tx_group_peoples://选择组员
                dialog(v, "组员");
                break;
            case R.id.bt_group_add:
                adapter.arr.add("");
                adapter.arr01.add("");
                adapter.notifyDataSetChanged();
                init();
                break;
            case R.id.bt_people_group_01:
                if (nameHeadmanID.length() > 0 && nameMembersID.length() > 0
                        && adapter.arr.size() > 0 ) {
                    Toast.makeText(this, "已保存", Toast.LENGTH_SHORT).show();

                    saveGroup(getIntent().getStringExtra("ReviewId"), nameHeadmanID, nameMembersID, "1");

                } else {
                    Toast.makeText(this, "请选择随行人员", Toast.LENGTH_SHORT).show();
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
        setResult(0);
        finish();
        overridePendingTransition(R.anim.zoomin, R.anim.zoomout);

    }

    protected void dialog(View view, final String s) {
        CustomDialog.Builder builder = new CustomDialog.Builder(this);
        builder.setTitle("请选择" + s);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

            }

        });

        builder.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.setMessage(nameHeadmanID);
        builder.create(new CustomDialog.DataBackListener() {
            @Override
            public void getData(String data, String id) {
                //设置你的操作事项
                if (s.equals("组长")) {
                    nameHeadman = data;
                    nameHeadmanID = id;
                    txGroupLeader.setText(data);
                } else {

                    if (nameMembers.length() > 0 && nameMembersID.length() > 0) {
                        String[] temp = null;
                        temp = nameMembersID.split(",");
                        for (int i = 0; i < temp.length; i++) {
                            if (!temp[i].equals(id) && i == temp.length - 1 && !nameHeadmanID.equals(id)) {
                                nameMembers = nameMembers + "," + data;
                                nameMembersID = nameMembersID + "," + id;
                            }
                        }

                    } else {
                        nameMembers = data;
                        nameMembersID = id;
                    }

                    txGroupPeoples.setText(nameMembers);
                }
            }
        }).show();
    }
    /**
     * 添加隐患地点适配器
     */
    private class MyAdapter extends BaseAdapter {

        private Context context;
        private LayoutInflater inflater;
        public ArrayList<String> arr;
        public ArrayList<String> arr01;

        public MyAdapter(Context context) {
            super();
            this.context = context;
            inflater = LayoutInflater.from(context);
            arr = new ArrayList<String>();
            arr01 = new ArrayList<String>();
            for (int i = 0; i < 1; i++) {    //listview初始化1个子项
                arr.add("");
                arr01.add("");
            }
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return arr.size();
        }

        @Override
        public Object getItem(int arg0) {
            // TODO Auto-generated method stub
            return arg0;
        }

        @Override
        public long getItemId(int arg0) {
            // TODO Auto-generated method stub
            return arg0;
        }

        @Override
        public View getView(final int position, View view, ViewGroup arg2) {
            // TODO Auto-generated method stub
            if (view == null) {
                view = inflater.inflate(R.layout.review_people_group_list_item, null);
            }
            final Spinner edit = (Spinner) view.findViewById(R.id.edit);
            Button del = (Button) view.findViewById(R.id.del);
            // 初始化，只需要调用一次
            AssetsDatabaseManager.initManager(getApplication());
            // 获取管理对象，因为数据库需要通过管理对象才能够获取
            AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
            // 通过管理对象获取数据库
            SQLiteDatabase db = mg.getDatabase("users.db");
            Cursor cursor = db.rawQuery("SELECT* FROM ELL_HiddenDanger WHERE ReviewId = ?",
                    new String[]{getIntent().getStringExtra("ReviewId")});
            final List<HashMap<String, Object>> list = new ArrayList<>();
            while (cursor.moveToNext()) {
                HashMap<String, Object> map = new HashMap<>();
                map.put("yhdd", cursor.getString(cursor.getColumnIndex("yhdd")));
                map.put("HiddenDangerId", cursor.getString(cursor.getColumnIndex("HiddenDangerId")));
                map.put("ReviewId", cursor.getString(cursor.getColumnIndex("ReviewId")));
                list.add(map);
            }
            cursor.close();
            SimpleAdapter simpleAdapter = new SimpleAdapter(ReviewPeopleGroupActivity.this, list,
                    R.layout.login_spinner_item,
                    new String[]{"yhdd"},
                    new int[]{R.id.text});
            edit.setAdapter(simpleAdapter);
            for (int i = 0; i < list.size(); i++) {
                if (arr.size() > 0 && arr.get(position).equals(list.get(i).get("yhdd").toString())) {
                    edit.setSelection(i);
                    break;
                }
            }
            edit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position1, long id) {
                    arr.set(position, list.get(position1).get("yhdd").toString());
                    arr01.set(position, list.get(position1).get("HiddenDangerId").toString());
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            del.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    // TODO Auto-generated method stub
                    //从集合中删除所删除项的EditText的内容
                    arr.remove(position);
                    arr01.remove(position);
                    adapter.notifyDataSetChanged();
                    init();
                }
            });
            return view;
        }
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

    /**
     * 删除数据
     */
    protected void deleterDate(String s) {
        // 初始化，只需要调用一次
        AssetsDatabaseManager.initManager(getApplication());
        // 获取管理对象，因为数据库需要通过管理对象才能够获取
        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
        // 通过管理对象获取数据库
        SQLiteDatabase db = mg.getDatabase("users.db");
        try {
            db.delete("ELL_ReviewGroupInfo", "GroupId = ?", new String[]{s});
            Toast.makeText(this, "已删除", Toast.LENGTH_SHORT).show();
            init();
        } catch (Exception e) {
            Log.e("删除计划数据库报错", e.toString());
        }
    }

    /**
     * 保存分组
     */
    protected void saveGroup(String s, String headmanID, String members, String state) {
        // 初始化，只需要调用一次
        AssetsDatabaseManager.initManager(getApplication());
        // 获取管理对象，因为数据库需要通过管理对象才能够获取
        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
        // 通过管理对象获取数据库
        SQLiteDatabase db = mg.getDatabase("users.db");
        switch (state) {
            case "1"://分组信息与计划一同保存
                try {
                    String GroupId = UUID.randomUUID().toString();//分组主键
                    db.execSQL("REPLACE INTO ELL_ReviewGroupInfo VALUES('" +
                            GroupId + "','" +
                            s + "','" +
                            headmanID + "')");
                    String[] strings = members.split(",");
                    for (int i = 0; i < strings.length; i++) {
                        saveMember(GroupId, strings[i]);
                    }

                    for (int x = 0; x < adapter.arr.size(); x++) {
                        if (!adapter.arr01.get(x).isEmpty()) {
                            saveReviewDangerInfo(GroupId, adapter.arr01.get(x));
                        }

                    }
                    Log.d("保存分组数据", "REPLACE INTO ELL_ReviewGroupInfo VALUES('" +
                            GroupId + "','" +
                            s + "','" +
                            headmanID + "')");
                    setResult(1);
                    finish();
                } catch (Exception e) {
                    Log.e("数据库报错", e.toString());
                }
                break;
            case "2"://分组信息在修改中保存
                break;
            default:
                break;

        }

    }

    protected void saveMember(String gourp, String users) {
        // 初始化，只需要调用一次
        AssetsDatabaseManager.initManager(getApplication());
        // 获取管理对象，因为数据库需要通过管理对象才能够获取
        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
        // 通过管理对象获取数据库
        SQLiteDatabase db = mg.getDatabase("users.db");
        try {
            String memberId = UUID.randomUUID().toString();//分组主键
            db.execSQL("REPLACE INTO ELL_ReviewMember VALUES('" +
                    memberId + "','" +
                    gourp + "','" +
                    users + "')");
            Log.d("保存组员数据", "REPLACE INTO ELL_ReviewMember VALUES('" +
                    memberId + "','" +
                    gourp + "','" +
                    users + "')");

        } catch (Exception e) {
            Log.e("数据库报错", e.toString());
        }

    }

    /**
     * 保存隐患与分组的关联（用于版本管理）
     */
    protected void saveReviewDangerInfo(String GroupId, String HiddenDangerId) {
        // 初始化，只需要调用一次
        AssetsDatabaseManager.initManager(getApplication());
        // 获取管理对象，因为数据库需要通过管理对象才能够获取
        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
        // 通过管理对象获取数据库
        SQLiteDatabase db = mg.getDatabase("users.db");
        try {
            String ReviewDangerInfo = UUID.randomUUID().toString();//分组主键
            db.execSQL("REPLACE INTO ELL_ReviewDangerInfo VALUES('" +
                    ReviewDangerInfo + "','" +
                    HiddenDangerId + "','" +
                    GroupId + "')");
            Log.d("保存隐患与分组的关联数据", "REPLACE INTO ELL_ReviewDangerInfo VALUES('" +
                    ReviewDangerInfo + "','" +
                    HiddenDangerId + "','" +
                    GroupId + "')");

        } catch (Exception e) {
            Log.e("保存隐患与分组的关联数据库报错", e.toString());
        }
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
        hashMaps = new ArrayList<>();
        while (cursor1.moveToNext()) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("GroupId", cursor1.getString(cursor1.getColumnIndex("GroupId")));

            Cursor cursor2 = db.rawQuery("SELECT* FROM Base_User WHERE UserId = ?",
                    new String[]{cursor1.getString(cursor1.getColumnIndex("Headman"))});
            cursor2.moveToFirst();
            map.put("Headman", cursor2.getString(cursor2.getColumnIndex("RealName")));
            map.put("HeadmanId", cursor2.getString(cursor2.getColumnIndex("UserId")));
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
                R.layout.check_up_list_03_item,
                new String[]{"Headman", "Member", "Address"},
                new int[]{R.id.tx_add_check_up_01, R.id.tx_add_check_up_02, R.id.tx_add_check_up_03});
        if (hashMaps.size() > 0) {
            lPeopleGroup.setVisibility(View.VISIBLE);
            lGroupLeader.setVisibility(View.GONE);
            txAddCheckUp01.setText(hashMaps.get(0).get("Headman").toString());
            nameHeadman = hashMaps.get(0).get("Headman").toString();
            nameHeadmanID = hashMaps.get(0).get("HeadmanId").toString();
            txGroupLeader.setText(hashMaps.get(0).get("Headman").toString());
            listPeopleGroup.setAdapter(simpleAdapter);
            Utility.setListViewHeightBasedOnChildren(listPeopleGroup);
            listPeopleGroup.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    dialogGroup(hashMaps.get(position).get("GroupId").toString());


                }
            });
        }
        cursor1.close();
//        } catch (Exception e) {
//            Log.e("数据库报错", e.toString());
//        }
    }

    /**
     * 弹出确认对话框
     */
    protected void dialogGroup(final String s) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("确认删除");
        builder.setMessage("是否删除分组 ？");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                deleterDate(s);
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Toast.makeText(ReviewPeopleGroupActivity.this, "已取消", Toast.LENGTH_SHORT).show();
            }
        });
        builder.show();
    }
}
