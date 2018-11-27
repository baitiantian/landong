package com.jinan.ladongjiguan.anjiantong.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.jinan.ladongjiguan.anjiantong.PublicClass.ViewHold;
import com.jinan.ladongjiguan.anjiantong.R;

import java.util.HashMap;
import java.util.List;


public class PeopleListAdapter extends BaseAdapter {
    public static HashMap<Integer, Boolean> isSelected;
    private Context context = null;
    private LayoutInflater inflater = null;
    private List<HashMap<String, Object>> list = null;
    private String keyString[] = null;
    private String itemString = null; // 记录每个item中textview的值
    private int idValue[] = null;// id值
    private int resource;

    public PeopleListAdapter(Context context,  List<HashMap<String, Object>> list,
                              int resource, String[] from, int[] to) {
        this.context = context;
        this.list = list;
        this.resource = resource;
        keyString = new String[from.length];
        idValue = new int[to.length];
        System.arraycopy(from, 0, keyString, 0, from.length);
        System.arraycopy(to, 0, idValue, 0, to.length);
        inflater = LayoutInflater.from(context);
        init();
    }

    // 初始化 设置所有checkbox都为未选择
    public void init() {
        isSelected = new HashMap<>();
        for (int i = 0; i < list.size(); i++) {
            isSelected.put(i, false);
        }
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int arg0) {
        return list.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return 0;
    }

    @Override
    public View getView(final int position, View view, ViewGroup arg2) {
        ViewHold holder = null;
        if (holder == null) {
            holder = new ViewHold();
            if (view == null) {
                view = inflater.inflate(resource, null);
            }
            holder.tv1 = (TextView) view.findViewById(R.id.tx_people_01);

            holder.cb = (CheckBox) view.findViewById(R.id.check_box_02);
            view.setTag(holder);
        } else {
            holder = (ViewHold) view.getTag();
        }
        HashMap<String, Object> map = list.get(position);
        if (map != null) {
            try {
                itemString = (String) map.get(keyString[0]);
                holder.tv1.setText(itemString);
            }catch (Exception e){
                Log.e("破烂报错",e.toString());
            }

        }
        holder.cb.setChecked(isSelected.get(position));
        holder.cb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isSelected.get(position)){

                    isSelected.put(position, false);

                }else{

                    isSelected.put(position, true);

                }

                notifyDataSetChanged();
            }
        });
        return view;
    }
}
