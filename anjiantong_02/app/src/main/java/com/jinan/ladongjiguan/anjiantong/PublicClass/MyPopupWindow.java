package com.jinan.ladongjiguan.anjiantong.PublicClass;

import android.app.Activity;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.PopupWindow;

import com.jinan.ladongjiguan.anjiantong.R;

/**
 * Created by wangfuchun on 2018/9/19.
 */

public class MyPopupWindow {
    private PopupWindow mPopupWindow;
    private Context mContext;
    private Activity mAc;

    public MyPopupWindow(PopupWindow mPopupWindow, Context mContext) {
        this.mPopupWindow = mPopupWindow;
        this.mContext = mContext;
    }

    /**
     * 弹出菜单
     */
    public void initmPopupWindowView(Context context) {

        // // 获取自定义布局文件pop.xml的视图
        View customView = mAc.getLayoutInflater().inflate(R.layout.popview_item_1, null, false);
        // 创建PopupWindow实例,200,150分别是宽度和高度
        mPopupWindow = new PopupWindow(customView, 250, mContext.getWallpaperDesiredMinimumHeight());
        // 设置动画效果 [R.style.AnimationFade 是自己事先定义好的]
        mPopupWindow.setAnimationStyle(R.style.AnimationFade);
        mPopupWindow.setFocusable(true);
        // 自定义view添加触摸事件
        customView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mPopupWindow != null && mPopupWindow.isShowing()) {
                    mPopupWindow.dismiss();
                    mPopupWindow = null;
                }

                return false;
            }
        });

        /** 在这里可以实现自定义视图的功能 */
/*        Button btton2 = (Button) customView.findViewById(R.id.button2);
        btton2.setOnClickListener(this);
        btton2.setOnTouchListener(this);
        Button btton1 = (Button) customView.findViewById(R.id.button1);
        btton1.setOnClickListener(this);
        btton1.setOnTouchListener(this);
        Button btton3 = (Button) customView.findViewById(R.id.button3);
        btton3.setOnClickListener(this);
        btton3.setOnTouchListener(this);*/
    }
}
