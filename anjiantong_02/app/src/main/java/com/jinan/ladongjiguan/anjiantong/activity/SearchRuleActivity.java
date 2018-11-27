package com.jinan.ladongjiguan.anjiantong.activity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.opengl.Visibility;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jinan.ladongjiguan.anjiantong.PublicClass.AssetsDatabaseManager;
import com.jinan.ladongjiguan.anjiantong.PublicClass.CustomProgressDialog;
import com.jinan.ladongjiguan.anjiantong.R;
import com.jinan.ladongjiguan.anjiantong.adapter.SearchAdapter;
import com.jinan.ladongjiguan.anjiantong.utils.HtmlUtils;
import com.jinan.ladongjiguan.anjiantong.view.Bean;
import com.jinan.ladongjiguan.anjiantong.view.SearchView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 法规查询  查询标题
 */
public class SearchRuleActivity extends BaseActivity implements View.OnClickListener, View.OnTouchListener, SearchView.SearchViewListener {
    @BindView(R.id.examine_page_back)
    LinearLayout examinePageBack;
    @BindView(R.id.title_layout)
    TextView titleLayout;
    //搜索view
    @BindView(R.id.search_rule_search)
    SearchView mSearchView;
    //搜索结果列表view
    @BindView(R.id.search_rule_listview)
    ListView mListView;
    // TODO: 2018/10/12
    @BindView(R.id.search_rule_rl)
    RelativeLayout mBottomRl;
    /********************************************************/
    private TextView mAutoCompleteText, mResultBeanText, mResultBeanTextC, mHintText, mResultMapText/*,mTestText*/;
    //热搜框列表adapter
    private ArrayAdapter<CharSequence> mHintAdapter;

    //自动补全列表adapter
    // TODO: 2018/8/7
//    private ArrayAdapter<String> mAutoCompleteAdapter;
    private ArrayAdapter<CharSequence> mAutoCompleteAdapter;

    //搜索结果列表adapter
    private SearchAdapter mSearchAdapter;

    private List<Bean> mBeanDatas;
    private List<HashMap<String, CharSequence>> mMapDatas;

    //热搜版数据
    private List<CharSequence> mHintDatas;

    //搜索过程中自动补全数据
    private List<CharSequence> mCompleteDatas;

    //搜索结果的数据
    private List<Bean> mResultBeanDatas;
    private List<HashMap<String, CharSequence>> mResultMapDatas;
    private String mType;
    //默认提示框显示项的个数
    private static int DEFAULT_HINT_SIZE = 4;
    //提示框显示项的个数
    private static int hintSize = DEFAULT_HINT_SIZE;
    private CustomProgressDialog mProgressDialog = null;//加载页
    private HtmlUtils.NetworkImageGetter mImageGetter;
    private Spanned mSpannedTitle, mSpannedContent;
    private SpannableString mSpannableString, mSpannableStringC;
    private HtmlUtils mHtmlTools, mHtmlToolsTest, mHtmlToolsC;
    private String TAG = SearchRuleActivity.class.getSimpleName();
    private String mText;
    // TODO: 2018/10/12 16:58
    private int mCurrentPage = 1;//当前页码
    private int mPageSize = 10;//每页显示的条数
    private int mTotalNum;//数据总条数
    private boolean mIsPageBottom = false;//判断是否到当前页码的底部
    private boolean mIsBottom = false;//判断是否到最底部
    private boolean mFlag = false;//判断是否已经吐司
    private SQLiteDatabase mDb;
    private int mStartNum;

