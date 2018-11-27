package com.jinan.ladongjiguan.anjiantong.utils;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.jinan.ladongjiguan.anjiantong.PublicClass.HouzhuiClass;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

/**
 * Created by fuweiwei on 2015/9/17.
 * 时间操作类
 */
public class CommonUtils {
    private static final SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    /**
     * 判断网络是否链接
     */
    public static boolean isNetworkConnected(Context context) {
        try {
            ConnectivityManager cManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cManager != null) {
                NetworkInfo localNetworkInfo = cManager.getActiveNetworkInfo();
                if (localNetworkInfo != null && localNetworkInfo.isConnected()) {
                    // 判断当前网络是否已经连接
                    if (localNetworkInfo.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
        }
        return false;
    }

    /**
     * 调用手机中安装的可打开word的软件
     */
    public static void doOpenPdf(Context activity,String newPath) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
//        intent.setAction("android.intent.action.EDIT");
        intent.addCategory("android.intent.category.DEFAULT");
        String fileMimeType = HouzhuiClass.getMIMEType(newPath);
        Uri uri ;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(activity, "jinan.landong.fileprovider", new File(newPath));
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }else {
            uri = Uri.fromFile(new File(newPath));
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(uri, fileMimeType);
        try {
            activity.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            //检测到系统尚未安装OliveOffice的apk程序
            Toast.makeText(activity, "未找到软件，请下载相关软件", Toast.LENGTH_LONG).show();
            //请先到www.olivephone.com/e.apk下载并安装
        }
    }

    /**
     * 判断一个字符串是否是标准的IPv4地址
     */
    public static boolean isIp(String IP) {
        boolean b = false;
        //去掉IP字符串前后所有的空格
        while (IP.startsWith(" ")) {
            IP = IP.substring(1, IP.length()).trim();
        }
        while (IP.endsWith(" ")) {
            IP = IP.substring(0, IP.length() - 1).trim();
        }
        if (IP.matches("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}")) {
            String s[] = IP.split("\\.");
            if (Integer.parseInt(s[0]) < 255)
                if (Integer.parseInt(s[1]) < 255)
                    if (Integer.parseInt(s[2]) < 255)
                        if (Integer.parseInt(s[3]) < 255)
                            b = true;
        }
        return b;
    }

/*    //使用系统下载器下载
    public static void downloadAPK(Context context,DownloadManager downloadManager, BroadcastReceiver receiver, long mTaskId, String versionUrl, String versionName) {
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
//        downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        //加入下载队列后会给该任务返回一个long型的id，
        //通过该id可以取消任务，重启任务等等，看上面源码中框起来的方法
        mTaskId = downloadManager.enqueue(request);
        //注册广播接收者，监听下载状态
        context.registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }*/

    /**
     * 判断本地时间是否在后台时间之前
     *
     * @param localTime 本地时间
     * @param netTime   后台时间
     * @return
     */
    public static Boolean IsBefore(String localTime, String netTime) {
        Boolean flag = false;
       /* try {
            Date date1 = df.parse(localTime);
            Date date2 = df.parse(netTime);
            if(date1.before(date2)){
                flag=true;
            }else {
                flag = false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }*/
        if (localTime == null) {
            return true;
        }
        if (!localTime.equals(netTime)) {
            flag = true;
        } else {
            flag = false;
        }
        return flag;
    }

    /**
     * 随机生成一个GUID
     *
     * @return
     */
    public static String getGuid() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

    /**
     * 获取android中的DeviceId
     *
     * @param mContext
     * @return
     */
    public static String getDeviceId(Context mContext) {
        return ((TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
    }

    /**
     * 获取android中的SN号
     *
     * @param mContext
     * @return
     */
    public static String getSN(Context mContext) {
        return android.provider.Settings.System.getString(mContext.getContentResolver(), "android_id");
    }

    /**
     * 插入签名图片
     */
    public static void insertImage(PdfStamper ps,String path) throws DocumentException {

        try {
            PdfContentByte over = ps.getOverContent(1);//设置在第几页打印印章
            Image img = Image.getInstance(path);//选择图片
            //插入了图片
            img.setAlignment(1);
            img.scaleAbsolute(90, 40);//控制图片大小，第一个参数是宽，第二个是高
            img.setAbsolutePosition(210, 175);//控制图片位置，左下角是坐标原点
            over.addImage(img);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void readpdfandFillData(ByteArrayOutputStream bos, OutputStream fos, String pdf, PdfStamper ps, BaseFont bf,
                                          ArrayList<BaseFont> fontList, AcroFields fields, String pdftemp, Map<String, String> data){
        try {
            PdfReader reader = null;
            reader = new PdfReader(pdftemp);
            bos = new ByteArrayOutputStream();
        /* 将要生成的目标PDF文件名称 */
            ps = new PdfStamper(reader, bos);
            //添加仿宋的字体
            bf = BaseFont.createFont("assets/fonts/simfang.ttf", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
            fontList = new ArrayList<BaseFont>();
            fontList.add(bf);
        /* 取出报表模板中的所有字段 */
            fields = ps.getAcroFields();
            fields.setSubstitutionFonts(fontList);

            //填充数据
            for (Iterator it = data.keySet().iterator(); it.hasNext(); ) {
                String key = (String) it.next();
                String value = data.get(key);
                fields.setField(key, value);
            }
            //插如图片
//            CommonUtils.insertImage(ps,"/sdcard/doc/sign.jpg");
            // 必须要调用这个，否则文档不会生成的
            ps.setFormFlattening(true);
            ps.close();
            fos = new FileOutputStream(pdf);
            fos.write(bos.toByteArray());
            fos.flush();
            fos.close();
            bos.close();
        }catch (Exception e){
            Log.e("CommonUtil","报错"+e.getMessage());
        }
    }

    public static void readpdfandFillData(ByteArrayOutputStream bos,PdfStamper ps, BaseFont bf,
                                          ArrayList<BaseFont> fontList, AcroFields fields, String pdftemp, Map<String, String> data){
        try {
            PdfReader reader = null;
            reader = new PdfReader(pdftemp);
            bos = new ByteArrayOutputStream();
        /* 将要生成的目标PDF文件名称 */
            ps = new PdfStamper(reader, bos);
            //添加仿宋的字体
            bf = BaseFont.createFont("assets/fonts/simfang.ttf", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
            fontList = new ArrayList<BaseFont>();
            fontList.add(bf);
        /* 取出报表模板中的所有字段 */
            fields = ps.getAcroFields();
            fields.setSubstitutionFonts(fontList);

            //填充数据
            for (Iterator it = data.keySet().iterator(); it.hasNext(); ) {
                String key = (String) it.next();
                String value = data.get(key);
                fields.setField(key, value);
            }
            //插如图片
//            CommonUtils.insertImage(ps,"/sdcard/doc/sign.jpg");
            // 必须要调用这个，否则文档不会生成的
            ps.setFormFlattening(true);
            ps.close();
            bos.close();
        }catch (Exception e){
            Log.e("CommonUtil","报错"+e.getMessage());
        }
    }

}
