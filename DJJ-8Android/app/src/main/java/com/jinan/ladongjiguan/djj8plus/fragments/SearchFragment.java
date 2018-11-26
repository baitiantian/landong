package com.jinan.ladongjiguan.djj8plus.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.jinan.ladongjiguan.djj8plus.R;
import com.jinan.ladongjiguan.djj8plus.activity.MoreHistoryActivity;
import com.jinan.ladongjiguan.djj8plus.activity.MultiLineChartActivity;
import com.jinan.ladongjiguan.djj8plus.adapter.HRecyclerAdapter;
import com.jinan.ladongjiguan.djj8plus.adapter.HRecyclerViewHolder;
import com.jinan.ladongjiguan.djj8plus.bean.SearchBean;
import com.jinan.ladongjiguan.djj8plus.publicClass.AssetsDatabaseManager;
import com.jinan.ladongjiguan.djj8plus.publicClass.DoubleDatePickerDialog;
import com.jinan.ladongjiguan.djj8plus.publicClass.HRecyclerView;
import com.jinan.ladongjiguan.djj8plus.publicClass.SharedPreferencesUtil;
import com.jinan.ladongjiguan.djj8plus.publicClass.StringOrDate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static android.app.Activity.RESULT_OK;
import static com.jinan.ladongjiguan.djj8plus.activity.ChooseActivity.CHOOSE_BACK_HOME;
import static com.jinan.ladongjiguan.djj8plus.activity.MultiLineChartActivity.CHOOSE_BACK_SEARCH;

/**
 * Created by wangfuchun on 2018/6/8.
 */

public class SearchFragment extends LazyLoadFragment {

    @BindView(R.id.fragment_search_sp_route)
    Spinner mSpRoute;
    @BindView(R.id.fragment_search_sp_area)
    Spinner mSpArea;
    @BindView(R.id.fragment_search_sp_section)
    Spinner mSpSection;
    @BindView(R.id.fragment_search_sp_tunnel)
    Spinner mSpTunnel;
    @BindView(R.id.fragment_search_sp_ganhao)
    Spinner mSpGanhao;
    @BindView(R.id.fragment_search_tv_route)
    TextView mTvRoute;
    @BindView(R.id.fragment_search_tv_area)
    TextView mTvArea;
    @BindView(R.id.fragment_search_tv_section)
    TextView mTvSection;
    @BindView(R.id.fragment_search_tv_tunnel)
    TextView mTvTunnel;
    @BindView(R.id.fragment_search_tv_ganhao)
    TextView mTvGanhao;
    @BindView(R.id.fragment_search_rl_time)
    RelativeLayout mRlTime;
    @BindView(R.id.fragment_search_tv_starttime)
    TextView mTvStarttime;
    @BindView(R.id.fragmnet_search_tv_endtime)
    TextView mTvEndtime;
    @BindView(R.id.fragment_search_tv_beizhu)
    TextView mTvBeizhu;
    @BindView(R.id.fragment_search_sp_type)
    Spinner mSpType;
    @BindView(R.id.fragment_search_hrecycler)
    HRecyclerView mHRecyclerView;
    @BindView(R.id.fragment_search_tv_more)
    Button mMore;

