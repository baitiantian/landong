package com.lunduimohao.landongjiguang.lunduimohao.activity;


import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.lunduimohao.landongjiguang.lunduimohao.PublicClass.AssetsDatabaseManager;
import com.lunduimohao.landongjiguang.lunduimohao.PublicClass.BluetoothChatService;
import com.lunduimohao.landongjiguang.lunduimohao.PublicClass.CustomProgressDialog;
import com.lunduimohao.landongjiguang.lunduimohao.PublicClass.DataListAdapter;
import com.lunduimohao.landongjiguang.lunduimohao.PublicClass.Utility;
import com.lunduimohao.landongjiguang.lunduimohao.R;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class MainActivity extends BaseActivity implements View.OnClickListener{
    private CustomProgressDialog progressDialog = null;//加载页
    private long exitTime = 0;
    private TextView tx_name;//设备名称
    private String wheel_data = "0000000";//测量对象数据
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 0;
    private static final int REQUEST_WHEEL_data = 2;
    // Debugging
    private static final String TAG = "MainActivity";
    private static final boolean D = true;

    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    private String[] temp = {"7","8","9","10","11"};
    // Layout Views
    private TextView mTitle;
    private EditText mOutEditText;

    // Name of the connected device
    private String mConnectedDeviceName = null;
    // Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;
    // Member object for the chat services
    public static BluetoothChatService mChatService = null;
    // String buffer for outgoing messages
    //测量结果框
    private LinearLayout l_wheel_data;
//    设定车轮数据
    private String s_wheel_style = "";
    private String i_wheel_style = "1";
    private TextView i_car_num_5;//导线型号
    private int anInt = 0;
    private EditText numID;
    /**
     * 设备电量
     * */
//    private TextView tx_power;//文字
//    private ProgressBar pb_progressbar;//条

    /**
     * textView数值
     * */
    private TextView data_1;
    private TextView data_2;
    private TextView data_3;
    private TextView data_4;
    /**
     * 导线型号
     * */
    private Spinner e_car_num_5;
    /**
     * 连接设备地址
     * */
    private String address;

    /**
     * 列表
     * */
    private List<HashMap<String,Object>> list = new ArrayList<>();//列表叠加数据
    private ListView listView;//列表显示
    private LinearLayout lData;
    private DataListAdapter dataListAdapter;
    private Button saveData;
    private Button deleteData;
    private TextView selectData;
    /**
     * scrollView
     * */
    private ScrollView scrollView;
    private LinearLayout scrollHeader;
    @Override
    protected void initView() {
        setContentView(R.layout.activity_main);
        /**
         * 设备名称
         * */
        tx_name = (TextView)findViewById(R.id.tx_name);
        /**
         * 欢迎页面
         * */
        Intent intent = new Intent();
        intent.setClass(MainActivity.this,WelcomeActivity.class);
        startActivity(intent);
        /**
         * 测量按钮
         * */
        findViewById(R.id.data).setOnClickListener(this);
        /**
         * 搜索按钮
         * */
        findViewById(R.id.bt_search_data).setOnClickListener(this);
        /**
         * 连接设备按钮
         * */
        findViewById(R.id.device_list).setOnClickListener(this);
        /**
         * 保存曝光时间按钮
         * */
        findViewById(R.id.bt_light_time).setOnClickListener(this);
        /**
         * 踏面轮廓按钮
         * */
        findViewById(R.id.bt_car_num_set).setOnClickListener(this);
        /**
         * 设备连接状态
         * */
        mTitle = (TextView) findViewById(R.id.title_left_text);
       /**
        * 输入框
        * */
        mOutEditText = (EditText) findViewById(R.id.light_time);
//        mOutEditText.setOnEditorActionListener(mWriteListener);
// Initialize the BluetoothChatService to perform bluetooth connections
        /**
         * 设备电量
         * */
//        tx_power = (TextView)findViewById(R.id.tx_power);
//        pb_progressbar = (ProgressBar)findViewById(R.id.pb_progressbar);
        /**
         * 版本号
         * */
        TextView setting_edition_number = (TextView)findViewById(R.id.edition_number);
        String s = "V "+getVersion();
        setting_edition_number.setText(s);
        /**
         * 反选按钮
         * */
        selectData = (TextView)findViewById(R.id.select_data);
        selectData.setOnClickListener(this);
        findViewById(R.id.select_data_01).setOnClickListener(this);
        /**
         * 测量结果
         * */
        l_wheel_data = (LinearLayout)findViewById(R.id.l_wheel_data);
        lData = (LinearLayout)findViewById(R.id.l_data);
        lData.setVisibility(View.GONE);
        /**
         * 保存数据键
         * */
        saveData = (Button) findViewById(R.id.save_data);
        saveData.setOnClickListener(this);
        saveData.setVisibility(View.GONE);
        /**
         * 清除数据键
         * */
        deleteData = (Button) findViewById(R.id.delete_data);
        deleteData.setOnClickListener(this);
        deleteData.setVisibility(View.GONE);
        /**
         * 导线型号
         */
        i_car_num_5 = (TextView)findViewById(R.id.i_car_num_5) ;
        /**
         * 杆号
         * */
        numID = (EditText)findViewById(R.id.num_ID);
        /**
         * 车轮型号
         * */
        e_car_num_5 = (Spinner)findViewById(R.id.e_car_num_5);
        /**
         * 列表
         * */
        listView = (ListView) findViewById(R.id.list_01);
        Resources res =getResources();
        String[] strings = res.getStringArray(R.array.s_car_num_6);
        final List<Map<String,Object>> strings1 = new ArrayList<>();
        for (String string : strings) {
            Map<String, Object> map = new HashMap<>();
            map.put("data", string);
            strings1.add(map);
        }

        SimpleAdapter simpleAdapter = new SimpleAdapter(MainActivity.this,strings1,
                R.layout.device_name_item, new String[]{"data"},new int[]{R.id.data});
        e_car_num_5.setAdapter(simpleAdapter);


        e_car_num_5.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                anInt = position;
                s_wheel_style = strings1.get(position).get("data").toString();
                Log.d("导线型号数据s",s_wheel_style);
                i_wheel_style=position+1+"";
                Log.d("导线型号数据i",i_wheel_style);
                Log.d("设置导线型号数据","8012"+i_wheel_style);
                if(mTitle.getText().equals("无连接")){
                    Toast.makeText(MainActivity.this, "请先连接设备", Toast.LENGTH_SHORT).show();
                }else {
//                    l_wheel_data.setVisibility(View.GONE);
//                    if(!wheel_data.equals("0000000")){
//                        intent.putExtra("wheel_data",wheel_data);
//                        intent.putExtra("s_wheel_style",s_wheel_style);
//                    }else {
//                        intent.putExtra("wheel_data","");
//                    }
//                    intent.setClass(MainActivity.this,WheeldataActivity.class);
//                    startActivityForResult(intent,REQUEST_WHEEL_data);
//                    overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                    String message = "8012"+i_wheel_style;//导线型号
                    sendMessage(message);
                    //加载页添加
                    if (progressDialog == null){
                        progressDialog = CustomProgressDialog.createDialog(MainActivity.this);
                    }
                    progressDialog.show();

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    /**
     * 滑动栏
     * */
        scrollView = (ScrollView)findViewById(R.id.scrollView);
        scrollHeader = (LinearLayout)findViewById(R.id.scrollView_header);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            scrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    if(scrollY>370){
                        scrollHeader.setVisibility(View.VISIBLE);
                    }else {
                        scrollHeader.setVisibility(View.INVISIBLE);
                    }
                }
            });
        }
    }

    @Override
    protected void init() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(
                    BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            // Otherwise, setup the chat session
        }
        mChatService = new BluetoothChatService(this, mHandler);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()){
            case R.id.bt_light_time://设置阈值系数
                Log.d("设置阈值数据","8014"+mOutEditText.getText().toString());
                if(tx_name.getText().equals("未知")||mTitle.getText().equals("无连接")){
                    Toast.makeText(this, "请选择设备进行连接", Toast.LENGTH_SHORT).show();
//                }else if(mOutEditText.getText().toString().length() != 0){
//                    Toast.makeText(this, "请输入所需曝光时间", Toast.LENGTH_SHORT).show();
                }else if(!tx_name.getText().equals("未知")&&wheel_data.length() == 7&&
                        mOutEditText.getText().toString().length() == 0){
                    String message = "8014"+mOutEditText.getText().toString();//阈值
                    //加载页添加
                    if (progressDialog == null){
                        progressDialog = CustomProgressDialog.createDialog(this);
                    }
                    progressDialog.show();
                    sendMessage(message);
                }else if(mOutEditText.getText().toString().length() > 0){
                    String message = "8014"+mOutEditText.getText().toString();//阈值
                    //加载页添加
                    if (progressDialog == null){
                        progressDialog = CustomProgressDialog.createDialog(this);
                    }
                    progressDialog.show();
                    sendMessage(message);
                    Toast.makeText(this, "请设置测试对象数据", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(this, "请输入阈值", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.device_list://连接
                if (mChatService != null) mChatService.stop();
                intent.setClass(MainActivity.this,DeviceListActivity.class);
                startActivityForResult(intent,REQUEST_CONNECT_DEVICE);
                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                break;
            case R.id.data://测量
                //单机版假数据
                String s = "0.00,";
                Double aDouble = 0.50-(Math.random() * 8/10);
                s = s + aDouble+",";
                aDouble = (Math.random() * 5/10)+31.50;
                s = s+ aDouble+",";
                aDouble = (Math.random() * 5/10)+144.70;
                s = s+ aDouble+",";
                aDouble = (Math.random() * 5/10)+24.00;
                s = s+ aDouble;
//                wheelData(s);
//                wheelData("7.00,8.00,9.00,10.00,11.00");
                if(tx_name.getText().equals("未知")){
                    Toast.makeText(this, "请选择设备进行连接", Toast.LENGTH_SHORT).show();
//                }else if (wheel_data.equals("0000000")){
//                    Toast.makeText(this, "请先输入测量对象数据", Toast.LENGTH_SHORT).show();
                }else if(!(numID.getText().length() >0)){
                    Toast.makeText(this, "请先输入杆号", Toast.LENGTH_SHORT).show();
                }else {
//                    wheelData("7.0000,8.0000,9.0000,10.0000");

                    String message = "8010";
                    sendMessage(message);
                    //加载页添加
                    if (progressDialog == null){
                        progressDialog = CustomProgressDialog.createDialog(this);
                    }
                    progressDialog.show();
//                    intent.setClass(MainActivity.this,ListdataActivity.class);
//                    intent.putExtra("numID",numID.getText().toString());
//                    intent.putExtra("style",s_wheel_style);
//                    intent.putExtra("address",address);
//                    startActivity(intent);
//                    overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                }
//                intent.setClass(MainActivity.this,dataActivity.class);
////                    intent.putExtra("light",mOutEditText.getText());
//                intent.putExtra("name","保存测试");
//                intent.putExtra("wheel_data",wheel_data);
//                startActivity(intent);
//                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                break;
            case R.id.bt_car_num_set://踏面轮廓
                Log.d("踏面轮廓数据","8012"+i_wheel_style);

                intent.putExtra("data","1,2;3,4");
                intent.setClass(this,LineChartActivity1.class);
                startActivity(intent);
                if(mTitle.getText().equals("无连接")){
                    Toast.makeText(this, "请先确定设备是否连接", Toast.LENGTH_SHORT).show();
                }else {
//                    l_wheel_data.setVisibility(View.GONE);
//                    if(!wheel_data.equals("0000000")){
//                        intent.putExtra("wheel_data",wheel_data);
//                        intent.putExtra("s_wheel_style",s_wheel_style);
//                    }else {
//                        intent.putExtra("wheel_data","");
//                    }
//                    intent.setClass(MainActivity.this,WheelDataActivity.class);
//                    startActivityForResult(intent,REQUEST_WHEEL_data);
//                    overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                    String message = "8015";//导线型号
                    sendMessage(message);
                    //加载页添加
                    if (progressDialog == null){
                        progressDialog = CustomProgressDialog.createDialog(this);
                    }
                    progressDialog.show();

                }
                break;
            case R.id.bt_search_data://查询
                intent.setClass(MainActivity.this,SearchDataActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                break;
            case R.id.save_data://保存
                TextView data_1 = (TextView)findViewById(R.id.data_1);
                if(numID.getText().length()>0&&!data_1.getText().equals("未知")){
                    dialog();
                }else {
                    Toast.makeText(this, "请先测量或输入杆号", Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.delete_data:
                list = new ArrayList<>();
                lData.setVisibility(View.GONE);
                deleteData.setVisibility(View.GONE);
                saveData.setVisibility(View.GONE);
                break;
            case R.id.select_data:
                for (int i = 0; i < list.size(); i++) {
                    if(DataListAdapter.isSelected.get(i)){
                        DataListAdapter.isSelected.put(i, false);
                    }else {
                        DataListAdapter.isSelected.put(i, true);
                    }
                    dataListAdapter.notifyDataSetChanged();
                }
                break;
            case R.id.select_data_01:
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
    /**
     * 销毁后停止子线程
     * */
    @Override
    public void onDestroy() {

        if (mChatService != null) mChatService.stop();
        if(D) Log.e(TAG, "--- ON DESTROY ---");

        // Stop the Bluetooth chat services
        super.onDestroy();
//        如果有推送服务，注释掉下面一条。
        System.exit(0);
    }
    /**
     * Sends a message.
     * @param message  A string of text to send.
     */
    private void sendMessage(String message) {
        // Check that we're actually connected before trying anything
        if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
            Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
            if (mChatService != null) mChatService.stop();
            return;
        }

        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = message.getBytes();
            mChatService.write(send);
        }
    }
//    // The action listener for the EditText widget, to listen for the return key
//    private TextView.OnEditorActionListener mWriteListener =
//            new TextView.OnEditorActionListener() {
//                public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
//                    // If the action is a key-up event on the return key, send the message
//                    if (actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_UP) {
//                        String message = view.getText().toString();
//                        sendMessage(message);
//                    }
//                    if(D) Log.i(TAG, "END onEditorAction");
//                    return true;
//                }
//            };
    // The Handler that gets information back from the BluetoothChatService

    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
                    if(D) Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);

                    switch (msg.arg1) {
                        case BluetoothChatService.STATE_CONNECTED:
                            mTitle.setText(R.string.title_connected_to);
                            mTitle.append(mConnectedDeviceName);
                            //关闭加载页
                            if (progressDialog != null){
                                progressDialog.dismiss();
                                progressDialog = null;
                            }
                            e_car_num_5.setSelection(0);
                            /**
                             * 电量口
                             * */
                            break;
                        case BluetoothChatService.STATE_CONNECTING:
                            mTitle.setText(R.string.title_connecting);
                            break;
                        case BluetoothChatService.STATE_LISTEN:
                        case BluetoothChatService.STATE_NONE:
                            mTitle.setText(R.string.title_not_connected);
//                            tx_power.setText(R.string.data_main_5);
                            break;
                    }
                    break;
                case MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
//                     construct a string from the buffer
                    String writeMessage = new String(writeBuf);
//                    Toast.makeText(getApplicationContext(),"向"+mConnectedDeviceName+"发送："
//                            + writeMessage, Toast.LENGTH_SHORT).show();
                    break;
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
                        TextView i_car_num_1 = (TextView) findViewById(R.id.i_car_num_1);
                        TextView i_car_num_2 = (TextView) findViewById(R.id.i_car_num_2);
                        TextView i_car_num_3 = (TextView) findViewById(R.id.i_car_num_3);
                        TextView i_car_num_4 = (TextView) findViewById(R.id.i_car_num_4);
                        i_car_num_1.setText(wheel_data.subSequence(0, 3));
                        i_car_num_2.setText(wheel_data.subSequence(3, 5));
                        i_car_num_3.setText(wheel_data.subSequence(5, 6));
                        if(wheel_data.subSequence(6, 7).equals("0")){
                            i_car_num_4.setText("左侧轮");
                        }else {
                            i_car_num_4.setText("右侧轮");
                        }

                        Toast.makeText(getApplicationContext(),"已设定成功", Toast.LENGTH_SHORT).show();
                        break;
                    }
                case MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                    Toast.makeText(getApplicationContext(), "已连接到 "
                            + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_TOAST:
                    Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),
                            Toast.LENGTH_SHORT).show();
                    //关闭加载页
                    if (progressDialog != null){
                        progressDialog.dismiss();
                        progressDialog = null;
                    }
                    break;
            }
        }
    };

    /**
     * 捕捉返回事件按钮
     *
     * 因为此 Activity 继承 TabActivity 用 onKeyDown 无响应，所以改用 dispatchKeyEvent
     * 一般的 Activity 用 onKeyDown 就可以了
     */

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (event.getAction() == KeyEvent.ACTION_DOWN && event.getRepeatCount() == 0) {
                this.exitApp();
            }
            return true;
        }
        return super.dispatchKeyEvent(event);
    }
    /**
     * 退出程序
     */

    private void exitApp() {
        // 判断2次点击事件时间
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(MainActivity.this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
//            stopService(new Intent(MainActivity.this, MessageService.class));
            if (mChatService != null) mChatService.stop();
            finish();
            overridePendingTransition(0, R.anim.zoomout);
        }
    }
    /**
     * 返回连接设备名称
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_ENABLE_BT:
                if (resultCode == RESULT_OK) {
                    // Bluetooth is now enabled Launch the DeviceListActivity to see
                    // devices and do scan
                    Intent serverIntent = new Intent(this, DeviceListActivity.class);
                    startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
                } else {
                    // User did not enable Bluetooth or an error occured
                    Toast.makeText(this, "请手动打开蓝牙", Toast.LENGTH_SHORT)
                            .show();
                    return;
                }
                break;
            case REQUEST_CONNECT_DEVICE:
                if (resultCode != RESULT_OK) {
                    return;
                } else {
                    if(data.getStringExtra("name").length()>0
                            &&data.getStringExtra(DeviceListActivity.EXTRA_DEVICE_ADDRESS).length()>0){
                        tx_name.setText(data.getStringExtra("name"));
                        // Get the device MAC address
                        address = data.getExtras()
                                .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                        // Get the BLuetoothDevice object
                        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
                        //加载页添加
                        if (progressDialog == null){
                            progressDialog = CustomProgressDialog.createDialog(this);
                        }
                        progressDialog.show();
                        // Attempt to connect to the device
                        mChatService.connect(device);
                    }
                }
                break;
            case REQUEST_WHEEL_data:
                if (resultCode != RESULT_OK) {
                    return;
                } else {

                    wheel_data = data.getStringExtra("data");
                    s_wheel_style = data.getStringExtra("wheel_style");

                    Resources res =getResources();
                    String[] strings = res.getStringArray(R.array.s_car_num_7);
                    switch (s_wheel_style){
                        case "1":
                            i_car_num_5.setText(strings[0]);
                            break;
                        case "2":
                            i_car_num_5.setText(strings[1]);
                            break;
                        case "3":
                            i_car_num_5.setText(strings[2]);
                            break;
                        case "4":
                            i_car_num_5.setText(strings[3]);
                            break;
                        case "5":
                            i_car_num_5.setText(strings[4]);
                            break;
                        default:
                            break;
                    }
                    //加载页添加
                    if (progressDialog == null){
                        progressDialog = CustomProgressDialog.createDialog(this);
                    }
                    progressDialog.show();
                    String message = "8012"+data.getStringExtra("wheel_style");
                    sendMessage(message);

                }
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
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
     * 获取版本号
     * @return 当前应用的版本号
     */
    public String getVersion() {
        try {
            PackageManager manager = this.getPackageManager();
            PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
            return info.versionName;
        } catch (Exception e) {
            e.printStackTrace();
            return "null";
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
        double s1;
         data_1 = (TextView)findViewById(R.id.data_1);
//        s1 = temp[1]+" mm";
        s1 = Double.valueOf(temp[1]);//踏面磨耗
//        Log.d("数据",temp[1].substring(0,3));
        data_1.setText(String.valueOf(s1));
        map.put("data_1", new DecimalFormat("0.00").format(s1));
         data_2 = (TextView)findViewById(R.id.data_2);
        s1 =Double.valueOf(temp[2]);//车轮厚度
//        s1 = temp[2]+" °";
        data_2.setText(String.valueOf(s1));
        map.put("data_2",new DecimalFormat("0.00").format(s1));
         data_3 = (TextView)findViewById(R.id.data_3);
        s1 = Double.valueOf(temp[3]);//轮辋宽度
//        s1 = temp[3]+" mm";
        map.put("data_3",new DecimalFormat("0.00").format(s1));
        data_3.setText(String.valueOf(s1));
        data_4 = (TextView)findViewById(R.id.data_4);
        s1 = Double.valueOf(temp[4]);//轮辋厚度
        map.put("data_4",new DecimalFormat("0.00").format(s1));
        data_4.setText(String.valueOf(s1));


        /**
         * 显示数据
         * */
//        l_wheel_data.setVisibility(View.VISIBLE);\
        list.add(map);
        /**
         * 显示数据
         * */
        dataListAdapter = new DataListAdapter(this,list,
                R.layout.list_data_item,
                new String[]{"num","data_1","data_2","data_3","data_4"},
                new int[]{R.id.list_item_01,R.id.data_1,R.id.data_2,R.id.data_3,R.id.data_4});
        listView.setAdapter(dataListAdapter);
        Utility.setListViewHeightBasedOnChildren(listView);
        lData.setVisibility(View.VISIBLE);
        saveData.setVisibility(View.VISIBLE);
        deleteData.setVisibility(View.VISIBLE);
        scrollView.fullScroll(ScrollView.FOCUS_DOWN);
    }

    /**
     * 弹出确认对话框
     * */
    protected void dialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("确认保存");
        builder.setMessage("是否保存本次测试数据到文件夹 LineWear 下的 data.db ？");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                for (int i = 0; i < list.size(); i++){
                    if(DataListAdapter.isSelected.get(i)){
                        saveData(i);
                    }
                    Toast.makeText(MainActivity.this, "数据已保存", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Toast.makeText(MainActivity.this, "已取消", Toast.LENGTH_SHORT).show();
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
        String date = sDateFormat.format(new java.util.Date());
        //创建文件夹
        File file = new File("/sdcard/LineWear");
        boolean isDirectoryCreated=file.exists();
        if (!isDirectoryCreated) {
            isDirectoryCreated= file.mkdir();
        }
        if(isDirectoryCreated) {
            SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase("/sdcard/LineWear/data.db",null);
            //创建一个表
            try {
                String s = "create table wheel_data(" +
                        "WheelId varchar(50) NOT NULL primary key," +
                        "TreadWear varchar(50) NULL," +
                        "WheelThick varchar(50) NULL," +
                        "RimWidth varchar(50) NULL," +
                        "RimThick varchar(50) NULL," +
                        "Time varchar(50) NOT NULL,"+
                        "Alldata varchar(50) NOT NULL)";
                db.execSQL(s);
            }catch (Exception e) {
                //This happens on every launch that isn't the first one.
                Log.w("一般第一次不发生的错误", "Error while creating db: " + e.toString());
            }
            /**
             * 更新插入数据
             * */
            try {
                ContentValues values  = new ContentValues();
                values.put("WheelId",numID.getText().toString()+"-"+list.get(i).get("num"));
                values.put("TreadWear",list.get(i).get("data_1").toString());
                values.put("WheelThick",list.get(i).get("data_2").toString());
                values.put("RimWidth",list.get(i).get("data_3").toString());
                values.put("RimThick",list.get(i).get("data_4").toString());
                values.put("Time",date.substring(2,date.length()));
                values.put("Alldata","1");
                Log.d("保存数据","REPLACE INTO wheel_data VALUES('"+
                        numID.getText().toString()+"-"+list.get(i).get("num") +"','"+
                        list.get(i).get("data_1").toString() +"','"+
                        list.get(i).get("data_2").toString() +"','"+
                        list.get(i).get("data_3").toString() +"','"+
                        list.get(i).get("data_4").toString() +"','"+
                        date +"','1')");
//                db.execSQL("REPLACE INTO wheel_data VALUES('"+
//                        wheel_data +"','"+
//                        temp[1] +"','"+
//                        temp[2] +"','"+
//                        temp[3] +"','"+
//                        temp[4] +"','"+
//                        data +"','1')");
                db.replace("wheel_data",null,values);
            }catch (Exception e) {
                //This happens on every launch that isn't the first one.
                Log.w("一般不会发生的错误", "Error while REPLACE INTO db: " + e.toString());
            }
             db.close();
        }
    }
    @Override
    public void finish() {

        super.finish();
        android.os.Process.killProcess(android.os.Process.myPid()); /**杀死这个应用的全部进程*/
    }
}
