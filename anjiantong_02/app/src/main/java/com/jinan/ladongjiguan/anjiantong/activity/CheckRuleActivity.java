package com.jinan.ladongjiguan.anjiantong.activity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jinan.ladongjiguan.anjiantong.PublicClass.AssetsDatabaseManager;
import com.jinan.ladongjiguan.anjiantong.PublicClass.CustomProgressDialog;
import com.jinan.ladongjiguan.anjiantong.R;
import com.jinan.ladongjiguan.anjiantong.adapter.CheckRuleAdapter;
import com.jinan.ladongjiguan.anjiantong.utils.HtmlUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 法规反查  查询内容
 */
public class CheckRuleActivity extends BaseActivity implements View.OnTouchListener, View.OnClickListener {
    @BindView(R.id.examine_page_back)
    LinearLayout mBack;
    @BindView(R.id.title_layout)
    TextView mTvTitle;
    @BindView(R.id.check_rule_et_name)
    EditText mEtName;
    @BindView(R.id.check_rule_btn_check)
    Button mBtnCheck;
    @BindView(R.id.check_rule_recyclerview)
    RecyclerView mRecyclerView;

    private TextView mItemText;

    private List<Map<String, CharSequence>> mMapDatas = new ArrayList<>();
    private HtmlUtils mHtmlTools;
    private String TAG = CheckRuleActivity.class.getSimpleName();
    private HtmlUtils.NetworkImageGetter mImageGetter;
    private CheckRuleAdapter mAdapter;
    private Spanned mSpanned;
    private SpannableString mSpannableString;
    private CustomProgressDialog mProgressDialog = null;//加载页

    @Override
    protected void initView() {
        setContentView(R.layout.activity_check_rule);
        ButterKnife.bind(this);
        mTvTitle.setText("法规反查");
    }

    @Override
    protected void init() {
        LinearLayoutManager manager = new LinearLayoutManager(CheckRuleActivity.this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(manager);
        mBack.setOnTouchListener(this);
        mBack.setOnClickListener(this);
        mBtnCheck.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.examine_page_back://返回键
                onBackPressed();
                break;
            case R.id.check_rule_btn_check:
                //加载页添加
                if (mProgressDialog==null){
                    Log.e(TAG+"-1","-----");
                    mProgressDialog = CustomProgressDialog.createDialog(CheckRuleActivity.this);
                }
                mProgressDialog.show();
                if (mEtName.getText().toString().length() > 0) {
                    lawData();
                } else {
                    Toast.makeText(this, "请输入需要搜索的内容", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    protected void lawData() {
        // 初始化，只需要调用一次
        AssetsDatabaseManager.initManager(getApplication());
        // 获取管理对象，因为数据库需要通过管理对象才能够获取
        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
        // 通过管理对象获取数据库
        SQLiteDatabase db = mg.getDatabase("users.db");
        try {
            mMapDatas = new ArrayList<>();
            Cursor cursor = db.rawQuery("SELECT* FROM ELL_Law WHERE LawContent like ? ",
                    new String[]{"%" + mEtName.getText().toString() + "%"});
            while (cursor.moveToNext()) {
                Map<String, CharSequence> map = new HashMap<>();
                String s = cursor.getString(cursor.getColumnIndex("LawContent"));
                String title = cursor.getString(cursor.getColumnIndex("LawName"));
                Log.e(TAG+"-lawDate","title为"+title);
                mItemText = (TextView) findViewById(R.id.item_checkrule_name);
//                mHtmlTools = new HtmlUtils(CheckRuleActivity.this, mItemText, mEtName.getText().toString());
//                mImageGetter = new HtmlUtils.NetworkImageGetter();
                mSpanned = Html.fromHtml(s, null, null);
                mSpannableString = mHtmlTools.getHighLightKeyWord(Color.RED, mSpanned, mEtName.getText().toString());

                map.put("lawcontent", mSpannableString);
                map.put("lawid", cursor.getString(cursor.getColumnIndex("LawId")));
                map.put("lawname", cursor.getString(cursor.getColumnIndex("LawName")));
                if (mSpanned.toString().length() > 8) {
                    mMapDatas.add(map);
                }
            }
            cursor.close();
            Log.e(TAG + "-lawData", "mMapDatas.size为" + mMapDatas.size());
            //关闭加载页
            if (cursor.isClosed()&&mProgressDialog != null) {
                mProgressDialog.dismiss();
                mProgressDialog = null;
            }
            mAdapter = new CheckRuleAdapter(CheckRuleActivity.this, mMapDatas, R.layout.item_check_rule);
            if(mAdapter.getItemCount()==0){
                if(mProgressDialog!=null){
                    mProgressDialog.dismiss();
                    mProgressDialog = null;
                }
            }

            mRecyclerView.setAdapter(mAdapter);
            mAdapter.setClick(new CheckRuleAdapter.MyClick() {
                @Override
                public void click(View view, int position) {
                    Intent intent = new Intent(CheckRuleActivity.this, LawDetailActivity.class);
                    intent.putExtra("LawId", mMapDatas.get(position).get("lawid").toString());
                    intent.putExtra("Keyword", mEtName.getText().toString());
                    intent.putExtra("LawContent", mMapDatas.get(position).get("lawcontent"));
                    intent.putExtra("LawName", mMapDatas.get(position).get("lawname").toString());
                    startActivity(intent);
                    overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                }
            });
        } catch (Exception e) {
            Log.e("法律数据库报错", e.toString(),e);
            //关闭加载页
            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
                mProgressDialog = null;
            }
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
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