    private View mView;
    private List<SearchBean> mDataModels;
    private HRecyclerAdapter mAdapter;
    private int mLastX;
    private int mLastY;
    private Unbinder mUnBind;
    private SQLiteDatabase mDb;
    private String mSelectedRouteId;
    private String mSelectedAreaId;
    private String mSelectedSectionId;
    private String mSelectedTunnelId;
    private String mSelectedGanhaoId = "";
    private List<Map<String, Object>> mListItemsArea = new ArrayList<>();
    private List<Map<String, Object>> mListItemsSection = new ArrayList<>();
    private List<Map<String, Object>> mListItemsTunnel = new ArrayList<>();
    private List<Map<String, Object>> mListItemsRoute = new ArrayList<>();
    private List<Map<String, Object>> mListItemsGanhao = new ArrayList<>();
    private Map<String, Object> mListItemArea = new HashMap<>();
    private Map<String, Object> mListItemSection = new HashMap<>();
    private Map<String, Object> mListItemTunnel = new HashMap<>();
    private Map<String, Object> mListItemRoute = new HashMap<>();
    private Map<String, Object> mListItemGanhao = new HashMap<>();
    private AssetsDatabaseManager mMg;
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
    private String mStartTime;
    private Date mStartTime2Date;
    private String mEndTime;
    private Date mEndTime2Date;
    private String mStartDate;
    private String mEndDate;
    private int num1 = 0, num2 = 0, num3 = 0, num5 = 0, num6 = 0, num7 = 0, num8 = 0, num9 = 0, num10 = 0, num11 = 0;
    private SimpleAdapter mSpAdapter;
    private SearchBean mSearchBean1, mSearchBean2, mSearchBean3, mSearchBean5, mSearchBean6, mSearchBean7, mSearchBean8, mSearchBean9, mSearchBean10, mSearchBean11;
    private String mA1 = "", mA2 = "", mA3 = "", mA5 = "", mA6 = "", mA7 = "", mA8 = "", mA9 = "", mA10 = "", mA11 = "";
    private String mSelectType;
    private String mSearchName = "";
    private String mSearchState = "";
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
    private String TAG = SearchFragment.class.getSimpleName();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_search, container, false);
        ButterKnife.bind(this, mView);
        // 初始化，只需要调用一次
        AssetsDatabaseManager.initManager(mView.getContext());
        // 获取管理对象，因为数据库需要通过管理对象才能够获取
        mMg = AssetsDatabaseManager.getManager();
        // 通过管理对象获取数据库
        mDb = mMg.getDatabase("DJJ-8data.db");
        if (!isPrepared) {
            setSpinner();
        }
        isPrepared = true;
        return mView;
    }

    @Override
    protected void lazyLoad() {
        if (!isPrepared || !isVisible) {
            return;
        }
         /*是否配置过路线等*/
        mSelectedGanhaoId = SharedPreferencesUtil.getStringData(mView.getContext(), "s_ganhaoId", "");
        mSelectedSectionId = SharedPreferencesUtil.getStringData(mView.getContext(), "s_tunnelId", "");
        if (SharedPreferencesUtil.getBooleanData(mView.getContext(), "s_set", false) && mSelectedGanhaoId != "") {
            Log.e(TAG + "-lazyLoad", "测试测试-----");
            clickGanhao(mSelectedGanhaoId);
        } else {
            setSpinner();
        }
//        setSpinner();
    }

    @Override
    public synchronized void onResume() {
        super.onResume();
        Log.e(TAG + "-onResume", "+++++++++");
        if(SharedPreferencesUtil.getBooleanData(mView.getContext(),MultiLineChartActivity.CHART_BACK,false)==false){
            Log.e(TAG + "-onResume", "--------");
            setSpinner();
        }
        SharedPreferencesUtil.saveBooleanData(mView.getContext(),MultiLineChartActivity.CHART_BACK,false);
//        showChart();
    }

    /**
     * 设置路线、工区、区间、隧道的下拉框
     */
    private void setSpinner() {
        Cursor cursorRoute;
        try {
            // 对数据库进行操作
            cursorRoute = mDb.rawQuery("SELECT* FROM ELL_Route", null);
            // 创建一个List集合，List集合的元素是Map
            // TODO: 2018/9/6  
            mListItemsRoute = new ArrayList<>();
            mListItemRoute = new HashMap<>();
            while (cursorRoute.moveToNext()) {
                mListItemRoute = new HashMap<>();
                mListItemRoute.put("RouteId", cursorRoute.getString(cursorRoute.getColumnIndex("RouteId")));
                mListItemRoute.put("RouteName", cursorRoute.getString(cursorRoute.getColumnIndex("RouteName")));
                mListItemsRoute.add(mListItemRoute);
            }
            Log.e(TAG + "-setSpinner", "mListItemsRoute.size为" + mListItemsRoute.size());
            if (mListItemsRoute.size() == 0) {
                mSpRoute.setVisibility(View.GONE);
                mSpArea.setVisibility(View.GONE);
                mSpSection.setVisibility(View.GONE);
                mSpTunnel.setVisibility(View.GONE);
                mSpGanhao.setVisibility(View.GONE);

                mTvRoute.setVisibility(View.VISIBLE);
                mTvArea.setVisibility(View.VISIBLE);
                mTvSection.setVisibility(View.VISIBLE);
                mTvTunnel.setVisibility(View.VISIBLE);
                mTvGanhao.setVisibility(View.VISIBLE);
            } else {
                mSpRoute.setVisibility(View.VISIBLE);
                mSpArea.setVisibility(View.VISIBLE);
                mSpSection.setVisibility(View.VISIBLE);
                mSpTunnel.setVisibility(View.VISIBLE);
                mSpGanhao.setVisibility(View.VISIBLE);

                mTvRoute.setVisibility(View.GONE);
                mTvArea.setVisibility(View.GONE);
                mTvSection.setVisibility(View.GONE);
                mTvTunnel.setVisibility(View.GONE);
                mTvGanhao.setVisibility(View.GONE);
            }
            /**
             * 下拉列表 路线
             * */
            //绑定适配器和值
            SimpleAdapter simpleAdapter = new SimpleAdapter(getContext(), mListItemsRoute,
                    R.layout.dialog_spinner_item,
                    new String[]{"RouteName"},
                    new int[]{R.id.text});
            mSpRoute.setAdapter(simpleAdapter);
            mSpRoute.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    try {
                        /**
                         * 下拉列表 工区
                         */
                        // 对数据库进行操作
                        mSelectedRouteId = mListItemsRoute.get(position).get("RouteId").toString();
                        Cursor cursorArea = mDb.rawQuery("SELECT* FROM ELL_Area WHERE RouteId = ?",
                                new String[]{mSelectedRouteId});
                        // 创建一个List集合，List集合的元素是Map
                        mListItemsArea = new ArrayList<>();
                        mListItemArea = new HashMap<>();
                        while (cursorArea.moveToNext()) {
                            mListItemArea = new HashMap<>();
                            mListItemArea.put("AreaId", cursorArea.getString(cursorArea.getColumnIndex("AreaId")));
                            mListItemArea.put("AreaName", cursorArea.getString(cursorArea.getColumnIndex("AreaName")));
                            mListItemsArea.add(mListItemArea);
                        }
                        Log.e(TAG + "-setSpinner", "listItemsArea.size为" + mListItemsArea.size());
                        if (mListItemsArea.size() == 0) {
                            mListItemArea.put("AreaId", "");
                            mListItemArea.put("AreaName", "工区暂无");
                            mSpArea.setEnabled(false);
                            mListItemsArea.add(mListItemArea);
                        } else {
                            mSpArea.setEnabled(true);
                        }

                        SimpleAdapter simpleAdapter = new SimpleAdapter(getContext(), mListItemsArea,
                                R.layout.dialog_spinner_item,
                                new String[]{"AreaName"},
                                new int[]{R.id.text});
                        mSpArea.setAdapter(simpleAdapter);
                        mSpArea.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                try {
                                    /**
                                     * 下拉列表 区间
                                     **/
                                    mSelectedAreaId = mListItemsArea.get(position).get("AreaId").toString();
                                    Cursor cursorSection = mDb.rawQuery("SELECT* FROM ELL_Section WHERE AreaId = ? ",
                                            new String[]{mSelectedAreaId});
                                    // 创建一个List集合，List集合的元素是Map
                                    mListItemsSection = new ArrayList<>();
                                    mListItemSection = new HashMap<>();
                                    while (cursorSection.moveToNext()) {
                                        mListItemSection = new HashMap<>();
                                        mListItemSection.put("SectionId", cursorSection.getString(cursorSection.getColumnIndex("SectionId")));
                                        mListItemSection.put("SectionName", cursorSection.getString(cursorSection.getColumnIndex("SectionName")));
                                        mListItemsSection.add(mListItemSection);
                                    }
                                    Log.e(TAG + "-setSpinner", "listItemsSection.size为" + mListItemsSection.size());
                                    if (mListItemsSection.size() == 0) {
                                        mListItemSection.put("SectionId", "");
                                        mListItemSection.put("SectionName", "区间暂无");
                                        mSpSection.setEnabled(false);
                                        mListItemsSection.add(mListItemSection);
                                    } else {
                                        mSpSection.setEnabled(true);
                                    }
                                    SimpleAdapter simpleAdapter = new SimpleAdapter(getContext(), mListItemsSection,
                                            R.layout.dialog_spinner_item,
                                            new String[]{"SectionName"},
                                            new int[]{R.id.text});
                                    mSpSection.setAdapter(simpleAdapter);
                                    mSpSection.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                        @Override
                                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                            mSelectedSectionId = mListItemsSection.get(position).get("SectionId").toString();
                                            try {
                                                /**
                                                 * 下拉列表 隧道
                                                 * */
                                                Cursor cursorTunnel = mDb.rawQuery("SELECT* FROM ELL_Tunnel WHERE SectionId = ? ",
                                                        new String[]{mSelectedSectionId});
                                                // 创建一个List集合，List集合的元素是Map
                                                mListItemsTunnel = new ArrayList<>();
                                                mListItemTunnel = new HashMap<>();
                                                while (cursorTunnel.moveToNext()) {
                                                    mListItemTunnel = new HashMap<>();
                                                    mListItemTunnel.put("TunnelId", cursorTunnel.getString(cursorTunnel.getColumnIndex("TunnelId")));
                                                    mListItemTunnel.put("TunnelName", cursorTunnel.getString(cursorTunnel.getColumnIndex("TunnelName")));
                                                    mListItemsTunnel.add(mListItemTunnel);
                                                }

                                                Log.e(TAG + "-ceshi", "size为" + mListItemsTunnel.size());
                                                if (mListItemsTunnel.size() == 0) {
                                                    mListItemTunnel.put("TunnelId", "");
                                                    mListItemTunnel.put("TunnelName", "隧道暂无");
                                                    mSpTunnel.setEnabled(false);
                                                    mListItemsTunnel.add(mListItemTunnel);
                                                } else {
                                                    mSpTunnel.setEnabled(true);
                                                }

                                                SimpleAdapter simpleAdapter = new SimpleAdapter(getContext(), mListItemsTunnel,
                                                        R.layout.dialog_spinner_item,
                                                        new String[]{"TunnelName"},
                                                        new int[]{R.id.text});
                                                mSpTunnel.setAdapter(simpleAdapter);

                                                mSpTunnel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                    @Override
                                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                                        mSelectedTunnelId = mListItemsTunnel.get(position).get("TunnelId").toString();
                                                        Log.e(TAG + "-setSpinner", "mSelectedTunnel为" + mSelectedTunnelId);
//                                                        getTable();
                                                        showChart();
                                                    }

                                                    @Override
                                                    public void onNothingSelected(AdapterView<?> parent) {
                                                    }
                                                });
                                                cursorTunnel.close();
                                            } catch (Exception e) {
                                                Log.e(TAG + "-setSpinner(tunnel)", "数据库报错:" + e.toString());
                                            }

                                        }

                                        @Override
                                        public void onNothingSelected(AdapterView<?> parent) {

                                        }
                                    });
                                    cursorSection.close();
                                } catch (Exception e) {
                                    Log.e(TAG + "-setSpinner(section)", "数据库报错:" + e.toString());
                                }

                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                        cursorArea.close();
                    } catch (Exception e) {
                        Log.e(TAG + "-setSpinner(area)", "数据库报错:" + e.toString());
                    }

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            cursorRoute.close();
        } catch (Exception e) {
            Log.e(TAG + "-setSpinner", "数据库报错:" + e.toString());

        }
