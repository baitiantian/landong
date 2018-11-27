package com.jinan.ladongjiguan.anjiantong.fragments;


import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
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
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jinan.ladongjiguan.anjiantong.PublicClass.AddWatermark;
import com.jinan.ladongjiguan.anjiantong.PublicClass.AssetsDatabaseManager;
import com.jinan.ladongjiguan.anjiantong.PublicClass.CustomProgressDialog;
import com.jinan.ladongjiguan.anjiantong.PublicClass.ImageTools;
import com.jinan.ladongjiguan.anjiantong.R;
import com.jinan.ladongjiguan.anjiantong.activity.AddCheckProblemActivity;
import com.jinan.ladongjiguan.anjiantong.activity.CheckUpActivity;
import com.jinan.ladongjiguan.anjiantong.activity.CheckUpMainActivity;
import com.jinan.ladongjiguan.anjiantong.activity.DefaultCaptureActivity;
import com.jinan.ladongjiguan.anjiantong.activity.EnterpriseInformationActivity;
import com.jinan.ladongjiguan.anjiantong.activity.EnterpriseInformationDateActivity;
import com.jinan.ladongjiguan.anjiantong.activity.LoginActivity;
import com.jinan.ladongjiguan.anjiantong.activity.ReviewActivity;
import com.jinan.ladongjiguan.anjiantong.activity.ReviewPlanActivity;
import com.jinan.ladongjiguan.anjiantong.activity.SimpleCheckMainActivity;
import com.jinan.ladongjiguan.anjiantong.utils.CommonUtils;
import com.jinan.ladongjiguan.anjiantong.utils.SharedPreferencesUtil;
import com.jinan.ladongjiguan.anjiantong.utils.WebServiceUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.ksoap2.serialization.SoapObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static android.app.Activity.RESULT_OK;
import static com.jinan.ladongjiguan.anjiantong.activity.MainActivity.b_start;
import static com.jinan.ladongjiguan.anjiantong.activity.PrintOut01Activity.isConnect;

