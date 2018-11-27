package com.jinan.ladongjiguan.anjiantong.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.jinan.ladongjiguan.anjiantong.PublicClass.AssetsDatabaseManager;
import com.jinan.ladongjiguan.anjiantong.utils.SharedPreferencesUtil;
import com.jinan.ladongjiguan.anjiantong.R;

import butterknife.BindView;
import butterknife.ButterKnife;


public class EnterpriseInformationDateActivity extends BaseActivity implements View.OnClickListener, View.OnTouchListener {


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
    TextView checkEnterprise06;
    @BindView(R.id.check_enterprise_07)
    TextView checkEnterprise07;
    @BindView(R.id.check_enterprise_08)
    TextView checkEnterprise08;
    @BindView(R.id.check_enterprise_09)
    TextView checkEnterprise09;
    @BindView(R.id.check_enterprise_10)
    TextView checkEnterprise10;
    @BindView(R.id.bt_enterprise_information_date_01)
    Button btEnterpriseInformationDate01;
    @BindView(R.id.del_text)
    TextView delText;
    @BindView(R.id.delete_layout)
    LinearLayout deleteLayout;
    @BindView(R.id.tv_enterprise_information_date_03)
    TextView tvEnterpriseInformationDate03;
    @BindView(R.id.check_enterprise_11)
    TextView checkEnterprise11;
    @BindView(R.id.check_enterprise_12)
    TextView checkEnterprise12;
    @BindView(R.id.check_enterprise_13)
    TextView checkEnterprise13;
    @BindView(R.id.check_enterprise_14)
    TextView checkEnterprise14;
    @BindView(R.id.check_enterprise_15)
    TextView checkEnterprise15;
    @BindView(R.id.check_enterprise_16)
    TextView checkEnterprise16;
    @BindView(R.id.check_enterprise_17)
    TextView checkEnterprise17;
    @BindView(R.id.check_enterprise_18)
    TextView checkEnterprise18;
    @BindView(R.id.check_enterprise_19)
    TextView checkEnterprise19;
    @BindView(R.id.check_enterprise_20)
    TextView checkEnterprise20;
    @BindView(R.id.check_enterprise_21)
    TextView checkEnterprise21;
    @BindView(R.id.check_enterprise_22)
    TextView checkEnterprise22;
    @BindView(R.id.check_enterprise_23)
    TextView checkEnterprise23;
    @BindView(R.id.check_enterprise_24)
    TextView checkEnterprise24;
    @BindView(R.id.check_enterprise_25)
    TextView checkEnterprise25;
    @BindView(R.id.check_enterprise_26)
    TextView checkEnterprise26;
    @BindView(R.id.check_enterprise_27)
    TextView checkEnterprise27;
    @BindView(R.id.check_enterprise_28)
    TextView checkEnterprise28;
    @BindView(R.id.l_coal_business)
    LinearLayout lCoalBusiness;
    @BindView(R.id.et_check_enterprise_01)
    EditText etCheckEnterprise01;
    @BindView(R.id.et_check_enterprise_02)
    EditText etCheckEnterprise02;
    @BindView(R.id.et_check_enterprise_03)
    EditText etCheckEnterprise03;
    @BindView(R.id.et_check_enterprise_04)
    EditText etCheckEnterprise04;
    @BindView(R.id.et_check_enterprise_05)
    EditText etCheckEnterprise05;
    @BindView(R.id.et_check_enterprise_06)
    EditText etCheckEnterprise06;
    @BindView(R.id.et_check_enterprise_07)
    EditText etCheckEnterprise07;
    @BindView(R.id.et_check_enterprise_08)
    EditText etCheckEnterprise08;
    @BindView(R.id.et_check_enterprise_09)
    EditText etCheckEnterprise09;
    @BindView(R.id.et_check_enterprise_10)
    EditText etCheckEnterprise10;
    @BindView(R.id.et_check_enterprise_11)
    EditText etCheckEnterprise11;
    @BindView(R.id.et_check_enterprise_12)
    EditText etCheckEnterprise12;
    @BindView(R.id.et_check_enterprise_13)
    EditText etCheckEnterprise13;
    @BindView(R.id.et_check_enterprise_14)
    EditText etCheckEnterprise14;
    @BindView(R.id.et_check_enterprise_15)
    EditText etCheckEnterprise15;
    @BindView(R.id.et_check_enterprise_16)
    EditText etCheckEnterprise16;
    @BindView(R.id.et_check_enterprise_17)
    EditText etCheckEnterprise17;
    @BindView(R.id.et_check_enterprise_18)
    EditText etCheckEnterprise18;
    @BindView(R.id.et_check_enterprise_19)
    EditText etCheckEnterprise19;
    @BindView(R.id.et_check_enterprise_20)
    EditText etCheckEnterprise20;
    @BindView(R.id.et_check_enterprise_21)
    EditText etCheckEnterprise21;
    @BindView(R.id.et_check_enterprise_22)
    EditText etCheckEnterprise22;
    @BindView(R.id.et_check_enterprise_23)
    EditText etCheckEnterprise23;
    @BindView(R.id.et_check_enterprise_24)
    EditText etCheckEnterprise24;
    @BindView(R.id.et_check_enterprise_25)
    EditText etCheckEnterprise25;
    @BindView(R.id.et_check_enterprise_26)
    EditText etCheckEnterprise26;
    @BindView(R.id.et_check_enterprise_27)
    EditText etCheckEnterprise27;
    @BindView(R.id.et_check_enterprise_28)
    EditText etCheckEnterprise28;
    private PopupWindow popupwindow;
    private String modifyindex;
    private String validflag;
    @Override
    protected void initView() {
        setContentView(R.layout.enterprise_information_date_layout);
        ButterKnife.bind(this);
        titleLayout.setText("企业详细信息");
        deleteLayout.setVisibility(View.VISIBLE);
        delText.setText("菜单");

    }