    /********************************************************/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_rule);
        ButterKnife.bind(this);
        mType = getIntent().getStringExtra("date_state") == null ? "" : getIntent().getStringExtra("date_state");
        //加载页添加
        if (mProgressDialog == null) {
            mProgressDialog = CustomProgressDialog.createDialog(this);
        }
        mProgressDialog.show();
        // 初始化，只需要调用一次
        AssetsDatabaseManager.initManager(getApplication());
        // 获取管理对象，因为数据库需要通过管理对象才能够获取
        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
        // 通过管理对象获取数据库
        mDb = mg.getDatabase("users.db");
        Cursor c = mDb.rawQuery("SELECT* FROM ELL_Law", null);
        mTotalNum = c.getCount();
        initData();
        setData();

        mListView.setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (mIsPageBottom && (scrollState == OnScrollListener.SCROLL_STATE_IDLE)) {
                    mBottomRl.setVisibility(View.VISIBLE);

                } else {
                    mBottomRl.setVisibility(View.GONE);
                }

                if (mIsBottom && mIsPageBottom) {
                    mFlag = true;
                    Toast.makeText(SearchRuleActivity.this, "加载完毕", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                Log.e(TAG + "-onScroll", "firstVisibleItem为" + firstVisibleItem+"；visibleItemCount为" + visibleItemCount);
                Log.e(TAG + "-onScroll", "mTotalNum为" + mTotalNum+ ";totalItemCount为" + totalItemCount);
                mIsPageBottom = ((firstVisibleItem + visibleItemCount) == totalItemCount);
                if (totalItemCount == mTotalNum) {
                    mBottomRl.setVisibility(View.GONE);
                    mIsBottom = true;
                }
            }
        });
        switch (mType) {
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
         * 触摸事件
         * */
        examinePageBack.setOnTouchListener(this);
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
        mBeanDatas = new ArrayList<>();
        switch (mType) {
            case "1"://法规查询
                mMapDatas = new ArrayList<>();
                try {
                    mBeanDatas = new ArrayList<>();
                    Cursor cursor = mDb.rawQuery("SELECT* FROM ELL_Law LIMIT ?,?", new String[]{0 + "", 10 + ""});
                    while (cursor.moveToNext()) {
                        HashMap<String, CharSequence> map = new HashMap<>();
                        mResultBeanText = (TextView) findViewById(R.id.item_search_tv_title);
                        String title,content;
                        if (!(cursor.getString(cursor.getColumnIndex("LawTitle")) + "").equals("null")) {//副标题
                             title = cursor.getString(cursor.getColumnIndex("LawName")) + "(" +
                                    cursor.getString(cursor.getColumnIndex("LawTitle")) + ")";
                             content = cursor.getString(cursor.getColumnIndex("LawContent"));
                        } else {
                             title = cursor.getString(cursor.getColumnIndex("LawName"));
                             content = cursor.getString(cursor.getColumnIndex("LawContent"));
                        }
                        Log.e(TAG + "-getDbData1-2", "title为" + title);
                        map.put("LawId", cursor.getString(cursor.getColumnIndex("LawId")));
                        map.put("LawName", cursor.getString(cursor.getColumnIndex("LawName")));
                        map.put("LawContent", content);
                        mBeanDatas.add(new Bean(title));
                        mMapDatas.add(map);
                    }
                    cursor.close();
                    //关闭加载页
                    if (mProgressDialog != null) {
                        mProgressDialog.dismiss();
                        mProgressDialog = null;
                    }
                } catch (Exception e) {
                    Log.e("数据库报错", e.toString());
                }
                break;
            case "2"://危化特性
                mMapDatas = new ArrayList<>();
                try {
                    mBeanDatas = new ArrayList<>();
                    Cursor cursor = mDb.rawQuery("SELECT* FROM ELL_Law", null);
                    while (cursor.moveToNext()) {
                        mResultBeanText = (TextView) findViewById(R.id.item_search_tv_title);
                        HashMap<String, CharSequence> map = new HashMap<>();
                        String title,content;
                        if (!(cursor.getString(cursor.getColumnIndex("LawTitle")) + "").equals("null")) {
                             title = cursor.getString(cursor.getColumnIndex("LawName")) + "(" +
                                    cursor.getString(cursor.getColumnIndex("LawTitle")) + ")";
//                             content = cursor.getString(cursor.getColumnIndex("LawContent"));
                        } else {
                             title = cursor.getString(cursor.getColumnIndex("LawName"));
//                             content = cursor.getString(cursor.getColumnIndex("LawContent"));
                        }
                        Log.e(TAG + "-getDbData2-2", "title为" + title);
                        map.put("LawId", cursor.getString(cursor.getColumnIndex("LawId")));
                        map.put("LawName", cursor.getString(cursor.getColumnIndex("LawName")));
//                        map.put("LawContent", content);
                        mBeanDatas.add(new Bean(title));
                        mMapDatas.add(map);
                    }
                    cursor.close();
                    //关闭加载页
                    if (mProgressDialog != null) {
                        mProgressDialog.dismiss();
                        mProgressDialog = null;
                    }
                } catch (Exception e) {
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
        mHintDatas = new ArrayList<>();
        switch (mType) {
            case "1":
                try {
                    Cursor cursor = mDb.rawQuery("SELECT* FROM ELL_Law LIMIT ?,?", new String[]{0 + "", 10 + ""});
                    mHintDatas = new ArrayList<>();
                    for (int i = 1; i <= hintSize; i++) {
                        cursor.moveToNext();
                        mHintText = (TextView) findViewById(android.R.id.text1);
                        if (!(cursor.getString(cursor.getColumnIndex("LawTitle")) + "").equals("null")) {
                            mHintDatas.add(cursor.getString(cursor.getColumnIndex("LawName")) + "(" +
                                    cursor.getString(cursor.getColumnIndex("LawTitle")) + ")");
                        } else {
                            mHintDatas.add(cursor.getString(cursor.getColumnIndex("LawName")));
                        }

                    }
                    cursor.close();
                } catch (Exception e) {
                    Log.e("数据库报错", e.toString());
                }
                break;
            case "2":
                try {
                    Cursor cursor = mDb.rawQuery("SELECT* FROM ELL_Law", null);
                    mHintDatas = new ArrayList<>();
                    for (int i = 1; i <= hintSize; i++) {
                        cursor.moveToNext();
                        if (!(cursor.getString(cursor.getColumnIndex("LawTitle")) + "").equals("null")) {
                            mHintDatas.add(cursor.getString(cursor.getColumnIndex("LawName")) + "(" +
                                    cursor.getString(cursor.getColumnIndex("LawTitle")) + ")");
                        } else {
                            mHintDatas.add(cursor.getString(cursor.getColumnIndex("LawName")));
                        }

                    }
                    cursor.close();
                } catch (Exception e) {
                    Log.e("数据库报错", e.toString());
                }
                break;
            default:
                break;

        }
        mHintAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mHintDatas);
    }

    /**
     * 获取自动补全data 和adapter
     */
    private void getAutoCompleteData(String text) {
        if (mCompleteDatas == null) {
            //初始化
            mCompleteDatas = new ArrayList<>(hintSize);
        } else {
            // 根据text 获取auto data
            mCompleteDatas.clear();
            for (int i = 0, count = 0; i < mBeanDatas.size() && count < hintSize; i++) {
                mAutoCompleteText = (TextView) findViewById(android.R.id.text1);
                if (mBeanDatas.get(i).getTitle().contains(text.trim())) {
                    String completeData = mBeanDatas.get(i).getTitle();
//                    mHtmlTools = new HtmlUtils(SearchRuleActivity.this, mAutoCompleteText, text.trim());
                    mSpannedTitle = Html.fromHtml(completeData, null, null);
                    mSpannableString = mHtmlTools.getHighLightKeyWord(Color.RED, mSpannedTitle, text.trim());
                    mCompleteDatas.add(mSpannableString);
                    count++;
                }
            }
        }
        if (mAutoCompleteAdapter == null) {
            mAutoCompleteAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mCompleteDatas);
        } else {
            mAutoCompleteAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 获取搜索结果data和adapter
     */
    private void getResultData(String text) {
        if (mResultBeanDatas == null) {
            // 初始化
            mResultBeanDatas = new ArrayList<>();
            mResultMapDatas = new ArrayList<>();
        } else {
            mResultBeanDatas.clear();
            mResultMapDatas.clear();
            for (int i = 0; i < mBeanDatas.size(); i++) {
                if (mBeanDatas.get(i).getTitle().contains(text.trim())) {
                    mResultBeanDatas.add(mBeanDatas.get(i));
                    mResultMapDatas.add(mMapDatas.get(i));
                }
            }
        }
        if (mSearchAdapter == null) {
            mSearchAdapter = new SearchAdapter(this, mResultBeanDatas, R.layout.search_fragment_item_bean_list);
        } else {
            mSearchAdapter.notifyDataSetChanged();
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
        mText = text;
        Log.e(TAG + "-onRefresh", "------" + mText);
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
        mText = text;
        Log.e(TAG + "-onSearch", "------" + mText);
        if (text.length() > 0) {
            getResultData(text);
            Toast.makeText(this, "完成搜索", Toast.LENGTH_SHORT).show();
            mListView.setVisibility(View.VISIBLE);
        } else {
            Toast.makeText(this, "请输入需要搜索的内容", Toast.LENGTH_SHORT).show();
            mListView.setVisibility(View.GONE);
        }

//        mListView.setVisibility(View.VISIBLE);
        //第一次获取结果 还未配置适配器
        if (mListView.getAdapter() == null) {
            //获取搜索数据 设置适配器
            mListView.setAdapter(mSearchAdapter);
        } else {
            //更新搜索数据
            mSearchAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
    }

    /**
     * 初始化视图
     */
    private void setData() {
        Log.e(TAG + "-setData", "mText-1为" + mText);
//        mMapDatas = new ArrayList<>();
//        mResultBeanDatas = new ArrayList<>();
        mText = mSearchView.getEtInput().toString();
        switch (mType) {
            case "1":
                try {
                    mStartNum = (mCurrentPage - 1) * mPageSize;
                    Log.e(TAG + "-setData", "a为" + mStartNum + ";mPageSize为" + mPageSize);
                    Cursor cursor = mDb.rawQuery("SELECT* FROM ELL_Law LIMIT ?,?", new String[]{mStartNum + "", mPageSize + ""});//每次10条，从0,10,20,30开始。。。
//                    Cursor cursor = mDb.rawQuery("SELECT* FROM ELL_Law", null);//每次10条，从0,10,20,30开始。。。
                    while (cursor.moveToNext()) {
                        HashMap<String, CharSequence> map = new HashMap<>();
                        mResultBeanText = (TextView) findViewById(R.id.item_search_tv_title);
                        String title, content;
                        if (!(cursor.getString(cursor.getColumnIndex("LawTitle")) + "").equals("null")) {
                            title = cursor.getString(cursor.getColumnIndex("LawName")) + "(" +
                                    cursor.getString(cursor.getColumnIndex("LawTitle")) + ")";
//                            content = cursor.getString(cursor.getColumnIndex("LawContent"));
                        } else {
                            title = cursor.getString(cursor.getColumnIndex("LawName"));
//                            content = cursor.getString(cursor.getColumnIndex("LawContent"));
                        }
                        Log.e(TAG + "-setData1-1", "title为" + title);
                        map.put("LawId", cursor.getString(cursor.getColumnIndex("LawId")));
                        map.put("LawName", cursor.getString(cursor.getColumnIndex("LawName")));
//                        map.put("LawContent", content);
                        mResultBeanDatas.add(new Bean(title));
                        mMapDatas.add(map);
                        mResultMapDatas.add(map);
                    }
                    cursor.close();
                } catch (Exception e) {
                    Log.e("打开法律库报错", e.toString());
                }

                break;
            case "2":
                try {
                    Cursor cursor = mDb.rawQuery("SELECT* FROM ELL_Law", null);
                    while (cursor.moveToNext()) {
                        mResultBeanText = (TextView) findViewById(R.id.item_search_tv_title);
                        HashMap<String, CharSequence> map = new HashMap<>();
                        String title, content;
                        if (!(cursor.getString(cursor.getColumnIndex("LawTitle")) + "").equals("null")) {
                            title = cursor.getString(cursor.getColumnIndex("LawName")) + "(" +
                                    cursor.getString(cursor.getColumnIndex("LawTitle")) + ")";
                            content = cursor.getString(cursor.getColumnIndex("LawContent"));
                        } else {
                            title = cursor.getString(cursor.getColumnIndex("LawName"));
                            content = cursor.getString(cursor.getColumnIndex("LawContent"));
                        }
                        Log.e(TAG + "-setData2-1", "title为" + title);
                        map.put("LawId", cursor.getString(cursor.getColumnIndex("LawId")));
                        map.put("LawName", cursor.getString(cursor.getColumnIndex("LawName")));
                        map.put("LawContent", content);
                        mResultBeanDatas.add(new Bean(title));
                        mResultMapDatas.add(map);
                        mMapDatas.add(map);

                    }
                    cursor.close();
                } catch (Exception e) {
                    Log.e("打开危化库报错", e.toString());
                }
                break;
            default:
                break;
        }
        //设置监听
        mSearchView.setSearchViewListener(this);
        Log.e(TAG + "-setData", "mText-2为" + mText);
        //设置adapter
        mSearchView.setTipsHintAdapter(mHintAdapter);
        mSearchView.setAutoCompleteAdapter(mAutoCompleteAdapter);
        mSearchAdapter = new SearchAdapter(this, mResultBeanDatas, R.layout.search_fragment_item_bean_list);

        mListView.setAdapter(mSearchAdapter);
        mListView.setVisibility(View.VISIBLE);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent intent = new Intent();
                switch (mType) {
                    case "1":
                        if (mResultMapDatas.get(position).get("LawName").toString().equals("煤矿安全规程(取消)")) {
                            intent.setClass(SearchRuleActivity.this, HelpActivity.class);
                            intent.putExtra("state", "1");
                            startActivity(intent);
                            overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                        } else {
                            CharSequence content = mResultMapDatas.get(position).get("LawContent");
//                            mHtmlTools = new HtmlUtils(SearchRuleActivity.this, mResultBeanText, mText);
//                            mSpannedContent = Html.fromHtml(content.toString(), null, null);
                            Log.e(TAG + "-setData", "mSpannedContent为" + mSpannedContent);
                            intent.setClass(SearchRuleActivity.this, LawDetailActivity.class);
                            intent.putExtra("Keyword", mText);
                            intent.putExtra("LawId", mResultMapDatas.get(position).get("LawId"));
//                            intent.putExtra("LawContent", mSpannedContent);
                            intent.putExtra("LawName", mResultMapDatas.get(position).get("LawName"));
                            startActivity(intent);
                            overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                        }

                        break;
                    case "2":
                        intent.setClass(SearchRuleActivity.this, HazardousChemicalsActivity.class);
                        intent.putExtra("LawName", mResultMapDatas.get(position).get("LawName").toString());
                        startActivity(intent);
                        overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                        break;
                    default:
                        break;

                }
            }
        });
    }

    @OnClick({R.id.examine_page_back, R.id.search_rule_rl})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.examine_page_back://返回键
                onBackPressed();
                break;
            case R.id.search_rule_rl:
                mCurrentPage++;
                setData();
                mListView.setSelection(mStartNum);
                mSearchAdapter.notifyDataSetChanged();
                mBottomRl.setVisibility(View.GONE);
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
    protected void initView() {

    }

    @Override
    protected void init() {

    }
}
