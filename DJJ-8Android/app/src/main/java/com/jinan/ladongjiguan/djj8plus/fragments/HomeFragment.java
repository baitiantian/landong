package com.jinan.ladongjiguan.djj8plus.fragments;


import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.jinan.ladongjiguan.djj8plus.R;
import com.jinan.ladongjiguan.djj8plus.activity.ChooseActivity;
import com.jinan.ladongjiguan.djj8plus.activity.DeviceListActivity;
import com.jinan.ladongjiguan.djj8plus.dialog.DataDialog;
import com.jinan.ladongjiguan.djj8plus.dialog.DialogNormalDialog;
import com.jinan.ladongjiguan.djj8plus.dialog.TypeDialog;
import com.jinan.ladongjiguan.djj8plus.publicClass.AssetsDatabaseManager;
import com.jinan.ladongjiguan.djj8plus.publicClass.BluetoothChatService;
import com.jinan.ladongjiguan.djj8plus.publicClass.CustomProgressDialog;
import com.jinan.ladongjiguan.djj8plus.publicClass.GetDate;
import com.jinan.ladongjiguan.djj8plus.publicClass.DataTransformation;
import com.jinan.ladongjiguan.djj8plus.publicClass.SharedPreferencesUtil;
import com.jinan.ladongjiguan.djj8plus.views.ClockViewByPath;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;
import static android.provider.Settings.Global.DEVICE_NAME;
import static com.jinan.ladongjiguan.djj8plus.activity.ChooseActivity.CHOOSE_BACK_HOME;
import static com.jinan.ladongjiguan.djj8plus.activity.ChooseActivity.CHOOSE_BACK_HOME_EDIT_DELETE;
import static com.jinan.ladongjiguan.djj8plus.activity.MainActivity.MESSAGE_DEVICE_NAME;
import static com.jinan.ladongjiguan.djj8plus.activity.MainActivity.MESSAGE_READ;
import static com.jinan.ladongjiguan.djj8plus.activity.MainActivity.MESSAGE_STATE_CHANGE;
import static com.jinan.ladongjiguan.djj8plus.activity.MainActivity.MESSAGE_TOAST;
import static com.jinan.ladongjiguan.djj8plus.activity.MainActivity.MESSAGE_WRITE;
import static com.jinan.ladongjiguan.djj8plus.activity.MainActivity.REQUEST_CONNECT_DEVICE;
import static com.jinan.ladongjiguan.djj8plus.activity.MainActivity.REQUEST_ENABLE_BT;
import static com.jinan.ladongjiguan.djj8plus.activity.MainActivity.TOAST;
import static com.jinan.ladongjiguan.djj8plus.activity.MainActivity.mChatService;


public class HomeFragment extends BaseFragment {


    @BindView(R.id.tx_home_route)
    TextView txHomeRoute;
    @BindView(R.id.tx_home_area)
    TextView txHomeArea;
    @BindView(R.id.tx_home_section)
    TextView txHomeSection;
    @BindView(R.id.tx_home_tunnel)
    TextView txHomeTunnel;
    @BindView(R.id.tx_home_num)
    TextView txHomeNum;
    @BindView(R.id.e_car_num_5)
    Spinner eCarNum5;
    @BindView(R.id.bt_home_setting)
    Button btHomeSetting;
    @BindView(R.id.bt_home_route)
    LinearLayout btHomeRoute;
    @BindView(R.id.bt_home_area)
    LinearLayout btHomeArea;
    @BindView(R.id.bt_home_section)
    LinearLayout btHomeSection;
    @BindView(R.id.bt_home_tunnel)
    LinearLayout btHomeTunnel;
    @BindView(R.id.tx_device_name)
    TextView txDeviceName;
    @BindView(R.id.bt_device_list)
    Button btDeviceList;
    @BindView(R.id.l_data)
    TextView lData;
    @BindView(R.id.bt_data)
    Button btData;
    @BindView(R.id.clock_view_by_path)
    ClockViewByPath clockViewByPath;
    @BindView(R.id.tx_date)
    TextView txDate;
    @BindView(R.id.Radio1)
    RadioButton Radio1;
    @BindView(R.id.Radio2)
    RadioButton Radio2;
    @BindView(R.id.RadioGroup1)
    RadioGroup RadioGroup1;
    @BindView(R.id.tx_home_note)
    TextView txHomeNote;


    private View view;
    private int mMaskColor;
    /**
     * 连接设备地址
     */
    private String address;
    // Debugging
    private static final String TAG = HomeFragment.class.getSimpleName();
    private static final boolean D = true;


    // Local Bluetooth adapter
    public static BluetoothAdapter mBluetoothAdapter = null;
    // Member object for the chat services
    private CustomProgressDialog progressDialog = null;//加载页
    private String mConnectedDeviceName = null;//已连接设备名
    /**
     * 添加新路线工区区间隧道
     */
    public static final int ADD_NEW_ROUTE = 0;
    public static final int ADD_NEW_AREA = 1;
    public static final int ADD_NEW_SECTION = 2;
    public static final int ADD_NEW_TUNNEL = 3;
    public static final int HOME_TO_CHOOSE = 1001;

    private DataDialog builder = null;//测量数据
    private TypeDialog typeDialog = null;//设置
    private String mChoosed_routeId = "";
    private String mChoosed_areaId = "";
    private String mChoosed_sectionId = "";
    private String mChoosed_tunnelId = "";
    private int mChoosed_type;
    private SQLiteDatabase mDb;
    private int mChoosed_edit_delete;
    private int mChoosed_edit_delete_flag;

    private String sxx = "上行";//上下行
    private String mLastDataId = "";
    private String mD1_dg = null, mD1_lcz = null, mD1_gj = null, mD1_cg = null;
    private String mD2_dg = null, mD2_plz = null, mD2_ngj = null;
    private String mD3_hxbg = null, mD3_clxj = null;
    private String mD5_fztg = null, mD5_fzpl = null;
    private String mD6_dwqpd = null, mD6_gc = null;
    private String mD7_gd1 = null, mD7_gd2 = null, mD7_gc = null;
    private String mD8_gd = null, mD8_xj = null, mD8_gc = null;
    private String mD9_spj = null, mD9_czj = null;
    private String mD10_zzczd = null;
    private String mD11_zzkd = null;
    private boolean mAdded = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, view);
