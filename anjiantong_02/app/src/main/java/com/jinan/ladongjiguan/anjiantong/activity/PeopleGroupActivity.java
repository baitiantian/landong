package com.jinan.ladongjiguan.anjiantong.activity;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.jinan.ladongjiguan.anjiantong.PublicClass.AssetsDatabaseManager;
import com.jinan.ladongjiguan.anjiantong.PublicClass.CustomDialog;
import com.jinan.ladongjiguan.anjiantong.utils.SharedPreferencesUtil;
import com.jinan.ladongjiguan.anjiantong.PublicClass.Utility;
import com.jinan.ladongjiguan.anjiantong.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;


public class PeopleGroupActivity extends BaseActivity implements View.OnClickListener, View.OnTouchListener {


    @BindView(R.id.examine_page_back)
    LinearLayout examinePageBack;
    @BindView(R.id.title_layout)
    TextView titleLayout;
    @BindView(R.id.tx_group_leader)
    TextView txGroupLeader;
    @BindView(R.id.tx_group_peoples)
    TextView txGroupPeoples;
    @BindView(R.id.bt_people_group_00)
    Button btPeopleGroup00;
    @BindView(R.id.bt_people_group_01)
    Button btPeopleGroup01;
    String nameMembers = "";
    String nameHeadman = "";
    String nameHeadmanID = SharedPreferencesUtil.getStringData(this, "userId", "");
    String nameMembersID = "";
    @BindView(R.id.delete_layout)
    LinearLayout deleteLayout;
    @BindView(R.id.l_line)
    ListView lLine;
    @BindView(R.id.bt_group_add)
    Button btGroupAdd;
    public MyAdapter adapter;
    public MemberAdapter adapterMember;
    @BindView(R.id.list_people_group)
    ListView listPeopleGroup;
    @BindView(R.id.l_people_group)
    LinearLayout lPeopleGroup;
    @BindView(R.id.l_add_people_group)
    LinearLayout lAddPeopleGroup;
    @BindView(R.id.tx_line)
    TextView txLine;
    @BindView(R.id.tx_add_check_up_01)
    TextView txAddCheckUp01;
    @BindView(R.id.l_group_leader)
    LinearLayout lGroupLeader;
    @BindView(R.id.list_members)
    ListView listMembers;
    @BindView(R.id.bt_member_add)
    Button btMemberAdd;
    private String s = "";
    private ArrayList<HashMap<String, Object>> hashMaps = new ArrayList<>();//已有人员分组
    private Intent intent = new Intent();
    private TextView textView;

    @Override
    protected void initView() {
        setContentView(R.layout.people_group_layout);
        ButterKnife.bind(this);
        /**
         * 设置标题
         * */
        titleLayout.setText("制定人员分组");
        adapter = new MyAdapter(this);
        adapterMember = new MemberAdapter(this);
        lPeopleGroup.setVisibility(View.GONE);
        txGroupLeader.setText(SharedPreferencesUtil.getStringData(this,"Account",""));
        nameHeadman = SharedPreferencesUtil.getStringData(this,"Account","");
    }

    @Override
    protected void init() {
        /**
         * 点击事件
         * */
        examinePageBack.setOnClickListener(this);
        btPeopleGroup00.setOnClickListener(this);
        btPeopleGroup01.setOnClickListener(this);
//        txGroupLeader.setOnClickListener(this);
        txGroupPeoples.setOnClickListener(this);
        btGroupAdd.setOnClickListener(this);
        btMemberAdd.setOnClickListener(this);
        txAddCheckUp01.setOnClickListener(this);
        /**
         * 触摸事件
         * */
        examinePageBack.setOnTouchListener(this);
//        txGroupLeader.setOnTouchListener(this);
        txGroupPeoples.setOnTouchListener(this);
        txAddCheckUp01.setOnTouchListener(this);
        /**
         * 适配列表
         * */

        lLine.setAdapter(adapter);
        Utility.setListViewHeightBasedOnChildren(lLine);
        listMembers.setAdapter(adapterMember);
        Utility.setListViewHeightBasedOnChildren(listMembers);
        s = "";
        for (int i = 0; i < adapter.arr.size(); i++) {
            if (i == 0&&adapter.arr.get(i).length()>0) {
                s = adapter.arr.get(i);
            } else if(adapter.arr.get(i).length()>0){
                s = s + "-->" + adapter.arr.get(i);
            }
        }
        textView = (TextView) findViewById(R.id.tx_line);
        textView.setText(s);
        if (getIntent().getStringExtra("state").equals("0")) {
            lAddPeopleGroup.setVisibility(View.GONE);
            titleLayout.setText("查看人员分组");
        }
        openPeopleGroupDate();

    }

