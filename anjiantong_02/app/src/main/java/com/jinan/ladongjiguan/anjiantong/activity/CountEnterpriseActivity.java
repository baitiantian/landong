package com.jinan.ladongjiguan.anjiantong.activity;


import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.jinan.ladongjiguan.anjiantong.PublicClass.AssetsDatabaseManager;
import com.jinan.ladongjiguan.anjiantong.PublicClass.CustomProgressDialog;
import com.jinan.ladongjiguan.anjiantong.PublicClass.DoubleDatePickerDialog;
import com.jinan.ladongjiguan.anjiantong.utils.SharedPreferencesUtil;
import com.jinan.ladongjiguan.anjiantong.PublicClass.StringOrDate;
import com.jinan.ladongjiguan.anjiantong.PublicClass.Utility;
import com.jinan.ladongjiguan.anjiantong.utils.WebServiceUtils;
import com.jinan.ladongjiguan.anjiantong.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.ksoap2.serialization.SoapObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CountEnterpriseActivity extends BaseActivity implements View.OnClickListener, View.OnTouchListener {
    @BindView(R.id.examine_page_back)
    LinearLayout examinePageBack;
    @BindView(R.id.title_layout)
    TextView titleLayout;
    @BindView(R.id.check_enterprise_01)
    TextView checkEnterprise01;
    @BindView(R.id.negativeButton)
    Button negativeButton;
    @BindView(R.id.l_personnel)
    LinearLayout lPersonnel;
    @BindView(R.id.hidden_danger_num)
    TextView hiddenDangerNum;
    @BindView(R.id.hadden_danger_lv)
    ListView haddenDangerLv;
    @BindView(R.id.from_time)
    TextView fromTime;
    @BindView(R.id.end_time)
    TextView endTime;
    @BindView(R.id.l_search_time)
    LinearLayout lSearchTime;
    @BindView(R.id.my_scrollView)
    ScrollView myScrollView;
    @BindView(R.id.top_btn)
    Button topBtn;
    private String s_from_time = "1900-01-01";//开始时间
    private Date d_from_time;
    private String s_end_time = "9999-01-01";//结束时间
    private Date d_end_time;
    private String WEB_SERVER_URL;
    private CustomProgressDialog progressDialog = null;//加载页
    private List<Map<String, Object>> listItems_2 = new ArrayList<>();//隐患信息集合
    private String message = SharedPreferencesUtil.getStringData(this, "userId", "");
    private List<Map<String, Object>> listItems = new ArrayList<>();
    private List<Map<String, Object>> listItems1 = new ArrayList<>();
    private List<Map<String, Object>> listItems2 = new ArrayList<>();
    private String BusinessId = "";//企业ID
    private String hiddenNum = "0";
    private String planNum = "0";
    private View contentView;
    private int scrollY = 0;// 标记上次滑动位置
    private View loadMoreView;
    private Button loadMoreButton;
    private Handler handler = new Handler();
    private int PageNum = 1;

    @Override
    protected void initView() {
        setContentView(R.layout.count_enterprise_layout);
        ButterKnife.bind(this);

        switch (getIntent().getStringExtra("state")) {
            case "1":
                titleLayout.setText("企业隐患统计");
                break;
            case "2":
                titleLayout.setText("企业计划统计");
                break;
            case "3"://企业信息库进入后查询
                titleLayout.setText("企业隐患统计");
                checkEnterprise01.setText(getIntent().getStringExtra("BusinessName"));
                date(getIntent().getStringExtra("BusinessId"));
                BusinessId = getIntent().getStringExtra("BusinessId");
                checkEnterprise01.setClickable(false);
                break;
            default:
                break;
        }
        WEB_SERVER_URL = SharedPreferencesUtil.getStringData(this, "IPString", "");
        loadMoreView = getLayoutInflater().inflate(R.layout.loadmore, null);
        loadMoreButton = (Button) loadMoreView.findViewById(R.id.loadMoreButton);
        loadMoreButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                loadMoreButton.setClickable(false);
                PageNum++;
                loadMoreButton.setText("正在加载中...");   //设置按钮文字
                switch (getIntent().getStringExtra("state")) {
                    case "1":
                        findHiddenDanger();
                        break;
                    case "2":
                        findPlan();
                        break;
                    case "3"://企业信息入口
                        findHiddenDanger();
                        break;
                    default:
                        break;
                }
                handler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        loadMoreButton.setClickable(true);
                        HiddenDangerAdapter().notifyDataSetChanged();
                        Utility.setListViewHeightBasedOnChildren(haddenDangerLv);
                        loadMoreButton.setText("查看更多...");  //恢复按钮文字
                    }
                }, 2000);

            }
        });
        haddenDangerLv.addFooterView(loadMoreView);    //设置列表底部视图
    }

    @Override
    protected void init() {
        examinePageBack.setOnClickListener(this);
        examinePageBack.setOnTouchListener(this);
        negativeButton.setOnClickListener(this);
        checkEnterprise01.setOnClickListener(this);
        checkEnterprise01.setOnTouchListener(this);
        lSearchTime.setOnClickListener(this);
        String s = "";
        switch (getIntent().getStringExtra("state")) {
            case "1":
                s = "执行计划 " + planNum + "次" + "  " + "检查隐患 " + hiddenNum + "条";
                hiddenDangerNum.setText(s);
                break;
            case "2":
                s = "执行计划 " + planNum + "次" + "  " + "检查隐患 " + hiddenNum + "条";
                hiddenDangerNum.setText(s);
                negativeButton.setText("计划查询");
                break;
            default:
                break;
        }
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
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.examine_page_back://返回键
                onBackPressed();
                break;
            case R.id.negativeButton:
                listItems_2 = new ArrayList<>();
                PageNum = 1;
                if (BusinessId.length() > 0) {
                    switch (getIntent().getStringExtra("state")) {
                        case "1":
                            findHiddenDanger();
                            break;
                        case "2":
                            findPlan();
                            break;
                        case "3"://企业信息入口
                            findHiddenDanger();
                            break;
                        default:
                            break;
                    }

                } else {
                    Toast.makeText(CountEnterpriseActivity.this, "请先选择企业", Toast.LENGTH_SHORT).show();
                }

                break;

            case R.id.l_search_time://时间选择器
                Calendar c = Calendar.getInstance();
                // 最后一个false表示不显示日期，如果要显示日期，最后参数可以是true或者不用输入
                new DoubleDatePickerDialog(CountEnterpriseActivity.this, 0, new DoubleDatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker startDatePicker, int startYear, int startMonthOfYear,
                                          int startDayOfMonth, DatePicker endDatePicker, int endYear, int endMonthOfYear,
                                          int endDayOfMonth) {

                        if (startYear > endYear) {
                            Toast.makeText(CountEnterpriseActivity.this, "开始日期不能晚于结束日期", Toast.LENGTH_LONG).show();
                            return;
                        } else if (startYear == endYear) {
                            if (startMonthOfYear > endMonthOfYear) {
                                Toast.makeText(CountEnterpriseActivity.this, "开始日期不能晚于结束日期", Toast.LENGTH_LONG).show();
                                return;
                            } else if (startMonthOfYear == endMonthOfYear) {
                                if (startDayOfMonth > endDayOfMonth) {
                                    Toast.makeText(CountEnterpriseActivity.this, "开始日期不能晚于结束日期", Toast.LENGTH_LONG).show();
                                    return;
                                } else {
                                    s_from_time = String.format("%d-%d-%d", startYear, startMonthOfYear + 1, startDayOfMonth);
                                    d_from_time = StringOrDate.stringToDate(s_from_time);
                                    fromTime.setText(s_from_time);
                                    s_end_time = String.format("%d-%d-%d", endYear, endMonthOfYear + 1, endDayOfMonth);
                                    d_end_time = StringOrDate.stringToDate(s_end_time);
                                    endTime.setText(s_end_time);
                                }
                            } else {
                                s_from_time = String.format("%d-%d-%d", startYear, startMonthOfYear + 1, startDayOfMonth);
                                d_from_time = StringOrDate.stringToDate(s_from_time);
                                fromTime.setText(s_from_time);
                                s_end_time = String.format("%d-%d-%d", endYear, endMonthOfYear + 1, endDayOfMonth);
                                d_end_time = StringOrDate.stringToDate(s_end_time);
                                endTime.setText(s_end_time);
                            }

                        } else {
                            s_from_time = String.format("%d-%d-%d", startYear, startMonthOfYear + 1, startDayOfMonth);
                            d_from_time = StringOrDate.stringToDate(s_from_time);
                            fromTime.setText(s_from_time);
                            s_end_time = String.format("%d-%d-%d", endYear, endMonthOfYear + 1, endDayOfMonth);
                            d_end_time = StringOrDate.stringToDate(s_end_time);
                            endTime.setText(s_end_time);
                        }

                    }
                }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE), true).show();
                break;
            case R.id.check_enterprise_01://选择企业
                Intent intent = new Intent();
                intent.setClass(CountEnterpriseActivity.this, EnterpriseInformationActivity.class);
                intent.putExtra("state", "check");
                startActivityForResult(intent, 0);
                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
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
     * 返回企业信息
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 0:
                if (resultCode == 0) {
                    checkEnterprise01.setText(data.getStringExtra("name"));
                    date(data.getStringExtra("BusinessId"));
                    BusinessId = data.getStringExtra("BusinessId");
                }
                break;

            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
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
            Cursor c = db.rawQuery("SELECT* FROM ELL_Business WHERE BusinessId = ?", new String[]{s});
            c.moveToFirst();
            // 填入数据
            String s1 = c.getString(c.getColumnIndex("BusinessName"));
            checkEnterprise01.setText(s1);
            c.close();
        } catch (Exception e) {
            Log.e("数据库报错", e.toString());
        }
    }

    /**
     * 根据企业查找隐患
     */
    protected void findHiddenDanger() {
        //加载页添加
        if (progressDialog == null) {
            progressDialog = CustomProgressDialog.createDialog(this);
        }
        progressDialog.show();

        //添加参数
        HashMap<String, String> properties = new HashMap<>();
        properties.put("Xml", "<Request><data  code='select-HiddenInfoByBuinessid'><no><Businessid>" +
                BusinessId + "</Businessid><StartTime>" +
                s_from_time + "</StartTime><EndTime>" +
                s_end_time + "</EndTime><Page>" +
                PageNum + "</Page></no></data></Request>");
//        properties.put("Xml", "<Request><data  code='select-HiddenInfoByBuinessid'><no><Businessid>" +
//                BusinessId+"</Businessid></no></data></Request>");
        properties.put("Token", "");
        Log.d("发送数据", properties.toString());
        WebServiceUtils.callWebService(WEB_SERVER_URL, "DynamicInvoke", properties, new WebServiceUtils.WebServiceCallBack() {

            //WebService接口返回的数据回调到这个方法中
            @Override
            public void callBack(SoapObject result) {
                if (result != null) {
                    Log.d("返回数据", result.toString());
                    try {
//                                       Log.d("用户名发过去的结果",""+result);
                        Object detail = result.getProperty("DynamicInvokeResult");
                        JSONObject jsonObj;
                        jsonObj = XML.toJSONObject(detail.toString());
//                        System.out.println("===========================" + jsonObj);
                        JSONArray array;
                        JSONObject obj;
//                        listItems_2 = new ArrayList<>();

                        // 初始化，只需要调用一次
                        AssetsDatabaseManager.initManager(getApplication());
                        // 获取管理对象，因为数据库需要通过管理对象才能够获取
                        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
                        // 通过管理对象获取数据库
                        SQLiteDatabase db = mg.getDatabase("users.db");
//                                        Log.d("秘钥",jsonObj.getJSONObject("DocumentElement").getJSONObject("Table").getString("secretkey"));
                        if (jsonObj.has("DocumentElement") && jsonObj.toString().substring(jsonObj.toString().length() - 3, jsonObj.toString().length() - 2).equals("}")) {
                            obj = jsonObj.getJSONObject("DocumentElement").getJSONObject("Table");
                            Map<String, Object> listItem = new HashMap<>();
//                            listItem.put("checktype", obj.getString("checktype"));
                            String s = obj.getString("yhms") + "(" + obj.getString("disposeresult") + ")";
                            listItem.put("YHMS", s);
                            Cursor c = db.rawQuery("SELECT* FROM ELL_Business WHERE BusinessId = ?",
                                    new String[]{obj.getString("businessid")});
                            c.moveToFirst();
                            listItem.put("hiddendangerid", c.getString(c.getColumnIndex("BusinessName")));
                            listItem.put("id", obj.getString("hiddendangerid"));
                            c.close();
                            if (obj.has("zgqx")) {
                                listItem.put("ZGQX", obj.getString("zgqx").substring(0, 10));
                            } else {
                                listItem.put("ZGQX", "");
                            }
                            if (obj.has("documentname")) {

                                listItem.put("documentname", obj.getString("documentname"));
                            } else {
                                listItem.put("documentname", "");
                            }
                            if (obj.has("addtime")) {
                                listItem.put("addtime", obj.getString("addtime").substring(0, 10));
                            } else {
                                listItem.put("addtime", "");
                            }
                            if (obj.has("plannumber")) {
                                planNum = obj.getString("plannumber");
                            }
                            if (obj.has("hiddendangernumber")) {
                                hiddenNum = obj.getString("hiddendangernumber");
                            }

                            listItems_2.add(listItem);

                        } else if (jsonObj.has("DocumentElement") && jsonObj.toString().substring(jsonObj.toString().length() - 3, jsonObj.toString().length() - 2).equals("]")) {
                            array = jsonObj.getJSONObject("DocumentElement").getJSONArray("Table");
                            listItems_2 = new ArrayList<>();
                            for (int i = 0; i < array.length(); i++) {
                                obj = array.getJSONObject(i);
                                Map<String, Object> listItem = new HashMap<>();
//                                listItem.put("checktype", obj.getString("checktype"));
                                String s = obj.getString("yhms") + "(" + obj.getString("disposeresult") + ")";
                                listItem.put("YHMS", s);
                                Cursor c = db.rawQuery("SELECT* FROM ELL_Business WHERE BusinessId = ?",
                                        new String[]{obj.getString("businessid")});
                                c.moveToFirst();
                                listItem.put("hiddendangerid", c.getString(c.getColumnIndex("BusinessName")));
                                listItem.put("id", obj.getString("hiddendangerid"));
                                c.close();
                                if (obj.has("zgqx")) {
                                    listItem.put("ZGQX", obj.getString("zgqx").substring(0, 10));
                                } else {
                                    listItem.put("ZGQX", "");
                                }
                                if (obj.has("documentname")) {

                                    listItem.put("documentname", obj.getString("documentname"));
                                } else {
                                    listItem.put("documentname", "");
                                }
                                if (obj.has("addtime")) {
                                    listItem.put("addtime", obj.getString("addtime").substring(0, 10));
                                } else {
                                    listItem.put("addtime", "");
                                }
                                if (obj.has("plannumber")) {
                                    planNum = obj.getString("plannumber");
                                }
                                if (obj.has("hiddendangernumber")) {
                                    hiddenNum = obj.getString("hiddendangernumber");
                                }
                                listItems_2.add(listItem);
                            }

                        }else {
                            planNum="0";
                            hiddenNum="0";

                        }
                        if (PageNum == 1) {
                            String s;
                            switch (getIntent().getStringExtra("state")) {
                                case "1":
                                    s = "执行计划 " + planNum + "次" + "  " + "检查隐患 " + hiddenNum + "条";
                                    hiddenDangerNum.setText(s);
                                    break;
                                case "2":
                                    s = "执行计划 " + planNum + "次" + "  " + "检查隐患 " + hiddenNum + "条";
                                    hiddenDangerNum.setText(s);
                                    negativeButton.setText("计划查询");
                                    break;
                                default:
                                    break;
                            }
                            haddenDangerLv.setAdapter(HiddenDangerAdapter());
                            Utility.setListViewHeightBasedOnChildren(haddenDangerLv);
                        }
                        init();
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
                        Toast.makeText(CountEnterpriseActivity.this, "提交失败", Toast.LENGTH_SHORT).show();
                        Log.e("JSON exception", e.getMessage());
                        e.printStackTrace();
                    }
                } else {
                    //关闭加载页
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                        progressDialog = null;
                    }
                    Toast.makeText(CountEnterpriseActivity.this, "网络未响应，提交失败", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    @NonNull
    private BaseAdapter HiddenDangerAdapter() {
        return new BaseAdapter() {
            @Override
            public int getCount() {
                if (listItems_2 != null) {
                    return listItems_2.size();
                }
                return 0;
            }

            @Override
            public Object getItem(int position) {
                return listItems_2.get(position);
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {
                ViewHolder holder;
                if (convertView == null) { // 如果为空，就表示是第一次加载，还没有加入到缓存中
                    holder = new ViewHolder();
                    convertView = View.inflate(CountEnterpriseActivity.this, R.layout.hidden_danger_item, null);
//                    holder.type = (TextView) convertView.findViewById(R.id.type_tv);
                    holder.yhms = (TextView) convertView.findViewById(R.id.hiddenDanger_tv);
                    holder.hiddenDanger_tv = (TextView) convertView.findViewById(R.id.type_tv);
                    holder.zgqx = (TextView) convertView.findViewById(R.id.zgqx_tv);
                    holder.jcsj = (TextView) convertView.findViewById(R.id.jcsj_tv);
                    holder.ssws = (TextView) convertView.findViewById(R.id.ssws_tv);
                    convertView.setTag(holder); // 加入缓存
                } else {
                    holder = (ViewHolder) convertView.getTag(); // 如果ConvertView不为空，则表示在缓存中
                }
                holder.jcsj.setText(listItems_2.get(position).get("addtime").toString());
                holder.ssws.setText(listItems_2.get(position).get("documentname").toString());
                holder.zgqx.setText(listItems_2.get(position).get("ZGQX").toString());
                holder.yhms.setText(listItems_2.get(position).get("YHMS").toString());
                holder.hiddenDanger_tv.setText(listItems_2.get(position).get("hiddendangerid").toString());
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(CountEnterpriseActivity.this, HiddenDangerActivity.class);
                        intent.putExtra("id", listItems_2.get(position).get("id").toString());
                        intent.putExtra("state", "1");
                        startActivity(intent);
                    }
                });
                return convertView;
            }
        };
    }

    class ViewHolder {
        private TextView zgqx;
        private TextView yhms;
        private TextView hiddenDanger_tv;
        private TextView ssws;
        private TextView jcsj;
    }

    /**
     * 按照企业查找计划
     */
    protected void findPlan() {
        //加载页添加
        if (progressDialog == null) {
            progressDialog = CustomProgressDialog.createDialog(this);
        }
        progressDialog.show();

        //添加参数
        HashMap<String, String> properties = new HashMap<>();
        properties.put("Xml", "<Request><data  code='1select-HiddenInfoByCompanyID'><no><Businessid>" +
                BusinessId + "</Businessid><StartTime>" +
                s_from_time + "</StartTime><EndTime>" +
                s_end_time + "</EndTime><Page>" +
                PageNum + "</Page></no></data></Request>");
//        properties.put("Xml", "<Request><data  code='select-departhiddendanger'><no><DepartmentCode>" +
//                Department + "</DepartmentCode></no></data></Request>");
//        properties.put("Token", "");
        Log.d("发送数据", properties.toString());
        WebServiceUtils.callWebService(WEB_SERVER_URL, "DynamicInvoke", properties, new WebServiceUtils.WebServiceCallBack() {

            //WebService接口返回的数据回调到这个方法中
            @Override
            public void callBack(SoapObject result) {
                if (result != null) {
                    Log.d("返回数据", result.toString());
                    try {
//                                       Log.d("用户名发过去的结果",""+result);
                        Object detail = result.getProperty("DynamicInvokeResult");
                        JSONObject jsonObj;
                        jsonObj = XML.toJSONObject(detail.toString());
//                        System.out.println("===========================" + jsonObj);
                        JSONArray array;
                        JSONObject obj;
//                        listItems_2 = new ArrayList<>();

                        // 初始化，只需要调用一次
                        AssetsDatabaseManager.initManager(getApplication());
                        // 获取管理对象，因为数据库需要通过管理对象才能够获取
                        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
                        // 通过管理对象获取数据库
                        SQLiteDatabase db = mg.getDatabase("users.db");
//                                        Log.d("秘钥",jsonObj.getJSONObject("DocumentElement").getJSONObject("Table").getString("secretkey"));
                        if (jsonObj.has("DocumentElement") && jsonObj.toString().substring(jsonObj.toString().length() - 3, jsonObj.toString().length() - 2).equals("}")) {
                            obj = jsonObj.getJSONObject("DocumentElement").getJSONObject("Table");
                            Map<String, Object> listItem = new HashMap<>();
                            // 查找企业名称
                            Cursor cursor = db.rawQuery("SELECT* FROM ELL_Business WHERE BusinessId = ?",
                                    new String[]{obj.getString("businessid")});
                            cursor.moveToFirst();
                            listItem.put("Business", cursor.getString(cursor.getColumnIndex("BusinessName")));
                            listItem.put("CheckType", obj.getString("checktype"));
                            listItem.put("StatTime", obj.getString("stattime").substring(0, 10));
                            listItem.put("EndTime", obj.getString("endtime").substring(0, 10));
                            listItem.put("PlanId", obj.getString("planid"));
                            listItem.put("Address", obj.getString("address"));
                            listItem.put("businessid", obj.getString("businessid"));
                            listItem.put("enforceaccording", obj.getString("enforceaccording"));
                            listItem.put("planchecktype", obj.getString("checktype"));
                            listItem.put("companyname", obj.getString("companyname"));
                            if (obj.has("plantotelnumber")) {
                                planNum = obj.getString("plantotelnumber");
                            }
                            if (obj.has("hiddendangernumber")) {
                                hiddenNum = obj.getString("hiddendangernumber");
                            }
                            listItems_2.add(listItem);

                            cursor.close();
                        } else if (jsonObj.has("DocumentElement") && jsonObj.toString().substring(jsonObj.toString().length() - 3, jsonObj.toString().length() - 2).equals("]")) {
                            array = jsonObj.getJSONObject("DocumentElement").getJSONArray("Table");
//                            listItems_2 = new ArrayList<>();
                            for (int i = 0; i < array.length(); i++) {
                                obj = array.getJSONObject(i);
                                Map<String, Object> listItem = new HashMap<>();
                                // 查找企业名称
                                Cursor cursor = db.rawQuery("SELECT* FROM ELL_Business WHERE BusinessId = ?",
                                        new String[]{obj.getString("businessid")});
                                cursor.moveToFirst();
                                listItem.put("Business", cursor.getString(cursor.getColumnIndex("BusinessName")));
                                listItem.put("CheckType", obj.getString("checktype"));
                                listItem.put("StatTime", obj.getString("stattime").substring(0, 10));
                                listItem.put("EndTime", obj.getString("endtime").substring(0, 10));
                                listItem.put("PlanId", obj.getString("planid"));
                                listItem.put("Address", obj.getString("address"));
                                listItem.put("businessid", obj.getString("businessid"));
                                listItem.put("enforceaccording", obj.getString("enforceaccording"));
                                listItem.put("planchecktype", obj.getString("checktype"));
                                listItem.put("companyname", obj.getString("companyname"));
                                listItems_2.add(listItem);
                                if (obj.has("plantotelnumber")) {
                                    planNum = obj.getString("plantotelnumber");
                                }
                                if (obj.has("hiddendangernumber")) {
                                    hiddenNum = obj.getString("hiddendangernumber");
                                }
                                cursor.close();
                            }

                        }else {
                            planNum="0";
                            hiddenNum="0";

                        }
                        init();
                        // 创建一个SimpleAdapter
                        SimpleAdapter simpleAdapter = new SimpleAdapter(CountEnterpriseActivity.this, listItems_2,
                                R.layout.check_up_list_02_item,
                                new String[]{"Business", "CheckType", "StatTime", "EndTime", "Address"},
                                new int[]{R.id.tx_add_check_up_01, R.id.tx_add_check_up_02,
                                        R.id.tx_add_check_up_04, R.id.tx_add_check_up_05, R.id.tx_add_check_up_03});
                        // 为ListView设置Adapter
                        haddenDangerLv.setAdapter(simpleAdapter);
                        if(PageNum == 1){

                            String s = "执行计划 " + planNum + "次" + "  " + "检查隐患 " + hiddenNum + "条";
                            hiddenDangerNum.setText(s);
                        }
                        Utility.setListViewHeightBasedOnChildren(haddenDangerLv);
                        haddenDangerLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Intent intent = new Intent(CountEnterpriseActivity.this, CheckUpDateLoadActivity.class);
                                intent.putExtra("PlanId", listItems_2.get(position).get("PlanId").toString());
                                intent.putExtra("state", "2");
                                intent.putExtra("businessid", listItems_2.get(position).get("businessid").toString());
                                intent.putExtra("statTime", listItems_2.get(position).get("StatTime").toString());
                                intent.putExtra("endTime", listItems_2.get(position).get("EndTime").toString());
                                intent.putExtra("address", listItems_2.get(position).get("Address").toString());
                                intent.putExtra("enforceaccording", listItems_2.get(position).get("enforceaccording").toString());
                                intent.putExtra("planchecktype", listItems_2.get(position).get("planchecktype").toString());
                                intent.putExtra("companyname", listItems_2.get(position).get("companyname").toString());
                                intent.putExtra("XmlCod", "4");
                                intent.putExtra("XmlCodID", BusinessId);
                                intent.putExtra("s_from_time", s_from_time);
                                intent.putExtra("s_end_time", s_end_time);
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
                        Toast.makeText(CountEnterpriseActivity.this, "提交失败", Toast.LENGTH_SHORT).show();
                        Log.e("JSON exception", e.getMessage());
                        e.printStackTrace();
                    }
                } else {
                    //关闭加载页
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                        progressDialog = null;
                    }
                    Toast.makeText(CountEnterpriseActivity.this, "网络未响应，提交失败", Toast.LENGTH_SHORT).show();

                }
            }
        });
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
        else if (myScrollView.getScrollY() == 0) {
            topBtn.setVisibility(View.GONE);

        } else if (myScrollView.getScrollY() > 30) {
            topBtn.setVisibility(View.VISIBLE);

        }

    }
}
