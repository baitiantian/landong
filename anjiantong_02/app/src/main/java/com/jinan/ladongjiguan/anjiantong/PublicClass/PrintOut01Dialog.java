package com.jinan.ladongjiguan.anjiantong.PublicClass;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.jinan.ladongjiguan.anjiantong.R;
import com.jinan.ladongjiguan.anjiantong.utils.SharedPreferencesUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by baitiantian on 2017/9/25.
 */

public class PrintOut01Dialog  extends Dialog implements View.OnClickListener{
    private TextView contentTxt;
    private TextView titleTxt;
    private Button submitTxt;
    private TextView cancelTxt;

    private Context mContext;
    private String content;
    private OnCloseListener listener;
    private String positiveName;
    private String negativeName;
    private String title;
    private ListView listView ;
    private List<Map<String, Object>> listItems = new ArrayList<>();

    public PrintOut01Dialog(Context context) {
        super(context);
        this.mContext = context;
    }

    public PrintOut01Dialog(Context context, int themeResId, String content) {
        super(context, themeResId);
        this.mContext = context;
        this.content = content;
    }

    public PrintOut01Dialog(Context context, int themeResId, String content, OnCloseListener listener) {
        super(context, themeResId);
        this.mContext = context;
        this.content = content;
        this.listener = listener;
    }

    protected PrintOut01Dialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.mContext = context;
    }

    public PrintOut01Dialog setTitle(String title){
        this.title = title;
        return this;
    }

    public PrintOut01Dialog setPositiveButton(String name){
        this.positiveName = name;
        return this;
    }

    public PrintOut01Dialog setNegativeButton(String name){
        this.negativeName = name;
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.print_out_01_dialog);
        setCanceledOnTouchOutside(false);
        initView();
    }

    private void initView(){
        contentTxt = (TextView)findViewById(R.id.content);
        titleTxt = (TextView)findViewById(R.id.title);
        submitTxt = (Button)findViewById(R.id.negativeButton);
        listView = (ListView)findViewById(R.id.print_out_01_list);
        submitTxt.setOnClickListener(this);

//        contentTxt.setText(content);
        if(!TextUtils.isEmpty(positiveName)){
            submitTxt.setText(positiveName);
        }

        if(!TextUtils.isEmpty(negativeName)){
            cancelTxt.setText(negativeName);
        }

        if(!TextUtils.isEmpty(title)){
            titleTxt.setText(title);
        }
        String s = SharedPreferencesUtil.getStringData(mContext,"PrintOut01","");
        String[] strings = s.split("\\|");

        for (String string : strings) {
            Map<String, Object> map = new HashMap<>();
            map.put("item", string);
            listItems.add(map);
        }

        // 创建一个SimpleAdapter
        SimpleAdapter simpleAdapter = new SimpleAdapter(mContext, listItems,
                R.layout.print_out_01_dialog_item,
                new String[]{"item"},
                new int[]{R.id.print_out_01_item});
        listView.setAdapter(simpleAdapter);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.negativeButton:
                if(listener != null){
                    listener.onClick(this, true);
                }
                break;
        }
    }

    public interface OnCloseListener{
        void onClick(Dialog dialog, boolean confirm);
    }
}
