package com.jinan.ladongjiguan.anjiantong.activity;

import android.content.ContentValues;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.jinan.ladongjiguan.anjiantong.PublicClass.AssetsDatabaseManager;
import com.jinan.ladongjiguan.anjiantong.R;

import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;


public class EnterpriseInformationAddActivity extends BaseActivity implements View.OnTouchListener, View.OnClickListener {
    @BindView(R.id.examine_page_back)
    LinearLayout examinePageBack;
    @BindView(R.id.title_layout)
    TextView titleLayout;
    @BindView(R.id.e_car_num_6)
    Spinner eCarNum6;
    @BindView(R.id.et_check_enterprise_01)
    EditText etCheckEnterprise01;
    @BindView(R.id.et_check_enterprise_02)
    EditText etCheckEnterprise02;
    @BindView(R.id.et_check_enterprise_03)
    EditText etCheckEnterprise03;
    @BindView(R.id.et_check_enterprise_05)
    EditText etCheckEnterprise05;
    @BindView(R.id.et_check_enterprise_06)
    EditText etCheckEnterprise06;
    @BindView(R.id.et_check_enterprise_07)
    EditText etCheckEnterprise07;
    @BindView(R.id.et_check_enterprise_08)
    EditText etCheckEnterprise08;
    @BindView(R.id.tv_enterprise_information_date_03)
    TextView tvEnterpriseInformationDate03;
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
    @BindView(R.id.l_coal_business)
    LinearLayout lCoalBusiness;
    @BindView(R.id.bt_enterprise_information_date_01)
    Button btEnterpriseInformationDate01;
    private String string = "";//下拉列表选择的
    @Override
    protected void initView() {
        setContentView(R.layout.enterprise_information_add_layout);
        ButterKnife.bind(this);
        titleLayout.setText("新添企业信息");
    }

    @Override
    protected void init() {
        btEnterpriseInformationDate01.setOnClickListener(this);
        examinePageBack.setOnClickListener(this);
        examinePageBack.setOnTouchListener(this);
        /**
         * 下拉列表
         * */
        Resources res = getResources();
        final String[] strings01 = res.getStringArray(R.array.s_car_num_10);
        eCarNum6.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView v1 = (TextView) view;
                v1.setTextColor(ContextCompat.getColor(EnterpriseInformationAddActivity.this, R.color.main_color_3));
                v1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                string = "";
                if (position != 0) {
                    string = strings01[position];
                    if(string.equals("煤矿")){
                        lCoalBusiness.setVisibility(View.VISIBLE);
                    }else {
                        lCoalBusiness.setVisibility(View.GONE);
                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.examine_page_back://返回键
                onBackPressed();
                break;
            case R.id.bt_enterprise_information_date_01://保存企业信息
                if(string.length()>0&&etCheckEnterprise01.getText().length()>0){
                    saveDate();
                }else {
                    Toast.makeText(this,"请完善内容", Toast.LENGTH_SHORT).show();
                }
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
     * 保存企业信息
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
            values.put("BusinessId", UUID.randomUUID().toString());
            values.put("BusinessName",etCheckEnterprise01.getText().toString());
            values.put("OrgCode",etCheckEnterprise03.getText().toString());
            values.put("BusinessType",string);
            values.put("LegalPerson",etCheckEnterprise06.getText().toString());
            values.put("LegalPersonPost",etCheckEnterprise07.getText().toString());
            values.put("LegalPersonPhone",etCheckEnterprise08.getText().toString());
            values.put("RegistrationNumber",etCheckEnterprise02.getText().toString());
            values.put("SafetyOfficer",etCheckEnterprise09.getText().toString());
            values.put("SafetyOfficerPhone",etCheckEnterprise10.getText().toString());
            values.put("Address",etCheckEnterprise05.getText().toString());
            values.put("ModifyIndex","0");
            values.put("ValidFlag","0");
            values.put("UserDefined","1");
            if(string.equals("煤矿")){
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
