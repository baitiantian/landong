package com.jinan.ladongjiguan.anjiantong.PublicClass;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import com.jinan.ladongjiguan.anjiantong.activity.LawDetailActivity;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * Created by wangfuchun on 2018/9/27.
 * 每次加载50行
 */

public class AsyncTextLoadTask extends AsyncTask<Object, String, String> {
    private Context mContext;
    private LawDetailActivity mActivity;
    private BufferedReader mBr;

    public AsyncTextLoadTask(Context mContext, BufferedReader mBr) {
        this.mContext = mContext;
        this.mBr = mBr;
        mActivity = (LawDetailActivity) mContext;
    }

    @Override
    protected String doInBackground(Object... objects) {
        StringBuilder paragraph = new StringBuilder();
        try {

            String line = "";

            int index = 0;
            while (index < 50 && (line = mBr.readLine()) != null) {
                paragraph.append(line + "\n");
                index++;

            }

        } catch (IOException e) {
            e.printStackTrace();

        }

        return paragraph.toString();

    }

    @Override


    protected void onPreExecute() {
        super.onPreExecute();

    }

    @Override


    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        mActivity.mTvContent.setText(result);
        new Handler().postDelayed(new Runnable() {

            @Override


            public void run() {
//                mActivity.mScrollView.scrollTo(0, 0); // 记载完新数据后滚动到顶部

            }

        }, 100);
        mActivity.isLoading = false;
    }
}
