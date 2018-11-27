package com.jinan.ladongjiguan.anjiantong.activity;


import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.support.v4.content.ContextCompat;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mjdev.libaums.UsbMassStorageDevice;
import com.github.mjdev.libaums.fs.FileSystem;
import com.github.mjdev.libaums.fs.UsbFile;
import com.github.mjdev.libaums.fs.UsbFileOutputStream;
import com.github.mjdev.libaums.partition.Partition;
import com.jinan.ladongjiguan.anjiantong.R;
import com.jinan.ladongjiguan.anjiantong.utils.CommonUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DataTransmissionFileActivity extends BaseActivity implements View.OnClickListener, View.OnTouchListener {


    @BindView(R.id.examine_page_back)
    LinearLayout examinePageBack;
    @BindView(R.id.title_layout)
    TextView titleLayout;
    @BindView(R.id.e_car_num_6)
    Spinner eCarNum6;
    @BindView(R.id.list_01)
    ListView list01;
    /**
     * 各类文件目录
     * */
    private String s_m_path="";
    private String s_path="";
    private String[] strings_path = {"","xcjcfa","xcjcjl","xcclcsjds","zlxqzgzls","xzcfjdsdw","xzcfjdsgr"};
    private UsbFile cFolder;//当前目录
    private List<UsbFile> usbFiles = new ArrayList<>();
    private UsbMassStorageDevice[] storageDevices;
    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
    private ExecutorService executorService;
    /**
     * 导出文件广播监听
     * */
    private BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                case ACTION_USB_PERMISSION://接受到自定义广播
                    UsbDevice usbDevice = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {  //允许权限申请
                        if (usbDevice != null) {  //Do something
                            Toast.makeText(DataTransmissionFileActivity.this,"用户已授权，可以进行操作", Toast.LENGTH_SHORT).show();
                            readDevice(getUsbMass(usbDevice));
                        } else {
//                            Toast.makeText(DataTransmissionFileActivity.this,"未获取到设备信息", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(DataTransmissionFileActivity.this,"用户未授权，读取失败", Toast.LENGTH_SHORT).show();

                    }
                    break;
                case UsbManager.ACTION_USB_DEVICE_ATTACHED://接收到存储设备插入广播
                    UsbDevice device_add = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (device_add != null) {
                        Toast.makeText(DataTransmissionFileActivity.this,"存储设备已插入", Toast.LENGTH_SHORT).show();
                        redDeviceList();
                    }
                    break;
                case UsbManager.ACTION_USB_DEVICE_DETACHED://接收到存储设备拔出广播
                    UsbDevice device_remove = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (device_remove != null) {
                        Toast.makeText(DataTransmissionFileActivity.this,"存储设备已拔出", Toast.LENGTH_SHORT).show();
                        usbFiles.clear();//清除
                        cFolder = null;
                    }
                    break;
            }
        }
    };
    @Override
    protected void initView() {
        setContentView(R.layout.data_transmission_file_layout);
        ButterKnife.bind(this);
        switch (getIntent().getStringExtra("state")){
            case "zs":
                titleLayout.setText("正式文书");
                s_m_path ="/sdcard/00_zhengshiwenshu/zhengshi_";
                break;
            case "ls":
                titleLayout.setText("临时文书");
                s_m_path ="/sdcard/00_linshiwenshu/linshi_";
                break;
            default:
                break;
        }
        /**
         * 点击事件
         * */
        examinePageBack.setOnClickListener(this);
        /**
         * 触摸事件
         * */
        examinePageBack.setOnTouchListener(this);
    }

    @Override
    protected void init() {
        eCarNum6.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position!=0){
                    s_path = s_m_path +strings_path[position];
                    getFlieList(s_path);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        /*读取外接U盘*/
        redDeviceList();//一开始就需要尝试读取一次
        registerReceiver();
        executorService = Executors.newCachedThreadPool();//30大小的线程池
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


    /**
     * 重写返回键
     * */
    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.zoomin, R.anim.zoomout);

    }
    /**
     * 获取文件列表公共类
     */
    protected void getFlieList(String s) {
        //打开指定目录，显示项目说明书列表，供用户选择

        File specItemDir = new File(s);
        if (!specItemDir.exists()) {
            specItemDir.mkdir();
        }

        if (!specItemDir.exists()) {
            Toast.makeText(this, "没有找到文件", Toast.LENGTH_SHORT).show();
        } else {
            //取出文件列表：
            final File[] files = specItemDir.listFiles();
            final List<HashMap<String, Object>> specs = new ArrayList<>();
            int seq = 0;
            for(File spec : files){
                long time=files[seq].lastModified();
                SimpleDateFormat formatter = new
                        SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String result=formatter.format(time);
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("seq", seq);
                hashMap.put("name", spec.getName());
                hashMap.put("Path", files[seq].getAbsolutePath());
                hashMap.put("time", result);
                seq++;
                specs.add(hashMap);
            }
            Collections.reverse(specs);//list倒序
            SimpleAdapter adapter =
                    new SimpleAdapter(
                            this,
                            specs,
                            R.layout.flie_list_bean_item,
                            new String[]{"name","time"},
                            new int[]{R.id.item_search_tv_title,R.id.item_search_tv_time}
                    );

            list01.setAdapter(adapter);

            list01.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1,int position, long aid) {
//                    String filePath = files[position].getAbsolutePath();
                    String filePath = specs.get(position).get("Path").toString();
                    CommonUtils.doOpenPdf(DataTransmissionFileActivity.this,filePath);
                }
            });
            list01.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {//长按选择文件或文件夹
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//                    final File file = files[position];
                    final File file = new File(specs.get(position).get("Path").toString());
                    if (cFolder == null) {//表示当前目录根本不存在
                        Toast.makeText(DataTransmissionFileActivity.this, "未检测到有U盘接入", Toast.LENGTH_SHORT).show();
                    } else {
                        if (usbFiles != null) {
                            for (UsbFile uf : usbFiles) {
                                if (uf.getName().equals(file.getName())) {
                                    if (uf.isDirectory()) {
                                        Toast.makeText(DataTransmissionFileActivity.this, "目录" + file.getName() + "已存在，请删除后再试", Toast.LENGTH_SHORT).show();

                                        break;
                                    } else {
                                        if (uf.getLength() == file.length()) {
                                            Toast.makeText(DataTransmissionFileActivity.this, file.getName() + "已存在，不需要写入", Toast.LENGTH_SHORT).show();
                                            break;
                                        }
                                    }
                                }
                            }
                        }

                        executorService.execute(new Runnable() {
                            @Override
                            public void run() {
                                readSDFile(file, cFolder);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(DataTransmissionFileActivity.this, file.getName() + "已写入到U盘中", Toast.LENGTH_SHORT).show();

                                    }
                                });
                            }
                        });
                    }
                    return true;
                }
            });
        }

    }

    /**
     * 导出文件到U盘
     * */
    private void redDeviceList() {
        UsbManager usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        //获取存储设备
        storageDevices = UsbMassStorageDevice.getMassStorageDevices(this);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
        for (UsbMassStorageDevice device : storageDevices) {//可能有几个 一般只有一个 因为大部分手机只有1个otg插口
            if (usbManager.hasPermission(device.getUsbDevice())) {//有就直接读取设备是否有权限

                readDevice(device);
            } else {//没有就去发起意图申请
                Toast.makeText(DataTransmissionFileActivity.this, "检测到设备，但是没有权限，进行申请", Toast.LENGTH_SHORT).show();
                usbManager.requestPermission(device.getUsbDevice(), pendingIntent); //该代码执行后，系统弹出一个对话框，
            }
        }
        if (storageDevices.length == 0)
            Toast.makeText(DataTransmissionFileActivity.this, "未检测到有任何存储设备插入", Toast.LENGTH_SHORT).show();

    }
    private void readDevice(UsbMassStorageDevice device) {
        // before interacting with a device you need to call init()!
        try {
            device.init();//初始化
//          Only uses the first partition on the device
            Partition partition = device.getPartitions().get(0);
            FileSystem currentFs = partition.getFileSystem();
//fileSystem.getVolumeLabel()可以获取到设备的标识
//通过FileSystem可以获取当前U盘的一些存储信息，包括剩余空间大小，容量等等
//            Log.d(TAG, "Capacity: " + currentFs.getCapacity());
//            Log.d(TAG, "Occupied Space: " + currentFs.getOccupiedSpace());
//            Log.d(TAG, "Free Space: " + currentFs.getFreeSpace());
//            Log.d(TAG, "Chunk size: " + currentFs.getChunkSize());
            UsbFile root = currentFs.getRootDirectory();//获取根目录
            String deviceName = currentFs.getVolumeLabel();//获取设备标签
            Toast.makeText(DataTransmissionFileActivity.this, "已连接U盘" + deviceName+"，请长按文件名导出文件", Toast.LENGTH_SHORT).show();

            cFolder = root;//设置当前文件对象
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void readSDFile(final File f, UsbFile folder) {
        UsbFile usbFile = null;
        if (f.isDirectory()) {//如果选择是个文件夹
            try {
                usbFile = folder.createDirectory(f.getName());
                if (folder == cFolder) {//如果是在当前目录 就添加到集合中
                    usbFiles.add(usbFile);
                }
                for (File sdFile : f.listFiles()) {
                    readSDFile(sdFile, usbFile);
                }
            } catch (IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        Toast.makeText(DataTransmissionFileActivity.this,"读取异常", Toast.LENGTH_SHORT).show();

                    }
                });
            }
        } else {//如果选了一个文件
            try {
                usbFile = folder.createFile(f.getName());
                if (folder == cFolder) {//如果是在当前目录 就添加到集合中
                    usbFiles.add(usbFile);
                }
                saveSDFile2OTG(usbFile, f);
            } catch (IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        Toast.makeText(DataTransmissionFileActivity.this,"读取异常", Toast.LENGTH_SHORT).show();

                    }
                });
            }
        }
    }

    private void saveSDFile2OTG(final UsbFile usbFile, final File f) {
        try {//开始写入
            FileInputStream fis = new FileInputStream(f);//读取选择的文件的
            UsbFileOutputStream uos = new UsbFileOutputStream(usbFile);
            redFileStream(uos, fis);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(DataTransmissionFileActivity.this,"文件" + f.getName() + "已写入U盘", Toast.LENGTH_SHORT).show();

                }
            });
        } catch (final Exception e) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    Toast.makeText(DataTransmissionFileActivity.this,"读取异常", Toast.LENGTH_SHORT).show();

                    try {
                        usbFile.delete();
                    } catch (IOException e1) {
//                        Toast.makeText(DataTransmissionFileActivity.this,"读取异常", Toast.LENGTH_SHORT).show();

                    }
                }
            });
        }
    }


    private void redFileStream(OutputStream os, InputStream is) throws IOException {
        /**
         *  写入文件到U盘同理 要获取到UsbFileOutputStream后 通过
         *  f.createNewFile();调用 在U盘中创建文件 然后获取os后
         *  可以通过输出流将需要写入的文件写到流中即可完成写入操作
         */
        int bytesRead = 0;
        byte[] buffer = new byte[1024 * 8];
        while ((bytesRead = is.read(buffer)) != -1) {
            os.write(buffer, 0, bytesRead);
        }
        os.flush();
        os.close();
        is.close();
    }
    private void registerReceiver() {
        //监听otg插入 拔出
        IntentFilter usbDeviceStateFilter = new IntentFilter();
        usbDeviceStateFilter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        usbDeviceStateFilter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        registerReceiver(mUsbReceiver, usbDeviceStateFilter);
        //注册监听自定义广播
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        registerReceiver(mUsbReceiver, filter);
    }

    private UsbMassStorageDevice getUsbMass(UsbDevice usbDevice) {
        for (UsbMassStorageDevice device : storageDevices) {
            if (usbDevice.equals(device.getUsbDevice())) {
                return device;
            }
        }
        return null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mUsbReceiver != null) {//有注册就有注销
            unregisterReceiver(mUsbReceiver);
            mUsbReceiver = null;
        }
    }
}