//        getTable();//重新选择隧道，时间和杆号选择不更新
    }

    /**
     * 获取 杆号，测量类型和表的内容
     */

    private void showChart() {
        Log.e(TAG + "-showChart", "mStartTime为" + mStartTime);
        mStartDate = mTvStarttime.getText().toString();
        mEndDate = mTvEndtime.getText().toString();
        Log.e(TAG + "-showChart", "mStartDate为" + mStartDate + ";mEndDate为" + mEndDate);
        if (mStartTime != null && !mStartDate.equals("-点击选择-") && mStartDate.length() >= 10 && mEndDate.length() >= 10) {
//            mStartDate = mTvStarttime.getText().toString();
//            mEndDate = mTvEndtime.getText().toString();
            mStartDate = mStartDate.substring(0, 4) + "年" + mStartDate.substring(5, 7) + "月" + mStartDate.substring(8, 10) + "日 00:00:00";//2018年07月09日
            mEndDate = mEndDate.substring(0, 4) + "年" + mEndDate.substring(5, 7) + "月" + mEndDate.substring(8, 10) + "日 23:59:59";//2018年07月09日
            Log.e(TAG + "-showChart", "mStartTime-" + mStartTime + ";mEndTime-" + mEndTime);
            Log.e(TAG + "-showChart(itemSelect)", "mSelectedGanhaoId为" + mSelectedGanhaoId + ";mStartDate为" + mStartDate + ";mEndDate为" + mEndDate);
        } else {
            mStartTime = "1990-01-01";
            Calendar calendar = Calendar.getInstance();
            String month = calendar.get(calendar.MONTH) + 1 + "";
            if (month.length() == 1) {
                month = "0" + month;
            }
            mEndTime = calendar.get(calendar.YEAR) + "-" + month + "-" + calendar.get(calendar.DATE);
            Log.e(TAG + "-showChart", "mEndTime为" + mEndTime);
            mTvStarttime.setText(mStartTime);
            mTvEndtime.setText(mEndTime);
            mStartDate = "1990年01月01日 00:00:00";
            mEndDate = calendar.get(calendar.YEAR) + "年" + month + "月" + calendar.get(calendar.DATE) + "日 23:59:59";
        }
        try {
            //下拉杆号 列表
            // 对数据库进行操作
            Cursor cursorGanhao = null;
            Log.e(TAG + "-showChart", "mSelectedTunnelId为" + mSelectedTunnelId);
            if (mSelectedTunnelId != null) {
                cursorGanhao = mDb.rawQuery("SELECT* FROM ELL_PoleData WHERE TunnelId = ? AND Date BETWEEN ? AND ?",
                        new String[]{mSelectedTunnelId, mStartDate, mEndDate});
            } else if (mSelectedSectionId != null) {
                cursorGanhao = mDb.rawQuery("SELECT* FROM ELL_PoleData WHERE SectionId = ? AND Date BETWEEN ? AND ?",
                        new String[]{mSelectedSectionId, mStartDate, mEndDate});
            }
            if (cursorGanhao != null) {
                // 创建一个List集合，List集合的元素是Map
                mListItemsGanhao = new ArrayList<>();
                mListItemGanhao = new HashMap<>();
                while (cursorGanhao.moveToNext()) {
                    mListItemGanhao = new HashMap<>();
                    String ganhaoId = cursorGanhao.getString(cursorGanhao.getColumnIndex("DataId"));
                    mListItemGanhao.put("DataId", ganhaoId);
                    mListItemsGanhao.add(mListItemGanhao);
                }
                Log.e(TAG + "-showChart", "mListItemsGanhao.size为" + mListItemsGanhao.size());
                if (mListItemsGanhao.size() == 0) {
                    mListItemGanhao.put("DataId", "杆号暂无");
                    SharedPreferencesUtil.saveBooleanData(mView.getContext(), "s_set", false);
                    SharedPreferencesUtil.saveStringData(mView.getContext(), "s_tunnelId", "");
                    SharedPreferencesUtil.saveStringData(mView.getContext(), "s_ganhaoId", "");
                    mSpGanhao.setEnabled(false);
                    mHRecyclerView.setVisibility(View.GONE);
                } else {
                    SharedPreferencesUtil.saveBooleanData(mView.getContext(), "s_set", true);
                    SharedPreferencesUtil.saveStringData(mView.getContext(), "s_tunnelId", mSelectedTunnelId);
                    SharedPreferencesUtil.saveStringData(mView.getContext(), "s_ganhaoId", mSelectedGanhaoId);
                    mSpGanhao.setEnabled(true);
                    mHRecyclerView.setVisibility(View.VISIBLE);
                }
                SimpleAdapter simpleAdapter = new SimpleAdapter(getContext(), mListItemsGanhao,
                        R.layout.dialog_spinner_item,
                        new String[]{"DataId"},
                        new int[]{R.id.text});
                mSpGanhao.setAdapter(simpleAdapter);
                mSpGanhao.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                        mSelectedGanhaoId = mListItemsGanhao.get(position).get("DataId").toString();
                        Log.e(TAG + "-showChart", "mSelectedGanhaoId为" + mSelectedGanhaoId);
                        SharedPreferencesUtil.saveBooleanData(mView.getContext(), "s_set", true);
                        SharedPreferencesUtil.saveStringData(mView.getContext(), "s_tunnelId", mSelectedTunnelId);
                        SharedPreferencesUtil.saveStringData(mView.getContext(), "s_ganhaoId", mSelectedGanhaoId);
                        clickGanhao(mSelectedGanhaoId);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {
                        mHRecyclerView.setVisibility(View.GONE);
                    }
                });
            }
        } catch (Exception e) {
            Log.e(TAG + "-showChart", "数据库报错" + e.toString());
        }
    }

    private void clickGanhao(String selectedGanhaoId) {
        mDataModels1 = new ArrayList<>();
        mDataModels2 = new ArrayList<>();
        mDataModels3 = new ArrayList<>();
        mDataModels5 = new ArrayList<>();
        mDataModels6 = new ArrayList<>();
        mDataModels7 = new ArrayList<>();
        mDataModels8 = new ArrayList<>();
        mDataModels9 = new ArrayList<>();
        mDataModels10 = new ArrayList<>();
        mDataModels11 = new ArrayList<>();
        mA1 = "";
        mA2 = "";
        mA3 = "";
        mA5 = "";
        mA6 = "";
        mA7 = "";
        mA8 = "";
        mA9 = "";
        mA10 = "";
        mA11 = "";
        mListItemsType = new ArrayList<>();
        mListItemsName = new ArrayList<>();
        getName(selectedGanhaoId);
        setData();
    }

    /**
     * 获取 名称
     */
    private void getName(String ganhaoId) {
        try {
            /**
             * 下拉杆号 列表
             * */
            // 对数据库进行操作
            mCursor1 = mDb.rawQuery("SELECT* FROM ELL_D1Data WHERE Num = ?",
                    new String[]{ganhaoId});
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
                    new String[]{ganhaoId});
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
                    new String[]{ganhaoId});
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
                    new String[]{ganhaoId});
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
                    new String[]{ganhaoId});
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
                    new String[]{ganhaoId});
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
                    new String[]{ganhaoId});
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
                    new String[]{ganhaoId});
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
                    new String[]{ganhaoId});
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
                    new String[]{ganhaoId});
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

        mSpAdapter = new SimpleAdapter(getContext(), mListItemsName,
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
        mHRecyclerView.removeAllViews();
        Log.e(TAG + "-showData", "mListItemsName.size()为" + mListItemsName.size() + "；mListItemsType.size()为" + mListItemsType.size());
        switch (state) {
            case "d1":
                Log.e(TAG + "-showData", "mDataModels1.size()为" + mDataModels1.size());
                mHRecyclerView.setHeaderListData(getResources().getStringArray(R.array.search_title_d1));
                setAdapter(state, mDataModels1);
                break;
            case "d2":
                mHRecyclerView.setHeaderListData(getResources().getStringArray(R.array.search_title_d2));
                setAdapter(state, mDataModels2);
                break;
            case "d3":
                Log.e(TAG + "-showData(3)", "mDataModels3.size()为" + mDataModels3.size());
                mHRecyclerView.setHeaderListData(getResources().getStringArray(R.array.search_title_d3));
                setAdapter(state, mDataModels3);
                break;
            case "d5":
                Log.e(TAG + "-showData(5)", "mDataModels5.size()为" + mDataModels5.size());
                mHRecyclerView.setHeaderListData(getResources().getStringArray(R.array.search_title_d5));
                setAdapter(state, mDataModels5);
                break;
            case "d6":
                Log.e(TAG + "-showData(6)", "mDataModels6.size()为" + mDataModels6.size());
                mHRecyclerView.setHeaderListData(getResources().getStringArray(R.array.search_title_d6));
                setAdapter(state, mDataModels6);
                break;
            case "d7":
                Log.e(TAG + "-showData(7)", "mDataModels7.size()为" + mDataModels7.size());
                mHRecyclerView.setHeaderListData(getResources().getStringArray(R.array.search_title_d7));
                setAdapter(state, mDataModels7);
                break;
            case "d8":
                Log.e(TAG + "-showData(8)", "mDataModels8.size()为" + mDataModels8.size());
                mHRecyclerView.setHeaderListData(getResources().getStringArray(R.array.search_title_d8));
                setAdapter(state, mDataModels8);
                break;
            case "d9":
                mHRecyclerView.setHeaderListData(getResources().getStringArray(R.array.search_title_d9));
                setAdapter(state, mDataModels9);
                break;
            case "dc":
                mHRecyclerView.setHeaderListData(getResources().getStringArray(R.array.search_title_dc));
                setAdapter(state, mDataModels10);
                break;
            case "de":
                mHRecyclerView.setHeaderListData(getResources().getStringArray(R.array.search_title_de));
                setAdapter(state, mDataModels11);
                break;
        }
    }

    private void setAdapter(String state, List<SearchBean> list) {
        mHRecyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                mHRecyclerView.getParent().requestDisallowInterceptTouchEvent(true);
                int x = (int) event.getRawX();
                int y = (int) event.getRawY();

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mLastX = x;
                        mLastY = y;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int deltaY = y - mLastY;
                        int deltaX = x - mLastX;
                        if (Math.abs(deltaX) < Math.abs(deltaY)) {
                            mHRecyclerView.getParent().requestDisallowInterceptTouchEvent(false);
                        } else {
                            mHRecyclerView.getParent().requestDisallowInterceptTouchEvent(true);
                        }
                    default:
                        break;
                }
                return false;
            }
        });

        mAdapter = new HRecyclerAdapter(getContext(), state, list, R.layout.item_search, new HRecyclerViewHolder.onItemCommonClickListener() {
            @Override
            public void onItemClickListener(int position) {
                Log.e(TAG + "-setAdapter", "position为" + position);
                Intent intent = new Intent(getContext(), MultiLineChartActivity.class);
//                intent.putExtra("state", mSelectType);
//                intent.putExtra("ganhao", mSelectedGanhaoId);
//                intent.putExtra("tunnelId", mSelectedTunnelId);
//                startActivity(intent);
                intent.putExtra(MultiLineChartActivity.CHOOSE_STATE, mSelectType);
                intent.putExtra(MultiLineChartActivity.CHOOSE_GANHAO, mSelectedGanhaoId);
                intent.putExtra(MultiLineChartActivity.CHOOSE_TUNNELID, mSelectedTunnelId);
                startActivityForResult(intent, CHOOSE_BACK_SEARCH);
            }

            @Override
            public void onItemLongClickListener(int position) {

            }
        });
        mAdapter.clearAndRefresh(getContext());
        mHRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case CHOOSE_BACK_HOME:
                if (resultCode != RESULT_OK) {
                    return;
                } else {
                    mSelectType = data.getStringExtra(MultiLineChartActivity.CHOOSE_STATE) == null ? "" : data.getStringExtra(MultiLineChartActivity.CHOOSE_STATE);
                    mSelectedGanhaoId = data.getStringExtra(MultiLineChartActivity.CHOOSE_GANHAO) == null ? "" : data.getStringExtra(MultiLineChartActivity.CHOOSE_GANHAO);
                    mSelectedTunnelId = data.getStringExtra(MultiLineChartActivity.CHOOSE_TUNNELID) == null ? "" : data.getStringExtra(MultiLineChartActivity.CHOOSE_TUNNELID);
                    Log.e(TAG + "-onActivity", "mSelectType-" + mSelectType + ";mSelectedGanhaoId-" + mSelectedGanhaoId);
                    clickGanhao(mSelectedGanhaoId);
                    /*储存设置*/
                    if (mSelectedTunnelId.length() > 0) {
                        SharedPreferencesUtil.saveBooleanData(mView.getContext(), "s_set", true);
                        SharedPreferencesUtil.saveStringData(mView.getContext(), "s_tunnelId", mSelectedTunnelId);
                        SharedPreferencesUtil.saveStringData(mView.getContext(), "s_ganhaoId", mSelectedGanhaoId);
                    }
                }
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @OnClick({R.id.fragment_search_tv_more, R.id.fragment_search_rl_time})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fragment_search_tv_more:
                // TODO: 2018/7/24 fragment更改表的时候修改
                if (mSelectedGanhaoId != "" && mSelectedGanhaoId.length() > 0) {
                    Log.e(TAG + "-onClick", "mSelectGanhaoId为" + mSelectedGanhaoId);
                    Intent intent = new Intent(getContext(), MoreHistoryActivity.class).putExtra("ganhao", mSelectedGanhaoId);
                    startActivity(intent);
                } else {
                    Toast.makeText(getContext(), "请选择杆号", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.fragment_search_rl_time:
                Calendar c = Calendar.getInstance();
                // 最后一个false表示不显示日期，如果要显示日期，最后参数可以是true或者不用输入
                new DoubleDatePickerDialog(getContext(), 0, new DoubleDatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker startDatePicker, int startYear, int startMonthOfYear,
                                          int startDayOfMonth, DatePicker endDatePicker, int endYear, int endMonthOfYear,
                                          int endDayOfMonth) {

                        if (startYear > endYear) {
                            Toast.makeText(getContext(), "开始日期不能晚于结束日期", Toast.LENGTH_LONG).show();
                            return;
                        } else if (startYear == endYear) {
                            if (startMonthOfYear > endMonthOfYear) {
                                Toast.makeText(getContext(), "开始日期不能晚于结束日期", Toast.LENGTH_LONG).show();
                                return;
                            } else if (startMonthOfYear == endMonthOfYear) {
                                if (startDayOfMonth > endDayOfMonth) {
                                    Toast.makeText(getContext(), "开始日期不能晚于结束日期", Toast.LENGTH_LONG).show();
                                    return;
                                } else {
                                    mStartTime = String.format("%d-%d-%d", startYear, startMonthOfYear + 1, startDayOfMonth);
                                    mStartTime2Date = StringOrDate.stringToDate(mStartTime);
                                    mTvStarttime.setText(mStartTime2Date.toString());
                                    mEndTime = String.format("%d-%d-%d", endYear, endMonthOfYear + 1, endDayOfMonth);
                                    mEndTime2Date = StringOrDate.stringToDate(mEndTime);
                                    mTvEndtime.setText(mEndTime2Date.toString());
                                }
                            } else {
                                mStartTime = String.format("%d-%d-%d", startYear, startMonthOfYear + 1, startDayOfMonth);
                                mStartTime2Date = StringOrDate.stringToDate(mStartTime);
                                mTvStarttime.setText(mStartTime2Date.toString());
                                mEndTime = String.format("%d-%d-%d", endYear, endMonthOfYear + 1, endDayOfMonth);
                                mEndTime2Date = StringOrDate.stringToDate(mEndTime);
                                mTvEndtime.setText(mEndTime2Date.toString());
                            }

                        } else {
                            mStartTime = String.format("%d-%d-%d", startYear, startMonthOfYear + 1, startDayOfMonth);
                            mStartTime2Date = StringOrDate.stringToDate(mStartTime);
                            mTvStarttime.setText(mStartTime2Date.toString());
                            mEndTime = String.format("%d-%d-%d", endYear, endMonthOfYear + 1, endDayOfMonth);
                            mEndTime2Date = StringOrDate.stringToDate(mEndTime);
                            mTvEndtime.setText(mEndTime2Date.toString());
                        }
                        Log.e(TAG + "-onClick", "mStartTime-" + mStartTime + ";mEndTime-" + mEndTime + ";mStartTime2Date-" + mStartTime2Date.toString());
//                        getTable();
                        showChart();
                    }
                }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE), true).show();
                break;
        }
    }

    @Override
    public void onDestroyView() {
        if (this.mUnBind != null) {
            this.mUnBind.unbind();
        }
        super.onDestroyView();
    }
    /**
     * 显示每种测量类型的数目，显示在图表中
     * */
    /*    private void getTable() {
        Log.e(TAG + "-getTable", "mStartTime为" + mStartTime);
        mStartDate = mTvStarttime.getText().toString();
        mEndDate = mTvEndtime.getText().toString();
        Log.e(TAG + "-getTable", "mStartDate为" + mStartDate + ";mEndDate为" + mEndDate);
        if (mStartTime != null && !mStartDate.equals("-点击选择-")) {
//            mStartDate = mTvStarttime.getText().toString();
//            mEndDate = mTvEndtime.getText().toString();
            mStartDate = mStartDate.substring(0, 4) + "年" + mStartDate.substring(5, 7) + "月" + mStartDate.substring(8, 10) + "日 00:00:00";//2018年07月09日
            mEndDate = mEndDate.substring(0, 4) + "年" + mEndDate.substring(5, 7) + "月" + mEndDate.substring(8, 10) + "日 23:59:59";//2018年07月09日
            Log.e(TAG + "-getTable", "mStartTime-" + mStartTime + ";mEndTime-" + mEndTime);
            Log.e(TAG + "-getTable(itemSelect)", "mSelectedGanhaoId为" + mSelectedGanhaoId + ";mStartDate为" + mStartDate + ";mEndDate为" + mEndDate);
        } else {
            mStartTime = "1990-01-01";
            Calendar calendar = Calendar.getInstance();
            String month = calendar.get(calendar.MONTH) + 1 + "";
            if (month.length() == 1) {
                month = "0" + month;
            }
            mEndTime = calendar.get(calendar.YEAR) + "-" + month + "-" + calendar.get(calendar.DATE);
            Log.e(TAG + "-getTable", "mEndTime为" + mEndTime);
            mTvStarttime.setText(mStartTime);
            mTvEndtime.setText(mEndTime);
            mStartDate = "1990年01月01日 00:00:00";
            mEndDate = calendar.get(calendar.YEAR) + "年" + month + "月" + calendar.get(calendar.DATE) + "日 23:59:59";
        }
        try {
            //下拉杆号 列表
            // 对数据库进行操作
            Cursor cursorGanhao = null;
            Log.e(TAG + "-getTable", "mSelectedTunnelId为" + mSelectedTunnelId);
            if (mSelectedTunnelId != null) {
                cursorGanhao = mDb.rawQuery("SELECT* FROM ELL_PoleData WHERE TunnelId = ?",
                        new String[]{mSelectedTunnelId});
            } else if (mSelectedSectionId != null) {
                cursorGanhao = mDb.rawQuery("SELECT* FROM ELL_PoleData WHERE SectionId = ?",
                        new String[]{mSelectedSectionId});
            }
            if (cursorGanhao != null) {
                // 创建一个List集合，List集合的元素是Map
                mListItemsGanhao = new ArrayList<>();
                mListItemGanhao = new HashMap<>();
                while (cursorGanhao.moveToNext()) {
                    mListItemGanhao = new HashMap<>();
                    String ganhaoId = cursorGanhao.getString(cursorGanhao.getColumnIndex("DataId"));
                    mListItemGanhao.put("DataId", ganhaoId);
                    mListItemsGanhao.add(mListItemGanhao);
                }
                Log.e(TAG + "-getTable", "mListItemsGanhao.size为" + mListItemsGanhao.size());
                if (mListItemsGanhao.size() == 0) {
                    mListItemGanhao.put("DataId", "无");
                    mSpGanhao.setEnabled(false);
                    mHRecyclerView.setVisibility(View.GONE);
                } else {
                    mSpGanhao.setEnabled(true);
                    mHRecyclerView.setVisibility(View.VISIBLE);
                }
                SimpleAdapter simpleAdapter = new SimpleAdapter(getContext(), mListItemsGanhao,
                        R.layout.dialog_spinner_item,
                        new String[]{"DataId"},
                        new int[]{R.id.text});
                mSpGanhao.setAdapter(simpleAdapter);
                mSpGanhao.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                        mHRecyclerView.setVisibility(View.VISIBLE);
                        mSelectedGanhaoId = mListItemsGanhao.get(position).get("DataId").toString();
                        num1 = 0;
                        num2 = 0;
                        num3 = 0;
                        num5 = 0;
                        num6 = 0;
                        num7 = 0;
                        num8 = 0;
                        num9 = 0;
                        num10 = 0;
                        num11 = 0;
                        mCursor1 = mDb.rawQuery("SELECT* FROM ELL_D1Data WHERE Num = ? AND Date BETWEEN ? AND ?",
                                new String[]{mSelectedGanhaoId, mStartDate, mEndDate});
                        while (mCursor1.moveToNext()) {
                            String dataId = mCursor1.getString(mCursor1.getColumnIndex("DataId"));
                            Log.e(TAG + "-getTable(1)", "dataId为" + dataId);
                            num1++;
                        }
                        mCursor2 = mDb.rawQuery("SELECT* FROM ELL_D2Data WHERE Num = ? AND Date BETWEEN ? AND ?",
                                new String[]{mSelectedGanhaoId, mStartDate, mEndDate});
                        while (mCursor2.moveToNext()) {
                            mDataModels = new ArrayList<>();
                            String dataId = mCursor2.getString(mCursor2.getColumnIndex("DataId"));
                            Log.e(TAG + "-getTable(2)", "dataId为" + dataId);
                            num2++;
                        }
                        mCursor3 = mDb.rawQuery("SELECT* FROM ELL_D3Data WHERE Num = ? AND Date BETWEEN ? AND ?",
                                new String[]{mSelectedGanhaoId, mStartDate, mEndDate});
                        while (mCursor3.moveToNext()) {
                            mDataModels = new ArrayList<>();
                            String dataId = mCursor3.getString(mCursor3.getColumnIndex("DataId"));
                            Log.e(TAG + "-getTable(3)", "dataId为" + dataId);
                            num3++;
                        }
                        mCursor5 = mDb.rawQuery("SELECT* FROM ELL_D5Data WHERE Num = ? AND Date BETWEEN ? AND ?",
                                new String[]{mSelectedGanhaoId, mStartDate, mEndDate});
                        while (mCursor5.moveToNext()) {
                            mDataModels = new ArrayList<>();
                            String dataId = mCursor5.getString(mCursor5.getColumnIndex("DataId"));
                            Log.e(TAG + "-getTable(5)", "dataId为" + dataId);
                            num5++;
                        }
                        mCursor6 = mDb.rawQuery("SELECT* FROM ELL_D6Data WHERE Num = ? AND Date BETWEEN ? AND ?",
                                new String[]{mSelectedGanhaoId, mStartDate, mEndDate});
                        while (mCursor6.moveToNext()) {
                            mDataModels = new ArrayList<>();
                            String dataId = mCursor6.getString(mCursor6.getColumnIndex("DataId"));
                            Log.e(TAG + "-getTable(6)", "dataId为" + dataId);
                            num6++;
                        }
                        mCursor7 = mDb.rawQuery("SELECT* FROM ELL_D7Data WHERE Num = ? AND Date BETWEEN ? AND ?",
                                new String[]{mSelectedGanhaoId, mStartDate, mEndDate});
                        while (mCursor7.moveToNext()) {
                            mDataModels = new ArrayList<>();
                            String dataId = mCursor7.getString(mCursor7.getColumnIndex("DataId"));
                            Log.e(TAG + "-getTable(7)", "dataId为" + dataId);
                            num7++;
                        }
                        mCursor8 = mDb.rawQuery("SELECT* FROM ELL_D8Data WHERE Num = ? AND Date BETWEEN ? AND ?",
                                new String[]{mSelectedGanhaoId, mStartDate, mEndDate});
                        while (mCursor8.moveToNext()) {
                            mDataModels = new ArrayList<>();
                            String dataId = mCursor8.getString(mCursor8.getColumnIndex("DataId"));
                            Log.e(TAG + "-getTable(8)", "dataId为" + dataId);
                            num8++;
                        }
                        mCursor9 = mDb.rawQuery("SELECT* FROM ELL_D9Data WHERE Num = ? AND Date BETWEEN ? AND ?",
                                new String[]{mSelectedGanhaoId, mStartDate, mEndDate});
                        while (mCursor9.moveToNext()) {
                            mDataModels = new ArrayList<>();
                            String dataId = mCursor9.getString(mCursor9.getColumnIndex("DataId"));
                            Log.e(TAG + "-getTable(9)", "dataId为" + dataId);
                            num9++;
                        }
                        mCursor10 = mDb.rawQuery("SELECT* FROM ELL_DCData WHERE Num = ? AND Date BETWEEN ? AND ?",
                                new String[]{mSelectedGanhaoId, mStartDate, mEndDate});
                        while (mCursor10.moveToNext()) {
                            mDataModels = new ArrayList<>();
                            String dataId = mCursor10.getString(mCursor10.getColumnIndex("DataId"));
                            Log.e(TAG + "-getTable(10)", "dataId为" + dataId);
                            num10++;
                        }
                        mCursor11 = mDb.rawQuery("SELECT* FROM ELL_DEData WHERE Num = ? AND Date BETWEEN ? AND ?",
                                new String[]{mSelectedGanhaoId, mStartDate, mEndDate});
                        while (mCursor11.moveToNext()) {
                            mDataModels = new ArrayList<>();
                            String dataId = mCursor11.getString(mCursor11.getColumnIndex("DataId"));
                            Log.e(TAG + "-getTable(11)", "dataId为" + dataId);
                            num11++;
                        }
                        Log.e(TAG + "-getTable", "num1为" + num1 + ";num2为" + num2 + ";num3为" + num3 + ";num5为" + num5 + ";num6为" + num6 + ";num7为" + num7 + ";num8为" + num8 + ";num9为" + num9);
                        Log.e(TAG + "-getTable", "num10为" + num10 + ";num11为" + num11);
                        List<SearchBean> list = new ArrayList<>();
                        SearchBean searchBean = new SearchBean();
                        for (int i = 0; i < 10; i++) {
                            searchBean.name = "数目";
                            searchBean.num = num1 + "";
                            searchBean.d1_dg = num2 + "";
                            searchBean.d1_lcz = num3 + "";
                            searchBean.d1_gj = num5 + "";
                            searchBean.d1_cg = num6 + "";
                            searchBean.date = num7 + "";
                            searchBean.note = num8 + "";
                            searchBean.count10 = num9 + "";
                            searchBean.count11 = num10 + "";
                            searchBean.count12 = num11 + "";
                        }
                        list.add(searchBean);
                        setAdapter(list);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {
                        mHRecyclerView.setVisibility(View.GONE);
                    }
                });
            }
        } catch (Exception e) {
            Log.e(TAG + "-getTable", "数据库报错" + e.toString());
        }
    }

    private void setAdapter(List<SearchBean> list) {
        mHRecyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                mHRecyclerView.getParent().requestDisallowInterceptTouchEvent(true);
                int x = (int) event.getRawX();
                int y = (int) event.getRawY();

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mLastX = x;
                        mLastY = y;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int deltaY = y - mLastY;
                        int deltaX = x - mLastX;
                        if (Math.abs(deltaX) < Math.abs(deltaY)) {
                            mHRecyclerView.getParent().requestDisallowInterceptTouchEvent(false);
                        } else {
                            mHRecyclerView.getParent().requestDisallowInterceptTouchEvent(true);
                        }
                    default:
                        break;
                }
                return false;
            }
        });
        mHRecyclerView.setHeaderListData(getResources().getStringArray(R.array.search_title));
        mAdapter = new HRecyclerAdapter(getContext(), "SearchFragment", list, R.layout.item_search, new HRecyclerViewHolder.onItemCommonClickListener() {
            @Override
            public void onItemClickListener(int position) {
                Log.e(TAG + "-setAdapter", "position为" + position);
            }

            @Override
            public void onItemLongClickListener(int position) {

            }
        });
        mHRecyclerView.setAdapter(mAdapter);
    }*/

    /*************************************************************************************88*/


    /**
     * 得到最新一条的历史数据，显示在图表中
     */