    @Override
    protected void init() {
        /**
         * 点击事件
         * */
        examinePageBack.setOnClickListener(this);
        btEnterpriseInformationDate01.setOnClickListener(this);
        deleteLayout.setOnClickListener(this);

        /**
         * 触摸事件
         * */
        examinePageBack.setOnTouchListener(this);
        deleteLayout.setOnTouchListener(this);

        /**
         *打开数据库
         * */
        date(getIntent().getStringExtra("BusinessId"));

    }

    @Override
    public void onClick(View v) {
        //设置可查看权限
        String headShip= SharedPreferencesUtil.getStringData(this, "Headship","1");

        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.examine_page_back://返回键
                onBackPressed();
                break;
            case R.id.bt_enterprise_information_date_01://保存键
                saveDate();
                break;
            case R.id.delete_layout:
                if (popupwindow != null && popupwindow.isShowing()) {
                    popupwindow.dismiss();
                    return;
                } else {
                    initmPopupWindowView();
                    popupwindow.showAsDropDown(v, 0, 5);
                }
                break;
            case R.id.button2://制定计划
                //判断权限
                if(!headShip.equals("办公室主任")) {
                    intent.setClass(this, CheckUpDateActivity.class);
                    intent.putExtra("state", "5");
                    intent.putExtra("BusinessId", getIntent().getStringExtra("BusinessId"));
                    intent.putExtra("name", "6");
                    intent.putExtra("BusinessName", checkEnterprise01.getText());
                    startActivity(intent);
                    overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                }else {
                    Toast.makeText(this, "您没有制定的权限", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.button3://监管执法
                intent.setClass(this, CheckUpActivity.class);
                intent.putExtra("name", "6");
                intent.putExtra("BusinessId", getIntent().getStringExtra("BusinessId"));
                intent.putExtra("BusinessName", checkEnterprise01.getText());
                intent.putExtra("state", "5");
                startActivity(intent);
                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                break;
            case R.id.button4://执法统计
                intent.setClass(this, CountActivity.class);
                intent.putExtra("BusinessId", getIntent().getStringExtra("BusinessId"));
                intent.putExtra("BusinessName", checkEnterprise01.getText());
                intent.putExtra("date_state", "8");
                startActivity(intent);
                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                break;
            case R.id.button5://修改企业信息
                modifyDate();
                if (popupwindow != null && popupwindow.isShowing()) {
                    popupwindow.dismiss();
                    return;
                }
                break;
            case R.id.button6://出具现场检查方案文书
                intent.setClass(this, CheckUpActivity.class);
                intent.putExtra("name", "7");
                intent.putExtra("BusinessId", getIntent().getStringExtra("BusinessId"));
                intent.putExtra("BusinessName", checkEnterprise01.getText());
                intent.putExtra("state", "9");
                startActivity(intent);
                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                break;
            case R.id.button7://企业隐患统计查询
                intent.setClass(this, CountEnterpriseActivity.class);
                intent.putExtra("name", "7");
                intent.putExtra("BusinessId", getIntent().getStringExtra("BusinessId"));
                intent.putExtra("BusinessName", checkEnterprise01.getText());
                intent.putExtra("state", "3");
                startActivity(intent);
                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                break;
            case R.id.button8://制定复查计划
                intent.setClass(this, ReviewPlanDateActivity.class);
                intent.putExtra("name", checkEnterprise01.getText());
                intent.putExtra("BusinessId", getIntent().getStringExtra("BusinessId"));
                startActivity(intent);
                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                break;
            default:
                break;
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

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
    }

    /**
     * 打开企业信息数据库
     */
    protected void date(String s) {

        // 初始化，只需要调用一次
        AssetsDatabaseManager.initManager(getApplication());
        // 获取管理对象，因为数据库需要通过管理对象才能够获取
        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
        // 通过管理对象获取数据库
        SQLiteDatabase db = mg.getDatabase("users.db");

        try {
            // 对数据库进行操作
            String s_c = "SELECT* FROM ELL_Business WHERE BusinessId = ?";
            Cursor c = db.rawQuery(s_c, new String[]{s});
            c.moveToFirst();
            // 填入数据
            String s1 = c.getString(c.getColumnIndex("BusinessName"));
            checkEnterprise01.setText(s1);
            etCheckEnterprise01.setText(s1);
            checkEnterprise02.setText(c.getString(c.getColumnIndex("RegistrationNumber")));
            etCheckEnterprise02.setText(c.getString(c.getColumnIndex("RegistrationNumber")));
            checkEnterprise03.setText(c.getString(c.getColumnIndex("OrgCode")));
            etCheckEnterprise03.setText(c.getString(c.getColumnIndex("OrgCode")));
            checkEnterprise04.setText(c.getString(c.getColumnIndex("BusinessType")));
            etCheckEnterprise04.setText(c.getString(c.getColumnIndex("BusinessType")));
            checkEnterprise05.setText(c.getString(c.getColumnIndex("Address")));
            etCheckEnterprise05.setText(c.getString(c.getColumnIndex("Address")));
            checkEnterprise06.setText(c.getString(c.getColumnIndex("LegalPerson")));
            etCheckEnterprise06.setText(c.getString(c.getColumnIndex("LegalPerson")));
            checkEnterprise07.setText(c.getString(c.getColumnIndex("LegalPersonPost")));
            etCheckEnterprise07.setText(c.getString(c.getColumnIndex("LegalPersonPost")));
            checkEnterprise08.setText(c.getString(c.getColumnIndex("LegalPersonPhone")));
            etCheckEnterprise08.setText(c.getString(c.getColumnIndex("LegalPersonPhone")));
            checkEnterprise09.setText(c.getString(c.getColumnIndex("SafetyOfficer")));
            etCheckEnterprise09.setText(c.getString(c.getColumnIndex("SafetyOfficer")));
            checkEnterprise10.setText(c.getString(c.getColumnIndex("SafetyOfficerPhone")));
            etCheckEnterprise10.setText(c.getString(c.getColumnIndex("SafetyOfficerPhone")));
            modifyindex = c.getString(c.getColumnIndex("ModifyIndex"));
            validflag = "0";
            if (c.getString(c.getColumnIndex("BusinessType")).equals("煤矿")) {
                lCoalBusiness.setVisibility(View.VISIBLE);
                String s2;
                checkEnterprise11.setText(c.getString(c.getColumnIndex("MineShaftCond")));//矿井状态
                etCheckEnterprise11.setText(c.getString(c.getColumnIndex("MineShaftCond")));
                checkEnterprise12.setText(c.getString(c.getColumnIndex("MineShaftType")));//矿井类型
                etCheckEnterprise12.setText(c.getString(c.getColumnIndex("MineShaftType")));
                s2 = c.getString(c.getColumnIndex("GeologicalReserves"))+"万吨";
                checkEnterprise13.setText(s2);//地质储量
                etCheckEnterprise13.setText(c.getString(c.getColumnIndex("GeologicalReserves")));
                s2 = c.getString(c.getColumnIndex("WorkableReserves"))+"万吨";
                checkEnterprise14.setText(s2);//可采储量
                etCheckEnterprise14.setText(c.getString(c.getColumnIndex("WorkableReserves")));
                s2 = c.getString(c.getColumnIndex("DesignProdCapacity"))+"万吨/年";
                checkEnterprise15.setText(s2);//设计生产能力
                etCheckEnterprise15.setText(c.getString(c.getColumnIndex("DesignProdCapacity")));
                s2 = c.getString(c.getColumnIndex("CheckProdCapacity"))+"万吨/年";
                checkEnterprise16.setText(s2);//核定生产能力
                etCheckEnterprise16.setText(c.getString(c.getColumnIndex("CheckProdCapacity")));
                s2 = c.getString(c.getColumnIndex("Area"))+"平方千米";
                checkEnterprise17.setText(s2);//矿区面积
                etCheckEnterprise17.setText(c.getString(c.getColumnIndex("Area")));
                s2 = c.getString(c.getColumnIndex("Wthdraw"))+"万吨/年";
                checkEnterprise18.setText(s2);//上半年产量
                etCheckEnterprise18.setText(c.getString(c.getColumnIndex("Wthdraw")));
                checkEnterprise19.setText(c.getString(c.getColumnIndex("CoalType")));//主要煤种
                etCheckEnterprise19.setText(c.getString(c.getColumnIndex("CoalType")));
                checkEnterprise20.setText(c.getString(c.getColumnIndex("WorkableSeam")));//可采煤层
                etCheckEnterprise20.setText(c.getString(c.getColumnIndex("WorkableSeam")));
                checkEnterprise21.setText(c.getString(c.getColumnIndex("ExploreCraft")));//开采工艺
                etCheckEnterprise21.setText(c.getString(c.getColumnIndex("ExploreCraft")));
                checkEnterprise22.setText(c.getString(c.getColumnIndex("ContractPhone")));//开拓方式
                etCheckEnterprise22.setText(c.getString(c.getColumnIndex("ContractPhone")));
                checkEnterprise23.setText(c.getString(c.getColumnIndex("ExploreMode")));//运输方式
                etCheckEnterprise23.setText(c.getString(c.getColumnIndex("ExploreMode")));
                checkEnterprise24.setText(c.getString(c.getColumnIndex("RiskAppraisal")));//突出危险性鉴定
                etCheckEnterprise24.setText(c.getString(c.getColumnIndex("RiskAppraisal")));
                checkEnterprise25.setText(c.getString(c.getColumnIndex("GasLevel")));//瓦斯等级
                etCheckEnterprise25.setText(c.getString(c.getColumnIndex("GasLevel")));
                checkEnterprise26.setText(c.getString(c.getColumnIndex("CoalSeamLevel")));//煤层自燃等级
                etCheckEnterprise26.setText(c.getString(c.getColumnIndex("CoalSeamLevel")));
                checkEnterprise27.setText(c.getString(c.getColumnIndex("Explosion")));//煤层爆炸性
                etCheckEnterprise27.setText(c.getString(c.getColumnIndex("Explosion")));
                checkEnterprise28.setText(c.getString(c.getColumnIndex("VentilateMode")));//矿井通风方式
                etCheckEnterprise28.setText(c.getString(c.getColumnIndex("VentilateMode")));
            }
            c.close();
        } catch (Exception e) {
            Log.e("数据库报错", e.toString());
        }
    }

    /**
     * 弹出菜单
     */
    public void initmPopupWindowView() {

        // // 获取自定义布局文件pop.xml的视图
        View customView = getLayoutInflater().inflate(R.layout.popview_item,
                null, false);
        // 创建PopupWindow实例,200,150分别是宽度和高度
        popupwindow = new PopupWindow(customView, 250, getWallpaperDesiredMinimumHeight());
        // 设置动画效果 [R.style.AnimationFade 是自己事先定义好的]
        popupwindow.setAnimationStyle(R.style.AnimationFade);
        popupwindow.setFocusable(true);
        // 自定义view添加触摸事件
        customView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (popupwindow != null && popupwindow.isShowing()) {
                    popupwindow.dismiss();
                    popupwindow = null;
                }

                return false;
            }
        });

