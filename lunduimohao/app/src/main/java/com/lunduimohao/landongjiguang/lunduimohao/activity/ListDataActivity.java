package com.lunduimohao.landongjiguang.lunduimohao.activity;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lunduimohao.landongjiguang.lunduimohao.PublicClass.AssetsDatabaseManager;
import com.lunduimohao.landongjiguang.lunduimohao.PublicClass.BluetoothChatService;
import com.lunduimohao.landongjiguang.lunduimohao.PublicClass.CustomProgressDialog;
import com.lunduimohao.landongjiguang.lunduimohao.PublicClass.DataListAdapter;
import com.lunduimohao.landongjiguang.lunduimohao.R;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static com.lunduimohao.landongjiguang.lunduimohao.activity.MainActivity.MESSAGE_READ;
import static com.lunduimohao.landongjiguang.lunduimohao.activity.MainActivity.mChatService;


public class ListDataActivity extends BaseActivity implements View.OnClickListener{
    // Debugging
    private static final String TAG = "ListDataActivity";
    private static final boolean D = true;
    private CustomProgressDialog progressDialog = null;//加载页
    private String[] temp = {"0","1","2","3","4"};
    private List<HashMap<String,Object>> list = new ArrayList<>();//列表叠加数据
    private ListView listView;//列表显示
    private TextView allData;
    private DataListAdapter dataListAdapter;
    private BluetoothAdapter mBluetoothAdapter = null;
    @Override
    protected void initView() {
        setContentView(R.layout.list_data_layout);
        /**
         * 标题
         * */
        TextView title_layout = (TextView)findViewById(R.id.title_layout);
        String s = getIntent().getStringExtra("numID")+" 号杆";
        title_layout.setText(s);


        /**
         * 全选
         * */
        allData = (TextView)findViewById(R.id.delete_layout);
        allData.setText("反选");
        allData.setOnClickListener(this);
        /**
         * 保存数据键
         * */
        findViewById(R.id.save_data).setOnClickListener(this);
        /**
         * 测量按钮
         * */
        findViewById(R.id.data).setOnClickListener(this);
        /**
         * 列表
         * */
        listView = (ListView) findViewById(R.id.list_01);

        /**
         * 返回键
         * */
        findViewById(R.id.examine_page_back).setOnClickListener(this);
//        getData();
    }