/*    private void getNewData(String state) {
        Log.e(TAG + "-getNewData", "state为" + state);
        if (mListItemsGanhao.size() > 0) {
            mDataModels = new ArrayList<>();
            switch (state) {
                case "d1":
                    mHRecyclerView.setHeaderListData(getResources().getStringArray(R.array.search_title_d1));
                    mDataModels = new ArrayList<>();
                    SearchBean searchBean1 = new SearchBean();
                    searchBean1.name = "基础测量";
                    searchBean1.num = mCursor1.getString(mCursor1.getColumnIndex("Num"));
                    searchBean1.d1_dg = mCursor1.getString(mCursor1.getColumnIndex("D1_DG"));
                    searchBean1.d1_lcz = mCursor1.getString(mCursor1.getColumnIndex("D1_LCZ"));
                    searchBean1.d1_gj = mCursor1.getString(mCursor1.getColumnIndex("D1_GJ"));
                    searchBean1.d1_cg = mCursor1.getString(mCursor1.getColumnIndex("D1_CG"));
                    searchBean1.date = mCursor1.getString(mCursor1.getColumnIndex("Date"));
                    searchBean1.note = mCursor1.getString(mCursor1.getColumnIndex("Note"));
                    mDataModels.add(searchBean1);
                    break;
                case "d2":
                    mHRecyclerView.setHeaderListData(getResources().getStringArray(R.array.search_title_d2));
                    SearchBean searchBean2 = new SearchBean();
                    searchBean2.name = "线岔中心测量";
                    searchBean2.num = mCursor2.getString(mCursor2.getColumnIndex("Num"));
                    searchBean2.d1_dg = mCursor2.getString(mCursor2.getColumnIndex("D2_DG"));
                    searchBean2.d1_lcz = mCursor2.getString(mCursor2.getColumnIndex("D2_PLZ"));
                    searchBean2.d1_gj = mCursor2.getString(mCursor2.getColumnIndex("D2_NGJ"));
                    searchBean2.date = mCursor2.getString(mCursor2.getColumnIndex("Date"));
                    searchBean2.note = mCursor2.getString(mCursor2.getColumnIndex("Note"));
                    mDataModels.add(searchBean2);
                    break;
                case "d3":
                    mHRecyclerView.setHeaderListData(getResources().getStringArray(R.array.search_title_d3));
                    SearchBean searchBean3 = new SearchBean();
                    searchBean3.name = "限界测量";
                    searchBean3.num = mCursor3.getString(mCursor3.getColumnIndex("Num"));
                    searchBean3.d1_dg = mCursor3.getString(mCursor3.getColumnIndex("D3_HXBG"));
                    searchBean3.d1_lcz = mCursor3.getString(mCursor3.getColumnIndex("D3_CLXJ"));
                    searchBean3.date = mCursor3.getString(mCursor3.getColumnIndex("Date"));
                    searchBean3.note = mCursor3.getString(mCursor3.getColumnIndex("Note"));
                    mDataModels.add(searchBean3);
                    Log.e(TAG + "-d3", "1为" + searchBean3.num + ";2为" + searchBean3.d1_dg + ";3为" + searchBean3.d1_lcz + ";4为" + searchBean3.date + ";5为" + searchBean3.note);
                    break;
                case "d5":
                    mHRecyclerView.setHeaderListData(getResources().getStringArray(R.array.search_title_d5));
                    SearchBean searchBean5 = new SearchBean();
                    searchBean5.name = "非支测量";
                    searchBean5.num = mCursor5.getString(mCursor5.getColumnIndex("Num"));
                    searchBean5.d1_dg = mCursor5.getString(mCursor5.getColumnIndex("D5_FZTG"));
                    searchBean5.d1_lcz = mCursor5.getString(mCursor5.getColumnIndex("D5_FZPL"));
                    searchBean5.date = mCursor5.getString(mCursor5.getColumnIndex("Date"));
                    searchBean5.note = mCursor5.getString(mCursor5.getColumnIndex("Note"));
                    mDataModels.add(searchBean5);
                    break;
                case "d6":
                    mHRecyclerView.setHeaderListData(getResources().getStringArray(R.array.search_title_d6));
                    SearchBean searchBean6 = new SearchBean();
                    searchBean6.name = "定位器测量";
                    searchBean6.num = mCursor6.getString(mCursor6.getColumnIndex("Num"));
                    searchBean6.d1_dg = mCursor6.getString(mCursor6.getColumnIndex("D6_DWQPD"));
                    searchBean6.d1_lcz = mCursor6.getString(mCursor6.getColumnIndex("D6_GC"));
                    searchBean6.date = mCursor6.getString(mCursor6.getColumnIndex("Date"));
                    searchBean6.note = mCursor6.getString(mCursor6.getColumnIndex("Note"));
                    mDataModels.add(searchBean6);
                    break;
                case "d7":
                    mHRecyclerView.setHeaderListData(getResources().getStringArray(R.array.search_title_d7));
                    SearchBean searchBean7 = new SearchBean();
                    searchBean7.name = "承力索高差测量";
                    searchBean7.num = mCursor7.getString(mCursor7.getColumnIndex("Num"));
                    searchBean7.d1_dg = mCursor7.getString(mCursor7.getColumnIndex("D7_GD1"));
                    searchBean7.d1_lcz = mCursor7.getString(mCursor7.getColumnIndex("D7_GD2"));
                    searchBean7.d1_gj = mCursor7.getString(mCursor7.getColumnIndex("D7_GC"));
                    searchBean7.date = mCursor7.getString(mCursor7.getColumnIndex("Date"));
                    searchBean7.note = mCursor7.getString(mCursor7.getColumnIndex("Note"));
                    mDataModels.add(searchBean7);
                    break;
                case "d8":
                    mHRecyclerView.setHeaderListData(getResources().getStringArray(R.array.search_title_d8));
                    SearchBean searchBean8 = new SearchBean();
                    searchBean8.name = "500mm处高差测量";
                    searchBean8.num = mCursor8.getString(mCursor8.getColumnIndex("Num"));
                    searchBean8.d1_dg = mCursor8.getString(mCursor8.getColumnIndex("D8_GD1"));
                    searchBean8.d1_lcz = mCursor8.getString(mCursor8.getColumnIndex("D8_XJ"));
                    searchBean8.d1_gj = mCursor8.getString(mCursor8.getColumnIndex("D8_GC"));
                    searchBean8.date = mCursor8.getString(mCursor8.getColumnIndex("Date"));
                    searchBean8.note = mCursor8.getString(mCursor8.getColumnIndex("Note"));
                    mDataModels.add(searchBean8);
                    break;
                case "d9":
                    mHRecyclerView.setHeaderListData(getResources().getStringArray(R.array.search_title_d9));
                    SearchBean searchBean9 = new SearchBean();
                    searchBean9.name = "自由测量";
                    searchBean9.num = mCursor9.getString(mCursor9.getColumnIndex("Num"));
                    searchBean9.d1_dg = mCursor9.getString(mCursor9.getColumnIndex("D9_SPJ"));
                    searchBean9.d1_lcz = mCursor9.getString(mCursor9.getColumnIndex("D9_CZJ"));
                    searchBean9.date = mCursor9.getString(mCursor9.getColumnIndex("Date"));
                    searchBean9.note = mCursor9.getString(mCursor9.getColumnIndex("Note"));
                    mDataModels.add(searchBean9);
                    break;
                case "dc":
                    mHRecyclerView.setHeaderListData(getResources().getStringArray(R.array.search_title_dc));
                    SearchBean searchBean10 = new SearchBean();
                    searchBean10.name = "垂直测量";
                    searchBean10.num = mCursor10.getString(mCursor10.getColumnIndex("Num"));
                    searchBean10.d1_dg = mCursor10.getString(mCursor10.getColumnIndex("DC_ZZCZD"));
                    searchBean10.date = mCursor10.getString(mCursor10.getColumnIndex("Date"));
                    searchBean10.note = mCursor10.getString(mCursor10.getColumnIndex("Note"));
                    mDataModels.add(searchBean10);
                    break;
                case "de":
                    mHRecyclerView.setHeaderListData(getResources().getStringArray(R.array.search_title_de));
                    SearchBean searchBean11 = new SearchBean();
                    searchBean11.name = "跨距测量";
                    searchBean11.num = mCursor11.getString(mCursor11.getColumnIndex("Num"));
                    searchBean11.d1_dg = mCursor11.getString(mCursor11.getColumnIndex("DE_ZZKD"));
                    searchBean11.date = mCursor11.getString(mCursor11.getColumnIndex("Date"));
                    searchBean11.note = mCursor11.getString(mCursor11.getColumnIndex("Note"));
                    mDataModels.add(searchBean11);
                    break;
                default:
                    break;
            }

            mHRecyclerView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent event) {
                    mHRecyclerView.getParent().requestDisallowInterceptTouchEvent(true);
                    int x = (int) event.getRawX();
                    int y = (int) event.getRawY();

                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            mLastX = x;
                            mLastY = y;
                            break;
                        case MotionEvent.ACTION_MOVE:
                            int deltaY = y - mLastY;
                            int deltaX = x - mLastX;
                            if (Math.abs(deltaX) < Math.abs(deltaY)) {
                                mHRecyclerView.getParent().requestDisallowInterceptTouchEvent(false);
                            } else {
                                mHRecyclerView.getParent().requestDisallowInterceptTouchEvent(true);
                            }
                        default:
                            break;
                    }
                    return false;
                }
            });
            mAdapter = new HRecyclerAdapter(getContext(), state, mDataModels, R.layout.item_search, new HRecyclerViewHolder.onItemCommonClickListener() {
                @Override
                public void onItemClickListener(int position) {

                }

                @Override
                public void onItemLongClickListener(int position) {

                }
            });
            mHRecyclerView.setAdapter(mAdapter);
        }
    }*/
    /**
     * 获取 杆号，测量类型和表的内容
     */
    /*private void getTable() {
        try {
            *//**
     * 下拉杆号 列表
     * *//*
            // 对数据库进行操作
            Cursor cursorGanhao = mDb.rawQuery("SELECT* FROM ELL_PoleData", null);
            // 创建一个List集合，List集合的元素是Map
            mListItemsGanhao = new ArrayList<>();
            mListItemGanhao = new HashMap<>();
            while (cursorGanhao.moveToNext()) {
                mListItemGanhao = new HashMap<>();
                String ganhaoId = cursorGanhao.getString(cursorGanhao.getColumnIndex("DataId"));
//                String ganhaoTime = cursorGanhao.getString(cursorGanhao.getColumnIndex("Date"));
                mListItemGanhao.put("DataId", ganhaoId);
//                mListItemGanhao.put("Date", ganhaoTime);
                mListItemsGanhao.add(mListItemGanhao);
            }
            Log.e(TAG + "-getTable", "mListItemsGanhao.size为" + mListItemsGanhao.size());
            if (mListItemsGanhao.size() == 0) {
                mListItemGanhao.put("DataId", "");
//                mListItemGanhao.put("DataId", "暂无杆号");
//                mListItemGanhao.put("Date", "");
                mSpGanhao.setEnabled(false);
            } else {
                mSpGanhao.setEnabled(true);
            }
            SimpleAdapter simpleAdapter = new SimpleAdapter(getContext(), mListItemsGanhao,
                    R.layout.dialog_spinner_item,
                    new String[]{"DataId"},
                    new int[]{R.id.text});
            mSpGanhao.setAdapter(simpleAdapter);
            mSpGanhao.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                    mSelectedGanhaoId = mListItemsGanhao.get(position).get("DataId").toString();
                    String mStartDate = mTvStarttime.getText().toString();
                    String mEndDate = mTvEndtime.getText().toString();
                    mStartDate = mStartDate.substring(0, 4) + "年" + mStartDate.substring(5, 7) + "月" + mStartDate.substring(8, 10) + "日 00:00:00";//2018年07月09日
                    mEndDate = mEndDate.substring(0, 4) + "年" + mEndDate.substring(5, 7) + "月" + mEndDate.substring(8, 10) + "日 23:59:59";//2018年07月09日
                    Log.e(TAG + "-getTable", "1-" + mStartTime + ";2-" + mEndTime);
                    Log.e(TAG + "-getTable(itemSelect)", "mSelectedGanhaoId为" + mSelectedGanhaoId + ";startDate为" + mStartDate + ";endDate为" + mEndDate);
                    mCursor1 = mDb.rawQuery("SELECT* FROM ELL_D1Data WHERE Num = ? AND Date BETWEEN ? AND ?",
                            new String[]{mSelectedGanhaoId, mStartDate, mEndDate});
                    while (mCursor1.moveToNext()) {
                        String dataId = mCursor1.getString(mCursor1.getColumnIndex("DataId"));
                        mSearchState = "d1";
                        mSearchName = "基础测量";
                        Log.e(TAG + "-getTable(1)", "dataId为" + dataId);
                        getNewData(mSearchState);
                        String a = mDataModels.get(0).d1_dg;
                        Log.e(TAG, "a1为" + a);
                    }
                    mCursor2 = mDb.rawQuery("SELECT* FROM ELL_D2Data WHERE Num = ? AND Date BETWEEN ? AND ?",
                            new String[]{mSelectedGanhaoId, mStartDate, mEndDate});
                    while (mCursor2.moveToNext()) {
                        mDataModels = new ArrayList<>();
                        String dataId = mCursor2.getString(mCursor2.getColumnIndex("DataId"));
                        mSearchState = "d2";
                        mSearchName = "线岔中心测量";
                        Log.e(TAG + "-getTable(2)", "dataId为" + dataId);
                        getNewData(mSearchState);
                        String a = mDataModels.get(0).d1_dg;
                        Log.e(TAG, "a2为" + a);
                    }
                    mCursor3 = mDb.rawQuery("SELECT* FROM ELL_D3Data WHERE Num = ? AND Date BETWEEN ? AND ?",
                            new String[]{mSelectedGanhaoId, mStartDate, mEndDate});
                    while (mCursor3.moveToNext()) {
                        mDataModels = new ArrayList<>();
                        String dataId = mCursor3.getString(mCursor3.getColumnIndex("DataId"));
                        mSearchState = "d3";
                        mSearchName = "限界测量";
                        Log.e(TAG + "-getTable(3)", "dataId为" + dataId);
                        getNewData(mSearchState);
                        String a = mDataModels.get(0).d1_dg;
                        Log.e(TAG, "a3为" + a);
                        Log.e(TAG + "-cursor3", "d1_dg为" + mCursor3.getString(mCursor3.getColumnIndex("D3_HXBG")));
                    }
                    mCursor5 = mDb.rawQuery("SELECT* FROM ELL_D5Data WHERE Num = ? AND Date BETWEEN ? AND ?",
                            new String[]{mSelectedGanhaoId, mStartDate, mEndDate});
                    while (mCursor5.moveToNext()) {
                        mDataModels = new ArrayList<>();
                        String dataId = mCursor5.getString(mCursor5.getColumnIndex("DataId"));
                        mSearchState = "d5";
                        mSearchName = "非支测量";
                        Log.e(TAG + "-getTable(5)", "dataId为" + dataId);
                        getNewData(mSearchState);
                        String a = mDataModels.get(0).d1_dg;
                        Log.e(TAG, "a5为" + a);
                    }
                    mCursor6 = mDb.rawQuery("SELECT* FROM ELL_D6Data WHERE Num = ? AND Date BETWEEN ? AND ?",
                            new String[]{mSelectedGanhaoId, mStartDate, mEndDate});
                    while (mCursor6.moveToNext()) {
                        mDataModels = new ArrayList<>();
                        String dataId = mCursor6.getString(mCursor6.getColumnIndex("DataId"));
                        mSearchState = "d6";
                        mSearchName = "定位器测量";
                        Log.e(TAG + "-getTable(6)", "dataId为" + dataId);
                        getNewData(mSearchState);
                        String a = mDataModels.get(0).d1_dg;
                        Log.e(TAG, "a6为" + a);
                    }
                    mCursor7 = mDb.rawQuery("SELECT* FROM ELL_D7Data WHERE Num = ? AND Date BETWEEN ? AND ?",
                            new String[]{mSelectedGanhaoId, mStartDate, mEndDate});
                    while (mCursor7.moveToNext()) {
                        mDataModels = new ArrayList<>();
                        String dataId = mCursor7.getString(mCursor7.getColumnIndex("DataId"));
                        mSearchState = "d7";
                        mSearchName = "承力索高差测量";
                        Log.e(TAG + "-getTable(7)", "dataId为" + dataId);
                        getNewData(mSearchState);
                        String a = mDataModels.get(0).d1_dg;
                        Log.e(TAG, "a7为" + a);
                    }
                    mCursor8 = mDb.rawQuery("SELECT* FROM ELL_D8Data WHERE Num = ? AND Date BETWEEN ? AND ?",
                            new String[]{mSelectedGanhaoId, mStartDate, mEndDate});
                    while (mCursor8.moveToNext()) {
                        mDataModels = new ArrayList<>();
                        String dataId = mCursor8.getString(mCursor8.getColumnIndex("DataId"));
                        mSearchState = "d8";
                        mSearchName = "500mm处高差测量";
                        Log.e(TAG + "-getTable(8)", "dataId为" + dataId);
                        getNewData(mSearchState);
                        String a = mDataModels.get(0).d1_dg;
                        Log.e(TAG, "a8为" + a);
                    }
                    mCursor9 = mDb.rawQuery("SELECT* FROM ELL_D9Data WHERE Num = ? AND Date BETWEEN ? AND ?",
                            new String[]{mSelectedGanhaoId, mStartDate, mEndDate});
                    while (mCursor9.moveToNext()) {
                        mDataModels = new ArrayList<>();
                        String dataId = mCursor9.getString(mCursor9.getColumnIndex("DataId"));
                        mSearchState = "d9";
                        mSearchName = "自由测量";
                        Log.e(TAG + "-getTable(9)", "dataId为" + dataId);
                        getNewData(mSearchState);
                        String a = mDataModels.get(0).d1_dg;
                        Log.e(TAG, "a9为" + a);
                    }
                    mCursor10 = mDb.rawQuery("SELECT* FROM ELL_DCData WHERE Num = ? AND Date BETWEEN ? AND ?",
                            new String[]{mSelectedGanhaoId, mStartDate, mEndDate});
                    while (mCursor10.moveToNext()) {
                        mDataModels = new ArrayList<>();
                        String dataId = mCursor10.getString(mCursor10.getColumnIndex("DataId"));
                        mSearchState = "dc";
                        mSearchName = "垂直测量";
                        Log.e(TAG + "-getTable(10)", "dataId为" + dataId);
                        getNewData(mSearchState);
                        String a = mDataModels.get(0).d1_dg;
                        Log.e(TAG, "a10为" + a);
                    }
                    mCursor11 = mDb.rawQuery("SELECT* FROM ELL_DEData WHERE Num = ? AND Date BETWEEN ? AND ?",
                            new String[]{mSelectedGanhaoId, mStartDate, mEndDate});
                    while (mCursor11.moveToNext()) {
                        mDataModels = new ArrayList<>();
                        String dataId = mCursor11.getString(mCursor11.getColumnIndex("DataId"));
                        mSearchState = "de";
                        mSearchName = "跨距测量";
                        Log.e(TAG + "-getTable(11)", "dataId为" + dataId);
                        getNewData(mSearchState);
                        String a = mDataModels.get(0).d1_dg;
                        Log.e(TAG, "a11为" + a);
                    }
                    Log.e(TAG + "-getTable", "mSearchState-0为" + mSearchState + ";mSearchName-0为" + mSearchName);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
            Log.e(TAG + "-getTable", "mSearchState-1为" + mSearchState + ";mSearchName-1为" + mSearchName);
        } catch (Exception e) {
            Log.e(TAG + "-getTable", "数据库报错" + e.toString());
        }
    }*/
}