package com.jinan.ladongjiguan.anjiantong.PublicClass;

import android.util.Log;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.SocketException;


public class FTPUtils {

    private static final String TAG = FTPUtils.class.getSimpleName();
    private FTPClient ftpClient = null;
    private static FTPUtils ftpUtilsInstance = null;
    private String FTPUrl;
    private int FTPPort;
    private String UserName;
    private String UserPassword;
    private FTPUtils() {
        ftpClient = new FTPClient();
    }

    // 得到类对象实例
    public static FTPUtils getInstance() {
        if (ftpUtilsInstance == null) {
            ftpUtilsInstance = new FTPUtils();
        }
        return ftpUtilsInstance;
    }

    /**
     * 设置FTP服务器
     * @param FTPUrl        FTP服务器IP地址
     * @param FTPPort       FTP服务器端口号
     * @param UserName      登陆FTP服务器的账号
     * @param UserPassword  登陆FTP服务器的密码
     */
    public boolean initFTPSetting(String FTPUrl,int FTPPort,String UserName,String UserPassword) {


        //修改测试用，发布时一定要注意改回来
//        this.FTPUrl = FTPUrl;
//        this.UserName=UserName;
//        this.UserPassword=UserPassword;
//        this.FTPPort= FTPPort;
        //安监通外网FTP
        this.FTPUrl = "218.201.222.159";
        this.UserName="ftpuser";
        this.UserPassword="ld123456";
//        this.FTPPort= 803;//20180925前端口
        this.FTPPort= 4000;
        //蓝动内网
//        this.FTPUrl = "192.168.2.220";
//        this.UserName="test";
//        this.UserPassword="ld@123456";
//        this.FTPPort= 21;
        int reply;
        try {
            //1.要连接的FTP服务器的地址、端口
            ftpClient.connect( this.FTPUrl, this.FTPPort);

            //2.登陆FTP服务器
            ftpClient.login(this.UserName, this.UserPassword);

            //3.看返回值是不是230，如果是，表示登陆成功
            reply = ftpClient.getReplyCode();

            if (!FTPReply.isPositiveCompletion(reply)) {
                //断开连接
                ftpClient.disconnect();
                return false;
            }

            return true;
        } catch (SocketException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     *   上传文件
     *   @param FilePath    要上传文件所在的SDCard的路径
     *   @param FileName    要上传文件的文件名（如：SIM的唯一标识码）
     *   @return  true为成功， false为失败
     */
    public boolean uploadFile(String FilePath,String FileName) {
        //判断FTP是否仍在连接
        if (!ftpClient.isConnected()) {
            if (!initFTPSetting(FTPUrl, FTPPort, UserName, UserPassword)) {
                Log.d("上传文件","2false"); //输出错误消息
                return false;
            }
        }

        try {
            //1.设置存储路径
            ftpClient.makeDirectory("/ftpdata");
            ftpClient.changeWorkingDirectory("/ftpdata");

            //2.设置上传文件需要的一些基本信息
            ftpClient.setBufferSize(1024);
            ftpClient.setControlEncoding("UTF-8");
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            Log.d("上传路径",FileName); //输出错误消息
            Log.d("上传路径",FilePath); //输出错误消息
            //文件上传吧～
            FileInputStream fileInputStream = new FileInputStream(FilePath);
            ftpClient.storeFile(FileName, fileInputStream);
            //关闭文件流
            fileInputStream.close();
            //退出登陆FTP，关闭ftpCLient的连接
            ftpClient.logout();
            ftpClient.disconnect();

        } catch (IOException e) {
            Log.e("上传文件",e.toString()); //输出错误消息
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 下载文件
     * @param FilePath 要存放的文件路径
     * @param FileName 远程FTP服务器上的那个文件的名字
     * @return  true为成功， false为失败
     */
    public boolean downLoadFile(String FilePath,String FileName) {
        //判断FTP是否仍在连接
        if (!ftpClient.isConnected()) {
            if (!initFTPSetting(FTPUrl, FTPPort, UserName, UserPassword)) {
                return false;
            }
        }
        try {
            //1.转到指定的下载目录
            ftpClient.changeWorkingDirectory("/ftpdata");
            //2.列出该目录下的所有文件
            FTPFile[] files = ftpClient.listFiles();
            //3.遍历所有文件，找到指定的文件
            for (FTPFile file : files) {
                String fileName = file.getName();
                if (file.getName().equals(FileName)) {
                    //根据绝对路径初始化文件
                    File localFile = new File(FilePath);
                    //输出流
                    FileOutputStream outputStream = new FileOutputStream(localFile);
                    //下载文件
                    ftpClient.retrieveFile(file.getName(), outputStream);
                    //关闭流
                    outputStream.close();

                }
            }
            //4.退出登录FTP，关闭ftpClient的连接
            ftpClient.logout();
            ftpClient.disconnect();

        } catch (IOException e) {
            // TODO: handle exception
            Log.e(TAG, "DownLoad failed");
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