public class HomeFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {
    @BindView(R.id.t_user_name)
    TextView tUserName;
    @BindView(R.id.l_saoyisao)
    LinearLayout lSaoyisao;
    @BindView(R.id.l_chayinhuan)
    LinearLayout lChayinhuan;
    @BindView(R.id.l_chasanfang)
    LinearLayout lChasanfang;
    @BindView(R.id.l_new_message)
    LinearLayout lNewMessage;
    @BindView(R.id.bt_home_01)
    LinearLayout btHome01;
    @BindView(R.id.bt_home_02)
    LinearLayout btHome02;
    @BindView(R.id.bt_home_03)
    LinearLayout btHome03;
    @BindView(R.id.bt_home_04)
    LinearLayout btHome04;
    @BindView(R.id.swipe_container)
    SwipeRefreshLayout swipeContainer;
    Unbinder unbinder;
    @BindView(R.id.l_kuaisuzhifa)
    LinearLayout lKuaisuzhifa;
    private View view;
    private SwipeRefreshLayout mSwipeLayout;//下拉刷新
    private CustomProgressDialog progressDialog = null;//加载页
    public static final int L_SAOYISAO = 1;
    public static final String Id = UUID.randomUUID().toString();//日志主键
    private String capturePath = null;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.home_main_layout,
                container, false);

        //下拉刷新配置
        mSwipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        mSwipeLayout.setOnRefreshListener(this);
        // 设置下拉圆圈上的颜色，蓝色、绿色、橙色
        mSwipeLayout.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light,
                android.R.color.holo_orange_light);
        mSwipeLayout.setDistanceToTriggerSync(100);// 设置手指在屏幕下拉多少距离会触发下拉刷新
        mSwipeLayout.setProgressBackgroundColorSchemeResource(android.R.color.white);
        mSwipeLayout.setSize(SwipeRefreshLayout.LARGE);
        unbinder = ButterKnife.bind(this, view);
        /**
         * 登录界面
         * */
        if (!b_start) {
            Intent intent = new Intent();
            intent.setClass(getContext(), LoginActivity.class);
            startActivityForResult(intent, 0);
            b_start = true;
        } else {
            getData();
        }


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
                    Toast.makeText(getContext(), "网络无连接", Toast.LENGTH_SHORT).show();
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
        /*重新定义各个元件*/
        tUserName = (TextView) view.findViewById(R.id.t_user_name);
        lNewMessage = (LinearLayout) view.findViewById(R.id.l_new_message);
        lSaoyisao = (LinearLayout) view.findViewById(R.id.l_saoyisao);
        lChayinhuan = (LinearLayout) view.findViewById(R.id.l_chayinhuan);
        lChasanfang = (LinearLayout) view.findViewById(R.id.l_chasanfang);
        lKuaisuzhifa = (LinearLayout) view.findViewById(R.id.l_kuaisuzhifa);
        btHome01 = (LinearLayout) view.findViewById(R.id.bt_home_01);
        btHome02 = (LinearLayout) view.findViewById(R.id.bt_home_02);
        btHome03 = (LinearLayout) view.findViewById(R.id.bt_home_03);
        btHome04 = (LinearLayout) view.findViewById(R.id.bt_home_04);
        lSaoyisao.setOnClickListener(this);
        lChayinhuan.setOnClickListener(this);
        lChasanfang.setOnClickListener(this);
        btHome01.setOnClickListener(this);
        btHome02.setOnClickListener(this);
        btHome03.setOnClickListener(this);
        btHome04.setOnClickListener(this);
        lKuaisuzhifa.setOnClickListener(this);
        // 初始化，只需要调用一次
        AssetsDatabaseManager.initManager(getContext());
        // 获取管理对象，因为数据库需要通过管理对象才能够获取
        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
        // 通过管理对象获取数据库
        SQLiteDatabase db = mg.getDatabase("users.db");
        String[] strings = SharedPreferencesUtil.getStringData(getContext(), "DepartmentId", null).split("-");
        try {
            Cursor cursor = db.rawQuery("SELECT* FROM Base_Company WHERE CompanyId = ?", new String[]{
//                                SharedPreferencesUtil.getStringData(this, "DepartmentId", null).substring(0, 6)});
                    strings[0]});
            cursor.moveToFirst();
            Cursor cursor1 = db.rawQuery("SELECT* FROM Base_Department WHERE DepartmentId = ?", new String[]{
                    SharedPreferencesUtil.getStringData(getContext(), "DepartmentId", null)});
            cursor1.moveToFirst();
//                    Log.d("用户数据",cursor1.getString(cursor1.getColumnIndex("FullName")));
            String s = cursor.getString(cursor.getColumnIndex("FullName")) +
                    cursor1.getString(cursor1.getColumnIndex("FullName")) +
                    "    " +
                    SharedPreferencesUtil.getStringData(getContext(), "Account", "");
            tUserName.setText(s);
            SharedPreferencesUtil.saveStringData(getContext(), "Company", cursor.getString(cursor.getColumnIndex("FullName")));
            cursor.close();
            cursor1.close();

        } catch (Exception e) {
            Log.e("报错", e.toString(), e);
//                        Toast.makeText(MainActivity.this, "数据异常", Toast.LENGTH_SHORT).show();
        }
        /**
         * 启动消息推送服务
         * */
        if (CommonUtils.isNetworkConnected(getContext()) && !b_start) {
            getMessage();
        }
    }

    /***
     * 获取消息数据
     */
    protected void getMessage() {
        //加载页添加
        if (progressDialog == null) {
            progressDialog = CustomProgressDialog.createDialog(getContext());
        }
        progressDialog.show();
        String WEB_SERVER_URL = SharedPreferencesUtil.getStringData(getContext(), "IPString", "");
        HashMap<String, String> properties = new HashMap<>();
        properties.put("Xml", "<Request><data code='select-Message'><no><Userid>" +
                SharedPreferencesUtil.getStringData(getContext(), "userId", null) + "</Userid></no></data></Request>");
        properties.put("Token", "");
        Log.d("发出的数据", properties.toString());
        WebServiceUtils.callWebService(WEB_SERVER_URL, "DynamicInvoke", properties, new WebServiceUtils.WebServiceCallBack() {

            //WebService接口返回的数据回调到这个方法中
            @Override
            public void callBack(SoapObject result) {
                if (result != null) {
                    Log.d("返回数据", result.toString());
                    try {

                        Object detail = result.getProperty("DynamicInvokeResult");
                        JSONObject jsonObj;
                        jsonObj = XML.toJSONObject(detail.toString());

                        if (jsonObj.has("DocumentElement")) {
                            lNewMessage.setVisibility(View.VISIBLE);

                        } else {
                            lNewMessage.setVisibility(View.GONE);
                        }
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
                        Toast.makeText(getContext(), "提交失败,数据错误缺失", Toast.LENGTH_SHORT).show();
                        Log.e("JSON exception", e.getMessage());
                        e.printStackTrace();
                    }
                } else {
                    //关闭加载页
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                        progressDialog = null;
                    }
                    Toast.makeText(getContext(), "网络未响应，提交失败", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    /**
     * 点击事件
     */
    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        //设置可查看权限
        String headShip = SharedPreferencesUtil.getStringData(getContext(), "Headship", "1");
        switch (v.getId()) {

            case R.id.bt_home_01://监管执法
                /*LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    Toast.makeText(MainActivity.this, "请打开GPS定位，否则导致程序异常", Toast.LENGTH_SHORT).show();
                } else {
                    intent.setClass(this, CheckUpMainActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                }*/
//                LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//                if (Build.VERSION.SDK_INT >= 23 && !manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//                    Toast.makeText(MainActivity.this, "请打开GPS定位，否则导致程序异常", Toast.LENGTH_SHORT).show();
//                } else {
                intent.setClass(getContext(), CheckUpMainActivity.class);
                startActivity(intent);
                ((Activity) getContext()).overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
//                }
                break;
            case R.id.bt_home_02://问题复查
                intent.setClass(getContext(), ReviewActivity.class);
//                intent.putExtra("date_state","10");
                startActivity(intent);
                ((Activity) getContext()).overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                break;
            case R.id.bt_home_03://企业信息
                intent.setClass(getContext(), EnterpriseInformationActivity.class);
                intent.putExtra("state", "home");
                startActivity(intent);
                ((Activity) getContext()).overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                break;
            case R.id.bt_home_04://职业卫生检查
                intent.setClass(getContext(), CheckUpActivity.class);
                intent.putExtra("state", "6");
                intent.putExtra("date_state", "12");
                startActivity(intent);
                ((Activity) getContext()).overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                break;
            case R.id.l_saoyisao://扫描二维码|拍摄照片
                dialog();

                break;
            case R.id.l_chayinhuan://检查计划
                intent.setClass(getContext(), CheckUpActivity.class);
                intent.putExtra("state", "2");
                startActivity(intent);
                ((Activity) getContext()).overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                break;
            case R.id.l_chasanfang://复查计划
                intent.setClass(getContext(), ReviewPlanActivity.class);
                startActivity(intent);
                ((Activity) getContext()).overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                break;
            case R.id.l_kuaisuzhifa://快速执法
                intent.setClass(getContext(), SimpleCheckMainActivity.class);
                startActivity(intent);
                ((Activity) getContext()).overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        Toast.makeText(getContext(), resultCode+":"+requestCode, Toast.LENGTH_LONG).show();
        switch (requestCode) {
            case 0:
                if (resultCode == 0) {
                    // 初始化，只需要调用一次
                    AssetsDatabaseManager.initManager(getContext());
                    // 获取管理对象，因为数据库需要通过管理对象才能够获取
                    AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
                    // 通过管理对象获取数据库
                    SQLiteDatabase db = mg.getDatabase("users.db");
                    Date curDate = new Date(System.currentTimeMillis());//获取当前时间
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
                    String loginTime = sdf.format(curDate);
                    SharedPreferencesUtil.saveStringData(getContext(), "LoginTime", loginTime);
                    String macAddress = null;
                    WifiManager wifiMgr = (WifiManager) getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                    WifiInfo info = (null == wifiMgr ? null : wifiMgr.getConnectionInfo());
                    if (null != info) {
                        macAddress = info.getMacAddress();
                    }
                    ContentValues values = new ContentValues();
                    values.put("DateId", Id);
                    values.put("UserId", SharedPreferencesUtil.getStringData(getContext(), "userId", null));
                    values.put("Mac", macAddress);
                    values.put("LoginTime", loginTime);
                    values.put("ExitTime", "");
                    db.replace("ELL_Date", null, values);
                    /**
                     * 添加数据库字段列
                     * */
                    try {
                        db.execSQL("ALTER TABLE ELL_HiddenDanger ADD COLUMN AddUsers");//添加隐患复查人
                    } catch (Exception e) {
                        Log.e("数据库列表已存在", e.toString());
                    }

//                    try {
//                        String[] strings = SharedPreferencesUtil.getStringData(this, "DepartmentId", null).split("-");
//                        Cursor cursor = db.rawQuery("SELECT* FROM Base_Company WHERE CompanyId = ?", new String[]{
////                                SharedPreferencesUtil.getStringData(this, "DepartmentId", null).substring(0, 6)});
//                                strings[0]});
//                        cursor.moveToFirst();
//                        Cursor cursor1 = db.rawQuery("SELECT* FROM Base_Department WHERE DepartmentId = ?", new String[]{
//                                SharedPreferencesUtil.getStringData(this, "DepartmentId", null)});
//                        cursor1.moveToFirst();
////                    Log.d("用户数据",cursor1.getString(cursor1.getColumnIndex("FullName")));
//                        tUserName.setText(cursor.getString(cursor.getColumnIndex("FullName")) +
//                                cursor1.getString(cursor1.getColumnIndex("FullName")) +
//                                "    " +
//                                data.getStringExtra("Account"));
//                        SharedPreferencesUtil.saveStringData(this, "Company", cursor.getString(cursor.getColumnIndex("FullName")));
//                        cursor.close();
//                        cursor1.close();
////                        /**
////                         * 启动消息推送服务
////                         * */
////                        if (CommonUtils.isNetworkConnected(MainActivity.this)) {
////                            getMessage();
////                        }
//                    } catch (Exception e) {
//                        Log.e("报错", e.toString());
//                        Toast.makeText(MainActivity.this, "数据异常", Toast.LENGTH_SHORT).show();
//                    }
                    if (b_start) getData();

                } else {
                    ((Activity) getContext()).finish();
                }
                break;
            case L_SAOYISAO:
                if (resultCode == 1) {
                    dataId(data.getStringExtra("BusinessId"));
//                    Toast.makeText(getContext(),  data.getStringExtra("BusinessId"), Toast.LENGTH_LONG).show();
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

                        String mark = "执法时间：" + c.get(Calendar.YEAR) + "-" + (c.get(Calendar.MONTH) + 1) + "-" + c.get(Calendar.DAY_OF_MONTH)+" "+
                                c.get(Calendar.HOUR_OF_DAY)+":"+c.get(Calendar.SECOND);
                        newBitmap = AddWatermark.drawTextToLeftBottom(getContext(),
                                newBitmap, mark, 10, Color.RED, 10, 40);


                        ImageTools.savePhotoToSDCard(newBitmap,  "/sdcard/快速执法图库", s);
//
                        File file = new File("/sdcard/快速执法图库");
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

    /**
     * 打开企业信息数据库
     */
    protected void dataId(String name) {

        // 初始化，只需要调用一次
        AssetsDatabaseManager.initManager(getContext());
        // 获取管理对象，因为数据库需要通过管理对象才能够获取
        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
        // 通过管理对象获取数据库
        SQLiteDatabase db = mg.getDatabase("users.db");

        try {
            // 对数据库进行操作
            Cursor c = db.rawQuery("SELECT* FROM ELL_Business WHERE  BusinessId = ? AND ValidFlag = 0 ",
                    new String[]{name});
            if (c.getCount() == 0) {
                Toast.makeText(getContext(), "无效二维码", Toast.LENGTH_LONG).show();
            } else {
                c.moveToFirst();
                Intent intent = new Intent();
                intent.putExtra("BusinessId", name);
                intent.setClass(getContext(), EnterpriseInformationDateActivity.class);
                startActivity(intent);
                ((Activity) getContext()).overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
            }

            c.close();
        } catch (Exception e) {
            Log.e("企业数据库报错", e.toString());
        }
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        Toast.makeText(getContext(), resultCode+":"+requestCode, Toast.LENGTH_LONG).show();
//        switch (requestCode) {
//
//            case L_SAOYISAO:
//                if (resultCode == 1) {
//                    dataId( data.getStringExtra("BusinessId"));
//                    Toast.makeText(getContext(),  data.getStringExtra("BusinessId"), Toast.LENGTH_LONG).show();
//                }
//                break;
//            default:
//                break;
//        }
//    }
    /**
     * 弹出确认对话框
     */
    protected void dialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("请选择");
        builder.setMessage("请选择现场取证拍照或扫描企业二维码");
        builder.setPositiveButton("扫描", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent intent = new Intent();
                intent.setClass(getContext(), DefaultCaptureActivity.class);
                startActivityForResult(intent, L_SAOYISAO);

            }
        });
        builder.setNegativeButton("取证", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                String out_file_path = "/sdcard/anjiantong/";
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
                        capturePath = "/sdcard/anjiantong/" + System.currentTimeMillis() + ".jpg";
                        Uri uri;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            uri = FileProvider.getUriForFile(getContext(), "jinan.landong.fileprovider", new File(capturePath));
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
                    capturePath = "/sdcard/anjiantong/" + System.currentTimeMillis() + ".jpg";
                    Uri uri;
                    uri = Uri.fromFile(new File(capturePath));
                    //添加权限
                    openCameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                    startActivityForResult(openCameraIntent, 2);
                }
            }
        });
        builder.show();
    }
}