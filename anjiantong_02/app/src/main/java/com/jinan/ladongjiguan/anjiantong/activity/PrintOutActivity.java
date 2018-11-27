package com.jinan.ladongjiguan.anjiantong.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.jinan.ladongjiguan.anjiantong.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;


public class PrintOutActivity extends BaseActivity implements View.OnClickListener, View.OnTouchListener {
    @BindView(R.id.examine_page_back)
    LinearLayout examinePageBack;
    @BindView(R.id.title_layout)
    TextView titleLayout;
    @BindView(R.id.delete_layout)
    LinearLayout deleteLayout;
    @BindView(R.id.gridView2)
    GridView gridView2;
    @BindView(R.id.bt_print_out_01)
    LinearLayout btPrintOut01;
    @BindView(R.id.bt_print_out_02)
    LinearLayout btPrintOut02;
    @BindView(R.id.bt_print_out_03)
    LinearLayout btPrintOut03;
    @BindView(R.id.bt_print_out_04)
    LinearLayout btPrintOut04;
    @BindView(R.id.bt_print_out_05)
    LinearLayout btPrintOut05;
    private List<Map<String, Object>> data_list;
    // 图片封装为一个数组
    private int[] icon = {R.drawable.img_print_out_01,
            R.drawable.img_print_out_03};
    private String[] iconName = {"现场检查记录", "限期整改" + "\n" + "指令书"};

    @Override
    protected void initView() {
        setContentView(R.layout.print_out_layout);
        ButterKnife.bind(this);
        titleLayout.setText("制作文书");
    }

