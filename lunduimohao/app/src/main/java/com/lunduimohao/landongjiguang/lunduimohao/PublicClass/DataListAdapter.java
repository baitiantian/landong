package com.lunduimohao.landongjiguang.lunduimohao.PublicClass;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;


import com.lunduimohao.landongjiguang.lunduimohao.R;

import java.util.HashMap;
import java.util.List;

/**
 * Created by baitiantian on 2018/1/23.
 */

public class DataListAdapter extends BaseAdapter {
    public static HashMap<Integer, Boolean> isSelected;
    private Context context = null;
    private LayoutInflater inflater = null;
    private List<HashMap<String, Object>> list = null;
    private String keyString[] = null;
    private String itemString = null; // 记录每个item中textview的值
    private int idValue[] = null;// id值

    public DataListAdapter(Context context, List<HashMap<String, Object>> list,
                           int resource, String[] from, int[] to) {
        this.context = context;
        this.list = list;
        keyString = new String[from.length];
        idValue = new int[to.length];
        System.arraycopy(from, 0, keyString, 0, from.length);
        System.arraycopy(to, 0, idValue, 0, to.length);
        inflater = LayoutInflater.from(context);
        init();
    }
    // 初始化 设置所有checkbox都为未选择
    public void init() {
        isSelected = new HashMap<Integer, Boolean>();
        for (int i = 0; i < list.size(); i++) {
            isSelected.put(i, false);
        }
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        ViewHold holder = null;
        if (holder == null) {
            holder = new ViewHold();
            if (view == null) {
                view = inflater.inflate(R.layout.list_data_item, null);
            }
            holder.tv1 = (TextView) view.findViewById(R.id.list_item_01);
            holder.tv2 = (TextView) view.findViewById(R.id.data_1);
            holder.tv3 = (TextView) view.findViewById(R.id.data_2);
            holder.tv4 = (TextView) view.findViewById(R.id.data_3);
            holder.tv5 = (TextView) view.findViewById(R.id.data_4);
            holder.cb = (CheckBox) view.findViewById(R.id.check_box_01);

            view.setTag(holder);
        } else {
            holder = (ViewHold) view.getTag();
        }
        HashMap<String, Object> map = list.get(position);
        if (map != null) {
            itemString = (String) map.get(keyString[0]);
            holder.tv1.setText(itemString);
            itemString = (String) map.get(keyString[1]);
            holder.tv2.setText(itemString);
            itemString = (String) map.get(keyString[2]);
            holder.tv3.setText(itemString);
            itemString = (String) map.get(keyString[3]);
            holder.tv4.setText(itemString);
            itemString = (String) map.get(keyString[4]);
            holder.tv5.setText(itemString);

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
