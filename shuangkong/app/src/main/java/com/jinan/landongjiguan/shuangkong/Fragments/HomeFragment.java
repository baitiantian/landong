package com.jinan.landongjiguan.shuangkong.Fragments;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.jinan.landongjiguan.shuangkong.Activities.AddProblemActivity;
import com.jinan.landongjiguan.shuangkong.Activities.DefaultCaptureActivity;
import com.jinan.landongjiguan.shuangkong.PublicClasses.AddWatermark;
import com.jinan.landongjiguan.shuangkong.PublicClasses.ImageTools;
import com.jinan.landongjiguan.shuangkong.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static android.app.Activity.RESULT_OK;
import static com.jinan.landongjiguan.shuangkong.PublicClasses.isConnect.isConnect;


public class HomeFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {
    @BindView(R.id.l_saoyisao)
    LinearLayout lSaoyisao;
    @BindView(R.id.l_chayinhuan)
    LinearLayout lChayinhuan;
    @BindView(R.id.l_chasanfang)
    LinearLayout lChasanfang;
    @BindView(R.id.l_paizhaopian)
    LinearLayout lPaizhaopian;
    @BindView(R.id.l_new_message)
    LinearLayout lNewMessage;
    @BindView(R.id.s_messages_state)
    Spinner sMessagesState;
    @BindView(R.id.s_messages_time)
    Spinner sMessagesTime;
    @BindView(R.id.s_messages_people)
    Spinner sMessagesPeople;
    @BindView(R.id.s_messages_address)
    Spinner sMessagesAddress;
    @BindView(R.id.swipe_container)
    SwipeRefreshLayout swipeContainer;
    Unbinder unbinder;
    @BindView(R.id.list_message_home)
    ListView listMessageHome;
    private View view;
    private SwipeRefreshLayout mSwipeLayout;//下拉刷新
    public static final int L_SAOYISAO = 1;//扫一扫返回标
    private String capturePath = null;//拍照地址
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home,
                container, false);

        //下拉刷新配置
        mSwipeLayout = view.findViewById(R.id.swipe_container);
        mSwipeLayout.setOnRefreshListener(this);
        // 设置下拉圆圈上的颜色，蓝色、绿色、橙色
        mSwipeLayout.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light,
                android.R.color.holo_orange_light);
        mSwipeLayout.setDistanceToTriggerSync(100);// 设置手指在屏幕下拉多少距离会触发下拉刷新
        mSwipeLayout.setProgressBackgroundColorSchemeResource(android.R.color.white);
        mSwipeLayout.setSize(SwipeRefreshLayout.LARGE);
        unbinder = ButterKnife.bind(this, view);
        /*重新定义各个元件*/
        lSaoyisao = view.findViewById(R.id.l_saoyisao);
        lChayinhuan = view.findViewById(R.id.l_chayinhuan);
        lChasanfang = view.findViewById(R.id.l_chasanfang);
        lPaizhaopian = view.findViewById(R.id.l_paizhaopian);
        lNewMessage = view.findViewById(R.id.l_new_message);
        sMessagesState = view.findViewById(R.id.s_messages_state);
        sMessagesPeople = view.findViewById(R.id.s_messages_people);
        sMessagesTime = view.findViewById(R.id.s_messages_time);
        sMessagesAddress = view.findViewById(R.id.s_messages_address);
        listMessageHome = view.findViewById(R.id.list_message_home);
        lSaoyisao.setOnClickListener(this);
        lChayinhuan.setOnClickListener(this);
        lChasanfang.setOnClickListener(this);
        lPaizhaopian.setOnClickListener(this);
        /*获取以及配置数据*/

        setData();
        return view;
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // 停止刷新
                mSwipeLayout.setRefreshing(false);
                //判断无网络连接弹出对话框
                if (!isConnect(getActivity())) {
                    new AlertDialog.Builder(getActivity())
                            .setTitle("网络错误")
                            .setMessage("网络连接失败，请确认网络连接")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {

//                                    Process.killProcess(Process.myPid());
//                                    System.exit(0);
                                }
                            }).show();
                } else {
//                    //加载页添加
//                    if (progressDialog == null){
//                        progressDialog = CustomProgressDialog.createDialog(getActivity());
//                    }
//                    progressDialog.show();
                    getData();
                }
            }
        }, 2000); // 2秒后发送消息，停止刷新
    }

    /**
     * 获得数据
     */
    protected void getData() {

    }

    /**
     * 设置数据
     */
    protected void setData() {
        /*选择是否处理*/
        List<Map<String, Object>> listItems = new ArrayList<>();
        Resources res = getResources();
        String[] strings01 = res.getStringArray(R.array.s_home_message_state_1);
        for (String string : strings01) {
            Map<String, Object> map = new HashMap<>();
            map.put("txt", string);
            listItems.add(map);
        }
        SimpleAdapter simpleAdapter = new SimpleAdapter(getActivity(), listItems,
                R.layout.item_spinner_home,
                new String[]{"txt"},
                new int[]{R.id.item_spinner_txt});
        sMessagesState.setAdapter(simpleAdapter);
        /*选择检查时间*/
        listItems = new ArrayList<>();
        strings01 = res.getStringArray(R.array.s_home_message_time_1);
        for (String string : strings01) {
            Map<String, Object> map = new HashMap<>();
            map.put("txt", string);
            listItems.add(map);
        }
        simpleAdapter = new SimpleAdapter(getActivity(), listItems,
                R.layout.item_spinner_home,
                new String[]{"txt"},
                new int[]{R.id.item_spinner_txt});
        sMessagesTime.setAdapter(simpleAdapter);
        /*选择检查地点*/
        listItems = new ArrayList<>();
        strings01 = res.getStringArray(R.array.s_home_message_address_1);
        for (String string : strings01) {
            Map<String, Object> map = new HashMap<>();
            map.put("txt", string);
            listItems.add(map);
        }
        simpleAdapter = new SimpleAdapter(getActivity(), listItems,
                R.layout.item_spinner_home,
                new String[]{"txt"},
                new int[]{R.id.item_spinner_txt});
        sMessagesAddress.setAdapter(simpleAdapter);
        /*列表填入数据*/
        listItems = new ArrayList<>();
        strings01 = res.getStringArray(R.array.s_home_message_1);
        String[] strings02 = res.getStringArray(R.array.s_home_message_address_1);
        String[] strings03 = res.getStringArray(R.array.s_home_message_people_1);
        String[] strings04 = res.getStringArray(R.array.s_home_message_time_1);
        for (int i=0;i<strings01.length;i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("message", strings01[i]);
            map.put("address", strings02[i+1]);
            map.put("people", strings03[i+1]);
            map.put("time", strings04[i+1]);
            listItems.add(map);
        }
        simpleAdapter = new SimpleAdapter(getActivity(), listItems,
                R.layout.item_message_list_home,
                new String[]{"message","address","people","time"},
                new int[]{R.id.tx_list_home_message,R.id.tx_list_home_address,
                        R.id.tx_list_home_people,R.id.tx_list_home_time,});
        listMessageHome.setAdapter(simpleAdapter);
        getData();

    }

    /**
     * 点击事件
     */
    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.l_saoyisao:
                intent.setClass(getContext(), DefaultCaptureActivity.class);
                startActivityForResult(intent, L_SAOYISAO);
                break;
            case R.id.l_chayinhuan:
                intent.setClass(getContext(), AddProblemActivity.class);
                startActivity(intent);
                ((Activity) getContext()).overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                break;
            case R.id.l_chasanfang:

                break;
            case R.id.l_paizhaopian:
                Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                String out_file_path = "/sdcard/shuangkong/";
                File dir = new File(out_file_path);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                if (Build.VERSION.SDK_INT >= 23) {
                    int check = ContextCompat.checkSelfPermission(getContext(), permissions[0]);
                    // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
                    if (check == PackageManager.PERMISSION_GRANTED) {
                        //调用相机
                        capturePath = "/sdcard/shuangkong/" + System.currentTimeMillis() + ".jpg";
                        Uri uri;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            uri = FileProvider.getUriForFile(getContext(), "jinan.landong.shuangkong.fileprovider", new File(capturePath));
                        } else {
                            uri = Uri.fromFile(new File(capturePath));
                        }
                        //添加权限
                        openCameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                        startActivityForResult(openCameraIntent, 2);
                    } else {
                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                    }
                } else {
                    capturePath = "/sdcard/shuangkong/" + System.currentTimeMillis() + ".jpg";
                    Uri uri;
                    uri = Uri.fromFile(new File(capturePath));
                    //添加权限
                    openCameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                    startActivityForResult(openCameraIntent, 2);
                }
                break;
            default:
                break;
        }
    }
    /**
     * 返回数据
     * */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        Toast.makeText(getContext(), resultCode+":"+requestCode, Toast.LENGTH_LONG).show();
        switch (requestCode) {
            case 0:

                break;
            case L_SAOYISAO:
                if (resultCode == 1) {
                    Toast.makeText(getContext(),  data.getStringExtra("BusinessId"), Toast.LENGTH_LONG).show();
                }
                break;
            case 2:
                if (resultCode == RESULT_OK) {
                    FileInputStream fis;
                    String s = UUID.randomUUID().toString();

                    Bitmap bitmap;
                    try {
                        // 获取当前的年、月、日、小时、分钟
                        fis = new FileInputStream(capturePath);
                        bitmap = BitmapFactory.decodeStream(fis); //拿到图片
//                                    bitmap = BitmapFactory.decodeFile(originalUri.getPath()); //拿到图片
                        Bitmap newBitmap = ImageTools.zoomBitmap(bitmap, bitmap.getWidth() / 5, bitmap.getHeight() / 5);
                        //由于Bitmap内存占用较大，这里需要回收内存，否则会报out of memory异常
                        Calendar c = Calendar.getInstance();

                        String mark = "检查时间：" + c.get(Calendar.YEAR) + "-" + (c.get(Calendar.MONTH) + 1) + "-" + c.get(Calendar.DAY_OF_MONTH)+" "+
                                c.get(Calendar.HOUR_OF_DAY)+":"+c.get(Calendar.SECOND);
                        newBitmap = AddWatermark.drawTextToLeftBottom(getContext(),
                                newBitmap, mark, 10, Color.RED, 10, 40);


                        ImageTools.savePhotoToSDCard(newBitmap,  "/sdcard/快速查隐患图库", s);
//
                        File file = new File("/sdcard/快速查隐患图库");
//                        //在手机相册中显示刚拍摄的图片
//                        getContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));

                        // 其次把文件插入到系统图库
                        try {
                            MediaStore.Images.Media.insertImage(getContext().getContentResolver(),
                                    file.getAbsolutePath(), s+".jpg", null);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        // 最后通知图库更新
                        getContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + "/sdcard/快速执法图库/"+ s+".jpg")));

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        Log.e("照片数据报错",e.toString(),e);
                    }



                }

                break;
            default:
                break;
        }
    }
}