//        clockViewByPath.performAnimation();//启动时钟
        txDate.setText(GetDate.Date());//添加日期
        /*初始化蓝牙*/
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(
                    BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            // Otherwise, setup the chat session
        }
        mChatService = new BluetoothChatService(view.getContext(), mHandler);
        getData();
        progressDialog = CustomProgressDialog.createDialog(view.getContext());
        builder = new DataDialog(view.getContext());//初始化测量数据对话框
        return view;
    }

    /**
     * 获取数据
     */
    private void getData() {
        // 初始化，只需要调用一次
        AssetsDatabaseManager.initManager(view.getContext());
        // 获取管理对象，因为数据库需要通过管理对象才能够获取
        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
        // 通过管理对象获取数据库
        mDb = mg.getDatabase("DJJ-8data.db");
//        searchName();
        /*是否配置过路线等*/
        mChoosed_sectionId = SharedPreferencesUtil.getStringData(view.getContext(), "s_type_sectionId", "");
        if (SharedPreferencesUtil.getBooleanData(view.getContext(), "b_type", false) && mChoosed_sectionId != "") {
            Log.e(TAG + "-getData", "测试测试-----");
            setType();
        }
        /*选择上下行监听*/
        RadioGroup1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int id = group.getCheckedRadioButtonId();
                switch (id) {
                    case R.id.Radio1:
                        sxx = "上行";
                        break;
                    case R.id.Radio2:
                        sxx = "下行";
                        break;
                    default:
                        break;
                }
            }
        });
    }

    /**
     * 初始配置路线等
     */
    private void setType() {
        Cursor cursor, cursor1, cursor2, cursor3;
        String sectionName = "";
        String areaName = "";
        String routeName = "";
        String tunnelName = "";
        cursor2 = mDb.rawQuery("SELECT* FROM ELL_Section WHERE SectionId = ?", new String[]{mChoosed_sectionId});
        while (cursor2.moveToNext()) {
            sectionName = cursor2.getString(cursor2.getColumnIndex("SectionName"));
            mChoosed_areaId = cursor2.getString(cursor2.getColumnIndex("AreaId"));
            cursor1 = mDb.rawQuery("SELECT* FROM ELL_Area WHERE AreaId = ?", new String[]{mChoosed_areaId});
            while (cursor1.moveToNext()) {
                areaName = cursor1.getString(cursor1.getColumnIndex("AreaName"));
                mChoosed_routeId = cursor1.getString(cursor1.getColumnIndex("RouteId"));
                cursor = mDb.rawQuery("SELECT* FROM ELL_Route WHERE RouteId = ?", new String[]{mChoosed_routeId});
                while (cursor.moveToNext()) {
                    routeName = cursor.getString(cursor.getColumnIndex("RouteName"));
                    txHomeRoute.setText(routeName);
                }
                cursor.close();
                txHomeArea.setText(areaName);
            }
            cursor1.close();
            txHomeSection.setText(sectionName);
        }
        cursor2.close();
        /*if (mChoosed_tunnelId != "") {
            cursor3 = mDb.rawQuery("SELECT* FROM ELL_Tunnel WHERE TunnelId = ?", new String[]{mChoosed_tunnelId});
            while (cursor3.moveToNext()) {
                tunnelName = cursor3.getString(cursor3.getColumnIndex("TunnelName"));
                txHomeTunnel.setText(tunnelName);
            }
            cursor3.close();
        }*/
        cursor3 = mDb.rawQuery("SELECT* FROM ELL_Tunnel WHERE SectionId = ?", new String[]{mChoosed_sectionId});
        while (cursor3.moveToNext()) {
            tunnelName = cursor3.getString(cursor3.getColumnIndex("TunnelName"));
            txHomeTunnel.setText(tunnelName);
            Log.e(TAG + "-setType", "tunnelName为" + tunnelName);
        }
        cursor3.close();
    }

    @OnClick({R.id.bt_device_list, R.id.bt_home_route, R.id.bt_home_area, R.id.bt_home_section, R.id.bt_home_tunnel,
            R.id.tx_home_route, R.id.tx_home_area, R.id.tx_home_section, R.id.tx_home_tunnel})
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.bt_device_list://l连接设备
                if (mChatService != null) mChatService.stop();
                intent.setClass(view.getContext(), DeviceListActivity.class);
                startActivityForResult(intent, 1);
                break;
            case R.id.bt_home_route://添加新路线
                addNew(ADD_NEW_ROUTE);
                break;
            case R.id.bt_home_area://添加新工区
                addNew(ADD_NEW_AREA);
                break;
            case R.id.bt_home_section://添加新区间
                addNew(ADD_NEW_SECTION);
                break;
            case R.id.bt_home_tunnel://添加新隧道
                addNew(ADD_NEW_TUNNEL);
                break;
            case R.id.tx_home_route://选择路线
                intent.setClass(getContext(), ChooseActivity.class);
                intent.putExtra("TYPE_FLAG", ADD_NEW_ROUTE);//
                intent.putExtra(ChooseActivity.CHOOSE_ROUTEID, mChoosed_routeId);
                intent.putExtra(ChooseActivity.CHOOSE_AREAID, mChoosed_areaId);
                intent.putExtra(ChooseActivity.CHOOSE_SECTIONID, mChoosed_sectionId);
                intent.putExtra(ChooseActivity.CHOOSE_TUNNELID, mChoosed_tunnelId);
                startActivityForResult(intent, CHOOSE_BACK_HOME);
                break;
            case R.id.tx_home_area://选择工区
                if (mChoosed_routeId != "" && txHomeRoute.getText().toString() != "选择路线") {
                    intent.setClass(getContext(), ChooseActivity.class);
                    intent.putExtra("TYPE_FLAG", ADD_NEW_AREA);
                    intent.putExtra(ChooseActivity.CHOOSE_ROUTEID, mChoosed_routeId);//
                    intent.putExtra(ChooseActivity.CHOOSE_AREAID, mChoosed_areaId);
                    intent.putExtra(ChooseActivity.CHOOSE_SECTIONID, mChoosed_sectionId);
                    intent.putExtra(ChooseActivity.CHOOSE_TUNNELID, mChoosed_tunnelId);
                    startActivityForResult(intent, CHOOSE_BACK_HOME);
                } else {
                    Toast.makeText(getContext(), "请选择路线", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.tx_home_section://选择区间
                if (mChoosed_areaId != "" && txHomeArea.getText().toString() != "选择工区") {
                    intent.setClass(getContext(), ChooseActivity.class);
                    intent.putExtra("TYPE_FLAG", ADD_NEW_SECTION);
                    intent.putExtra(ChooseActivity.CHOOSE_ROUTEID, mChoosed_routeId);
                    intent.putExtra(ChooseActivity.CHOOSE_AREAID, mChoosed_areaId);//
                    intent.putExtra(ChooseActivity.CHOOSE_SECTIONID, mChoosed_sectionId);
                    intent.putExtra(ChooseActivity.CHOOSE_TUNNELID, mChoosed_tunnelId);
                    startActivityForResult(intent, CHOOSE_BACK_HOME);
                } else {
                    Toast.makeText(getContext(), "请选择工区", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.tx_home_tunnel://选择隧道
                if (mChoosed_sectionId != "" && txHomeSection.getText().toString() != "选择区间") {
                    intent.setClass(getContext(), ChooseActivity.class);
                    intent.putExtra("TYPE_FLAG", ADD_NEW_TUNNEL);
                    intent.putExtra(ChooseActivity.CHOOSE_ROUTEID, mChoosed_routeId);
                    intent.putExtra(ChooseActivity.CHOOSE_AREAID, mChoosed_areaId);
                    intent.putExtra(ChooseActivity.CHOOSE_SECTIONID, mChoosed_sectionId);//
                    intent.putExtra(ChooseActivity.CHOOSE_TUNNELID, mChoosed_tunnelId);
                    startActivityForResult(intent, CHOOSE_BACK_HOME);
                } else {
                    Toast.makeText(getContext(), "请选择区间", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    /**
     * 销毁后停止子线程
     */
    @Override
    public void onDestroy() {

        if (mChatService != null) mChatService.stop();
        if (D) Log.e(TAG, "--- ON DESTROY ---");

        // Stop the Bluetooth chat services
        super.onDestroy();
//        如果有推送服务，注释掉下面一条。
        System.exit(0);
    }

    /**
     * Sends a message.
     *
     * @param message A string of text to send.
     */
    private void sendMessage(String message) {
        // Check that we're actually connected before trying anything
        if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
            Toast.makeText(view.getContext(), R.string.not_connected, Toast.LENGTH_SHORT).show();
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
                    if (D) Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);

                    switch (msg.arg1) {
                        case BluetoothChatService.STATE_CONNECTED:
                            txDeviceName.setText(R.string.title_connected_to);
                            txDeviceName.append(mConnectedDeviceName);
//                            关闭加载页
                            if (progressDialog != null && progressDialog.isShowing()) {
                                progressDialog.dismiss();
                                progressDialog = null;
                            }
                            /**
                             * 电量口
                             * */
                            break;
                        case BluetoothChatService.STATE_CONNECTING:
                            txDeviceName.setText(R.string.title_connecting);
                            break;
                        case BluetoothChatService.STATE_LISTEN:
                        case BluetoothChatService.STATE_NONE:
                            txDeviceName.setText(R.string.title_not_connected);
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
//                    String readMessage = new String(readBuf, 0, msg.arg1);
                    String readMessage = DataTransformation.bytesToHex(readBuf);
//                    Toast.makeText(view.getContext(), "从" + mConnectedDeviceName + "接收："
//                            + readMessage, Toast.LENGTH_SHORT).show();
                    //关闭加载页
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                        progressDialog = null;
                    }
                    showData(readMessage);
                    break;
                case MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                    Toast.makeText(view.getContext(), "已连接到 "
                            + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    txDeviceName.setText(R.string.title_connected_to);
                    txDeviceName.append(mConnectedDeviceName);
                    //关闭加载页
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                        progressDialog = null;
                    }
                    break;
                case MESSAGE_TOAST:
                    Toast.makeText(view.getContext(), msg.getData().getString(TOAST),
                            Toast.LENGTH_SHORT).show();
                    //关闭加载页
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                        progressDialog = null;
                    }
                    break;
            }
        }
    };

    /**
     * 返回连接设备名称
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_ENABLE_BT:
                if (resultCode == RESULT_OK) {
                    // Bluetooth is now enabled Launch the DeviceListActivity to see
                    // devices and do scan
                    Intent serverIntent = new Intent(view.getContext(), DeviceListActivity.class);
                    startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
                } else {
                    // User did not enable Bluetooth or an error occured
                    Toast.makeText(view.getContext(), "请手动打开蓝牙", Toast.LENGTH_SHORT)
                            .show();
                    return;
                }
                break;
            case REQUEST_CONNECT_DEVICE:
                if (resultCode != RESULT_OK) {
                    return;
                } else {
                    if (data.getStringExtra("name").length() > 0
                            && data.getStringExtra(DeviceListActivity.EXTRA_DEVICE_ADDRESS).length() > 0) {
//                        txDeviceName.setText(data.getStringExtra("name"));
                        // Get the device MAC address
                        address = data.getExtras()
                                .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                        // Get the BLuetoothDevice object
                        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
                        //加载页添加
                        if (progressDialog == null) {
                            progressDialog = CustomProgressDialog.createDialog(view.getContext());
                        }
                        progressDialog.show();
                        // Attempt to connect to the device
                        mChatService.connect(device);
                    }
                }
                break;
            case HOME_TO_CHOOSE:
                if (resultCode == RESULT_OK) {
                    Intent intent = new Intent(getContext(), ChooseActivity.class);
                    intent.putExtra(ChooseActivity.CHOOSE_ROUTEID, mChoosed_routeId);
                    intent.putExtra(ChooseActivity.CHOOSE_AREAID, mChoosed_areaId);
                    intent.putExtra(ChooseActivity.CHOOSE_SECTIONID, mChoosed_sectionId);
                    intent.putExtra(ChooseActivity.CHOOSE_TUNNELID, mChoosed_tunnelId);
                    startActivityForResult(intent, CHOOSE_BACK_HOME);
                } else {
                    Toast.makeText(getContext(), "返回值错误", Toast.LENGTH_SHORT).show();
                    return;
                }
                break;
            case CHOOSE_BACK_HOME:
                if (resultCode != RESULT_OK) {
                    return;
                } else {
                    mChoosed_routeId = data.getStringExtra(ChooseActivity.CHOOSE_ROUTEID) == null ? "" : data.getStringExtra(ChooseActivity.CHOOSE_ROUTEID);
                    mChoosed_areaId = data.getStringExtra(ChooseActivity.CHOOSE_AREAID) == null ? "" : data.getStringExtra(ChooseActivity.CHOOSE_AREAID);
                    mChoosed_sectionId = data.getStringExtra(ChooseActivity.CHOOSE_SECTIONID) == null ? "" : data.getStringExtra(ChooseActivity.CHOOSE_SECTIONID);
                    mChoosed_tunnelId = data.getStringExtra(ChooseActivity.CHOOSE_TUNNELID) == null ? "" : data.getStringExtra(ChooseActivity.CHOOSE_TUNNELID);
                    mChoosed_type = data.getIntExtra(ChooseActivity.CHOOSE_TYPE, 1000);//类型（路线、工区、区间、隧道）
                    mChoosed_edit_delete = data.getIntExtra(ChooseActivity.CHOOSE_EDIT_DELETE, 1000);//编辑与否
                    mChoosed_edit_delete_flag = data.getIntExtra(ChooseActivity.CHOOSE_EDIT_DELETE_TYPE, 1000);//
                    Log.e(TAG + "-onActivity", "mChoosed_routeId-" + mChoosed_routeId + ";mChoosed_areaId-" + mChoosed_areaId);
                    Log.e(TAG + "-onActivity", "mChoosed_sectionId-" + mChoosed_sectionId + ";mChoosed_tunnelId-" + mChoosed_tunnelId);
                    Log.e(TAG, "mChoosed_edit_delete为" + mChoosed_edit_delete + "mChoosed_edit_delete_flag为" + mChoosed_edit_delete_flag);//选择的名字

                    if (mChoosed_edit_delete == CHOOSE_BACK_HOME_EDIT_DELETE) {//此时没有做出修改操作
                        editName();
                    } else {
                        searchName();
                    }
                    /*if(mChoosed_delete!=1000){//此时没有进行删除操作

                    }*/
                    /*储存设置*/
                    if (mChoosed_sectionId.length() > 0) {
                        SharedPreferencesUtil.saveBooleanData(view.getContext(), "b_type", true);
                        SharedPreferencesUtil.saveStringData(view.getContext(), "s_type_sectionId", mChoosed_sectionId);

                    }
                }
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void editName() {
        Log.e(TAG + "-editName", "mChoosed_routeId-" + mChoosed_routeId + ";mChoosed_areaId-" + mChoosed_areaId);
        Log.e(TAG + "-editName", "mChoosed_sectionId-" + mChoosed_sectionId + ";mChoosed_tunnelId-" + mChoosed_tunnelId);
        Cursor cursor0, cursor1, cursor2, cursor3;
        cursor0 = mDb.rawQuery("SELECT* FROM ELL_Route WHERE RouteId = ?", new String[]{mChoosed_routeId});
        String routeName = "";
        while (cursor0.moveToNext()) {
            routeName = cursor0.getString(cursor0.getColumnIndex("RouteName"));
            Log.e(TAG + "-editName", "routeName为" + routeName);
        }
        cursor0.close();
        if (routeName != "") {
            txHomeRoute.setText(routeName);//路线
            cursor1 = mDb.rawQuery("SELECT* FROM ELL_Area WHERE RouteId = ? AND AreaId = ?", new String[]{mChoosed_routeId, mChoosed_areaId});
            String areaName = "";
            while (cursor1.moveToNext()) {
                areaName = cursor1.getString(cursor1.getColumnIndex("AreaName"));
                Log.e(TAG + "-editName", "areaName为" + areaName);
            }
            cursor1.close();
            if (areaName != "") {
                txHomeArea.setText(areaName);
                cursor2 = mDb.rawQuery("SELECT* FROM ELL_Section WHERE AreaId = ? AND SectionId = ?", new String[]{mChoosed_areaId, mChoosed_sectionId});
                String sectionName = "";
                while (cursor2.moveToNext()) {
                    sectionName = cursor2.getString(cursor2.getColumnIndex("SectionName"));
                    Log.e(TAG + "-editName", "sectionName为" + sectionName);
                }
                cursor2.close();
                if (sectionName != "") {
                    txHomeSection.setText(sectionName);
                    cursor3 = mDb.rawQuery("SELECT* FROM ELL_Tunnel WHERE SectionId = ? AND TunnelId = ?", new String[]{mChoosed_sectionId, mChoosed_tunnelId});
                    String tunnelName = "";
                    while (cursor3.moveToNext()) {
                        tunnelName = cursor3.getString(cursor3.getColumnIndex("TunnelName"));
                        Log.e(TAG + "-editName", "tunnelName为" + tunnelName);
                    }
                    cursor3.close();
                    if (tunnelName != "") {
                        txHomeTunnel.setText(tunnelName);
                    } else {
                        txHomeTunnel.setText("选择隧道");
                    }
                } else {
                    txHomeSection.setText("选择区间");
                    txHomeTunnel.setText("选择隧道");
                }
            } else {
                txHomeArea.setText("选择工区");
                txHomeSection.setText("选择区间");
                txHomeTunnel.setText("选择隧道");
            }
        } else {
            txHomeRoute.setText("选择路线");
            txHomeArea.setText("选择工区");
            txHomeSection.setText("选择区间");
            txHomeTunnel.setText("选择隧道");
        }
    }

    private void deleteName() {
        Log.e(TAG + "-editName", "mChoosed_routeId-" + mChoosed_routeId + ";mChoosed_areaId-" + mChoosed_areaId);
        Log.e(TAG + "-editName", "mChoosed_sectionId-" + mChoosed_sectionId + ";mChoosed_tunnelId-" + mChoosed_tunnelId);
        Cursor cursor;
        if (mChoosed_edit_delete_flag == ADD_NEW_ROUTE) {
            cursor = mDb.rawQuery("SELECT* FROM ELL_Route WHERE RouteId = ?", new String[]{mChoosed_routeId});
            String routeName = "";
            while (cursor.moveToNext()) {
                routeName = cursor.getString(cursor.getColumnIndex("RouteName"));
                Log.e(TAG + "-editName", "routeName为" + routeName);
            }
            cursor.close();
            txHomeRoute.setText(routeName);//路线
            txHomeArea.setText("选择工区");
            txHomeSection.setText("选择区间");
            txHomeTunnel.setText("选择隧道");
        } else if (mChoosed_edit_delete_flag == ADD_NEW_AREA) {
            cursor = mDb.rawQuery("SELECT* FROM ELL_Area WHERE AreaId = ?", new String[]{mChoosed_areaId});
            String areaName = "";
            while (cursor.moveToNext()) {
                areaName = cursor.getString(cursor.getColumnIndex("AreaName"));
                Log.e(TAG + "-editName", "areaName为" + areaName);
            }
            cursor.close();
            txHomeArea.setText(areaName);
            txHomeSection.setText("选择区间");
            txHomeTunnel.setText("选择隧道");
        } else if (mChoosed_edit_delete_flag == ADD_NEW_SECTION) {
            cursor = mDb.rawQuery("SELECT* FROM ELL_Section WHERE SectionId = ?", new String[]{mChoosed_sectionId});
            String sectionName = "";
            while (cursor.moveToNext()) {
                sectionName = cursor.getString(cursor.getColumnIndex("SectionName"));
                Log.e(TAG + "-editName", "sectionName为" + sectionName);
            }
            cursor.close();
            txHomeSection.setText(sectionName);
            txHomeTunnel.setText("选择隧道");
        } else if (mChoosed_edit_delete_flag == ADD_NEW_TUNNEL) {
            cursor = mDb.rawQuery("SELECT* FROM ELL_Tunnel WHERE TunnelId = ?", new String[]{mChoosed_tunnelId});
            String tunnelName = "";
            while (cursor.moveToNext()) {
                tunnelName = cursor.getString(cursor.getColumnIndex("TunnelName"));
                Log.e(TAG + "-editName", "tunnelName为" + tunnelName);
            }
            cursor.close();
            txHomeTunnel.setText(tunnelName);
        }

    }

    private void searchName() {
        Log.e(TAG + "-searchName", "Num为" + txHomeNum.getText().toString());
        Cursor cursor;
        if (mChoosed_routeId != "" && mChoosed_type == ADD_NEW_ROUTE) {
            cursor = mDb.rawQuery("SELECT* FROM ELL_Route WHERE RouteId = ?", new String[]{mChoosed_routeId});
            String routeName = "";
            while (cursor.moveToNext()) {
                routeName = cursor.getString(cursor.getColumnIndex("RouteName"));
                Log.e(TAG + "-searchName", "routeName为" + routeName);
            }
            cursor.close();
            txHomeRoute.setText(routeName);//路线
            txHomeArea.setText("选择工区");
            txHomeSection.setText("选择区间");
            txHomeTunnel.setText("选择隧道");
        } else if (mChoosed_areaId != "" && mChoosed_type == ADD_NEW_AREA) {
            cursor = mDb.rawQuery("SELECT* FROM ELL_Area WHERE AreaId = ?", new String[]{mChoosed_areaId});
            String areaName = "";
            while (cursor.moveToNext()) {
                areaName = cursor.getString(cursor.getColumnIndex("AreaName"));
                Log.e(TAG + "-searchName", "areaName为" + areaName);
            }
            cursor.close();
            txHomeArea.setText(areaName);
            txHomeSection.setText("选择区间");
            txHomeTunnel.setText("选择隧道");
        } else if (mChoosed_sectionId != "" && mChoosed_type == ADD_NEW_SECTION) {
            cursor = mDb.rawQuery("SELECT* FROM ELL_Section WHERE SectionId = ?", new String[]{mChoosed_sectionId});
            String sectionName = "";
            while (cursor.moveToNext()) {
                sectionName = cursor.getString(cursor.getColumnIndex("SectionName"));
                Log.e(TAG + "-searchName", "sectionName为" + sectionName);
            }
            cursor.close();
            txHomeSection.setText(sectionName);
            txHomeTunnel.setText("选择隧道");
        } else if (mChoosed_tunnelId != "" && mChoosed_type == ADD_NEW_TUNNEL) {
//        } else if (mChoosed_tunnelId != null&& mChoosed_type == ADD_NEW_TUNNEL) {
            cursor = mDb.rawQuery("SELECT* FROM ELL_Tunnel WHERE TunnelId = ?", new String[]{mChoosed_tunnelId});
            String tunnelName = "";
            while (cursor.moveToNext()) {
                tunnelName = cursor.getString(cursor.getColumnIndex("TunnelName"));
                Log.e(TAG + "-searchName", "tunnelName-1为" + tunnelName);
            }
            Log.e(TAG + "-searchName", "tunnelName-2为" + tunnelName);
            cursor.close();
            /*if(tunnelName.equals("")){
                txHomeTunnel.setText("无");
            }else {
                txHomeTunnel.setText(tunnelName);
            }*/
            txHomeTunnel.setText(tunnelName);
        }
        if (txHomeSection.getText().toString().equals("选择区间")) {
            SharedPreferencesUtil.saveBooleanData(view.getContext(), "b_type", false);
            SharedPreferencesUtil.saveStringData(view.getContext(), "s_type_sectionId", null);
        }
    }

    /**
     * 重写苏醒事件
     */
    @Override
    public synchronized void onResume() {
        super.onResume();
        if (D) Log.e(TAG, "+ ON RESUME +");
        clockViewByPath.performAnimation();
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
     * 显示测量返回数据
     */

    protected void showData(String data) {
        Log.e(TAG + "-showData", "Num为" + txHomeNum.getText().toString());
        if (builder != null) {
            builder.dismiss();
        }
        String showData = "";
        String showLastData = "";
        mLastDataId = "";
        String mD1_dg = null, mD1_lcz = null, mD1_gj = null, mD1_cg = null;
        String mD2_dg = null, mD2_plz = null, mD2_ngj = null;
        String mD3_hxbg = null, mD3_clxj = null;
        String mD5_fztg = null, mD5_fzpl = null;
        String mD6_dwqpd = null, mD6_gc = null;
        String mD7_gd1 = null, mD7_gd2 = null, mD7_gc = null;
        String mD8_gd = null, mD8_xj = null, mD8_gc = null;
        String mD9_spj = null, mD9_czj = null;
        String mD10_zzczd = null;
        String mD11_zzkd = null;
        final String state = data.substring(4, 6);
        int dataSize = Integer.valueOf(data.substring(2, 4), 16) * 2 + 4;
        Log.e("获得数据", data.substring(4, 6));
        switch (data.substring(4, 6)) {
            case "d1":
                Cursor mCursor1 = mDb.rawQuery("SELECT* FROM ELL_D1Data WHERE Num = ?",
                        new String[]{txHomeNum.getText().toString()});
                while (mCursor1.moveToNext()) {
                    mLastDataId = mCursor1.getString(mCursor1.getColumnIndex("DataId"));
                    Log.e(TAG + "-showData(11)", "mLastDataId为" + mLastDataId);
                    mD1_dg = mCursor1.getString(mCursor1.getColumnIndex("D1_DG"));//导高
                    mD1_lcz = mCursor1.getString(mCursor1.getColumnIndex("D1_LCZ"));//拉出值
                    mD1_gj = mCursor1.getString(mCursor1.getColumnIndex("D1_GJ"));//轨距
                    mD1_cg = mCursor1.getString(mCursor1.getColumnIndex("D1_CG"));//超高
                }
                Log.e(TAG + "-showData(12)", "mLastDataId为" + mLastDataId);
                Log.e(TAG + "-showData(13)", mD1_dg + ";" + mD1_lcz + ";" + mD1_gj + ";" + mD1_cg);

                builder.setTitle("基础测量");
                showData = DataTransformation.hexToIntForDJJ(data.substring(8, 14)) + ",";//导高
                showLastData = (mD1_dg == null ? "()," : "(" + mD1_dg + "),");
                Log.e(TAG + "--Home-11", "showlastdata--" + showLastData);
                if (data.substring(14, 16).equals("01")) {
                    showData = showData + "-";
                }
                showData = showData + DataTransformation.hexToIntForDJJ(data.substring(16, 20)) + ",";//拉出值
                showLastData = (mD1_lcz == null ? showLastData + "()," : showLastData + "(" + mD1_lcz + "),");
                Log.e(TAG + "--Home-12", "showlastdata--" + showLastData);
                if (data.substring(20, 22).equals("01")) {
                    showData = showData + "-";
                }
                showData = showData + DataTransformation.hexToIntForDJJ(data.substring(22, 26)) + ",";//轨距
                showLastData = (mD1_gj == null ? showLastData + "()," : showLastData + "(" + mD1_gj + "),");
                Log.e(TAG + "--Home-13", "showlastdata--" + showLastData);
                if (data.substring(26, 28).equals("01")) {
                    showData = showData + "-";
                }
                showData = showData + DataTransformation.hexToIntForDJJ(data.substring(28, 32));//超高
                showLastData = (mD1_cg == null ? showLastData + "()" : showLastData + "(" + mD1_cg + ")");
                Log.e(TAG + "--Home-14", "showlastdata--" + showLastData);
                builder.setMessage(data.substring(0, dataSize));
                break;
            case "d2":
                Cursor mCursor21 = mDb.rawQuery("SELECT* FROM ELL_D2Data",
                        new String[]{});
                while (mCursor21.moveToNext()) {
                    String num = mCursor21.getString(mCursor21.getColumnIndex("Num"));
                    Log.e(TAG + "-d2", "num为" + num);
                }
                Cursor mCursor2 = mDb.rawQuery("SELECT* FROM ELL_D2Data WHERE Num = ?",
                        new String[]{txHomeNum.getText().toString()});
                while (mCursor2.moveToNext()) {
                    mLastDataId = mCursor2.getString(mCursor2.getColumnIndex("DataId"));
                    Log.e(TAG + "-showData(21)", "mLastDataId为" + mLastDataId);
                    mD2_dg = mCursor2.getString(mCursor2.getColumnIndex("D2_DG"));//导高
                    mD2_plz = mCursor2.getString(mCursor2.getColumnIndex("D2_PLZ"));//偏离值
                    mD2_ngj = mCursor2.getString(mCursor2.getColumnIndex("D2_NGJ"));//内轨距
                }
                Log.e(TAG + "-showData(22)", "mLastDataId为" + mLastDataId);
                Log.e(TAG + "-showData(23)", mD2_dg + ";" + mD2_plz + ";" + mD2_ngj);

                builder.setTitle("线岔中心测量");
                showData = DataTransformation.hexToIntForDJJ(data.substring(8, 14)) + ",";//导高
                showLastData = (mD2_dg == null ? "()," : "(" + mD2_dg + "),");
                Log.e(TAG + "--Home-21", "showlastdata--" + showLastData);
                if (data.substring(14, 16).equals("01")) {
                    showData = showData + "-";
                }
                showData = showData + DataTransformation.hexToIntForDJJ(data.substring(16, 20)) + ",";//偏离值
                showLastData = (mD2_plz == null ? showLastData + "()," : showLastData + "(" + mD2_plz + "),");
                Log.e(TAG + "--Home-22", "showlastdata--" + showLastData);
                showData = showData + DataTransformation.hexToIntForDJJ(data.substring(20, 26));//内轨距
                showLastData = (mD2_ngj == null ? showLastData + "()" : showLastData + "(" + mD2_ngj + ")");
                Log.e(TAG + "--Home-23", "showlastdata--" + showLastData);
                builder.setMessage(data.substring(0, dataSize));
                break;
            case "d3":
                Cursor mCursor3 = mDb.rawQuery("SELECT* FROM ELL_D3Data WHERE Num = ?",
                        new String[]{txHomeNum.getText().toString()});
                while (mCursor3.moveToNext()) {
                    mLastDataId = mCursor3.getString(mCursor3.getColumnIndex("DataId"));
                    Log.e(TAG + "-showData(31)", "mLastDataId为" + mLastDataId);
                    mD3_hxbg = mCursor3.getString(mCursor3.getColumnIndex("D3_HXBG"));//红线标高
                    mD3_clxj = mCursor3.getString(mCursor3.getColumnIndex("D3_CLXJ"));//测量限界
                }
                Log.e(TAG + "-showData(32)", "mLastDataId为" + mLastDataId);
                Log.e(TAG + "-showData(33)", mD3_hxbg + ";" + mD3_clxj);

                builder.setTitle("限界测量");
                showData = DataTransformation.hexToIntForDJJ(data.substring(8, 14)) + ",";//红线标高
                showLastData = (mD3_hxbg == null ? "()," : "(" + mD3_hxbg + "),");
                Log.e(TAG + "--Home-31", "showlastdata--" + showLastData);
                showData = showData + DataTransformation.hexToIntForDJJ(data.substring(14, 20));//侧面限界
                showLastData = (mD3_clxj == null ? showLastData + "()" : showLastData + "(" + mD3_clxj + ")");
                Log.e(TAG + "--Home-32", "showlastdata--" + showLastData);
                builder.setMessage(data.substring(0, dataSize));
                break;
            case "d5":
                Cursor mCursor5 = mDb.rawQuery("SELECT* FROM ELL_D5Data WHERE Num = ?",
                        new String[]{txHomeNum.getText().toString()});
                while (mCursor5.moveToNext()) {
                    mLastDataId = mCursor5.getString(mCursor5.getColumnIndex("DataId"));
                    mD5_fztg = mCursor5.getString(mCursor5.getColumnIndex("D5_FZTG"));//非支抬高
                    mD5_fzpl = mCursor5.getString(mCursor5.getColumnIndex("D5_FZPL"));//非支偏离
                }
                Log.e(TAG + "-showData(52)", "mLastDataId为" + mLastDataId);
                Log.e(TAG + "-showData(53)", mD5_fztg + ";" + mD5_fzpl);

                builder.setTitle("非支测量");
                if (data.substring(8, 10).equals("01")) {
                    showData = "-";
                }
                showData = showData + DataTransformation.hexToIntForDJJ(data.substring(10, 14)) + ",";//非支抬高
                showLastData = (mD5_fztg == null ? "()," : "(" + mD5_fztg + "),");
                Log.e(TAG + "--Home-51", "showlastdata--" + showLastData);
                showData = showData + DataTransformation.hexToIntForDJJ(data.substring(14, 20));//非支偏离
                showLastData = (mD5_fzpl == null ? showLastData + "()" : showLastData + "(" + mD5_fzpl + ")");
                Log.e(TAG + "--Home-52", "showlastdata--" + showLastData);
                builder.setMessage(data.substring(0, dataSize));
                break;
            case "d6":
                Cursor mCursor6 = mDb.rawQuery("SELECT* FROM ELL_D6Data WHERE Num = ?",
                        new String[]{txHomeNum.getText().toString()});
                while (mCursor6.moveToNext()) {
                    mLastDataId = mCursor6.getString(mCursor6.getColumnIndex("DataId"));
                    Log.e(TAG + "-showData(61)", "mLastDataId为" + mLastDataId);
                    mD6_dwqpd = mCursor6.getString(mCursor6.getColumnIndex("D6_DWQPD"));//定位器坡度
                    mD6_gc = mCursor6.getString(mCursor6.getColumnIndex("D6_GC"));//高差
                }
                Log.e(TAG + "-showData(62)", "mLastDataId为" + mLastDataId);
                Log.e(TAG + "-showData(63)", mD6_dwqpd + ";" + mD6_gc);

                builder.setTitle("定位器测量");
                showData = DataTransformation.hexToIntForDJJ(data.substring(8, 14)) + ",";//定位器坡度
                showLastData = (mD6_dwqpd == null ? "()," : "(" + mD6_dwqpd + "),");
                Log.e(TAG + "--Home-61", "showlastdata--" + showLastData);
                showData = showData + DataTransformation.hexToIntForDJJ(data.substring(14, 20));//高差
                showLastData = (mD6_gc == null ? showLastData + "()" : showLastData + "(" + mD6_gc + ")");
                Log.e(TAG + "--Home-62", "showlastdata--" + showLastData);
                builder.setMessage(data.substring(0, dataSize));
                break;
            case "d7":
                Cursor mCursor7 = mDb.rawQuery("SELECT* FROM ELL_D7Data WHERE Num = ?",
                        new String[]{txHomeNum.getText().toString()});
                while (mCursor7.moveToNext()) {
                    mLastDataId = mCursor7.getString(mCursor7.getColumnIndex("DataId"));
                    Log.e(TAG + "-showData(71)", "mLastDataId为" + mLastDataId);
                    mD7_gd1 = mCursor7.getString(mCursor7.getColumnIndex("D7_GD1"));//高度1
                    mD7_gd2 = mCursor7.getString(mCursor7.getColumnIndex("D7_GD2"));//高度2
                    mD7_gc = mCursor7.getString(mCursor7.getColumnIndex("D7_GC"));//高差
                }
                Log.e(TAG + "-showData(72)", "mLastDataId为" + mLastDataId);
                Log.e(TAG + "-showData(73)", mD7_gd1 + ";" + mD7_gd2 + ";" + mD7_gc);

                builder.setTitle("承力索高差测量");
                showData = DataTransformation.hexToIntForDJJ(data.substring(8, 14)) + ",";//高度1
                showLastData = (mD7_gd1 == null ? "()," : "(" + mD7_gd1 + "),");
                Log.e(TAG + "--home-71", "showlastdata--" + showLastData);
                showData = showData + DataTransformation.hexToIntForDJJ(data.substring(14, 20)) + ",";//高度2
                showLastData = (mD7_gd2 == null ? showLastData + "()," : showLastData + "(" + mD7_gd2 + "),");
                Log.e(TAG + "--home-72", "showlastdata--" + showLastData);
                showData = showData + DataTransformation.hexToIntForDJJ(data.substring(20, 26));//高差
                showLastData = (mD7_gc == null ? showLastData + "()" : showLastData + "(" + mD7_gc + ")");
                Log.e(TAG + "--home-73", "showlastdata--" + showLastData);
                builder.setMessage(data.substring(0, dataSize));
                break;
            case "d8":
                Cursor mCursor8 = mDb.rawQuery("SELECT* FROM ELL_D8Data WHERE Num = ?",
                        new String[]{txHomeNum.getText().toString()});
                while (mCursor8.moveToNext()) {
                    mLastDataId = mCursor8.getString(mCursor8.getColumnIndex("DataId"));
                    Log.e(TAG + "-showData(81)", "mLastDataId为" + mLastDataId);
                    mD8_gd = mCursor8.getString(mCursor8.getColumnIndex("D8_GD"));//高度
                    mD8_xj = mCursor8.getString(mCursor8.getColumnIndex("D8_XJ"));//线距
                    mD8_gc = mCursor8.getString(mCursor8.getColumnIndex("D8_GC"));//高差
                }
                Log.e(TAG + "-showData(82)", "mLastDataId为" + mLastDataId);
                Log.e(TAG + "-showData(83)", mD8_gd + ";" + mD8_xj + ";" + mD8_gc);

                builder.setTitle("500mm处高差测量");
                showData = DataTransformation.hexToIntForDJJ(data.substring(8, 14)) + ",";//高度1
                showLastData = (mD8_gd == null ? "()," : "(" + mD8_gd + "),");
                Log.e(TAG + "--Home-81", "showlastdata--" + showLastData);
                showData = showData + DataTransformation.hexToIntForDJJ(data.substring(14, 20)) + ",";//线距
                showLastData = (mD8_xj == null ? showLastData + "()," : showLastData + "(" + mD8_xj + "),");
                Log.e(TAG + "--Home-82", "showlastdata--" + showLastData);
                if (data.substring(20, 22).equals("01")) {
                    showData = showData + "-";
                }
                showData = showData + DataTransformation.hexToIntForDJJ(data.substring(22, 26));//高差
                showLastData = (mD8_gc == null ? showLastData + "()" : showLastData + "(" + mD8_gc + ")");
                Log.e(TAG + "--Home-83", "showlastdata--" + showLastData);
                builder.setMessage(data.substring(0, dataSize));
                break;
            case "d9":
                Cursor mCursor9 = mDb.rawQuery("SELECT* FROM ELL_D9Data WHERE Num = ?",
                        new String[]{txHomeNum.getText().toString()});
                while (mCursor9.moveToNext()) {
                    mLastDataId = mCursor9.getString(mCursor9.getColumnIndex("DataId"));
                    Log.e(TAG + "-showData(91)", "mLastDataId为" + mLastDataId);
                    mD9_spj = mCursor9.getString(mCursor9.getColumnIndex("D9_SPJ"));//水平距
                    mD9_czj = mCursor9.getString(mCursor9.getColumnIndex("D9_CZJ"));//垂直距
                }
                Log.e(TAG + "-showData(92)", "mLastDataId为" + mLastDataId);
                Log.e(TAG + "-showData(93)", mD9_spj + ";" + mD9_czj);

                builder.setTitle("自由测量");
                showData = DataTransformation.hexToIntForDJJD9(data.substring(8, 14)) + ",";//水平距
                showLastData = (mD9_spj == null ? "()," : "(" + mD9_spj + "),");
                Log.e(TAG + "--Home-91", "showlastdata--" + showLastData);
                showData = showData + DataTransformation.hexToIntForDJJD9(data.substring(14, 20));//垂直距
                showLastData = (mD9_czj == null ? showLastData + "()" : showLastData + "(" + mD9_czj + ")");
                Log.e(TAG + "--Home-92", "showlastdata--" + showLastData);
                builder.setMessage(data.substring(0, dataSize));
                break;
            case "dc":
                Cursor mCursor10 = mDb.rawQuery("SELECT* FROM ELL_DCData WHERE Num = ?",
                        new String[]{txHomeNum.getText().toString()});
                while (mCursor10.moveToNext()) {
                    mLastDataId = mCursor10.getString(mCursor10.getColumnIndex("DataId"));
                    Log.e(TAG + "-showData(101)", "mLastDataId为" + mLastDataId);
                    mD10_zzczd = mCursor10.getString(mCursor10.getColumnIndex("DC_ZZCZD"));//支柱垂直度
                }
                Log.e(TAG + "-showData(102)", "mD10_zzczd为" + mD10_zzczd);
                Log.e(TAG + "-showData(103)", "mD10_zzczd为" + mD10_zzczd);

                builder.setTitle("垂直测量");
                showData = DataTransformation.hexToIntForDJJDc(data.substring(8, 12));//支柱垂直度
                showLastData = (mD10_zzczd == null ? "" : "(" + mD10_zzczd + ")");
                Log.e(TAG + "--Home-101", "showlastdata--" + showLastData);
                builder.setMessage(data.substring(0, dataSize));
                break;
            case "de":
                Cursor mCursor11 = mDb.rawQuery("SELECT* FROM ELL_DEData WHERE Num = ?",
                        new String[]{txHomeNum.getText().toString()});
                while (mCursor11.moveToNext()) {
                    mLastDataId = mCursor11.getString(mCursor11.getColumnIndex("DataId"));
                    Log.e(TAG + "-showData(111)", "mLastDataId为" + mLastDataId);
                    mD11_zzkd = mCursor11.getString(mCursor11.getColumnIndex("DE_ZZKD"));//支柱跨度
                }
                Log.e(TAG + "-showData(112)", "mLastDataId为" + mLastDataId);
                Log.e(TAG + "-showData(113)", "mD11_zzkd为" + mD11_zzkd);

                builder.setTitle("跨距测量");
                showData = DataTransformation.hexToIntForDJJD9(data.substring(10, 14));//支柱跨距
                showLastData = (mD11_zzkd == null ? "" : "(" + mD11_zzkd + ")");
                Log.e(TAG + "--Home-111", "showlastdata--" + showLastData);
                builder.setMessage(data.substring(0, dataSize));
                break;
            default:
                builder.setTitle("显示未知数据");
                builder.setMessage(data.substring(0, dataSize));
                break;

        }
        builder.setData(state, showData, showLastData);


        final String finalShowData = showData;
        builder.setOnNegativeListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder.dismiss();
                String note = builder.getNote();
                saveDataDialog(state, finalShowData, note);
            }
        });
        builder.setOnPositiveListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder.dismiss();
                Toast.makeText(view.getContext(), "已取消", Toast.LENGTH_SHORT).show();
            }
        });
        builder.show();
    }

    /**
     * 保存数据设置
     */
    protected void saveDataDialog(final String state, final String data, final String note) {
        Log.e(TAG + "-saveDataDialog", "Num为" + txHomeNum.getText().toString());
        if (mChoosed_sectionId.length() > 0 && txHomeNum.getText().length() > 0) {
            saveData(state, data, note);


        } else {
            typeDialog = new TypeDialog(view.getContext());
            typeDialog.setType(mChoosed_sectionId,mChoosed_tunnelId);
            typeDialog.setOnNegativeListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    String[] strings = typeDialog.getType();
                    if (strings == null) {
                        Toast.makeText(view.getContext(), "请完善设置", Toast.LENGTH_SHORT).show();

                    } else {
                        mChoosed_sectionId = strings[0];//区间ID
                        mChoosed_tunnelId = strings[1];//隧道ID
                        Log.e(TAG + "-saveDataDialog(1)", "string[1]为" + strings[1]);
                        sxx = strings[2];//上下行
                        txHomeNum.setText(strings[3]);
                        SharedPreferencesUtil.saveBooleanData(view.getContext(), "b_type", true);
                        SharedPreferencesUtil.saveStringData(view.getContext(), "s_type_sectionId", mChoosed_sectionId);
                        setSaveType(state, data, note);
                    }
                }
            });
            typeDialog.setOnPositiveListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    typeDialog.dismiss();
                    Toast.makeText(view.getContext(), "已取消", Toast.LENGTH_SHORT).show();
                }
            });
            typeDialog.show();
