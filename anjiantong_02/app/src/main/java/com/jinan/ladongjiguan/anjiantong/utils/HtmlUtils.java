package com.jinan.ladongjiguan.anjiantong.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LevelListDrawable;
import android.os.AsyncTask;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.jinan.ladongjiguan.anjiantong.PublicClass.CustomProgressDialog;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by wangfuchun on 2018/8/2.
 */

public class HtmlUtils {
    private static Context mContext;
    private static TextView mTextView;
    private static String mKeyword;
    private static String TAG = HtmlUtils.class.getSimpleName();

    public HtmlUtils(Context context, TextView textView, String keyWord) {
        this.mContext = context;
        this.mTextView = textView;
        this.mKeyword = keyWord;
    }

    public static class NetworkImageGetter implements Html.ImageGetter {

        @Override
        public Drawable getDrawable(String source) {
            // TODO Auto-generated method stub

            final LevelListDrawable drawable = new LevelListDrawable();
    //法（一）
//            new ImageTools.LoadImage().execute(source, drawable);
    //法（二）没有图片显示
            CharSequence t = mTextView.getText();
            mTextView.setText(t);
    //法（三）
//            Glide.with(mContext).asBitmap().load(source).into(new SimpleTarget<Bitmap>() {
//                @Override
//                public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
//                    if (resource != null) {
//                        BitmapDrawable bitmapDrawable = new BitmapDrawable(resource);
//                        drawable.addLevel(1, 1, bitmapDrawable);
//                        drawable.setBounds(0, 0, resource.getWidth(), resource.getHeight());
//                        drawable.setLevel(1);
//                        Log.e("Html--",mKeyword);
//                        if(mTextView.getText().toString().contains(mKeyword)){
//                            Log.e("Html-",mTextView.getText().toString().substring(0,mTextView.getText().toString().lastIndexOf(mKeyword)));
//                        }
//                        CharSequence t = mTextView.getText();
//                        mTextView.setText(t);
//                    }
//                }
//            });
            return drawable;
        }

    }

    /**
     * 异步加载图片 (耗内存)
     */
    public static final class LoadImage extends AsyncTask<Object, Void, Bitmap> {

        private LevelListDrawable mDrawable;

        @Override
        protected Bitmap doInBackground(Object... params) {
            String source = (String) params[0];
            mDrawable = (LevelListDrawable) params[1];

            try {
                InputStream is = new URL(source).openStream();
                return BitmapFactory.decodeStream(is);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {

            if (bitmap != null) {
                BitmapDrawable d = new BitmapDrawable(bitmap);
                mDrawable.addLevel(1, 1, d);
                mDrawable.setBounds(0, 0, bitmap.getWidth(), bitmap.getHeight());
                // mDrawable.setBounds(0, 0,
                // getWindowManager().getDefaultDisplay().getWidth(),
                // bitmap.getHeight());
                mDrawable.setLevel(1);
                CharSequence t = mTextView.getText();
                mTextView.setText(t);
            }
        }
    }

    /**
     * 单个关键词(适用于原文只包含文本）
     *
     * @param color   关键字颜色
     * @param keyword 关键字
     */
    public static SpannableString getHighLightKeyWord(int color, CharSequence text, String keyword) {
        SpannableString s = new SpannableString(text);
        Pattern p = Pattern.compile(keyword);
        Matcher m = p.matcher(s);
        while (m.find()) {
            int start = m.start();
            int end = m.end();
            s.setSpan(new ForegroundColorSpan(color), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return s;
    }
    public static SpannableString getHighLightKeyWord2(int color,CharSequence text2, String keyword) {

        SpannableString s2 = new SpannableString(text2);
        Pattern p = Pattern.compile(keyword);
        Matcher m2 = p.matcher(s2);
        while (m2.find()){
            int start = m2.start();
            int end = m2.end();
            s2.setSpan(new ForegroundColorSpan(color),start,end,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return s2;
    }
    /**
     * 多个关键词(适用于原文只包含文本）
     *
     * @param color   关键字颜色
     * @param text    文本
     * @param keyword 多个关键字
     */
    public static SpannableString getHighLightKeyWord(int color, String text, String[] keyword) {
        SpannableString s = new SpannableString(text);
        for (int i = 0; i < keyword.length; i++) {
            Pattern p = Pattern.compile(keyword[i]);
            Matcher m = p.matcher(s);
            while (m.find()) {
                int start = m.start();
                int end = m.end();
                s.setSpan(new ForegroundColorSpan(color), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        return s;
    }
}