    @Override
    public void onClick(View v) {
        nameHeadmanID = SharedPreferencesUtil.getStringData(this, "userId", "");

        switch (v.getId()) {
            case R.id.examine_page_back:
                onBackPressed();
                break;
            case R.id.bt_people_group_00:
                onBackPressed();
                Toast.makeText(this, "已取消", Toast.LENGTH_SHORT).show();
                break;
            case R.id.bt_people_group_01:
                s = adapter.arr.get(0);
                for (int i=0;i<adapterMember.arr.size();i++){
                    if(i==0){
                        nameMembersID = adapterMember.arr.get(i).get("memberId");
                    }else {
                        nameMembersID = nameMembersID+","+adapterMember.arr.get(i).get("memberId");
                    }
                }
                if (nameHeadmanID.length() > 0 && nameMembersID.length() > 0
                        && adapter.arr.size() > 0 && s.length() > 0) {
                    Toast.makeText(this, "已保存", Toast.LENGTH_SHORT).show();

                    intent.putExtra("nameHeadmanID", nameHeadmanID);
                    intent.putExtra("nameMembersID", nameMembersID);

                    intent.putExtra("Address", textView.getText().toString());
                    intent.putExtra("nameHeadman", nameHeadman + "组");

                    saveGroup(getIntent().getStringExtra("id"), nameHeadmanID, textView.getText().toString(), nameMembersID, "1");

                } else {
                    Toast.makeText(this, "请完善信息", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.tx_group_leader://选择组长
                dialog(v, "组长");
                break;
            case R.id.tx_group_peoples://选择组员
                if(nameHeadmanID.length()>0){
                    dialog(v, "组员");
                }else {
                    Toast.makeText(this, "请先选择组长", Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.bt_group_add:
                adapter.arr.add("");
                adapter.notifyDataSetChanged();
                init();
                break;
            case R.id.bt_member_add:

                if(nameHeadman.length()>0){
                    dialog(v, "组员");
                }else {
                    Toast.makeText(this, "请先选择组长", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.tx_add_check_up_01://修改组长
                dialogModify();
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
        setResult(1);
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

        builder.setMessage(nameHeadmanID);


        builder.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        Toast.makeText(this, "请先选择"+s, Toast.LENGTH_SHORT).show();
        builder.create(new CustomDialog.DataBackListener() {
            @Override
            public void getData(String data, String id) {
                //设置你的操作事项
                if (s.equals("组长")) {
                    nameHeadman = data;
                    nameHeadmanID = id;
                    txGroupLeader.setText(data);
                } else {

//                    if (nameMembers.length() > 0 && nameMembersID.length() > 0) {
//                        String[] temp = null;
//                        temp = nameMembersID.split(",");
//                        for (int i = 0; i < temp.length; i++) {
//                            if (!temp[i].equals(id) && i == temp.length - 1 && !nameHeadmanID.equals(id)) {
//                                nameMembers = nameMembers + "," + data;
//                                nameMembersID = nameMembersID + "," + id;
//                            }
//                        }
//
//                    } else {
//                        nameMembers = data;
//                        nameMembersID = id;
//                    }

//                    txGroupPeoples.setText(nameMembers);
                    HashMap<String,String> map = new HashMap<String, String>();
                    if(adapterMember.arr.size()>0){
                        for(int i = 0;i<adapterMember.arr.size();i++){
                            if(id.equals(nameHeadmanID)||id.equals(adapterMember.arr.get(i).get("memberId"))){
                                break;
                            }else if(i==adapterMember.arr.size()-1){
                                map.put("member",data);
                                map.put("memberId",id);
                                adapterMember.arr.add(map);
                                adapterMember.notifyDataSetChanged();
                                init();
                            }
                        }
                    }else if(!id.equals(nameHeadmanID)){
                        map.put("member",data);
                        map.put("memberId",id);
                        adapterMember.arr.add(map);
                        adapterMember.notifyDataSetChanged();
                        init();
                    }



                }
            }
        }).show();


    }
    /**
     * 修改组长
     * */

    protected void dialogModify(){
        CustomDialog.Builder builder = new CustomDialog.Builder(this);
        builder.setTitle("请选择新组长" );
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setMessage(nameHeadmanID);
        builder.setNegativeButton("取消",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {dialog.dismiss();
                    }
                });
        builder.create(new CustomDialog.DataBackListener() {
            @Override
            public void getData(String data, String id) {
                nameHeadman = data;
                nameHeadmanID = id;
//                Log.d("数据",data);
                txGroupLeader.setText(data);
                txAddCheckUp01.setText(data);
//                Log.d("数据",txAddCheckUp01.getText().toString());
                // 初始化，只需要调用一次
                AssetsDatabaseManager.initManager(getApplication());
                // 获取管理对象，因为数据库需要通过管理对象才能够获取
                AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
                // 通过管理对象获取数据库
                SQLiteDatabase db = mg.getDatabase("users.db");
                try {
                    Cursor cursor = db.rawQuery("SELECT* FROM ELL_GroupInfo WHERE PlanId = ?",
                            new String[]{getIntent().getStringExtra("id")});
                    while (cursor.moveToNext()){
                        ContentValues values  = new ContentValues();
                        values.put("GroupId",cursor.getString(cursor.getColumnIndex("GroupId")));
                        values.put("PlanId",getIntent().getStringExtra("id"));
                        values.put("Headman",nameHeadmanID);
                        values.put("Address",cursor.getString(cursor.getColumnIndex("Address")));
                        db.replace("ELL_GroupInfo",null,values);
                    }
                    cursor.close();
                }catch (Exception e){
                    Log.e("修改组长数据库报错",e.toString());
                }
                init();
            }
        }).show();
    }
    private class MyAdapter extends BaseAdapter {

        private Context context;
        private LayoutInflater inflater;
        public ArrayList<String> arr;

        public MyAdapter(Context context) {
            super();
            this.context = context;
            inflater = LayoutInflater.from(context);
            arr = new ArrayList<String>();
            for (int i = 0; i < 1; i++) {    //listview初始化3个子项
                arr.add("");
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
                view = inflater.inflate(R.layout.people_group_list_item, null);
            }
            final EditText edit = (EditText) view.findViewById(R.id.edit);
            edit.setText(arr.get(position));    //在重构adapter的时候不至于数据错乱
            Button del = (Button) view.findViewById(R.id.del);
//            edit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//                @Override
//                public void onFocusChange(View v, boolean hasFocus) {
//                    if (arr.size() > 0) {
//                        arr.set(position, edit.getText().toString());
//                    }
//                }
//            });
            edit.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    arr.set(position, edit.getText().toString());
                    String s1 = "";
                    for (int i = 0; i < adapter.arr.size(); i++) {
                        if (i == 0) {
                            s1 = adapter.arr.get(i);
                        } else {
                            s1 = s1 + "-->" + adapter.arr.get(i);
                        }
                    }
                    TextView textView = (TextView) findViewById(R.id.tx_line);
                    textView.setText(s1);
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            del.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    // TODO Auto-generated method stub
                    //从集合中删除所删除项的EditText的内容
                    arr.remove(position);
                    adapter.notifyDataSetChanged();
                    init();
                }
            });
            return view;
        }
    }
    private class MemberAdapter extends BaseAdapter {

        private Context context;
        private LayoutInflater inflater;
        public ArrayList<HashMap<String,String>> arr;

        public MemberAdapter(Context context) {
            super();
            this.context = context;
            inflater = LayoutInflater.from(context);
            arr = new ArrayList<>();
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
                view = inflater.inflate(R.layout.people_group_members_list_item, null);
            }
            final TextView textView = (TextView) view.findViewById(R.id.text_member);
            textView.setText(arr.get(position).get("member"));    //在重构adapter的时候不至于数据错乱
            TextView textView1 = (TextView)view.findViewById(R.id.text_member_id);
            textView1.setText(arr.get(position).get("memberId"));
            Button del = (Button) view.findViewById(R.id.button_del_member);


            del.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    // TODO Auto-generated method stub
                    //从集合中删除所删除项的EditText的内容
                    arr.remove(position);
                    adapterMember.notifyDataSetChanged();
                    init();
                }
            });
            return view;
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
        try {
            Cursor cursor1 = db.rawQuery("SELECT* FROM ELL_GroupInfo WHERE PlanId = ?",
                    new String[]{getIntent().getStringExtra("id")});
            hashMaps = new ArrayList<>();
            while (cursor1.moveToNext()) {
                HashMap<String, Object> map = new HashMap<>();
                map.put("GroupId", cursor1.getString(cursor1.getColumnIndex("GroupId")));
                map.put("Address", cursor1.getString(cursor1.getColumnIndex("Address")));
                map.put("HeadmanId", cursor1.getString(cursor1.getColumnIndex("Headman")));
                Cursor cursor2 = db.rawQuery("SELECT* FROM Base_User WHERE UserId = ?",
                        new String[]{cursor1.getString(cursor1.getColumnIndex("Headman"))});
                cursor2.moveToFirst();
                map.put("Headman", cursor2.getString(cursor2.getColumnIndex("RealName")));
                Cursor cursor = db.rawQuery("SELECT* FROM ELL_Member WHERE GroupId = ?",
                        new String[]{cursor1.getString(cursor1.getColumnIndex("GroupId"))});
                int i = 0;
                String s2 = "";
                while (cursor.moveToNext()) {
                    Cursor cursor3 = db.rawQuery("SELECT* FROM Base_User WHERE UserId = ?",
                            new String[]{cursor.getString(cursor.getColumnIndex("Member"))});
                    cursor3.moveToFirst();
                    if (i == 0) {
                        Log.d("Member的数据0", cursor.getString(cursor.getColumnIndex("Member")));
                        s2 = cursor3.getString(cursor3.getColumnIndex("RealName"));
                    } else {
                        s2 = s2 + "," + cursor3.getString(cursor3.getColumnIndex("RealName"));
                    }
                    i++;
                    cursor3.close();
                }
                map.put("Member", s2);

                cursor.close();
                cursor2.close();
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
                        if (getIntent().getStringExtra("state").equals("0")) {
                            dialogDoneDelete();
                        } else {
                            dialogGroup(hashMaps.get(position).get("GroupId").toString());
                        }

                    }
                });
            }else {
                lPeopleGroup.setVisibility(View.GONE);
                lGroupLeader.setVisibility(View.VISIBLE);
            }
            cursor1.close();
        } catch (Exception e) {
            Log.e("数据库报错", e.toString(),e);
        }
    }

    /**
     * 弹出无法删除对话框（偷懒了这是）
     */
    protected void dialogDoneDelete() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("无法删除或修改");
        builder.setMessage("此状态下无法删除或修改分组。");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    /**
     * 弹出确认对话框
     */
    protected void dialogGroup(final String s) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("确认删除或修改分组");
        builder.setMessage("请选择删除或修改分组");
        builder.setPositiveButton("删除", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                deleterDate(s);
            }
        });
        builder.setNegativeButton("修改", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
//                Toast.makeText(PeopleGroupActivity.this, "已取消", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.setClass(PeopleGroupActivity.this,PeopleGroupModifyActivity.class);
                intent.putExtra("GroupId",s);
                intent.putExtra("id",getIntent().getStringExtra("id"));
                startActivityForResult(intent,0);
                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
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
            db.delete("ELL_GroupInfo", "GroupId = ?", new String[]{s});
            db.delete("ELL_Member", "GroupId = ?", new String[]{s});
            Toast.makeText(this, "已删除", Toast.LENGTH_SHORT).show();
            init();
        } catch (Exception e) {
            Log.e("删除计划数据库报错", e.toString());
        }
    }

    /**
     * 保存分组
     */
    protected void saveGroup(String s, String headmanID, String address, String members, String state) {
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
                    db.execSQL("REPLACE INTO ELL_GroupInfo VALUES('" +
                            GroupId + "','" +
                            s + "','" +
                            headmanID + "','" +
                            address + "')");
                    String[] strings = members.split(",");
                    for (int i = 0; i < strings.length; i++) {
                        saveMember(GroupId, strings[i]);
                    }
//                    Log.d("保存分组数据", "REPLACE INTO ELL_GroupInfo VALUES('" +
//                            GroupId + "','" +
//                            headmanID + "','" +
//                            s + "','" + address + "')");
                    setResult(0, intent);
                    finish();
                    overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                } catch (Exception e) {
                    Log.e("数据库报错", e.toString(),e);
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
            db.execSQL("REPLACE INTO ELL_Member VALUES('" +
                    memberId + "','" +
                    gourp + "','" +
                    users + "')");
//            Log.d("保存组员数据", "REPLACE INTO ELL_Member VALUES('" +
//                    memberId + "','" +
//                    gourp + "','" +
//                    users + "')");

        } catch (Exception e) {
            Log.e("数据库报错", e.toString(),e);
        }

    }
    /**
     * 修改分组后返回
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 0:
                if (resultCode == 0) {
                    init();
                }
                break;

            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
