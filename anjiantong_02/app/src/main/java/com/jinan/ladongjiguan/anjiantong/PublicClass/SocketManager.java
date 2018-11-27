package com.jinan.ladongjiguan.anjiantong.PublicClass;

import android.annotation.SuppressLint;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
/**
 * 重构Socket传输代码工具类
 * */
public class SocketManager {
    private ServerSocket server;
    private int length;
    private byte[] sendBytes;
    private Socket socket;
    private DataOutputStream dos;
    private FileInputStream fis;
    private boolean bool = false;
    private boolean aBoolean = true;
    @SuppressLint("HandlerLeak")
    private Handler shandler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            switch (msg.what){
                case 0:

                    break;
                case 1:

                    break;
                default:
                    break;
            }
        }
    };
    public SocketManager(ServerSocket server) {
        this.server = server;
    }


    // 接收文件
    public String ReceiveFile() {
        String File_Name = "";
        try {
            // 接收文件名
            Socket socket = server.accept();
            //接受到的文件存放在SD卡LanDong目录下面
            String pathdir = Environment.getExternalStorageDirectory().getPath() + "/LanDong";
            byte[] inputByte = null;
            long length = 0;
            DataInputStream dis = null;
            FileOutputStream fos = null;
            String filePath;

            long L;

            try {

                dis = new DataInputStream(socket.getInputStream());
                File f = new File(pathdir);
                if (!f.exists()) {
                    f.mkdir();
                }
                File_Name = dis.readUTF();
                filePath = pathdir + "/" + File_Name;
                fos = new FileOutputStream(new File(filePath));
                inputByte = new byte[1024];
                L = f.length();
                Log.d("文件路径：", filePath);
                double rfl = 0;
                L = dis.readLong();
                Log.d("文件长度", L + "kB");
                Log.d("文件", "开始接收数据...");


                while ((length = dis.read(inputByte, 0, inputByte.length)) > 0 && aBoolean) {
                    rfl += length;
                    fos.write(inputByte, 0, (int) length);
                    System.out.println("rfl:" + rfl);
                    fos.flush();

                }
                fos.close();
                dis.close();
                socket.close();
                Log.d("文件完成接收：", filePath);
                //接受完成信号

                return File_Name;



            } catch (Exception e) {
                e.printStackTrace();
                return File_Name;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return File_Name;
        }

    }

    //String fileName,
    public Boolean SendFile(final String path, final String ipAddress, final int port) {

        length = 0;
        sendBytes = null;
        socket = null;
        dos = null;
        fis = null;
        bool = false;


        try {
            if(!aBoolean){
                return false;
            }
            File file = new File(path); // 要传输的文件路径
            long l = file.length();
            socket = new Socket();
            socket.connect(new InetSocketAddress(ipAddress, port));
            dos = new DataOutputStream(socket.getOutputStream());
            fis = new FileInputStream(path);
            sendBytes = new byte[1024];
            dos.writeUTF(file.getName());// 传递文件名
            dos.flush();
            dos.writeLong((long) file.length() / 1024 + 1);
            dos.flush();

            while ((length = fis.read(sendBytes, 0, sendBytes.length)) > 0 && aBoolean) {
                dos.write(sendBytes, 0, length);
                dos.flush();
            }
            bool = true;
            Log.e("传输数据完毕","成功");
            return bool;
        } catch (Exception e) {
            System.out.println("客户端文件传输异常");
            e.printStackTrace();
            Log.e("客户端文件传输异常", e.toString());
            return SendFile(path, ipAddress, port);
        } finally {
            if (dos != null)
                try {
                    dos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            if (fis != null)
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            if (socket != null)
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }


    }
    /**
     * 停止传输
     * */
    public void Stop(){
        aBoolean = false;
    }
    /**
     * 发送文件完毕
     * */
    public Boolean Result(){
        return bool;
    }
}