    @Override
    protected void init() {
//        wheelData("1,2,3,4");
//        mChatService.stop();
//        mChatService = null;
//        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//        if (!mBluetoothAdapter.isEnabled()) {
//            Intent enableIntent = new Intent(
//                    BluetoothAdapter.ACTION_REQUEST_ENABLE);
//            startActivityForResult(enableIntent, 0);
//            // Otherwise, setup the chat session
//        }

        mChatService = new BluetoothChatService(this, Handler);
//        mChatService.start();
//        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(getIntent().getStringExtra("address"));
//        mChatService.connect(device);
//        if (mChatService != null) {
//            // Only if the state is STATE_NONE, do we know that we haven't started already
//            if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
//                // Start the Bluetooth chat services
//                mChatService.start();
//            }
//        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.data:
                getData();
//                wheelData("1,2,3,4");
                break;
            case R.id.save_data:

                if(DataListAdapter.isSelected.size()>0){
                    dialog();
                }

                break;
            case R.id.examine_page_back:
                onBackPressed();
                break;
            case R.id.delete_layout:
                for (int i = 0; i < list.size(); i++) {
                    if(DataListAdapter.isSelected.get(i)){
                        DataListAdapter.isSelected.put(i, false);
                    }else {
                        DataListAdapter.isSelected.put(i, true);
                    }
                    dataListAdapter.notifyDataSetChanged();
                }
                break;
            default:
                break;
        }
    }

    /**获得测量数据*/
    private void getData(){
        String message = "8010";
        sendMessage(message);
        //加载页添加
        if (progressDialog == null){
            progressDialog = CustomProgressDialog.createDialog(this);
        }
        progressDialog.show();
    }

    /**
     * 重写苏醒事件
     * */
    @Override
    public synchronized void onResume() {
        super.onResume();
        if(D) Log.e(TAG, "+ ON RESUME +");

        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (mChatService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
                // Start the Bluetooth chat services
                mChatService.start();
            }
        }
    }

    /**
     * Sends a message.
     * @param message  A string of text to send.
     */
    private void sendMessage(String message) {
        // Check that we're actually connected before trying anything
        Log.d("数据",mChatService.getState()+"");
        if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
            Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
            if (mChatService != null) mChatService.stop();
            //关闭加载页
            if (progressDialog != null){
                progressDialog.dismiss();
                progressDialog = null;
            }
            return;
        }

        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = message.getBytes();
            mChatService.write(send);
        }
    }

    /**
     * 显示测量返回数据
     * */
    protected void wheelData(String s){

        /**
         * 读取显示数据
         * */
        temp = s.split(",");
        HashMap<String,Object> map = new HashMap<>();
        map.put("num",list.size()+1+"");
        String s1;
        s1 = temp[1]+" mm";
//        Log.d("数据",temp[1].substring(0,3));
        map.put("data_1",s1);
        s1 = temp[2]+" °";
        map.put("data_2",s1);
        s1 = temp[3]+" mm";
        map.put("data_3",s1);
//        s1 = temp[4]+" mm";
//        data_4.setText(s1);
        double d = 0;
        try {
            d = Double.parseDouble(temp[1]);
        }catch (Exception e){
            Log.e("转换double数据报错",e.toString());
        }
        String Col_1 = (d+0.05)+"";
        try {
            // 初始化，只需要调用一次
            AssetsDatabaseManager.initManager(getApplication());
            // 获取管理对象，因为数据库需要通过管理对象才能够获取
            AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
            // 通过管理对象获取数据库
            SQLiteDatabase db = mg.getDatabase("mohaodate.db");
            Cursor cursor;
            String style = getIntent().getStringExtra("style");
            String style2;
            if(style.contains("_")){
                style2 = style.substring(0,style.indexOf("_"));
            }else {
                style2 = style;
            }
//            Log.e("ListDate","style为"+style+" ;style2为"+style2);
            switch (style2){
                case "120":
                    cursor = db.rawQuery("SELECT* FROM Data_120 WHERE Col_1 = ?",
                            new String[]{Col_1.substring(0,3)+" "});

                    cursor.moveToFirst();
                    s1 = cursor.getString(cursor.getColumnIndex("Col_3"))+"%";
                    map.put("data_4",s1);
                    cursor.close();
                    break;
                case "150_1":
                    cursor = db.rawQuery("SELECT* FROM Data_150 WHERE Col_1 = ?",
                            new String[]{Col_1.substring(0,3)+" "});
                    cursor.moveToFirst();
                    s1 = cursor.getString(cursor.getColumnIndex("Col_3"))+"%";
                    map.put("data_4",s1);
                    cursor.close();
                    break;
                case "150_2":
                    cursor = db.rawQuery("SELECT* FROM Data_150 WHERE Col_1 = ?",
                            new String[]{Col_1.substring(0,3)+" "});
                    cursor.moveToFirst();
                    s1 = cursor.getString(cursor.getColumnIndex("Col_3"))+"%";
                    map.put("data_4",s1);
                    cursor.close();
                    break;
                case "85":
                    cursor = db.rawQuery("SELECT* FROM Data_85 WHERE Col_1 = ?",
                            new String[]{Col_1.substring(0,3)+" "});
                    cursor.moveToFirst();
                    s1 = cursor.getString(cursor.getColumnIndex("Col_3"))+"%";
                    map.put("data_4",s1);
                    cursor.close();
                    break;
                case "110":
                    cursor = db.rawQuery("SELECT* FROM Data_110 WHERE Col_1 = ?",
                            new String[]{Col_1.substring(0,3)+" "});
                    cursor.moveToFirst();
                    s1 = cursor.getString(cursor.getColumnIndex("Col_3"))+"%";
                    map.put("data_4",s1);
                    cursor.close();
                    break;
                default:
                    map.put("data_4","0%");
                    break;

            }
        }catch (Exception e){
            Log.e("查找磨耗百分比数据报错",e.toString(),e);
        }
        list.add(map);
        for (int i = 0; i < list.size(); i++) {
            Log.e("ListDate","百分比为"+list.get(i).toString());
        }

        /**
         * 显示数据
         * */
        dataListAdapter = new DataListAdapter(this,list,
                R.layout.list_data_item,
                new String[]{"num","data_1","data_2","data_3","data_4"},
                new int[]{R.id.list_item_01,R.id.data_1,R.id.data_2,R.id.data_3,R.id.data_4});
        listView.setAdapter(dataListAdapter);
    }

    // The Handler that gets information back from the BluetoothChatService

    @SuppressLint("HandlerLeak")
    private final android.os.Handler Handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // 传回的数据
                    String readMessage = new String(readBuf, 0, msg.arg1);
