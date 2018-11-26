package com.lunduimohao.landongjiguang.lunduimohao.activity;


import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.lunduimohao.landongjiguang.lunduimohao.R;


public class WheelDataActivity extends BaseActivity implements View.OnClickListener {
    private String string = "0";//左右轮
    private String s_wheel_style = "1";//车轮型号
    private String s_data = "0000000";
    @Override
    protected void initView() {
        setContentView(R.layout.wheel_data_layout);
        /**
         * 标题名称
         * */
        TextView title_layout = (TextView)findViewById(R.id.title_layout);
        title_layout.setText("设置监测对象");
        /**
         * 返回键
         * */
        findViewById(R.id.examine_page_back).setOnClickListener(this);
        /**
         * 确定按钮
         * */
        findViewById(R.id.bt_car_num_save).setOnClickListener(this);
        /**
         * 左右轮下拉菜单
         * */
        Spinner e_car_num_4 = (Spinner)findViewById(R.id.e_car_num_4);
        e_car_num_4.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                string = position+"";
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        if(getIntent().getStringExtra("wheel_date").length()>0&&getIntent().getStringExtra("wheel_date").subSequence(6, 7).equals("0")){
            e_car_num_4.setSelection(0,true);
        }else if(getIntent().getStringExtra("wheel_date").length()>0){
            e_car_num_4.setSelection(1,true);
        }

        /**
         * 车轮型号
         * */
        Spinner e_car_num_5 = (Spinner)findViewById(R.id.e_car_num_5);
        e_car_num_5.setSelection(0,true);
        e_car_num_5.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                s_wheel_style = (position+1)+"";
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        if(getIntent().getStringExtra("wheel_date").length()>0){
            e_car_num_5.setSelection(Integer.parseInt(getIntent().getStringExtra("s_wheel_style"))-1,true);
        }
        if(getIntent().getStringExtra("wheel_date").length()>0){
            EditText i_car_num_1 = (EditText) findViewById(R.id.e_car_num_1);
            EditText i_car_num_2 = (EditText) findViewById(R.id.e_car_num_2);
            EditText i_car_num_3 = (EditText) findViewById(R.id.e_car_num_3);
            i_car_num_1.setText(getIntent().getStringExtra("wheel_date").subSequence(0, 3));
            i_car_num_2.setText(getIntent().getStringExtra("wheel_date").subSequence(3, 5));
            i_car_num_3.setText(getIntent().getStringExtra("wheel_date").subSequence(5, 6));
        }

    }

    @Override
    protected void init() {

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("date", "");
        setResult(0, intent);
        finish();
        overridePendingTransition(0, R.anim.zoomout);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.examine_page_back:
                onBackPressed();
                break;
            case R.id.bt_car_num_save:

                EditText e_car_num_1 = (EditText)findViewById(R.id.e_car_num_1);
                EditText e_car_num_2 = (EditText)findViewById(R.id.e_car_num_2);
                EditText e_car_num_3 = (EditText)findViewById(R.id.e_car_num_3);

//                s_date = e_car_num_1.getText().toString() +
//                        e_car_num_2.getText().toString() +
//                        e_car_num_3.getText().toString() + string;
                if(s_data.length()==7){
                    Intent intent = new Intent();
                    intent.putExtra("data", s_data);
                    intent.putExtra("wheel_style",s_wheel_style);
                    setResult(MainActivity.RESULT_OK, intent);
                    finish();
                    overridePendingTransition(0, R.anim.zoomout);
                }else {
                    Toast.makeText(this, "请输入完整信息", Toast.LENGTH_SHORT)
                            .show();
                }
            default:
                break;
        }
    }
}