//        } else {
//            typeDialog = new TypeDialog(view.getContext());
//            typeDialog.setOnNegativeListener(new View.OnClickListener() {
//
//                @Override
//                public void onClick(View v) {
//                    String[] strings = typeDialog.getType();
//                    if (strings == null) {
//                        Toast.makeText(view.getContext(), "请完善设置", Toast.LENGTH_SHORT).show();
//
//                    } else {
//                        mChoosed_sectionId = strings[0];//区间ID
//                        mChoosed_tunnelId = strings[1];//隧道ID
//                        Log.e(TAG + "-saveDataDialog(2)", "string[1]为" + strings[1]);
//                        sxx = strings[2];//上下行
//                        txHomeNum.setText(strings[3]);
//                        SharedPreferencesUtil.saveBooleanData(view.getContext(), "b_type", true);
//                        SharedPreferencesUtil.saveStringData(view.getContext(), "s_type_sectionId", mChoosed_sectionId);
//                        setSaveType(state, data, note);
//                    }
//                }
//            });
//            typeDialog.setOnPositiveListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    typeDialog.dismiss();
//                    Toast.makeText(view.getContext(), "已取消", Toast.LENGTH_SHORT).show();
//                }
//            });
//            typeDialog.show();
        }
    }

    /**
     * 暂停处理
     */
    @Override
    public void onPause() {
        super.onPause();
        clockViewByPath.cancelAnimation();
    }

    /**
     * 添加新路线弹出框
     */
    protected void addNew(final int state) {
        final DialogNormalDialog dialog = new DialogNormalDialog(view.getContext());
        final EditText editText = (EditText) dialog.getEditText();//方法在CustomDialog中实现
        dialog.setDialog(state);
        dialog.setMessage("");
        dialog.setOnPositiveListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
            }
        });
        dialog.setOnNegativeListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText.getText().length() > 0) {
                    if (state == ADD_NEW_ROUTE) {
                        saveNewRoute(state, editText.getText().toString(), dialog.getParentId(state));
//                        saveEmptyTunnel();
                        dialog.dismiss();
                    } else {
                        if (dialog.getParentId(state).length() > 0) {
                            saveNewRoute(state, editText.getText().toString(), dialog.getParentId(state));
                            dialog.dismiss();
                        } else {
                            Toast.makeText(view.getContext(), "请配置好上级选项", Toast.LENGTH_SHORT).show();
                        }
                    }

                } else {
                    Toast.makeText(view.getContext(), "请输入", Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialog.show();
    }

    /*private void saveEmptyTunnel(){
        ContentValues values = new ContentValues();
        String Id = UUID.randomUUID().toString();//各种数据的主键
        if(!mAdded){
            Log.e(TAG+"-saveNewRoute","mAdded"+mAdded);
            mAdded = true;
            values.put("TunnelId", Id);
            values.put("TunnelName", "无");
            values.put("SectionId", ParentId);
            mDb.replace("ELL_Tunnel", null, values);
        }
    }*/

    /**
     * 新路线等写入数据库
     */
    protected void saveNewRoute(int state, String name, String ParentId) {
        Log.e(TAG + "-saveNewRoute", "Num为" + txHomeNum.getText().toString());
        // 初始化，只需要调用一次
//        AssetsDatabaseManager.initManager(view.getContext());
//        // 获取管理对象，因为数据库需要通过管理对象才能够获取
//        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
//        // 通过管理对象获取数据库
//        SQLiteDatabase db = mg.getDatabase("DJJ-8data.db");
        ContentValues values = new ContentValues();
        ContentValues values2 = new ContentValues();
        String Id = UUID.randomUUID().toString();//各种数据的主键
        String Id2 = UUID.randomUUID().toString();//各种数据的主键
        try {
            switch (state) {
                case ADD_NEW_ROUTE://新路线写入数据库
                    values.put("RouteId", Id);
                    values.put("RouteName", name);
                    mDb.replace("ELL_Route", null, values);
                    Log.e(TAG+"-saveNewRoute","新添数据:"+values.toString());
                    Toast.makeText(view.getContext(), "已新添路线：" + name, Toast.LENGTH_SHORT).show();
                    break;
                case ADD_NEW_AREA://新工区写入数据库
                    values.put("AreaId", Id);
                    values.put("AreaName", name);
                    values.put("RouteId", ParentId);
                    mDb.replace("ELL_Area", null, values);
                    Log.e(TAG+"-saveNewRoute","新添数据:"+values.toString());
                    Toast.makeText(view.getContext(), "已新添工区：" + name, Toast.LENGTH_SHORT).show();
                    break;
                case ADD_NEW_SECTION://新区间写入数据库
                    values.put("SectionId", Id);
                    values.put("SectionName", name);
                    values.put("AreaId", ParentId);
                    mDb.replace("ELL_Section", null, values);
//                    if(!mAdded){
                    Log.e(TAG + "-saveNewRoute", "mAdded为" + mAdded);
//                        mAdded = true;
                    values2.put("TunnelId", Id2);
                    values2.put("TunnelName", "无");
                    values2.put("SectionId", Id);
                    mDb.replace("ELL_Tunnel", null, values2);
//                    }
                    Log.e(TAG+"-saveNewRoute","新添数据:"+ values.toString());
                    Toast.makeText(view.getContext(), "已新添区间：" + name, Toast.LENGTH_SHORT).show();
                    break;
                case ADD_NEW_TUNNEL://新隧道写入数据库
                    values.put("TunnelId", Id);
                    values.put("TunnelName", name);
                    values.put("SectionId", ParentId);
                    mDb.replace("ELL_Tunnel", null, values);
                    Log.e(TAG+"-saveNewRoute","新添数据:"+values.toString());
                    Toast.makeText(view.getContext(), "已新添隧道：" + name, Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            Log.e("新添数据库报错", e.toString(), e);
            Toast.makeText(view.getContext(), "未知错误", Toast.LENGTH_SHORT).show();

        }

    }


    /**
     * 保存数据时配置路线等
     */
    private void setSaveType(String state, String data, String note) {
        Log.e(TAG + "-setSaveType", "Num为" + txHomeNum.getText().toString());
        Cursor cursor;
        /*设置区间*/
        cursor = mDb.rawQuery("SELECT* FROM ELL_Section WHERE SectionId = ?", new String[]{mChoosed_sectionId});
        String sectionName = "";
        cursor.moveToFirst();
        sectionName = cursor.getString(cursor.getColumnIndex("SectionName"));
        mChoosed_areaId = cursor.getString(cursor.getColumnIndex("AreaId"));
        /*设置工区*/
        cursor = mDb.rawQuery("SELECT* FROM ELL_Area WHERE AreaId = ?", new String[]{mChoosed_areaId});
        String areaName = "";
        cursor.moveToFirst();
        areaName = cursor.getString(cursor.getColumnIndex("AreaName"));
        mChoosed_routeId = cursor.getString(cursor.getColumnIndex("RouteId"));
        /*设置路线*/
        cursor = mDb.rawQuery("SELECT* FROM ELL_Route WHERE RouteId = ?", new String[]{mChoosed_routeId});
        String routeName = "";
        cursor.moveToFirst();
        routeName = cursor.getString(cursor.getColumnIndex("RouteName"));
        /*设置隧道*/
        if (mChoosed_tunnelId.length() > 0) {
            cursor = mDb.rawQuery("SELECT* FROM ELL_Tunnel WHERE TunnelId = ?", new String[]{mChoosed_tunnelId});
            String tunnelName = "";
            cursor.moveToFirst();
            tunnelName = cursor.getString(cursor.getColumnIndex("TunnelName"));
            Log.e(TAG + "-setSaveType", "tunnelName为" + tunnelName);
            txHomeTunnel.setText(tunnelName);
        }
        cursor.close();
        txHomeSection.setText(sectionName);
        txHomeArea.setText(areaName);
        txHomeRoute.setText(routeName);
        /*设置上下行*/
        if (sxx.equals("下行")) {
            Radio1.setChecked(false);
            Radio2.setChecked(true);
        }
        /*设置完成，关闭弹出框并保存*/
        if (typeDialog != null) {
            saveDataDialog(state, data, note);
            typeDialog.dismiss();
        }

    }


    /**
     * 保存数据
     */
    protected void saveData(final String state, final String data, final String note) {
        Log.e(TAG + "-saveData", "Num为" + txHomeNum.getText().toString());
        String d11, d12, d13, d14;
        String d21, d22, d23;
        String d31, d32;
        String d51, d52;
        String d61, d62;
        String d71, d72, d73;
        String d81, d82, d83;
        String d91, d92;
        String dc1;
        String de1;
        ContentValues values = new ContentValues();
        String Id = UUID.randomUUID().toString();//各种数据的主键
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss", Locale.CHINA);// HH:mm:ss
        //获取当前时间
        Date date = new Date(System.currentTimeMillis());
        values.put("DataId", txHomeNum.getText().toString());
        values.put("RouteId", mChoosed_routeId);
        values.put("AreaId", mChoosed_areaId);
        values.put("SectionId", mChoosed_sectionId);
        values.put("TunnelId", mChoosed_tunnelId);
        values.put("Direction", sxx);
        values.put("Date", simpleDateFormat.format(date));
        mDb.replace("ELL_PoleData", null, values);//保存杆号信息
//        values.put("Note",note);
        String[] strings = data.split(",");
        try {
            switch (state) {
                case "d1"://基础测量
                    values = new ContentValues();
                    values.put("DataId", Id);
                    values.put("Date", simpleDateFormat.format(date));
                    values.put("Note", note);
                    values.put("Num", txHomeNum.getText().toString());
                    values.put("D1_DG", strings[0]);
                    values.put("D1_LCZ", strings[1]);
                    values.put("D1_GJ", strings[2]);
                    values.put("D1_CG", strings[3]);
                    mDb.replace("ELL_D1Data", null, values);//保存基础测量
                    break;
                case "d2"://线岔中心测量
                    values = new ContentValues();
                    values.put("DataId", Id);
                    values.put("Date", simpleDateFormat.format(date));
                    values.put("Note", note);
                    values.put("Num", txHomeNum.getText().toString());
                    values.put("D2_DG", strings[0]);
                    values.put("D2_PLZ", strings[1]);
                    values.put("D2_NGJ", strings[2]);
                    mDb.replace("ELL_D2Data", null, values);//保存线岔中心测量
                    break;
                case "d3"://限界测量
                    values = new ContentValues();
                    values.put("DataId", Id);
                    values.put("Date", simpleDateFormat.format(date));
                    values.put("Note", note);
                    values.put("Num", txHomeNum.getText().toString());
                    values.put("D3_HXBG", strings[0]);
                    values.put("D3_CLXJ", strings[1]);
                    mDb.replace("ELL_D3Data", null, values);//保存限界测量
                    break;
                case "d5"://非支测量
                    values = new ContentValues();
                    values.put("DataId", Id);
                    values.put("Date", simpleDateFormat.format(date));
                    values.put("Note", note);
                    values.put("Num", txHomeNum.getText().toString());
                    values.put("D5_FZTG", strings[0]);
                    values.put("D5_FZPL", strings[1]);
                    mDb.replace("ELL_D5Data", null, values);//保存非支测量
                    break;
                case "d6"://定位器测量
                    values = new ContentValues();
                    values.put("DataId", Id);
                    values.put("Date", simpleDateFormat.format(date));
                    values.put("Note", note);
                    values.put("Num", txHomeNum.getText().toString());
                    values.put("D6_DWQPD", strings[0]);
                    values.put("D6_GC", strings[1]);
                    mDb.replace("ELL_D6Data", null, values);//保存定位器测量
                    break;
                case "d7"://承力索高差测量
                    values = new ContentValues();
                    values.put("DataId", Id);
                    values.put("Date", simpleDateFormat.format(date));
                    values.put("Note", note);
                    values.put("Num", txHomeNum.getText().toString());
                    values.put("D7_GD1", strings[0]);
                    values.put("D7_GD2", strings[1]);
                    values.put("D7_GC", strings[2]);
                    mDb.replace("ELL_D7Data", null, values);//保存承力索高差测量
                    break;
                case "d8"://500mm处高差测量
                    values = new ContentValues();
                    values.put("DataId", Id);
                    values.put("Date", simpleDateFormat.format(date));
                    values.put("Note", note);
                    values.put("Num", txHomeNum.getText().toString());
                    values.put("D8_GD1", strings[0]);
                    values.put("D8_XJ", strings[1]);
                    values.put("D8_GC", strings[2]);
                    mDb.replace("ELL_D8Data", null, values);//保存500mm处高差测量
                    break;
                case "d9"://自由测量
                    values = new ContentValues();
                    values.put("DataId", Id);
                    values.put("Date", simpleDateFormat.format(date));
                    values.put("Note", note);
                    values.put("Num", txHomeNum.getText().toString());
                    values.put("D9_SPJ", strings[0]);
                    values.put("D9_CZJ", strings[1]);
                    mDb.replace("ELL_D9Data", null, values);//保存自由测量
                    break;
                case "dc"://垂直测量
                    values = new ContentValues();
                    values.put("DataId", Id);
                    values.put("Date", simpleDateFormat.format(date));
                    values.put("Note", note);
                    values.put("Num", txHomeNum.getText().toString());
                    values.put("DC_ZZCZD", strings[0]);
                    mDb.replace("ELL_DCData", null, values);//保存垂直测量
                    break;
                case "de"://跨距测量
                    values = new ContentValues();
                    values.put("DataId", Id);
                    values.put("Date", simpleDateFormat.format(date));
                    values.put("Note", note);
                    values.put("Num", txHomeNum.getText().toString());
                    values.put("DE_ZZKD", strings[0]);
                    mDb.replace("ELL_DEData", null, values);//保存跨距测量
                    break;
                default:
                    break;
            }
            Toast.makeText(view.getContext(), "已保存" + data, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e("新添数据库报错", e.toString(), e);
            Toast.makeText(view.getContext(), "未知错误", Toast.LENGTH_SHORT).show();
        }

    }
}
