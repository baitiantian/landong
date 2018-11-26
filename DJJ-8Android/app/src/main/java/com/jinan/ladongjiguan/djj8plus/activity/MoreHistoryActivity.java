package com.jinan.ladongjiguan.djj8plus.activity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import com.jinan.ladongjiguan.djj8plus.R;
import com.jinan.ladongjiguan.djj8plus.adapter.HRecyclerAdapter;
import com.jinan.ladongjiguan.djj8plus.adapter.HRecyclerViewHolder;
import com.jinan.ladongjiguan.djj8plus.bean.SearchBean;
import com.jinan.ladongjiguan.djj8plus.publicClass.AssetsDatabaseManager;
import com.jinan.ladongjiguan.djj8plus.publicClass.HRecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MoreHistoryActivity extends BaseActivity {
    @BindView(R.id.examine_page_back)
    LinearLayout mLinearBack;
    @BindView(R.id.title_layout)
    TextView mTvTitle;
    @BindView(R.id.delete_layout)
    TextView mTvChart;
    @BindView(R.id.activity_more_history_sp_type)
    Spinner mSpType;
    @BindView(R.id.activity_more_history_hrecycler)
    HRecyclerView mRecyclerView;

    private Map<String, Object> mListItemName = new HashMap<>();
    private Map<String, Object> mListItemType = new HashMap<>();
    private List<Map<String, Object>> mListItemsName = new ArrayList<>();
    private List<Map<String, Object>> mListItemsType = new ArrayList<>();
    List<SearchBean> mDataModels1 = new ArrayList<>();
    List<SearchBean> mDataModels2 = new ArrayList<>();
    List<SearchBean> mDataModels3 = new ArrayList<>();
    List<SearchBean> mDataModels5 = new ArrayList<>();
    List<SearchBean> mDataModels6 = new ArrayList<>();
    List<SearchBean> mDataModels7 = new ArrayList<>();
    List<SearchBean> mDataModels8 = new ArrayList<>();
    List<SearchBean> mDataModels9 = new ArrayList<>();
    List<SearchBean> mDataModels10 = new ArrayList<>();
    List<SearchBean> mDataModels11 = new ArrayList<>();
    private AssetsDatabaseManager mMg;
    private SQLiteDatabase mDb;
    private String mSearchName = "";
    private String mSearchState = "";
    // 标志位，标志已经初始化完成。
    private boolean isPrepared = false;
    private Cursor mCursor1;
    private Cursor mCursor2;
    private Cursor mCursor11;
    private Cursor mCursor10;
    private Cursor mCursor9;
    private Cursor mCursor7;
    private Cursor mCursor6;
    private Cursor mCursor5;
    private Cursor mCursor3;
    private Cursor mCursor8;
    private String TAG = MoreHistoryActivity.class.getSimpleName();
    private HRecyclerAdapter mAdapter;
    private SimpleAdapter mSpAdapter;
    private SearchBean mSearchBean1, mSearchBean2, mSearchBean3, mSearchBean5, mSearchBean6, mSearchBean7, mSearchBean8, mSearchBean9, mSearchBean10, mSearchBean11;
    private String mA1 = "", mA2 = "", mA3 = "", mA5 = "", mA6 = "", mA7 = "", mA8 = "", mA9 = "", mA10 = "", mA11 = "";
    private String mSelectType;
    private String mGanhaoId_get;//

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_history);
        ButterKnife.bind(this);
        mGanhaoId_get = getIntent().getStringExtra("ganhao")==null?"":getIntent().getStringExtra("ganhao");
        Log.e(TAG+"-onCreate","mGanhaoId为"+mGanhaoId_get);
        // 初始化，只需要调用一次
        AssetsDatabaseManager.initManager(MoreHistoryActivity.this);
        // 获取管理对象，因为数据库需要通过管理对象才能够获取
        mMg = AssetsDatabaseManager.getManager();
        // 通过管理对象获取数据库
        mDb = mMg.getDatabase("DJJ-8data.db");
        mTvTitle.setText("历史记录");
        mTvChart.setText("折线图");
        getName();
        setData();
    }

    /**
     * 获取 名称
     */
    private void getName() {
        try {
            /**
             * 下拉杆号 列表
             * */
            // 对数据库进行操作
                mCursor1 = mDb.rawQuery("SELECT* FROM ELL_D1Data WHERE Num = ?",
                        new String[]{mGanhaoId_get});
                while (mCursor1.moveToNext()) {
                    String dataId = mCursor1.getString(mCursor1.getColumnIndex("DataId"));
                    mSearchState = "d1";
                    mSearchName = "基础测量";
                    Log.e(TAG + "-getGanhao(1)", "dataId为" + dataId);
                    getNewData(mSearchState);
                    mA1 = mDataModels1.get(0).d1_dg;
                    Log.e(TAG, "a1为" + mA1);
                }
                mCursor2 = mDb.rawQuery("SELECT* FROM ELL_D2Data WHERE Num = ?",
                        new String[]{mGanhaoId_get});
//                        new String[]{ganhaoId});
                while (mCursor2.moveToNext()) {
                    String dataId = mCursor2.getString(mCursor2.getColumnIndex("DataId"));
                    mSearchState = "d2";
                    mSearchName = "线岔中心测量";
                    Log.e(TAG + "-getGanhao(2)", "dataId为" + dataId);
                    getNewData(mSearchState);
                    mA2 = mDataModels2.get(0).d1_dg;
                    Log.e(TAG, "a2为" + mA2);
                }
                mCursor3 = mDb.rawQuery("SELECT* FROM ELL_D3Data WHERE Num = ?",
                        new String[]{mGanhaoId_get});
                while (mCursor3.moveToNext()) {
                    String dataId = mCursor3.getString(mCursor3.getColumnIndex("DataId"));
                    mSearchState = "d3";
                    mSearchName = "限界测量";
                    Log.e(TAG + "-getGanhao(3)", "dataId为" + dataId);
                    getNewData(mSearchState);
                    mA3 = mDataModels3.get(0).d1_dg;
                    Log.e(TAG, "a3为" + mA3);
                    Log.e(TAG + "-cursor3", "d1_dg为" + mCursor3.getString(mCursor3.getColumnIndex("D3_HXBG")));
                }
                mCursor5 = mDb.rawQuery("SELECT* FROM ELL_D5Data WHERE Num = ?",
                        new String[]{mGanhaoId_get});
                while (mCursor5.moveToNext()) {
                    String dataId = mCursor5.getString(mCursor5.getColumnIndex("DataId"));
                    mSearchState = "d5";
                    mSearchName = "非支测量";
                    Log.e(TAG + "-getGanhao(5)", "dataId为" + dataId);
                    getNewData(mSearchState);
                    mA5 = mDataModels5.get(0).d1_dg;
                    Log.e(TAG, "a5为" + mA5);
                }
                mCursor6 = mDb.rawQuery("SELECT* FROM ELL_D6Data WHERE Num = ?",
                        new String[]{mGanhaoId_get});
                while (mCursor6.moveToNext()) {
                    String dataId = mCursor6.getString(mCursor6.getColumnIndex("DataId"));
                    mSearchState = "d6";
                    mSearchName = "定位器测量";
                    Log.e(TAG + "-getGanhao(6)", "dataId为" + dataId);
                    getNewData(mSearchState);
                    mA6 = mDataModels6.get(0).d1_dg;
                    Log.e(TAG, "a6为" + mA6);
                }
                mCursor7 = mDb.rawQuery("SELECT* FROM ELL_D7Data WHERE Num = ?",
                        new String[]{mGanhaoId_get});
                while (mCursor7.moveToNext()) {
                    String dataId = mCursor7.getString(mCursor7.getColumnIndex("DataId"));
                    mSearchState = "d7";
                    mSearchName = "承力索高差测量";
                    Log.e(TAG + "-getGanhao(7)", "dataId为" + dataId);
                    getNewData(mSearchState);
                    mA7 = mDataModels7.get(0).d1_dg;
                    Log.e(TAG, "a7为" + mA7);
                }
                mCursor8 = mDb.rawQuery("SELECT* FROM ELL_D8Data WHERE Num = ?",
                        new String[]{mGanhaoId_get});
                while (mCursor8.moveToNext()) {
                    String dataId = mCursor8.getString(mCursor8.getColumnIndex("DataId"));
                    mSearchState = "d8";
                    mSearchName = "500mm处高差测量";
                    Log.e(TAG + "-getGanhao(8)", "dataId为" + dataId);
                    getNewData(mSearchState);
                    mA8 = mDataModels8.get(0).d1_dg;
                    Log.e(TAG, "a8为" + mA8);
                }
                mCursor9 = mDb.rawQuery("SELECT* FROM ELL_D9Data WHERE Num = ?",
                        new String[]{mGanhaoId_get});
                while (mCursor9.moveToNext()) {
                    String dataId = mCursor9.getString(mCursor9.getColumnIndex("DataId"));
                    mSearchState = "d9";
                    mSearchName = "自由测量";
                    Log.e(TAG + "-getGanhao(9)", "dataId为" + dataId);
                    getNewData(mSearchState);
                    mA9 = mDataModels9.get(0).d1_dg;
                    Log.e(TAG, "a9为" + mA9);
                }
                mCursor10 = mDb.rawQuery("SELECT* FROM ELL_DCData WHERE Num = ?",
                        new String[]{mGanhaoId_get});
                while (mCursor10.moveToNext()) {
                    String dataId = mCursor10.getString(mCursor10.getColumnIndex("DataId"));
                    mSearchState = "dc";
                    mSearchName = "垂直测量";
                    Log.e(TAG + "-getGanhao(10)", "dataId为" + dataId);
                    getNewData(mSearchState);
                    mA10 = mDataModels10.get(0).d1_dg;
                    Log.e(TAG, "a10为" + mA10);
                }
                mCursor11 = mDb.rawQuery("SELECT* FROM ELL_DEData WHERE Num = ?",
                        new String[]{mGanhaoId_get});
                while (mCursor11.moveToNext()) {
                    String dataId = mCursor11.getString(mCursor11.getColumnIndex("DataId"));
                    mSearchState = "de";
                    mSearchName = "跨距测量";
                    Log.e(TAG + "-getGanhao(11)", "dataId为" + dataId);
                    getNewData(mSearchState);
                    mA11 = mDataModels11.get(0).d1_dg;
                    Log.e(TAG, "a11为" + mA11);
                }
            Log.e(TAG + "-getGanhao", "mSearchState-1为" + mSearchState + ";mSearchName-1为" + mSearchName);
        } catch (Exception e) {
            Log.e(TAG + "-getGanhao", "数据库报错" + e.toString());
        }
    }

    /**
     * 得到最新一条的历史数据
     */
    private void getNewData(String state) {
        Log.e(TAG + "-getNewData", "state为" + state);
        if (state != "") {
            switch (state) {
                case "d1":
                    mSearchBean1 = new SearchBean();
                    mSearchBean1.name = "基础测量";
                    mSearchBean1.num = mCursor1.getString(mCursor1.getColumnIndex("Num"));
                    mSearchBean1.d1_dg = mCursor1.getString(mCursor1.getColumnIndex("D1_DG"));
                    mSearchBean1.d1_lcz = mCursor1.getString(mCursor1.getColumnIndex("D1_LCZ"));
                    mSearchBean1.d1_gj = mCursor1.getString(mCursor1.getColumnIndex("D1_GJ"));
                    mSearchBean1.d1_cg = mCursor1.getString(mCursor1.getColumnIndex("D1_CG"));
                    mSearchBean1.date = mCursor1.getString(mCursor1.getColumnIndex("Date"));
                    mSearchBean1.note = mCursor1.getString(mCursor1.getColumnIndex("Note"));
                    mDataModels1.add(mSearchBean1);
                    break;
                case "d2":
                    mSearchBean2 = new SearchBean();
                    mSearchBean2.name = "线岔中心测量";
                    mSearchBean2.num = mCursor2.getString(mCursor2.getColumnIndex("Num"));
                    mSearchBean2.d1_dg = mCursor2.getString(mCursor2.getColumnIndex("D2_DG"));
                    mSearchBean2.d1_lcz = mCursor2.getString(mCursor2.getColumnIndex("D2_PLZ"));
                    mSearchBean2.d1_gj = mCursor2.getString(mCursor2.getColumnIndex("D2_NGJ"));
                    mSearchBean2.date = mCursor2.getString(mCursor2.getColumnIndex("Date"));
                    mSearchBean2.note = mCursor2.getString(mCursor2.getColumnIndex("Note"));
                    mDataModels2.add(mSearchBean2);
                    break;
                case "d3":
                    mSearchBean3 = new SearchBean();
                    mSearchBean3.name = "限界测量";
                    mSearchBean3.num = mCursor3.getString(mCursor3.getColumnIndex("Num"));
                    mSearchBean3.d1_dg = mCursor3.getString(mCursor3.getColumnIndex("D3_HXBG"));
                    mSearchBean3.d1_lcz = mCursor3.getString(mCursor3.getColumnIndex("D3_CLXJ"));
                    mSearchBean3.date = mCursor3.getString(mCursor3.getColumnIndex("Date"));
                    mSearchBean3.note = mCursor3.getString(mCursor3.getColumnIndex("Note"));
                    mDataModels3.add(mSearchBean3);
                    Log.e(TAG + "-d3", "1为" + mSearchBean3.num + ";2为" + mSearchBean3.d1_dg + ";3为" + mSearchBean3.d1_lcz + ";4为" + mSearchBean3.date + ";5为" + mSearchBean3.note);
                    break;
                case "d5":
                    mSearchBean5 = new SearchBean();
                    mSearchBean5.name = "非支测量";
                    mSearchBean5.num = mCursor5.getString(mCursor5.getColumnIndex("Num"));
                    mSearchBean5.d1_dg = mCursor5.getString(mCursor5.getColumnIndex("D5_FZTG"));
                    mSearchBean5.d1_lcz = mCursor5.getString(mCursor5.getColumnIndex("D5_FZPL"));
                    mSearchBean5.date = mCursor5.getString(mCursor5.getColumnIndex("Date"));
                    mSearchBean5.note = mCursor5.getString(mCursor5.getColumnIndex("Note"));
                    mDataModels5.add(mSearchBean5);
                    break;
                case "d6":
                    mSearchBean6 = new SearchBean();
                    mSearchBean6.name = "定位器测量";
                    mSearchBean6.num = mCursor6.getString(mCursor6.getColumnIndex("Num"));
                    mSearchBean6.d1_dg = mCursor6.getString(mCursor6.getColumnIndex("D6_DWQPD"));
                    mSearchBean6.d1_lcz = mCursor6.getString(mCursor6.getColumnIndex("D6_GC"));
                    mSearchBean6.date = mCursor6.getString(mCursor6.getColumnIndex("Date"));
                    mSearchBean6.note = mCursor6.getString(mCursor6.getColumnIndex("Note"));
                    mDataModels6.add(mSearchBean6);
                    break;
                case "d7":
                    mSearchBean7 = new SearchBean();
                    mSearchBean7.name = "承力索高差测量";
                    mSearchBean7.num = mCursor7.getString(mCursor7.getColumnIndex("Num"));
                    mSearchBean7.d1_dg = mCursor7.getString(mCursor7.getColumnIndex("D7_GD1"));
                    mSearchBean7.d1_lcz = mCursor7.getString(mCursor7.getColumnIndex("D7_GD2"));
                    mSearchBean7.d1_gj = mCursor7.getString(mCursor7.getColumnIndex("D7_GC"));
                    mSearchBean7.date = mCursor7.getString(mCursor7.getColumnIndex("Date"));
                    mSearchBean7.note = mCursor7.getString(mCursor7.getColumnIndex("Note"));
                    mDataModels7.add(mSearchBean7);
                    break;
                case "d8":
                    mSearchBean8 = new SearchBean();
                    mSearchBean8.name = "500mm处高差测量";
                    mSearchBean8.num = mCursor8.getString(mCursor8.getColumnIndex("Num"));
                    mSearchBean8.d1_dg = mCursor8.getString(mCursor8.getColumnIndex("D8_GD1"));
                    mSearchBean8.d1_lcz = mCursor8.getString(mCursor8.getColumnIndex("D8_XJ"));
                    mSearchBean8.d1_gj = mCursor8.getString(mCursor8.getColumnIndex("D8_GC"));
                    mSearchBean8.date = mCursor8.getString(mCursor8.getColumnIndex("Date"));
                    mSearchBean8.note = mCursor8.getString(mCursor8.getColumnIndex("Note"));
                    mDataModels8.add(mSearchBean8);
                    break;
                case "d9":
                    mSearchBean9 = new SearchBean();
                    mSearchBean9.name = "自由测量";
                    mSearchBean9.num = mCursor9.getString(mCursor9.getColumnIndex("Num"));
                    mSearchBean9.d1_dg = mCursor9.getString(mCursor9.getColumnIndex("D9_SPJ"));
                    mSearchBean9.d1_lcz = mCursor9.getString(mCursor9.getColumnIndex("D9_CZJ"));
                    mSearchBean9.date = mCursor9.getString(mCursor9.getColumnIndex("Date"));
                    mSearchBean9.note = mCursor9.getString(mCursor9.getColumnIndex("Note"));
                    mDataModels9.add(mSearchBean9);
                    break;
                case "dc":
                    mSearchBean10 = new SearchBean();
                    mSearchBean10.name = "垂直测量";
                    mSearchBean10.num = mCursor10.getString(mCursor10.getColumnIndex("Num"));
                    mSearchBean10.d1_dg = mCursor10.getString(mCursor10.getColumnIndex("DC_ZZCZD"));
                    mSearchBean10.date = mCursor10.getString(mCursor10.getColumnIndex("Date"));
                    mSearchBean10.note = mCursor10.getString(mCursor10.getColumnIndex("Note"));
                    mDataModels10.add(mSearchBean10);
                    break;
                case "de":
                    mSearchBean11 = new SearchBean();
                    mSearchBean11.name = "跨距测量";
                    mSearchBean11.num = mCursor11.getString(mCursor11.getColumnIndex("Num"));
                    mSearchBean11.d1_dg = mCursor11.getString(mCursor11.getColumnIndex("DE_ZZKD"));
                    mSearchBean11.date = mCursor11.getString(mCursor11.getColumnIndex("Date"));
                    mSearchBean11.note = mCursor11.getString(mCursor11.getColumnIndex("Note"));
                    mDataModels11.add(mSearchBean11);
                    break;
                default:
                    break;
            }
        }
    }

    private void setData() {
        /**
         * 最后的配置
         * */
        if (mA1 != "") {
            mListItemName = new HashMap<>();
            mListItemType = new HashMap<>();
            mListItemName.put("name", "基础测量");
            mListItemType.put("state", "d1");
            mListItemsName.add(mListItemName);
            mListItemsType.add(mListItemType);
        }
        if (mA2 != "") {
            mListItemName = new HashMap<>();
            mListItemType = new HashMap<>();
            mListItemName.put("name", "线岔中心测量");
            mListItemType.put("state", "d2");
            mListItemsName.add(mListItemName);
            mListItemsType.add(mListItemType);
        }
        if (mA3 != "") {
            mListItemName = new HashMap<>();
            mListItemType = new HashMap<>();
            mListItemName.put("name", "限界测量");
            mListItemType.put("state", "d3");
            mListItemsName.add(mListItemName);
            mListItemsType.add(mListItemType);
        }
        if (mA5 != "") {
            mListItemName = new HashMap<>();
            mListItemType = new HashMap<>();
            mListItemName.put("name", "非支测量");
            mListItemType.put("state", "d5");
            mListItemsName.add(mListItemName);
            mListItemsType.add(mListItemType);
        }
        if (mA6 != "") {
            mListItemName = new HashMap<>();
            mListItemType = new HashMap<>();
            mListItemName.put("name", "定位器测量");
            mListItemType.put("state", "d6");
            mListItemsName.add(mListItemName);
            mListItemsType.add(mListItemType);
        }
        if (mA7 != "") {
            mListItemName = new HashMap<>();
            mListItemType = new HashMap<>();
            mListItemName.put("name", "承力索高差测量");
            mListItemType.put("state", "d7");
            mListItemsName.add(mListItemName);
            mListItemsType.add(mListItemType);
        }
        if (mA8 != "") {
            mListItemName = new HashMap<>();
            mListItemType = new HashMap<>();
            mListItemName.put("name", "500mm处高差测量");
            mListItemType.put("state", "d8");
            mListItemsName.add(mListItemName);
            mListItemsType.add(mListItemType);
        }
        if (mA9 != "") {
            mListItemName = new HashMap<>();
            mListItemType = new HashMap<>();
            mListItemName.put("name", "自由测量");
            mListItemType.put("state", "d9");
            mListItemsName.add(mListItemName);
            mListItemsType.add(mListItemType);
        }
        if (mA10 != "") {
            mListItemName = new HashMap<>();
            mListItemType = new HashMap<>();
            mListItemName.put("name", "垂直测量");
            mListItemType.put("state", "dc");
            mListItemsName.add(mListItemName);
            mListItemsType.add(mListItemType);
        }
        if (mA11 != "") {
            mListItemName = new HashMap<>();
            mListItemType = new HashMap<>();
            mListItemName.put("name", "跨距测量");
            mListItemType.put("state", "de");
            mListItemsName.add(mListItemName);
            mListItemsType.add(mListItemType);
        }
        if (mListItemsName.size() == 0) {
            mListItemName.put("name", "暂无数据");
            mListItemsName.add(mListItemName);
        }

        mSpAdapter = new SimpleAdapter(MoreHistoryActivity.this, mListItemsName,
                R.layout.dialog_spinner_item,
                new String[]{"name"},
                new int[]{R.id.text});
        mSpType.setAdapter(mSpAdapter);
        mSpType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                mSelectType = mListItemsType.get(position).toString();
                Log.e(TAG + "-onItemSelected", "mType为" + mSelectType);
                if (mSelectType.startsWith("{state=") && mSelectType.endsWith("}")) {
                    mSelectType = mSelectType.substring(mSelectType.indexOf("=") + 1, mSelectType.indexOf("}"));
                }
                Log.e(TAG + "-substring", "截取后的mType为" + mSelectType);
                showData(mSelectType);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void showData(String state) {
        mRecyclerView.removeAllViews();
        Log.e(TAG + "-showData", "mListItemsName.size()为" + mListItemsName.size() + "mListItemsType.size()为" + mListItemsType.size());
        switch (state) {
            case "d1":
                Log.e(TAG + "-showData", "mDataModels1.size()为" + mDataModels1.size());
                mRecyclerView.setHeaderListData(getResources().getStringArray(R.array.search_title_d1));
                mAdapter = new HRecyclerAdapter(MoreHistoryActivity.this, state, mDataModels1, R.layout.item_search, new HRecyclerViewHolder.onItemCommonClickListener() {
                    @Override
                    public void onItemClickListener(int position) {

                    }

                    @Override
                    public void onItemLongClickListener(int position) {

                    }
                });
                mAdapter.clearAndRefresh(MoreHistoryActivity.this);
                mRecyclerView.setAdapter(mAdapter);
                break;
            case "d2":
                mRecyclerView.setHeaderListData(getResources().getStringArray(R.array.search_title_d2));
                mAdapter = new HRecyclerAdapter(MoreHistoryActivity.this, state, mDataModels2, R.layout.item_search, new HRecyclerViewHolder.onItemCommonClickListener() {
                    @Override
                    public void onItemClickListener(int position) {

                    }

                    @Override
                    public void onItemLongClickListener(int position) {

                    }
                });
                mAdapter.clearAndRefresh(MoreHistoryActivity.this);
                mRecyclerView.setAdapter(mAdapter);
                break;
            case "d3":
                Log.e(TAG + "-showData(3)", "mDataModels3.size()为" + mDataModels3.size());
                mRecyclerView.setHeaderListData(getResources().getStringArray(R.array.search_title_d3));
                mAdapter = new HRecyclerAdapter(MoreHistoryActivity.this, state, mDataModels3, R.layout.item_search, new HRecyclerViewHolder.onItemCommonClickListener() {
                    @Override
                    public void onItemClickListener(int position) {

                    }

                    @Override
                    public void onItemLongClickListener(int position) {

                    }
                });
                mAdapter.clearAndRefresh(MoreHistoryActivity.this);
                mRecyclerView.setAdapter(mAdapter);
                break;
            case "d5":
                Log.e(TAG + "-showData(5)", "mDataModels5.size()为" + mDataModels5.size());
                mRecyclerView.setHeaderListData(getResources().getStringArray(R.array.search_title_d5));
                mAdapter = new HRecyclerAdapter(MoreHistoryActivity.this, state, mDataModels5, R.layout.item_search, new HRecyclerViewHolder.onItemCommonClickListener() {
                    @Override
                    public void onItemClickListener(int position) {

                    }

                    @Override
                    public void onItemLongClickListener(int position) {

                    }
                });
                mAdapter.clearAndRefresh(MoreHistoryActivity.this);
                mRecyclerView.setAdapter(mAdapter);
                break;
            case "d6":
                Log.e(TAG + "-showData(6)", "mDataModels6.size()为" + mDataModels6.size());
                mRecyclerView.setHeaderListData(getResources().getStringArray(R.array.search_title_d6));
                mAdapter = new HRecyclerAdapter(MoreHistoryActivity.this, state, mDataModels6, R.layout.item_search, new HRecyclerViewHolder.onItemCommonClickListener() {
                    @Override
                    public void onItemClickListener(int position) {

                    }

                    @Override
                    public void onItemLongClickListener(int position) {

                    }
                });
                mAdapter.clearAndRefresh(MoreHistoryActivity.this);
                mRecyclerView.setAdapter(mAdapter);
                break;
            case "d7":
                Log.e(TAG + "-showData(7)", "mDataModels7.size()为" + mDataModels7.size());
                mRecyclerView.setHeaderListData(getResources().getStringArray(R.array.search_title_d7));
                mAdapter = new HRecyclerAdapter(MoreHistoryActivity.this, state, mDataModels7, R.layout.item_search, new HRecyclerViewHolder.onItemCommonClickListener() {
                    @Override
                    public void onItemClickListener(int position) {

                    }

                    @Override
                    public void onItemLongClickListener(int position) {

                    }
                });
                mAdapter.clearAndRefresh(MoreHistoryActivity.this);
                mRecyclerView.setAdapter(mAdapter);
                break;
            case "d8":
                Log.e(TAG + "-showData(8)", "mDataModels8.size()为" + mDataModels8.size());
                mRecyclerView.setHeaderListData(getResources().getStringArray(R.array.search_title_d8));
                mAdapter = new HRecyclerAdapter(MoreHistoryActivity.this, state, mDataModels8, R.layout.item_search, new HRecyclerViewHolder.onItemCommonClickListener() {
                    @Override
                    public void onItemClickListener(int position) {

                    }

                    @Override
                    public void onItemLongClickListener(int position) {

                    }
                });
                mAdapter.clearAndRefresh(MoreHistoryActivity.this);
                mRecyclerView.setAdapter(mAdapter);
                break;
            case "d9":
                mRecyclerView.setHeaderListData(getResources().getStringArray(R.array.search_title_d9));
                mAdapter = new HRecyclerAdapter(MoreHistoryActivity.this, state, mDataModels9, R.layout.item_search, new HRecyclerViewHolder.onItemCommonClickListener() {
                    @Override
                    public void onItemClickListener(int position) {

                    }

                    @Override
                    public void onItemLongClickListener(int position) {

                    }
                });
                mAdapter.clearAndRefresh(MoreHistoryActivity.this);
                mRecyclerView.setAdapter(mAdapter);
                break;
            case "dc":
                mRecyclerView.setHeaderListData(getResources().getStringArray(R.array.search_title_dc));
                mAdapter = new HRecyclerAdapter(MoreHistoryActivity.this, state, mDataModels10, R.layout.item_search, new HRecyclerViewHolder.onItemCommonClickListener() {
                    @Override
                    public void onItemClickListener(int position) {

                    }

                    @Override
                    public void onItemLongClickListener(int position) {

                    }
                });
                mAdapter.clearAndRefresh(MoreHistoryActivity.this);
                mRecyclerView.setAdapter(mAdapter);
                break;
            case "de":
                mRecyclerView.setHeaderListData(getResources().getStringArray(R.array.search_title_de));
                mAdapter = new HRecyclerAdapter(MoreHistoryActivity.this, state, mDataModels11, R.layout.item_search, new HRecyclerViewHolder.onItemCommonClickListener() {
                    @Override
                    public void onItemClickListener(int position) {

                    }

                    @Override
                    public void onItemLongClickListener(int position) {

                    }
                });
                mAdapter.clearAndRefresh(MoreHistoryActivity.this);
                mRecyclerView.setAdapter(mAdapter);
                break;
        }
    }

    @OnClick({R.id.examine_page_back, R.id.delete_layout})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.examine_page_back:
                onBackPressed();
                break;
            case R.id.delete_layout://折线图
                Intent intent = new Intent(MoreHistoryActivity.this,MultiLineChartActivity.class).putExtra("state",mSelectType).putExtra("ganhao",mGanhaoId_get);
                startActivity(intent);
                break;

        }
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void init() {

    }
}
