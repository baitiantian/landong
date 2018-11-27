package com.jinan.ladongjiguan.anjiantong.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jinan.ladongjiguan.anjiantong.PublicClass.AssetsDatabaseManager;
import com.jinan.ladongjiguan.anjiantong.PublicClass.DoubleDatePickerDialog;
import com.jinan.ladongjiguan.anjiantong.adapter.ProblemListAdapter;
import com.jinan.ladongjiguan.anjiantong.utils.SharedPreferencesUtil;
import com.jinan.ladongjiguan.anjiantong.PublicClass.StringOrDate;
import com.jinan.ladongjiguan.anjiantong.PublicClass.Utility;
import com.jinan.ladongjiguan.anjiantong.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ReviewModifyActivity extends BaseActivity implements View.OnTouchListener, View.OnClickListener {


    @BindView(R.id.examine_page_back)
    LinearLayout examinePageBack;
    @BindView(R.id.title_layout)
    TextView titleLayout;
    @BindView(R.id.check_enterprise_01)
    TextView checkEnterprise01;
    @BindView(R.id.check_enterprise_02)
    TextView checkEnterprise02;
    @BindView(R.id.check_enterprise_03)
    TextView checkEnterprise03;
    @BindView(R.id.check_enterprise_04)
    TextView checkEnterprise04;
    @BindView(R.id.check_enterprise_05)
    TextView checkEnterprise05;
    @BindView(R.id.check_enterprise_06)
    EditText checkEnterprise06;
    @BindView(R.id.from_time)
    TextView fromTime;
    @BindView(R.id.end_time)
    TextView endTime;
    @BindView(R.id.l_search_time)
    LinearLayout lSearchTime;
    @BindView(R.id.check_enterprise_11)
    TextView checkEnterprise11;
    @BindView(R.id.l_check_up_date_01)
    LinearLayout lCheckUpDate01;
    @BindView(R.id.bt_check_up_date_05)
    Button btCheckUpDate05;
    @BindView(R.id.bt_check_up_date_06)
    Button btCheckUpDate06;
    @BindView(R.id.l_bt_02)
    LinearLayout lBt02;
    @BindView(R.id.list_problem)
    ListView listProblem;
    @BindView(R.id.tx_list_table)
    LinearLayout txListTable;
    @BindView(R.id.del_text)
    TextView delText;
    @BindView(R.id.delete_layout)
    LinearLayout deleteLayout;

    private String s_from_time;//开始时间
    private Date d_from_time;
    private String s_end_time;//结束时间
    private Date d_end_time;
    // 创建一个List集合，List集合的元素是Map
    private List<HashMap<String, Object>> listItems = new ArrayList<>();
    private String DocumentId;
    private String BusinessId;

    @Override
    protected void initView() {
        setContentView(R.layout.review_modify_layout);
        ButterKnife.bind(this);
        titleLayout.setText("修改复查计划");
        deleteLayout.setVisibility(View.VISIBLE);
        openDate();
    }

    @Override
    protected void init() {
        examinePageBack.setOnClickListener(this);
        examinePageBack.setOnTouchListener(this);
        lSearchTime.setOnClickListener(this);
        btCheckUpDate05.setOnClickListener(this);
        btCheckUpDate06.setOnClickListener(this);
        checkEnterprise11.setOnClickListener(this);
        checkEnterprise11.setOnTouchListener(this);
        deleteLayout.setOnClickListener(this);
        openGroup();
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.examine_page_back://返回键
                onBackPressed();
                break;
            case R.id.bt_check_up_date_05://取消键
                onBackPressed();
                break;
            case R.id.bt_check_up_date_06://保存键
                saveDate();
                break;
            case R.id.check_enterprise_11://选择执法人员
                intent.setClass(ReviewModifyActivity.this, ReviewPeopleGroupActivity.class);
                intent.putExtra("state", "3");
                intent.putExtra("ReviewId", getIntent().getStringExtra("ReviewId"));
                startActivity(intent);
                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                break;
            case R.id.l_search_time://时间选择器
                Calendar c = Calendar.getInstance();
                // 最后一个false表示不显示日期，如果要显示日期，最后参数可以是true或者不用输入
                new DoubleDatePickerDialog(ReviewModifyActivity.this, 0, new DoubleDatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker startDatePicker, int startYear, int startMonthOfYear,
                                          int startDayOfMonth, DatePicker endDatePicker, int endYear, int endMonthOfYear,
                                          int endDayOfMonth) {
                        s_from_time = String.format("%d-%d-%d", startYear, startMonthOfYear + 1, startDayOfMonth);
                        d_from_time = StringOrDate.stringToDate(s_from_time);
                        fromTime.setText(s_from_time);
                        s_end_time = String.format("%d-%d-%d", endYear, endMonthOfYear + 1, endDayOfMonth);
                        d_end_time = StringOrDate.stringToDate(s_end_time);
                        endTime.setText(s_end_time);
                    }
                }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE), true).show();
                break;
            case R.id.delete_layout://删除
                dialog(getIntent().getStringExtra("ReviewId"));
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
        // 初始化，只需要调用一次
        AssetsDatabaseManager.initManager(getApplication());
        // 获取管理对象，因为数据库需要通过管理对象才能够获取
        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
        // 通过管理对象获取数据库
        SQLiteDatabase db = mg.getDatabase("users.db");
        db.delete("ELL_ReviewInfo", "ReviewId = ?",
                new String[]{getIntent().getStringExtra("ReviewId")});
        finish();
        overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
    }

    /**
     * 保存复查计划表
     */
    protected void saveDate() {
        // 初始化，只需要调用一次
        AssetsDatabaseManager.initManager(getApplication());
        // 获取管理对象，因为数据库需要通过管理对象才能够获取
        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
        // 通过管理对象获取数据库
        SQLiteDatabase db = mg.getDatabase("users.db");
        try {
        DateFormat df = new SimpleDateFormat(
                "yyyy-MM-dd", Locale.getDefault());
        String date = df.format(new Date());
        Date curDate = StringOrDate.stringToDate(date);//获取当前时间
        String userId = SharedPreferencesUtil.getStringData(this, "userId", "");
        String CompanyId = SharedPreferencesUtil.getStringData(this, "CompanyId", "");
        db.execSQL("REPLACE INTO ELL_ReviewInfo VALUES('" +
                getIntent().getStringExtra("ReviewId") + "','" +
                DocumentId + "','" +
                BusinessId + "','" +
                fromTime.getText().toString() + "','" +
                endTime.getText().toString() + "','" +
                checkEnterprise06.getText().toString() + "','" +
                CompanyId + "','" +
                userId + "','" +
                curDate + "','" +
                getIntent().getStringExtra("documentnumber") + "')");

//        Log.d("复查表数据", "REPLACE INTO ELL_ReviewInfo VALUES('" +
//                getIntent().getStringExtra("ReviewId") + "','" +
//                DocumentId + "','" +
//                BusinessId + "','" +
//                fromTime.getText().toString() + "','" +
//                endTime.getText().toString() + "','" +
//                checkEnterprise06.getText().toString() + "','" +
//                CompanyId + "','" +
//                userId + "','" +
//                curDate + "','" +
//                getIntent().getStringExtra("documentnumber") + "')");
            finish();
            Intent intent = new Intent();
            intent.setClass(this, ReviewImplementActivity.class);
            intent.putExtra("name", "1");
            intent.putExtra("ReviewId", getIntent().getStringExtra("ReviewId"));
            startActivity(intent);
            overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
        } catch (Exception e) {
            Log.e("数据库报错", e.toString());
        }
    }

    /**
     * 打开复查计划表数据
     */
    protected void openDate() {
        // 初始化，只需要调用一次
        AssetsDatabaseManager.initManager(getApplication());
        // 获取管理对象，因为数据库需要通过管理对象才能够获取
        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
        // 通过管理对象获取数据库
        SQLiteDatabase db = mg.getDatabase("users.db");
//        try {
        Cursor cursor = db.rawQuery("SELECT* FROM ELL_ReviewInfo WHERE ReviewId = ?",
                new String[]{getIntent().getStringExtra("ReviewId")});
        cursor.moveToFirst();
        DocumentId = cursor.getString(cursor.getColumnIndex("DocumentId"));
        BusinessId = cursor.getString(cursor.getColumnIndex("BusinessId"));
        fromTime.setText(cursor.getString(cursor.getColumnIndex("StartTime")));
        endTime.setText(cursor.getString(cursor.getColumnIndex("EndTime")));
        checkEnterprise06.setText(cursor.getString(cursor.getColumnIndex("Address")));
        Cursor c = db.rawQuery("SELECT* FROM ELL_Business WHERE BusinessId = ?",
                new String[]{cursor.getString(cursor.getColumnIndex("BusinessId"))});
        c.moveToFirst();
        checkEnterprise01.setText(c.getString(c.getColumnIndex("BusinessName")));
        checkEnterprise02.setText(c.getString(c.getColumnIndex("Address")));
        checkEnterprise03.setText(c.getString(c.getColumnIndex("LegalPerson")));
        checkEnterprise04.setText(c.getString(c.getColumnIndex("LegalPersonPost")));
        checkEnterprise05.setText(c.getString(c.getColumnIndex("LegalPersonPhone")));
        DateFormat df = new SimpleDateFormat(
                "yyyy-MM-dd", Locale.getDefault());
        //获取当前时间
        String date = df.format(new Date(System.currentTimeMillis()));
        fromTime.setText(date);
        d_from_time = StringOrDate.stringToDate(date);
        endTime.setText(date);
        d_end_time = StringOrDate.stringToDate(date);
        c.close();
        cursor.close();
        openHiddenDate();
//        } catch (Exception e) {
//            Log.e("数据库数据报错", e.toString());
//        }

    }
    /**
     * 打开组员
     * */
    protected void openGroup(){
        // 初始化，只需要调用一次
        AssetsDatabaseManager.initManager(getApplication());
        // 获取管理对象，因为数据库需要通过管理对象才能够获取
        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
        // 通过管理对象获取数据库
        SQLiteDatabase db = mg.getDatabase("users.db");
        Cursor cursor1 = db.rawQuery("SELECT* FROM ELL_ReviewGroupInfo WHERE ReviewId = ?",
                new String[]{getIntent().getStringExtra("ReviewId")});
        String s2 = "--点击选择--";
        int i = 0;
        while (cursor1.moveToNext()){
            try {
                Cursor cursor2 = db.rawQuery("SELECT* FROM Base_User WHERE UserId = ?",
                        new String[]{cursor1.getString(cursor1.getColumnIndex("Headman"))});
                cursor2.moveToFirst();
//                if (i == 0) {
                    s2 = cursor2.getString(cursor2.getColumnIndex("RealName")) + "组";
//                } else {
//                    s2 = s2 + "," + cursor2.getString(cursor2.getColumnIndex("RealName")) + "组";
//                }
                i++;
                cursor2.close();
            } catch (Exception e) {
                Log.e("人员用户数据库报错", e.toString());
            }
        }
        checkEnterprise11.setText(s2);
        cursor1.close();
    }
    /**
     * 打开匹配的隐患表
     */
    protected void openHiddenDate() {
        // 初始化，只需要调用一次
        AssetsDatabaseManager.initManager(getApplication());
        // 获取管理对象，因为数据库需要通过管理对象才能够获取
        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
        // 通过管理对象获取数据库
        SQLiteDatabase db = mg.getDatabase("users.db");
        try {
            Cursor cursor = db.rawQuery("SELECT* FROM ELL_HiddenDanger WHERE ReviewId = ?",
                    new String[]{getIntent().getStringExtra("ReviewId")});
            listItems = new ArrayList<>();
            while (cursor.moveToNext()) {
                HashMap<String, Object> listItem = new HashMap<>();
                listItem.put("hiddendangerid", cursor.getString(cursor.getColumnIndex("HiddenDangerId")));
                listItem.put("yhdd", cursor.getString(cursor.getColumnIndex("yhdd")));
                listItem.put("yhms", cursor.getString(cursor.getColumnIndex("yhms")));
                listItem.put("zgqx", cursor.getString(cursor.getColumnIndex("zgqx")));
                listItem.put("zglx", cursor.getString(cursor.getColumnIndex("zglx")));
                listItem.put("checkresult", cursor.getString(cursor.getColumnIndex("checkresult")));
                listItem.put("RealName", "");
                listItem.put("disposeresult",cursor.getString(cursor.getColumnIndex("disposeresult")));
                listItem.put("zglx1","");
                listItems.add(listItem);
            }
//            Log.d("隐患数据表", listItems.toString());
            cursor.close();
            ProblemListAdapter listAdapter = new ProblemListAdapter(ReviewModifyActivity.this, listItems, R.layout.review_hidden_list_item,
                    new String[]{"yhms", "checkresult", "disposeresult", "zgqx", "yhdd","RealName","zglx1"},
                    new int[]{R.id.tx_problem_01, R.id.tx_problem_02, R.id.tx_problem_03,
                            R.id.tx_problem_04, R.id.tx_problem_05, R.id.tx_problem_06, R.id.tx_problem_07});
            listProblem.setAdapter(listAdapter);
            Utility.setListViewHeightBasedOnChildren(listProblem);
        } catch (Exception e) {
            Log.e("数据库数据报错", e.toString());
        }
        Intent intent = new Intent();
        intent.setClass(ReviewModifyActivity.this, ReviewPeopleGroupActivity.class);
        intent.putExtra("state", "3");
        intent.putExtra("ReviewId", getIntent().getStringExtra("ReviewId"));
        startActivityForResult(intent,0);
        overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
    }

    /**
     * 弹出确认对话框
     */
    protected void dialog(final String s) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("确认删除");
        builder.setMessage("是否删除复查计划 ？");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                deleterPlaneDate(s);
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Toast.makeText(ReviewModifyActivity.this, "已取消", Toast.LENGTH_SHORT).show();
            }
        });
        builder.show();
    }

    /**
     * 删除数据
     */
    protected void deleterPlaneDate(String s) {
        // 初始化，只需要调用一次
        AssetsDatabaseManager.initManager(getApplication());
        // 获取管理对象，因为数据库需要通过管理对象才能够获取
        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
        // 通过管理对象获取数据库
        SQLiteDatabase db = mg.getDatabase("users.db");
        try {
            db.delete("ELL_ReviewInfo", "ReviewId = ?", new String[]{s});
            Toast.makeText(this, "已删除", Toast.LENGTH_SHORT).show();
            finish();
        } catch (Exception e) {
            Log.e("删除复查计划数据库报错", e.toString());
        }
    }
    /**
     * 返回刷新
     * */
    @Override
    protected void onResume() {
        init();
        super.onResume();
    }

    /**
     * 返回
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case 0:
                switch (resultCode){
                    case 0:
                        // 初始化，只需要调用一次
                        AssetsDatabaseManager.initManager(getApplication());
                        // 获取管理对象，因为数据库需要通过管理对象才能够获取
                        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
                        // 通过管理对象获取数据库
                        SQLiteDatabase db = mg.getDatabase("users.db");
                        db.delete("ELL_ReviewInfo", "ReviewId = ?",
                                new String[]{getIntent().getStringExtra("ReviewId")});
                        onBackPressed();
                        break;
                    case 1:
//                        saveDate();

                        break;
                    default:
                        break;
            }
                break;
            default:
                break;
        }
    }
}
