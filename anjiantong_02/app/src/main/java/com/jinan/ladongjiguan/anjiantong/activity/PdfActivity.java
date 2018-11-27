package com.jinan.ladongjiguan.anjiantong.activity;

import android.graphics.Canvas;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jinan.ladongjiguan.anjiantong.R;
import com.lidong.pdf.PDFView;
import com.lidong.pdf.listener.OnDrawListener;
import com.lidong.pdf.listener.OnLoadCompleteListener;
import com.lidong.pdf.listener.OnPageChangeListener;


import butterknife.BindView;
import butterknife.ButterKnife;

import static com.jinan.ladongjiguan.anjiantong.activity.CountMainActivity.FTPID;

public class PdfActivity extends BaseActivity implements OnPageChangeListener,View.OnTouchListener,View.OnClickListener
        ,OnLoadCompleteListener, OnDrawListener {


    @BindView(R.id.pdfView)
    PDFView pdfView;
    @BindView(R.id.text)
    TextView text;
    @BindView(R.id.examine_page_back)
    LinearLayout examinePageBack;
    @BindView(R.id.title_layout)
    TextView titleLayout;

    @Override
    protected void initView() {
        setContentView(R.layout.activity_pdf);
        ButterKnife.bind(this);
    }

    @Override
    protected void init() {
        switch (getIntent().getStringExtra("state")){
            case "1":
                pdfView.fromAsset("p1.pdf").defaultPage(1).onPageChange(this).load();
                titleLayout.setText("固体危化燃烧");
                break;
            case "2":
                pdfView.fromAsset("p2.pdf").defaultPage(1).onPageChange(this).load();
                titleLayout.setText("固体危化泄露");
                break;
            case "3":
                pdfView.fromAsset("p3.pdf").defaultPage(1).onPageChange(this).load();
                titleLayout.setText("液体危化燃烧");
                break;
            case "4":
                pdfView.fromAsset("p4.pdf").defaultPage(1).onPageChange(this).load();
                titleLayout.setText("液体危化泄露");
                break;
            case "5":
                pdfView.fromAsset("p5.pdf").defaultPage(1).onPageChange(this).load();
                titleLayout.setText("气体危化燃烧");
                break;
            case "6":
                pdfView.fromAsset("p6.pdf").defaultPage(1).onPageChange(this).load();
                titleLayout.setText("气体危化泄露");
                break;
            case "7":
//                pdfView.fromAsset("p6.pdf").defaultPage(1).onPageChange(this).load();
                titleLayout.setText("查看文书");
                Log.d("下载文书的数据",FTPID+getIntent().getStringExtra("documentid")+".pdf");
                displayFromFile1(FTPID+getIntent().getStringExtra("documentid")+".pdf", getIntent().getStringExtra("documentid")+".pdf");
//                displayFromFile1("http://file.chmsp.com.cn/colligate/file/00100000224821.pdf", "00100000224821.pdf");

                break;
            default:
                break;
        }
//        pdfView.fromAsset("p1.pdf").defaultPage(1).onPageChange(this).load();
        examinePageBack.setOnClickListener(this);
        examinePageBack.setOnTouchListener(this);
    }


    @Override
    public void onPageChanged(int page, int pageCount) {
        text.setText(page + "/" + pageCount);
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
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
            v.setBackgroundColor(ContextCompat.getColor(this, R.color.main_color_3));
        } else if (event.getAction() == MotionEvent.ACTION_CANCEL) {
            v.setBackgroundColor(ContextCompat.getColor(this, R.color.main_color_2));
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            v.setBackgroundColor(ContextCompat.getColor(this, R.color.main_color_2));
        }
        return false;
    }

    /**
     * 获取打开网络的pdf文件
     * @param fileUrl
     * @param fileName
     */
    private void displayFromFile1( String fileUrl ,String fileName) {
        pdfView.fileFromLocalStorage(this,this,this,fileUrl,fileName);   //设置pdf文件地址

    }

    @Override
    public void onLayerDrawn(Canvas canvas, float pageWidth, float pageHeight, int displayedPage) {

    }

    @Override
    public void loadComplete(int nbPages) {
        Toast.makeText( PdfActivity.this ,  "加载完成" + nbPages  , Toast.LENGTH_SHORT).show();
    }
}
