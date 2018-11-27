package com.jinan.landongjiguan.shuangkong.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.zxing.Result;
import com.google.zxing.client.android.AnimeViewCallback;
import com.google.zxing.client.android.BaseCaptureActivity;
import com.google.zxing.client.android.ViewfinderView;
import com.jinan.landongjiguan.shuangkong.R;

public class DefaultCaptureActivity extends BaseCaptureActivity {

    private static final String TAG = DefaultCaptureActivity.class.getSimpleName();

    private SurfaceView surfaceView;
    private ViewfinderView viewfinderView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture);
        surfaceView = (SurfaceView) findViewById(R.id.preview_view);
        viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
        TextView textView = (TextView)findViewById(R.id.title_layout);
        textView.setText("扫描地点二维码");
        LinearLayout linearLayout = (LinearLayout)findViewById(R.id.examine_page_back);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public SurfaceView getSurfaceView() {
        return (surfaceView == null) ? (SurfaceView) findViewById(R.id.preview_view) : surfaceView;
    }

    @Override
    public AnimeViewCallback getViewfinderHolder() {
        return (viewfinderView == null) ? (ViewfinderView) findViewById(R.id.viewfinder_view) : viewfinderView;
    }

    @Override
    public void dealDecode(Result rawResult, Bitmap barcode, float scaleFactor) {
        Log.i(TAG, "dealDecode ~~~~~ " + rawResult.getText() + " " + barcode + " " + scaleFactor);
//        playBeepSoundAndVibrate();
//        Toast.makeText(this, rawResult.getText(), Toast.LENGTH_LONG).show();
        Intent intent = new Intent();
        intent.putExtra("BusinessId",rawResult.getText());
        setResult(1, intent);
        finish();
//        对此次扫描结果不满意可以调用
//        reScan();
    }

    @Override
    public void onBackPressed() {
        setResult(0, new Intent());
        finish();
        super.onBackPressed();
    }
}
