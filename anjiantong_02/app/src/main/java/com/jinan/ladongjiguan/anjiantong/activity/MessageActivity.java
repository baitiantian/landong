package com.jinan.ladongjiguan.anjiantong.activity;

import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.jinan.ladongjiguan.anjiantong.PublicClass.CustomProgressDialog;
import com.jinan.ladongjiguan.anjiantong.utils.SharedPreferencesUtil;
import com.jinan.ladongjiguan.anjiantong.utils.WebServiceUtils;
import com.jinan.ladongjiguan.anjiantong.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.ksoap2.serialization.SoapObject;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by baitiantian on 2017/9/7.
 */

public class MessageActivity extends BaseActivity implements View.OnClickListener, View.OnTouchListener {
    @BindView(R.id.examine_page_back)
    LinearLayout examinePageBack;
    @BindView(R.id.title_layout)
    TextView titleLayout;
    @BindView(R.id.message_list)
    ListView messageList;
    private String WEB_SERVER_URL;
    private CustomProgressDialog progressDialog = null;//加载页
    private ArrayList<HashMap<String,Object>> messageArray;
    @Override
    protected void initView() {
        setContentView(R.layout.message_layout);
        ButterKnife.bind(this);
        titleLayout.setText("消息列表");
    }

    @Override
    protected void init() {
        examinePageBack.setOnClickListener(this);
        examinePageBack.setOnTouchListener(this);
        getMessage();
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
    /***
     * 获取消息数据
     * */
    protected void getMessage(){
        //加载页添加
        if (progressDialog == null){
            progressDialog = CustomProgressDialog.createDialog(this);
        }
        progressDialog.show();
        WEB_SERVER_URL= SharedPreferencesUtil.getStringData(this,"IPString","");
        HashMap<String, String> properties = new HashMap<>();
        properties.put("Xml", "<Request><data code='select-Message'><no><Userid>" +
                SharedPreferencesUtil.getStringData(this,"userId",null)+"</Userid></no></data></Request>");
        properties.put("Token", "");
        Log.d("发出的数据",properties.toString());
        WebServiceUtils.callWebService(WEB_SERVER_URL, "DynamicInvoke", properties, new WebServiceUtils.WebServiceCallBack() {

            //WebService接口返回的数据回调到这个方法中
            @Override
            public void callBack(SoapObject result) {
                if(result != null){
                    Log.d("返回数据",result.toString());
                    messageArray = new ArrayList<>();
                    try {

                        Object detail = result.getProperty("DynamicInvokeResult");
                        JSONObject jsonObj;
                        jsonObj = XML.toJSONObject(detail.toString());
                        JSONArray array;
                        JSONObject obj;
                        HashMap<String,Object> map = new HashMap<>();
                        if(jsonObj.has("DocumentElement")){
                            if(jsonObj.toString().substring(jsonObj.toString().length()-3,jsonObj.toString().length()-2).equals("}")){
                                obj = jsonObj.getJSONObject("DocumentElement").getJSONObject("Table");
                                map.put("messagetype",obj.getString("messagetype"));
                                map.put("messagetext",obj.getString("messagetext"));
                                map.put("id",obj.getString("id"));
                                map.put("relationid",obj.getString("relationid"));
                                map.put("messagereceiver",obj.getString("messagereceiver"));
                                map.put("messagetime",obj.getString("messagetime"));
                                messageArray.add(map);

                            }else if(jsonObj.toString().substring(jsonObj.toString().length()-3,jsonObj.toString().length()-2).equals("]")){
                                array =  jsonObj.getJSONObject("DocumentElement").getJSONArray("Table");
                                for (int i=0;i<array.length();i++){
                                    obj = array.getJSONObject(i);
                                    map.put("messagetype",obj.getString("messagetype"));
                                    map.put("messagetext",obj.getString("messagetext"));
                                    map.put("id",obj.getString("id"));
                                    map.put("relationid",obj.getString("relationid"));
                                    map.put("messagereceiver",obj.getString("messagereceiver"));
                                    map.put("messagetime",obj.getString("messagetime"));
                                    messageArray.add(map);
                                }
                            }
                            SimpleAdapter simpleAdapter = new SimpleAdapter(MessageActivity.this,messageArray,R.layout.message_list_item,
                                    new String[]{"messagetype","messagetext"},new int[]{R.id.tx_message_01,R.id.tx_message_02});
                            messageList.setAdapter(simpleAdapter);
                        }else {
                            Toast.makeText(MessageActivity.this,"无数据", Toast.LENGTH_SHORT).show();

                        }

                        //关闭加载页
                        if (progressDialog != null){
                            progressDialog.dismiss();
                            progressDialog = null;
                        }
                    } catch (JSONException e) {
                        //关闭加载页
                        if (progressDialog != null){
                            progressDialog.dismiss();
                            progressDialog = null;
                        }
                        Toast.makeText(MessageActivity.this,"提交失败,数据错误缺失", Toast.LENGTH_SHORT).show();
                        Log.e("JSON exception", e.getMessage());
                        e.printStackTrace();
                    }

                }else {
                    //关闭加载页
                    if (progressDialog != null){
                        progressDialog.dismiss();
                        progressDialog = null;
                    }
                    Toast.makeText(MessageActivity.this,"网络未响应，提交失败", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }
}