        /** 在这里可以实现自定义视图的功能 */
        Button btton2 = (Button) customView.findViewById(R.id.button2);
        Button btton3 = (Button) customView.findViewById(R.id.button3);
        Button btton4 = (Button) customView.findViewById(R.id.button4);
        Button btton5 = (Button) customView.findViewById(R.id.button5);
        Button btton6 = (Button) customView.findViewById(R.id.button6);
        Button btton7 = (Button) customView.findViewById(R.id.button7);
        Button btton8 = (Button) customView.findViewById(R.id.button8);
        btton2.setOnClickListener(this);
        btton3.setOnClickListener(this);
        btton4.setOnClickListener(this);
        btton5.setOnClickListener(this);
        btton6.setOnClickListener(this);
        btton7.setOnClickListener(this);
        btton8.setOnClickListener(this);
        btton2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    v.setBackgroundColor(ContextCompat.getColor(EnterpriseInformationDateActivity.this, R.color.main_color_2));
                } else if (event.getAction() == MotionEvent.ACTION_CANCEL) {
                    v.setBackgroundColor(ContextCompat.getColor(EnterpriseInformationDateActivity.this, R.color.main_color_3));
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    v.setBackgroundColor(ContextCompat.getColor(EnterpriseInformationDateActivity.this, R.color.main_color_3));
                }
                return false;
            }
        });
        btton3.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    v.setBackgroundColor(ContextCompat.getColor(EnterpriseInformationDateActivity.this, R.color.main_color_2));
                } else if (event.getAction() == MotionEvent.ACTION_CANCEL) {
                    v.setBackgroundColor(ContextCompat.getColor(EnterpriseInformationDateActivity.this, R.color.main_color_3));
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    v.setBackgroundColor(ContextCompat.getColor(EnterpriseInformationDateActivity.this, R.color.main_color_3));
                }
                return false;
            }
        });
        btton4.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    v.setBackgroundColor(ContextCompat.getColor(EnterpriseInformationDateActivity.this, R.color.main_color_2));
                } else if (event.getAction() == MotionEvent.ACTION_CANCEL) {
                    v.setBackgroundColor(ContextCompat.getColor(EnterpriseInformationDateActivity.this, R.color.main_color_3));
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    v.setBackgroundColor(ContextCompat.getColor(EnterpriseInformationDateActivity.this, R.color.main_color_3));
                }
                return false;
            }
        });
        btton5.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    v.setBackgroundColor(ContextCompat.getColor(EnterpriseInformationDateActivity.this, R.color.main_color_2));
                } else if (event.getAction() == MotionEvent.ACTION_CANCEL) {
                    v.setBackgroundColor(ContextCompat.getColor(EnterpriseInformationDateActivity.this, R.color.main_color_3));
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    v.setBackgroundColor(ContextCompat.getColor(EnterpriseInformationDateActivity.this, R.color.main_color_3));
                }
                return false;
            }
        });
        btton6.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    v.setBackgroundColor(ContextCompat.getColor(EnterpriseInformationDateActivity.this, R.color.main_color_2));
                } else if (event.getAction() == MotionEvent.ACTION_CANCEL) {
                    v.setBackgroundColor(ContextCompat.getColor(EnterpriseInformationDateActivity.this, R.color.main_color_3));
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    v.setBackgroundColor(ContextCompat.getColor(EnterpriseInformationDateActivity.this, R.color.main_color_3));
                }
                return false;
            }
        });
        btton7.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    v.setBackgroundColor(ContextCompat.getColor(EnterpriseInformationDateActivity.this, R.color.main_color_2));
                } else if (event.getAction() == MotionEvent.ACTION_CANCEL) {
                    v.setBackgroundColor(ContextCompat.getColor(EnterpriseInformationDateActivity.this, R.color.main_color_3));
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    v.setBackgroundColor(ContextCompat.getColor(EnterpriseInformationDateActivity.this, R.color.main_color_3));
                }
                return false;
            }
        });
        btton8.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    v.setBackgroundColor(ContextCompat.getColor(EnterpriseInformationDateActivity.this, R.color.main_color_2));
                } else if (event.getAction() == MotionEvent.ACTION_CANCEL) {
                    v.setBackgroundColor(ContextCompat.getColor(EnterpriseInformationDateActivity.this, R.color.main_color_3));
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    v.setBackgroundColor(ContextCompat.getColor(EnterpriseInformationDateActivity.this, R.color.main_color_3));
                }
                return false;
            }
        });
    }

    /**
     * 修改企业信息
     */
    protected void modifyDate() {
        checkEnterprise01.setVisibility(View.GONE);
        etCheckEnterprise01.setVisibility(View.VISIBLE);
        checkEnterprise02.setVisibility(View.GONE);
        etCheckEnterprise02.setVisibility(View.VISIBLE);
        checkEnterprise03.setVisibility(View.GONE);
        etCheckEnterprise03.setVisibility(View.VISIBLE);
        checkEnterprise04.setVisibility(View.GONE);
        etCheckEnterprise04.setVisibility(View.VISIBLE);
        checkEnterprise05.setVisibility(View.GONE);
        etCheckEnterprise05.setVisibility(View.VISIBLE);
        checkEnterprise06.setVisibility(View.GONE);
        etCheckEnterprise06.setVisibility(View.VISIBLE);
        checkEnterprise07.setVisibility(View.GONE);
        etCheckEnterprise07.setVisibility(View.VISIBLE);
        checkEnterprise08.setVisibility(View.GONE);
        etCheckEnterprise08.setVisibility(View.VISIBLE);
        checkEnterprise09.setVisibility(View.GONE);
        etCheckEnterprise09.setVisibility(View.VISIBLE);
        checkEnterprise10.setVisibility(View.GONE);
        etCheckEnterprise10.setVisibility(View.VISIBLE);
       btEnterpriseInformationDate01.setVisibility(View.VISIBLE);
        if(checkEnterprise04.getText().toString().equals("煤矿")) {
            checkEnterprise11.setVisibility(View.GONE);
            etCheckEnterprise11.setVisibility(View.VISIBLE);
            checkEnterprise12.setVisibility(View.GONE);
            etCheckEnterprise12.setVisibility(View.VISIBLE);
            checkEnterprise13.setVisibility(View.GONE);
            etCheckEnterprise13.setVisibility(View.VISIBLE);
            checkEnterprise14.setVisibility(View.GONE);
            etCheckEnterprise14.setVisibility(View.VISIBLE);
            checkEnterprise15.setVisibility(View.GONE);
            etCheckEnterprise15.setVisibility(View.VISIBLE);
            checkEnterprise16.setVisibility(View.GONE);
            etCheckEnterprise16.setVisibility(View.VISIBLE);
            checkEnterprise17.setVisibility(View.GONE);
            etCheckEnterprise17.setVisibility(View.VISIBLE);
            checkEnterprise18.setVisibility(View.GONE);
            etCheckEnterprise18.setVisibility(View.VISIBLE);
            checkEnterprise19.setVisibility(View.GONE);
            etCheckEnterprise19.setVisibility(View.VISIBLE);
            checkEnterprise20.setVisibility(View.GONE);
            etCheckEnterprise20.setVisibility(View.VISIBLE);
            checkEnterprise21.setVisibility(View.GONE);
            etCheckEnterprise21.setVisibility(View.VISIBLE);
            checkEnterprise22.setVisibility(View.GONE);
            etCheckEnterprise22.setVisibility(View.VISIBLE);
            checkEnterprise23.setVisibility(View.GONE);
            etCheckEnterprise23.setVisibility(View.VISIBLE);
            checkEnterprise24.setVisibility(View.GONE);
            etCheckEnterprise24.setVisibility(View.VISIBLE);
            checkEnterprise25.setVisibility(View.GONE);
            etCheckEnterprise25.setVisibility(View.VISIBLE);
            checkEnterprise26.setVisibility(View.GONE);
            etCheckEnterprise26.setVisibility(View.VISIBLE);
            checkEnterprise27.setVisibility(View.GONE);
            etCheckEnterprise27.setVisibility(View.VISIBLE);
            checkEnterprise28.setVisibility(View.GONE);
            etCheckEnterprise28.setVisibility(View.VISIBLE);
        }

    }
    /**
     * 保存修改后的企业信息
     * */
    protected void saveDate(){
// 初始化，只需要调用一次
        AssetsDatabaseManager.initManager(getApplication());
        // 获取管理对象，因为数据库需要通过管理对象才能够获取
        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
        // 通过管理对象获取数据库
        SQLiteDatabase db = mg.getDatabase("users.db");
        try {
            ContentValues values  = new ContentValues();
            values.put("BusinessId",getIntent().getStringExtra("BusinessId"));
            values.put("BusinessName",etCheckEnterprise01.getText().toString());
            values.put("OrgCode",etCheckEnterprise03.getText().toString());
            values.put("BusinessType",etCheckEnterprise04.getText().toString());
            values.put("LegalPerson",etCheckEnterprise06.getText().toString());
            values.put("LegalPersonPost",etCheckEnterprise07.getText().toString());
            values.put("LegalPersonPhone",etCheckEnterprise08.getText().toString());
            values.put("RegistrationNumber",etCheckEnterprise02.getText().toString());
            values.put("SafetyOfficer",etCheckEnterprise09.getText().toString());
            values.put("SafetyOfficerPhone",etCheckEnterprise10.getText().toString());
            values.put("Address",etCheckEnterprise05.getText().toString());
            values.put("ModifyIndex",modifyindex);
            values.put("ValidFlag",validflag);
            values.put("UserDefined","2");
            if(checkEnterprise04.getText().toString().equals("煤矿")){
                values.put("MineShaftCond",etCheckEnterprise11.getText().toString());
                values.put("MineShaftType",etCheckEnterprise12.getText().toString());
                values.put("GeologicalReserves",etCheckEnterprise13.getText().toString());
                values.put("WorkableReserves",etCheckEnterprise14.getText().toString());
                values.put("DesignProdCapacity",etCheckEnterprise15.getText().toString());
                values.put("CheckProdCapacity",etCheckEnterprise16.getText().toString());
                values.put("Area",etCheckEnterprise17.getText().toString());
                values.put("Wthdraw",etCheckEnterprise18.getText().toString());
                values.put("CoalType",etCheckEnterprise19.getText().toString());
                values.put("WorkableSeam",etCheckEnterprise20.getText().toString());
                values.put("ExploreCraft",etCheckEnterprise21.getText().toString());
                values.put("ContractPhone",etCheckEnterprise22.getText().toString());
                values.put("ExploreMode",etCheckEnterprise23.getText().toString());
                values.put("RiskAppraisal",etCheckEnterprise24.getText().toString());
                values.put("GasLevel",etCheckEnterprise25.getText().toString());
                values.put("CoalSeamLevel",etCheckEnterprise26.getText().toString());
                values.put("Explosion",etCheckEnterprise27.getText().toString());
                values.put("VentilateMode",etCheckEnterprise28.getText().toString());
            }
            db.replace("ELL_Business",null,values);
            Toast.makeText(this,"修改成功", Toast.LENGTH_SHORT).show();
            onBackPressed();
        }catch (Exception e){
            Log.e("保存企业信息数据报错",e.toString());
        }
    }

}