//                    Toast.makeText(getApplicationContext(),"从"+mConnectedDeviceName+"接收："
//                            + readMessage, Toast.LENGTH_SHORT).show();
                    //关闭加载页
                    if (progressDialog != null){
                        progressDialog.dismiss();
                        progressDialog = null;
                    }
                    if(readMessage.length()>10){
//                        Intent intent = new Intent();
//                        intent.setClass(MainActivity.this,dataActivity.class);
//                        intent.putExtra("data",readMessage);
//                        intent.putExtra("name",tx_name.getText());
//                        intent.putExtra("wheel_data",wheel_data);
//                        startActivity(intent);
//                        overridePendingTransition(R.anim.zoomin, R.anim.zoomout);

                        wheelData(readMessage);
                        break;
                    }else {
                        Toast.makeText(getApplicationContext(),"已设定成功", Toast.LENGTH_SHORT).show();
                        break;
                    }

            }
        }
    };
    /**
     * 弹出确认对话框
     * */
    protected void dialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("确认保存");
        builder.setMessage("是否保存本次测试数据到文件夹 TreadWear 下的 data.db ？");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                for (int i = 0; i < list.size(); i++){
                    if(DataListAdapter.isSelected.get(i)){
                        saveData(i);
                    }
                }

            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Toast.makeText(ListDataActivity.this, "已取消", Toast.LENGTH_SHORT).show();
            }
        });
        builder.show();
    }

    /**
     * 保存数据
     * */
    protected void saveData(int i){
        SimpleDateFormat sDateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm", Locale.getDefault());
        String data = sDateFormat.format(new java.util.Date());
        //创建文件夹
        File file = new File("/sdcard/TreadWear");
        boolean isDirectoryCreated=file.exists();
        if (!isDirectoryCreated) {
            isDirectoryCreated= file.mkdir();
        }
        if(isDirectoryCreated) {
            SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase("/sdcard/TreadWear/data.db",null);
            //创建一个表
            try {
                db.execSQL("create table wheel_data(" +
                        "WheelId varchar(50) NOT NULL primary key," +
                        "TreadWear varchar(50) NULL," +
                        "WheelThick varchar(50) NULL," +
                        "RimWidth varchar(50) NULL," +
                        "RimThick varchar(50) NULL," +
                        "Time varchar(50) NOT NULL,"+
                        "AllDate varchar(50) NOT NULL)");
            }catch (Exception e) {
                //This happens on every launch that isn't the first one.
                Log.w("一般第一次不发生的错误", "Error while creating db: " + e.toString());
            }
            /**
             * 更新插入数据
             * */
            try {
                ContentValues values  = new ContentValues();
                values.put("WheelId",getIntent().getStringExtra("numID")+"-"+list.get(i).get("num"));
                values.put("TreadWear",list.get(i).get("data_1").toString());
                values.put("WheelThick",list.get(i).get("data_2").toString());
                values.put("RimWidth",list.get(i).get("data_3").toString());
                values.put("RimThick",list.get(i).get("data_4").toString());
                values.put("Time",data);
                values.put("AllDate","1");
                Log.d("保存数据","REPLACE INTO wheel_data VALUES('"+
                        getIntent().getStringExtra("numID")+"-"+list.get(i).get("num") +"','"+
                        list.get(i).get("data_1").toString() +"','"+
                        list.get(i).get("data_2").toString() +"','"+
                        list.get(i).get("data_3").toString() +"','"+
                        list.get(i).get("data_4").toString() +"','"+
                        data +"','1')");
//                db.execSQL("REPLACE INTO wheel_data VALUES('"+
//                        wheel_data +"','"+
//                        temp[1] +"','"+
//                        temp[2] +"','"+
//                        temp[3] +"','"+
//                        temp[4] +"','"+
//                        data +"','1')");
                db.replace("wheel_date",null,values);
            }catch (Exception e) {
                //This happens on every launch that isn't the first one.
                Log.w("一般不会发生的错误", "Error while REPLACE INTO db: " + e.toString());
            }
            Toast.makeText(ListDataActivity.this, "数据已保存", Toast.LENGTH_SHORT).show();
            db.close();
        }else {
            Toast.makeText(ListDataActivity.this, "保存数据失败", Toast.LENGTH_SHORT).show();
        }
    }
    /**
     * 重写返回键
     * */
    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
    }
}
