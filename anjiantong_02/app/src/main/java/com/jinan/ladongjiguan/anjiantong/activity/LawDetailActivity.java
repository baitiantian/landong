package com.jinan.ladongjiguan.anjiantong.activity;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.http.SslError;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.jinan.ladongjiguan.anjiantong.PublicClass.AssetsDatabaseManager;
import com.jinan.ladongjiguan.anjiantong.PublicClass.AsyncTextLoadTask;
import com.jinan.ladongjiguan.anjiantong.PublicClass.CustomProgressDialog;
import com.jinan.ladongjiguan.anjiantong.PublicClass.MyScrollView;
import com.jinan.ladongjiguan.anjiantong.PublicClass.OnScrollChangedListener;
import com.jinan.ladongjiguan.anjiantong.R;
import com.jinan.ladongjiguan.anjiantong.utils.HtmlUtils;
import com.jinan.ladongjiguan.anjiantong.view.Bean;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LawDetailActivity extends BaseActivity implements View.OnTouchListener, View.OnClickListener {
    @BindView(R.id.examine_page_back)
    LinearLayout mBack;
    @BindView(R.id.title_layout)
    TextView mTvTitle;
    @BindView(R.id.law_detail_scrollview)
    public ScrollView mScrollView;
    //    public MyScrollView mScrollView;
    @BindView(R.id.law_detail_textview)
    public TextView mTvContent;

    private List<Map<String, CharSequence>> mMaps;
    private String mTitle, mKeyword,mDataId;
    private CharSequence mContent;
    private SpannableString mSpannableString;
    private HtmlUtils mHtmlTools;
    private String TAG = LawDetailActivity.class.getSimpleName();
    public boolean isLoading;
    private BufferedReader mBr;
    private SQLiteDatabase mDb;
    private CustomProgressDialog mProgressDialog = null;//加载页

    @Override
    protected void initView() {
        setContentView(R.layout.activity_law_detail);
        ButterKnife.bind(this);
        //加载页添加
        if (mProgressDialog == null) {
            mProgressDialog = CustomProgressDialog.createDialog(this);
        }
        mProgressDialog.show();
        mDataId = getIntent().getStringExtra("LawId");
        mKeyword = getIntent().getStringExtra("Keyword");
        mTitle = getIntent().getStringExtra("LawName");
        mProgressDialog.show();
        mSpannableString = new SpannableString("");
        // 初始化，只需要调用一次
        AssetsDatabaseManager.initManager(getApplication());
        // 获取管理对象，因为数据库需要通过管理对象才能够获取
        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
        // 通过管理对象获取数据库
        mDb = mg.getDatabase("users.db");
        try{
            Cursor c = mDb.rawQuery("SELECT* FROM ELL_Law  WHERE LawId = ?", new String[]{mDataId});
            c.moveToFirst();
            mContent = Html.fromHtml(c.getString(c.getColumnIndex("LawContent")), null, null);
        }catch (Exception e){
            Log.e("打开法律数据报错"+mTitle,e.toString(),e);
        }
//        mContent = getIntent().getCharSequenceExtra("LawContent");
        mTvTitle.setText(mTitle);
    }

    @Override
    protected void init() {
        mBack.setOnTouchListener(this);
        mBack.setOnClickListener(this);
        lawDate();
    }

    protected void lawDate() {
        Log.e(TAG + "-lawDate", "mKeyword为" + mKeyword);
        if(mKeyword.length()>1){
            mSpannableString = HtmlUtils.getHighLightKeyWord(Color.RED, mContent, mKeyword);
        }
//
        mTvContent.setText(mSpannableString);
        mTvContent.setText(mContent);
        //关闭加载页
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
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
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mBr) {
            try {
                mBr.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            v.setBackgroundColor(ContextCompat.getColor(this, R.color.main_color_3));
        } else if (event.getAction() == MotionEvent.ACTION_CANCEL) {
            v.setBackgroundColor(ContextCompat.getColor(this, R.color.main_color_2));
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            v.setBackgroundColor(ContextCompat.getColor(this, R.color.main_color_2));
        }
        return false;
    }
}