    @Override
    protected void init() {
        btPrintOut01.setOnClickListener(this);
        btPrintOut02.setOnClickListener(this);
        btPrintOut03.setOnClickListener(this);
        btPrintOut04.setOnClickListener(this);
        btPrintOut05.setOnClickListener(this);
        //新建List
        data_list = new ArrayList<>();
        //获取数据
        getData();
        //新建适配器
        String[] from = {"image", "text"};
        int[] to = {R.id.image, R.id.text};
        SimpleAdapter sim_adapter = new SimpleAdapter(this, data_list, R.layout.print_out_grid_item, from, to);
        gridView2.setAdapter(sim_adapter);
        examinePageBack.setOnClickListener(this);
        examinePageBack.setOnTouchListener(this);
        gridView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                itemClick(position);
            }
        });

    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.examine_page_back://返回键
                finish();
                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                break;
            case R.id.bt_print_out_01://现场检查记录
                intent.putExtra("PlanId", getIntent().getStringExtra("PlanId"));
                intent.putExtra("CheckId", getIntent().getStringExtra("CheckId"));
                intent.putExtra("Problems", getIntent().getStringExtra("Problems"));
                intent.setClass(this, PrintOut01Activity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                break;
            case R.id.bt_print_out_02://限期整改指令
                intent.putExtra("PlanId", getIntent().getStringExtra("PlanId"));
                intent.putExtra("CheckId", getIntent().getStringExtra("CheckId"));
                intent.putExtra("Problems", getIntent().getStringExtra("Problems"));
                intent.putExtra("Problems_1", getIntent().getStringExtra("Problems_1"));
                intent.putExtra("rectify", getIntent().getStringExtra("rectify"));
                intent.putExtra("DayNum", getIntent().getStringExtra("DayNum"));
                intent.putExtra("BusinessType", getIntent().getStringExtra("BusinessType"));
                intent.setClass(this, PrintOut02Activity.class);
//                Log.d("数据BusinessType", getIntent().getStringExtra("BusinessType")+getIntent().getStringExtra("DayNum"));
                startActivity(intent);
                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                break;
            case R.id.bt_print_out_03://单位行政处罚
                intent.putExtra("PlanId", getIntent().getStringExtra("PlanId"));
                intent.putExtra("CheckId", getIntent().getStringExtra("CheckId"));
                intent.putExtra("Problems", getIntent().getStringExtra("Problems"));
                intent.setClass(this, PrintOut04Activity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                break;
            case R.id.bt_print_out_04://个人行政处罚
                intent.putExtra("PlanId", getIntent().getStringExtra("PlanId"));
                intent.putExtra("CheckId", getIntent().getStringExtra("CheckId"));
                intent.putExtra("Problems", getIntent().getStringExtra("Problems"));
                intent.setClass(this, PrintOut06Activity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                break;
            case R.id.bt_print_out_05://现场处理措施
                intent.putExtra("PlanId", getIntent().getStringExtra("PlanId"));
                intent.putExtra("CheckId", getIntent().getStringExtra("CheckId"));
                intent.putExtra("Problems", getIntent().getStringExtra("Problems"));
                intent.putExtra("Problems_1", getIntent().getStringExtra("Problems_1"));
                intent.putExtra("rectify", getIntent().getStringExtra("rectify"));
                intent.putExtra("DayNum", getIntent().getStringExtra("DayNum"));
                intent.putExtra("BusinessType", getIntent().getStringExtra("BusinessType"));
                intent.setClass(this, PrintOut09Activity.class);
//                Log.d("数据BusinessType", getIntent().getStringExtra("BusinessType")+getIntent().getStringExtra("DayNum"));
                startActivity(intent);
                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
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

    public List<Map<String, Object>> getData() {
        //cion和iconName的长度是相同的，这里任选其一都可以
        for (int i = 0; i < icon.length; i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("image", icon[i]);
            map.put("text", iconName[i]);
            data_list.add(map);
        }
        return data_list;
    }

    protected void itemClick(int i) {
        Intent intent = new Intent();
        switch (i) {
            case 0://现场检查记录
                intent.putExtra("PlanId", getIntent().getStringExtra("PlanId"));
                intent.putExtra("CheckId", getIntent().getStringExtra("CheckId"));
                intent.putExtra("Problems", getIntent().getStringExtra("Problems"));
                intent.setClass(this, PrintOut01Activity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                break;
            case 1://

                intent.putExtra("PlanId", getIntent().getStringExtra("PlanId"));
                intent.putExtra("CheckId", getIntent().getStringExtra("CheckId"));
                intent.putExtra("Problems", getIntent().getStringExtra("Problems"));
                intent.putExtra("rectify", getIntent().getStringExtra("rectify"));
                intent.putExtra("DayNum", getIntent().getStringExtra("DayNum"));
                intent.setClass(this, PrintOut02Activity.class);
                startActivity(intent);

                break;
            case 2:
                intent.putExtra("PlanId", getIntent().getStringExtra("PlanId"));
                intent.putExtra("CheckId", getIntent().getStringExtra("CheckId"));
                intent.putExtra("Problems", getIntent().getStringExtra("Problems"));
                intent.setClass(this, PrintOut03Activity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                break;
            case 3://单位行政处罚告知书
                intent.putExtra("PlanId", getIntent().getStringExtra("PlanId"));
                intent.putExtra("CheckId", getIntent().getStringExtra("CheckId"));
                intent.putExtra("Problems", getIntent().getStringExtra("Problems"));
                intent.setClass(this, PrintOut04Activity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                break;
            case 4://单位行政处罚决定书
                intent.putExtra("PlanId", getIntent().getStringExtra("PlanId"));
                intent.putExtra("CheckId", getIntent().getStringExtra("CheckId"));
                intent.putExtra("Problems", getIntent().getStringExtra("Problems"));
                intent.setClass(this, PrintOut04Activity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                break;
            case 5://个人行政处罚告知书
                intent.putExtra("PlanId", getIntent().getStringExtra("PlanId"));
                intent.putExtra("CheckId", getIntent().getStringExtra("CheckId"));
                intent.putExtra("Problems", getIntent().getStringExtra("Problems"));
                intent.setClass(this, PrintOut06Activity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                break;
            case 6://个人行政处罚决定书
                intent.putExtra("PlanId", getIntent().getStringExtra("PlanId"));
                intent.putExtra("CheckId", getIntent().getStringExtra("CheckId"));
                intent.putExtra("Problems", getIntent().getStringExtra("Problems"));
                intent.setClass(this, PrintOut06Activity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                break;
            case 7:
                break;
            default:
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}
