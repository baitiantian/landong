package com.lunduimohao.landongjiguang.lunduimohao.PublicClass;


import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;

import jxl.Workbook;
import jxl.format.Colour;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

public class ExcelUtils {
    //内存地址
    private static String root = Environment.getExternalStorageDirectory()
            .getPath();

    public static void writeExcel(Context context, List<Order> exportOrder,
                                  String fileName) throws Exception {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)&&getAvailableStorage()>1000000) {
            Toast.makeText(context, "SD卡不可用", Toast.LENGTH_LONG).show();
            return;
        }
        String[] title = { "杆号", "踏面磨耗(mm)", "车轮厚度(mm)", "轮辋宽度(mm)", "轮辋厚度(mm)", "检测时间" };
        File file;
        File dir = new File("/sdcard/LineWear");
        file = new File(dir, fileName + ".xls");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        // 创建Excel工作表
        WritableWorkbook wwb;
        OutputStream os = new FileOutputStream(file);
        wwb = Workbook.createWorkbook(os);
        // 添加第一个工作表并设置第一个Sheet的名字
        WritableSheet sheet = wwb.createSheet("杆号", 0);
        Label label;
        for (int i = 0; i < title.length; i++) {
            // Label(x,y,z) 代表单元格的第x+1列，第y+1行, 内容z
            // 在Label对象的子对象中指明单元格的位置和内容
            label = new Label(i, 0, title[i], getHeader());
            // 将定义好的单元格添加到工作表中
            sheet.addCell(label);
        }

        for (int i = 0; i < exportOrder.size(); i++) {
            Order order = exportOrder.get(i);

            Label WheelId = new Label(0, i + 1, order.WheelId);
            Label TreadWear = new Label(1, i + 1, order.TreadWear);
            Label WheelThick = new Label(2,i+1,order.WheelThick);
            Label RimWidth = new Label(3, i + 1, order.RimWidth);
            Label RimThick = new Label(4,i+1,order.RimThick);
            Label Time = new Label(5, i + 1, order.Time);

            sheet.addCell(WheelId);
            sheet.addCell(TreadWear);
            sheet.addCell(WheelThick);
            sheet.addCell(RimWidth);
            sheet.addCell(RimThick);
            sheet.addCell(Time);


        }
        // 写入数据
        wwb.write();
        // 关闭文件
        wwb.close();
        Toast.makeText(context, "当前数据导出表格文件成功", Toast.LENGTH_LONG).show();
    }

    private static WritableCellFormat getHeader() {
        WritableFont font = new WritableFont(WritableFont.TIMES, 10,
                WritableFont.BOLD);// 定义字体
        try {
            font.setColour(Colour.BLUE);// 蓝色字体
        } catch (WriteException e1) {
            e1.printStackTrace();
        }
        WritableCellFormat format = new WritableCellFormat(font);
        try {
            format.setAlignment(jxl.format.Alignment.CENTRE);// 左右居中
            format.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE);// 上下居中
            // format.setBorder(Border.ALL, BorderLineStyle.THIN,
            // Colour.BLACK);// 黑色边框
            // format.setBackground(Colour.YELLOW);// 黄色背景
        } catch (WriteException e) {
            e.printStackTrace();
        }
        return format;
    }

    /** 获取SD可用容量 */
    private static long getAvailableStorage() {

        StatFs statFs = new StatFs(root);
        long blockSize = statFs.getBlockSize();
        long availableBlocks = statFs.getAvailableBlocks();
        // Formatter.formatFileSize(context, availableSize);
        return blockSize * availableBlocks;
    }
}
