package com.jinan.ladongjiguan.anjiantong.activity;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.jinan.ladongjiguan.anjiantong.PublicClass.AssetsDatabaseManager;
import com.jinan.ladongjiguan.anjiantong.PublicClass.CustomProgressDialog;
import com.jinan.ladongjiguan.anjiantong.PublicClass.FTP;
import com.jinan.ladongjiguan.anjiantong.utils.ImageLoaderUtils;
import com.jinan.ladongjiguan.anjiantong.utils.SharedPreferencesUtil;
import com.jinan.ladongjiguan.anjiantong.PublicClass.Utility;
import com.jinan.ladongjiguan.anjiantong.utils.WebServiceUtils;
import com.jinan.ladongjiguan.anjiantong.R;
import com.jinan.ladongjiguan.anjiantong.utils.CommonUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.ksoap2.serialization.SoapObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.jinan.ladongjiguan.anjiantong.activity.CountMainActivity.FTPID;
/**
 * 此处下载文件走的是FTP并非是http
 * */
public class HiddenDangerActivity extends BaseActivity implements View.OnClickListener, View.OnTouchListener {


    @BindView(R.id.examine_page_back)
    LinearLayout examinePageBack;
    @BindView(R.id.title_layout)
    TextView titleLayout;
    @BindView(R.id.del_text)
    TextView delText;
    @BindView(R.id.delete_layout)
    LinearLayout deleteLayout;
    @BindView(R.id.activity_hidden_danger)
    LinearLayout activityHiddenDanger;
    @BindView(R.id.et_add_check_problem_08)
    TextView etAddCheckProblem08;
    @BindView(R.id.et_add_check_problem_04)
    TextView etAddCheckProblem04;
    @BindView(R.id.et_add_check_problem_05)
    TextView etAddCheckProblem05;
    @BindView(R.id.tx_add_check_problem_01)
    TextView txAddCheckProblem01;
    @BindView(R.id.tx_add_check_problem_04)
    TextView txAddCheckProblem04;
    @BindView(R.id.tx_add_check_problem_05)
    TextView txAddCheckProblem05;
    @BindView(R.id.tx_add_check_problem_02)
    TextView txAddCheckProblem02;
    @BindView(R.id.tx_add_check_problem_03)
    TextView txAddCheckProblem03;
    @BindView(R.id.et_add_check_problem_01)
    TextView etAddCheckProblem01;
    @BindView(R.id.l_add_check_problem_01)
    LinearLayout lAddCheckProblem01;
    @BindView(R.id.et_add_check_big_problem_01)
    TextView etAddCheckBigProblem01;
    @BindView(R.id.tx_add_check_problem_06)
    TextView txAddCheckProblem06;
    @BindView(R.id.tx_add_check_problem_07)
    TextView txAddCheckProblem07;
    @BindView(R.id.l_add_check_big_problem)
    LinearLayout lAddCheckBigProblem;
    @BindView(R.id.et_add_check_problem_09)
    TextView etAddCheckProblem09;
    @BindView(R.id.bt_01)
    Button bt01;
    @BindView(R.id.tx_problem_06)
    TextView txProblem06;
    @BindView(R.id.et_add_check_problem_10)
    TextView etAddCheckProblem10;
    @BindView(R.id.et_add_check_problem_11)
    TextView etAddCheckProblem11;
    @BindView(R.id.et_add_check_problem_12)
    TextView etAddCheckProblem12;
    @BindView(R.id.gridView1)
    ImageView gridView1;
    @BindView(R.id.list)
    ListView list;
    @BindView(R.id.tx_problem_07)
    TextView txProblem07;
    private CustomProgressDialog progressDialog = null;//加载页
    private String WEB_SERVER_URL;
    private String img = "";
    private ImageLoader imageLoader = ImageLoader.getInstance();//定义图片下载
    private LinearLayout Linear_root;
    private PopupWindow popupwindow;//弹出图片
    private List<Map<String, Object>> listItems = new ArrayList<>();//文书合集
    long fileLength = 0;//下载的文件长度
    long contentLength;//文件的总长度
    private String filePath;//文件的保存路径
    private DownloadManager downloadManager;
    private long mTaskId;

    private String TAG = HiddenDangerActivity.class.getSimpleName();

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    Toast.makeText(HiddenDangerActivity.this, "下载完成", Toast.LENGTH_SHORT).show();
                    CommonUtils.doOpenPdf(HiddenDangerActivity.this, "/sdcard/Download/" + filePath);

