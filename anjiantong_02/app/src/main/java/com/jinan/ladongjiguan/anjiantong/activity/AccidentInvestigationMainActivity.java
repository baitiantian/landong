package com.jinan.ladongjiguan.anjiantong.activity;


import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.jinan.ladongjiguan.anjiantong.PublicClass.AssetsDatabaseManager;
import com.jinan.ladongjiguan.anjiantong.PublicClass.CustomProgressDialog;
import com.jinan.ladongjiguan.anjiantong.utils.SharedPreferencesUtil;
import com.jinan.ladongjiguan.anjiantong.PublicClass.Utility;
import com.jinan.ladongjiguan.anjiantong.utils.WebServiceUtils;
import com.jinan.ladongjiguan.anjiantong.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.ksoap2.serialization.SoapObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AccidentInvestigationMainActivity extends BaseActivity implements View.OnTouchListener, View.OnClickListener {
    @BindView(R.id.examine_page_back)
    LinearLayout examinePageBack;
    @BindView(R.id.title_layout)
    TextView titleLayout;
    @BindView(R.id.e_car_num_6)
    Spinner eCarNum6;
    @BindView(R.id.bt_review)
    Button btReview;
    @BindView(R.id.list_01)
    ListView list01;
    @BindView(R.id.my_scrollView)
    ScrollView myScrollView;
    @BindView(R.id.top_btn)
    Button topBtn;
    private View contentView;
    private int scrollY = 0;// 标记上次滑动位置
    private View loadMoreView;
    private Button loadMoreButton;
    private Handler handler = new Handler();
    private int PageNum = 1;
    private String WEB_SERVER_URL;
    protected String AccidentType ="一般事故";//选择一般事故还是重大事故
    private CustomProgressDialog progressDialog = null;//加载页
    private List<Map<String, Object>> listItems = new ArrayList<>();//事故列表数据
    @Override
    protected void initView() {
        setContentView(R.layout.accident_investigation_main_layout);
        ButterKnife.bind(this);
        WEB_SERVER_URL = SharedPreferencesUtil.getStringData(this, "IPString", "");
        titleLayout.setText("事故调查");
        loadMoreView = getLayoutInflater().inflate(R.layout.loadmore, null);
        loadMoreButton = (Button)loadMoreView.findViewById(R.id.loadMoreButton);
        loadMoreButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                loadMoreButton.setClickable(false);
                PageNum++;
                loadMoreButton.setText("正在加载中...");   //设置按钮文字

                handler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        loadMoreButton.setClickable(true);

                        Utility.setListViewHeightBasedOnChildren(list01);
                        loadMoreButton.setText("查看更多...");  //恢复按钮文字
                    }
                },2000);

            }
        });
        list01.addFooterView(loadMoreView);    //设置列表底部视图
    }

    @Override
    protected void init() {
        examinePageBack.setOnClickListener(this);
        examinePageBack.setOnTouchListener(this);
        btReview.setOnClickListener(this);
        if (contentView == null) {
            contentView = myScrollView.getChildAt(0);
        }


        topBtn.setOnClickListener(this);


        /******************** 监听ScrollView滑动停止 *****************************/
        myScrollView.setOnTouchListener(new View.OnTouchListener() {
            private int lastY = 0;
            private int touchEventId = -9983761;
            Handler handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    View scroller = (View) msg.obj;
                    if (msg.what == touchEventId) {
                        if (lastY == scroller.getScrollY()) {
                            handleStop(scroller);
                        } else {
                            handler.sendMessageDelayed(handler.obtainMessage(
                                    touchEventId, scroller), 5);
                            lastY = scroller.getScrollY();
                        }
                    }
                }
            };

            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    handler.sendMessageDelayed(
                            handler.obtainMessage(touchEventId, v), 5);
                }
                return false;
            }

            /**
             * ScrollView 停止
             *
             * @param view
             */
            private void handleStop(Object view) {


                ScrollView scroller = (ScrollView) view;
                scrollY = scroller.getScrollY();

                doOnBorderListener();
            }
        });
        eCarNum6.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
               switch (position){
                   case 0:
                       AccidentType = "一般事故";
                       break;
                   case 1:
                       AccidentType = "重大事故";
                       break;
                   default:
                       break;
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
            case R.id.top_btn:
                myScrollView.post(new Runnable() {
                    @Override
                    public void run() {
//                        scrollView.fullScroll(ScrollView.FOCUS_DOWN);滚动到底部
//                        scrollView.fullScroll(ScrollView.FOCUS_UP);滚动到顶部
//
//                        需要注意的是，该方法不能直接被调用
//                        因为Android很多函数都是基于消息队列来同步，所以需要一部操作，
//                        addView完之后，不等于马上就会显示，而是在队列中等待处理，虽然很快，但是如果立即调用fullScroll， view可能还没有显示出来，所以会失败
//                                应该通过handler在新线程中更新
                        myScrollView.fullScroll(ScrollView.FOCUS_UP);
                    }
                });
                topBtn.setVisibility(View.GONE);
                break;
            case R.id.bt_review:
                HttpDate();
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
        finish();
        overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
    }
    /**
     * ScrollView 的顶部，底部判断：
     */
    private void doOnBorderListener() {
        // 底部判断
        if (contentView != null
                && contentView.getMeasuredHeight() <= myScrollView.getScrollY()
                + myScrollView.getHeight()) {
            topBtn.setVisibility(View.VISIBLE);

        }
        // 顶部判断
        else if (myScrollView.getScrollY() < 30) {
            topBtn.setVisibility(View.GONE);

        } else if (myScrollView.getScrollY() > 30) {
            topBtn.setVisibility(View.VISIBLE);

        }

    }
    /**
     * 获取数据
     * */
    private void HttpDate(){
        //加载页添加
        if (progressDialog == null) {
            progressDialog = CustomProgressDialog.createDialog(this);
        }
        progressDialog.show();

        //添加参数
        HashMap<String, String> properties = new HashMap<>();
        properties.put("Xml", "<Request><data  code='select-AccidentByType'><no><AccidentType>" +
                AccidentType+"</AccidentType><Page>" +
                PageNum+"</Page></no></data></Request>");
        properties.put("Token", "");
        Log.d("发送数据",""+properties);
        WebServiceUtils.callWebService(WEB_SERVER_URL, "DynamicInvoke", properties, new WebServiceUtils.WebServiceCallBack() {

            //WebService接口返回的数据回调到这个方法中
            @Override
            public void callBack(SoapObject result) {
                if (result != null) {
                    Log.d("返回数据", result.toString());
                    try {
                        listItems = new ArrayList<>();
//                                       Log.d("用户名发过去的结果",""+result);
                        Object detail = result.getProperty("DynamicInvokeResult");
                        JSONObject jsonObj;
                        jsonObj = XML.toJSONObject(detail.toString());
                        JSONObject obj;
                        JSONArray array;
                        // 初始化，只需要调用一次
                        AssetsDatabaseManager.initManager(getApplication());
                        // 获取管理对象，因为数据库需要通过管理对象才能够获取
                        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
                        // 通过管理对象获取数据库
                        SQLiteDatabase db = mg.getDatabase("users.db");
                        if (jsonObj.has("DocumentElement") && jsonObj.toString().substring(jsonObj.toString().length() - 3, jsonObj.toString().length() - 2).equals("}")) {
                            obj = jsonObj.getJSONObject("DocumentElement").getJSONObject("Table");
                            Map<String,Object> map = new HashMap<>();
                            map.put("filename",obj.getString("filename"));
                            map.put("filepath",obj.getString("filepath"));
                            map.put("accident_id",obj.getString("accident_id"));
                            map.put("accident_name",obj.getString("accident_name"));
                            map.put("accident_time",obj.getString("accident_time").substring(0,10));
                            map.put("accident_region",obj.getString("accident_region"));
                            map.put("accident_industry",obj.getString("accident_industry"));
                            map.put("accident_business",obj.getString("accident_business"));
                            map.put("mark",obj.getString("mark"));
                            map.put("addtime",obj.getString("addtime").substring(0,10));
                            listItems.add(map);
                        } else if (jsonObj.has("DocumentElement") && jsonObj.toString().substring(jsonObj.toString().length() - 3, jsonObj.toString().length() - 2).equals("]")) {
                            array = jsonObj.getJSONObject("DocumentElement").getJSONArray("Table");
                            for(int i = 0;i<array.length();i++){
                                obj = array.getJSONObject(i);
                                Map<String,Object> map = new HashMap<>();
                                map.put("filename",obj.getString("filename"));
                                map.put("filepath",obj.getString("filepath"));
                                map.put("accident_id",obj.getString("accident_id"));
                                map.put("accident_name",obj.getString("accident_name"));
                                map.put("accident_time",obj.getString("accident_time").substring(0,10));
                                map.put("accident_region",obj.getString("accident_region"));
                                map.put("accident_industry",obj.getString("accident_industry"));
                                map.put("accident_business",obj.getString("accident_business"));
                                map.put("mark",obj.getString("mark"));
                                map.put("addtime",obj.getString("addtime").substring(0,10));
                                listItems.add(map);
                            }


                        }

                        SimpleAdapter simpleAdapter = new SimpleAdapter(AccidentInvestigationMainActivity.this, listItems,
                                R.layout.accident_investigation_main_item,
                                new String[]{"accident_name", "accident_business", "accident_time", "mark"},
                                new int[]{R.id.tx_add_check_up_00, R.id.tx_add_check_up_01,
                                        R.id.tx_add_check_up_05, R.id.tx_add_check_up_06});
                        // 为ListView设置Adapter
                        list01.setAdapter(simpleAdapter);
                        Utility.setListViewHeightBasedOnChildren(list01);
                        list01.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Intent intent = new Intent();
                                intent.setClass(AccidentInvestigationMainActivity.this,AccidentInvestigationDateActivity.class);
                                intent.putExtra("filename",listItems.get(position).get("filename").toString());
                                intent.putExtra("filepath",listItems.get(position).get("filepath").toString());
                                intent.putExtra("accident_id",listItems.get(position).get("accident_id").toString());
                                intent.putExtra("accident_name",listItems.get(position).get("accident_name").toString());
                                intent.putExtra("accident_time",listItems.get(position).get("accident_time").toString());
                                intent.putExtra("accident_region",listItems.get(position).get("accident_region").toString());
                                intent.putExtra("accident_industry",listItems.get(position).get("accident_industry").toString());
                                intent.putExtra("accident_business",listItems.get(position).get("accident_business").toString());
                                intent.putExtra("mark",listItems.get(position).get("mark").toString());
                                intent.putExtra("addtime",listItems.get(position).get("addtime").toString());
                                startActivity(intent);
                            }
                        });
                        //关闭加载页
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                            progressDialog = null;
                        }

                    } catch (JSONException e) {
                        //关闭加载页
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                            progressDialog = null;
                        }
                        Toast.makeText(AccidentInvestigationMainActivity.this, "数据缺失", Toast.LENGTH_SHORT).show();
                        Log.e("JSON exception", e.getMessage());
                        e.printStackTrace();
                    }
                } else {
                    //关闭加载页
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                        progressDialog = null;
                    }
                    Toast.makeText(AccidentInvestigationMainActivity.this, "网络未响应，提交失败", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }
}
