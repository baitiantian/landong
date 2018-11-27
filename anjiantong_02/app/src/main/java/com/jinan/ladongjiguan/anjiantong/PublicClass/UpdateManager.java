package com.jinan.ladongjiguan.anjiantong.PublicClass;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.content.FileProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.jinan.ladongjiguan.anjiantong.R;
import com.jinan.ladongjiguan.anjiantong.activity.AddCheckProblemActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;


public class UpdateManager {

    private static final int DOWNLOAD = 1;
    private static final int DOWNLOAD_FINISH = 2;
    HashMap<String, String> mHashMap;
    private String mSavePath;
    private int progress;
    private boolean cancelUpdate = false;
    private Context mContext;
    private ProgressBar mProgress;
    private Dialog mDownloadDialog;
    private String string;//网址

    private android.os.Handler mHandler = new Handler()
    {
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case DOWNLOAD:

                    mProgress.setProgress(progress);
                    break;
                case DOWNLOAD_FINISH:

                    installApk();
                    break;
                default:
                    break;
            }
        }
    };

    public UpdateManager(Context context, String s)
    {
        this.mContext = context;
        this.string = s;
    }

    public void checkUpdate()throws Resources.NotFoundException, IOException {
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    if (isUpdate()) {
                        // 显示提示对话框
                        Looper.prepare();
                        showNoticeDialog();
                        Looper.loop();
                    } else {
                        Handler.sendEmptyMessage(0);
                    }
                } catch (Resources.NotFoundException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    /**
     * 土司提示
     * */
    private android.os.Handler Handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    toastMessage();
                    break;
                default:
                    break;
            }
        }

    };

    protected void toastMessage(){
        Toast.makeText(mContext, "此为最新版本，无需升级", Toast.LENGTH_LONG).show();
    }

    private boolean isUpdate() {
        URL url=null;
        try {
//            url = new URL("http://218.201.222.159:801/Version/lps/version.xml");//20180925前端口
            url = new URL("http://218.201.222.159:4003/Version/lps/version.xml");

        } catch (MalformedURLException e1) {
            e1.printStackTrace();
        }

        int versionCode = getVersionCode(mContext);
        HttpURLConnection conn = null;
        InputStream inStream=null;
        try {
            conn = (HttpURLConnection) url.openConnection();
        } catch (IOException e2) {
            e2.printStackTrace();
        }//基于HTTP协议连接对象
        conn.setConnectTimeout(5000);
        try {
            conn.setRequestMethod("GET");
        } catch (ProtocolException e1) {
            e1.printStackTrace();
        }
        try {
            if(conn.getResponseCode() == 200){
                inStream = conn.getInputStream();
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        ParseXmlService service = new ParseXmlService();
        try {
            mHashMap = service.parseXml(inStream);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        if (null != mHashMap) {
            int serviceCode = Integer.valueOf(mHashMap.get("version"));

            if (serviceCode > versionCode) {
                return true;
            }
        }
        return false;
    }

    private int getVersionCode(Context context)
    {
        int versionCode = 0;
        try {

            versionCode = context.getPackageManager().getPackageInfo("com.jinan.ladongjiguan.anjiantong", 0).versionCode;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    private void showNoticeDialog()
    {
        AlertDialog.Builder builder = new Builder(mContext);
        builder.setTitle("发现新版本");
        builder.setMessage("是否需要更新？");
        builder.setPositiveButton("是", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();

                showDownloadDialog();
            }
        });
        builder.setNegativeButton("否", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
            }
        });
        Dialog noticeDialog = builder.create();
        noticeDialog.show();
    }

    private void showDownloadDialog()
    {
        AlertDialog.Builder builder = new Builder(mContext);
        builder.setTitle("正在更新新版本");

        final LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.softupdate_progress, null);
        mProgress = (ProgressBar) v.findViewById(R.id.update_progress);
        builder.setView(v);

        builder.setNegativeButton("取消", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                cancelUpdate = true;
            }
        });
        mDownloadDialog = builder.create();
        mDownloadDialog.show();

        downloadApk();
    }

    private void downloadApk() {
        new downloadApkThread().start();
    }

    private class downloadApkThread extends Thread {
        @Override
        public void run() {
            try {
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

                    String sdpath = Environment.getExternalStorageDirectory() + "/";
                    mSavePath = sdpath + "download";
                    URL url = new URL(mHashMap.get("url"));

                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.connect();

                    int length = conn.getContentLength();

                    InputStream is = conn.getInputStream();

                    File file = new File(mSavePath);

                    if (!file.exists()) {
                        file.mkdir();
                    }
                    File apkFile = new File(mSavePath, mHashMap.get("name"));
                    FileOutputStream fos = new FileOutputStream(apkFile);
                    int count = 0;

                    byte buf[] = new byte[1024];

                    do {
                        int numread = is.read(buf);
                        count += numread;

                        progress = (int) (((float) count / length) * 100);

                        mHandler.sendEmptyMessage(DOWNLOAD);
                        if (numread <= 0) {

                            mHandler.sendEmptyMessage(DOWNLOAD_FINISH);
                            break;
                        }

                        fos.write(buf, 0, numread);
                    } while (!cancelUpdate);
                    fos.close();
                    is.close();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            mDownloadDialog.dismiss();
        }
    };

    private void installApk() {
        File apkfile = new File(mSavePath, mHashMap.get("name"));
        if (!apkfile.exists())
        {
            return;
        }
        Intent i = new Intent(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            i.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri uri = FileProvider.getUriForFile(mContext, "jinan.landong.fileprovider", apkfile);
            i.setDataAndType(uri, "application/vnd.android.package-archive");
        } else {
            i.setDataAndType(Uri.parse("file://" + apkfile.toString()), "application/vnd.android.package-archive");
        }
        mContext.startActivity(i);
    }
}