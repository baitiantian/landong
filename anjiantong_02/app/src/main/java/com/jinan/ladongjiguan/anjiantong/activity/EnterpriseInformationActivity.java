package com.jinan.ladongjiguan.anjiantong.activity;

import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.jinan.ladongjiguan.anjiantong.PublicClass.AssetsDatabaseManager;
import com.jinan.ladongjiguan.anjiantong.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EnterpriseInformationActivity extends BaseActivity implements View.OnClickListener, View.OnTouchListener {
    @BindView(R.id.examine_page_back)
    LinearLayout examinePageBack;
    @BindView(R.id.title_layout)
    TextView titleLayout;
    @BindView(R.id.e_car_num_6)
    Spinner eCarNum6;
    @BindView(R.id.list_01)
    ListView list01;
    @BindView(R.id.bt_enterprise_information_01)
    Button btEnterpriseInformation01;
    @BindView(R.id.bt_enterprise_information_02)
    Button btEnterpriseInformation02;
    @BindView(R.id.del_text)
    TextView delText;
    @BindView(R.id.delete_layout)
    LinearLayout deleteLayout;
    @BindView(R.id.bt_capture)
    Button btCapture;
    private String string = "";//下拉列表选择的
    // 创建一个List集合，List集合的元素是Map
    private List<Map<String, Object>> listItems = new ArrayList<>();
    //输入框
    private EditText et_name;

    @Override
    protected void initView() {
        setContentView(R.layout.enterprise_information_layout);
        ButterKnife.bind(this);
        titleLayout.setText("企业信息");//设置题目
        delText.setText("新增");
        deleteLayout.setVisibility(View.VISIBLE);
    }

    @Override
    protected void init() {
        deleteLayout.setOnClickListener(this);
        deleteLayout.setOnTouchListener(this);
        et_name = (EditText) findViewById(R.id.et_name);//输入名字
        /**
         * 下拉列表
         * */
        Resources res = getResources();
        final String[] strings01 = res.getStringArray(R.array.s_car_num_10);
        eCarNum6.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView v1 = (TextView) view;
                v1.setTextColor(ContextCompat.getColor(EnterpriseInformationActivity.this, R.color.main_color_3));
                v1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                string = "";
                if (position != 0) {
                    string = strings01[position];
                    date("2", et_name.getText().toString());
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        /**
         * 点击事件
         * */
        examinePageBack.setOnClickListener(this);
        btEnterpriseInformation01.setOnClickListener(this);
        btEnterpriseInformation02.setOnClickListener(this);
        btCapture.setOnClickListener(this);
        /**
         * 触摸事件
         * */

        examinePageBack.setOnTouchListener(this);
        /**
         * 监听输入文字变化
         * */
        et_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                date("3", et_name.getText().toString());
//                Log.d("数据",et_name.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.examine_page_back://返回键
                onBackPressed();
                break;
            case R.id.bt_enterprise_information_01://非重点
                date("0", et_name.getText().toString());
                break;
            case R.id.bt_enterprise_information_02://重点
                date("1", et_name.getText().toString());
                break;
            case R.id.delete_layout://增加新企业
                intent.setClass(this, EnterpriseInformationAddActivity.class);
                startActivity(intent);
                break;
            case R.id.bt_capture://扫描二维码
                intent.setClass(this, DefaultCaptureActivity.class);
                startActivityForResult(intent, 0);
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
     * 构建列表
     */
    protected void addItem() {


        // 创建一个SimpleAdapter
        SimpleAdapter simpleAdapter = new SimpleAdapter(EnterpriseInformationActivity.this, listItems,
                R.layout.enterprise_information_list_item,
                new String[]{"BusinessName"},
                new int[]{R.id.enterprise_name});

        // 为ListView设置Adapter
        list01.setAdapter(simpleAdapter);

        // 为ListView的列表项的单击事件绑定事件监听器
        list01.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            // 第position项被单击时激发该方法
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
//                Toast.makeText(EnterpriseInformationActivity.this, listItems.get(position).get("BusinessName")
//                        + "被单击了", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                if (getIntent().getStringExtra("state").equals("check")) {
                    intent.putExtra("name", listItems.get(position).get("BusinessName").toString());
                    intent.putExtra("BusinessId", listItems.get(position).get("BusinessId").toString());
                    setResult(0, intent);
                    finish();
                    overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                } else {
                    intent.putExtra("BusinessId", listItems.get(position).get("BusinessId").toString());
                    intent.setClass(EnterpriseInformationActivity.this, EnterpriseInformationDateActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                }

            }
        });

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        if (getIntent().getStringExtra("state").equals("check")) {
            setResult(1, intent);
            finish();
            overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
        } else {
            finish();
            overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
        }
    }

    /**
     * 打开企业信息数据库
     */
    protected void date(String s, String name) {

        // 初始化，只需要调用一次
        AssetsDatabaseManager.initManager(getApplication());
        // 获取管理对象，因为数据库需要通过管理对象才能够获取
        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
        // 通过管理对象获取数据库
        SQLiteDatabase db = mg.getDatabase("users.db");

        try {
            // 对数据库进行操作
            Cursor c = db.rawQuery("SELECT* FROM ELL_Business", null);
            switch (s) {
                case "0":
                    if (string.length() > 0 && name.length() == 0) {
                        c = db.rawQuery("SELECT* FROM ELL_Business WHERE Emphases = ? AND BusinessType = ? AND ValidFlag = 0 ",
                                new String[]{s, string});
                    } else if (string.length() > 0 && name.length() > 0) {
                        c = db.rawQuery("SELECT* FROM ELL_Business WHERE Emphases = ? AND BusinessType = ?  AND ValidFlag = 0 " +
                                        "AND BusinessName like ?",
                                new String[]{s, string, "%" + name + "%"});
                    } else if (string.length() == 0 && name.length() > 0) {
                        c = db.rawQuery("SELECT* FROM ELL_Business WHERE Emphases = ?  AND ValidFlag = 0 " +
                                        "AND BusinessName like ?",
                                new String[]{s, "%" + name + "%"});
                    } else {
                        c = db.rawQuery("SELECT* FROM ELL_Business WHERE Emphases = ? AND ValidFlag = 0 ",
                                new String[]{s});
                    }
                    break;
                case "1":
                    if (string.length() > 0 && name.length() == 0) {
                        c = db.rawQuery("SELECT* FROM ELL_Business WHERE Emphases = ? AND BusinessType = ? AND ValidFlag = 0 ",
                                new String[]{s, string});
                    } else if (string.length() > 0 && name.length() > 0) {
                        c = db.rawQuery("SELECT* FROM ELL_Business WHERE Emphases = ? AND BusinessType = ? AND ValidFlag = 0 " +
                                        "AND BusinessName like ?",
                                new String[]{s, string, "%" + name + "%"});
                    } else if (string.length() == 0 && name.length() > 0) {
                        c = db.rawQuery("SELECT* FROM ELL_Business WHERE Emphases = ? AND ValidFlag = 0 " +
                                        "AND BusinessName like ?",
                                new String[]{s, "%" + name + "%"});
                    } else {
                        c = db.rawQuery("SELECT* FROM ELL_Business WHERE Emphases = ? AND ValidFlag = 0 ",
                                new String[]{s});
                    }
                    break;
                case "2":
                    if (string.length() > 0 && name.length() == 0) {
                        c = db.rawQuery("SELECT* FROM ELL_Business WHERE  BusinessType = ? AND ValidFlag = 0 ",
                                new String[]{string});
                    } else if (string.length() > 0 && name.length() > 0) {
                        c = db.rawQuery("SELECT* FROM ELL_Business WHERE BusinessType = ?  AND ValidFlag = 0 " +
                                        "AND BusinessName like ?",
                                new String[]{string, "%" + name + "%"});
                    } else if (string.length() == 0 && name.length() > 0) {
                        c = db.rawQuery("SELECT* FROM ELL_Business WHERE ValidFlag = 0 " +
                                        "AND BusinessName like ?",
                                new String[]{"%" + name + "%"});
                    }
                    break;
                case "3":
                    if (string.length() > 0 && name.length() == 0) {
                        c = db.rawQuery("SELECT* FROM ELL_Business WHERE  BusinessType = ? AND ValidFlag = 0 ",
                                new String[]{string});
                    } else if (string.length() > 0 && name.length() > 0) {
                        c = db.rawQuery("SELECT* FROM ELL_Business WHERE BusinessType = ?  AND ValidFlag = 0 " +
                                        "AND BusinessName like ?",
                                new String[]{string, "%" + name + "%"});
                    } else if (string.length() == 0 && name.length() > 0) {
                        c = db.rawQuery("SELECT* FROM ELL_Business WHERE ValidFlag = 0 " +
                                        "AND BusinessName like ?",
                                new String[]{"%" + name + "%"});
                    }
                    break;
                case "4":
                    c = db.rawQuery("SELECT* FROM ELL_Business WHERE  BusinessId = ? AND ValidFlag = 0 ",
                            new String[]{name});
                    if(c.getCount() == 0){
                        Toast.makeText(this, "无效二维码", Toast.LENGTH_LONG).show();
                    }
                    break;
                default:
                    break;
            }

            // 创建一个List集合，List集合的元素是Map

            listItems = new ArrayList<>();
            Map<String, Object> listItem;
            while (c.moveToNext()) {
                listItem = new HashMap<>();
                listItem.put("BusinessName", c.getString(c.getColumnIndex("BusinessName")));
                listItem.put("BusinessId", c.getString(c.getColumnIndex("BusinessId")));
                listItems.add(listItem);
            }

            addItem();
            c.close();
        } catch (Exception e) {
            Log.e("数据库报错", e.toString());
        }
    }

    /**
     * 返回值
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0://返回二维码数据
                if (resultCode == 1) {
                    date("4", data.getStringExtra("BusinessId"));
                }
                break;
            default:
                break;
        }
    }
}