                    break;
                case 1:
                    Toast.makeText(HiddenDangerActivity.this, "下载失败", Toast.LENGTH_SHORT).show();

                    break;
                default:
                    break;
            }

        }

    };
    @Override
    protected void initView() {
        setContentView(R.layout.activity_hidden_danger);
        ButterKnife.bind(this);
        titleLayout.setText("隐患详情");
        WEB_SERVER_URL = SharedPreferencesUtil.getStringData(this, "IPString", "");

    }

    @Override
    protected void init() {
        examinePageBack.setOnTouchListener(this);
        examinePageBack.setOnClickListener(this);
        bt01.setOnClickListener(this);
        gridView1.setOnClickListener(this);
        txProblem07.setOnClickListener(this);
        txProblem07.setOnTouchListener(this);
        httpDate();
        Linear_root = (LinearLayout) findViewById(R.id.activity_hidden_danger);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.examine_page_back://返回
                onBackPressed();
                break;
            case R.id.bt_01:
                onBackPressed();
                break;
            case R.id.gridView1:
                initmPopupWindowView();
                break;
            case R.id.tx_problem_07:
                Intent  intent = new Intent();
                intent.setClass(this,CheckUpDateLoadActivity.class);
                intent.putExtra("id",getIntent().getStringExtra("id"));
                intent.putExtra("state", "1");
                startActivity(intent);
                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
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
     * 查询隐患详细信息
     */
    protected void httpDate() {
        //加载页添加
        if (progressDialog == null) {
            progressDialog = CustomProgressDialog.createDialog(this);
        }
        progressDialog.show();

        //添加参数
        HashMap<String, String> properties = new HashMap<>();
        properties.put("Xml", "<Request><data  code='select-HiddendangerInfoByDangerid'><no><HiddenDangerId>" +
                getIntent().getStringExtra("id") + "</HiddenDangerId></no></data></Request>");
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
                            try {
                                Cursor c = db.rawQuery("SELECT* FROM ELL_Business WHERE BusinessId = ?",
                                        new String[]{obj.getString("businessid")});
                                c.moveToFirst();
                                etAddCheckProblem08.setText(c.getString(c.getColumnIndex("BusinessName")));
                                c.close();
                            } catch (Exception e) {
                                Log.e("打开企业数据库报错", e.toString());
                            }
                            if(obj.getString("yhdd").length()>1){
                                etAddCheckProblem04.setText(obj.getString("yhdd"));
                            }
                            if(obj.getString("yhbw").length()>1){
                                etAddCheckProblem05.setText(obj.getString("yhbw"));
                            }
                            txAddCheckProblem01.setText(obj.getString("checkresult"));
                            if (obj.getString("checkresult").equals("重大隐患")) {
                                lAddCheckBigProblem.setVisibility(View.VISIBLE);
                            }
                            txAddCheckProblem04.setText(obj.getString("checktype"));
                            txAddCheckProblem05.setText(obj.getString("subject"));
                            txAddCheckProblem02.setText(obj.getString("yhms"));
                            txAddCheckProblem03.setText(obj.getString("zglx"));
                            etAddCheckProblem01.setText(obj.getString("zgqx").substring(0, 10));
                            etAddCheckProblem09.setText(obj.getString("disposeresult"));
                            etAddCheckBigProblem01.setText(obj.getString("gpdbsj").substring(0, 10));
                            txAddCheckProblem06.setText(obj.getString("gpdbjb"));
                            txAddCheckProblem07.setText(obj.getString("gpdbdw"));
                            etAddCheckProblem10.setText(obj.getString("companyname"));
                            if(obj.has("departmentname")){

                                etAddCheckProblem11.setText(obj.getString("departmentname"));
                            }
//                        etAddCheckProblem12.setText(obj.getString("documentname"));
                            /**
                             * 添加超链接
                             * */
                            Map<String, Object> listItem = new HashMap<>();
                            if (obj.has("documentname")) {

                                listItem.put("documentname", obj.getString("documentname"));
                            } else {
                                listItem.put("documentname", "");
                            }

                            if (obj.has("documentid2") && obj.getString("documenttype").equals("1")) {

                                String webLinkText = "<font color='#333333'><a href='" + FTPID + obj.getString("documentid2") +
                                        ".pdf' style='text-decoration:none; color:#0000FF'> "
                                        + obj.getString("documentname") + "</a>";
                                etAddCheckProblem12.setText(Html.fromHtml(webLinkText));
                                etAddCheckProblem12.setMovementMethod(LinkMovementMethod.getInstance());
                                listItem.put("documentid", obj.getString("documentid2"));
                            } else {
                                String webLinkText = "<font color='#333333'><a href='" + FTPID + obj.getString("documentid") +
                                        ".pdf' style='text-decoration:none; color:#0000FF'> "
                                        + obj.getString("documentname") + "</a>";
                                etAddCheckProblem12.setText(Html.fromHtml(webLinkText));
                                etAddCheckProblem12.setMovementMethod(LinkMovementMethod.getInstance());
                                listItem.put("documentid", obj.getString("documentid"));
                            }

                            if (obj.getString("yhzgqtp").length() > 10) {
                                String[] strings = obj.getString("yhzgqtp").split(",");
                                img = FTPID + strings[0];
                            }
                            imageLoader.init(ImageLoaderUtils.initConfiguration(HiddenDangerActivity.this));//图片初始化与优化（主要是缓存的优化跟加载）
                            imageLoader.displayImage(img, gridView1, ImageLoaderUtils.initOptions());
                            String[] strings = obj.getString("memberid").split(",");
                            String s1 = "";
                            for (String string : strings) {
                                Cursor cursor2 = db.rawQuery("SELECT* FROM Base_User WHERE UserId = ?",
                                        new String[]{string});
                                cursor2.moveToFirst();

                                String s2 = "";
                                try {
                                    s2 = cursor2.getString(cursor2.getColumnIndex("RealName"));
                                } catch (Exception e) {
                                    Log.e("问题发现人数据表报错", e.toString());
                                }
                                if (s1.length() > 0) {
                                    s1 = s1 + "," + s2;
                                } else {
                                    s1 = s2;
                                }
                                cursor2.close();
                            }
                            txProblem06.setText(s1);
                            listItems.add(listItem);
                        } else if (jsonObj.has("DocumentElement") && jsonObj.toString().substring(jsonObj.toString().length() - 3, jsonObj.toString().length() - 2).equals("]")) {
                            array = jsonObj.getJSONObject("DocumentElement").getJSONArray("Table");

                            for (int i = 0; i < array.length(); i++) {
                                obj = array.getJSONObject(i);
                                try {
                                    Cursor c = db.rawQuery("SELECT* FROM ELL_Business WHERE BusinessId = ?",
                                            new String[]{obj.getString("businessid")});
                                    c.moveToFirst();
                                    etAddCheckProblem08.setText(c.getString(c.getColumnIndex("BusinessName")));
                                    c.close();
                                } catch (Exception e) {
                                    Log.e("打开企业数据库报错", e.toString());
                                }
                                if(obj.getString("yhdd").length()>1){
                                    etAddCheckProblem04.setText(obj.getString("yhdd"));
                                }
                                if(obj.getString("yhbw").length()>1){
                                    etAddCheckProblem05.setText(obj.getString("yhbw"));
                                }


                                txAddCheckProblem01.setText(obj.getString("checkresult"));
                                if (obj.getString("checkresult").equals("重大隐患")) {
                                    lAddCheckBigProblem.setVisibility(View.VISIBLE);
                                }
                                txAddCheckProblem04.setText(obj.getString("checktype"));
                                txAddCheckProblem05.setText(obj.getString("subject"));
                                txAddCheckProblem02.setText(obj.getString("yhms"));
                                txAddCheckProblem03.setText(obj.getString("zglx"));
                                etAddCheckProblem01.setText(obj.getString("zgqx").substring(0, 10));
                                etAddCheckProblem09.setText(obj.getString("disposeresult"));
                                etAddCheckBigProblem01.setText(obj.getString("gpdbsj").substring(0, 10));
                                txAddCheckProblem06.setText(obj.getString("gpdbjb"));
                                txAddCheckProblem07.setText(obj.getString("gpdbdw"));
                                etAddCheckProblem10.setText(obj.getString("companyname"));
                                if(obj.has("departmentname")){

                                    etAddCheckProblem11.setText(obj.getString("departmentname"));
                                }
//                        etAddCheckProblem12.setText(obj.getString("documentname"));
                                /**
                                 * 添加超链接
                                 * */
                                Map<String, Object> listItem = new HashMap<>();
                                if (obj.has("documentname")) {

                                    listItem.put("documentname", obj.getString("documentname"));
                                } else {
                                    listItem.put("documentname", "");
                                }
                                if (obj.has("documentid2") && obj.getString("documenttype").equals("1")) {

                                    String webLinkText = "<font color='#333333'><a href='" + FTPID + obj.getString("documentid2") +
                                            ".pdf' style='text-decoration:none; color:#0000FF'> "
                                            + obj.getString("documentname") + "</a>";
                                    etAddCheckProblem12.setText(Html.fromHtml(webLinkText));
                                    etAddCheckProblem12.setMovementMethod(LinkMovementMethod.getInstance());
                                    listItem.put("documentid", obj.getString("documentid2"));
                                } else {
                                    String webLinkText = "<font color='#333333'><a href='" + FTPID + obj.getString("documentid") +
                                            ".pdf' style='text-decoration:none; color:#0000FF'> "
                                            + obj.getString("documentname") + "</a>";
                                    etAddCheckProblem12.setText(Html.fromHtml(webLinkText));
                                    etAddCheckProblem12.setMovementMethod(LinkMovementMethod.getInstance());
                                    listItem.put("documentid", obj.getString("documentid"));
                                }
                                if (obj.getString("yhzgqtp").length() > 10) {
                                    String[] strings = obj.getString("yhzgqtp").split(",");
                                    img = FTPID + strings[0];
                                }
                                imageLoader.init(ImageLoaderUtils.initConfiguration(HiddenDangerActivity.this));//图片初始化与优化（主要是缓存的优化跟加载）
                                imageLoader.displayImage(img, gridView1, ImageLoaderUtils.initOptions());
                                String[] strings = obj.getString("memberid").split(",");
                                String s1 = "";
                                for (String string : strings) {
                                    Cursor cursor2 = db.rawQuery("SELECT* FROM Base_User WHERE UserId = ?",
                                            new String[]{string});
                                    cursor2.moveToFirst();

                                    String s2 = "";
                                    try {
                                        s2 = cursor2.getString(cursor2.getColumnIndex("RealName"));
                                    } catch (Exception e) {
                                        Log.e("问题发现人数据表报错", e.toString());
                                    }
                                    if (s1.length() > 0) {
                                        s1 = s1 + "," + s2;
                                    } else {
                                        s1 = s2;
                                    }
                                    cursor2.close();
                                }
                                txProblem06.setText(s1);
                                listItems.add(listItem);
                            }

                        }


                        //关闭加载页
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                            progressDialog = null;
                        }
                        SimpleAdapter simpleAdapter = new SimpleAdapter(HiddenDangerActivity.this, listItems,
                                R.layout.enterprise_information_list_item,
                                new String[]{"documentname"},
                                new int[]{R.id.enterprise_name});
                        list.setAdapter(simpleAdapter);
                        Utility.setListViewHeightBasedOnChildren(list);
                        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                                Log.d("下载文书的数据", FTPID + listItems.get(position).get("documentid") + ".pdf");
                                filePath = listItems.get(position).get("documentname")+".pdf";
                                File file = new File("/mnt/sdcard/Download/"+filePath);
                                if(file.exists()){
                                    file.delete();
                                }
                                downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                                Toast.makeText(HiddenDangerActivity.this, "正在下载请稍等", Toast.LENGTH_SHORT).show();

                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {

                                        // 下载
                                        try {

                                            //单文件下载
                                            new FTP().downloadSingleFile( "/ftpdata/"+listItems.get(position).get("documentid") + ".pdf","/sdcard/Download/",filePath,new FTP.DownLoadProgressListener(){

                                                @Override
                                                public void onDownLoadProgress(String currentStep, long downProcess, File file) {
                                                    Log.d(TAG, currentStep);
                                                    if(currentStep.equals(MainActivity.FTP_DOWN_SUCCESS)){
                                                        Log.d(TAG, "-----xiazai--successful");
                                                        handler.sendEmptyMessage(0);
                                                    } else if(currentStep.equals(MainActivity.FTP_DOWN_LOADING)){
                                                        Log.d(TAG, "-----xiazai---"+downProcess + "%");
                                                    }else if(currentStep.equals(MainActivity.FTP_DOWN_FAIL)||currentStep.equals(MainActivity.FTP_CONNECT_FAIL)){
                                                        handler.sendEmptyMessage(1);
                                                    }
                                                }

                                            });

                                        } catch (Exception e) {
                                            // TODO Auto-generated catch block
                                            e.printStackTrace();
                                        }

                                    }
                                }).start();
