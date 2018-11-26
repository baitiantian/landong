package com.jinan.ladongjiguan.djj8plus.activity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jinan.ladongjiguan.djj8plus.R;
import com.jinan.ladongjiguan.djj8plus.adapter.ChooseAdapter;
import com.jinan.ladongjiguan.djj8plus.bean.ChooseBean;
import com.jinan.ladongjiguan.djj8plus.dialog.DialogNormalDialog;
import com.jinan.ladongjiguan.djj8plus.publicClass.AssetsDatabaseManager;
import com.jinan.ladongjiguan.djj8plus.publicClass.SwipeItemLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChooseActivity extends BaseActivity {

    @BindView(R.id.activity_choose_srefresh)
    SwipeRefreshLayout mSwipe;
    @BindView(R.id.activity_choose_recycler)
    RecyclerView mRecyclerView;
    @BindView(R.id.title_layout)
    TextView mTitleText;
    @BindView(R.id.examine_page_back)
    LinearLayout mBack;
    @BindView(R.id.r_activity_choose)
    RelativeLayout mRl;
    @BindView(R.id.l_data_main_title)
    LinearLayout lDataMainTitle;

    private List<ChooseBean> mData_Route = new ArrayList<>();
    private List<ChooseBean> mDatas_Route = new ArrayList<>();
    private List<ChooseBean> mData_Area = new ArrayList<>();
    private List<ChooseBean> mDatas_Area = new ArrayList<>();
    private List<ChooseBean> mData_Section = new ArrayList<>();
    private List<ChooseBean> mDatas_Section = new ArrayList<>();
    private List<ChooseBean> mData_Tunnel = new ArrayList<>();
    private List<ChooseBean> mDatas_Tunnel = new ArrayList<>();
    private ChooseAdapter mAdapter;
    private int mType_flag;

    public static final int ADD_NEW_ROUTE = 0;
    public static final int ADD_NEW_AREA = 1;
    public static final int ADD_NEW_SECTION = 2;
    public static final int ADD_NEW_TUNNEL = 3;
    public static final int CHOOSE_BACK_HOME = 1010;
    public static int CHOOSE_BACK_HOME_EDIT_DELETE = 1011;
    public static int CHOOSE_BACK_HOME_DELETE = 1100;
    public static String CHOOSE_ROUTEID = "choose_routeid";
    public static String CHOOSE_AREAID = "choose_areaid";
    public static String CHOOSE_SECTIONID = "choose_sectionid";
    public static String CHOOSE_TUNNELID = "choose_tunnelid";
    public static String CHOOSE_TYPE = "choose_type";
    public static String CHOOSE_EDIT_DELETE = "choose_edit";
    public static String CHOOSE_EDIT_DELETE_TYPE = "choose_edit_type";


    //    private String mCurrentId;
    private String mChoosed_routeId;
    private String mChoosed_areaId;
    private String mChoosed_sectionId;
    private String mChoosed_tunnelId;
    private SQLiteDatabase mDb;
    private String TAG = ChooseActivity.class.getSimpleName();

    @Override
    protected void initView() {
        setContentView(R.layout.activity_choose);
        ButterKnife.bind(this);
        mType_flag = getIntent().getIntExtra("TYPE_FLAG", 0);
        mChoosed_routeId = getIntent().getStringExtra(CHOOSE_ROUTEID) == null ? "" : getIntent().getStringExtra(CHOOSE_ROUTEID);
        Log.e(TAG, "mType_flag为" + mType_flag + "；mChoosed_routeId为" + mChoosed_routeId);
        mChoosed_areaId = getIntent().getStringExtra(CHOOSE_AREAID) == null ? "" : getIntent().getStringExtra(CHOOSE_AREAID);
        Log.e(TAG, "mType_flag为" + mType_flag + "；mChoosed_areaId为" + mChoosed_areaId);
        mChoosed_sectionId = getIntent().getStringExtra(CHOOSE_SECTIONID) == null ? "" : getIntent().getStringExtra(CHOOSE_SECTIONID);
        Log.e(TAG, "mType_flag为" + mType_flag + "；mChoosed_sectionId为" + mChoosed_sectionId);
        mChoosed_tunnelId = getIntent().getStringExtra(CHOOSE_TUNNELID) == null ? "" : getIntent().getStringExtra(CHOOSE_TUNNELID);
        Log.e(TAG, "mType_flag为" + mType_flag + "；mChoosed_tunnelId为" + mChoosed_tunnelId);
        setBackGroudAndData(mType_flag);
        setView();
    }

    private void setBackGroudAndData(int type) {
        // 初始化，只需要调用一次
        AssetsDatabaseManager.initManager(ChooseActivity.this);
        // 获取管理对象，因为数据库需要通过管理对象才能够获取
        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
        // 通过管理对象获取数据库
        mDb = mg.getDatabase("DJJ-8data.db");
        Cursor cursor;
        try {
            switch (type) {
                case ADD_NEW_ROUTE:
                    mTitleText.setText("选择路线");
                    mDatas_Area.clear();
                    mDatas_Section.clear();
                    mDatas_Tunnel.clear();
//                    mRl.setBackground(getResources().getDrawable(R.drawable.bg_dialog_new_route));
                    //获取路线信息
                    cursor = mDb.rawQuery("SELECT* FROM ELL_Route", null);
                    while (cursor.moveToNext()) {
                        ChooseBean chooseBean1 = new ChooseBean();
                        chooseBean1.id = cursor.getString(cursor.getColumnIndex("RouteId"));
                        chooseBean1.name = cursor.getString(cursor.getColumnIndex("RouteName"));
                        String routeId_route = chooseBean1.id;
                        String routeName_route = chooseBean1.name;
                        Log.e(TAG, "routeName_route为" + routeName_route);
                        mData_Route.add(chooseBean1);
                    }
                    mDatas_Route.addAll(mData_Route);
                    cursor.close();
                    mAdapter = new ChooseAdapter(ChooseActivity.this, mDatas_Route, type);
                    break;
                case ADD_NEW_AREA:
                    mTitleText.setText("选择工区");
                    mDatas_Section.clear();
                    mDatas_Tunnel.clear();
//                    mRl.setBackground(getResources().getDrawable(R.drawable.bg_dialog_new_area));
                    //获取工区信息
                    if (mChoosed_routeId != "") {
                        cursor = mDb.rawQuery("SELECT* FROM ELL_Area WHERE RouteId = ?",
                                new String[]{mChoosed_routeId});
                        while (cursor.moveToNext()) {
                            ChooseBean chooseBean2 = new ChooseBean();
                            chooseBean2.id = cursor.getString(cursor.getColumnIndex("AreaId"));
                            String areaId_area = chooseBean2.id;
                            chooseBean2.name = cursor.getString(cursor.getColumnIndex("AreaName"));
                            String areaName_area = chooseBean2.name;
                            Log.e(TAG, "areaName_area为" + areaName_area);
                            mData_Area.add(chooseBean2);
                        }
                        cursor.close();
                        mDatas_Area.addAll(mData_Area);
                    } else {
                        Toast.makeText(ChooseActivity.this, "请选择上级选项", Toast.LENGTH_SHORT).show();
                    }

                    mAdapter = new ChooseAdapter(ChooseActivity.this, mDatas_Area, type);
                    break;
                case ADD_NEW_SECTION:
                    mTitleText.setText("选择区间");
                    mDatas_Tunnel.clear();
//                    mRl.setBackground(getResources().getDrawable(R.drawable.bg_dialog_new_section));
                    //获取区间信息
                    if (mChoosed_areaId != "") {
                        cursor = mDb.rawQuery("SELECT* FROM ELL_Section WHERE AreaId = ?",
                                new String[]{mChoosed_areaId});
                        while (cursor.moveToNext()) {
                            ChooseBean chooseBean3 = new ChooseBean();
                            chooseBean3.id = cursor.getString(cursor.getColumnIndex("SectionId"));
                            String sectionId_section = chooseBean3.id;
                            chooseBean3.name = cursor.getString(cursor.getColumnIndex("SectionName"));
                            String sectionName_section = chooseBean3.name;
                            Log.e(TAG, "sectionName_section为" + sectionName_section);
                            mData_Section.add(chooseBean3);
                        }
                        cursor.close();
                        mDatas_Section.addAll(mData_Section);
                    } else {
                        Toast.makeText(ChooseActivity.this, "请选择上级选项", Toast.LENGTH_SHORT).show();
                    }
                    mAdapter = new ChooseAdapter(ChooseActivity.this, mData_Section, type);
                    break;
                case ADD_NEW_TUNNEL:
                    mTitleText.setText("选择隧道");
//                    mRl.setBackground(getResources().getDrawable(R.drawable.bg_dialog_new_tunnel));
                    //获取隧道信息
                    if (mChoosed_sectionId != "") {
                        cursor = mDb.rawQuery("SELECT* FROM ELL_Tunnel WHERE SectionId = ?",
                                new String[]{mChoosed_sectionId});
                       /* ChooseBean chooseBean = new ChooseBean();
                        chooseBean.id = "";
                        chooseBean.name = "无";
                        mData_Tunnel.add(chooseBean);*/
                        Log.e(TAG+"-setBackGroudAndData","mData_Tunnel-1为"+mData_Tunnel.size());
                        while (cursor.moveToNext()) {
                            ChooseBean chooseBean4 = new ChooseBean();
                            chooseBean4.id = cursor.getString(cursor.getColumnIndex("TunnelId"));
                            String tunnelId_tunnel = chooseBean4.id;
                            chooseBean4.name = cursor.getString(cursor.getColumnIndex("TunnelName"));
                            String tunnelName_tunnel = chooseBean4.name;
                            Log.e(TAG, "tunnelName_tunnel为" + tunnelName_tunnel);
                            mData_Tunnel.add(chooseBean4);
                        }
                        Log.e(TAG+"-setBackGroudAndData","mData_Tunnel-2为"+mData_Tunnel.size());
                        cursor.close();
                        mDatas_Tunnel.addAll(mData_Tunnel);
                    } else {
                        Toast.makeText(ChooseActivity.this, "请选择上级选项", Toast.LENGTH_SHORT).show();
                    }
                    mAdapter = new ChooseAdapter(ChooseActivity.this, mDatas_Tunnel, type);
                    break;
            }
        } catch (Exception e) {
            Log.e(TAG + "查询数据库报错", e.toString(), e);
            Toast.makeText(ChooseActivity.this, "未知错误", Toast.LENGTH_SHORT).show();
        }
    }

    private void setView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(ChooseActivity.this));
        mRecyclerView.addOnItemTouchListener(new SwipeItemLayout.OnSwipeItemTouchListener(ChooseActivity.this));

        mAdapter.setClick(new ChooseAdapter.onChooseClick() {

            @Override
            public void setClick(String name, int position) {
//                Toast.makeText(ChooseActivity.this, "点击位置为：" + name, Toast.LENGTH_SHORT).show();
                getCurrentId(position);
//                Log.e(TAG, "id为" + mChoosed_id + "name为" + mChoosed_name);
                Intent intent = new Intent();
                switch (mType_flag) {
                    case ADD_NEW_ROUTE:
                        intent.putExtra(CHOOSE_ROUTEID, mChoosed_routeId);
                        break;
                    case ADD_NEW_AREA:
                        intent.putExtra(CHOOSE_ROUTEID, mChoosed_routeId);
                        intent.putExtra(CHOOSE_AREAID, mChoosed_areaId);
                        break;
                    case ADD_NEW_SECTION:
                        intent.putExtra(CHOOSE_ROUTEID, mChoosed_routeId);
                        intent.putExtra(CHOOSE_AREAID, mChoosed_areaId);
                        intent.putExtra(CHOOSE_SECTIONID, mChoosed_sectionId);
                        break;
                    case ADD_NEW_TUNNEL:
                        intent.putExtra(CHOOSE_ROUTEID, mChoosed_routeId);
                        intent.putExtra(CHOOSE_AREAID, mChoosed_areaId);
                        intent.putExtra(CHOOSE_SECTIONID, mChoosed_sectionId);
                        intent.putExtra(CHOOSE_TUNNELID, mChoosed_tunnelId);
                        break;
                }
                intent.putExtra(CHOOSE_TYPE, mType_flag);
                setResult(Activity.RESULT_OK, intent);
                finish();
                overridePendingTransition(0, R.anim.zoomout);
            }

            @Override
            public void setEditClick(String name, int position) {
                editDialog(mType_flag, position);
                Intent intent = new Intent();
                getCurrentId(position);
                intent.putExtra(CHOOSE_ROUTEID, mChoosed_routeId);
                intent.putExtra(CHOOSE_AREAID, mChoosed_areaId);
                intent.putExtra(CHOOSE_SECTIONID, mChoosed_sectionId);
                intent.putExtra(CHOOSE_TUNNELID, mChoosed_tunnelId);
                intent.putExtra(CHOOSE_EDIT_DELETE, CHOOSE_BACK_HOME_EDIT_DELETE);
                intent.putExtra(CHOOSE_EDIT_DELETE_TYPE, mType_flag);
                setResult(Activity.RESULT_OK, intent);
            }

            @Override
            public void setDeleteClick(String id, int position) {
                getCurrentId(position);
                deleteDateItem(mType_flag, position);
                Intent intent = new Intent();
                intent.putExtra(CHOOSE_EDIT_DELETE,CHOOSE_BACK_HOME_EDIT_DELETE);
                intent.putExtra(CHOOSE_EDIT_DELETE_TYPE, mType_flag);
                setResult(Activity.RESULT_OK, intent);
            }
        });
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(ChooseActivity.this, LinearLayoutManager.VERTICAL));
        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.activity_choose_srefresh);
        swipeRefreshLayout.setColorSchemeColors(Color.RED);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 2000);
            }
        });
        /*没有服务器之前，不启动刷新功能*/
        swipeRefreshLayout.setEnabled(false);

    }

    /**
     * 添加新路线弹出框
     */
    private void editDialog(final int type, final int position) {
        final DialogNormalDialog dialog = new DialogNormalDialog(ChooseActivity.this);
        final EditText editText = (EditText) dialog.getEditText();//方法在CustomDialog中实现
        dialog.editDialog(type);
        dialog.setMessage("");
        dialog.setOnPositiveListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setOnNegativeListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText.getText().toString().trim().length() > 0) {
                    ContentValues values = new ContentValues();
                    switch (type) {
                        case ADD_NEW_ROUTE:
                            values.put("RouteId", mDatas_Route.get(position).id);
                            values.put("RouteName", editText.getText().toString());
                            mDb.replace("ELL_Route", null, values);
                            break;
                        case ADD_NEW_AREA:
                            values.put("RouteId", mChoosed_routeId);
                            values.put("AreaId", mDatas_Area.get(position).id);
                            values.put("AreaName", editText.getText().toString());
                            mDb.replace("ELL_Area", null, values);
                            break;
                        case ADD_NEW_SECTION:
                            values.put("AreaId", mChoosed_areaId);
                            values.put("SectionId", mDatas_Section.get(position).id);
                            values.put("SectionName", editText.getText().toString());
                            mDb.replace("ELL_Section", null, values);
                            break;
                        case ADD_NEW_TUNNEL:
                            values.put("SectionId", mChoosed_sectionId);
                            values.put("TunnelId", mDatas_Tunnel.get(position).id);
                            values.put("TunnelName", editText.getText().toString());
                            mDb.replace("ELL_Tunnel", null, values);
                            break;
                    }
                    dialog.dismiss();
                    mAdapter.clearAfterToAdd(position, editText.getText().toString());
                } else {
                    Toast.makeText(ChooseActivity.this, "请输入", Toast.LENGTH_SHORT).show();
                }
            }
        });
        mAdapter.notifyDataSetChanged();
        dialog.show();
    }

    /**
     * 获得CurrentId
     */
    public void getCurrentId(int position) {
        switch (mType_flag) {
            case ADD_NEW_ROUTE:
                mChoosed_routeId = mDatas_Route.get(position).id;
                break;
            case ADD_NEW_AREA:
                mChoosed_areaId = mDatas_Area.get(position).id;
                break;
            case ADD_NEW_SECTION:
                mChoosed_sectionId = mDatas_Section.get(position).id;
                break;
            case ADD_NEW_TUNNEL:
                mChoosed_tunnelId = mDatas_Tunnel.get(position).id;
                break;
            default:
                break;
        }
//        return mCurrentId;
    }


    @Override
    protected void init() {

    }

    /**
     * 删除数据
     */
    protected void deleteDateItem(int type, int position) {
        Cursor cursor;
        Cursor cursor2;
        try {
            switch (type) {
                case ADD_NEW_ROUTE:
                    mDb.delete("ELL_Route", "RouteId = ?", new String[]{mDatas_Route.get(position).id});
                    cursor = mDb.rawQuery("SELECT* FROM ELL_Area WHERE RouteId = ?", new String[]{mDatas_Route.get(position).id});
                    while (cursor.moveToNext()) {
                        String areaId = cursor.getString(cursor.getColumnIndex("AreaId"));
                        cursor2 = mDb.rawQuery("SELECT* FROM ELL_Section WHERE AreaId = ?", new String[]{areaId});
                        while (cursor2.moveToNext()) {
                            String sectionId = cursor2.getString(cursor2.getColumnIndex("SectionId"));
                            mDb.delete("ELL_Tunnel", "SectionId=?", new String[]{sectionId});
                        }
                        cursor2.close();
                        mDb.delete("ELL_Section", "AreaId=?", new String[]{areaId});
                    }
                    cursor.close();
                    mDb.delete("ELL_Area", "RouteId=?", new String[]{mDatas_Route.get(position).id});
                    Toast.makeText(this, "已删除", Toast.LENGTH_SHORT).show();
//                    finish();
                    break;
                case ADD_NEW_AREA:
                    mDb.delete("ELL_Area", "AreaId = ?", new String[]{mDatas_Area.get(position).id});
                    cursor = mDb.rawQuery("SELECT* FROM ELL_Section WHERE AreaId = ?", new String[]{mDatas_Area.get(position).id});
                    while (cursor.moveToNext()) {
                        String sectionId = cursor.getString(cursor.getColumnIndex("SectionId"));
                        mDb.delete("ELL_Tunnel", "SectionId=?", new String[]{sectionId});
                    }
                    cursor.close();
                    mDb.delete("ELL_Section", "AreaId = ?", new String[]{mDatas_Area.get(position).id});
                    Toast.makeText(this, "已删除", Toast.LENGTH_SHORT).show();
//                    finish();
                    break;
                case ADD_NEW_SECTION:
                    mDb.delete("ELL_Section", "SectionId = ?", new String[]{mDatas_Section.get(position).id});
                    mDb.delete("ELL_Tunnel", "SectionId = ?", new String[]{mDatas_Section.get(position).id});
                    Toast.makeText(this, "已删除", Toast.LENGTH_SHORT).show();
//                    finish();
                    break;
                case ADD_NEW_TUNNEL:
                    mDb.delete("ELL_Tunnel", "TunnelId = ?", new String[]{mDatas_Tunnel.get(position).id});
                    Toast.makeText(this, "已删除", Toast.LENGTH_SHORT).show();
//                    finish();
                    break;
                default:
                    break;

            }
            mAdapter.clearAfterToRefresh(position);
        } catch (Exception e) {
            Log.e("删除数据库报错", e.toString());
        }
    }

    @OnClick({R.id.examine_page_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.examine_page_back:
                onBackPressed();
                break;

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent();
        intent.putExtra(CHOOSE_ROUTEID, mChoosed_routeId);
        intent.putExtra(CHOOSE_AREAID, mChoosed_areaId);
        intent.putExtra(CHOOSE_SECTIONID, mChoosed_sectionId);
        intent.putExtra(CHOOSE_TUNNELID, mChoosed_tunnelId);
        intent.putExtra(CHOOSE_TYPE, "");
        // Set result and finish this Activity
        setResult(Activity.RESULT_OK, intent);
        finish();
        overridePendingTransition(0, R.anim.zoomout);
    }
}
