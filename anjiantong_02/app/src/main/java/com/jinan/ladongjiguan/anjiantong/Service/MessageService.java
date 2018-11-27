package com.jinan.ladongjiguan.anjiantong.Service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
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
import java.util.List;


public class MessageService extends Service {
    //获取消息线程
    private MessageThread messageThread = null;
    //通知栏通知叠加
    private int num = 0;
    //推送消息信息
    private List<String> stringArrayList = new ArrayList<>();
    //消息的时间
    private List<String> stringListTime = new ArrayList<>();
    //消息地址
    private List<String> stringListAddress = new ArrayList<>();
    //推送消息ID，用于删除
    private List<String> stringArrayListID = new ArrayList<>();
    //用户ID
    private String user_id;
    //发送给服务器用于删除消息推送
    private String string = null;
    private String WEB_SERVER_URL;//服务器地址
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //用户id
        user_id = intent.getStringExtra("id");
        //服务器地址
        WEB_SERVER_URL= intent.getStringExtra("ip");
        //开启线程
        messageThread = new MessageThread();
        messageThread.isRunning = true;
//        Log.e("启动service数据","2");
        messageThread.start();
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 从服务器端获取消息
     */
    class MessageThread extends Thread {
        //运行状态，下一步骤有大用
        public boolean isRunning = true;//终止服务时需要终止这个子线程，赋值false
        public void run() {
            Looper.prepare();
            while(isRunning){
                try {

                    //休息10秒
                    //获取服务器消息
                    List<String> serverMessage;
                    //通知主线程访问服务器
                    mHandler.sendEmptyMessage(0);
                    Thread.sleep(10000);
                    serverMessage = stringArrayList;
//                    Log.d("3得到的数据",""+stringArrayList);
                    if(serverMessage.size()>0){
                        for (int i=0;i<serverMessage.size();i++){
                            //更新通知栏
                        /*获取状态通知栏管理*/
                            NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                        /*实例化通知栏构造器NotificationCompat.Builder*/
                            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getBaseContext());
                        /*点击事件*/
//                            Intent intent = new Intent(getBaseContext(),OrderDetailActivity.class);
//                            intent.putExtra("step","2");
//                            intent.putExtra("applyId",stringListAddress.get(i).subSequence(45,stringListAddress.get(i).length()));
////                            Log.d("发送的数据",""+stringListAddress);
//                            PendingIntent pendingIntent = PendingIntent.getActivity(getBaseContext(), UUID.randomUUID().hashCode()
//                                    , intent, PendingIntent.FLAG_UPDATE_CURRENT);
////                            Log.d("intent发送的数据",""+pendingIntent);
//                            if(!serverMessage.get(i).substring(0,3).equals("已审批")){
//                                pendingIntent = PendingIntent.getService(getBaseContext(), UUID.randomUUID().hashCode()
//                                        , intent, PendingIntent.FLAG_UPDATE_CURRENT);
//                            }
                        /*配置通知栏*/
                            mBuilder.setContentTitle(serverMessage.get(i))//设置通知栏标题
                                    .setContentText("执行时间： "+stringListTime.get(i)) //设置通知栏显示内容
//                                    .setContentIntent(pendingIntent)//通知栏点击事件
//                                    .setNumber(number) //设置通知集合的数量
//                                    .setTicker("你有新的消息") //通知首次出现在通知栏，带上升动画效果的
                                    .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
                                    .setPriority(Notification.PRIORITY_DEFAULT) //设置该通知优先级
                                    .setAutoCancel(true)//设置这个标志当用户单击面板就可以让通知将自动取消
                                    .setOngoing(false)//ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
                                    .setDefaults(Notification.DEFAULT_ALL)//向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合
                                    //Notification.DEFAULT_ALL声音+震动  Notification.DEFAULT_SOUND 添加声音 DEFAULT_VIBRATE震动
                                    // requires VIBRATE permission
                                    .setSmallIcon(R.drawable.ic_launcher);//设置通知小ICON

                            mNotificationManager.notify(num, mBuilder.build());
                            num++;
                        }
                        SharedPreferencesUtil.saveStringData(MessageService.this,"message", "");
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 这里以此方法通知主线程访问服务器
     */
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    getServerMessage();
                    break;
                default:
                    break;
            }
        }

    };
    /**
     * 从服务器获取消息推送信息
     */
    protected void getServerMessage(){
        stringArrayListID = new ArrayList<>();
        stringArrayList = new ArrayList<>();
        stringListAddress = new ArrayList<>();
        //添加参数
        HashMap<String, String> properties = new HashMap<>();
        properties.put("Xml", "<Request><data  code='select-Message'><no><MessageReceiver>"+
                user_id +"</MessageReceiver></no></data></Request>");
        properties.put("Token", "");
        //通过工具类调用WebService接口
//        Log.e("发送的数据",""+properties);
//        Log.e("ip地址的数据",""+WEB_SERVER_URL);
        WebServiceUtils.callWebService(WEB_SERVER_URL, "DynamicInvoke", properties, new WebServiceUtils.WebServiceCallBack() {
            @Override
            public void callBack(SoapObject result) {
                if(result != null){
                    try {
                        Object detail = result.getProperty("DynamicInvokeResult");
                        JSONObject jsonObj = XML.toJSONObject(detail.toString());
                        JSONArray array;
                        JSONObject obj;
//                        Log.e("传回的数据",""+detail);
                        if(jsonObj.has("DocumentElement")&&jsonObj.toString().substring(jsonObj.toString().length()-3,jsonObj.toString().length()-2).equals("}")){
                            obj = jsonObj.getJSONObject("DocumentElement").getJSONObject("Table");
                            stringArrayListID.add(obj.getString("id"));
                            stringArrayList.add(obj.getString("messagetext"));
                            stringListTime.add(obj.getString("messagetime"));
                            stringListAddress.add(obj.getString("address"));
                            MessageDel();

                        }else if(jsonObj.has("DocumentElement")&&jsonObj.toString().substring(jsonObj.toString().length()-3,jsonObj.toString().length()-2).equals("]")){
                            array =  jsonObj.getJSONObject("DocumentElement").getJSONArray("Table");
                            for(int i=0; i<array.length();i++){
                                obj = array.getJSONObject(i);
                                stringArrayListID.add(obj.getString("id"));
                                stringArrayList.add(obj.getString("messagetext"));
                                stringListTime.add(obj.getString("messagetime"));
                                stringListAddress.add(obj.getString("address"));
                                MessageDel();
                            }
                        }

                    }catch (JSONException e){
//                        Log.e("JSON exception", e.getMessage());
                        e.printStackTrace();
                    }
                }

            }
        });
    }
    /**
     * 通知服务器删除获得的推送信息
     * */
    protected void MessageDel(){
        for (int i=0;i<stringArrayListID.size();i++){
            if(string == null){
                string = stringArrayListID.get(i);
            }else {
                string = string + "," + stringArrayListID.get(i);
            }
        }
        //添加参数
        HashMap<String, String> properties = new HashMap<>();
        properties.put("Xml", "<Request><data  code='delete-Message'><no><s>"+
                string+"</s></no></data></Request>");
        properties.put("Token", "");
        //通过工具类调用WebService接口
        WebServiceUtils.callWebService(WEB_SERVER_URL, "DynamicInvoke", properties, new WebServiceUtils.WebServiceCallBack() {
            @Override
            public void callBack(SoapObject result) {
            }
        });
    }
    @Override
    public void onDestroy() {
        messageThread.isRunning = false;//销毁service之时，销毁子线程
        super.onDestroy();
    }

}