//                                downloadAPK(FTPID + listItems.get(position).get("documentid") + ".pdf",filePath);
                            }
                        });
                    } catch (JSONException e) {
                        //关闭加载页
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                            progressDialog = null;
                        }
                        Toast.makeText(HiddenDangerActivity.this, "数据缺失", Toast.LENGTH_SHORT).show();
                        Log.e("JSON exception", e.getMessage());
                        e.printStackTrace();
                    }
                } else {
                    //关闭加载页
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                        progressDialog = null;
                    }
                    Toast.makeText(HiddenDangerActivity.this, "网络未响应，提交失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //使用系统下载器下载
    private void downloadAPK(String versionUrl, String versionName) {
//创建下载任务
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(versionUrl));
        request.setAllowedOverRoaming(false);
//漫游网络是否可以下载
//设置文件类型，可以在下载结束后自动打开该文件
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        String mimeString = mimeTypeMap.getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(versionUrl));
        request.setMimeType(mimeString);
//在通知栏中显示，默认就是显示的
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        request.setVisibleInDownloadsUi(true);
        //sdcard的目录下的download文件夹，必须设置
        request.setDestinationInExternalPublicDir("/Download/", versionName);
        //request.setDestinationInExternalFilesDir(),也可以自己制定下载路径
        //将下载请求加入下载队列
        downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        //加入下载队列后会给该任务返回一个long型的id，
        //通过该id可以取消任务，重启任务等等，看上面源码中框起来的方法
        mTaskId = downloadManager.enqueue(request);
        //注册广播接收者，监听下载状态
        this.registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    /**
     * 弹出图片
     */
    public void initmPopupWindowView() {

        // // 获取自定义布局文件pop.xml的视图
        View customView = getLayoutInflater().inflate(R.layout.popview_view,
                null, false);
        // 创建PopupWindow实例,200,150分别是宽度和高度
        popupwindow = new PopupWindow(customView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
        // 设置动画效果 [R.style.AnimationFade 是自己事先定义好的]
        popupwindow.setAnimationStyle(R.style.AnimationImage);
        popupwindow.setBackgroundDrawable(new ColorDrawable(0xcc000000));
        // 自定义view添加触摸事件
        customView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (popupwindow != null && popupwindow.isShowing()) {
                    popupwindow.dismiss();
                    popupwindow = null;
                }

                return false;
            }
        });
        ImageView imageView = (ImageView) customView.findViewById(R.id.image_01);

        imageLoader.init(ImageLoaderUtils.initConfiguration(HiddenDangerActivity.this));//图片初始化与优化（主要是缓存的优化跟加载）
        imageLoader.displayImage(img, imageView, ImageLoaderUtils.initOptions());
        popupwindow.showAtLocation(Linear_root, Gravity.CENTER, 0, 0);
    }

    //广播接受者，接收下载状态
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            checkDownloadStatus();
//检查下载状态
        }
    };
    protected void checkDownloadStatus(){
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(mTaskId);
        //筛选下载任务，传入任务ID，可变参数
         Cursor c = downloadManager.query(query);
        if (c.moveToFirst()) {
            int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
            switch (status) {
                case DownloadManager.STATUS_PAUSED:
                    Log.d("下载数据",">>>下载暂停");
                case DownloadManager.STATUS_PENDING:
                    Log.d("下载数据",">>>下载延迟");
                case DownloadManager.STATUS_RUNNING:
                    Log.d("下载数据",">>>正在下载");
                    break;
                case DownloadManager.STATUS_SUCCESSFUL:
                    Log.d("下载数据",">>>下载完成");
                    //下载完成安装APK
                    Toast.makeText(HiddenDangerActivity.this, "加载中，请稍等", Toast.LENGTH_SHORT).show();
                    CommonUtils.doOpenPdf(HiddenDangerActivity.this,"/mnt/sdcard/Download/"+filePath);
                    break;
                case DownloadManager.STATUS_FAILED:
                    Log.d("下载数据",">>>下载失败");
                    break;
            }
        }
    }
}
