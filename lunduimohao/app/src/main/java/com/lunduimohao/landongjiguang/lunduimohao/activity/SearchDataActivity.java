package com.lunduimohao.landongjiguang.lunduimohao.activity;

import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.lunduimohao.landongjiguang.lunduimohao.PublicClass.ExcelUtils;
import com.lunduimohao.landongjiguang.lunduimohao.PublicClass.Order;
import com.lunduimohao.landongjiguang.lunduimohao.PublicClass.SearchDataAdapter;
import com.lunduimohao.landongjiguang.lunduimohao.R;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


public class SearchDataActivity extends BaseActivity implements View.OnClickListener, TextWatcher {
    private String string = "0";//左右轮
    /**
     * 各个数据显示
     */
    private TextView data_1;
    private TextView data_2;
    private TextView data_3;
    private TextView data_4;
    private TextView data_5;
    private TextView t_car_num_1;
    private TextView t_car_num_2;
    private TextView t_car_num_3;
    private TextView t_car_num_4;
    /**
     * 读取全部数据库信息
     */
    private List<HashMap<String, Object>> mList;
    private ListView mListView;//列表显示
    /**
     * 确认是否有数据库
     */
//    private File file;
    private boolean isDirectoryCreated;
    /**
     * data显示栏
     * */

    /**
     * 数据库
     */
    private SQLiteDatabase db;
    /**
     * 数据第几条
     */
    private int n;
    /**
     * 查询信息
     */
    private String s_data = "";
    /**
     * 杆号
     */
    private EditText numID;
    /**
     * 数据库数据操作
     * */
    private SearchDataAdapter mdataListAdapter2;
    private List<HashMap<String, Object>> mList2;
    private SearchDataAdapter mDataListAdapter;
    private TextView mFanxuan;
    private Button mDelete;
    private String TAG = SearchDataActivity.class.getSimpleName();
    private TextView searchDataDelete;
    /**
     * 复选框标识
     * */
    public static Boolean CHECK_BOX = false;
    /**
     * 导出文件名标识
     * */
    private int getN = 0;
    @Override
    protected void initView() {
        setContentView(R.layout.search_data_layout);
        /**
         * 标题名称
         * */
        TextView title_layout = (TextView) findViewById(R.id.title_layout);
        title_layout.setText("查询");
        /**
         * 返回键
         * */
        findViewById(R.id.examine_page_back).setOnClickListener(this);
        /**
         * 搜索按钮
         * */
        findViewById(R.id.bt_car_num_save).setOnClickListener(this);
        mListView = (ListView) findViewById(R.id.search_data_listview);
        /**
         * 下拉菜单
         * */
        Spinner e_car_num_4 = (Spinner) findViewById(R.id.e_car_num_4);
        e_car_num_4.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                string = position + "";
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        /**
         * 定义各个text
         * */
        data_1 = (TextView) findViewById(R.id.data_1);
        data_2 = (TextView) findViewById(R.id.data_2);
        data_3 = (TextView) findViewById(R.id.data_3);
        data_4 = (TextView) findViewById(R.id.data_4);
        data_5 = (TextView) findViewById(R.id.data_5);
        t_car_num_1 = (TextView) findViewById(R.id.t_car_num_1);
        t_car_num_2 = (TextView) findViewById(R.id.t_car_num_2);
        t_car_num_3 = (TextView) findViewById(R.id.t_car_num_3);
        t_car_num_4 = (TextView) findViewById(R.id.t_car_num_4);

        /**
         * 杆号
         * */
        numID = (EditText) findViewById(R.id.num_ID);
        /**
         * 导出
         * */
        TextView deleteLayout = (TextView) findViewById(R.id.delete_layout);
        deleteLayout.setText("导出");
        deleteLayout.setOnClickListener(this);
        /**
         * 反选
         * */
        mFanxuan = (TextView) findViewById(R.id.searchdata_select_data);
        mFanxuan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < mList.size(); i++) {
                    if (SearchDataAdapter.isSelected.get(i)) {
                        SearchDataAdapter.isSelected.put(i, false);
                    } else {
                        SearchDataAdapter.isSelected.put(i, true);
                    }
                    mDataListAdapter.notifyDataSetChanged();
                }
            }
        });
        mFanxuan.setVisibility(View.GONE);
        /**
         *删除
         * */
        mDelete = (Button) findViewById(R.id.delete_data);
        mDelete.setVisibility(View.GONE);
        /**
         * 批量操作
         * */
        searchDataDelete = (TextView)findViewById(R.id.search_data_delete);
        searchDataDelete.setOnClickListener(this);
        findViewById(R.id.page_back).setVisibility(View.GONE);
        findViewById(R.id.page_back).setOnClickListener(this);
    }

    @Override
    protected void init() {
        /**
         * 提取数据库数据，转换成内存数据
         * */
        File file = new File("/sdcard/LineWear");
        isDirectoryCreated = file.exists();
        if (!isDirectoryCreated) {
            mListView.setVisibility(View.INVISIBLE);
        } else {
            db = SQLiteDatabase.openOrCreateDatabase("/sdcard/LineWear/data.db", null);
            //读取数据
            mList = new ArrayList<>();
            try {
                Cursor c = db.rawQuery("SELECT* FROM wheel_data WHERE Alldata = ?", new String[]{"1"});
                n = 0;
                while (c.moveToNext()) {
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("num", c.getString(c.getColumnIndex("WheelId")));
                    hashMap.put("TreadWear", c.getString(c.getColumnIndex("TreadWear")));
                    hashMap.put("WheelThick", c.getString(c.getColumnIndex("WheelThick")));
                    hashMap.put("RimWidth", c.getString(c.getColumnIndex("RimWidth")));
                    hashMap.put("RimThick", c.getString(c.getColumnIndex("RimThick")));
                    hashMap.put("Time", c.getString(c.getColumnIndex("Time")));
                    hashMap.put("WheelId", c.getString(c.getColumnIndex("WheelId")));
                    mList.add(hashMap);
                    Log.e(TAG + "数据库报错", hashMap.toString());
                    n++;
                }
                if (n == 0) {
                    mListView.setVisibility(View.GONE);
                }
                mDataListAdapter = new SearchDataAdapter(this, mList, R.layout.list_data_item,
                        new String[]{"num", "TreadWear", "WheelThick", "RimWidth", "RimThick", "Time"},
                        new int[]{R.id.list_item_01, R.id.data_1, R.id.data_2, R.id.data_3, R.id.data_4, R.id.data_5});
                mListView.setAdapter(mDataListAdapter);
                c.close();
            } catch (Exception e) {
                Log.e(TAG + "数据库报错", e.toString());
            }
            mDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog(mList, 1);
                }
            });
        }
        numID.addTextChangedListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.examine_page_back://返回
                onBackPressed();
                break;
            case R.id.bt_car_num_save://搜索键
                EditText e_car_num_1 = (EditText) findViewById(R.id.e_car_num_1);
                EditText e_car_num_2 = (EditText) findViewById(R.id.e_car_num_2);
                EditText e_car_num_3 = (EditText) findViewById(R.id.e_car_num_3);

                s_data = e_car_num_1.getText().toString() +
                        e_car_num_2.getText().toString() +
                        e_car_num_3.getText().toString() + string;
                Log.e(TAG + "杆号", "杆号为" + s_data);
                if (numID.getText().toString().trim().length() > 0) {
                    searchData(numID.getText().toString());
                } else {
                    Toast.makeText(this, "请输入完整信息", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.delete_layout://导出
                SimpleDateFormat sDateFormat = new SimpleDateFormat(
                        "yyyy-MM-dd HH:mm", Locale.getDefault());
                String date = sDateFormat.format(new java.util.Date());
                if (mList.size() > 0) {
                    List<Order> orders = new ArrayList<>();
                    for (int i = 0; i < mList.size(); i++) {
                        Order order = new Order(mList.get(i).get("WheelId").toString(),
                                mList.get(i).get("TreadWear").toString(),
                                mList.get(i).get("WheelThick").toString(),
                                mList.get(i).get("RimWidth").toString(),
                                mList.get(i).get("RimThick").toString(),
                                mList.get(i).get("Time").toString());
                        orders.add(order);
                    }
                    try {
                        getN++;
                        ExcelUtils.writeExcel(this, orders, "LineWear_" + date.substring(2, date.length())+"_"+getN);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "导出失败", Toast.LENGTH_SHORT).show();
                        Log.e(TAG + "数据报错", e.toString(), e);
                    }
                } else {
                    Toast.makeText(this, "没有需要导出的数据", Toast.LENGTH_SHORT).show();

                }
                break;
            case R.id.searchdata_select_data://反选
                Log.e(TAG + "88", "888888");
                for (int i = 0; i < mList.size(); i++) {
                    if (SearchDataAdapter.isSelected.get(i)) {
                        SearchDataAdapter.isSelected.put(i, false);
                    } else {
                        SearchDataAdapter.isSelected.put(i, true);
                    }
                    mDataListAdapter.notifyDataSetChanged();
                }
                break;
            case R.id.search_data_delete:
                mFanxuan.setVisibility(View.VISIBLE);
                searchDataDelete.setVisibility(View.GONE);
                CHECK_BOX = true;
                mDataListAdapter.notifyDataSetChanged();
                findViewById(R.id.search_data_delete).setVisibility(View.GONE);
                mDelete.setVisibility(View.VISIBLE);
                findViewById(R.id.page_back).setVisibility(View.VISIBLE);
                break;
            case R.id.page_back:
                mFanxuan.setVisibility(View.GONE);
                searchDataDelete.setVisibility(View.VISIBLE);
                CHECK_BOX = false;
                mDataListAdapter.notifyDataSetChanged();
                findViewById(R.id.search_data_delete).setVisibility(View.VISIBLE);
                mDelete.setVisibility(View.GONE);
                findViewById(R.id.page_back).setVisibility(View.GONE);
                break;
            default:
                break;

        }
    }

    /**
     * 搜索逻辑
     */
    protected void searchData(final String string) {
        if (!isDirectoryCreated) {
            Toast.makeText(SearchDataActivity.this, "无数据库", Toast.LENGTH_SHORT).show();
        } else {
            mList2 = new ArrayList<>();
            //读取数据
            Cursor c = db.rawQuery("SELECT* FROM wheel_data WHERE WheelId like ?", new String[]{"%" + string + "%"});
            if (c.getCount() == 0) {
                Toast.makeText(SearchDataActivity.this, "无数据", Toast.LENGTH_SHORT).show();
                mListView.setVisibility(View.GONE);
            } else {
                while (c.moveToNext()) {
                    mListView.setVisibility(View.VISIBLE);
                    try {
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("num", string + "");
                        hashMap.put("TreadWear", c.getString(c.getColumnIndex("TreadWear")));
                        hashMap.put("WheelThick", c.getString(c.getColumnIndex("WheelThick")));
                        hashMap.put("RimWidth", c.getString(c.getColumnIndex("RimWidth")));
                        hashMap.put("RimThick", c.getString(c.getColumnIndex("RimThick")));
                        hashMap.put("Time", c.getString(c.getColumnIndex("Time")));
                        hashMap.put("WheelId", c.getString(c.getColumnIndex("WheelId")));
                        mList2.add(hashMap);
                    } catch (Exception e) {
                        Log.e(TAG + "数据库报错", e.toString());
                    }
                }
                mdataListAdapter2 = new SearchDataAdapter(this, mList2, R.layout.list_data_item,
                        new String[]{"WheelId", "TreadWear", "WheelThick", "RimWidth", "RimThick", "Time"},
                        new int[]{R.id.list_item_01, R.id.data_1, R.id.data_2, R.id.data_3, R.id.data_4, R.id.data_5});
                mListView.setAdapter(mdataListAdapter2);
                mFanxuan.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for (int i = 0; i < mList2.size(); i++) {
                            if (SearchDataAdapter.isSelected.get(i)) {
                                SearchDataAdapter.isSelected.put(i, false);
                            } else {
                                SearchDataAdapter.isSelected.put(i, true);
                            }
                            mdataListAdapter2.notifyDataSetChanged();
                        }
                    }
                });
                mDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog(mList2, 2);
                    }
                });
            }
            c.close();
        }
    }

    /**
     * 重写返回键
     */
    @Override
    public void onBackPressed() {
        if (isDirectoryCreated) {
            db.close();
        }
        finish();
        overridePendingTransition(0, R.anim.zoomout);
    }

    /**
     * 重写销毁
     */
    @Override
    protected void onDestroy() {
        if (isDirectoryCreated) {
            db.close();
        }
        super.onDestroy();
    }

    /**
     * 弹出确认对话框
     */
    protected void dialog(final List<HashMap<String, Object>> list, final int type) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("确认删除");
        builder.setMessage("是否删除已选数据 ？");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (list != null) {
                    if (list != null && list.size() > 0) {
                        Log.e(TAG, "len-1为" + list.size());
                        dataDelete(list, type);
                        if (type == 1) {
                            mDataListAdapter.notifyDataSetChanged();
                        } else {
                            mdataListAdapter2.notifyDataSetChanged();
                        }
                    }
                }
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Toast.makeText(SearchDataActivity.this, "已取消", Toast.LENGTH_SHORT).show();
            }
        });
        builder.show();
    }

    /**
     * 删除数据
     */
    protected void dataDelete(List<HashMap<String, Object>> list, int type) {
        for (int i = 0; i < list.size(); i++) {
            if (SearchDataAdapter.isSelected.get(i).equals(true)) {
                String s = list.get(i).get("WheelId").toString();
                db.delete("wheel_data", "WheelId = ?", new String[]{s});
            }
        }
        if (type == 1) {
            init();
        } else if (type == 2) {
            searchData(numID.getText().toString());
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        Log.e(TAG, "before-s为" + s + "start为" + start + "count为" + count + "after为" + after);
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        Log.e(TAG, "Changed-s为" + s + "start为" + start + "count为" + count);
        if (s.equals("")) {
            mDataListAdapter.notifyDataSetChanged();
            init();

        }
    }

    @Override
    public void afterTextChanged(Editable s) {
        Log.e(TAG, "after-s为" + s.toString());
        if (s.toString().equals("")) {
            mDataListAdapter.notifyDataSetChanged();
            init();
        }
    }
}
