package com.jinan.ladongjiguan.anjiantong.activity;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.jinan.ladongjiguan.anjiantong.PublicClass.FTP;
import com.jinan.ladongjiguan.anjiantong.PublicClass.HouzhuiClass;
import com.jinan.ladongjiguan.anjiantong.PublicClass.Utility;
import com.jinan.ladongjiguan.anjiantong.R;
import com.jinan.ladongjiguan.anjiantong.utils.CommonUtils;

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
public class AccidentInvestigationDateActivity extends BaseActivity implements View.OnClickListener, View.OnTouchListener {
    @BindView(R.id.examine_page_back)
    LinearLayout examinePageBack;
    @BindView(R.id.title_layout)
    TextView titleLayout;
    @BindView(R.id.tx_add_check_up_00)
    TextView txAddCheckUp00;
    @BindView(R.id.et_add_check_problem_08)
    TextView etAddCheckProblem08;
    @BindView(R.id.tx_add_check_problem_04)
    TextView txAddCheckProblem04;
    @BindView(R.id.tx_add_check_up_05)
    TextView txAddCheckUp05;
    @BindView(R.id.tx_add_check_up_06)
    TextView txAddCheckUp06;
    @BindView(R.id.list)
    ListView list;
    @BindView(R.id.bt_01)
    Button bt01;
    private List<Map<String, Object>> listItems = new ArrayList<>();//文书列表数据
//    public static final String  FTPIP = "http://218.201.222.159:802";//20180925前端口
    public static final String  FTPIP = "http://218.201.222.159:4005";
    private DownloadManager downloadManager;
    private long mTaskId;
    private String filePath;//文件的保存路径
    private String TAG = AccidentInvestigationDateActivity.class.getSimpleName();

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    Toast.makeText(AccidentInvestigationDateActivity.this, "下载完成", Toast.LENGTH_SHORT).show();
                    CommonUtils.doOpenPdf(AccidentInvestigationDateActivity.this, "/sdcard/Download/" + filePath);

                    break;
                case 1:
                    Toast.makeText(AccidentInvestigationDateActivity.this, "下载失败", Toast.LENGTH_SHORT).show();

                    break;
                default:
                    break;
            }

        }

    };
    @Override
    protected void initView() {
        setContentView(R.layout.accident_investigation_date_layout);
        ButterKnife.bind(this);
    }

    @Override
    protected void init() {
        examinePageBack.setOnTouchListener(this);
        examinePageBack.setOnClickListener(this);
        bt01.setOnClickListener(this);
        titleLayout.setText("详细内容");
        getDate();
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
     * 添加数据
     * */
    protected void getDate(){
        listItems = new ArrayList<>();
        txAddCheckUp00.setText(getIntent().getStringExtra("accident_name"));
        etAddCheckProblem08.setText(getIntent().getStringExtra("accident_business"));
        txAddCheckProblem04.setText(getIntent().getStringExtra("accident_industry"));
        txAddCheckUp05.setText(getIntent().getStringExtra("accident_time"));
        txAddCheckUp06.setText(getIntent().getStringExtra("mark"));
        String[] strings = getIntent().getStringExtra("filename").split(",");
        String[] strings1 = getIntent().getStringExtra("filepath").split(",");
        for (int i=0;i<strings.length;i++){
            Map<String,Object> map = new HashMap<>();
            map.put("filename",strings[i]);
            map.put("filepath",strings1[i]);
            listItems.add(map);
        }
        SimpleAdapter simpleAdapter = new SimpleAdapter(AccidentInvestigationDateActivity.this, listItems,
                R.layout.enterprise_information_list_item,
                new String[]{"filename"},
                new int[]{R.id.enterprise_name});
        list.setAdapter(simpleAdapter);
        Utility.setListViewHeightBasedOnChildren(list);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                Log.d("下载文书的数据", FTPIP + listItems.get(position).get("filepath") +
                        listItems.get(position).get("filename"));
                File file = new File("/sdcard/Download/"+listItems.get(position).get("filename"));
                if(file.exists()){
                    file.delete();
                }
                filePath = listItems.get(position).get("filename").toString();
               /* downloadAPK(FTPIP + listItems.get(position).get("filepath"),
                        listItems.get(position).get("filename").toString());*/
                downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
//                downloadAPK(FTPID + listItems.get(position).get("filepath"), listItems.get(position).get("filename").toString());
                Toast.makeText(AccidentInvestigationDateActivity.this, "正在下载请稍等", Toast.LENGTH_SHORT).show();

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        // 下载
                        try {

                            //单文件下载
                            final String[] strings2 = listItems.get(position).get("filepath").toString().split("/Ftp");
                            new FTP().downloadSingleFile( strings2[1],"/sdcard/Download/",filePath,new FTP.DownLoadProgressListener(){

                                @Override
                                public void onDownLoadProgress(String currentStep, long downProcess, File file) {
                                    Log.d(TAG, currentStep+strings2[1]);
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
                    Toast.makeText(AccidentInvestigationDateActivity.this, "加载中，请稍等", Toast.LENGTH_SHORT).show();
                    CommonUtils.doOpenPdf(AccidentInvestigationDateActivity.this,"/sdcard/Download/"+filePath);
                    break;
                case DownloadManager.STATUS_FAILED:
                    Log.d("下载数据",">>>下载失败");
                    break;
            }
        }

    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
    }
}
