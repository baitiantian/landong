package com.jinan.ladongjiguan.anjiantong.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jinan.ladongjiguan.anjiantong.PublicClass.AssetsDatabaseManager;
import com.jinan.ladongjiguan.anjiantong.PublicClass.CustomProgressDialog;
import com.jinan.ladongjiguan.anjiantong.R;
import com.jinan.ladongjiguan.anjiantong.view.Bean;
import com.jinan.ladongjiguan.anjiantong.adapter.SearchAdapter;
import com.jinan.ladongjiguan.anjiantong.view.SearchView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class SearchActivity extends Activity implements SearchView.SearchViewListener ,View.OnTouchListener,View.OnClickListener{

    @BindView(R.id.examine_page_back)
    LinearLayout examinePageBack;
    @BindView(R.id.title_layout)
    TextView titleLayout;
    /**
     * 搜索结果列表view
     */
    private ListView lvResults;

    /**
     * 搜索view
     */
    private SearchView searchView;


    /**
     * 热搜框列表adapter
     */
    private ArrayAdapter<CharSequence> hintAdapter;

    /**
     * 自动补全列表adapter
     */
    private ArrayAdapter<CharSequence> autoCompleteAdapter;

    /**
     * 搜索结果列表adapter
     */
    private SearchAdapter resultAdapter;

    private List<Bean> dbData;
    private List<HashMap<String,Object>> list;

    /**
     * 热搜版数据
     */
    private List<CharSequence> hintData;

    /**
     * 搜索过程中自动补全数据
     */
    private List<CharSequence> autoCompleteData;

    /**
     * 搜索结果的数据
     */
    private List<Bean> resultData;
    private List<HashMap<String,Object>> resultlist;

    /**
     * 默认提示框显示项的个数
     */
    private static int DEFAULT_HINT_SIZE = 4;

    /**
     * 提示框显示项的个数
     */
    private static int hintSize = DEFAULT_HINT_SIZE;
    private int mPage=1;
    private int mCapcity = 2;
    int mTotalNum;//数据总条数
    int mPageSize=15;//每页显示的条数
    int mTotalPage;//总的页数
    int mCurrentPage=1;//当前页码
    boolean mIsbottom=false;//判断是否到当前页码的底部
    private CustomProgressDialog progressDialog = null;//加载页
    private String TAG = SearchActivity.class.getSimpleName();
    /**
     * 设置提示框显示项的个数
     *
     * @param hintSize 提示框显示个数
     */
    public static void setHintSize(int hintSize) {
        SearchActivity.hintSize = hintSize;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.search_layout);
        ButterKnife.bind(this);
        //加载页添加
        if (progressDialog == null){
            progressDialog = CustomProgressDialog.createDialog(this);
        }
        progressDialog.show();
        initData();
        initViews();
        switch (getIntent().getStringExtra("date_state")){
            case "1":
                titleLayout.setText("法规查询");
                break;
            case "2":
                titleLayout.setText("危化特性");
                break;
            default:
                break;
        }
        /**
         * 点击事件
         * */
        examinePageBack.setOnClickListener(this);

        /**
         * 触摸事件
         * */
        examinePageBack.setOnTouchListener(this);
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
            v.setBackgroundColor(ContextCompat.getColor(this, R.color.main_color_7));
        } else if (event.getAction() == MotionEvent.ACTION_CANCEL) {
            v.setBackgroundColor(ContextCompat.getColor(this, R.color.main_color_6));
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            v.setBackgroundColor(ContextCompat.getColor(this, R.color.main_color_6));
        }
        return false;
    }

    /**
     * 初始化视图
     */
    private void initViews() {
        lvResults = (ListView) findViewById(R.id.list_main_search);
        // 初始化，只需要调用一次
        AssetsDatabaseManager.initManager(getApplication());
        // 获取管理对象，因为数据库需要通过管理对象才能够获取
        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
        // 通过管理对象获取数据库
        SQLiteDatabase db = mg.getDatabase("users.db");
        list = new ArrayList<>();
        resultData = new ArrayList<>();
        switch (getIntent().getStringExtra("date_state")){
            case "1":
                try {
                    // TODO: 2018/10/12 9：22
                    Cursor cursor = db.rawQuery("SELECT* FROM basiclawsmain", null);
//                    Cursor cursor = db.rawQuery("SELECT* FROM basiclawsmain LIMIT ?,?",new String[]{(mPage-1)*mCapcity+"",mCapcity+""});
//                    Cursor cursor = db.rawQuery("SELECT* FROM basiclawsmain LIMIT ? OFFSET ?",new String[]{mCapcity+"",(mPage-1)*mCapcity+""});
                    Log.e(TAG+"-initViews","mPage为"+mPage+";mCapcity为"+mCapcity);
                    while (cursor.moveToNext()){
                        HashMap<String,Object> map = new HashMap<>();
                        if(!(cursor.getString(cursor.getColumnIndex("SUBTITLE"))+"").equals("null")){

                            resultData.add(new Bean(cursor.getString(cursor.getColumnIndex("MAINTITLE"))+"("+
                                    cursor.getString(cursor.getColumnIndex("SUBTITLE"))+")"));
                            map.put("SYSNO",cursor.getString(cursor.getColumnIndex("SYSNO")));
                            map.put("MAINTITLE",cursor.getString(cursor.getColumnIndex("MAINTITLE")));
                        }else {
                            resultData.add(new Bean(cursor.getString(cursor.getColumnIndex("MAINTITLE"))));
                            map.put("SYSNO",cursor.getString(cursor.getColumnIndex("SYSNO")));
                            map.put("MAINTITLE",cursor.getString(cursor.getColumnIndex("MAINTITLE")));
                        }
                        list.add(map);
                        resultlist.add(map);
                    }
                    cursor.close();
                }catch (Exception e){
                    Log.e("打开法律库报错",e.toString());
                }

                break;
            case "2":
                try {
                    Cursor cursor = db.rawQuery("SELECT* FROM HUAXUEPINS", null);
                    while (cursor.moveToNext()){
                        HashMap<String,Object> map = new HashMap<>();
                        if(!(cursor.getString(cursor.getColumnIndex("zhbm"))+"").equals("null")){
                            resultData.add(new Bean(cursor.getString(cursor.getColumnIndex("zhname"))+"("+
                                    cursor.getString(cursor.getColumnIndex("zhbm"))+")"));
                            map.put("zhname",cursor.getString(cursor.getColumnIndex("zhname")));
                        }else {
                            resultData.add(new Bean(cursor.getString(cursor.getColumnIndex("zhname"))));
                            map.put("zhname",cursor.getString(cursor.getColumnIndex("zhname")));
                        }
                        resultlist.add(map);
                        list.add(map);

                    }
                    cursor.close();
                }catch (Exception e){
                    Log.e("打开危化库报错",e.toString());
                }
                break;
            default:
                break;
        }

        searchView = (SearchView) findViewById(R.id.main_search_layout);
        //设置监听
        searchView.setSearchViewListener(this);
        //设置adapter
        searchView.setTipsHintAdapter(hintAdapter);
        searchView.setAutoCompleteAdapter(autoCompleteAdapter);
        resultAdapter = new SearchAdapter(this, resultData, R.layout.search_fragment_item_bean_list);
        lvResults.setAdapter(resultAdapter);
        lvResults.setVisibility(View.VISIBLE);
        lvResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent intent = new Intent();
                switch (getIntent().getStringExtra("date_state")){
                    case "1":
                        if(resultlist.get(position).get("MAINTITLE").toString().equals("煤矿安全规程(取消)")){
                            intent.setClass(SearchActivity.this, HelpActivity.class);
                            intent.putExtra("state","1");
                            startActivity(intent);
                            overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                        }else {
                            intent.setClass(SearchActivity.this,LawDateActivity.class);
                            intent.putExtra("SYSNO",resultlist.get(position).get("SYSNO").toString());
                            intent.putExtra("MAINTITLE",resultlist.get(position).get("MAINTITLE").toString());
                            startActivity(intent);
                            overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                        }

                        break;
                    case "2":
                        intent.setClass(SearchActivity.this,HazardousChemicalsActivity.class);
                        intent.putExtra("zhname",resultlist.get(position).get("zhname").toString());
                        startActivity(intent);
                        overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                        break;
                    default:
                        break;

                }
            }
        });
    }

    /**
     * 初始化数据
     */
    private void initData() {
        //从数据库获取数据
        getDbData();
        //初始化热搜版数据
        getHintData();
        //初始化自动补全数据
        getAutoCompleteData(null);
        //初始化搜索结果数据
        getResultData(null);
    }

    /**
     * 获取db 数据
     */
    private void getDbData() {
        // 初始化，只需要调用一次
        AssetsDatabaseManager.initManager(getApplication());
        // 获取管理对象，因为数据库需要通过管理对象才能够获取
        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
        // 通过管理对象获取数据库
        SQLiteDatabase db = mg.getDatabase("users.db");
        int size = 100;
        dbData = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            dbData.add(new Bean((i + 1) +"条"));
        }
        switch (getIntent().getStringExtra("date_state")){
            case "1":
                list = new ArrayList<>();

                try {
                    dbData = new ArrayList<>();
                    // TODO: 2018/10/12 9:14
                    Cursor cursor = db.rawQuery("SELECT* FROM basiclawsmain", null);
//                    Cursor cursor = db.rawQuery("SELECT* FROM basiclawsmain LIMIT ?,?", new String[]{(mPage-1)*mCapcity+"",mCapcity+""});
//                    Cursor cursor = db.rawQuery("SELECT* FROM basiclawsmain LIMIT ? OFFSET ?", new String[]{mCapcity+"",(mPage-1)*mCapcity+""});
                   Log.e(TAG+"-getDbData","mPage为"+mPage+";mCapcity为"+mCapcity);
                    while (cursor.moveToNext()){
                        HashMap<String,Object> map = new HashMap<>();
                        if(!(cursor.getString(cursor.getColumnIndex("SUBTITLE"))+"").equals("null")){

                            dbData.add(new Bean(cursor.getString(cursor.getColumnIndex("MAINTITLE"))+"("+
                                    cursor.getString(cursor.getColumnIndex("SUBTITLE"))+")"));
                            map.put("SYSNO",cursor.getString(cursor.getColumnIndex("SYSNO")));
                            map.put("MAINTITLE",cursor.getString(cursor.getColumnIndex("MAINTITLE")));
                        }else {
                            dbData.add(new Bean(cursor.getString(cursor.getColumnIndex("MAINTITLE"))));
                            map.put("SYSNO",cursor.getString(cursor.getColumnIndex("SYSNO")));
                            map.put("MAINTITLE",cursor.getString(cursor.getColumnIndex("MAINTITLE")));
                        }
                        list.add(map);
                    }
                    cursor.close();
                    //关闭加载页
                    if (progressDialog != null){
                        progressDialog.dismiss();
                        progressDialog = null;
                    }
                }catch (Exception e){
                    Log.e("数据库报错", e.toString());
                }
                break;
            case "2":
                list = new ArrayList<>();

                try {
                    dbData = new ArrayList<>();

                    Cursor cursor = db.rawQuery("SELECT* FROM HUAXUEPINS", null);
                    while (cursor.moveToNext()){
                        HashMap<String,Object> map = new HashMap<>();
                        if(!(cursor.getString(cursor.getColumnIndex("zhbm"))+"").equals("null")){
                            dbData.add(new Bean(cursor.getString(cursor.getColumnIndex("zhname"))+"("+
                                    cursor.getString(cursor.getColumnIndex("zhbm"))+")"));
                            map.put("zhname",cursor.getString(cursor.getColumnIndex("zhname")));
                        }else {
                            dbData.add(new Bean(cursor.getString(cursor.getColumnIndex("zhname"))));
                            map.put("zhname",cursor.getString(cursor.getColumnIndex("zhname")));
                        }

                        list.add(map);
                    }
                    cursor.close();
                    //关闭加载页
                    if (progressDialog != null){
                        progressDialog.dismiss();
                        progressDialog = null;
                    }
                }catch (Exception e){
                    Log.e("数据库报错", e.toString());
                }
                break;
            default:
                break;

        }

    }

    /**
     * 获取热搜版data 和adapter
     */
    private void getHintData() {
        // 初始化，只需要调用一次
        AssetsDatabaseManager.initManager(getApplication());
        // 获取管理对象，因为数据库需要通过管理对象才能够获取
        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
        // 通过管理对象获取数据库
        SQLiteDatabase db = mg.getDatabase("users.db");
        hintData = new ArrayList<>(hintSize);
        for (int i = 1; i <= hintSize; i++) {
            hintData.add( i  +"条" );
        }
        switch (getIntent().getStringExtra("date_state")){
            case "1":
                try {
                    Cursor cursor = db.rawQuery("SELECT* FROM basiclawsmain", null);
                    hintData = new ArrayList<>(hintSize);
                    for (int i = 1; i <= hintSize; i++) {
                        cursor.moveToNext();
                        if(!(cursor.getString(cursor.getColumnIndex("SUBTITLE"))+"").equals("null")){
                            hintData.add(cursor.getString(cursor.getColumnIndex("MAINTITLE"))+"("+
                                    cursor.getString(cursor.getColumnIndex("SUBTITLE"))+")");
                        }else {
                            hintData.add(cursor.getString(cursor.getColumnIndex("MAINTITLE")));
                        }

                    }
                    cursor.close();
                }catch (Exception e){
                    Log.e("数据库报错", e.toString());
                }
                break;
            case "2":
                try {
                    Cursor cursor = db.rawQuery("SELECT* FROM HUAXUEPINS", null);
                    hintData = new ArrayList<>(hintSize);
                    for (int i = 1; i <= hintSize; i++) {
                        cursor.moveToNext();
                        if(!(cursor.getString(cursor.getColumnIndex("zhbm"))+"").equals("null")){
                            hintData.add(cursor.getString(cursor.getColumnIndex("zhname"))+"("+
                                    cursor.getString(cursor.getColumnIndex("zhbm"))+")");
                        }else {
                            hintData.add(cursor.getString(cursor.getColumnIndex("zhname")));
                        }

                    }
                    cursor.close();
                }catch (Exception e){
                    Log.e("数据库报错", e.toString());
                }
                break;
            default:
                break;

        }
        hintAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, hintData);
    }

    /**
     * 获取自动补全data 和adapter
     */
    private void getAutoCompleteData(String text) {
        if (autoCompleteData == null) {
            //初始化
            autoCompleteData = new ArrayList<>(hintSize);
        } else {
            // 根据text 获取auto data
            autoCompleteData.clear();
            for (int i = 0, count = 0; i < dbData.size()
                    && count < hintSize; i++) {
                if (dbData.get(i).getTitle().contains(text.trim())) {
                    autoCompleteData.add(dbData.get(i).getTitle());
                    count++;
                }
            }
        }
        if (autoCompleteAdapter == null) {
            autoCompleteAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, autoCompleteData);
        } else {
            autoCompleteAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 获取搜索结果data和adapter
     */
    private void getResultData(String text) {
        if (resultData == null) {
            // 初始化
            resultData = new ArrayList<>();
            resultlist = new ArrayList<>();
        } else {
            resultData.clear();
            resultlist.clear();
            for (int i = 0; i < dbData.size(); i++) {
                if (dbData.get(i).getTitle().contains(text.trim())) {
                    resultData.add(dbData.get(i));
                    resultlist.add(list.get(i));
                }
            }
        }
        if (resultAdapter == null) {
            resultAdapter = new SearchAdapter(this, resultData, R.layout.search_fragment_item_bean_list);
        } else {
            resultAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 当搜索框 文本改变时 触发的回调 ,更新自动补全数据
     *
     * @param text
     */
    @Override
    public void onRefreshAutoComplete(String text) {
        //更新数据
        getAutoCompleteData(text);
    }

    /**
     * 点击搜索键时edit text触发的回调
     *
     * @param text
     */
    @Override
    public void onSearch(String text) {
        //更新result数据
        if(text.length()>0){
            getResultData(text);
            Toast.makeText(this, "完成搜索", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this, "请输入需要搜索的内容", Toast.LENGTH_SHORT).show();
        }

        lvResults.setVisibility(View.VISIBLE);
        //第一次获取结果 还未配置适配器
        if (lvResults.getAdapter() == null) {
            //获取搜索数据 设置适配器
            lvResults.setAdapter(resultAdapter);
        } else {
            //更新搜索数据
            resultAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
    }
}
